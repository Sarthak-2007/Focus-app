package com.example

import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.service.InterceptLaunch
import com.example.service.MonolithShieldService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Issue #94 (audit R2, oem-battery-reality F2): a background intercept
 * launch that the system (or an OEM background-popup gate) refuses must
 * never vanish silently — it stamps the drop and posts a tappable
 * notification carrying the intercept.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class InterceptLaunchTest {

    /** A context whose startActivity always throws, as a denied
     *  background launch does. */
    private fun throwingContext(base: Context): Context =
        object : ContextWrapper(base) {
            override fun startActivity(intent: Intent) {
                throw SecurityException("Background activity launch blocked")
            }
        }

    @Test
    fun droppedLaunch_isStampedAndFallsBackToNotification() {
        val base = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(base, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(MonolithShieldService.EXTRA_INTERCEPTED_APP_NAME, "Instagram")
        }
        val before = InterceptLaunch.droppedLaunchCount

        val launched = InterceptLaunch.launch(throwingContext(base), intent, "Instagram")

        assertFalse(launched)
        assertEquals(before + 1, InterceptLaunch.droppedLaunchCount)
        assertTrue(InterceptLaunch.lastDroppedLaunchWallMillis > 0L)
        val manager = base.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager
        val posted = Shadows.shadowOf(manager).allNotifications
        assertTrue(
            "a dropped launch must post the fallback notification",
            posted.isNotEmpty()
        )
    }

    @Test
    fun successfulLaunch_postsNoFallback() {
        val base = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(base, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val before = InterceptLaunch.droppedLaunchCount

        val launched = InterceptLaunch.launch(base, intent, "Instagram")

        assertTrue(launched)
        assertEquals(before, InterceptLaunch.droppedLaunchCount)
    }
}
