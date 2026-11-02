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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomGuard
import com.example.service.StrongroomEngineBridge
import com.example.service.haptics.HapticEvent
import com.example.ui.foundation.LocalStrongroomLightState
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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
        TokenTray(state, actions)
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
private fun TokenTray(state: SigningState, actions: SigningActions) {
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
                                StrongroomLightState.SIGNING
                            ) else StrongroomInks.Graphite,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
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

// ── The Signing Dial — fresh in-app (brief C-05; ledger E-11) ───

@Composable
private fun SigningDialSurface(
    state: SigningState,
    actions: SigningActions,
    lightState: StrongroomLightState
) {
    val record = state.day.record
    val context = LocalContext.current
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
                    var lastDetent: Int? = null
                    detectDragGestures(
                        onDragStart = { lastAngle = null; lastDetent = null },
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
                            // The detent stones speak in feel as
                            // the hand crosses them (FEEL-SR-3 C2.1,
                            // wired by the SOUND seat): a quarter
                            // stone, or the whole-hour stone one
                            // size up — felt only, by filed law.
                            val detent = SigningDial.minuteFor(dragAngle) / 15
                            if (lastDetent != null && detent != lastDetent) {
                                StrongroomEngineBridge.fireSurfaceEvent(
                                    context,
                                    if ((detent * 15) % 60 == 0) {
                                        HapticEvent.HOUR_DETENT_WHOLE
                                    } else {
                                        HapticEvent.HOUR_DETENT_QUARTER
                                    }
                                )
                            }
                            lastDetent = detent
                            change.consume()
                        }
                    )
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val outer = size.minDimension / 2f
                // The steel case and the enamelled ring.
                drawCircle(color = StrongroomRoles.Metal, radius = outer)
                drawCircle(
                    color = StrongroomLight.stateSurface(
                        StrongroomInks.Violet, lightState,
                        com.example.ui.foundation.StrongroomLitSurface.FACE
                    ),
                    radius = outer - 10.dp.toPx()
                )
                // Detent stones: quarters small, whole hours firm —
                // the hour train's topography, drawn (amplitudes
                // 55 / 85 in the foundation's figures).
                for (step in 0 until 48) {
                    val minute = step * 15
                    val whole = minute % 60 == 0
                    val a = Math.toRadians(
                        SigningDial.angleFor(minute) - 90.0
                    ).toFloat()
                    val r1 = outer - if (whole) 26.dp.toPx() else 19.dp.toPx()
                    val r2 = outer - 12.dp.toPx()
                    drawLine(
                        color = if (whole) StrongroomInks.Gold
                        else StrongroomLight.descend(StrongroomInks.Gold, 3),
                        start = Offset(cx + r1 * cos(a), cy + r1 * sin(a)),
                        end = Offset(cx + r2 * cos(a), cy + r2 * sin(a)),
                        strokeWidth = if (whole) 3.dp.toPx() else 1.5.dp.toPx()
                    )
                }
                // The hand, at the placed hour.
                val ha = Math.toRadians(
                    SigningDial.angleFor(previewMinute) - 90.0
                ).toFloat()
                drawLine(
                    color = StrongroomInks.Gold,
                    start = Offset(cx, cy),
                    end = Offset(
                        cx + (outer - 34.dp.toPx()) * cos(ha),
                        cy + (outer - 34.dp.toPx()) * sin(ha)
                    ),
                    strokeWidth = 4.dp.toPx()
                )
                drawCircle(color = StrongroomInks.Gold, radius = 7.dp.toPx())
                // THE VERNIER APERTURE at the index (top): a window
                // in the ring where the scale passes enlarged under
                // a hairline — the thumb works the ring's edge, the
                // reading is taken here, where the thumb is not.
                val winW = 92.dp.toPx()
                val winH = 30.dp.toPx()
                val winX = cx - winW / 2f
                val winY = cy - outer + 2.dp.toPx()
                drawRect(
                    color = StrongroomInks.Graphite,
                    topLeft = Offset(winX, winY),
                    size = Size(winW, winH)
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
    val context = LocalContext.current
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
                        var lastPreview: Int? = null
                        detectDragGestures(
                            onDragStart = { lastPreview = preview },
                            onDragEnd = {
                                // The release speaks its landing
                                // (FEEL-SR-3 C5.2, wired by the SOUND
                                // seat): the zero floor flat, or the
                                // resting pulse on a preset.
                                when {
                                    preview == 0 ->
                                        StrongroomEngineBridge.fireSurfaceEvent(
                                            context, HapticEvent.BREAK_ZERO_STOP
                                        )
                                    BreakChoice.isPreset(preview) ->
                                        StrongroomEngineBridge.fireSurfaceEvent(
                                            context, HapticEvent.BREAK_PRESET_REST
                                        )
                                }
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
                                // The rail's grain under the hand
                                // (FEEL-SR-3 C5.1/C5.2): a brush
                                // per minute, a firmer grain at the
                                // five-mark, the brass notch as a
                                // preset is crossed — felt only.
                                val prev = lastPreview
                                if (prev != null && preview != prev) {
                                    StrongroomEngineBridge.fireSurfaceEvent(
                                        context,
                                        when {
                                            BreakChoice.isPreset(preview) ->
                                                HapticEvent.BREAK_PRESET_NOTCH
                                            preview % 5 == 0 ->
                                                HapticEvent.BREAK_GRAIN_FIVE
                                            else -> HapticEvent.BREAK_GRAIN_MINUTE
                                        }
                                    )
                                }
                                lastPreview = preview
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
                drawCircle(
                    color = StrongroomInks.Gold,
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
                        .background(StrongroomInks.Gold, CircleShape)
                        .clickable {
                            preview = BreakChoice.shortcut(preset)
                            // A tap lands ON the notch (C5.2).
                            StrongroomEngineBridge.fireSurfaceEvent(
                                context, HapticEvent.BREAK_PRESET_NOTCH
                            )
                            actions.onAmend(
                                StrongroomComposition.SigningAmendment(
                                    breakLengthMinutes = preview
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
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
    val context = LocalContext.current
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
                            // The CLICK-LOCK on finger-lift
                            // (FEEL-SR-3 C3.2, wired by the SOUND
                            // seat): a tightening that stands gets
                            // its one hard snap. A wound raise gets
                            // none — it never seats today; its rock
                            // is a dawn event, not a signing one.
                            if (preview < startAllowance) {
                                StrongroomEngineBridge.fireSurfaceEvent(
                                    context, HapticEvent.GUARD_TIGHTEN_SEAT
                                )
                            }
                            actions.onAllowanceMove(preview)
                        },
                        onDrag = { change, dragAmount ->
                            travelled += dragAmount.y
                            // One pawl tooth per 5 minutes per 14 dp
                            // of travel: downward travel tightens
                            // (clicks off instantly), upward winds a
                            // raise against the pawl's stop.
                            val steps = (travelled / 14.dp.toPx()).toInt()
                            val before = preview
                            preview = (startAllowance - steps * 5)
                                .coerceIn(0, 240)
                            // Each tooth crossed speaks (C3.1/C4.1):
                            // tightening, the pawl's double click;
                            // winding a raise, the hollow detent —
                            // felt only, late by design.
                            if (preview < before) {
                                StrongroomEngineBridge.fireSurfaceEvent(
                                    context, HapticEvent.GUARD_TIGHTEN_DETENT
                                )
                            } else if (preview > before) {
                                StrongroomEngineBridge.fireSurfaceEvent(
                                    context, HapticEvent.GUARD_RAISE_DETENT
                                )
                            }
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
            // The pawl itself, seated at the preview's height.
            val frac = 1f - (preview.coerceIn(0, 240) / 240f)
            val pawlY = frac * size.height
            drawCircle(
                color = StrongroomInks.Gold,
                radius = 10.dp.toPx(),
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
            .background(paper, RoundedCornerShape(3.dp))
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
private fun GreatWheelSignature(
    state: SigningState,
    actions: SigningActions,
    lightState: StrongroomLightState
) {
    val spin = remember { CombinationSpin() }
    val context = LocalContext.current
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
                                // The seat speaks (SOUND seat wiring,
                                // 2026-11-03): the final seat is the
                                // combination settling whole; a lesser
                                // seat latches, and the wheel's turnover
                                // into the next pass passes the GATE —
                                // the clack after the felt void.
                                if (spin.isComplete) {
                                    StrongroomEngineBridge.fireSurfaceEvent(
                                        context, HapticEvent.COMBINATION_FINAL_SEAT
                                    )
                                } else {
                                    StrongroomEngineBridge.fireSurfaceEvent(
                                        context, HapticEvent.SIGNATURE_LATCH
                                    )
                                    StrongroomEngineBridge.fireSurfaceEvent(
                                        context, HapticEvent.COMBINATION_GATE
                                    )
                                }
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
                                val stationBefore = spin.stationAt(rotation)
                                rotation += velocity * dt
                                // A station crossed under the hand
                                // speaks its tick (the spin's ratchet
                                // work, wired by the SOUND seat).
                                if (spin.stationAt(rotation) != stationBefore) {
                                    StrongroomEngineBridge.fireSurfaceEvent(
                                        context, HapticEvent.COMBINATION_STATION
                                    )
                                }
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
                val outer = size.minDimension / 2f
                drawCircle(
                    color = StrongroomRoles.Metal,
                    radius = outer - 4.dp.toPx(),
                    style = Stroke(width = 13.dp.toPx())
                )
                drawCircle(
                    color = StrongroomLight.relief(
                        StrongroomInks.Graphite, StrongroomInks.Violet, 0.7f, lightState
                    ),
                    radius = outer - 4.dp.toPx(),
                    style = Stroke(width = 3.dp.toPx())
                )
                for (spoke in 0 until 3) {
                    val a = Math.toRadians(spoke * 120.0 - 90.0).toFloat()
                    drawLine(
                        color = StrongroomRoles.Metal,
                        start = Offset(cx, cy),
                        end = Offset(
                            cx + (outer - 12.dp.toPx()) * cos(a),
                            cy + (outer - 12.dp.toPx()) * sin(a)
                        ),
                        strokeWidth = 10.dp.toPx()
                    )
                }
                drawCircle(color = StrongroomInks.Gold, radius = 15.dp.toPx())
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
