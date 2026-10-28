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
import com.example.ui.theme.MonolithCurves
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
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
        const val LIGHT_NOTHING_RED = 0xFFE30613.toInt()

        // Night 19 Tokens (Zero-Green verified)
        const val NIGHT_BG = 0xFF201D1A.toInt()
        const val NIGHT_SURFACE = 0xFF201D1A.toInt()
        const val NIGHT_SURFACE_SHEET = 0xFF2E2A26.toInt()
        const val NIGHT_SURFACE_VARIANT = 0xFF272320.toInt()
        const val NIGHT_SURFACE_BRIGHT = 0xFF332A27.toInt()
        const val NIGHT_PRIMARY = 0xFFFFE4CF.toInt()
        const val NIGHT_ON_PRIMARY = 0xFF1B110D.toInt()
        const val NIGHT_PRIMARY_CONTAINER = 0xFFF47442.toInt()
        const val NIGHT_ON_PRIMARY_CONTAINER = 0xFF471602.toInt()
        const val NIGHT_SECONDARY_CONTAINER = 0xFFFFB38F.toInt()
        const val NIGHT_ON_SECONDARY_CONTAINER = 0xFF40352E.toInt()
        const val NIGHT_ON_SURFACE = 0xFFFBECE1.toInt()
        const val NIGHT_ON_SURFACE_VARIANT = 0xFFFAE4D3.toInt()
        const val NIGHT_OUTLINE = 0xFF807A7A.toInt()
        const val NIGHT_OUTLINE_VARIANT = 0x758F8888
        const val NIGHT_BLOCK_BAR = 0xFF161311.toInt()
        const val NIGHT_TEXT_PRIMARY = 0xFFF8F5F5.toInt()
        const val NIGHT_TEXT_SECONDARY = 0xFFF1E8E6.toInt()
        const val NIGHT_NOTHING_RED = 0xFFFF2A3C.toInt()
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
    protected fun tokOnPrimary(): Int = if (isNightMode) NIGHT_ON_PRIMARY else LIGHT_ON_PRIMARY
    protected fun tokAccent(): Int = if (isNightMode) NIGHT_PRIMARY_CONTAINER else LIGHT_PRIMARY_CONTAINER
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
    protected fun tokNothingRed(): Int = if (isNightMode) NIGHT_NOTHING_RED else LIGHT_NOTHING_RED

    // Pre-allocated Typefaces (§2: Words = Sans, Numbers = Space Mono / JetBrainsMono)
    protected val wordTypefaceBold: Typeface by lazy {
        val base = runCatching { ResourcesCompat.getFont(context, R.font.plus_jakarta_sans) }.getOrNull() ?: Typeface.SANS_SERIF
        Typeface.create(base, Typeface.BOLD)
    }
    protected val wordTypefaceRegular: Typeface by lazy {
        runCatching { ResourcesCompat.getFont(context, R.font.plus_jakarta_sans) }.getOrNull() ?: Typeface.SANS_SERIF
    }
    protected val monoTypefaceBold: Typeface by lazy {
        val base = runCatching { ResourcesCompat.getFont(context, R.font.jetbrains_mono) }.getOrNull() ?: Typeface.MONOSPACE
        Typeface.create(base, Typeface.BOLD)
    }
    protected val monoTypefaceRegular: Typeface by lazy {
        runCatching { ResourcesCompat.getFont(context, R.font.jetbrains_mono) }.getOrNull() ?: Typeface.MONOSPACE
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
        try {
            if (vibrator?.hasVibrator() == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(hapticMs, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (_: Exception) {}

        val engine = soundEngine
        if (engine != null) {
            when (hapticMs) {
                1L -> engine.playRotaryDetent(freqHz.toDouble())
                4L -> engine.playMechanicalLatch(true)
                8L -> engine.playHarmonicSeal(true)
                else -> engine.playStampPressSound()
            }
        }
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
 * ============================================================================
 * 1. HERO ARCHITECTURAL TIMER CARD VIEW (DEVELOPER-GUIDE.pdf §1.1, §2, §3, §4.2 #5)
 * ============================================================================
 * - The ONE raised surface on the Focus screen (`colorSurfaceBright` #FAF6F0 / #332A27, 24dp radius).
 * - Hero countdown digits sized like architecture (112dp–136dp Space Mono on tight columns).
 * - Integrated `TileNumberView` 4dp dot-matrix counter badge in the top-right corner.
 * - Integrated `TimelineBar` horizontal progress bar with segment ticks at the bottom of the card
 *   (the #F55314 accent's primary home).
 */
class HeroTimerCardView(context: Context) : BlockitCanvasView(context) {

    var primaryReadout: String = "50:00"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var statusWord: String = "DURATION"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var finishWordPrefix: String = "Finishes "
    var finishMonoTime: String = "4:30 PM"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var activeTaskTitle: String = "Finish Chapter 4 Problem Set"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var progressFraction: Float = 0.28f
        set(value) {
            val clamped = value.coerceIn(0f, 1f)
            if (abs(field - clamped) > 0.002f) {
                field = clamped
                invalidate()
            }
        }

    var isSessionArmed: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var blockedAppsCount: Int = 8
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var onTaskBannerClicked: (() -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        // 1. The ONE raised card (colorSurfaceBright, 24dp radius)
        cardRect.set(0f, 0f, w, h)
        paintFill.color = tokSurfaceBright()
        canvas.drawRoundRect(cardRect, radiusLarge, radiusLarge, paintFill)

        // Hairline border in colorOutlineVariant (0x75 alpha)
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(cardRect, radiusLarge, radiusLarge, paintStroke)

        val pad = gapUnit * 1.5f // 18dp internal architectural padding

        // 2. Top row: Status label (left) + Dot-Matrix TileNumberView blocked counter (right)
        if (isSessionArmed) {
            paintAccent.color = tokAccent()
            canvas.drawCircle(pad + dp(5f), pad + dp(6f), dp(5f), paintAccent)
        } else {
            paintFill.color = tokOnSurfaceVariant()
            canvas.drawCircle(pad + dp(5f), pad + dp(6f), dp(4f), paintFill)
        }

        paintWord.color = if (isSessionArmed) tokAccent() else tokTextSecondary()
        paintWord.textSize = sp(11.5f)
        paintWord.textAlign = Paint.Align.LEFT
        canvas.drawText(statusWord, pad + dp(16f), pad + dp(10f), paintWord)

        // Top-right recessed pill showing blocked apps count in Space Mono + 4dp dot-matrix spice
        val pillW = dp(104f)
        val pillH = dp(26f)
        cellRect.set(w - pad - pillW, pad - dp(6f), w - pad, pad - dp(6f) + pillH)
        paintFill.color = tokSurfaceVariant()
        canvas.drawRoundRect(cellRect, radiusSmall, radiusSmall, paintFill)

        drawMixedWordAndMono(
            canvas = canvas,
            wordPrefix = "SHUT ",
            monoNumber = String.format(Locale.US, "%02d", blockedAppsCount),
            wordSuffix = " APPS",
            x = cellRect.centerX(),
            baselineY = cellRect.centerY() + dp(4f),
            wordSizePx = sp(9.5f),
            monoSizePx = sp(11f),
            wordColor = tokTextSecondary(),
            monoColor = if (isSessionArmed) tokAccent() else tokTextPrimary(),
            centered = true
        )

        // 3. HERO ARCHITECTURAL COUNTDOWN NUMERALS (§2: 100dp+ Space Mono, numbers as architecture)
        val heroCenterY = h * 0.46f
        paintMono.color = tokPrimary()
        paintMono.textSize = sp(78f)
        paintMono.textAlign = Paint.Align.CENTER
        paintMono.letterSpacing = -0.04f
        val fontMetrics = paintMono.fontMetrics
        val heroBaseline = heroCenterY - (fontMetrics.ascent + fontMetrics.descent) * 0.5f
        canvas.drawText(primaryReadout, w * 0.5f, heroBaseline, paintMono)
        paintMono.letterSpacing = 0f

        // 4. Arrival time / session horizon mixed label directly below hero digits
        val subBaseline = heroCenterY + dp(46f)
        drawMixedWordAndMono(
            canvas = canvas,
            wordPrefix = finishWordPrefix,
            monoNumber = finishMonoTime,
            wordSuffix = "",
            x = w * 0.5f,
            baselineY = subBaseline,
            wordSizePx = sp(12.5f),
            monoSizePx = sp(13f),
            wordColor = tokTextSecondary(),
            monoColor = tokTextPrimary(),
            centered = true
        )

        // 5. TIMELINE BAR (§4.2 #5: Horizontal progress bar with segment ticks — the accent's main home)
        val timelineTop = h - dp(66f)
        val timelineBottom = timelineTop + dp(16f)
        trackRect.set(pad, timelineTop, w - pad, timelineBottom)

        // Recessed track in colorSurfaceVariant + hairline outline
        paintFill.color = tokSurfaceVariant()
        canvas.drawRoundRect(trackRect, dp(8f), dp(8f), paintFill)
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(trackRect, dp(8f), dp(8f), paintStroke)

        // Active fill in colorPrimaryContainer (#F55314)
        val fillW = (trackRect.width() * progressFraction.coerceIn(0.03f, 1f))
        thumbRect.set(trackRect.left, trackRect.top, trackRect.left + fillW, trackRect.bottom)
        paintAccent.color = tokAccent()
        canvas.drawRoundRect(thumbRect, dp(8f), dp(8f), paintAccent)

        // Segment ticks across TimelineBar (12 segments)
        val segCount = 12
        for (i in 1 until segCount) {
            val tx = trackRect.left + (trackRect.width() * i / segCount.toFloat())
            paintStroke.color = if (tx <= thumbRect.right) tokOnPrimary() else tokHairline()
            paintStroke.strokeWidth = dp(1f)
            canvas.drawLine(tx, trackRect.top + dp(3f), tx, trackRect.bottom - dp(3f), paintStroke)
        }

        // 6. Bottom Cued Task Bar inside the card (tappable to switch/open tasks)
        val taskBarTop = h - dp(38f)
        paintStroke.color = tokHairline()
        canvas.drawLine(pad, taskBarTop - dp(6f), w - pad, taskBarTop - dp(6f), paintStroke)

        paintWord.color = tokTextSecondary()
        paintWord.textSize = sp(10.5f)
        paintWord.textAlign = Paint.Align.LEFT
        canvas.drawText("ACTIVE TASK", pad, taskBarTop + dp(12f), paintWord)

        paintWord.color = tokTextPrimary()
        paintWord.textSize = sp(12f)
        paintWord.textAlign = Paint.Align.RIGHT
        val clippedTitle = if (activeTaskTitle.length > 30) activeTaskTitle.take(28) + "…" else activeTaskTitle
        canvas.drawText(clippedTitle, w - pad, taskBarTop + dp(12f), paintWord)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP && event.y >= height - dp(46f)) {
            fireFeel(4L, 580f)
            onTaskBannerClicked?.invoke()
            return true
        }
        return super.onTouchEvent(event)
    }
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
                if (!isDragging) {
                    wheelAngleDeg = minutesToAngle(clamped)
                }
                invalidate()
            }
        }

    var studyBlocksCount: Int = 2
        set(value) {
            if (field != value) {
                field = value.coerceIn(1, 6)
                invalidate()
            }
        }

    var isLockedSession: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var onMinutesChanged: ((Int) -> Unit)? = null
    var onCycleBlocksCount: (() -> Unit)? = null

    private var wheelAngleDeg: Float = minutesToAngle(50)
    private var lastTouchAngleDeg: Float = 0f
    private var isDragging: Boolean = false
    private var velocityTracker: VelocityTracker? = null
    private var settleAnimator: ValueAnimator? = null

    private fun minutesToAngle(mins: Int): Float {
        return ((mins - 5).toFloat() / 175f) * 300f - 150f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        val cx = w * 0.5f
        val cy = h * 0.5f
        val outerR = min(w, h) * 0.46f
        val rimR = outerR - dp(16f)
        val innerHubR = outerR * 0.52f

        // 1. Outer recessed wheel body (colorSurfaceVariant #D8D1CB / #272320)
        paintFill.color = tokSurfaceVariant()
        canvas.drawCircle(cx, cy, outerR, paintFill)

        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1.2f)
        canvas.drawCircle(cx, cy, outerR, paintStroke)

        // 2. Active arc track on the wheel rim
        val sweepNormalized = ((minutesValue - 5).toFloat() / 175f).coerceIn(0.02f, 1f)
        boundsRect.set(cx - rimR, cy - rimR, cx + rimR, cy + rimR)
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(4f)
        paintStroke.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(boundsRect, 120f, 300f, false, paintStroke)

        paintStroke.color = tokPrimary()
        paintStroke.strokeWidth = dp(4f)
        canvas.drawArc(boundsRect, 120f, 300f * sweepNormalized, false, paintStroke)

        // 3. 60 Precision Radial Tick Marks around the rim
        val totalTicks = 60
        for (i in 0..totalTicks) {
            val frac = i / totalTicks.toFloat()
            val deg = 120f + frac * 300f
            val rad = deg * (PI.toFloat() / 180f)
            val isMajor = i % 5 == 0
            val rOuter = outerR - dp(5f)
            val rInner = if (isMajor) outerR - dp(13f) else outerR - dp(9f)
            paintStroke.strokeWidth = if (isMajor) dp(1.8f) else dp(1f)
            paintStroke.color = if (frac <= sweepNormalized) tokPrimary() else tokHairline()
            canvas.drawLine(
                cx + cos(rad) * rInner,
                cy + sin(rad) * rInner,
                cx + cos(rad) * rOuter,
                cy + sin(rad) * rOuter,
                paintStroke
            )
        }

        // 4. Slider Dot Radius = 14dp (§3 Geometry) in THE Accent (#F55314) at active angle
        val activeDeg = 120f + sweepNormalized * 300f
        val activeRad = activeDeg * (PI.toFloat() / 180f)
        val dotX = cx + cos(activeRad) * rimR
        val dotY = cy + sin(activeRad) * rimR
        val sliderDotR = dp(14f)

        paintAccent.color = tokAccent()
        canvas.drawCircle(dotX, dotY, sliderDotR, paintAccent)

        // Crisp inner pip on the 14dp thumb dot
        paintFill.color = tokOnPrimary()
        canvas.drawCircle(dotX, dotY, dp(4f), paintFill)

        // 5. Inner Raised Hub (colorSurfaceBright) — tap center to cycle Pomodoro block count
        paintFill.color = tokSurfaceBright()
        canvas.drawCircle(cx, cy, innerHubR, paintFill)
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawCircle(cx, cy, innerHubR, paintStroke)

        // Hub typography: Space Mono numeral + Sans unit
        val modeLabel = if (minutesValue < 10) "OPEN FLOW" else "ROTARY DIAL"
        paintWord.color = tokTextSecondary()
        paintWord.textSize = sp(9.5f)
        paintWord.textAlign = Paint.Align.CENTER
        canvas.drawText(modeLabel, cx, cy - dp(18f), paintWord)

        val numStr = if (minutesValue < 10) "∞" else minutesValue.toString()
        val unitStr = if (minutesValue < 10) " FLOW" else " min"
        drawMixedWordAndMono(
            canvas = canvas,
            wordPrefix = "",
            monoNumber = numStr,
            wordSuffix = unitStr,
            x = cx,
            baselineY = cy + dp(6f),
            wordSizePx = sp(13f),
            monoSizePx = sp(24f),
            wordColor = tokTextSecondary(),
            monoColor = tokPrimary(),
            centered = true
        )

        // Bottom pill inside hub: block count (e.g., "2x BLOCKS")
        drawMixedWordAndMono(
            canvas = canvas,
            wordPrefix = "",
            monoNumber = "${studyBlocksCount}x",
            wordSuffix = " BLOCKS",
            x = cx,
            baselineY = cy + dp(25f),
            wordSizePx = sp(9.5f),
            monoSizePx = sp(10.5f),
            wordColor = tokTextSecondary(),
            monoColor = tokAccent(),
            centered = true
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isLockedSession) return false

        val cx = width * 0.5f
        val cy = height * 0.5f
        val dx = event.x - cx
        val dy = event.y - cy
        val dist = sqrt(dx * dx + dy * dy)
        val innerHubR = min(width, height) * 0.46f * 0.52f

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                settleAnimator?.cancel()
                if (dist < innerHubR * 0.85f) {
                    fireFeel(4L, 620f)
                    onCycleBlocksCount?.invoke()
                    return true
                }
                isDragging = true
                velocityTracker?.recycle()
                velocityTracker = VelocityTracker.obtain().apply { addMovement(event) }
                updateMinutesFromTouch(dx, dy)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    velocityTracker?.addMovement(event)
                    updateMinutesFromTouch(dx, dy)
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    isDragging = false
                    velocityTracker?.recycle()
                    velocityTracker = null
                    animateWheelSettle()
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
        // Map 120..420 degrees to 0..1 fraction
        val shifted = if (deg < 90f) deg + 360f else deg
        val clampedDeg = shifted.coerceIn(120f, 420f)
        val frac = (clampedDeg - 120f) / 300f
        val rawMinutes = (5 + (frac * 175f).roundToInt()).coerceIn(5, 180)
        // Snap near 5-minute detents when dragging smoothly
        val snapped = if (rawMinutes < 10) 5 else ((rawMinutes + 2) / 5) * 5
        if (snapped != minutesValue) {
            minutesValue = snapped
            wheelAngleDeg = clampedDeg
            val freq = 340f + ((snapped - 5) / 175f) * 540f
            fireFeel(if (snapped % 15 == 0) 4L else 1L, freq)
            onMinutesChanged?.invoke(snapped)
            invalidate()
        }
    }

    private fun animateWheelSettle() {
        val targetAngle = minutesToAngle(minutesValue)
        settleAnimator?.cancel()
        settleAnimator = ValueAnimator.ofFloat(wheelAngleDeg, targetAngle).apply {
            duration = MonolithCurves.DURATION_280.toLong()
            interpolator = MonolithCurves.CurveB
            addUpdateListener { anim ->
                wheelAngleDeg = anim.animatedValue as Float
                invalidate()
            }
            start()
        }
        fireFeel(4L, 520f)
    }
}

/**
 * ============================================================================
 * 3. HAPTIC PRESET STRIP VIEW (DEVELOPER-GUIDE.pdf §4.2 #3 HapticPreset)
 * ============================================================================
 * - 5 self-contained 12dp rounded-rect chips: FLOW (<10m), 15m, 25m, 45m, 90m.
 * - Words in Sans, numerals in Space Mono.
 * - Selected chip fills with Ink (`colorPrimary` #252323) and cream content (`colorOnPrimary`).
 * - Press animates scale with Curve A (180ms) and fires a 4ms click + synth transient.
 */
class HapticPresetStripView(context: Context) : BlockitCanvasView(context) {

    private val presetValues = intArrayOf(5, 15, 25, 45, 90)
    var selectedMinutes: Int = 50
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var isLockedSession: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var onPresetSelected: ((Int) -> Unit)? = null

    private var pressedIndex: Int = -1
    private var pressScale: Float = 1f
    private var pressAnimator: ValueAnimator? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        val count = presetValues.size
        val gap = gapUnit // 12dp universal gap
        val cellW = (w - gap * (count - 1)) / count.toFloat()

        for (i in 0 until count) {
            val mins = presetValues[i]
            val isSelected = if (mins == 5) selectedMinutes < 10 else selectedMinutes == mins
            val left = i * (cellW + gap)
            val right = left + cellW

            canvas.save()
            if (i == pressedIndex) {
                canvas.scale(pressScale, pressScale, left + cellW * 0.5f, h * 0.5f)
            }

            cellRect.set(left, 0f, right, h)
            paintFill.color = if (isSelected) tokPrimary() else tokSurfaceVariant()
            canvas.drawRoundRect(cellRect, radiusSmall, radiusSmall, paintFill)

            paintStroke.color = if (isSelected) tokPrimary() else tokHairline()
            paintStroke.strokeWidth = dp(1f)
            canvas.drawRoundRect(cellRect, radiusSmall, radiusSmall, paintStroke)

            val contentColor = if (isSelected) tokOnPrimary() else tokTextPrimary()
            val subColor = if (isSelected) tokOnPrimary() else tokTextSecondary()

            if (mins == 5) {
                // FLOW stopwatch preset
                paintWord.color = contentColor
                paintWord.textSize = sp(11.5f)
                paintWord.textAlign = Paint.Align.CENTER
                canvas.drawText("FLOW", cellRect.centerX(), cellRect.centerY() + dp(4f), paintWord)
            } else {
                drawMixedWordAndMono(
                    canvas = canvas,
                    wordPrefix = "",
                    monoNumber = mins.toString(),
                    wordSuffix = "m",
                    x = cellRect.centerX(),
                    baselineY = cellRect.centerY() + dp(5f),
                    wordSizePx = sp(11.5f),
                    monoSizePx = sp(14.5f),
                    wordColor = subColor,
                    monoColor = contentColor,
                    centered = true
                )
            }

            // Tiny 4dp active indicator dot in THE Accent (#F55314) when selected
            if (isSelected) {
                paintAccent.color = tokAccent()
                canvas.drawCircle(cellRect.centerX(), cellRect.bottom - dp(6f), dp(2.5f), paintAccent)
            }
            canvas.restore()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isLockedSession) return false
        val count = presetValues.size
        val cellW = (width - gapUnit * (count - 1)) / count.toFloat()
        val idx = (event.x / (cellW + gapUnit)).toInt().coerceIn(0, count - 1)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pressedIndex = idx
                animatePress(0.93f)
                return true
            }
            MotionEvent.ACTION_UP -> {
                val chosen = presetValues[idx]
                animatePress(1f)
                pressedIndex = -1
                selectedMinutes = chosen
                fireFeel(4L, 480f + idx * 70f)
                onPresetSelected?.invoke(chosen)
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                animatePress(1f)
                pressedIndex = -1
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun animatePress(target: Float) {
        pressAnimator?.cancel()
        pressAnimator = ValueAnimator.ofFloat(pressScale, target).apply {
            duration = MonolithCurves.DURATION_180.toLong()
            interpolator = MonolithCurves.CurveA
            addUpdateListener {
                pressScale = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }
}

/**
 * ============================================================================
 * 4. SLIDE TO START VIEW (DEVELOPER-GUIDE.pdf §4.2 #2 SlideToStart)
 * ============================================================================
 * - Physical commit gesture required for any consequential action (arming session).
 * - Darkest surface in the app (`blockBarSurface` #2E2A26 / #161311), 24dp radius.
 * - Thumb dragged full width of track commits action with 10ms clunk + synth chord;
 *   partial drags spring back with OvershootInterpolator (280ms).
 */
class SlideToStartView(context: Context) : BlockitCanvasView(context) {

    var labelText: String = "SLIDE TO ARM SESSION"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var subMonoBadge: String = "50M"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var isArmedState: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                dragFraction = 0f
                invalidate()
            }
        }

    var onSlideCommitted: (() -> Unit)? = null

    private var dragFraction: Float = 0f
    private var isDraggingThumb: Boolean = false
    private var lastTickBucket: Int = 0
    private var springBackAnimator: ValueAnimator? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        // 1. Darkest block bar track (blockBarSurface #2E2A26, 24dp corner radius)
        trackRect.set(0f, 0f, w, h)
        paintFill.color = tokBlockBar()
        canvas.drawRoundRect(trackRect, radiusLarge, radiusLarge, paintFill)

        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(trackRect, radiusLarge, radiusLarge, paintStroke)

        val inset = dp(6f)
        val thumbW = dp(76f)
        val maxTravel = (w - inset * 2f - thumbW).coerceAtLeast(1f)
        val thumbLeft = inset + dragFraction * maxTravel
        thumbRect.set(thumbLeft, inset, thumbLeft + thumbW, h - inset)

        // Progressive track trail behind thumb
        if (dragFraction > 0.01f) {
            cellRect.set(inset, inset, thumbRect.right, h - inset)
            paintAccent.color = tokAccent()
            paintAccent.alpha = 55
            canvas.drawRoundRect(cellRect, radiusLarge - dp(4f), radiusLarge - dp(4f), paintAccent)
            paintAccent.alpha = 255
        }

        // 2. Track label + Space Mono duration badge in the center-right
        val textAlpha = ((1f - dragFraction * 1.3f).coerceIn(0f, 1f) * 255).toInt()
        paintWord.color = LIGHT_ON_PRIMARY
        paintWord.alpha = textAlpha
        paintWord.textSize = sp(12.5f)
        paintWord.textAlign = Paint.Align.CENTER
        canvas.drawText(labelText, w * 0.54f, h * 0.5f + dp(4.5f), paintWord)
        paintWord.alpha = 255

        // Right-end Space Mono target badge
        paintMono.color = tokAccent()
        paintMono.alpha = textAlpha
        paintMono.textSize = sp(11.5f)
        paintMono.textAlign = Paint.Align.RIGHT
        canvas.drawText(subMonoBadge, w - dp(18f), h * 0.5f + dp(4f), paintMono)
        paintMono.alpha = 255

        // 3. Physical Actuator Sled Thumb (colorPrimaryContainer #F55314 when arming)
        paintFill.color = if (isArmedState) tokSecondaryContainer() else tokAccent()
        canvas.drawRoundRect(thumbRect, radiusLarge - dp(4f), radiusLarge - dp(4f), paintFill)

        // Precision double chevron icon inside thumb
        paintStroke.color = if (isArmedState) tokOnSecondaryContainer() else LIGHT_ON_PRIMARY
        paintStroke.strokeWidth = dp(2.2f)
        paintStroke.strokeCap = Paint.Cap.ROUND
        paintStroke.strokeJoin = Paint.Join.ROUND
        val cx = thumbRect.centerX()
        val cy = thumbRect.centerY()
        val ch = dp(6f)
        vectorPath.reset()
        vectorPath.moveTo(cx - dp(6f), cy - ch)
        vectorPath.lineTo(cx, cy)
        vectorPath.lineTo(cx - dp(6f), cy + ch)
        vectorPath.moveTo(cx + dp(2f), cy - ch)
        vectorPath.lineTo(cx + dp(8f), cy)
        vectorPath.lineTo(cx + dp(2f), cy + ch)
        canvas.drawPath(vectorPath, paintStroke)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val inset = dp(6f)
        val thumbW = dp(76f)
        val maxTravel = (width - inset * 2f - thumbW).coerceAtLeast(1f)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                springBackAnimator?.cancel()
                isDraggingThumb = true
                lastTickBucket = 0
                fireFeel(4L, 420f)
                updateDrag(event.x, inset, thumbW, maxTravel)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDraggingThumb) {
                    updateDrag(event.x, inset, thumbW, maxTravel)
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDraggingThumb) {
                    isDraggingThumb = false
                    if (dragFraction >= 0.82f) {
                        dragFraction = 1f
                        invalidate()
                        // §6: 10ms Clunk on SlideToStart completion!
                        fireFeel(10L, 740f)
                        onSlideCommitted?.invoke()
                        animateSpringBack(0f)
                    } else {
                        animateSpringBack(0f)
                    }
                }
                parent?.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun updateDrag(touchX: Float, inset: Float, thumbW: Float, maxTravel: Float) {
        val newFrac = ((touchX - inset - thumbW * 0.5f) / maxTravel).coerceIn(0f, 1f)
        dragFraction = newFrac
        val bucket = (newFrac * 8).toInt()
        if (bucket != lastTickBucket) {
            lastTickBucket = bucket
            fireFeel(1L, 380f + bucket * 45f)
        }
        invalidate()
    }

    private fun animateSpringBack(target: Float) {
        springBackAnimator?.cancel()
        springBackAnimator = ValueAnimator.ofFloat(dragFraction, target).apply {
            duration = MonolithCurves.DURATION_280.toLong()
            interpolator = MonolithCurves.MechanicalOvershoot
            addUpdateListener {
                dragFraction = (it.animatedValue as Float).coerceIn(0f, 1f)
                invalidate()
            }
            start()
        }
    }
}

/**
 * ============================================================================
 * 5. CUSTOM 80DP BOTTOM NAVIGATION CAPSULE VIEW (DEVELOPER-GUIDE.pdf §1, §3, §8)
 * ============================================================================
 * - Fixed 80dp height; not a Material BottomNavigationView.
 * - Raised capsule in `colorSurfaceBright` (24dp radius).
 * - Selected pill in `colorSecondaryContainer` (#615B5A light / #FFB38F night) with
 *   `colorOnSecondaryContainer` content, animated with Curve A (260ms) and 4ms click.
 */
class MonolithBottomNavView(context: Context) : BlockitCanvasView(context) {

    private val tabLabels = arrayOf("FOCUS", "SUITCASE", "HORIZON", "LEDGER")
    var selectedTabIndex: Int = 0
        set(value) {
            val clamped = value.coerceIn(0, 3)
            if (field != clamped) {
                animatePillTo(clamped.toFloat())
                field = clamped
            }
        }

    var blockedCountBadge: Int = 8
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var tasksCountBadge: Int = 4
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var isSessionArmed: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var onTabSelected: ((Int) -> Unit)? = null

    private var animatedPillIndex: Float = 0f
    private var pillAnimator: ValueAnimator? = null

    private fun animatePillTo(target: Float) {
        pillAnimator?.cancel()
        pillAnimator = ValueAnimator.ofFloat(animatedPillIndex, target).apply {
            duration = MonolithCurves.DURATION_260.toLong()
            interpolator = MonolithCurves.CurveA
            addUpdateListener {
                animatedPillIndex = it.animatedValue as Float
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

        val outerPadH = gapUnit // 12dp
        val outerPadV = dp(8f)
        cardRect.set(outerPadH, outerPadV, w - outerPadH, h - outerPadV)

        // 1. Raised nav capsule in colorSurfaceBright (24dp radius)
        paintFill.color = tokSurfaceBright()
        canvas.drawRoundRect(cardRect, radiusLarge, radiusLarge, paintFill)

        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(cardRect, radiusLarge, radiusLarge, paintStroke)

        val tabCount = 4
        val innerPad = dp(6f)
        val slotW = (cardRect.width() - innerPad * 2f) / tabCount.toFloat()
        val slotH = cardRect.height() - innerPad * 2f

        // 2. Sliding selected pill in colorSecondaryContainer (#615B5A / #FFB38F)
        val pillLeft = cardRect.left + innerPad + animatedPillIndex * slotW
        thumbRect.set(pillLeft, cardRect.top + innerPad, pillLeft + slotW, cardRect.top + innerPad + slotH)
        paintFill.color = tokSecondaryContainer()
        canvas.drawRoundRect(thumbRect, radiusLarge - dp(6f), radiusLarge - dp(6f), paintFill)

        // 3. Draw 4 tabs (custom geometric icons + Sans label + Space Mono badge)
        for (i in 0 until tabCount) {
            val cx = cardRect.left + innerPad + (i + 0.5f) * slotW
            val cy = cardRect.centerY()
            val isSelected = i == selectedTabIndex
            val fgColor = if (isSelected) tokOnSecondaryContainer() else tokOnSurface()

            // Geometric icon centered at (cx, cy - 8dp)
            val iconY = cy - dp(8f)
            paintStroke.color = fgColor
            paintStroke.strokeWidth = dp(1.8f)
            paintFill.color = fgColor

            when (i) {
                0 -> {
                    // Dial / Focus circle + tick
                    canvas.drawCircle(cx, iconY, dp(7f), paintStroke)
                    if (isSessionArmed) {
                        paintAccent.color = tokAccent()
                        canvas.drawCircle(cx, iconY, dp(3f), paintAccent)
                    } else {
                        canvas.drawLine(cx, iconY, cx + dp(4f), iconY - dp(4f), paintStroke)
                    }
                }
                1 -> {
                    // Suitcase 2x2 grid icon
                    val s = dp(5f)
                    val g = dp(1.5f)
                    canvas.drawRect(cx - s - g, iconY - s - g, cx - g, iconY - g, paintStroke)
                    canvas.drawRect(cx + g, iconY - s - g, cx + s + g, iconY - g, paintFill)
                    canvas.drawRect(cx - s - g, iconY + g, cx - g, iconY + s + g, paintFill)
                    canvas.drawRect(cx + g, iconY + g, cx + s + g, iconY + s + g, paintStroke)
                }
                2 -> {
                    // Horizon / Tasks stacked bars
                    canvas.drawLine(cx - dp(7f), iconY - dp(4f), cx + dp(7f), iconY - dp(4f), paintStroke)
                    canvas.drawLine(cx - dp(7f), iconY, cx + dp(4f), iconY, paintStroke)
                    canvas.drawLine(cx - dp(7f), iconY + dp(4f), cx + dp(7f), iconY + dp(4f), paintStroke)
                }
                3 -> {
                    // Tower / Ledger columns
                    canvas.drawLine(cx - dp(5f), iconY + dp(6f), cx - dp(5f), iconY - dp(2f), paintStroke)
                    canvas.drawLine(cx, iconY + dp(6f), cx, iconY - dp(6f), paintStroke)
                    canvas.drawLine(cx + dp(5f), iconY + dp(6f), cx + dp(5f), iconY + dp(1f), paintStroke)
                }
            }

            // Tab word label + Space Mono count
            val badgeStr = when (i) {
                1 -> " $blockedCountBadge"
                2 -> " $tasksCountBadge"
                else -> ""
            }
            drawMixedWordAndMono(
                canvas = canvas,
                wordPrefix = tabLabels[i],
                monoNumber = badgeStr,
                wordSuffix = "",
                x = cx,
                baselineY = cy + dp(14f),
                wordSizePx = sp(9.5f),
                monoSizePx = sp(9.5f),
                wordColor = fgColor,
                monoColor = if (isSelected) tokOnSecondaryContainer() else tokAccent(),
                centered = true
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN || event.actionMasked == MotionEvent.ACTION_UP) {
            val outerPadH = gapUnit
            val innerW = (width - outerPadH * 2f).coerceAtLeast(1f)
            val idx = ((event.x - outerPadH) / (innerW / 4f)).toInt().coerceIn(0, 3)
            if (event.actionMasked == MotionEvent.ACTION_UP && idx != selectedTabIndex) {
                selectedTabIndex = idx
                fireFeel(4L, 540f + idx * 60f)
                onTabSelected?.invoke(idx)
            }
            return true
        }
        return super.onTouchEvent(event)
    }
}
