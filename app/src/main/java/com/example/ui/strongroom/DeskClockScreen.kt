package com.example.ui.strongroom

import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.domain.DeskClockRecord
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin

/**
 * P8a — THE DESK CLOCK, AS THE FORMING PLATE (BUILD-SR-6; visual
 * language by DESIGNER-SR-6, `forming-plate-design.md` — the plate
 * prefigured). The regulator's case on the room's wall holds the
 * day's plate blank itself: the record he watches form all day is
 * the plate P6 strikes at the lifting — same blank, same field,
 * same holes in the same places.
 *
 * NO NUMBERS, NO TIME-OF-DAY — this file contains no text
 * composable, no digit, no hand, no dial (Ruling R-C2: time-of-day
 * inside the seal is none, by decision; an earlier draft's hands
 * were sent back under E-3/E-4 and are absent BY DECISION, named
 * here so the absence is read as one). What the surface carries:
 *
 *  - THE FIELD, forming as a PREFIX in reading order: struck grain
 *    (8 ticks to a column, relief from the surface's own luminance
 *    under RELIGHT) over a flat ghost that holds the unformed
 *    field's full advance — the not-yet, never content (PF-C).
 *  - THE DAY'S SHAPE AS TEXTURE: gap spans form SMOOTH (burnished,
 *    ember warmth at relief strength only); the break's firing
 *    forms SMOOTH with twin struck edge-lines. A gap's arrival is
 *    learned from the frontier's texture changing — its name is
 *    P2/P5's surfaces' business, never this record's.
 *  - PUNCHES IN PLACE: each event at its field position, in its
 *    lane (reach upper · held middle · showing lower) at its
 *    graded diameter — dark core with a RELIGHT crescent.
 *  - TWO KEPT PLACES, EMPTY: the hours margin (figures are the
 *    strike's business) and the seal place (a flat ring outline —
 *    the ring-impression is P6's strike, never drawn here).
 *
 * STILLNESS (MOTION-SR-3's budgets, DESIGNER-SR-6 §3): appearance
 * is a CUT on the host's minute-boundary tick — no fade, slide,
 * scale, shimmer, or sweep anywhere in this file; no animation
 * loop is held between ticks; no sound, no haptic attends a column
 * (slice gauntlet H-4). The tick is CLOCK-LOCAL: it lives inside
 * [DeskClockScreen] and is never shared with, or collected by, the
 * sealed face — the face holds no subscription to it (Z-5).
 * When the seal dies the host passes [formationHalted] and the
 * tick stops: the frozen prefix, nothing added (§8).
 *
 * IN THE ROOM, NEVER ON THE DOOR (ledger A-9).
 */

/** Deterministic per-tick luminance variation — the hand-struck
 *  unevenness of a real strike field (prototype's `vary`, lifted). */
private fun tickVary(index: Int): Float =
    0.72f + 0.28f * abs((sin(index * 12.9898) * 43758.5453 % 1.0)).toFloat()

@Composable
fun DeskClockFace(
    plate: DeskClockRecord.FormingPlate,
    gaps: List<DeskClockRecord.GapSpan>,
    spanDurationMillis: Long,
    modifier: Modifier = Modifier,
    relightState: ClockRelightState = ClockRelightState.SEALED
) {
    val ground = ClockRelight.stateSurface(ClockInks.Ground, relightState)
    val ghost = ClockRelight.blendEighth(ground, ClockInks.Leaf, 1)
    val leaf = ClockRelight.stateSurface(ClockInks.Leaf, relightState)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(5f / 3f)
            .padding(16.dp)
            .semantics { contentDescription = "Desk clock — the day's plate, forming" }
    ) {
        val w = size.width
        val h = size.height
        val scale = w / FormingPlateGeometry.REFERENCE_WIDTH

        // The room's ground, then the blank placed on it.
        drawRect(color = ground, size = Size(w, h))
        drawRoundRect(
            color = ghost,
            topLeft = Offset(0f, 0f),
            size = Size(w, h),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )
        // Pinstripe: the blank is placed from the first moment.
        val pinInset = FormingPlateGeometry.PINSTRIPE_INSET_FRACTION * w
        drawRoundRect(
            color = leaf,
            topLeft = Offset(pinInset, pinInset),
            size = Size(w - 2 * pinInset, h - 2 * pinInset),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
            style = Stroke(width = 1.4f * scale)
        )

        // The field's frame.
        val fieldX = FormingPlateGeometry.HOURS_MARGIN_FRACTION * w
        val fieldW = FormingPlateGeometry.FIELD_FRACTION * w
        val fieldY = FormingPlateGeometry.FIELD_INSET_FRACTION * h
        val fieldH = h - 2 * fieldY
        val columnW = fieldW / DeskClockRecord.COLUMNS

        // Formed columns, prefix only. The ghost ahead of the
        // frontier is the blank itself — nothing is drawn there.
        for (column in 0 until plate.formedColumns) {
            val x = fieldX + column * columnW
            when (plate.columnKinds[column]) {
                DeskClockRecord.ColumnKind.GRAIN -> {
                    val tickH = fieldH / FormingPlateGeometry.TICKS_PER_COLUMN
                    for (row in 0 until FormingPlateGeometry.TICKS_PER_COLUMN) {
                        val cy = fieldY + (row + 0.5f) * tickH
                        val alpha = tickVary(column * FormingPlateGeometry.TICKS_PER_COLUMN + row) *
                            if (relightState == ClockRelightState.DARK) 0.55f else 0.95f
                        drawLine(
                            color = leaf.copy(alpha = alpha),
                            start = Offset(x + columnW / 2f, cy - tickH * 0.31f),
                            end = Offset(x + columnW / 2f, cy + tickH * 0.31f),
                            strokeWidth = (columnW * 0.52f).coerceAtLeast(1f),
                            cap = StrokeCap.Round
                        )
                    }
                }
                DeskClockRecord.ColumnKind.SMOOTH, DeskClockRecord.ColumnKind.SMOOTH_FIRING -> {
                    // A burnished band: even luminance, ember warmth
                    // at relief strength only — never a filled stripe.
                    drawRect(
                        color = leaf.copy(alpha = if (relightState == ClockRelightState.DARK) 0.30f else 0.52f),
                        topLeft = Offset(x, fieldY),
                        size = Size(columnW + 0.5f, fieldH)
                    )
                    drawRect(
                        color = ClockInks.GapWarmth.copy(alpha = if (relightState == ClockRelightState.DARK) 0.05f else 0.16f),
                        topLeft = Offset(x, fieldY),
                        size = Size(columnW + 0.5f, fieldH)
                    )
                }
            }
        }

        // The firing's twin edge-lines, at its span's true edges.
        for (gap in gaps) {
            if (!gap.isBreakFiring) continue
            val startX = fieldX + (gap.startOffsetMillis.toFloat() / spanDurationMillis.toFloat()) * fieldW
            val endX = fieldX + (gap.endOffsetMillis.toFloat() / spanDurationMillis.toFloat()) * fieldW
            val formedEdge = fieldX + plate.formedColumns * columnW
            for (edgeX in listOf(startX, endX)) {
                if (edgeX <= formedEdge + columnW) {
                    drawLine(
                        color = ClockInks.LineWork,
                        start = Offset(edgeX, fieldY),
                        end = Offset(edgeX, fieldY + fieldH),
                        strokeWidth = 1.2f * scale
                    )
                }
            }
        }

        // Punches in place: dark core, RELIGHT crescent above.
        for (punch in plate.punches) {
            val cx = fieldX + punch.xFraction * fieldW
            val cy = fieldY + when (punch.lane) {
                DeskClockRecord.Lane.UPPER -> FormingPlateGeometry.LANE_UPPER_FRACTION * fieldH
                DeskClockRecord.Lane.MIDDLE -> FormingPlateGeometry.LANE_MIDDLE_FRACTION * fieldH
                DeskClockRecord.Lane.LOWER -> FormingPlateGeometry.LANE_LOWER_FRACTION * fieldH
            }
            val diameter = when (punch.diameterClass) {
                DeskClockRecord.DiameterClass.LARGE -> FormingPlateGeometry.REACH_DIAMETER
                DeskClockRecord.DiameterClass.MEDIUM -> FormingPlateGeometry.HELD_DIAMETER
                DeskClockRecord.DiameterClass.SMALL -> FormingPlateGeometry.SHOWING_DIAMETER
            }
            val r = diameter / 2f * scale
            drawCircle(color = ClockInks.LineWork, radius = r, center = Offset(cx, cy))
            drawArc(
                color = leaf.copy(alpha = 0.55f),
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(cx - r, cy - r),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = 0.9f * scale)
            )
        }

        // The seal place: an EMPTY ring-place — flat outline, never
        // filled here. The ring itself is P6's strike.
        val sealMarginW = FormingPlateGeometry.SEAL_MARGIN_FRACTION * w
        val sealCenter = Offset(w - sealMarginW / 2f, h / 2f)
        drawCircle(
            color = ClockInks.LineWork.copy(alpha = 0.6f),
            radius = FormingPlateGeometry.SEAL_RING_RADIUS * scale,
            center = sealCenter,
            style = Stroke(width = 1.2f * scale)
        )
    }
}

/**
 * The hosted clock: reads the seal's facts, re-reading on the
 * minute boundary and at no other time. The tick is local to this
 * composable (MOTION-SR-3 Z-5). [formationHalted] freezes the
 * record where the seal died — the tick stops, nothing is added.
 * Seam: at composition the host passes P1's `SealSpan` facts
 * (sealedAt / spanDuration) with `Punch`/`ShapedGap` adapted to
 * [DeskClockRecord]'s plain facts. In-session only.
 */
@Composable
fun DeskClockScreen(
    spanDurationMillis: Long,
    sealedAtElapsedMillis: Long,
    punches: List<DeskClockRecord.PunchFact>,
    gaps: List<DeskClockRecord.GapSpan>,
    modifier: Modifier = Modifier,
    relightState: ClockRelightState = ClockRelightState.SEALED,
    formationHalted: Boolean = false
) {
    var tick by remember { mutableStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(spanDurationMillis, sealedAtElapsedMillis, formationHalted) {
        if (formationHalted) return@LaunchedEffect
        while (true) {
            tick = SystemClock.elapsedRealtime()
            val nowWall = System.currentTimeMillis()
            val toNextMinute = 60_000L - (nowWall % 60_000L)
            delay(toNextMinute.coerceIn(1_000L, 60_000L))
        }
    }
    val elapsed = (tick - sealedAtElapsedMillis).coerceAtLeast(0L)
    val plate = remember(tick, punches, gaps) {
        DeskClockRecord.record(spanDurationMillis, elapsed, punches, gaps)
    }
    DeskClockFace(
        plate = plate,
        gaps = gaps,
        spanDurationMillis = spanDurationMillis,
        modifier = modifier,
        relightState = relightState
    )
}
