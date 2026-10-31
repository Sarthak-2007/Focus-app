package com.example

import com.example.data.InterceptInsight
import com.example.data.InterceptLogEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.TimeZone

/**
 * Issues #251 / #253 / #254 / #255 (Audit R5, DURCH-E lane) —
 * the intercept log read back: per-app tallies and resist rates,
 * per-trigger tallies, the danger hour, and the truth rules
 * (peek is never a resist, unclassified is OTHER, exposure
 * bookkeeping rows are not urges, empty invents nothing).
 */
class InterceptInsightTest {

    private val utc: TimeZone = TimeZone.getTimeZone("UTC")

    private fun log(
        pkg: String = "com.example.distractor",
        app: String = "Distractor",
        outcome: String = "Stayed Focused",
        trigger: String = "Habit",
        atMillis: Long = 0L
    ) = InterceptLogEntity(
        timestampMillis = atMillis,
        packageName = pkg,
        appName = app,
        sessionIntention = "Study",
        outcome = outcome,
        triggerReason = trigger
    )

    /** 2026-01-05T14:30:00Z — hour 14 in UTC. */
    private val h14 = 1_767_623_400_000L
    private val hourMs = 3_600_000L

    @Test
    fun `empty log derives to zeros and nulls, never inventions`() {
        val insight = InterceptInsight.derive(emptyList(), utc)
        assertEquals(0, insight.total)
        assertEquals(0, insight.resisted)
        assertTrue(insight.perApp.isEmpty())
        assertTrue(insight.perTrigger.isEmpty())
        assertEquals(List(24) { 0 }, insight.perHour)
        assertNull(insight.dangerHour)
        assertNull(insight.dominantTrigger)
        assertNull(insight.resistRate)
    }

    @Test
    fun `per-app tally counts attempts, resists and peeks separately`() {
        val logs = listOf(
            log(outcome = "Stayed Focused", atMillis = h14),
            log(outcome = "Blocked", atMillis = h14 + hourMs),
            log(outcome = "Used 2m Peek", atMillis = h14 + 2 * hourMs),
            log(pkg = "com.other", app = "Other", outcome = "Stayed Focused", atMillis = h14)
        )
        val insight = InterceptInsight.derive(logs, utc)
        assertEquals(4, insight.total)
        assertEquals(3, insight.resisted)
        assertEquals(1, insight.peeked)
        assertEquals(0, insight.other)
        assertEquals(0.75f, insight.resistRate!!, 0.001f)

        val top = insight.perApp.first()
        assertEquals("com.example.distractor", top.packageName)
        assertEquals(3, top.total)
        assertEquals(2, top.resisted)
        assertEquals(1, top.peeked)
        assertEquals(2f / 3f, top.resistRate!!, 0.001f)
        assertEquals("com.other", insight.perApp[1].packageName)
    }

    @Test
    fun `a peek is never counted as a resist`() {
        val insight = InterceptInsight.derive(
            listOf(log(outcome = "Used 2m Peek", atMillis = h14)), utc
        )
        assertEquals(0, insight.resisted)
        assertEquals(1, insight.peeked)
        assertEquals(0.0f, insight.resistRate!!, 0.001f)
    }

    @Test
    fun `unclassified outcomes land in other and carry no rate`() {
        val logs = listOf(
            log(outcome = "Left Airlock Without Choosing", atMillis = h14),
            log(outcome = "Opened Launcher", atMillis = h14)
        )
        val insight = InterceptInsight.derive(logs, utc)
        assertEquals(2, insight.other)
        assertEquals(0, insight.resisted)
        assertNull(insight.resistRate)
        assertNull(insight.perApp.first().resistRate)
    }

    @Test
    fun `danger hour is the busiest local hour, earliest on a tie`() {
        val logs = listOf(
            log(atMillis = h14),               // 14 UTC
            log(atMillis = h14 + 30_000L),     // 14 UTC
            log(atMillis = h14 - 5 * hourMs),  // 09 UTC
            log(atMillis = h14 - 5 * hourMs + 30_000L) // 09 UTC
        )
        val insight = InterceptInsight.derive(logs, utc)
        assertEquals(2, insight.perHour[14])
        assertEquals(2, insight.perHour[9])
        assertEquals(9, insight.dangerHour)
    }

    @Test
    fun `per-trigger tally and dominant trigger`() {
        val logs = listOf(
            log(trigger = "Boredom", outcome = "Stayed Focused", atMillis = h14),
            log(trigger = "Boredom", outcome = "Used 2m Peek", atMillis = h14),
            log(trigger = "Habit", outcome = "Stayed Focused", atMillis = h14)
        )
        val insight = InterceptInsight.derive(logs, utc)
        assertEquals("Boredom", insight.dominantTrigger)
        val boredom = insight.perTrigger.first()
        assertEquals(2, boredom.total)
        assertEquals(1, boredom.resisted)
    }

    @Test
    fun `exposure bookkeeping rows are not urges`() {
        val logs = listOf(
            log(
                pkg = "com.monolith.exposure", app = "Exposed",
                outcome = "Open exposure", atMillis = h14
            ),
            log(outcome = "Stayed Focused", atMillis = h14)
        )
        val insight = InterceptInsight.derive(logs, utc)
        assertEquals(1, insight.total)
        assertEquals(1, insight.perApp.size)
        assertEquals("com.example.distractor", insight.perApp.first().packageName)
    }

    @Test
    fun `app name served is the most recent row's name`() {
        val logs = listOf(
            log(app = "Old Name", atMillis = h14),
            log(app = "New Name", atMillis = h14 + hourMs)
        )
        val insight = InterceptInsight.derive(logs, utc)
        assertEquals("New Name", insight.perApp.first().appName)
    }

    @Test
    fun `split into micro-step counts as resisted, per the slip allowlist`() {
        val insight = InterceptInsight.derive(
            listOf(log(outcome = "Split Into 10m Micro-Step", atMillis = h14)), utc
        )
        assertEquals(1, insight.resisted)
    }
}
