package com.example

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.service.BreakEndReceiver
import com.example.service.InterceptLaunch
import com.example.service.MonolithShieldService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Issue #155 F3 residue (BUILDER-2, WING-F) — pins for the intercept
 * fallback card's lifecycle.
 *
 * The F3 sweep of every notification the app can leave standing
 * found the InterceptLaunch fallback (issue #94) carrying two ghost
 * defects the known cards (4096/4097) did not:
 *
 *  1. ID COLLISION: the fallback posted under id 4097 — the summons
 *     id. Notification identity is (package, id), channel aside, so
 *     a summons and a fallback silently replaced each other: a
 *     BREAK OVER / SESSION COMPLETE summons could erase the only
 *     user-gesture door to a dropped Airlock firing.
 *  2. NO EXPIRY: the card was autoCancel-only, so an untapped
 *     "<app> blocked" card lingered indefinitely and a late tap
 *     delivered a stale firing's intent as if it were current.
 *
 * The cure pinned here: the fallback owns id 4099, self-expires on
 * the estate's 45-minute moment-card clock, and a successful direct
 * launch retires a standing fallback card (its premise — "the block
 * screen could not open on its own" — is proven dead by the launch).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class InterceptFallbackExpiryTest {

    private fun throwingContext(base: Context): Context =
        object : ContextWrapper(base) {
            override fun startActivity(intent: Intent) {
                throw SecurityException("Background activity launch blocked")
            }
        }

    private fun interceptIntent(base: Context): Intent =
        Intent(base, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(MonolithShieldService.EXTRA_INTERCEPTED_APP_NAME, "Instagram")
        }

    private fun manager(base: Context): NotificationManager =
        base.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Test
    fun fallbackIdIsItsOwn_noCollisionWithAnyEstateCard() {
        assertNotEquals(
            MonolithShieldService.NOTIFICATION_ID,
            InterceptLaunch.FALLBACK_NOTIFICATION_ID
        )
        assertNotEquals(
            MonolithShieldService.SUMMONS_NOTIFICATION_ID,
            InterceptLaunch.FALLBACK_NOTIFICATION_ID
        )
        assertNotEquals(
            MonolithShieldService.BREAK_ALERT_NOTIFICATION_ID,
            InterceptLaunch.FALLBACK_NOTIFICATION_ID
        )
        assertNotEquals(
            BreakEndReceiver.NOTIFICATION_ID,
            InterceptLaunch.FALLBACK_NOTIFICATION_ID
        )
    }

    @Test
    fun droppedLaunchPostsFallbackUnderItsOwnId_notTheSummonsId() {
        val base = ApplicationProvider.getApplicationContext<Context>()
        InterceptLaunch.launch(throwingContext(base), interceptIntent(base), "Instagram")
        val shadow = Shadows.shadowOf(manager(base))
        assertTrue(
            "fallback must post under its own id",
            shadow.allNotifications.any { it.id == InterceptLaunch.FALLBACK_NOTIFICATION_ID }
        )
        assertNull(
            "nothing may post under the summons id from the intercept door",
            shadow.getNotification(MonolithShieldService.SUMMONS_NOTIFICATION_ID)
        )
    }

    @Test
    fun fallbackCardExpiresOnTheEstateMomentCardClock() {
        val base = ApplicationProvider.getApplicationContext<Context>()
        InterceptLaunch.launch(throwingContext(base), interceptIntent(base), "Instagram")
        val posted = Shadows.shadowOf(manager(base)).allNotifications
            .first { it.id == InterceptLaunch.FALLBACK_NOTIFICATION_ID }
            .notification
        assertEquals(
            MonolithShieldService.COMPLETION_TIMEOUT_MS,
            posted.timeoutAfter
        )
        assertTrue(
            "fallback must dismiss on tap",
            posted.flags and Notification.FLAG_AUTO_CANCEL != 0
        )
        assertTrue(
            "fallback must never be an ongoing standing card",
            posted.flags and Notification.FLAG_ONGOING_EVENT == 0
        )
    }

    @Test
    fun fallbackEnsuresTheShieldChannelWithTheShieldsOwnSpec() {
        // Issue #155 C2 (SMALL-3 manifest audit): the fallback path
        // used to create monolith_shield_channel_v2 itself —
        // IMPORTANCE_HIGH, no badge call — against the shield's
        // IMPORTANCE_LOW + badge ON. First-writer-wins would fix
        // the standing card wrong for the life of the install.
        // After a dropped launch (the fallback path ran), the
        // channel on record must carry the SHIELD's spec.
        val base = ApplicationProvider.getApplicationContext<Context>()
        InterceptLaunch.launch(throwingContext(base), interceptIntent(base), "Instagram")
        val channel = manager(base).getNotificationChannel(MonolithShieldService.CHANNEL_ID)
        assertEquals(
            NotificationManager.IMPORTANCE_LOW,
            channel.importance
        )
        assertTrue(
            "the shield channel's badge stays ON (issue #155 F4)",
            channel.canShowBadge()
        )
    }

    @Test
    fun interceptLaunchNeverCreatesAChannelItself() {
        // Source pin: channel creation is single-sourced in the
        // shield (ensureShieldChannel). Any NotificationChannel
        // construction or IMPORTANCE_ constant creeping back into
        // InterceptLaunch re-opens the C2 drift.
        val source = listOf(
            java.io.File("src/main/java/com/example/service/InterceptLaunch.kt"),
            java.io.File("app/src/main/java/com/example/service/InterceptLaunch.kt"),
            java.io.File("../app/src/main/java/com/example/service/InterceptLaunch.kt")
        ).firstOrNull { it.exists() }?.readText()
            ?: error("InterceptLaunch.kt not found")
        assertTrue(source.contains("MonolithShieldService.ensureShieldChannel(context)"))
        assertTrue(!source.contains("NotificationChannel("))
        assertTrue(!source.contains("NotificationManager.IMPORTANCE"))
    }

    @Test
    fun successfulLaunchRetiresAStandingFallbackCard() {
        val base = ApplicationProvider.getApplicationContext<Context>()
        InterceptLaunch.launch(throwingContext(base), interceptIntent(base), "Instagram")
        val shadow = Shadows.shadowOf(manager(base))
        assertTrue(
            shadow.allNotifications.any { it.id == InterceptLaunch.FALLBACK_NOTIFICATION_ID }
        )

        val launched = InterceptLaunch.launch(base, interceptIntent(base), "Instagram")

        assertTrue(launched)
        assertNull(
            "a launch that opened on its own kills the fallback's premise; the card must go",
            shadow.getNotification(InterceptLaunch.FALLBACK_NOTIFICATION_ID)
        )
    }
}
