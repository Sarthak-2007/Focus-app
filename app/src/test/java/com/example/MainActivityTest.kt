package com.example

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * BUILD-SR-34 — the shell's identity, pinned: the launcher
 * activity is still com.example.MainActivity (the deletion's
 * C-11 law — the class identity the manifest, both aliases and
 * the engine's callers name does not move when its composition
 * becomes the Strongroom's own order).
 */
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    @Test
    fun launcherActivityKeepsItsIdentity() {
        assertEquals("com.example.MainActivity", MainActivity::class.java.name)
    }
}
