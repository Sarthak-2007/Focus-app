package com.example

import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #272 (R5 Build quality): the "on this phone only" guarantee
 * must be ENFORCED, not just true today. If any future change merges
 * an INTERNET permission into the manifest, this test fails CI —
 * the guarantee cannot ship silently broken.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OfflineGuaranteeTest {

    @Test
    fun `manifest must never request INTERNET`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val info = context.packageManager.getPackageInfo(
            context.packageName, PackageManager.GET_PERMISSIONS
        )
        val requested = info.requestedPermissions?.toList().orEmpty()
        assertFalse(
            "INTERNET permission would break the on-this-phone-only guarantee: $requested",
            requested.contains("android.permission.INTERNET")
        )
        assertFalse(
            requested.contains("android.permission.ACCESS_NETWORK_STATE")
        )
        // Sanity: the permissions the blocker actually needs are present,
        // so an empty permission list cannot pass this test vacuously.
        assertTrue(requested.contains("android.permission.FOREGROUND_SERVICE"))
        assertTrue(requested.contains("android.permission.POST_NOTIFICATIONS"))
    }
}
