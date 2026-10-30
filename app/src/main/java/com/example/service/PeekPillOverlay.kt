package com.example.service

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.ui.components.peekCountdownText

/**
 * Issue #102 (peek-lived, finding 1): the floating countdown pill,
 * drawn as a SYSTEM OVERLAY by the shield service so it floats
 * over the peeked app itself — the place the user actually is
 * during the 2 minutes. Before this, `floatingPeekPill` state
 * existed in the ViewModel with zero collectors: the pill was
 * promised in the docs and never drawn anywhere.
 *
 * The face: "PEEK" (Atkinson Hyperlegible), the countdown
 * (Anonymous Pro, derived by the caller from the Room lease's
 * stored end — never a second clock), and a ✕ that hands the
 * peek back early through [onEndPeek]. Draggable, snaps to the
 * nearest vertical edge on release. Colours are the night tokens
 * (Color.kt Night*): surface-bright #362C27, cream #FFE4CF,
 * accent #F47442 — no green anywhere, per the standing law.
 *
 * Draws only when the overlay permission is actually granted
 * (Settings.canDrawOverlays); without it the pill silently does
 * not draw and the in-Monolith Compose pill (PeekPill.kt) plus
 * the shield's expiry enforcer still carry the journey.
 */
class PeekPillOverlay(
    private val context: Context,
    private val onEndPeek: (String) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var pillView: LinearLayout? = null
    private var countdownView: TextView? = null
    private var params: WindowManager.LayoutParams? = null

    var shownPackage: String? = null
        private set

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), context.resources.displayMetrics
    ).toInt()

    fun canDraw(): Boolean = Settings.canDrawOverlays(context)

    fun show(packageName: String, secondsRemaining: Int) {
        mainHandler.post {
            if (shownPackage == packageName && pillView != null) {
                update(secondsRemaining)
                return@post
            }
            hideNow()
            if (!canDraw()) return@post
            val pill = buildPill(packageName)
            val lp = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = dp(12)
                y = dp(72)
            }
            try {
                windowManager.addView(pill, lp)
                pillView = pill
                params = lp
                shownPackage = packageName
                update(secondsRemaining)
            } catch (_: Exception) {
                // Permission revoked between check and draw: the
                // enforcer in the shield service still cuts the
                // peek off at expiry; only the face is missing.
                pillView = null
                params = null
                shownPackage = null
            }
        }
    }

    fun update(secondsRemaining: Int) {
        mainHandler.post {
            countdownView?.text = peekCountdownText(secondsRemaining)
        }
    }

    fun hide() {
        mainHandler.post { hideNow() }
    }

    private fun hideNow() {
        pillView?.let { view ->
            try {
                windowManager.removeView(view)
            } catch (_: Exception) {
                // Already detached.
            }
        }
        pillView = null
        countdownView = null
        params = null
        shownPackage = null
    }

    private fun buildPill(packageName: String): LinearLayout {
        val cream = Color.rgb(0xFF, 0xE4, 0xCF)
        val accent = Color.rgb(0xF4, 0x74, 0x42)
        val atkinson = ResourcesCompat.getFont(context, R.font.atkinson_hyperlegible_bold)
        val anonymous = ResourcesCompat.getFont(context, R.font.anonymous_pro_bold)

        val background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(999).toFloat()
            setColor(Color.rgb(0x36, 0x2C, 0x27))
            setStroke(dp(1), Color.rgb(0x80, 0x7A, 0x7A))
        }

        val pill = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            this.background = background
            setPadding(dp(14), dp(8), dp(10), dp(8))
        }

        val label = TextView(context).apply {
            text = "PEEK"
            typeface = atkinson
            setTextColor(cream)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
        }
        val countdown = TextView(context).apply {
            typeface = anonymous
            setTextColor(cream)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setPadding(dp(10), 0, dp(6), 0)
        }
        countdownView = countdown
        val close = TextView(context).apply {
            text = "✕"
            typeface = atkinson
            setTextColor(accent)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setPadding(dp(6), dp(2), dp(6), dp(2))
            contentDescription = "End peek early"
            setOnClickListener { onEndPeek(packageName) }
        }

        pill.addView(label)
        pill.addView(countdown)
        pill.addView(close)
        attachDrag(pill)
        return pill
    }

    /** Drag anywhere on the pill body; on release it snaps to the
     *  nearest vertical edge of the screen. The ✕ keeps its own
     *  click — a drag that starts on the ✕ still ends the peek,
     *  which is the safe direction for an accidental touch. */
    private fun attachDrag(pill: LinearLayout) {
        var startRawX = 0f
        var startRawY = 0f
        var startX = 0
        var startY = 0
        pill.setOnTouchListener { view, event ->
            val lp = params ?: return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startRawX = event.rawX
                    startRawY = event.rawY
                    startX = lp.x
                    startY = lp.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    lp.x = startX + (event.rawX - startRawX).toInt()
                    lp.y = (startY + (event.rawY - startRawY).toInt()).coerceAtLeast(0)
                    try {
                        windowManager.updateViewLayout(view, lp)
                    } catch (_: Exception) {
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val screenWidth = context.resources.displayMetrics.widthPixels
                    val center = lp.x + view.width / 2
                    lp.x = if (center < screenWidth / 2) dp(8) else screenWidth - view.width - dp(8)
                    try {
                        windowManager.updateViewLayout(view, lp)
                    } catch (_: Exception) {
                    }
                    true
                }
                else -> false
            }
        }
    }
}

/**
 * Issue #102 (peek-lived, finding 2): the expiry eviction
 * decision, pure so it is unit-tested — at lease expiry the user
 * is evicted ONLY if the foreground package is the peeked package
 * itself. Any other foreground (he already left, or never got
 * there) needs no eviction: the doors re-block on entry as usual.
 */
internal fun peekExpiryShouldEvict(foregroundPackage: String?, peekPackage: String): Boolean =
    peekPackage.isNotBlank() && foregroundPackage == peekPackage
