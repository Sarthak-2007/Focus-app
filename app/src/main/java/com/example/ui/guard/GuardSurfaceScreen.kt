package com.example.ui.guard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.ui.strongroom.StrongroomCopy
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import kotlin.math.atan2

/**
 * P7 — THE GUARD SURFACE (BUILD-SR-5, Slice 1).
 *
 * The vault's control room, not a settings page: the kept
 * violet ground, the gold pinstripe, and controls with weight.
 * This composable renders [GuardSurfaceModel] and NOTHING else
 * — the inventory decides what exists. A loosening control
 * beyond the hard stop is not drawn greyed; it is not drawn.
 * An off inside a stretch is not drawn at all. There is no
 * witness or urge plate anywhere in this file, because the
 * model's census has none to draw.
 *
 * The surface holds no Guard state of its own: it takes one
 * [GuardSurfaceSnapshot] and speaks [GuardIntent]s. Its only
 * local state is the wheels' turn and the stretch stepper —
 * presentation, settled into intents. Detent ticks are the
 * wiring layer's voice ([onDetentTick] → the tactile engine);
 * this file makes no sound and feels no haptic itself.
 *
 * G-a UNRESOLVED (carried from the model): the raise section
 * applies D-1 as filed — from the next day-start — and carries
 * no two-signature step. See GuardSurfaceModel's header.
 */
@Composable
fun GuardSurfaceScreen(
    snapshot: GuardSurfaceSnapshot,
    onIntent: (GuardIntent) -> Unit,
    onDetentTick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val model = remember(snapshot) { GuardSurface.surfaceFor(snapshot) }
    val groundDeep = GuardInkLine.copy(alpha = 0.35f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GuardGround, GuardGround, groundDeep)))
            .testTag("guard_surface")
    ) {
        PinstripeField()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 36.dp)
        ) {
            Text(GuardCopy.TITLE, style = GuardTitleStyle, color = GuardLeaf)
            Spacer(Modifier.height(10.dp))
            Text(model.standingLine, style = GuardStandingStyle, color = GuardLeaf)
            Spacer(Modifier.height(6.dp))
            Text(model.allowanceLine, style = GuardFigureStyle, color = GuardLeaf)

            // --------------------------------------------------
            // THE ALLOWANCE — the tightening wheel always stands;
            // the raising wheel stands only inside the inventory.
            // --------------------------------------------------
            Spacer(Modifier.height(34.dp))
            if (model.has(GuardControl.ALLOWANCE_TIGHTEN_WHEEL)) {
                val detents = remember(snapshot.allowanceMinutes) {
                    GuardSurface.tightenDetents(snapshot.allowanceMinutes)
                }
                Text(GuardCopy.TIGHTEN_TITLE, style = GuardSectionStyle, color = GuardLeaf)
                Spacer(Modifier.height(12.dp))
                AllowanceDial(
                    detentValues = detents,
                    standingValue = snapshot.allowanceMinutes,
                    onDetentTick = onDetentTick,
                    onSettled = { minutes ->
                        if (minutes != snapshot.allowanceMinutes) {
                            onIntent(GuardIntent.Tighten(minutes))
                        }
                    },
                    modifier = Modifier.testTag("guard_tighten_wheel")
                )
            }

            model.pendingRaiseLine?.let { line ->
                Spacer(Modifier.height(14.dp))
                Text(line, style = GuardSecondaryStyle, color = GuardInkEmber)
            }

            if (model.has(GuardControl.ALLOWANCE_RAISE_WHEEL)) {
                Spacer(Modifier.height(26.dp))
                Text(GuardCopy.RAISE_TITLE, style = GuardSectionStyle, color = GuardLeaf)
                Spacer(Modifier.height(6.dp))
                // The control itself says so, plainly (D-1).
                Text(stringResource(StrongroomCopy.StandsFromNextDayStart), style = GuardBodyStyle, color = GuardLeaf)
                Spacer(Modifier.height(12.dp))
                var raiseMinutes by remember(snapshot.allowanceMinutes) {
                    mutableStateOf(snapshot.allowanceMinutes + GuardWheel.STEP_MINUTES)
                }
                AllowanceDial(
                    detentValues = remember(snapshot.allowanceMinutes) {
                        (1..36).map { snapshot.allowanceMinutes + it * GuardWheel.STEP_MINUTES }
                    },
                    standingValue = raiseMinutes,
                    onDetentTick = onDetentTick,
                    onSettled = { minutes -> raiseMinutes = minutes },
                    modifier = Modifier.testTag("guard_raise_wheel")
                )
                if (model.has(GuardControl.ALLOWANCE_RAISE_CONFIRM)) {
                    Spacer(Modifier.height(14.dp))
                    GuardPlate(
                        label = stringResource(StrongroomCopy.StandsFromNextDayStart),
                        onClick = { onIntent(GuardIntent.RequestRaise(raiseMinutes)) },
                        modifier = Modifier.testTag("guard_raise_confirm")
                    )
                }
            }

            // Spent: testimony only. There is no control here to
            // grey — the loosening side is beyond the hard stop.
            model.hardStopLine?.let { line ->
                Spacer(Modifier.height(26.dp))
                Text(line, style = GuardBodyStyle, color = GuardInkEmber)
            }

            // --------------------------------------------------
            // THE STANDING — on instant; off only on calm ground.
            // --------------------------------------------------
            Spacer(Modifier.height(34.dp))
            model.pendingOffLine?.let { line ->
                Text(line, style = GuardSecondaryStyle, color = GuardInkEmber)
                Spacer(Modifier.height(12.dp))
            }
            if (model.has(GuardControl.GUARD_TURN_ON)) {
                Text(GuardCopy.ON_LAW, style = GuardBodyStyle, color = GuardLeaf)
                Spacer(Modifier.height(10.dp))
                GuardPlate(
                    label = "Turn the Guard on",
                    onClick = { onIntent(GuardIntent.TurnOn) },
                    modifier = Modifier.testTag("guard_turn_on")
                )
            }
            if (model.has(GuardControl.GUARD_REQUEST_OFF)) {
                Text(stringResource(StrongroomCopy.StandsFromNextDayStart), style = GuardBodyStyle, color = GuardLeaf)
                Spacer(Modifier.height(10.dp))
                GuardPlate(
                    label = "Ask the Guard off — from the next day-start",
                    onClick = { onIntent(GuardIntent.RequestOff) },
                    modifier = Modifier.testTag("guard_request_off")
                )
            }
            // Sealed / stretch: no off plate is composed at all.

            // --------------------------------------------------
            // THE STRETCH — setup outside; inside, only its end.
            // --------------------------------------------------
            Spacer(Modifier.height(34.dp))
            Text("The stretch", style = GuardSectionStyle, color = GuardLeaf)
            Spacer(Modifier.height(8.dp))
            if (model.stretchLine != null) {
                // Inside a stretch this section is a statement,
                // never a control room: end date, and nothing else.
                Text(model.stretchLine, style = GuardBodyStyle, color = GuardLeaf)
            } else {
                Text(stringResource(StrongroomCopy.EndDateIsTheExit), style = GuardBodyStyle, color = GuardLeaf)
                if (model.has(GuardControl.STRETCH_END_STEPPER)) {
                    Spacer(Modifier.height(14.dp))
                    StretchStepper(
                        nowMillis = snapshot.nowMillis,
                        onDetentTick = onDetentTick,
                        onBegin = { endMillis -> onIntent(GuardIntent.BeginStretch(endMillis)) },
                        beginInInventory = model.has(GuardControl.STRETCH_BEGIN_CONFIRM)
                    )
                }
            }

            // --------------------------------------------------
            // THE WAYS OUT — advance-chosen, presented; the valve
            // is told, never spent from this calm surface (D-4).
            // --------------------------------------------------
            Spacer(Modifier.height(34.dp))
            Text(GuardCopy.EXITS_TITLE, style = GuardSectionStyle, color = GuardLeaf)
            Spacer(Modifier.height(8.dp))
            model.exitNames.forEach { name ->
                Spacer(Modifier.height(8.dp))
                Text(name, style = GuardFigureStyle, color = GuardLeaf)
            }
            Spacer(Modifier.height(10.dp))
            if (model.valveLine.isNotEmpty()) {
                Text(model.valveLine, style = GuardSecondaryStyle, color = GuardInkEmber)
            }

            // --------------------------------------------------
            // ROUTINES — settled here, under the Guard (D-9).
            // --------------------------------------------------
            if (model.routineRows.isNotEmpty()) {
                // No collective header (TYPE-SR-1, deck §12.7):
                // the routines stand under their own names only,
                // grouped by place — the law line leads, and each
                // routine's own name is its heading.
                Spacer(Modifier.height(34.dp))
                Text(GuardCopy.ROUTINES_LAW, style = GuardBodyStyle, color = GuardLeaf)
                model.routineRows.forEach { row ->
                    Spacer(Modifier.height(16.dp))
                    Text(row.routine.name, style = GuardFigureStyle, color = GuardLeaf)
                    row.line?.let { line ->
                        Spacer(Modifier.height(4.dp))
                        Text(line, style = GuardSecondaryStyle, color = GuardInkEmber)
                    }
                    Spacer(Modifier.height(8.dp))
                    if (GuardControl.ROUTINE_TURN_ON in row.controls) {
                        GuardPlate(
                            label = "Turn on — stands at once",
                            onClick = {
                                onIntent(GuardIntent.SetRoutineEnabled(row.routine.id, true))
                            },
                            modifier = Modifier.testTag("guard_routine_on_${row.routine.id}")
                        )
                    }
                    if (GuardControl.ROUTINE_TURN_OFF in row.controls) {
                        GuardPlate(
                            label = "Turn off — from the next day-start",
                            onClick = {
                                onIntent(GuardIntent.SetRoutineEnabled(row.routine.id, false))
                            },
                            modifier = Modifier.testTag("guard_routine_off_${row.routine.id}")
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

/**
 * THE WHEEL — a combination dial over a detent list. Dragging
 * turns it by angle around its centre; crossing a detent speaks
 * a tick through [onDetentTick]; the drawn rotation settles onto
 * the resting detent on the craft spring (GuardWheel figures —
 * firm, one overshoot at most). On release the settled value is
 * reported once via [onSettled]. The dial itself owns nothing.
 */
@Composable
private fun AllowanceDial(
    detentValues: List<Int>,
    standingValue: Int,
    onDetentTick: () -> Unit,
    onSettled: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val standingIndex = remember(detentValues, standingValue) {
        val exact = detentValues.indexOf(standingValue)
        if (exact >= 0) exact
        else detentValues.indices.minByOrNull { kotlin.math.abs(detentValues[it] - standingValue) } ?: 0
    }
    var detentIndex by remember(detentValues) { mutableStateOf(standingIndex) }
    var rawRotation by remember(detentValues) {
        mutableStateOf(GuardWheel.rotationForDetent(standingIndex))
    }
    var dialSize by remember { mutableStateOf(IntSize.Zero) }

    val settledRotation by animateFloatAsState(
        targetValue = GuardWheel.rotationForDetent(detentIndex).toFloat(),
        animationSpec = spring(
            dampingRatio = GuardWheel.SETTLE_DAMPING_RATIO,
            stiffness = GuardWheel.SETTLE_STIFFNESS
        ),
        label = "guard_wheel_settle"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(216.dp)
                .onSizeChanged { dialSize = it }
                .pointerInput(detentValues) {
                    detectDragGestures(
                        onDragEnd = { onSettled(detentValues[detentIndex]) },
                        onDragCancel = { onSettled(detentValues[detentIndex]) }
                    ) { change, _ ->
                        if (dialSize == IntSize.Zero) return@detectDragGestures
                        val cx = dialSize.width / 2.0
                        val cy = dialSize.height / 2.0
                        val prev = change.previousPosition
                        val now = change.position
                        val aPrev = atan2(prev.y - cy, prev.x - cx)
                        val aNow = atan2(now.y - cy, now.x - cx)
                        var delta = Math.toDegrees(aNow - aPrev)
                        if (delta > 180.0) delta -= 360.0
                        if (delta < -180.0) delta += 360.0
                        rawRotation = (rawRotation + delta)
                            .coerceIn(
                                GuardWheel.rotationForDetent(0),
                                GuardWheel.rotationForDetent(detentValues.lastIndex)
                            )
                        val next = GuardWheel.detentForRotation(rawRotation)
                            .coerceIn(0, detentValues.lastIndex)
                        if (next != detentIndex) {
                            detentIndex = next
                            onDetentTick()
                        }
                        change.consume()
                    }
                }
                .semantics {
                    contentDescription =
                        "Allowance wheel — ${GuardCopy.minutesText(detentValues[detentIndex])}"
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f - 6.dp.toPx()
                // The dial's brass rim.
                drawCircle(
                    color = GuardLeaf,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.5.dp.toPx())
                )
                drawCircle(
                    color = GuardLeaf.copy(alpha = 0.55f),
                    radius = radius - 10.dp.toPx(),
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                // Detent ticks, turning WITH the dial: twelve to
                // the turn, struck in gold, the standing tick at
                // the top under the fixed marker.
                repeat(24) { i ->
                    val angle = Math.toRadians(
                        (i * (360.0 / 24) + settledRotation % 360.0) - 90.0
                    )
                    val inner = radius - 22.dp.toPx()
                    val outer = radius - 12.dp.toPx()
                    drawLine(
                        color = GuardLeaf.copy(alpha = if (i % 2 == 0) 0.9f else 0.45f),
                        start = Offset(
                            center.x + (kotlin.math.cos(angle) * inner).toFloat(),
                            center.y + (kotlin.math.sin(angle) * inner).toFloat()
                        ),
                        end = Offset(
                            center.x + (kotlin.math.cos(angle) * outer).toFloat(),
                            center.y + (kotlin.math.sin(angle) * outer).toFloat()
                        ),
                        strokeWidth = 2.dp.toPx()
                    )
                }
                // The fixed marker at the top — the dial's reading point.
                drawLine(
                    color = GuardInkPink,
                    start = Offset(center.x, center.y - radius - 4.dp.toPx()),
                    end = Offset(center.x, center.y - radius + 8.dp.toPx()),
                    strokeWidth = 3.dp.toPx()
                )
            }
            Text(
                text = GuardCopy.minutesText(detentValues[detentIndex]),
                style = GuardFigureStyle,
                color = GuardLeaf,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/** Stretch setup's stepper: whole days ahead, one detent a day,
 *  the end struck at that date's day-start (03:30). */
@Composable
private fun StretchStepper(
    nowMillis: Long,
    onDetentTick: () -> Unit,
    onBegin: (Long) -> Unit,
    beginInInventory: Boolean
) {
    val zone = remember { ZoneId.systemDefault() }
    var daysAhead by remember { mutableStateOf(7) }
    val today = remember(nowMillis) {
        Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
    }
    val endDate = today.plusDays(daysAhead.toLong())
    val endMillis = remember(endDate) { GuardSurface.stretchEndMillis(endDate, zone) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        GuardPlate(
            label = "− a day",
            onClick = {
                if (daysAhead > 1) {
                    daysAhead -= 1
                    onDetentTick()
                }
            },
            modifier = Modifier.testTag("guard_stretch_step_down")
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = GuardCopy.dateText(endMillis, zone),
                style = GuardFigureStyle,
                color = GuardLeaf
            )
            Text(
                text = "at day-start, 03:30",
                style = GuardSecondaryStyle,
                color = GuardInkEmber
            )
        }
        Spacer(Modifier.width(14.dp))
        GuardPlate(
            label = "+ a day",
            onClick = {
                daysAhead += 1
                onDetentTick()
            },
            modifier = Modifier.testTag("guard_stretch_step_up")
        )
    }
    if (beginInInventory) {
        Spacer(Modifier.height(14.dp))
        GuardPlate(
            label = "Begin the stretch — its end is its exit",
            onClick = { onBegin(endMillis) },
            modifier = Modifier.testTag("guard_stretch_begin")
        )
    }
}

/** A brass plate: gold-ruled, line-work ground at a shade, the
 *  label in Sora. A press is a plain deliberate touch — the
 *  weight of these acts is carried by WHEN they land (the
 *  model's law), never by press theatrics. */
@Composable
private fun GuardPlate(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .border(width = 1.5.dp, color = GuardLeaf.copy(alpha = 0.85f))
            .background(GuardInkLine.copy(alpha = 0.30f))
            .clickable(interactionSource = source, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(label, style = GuardPlateLabelStyle, color = GuardLeaf)
    }
}

/** The hand-laid pinstripe: a double gold rule inset from the
 *  surface's edge — the kept finish, drawn as strokes. */
@Composable
private fun PinstripeField() {
    Canvas(Modifier.fillMaxSize()) {
        val outerInset = 14.dp.toPx()
        val innerInset = 20.dp.toPx()
        drawRect(
            color = GuardLeaf,
            topLeft = Offset(outerInset, outerInset),
            size = Size(size.width - outerInset * 2, size.height - outerInset * 2),
            style = Stroke(width = 2.dp.toPx())
        )
        drawRect(
            color = GuardLeaf.copy(alpha = 0.55f),
            topLeft = Offset(innerInset, innerInset),
            size = Size(size.width - innerInset * 2, size.height - innerInset * 2),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}
