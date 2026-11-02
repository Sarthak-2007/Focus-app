package com.example

import com.example.ui.guard.GuardControl
import com.example.ui.guard.GuardCopy
import com.example.ui.guard.GuardIntent
import com.example.ui.guard.GuardRoutineSnapshot
import com.example.ui.guard.GuardSurface
import com.example.ui.guard.GuardSurfaceSnapshot
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P7 (BUILD-SR-5) — THE GUARD SURFACES' MODEL, pure JVM.
 *
 * His Oct 31 rulings by name (ledger D-1/D-2/D-3/D-4/D-9):
 * the tests that matter most here are NEGATIVES — a control
 * that must NOT exist in a state is asserted absent from the
 * inventory, not disabled, not dimmed. Absence is the ruling's
 * shape: beyond the hard stop (D-1), no off while sealed (D-2),
 * no off inside a stretch at all (D-3), no witness/urge entry
 * in the census, ever (D-3), no exit counter anywhere (D-4).
 *
 * G-a is carried UNRESOLVED: the raise asserts D-1's own road
 * (next day-start) and the census asserts NO two-signature
 * control exists for this seat to have pre-built.
 */
class GuardSurfaceModelP7Test {

    private val zone: ZoneId = ZoneId.of("Asia/Kolkata")
    private val now: Long =
        LocalDate.of(2026, 11, 3).atTime(20, 0).atZone(zone).toInstant().toEpochMilli()
    private val nextDayStart: Long =
        LocalDate.of(2026, 11, 4).atTime(3, 30).atZone(zone).toInstant().toEpochMilli()

    private fun snapshot(
        guardOn: Boolean = true,
        allowanceMinutes: Int = 30,
        pendingAllowanceMinutes: Int = -1,
        pendingAllowanceFromMillis: Long = 0L,
        pendingOffFromMillis: Long = 0L,
        stretchEndMillis: Long = 0L,
        sealedBlockActive: Boolean = false,
        purseSpentToday: Boolean = false,
        crankMinuteSpent: Boolean = false,
        advanceChosenExits: List<String> = listOf("The pre-set PIN, to the antechamber"),
        routines: List<GuardRoutineSnapshot> = emptyList()
    ) = GuardSurfaceSnapshot(
        guardOn = guardOn,
        allowanceMinutes = allowanceMinutes,
        pendingAllowanceMinutes = pendingAllowanceMinutes,
        pendingAllowanceFromMillis = pendingAllowanceFromMillis,
        pendingOffFromMillis = pendingOffFromMillis,
        stretchEndMillis = stretchEndMillis,
        nowMillis = now,
        nextDayStartMillis = nextDayStart,
        sealedBlockActive = sealedBlockActive,
        purseSpentToday = purseSpentToday,
        crankMinuteSpent = crankMinuteSpent,
        advanceChosenExits = advanceChosenExits,
        routines = routines
    )

    private fun surface(
        snap: GuardSurfaceSnapshot = snapshot()
    ) = GuardSurface.surfaceFor(snap, zone)

    // ---------- D-1: frozen allowance ----------

    @Test
    fun tightenWheel_alwaysExists_evenSealedEvenInAStretch() {
        assertTrue(surface().has(GuardControl.ALLOWANCE_TIGHTEN_WHEEL))
        assertTrue(
            surface(snapshot(sealedBlockActive = true))
                .has(GuardControl.ALLOWANCE_TIGHTEN_WHEEL)
        )
        val stretchEnd =
            LocalDate.of(2026, 11, 10).atTime(3, 30).atZone(zone).toInstant().toEpochMilli()
        assertTrue(
            surface(snapshot(stretchEndMillis = stretchEnd))
                .has(GuardControl.ALLOWANCE_TIGHTEN_WHEEL)
        )
    }

    @Test
    fun raiseControl_saysItsTruthPlainly_nextDayStartNeverToday() {
        val model = surface()
        assertTrue(model.has(GuardControl.ALLOWANCE_RAISE_WHEEL))
        assertTrue(model.has(GuardControl.ALLOWANCE_RAISE_CONFIRM))
        assertEquals(GuardCopy.RAISE_LAW, model.raiseLine)
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // Stage 9 §12.2 struck the dated form — one line serves
        // every pending loosening, and it carries NO figure: no
        // day-start time, no date, no minutes. The truth the name
        // asserts (next day-start, never today) is the line's
        // whole content, in the deck's words.
        assertEquals("Stands from the next day-start.", model.raiseLine)
        assertFalse(model.raiseLine!!.contains("03:30"))
        assertFalse(model.raiseLine!!.any { it.isDigit() })
    }

    @Test
    fun onceSpent_looseningControlsAreAbsent_notGreyed() {
        val model = surface(snapshot(purseSpentToday = true))
        assertFalse(model.has(GuardControl.ALLOWANCE_RAISE_WHEEL))
        assertFalse(model.has(GuardControl.ALLOWANCE_RAISE_CONFIRM))
        assertNull(model.raiseLine)
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // Stage 9 §12 composes NO hard-stop line at all — beyond
        // the spent purse the loosening side is absence with
        // ∅ copy, so the model composes null (GuardCopy.HARD_STOP
        // stands empty precisely to name that ∅).
        assertNull(model.hardStopLine)
        assertEquals("", GuardCopy.HARD_STOP)
        // Tightening is not a loosening: it survives the hard stop.
        assertTrue(model.has(GuardControl.ALLOWANCE_TIGHTEN_WHEEL))
    }

    @Test
    fun pendingRaise_isToldWithItsDayStart() {
        val model = surface(
            snapshot(pendingAllowanceMinutes = 45, pendingAllowanceFromMillis = nextDayStart)
        )
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // §12.2's one line carries no figure — not the raised
        // minutes, not the date, not the day-start time.
        assertEquals("Stands from the next day-start.", model.pendingRaiseLine)
        assertFalse(model.pendingRaiseLine!!.contains("45"))
    }

    @Test
    fun tightenDetents_fiveMinuteStops_andTheStandingFigureAlwaysReachable() {
        assertEquals(listOf(0, 5, 10, 15, 20, 25, 30), GuardSurface.tightenDetents(30))
        // A legacy allowance off the grid keeps its own stop.
        assertEquals(listOf(0, 5, 10, 15, 20, 25, 30, 32), GuardSurface.tightenDetents(32))
        assertEquals(listOf(0), GuardSurface.tightenDetents(0))
    }

    // ---------- D-2: slow off ----------

    @Test
    fun off_offeredOnCalmGround_andItsLawIsNextDayStart() {
        val model = surface()
        assertTrue(model.has(GuardControl.GUARD_REQUEST_OFF))
        assertNull(model.pendingOffLine)
    }

    @Test
    fun off_whileSealed_doesNotExist() {
        val model = surface(snapshot(sealedBlockActive = true))
        assertFalse(model.has(GuardControl.GUARD_REQUEST_OFF))
    }

    @Test
    fun pendingOff_theOffPlateLeaves_andOnStandsToCancelIt() {
        val model = surface(snapshot(pendingOffFromMillis = nextDayStart))
        assertFalse(model.has(GuardControl.GUARD_REQUEST_OFF))
        assertTrue(model.has(GuardControl.GUARD_TURN_ON))
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // a pending off is told by §12.2's one line, no figure.
        assertEquals("Stands from the next day-start.", model.pendingOffLine)
    }

    @Test
    fun guardOff_onIsTheOnlyStandingPlate() {
        val model = surface(snapshot(guardOn = false))
        assertTrue(model.has(GuardControl.GUARD_TURN_ON))
        assertFalse(model.has(GuardControl.GUARD_REQUEST_OFF))
        assertEquals(GuardCopy.STANDING_OFF, model.standingLine)
    }

    // ---------- D-3: a stretch has no off ----------

    @Test
    fun stretchSetup_existsOnlyOutsideAStretch() {
        val calm = surface()
        assertTrue(calm.has(GuardControl.STRETCH_END_STEPPER))
        assertTrue(calm.has(GuardControl.STRETCH_BEGIN_CONFIRM))
        assertNull(calm.stretchLine)
    }

    @Test
    fun insideAStretch_noOffNoSetup_onlyTheEndDate() {
        val end =
            LocalDate.of(2026, 11, 10).atTime(3, 30).atZone(zone).toInstant().toEpochMilli()
        val model = surface(snapshot(stretchEndMillis = end))
        assertFalse(model.has(GuardControl.GUARD_REQUEST_OFF))
        assertFalse(model.has(GuardControl.STRETCH_END_STEPPER))
        assertFalse(model.has(GuardControl.STRETCH_BEGIN_CONFIRM))
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // §12.4 renders NO exit wording inside the thing it
        // would exit — inside a stretch the line is the end
        // date, and nothing else.
        assertEquals("This stretch ends Tuesday 10 November.", model.stretchLine)
        assertFalse(model.stretchLine!!.contains("exit", ignoreCase = true))
        assertFalse(model.stretchLine!!.contains("off", ignoreCase = true))
    }

    @Test
    fun stretchEnd_isStruckAtItsDayStart() {
        val end = GuardSurface.stretchEndMillis(LocalDate.of(2026, 11, 10), zone)
        assertEquals(
            LocalDate.of(2026, 11, 10).atTime(3, 30).atZone(zone).toInstant().toEpochMilli(),
            end
        )
    }

    @Test
    fun census_carriesNoWitnessNoUrgeNoEarlyStretchEndNoValveSpend() {
        val names = GuardControl.values().map { it.name }
        assertTrue(names.none { it.contains("WITNESS") || it.contains("URGE") })
        assertTrue(names.none { it.contains("END_STRETCH") || it.contains("CANCEL_STRETCH") })
        assertTrue(names.none { it.contains("VALVE") || it.contains("CRANK") })
        val intents = GuardIntent::class.java.declaredClasses.map { it.simpleName }
        assertTrue(intents.none { it.contains("Witness") || it.contains("Urge") })
        assertTrue(intents.none { it.contains("EndStretch") || it.contains("SpendValve") })
    }

    // ---------- D-4: exits, advance-chosen + the valve only ----------

    @Test
    fun exits_areNamesAndTheValveFact_neverACount() {
        val model = surface(snapshot(crankMinuteSpent = true))
        assertEquals(listOf("The pre-set PIN, to the antechamber"), model.exitNames)
        assertEquals(GuardCopy.valveLine(true), model.valveLine)
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // §12.6/§6.3 shows the valve as a struck fact with
        // ∅ words — whole or spent reads from the seal itself,
        // so the line is empty in BOTH states and no wording
        // ever stands for the valve's fact.
        assertEquals("", model.valveLine)
        assertEquals("", GuardCopy.valveLine(false))
        // Type-level: no field on the snapshot or the model can
        // hold a stockpile — no count, no balance, no remaining.
        val fields = (GuardSurfaceSnapshot::class.java.declaredFields +
            com.example.ui.guard.GuardSurfaceModel::class.java.declaredFields)
            .map { it.name.lowercase() }
        assertTrue(
            fields.none {
                it.contains("exitcount") || it.contains("stockpile") ||
                    it.contains("remainingexits") || it.contains("exitbalance")
            }
        )
    }

    // ---------- D-9: routines under the Guard ----------

    @Test
    fun routineOn_isInstant_routineOff_travelsTheSlowRoad() {
        val off = GuardRoutineSnapshot(id = 1, name = "Morning block", enabled = false)
        val on = GuardRoutineSnapshot(id = 2, name = "Evening block", enabled = true)
        val model = surface(snapshot(routines = listOf(off, on)))
        assertEquals(setOf(GuardControl.ROUTINE_TURN_ON), model.routineRows[0].controls)
        assertEquals(setOf(GuardControl.ROUTINE_TURN_OFF), model.routineRows[1].controls)
    }

    @Test
    fun routineEnforcing_itsOffDoesNotExist() {
        val enforcing =
            GuardRoutineSnapshot(id = 3, name = "Live window", enabled = true, enforcingNow = true)
        val row = surface(snapshot(routines = listOf(enforcing))).routineRows.single()
        assertTrue(row.controls.isEmpty())
        assertEquals(GuardCopy.ROUTINE_ENFORCING_LINE, row.line)
    }

    @Test
    fun routinePendingOff_noPlate_onlyItsLandingLine() {
        val pending = GuardRoutineSnapshot(
            id = 4, name = "Winding down", enabled = true, pendingOffFromMillis = nextDayStart
        )
        val row = surface(snapshot(routines = listOf(pending))).routineRows.single()
        assertTrue(row.controls.isEmpty())
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // the routine's landing line is §12.2's one line.
        assertEquals("Stands from the next day-start.", row.line)
    }

    // ---------- VERIFY-SR-4's owed P7-T names, by name ----------
    // The pre-verification map (ascent/build-first-usable/work/
    // verify-sr-4/p2-p7-state-test-map.md, P7 table) owes these
    // names; each is discharged here against the same model the
    // tests above prove in this lane's own words.

    @Test
    fun tighten_appliesInstantly_fromSurface() {
        // The tighten wheel is in the inventory in EVERY state —
        // no pending, no day-start, no queue exists for it in the
        // model: presence here IS the surface's instant tighten.
        assertTrue(surface().has(GuardControl.ALLOWANCE_TIGHTEN_WHEEL))
        assertTrue(
            surface(snapshot(sealedBlockActive = true))
                .has(GuardControl.ALLOWANCE_TIGHTEN_WHEEL)
        )
    }

    @Test
    fun raise_effectiveNextDayStart_shownAsPendingUntilThen() {
        val model = surface(
            snapshot(pendingAllowanceMinutes = 45, pendingAllowanceFromMillis = nextDayStart)
        )
        // Until the day-start lands, the standing allowance is
        // still today's word and the raise reads as pending.
        assertEquals(GuardCopy.allowanceLine(30), model.allowanceLine)
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // the pending raise is told by §12.2's one line exactly.
        assertEquals("Stands from the next day-start.", model.pendingRaiseLine)
    }

    @Test
    fun offControl_schedulesNextDayStart_neverImmediate() {
        val calm = surface()
        assertTrue(calm.has(GuardControl.GUARD_REQUEST_OFF))
        assertTrue(GuardCopy.OFF_LAW.contains("next day-start"))
        // Once asked, it stands as a PENDING off landing at the
        // day-start — the standing line still reads ON until then.
        val pending = surface(snapshot(pendingOffFromMillis = nextDayStart))
        assertEquals(GuardCopy.STANDING_ON, pending.standingLine)
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // the pending off lands at the day-start and is told by
        // §12.2's one line exactly — no date, no figure.
        assertEquals("Stands from the next day-start.", pending.pendingOffLine)
    }

    @Test
    fun offControl_absentWhileSealedBlockActive() {
        assertFalse(
            surface(snapshot(sealedBlockActive = true)).has(GuardControl.GUARD_REQUEST_OFF)
        )
    }

    @Test
    fun stretchSetup_endDateIsTheExit_noOffControlRendered() {
        val end =
            LocalDate.of(2026, 11, 10).atTime(3, 30).atZone(zone).toInstant().toEpochMilli()
        val inside = surface(snapshot(stretchEndMillis = end))
        assertFalse(inside.has(GuardControl.GUARD_REQUEST_OFF))
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // the end-date-is-the-exit truth stands at SETUP in the
        // deck's term (§12.4, GuardCopy.STRETCH_SETUP_LAW);
        // inside the stretch no exit wording is rendered at
        // all — the line is the end date alone.
        assertEquals("The end date is the exit.", GuardCopy.STRETCH_SETUP_LAW)
        assertEquals("This stretch ends Tuesday 10 November.", inside.stretchLine)
        assertFalse(inside.stretchLine!!.contains("exit", ignoreCase = true))
    }

    @Test
    fun noWitnessOrUrgeButton_anywhereInStretchSurfaces() {
        val names = GuardControl.values().map { it.name }
        assertTrue(names.none { it.contains("WITNESS") || it.contains("URGE") })
        val end =
            LocalDate.of(2026, 11, 10).atTime(3, 30).atZone(zone).toInstant().toEpochMilli()
        val inside = surface(snapshot(stretchEndMillis = end))
        assertTrue(
            inside.controls.none {
                it.name.contains("WITNESS") || it.name.contains("URGE")
            }
        )
    }

    @Test
    fun guardSurfaces_offerNoSpendableExitStockpile() {
        val model = surface()
        // Exits render as NAMES; the valve as a whole/spent fact.
        assertTrue(model.exitNames.isNotEmpty())
        assertEquals(GuardCopy.valveLine(false), model.valveLine)
        assertTrue(GuardControl.values().none { it.name.contains("EXIT") })
    }

    @Test
    fun routinesSettle_underGuardRules_notStandalone() {
        val on = GuardRoutineSnapshot(id = 2, name = "Evening block", enabled = true)
        val row = surface(snapshot(routines = listOf(on))).routineRows.single()
        assertEquals(setOf(GuardControl.ROUTINE_TURN_OFF), row.controls)
        // The routine's off is the Guard's own slow road — the
        // same day-start law the Guard's off stands under.
        assertTrue(GuardCopy.ROUTINES_LAW.contains("next day-start"))
        assertTrue(GuardCopy.OFF_LAW.contains("next day-start"))
    }

    // ---------- G-a: carried unresolved, D-1 applied as filed ----------

    @Test
    fun census_carriesNoTwoSignatureControl_gAStaysRensToRule() {
        val names = GuardControl.values().map { it.name }
        assertTrue(names.none { it.contains("SIGNATURE") || it.contains("SECOND") })
    }
}
