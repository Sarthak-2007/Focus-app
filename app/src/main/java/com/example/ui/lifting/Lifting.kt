package com.example.ui.lifting

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.domain.LiftingPlate
import com.example.ui.foundation.LocalStrongroomLightState
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import androidx.compose.ui.draw.rotate
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomLitSurface
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType
import com.example.ui.foundation.boltHead
import com.example.ui.foundation.grainAnnulus
import com.example.ui.foundation.grainDisc
import com.example.ui.foundation.grainRect
import com.example.ui.foundation.lightVectorFor
import com.example.ui.foundation.taperedSpoke
import kotlin.math.cos
import kotlin.math.sin

/**
 * C-17 · THE LIFTING — and C-06 · THE PLATE STRIKE (a HERO) and
 * C-07 · THE PLATE STACK, built by BUILD-SR-40 from the rebuild
 * element census (INVENTORY-SR-1), firsthand.
 *
 * At the engraved hour, uninvocable and unhurried: the time-lock
 * fires by itself, the bolts withdraw one by one in reverse load
 * order (heaviest first — [LiftingCeremony]), the door opens BY
 * WEIGHT on the foundation's driven body, and at the instant its
 * travel meets the stop the day is STRUCK: a brass plate punched
 * with the day's graded holes, its hours held (TX-SHOW-10) and
 * its seal line (TX-SHOW-13), landing with weight into the tray's
 * pile — the pile that grows plate by plate all season and is
 * never counted, scored or totalled (C-07). The handing back
 * (C-11) is the lift's first moment and is executed by the
 * engine as the lift fires; this surface presents no list of
 * those who waited, and the lift announces nothing — there are
 * no words on this surface beyond the plate's own struck ones.
 * LEAD-MOVES (2026-11-03): the handing back now also stands
 * PRESENT as mechanism — the waiting stand at the threshold as
 * wordless brass slips and are handed back one by one on
 * [HandingBackMotion] as the lock trips, every one gone before
 * the swing opens and the plate is struck; presence without
 * presentation — no name, no printed count, no word was added,
 * and the domain machine's gate is untouched. The same pass
 * machined what read as diagrams: bolts travel in jamb
 * housings, the wheel and dial are solid grained castings, and
 * the pile answers the landing with its own dip and settle
 * ([PlateLanding.pileDipAt]).
 *
 * THE FAILURE FORM stands in this same file, at the same craft
 * (C-18): the same door, AJAR in a dark room, the seal gone and
 * nothing added — no banner, no count, no comfort, no recovery
 * offer, no damage imagery of any kind — and its plate struck
 * WITH THE GAP, the bracket pair in its settled form
 * (TX-SHOW-14). A fallen day has no ceremony: this surface never
 * animates it.
 *
 * Texture is a surface property (intake §1): brass plate grain,
 * enamel grain, steel machining — deterministic grain derived
 * from each surface's own ink through RELIGHT ([LiftingTexture]).
 * Motion is physics vocabulary only: poses are read off
 * [LiftingCeremony] (the filed score + the foundation's bodies)
 * at the frame clock; there is no easing curve and no fade
 * anywhere in this file. All figures UNPROVEN-ON-DEVICE.
 */
data class LiftingState(
    /** The day's struck plate — the pile's landing plate — or
     *  null when no seal stood over a day (B-14: nothing is
     *  struck, and this surface does not stand). */
    val plate: StruckPlateView? = null,
    /** The pile as it stands in the tray, landing plate on top. */
    val pile: PlatePileView = PlatePileView(null, emptyList(), emptyList()),
    /** True when the standing struck day is a fallen day: the
     *  failure face stands, and no ceremony is played. */
    val isFailure: Boolean = false,
    /** How many people waited through this day, each person
     *  once — the handing back's standing fact, carried as a
     *  count and nothing more: the surface draws that many
     *  wordless slips at the threshold and hands them back on
     *  [HandingBackMotion]; it never learns or shows who they
     *  are (the names stand at the Drawer's threshold, C-11). */
    val waitingSlips: Int = 0,
    /** Where the ceremony clock stands when this surface is
     *  entered: the host reads it from the finish's own elapsed
     *  zero; a surface entered after the day has lifted stands
     *  at the score's end — the door open, the plate in the pile. */
    val ceremonyStartOffsetMs: Int = LiftingCeremony.ScoreEndMs,
    val reducedMotion: Boolean = false,
    /** THE HANDING BACK, as the store stands it for the landing
     *  day (PLACES): the people who waited, prepared by P6's own
     *  preparation, and [handingBackDelivered] — the proof the
     *  lift's precondition stood (their rows resolved
     *  HANDED_BACK before the plate was struck). Carried as a
     *  fact, presented as nothing: this surface shows no list of
     *  those who waited (R2-7) — they are in HIS phone's list
     *  again, which is the whole act. */
    val handingBack: LiftingPlate.HandingBack = LiftingPlate.HandingBack(emptyList()),
    val handingBackDelivered: Boolean = false
)

@Composable
fun Lifting(state: LiftingState, modifier: Modifier = Modifier) {
    val plate = state.plate
    if (plate == null) {
        // No seal stood, no plate was struck: stillness (GAP-R03).
        Box(modifier = modifier.fillMaxSize().background(StrongroomRoles.Field))
        return
    }
    if (state.isFailure) {
        FailureFace(plate = plate, pile = state.pile, modifier = modifier)
        return
    }

    var elapsedMs by remember(plate.filingDateIso) {
        mutableIntStateOf(state.ceremonyStartOffsetMs.coerceIn(0, LiftingCeremony.ScoreEndMs))
    }
    LaunchedEffect(plate.filingDateIso) {
        val start = withFrameMillis { it }
        val base = elapsedMs
        while (elapsedMs < LiftingCeremony.ScoreEndMs) {
            val now = withFrameMillis { it }
            elapsedMs = (base + (now - start).toInt())
                .coerceAtMost(LiftingCeremony.ScoreEndMs)
        }
    }
    val pose = LiftingCeremony.poseAt(elapsedMs, state.reducedMotion)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StrongroomRoles.Field)
            .semantics { contentDescription = "ULYSSES" }
    ) {
        DoorCeremony(
            pose = pose,
            waitingSlips = state.waitingSlips,
            reducedMotion = state.reducedMotion,
            modifier = Modifier.weight(1f)
        )
        TrayZone(
            plate = plate,
            pile = state.pile,
            pose = pose,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(18.dp))
    }
}

// ────────────────────────────────────────────────────────────────
// The door at the lifting — bolts, then weight (C-17 · C-01)
// ────────────────────────────────────────────────────────────────

@Composable
private fun DoorCeremony(
    pose: LiftCeremonyPose,
    waitingSlips: Int = 0,
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier
) {
    val lightState = LocalStrongroomLightState.current
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawRect(
            StrongroomLight.stateSurface(
                StrongroomInks.Graphite, lightState, StrongroomLitSurface.FACE
            )
        )

        // The frame and its dark opening.
        val frameX = w * 0.16f
        val frameY = h * 0.05f
        val frameW = w * 0.68f
        val frameH = h * 0.90f
        drawRect(
            color = StrongroomRoles.Metal,
            topLeft = Offset(frameX - 10.dp.toPx(), frameY - 10.dp.toPx()),
            size = Size(frameW + 20.dp.toPx(), frameH + 20.dp.toPx())
        )
        drawRect(
            color = StrongroomLight.relief(
                StrongroomInks.Graphite, StrongroomInks.Violet, 0.8f, lightState
            ),
            topLeft = Offset(frameX - 10.dp.toPx(), frameY - 10.dp.toPx()),
            size = Size(frameW + 20.dp.toPx(), 3.dp.toPx())
        )
        // The opening: the room beyond, dark while the door stands
        // shut, opening onto darkness as the leaf leaves it — what
        // is behind the door is the room, not a reward.
        drawRect(
            color = StrongroomInks.Graphite,
            topLeft = Offset(frameX, frameY),
            size = Size(frameW, frameH)
        )

        // The bolts, on the frame's right jamb — iron that held
        // the door, withdrawing into their jamb housings as
        // their fractions run (reverse load order: the lowest,
        // heaviest bolt first). The jamb is a machined steel
        // strip let into the case; each bolt travels in its own
        // housing slot, a recess with a shadowed mouth — the
        // bolt is a grained bar with a bevelled nose, and under
        // strain (its slide not yet broken free) its nose edge
        // catches the light with the load it carries. A
        // withdrawn bolt is gone into the steel; its housing
        // stands empty, and reads empty.
        val boltFull = frameW * 0.16f
        val jambW = boltFull + 20.dp.toPx()
        val jambX = frameX + frameW - 6.dp.toPx()
        grainRect(
            topLeft = Offset(jambX, frameY + frameH * 0.10f),
            size = Size(jambW, frameH * 0.80f),
            cellPx = LiftingTexture.Species.STEEL.cellPx.dp.toPx()
        ) { cx, cy ->
            LiftingTexture.cellTone(
                StrongroomInks.Graphite, StrongroomInks.Violet,
                LiftingTexture.Species.STEEL, cx, cy,
                seed = 47, state = lightState
            )
        }
        drawRect(
            color = StrongroomLight.relief(
                StrongroomInks.Graphite, StrongroomInks.Violet, 0.8f, lightState
            ),
            topLeft = Offset(jambX, frameY + frameH * 0.10f),
            size = Size(2.5.dp.toPx(), frameH * 0.80f)
        )
        for (i in 0 until 3) {
            // Withdrawal order is heaviest-first; the door's bolts
            // seat top-to-bottom light-to-heavy, so withdrawal
            // runs bottom-to-top.
            val boltIndex = 2 - i
            val fraction = pose.boltFractions.getOrElse(boltIndex) { 0f }
            val extension = boltFull * (1f - fraction)
            val by = frameY + frameH * (0.24f + i * 0.26f)
            val slotH = 17.dp.toPx()
            // The housing: a recess slot in the jamb, longer than
            // the bolt's travel, its mouth shadowed, its end wall
            // the steel the bolt strikes home against at sealing.
            drawRect(
                color = StrongroomLight.descend(StrongroomInks.Graphite, 2),
                topLeft = Offset(jambX + 3.dp.toPx(), by - slotH / 2f),
                size = Size(jambW - 3.dp.toPx(), slotH)
            )
            drawRect(
                color = StrongroomLight.shadowPartner(StrongroomInks.Graphite),
                topLeft = Offset(jambX + 3.dp.toPx(), by - slotH / 2f),
                size = Size(jambW - 3.dp.toPx(), 2.dp.toPx())
            )
            if (extension <= 0.5f) continue
            val boltH = 11.dp.toPx()
            val boltX = frameX + frameW - extension
            // The bar itself: machined steel grain, locked to
            // the bolt (its cells ride its own frame, offset by
            // its drawn position's cell phase so the skin does
            // not swim as it travels).
            grainRect(
                topLeft = Offset(boltX, by - boltH / 2f),
                size = Size(extension, boltH),
                cellPx = LiftingTexture.Species.STEEL.cellPx.dp.toPx()
            ) { cx, cy ->
                LiftingTexture.cellTone(
                    StrongroomInks.Graphite, StrongroomInks.Violet,
                    LiftingTexture.Species.STEEL,
                    cx + (boltX / LiftingTexture.Species.STEEL.cellPx.dp.toPx()).toInt(),
                    cy, seed = 49 + boltIndex, state = lightState
                )
            }
            // Its top face catches the light along its length.
            drawRect(
                color = StrongroomLight.relief(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 0.7f, lightState
                ),
                topLeft = Offset(boltX, by - boltH / 2f),
                size = Size(extension, 2.2.dp.toPx())
            )
            // The nose: a bevelled leading end — a darker wedge,
            // and under strain a lit edge where the load stands.
            val bevel = 5.dp.toPx()
            val nosePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(boltX, by - boltH / 2f)
                lineTo(boltX + bevel, by)
                lineTo(boltX, by + boltH / 2f)
                close()
            }
            drawPath(
                nosePath,
                StrongroomLight.descend(StrongroomInks.Graphite, 1)
            )
            if (fraction > 0f && fraction <= 0.08f) {
                drawLine(
                    color = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.95f, lightState
                    ),
                    start = Offset(boltX + 1.dp.toPx(), by - boltH / 2f + 1.dp.toPx()),
                    end = Offset(boltX + 1.dp.toPx(), by + boltH / 2f - 1.dp.toPx()),
                    strokeWidth = 1.6.dp.toPx()
                )
            }
            drawCircle(
                color = StrongroomLight.descend(StrongroomInks.Graphite, 2),
                radius = 3.4.dp.toPx(),
                center = Offset(boltX + extension - 6.dp.toPx(), by)
            )
        }

        // The leaf, hinged at the frame's left. Its projected
        // width is the cosine of its weight-driven angle: at 0°
        // it fills the frame; at its 82° rest it stands as a
        // sliver against the jamb, its face turned away.
        val angleRad = (pose.doorAngleDeg * Math.PI / 180.0).toFloat()
        val leafW = (frameW * cos(angleRad.toDouble())).toFloat().coerceAtLeast(frameW * 0.07f)
        val leafH = frameH * 0.985f
        val leafX = frameX
        val leafY = frameY + frameH * 0.0075f
        val enamel = StrongroomLight.stateSurface(
            StrongroomInks.Violet, lightState, StrongroomLitSurface.FACE
        )
        val cell = LiftingTexture.Species.ENAMEL.cellPx.dp.toPx()
        var cy = 0
        var yy = leafY
        while (yy < leafY + leafH) {
            var cx = 0
            var xx = leafX
            while (xx < leafX + leafW) {
                val tone = LiftingTexture.cellTone(
                    enamel, StrongroomInks.Graphite,
                    LiftingTexture.Species.ENAMEL, cx, cy,
                    seed = 41, state = lightState
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
        // The hand-laid gold pinstripe, inset — it turns with the
        // leaf, narrowing as the face turns away.
        val pin = StrongroomLight.sealedPinstripe()
        val inset = 12.dp.toPx()
        if (leafW > inset * 2 + 8.dp.toPx()) {
            drawRect(
                color = pin,
                topLeft = Offset(leafX + inset, leafY + inset),
                size = Size(leafW - 2 * inset, 2.dp.toPx())
            )
            drawRect(
                color = pin,
                topLeft = Offset(leafX + inset, leafY + leafH - inset - 2.dp.toPx()),
                size = Size(leafW - 2 * inset, 2.dp.toPx())
            )
            drawRect(
                color = pin,
                topLeft = Offset(leafX + inset, leafY + inset),
                size = Size(2.dp.toPx(), leafH - 2 * inset)
            )
            drawRect(
                color = pin,
                topLeft = Offset(leafX + leafW - inset - 2.dp.toPx(), leafY + inset),
                size = Size(2.dp.toPx(), leafH - 2 * inset)
            )
        } else {
            // The turned leaf's lit edge: one gold line, the
            // pinstripe seen edge-on.
            drawRect(
                color = pin,
                topLeft = Offset(leafX + leafW - 2.dp.toPx(), leafY + inset),
                size = Size(2.dp.toPx(), leafH - 2 * inset)
            )
        }

        // Face furniture, drawn while the face still fronts the
        // room (it swings out of the drawing's plane as the leaf
        // turns): the dead dial and the great wheel, at rest —
        // machined solid (LEAD-MOVES): the dial a grained steel
        // disc in a brass ring with a cast hand; the wheel a
        // grained rim band with bolt heads around it, tapered
        // spokes and a stacked hub, as the picked print wears
        // its iron.
        if (leafW > frameW * 0.45f) {
            val lightDir = lightVectorFor(lightState)
            val dialC = Offset(leafX + leafW / 2f, leafY + leafH * 0.17f)
            val dialR = leafW * 0.13f
            grainDisc(
                center = dialC, radius = dialR,
                cellPx = LiftingTexture.Species.STEEL.cellPx.dp.toPx()
            ) { cx, cy ->
                LiftingTexture.cellTone(
                    StrongroomInks.Graphite, StrongroomInks.Violet,
                    LiftingTexture.Species.STEEL, cx, cy,
                    seed = 43, state = lightState
                )
            }
            grainAnnulus(
                center = dialC, outerR = dialR + 2.5.dp.toPx(), innerR = dialR - 1.5.dp.toPx(),
                cellPx = LiftingTexture.Species.BRASS.cellPx.dp.toPx()
            ) { cx, cy ->
                LiftingTexture.cellTone(
                    StrongroomInks.Gold, StrongroomInks.Graphite,
                    LiftingTexture.Species.BRASS, cx, cy,
                    seed = 45, state = lightState
                )
            }
            for (i in 0 until 12) {
                val a = (i * 30f) * (Math.PI / 180f).toFloat()
                val inner = dialC + Offset(sin(a) * dialR * 0.74f, -cos(a) * dialR * 0.74f)
                val outer = dialC + Offset(sin(a) * dialR * 0.92f, -cos(a) * dialR * 0.92f)
                drawLine(color = pin, start = inner, end = outer, strokeWidth = 2.dp.toPx())
            }
            taperedSpoke(
                center = dialC, angleDeg = 90.0,
                fromR = dialR * 0.10f, toR = dialR * 0.62f,
                fromHalfPx = 3.2.dp.toPx(), toHalfPx = 1.2.dp.toPx(),
                fill = pin,
                litEdge = StrongroomLight.sealedPinstripeLitLip(),
                shadowEdge = StrongroomLight.descend(StrongroomInks.Gold, 2),
                lightDir = lightDir
            )
            drawCircle(color = pin, radius = 3.dp.toPx(), center = dialC)

            val wheelC = Offset(leafX + leafW / 2f, leafY + leafH * 0.47f)
            val wheelR = leafW * 0.22f
            val rimIn = wheelR * 0.78f
            grainAnnulus(
                center = wheelC, outerR = wheelR, innerR = rimIn,
                cellPx = LiftingTexture.Species.STEEL.cellPx.dp.toPx()
            ) { cx, cy ->
                LiftingTexture.cellTone(
                    StrongroomInks.Graphite, StrongroomInks.Violet,
                    LiftingTexture.Species.STEEL, cx, cy,
                    seed = 51, state = lightState
                )
            }
            // Bolt heads around the rim, as the print wears them.
            for (i in 0 until 8) {
                val a = Math.toRadians(i * 45.0 + 22.0)
                val bc = wheelC + Offset(
                    cos(a).toFloat() * (wheelR + rimIn) / 2f,
                    sin(a).toFloat() * (wheelR + rimIn) / 2f
                )
                boltHead(
                    center = bc, radius = wheelR * 0.085f,
                    face = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.3f, lightState
                    ),
                    lit = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.85f, lightState
                    ),
                    shadow = StrongroomLight.descend(StrongroomInks.Graphite, 2)
                )
            }
            for (deg in listOf(90.0, 210.0, 330.0)) {
                taperedSpoke(
                    center = wheelC, angleDeg = deg,
                    fromR = wheelR * 0.16f, toR = rimIn,
                    fromHalfPx = wheelR * 0.10f, toHalfPx = wheelR * 0.065f,
                    fill = StrongroomLight.descend(StrongroomInks.Graphite, 1),
                    litEdge = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.85f, lightState
                    ),
                    shadowEdge = StrongroomLight.descend(StrongroomInks.Graphite, 3),
                    lightDir = lightDir
                )
            }
            grainDisc(
                center = wheelC, radius = wheelR * 0.20f,
                cellPx = LiftingTexture.Species.BRASS.cellPx.dp.toPx()
            ) { cx, cy ->
                LiftingTexture.cellTone(
                    StrongroomInks.Gold, StrongroomInks.Graphite,
                    LiftingTexture.Species.BRASS, cx, cy,
                    seed = 53, state = lightState
                )
            }
            drawCircle(color = StrongroomInks.Graphite, radius = wheelR * 0.08f, center = wheelC)
        }

        // ── THE HANDING BACK, at the threshold (C-11) ──
        // The people who waited stand at the door's foot as
        // wordless brass slips on the threshold — no name, no
        // printed count, no word — and as the lock trips each
        // is handed back in turn: it sinks into the threshold's
        // return slot on its own body and is gone into the
        // phone's own keeping, every one before the swing opens
        // and long before the plate is struck. What stands here
        // after the return is an empty threshold and the slot
        // that took them.
        if (waitingSlips > 0) {
            val drawn = minOf(waitingSlips, HandingBackMotion.DRAWN_SLIPS_MAX)
            val slipW = 30.dp.toPx()
            val slipH = 15.dp.toPx()
            val slotY = frameY + frameH - 3.dp.toPx()
            var sx = frameX + 16.dp.toPx()
            // The return slot: a dark cut in the threshold under
            // the slips' stand, rimmed in the threshold's gold.
            drawRect(
                color = StrongroomLight.descend(StrongroomInks.Graphite, 2),
                topLeft = Offset(frameX + 10.dp.toPx(), slotY),
                size = Size(drawn * (slipW + 9.dp.toPx()) + 3.dp.toPx(), 5.dp.toPx())
            )
            for (i in 0 until drawn) {
                val f = HandingBackMotion.fractionFor(
                    i, waitingSlips, pose.elapsedMs, reducedMotion
                ).toFloat()
                if (f < 1f) {
                    // The slip sinks into the slot on its body:
                    // the standing height above the threshold is
                    // the fraction not yet handed back.
                    val standingH = slipH * (1f - f)
                    val top = slotY - standingH
                    grainRect(
                        topLeft = Offset(sx, top),
                        size = Size(slipW, standingH),
                        cellPx = LiftingTexture.Species.BRASS.cellPx.dp.toPx()
                    ) { cx, cy ->
                        LiftingTexture.cellTone(
                            StrongroomInks.Gold, StrongroomInks.Graphite,
                            LiftingTexture.Species.BRASS, cx, cy,
                            seed = 61 + i * 7, state = lightState
                        )
                    }
                    drawRect(
                        color = StrongroomInks.Graphite,
                        topLeft = Offset(sx, top),
                        size = Size(slipW, 1.4.dp.toPx())
                    )
                    // The slip's punched corner — a held thing's
                    // mark, struck like the plate's holes in
                    // miniature; it carries no word.
                    if (f < 0.35f) {
                        drawCircle(
                            color = StrongroomInks.Graphite,
                            radius = 2.6.dp.toPx(),
                            center = Offset(sx + slipW - 6.dp.toPx(), top + 5.dp.toPx())
                        )
                    }
                }
                sx += slipW + 9.dp.toPx()
            }
        }

        // The threshold: one gold line where the door's swing ends.
        drawRect(
            color = pin,
            topLeft = Offset(frameX, frameY + frameH),
            size = Size(frameW, 2.5.dp.toPx())
        )
    }
}

// ────────────────────────────────────────────────────────────────
// The tray zone — the strike's landing + the pile (C-06·C-07·C-19)
// ────────────────────────────────────────────────────────────────

@Composable
private fun TrayZone(
    plate: StruckPlateView,
    pile: PlatePileView,
    pose: LiftCeremonyPose,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // The landing plate: present only from the strike — the
        // door strikes it, it falls the last distance into the
        // tray on its landing body and settles on the pile. Before
        // the strike there is no plate here and nothing counts
        // down to one.
        Box(modifier = Modifier.fillMaxWidth().height(124.dp)) {
            if (pose.struck) {
                val dropPx = ((1.0 - pose.plateDropFraction.coerceAtMost(1.0)) * -72.0).toFloat()
                StruckPlateCard(
                    plate = plate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = dropPx.dp)
                        // The plate arrives slightly askew and
                        // squares itself as the landing body
                        // settles — the overshoot past 1 rocks it
                        // a breath the other way, then rest.
                        .rotate(
                            ((1.0 - pose.plateDropFraction) * 2.2).toFloat()
                        )
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        PlatePileStrip(
            pile = pile,
            landingStruck = pose.struck,
            dipFraction = pose.pileDip.toFloat()
        )
    }
}

/**
 * THE STRUCK PLATE (C-06) — brass, struck once: its grain, its
 * graded punched holes at their places in the day, the hours
 * held struck at its head (TX-SHOW-10), the seal line in its
 * one lawful state (TX-SHOW-13), and — on a fallen day — the
 * bracket pair struck beneath (TX-SHOW-14). The plate carries
 * no other words and no figure of any other kind.
 */
@Composable
fun StruckPlateCard(plate: StruckPlateView, modifier: Modifier = Modifier) {
    val lightState = LocalStrongroomLightState.current
    val plateGround = StrongroomLight.stateSurface(
        StrongroomInks.Gold, lightState, StrongroomLitSurface.PLATE
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (plate.bracket != null) 124.dp else 104.dp)
            .background(plateGround, RoundedCornerShape(3.dp))
            .border(1.dp, StrongroomInks.Graphite, RoundedCornerShape(3.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Brass grain, from the plate's own gold — each day's
            // plate seeds its grain from its own date, so no two
            // plates in the pile carry the same skin.
            val cell = LiftingTexture.Species.BRASS.cellPx.dp.toPx()
            val seed = plate.filingDateIso.hashCode()
            var cy = 0
            var yy = 0f
            while (yy < size.height) {
                var cx = 0
                var xx = 0f
                while (xx < size.width) {
                    val tone = LiftingTexture.cellTone(
                        plateGround, StrongroomInks.Graphite,
                        LiftingTexture.Species.BRASS, cx, cy,
                        seed = seed, state = lightState
                    )
                    drawRect(
                        color = tone, topLeft = Offset(xx, yy),
                        size = Size(cell + 1f, cell + 1f)
                    )
                    xx += cell; cx++
                }
                yy += cell; cy++
            }
            // The punched holes: graded diameters a finger could
            // learn (reach 14 · held 9 · floor 5.5 dp), each at
            // its place in the day, punched through to graphite
            // with the strike's lit rim on its upper edge.
            val midY = size.height * 0.52f
            for (punch in plate.punches) {
                val d = when (punch.grade) {
                    LiftingPlate.PunchGrade.LARGE -> 14.dp.toPx()
                    LiftingPlate.PunchGrade.MEDIUM -> 9.dp.toPx()
                    LiftingPlate.PunchGrade.SMALL -> 5.5.dp.toPx()
                }
                val cx = 22.dp.toPx() + punch.dayFraction * (size.width - 44.dp.toPx())
                drawCircle(
                    color = StrongroomInks.Graphite, radius = d / 2f,
                    center = Offset(cx, midY)
                )
                drawArc(
                    color = StrongroomLight.shadowPartner(StrongroomInks.Gold),
                    startAngle = 180f, sweepAngle = 180f, useCenter = false,
                    topLeft = Offset(cx - d / 2f, midY - d / 2f),
                    size = Size(d, d), style = Stroke(width = 1.2.dp.toPx())
                )
            }
            // The plate's machined edge (LEAD-MOVES): a bevelled
            // top edge catching the light, a shadowed bottom
            // edge where the plate meets the pile — a struck
            // plate has thickness, and thickness reads at its rim.
            drawRect(
                color = StrongroomLight.relief(
                    StrongroomInks.Gold, StrongroomInks.Graphite, 0.55f, lightState
                ),
                topLeft = Offset.Zero,
                size = Size(size.width, 2.6.dp.toPx())
            )
            drawRect(
                color = StrongroomLight.descend(StrongroomInks.Gold, 2),
                topLeft = Offset(0f, size.height - 2.6.dp.toPx()),
                size = Size(size.width, 2.6.dp.toPx())
            )
            drawRect(
                color = StrongroomLight.relief(
                    StrongroomInks.Gold, StrongroomInks.Graphite, 0.3f, lightState
                ),
                topLeft = Offset.Zero,
                size = Size(2.dp.toPx(), size.height)
            )
        }
        // The hours held, struck in brass at the plate's head.
        Text(
            text = plate.hoursHeldText,
            style = StrongroomType.Anon2,
            color = StrongroomInks.Graphite,
            modifier = Modifier.align(Alignment.TopStart).padding(start = 14.dp, top = 10.dp)
        )
        // The seal line — exactly two lawful states, no third.
        Text(
            text = plate.sealLineText,
            style = StrongroomType.Anon1,
            color = StrongroomInks.Graphite,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 14.dp, top = 12.dp)
        )
        // The bracket pair, struck beneath on a fallen day: the
        // settled sentence, and its two hours as figures.
        plate.bracket?.let { bracket ->
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 14.dp, end = 14.dp, bottom = 10.dp)
            ) {
                Text(
                    text = bracket.sentence,
                    style = StrongroomType.Scr3,
                    color = StrongroomInks.Graphite
                )
                Text(
                    text = "${bracket.standingHourText} · ${bracket.fallenHourText}",
                    style = StrongroomType.Anon1,
                    color = StrongroomInks.Graphite
                )
            }
        }
    }
}

/**
 * THE PILE IN THE TRAY (C-07 · C-19) — brass on brass, resting
 * at honest angles under the landing plate; the tray also keeps
 * the governor's snapped seals, drawn as lead-grey discs split
 * at their snap. The pile is drawn, never counted: no number,
 * no label, no total stands anywhere in this strip.
 */
@Composable
private fun PlatePileStrip(
    pile: PlatePileView,
    landingStruck: Boolean,
    dipFraction: Float = 0f
) {
    val lightState = LocalStrongroomLightState.current
    // The pile's dip under the landing: the whole stack rides
    // down by the dip's depth (3 dp at full dip) and rings back
    // on the pile's own body — brass pressed by brass, settling.
    val dipDp = (dipFraction.coerceIn(-0.35f, 1f) * 3f).dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .offset(y = dipDp)
    ) {
        // The tray: a graphite receptacle with a gold rim — it
        // receives and keeps; it displays nothing.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(26.dp)
                .background(StrongroomRoles.Metal, RoundedCornerShape(3.dp))
                .border(1.dp, StrongroomInks.Gold, RoundedCornerShape(3.dp))
        )
        // The resting plates, newest on top of the stack, each at
        // its honest angle, rising as the pile grows.
        val resting = pile.resting.take(PlatePileView.DRAWN_RESTING_MAX)
        resting.forEachIndexed { depth, restingPlate ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = (22 + depth * 8).dp)
                    .rotate(PlatePileView.tiltDegFor(depth))
                    .fillMaxWidth(0.94f - depth * 0.03f)
                    .height(30.dp)
                    .background(
                        StrongroomLight.stateSurface(
                            StrongroomInks.Gold, lightState, StrongroomLitSurface.PLATE
                        ),
                        RoundedCornerShape(2.dp)
                    )
                    .border(1.dp, StrongroomInks.Graphite, RoundedCornerShape(2.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cell = LiftingTexture.Species.BRASS.cellPx.dp.toPx()
                    val seed = restingPlate.filingDateIso.hashCode()
                    var cy = 0
                    var yy = 0f
                    while (yy < size.height) {
                        var cx = 0
                        var xx = 0f
                        while (xx < size.width) {
                            val tone = LiftingTexture.cellTone(
                                StrongroomInks.Gold, StrongroomInks.Graphite,
                                LiftingTexture.Species.BRASS, cx, cy,
                                seed = seed, state = lightState
                            )
                            drawRect(
                                color = tone, topLeft = Offset(xx, yy),
                                size = Size(cell + 1f, cell + 1f)
                            )
                            xx += cell; cx++
                        }
                        yy += cell; cy++
                    }
                    // The resting plate's first holes, hinted at
                    // its exposed edge — the stack is brass all
                    // the way down, struck faces under struck faces.
                    for (punch in restingPlate.punches.take(4)) {
                        val d = when (punch.grade) {
                            LiftingPlate.PunchGrade.LARGE -> 8.dp.toPx()
                            LiftingPlate.PunchGrade.MEDIUM -> 5.5.dp.toPx()
                            LiftingPlate.PunchGrade.SMALL -> 3.5.dp.toPx()
                        }
                        val cx = 18.dp.toPx() +
                            punch.dayFraction * (size.width - 36.dp.toPx())
                        drawCircle(
                            color = StrongroomInks.Graphite, radius = d / 2f,
                            center = Offset(cx, size.height / 2f)
                        )
                    }
                }
            }
        }
        // The kept snapped seals, in the tray's corner: lead in
        // the graphite depths, split at the snap, kept — never
        // thrown away (C-19). One disc per snapped day in the
        // pile, drawn up to the tray's corner width.
        if (pile.snappedSealDates.isNotEmpty()) {
            Canvas(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 30.dp)
                    .size(width = 76.dp, height = 20.dp)
            ) {
                val count = minOf(pile.snappedSealDates.size, 4)
                for (i in 0 until count) {
                    val c = Offset(10.dp.toPx() + i * 18.dp.toPx(), size.height / 2f)
                    drawArc(
                        color = StrongroomInks.Graphite, startAngle = 100f,
                        sweepAngle = 160f, useCenter = true,
                        topLeft = Offset(c.x - 7.dp.toPx(), c.y - 7.dp.toPx()),
                        size = Size(14.dp.toPx(), 14.dp.toPx())
                    )
                    drawArc(
                        color = StrongroomInks.Graphite, startAngle = 285f,
                        sweepAngle = 155f, useCenter = true,
                        topLeft = Offset(c.x - 6.dp.toPx() + 2.dp.toPx(), c.y - 7.dp.toPx() + 1.dp.toPx()),
                        size = Size(14.dp.toPx(), 14.dp.toPx())
                    )
                    drawCircle(
                        color = StrongroomLight.sealedPinstripe(),
                        radius = 7.dp.toPx(), center = c,
                        style = Stroke(width = 0.8.dp.toPx())
                    )
                }
            }
        }
        // Before the strike (landingStruck = false) the pile
        // stands as it was left: resting plates only, the tray's
        // top empty until the strike lands today's plate.
    }
}

// ────────────────────────────────────────────────────────────────
// THE FAILURE FACE — the door ajar in a dark room (C-18)
// ────────────────────────────────────────────────────────────────

/**
 * The failure face: the SAME door, ajar in a dark room — the
 * sealed face with the seal gone and NOTHING ADDED (census
 * C-18: zero added words is this row's whole law). The lamp is
 * out (RELIGHT's DARK: subtraction only, no warm partner), the
 * leaf stands a hand's width off its frame with darkness in
 * the gap, and below it the day's plate lies in the tray
 * struck with the gap. No ceremony is played over a failure,
 * and nothing here offers, explains, comforts or recovers.
 */
@Composable
private fun FailureFace(
    plate: StruckPlateView,
    pile: PlatePileView,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalStrongroomLightState provides StrongroomLightState.DARK
    ) {
        val darkState = StrongroomLightState.DARK
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    StrongroomLight.stateSurface(
                        StrongroomInks.Graphite, darkState, StrongroomLitSurface.FACE
                    )
                )
                .semantics { contentDescription = "ULYSSES" }
        ) {
            Canvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val w = size.width
                val h = size.height
                drawRect(
                    StrongroomLight.stateSurface(
                        StrongroomInks.Graphite, darkState, StrongroomLitSurface.FACE
                    )
                )
                val frameX = w * 0.16f
                val frameY = h * 0.05f
                val frameW = w * 0.68f
                val frameH = h * 0.90f
                // The case, unlit: graphite mass in a dark room.
                drawRect(
                    color = StrongroomInks.Graphite,
                    topLeft = Offset(frameX - 10.dp.toPx(), frameY - 10.dp.toPx()),
                    size = Size(frameW + 20.dp.toPx(), frameH + 20.dp.toPx())
                )
                // The opening, darker than the room.
                drawRect(
                    color = StrongroomLight.blendEighth(
                        StrongroomInks.Graphite,
                        StrongroomLight.shadowPartner(StrongroomInks.Graphite), 3
                    ),
                    topLeft = Offset(frameX, frameY),
                    size = Size(frameW, frameH)
                )
                // The leaf, AJAR: off its frame by a hand's width,
                // the gap between leaf and frame pure darkness.
                // Its enamel is the same enamel, unlit — in DARK
                // the grain's lit side never moves.
                val gapPx = 16.dp.toPx()
                val leafX = frameX + gapPx
                val leafY = frameY + frameH * 0.0075f
                val leafW = frameW * 0.94f
                val leafH = frameH * 0.985f
                val enamel = StrongroomLight.stateSurface(
                    StrongroomInks.Violet, darkState, StrongroomLitSurface.FACE
                )
                val cell = LiftingTexture.Species.ENAMEL.cellPx.dp.toPx()
                var cy = 0
                var yy = leafY
                while (yy < leafY + leafH) {
                    var cx = 0
                    var xx = leafX
                    while (xx < leafX + leafW) {
                        val tone = LiftingTexture.cellTone(
                            enamel, StrongroomInks.Graphite,
                            LiftingTexture.Species.ENAMEL, cx, cy,
                            seed = 41, state = darkState
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
                // The pinstripe, unlit gold — the same hand-laid
                // line, in a room with no lamp.
                val pin = StrongroomInks.Gold
                val inset = 12.dp.toPx()
                drawRect(
                    color = pin,
                    topLeft = Offset(leafX + inset, leafY + inset),
                    size = Size(leafW - 2 * inset, 2.dp.toPx())
                )
                drawRect(
                    color = pin,
                    topLeft = Offset(leafX + inset, leafY + leafH - inset - 2.dp.toPx()),
                    size = Size(leafW - 2 * inset, 2.dp.toPx())
                )
                drawRect(
                    color = pin,
                    topLeft = Offset(leafX + inset, leafY + inset),
                    size = Size(2.dp.toPx(), leafH - 2 * inset)
                )
                drawRect(
                    color = pin,
                    topLeft = Offset(leafX + leafW - inset - 2.dp.toPx(), leafY + inset),
                    size = Size(2.dp.toPx(), leafH - 2 * inset)
                )
                // The wheel at rest and the dead dial — the same
                // furniture, unlit. Nothing has been added to
                // this face, and nothing taken away but the seal.
                // Machined solid as on the lifting face
                // (LEAD-MOVES) — in the dark room it reads by
                // its grain and edges alone.
                val wheelC = Offset(leafX + leafW / 2f, leafY + leafH * 0.47f)
                val wheelR = leafW * 0.22f
                val darkLight = lightVectorFor(darkState)
                grainAnnulus(
                    center = wheelC, outerR = wheelR, innerR = wheelR * 0.78f,
                    cellPx = LiftingTexture.Species.STEEL.cellPx.dp.toPx()
                ) { cellX, cellY ->
                    LiftingTexture.cellTone(
                        StrongroomInks.Graphite, StrongroomInks.Violet,
                        LiftingTexture.Species.STEEL, cellX, cellY,
                        seed = 51, state = darkState
                    )
                }
                for (deg in listOf(90.0, 210.0, 330.0)) {
                    taperedSpoke(
                        center = wheelC, angleDeg = deg,
                        fromR = wheelR * 0.16f, toR = wheelR * 0.78f,
                        fromHalfPx = wheelR * 0.10f, toHalfPx = wheelR * 0.065f,
                        fill = StrongroomLight.descend(StrongroomInks.Graphite, 1),
                        litEdge = StrongroomLight.relief(
                            StrongroomInks.Graphite, StrongroomInks.Violet, -0.3f, darkState
                        ),
                        shadowEdge = StrongroomLight.descend(StrongroomInks.Graphite, 3),
                        lightDir = darkLight
                    )
                }
                grainDisc(
                    center = wheelC, radius = wheelR * 0.20f,
                    cellPx = LiftingTexture.Species.BRASS.cellPx.dp.toPx()
                ) { cellX, cellY ->
                    LiftingTexture.cellTone(
                        StrongroomInks.Gold, StrongroomInks.Graphite,
                        LiftingTexture.Species.BRASS, cellX, cellY,
                        seed = 53, state = darkState
                    )
                }
                drawCircle(color = StrongroomInks.Graphite, radius = wheelR * 0.08f, center = wheelC)
            }
            // The gapped plate in the tray, and the pile beneath
            // it — struck with the gap, read in the dark room's
            // own light.
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                StruckPlateCard(plate = plate, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                PlatePileStrip(pile = pile, landingStruck = true)
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}
