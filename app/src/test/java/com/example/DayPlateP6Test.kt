package com.example

import androidx.compose.ui.graphics.Color
import com.example.domain.LiftingPlate
import com.example.ui.strongroom.DayPlateView
import com.example.ui.strongroom.LiftingScreen
import com.example.ui.strongroom.PlatePileScreen
import com.example.ui.strongroom.PlateRoomScreens
import com.example.ui.strongroom.StrongroomDrawerScreen
import com.example.ui.strongroom.heldFigures
import com.example.ui.theme.PlateInks
import com.example.ui.theme.PlateTokens
import com.example.ui.theme.punchDiameter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P6 — THE PLATE'S RENDERING FIGURES + THE NO-SCORE LAW
 * (ui/theme/PlateTokens.kt, ui/strongroom/DayPlateView.kt):
 *
 *  - E-9/F-1: the plate renders inside the seven inks ONLY —
 *    the token census is exactly the filed palette, never green.
 *  - E-9: the graded holes descend strictly (a finger could
 *    learn them): LARGE > MEDIUM > SMALL, at the filed kinds.
 *  - Hours held render as struck figures h:mm — the plate's
 *    truth, in its own hand.
 *  - Audit A §6 / CRITIC U-6: NO SCORE IN DISGUISE — the struck
 *    plate, the pile, and the drawer expose no member whose name
 *    could carry a total, score, grade, streak, percentage, or
 *    sum. Checked by reflection over the types themselves, so a
 *    future field added in a quiet commit fails this test by
 *    name rather than shipping a tracker.
 */
class DayPlateP6Test {

    @Test
    fun palette_isExactlyTheSevenFiledInks_neverGreen() {
        assertEquals(
            listOf(
                Color(0xFFF04FCB), Color(0xFF493EA4), Color(0xFFE2BC39),
                Color(0xFFC84481), Color(0xFFE47E35), Color(0xFF377ED0),
                Color(0xFF34323B)
            ),
            PlateInks
        )
        for (ink in PlateInks) {
            // No ink is green-dominant (the palette law's plain form).
            assertFalse(
                "ink $ink reads green",
                ink.green > ink.red && ink.green > ink.blue && ink.green > 0.5f
            )
        }
    }

    @Test
    fun punchDiameters_descendStrictlyByGrade() {
        val large = punchDiameter(LiftingPlate.PunchGrade.LARGE)
        val medium = punchDiameter(LiftingPlate.PunchGrade.MEDIUM)
        val small = punchDiameter(LiftingPlate.PunchGrade.SMALL)
        assertTrue(large > medium && medium > small)
        // RE-AIMED at the end phase (BUILD-SR-24) to the ruled
        // figures: Slice 1's 20/13/8 dp are RETIRED — DESIGNER-
        // SR-4's corrected ladder governs (REACH Ø 2.8 mm ·
        // HELD Ø 2.0 mm · FLOOR Ø 1.5 mm at 1 mm ≈ 6.3 dp),
        // the same figures LayoutGradeWiringTest pins at the
        // grade wire. The code does not go back.
        assertEquals(17.64f, large.value, 0.01f)
        assertEquals(12.6f, medium.value, 0.01f)
        assertEquals(9.45f, small.value, 0.01f)
    }

    @Test
    fun heldFigures_renderHoursHeldAsStruckFigures() {
        assertEquals("0:00", heldFigures(0L))
        assertEquals("7:20", heldFigures(7L * 3_600_000L + 20L * 60_000L))
        assertEquals("12:05", heldFigures(12L * 3_600_000L + 5L * 60_000L + 59_000L))
    }

    @Test
    fun noScoreInDisguise_platePileDrawerCarryNoJudgingMember() {
        val banned = listOf("total", "score", "grade", "streak", "percent", "sum", "average", "count")
        val types = listOf(
            LiftingPlate.StruckPlate::class.java,
            LiftingPlate.PlateTruth::class.java,
            LiftingPlate.PlatePile::class.java,
            LiftingPlate.TaskDrawer::class.java,
            LiftingPlate.DrawerTask::class.java
        )
        for (type in types) {
            val names = type.declaredFields.map { it.name } + type.declaredMethods.map { it.name }
            for (name in names) {
                for (word in banned) {
                    assertFalse(
                        "${type.simpleName}.$name could carry a $word — the plate/drawer judge nothing",
                        name.lowercase().contains(word)
                    )
                }
            }
        }
    }

    @Test
    fun roomScreens_standInTheRoomTray_andTokenCensusIsThePalette() {
        // B-16 as a standing value on the surfaces' own contract.
        assertEquals(LiftingPlate.PilePlace.ROOM_TRAY, PlateRoomScreens.pilePlace)
        assertEquals(PlateInks, PlateTokens.inks)
        assertEquals(
            punchDiameter(LiftingPlate.PunchGrade.LARGE),
            PlateTokens.punchDiameter(LiftingPlate.PunchGrade.LARGE)
        )
        // The surfaces exist as the piece's named whole — their
        // file classes load (the rendering itself is venue/device
        // judged, never asserted from a unit test).
        for (fileClass in listOf(
            "com.example.ui.strongroom.DayPlateViewKt",
            "com.example.ui.strongroom.LiftingScreenKt",
            "com.example.ui.strongroom.PlateRoomScreensKt"
        )) {
            assertTrue(fileClass, Class.forName(fileClass) != null)
        }
    }

    @Test
    fun liftVoice_ascendsAndTheDoorSpeaksOnce() {
        val voices = com.example.service.StrongroomLift.BOLT_VOICES
        assertEquals(LiftingPlate.LiftingScore.BOLTS.map { it.atMs }, voices.map { it.atMs })
        assertTrue(voices.zipWithNext().all { (a, b) -> a.pitchHz < b.pitchHz && a.gain < b.gain })
        assertTrue(com.example.service.StrongroomLift.DOOR_GAIN < voices.last().gain)
        assertTrue(com.example.service.StrongroomLift.BOLT_HAPTIC_AMPLITUDES.drop(1).zipWithNext().all { (a, b) -> a > b })
    }
}
