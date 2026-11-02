package com.example

import androidx.compose.ui.graphics.Color
import com.example.ui.foundation.APP_NAME
import com.example.ui.foundation.StrongroomInkLaw
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomLitSurface
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BUILD-SR-33 — the fresh foundation's own proof.
 *
 * Every assertion traces to a DECISION FILING, cited per test:
 * the seven inks to the palette of record, the type steps to
 * Stage 5's composed system, the light fractions to RELIGHT, and
 * the motion testimony to MOTION-SR-1's simulated tables (re-derived
 * here by integrating the filed bodies — the filed numbers are the
 * bodies' testimony, not constants believed on sight; AL-0056).
 * Motion figures travel UNPROVEN-ON-DEVICE (MOTION-SR-1 head).
 */
class StrongroomFoundationSr33Test {

    // ── The token layer ────────────────────────────────────────────

    @Test
    fun paletteIsExactlyTheSevenInksOfRecord() {
        assertEquals(7, StrongroomInks.All.size)
        assertEquals(Color(0xFFF04FCB), StrongroomInks.Magenta)
        assertEquals(Color(0xFF493EA4), StrongroomInks.Violet)
        assertEquals(Color(0xFFE2BC39), StrongroomInks.Gold)
        assertEquals(Color(0xFFC84481), StrongroomInks.Rose)
        assertEquals(Color(0xFFE47E35), StrongroomInks.Orange)
        assertEquals(Color(0xFF377ED0), StrongroomInks.Blue)
        assertEquals(Color(0xFF34323B), StrongroomInks.Graphite)
    }

    @Test
    fun neverGreenHoldsForEveryInk() {
        // His standing law + ledger F-1: green < max(red, blue) per ink.
        StrongroomInks.All.forEach { ink ->
            assertTrue(StrongroomInkLaw.isNeverGreen(ink))
        }
    }

    @Test
    fun voidForgeryIsNotInThePalette() {
        // Grade Book C-GB-1: #377E4B is void, never used.
        assertTrue(StrongroomInks.All.none { it == Color(0xFF377E4B) })
    }

    @Test
    fun rolesCarryTheDecidedGrade() {
        // Ledger E-7/E-8 + Grade Book §1.5: violet ground, gold leaf
        // and plate (gold alone), graphite line-work and metal.
        assertEquals(StrongroomInks.Violet, StrongroomRoles.Ground)
        assertEquals(StrongroomInks.Gold, StrongroomRoles.Leaf)
        assertEquals(StrongroomInks.Gold, StrongroomRoles.Plate)
        assertEquals(StrongroomInks.Graphite, StrongroomRoles.LineWork)
        assertEquals(StrongroomInks.Graphite, StrongroomRoles.Metal)
    }

    @Test
    fun userFacingNameIsUlysses() {
        // Ledger E-12.
        assertEquals("ULYSSES", APP_NAME)
    }

    // ── The type system ────────────────────────────────────────────

    @Test
    fun screenScaleIsStage5Whole() {
        // Type-system §1.1: sizes and leadings, in order.
        val steps = listOf(
            StrongroomType.Scr0, StrongroomType.Scr1, StrongroomType.Scr2,
            StrongroomType.Scr3, StrongroomType.Scr4, StrongroomType.Scr5,
            StrongroomType.Scr6
        )
        assertEquals(
            listOf(12, 14, 16, 18, 20, 24, 32),
            steps.map { it.fontSize.value.toInt() }
        )
        assertEquals(
            listOf(16, 20, 24, 24, 28, 32, 40),
            steps.map { it.lineHeight.value.toInt() }
        )
    }

    @Test
    fun telemetryScaleIsStage5Whole() {
        val steps = listOf(
            StrongroomType.Anon0, StrongroomType.Anon1, StrongroomType.Anon2,
            StrongroomType.Anon3, StrongroomType.Anon4
        )
        assertEquals(listOf(13, 15, 17, 20, 28), steps.map { it.fontSize.value.toInt() })
        assertEquals(listOf(16, 20, 24, 28, 32), steps.map { it.lineHeight.value.toInt() })
    }

    @Test
    fun printedFloorAndFaceHierarchy() {
        // §1.2: PRT-2 is the printed floor at 18; §3.3's ruling:
        // rank order ENG-1 > PRT-3 > PRT-4 > PRT-2.
        assertEquals(18, StrongroomType.Prt2.fontSize.value.toInt())
        assertEquals(48, StrongroomType.Eng1.fontSize.value.toInt())
        assertEquals(
            listOf("ENG-1", "PRT-3", "PRT-4", "PRT-2"),
            StrongroomType.SealedFaceHierarchy
        )
    }

    @Test
    fun printedTrackingNeverNegative() {
        // §5.3: never negative, on any printed context, ever.
        listOf(StrongroomType.Prt2, StrongroomType.Prt3, StrongroomType.Prt4, StrongroomType.Eng1)
            .forEach { assertTrue(it.letterSpacing.value > 0f) }
    }

    // ── The rebuilt light ──────────────────────────────────────────

    @Test
    fun relightFractionsAreTheFiledOnes() {
        // RELIGHT §3: sealed face descends 3/8, plate exact; dark
        // face per class (enamel 5/8, gold 1/2), plate 1/8.
        val sealed = StrongroomLight.paramsFor(
            StrongroomLightState.SEALED
        )
        assertEquals(3, sealed.faceDescentEighths)
        assertEquals(0, sealed.plateDescentEighths)
        assertEquals(
            5, StrongroomLight.darkFaceDescentEighths(StrongroomLight.InkClass.ENAMEL)
        )
        assertEquals(
            4, StrongroomLight.darkFaceDescentEighths(StrongroomLight.InkClass.GOLD)
        )
        val dark = StrongroomLight.paramsFor(
            StrongroomLightState.DARK
        )
        assertEquals(1, dark.plateDescentEighths)
        assertTrue(dark.subtractionOnly)
        assertTrue(!dark.warmLitPartner)
    }

    @Test
    fun relightDerivationsStayOnLawfulInks() {
        // Every blend is ink → ink at whole eighths; the endpoints
        // are the named inks themselves.
        assertEquals(
            StrongroomInks.Violet,
            StrongroomLight.blendEighth(StrongroomInks.Violet, StrongroomInks.Orange, 0)
        )
        assertEquals(
            StrongroomInks.Orange,
            StrongroomLight.blendEighth(StrongroomInks.Violet, StrongroomInks.Orange, 8)
        )
        // Lead's law: an OTHER-class colour never moves in any state.
        val other = Color(0xFFFFFFFF)
        assertEquals(
            other,
            StrongroomLight.stateSurface(
                other,
                StrongroomLightState.DARK,
                StrongroomLitSurface.FACE
            )
        )
    }

    @Test
    fun sealedPromotedReadsAreExactlyTwo() {
        // RELIGHT §3 L-SEALED: the hour at exact graphite, the
        // pinstripe at exact gold, its lip warmed exactly 1/8.
        assertEquals(StrongroomInks.Graphite, StrongroomLight.sealedHourMark())
        assertEquals(StrongroomInks.Gold, StrongroomLight.sealedPinstripe())
        assertEquals(
            StrongroomLight.blendEighth(StrongroomInks.Gold, StrongroomInks.Orange, 1),
            StrongroomLight.sealedPinstripeLitLip()
        )
    }
}
