package com.example

import com.example.ui.guard.GuardInks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P7 (BUILD-SR-5) — THE GUARD TOKENS' CENSUS, pure JVM.
 *
 * The palette is closed (Audit A §4): exactly the seven inks,
 * read from the lane token file's own census — and never green,
 * by the same per-ink rule the foundation's census uses (green
 * below the stronger of red/blue in every ink).
 */
class GuardTokensP7Test {

    @Test
    fun palette_isExactlyTheSevenInks_neverGreen() {
        assertEquals(7, GuardInks.size)
        assertEquals(7, GuardInks.distinct().size)
        GuardInks.forEach { ink ->
            assertTrue(
                "an ink whose green dominates is not in this palette",
                ink.green < maxOf(ink.red, ink.blue)
            )
        }
    }
}
