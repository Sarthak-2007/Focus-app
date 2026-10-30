package com.example

import com.example.ui.theme.MonolithIcons
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Issue #336: the one icon system — registry invariants. */
class MonolithIconsTest {

    @Test
    fun registryIsCompleteAndUnique() {
        assertEquals(16, MonolithIcons.All.size)
        val names = MonolithIcons.All.map { it.name }
        assertEquals("icon names must be unique", names.size, names.toSet().size)
    }

    @Test
    fun everyIconSharesTheOneGrid() {
        for (icon in MonolithIcons.All) {
            assertEquals("viewport width for ${icon.name}", 24f, icon.viewportWidth)
            assertEquals("viewport height for ${icon.name}", 24f, icon.viewportHeight)
            assertTrue("${icon.name} must carry path geometry", icon.root.iterator().hasNext())
        }
    }

    @Test
    fun theMeaningsTheAuditNamedArePresent() {
        // The surfaces Freedom's five fonts serve — devices,
        // navigation, onboarding, social, utilities — map to named
        // metaphors here: shield/lock (the block), chevrons and
        // arrow (navigation), eye/key/bell/clock (the product's own
        // acts). A missing metaphor is a registry failure.
        val names = MonolithIcons.All.map { it.name }.toSet()
        for (expected in listOf(
            "Filled.Shield", "Filled.Lock", "Filled.Schedule",
            "Filled.Visibility", "Filled.VpnKey", "Filled.Notifications",
            "Filled.Check", "Filled.Close", "Filled.ChevronRight",
            "Filled.ChevronLeft", "Filled.FlashOn", "Filled.DarkMode",
            "Filled.PlayArrow", "Filled.Pause"
        )) {
            assertTrue("missing icon $expected", names.contains(expected))
        }
    }
}
