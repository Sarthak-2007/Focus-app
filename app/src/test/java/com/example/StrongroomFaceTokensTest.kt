package com.example

import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.FaceDoorWordStyle
import com.example.ui.theme.FaceHeldMarkStyle
import com.example.ui.theme.FaceHourStyle
import com.example.ui.theme.FaceSentenceStyle
import com.example.ui.theme.InkBlue
import com.example.ui.theme.InkEmber
import com.example.ui.theme.InkGold
import com.example.ui.theme.InkLine
import com.example.ui.theme.InkPink
import com.example.ui.theme.InkRose
import com.example.ui.theme.InkViolet
import com.example.ui.theme.SoraFamily
import com.example.ui.theme.StrongroomGold
import com.example.ui.theme.StrongroomGround
import com.example.ui.theme.StrongroomInks
import com.example.ui.theme.StrongroomLineWork
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * THE STRONGROOM TOKENS' contract (ui/theme/StrongroomTokens.kt):
 *
 *  - The palette is CLOSED at Audit A §4's seven inks, at their
 *    exact hexes — no eighth colour, no drifted value.
 *  - NEVER GREEN: in every ink, the green channel is beaten by
 *    red or blue — green never carries a colour here.
 *  - The kept ground & finish name the inks they are: violet
 *    ground, gold leaf/pinstripe, dark line-work.
 *  - The face's type is Stage 5 §1.2's printed scale at its filed
 *    steps/weights/tracking: PRT-5 hour (Anonymous Pro 700, 48,
 *    +0.02em) · PRT-3 door-word (Sora 700, 24, +0.05em) · PRT-4
 *    sentence (Sora 600, 32, +0.04em) · PRT-2 held mark (Sora
 *    600, 18, +0.06em) — tracking never negative (§5.3).
 */
class StrongroomFaceTokensTest {

    private fun hex(color: androidx.compose.ui.graphics.Color): String =
        "#%06X".format(color.toArgb() and 0xFFFFFF)

    @Test
    fun palette_isExactlyTheSevenFiledInks() {
        assertEquals(7, StrongroomInks.size)
        assertEquals(
            listOf("#F04FCB", "#493EA4", "#E2BC39", "#C84481", "#E47E35", "#377ED0", "#34323B"),
            StrongroomInks.map { hex(it) }
        )
        assertEquals("#F04FCB", hex(InkPink))
        assertEquals("#493EA4", hex(InkViolet))
        assertEquals("#E2BC39", hex(InkGold))
        assertEquals("#C84481", hex(InkRose))
        assertEquals("#E47E35", hex(InkEmber))
        assertEquals("#377ED0", hex(InkBlue))
        assertEquals("#34323B", hex(InkLine))
    }

    @Test
    fun palette_neverGreen() {
        StrongroomInks.forEach { c ->
            val g = (c.toArgb() shr 8) and 0xFF
            val r = (c.toArgb() shr 16) and 0xFF
            val b = c.toArgb() and 0xFF
            assertTrue(
                "green must never dominate: ${hex(c)}",
                g < maxOf(r, b)
            )
        }
    }

    @Test
    fun groundAndFinish_areTheKeptInks() {
        assertEquals(InkViolet, StrongroomGround)
        assertEquals(InkGold, StrongroomGold)
        assertEquals(InkLine, StrongroomLineWork)
    }

    @Test
    fun faceType_carriesTheFiledStepsWeightsAndTracking() {
        // Rank 1 — the hour.
        assertEquals(AnonymousProFamily, FaceHourStyle.fontFamily)
        assertEquals(FontWeight.Bold, FaceHourStyle.fontWeight)
        assertEquals(48.sp, FaceHourStyle.fontSize)
        assertEquals(50.sp, FaceHourStyle.lineHeight)
        // Rank 2 — the door-word slot.
        assertEquals(SoraFamily, FaceDoorWordStyle.fontFamily)
        assertEquals(FontWeight.Bold, FaceDoorWordStyle.fontWeight)
        assertEquals(24.sp, FaceDoorWordStyle.fontSize)
        // Rank 3 — the sentence.
        assertEquals(SoraFamily, FaceSentenceStyle.fontFamily)
        assertEquals(FontWeight.SemiBold, FaceSentenceStyle.fontWeight)
        assertEquals(32.sp, FaceSentenceStyle.fontSize)
        assertEquals(40.sp, FaceSentenceStyle.lineHeight)
        // Rank 4 — the held-mark slot, the printed floor.
        assertEquals(SoraFamily, FaceHeldMarkStyle.fontFamily)
        assertEquals(FontWeight.SemiBold, FaceHeldMarkStyle.fontWeight)
        assertEquals(18.sp, FaceHeldMarkStyle.fontSize)
        // §5.3: printed tracking is set after-spread, never negative,
        // and each style's tracking is its filed em × its size.
        listOf(
            FaceHourStyle to 0.02f,
            FaceDoorWordStyle to 0.05f,
            FaceSentenceStyle to 0.04f,
            FaceHeldMarkStyle to 0.06f
        ).forEach { (style, em) ->
            assertTrue(style.letterSpacing.value > 0f)
            assertEquals(
                style.fontSize.value * em,
                style.letterSpacing.value,
                0.01f
            )
        }
    }
}
