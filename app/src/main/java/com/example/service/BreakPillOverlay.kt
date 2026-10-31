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
import com.example.R

/**
 * Issue #102 (retained DAYLIGHT break 2): THE BREAK PILL — the
 * half of his pill idea that was never built.
 *
 * His words (Oct 29, 21:18): "there should be a pill that comes
 * when u go on other apps and tells times or break time or peek
 * time". The peek half existed ([PeekPillOverlay]); the break
 * half did not — during a Safe Break, over other apps, nothing
 * told him how much break was left, and the break's only
 * over-app voice was a sound cue gated by ringer and DND. He
 * had to return to Monolith to learn his own break was still
 * running — at exactly the moment he most needed permission to
 * relax against a visible clock.
 *
 * This is the peek pill's twin, in its own window: a BREAK
 * label, the remaining time as M:SS, and an END BREAK button —
 * the hand-back, wired by the ViewModel to the same
 * `endSafeBreakEarly()` the Deck's END BREAK chip uses. The
 * seconds it prints are DERIVED by its driver from the session
 * row's stored (remaining, lastTick) pair
 * (`breakRemainingAfter`, the derivation the progress machine
 * and PillDerivation.Break share) — this face never counts on
 * its own, so it cannot disagree with the break it names.
 *
 * It wears the same ceremony (PillCeremony), the same figures
 * (PillTheme — Anonymous Pro), and the same seat as the peek
 * pill. A break and a peek never stand at once (during a break
 * the peek doors stand open without a lease), so the two
 * windows never collide.
 *
 * If the draw-over-apps grant is missing the overlay silently
 * does not draw; the Deck's break face inside Monolith remains
 * the fallback, exactly as the in-app pill covers the peek.
 *
 * Main-thread confined: every caller is the ViewModel's
 * activeSession collector or a view click.
 */
object BreakPillOverlay {

    /** Wired once by MonolithViewModel; the overlay's END button. */
    var onEndBreakRequest: (() -> Unit)? = null

    private var windowManager: WindowManager? = null
    private var root: LinearLayout? = null
    private var labelView: TextView? = null
    private var clockView: TextView? = null
    private var exiting = false

    fun showOrUpdate(context: Context, secondsRemaining: Int) {
        val appCtx = context.applicationContext
        if (!Settings.canDrawOverlays(appCtx)) {
            hide(appCtx)
            return
        }
        if (exiting) {
            root?.animate()?.cancel()
            root?.alpha = PillCeremony.REST_ALPHA
            root?.translationY = 0f
            exiting = false
        }
        if (root == null) {
            if (!buildAndAttach(appCtx)) return
        }
        labelView?.text = breakPillLabelText(secondsRemaining)
        clockView?.text = peekPillClockText(secondsRemaining)
    }

    fun hide(context: Context) {
        val view = root ?: return
        if (exiting) return
        exiting = true
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
            text = appCtx.getString(R.string.pill_end_break)
            textSize = 11f
            typeface = figures
            setTextColor(Color.rgb(0x2A, 0x26, 0x23))
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(10).toFloat()
                setColor(Color.rgb(0xF4, 0xEF, 0xEE))
            }
            setOnClickListener {
                it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                onEndBreakRequest?.invoke()
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
        animatePillIn(row)
        return true
    }
}

/**
 * The break pill's label print: `BREAK`, gaining `· ENDING
 * SOON` inside the same last-30-live-seconds warning window
 * the peek pill uses ([peekPillWarning]) — one warning law for
 * both halves of the pill. Pure, tested.
 */
fun breakPillLabelText(secondsRemaining: Int): String =
    if (peekPillWarning(secondsRemaining)) "BREAK · ENDING SOON" else "BREAK"
