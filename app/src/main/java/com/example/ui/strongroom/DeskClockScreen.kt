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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.domain.DeskClockRecord
import kotlinx.coroutines.delay
import java.time.LocalTime
import kotlin.math.cos
import kotlin.math.sin

/**
 * P8a — THE DESK CLOCK (BUILD-SR-6). The regulator on the room's
 * wall: a live-forming record with NO NUMBERS anywhere — this file
 * contains no text composable and no digit, by construction. The
 * form itself is the information:
 *
 *  - THE FORMING RIM. The day's record forms around the dial as a
 *    gold rim, grain by grain, as a PREFIX of the sealed span
 *    (DeskClockRecord): formed gold is the day already held; the
 *    unformed groove ahead of it holds its full advance and reads
 *    as not-yet. Quantity is extent (Stage 5 §7, FF-1).
 *  - THE DAY'S OWN MARKS. Each event the day made is punched onto
 *    the rim at its place, at P1's graded sizes (reach large ·
 *    held medium · floor small); each shaped gap already lived is
 *    drawn warm across its span. Nothing else can appear: the
 *    record is time's passage plus the day's own acts, and it
 *    carries no verdict (CRITIC-SR-1 W-3b / U-6).
 *  - THE HANDS. Hour and minute hands on a numeral-less dial —
 *    time of day read from position at a glance, one touch,
 *    nothing opened, nothing spent (CRITIC-SR-1 C-2 / W-3a).
 *    Positions, not figures: the no-numbers law stands whole.
 *
 * STILLNESS IS THE DESIGN. There is no animation, no second hand,
 * no sound, no haptic for the forming (slice gauntlet H-4): the
 * face re-reads its facts on a minute boundary and at no other
 * time, and a grain takes a quarter hour to form on a twelve-hour
 * span. The clock accretes between glances — dopamine at a PASSIVE
 * moment only (ledger E-5) — and never performs while watched.
 *
 * The light is RELIGHT (P0's law, at this lane's seam): the ground
 * is the violet enamel descended under the SEALED state, and every
 * mark's relief is derived from its own luminance against the
 * field it sits on — no colour here is a raw hex outside
 * [ClockInks], and no tone exists that is not an ink or an ink
 * moved by whole eighths along its own luminance.
 *
 * IN THE ROOM, NEVER ON THE DOOR (ledger A-9): this surface lives
 * inside the sealed hours, beside the antechamber — the sealed
 * face (P3) is engraved once and carries none of this.
 */

/** The light's direction, as filed (upper left, low angle). */
private const val LIGHT_X = -0.83f
private const val LIGHT_Y = -0.55f

/** A point's light gradient: its position projected on the light. */
private fun gradientAt(angleDegrees: Float): Float {
    val rad = Math.toRadians(angleDegrees.toDouble())
    return (cos(rad) * LIGHT_X + sin(rad) * LIGHT_Y).toFloat().coerceIn(-1f, 1f)
}

@Composable
fun DeskClockFace(
    record: DeskClockRecord.FormingRecord,
    spanDurationMillis: Long,
    hands: DeskClockRecord.HandPositions,
    modifier: Modifier = Modifier,
    relightState: ClockRelightState = ClockRelightState.SEALED
) {
    val ground = ClockRelight.stateSurface(ClockInks.Ground, relightState)
    val groove = ClockRelight.descend(ground, 2)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(24.dp)
            .semantics { contentDescription = "Desk clock — the day's record, forming" }
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val center = Offset(cx, cy)
        val radius = (minOf(size.width, size.height) / 2f) * 0.94f
        val rimRadius = radius * 0.86f
        val rimWidth = 11.dp.toPx()

        // The ground: the room's violet enamel, descended per state.
        drawCircle(color = ground, radius = radius, center = center)

        // Gold leaf rim + pinstripe — the regulator's case line.
        val leaf = ClockRelight.stateSurface(ClockInks.Leaf, relightState)
        drawCircle(color = leaf, radius = radius * 0.985f, center = center, style = Stroke(width = 2.5.dp.toPx()))
        drawCircle(color = leaf, radius = radius * 0.70f, center = center, style = Stroke(width = 1.25.dp.toPx()))

        // Tick marks: sixty, the twelve hour ticks longer — marks,
        // never numerals. Each tick's relief comes from its own
        // position against the light.
        for (tick in 0 until 60) {
            val angle = tick * 6f
            val rad = Math.toRadians((angle - 90f).toDouble())
            val isHour = tick % 5 == 0
            val inner = rimRadius * (if (isHour) 1.10f else 1.13f)
            val outer = rimRadius * 1.19f
            val tickColor = ClockRelight.relief(ClockInks.LineWork, ground, gradientAt(angle - 90f), relightState)
            drawLine(
                color = tickColor,
                start = Offset(cx + (cos(rad) * inner).toFloat(), cy + (sin(rad) * inner).toFloat()),
                end = Offset(cx + (cos(rad) * outer).toFloat(), cy + (sin(rad) * outer).toFloat()),
                strokeWidth = (if (isHour) 2.5.dp else 1.25.dp).toPx(),
                cap = StrokeCap.Round
            )
        }

        // The unformed groove: the whole rim, recessed — the not-yet
        // holds its full advance (PF-C) and reads as absence.
        drawCircle(color = groove, radius = rimRadius, center = center, style = Stroke(width = rimWidth))

        // The formed rim: gold, grain by grain, a prefix. Each grain
        // is relieved from its own place on the dial.
        val grainStep = 360f / DeskClockRecord.GRAINS
        for (grain in 0 until record.formedGrains) {
            val start = -90f + grain * grainStep
            val grainColor = ClockRelight.relief(
                ClockInks.Leaf, ground, gradientAt(start + grainStep / 2f), relightState
            )
            drawArc(
                color = grainColor,
                startAngle = start + 0.9f,
                sweepAngle = grainStep - 1.8f,
                useCenter = false,
                topLeft = Offset(cx - rimRadius, cy - rimRadius),
                size = androidx.compose.ui.geometry.Size(rimRadius * 2f, rimRadius * 2f),
                style = Stroke(width = rimWidth)
            )
        }

        // Lived shaped gaps: warm ember across their span of the rim.
        for (gap in record.gaps) {
            val startFraction = DeskClockRecord.fractionAt(spanDurationMillis, gap.startOffsetMillis)
            val endFraction = DeskClockRecord.fractionAt(spanDurationMillis, gap.endOffsetMillis)
            drawArc(
                color = ClockRelight.relief(ClockInks.GapWarmth, ground, 0.4f, relightState),
                startAngle = -90f + startFraction * 360f,
                sweepAngle = (endFraction - startFraction) * 360f,
                useCenter = false,
                topLeft = Offset(cx - rimRadius, cy - rimRadius),
                size = androidx.compose.ui.geometry.Size(rimRadius * 2f, rimRadius * 2f),
                style = Stroke(width = rimWidth * 0.45f)
            )
        }

        // The day's own marks, punched at their places, graded.
        for (mark in record.marks) {
            val fraction = DeskClockRecord.fractionAt(spanDurationMillis, mark.atOffsetMillis)
            val rad = Math.toRadians((-90f + fraction * 360f).toDouble())
            val dotCenter = Offset(
                cx + (cos(rad) * rimRadius).toFloat(),
                cy + (sin(rad) * rimRadius).toFloat()
            )
            val dotRadius = when (mark.size) {
                DeskClockRecord.MarkSize.LARGE -> 8.5.dp.toPx()
                DeskClockRecord.MarkSize.MEDIUM -> 6.dp.toPx()
                DeskClockRecord.MarkSize.SMALL -> 3.75.dp.toPx()
            }
            // The punch's lit lip, one step toward the light.
            drawCircle(
                color = ClockRelight.relief(ClockInks.LineWork, ground, 1f, relightState),
                radius = dotRadius,
                center = dotCenter + Offset(LIGHT_X * 1.5.dp.toPx(), LIGHT_Y * 1.5.dp.toPx())
            )
            drawCircle(color = ClockInks.LineWork, radius = dotRadius, center = dotCenter)
        }

        // The hands: gold leaf over a graphite shadow — positions,
        // read at a glance. No second hand; a regulator at rest.
        fun hand(angleDegrees: Float, length: Float, widthDp: Float) {
            val rad = Math.toRadians((angleDegrees - 90f).toDouble())
            val end = Offset(cx + (cos(rad) * length).toFloat(), cy + (sin(rad) * length).toFloat())
            drawLine(
                color = ClockInks.LineWork.copy(alpha = 0.55f),
                start = center + Offset(2.dp.toPx(), 3.dp.toPx()),
                end = end + Offset(2.dp.toPx(), 3.dp.toPx()),
                strokeWidth = widthDp.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(color = leaf, start = center, end = end, strokeWidth = widthDp.dp.toPx(), cap = StrokeCap.Round)
        }
        hand(hands.hourAngleDegrees, radius * 0.42f, 7f)
        hand(hands.minuteAngleDegrees, radius * 0.64f, 4.5f)
        drawCircle(color = leaf, radius = 7.dp.toPx(), center = center)
        drawCircle(color = ClockInks.LineWork, radius = 3.dp.toPx(), center = center)
    }
}

/**
 * The hosted clock: reads the seal's facts and the wall, re-reading
 * on the minute boundary and at no other time. [sealedAtElapsedMillis]
 * is the seal's monotonic (elapsedRealtime-basis) start — P1's seam:
 * at composition the host passes `SealSpan.sealedAtElapsedMillis`
 * and `SealSpan.spanDurationMillis`, with `Punch`/`ShapedGap`
 * adapted to [DeskClockRecord]'s plain facts. In-session only: the
 * host shows this surface only while a seal stands.
 */
@Composable
fun DeskClockScreen(
    spanDurationMillis: Long,
    sealedAtElapsedMillis: Long,
    marks: List<DeskClockRecord.Mark>,
    gaps: List<DeskClockRecord.GapSpan>,
    modifier: Modifier = Modifier,
    relightState: ClockRelightState = ClockRelightState.SEALED
) {
    var tick by remember { mutableStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(spanDurationMillis, sealedAtElapsedMillis) {
        while (true) {
            tick = SystemClock.elapsedRealtime()
            val nowWall = System.currentTimeMillis()
            val toNextMinute = 60_000L - (nowWall % 60_000L)
            delay(toNextMinute.coerceIn(1_000L, 60_000L))
        }
    }
    val elapsed = (tick - sealedAtElapsedMillis).coerceAtLeast(0L)
    val record = remember(tick, marks, gaps) {
        DeskClockRecord.record(spanDurationMillis, elapsed, marks, gaps)
    }
    val now = LocalTime.now()
    val hands = remember(tick) { DeskClockRecord.hands(now.hour, now.minute) }
    DeskClockFace(
        record = record,
        spanDurationMillis = spanDurationMillis,
        hands = hands,
        modifier = modifier,
        relightState = relightState
    )
}
