package com.example

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.strongroom.ClockInks
import com.example.ui.strongroom.ClockRelight
import com.example.ui.strongroom.ClockRelightState
import com.example.ui.strongroom.ClockFrm1
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * P8 — THE CLOCK'S TOKENS AT THE SEAM (ui/strongroom/DeskClockTokens.kt):
 *
 *  - THE PALETTE IS CLOSED: exactly the seven filed inks, by hex,
 *    and never green.
 *  - FRM-1 IS THE FILED STEP: Atkinson 400 at the record's body
 *    size (Stage 5 §1.3 / A-6.7).
 *  - RELIGHT BEHAVES AS FILED: SEALED descends the face three
 *    eighths toward graphite; DARK brightens nothing, ever; every
 *    derivation is an ink moved toward another ink by eighths.
 */
class DeskClockTokensP8Test {

    @Test
    fun palette_isExactlyTheSevenInks() {
        assertEquals(
            listOf(
                Color(0xFFF04FCB), Color(0xFF493EA4), Color(0xFFE2BC39), Color(0xFFC84481),
                Color(0xFFE47E35), Color(0xFF377ED0), Color(0xFF34323B)
            ),
            ClockInks.All
        )
    }

    @Test
    fun palette_isNeverGreen() {
        for (ink in ClockInks.All) {
            assertFalse(
                "a green-dominant ink stands in the palette",
                ink.green > ink.red && ink.green > ink.blue
            )
        }
    }

    @Test
    fun frm1_isTheFiledStep() {
        assertEquals(16.sp, ClockFrm1.fontSize)
        assertEquals(24.sp, ClockFrm1.lineHeight)
        assertEquals(FontWeight.Normal, ClockFrm1.fontWeight)
    }

    @Test
    fun relight_sealedDescendsTheFaceThreeEighths() {
        assertEquals(
            ClockRelight.blendEighth(ClockInks.Violet, ClockInks.Graphite, 3),
            ClockRelight.stateSurface(ClockInks.Violet, ClockRelightState.SEALED)
        )
        // Signing and day descend nothing.
        assertEquals(ClockInks.Violet, ClockRelight.stateSurface(ClockInks.Violet, ClockRelightState.SIGNING))
        assertEquals(ClockInks.Violet, ClockRelight.stateSurface(ClockInks.Violet, ClockRelightState.DAY))
    }

    @Test
    fun relight_darkBrightensNothing() {
        for (ink in ClockInks.All) {
            assertEquals(
                "DARK lifted a mark on its lit side",
                ink,
                ClockRelight.relief(ink, ClockInks.Violet, 1f, ClockRelightState.DARK)
            )
        }
        // And descends harder than SEALED: enamel five eighths.
        assertEquals(
            ClockRelight.blendEighth(ClockInks.Violet, ClockInks.Graphite, 5),
            ClockRelight.stateSurface(ClockInks.Violet, ClockRelightState.DARK)
        )
    }

    @Test
    fun relight_zeroGradientLeavesTheMarkAlone() {
        for (state in ClockRelightState.values()) {
            assertEquals(
                ClockInks.Gold,
                ClockRelight.relief(ClockInks.Gold, ClockInks.Violet, 0f, state)
            )
        }
    }
}
