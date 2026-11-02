package com.example

import org.junit.Assert.assertTrue
import org.junit.Test

/** STAGE 10 PRE-BUILD (BUILD-SR-10): structural coverage for
 *  GradeRenderPrimitives — the render itself is judged in the
 *  installable build (AL-0063); the JVM proves the primitives exist
 *  with bounded shape, so a new unlegislated renderer fails by name. */
class GradeRenderPrimitivesTest {

    private val primitivesClass: Class<*> =
        Class.forName("com.example.ui.strongroom.grade.GradeRenderPrimitivesKt")

    @Test
    fun gradePrimitivesExist() {
        val names = primitivesClass.declaredMethods.map { it.name }.toSet()
        listOf("PunchedHoleGrade", "RelightFieldCanvas").forEach {
            assertTrue("missing primitive: $it", it in names)
        }
    }

    @Test
    fun punchedHoleHasBoundedParameters() {
        val method = primitivesClass.declaredMethods.single { it.name == "PunchedHoleGrade" }
        assertTrue("PunchedHoleGrade grew parameters: ${method.parameterCount}", method.parameterCount <= 7)
    }
}
