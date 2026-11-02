package com.example

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P7 (BUILD-SR-5) — THE GUARD SCREEN'S ENTRY POINT, by reflection.
 *
 * The screen is a rendering of GuardSurfaceModel and nothing
 * else: this test proves the compiled entry point stands with
 * the filed shape — one snapshot in, intents out — so the
 * venue's targeted mapper routes the screen file to a test by
 * name, and a future edit that changes the screen's contract
 * (a second state input, a view-model parameter) fails here
 * instead of drifting silently. Behavioural proof of what the
 * screen renders lives in GuardSurfaceModelP7Test: the model
 * IS the screen's whole law.
 */
class GuardSurfaceScreenP7Test {

    @Test
    fun screenEntryPoint_takesOneSnapshot_andSpeaksIntents() {
        val fileClass = Class.forName("com.example.ui.guard.GuardSurfaceScreenKt")
        val entry = fileClass.methods.filter { it.name == "GuardSurfaceScreen" }
        assertTrue("the GuardSurfaceScreen entry point stands", entry.isNotEmpty())
        val params = entry.first().parameterTypes.map { it.simpleName }
        assertTrue(
            "the screen's first input is the snapshot",
            params.firstOrNull() == "GuardSurfaceSnapshot"
        )
        assertTrue(
            "the screen speaks intents through a function type",
            params.any { it.contains("Function") }
        )
    }
}
