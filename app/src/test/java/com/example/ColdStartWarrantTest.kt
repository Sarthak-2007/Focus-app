package com.example

import android.os.SystemClock
import com.example.service.MonolithAccessibilityService
import com.example.service.UsageAccess
import com.example.service.Warrant
import com.example.service.WarrantState
import com.example.service.WatchStore
import com.example.ui.components.RefusalAction
import com.example.ui.components.refusalSentenceFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.util.concurrent.TimeUnit

/**
 * Cold-start cure (issue #7) — BUILD-3's deterministic proofs for the
 * cured warrant table. The bug: on a cold open the Deck showed before
 * the accessibility service bound, a quiet Deck generated no a11y
 * events, the residency stamp never landed, and the old sentence
 * table printed its fog sentence ("CAN'T CHECK BLOCKING — TAP TO
 * RETRY", action RETRY_CHECK) AT REST, forever — a retry the hand
 * could never satisfy. The cure (BUILD-1: the service self-probes at
 * residency start, +1500 ms, and on rebind; the warrant carries its
 * residency start; BUILD-2: the cured sentence table, fog deleted)
 * makes the transient SILENT for one grace window and truthful after
 * it. Everything below is pure derivation: hand-built warrants read
 * through the TIMED [refusalSentenceFor] overload, each read carrying
 * a FRESH readMono (readMono == the read instant) so the stale-print
 * guard (UNA2-03) can never mask the grace table being proved.
 *
 * What is NOT unit-provable here, said plainly (never faked):
 *  - MonolithAccessibilityService.probePipelineNow() itself needs a
 *    live accessibility service holding a real window root
 *    (rootInActiveWindow) — device/Android-runtime only. The
 *    Robolectric class below proves the store-side contract the
 *    probe WRITES to (lastRootMono at/after the residency start ⇒
 *    stampedThisResidency ⇒ deriveWarrant LIVE), which is every
 *    consequence the sentence table consumes.
 *  - The +1500 ms re-probe's timing is asynchronous device
 *    behaviour; its guard (same residency, still unstamped) is the
 *    stampedThisResidency semantics asserted below.
 *  - The no-clock refusalSentenceFor overload delegates to the timed
 *    one with the process clock (SystemClock) — off-device that
 *    clock is a stub, so every sentence assertion here goes through
 *    the timed overload, which is the same cured table.
 */
class ColdStartWarrantTest {

    @Before
    fun reset() {
        WatchStore.resetForTest()
    }

    private fun warrant(
        state: WarrantState,
        bound: Boolean = true,
        stamped: Boolean = true,
        usage: UsageAccess = UsageAccess.ALLOWED,
        readMono: Long,
        residencyStartMono: Long = 0L
    ) = Warrant(
        state = state,
        a11yBound = bound,
        a11yStampedThisResidency = stamped,
        shieldProtecting = false,
        usage = usage,
        readMono = readMono,
        residencyStartMono = residencyStartMono
    )

    /** A residency that started at T; reads happen at T + delta. */
    private val t = 1_000_000L

    @Test
    fun graceConstantIsFourSeconds() {
        // The window the self-probe gets before silence hardens.
        assertEquals(4_000L, WatchStore.RESIDENCY_GRACE_MS)
    }

    @Test
    fun stampedBoundWarrantReadsLiveAndStaysSilentAtEveryResidencyAge() {
        // Requirement 1 — the cold-start cure's happy path: the
        // probe (or an event) stamped this residency, the warrant
        // reads LIVE, and no sentence exists at ANY residency age —
        // inside the grace, at its edge, and long past it. A LIVE
        // warrant never borrows the grace table's silence; it is
        // silent because there is nothing to fix.
        val derived = WatchStore.deriveWarrant(
            a11yBound = true, stamped = true,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = t
        )
        assertEquals(WarrantState.LIVE, derived.state)
        for (delta in listOf(0L, 1L, 2_000L, 3_999L, 4_000L, 4_001L, 30_000L, 44_999L)) {
            val now = t + delta
            val w = warrant(
                WarrantState.LIVE, stamped = true,
                readMono = now, residencyStartMono = t
            )
            assertNull(
                "LIVE at residency age $delta must stay silent",
                refusalSentenceFor(w, now)
            )
        }
        assertNull(refusalSentenceFor(null, t))
    }

    @Test
    fun graceWindowIsSilentThenBlockingOffAfterGrace() {
        // Requirement 2 — UNPROVEN + unstamped + usage ALLOWED +
        // residency carried at T: the slot's reserved emptiness
        // while the self-probe lands its proof, then the truth —
        // bound but blind is OFF in fact, and its FIX door rebinds
        // the service, the one act that can change the fact.
        for (delta in listOf(0L, 2_000L, 3_999L)) {
            val now = t + delta
            val w = warrant(
                WarrantState.UNPROVEN, stamped = false,
                readMono = now, residencyStartMono = t
            )
            assertNull(
                "in-grace read at T+$delta must be silent",
                refusalSentenceFor(w, now)
            )
        }
        // The boundary itself: the landed table holds the grace
        // while age <= RESIDENCY_GRACE_MS, so T+4000 is still silent.
        val atEdge = t + WatchStore.RESIDENCY_GRACE_MS
        assertNull(
            refusalSentenceFor(
                warrant(
                    WarrantState.UNPROVEN, stamped = false,
                    readMono = atEdge, residencyStartMono = t
                ),
                atEdge
            )
        )
        for (delta in listOf(4_001L, 10_000L, 44_999L)) {
            val now = t + delta
            val s = refusalSentenceFor(
                warrant(
                    WarrantState.UNPROVEN, stamped = false,
                    readMono = now, residencyStartMono = t
                ),
                now
            )
            assertEquals("past-grace read at T+$delta", R.string.sentence_blocking_off, s?.textRes)
            assertEquals(RefusalAction.FIX_ACCESSIBILITY, s?.action)
        }
    }

    @Test
    fun noResidencyRecordReadsBlockingOffImmediately() {
        // Requirement 3 — residencyStartMono 0 is age-unknown, never
        // fresh: no grace silence is owed, at any read instant.
        for (now in listOf(1L, 500_000L, 10_000_000L)) {
            val s = refusalSentenceFor(
                warrant(
                    WarrantState.UNPROVEN, stamped = false,
                    readMono = now, residencyStartMono = 0L
                ),
                now
            )
            assertEquals(R.string.sentence_blocking_off, s?.textRes)
            assertEquals(RefusalAction.FIX_ACCESSIBILITY, s?.action)
        }
        // A hand-built warrant carrying no read instant either
        // (readMono 0: age-unknown, never stale) reads the same.
        val handBuilt = refusalSentenceFor(
            warrant(
                WarrantState.UNPROVEN, stamped = false,
                readMono = 0L, residencyStartMono = 0L
            ),
            500_000L
        )
        assertEquals(R.string.sentence_blocking_off, handBuilt?.textRes)
        assertEquals(RefusalAction.FIX_ACCESSIBILITY, handBuilt?.action)
    }

    @Test
    fun usageDeniedOutranksTheStampRaceAtEveryAge() {
        // Requirement 4 — the order cure: a known lack with a known
        // owner is never masked by the transient stamp race. The old
        // table fired its fog row BEFORE the usage rows for an
        // unstamped DENIED warrant; the cured table claims DENIED
        // first, in-grace, past grace, and with no residency at all.
        for (residency in listOf(t, 0L)) {
            for (delta in listOf(0L, 2_000L, 3_999L, 4_001L, 44_999L)) {
                val now = (if (residency > 0L) residency else 500_000L) + delta
                val s = refusalSentenceFor(
                    warrant(
                        WarrantState.UNPROVEN, stamped = false,
                        usage = UsageAccess.DENIED,
                        readMono = now, residencyStartMono = residency
                    ),
                    now
                )
                assertEquals(
                    "DENIED (residency=$residency, delta=$delta)",
                    R.string.sentence_usage_off, s?.textRes
                )
                assertEquals(RefusalAction.FIX_USAGE, s?.action)
            }
        }
        // A stamped DENIED warrant (state OFF by derivation) names
        // the same pane — the usage row stands above the OFF row.
        val stampedDenied = refusalSentenceFor(
            warrant(
                WarrantState.OFF, stamped = true,
                usage = UsageAccess.DENIED,
                readMono = t, residencyStartMono = t
            ),
            t
        )
        assertEquals(R.string.sentence_usage_off, stampedDenied?.textRes)
        assertEquals(RefusalAction.FIX_USAGE, stampedDenied?.action)
    }

    @Test
    fun usageUnsetStaysCuredStampedOrNot() {
        // Requirement 5 — UNSET (issue #6 C3/F9) is a known lack,
        // never the fog and never the grace: stamped or unstamped,
        // carried residency or none, the sentence names the Usage
        // pane at every age.
        for (stamped in listOf(true, false)) {
            for (residency in listOf(t, 0L)) {
                for (delta in listOf(0L, 2_000L, 3_999L, 4_001L, 44_999L)) {
                    val now = (if (residency > 0L) residency else 500_000L) + delta
                    val s = refusalSentenceFor(
                        warrant(
                            WarrantState.UNPROVEN, stamped = stamped,
                            usage = UsageAccess.UNSET,
                            readMono = now, residencyStartMono = residency
                        ),
                        now
                    )
                    assertEquals(
                        "UNSET (stamped=$stamped, residency=$residency, delta=$delta)",
                        R.string.sentence_usage_off, s?.textRes
                    )
                    assertEquals(RefusalAction.FIX_USAGE, s?.action)
                }
            }
        }
    }

    @Test
    fun unboundIsSilentInsideTheColdOpenGraceThenReadsBlockingOff() {
        // Requirement 6 (as re-ruled — issue #7 cold-open grace,
        // chair ruling): an unbound warrant whose carried residency
        // is still inside RESIDENCY_GRACE_MS is the cold open itself
        // — the Deck is up, the system has not finished binding
        // the service, and bind latency has no platform bound, so
        // the slot holds its reserved silence instead of printing a
        // death that may be one frame old. Past the grace the print
        // stands at every age, and a warrant carrying no residency
        // (age unknown, never fresh) reads BLOCKING IS OFF at once.
        // The a11y row keeps its priority throughout: even a DENIED
        // or UNSET usage is silent in-grace (the a11y row fires
        // first), and past the grace the a11y sentence — not the
        // usage sentence — is what stands.
        for (state in listOf(WarrantState.OFF, WarrantState.UNPROVEN)) {
            for (usage in listOf(UsageAccess.ALLOWED, UsageAccess.DENIED, UsageAccess.UNSET)) {
                for (delta in listOf(0L, 2_000L, 3_999L, 4_000L)) {
                    val now = t + delta
                    assertNull(
                        "unbound in-grace (state=$state, usage=$usage, delta=$delta)",
                        refusalSentenceFor(
                            warrant(
                                state, bound = false, stamped = false,
                                usage = usage,
                                readMono = now, residencyStartMono = t
                            ),
                            now
                        )
                    )
                }
                for (delta in listOf(4_001L, 10_000L, 44_999L)) {
                    val now = t + delta
                    val s = refusalSentenceFor(
                        warrant(
                            state, bound = false, stamped = false,
                            usage = usage,
                            readMono = now, residencyStartMono = t
                        ),
                        now
                    )
                    assertEquals(
                        "unbound past-grace (state=$state, usage=$usage, delta=$delta)",
                        R.string.sentence_blocking_off, s?.textRes
                    )
                    assertEquals(RefusalAction.FIX_ACCESSIBILITY, s?.action)
                }
                // No residency carried: no grace is owed at any age.
                for (delta in listOf(0L, 2_000L, 44_999L)) {
                    val now = 500_000L + delta
                    val s = refusalSentenceFor(
                        warrant(
                            state, bound = false, stamped = false,
                            usage = usage,
                            readMono = now, residencyStartMono = 0L
                        ),
                        now
                    )
                    assertEquals(
                        "unbound no-residency (state=$state, usage=$usage, delta=$delta)",
                        R.string.sentence_blocking_off, s?.textRes
                    )
                    assertEquals(RefusalAction.FIX_ACCESSIBILITY, s?.action)
                }
            }
        }
    }

    @Test
    fun fogSentenceIsUnreachableAcrossTheCuredTable() {
        // The fog is deleted, not reworded: across the whole input
        // space of the table, every sentence that can print is one
        // of the two standing sentences, and RETRY_CHECK is issued
        // by no row. (Whitelist assertion — the fog's own words are
        // deliberately not re-typed here; they survive nowhere.)
        val standing = setOf(
            R.string.sentence_blocking_off,
            R.string.sentence_usage_off
        )
        var printed = 0
        for (state in WarrantState.values()) {
            for (bound in listOf(true, false)) {
                for (stamped in listOf(true, false)) {
                    for (usage in UsageAccess.values()) {
                        for (residency in listOf(0L, t)) {
                            for (delta in listOf(0L, 3_999L, 4_001L)) {
                                val now = (if (residency > 0L) residency else 500_000L) + delta
                                val s = refusalSentenceFor(
                                    warrant(
                                        state, bound = bound, stamped = stamped,
                                        usage = usage,
                                        readMono = now, residencyStartMono = residency
                                    ),
                                    now
                                ) ?: continue
                                printed++
                                assertTrue(
                                    "unlisted sentence printed: ${s.textRes}",
                                    s.textRes in standing
                                )
                                assertFalse(
                                    "RETRY_CHECK issued for state=$state bound=$bound " +
                                        "stamped=$stamped usage=$usage residency=$residency delta=$delta",
                                    s.action == RefusalAction.RETRY_CHECK
                                )
                            }
                        }
                    }
                }
            }
        }
        assertTrue("the sweep must actually print sentences", printed > 0)
    }
}

/**
 * The probe contract's store side (requirement 7), under
 * Robolectric so the monotonic clock is real-but-shadowed and
 * deterministic: [WatchStore.noteResidencyStart] stamps the
 * residency, [WatchStore.deriveWarrant] carries that stamp onto
 * every warrant it builds, and the stamp test the probe feeds —
 * MonolithAccessibilityService.lastRootMono at or after the
 * residency start — is exactly what turns a derivation LIVE.
 * Writing lastRootMono directly here is not a fake: it is the one
 * write probePipelineNow (and the event path) performs on success,
 * and the semantics under proof are the store's, not the service's.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ColdStartResidencyStampTest {

    @Before
    fun reset() {
        WatchStore.resetForTest()
        MonolithAccessibilityService.lastRootMono = 0L
    }

    @Test
    fun residencyStartIsStampedOntoEveryDerivedWarrant() {
        // No residency in force: the warrant carries 0 — age-unknown,
        // never a fresh residency (the header's contract).
        assertFalse(WatchStore.residencyActive())
        val before = WatchStore.deriveWarrant(
            a11yBound = true, stamped = false,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = 111L
        )
        assertEquals(0L, before.residencyStartMono)

        WatchStore.noteResidencyStart()
        assertTrue(WatchStore.residencyActive())
        val carried = WatchStore.deriveWarrant(
            a11yBound = true, stamped = false,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = 222L
        )
        assertTrue(
            "derived warrant must carry the live residency start",
            carried.residencyStartMono > 0L
        )
        // The stamp is the residency's own instant: bracketed by the
        // process clock readings around the residency's life.
        assertTrue(carried.residencyStartMono <= SystemClock.elapsedRealtime())
        // Every later derivation under the same residency carries
        // the SAME start — the age the sentence table computes is
        // the residency's, not the read's.
        val again = WatchStore.deriveWarrant(
            a11yBound = true, stamped = false,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = 333L
        )
        assertEquals(carried.residencyStartMono, again.residencyStartMono)
    }

    @Test
    fun stampAtOrAfterResidencyStartIsWhatProvesThisResidency() {
        WatchStore.noteResidencyStart()
        val start = WatchStore.deriveWarrant(
            a11yBound = true, stamped = false,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = 1L
        ).residencyStartMono

        // Nothing stamped yet: unproven, exactly the cold-open state.
        assertFalse(WatchStore.stampedThisResidency())
        // A stamp from BEFORE this residency (the previous visit's
        // proof) does not prove this one.
        MonolithAccessibilityService.lastRootMono = start - 1L
        assertFalse(WatchStore.stampedThisResidency())
        // The probe's success write — a stamp AT the residency
        // start — proves it, and the derivation turns LIVE.
        MonolithAccessibilityService.lastRootMono = start
        assertTrue(WatchStore.stampedThisResidency())
        val live = WatchStore.deriveWarrant(
            a11yBound = true, stamped = WatchStore.stampedThisResidency(),
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = start
        )
        assertEquals(WarrantState.LIVE, live.state)
        assertTrue(live.a11yStampedThisResidency)
        assertEquals(start, live.residencyStartMono)
    }

    @Test
    fun aNewResidencySupersedesTheOldStamp() {
        WatchStore.noteResidencyStart()
        val firstStart = WatchStore.deriveWarrant(
            a11yBound = true, stamped = false,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = 1L
        ).residencyStartMono
        MonolithAccessibilityService.lastRootMono = firstStart
        assertTrue(WatchStore.stampedThisResidency())

        // The Deck is re-entered: a new residency starts, and the
        // old residency's stamp no longer proves anything — the new
        // residency must be proved by its own probe.
        ShadowSystemClock.advanceBy(50L, TimeUnit.MILLISECONDS)
        WatchStore.noteResidencyStart()
        val secondStart = WatchStore.deriveWarrant(
            a11yBound = true, stamped = false,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = 2L
        ).residencyStartMono
        assertTrue(secondStart > firstStart)
        assertFalse(WatchStore.stampedThisResidency())

        // A reset (process death's test double) carries no
        // residency at all.
        WatchStore.resetForTest()
        assertFalse(WatchStore.residencyActive())
        assertFalse(WatchStore.stampedThisResidency())
        assertEquals(
            0L,
            WatchStore.deriveWarrant(
                a11yBound = true, stamped = true,
                usage = UsageAccess.ALLOWED, shieldProtecting = false,
                readMono = 3L
            ).residencyStartMono
        )
    }
}
