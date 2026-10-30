package com.example

import com.example.service.shieldResurrectOnTaskRemoved
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Issue #328 (Audit R5, Zario second pass): the shield's
 * resurrection story. Zario stacks re-arm paths; Monolith's
 * shield had none — no onTaskRemoved, no stopWithTask, no
 * excludeFromRecents. The decision table and the manifest
 * declarations are both pinned here.
 */
class ShieldResurrectionTest {

    @Test
    fun protectingShieldResurrectsOnTaskRemoval() {
        assertTrue(shieldResurrectOnTaskRemoved(protecting = true))
    }

    @Test
    fun idleShieldStaysDownOnTaskRemoval() {
        assertFalse(shieldResurrectOnTaskRemoved(protecting = false))
    }

    @Test
    fun manifestDeclaresTheResurrectionSurface() {
        val manifest = listOf(
            File("src/main/AndroidManifest.xml"),
            File("app/src/main/AndroidManifest.xml"),
            File("../app/src/main/AndroidManifest.xml")
        ).firstOrNull { it.exists() }?.readText()
            ?: error("AndroidManifest.xml not found from ${File(".").absolutePath}")
        assertTrue(manifest.contains("android:stopWithTask=\"false\""))
        assertTrue(manifest.contains("android:excludeFromRecents=\"true\""))
        // Already true before this issue (issue #26) and load-bearing
        // for the resurrection ask: the battery exemption the app
        // prompts for is declared in the manifest.
        assertTrue(manifest.contains("android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"))
    }
}
