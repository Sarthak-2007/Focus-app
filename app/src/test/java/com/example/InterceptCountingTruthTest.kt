package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #7, charge N2 (DURCH-PRUEF2, verified LIVE on current
 * main, spec at issue #7 comment 6531779678) — the counting
 * truth table for one reach, pinned at the repository surface
 * the charge names.
 *
 * The shield prints the session's phantomReachesBlocked as
 * "N urges stopped" (MonolithShieldService). A counter that
 * moves for a LET-IN prints a lie. The ruled table:
 *
 *  - peek grant ("Used 2m Peek") ......... 0 (W-41/R-09 gate)
 *  - launcher-door exit ("Opened
 *    Launcher") ......................... 0 (this cure — the
 *    charge's residual live path: the C-11 arrival/resolve
 *    split had already cured the peek and the returned
 *    reach, but this verb still booked +1 on both counters)
 *  - returned reach ("Stayed Focused") ... 1 (a true block)
 *  - plain block ("Blocked") ............. 1
 *  - abandoned airlock ("Left Airlock
 *    Without Choosing") ................. 1 — PRESERVED by
 *    ruling (#99 F3: the escape itself moves the counter),
 *    named here so the preservation is a decision on record,
 *    never an accident of the gate's shape
 *  - uninstall attempt with
 *    countAsReach = false ................ 0
 *
 * Every outcome still writes its intercept log row — the row
 * is the evidence the slip and the re-derive read; only the
 * counters are gated.
 *
 * The split verb ("Split Into 10m Micro-Step") has NO writer
 * anywhere in current main (checked this run), so there is
 * nothing to pin: if a writer appears, it is in
 * InterceptOutcome.BLOCKED_OUTCOMES and must count once.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class InterceptCountingTruthTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    /** Runs [block] against a fresh active session on the
     *  shared test database; returns the session id. The
     *  session is deleted afterwards, whatever happens. */
    private fun withActiveSession(block: suspend (MonolithRepository, Int) -> Unit) =
        runBlocking {
            val db = MonolithDatabase.getInstance(context)
            val dao = db.monolithDao()
            val repository = MonolithRepository(context, dao)
            val nowMono = SystemClock.elapsedRealtime()
            dao.deactivateAllSessions()
            val id = dao.insertSession(
                FocusSessionEntity(
                    intention = "N2 COUNTING",
                    profileName = "Manual",
                    targetDurationSeconds = 3600,
                    elapsedSeconds = 0,
                    startWallTimeMillis = System.currentTimeMillis(),
                    startElapsedRealtimeMillis = nowMono,
                    lastTickElapsedRealtimeMillis = nowMono,
                    totalStudyBlocks = 1,
                    currentBlockIndex = 1,
                    isActive = true,
                    completedSuccessfully = false
                )
            ).toInt()
            try {
                block(repository, id)
            } finally {
                dao.deleteSessionById(id)
            }
        }

    private suspend fun phantomCount(sessionId: Int): Int {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        return dao.getSessionById(sessionId)!!.phantomReachesBlocked
    }

    private suspend fun logRowsFor(packageName: String): Int {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        return dao.getInterceptLogsBetween(0L, Long.MAX_VALUE)
            .count { it.packageName == packageName }
    }

    @Test
    fun peekGrantCountsNothing() = withActiveSession { repository, id ->
        repository.recordIntercept(
            packageName = "com.example.n2.peek",
            appName = "Peeked",
            sessionIntention = "N2 COUNTING",
            outcome = "Used 2m Peek"
        )
        assertEquals(0, phantomCount(id))
        assertEquals("the evidence row is still logged", 1, logRowsFor("com.example.n2.peek"))
    }

    @Test
    fun openedLauncherCountsNothing() = withActiveSession { repository, id ->
        repository.recordIntercept(
            packageName = "com.example.n2.launcher",
            appName = "Launchered",
            sessionIntention = "N2 COUNTING",
            outcome = "Opened Launcher"
        )
        assertEquals(0, phantomCount(id))
        assertEquals("the evidence row is still logged", 1, logRowsFor("com.example.n2.launcher"))
    }

    @Test
    fun stayedFocusedCountsExactlyOnce() = withActiveSession { repository, id ->
        repository.recordIntercept(
            packageName = "com.example.n2.returned",
            appName = "Returned",
            sessionIntention = "N2 COUNTING",
            outcome = "Stayed Focused"
        )
        assertEquals(1, phantomCount(id))
    }

    @Test
    fun leftAirlockCountsOnce_preservedBy99F3() = withActiveSession { repository, id ->
        repository.recordIntercept(
            packageName = "com.example.n2.abandoned",
            appName = "Abandoned",
            sessionIntention = "N2 COUNTING",
            outcome = "Left Airlock Without Choosing"
        )
        assertEquals(1, phantomCount(id))
    }

    @Test
    fun plainBlockCountsOnce() = withActiveSession { repository, id ->
        repository.recordIntercept(
            packageName = "com.example.n2.blocked",
            appName = "Blocked",
            sessionIntention = "N2 COUNTING",
            outcome = "Blocked"
        )
        assertEquals(1, phantomCount(id))
    }

    @Test
    fun uninstallAttemptWithCountAsReachFalseCountsNothing() =
        withActiveSession { repository, id ->
            repository.recordIntercept(
                packageName = "com.example.n2.uninstall",
                appName = "Uninstaller",
                sessionIntention = "N2 COUNTING",
                outcome = "Uninstall Attempt — Returned",
                countAsReach = false
            )
            assertEquals(0, phantomCount(id))
        }

    @Test
    fun arrivalResolvedAsPeekCountsNothing() = withActiveSession { repository, id ->
        val rowId = repository.bookInterceptArrival(
            packageName = "com.example.n2.arrivalpeek",
            appName = "ArrivalPeek",
            sessionIntention = "N2 COUNTING",
            arrivedAtWallMillis = System.currentTimeMillis()
        )
        repository.resolveIntercept(rowId, "Used 2m Peek")
        assertEquals(0, phantomCount(id))
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val rows = dao.getInterceptLogsBetween(0L, Long.MAX_VALUE)
            .filter { it.packageName == "com.example.n2.arrivalpeek" }
        assertEquals(1, rows.size)
        assertEquals("Used 2m Peek", rows.single().outcome)
    }

    @Test
    fun mixedSequenceCountsOnlyTrueBlocks() = withActiveSession { repository, id ->
        // One peeked reach (arrival + resolve), one launcher
        // exit, one true return — the counter may move once.
        val rowId = repository.bookInterceptArrival(
            packageName = "com.example.n2.mixed",
            appName = "Mixed",
            sessionIntention = "N2 COUNTING",
            arrivedAtWallMillis = System.currentTimeMillis()
        )
        repository.resolveIntercept(rowId, "Used 2m Peek")
        repository.recordIntercept(
            packageName = "com.example.n2.mixed",
            appName = "Mixed",
            sessionIntention = "N2 COUNTING",
            outcome = "Opened Launcher"
        )
        repository.recordIntercept(
            packageName = "com.example.n2.mixed",
            appName = "Mixed",
            sessionIntention = "N2 COUNTING",
            outcome = "Stayed Focused"
        )
        assertEquals(1, phantomCount(id))
    }
}
