package com.example

import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * P1 (BUILD-SR-3) — THE COMPOSITION, per the governing filings
 * (ledger B-1/B-2/B-3; refit Floor 1 Beat 1):
 *
 *  - tomorrow stands composed from the WEEK'S STANDING ORDER
 *    (ordinary · test · small · rest) whether or not he signs;
 *  - the signing AMENDS, never creates; the app never checks
 *    whether the evening happened (proven at the store level in
 *    StrongroomMigrationP1Test: an unamended composed day seals);
 *  - signature ≠ swing: the composition carries no seal; the seal
 *    is struck separately, at the first sitting;
 *  - the task line is his own words on the composition, spent when
 *    the sealing lifts (carried onto the plate);
 *  - the purpose sentence stands verbatim, twenty-three words.
 */
class StrongroomCompositionP1Test {

    private val ordinary = StrongroomComposition.DayTemplate(
        lockedPackages = setOf("com.video.app"),
        liftingTime = LocalTime.of(6, 0),
        breakLengthMinutes = 20,
        breakFiresAtOffsetMillis = 7_200_000L,
        allowanceMinutes = 30,
        gaps = listOf(StrongroomDay.ShapedGap(StrongroomDay.GapKind.GYM, 7_200_000L, 3_600_000L)),
        taskLine = null
    )
    private val rest = ordinary.copy(
        lockedPackages = emptySet(),
        allowanceMinutes = 60,
        gaps = emptyList()
    )
    private val order = StrongroomComposition.WeekStandingOrder(
        shapeByWeekday = mapOf(
            DayOfWeek.MONDAY to StrongroomComposition.DayShape.ORDINARY,
            DayOfWeek.TUESDAY to StrongroomComposition.DayShape.ORDINARY,
            DayOfWeek.WEDNESDAY to StrongroomComposition.DayShape.TEST,
            DayOfWeek.THURSDAY to StrongroomComposition.DayShape.ORDINARY,
            DayOfWeek.FRIDAY to StrongroomComposition.DayShape.SMALL,
            DayOfWeek.SATURDAY to StrongroomComposition.DayShape.ORDINARY,
            DayOfWeek.SUNDAY to StrongroomComposition.DayShape.REST
        ),
        templateByShape = mapOf(
            StrongroomComposition.DayShape.ORDINARY to ordinary,
            StrongroomComposition.DayShape.TEST to ordinary.copy(taskLine = "test day — hold the line"),
            StrongroomComposition.DayShape.SMALL to ordinary.copy(allowanceMinutes = 15),
            StrongroomComposition.DayShape.REST to rest
        )
    )

    @Test
    fun composition_standsFromTheStandingOrder_whetherOrNotHeSigns() {
        // 2026-11-03 is a Tuesday (ordinary); 2026-11-08 a Sunday (rest).
        val tuesday = StrongroomComposition.compose(order, "2026-11-03")
        assertEquals(StrongroomComposition.DayShape.ORDINARY, tuesday.shape)
        assertFalse(tuesday.amendedBySigning)
        assertEquals(setOf("com.video.app"), tuesday.record.lockedPackages)
        assertNull(tuesday.record.sentence) // the kept place stands until a signing speaks
        val sunday = StrongroomComposition.compose(order, "2026-11-08")
        assertEquals(StrongroomComposition.DayShape.REST, sunday.shape)
        assertEquals(emptySet<String>(), sunday.record.lockedPackages)
        assertEquals(60, sunday.record.allowanceMinutes)
    }

    @Test
    fun signing_amendsOnlyWhatItNames_andNeverCreates() {
        val composed = StrongroomComposition.compose(order, "2026-11-03")
        val amended = StrongroomComposition.amend(
            composed,
            StrongroomComposition.SigningAmendment(
                sentence = "hold through the morning",
                taskLine = "finish the physics sheet"
            )
        )
        assertTrue(amended.amendedBySigning)
        assertEquals("hold through the morning", amended.record.sentence)
        assertEquals("finish the physics sheet", amended.record.taskLine)
        // Everything unnamed stands exactly as composed.
        assertEquals(composed.record.lockedPackages, amended.record.lockedPackages)
        assertEquals(composed.record.liftingTime, amended.record.liftingTime)
        assertEquals(composed.record.breakLengthMinutes, amended.record.breakLengthMinutes)
        assertEquals(composed.record.gaps, amended.record.gaps)
        // The unamended original is untouched — amendment makes a new day record.
        assertNull(composed.record.sentence)
        assertFalse(composed.amendedBySigning)
    }

    @Test
    fun taskLine_ridesTheComposition_andIsSpentOnThePlate() {
        // Wednesday is the TEST shape, whose template carries a line.
        val composed = StrongroomComposition.compose(order, "2026-11-04")
        assertEquals("test day — hold the line", composed.record.taskLine)
        val seal = StrongroomDay.SealSpan(100_000L, 3, 3_600_000L)
        val plate = StrongroomDay.strikePlate(
            composed.record, seal, emptyList(),
            StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 1L),
            nowElapsedMillis = seal.liftingAtElapsedMillis, nowBootCount = 3, sealFailed = false
        )
        assertEquals("test day — hold the line", plate.taskLine)
    }

    @Test
    fun purposeSentence_standsVerbatim_twentyThreeWords_onEveryRecord() {
        assertEquals(
            "What I settle while I am strong, this holds while I am not. " +
                "It reopens nothing. At night it tells me the truth.",
            StrongroomDay.PURPOSE_SENTENCE
        )
        assertEquals(23, StrongroomDay.PURPOSE_SENTENCE.split(" ").size)
        // KEPT, BOUND (BUILD-SR-23, step 7, named cure): the
        // domain's PURPOSE_SENTENCE is the record's carried-content
        // default — data, not display copy — and the domain layer
        // cannot read Android resources, so the constant stays;
        // this assertion binds it to the single copy source so
        // any drift between the carried default and the deck's
        // purpose line fails here, by name.
        assertEquals(
            deckString("copy_1_5_purpose"),
            StrongroomDay.PURPOSE_SENTENCE
        )
        val composed = StrongroomComposition.compose(order, "2026-11-03")
        assertEquals(StrongroomDay.PURPOSE_SENTENCE, composed.record.purposeSentence)
    }

    @Test
    fun standingOrder_incomplete_cannotCompose() {
        val broken = runCatching {
            StrongroomComposition.WeekStandingOrder(
                shapeByWeekday = mapOf(DayOfWeek.MONDAY to StrongroomComposition.DayShape.ORDINARY),
                templateByShape = mapOf(StrongroomComposition.DayShape.ORDINARY to ordinary)
            )
        }
        assertTrue(broken.isFailure)
    }
}

// The single copy source, read as the file the app ships (BUILD-SR-23 binding).
private fun deckString(key: String): String {
    val xml = java.io.File("src/main/res/values/strongroom_strings.xml").readText()
    val match = Regex("<string name=\"" + key + "\">(.*?)</string>", RegexOption.DOT_MATCHES_ALL)
        .find(xml) ?: error("deck key missing from the single source: $key")
    return match.groupValues[1]
}
