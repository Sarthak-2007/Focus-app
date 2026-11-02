package com.example

import com.example.data.StrongroomCrankWeekEntity
import com.example.data.StrongroomEncoding
import com.example.data.StrongroomGuardEntity
import com.example.data.StrongroomPunchEntity
import com.example.data.toCrankWeek
import com.example.data.toEntity
import com.example.data.toGuardState
import com.example.data.toPunch
import com.example.data.toSigningRecord
import com.example.domain.StrongroomDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

/**
 * P1 (BUILD-SR-3) — StrongroomModels: the persistence shapes of
 * the Strongroom day. Encodings round-trip exactly (package names
 * and gap rows), entity↔domain mappers lose nothing the model
 * carries, and a corrupt punch kind fails safe to the FLOOR grade
 * rather than crashing a plate render.
 */
class StrongroomModelsP1Test {

    @Test
    fun packages_roundTrip_sortedAndExact() {
        val packages = setOf("com.zeta.app", "com.alpha.app", "com.mid.app")
        val encoded = StrongroomEncoding.encodePackages(packages)
        assertEquals("com.alpha.app,com.mid.app,com.zeta.app", encoded)
        assertEquals(packages, StrongroomEncoding.decodePackages(encoded))
        assertEquals(emptySet<String>(), StrongroomEncoding.decodePackages(""))
    }

    @Test
    fun gaps_roundTrip_andCorruptRowsDropNotCrash() {
        val gaps = listOf(
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.GYM, 100L, 200L),
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.MEAL, 500L, 300L)
        )
        assertEquals(gaps, StrongroomEncoding.decodeGaps(StrongroomEncoding.encodeGaps(gaps)))
        assertEquals(emptyList<StrongroomDay.ShapedGap>(), StrongroomEncoding.decodeGaps(""))
        // A torn row (unknown kind / bad numbers) drops alone; the
        // lawful rows around it survive.
        val decoded = StrongroomEncoding.decodeGaps("GYM|1|2;NOPE|1|2;MEAL|x|2;SADHANA|3|4")
        assertEquals(
            listOf(
                StrongroomDay.ShapedGap(StrongroomDay.GapKind.GYM, 1L, 2L),
                StrongroomDay.ShapedGap(StrongroomDay.GapKind.SADHANA, 3L, 4L)
            ),
            decoded
        )
    }

    @Test
    fun signingRecord_entityRoundTrip_losesNothing() {
        val record = StrongroomDay.SigningRecord(
            filingDateIso = "2026-11-03",
            lockedPackages = setOf("com.video.app"),
            liftingTime = LocalTime.of(6, 30),
            sentence = null,
            breakLengthMinutes = 25,
            breakFiresAtOffsetMillis = 3_600_000L,
            allowanceMinutes = 45,
            gaps = listOf(StrongroomDay.ShapedGap(StrongroomDay.GapKind.SADHANA, 10L, 20L)),
            taskLine = "his own words"
        )
        val entity = record.toEntity(dayShape = "TEST", amendedBySigning = true)
        assertEquals("TEST", entity.dayShape)
        assertTrue(entity.amendedBySigning)
        val back = entity.toSigningRecord()
        assertEquals(record.lockedPackages, back.lockedPackages)
        assertEquals(record.liftingTime, back.liftingTime)
        assertEquals(record.breakLengthMinutes, back.breakLengthMinutes)
        assertEquals(record.breakFiresAtOffsetMillis, back.breakFiresAtOffsetMillis)
        assertEquals(record.allowanceMinutes, back.allowanceMinutes)
        assertEquals(record.gaps, back.gaps)
        assertEquals("his own words", back.taskLine)
        assertNull(back.sentence)
    }

    @Test
    fun punchMapper_corruptKindFailsSafeToFloor() {
        val punch = StrongroomPunchEntity(
            filingDateIso = "2026-11-03", kind = "GARBAGE", atOffsetMillis = 5L
        ).toPunch()
        assertEquals(StrongroomDay.PunchKind.FLOOR, punch.kind)
        val reach = StrongroomPunchEntity(
            filingDateIso = "2026-11-03", kind = "REACH", atOffsetMillis = 5L,
            packageName = "com.video.app"
        ).toPunch()
        assertEquals(StrongroomDay.PunchKind.REACH, reach.kind)
        assertEquals("com.video.app", reach.packageName)
    }

    @Test
    fun guardAndCrankMappers_carryEveryField() {
        val state = StrongroomGuardEntity(
            guardOn = false, allowanceMinutes = 40,
            pendingAllowanceMinutes = 55, pendingAllowanceFromMillis = 99L,
            pendingOffFromMillis = 0L, stretchEndMillis = 123L
        ).toGuardState(spentDateIso = "2026-11-03", spentMinutesThatDay = 12)
        assertEquals(40, state.allowanceMinutes)
        assertEquals(55, state.pendingAllowanceMinutes)
        assertEquals(123L, state.stretchEndMillis)
        assertEquals(12, state.spentMinutesThatDay)
        val back = state.toEntity()
        assertEquals(40, back.allowanceMinutes)
        assertEquals(123L, back.stretchEndMillis)

        val week = StrongroomCrankWeekEntity(
            weekAnchorMillis = 7L, spentSeconds = 60, sealBroken = true,
            spentAtWallMillis = 8L, carriedFromLegacySpend = true
        ).toCrankWeek()
        assertEquals(7L, week.weekAnchorMillis)
        assertTrue(week.sealBroken)
        assertEquals(8L, week.spentAtWallMillis)
    }
}
