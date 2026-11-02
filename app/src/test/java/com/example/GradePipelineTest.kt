package com.example

import com.example.ui.strongroom.grade.FaceHeightfield
import com.example.ui.strongroom.grade.FinishDepth
import com.example.ui.strongroom.grade.GradeColour
import com.example.ui.strongroom.grade.GradeFieldRenderer
import com.example.ui.strongroom.grade.GradeInk
import com.example.ui.strongroom.grade.GradePipeline
import com.example.ui.strongroom.grade.GradeState
import com.example.ui.strongroom.grade.GradeSurface
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** STAGE 10 PRE-BUILD (BUILD-SR-10): the grade pipeline's law — shaped
 *  value-for-value to P0's filed Relight table. */
class GradePipelineTest {

    // The palette is exactly seven inks, and none of them is green-dominant.
    @Test
    fun paletteIsSevenInksNeverGreen() {
        assertEquals(7, GradeInk.entries.size)
        GradeInk.entries.forEach { ink ->
            assertTrue("green-dominant ink: $ink", !(ink.g > ink.r && ink.g > ink.b && ink.g > 0x99))
        }
        assertEquals(0xFF493EA4, GradeInk.VIOLET.argb)
        assertEquals(0xFFE2BC39, GradeInk.GOLD.argb)
        assertEquals(0xFF34323B, GradeInk.GRAPHITE.argb)
    }

    // P0's filed per-state table, mirrored exactly.
    @Test
    fun stateParamsMirrorP0() {
        val signing = GradePipeline.paramsFor(GradeState.SIGNING)
        assertEquals(-0.83f, signing.lightDirectionX, 0.001f)
        assertEquals(-0.55f, signing.lightDirectionY, 0.001f)
        assertEquals(0, signing.faceDescentEighths)
        assertTrue(signing.warmLitPartner)

        val sealed = GradePipeline.paramsFor(GradeState.SEALED)
        assertEquals(3, sealed.faceDescentEighths)
        assertEquals(0, sealed.plateDescentEighths)
        assertEquals(1, sealed.maxLitEighths)

        val dark = GradePipeline.paramsFor(GradeState.DARK)
        assertTrue(dark.subtractionOnly)
        assertEquals(0, dark.maxLitEighths)
        assertEquals(1, dark.plateDescentEighths)

        val day = GradePipeline.paramsFor(GradeState.DAY)
        assertEquals(-1f, day.lightDirectionY, 0.001f)
        assertEquals(false, day.warmLitPartner)
    }

    // Blends are quantised to whole eighths by construction.
    @Test
    fun blendsAreWholeEighths() {
        val gold = GradeColour.of(GradeInk.GOLD)
        val orange = GradeColour.of(GradeInk.ORANGE)
        assertEquals(gold, GradePipeline.blendEighth(gold, orange, 0))
        assertEquals(orange, GradePipeline.blendEighth(gold, orange, 8))
        assertEquals(orange, GradePipeline.blendEighth(gold, orange, 99))
        val one = GradePipeline.blendEighth(gold, orange, 1)
        assertEquals((gold.r + (orange.r - gold.r) / 8f).toInt().let {
            kotlin.math.round(it.toDouble()).toInt()
        }, one.r)
    }

    // DARK is subtraction only: relief never brightens a mark in DARK.
    @Test
    fun darkReliefNeverBrightens() {
        val violet = GradeColour.of(GradeInk.VIOLET)
        val lit = GradePipeline.relief(violet, violet, 1f, GradeState.DARK)
        assertEquals(violet, lit)
    }

    // SEALED descends the face 3/8 and carries the plate exact.
    @Test
    fun sealedDescendsFaceNotPlate() {
        val violet = GradeColour.of(GradeInk.VIOLET)
        val face = GradePipeline.stateSurface(violet, GradeState.SEALED, GradeSurface.FACE)
        val plate = GradePipeline.stateSurface(violet, GradeState.SEALED, GradeSurface.PLATE)
        assertEquals(GradePipeline.descend(violet, 3), face)
        assertEquals(violet, plate)
    }

    // L-SEALED's two promoted reads, exact.
    @Test
    fun sealedPromotedReadsAreExact() {
        assertEquals(GradeColour.of(GradeInk.GRAPHITE), GradePipeline.sealedHourMark())
        assertEquals(GradeColour.of(GradeInk.GOLD), GradePipeline.sealedPinstripe())
        assertEquals(
            GradePipeline.blendEighth(GradeColour.of(GradeInk.GOLD), GradeColour.of(GradeInk.ORANGE), 1),
            GradePipeline.sealedPinstripeLitLip(),
        )
    }

    // SL-3: one depth per kind; punch depth never varies by kind of hole.
    @Test
    fun finishDepthIsOnePerKind() {
        assertEquals(0.30f, FinishDepth.depthFor(FinishDepth.CutKind.GROOVE), 0.001f)
        assertEquals(0.30f, FinishDepth.depthFor(FinishDepth.CutKind.PUNCH), 0.001f)
        assertEquals(FinishDepth.STRIKE_DEPTH, FinishDepth.depthFor(FinishDepth.CutKind.STRIKE), 0.001f)
    }

    // The heightfield is deterministic and cuts only downward.
    @Test
    fun heightfieldIsDeterministicAndCutOnly() {
        val field = FaceHeightfield()
        val a = field.heightAt(0.5f, 0.08f)
        assertEquals(a, field.heightAt(0.5f, 0.08f), 0.0f)
        assertTrue("pinstripe must cut", a < 0f)
        assertEquals(0f, field.heightAt(0.5f, 0.5f), 0.001f)
    }

    // The reference renderer runs and preserves dimensions.
    @Test
    fun rendererPreservesDimensions() {
        val base = Array(4) { Array(4) { GradeColour.of(GradeInk.VIOLET) } }
        val out = GradeFieldRenderer.render(base, GradeState.SEALED, GradeSurface.FACE)
        assertEquals(4, out.size)
        assertEquals(4, out[0].size)
    }
}
