package com.example.ui.`break`

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.domain.StrongroomDay
import com.example.ui.antechamber.AntechamberModel
import com.example.ui.antechamber.AntechamberToken
import com.example.ui.antechamber.HeldSlip
import com.example.ui.foundation.LocalStrongroomLightState
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType
import com.example.ui.foundation.boltHead
import com.example.ui.foundation.clubTooth
import com.example.ui.foundation.grainAnnulus
import com.example.ui.foundation.grainDisc
import com.example.ui.foundation.grainRect
import com.example.ui.foundation.lightVectorFor
import com.example.ui.foundation.taperedSpoke
import kotlin.math.cos
import kotlin.math.sin

/**
 * C-16 · THE BREAK — the sealed day's one recess, authored by
 * BUILD-SR-39 from the rebuild element census (INVENTORY-SR-1)
 * and the Stage 4 State Rows' X1 block, firsthand. The pure law
 * stands in BreakMechanisms.kt; this file renders it.
 *
 * THE SURFACE IS THE FIRING, RENDERED — NEVER AN INVOCATION.
 * The shell stands this place only while the engine's break
 * span runs (its derivation is the session's standing, not a
 * route), and [BreakActions] carries NO start, stop, extend,
 * shorten or summon act — by type, not by discipline: the
 * census's fence (F-M06: no break object, break button, or
 * break menu) is executed as the absence of those verbs. What
 * his hand can do here is only what the held room already
 * allows: take what is within reach, choose per held slip, and
 * knock on the leaf — the knock is answered as it is answered
 * everywhere in the seal.
 *
 * WHAT STANDS HERE, in order:
 *
 *  1. THE EASED LEAF, head of the surface: the door itself,
 *     eased on its cam by the setting — a crack of the room's
 *     lamplight at its edge (orange AS LIGHT only, through
 *     RELIGHT), the three bolts eased off their seats by their
 *     filed share and no further. The leaf is not open; the
 *     seal stands. A tap on the leaf is a reach, and is knocked.
 *  2. THE ESCAPEMENT, in its case window in the leaf: the
 *     time-lock's own wheel — gold teeth ahead of the pallet
 *     (the span not yet run), graphite behind it (the span
 *     spent), the pallet fork rocking tooth by tooth. The
 *     remaining break is the gold ARC: readable at a glance,
 *     carried by no digit, no countdown, no word. The wheel is
 *     not the governor's crank (no hand winds it; it opens
 *     nothing; the crank hangs in the antechamber under its
 *     lead seal, a different mechanism in a different room) and
 *     not the drum (it spends nothing and turns nothing to him).
 *  3. THE ROOM AT REST — the break's interior, ruled inline
 *     from Stage 4 (T2-X1-FACE) as the antechamber's HELD
 *     STATE (GAP-COORD-08, flagged on the PR): the allowed
 *     things as signed, standing within reach on their rack —
 *     unmetered, because the break was priced whole at signing;
 *     the held pile in the corner, still held, its one quiet
 *     dot standing as it stands in the room; behind its step,
 *     each slip shown whole with the room's own choice. Calling
 *     out stands first in the room's law and is staged by
 *     nothing here — a known contact rings through natively
 *     during the break exactly as in the sealed hours, and the
 *     room writes no word over a call.
 *  4. THE RE-DRIVE: as the span's end arrives the ease comes
 *     off and the bolts drive home on the slice's score — the
 *     surface's last movement is the machine resealing itself.
 *     When the clock reads spent this surface's standing ends:
 *     the shell derives the sealed face from the same engine
 *     facts, and the seal stands whole again exactly as struck,
 *     with nothing renegotiated, nothing re-signed, no act of
 *     his anywhere in the return.
 *
 * LEAD-MOVES (2026-11-03): the firing machined solid — what
 * read as a diagram (teeth as lines, spokes as lines, the rim
 * a stroke circle, bolts as flat bars) is now drawn as mass
 * through the foundation's machining kit: the escape wheel a
 * grained steel rim with forty-eight solid club teeth and
 * tapered spokes, its grain locked to its own turning; the
 * pallet a solid anchor with gold stones; the bolts grained
 * bars travelling in a machined frame strip. The mechanisms,
 * the score and the law are untouched — only the metal grew.
 *
 * TEXTURE (intake §1): enamel, brass and steel grain derived
 * from each surface itself ([BreakTexture]); light is
 * RELIGHT's sealed state — the break stands INSIDE the seal
 * and its light is the sealed hours' light, warmed only at the
 * seam where the room's lamp spills. No image exists in this
 * file. WORDS: the labels are the phone's own (as signed), the
 * held lines are the arrivals' own, and the choice is the
 * room's ruled "take it / leave it" — the surface authors no
 * word of its own, names neither itself nor the break, and
 * counts nothing.
 */

/** The break's standing facts, supplied by the host from the
 *  kept engine (the UI renders the firing, never invokes it). */
data class BreakState(
    /** The sealed day's signing, exactly as signed. */
    val signing: StrongroomDay.SigningRecord,
    /** The break's clock: the engine's standing span, anchored. */
    val clock: BreakClock,
    /** Token faces: labels for the within-reach stock, the
     *  phone's own names. A name with no label here renders its
     *  package name — the phone's own identifier. */
    val tokens: List<AntechamberToken>,
    /** The waiting pile, first-waited first (the room's own
     *  derivation, reused whole). */
    val waiting: List<HeldSlip>,
    /** The one quiet dot: [waiting] non-empty, and nothing more
     *  about it is ever true (S4-C27). */
    val quietDot: Boolean,
    /** Held codes whose cover his TAKE has already lifted. */
    val revealedCodeIds: Set<Int> = emptySet()
)

/** The break's commands. NOTE WHAT IS NOT HERE: no start, no
 *  stop, no extend, no shorten, no summon — the firing is
 *  invocable by no one, and the type is the fence. Every act
 *  below is an act of the held room, lawful in the sealed hours
 *  and lawful here because the interior is that room at rest. */
data class BreakActions(
    /** Take a thing within reach: the host launches it through
     *  the phone's own launcher entry. No spend exists — the
     *  break was priced whole at signing. */
    val onLaunchAllowed: (packageName: String) -> Unit,
    /** Lift a held code's cover: the showing, priced and struck
     *  by the host exactly as in the antechamber (B-18). */
    val onShowCode: (slip: HeldSlip) -> Unit,
    /** Discharge a held slip by his choice: taken, or left
     *  (complete — no escalation, no count, no debt). */
    val onResolveHeld: (slip: HeldSlip, taken: Boolean) -> Unit,
    /** Take a held call: connects THIS call only, through the
     *  phone's own dialler. Not an unlock; grants nothing else. */
    val onTakeCall: (slip: HeldSlip) -> Unit,
    /** A reach landed on the leaf: the knock is answered. The
     *  seam for the Stage 13/14 substance, as on the face. */
    val onKnock: () -> Unit
)

private enum class BreakStep { ROOM, PILE }

@Composable
fun Break(
    state: BreakState,
    actions: BreakActions,
    nowElapsedMillis: Long,
    modifier: Modifier = Modifier
) {
    // The break stands inside the seal: its light is the sealed
    // hours' light (RELIGHT L-SEALED), whatever the room around
    // the shell is lit by.
    CompositionLocalProvider(LocalStrongroomLightState provides StrongroomLightState.SEALED) {
        BreakInSealedLight(state, actions, nowElapsedMillis, modifier)
    }
}

@Composable
private fun BreakInSealedLight(
    state: BreakState,
    actions: BreakActions,
    nowElapsedMillis: Long,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(BreakStep.ROOM) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StrongroomRoles.Field)
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "ULYSSES" }
    ) {
        EasedLeaf(state, actions, nowElapsedMillis)
        when (step) {
            BreakStep.ROOM -> {
                Spacer(Modifier.height(22.dp))
                WithinReachRack(state, actions)
                Spacer(Modifier.height(26.dp))
                HeldPileCorner(state = state, onOpen = { step = BreakStep.PILE })
                Spacer(Modifier.height(18.dp))
            }
            BreakStep.PILE -> {
                Spacer(Modifier.height(22.dp))
                HeldPileSlips(state, actions)
                Spacer(Modifier.height(10.dp))
                // The step back is the pile's own corner, reversed:
                // a plain brass plate, no navigation furniture.
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .size(width = 148.dp, height = 10.dp)
                        .background(
                            StrongroomLight.descend(StrongroomInks.Gold, 3),
                            RoundedCornerShape(2.dp)
                        )
                        .border(1.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(2.dp))
                        .clickable { step = BreakStep.ROOM }
                )
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

// ── The eased leaf + the escapement (the firing, rendered) ─────

@Composable
private fun EasedLeaf(
    state: BreakState,
    actions: BreakActions,
    nowElapsedMillis: Long
) {
    val lightState = LocalStrongroomLightState.current
    val clock = state.clock
    val ease = DoorEase.easeFraction(clock, nowElapsedMillis)
    val fractionElapsed = clock.fractionElapsedAt(nowElapsedMillis)
    val wheelAngle = Escapement.wheelAngleDeg(fractionElapsed)
    val palletAngle = Escapement.palletAngleDeg(fractionElapsed)
    val remainingArc = Escapement.remainingArcDeg(clock.fractionRemainingAt(nowElapsedMillis))
    val boltHome = listOf(0, 1, 2).map { BoltEase.homeFraction(it, clock, nowElapsedMillis) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(330.dp)
            .pointerInput(Unit) {
                detectTapGestures { actions.onKnock() }
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val enamel = StrongroomLight.stateSurface(
                StrongroomInks.Violet, lightState, com.example.ui.foundation.StrongroomLitSurface.FACE
            )
            // The leaf's enamel, grained cell by cell — the same
            // leaf the face prints on, eased.
            val cell = BreakTexture.Species.ENAMEL.cellPx.dp.toPx()
            var cy = 0
            var yy = 0f
            while (yy < h) {
                var cx = 0
                var xx = 0f
                while (xx < w) {
                    drawRect(
                        color = BreakTexture.cellTone(
                            enamel, StrongroomInks.Graphite,
                            BreakTexture.Species.ENAMEL, cx, cy,
                            seed = 43, state = lightState
                        ),
                        topLeft = Offset(xx, yy),
                        size = Size(cell + 1f, cell + 1f)
                    )
                    xx += cell; cx++
                }
                yy += cell; cy++
            }
            // The hand-laid pinstripe, held exact (a promoted read).
            val pin = StrongroomLight.sealedPinstripe()
            val inset = 14.dp.toPx()
            drawRect(color = pin, topLeft = Offset(inset, inset), size = Size(w - 2 * inset, 2.dp.toPx()))
            drawRect(color = pin, topLeft = Offset(inset, h - inset - 2.dp.toPx()), size = Size(w - 2 * inset, 2.dp.toPx()))
            drawRect(color = pin, topLeft = Offset(inset, inset), size = Size(2.dp.toPx(), h - 2 * inset))

            // THE SEAM OF LAMPLIGHT at the leaf's eased edge: the
            // room's warm light spilling through the crack the
            // setting made. Its width IS the ease (0 shut → its
            // full crack at ease 1 → 0 as the re-drive seats the
            // leaf). Orange as light only: every strip is a RELIGHT
            // eighths blend toward the lit partner, brightest at
            // the crack's heart, dying into the enamel.
            val seamW = (ease * 30.0).toFloat().dp.toPx()
            if (seamW > 0.5f) {
                val strips = 6
                for (s in 0 until strips) {
                    val t = s.toFloat() / strips
                    val tone = StrongroomLight.blendEighth(
                        StrongroomInks.Gold, StrongroomInks.Orange,
                        (8f * (1f - t)).toInt().coerceIn(0, 8)
                    )
                    drawRect(
                        color = tone,
                        topLeft = Offset(w - inset - seamW + seamW * t, inset),
                        size = Size(seamW / strips + 1f, h - 2 * inset)
                    )
                }
            }

            // THE BOLTS, eased off their seats by their filed
            // share — machined (LEAD-MOVES): a grained steel
            // frame strip stands at the leaf's edge, each bolt a
            // grained bar travelling in its own housing slot,
            // its drawn length the home fraction — at the
            // re-drive they travel home bolt by bolt on the
            // score, and the slot stands empty behind a bolt
            // fully eased.
            val stripW = 58.dp.toPx()
            val stripX = w - inset - stripW
            grainRect(
                topLeft = Offset(stripX, inset),
                size = Size(stripW, h - 2 * inset),
                cellPx = BreakTexture.Species.STEEL.cellPx.dp.toPx()
            ) { cx, cy ->
                BreakTexture.cellTone(
                    StrongroomInks.Graphite, StrongroomInks.Violet,
                    BreakTexture.Species.STEEL, cx, cy,
                    seed = 47, state = lightState
                )
            }
            drawRect(
                color = StrongroomLight.relief(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 0.8f, lightState
                ),
                topLeft = Offset(stripX, inset),
                size = Size(2.5.dp.toPx(), h - 2 * inset)
            )
            for (i in 0 until 3) {
                val by = h * (0.24f + i * 0.26f)
                val full = 40.dp.toPx()
                val len = full * boltHome[i].toFloat()
                // The housing slot the bolt travels in.
                drawRect(
                    color = StrongroomLight.descend(StrongroomInks.Graphite, 2),
                    topLeft = Offset(w - inset - full - 6.dp.toPx(), by - 7.5.dp.toPx()),
                    size = Size(full + 6.dp.toPx(), 15.dp.toPx())
                )
                drawRect(
                    color = StrongroomLight.shadowPartner(StrongroomInks.Graphite),
                    topLeft = Offset(w - inset - full - 6.dp.toPx(), by - 7.5.dp.toPx()),
                    size = Size(full + 6.dp.toPx(), 2.dp.toPx())
                )
                if (len <= 0.5f) continue
                val boltX = w - inset - len
                grainRect(
                    topLeft = Offset(boltX, by - 5.dp.toPx()),
                    size = Size(len, 10.dp.toPx()),
                    cellPx = BreakTexture.Species.STEEL.cellPx.dp.toPx()
                ) { cx, cy ->
                    BreakTexture.cellTone(
                        StrongroomInks.Graphite, StrongroomInks.Violet,
                        BreakTexture.Species.STEEL,
                        cx + (boltX / BreakTexture.Species.STEEL.cellPx.dp.toPx()).toInt(),
                        cy, seed = 49 + i, state = lightState
                    )
                }
                drawRect(
                    color = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.85f, lightState
                    ),
                    topLeft = Offset(boltX, by - 5.dp.toPx()),
                    size = Size(len, 2.dp.toPx())
                )
            }

            // ── THE ESCAPEMENT, in its case window ──
            val winC = Offset(w / 2f, h / 2f)
            val winR = 118.dp.toPx()
            // The case: graphite depth, a steel inner ring, a
            // brass rim — a window cut into the leaf, not a dial
            // printed on it. (The combination dial is the face's
            // furniture and stands dead elsewhere; this window is
            // the time-lock's case, and what moves in it is the
            // clock's wheel alone.)
            drawCircle(color = StrongroomInks.Graphite, radius = winR, center = winC)
            // The window's depth (LEAD-MOVES): the case is a
            // cavity in the leaf — an inner shadow ring where
            // the wall falls away, then a grained brass rim ring
            // proud of the enamel, screwed down at six points as
            // a case is screwed down.
            val lightDir = lightVectorFor(lightState)
            drawCircle(
                color = StrongroomInks.Graphite, radius = winR * 0.97f,
                center = winC, style = Stroke(width = winR * 0.07f)
            )
            drawCircle(
                color = StrongroomLight.shadowPartner(StrongroomInks.Graphite),
                radius = winR * 0.935f, center = winC, style = Stroke(width = 3.dp.toPx())
            )
            drawCircle(
                color = StrongroomLight.relief(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 0.6f, lightState
                ),
                radius = winR, center = winC, style = Stroke(width = 5.dp.toPx())
            )
            grainAnnulus(
                center = winC, outerR = winR + 8.dp.toPx(), innerR = winR + 1.5.dp.toPx(),
                cellPx = BreakTexture.Species.BRASS.cellPx.dp.toPx()
            ) { cx, cy ->
                BreakTexture.cellTone(
                    StrongroomInks.Gold, StrongroomInks.Graphite,
                    BreakTexture.Species.BRASS, cx, cy,
                    seed = 71, state = lightState
                )
            }
            for (s in 0 until 6) {
                val a = Math.toRadians(s * 60.0 + 30.0)
                boltHead(
                    center = winC + Offset(
                        cos(a).toFloat() * (winR + 4.75.dp.toPx()),
                        sin(a).toFloat() * (winR + 4.75.dp.toPx())
                    ),
                    radius = 4.6.dp.toPx(),
                    face = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.3f, lightState
                    ),
                    lit = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.85f, lightState
                    ),
                    shadow = StrongroomLight.descend(StrongroomInks.Graphite, 2)
                )
            }

            // The teeth: 48 around the wheel, turning WITH it.
            // A tooth stands in GOLD while its travel to the
            // pallet (the index at the window's top) still lies
            // inside the remaining arc; behind the pallet it
            // stands in graphite — the span, spent tooth by
            // tooth. At the firing every tooth is gold; at the
            // re-drive's end every tooth is graphite.
            val toothR = winR * 0.80f
            val stepDeg = 360.0 / Escapement.TEETH
            // The wheel's rim FIRST (LEAD-MOVES): a solid grained
            // steel band under the teeth, its grain locked to the
            // wheel's own turning — sampled in the wheel's frame,
            // so the skin turns with the iron and never swims.
            val rimOut = toothR * 0.86f
            val rimIn = winR * 0.58f
            grainAnnulus(
                center = winC, outerR = rimOut, innerR = rimIn,
                cellPx = BreakTexture.Species.STEEL.cellPx.dp.toPx(),
                rotationDeg = wheelAngle.toFloat()
            ) { cx, cy ->
                BreakTexture.cellTone(
                    StrongroomInks.Graphite, StrongroomInks.Violet,
                    BreakTexture.Species.STEEL, cx, cy,
                    seed = 53, state = lightState
                )
            }
            drawCircle(
                color = StrongroomLight.relief(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 0.5f, lightState
                ),
                radius = rimOut, center = winC, style = Stroke(width = 1.6.dp.toPx())
            )
            // The teeth: 48 SOLID CLUB TEETH around the wheel,
            // turning WITH it — a root, a tip, two flanks lit and
            // shadowed by the room's light. A tooth stands in GOLD
            // while its travel to the pallet (the index at the
            // window's top) still lies inside the remaining arc;
            // behind the pallet it stands in machined steel — the
            // span, spent tooth by tooth. At the firing every
            // tooth is gold; at the re-drive's end every tooth is
            // steel.
            for (i in 0 until Escapement.TEETH) {
                val base = -90.0 + i * stepDeg
                val theta = base + wheelAngle
                val travel = (270.0 - ((theta % 360.0) + 360.0) % 360.0 + 360.0) % 360.0
                val gold = travel <= remainingArc + stepDeg / 2.0 && remainingArc > 0.0
                val fill = if (gold) {
                    BreakTexture.cellTone(
                        StrongroomInks.Gold, StrongroomInks.Graphite,
                        BreakTexture.Species.BRASS, i, 3, seed = 59, state = lightState
                    )
                } else {
                    BreakTexture.cellTone(
                        StrongroomInks.Graphite, StrongroomInks.Violet,
                        BreakTexture.Species.STEEL, i, 5, seed = 67, state = lightState
                    )
                }
                clubTooth(
                    center = winC, angleDeg = theta,
                    rootR = rimOut - 1.dp.toPx(), tipR = toothR * 1.13f,
                    rootHalfPx = 5.2.dp.toPx(), tipHalfPx = 3.4.dp.toPx(),
                    fill = fill,
                    litEdge = if (gold) {
                        StrongroomLight.relief(
                            StrongroomInks.Gold, StrongroomInks.Graphite, 0.6f, lightState
                        )
                    } else {
                        StrongroomLight.relief(
                            StrongroomInks.Graphite, StrongroomInks.Violet, 0.7f, lightState
                        )
                    },
                    shadowEdge = if (gold) {
                        StrongroomLight.descend(StrongroomInks.Gold, 2)
                    } else {
                        StrongroomLight.descend(StrongroomInks.Graphite, 3)
                    },
                    lightDir = lightDir
                )
            }
            // Its spokes — tapered castings, turning with the
            // wheel — and the hub stack at its heart.
            for (deg in listOf(0.0, 120.0, 240.0)) {
                taperedSpoke(
                    center = winC, angleDeg = deg + wheelAngle,
                    fromR = winR * 0.13f, toR = rimIn,
                    fromHalfPx = 8.dp.toPx(), toHalfPx = 5.dp.toPx(),
                    fill = StrongroomLight.descend(StrongroomInks.Graphite, 1),
                    litEdge = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.7f, lightState
                    ),
                    shadowEdge = StrongroomLight.descend(StrongroomInks.Graphite, 3),
                    lightDir = lightDir
                )
            }
            grainDisc(
                center = winC, radius = winR * 0.155f,
                cellPx = BreakTexture.Species.STEEL.cellPx.dp.toPx(),
                rotationDeg = wheelAngle.toFloat()
            ) { cx, cy ->
                BreakTexture.cellTone(
                    StrongroomInks.Graphite, StrongroomInks.Violet,
                    BreakTexture.Species.STEEL, cx, cy,
                    seed = 55, state = lightState
                )
            }
            // The throw cam on the hub: one solid gold lobe,
            // turning with the wheel — when the lobe meets the
            // pallet at the span's end, the bolts re-drive.
            val camA = Math.toRadians(-90.0 + wheelAngle)
            drawCircle(
                color = BreakTexture.cellTone(
                    StrongroomInks.Gold, StrongroomInks.Graphite,
                    BreakTexture.Species.BRASS, 3, 3, seed = 61, state = lightState
                ),
                radius = winR * 0.115f,
                center = winC + Offset(cos(camA).toFloat() * winR * 0.27f, sin(camA).toFloat() * winR * 0.27f)
            )
            grainDisc(
                center = winC, radius = winR * 0.095f,
                cellPx = BreakTexture.Species.BRASS.cellPx.dp.toPx(),
                rotationDeg = wheelAngle.toFloat()
            ) { cx, cy ->
                BreakTexture.cellTone(
                    StrongroomInks.Gold, StrongroomInks.Graphite,
                    BreakTexture.Species.BRASS, cx, cy,
                    seed = 63, state = lightState
                )
            }
            drawCircle(color = StrongroomInks.Graphite, radius = winR * 0.045f, center = winC)

            // THE PALLET FORK, astride the index at the window's
            // top: two steel prongs rocking on their arbor as the
            // teeth pass — still while the wheel is locked, thrown
            // at each release. Its arbor is the index: the one
            // fixed point of the window, in exact gold.
            val forkC = Offset(winC.x, winC.y - toothR)
            val palletRot = palletAngle.toFloat()
            // The anchor's boss: a grained steel disc on the
            // arbor, rocking with the fork.
            grainDisc(
                center = forkC, radius = 10.dp.toPx(),
                cellPx = BreakTexture.Species.STEEL.cellPx.dp.toPx(),
                rotationDeg = palletRot
            ) { cx, cy ->
                BreakTexture.cellTone(
                    StrongroomInks.Graphite, StrongroomInks.Violet,
                    BreakTexture.Species.STEEL, cx, cy,
                    seed = 69, state = lightState
                )
            }
            // Its two arms: tapered steel, rocking ±24° about the
            // arbor on the pallet's own angle, each ending in a
            // GOLD PALLET STONE — the faces the teeth strike.
            val prong = toothR * 0.30f
            for (sign in listOf(-1.0, 1.0)) {
                val armDeg = -90.0 + sign * 24.0 + palletAngle
                taperedSpoke(
                    center = forkC, angleDeg = armDeg,
                    fromR = 7.dp.toPx(), toR = prong,
                    fromHalfPx = 5.5.dp.toPx(), toHalfPx = 3.6.dp.toPx(),
                    fill = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.35f, lightState
                    ),
                    litEdge = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.7f, lightState
                    ),
                    shadowEdge = StrongroomLight.descend(StrongroomInks.Graphite, 3),
                    lightDir = lightDir
                )
                val a = Math.toRadians(armDeg)
                val stoneC = forkC + Offset(cos(a).toFloat() * prong, sin(a).toFloat() * prong)
                drawCircle(
                    color = BreakTexture.cellTone(
                        StrongroomInks.Gold, StrongroomInks.Graphite,
                        BreakTexture.Species.BRASS, 2, 2, seed = 73, state = lightState
                    ),
                    radius = 5.4.dp.toPx(), center = stoneC
                )
                drawArc(
                    color = StrongroomLight.relief(
                        StrongroomInks.Gold, StrongroomInks.Graphite, 0.6f, lightState
                    ),
                    startAngle = 180f, sweepAngle = 180f, useCenter = false,
                    topLeft = Offset(stoneC.x - 5.4.dp.toPx(), stoneC.y - 5.4.dp.toPx()),
                    size = Size(10.8.dp.toPx(), 10.8.dp.toPx()),
                    style = Stroke(width = 1.4.dp.toPx())
                )
            }
            // The counterweight tail below the arbor: the anchor
            // is balanced, and the tail is how it shows.
            taperedSpoke(
                center = forkC, angleDeg = 90.0 + palletAngle,
                fromR = 7.dp.toPx(), toR = toothR * 0.17f,
                fromHalfPx = 4.5.dp.toPx(), toHalfPx = 6.5.dp.toPx(),
                fill = StrongroomLight.descend(StrongroomInks.Graphite, 1),
                litEdge = StrongroomLight.relief(
                    StrongroomInks.Graphite, StrongroomInks.Violet, 0.5f, lightState
                ),
                shadowEdge = StrongroomLight.descend(StrongroomInks.Graphite, 3),
                lightDir = lightDir
            )
            drawCircle(color = pin, radius = 4.5.dp.toPx(), center = forkC)
        }
    }
}

// ── The within-reach rack (the room at rest, unmetered) ────────

@Composable
private fun WithinReachRack(state: BreakState, actions: BreakActions) {
    val stock = remember(state.signing) { BreakInterior.withinReach(state.signing) }
    val labels = remember(state.tokens) { state.tokens.associate { it.packageName to it.label } }
    if (stock.isEmpty()) {
        // Nothing was put within reach: the rack stands empty —
        // a bare brass rail. Stillness, no placeholder (GAP-R03).
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(10.dp)
                .background(
                    StrongroomLight.descend(StrongroomInks.Gold, 3),
                    RoundedCornerShape(2.dp)
                )
                .border(1.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(2.dp))
        )
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.Top
    ) {
        stock.forEach { pkg ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(end = 18.dp)
                    .clickable { actions.onLaunchAllowed(pkg) }
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val cell = BreakTexture.Species.BRASS.cellPx.dp.toPx()
                        var yy = 0f
                        var rowI = 0
                        while (yy < size.height) {
                            var xx = 0f
                            var colI = 0
                            while (xx < size.width) {
                                drawRect(
                                    color = BreakTexture.cellTone(
                                        StrongroomInks.Gold, StrongroomInks.Graphite,
                                        BreakTexture.Species.BRASS, colI, rowI,
                                        seed = 67, state = StrongroomLightState.SEALED
                                    ),
                                    topLeft = Offset(xx, yy),
                                    size = Size(cell + 1f, cell + 1f)
                                )
                                xx += cell; colI++
                            }
                            yy += cell; rowI++
                        }
                        drawCircle(
                            color = StrongroomInks.Graphite,
                            radius = size.minDimension / 2f - 1.5.dp.toPx(),
                            style = Stroke(width = 2.5.dp.toPx())
                        )
                    }
                    Text(
                        text = (labels[pkg] ?: pkg).take(1).uppercase(),
                        style = StrongroomType.Scr3,
                        color = StrongroomRoles.OnLeaf
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = labels[pkg] ?: pkg,
                    style = StrongroomType.Scr1,
                    color = StrongroomRoles.OnGround
                )
            }
        }
    }
}

// ── The held pile — the corner, and its second step ────────────

@Composable
private fun HeldPileCorner(state: BreakState, onOpen: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(96.dp)
            .clickable(enabled = state.waiting.isNotEmpty()) { onOpen() }
    ) {
        val layers = minOf(state.waiting.size, 6)
        for (i in 0 until layers) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (i * 7).dp, y = (-(layers - i) * 6).dp)
                    .size(width = 148.dp, height = 54.dp)
                    .background(
                        StrongroomLight.descend(StrongroomInks.Gold, 2 + (i % 2)),
                        RoundedCornerShape(3.dp)
                    )
                    .border(1.5.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(3.dp))
            )
        }
        if (layers == 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(width = 148.dp, height = 10.dp)
                    .background(
                        StrongroomLight.descend(StrongroomInks.Gold, 3),
                        RoundedCornerShape(2.dp)
                    )
                    .border(1.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(2.dp))
            )
        }
        if (state.quietDot) {
            // THE QUIET DOT (S4-C27): one dot, identical for one
            // waiting arrival or nine — no word, no count, no name.
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(11.dp)
                    .background(StrongroomInks.Magenta, CircleShape)
            )
        }
    }
}

@Composable
private fun HeldPileSlips(state: BreakState, actions: BreakActions) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        state.waiting.forEach { slip ->
            HeldSlipCard(slip, state, actions)
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun HeldSlipCard(
    slip: HeldSlip,
    state: BreakState,
    actions: BreakActions
) {
    val revealed = slip.kind == HeldSlip.Kind.CODE && slip.id in state.revealedCodeIds
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                StrongroomLight.descend(StrongroomInks.Gold, 4),
                RoundedCornerShape(3.dp)
            )
            .border(1.5.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(3.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            when (slip.kind) {
                HeldSlip.Kind.CALL -> {
                    Text(
                        text = AntechamberModel.heldLine(slip),
                        style = StrongroomType.Anon2,
                        color = StrongroomRoles.OnGround
                    )
                }
                HeldSlip.Kind.CODE -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (revealed) {
                            Text(
                                text = AntechamberModel.heldLine(slip),
                                style = StrongroomType.Anon2,
                                color = StrongroomRoles.OnGround
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(width = 118.dp, height = 26.dp)
                                    .background(StrongroomInks.Gold, RoundedCornerShape(2.dp))
                                    .border(1.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(2.dp))
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = AntechamberModel.hourOf(slip.arrivedAtWallMillis),
                                style = StrongroomType.Anon2,
                                color = StrongroomRoles.OnGround
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row {
                Box(
                    modifier = Modifier
                        .background(StrongroomInks.Gold, RoundedCornerShape(3.dp))
                        .border(1.5.dp, StrongroomRoles.OnLeaf, RoundedCornerShape(3.dp))
                        .clickable {
                            when {
                                slip.kind == HeldSlip.Kind.CALL -> actions.onTakeCall(slip)
                                slip.kind == HeldSlip.Kind.CODE && !revealed ->
                                    actions.onShowCode(slip)
                                else -> actions.onResolveHeld(slip, true)
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = AntechamberModel.TAKE_WORD,
                        style = StrongroomType.Scr1,
                        color = StrongroomRoles.OnLeaf
                    )
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .background(StrongroomRoles.Field, RoundedCornerShape(3.dp))
                        .border(1.5.dp, StrongroomInks.Gold, RoundedCornerShape(3.dp))
                        .clickable { actions.onResolveHeld(slip, false) }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = AntechamberModel.LEAVE_WORD,
                        style = StrongroomType.Scr1,
                        color = StrongroomRoles.OnGround
                    )
                }
            }
        }
    }
}
