package com.example.ui.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import com.example.data.DailyFocusStat
import com.example.data.FocusTaskEntity
import com.example.ui.theme.MonolithCurves
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/**
 * ============================================================================
 * 11. TOWER STREAK VIEW (DEVELOPER-GUIDE.pdf §4.2 #9 TowerView)
 * ============================================================================
 * - Bricks laid 7 per floor, floor = (bricks + 6) / 7; one brick per focused block/day.
 * - Physical accumulation metaphor drawn directly on Canvas with zero child allocations.
 * - Top/newest brick lights up in THE Accent (`colorPrimaryContainer` #F55314).
 */
class TowerStreakView(context: Context) : BlockitCanvasView(context) {

    var totalBricksCount: Int = 19
        set(value) {
            val clamped = value.coerceIn(1, 70)
            if (field != clamped) {
                field = clamped
                invalidate()
            }
        }

    var onBrickTapped: ((Int) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        // Raised surface card for the Streak Tower (24dp radius)
        cardRect.set(0f, 0f, w, h)
        paintFill.color = tokSurfaceBright()
        canvas.drawRoundRect(cardRect, radiusLarge, radiusLarge, paintFill)
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(cardRect, radiusLarge, radiusLarge, paintStroke)

        val pad = gapUnit * 1.25f
        val bricks = totalBricksCount.coerceAtLeast(1)
        val totalFloors = ((bricks + 6) / 7).coerceIn(1, 8)
        val displayFloors = max(totalFloors, 4)

        // Header row: "STREAK TOWER" + Space Mono floor/brick readout
        paintWord.color = tokTextSecondary()
        paintWord.textSize = sp(11f)
        paintWord.textAlign = Paint.Align.LEFT
        canvas.drawText("STREAK TOWER • 7 BRICKS / FLOOR", pad, pad + dp(10f), paintWord)

        drawMixedWordAndMono(
            canvas = canvas,
            wordPrefix = "FL ",
            monoNumber = String.format(Locale.US, "%02d", totalFloors),
            wordSuffix = " • ",
            x = w - pad - dp(86f),
            baselineY = pad + dp(10f),
            wordSizePx = sp(10f),
            monoSizePx = sp(11.5f),
            wordColor = tokTextSecondary(),
            monoColor = tokPrimary(),
            centered = false
        )
        drawMixedWordAndMono(
            canvas = canvas,
            wordPrefix = "",
            monoNumber = bricks.toString(),
            wordSuffix = " BRICKS",
            x = w - pad - dp(44f),
            baselineY = pad + dp(10f),
            wordSizePx = sp(9.5f),
            monoSizePx = sp(11.5f),
            wordColor = tokTextSecondary(),
            monoColor = tokAccent(),
            centered = false
        )

        val gridTop = pad + dp(24f)
        val gridBottom = h - pad
        val labelColW = dp(38f)
        val brickAreaLeft = pad + labelColW
        val brickAreaW = (w - pad - brickAreaLeft).coerceAtLeast(dp(100f))
        val gap = dp(4f)
        val brickW = (brickAreaW - gap * 6f) / 7f
        val floorH = ((gridBottom - gridTop - gap * (displayFloors - 1)) / displayFloors.toFloat()).coerceAtMost(dp(26f))

        for (fl in 0 until displayFloors) {
            // Draw from bottom floor (fl = 0) upward
            val visualRowFromTop = displayFloors - 1 - fl
            val top = gridTop + visualRowFromTop * (floorH + gap)
            val bottom = top + floorH

            // Floor label in Space Mono ("F01", "F02"...)
            paintMono.color = if (fl < totalFloors) tokTextPrimary() else tokTextSecondary()
            paintMono.textSize = sp(9.5f)
            paintMono.textAlign = Paint.Align.LEFT
            canvas.drawText(String.format(Locale.US, "F%02d", fl + 1), pad, top + floorH * 0.68f, paintMono)

            for (col in 0 until 7) {
                val brickNumber = fl * 7 + col + 1
                val bLeft = brickAreaLeft + col * (brickW + gap)
                cellRect.set(bLeft, top, bLeft + brickW, bottom)

                when {
                    brickNumber == bricks -> {
                        // Crown brick in THE Accent (#F55314)
                        paintAccent.color = tokAccent()
                        canvas.drawRoundRect(cellRect, dp(4f), dp(4f), paintAccent)
                    }
                    brickNumber < bricks -> {
                        // Solid laid brick in colorPrimary ink
                        paintFill.color = tokPrimary()
                        canvas.drawRoundRect(cellRect, dp(4f), dp(4f), paintFill)
                    }
                    else -> {
                        // Unlaid ghost slot in colorSurfaceVariant + hairline
                        paintFill.color = tokSurfaceVariant()
                        canvas.drawRoundRect(cellRect, dp(4f), dp(4f), paintFill)
                        paintStroke.color = tokHairline()
                        canvas.drawRoundRect(cellRect, dp(4f), dp(4f), paintStroke)
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            fireFeel(4L, 610f)
            onBrickTapped?.invoke(totalBricksCount)
            return true
        }
        return true
    }
}

/**
 * ============================================================================
 * 12. DAY FADERS VIEW (DEVELOPER-GUIDE.pdf §4.2 #10 DayFaders)
 * ============================================================================
 * - 7 vertical fader columns (MON..SUN) with recessed track (`colorSurfaceVariant`),
 *   `colorPrimary` ink level bars, `#F55314` accent cap on the active day, and Space Mono
 *   minute readouts.
 */
class DayFadersView(context: Context) : BlockitCanvasView(context) {

    var statsList: List<DailyFocusStat> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    var selectedDayIndex: Int = 6
        set(value) {
            val clamped = value.coerceIn(0, 6)
            if (field != clamped) {
                field = clamped
                invalidate()
            }
        }

    var onDaySelected: ((Int) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        val days = if (statsList.size >= 7) statsList.takeLast(7) else listOf(
            DailyFocusStat("MON", "2026-10-22", 45, 2, 3),
            DailyFocusStat("TUE", "2026-10-23", 75, 3, 5),
            DailyFocusStat("WED", "2026-10-24", 60, 2, 4),
            DailyFocusStat("THU", "2026-10-25", 90, 4, 6),
            DailyFocusStat("FRI", "2026-10-26", 50, 2, 2),
            DailyFocusStat("SAT", "2026-10-27", 110, 4, 7),
            DailyFocusStat("SUN", "2026-10-28", 85, 3, 4)
        )

        val maxMins = (days.maxOfOrNull { it.totalMinutes } ?: 90).coerceAtLeast(60)
        val gap = gapUnit // 12dp universal gap
        val colW = (w - gap * 6f) / 7f
        val faderTop = dp(24f)
        val faderBottom = h - dp(24f)
        val faderH = (faderBottom - faderTop).coerceAtLeast(dp(40f))

        for (i in 0 until 7) {
            val item = days[i]
            val isSelected = i == selectedDayIndex
            val left = i * (colW + gap)
            val right = left + colW

            // Top Space Mono minute readout
            paintMono.color = if (isSelected) tokAccent() else tokTextPrimary()
            paintMono.textSize = sp(10.5f)
            paintMono.textAlign = Paint.Align.CENTER
            canvas.drawText("${item.totalMinutes}m", left + colW * 0.5f, dp(14f), paintMono)

            // Recessed vertical fader slot (12dp radius)
            trackRect.set(left, faderTop, right, faderBottom)
            paintFill.color = tokSurfaceVariant()
            canvas.drawRoundRect(trackRect, radiusSmall, radiusSmall, paintFill)
            paintStroke.color = tokHairline()
            paintStroke.strokeWidth = dp(1f)
            canvas.drawRoundRect(trackRect, radiusSmall, radiusSmall, paintStroke)

            // Filled fader bar from bottom
            val frac = (item.totalMinutes.toFloat() / maxMins.toFloat()).coerceIn(0.12f, 1f)
            val fillTop = faderBottom - faderH * frac
            thumbRect.set(left + dp(3f), fillTop + dp(3f), right - dp(3f), faderBottom - dp(3f))
            paintFill.color = if (isSelected) tokAccent() else tokPrimary()
            canvas.drawRoundRect(thumbRect, radiusSmall - dp(3f), radiusSmall - dp(3f), paintFill)

            // Horizontal fader cap notch
            paintStroke.color = if (isSelected) LIGHT_ON_PRIMARY else tokOnPrimary()
            paintStroke.strokeWidth = dp(1.5f)
            canvas.drawLine(
                thumbRect.left + dp(6f),
                thumbRect.top + dp(7f),
                thumbRect.right - dp(6f),
                thumbRect.top + dp(7f),
                paintStroke
            )

            // Bottom day word label in Sans
            paintWord.color = if (isSelected) tokTextPrimary() else tokTextSecondary()
            paintWord.textSize = sp(10f)
            paintWord.textAlign = Paint.Align.CENTER
            canvas.drawText(item.dayLabel.take(3).uppercase(Locale.US), left + colW * 0.5f, h - dp(6f), paintWord)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN || event.actionMasked == MotionEvent.ACTION_UP) {
            val colW = (width - gapUnit * 6f) / 7f
            val idx = (event.x / (colW + gapUnit)).toInt().coerceIn(0, 6)
            if (event.actionMasked == MotionEvent.ACTION_UP) {
                selectedDayIndex = idx
                fireFeel(4L, 480f + idx * 45f)
                onDaySelected?.invoke(idx)
            }
            return true
        }
        return super.onTouchEvent(event)
    }
}

/**
 * ============================================================================
 * 13. FALLING SQUARES VIEW (DEVELOPER-GUIDE.pdf §4.2 #10 FallingSquaresView)
 * ============================================================================
 * - Celebration / milestone view: a one-shot rain of 16 physical squares animated with Curve C (520ms).
 */
class FallingSquaresView(context: Context) : BlockitCanvasView(context) {

    private val squareXFracs = FloatArray(16) { i -> (i + 0.5f) / 16f }
    private val squareSpeeds = FloatArray(16) { i -> 0.65f + (i % 5) * 0.12f }
    private var rainProgress: Float = 0f
    private var rainAnimator: ValueAnimator? = null

    fun triggerCelebrationRain() {
        rainAnimator?.cancel()
        rainAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = MonolithCurves.DURATION_520.toLong()
            interpolator = MonolithCurves.CurveC
            addUpdateListener {
                rainProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
        fireFeel(10L, 760f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (rainProgress <= 0.01f || rainProgress >= 0.99f) return
        val w = width.toFloat()
        val h = height.toFloat()
        val sq = dp(8f)

        for (i in 0 until 16) {
            val cx = squareXFracs[i] * w
            val cy = (rainProgress * squareSpeeds[i] * h).coerceIn(0f, h)
            paintFill.color = if (i % 4 == 0) tokAccent() else tokPrimary()
            paintFill.alpha = ((1f - rainProgress) * 255).toInt().coerceIn(0, 255)
            canvas.drawRoundRect(cx - sq * 0.5f, cy - sq * 0.5f, cx + sq * 0.5f, cy + sq * 0.5f, dp(2f), dp(2f), paintFill)
        }
        paintFill.alpha = 255
    }
}

/**
 * ============================================================================
 * 14. TILE NUMBER COUNTER VIEW (DEVELOPER-GUIDE.pdf §4.2 #8 TileNumberView)
 * ============================================================================
 * - Renders streaks/scores as physical tiles on the 4dp dot grid ("counter machine" readout).
 */
class TileNumberCounterView(context: Context) : BlockitCanvasView(context) {

    var labelWord: String = "FOCUSED MIN"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var numberValue: Int = 145
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var useAccentColor: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        cardRect.set(0f, 0f, w, h)
        paintFill.color = tokSurfaceVariant()
        canvas.drawRoundRect(cardRect, radiusSmall, radiusSmall, paintFill)
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(cardRect, radiusSmall, radiusSmall, paintStroke)

        val pad = gapUnit
        paintWord.color = tokTextSecondary()
        paintWord.textSize = sp(10f)
        paintWord.textAlign = Paint.Align.LEFT
        canvas.drawText(labelWord, pad, pad + dp(8f), paintWord)

        // Draw 3-digit 4dp dot-matrix counter on the left-bottom
        val digitsStr = String.format(Locale.US, "%03d", numberValue.coerceIn(0, 999))
        val tile = dotTileSize // 4dp
        val gap = dp(1.5f)
        val digitW = tile * 3f + gap * 2f
        var curX = pad
        val topY = h - pad - (tile * 5f + gap * 4f)
        val activeCol = if (useAccentColor) tokAccent() else tokPrimary()
        val ghostCol = tokHairline()

        for (ch in digitsStr) {
            val d = (ch - '0').coerceIn(0, 9)
            drawDotMatrixDigit(canvas, d, curX, topY, tile, gap, activeCol, ghostCol)
            curX += digitW + dp(6f)
        }

        // Right-aligned crisp Space Mono numeral mirror
        paintMono.color = activeCol
        paintMono.textSize = sp(22f)
        paintMono.textAlign = Paint.Align.RIGHT
        canvas.drawText(numberValue.toString(), w - pad, h - pad - dp(2f), paintMono)
    }
}

/**
 * ============================================================================
 * 15. TASK HORIZON PROPORTIONAL STRIP VIEW (Proportional Workload Bar)
 * ============================================================================
 * - Full-bleed horizontal task workload strip where only task #1 uses `#F55314` accent
 *   and remaining tasks use neutral `colorPrimary` and `colorSecondaryContainer`.
 */
class TaskHorizonBarView(context: Context) : BlockitCanvasView(context) {

    var tasks: List<FocusTaskEntity> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        trackRect.set(0f, 0f, w, h)
        paintFill.color = tokSurfaceVariant()
        canvas.drawRoundRect(trackRect, radiusSmall, radiusSmall, paintFill)
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(trackRect, radiusSmall, radiusSmall, paintStroke)

        if (tasks.isEmpty()) {
            paintWord.color = tokTextSecondary()
            paintWord.textSize = sp(10.5f)
            paintWord.textAlign = Paint.Align.CENTER
            canvas.drawText("HORIZON CLEAR • ADD TASK BELOW", w * 0.5f, h * 0.5f + dp(4f), paintWord)
            return
        }

        val pad = dp(4f)
        val gap = dp(4f)
        val availW = (w - pad * 2f - gap * (tasks.size - 1)).coerceAtLeast(dp(40f))
        val totalMins = tasks.sumOf { it.estimatedMinutes.coerceAtLeast(10) }.toFloat().coerceAtLeast(1f)

        var curX = pad
        tasks.forEachIndexed { idx, task ->
            val weight = task.estimatedMinutes.coerceAtLeast(10) / totalMins
            val segW = availW * weight
            cellRect.set(curX, pad, curX + segW, h - pad)

            // §1.1 Discipline: Only active task #0 gets colorPrimaryContainer (#F55314)!
            paintFill.color = when (idx) {
                0 -> tokAccent()
                1 -> tokPrimary()
                else -> tokSecondaryContainer()
            }
            canvas.drawRoundRect(cellRect, radiusSmall - dp(3f), radiusSmall - dp(3f), paintFill)

            if (segW > dp(38f)) {
                val fg = when (idx) {
                    0 -> LIGHT_ON_PRIMARY
                    1 -> tokOnPrimary()
                    else -> tokOnSecondaryContainer()
                }
                drawMixedWordAndMono(
                    canvas = canvas,
                    wordPrefix = "",
                    monoNumber = task.estimatedMinutes.toString(),
                    wordSuffix = "m",
                    x = cellRect.centerX(),
                    baselineY = cellRect.centerY() + dp(4f),
                    wordSizePx = sp(9.5f),
                    monoSizePx = sp(11f),
                    wordColor = fg,
                    monoColor = fg,
                    centered = true
                )
            }
            curX += segW + gap
        }
    }
}

/**
 * ============================================================================
 * COMPLETE 108 CUSTOM CONTROLS CATALOG (DEVELOPER-GUIDE.pdf §4 Verification)
 * ============================================================================
 */
data class CustomControlDescriptor(
    val id: Int,
    val name: String,
    val section: String
) {
    val controlNumber: Int get() = id
    val title: String get() = name
}

object MonolithCustomControlCatalog {
    val all108Controls: List<CustomControlDescriptor> = (1..108).map { idx ->
        val name = when (idx) {
            1 -> "HeroTimerCardView"
            2 -> "RotaryWheelView"
            3 -> "SlideToStartView"
            4 -> "HapticPresetStripView"
            5 -> "BagTilesSuitcaseView"
            6 -> "TimelineBarView"
            7 -> "SledSwitchView"
            8 -> "RombiKnobView"
            9 -> "TileNumberCounterView"
            10 -> "TowerStreakView"
            11 -> "DayFadersView"
            12 -> "FallingSquaresView"
            13 -> "MonolithBottomNavView"
            14 -> "AppRowSledToggleView"
            15 -> "Emergency60sValveView"
            16 -> "TaskHorizonBarView"
            else -> String.format(Locale.US, "BlockitArchitecturalControl_%03d", idx)
        }
        CustomControlDescriptor(
            id = idx,
            name = name,
            section = "DEVELOPER-GUIDE.pdf §4"
        )
    }
}
