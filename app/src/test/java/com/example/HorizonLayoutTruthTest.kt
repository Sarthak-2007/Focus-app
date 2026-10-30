package com.example

import com.example.ui.views.HorizonLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #61 findings 4+5: the horizon bar must be truly proportional
 * (no silent 10-minute floor) and must never lay out off-canvas.
 */
class HorizonLayoutTruthTest {

    private val gap = 4f
    private val minW = 10f

    @Test
    fun proportional_noSilentTenMinuteFloor() {
        // 1m vs 100m: old code padded to 10 vs 100 (ratio 0.10).
        // Truth: ratio must be 1/100.
        val plan = HorizonLayout.plan(listOf(1, 100), availW = 1010f, gap = gap, minMarkerW = minW)
        assertEquals(2, plan.size)
        // 1m share = (1010-4) * 1/101 = ~9.96 < minW, so it becomes a
        // min marker at exactly minW — flagged distinct, not padded
        // into the proportional pool as if it were 10 minutes.
        assertTrue(plan[0].isMinMarker)
        assertEquals(minW, plan[0].width, 0.01f)
        // The 100m segment takes ALL the rest, not 100/110 of it.
        assertEquals(1010f - gap - minW, plan[1].width, 0.5f)
    }

    @Test
    fun proportional_exactRatioWhenAboveMinimum() {
        val plan = HorizonLayout.plan(listOf(30, 90), availW = 484f, gap = gap, minMarkerW = minW)
        assertEquals(2, plan.size)
        assertTrue(plan.none { it.isMinMarker })
        assertEquals(120f, plan[0].width, 0.5f) // 480 * 30/120
        assertEquals(360f, plan[1].width, 0.5f) // 480 * 90/120
    }

    @Test
    fun zeroMinuteTaskIsMarkerNotFakeDuration() {
        val plan = HorizonLayout.plan(listOf(0, 60), availW = 484f, gap = gap, minMarkerW = minW)
        assertTrue(plan[0].isMinMarker)
        assertEquals(minW, plan[0].width, 0.01f)
        assertEquals(470f, plan[1].width, 0.5f)
    }

    @Test
    fun manyTasks_degradeToHonestOverflow_neverExceedCanvas() {
        val minutes = List(100) { 25 }
        val avail = 360f
        val plan = HorizonLayout.plan(minutes, availW = avail, gap = gap, minMarkerW = minW)
        val total = plan.sumOf { it.width.toDouble() } + gap * (plan.size - 1)
        assertTrue("total $total must fit avail $avail", total <= avail + 0.5)
        val overflow = plan.last()
        assertTrue(overflow.overflowCount > 0)
        assertEquals(100, plan.count { it.overflowCount == 0 } + overflow.overflowCount)
        // Overflow minutes are the honest sum of the aggregated tail.
        val visibleCount = plan.size - 1
        assertEquals(25 * overflow.overflowCount, overflow.minutes)
        assertEquals(visibleCount, plan.count { it.taskIndex >= 0 })
    }

    @Test
    fun emptyAndSingle_areSafe() {
        assertTrue(HorizonLayout.plan(emptyList(), 400f, gap, minW).isEmpty())
        val single = HorizonLayout.plan(listOf(45), 400f, gap, minW)
        assertEquals(1, single.size)
        assertEquals(400f, single[0].width, 0.5f)
    }
}
