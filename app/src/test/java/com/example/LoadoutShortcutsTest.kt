package com.example

import com.example.data.FocusProfileEntity
import com.example.ui.LoadoutShortcuts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #274: the dynamic per-loadout shortcut derivation —
 * pure logic. The launcher set must be a faithful mirror of the
 * profiles table: capped by the platform budget (statics share
 * the cap), deterministic in flow order, immune to duplicate
 * ids, and never publishing an empty label.
 */
class LoadoutShortcutsTest {

    private fun profile(id: Int, name: String, codeName: String = "CODE$id") =
        FocusProfileEntity(
            id = id, name = name, codeName = codeName, description = "d",
            defaultMinutes = 50, blockedCategoriesCsv = "",
            scheduleLabel = "", isScheduleEnabled = false,
            startHour = 9, startMinute = 0, endHour = 13, endMinute = 0
        )

    @Test
    fun `one spec per profile with stable id and labels`() {
        val specs = LoadoutShortcuts.specsFor(
            listOf(profile(7, "Study Loadout"), profile(9, "Sleep")), maxDynamic = 4
        )
        assertEquals(2, specs.size)
        assertEquals("loadout_7", specs[0].id)
        assertEquals(7, specs[0].profileId)
        assertEquals("Study Loadout", specs[0].shortLabel)
        assertEquals("Apply Study Loadout in Monolith", specs[0].longLabel)
        assertEquals("loadout_9", specs[1].id)
    }

    @Test
    fun `budget caps the set and a dead budget publishes nothing`() {
        val profiles = listOf(profile(1, "A"), profile(2, "B"), profile(3, "C"))
        assertEquals(2, LoadoutShortcuts.specsFor(profiles, maxDynamic = 2).size)
        assertTrue(LoadoutShortcuts.specsFor(profiles, maxDynamic = 0).isEmpty())
        assertTrue(LoadoutShortcuts.specsFor(profiles, maxDynamic = -1).isEmpty())
        assertTrue(LoadoutShortcuts.specsFor(emptyList(), maxDynamic = 4).isEmpty())
    }

    @Test
    fun `duplicate ids collapse to the first occurrence`() {
        val specs = LoadoutShortcuts.specsFor(
            listOf(profile(5, "First"), profile(5, "Second")), maxDynamic = 4
        )
        assertEquals(1, specs.size)
        assertEquals("First", specs[0].shortLabel)
    }

    @Test
    fun `blank name falls back to code name then to a plain label`() {
        val blankName = LoadoutShortcuts.specsFor(
            listOf(profile(3, "", codeName = "DEEP")), maxDynamic = 1
        )
        assertEquals("DEEP", blankName[0].shortLabel)
        val allBlank = LoadoutShortcuts.specsFor(
            listOf(profile(4, "", codeName = "")), maxDynamic = 1
        )
        assertEquals("Loadout", allBlank[0].shortLabel)
    }
}
