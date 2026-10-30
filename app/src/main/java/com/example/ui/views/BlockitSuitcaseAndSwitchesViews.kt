package com.example.ui.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.SystemClock
import android.view.MotionEvent
import android.view.VelocityTracker
import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.ui.theme.MonolithCurves
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * ============================================================================
 * 7. SLED SWITCH / ADVANCE LEVER VIEW (DEVELOPER-GUIDE.pdf §4.2 #6 SledSwitch)
 * ============================================================================
 * - Mechanical toggle: a physical sled that slides in a recessed track with a lever throw,
 *   not a Material Switch.
 * - State changes are animated throws (260ms, Curve A), with an 8ms firm click + synth clunk.
 */
class SledSwitchView(context: Context) : BlockitCanvasView(context) {

    var titleText: String = "SHORTS & REELS BLOCKER"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var subtitleText: String = "Auto-back out of vertical short feeds"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var isChecked: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                animateThrow(if (value) 1f else 0f)
            }
        }

    var onToggleRequested: (() -> Unit)? = null

    /**
     * PARK mode (chrome C4): the sled seated on PARK's ground. The
     * control is unchanged — same track, same throw, same engine it
     * gates — but the row's furniture is PARK's: no background fill,
     * no hairline, no title/subtitle (the sheet prints the name as a
     * plate), and no ARM/OFF word inside the thumb (PARK's margin
     * word is the state's one rendering; a fact renders once).
     * Bounds in this mode are the sled itself: 76 × 34dp.
     */
    var chromeMode: Boolean = false

    private var sledProgress: Float = 1f
    private var throwAnimator: ValueAnimator? = null

    private fun animateThrow(target: Float) {
        throwAnimator?.cancel()
        throwAnimator = ValueAnimator.ofFloat(sledProgress, target).apply {
            duration = MonolithCurves.DURATION_260.toLong()
            interpolator = MonolithCurves.CurveA
            addUpdateListener {
                sledProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        if (!chromeMode) {
            // Full-bleed row background (flat by default: tokSurface) + bottom hairline
            paintFill.color = tokSurface()
            canvas.drawRect(0f, 0f, w, h, paintFill)

            paintStroke.color = tokHairline()
            paintStroke.strokeWidth = dp(1f)
            canvas.drawLine(0f, h - dp(0.5f), w, h - dp(0.5f), paintStroke)

            // Left title & subtitle
            paintWord.color = tokTextPrimary()
            paintWord.textSize = sp(13f)
            paintWord.textAlign = Paint.Align.LEFT
            canvas.drawText(titleText, 0f, h * 0.42f, paintWord)

            paintWordSub.color = tokTextSecondary()
            paintWordSub.textSize = sp(11f)
            paintWordSub.textAlign = Paint.Align.LEFT
            canvas.drawText(subtitleText, 0f, h * 0.74f, paintWordSub)
        }

        // Right Mechanical Sled Track (76dp wide x 34dp tall, 12dp corner radius)
        val trackW = dp(76f)
        val trackH = dp(34f)
        val trackLeft = w - trackW
        val trackTop = (h - trackH) * 0.5f
        trackRect.set(trackLeft, trackTop, w, trackTop + trackH)

        paintFill.color = tokSurfaceVariant()
        canvas.drawRoundRect(trackRect, radiusSmall, radiusSmall, paintFill)
        paintStroke.color = tokHairline()
        canvas.drawRoundRect(trackRect, radiusSmall, radiusSmall, paintStroke)

        // Recess shading (Ren's depth pass): shadow pooled at the track's
        // top inner edge, light caught at its bottom inner edge.
        paintStroke.color = 0x2E000000
        paintStroke.strokeWidth = dp(1.4f)
        canvas.drawLine(trackLeft + radiusSmall, trackTop + dp(1.4f), trackLeft + trackW - radiusSmall, trackTop + dp(1.4f), paintStroke)
        paintStroke.color = 0x2EFFFFFF
        canvas.drawLine(trackLeft + radiusSmall, trackTop + trackH - dp(1.4f), trackLeft + trackW - radiusSmall, trackTop + trackH - dp(1.4f), paintStroke)

        // Sled thumb inside track
        val pad = dp(3f)
        val sledW = (trackW - pad * 2f) * 0.54f
        val maxTravel = (trackW - pad * 2f - sledW)
        val sledLeft = trackLeft + pad + sledProgress * maxTravel
        thumbRect.set(sledLeft, trackTop + pad, sledLeft + sledW, trackTop + trackH - pad)

        // Contact shadow under the moving sled, then the sled + its lit top edge
        cellRect.set(thumbRect.left, thumbRect.top + dp(2f), thumbRect.right, thumbRect.bottom + dp(2f))
        paintFill.color = 0x38000000
        canvas.drawRoundRect(cellRect, radiusSmall - dp(2f), radiusSmall - dp(2f), paintFill)

        paintFill.color = if (sledProgress > 0.5f) tokPrimary() else tokSecondaryContainer()
        canvas.drawRoundRect(thumbRect, radiusSmall - dp(2f), radiusSmall - dp(2f), paintFill)
        paintStroke.color = 0x59FFFFFF
        paintStroke.strokeWidth = dp(1.1f)
        canvas.drawLine(thumbRect.left + dp(8f), thumbRect.top + dp(1.1f), thumbRect.right - dp(8f), thumbRect.top + dp(1.1f), paintStroke)

        if (!chromeMode) {
            // Sled state label ("ON" / "OFF") + #F55314 hot-orange armed pip when ON
            paintMono.color = if (sledProgress > 0.5f) tokOnPrimary() else tokOnSecondaryContainer()
            paintMono.textSize = sp(9.5f)
            paintMono.textAlign = Paint.Align.CENTER
            val stateWord = if (sledProgress > 0.5f) "ARM" else "OFF"
            canvas.drawText(stateWord, thumbRect.centerX(), thumbRect.centerY() + dp(3.5f), paintMono)

            if (sledProgress > 0.5f) {
                paintAccent.color = tokTextPrimary() // state pip in ink, not accent (Round 1c)
                canvas.drawCircle(trackLeft + dp(10f), trackRect.centerY(), dp(3f), paintAccent)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            // §6: 8ms Firm click on SledSwitch throw!
            fireFeel(8L)
            onToggleRequested?.invoke()
            return true
        }
        return true
    }
}


/**
 * ============================================================================
 * 9. APP ROW 3-STOP SLED ROCKER VIEW (Ultra-Minimal Installed App Row)
 * ============================================================================
 * - Full-bleed installed app row with bottom hairline (`colorOutlineVariant`).
 * - Left: margin glyph (solid brick SHUT / hollow brick PEEK / none
 *   OPEN) + Atkinson app name — bold in ink when decided, quiet when OPEN.
 * - Right: 3-stop physical sled switch (`SHUT` / `PEEK` / `OPEN`) —
 *   tap a stop, or drag and throw the rocker (Vega P2); Curve A seats it.
 */
class AppRowSledToggleView(context: Context) : BlockitCanvasView(context) {

    var appName: String = "Instagram"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var currentTier: AppBlockTier = AppBlockTier.LOCKED
        set(value) {
            if (field != value) {
                field = value
                if (dragging) {
                    // The finger owns the rocker mid-drag; an outside
                    // change (a loadout landing) seats it directly.
                    rockerIndex = tierToIndex(value).toFloat()
                    invalidate()
                } else {
                    animateRockerTo(tierToIndex(value).toFloat())
                }
            }
        }

    var onTierSelected: ((AppBlockTier) -> Unit)? = null

    private var rockerIndex: Float = 0f
    private var rockerAnimator: ValueAnimator? = null

    // The throw (Vega P2 — shipped minus its overshoot keyframe, per
    // Gust's ruling: switches in this house seat, they don't bounce).
    // The thumb drags 1:1; a release faster than ~800 dp/s carries
    // ONE stop from where the drag began (issue #141: the far-stop
    // throw let a 7dp scroll nudge fling LOCKED straight to OPEN —
    // the maximum weakening from the minimum gesture); a slow
    // release snaps to the nearest. The plain tap on a stop is
    // untouched.
    private var touchInRocker = false
    private var dragging = false
    private var downX = 0f
    private var downY = 0f
    private var dragStartIndex = 0f
    private var velocityTracker: VelocityTracker? = null

    private fun tierToIndex(tier: AppBlockTier): Int = when (tier) {
        AppBlockTier.LOCKED -> 0
        AppBlockTier.PEEK_2M -> 1
        AppBlockTier.OPEN -> 2
    }

    private fun indexToTier(index: Int): AppBlockTier = when (index.coerceIn(0, 2)) {
        0 -> AppBlockTier.LOCKED
        1 -> AppBlockTier.PEEK_2M
        else -> AppBlockTier.OPEN
    }

    /** M1's crossing tick (Mikro): the middle stop passed in flight
     *  says so — once per crossing, whichever way the rocker travels
     *  and whatever moves it. One crossing rule for tap and thumb,
     *  so the sled never grows two dialects. */
    private fun noteTravel(previous: Float, now: Float) {
        if ((previous < 1f && now > 1f) || (previous > 1f && now < 1f)) {
            fireFeel(1L, 440f)
        }
    }

    private fun animateRockerTo(
        target: Float,
        durationMs: Long = MonolithCurves.DURATION_260.toLong()
    ) {
        rockerAnimator?.cancel()
        rockerAnimator = ValueAnimator.ofFloat(rockerIndex, target).apply {
            duration = durationMs
            interpolator = MonolithCurves.CurveA
            addUpdateListener {
                val v = it.animatedValue as Float
                noteTravel(rockerIndex, v)
                rockerIndex = v
                invalidate()
            }
            start()
        }
    }

    /** M1 — the landing has a direction. Toward SHUT the mechanical
     *  latch engages (4L). Onto PEEK a pitched detent (1L, 520 Hz) —
     *  the middle is a notch you pass into, not a destination with
     *  its own voice. Toward OPEN the same latch, released — the
     *  engine's existing call, its other argument. */
    private fun fireCommitFeel(target: AppBlockTier) {
        when (target) {
            AppBlockTier.LOCKED -> fireFeel(4L)
            AppBlockTier.PEEK_2M -> fireFeel(1L, 520f)
            AppBlockTier.OPEN -> {
                val engine = soundEngine
                if (engine != null) engine.playMechanicalLatch(false) else fireFeel(8L)
            }
        }
    }

    private fun commitTo(targetIndex: Int) {
        val target = targetIndex.coerceIn(0, 2)
        val targetTier = indexToTier(target)
        // Settle duration scales with distance (Vega P2): 260ms for
        // one stop — the house vocabulary — 340ms for a two-stop
        // throw. It lands ON the stop, Curve A; no overshoot.
        val distance = kotlin.math.abs(target.toFloat() - rockerIndex)
        animateRockerTo(target.toFloat(), if (distance > 1f) 340L else MonolithCurves.DURATION_260.toLong())
        if (targetTier != currentTier) {
            fireCommitFeel(targetTier)
            onTierSelected?.invoke(targetTier)
        }
    }

    private fun ellipsizeToWidth(text: String, paint: Paint, maxWidth: Float): String {
        if (maxWidth <= 0f || paint.measureText(text) <= maxWidth) return text
        val ellipsis = "…"
        var end = text.length
        while (end > 0 &&
            paint.measureText(text, 0, end) + paint.measureText(ellipsis) > maxWidth
        ) {
            end--
        }
        return if (end <= 0) ellipsis else text.substring(0, end) + ellipsis
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        // Bottom hairline
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawLine(0f, h - dp(0.5f), w, h - dp(0.5f), paintStroke)

        // Left margin glyph (Liv Part 3, adopted for the catalogue by
        // Gust): the tier as a shape a tired eye reads without
        // decoding the rocker — a solid brick for SHUT, the same
        // brick hollow for PEEK, and for OPEN the brick hollow in
        // the secondary tone (issue #141: OPEN used to draw nothing,
        // so a flipped app was discoverable only by its absence —
        // the glyph scale now fades solid -> hollow -> dim hollow,
        // every tier has a shape).
        val glyphSize = dp(8f)
        val glyphLeft = dp(8f)
        val glyphTop = h * 0.5f - glyphSize / 2f
        when (currentTier) {
            AppBlockTier.LOCKED -> {
                paintFill.color = tokTextPrimary()
                canvas.drawRect(glyphLeft, glyphTop, glyphLeft + glyphSize, glyphTop + glyphSize, paintFill)
            }
            AppBlockTier.PEEK_2M -> {
                paintStroke.color = tokTextPrimary()
                paintStroke.strokeWidth = dp(1.4f)
                canvas.drawRect(glyphLeft, glyphTop, glyphLeft + glyphSize, glyphTop + glyphSize, paintStroke)
            }
            AppBlockTier.OPEN -> {
                paintStroke.color = tokTextSecondary()
                paintStroke.strokeWidth = dp(1.4f)
                canvas.drawRect(glyphLeft, glyphTop, glyphLeft + glyphSize, glyphTop + glyphSize, paintStroke)
            }
        }

        // App name: a decided app speaks bold in ink; an OPEN app is
        // quiet — regular weight, secondary tone. One line, ellipsised
        // to the space before the rocker: a ledger entry never wraps.
        val decided = currentTier != AppBlockTier.OPEN
        val namePaint = if (decided) paintWord else paintWordSub
        namePaint.color = if (decided) tokTextPrimary() else tokTextSecondary()
        namePaint.textSize = sp(14f)
        namePaint.textAlign = Paint.Align.LEFT
        val nameX = dp(28f)
        val nameMaxWidth = (w - dp(168f)) - nameX - dp(10f)
        canvas.drawText(
            ellipsizeToWidth(appName, namePaint, nameMaxWidth),
            nameX, h * 0.5f + dp(5f), namePaint
        )

        // Right 3-Stop Sled Rocker (168dp wide x 34dp tall, 12dp radius)
        val rockerW = dp(168f)
        val rockerH = dp(34f)
        val rLeft = w - rockerW
        val rTop = (h - rockerH) * 0.5f
        trackRect.set(rLeft, rTop, w, rTop + rockerH)

        paintFill.color = tokSurfaceVariant()
        canvas.drawRoundRect(trackRect, radiusSmall, radiusSmall, paintFill)

        // Recess shading (Ren's depth pass), same treatment as the sled track
        paintStroke.color = 0x2E000000
        paintStroke.strokeWidth = dp(1.4f)
        canvas.drawLine(rLeft + radiusSmall, rTop + dp(1.4f), rLeft + rockerW - radiusSmall, rTop + dp(1.4f), paintStroke)
        paintStroke.color = 0x2EFFFFFF
        canvas.drawLine(rLeft + radiusSmall, rTop + rockerH - dp(1.4f), rLeft + rockerW - radiusSmall, rTop + dp(1.4f), paintStroke)

        val pad = dp(3f)
        val slotW = (rockerW - pad * 2f) / 3f
        val thumbL = rLeft + pad + rockerIndex * slotW
        thumbRect.set(thumbL, rTop + pad, thumbL + slotW, rTop + rockerH - pad)

        // Contact shadow under the moving rocker thumb + lit top edge
        cellRect.set(thumbRect.left, thumbRect.top + dp(2f), thumbRect.right, thumbRect.bottom + dp(2f))
        paintFill.color = 0x38000000
        canvas.drawRoundRect(cellRect, radiusSmall - dp(2f), radiusSmall - dp(2f), paintFill)

        // Colour follows the CAP, not the committed tier (Round 0 fix):
        // currentTier flips at tap-time while the cap animates ~260ms, so
        // an OPEN-white cap sat under the word SHUT for the whole throw.
        val capTier = when (rockerIndex.roundToInt().coerceIn(0, 2)) {
            0 -> AppBlockTier.LOCKED
            1 -> AppBlockTier.PEEK_2M
            else -> AppBlockTier.OPEN
        }
        paintFill.color = when (capTier) {
            AppBlockTier.LOCKED -> tokControlFill()
            // Night peach demoted: a dark cap at night, not a peach slab.
            AppBlockTier.PEEK_2M -> if (isNightMode) tokControlFill() else tokSecondaryContainer()
            AppBlockTier.OPEN -> tokSurfaceBright()
        }
        canvas.drawRoundRect(thumbRect, radiusSmall - dp(2f), radiusSmall - dp(2f), paintFill)
        paintStroke.color = 0x59FFFFFF
        paintStroke.strokeWidth = dp(1.1f)
        canvas.drawLine(thumbRect.left + dp(8f), thumbRect.top + dp(1.1f), thumbRect.right - dp(8f), thumbRect.top + dp(1.1f), paintStroke)

        // The stops print WORDS (Ren's ruling, Oct 29 — one word for
        // one fact): SHUT / PEEK / OPEN, three words of four letters.
        // The middle stop said "2m" until tonight; the word is PEEK.
        val stops = arrayOf("SHUT", "PEEK", "OPEN")
        for (i in 0..2) {
            val cx = rLeft + pad + (i + 0.5f) * slotW
            val cy = trackRect.centerY() + dp(4f)
            val selected = i == tierToIndex(capTier)
            val txtColor = if (selected) {
                when (capTier) {
                    AppBlockTier.LOCKED -> tokOnControlFill()
                    AppBlockTier.PEEK_2M -> if (isNightMode) tokOnControlFill() else tokOnSecondaryContainer()
                    AppBlockTier.OPEN -> tokTextPrimary()
                }
            } else {
                tokTextSecondary()
            }

            paintWord.color = txtColor
            paintWord.textSize = sp(10f)
            paintWord.textAlign = Paint.Align.CENTER
            canvas.drawText(stops[i], cx, cy, paintWord)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val rockerW = dp(168f)
        val rLeft = width - rockerW
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // M2 (Mikro): the name is a label, not a door. Until
                // tonight a tap on the name CYCLED the tier — wrap
                // included, the strongest change on the screen on the
                // weakest gesture. A touch that starts off the rocker
                // now does nothing, on purpose.
                touchInRocker = event.x >= rLeft
                if (touchInRocker) {
                    downX = event.x
                    downY = event.y
                    dragStartIndex = rockerIndex
                    dragging = false
                    rockerAnimator?.cancel()
                    velocityTracker?.recycle()
                    velocityTracker = VelocityTracker.obtain().also { it.addMovement(event) }
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!touchInRocker) return true
                velocityTracker?.addMovement(event)
                // Issue #141: a gesture travelling mostly vertically is
                // the list trying to scroll, not a throw — the rocker
                // used to judge UP on x alone, so an unabsorbed scroll
                // (short filtered list, edge pull) committed whatever
                // tier the thumb happened to lift over. Vertical travel
                // past the slop cancels the gesture exactly like the
                // parent's ACTION_CANCEL: reseat, commit nothing.
                val dy = kotlin.math.abs(event.y - downY)
                if (dy > dp(24f) && dy > kotlin.math.abs(event.x - downX)) {
                    touchInRocker = false
                    dragging = false
                    velocityTracker?.recycle()
                    velocityTracker = null
                    animateRockerTo(tierToIndex(currentTier).toFloat())
                    return true
                }
                if (!dragging && kotlin.math.abs(event.x - downX) > dp(6f)) {
                    dragging = true
                }
                if (dragging) {
                    val slotW = rockerW / 3f
                    val next = (dragStartIndex + (event.x - downX) / slotW).coerceIn(0f, 2f)
                    noteTravel(rockerIndex, next)
                    rockerIndex = next
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (!touchInRocker) return true
                touchInRocker = false
                velocityTracker?.addMovement(event)
                // Belt-and-braces for a fast vertical flick whose MOVE
                // never crossed the slop: vertical travel still means
                // scroll, never a commit (issue #141).
                if (kotlin.math.abs(event.y - downY) > dp(24f)) {
                    dragging = false
                    velocityTracker?.recycle()
                    velocityTracker = null
                    animateRockerTo(tierToIndex(currentTier).toFloat())
                    return true
                }
                if (dragging) {
                    dragging = false
                    velocityTracker?.computeCurrentVelocity(1000)
                    val velocityDpPerSec = (velocityTracker?.xVelocity ?: 0f) / density
                    // Issue #141: a flick carries ONE stop from where
                    // the drag began — never two. LOCKED can reach
                    // PEEK on a wild release, never OPEN.
                    val startIdx = dragStartIndex.roundToInt().coerceIn(0, 2)
                    val target = when {
                        velocityDpPerSec > FLICK_DP_PER_SEC -> (startIdx + 1).coerceIn(0, 2)
                        velocityDpPerSec < -FLICK_DP_PER_SEC -> (startIdx - 1).coerceIn(0, 2)
                        else -> rockerIndex.roundToInt().coerceIn(0, 2)
                    }
                    velocityTracker?.recycle()
                    velocityTracker = null
                    commitTo(target)
                } else {
                    velocityTracker?.recycle()
                    velocityTracker = null
                    val idx = ((event.x - rLeft) / (rockerW / 3f)).toInt().coerceIn(0, 2)
                    commitTo(idx)
                }
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                // The list scroller took the gesture: the rocker seats
                // back on the tier that is actually committed.
                if (touchInRocker) {
                    touchInRocker = false
                    dragging = false
                    velocityTracker?.recycle()
                    velocityTracker = null
                    animateRockerTo(tierToIndex(currentTier).toFloat())
                }
                return true
            }
        }
        return true
    }

    private companion object {
        /** Vega P2's throw threshold: a release faster than this
         *  carries the rocker ONE stop from the drag's start
         *  (issue #141 — the far-stop throw is dead). */
        const val FLICK_DP_PER_SEC = 800f
    }
}

/**
 * ============================================================================
 * 10. EMERGENCY PRESSURE VALVE VIEW (Unbreakable Lock + 1/Week Banked Hold)
 * ============================================================================
 * - The weekly emergency break costs a full 60 seconds of holding, BANKED
 *   in 15-second chambers (Sarthak's call, Oct 29 2026 — replaces the
 *   unbroken hold): crossing 15 / 30 / 45 locks that chamber for the
 *   rest of the attempt. Banks land as a pitched latch (560 -> 470 ->
 *   390 Hz) plus a firm click — progress you can feel turning solid.
 * - A slip is rest, not erasure (the Barriere/Ines slip grammar, arrived
 *   at independently by three cohorts): release earns 800ms of grace with
 *   no drain, then the un-banked remainder drains at 1.0x for 3s and
 *   2.0x after — never below the last bank. Re-gripping mid-drain halts
 *   the drain instantly; accrual resumes after a 250ms re-seat. The
 *   catch-click at the floor says: the bank held you.
 * - The weekly token is spent only at the full 60 held seconds.
 */
class Emergency60sValveView(context: Context) : BlockitCanvasView(context) {

    var requiredSeconds: Int = 60

    // Ledger-Direct (Freya, invention 1 — TOKEN-SPEC Amendment D is
    // moot because this input no longer exists): the valve receives NO
    // token count. The old `tokensRemaining` setter called resetHold()
    // on every change — under the repository's live flow, every
    // emission mid-visit would have silently erased the bank. The
    // overlay collects the flow and simply does not compose this view
    // at 0 remaining; hold reset is owned by the attempt lifecycle
    // below (completion), never by a rendering.

    var onEmergencyBreakCompleted: (() -> Unit)? = null

    /** Fired the moment a hold begins (ACTION_DOWN taking the grip).
     *  The routines rebuild stamps E4 attendance evidence on this —
     *  starting the 60 s hold is itself proof he showed up. */
    var onHoldStarted: (() -> Unit)? = null

    private var isHolding: Boolean = false
    private var heldMs: Long = 0L
    private var bankedMs: Long = 0L
    private var segmentStartMs: Long = 0L
    private var segmentBaseMs: Long = 0L
    private var lastSecondTick: Int = -1
    private var lastBankIndex: Int = 0

    private var isDraining: Boolean = false
    private var drainStartMs: Long = 0L
    private var drainFromMs: Long = 0L

    private val totalMs: Long get() = (requiredSeconds * 1000L).coerceAtLeast(1000L)
    private val bankIntervalMs: Long get() = totalMs / 4L
    private val holdProgress: Float
        get() = (heldMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
    private val bankedProgress: Float
        get() = (bankedMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)

    private fun resetHold() {
        isHolding = false
        isDraining = false
        heldMs = 0L
        bankedMs = 0L
        lastSecondTick = -1
        lastBankIndex = 0
        removeCallbacks(holdTickRunnable)
        removeCallbacks(drainRunnable)
    }

    private val holdTickRunnable = object : Runnable {
        override fun run() {
            if (!isHolding) return
            val now = SystemClock.elapsedRealtime()
            if (now >= segmentStartMs) {
                heldMs = (segmentBaseMs + (now - segmentStartMs)).coerceAtMost(totalMs)
            }
            val bankIndex = (heldMs / bankIntervalMs).toInt().coerceAtMost(3)
            if (bankIndex > lastBankIndex) {
                lastBankIndex = bankIndex
                bankedMs = bankIndex * bankIntervalMs
                val pitch = when (bankIndex) {
                    1 -> 560f
                    2 -> 470f
                    else -> 390f
                }
                fireFeel(1L, pitch)
                fireFeel(8L)
            }
            val sec = (heldMs / 1000L).toInt()
            if (sec != lastSecondTick) {
                lastSecondTick = sec
                fireFeel(if (sec % 5 == 0) 8L else 4L)
            }
            invalidate()
            if (heldMs >= totalMs) {
                isHolding = false
                fireFeel(10L)
                onEmergencyBreakCompleted?.invoke()
                resetHold()
                invalidate()
            } else {
                postDelayed(this, 32L)
            }
        }
    }

    private val drainRunnable = object : Runnable {
        override fun run() {
            if (!isDraining) return
            val drainElapsed = SystemClock.elapsedRealtime() - drainStartMs - GRACE_MS
            if (drainElapsed > 0L) {
                val slowMs = minOf(drainElapsed, DRAIN_SLOW_MS)
                val fastMs = (drainElapsed - DRAIN_SLOW_MS).coerceAtLeast(0L)
                heldMs = (drainFromMs - (slowMs + fastMs * 2L)).coerceAtLeast(bankedMs)
                if (heldMs <= bankedMs) {
                    isDraining = false
                    fireFeel(8L)
                    invalidate()
                    return
                }
            }
            invalidate()
            postDelayed(this, 32L)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        trackRect.set(0f, 0f, w, h)
        paintFill.color = tokBlockBar()
        canvas.drawRoundRect(trackRect, radiusLarge, radiusLarge, paintFill)

        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(trackRect, radiusLarge, radiusLarge, paintStroke)

        if (holdProgress > 0f) {
            // The chamber still in the hand: translucent.
            cellRect.set(w * bankedProgress, 0f, w * holdProgress, h)
            paintAccent.color = tokAccent()
            paintAccent.alpha = 180
            canvas.drawRoundRect(cellRect, radiusLarge, radiusLarge, paintAccent)
            // What is banked is solid — it does not drain.
            if (bankedProgress > 0f) {
                cellRect.set(0f, 0f, w * bankedProgress, h)
                paintAccent.alpha = 255
                canvas.drawRoundRect(cellRect, radiusLarge, radiusLarge, paintAccent)
            }
            paintAccent.alpha = 255
            // Chamber seams at the bank lines the progress has crossed.
            paintStroke.color = tokBlockBar()
            paintStroke.strokeWidth = dp(1.5f)
            for (i in 1..3) {
                val x = w * i / 4f
                if (holdProgress > i / 4f) {
                    canvas.drawLine(x, dp(4f), x, h - dp(4f), paintStroke)
                }
            }
        }

        val remSeconds = (requiredSeconds - (heldMs / 1000L).toInt()).coerceAtLeast(0)
        val heldSeconds = (heldMs / 1000L).toInt()
        val label: String
        val monoBadge: String
        when {
            isHolding -> {
                label = "KEEP HOLDING VALVE \u2022 "
                monoBadge = "${remSeconds}s"
            }
            heldMs > 0L -> {
                label = if (bankedMs > 0L) {
                    "BANKED ${bankedMs / 1000L}S \u2022 RESUME HOLD \u2022 "
                } else {
                    "RESUME HOLD \u2022 "
                }
                monoBadge = "${heldSeconds}s"
            }
            else -> {
                label = "HOLD FOR WEEKLY EMERGENCY BREAK \u2022 "
                monoBadge = "${requiredSeconds}s"
            }
        }

        drawMixedWordAndMono(
            canvas = canvas,
            wordPrefix = label,
            monoNumber = monoBadge,
            wordSuffix = "",
            x = w * 0.5f,
            baselineY = h * 0.5f + dp(5f),
            wordSizePx = sp(11.5f),
            monoSizePx = sp(13f),
            wordColor = LIGHT_ON_PRIMARY,
            monoColor = LIGHT_ON_PRIMARY,
            centered = true
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (isHolding) return true
                parent?.requestDisallowInterceptTouchEvent(true)
                val now = SystemClock.elapsedRealtime()
                if (isDraining) {
                    // Re-grip halts the drain where it stands; accrual
                    // resumes after the re-seat beat.
                    isDraining = false
                    removeCallbacks(drainRunnable)
                    segmentStartMs = now + RESEAT_MS
                } else {
                    segmentStartMs = now
                }
                segmentBaseMs = heldMs
                isHolding = true
                onHoldStarted?.invoke()
                removeCallbacks(holdTickRunnable)
                post(holdTickRunnable)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isHolding) return true
                isHolding = false
                removeCallbacks(holdTickRunnable)
                if (heldMs > bankedMs) {
                    isDraining = true
                    drainStartMs = SystemClock.elapsedRealtime()
                    drainFromMs = heldMs
                    removeCallbacks(drainRunnable)
                    post(drainRunnable)
                }
                invalidate()
                parent?.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private companion object {
        const val GRACE_MS = 800L
        const val DRAIN_SLOW_MS = 3000L
        const val RESEAT_MS = 250L
    }
}
