package com.example

import com.example.ui.SupersedingReload
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

/**
 * Issue #77 F2 (VERIFIER-77B concurrency census): the digest mint
 * reload is BOUNDED and SUPERSEDING. Before the cure, every
 * external signal launched an untracked coroutine running a full
 * uncapped library snapshot — unbounded concurrent loads, and
 * racing mints landing stale-over-new. These tests instrument
 * the loader itself: concurrency is counted inside the load, not
 * inferred from timings.
 */
class DigestMintBoundTest {

    /** (a) A volume flap (40 signals) over a large fake library
     *  (500 items) completes with peak concurrent loads at or
     *  under the stated cap, and the standing result is the LAST
     *  signal's — never an earlier signal landing late. */
    @Test
    fun volumeFlap_overLargeLibrary_peakConcurrencyWithinCap() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val inFlight = AtomicInteger(0)
        val peak = AtomicInteger(0)
        val published = Collections.synchronizedList(mutableListOf<Int>())
        val library = List(500) { it }
        val reload = SupersedingReload<Int, List<Int>>(
            scope = scope,
            load = { signal ->
                val now = inFlight.incrementAndGet()
                peak.updateAndGet { p -> maxOf(p, now) }
                try {
                    delay(30)
                    library.map { signal }
                } finally {
                    inFlight.decrementAndGet()
                }
            },
            publish = { rows -> published.add(rows.first()) }
        )

        val jobs = (1..40).map { reload.signal(it) }
        jobs.joinAll()

        assertTrue(
            "peak concurrent loads ${peak.get()} exceeded cap ${SupersedingReload.MAX_IN_FLIGHT}",
            peak.get() <= SupersedingReload.MAX_IN_FLIGHT
        )
        assertEquals(40, published.last())
        scope.cancel()
    }

    /** (b) A second signal cancels the first mid-load: the first
     *  job ends cancelled, its result never lands, and only the
     *  second signal's result is published. */
    @Test
    fun secondSignal_cancelsFirst_noStaleResultsLand() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val firstEntered = CompletableDeferred<Unit>()
        val published = Collections.synchronizedList(mutableListOf<Int>())
        val reload = SupersedingReload<Int, Int>(
            scope = scope,
            load = { signal ->
                if (signal == 1) {
                    firstEntered.complete(Unit)
                    delay(10_000)
                    1
                } else {
                    signal
                }
            },
            publish = { published.add(it) }
        )

        val job1 = reload.signal(1)
        withTimeout(5_000) { firstEntered.await() }
        val job2 = reload.signal(2)
        job2.join()
        job1.join()

        assertTrue("first reload must end cancelled", job1.isCancelled)
        assertEquals(listOf(2), published.toList())
        scope.cancel()
    }

    /** A signal superseded while QUEUED behind the load window
     *  never even reads: with 1 slow + 2 + 3 fired in a flap, the
     *  loader runs for 1 and 3 only, and 3's result stands. */
    @Test
    fun supersededWhileQueued_neverLoads() = runBlocking {
        // One thread: a queued reload cannot start between the
        // test's signal(2) and signal(3) calls, so the supersede
        // is deterministic, not a race the test happens to win.
        val scope = CoroutineScope(
            SupervisorJob() + Dispatchers.Default.limitedParallelism(1)
        )
        val firstEntered = CompletableDeferred<Unit>()
        val loadCalls = Collections.synchronizedList(mutableListOf<Int>())
        val published = Collections.synchronizedList(mutableListOf<Int>())
        val reload = SupersedingReload<Int, Int>(
            scope = scope,
            load = { signal ->
                loadCalls.add(signal)
                if (signal == 1) {
                    firstEntered.complete(Unit)
                    delay(300)
                }
                signal
            },
            publish = { published.add(it) }
        )

        val job1 = reload.signal(1)
        withTimeout(5_000) { firstEntered.await() }
        val job2 = reload.signal(2)
        val job3 = reload.signal(3)
        job1.join()
        job2.join()
        job3.join()

        assertEquals(listOf(1, 3), loadCalls.toList())
        assertEquals(3, published.last())
        scope.cancel()
    }
}
