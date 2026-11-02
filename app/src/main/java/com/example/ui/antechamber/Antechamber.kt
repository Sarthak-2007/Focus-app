package com.example.ui.antechamber

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.domain.StrongroomDay
import com.example.ui.foundation.BodyIntegrator
import com.example.ui.foundation.BodyState
import com.example.ui.foundation.LocalStrongroomLightState
import com.example.ui.foundation.MechanismBodies
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * C-08 · THE ANTECHAMBER — the room behind the grille, where the
 * day is actually lived inside the seal. Authored by BUILD-SR-38
 * from the rebuild element census (INVENTORY-SR-1), firsthand.
 *
 * The room is OBJECTS in B-9's order and no other (the pure
 * derivation stands in [AntechamberModel]; this file renders it):
 *
 *  (1) CALLING OUT, first and biggest — a brass calling horn: a
 *      known contact rings through natively and a call out is
 *      placed from inside the seal, as decided (C-15). The room
 *      stages no part of a call and writes no word over one; the
 *      horn carries none.
 *  (2) THE TOKENS + THE REVOLVING DRUM (C-09) — the allowed apps
 *      as physical tokens standing on a revolving brass drum, in
 *      signed order, each bearing the phone's own name exactly as
 *      signed. The hand turns the drum (its crank, or the drum
 *      itself); the detents catch it station by station on the
 *      foundation's drum body (ζ 0.78 — under-damped, one detent
 *      overshoot, caught); pressing the hub TAKES the turned app
 *      for its minute — the spend computed by the domain purse
 *      behind the model's TURNED outcome and persisted by the
 *      host through the kept store (Law R-1: the turned name
 *      resolves only through the drum stock; spend persists, the
 *      purse never resurrects). A taking is authorised drum
 *      spending: it strikes no hole on the plate (TX-SHOW-11).
 *  (3) THE HELD PILE (C-14), behind a second step of its own —
 *      a physical pile of slips in the room's corner that GROWS
 *      as arrivals land, its one quiet dot standing identical for
 *      one arrival or nine (S4-C27). On the second step each slip
 *      stands shown whole — a number as it arrived, a code as
 *      digits alone with the hour, its digits under a brass cover
 *      until his TAKE lifts it (the showing rides the drum: one
 *      minute from the same purse, B-18) — and he chooses per
 *      slip, in the exact words: take it / leave it.
 *  (4) SPENT THINGS GONE — when the purse reaches 00:00 the
 *      BRASS SHUTTER closes over the drum (intake §3 APPLY): the
 *      tokens are not drawn at all behind it — gone, never
 *      dimmed — and the drum's crank strains 4.0° against the
 *      filed hard stop and returns, its refusal spent once per
 *      approach (the foundation's HardStopAttempt).
 *  (5) Nothing else is behind the door. There is no grid of app
 *      icons, no list, no label-headed section anywhere in this
 *      room: a screen of icons with labels is the mediocre form
 *      of this room and does not exist here.
 *
 * On the room's wall hangs THE GOVERNOR'S CRANK (C-04, seated in
 * this slice by route book R-3): one heavy crank under its lead
 * crimp seal, whole. Winding it a full turn spends the week's
 * emergency minute (the host persists the spend through the
 * store's crank verb and the act returns him to the sealed face,
 * where the door's crack is the seal's own business); the snapped
 * seal drops into the TRAY (C-19) and is kept there. Once spent,
 * the wall holds an empty hook — a second taking in the week is
 * refused by the empty wall, and its absence is the record.
 *
 * The room's door is THE GRILLE (C-08): a small steel grille in
 * the room's flank, at the room's head. Touching it is the second
 * touch reversed — he steps back to the sealed face. The grille
 * is a mechanism, not a navigation control: there is no rail, no
 * tab, no back stack in this room (census GAP-R01).
 *
 * Pre-seal honesty (the shell's routing table): this surface is
 * composed only inside a standing seal, and the host renders
 * nothing when no sealed day stands — the room does not exist
 * before the swing, and no dead route leads here.
 *
 * Texture (intake §1): grille steel, drum brass, seal lead carry
 * real grain derived from the surface itself
 * ([AntechamberTexture]); no flat fill carries a material
 * surface, and no image exists in this file. Motion is physics
 * vocabulary only: the drum is caught by its detent rail and
 * settles on the foundation's drum body, integrated live; the
 * crank's strain is the filed hard stop's. All felt figures
 * UNPROVEN-ON-DEVICE, as filed.
 */

/** The room's standing facts, supplied by the host from the kept
 *  engine (the UI issues commands, never sets state). */
data class AntechamberState(
    /** The sealed day's signing, exactly as signed. */
    val signing: StrongroomDay.SigningRecord,
    /** The room derived whole from the standing facts. */
    val room: AntechamberRoom,
    /** Token faces: labels for the drum stock, the phone's own
     *  names. A stock name with no label here renders its package
     *  name — the phone's own identifier, minted by no one. */
    val tokens: List<AntechamberToken>,
    /** Held codes whose cover his TAKE has already lifted (the
     *  showing spent, the digits standing shown). */
    val revealedCodeIds: Set<Int> = emptySet()
)

/** The room's commands. Every spend is the host's, through the
 *  kept store's verbs; this surface computes outcomes, it never
 *  persists. */
data class AntechamberActions(
    /** Take the turned app for its minute: the host spends one
     *  minute through the store and, only on a standing spend,
     *  launches the app. At the hard stop the host spends nothing
     *  and launches nothing. */
    val onTakeTurned: (packageName: String) -> Unit,
    /** Lift a held code's cover: the showing — one minute from
     *  the same purse, a SHOWING punch struck by the host. At the
     *  hard stop the cover stays on. */
    val onShowCode: (slip: HeldSlip) -> Unit,
    /** Discharge a held slip by his choice: taken (used) or left
     *  (complete — no escalation, no count, no debt). */
    val onResolveHeld: (slip: HeldSlip, taken: Boolean) -> Unit,
    /** Take a held call: connects THIS call only (the host opens
     *  the phone's own dialler on the number) and discharges the
     *  slip as taken. It is not an unlock and grants nothing else. */
    val onTakeCall: (slip: HeldSlip) -> Unit,
    /** Wind the governor's crank: the host spends the week's
     *  minute through the store; the snapped seal is kept. */
    val onGovernorCrank: () -> Unit,
    /** Call out from inside the seal: the phone's own dialler,
     *  unstaged by the room. */
    val onCallOut: () -> Unit,
    /** Step back through the grille to the sealed face. */
    val onReturnToFace: () -> Unit
)

private enum class RoomStep { ROOM, PILE }

@Composable
fun Antechamber(
    state: AntechamberState,
    actions: AntechamberActions,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(RoomStep.ROOM) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StrongroomRoles.Field)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .semantics { contentDescription = "ULYSSES" }
    ) {
        when (step) {
            RoomStep.ROOM -> {
                GrilleDoor(onReturn = actions.onReturnToFace)
                Spacer(Modifier.height(20.dp))
                CallingHorn(onCallOut = actions.onCallOut)
                Spacer(Modifier.height(24.dp))
                DrumAndTokens(state, actions)
                Spacer(Modifier.height(24.dp))
                GovernorsWall(state, actions)
                Spacer(Modifier.height(24.dp))
                HeldPileCorner(
                    state = state,
                    onOpen = { step = RoomStep.PILE }
                )
                Spacer(Modifier.height(16.dp))
            }
            RoomStep.PILE -> {
                GrilleDoor(onReturn = { step = RoomStep.ROOM })
                Spacer(Modifier.height(20.dp))
                HeldPileSlips(state, actions)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ── The grille door (C-08) ──────────────────────────────────────

@Composable
private fun GrilleDoor(onReturn: () -> Unit) {
    val lightState = LocalStrongroomLightState.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .background(
                StrongroomLight.descend(StrongroomInks.Violet, 3),
                RoundedCornerShape(4.dp)
            )
            .border(2.dp, StrongroomRoles.Metal, RoundedCornerShape(4.dp))
            .clickable { onReturn() }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val bars = 9
            val cell = AntechamberTexture.Species.STEEL.cellPx.dp.toPx()
            for (b in 0 until bars) {
                val x = size.width * (b + 0.5f) / bars
                var yy = 0f
                var cy = 0
                while (yy < size.height) {
                    val tone = AntechamberTexture.cellTone(
                        StrongroomInks.Graphite, StrongroomInks.Violet,
                        AntechamberTexture.Species.STEEL, b, cy, seed = 23,
                        state = lightState
                    )
                    drawRect(
                        color = tone,
                        topLeft = Offset(x - 3.dp.toPx(), yy),
                        size = Size(6.dp.toPx(), cell + 1f)
                    )
                    yy += cell; cy++
                }
            }
            drawLine(
                color = StrongroomRoles.Metal,
                start = Offset(0f, size.height / 2f),
                end = Offset(size.width, size.height / 2f),
                strokeWidth = 5.dp.toPx()
            )
        }
    }
}

// ── Calling out (C-15) — first and biggest ──────────────────────

@Composable
private fun CallingHorn(onCallOut: () -> Unit) {
    val lightState = LocalStrongroomLightState.current
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(136.dp)
                .clip(CircleShape)
                .clickable { onCallOut() }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val outer = size.minDimension / 2f
                val cell = AntechamberTexture.Species.BRASS.cellPx.dp.toPx()
                var yy = 0f
                var row = 0
                while (yy < size.height) {
                    var xx = 0f
                    var col = 0
                    while (xx < size.width) {
                        val tone = AntechamberTexture.cellTone(
                            StrongroomInks.Gold, StrongroomInks.Graphite,
                            AntechamberTexture.Species.BRASS, col, row,
                            seed = 7, state = lightState
                        )
                        drawRect(
                            color = tone,
                            topLeft = Offset(xx, yy),
                            size = Size(cell + 1f, cell + 1f)
                        )
                        xx += cell; col++
                    }
                    yy += cell; row++
                }
                // The horn's throat rings: brass deepening to the
                // graphite throat — a speaking mouth, not a button.
                drawCircle(
                    color = StrongroomLight.descend(StrongroomInks.Gold, 3),
                    radius = outer * 0.62f
                )
                drawCircle(
                    color = StrongroomInks.Graphite,
                    radius = outer * 0.38f
                )
                drawCircle(
                    color = StrongroomLight.descend(StrongroomInks.Graphite, 2),
                    radius = outer * 0.38f,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}

// ── The drum + its tokens + the shutter (C-09 · C-08(2) · B-9(4)) ──

@Composable
private fun DrumAndTokens(state: AntechamberState, actions: AntechamberActions) {
    val lightState = LocalStrongroomLightState.current
    val room = state.room
    val stock = room.drumStock
    val labels = remember(state.tokens) { state.tokens.associate { it.packageName to it.label } }

    var rotation by remember(stock, room.shutterClosed) { mutableStateOf(0.0) }
    var settleTarget by remember { mutableStateOf<Double?>(null) }
    // The spent drum's hard-stop attempt: one refusal per
    // approach, re-armed only when the hand leaves the approach
    // (a release ends the approach; the next approach is a new
    // attempt — the foundation's HardStopAttempt, verbatim law).
    var stopAttempt by remember(room.shutterClosed) {
        mutableStateOf(
            if (room.shutterClosed) RevolvingDrum.spentStopAttempt() else null
        )
    }
    var restRotation by remember(stock) { mutableStateOf(0.0) }

    // The settle: the released drum is caught by its detent rail
    // and lands on the foundation's drum body, integrated live at
    // the integrator's own substep — poses are the body's, never
    // an easing curve's.
    LaunchedEffect(settleTarget) {
        val target = settleTarget ?: return@LaunchedEffect
        var body = BodyState(rotation, 0.0)
        var lastMs = -1L
        while (!BodyIntegrator.isSettled(body, target, MechanismBodies.Drum)) {
            val frameMs = withFrameMillis { it }
            if (lastMs >= 0L) {
                val dt = ((frameMs - lastMs).coerceIn(0L, 50L)) / 1000.0
                if (dt > 0.0) {
                    body = BodyIntegrator.step(body, target, MechanismBodies.Drum, dt)
                    rotation = body.value
                }
            }
            lastMs = frameMs
        }
        rotation = target
        restRotation = target
        settleTarget = null
    }

    val turnedIndex = if (stock.isEmpty()) -1
    else RevolvingDrum.stationAt(rotation, stock.size)
    val turnedPackage = stock.getOrNull(turnedIndex)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // The drum itself.
        Box(
            modifier = Modifier
                .size(228.dp)
                .clip(CircleShape)
                .pointerInput(stock, room.shutterClosed) {
                    var lastAngle: Double? = null
                    detectDragGestures(
                        onDragStart = { lastAngle = null; settleTarget = null },
                        onDragEnd = {
                            if (room.shutterClosed) {
                                settleTarget = restRotation
                                stopAttempt = RevolvingDrum.spentStopAttempt()
                            } else if (stock.isNotEmpty()) {
                                val idx = RevolvingDrum.stationAt(rotation, stock.size)
                                settleTarget = RevolvingDrum.settleTarget(rotation, idx, stock.size)
                            }
                        },
                        onDragCancel = { lastAngle = null },
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
                                rotation = if (room.shutterClosed) {
                                    // The filed hard stop: the crank
                                    // strains 4.0° against the stop —
                                    // and no further (foundation
                                    // SPENT_STOP_TRAVEL_DEG). The
                                    // refusal fires once per approach
                                    // inside the attempt; its felt
                                    // event is the registry's own row.
                                    val strained = (rotation + delta).coerceIn(
                                        restRotation - 4.0, restRotation + 4.0
                                    )
                                    stopAttempt?.onApproach(
                                        kotlin.math.abs(strained - restRotation) - 4.0
                                    )
                                    strained
                                } else {
                                    rotation + delta
                                }
                            }
                            lastAngle = angle
                            change.consume()
                        }
                    )
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val outer = size.minDimension / 2f
                // The drum's brass body, grained.
                val cell = AntechamberTexture.Species.BRASS.cellPx.dp.toPx()
                var yy = 0f
                var rowI = 0
                while (yy < size.height) {
                    var xx = 0f
                    var colI = 0
                    while (xx < size.width) {
                        val tone = AntechamberTexture.cellTone(
                            StrongroomLight.descend(StrongroomInks.Gold, 1),
                            StrongroomInks.Graphite,
                            AntechamberTexture.Species.BRASS, colI, rowI,
                            seed = 31, state = lightState
                        )
                        drawRect(
                            color = tone,
                            topLeft = Offset(xx, yy),
                            size = Size(cell + 1f, cell + 1f)
                        )
                        xx += cell; colI++
                    }
                    yy += cell; rowI++
                }
                drawCircle(
                    color = StrongroomRoles.Metal,
                    radius = outer - 3.dp.toPx(),
                    style = Stroke(width = 5.dp.toPx())
                )
                if (room.shutterClosed) {
                    // THE BRASS SHUTTER, closed over the metered
                    // apps: slat over slat, the drum's own brass —
                    // behind it the tokens are GONE (they are not
                    // drawn at all below), never dimmed.
                    val slats = 7
                    for (s in 0 until slats) {
                        val y = size.height * s / slats
                        val tone = AntechamberTexture.cellTone(
                            StrongroomInks.Gold, StrongroomInks.Graphite,
                            AntechamberTexture.Species.BRASS, s, 3,
                            seed = 53, state = lightState
                        )
                        drawRect(
                            color = tone,
                            topLeft = Offset(0f, y),
                            size = Size(size.width, size.height / slats + 1f)
                        )
                        drawLine(
                            color = StrongroomRoles.OnLeaf,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
            }
            if (!room.shutterClosed) {
                // The tokens, standing on the drum at their
                // stations — physical discs bearing the phone's
                // own names, turned with the drum.
                stock.forEachIndexed { index, pkg ->
                    val a = Math.toRadians(
                        RevolvingDrum.stationAngle(index, stock.size) + rotation - 90.0
                    )
                    val r = 74.dp
                    val isTurned = index == turnedIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(
                                x = (cos(a) * r.value).dp,
                                y = (sin(a) * r.value).dp
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isTurned) 52.dp else 42.dp)
                                .background(
                                    if (isTurned) StrongroomInks.Gold
                                    else StrongroomLight.descend(StrongroomInks.Gold, 3),
                                    CircleShape
                                )
                                .border(
                                    width = if (isTurned) 2.5.dp else 1.dp,
                                    color = StrongroomInks.Graphite,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (labels[pkg] ?: pkg).take(1).uppercase(),
                                style = StrongroomType.Scr2,
                                color = StrongroomRoles.OnLeaf
                            )
                        }
                        Text(
                            text = labels[pkg] ?: pkg,
                            style = StrongroomType.Scr0,
                            color = if (isTurned) StrongroomRoles.OnGround
                            else StrongroomLight.descend(StrongroomInks.Gold, 3)
                        )
                    }
                }
                // The index window at the top: a brass frame around
                // the turned station — where the drum presents.
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                        .size(width = 66.dp, height = 30.dp)
                        .border(2.5.dp, StrongroomInks.Graphite, RoundedCornerShape(3.dp))
                )
                // The hub: pressing it TAKES the turned app for its
                // minute — the crank's work completed by the hand.
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(46.dp)
                        .background(StrongroomRoles.Metal, CircleShape)
                        .border(2.dp, StrongroomInks.Gold, CircleShape)
                        .clickable(enabled = turnedPackage != null) {
                            turnedPackage?.let { actions.onTakeTurned(it) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(StrongroomInks.Gold, CircleShape)
                    )
                }
            } else {
                // Behind the shutter, the hub is shutter furniture:
                // a seam boss, untouchable — spent is spent.
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(30.dp)
                        .background(StrongroomRoles.Metal, CircleShape)
                        .border(2.dp, StrongroomRoles.OnLeaf, CircleShape)
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        // The drum's crank: a handle on the room's flank that turns
        // the drum with the hand's own circular travel.
        DrumCrank(
            shutterClosed = room.shutterClosed,
            onTurn = { delta ->
                rotation = if (room.shutterClosed) {
                    (rotation + delta).coerceIn(restRotation - 4.0, restRotation + 4.0)
                } else {
                    rotation + delta
                }
            },
            onRelease = {
                if (room.shutterClosed) {
                    settleTarget = restRotation
                    stopAttempt = RevolvingDrum.spentStopAttempt()
                } else if (stock.isNotEmpty()) {
                    val idx = RevolvingDrum.stationAt(rotation, stock.size)
                    settleTarget = RevolvingDrum.settleTarget(rotation, idx, stock.size)
                }
            }
        )
    }
}

@Composable
private fun DrumCrank(
    shutterClosed: Boolean,
    onTurn: (deltaDeg: Double) -> Unit,
    onRelease: () -> Unit
) {
    var armDeg by remember { mutableStateOf(-40.0) }
    Box(
        modifier = Modifier
            .size(76.dp)
            .pointerInput(shutterClosed) {
                var lastAngle: Double? = null
                detectDragGestures(
                    onDragStart = { lastAngle = null },
                    onDragEnd = { onRelease() },
                    onDragCancel = { lastAngle = null },
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
                            armDeg += delta
                            onTurn(delta)
                        }
                        lastAngle = angle
                        change.consume()
                    }
                )
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            drawCircle(color = StrongroomRoles.Metal, radius = size.minDimension / 2f - 2.dp.toPx())
            val a = Math.toRadians(armDeg - 90.0).toFloat()
            val hx = cx + (size.minDimension / 2f - 14.dp.toPx()) * cos(a)
            val hy = cy + (size.minDimension / 2f - 14.dp.toPx()) * sin(a)
            drawLine(
                color = StrongroomInks.Gold,
                start = Offset(cx, cy),
                end = Offset(hx, hy),
                strokeWidth = 6.dp.toPx()
            )
            drawCircle(color = StrongroomInks.Gold, radius = 9.dp.toPx(), center = Offset(hx, hy))
            drawCircle(color = StrongroomRoles.Metal, radius = 6.dp.toPx())
        }
    }
}

// ── The governor's wall (C-04) + the tray (C-19) ────────────────

@Composable
private fun GovernorsWall(state: AntechamberState, actions: AntechamberActions) {
    val lightState = LocalStrongroomLightState.current
    val room = state.room
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (room.crankAvailable) {
                var wound by remember { mutableStateOf(0.0) }
                var fired by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .pointerInput(Unit) {
                            var lastAngle: Double? = null
                            detectDragGestures(
                                onDragStart = { lastAngle = null },
                                onDragEnd = { lastAngle = null },
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
                                        if (delta > 0.0) {
                                            wound += delta
                                            if (wound >= 300.0 && !fired) {
                                                fired = true
                                                actions.onGovernorCrank()
                                            }
                                        }
                                    }
                                    lastAngle = angle
                                    change.consume()
                                }
                            )
                        }
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val outer = size.minDimension / 2f
                        drawCircle(
                            color = StrongroomRoles.Metal,
                            radius = outer - 2.dp.toPx(),
                            style = Stroke(width = 7.dp.toPx())
                        )
                        val a = Math.toRadians(wound - 90.0).toFloat()
                        val hx = cx + (outer - 16.dp.toPx()) * cos(a)
                        val hy = cy + (outer - 16.dp.toPx()) * sin(a)
                        drawLine(
                            color = StrongroomInks.Gold,
                            start = Offset(cx, cy),
                            end = Offset(hx, hy),
                            strokeWidth = 8.dp.toPx()
                        )
                        drawCircle(
                            color = StrongroomInks.Gold,
                            radius = 11.dp.toPx(),
                            center = Offset(hx, hy)
                        )
                        // The lead crimp seal, WHOLE, hanging at the
                        // crank's rim: dull lead in the graphite
                        // depths, crimped — checkable at a glance.
                        val sx = cx + outer * 0.52f
                        val sy = cy + outer * 0.62f
                        drawCircle(
                            color = AntechamberTexture.cellTone(
                                StrongroomInks.Graphite, StrongroomInks.Violet,
                                AntechamberTexture.Species.LEAD, 2, 2,
                                seed = 61, state = lightState
                            ),
                            radius = 13.dp.toPx(),
                            center = Offset(sx, sy)
                        )
                        drawLine(
                            color = StrongroomLight.descend(StrongroomInks.Graphite, 3),
                            start = Offset(sx - 8.dp.toPx(), sy),
                            end = Offset(sx + 8.dp.toPx(), sy),
                            strokeWidth = 2.dp.toPx()
                        )
                        drawLine(
                            color = StrongroomLight.descend(StrongroomInks.Graphite, 3),
                            start = Offset(sx, sy - 8.dp.toPx()),
                            end = Offset(sx, sy + 8.dp.toPx()),
                            strokeWidth = 2.dp.toPx()
                        )
                        drawCircle(color = StrongroomInks.Gold, radius = 7.dp.toPx())
                    }
                }
            } else {
                // The empty hook: the week's minute is spent and the
                // wall itself refuses a second taking — an arc of
                // bare steel where the crank hung.
                Canvas(Modifier.size(104.dp)) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    drawArc(
                        color = StrongroomRoles.Metal,
                        startAngle = 200f,
                        sweepAngle = 140f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx())
                    )
                    drawCircle(
                        color = StrongroomRoles.Metal,
                        radius = 5.dp.toPx(),
                        center = Offset(cx, cy - 18.dp.toPx())
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            // The tray (C-19): a shallow receptacle below the wall.
            // It receives and keeps — the snapped seal, when the
            // week's minute was taken, lies in it visible.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(
                        StrongroomLight.descend(StrongroomInks.Graphite, 1),
                        RoundedCornerShape(3.dp)
                    )
                    .border(1.5.dp, StrongroomRoles.Metal, RoundedCornerShape(3.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                if (room.snappedSealKept) {
                    Canvas(Modifier.padding(start = 14.dp).size(26.dp)) {
                        val c = Offset(size.width / 2f, size.height / 2f)
                        drawCircle(
                            color = AntechamberTexture.cellTone(
                                StrongroomInks.Graphite, StrongroomInks.Violet,
                                AntechamberTexture.Species.LEAD, 1, 1,
                                seed = 61, state = lightState
                            ),
                            radius = size.minDimension / 2f,
                            center = c
                        )
                        // Snapped: the crimp stands open — two halves
                        // of one seal, kept, never thrown away.
                        drawLine(
                            color = StrongroomRoles.Field,
                            start = Offset(c.x - size.width / 2f, c.y),
                            end = Offset(c.x + size.width / 2f, c.y),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                }
            }
        }
    }
}

// ── The held pile (C-14) — the corner, and its second step ──────

@Composable
private fun HeldPileCorner(state: AntechamberState, onOpen: () -> Unit) {
    val room = state.room
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(enabled = room.waiting.isNotEmpty()) { onOpen() }
    ) {
        // The pile: slips landed with weight, each offset on the
        // one below — the pile GROWS with the waiting count (its
        // drawn height caps at six slips; the pile's truth is the
        // store's, and the second step shows every slip whole).
        val layers = minOf(room.waiting.size, 6)
        for (i in 0 until layers) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (i * 7).dp, y = (-(layers - i) * 6).dp)
                    .size(width = 148.dp, height = 54.dp)
                    .background(
                        StrongroomLight.descend(StrongroomInks.Gold, 2 + (i % 2)),
                        RoundedCornerShape(3.dp)
                    )
                    .border(1.5.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(3.dp))
            )
        }
        if (layers == 0) {
            // The empty pile is the pile as it is — a bare brass
            // base plate. Stillness, no "all clear" (GAP-R03).
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(width = 148.dp, height = 10.dp)
                    .background(
                        StrongroomLight.descend(StrongroomInks.Gold, 3),
                        RoundedCornerShape(2.dp)
                    )
                    .border(1.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(2.dp))
            )
        }
        if (room.quietDot) {
            // THE QUIET DOT (S4-C27): one dot — the same dot for
            // one waiting arrival as for nine, at hour 1 as at
            // hour 9. It carries no word, no count, no name; it
            // clears on entering the pile (the PILE step draws
            // none). Its place is the pile's own marker plate.
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(11.dp)
                    .background(StrongroomInks.Magenta, CircleShape)
            )
        }
    }
}

@Composable
private fun HeldPileSlips(state: AntechamberState, actions: AntechamberActions) {
    val room = state.room
    Column(Modifier.fillMaxWidth()) {
        room.waiting.forEach { slip ->
            HeldSlipCard(slip, state, actions)
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun HeldSlipCard(
    slip: HeldSlip,
    state: AntechamberState,
    actions: AntechamberActions
) {
    val revealed = slip.kind == HeldSlip.Kind.CODE && slip.id in state.revealedCodeIds
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                StrongroomLight.descend(StrongroomInks.Gold, 4),
                RoundedCornerShape(3.dp)
            )
            .border(1.5.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(3.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            when (slip.kind) {
                HeldSlip.Kind.CALL -> {
                    // The number, shown whole, as it arrived.
                    Text(
                        text = AntechamberModel.heldLine(slip),
                        style = StrongroomType.Anon2,
                        color = StrongroomRoles.OnGround
                    )
                }
                HeldSlip.Kind.CODE -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (revealed) {
                            // Digits alone, with the hour.
                            Text(
                                text = AntechamberModel.heldLine(slip),
                                style = StrongroomType.Anon2,
                                color = StrongroomRoles.OnGround
                            )
                        } else {
                            // The digits under their brass cover: the
                            // slip stands with its hour, and the
                            // cover is the showing's closed door.
                            Box(
                                modifier = Modifier
                                    .size(width = 118.dp, height = 26.dp)
                                    .background(StrongroomInks.Gold, RoundedCornerShape(2.dp))
                                    .border(1.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(2.dp))
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = AntechamberModel.hourOf(slip.arrivedAtWallMillis),
                                style = StrongroomType.Anon2,
                                color = StrongroomRoles.OnGround
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row {
                // take it — the exact words (TX-DO-01), on brass.
                Box(
                    modifier = Modifier
                        .background(StrongroomInks.Gold, RoundedCornerShape(3.dp))
                        .border(1.5.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(3.dp))
                        .clickable {
                            when {
                                slip.kind == HeldSlip.Kind.CALL ->
                                    actions.onTakeCall(slip)
                                slip.kind == HeldSlip.Kind.CODE && !revealed ->
                                    actions.onShowCode(slip)
                                else -> actions.onResolveHeld(slip, taken = true)
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = AntechamberModel.TAKE_WORD,
                        style = StrongroomType.Scr1,
                        color = StrongroomRoles.OnLeaf
                    )
                }
                Spacer(Modifier.width(12.dp))
                // leave it — leaving is complete.
                Box(
                    modifier = Modifier
                        .background(StrongroomRoles.Field, RoundedCornerShape(3.dp))
                        .border(1.5.dp, StrongroomInks.Gold, RoundedCornerShape(3.dp))
                        .clickable { actions.onResolveHeld(slip, taken = false) }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = AntechamberModel.LEAVE_WORD,
                        style = StrongroomType.Scr1,
                        color = StrongroomRoles.OnGround
                    )
                }
            }
        }
    }
}
