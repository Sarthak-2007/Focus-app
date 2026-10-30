package com.example

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests (device/emulator only — CI's unit-test job does not
 * run this source set; it exists for on-device verification).
 *
 * Issue #66 (audit R1 tests-2): the previous file was the untouched IDE
 * template asserting only the package name. It now also asserts facts
 * that are true on a real device and false if the installed app is
 * broken: the launcher activity resolves and is exported, the shield
 * foreground service is declared, and the app label is present.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

  @Test
  fun useAppContext() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals(BuildConfig.APPLICATION_ID, appContext.packageName)
  }

  @Test
  fun launcherActivityAndShieldServiceAreDeclaredOnDevice() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val pm = appContext.packageManager

    // The launcher entry point the user actually taps.
    val launchIntent = pm.getLaunchIntentForPackage(appContext.packageName)
    assertNotNull("a LAUNCHER intent must resolve for the installed app", launchIntent)
    assertEquals(Intent.ACTION_MAIN, launchIntent!!.action)
    assertTrue(launchIntent.hasCategory(Intent.CATEGORY_LAUNCHER))

    val mainInfo = pm.getActivityInfo(
      ComponentName(appContext, MainActivity::class.java), 0
    )
    assertTrue("MainActivity must be exported (it is the launcher)", mainInfo.exported)

    // The app's reason to exist: the shield foreground service.
    val shieldInfo = pm.getServiceInfo(
      ComponentName(appContext, "com.example.service.MonolithShieldService"),
      PackageManager.GET_META_DATA
    )
    assertEquals("com.example.service.MonolithShieldService", shieldInfo.name)

    val label = pm.getApplicationLabel(appContext.applicationInfo)
    assertTrue("app label must be present and non-blank", label.isNotBlank())
  }
}
