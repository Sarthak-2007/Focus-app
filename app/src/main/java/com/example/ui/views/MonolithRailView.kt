package com.example.ui.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.MonolithKin
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Rail geometry — EVERY dimension of the chrome band lives here and
 * nowhere else (chrome C1, spec §6.1). Pure dp math, unit-testable.
 *
 * Reference width 390dp: margins 20 → recess 350; track inset 4 →
 * track 342; segment 73.5 ×4 = 294; PARK zone 48 at the track's end;
 * cap 65.5 × 44. Travel DECK→LEDGER = 220.5dp. There is no fifth
 * rest position: PARK is a zone the cap enters, not a stop it
 * selects — stops are 0..3, the zone is index 4 in travel math only.
 */
object RailGeometry {
    const val HEIGHT_DP = 52f
    const val RADIUS_DP = 16f
    const val MARGIN_DP = 20f
    const val INSET_DP = 4f
    const val PARK_ZONE_DP = 48f
    const val CAP_HEIGHT_DP = 44f
    const val PARK_INDEX = 4

    /** stopForX result when the x is outside the track: no stop lives
     *  there — the margins fabricate nothing (issue #62 finding 4). */
    const val NO_STOP = -1

    /** Intrinsic width the rail asks for under UNSPECIFIED / AT_MOST:
     *  the spec's reference width (§6.1 geometry is priced at 390dp). */
    const val REFERENCE_WIDTH_DP = 390f

    val WORDS = listOf("DECK", "SHIELD", "TASKS", "LEDGER")

    fun recessWidthDp(screenWidthDp: Float): Float =
        (screenWidthDp - 2 * MARGIN_DP).coerceAtLeast(0f)

    fun segmentDp(recessWidthDp: Float): Float =
        ((recessWidthDp - 2 * INSET_DP - PARK_ZONE_DP) / 4f).coerceAtLeast(0f)

    fun trackRightDp(recessWidthDp: Float): Float = recessWidthDp - INSET_DP

    fun parkStartDp(recessWidthDp: Float): Float = trackRightDp(recessWidthDp) - PARK_ZONE_DP

    fun capWidthDp(recessWidthDp: Float): Float =
        (segmentDp(recessWidthDp) - 8f).coerceAtLeast(0f)

    /** MeasureSpec honouring, pure int math (issue #62 finding 5):
     *  EXACTLY takes the spec size, AT_MOST caps the desired size at
     *  the spec size, UNSPECIFIED takes the desired size. */
    fun resolveMeasure(specMode: Int, specSize: Int, desiredPx: Int): Int = when (specMode) {
        android.view.View.MeasureSpec.EXACTLY -> specSize
        android.view.View.MeasureSpec.AT_MOST -> minOf(specSize, desiredPx)
        else -> desiredPx
    }

    /** Cap left edge (dp, recess-relative) at travel position [pos]
     *  in stop units: stops centre the cap in their segment; past
     *  LEDGER the cap runs to the end wall of the PARK zone. */
    fun capLeftDp(pos: Float, recessWidthDp: Float): Float {
        val seg = segmentDp(recessWidthDp)
        val capW = capWidthDp(recessWidthDp)
        fun atStop(s: Float) = INSET_DP + s * seg + (seg - capW) / 2f
        return if (pos <= 3f) {
            atStop(pos)
        } else {
            val a = atStop(3f)
            val b = trackRightDp(recessWidthDp) - capW
            a + (b - a) * (pos - 3f).coerceIn(0f, 1f)
        }
    }

    /** The stop (or PARK zone, 4) under a recess-relative x.
     *  X outside the track (the margins, the inset strips) is
     *  NO_STOP — hit-testing fabricates nothing (issue #62). */
    fun stopForX(xDp: Float, recessWidthDp: Float): Int {
        if (xDp < INSET_DP || xDp > trackRightDp(recessWidthDp)) return NO_STOP
        if (xDp >= parkStartDp(recessWidthDp)) return PARK_INDEX
        val seg = segmentDp(recessWidthDp)
        if (seg <= 0f) return NO_STOP
        return floor((xDp - INSET_DP) / seg).toInt().coerceIn(0, 3)
    }

    fun nearestStop(pos: Float): Int = floor(pos + 0.5f).toInt().coerceIn(0, PARK_INDEX)
}

/**
 * ============================================================================
 * THE RAIL (chrome C1) — the dissolved Guard's replacement band.
 *
 * A 52dp recessed band flush at the foot of the screen: four stops
 * printed ON the recess (DECK · SHIELD · TASKS · LEDGER) and, at the
 * track's end, the PARK zone — a flat 48dp stretch that prints the
 * one word PARK (issue #115: the zone holds the machine's own state
 * AND every Settings surface — sound, theme, the Guard board — so a
 * blank, wordless strip was undiscoverable; the word prints in the
 * same mono hand as the four stops, nothing else is added). One cap
 * travels the stops under KIN-NAV.
 *
 * The old MonolithBottomNavView's vocabulary is dead with it: no
 * icons, no badges, no counts, no FOCUS·SYSTEM·HORIZON words, no
 * capsule floating on a shadow. The band is furniture: the same in
 * day and night, at rest and (read-only) under the seal.
 *
 * Mechanics (§6.4): DOWN answers within the frame — a pressed word
 * strikes to full ink at once (the ink-55 → ink-100 strike is a
 * one-frame print, not a fade); the cap NEVER scales, ripples, or
 * rubber-bands. A drag carries the cap 1:1 with the offset
 * preserved and seats at the nearest stop; there is no magnet.
 * Travel runs KIN-NAV (180 + 60/stop, Curve A, zero overshoot);
 * words flip and content starts on the seat frame. Haptics: 1ms
 * per detent crossed, 4ms on seat — 8ms is never used in chrome.
 * ============================================================================
 */
class MonolithRailView(context: Context) : BlockitCanvasView(context) {

    /** The committed stop, 0..3 (DECK/SHIELD/TASKS/LEDGER). */
    var selectedTabIndex: Int = 0
        set(value) {
            val v = value.coerceIn(0, 3)
            if (field != v) {
                field = v
                focusStop = v
                updateA11yDescription()
                if (!dragging && !parkOpen) animateCapTo(v.toFloat(), null)
                invalidate()
            }
        }

    /** PARK open: the cap stands in the zone; the band is read-only
     *  furniture behind the sheet except for its dismissal inputs. */
    var parkOpen: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                updateA11yDescription()
                if (!dragging) {
                    animateCapTo(
                        if (value) RailGeometry.PARK_INDEX.toFloat()
                        else selectedTabIndex.toFloat(),
                        null
                    )
                }
                invalidate()
            }
        }

    /** A session is armed/running: the DECK stop carries the
     *  filled-centre marker (the old FOCUS dot's form), in ink —
     *  never orange, steady, and ABSENT (not dimmed) otherwise. */
    var isSessionArmed: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var onTabSelected: ((Int) -> Unit)? = null

    /** A PARK-zone seat or tap (open), or a PARK re-tap (close).
     *  The host owns the sheet; the band only reports the input. */
    var onParkInvoked: (() -> Unit)? = null

    private var capPos: Float = 0f
    private var animator: ValueAnimator? = null
    private var dragging = false
    private var downX = 0f
    private var downPos = 0f
    private var pressedStop = -1
    private var lastDetent = 0

    /** Destination the keyboard / accessibility focus rests on;
     *  arrows move it, ENTER / performClick commits it. Follows the
     *  committed selection whenever the host moves it. */
    private var focusStop: Int = 0

    init {
        capPos = selectedTabIndex.toFloat()
        lastDetent = selectedTabIndex
        // Accessibility (issue #62 finding 2): the rail is primary
        // navigation — it must be discoverable, focusable and
        // operable without touch. Per-destination actions are added
        // in onInitializeAccessibilityNodeInfo.
        isFocusable = true
        isClickable = true
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
        updateA11yDescription()
    }

    private fun recessWidthDp(): Float =
        RailGeometry.recessWidthDp(width / density)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Honour both specs' modes (issue #62 finding 5): EXACTLY
        // takes the offered size, AT_MOST caps at the rail's desired
        // size, UNSPECIFIED takes the desired size.
        setMeasuredDimension(
            RailGeometry.resolveMeasure(
                MeasureSpec.getMode(widthMeasureSpec),
                MeasureSpec.getSize(widthMeasureSpec),
                dp(RailGeometry.REFERENCE_WIDTH_DP).toInt()
            ),
            RailGeometry.resolveMeasure(
                MeasureSpec.getMode(heightMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec),
                dp(RailGeometry.HEIGHT_DP).toInt()
            )
        )
    }

    /** The animator dies with the window (issue #62 finding 3):
     *  a detached rail never animates, never invalidates, and —
     *  because cancel suppresses the seat callback — never reports
     *  a navigation after it has left the screen. */
    override fun onDetachedFromWindow() {
        animator?.cancel()
        animator = null
        dragging = false
        pressedStop = -1
        super.onDetachedFromWindow()
    }

    // ----------------------------------------------------------------
    // Accessibility: per-destination actions, click, keyboard.
    // ----------------------------------------------------------------

    private fun updateA11yDescription() {
        contentDescription = if (parkOpen) {
            "Navigation rail. Park is open. Current destination: ${A11Y_NAMES[selectedTabIndex]}."
        } else {
            "Navigation rail. ${A11Y_NAMES[selectedTabIndex]} selected."
        }
    }

    private fun announceA11y(text: String) {
        val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
        event.text.add(text)
        sendAccessibilityEventUnchecked(event)
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = MonolithRailView::class.java.name
        info.isClickable = true
        info.isFocusable = true
        for (i in 0..3) {
            info.addAction(
                AccessibilityNodeInfo.AccessibilityAction(
                    A11Y_ACTION_BASE + i, "Go to ${A11Y_NAMES[i]}"
                )
            )
        }
        info.addAction(
            AccessibilityNodeInfo.AccessibilityAction(
                A11Y_ACTION_BASE + RailGeometry.PARK_INDEX,
                if (parkOpen) "Close Park" else "Open Park"
            )
        )
    }

    override fun performAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
        val stop = action - A11Y_ACTION_BASE
        if (stop in 0..RailGeometry.PARK_INDEX) {
            activateStop(stop)
            return true
        }
        if (action == AccessibilityNodeInfo.ACTION_CLICK) {
            performClick()
            return true
        }
        return super.performAccessibilityAction(action, arguments)
    }

    /** TalkBack double-tap / generic click: commit the destination
     *  the accessibility focus rests on. */
    override fun performClick(): Boolean {
        super.performClick()
        activateStop(focusStop)
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                moveA11yFocus(-1)
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                moveA11yFocus(1)
                return true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER, KeyEvent.KEYCODE_SPACE -> {
                performClick()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun moveA11yFocus(delta: Int) {
        val next = (focusStop + delta).coerceIn(0, RailGeometry.PARK_INDEX)
        if (next != focusStop) {
            focusStop = next
            announceA11y(
                if (next == RailGeometry.PARK_INDEX) "Park focused"
                else "${A11Y_NAMES[next]} focused"
            )
            invalidate()
        }
    }

    /** One activation path for touch taps, accessibility actions,
     *  keyboard and performClick — same travel, same seat callback. */
    private fun activateStop(stop: Int) {
        if (stop in 0..RailGeometry.PARK_INDEX) tapStop(stop)
    }

    companion object {
        /** Custom accessibility action ids for the five destinations
         *  (stops 0..3 + PARK at +4). Private range, no R.id needed. */
        private const val A11Y_ACTION_BASE = 0x62000
        private val A11Y_NAMES = listOf("Deck", "Shield", "Tasks", "Ledger")
    }

    private fun animateCapTo(target: Float, onSeat: (() -> Unit)?) {
        animator?.cancel()
        val from = capPos
        if (abs(target - from) < 0.001f) {
            capPos = target
            onSeat?.invoke()
            invalidate()
            return
        }
        val stops = ceil(abs(target - from)).toInt().coerceAtLeast(1)
        // Issue #62 finding 1: Android delivers onAnimationEnd AFTER
        // onAnimationCancel when cancel() interrupts a travel. Without
        // this flag, an interrupted travel snapped the cap to the old
        // target, buzzed, and fired the old seat callback — reporting
        // a navigation the user had just cancelled. A cancelled
        // animation now ends silently: no teleport, no feel, no seat.
        var cancelled = false
        animator = ValueAnimator.ofFloat(from, target).apply {
            duration = MonolithKin.navDurationMs(stops).toLong()
            interpolator = MonolithCurves.CurveA
            addUpdateListener {
                capPos = it.animatedValue as Float
                tickDetents()
                invalidate()
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: android.animation.Animator) {
                    cancelled = true
                }

                override fun onAnimationEnd(animation: android.animation.Animator) {
                    if (animator === animation) animator = null
                    if (cancelled) return
                    capPos = target
                    fireFeel(4L)
                    invalidate()
                    onSeat?.invoke()
                }
            })
            start()
        }
    }

    private fun tickDetents() {
        val d = RailGeometry.nearestStop(capPos)
        if (d != lastDetent) {
            lastDetent = d
            fireFeel(1L, 300f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return
        val recessW = recessWidthDp()
        val left = dp(RailGeometry.MARGIN_DP)
        val seg = RailGeometry.segmentDp(recessW)
        val centerY = h / 2f

        // The recess: one flat fill, radius 16, letterpress top line.
        boundsRect.set(left, 0f, left + dp(recessW), h)
        paintFill.color = tokSurfaceVariant()
        canvas.drawRoundRect(boundsRect, dp(RailGeometry.RADIUS_DP), dp(RailGeometry.RADIUS_DP), paintFill)
        paintStroke.color = if (isNightMode) 0x1AFFFFFF else 0x59FFFFFF
        paintStroke.strokeWidth = dp(1f)
        canvas.drawLine(
            left + dp(RailGeometry.RADIUS_DP), dp(0.5f),
            left + dp(recessW - RailGeometry.RADIUS_DP), dp(0.5f),
            paintStroke
        )

        // PARK zone furniture: score line at its inner edge, end-wall
        // line at the track's end, and the PARK word between them
        // (printed with the stop words below — it inks up as the cap
        // enters the zone, as the old notch grip did).
        val parkStartX = left + dp(RailGeometry.parkStartDp(recessW))
        val trackRightX = left + dp(RailGeometry.trackRightDp(recessW))
        val parkFraction = (capPos - 3f).coerceIn(0f, 1f)
        paintStroke.color = inkWithAlpha(0.18f)
        paintStroke.strokeWidth = dp(1f)
        canvas.drawLine(parkStartX, dp(6f), parkStartX, h - dp(6f), paintStroke)
        paintStroke.color = inkWithAlpha(0.35f)
        canvas.drawLine(trackRightX - dp(0.5f), dp(6f), trackRightX - dp(0.5f), h - dp(6f), paintStroke)
        // The PARK word replaces the old notch grip (issue #115):
        // the grip was a glyph nobody could read as "settings live
        // here"; the word is the discoverability. It is printed
        // AFTER the cap (below, with the stop words) so it stays
        // legible over the cap exactly like DECK/SHIELD/TASKS/LEDGER.
        val notchCx = (parkStartX + trackRightX) / 2f

        // The cap: ink fill, visual only. A DOWN on the selected stop
        // steps its bottom band one tone down — a null press: the
        // machine acknowledges the finger and does nothing else.
        val capLeft = left + dp(RailGeometry.capLeftDp(capPos, recessW))
        val capW = dp(RailGeometry.capWidthDp(recessW))
        thumbRect.set(capLeft, dp(RailGeometry.INSET_DP), capLeft + capW, dp(RailGeometry.INSET_DP + RailGeometry.CAP_HEIGHT_DP))
        paintFill.color = tokControlFill()
        canvas.drawRoundRect(thumbRect, dp(12f), dp(12f), paintFill)
        if (pressedStop == selectedTabIndex && !parkOpen && pressedStop >= 0 && !dragging) {
            paintFill.color = if (isNightMode) 0xFF362C27.toInt() else 0xFF171616.toInt()
            canvas.save()
            canvas.clipRect(capLeft, thumbRect.bottom - dp(10f), capLeft + capW, thumbRect.bottom)
            canvas.drawRoundRect(thumbRect, dp(12f), dp(12f), paintFill)
            canvas.restore()
        }
        // The selection's marker line: 1dp on the cap's leading edge,
        // cream at 90% (the settled selection language, F-05).
        paintFill.color = if (isNightMode) 0xE6FFE4CF.toInt() else 0xE6FAF6F0.toInt()
        canvas.drawRect(capLeft + dp(3f), thumbRect.top + dp(8f), capLeft + dp(4f), thumbRect.bottom - dp(8f), paintFill)

        // The words, printed on the recess over the cap's travel.
        // Words flip on arrival: mid-travel the DEPARTING stop keeps
        // the glyph tone; the arriving word takes it only when the
        // cap seats. While PARK is open no word is selected.
        paintMono.textSize = sp(10f)
        paintMono.letterSpacing = 0.2f
        paintMono.textAlign = android.graphics.Paint.Align.CENTER
        val wordBaseline = centerY + sp(10f) * 0.36f
        val toneStop = if (animator?.isRunning == true) {
            selectedTabIndex
        } else {
            RailGeometry.nearestStop(capPos).coerceAtMost(3)
        }
        for (i in 0..3) {
            val cx = left + dp(RailGeometry.INSET_DP + seg * (i + 0.5f))
            paintMono.color = when {
                i == pressedStop && !dragging -> tokTextPrimary()
                !parkOpen && i == toneStop -> tokOnControlFill()
                else -> tokTextSecondary()
            }
            canvas.drawText(RailGeometry.WORDS[i], cx, wordBaseline, paintMono)
        }
        // PARK, in the same hand, dead-centre in its zone: secondary
        // ink at rest, the on-control tone once the cap stands in the
        // zone (parkFraction tracks the cap's entry), full ink while
        // pressed — the four stops' tone language, no new vocabulary.
        paintMono.color = when {
            pressedStop == RailGeometry.PARK_INDEX && !dragging -> tokTextPrimary()
            parkOpen || parkFraction >= 0.5f -> tokOnControlFill()
            else -> inkWithAlpha(0.55f + 0.45f * parkFraction)
        }
        canvas.drawText("PARK", notchCx, wordBaseline, paintMono)

        // The armed marker at the DECK stop: stroked circle, filled
        // centre, ink — drawn last, over whatever the cap is doing.
        if (isSessionArmed) {
            val wordW = paintMono.measureText(RailGeometry.WORDS[0])
            val cx = left + dp(RailGeometry.INSET_DP + seg * 0.5f) - wordW / 2f - dp(10f)
            val markerColor = if (capPos < 0.5f) tokOnControlFill() else tokTextPrimary()
            paintStroke.color = markerColor
            paintStroke.strokeWidth = dp(1.2f)
            canvas.drawCircle(cx, centerY, dp(4f), paintStroke)
            paintFill.color = markerColor
            canvas.drawCircle(cx, centerY, dp(1.8f), paintFill)
        }
    }

    private fun inkWithAlpha(alpha: Float): Int {
        val base = tokTextPrimary()
        return Color.argb(
            (alpha * 255).toInt().coerceIn(0, 255),
            Color.red(base), Color.green(base), Color.blue(base)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val recessW = recessWidthDp()
        val relX = (event.x - dp(RailGeometry.MARGIN_DP)) / density
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                animator?.cancel()
                downX = event.x
                downPos = capPos
                dragging = false
                pressedStop = RailGeometry.stopForX(relX, recessW)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - downX
                if (!dragging && abs(dx) > dp(8f)) dragging = true
                if (dragging) {
                    val seg = RailGeometry.segmentDp(recessW)
                    var raw = downPos + (dx / density) / seg
                    // Resistance over the last stretch into PARK: the
                    // zone is a detent, not a slide — the cap leans
                    // against it before it seats.
                    if (raw > 3f && downPos <= 3f) {
                        val over = raw - 3f
                        raw = 3f + if (over <= 0.3f) over * 0.55f
                        else 0.3f * 0.55f + (over - 0.3f) * 0.85f
                    }
                    capPos = raw.coerceIn(0f, RailGeometry.PARK_INDEX.toFloat())
                    tickDetents()
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val wasDragging = dragging
                dragging = false
                pressedStop = -1
                if (event.actionMasked == MotionEvent.ACTION_CANCEL) {
                    animateCapTo(if (parkOpen) RailGeometry.PARK_INDEX.toFloat() else selectedTabIndex.toFloat(), null)
                    return true
                }
                if (wasDragging) {
                    // Issue #117 (one-hand-commute): a drag seats at
                    // the nearest stop — EXCEPT PARK. PARK opens a
                    // sheet, so it may only be seated when the finger
                    // itself is inside the PARK zone at release
                    // (position, never nearest-stop inference). The
                    // old code let a jolted tap that crossed the 8dp
                    // drag threshold slide the cap past the 3.5 round
                    // point and seat PARK, opening a sheet the user
                    // never asked for; such a release now seats at
                    // LEDGER (3), the nearest real stop.
                    var seat = RailGeometry.nearestStop(capPos)
                    if (seat == RailGeometry.PARK_INDEX &&
                        RailGeometry.stopForX(relX, recessW) != RailGeometry.PARK_INDEX
                    ) {
                        seat = 3
                    }
                    commitStop(seat)
                } else {
                    // Issue #62 finding 4: a tap commits the stop under
                    // the RELEASE point, recomputed here from this
                    // event's x — never the DOWN stop. A release
                    // outside the track commits nothing and the cap
                    // settles back to the committed stop.
                    val upStop = RailGeometry.stopForX(relX, recessW)
                    if (upStop >= 0) {
                        tapStop(upStop)
                    } else {
                        animateCapTo(
                            if (parkOpen) RailGeometry.PARK_INDEX.toFloat()
                            else selectedTabIndex.toFloat(),
                            null
                        )
                    }
                }
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /** A tap: PARK seats invoke the sheet; a selected stop is a
     *  null press (the dip already answered); any other stop
     *  travels under KIN-NAV and reports on the seat frame. */
    private fun tapStop(stop: Int) {
        when {
            parkOpen && stop == RailGeometry.PARK_INDEX -> onParkInvoked?.invoke()
            parkOpen -> animateCapTo(stop.toFloat()) { onTabSelected?.invoke(stop) }
            stop == RailGeometry.PARK_INDEX ->
                animateCapTo(RailGeometry.PARK_INDEX.toFloat()) { onParkInvoked?.invoke() }
            stop == selectedTabIndex -> animateCapTo(selectedTabIndex.toFloat(), null)
            else -> animateCapTo(stop.toFloat()) { onTabSelected?.invoke(stop) }
        }
    }

    /** Settle the cap onto [stop] (0..3) or the PARK zone (4) —
     *  the host's truth-telling move after it REFUSES a seat the
     *  cap already travelled to (issues #128 findings 1 + 4): the
     *  committed stop never changed, so without this the cap would
     *  stand over a destination the phone is not on and the band
     *  would lie about where you are. No seat callback fires: this
     *  is a return, not a navigation. */
    fun settleCapTo(stop: Int) {
        val target = stop.coerceIn(0, RailGeometry.PARK_INDEX).toFloat()
        if (kotlin.math.abs(capPos - target) > 0.001f) {
            animateCapTo(target, null)
        }
    }

    /** A drag's seat: the nearest stop, PARK included. */
    private fun commitStop(stop: Int) {
        when {
            stop == RailGeometry.PARK_INDEX && parkOpen ->
                animateCapTo(RailGeometry.PARK_INDEX.toFloat(), null)
            stop == RailGeometry.PARK_INDEX ->
                animateCapTo(RailGeometry.PARK_INDEX.toFloat()) { onParkInvoked?.invoke() }
            parkOpen -> animateCapTo(stop.toFloat()) { onTabSelected?.invoke(stop) }
            stop == selectedTabIndex -> animateCapTo(selectedTabIndex.toFloat(), null)
            else -> animateCapTo(stop.toFloat()) { onTabSelected?.invoke(stop) }
        }
    }
}
