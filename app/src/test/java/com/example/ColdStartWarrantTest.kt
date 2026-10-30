package com.example

import android.os.SystemClock
import com.example.service.MonolithAccessibilityService
import com.example.service.UsageAccess
import com.example.service.Warrant
import com.example.service.WarrantState
import com.example.service.WatchStore
import com.example.ui.components.RefusalAction
import com.example.ui.components.refusalSentenceFor
import com.example.ui.components.seamMarksFor
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
 * RETRY", carrying a retry action) AT REST, forever — a retry the
 * hand could never satisfy. The cure (BUILD-1: the service
 * self-probes at residency start, +1500 ms, and on rebind; the
 * warrant carries its residency start; BUILD-2: the cured sentence
 * table, fog deleted; FIX-1: stamp validity is freshness, and an
 * enabled-but-unbound service derives UNPROVEN inside the grace;
 * FIX-2: the table's row order restructured around that unbound
 * grace, the seam's proof mark dies past grace, and the retry
 * action is deleted from the enum outright) makes the transient
 * SILENT for one grace window and truthful after it. Everything
 * below is pure derivation: hand-built warrants read
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
        residencyStartMono: Long = 0L,
        graceAnchorMono: Long = 0L
    ) = Warrant(
        state = state,
        a11yBound = bound,
        a11yStampedThisResidency = stamped,
        shieldProtecting = false,
        usage = usage,
        readMono = readMono,
        residencyStartMono = residencyStartMono,
        graceAnchorMono = graceAnchorMono
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
            assertEquals("past-grace read at T+$delta", "BLOCKING IS OFF — TAP TO FIX", s?.text)
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
            assertEquals("BLOCKING IS OFF — TAP TO FIX", s?.text)
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
        assertEquals("BLOCKING IS OFF — TAP TO FIX", handBuilt?.text)
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
                    "USAGE ACCESS IS OFF — TAP TO FIX", s?.text
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
        assertEquals("USAGE ACCESS IS OFF — TAP TO FIX", stampedDenied?.text)
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
                        "USAGE ACCESS IS OFF — TAP TO FIX", s?.text
                    )
                    assertEquals(RefusalAction.FIX_USAGE, s?.action)
                }
            }
        }
    }

    @Test
    fun unboundWithGrantEnabledIsSilentInGraceAndOffPastIt() {
        // Requirement 6a (FIX-2, answering CRIT-1 C-5 / DAY-1 D-1) —
        // SUPERSEDES the old requirement 6, which pinned "unbound
        // reads BLOCKING IS OFF at every age, the a11y row outranks
        // every other row". That pin predates FIX-1's derivation:
        // an enabled-but-unbound service inside the residency grace
        // now derives UNPROVEN (a bind still landing), and the
        // restructured table's grace row is bound-blind — this
        // warrant is silent in grace even though a11yBound is
        // false. Past grace the bind that never came is a fact.
        for (delta in listOf(0L, 2_000L, 3_999L, 4_000L)) {
            val now = t + delta
            assertNull(
                "unbound UNPROVEN in grace at T+$delta must be silent",
                refusalSentenceFor(
                    warrant(
                        WarrantState.UNPROVEN, bound = false, stamped = false,
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
                    WarrantState.UNPROVEN, bound = false, stamped = false,
                    readMono = now, residencyStartMono = t
                ),
                now
            )
            assertEquals("unbound UNPROVEN past grace at T+$delta", "BLOCKING IS OFF — TAP TO FIX", s?.text)
            assertEquals(RefusalAction.FIX_ACCESSIBILITY, s?.action)
        }
    }

    @Test
    fun unboundWithoutGrantReadsBlockingOffAtAgeZero() {
        // Requirement 6b — grant-absent unbound derives OFF (FIX-1):
        // nothing is enabled, so nothing is binding, and no grace
        // is owed at any age — the OFF lands at residency age 0.
        for (delta in listOf(0L, 2_000L, 3_999L, 4_001L, 44_999L)) {
            val now = t + delta
            val s = refusalSentenceFor(
                warrant(
                    WarrantState.OFF, bound = false, stamped = false,
                    readMono = now, residencyStartMono = t
                ),
                now
            )
            assertEquals("grant-absent unbound at T+$delta", "BLOCKING IS OFF — TAP TO FIX", s?.text)
            assertEquals(RefusalAction.FIX_ACCESSIBILITY, s?.action)
        }
        // And with no residency carried at all.
        val s = refusalSentenceFor(
            warrant(
                WarrantState.OFF, bound = false, stamped = false,
                readMono = 500_000L, residencyStartMono = 0L
            ),
            500_000L
        )
        assertEquals("BLOCKING IS OFF — TAP TO FIX", s?.text)
        assertEquals(RefusalAction.FIX_ACCESSIBILITY, s?.action)
    }

    @Test
    fun usageRowsClaimBeforeTheUnboundRow() {
        // Requirement 6c — the FIX-2 row order's deliberate
        // consequence, pinned so it is a decision, not an accident:
        // with a usage lack on the warrant, the USAGE sentence
        // prints even when the service is also unbound, at every
        // age and in either state — a known lack with a known
        // owner is never masked by another leg's death. (The old
        // requirement 6 pinned the opposite for exactly these
        // inputs; ruling 3's row order supersedes it.)
        for (state in listOf(WarrantState.OFF, WarrantState.UNPROVEN)) {
            for (usage in listOf(UsageAccess.DENIED, UsageAccess.UNSET)) {
                for (delta in listOf(0L, 2_000L, 4_001L, 44_999L)) {
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
                        "unbound with usage lack (state=$state, usage=$usage, delta=$delta)",
                        "USAGE ACCESS IS OFF — TAP TO FIX", s?.text
                    )
                    assertEquals(RefusalAction.FIX_USAGE, s?.action)
                }
            }
        }
    }

    @Test
    fun fogSentenceIsUnreachableAcrossTheCuredTable() {
        // The fog is deleted, not reworded: across the whole input
        // space of the table, every sentence that can print is one
        // of the two standing sentences, and the only actions any
        // row can issue are the two FIX doors. The fog's retry
        // action is not merely unissued — FIX-2 deleted the enum
        // value outright, so this whitelist is now backed by the
        // compiler as well. (Whitelist assertion — the fog's own
        // words are deliberately not re-typed here; they survive
        // nowhere.)
        val standing = setOf(
            "BLOCKING IS OFF — TAP TO FIX",
            "USAGE ACCESS IS OFF — TAP TO FIX"
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
                                    "unlisted sentence printed: ${s.text}",
                                    s.text in standing
                                )
                                assertTrue(
                                    "unlisted action issued for state=$state bound=$bound " +
                                        "stamped=$stamped usage=$usage residency=$residency delta=$delta",
                                    s.action == RefusalAction.FIX_ACCESSIBILITY ||
                                        s.action == RefusalAction.FIX_USAGE
                                )
                            }
                        }
                    }
                }
            }
        }
        assertTrue("the sweep must actually print sentences", printed > 0)
    }

    @Test
    fun seamProofDiesToNonePastGraceAndStaysBindOnlyInsideIt() {
        // FIX-2 item 2 (CRIT-1 C-3: one verdict per screen) —
        // UNPROVEN + bound + unstamped, read fresh at every age.
        // Inside the grace the seam and the sentence agree: the
        // sentence is silent and the seam still certifies the
        // bind ("PROOF: BIND ONLY"). Past the grace the sentence
        // hardens to BLOCKING IS OFF, and the seam's right mark
        // dies to "PROOF: NONE" in the same frame — the left
        // keeps the bound fact, so the screen speaks one verdict.
        for (delta in listOf(0L, 2_000L, 3_999L, 4_000L)) {
            val now = t + delta
            val w = warrant(
                WarrantState.UNPROVEN, stamped = false,
                readMono = now, residencyStartMono = t
            )
            assertEquals(
                "seam in grace at T+$delta",
                "BIND HELD · UNSEEN" to "PROOF: BIND ONLY",
                seamMarksFor(w, now)
            )
            assertNull("sentence in grace at T+$delta", refusalSentenceFor(w, now))
        }
        for (delta in listOf(4_001L, 10_000L, 44_999L)) {
            val now = t + delta
            val w = warrant(
                WarrantState.UNPROVEN, stamped = false,
                readMono = now, residencyStartMono = t
            )
            assertEquals(
                "seam past grace at T+$delta",
                "BIND HELD · UNSEEN" to "PROOF: NONE",
                seamMarksFor(w, now)
            )
            assertEquals(
                "sentence past grace at T+$delta",
                "BLOCKING IS OFF — TAP TO FIX",
                refusalSentenceFor(w, now)?.text
            )
        }
        // No residency carried: age unknown, never in grace — the
        // proof is NONE from the first read.
        val noResidency = warrant(
            WarrantState.UNPROVEN, stamped = false,
            readMono = t, residencyStartMono = 0L
        )
        assertEquals(
            "BIND HELD · UNSEEN" to "PROOF: NONE",
            seamMarksFor(noResidency, t)
        )
    }

    @Test
    fun graceAnchorOlderThanResidencyReadsPastGraceAtResidencyStart() {
        // Addendum 2 (bind-epoch law, DAY-1 D-2): every grace age
        // in the prints is measured from the warrant's grace
        // ANCHOR, not its residency start. This warrant's anchor
        // sits 5 s before its residency start — a proof debt
        // inherited from the previous residency of the same bind
        // epoch. At residency start + 0 the grace age is already
        // 5_000 > RESIDENCY_GRACE_MS, so no fresh silence is
        // minted by the re-entry: the sentence is already OFF
        // and the seam's proof is already NONE. (Hand-built
        // warrants carrying no anchor fall back to the residency
        // start — every other test in this class is that proof,
        // unchanged from BUILD-3's meaning.)
        val now = t // residency start + 0
        val w = warrant(
            WarrantState.UNPROVEN, stamped = false,
            readMono = now, residencyStartMono = t,
            graceAnchorMono = t - 5_000L
        )
        val s = refusalSentenceFor(w, now)
        assertEquals("BLOCKING IS OFF — TAP TO FIX", s?.text)
        assertEquals(RefusalAction.FIX_ACCESSIBILITY, s?.action)
        assertEquals(
            "BIND HELD · UNSEEN" to "PROOF: NONE",
            seamMarksFor(w, now)
        )
        // Control, same warrant minus the anchor: the residency
        // start alone is age 0 — deep inside the grace, silent,
        // BIND ONLY. The anchor, and only the anchor, hardened it.
        val noAnchor = warrant(
            WarrantState.UNPROVEN, stamped = false,
            readMono = now, residencyStartMono = t
        )
        assertNull(refusalSentenceFor(noAnchor, now))
        assertEquals(
            "BIND HELD · UNSEEN" to "PROOF: BIND ONLY",
            seamMarksFor(noAnchor, now)
        )
    }

    @Test
    fun seamStalePrecedenceBeatsThePastGraceNone() {
        // The stale-read STALE mark keeps its precedence over the
        // past-grace NONE (FIX-2 item 2): a dead read admits its
        // age before any grace arithmetic runs, and the sentence
        // stays silent on the same read.
        val w = warrant(
            WarrantState.UNPROVEN, stamped = false,
            readMono = t, residencyStartMono = t
        )
        val now = t + WatchStore.STALE_AFTER_MS + 1L
        assertEquals(
            "BIND HELD · UNSEEN" to "PROOF: STALE",
            seamMarksFor(w, now)
        )
        assertNull(refusalSentenceFor(w, now))
    }

    @Test
    fun curedTableRowsPinInRulingOrder() {
        // FIX-2 item 3/6d — the restructured table, row by row, in
        // the order the rows claim. One representative warrant per
        // row; the sweeps above prove the spaces between them.
        fun read(
            state: WarrantState,
            bound: Boolean = true,
            stamped: Boolean = true,
            usage: UsageAccess = UsageAccess.ALLOWED,
            now: Long,
            residencyStartMono: Long = t
        ) = refusalSentenceFor(
            warrant(
                state, bound = bound, stamped = stamped, usage = usage,
                readMono = now, residencyStartMono = residencyStartMono
            ),
            now
        )
        // Row 0 — stale read: silence, whatever the rows below say.
        assertNull(
            refusalSentenceFor(
                warrant(
                    WarrantState.OFF, bound = false, stamped = false,
                    readMono = t, residencyStartMono = t
                ),
                t + WatchStore.STALE_AFTER_MS + 1L
            )
        )
        // Row 1 — usage DENIED: the USAGE sentence and its door.
        assertEquals(
            "USAGE ACCESS IS OFF — TAP TO FIX" to RefusalAction.FIX_USAGE,
            read(WarrantState.OFF, usage = UsageAccess.DENIED, now = t).let { it?.text to it?.action }
        )
        // Row 2 — usage UNSET: same sentence, same door.
        assertEquals(
            "USAGE ACCESS IS OFF — TAP TO FIX" to RefusalAction.FIX_USAGE,
            read(WarrantState.UNPROVEN, usage = UsageAccess.UNSET, now = t).let { it?.text to it?.action }
        )
        // Row 3 — UNPROVEN inside grace, bound: silence.
        assertNull(read(WarrantState.UNPROVEN, stamped = false, now = t + 2_000L))
        // Row 3 again — UNPROVEN inside grace, UNBOUND: silence.
        assertNull(read(WarrantState.UNPROVEN, bound = false, stamped = false, now = t + 2_000L))
        // Row 4 — not bound (grant-absent OFF) at residency age 0.
        assertEquals(
            "BLOCKING IS OFF — TAP TO FIX" to RefusalAction.FIX_ACCESSIBILITY,
            read(WarrantState.OFF, bound = false, stamped = false, now = t).let { it?.text to it?.action }
        )
        // Row 5 — UNPROVEN past grace, bound: BLOCKING IS OFF.
        assertEquals(
            "BLOCKING IS OFF — TAP TO FIX" to RefusalAction.FIX_ACCESSIBILITY,
            read(WarrantState.UNPROVEN, stamped = false, now = t + 4_001L).let { it?.text to it?.action }
        )
        // Row 5 via row 4 — UNPROVEN past grace, unbound: same.
        assertEquals(
            "BLOCKING IS OFF — TAP TO FIX" to RefusalAction.FIX_ACCESSIBILITY,
            read(WarrantState.UNPROVEN, bound = false, stamped = false, now = t + 4_001L).let { it?.text to it?.action }
        )
        // Row 6 — OFF, bound and stamped, usage ALLOWED: OFF.
        assertEquals(
            "BLOCKING IS OFF — TAP TO FIX" to RefusalAction.FIX_ACCESSIBILITY,
            read(WarrantState.OFF, now = t).let { it?.text to it?.action }
        )
        // Row 7 — LIVE: silence.
        assertNull(read(WarrantState.LIVE, now = t))
    }
}

/**
 * The probe contract's store side (requirement 7), under
 * Robolectric so the monotonic clock is real-but-shadowed and
 * deterministic: [WatchStore.noteResidencyStart] stamps the
 * residency, [WatchStore.deriveWarrant] carries that stamp onto
 * every warrant it builds, and the stamp test the probe feeds —
 * WatchStore.pipelineProvenFresh over
 * MonolithAccessibilityService.lastRootMono — is exactly what
 * turns a derivation LIVE. Writing lastRootMono directly here is
 * not a fake: it is the one write probePipelineNow (and the event
 * path) performs on success, and the semantics under proof are
 * the store's, not the service's.
 *
 * RE-PINNED by FIX-2 to FIX-1's freshness doctrine: BUILD-3
 * pinned the residency COMPARISON (a stamp proved only the
 * residency it was minted in). FIX-1 deleted that comparison —
 * validity is the stamp's AGE (≤ [WatchStore.STAMP_FRESH_MS]),
 * never its residency — because the comparison trusted a mint
 * for a residency's whole life (CRIT-1 C-2's zombie LIVE) and
 * re-opened the fog race on every Deck re-entry (DAY-1 D-2).
 * The two stamp tests below assert the freshness semantics
 * where they conflict with the old pins, and say so in place.
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
    fun stampFreshnessBoundaryIsFortyFiveSeconds() {
        // FIX-2 item 6a — the freshness boundary, through the
        // store API, deterministic under the shadow clock: fresh
        // at the boundary (age ≤ STAMP_FRESH_MS), no proof 1 ms
        // past it. One staleness law for the whole store: the
        // stamp window IS the print-staleness window.
        assertEquals(45_000L, WatchStore.STAMP_FRESH_MS)
        assertEquals(WatchStore.STALE_AFTER_MS, WatchStore.STAMP_FRESH_MS)
        // No stamp at all: never proven.
        MonolithAccessibilityService.lastRootMono = 0L
        assertFalse(WatchStore.pipelineProvenFresh())
        // A stamp minted now is fresh… (The clock is advanced
        // first so the stamp is a positive instant whatever the
        // shadow clock's initial value is — 0 is the no-stamp
        // sentinel, never a mint.)
        ShadowSystemClock.advanceBy(100L, TimeUnit.MILLISECONDS)
        MonolithAccessibilityService.lastRootMono = SystemClock.elapsedRealtime()
        assertTrue(WatchStore.pipelineProvenFresh())
        // …still fresh at exactly the boundary…
        ShadowSystemClock.advanceBy(WatchStore.STAMP_FRESH_MS, TimeUnit.MILLISECONDS)
        assertTrue(WatchStore.pipelineProvenFresh())
        // …and no proof 1 ms past it. What keeps a real device
        // inside the boundary is FIX-1's renewal loop (re-probe
        // every 15 s while bound) — device behaviour, exercised
        // on the phone pass, not asserted here; what is pinned
        // here is the boundary arithmetic itself.
        ShadowSystemClock.advanceBy(1L, TimeUnit.MILLISECONDS)
        assertFalse(WatchStore.pipelineProvenFresh())
    }

    @Test
    fun freshStampProvesThePipelineWhateverResidencyMintedIt() {
        // Nothing stamped yet: unproven, exactly the cold-open state.
        assertFalse(WatchStore.pipelineProvenFresh())
        // A stamp is minted — the previous visit's probe write —
        // and 50 ms later the Deck is re-entered and a new
        // residency begins. (Clock advanced first so every
        // instant here is positive: 0 is the no-stamp sentinel.)
        ShadowSystemClock.advanceBy(100L, TimeUnit.MILLISECONDS)
        MonolithAccessibilityService.lastRootMono = SystemClock.elapsedRealtime()
        ShadowSystemClock.advanceBy(50L, TimeUnit.MILLISECONDS)
        WatchStore.noteResidencyStart()
        val start = WatchStore.deriveWarrant(
            a11yBound = true, stamped = false,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = 1L
        ).residencyStartMono
        // Setup proof: the stamp really does predate this
        // residency — it is the previous visit's mint.
        assertTrue(MonolithAccessibilityService.lastRootMono < start)
        // RE-PINNED (was: a stamp from BEFORE this residency does
        // not prove it). Under freshness doctrine that stamp is
        // 50 ms old, and 50 ms old is fresh proof — the pipeline
        // answered the system a breath ago. Pretending otherwise
        // re-opened the fog race on every Deck re-entry (DAY-1
        // D-2). Anti-inheritance survives elsewhere: onUnbind
        // zeroes the stamp, and age kills whatever unbind misses
        // (boundary test above).
        assertTrue(WatchStore.pipelineProvenFresh())
        val live = WatchStore.deriveWarrant(
            a11yBound = true, stamped = WatchStore.pipelineProvenFresh(),
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = start
        )
        assertEquals(WarrantState.LIVE, live.state)
        assertTrue(live.a11yStampedThisResidency)
        assertEquals(start, live.residencyStartMono)
        // The probe's success write — a stamp minted inside this
        // residency — proves it the same way.
        MonolithAccessibilityService.lastRootMono = SystemClock.elapsedRealtime()
        assertTrue(WatchStore.pipelineProvenFresh())
    }

    @Test
    fun aNewResidencyDoesNotSupersedeAFreshStamp() {
        // Clock advanced first so the residency start is a
        // positive instant (0 is the no-residency sentinel).
        ShadowSystemClock.advanceBy(100L, TimeUnit.MILLISECONDS)
        WatchStore.noteResidencyStart()
        val firstStart = WatchStore.deriveWarrant(
            a11yBound = true, stamped = false,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = 1L
        ).residencyStartMono
        MonolithAccessibilityService.lastRootMono = firstStart
        assertTrue(WatchStore.pipelineProvenFresh())

        // RE-PINNED (was: a new residency supersedes the old
        // stamp). The Deck is re-entered: a new residency starts —
        // now only the probe trigger and the grace anchor — and
        // the still-fresh stamp still proves the pipeline. No new
        // fog race opens on re-entry (DAY-1 D-2's masking cure).
        ShadowSystemClock.advanceBy(50L, TimeUnit.MILLISECONDS)
        WatchStore.noteResidencyStart()
        val secondStart = WatchStore.deriveWarrant(
            a11yBound = true, stamped = false,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = 2L
        ).residencyStartMono
        assertTrue(secondStart > firstStart)
        assertTrue(WatchStore.pipelineProvenFresh())

        // What kills the stamp is AGE, not residency: past the
        // freshness window it is no proof in any residency.
        ShadowSystemClock.advanceBy(WatchStore.STAMP_FRESH_MS, TimeUnit.MILLISECONDS)
        assertFalse(WatchStore.pipelineProvenFresh())

        // A reset (process death's test double) carries no
        // residency at all.
        WatchStore.resetForTest()
        assertFalse(WatchStore.residencyActive())
        assertEquals(
            0L,
            WatchStore.deriveWarrant(
                a11yBound = true, stamped = true,
                usage = UsageAccess.ALLOWED, shieldProtecting = false,
                readMono = 3L
            ).residencyStartMono
        )
    }

    @Test
    fun unboundWithGrantDerivesUnprovenInsideGraceAndOffOutsideIt() {
        // FIX-1's derivation half of the unbound grace (the
        // sentence-table half is the plain class's requirement
        // 6a/6b): grace age is readMono − graceAnchor, and the
        // anchor under the bind-epoch law is the first unproven
        // observation — the loop's first read below starts the
        // debt at exactly `start`, so ages measured from `start`
        // are anchor ages. (Clock advanced first so the residency
        // start is positive — 0 is the no-residency sentinel and
        // carries no grace. The start is read off a STAMPED
        // derive: a stamped read starts no debt, so it cannot
        // poison the anchor the loop is about to set.)
        ShadowSystemClock.advanceBy(100L, TimeUnit.MILLISECONDS)
        WatchStore.noteResidencyStart()
        val start = WatchStore.deriveWarrant(
            a11yBound = true, stamped = true,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = 1L
        ).residencyStartMono

        // Grant enabled, bind still landing: UNPROVEN at every
        // in-grace age, the boundary included — a checking state,
        // not a death.
        for (age in listOf(0L, 2_000L, WatchStore.RESIDENCY_GRACE_MS)) {
            val w = WatchStore.deriveWarrant(
                a11yBound = false, stamped = false,
                usage = UsageAccess.ALLOWED, shieldProtecting = false,
                readMono = start + age, a11yGrantEnabled = true
            )
            assertEquals("grant-enabled unbound at residency age $age", WarrantState.UNPROVEN, w.state)
            assertFalse(w.a11yBound)
        }
        // Past grace, the bind that never came is a fact: OFF.
        val pastGrace = WatchStore.deriveWarrant(
            a11yBound = false, stamped = false,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = start + WatchStore.RESIDENCY_GRACE_MS + 1L,
            a11yGrantEnabled = true
        )
        assertEquals(WarrantState.OFF, pastGrace.state)
        // No grant at all: OFF from residency age 0 — nothing is
        // binding because nothing is enabled.
        val noGrant = WatchStore.deriveWarrant(
            a11yBound = false, stamped = false,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = start, a11yGrantEnabled = false
        )
        assertEquals(WarrantState.OFF, noGrant.state)
    }
}
