package com.example

import com.example.service.AccessibilityShortcutSupport
import com.example.service.RepairAction
import com.example.service.missingRepairActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Issue #337 (Freedom, Platform execution): notification repair
 *  taxonomy + accessibility-shortcut target parsing. */
class PlatformExecution337Test {

    @Test
    fun noRepairActionsWhenEveryGrantPresent() {
        assertEquals(
            emptyList<RepairAction>(),
            missingRepairActions(accessibilityBound = true, usageAccessGranted = true)
        )
    }

    @Test
    fun accessibilityIsTheFatalFirstAction() {
        val missing = missingRepairActions(
            accessibilityBound = false,
            usageAccessGranted = false
        )
        assertEquals(
            listOf(RepairAction.ACCESSIBILITY, RepairAction.USAGE_ACCESS),
            missing
        )
        assertEquals("Enable blocking", RepairAction.ACCESSIBILITY.label)
    }

    @Test
    fun usageOnlyMissingYieldsUsageOnly() {
        assertEquals(
            listOf(RepairAction.USAGE_ACCESS),
            missingRepairActions(accessibilityBound = true, usageAccessGranted = false)
        )
    }

    @Test
    fun shortcutTargetsParseBothStoredShapes() {
        assertEquals(
            listOf("com.example/com.example.service.MonolithAccessibilityService"),
            AccessibilityShortcutSupport.parseTargets(
                "com.example/com.example.service.MonolithAccessibilityService"
            )
        )
        assertEquals(
            listOf("com.a/.A", "com.b/.B"),
            AccessibilityShortcutSupport.parseTargets("com.a/.A:com.b/.B")
        )
        assertEquals(
            emptyList<String>(),
            AccessibilityShortcutSupport.parseTargets(null)
        )
    }

    @Test
    fun isTargetedMatchesFullAndShortForms() {
        val component = "com.example/com.example.service.MonolithAccessibilityService"
        assertTrue(
            AccessibilityShortcutSupport.isTargeted(listOf(component), component)
        )
        assertTrue(
            AccessibilityShortcutSupport.isTargeted(
                listOf("com.example/.service.MonolithAccessibilityService"),
                component
            )
        )
        assertFalse(
            AccessibilityShortcutSupport.isTargeted(
                listOf("com.other/.service.MonolithAccessibilityService"),
                component
            )
        )
        assertFalse(
            AccessibilityShortcutSupport.isTargeted(emptyList(), component)
        )
    }
}
