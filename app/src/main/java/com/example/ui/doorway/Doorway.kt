package com.example.ui.doorway

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.LiftingPlate
import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import com.example.service.haptics.HapticEvent
import com.example.ui.foundation.DrivenDoor
import com.example.ui.foundation.LocalStrongroomLightState
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomLitSurface
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType
import com.example.ui.signing.CombinationSpin
import com.example.ui.signing.SigningFeel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * THE HOME SCREEN (HOME-FIRST, 2026-11-03 — Sarthak's redirect:
 * "Make the wireframe of the app first. One thing end to end
 * then next. Make home screen first. I don't want a static
 * page. Don't use this image, just model the home page vault
 * door after this.").
 *
 * The home is the vault door, MODELLED in code after the
 * seven-ink print — the print is the model, never the face (the
 * pasted-photo face is retired from this surface; the print
 * survives only as the app's icon, his separate order). The
 * whole place is drawn in code — no full-bleed bitmap, no
 * weight-stacked regions standing over an image — so what the
 * harness renders at a device's spec is what the device draws.
 *
 * The wireframe's five, in its order:
 *
 *  5. **The top plate** — his sentence (or its kept place, the
 *     purpose sentence standing in it) and the lifting hour,
 *     engraved on the door: the signing day's own record, read
 *     by the host ([DoorwayState.plateSentence] /
 *     [DoorwayState.plateLiftingText]).
 *  1. **The great wheel** — violet enamel, gold, graphite
 *     steel, grain as material ([DoorwayTexture] ENAMEL cells,
 *     per-cell, position-locked). It TURNS under drag with the
 *     signing spin's own law: governed ceiling 240°/s, stations
 *     at 36°, seats attempted at drag end, felt through
 *     [SigningFeel] into the one sensory bus (the host fires).
 *  2. **The combination dial** — at the door's lower-left rim:
 *     a graphite dial whose needle reads the station under the
 *     hand. Turning the wheel works the combination by hand —
 *     the [CombinationSpin] itself, the same mechanism the
 *     signing table works. A combination is a value nowhere:
 *     never stored, never shown whole (A-2); it is worked where
 *     he stands, and the signing's own spin at the table is
 *     worked there, by the same law.
 *  3. **SIGN TOMORROW** — the one big button: opens the signing
 *     table (the host's act). This is how the app is used:
 *     open app → home door → SIGN TOMORROW → set apps + lifting
 *     hour + combination → sign.
 *  4. **The drawer pull** — at the foot, under the gold
 *     threshold line: opens the past-days drawer (the host's
 *     act, with its DRAWER_OPEN voice as before).
 *
 * Yesterday's struck plate (C-06) still rests at the doorway
 * when a plate stands — the morning's record, between the door
 * and the button, exactly as the doorway has always carried it.
 *
 * Motion figures are the foundation's filed ones; all feel is
 * UNPROVEN-ON-DEVICE (G-7) until Sarthak's hand.
 */
data class DoorwayState(
    /** (1) The Composition standing at the open door, or null when
     *  the host has no standing day to show — the signing place
     *  then stands empty, as furniture does (GAP-R03 stillness). */
    val composition: DoorwayComposition? = null,
    /** (2) The plain line's formed fraction, 0..1 — extent, never
     *  a figure (C-05). Supplied by the host from the day's own
     *  record; 0 = the line stands unformed. */
    val plainLineFormedFraction: Float = 0f,
    /** (3) Whether the waiting slip stands at the doorway. */
    val waitingSlipPresent: Boolean = false,
    /** (4) Yesterday's struck plate resting at the doorway, or
     *  null when no plate was struck (no seal stood — B-14). */
    val plate: LiftingPlate.PlateTruth? = null,
    /** (5) The top plate's sentence: the signing day's sentence
     *  as signed, or its kept place (the purpose sentence) when
     *  the record carries it — null only when no day stands at
     *  all, when the plate itself shows the purpose sentence. */
    val plateSentence: String? = null,
    /** (5) The top plate's lifting hour, as his figure (HH:MM),
     *  from the signing day's record — null when no day stands. */
    val plateLiftingText: String? = null
) {
    companion object {
        /** The doorway with nothing standing in it: the door
         *  modelled and at rest, the place empty. Since the
         *  composition onto BUILD-SR-34's shell, this is a
         *  DERIVED result, never a hardcoded host state: it
         *  stands exactly when no composed day stands, nothing
         *  waits, and no plate rests at the doorway. */
        val AtRest = DoorwayState()

        /**
         * THE COMPOSITION (BUILD-SR-35, second act): the doorway's
         * state from the standing engine facts — the composed day
         * (mapped through [DoorwayComposition.fromComposedDay]),
         * the held-waiting fact, and yesterday's struck plate.
         * The plain line's fraction has no standing engine fact
         * behind it yet (its record is the desk clock's, in-session),
         * so it stands unformed rather than invented; the pencil-grey
         * loosening likewise has no standing source and stays absent.
         *
         * HOME-FIRST: the top plate's facts derive from the
         * SIGNING day ([signingDay] — tomorrow's standing row, the
         * table's own day; falls back to [composedDay]): its
         * sentence or kept place, and its lifting hour.
         */
        fun fromFacts(
            composedDay: StrongroomComposition.ComposedDay?,
            waitingSlipPresent: Boolean,
            plate: LiftingPlate.PlateTruth?,
            signingDay: StrongroomComposition.ComposedDay? = null
        ): DoorwayState {
            val plateDay = signingDay ?: composedDay
            return DoorwayState(
                composition = composedDay?.let { DoorwayComposition.fromComposedDay(it) },
                plainLineFormedFraction = 0f,
                waitingSlipPresent = waitingSlipPresent,
                plate = plate,
                plateSentence = plateDay?.record?.sentenceOrKeptPlace
                    ?: plateDay?.record?.purposeSentence,
                plateLiftingText = plateDay?.record?.liftingTime?.let {
                    "%02d:%02d".format(it.hour, it.minute)
                }
            )
        }
    }
}

/**
 * Yesterday's struck plate, re-expressed as the doorway strip's
 * truth ([LiftingPlate.PlateTruth]) from the store's plate shape
 * ([StrongroomDay.DayPlate]) — the two types mirror each other
 * field for field (P6's header), and this is the one mapping
 * between them at the doorway.
 *
 * Two named resolutions:
 *  - SHOWING punches (a code-showing handed a price, R7-2) strike
 *    at the HELD grade in P1's kind set and have no kind of their
 *    own in P6's: they map to HELD, their grade — the hole the
 *    strip draws is unchanged.
 *  - The bracket gap is NOT carried: the day row persists the
 *    gap's length but neither bracket hour, and [LiftingPlate
 *    .PlateTruth] forbids a gap without both hours — inventing
 *    them would fake a strike. The strip renders holes + the seal
 *    line only; the bracket pair in its struck form rides the
 *    plate strike's own slice, as this slice's first leg recorded.
 *    The held-hours figure rides with it, likewise unrendered here.
 */
fun plateTruthForDoorway(plate: StrongroomDay.DayPlate): LiftingPlate.PlateTruth {
    // A plate exists only once struck: a still-sealed day has no
    // plate truth to carry (B-14), and no seal state is invented here.
    require(plate.sealState != StrongroomDay.SealState.SEALED) {
        "a sealed day's plate is not struck yet"
    }
    return LiftingPlate.PlateTruth(
        filingDateIso = plate.filingDateIso,
        hoursHeldMillis = plate.hoursHeldMillis,
        punches = plate.punches.map { punch ->
            LiftingPlate.Punch(
                kind = when (punch.kind) {
                    StrongroomDay.PunchKind.REACH -> LiftingPlate.PunchKind.REACH
                    StrongroomDay.PunchKind.HELD,
                    StrongroomDay.PunchKind.SHOWING -> LiftingPlate.PunchKind.HELD
                    StrongroomDay.PunchKind.FLOOR -> LiftingPlate.PunchKind.FLOOR
                },
                atOffsetMillis = punch.atOffsetMillis,
                packageName = punch.packageName
            )
        },
        sealTruth = when (plate.sealState) {
            StrongroomDay.SealState.LIFTED -> LiftingPlate.SealTruth.WHOLE
            StrongroomDay.SealState.BROKEN -> LiftingPlate.SealTruth.BROKEN
            StrongroomDay.SealState.AJAR -> LiftingPlate.SealTruth.AJAR
            StrongroomDay.SealState.SEALED -> error("unreachable: guarded above")
        },
        crankSealBroken = plate.crankSealBroken,
        bracketGapMillis = 0L
    )
}

/** The Composition as the Doorway's sheet carries it (C-13). */
data class DoorwayComposition(
    /** The day-shape's name, exactly as signed (TX-DO-02…05). */
    val dayShapeWord: String,
    /** The task, in his own words (TX-SAY-07) — or null. */
    val taskLine: String?,
    /** A loosening proposal standing in pencil-grey
     *  (TX-SHOW-16) — or null. Grey, erasable, binding nothing. */
    val pencilGreyProposal: String?,
    /** The lifting hour he set, as his figure (HH:MM). */
    val liftingHourText: String,
    /** The locked things, as a count of objects only — the sheet
     *  renders one enamel chip per thing and never prints this
     *  number anywhere. */
    val lockedThings: Int,
    /** The in-span shaped gaps (gym · sadhana · meals), as a count
     *  of empty slots; meditation stands before the swing and
     *  sleep after the lifting, outside the sheet (ledger C-2). */
    val inSpanGaps: Int
) {
    companion object {
        /** Map a standing composed day (C-13) onto the sheet. The
         *  day-shape words are the record's own (TX-DO-02…05). */
        fun fromComposedDay(
            day: StrongroomComposition.ComposedDay,
            pencilGreyProposal: String? = null
        ): DoorwayComposition {
            val record = day.record
            return DoorwayComposition(
                dayShapeWord = when (day.shape) {
                    StrongroomComposition.DayShape.ORDINARY -> "ordinary day"
                    StrongroomComposition.DayShape.TEST -> "test day"
                    StrongroomComposition.DayShape.SMALL -> "the small day"
                    StrongroomComposition.DayShape.REST -> "the rest day"
                },
                taskLine = record.taskLine,
                pencilGreyProposal = pencilGreyProposal,
                liftingHourText = "%02d:%02d".format(
                    record.liftingTime.hour, record.liftingTime.minute
                ),
                lockedThings = record.lockedPackages.size,
                inSpanGaps = com.example.domain.StrongroomDay.inSpanGaps(record.gaps).size
            )
        }
    }
}

/**
 * The Door's entry settle, read off the foundation's driven body:
 * the swing-relative millisecond at which the body first passes
 * the end-stop's engagement (74°), and the millisecond at which
 * it settles against the stop. Kept as the foundation's filed
 * testimony of the door's weight (its tests stand on it); the
 * modelled home door itself is at rest until his hand works it.
 */
object DoorwaySwing {
    private val door = DrivenDoor()

    /** First integrated millisecond at or past 74°. */
    val StartMs: Int = (0..6000).firstOrNull { door.angleAt(it) >= 74.0 } ?: 0

    /** The body's own settle millisecond (filed testimony 3,246). */
    val SettledMs: Int = door.settledMs ?: 6000

    /** The entry travel's duration, in milliseconds. */
    val DurationMs: Int = (SettledMs - StartMs).coerceAtLeast(0)

    /** The door's angle at an entry-relative millisecond. */
    fun angleAt(entryMs: Int): Double =
        door.angleAt(StartMs + entryMs.coerceIn(0, DurationMs))
}

/** The home door's acts — every one of them the host's to carry
 *  out (the surface authors no navigation of its own). */
data class HomeDoorActions(
    /** (3) SIGN TOMORROW pressed: the host stands the signing
     *  table in this place. */
    val onSignTomorrow: () -> Unit,
    /** (4) The drawer pull drawn: the host opens the past-days
     *  drawer (and fires its DRAWER_OPEN voice, as before). */
    val onOpenDrawer: () -> Unit,
    /** The wheel's earned felt events (stations · gates · the
     *  final seat · the seated dial's wall), fired by the host
     *  through the bridge's one bus — the FEEL seam. */
    val onFeelEvent: (HapticEvent) -> Unit
)

/**
 * THE HOME DOOR — the wireframe's five, composed as one place.
 *
 * [initialRotationDeg] seeds the wheel's standing rotation (the
 * harness's mid-turn seam, the same pattern as the Break's
 * standing clock): production always enters at 0.
 */
@Composable
fun HomeDoor(
    state: DoorwayState,
    actions: HomeDoorActions,
    modifier: Modifier = Modifier,
    initialRotationDeg: Double = 0.0
) {
    val lightState = LocalStrongroomLightState.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StrongroomInks.Graphite)
            .semantics { contentDescription = "ULYSSES" }
    ) {
        Spacer(Modifier.height(20.dp))
        TopPlate(state, lightState)
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val doorSize = min(
                min(maxWidth.value * 0.94f, maxHeight.value),
                400f
            ).dp
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DoorAssembly(
                    size = doorSize,
                    lightState = lightState,
                    initialRotationDeg = initialRotationDeg,
                    onFeelEvent = actions.onFeelEvent
                )
            }
        }
        state.plate?.let {
            StruckPlateStrip(
                it,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(14.dp))
        }
        SignTomorrowButton(onClick = actions.onSignTomorrow)
        Spacer(Modifier.height(18.dp))
        // (4) The threshold: a gold line across the room, the
        // drawer's pull under it, its name in the telemetry hand.
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    StrongroomLight.blendEighth(
                        StrongroomInks.Gold, StrongroomInks.Graphite, 2
                    )
                )
        )
        Spacer(Modifier.height(14.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(width = 132.dp, height = 30.dp)
                    .border(2.dp, StrongroomInks.Gold, RoundedCornerShape(15.dp))
                    .clickable { actions.onOpenDrawer() }
                    .semantics { contentDescription = "PAST DAYS" },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(84.dp)
                        .height(2.dp)
                        .background(StrongroomInks.Gold)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "PAST DAYS",
                style = StrongroomType.Anon1,
                color = StrongroomLight.blendEighth(
                    StrongroomInks.Gold, StrongroomInks.Graphite, 4
                )
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}

// ------------------------------------------------------------------
// (5) The top plate — his sentence + the lifting hour, engraved
// ------------------------------------------------------------------

@Composable
private fun TopPlate(state: DoorwayState, lightState: StrongroomLightState) {
    val plateGround = StrongroomLight.stateSurface(
        StrongroomInks.Violet, lightState, StrongroomLitSurface.FACE
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .background(plateGround, RoundedCornerShape(6.dp))
            .border(1.dp, StrongroomInks.Gold, RoundedCornerShape(6.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // His sentence as signed — or, standing in its kept
        // place, the purpose sentence the record itself carries.
        Text(
            text = state.plateSentence ?: StrongroomDay.PURPOSE_SENTENCE,
            style = StrongroomType.Scr2,
            color = StrongroomInks.Gold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        state.plateLiftingText?.let {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "LIFTS $it",
                style = StrongroomType.Anon2,
                color = StrongroomInks.Gold
            )
        }
    }
}

// ------------------------------------------------------------------
// (1) + (2) The door — the great wheel and the combination dial
// ------------------------------------------------------------------

@Composable
private fun DoorAssembly(
    size: androidx.compose.ui.unit.Dp,
    lightState: StrongroomLightState,
    initialRotationDeg: Double,
    onFeelEvent: (HapticEvent) -> Unit
) {
    val spin = remember { CombinationSpin() }
    val feel = remember { SigningFeel() }
    var rotation by remember { mutableStateOf(initialRotationDeg) }
    var lastVelocity by remember { mutableStateOf(0.0) }
    var seats by remember { mutableStateOf(0) }

    Box(modifier = Modifier.size(size)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    var lastAngle: Double? = null
                    var lastMs: Long = 0L
                    detectDragGestures(
                        onDragStart = { lastAngle = null },
                        onDragEnd = {
                            if (spin.isComplete) {
                                // A seat attempt against the
                                // seated dial: its flat wall.
                                feel.onAttemptAgainstSeatedDial()
                                    .forEach(onFeelEvent)
                            } else {
                                val clockwise = lastVelocity > 0.0
                                if (spin.attemptSeat(rotation, lastVelocity, clockwise)) {
                                    seats = spin.seatsTaken
                                    feel.onSeatTaken(finalSeat = spin.isComplete)
                                        .forEach(onFeelEvent)
                                }
                            }
                        },
                        onDrag = { change, _ ->
                            val cx = this.size.width / 2.0
                            val cy = this.size.height / 2.0
                            val angle = Math.toDegrees(
                                atan2(change.position.y - cy, change.position.x - cx)
                            )
                            val prev = lastAngle
                            if (prev != null) {
                                var delta = angle - prev
                                if (delta > 180.0) delta -= 360.0
                                if (delta < -180.0) delta += 360.0
                                // The governed ceiling: a dial that
                                // cannot be flicked (240°/s, filed).
                                val dt = ((change.uptimeMillis - lastMs)
                                    .coerceAtLeast(1)) / 1000.0
                                var velocity = delta / dt
                                if (velocity > 240.0) velocity = 240.0
                                if (velocity < -240.0) velocity = -240.0
                                lastVelocity = velocity
                                rotation += velocity * dt
                                // The travel the mechanism accepted
                                // is the travel the hand feels.
                                feel.onTravel(rotation).forEach(onFeelEvent)
                            }
                            lastAngle = angle
                            lastMs = change.uptimeMillis
                            change.consume()
                        }
                    )
                }
        ) {
            // The wheel turns over with the seats: its drawn
            // rotation is the spin's own turnover (120° per seat)
            // plus the hand's live travel within the pass — the
            // signing's law, carried whole (A-16).
            val drawn = (spin.wheelTurnoverDeg + rotation % 360.0).toFloat()
            Canvas(Modifier.fillMaxSize()) {
                val cx = this.size.width / 2f
                val cy = this.size.height / 2f
                val r = this.size.minDimension / 2f

                // The enamelled leaf: violet enamel with its own
                // grain — fine cells, position-locked, clipped to
                // the leaf (the plate brass's proven pattern).
                val discPath = Path().apply {
                    addOval(Rect(Offset(cx - r, cy - r), Size(2f * r, 2f * r)))
                }
                clipPath(discPath) {
                    drawRect(color = StrongroomInks.Violet)
                    val cell = DoorwayTexture.Species.ENAMEL.cellPx.dp.toPx()
                    var rowI = 0
                    var yy = cy - r
                    while (yy < cy + r) {
                        var colI = 0
                        var xx = cx - r
                        while (xx < cx + r) {
                            val tone = DoorwayTexture.cellTone(
                                StrongroomInks.Violet, StrongroomInks.Graphite,
                                DoorwayTexture.Species.ENAMEL, colI, rowI,
                                seed = 41, state = lightState
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
                }
                // The gold furniture of the leaf: the outer ring
                // and the hand-laid pinstripe inside it.
                drawCircle(
                    color = StrongroomInks.Gold,
                    radius = r - 2.dp.toPx(),
                    style = Stroke(width = 3.dp.toPx())
                )
                drawCircle(
                    color = StrongroomLight.blendEighth(
                        StrongroomInks.Gold, StrongroomInks.Graphite, 2
                    ),
                    radius = r - 13.dp.toPx(),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                // The bolt studs around the rim — twelve heads of
                // graphite steel on the ring, gold-rimmed.
                for (i in 0 until 12) {
                    val a = Math.toRadians(i * 30.0 - 90.0)
                    val sx = cx + (r - 8.dp.toPx()) * cos(a).toFloat()
                    val sy = cy + (r - 8.dp.toPx()) * sin(a).toFloat()
                    drawCircle(
                        color = StrongroomInks.Gold,
                        radius = 6.5.dp.toPx(),
                        center = Offset(sx, sy)
                    )
                    drawCircle(
                        color = StrongroomRoles.Metal,
                        radius = 5.dp.toPx(),
                        center = Offset(sx, sy)
                    )
                    drawCircle(
                        color = StrongroomLight.blendEighth(
                            StrongroomInks.Graphite, StrongroomInks.Violet, 4
                        ),
                        radius = 1.6.dp.toPx(),
                        center = Offset(sx - 1.5.dp.toPx(), sy - 1.5.dp.toPx())
                    )
                }

                // THE GREAT WHEEL — turning with the spin.
                val wheelR = r * 0.52f
                withTransform({ rotate(drawn, Offset(cx, cy)) }) {
                    // The steel race the wheel runs in.
                    drawCircle(
                        color = StrongroomRoles.Metal,
                        radius = wheelR + 9.dp.toPx(),
                        style = Stroke(width = 5.dp.toPx())
                    )
                    // The wheel's gold rim.
                    drawCircle(
                        color = StrongroomInks.Gold,
                        radius = wheelR,
                        style = Stroke(width = 6.dp.toPx())
                    )
                    // Its three spokes.
                    for (spoke in 0 until 3) {
                        val a = Math.toRadians(spoke * 120.0 - 90.0).toFloat()
                        drawLine(
                            color = StrongroomInks.Gold,
                            start = Offset(cx, cy),
                            end = Offset(
                                cx + wheelR * cos(a),
                                cy + wheelR * sin(a)
                            ),
                            strokeWidth = 11.dp.toPx()
                        )
                    }
                    // A gold index head on the rim, so the turn
                    // itself is visible at a glance.
                    drawCircle(
                        color = StrongroomInks.Gold,
                        radius = 7.dp.toPx(),
                        center = Offset(cx, cy - wheelR)
                    )
                    // The hub: graphite steel, gold-ringed.
                    drawCircle(
                        color = StrongroomRoles.Metal,
                        radius = 25.dp.toPx()
                    )
                    drawCircle(
                        color = StrongroomInks.Gold,
                        radius = 25.dp.toPx(),
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }
                drawCircle(
                    color = StrongroomInks.Gold,
                    radius = 3.dp.toPx()
                )
            }
            // The station under the hand — one digit, read at the
            // hub; the combination itself is never shown whole,
            // never stored, never written (A-2).
            Box(
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${spin.stationAt(rotation)}",
                    style = StrongroomType.Anon4,
                    color = StrongroomRoles.OnGround
                )
            }
        }

        // (2) THE COMBINATION DIAL — overlapping the leaf's
        // lower-left rim, as the wireframe stands it: a graphite
        // dial, twelve teeth, its needle reading the station the
        // wheel stands at.
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = size * 0.03f, y = size * 0.02f)
                .size(size * 0.21f)
        ) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val r = this.size.minDimension / 2f
            drawCircle(color = StrongroomRoles.Metal, radius = r)
            drawCircle(
                color = StrongroomInks.Gold,
                radius = r - 1.dp.toPx(),
                style = Stroke(width = 2.dp.toPx())
            )
            // The teeth: twelve, the stations' own count at
            // reading scale.
            for (i in 0 until 12) {
                val a = Math.toRadians(i * 30.0 - 90.0).toFloat()
                val inner = r - 9.dp.toPx()
                drawLine(
                    color = StrongroomInks.Gold,
                    start = Offset(cx + inner * cos(a), cy + inner * sin(a)),
                    end = Offset(
                        cx + (r - 3.dp.toPx()) * cos(a),
                        cy + (r - 3.dp.toPx()) * sin(a)
                    ),
                    strokeWidth = 1.6.dp.toPx()
                )
            }
            // The needle, standing at the station under the hand.
            val station = spin.stationAt(rotation)
            val na = Math.toRadians(station * 36.0 - 90.0).toFloat()
            drawLine(
                color = StrongroomInks.Gold,
                start = Offset(cx, cy),
                end = Offset(
                    cx + (r - 12.dp.toPx()) * cos(na),
                    cy + (r - 12.dp.toPx()) * sin(na)
                ),
                strokeWidth = 3.dp.toPx()
            )
            drawCircle(color = StrongroomInks.Gold, radius = 3.5.dp.toPx())
        }
    }

    // The seats taken, as three bolt heads filling under the
    // door — the combination's progress is furniture, not a
    // counter (the signing's own telling, carried).
    Row(
        modifier = Modifier.padding(top = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until 3) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .size(12.dp)
                    .background(
                        if (i < seats) StrongroomInks.Gold
                        else StrongroomLight.descend(StrongroomInks.Graphite, 1),
                        CircleShape
                    )
            )
        }
    }
}

// ------------------------------------------------------------------
// (3) SIGN TOMORROW — the one big button
// ------------------------------------------------------------------

@Composable
private fun SignTomorrowButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(58.dp)
            .background(StrongroomInks.Gold, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .semantics { contentDescription = "SIGN TOMORROW" },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SIGN TOMORROW",
            style = StrongroomType.Scr3.copy(fontWeight = FontWeight.Bold),
            color = StrongroomInks.Graphite
        )
    }
}

// ------------------------------------------------------------------
// The press — yesterday's struck plate at the doorway (C-06)
// ------------------------------------------------------------------

@Composable
private fun StruckPlateStrip(
    plate: LiftingPlate.PlateTruth,
    modifier: Modifier = Modifier
) {
    val lightState = LocalStrongroomLightState.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(StrongroomInks.Gold, RoundedCornerShape(3.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Brass grain, from the plate's own gold.
            val cell = DoorwayTexture.Species.BRASS.cellPx.dp.toPx()
            var cy = 0
            var yy = 0f
            while (yy < size.height) {
                var cx = 0
                var xx = 0f
                while (xx < size.width) {
                    val tone = DoorwayTexture.cellTone(
                        StrongroomInks.Gold, StrongroomInks.Graphite,
                        DoorwayTexture.Species.BRASS, cx, cy,
                        seed = 23, state = lightState
                    )
                    drawRect(
                        color = tone, topLeft = Offset(xx, yy),
                        size = Size(cell + 1f, cell + 1f)
                    )
                    xx += cell; cx++
                }
                yy += cell; cy++
            }
            // The punched holes, in striking order, at their graded
            // diameters — a finger could learn them (ledger E-9;
            // figures from the desk clock's drawing constants:
            // reach 11 · held 7.5 · floor 4.5 dp).
            val punches = plate.punchesInOrder
            val gap = 16.dp.toPx()
            var hx = 16.dp.toPx()
            val midY = size.height / 2f
            for (punch in punches) {
                val d = when (punch.kind.grade) {
                    LiftingPlate.PunchGrade.LARGE -> 11.dp.toPx()
                    LiftingPlate.PunchGrade.MEDIUM -> 7.5.dp.toPx()
                    LiftingPlate.PunchGrade.SMALL -> 4.5.dp.toPx()
                }
                if (hx + d > size.width * 0.62f) break
                drawCircle(
                    color = StrongroomInks.Graphite, radius = d / 2f,
                    center = Offset(hx + d / 2f, midY)
                )
                hx += d + gap
            }
        }
        // The seal line, in exactly its two lawful states
        // (TX-SHOW-13) — struck words on the brass, in graphite.
        Text(
            text = if (plate.sealTruth == LiftingPlate.SealTruth.WHOLE) {
                "seal whole"
            } else {
                "seal broken"
            },
            style = StrongroomType.Anon1,
            color = StrongroomInks.Graphite,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp)
        )
    }
}
