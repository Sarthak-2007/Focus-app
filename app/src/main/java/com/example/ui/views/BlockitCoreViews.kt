package com.example.ui.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Choreographer
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.service.TactileSoundEngine
import com.example.service.Warrant
import com.example.service.WarrantState
import com.example.ui.theme.MonolithCurves
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * ============================================================================
 * BLOCKIT-LEVEL ZERO-ALLOCATION CUSTOM VIEW ARCHITECTURE (DEVELOPER-GUIDE.pdf)
 * ============================================================================
 * - Every colour resolves strictly to the 19 tokens (Light & Night).
 * - Only ONE saturated accent exists: colorPrimaryContainer (#F55314 / #F47442).
 * - Words render in Google Sans / PlusJakartaSans; EVERY number renders in Space Mono / JetBrainsMono.
 * - Only two corner radii exist app-wide: 12dp (small) and 24dp (large), with 12dp universal gap.
 * - Pre-allocated final Paint, final RectF, final Path fields — zero allocation in onDraw().
 * - Every animation uses Curve A, B, C or OvershootInterpolator and durations {180, 260, 280, 420, 520}ms.
 * - Every haptic is a 1/4/8/10ms VibrationEffect.createOneShot(ms, DEFAULT_AMPLITUDE) paired with
 *   Float-PCM synth transients from the exact same call site.
 */
abstract class BlockitCanvasView(context: Context) : View(context) {

    companion object {
        // Light 19 Tokens (Zero-Green verified)
        const val LIGHT_BG = 0xFFE4DFDA.toInt()
        const val LIGHT_SURFACE = 0xFFE4DFDA.toInt()
        const val LIGHT_SURFACE_SHEET = 0xFFE4DFDA.toInt()
        const val LIGHT_SURFACE_VARIANT = 0xFFD8D1CB.toInt()
        const val LIGHT_SURFACE_BRIGHT = 0xFFFAF6F0.toInt()
        const val LIGHT_PRIMARY = 0xFF252323.toInt()
        const val LIGHT_ON_PRIMARY = 0xFFFBF1EC.toInt()
        const val LIGHT_PRIMARY_CONTAINER = 0xFFF55314.toInt()
        const val LIGHT_ON_PRIMARY_CONTAINER = 0xFF471602.toInt()
        const val LIGHT_SECONDARY_CONTAINER = 0xFF615B5A.toInt()
        const val LIGHT_ON_SECONDARY_CONTAINER = 0xFFF4EFEE.toInt()
        const val LIGHT_ON_SURFACE = 0xFF4E4948.toInt()
        const val LIGHT_ON_SURFACE_VARIANT = 0xFF787170.toInt()
        const val LIGHT_OUTLINE = 0xFF615B5A.toInt()
        const val LIGHT_OUTLINE_VARIANT = 0x75615B5A
        const val LIGHT_BLOCK_BAR = 0xFF2E2A26.toInt()
        const val LIGHT_TEXT_PRIMARY = 0xFF2C2A29.toInt()
        const val LIGHT_TEXT_SECONDARY = 0xFF615B5A.toInt()
        const val LIGHT_ALERT = 0xFFD63E06.toInt()

        // Night 19 Tokens (Zero-Green verified)
        const val NIGHT_BG = 0xFF171412.toInt()
        const val NIGHT_SURFACE = 0xFF171412.toInt()
        const val NIGHT_SURFACE_SHEET = 0xFF4A423C.toInt()
        const val NIGHT_SURFACE_VARIANT = 0xFF100E0C.toInt()
        const val NIGHT_SURFACE_BRIGHT = 0xFF362C27.toInt()
        const val NIGHT_PRIMARY = 0xFFFFE4CF.toInt()
        const val NIGHT_ON_PRIMARY = 0xFF1B110D.toInt()
        const val NIGHT_PRIMARY_CONTAINER = 0xFFF47442.toInt()
        const val NIGHT_ON_PRIMARY_CONTAINER = 0xFF471602.toInt()
        const val NIGHT_SECONDARY_CONTAINER = 0xFF4A423C.toInt()
        const val NIGHT_ON_SECONDARY_CONTAINER = 0xFFFFE4CF.toInt()
        const val NIGHT_ON_SURFACE = 0xFFEDE3D8.toInt()
        const val NIGHT_ON_SURFACE_VARIANT = 0xFF776A60.toInt()
        const val NIGHT_OUTLINE = 0xFF807A7A.toInt()
        const val NIGHT_OUTLINE_VARIANT = 0x758F8888
        const val NIGHT_BLOCK_BAR = 0xFF161311.toInt()
        const val NIGHT_TEXT_PRIMARY = 0xFFD2C6BB.toInt()
        const val NIGHT_TEXT_SECONDARY = 0xFF9C8E84.toInt()
        const val NIGHT_ALERT = 0xFFFF8A5C.toInt()
    }

    var isNightMode: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var soundEngine: TactileSoundEngine? = null

    protected val density: Float = resources.displayMetrics.density
    protected val scaledDensity: Float = resources.displayMetrics.scaledDensity

    protected fun dp(v: Float): Float = v * density
    protected fun sp(v: Float): Float = v * scaledDensity

    protected val radiusSmall: Float = dp(12f)
    protected val radiusLarge: Float = dp(24f)
    protected val gapUnit: Float = dp(12f)
    protected val dotTileSize: Float = dp(4f)

    // Token resolvers (Zero allocation)
    protected fun tokBg(): Int = if (isNightMode) NIGHT_BG else LIGHT_BG
    protected fun tokSurface(): Int = if (isNightMode) NIGHT_SURFACE else LIGHT_SURFACE
    protected fun tokSurfaceSheet(): Int = if (isNightMode) NIGHT_SURFACE_SHEET else LIGHT_SURFACE_SHEET
    protected fun tokSurfaceVariant(): Int = if (isNightMode) NIGHT_SURFACE_VARIANT else LIGHT_SURFACE_VARIANT
    protected fun tokSurfaceBright(): Int = if (isNightMode) NIGHT_SURFACE_BRIGHT else LIGHT_SURFACE_BRIGHT
    protected fun tokPrimary(): Int = if (isNightMode) NIGHT_PRIMARY else LIGHT_PRIMARY
    // Night control fill (X1): filled controls must not flip to cream at night.
    protected fun tokControlFill(): Int = if (isNightMode) 0xFF4A423C.toInt() else LIGHT_PRIMARY

    /** Label paired to tokControlFill(): cream in both modes (never the
     *  dark onPrimary — on the night #4A423C fill it was ~1.8:1, illegible). */
    protected fun tokOnControlFill(): Int = if (isNightMode) NIGHT_PRIMARY else LIGHT_ON_PRIMARY
    protected fun tokOnPrimary(): Int = if (isNightMode) NIGHT_ON_PRIMARY else LIGHT_ON_PRIMARY
    protected fun tokAccent(): Int = if (isNightMode) NIGHT_PRIMARY_CONTAINER else LIGHT_PRIMARY_CONTAINER

    /** Slice E, E5b (Horiz §2): the accent's shaded exit — at the
     *  seal the cap's orange steps DOWN a printed table (round
     *  half-up, never an eyeballed lerp): phase 1 = x0.75, phase 2
     *  = x0.50, phase 3 = the terminal control fill. Zero orange
     *  survives 200ms past t=0; phase 0 is the accent itself, so
     *  every unsealed frame is byte-identical to before. */
    protected fun tokAccentShaded(phase: Int): Int {
        if (phase <= 0) return tokAccent()
        if (phase >= 3) return tokControlFill()
        val base = tokAccent()
        val keep = 1f - phase * 0.25f
        fun ch(shift: Int): Int {
            val v = (base ushr shift) and 0xFF
            return kotlin.math.floor(v * keep + 0.5f).toInt().coerceIn(0, 255)
        }
        return (0xFF shl 24) or (ch(16) shl 16) or (ch(8) shl 8) or ch(0)
    }
    protected fun tokOnAccent(): Int = if (isNightMode) NIGHT_ON_PRIMARY_CONTAINER else LIGHT_ON_PRIMARY_CONTAINER
    protected fun tokSecondaryContainer(): Int = if (isNightMode) NIGHT_SECONDARY_CONTAINER else LIGHT_SECONDARY_CONTAINER
    protected fun tokOnSecondaryContainer(): Int = if (isNightMode) NIGHT_ON_SECONDARY_CONTAINER else LIGHT_ON_SECONDARY_CONTAINER
    protected fun tokOnSurface(): Int = if (isNightMode) NIGHT_ON_SURFACE else LIGHT_ON_SURFACE
    protected fun tokOnSurfaceVariant(): Int = if (isNightMode) NIGHT_ON_SURFACE_VARIANT else LIGHT_ON_SURFACE_VARIANT
    protected fun tokOutline(): Int = if (isNightMode) NIGHT_OUTLINE else LIGHT_OUTLINE
    protected fun tokHairline(): Int = if (isNightMode) NIGHT_OUTLINE_VARIANT else LIGHT_OUTLINE_VARIANT
    protected fun tokBlockBar(): Int = if (isNightMode) NIGHT_BLOCK_BAR else LIGHT_BLOCK_BAR
    protected fun tokTextPrimary(): Int = if (isNightMode) NIGHT_TEXT_PRIMARY else LIGHT_TEXT_PRIMARY
    protected fun tokTextSecondary(): Int = if (isNightMode) NIGHT_TEXT_SECONDARY else LIGHT_TEXT_SECONDARY
    protected fun tokAlert(): Int = if (isNightMode) NIGHT_ALERT else LIGHT_ALERT

    // Pre-allocated Typefaces (§2: Words = Sans, Numbers = Space Mono / JetBrainsMono)
    protected val wordTypefaceBold: Typeface by lazy {
        // The REAL Bold file (Round 0b fix) — Typeface.create(base, BOLD) on
        // the Regular file was synthesized smear across every Canvas view.
        runCatching { ResourcesCompat.getFont(context, R.font.atkinson_hyperlegible_bold) }.getOrNull()
            ?: runCatching { ResourcesCompat.getFont(context, R.font.atkinson_hyperlegible) }.getOrNull()
            ?: Typeface.SANS_SERIF
    }
    protected val wordTypefaceRegular: Typeface by lazy {
        runCatching { ResourcesCompat.getFont(context, R.font.atkinson_hyperlegible) }.getOrNull() ?: Typeface.SANS_SERIF
    }
    protected val monoTypefaceBold: Typeface by lazy {
        runCatching { ResourcesCompat.getFont(context, R.font.anonymous_pro_bold) }.getOrNull()
            ?: runCatching { ResourcesCompat.getFont(context, R.font.anonymous_pro) }.getOrNull()
            ?: Typeface.MONOSPACE
    }
    protected val monoTypefaceRegular: Typeface by lazy {
        runCatching { ResourcesCompat.getFont(context, R.font.anonymous_pro) }.getOrNull() ?: Typeface.MONOSPACE
    }

    // Pre-allocated final Paints, RectFs, Paths (§4.1: zero allocation inside onDraw)
    protected val paintFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    protected val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    protected val paintAccent = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    protected val paintWord = Paint(Paint.ANTI_ALIAS_FLAG).apply { typeface = wordTypefaceBold }
    protected val paintWordSub = Paint(Paint.ANTI_ALIAS_FLAG).apply { typeface = wordTypefaceRegular }
    protected val paintMono = Paint(Paint.ANTI_ALIAS_FLAG).apply { typeface = monoTypefaceBold }
    protected val paintMonoSub = Paint(Paint.ANTI_ALIAS_FLAG).apply { typeface = monoTypefaceRegular }

    protected val boundsRect = RectF()
    protected val cardRect = RectF()
    protected val cellRect = RectF()
    protected val trackRect = RectF()
    protected val thumbRect = RectF()
    protected val vectorPath = Path()

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * §6 & §7: Fire 1/4/8/10ms one-shot haptic and Float-PCM synth from the exact same call site.
     */
    protected fun fireFeel(hapticMs: Long, freqHz: Float = 520f) {
        // CONTRACT (Round 0b honesty fix): freqHz is honored ONLY by the 1L
        // branch (playRotaryDetent). The 4L/8L/10L branches play canned
        // engine effects and ignore pitch — call sites no longer pass fake
        // pitches to them. Sites whose pitch carried meaning (rocker
        // direction 680/420, sled detent ladder, hold-escalation rise) get
        // real pitched engine variants in the Round 2 feel pass.
        val engine = soundEngine
        if (engine != null) {
            // Engine only (Ren's revamp): the old body ALSO fired a raw
            // one-shot here, so every tap buzzed twice and felt like mush.
            when (hapticMs) {
                1L -> engine.playRotaryDetent(freqHz.toDouble())
                4L -> engine.playMechanicalLatch(true)
                8L -> engine.playFirmClick()
                // Slice E (E4 §7.9's census): the else branch spent
                // the completion pulse on generic heavy feels —
                // the pulse has exactly one lawful spend left (the
                // finish's E700). Heavy generic feel = the firm
                // click.
                else -> engine.playFirmClick()
            }
            return
        }
        // No engine attached (previews): plain one-shot fallback.
        try {
            if (vibrator?.hasVibrator() == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(hapticMs, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (_: Exception) {}
    }

    /**
     * Weighted knock for events whose WEIGHT is the information (the
     * Loaded Wheel's seat, the strait's gate landings): duration and
     * amplitude are real parameters, not effect selectors — the one
     * place fireFeel's canned ladder cannot speak. Engine when
     * attached, calibrated one-shot otherwise. Never both (the
     * double-buzz lesson stands).
     */
    protected fun fireWeightedKnock(durationMs: Int, amplitude01: Float, pitchHz: Float = 300f) {
        val engine = soundEngine
        if (engine != null) {
            engine.playSeatKnock(durationMs, amplitude01, pitchHz)
            return
        }
        try {
            if (vibrator?.hasVibrator() == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val amp = (amplitude01.coerceIn(0f, 1f) * 255f).toInt().coerceIn(1, 255)
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs.toLong().coerceIn(6, 32), amp)
                )
            }
        } catch (_: Exception) {}
    }

    /**
     * Draws a mixed label where words use Google Sans and quantities use Space Mono (§2).
     */
    protected fun drawMixedWordAndMono(
        canvas: Canvas,
        wordPrefix: String,
        monoNumber: String,
        wordSuffix: String,
        x: Float,
        baselineY: Float,
        wordSizePx: Float,
        monoSizePx: Float,
        wordColor: Int,
        monoColor: Int,
        centered: Boolean = false
    ) {
        paintWord.textSize = wordSizePx
        paintWord.color = wordColor
        paintWord.textAlign = Paint.Align.LEFT

        paintMono.textSize = monoSizePx
        paintMono.color = monoColor
        paintMono.textAlign = Paint.Align.LEFT

        val w1 = if (wordPrefix.isNotEmpty()) paintWord.measureText(wordPrefix) else 0f
        val w2 = if (monoNumber.isNotEmpty()) paintMono.measureText(monoNumber) else 0f
        val w3 = if (wordSuffix.isNotEmpty()) paintWord.measureText(wordSuffix) else 0f
        val totalW = w1 + w2 + w3
        var curX = if (centered) x - totalW * 0.5f else x

        if (wordPrefix.isNotEmpty()) {
            canvas.drawText(wordPrefix, curX, baselineY, paintWord)
            curX += w1
        }
        if (monoNumber.isNotEmpty()) {
            canvas.drawText(monoNumber, curX, baselineY, paintMono)
            curX += w2
        }
        if (wordSuffix.isNotEmpty()) {
            canvas.drawText(wordSuffix, curX, baselineY, paintWord)
        }
    }

    private val dotDigitMasks = intArrayOf(
        0b111_101_101_101_111, // 0
        0b010_110_010_010_111, // 1
        0b111_001_111_100_111, // 2
        0b111_001_111_001_111, // 3
        0b101_101_111_001_001, // 4
        0b111_100_111_001_111, // 5
        0b111_100_111_101_111, // 6
        0b111_001_001_001_001, // 7
        0b111_101_111_101_111, // 8
        0b111_101_111_001_111  // 9
    )

    /**
     * Draws a 3x5 dot-matrix digit on the 4dp grid (§2 & §4.2 #8 TileNumberView).
     */
    protected fun drawDotMatrixDigit(
        canvas: Canvas,
        digit: Int,
        left: Float,
        top: Float,
        tilePx: Float,
        gapPx: Float,
        onColor: Int,
        offColor: Int
    ) {
        val mask = dotDigitMasks[digit.coerceIn(0, 9)]
        val step = tilePx + gapPx
        for (row in 0 until 5) {
            for (col in 0 until 3) {
                val bit = 1 shl (14 - (row * 3 + col))
                paintFill.color = if ((mask and bit) != 0) onColor else offColor
                val cx = left + col * step
                val cy = top + row * step
                canvas.drawRect(cx, cy, cx + tilePx, cy + tilePx, paintFill)
            }
        }
    }
}

/**
 * Issue #123 (dial-precision): the wheel's pure angle/value arithmetic,
 * extracted so the precision law is unit-testable without a View.
 *
 * The scale is 1.8 degrees per minute from the strait's far shore
 * (126 degrees = 10 minutes) to 432 degrees (= 180). FLOW seats at
 * 108, across the strait whose centreline is 117.
 *
 * PRECISION LAW: every whole minute 10..180 is a real stop — 47 is
 * as lawful as 45. The printed 5-minute ticks remain the scale's
 * majors/minors (with 1-minute micro-ticks between them); they are
 * print, not the limit of travel.
 *
 * HYSTERESIS LAW: under the finger a value changes only when the
 * angle has crossed the midpoint toward the neighbour by
 * [HYSTERESIS_MIN] minutes past it — a finger trembling on a
 * boundary holds its value instead of toggling it (and re-firing
 * its tick) on every crossing. Free motion and seating use the
 * plain nearest-minute read: where the wheel lands is exact.
 *
 * DETENT PITCH LAW: ONE ladder speaks for a detent, here and only
 * here — 340Hz at 5 minutes rising linearly to 880Hz at 180. Any
 * other caller that reports a dial value must stay silent, or the
 * user hears two pitches for one stop and the ladder lies.
 */
internal object DialPrecision {
    const val FLOW_ANGLE = 108f
    const val STRAIT_CENTRE = 117f
    const val SCALE_ORIGIN = 126f
    const val DEG_PER_MIN = 1.8f
    const val HYSTERESIS_MIN = 0.22f

    fun angleForMinutes(mins: Int): Float =
        if (mins < 10) FLOW_ANGLE else SCALE_ORIGIN + (mins - 10) * DEG_PER_MIN

    /** Plain nearest-minute read of an angle (FLOW north of the strait). */
    fun valueForAngle(angle: Float): Int {
        if (angle < STRAIT_CENTRE) return 5
        return (10 + ((angle - SCALE_ORIGIN) / DEG_PER_MIN).roundToInt()).coerceIn(10, 180)
    }

    /** Drag read with boundary hysteresis around [current]. */
    fun valueForAngle(angle: Float, current: Int): Int {
        if (angle < STRAIT_CENTRE) return 5
        if (current < 10) return valueForAngle(angle)
        val raw = 10f + (angle - SCALE_ORIGIN) / DEG_PER_MIN
        val nearest = raw.roundToInt().coerceIn(10, 180)
        if (nearest == current) return current
        return if (nearest > current) {
            if (raw >= current + 0.5f + HYSTERESIS_MIN) (raw - HYSTERESIS_MIN).roundToInt().coerceIn(10, 180) else current
        } else {
            if (raw <= current - 0.5f - HYSTERESIS_MIN) (raw + HYSTERESIS_MIN).roundToInt().coerceIn(10, 180) else current
        }
    }

    /** The single detent pitch ladder (see law above). */
    fun detentPitchHz(minutes: Int): Float =
        340f + ((minutes - 5).coerceIn(0, 175) / 175f) * 540f
}

/**
 * ============================================================================
 * 2. ROTARY WHEEL VIEW (DEVELOPER-GUIDE.pdf §4.1 & §4.2 #1 RotaryWheel.java)
 * ============================================================================
 * - Self-contained rotational time picker dial (`5..180` min; `<10m` = Open Flow).
 * - Preallocated paints, arcBounds, velocity tracker, and `CurveB` (280ms) settle animator.
 * - Fires 1ms/4ms detent ticks + Float-PCM synth clicks at every minute crossing.
 */
class RotaryWheelView(context: Context) : BlockitCanvasView(context) {

    var minutesValue: Int = 50
        set(value) {
            val clamped = value.coerceIn(5, 180)
            if (field != clamped) {
                field = clamped
                if (!isDragging && !inPhysics) {
                    wheelAngleDeg = minutesToAngle(clamped)
                }
                invalidate()
            }
        }

    var studyBlocksCount: Int = 2
        set(value) {
            if (field != value) {
                field = value.coerceIn(1, 8)
                // Tetrad closure (Vega): landing on 4 or 8 joins four
                // wells into one bar; the bar lands at 88% ink and
                // settles to full over 160ms - the one place the hub
                // is allowed to feel heavier. Settle never gates input.
                if (field == 4 || field == 8) {
                    closureAnimator?.cancel()
                    barInkFraction = 0.88f
                    closureAnimator = ValueAnimator.ofFloat(0.88f, 1f).apply {
                        duration = 160
                        addUpdateListener {
                            barInkFraction = it.animatedValue as Float
                            invalidate()
                        }
                        start()
                    }
                }
                invalidate()
            }
        }

    var isLockedSession: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    // Zug-II §4.2's freezeAtSeal completion: the
                    // wheel's physics HALTS at the seal — no coast
                    // outlives t=0 — and any half-finished hub
                    // press dies with it (its return runs so the
                    // dip never parks mid-travel).
                    stopPhysics()
                    hubPressed = false
                    hubTarget = null
                    startHubReturn()
                }
                invalidate()
            }
        }

    var onMinutesChanged: ((Int) -> Unit)? = null
    /** Issue #123: the seat the current physics run will land on,
     *  reported the moment the spring takes the wheel — the host
     *  previews it ("LANDS 47") so where you let go is never a
     *  surprise. Null when no seat is committed (coasting free). */
    var onSeatTargetChanged: ((Int?) -> Unit)? = null
    var onCycleBlocksCount: (() -> Unit)? = null
    var onSetBlocksCount: ((Int) -> Unit)? = null

    // Tetrad hub touch state (Vega press law): press-and-release inside
    // the hub commits; drag-off or release outside cancels silently.
    private var hubPressed: Boolean = false
    private var hubTarget: Int? = null // null target + hubPressed = centre cycle press
    private var barInkFraction: Float = 1f
    private var closureAnimator: ValueAnimator? = null

    // Slice E, E5a (Nabe §4): the press depth — 0 at rest, 1 at the
    // full filed 1dp. Exactly two readers (the hub-group translate
    // and the core's shader offset in onDraw) and one writer (the
    // two animator listeners below); the test accessors are the
    // sanctioned extra readers. Depth never enters loadInertia(),
    // the wells, the bars, or any haptic.
    private var hubPressDepth: Float = 0f
    private var hubPressAnimator: ValueAnimator? = null
    private var hubPressReturning: Boolean = false
    private var hubPointerInside: Boolean = true

    internal val hubPressDepthForTest: Float get() = hubPressDepth
    internal val hubPressAnimatorForTest: ValueAnimator? get() = hubPressAnimator

    companion object {
        /** E5a device-pass gate (Nabe §6): 1 = the core's travel
         *  ships, 0 = travel dead, the rest core ships alone.
         *  Flipped ONLY on the DEVICE_PASS_E5A result — never on
         *  a render, a screenshot, or taste. */
        const val CORE_TRAVEL_GATE = 1
    }

    private fun startHubDip() {
        hubPressAnimator?.cancel()
        hubPressReturning = false
        val anim = ValueAnimator.ofFloat(hubPressDepth, 1f)
        anim.duration = 45L
        anim.interpolator = MonolithCurves.HubPressDip
        anim.addUpdateListener {
            hubPressDepth = it.animatedValue as Float
            invalidate()
        }
        hubPressAnimator = anim
        anim.start()
    }

    private fun startHubReturn() {
        // Already returning is a no-op (Nabe §4.4): a MOVE-crossing
        // return is never restarted by the UP that follows it.
        if (hubPressReturning && hubPressAnimator?.isRunning == true) return
        if (hubPressDepth == 0f) return
        hubPressAnimator?.cancel()
        hubPressReturning = true
        val anim = ValueAnimator.ofFloat(hubPressDepth, 0f)
        anim.duration = 160L
        anim.interpolator = MonolithCurves.HubSeatReturn
        anim.addUpdateListener {
            hubPressDepth = it.animatedValue as Float
            invalidate()
        }
        hubPressAnimator = anim
        anim.start()
    }

    private var wheelAngleDeg: Float = minutesToAngle(50)
    private var lastTouchAngleDeg: Float = 0f
    private var isDragging: Boolean = false
    private var velocityTracker: VelocityTracker? = null

    // ------------------------------------------------------------------
    // BEYOND W2 frame + physics (Vega's Strait and Loaded Wheel; Dario's
    // one (theta, omega) integrator). The wheel's angle is UNWRAPPED:
    // FLOW seats at 108, the strait runs 108..126 (FLOW's landing step
    // 108..117, the smooth channel 117..126, centreline 117), numeric
    // stops 10..180 sit at 1.8 degrees per minute from 126 to 432
    // (=72 on the canvas), and the 6-o'clock dead sector is the 36
    // degrees between 72 and 108. Total travel FLOW->180 is 324.
    // ------------------------------------------------------------------
    private var inPhysics: Boolean = false
    private var physOmega: Float = 0f // degrees per second, signed
    private var physSpringTarget: Float? = null // non-null: springing to a well
    private var physLastNanos: Long = 0L
    private var physStartNanos: Long = 0L
    private var physTravelDeg: Float = 0f // travel this run, for the seat knock's honesty
    private var lastSeatPreview: Int? = null // last seat preview reported to the host
    private val choreographer: Choreographer = Choreographer.getInstance()
    private val physCallback = Choreographer.FrameCallback { nanos -> stepPhysics(nanos) }

    private fun minutesToAngle(mins: Int): Float {
        // ONE angle frame: FLOW is a seat, not the bottom of a ramp -
        // below 10 the value is the mode, and the mode sits across the
        // strait at 108. Numeric stops keep the filed scale, 1.8 deg
        // per minute, from the strait's far shore. Issue #123: the
        // scale is per-minute — a task-seeded 47 sits at its own true
        // angle and reads back 47, never a rounded neighbour.
        return DialPrecision.angleForMinutes(mins)
    }

    private fun valueForAngle(angle: Float): Int {
        // The strait's centreline is the value boundary: north of it
        // the wheel reads FLOW, south of it the numeric scale. Inside
        // the channel there are no values at all - the nearest shore
        // speaks. Issue #123: nearest WHOLE minute, not nearest 5.
        return DialPrecision.valueForAngle(angle)
    }

    /** Relative inertia of the loaded wheel: the block count has mass.
     *  FLOW rides unloaded - an open session is the lightest wheel. */
    private fun loadInertia(): Float =
        if (minutesValue < 10) 1f else 1f + 0.14f * (studyBlocksCount - 1).coerceAtLeast(0)

    /** Viscous coast constant: tau rises 0.34s -> 0.48s across the load. */
    private fun coastTau(): Float = 0.34f * sqrt(loadInertia())

    /** Seat knock weight by count: 16ms @ 0.85 at one block, linear
     *  to 24ms @ 1.0 at eight. The pawl stops the mass it is given. */
    private fun fireSeatKnockForCount() {
        val w = ((studyBlocksCount - 1).coerceIn(0, 7)) / 7f
        fireWeightedKnock(
            durationMs = 16 + (8f * w).toInt(),
            amplitude01 = 0.85f + 0.15f * w,
            pitchHz = 320f - 90f * w
        )
    }

    /** Broken Flare (Mara): per-tooth specular variance, computed ONCE
     *  from a fixed seed - 0.84..1.16, teeth 17 and 83 hand-set low
     *  (0.62 / 0.68), the two teeth that left the factory wrong. There
     *  is no writer for this table after construction, ever: a runtime
     *  write would be history written in light, and reverts on sight. */
    private val toothFlare: FloatArray by lazy {
        FloatArray(120) { i ->
            when (i) {
                17 -> 0.62f
                83 -> 0.68f
                else -> {
                    val x = sin(i * 127.1) * 43758.5453
                    0.84f + 0.32f * (x - floor(x)).toFloat()
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        val cx = w * 0.5f
        val cy = h * 0.5f
        val outerR = min(w, h) * 0.45f
        val rimR = outerR - dp(16f)
        val innerHubR = outerR * 0.494f

        // 1. Socket + wheel body (Ren, craft pass after TP-7/ET66 study):
        // the wheel sits IN the panel — a socket ring one tonal step DOWN
        // (surfaceVariant) with its inner edge shaded top-dark/bottom-light,
        // and the disc itself at panel tone (surface), TP-7 style: separation
        // by seam and sheen, not by a value jump.
        val socketR = outerR + dp(7f)
        paintFill.color = tokSurfaceVariant()
        canvas.drawCircle(cx, cy, socketR, paintFill)
        // Socket shading (chrome C8, one lamp): the old two flat arcs
        // (a dark half, a light half) become ONE graded ring on the
        // lamp curve — segmented, 3dp uniform, no step anywhere:
        // s follows 0.16 + 0.66 cos^3(azimuth - 245.2) normalised;
        // white rides at 36s, black at 42(1-s), black under white.
        boundsRect.set(cx - socketR + dp(1.5f), cy - socketR + dp(1.5f), cx + socketR - dp(1.5f), cy + socketR - dp(1.5f))
        paintStroke.strokeWidth = dp(3f)
        paintStroke.strokeCap = Paint.Cap.BUTT
        var socketDeg = 0f
        while (socketDeg < 360f) {
            val az = socketDeg + 90f
            var dAz = (az - 245.2f) % 360f
            if (dAz > 180f) dAz -= 360f
            if (dAz < -180f) dAz += 360f
            val cosAz = cos(dAz * (PI.toFloat() / 180f))
            val curve = if (kotlin.math.abs(dAz) <= 90f) {
                0.16f + 0.66f * cosAz * cosAz * cosAz
            } else 0.16f
            val sN = (curve / 0.82f).coerceIn(0f, 1f)
            paintStroke.color = (kotlin.math.round(42f * (1f - sN)).toInt() shl 24) or 0x000000
            canvas.drawArc(boundsRect, socketDeg, 3.5f, false, paintStroke)
            paintStroke.color = (kotlin.math.round(36f * sN).toInt() shl 24) or 0x00FFFFFF
            canvas.drawArc(boundsRect, socketDeg, 3.5f, false, paintStroke)
            socketDeg += 3f
        }

        paintFill.color = tokSurface()
        canvas.drawCircle(cx, cy, outerR, paintFill)
        // Chamfer, never a border (BEYOND D2): the full catch-hairline
        // loop is deleted - a loop reads as a card. The catch itself
        // is re-voiced by chrome C8 below: segmented, on the lamp.
        val chamferR = outerR - dp(10f)
        boundsRect.set(cx - chamferR, cy - chamferR, cx + chamferR, cy + chamferR)
        // The chamfer catch (chrome C8, one lamp): the old single arc
        // sat at the OLD light (azimuth 250-350) in a locked surface
        // tone. Now the catch is segmented (2dp stroke) in the lamp
        // tint #FFF6EC, alpha per azimuth 0.16 + 0.66 cos^3(az -
        // 245.2), zero beyond +/-90 of the lamp — the catch lives
        // where the lamp is, and nowhere else.
        paintStroke.strokeWidth = dp(2f)
        paintStroke.strokeCap = Paint.Cap.BUTT
        var chamferDeg = 65.2f
        while (chamferDeg < 245.2f) {
            val az = chamferDeg + 90f
            val dAz = az - 245.2f
            val cosAz = cos(dAz * (PI.toFloat() / 180f))
            val alphaF = 0.16f + 0.66f * cosAz * cosAz * cosAz
            val a = (255f * alphaF).toInt().coerceIn(0, 255)
            paintStroke.color = (a shl 24) or 0x00FFF6EC
            canvas.drawArc(boundsRect, chamferDeg, 2.5f, false, paintStroke)
            chamferDeg += 2f
        }

        // The Strait's channel floor (Vega): across the strait the rim
        // drops one tone step to recess and goes smooth - a cut through
        // the rim between FLOW's shore (117) and the 10 shore (126),
        // its two edges catching the light. Drawn under the teeth;
        // there are no teeth here to draw over it.
        boundsRect.set(
            cx - (outerR - dp(2.75f)), cy - (outerR - dp(2.75f)),
            cx + (outerR - dp(2.75f)), cy + (outerR - dp(2.75f))
        )
        paintStroke.color = if (isNightMode) 0xFF100E0C.toInt() else tokSurfaceVariant()
        paintStroke.strokeWidth = dp(3.5f)
        paintStroke.strokeCap = Paint.Cap.BUTT
        canvas.drawArc(boundsRect, 117f, 9f, false, paintStroke)
        paintStroke.strokeWidth = dp(1f)
        for ((edgeDeg, edgeColor) in listOf(117f to 0x33FFFFFF, 126f to 0x2E000000)) {
            val er = edgeDeg * (PI.toFloat() / 180f)
            paintStroke.color = edgeColor
            canvas.drawLine(
                cx + cos(er) * (outerR - dp(4.5f)), cy + sin(er) * (outerR - dp(4.5f)),
                cx + cos(er) * (outerR - dp(1f)), cy + sin(er) * (outerR - dp(1f)),
                paintStroke
            )
        }

        // Knurled rim (T1 + Mara's light + the Broken Flare): fine
        // radial teeth at the filed 3-degree pitch on the disc's outer
        // band. The light teeth carry Mara's specular - 0.16 + 0.66 t^3
        // under her one fixed lamp (azimuth 245.2, chrome C8) -
        // each tooth dimmed or lifted by its compile-time flare, so
        // teeth 17 and 83 sit visibly wrong inside the bright zone.
        // The knurl is screen-fixed material: it cannot alias, so the
        // rest-state form of her law is the whole of it here; the swept
        // integral lands the day the rim itself rotates. Teeth are CUT
        // across FLOW's basin (+/-13.5 of the FLOW seat) and the whole
        // strait - inside the break there is no grip, no tick, no value.
        paintStroke.strokeWidth = dp(1.3f)
        var tooth = 0
        var toothDeg = 0f
        while (toothDeg < 360f) {
            if (toothDeg > 94.5f && toothDeg < 126f) {
                tooth++
                toothDeg += 3f
                continue
            }
            val rad = toothDeg * (PI.toFloat() / 180f)
            paintStroke.color = if (tooth % 2 == 0) {
                0x2E000000
            } else {
                // One lamp (chrome C8): the specular re-centres from
                // the old canvas-220 light to Mara's lamp at azimuth
                // 245.2 (azimuth = canvas angle + 90 in this view's
                // convention). Same cos^3 curve, same compile-time
                // flare (teeth 17/83 stay broken); the peak is capped
                // at 14% by day, 8% by night — the alpha factor is
                // 0.17 / 0.10 against the curve's 0.82 maximum.
                val toothAz = (toothDeg + 90f) % 360f
                var dAz = toothAz - 245.2f
                if (dAz > 180f) dAz -= 360f
                if (dAz < -180f) dAz += 360f
                val t = max(0f, cos(dAz * (PI.toFloat() / 180f)))
                val spec = 0.16f + 0.66f * t * t * t * toothFlare[tooth]
                val a = (255f * spec * if (isNightMode) 0.10f else 0.17f).toInt().coerceIn(0, 255)
                (a shl 24) or 0x00FFFFFF
            }
            canvas.drawLine(
                cx + cos(rad) * (outerR - dp(4.5f)), cy + sin(rad) * (outerR - dp(4.5f)),
                cx + cos(rad) * (outerR - dp(1f)), cy + sin(rad) * (outerR - dp(1f)),
                paintStroke
            )
            tooth++
            toothDeg += 3f
        }

        // 2. Active arc track on the wheel rim
        // The drawn sweep comes from wheelAngleDeg — the value the settle
        // animator and the drag actually move (Round 0 fix: onDraw used to
        // derive everything from minutesValue, so the settle animated a
        // variable nothing read and the thumb teleported).
        val sweepNormalized = ((wheelAngleDeg - 108f) / 324f).coerceIn(0f, 1f)
        boundsRect.set(cx - rimR, cy - rimR, cx + rimR, cy + rimR)
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(4f)
        paintStroke.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(boundsRect, 108f, 324f, false, paintStroke)

        if (sweepNormalized > 0.002f) {
            // The sweep (chrome C8): no longer the primary ink — it
            // takes the secondary text tone by day and the glyph at
            // 70% by night. The reader carries the position; the
            // sweep is its trace, one voice quieter.
            paintStroke.color = if (isNightMode) {
                (0xB3 shl 24) or (tokOnSurface() and 0x00FFFFFF)
            } else {
                tokTextSecondary()
            }
            paintStroke.strokeWidth = dp(4f)
            canvas.drawArc(boundsRect, 108f, 324f * sweepNormalized, false, paintStroke)
        }

        // 3. True detent scale (Round 0 fix, W2 frame; issue #123
        // precision pass): ticks exist ONLY where the wheel can
        // actually stop. The wheel now stops on EVERY whole minute,
        // so the print gains 1-minute micro-ticks — short, faint,
        // engraved — between the 5-minute stops; the 5-minute ticks
        // below keep their filed voices (majors every 30 min, mids
        // every 15). Inside the strait there is no tick at all: the
        // break in the scale is a break in the print too. FLOW prints
        // as a word in the hero, never as a tick or a number here.
        paintStroke.strokeWidth = dp(0.7f)
        paintStroke.color = (0x59 shl 24) or (tokTextSecondary() and 0x00FFFFFF)
        for (m in 10..180) {
            if (m % 5 == 0) continue
            val deg = minutesToAngle(m)
            val rad = deg * (PI.toFloat() / 180f)
            canvas.drawLine(
                cx + cos(rad) * (outerR - dp(13.5f)),
                cy + sin(rad) * (outerR - dp(13.5f)),
                cx + cos(rad) * (outerR - dp(11f)),
                cy + sin(rad) * (outerR - dp(11f)),
                paintStroke
            )
        }
        for (m in 10..180 step 5) {
            val deg = minutesToAngle(m)
            val rad = deg * (PI.toFloat() / 180f)
            val isMajor = m % 30 == 0
            val isMid = m % 15 == 0
            val rOuter = outerR - dp(11f)
            val rInner = when {
                isMajor -> outerR - dp(21f)
                isMid -> outerR - dp(18.5f)
                else -> outerR - dp(16f)
            }
            paintStroke.strokeWidth = when {
                isMajor -> dp(1.8f)
                isMid -> dp(1.3f)
                else -> dp(1f)
            }
            // Print voices (BEYOND D9): ticks speak by class, never by
            // sweep - the rim arc carries the sweep. Majors: ink / night
            // secondary. Mediums: secondary / night muted. Minors: one
            // engraved step off the disc (surfaceVariant; D9's literal
            // ground-on-recess assumes a recess disc - ours is surface-
            // toned by the locked staircase, so the step goes down).
            // Tick voices (chrome C8): majors at night take the
            // glyph at 80% (they were text-2, reading as minor);
            // minors take text-2 at 55% in BOTH modes (day was the
            // recess tone — a tick the same tone as the socket).
            // Canvas token note: this family's text-2 IS #615B5A
            // (LIGHT_TEXT_SECONDARY); the spec's #5F5856 is the
            // Compose scheme's spelling of the same tier.
            paintStroke.color = when {
                isMajor -> if (isNightMode) {
                    (0xCC shl 24) or (tokOnSurface() and 0x00FFFFFF)
                } else tokPrimary()
                isMid -> if (isNightMode) tokOnSurfaceVariant() else tokTextSecondary()
                else -> (0x8C shl 24) or (tokTextSecondary() and 0x00FFFFFF)
            }
            canvas.drawLine(
                cx + cos(rad) * rInner,
                cy + sin(rad) * rInner,
                cx + cos(rad) * rOuter,
                cy + sin(rad) * rOuter,
                paintStroke
            )
        }

        // Rim numerals at the majors only (Anonymous Pro 15): the scale
        // states the block length; the hero states the total — and
        // since issue #123 the Deck ALSO prints both, in words, under
        // the wheel ("47 MIN × 2 BLOCKS + BREAK = 1H 44M TOTAL"), so
        // the split read is stated, never left to inference.
        paintMono.color = tokTextSecondary()
        paintMono.textSize = sp(15f)
        paintMono.textAlign = Paint.Align.CENTER
        // Midway between the hub edge and the major ticks' inner ends —
        // a fixed offset from the rim lands the glyphs on the hub disc.
        val numeralR = innerHubR + (outerR - dp(21f) - innerHubR) * 0.5f
        for (m in 30..180 step 30) {
            val a = minutesToAngle(m) * (PI.toFloat() / 180f)
            canvas.drawText(
                m.toString(),
                cx + cos(a) * numeralR,
                cy + sin(a) * numeralR + sp(5f),
                paintMono
            )
        }

        // 4. Slider Dot Radius = 14dp (§3 Geometry) in THE Accent (#F55314) at active angle
        val activeDeg = wheelAngleDeg
        val activeRad = activeDeg * (PI.toFloat() / 180f)
        val dotX = cx + cos(activeRad) * rimR
        val dotY = cy + sin(activeRad) * rimR
        val sliderDotR = dp(14f)

        // Contact shadow under the moving dot, then the dot itself
        paintFill.color = 0x42000000
        canvas.drawCircle(dotX, dotY + dp(2.5f), sliderDotR, paintFill)
        // Ink, not accent (D1): the slide thumb owns the screen's one orange;
        // the wheel's state is carried by position on its scale (T6).
        // The reader (chrome C8): at night the disc drops from the
        // glyph tier to the sheet tone — a dark cap on the rim, not
        // a cream slab; by day it stays ink.
        paintAccent.color = if (isNightMode) tokSurfaceSheet() else tokPrimary()
        canvas.drawCircle(dotX, dotY, sliderDotR, paintAccent)

        // The pip becomes a DIMPLE (chrome C8): 6dp across, ground
        // at 40% — a pressed hollow, not a printed dot — with a 1dp
        // marker line at its leading edge (the direction of travel)
        // in the marker cream #FBF1EC, both modes.
        paintFill.color = (0x66 shl 24) or (tokBg() and 0x00FFFFFF)
        canvas.drawCircle(dotX, dotY, dp(3f), paintFill)
        val leadRad = activeRad + (PI.toFloat() / 2f)
        val leadCos = cos(leadRad)
        val leadSin = sin(leadRad)
        paintStroke.color = 0xFFFBF1EC.toInt()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawLine(
            dotX + leadCos * (sliderDotR - dp(1.5f)), dotY + leadSin * (sliderDotR - dp(1.5f)),
            dotX + leadCos * (sliderDotR - dp(6f)), dotY + leadSin * (sliderDotR - dp(6f)),
            paintStroke
        )

        // 5. Inner Raised Hub (colorSurfaceBright) — tap center to cycle Pomodoro block count
        // Soft contact shadow first: the hub sits ON the wheel, not in it.
        // The shadow is drawn UNTRANSLATED (Nabe §4.3): as the hub
        // group seats down its 1dp the shadow gap closes 2dp -> 1dp,
        // and that closing gap is the press's contact read.
        paintFill.color = 0x26000000
        canvas.drawCircle(cx, cy + dp(2f), innerHubR, paintFill)
        canvas.save()
        canvas.translate(0f, hubPressDepth * dp(1f))
        // ESR-13 (chrome C6): the hub disc steps to the sheet tone
        // at night — surfaceBright at night is the raised step
        // (#362C27), one step under the sheet the staircase assigns
        // the disc. Day keeps surfaceBright (porcelain).
        paintFill.color = if (isNightMode) tokSurfaceSheet() else tokSurfaceBright()
        canvas.drawCircle(cx, cy, innerHubR, paintFill)
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawCircle(cx, cy, innerHubR, paintStroke)

        // The hub core (chrome C8, REGISTERED at azimuth 315 —
        // deliberately NOT the lamp's 245.2; the Register keeps the
        // two apart and a builder who "unifies" them breaks it):
        // a specular core, full lift inside +/-9 of 315, cos^2
        // falloff to zero at +/-34, peak lift +10% day / +8% night
        // toward #FFF6EC, on the hub disc only — drawn before the
        // guide ring and wells so the tetrad paints over it.
        run {
            val lift = if (isNightMode) 0.08f else 0.10f
            // E5a's roll (Nabe §5): the light travels a further
            // depth x 2dp toward bearing 135 (canvas (+0.7071,
            // +0.7071)) relative to the cap — the wedge fan's
            // origin shifts, the clip stays on the cap's circle.
            // Same depression => same offset, at any count, any
            // hold, any approach speed. CORE_TRAVEL_GATE is the
            // device-pass kill switch for this term alone.
            val offMag = hubPressDepth * dp(2f) * CORE_TRAVEL_GATE
            val ccx = cx + offMag * 0.7071f
            val ccy = cy + offMag * 0.7071f
            val corePath = android.graphics.Path()
            corePath.addCircle(cx, cy, innerHubR, android.graphics.Path.Direction.CW)
            canvas.save()
            canvas.clipPath(corePath)
            var coreDeg = 315f - 34f
            while (coreDeg < 315f + 34f) {
                val dAz = kotlin.math.abs(coreDeg - 315f)
                val f = when {
                    dAz <= 9f -> 1f
                    else -> {
                        val c = cos((dAz - 9f) / 25f * (PI.toFloat() / 2f))
                        c * c
                    }
                }
                if (f > 0.003f) {
                    val a = (255f * lift * f).toInt().coerceIn(0, 255)
                    paintFill.color = (a shl 24) or 0x00FFF6EC
                    val r0 = (coreDeg - 90f) * (PI.toFloat() / 180f)
                    val r1 = (coreDeg + 2f - 90f) * (PI.toFloat() / 180f)
                    vectorPath.reset()
                    vectorPath.moveTo(ccx, ccy)
                    vectorPath.lineTo(ccx + cos(r0) * innerHubR, ccy + sin(r0) * innerHubR)
                    vectorPath.lineTo(ccx + cos(r1) * innerHubR, ccy + sin(r1) * innerHubR)
                    vectorPath.close()
                    canvas.drawPath(vectorPath, paintFill)
                }
                coreDeg += 2f
            }
            canvas.restore()
        }

        // Hub = TETRAD CLOSURE (Vega, BEYOND W2): the hub itself stays
        // pure geometry (the ritual object) — but since issue #123 the
        // count ALSO lives as a printed numeral + word under the wheel
        // on the Deck, because geometry alone let a stray hub tap add
        // ~60 silent minutes. Eight wells (O10dp) on
        // the R40 ring in two tetrads: wells 1-4 on the upper arc
        // centred at 12 o'clock, wells 5-8 on the lower. At count 4
        // (and again at 8) the filled wells join into a solid bar on
        // the same ring, so the object count by count runs 1,2,3,1,2,
        // 3,4,2 - never above four, and the heaviest counts are the
        // simplest pictures. The tetrad boundary is printed as a 12
        // degree break in the faint guide ring at the two side
        // azimuths (90/270): Vega's "+12 extra gap between well 4 and
        // 5" cannot exist as spacing on a closed 45-degree ring (it
        // sums to 372), so the break lives in the guide ring instead -
        // catch logged with her seat for her re-seat.
        val isFlow = minutesValue < 10
        val ringR = dp(40f)
        val wellAzimuths = floatArrayOf(-67.5f, -22.5f, 22.5f, 67.5f, 112.5f, 157.5f, 202.5f, 247.5f)
        fun inkWith(color: Int, frac: Float): Int {
            val a = ((color ushr 24) * frac).toInt().coerceIn(0, 255)
            return (a shl 24) or (color and 0x00FFFFFF)
        }
        // Guide ring first (under everything): two arcs, breaks at the
        // tetrad boundaries. FLOW dims the whole assembly to ~40%.
        boundsRect.set(cx - ringR, cy - ringR, cx + ringR, cy + ringR)
        paintStroke.color = inkWith(tokHairline(), if (isFlow) 0.4f else 1f)
        paintStroke.strokeWidth = dp(1f)
        paintStroke.strokeCap = Paint.Cap.BUTT
        canvas.drawArc(boundsRect, -174f, 168f, false, paintStroke)
        canvas.drawArc(boundsRect, 6f, 168f, false, paintStroke)
        // Closure bars: ink, not relief - same tone as a filled well,
        // no shadow. The just-closed bar carries the settle fraction.
        if (!isFlow && studyBlocksCount >= 4) {
            paintStroke.strokeWidth = dp(10f)
            paintStroke.strokeCap = Paint.Cap.ROUND
            val bar0Frac = if (studyBlocksCount == 4) barInkFraction else 1f
            paintStroke.color = inkWith(tokPrimary(), bar0Frac)
            canvas.drawArc(boundsRect, -157.5f, 135f, false, paintStroke)
            if (studyBlocksCount >= 8) {
                paintStroke.color = inkWith(tokPrimary(), barInkFraction)
                canvas.drawArc(boundsRect, 22.5f, 135f, false, paintStroke)
            }
        }
        // Wells over the bar ends read as swellings on the bar.
        for (i in 0 until 8) {
            val az = wellAzimuths[i] * (PI.toFloat() / 180f)
            val wx = cx + sin(az) * ringR
            val wy = cy - cos(az) * ringR
            val filled = !isFlow && i < studyBlocksCount
            // Wells (chrome C8): a filled well at night was raw
            // marker cream (#FFE4CF via tokPrimary) — the brightest
            // mass on the hub. It takes the glyph at 80% now; the
            // well radius steps 5 -> 4dp. Closure bars (above) keep
            // tokPrimary by the same ruling — bars are structure,
            // wells are print.
            paintFill.color = inkWith(
                when {
                    filled && isNightMode -> tokOnSurface()
                    filled -> tokPrimary()
                    else -> tokSurfaceVariant()
                },
                when {
                    isFlow -> 0.4f
                    filled && isNightMode -> 0.8f
                    else -> 1f
                }
            )
            canvas.drawCircle(wx, wy, dp(4f), paintFill)
        }
        canvas.restore()

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Zug-II §4.2: while locked the wheel CONSUMES and drops —
        // returning false would hand the gesture to whatever sits
        // beneath, and a sealed Deck has nothing beneath worth
        // feeding.
        if (isLockedSession) return true

        val cx = width * 0.5f
        val cy = height * 0.5f
        val dx = event.x - cx
        val dy = event.y - cy
        val dist = sqrt(dx * dx + dy * dy)
        val innerHubR = min(width, height) * 0.45f * 0.494f

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                stopPhysics()
                if (dist <= innerHubR) {
                    // Tetrad press law (Vega): press-and-release commits,
                    // drag-off cancels - nothing fires on DOWN any more.
                    // A well direct-sets (well k -> count k); a closed bar
                    // sets its tetrad's end (4 or 8); anywhere else in the
                    // hub is the cycle press. Under FLOW the hub is
                    // display-only: the press registers nothing.
                    hubPressed = true
                    hubTarget = null
                    if (minutesValue >= 10) {
                        // E5a (Nabe §4.4): the dip starts on DOWN —
                        // under FLOW nothing starts (the hub visibly
                        // rests; a dip with no possible commit would
                        // be a tease-motion). The dip never gates
                        // the commit.
                        hubPointerInside = true
                        startHubDip()
                    }
                    if (minutesValue >= 10) {
                        val ringR = dp(40f)
                        val wellAz = floatArrayOf(-67.5f, -22.5f, 22.5f, 67.5f, 112.5f, 157.5f, 202.5f, 247.5f)
                        var bestWell = -1
                        var bestDist = dp(14f)
                        for (i in 0 until 8) {
                            val a = wellAz[i] * (PI.toFloat() / 180f)
                            val wx = sin(a) * ringR
                            val wy = -cos(a) * ringR
                            val ddx = dx - wx
                            val ddy = dy - wy
                            val d = sqrt(ddx * ddx + ddy * ddy)
                            if (d < bestDist) { bestDist = d; bestWell = i }
                        }
                        if (bestWell >= 0) {
                            hubTarget = bestWell + 1
                        } else if (abs(dist - ringR) <= dp(7f)) {
                            val az = (atan2(dx, -dy) * (180.0 / PI)).toFloat()
                            val normAz = if (az < -90f) az + 360f else az
                            if (studyBlocksCount >= 4 && az >= -67.5f && az <= 67.5f) hubTarget = 4
                            else if (studyBlocksCount >= 8 && normAz >= 112.5f && normAz <= 247.5f) hubTarget = 8
                        }
                    }
                    return true
                }
                isDragging = true
                velocityTracker?.recycle()
                velocityTracker = VelocityTracker.obtain().apply { addMovement(event) }
                updateMinutesFromTouch(dx, dy)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (hubPressed && minutesValue >= 10) {
                    // E5a (Nabe §4.4): the x1.1 crossing, edge-
                    // triggered — out starts the return, back in
                    // restarts the dip. MOVE never touches the
                    // target, never commits, never cancels the
                    // press; the shipped commit rule is untouched.
                    val inside = dist <= innerHubR * 1.1f
                    if (inside != hubPointerInside) {
                        hubPointerInside = inside
                        if (inside) startHubDip() else startHubReturn()
                    }
                }
                if (isDragging) {
                    velocityTracker?.addMovement(event)
                    updateMinutesFromTouch(dx, dy)
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (hubPressed) {
                    // Release inside the hub commits; release outside or
                    // a cancel is a silent drag-off - nothing fires.
                    val commit = event.actionMasked == MotionEvent.ACTION_UP && dist <= innerHubR * 1.1f
                    val target = hubTarget
                    hubPressed = false
                    hubTarget = null
                    // E5a (Nabe §4.4): return-first ordering — the
                    // return starts on the commit frame, before any
                    // callback below can re-enter the view.
                    startHubReturn()
                    parent?.requestDisallowInterceptTouchEvent(false)
                    if (commit && minutesValue >= 10) {
                        if (target != null) {
                            // Closure commits (landing on 4 or 8 from
                            // below) carry the heavier click; loose sets
                            // and de-closures stay light - breaking a
                            // hand must not feel like a warning.
                            val closure = (target == 4 || target == 8) && target > studyBlocksCount
                            fireFeel(if (closure) 8L else 4L)
                            onSetBlocksCount?.invoke(target)
                        } else {
                            fireFeel(4L)
                            onCycleBlocksCount?.invoke()
                        }
                    }
                    return true
                }
                if (isDragging) {
                    isDragging = false
                    // Release hands the wheel to the integrator (Dario's
                    // one (theta, omega) state): whatever angular
                    // velocity the finger leaves in the wheel is the
                    // coast's opening condition. The old projection
                    // (omega x 0.18s, then a canned animator) is gone -
                    // a wheel that is thrown now travels, pays the
                    // strait's toll if it crosses, and is caught by the
                    // wells or falls back from the gate, in the open.
                    var omegaDeg = 0f
                    val vt = velocityTracker
                    if (vt != null && event.actionMasked == MotionEvent.ACTION_UP) {
                        vt.computeCurrentVelocity(1000)
                        val r2 = (dx * dx + dy * dy).coerceAtLeast(1f)
                        omegaDeg = (((dx * vt.yVelocity - dy * vt.xVelocity) / r2) * (180.0 / PI).toFloat())
                            .coerceIn(-1200f, 1200f)
                    }
                    velocityTracker?.recycle()
                    velocityTracker = null
                    startPhysics(omegaDeg)
                }
                parent?.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun updateMinutesFromTouch(dx: Float, dy: Float) {
        var deg = (atan2(dy, dx) * (180.0 / PI)).toFloat()
        if (deg < 0f) deg += 360f
        // Unwrap into the travel frame: canvas 72..360 stays as is,
        // canvas 0..72 wraps to 360..432, and touches inside the
        // 36-degree dead sector (72..108 at 6 o'clock) clamp to the
        // nearer shore - the sector's upper half belongs to the 180
        // end, its lower half to FLOW.
        val unwrapped = when {
            deg < 72f -> deg + 360f
            deg < 90f -> 432f
            deg < 108f -> 108f
            else -> deg
        }
        // Under the finger the wheel is 1:1 at every load - mass
        // never fakes position; it speaks only in free motion.
        wheelAngleDeg = unwrapped.coerceIn(108f, 432f)
        applyValueFromAngle(fromDrag = true)
        invalidate()
    }

    /** Reads the value off the current angle and reports a change.
     *  Tick law: numeric crossings tick on the pitch ladder. A strait
     *  crossing under the finger is 9 degrees of haptic silence, and
     *  the landing speaks on the far shore - FLOW takes the gate
     *  knock (14ms @ 0.8, the deeper well catching), the 10 shore
     *  takes the ordinary detent. In free motion the gate knock is
     *  NOT spent on a crossing: a coast that falls back from the
     *  gate has landed nowhere, and the seat knock at the spring's
     *  end is the coast's only report of arrival. */
    private fun applyValueFromAngle(fromDrag: Boolean) {
        // Issue #123: under the finger the read carries boundary
        // hysteresis (DialPrecision) — trembling on a midpoint holds
        // the value instead of toggling it and re-firing its tick on
        // every crossing. In free motion the read is plain, so the
        // coast reports the exact minute it passes.
        val newValue = if (fromDrag) {
            DialPrecision.valueForAngle(wheelAngleDeg, minutesValue)
        } else {
            valueForAngle(wheelAngleDeg)
        }
        if (newValue == minutesValue) return
        val oldValue = minutesValue
        minutesValue = newValue
        when {
            oldValue < 10 && newValue >= 10 ->
                fireFeel(1L, 340f) // landing on the 10 shore
            oldValue >= 10 && newValue < 10 ->
                if (fromDrag) fireWeightedKnock(14, 0.8f, 300f) else fireFeel(1L, 340f)
            else -> {
                // ONE sound per detent, on the ONE ladder
                // (DialPrecision.detentPitchHz): the ViewModel's
                // setDialMinutes no longer sounds a second detent at
                // an unrelated pitch for wheel-originated changes
                // (issue #123, finding 4). The sound IS paired with
                // a real haptic — playRotaryDetent/playMechanicalLatch
                // each fire their calibrated waveform in the engine.
                val freq = DialPrecision.detentPitchHz(newValue)
                fireFeel(if (newValue % 15 == 0) 4L else 1L, freq)
            }
        }
        onMinutesChanged?.invoke(newValue)
    }

    private fun nearestStopAngle(angle: Float): Float = minutesToAngle(valueForAngle(angle))

    private fun stopPhysics() {
        if (inPhysics) {
            inPhysics = false
            choreographer.removeFrameCallback(physCallback)
        }
        physSpringTarget = null
        physOmega = 0f
        reportSeatPreview(null)
    }

    /** Issue #123: report the committed seat to the host, once per
     *  change — the Deck prints it under the wheel while the wheel
     *  travels, so the landing is known before it happens. */
    private fun reportSeatPreview(targetAngle: Float?) {
        val value = targetAngle?.let { valueForAngle(it) }
        if (value != lastSeatPreview) {
            lastSeatPreview = value
            onSeatTargetChanged?.invoke(value)
        }
    }

    /** Hands the wheel to the integrator at release. A slow release
     *  inside a well's capture goes straight to the spring; anything
     *  faster coasts on the load's physics until a well catches it,
     *  the strait refuses it, or the fling deadline springs it home. */
    private fun startPhysics(releaseOmega: Float) {
        stopPhysics()
        inPhysics = true
        physTravelDeg = 0f
        physStartNanos = System.nanoTime()
        physLastNanos = physStartNanos
        val target = nearestStopAngle(wheelAngleDeg)
        val captureVel = 540f / sqrt(loadInertia())
        val captureRadius = if (target == 108f) 6f else 4f
        if (abs(releaseOmega) < 30f ||
            (abs(releaseOmega) <= captureVel && abs(wheelAngleDeg - target) <= captureRadius + 2f)
        ) {
            physSpringTarget = target
            physOmega = releaseOmega
            reportSeatPreview(target)
        } else {
            physSpringTarget = null
            physOmega = releaseOmega.coerceIn(-1200f, 1200f)
        }
        choreographer.postFrameCallback(physCallback)
    }

    private fun stepPhysics(frameNanos: Long) {
        if (!inPhysics) return
        val dt = ((frameNanos - physLastNanos) / 1e9f).coerceIn(0.001f, 0.05f)
        physLastNanos = frameNanos
        val prevAngle = wheelAngleDeg
        val springTarget = physSpringTarget
        if (springTarget != null) {
            // The well spring. Written per unit mass - stiffness and
            // damping both scale with the load. Issue #123: zeta
            // rises 0.38 -> 0.72 — the old spring overshot TWICE past
            // the seat and re-seated the value elsewhere on every
            // fumbled release, so where you let go was not where you
            // landed. At 0.72 the wheel keeps ONE soft, living
            // overshoot (the analog settle Sarthak's ritual runs on)
            // and then seats, inside ~520ms at every count. FLOW's
            // well is the deeper one: the filed 260 vs 210 survives
            // as the ratio of rates.
            val omega0 = if (springTarget == 108f) 23.5f else 21f
            val accel = omega0 * omega0 * (springTarget - wheelAngleDeg) -
                2f * 0.72f * omega0 * physOmega
            physOmega += accel * dt
            wheelAngleDeg += physOmega * dt
            physTravelDeg += abs(wheelAngleDeg - prevAngle)
            if (abs(springTarget - wheelAngleDeg) < 0.05f && abs(physOmega) < 8f) {
                wheelAngleDeg = springTarget
                val seated = valueForAngle(springTarget)
                val travelled = physTravelDeg
                stopPhysics()
                if (seated != minutesValue) {
                    minutesValue = seated
                    onMinutesChanged?.invoke(seated)
                }
                // The knock is spent only on a real arrival - a
                // release already seated in its well is not an event.
                if (travelled >= 2f) fireSeatKnockForCount()
                invalidate()
                return
            }
        } else {
            // Coast. Viscous decay on the load's tau plus the eddy
            // brake - a constant deceleration strengthening with the
            // load - holds the hardest fling's seat deadline
            // (1,400ms) at every count.
            val iRel = loadInertia()
            physOmega *= exp(-dt / coastTau())
            val brake = 300f * sqrt(iRel) * dt
            physOmega = if (abs(physOmega) <= brake) 0f else physOmega - sign(physOmega) * brake
            wheelAngleDeg = (wheelAngleDeg + physOmega * dt).coerceIn(108f, 432f)
            physTravelDeg += abs(wheelAngleDeg - prevAngle)
            // The strait is a potential step at its centreline. A
            // coast arriving under 180 deg/s does not clear it: the
            // spring for the shore it came from takes the wheel and
            // seats it there, so a refusal sounds like an ordinary
            // arrival, never an alarm. A faster coast crosses and
            // pays the toll - 90 deg/s at the centreline, once.
            if ((prevAngle - 117f) * (wheelAngleDeg - 117f) < 0f) {
                if (abs(physOmega) < 180f) {
                    physSpringTarget = if (prevAngle < 117f) 108f else 126f
                    reportSeatPreview(physSpringTarget)
                } else {
                    physOmega -= sign(physOmega) * 90f
                    fireWeightedKnock(10, 0.5f, 280f)
                }
            }
            if (physSpringTarget == null) {
                // Well capture, the ends, a full stop, or the fling
                // deadline: the spring takes the wheel home.
                val nearest = nearestStopAngle(wheelAngleDeg)
                val radius = if (nearest == 108f) 6f else 4f
                val captureVel = 540f / sqrt(iRel)
                val atEnd = (wheelAngleDeg == 108f && physOmega < 0f) ||
                    (wheelAngleDeg == 432f && physOmega > 0f)
                if (atEnd || physOmega == 0f ||
                    (abs(wheelAngleDeg - nearest) <= radius && abs(physOmega) <= captureVel) ||
                    frameNanos - physStartNanos > 1_400_000_000L
                ) {
                    physSpringTarget = nearest
                    reportSeatPreview(nearest)
                }
            }
        }
        applyValueFromAngle(fromDrag = false)
        invalidate()
        if (inPhysics) choreographer.postFrameCallback(physCallback)
    }

    override fun onDetachedFromWindow() {
        stopPhysics()
        // Nabe R4's pre-priced fix: a press interrupted by detach
        // never parks mid-dip — the return runs from here.
        if (hubPressDepth > 0f) startHubReturn()
        super.onDetachedFromWindow()
    }
}

/**
 * The slide's pure gesture arithmetic (Slice D, joints D1–D5) — every
 * function here is a position function or a predicate, unit-tested
 * directly (Teo's A9 made executable). The view owns the machine; this
 * object owns the math the machine consults.
 */
internal object SlideGestureMath {

    /** The gate (Luz): displayed fraction where the Schmitt latch arms. */
    const val GATE = 0.68f

    /** Schmitt dead-band: the latch releases at GATE − 0.020. */
    const val GATE_RELEASE = 0.66f

    /** The catch (Vega): displayed crossing under contact commits. */
    const val CATCH = 0.84f

    /** The OFF-stop (Joss): hard clamp under a positively dead wire. */
    const val OFF_STOP = 0.65f

    /** F1: travel is fractions of ACTUAL travel, 180dp the floor. */
    const val TRAVEL_FLOOR_DP = 180f

    /** Rhea: a press shorter than this is a brush, not an order. */
    const val BRUSH_FLOOR_MS = 90L

    /**
     * Vega's memoryless gear: FREE 1:1 to the gate, DAMPED at gain 2/3
     * beyond it — so the catch (displayed 0.84) sits at finger p = 0.92,
     * and a full-throw finger (p = 1) displays 0.8933. Pure position
     * function: the cap's place is a function of the finger and the
     * limit, every frame; nothing is stored, nothing unwinds.
     */
    fun gear(p: Float): Float {
        val c = p.coerceIn(0f, 1f)
        return if (c <= GATE) c else GATE + (c - GATE) * (2f / 3f)
    }

    /** The travel limit a warrant state imposes on displayed travel. */
    fun travelLimit(state: WarrantState): Float = when (state) {
        WarrantState.LIVE -> 1f
        // No positive evidence of death: travel to the catch is free;
        // the catch itself cannot commit (Rosa §3.1).
        WarrantState.UNPROVEN -> CATCH
        WarrantState.OFF -> OFF_STOP
    }

    /** The commit predicate (D1 + D3's boundary): displayed at or past
     *  the catch, under contact, on a LIVE latch-frame read. */
    fun commits(displayed: Float, underContact: Boolean, state: WarrantState): Boolean =
        underContact && displayed >= CATCH && state == WarrantState.LIVE

    /** Rhea's brush floor: 90ms inclusive is an order; below is a brush. */
    fun isBrush(pressDurationMs: Long): Boolean = pressDurationMs < BRUSH_FLOOR_MS

    /**
     * Luz's Schmitt latch: returns (newArmed, fired) where fired is
     * +1 (up-cross), −1 (retreat-cross), 0 (no transition). A hover at
     * 0.679/0.681 fires exactly once — the dead-band absorbs it.
     */
    fun schmitt(armed: Boolean, displayed: Float): Pair<Boolean, Int> = when {
        !armed && displayed >= GATE -> true to 1
        armed && displayed <= GATE_RELEASE -> false to -1
        else -> armed to 0
    }

    /**
     * Cato's mesh curve: cubic-bezier (0.30, 0, 0.20, 1.00). Given the
     * time fraction u (the curve's x), returns the travel fraction
     * (the curve's y). P2.y == P3.y == 1, so the exit velocity is
     * exactly 0 — the seat spring seeds (0.92, 0) honestly.
     */
    fun meshCurve(u: Float): Float {
        val x = u.coerceIn(0f, 1f)
        if (x <= 0f) return 0f
        if (x >= 1f) return 1f
        // Solve BezX(t) = x for t (Newton with a bisection guard).
        var lo = 0f
        var hi = 1f
        var t = x
        repeat(12) {
            val xt = bez(t, 0.30f, 0.20f)
            if (abs(xt - x) < 0.0005f) return bez(t, 0f, 1f)
            if (xt < x) lo = t else hi = t
            val slope = bezSlope(t, 0.30f, 0.20f)
            val next = if (slope > 0.0001f) t - (xt - x) / slope else (lo + hi) / 2f
            t = if (next <= lo || next >= hi) (lo + hi) / 2f else next
        }
        return bez(t, 0f, 1f)
    }

    private fun bez(t: Float, p1: Float, p2: Float): Float {
        val s = 1f - t
        return 3f * s * s * t * p1 + 3f * s * t * t * p2 + t * t * t
    }

    private fun bezSlope(t: Float, p1: Float, p2: Float): Float {
        val s = 1f - t
        return 3f * s * s * p1 + 6f * s * t * (p2 - p1) + 3f * t * t * (1f - p2)
    }
}

/**
 * ============================================================================
 * 4. SLIDE TO START VIEW — Slice D rebuild (D1+D2+D3+D4+D5+D10, Live Wire form)
 * ============================================================================
 * - Commit = displayed crossing 0.84 UNDER CONTACT on a LIVE wire read
 *   (Vega). Damping is a pure position function: free 1:1 to the 0.68
 *   gate, gain 2/3 beyond (finger commit coordinate p = 0.92).
 * - The gate is a Schmitt latch (0.680 up / 0.660 retreat, Luz): pitched
 *   both directions through fireFeel(1L) — 620Hz up, 340Hz retreat —
 *   and the wire is re-read in the same frame, BEFORE the feel.
 * - The OFF-stop (Joss): under a positively dead wire the cap clamps
 *   hard at 0.65; the first frame raw reaches the stop starts a
 *   touch-independent refusal sequence (knock, 300ms hold, universal
 *   spring home). The touch is REFUSED-DEAD from that frame.
 * - Mesh, seat, and abort are three modes of ONE Choreographer
 *   integrator (Cato): spring accel = 400·(1−x) − 16·v (ω0=20,
 *   ζ=0.40); a hard-stop clamp at x ≥ 1.00 in a ≤5ms substep IS
 *   onLatch; the 14ms @ 0.85 latch knock fires by travel predicate
 *   at 0.97, in-step, before the latch.
 * - The tap is a first-class order (Rhea): hit-tested on the cap
 *   (drawn rect inflated 8dp in x, full track height in y); a press
 *   ≥90ms released on a live wire runs the driven mesh (140ms bezier
 *   to 0.92, exit velocity exactly 0) + the shared seat spring; a
 *   second touch during the mesh aborts it on the same integrator;
 *   an aborted mesh has dispatched nothing, provably (enforcement
 *   dispatches only at the latch; the verdict is a read-only value).
 *   A press <90ms is a brush: 1dp sink, one dull knock, zero travel.
 * - Print (Aldo): the badge is DEAD (the hero owns the total). The cap
 *   words print on the track face in the shipped label's home, at
 *   plate: SLIDE OR TAP TO START at rest and frozen through every
 *   drag state; TAP AGAIN TO UNDO only inside the tap-mesh window.
 *   The strings are compile-time constants — no state input at all
 *   (Teo). The 1 − 1.3·frac label fade survives for hand-drags only.
 * - The view holds NO evidence logic: the host injects a warrant
 *   provider (the repository's liveWarrant under the hood); the view
 *   reads it at DOWN, at the gate, on every MOVE frame, and at the
 *   latch frame — the verdict consulted is never older than the frame
 *   that uses it.
 */
class SlideToStartView(context: Context) : BlockitCanvasView(context) {

    /** The latch: enforcement dispatches here, and only here. */
    var onSlideCommitted: (() -> Unit)? = null

    /** DOWN that begins a gesture — the host freezes its print
     *  (Emil). Carries the attempt's DOWN-frame warrant state: the
     *  host's latch must capture from the slide's own synchronous
     *  read, never from its async flow (Emil, review E-4). */
    var onPressStart: ((WarrantState) -> Unit)? = null

    /** The gesture's machine is back at REST (or the latch has fired). */
    var onPressEnd: (() -> Unit)? = null

    /** The Live Wire verdict, read synchronously. Null = no provider
     *  wired (preview): the wire reads LIVE so the surface still works. */
    var warrantProvider: (() -> Warrant)? = null

    var isArmedState: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    /** The cascade's annulment key (Slice D step 4): the host
     *  increments this when an attempt is annulled, and the view
     *  returns to REST — without it a cap seated by a latched
     *  gesture would sit at the end of its track forever after
     *  the machine refused the seal late. Set by the host's
     *  update pass; a NEW key is the order, the value itself
     *  carries no meaning. */
    var resetKey: Int = 0

    /** Return the machine to REST, the cap home, the refusal
     *  latch clean — the detach reset's state, callable while
     *  still attached (annulment is not a detach). */
    fun resetToRest() {
        stopIntegrator()
        springBackAnimator?.cancel()
        mode = Mode.REST
        displayed = 0f
        velocity = 0f
        refusalFired = false
        gateArmed = false
        latchKnockFired = false
        retreatTouched = false
        // Slice E: an annulment hands the slide back — the seal
        // gate and the exit shading die with the attempt.
        sealedGate = false
        sealExitPhase = 0
        invalidate()
    }

    private enum class Mode {
        REST, DRAG, MESH, SEAT, ABORT_RETURN,
        REFUSAL_RETREAT, REFUSAL_HOLD, REFUSAL_RETURN, SEATED
    }

    private var mode: Mode = Mode.REST
    private var displayed: Float = 0f
    private var velocity: Float = 0f

    // Per-attempt drag state (an attempt = one DOWN → UP/CANCEL life).
    private var attemptWarrant: WarrantState = WarrantState.LIVE
    private var grabOffsetPx: Float = 0f
    private var downTimeMs: Long = 0L
    private var downOnCap: Boolean = false
    private var movedBeyondSlop: Boolean = false
    private var downXPx: Float = 0f
    private var gateArmed: Boolean = false
    private var refusalFired: Boolean = false
    private var seatFromDrag: Boolean = false
    // The revival pin (Joss, cascade review CHARGE 1-B): once a
    // refusal retreat has started in this attempt, the attempt's
    // warrant is pinned OFF for the remainder of the press. §3(d)'s
    // bright line — revival after the death frame is honoured only
    // by the NEXT press — was broken through the retake: dragMove's
    // fresh re-read replaced OFF with a revived LIVE, restoring full
    // travel to a press the machine had already refused. Every
    // re-read on the drag path consults this flag; beginAttempt and
    // resetToRest clear it.
    private var retreatTouched: Boolean = false

    private var latchKnockFired: Boolean = false

    // The commutator floor (Vega, review V-1): the displayed value
    // at which this SEAT run began — the catch crossing for a drag,
    // 0.92 for the mesh. Inside the seat, displayed may never fall
    // below it: the commit crossing ends finger ownership, and a
    // stalled frame must not walk a committed slide home (D3's
    // failure budget ends at refusal-from-the-catch, never at a
    // silent reversal under the commutator).
    private var seatFloor: Float = 0f

    // Integrator state (one Choreographer loop for MESH/SEAT/returns).
    private val choreographer: Choreographer = Choreographer.getInstance()
    private var inIntegrator: Boolean = false
    private var lastFrameNanos: Long = 0L
    private var modeStartNanos: Long = 0L
    private var modeFrom: Float = 0f
    private var holdUntilNanos: Long = 0L

    private val frameCallback = Choreographer.FrameCallback { nanos -> stepMachine(nanos) }

    // Brush pose: a 1dp sink that lives for the brush's moment only.
    private var sinkUntilMs: Long = 0L

    private var springBackAnimator: ValueAnimator? = null

    // --- Slice E: the seal ghost + the sled return (Zug-II §4.3,
    // Schlitten §§2/4/6). The ghost is the view's sealed posture:
    // mode SEATED at displayed exactly 1 (the ghost fraction is
    // 1.00 BY DEFINITION of t=0), every touch consumed and dropped
    // by the gate. The return is the one animation that runs from
    // the sealed posture: total silence by construction — no
    // fireFeel, no knock, no engine token exists on its path.
    private var sealedGate: Boolean = false

    /** E5b's exit phase (Horiz §4.2): pushed by the host from its
     *  own shot clock — 0 at rest and at t=0, then 1/2/3 at
     *  +50/+100/+150ms. The view never times this itself. */
    var sealExitPhase: Int = 0
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    /** Schlitten §2.3's factory rule, applied at attach: the
     *  host sets this from the gate before the view first draws
     *  (seated while a session runs or a return is pending, home
     *  in every other world). */
    var initialFraction: Float = 0f

    var isReturning: Boolean = false
        private set
    private var returnAnimator: ValueAnimator? = null
    private var returnActive: Boolean = false
    private var returnPlayTimeMs: Long = 0L
    private var frozenPlayTimeMs: Long = 0L
    private var resumeUsed: Boolean = false
    private var pendingResume: Boolean = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val f = initialFraction.coerceIn(0f, 1f)
        if (f > 0f && mode == Mode.REST && displayed == 0f) {
            displayed = f
            if (f >= 1f) {
                mode = Mode.SEATED
                sealedGate = true
            }
        }
    }

    /** Zug-II §4.3: the seal takes the view. Identical whether
     *  the arm underneath was a drag or a tap (the machine is
     *  already SEATED via the seat integrator; a live one is
     *  stopped so no frame after t=0 advances it). */
    fun sealGhost() {
        stopIntegrator()
        springBackAnimator?.cancel()
        mode = Mode.SEATED
        displayed = 1f
        sealedGate = true
        invalidate()
    }

    /** Schlitten §2.2: the return home. Callable only in the
     *  gate's FIRED state (the host enforces); idempotent here. */
    fun animateReturnHome() {
        if (isReturning) return
        // §6.3: the cap's tone resets to rest inside the return's
        // first frame — the pop hides inside motion onset.
        sealExitPhase = 0
        resumeUsed = false
        startReturnAnimator(fromPlayTimeMs = 0L)
    }

    private fun startReturnAnimator(fromPlayTimeMs: Long) {
        val old = returnAnimator
        returnAnimator = null
        returnActive = false
        old?.cancel()
        val anim = ValueAnimator.ofFloat(1f, 0f)
        anim.duration = 650L // literal at the animator (Spule L-4) — never tokenised
        anim.interpolator = MonolithCurves.ReturnHome
        anim.addUpdateListener {
            returnPlayTimeMs = it.currentPlayTime
            displayed = (it.animatedValue as Float).coerceIn(0f, 1f)
            invalidate()
        }
        anim.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                if (returnActive && returnAnimator === animation) {
                    returnActive = false
                    isReturning = false
                    displayed = 0f
                    mode = Mode.REST
                    sealedGate = false
                    invalidate()
                }
            }
        })
        returnAnimator = anim
        returnActive = true
        isReturning = true
        anim.start()
        if (fromPlayTimeMs > 0L) anim.currentPlayTime = fromPlayTimeMs
    }

    /** Schlitten §4.2: the resumed return is the same animator,
     *  re-entered at the frozen frame by TIME OFFSET (F-RS) —
     *  identical interpolator and duration, seeked; never a
     *  re-proportioned remainder. */
    fun resumeReturnHome() {
        if (resumeUsed || isReturning) return
        resumeUsed = true
        startReturnAnimator(fromPlayTimeMs = frozenPlayTimeMs)
    }

    private fun freezeReturn() {
        // The play time is read out of the running listener's last
        // sample — never sampled after the cancel.
        frozenPlayTimeMs = returnPlayTimeMs
        returnActive = false
        val anim = returnAnimator
        returnAnimator = null
        anim?.cancel()
        isReturning = false
    }

    private fun handleReturnTouch(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                freezeReturn()
                // The slab test reads the FROZEN fraction's drawn
                // rect, inflated 8dp in x, full height in y.
                val inset = insetPx()
                val capLeft = inset + displayed * (width - inset * 2f - capWPx()).coerceAtLeast(1f)
                val inSlab = event.x >= capLeft - dp(8f) &&
                    event.x <= capLeft + capWPx() + dp(8f)
                if (inSlab) {
                    // Adoption: the gesture owns the cap from the
                    // frozen fraction, seeded exactly as a fresh
                    // attempt minus every sound — the return's
                    // silence covers its interruption too.
                    parent?.requestDisallowInterceptTouchEvent(true)
                    attemptWarrant = readWarrant()
                    refusalFired = false
                    gateArmed = false
                    seatFromDrag = false
                    latchKnockFired = false
                    retreatTouched = false
                    downTimeMs = event.eventTime
                    downXPx = event.x
                    movedBeyondSlop = false
                    prevDragDisplayed = displayed
                    prevDragTimeMs = event.eventTime
                    downOnCap = true
                    grabOffsetPx = event.x - (capLeft + capWPx() * 0.5f)
                    mode = Mode.DRAG
                    sealedGate = false
                    onPressStart?.invoke(attemptWarrant)
                } else {
                    pendingResume = true
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (pendingResume) {
                    pendingResume = false
                    resumeReturnHome()
                }
                return true
            }
            else -> return true
        }
    }

    private fun readWarrant(): WarrantState =
        warrantProvider?.invoke()?.state ?: WarrantState.LIVE

    private fun insetPx(): Float = dp(8f)
    private fun capWPx(): Float = dp(84f)

    private fun travelPx(): Float {
        val raw = width - insetPx() * 2f - capWPx()
        return max(raw, dp(SlideGestureMath.TRAVEL_FLOOR_DP))
    }

    /** Finger fraction p from a touch x, honouring the grab offset:
     *  the cap does not jump to the finger — the offset taken at DOWN
     *  is carried for the attempt's whole life (Rhea: the teleport
     *  dies for both entries). */
    private fun fingerFraction(touchX: Float): Float {
        val center = touchX - grabOffsetPx
        return ((center - insetPx() - capWPx() * 0.5f) / travelPx()).coerceIn(0f, 1f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        // 1. Darkest block bar track (blockBarSurface, 24dp corner radius)
        trackRect.set(0f, 0f, w, h)
        paintFill.color = tokBlockBar()
        canvas.drawRoundRect(trackRect, radiusLarge, radiusLarge, paintFill)

        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(trackRect, radiusLarge, radiusLarge, paintStroke)

        val inset = insetPx()
        val capW = capWPx()
        val physicalTravel = (w - inset * 2f - capW).coerceAtLeast(1f)
        val sink = if (System.currentTimeMillis() < sinkUntilMs) dp(1f) else 0f
        val thumbLeft = inset + displayed * physicalTravel
        thumbRect.set(thumbLeft, inset + sink, thumbLeft + capW, h - inset + sink)

        // Progressive track trail behind the cap. Suppressed
        // while the sled returns (Schlitten §6.2): a trail behind
        // a cap travelling home would print travel that never
        // happened.
        if (displayed > 0.01f && !isReturning) {
            cellRect.set(inset, inset, thumbRect.right, h - inset)
            paintAccent.color = tokAccentShaded(sealExitPhase)
            paintAccent.alpha = 55
            canvas.drawRoundRect(cellRect, radiusLarge - dp(4f), radiusLarge - dp(4f), paintAccent)
            paintAccent.alpha = 255
        }

        // 2. The cap words, on the track face at plate (Aldo/F16): a
        // compile-time constant per machine state — SLIDE OR TAP TO
        // START at rest and frozen through every drag state; TAP AGAIN
        // TO UNDO only inside the tap-mesh window. The fade is
        // occlusion handling and survives for hand-drags only (R5):
        // during the mesh the words must read, unfaded.
        val words = if (mode == Mode.MESH) WORDS_UNDO else WORDS_START
        val fade = if (mode == Mode.DRAG || (mode == Mode.SEAT && seatFromDrag)) {
            (1f - displayed * 1.3f).coerceIn(0f, 1f)
        } else {
            1f
        }
        paintMono.textSize = sp(10f)
        paintMono.letterSpacing = 0.2f
        paintMono.color = LIGHT_ON_PRIMARY
        paintMono.alpha = (fade * 255).toInt()
        paintMono.textAlign = Paint.Align.CENTER
        canvas.drawText(words, w * 0.54f, h * 0.5f + dp(4.5f), paintMono)
        paintMono.alpha = 255
        paintMono.letterSpacing = 0f

        // 3. Physical actuator cap (accent = the commit fill, its one home)
        cellRect.set(thumbRect.left, thumbRect.top + dp(2.5f), thumbRect.right, thumbRect.bottom + dp(2.5f))
        paintFill.color = 0x4D000000
        canvas.drawRoundRect(cellRect, radiusLarge - dp(4f), radiusLarge - dp(4f), paintFill)

        paintFill.color = if (isArmedState) tokSecondaryContainer() else tokAccentShaded(sealExitPhase)
        canvas.drawRoundRect(thumbRect, radiusLarge - dp(4f), radiusLarge - dp(4f), paintFill)

        // Lit top edge (one obeyed light source: from above)
        paintStroke.color = 0x66FFFFFF
        paintStroke.strokeWidth = dp(1.2f)
        canvas.drawLine(thumbRect.left + dp(14f), thumbRect.top + dp(1.2f), thumbRect.right - dp(14f), thumbRect.top + dp(1.2f), paintStroke)

        // The MARKER (T3, mixer fader; named by chrome C11): the
        // cap's one high-contrast mark — a 1dp line at its leading
        // edge in the marker cream #FFE4CF at 90%, both modes. (It
        // was a 3dp grip line in LIGHT_ON_PRIMARY; the census names
        // it a marker, and a marker is 1dp.)
        paintStroke.color = 0xE6FFE4CF.toInt()
        paintStroke.strokeWidth = dp(1f)
        paintStroke.strokeCap = Paint.Cap.BUTT
        canvas.drawLine(thumbRect.right - dp(11f), thumbRect.top + dp(11f), thumbRect.right - dp(11f), thumbRect.bottom - dp(11f), paintStroke)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Slice E, first in line (Schlitten §4.3 + Zug-II §4.3):
        // a returning sled answers its interrupts before anything;
        // a pending resume fires on its UP; a sealed view consumes
        // and drops every touch whole — no branch below may see it.
        if (isReturning) return handleReturnTouch(event)
        if (pendingResume &&
            (event.actionMasked == MotionEvent.ACTION_UP ||
                event.actionMasked == MotionEvent.ACTION_CANCEL)
        ) {
            pendingResume = false
            resumeReturnHome()
            return true
        }
        if (sealedGate) return true
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // A second touch during the MESH is the buttonhole
                // abort (F4, strict): any second touch, no bounds test.
                if (mode == Mode.MESH) {
                    startAbortReturn()
                    return true
                }
                // A DOWN inside a running machine or a live drag is not
                // a new attempt (a second pointer never hijacks a drag;
                // a refused touch admits no new attempt until its UP).
                if (mode != Mode.REST) return true
                beginAttempt(event)
                return true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (mode == Mode.MESH) {
                    startAbortReturn()
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (mode == Mode.DRAG) dragMove(event)
                else if (mode == Mode.REFUSAL_RETREAT) retreatMove(event)
                return true
            }
            MotionEvent.ACTION_UP -> {
                when (mode) {
                    Mode.DRAG -> endDrag(event, cancelled = false)
                    Mode.REFUSAL_HOLD, Mode.REFUSAL_RETREAT, Mode.REFUSAL_RETURN -> {
                        // The refusal sequence is touch-independent: an
                        // UP inside it ends the touch, not the sequence.
                        parent?.requestDisallowInterceptTouchEvent(false)
                    }
                    else -> parent?.requestDisallowInterceptTouchEvent(false)
                }
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                if (mode == Mode.DRAG) endDrag(event, cancelled = true)
                parent?.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun beginAttempt(event: MotionEvent) {
        parent?.requestDisallowInterceptTouchEvent(true)
        springBackAnimator?.cancel()
        stopIntegrator()
        attemptWarrant = readWarrant()
        refusalFired = false
        gateArmed = false
        seatFromDrag = false
        latchKnockFired = false
        retreatTouched = false
        downTimeMs = event.eventTime
        downXPx = event.x
        movedBeyondSlop = false
        prevDragDisplayed = displayed
        prevDragTimeMs = event.eventTime

        // Rhea's hit-test: the drawn cap rect inflated 8dp in x, full
        // track height in y. (thumbRect holds the last drawn rect; at
        // rest the cap sits at the left inset.)
        val inset = insetPx()
        val capLeft = inset + displayed * (width - inset * 2f - capWPx()).coerceAtLeast(1f)
        val capRight = capLeft + capWPx()
        downOnCap = event.x >= capLeft - dp(8f) && event.x <= capRight + dp(8f)

        // The grab offset keeps the cap where the finger found it.
        val capCenter = capLeft + capWPx() * 0.5f
        grabOffsetPx = event.x - capCenter

        mode = Mode.DRAG
        onPressStart?.invoke(attemptWarrant)

        // No DOWN-frame F0 exists here, by arithmetic: the grab
        // offset above is computed from this same event.x, so a
        // finger fraction taken at DOWN is identically the cap's
        // standing fraction (0 at rest) for any touch x — a
        // DOWN-past-stop test can never fire from REST, the only
        // mode beginAttempt runs from. The branch that claimed it
        // was deleted (Ren's ruling, mail/marlowe/1115; Joss's
        // proof in his slide review §2); F0 is a MOVE frame on the
        // drag path and the UP frame on the tap path, and the spec
        // note in slice-de-parts/joss.md §(b) is amended to say so.
        fireFeel(4L)
        applyDragPosition(event.x, event.eventTime)
        invalidate()
    }

    private fun dragMove(event: MotionEvent) {
        // The verdict consulted is never older than the frame that
        // uses it: re-read on every MOVE (Joss (c)) — except in a
        // retreat-touched attempt, where the pin holds the verdict
        // at OFF no matter what the wire does now (CHARGE 1-B).
        val prevWarrant = attemptWarrant
        attemptWarrant = readWarrant()
        if (retreatTouched) attemptWarrant = WarrantState.OFF
        if (abs(event.x - downXPx) > dp(8f)) movedBeyondSlop = true

        // Mid-drag death above the stop: powered retreat to 0.65 in
        // 160ms (Curve A); a snap would teleport and break the lag law.
        if (attemptWarrant == WarrantState.OFF && prevWarrant != WarrantState.OFF &&
            displayed > SlideGestureMath.OFF_STOP
        ) {
            startRefusalRetreat()
            return
        }
        applyDragPosition(event.x, event.eventTime)
        invalidate()
    }

    /** The finger-retake (Joss (c), built per Ren's ruling,
     *  mail/marlowe/1115): during the powered retreat the touch is
     *  refused-dead for commitment, but the finger keeps one honest
     *  power — if its raw fraction falls below the retreating
     *  displayed position, the man is retreating faster than the
     *  machine, the retreat animator cancels, and the cap rejoins
     *  his finger in DRAG (the hand wins). Above the retreating
     *  position the MOVE is read for nothing: the sequence is the
     *  machine's, and arrival at the stop still knocks. A rejoin
     *  at or above the stop lands in applyDragPosition's F0, which
     *  is the refusal's own door — nothing here can commit, because
     *  the warrant that drove us into retreat is OFF and every
     *  commit check re-reads it. */
    private fun retreatMove(event: MotionEvent) {
        if (fingerFraction(event.x) >= displayed) return
        stopIntegrator()
        mode = Mode.DRAG
        // The seat-seed bookkeeping restarts at the rejoin point:
        // the crossing MOVE inside dragMove then measures velocity
        // from the finger's own motion, not the machine's retreat.
        prevDragDisplayed = displayed
        prevDragTimeMs = event.eventTime
        dragMove(event)
    }

    // Velocity bookkeeping for the seat seed: the previous drag
    // frame's displayed value and its event time.
    private var prevDragDisplayed: Float = 0f
    private var prevDragTimeMs: Long = 0L

    /** Position the cap from the finger under the current warrant. */
    private fun applyDragPosition(touchX: Float, eventTimeMs: Long) {
        val p = fingerFraction(touchX)
        val geared = SlideGestureMath.gear(p)
        var next = min(geared, SlideGestureMath.travelLimit(attemptWarrant))

        // The gate (Luz): a Schmitt latch on DISPLAYED progress. The
        // wire is re-read BEFORE the feel on every latch transition;
        // a dead read suppresses the feel and the OFF law takes the
        // gesture alone (retreat if above the stop, clamp below it).
        val (newArmed, fired) = SlideGestureMath.schmitt(gateArmed, next)
        if (fired != 0) {
            // The pin binds the gate's re-read too (CHARGE 1-B): a
            // retreat-touched attempt never sees a revived wire.
            val gateRead = if (retreatTouched) WarrantState.OFF else readWarrant()
            attemptWarrant = gateRead
            if (gateRead == WarrantState.OFF) {
                if (next > SlideGestureMath.OFF_STOP) {
                    startRefusalRetreat()
                    return
                }
            } else {
                fireFeel(1L, if (fired > 0) 620f else 340f)
                gateArmed = newArmed
            }
            next = min(geared, SlideGestureMath.travelLimit(attemptWarrant))
        }

        // Joss (b): F0 — the first frame the finger reaches the stop
        // on a dead wire. The sequence starts on the clamp frame.
        if (attemptWarrant == WarrantState.OFF &&
            p >= SlideGestureMath.OFF_STOP && !refusalFired
        ) {
            displayed = SlideGestureMath.OFF_STOP
            startRefusal(knockFirst = true)
            return
        }

        // The commit (Vega + D3's boundary): displayed crossing the
        // catch under contact, on this frame's LIVE read, ends finger
        // ownership — Cato's integrator drives the seat from here.
        // Seed velocity from the crossing MOVE delta (floor 0, cap
        // +6.0 frac/s): a thrown seat is faster, never slower.
        val dtSec = (eventTimeMs - prevDragTimeMs) / 1000f
        val moveVelocity = if (dtSec > 0.001f) {
            ((next - prevDragDisplayed) / dtSec).coerceIn(0f, 6f)
        } else {
            0f
        }
        prevDragDisplayed = next
        prevDragTimeMs = eventTimeMs
        if (SlideGestureMath.commits(next, underContact = true, attemptWarrant)) {
            displayed = next
            startSeat(fromDrag = true, seedVelocity = moveVelocity)
            return
        }

        displayed = next
    }

    private fun endDrag(event: MotionEvent, cancelled: Boolean) {
        parent?.requestDisallowInterceptTouchEvent(false)
        val pressDuration = event.eventTime - downTimeMs
        val isTap = !cancelled && downOnCap && !movedBeyondSlop
        if (!isTap) {
            // The drag branch judges its release on a fresh UP read,
            // like the tap branch and the latch do (Rosa, review
            // F6) — the last MOVE's verdict is a frame stale here.
            attemptWarrant = readWarrant()
        }

        if (isTap) {
            if (SlideGestureMath.isBrush(pressDuration)) {
                // A brush on a DEAD wire is not a brush (Joss, review
                // J-3): the dull tick is the vocabulary for non-order
                // contacts, and on the mornings the refusal exists
                // for, a cap press that earns only a dull tick teaches
                // that pressing does nothing. The wire classifies the
                // contact first: OFF earns the full refusal sequence.
                if (readWarrant() == WarrantState.OFF) {
                    attemptWarrant = WarrantState.OFF
                    startRefusal(knockFirst = true)
                    return
                }
                // Brush: z-only. 1dp sink, one dull knock, zero travel,
                // nothing dispatched. (Knock amplitude 0.55 as ruled —
                // Ren, mail/marlowe/1115; the built 0.40 sat under the
                // spec value.)
                sinkUntilMs = System.currentTimeMillis() + 140L
                fireWeightedKnock(10, 0.55f, 220f)
                mode = Mode.REST
                onPressEnd?.invoke()
                invalidate()
                postDelayed({ invalidate() }, 150L)
                return
            }
            // A classified tap: the wire at UP owns the outcome (Joss:
            // the tap path's F0 is the UP frame).
            val upRead = readWarrant()
            attemptWarrant = upRead
            if (upRead == WarrantState.OFF) {
                // Knock + sentence (already standing) + 300ms hold of
                // the dead state; the cap never travelled, no spring.
                startRefusal(knockFirst = true)
                return
            }
            // Rosa's catch-pin governs taps too (review F3): on an
            // UNPROVEN wire the mesh drives only to the catch, and
            // the refusal runs FROM the catch — travel past 0.84 on
            // an unproven wire is the breach, whatever the entry.
            startMesh(
                target = if (upRead == WarrantState.UNPROVEN) {
                    SlideGestureMath.CATCH
                } else {
                    0.92f
                }
            )
            return
        }

        // A plain drag release. At the UNPROVEN catch-stop, release
        // refuses FROM the catch (Rosa): knock, 300ms hold at the
        // catch, universal spring home.
        if (attemptWarrant == WarrantState.UNPROVEN &&
            displayed >= SlideGestureMath.CATCH - 0.001f
        ) {
            startRefusal(knockFirst = true)
            return
        }

        // Sub-catch release: the shipped spring-back survives for this
        // path alone (Cato's site law) — it is not the seat, not the
        // refusal spring, and is not unified into the integrator.
        mode = Mode.REST
        animateSpringBack(0f) { onPressEnd?.invoke() }
    }

    // -----------------------------------------------------------------
    // The machine: mesh, seat, abort, refusal — one integrator (Cato).
    // -----------------------------------------------------------------

    // The mesh's destination: 0.92 (the seat's doorstep) on a LIVE
    // wire; the catch itself on an UNPROVEN one (Rosa's pin).
    private var meshTarget: Float = 0.92f

    private fun startMesh(target: Float) {
        mode = Mode.MESH
        modeFrom = displayed
        meshTarget = target
        modeStartNanos = System.nanoTime()
        velocity = 0f
        startIntegrator()
        invalidate()
    }

    private fun startSeat(fromDrag: Boolean, seedVelocity: Float) {
        mode = Mode.SEAT
        seatFromDrag = fromDrag
        velocity = seedVelocity
        seatFloor = displayed
        latchKnockFired = false
        startIntegrator()
        invalidate()
    }

    private fun startAbortReturn() {
        // Buttonhole abort: spring-back from the abort frame's sampled
        // fraction, on the same integrator (250ms class). Nothing was
        // dispatched — enforcement dispatches only at the latch.
        mode = Mode.ABORT_RETURN
        velocity = 0f
        startIntegrator()
        invalidate()
    }

    private fun startRefusal(knockFirst: Boolean) {
        if (refusalFired) return
        refusalFired = true
        // The refusal knock ships AT Zug's perceptibility floor —
        // 12ms @ 0.70 (PLAN-AUDIT PA-S1-17: the filed 10ms @ 0.40
        // sat below the floor, and Joss's filing was a build gate,
        // not a deferral). The device session may only raise it.
        if (knockFirst) fireWeightedKnock(12, 0.70f, 220f)
        mode = Mode.REFUSAL_HOLD
        holdUntilNanos = System.nanoTime() + 300_000_000L
        velocity = 0f
        startIntegrator()
        invalidate()
    }

    private fun startRefusalRetreat() {
        // Powered retreat to the stop over 160ms, Curve A; the knock
        // waits for the stop (Joss (c)). The retreat's start is the
        // attempt's death frame: the revival pin is set here, so
        // every path that enters a retreat pins the attempt (the
        // mid-drag death and the gate's dead read alike).
        retreatTouched = true
        mode = Mode.REFUSAL_RETREAT
        modeFrom = displayed
        modeStartNanos = System.nanoTime()
        velocity = 0f
        startIntegrator()
        invalidate()
    }

    private fun startIntegrator() {
        if (!inIntegrator) {
            inIntegrator = true
            lastFrameNanos = System.nanoTime()
            choreographer.postFrameCallback(frameCallback)
        }
    }

    private fun stopIntegrator() {
        if (inIntegrator) {
            inIntegrator = false
            choreographer.removeFrameCallback(frameCallback)
        }
    }

    private fun stepMachine(frameNanos: Long) {
        if (!inIntegrator) return
        val dt = ((frameNanos - lastFrameNanos) / 1e9f).coerceIn(0.001f, 0.05f)
        lastFrameNanos = frameNanos

        when (mode) {
            Mode.MESH -> {
                val elapsedMs = (frameNanos - modeStartNanos) / 1_000_000f
                val u = (elapsedMs / MonolithCurves.DURATION_140).coerceIn(0f, 1f)
                displayed = modeFrom + (meshTarget - modeFrom) * SlideGestureMath.meshCurve(u)
                if (u >= 1f) {
                    displayed = meshTarget
                    if (meshTarget >= 0.92f) {
                        // Hand-off: the spring seeds (0.92, 0) — the
                        // bezier's exit velocity is exactly 0 by its
                        // second control point (Cato).
                        startSeat(fromDrag = false, seedVelocity = 0f)
                    } else {
                        // Arrival at the catch on an unproven wire:
                        // one fresh read (the latch-time-verdict
                        // law). LIVE — the wire healed during the
                        // mesh — seats from the catch; anything else
                        // refuses FROM the catch: knock, 300ms hold
                        // at 0.84, universal spring home (Rosa F3).
                        val verdict = readWarrant()
                        attemptWarrant = verdict
                        if (verdict == WarrantState.LIVE) {
                            startSeat(fromDrag = false, seedVelocity = 0f)
                        } else {
                            startRefusal(knockFirst = true)
                        }
                    }
                }
            }
            Mode.SEAT -> stepSeat(dt)
            Mode.ABORT_RETURN -> {
                // 250ms-class spring-back: ω0=22, ζ=0.90 — substepped
                // like the seat (Cato, review F-C2): raw frame steps
                // cross this spring's stability bound at the loop's
                // own 50ms clamp and walk displayed out of [0, 1].
                var remaining = dt
                while (remaining > 0f && mode == Mode.ABORT_RETURN) {
                    val h = min(remaining, 0.005f)
                    val accel = 484f * (0f - displayed) - 39.6f * velocity
                    velocity += accel * h
                    displayed += velocity * h
                    if (displayed <= 0.001f && abs(velocity) < 0.05f) {
                        displayed = 0f
                        velocity = 0f
                        mode = Mode.REST
                        stopIntegrator()
                        onPressEnd?.invoke()
                    }
                    remaining -= h
                }
            }
            Mode.REFUSAL_RETREAT -> {
                val elapsedMs = (frameNanos - modeStartNanos) / 1_000_000f
                val u = (elapsedMs / 160f).coerceIn(0f, 1f)
                val eased = MonolithCurves.CurveA.getInterpolation(u)
                displayed = modeFrom + (SlideGestureMath.OFF_STOP - modeFrom) * eased
                if (u >= 1f) {
                    displayed = SlideGestureMath.OFF_STOP
                    startRefusal(knockFirst = true)
                }
            }
            Mode.REFUSAL_HOLD -> {
                if (frameNanos >= holdUntilNanos) {
                    if (displayed <= 0.001f) {
                        // The tap-path refusal: the cap never
                        // travelled — no spring, straight to rest.
                        mode = Mode.REST
                        stopIntegrator()
                        onPressEnd?.invoke()
                    } else {
                        mode = Mode.REFUSAL_RETURN
                        velocity = 0f
                    }
                }
            }
            Mode.REFUSAL_RETURN -> {
                // Fen's universal return spring (dampingRatio 0.78,
                // stiffness 260, ~320ms) — the app's one
                // "not committed" voice (Joss A6).
                val accel = 260f * (0f - displayed) - 25.2f * velocity
                velocity += accel * dt
                displayed += velocity * dt
                if (displayed <= 0.001f && abs(velocity) < 0.05f) {
                    displayed = 0f
                    velocity = 0f
                    mode = Mode.REST
                    stopIntegrator()
                    onPressEnd?.invoke()
                }
            }
            else -> stopIntegrator()
        }
        invalidate()
        if (inIntegrator) choreographer.postFrameCallback(frameCallback)
    }

    /** Cato's seat spring in ≤5ms substeps, so the knock predicate and
     *  the hard-stop clamp land in-step even on a dropped frame. */
    private fun stepSeat(dt: Float) {
        var remaining = dt
        while (remaining > 0f) {
            val h = min(remaining, 0.005f)
            val accel = 400f * (1f - displayed) - 16f * velocity
            velocity += accel * h
            displayed += velocity * h
            // The commutator clamp (Vega V-1): the seat is one-way.
            if (displayed < seatFloor) displayed = seatFloor
            // Travel predicates, in order: knock at 0.97, then latch.
            if (!latchKnockFired && displayed >= 0.97f) {
                latchKnockFired = true
                fireWeightedKnock(14, 0.85f, LATCH_KNOCK_PITCH_HZ)
            }
            if (displayed >= 1f) {
                displayed = 1f
                velocity = 0f
                latchFrame()
                return
            }
            remaining -= h
        }
    }

    /** The latch frame (D3's boundary): a fresh read dispatches.
     *  LIVE → the commit fires. Anything else → the refusal runs
     *  from the seat's end (displayed == 1.00): the seat proved
     *  nothing, so nothing dispatches. */
    private fun latchFrame() {
        val verdict = readWarrant()
        attemptWarrant = verdict
        if (verdict == WarrantState.LIVE) {
            stopIntegrator()
            mode = Mode.SEATED
            // The commit dispatches BEFORE the press latch releases
            // (Emil, review E-5): §3.1d holds the latch until the
            // seat lands, so no frame can render the un-latched idle
            // tenant inside the seal transition.
            onSlideCommitted?.invoke()
            onPressEnd?.invoke()
        } else {
            // The integrator keeps running into the refusal (Cato's
            // referred seam): stopping and restarting it here, inside
            // the frame callback, would double-post the callback and
            // double-step every refusal mode that follows.
            startRefusal(knockFirst = true)
        }
    }

    private fun animateSpringBack(target: Float, onEnd: (() -> Unit)? = null) {
        springBackAnimator?.cancel()
        springBackAnimator = ValueAnimator.ofFloat(displayed, target).apply {
            duration = MonolithCurves.DURATION_280.toLong()
            interpolator = MonolithCurves.MechanicalOvershoot
            addUpdateListener {
                displayed = (it.animatedValue as Float).coerceIn(0f, 1f)
                invalidate()
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onEnd?.invoke()
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {
                    onEnd?.invoke()
                }
            })
            start()
        }
    }

    override fun onDetachedFromWindow() {
        stopIntegrator()
        springBackAnimator?.cancel()
        // Schlitten §4.5: detach cancels the return where it
        // stands — no completion fires, no state pretends the
        // travel happened; the gate's factory rule rebuilds the
        // posture on the next attach.
        returnActive = false
        returnAnimator?.cancel()
        returnAnimator = null
        isReturning = false
        // Detach reset (Joss, ruled in — mail/marlowe/1115): the
        // machine returns to its rest state, so a view detached
        // mid-gesture or mid-refusal re-attaches at REST with a
        // home cap and a clean refusal latch — without this, a
        // detach inside a refusal mode left the view answering to
        // a dead sequence until process death.
        mode = Mode.REST
        displayed = 0f
        refusalFired = false
        super.onDetachedFromWindow()
    }

    companion object {
        // Aldo: the cap words are compile-time constants (Teo: no
        // state input at all). The badge that once printed the block
        // length here is dead — the hero owns the total.
        private const val WORDS_START = "SLIDE OR TAP TO START"
        private const val WORDS_UNDO = "TAP AGAIN TO UNDO"

        // The latch knock's pitch (Cato §1.3, via Vega review V-2):
        // 320Hz ≈ 0.35 × the 920Hz calibrated resonance the knock
        // family is tuned against — the knock names its target
        // inside the engine's pitch space, not as a bare constant.
        private const val LATCH_KNOCK_PITCH_HZ = 320f
    }
}
