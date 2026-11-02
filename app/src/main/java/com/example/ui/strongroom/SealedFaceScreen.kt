package com.example.ui.strongroom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.FaceDoorWordStyle
import com.example.ui.theme.RelightState
import com.example.ui.theme.FaceSentenceStyle
import com.example.ui.theme.StrongroomGold
import com.example.ui.theme.StrongroomGround
import com.example.ui.theme.StrongroomLineWork
import com.example.ui.theme.reliefHighlight
import com.example.ui.theme.reliefShade
import com.example.ui.strongroom.grade.FaceHeightfield
import com.example.ui.strongroom.grade.GradeColour
import com.example.ui.strongroom.grade.GradeInk
import com.example.ui.strongroom.grade.GradeSurface
import com.example.ui.strongroom.grade.RelightFieldCanvas
import com.example.ui.strongroom.grade.asGradeState
import com.example.ui.strongroom.grade.gradeGround
import com.example.ui.strongroom.layout.FaceFieldLayout

/**
 * P3 — THE SEALED FACE (Slice 1).
 *
 * The launcher standing in the strongroom before the door: the
 * kept ground & finish (deep-violet enamelled steel, gold leaf +
 * pinstripe — sitting walk record 2, L1/L2 KEPT), the four
 * printings at the Stage 5 §3 hierarchy, relief light per the
 * KEPT mechanism ([RelightState] — derived from the surface's
 * own luminance, per state; the sealed home state carries it at
 * working strength, Audit A G-5).
 *
 * THE FACE NEVER ALTERS WHILE THE SEAL STANDS. This composable
 * holds no state, runs no clock, and answers a touch with no
 * visual change of any kind — no press scale, no ripple, no
 * brightening. Its only event is THE KNOCK: a reach against the
 * door fires [onKnockReach] (the caller spends the knock voice,
 * [com.example.service.TactileSoundEngine.playKnock]) and that
 * is the whole reply. Nothing opens, nothing apologises.
 *
 * The one way onward is the grille plate at the foot of the door
 * — the antechamber's deliberate second touch ([onAntechamberRequest];
 * the antechamber itself is P5). It is a separate, smaller touch
 * by design: reaching the door knocks; choosing the grille is a
 * second, deliberate act.
 *
 * THE SEAL-GONE FORM (B-11 / R7-1, landed by BUILD-SR-14): when
 * [SealedFaceState.sealGoneBracket] stands, this same renderer
 * draws the fallen day's testimony — the seal absent, the bracket
 * pair at the hour's rank in TYPE-SR-2's drawn figures, every
 * kept place and the sentence exactly as engraved, and nothing
 * added. It is testimony at the next open, never a gate (R2-3):
 * P6 presents it at the lifting with its callbacks standing as
 * no-ops, and the phone was already released at the seal's death.
 *
 * Wiring seam (named in this lane's return): the face takes its
 * [SealedFaceState] as a sealing-time snapshot and its two events
 * as callbacks — P2's engine wiring (BUILD-SR-3) supplies the
 * snapshot from the seal and routes the knock from a locked-app
 * reach; P9 composes the whole day. No session type is invented
 * here to guess at theirs.
 */
@Composable
fun SealedFaceScreen(
    state: SealedFaceState,
    lightState: RelightState = RelightState.SEALED,
    onKnockReach: () -> Unit,
    onAntechamberRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    // STAGE 10 COMPOSITION (BUILD-SR-16): the face's ground is
    // struck through BUILD-SR-10's grade pipeline — the state's
    // mass descent on the enamel ink as data (GradePipeline's
    // stateSurface, P0's table mirrored value-for-value), and the
    // relief layer is the pipeline's own working form
    // (RelightFieldCanvas over a FaceHeightfield: the ring groove
    // and the pinstripe cut into the ground's own luminance, so
    // the light belongs to the surface as drawn). The token
    // bridge's flat gradient of Slice 1 is retired from this face.
    val gradeState = lightState.asGradeState()
    val groundStruck = gradeGround(GradeInk.VIOLET, lightState, GradeSurface.FACE)
    val groundBase = remember {
        Array(36) { Array(24) { GradeColour.of(GradeInk.VIOLET) } }
    }
    val knockSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(groundStruck)
            .clickable(
                interactionSource = knockSource,
                indication = null,
                onClick = onKnockReach
            )
            .testTag("sealed_face")
    ) {
        RelightFieldCanvas(
            base = groundBase,
            state = gradeState,
            surface = GradeSurface.FACE,
            heightfield = FaceHeightfield(),
            modifier = Modifier.fillMaxSize()
        )
        PinstripeField(lightState)
        // STAGE 8 COMPOSITION (BUILD-SR-16): the four printings
        // stand in BUILD-SR-7's FaceFieldLayout — Stage 5 §3.3's
        // hierarchy as structure (hour · door-word slot · sentence
        // or kept place · held-mark slot, FACE_BAND apart), not a
        // surface's own column of spacers. The grille plate is not
        // a printing: it stands at the door's foot, over the field.
        // COMPOSITION (BUILD-SR-23, step 2): rank 1's CONTENT is
        // BUILD-SR-14's — the ring while the seal stands, the
        // bracket pair (B-11 / R7-1) in the seal-gone form —
        // seated inside BUILD-SR-16's slot: #856's content governs
        // over the pre-restructure form, per the merge order.
        FaceFieldLayout(
            liftingHour = {
                // RANK 1 — the lifting hour on the fresh ring; or,
                // in the seal-gone form (B-11 / R7-1), the bracket
                // pair at the same rank: the seal is absent, the
                // pair is carried in the same drawn figures, and
                // NOTHING else on the face changes — no banner, no
                // badge, no word of explanation, no route onward.
                val bracket = state.sealGoneBracket
                if (bracket != null) {
                    BracketPairCanvas(
                        bracket = bracket,
                        lightState = lightState,
                        modifier = Modifier
                            .size(width = 300.dp, height = 168.dp)
                            .testTag("sealed_face_bracket")
                    )
                } else {
                    RingCanvas(
                        hour = state.liftingHour,
                        lightState = lightState,
                        modifier = Modifier
                            .size(300.dp)
                            .testTag("sealed_face_ring")
                    )
                }
            },
            doorWordSlot = {
                // RANK 2 — the door-word slot as its KEPT PLACE
                // (G-1): an empty, sized place — the slot's measure
                // (PRT-3's line) held by a hairline rule, no word.
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(FaceDoorWordStyle.lineHeight.value.dp)
                        .testTag("door_word_kept_place"),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(StrongroomGold.copy(alpha = 0.55f))
                    )
                }
            },
            sentenceOrKeptPlace = {
                // RANK 3 — his sentence, or its kept place (PRT-4):
                // the kept place holds the same block — three lines
                // at the sentence's own leading — sized like the
                // presence it replaces.
                if (state.hasSentence) {
                    Text(
                        text = state.sentence.orEmpty(),
                        style = FaceSentenceStyle,
                        color = StrongroomLineWork,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        modifier = Modifier.testTag("face_sentence")
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .width(260.dp)
                            .height(120.dp)
                            .testTag("sentence_kept_place")
                    ) {
                        repeat(3) { line ->
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .padding(top = 39.dp)
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(
                                            StrongroomLineWork.copy(
                                                alpha = if (line == 2) 0.10f else 0.18f
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            },
            heldMarkSlot = {
                // RANK 4 — the held-mark slot as its kept place
                // (G-2): a quiet struck empty place — a shallow
                // punch with no glyph in it.
                HeldMarkKeptPlace(
                    lightState = lightState,
                    modifier = Modifier.testTag("held_mark_kept_place")
                )
            }
        )
        // The grille plate — the antechamber's second touch, at
        // the foot of the door.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
        ) {
            GrillePlate(onClick = onAntechamberRequest)
        }
    }
}

/** The hand-laid pinstripe: a double gold rule inset from the
 *  door's edge, drawn as strokes on the ground. Its fineness is
 *  a best-judgement software value (Audit A G-4, UNPROVEN) —
 *  struck at the test-condition fineness and judged at Slice 4. */
@Composable
private fun PinstripeField(lightState: RelightState) {
    // DESIGNER-SR-1's FaceCraft, consumed at Stage 10 composition
    // (BUILD-SR-16): the pinstripe pair is FaceCraftFallback's —
    // in SEALED the two promoted reads stand (exact gold held; its
    // lip warmed exactly one eighth — the same pair the grade
    // pipeline files as sealedPinstripe / sealedPinstripeLitLip);
    // in other states the lip is P0's relief at full gradient.
    val (gold, goldLip) = FaceCraftFallback.pinstripe(lightState)
    Canvas(Modifier.fillMaxSize()) {
        val outerInset = 14.dp.toPx()
        val innerInset = 20.dp.toPx()
        drawRect(
            color = goldLip,
            topLeft = Offset(outerInset, outerInset),
            size = Size(size.width - outerInset * 2, size.height - outerInset * 2),
            style = Stroke(width = 2.dp.toPx())
        )
        drawRect(
            color = gold.copy(alpha = 0.55f),
            topLeft = Offset(innerInset, innerInset),
            size = Size(size.width - innerInset * 2, size.height - innerInset * 2),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

/**
 * THE RING, drawn fresh (Audit A G-3): a band of rules on the
 * ground — NO FILL anywhere: a stroked band, two gold hairlines,
 * the gold chord struck exactly under the hour's span, and the
 * hour's five figures engraved on the curve (each glyph turned
 * with its arc position, EL-2; a dark groove pass caught by a
 * relief-highlight pass offset toward the light — the RELIGHT
 * mechanism at glyph scale). The twice-rejected render species
 * is not consulted by a single line of this drawing.
 */
@Composable
private fun RingCanvas(
    hour: String,
    lightState: RelightState,
    modifier: Modifier = Modifier
) {
    val gold = StrongroomGold
    val bandTone = StrongroomGround.reliefShade(lightState)
    // FaceCraft's groove floor (DESIGNER-SR-1, consumed BUILD-SR-16):
    // in SEALED this is promoted read (i) — exact graphite, held.
    val groove = FaceCraftFallback.grooveFloor(lightState)
    val edgeLight = StrongroomGround.reliefHighlight(lightState)

    Canvas(modifier) {
        val scale = size.minDimension / (2.0 * RingGeometry.OUTER_RADIUS_MM).toFloat()
        val center = Offset(size.width / 2f, size.height / 2f)
        val baselinePx = (RingGeometry.RADIUS_MM * scale).toFloat()
        val outerPx = (RingGeometry.OUTER_RADIUS_MM * scale).toFloat()
        val innerPx = (RingGeometry.INNER_RADIUS_MM * scale).toFloat()
        val bandWidthPx = outerPx - innerPx
        val midPx = (outerPx + innerPx) / 2f

        // The band: one stroked circle in the ground's own shade —
        // a ring, never a disc.
        drawCircle(
            color = bandTone,
            radius = midPx,
            center = center,
            style = Stroke(width = bandWidthPx)
        )
        // Its two gold rules.
        drawCircle(
            color = gold,
            radius = outerPx,
            center = center,
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = gold.copy(alpha = 0.75f),
            radius = innerPx,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        // The gold chord: struck under the hour's span, no wider.
        val (chordStart, chordSweep) = RingGeometry.chordSpan()
        drawArc(
            color = gold,
            startAngle = (-90.0 + chordStart).toFloat(),
            sweepAngle = chordSweep.toFloat(),
            useCenter = false,
            topLeft = Offset(center.x - midPx, center.y - midPx),
            size = Size(midPx * 2, midPx * 2),
            style = Stroke(width = 2.5.dp.toPx())
        )

        // The engraved figures — TYPE-SR-2's DRAWN forms
        // (HourFigures.kt, consumed under their filed contract
        // in the P3 rethink): RingGeometry untouched; per
        // placement, per glyph, the catch edge first — the same
        // path shifted up-left toward the light, in gold — then
        // the groove, dark: RELIGHT at glyph scale. Strokes and
        // the colon's punches only; no font renders the hour
        // anywhere on this face (Audit A G-3 carried whole: the
        // figures are a drawing, not a typeface). 6 and 9 are
        // unlike constructions in the drawing (measured
        // separation 46.7% of union ink; the font's pair
        // measured 5–7%) — Slice 4 judges them in the app.
        val s = (RingGeometry.FONT_SIZE_MM * scale).toFloat() / HourFigures.EM_UNITS
        val graverStroke = Stroke(
            width = HourFigures.STROKE_UNITS * s,
            cap = StrokeCap.Square,
            join = StrokeJoin.Round
        )
        RingGeometry.placements(hour).forEach { p ->
            val glyph = HourFigures.glyphFor(p.char)
            val gx = center.x + (p.unitX * baselinePx).toFloat()
            val gy = center.y + (p.unitY * baselinePx).toFloat()
            // FaceCraft's per-glyph lip (DESIGNER-SR-1, consumed
            // BUILD-SR-16): the catch edge's offset is the state's
            // light direction ROTATED BY THIS GLYPH'S OWN ARC
            // POSITION — two glyphs at opposite ends of the arc
            // catch light differently. Slice 1's single uniform
            // offset (-16s, -16s for every glyph) was the failed
            // species the soul panel named; it is retired here.
            val (lipX, lipY) = FaceCraftFallback.glyphLipOffsetPx(
                glyphOffsetDeg = p.rotationDeg,
                state = lightState,
                grooveDepthPx = 16f * s
            )
            withTransform({
                translate(gx, gy)
                rotate(p.rotationDeg.toFloat(), pivot = Offset.Zero)
            }) {
                glyph.strokes.forEach { cmds ->
                    drawPath(
                        glyphPath(cmds, s, lipX, lipY),
                        color = edgeLight,
                        style = graverStroke
                    )
                    drawPath(
                        glyphPath(cmds, s, 0f, 0f),
                        color = groove,
                        style = graverStroke
                    )
                }
                glyph.punches.forEach { punch ->
                    val w = punch.w * s
                    val h = punch.h * s
                    val left = (punch.x - HourFigures.CENTRE_X) * s
                    val top = -(punch.y + punch.h - HourFigures.CENTRE_Y) * s
                    drawRect(
                        color = edgeLight,
                        topLeft = Offset(left + lipX, top + lipY),
                        size = Size(w, h)
                    )
                    drawRect(
                        color = groove,
                        topLeft = Offset(left, top),
                        size = Size(w, h)
                    )
                }
            }
        }
    }
}

/**
 * THE BRACKET PAIR, drawn (B-11 / A-12) — the seal-gone form's
 * rank-1 printing. The pair stands as two rows of TYPE-SR-2's
 * drawn figures in the ring's own slot: the standing hour first
 * (earliest), the fallen hour below it (latest) — the pair in
 * its settled order, and nothing between or around them: no
 * dash, no label, no sentence of explanation (the pair's settled
 * words — "standing at the last check, fallen by the first check
 * that found it" — are its READING, not its lettering, exactly
 * as TYPE-SR-3 ruled for the plate). Each glyph is cut the
 * ring's way: the catch edge toward the light first, then the
 * groove — RELIGHT at glyph scale, no font rendering the hours.
 */
@Composable
private fun BracketPairCanvas(
    bracket: BracketPair,
    lightState: RelightState,
    modifier: Modifier = Modifier
) {
    val groove = StrongroomLineWork
    val edgeLight = StrongroomGround.reliefHighlight(lightState)

    Canvas(modifier) {
        // One scale for both rows: the five fixed cells of an
        // hour (EL-1) across the slot's width, as on the ring.
        val s = (size.width * 0.92f) / (HourFigures.ADVANCE_UNITS * RingGeometry.GLYPH_COUNT)
        val rowHeight = HourFigures.EM_UNITS * s
        val rowCentres = listOf(
            size.height / 2f - rowHeight * 0.62f,
            size.height / 2f + rowHeight * 0.62f
        )
        val graverStroke = Stroke(
            width = HourFigures.STROKE_UNITS * s,
            cap = StrokeCap.Square,
            join = StrokeJoin.Round
        )
        val catchShift = -16f * s
        val leftEdge = (size.width - HourFigures.ADVANCE_UNITS * RingGeometry.GLYPH_COUNT * s) / 2f
        listOf(bracket.standingHour, bracket.fallenHour).forEachIndexed { row, hour ->
            hour.forEachIndexed { i, char ->
                val glyph = HourFigures.glyphFor(char)
                val cellCentreX = leftEdge +
                    (i * HourFigures.ADVANCE_UNITS + HourFigures.CENTRE_X) * s
                withTransform({
                    translate(cellCentreX, rowCentres[row])
                }) {
                    glyph.strokes.forEach { cmds ->
                        drawPath(
                            glyphPath(cmds, s, catchShift, catchShift),
                            color = edgeLight,
                            style = graverStroke
                        )
                        drawPath(
                            glyphPath(cmds, s, 0f, 0f),
                            color = groove,
                            style = graverStroke
                        )
                    }
                    glyph.punches.forEach { punch ->
                        val w = punch.w * s
                        val h = punch.h * s
                        val left = (punch.x - HourFigures.CENTRE_X) * s
                        val top = -(punch.y + punch.h - HourFigures.CENTRE_Y) * s
                        drawRect(
                            color = edgeLight,
                            topLeft = Offset(left + catchShift, top + catchShift),
                            size = Size(w, h)
                        )
                        drawRect(
                            color = groove,
                            topLeft = Offset(left, top),
                            size = Size(w, h)
                        )
                    }
                }
            }
        }
    }
}

/**
 * One glyph stroke as a canvas path, under TYPE-SR-2's
 * transform: drawing point (x, y) → canvas
 * ((x − 273)·s + dx, −(y − 350)·s + dy) — y-up drawing space
 * into y-down canvas space, centred on the cell pivot.
 */
private fun glyphPath(
    cmds: List<HourFigures.Cmd>,
    s: Float,
    dx: Float,
    dy: Float
): Path {
    fun px(x: Float) = (x - HourFigures.CENTRE_X) * s + dx
    fun py(y: Float) = -(y - HourFigures.CENTRE_Y) * s + dy
    val path = Path()
    cmds.forEach { c ->
        when (c.op) {
            HourFigures.Op.M -> path.moveTo(px(c.a[0]), py(c.a[1]))
            HourFigures.Op.L -> path.lineTo(px(c.a[0]), py(c.a[1]))
            HourFigures.Op.C -> path.cubicTo(
                px(c.a[0]), py(c.a[1]), px(c.a[2]), py(c.a[3]),
                px(c.a[4]), py(c.a[5])
            )
            HourFigures.Op.Z -> path.close()
        }
    }
    return path
}

/** The held-mark's kept place: a shallow punch — a depressed disc
 *  of the ground's own shade, ringed by the line-work, its upper
 *  inner edge catching the relief light. Empty, by law (G-2). */
@Composable
private fun HeldMarkKeptPlace(
    lightState: RelightState,
    modifier: Modifier = Modifier
) {
    val deep = StrongroomGround.reliefShade(lightState)
    val line = StrongroomLineWork
    val edgeLight = StrongroomGround.reliefHighlight(lightState)
    Canvas(modifier.size(48.dp)) {
        val radius = size.minDimension / 2f - 2.dp.toPx()
        drawCircle(color = deep, radius = radius, center = center)
        drawCircle(
            color = line.copy(alpha = 0.8f),
            radius = radius,
            center = center,
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawArc(
            color = edgeLight.copy(alpha = 0.7f),
            startAngle = 190f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

/** The antechamber's grille plate: a small gold-ruled plate with
 *  five slits — a door within the door's foot, touched on
 *  purpose. Its press is P5's to give weight; in Slice 1 it is a
 *  plain, deliberate second touch and nothing more. */
@Composable
private fun GrillePlate(onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(54.dp)
            .clickable(interactionSource = source, indication = null, onClick = onClick)
            .semantics { contentDescription = "Antechamber" }
            .testTag("antechamber_grille")
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val gold = StrongroomGold
            drawRect(
                color = gold.copy(alpha = 0.75f),
                style = Stroke(width = 1.5.dp.toPx())
            )
            val slitTop = size.height * 0.24f
            val slitBottom = size.height * 0.76f
            repeat(5) { i ->
                val x = size.width * (0.24f + i * 0.13f)
                drawLine(
                    color = gold,
                    start = Offset(x, slitTop),
                    end = Offset(x, slitBottom),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}
