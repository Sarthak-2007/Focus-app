package com.example.ui.drawer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.domain.LiftingPlate
import com.example.ui.foundation.LocalStrongroomLightState
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType
import kotlin.math.roundToInt

/**
 * C-22 · THE DRAWER — the quiet place where finished days are
 * kept. Authored by BUILD-SR-41 from the rebuild element census
 * (INVENTORY-SR-1), firsthand, with the brief's merge of rows:
 * the Drawer (C-22) holds the Plate Stack (C-07) as its contents
 * in the Tray (C-19), and the Handing Back (C-11) is its
 * threshold act. The pure law stands in [DrawerMechanisms]; this
 * file renders it.
 *
 * THE FORM, AGAINST THE CONDEMNED FORM: a drawer that reads as
 * a history LIST is the condemned form — so there is no list
 * here. There is a wooden drawer in the room's furniture,
 * standing in the DOORWAY place (BUILD-SR-34's accounting:
 * VAULT → drawer-as-doorway-furniture), shut at rest as a dark
 * wood front with a brass pull. His hand opens it — rarely, and
 * only in unsealed hours; while a seal stands the doorway place
 * itself is the sealed face and this furniture does not exist
 * (ledger D-7). The drawer slides open on its heavy body
 * ([DrawerSlide.Body]: mass, a breath of overshoot, detents at
 * shut and drawn — poses are the body's, never an easing curve).
 *
 * THE THRESHOLD (C-11): before the plate is read, the day is
 * handed back. If anyone waited through the days kept here, the
 * drawer's first standing is the handing back itself: the
 * waiting slips lie at the threshold and are handed back into
 * his phone's own list by the room's hand — mute slips, no
 * word, no count, no name (C-11 prints nothing new) — and only
 * when the host confirms delivery do the plates become
 * liftable ([HandingBackGate]). The drawer resolves nobody
 * TAKEN or LEFT: that choice is the held surface's, per call,
 * at its own hour — never a browsing act in here.
 *
 * INSIDE: the plates lie in their pile — brass on brass,
 * overlapped, newest on top, the pile's height growing with
 * the season: the growing pile IS the progress display (E-2),
 * and it is the only one. Nothing is summed, graded, streaked,
 * or totalled anywhere in this file — the model carries no
 * such operation and the surface mints no such figure, not
 * even a count of the plates. A plate is LIFTED by his hand
 * and looked at one by one: it rises out of the pile on its
 * own body ([DrawerSlide.PlateBody]), settles into the hand's
 * view whole — its struck holes at their graded sizes, its
 * seal line in exactly its two lawful states, the day's task
 * in his own words where one stands — and settles back into
 * the stack when he puts it down. In the drawer's back the
 * TRAY keeps the governor's snapped lead seals (C-19), one
 * kept seal per crank day, visible at a glance. Below the
 * pile, the drawer's older keeping: the stored finished
 * tasks as slips in his own words (C-22's stored tasks; the
 * store's drawerPage rows), lying quiet — stored, not
 * displayed; they prompt nothing.
 *
 * WORDS: the drawer adds NO word of its own — no title, no
 * header, no count, no empty-state sentence (GAP-R03: an empty
 * drawer is the furniture, empty). The only text rendered is
 * a kept plate's own: the seal line (TX-SHOW-13) and the task
 * in his words (TX-SAY-07), struck on the brass at the strike
 * and merely READ here. The hours-held figure (TX-SHOW-10) is
 * carried in the model and rendered nowhere: no format for it
 * was ever decided, and this surface mints none.
 *
 * R-6 · FLAG_SECURE: while this drawer stands open, the host
 * holds the window FLAG_SECURE ([drawerSurfaceSecure] — the
 * re-earned archive law, named in DrawerMechanisms). His kept
 * days are not screenshotted and do not thumbnail in Recents.
 *
 * TEXTURE (intake §1): wood-dark drawer furniture, brass
 * plates, steel fittings — grain derived from each surface
 * itself ([DrawerTexture]) inside the seven inks; no flat fill
 * carries a material surface, no image exists in this file.
 * All motion figures UNPROVEN-ON-DEVICE, as filed.
 */
data class DrawerState(
    /** The drawer's contents, derived whole by the host from the
     *  store's struck rows + stored tasks ([DrawerContents.derive]). */
    val contents: DrawerContents = DrawerContents.Empty,
    /** The handing back prepared for the drawer's threshold:
     *  the people who waited through the kept days, each once,
     *  in the order they first waited (P6's preparation). */
    val handingBack: LiftingPlate.HandingBack = LiftingPlate.HandingBack(emptyList()),
    /** The host's confirmation that the handing back was
     *  delivered into his phone's own list. The plates are not
     *  liftable until this stands (or nobody waited). */
    val handingBackDelivered: Boolean = false
)

data class DrawerActions(
    /** Execute the handing back: return the waiting people to
     *  his phone's own list, then confirm by standing
     *  [DrawerState.handingBackDelivered]. Called once, by the
     *  threshold, when the drawer has finished sliding open. */
    val onHandingBack: () -> Unit = {},
    /** Shut the drawer (his hand on the lip, or a downward drag
     *  of the drawer body). The drawer closes on its body and
     *  the doorway stands as it stood. */
    val onClose: () -> Unit = {}
)

// ------------------------------------------------------------------
// The shut drawer — furniture in the doorway room (C-22 at rest)
// ------------------------------------------------------------------

/**
 * The drawer SHUT: a dark wood front in the doorway's room, a
 * brass pull, a keyhole. It carries no word and no badge: a man
 * who knows the room knows the drawer. Tapping it is his hand
 * opening it — the host stands the open drawer in its place.
 */
@Composable
fun DrawerFront(onOpen: () -> Unit, modifier: Modifier = Modifier) {
    val lightState = LocalStrongroomLightState.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .clickable { onOpen() }
            .semantics { contentDescription = "the drawer" }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // The wood front: graphite timber, grained along its run.
            val cell = DrawerTexture.Species.WOOD.cellPx.dp.toPx()
            var cy = 0
            var yy = 0f
            while (yy < size.height) {
                var cx = 0
                var xx = 0f
                while (xx < size.width) {
                    val tone = DrawerTexture.cellTone(
                        DrawerTexture.WoodInk, StrongroomInks.Violet,
                        DrawerTexture.Species.WOOD, cx, cy,
                        seed = 41, state = lightState
                    )
                    drawRect(
                        color = tone, topLeft = Offset(xx, yy),
                        size = Size(cell + 1f, cell + 1f)
                    )
                    xx += cell; cx++
                }
                yy += cell; cy++
            }
            // The frame's inset: a finer inner panel line, relieved
            // toward violet — joinery, not decoration.
            val inset = 9.dp.toPx()
            val joinery = StrongroomLight.relief(
                StrongroomInks.Graphite, StrongroomInks.Violet, 0.7f, lightState
            )
            drawRect(
                color = joinery,
                topLeft = Offset(inset, inset),
                size = Size(size.width - 2 * inset, 1.5.dp.toPx())
            )
            drawRect(
                color = joinery,
                topLeft = Offset(inset, size.height - inset - 1.5.dp.toPx()),
                size = Size(size.width - 2 * inset, 1.5.dp.toPx())
            )
            // The brass top edge: where the drawer meets the room.
            drawRect(
                color = StrongroomLight.sealedPinstripe(),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, 2.dp.toPx())
            )
            // The pull: a brass bail handle — a bar on two posts,
            // centred, at the hand's height.
            val pullW = size.width * 0.30f
            val pullCx = size.width / 2f
            val pullY = size.height * 0.46f
            val brass = StrongroomInks.Gold
            drawRect(
                color = brass,
                topLeft = Offset(pullCx - pullW / 2f, pullY),
                size = Size(pullW, 4.dp.toPx())
            )
            drawRect(
                color = brass,
                topLeft = Offset(pullCx - pullW / 2f, pullY - 7.dp.toPx()),
                size = Size(4.dp.toPx(), 7.dp.toPx())
            )
            drawRect(
                color = brass,
                topLeft = Offset(pullCx + pullW / 2f - 4.dp.toPx(), pullY - 7.dp.toPx()),
                size = Size(4.dp.toPx(), 7.dp.toPx())
            )
            // The keyhole escutcheon below the pull: a small brass
            // plate, its hole graphite — the drawer's one lock,
            // and it is never locked against him.
            val escCx = pullCx
            val escCy = size.height * 0.76f
            drawCircle(color = brass, radius = 6.dp.toPx(), center = Offset(escCx, escCy))
            drawCircle(
                color = StrongroomInks.Graphite, radius = 2.2.dp.toPx(),
                center = Offset(escCx, escCy - 1.dp.toPx())
            )
            drawRect(
                color = StrongroomInks.Graphite,
                topLeft = Offset(escCx - 1.1.dp.toPx(), escCy),
                size = Size(2.2.dp.toPx(), 4.dp.toPx())
            )
        }
    }
}

// ------------------------------------------------------------------
// The open drawer (C-22 · C-07 · C-19 · C-11)
// ------------------------------------------------------------------

@Composable
fun Drawer(
    state: DrawerState,
    actions: DrawerActions,
    modifier: Modifier = Modifier
) {
    val lightState = LocalStrongroomLightState.current

    // The slide: the drawer body travels shut → drawn on its own
    // body from the moment it stands. Poses are read off the
    // body, never eased.
    var slideMs by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        val start = withFrameMillis { it }
        while (slideMs < SLIDE_CAP_MS) {
            val now = withFrameMillis { it }
            slideMs = (now - start).toInt().coerceAtMost(SLIDE_CAP_MS)
        }
    }
    val travel = DrawerSlide.travelAt(0.0, 1.0, slideMs).toFloat()
    val slideSettled = slideMs >= SLIDE_CAP_MS || travel >= 0.999f

    val revealed = HandingBackGate.platesRevealed(state.handingBack, state.handingBackDelivered)

    // The threshold act fires once the drawer stands open: the
    // room's hand delivers the waiting people, the host confirms.
    var handingFired by remember { mutableStateOf(false) }
    LaunchedEffect(slideSettled, revealed) {
        if (slideSettled && !revealed && !handingFired) {
            handingFired = true
            actions.onHandingBack()
        }
    }

    var liftedIso by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StrongroomRoles.Field)
            .offset { IntOffset(0, ((1f - travel) * 36.dp.toPx()).roundToInt()) }
            .semantics { contentDescription = "the drawer" }
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // The drawer's wooden body: side walls + back, grained.
            DrawerBody(lightState)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                if (!revealed) {
                    HandingBackThreshold(state.handingBack, lightState)
                } else {
                    val contents = state.contents
                    if (contents.platesNewestFirst.isNotEmpty()) {
                        PlatePile(
                            contents = contents,
                            liftedIso = liftedIso,
                            onLift = { liftedIso = it },
                            onSettle = { liftedIso = null },
                            lightState = lightState
                        )
                    }
                    // The tray's kept seals (C-19) — drawn whenever
                    // a crank day stands in the pile.
                    val sealDays = contents.snappedSealDays()
                    if (sealDays.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        KeptSeals(sealDays.size, lightState)
                    }
                    if (contents.tasksNewestFirst.isNotEmpty()) {
                        Spacer(Modifier.height(18.dp))
                        TaskSlips(contents.tasksNewestFirst, lightState)
                    }
                    // An empty drawer renders none of the above:
                    // the wooden body, empty — stillness (GAP-R03).
                }
                Spacer(Modifier.height(12.dp))
            }
        }
        DrawerLip(onClose = actions.onClose, lightState = lightState)
    }
}

private const val SLIDE_CAP_MS: Int = 1600

/** The drawer's wooden body: back panel and side cheeks, grained
 *  wood in graphite, the back relieved a breath toward violet so
 *  the brass lying in it reads against depth, not flatness. */
@Composable
private fun DrawerBody(lightState: StrongroomLightState) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cell = DrawerTexture.Species.WOOD.cellPx.dp.toPx()
        var cy = 0
        var yy = 0f
        while (yy < size.height) {
            var cx = 0
            var xx = 0f
            while (xx < size.width) {
                val tone = DrawerTexture.cellTone(
                    DrawerTexture.WoodInk, StrongroomInks.Violet,
                    DrawerTexture.Species.WOOD, cx, cy,
                    seed = 43, state = lightState
                )
                drawRect(
                    color = tone, topLeft = Offset(xx, yy),
                    size = Size(cell + 1f, cell + 1f)
                )
                xx += cell; cx++
            }
            yy += cell; cy++
        }
        // The side cheeks: steel runners the drawer slides on,
        // at both edges — machinery at the furniture's flank.
        val runner = StrongroomLight.relief(
            StrongroomInks.Graphite, StrongroomInks.Violet, 0.85f, lightState
        )
        drawRect(
            color = runner, topLeft = Offset(0f, 0f),
            size = Size(5.dp.toPx(), size.height)
        )
        drawRect(
            color = runner, topLeft = Offset(size.width - 5.dp.toPx(), 0f),
            size = Size(5.dp.toPx(), size.height)
        )
    }
}

// ------------------------------------------------------------------
// The threshold — the handing back (C-11)
// ------------------------------------------------------------------

/**
 * The waiting people, as mute slips at the drawer's threshold.
 * No word, no count, no name is rendered — the slips carry
 * nothing (C-11 prints nothing new; the held surface is where
 * a held thing is ever shown whole, never here). The slips lie
 * still while the drawer finishes its slide; then the room's
 * hand takes them — the host's delivery — and the plates stand
 * revealed behind them.
 */
@Composable
private fun HandingBackThreshold(
    handingBack: LiftingPlate.HandingBack,
    lightState: StrongroomLightState
) {
    Column {
        handingBack.waiting.forEachIndexed { index, _ ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .height(44.dp)
                    .background(
                        StrongroomLight.blendEighth(
                            StrongroomInks.Gold, StrongroomInks.Graphite, 5
                        ),
                        RoundedCornerShape(2.dp)
                    )
                    .border(1.dp, StrongroomInks.Gold, RoundedCornerShape(2.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cell = DrawerTexture.Species.BRASS.cellPx.dp.toPx()
                    var cy = 0
                    var yy = 0f
                    while (yy < size.height) {
                        var cx = 0
                        var xx = 0f
                        while (xx < size.width) {
                            val tone = DrawerTexture.cellTone(
                                StrongroomInks.Gold, StrongroomInks.Graphite,
                                DrawerTexture.Species.BRASS, cx, cy,
                                seed = 47 + index, state = lightState
                            )
                            drawRect(
                                color = tone, topLeft = Offset(xx, yy),
                                size = Size(cell + 1f, cell + 1f)
                            )
                            xx += cell; cx++
                        }
                        yy += cell; cy++
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------
// The pile — plates lying in the drawer (C-07 in C-19)
// ------------------------------------------------------------------

@Composable
private fun PlatePile(
    contents: DrawerContents,
    liftedIso: String?,
    onLift: (String) -> Unit,
    onSettle: () -> Unit,
    lightState: StrongroomLightState
) {
    val plates = contents.platesNewestFirst
    val lifted = liftedIso?.let { contents.plateFor(it) }

    Column {
        // The lifted plate stands first, in the hand's view,
        // whole — lifted and looked at, one by one.
        if (lifted != null) {
            LiftedPlate(lifted, onSettle, lightState)
            Spacer(Modifier.height(14.dp))
        }
        // The stack itself: plates overlapped, newest on top.
        // Buried plates show their struck band only; the top
        // plate (when none is lifted) shows whole. Tapping a
        // plate lifts it; tapping the lifted plate's place in
        // the stack settles it back.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(PileGeometry.pileHeightDp(plates.size).dp)
        ) {
            // Draw oldest first so newer plates lie over them.
            for (index in plates.indices.reversed()) {
                val plate = plates[index]
                val isTop = index == 0
                val isLifted = plate.filingDateIso == liftedIso
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (index * PileGeometry.BAND_DP).dp)
                        .height(
                            if (isTop && lifted == null) {
                                PileGeometry.PLATE_HEIGHT_DP.dp
                            } else {
                                PileGeometry.BAND_DP.dp
                            }
                        )
                        .clipToBounds()
                        .clickable {
                            if (isLifted) onSettle() else onLift(plate.filingDateIso)
                        }
                ) {
                    PlateFace(
                        plate = plate,
                        seed = 53 + index,
                        lightState = lightState
                    )
                }
            }
        }
    }
}

/** A plate lifted into the hand's view: it rises on its own body
 *  from the pile and settles, whole, above the stack. Tapping it
 *  puts it back — it settles into the pile on the same body. */
@Composable
private fun LiftedPlate(
    plate: DrawerPlate,
    onSettle: () -> Unit,
    lightState: StrongroomLightState
) {
    var riseMs by remember(plate.filingDateIso) { mutableIntStateOf(0) }
    LaunchedEffect(plate.filingDateIso) {
        riseMs = 0
        val start = withFrameMillis { it }
        while (riseMs < 1200) {
            val now = withFrameMillis { it }
            riseMs = (now - start).toInt().coerceAtMost(1200)
        }
    }
    val rise = DrawerSlide.plateRiseAt(0.0, 1.0, riseMs).toFloat()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    0,
                    (((1f - rise) * PileGeometry.LIFT_CLEARANCE_DP).dp).roundToPx()
                )
            }
            .clickable { onSettle() }
    ) {
        PlateFace(plate = plate, seed = 59, lightState = lightState, whole = true)
    }
}

/**
 * One brass plate's face. [whole] renders everything the strike
 * struck: the graded holes in a row, the seal line in its two
 * lawful states, the day's task in his words, the kept crank
 * seal's mark, and a fallen day's bracket pair where the strike
 * carried it. A band rendering (whole = false) is the plate's
 * struck edge only — holes along its rim, as they lie buried.
 */
@Composable
private fun PlateFace(
    plate: DrawerPlate,
    seed: Int,
    lightState: StrongroomLightState,
    whole: Boolean = true
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PileGeometry.PLATE_HEIGHT_DP.dp)
            .background(StrongroomInks.Gold, RoundedCornerShape(3.dp))
            .border(1.dp, StrongroomInks.Graphite, RoundedCornerShape(3.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Brass grain, from the plate's own gold.
            val cell = DrawerTexture.Species.BRASS.cellPx.dp.toPx()
            var cy = 0
            var yy = 0f
            while (yy < size.height) {
                var cx = 0
                var xx = 0f
                while (xx < size.width) {
                    val tone = DrawerTexture.cellTone(
                        StrongroomInks.Gold, StrongroomInks.Graphite,
                        DrawerTexture.Species.BRASS, cx, cy,
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
            // The punched holes, in struck order, at their graded
            // diameters (ledger E-9; the doorway strip's figures:
            // reach 11 · held 7.5 · floor 4.5 dp).
            val gap = 13.dp.toPx()
            var hx = 14.dp.toPx()
            val midY = if (whole) size.height / 2f else size.height * 0.5f
            for (punch in plate.punches) {
                val d = when (punch.grade) {
                    LiftingPlate.PunchGrade.LARGE -> 11.dp.toPx()
                    LiftingPlate.PunchGrade.MEDIUM -> 7.5.dp.toPx()
                    LiftingPlate.PunchGrade.SMALL -> 4.5.dp.toPx()
                }
                if (hx + d > size.width * 0.58f) break
                drawCircle(
                    color = StrongroomInks.Graphite, radius = d / 2f,
                    center = Offset(hx + d / 2f, midY)
                )
                // The hole's struck rim: a lit lip on its lower edge.
                drawArc(
                    color = StrongroomLight.blendEighth(
                        StrongroomInks.Gold, StrongroomInks.Graphite, 2
                    ),
                    startAngle = 20f, sweepAngle = 140f, useCenter = false,
                    topLeft = Offset(hx, midY - d / 2f),
                    size = Size(d, d),
                    style = Stroke(width = 1.dp.toPx())
                )
                hx += d + gap
            }
            // The kept crank seal's mark: a small lead disc struck
            // onto the plate's left foot — dull inside graphite
            // depths, its crimp cross cut (C-19's kept proof,
            // carried on the plate's own fact).
            if (whole && plate.crankSealKept) {
                val c = Offset(16.dp.toPx(), size.height - 13.dp.toPx())
                drawCircle(
                    color = StrongroomLight.blendEighth(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 2
                    ),
                    radius = 6.dp.toPx(), center = c
                )
                drawLine(
                    color = StrongroomInks.Graphite,
                    start = c + Offset(-4.dp.toPx(), 0f),
                    end = c + Offset(4.dp.toPx(), 0f),
                    strokeWidth = 1.2.dp.toPx()
                )
                drawLine(
                    color = StrongroomInks.Graphite,
                    start = c + Offset(0f, -4.dp.toPx()),
                    end = c + Offset(0f, 4.dp.toPx()),
                    strokeWidth = 1.2.dp.toPx()
                )
            }
        }
        if (whole) {
            // The seal line, in exactly its two lawful states
            // (TX-SHOW-13) — struck words on the brass, graphite.
            Text(
                text = if (plate.sealLine == DrawerSealLine.WHOLE) "seal whole" else "seal broken",
                style = StrongroomType.Anon1,
                color = StrongroomInks.Graphite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp, top = 8.dp)
            )
            // The day's task, in his own words (TX-SAY-07) — the
            // drawer's one lawful text, lying on its plate.
            plate.taskLine?.takeIf { it.isNotBlank() }?.let { task ->
                Text(
                    text = task,
                    style = StrongroomType.Scr1,
                    color = StrongroomInks.Graphite,
                    maxLines = 1,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 7.dp)
                        .width(190.dp)
                )
            }
            // A fallen day's bracket pair, in its settled words
            // (TX-SHOW-14) — rendered only where the strike itself
            // carried both hours; never invented to fill the plate.
            val standing = plate.bracketStandingHour
            val fallen = plate.bracketFallenHour
            if (standing != null && fallen != null) {
                Text(
                    text = "standing at the last check $standing · " +
                        "fallen by the first check that found it $fallen",
                    style = StrongroomType.Anon1,
                    color = StrongroomInks.Graphite,
                    maxLines = 1,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 30.dp, bottom = 7.dp)
                        .width(200.dp)
                )
            }
        }
    }
}

// ------------------------------------------------------------------
// The tray's kept seals (C-19) + the stored task slips (C-22)
// ------------------------------------------------------------------

/** The snapped lead seals, kept in the drawer's back: one dull
 *  lead disc per crank day, its crimp cross snapped — visible
 *  proof, never thrown away, counted by nobody aloud: the discs
 *  themselves are the record. */
@Composable
private fun KeptSeals(count: Int, lightState: StrongroomLightState) {
    Row(modifier = Modifier.padding(start = 2.dp)) {
        repeat(count) { index ->
            Canvas(modifier = Modifier.size(26.dp)) {
                val c = Offset(size.width / 2f, size.height / 2f)
                val lead = DrawerTexture.cellTone(
                    StrongroomLight.blendEighth(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 2
                    ),
                    StrongroomInks.Violet,
                    DrawerTexture.Species.STEEL, index, 0,
                    seed = 61, state = lightState
                )
                drawCircle(color = lead, radius = 10.dp.toPx(), center = c)
                drawCircle(
                    color = StrongroomInks.Graphite, radius = 10.dp.toPx(),
                    center = c, style = Stroke(width = 1.dp.toPx())
                )
                // The snap: two cut lines across the crimp, offset —
                // a broken seal, kept whole as proof.
                drawLine(
                    color = StrongroomInks.Graphite,
                    start = c + Offset(-7.dp.toPx(), -2.dp.toPx()),
                    end = c + Offset(7.dp.toPx(), 2.dp.toPx()),
                    strokeWidth = 1.6.dp.toPx()
                )
                drawLine(
                    color = StrongroomInks.Graphite,
                    start = c + Offset(-2.dp.toPx(), -7.dp.toPx()),
                    end = c + Offset(2.dp.toPx(), 7.dp.toPx()),
                    strokeWidth = 1.2.dp.toPx()
                )
            }
            Spacer(Modifier.width(8.dp))
        }
    }
}

/** The stored finished tasks, lying as slips in the drawer's
 *  lower keeping: graphite slips with a brass edge, each
 *  carrying its task in his own words and nothing else — no
 *  date-grade, no check mark, no verdict (D-8). */
@Composable
private fun TaskSlips(tasks: List<DrawerTaskSlip>, lightState: StrongroomLightState) {
    Column {
        tasks.forEach { task ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(
                        StrongroomLight.stateSurface(
                            StrongroomInks.Graphite, lightState,
                            com.example.ui.foundation.StrongroomLitSurface.FACE
                        ),
                        RoundedCornerShape(2.dp)
                    )
                    .border(1.dp, StrongroomInks.Gold, RoundedCornerShape(2.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = task.title,
                    style = StrongroomType.Scr1,
                    color = StrongroomInks.Gold
                )
            }
        }
    }
}

// ------------------------------------------------------------------
// The lip — the drawer's front edge, standing when open
// ------------------------------------------------------------------

/**
 * The drawer's front lip, standing at the foot of the open
 * drawer: the wood front's top portion with its brass pull —
 * his hand takes it to shut the drawer (a tap, or a downward
 * drag of the drawer body, which this lip also catches). The
 * drawer shuts on its body in the host's standing; the lip is
 * the handle of that act, never a navigation control.
 */
@Composable
private fun DrawerLip(onClose: () -> Unit, lightState: StrongroomLightState) {
    var dragTotal by remember { mutableStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { dragTotal = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        dragTotal += dragAmount
                        if (dragTotal > 48.dp.toPx()) {
                            dragTotal = 0f
                            onClose()
                        }
                    }
                )
            }
            .clickable { onClose() }
            .semantics { contentDescription = "shut the drawer" }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cell = DrawerTexture.Species.WOOD.cellPx.dp.toPx()
            var cy = 0
            var yy = 0f
            while (yy < size.height) {
                var cx = 0
                var xx = 0f
                while (xx < size.width) {
                    val tone = DrawerTexture.cellTone(
                        DrawerTexture.WoodInk, StrongroomInks.Violet,
                        DrawerTexture.Species.WOOD, cx, cy,
                        seed = 41, state = lightState
                    )
                    drawRect(
                        color = tone, topLeft = Offset(xx, yy),
                        size = Size(cell + 1f, cell + 1f)
                    )
                    xx += cell; cx++
                }
                yy += cell; cy++
            }
            drawRect(
                color = StrongroomLight.sealedPinstripe(),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, 2.dp.toPx())
            )
            // The pull, as on the shut front — the same handle.
            val pullW = size.width * 0.30f
            val pullCx = size.width / 2f
            val pullY = size.height * 0.52f
            drawRect(
                color = StrongroomInks.Gold,
                topLeft = Offset(pullCx - pullW / 2f, pullY),
                size = Size(pullW, 4.dp.toPx())
            )
            drawRect(
                color = StrongroomInks.Gold,
                topLeft = Offset(pullCx - pullW / 2f, pullY - 7.dp.toPx()),
                size = Size(4.dp.toPx(), 7.dp.toPx())
            )
            drawRect(
                color = StrongroomInks.Gold,
                topLeft = Offset(pullCx + pullW / 2f - 4.dp.toPx(), pullY - 7.dp.toPx()),
                size = Size(4.dp.toPx(), 7.dp.toPx())
            )
        }
    }
}
