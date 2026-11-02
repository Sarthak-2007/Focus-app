package com.example.ui.signing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomGuard
import com.example.ui.foundation.LocalStrongroomLightState
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * C-02 · THE SIGNING — a HERO. The evening's act at the open door,
 * authored by BUILD-SR-36 from the rebuild element census
 * (INVENTORY-SR-1), firsthand.
 *
 * The signing table is a place of OBJECTS, in the signing's filed
 * order and no other (insertion figure 0, ledger C-4): the spoken
 * words the record names → the tray of tokens → the dial that sets
 * the lifting hour → the break rail → the allowance pawl → his
 * sentence and task, written on the slip's lines → the great wheel
 * worked for the signature itself → the slip, struck as the act's
 * record. There are no section titles, no labels, no form fields:
 * a screen of labelled inputs is the condemned form, and every
 * setter here is a mechanism with mass, detents and resistance,
 * built on the foundation's filed trains ([SigningDial] ·
 * [BreakChoice] · [AllowancePawl] · [CombinationSpin]).
 *
 * Staging law (ledger head correction, AL-0056): the signature is
 * the EVENING's act; the SWING is the Seal's act at the first
 * sitting next morning. This surface authors the signing and its
 * setting of the seal — it does not swing, does not seal, and the
 * signature does not seal (census F-T04: "the signature seals" is
 * a ruled non-transition). [SigningActions.onSignature] reports
 * the completed signature and carries NO combination: his
 * combination is spun by his hand alone and written nowhere
 * (ledger A-2) — structurally, no value in this file can hold it.
 *
 * TWO DERIVATIONS, charged to this slice, stated on its PR:
 *  (1) THE DOOR-WORD (the fourth printing, census C-12): the slot
 *      is admitted and the word is defined in NO source — census
 *      GAP-COORD-01, Stage 5 TI-G1 ("no word exists"). The record
 *      genuinely underdetermines it; this surface builds the slot's
 *      standing form on the sealed face's slice, not here, and
 *      invents nothing. GAP-COORD-01 stands flagged on the PR.
 *  (2) THE UNSIGNED SWING (census GAP-COORD-04 / F-T11): lawful in
 *      substance, unmodelled in mechanism. This slice builds the
 *      signed signing whole and mints no mechanism for the
 *      unsigned morning. GAP-COORD-04 stands flagged on the PR.
 *
 * Words: only census C-02's lawful words appear — the purpose
 * sentence and the clause, spoken whole at the signing; the door's
 * telling at the first signing; "codes wait tonight" where the
 * purse stands spent; the day-shape's name; his own words (task ·
 * sentence) where he writes them. Every other figure on the table
 * is his figure (an hour, a length, a count of tokens), never a
 * sentence the app authors.
 *
 * Texture (intake §1): brass, steel and paper carry real grain
 * derived from the surface itself ([SigningTexture]); no flat fill
 * carries a material surface, and no image exists in this file.
 * Motion is physics vocabulary only: the dial is caught by the
 * foundation hour train's rail, the break by its free train, the
 * wheel by the spin's governed ceiling and seat law — the figures
 * live in [SigningMechanisms], read off [com.example.ui.foundation
 * .StrongroomMaximum], UNPROVEN-ON-DEVICE as filed.
 */

/** One app, as a physical token on the tray: the phone's own name
 *  for it, carried as the token's face — chosen by sight and touch,
 *  never as a row in a scrollable text list. */
data class SigningToken(val packageName: String, val label: String)

/** The signing table's standing facts, supplied by the host from
 *  the kept engine (the UI issues commands, never sets state). */
data class SigningState(
    /** Tomorrow, standing composed (C-13) — the signing AMENDS it,
     *  never creates it (ledger B-2). */
    val day: StrongroomComposition.ComposedDay,
    /** Every app the tray can hold, as tokens. */
    val tokens: List<SigningToken>,
    /** The Guard's standing state, for the pawl (C-23). */
    val guard: StrongroomGuard.GuardState,
    /** The first signing carries the door's telling (B-19). */
    val firstSigning: Boolean = false,
    /** The day's purse stands spent: "codes wait tonight" (B-4). */
    val codesWaitTonight: Boolean = false,
    /** A loosening proposal standing in pencil-grey, read back
     *  aloud at this signing (TX-SAY-05) — or null. */
    val pencilGreyProposal: String? = null,
    /** The signature has bound: the slip stands struck. */
    val signed: Boolean = false
)

/** The signing's commands. Amendments name what they move and
 *  leave the composition's rest standing (StrongroomComposition). */
data class SigningActions(
    val onAmend: (StrongroomComposition.SigningAmendment) -> Unit,
    /** An allowance move, judged by the pawl's law (D-1): tighten
     *  instant, raise wound to the next day-start, refused beyond
     *  the hard stop. The host applies it to the Guard. */
    val onAllowanceMove: (targetMinutes: Int) -> Unit,
    /** The signature is whole. Carries nothing — by construction
     *  there is no combination value to carry. */
    val onSignature: () -> Unit
)

@Composable
fun Signing(state: SigningState, actions: SigningActions, modifier: Modifier = Modifier) {
    val lightState = LocalStrongroomLightState.current
    val record = state.day.record
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StrongroomRoles.Field)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .semantics { contentDescription = "ULYSSES" }
    ) {
        SpokenWords(state)
        Spacer(Modifier.height(22.dp))
        TokenTray(state, actions, lightState)
        Spacer(Modifier.height(26.dp))
        SigningDialSurface(state, actions, lightState)
        Spacer(Modifier.height(26.dp))
        BreakRail(state, actions, lightState)
        Spacer(Modifier.height(26.dp))
        AllowancePawlSurface(state, actions, lightState)
        Spacer(Modifier.height(26.dp))
        SentenceAndTaskLines(state, actions, lightState)
        Spacer(Modifier.height(30.dp))
        GreatWheelSignature(state, actions, lightState)
        Spacer(Modifier.height(30.dp))
        SigningSlipSurface(state, lightState)
        Spacer(Modifier.height(24.dp))
    }
}

// ── The spoken words (census C-02, TX-SAY-01…06) ────────────────

@Composable
private fun SpokenWords(state: SigningState) {
    Text(
        text = StrongroomDay.PURPOSE_SENTENCE,
        style = StrongroomType.Scr2,
        color = StrongroomRoles.OnGround
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = SigningSlip.CLAUSE,
        style = StrongroomType.Scr1,
        color = StrongroomLight.descend(StrongroomInks.Gold, 2)
    )
    if (state.firstSigning) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = SigningSlip.DOOR_TELLING,
            style = StrongroomType.Scr1,
            color = StrongroomLight.descend(StrongroomInks.Gold, 3)
        )
    }
    if (state.codesWaitTonight) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = SigningSlip.SPENT_DAY_TELLING,
            style = StrongroomType.Scr1,
            color = StrongroomRoles.MarkRose
        )
    }
    state.pencilGreyProposal?.let { proposal ->
        Spacer(Modifier.height(10.dp))
        // The read-back: his pencil-grey proposal, word for word —
        // grey, erasable, binding nothing (TX-SHOW-16).
        Text(
            text = proposal,
            style = StrongroomType.Scr2,
            color = StrongroomLight.blendEighth(
                StrongroomInks.Gold, StrongroomInks.Graphite, 4
            )
        )
    }
}

// ── The tray of tokens (brief C-08; census C-02's choosing) ─────

@Composable
private fun TokenTray(
    state: SigningState,
    actions: SigningActions,
    lightState: StrongroomLightState
) {
    val allowed = state.day.record.allowedPackages
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 6.dp)
    ) {
        state.tokens.forEach { token ->
            val isAllowed = token.packageName in allowed
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 7.dp)
                    .offset(y = if (isAllowed) 0.dp else 7.dp)
                    .clickable {
                        val next = if (isAllowed) {
                            allowed - token.packageName
                        } else {
                            allowed + token.packageName
                        }
                        actions.onAmend(
                            StrongroomComposition.SigningAmendment(allowedPackages = next)
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(
                            color = if (isAllowed) StrongroomInks.Gold
                            else StrongroomLight.descend(StrongroomInks.Graphite, 2),
                            shape = CircleShape
                        )
                        .border(
                            width = if (isAllowed) 2.5.dp else 1.dp,
                            color = if (isAllowed) StrongroomLight.litPartner(
                                StrongroomInks.Gold, StrongroomInks.Graphite,
                                lightState
                            ) else StrongroomInks.Graphite,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // The minted edge: a lit crescent toward the
                    // lamp and a shadow crescent away from it —
                    // a struck token, not a flat disc.
                    Canvas(Modifier.matchParentSize()) {
                        val c = Offset(size.width / 2f, size.height / 2f)
                        val r = size.minDimension / 2f - 2.dp.toPx()
                        val base = if (isAllowed) StrongroomInks.Gold
                        else StrongroomInks.Graphite
                        val fld = if (isAllowed) StrongroomInks.Graphite
                        else StrongroomInks.Violet
                        drawArc(
                            color = StrongroomLight.relief(base, fld, 0.85f, lightState),
                            startAngle = 153f, sweepAngle = 120f, useCenter = false,
                            topLeft = Offset(c.x - r, c.y - r),
                            size = Size(r * 2f, r * 2f),
                            style = Stroke(width = 3.dp.toPx())
                        )
                        drawArc(
                            color = StrongroomLight.relief(base, fld, -0.85f, lightState),
                            startAngle = -27f, sweepAngle = 120f, useCenter = false,
                            topLeft = Offset(c.x - r, c.y - r),
                            size = Size(r * 2f, r * 2f),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                    Text(
                        text = token.label.take(1).uppercase(),
                        style = StrongroomType.Scr3,
                        color = if (isAllowed) StrongroomRoles.OnLeaf
                        else StrongroomLight.descend(StrongroomInks.Gold, 4)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = token.label,
                    style = StrongroomType.Scr0,
                    color = if (isAllowed) StrongroomRoles.OnGround
                    else StrongroomLight.descend(StrongroomInks.Gold, 4)
                )
            }
        }
    }
}

// ── Crafted metal: the drawing helpers ─────────────────────────
// The dial and the wheel are drawn as MACHINED objects, not flat
// shapes: every ring is relieved segment by segment from the
// surface's own light (RELIGHT — relief light derived from the
// surface's own luminance, the mechanism Sarthak's eye kept),
// every bolt head carries its lit lip and its slot, every enamel
// field carries fine grain. The lamp is the state's own, read
// from StrongroomLight's law — no call site invents a light.

/** The RELIGHT gradient (−1..1) of a direction on the canvas:
 *  the direction's projection onto the state's light direction. */
private fun lightGradient(angleRad: Double, state: StrongroomLightState): Float {
    val p = StrongroomLight.paramsFor(state)
    val len = sqrt(
        (p.lightDirectionX * p.lightDirectionX +
            p.lightDirectionY * p.lightDirectionY).toDouble()
    )
    if (len == 0.0) return 0f
    return (cos(angleRad) * p.lightDirectionX / len +
        sin(angleRad) * p.lightDirectionY / len).toFloat()
}

/** A machined ring, relieved segment by segment: each segment's
 *  tone is the base ink's RELIGHT relief at that segment's own
 *  facing — light travels around the ring the way it travels
 *  around the picked print's door rings. */
private fun DrawScope.reliefRing(
    center: Offset,
    radius: Float,
    width: Float,
    base: Color,
    field: Color,
    state: StrongroomLightState,
    segments: Int = 72
) {
    val sweep = 360f / segments
    for (i in 0 until segments) {
        val start = i * sweep
        val mid = Math.toRadians((start + sweep / 2.0).toDouble())
        drawArc(
            color = StrongroomLight.relief(base, field, lightGradient(mid, state), state),
            startAngle = start,
            sweepAngle = sweep + 0.8f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2f, radius * 2f),
            style = Stroke(width = width)
        )
    }
}

/** One bolt head, as the print's rings carry them: a body toned
 *  from its own place on the ring, a lit lip toward the lamp, a
 *  slot cut across it. [slotAngleRad] turns the slot — no two
 *  bolts on a real door share a slot angle. */
private fun DrawScope.boltHead(
    center: Offset,
    radius: Float,
    gradient: Float,
    slotAngleRad: Double,
    field: Color,
    state: StrongroomLightState
) {
    drawCircle(
        color = StrongroomLight.relief(StrongroomInks.Graphite, field, gradient, state),
        radius = radius, center = center
    )
    val p = StrongroomLight.paramsFor(state)
    val len = sqrt(
        (p.lightDirectionX * p.lightDirectionX +
            p.lightDirectionY * p.lightDirectionY).toDouble()
    )
    if (len > 0.0) {
        val lx = (p.lightDirectionX / len).toFloat()
        val ly = (p.lightDirectionY / len).toFloat()
        drawCircle(
            color = StrongroomLight.relief(StrongroomInks.Graphite, field, 1f, state),
            radius = radius * 0.42f,
            center = center + Offset(lx * radius * 0.4f, ly * radius * 0.4f)
        )
    }
    val sx = cos(slotAngleRad).toFloat()
    val sy = sin(slotAngleRad).toFloat()
    drawLine(
        color = StrongroomLight.blendEighth(StrongroomInks.Violet, StrongroomInks.Graphite, 5),
        start = center - Offset(sx * radius * 0.62f, sy * radius * 0.62f),
        end = center + Offset(sx * radius * 0.62f, sy * radius * 0.62f),
        strokeWidth = radius * 0.45f
    )
}

/** Fine grain over a disc (or annulus): per-cell tones from the
 *  surface ink itself, through [SigningTexture] — the print's
 *  halftone fineness; never blocks, never a flat fill. */
private fun DrawScope.grainDisc(
    center: Offset,
    radius: Float,
    innerRadius: Float,
    ink: Color,
    field: Color,
    species: SigningTexture.Species,
    seed: Int,
    state: StrongroomLightState
) {
    val cell = species.cellPx.dp.toPx()
    var row = 0
    var gy = center.y - radius
    while (gy < center.y + radius) {
        var col = 0
        var gx = center.x - radius
        while (gx < center.x + radius) {
            val dx = gx + cell / 2f - center.x
            val dy = gy + cell / 2f - center.y
            val d = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
            if (d in innerRadius..radius) {
                drawRect(
                    color = SigningTexture.cellTone(ink, field, species, col, row, seed, state),
                    topLeft = Offset(gx, gy),
                    size = Size(cell + 1f, cell + 1f)
                )
            }
            gx += cell; col++
        }
        gy += cell; row++
    }
}

// ── The Signing Dial — fresh in-app (brief C-05; ledger E-11) ───

@Composable
internal fun SigningDialSurface(
    state: SigningState,
    actions: SigningActions,
    lightState: StrongroomLightState
) {
    val record = state.day.record
    var previewMinute by remember(record.liftingTime) {
        mutableStateOf(SigningDial.minuteOfDay(record.liftingTime))
    }
    val pm = previewMinute >= SigningDial.CIRCLE_MINUTES ||
        record.liftingTime.hour >= 12
    var dragAngle by remember(record.liftingTime) {
        mutableStateOf(SigningDial.angleFor(SigningDial.minuteOfDay(record.liftingTime)))
    }
    val vernier = SigningDial.vernierFor(previewMinute)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(272.dp)
                .pointerInput(record.liftingTime) {
                    var lastAngle: Double? = null
                    detectDragGestures(
                        onDragStart = { lastAngle = null },
                        onDragEnd = {
                            val (placed, _) = SigningDial.settle(
                                SigningDial.minuteFor(dragAngle), pm
                            )
                            previewMinute = placed
                            actions.onAmend(
                                StrongroomComposition.SigningAmendment(
                                    liftingTime = SigningDial.timeFor(placed)
                                )
                            )
                        },
                        onDragCancel = { lastAngle = null },
                        onDrag = { change, _ ->
                            val cx = size.width / 2.0
                            val cy = size.height / 2.0
                            val angle = Math.toDegrees(
                                atan2(change.position.y - cy, change.position.x - cx)
                            ) + 90.0
                            val prev = lastAngle
                            if (prev != null) {
                                var delta = angle - prev
                                if (delta > 180.0) delta -= 360.0
                                if (delta < -180.0) delta += 360.0
                                dragAngle += delta
                            }
                            lastAngle = angle
                            previewMinute = SigningDial.minuteFor(dragAngle) +
                                if (pm) SigningDial.CIRCLE_MINUTES else 0
                            change.consume()
                        }
                    )
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val center = Offset(cx, cy)
                val outer = size.minDimension / 2f
                val enamel = StrongroomLight.stateSurface(
                    StrongroomInks.Violet, lightState,
                    com.example.ui.foundation.StrongroomLitSurface.FACE
                )
                // The shadow's fall, from the state's own lamp:
                // every cut on this face throws its shadow this way.
                val lp = StrongroomLight.paramsFor(lightState)
                val llen = sqrt(
                    (lp.lightDirectionX * lp.lightDirectionX +
                        lp.lightDirectionY * lp.lightDirectionY).toDouble()
                )
                val shx = if (llen > 0.0) {
                    (-lp.lightDirectionX / llen * 1.2.dp.toPx()).toFloat()
                } else 0f
                val shy = if (llen > 0.0) {
                    (-lp.lightDirectionY / llen * 1.2.dp.toPx()).toFloat()
                } else 0f
                // THE ENAMEL FACE, grained: the dial's deep-violet
                // enamel carries fine grain from its own ink — the
                // print's halftone fineness; never a flat fill,
                // never blocks.
                drawCircle(color = enamel, radius = outer - 20.dp.toPx(), center = center)
                grainDisc(
                    center = center, radius = outer - 20.dp.toPx(), innerRadius = 0f,
                    ink = enamel, field = StrongroomInks.Graphite,
                    species = SigningTexture.Species.ENAMEL, seed = 53,
                    state = lightState
                )
                // THE STEEL CASE: a wide machined flange, relieved
                // segment by segment — the light travels around
                // the ring as it travels around the print's door
                // rings, catching the upper-left, dying lower-right.
                reliefRing(
                    center = center, radius = outer - 10.dp.toPx(),
                    width = 20.dp.toPx(), base = StrongroomInks.Graphite,
                    field = StrongroomInks.Violet, state = lightState
                )
                // A gold pinstripe seated in the flange, by hand.
                reliefRing(
                    center = center, radius = outer - 18.5.dp.toPx(),
                    width = 2.dp.toPx(), base = StrongroomInks.Gold,
                    field = StrongroomInks.Graphite, state = lightState
                )
                // Twelve bolt heads on the flange, as the print's
                // door ring carries them — each lit from its own
                // place, each slot turned differently.
                for (i in 0 until 12) {
                    val a = Math.toRadians(i * 30.0 - 90.0)
                    val bx = cx + (outer - 10.dp.toPx()) * cos(a).toFloat()
                    val by = cy + (outer - 10.dp.toPx()) * sin(a).toFloat()
                    boltHead(
                        center = Offset(bx, by), radius = 4.2.dp.toPx(),
                        gradient = lightGradient(a, lightState),
                        slotAngleRad = a + i * 0.83,
                        field = StrongroomInks.Violet, state = lightState
                    )
                }
                // Detent stones: quarters small, whole hours firm —
                // the hour train's topography, cut into the enamel:
                // each stone's shadow cut is drawn first, thrown
                // away from the lamp, then its gold face.
                for (step in 0 until 48) {
                    val minute = step * 15
                    val whole = minute % 60 == 0
                    val a = Math.toRadians(
                        SigningDial.angleFor(minute) - 90.0
                    ).toFloat()
                    val r1 = outer - if (whole) 40.dp.toPx() else 33.dp.toPx()
                    val r2 = outer - 26.dp.toPx()
                    drawLine(
                        color = StrongroomLight.shadowPartner(StrongroomInks.Gold),
                        start = Offset(cx + r1 * cos(a) + shx, cy + r1 * sin(a) + shy),
                        end = Offset(cx + r2 * cos(a) + shx, cy + r2 * sin(a) + shy),
                        strokeWidth = if (whole) 3.dp.toPx() else 1.5.dp.toPx()
                    )
                    drawLine(
                        color = if (whole) StrongroomInks.Gold
                        else StrongroomLight.descend(StrongroomInks.Gold, 3),
                        start = Offset(cx + r1 * cos(a), cy + r1 * sin(a)),
                        end = Offset(cx + r2 * cos(a), cy + r2 * sin(a)),
                        strokeWidth = if (whole) 3.dp.toPx() else 1.5.dp.toPx()
                    )
                }
                // The scale's inner groove: a machined gold line
                // closing the detent band.
                reliefRing(
                    center = center, radius = outer - 43.dp.toPx(),
                    width = 1.5.dp.toPx(), base = StrongroomInks.Gold,
                    field = StrongroomInks.Graphite, state = lightState
                )
                // The hand, crafted: a tapered gold hand with a
                // counterweight tail — its shadow thrown first,
                // then its relieved face.
                val ha = Math.toRadians(
                    SigningDial.angleFor(previewMinute) - 90.0
                ).toFloat()
                val hdx = cos(ha)
                val hdy = sin(ha)
                val tip = Offset(
                    cx + (outer - 30.dp.toPx()) * hdx,
                    cy + (outer - 30.dp.toPx()) * hdy
                )
                val tailC = Offset(cx - 15.dp.toPx() * hdx, cy - 15.dp.toPx() * hdy)
                val perp = Offset(-hdy, hdx)
                fun handPath(ox: Float, oy: Float): Path = Path().apply {
                    moveTo(
                        tailC.x + ox + perp.x * 4.5.dp.toPx(),
                        tailC.y + oy + perp.y * 4.5.dp.toPx()
                    )
                    lineTo(tip.x + ox, tip.y + oy)
                    lineTo(
                        tailC.x + ox - perp.x * 4.5.dp.toPx(),
                        tailC.y + oy - perp.y * 4.5.dp.toPx()
                    )
                    close()
                }
                drawPath(path = handPath(shx, shy), color = StrongroomInks.Graphite)
                drawPath(
                    path = handPath(0f, 0f),
                    color = StrongroomLight.relief(
                        StrongroomInks.Gold, StrongroomInks.Graphite,
                        lightGradient(ha.toDouble(), lightState), lightState
                    )
                )
                drawCircle(
                    color = StrongroomLight.relief(
                        StrongroomInks.Gold, StrongroomInks.Graphite,
                        lightGradient(ha.toDouble() + Math.PI, lightState), lightState
                    ),
                    radius = 5.dp.toPx(), center = tailC
                )
                // The hub: a machined graphite collar, relieved,
                // with a gold heart.
                reliefRing(
                    center = center, radius = 9.dp.toPx(),
                    width = 5.dp.toPx(), base = StrongroomInks.Graphite,
                    field = StrongroomInks.Violet, state = lightState
                )
                drawCircle(color = StrongroomInks.Gold, radius = 4.dp.toPx(), center = center)
                // THE VERNIER APERTURE at the index (top): a window
                // recessed into the flange, where the scale passes
                // enlarged under a hairline — the thumb works the
                // ring's edge, the reading is taken here, where the
                // thumb is not. The recess reads by its shadowed
                // head and its lit sill.
                val winW = 92.dp.toPx()
                val winH = 30.dp.toPx()
                val winX = cx - winW / 2f
                val winY = cy - outer + 2.dp.toPx()
                drawRect(
                    color = StrongroomLight.blendEighth(
                        StrongroomInks.Violet, StrongroomInks.Graphite, 6
                    ),
                    topLeft = Offset(winX, winY),
                    size = Size(winW, winH)
                )
                drawRect(
                    color = StrongroomInks.Graphite,
                    topLeft = Offset(winX, winY),
                    size = Size(winW, 5.dp.toPx())
                )
                drawLine(
                    color = StrongroomLight.relief(
                        StrongroomInks.Gold, StrongroomInks.Graphite, 0.5f, lightState
                    ),
                    start = Offset(winX, winY + winH - 1.dp.toPx()),
                    end = Offset(winX + winW, winY + winH - 1.dp.toPx()),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawRect(
                    color = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.6f, lightState
                    ),
                    topLeft = Offset(winX, winY),
                    size = Size(winW, winH),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                // Enlarged vernier ticks: the detent and its
                // neighbours, spread across the window.
                for (k in -3..3) {
                    val x = cx + k * (winW / 8f)
                    val tall = k == 0
                    drawLine(
                        color = if (tall) StrongroomInks.Gold
                        else StrongroomLight.descend(StrongroomInks.Gold, 2),
                        start = Offset(x, winY + winH - if (tall) 16.dp.toPx() else 10.dp.toPx()),
                        end = Offset(x, winY + winH - 3.dp.toPx()),
                        strokeWidth = if (tall) 2.5.dp.toPx() else 1.5.dp.toPx()
                    )
                }
                // The hairline itself.
                drawLine(
                    color = StrongroomInks.Magenta,
                    start = Offset(cx, winY + 2.dp.toPx()),
                    end = Offset(cx, winY + winH - 2.dp.toPx()),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "%02d:%02d".format(
                        (previewMinute / 60) % 24, previewMinute % 60
                    ),
                    style = StrongroomType.Anon4,
                    color = StrongroomRoles.OnGround
                )
                Text(
                    text = "+${vernier.vernierMinute}",
                    style = StrongroomType.Anon0,
                    color = StrongroomLight.descend(StrongroomInks.Gold, 2)
                )
            }
        }
    }
}

// ── The break rail — freely chosen (brief C-03; ledger D-6) ─────

@Composable
private fun BreakRail(
    state: SigningState,
    actions: SigningActions,
    lightState: StrongroomLightState
) {
    val record = state.day.record
    var preview by remember(record.breakLengthMinutes) {
        mutableStateOf(record.breakLengthMinutes)
    }
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .pointerInput(record.breakLengthMinutes) {
                        detectDragGestures(
                            onDragEnd = {
                                actions.onAmend(
                                    StrongroomComposition.SigningAmendment(
                                        breakLengthMinutes = preview
                                    )
                                )
                            },
                            onDrag = { change, _ ->
                                val frac = (change.position.x / size.width)
                                    .coerceIn(0f, 1f)
                                preview = BreakChoice.place(
                                    (frac * BreakChoice.MAX_MINUTES).toDouble()
                                )
                                change.consume()
                            }
                        )
                    }
            ) {
                val midY = size.height / 2f
                drawLine(
                    color = StrongroomRoles.Metal,
                    start = Offset(0f, midY),
                    end = Offset(size.width, midY),
                    strokeWidth = 5.dp.toPx()
                )
                // Fine grain: a tick a minute, firmer at five —
                // topography on a FREE curve (amplitudes 30 / 50
                // in the foundation's figures); presets ride as
                // brass notches (75, ramped), never as walls.
                for (m in 0..BreakChoice.MAX_MINUTES) {
                    val x = size.width * m / BreakChoice.MAX_MINUTES
                    val preset = BreakChoice.isPreset(m)
                    val h = when {
                        preset -> 20.dp.toPx()
                        m % 5 == 0 -> 13.dp.toPx()
                        else -> 7.dp.toPx()
                    }
                    drawLine(
                        color = if (preset) StrongroomInks.Gold
                        else StrongroomLight.descend(StrongroomInks.Gold, 4),
                        start = Offset(x, midY - h / 2f),
                        end = Offset(x, midY + h / 2f),
                        strokeWidth = if (preset) 3.dp.toPx() else 1.dp.toPx()
                    )
                }
                val px = size.width * preview / BreakChoice.MAX_MINUTES
                // The rail's knob, as a struck brass stud: its
                // shadow thrown away from the lamp, its face
                // relieved toward it.
                drawCircle(
                    color = StrongroomLight.descend(StrongroomInks.Gold, 4),
                    radius = 11.dp.toPx(),
                    center = Offset(px + 1.5.dp.toPx(), midY + 2.dp.toPx())
                )
                drawCircle(
                    color = StrongroomLight.relief(
                        StrongroomInks.Gold, StrongroomInks.Graphite, 0.5f, lightState
                    ),
                    radius = 11.dp.toPx(),
                    center = Offset(px, midY)
                )
                drawCircle(
                    color = StrongroomRoles.OnLeaf,
                    radius = 4.dp.toPx(),
                    center = Offset(px, midY)
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = "$preview",
                style = StrongroomType.Anon3,
                color = StrongroomRoles.OnGround
            )
        }
        // The preset shortcuts: brass discs, one tap each — a
        // shortcut to a figure, never the ceiling above the rail.
        Row(modifier = Modifier.padding(top = 4.dp)) {
            StrongroomDay.BREAK_PRESET_SHORTCUTS_MINUTES.forEach { preset ->
                Box(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(StrongroomInks.Gold, CircleShape)
                        .clickable {
                            preview = BreakChoice.shortcut(preset)
                            actions.onAmend(
                                StrongroomComposition.SigningAmendment(
                                    breakLengthMinutes = preview
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Struck brass grain, from the disc itself.
                    Canvas(Modifier.matchParentSize()) {
                        val cell = SigningTexture.Species.BRASS.cellPx.dp.toPx()
                        var gy = 0
                        var yy = 0f
                        while (yy < size.height) {
                            var gx = 0
                            var xx = 0f
                            while (xx < size.width) {
                                drawRect(
                                    color = SigningTexture.cellTone(
                                        StrongroomInks.Gold, StrongroomInks.Graphite,
                                        SigningTexture.Species.BRASS, gx, gy,
                                        seed = 31 + preset, state = lightState
                                    ),
                                    topLeft = Offset(xx, yy),
                                    size = Size(cell + 1f, cell + 1f)
                                )
                                xx += cell; gx++
                            }
                            yy += cell; gy++
                        }
                    }
                    Text(
                        text = "$preset",
                        style = StrongroomType.Anon1,
                        color = StrongroomRoles.OnLeaf
                    )
                }
            }
        }
    }
}

// ── The allowance pawl (brief C-03; census C-23; intake §3) ─────

@Composable
private fun AllowancePawlSurface(
    state: SigningState,
    actions: SigningActions,
    lightState: StrongroomLightState
) {
    val guard = state.guard
    var preview by remember(guard.allowanceMinutes, guard.pendingAllowanceMinutes) {
        mutableStateOf(
            if (guard.pendingAllowanceMinutes >= 0) guard.pendingAllowanceMinutes
            else guard.allowanceMinutes
        )
    }
    val wound = AllowancePawl.raiseWound(guard)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier
                .size(width = 72.dp, height = 168.dp)
                .pointerInput(guard.allowanceMinutes) {
                    var startAllowance = guard.allowanceMinutes
                    var travelled = 0f
                    detectDragGestures(
                        onDragStart = {
                            startAllowance = preview
                            travelled = 0f
                        },
                        onDragEnd = {
                            actions.onAllowanceMove(preview)
                        },
                        onDrag = { change, dragAmount ->
                            travelled += dragAmount.y
                            // One pawl tooth per 5 minutes per 14 dp
                            // of travel: downward travel tightens
                            // (clicks off instantly), upward winds a
                            // raise against the pawl's stop.
                            val steps = (travelled / 14.dp.toPx()).toInt()
                            preview = (startAllowance - steps * 5)
                                .coerceIn(0, 240)
                            change.consume()
                        }
                    )
                }
        ) {
            val toothH = size.height / 12f
            // The ratchet rack: teeth lean one way — the pawl's
            // one-way law drawn in steel.
            for (i in 0 until 12) {
                val y = i * toothH
                val tone = SigningTexture.cellTone(
                    StrongroomInks.Graphite, StrongroomInks.Violet,
                    SigningTexture.Species.STEEL, 3, i, seed = 41,
                    state = lightState
                )
                drawLine(
                    color = tone,
                    start = Offset(8.dp.toPx(), y + toothH),
                    end = Offset(size.width - 8.dp.toPx(), y),
                    strokeWidth = 4.dp.toPx()
                )
            }
            // The pawl itself, seated at the preview's height:
            // a brass knob — shadow thrown, face relieved, a
            // graphite heart where the pin seats.
            val frac = 1f - (preview.coerceIn(0, 240) / 240f)
            val pawlY = frac * size.height
            drawCircle(
                color = StrongroomLight.descend(StrongroomInks.Gold, 4),
                radius = 10.dp.toPx(),
                center = Offset(size.width / 2f + 1.5.dp.toPx(), pawlY + 2.dp.toPx())
            )
            drawCircle(
                color = StrongroomLight.relief(
                    StrongroomInks.Gold, StrongroomInks.Graphite, 0.55f, lightState
                ),
                radius = 10.dp.toPx(),
                center = Offset(size.width / 2f, pawlY)
            )
            drawCircle(
                color = StrongroomRoles.OnLeaf,
                radius = 3.5.dp.toPx(),
                center = Offset(size.width / 2f, pawlY)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = "${guard.allowanceMinutes}",
                style = StrongroomType.Anon3,
                color = StrongroomRoles.OnGround
            )
            if (wound) {
                Spacer(Modifier.height(10.dp))
                // The pending ring: a raise wound against the pawl,
                // standing visible until the next day-start — an
                // object, not a sentence (C-23 carries no words).
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .border(3.dp, StrongroomInks.Gold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${guard.pendingAllowanceMinutes}",
                        style = StrongroomType.Anon1,
                        color = StrongroomRoles.OnGround
                    )
                }
            }
        }
    }
}

// ── His sentence / its kept place · the task (brief C-07) ───────

@Composable
private fun SentenceAndTaskLines(
    state: SigningState,
    actions: SigningActions,
    lightState: StrongroomLightState
) {
    val record = state.day.record
    val paper = StrongroomLight.blendEighth(
        StrongroomInks.Gold, StrongroomInks.Graphite, 6
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(paper, RoundedCornerShape(3.dp))
            .drawBehind {
                // Paper grain, from the paper itself — the sheet
                // the sentence stands on is the slip's own stock.
                val cell = SigningTexture.Species.PAPER.cellPx.dp.toPx()
                var gy = 0
                var yy = 0f
                while (yy < size.height) {
                    var gx = 0
                    var xx = 0f
                    while (xx < size.width) {
                        drawRect(
                            color = SigningTexture.cellTone(
                                paper, StrongroomInks.Graphite,
                                SigningTexture.Species.PAPER, gx, gy,
                                seed = 61, state = lightState
                            ),
                            topLeft = Offset(xx, yy),
                            size = Size(cell + 1f, cell + 1f)
                        )
                        xx += cell; gx++
                    }
                    yy += cell; gy++
                }
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // The task: the reason for the block, in his own words,
        // exempt from any word-bench wholly (ledger B-3).
        var task by remember(record.taskLine) {
            mutableStateOf(record.taskLine ?: "")
        }
        val focusManager = LocalFocusManager.current
        BasicTextField(
            value = task,
            onValueChange = { task = it },
            textStyle = StrongroomType.Scr2.copy(color = StrongroomRoles.OnLeaf),
            cursorBrush = SolidColor(StrongroomRoles.OnLeaf),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focus ->
                    if (!focus.isFocused) {
                        actions.onAmend(
                            StrongroomComposition.SigningAmendment(
                                taskLine = task.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(StrongroomInks.Graphite)
        )
        Spacer(Modifier.height(16.dp))
        // His sentence, or its kept place: where no sentence
        // stands, the ruled line stands empty — a deliberate
        // standing blank that is itself the form (census C-12).
        var sentence by remember(record.sentence) {
            mutableStateOf(record.sentence ?: "")
        }
        BasicTextField(
            value = sentence,
            onValueChange = { sentence = it },
            textStyle = StrongroomType.Scr2.copy(color = StrongroomRoles.OnLeaf),
            cursorBrush = SolidColor(StrongroomRoles.OnLeaf),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focus ->
                    if (!focus.isFocused) {
                        actions.onAmend(
                            StrongroomComposition.SigningAmendment(
                                sentence = sentence.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(StrongroomInks.Graphite)
        )
    }
}

// ── The Great Wheel + the signature (brief C-04; census C-02) ───

@Composable
internal fun GreatWheelSignature(
    state: SigningState,
    actions: SigningActions,
    lightState: StrongroomLightState
) {
    val spin = remember { CombinationSpin() }
    var rotation by remember { mutableStateOf(0.0) }
    var lastVelocity by remember { mutableStateOf(0.0) }
    var seats by remember { mutableStateOf(0) }
    LaunchedEffect(seats) {
        if (spin.isComplete && !state.signed) actions.onSignature()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(248.dp)
                .pointerInput(Unit) {
                    var lastAngle: Double? = null
                    var lastMs: Long = 0L
                    detectDragGestures(
                        onDragStart = { lastAngle = null },
                        onDragEnd = {
                            val clockwise = lastVelocity > 0.0
                            if (spin.attemptSeat(rotation, lastVelocity, clockwise)) {
                                seats = spin.seatsTaken
                            }
                        },
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
                                // The governed ceiling: a dial that
                                // cannot be flicked (240°/s, filed).
                                val dt = ((change.uptimeMillis - lastMs)
                                    .coerceAtLeast(1)) / 1000.0
                                var velocity = delta / dt
                                if (velocity > 240.0) velocity = 240.0
                                if (velocity < -240.0) velocity = -240.0
                                lastVelocity = velocity
                                rotation += velocity * dt
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
            // plus the hand's live travel within the pass.
            val drawn = (spin.wheelTurnoverDeg + rotation % 360.0).toFloat()
            Canvas(Modifier.fillMaxSize().rotate(drawn)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val center = Offset(cx, cy)
                val outer = size.minDimension / 2f
                // THE RIM: machined steel, relieved segment by
                // segment — the light travels around it as it
                // travels around the print's rings, catching the
                // upper-left, dying into graphite lower-right.
                reliefRing(
                    center = center, radius = outer - 9.dp.toPx(),
                    width = 18.dp.toPx(), base = StrongroomInks.Graphite,
                    field = StrongroomInks.Violet, state = lightState,
                    segments = 96
                )
                // A gold pinstripe inlaid in the rim's face.
                reliefRing(
                    center = center, radius = outer - 9.dp.toPx(),
                    width = 2.dp.toPx(), base = StrongroomInks.Gold,
                    field = StrongroomInks.Graphite, state = lightState,
                    segments = 96
                )
                // Twelve bolt heads on the rim, as the print's
                // wheel ring carries them — each lit from its own
                // place, each slot turned differently.
                for (i in 0 until 12) {
                    val a = Math.toRadians(i * 30.0 - 90.0)
                    val bx = cx + (outer - 9.dp.toPx()) * cos(a).toFloat()
                    val by = cy + (outer - 9.dp.toPx()) * sin(a).toFloat()
                    boltHead(
                        center = Offset(bx, by), radius = 4.dp.toPx(),
                        gradient = lightGradient(a, lightState),
                        slotAngleRad = a + i * 0.83,
                        field = StrongroomInks.Violet, state = lightState
                    )
                }
                // The spokes: tapered bars of steel, relieved by
                // their own facing, the lit edge drawn on the
                // lamp's side.
                for (spoke in 0 until 3) {
                    val a = Math.toRadians(spoke * 120.0 - 90.0).toFloat()
                    val dx = cos(a)
                    val dy = sin(a)
                    val px = -dy
                    val py = dx
                    val hubHalf = 7.dp.toPx()
                    val rimHalf = 4.5.dp.toPx()
                    val hubR = 13.dp.toPx()
                    val rimR = outer - 19.dp.toPx()
                    val spokePath = Path().apply {
                        moveTo(cx + dx * hubR + px * hubHalf, cy + dy * hubR + py * hubHalf)
                        lineTo(cx + dx * rimR + px * rimHalf, cy + dy * rimR + py * rimHalf)
                        lineTo(cx + dx * rimR - px * rimHalf, cy + dy * rimR - py * rimHalf)
                        lineTo(cx + dx * hubR - px * hubHalf, cy + dy * hubR - py * hubHalf)
                        close()
                    }
                    drawPath(
                        path = spokePath,
                        color = StrongroomLight.relief(
                            StrongroomInks.Graphite, StrongroomInks.Violet,
                            lightGradient(a.toDouble(), lightState), lightState
                        )
                    )
                    val lpw = StrongroomLight.paramsFor(lightState)
                    val litSign =
                        if (px * lpw.lightDirectionX + py * lpw.lightDirectionY >= 0f) 1f else -1f
                    drawLine(
                        color = StrongroomLight.relief(
                            StrongroomInks.Graphite, StrongroomInks.Violet, 0.9f, lightState
                        ),
                        start = Offset(
                            cx + dx * hubR + px * hubHalf * litSign,
                            cy + dy * hubR + py * hubHalf * litSign
                        ),
                        end = Offset(
                            cx + dx * rimR + px * rimHalf * litSign,
                            cy + dy * rimR + py * rimHalf * litSign
                        ),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
                // The hub: a relieved steel collar, a gold heart,
                // and a bolt at its centre.
                reliefRing(
                    center = center, radius = 14.dp.toPx(),
                    width = 7.dp.toPx(), base = StrongroomInks.Graphite,
                    field = StrongroomInks.Violet, state = lightState
                )
                drawCircle(
                    color = StrongroomLight.relief(
                        StrongroomInks.Gold, StrongroomInks.Graphite, 0.35f, lightState
                    ),
                    radius = 10.dp.toPx(), center = center
                )
                boltHead(
                    center = center, radius = 4.5.dp.toPx(),
                    gradient = 0.3f, slotAngleRad = 0.7,
                    field = StrongroomInks.Violet, state = lightState
                )
            }
            // The station under the hand — one digit, read at the
            // index; the combination itself is never shown whole,
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
            // The pass's direction, as a mark at the wheel's rim —
            // a drawn fact, not a word.
            val dirAngle = if (spin.passClockwise) -35f else 215f
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(
                        x = (sin(Math.toRadians(dirAngle.toDouble())) * 108).dp,
                        y = (108 - cos(Math.toRadians(dirAngle.toDouble())) * 108).dp
                    )
                    .size(9.dp)
                    .background(
                        if (seats >= 3) StrongroomInks.Graphite else StrongroomInks.Magenta,
                        CircleShape
                    )
            )
        }
        // The seats taken, as three bolt heads filling — the
        // signature's progress is furniture, not a counter.
        Row(modifier = Modifier.padding(top = 12.dp)) {
            for (i in 0 until 3) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .size(13.dp)
                        .background(
                            if (i < seats) StrongroomInks.Gold
                            else StrongroomLight.descend(StrongroomInks.Graphite, 1),
                            CircleShape
                        )
                )
            }
        }
    }
}

// ── The slip (brief C-09) ────────────────────────────────────────

@Composable
private fun SigningSlipSurface(state: SigningState, lightState: StrongroomLightState) {
    val lines = SigningSlip.lines(
        day = state.day,
        firstSigning = state.firstSigning,
        codesWaitTonight = state.codesWaitTonight,
        allowedCount = state.day.record.allowedPackages.size
    )
    val paper = StrongroomLight.blendEighth(
        StrongroomInks.Gold, StrongroomInks.Graphite, 6
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .rotate(if (state.signed) -0.7f else 0f)
            .background(paper, RoundedCornerShape(3.dp))
            .border(
                width = if (state.signed) 2.5.dp else 1.dp,
                color = StrongroomInks.Graphite,
                shape = RoundedCornerShape(3.dp)
            )
    ) {
        // Paper grain, from the paper itself.
        Canvas(Modifier.matchParentSize()) {
            val cell = SigningTexture.Species.PAPER.cellPx.dp.toPx()
            var cy = 0
            var yy = 0f
            while (yy < size.height) {
                var cx = 0
                var xx = 0f
                while (xx < size.width) {
                    val tone = SigningTexture.cellTone(
                        paper, StrongroomInks.Graphite,
                        SigningTexture.Species.PAPER, cx, cy, seed = 97,
                        state = lightState
                    )
                    drawRect(
                        color = tone,
                        topLeft = Offset(xx, yy),
                        size = Size(cell + 1f, cell + 1f)
                    )
                    xx += cell; cx++
                }
                yy += cell; cy++
            }
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            if (state.signed) {
                // Struck: one heavy graphite rule across the head —
                // the press's mark. Struck once, never re-struck.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(StrongroomInks.Graphite)
                )
                Spacer(Modifier.height(10.dp))
            }
            lines.forEach { line ->
                when (line.kind) {
                    SigningSlip.LineKind.PURPOSE -> Text(
                        text = line.text,
                        style = StrongroomType.Scr1,
                        color = StrongroomRoles.OnLeaf
                    )
                    SigningSlip.LineKind.KEPT_PLACE -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.5.dp)
                                .align(Alignment.BottomStart)
                                .background(StrongroomInks.Graphite)
                        )
                    }
                    SigningSlip.LineKind.LIFTING,
                    SigningSlip.LineKind.BREAK,
                    SigningSlip.LineKind.ALLOWANCE,
                    SigningSlip.LineKind.ALLOWED -> Text(
                        text = line.text,
                        style = StrongroomType.Anon1,
                        color = StrongroomRoles.OnLeaf
                    )
                    else -> Text(
                        text = line.text,
                        style = StrongroomType.Scr1,
                        color = StrongroomRoles.OnLeaf
                    )
                }
                Spacer(Modifier.height(5.dp))
            }
        }
    }
}
