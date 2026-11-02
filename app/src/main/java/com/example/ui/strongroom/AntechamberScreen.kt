package com.example.ui.strongroom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.service.TactileSoundEngine
import com.example.ui.theme.FaceLightState
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
 * P5 — THE ANTECHAMBER (Slice 1).
 *
 * The room behind P3's grille plate — reached by the deliberate
 * second touch, left by the same door. Its arrangement IS the
 * list law (ledger B-9), consumed from [AntechamberModel] and
 * lettered to BUILD-SR-7's layout lane as a coordination, not a
 * fork (mail/build-sr-7, 2026-11-02): calling out first and
 * biggest · the allowed things as plain words, exactly as
 * signed · the held pile behind its own second grille ·
 * spent things gone, never dimmed · nothing else behind the
 * door. Slice 2 (LAYOUT) re-arranges this room in the running
 * app on the layout lane's primitive when it lands; the law
 * above does not move.
 *
 * THE DRUM (A-7) is worked from the allowed rows: CRANK turns
 * one app to him for its minute; TURN BACK returns it (it also
 * returns itself — the caller fires the same act at the
 * minute). A spent purse answers the crank with
 * [CrankOutcome.HARD_STOP_SPENT] and the route puts the hard
 * stop in his hand; the spent apps are simply not here.
 *
 * THE HELD PILE (second step, its own view) carries strangers
 * only — contacts ring through natively and never wait here.
 * Per call: TAKE IT / LEAVE IT, two handles, neither the lesser
 * (TI-35). Per code: the digits fork (B-18) — the shown line is
 * digits alone with the hour, no name, no words, no way onward;
 * every taking is priced one minute by the model.
 *
 * The day-as-set panel at the foot is a READ-ONLY FACE: it is
 * built with no clickable anywhere in it, by construction.
 *
 * Not in this room, by the re-fit's own placement: the week's
 * emergency minute — it lives behind a door of its own, a longer
 * hold on the seal itself (its week-state is P1's, its firing
 * P2's; the seam is named in this lane's return).
 */
@Composable
fun AntechamberScreen(
    initialState: AntechamberState,
    readOnlyLines: List<String>,
    onCrankOutcome: (CrankOutcome) -> Unit,
    onShowingOutcome: (ShowingOutcome) -> Unit,
    onTakeCall: (HeldCall) -> Unit,
    onReturned: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var room by remember(initialState) { mutableStateOf(initialState) }
    var inPile by remember { mutableStateOf(false) }

    val ink = InkGold.reliefHighlight(FaceLightState.SIGNING)
    val quietInk = InkGold.copy(alpha = 0.75f)
    val lampShade = StrongroomLamp.reliefShade(FaceLightState.SIGNING)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(lampShade, InkLine)))
            .padding(horizontal = 24.dp, vertical = 28.dp)
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
 * engine — the crank's turn as a weighted seat knock, the spent
 * purse's hard stop at full mass in the hand, the latch family
 * for takings and returns.
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
                CrankOutcome.NOT_BEHIND_THE_DOOR -> Unit
            }
        },
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

@Composable
private fun AntechamberListView(
    room: AntechamberState,
    readOnlyLines: List<String>,
    ink: Color,
    quietInk: Color,
    onCrank: (String) -> Unit,
    onReturn: () -> Unit,
    onOpenPile: () -> Unit,
    onLeave: () -> Unit
) {
    Text(text = "THE ANTECHAMBER", style = SigningHeadStyle, color = ink)

    // B-9.1 — calling out, first and biggest.
    if (room.callingOut.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))
        Text(text = "CALLING OUT", style = SigningQuietStyle, color = quietInk)
        room.callingOut.forEach { name ->
            Text(
                text = name,
                style = SigningHeadStyle,
                color = ink,
                modifier = Modifier.testTag("antechamber_calling_out")
            )
        }
    }

    // The drum's open minute, while one stands open.
    if (room.openApp != null) {
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "OPEN — ${room.openApp}",
                style = SigningBodyStyle,
                color = ink,
                modifier = Modifier.weight(1f)
            )
            AntechamberHandle("TURN BACK", ink, "antechamber_turn_back", onReturn)
        }
    }

    // B-9.2 + B-9.4 — the allowed things, exactly as signed, or
    // gone. The model decides; this view only ever draws
    // visibleAllowed, so a spent thing cannot be drawn dimmed —
    // there is no drawing of it at all.
    Spacer(Modifier.height(18.dp))
    if (room.purseSpent) {
        Text(
            text = "The purse is spent — the crank will not turn.",
            style = SigningBodyStyle,
            color = ink,
            modifier = Modifier.testTag("antechamber_purse_spent")
        )
    } else {
        Text(
            text = "The drum holds ${room.purseMinutes} minutes",
            style = SigningFiguresStyle,
            color = quietInk
        )
        Spacer(Modifier.height(8.dp))
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
                AntechamberHandle("CRANK", ink, "antechamber_crank_$name") {
                    onCrank(name)
                }
            }
        }
    }

    // B-9.3 — the held pile, behind its own second step.
    if (room.heldCount > 0) {
        Spacer(Modifier.height(18.dp))
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
            Text(
                text = "THE HELD PILE",
                style = SigningHandleStyle,
                color = ink,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = room.heldCount.toString(),
                style = SigningFiguresStyle,
                color = quietInk
            )
        }
    }

    Spacer(Modifier.weight(1f))

    // The read-only face: the day as set. No clickable exists
    // in this block — reading is all it is for.
    Column(modifier = Modifier.testTag("antechamber_readonly")) {
        Text(text = "THE DAY AS SET", style = SigningQuietStyle, color = quietInk)
        Spacer(Modifier.height(6.dp))
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "THE HELD PILE",
            style = SigningHeadStyle,
            color = ink,
            modifier = Modifier.weight(1f)
        )
        AntechamberHandle("BACK", ink, "pile_back", onBack)
    }
    Spacer(Modifier.height(16.dp))

    // Held calls: the number whole, his choice per call —
    // TAKE IT / LEAVE IT, two handles, neither the lesser (TI-35).
    room.heldCalls.forEach { call ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .testTag("pile_call_${call.id}")
        ) {
            Text(text = call.number, style = SigningFiguresStyle, color = ink)
            Text(text = call.hour, style = SigningQuietStyle, color = quietInk)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                AntechamberHandle("TAKE IT", ink, "pile_take_${call.id}") { onTake(call) }
                AntechamberHandle("LEAVE IT", ink, "pile_leave_${call.id}") {
                    onLeaveCall(call)
                }
            }
        }
    }

    // Held codes: the digits fork (B-18). Unshown, a code is an
    // hour and a handle. Shown, its line is digits alone with
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
                    Text(
                        text = code.hour,
                        style = SigningFiguresStyle,
                        color = ink,
                        modifier = Modifier.weight(1f)
                    )
                    AntechamberHandle("SHOW", ink, "pile_show_${code.id}") {
                        onShow(code)
                    }
                }
            }
        }
    }
    if (room.purseSpent && room.heldCodes.isNotEmpty()) {
        Spacer(Modifier.height(10.dp))
        Text(
            text = "The purse is spent — codes wait for the lift.",
            style = SigningBodyStyle,
            color = quietInk,
            modifier = Modifier.testTag("pile_codes_wait")
        )
    }
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
