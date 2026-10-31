package com.example.service

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Issue #114, finding 1 (Audit R3, moment-of-temptation, CRITICAL):
 * the peek countdown pill the ViewModel's KDoc always promised.
 *
 * Before this object existed, `FloatingPeekPillState` was written
 * and ticked inside MonolithViewModel and collected NOWHERE — the
 * user spent a 2-minute lease with no clock, no warning and no
 * hand-back. This is the pill's face over other apps: a plain-view
 * WindowManager overlay (no Compose runtime needed in a window the
 * activity does not own), driven solely by the ViewModel's pill
 * state, whose seconds are themselves DERIVED from the Room lease
 * end (Truth Car 3, UNA2-06) — this face never counts on its own,
 * so it cannot disagree with the lease it names.
 *
 * The face carries exactly three things: the app's name, the
 * remaining time as M:SS (with an ENDING SOON warning in the last
 * 30 seconds), and an END PEEK button — the hand-back. The button
 * calls [onEndPeekRequest], wired by the ViewModel to the same
 * `end2MinutePeekEarly()` the in-app pill uses, so state, lease
 * row and both faces retire together.
 *
 * If the draw-over-apps grant is missing the overlay silently
 * does not draw; the in-activity Compose pill
 * (PeekCountdownPill) is the fallback face in that world, so a
 * peek is never again clock-less inside Monolith itself.
 *
 * Main-thread confined: every caller is the ViewModel's
 * viewModelScope collector or a view click.
 */
object PeekPillOverlay {

    /** Wired once by MonolithViewModel; the overlay's END button. */
    var onEndPeekRequest: (() -> Unit)? = null

    private var windowManager: WindowManager? = null
    private var root: LinearLayout? = null
    private var labelView: TextView? = null
    private var clockView: TextView? = null
    // Issue #102: an exit fade in flight — a re-show in this
    // window cancels the exit instead of fighting it.
    private var exiting = false

    fun showOrUpdate(context: Context, appName: String, secondsRemaining: Int) {
        val appCtx = context.applicationContext
        if (!Settings.canDrawOverlays(appCtx)) {
            hide(appCtx)
            return
        }
        if (exiting) {
            // The lease lives again before the fade finished:
            // cancel the exit, restore the resting face.
            root?.animate()?.cancel()
            root?.alpha = PillCeremony.REST_ALPHA
            root?.translationY = 0f
            exiting = false
        }
        if (root == null) {
            if (!buildAndAttach(appCtx)) return
        }
        labelView?.text = peekPillLabelText(appName, secondsRemaining)
        clockView?.text = peekPillClockText(secondsRemaining)
        clockView?.setTextColor(
            if (peekPillWarning(secondsRemaining)) Color.rgb(0xE4, 0x7E, 0x35)
            else Color.rgb(0xF4, 0xEF, 0xEE)
        )
    }

    fun hide(context: Context) {
        val view = root ?: return
        if (exiting) return
        exiting = true
        // Issue #102: the pill leaves the way it came — a fade,
        // not a snap. The window is removed only when the fade
        // completes (PillCeremony), and only if this window is
        // still the standing one.
        animatePillOut(view) {
            if (root === view) {
                runCatching { windowManager?.removeView(view) }
                windowManager = null
                root = null
                labelView = null
                clockView = null
            }
            exiting = false
        }
    }

    private fun buildAndAttach(appCtx: Context): Boolean {
        val wm = appCtx.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            ?: return false

        val density = appCtx.resources.displayMetrics.density
        fun dp(v: Int): Int = (v * density).toInt()

        val plate = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(16).toFloat()
            setColor(Color.rgb(0x2A, 0x26, 0x23))
            setStroke(dp(1), Color.rgb(0xE4, 0x7E, 0x35))
        }

        // Issue #102: the app's own figures reach this face —
        // Anonymous Pro via PillTheme, the same typeface the
        // #16 pill lane loads. Before this the one surface he
        // sees inside another app wore the platform default.
        val figures = PillTheme.figures(appCtx)
        val label = TextView(appCtx).apply {
            setTextColor(Color.rgb(0xF4, 0xEF, 0xEE))
            textSize = 11f
            typeface = figures
            setPadding(0, 0, dp(10), 0)
        }
        val clock = TextView(appCtx).apply {
            setTextColor(Color.rgb(0xF4, 0xEF, 0xEE))
            textSize = 18f
            typeface = figures
            setPadding(0, 0, dp(10), 0)
        }
        val end = Button(appCtx).apply {
            text = "END PEEK"
            textSize = 11f
            typeface = figures
            setTextColor(Color.rgb(0x2A, 0x26, 0x23))
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(10).toFloat()
                setColor(Color.rgb(0xF4, 0xEF, 0xEE))
            }
            setOnClickListener {
                // A press you can feel: the hand-back answers
                // in the hand, not only on the glass.
                it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                onEndPeekRequest?.invoke()
            }
        }

        val row = LinearLayout(appCtx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = plate
            setPadding(dp(14), dp(8), dp(10), dp(8))
            addView(label)
            addView(clock)
            addView(end)
        }

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = dp(12)
            y = dp(72)
        }

        val attached = runCatching { wm.addView(row, params) }.isSuccess
        if (!attached) return false
        windowManager = wm
        root = row
        labelView = label
        clockView = clock
        // Issue #102: the ceremony — the pill settles in, it
        // does not snap in like a system toast.
        animatePillIn(row)
        return true
    }
}

/** The pill's clock print: M:SS, clamped at zero — pure, tested. */
fun peekPillClockText(secondsRemaining: Int): String {
    val s = secondsRemaining.coerceAtLeast(0)
    return String.format(java.util.Locale.US, "%d:%02d", s / 60, s % 60)
}

/**
 * The pill's label print: `PEEK · NAME`, gaining `· ENDING SOON`
 * inside the warning window — pure, tested. The name is uppercased
 * with [java.util.Locale.US], the same pin the clock below already
 * uses: the no-arg `uppercase()` follows the device's default
 * locale, so under e.g. tr-TR "Instagram" would print "İNSTAGRAM" —
 * a name the app does not have, at a different width.
 */
fun peekPillLabelText(appName: String, secondsRemaining: Int): String {
    val name = appName.uppercase(java.util.Locale.US)
    return if (peekPillWarning(secondsRemaining)) {
        "PEEK · $name · ENDING SOON"
    } else {
        "PEEK · $name"
    }
}

/**
 * The pill's expiry warning law: the last 30 LIVE seconds warn.
 * Zero is not a live second — `peekSecondsRemaining` retires the
 * lease at its end (`remainingMs <= 0` → null) instead of ever
 * deriving 0, so a face shown at 0 would be a dead lease wearing
 * a warning. Pure, tested.
 */
fun peekPillWarning(secondsRemaining: Int): Boolean = secondsRemaining in 1..30
