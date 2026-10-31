package com.example

import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.service.BreakEndReceiver
import com.example.service.BreakEndScheduler
import com.example.service.InterceptLaunch
import com.example.service.MonolithShieldService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Issue #94, routed strays ex #155 (WING-D, EXPERIENCE):
 *
 * Stray 2 — the channel-importance collision. The intercept
 * fallback used to re-create the shield's standing channel at
 * IMPORTANCE_HIGH while the shield creates it at IMPORTANCE_LOW;
 * Android channels are immutable once created, so first creator
 * won forever. The behavioural pin below: after a dropped launch
 * posts its fallback, the shield channel is NOT standing at HIGH
 * (it is absent — only the shield creates it, at LOW), and the
 * fallback's own channel is the HIGH one.
 *
 * Stray 1 — unspecified HIGH-channel defaults. The summons and
 * break-end channels inherited system sound/vibration defaults
 * and stacked them on the engine's own voice at the same event.
 * Both channels are now explicitly silent under v2 ids (engine
 * speaks, channel carries the card). The break-end half is
 * pinned behaviourally through the receiver; the summons half
 * lives inside the shield service, so it follows the repo's
 * source-pin pattern (BreakEndNotificationExpiryTest).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue94ChannelSpecTest {

    private fun throwingContext(base: Context): Context =
        object : ContextWrapper(base) {
            override fun startActivity(intent: Intent) {
                throw SecurityException("Background activity launch blocked")
            }
        }

    @Test
    fun droppedLaunch_doesNotPoisonTheShieldChannel() {
        val base = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(base, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(MonolithShieldService.EXTRA_INTERCEPTED_APP_NAME, "Instagram")
        }

        val launched = InterceptLaunch.launch(throwingContext(base), intent, "Instagram")
        assertFalse(launched)

        val manager = base.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager
        val shieldChannel = manager.getNotificationChannel(MonolithShieldService.CHANNEL_ID)
        assertTrue(
            "the fallback must never fix the shield channel at HIGH",
            shieldChannel == null ||
                shieldChannel.importance != NotificationManager.IMPORTANCE_HIGH
        )
        val fallbackChannel =
            manager.getNotificationChannel(InterceptLaunch.FALLBACK_CHANNEL_ID)
        assertEquals(
            NotificationManager.IMPORTANCE_HIGH, fallbackChannel?.importance
        )
    }

    @Test
    fun breakEndChannel_isExplicitlySilent() {
        val base = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(base, BreakEndReceiver::class.java).apply {
            putExtra(BreakEndScheduler.EXTRA_BREAK_END_MILLIS, 1_700_000_000_000L)
        }

        BreakEndReceiver().onReceive(base, intent)

        val manager = base.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager
        val channel = manager.getNotificationChannel(BreakEndReceiver.CHANNEL_ID)
        assertTrue("the v2 break-end channel must exist after a post", channel != null)
        assertNull(
            "break-end is engine-owned: the channel carries no sound of its own",
            channel?.sound
        )
        assertFalse(
            "break-end is engine-owned: the channel adds no vibration of its own",
            channel?.shouldVibrate() ?: true
        )
        // The legacy channel's deletion is pinned at the source:
        // in a fresh test environment it never existed, so only
        // the receiver's own delete call proves the migration.
        val source = listOf(
            File("src/main/java/com/example/service/BreakEndReceiver.kt"),
            File("app/src/main/java/com/example/service/BreakEndReceiver.kt"),
            File("../app/src/main/java/com/example/service/BreakEndReceiver.kt")
        ).firstOrNull { it.exists() }?.readText()
            ?: error("BreakEndReceiver.kt not found from ${File(".").absolutePath}")
        assertTrue(source.contains("deleteNotificationChannel(LEGACY_CHANNEL_ID)"))
        assertTrue(source.contains("setSound(null, null)"))
        assertTrue(source.contains("enableVibration(false)"))
    }

    @Test
    fun summonsChannel_isExplicitlySilent_v2() {
        assertEquals(
            "monolith_summons_channel_v2", MonolithShieldService.SUMMONS_CHANNEL_ID
        )
        assertEquals(
            "monolith_summons_channel", MonolithShieldService.LEGACY_SUMMONS_CHANNEL_ID
        )
        val source = listOf(
            File("src/main/java/com/example/service/MonolithShieldService.kt"),
            File("app/src/main/java/com/example/service/MonolithShieldService.kt"),
            File("../app/src/main/java/com/example/service/MonolithShieldService.kt")
        ).firstOrNull { it.exists() }?.readText()
            ?: error("MonolithShieldService.kt not found from ${File(".").absolutePath}")
        val summonsSite = source.substringAfter("private fun postSummons")
            .substringBefore("private fun")
        assertTrue(summonsSite.contains("setSound(null, null)"))
        assertTrue(summonsSite.contains("enableVibration(false)"))
        assertTrue(
            summonsSite.contains("deleteNotificationChannel(LEGACY_SUMMONS_CHANNEL_ID)")
        )
    }
}
