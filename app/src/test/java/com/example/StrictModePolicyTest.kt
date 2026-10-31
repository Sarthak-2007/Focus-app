package com.example

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #272 (Build quality): the StrictMode debug policy must
 * exist and be safe to install. Log-only by design — these tests
 * pin that the policies build and that install() (which every
 * debug process start now runs) never throws into the app's
 * boot path.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StrictModePolicyTest {

    @Test
    fun policiesBuild() {
        assertNotNull(DebugStrictMode.threadPolicy())
        assertNotNull(DebugStrictMode.vmPolicy())
    }

    @Test
    fun installIsSafe() {
        DebugStrictMode.install()
    }
}
