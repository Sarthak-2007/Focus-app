package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.service.BreakEndScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Issue #155 WING-F (SMALL-2) — pins for the break-end ledger, the
 * one "last shown" stamp behind a glance notification.
 *
 * The ledger is the exactly-once claim for the "Break's over" call:
 *  - it must be written with commit(), not apply() — apply()'s
 *    async disk flush lets a process death in the flush window
 *    resurrect the pre-mark value and double-post the call on the
 *    alarm's re-delivery (the standard WatchStore's LAST PROVED
 *    and the seal marker already hold);
 *  - the receiver must obtain the NotificationManager BEFORE
 *    stamping the ledger — stamping first consumed the instant
 *    even when nothing could be posted, and the dedupe rule then
 *    suppressed every legitimate re-delivery forever.
 *
 * commit-vs-apply and call ordering are not observable from a
 * same-process readback (apply() updates memory instantly), so —
 * following ShieldResurrectionTest's precedent — they are pinned
 * on the source, while the ledger's behaviour is pinned live.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WingFSmallGlanceTest {

    private fun sourceOf(relative: String): String =
        listOf(
            File("src/main/java/com/example/service/$relative"),
            File("app/src/main/java/com/example/service/$relative"),
            File("../app/src/main/java/com/example/service/$relative")
        ).firstOrNull { it.exists() }?.readText()
            ?: error("$relative not found from ${File(".").absolutePath}")

    @Test
    fun breakEndLedgerStampCommitsSynchronously() {
        val scheduler = sourceOf("BreakEndScheduler.kt")
        val markBody = scheduler.substringAfter("fun markNotified")
            .substringBefore("fun channelsSupported")
        assertTrue(
            "the exactly-once ledger must commit() — apply() can be lost with the process",
            markBody.contains(".commit()")
        )
        assertFalse(
            "the exactly-once ledger must not be left to apply()'s async flush",
            markBody.contains(".apply()")
        )
    }

    @Test
    fun breakEndReceiverObtainsManagerBeforeStampingLedger() {
        val receiver = sourceOf("BreakEndReceiver.kt")
        val managerAt = receiver.indexOf("getSystemService")
        val markAt = receiver.indexOf("BreakEndScheduler.markNotified")
        assertTrue("receiver source must obtain the manager", managerAt >= 0)
        assertTrue("receiver source must stamp the ledger", markAt >= 0)
        assertTrue(
            "a null manager must not consume the break-end instant: " +
                "obtain the manager first, stamp second",
            managerAt < markAt
        )
    }

    @Test
    fun breakEndLedgerBehaviour_markedInstantNeverRenotifies() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(
            "break_end_ledger", Context.MODE_PRIVATE
        )
        prefs.edit().clear().commit()

        assertEquals(0L, BreakEndScheduler.lastNotified(context))
        assertTrue(BreakEndScheduler.shouldNotifyBreakEnd(0L, 42_000L))

        BreakEndScheduler.markNotified(context, 42_000L)

        assertEquals(42_000L, BreakEndScheduler.lastNotified(context))
        assertFalse(
            BreakEndScheduler.shouldNotifyBreakEnd(
                BreakEndScheduler.lastNotified(context), 42_000L
            )
        )
        assertTrue(
            BreakEndScheduler.shouldNotifyBreakEnd(
                BreakEndScheduler.lastNotified(context), 43_000L
            )
        )
    }
}
