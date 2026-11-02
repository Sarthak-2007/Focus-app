package com.example

import com.example.ui.strongroom.grade.GradeColour
import com.example.ui.strongroom.grade.GradeInk
import com.example.ui.strongroom.grade.GradeState
import com.example.ui.strongroom.grade.RelightShader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** STAGE 10 PRE-BUILD (BUILD-SR-10): the AGSL source is pinned to the
 *  pipeline law's load-bearing terms so shader and reference renderer
 *  cannot drift silently. */
class RelightShaderTest {

    @Test
    fun shaderDeclaresTheWholeUniformContract() {
        RelightShader.UNIFORMS.forEach { name ->
            assertTrue("uniform missing from AGSL: $name", RelightShader.AGSL_SOURCE.contains(name))
        }
        assertTrue(RelightShader.AGSL_SOURCE.contains("uniform shader content"))
    }

    // Quantisation is load-bearing: whole eighths, struck by floor(+0.5).
    @Test
    fun shaderQuantisesToWholeEighths() {
        assertTrue(RelightShader.AGSL_SOURCE.contains("floor(g * uLitCapEighths + 0.5)"))
        assertTrue(RelightShader.AGSL_SOURCE.contains("floor(-g * 8.0 + 0.5)"))
        assertTrue(RelightShader.AGSL_SOURCE.contains("/ 8.0"))
    }

    // The shader names NO colour literal of its own — partners arrive
    // only as uniforms struck from named inks. In particular: no white
    // (the faces lane's fork), no green.
    @Test
    fun shaderNamesNoColourOfItsOwn() {
        assertFalse(RelightShader.AGSL_SOURCE.contains("1.0, 1.0, 1.0"))
        assertFalse(RelightShader.AGSL_SOURCE.lowercase().contains("white"))
        assertFalse(RelightShader.AGSL_SOURCE.lowercase().contains("green"))
    }

    // DARK's subtraction-only law reaches the shader as a uniform gate.
    @Test
    fun darkUniformsGateTheLitSide() {
        val uniforms = RelightShader.uniformsFor(
            GradeState.DARK,
            GradeColour.of(GradeInk.ORANGE),
            GradeColour.of(GradeInk.GRAPHITE),
            texelX = 0.01f, texelY = 0.01f,
        )
        assertEquals(1f, uniforms.getValue("uSubtractionOnly")[0], 0.001f)
        assertEquals(0f, uniforms.getValue("uLitCapEighths")[0], 0.001f)
        assertEquals(RelightShader.UNIFORMS.toSet(), uniforms.keys)
    }

    @Test
    fun signingUniformsCarryTheRakingLight() {
        val uniforms = RelightShader.uniformsFor(
            GradeState.SIGNING,
            GradeColour.of(GradeInk.ORANGE),
            GradeColour.of(GradeInk.GRAPHITE),
            texelX = 0.01f, texelY = 0.01f,
        )
        assertEquals(-0.83f, uniforms.getValue("uLightDir")[0], 0.001f)
        assertEquals(-0.55f, uniforms.getValue("uLightDir")[1], 0.001f)
        assertEquals(0f, uniforms.getValue("uSubtractionOnly")[0], 0.001f)
    }
}
