package com.example

import android.view.View
import com.example.ui.views.RotaryBatchMath
import com.example.ui.views.RotaryGradientMath
import com.example.ui.views.RotaryWheelView
import com.example.ui.views.SlideToStartView
import com.example.ui.views.inkWithAlpha
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode

/**
 * Issue #59 (AUDIT-R1 blockit-core-views) regression suite for the
 * re-ported cure on current main:
 *  F1 residue — Rotary detach cancels the tetrad closure animator.
 *  F2/F3 — Rotary onDraw is batched (draw-call census executes a real
 *          draw) and the lamp/batch math reproduces the original
 *          per-segment colour formulas exactly.
 *  F5 — Slide spring-back end callback: natural end fires exactly
 *          once; any cancel (direct, reset, detach) fires zero times.
 * (F4 was already fixed on main by PR #455 — not retested here.)
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

    @Test
    fun tickClassesAre6Major6Mid23Minor() {
        val counts = intArrayOf(0, 0, 0)
        for (m in 10..180 step 5) counts[RotaryBatchMath.tickClass(m)]++
        assertEquals(23, counts[0])
        assertEquals(6, counts[1])
        assertEquals(6, counts[2])
    }

    @Test
    fun coreAlphaPlateauAndFalloff() {
        assertEquals(25, RotaryBatchMath.coreAlpha(315f, night = false))
        assertEquals(20, RotaryBatchMath.coreAlpha(315f, night = true))
        assertEquals(25, RotaryBatchMath.coreAlpha(324f, night = false))
        assertEquals(0, RotaryBatchMath.coreAlpha(281f, night = false))
        assertEquals(0, RotaryBatchMath.coreAlpha(349f, night = false))
    }

    @Test
    fun knurlFlareTeethStayDimmer() {
        val broken = RotaryBatchMath.knurlOddAlpha(51f, night = false, flare = 0.62f)
        val normal = RotaryBatchMath.knurlOddAlpha(51f, night = false, flare = 1.0f)
        assertTrue(broken < normal)
        assertTrue(broken in 0..255)
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

    // F1 residue: detach cancels the closure animator.
    @Test
    fun rotaryDetachCancelsClosureAnimator() {
        val v = rotary()
        v.studyBlocksCount = 4 // tetrad closure starts closureAnimator
        assertNotNull(v.closureAnimatorForTest)
        detach(v)
        assertFalse(v.closureAnimatorForTest!!.isRunning)
    }

    // F5: natural end = exactly 1 fire.
    @Test
    fun slideSpringBackNaturalEndFiresExactlyOnce() {
        val v = slide()
        var ends = 0
        v.animateSpringBackForTest { ends++ }
        v.springBackAnimatorForTest!!.end()
        assertEquals(1, ends)
        // A second end dispatch on the consumed animator fires nothing.
        v.springBackAnimatorForTest?.end()
        assertEquals(1, ends)
    }

    // F5: direct animator cancel = 0 fires (cancel dispatches end too).
    @Test
    fun slideSpringBackDirectCancelFiresZero() {
        val v = slide()
        var ends = 0
        v.animateSpringBackForTest { ends++ }
        v.springBackAnimatorForTest!!.cancel()
        assertEquals(0, ends)
    }

    // F5: view-owned cancel sites (resetToRest, detach) = 0 fires.
    @Test
    fun slideSpringBackResetAndDetachFireZero() {
        val v = slide()
        var ends = 0
        v.animateSpringBackForTest { ends++ }
        v.resetToRest()
        assertEquals(0, ends)
        v.animateSpringBackForTest { ends++ }
        detach(v)
        assertEquals(0, ends)
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

    // F2/F3: a real draw of the wheel must be batched — the pre-fix
    // scene was ~650 segmented calls per frame (census vs main:
    // socket 240 + chamfer ~90 + knurl ~110 + micro-ticks 137 +
    // ticks 35 + hub fan ~34, plus the rest of the scene).
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
        // Second frame: steady state (caches warm) must not grow.
        val first = canvas.total
        v.draw(canvas)
        assertTrue("census=$first", first in 20..120)
        assertEquals(first * 2, canvas.total)
    }
}
