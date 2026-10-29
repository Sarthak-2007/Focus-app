package com.example

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import com.example.ui.views.RotaryWheelView
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

/**
 * Ren's render check for Slice C (tetrad hub): draws RotaryWheelView
 * directly at counts 4 and 8, day and night, so the closure bars can be
 * judged in a picture - the screen harness only seeds the default 2.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TetradRenderTest {

    private fun render(count: Int, night: Boolean, name: String) {
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
        canvas.drawColor(if (night) 0xFF171412.toInt() else 0xFFEDE8E2.toInt())
        view.draw(canvas)
        val dir = File("/home/hatch/workspace/clone-inspect/shots/tetrad")
        dir.mkdirs()
        FileOutputStream(File(dir, name)).use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test
    fun tetradClosures() {
        render(4, false, "t4-day.png")
        render(8, false, "t8-day.png")
        render(4, true, "t4-night.png")
        render(8, true, "t8-night.png")
        render(1, false, "t1-day.png")
    }
}
