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
                        // Solid laid brick in colorPrimary ink. At
                        // night the fill caps at #A79587 (F-03: the
                        // 38% cream blend ceiling for night fills) —
                        // raw cream bricks were a wall of light. The
                        // CURRENT brick's accent (above) is the filed
                        // exception and stands.
                        paintFill.color = if (isNightMode) 0xFFA79587.toInt() else tokPrimary()
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
            fireFeel(4L)
            onBrickTapped?.invoke(totalBricksCount)
            return true
        }
        return true
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
            canvas.drawText("NOTHING QUEUED", w * 0.5f, h * 0.5f + dp(4f), paintWord)
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

            // Tonal staircase (Ren; re-voiced chrome C9 §10.2): the
            // active task is the heaviest INK, never the loudest
            // color — orange is rationed to START's text. At night
            // seg 0 takes the CAP tone (#4A423C — it was raw cream
            // #FFE4CF, ESR-14's flood) with a 1dp marker line on its
            // top edge; segs >= 1 recede in steps toward the night
            // fill ceiling #A79587, never reaching raw cream. Day:
            // seg 0 ink, seg 1 #615B5A, seg >= 2 the recess tone.
            paintFill.color = when {
                idx == 0 -> tokControlFill()
                isNightMode && idx == 1 -> 0xFF574D45.toInt()
                isNightMode && idx == 2 -> 0xFF6B5D52.toInt()
                isNightMode -> 0xFF7F6E60.toInt()
                idx == 1 -> tokSecondaryContainer()
                else -> tokSurfaceVariant()
            }
            canvas.drawRoundRect(cellRect, radiusSmall - dp(3f), radiusSmall - dp(3f), paintFill)
            if (isNightMode && idx == 0) {
                paintStroke.color = 0xE6FFE4CF.toInt()
                paintStroke.strokeWidth = dp(1f)
                canvas.drawLine(
                    cellRect.left + dp(6f), cellRect.top + dp(1f),
                    cellRect.right - dp(6f), cellRect.top + dp(1f),
                    paintStroke
                )
            }

            // MINUTES DIE (Truth Car 2): the segment's minute
            // figure prints only where an estimate was given —
            // a name-only task's segment stands unfigured rather
            // than wearing a "0m" it never earned.
            if (segW > dp(38f) && task.estimatedMinutes > 0) {
                val fg = when {
                    idx == 0 -> tokOnControlFill()
                    isNightMode -> tokTextPrimary()
                    idx == 1 -> tokOnSecondaryContainer()
                    else -> tokTextPrimary()
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

