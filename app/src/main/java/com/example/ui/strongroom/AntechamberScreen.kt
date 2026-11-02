package com.example.ui.strongroom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.service.TactileSoundEngine
import com.example.ui.strongroom.layout.AntechamberLayout
import com.example.ui.strongroom.layout.AntechamberRoomLayout
import com.example.ui.strongroom.layout.HeldPileLayout
import com.example.ui.strongroom.layout.RoomGrid
import com.example.ui.theme.RelightState
import com.example.ui.theme.InkGold
import com.example.ui.theme.InkLine
import com.example.ui.theme.SigningBodyStyle
import com.example.ui.theme.SigningDigitsStyle
import com.example.ui.theme.SigningFiguresStyle
import com.example.ui.theme.SigningHandleStyle
import com.example.ui.theme.SigningHeadStyle
import com.example.ui.theme.SigningQuietStyle
import com.example.ui.theme.StrongroomLamp
import com.example.ui.theme.reliefHighlight
import com.example.ui.theme.reliefShade

/**
 * P5 — THE ANTECHAMBER (Slice 1; rethought under ruling C-3).
 *
 * The room behind P3's grille plate — reached by the deliberate
 * second touch, left by the same door. Its arrangement is the
 * list law (ledger B-9), consumed from [AntechamberModel] and —
 * since Stage 8/10 composition (BUILD-SR-16) — composed through
 * BUILD-SR-7's AntechamberLayout primitive (PR #843) and
 * BUILD-SR-10's room/shelf primitives (PR #853): the law is
 * structure now, not this surface's own column.
 *
 * TWO SETS, SURFACED DISTINCTLY (ruling C-3):
 *  - SET 1 — the signed allowed set: plain words exactly as
 *    signed, each opening DIRECTLY (OPEN) — no purse, no drum.
 *    They stand at every purse state.
 *  - SET 2 — THE DRUM, its own band below the list, never a row
 *    in it: CRANK turns one beyond-set app to him for its
 *    minute; its stock shows as the mechanism's own form — a
 *    strip of minute-marks that depletes (ruling R2-4: the
 *    purse's state is read from the mechanism, NEVER as a
 *    number on the surface). Spent: the crank will not turn,
 *    the route puts the hard stop in his hand, and the drum's
 *    stock is gone from the room.
 *
 * THE HELD PILE (second step, its own view) carries strangers
 * only — contacts ring through natively and never wait here.
 * Per call: TAKE IT / LEAVE IT, two handles, neither the lesser
 * (TI-35). Per code: the digits fork (B-18) — the price is
 * stated AT the taking, before he takes it (R2-4: "a showing
 * costs one minute" stands on the unshown row), and the shown
 * line is digits alone with the hour — no name, no words, no
 * way onward.
 *
 * The day-as-set panel at the foot is a READ-ONLY FACE: built
 * with no clickable anywhere in it, by construction.
 *
 * Not in this room, by the re-fit's own placement: the week's
 * emergency minute — behind a door of its own, a longer hold
 * on the seal itself (week-state P1's, firing P2's).
 */
@Composable
fun AntechamberScreen(
    initialState: AntechamberState,
    readOnlyLines: List<String>,
    onCrankOutcome: (CrankOutcome) -> Unit,
    onSignedOpen: (String) -> Unit,
    onShowingOutcome: (ShowingOutcome) -> Unit,
    onTakeCall: (HeldCall) -> Unit,
    onReturned: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var room by remember(initialState) { mutableStateOf(initialState) }
    var inPile by remember { mutableStateOf(false) }

    val ink = InkGold.reliefHighlight(RelightState.SIGNING)
    val quietInk = InkGold.copy(alpha = 0.75f)
    val lampShade = StrongroomLamp.reliefShade(RelightState.SIGNING)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(lampShade, InkLine)))
            .testTag("antechamber")
    ) {
        if (inPile) {
            HeldPileView(
                room = room,
                ink = ink,
                quietInk = quietInk,
                onBack = {
                    room = room.closePile()
                    inPile = false
                },
                onTake = { call ->
                    room = room.takeCall(call.id)
                    onTakeCall(call)
                },
                onLeaveCall = { call -> room = room.leaveCall(call.id) },
                onShow = { code ->
                    val (next, outcome) = room.showCode(code.id)
                    room = next
                    onShowingOutcome(outcome)
                }
            )
        } else {
            AntechamberListView(
                room = room,
                readOnlyLines = readOnlyLines,
                ink = ink,
                quietInk = quietInk,
                onOpenSigned = { app ->
                    val (next, outcome) = room.openSigned(app)
                    room = next
                    if (outcome == CrankOutcome.OPENED_FROM_THE_SIGNED_SET) {
                        onSignedOpen(app)
                    }
                    onCrankOutcome(outcome)
                },
                onCrank = { app ->
                    val (next, outcome) = room.crankTo(app)
                    room = next
                    onCrankOutcome(outcome)
                },
                onReturn = {
                    room = room.returnOpen()
                    onReturned()
                },
                onOpenPile = { inPile = true },
                onLeave = onLeave
            )
        }
    }
}

/**
 * The route form: the room's feel, wired from a surface-owned
 * engine — a signed opening as a latch (it was decided while he
 * was strong; opening it is a door, not a purchase), the drum's
 * turn as a weighted seat knock, the spent purse's hard stop at
 * full mass in the hand.
 */
@Composable
fun AntechamberRoute(
    initialState: AntechamberState,
    readOnlyLines: List<String>,
    onTakeCall: (HeldCall) -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val engine = remember(context) { TactileSoundEngine(context) }
    DisposableEffect(engine) { onDispose { engine.stopAllAudio() } }
    AntechamberScreen(
        initialState = initialState,
        readOnlyLines = readOnlyLines,
        onCrankOutcome = { outcome ->
            when (outcome) {
                CrankOutcome.TURNED -> engine.playSeatKnock(16, 0.85f)
                CrankOutcome.HARD_STOP_SPENT -> engine.playSeatKnock(24, 1.0f)
                CrankOutcome.ONE_ALREADY_OPEN -> engine.playFirmClick()
                else -> Unit
            }
        },
        onSignedOpen = { engine.playMechanicalLatch(engaged = true) },
        onShowingOutcome = { outcome ->
            when (outcome) {
                ShowingOutcome.SHOWN -> engine.playMechanicalLatch(engaged = true)
                ShowingOutcome.CANNOT_SPEND -> engine.playSeatKnock(24, 1.0f)
                else -> Unit
            }
        },
        onTakeCall = { call ->
            engine.playMechanicalLatch(engaged = true)
            onTakeCall(call)
        },
        onReturned = { engine.playMechanicalLatch(engaged = false) },
        onLeave = onLeave,
        modifier = modifier
    )
}

/**
 * The list view, composed through Stage 8's primitives at
 * composition (BUILD-SR-16): BUILD-SR-7's [AntechamberLayout]
 * carries B-9's order AS STRUCTURE — calling out · allowed as
 * signed · held pile behind its second step, no spent slot
 * anywhere — and BUILD-SR-10's [AntechamberRoomLayout] seats the
 * drum housing as a SIBLING of the list column under one room
 * root (Seam S-B ruled (b)): the drum is a zone of the room
 * (Z3, the machine wall), never a row in the list, and no
 * parameter of the list primitive can pass it in. The room's
 * foot furniture (the read-only day-as-set face, the leave line)
 * rides in the housing band, below the list — the room's foot,
 * as Slice 1 stood it.
 */
@Composable
private fun AntechamberListView(
    room: AntechamberState,
    readOnlyLines: List<String>,
    ink: Color,
    quietInk: Color,
    onOpenSigned: (String) -> Unit,
    onCrank: (String) -> Unit,
    onReturn: () -> Unit,
    onOpenPile: () -> Unit,
    onLeave: () -> Unit
) {
    AntechamberRoomLayout(
        listColumn = {
            AntechamberLayout(
                callingOut = {
                    // Deck §4.4 (Stage 9, BUILD-SR-17 — copy
                    // governs): the room's standing state line
                    // stands at its head, from the single source.
                    // Seated on BUILD-SR-16's slot by BUILD-SR-23
                    // (composition step 3).
                    Text(text = stringResource(StrongroomCopy.SealStands), style = SigningHeadStyle, color = ink)
                    // B-9.1 — calling out, first and biggest.
                    if (room.callingOut.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        room.callingOut.forEach { name ->
                            Text(
                                text = name,
                                style = SigningHeadStyle,
                                color = ink,
                                modifier = Modifier.testTag("antechamber_calling_out")
                            )
                        }
                    }
                },
                allowedAsSigned = {
                    // The open minute, while one stands open — from either set.
                    if (room.openApp != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = room.openApp.orEmpty(),
                                style = SigningBodyStyle,
                                color = ink,
                                modifier = Modifier.weight(1f)
                            )
                            AntechamberHandle("TURN BACK", ink, "antechamber_turn_back", onReturn)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    // SET 1 (B-9.2, ruling C-3) — the signed allowed
                    // set, plain words exactly as signed, opening
                    // directly. They stand at every purse state;
                    // the purse is never consulted here.
                    room.visibleAllowed.forEach { name ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .testTag("antechamber_allowed_$name"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                style = SigningBodyStyle,
                                color = ink,
                                modifier = Modifier.weight(1f)
                            )
                            AntechamberHandle("OPEN", ink, "antechamber_open_$name") {
                                onOpenSigned(name)
                            }
                        }
                    }
                },
                heldPileSecondStep = {
                    // B-9.3 — the held pile, behind its own second step.
                    if (room.heldCount > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, InkGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onOpenPile
                                )
                                .padding(horizontal = 14.dp, vertical = 14.dp)
                                .testTag("antechamber_pile_entry"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Deck §4.5/§5.1: no caption, no
                            // count — the step itself.
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            )
        },
        drumHousing = {
            Column(
                modifier = Modifier.padding(
                    horizontal = RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp
                )
            ) {
                // SET 2 — THE DRUM, in its housing: its own band
                // of the room, never a row in the list above. Its
                // stock is the mechanism's own form: a strip of
                // minute-marks that depletes (R2-4 — never a number).
                Spacer(Modifier.height(8.dp))
                if (room.purseSpent) {
                    // Deck §6.4: ∅ words at the hard stop.
                    // Nothing stands here.
                } else {
                    DrumStockStrip(
                        minutes = room.purseMinutes,
                        ink = InkGold,
                        modifier = Modifier.testTag("antechamber_drum_stock")
                    )
                    Spacer(Modifier.height(8.dp))
                    room.visibleDrumStock.forEach { name ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .testTag("antechamber_drum_$name"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                style = SigningBodyStyle,
                                color = ink,
                                modifier = Modifier.weight(1f)
                            )
                            AntechamberHandle("CRANK", ink, "antechamber_crank_$name") {
                                onCrank(name)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                // The read-only face: the day as set. No clickable
                // exists in this block — reading is all it is for.
                Column(modifier = Modifier.testTag("antechamber_readonly")) {
                    readOnlyLines.forEach { line ->
                        Text(text = line, style = SigningBodyStyle, color = quietInk)
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "BACK THROUGH THE GRILLE",
                    style = SigningQuietStyle,
                    color = quietInk,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onLeave
                        )
                        .padding(vertical = 8.dp)
                        .testTag("antechamber_leave")
                )
            }
        }
    )
}

/**
 * The purse as mechanism: one mark per minute the drum still
 * holds, struck in rows — the stock depletes visibly as it is
 * spent, and no numeral ever stands for it (R2-4).
 */
@Composable
private fun DrumStockStrip(minutes: Int, ink: Color, modifier: Modifier = Modifier) {
    val perRow = 10
    val rows = (minutes + perRow - 1) / perRow
    Canvas(
        modifier
            .fillMaxWidth()
            .height((rows * 14).dp)
    ) {
        val stepX = size.width / perRow
        val markW = 3.dp.toPx()
        val markH = 9.dp.toPx()
        val rowH = 14.dp.toPx()
        repeat(minutes) { i ->
            val col = i % perRow
            val row = i / perRow
            val x = col * stepX + (stepX - markW) / 2f
            val y = row * rowH + (rowH - markH) / 2f
            drawRect(
                color = ink,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(markW, markH)
            )
        }
    }
}

/**
 * The held pile's chamber, composed through BUILD-SR-10's
 * [HeldPileLayout] at Stage 10 composition (BUILD-SR-16): the
 * shelves in [HeldPileShelf]'s ruled order, in the order a reach
 * meets them — WAITING (what waits: the numbers, shown whole) ·
 * DIGITS_LINE (one code's digits, with the hour, alone — B-18:
 * digits only, no words shelf exists in this chamber) · TAKING
 * (the per-call choice: take it, or leave it waiting at no cost).
 */
@Composable
private fun HeldPileView(
    room: AntechamberState,
    ink: Color,
    quietInk: Color,
    onBack: () -> Unit,
    onTake: (HeldCall) -> Unit,
    onLeaveCall: (HeldCall) -> Unit,
    onShow: (HeldCode) -> Unit
) {
    HeldPileLayout(
        waiting = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Deck §5: the pile carries no heading.
                Spacer(Modifier.weight(1f))
                AntechamberHandle("BACK", ink, "pile_back", onBack)
            }
            Spacer(Modifier.height(16.dp))
            // What waits: held calls, the number whole, on this
            // surface only.
            room.heldCalls.forEach { call ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .testTag("pile_call_${call.id}")
                ) {
                    Text(text = call.number, style = SigningFiguresStyle, color = ink)
                    Text(text = call.hour, style = SigningQuietStyle, color = quietInk)
                }
            }
        },
        digitsLine = {
            // Held codes: the digits fork (B-18). Unshown, a code
            // is an hour, its price stated AT the taking (R2-4),
            // and a handle. Shown, its line is digits alone with
            // the hour — no name, no words around, no way onward.
            room.heldCodes.forEach { code ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .testTag("pile_code_${code.id}")
                ) {
                    if (room.openShowingId == code.id) {
                        Text(
                            text = code.digits,
                            style = SigningDigitsStyle,
                            color = ink,
                            modifier = Modifier.testTag("pile_digits_${code.id}")
                        )
                        Text(text = code.hour, style = SigningFiguresStyle, color = quietInk)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = code.hour, style = SigningFiguresStyle, color = ink)
                                Text(
                                    text = stringResource(StrongroomCopy.OneMinute),
                                    style = SigningQuietStyle,
                                    color = quietInk,
                                    modifier = Modifier.testTag("pile_price_${code.id}")
                                )
                            }
                            AntechamberHandle("SHOW", ink, "pile_show_${code.id}") {
                                onShow(code)
                            }
                        }
                    }
                }
            }
        },
        taking = {
            // The taking: per call, TAKE IT / LEAVE IT — two
            // handles, neither the lesser (TI-35); leaving costs
            // nothing and the call keeps waiting.
            room.heldCalls.forEach { call ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = call.number,
                        style = SigningFiguresStyle,
                        color = quietInk,
                        modifier = Modifier.weight(1f)
                    )
                    AntechamberHandle(stringResource(StrongroomCopy.TakeIt), ink, "pile_take_${call.id}") { onTake(call) }
                    AntechamberHandle(stringResource(StrongroomCopy.LeaveIt), ink, "pile_leave_${call.id}") {
                        onLeaveCall(call)
                    }
                }
            }
            // Deck §5.7: on a spent day the held line waits as
            // testimony — ∅ words.
        }
    )
}

@Composable
private fun AntechamberHandle(
    label: String,
    ink: Color,
    tag: String,
    onClick: () -> Unit
) {
    Text(
        text = label,
        style = SigningHandleStyle,
        color = ink,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag(tag)
    )
}
