package com.example

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import com.example.ui.views.RotaryWheelView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

/**
 * Render check for Slice C (tetrad hub): draws RotaryWheelView directly
 * at counts 4 and 8, day and night.
 *
 * Issue #66 (audit R1 tests-2) truth fix: this class previously had ZERO
 * assertions and wrote PNGs to a hardcoded absolute path on the author's
 * machine (`/home/hatch/.../shots/tetrad`). It now (a) writes only under
 * the module build dir (or $MONOLITH_SHOTS_DIR), and (b) asserts real,
 * failable structure on every render: exact size, a non-blank wheel
 * (many distinct colours / non-background pixels), day != night pixels,
 * and count 4 != count 8 pixels. A visual regression that blanks or
 * flattens the wheel, or collapses the day/night or count distinction,
 * goes red.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TetradRenderTest {

    private val outDir: File =
        File(System.getenv("MONOLITH_SHOTS_DIR")?.let { "$it/tetrad" } ?: "build/outputs/tetrad-render")

    private fun render(count: Int, night: Boolean, name: String): Bitmap {
        val ctx = RuntimeEnvironment.getApplication()
        // The wheel's proportions are only true at the real layout size:
        // the view is 288dp tall on the Deck. Render at density 3.0 and
        // 864px (= 288dp) so dp() geometry matches the device.
        ctx.resources.displayMetrics.density = 3.0f
        val view = RotaryWheelView(ctx)
        view.isNightMode = night
        view.minutesValue = 50
        view.studyBlocksCount = count
        val size = 864
        view.measure(
            View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, size, size)
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val bg = if (night) 0xFF171412.toInt() else 0xFFEDE8E2.toInt()
        canvas.drawColor(bg)
        view.draw(canvas)
        outDir.mkdirs()
        val out = File(outDir, name)
        FileOutputStream(out).use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue("render $name must be written", out.isFile && out.length() > 1_000)
        return bmp
    }

    private fun distinctAndNonBg(bmp: Bitmap, bg: Int): Pair<Int, Int> {
        val colours = HashSet<Int>()
        var nonBg = 0
        var y = 0
        while (y < bmp.height) {
            var x = 0
            while (x < bmp.width) {
                val px = bmp.getPixel(x, y)
                colours += px
                if (px != bg) nonBg++
                x += 4
            }
            y += 4
        }
        return colours.size to nonBg
    }

    private fun differs(a: Bitmap, b: Bitmap): Int {
        var diff = 0
        var y = 0
        while (y < a.height) {
            var x = 0
            while (x < a.width) {
                if (a.getPixel(x, y) != b.getPixel(x, y)) diff++
                x += 4
            }
            y += 4
        }
        return diff
    }

    @Test
    fun tetradClosures() {
        val dayBg = 0xFFEDE8E2.toInt()
        val nightBg = 0xFF171412.toInt()
        val t4Day = render(4, false, "t4-day.png")
        val t8Day = render(8, false, "t8-day.png")
        val t4Night = render(4, true, "t4-night.png")
        val t8Night = render(8, true, "t8-night.png")
        val t1Day = render(1, false, "t1-day.png")

        for ((bmp, bg, label) in listOf(
            Triple(t4Day, dayBg, "t4-day"), Triple(t8Day, dayBg, "t8-day"),
            Triple(t4Night, nightBg, "t4-night"), Triple(t8Night, nightBg, "t8-night"),
            Triple(t1Day, dayBg, "t1-day")
        )) {
            assertEquals("$label width", 864, bmp.width)
            assertEquals("$label height", 864, bmp.height)
            val (distinct, nonBg) = distinctAndNonBg(bmp, bg)
            assertTrue("$label must not be blank (distinct colours was $distinct)", distinct > 10)
            assertTrue("$label wheel must draw over the background (non-bg samples was $nonBg)", nonBg > 500)
        }
        assertTrue("day and night renders must differ", differs(t4Day, t4Night) > 1_000)
        assertTrue("count 4 and count 8 renders must differ", differs(t4Day, t8Day) > 100)
        assertTrue("count 1 and count 4 renders must differ", differs(t1Day, t4Day) > 100)
    }
}
