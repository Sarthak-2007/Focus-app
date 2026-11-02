package com.example.ui.strongroom

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.service.TactileSoundEngine
import com.example.ui.strongroom.layout.DoorwayLayout
import com.example.ui.strongroom.layout.OpenDoorProportion
import com.example.ui.strongroom.layout.RoomGrid
import com.example.ui.strongroom.layout.SigningPlaceLayout
import com.example.ui.strongroom.layout.SigningSlot
import com.example.ui.maximum.MaximumPresets
import com.example.ui.motion.SpringState
import kotlin.math.roundToInt
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

    // STAGE 8 COMPOSITION (BUILD-SR-16): the signing place stands
    // in BUILD-SR-7's layout primitives — the law as structure.
    // The arrival at the open door is the DOORWAY (B-17, count of
    // four): the composition as received · one plain line · the
    // held waiting · the striking. The amending steps that follow
    // stand in the SIGNING PLACE's eight slots (C-4: the slot
    // order is the sequence's skeleton — composition amended in
    // its slot, the clause only in its slot, the break only in
    // its slot, the spin only in its slot, the door's telling
    // last). Tray/signing separation is enforced by absence: no
    // slot in either primitive can carry the tray.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(roomShade, roomDeep)))
            .testTag("signing_surface")
    ) {
        // Deck §1.1/§1.2 (Stage 9, BUILD-SR-17 — copy governs):
        // no headline of the app's own stands over the
        // composition — the head is the day itself, its shape in
        // the deck's strings, drawn from the single source. An
        // unmapped shape name is carried as-is (carried content,
        // never authored here). Seated on BUILD-SR-16's frame by
        // BUILD-SR-23 (composition step 3).
        Text(
            text = when (draft.dayShapeName.lowercase()) {
                "ordinary" -> stringResource(StrongroomCopy.DayShapeOrdinary)
                "test" -> stringResource(StrongroomCopy.DayShapeTest)
                "small" -> stringResource(StrongroomCopy.DayShapeSmall)
                "rest" -> stringResource(StrongroomCopy.DayShapeRest)
                else -> draft.dayShapeName
            },
            style = SigningHeadStyle,
            color = ink,
            modifier = Modifier.padding(
                start = RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp,
                top = RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp
            )
        )

        when (flow.step) {
            SigningStep.COMPOSITION -> DoorwayLayout(
                modifier = Modifier.weight(1f),
                compositionAtOpenDoor = {
                    CompositionStep(
                        flow = flow,
                        ink = ink,
                        quietInk = quietInk,
                        onToggle = { flow = flow.toggleApp(it) }
                    )
                },
                onePlainLine = {
                    // The doorway's one plain line: tomorrow as
                    // resolved — information only, exactly one line.
                    Text(
                        text = "Tomorrow, as composed — ${draft.dayShapeName}",
                        style = SigningQuietStyle,
                        color = quietInk,
                        maxLines = OpenDoorProportion.PLAIN_LINE_LINES
                    )
                },
                heldWaiting = {
                    // The held waiting, told at the door: on a
                    // spent day the held codes wait tonight (B-4).
                    // The slot's conditionality is the law's own —
                    // SigningSlot.stands gates it, and an ordinary
                    // day contributes nothing here.
                    if (SigningSlot.stands(SigningSlot.SPENT_DAY_TELLING, draft.spentDay)) {
                        Text(
                            text = stringResource(StrongroomCopy.CodesWaitTonight),
                            style = SigningBodyStyle,
                            color = ink,
                            modifier = Modifier.testTag("signing_codes_wait")
                        )
                    }
                },
                striking = {
                    // The striking set in motion: the act that
                    // sends the composed day toward its seal.
                    SigningContinue(
                        label = "SET THE HOUR",
                        ink = ink,
                        onClick = { flow = flow.advance() }
                    )
                    Spacer(Modifier.height(12.dp))
                    DoorTellingLine(quietInk)
                }
            )
            else -> SigningPlaceLayout(
                modifier = Modifier.weight(1f),
                headTomorrowAsResolved = {
                    Text(
                        text = "Tomorrow, as composed — ${draft.dayShapeName}",
                        style = SigningQuietStyle,
                        color = quietInk,
                        maxLines = OpenDoorProportion.PLAIN_LINE_LINES
                    )
                },
                compositionAsReceived = {
                    when (flow.step) {
                        SigningStep.HOUR -> HourChoice(
                            flow = flow,
                            ink = ink,
                            quietInk = quietInk,
                            onHourDetent = onDetent,
                            onHour = { flow = flow.setHour(it) },
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
                        else -> Unit
                    }
                },
                // The spent-day telling stands at the doorway, at
                // the composition step, before anything seals
                // (B-4) — by the time the signing walks its slots
                // it has been said; the slot is null here, and
                // null contributes nothing (the law's own form).
                spentDayTelling = null,
                clauseAsTerm = {
                    if (flow.step == SigningStep.COMBINATION) {
                        CombinationClause(ink = ink, quietInk = quietInk)
                    }
                },
                breakChoice = {
                    if (flow.step == SigningStep.HOUR) {
                        BreakChoice(
                            flow = flow,
                            ink = ink,
                            quietInk = quietInk,
                            onBreak = { flow = flow.setBreakMinutes(it) },
                            onHourDetent = onDetent
                        )
                    }
                },
                footRule = {
                    if (flow.step == SigningStep.READY) {
                        ReadyStep(
                            flow = flow,
                            ink = ink,
                            quietInk = quietInk,
                            onDone = { flow.result?.let(onSigned) }
                        )
                    }
                },
                combinationSpin = {
                    if (flow.step == SigningStep.COMBINATION) {
                        CombinationSpin(
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
                                // Wiped: the turn is forgotten
                                // whole — the wheel stands at zero
                                // again, as if untouched (A-16).
                                wheel.reset()
                                wheelVersion++
                            }
                        )
                    }
                },
                doorTelling = { DoorTellingLine(quietInk) }
            )
        }

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
                    .padding(
                        start = RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp,
                        bottom = RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp,
                        top = 8.dp,
                        end = 8.dp
                    )
                    .testTag("signing_leave")
            )
        }
    }
}

/** B-19: the telling, standing at the foot of the signing —
 *  told once, quiet, never announced. Its lawful home is the
 *  signing place (SigningOnlyLaw): it travels nowhere else. */
@Composable
private fun DoorTellingLine(quietInk: androidx.compose.ui.graphics.Color) {
    Text(
        text = stringResource(StrongroomCopy.Telling),
        style = SigningQuietStyle,
        color = quietInk.copy(alpha = 0.8f),
        modifier = Modifier.testTag("signing_telling")
    )
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
    onToggle: (String) -> Unit
) {
    // B-1 / TI-01: the purpose sentence, at the head of the
    // composition as received (the composition slot's internal
    // order, B-3).
    Text(
        text = stringResource(StrongroomCopy.Purpose),
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
    // B-4's spent-day telling stands in the doorway's
    // held-waiting slot (see SigningScreen) — said by this
    // surface itself, before the seal, gated by the slot law.
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
}

/** The lifting-hour choice — an amendment of the composition in
 *  its slot (the composition slot's internal order carries the
 *  lifting-hour line, B-3). The break choice stands in its own
 *  slot below ([BreakChoice], SigningSlot.BREAK_CHOICE). */
@Composable
private fun HourChoice(
    flow: SigningState,
    ink: androidx.compose.ui.graphics.Color,
    quietInk: androidx.compose.ui.graphics.Color,
    onHourDetent: () -> Unit,
    onHour: (String) -> Unit,
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
    // E-15 AT MAXIMUM (Stage 11 assembly, BUILD-SR-18 — seated
    // in BUILD-SR-16's hour slot by BUILD-SR-23, step 5): the
    // hour setter is a drag, not steppers — sparse firm stones
    // (quarter W[0,8/0,55], whole-hour W[0,14/0,85], silence
    // between) on MaximumPresets.hourTrain, carried by hourDrag's
    // inertia; release within ±4 min of a quarter pulls in and
    // the detent fires at the snap; release elsewhere places the
    // hour with no waveform at all. Travel is unbounded —
    // wrapping past midnight is the same smooth travel. Figures
    // UNPROVEN-ON-DEVICE.
    SetterDragStrip(
        value = (hour * 60 + minute).toFloat(),
        minValue = 0f,
        maxValue = 24f * 60f,
        minutesPerDp = 2f,
        wrap = true,
        ink = ink,
        quietInk = quietInk,
        tag = "signing_hour_drag",
        train = MaximumPresets.hourTrain(),
        onCrossing = { onHourDetent() },
        onRelease = { released ->
            val snapped = MaximumPresets.hourDrag().release(
                SpringState(released, 0f)
            )
            val quarter = snapped.target
            val placed = if (kotlin.math.abs(released - quarter) <= MaximumPresets.HOUR_SNAP_WINDOW_MINUTES) {
                onHourDetent()
                quarter
            } else released
            val total = placed.roundToInt().mod(24 * 60)
            emit(total / 60, total % 60)
        }
    )
    Spacer(Modifier.height(20.dp))
    SigningContinue(label = "SET THE SENTENCE", ink = ink, onClick = onContinue)
}

/** The break choice, in its own slot (SigningSlot.BREAK_CHOICE,
 *  D-6). The break's span, chosen HERE (ruling C-4: the choosing
 *  moment is the signing; the firing itself is invocable by no
 *  one inside the day). Free to the minute — the preset row is
 *  one-tap shortcuts, never the ceiling (S-6/D-6). */
@Composable
private fun BreakChoice(
    flow: SigningState,
    ink: androidx.compose.ui.graphics.Color,
    quietInk: androidx.compose.ui.graphics.Color,
    onBreak: (Int) -> Unit,
    onHourDetent: () -> Unit
) {
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
    // ADD-2 AT MAXIMUM (Stage 11 assembly, BUILD-SR-18): the break
    // setter's grain is the deliberate opposite of the hour's —
    // a continuous brush (micro-tick per minute, strengthening per
    // five) on MaximumPresets.breakTrain with FREE resistance:
    // preset notches are topography, not walls (they cost no
    // travel). Release is silent; the drag rests on a whole minute
    // via breakDrag's honest settle. Figures UNPROVEN-ON-DEVICE.
    SetterDragStrip(
        value = flow.breakMinutes.toFloat(),
        minValue = 1f,
        maxValue = 720f,
        minutesPerDp = 1f,
        wrap = false,
        ink = ink,
        quietInk = quietInk,
        tag = "signing_break_drag",
        train = MaximumPresets.breakTrain(rangeMinutes = 720, presetMinutes = listOf(10, 20, 30)),
        onCrossing = { onHourDetent() },
        onRelease = { released ->
            val rested = MaximumPresets.breakDrag().release(SpringState(released, 0f))
            onBreak(rested.finalValue.roundToInt().coerceIn(1, 720))
        }
    )
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
    SigningTextField(
        value = flow.sentence.orEmpty(),
        placeholder = "",
        ink = ink,
        quietInk = quietInk,
        tag = "signing_sentence_field",
        onValue = { onSentence(it) }
    )
    Spacer(Modifier.height(18.dp))
    SigningTextField(
        value = flow.taskLine.orEmpty(),
        placeholder = "",
        ink = ink,
        quietInk = quietInk,
        tag = "signing_task_field",
        onValue = { onTaskLine(it) }
    )
    Spacer(Modifier.height(20.dp))
    SigningContinue(label = "TO THE SIGNATURE", ink = ink, onClick = onContinue)
}

/** The clause as a term, in its slot (SigningSlot.CLAUSE_AS_TERM,
 *  B-5): spoken whole at the signing — here, and nowhere else in
 *  the app (SigningOnlyLaw: it travels nowhere). Stood under,
 *  never gated. */
@Composable
private fun CombinationClause(
    ink: androidx.compose.ui.graphics.Color,
    quietInk: androidx.compose.ui.graphics.Color
) {
    Text(
        text = stringResource(StrongroomCopy.Clause),
        style = SigningBodyStyle,
        color = ink,
        modifier = Modifier.testTag("signing_clause")
    )
}

/** The combination spin, in its slot (SigningSlot.COMBINATION_SPIN,
 *  A-2): gesture-in, signed-fact-out — spun, never typed; there is
 *  no text-field slot in this zone by construction. */
@Composable
private fun CombinationSpin(
    flow: SigningState,
    wheel: WheelTurn,
    wheelVersion: Int,
    dials: List<Int>,
    ink: androidx.compose.ui.graphics.Color,
    quietInk: androidx.compose.ui.graphics.Color,
    onWheelTurn: (List<Long>) -> Unit,
    onSign: () -> Unit
) {
    Spacer(Modifier.height(4.dp))
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
    // Deck §1 (signing absences, BUILD-SR-17 — copy governs):
    // no confirmation wording after the spin — the sealing
    // itself is the confirmation. The READY slot (BUILD-SR-16's
    // footRule) stands; its sheet is struck to the one functional
    // handle that hands the result onward. Composed by
    // BUILD-SR-23 (step 3).
    Spacer(Modifier.height(20.dp))
    SigningContinue(label = "DONE", ink = ink, onClick = onDone, tag = "signing_done")
}


/**
 * A maximum drag strip for the signing's setters (Stage 11
 * assembly, BUILD-SR-18): horizontal drag accumulated in the
 * setter's own units; every detent of [train] crossed fires
 * [onCrossing] (position-keyed, never time-keyed); the release is
 * handed to the caller, whose preset drag (hourDrag / breakDrag)
 * decides the rest. The strip itself invents no physics.
 */
@Composable
private fun SetterDragStrip(
    value: Float,
    minValue: Float,
    maxValue: Float,
    minutesPerDp: Float,
    wrap: Boolean,
    ink: Color,
    quietInk: Color,
    tag: String,
    train: com.example.ui.maximum.DetentTrain,
    onCrossing: () -> Unit,
    onRelease: (Float) -> Unit
) {
    var current by remember { mutableFloatStateOf(value) }
    LaunchedEffect(value) { current = value }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .border(1.dp, quietInk.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .testTag(tag)
            .pointerInput(train, minValue, maxValue, wrap) {
                detectDragGestures(
                    onDragEnd = { onRelease(current) },
                    onDragCancel = { onRelease(current) }
                ) { change, _ ->
                    val delta = (change.position.x - change.previousPosition.x) / density * minutesPerDp
                    val before = current
                    var next = before + delta
                    if (!wrap) next = next.coerceIn(minValue, maxValue)
                    current = next
                    train.crossings(before, next).forEach { _ -> onCrossing() }
                    change.consume()
                }
            }
    ) {
        val fraction = ((current - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
        Box(
            Modifier
                .fillMaxWidth(fraction.coerceAtLeast(0.02f))
                .height(54.dp)
                .background(ink.copy(alpha = 0.25f))
        )
        Text(
            text = "DRAG",
            style = SigningQuietStyle,
            color = quietInk,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 10.dp)
        )
    }
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
