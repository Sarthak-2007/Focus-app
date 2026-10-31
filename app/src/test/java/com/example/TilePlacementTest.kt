package com.example

import com.example.service.TilePlacement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #274: the tile-placement line's pure derivations. The
 * board may print ADDED only on the tile service's lifecycle
 * testimony — an unrecorded placement prints no word (the line
 * stands as a lack) — and the system add-tile prompt exists
 * only from API 33; below it the line must name the panel's
 * edit screen instead of offering a tap that cannot ask.
 */
class TilePlacementTest {

    @Test
    fun `word prints ADDED only on recorded testimony`() {
        assertEquals("ADDED", TilePlacement.word(added = true))
        assertNull(TilePlacement.word(added = false))
    }

    @Test
    fun `system prompt boundary is API 33`() {
        assertFalse(TilePlacement.systemPromptAvailable(32))
        assertTrue(TilePlacement.systemPromptAvailable(33))
        assertTrue(TilePlacement.systemPromptAvailable(36))
    }
}
