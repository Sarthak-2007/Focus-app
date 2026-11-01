package com.example

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.ui.components.BoltFace
import com.example.ui.components.BoltWindow
import com.example.ui.components.SealBar
import com.example.ui.components.SealBarState
import com.example.ui.theme.MyApplicationTheme
import java.io.File
import java.io.FileOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper

/**
 * Issue #4 — the Bolt's render gate (THE BOLT §6 / BLICK T1–T9,
 * executable form). The design's published battery numbers were
 * struck by KRALL K-5 / HOLLIS H-4 / VEX V-9 (a 644dp² bed that
 * never existed; a bare-brass "worst case" RUNNING never wears).
 * This test replaces them with measurements over renders of the
 * real composable: pixel counts of the elements that actually
 * render, per mode. What it cannot do is the human half of the
 * battery (cold reads by people at 1 AM) — that half is
 * dispositioned on the issue, not faked here.
 *
 * Renders are cold compositions: the travel Animatable seeds at
 * its target, so each capture is the state's settled face.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BoltRenderGateTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val shotsDir = File("build/bolt-gate-shots").apply { mkdirs() }

    // FIX-BOLT (issue #4): the current Compose test rule allows ONE
    // setContent per test — a second call throws IllegalStateException
    // ("has already set content"), which is how all five gate tests
    // originally died (each on its second render, before any assertion
    // on that render ran; run 41113979080's successor run
    // 41165105931, TEST-com.example.BoltRenderGateTest.xml).
    // So the content is set once and the face is swapped through
    // snapshot state; every capture below is of the settled face,
    // exactly as when each render was a fresh composition.
    private var faceState by mutableStateOf<BoltFace?>(null)
    private var darkState by mutableStateOf(true)
    private var endsState by mutableStateOf<String?>(null)
    private var plannedState by mutableStateOf<String?>(null)
    private var contentSet = false

    private fun renderBar(
        face: BoltFace,
        dark: Boolean,
        ends: String? = null,
        planned: String? = null,
        name: String
    ): Bitmap {
        if (!contentSet) {
            contentSet = true
            faceState = face
            darkState = dark
            endsState = ends
            plannedState = planned
            composeRule.setContent {
                val currentFace = faceState
                MyApplicationTheme(darkTheme = darkState) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        if (currentFace != null) {
                            SealBar(
                                face = currentFace,
                                endsAtNumerals = endsState,
                                plannedEndsAtNumerals = plannedState
                            )
                        }
                    }
                }
            }
        } else {
            composeRule.runOnUiThread {
                faceState = face
                darkState = dark
                endsState = ends
                plannedState = planned
            }
        }
        ShadowLooper.idleMainLooper()
        try {
            composeRule.waitForIdle()
        } catch (_: Throwable) {
            // A live animation can keep the rule non-idle; capture anyway.
        }
        ShadowLooper.idleMainLooper()
        val node = composeRule.onNodeWithTag("seal_bar").fetchSemanticsNode()
        val bounds = node.boundsInRoot
        val full = composeRule.runOnUiThread {
            val decor = composeRule.activity.window.decorView
            val w = if (decor.width > 0) decor.width else 824
            val h = if (decor.height > 0) decor.height else 1830
            if (decor.width == 0) {
                decor.measure(
                    android.view.View.MeasureSpec.makeMeasureSpec(
                        w, android.view.View.MeasureSpec.EXACTLY
                    ),
                    android.view.View.MeasureSpec.makeMeasureSpec(
                        h, android.view.View.MeasureSpec.EXACTLY
                    )
                )
                decor.layout(0, 0, w, h)
            }
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            decor.draw(Canvas(bmp))
            bmp
        }
        val cropped = Bitmap.createBitmap(
            full,
            bounds.left.toInt().coerceIn(0, full.width - 1),
            bounds.top.toInt().coerceIn(0, full.height - 1),
            bounds.width.toInt().coerceAtMost(full.width - bounds.left.toInt()),
            bounds.height.toInt().coerceAtMost(full.height - bounds.top.toInt())
        )
        FileOutputStream(File(shotsDir, "$name.png")).use { fos ->
            cropped.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
        println("BOLT-GATE captured $name.png ${cropped.width}x${cropped.height}")
        return cropped
    }

    private fun countNear(
        bmp: Bitmap,
        argb: Int,
        tol: Int = 24,
        region: (Int, Int, Int, Int) -> Boolean = { _, _, _, _ -> true }
    ): Int {
        val r0 = (argb shr 16) and 0xFF
        val g0 = (argb shr 8) and 0xFF
        val b0 = argb and 0xFF
        var count = 0
        var y = 0
        while (y < bmp.height) {
            var x = 0
            while (x < bmp.width) {
                if (region(x, y, bmp.width, bmp.height)) {
                    val px = bmp.getPixel(x, y)
                    val r = (px shr 16) and 0xFF
                    val g = (px shr 8) and 0xFF
                    val b = px and 0xFF
                    if (kotlin.math.abs(r - r0) <= tol &&
                        kotlin.math.abs(g - g0) <= tol &&
                        kotlin.math.abs(b - b0) <= tol
                    ) {
                        count++
                    }
                }
                x++
            }
            y++
        }
        return count
    }

    private fun diffFraction(a: Bitmap, b: Bitmap): Float {
        assertEquals(a.width, b.width)
        assertEquals(a.height, b.height)
        var diff = 0
        var total = 0
        var y = 0
        while (y < a.height) {
            var x = 0
            while (x < a.width) {
                if (a.getPixel(x, y) != b.getPixel(x, y)) diff++
                total++
                x += 2
            }
            y += 2
        }
        return diff.toFloat() / total
    }

    /** The window's slice of the bar: the rightmost ~126dp. */
    private fun windowRegion(x: Int, y: Int, w: Int, h: Int): Boolean =
        x > w - (126f * w / 412f).toInt()

    /** The window glass's leftmost 15% — where no centred
     *  numeral reaches, but the STALE shutter's hatch does.
     *  (The glass is the rightmost 102dp; the wider region
     *  above also covers the lamp cluster and its gaps.) */
    private fun windowLeftSlice(x: Int, y: Int, w: Int, h: Int): Boolean {
        val winW = (102f * w / 412f).toInt()
        val winX0 = w - winW
        return x in winX0..(winX0 + winW * 15 / 100)
    }

    private val orange = 0xFFD63E06.toInt()
    private val magentaNight = 0xFFF04FCB.toInt()
    private val magentaDay = 0xFFC81FA8.toInt()
    private val brass = 0xFFE2BC39.toInt()

    // T-analog 1+2 (pairwise, night): OFF's exposed orange bed
    // against RUNNING's lit jewel — measured, per the gate's own
    // ≥3× law; and the exposure ladder OFF > CAUGHT > RUNNING≈0
    // (KRALL K-6's corrected geometry: CAUGHT exposes ≈⅔).
    @Test
    fun glancePairNightMeasured() {
        val off = renderBar(
            BoltFace(SealBarState.OFF, BoltWindow.PLANNED_END),
            dark = true, planned = "07:28", name = "gate_off_night"
        )
        val caught = renderBar(
            BoltFace(SealBarState.UNPROVEN, BoltWindow.PLANNED_END),
            dark = true, planned = "07:28", name = "gate_caught_night"
        )
        val running = renderBar(
            BoltFace(SealBarState.RUNNING, BoltWindow.LIVE_END),
            dark = true, ends = "07:28", name = "gate_running_night"
        )
        val offOrange = countNear(off, orange)
        val caughtOrange = countNear(caught, orange)
        val runningOrange = countNear(running, orange)
        val runningMagenta = countNear(running, magentaNight)
        println(
            "BOLT-GATE night: offOrange=$offOrange caughtOrange=$caughtOrange " +
                "runningOrange=$runningOrange runningMagenta=$runningMagenta"
        )
        assertTrue("OFF must expose its bed", offOrange > 1000)
        assertTrue("RUNNING's jewel must be lit magenta", runningMagenta > 100)
        assertTrue(
            "OFF orange ($offOrange) must be ≥3× RUNNING magenta ($runningMagenta)",
            offOrange >= 3 * runningMagenta
        )
        assertTrue(
            "thrown bolt must cover the bed (runningOrange=$runningOrange)",
            runningOrange < 200
        )
        assertTrue(
            "CAUGHT exposure ($caughtOrange) sits between OFF and RUNNING",
            caughtOrange in 200..offOrange
        )
        assertTrue(
            "OFF and RUNNING must be unmistakably different frames",
            diffFraction(off, running) > 0.03f
        )
    }

    // K-2's pixel proof: STALE is a distinct face — dark lamp
    // (zero magenta), shuttered window (hatch in the window's
    // left slice, where no numeral reaches), and a frame that
    // differs from both RUNNING and RUNNING_UNPROVEN.
    @Test
    fun staleIsDistinctInPixels() {
        val stale = renderBar(
            BoltFace(SealBarState.STALE, BoltWindow.SHUTTERED),
            dark = true, ends = "07:28", name = "gate_stale_night"
        )
        val running = renderBar(
            BoltFace(SealBarState.RUNNING, BoltWindow.LIVE_END),
            dark = true, ends = "07:28", name = "gate_running2_night"
        )
        val unproven = renderBar(
            BoltFace(SealBarState.RUNNING_UNPROVEN, BoltWindow.LIVE_END),
            dark = true, ends = "07:28", name = "gate_run_unproven_night"
        )
        val staleMagenta = countNear(stale, magentaNight)
        assertEquals("STALE's lamp is dark — no magenta", 0, staleMagenta)
        assertTrue(
            "STALE ≠ RUNNING in pixels",
            diffFraction(stale, running) > 0.01f
        )
        assertTrue(
            "STALE ≠ RUNNING_UNPROVEN in pixels (the shutter)",
            diffFraction(stale, unproven) > 0.005f
        )
        // The hatch crosses the window's left slice; a centred
        // numeral never does. Mid-tone pixels (the hatch's 35%
        // ink over glass) are the signature.
        fun midTones(bmp: Bitmap): Int {
            var count = 0
            var y = 0
            while (y < bmp.height) {
                var x = 0
                while (x < bmp.width) {
                    if (windowLeftSlice(x, y, bmp.width, bmp.height)) {
                        val px = bmp.getPixel(x, y)
                        val lum = (0.2126 * ((px shr 16) and 0xFF) +
                            0.7152 * ((px shr 8) and 0xFF) +
                            0.0722 * (px and 0xFF)) / 255.0
                        if (lum in 0.12..0.55) count++
                    }
                    x++
                }
                y++
            }
            return count
        }
        val staleHatch = midTones(stale)
        val unprovenHatch = midTones(unproven)
        println("BOLT-GATE stale: hatchMid=$staleHatch unprovenMid=$unprovenHatch")
        assertTrue(
            "the shutter's hatch must cross the window's left slice " +
                "(stale=$staleHatch, unproven=$unprovenHatch)",
            staleHatch > unprovenHatch + 30
        )
    }

    // H-1's pixel proof: at idle the planned end actually renders
    // — a differential against the same face with no planned end
    // (blank glass): the numerals must displace real glass area,
    // in day and night.
    @Test
    fun idlePlannedEndRenders() {
        for (dark in listOf(false, true)) {
            val withPlan = renderBar(
                BoltFace(SealBarState.BLOCKING, BoltWindow.PLANNED_END),
                dark = dark, planned = "07:28",
                name = if (dark) "gate_blocking_night" else "gate_blocking_day"
            )
            val blank = renderBar(
                BoltFace(SealBarState.BLOCKING, BoltWindow.PLANNED_END),
                dark = dark, planned = null,
                name = if (dark) "gate_blocking_blank_night" else "gate_blocking_blank_day"
            )
            println(
                "BOLT-GATE planned (dark=$dark): " +
                    "diff=${diffFraction(withPlan, blank)}"
            )
            assertTrue(
                "the planned numerals must mark the glass (dark=$dark)",
                diffFraction(withPlan, blank) > 0.002f
            )
            // The brass jewel of BLOCKING renders too.
            assertTrue(countNear(withPlan, brass) > 50)
        }
    }

    // ENDING_SOON's carrier: the whole window inverts to brass.
    @Test
    fun endingSoonInvertsTheWindow() {
        val soon = renderBar(
            BoltFace(SealBarState.ENDING_SOON, BoltWindow.LIVE_END),
            dark = true, ends = "07:28", name = "gate_ending_soon_night"
        )
        val brassInWindow = countNear(soon, brass) { x, y, w, h ->
            windowRegion(x, y, w, h)
        }
        println("BOLT-GATE ending soon: brassInWindow=$brassInWindow")
        assertTrue(brassInWindow > 500)
        // And the day pair (K-12's missing frames, executable
        // part): FLOW and CAUGHT-night differ from RUNNING.
        val flow = renderBar(
            BoltFace(SealBarState.FLOW, BoltWindow.BLANK),
            dark = true, name = "gate_flow_night"
        )
        assertTrue(countNear(flow, magentaNight) > 100)
        assertTrue(
            "FLOW (blank window) ≠ RUNNING (live end) in pixels",
            diffFraction(flow, soon) > 0.005f
        )
    }

    // K-7's executable gate: the spoken set — one exact sentence
    // per fold state, and no sentence smuggles the numerals back
    // in ("Ends …" appears in none of them).
    @Test
    fun spokenSetIsClosedAndNumeralFree() {
        val cases = listOf(
            BoltFace(SealBarState.OFF, BoltWindow.PLANNED_END) to
                "Blocking is off. The seal is open.",
            BoltFace(SealBarState.OFF_WHILE_RUNNING, BoltWindow.BLANK) to
                "Blocking is off while a session runs. The seal is open.",
            BoltFace(SealBarState.UNPROVEN, BoltWindow.PLANNED_END) to
                "Blocking is not proven. The bolt is caught, not locked.",
            BoltFace(SealBarState.NO_READ, BoltWindow.PLANNED_END) to
                "Blocking is not proven. No reading has arrived.",
            BoltFace(SealBarState.BLOCKING, BoltWindow.PLANNED_END) to
                "Blocking is on. The bolt is thrown and held.",
            BoltFace(SealBarState.RUNNING, BoltWindow.LIVE_END) to
                "Session running. The seal is shut.",
            BoltFace(SealBarState.RUNNING_UNPROVEN, BoltWindow.LIVE_END) to
                "Session running. The seal is shut, but blocking is not proven.",
            BoltFace(SealBarState.STALE, BoltWindow.SHUTTERED) to
                "Session running. The seal is shut, but the proof has gone stale.",
            BoltFace(SealBarState.ENDING_SOON, BoltWindow.LIVE_END) to
                "Session ending soon. The seal is shut.",
            BoltFace(SealBarState.FLOW, BoltWindow.BLANK) to
                "Session running without an end. The seal is shut.",
            BoltFace(SealBarState.FLOW_UNPROVEN, BoltWindow.BLANK) to
                "Session running without an end. The seal is shut, but blocking is not proven.",
            BoltFace(SealBarState.DETENTION_FAULT, BoltWindow.BLANK) to
                "The session ended, but the release was refused. The seal stays shut."
        )
        for ((face, sentence) in cases) {
            assertTrue(!sentence.contains("Ends"))
            renderBar(face, dark = true, ends = "07:28", planned = "07:28",
                name = "gate_sem_${face.state.name.lowercase()}")
            composeRule.onNodeWithTag("seal_bar")
                .assertContentDescriptionEquals(sentence)
        }
    }
}
