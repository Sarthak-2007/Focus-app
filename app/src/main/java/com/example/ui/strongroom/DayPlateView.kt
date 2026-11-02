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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.LiftingPlate
import com.example.ui.theme.PlateBodyStyle
import com.example.ui.theme.PlateBrass
import com.example.ui.theme.PlateBrassDark
import com.example.ui.theme.PlateBrassHigh
import com.example.ui.theme.PlateDarkRoom
import com.example.ui.theme.PlateDoorGround
import com.example.ui.theme.PlateHeadingStyle
import com.example.ui.theme.PlateHoleDepth
import com.example.ui.theme.PlateInkGold
import com.example.ui.theme.PlateLineWork
import com.example.ui.theme.punchDiameter

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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(brass, RoundedCornerShape(6.dp))
            .border(1.dp, PlateLineWork.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .testTag("day_plate")
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
                // Deck §7.3: hours held are figures only — no "held" caption is struck.
                text = heldFigures(truth.hoursHeldMillis),
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
            PunchRow(kind = kind, count = punches.size)
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
                // Deck §7.6 (A-12): the pair struck in its settled form —
                // [HH:MM — HH:MM], 15 cells, em-dash U+2014, one form.
                Text(
                    text = "[${truth.bracketStandingHour.orEmpty()} \u2014 ${truth.bracketFallenHour.orEmpty()}]",
                    style = PlateHeadingStyle,
                    color = PlateLineWork
                )
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

/** One kind's holes: the kind's name in the plate's hand, then its
 *  holes at the kind's graded diameter, in striking order. */
@Composable
private fun PunchRow(kind: LiftingPlate.PunchKind, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Deck §7.4: no legend is lettered — the holes' grades speak.
        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("day_plate_punches_${kind.name.lowercase()}")
        ) {
            repeat(count) { PunchedHole(kind.grade) }
        }
    }
}

/** A single punched hole: deep centre, raised lip toward the
 *  light (upper left, the RELIGHT direction the faces lane
 *  carries), no flat fill anywhere. */
@Composable
private fun PunchedHole(grade: LiftingPlate.PunchGrade) {
    val d = punchDiameter(grade)
    Canvas(Modifier.size(d)) {
        val r = size.minDimension / 2f
        // The raised lip: a brass-high crescent offset to the light.
        drawCircle(
            color = PlateBrassHigh,
            radius = r,
            center = Offset(center.x - r * 0.14f, center.y - r * 0.14f)
        )
        // The hole itself: punched depth, inset from the lip.
        drawCircle(
            color = PlateHoleDepth,
            radius = r * 0.82f,
            center = Offset(center.x + r * 0.07f, center.y + r * 0.07f)
        )
    }
}

/** The seal's mark: WHOLE = an unbroken ring in line-work;
 *  BROKEN / AJAR = the same ring cracked through by a jagged
 *  line. Drawn, never a stock icon. */
@Composable
private fun SealMark(truth: LiftingPlate.PlateTruth) {
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
