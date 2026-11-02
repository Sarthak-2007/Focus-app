package com.example.ui.strongroom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.withFrameNanos
import com.example.domain.LiftingPlate
import com.example.ui.motion.DoorMotionPlayer
import com.example.ui.motion.WithdrawalBodies
import com.example.ui.strongroom.layout.RoomGrid
import com.example.ui.theme.RelightState
import com.example.ui.theme.PlateDarkRoom
import com.example.ui.theme.PlateDoorGround
import com.example.ui.theme.PlateInkGold
import com.example.ui.theme.PlateLineWork
import com.example.ui.theme.RoomListStyle
import kotlinx.coroutines.delay

/**
 * P6 — THE LIFTING, AS AN EVENT (BUILD-SR-4, Slice 1).
 *
 * Not a screen change. The ceremony runs [LiftingPlate]'s phase
 * machine and renders iron and weight:
 *
 *  1. BOLTS WITHDRAW, ONE BY ONE — three bolt bars slide out of
 *     the door, each reported outward via [onBoltWithdrawn] so the
 *     wiring can spend the lift voice
 *     ([com.example.service.StrongroomLift]) at the true beat.
 *     The bars' MOTION is MOTION-SR-1's withdrawal (BUILD-SR-18,
 *     Stage 12): strain (90 ms, the bolt loads) → snap → slide,
 *     heaviest body first, driven by [WithdrawalBodies] — one
 *     drive, three masses; never a reversed bolt spring. (P0's
 *     BoltSpring consumption that stood here is superseded by
 *     name, MOTION-SR-1 §6.)
 *  2. THE DOOR OPENS ON ITS OWN WEIGHT — once the last bolt has
 *     fully withdrawn (a bolt counts when its stroke completes,
 *     never before), the door swings on its hinge under its own
 *     weight: [DoorMotionPlayer]'s driven body (declining
 *     imbalance torque, braked late by its arrival band, settling
 *     against its stop; the strike fires when the travel MEETS
 *     the stop at θ = 81.2°, never on a timer). Slow commitment,
 *     long settle. Nothing fades; nothing slides; the door's
 *     angle is the whole transition. (P0's DoorWeightSpring
 *     consumption that stood here is superseded by name,
 *     MOTION-SR-1 §6.) All figures UNPROVEN-ON-DEVICE (G-7).
 *  3. THE HANDING BACK, FIRST — as an execution PRECONDITION,
 *     never a presented phase (R2-7, MOTION-SR-4's M-2 ratified):
 *     [onHandedBack] fires as the lift fires, before the first
 *     bolt completes, and the wiring returns the waiting people
 *     to his phone's OWN list — they reappear there, not on any
 *     Monolith surface; this screen presents no list of people.
 *     The plate remains unreachable until the delivery is
 *     confirmed into the machine (the settled door simply waits,
 *     presented and patient, if it ever came to that).
 *  4. THE PLATE — struck once at the lifting (by the model/wiring;
 *     this screen only ever READS a struck plate), offered for
 *     reading, owed never (B-15): one touch marks it read.
 *
 * A FALLEN DAY has no ceremony: [failure] stands the testimony
 * face in its dark room from the first frame — no bolts, no
 * opening, no voice — with its bracket-struck plate beneath.
 * The testimony face is P3's seal-gone form (B-11 / R7-1,
 * consumed here per the ruling — P6 builds no face of its own):
 * when the wiring supplies [failureFace], the fallen branch
 * renders [SealedFaceScreen] in that form, in the DARK light of
 * the dark room, with both of its callbacks standing as no-ops —
 * testimony, never a gate (R2-3): a reach on it knocks nothing
 * and its grille opens nothing. The production composition
 * supplies the form from the day's snapshot and its plate's
 * bracket hours on every fallen day (BUILD-SR-23, step 7); this
 * lane's earlier ajar placeholder (AjarDoorFace) stood as the
 * branch's fallback until that supply landed and is RETIRED —
 * no fallback face stands. This surface is testimony only: the phone was
 * already released at the seal's death (R2-3), and the handing
 * back runs as the same precondition act — the failure is the
 * seal's, never the waiting people's, and neither is collected
 * as a toll here.
 *
 * Wiring seams (named): the seal's firing, the strike itself, the
 * delivery of the handing back into the phone's own list, and the
 * lift voice's performer are P2/P9's — this surface receives a
 * struck plate and a prepared handing back, and reports its beats
 * outward. Device claims UNPROVEN-ON-DEVICE (Audit A G-7).
 */
@Composable
fun LiftingScreen(
    plate: LiftingPlate.StruckPlate?,
    handingBack: LiftingPlate.HandingBack,
    failure: Boolean = false,
    failureFace: SealedFaceState? = null,
    onHandedBack: (LiftingPlate.HandingBack) -> Unit = {},
    onBoltWithdrawn: (Int) -> Unit = {},
    onPlateStrike: () -> Unit = {},
    onDoorSettled: () -> Unit = {},
    onPlateRead: (LiftingPlate.StruckPlate) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var ceremony by remember {
        mutableStateOf(
            if (failure) LiftingPlate.CeremonyState.beginAjar(handingBack)
            else LiftingPlate.CeremonyState.begin(handingBack)
        )
    }
    var doorSettledFired by remember { mutableStateOf(false) }

    fun fire(event: LiftingPlate.LiftEvent) {
        ceremony = LiftingPlate.advance(ceremony, event)
    }
    var plateStrikeFired by remember { mutableStateOf(false) }

    // THE MOTION CLOCK (Stage 12, BUILD-SR-18): one frame clock for
    // this surface; every pose below is a pure function of it under
    // MOTION-SR-1's bodies (DoorMotionPlayer). Poses are functions,
    // states are facts — a rebuild recomputes the pose the clock
    // dictates; it never restarts, freezes, or completes a travel.
    val doorPlayer = remember { DoorMotionPlayer() }
    var ceremonyElapsedMs by remember { mutableStateOf(0L) }
    LaunchedEffect(failure) {
        if (failure) return@LaunchedEffect
        var startNanos = -1L
        while (true) {
            val frameNanos = withFrameNanos { it }
            if (startNanos < 0L) startNanos = frameNanos
            ceremonyElapsedMs = (frameNanos - startNanos) / 1_000_000L
        }
    }
    // The door's own clock starts when its phase opens; its angle,
    // its strike and its settle are all read off the driven body.
    var doorStartMs by remember { mutableStateOf<Long?>(null) }
    val doorAngleDeg: Float = if (
        ceremony.phase == LiftingPlate.LiftPhase.DOOR_OPENING ||
        ceremony.phase == LiftingPlate.LiftPhase.PLATE ||
        ceremony.phase == LiftingPlate.LiftPhase.OPEN
    ) {
        if (doorStartMs == null) doorStartMs = ceremonyElapsedMs
        doorPlayer.openSwing().thetaAt(ceremonyElapsedMs - (doorStartMs ?: ceremonyElapsedMs))
    } else 0f
    LaunchedEffect(ceremonyElapsedMs) {
        val start = doorStartMs ?: return@LaunchedEffect
        if (ceremony.phase != LiftingPlate.LiftPhase.DOOR_OPENING) return@LaunchedEffect
        val local = ceremonyElapsedMs - start
        val swing = doorPlayer.openSwing()
        if (!plateStrikeFired && swing.strikeMs != null && local >= swing.strikeMs) {
            plateStrikeFired = true
            onPlateStrike()
        }
        if (!doorSettledFired && swing.settleMs != null && local >= swing.settleMs) {
            doorSettledFired = true
            onDoorSettled()
            fire(LiftingPlate.LiftEvent.DOOR_SETTLED)
        }
    }


    // THE PRECONDITION, executed first: as the lift fires, the
    // waiting people are handed back (the wiring performs the
    // return to his phone's own list on this callback). This is
    // deliberately the screen's first effect — it precedes the
    // bolt score below, and nothing in the layout presents it.
    LaunchedEffect(handingBack) {
        onHandedBack(handingBack)
        fire(LiftingPlate.LiftEvent.HANDED_BACK)
    }

    // The bolt score: fire the lifting, then withdraw each bolt at
    // its figure; a bolt is reported when its stroke COMPLETES.
    LaunchedEffect(failure) {
        if (failure) return@LaunchedEffect
        delay(240)
        fire(LiftingPlate.LiftEvent.LIFT_FIRED)
        var clock = 0
        for ((index, bolt) in LiftingPlate.LiftingScore.BOLTS.withIndex()) {
            val completesAt = bolt.atMs + bolt.durationMs
            delay((completesAt - clock).coerceAtLeast(0).toLong())
            clock = completesAt
            onBoltWithdrawn(index)
            fire(LiftingPlate.LiftEvent.BOLT_WITHDRAWN)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PlateDarkRoom)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp,
                vertical = RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp
            )
            .testTag("lifting_screen")
    ) {
        if (failure) {
            if (failureFace != null) {
                // P3's seal-gone form, consumed (R7-1): the same
                // face, the seal absent, the bracket pair at the
                // hour's rank — and nothing added. Its callbacks
                // are no-ops by construction here: testimony is
                // never a gate, a knock, or a route onward.
                SealedFaceScreen(
                    state = failureFace,
                    lightState = RelightState.DARK,
                    onKnockReach = {},
                    onAntechamberRequest = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(640.dp)
                )
            }
            // No fallback face stands here (BUILD-SR-23, step 7):
            // AjarDoorFace, P6's placeholder, is RETIRED — the
            // production host supplies failureFace on every fallen
            // day, and a fallen branch with no form supplied
            // renders the dark room and its plate alone rather
            // than a placeholder door.
        } else {
            DoorAndBolts(
                boltsWithdrawn = ceremony.boltsWithdrawn,
                doorAngleDeg = doorAngleDeg,
                ceremonyElapsedMs = ceremonyElapsedMs
            )
        }

        // No handing-back list is presented here — R2-7: the
        // people are back in HIS phone's own list, which is where
        // he finds them. This surface's next thing is the plate.

        // The plate: readable only where the machine allows it.
        if (plate != null && ceremony.plateReadable) {
            Spacer(Modifier.height(RoomGrid.dp(RoomGrid.SECOND_STEP_BAND_STEPS).dp))
            Box(
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!plate.isRead) onPlateRead(plate)
                        fire(LiftingPlate.LiftEvent.PLATE_READ)
                    }
                    .testTag("lifting_plate_gate")
            ) {
                DayPlateView(plate = plate)
            }
            // B-15's other arm, one quiet touch (CRITIC-SR-6
            // DF-10): leave the plate unread — it passes to the
            // pile, the day closes, and nothing is owed. The
            // plate is never badged or counted for this anywhere.
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Leave it unread",
                style = RoomListStyle,
                color = PlateInkGold,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { fire(LiftingPlate.LiftEvent.PLATE_LEFT_UNREAD) }
                    .padding(vertical = 6.dp)
                    .testTag("lifting_leave_unread")
            )
        }
    }
}

/** The door, ajar on no hinge but its own: violet ground, gold
 *  pinstripe, and its three bolts — each sliding out of the door's
 *  edge on MOTION-SR-1's withdrawal drive (strain → snap → slide,
 *  heaviest first; the withdrawal's local clock starts 240 ms in,
 *  spaced by the spec's 500 ms snap-to-snap evenness). The door's
 *  rotation is the driven body's angle itself, in degrees. */
@Composable
private fun DoorAndBolts(boltsWithdrawn: Int, doorAngleDeg: Float, ceremonyElapsedMs: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(430.dp)
            .testTag("lifting_door")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = -doorAngleDeg
                    transformOrigin = TransformOrigin(0f, 0.5f)
                    cameraDistance = 14f * density
                }
                .background(PlateDoorGround)
        ) {
            // The gold pinstripe, inset — the kept finish.
            Box(
                Modifier
                    .offset(x = 18.dp)
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(PlateInkGold.copy(alpha = 0.75f))
            )
            // The bolts: top, middle, foot — withdrawn in score order.
            val boltTops = listOf(54.dp, 200.dp, 346.dp)
            for (i in 0 until LiftingPlate.LiftingScore.BOLT_COUNT) {
                // Withdrawal i's driven fraction: strain holds the
                // bolt at 0 while it loads (90 ms), then the slide
                // runs its mass's travel. A bolt counted withdrawn
                // by the phase machine stands at exactly 1.
                val travel = if (i < boltsWithdrawn) 1f else WithdrawalBodies.fractionAt(
                    i, ceremonyElapsedMs - (240L + i * 500L)
                )
                Box(
                    Modifier
                        .offset(
                            x = (-118f * travel).dp,
                            y = boltTops[i]
                        )
                        .width(150.dp)
                        .height(26.dp)
                        .background(PlateLineWork)
                        .testTag("lifting_bolt_$i")
                )
            }
        }
    }
}
