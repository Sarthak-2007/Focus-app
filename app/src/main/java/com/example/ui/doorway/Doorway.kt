package com.example.ui.doorway

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.LiftingPlate
import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import com.example.ui.foundation.DrivenDoor
import com.example.ui.foundation.LocalStrongroomLightState
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomType

/**
 * C-21 · THE DOORWAY / THE GLANCE — the app's first screen, and
 * C-01's open state: the Door standing open in its world, the
 * signing place composed before it.
 *
 * Built by BUILD-SR-35 (T24) from the rebuild element census
 * (INVENTORY-SR-1), firsthand. The Doorway's count is FOUR and no
 * more (ledger B-17, census C-21):
 *
 *  1. **The sheet on the press** — the Composition standing at the
 *     open door (C-13): tomorrow already composed from the week's
 *     standing order, standing to be amended at the signing. Its
 *     words are only its own row's: the day-shape's name as signed
 *     (TX-DO-02…05), the task in his own words (TX-SAY-07), the
 *     lifting hour he set, a pencil-grey loosening where one
 *     stands (TX-SHOW-16). The locked things stand as enamel chips
 *     — objects, not a list; the shaped gaps stand as shaped
 *     empty slots — deliberate empty spaces, never openings.
 *  2. **One plain line** — the desk clock's in-room telling
 *     carried at the doorway (C-05's numberless record in its
 *     Glance form): one line, its formed part gold, carrying zero
 *     numerals, zero words. The fallen day's hour-form for this
 *     line stands GAP-COORD-07 and is NOT authored here.
 *  3. **The waiting slip** — the held waiting (C-14), present as a
 *     slip when anyone or anything waits, absent when nothing
 *     does. It carries no count, no name, no dot and no word: the
 *     held pile's own words live on the held surface, not here.
 *  4. **The press** — the striking (C-06): yesterday's struck
 *     brass plate resting at the doorway through the unsealed
 *     morning (IL U-03), its punched holes graded as struck and
 *     its seal line in exactly its two lawful states
 *     (TX-SHOW-13). No plate stands when no seal stood (B-14) —
 *     the tray side is simply empty.
 *
 * THE DOOR (C-01, its Doorway share): the seven-ink print — the
 * real vault-door photograph recoloured into the room's inks,
 * Sarthak's own pick, cleared by his eye — standing as the whole
 * surface through [DoorFace], which the host lays behind this
 * place edge to edge. The open Door carries NO printings: the
 * four printings (C-12) are cut at the closing onto the SEALED
 * face; an open door has nothing engraved to show, and the
 * door-word's slot (GAP-COORD-01) is not this surface's to fill
 * or to fake. (The flat drawn door that stood here before — 7dp
 * enamel grain cells, a leaf drawn at 26% of the room's width —
 * was killed by Sarthak's eye on his phone, 2026-11-03: "Even
 * that tiny thing is ugly." The print is the face; no drawn
 * stand-in stands in for the door again.)
 *
 * Texture: the print IS the texture — a real door's enamel,
 * steel and brass at photographic grain. The one drawn grain
 * left on this surface is the struck plate's brass
 * ([DoorwayTexture] BRASS), a kept record's own material.
 *
 * Motion, in physics vocabulary (foundation primitives only):
 * on entry [DoorFace] completes the last of the door's
 * weight-driven travel — [DrivenDoor]'s angle read through
 * [DoorwaySwing], spent as the whole face's settle. Nothing
 * else on this surface moves: the Doorway is a place at rest,
 * and dopamine lives at passive moments only (ledger E-5).
 * All motion figures UNPROVEN-ON-DEVICE (foundation head, G-7).
 *
 * This surface authors no navigation, no settings, no greeting,
 * no summary and no prompt (census GAP-R01/R02/R14): the doorway
 * adds no word of its own beyond its four furniture pieces' words.
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
    val plate: LiftingPlate.PlateTruth? = null
) {
    companion object {
        /** The doorway with nothing standing in it: the door open,
         *  the place empty. Since the composition onto BUILD-SR-34's
         *  shell, this is a DERIVED result, never a hardcoded host
         *  state: it stands exactly when no composed day stands,
         *  nothing waits, and no plate rests at the doorway. */
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
         */
        fun fromFacts(
            composedDay: StrongroomComposition.ComposedDay?,
            waitingSlipPresent: Boolean,
            plate: LiftingPlate.PlateTruth?
        ): DoorwayState = DoorwayState(
            composition = composedDay?.let { DoorwayComposition.fromComposedDay(it) },
            plainLineFormedFraction = 0f,
            waitingSlipPresent = waitingSlipPresent,
            plate = plate
        )
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
 * it settles against the stop. The Doorway plays only this last
 * travel — the door was already open; what entry shows is its
 * weight finishing, never a replayed lifting (C-17 is another
 * surface's event).
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

@Composable
fun Doorway(state: DoorwayState, modifier: Modifier = Modifier) {
    val lightState = LocalStrongroomLightState.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "ULYSSES" },
        verticalArrangement = Arrangement.Bottom
    ) {
        // The signing place, in the Doorway's count order (C-21),
        // standing at the foot of the door face — the host lays
        // [DoorFace] behind this surface whole, and these four
        // pieces stand on the print.
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            state.composition?.let { CompositionSheet(it, lightState) }
            Spacer(Modifier.height(14.dp))
            PlainLine(state.plainLineFormedFraction)
            if (state.waitingSlipPresent) {
                Spacer(Modifier.height(10.dp))
                WaitingSlip(modifier = Modifier.align(Alignment.End))
            }
            state.plate?.let {
                Spacer(Modifier.height(14.dp))
                StruckPlateStrip(it)
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

// ------------------------------------------------------------------
// The Door face (C-01, Doorway share) — THE SEVEN-INK PRINT
// ------------------------------------------------------------------

/**
 * THE FACE: the seven-ink print — the real vault-door photograph
 * recoloured into the room's seven inks, picked and cleared by
 * Sarthak's own eye (2026-11-01) — standing as the opening
 * surface's whole ground: full-bleed, cropped to the screen's
 * reach, the door IS the surface. The host lays this behind the
 * doorway place whole; every decided element of the place (the
 * sheet, the line, the slip, the plate, the swing act, the
 * signing table, the drawer front) stands on it. No drawn
 * stand-in: the flat vector door this replaced read as a toy,
 * and shrunk into the room's top corner it read as a bug
 * (Sarthak's phone, 2026-11-03 03:35 IST).
 *
 * Motion: on entry the face completes the last of the door's
 * weight-driven travel — the foundation's driven body
 * ([DrivenDoor], its angle read through [DoorwaySwing]) spent
 * as a settle of the whole face: a breath of scale off the
 * print and a few dp of downward ease as it comes onto its
 * stop. The print itself never redraws; the body only
 * projects its angle onto the face's carriage.
 */
@Composable
fun DoorFace(modifier: Modifier = Modifier) {
    val door = remember { DrivenDoor() }
    var entryMs by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        val start = withFrameMillis { it }
        while (entryMs < DoorwaySwing.DurationMs) {
            val now = withFrameMillis { it }
            entryMs = (now - start).toInt().coerceAtMost(DoorwaySwing.DurationMs)
        }
    }
    val angle = door.angleAt(DoorwaySwing.StartMs + entryMs)
    val startAngle = door.angleAt(DoorwaySwing.StartMs)
    val travelFraction = if (door.finalDeg > startAngle) {
        ((door.finalDeg - angle) / (door.finalDeg - startAngle))
            .toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    Image(
        painter = painterResource(R.drawable.strongroom_door_print),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = 1f + 0.03f * travelFraction
                scaleY = 1f + 0.03f * travelFraction
                translationY = -8.dp.toPx() * travelFraction
            }
    )
}

// ------------------------------------------------------------------
// (1) The sheet on the press — the Composition standing (C-13)
// ------------------------------------------------------------------

@Composable
private fun CompositionSheet(
    composition: DoorwayComposition,
    lightState: StrongroomLightState
) {
    val sheetGround = StrongroomLight.stateSurface(
        StrongroomInks.Violet, lightState, com.example.ui.foundation.StrongroomLitSurface.FACE
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(sheetGround, RoundedCornerShape(4.dp))
            .border(1.dp, StrongroomInks.Gold, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = composition.dayShapeWord,
                style = StrongroomType.Scr3,
                color = StrongroomInks.Gold,
                modifier = Modifier.weight(1f)
            )
            // The lifting hour he set — his figure, on the sheet
            // that will be shut into the vault (not a printing:
            // printings belong to the sealed face, C-12).
            Text(
                text = composition.liftingHourText,
                style = StrongroomType.Anon2,
                color = StrongroomInks.Gold
            )
        }
        composition.taskLine?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it, style = StrongroomType.Scr1, color = StrongroomInks.Gold)
        }
        composition.pencilGreyProposal?.let {
            Spacer(Modifier.height(6.dp))
            // Pencil-grey: the gold of the sheet descended halfway
            // to graphite — grey, erasable, binding nothing.
            Text(
                text = it,
                style = StrongroomType.Scr1,
                color = StrongroomLight.blendEighth(
                    StrongroomInks.Gold, StrongroomInks.Graphite, 4
                )
            )
        }
        if (composition.lockedThings > 0 || composition.inSpanGaps > 0) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // What goes into the vault: enamel chips, one per
                // thing — objects standing in a row, never a list
                // of names and never a printed count.
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState())
                ) {
                    repeat(composition.lockedThings) {
                        Box(
                            modifier = Modifier
                                .size(width = 18.dp, height = 12.dp)
                                .background(
                                    StrongroomLight.blendEighth(
                                        StrongroomInks.Violet, StrongroomInks.Graphite, 3
                                    ),
                                    RoundedCornerShape(2.dp)
                                )
                                .border(1.dp, StrongroomInks.Gold, RoundedCornerShape(2.dp))
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                }
                // The shaped gaps: deliberate empty spaces in the
                // sheet — outlined, unfilled, unlabelled.
                repeat(composition.inSpanGaps) {
                    Box(
                        modifier = Modifier
                            .size(width = 22.dp, height = 12.dp)
                            .border(1.dp, StrongroomInks.Gold, RoundedCornerShape(2.dp))
                    )
                    Spacer(Modifier.width(6.dp))
                }
            }
        }
    }
}

// ------------------------------------------------------------------
// (2) One plain line — the Glance (C-05's record, doorway form)
// ------------------------------------------------------------------

@Composable
private fun PlainLine(formedFraction: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        val cy = size.height / 2f
        val track = StrongroomLight.blendEighth(
            StrongroomInks.Graphite, StrongroomInks.Violet, 3
        )
        drawLine(
            color = track, start = Offset(0f, cy), end = Offset(size.width, cy),
            strokeWidth = 3.dp.toPx()
        )
        val formed = formedFraction.coerceIn(0f, 1f)
        if (formed > 0f) {
            val endX = size.width * formed
            drawLine(
                color = StrongroomInks.Gold, start = Offset(0f, cy),
                end = Offset(endX, cy), strokeWidth = 3.dp.toPx()
            )
            drawCircle(
                color = StrongroomInks.Gold, radius = 4.dp.toPx(),
                center = Offset(endX, cy)
            )
        }
    }
}

// ------------------------------------------------------------------
// (3) The waiting slip — the held waiting, as a slip (C-14)
// ------------------------------------------------------------------

@Composable
private fun WaitingSlip(modifier: Modifier = Modifier) {
    // A slip of the room's brass, dimmed — present, mute, carrying
    // nothing: no count, no name, no dot, no word.
    Box(
        modifier = modifier
            .rotate(-3f)
            .size(width = 64.dp, height = 22.dp)
            .background(
                StrongroomLight.blendEighth(
                    StrongroomInks.Gold, StrongroomInks.Graphite, 5
                ),
                RoundedCornerShape(2.dp)
            )
            .border(1.dp, StrongroomInks.Gold, RoundedCornerShape(2.dp))
    )
}

// ------------------------------------------------------------------
// (4) The press — yesterday's struck plate at the doorway (C-06)
// ------------------------------------------------------------------

@Composable
private fun StruckPlateStrip(plate: LiftingPlate.PlateTruth) {
    val lightState = LocalStrongroomLightState.current
    Box(
        modifier = Modifier
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
