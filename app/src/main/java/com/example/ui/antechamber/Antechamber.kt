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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.domain.StrongroomDay
import com.example.service.StrongroomEngineBridge
import com.example.service.haptics.HapticEvent
import com.example.ui.foundation.BodyIntegrator
import com.example.ui.foundation.BodyState
import com.example.ui.foundation.LocalStrongroomLightState
import com.example.ui.foundation.StrongroomLightState
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
                Spacer(Modifier.height(18.dp))
                GovernorsWall(state, actions)
                Spacer(Modifier.height(18.dp))
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

// ── Relief vocabulary — the room's machined depth ──────────────
// The print's law, carried into drawing: a raised piece is not a
// flat shape — it carries a light-facing lip, a shadow lip, and
// fasteners. Both lips are the mark's OWN RELIGHT partners (lit
// toward its lit partner, shadow toward the graphite floor), in
// whole eighths — no new ink, no wash. The lamp stands upper-left
// (the foundation's direction): lit arcs span the left-through-top
// perimeter, shadow arcs the right-through-bottom.

/** The lit and shadow lips of a raised round piece, as arcs. */
private fun DrawScope.reliefRing(
    ink: Color,
    field: Color,
    center: Offset,
    radius: Float,
    width: Float,
    state: StrongroomLightState,
    litGradient: Float = 0.9f,
    shadowGradient: Float = -0.6f
) {
    val corner = Offset(center.x - radius, center.y - radius)
    val disc = Size(radius * 2, radius * 2)
    drawArc(
        color = StrongroomLight.relief(ink, field, litGradient, state),
        startAngle = 150f, sweepAngle = 150f, useCenter = false,
        topLeft = corner, size = disc, style = Stroke(width = width)
    )
    drawArc(
        color = StrongroomLight.relief(ink, field, shadowGradient, state),
        startAngle = 330f, sweepAngle = 150f, useCenter = false,
        topLeft = corner, size = disc, style = Stroke(width = width)
    )
}

/** A bolt head: a steel disc, its lit crescent toward the lamp,
 *  its slot cut across — the print's fasteners, at room scale. */
private fun DrawScope.boltHead(
    center: Offset,
    radius: Float,
    state: StrongroomLightState
) {
    drawCircle(color = StrongroomInks.Graphite, radius = radius, center = center)
    drawArc(
        color = StrongroomLight.blendEighth(
            StrongroomInks.Graphite, StrongroomInks.Violet, 3
        ),
        startAngle = 150f, sweepAngle = 150f, useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = radius * 0.5f)
    )
    drawLine(
        color = StrongroomLight.blendEighth(
            StrongroomInks.Graphite, StrongroomInks.Violet, 2
        ),
        start = center + Offset(-radius * 0.55f, 0f),
        end = center + Offset(radius * 0.55f, 0f),
        strokeWidth = radius * 0.3f
    )
    // The state's own word on the bolt: in a warm state its lit
    // crescent warms with the room; in plain light it stands cool.
    if (StrongroomLight.paramsFor(state).warmLitPartner) {
        drawArc(
            color = StrongroomLight.relief(
                StrongroomInks.Graphite, StrongroomInks.Violet, 0.85f, state
            ),
            startAngle = 195f, sweepAngle = 60f, useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = radius * 0.5f)
        )
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
            // The rails BEHIND the bars: two horizontal steel
            // members the bars stand in front of — the grille has
            // depth, a front and a back, like the print's rings.
            for (railFrac in listOf(0.30f, 0.68f)) {
                val ry = size.height * railFrac
                drawRect(
                    color = StrongroomLight.blendEighth(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 1
                    ),
                    topLeft = Offset(0f, ry - 6.dp.toPx()),
                    size = Size(size.width, 12.dp.toPx())
                )
                drawLine(
                    color = StrongroomInks.Graphite,
                    start = Offset(0f, ry + 6.dp.toPx()),
                    end = Offset(size.width, ry + 6.dp.toPx()),
                    strokeWidth = 2.dp.toPx()
                )
            }
            // The bars: grained steel, each carrying its cylinder
            // read — a lit spine toward the lamp, shadow at its
            // flanks.
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
                        topLeft = Offset(x - 3.5.dp.toPx(), yy),
                        size = Size(7.dp.toPx(), cell + 1f)
                    )
                    yy += cell; cy++
                }
                drawLine(
                    color = StrongroomLight.blendEighth(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 3
                    ),
                    start = Offset(x - 1.dp.toPx(), 0f),
                    end = Offset(x - 1.dp.toPx(), size.height),
                    strokeWidth = 1.6.dp.toPx()
                )
                drawLine(
                    color = StrongroomInks.Graphite,
                    start = Offset(x + 3.dp.toPx(), 0f),
                    end = Offset(x + 3.dp.toPx(), size.height),
                    strokeWidth = 1.2.dp.toPx()
                )
            }
            // The mid rail in FRONT of the bars (the grille's
            // waist member) — a bar across, relieved.
            drawRect(
                color = StrongroomRoles.Metal,
                topLeft = Offset(0f, size.height / 2f - 3.5.dp.toPx()),
                size = Size(size.width, 7.dp.toPx())
            )
            drawLine(
                color = StrongroomLight.blendEighth(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 3
                ),
                start = Offset(0f, size.height / 2f - 3.5.dp.toPx()),
                end = Offset(size.width, size.height / 2f - 3.5.dp.toPx()),
                strokeWidth = 1.6.dp.toPx()
            )
            // The frame's own relief: a lit line where the frame
            // meets the light at its head, a shadow line at its
            // foot, and a bolt at every corner — the grille is
            // fastened into the wall, not pasted on it.
            drawLine(
                color = StrongroomLight.blendEighth(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 3
                ),
                start = Offset(0f, 1.5.dp.toPx()),
                end = Offset(size.width, 1.5.dp.toPx()),
                strokeWidth = 2.5.dp.toPx()
            )
            drawLine(
                color = StrongroomInks.Graphite,
                start = Offset(0f, size.height - 1.5.dp.toPx()),
                end = Offset(size.width, size.height - 1.5.dp.toPx()),
                strokeWidth = 2.5.dp.toPx()
            )
            val insetB = 11.dp.toPx()
            boltHead(Offset(insetB, insetB), 4.5.dp.toPx(), lightState)
            boltHead(Offset(size.width - insetB, insetB), 4.5.dp.toPx(), lightState)
            boltHead(Offset(insetB, size.height - insetB), 4.5.dp.toPx(), lightState)
            boltHead(Offset(size.width - insetB, size.height - insetB), 4.5.dp.toPx(), lightState)
            // The latch: a small plate on the right stile with its
            // ring — the grille is a door, and a door has a handle.
            val latchCx = size.width - 26.dp.toPx()
            val latchCy = size.height / 2f
            drawRect(
                color = StrongroomLight.blendEighth(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 1
                ),
                topLeft = Offset(latchCx - 8.dp.toPx(), latchCy - 20.dp.toPx()),
                size = Size(16.dp.toPx(), 40.dp.toPx())
            )
            drawCircle(
                color = StrongroomLight.blendEighth(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 3
                ),
                radius = 8.dp.toPx(),
                center = Offset(latchCx, latchCy),
                style = Stroke(width = 2.6.dp.toPx())
            )
            drawArc(
                color = StrongroomInks.Graphite,
                startAngle = 20f, sweepAngle = 130f, useCenter = false,
                topLeft = Offset(latchCx - 8.dp.toPx(), latchCy - 8.dp.toPx()),
                size = Size(16.dp.toPx(), 16.dp.toPx()),
                style = Stroke(width = 2.6.dp.toPx())
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
        // The mounting plate: the horn is bolted to the room's
        // wall — a steel plate, grained, a bolt at every corner.
        Canvas(
            modifier = Modifier
                .size(width = 196.dp, height = 158.dp)
                .align(Alignment.Center)
        ) {
            val plateTone = StrongroomLight.blendEighth(
                StrongroomInks.Graphite, StrongroomInks.Violet, 1
            )
            drawRect(
                color = plateTone,
                topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
                size = Size(size.width - 12.dp.toPx(), size.height - 12.dp.toPx())
            )
            val cellP = AntechamberTexture.Species.STEEL.cellPx.dp.toPx()
            var yy = 6.dp.toPx()
            var rowP = 0
            while (yy < size.height - 6.dp.toPx()) {
                var xx = 6.dp.toPx()
                var colP = 0
                while (xx < size.width - 6.dp.toPx()) {
                    drawRect(
                        color = AntechamberTexture.cellTone(
                            plateTone, StrongroomInks.Violet,
                            AntechamberTexture.Species.STEEL, colP, rowP,
                            seed = 17, state = lightState
                        ),
                        topLeft = Offset(xx, yy),
                        size = Size(cellP + 1f, cellP + 1f)
                    )
                    xx += cellP; colP++
                }
                yy += cellP; rowP++
            }
            drawLine(
                color = StrongroomLight.blendEighth(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 3
                ),
                start = Offset(6.dp.toPx(), 7.dp.toPx()),
                end = Offset(size.width - 6.dp.toPx(), 7.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
            val bi = 16.dp.toPx()
            boltHead(Offset(bi, bi), 4.5.dp.toPx(), lightState)
            boltHead(Offset(size.width - bi, bi), 4.5.dp.toPx(), lightState)
            boltHead(Offset(bi, size.height - bi), 4.5.dp.toPx(), lightState)
            boltHead(Offset(size.width - bi, size.height - bi), 4.5.dp.toPx(), lightState)
        }
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
                // The bell's rim: a rolled lip, lit toward the
                // lamp, shadowed away — brass turned on a lathe.
                reliefRing(
                    StrongroomInks.Gold, StrongroomInks.Graphite,
                    Offset(cx, cy), outer - 3.dp.toPx(), 5.5.dp.toPx(),
                    lightState
                )
                // The turned rings of the bell, stepping down into
                // the throat: each ring a shade deeper, each with
                // its own relief — the horn is a machined mouth,
                // not a target of flat circles.
                for ((frac, tone) in listOf(
                    0.86f to StrongroomLight.descend(StrongroomInks.Gold, 1),
                    0.74f to StrongroomLight.descend(StrongroomInks.Gold, 2),
                    0.63f to StrongroomLight.descend(StrongroomInks.Gold, 3)
                )) {
                    drawCircle(
                        color = tone,
                        radius = outer * frac,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    reliefRing(
                        tone, StrongroomInks.Graphite,
                        Offset(cx, cy), outer * frac, 4.dp.toPx(),
                        lightState, litGradient = 0.7f, shadowGradient = -0.5f
                    )
                }
                // The throat: brass deepening to graphite — a
                // speaking mouth, not a button.
                drawCircle(
                    color = StrongroomLight.descend(StrongroomInks.Gold, 3),
                    radius = outer * 0.62f
                )
                drawCircle(
                    color = StrongroomInks.Graphite,
                    radius = outer * 0.38f
                )
                drawCircle(
                    color = StrongroomLight.blendEighth(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 2
                    ),
                    radius = outer * 0.38f,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawCircle(
                    color = StrongroomLight.blendEighth(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 1
                    ),
                    radius = outer * 0.20f,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

// ── The drum + its tokens + the shutter (C-09 · C-08(2) · B-9(4)) ──

@Composable
private fun DrumAndTokens(state: AntechamberState, actions: AntechamberActions) {
    val lightState = LocalStrongroomLightState.current
    val context = LocalContext.current
    val room = state.room
    val stock = room.drumStock
    val labels = remember(state.tokens) { state.tokens.associate { it.packageName to it.label } }

    var rotation by remember(stock, room.shutterClosed) { mutableStateOf(0.0) }
    var settleTarget by remember { mutableStateOf<Double?>(null) }
    // Whether the standing settle is a LANDING (a released drum
    // caught by its rail) or a strain-return behind the shutter —
    // only the landing speaks DRUM_LAND (SOUND seat, 2026-11-03).
    var settleIsLanding by remember { mutableStateOf(false) }
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
        // The drum LANDS (ledger A-7, wired by the SOUND seat):
        // the settle's completion is the land — strike + seat —
        // on the open drum only; a strain-return behind the
        // shutter lands nothing.
        if (settleIsLanding && !room.shutterClosed) {
            StrongroomEngineBridge.fireSurfaceEvent(
                context, HapticEvent.DRUM_LAND
            )
        }
        settleIsLanding = false
    }

    val turnedIndex = if (stock.isEmpty()) -1
    else RevolvingDrum.stationAt(rotation, stock.size)
    val turnedPackage = stock.getOrNull(turnedIndex)

    // The drum's detents speak as stations pass the index —
    // under the dragging hand, under the crank, and through the
    // settle's last travel alike (one derivation, every road).
    var lastTurnedIndex by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(turnedIndex) {
        val prev = lastTurnedIndex
        if (prev != null && turnedIndex >= 0 && turnedIndex != prev) {
            StrongroomEngineBridge.fireSurfaceEvent(
                context, HapticEvent.DRUM_DETENT
            )
        }
        lastTurnedIndex = turnedIndex
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // The drum itself.
        Box(
            modifier = Modifier
                .size(248.dp)
                .clip(CircleShape)
                .pointerInput(stock, room.shutterClosed) {
                    var lastAngle: Double? = null
                    detectDragGestures(
                        onDragStart = { lastAngle = null; settleTarget = null },
                        onDragEnd = {
                            if (room.shutterClosed) {
                                settleIsLanding = false
                                settleTarget = restRotation
                                stopAttempt = RevolvingDrum.spentStopAttempt()
                            } else if (stock.isNotEmpty()) {
                                settleIsLanding = true
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
                                    // The refusal, when the attempt
                                    // spends it, is the registry's
                                    // own row — felt in the wrist,
                                    // silent by filed law (HARD-STOP-
                                    // SILENCE); it fires here, once
                                    // per approach (SOUND seat).
                                    if (stopAttempt?.onApproach(
                                            kotlin.math.abs(strained - restRotation) - 4.0
                                        ) == true
                                    ) {
                                        StrongroomEngineBridge.fireSurfaceEvent(
                                            context, HapticEvent.DRUM_HARD_STOP
                                        )
                                    }
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
                if (!room.shutterClosed) {
                    // The token well: a recess machined into the
                    // drum's face where the tokens stand — its floor
                    // a shade down from the face, its upper inner
                    // wall in shadow, its lower lip catching the
                    // light. The tokens stand IN the drum, not on
                    // a sticker.
                    drawCircle(
                        color = StrongroomLight.descend(StrongroomInks.Gold, 2),
                        radius = outer - 17.dp.toPx(),
                        center = Offset(cx, cy)
                    )
                    drawArc(
                        color = StrongroomLight.descend(StrongroomInks.Gold, 4),
                        startAngle = 180f, sweepAngle = 180f, useCenter = false,
                        topLeft = Offset(
                            cx - (outer - 17.dp.toPx()), cy - (outer - 17.dp.toPx())
                        ),
                        size = Size(
                            (outer - 17.dp.toPx()) * 2, (outer - 17.dp.toPx()) * 2
                        ),
                        style = Stroke(width = 4.5.dp.toPx())
                    )
                    drawArc(
                        color = StrongroomLight.relief(
                            StrongroomInks.Gold, StrongroomInks.Graphite, 0.6f,
                            lightState
                        ),
                        startAngle = 0f, sweepAngle = 180f, useCenter = false,
                        topLeft = Offset(
                            cx - (outer - 17.dp.toPx()), cy - (outer - 17.dp.toPx())
                        ),
                        size = Size(
                            (outer - 17.dp.toPx()) * 2, (outer - 17.dp.toPx()) * 2
                        ),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
                drawCircle(
                    color = StrongroomRoles.Metal,
                    radius = outer - 3.dp.toPx(),
                    style = Stroke(width = 5.dp.toPx())
                )
                reliefRing(
                    StrongroomInks.Graphite, StrongroomInks.Violet,
                    Offset(cx, cy), outer - 3.dp.toPx(), 5.dp.toPx(),
                    lightState, litGradient = 0.85f, shadowGradient = -0.4f
                )
                if (room.shutterClosed) {
                    // THE BRASS SHUTTER, closed over the metered
                    // apps: slat over slat, the drum's own brass —
                    // behind it the tokens are GONE (they are not
                    // drawn at all below), never dimmed. Each slat
                    // is GRAINED like the drum it covers (brass is
                    // brass, open or shut — a flat band would be a
                    // placeholder wearing the shutter's name), its
                    // tone stepped a shade per leaf so the leaves
                    // read as separate metal, seamed at each joint.
                    val slats = 7
                    val band = size.height / slats
                    for (s in 0 until slats) {
                        val y0 = band * s
                        var yy = y0
                        var rowI = (y0 / cell).toInt()
                        while (yy < y0 + band) {
                            var xx = 0f
                            var colI = 0
                            while (xx < size.width) {
                                val tone = AntechamberTexture.cellTone(
                                    StrongroomLight.descend(
                                        StrongroomInks.Gold, 1 + (s % 2)
                                    ),
                                    StrongroomInks.Graphite,
                                    AntechamberTexture.Species.BRASS,
                                    colI, rowI, seed = 53, state = lightState
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
                        drawLine(
                            color = StrongroomRoles.OnLeaf,
                            start = Offset(0f, y0),
                            end = Offset(size.width, y0),
                            strokeWidth = 2.dp.toPx()
                        )
                        // The slat's rolled profile: just under
                        // the seam above it, the leaf's curve
                        // catches the light; at its own foot it
                        // falls into the next seam's shadow — a
                        // shutter of rolled leaves, not bands.
                        if (s > 0) {
                            drawLine(
                                color = StrongroomLight.relief(
                                    StrongroomInks.Gold, StrongroomInks.Graphite,
                                    0.55f, lightState
                                ),
                                start = Offset(0f, y0 + 2.2.dp.toPx()),
                                end = Offset(size.width, y0 + 2.2.dp.toPx()),
                                strokeWidth = 1.6.dp.toPx()
                            )
                        }
                        drawLine(
                            color = StrongroomLight.descend(StrongroomInks.Gold, 3),
                            start = Offset(0f, y0 + band - 1.2.dp.toPx()),
                            end = Offset(size.width, y0 + band - 1.2.dp.toPx()),
                            strokeWidth = 1.6.dp.toPx()
                        )
                    }
                    // The cast shadow where the shutter leaves the
                    // rim: two quantised steps of the drum's own
                    // brass, deepening upward — the leaf comes OUT
                    // of the machine, and the machine shades it.
                    drawRect(
                        color = StrongroomLight.descend(StrongroomInks.Gold, 3),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, 5.dp.toPx())
                    )
                    drawRect(
                        color = StrongroomLight.descend(StrongroomInks.Gold, 4),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, 2.5.dp.toPx())
                    )
                    // The side channels: steel guides the shutter
                    // runs in — grained, bolted top and bottom.
                    for (channelX in listOf(0f, size.width - 10.dp.toPx())) {
                        var yy = 0f
                        var rowC = 0
                        while (yy < size.height) {
                            drawRect(
                                color = AntechamberTexture.cellTone(
                                    StrongroomInks.Graphite, StrongroomInks.Violet,
                                    AntechamberTexture.Species.STEEL, 3, rowC,
                                    seed = 59, state = lightState
                                ),
                                topLeft = Offset(channelX, yy),
                                size = Size(10.dp.toPx(), cell + 1f)
                            )
                            yy += cell; rowC++
                        }
                        drawLine(
                            color = StrongroomLight.blendEighth(
                                StrongroomInks.Graphite, StrongroomInks.Violet, 2
                            ),
                            start = Offset(
                                if (channelX == 0f) 9.dp.toPx() else channelX + 1.dp.toPx(),
                                0f
                            ),
                            end = Offset(
                                if (channelX == 0f) 9.dp.toPx() else channelX + 1.dp.toPx(),
                                size.height
                            ),
                            strokeWidth = 1.4.dp.toPx()
                        )
                    }
                    boltHead(
                        Offset(5.dp.toPx(), 14.dp.toPx()), 3.2.dp.toPx(), lightState
                    )
                    boltHead(
                        Offset(5.dp.toPx(), size.height - 14.dp.toPx()),
                        3.2.dp.toPx(), lightState
                    )
                    boltHead(
                        Offset(size.width - 5.dp.toPx(), 14.dp.toPx()),
                        3.2.dp.toPx(), lightState
                    )
                    boltHead(
                        Offset(size.width - 5.dp.toPx(), size.height - 14.dp.toPx()),
                        3.2.dp.toPx(), lightState
                    )
                    // The bottom rail: the shutter's heavy last
                    // member — a deeper band with its pull groove
                    // struck across the centre.
                    val railY = size.height - band
                    drawRect(
                        color = StrongroomLight.descend(StrongroomInks.Gold, 2),
                        topLeft = Offset(10.dp.toPx(), railY + band * 0.45f),
                        size = Size(size.width - 20.dp.toPx(), band * 0.55f)
                    )
                    drawLine(
                        color = StrongroomRoles.OnLeaf,
                        start = Offset(size.width / 2f - 27.dp.toPx(), railY + band * 0.72f),
                        end = Offset(size.width / 2f + 27.dp.toPx(), railY + band * 0.72f),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = StrongroomLight.relief(
                            StrongroomInks.Gold, StrongroomInks.Graphite, 0.5f,
                            lightState
                        ),
                        start = Offset(
                            size.width / 2f - 27.dp.toPx(),
                            railY + band * 0.72f + 2.4.dp.toPx()
                        ),
                        end = Offset(
                            size.width / 2f + 27.dp.toPx(),
                            railY + band * 0.72f + 2.4.dp.toPx()
                        ),
                        strokeWidth = 1.2.dp.toPx()
                    )
                }
                // The rim's perimeter bolts — the print's own
                // furniture: the drum's face is fastened to its
                // machine by a ring of bolt heads, open or shut.
                for (i in 0 until 8) {
                    val a = Math.toRadians(i * 45.0 + 22.5)
                    boltHead(
                        Offset(
                            cx + (outer - 10.dp.toPx()) * cos(a).toFloat(),
                            cy + (outer - 10.dp.toPx()) * sin(a).toFloat()
                        ),
                        4.2.dp.toPx(), lightState
                    )
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
                                .size(if (isTurned) 52.dp else 42.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // A STRUCK COIN, not a sticker: cast
                            // shadow on the well floor, a reeded
                            // rim, a face in relief — the phone's
                            // initial struck into it like a mint
                            // mark.
                            Canvas(Modifier.matchParentSize()) {
                                val c = Offset(size.width / 2f, size.height / 2f)
                                val r = size.minDimension / 2f
                                val face = if (isTurned) StrongroomInks.Gold
                                else StrongroomLight.descend(StrongroomInks.Gold, 3)
                                drawCircle(
                                    color = StrongroomLight.descend(
                                        StrongroomInks.Gold, 5
                                    ),
                                    radius = r - 1.dp.toPx(),
                                    center = c + Offset(1.6.dp.toPx(), 2.6.dp.toPx())
                                )
                                drawCircle(
                                    color = StrongroomLight.descend(face, 2),
                                    radius = r - 0.5.dp.toPx(), center = c
                                )
                                for (t in 0 until 24) {
                                    val ta = Math.toRadians(t * 15.0)
                                    val inner = Offset(
                                        c.x + (r - 4.6.dp.toPx()) * cos(ta).toFloat(),
                                        c.y + (r - 4.6.dp.toPx()) * sin(ta).toFloat()
                                    )
                                    val outerP = Offset(
                                        c.x + (r - 2.2.dp.toPx()) * cos(ta).toFloat(),
                                        c.y + (r - 2.2.dp.toPx()) * sin(ta).toFloat()
                                    )
                                    drawLine(
                                        color = StrongroomLight.descend(face, 3),
                                        start = inner, end = outerP,
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }
                                reliefRing(
                                    face, StrongroomInks.Graphite, c,
                                    r - 3.dp.toPx(), 2.4.dp.toPx(), lightState,
                                    litGradient = 0.8f, shadowGradient = -0.55f
                                )
                                drawCircle(
                                    color = face,
                                    radius = r - 4.4.dp.toPx(), center = c
                                )
                            }
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
                // A real aperture: a relieved frame, a recessed
                // dark opening, a glint on its lower inner lip.
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                        .size(width = 66.dp, height = 30.dp)
                ) {
                    Canvas(Modifier.matchParentSize()) {
                        drawRect(
                            color = StrongroomInks.Graphite,
                            topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                            size = Size(size.width - 8.dp.toPx(), size.height - 8.dp.toPx())
                        )
                        drawRect(
                            color = StrongroomLight.relief(
                                StrongroomInks.Gold, StrongroomInks.Graphite,
                                0.7f, lightState
                            ),
                            topLeft = Offset(0f, 0f),
                            size = Size(size.width, 2.2.dp.toPx())
                        )
                        drawRect(
                            color = StrongroomLight.relief(
                                StrongroomInks.Gold, StrongroomInks.Graphite,
                                0.7f, lightState
                            ),
                            topLeft = Offset(0f, 0f),
                            size = Size(2.2.dp.toPx(), size.height)
                        )
                        drawRect(
                            color = StrongroomLight.descend(StrongroomInks.Gold, 3),
                            topLeft = Offset(0f, size.height - 2.2.dp.toPx()),
                            size = Size(size.width, 2.2.dp.toPx())
                        )
                        drawRect(
                            color = StrongroomLight.descend(StrongroomInks.Gold, 3),
                            topLeft = Offset(size.width - 2.2.dp.toPx(), 0f),
                            size = Size(2.2.dp.toPx(), size.height)
                        )
                        drawLine(
                            color = StrongroomLight.blendEighth(
                                StrongroomInks.Graphite, StrongroomInks.Violet, 2
                            ),
                            start = Offset(5.dp.toPx(), size.height - 5.dp.toPx()),
                            end = Offset(size.width - 5.dp.toPx(), size.height - 5.dp.toPx()),
                            strokeWidth = 1.4.dp.toPx()
                        )
                        boltHead(
                            Offset(4.dp.toPx(), 4.dp.toPx()), 1.9.dp.toPx(), lightState
                        )
                        boltHead(
                            Offset(size.width - 4.dp.toPx(), 4.dp.toPx()),
                            1.9.dp.toPx(), lightState
                        )
                        boltHead(
                            Offset(4.dp.toPx(), size.height - 4.dp.toPx()),
                            1.9.dp.toPx(), lightState
                        )
                        boltHead(
                            Offset(size.width - 4.dp.toPx(), size.height - 4.dp.toPx()),
                            1.9.dp.toPx(), lightState
                        )
                    }
                }
                // The hub: pressing it TAKES the turned app for its
                // minute — the crank's work completed by the hand.
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(46.dp)
                        .clickable(enabled = turnedPackage != null) {
                            turnedPackage?.let { actions.onTakeTurned(it) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // The hub as a boss: a steel dome on the
                    // drum's axle, relieved, with its gold cap
                    // and centre bolt — a thing the hand presses,
                    // not a button drawn on the face.
                    Canvas(Modifier.matchParentSize()) {
                        val c = Offset(size.width / 2f, size.height / 2f)
                        drawCircle(
                            color = StrongroomLight.descend(StrongroomInks.Gold, 5),
                            radius = 21.dp.toPx(),
                            center = c + Offset(1.5.dp.toPx(), 2.5.dp.toPx())
                        )
                        drawCircle(
                            color = StrongroomRoles.Metal,
                            radius = 21.dp.toPx(), center = c
                        )
                        reliefRing(
                            StrongroomInks.Graphite, StrongroomInks.Violet, c,
                            19.dp.toPx(), 3.6.dp.toPx(), lightState,
                            litGradient = 0.85f, shadowGradient = -0.4f
                        )
                        drawCircle(
                            color = StrongroomInks.Gold,
                            radius = 9.dp.toPx(), center = c
                        )
                        reliefRing(
                            StrongroomInks.Gold, StrongroomInks.Graphite, c,
                            8.dp.toPx(), 2.dp.toPx(), lightState,
                            litGradient = 0.8f, shadowGradient = -0.5f
                        )
                        drawCircle(
                            color = StrongroomInks.Graphite,
                            radius = 2.6.dp.toPx(), center = c
                        )
                    }
                }
            } else {
                // Behind the shutter, the hub is shutter furniture:
                // a seam boss, untouchable — spent is spent.
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(30.dp)
                ) {
                    Canvas(Modifier.matchParentSize()) {
                        val c = Offset(size.width / 2f, size.height / 2f)
                        drawCircle(
                            color = StrongroomRoles.Metal,
                            radius = 13.dp.toPx(), center = c
                        )
                        reliefRing(
                            StrongroomInks.Graphite, StrongroomInks.Violet, c,
                            11.5.dp.toPx(), 2.6.dp.toPx(), lightState,
                            litGradient = 0.85f, shadowGradient = -0.4f
                        )
                        drawCircle(
                            color = StrongroomRoles.OnLeaf,
                            radius = 2.4.dp.toPx(), center = c
                        )
                    }
                }
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
                    settleIsLanding = false
                    settleTarget = restRotation
                    stopAttempt = RevolvingDrum.spentStopAttempt()
                } else if (stock.isNotEmpty()) {
                    settleIsLanding = true
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
    val lightState = LocalStrongroomLightState.current
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
            // The crank's boss plate on the room's flank.
            drawCircle(
                color = StrongroomLight.blendEighth(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 1
                ),
                radius = size.minDimension / 2f - 2.dp.toPx()
            )
            drawCircle(
                color = StrongroomRoles.Metal,
                radius = size.minDimension / 2f - 2.dp.toPx(),
                style = Stroke(width = 2.5.dp.toPx())
            )
            reliefRing(
                StrongroomInks.Graphite, StrongroomInks.Violet,
                Offset(cx, cy), size.minDimension / 2f - 5.dp.toPx(),
                3.dp.toPx(), lightState,
                litGradient = 0.85f, shadowGradient = -0.4f
            )
            val a = Math.toRadians(armDeg - 90.0).toFloat()
            val hx = cx + (size.minDimension / 2f - 14.dp.toPx()) * cos(a)
            val hy = cy + (size.minDimension / 2f - 14.dp.toPx()) * sin(a)
            // The arm's shadow, cast on the plate beneath it.
            drawLine(
                color = StrongroomInks.Graphite,
                start = Offset(cx + 2.dp.toPx(), cy + 2.6.dp.toPx()),
                end = Offset(hx + 2.dp.toPx(), hy + 2.6.dp.toPx()),
                strokeWidth = 6.5.dp.toPx()
            )
            drawLine(
                color = StrongroomInks.Gold,
                start = Offset(cx, cy),
                end = Offset(hx, hy),
                strokeWidth = 6.dp.toPx()
            )
            // The handle: a turned knob — grip rings and a
            // graphite end cap, a thing a hand closes on.
            drawCircle(color = StrongroomInks.Gold, radius = 9.dp.toPx(), center = Offset(hx, hy))
            drawCircle(
                color = StrongroomLight.descend(StrongroomInks.Gold, 2),
                radius = 6.dp.toPx(),
                center = Offset(hx, hy),
                style = Stroke(width = 1.6.dp.toPx())
            )
            drawCircle(color = StrongroomRoles.Metal, radius = 2.6.dp.toPx(), center = Offset(hx, hy))
            drawCircle(color = StrongroomInks.Gold, radius = 6.dp.toPx())
            drawCircle(color = StrongroomRoles.Metal, radius = 2.2.dp.toPx())
        }
    }
}

// ── The governor's wall (C-04) + the tray (C-19) ────────────────

@Composable
private fun GovernorsWall(state: AntechamberState, actions: AntechamberActions) {
    val lightState = LocalStrongroomLightState.current
    val context = LocalContext.current
    val room = state.room
    Box(Modifier.fillMaxWidth()) {
        // THE WALL PLATE: the crank and the tray are MOUNTED,
        // not floating — a grained steel plate fastened to the
        // room's wall, a bolt at every corner. The empty hook
        // stands on the same plate when the week's minute is
        // spent: the plate is the wall's furniture either way.
        Canvas(Modifier.matchParentSize()) {
            val plateInk = StrongroomLight.blendEighth(
                StrongroomInks.Graphite, StrongroomInks.Violet, 1
            )
            drawRect(
                color = plateInk,
                topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                size = Size(size.width - 4.dp.toPx(), size.height - 4.dp.toPx())
            )
            val cellW = AntechamberTexture.Species.STEEL.cellPx.dp.toPx()
            var yy = 2.dp.toPx()
            var rowW = 0
            while (yy < size.height - 2.dp.toPx()) {
                var xx = 2.dp.toPx()
                var colW = 0
                while (xx < size.width - 2.dp.toPx()) {
                    drawRect(
                        color = AntechamberTexture.cellTone(
                            plateInk, StrongroomInks.Violet,
                            AntechamberTexture.Species.STEEL, colW, rowW,
                            seed = 19, state = lightState
                        ),
                        topLeft = Offset(xx, yy),
                        size = Size(cellW + 1f, cellW + 1f)
                    )
                    xx += cellW; colW++
                }
                yy += cellW; rowW++
            }
            drawLine(
                color = StrongroomLight.blendEighth(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 3
                ),
                start = Offset(2.dp.toPx(), 3.dp.toPx()),
                end = Offset(size.width - 2.dp.toPx(), 3.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
            val bi = 10.dp.toPx()
            boltHead(Offset(bi, bi), 4.dp.toPx(), lightState)
            boltHead(Offset(size.width - bi, bi), 4.dp.toPx(), lightState)
            boltHead(Offset(bi, size.height - bi), 4.dp.toPx(), lightState)
            boltHead(Offset(size.width - bi, size.height - bi), 4.dp.toPx(), lightState)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (room.crankAvailable) {
                var wound by remember { mutableStateOf(0.0) }
                var fired by remember { mutableStateOf(false) }
                var sealSnapped by remember { mutableStateOf(false) }
                var trainsFired by remember { mutableStateOf(0) }
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
                                            // The crank's sound (SOUND
                                            // seat, 2026-11-03): the lead
                                            // seal SNAPS as the crank
                                            // first turns against it —
                                            // once, irreversible for the
                                            // week — and each 100° of
                                            // the throw speaks the
                                            // crank's detent train.
                                            if (!sealSnapped) {
                                                sealSnapped = true
                                                StrongroomEngineBridge.fireSurfaceEvent(
                                                    context, HapticEvent.GOVERNOR_SEAL_SNAP
                                                )
                                            }
                                            val trains = (wound / 100.0).toInt()
                                            if (trains > trainsFired) {
                                                trainsFired = trains
                                                StrongroomEngineBridge.fireSurfaceEvent(
                                                    context, HapticEvent.GOVERNOR_CRANK_TURN
                                                )
                                            }
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
                        reliefRing(
                            StrongroomInks.Graphite, StrongroomInks.Violet,
                            Offset(cx, cy), outer - 2.dp.toPx(), 7.dp.toPx(),
                            lightState, litGradient = 0.85f, shadowGradient = -0.4f
                        )
                        val a = Math.toRadians(wound - 90.0).toFloat()
                        val hx = cx + (outer - 16.dp.toPx()) * cos(a)
                        val hy = cy + (outer - 16.dp.toPx()) * sin(a)
                        drawLine(
                            color = StrongroomInks.Graphite,
                            start = Offset(cx + 2.dp.toPx(), cy + 2.6.dp.toPx()),
                            end = Offset(hx + 2.dp.toPx(), hy + 2.6.dp.toPx()),
                            strokeWidth = 8.5.dp.toPx()
                        )
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
                        drawCircle(
                            color = StrongroomLight.descend(StrongroomInks.Gold, 2),
                            radius = 7.5.dp.toPx(),
                            center = Offset(hx, hy),
                            style = Stroke(width = 1.8.dp.toPx())
                        )
                        drawCircle(
                            color = StrongroomInks.Graphite,
                            radius = 3.dp.toPx(),
                            center = Offset(hx, hy)
                        )
                        // The lead crimp seal, WHOLE, hanging at the
                        // crank's rim on its wire: dull lead in the
                        // graphite depths, its edge crimped by the
                        // die — checkable at a glance.
                        val sx = cx + outer * 0.52f
                        val sy = cy + outer * 0.62f
                        val leadTone = AntechamberTexture.cellTone(
                            StrongroomInks.Graphite, StrongroomInks.Violet,
                            AntechamberTexture.Species.LEAD, 2, 2,
                            seed = 61, state = lightState
                        )
                        drawLine(
                            color = StrongroomLight.blendEighth(
                                StrongroomInks.Graphite, StrongroomInks.Violet, 2
                            ),
                            start = Offset(cx + outer * 0.40f, cy + outer * 0.47f),
                            end = Offset(sx - outer * 0.06f, sy - outer * 0.07f),
                            strokeWidth = 1.6.dp.toPx()
                        )
                        for (bump in 0 until 10) {
                            val ba = Math.toRadians(bump * 36.0)
                            drawCircle(
                                color = leadTone,
                                radius = 2.6.dp.toPx(),
                                center = Offset(
                                    sx + 12.dp.toPx() * cos(ba).toFloat(),
                                    sy + 12.dp.toPx() * sin(ba).toFloat()
                                )
                            )
                        }
                        drawCircle(
                            color = leadTone,
                            radius = 12.5.dp.toPx(),
                            center = Offset(sx, sy)
                        )
                        drawArc(
                            color = StrongroomLight.blendEighth(
                                StrongroomInks.Graphite, StrongroomInks.Violet, 2
                            ),
                            startAngle = 150f, sweepAngle = 120f, useCenter = false,
                            topLeft = Offset(sx - 12.5.dp.toPx(), sy - 12.5.dp.toPx()),
                            size = Size(25.dp.toPx(), 25.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx())
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
                // The tray as a tray: an inner back edge above,
                // a front lip below with the light on its top —
                // a shallow steel receptacle, not a band.
                Canvas(Modifier.matchParentSize()) {
                    drawRect(
                        color = StrongroomInks.Graphite,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, 5.dp.toPx())
                    )
                    drawRect(
                        color = StrongroomRoles.Metal,
                        topLeft = Offset(0f, size.height - 7.dp.toPx()),
                        size = Size(size.width, 7.dp.toPx())
                    )
                    drawLine(
                        color = StrongroomLight.blendEighth(
                            StrongroomInks.Graphite, StrongroomInks.Violet, 3
                        ),
                        start = Offset(0f, size.height - 7.dp.toPx()),
                        end = Offset(size.width, size.height - 7.dp.toPx()),
                        strokeWidth = 1.6.dp.toPx()
                    )
                }
                if (room.snappedSealKept) {
                    Canvas(Modifier.padding(start = 14.dp).size(26.dp)) {
                        val c = Offset(size.width / 2f, size.height / 2f)
                        drawCircle(
                            color = StrongroomLight.blendEighth(
                                StrongroomInks.Graphite, StrongroomInks.Violet, 2
                            ),
                            radius = size.minDimension / 2f,
                            center = c + Offset(1.6.dp.toPx(), 2.2.dp.toPx())
                        )
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
    val lightState = LocalStrongroomLightState.current
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
        // The pile's ground shadow: the slips lie ON the room,
        // and the room darkens cool beneath their weight.
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 5.dp, y = 3.dp)
                .size(width = 158.dp, height = 12.dp)
                .background(
                    StrongroomLight.blendEighth(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 2
                    ),
                    RoundedCornerShape(6.dp)
                )
        )
        val layers = minOf(room.waiting.size, 6)
        for (i in 0 until layers) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (i * 7).dp, y = (-(layers - i) * 6).dp)
                    .rotate(((i * 37) % 5 - 2) * 0.65f)
                    .size(width = 148.dp, height = 54.dp)
                    .background(
                        StrongroomLight.descend(StrongroomInks.Gold, 2 + (i % 2)),
                        RoundedCornerShape(3.dp)
                    )
                    .border(1.5.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(3.dp))
            ) {
                // Each landed slip's leaf, grained.
                Canvas(Modifier.matchParentSize()) {
                    grainLeaf(
                        StrongroomLight.descend(StrongroomInks.Gold, 2 + (i % 2)),
                        seed = 71 + i,
                        state = lightState
                    )
                }
                // The slip's weight on the one below: a shadow
                // along its foot, and the light along its head.
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(
                            StrongroomLight.descend(StrongroomInks.Gold, 5)
                        )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(1.6.dp)
                        .background(
                            StrongroomLight.relief(
                                StrongroomInks.Gold, StrongroomInks.Graphite,
                                0.6f, lightState
                            )
                        )
                )
            }
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
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(1.6.dp)
                        .background(
                            StrongroomLight.relief(
                                StrongroomInks.Gold, StrongroomInks.Graphite,
                                0.6f, lightState
                            )
                        )
                )
            }
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

/** Grain a gold-leaf surface at the brass cell (the pile's slips
 *  and covers are leaf, and leaf is grained like every material
 *  in the room — a flat gold band is a placeholder finish, the
 *  same defect the shutter's first drawing carried). */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.grainLeaf(
    base: androidx.compose.ui.graphics.Color,
    seed: Int,
    state: StrongroomLightState
) {
    val cell = AntechamberTexture.Species.BRASS.cellPx.dp.toPx()
    var yy = 0f
    var rowI = 0
    while (yy < size.height) {
        var xx = 0f
        var colI = 0
        while (xx < size.width) {
            drawRect(
                color = AntechamberTexture.cellTone(
                    base, StrongroomInks.Graphite,
                    AntechamberTexture.Species.BRASS, colI, rowI,
                    seed = seed, state = state
                ),
                topLeft = Offset(xx, yy),
                size = Size(cell + 1f, cell + 1f)
            )
            xx += cell; colI++
        }
        yy += cell; rowI++
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
    val lightState = LocalStrongroomLightState.current
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
        // The slip's leaf, grained (seeded by the slip itself).
        Canvas(Modifier.matchParentSize()) {
            grainLeaf(
                StrongroomLight.descend(StrongroomInks.Gold, 4),
                seed = 61 + slip.id, state = lightState
            )
        }
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
                            ) {
                                // The cover is brass leaf, grained —
                                // and MADE: a lit lip along its head,
                                // a shadow at its foot, a rivet at
                                // each end where it is fastened.
                                Canvas(Modifier.matchParentSize()) {
                                    grainLeaf(
                                        StrongroomInks.Gold,
                                        seed = 67, state = lightState
                                    )
                                    drawLine(
                                        color = StrongroomLight.relief(
                                            StrongroomInks.Gold,
                                            StrongroomInks.Graphite,
                                            0.65f, lightState
                                        ),
                                        start = Offset(0f, 1.2.dp.toPx()),
                                        end = Offset(size.width, 1.2.dp.toPx()),
                                        strokeWidth = 1.4.dp.toPx()
                                    )
                                    drawLine(
                                        color = StrongroomLight.descend(
                                            StrongroomInks.Gold, 3
                                        ),
                                        start = Offset(0f, size.height - 1.2.dp.toPx()),
                                        end = Offset(size.width, size.height - 1.2.dp.toPx()),
                                        strokeWidth = 1.4.dp.toPx()
                                    )
                                    boltHead(
                                        Offset(8.dp.toPx(), size.height / 2f),
                                        2.3.dp.toPx(), lightState
                                    )
                                    boltHead(
                                        Offset(size.width - 8.dp.toPx(), size.height / 2f),
                                        2.3.dp.toPx(), lightState
                                    )
                                }
                            }
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
                                else -> actions.onResolveHeld(slip, true)
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
                        .clickable { actions.onResolveHeld(slip, false) }
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
