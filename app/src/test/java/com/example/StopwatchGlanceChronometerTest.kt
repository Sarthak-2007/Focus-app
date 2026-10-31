package com.example

import android.app.Notification
import com.example.service.MonolithShieldService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Audit R5 #278, line 52 — "the sole glance carries no visual
 * progress". Timed sessions already tick a system countdown
 * chronometer (#155 seam, deepened by PR #492), but the stopwatch
 * arm of the loop passed no anchor at all: its standing card was
 * a text snapshot re-posted by the loop, with no progress bar, no
 * chronometer and no proportion encoding — the exact absence the
 * audit named.
 *
 * The cure: the loop derives a count-UP chronometer start anchor
 * from the session's own elapsedSeconds, and buildNotification
 * applies it as a system chronometer counting up. These pins hold
 * both halves: the builder applies a count-up chronometer when
 * handed a start anchor (and the countdown path is untouched),
 * and the loop's derivation stays wired to the stopwatch arm.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StopwatchGlanceChronometerTest {

    private fun buildCard(
        chronometerEndMillis: Long?,
        chronometerStartMillis: Long?
    ): Notification {
        val service = Robolectric.buildService(MonolithShieldService::class.java)
            .create().get()
        val method = MonolithShieldService::class.java.getDeclaredMethod(
            "buildNotification",
            String::class.java,
            String::class.java,
            Boolean::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType,
            Long::class.javaPrimitiveType,
            String::class.java,
            Long::class.javaObjectType,
            Int::class.javaObjectType,
            Boolean::class.javaPrimitiveType,
            Long::class.javaObjectType
        )
        method.isAccessible = true
        return method.invoke(
            service, "Monolith", "Stopwatch +07:30 (20m floor)",
            true, false, 0L, null,
            chronometerEndMillis, null, false, chronometerStartMillis
        ) as Notification
    }

    @Test
    fun stopwatchCardCountsUpFromTheElapsedStartAnchor() {
        val anchor = 1_700_000_000_000L
        val n = buildCard(chronometerEndMillis = null, chronometerStartMillis = anchor)
        assertEquals(
            "stopwatch card must tick from the elapsed start anchor",
            anchor, n.`when`
        )
        assertTrue(
            "stopwatch card must show a chronometer",
            n.extras.getBoolean(Notification.EXTRA_SHOW_CHRONOMETER)
        )
        assertFalse(
            "stopwatch chronometer must count UP, not down",
            n.extras.getBoolean(Notification.EXTRA_CHRONOMETER_COUNT_DOWN)
        )
    }

    @Test
    fun countdownCardStillCountsDownToTheSessionEnd() {
        // Contrast pin: the timed-session path is untouched — a
        // card handed an end anchor still counts down to it, and
        // the mirror fallback never re-anchors it.
        val end = 1_700_003_600_000L
        val n = buildCard(chronometerEndMillis = end, chronometerStartMillis = null)
        assertEquals(end, n.`when`)
        assertTrue(n.extras.getBoolean(Notification.EXTRA_SHOW_CHRONOMETER))
        assertTrue(
            "timed card must keep counting DOWN to the session end",
            n.extras.getBoolean(Notification.EXTRA_CHRONOMETER_COUNT_DOWN)
        )
    }

    @Test
    fun loopDerivesTheStopwatchStartAnchorFromElapsedSeconds() {
        val source = shieldSource()
        val derivation = source.substringAfter("val chronometerEndMillis")
            .substringBefore("val badgeNumber")
        assertTrue(
            "loop must derive a chronometer start anchor",
            derivation.contains("chronometerStartMillis")
        )
        assertTrue(
            "start anchor must be keyed to the stopwatch arm",
            derivation.contains("isStopwatchMode")
        )
        assertTrue(
            "start anchor must derive from the printed elapsedSeconds",
            derivation.contains("elapsedSeconds")
        )
        assertTrue(
            "standing post must hand the start anchor to updateNotification",
            source.contains("chronometerStartMillis = chronometerStartMillis")
        )
    }

    private fun shieldSource(): String = listOf(
        File("src/main/java/com/example/service/MonolithShieldService.kt"),
        File("app/src/main/java/com/example/service/MonolithShieldService.kt"),
        File("../app/src/main/java/com/example/service/MonolithShieldService.kt")
    ).firstOrNull { it.exists() }?.readText()
        ?: error("MonolithShieldService.kt not found from ${File(".").absolutePath}")
}
