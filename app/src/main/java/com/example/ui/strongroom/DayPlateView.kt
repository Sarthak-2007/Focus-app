package com.example.ui.strongroom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.LiftingPlate
import com.example.ui.theme.PlateBodyStyle
import com.example.ui.theme.PlateBrass
import com.example.ui.theme.PlateBrassDark
import com.example.ui.theme.PlateDarkRoom
import com.example.ui.theme.PlateDoorGround
import com.example.ui.theme.PlateHeadingStyle
import com.example.ui.theme.PlateInkGold
import com.example.ui.theme.PlateLineWork
import com.example.ui.strongroom.grade.PlateGradeRender
import com.example.ui.strongroom.grade.asComposeColor
import com.example.ui.strongroom.grade.asHoleKind

/**
 * P6 — THE DAY PLATE, RENDERED (BUILD-SR-4, Slice 1).
 *
 * The brass plate his eye kept (E-9): struck ONCE at the lifting
 * and rendered here exactly as struck — this composable is a pure
 * function of a [LiftingPlate.StruckPlate]; it holds no state,
 * computes no total, and shows nothing the strike did not strike.
 * There is no score on this surface because there is no score in
 * its model.
 *
 * What the plate says, in the strike's own order:
 *  1. The day (its filing date) and the hours held — the truth,
 *     in the plate's telemetry hand (PLT scale).
 *  2. The punched holes, by kind, at their graded diameters —
 *     reach large, held medium, floor small (sizes a finger
 *     could learn; figures UNPROVEN, build record U-4). A hole
 *     is drawn as struck metal: a deep centre with a raised lip
 *     on the light side, never a flat dot.
 *  3. The seal: WHOLE (an unbroken ring) or BROKEN (the ring
 *     cracked through). The governor crank's snapped seal is
 *     carried in the truth but NOT rendered here — its kept
 *     home is the tray (A-8, R3 CC-5), rendered pile-side.
 *  4. On a fallen day: the bracket strike — the gap hours as two
 *     engraved figures under A-12's settled words, on brass
 *     descended into the dark.
 *  5. Confessions, when any stand: appended BELOW the struck
 *     face, on the plate's margin, each with its own moment —
 *     beside the strike, never inside it (B-14).
 */

/** "7:20" — held time as struck figures (hours:minutes), the
 *  plate's own way of carrying hours held. Pure; unit-tested. */
fun heldFigures(hoursHeldMillis: Long): String {
    val totalMinutes = (hoursHeldMillis / 60_000L).coerceAtLeast(0L)
    val h = totalMinutes / 60L
    val m = totalMinutes % 60L
    return "$h:${m.toString().padStart(2, '0')}"
}

@Composable
fun DayPlateView(
    plate: LiftingPlate.StruckPlate,
    modifier: Modifier = Modifier
) {
    val truth = plate.truth
    val brass = if (truth.isFailure) PlateBrassDark else PlateBrass
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(brass)
            .testTag("day_plate")
    ) {
        // DESIGNER-SR-4 §1 at Stage 10 composition (BUILD-SR-16):
        // the grain (fine horizontal striations, ±0.03 of the line
        // ink, 1 dp period — deterministic in y, never animated),
        // the chamfer (a light line on the top/left at the raised
        // tone, a dark line on the bottom/right — the flat uniform
        // border of Slice 1 is replaced by it), and the thickness
        // band (3 dp of deep brass along the bottom: the plate is
        // a slab lying in the tray, not a card). All tones arrive
        // from the grade pipeline's ladder — gold alone, descended.
        Canvas(Modifier.matchParentSize()) {
            val line = PlateLineWork
            var y = 0f
            val period = 1.dp.toPx()
            var stripe = 0
            while (y < size.height) {
                drawRect(
                    color = line.copy(alpha = PlateGradeRender.GRAIN_ALPHA),
                    topLeft = Offset(0f, y),
                    size = androidx.compose.ui.geometry.Size(
                        size.width, if (stripe % 2 == 0) period else 0f
                    )
                )
                y += period * 2
                stripe++
            }
            val raised = PlateGradeRender.BRASS_RAISED.asComposeColor()
            val deep = PlateGradeRender.BRASS_DEEP.asComposeColor()
            // Chamfer: light on top + left, dark on bottom + right.
            drawLine(raised, Offset(0f, 1f), Offset(size.width, 1f), strokeWidth = 2f)
            drawLine(raised, Offset(1f, 0f), Offset(1f, size.height), strokeWidth = 2f)
            drawLine(deep, Offset(0f, size.height - 1f), Offset(size.width, size.height - 1f), strokeWidth = 2f)
            drawLine(deep, Offset(size.width - 1f, 0f), Offset(size.width - 1f, size.height), strokeWidth = 2f)
            // The thickness band.
            drawRect(
                color = deep,
                topLeft = Offset(0f, size.height - PlateGradeRender.PLATE_THICKNESS_DP.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(
                    size.width, PlateGradeRender.PLATE_THICKNESS_DP.dp.toPx()
                )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
        // 1 · The day and the hours held.
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = truth.filingDateIso,
                style = PlateHeadingStyle,
                color = PlateLineWork,
                modifier = Modifier.testTag("day_plate_date")
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "${heldFigures(truth.hoursHeldMillis)} held",
                style = PlateHeadingStyle,
                color = PlateLineWork,
                modifier = Modifier.testTag("day_plate_hours")
            )
            Spacer(Modifier.weight(1f))
            SealMark(truth)
        }

        Spacer(Modifier.height(14.dp))

        // 2 · The punched holes, kind by kind, in the strike's order.
        for (kind in LiftingPlate.PunchKind.values()) {
            val punches = truth.punchesOf(kind)
            if (punches.isEmpty()) continue
            PunchRow(kind = kind, count = punches.size, inDark = truth.isFailure)
            Spacer(Modifier.height(8.dp))
        }

        // The crank's snapped seal is NOT struck here: its kept
        // home is the tray (A-8; coordinator R3 CC-5 — the plan's
        // "kept on the plate" wording stands corrected as errata).
        // The fact rides in the plate's truth (P1 carries it) and
        // the tray renders it — see PlatePileScreen's tray row.

        // 4 · The bracket strike, on a fallen day.
        if (truth.bracketGapMillis > 0L) {
            Spacer(Modifier.height(12.dp))
            Column(Modifier.testTag("day_plate_bracket")) {
                Row {
                    Text(
                        text = truth.bracketStandingHour.orEmpty(),
                        style = PlateHeadingStyle,
                        color = PlateLineWork
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(text = "—", style = PlateHeadingStyle, color = PlateLineWork)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = truth.bracketFallenHour.orEmpty(),
                        style = PlateHeadingStyle,
                        color = PlateLineWork
                    )
                }
                // A-12's settled words — "standing at the last
                // check, fallen by the first check that found it" —
                // are the bracket's READING, not its lettering
                // (TYPE-SR-3's ruling to this lane): the plate is
                // struck with the two bracket hours as figures,
                // above, and carries no sentence. The words live
                // on the reading surfaces of a later slice.
            }
        }

        // 5 · Confessions stand on the margin, below the strike.
        for (confession in plate.confessions) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = confession.standing,
                style = PlateBodyStyle,
                color = PlateLineWork,
                modifier = Modifier.testTag("day_plate_confession")
            )
        }
        }
    }
}

/** One kind's holes: the kind's name in the plate's hand, then its
 *  holes at the kind's graded diameter, in striking order. */
@Composable
private fun PunchRow(kind: LiftingPlate.PunchKind, count: Int, inDark: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = when (kind) {
                LiftingPlate.PunchKind.REACH -> "Reach"
                LiftingPlate.PunchKind.HELD -> "Held"
                LiftingPlate.PunchKind.FLOOR -> "Floor"
            },
            style = PlateBodyStyle,
            color = PlateLineWork,
            modifier = Modifier.width(52.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("day_plate_punches_${kind.name.lowercase()}")
        ) {
            repeat(count) { PunchedHole(kind.grade, inDark = inDark) }
        }
    }
}

/** A single punched hole, AT GRADE (DESIGNER-SR-4 §2, consumed
 *  from BUILD-SR-10's grade pipeline at Stage 10 composition,
 *  BUILD-SR-16): the four concentric facts — raised lip toward the
 *  light · the wall · the through-dark (the punch went THROUGH;
 *  the centre is the darkness behind the plate, not a floor) ·
 *  the far rim in day presentation — at the corrected ladder
 *  (17.6 / 12.6 / 9.5 dp via [punchDiameter]'s re-point). Slice 1's
 *  two-circle hole (ember-lit lip at 20/13/8) is retired whole:
 *  ember is off brass (BR-06) and depth is not graded (SL-3) —
 *  diameter alone carries the kind. A failure plate's holes stand
 *  in the dark presentation: no far rim, exactly as struck. */
@Composable
private fun PunchedHole(grade: LiftingPlate.PunchGrade, inDark: Boolean = false) {
    com.example.ui.strongroom.grade.PunchedHoleGrade(
        kind = grade.asHoleKind(),
        inDark = inDark
    )
}

/** The seal's mark: WHOLE = an unbroken ring in line-work;
 *  BROKEN / AJAR = the same ring cracked through by a jagged
 *  line. Drawn, never a stock icon.
 *
 *  THE FAILURE PLATE'S SEAL PLACE (DESIGNER-SR-4 §4, Stage 10
 *  composition, BUILD-SR-16): on a fallen day the place stands
 *  UNSTRUCK — an empty struck circle in the recess tone, no ring,
 *  no crack. B-11's law is subtraction (the seal gone, and NOTHING
 *  added); a crack is a pictogram explaining the failure, and
 *  Slice 1 drew one. The crack form survives only where the seal
 *  broke on a day that otherwise stands (a non-failure broken
 *  truth) — the failure plate itself carries absence in the exact
 *  shape of the thing that is absent. */
@Composable
private fun SealMark(truth: LiftingPlate.PlateTruth) {
    if (truth.isFailure) {
        Canvas(
            Modifier
                .size(30.dp)
                .testTag("day_plate_seal_place_empty")
        ) {
            val r = size.minDimension / 2f - 2f
            drawCircle(
                color = PlateGradeRender.BRASS_RECESS.asComposeColor(),
                radius = r,
                center = center,
                style = Stroke(width = 2.5f)
            )
        }
        return
    }
    val broken = truth.sealTruth != LiftingPlate.SealTruth.WHOLE
    Canvas(
        Modifier
            .size(30.dp)
            .testTag(if (broken) "day_plate_seal_broken" else "day_plate_seal_whole")
    ) {
        val r = size.minDimension / 2f - 2f
        drawCircle(
            color = PlateLineWork,
            radius = r,
            center = center,
            style = Stroke(width = 2.5f)
        )
        if (broken) {
            val crack = Path().apply {
                moveTo(center.x - r * 0.25f, center.y - r)
                lineTo(center.x + r * 0.18f, center.y - r * 0.35f)
                lineTo(center.x - r * 0.15f, center.y + r * 0.1f)
                lineTo(center.x + r * 0.22f, center.y + r * 0.55f)
                lineTo(center.x + r * 0.05f, center.y + r)
            }
            drawPath(crack, color = PlateLineWork, style = Stroke(width = 2f))
        }
    }
}

/**
 * THE AJAR FACE (A-12 / B-11 / E-6) — what a fallen day shows:
 * the sealed face's door standing ajar in a dark room, the seal
 * gone and NOTHING added. The door is the kept violet, swung a
 * crack on its hinge; behind the crack, darkness only — no
 * message, no apology, no summary. The failure's telling lives
 * on its plate (the bracket strike), never on this face.
 */
@Composable
fun AjarDoorFace(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
            .background(PlateDarkRoom)
            .testTag("ajar_face")
    ) {
        // The crack of darkness the ajar door stands open on —
        // wider at the swinging edge, nothing behind it.
        Canvas(Modifier.matchParentSize()) {
            val doorLeft = size.width * 0.16f
            val doorRight = size.width * 0.80f
            val gap = Path().apply {
                moveTo(doorRight, size.height * 0.06f)
                lineTo(doorRight + 16f, size.height * 0.10f)
                lineTo(doorRight + 16f, size.height * 0.94f)
                lineTo(doorRight, size.height * 0.98f)
                close()
            }
            drawPath(gap, color = PlateDarkRoom)
            // The door itself: kept violet, ajar on its left hinge.
            drawRect(
                color = PlateDoorGround,
                topLeft = Offset(doorLeft, size.height * 0.06f),
                size = androidx.compose.ui.geometry.Size(
                    doorRight - doorLeft, size.height * 0.92f
                )
            )
            // Its gold pinstripe, swung with it.
            drawLine(
                color = PlateInkGold.copy(alpha = 0.7f),
                start = Offset(doorLeft + 22f, size.height * 0.06f),
                end = Offset(doorLeft + 22f, size.height * 0.98f),
                strokeWidth = 2.5f
            )
        }
    }
}
