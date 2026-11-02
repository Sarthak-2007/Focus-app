package com.example

import com.example.domain.StrongroomDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.lang.reflect.Modifier
import java.time.LocalTime

/**
 * A-5 / B-14 — THE PLATE IS THE KNOCK'S ONLY TRACE, AND THE
 * PLATE CARRIES NO DIGITS. Landing: engine tree, with P1
 * (domain/StrongroomDay.kt — Punch · DayPlate · strikePlate).
 *
 * The knock half of this law stands in StrongroomKnockTraceTest
 * (faces tree): the knock keeps no count in the room. This half
 * proves where the trace lawfully lives, and what it may hold:
 *
 *  1. THE TRACE IS EXACT — every reach struck during the day
 *     stands on the plate at lifting, in time order, by kind,
 *     neither dropped, duplicated, nor re-kinded. The plate is
 *     struck from the day's punches and from nothing else.
 *  2. NO DIGITS CAN STAND ON IT — the punch's and the plate's
 *     field censuses are closed: a punch is a kind, an offset,
 *     and (for a reach) the package reached for. There is no
 *     field a code's digits could be written into — B-18's
 *     "the plate strikes the SHOWING, never a digit" holds by
 *     construction on the plate side, whatever P5 builds.
 *
 * Named limit: B-18's showing has no punch kind of its own yet
 * (P1 and P6 both carry REACH · HELD · FLOOR only) — GAP-T3 on
 * TEST-SR-2's map, a decider item for the coordinator. This
 * test deliberately does NOT strike a showing as a REACH:
 * REACH is a locked app's reach, and a showing struck as one
 * would falsify the plate this test exists to keep honest.
 */
class DayPlateTraceTest {

    private val signing = StrongroomDay.SigningRecord(
        filingDateIso = "2026-11-03",
        lockedPackages = setOf("com.video.app", "com.social.app"),
        liftingTime = LocalTime.of(6, 0),
        sentence = null,
        breakLengthMinutes = 30,
        breakFiresAtOffsetMillis = 3_600_000L,
        allowanceMinutes = 30,
        gaps = emptyList()
    )
    private val seal = StrongroomDay.SealSpan(
        sealedAtElapsedMillis = 100_000L,
        sealedBootCount = 3,
        spanDurationMillis = 10_800_000L // 3h
    )
    private val crank = StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 1L)

    private fun strike(punches: List<StrongroomDay.Punch>): StrongroomDay.DayPlate =
        StrongroomDay.strikePlate(
            signing, seal, punches, crank,
            nowElapsedMillis = seal.liftingAtElapsedMillis,
            nowBootCount = 3, sealFailed = false
        )

    @Test
    fun reaches_areStruckExactly_inTimeOrder_byKind() {
        val reaches = listOf(
            StrongroomDay.Punch(StrongroomDay.PunchKind.REACH, 900_000L, "com.video.app"),
            StrongroomDay.Punch(StrongroomDay.PunchKind.REACH, 300_000L, "com.social.app"),
            StrongroomDay.Punch(StrongroomDay.PunchKind.REACH, 600_000L, "com.video.app")
        )
        val held = StrongroomDay.Punch(StrongroomDay.PunchKind.HELD, 450_000L)
        val floor = StrongroomDay.Punch(StrongroomDay.PunchKind.FLOOR, 750_000L)
        // The day hands its punches over unordered, as a day does.
        val plate = strike(listOf(reaches[0], held, reaches[2], floor, reaches[1]))

        assertEquals(StrongroomDay.SealState.LIFTED, plate.sealState)
        // Every punch stands, exactly once, in time order.
        assertEquals(
            listOf(300_000L, 450_000L, 600_000L, 750_000L, 900_000L),
            plate.punches.map { it.atOffsetMillis }
        )
        // By kind: the three reaches, in their order, packages intact.
        assertEquals(
            listOf("com.social.app", "com.video.app", "com.video.app"),
            plate.punchesOf(StrongroomDay.PunchKind.REACH).map { it.packageName }
        )
        assertEquals(1, plate.punchesOf(StrongroomDay.PunchKind.HELD).size)
        assertEquals(1, plate.punchesOf(StrongroomDay.PunchKind.FLOOR).size)
        // A day with no reaches strikes a plate with no holes —
        // the trace is the day's, never a quota.
        assertEquals(0, strike(emptyList()).punches.size)
    }

    @Test
    fun punchAndPlate_carryNoFieldADigitCouldStandIn() {
        fun instanceFields(type: Class<*>): List<String> =
            type.declaredFields
                .filter {
                    !it.isSynthetic &&
                        !Modifier.isStatic(it.modifiers) &&
                        !it.name.startsWith("$")
                }
                .map { it.name }
                .sorted()

        assertEquals(
            listOf("atOffsetMillis", "kind", "packageName"),
            instanceFields(StrongroomDay.Punch::class.java)
        )
        assertEquals(
            listOf(
                "bracketGapMillis", "crankSealBroken", "filingDateIso",
                "hoursHeldMillis", "punches", "sealState", "taskLine"
            ),
            instanceFields(StrongroomDay.DayPlate::class.java)
        )
        val forbidden = listOf(
            "digit", "code", "otp", "score", "total", "grade", "streak"
        )
        (instanceFields(StrongroomDay.Punch::class.java) +
            instanceFields(StrongroomDay.DayPlate::class.java)).forEach { name ->
            forbidden.forEach { token ->
                assertFalse(
                    "'$name' must never carry '$token' substance on the plate",
                    name.contains(token, ignoreCase = true)
                )
            }
        }
    }
}
