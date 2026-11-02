package com.example

import com.example.domain.LiftingPlate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * P6 — THE LIFTING + PLATE contract (domain/LiftingPlate.kt),
 * by ledger row:
 *
 *  - A-13: the ceremony is bolts-then-weight in phase order; no
 *    phase is reachable out of order; an ajar day has no ceremony.
 *  - B-13: the handing back gates the plate — PLATE is
 *    unreachable, and plateReadable false, until HANDED_BACK.
 *  - A-10/B-14/E-1: struck once, never re-struck — a second
 *    strike returns the standing plate, the identical object.
 *  - B-14: uncovered hours strike nothing — no seal stood, no
 *    plate; punches beyond the held span are not struck.
 *  - A-12/B-11/E-6: the failure form — bracket gap requires both
 *    bracket hours and a non-whole seal; a whole day forbids them.
 *  - B-14/B-15: confessions append beside the plate, never
 *    backdated, never touching the struck truth; reading marks
 *    beside, never inside.
 *  - A-11/B-16/E-2: the pile's only place is the room tray; the
 *    pile refuses a second plate for a day already standing.
 *  - D-8: the drawer groups stored tasks by filing day (03:30
 *    seam) and keeps newest first.
 */
class LiftingPlateP6Test {

    private fun punches() = listOf(
        LiftingPlate.Punch(LiftingPlate.PunchKind.REACH, 1_000L, "com.locked.a"),
        LiftingPlate.Punch(LiftingPlate.PunchKind.HELD, 2_000L),
        LiftingPlate.Punch(LiftingPlate.PunchKind.FLOOR, 3_000L)
    )

    private fun wholeTruth(date: String = "2026-11-02") = LiftingPlate.plateTruthFor(
        filingDateIso = date,
        sealedSpanMillis = 8L * 3_600_000L,
        hoursHeldMillis = 8L * 3_600_000L,
        punches = punches(),
        sealTruth = LiftingPlate.SealTruth.WHOLE,
        crankSealBroken = false
    )!!

    // -- A-13 / B-13: the ceremony --------------------------------

    @Test
    fun ceremony_handBackIsAPrecondition_neverAPresentedPhase() {
        // R2-7 (MOTION-SR-4 M-2, ratified): the handing back is an
        // execution PRECONDITION of the lifting's first presented
        // phase — there is no handing-back phase to present.
        assertFalse(
            LiftingPlate.LiftPhase.values().any { it.name.contains("HANDING") }
        )
        val waiting = LiftingPlate.HandingBack.prepare(
            listOf(LiftingPlate.WaitingPerson("Mother", "contact://1"))
        )
        var s = LiftingPlate.CeremonyState.begin(waiting)
        assertFalse(s.plateReadable)
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.PLATE_READ)
        assertEquals(LiftingPlate.LiftPhase.SEALED, s.phase)

        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.LIFT_FIRED)
        assertEquals(LiftingPlate.LiftPhase.BOLTS_WITHDRAWING, s.phase)
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.DOOR_SETTLED)
        assertEquals(LiftingPlate.LiftPhase.BOLTS_WITHDRAWING, s.phase)

        repeat(LiftingPlate.LiftingScore.BOLT_COUNT) {
            s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.BOLT_WITHDRAWN)
        }
        assertEquals(LiftingPlate.LiftPhase.DOOR_OPENING, s.phase)

        // The door settles — but with the precondition unproven,
        // the ceremony holds at the settled door, NOT at the plate.
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.DOOR_SETTLED)
        assertEquals(LiftingPlate.LiftPhase.DOOR_OPENING, s.phase)
        assertTrue(s.doorSettled)
        assertFalse(s.plateReadable)

        // The precondition's confirmation completes the wait.
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.HANDED_BACK)
        assertEquals(LiftingPlate.LiftPhase.PLATE, s.phase)
        assertTrue(s.plateReadable)
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.PLATE_READ)
        assertEquals(LiftingPlate.LiftPhase.OPEN, s.phase)
    }

    @Test
    fun ceremony_preconditionConfirmedEarly_flowsStraightThrough() {
        val waiting = LiftingPlate.HandingBack.prepare(
            listOf(LiftingPlate.WaitingPerson("Mother", "contact://1"))
        )
        var s = LiftingPlate.CeremonyState.begin(waiting)
        // The wiring executes the handing back as the lift fires —
        // before the first presented phase completes.
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.HANDED_BACK)
        assertTrue(s.handingBackDelivered)
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.LIFT_FIRED)
        repeat(LiftingPlate.LiftingScore.BOLT_COUNT) {
            s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.BOLT_WITHDRAWN)
        }
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.DOOR_SETTLED)
        assertEquals(LiftingPlate.LiftPhase.PLATE, s.phase)
    }

    @Test
    fun ceremony_emptyHandingBackDeliversInTheSameAct() {
        var s = LiftingPlate.CeremonyState.begin(LiftingPlate.HandingBack(emptyList()))
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.LIFT_FIRED)
        repeat(LiftingPlate.LiftingScore.BOLT_COUNT) {
            s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.BOLT_WITHDRAWN)
        }
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.DOOR_SETTLED)
        // Nobody waited: the handing back is delivered empty and
        // the ceremony stands at the plate — it never stalls.
        assertEquals(LiftingPlate.LiftPhase.PLATE, s.phase)
        assertTrue(s.handingBackDelivered)
    }

    @Test
    fun ceremony_ajarDayHasNoBolts_andItsTestimonyGatesNothing() {
        val waiting = LiftingPlate.HandingBack.prepare(
            listOf(LiftingPlate.WaitingPerson("Unknown — 98xxxx", "call://9"))
        )
        var s = LiftingPlate.CeremonyState.beginAjar(waiting)
        assertEquals(LiftingPlate.LiftPhase.AJAR, s.phase)
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.BOLT_WITHDRAWN)
        assertEquals(LiftingPlate.LiftPhase.AJAR, s.phase)
        assertEquals(0, s.boltsWithdrawn)
        // R2-3: seal death already released the phone; this
        // testimony surface closes on his word alone — the
        // handing back is the wiring's precondition act, never
        // a toll this face collects.
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.PLATE_READ)
        assertEquals(LiftingPlate.LiftPhase.OPEN, s.phase)
    }

    @Test
    fun ceremony_unreadPlateClosesTheDay_andStaysUnreadInThePile() {
        // CRITIC-SR-6 DF-10 / B-15: the reading is offered, owed
        // never. From PLATE, leaving unread closes the day exactly
        // as reading does — and the plate that enters the pile is
        // the same unread object (no mark, no debt).
        val plate = LiftingPlate.StruckPlate(wholeTruth(), 100L)
        var s = LiftingPlate.CeremonyState.begin(LiftingPlate.HandingBack(emptyList()))
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.LIFT_FIRED)
        repeat(LiftingPlate.LiftingScore.BOLT_COUNT) {
            s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.BOLT_WITHDRAWN)
        }
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.DOOR_SETTLED)
        assertEquals(LiftingPlate.LiftPhase.PLATE, s.phase)
        s = LiftingPlate.advance(s, LiftingPlate.LiftEvent.PLATE_LEFT_UNREAD)
        assertEquals(LiftingPlate.LiftPhase.OPEN, s.phase)
        assertFalse(plate.isRead)
        val pile = LiftingPlate.PlatePile(listOf(plate))
        assertSame(plate, pile.plateFor(plate.truth.filingDateIso))

        // The ajar day closes unread on the same arm.
        var a = LiftingPlate.CeremonyState.beginAjar(LiftingPlate.HandingBack(emptyList()))
        a = LiftingPlate.advance(a, LiftingPlate.LiftEvent.PLATE_LEFT_UNREAD)
        assertEquals(LiftingPlate.LiftPhase.OPEN, a.phase)
    }

    @Test
    fun pile_snappedSealsLiveInTheTray_inPileOrder() {
        // R3 CC-5: the crank seal's home is the TRAY, not the
        // plate. The pile names the snapped days in stack order —
        // the tray's interim source until the week state carries
        // snap order itself (DESIGNER-SR-4's interim instruction).
        val snapped = LiftingPlate.plateTruthFor(
            filingDateIso = "2026-10-30", sealedSpanMillis = 1_000L, hoursHeldMillis = 1_000L,
            punches = emptyList(), sealTruth = LiftingPlate.SealTruth.WHOLE, crankSealBroken = true
        )!!
        val whole = wholeTruth("2026-11-02")
        val pSnapped = LiftingPlate.StruckPlate(snapped, 1L)
        val pWhole = LiftingPlate.StruckPlate(whole, 2L)
        val pile = LiftingPlate.PlatePile(listOf(pWhole, pSnapped))
        assertEquals(listOf("2026-10-30"), pile.snappedSealDays())
    }

    @Test
    fun handingBack_dedupesByOwnListRef_keepsFirstWaitedOrder() {
        val hb = LiftingPlate.HandingBack.prepare(
            listOf(
                LiftingPlate.WaitingPerson("Mother", "contact://1"),
                LiftingPlate.WaitingPerson("Mother (again)", "contact://1"),
                LiftingPlate.WaitingPerson("Coach", "contact://2")
            )
        )
        assertEquals(listOf("Mother", "Coach"), hb.waiting.map { it.displayName })
    }

    @Test
    fun liftingScore_isP0sLiftClunks_ascendingInWeight() {
        val bolts = LiftingPlate.LiftingScore.BOLTS
        assertEquals(3, bolts.size)
        assertEquals(listOf(0, 190, 420), bolts.map { it.atMs })
        assertEquals(listOf(120, 150, 190), bolts.map { it.durationMs })
        assertTrue(bolts.zipWithNext().all { (a, b) -> a.weight < b.weight })
        assertEquals(610, LiftingPlate.LiftingScore.boltsCompleteAtMs)
        assertEquals(0, LiftingPlate.LiftingScore.boltsWithdrawnAt(119))
        assertEquals(1, LiftingPlate.LiftingScore.boltsWithdrawnAt(120))
        assertEquals(3, LiftingPlate.LiftingScore.boltsWithdrawnAt(610))
        assertEquals(0.86f, LiftingPlate.LiftingScore.DOOR_SPRING_DAMPING)
        assertEquals(55f, LiftingPlate.LiftingScore.DOOR_SPRING_STIFFNESS)
    }

    @Test
    fun punchGrades_areTheFiledFingerLearnableOrder() {
        assertEquals(LiftingPlate.PunchGrade.LARGE, LiftingPlate.PunchKind.REACH.grade)
        assertEquals(LiftingPlate.PunchGrade.MEDIUM, LiftingPlate.PunchKind.HELD.grade)
        assertEquals(LiftingPlate.PunchGrade.SMALL, LiftingPlate.PunchKind.FLOOR.grade)
    }

    // -- Struck once, never re-struck ------------------------------

    // RETIRED AT COMPOSITION (BUILD-SR-13): the dedicated
    // strikeGuard_secondStrikeReturnsTheStandingPlateUntouched test
    // asserted the standalone PlateStrikeGuard, retired above into
    // the store. The struck-once law it carried is test-carried
    // against its surviving home by StrongroomMigrationP1Test
    // (P1): a second store.strikePlate returns the plate as struck
    // and writes nothing. Named in the composition record.

    // -- B-14: uncovered hours strike nothing ----------------------

    @Test
    fun uncoveredDay_strikesNoPlateAtAll() {
        val truth = LiftingPlate.plateTruthFor(
            filingDateIso = "2026-11-02",
            sealedSpanMillis = 0L,
            hoursHeldMillis = 0L,
            punches = punches(),
            sealTruth = LiftingPlate.SealTruth.WHOLE,
            crankSealBroken = false
        )
        assertNull(truth)
    }

    @Test
    fun punchesBeyondTheHeldSpan_areNotStruck() {
        val truth = LiftingPlate.plateTruthFor(
            filingDateIso = "2026-11-02",
            sealedSpanMillis = 10_000L,
            hoursHeldMillis = 2_500L,
            punches = punches(),
            sealTruth = LiftingPlate.SealTruth.AJAR,
            crankSealBroken = false,
            bracketGapMillis = 7_500L,
            bracketStandingHour = "05:02",
            bracketFallenHour = "11:40"
        )!!
        // Punches stand at 1000/2000/3000; the held span ends at
        // 2500 — the 3000 punch belongs to uncovered time.
        assertEquals(listOf(1_000L, 2_000L), truth.punchesInOrder.map { it.atOffsetMillis })
    }

    // -- A-12: the bracket form ------------------------------------

    @Test
    fun bracketStrike_requiresBothHoursAndANonWholeSeal() {
        try {
            LiftingPlate.PlateTruth(
                filingDateIso = "2026-11-02", hoursHeldMillis = 0L, punches = emptyList(),
                sealTruth = LiftingPlate.SealTruth.WHOLE, crankSealBroken = false,
                bracketGapMillis = 10L, bracketStandingHour = "05:02", bracketFallenHour = "11:40"
            )
            fail("a whole day must refuse a bracket strike")
        } catch (expected: IllegalArgumentException) { }
        try {
            LiftingPlate.PlateTruth(
                filingDateIso = "2026-11-02", hoursHeldMillis = 0L, punches = emptyList(),
                sealTruth = LiftingPlate.SealTruth.AJAR, crankSealBroken = false,
                bracketGapMillis = 10L
            )
            fail("a bracket strike without its hours must be refused")
        } catch (expected: IllegalArgumentException) { }
    }

    // -- B-14/B-15: confessions and reading -------------------------

    @Test
    fun confession_appendsBesideThePlate_neverBackdated_neverTouchingTheStrike() {
        val plate = LiftingPlate.StruckPlate(wholeTruth(), 100L)
        try {
            LiftingPlate.PlateConfession("late standing", aboutMomentMillis = 500L, confessedAtMillis = 400L)
            fail("a backdated confession must be refused")
        } catch (expected: IllegalArgumentException) { }

        val confessed = plate.confess(
            LiftingPlate.PlateConfession("The seal stood until the power died — learned at the next signing", 500L, 900L)
        )
        assertSame(plate.truth, confessed.truth)
        assertEquals(100L, confessed.struckAtElapsedMillis)
        assertEquals(1, confessed.confessions.size)
        assertEquals(0, plate.confessions.size)

        val read = confessed.markRead(1_000L)
        assertTrue(read.isRead)
        assertSame(plate.truth, read.truth)
        assertSame(read, read.markRead(2_000L))
    }

    // -- A-11/B-16/E-2: the pile ------------------------------------

    @Test
    fun pile_livesOnlyInTheRoomTray_newestOnTop_refusesDuplicates() {
        val p1 = LiftingPlate.StruckPlate(wholeTruth("2026-11-01"), 1L)
        val p2 = LiftingPlate.StruckPlate(wholeTruth("2026-11-02"), 2L)
        val pile = LiftingPlate.PlatePile(listOf(p1)).stacked(p2)
        assertEquals(LiftingPlate.PilePlace.ROOM_TRAY, pile.place)
        assertEquals(1, LiftingPlate.PilePlace.values().size)
        assertEquals(listOf("2026-11-02", "2026-11-01"), pile.platesNewestFirst.map { it.truth.filingDateIso })
        assertSame(pile, pile.stacked(p1))
        assertNotNull(pile.plateFor("2026-11-01"))
    }

    // -- D-8: the drawer ---------------------------------------------

    @Test
    fun drawer_storesByFilingDayAtThe0330Seam_newestFirst() {
        // 2026-11-02 02:00 local files under 2026-11-01 (before the
        // 03:30 seam); 2026-11-02 05:00 files under 2026-11-02.
        val zone = java.time.ZoneId.of("Asia/Kolkata")
        fun at(day: Int, hour: Int): Long =
            java.time.LocalDateTime.of(2026, 11, day, hour, 0).atZone(zone).toInstant().toEpochMilli()
        val drawer = LiftingPlate.TaskDrawer(
            listOf(
                LiftingPlate.DrawerTask(1, "early hours task", at(2, 2)),
                LiftingPlate.DrawerTask(2, "morning task", at(2, 5)),
                LiftingPlate.DrawerTask(3, "later task", at(2, 21))
            )
        )
        assertEquals(listOf("later task", "morning task", "early hours task"), drawer.tasksNewestFirst.map { it.title })
        val byDay = drawer.byFilingDay(zone)
        assertEquals(listOf("2026-11-02", "2026-11-01"), byDay.keys.toList())
        assertEquals(listOf("early hours task"), byDay["2026-11-01"]!!.map { it.title })
    }
}
