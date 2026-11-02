package com.example.ui.strongroom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.service.TactileSoundEngine
import com.example.ui.theme.InkGold
import com.example.ui.theme.InkLine
import com.example.ui.theme.SigningBodyStyle
import com.example.ui.theme.SigningDigitsStyle
import com.example.ui.theme.SigningFiguresStyle
import com.example.ui.theme.SigningHandleStyle
import com.example.ui.theme.SigningHeadStyle
import com.example.ui.theme.SigningQuietStyle
import com.example.ui.theme.StrongroomGround
import com.example.ui.theme.reliefHighlight
import com.example.ui.theme.reliefShade
import com.example.ui.theme.RelightState
import java.util.Locale

/**
 * P4 — THE SIGNING SURFACE (Slice 1, corrected staging).
 *
 * The evening's act at the open door, under a minute ordinary:
 * the composed day stands before him (ledger B-2 — the signing
 * amends, never creates), and he walks the sequence of
 * [SigningModel] — composition → hour → sentence → combination
 * → ready — with zero lawful insertions (C-4). The surface ends
 * with everything SET and the door standing ready.
 *
 * THERE IS NO SWING ON THIS SURFACE. The door swing, the wheel,
 * and the three descending clunks are the seal's act at the
 * first sitting (P1/P2), and no control here performs or
 * previews them; the seal's voices are likewise never spent
 * here (the engine's reservation law) — the signing's own feel
 * is the dial detent and the signature's firm latch, wired by
 * the caller through [onDetent]/[onLatch].
 *
 * Standing words, as filed: the purpose sentence at the head of
 * the composition (B-1/TI-01) · on a spent day, "codes wait
 * tonight" said by this surface itself before anything seals
 * (B-4) · the clause spoken whole at the signature step, only
 * there (B-5) · the telling line standing quiet at the foot of
 * every step (B-19).
 *
 * The combination's digits live in this composable's hand only,
 * for the act's duration, and are wiped the moment the
 * signature completes — they are never written into the flow,
 * the result, or any store (A-2).
 */
@Composable
fun SigningScreen(
    draft: CompositionDraft,
    onDetent: () -> Unit,
    onLatch: () -> Unit,
    onLeave: () -> Unit,
    onSigned: (SigningResult) -> Unit,
    modifier: Modifier = Modifier
) {
    var flow by remember(draft) { mutableStateOf(SigningState(draft)) }
    // The combination — surface-local, unwritten, wiped at the
    // signature's completion. Never state that leaves this hand.
    // A-16: it is worked THROUGH THE GREAT WHEEL — the wheel's
    // turn (GreatWheel.kt) is the combination's only mechanism;
    // the dials below are its tumbler, read by eye from the turn.
    val wheel = remember(draft) { WheelTurn() }
    var wheelVersion by remember(draft) { mutableStateOf(0) }
    val dials = wheel.dials()

    val ink = InkGold.reliefHighlight(RelightState.SIGNING)
    val quietInk = InkGold.copy(alpha = 0.75f)
    val roomDeep = InkLine
    val roomShade = StrongroomGround.reliefShade(RelightState.SIGNING)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(roomShade, roomDeep)))
            .padding(horizontal = 24.dp, vertical = 28.dp)
            .testTag("signing_surface")
    ) {
        Text(
            text = "THE SIGNING",
            style = SigningHeadStyle,
            color = ink
        )
        Text(
            text = "Tomorrow, as composed — ${draft.dayShapeName}",
            style = SigningQuietStyle,
            color = quietInk
        )
        Spacer(Modifier.height(18.dp))

        when (flow.step) {
            SigningStep.COMPOSITION -> CompositionStep(
                flow = flow,
                ink = ink,
                quietInk = quietInk,
                onToggle = { flow = flow.toggleApp(it) },
                onContinue = { flow = flow.advance() }
            )
            SigningStep.HOUR -> HourStep(
                flow = flow,
                ink = ink,
                quietInk = quietInk,
                onHour = { flow = flow.setHour(it) },
                onBreak = { flow = flow.setBreakMinutes(it) },
                onContinue = { flow = flow.advance() }
            )
            SigningStep.SENTENCE -> SentenceStep(
                flow = flow,
                ink = ink,
                quietInk = quietInk,
                onSentence = { flow = flow.setSentence(it) },
                onTaskLine = { flow = flow.setTaskLine(it) },
                onContinue = { flow = flow.advance() }
            )
            SigningStep.COMBINATION -> CombinationStep(
                flow = flow,
                wheel = wheel,
                wheelVersion = wheelVersion,
                dials = dials,
                ink = ink,
                quietInk = quietInk,
                onWheelTurn = { crossed ->
                    if (!flow.signatureBegun) flow = flow.beginSignature()
                    crossed.forEach { _ -> onDetent() }
                    wheelVersion++
                },
                onSign = {
                    onLatch()
                    flow = flow.completeSignature()
                    // Wiped: the turn is forgotten whole — the
                    // wheel stands at zero again, as if untouched.
                    wheel.reset()
                    wheelVersion++
                }
            )
            SigningStep.READY -> ReadyStep(
                flow = flow,
                ink = ink,
                quietInk = quietInk,
                onDone = { flow.result?.let(onSigned) }
            )
        }

        Spacer(Modifier.weight(1f))

        if (flow.canLeave) {
            Text(
                text = "LEAVE THE SIGNING",
                style = SigningQuietStyle,
                color = quietInk,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onLeave
                    )
                    .padding(vertical = 8.dp)
                    .testTag("signing_leave")
            )
        }
        // B-19: the telling, standing at every signing — told
        // once, quiet, never announced.
        Text(
            text = SigningWords.TELLING_LINE,
            style = SigningQuietStyle,
            color = quietInk.copy(alpha = 0.8f),
            modifier = Modifier.testTag("signing_telling")
        )
    }
}

/**
 * The route form: wires the signing's own feel from a
 * surface-owned engine (detent per dial step, the firm latch at
 * the signature) and tears it down with the surface. The seal's
 * voices are never spent here — the engine's reservation law is
 * honoured by simply never calling them.
 */
@Composable
fun SigningRoute(
    draft: CompositionDraft,
    onLeave: () -> Unit,
    onSigned: (SigningResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val engine = remember(context) { TactileSoundEngine(context) }
    DisposableEffect(engine) { onDispose { engine.stopAllAudio() } }
    SigningScreen(
        draft = draft,
        onDetent = { engine.playRotaryDetent() },
        onLatch = { engine.playFirmClick() },
        onLeave = onLeave,
        onSigned = onSigned,
        modifier = modifier
    )
}

@Composable
private fun CompositionStep(
    flow: SigningState,
    ink: androidx.compose.ui.graphics.Color,
    quietInk: androidx.compose.ui.graphics.Color,
    onToggle: (String) -> Unit,
    onContinue: () -> Unit
) {
    // B-1 / TI-01: the purpose sentence, at the signing.
    Text(
        text = SigningWords.PURPOSE_LINE,
        style = SigningBodyStyle,
        color = ink,
        modifier = Modifier.testTag("signing_purpose")
    )
    if (flow.draft.teachingLine != null) {
        Spacer(Modifier.height(12.dp))
        // R2-8: the swing taught at the FIRST signing only —
        // standing furniture on the composition, not a step.
        Text(
            text = flow.draft.teachingLine.orEmpty(),
            style = SigningBodyStyle,
            color = ink,
            modifier = Modifier.testTag("signing_teaching")
        )
    }
    if (flow.draft.spentDay) {
        Spacer(Modifier.height(12.dp))
        // B-4: the spent-day telling, in plain words, verbatim —
        // this surface says it itself, before the seal.
        Text(
            text = SigningWords.CODES_WAIT_LINE,
            style = SigningBodyStyle,
            color = ink,
            modifier = Modifier.testTag("signing_codes_wait")
        )
    }
    Spacer(Modifier.height(18.dp))
    flow.draft.appOptions.forEach { option ->
        val locked = option.packageName in flow.lockedPackages
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onToggle(option.packageName) }
                )
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .testTag("signing_app_${option.packageName}"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = option.appName,
                style = SigningBodyStyle,
                color = ink,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (locked) "LOCKED" else "LEFT OPEN",
                style = SigningQuietStyle,
                color = quietInk
            )
        }
    }
    Spacer(Modifier.height(20.dp))
    SigningContinue(label = "SET THE HOUR", ink = ink, onClick = onContinue)
}

@Composable
private fun HourStep(
    flow: SigningState,
    ink: androidx.compose.ui.graphics.Color,
    quietInk: androidx.compose.ui.graphics.Color,
    onHour: (String) -> Unit,
    onBreak: (Int) -> Unit,
    onContinue: () -> Unit
) {
    val parts = flow.liftingHour.split(":")
    val hour = parts[0].toInt()
    val minute = parts[1].toInt()
    fun emit(h: Int, m: Int) =
        onHour(String.format(Locale.US, "%02d:%02d", h.mod(24), m.mod(60)))

    Text(
        text = "The lifting hour — free, any hour",
        style = SigningBodyStyle,
        color = ink
    )
    Spacer(Modifier.height(16.dp))
    Text(
        text = flow.liftingHour,
        style = SigningDigitsStyle,
        color = ink,
        modifier = Modifier.testTag("signing_hour_value")
    )
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SigningStepper("HOUR −", quietInk, "signing_hour_down") { emit(hour - 1, minute) }
        SigningStepper("HOUR +", quietInk, "signing_hour_up") { emit(hour + 1, minute) }
        SigningStepper("MIN −", quietInk, "signing_minute_down") { emit(hour, minute - 5) }
        SigningStepper("MIN +", quietInk, "signing_minute_up") { emit(hour, minute + 5) }
    }
    Spacer(Modifier.height(22.dp))
    // The break's span, chosen HERE (ruling C-4: the choosing
    // moment is the signing; the firing itself is invocable by
    // no one inside the day). Free to the minute — the preset
    // row is one-tap shortcuts, never the ceiling (S-6/D-6).
    Text(
        text = "The break — its span is set now, and fires by the lock",
        style = SigningBodyStyle,
        color = ink
    )
    Spacer(Modifier.height(10.dp))
    Text(
        text = "${flow.breakMinutes} MIN",
        style = SigningFiguresStyle,
        color = ink,
        modifier = Modifier.testTag("signing_break_value")
    )
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SigningStepper("−10", quietInk, "signing_break_down10") {
            onBreak((flow.breakMinutes - 10).coerceAtLeast(1))
        }
        SigningStepper("−1", quietInk, "signing_break_down1") {
            onBreak((flow.breakMinutes - 1).coerceAtLeast(1))
        }
        SigningStepper("+1", quietInk, "signing_break_up1") {
            onBreak((flow.breakMinutes + 1).coerceAtMost(720))
        }
        SigningStepper("+10", quietInk, "signing_break_up10") {
            onBreak((flow.breakMinutes + 10).coerceAtMost(720))
        }
    }
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(10, 20, 30).forEach { preset ->
            SigningStepper("$preset", quietInk, "signing_break_preset_$preset") {
                onBreak(preset)
            }
        }
        Text(
            text = "shortcuts, never the ceiling",
            style = SigningQuietStyle,
            color = quietInk,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
    Spacer(Modifier.height(20.dp))
    SigningContinue(label = "SET THE SENTENCE", ink = ink, onClick = onContinue)
}

@Composable
private fun SentenceStep(
    flow: SigningState,
    ink: androidx.compose.ui.graphics.Color,
    quietInk: androidx.compose.ui.graphics.Color,
    onSentence: (String?) -> Unit,
    onTaskLine: (String?) -> Unit,
    onContinue: () -> Unit
) {
    Text(
        text = "His sentence for the face",
        style = SigningBodyStyle,
        color = ink
    )
    Spacer(Modifier.height(10.dp))
    SigningTextField(
        value = flow.sentence.orEmpty(),
        placeholder = "Write it — or leave the kept place standing",
        ink = ink,
        quietInk = quietInk,
        tag = "signing_sentence_field",
        onValue = { onSentence(it) }
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = if (flow.sentenceKeptPlace) "THE KEPT PLACE STANDS" else "THE SENTENCE STANDS",
        style = SigningQuietStyle,
        color = quietInk,
        modifier = Modifier.testTag("signing_sentence_state")
    )
    Spacer(Modifier.height(18.dp))
    Text(
        text = "The block's reason, in his words",
        style = SigningBodyStyle,
        color = ink
    )
    Spacer(Modifier.height(10.dp))
    SigningTextField(
        value = flow.taskLine.orEmpty(),
        placeholder = "The reason for the block — his words, unjudged",
        ink = ink,
        quietInk = quietInk,
        tag = "signing_task_field",
        onValue = { onTaskLine(it) }
    )
    Spacer(Modifier.height(20.dp))
    SigningContinue(label = "TO THE SIGNATURE", ink = ink, onClick = onContinue)
}

@Composable
private fun CombinationStep(
    flow: SigningState,
    wheel: WheelTurn,
    wheelVersion: Int,
    dials: List<Int>,
    ink: androidx.compose.ui.graphics.Color,
    quietInk: androidx.compose.ui.graphics.Color,
    onWheelTurn: (List<Long>) -> Unit,
    onSign: () -> Unit
) {
    // B-5: the clause, spoken whole at the signing — here, and
    // nowhere else in the app.
    Text(
        text = SigningWords.SIGNING_CLAUSE,
        style = SigningBodyStyle,
        color = ink,
        modifier = Modifier.testTag("signing_clause")
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = "Turn the great wheel to your combination — it is written nowhere.",
        style = SigningQuietStyle,
        color = quietInk
    )
    Spacer(Modifier.height(16.dp))
    // THE WHEEL (A-16): the signer turns it; its turning works
    // the combination's tumblers. Drag around its centre — the
    // wheel answers under WheelPhysics (resistance rising,
    // governed inertia, the seat at settle); every detent the
    // travel crosses spends the surface's detent voice, keyed to
    // position, so a slow turn and a fast turn speak the same
    // sequence. wheelVersion is read so the drawing follows the
    // mechanism's state.
    @Suppress("UNUSED_EXPRESSION") wheelVersion
    var lastAngle by remember { mutableStateOf<Float?>(null) }
    var lastDelta by remember { mutableStateOf(0f) }
    var lastMoveMs by remember { mutableStateOf(0L) }
    fun angleOf(pos: androidx.compose.ui.geometry.Offset, size: androidx.compose.ui.unit.IntSize): Float {
        val cx = size.width / 2f
        val cy = size.height / 2f
        return Math.toDegrees(
            kotlin.math.atan2((pos.y - cy).toDouble(), (pos.x - cx).toDouble())
        ).toFloat()
    }
    fun wrappedDelta(from: Float, to: Float): Float {
        var d = to - from
        while (d > 180f) d -= 360f
        while (d < -180f) d += 360f
        return d
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("signing_wheel")
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { pos ->
                        lastAngle = angleOf(pos, size)
                        lastDelta = 0f
                    },
                    onDragEnd = {
                        val now = System.currentTimeMillis()
                        val dt = (now - lastMoveMs).coerceAtLeast(1L)
                        val velocity = lastDelta / dt * 1000f
                        onWheelTurn(wheel.release(velocity))
                        lastAngle = null
                    },
                    onDragCancel = {
                        wheel.interrupt()
                        onWheelTurn(emptyList())
                        lastAngle = null
                    },
                    onDrag = { change, _ ->
                        val a = angleOf(change.position, size)
                        val prev = lastAngle
                        if (prev != null) {
                            val d = wrappedDelta(prev, a)
                            lastDelta = d
                            lastMoveMs = System.currentTimeMillis()
                            onWheelTurn(wheel.dragBy(d))
                        }
                        lastAngle = a
                        change.consume()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        GreatWheelCanvas(
            angleDegrees = wheel.angleDegrees +
                wheel.pendingFraction() * WheelPhysics.DETENT_DEGREES,
            lightState = RelightState.SIGNING,
            modifier = Modifier.size(232.dp)
        )
    }
    Spacer(Modifier.height(14.dp))
    // The tumbler, read by eye: three dials the wheel's turn
    // carries (ones with the wheel, tens and hundreds by carry).
    // They are a READOUT of the turn, never an input of their own.
    Row(
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        dials.forEachIndexed { index, digit ->
            Text(
                text = digit.toString(),
                style = SigningDigitsStyle,
                color = ink,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(64.dp)
                    .padding(vertical = 6.dp)
                    .testTag("signing_dial_$index")
            )
        }
    }
    Spacer(Modifier.height(18.dp))
    SigningContinue(
        label = "SIGN — SET THE DAY",
        ink = ink,
        enabled = flow.signatureBegun,
        onClick = onSign,
        tag = "signing_sign"
    )
    if (!flow.signatureBegun) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = "The signing cannot be left once the spinning begins.",
            style = SigningQuietStyle,
            color = quietInk
        )
    }
}

@Composable
private fun ReadyStep(
    flow: SigningState,
    ink: androidx.compose.ui.graphics.Color,
    quietInk: androidx.compose.ui.graphics.Color,
    onDone: () -> Unit
) {
    val result = flow.result
    Text(
        text = "SET.",
        style = SigningHeadStyle,
        color = ink,
        modifier = Modifier.testTag("signing_ready")
    )
    Spacer(Modifier.height(12.dp))
    if (result != null) {
        Text(
            text = "Lifting hour ${result.liftingHour}",
            style = SigningFiguresStyle,
            color = ink
        )
        Text(
            text = "${result.lockedPackages.size} locked for tomorrow",
            style = SigningBodyStyle,
            color = ink
        )
        Text(
            text = "Break ${result.breakMinutes} minutes — fires by the lock, summoned by no one",
            style = SigningBodyStyle,
            color = ink
        )
        Text(
            text = result.sentence ?: "The kept place stands on the face",
            style = SigningBodyStyle,
            color = quietInk
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = "The door stands ready. It swings at the first sitting.",
            style = SigningBodyStyle,
            color = ink
        )
    }
    Spacer(Modifier.height(20.dp))
    SigningContinue(label = "DONE", ink = ink, onClick = onDone, tag = "signing_done")
}

@Composable
private fun SigningTextField(
    value: String,
    placeholder: String,
    ink: androidx.compose.ui.graphics.Color,
    quietInk: androidx.compose.ui.graphics.Color,
    tag: String,
    onValue: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValue,
        // S-3 (B-3): his words travel byte-for-byte — no
        // autocorrect, no capitalisation, no suggestion pipeline
        // may touch them anywhere between his hand and the record.
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            autoCorrect = false,
            capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.None
        ),
        textStyle = SigningBodyStyle.copy(color = ink),
        cursorBrush = SolidColor(ink),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, quietInk.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .testTag(tag),
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) {
                    Text(text = placeholder, style = SigningBodyStyle, color = quietInk)
                }
                inner()
            }
        }
    )
}

@Composable
private fun SigningStepper(
    label: String,
    ink: androidx.compose.ui.graphics.Color,
    tag: String,
    onClick: () -> Unit
) {
    Text(
        text = label,
        style = SigningHandleStyle,
        color = ink,
        textAlign = TextAlign.Center,
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

@Composable
private fun SigningContinue(
    label: String,
    ink: androidx.compose.ui.graphics.Color,
    enabled: Boolean = true,
    tag: String = "signing_continue",
    onClick: () -> Unit
) {
    Text(
        text = label,
        style = SigningHandleStyle,
        color = if (enabled) ink else ink.copy(alpha = 0.4f),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (enabled) ink.copy(alpha = 0.6f) else ink.copy(alpha = 0.25f),
                RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(tag)
    )
}
