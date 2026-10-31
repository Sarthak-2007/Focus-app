package com.example

import com.example.service.WindowSurface
import com.example.service.WindowSurfaceClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #275 (Audit R5 supplement — Blockit head-to-head, the
 * keyguard line): the window-surface classification law.
 *
 * What is pinned here:
 *  - the lockscreen / PIN surface (system UI while the keyguard
 *    is locked) classifies as KEYGUARD — a named class of its
 *    own, not an unnamed casualty of a blanket package skip;
 *  - system chrome while unlocked (shade, recents, the
 *    biometric prompt's host) classifies as SYSTEM;
 *  - Monolith's own package classifies OWN, checked first,
 *    however the keyguard flag reads;
 *  - real apps classify APP, and only APP passes the door's
 *    process gate;
 *  - the system-UI family test is the historical prefix test —
 *    the same packages are in the family as before the
 *    classification existed, so avoidance behaviour is unchanged.
 */
class WindowSurfaceClassifierTest {

    private val own = "com.example.monolith"

    @Test
    fun lockscreenWhileKeyguardLocked_classifiesKeyguard() {
        assertEquals(
            WindowSurface.KEYGUARD,
            WindowSurfaceClassifier.classify("com.android.systemui", own, keyguardLocked = true)
        )
    }

    @Test
    fun shadeWhileUnlocked_classifiesSystem() {
        assertEquals(
            WindowSurface.SYSTEM,
            WindowSurfaceClassifier.classify("com.android.systemui", own, keyguardLocked = false)
        )
    }

    @Test
    fun systemUiSubpackageWhileLocked_classifiesKeyguard() {
        assertEquals(
            WindowSurface.KEYGUARD,
            WindowSurfaceClassifier.classify("com.android.systemui.keyguard", own, keyguardLocked = true)
        )
    }

    @Test
    fun ownPackage_classifiesOwn_regardlessOfKeyguard() {
        assertEquals(
            WindowSurface.OWN,
            WindowSurfaceClassifier.classify(own, own, keyguardLocked = true)
        )
        assertEquals(
            WindowSurface.OWN,
            WindowSurfaceClassifier.classify(own, own, keyguardLocked = false)
        )
    }

    @Test
    fun androidAndPermissionControllers_classifySystem() {
        assertEquals(
            WindowSurface.SYSTEM,
            WindowSurfaceClassifier.classify("android", own, keyguardLocked = false)
        )
        assertEquals(
            WindowSurface.SYSTEM,
            WindowSurfaceClassifier.classify("com.android.permissioncontroller", own, keyguardLocked = false)
        )
        assertEquals(
            WindowSurface.SYSTEM,
            WindowSurfaceClassifier.classify("com.google.android.permissioncontroller", own, keyguardLocked = true)
        )
    }

    @Test
    fun realApp_classifiesApp_andPassesTheGate() {
        assertEquals(
            WindowSurface.APP,
            WindowSurfaceClassifier.classify("com.instagram.android", own, keyguardLocked = false)
        )
        assertTrue(
            WindowSurfaceClassifier.isAppSurface("com.instagram.android", own, keyguardLocked = false)
        )
        assertFalse(
            WindowSurfaceClassifier.isAppSurface("com.android.systemui", own, keyguardLocked = true)
        )
        assertFalse(
            WindowSurfaceClassifier.isAppSurface(own, own, keyguardLocked = false)
        )
    }

    @Test
    fun systemUiFamily_isTheHistoricalPrefixTest() {
        assertTrue(WindowSurfaceClassifier.isSystemUiFamily("com.android.systemui"))
        assertTrue(WindowSurfaceClassifier.isSystemUiFamily("com.android.systemui.keyguard"))
        assertFalse(WindowSurfaceClassifier.isSystemUiFamily("com.android.settings"))
        assertFalse(WindowSurfaceClassifier.isSystemUiFamily("com.instagram.android"))
    }
}
