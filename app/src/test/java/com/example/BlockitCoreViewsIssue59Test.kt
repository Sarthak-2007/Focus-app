package com.example

import android.view.View
import com.example.ui.views.RotaryGradientMath
import com.example.ui.views.RotaryWheelView
import com.example.ui.views.SlideToStartView
import com.example.ui.views.inkWithAlpha
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode

/**
 * Issue #59 (AUDIT-R1 blockit-core-views) regression suite.
 * Pure parts run on the JVM; view lifecycle parts run Robolectric.
 */
class Issue59PureTest {

    @Test
    fun lampCurvePeaksAtLampAndFloorsOpposite() {
        assertEquals(1f, RotaryGradientMath.socketSN(0f), 0.001f)
        assertEquals(0.16f / 0.82f, RotaryGradientMath.socketSN(180f), 0.001f)
        assertEquals(34, RotaryGradientMath.socketBlackAlpha(180f))
        assertEquals(7, RotaryGradientMath.socketWhiteAlpha(180f))
        assertEquals(0, RotaryGradientMath.socketBlackAlpha(0f))
        assertEquals(36, RotaryGradientMath.socketWhiteAlpha(0f))
    }

    @Test
    fun chamferAlphaDiesOutsideLampWindow() {
        assertEquals(0, RotaryGradientMath.chamferAlpha(0f))
        assertTrue(RotaryGradientMath.chamferAlpha(155.2f) > 200)
    }

    @Test
    fun inkWithAlphaScalesOnlyAlpha() {
        val out = inkWithAlpha(0x80FF0000.toInt(), 0.5f)
        assertEquals(0x40, out ushr 24)
        assertEquals(0xFF0000, out and 0x00FFFFFF)
        assertEquals(0, inkWithAlpha(0x80FF0000.toInt(), 0f) ushr 24)
    }
}

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class Issue59ViewTest {

    private fun rotary(): RotaryWheelView {
        val ctx = RuntimeEnvironment.getApplication()
        ctx.resources.displayMetrics.density = 3.0f
        val v = RotaryWheelView(ctx)
        v.measure(
            View.MeasureSpec.makeMeasureSpec(864, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(864, View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 864, 864)
        return v
    }

    private fun slide(): SlideToStartView {
        val ctx = RuntimeEnvironment.getApplication()
        ctx.resources.displayMetrics.density = 3.0f
        val v = SlideToStartView(ctx)
        v.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(216, View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 216)
        return v
    }

    private fun detach(v: View) {
        val m = View::class.java.getDeclaredMethod("onDetachedFromWindow")
        m.isAccessible = true
        m.invoke(v)
    }

    // Finding 1: detach cancels everything, starts nothing, recycles tracker.
    @Test
    fun rotaryDetachCancelsAnimatorsAndStartsNone() {
        val v = rotary()
        v.studyBlocksCount = 4 // starts closureAnimator
        assertTrue(v.closureAnimatorForTest != null)
        // Hub dip via centre touch-down.
        val down = android.view.MotionEvent.obtain(0L, 0L, android.view.MotionEvent.ACTION_DOWN, 432f, 432f, 0)
        v.onTouchEvent(down)
        down.recycle()
        assertTrue(v.hubPressAnimatorForTest != null)
        detach(v)
        assertNull(v.closureAnimatorForTest)
        assertNull(v.hubPressAnimatorForTest)
        assertFalse(v.velocityTrackerAliveForTest)
        assertEquals(0f, v.hubPressDepthForTest, 0f)
    }

    // Finding 5: spring-back onEnd fires exactly once on natural end, never on cancel.
    @Test
    fun slideSpringBackCancelIsSilentAndEndFiresOnce() {
        val v = slide()
        var ends = 0
        v.animateSpringBackForTest { ends++ }
        v.springBackAnimatorForTest!!.cancel()
        assertEquals(0, ends)
        v.animateSpringBackForTest { ends++ }
        v.resetToRest() // cancels silently
        assertEquals(0, ends)
        v.animateSpringBackForTest { ends++ }
        v.springBackAnimatorForTest!!.end()
        assertEquals(1, ends)
    }

    // Finding 4: measure honours spec modes.
    @Test
    fun measureRespectsAtMostAndUnspecified() {
        val ctx = RuntimeEnvironment.getApplication()
        ctx.resources.displayMetrics.density = 3.0f
        val wheel = RotaryWheelView(ctx)
        wheel.measure(
            View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.AT_MOST)
        )
        assertEquals(300, wheel.measuredWidth)
        assertEquals(300, wheel.measuredHeight)
        wheel.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        assertEquals(864, wheel.measuredWidth) // 288dp desired at density 3
        val slide = SlideToStartView(ctx)
        slide.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        assertEquals(216, slide.measuredHeight) // 72dp desired
    }

    // Finding 4: saved state round-trips the wheel.
    @Test
    fun rotarySavedStateRoundTrips() {
        val v = rotary()
        v.minutesValue = 90
        v.studyBlocksCount = 3
        val save = View::class.java.getDeclaredMethod("onSaveInstanceState")
        save.isAccessible = true
        val state = save.invoke(v) as android.os.Parcelable
        val fresh = rotary()
        val restore = View::class.java.getDeclaredMethod(
            "onRestoreInstanceState", android.os.Parcelable::class.java
        )
        restore.isAccessible = true
        restore.invoke(fresh, state)
        assertEquals(90, fresh.minutesValue)
        assertEquals(3, fresh.studyBlocksCount)
    }

    // Finding 4: accessibility — labelled, clickable, performClick works.
    @Test
    fun accessibilitySurfaceExists() {
        val wheel = rotary()
        assertTrue(wheel.isClickable)
        assertTrue(wheel.contentDescription.toString().contains("50 minutes"))
        var cycled = 0
        wheel.onCycleBlocksCount = { cycled++ }
        assertTrue(wheel.performClick())
        assertEquals(1, cycled)

        val slide = slide()
        assertTrue(slide.isClickable)
        assertEquals("Slide to start", slide.contentDescription.toString())
        var pressStarts = 0
        slide.onPressStart = { pressStarts++ }
        assertTrue(slide.performClick())
        assertEquals(1, pressStarts)
        assertEquals("MESH", slide.modeNameForTest)
    }
}

class Issue59Tier1PureTest {

    @Test
    fun tickClassesAre6Major6Mid23Minor() {
        var counts = intArrayOf(0, 0, 0)
        for (m in 10..180 step 5) counts[com.example.ui.views.RotaryBatchMath.tickClass(m)]++
        assertEquals(23, counts[0])
        assertEquals(6, counts[1])
        assertEquals(6, counts[2])
    }

    @Test
    fun coreAlphaPlateauAndFalloff() {
        assertEquals(25, com.example.ui.views.RotaryBatchMath.coreAlpha(315f, night = false))
        assertEquals(20, com.example.ui.views.RotaryBatchMath.coreAlpha(315f, night = true))
        assertEquals(25, com.example.ui.views.RotaryBatchMath.coreAlpha(324f, night = false))
        assertEquals(0, com.example.ui.views.RotaryBatchMath.coreAlpha(281f, night = false))
        assertEquals(0, com.example.ui.views.RotaryBatchMath.coreAlpha(349f, night = false))
    }

    @Test
    fun knurlFlareTeethStayDimmer() {
        val broken = com.example.ui.views.RotaryBatchMath.knurlOddAlpha(51f, night = false, flare = 0.62f)
        val normal = com.example.ui.views.RotaryBatchMath.knurlOddAlpha(51f, night = false, flare = 1.0f)
        assertTrue(broken < normal)
        assertTrue(broken in 0..255)
    }
}

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class Issue59CensusTest {

    class CountingCanvas(bmp: android.graphics.Bitmap) : android.graphics.Canvas(bmp) {
        var total = 0
        override fun drawCircle(cx: Float, cy: Float, r: Float, p: android.graphics.Paint) { total++; super.drawCircle(cx, cy, r, p) }
        override fun drawArc(r: android.graphics.RectF, a: Float, b: Float, c: Boolean, p: android.graphics.Paint) { total++; super.drawArc(r, a, b, c, p) }
        override fun drawLine(a: Float, b: Float, c: Float, d: Float, p: android.graphics.Paint) { total++; super.drawLine(a, b, c, d, p) }
        override fun drawPath(path: android.graphics.Path, p: android.graphics.Paint) { total++; super.drawPath(path, p) }
        override fun drawText(t: String, x: Float, y: Float, p: android.graphics.Paint) { total++; super.drawText(t, x, y, p) }
        override fun drawRoundRect(r: android.graphics.RectF, a: Float, b: Float, p: android.graphics.Paint) { total++; super.drawRoundRect(r, a, b, p) }
        override fun drawRect(a: Float, b: Float, c: Float, d: Float, p: android.graphics.Paint) { total++; super.drawRect(a, b, c, d, p) }
    }

    @Test
    fun rotaryDrawCensusIsBatched() {
        val ctx = RuntimeEnvironment.getApplication()
        ctx.resources.displayMetrics.density = 3.0f
        val v = RotaryWheelView(ctx)
        v.measure(
            View.MeasureSpec.makeMeasureSpec(864, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(864, View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 864, 864)
        val bmp = android.graphics.Bitmap.createBitmap(864, 864, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = CountingCanvas(bmp)
        v.draw(canvas)
        // Pre-fix scene: ~519 calls. Tier-1 target (architect): ~65.
        assertTrue("census=${canvas.total}", canvas.total in 30..85)
    }
}
