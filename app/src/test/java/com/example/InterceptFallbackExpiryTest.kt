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
 *
 * PIN-SCOPE: POST + SOURCE — the lifecycle assertions are POST
 * (they read the NotificationManager state a real post created,
 * via Robolectric shadows); the channel-ownership assertion is a
 * SOURCE pin (it reads InterceptLaunch.kt text, per the repo's
 * source-pin pattern).
 *
 * RE-SCOPED at the merge pass (MERGE-PASS-D, Oct 31): the C2
 * channel route this file originally pinned (the fallback creates
 * no channel and posts on the shield's channel via
 * ensureShieldChannel) was cured on main by a rival form while
 * PR #626 sat unmerged — commit ff63d5ce (issue #94, routed
 * strays ex #155, landed 2026-10-31T13:19:25Z): the fallback
 * creates only its OWN channel (FALLBACK_CHANNEL_ID, HIGH) and
 * the shield channel keeps exactly one creator, a property
 * Issue94ChannelSpecTest pins behaviourally. The two
 * channel-route pins below therefore guard the surviving
 * single-creator property in main's landed form; the lifecycle
 * pins (id, expiry, cancel) stand as originally written.
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
    fun fallbackNeverFixesTheShieldChannelAtHigh() {
        // Issue #155 C2, in main's landed form (see header): after
        // a dropped launch (the fallback path ran), the shield
        // channel must not stand at HIGH — the fallback may never
        // be its creator at the wrong importance (first-writer-
        // wins would fix the standing card wrong for the life of
        // the install). It is absent here — only the shield
        // creates it, at LOW — or carries a non-HIGH importance.
        // The same property is pinned from the #94 side by
        // Issue94ChannelSpecTest.
        val base = ApplicationProvider.getApplicationContext<Context>()
        InterceptLaunch.launch(throwingContext(base), interceptIntent(base), "Instagram")
        val channel = manager(base).getNotificationChannel(MonolithShieldService.CHANNEL_ID)
        assertTrue(
            "the fallback must never fix the shield channel at HIGH",
            channel == null ||
                channel.importance != NotificationManager.IMPORTANCE_HIGH
        )
    }

    @Test
    fun interceptLaunchCreatesOnlyItsOwnFallbackChannel_neverTheShields() {
        // Source pin, re-scoped at the merge pass (see header):
        // InterceptLaunch constructs exactly ONE channel — its
        // own fallback channel — and never constructs the
        // shield's. Any second construction, or one aimed at the
        // shield's id, re-opens the C2 first-writer-wins drift.
        val source = listOf(
            java.io.File("src/main/java/com/example/service/InterceptLaunch.kt"),
            java.io.File("app/src/main/java/com/example/service/InterceptLaunch.kt"),
            java.io.File("../app/src/main/java/com/example/service/InterceptLaunch.kt")
        ).firstOrNull { it.exists() }?.readText()
            ?: error("InterceptLaunch.kt not found")
        assertEquals(
            1,
            Regex("(?<!create)NotificationChannel\\(").findAll(source).count()
        )
        assertTrue(
            source.contains(
                "NotificationChannel(\n                        FALLBACK_CHANNEL_ID,"
            )
        )
        assertTrue(
            !source.contains(
                "NotificationChannel(\n                        MonolithShieldService.CHANNEL_ID"
            )
        )
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
