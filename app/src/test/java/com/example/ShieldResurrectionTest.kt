package com.example

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Issue #324 (Audit R5, Zario second pass): the shield's
 * resurrection story. Zario stacks re-arm paths; when this lane
 * started, Monolith's shield had none — no onTaskRemoved, no
 * stopWithTask, no excludeFromRecents.
 *
 * Merge reconciliation (Oct 30): while this lane was in flight,
 * main landed the onTaskRemoved resurrection itself via issue
 * #43 — the ShieldRestartPolicy alarm re-dispatch in
 * MonolithShieldService, its truth table pinned by
 * ForegroundSelectionTest. This lane's duplicate override was
 * therefore dropped; what this lane uniquely adds is the
 * manifest surface below, pinned here.
 */
class ShieldResurrectionTest {

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
