package com.example.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import com.example.data.DailyFocusStat
import com.example.ui.theme.MonolithCurves
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import com.example.data.FocusTaskEntity

/**
 * Issue #61 (Audit R1 · blockit-tower-views) — truth fixes.
 *
 * TowerStreakView was DELETED, not patched: on current main it had no
 * construction site anywhere (only an unused import in
 * AirlockAndDisarmOverlays.kt, removed with it). That single deletion
 * resolves three findings at their root:
 *  1. [CRITICAL] 70-claim / 56-drawn / crown-vanishes lie — the view
 *     that lied no longer exists; there is no capacity to misclaim.
 *  2. [HIGH] Fake onBrickTapped (fired totalBricksCount on any tap,
 *     consumed every touch) — the callback and its touch consumption
 *     die with the view; no consumer ever existed for it.
 *  3. [HIGH] Fabricated default streak (totalBricksCount = 19) — the
 *     invented number dies with the view. A future streak surface must
 *     be built on real data or show nothing.
 *
 * What remains in this file is the live TaskHorizonBarView (used by
 * FocusSequencerScreen), fixed for findings 4 and 5 below.
 */

/**
 * Pure layout maths for [TaskHorizonBarView] — extracted so the truth
 * properties are unit-testable without a Canvas.
 *
 * Truth contract:
 *  - Widths are proportional to the REAL estimatedMinutes. No silent
 *    floor: a 1-minute task against a 100-minute task gets 1/101 of
 *    the proportional pool, never 10/110.
 *  - A segment whose proportional width would be unreadably small, and
 *    any 0-minute (name-only) task, is drawn at exactly [minMarkerW]
 *    and flagged [Segment.isMinMarker] so the renderer draws it in a
 *    VISIBLY DISTINCT marker style (outline, no solid fill, no minute
 *    figure) — a readability marker, never fake duration.
 *  - Degradation: at most `capacity` segments are ever laid out, where
 *    capacity is how many minimum markers + gaps fit in availW. Any
 *    tail beyond that is aggregated into ONE overflow segment whose
 *    minutes are the honest sum of the tail and whose label is "+N".
 *  - Invariant: sum(widths) + gap * (segments - 1) <= availW (+0.5f
 *    rounding slack). Nothing is ever laid out off-canvas.
 */
object HorizonLayout {

    data class Segment(
        /** Index into the original task list, or -1 for the overflow segment. */
        val taskIndex: Int,
        val minutes: Int,
        val width: Float,
        val isMinMarker: Boolean,
        /** 0 for a normal segment; N for the overflow segment ("+N more"). */
        val overflowCount: Int = 0
    )

    fun plan(
        minutes: List<Int>,
        availW: Float,
        gap: Float,
        minMarkerW: Float
    ): List<Segment> {
        if (minutes.isEmpty() || availW <= 0f) return emptyList()
        val safeMin = minMarkerW.coerceAtLeast(1f)
        val safeGap = gap.coerceAtLeast(0f)

        // Capacity: how many minimum-width segments fit, gaps included.
        val capacity = ((availW + safeGap) / (safeMin + safeGap)).toInt().coerceAtLeast(1)

        // Build entries: visible individuals + optional overflow entry.
        data class Entry(val taskIndex: Int, val minutes: Int, val overflowCount: Int)
        val entries: List<Entry> = if (minutes.size <= capacity) {
            minutes.mapIndexed { i, m -> Entry(i, m.coerceAtLeast(0), 0) }
        } else {
            val visibleCount = (capacity - 1).coerceAtLeast(0)
            val tail = minutes.drop(visibleCount)
            minutes.take(visibleCount).mapIndexed { i, m -> Entry(i, m.coerceAtLeast(0), 0) } +
                Entry(-1, tail.sumOf { it.coerceAtLeast(0) }, tail.size)
        }
        val segCount = entries.size
        val gapsTotal = safeGap * (segCount - 1).coerceAtLeast(0)
        val usable = (availW - gapsTotal).coerceAtLeast(safeMin * segCount)

        // Width allocation with water-filling: positive-minute entries
        // share proportionally; any entry whose share falls below the
        // minimum is pinned to the minimum and flagged as a marker, and
        // the rest re-share what remains. 0-minute entries are markers
        // from the start (they earned no duration at all).
        val widths = FloatArray(segCount) { 0f }
        val isMarker = BooleanArray(segCount) { entries[it].minutes <= 0 }
        var remaining = usable
        // Pin zero-minute entries first.
        for (i in entries.indices) if (isMarker[i]) {
            widths[i] = safeMin
            remaining -= safeMin
        }
        var pool = entries.indices.filter { !isMarker[it] }
        while (pool.isNotEmpty()) {
            val poolMins = pool.sumOf { entries[it].minutes }.toFloat()
            if (poolMins <= 0f || remaining <= 0f) {
                for (i in pool) { widths[i] = safeMin; isMarker[i] = true }
                break
            }
            val below = pool.filter { remaining * entries[it].minutes / poolMins < safeMin }
            if (below.isEmpty()) {
                for (i in pool) widths[i] = remaining * entries[i].minutes / poolMins
                break
            }
            for (i in below) {
                widths[i] = safeMin
                isMarker[i] = true
                remaining -= safeMin
            }
            pool = pool.filter { it !in below }
        }
        return entries.mapIndexed { i, e ->
            Segment(e.taskIndex, e.minutes, widths[i], isMarker[i], e.overflowCount)
        }
    }
}

/**
 * ============================================================================
 * 15. TASK HORIZON PROPORTIONAL STRIP VIEW (Proportional Workload Bar)
 * ============================================================================
 * - Full-bleed horizontal task workload strip where only task #1 uses `#F55314` accent
 *   and remaining tasks use neutral `colorPrimary` and `colorSecondaryContainer`.
 * - Issue #61: truly proportional (no 10-minute padding), min-width
 *   segments are visibly distinct markers, and many tasks degrade to
 *   an honest "+N more" overflow segment instead of drawing off-canvas.
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
        val availW = (w - pad * 2f).coerceAtLeast(dp(40f))
        val plan = HorizonLayout.plan(
            minutes = tasks.map { it.estimatedMinutes },
            availW = availW,
            gap = gap,
            minMarkerW = dp(10f)
        )

        var curX = pad
        plan.forEach { seg ->
            val segW = seg.width
            // Hard canvas bound: never draw past the right pad, ever.
            val right = minOf(curX + segW, w - pad)
            cellRect.set(curX, pad, right, h - pad)
            val idx = if (seg.overflowCount > 0) Int.MAX_VALUE else seg.taskIndex

            if (seg.isMinMarker) {
                // VISIBLY DISTINCT minimum marker: no solid fill, just
                // a hairline outline — it claims presence, not duration.
                paintStroke.color = tokHairline()
                paintStroke.strokeWidth = dp(1f)
                canvas.drawRoundRect(cellRect, radiusSmall - dp(3f), radiusSmall - dp(3f), paintStroke)
            } else {
                // Tonal staircase (Ren; re-voiced chrome C9 §10.2): the
                // active task is the heaviest INK, never the loudest
                // color — orange is rationed to START's text. At night
                // seg 0 takes the CAP tone (#4A423C) with a 1dp marker
                // line on its top edge; segs >= 1 recede in steps.
                paintFill.color = when {
                    seg.overflowCount > 0 -> tokSurfaceVariant()
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
            }

            if (seg.overflowCount > 0) {
                // Honest degradation label: "+N" — the tail exists, is
                // counted, and its minutes were summed into this width.
                if (segW > dp(24f)) {
                    paintWord.color = tokTextSecondary()
                    paintWord.textSize = sp(10f)
                    paintWord.textAlign = Paint.Align.CENTER
                    canvas.drawText("+${seg.overflowCount}", cellRect.centerX(), cellRect.centerY() + dp(4f), paintWord)
                }
            } else if (!seg.isMinMarker && segW > dp(38f) && seg.minutes > 0) {
                // Minute figure only where an estimate was given and
                // the segment is a real proportional segment.
                val fg = when {
                    idx == 0 -> tokOnControlFill()
                    isNightMode -> tokTextPrimary()
                    idx == 1 -> tokOnSecondaryContainer()
                    else -> tokTextPrimary()
                }
                drawMixedWordAndMono(
                    canvas = canvas,
                    wordPrefix = "",
                    monoNumber = seg.minutes.toString(),
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
