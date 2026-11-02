package com.example.ui.strongroom

import android.graphics.Paint
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
import androidx.core.content.res.ResourcesCompat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.FaceDoorWordStyle
import com.example.ui.theme.FaceLightState
import com.example.ui.theme.FaceSentenceStyle
import com.example.ui.theme.StrongroomGold
import com.example.ui.theme.StrongroomGround
import com.example.ui.theme.StrongroomLineWork
import com.example.ui.theme.reliefHighlight
import com.example.ui.theme.reliefShade

/**
 * P3 — THE SEALED FACE (Slice 1).
 *
 * The launcher standing in the strongroom before the door: the
 * kept ground & finish (deep-violet enamelled steel, gold leaf +
 * pinstripe — sitting walk record 2, L1/L2 KEPT), the four
 * printings at the Stage 5 §3 hierarchy, relief light per the
 * KEPT mechanism ([FaceLightState] — derived from the surface's
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
    lightState: FaceLightState = FaceLightState.SEALED,
    onKnockReach: () -> Unit,
    onAntechamberRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val groundHigh = StrongroomGround.reliefHighlight(lightState)
    val groundDeep = StrongroomGround.reliefShade(lightState)
    val knockSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(groundHigh, StrongroomGround, groundDeep)
                )
            )
            .clickable(
                interactionSource = knockSource,
                indication = null,
                onClick = onKnockReach
            )
            .testTag("sealed_face")
    ) {
        PinstripeField(lightState)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            // RANK 1 — the lifting hour on the fresh ring.
            RingCanvas(
                hour = state.liftingHour,
                lightState = lightState,
                modifier = Modifier
                    .size(300.dp)
                    .testTag("sealed_face_ring")
            )

            Spacer(Modifier.height(34.dp))

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

            Spacer(Modifier.height(26.dp))

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

            Spacer(Modifier.weight(1f))

            // RANK 4 — the held-mark slot as its kept place
            // (G-2): a quiet struck empty place — a shallow
            // punch with no glyph in it.
            HeldMarkKeptPlace(
                lightState = lightState,
                modifier = Modifier.testTag("held_mark_kept_place")
            )

            Spacer(Modifier.height(22.dp))

            // The grille plate — the antechamber's second touch.
            GrillePlate(onClick = onAntechamberRequest)
        }
    }
}

/** The hand-laid pinstripe: a double gold rule inset from the
 *  door's edge, drawn as strokes on the ground. Its fineness is
 *  a best-judgement software value (Audit A G-4, UNPROVEN) —
 *  struck at the test-condition fineness and judged at Slice 4. */
@Composable
private fun PinstripeField(lightState: FaceLightState) {
    val gold = StrongroomGold
    val goldHigh = gold.reliefHighlight(lightState)
    Canvas(Modifier.fillMaxSize()) {
        val outerInset = 14.dp.toPx()
        val innerInset = 20.dp.toPx()
        drawRect(
            color = goldHigh,
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
    lightState: FaceLightState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // The figures' hand is the bundled Anonymous Pro Bold, from
    // the app's own font file — and there is no silent substitute
    // (CRITIC-SR-3, charge 3a, cured in the rethink): if the kept
    // face cannot load, this composition fails loudly rather than
    // shipping the face's first fact in a generic monospace.
    val typeface = remember {
        ResourcesCompat.getFont(context, R.font.anonymous_pro_bold)
            ?: error(
                "The bundled Anonymous Pro Bold failed to load — " +
                    "the ring does not render in a substitute hand"
            )
    }
    val gold = StrongroomGold
    val bandTone = StrongroomGround.reliefShade(lightState)
    val groove = StrongroomLineWork
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

        // The engraved figures.
        val groovePaint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            this.typeface = typeface
            textSize = (RingGeometry.FONT_SIZE_MM * scale).toFloat()
            color = groove.toArgb()
        }
        val lightPaint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            this.typeface = typeface
            textSize = (RingGeometry.FONT_SIZE_MM * scale).toFloat()
            color = edgeLight.toArgb()
        }
        val centreLift = groovePaint.textSize * 0.35f
        val lightDrop = 1.6.dp.toPx()
        with(drawContext.canvas.nativeCanvas) {
            RingGeometry.placements(hour).forEach { p ->
                val gx = center.x + (p.unitX * baselinePx).toFloat()
                val gy = center.y + (p.unitY * baselinePx).toFloat()
                save()
                translate(gx, gy)
                rotate(p.rotationDeg.toFloat())
                // The caught edge first, dropped toward the light;
                // the groove over it. The pair is the engraving.
                drawText(p.char.toString(), 0f, centreLift + lightDrop, lightPaint)
                drawText(p.char.toString(), 0f, centreLift, groovePaint)
                restore()
            }
        }
    }
}

/** The held-mark's kept place: a shallow punch — a depressed disc
 *  of the ground's own shade, ringed by the line-work, its upper
 *  inner edge catching the relief light. Empty, by law (G-2). */
@Composable
private fun HeldMarkKeptPlace(
    lightState: FaceLightState,
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
