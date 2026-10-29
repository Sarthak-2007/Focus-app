package com.example.ui.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import com.example.data.DailyFocusStat
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


