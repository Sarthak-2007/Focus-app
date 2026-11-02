package com.example.ui.signing

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.foundation.LocalStrongroomLightState
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomMaximum
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * THE SIGNING PRINT BENCH — DESIGN-MOTION's prototype, built in
 * code: the moving-parts-on-a-photograph problem, solved on the
 * signing's own mechanisms.
 *
 * THE PROBLEM. The sealed face's base is Sarthak's picked print —
 * a real vault-door photograph in his seven inks (the same asset
 * LEAD-DOORFIX seats as the opening face,
 * `drawable-nodpi/strongroom_door_print.png`). A photograph's
 * wheel cannot spin and its dial cannot turn. The signing is
 * exactly where both must move: the dial sets the lifting hour,
 * the great wheel is worked by hand for the signature.
 *
 * THE APPROACH. Split the face into a STILL WORLD and CRAFTED
 * MECHANISMS. The print carries everything that does not move —
 * the bolted outer ring, the door's mass, its light — at full
 * photographic richness. Over the print's own stations sit two
 * mechanisms built as machined objects, not drawings:
 *
 *  · Each mechanism stands on a BACKPLATE — a steel disc grained
 *    cell-by-cell from its own ink ([SigningTexture] STEEL, the
 *    signing table's own grain law) that occludes the photograph's
 *    static hub cleanly, the way a real mechanism is seated into
 *    a door on a machined plate.
 *  · Each mechanism CASTS A SHADOW onto the print before it is
 *    drawn — three layered graphite discs, offset along the
 *    signing light's fall (upper-left lamp, shadow lower-right),
 *    so the object sits IN the photograph's light, not pasted
 *    over it. Its rim carries the same light: a lit arc on the
 *    lamp side ([StrongroomLight.relief] toward the violet lit
 *    partner), a shadow arc opposite, both quantised to the
 *    palette's whole eighths — no colour exists here that the
 *    print does not already own.
 *
 * THE MOTION, in explicit vocabulary — no adjectives:
 *  · THE WHEEL: hand travel is clamped to the governed ceiling
 *    ([StrongroomMaximum.SPIN_MAX_VELOCITY_DEG_PER_SEC], 240°/s —
 *    a dial that cannot be flicked). Seat attempts ride the
 *    standing law ([CombinationSpin]: lawful direction, arrival
 *    at or below the 120°/s seat ceiling, within the approach
 *    arc). A taken seat SETTLES: the wheel springs to the station
 *    angle on a detent spring (stiffness 300, damping ratio 0.62 —
 *    a catch with one felt overshoot, then rest). A FLUNG release
 *    (over the seat ceiling) seats nothing and DECAYS: exponential
 *    decay, friction multiplier 1.8/s, until |velocity| < 3°/s
 *    (the settle ε), where the wheel simply stops — a heavy wheel
 *    dying by friction, never snapping.
 *  · THE DIAL: the hand follows the finger raw; on release the
 *    rail catches it ([SigningDial.settle] — quarters within the
 *    snap window) and an underdamped spring (stiffness 220,
 *    damping ratio 0.50) carries it into the detent — the visible
 *    overshoot-and-seat of a real detent taking the hand.
 *
 * THE LIMIT, named plainly in the return: the mechanisms are
 * drawn metal, not photographs of metal. At rest, seated in
 * shadow and grain on the print, they hold the photo's world;
 * the seam is the light — the print's light is baked at one
 * instant, the mechanisms' relief is computed from the palette,
 * and an eye that hunts for it can find the join at the backplate's
 * edge.
 *
 * THE SWAP (Sarthak's verdict, 2026-11-03: the print-as-face
 * PASSED — "not toy" — and his demand is that it WORK and MOVE
 * as a full app, not stand as a still): this bench now stands
 * IN [Signing]'s column where the drawn dial and drawn wheel
 * stood — both drawn mechanisms are deleted, and the dial and
 * the wheel live here, on the door's own plate, in the order
 * the signature itself works them: the hour is set on this
 * plate's dial as the table's amendments reach it, and the
 * signature is spun on this plate's wheel. The bench's actions
 * are the signing's own ([SigningActions.onAmend] for the hour,
 * [SigningActions.onSignature] at the third seat) — the swap
 * changes where the mechanisms live, never what they command.
 *
 * The bench keeps the standing law whole: the combination is
 * never a value anywhere (the spin counts seats only, [SigningDial]
 * reads the hour) — A-2 stands exactly as [Signing] keeps it.
 */

/** A still pose of the bench, for renders and the harness: the
 *  wheel's angle, its motion-ghost trail, the dial hand's raw
 *  minute (between detents = mid-turn), the seats standing. In
 *  the living surface the pose is the default — at rest. */
data class PrintBenchPose(
    val wheelDeg: Float = 0f,
    val wheelGhostDeg: Float = 0f,
    val dialHandMinute: Int? = null,
    val seatsTaken: Int = 0
)

@Composable
fun SigningPrintBench(
    state: SigningState,
    actions: SigningActions,
    modifier: Modifier = Modifier,
    pose: PrintBenchPose = PrintBenchPose()
) {
    val lightState = LocalStrongroomLightState.current
    val spin = remember { CombinationSpin() }
    var seats by remember { mutableStateOf(pose.seatsTaken) }
    LaunchedEffect(seats) {
        if (spin.isComplete && !state.signed) actions.onSignature()
    }
    val seatsShown = if (pose.seatsTaken > seats) pose.seatsTaken else seats
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StrongroomRoles.Field)
            .semantics { contentDescription = "ULYSSES — the signing, on the print" }
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            val w = maxWidth
            // THE STILL WORLD — the print itself, square-cropped
            // on the wheel as the icon crop stands: hub at
            // (0.50, 0.41) of the panel, the bolted ring at
            // radius ~0.46 of the panel's width left visible as
            // the door around the crafted wheel.
            Image(
                painter = painterResource(R.drawable.strongroom_door_print),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // THE GREAT WHEEL — crafted, over the print's hub.
            PrintBenchWheel(
                spin = spin,
                pose = pose,
                lightState = lightState,
                onSeatTaken = { seats = spin.seatsTaken },
                modifier = Modifier
                    .offset(x = w * 0.10f, y = w * 0.01f)
                    .size(w * 0.80f)
            )
            // THE DIAL — crafted, mounted on the door's
            // upper-right boltwork.
            PrintBenchDial(
                state = state,
                actions = actions,
                pose = pose,
                lightState = lightState,
                modifier = Modifier
                    .offset(x = w * 0.62f, y = w * 0.05f)
                    .size(w * 0.34f)
            )
        }
        // The seats taken, as three bolt heads filling — the
        // signature's progress is furniture, not a counter
        // (the standing form from the drawn wheel, kept).
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp)
        ) {
            for (i in 0 until 3) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .size(13.dp)
                        .background(
                            if (i < seatsShown) StrongroomInks.Gold
                            else StrongroomLight.descend(StrongroomInks.Graphite, 1),
                            CircleShape
                        )
                )
            }
        }
    }
}

// ── The crafted great wheel ─────────────────────────────────────

@Composable
private fun PrintBenchWheel(
    spin: CombinationSpin,
    pose: PrintBenchPose,
    lightState: StrongroomLightState,
    onSeatTaken: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val wheel = remember { Animatable(pose.wheelDeg) }
    var handVelocity by remember { mutableStateOf(0.0) }
    Box(
        modifier = modifier.pointerInput(Unit) {
            var lastAngle: Double? = null
            var lastMs: Long = 0L
            detectDragGestures(
                onDragStart = { lastAngle = null },
                onDragEnd = {
                    val v = handVelocity
                    val clockwise = v > 0.0
                    if (spin.attemptSeat(wheel.value.toDouble(), v, clockwise)) {
                        onSeatTaken()
                        // THE DETENT SETTLE: spring to the station.
                        val station = spin.stationAt(wheel.value.toDouble()) *
                            StrongroomMaximum.SPIN_STATION_SPACING_DEG
                        var target = station
                        // Settle to the station the hand arrived at,
                        // on the near side of the circle.
                        while (target - wheel.value > 180.0) target -= 360.0
                        while (wheel.value - target > 180.0) target += 360.0
                        scope.launch {
                            wheel.animateTo(
                                target.toFloat(),
                                spring(stiffness = 300f, dampingRatio = 0.62f)
                            )
                        }
                    } else if (
                        kotlin.math.abs(v) >
                        StrongroomMaximum.SEAT_MAX_VELOCITY_DEG_PER_SEC
                    ) {
                        // THE FLUNG PASS: seats nothing; the wheel
                        // dies by friction (exponential decay,
                        // friction 1.8/s, settle ε in the decay's
                        // own tail).
                        scope.launch {
                            wheel.animateDecay(
                                v.toFloat(),
                                exponentialDecay(frictionMultiplier = 1.8f)
                            )
                        }
                    }
                },
                onDrag = { change, _ ->
                    val cx = size.width / 2.0
                    val cy = size.height / 2.0
                    val angle = Math.toDegrees(
                        atan2(change.position.y - cy, change.position.x - cx)
                    )
                    val prev = lastAngle
                    if (prev != null) {
                        var delta = angle - prev
                        if (delta > 180.0) delta -= 360.0
                        if (delta < -180.0) delta += 360.0
                        val dt = ((change.uptimeMillis - lastMs)
                            .coerceAtLeast(1)) / 1000.0
                        // The governed ceiling: 240°/s, filed.
                        var velocity = delta / dt
                        val ceiling = StrongroomMaximum.SPIN_MAX_VELOCITY_DEG_PER_SEC
                        if (velocity > ceiling) velocity = ceiling
                        if (velocity < -ceiling) velocity = -ceiling
                        handVelocity = velocity
                        val next = wheel.value + (velocity * dt).toFloat()
                        scope.launch { wheel.snapTo(next) }
                    }
                    lastAngle = angle
                    lastMs = change.uptimeMillis
                    change.consume()
                }
            )
        }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f * 0.79f
            drawCastShadow(c, r)
            drawBackplate(c, r, seed = 7, lightState = lightState)
            // The motion ghosts: the wheel a beat behind itself —
            // drawn only in a mid-turn pose, as trailing spokes
            // at dying alpha.
            if (pose.wheelGhostDeg > 0f) {
                for (g in 2 downTo 1) {
                    drawWheelBody(
                        c, r,
                        rotationDeg = wheel.value - g * pose.wheelGhostDeg,
                        lightState = lightState,
                        alpha = 0.07f * (3 - g)
                    )
                }
            }
            drawWheelBody(c, r, wheel.value, lightState, alpha = 1f)
        }
        // The station under the hand — one digit, read at the
        // hub; the combination itself is never shown whole,
        // never stored, never written (A-2).
        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${spin.stationAt(wheel.value.toDouble())}",
                style = StrongroomType.Anon4,
                color = StrongroomRoles.OnGround
            )
        }
    }
}

// ── The crafted dial ────────────────────────────────────────────

@Composable
private fun PrintBenchDial(
    state: SigningState,
    actions: SigningActions,
    pose: PrintBenchPose,
    lightState: StrongroomLightState,
    modifier: Modifier = Modifier
) {
    val record = state.day.record
    val scope = rememberCoroutineScope()
    val startMinute = pose.dialHandMinute
        ?: SigningDial.minuteOfDay(record.liftingTime)
    val dial = remember(record.liftingTime, pose.dialHandMinute) {
        Animatable(SigningDial.angleFor(startMinute).toFloat())
    }
    val pm = record.liftingTime.hour >= 12
    Box(
        modifier = modifier.pointerInput(record.liftingTime) {
            var lastAngle: Double? = null
            detectDragGestures(
                onDragStart = { lastAngle = null },
                onDragEnd = {
                    // THE RAIL'S CATCH + THE DETENT SPRING: settle
                    // reads the placed minute off the rail; the
                    // spring (stiffness 220, damping ratio 0.50 —
                    // underdamped: one overshoot into the detent)
                    // carries the hand there.
                    val (placed, _) = SigningDial.settle(
                        SigningDial.minuteFor(dial.value.toDouble()), pm
                    )
                    val target = SigningDial.angleFor(
                        placed % SigningDial.CIRCLE_MINUTES
                    ).toFloat()
                    scope.launch {
                        var t = target
                        while (t - dial.value > 180f) t -= 360f
                        while (dial.value - t > 180f) t += 360f
                        dial.animateTo(
                            t,
                            spring(stiffness = 220f, dampingRatio = 0.50f)
                        )
                    }
                    actions.onAmend(
                        com.example.domain.StrongroomComposition.SigningAmendment(
                            liftingTime = SigningDial.timeFor(placed)
                        )
                    )
                },
                onDragCancel = { lastAngle = null },
                onDrag = { change, _ ->
                    val cx = size.width / 2.0
                    val cy = size.height / 2.0
                    val angle = Math.toDegrees(
                        atan2(change.position.y - cy, change.position.x - cx)
                    ) + 90.0
                    val prev = lastAngle
                    if (prev != null) {
                        var delta = angle - prev
                        if (delta > 180.0) delta -= 360.0
                        if (delta < -180.0) delta += 360.0
                        scope.launch { dial.snapTo(dial.value + delta.toFloat()) }
                    }
                    lastAngle = angle
                    change.consume()
                }
            )
        }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f * 0.80f
            drawCastShadow(c, r)
            drawBackplate(c, r, seed = 19, lightState = lightState)
            // The enamelled face.
            val face = StrongroomLight.stateSurface(
                StrongroomInks.Violet, lightState,
                com.example.ui.foundation.StrongroomLitSurface.FACE
            )
            drawCircle(color = face, radius = r * 0.84f, center = c)
            drawGrainDisc(c, r * 0.84f, face, seed = 23, lightState = lightState)
            // Detent stones: quarters small, whole hours firm —
            // the hour train's topography, as the drawn dial
            // carries it (amplitudes 55 / 85 in the foundation).
            for (step in 0 until 48) {
                val minute = step * 15
                val whole = minute % 60 == 0
                val a = Math.toRadians(
                    SigningDial.angleFor(minute) - 90.0
                ).toFloat()
                val r1 = r * 0.84f - if (whole) r * 0.20f else r * 0.14f
                val r2 = r * 0.84f - r * 0.05f
                drawLine(
                    color = if (whole) StrongroomInks.Gold
                    else StrongroomLight.descend(StrongroomInks.Gold, 3),
                    start = Offset(c.x + r1 * cos(a), c.y + r1 * sin(a)),
                    end = Offset(c.x + r2 * cos(a), c.y + r2 * sin(a)),
                    strokeWidth = if (whole) 2.5.dp.toPx() else 1.2.dp.toPx()
                )
            }
            // THE VERNIER APERTURE at the index (top), as the
            // drawn dial carries it: a graphite window, the
            // detent enlarged under a magenta hairline.
            val winW = r * 1.05f
            val winH = r * 0.34f
            val winX = c.x - winW / 2f
            val winY = c.y - r * 0.84f + 1.dp.toPx()
            drawRect(
                color = StrongroomInks.Graphite,
                topLeft = Offset(winX, winY),
                size = Size(winW, winH)
            )
            for (k in -3..3) {
                val x = c.x + k * (winW / 8f)
                val tall = k == 0
                drawLine(
                    color = if (tall) StrongroomInks.Gold
                    else StrongroomLight.descend(StrongroomInks.Gold, 2),
                    start = Offset(x, winY + winH - if (tall) winH * 0.55f else winH * 0.34f),
                    end = Offset(x, winY + winH - 2.dp.toPx()),
                    strokeWidth = if (tall) 2.dp.toPx() else 1.2.dp.toPx()
                )
            }
            drawLine(
                color = StrongroomInks.Magenta,
                start = Offset(c.x, winY + 1.dp.toPx()),
                end = Offset(c.x, winY + winH - 1.dp.toPx()),
                strokeWidth = 1.2.dp.toPx()
            )
            // The hand, at its raw angle — settled on a detent
            // at rest, between detents mid-turn.
            val ha = Math.toRadians(dial.value.toDouble() - 90.0).toFloat()
            drawLine(
                color = StrongroomInks.Gold,
                start = c,
                end = Offset(
                    c.x + r * 0.60f * cos(ha),
                    c.y + r * 0.60f * sin(ha)
                ),
                strokeWidth = 3.dp.toPx()
            )
            drawCircle(color = StrongroomInks.Gold, radius = 4.5.dp.toPx(), center = c)
        }
    }
}

// ── The seating: shadow, backplate, grain ───────────────────────

/** The mechanism's cast shadow on the print: three layered
 *  graphite discs dying outward, offset along the signing
 *  light's fall — the lamp stands upper-left, so the shadow
 *  pools lower-right and HUGS the object (contact shadow,
 *  not a drop shadow at a distance). */
private fun DrawScope.drawCastShadow(center: Offset, radius: Float) {
    val layers = listOf(
        Triple(1.03f, 0.030f, 0.20f),
        Triple(1.07f, 0.055f, 0.12f),
        Triple(1.12f, 0.085f, 0.06f)
    )
    for ((scale, off, alpha) in layers) {
        drawCircle(
            color = StrongroomInks.Graphite.copy(alpha = alpha),
            radius = radius * scale,
            center = Offset(
                center.x + radius * off,
                center.y + radius * off * 1.4f
            )
        )
    }
}

/** The machined backplate a mechanism stands on: a steel disc
 *  grained from its own ink, ringed in metal — it occludes the
 *  photograph's static furniture where the mechanism seats, so
 *  nothing of the photo's frozen wheel turns underneath the
 *  crafted one. */
private fun DrawScope.drawBackplate(
    center: Offset,
    radius: Float,
    seed: Int,
    lightState: StrongroomLightState
) {
    val plate = StrongroomLight.blendEighth(
        StrongroomInks.Graphite, StrongroomInks.Violet, 2
    )
    drawCircle(color = plate, radius = radius, center = center)
    drawGrainDisc(center, radius, plate, seed, lightState)
    drawCircle(
        color = StrongroomRoles.Metal,
        radius = radius - 1.5.dp.toPx(),
        center = center,
        style = Stroke(width = 3.dp.toPx())
    )
    drawCircle(
        color = StrongroomLight.relief(
            StrongroomInks.Graphite, StrongroomInks.Violet, 0.8f, lightState
        ),
        radius = radius - 4.dp.toPx(),
        center = center,
        style = Stroke(width = 1.2.dp.toPx())
    )
}

/** Grain over a disc: the signing table's own STEEL grain
 *  ([SigningTexture.cellTone]) clipped to the disc — every cell
 *  a whole-eighth relief of the disc's own ink, deterministic
 *  in position, so the metal is the same metal every frame. */
private fun DrawScope.drawGrainDisc(
    center: Offset,
    radius: Float,
    ink: Color,
    seed: Int,
    lightState: StrongroomLightState
) {
    val cell = SigningTexture.Species.STEEL.cellPx.dp.toPx()
    val left = center.x - radius
    val top = center.y - radius
    clipRect(left, top, center.x + radius, center.y + radius) {
        var cy = 0
        var yy = top
        while (yy < center.y + radius) {
            var cx = 0
            var xx = left
            while (xx < center.x + radius) {
                val dx = xx + cell / 2f - center.x
                val dy = yy + cell / 2f - center.y
                if (dx * dx + dy * dy <= radius * radius) {
                    val tone = SigningTexture.cellTone(
                        ink, StrongroomInks.Violet,
                        SigningTexture.Species.STEEL, cx, cy,
                        seed = seed, state = lightState
                    )
                    drawRect(
                        color = tone,
                        topLeft = Offset(xx, yy),
                        size = Size(cell + 1f, cell + 1f)
                    )
                }
                xx += cell; cx++
            }
            yy += cell; cy++
        }
    }
}

// ── The wheel's body ────────────────────────────────────────────

private fun DrawScope.drawWheelBody(
    center: Offset,
    radius: Float,
    rotationDeg: Float,
    lightState: StrongroomLightState,
    alpha: Float
) {
    val metal = StrongroomRoles.Metal.copy(alpha = alpha)
    val lit = StrongroomLight.relief(
        StrongroomInks.Graphite, StrongroomInks.Violet, 0.75f, lightState
    ).copy(alpha = alpha)
    val shadow = StrongroomLight.relief(
        StrongroomInks.Graphite, StrongroomInks.Violet, -0.75f, lightState
    ).copy(alpha = alpha)
    // The rim: a metal ring, its lit arc on the lamp side, its
    // shadow arc opposite — the light does not rotate with the
    // wheel; the wheel turns UNDER the room's light.
    drawCircle(
        color = metal, radius = radius * 0.97f, center = center,
        style = Stroke(width = radius * 0.13f)
    )
    drawArc(
        color = lit, startAngle = 180f, sweepAngle = 140f, useCenter = false,
        topLeft = Offset(center.x - radius * 0.97f, center.y - radius * 0.97f),
        size = Size(radius * 1.94f, radius * 1.94f),
        style = Stroke(width = radius * 0.05f)
    )
    drawArc(
        color = shadow, startAngle = 0f, sweepAngle = 140f, useCenter = false,
        topLeft = Offset(center.x - radius * 0.97f, center.y - radius * 0.97f),
        size = Size(radius * 1.94f, radius * 1.94f),
        style = Stroke(width = radius * 0.05f)
    )
    // The gold pinstripe, laid inside the rim.
    drawCircle(
        color = StrongroomInks.Gold.copy(alpha = alpha * 0.9f),
        radius = radius * 0.86f, center = center,
        style = Stroke(width = 1.6.dp.toPx())
    )
    withTransform({ rotate(rotationDeg, center) }) {
        // Three spokes, each with its lit edge drawn toward the
        // lamp — the edge is a second, thinner line offset
        // against the light direction, so a turning spoke
        // carries its highlight around with it.
        for (spoke in 0 until 3) {
            val a = Math.toRadians(spoke * 120.0 - 90.0).toFloat()
            val end = Offset(
                center.x + radius * 0.88f * cos(a),
                center.y + radius * 0.88f * sin(a)
            )
            drawLine(
                color = metal, start = center, end = end,
                strokeWidth = radius * 0.10f
            )
            val px = cos(a + Math.PI.toFloat() / 2f) * radius * 0.028f
            val py = sin(a + Math.PI.toFloat() / 2f) * radius * 0.028f
            drawLine(
                color = lit,
                start = Offset(center.x + px, center.y + py),
                end = Offset(end.x + px, end.y + py),
                strokeWidth = radius * 0.022f
            )
        }
        // The hub: gold, with a graphite heart.
        drawCircle(
            color = StrongroomInks.Gold.copy(alpha = alpha),
            radius = radius * 0.16f, center = center
        )
        drawCircle(
            color = StrongroomInks.Graphite.copy(alpha = alpha),
            radius = radius * 0.06f, center = center
        )
    }
}
