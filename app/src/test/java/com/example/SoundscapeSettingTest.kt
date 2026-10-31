package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.service.SoundscapeSetting
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * #253 line 1: the soundscape fact (SoundscapeSetting) — born
 * OFF (content is chosen, never a surprise), and a committed
 * write is the value every later read returns.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SoundscapeSettingTest {

    private lateinit var context: Context

    @Before
    fun useFreshPrefs() {
        context = ApplicationProvider.getApplicationContext()
        val deviceContext = context.createDeviceProtectedStorageContext() ?: context
        deviceContext.getSharedPreferences(SoundscapeSetting.PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @Test
    fun defaultIsOff() {
        assertFalse(SoundscapeSetting.isEnabled(context))
    }

    @Test
    fun committedWriteIsTheFact() {
        assertTrue(SoundscapeSetting.setEnabled(context, true))
        assertTrue(SoundscapeSetting.isEnabled(context))
        assertTrue(SoundscapeSetting.setEnabled(context, false))
        assertFalse(SoundscapeSetting.isEnabled(context))
    }
}
