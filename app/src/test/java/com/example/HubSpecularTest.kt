package com.example

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import com.example.ui.theme.MonolithCurves
import com.example.ui.views.RotaryWheelView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode
import kotlin.math.cos
import kotlin.math.sin

/**
 * Slice E, E5a (Nabe §7): the D6 hub light and its press travel.
 *
 * §7.1 — the rest core EXISTS, proven at night where the merged
 * staircase gives it measurable lift (cap #4A423C, tint #FFF6EC
 * at 8%: plateau ≈ (88, 80, 74) against the flat (74, 66, 60)).
 * The day lift over #FAF6F0 is sub-quantisation against the
 * shipped tint — its existence proof is the roll (§7.3's travel),
 * and DEVICE_PASS_E5A remains the device gate; no still-pixel
 * claim is made for day here that arithmetic cannot support.
 * §7.2 — count changes move NO core parameter: the sample
 * vectors are identical at counts 1, 2, 3, 4, 8 and FLOW.
 * §7.3 — the 45ms dip / 160ms seat pair, driven deterministically
 * (the animators' fractions are set directly; endpoints and
 * configuration are asserted, never intermediate frames — Nabe R5).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class HubSpecularTest {

    private fun newView(count: Int = 2, minutes: Int = 50, night: Boolean = false): RotaryWheelView {
        val ctx = RuntimeEnvironment.getApplication()
        ctx.resources.displayMetrics.density = 3.0f
        val view = RotaryWheelView(ctx)
        view.isNightMode = night
        view.minutesValue = minutes
        view.studyBlocksCount = count
        val size = 864
        view.measure(
            View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, size, size)
        return view
    }

    private fun render(view: RotaryWheelView, night: Boolean): Bitmap {
        val bmp = Bitmap.createBitmap(864, 864, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(if (night) 0xFF171412.toInt() else 0xFFEDE8E2.toInt())
        view.draw(canvas)
        return bmp
    }

    /** Bearing (0 = north, clockwise) + radius → pixel, the
     *  wells' own convention. Centre (432, 432); innerHubR ≈ 192;
     *  sample radii 67 / 163 clear the R40 ring band (105–135). */
    private fun sample(bmp: Bitmap, bearingDeg: Double, r: Double): Int {
        val x = (432 + r * sin(Math.toRadians(bearingDeg))).toInt()
        val y = (432 - r * cos(Math.toRadians(bearingDeg))).toInt()
        return bmp.getPixel(x, y)
    }

    @Test
    fun nightCoreExistsWithMergedNumbers() {
        val bmp = render(newView(night = true), night = true)
        val plateau = sample(bmp, 315.0, 163.0)
        val flat = sample(bmp, 135.0, 163.0)
        // Flat cap: the sheet tone, untouched by the fan (Δ = 180°).
        assertEquals(74, Color.red(flat))
        assertEquals(66, Color.green(flat))
        assertEquals(60, Color.blue(flat))
        // Plateau: mix(#4A423C, #FFF6EC, 0.08) = (88.5, 80.4, 74.1).
        assertTrue("plateau R was ${Color.red(plateau)}", Color.red(plateau) in 84..93)
        assertTrue("plateau G was ${Color.green(plateau)}", Color.green(plateau) in 76..85)
        assertTrue("plateau B was ${Color.blue(plateau)}", Color.blue(plateau) in 70..79)
        assertTrue(Color.red(plateau) > Color.red(flat) + 8)
        // Inside the ±9° plateau the lift holds; at Δ = 21° the
        // cos² falloff sits strictly between cap and peak.
        val plateauEdge = sample(bmp, 323.0, 163.0)
        assertEquals(Color.red(plateau), Color.red(plateauEdge))
        val falloff = sample(bmp, 336.0, 163.0)
        assertTrue(Color.red(falloff) in (Color.red(flat) + 2)..(Color.red(plateau) - 2))
    }

    @Test
    fun countChangesMoveNoCoreParameter() {
        val bearings = listOf(315.0, 135.0, 45.0, 225.0)
        val radii = listOf(67.0, 163.0)
        fun vector(count: Int, minutes: Int): List<Int> {
            val bmp = render(newView(count = count, minutes = minutes), night = false)
            return bearings.flatMap { b -> radii.map { r -> sample(bmp, b, r) } }
        }
        val base = vector(2, 50)
        for (count in listOf(1, 3, 4, 8)) {
            assertEquals("count $count moved the core", base, vector(count, 50))
        }
        assertEquals("FLOW moved the core", base, vector(2, 5))
    }

    private fun event(action: Int, x: Float, y: Float, time: Long): MotionEvent =
        MotionEvent.obtain(time, time, action, x, y, 0)

    @Test
    fun dipAndSeatPairWithStartFrameIdentity() {
        val view = newView()
        var commits = 0
        view.onCycleBlocksCount = { commits++ }
        view.onSetBlocksCount = { commits++ }

        // DOWN at the centre: the dip starts (45ms, HubPressDip);
        // NO commit fires — the dip never gates the commit and
        // never replaces it.
        view.onTouchEvent(event(MotionEvent.ACTION_DOWN, 432f, 432f, 0L))
        val dip = view.hubPressAnimatorForTest
        assertTrue(dip != null && dip.isRunning)
        assertEquals(45L, dip!!.duration)
        assertTrue(dip.interpolator === MonolithCurves.HubPressDip)
        assertEquals(0, commits)
        dip.setCurrentFraction(1f)
        assertEquals(1f, view.hubPressDepthForTest, 0.01f)
        assertEquals(0, commits)

        // UP at the centre: the commit fires exactly once AND the
        // return (160ms, HubSeatReturn) is already running on the
        // commit frame — return-first ordering.
        view.onTouchEvent(event(MotionEvent.ACTION_UP, 432f, 432f, 50L))
        assertEquals(1, commits)
        val ret = view.hubPressAnimatorForTest
        assertTrue(ret != null && ret.isRunning)
        assertEquals(160L, ret!!.duration)
        assertTrue(ret.interpolator === MonolithCurves.HubSeatReturn)
        ret.setCurrentFraction(1f)
        assertEquals(0f, view.hubPressDepthForTest, 0.01f)
    }

    @Test
    fun fastTapStillCommitsAndDragOffCrossingsDriveTheDip() {
        val view = newView()
        var commits = 0
        view.onCycleBlocksCount = { commits++ }
        view.onSetBlocksCount = { commits++ }

        // Fast tap: release before the dip completes — commit
        // still fires, the return runs from the reached depth.
        view.onTouchEvent(event(MotionEvent.ACTION_DOWN, 432f, 432f, 0L))
        view.onTouchEvent(event(MotionEvent.ACTION_UP, 432f, 432f, 20L))
        assertEquals(1, commits)

        // Drag-off: crossing ×1.1 starts the return with no
        // commit; crossing back restarts the dip; release outside
        // commits nothing.
        view.onTouchEvent(event(MotionEvent.ACTION_DOWN, 432f, 432f, 100L))
        view.hubPressAnimatorForTest?.setCurrentFraction(1f)
        assertEquals(1f, view.hubPressDepthForTest, 0.01f)
        view.onTouchEvent(event(MotionEvent.ACTION_MOVE, 700f, 432f, 120L))
        assertTrue(view.hubPressAnimatorForTest!!.interpolator === MonolithCurves.HubSeatReturn)
        view.onTouchEvent(event(MotionEvent.ACTION_MOVE, 432f, 432f, 140L))
        assertTrue(view.hubPressAnimatorForTest!!.interpolator === MonolithCurves.HubPressDip)
        view.onTouchEvent(event(MotionEvent.ACTION_UP, 700f, 432f, 160L))
        assertEquals("drag-off release must not commit", 1, commits)
    }

    @Test
    fun flowHubVisiblyRests() {
        val view = newView(minutes = 5)
        var commits = 0
        view.onCycleBlocksCount = { commits++ }
        view.onSetBlocksCount = { commits++ }
        view.onTouchEvent(event(MotionEvent.ACTION_DOWN, 432f, 432f, 0L))
        assertEquals(0f, view.hubPressDepthForTest, 0.0f)
        view.onTouchEvent(event(MotionEvent.ACTION_UP, 432f, 432f, 50L))
        assertEquals(0, commits)
    }
}
