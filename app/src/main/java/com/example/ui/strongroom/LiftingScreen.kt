package com.example.ui.strongroom

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
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
import com.example.domain.LiftingPlate
import com.example.ui.theme.PlateDarkRoom
import com.example.ui.theme.PlateDoorGround
import com.example.ui.theme.PlateInkGold
import com.example.ui.theme.PlateLineWork
import com.example.ui.theme.RoomHeadingStyle
import com.example.ui.theme.RoomListStyle
import kotlinx.coroutines.delay

/**
 * P6 — THE LIFTING, AS AN EVENT (BUILD-SR-4, Slice 1).
 *
 * Not a screen change. The ceremony runs [LiftingPlate]'s phase
 * machine and renders iron and weight:
 *
 *  1. BOLTS WITHDRAW, ONE BY ONE — three bolt bars slide out of
 *     the door on the bolt spring, each at its score figure
 *     (P0's LiftClunks by citation), each reported outward via
 *     [onBoltWithdrawn] so the wiring can spend the lift voice
 *     ([com.example.service.StrongroomLift]) at the true beat.
 *  2. THE DOOR OPENS ON ITS OWN WEIGHT — once the last bolt has
 *     fully withdrawn (a bolt counts when its stroke completes,
 *     never before), the door swings on its hinge under P0's
 *     DoorWeightSpring (damping 0.86, stiffness 55): slow
 *     commitment, long settle. Nothing fades; nothing slides;
 *     the door's angle is the whole transition.
 *  3. THE HANDING BACK, FIRST — with the door open, the people
 *     who waited are presented and delivered back to his phone's
 *     own list ([onHandedBack]) BEFORE the plate can be read.
 *     The phase machine makes the plate unreachable before this;
 *     the screen's callback order proves it: onHandedBack fires
 *     strictly before any plate is composed.
 *  4. THE PLATE — struck once at the lifting (by the model/wiring;
 *     this screen only ever READS a struck plate), offered for
 *     reading, owed never (B-15): one touch marks it read.
 *
 * A FALLEN DAY has no ceremony: [failure] stands the ajar face
 * in its dark room from the first frame — no bolts, no opening,
 * no voice — with its bracket-struck plate beneath. Even there,
 * the handing back is delivered before the room is his again:
 * the failure is the seal's, not the waiting people's.
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
    onHandedBack: (LiftingPlate.HandingBack) -> Unit = {},
    onBoltWithdrawn: (Int) -> Unit = {},
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
    var handedBackFired by remember { mutableStateOf(false) }

    fun fire(event: LiftingPlate.LiftEvent) {
        ceremony = LiftingPlate.advance(ceremony, event)
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

    // The door's weight: a spring, not a tween — mass with a long
    // settle, exactly P0's DoorWeightSpring figures.
    val doorTravel by animateFloatAsState(
        targetValue = if (ceremony.phase == LiftingPlate.LiftPhase.DOOR_OPENING ||
            ceremony.phase == LiftingPlate.LiftPhase.HANDING_BACK ||
            ceremony.phase == LiftingPlate.LiftPhase.PLATE ||
            ceremony.phase == LiftingPlate.LiftPhase.OPEN
        ) 1f else 0f,
        animationSpec = SpringSpec(
            dampingRatio = LiftingPlate.LiftingScore.DOOR_SPRING_DAMPING,
            stiffness = LiftingPlate.LiftingScore.DOOR_SPRING_STIFFNESS
        ),
        label = "doorWeight"
    )
    LaunchedEffect(doorTravel) {
        if (!doorSettledFired && ceremony.phase == LiftingPlate.LiftPhase.DOOR_OPENING && doorTravel > 0.985f) {
            doorSettledFired = true
            onDoorSettled()
            fire(LiftingPlate.LiftEvent.DOOR_SETTLED)
        }
    }

    // The handing back, delivered at its phase — before the plate.
    LaunchedEffect(ceremony.phase) {
        if (handedBackFired) return@LaunchedEffect
        if (ceremony.phase == LiftingPlate.LiftPhase.HANDING_BACK || ceremony.phase == LiftingPlate.LiftPhase.AJAR) {
            delay(600)
            handedBackFired = true
            onHandedBack(handingBack)
            fire(LiftingPlate.LiftEvent.HANDED_BACK)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PlateDarkRoom)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp)
            .testTag("lifting_screen")
    ) {
        if (failure) {
            AjarDoorFace()
        } else {
            DoorAndBolts(boltsWithdrawn = ceremony.boltsWithdrawn, doorTravel = doorTravel)
        }

        // The handing back stands before the plate — in the
        // layout's order as in the machine's.
        if (!handingBack.isEmpty &&
            (ceremony.phase == LiftingPlate.LiftPhase.HANDING_BACK ||
                ceremony.phase == LiftingPlate.LiftPhase.AJAR ||
                ceremony.phase == LiftingPlate.LiftPhase.PLATE ||
                ceremony.phase == LiftingPlate.LiftPhase.OPEN)
        ) {
            Spacer(Modifier.height(26.dp))
            Column(Modifier.testTag("handing_back")) {
                Text(
                    text = "Handed back",
                    style = RoomHeadingStyle,
                    color = PlateInkGold
                )
                Spacer(Modifier.height(8.dp))
                for (person in handingBack.waiting) {
                    Text(
                        text = person.displayName,
                        style = RoomListStyle,
                        color = PlateInkGold,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
        }

        // The plate: readable only where the machine allows it.
        if (plate != null && ceremony.plateReadable) {
            Spacer(Modifier.height(26.dp))
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
 *  edge as its stroke completes, on the bolt spring. */
@Composable
private fun DoorAndBolts(boltsWithdrawn: Int, doorTravel: Float) {
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
                    rotationY = -64f * doorTravel
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
                val travel by animateFloatAsState(
                    targetValue = if (i < boltsWithdrawn) 1f else 0f,
                    animationSpec = SpringSpec(
                        dampingRatio = LiftingPlate.LiftingScore.BOLT_SPRING_DAMPING,
                        stiffness = LiftingPlate.LiftingScore.BOLT_SPRING_STIFFNESS
                    ),
                    label = "bolt$i"
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
