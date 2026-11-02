package com.example

import com.example.domain.StrongroomDay
import com.example.domain.StrongroomRouting
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

/**
 * P2 (BUILD-SR-3) — engine routing, pure JVM. The verdicts the
 * services execute, by name: containment's closed surface set, the
 * knock (and nothing else), exits landing in the antechamber only
 * with the session still sealed, contacts ring / unknowns held
 * quietly with a per-call choice that grants nothing else, firings
 * derived from the elapsed span, and tamper outranking ajar.
 * Device effect stands UNPROVEN-ON-DEVICE (Audit A G-7).
 */
class StrongroomRoutingP2Test {

    private val locked = setOf("com.video.app", "com.social.app")
    private val allowed = setOf("com.notes.app", "com.dialer")

    @Test
    fun containment_sealedSurfaceSetIsClosed_normalAppNeverLawful() {
        assertTrue(StrongroomRouting.surfaceLawfulWhileSealed(StrongroomRouting.Surface.SEALED_FACE))
        assertTrue(StrongroomRouting.surfaceLawfulWhileSealed(StrongroomRouting.Surface.ANTECHAMBER))
        assertTrue(StrongroomRouting.surfaceLawfulWhileSealed(StrongroomRouting.Surface.HELD_CALLS))
        assertFalse(StrongroomRouting.surfaceLawfulWhileSealed(StrongroomRouting.Surface.NORMAL_APP))
        assertFalse(StrongroomRouting.surfaceLawfulWhileSealed(StrongroomRouting.Surface.DECK))
        assertFalse(StrongroomRouting.surfaceLawfulWhileSealed(StrongroomRouting.Surface.SETTINGS))
        assertFalse(StrongroomRouting.surfaceLawfulWhileSealed(StrongroomRouting.Surface.RECORDS))
    }

    @Test
    fun knock_lockedReachKnocks_allowedReachesTheAntechamber_unlistedKnocks() {
        assertEquals(
            StrongroomRouting.ReachDecision.KNOCK,
            StrongroomRouting.routeReach(true, "com.video.app", locked, allowed)
        )
        assertEquals(
            StrongroomRouting.ReachDecision.ANTECHAMBER,
            StrongroomRouting.routeReach(true, "com.notes.app", locked, allowed)
        )
        // Unlisted is born LOCKED (the substrate's fail-closed rule).
        assertEquals(
            StrongroomRouting.ReachDecision.KNOCK,
            StrongroomRouting.routeReach(true, "com.unlisted.app", locked, allowed)
        )
        // A package in both sets resolves toward the seal.
        assertEquals(
            StrongroomRouting.ReachDecision.KNOCK,
            StrongroomRouting.routeReach(true, "com.video.app", locked, allowed + "com.video.app")
        )
        assertEquals(
            StrongroomRouting.ReachDecision.NOT_SEALED,
            StrongroomRouting.routeReach(false, "com.video.app", locked, allowed)
        )
    }

    @Test
    fun exits_everyKindLandsInTheAntechamber_sessionStillSealed_doorShut() {
        for (kind in StrongroomRouting.ExitKind.values()) {
            val landing = StrongroomRouting.routeExit(kind)
            assertEquals(StrongroomRouting.Surface.ANTECHAMBER, landing.surface)
            assertFalse(landing.doorOpened)
            assertTrue(landing.sessionSealed)
        }
    }

    @Test
    fun heldCalls_contactsRing_unknownsHeld_perCallChoiceGrantsNothingElse() {
        assertEquals(
            StrongroomRouting.CallDecision.RING_THROUGH,
            StrongroomRouting.routeCall(StrongroomRouting.CallerClass.CONTACT)
        )
        assertEquals(
            StrongroomRouting.CallDecision.HELD_QUIETLY,
            StrongroomRouting.routeCall(StrongroomRouting.CallerClass.UNKNOWN)
        )
        val take = StrongroomRouting.resolveHeldCall(StrongroomRouting.HeldCallChoice.TAKE)
        assertTrue(take.connectsThisCallOnly)
        assertFalse(take.doorOpened)
        assertTrue(take.sessionSealed)
        val leave = StrongroomRouting.resolveHeldCall(StrongroomRouting.HeldCallChoice.LEAVE)
        assertFalse(leave.connectsThisCallOnly)
        assertTrue(leave.sessionSealed)
    }

    @Test
    fun firings_derivedFromTheElapsedSpan_inOrder() {
        val signing = StrongroomDay.SigningRecord(
            filingDateIso = "2026-11-03",
            lockedPackages = locked,
            liftingTime = LocalTime.of(6, 0),
            sentence = null,
            breakLengthMinutes = 20,
            breakFiresAtOffsetMillis = 3_600_000L,
            allowanceMinutes = 30,
            gaps = emptyList()
        )
        val seal = StrongroomDay.SealSpan(100_000L, 3, 10_800_000L)
        val schedule = StrongroomRouting.firingSchedule(signing, seal)
        assertEquals(
            listOf(
                StrongroomRouting.FiringKind.BREAK_START,
                StrongroomRouting.FiringKind.BREAK_END,
                StrongroomRouting.FiringKind.LIFT
            ),
            schedule.map { it.kind }
        )
        assertEquals(100_000L + 3_600_000L, schedule[0].atElapsedMillis)
        assertEquals(seal.liftingAtElapsedMillis, schedule[2].atElapsedMillis)
        assertEquals(
            StrongroomRouting.FiringKind.BREAK_START,
            StrongroomRouting.nextFiring(signing, seal, 100_000L)?.kind
        )
        assertNull(StrongroomRouting.nextFiring(signing, seal, seal.liftingAtElapsedMillis))
    }

    @Test
    fun firings_bootGuard_noFiringInAnAjarDay() {
        // CH-2: the break (and the lift) are firings of the span's
        // own clock; when the boot died, that clock died — nothing
        // fires in an ajar day.
        val signing = StrongroomDay.SigningRecord(
            filingDateIso = "2026-11-03",
            lockedPackages = locked,
            liftingTime = LocalTime.of(6, 0),
            sentence = null,
            breakLengthMinutes = 20,
            breakFiresAtOffsetMillis = 3_600_000L,
            allowanceMinutes = 30,
            gaps = emptyList()
        )
        val seal = StrongroomDay.SealSpan(100_000L, 3, 10_800_000L)
        val breakDueAt = 100_000L + 3_600_000L
        assertEquals(
            StrongroomRouting.FiringKind.BREAK_START,
            StrongroomRouting.firingDue(signing, seal, breakDueAt, nowBootCount = 3)?.kind
        )
        assertNull(StrongroomRouting.firingDue(signing, seal, breakDueAt, nowBootCount = 4))
        assertNull(
            StrongroomRouting.firingDue(
                signing, seal, seal.liftingAtElapsedMillis, nowBootCount = 4
            )
        )
    }

    @Test
    fun integrity_tamperOutranksAjar_bootDeathIsAjar_intactStands() {
        val seal = StrongroomDay.SealSpan(100_000L, 3, 10_800_000L)
        assertEquals(
            StrongroomRouting.SealIntegrity.INTACT,
            StrongroomRouting.classifyIntegrity(seal, nowBootCount = 3, sidewaysKillObserved = false)
        )
        assertEquals(
            StrongroomRouting.SealIntegrity.AJAR_BOOT_DEATH,
            StrongroomRouting.classifyIntegrity(seal, nowBootCount = 4, sidewaysKillObserved = false)
        )
        assertEquals(
            StrongroomRouting.SealIntegrity.BROKEN_TAMPER,
            StrongroomRouting.classifyIntegrity(seal, nowBootCount = 4, sidewaysKillObserved = true)
        )
        assertEquals(
            StrongroomRouting.SealIntegrity.BROKEN_TAMPER,
            StrongroomRouting.classifyIntegrity(seal, nowBootCount = 3, sidewaysKillObserved = true)
        )
    }
}
