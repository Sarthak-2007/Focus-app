package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameMillis
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.AppBlockTier
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.data.StudyModePreset
import com.example.service.TactileSoundEngine
import com.example.service.UsageAccess
import com.example.service.Warrant
import com.example.service.WarrantState
import com.example.ui.ArmPhase
import com.example.ui.components.BlockitActionChip
import com.example.ui.components.ImprintSlot
import com.example.ui.components.SealBar
import com.example.ui.components.RefusalAction
import com.example.ui.components.RefusalSentenceSlot
import com.example.ui.components.StripDensity
import com.example.ui.components.RefusalSentence
import com.example.ui.components.endsAtText
import com.example.ui.components.imprintTenantText
import com.example.ui.components.endsAtNumeralsFor
import com.example.ui.components.jamSentenceFor
import com.example.ui.components.refusalSentenceFor
import com.example.ui.components.sealBarStateFor
import com.example.ui.components.inkControlFill
import com.example.ui.components.onInkControlFill
import com.example.ui.components.FullBleedSectionHeader
import com.example.ui.components.rememberMixedTypographyText
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.DialClockTextStyle
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.views.RotaryWheelView
import com.example.ui.views.SlideToStartView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * TAB 0: FOCUS COMMAND CONSOLE (DEVELOPER-GUIDE.pdf §1–§11)
 * - Full-bleed stacked sections. (Spacing is per-section;
 *   the old '12dp universal spacing' promise was false — F21.)
 * - The Instrument: one raised panel holding the readout + RotaryWheelView (Ren).
 * - Pinned `SlideToStartView` commit bar below the scroll.
 * - When armed, transforms into the dark Armed Lockdown state with One-Way Ratchet enforcement.
 */
@Composable
fun TactileDeckScreen(
    activeSession: FocusSessionEntity?,
    dialMinutes: Int,
    studyBlocksCount: Int,
    activeTask: FocusTaskEntity?,
    queuedTasks: List<FocusTaskEntity>,
    capturedNotificationsCount: Int,
    soundEngine: TactileSoundEngine? = null,
    isDarkTheme: Boolean = false,
    onDialMinutesChanged: (Int) -> Unit,
    onSelectStudyPreset: (StudyModePreset) -> Unit,
    onSetStudyBlocksCount: (Int) -> Unit,
    onCompleteActiveTask: (Int) -> Unit,
    onToggleTaskSubStep: (Int, Int) -> Unit,
    onOpenTaskSequencer: () -> Unit,
    onArmSession: () -> Unit,
    onRequestDisarm: () -> Unit,
    onCancelWithinGraceWindow: () -> Unit,
    onOpenAlwaysOnDeskClock: () -> Unit,
    onOpenNotificationDigest: () -> Unit,
    // ST-02 (Truth Car 3): the break's own verb — defaulted so
    // render harnesses that seat the Deck without a live break
    // keep composing; MainActivity wires the VM's verb.
    onEndSafeBreak: () -> Unit = {},
    weeklyEmergencyTokensRemaining: Int = 1,
    // Slice D (the proof strip, the sentence slot, the imprint): the
    // host owns the wire; the screen renders its word.
    appsCellCount: Int? = null,
    appsSplit: com.example.ui.MonolithViewModel.AppsSplit? = null,
    deckWarrant: Warrant? = null,
    sealFreeze: com.example.ui.MonolithViewModel.SealFreeze? = null,
    warrantProvider: () -> Warrant = {
        Warrant(
            state = WarrantState.UNPROVEN,
            a11yBound = false,
            a11yStampedThisResidency = false,
            shieldProtecting = false,
            usage = UsageAccess.UNSET
        )
    },
    onFixBlockingAccess: () -> Unit = {},
    onFixUsageAccess: () -> Unit = {},
    onRetryBlockingCheck: () -> Unit = {},
    onHandBackTick: () -> Unit = {},
    // Slice D step 4 (the cascade): the host owns the attempt's
    // phase; the screen renders its word. Defaults keep every
    // other host (previews, render tests) on the standing form.
    armPhase: ArmPhase = ArmPhase.Idle,
    armSentence: RefusalSentence? = null,
    armResetKey: Int = 0,
    // The struck record (Emil E-12): VM scope, beside the freeze.
    // The imprint fires its Strike only for a key this record does
    // not already carry, and reports the strike back — a session
    // the host seeded as struck (routine-born, restored) renders
    // settled from its first composition, whenever that is.
    struckSessionId: Int? = null,
    onSessionStruck: (Int) -> Unit = {},
    // Slice E: the seal shot's t=0 on the controller's clock
    // (elapsedRealtime), present only for the live witnessed
    // attempt; null for sessions born outside it (routine-born,
    // restored) — those render pre-settled and no shot replays.
    sealT0MonoMs: Long? = null,
    // Slice E: the finish machine's held frame and the standing
    // receipt, with the sled gate's host seams (Schlitten §3).
    finishState: com.example.ui.MonolithViewModel.FinishState? = null,
    receiptState: com.example.data.ReceiptState? = null,
    onWitnessSledReturn: (Int) -> Unit = {},
    onTryStartSledReturn: (Int) -> Boolean = { false },
    sledInitialFraction: () -> Float = { 0f },
) {
    val scheme = MaterialTheme.colorScheme
    // Slice E: the two AndroidView instances, held across the
    // seal (Zug-II §4.1's hoist in reference form): the shot's
    // driver and the sled's scheduler act on THE views the seal
    // happened on, never on a recomposed namesake.
    var wheelViewRef by remember { mutableStateOf<RotaryWheelView?>(null) }
    var slideViewRef by remember { mutableStateOf<SlideToStartView?>(null) }
    // The cascade's gate (step 4): the row is born at the latch,
    // but the session is not RUNNING until the cascade declares
    // t=0. While the attempt on THIS row is PROVING or JAMMED,
    // the Deck renders its idle form — the readout never starts
    // on an unproven seal, and the imprint's Strike (keyed below
    // off this same flag) fires exactly at t=0, not at the row's
    // first emission. Sessions born outside the cascade
    // (routines, restores) find the phase Idle and run as they
    // always have.
    val provingThisSession = when (val p = armPhase) {
        is ArmPhase.Proving -> p.sessionId == null || p.sessionId == activeSession?.id
        is ArmPhase.Jammed -> p.sessionId == null || p.sessionId == activeSession?.id
        else -> false
    }
    val isSessionRunning = activeSession != null && activeSession.isActive && !provingThisSession
    val effectiveNight = isDarkTheme || isSessionRunning

    // =====================================================================
    // SLICE E — the seal shot (Zug-II §5.3) and the finish's held
    // frame (Nocke §3.2). The shot is ONE state object; its phases
    // are derived from the controller's own clock (elapsedRealtime —
    // the merged cascade standardised on it; Zug-II's paper assumed
    // uptimeMillis and the shot reads the clock its t=0 was stamped
    // on). Derive-don't-replay: a shot first observed past a phase
    // boundary starts past it; a pre-existing session (routine-born,
    // restored) is born settled and no shot ever replays for it.
    // =====================================================================
    var sealShot by remember { mutableStateOf<SealShotState?>(null) }
    var exitPhase by remember { mutableStateOf(0) }
    LaunchedEffect(activeSession?.id, isSessionRunning) {
        val session = activeSession
        if (isSessionRunning && session != null) {
            if (sealShot?.sessionId != session.id) {
                val t0 = sealT0MonoMs
                sealShot = if (t0 != null) {
                    SealShotState(session.id, t0, preExisting = false)
                } else {
                    exitPhase = 3
                    SealShotState(session.id, 0L, preExisting = true)
                }
            }
        } else if (session == null && sealShot != null) {
            sealShot = null
            exitPhase = 0
        }
    }
    val shot = sealShot
    LaunchedEffect(shot) {
        if (shot == null || shot.preExisting) return@LaunchedEffect
        // The ghosts take their posts before the first shot frame.
        slideViewRef?.sealGhost()
        while (!shot.heroLanded) {
            withFrameMillis { }
            val e = SystemClock.elapsedRealtime() - shot.t0Mono
            shot.vertigoT = MonolithCurves.VertigoTransformEasing.transform(
                (e / 1050f).coerceIn(0f, 1f)
            )
            shot.blurT = MonolithCurves.VertigoBlurEasing.transform(
                (e / 1050f).coerceIn(0f, 1f)
            )
            if (e >= 180L) shot.departed = true
            if (e >= 400L) shot.groundStepped = true
            val phase = (e / 50L).toInt().coerceIn(0, 3)
            if (phase != exitPhase) exitPhase = phase
            slideViewRef?.sealExitPhase = phase
            if (e >= 1050L) {
                shot.heroLanded = true
                shot.vertigoT = 1f
            }
        }
    }
    val shotActive = shot != null && isSessionRunning && !shot.heroLanded
    // The night split (Zug-II §5.1): the hero reads t=0's night at
    // once (heroNight == effectiveNight); the GROUND — eyebrow,
    // wheel, slide — steps at t=400 exactly, with the scheme pin.
    val groundNight = isDarkTheme ||
        (isSessionRunning && (!shotActive || shot?.groundStepped == true))

    // The finish's held frame: while a finish detains, the Deck
    // renders the session's last frame — frozen readout, frozen
    // progress, the slide still seated — and nothing else moves.
    val heldFinish = finishState != null && !finishState.issued
    val showRunningForm = isSessionRunning || heldFinish

    // The sled's scheduler (Schlitten §3.2): the host effect that
    // turns a standing receipt into the return. Witness first,
    // then count the SAME 1050 the seal shot ran, from the
    // receipt's own publication stamp.
    LaunchedEffect(receiptState?.sessionId) {
        val receipt = receiptState ?: return@LaunchedEffect
        onWitnessSledReturn(receipt.sessionId)
        val deadline = receipt.receiptPublishedMonoMs + 1050L
        val remaining = deadline - SystemClock.elapsedRealtime()
        if (remaining > 0L) kotlinx.coroutines.delay(remaining)
        if (onTryStartSledReturn(receipt.sessionId)) {
            slideViewRef?.animateReturnHome()
        }
    }

    val isStopwatchMode = if (finishState != null) {
        finishState.frozenIsStopwatch
    } else if (isSessionRunning) {
        activeSession.isStopwatchMode
    } else {
        // The dial alone keys flow (ESR-08, truth patch at the
        // drawer merge, re-applied over this lane at the slide
        // merge): the engine arms on durationMinutes (< 10 =
        // stopwatch) and never reads the preset, so a wheel drag
        // to 90 after tapping FLOW must sell a timed 90.
        dialMinutes < 10
    }

    val displaySeconds = if (finishState != null) {
        // The held frame's frozen readout (F-L2: a FLOW finish
        // freezes its count-up total; a timed finish its last
        // remaining count) — the ticker has stopped; nothing here
        // re-derives from it.
        finishState.frozenDisplaySeconds
    } else if (isSessionRunning) {
        if (activeSession.isStopwatchMode) activeSession.elapsedSeconds else activeSession.remainingSeconds
    } else {
        dialMinutes * 60
    }

    val minsPart = displaySeconds / 60
    val secsPart = displaySeconds % 60
    val primaryReadout = String.format(Locale.US, "%02d:%02d", minsPart, secsPart)

    // True session span (BEYOND W3, verified against the engine): blocks
    // plus the 10-minute Safe Breaks BETWEEN them - n x block + (n-1) x 10.
    // The hero and the finish line both price from this and nowhere else;
    // the naive product understated a 3 x 50 morning by 20 minutes.
    val totalSpanMinutes = dialMinutes * studyBlocksCount + (studyBlocksCount - 1) * 10

    // Issue #6 F3 (audit §3, Vega T-1/T-2/T-3 — the floored,
    // inverted, invented fraction dies): timed progress is
    // ELAPSED / TARGET, filling, no floors — never remaining/target
    // born full and draining, never a 3–5% that isn't true. FLOW
    // (stopwatch) has NO bar and NO fraction: NO END means no bar,
    // and the old /1200 denominator was invented by the mode that
    // denies it. Idle: no session exists, so no progress exists.
    val progressFraction = if (finishState != null) {
        if (finishState.frozenIsStopwatch) {
            0f
        } else {
            val target = finishState.frozenTargetSeconds.coerceAtLeast(1)
            val elapsed = (target - finishState.frozenDisplaySeconds).coerceAtLeast(0)
            (elapsed.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        }
    } else if (isSessionRunning) {
        if (activeSession.isStopwatchMode) {
            0f
        } else {
            val target = activeSession.effectiveTargetSeconds.coerceAtLeast(1)
            val elapsed = (target - activeSession.remainingSeconds).coerceAtLeast(0)
            (elapsed.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        }
    } else {
        0f
    }

    // =====================================================================
    // SLICE D — the strip's derivations (Uma/Rosa/Emil). TIME is a pure
    // function of now + a span; it is re-derived at minute boundaries
    // from a frame clock, never stored, never slid. The imprint's count
    // freezes at the seal (host-side, Emil's −1 law): the first
    // composition that sees a running session captures the then-live
    // count and the BLOCK verdict, keyed by the session's id.
    // =====================================================================
    var nowFrame by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            nowFrame = now
            kotlinx.coroutines.delay(60_000L - (now % 60_000L) + 50L)
        }
    }

    val liveWarrantState = deckWarrant?.state
    // The print tense law (Truth Car 3, UNA2-03/04): every warrant
    // print in this frame is judged against the warrant's READ
    // INSTANT — a warrant older than the store's threshold prints
    // UNPROVEN, never LIVE, and its dependent tones degrade with
    // it. liveWarrantState stays the raw wire word for the one
    // place that must follow the wire itself (the running BLOCK
    // cell's OFF, below).
    val nowMono = SystemClock.elapsedRealtime()
    val warrantPrintState = com.example.service.WatchStore.printState(deckWarrant, nowMono)
    fun blockCellFor(state: WarrantState?): Pair<String, StripDensity> = when (state) {
        WarrantState.LIVE -> "ON" to StripDensity.PASS
        WarrantState.OFF -> "OFF" to StripDensity.FAULT
        else -> "UNKNOWN" to StripDensity.UNKNOWN
    }
    val idleBlockCell = blockCellFor(warrantPrintState)
    val idleTimeCell: Pair<String, StripDensity> = when {
        isStopwatchMode && !isSessionRunning -> "NO END" to StripDensity.PASS
        dialMinutes !in 5..180 || studyBlocksCount !in 1..8 -> "INVALID" to StripDensity.FAULT
        // UNA2-03 rider: "ENDS HH:mm" is a fact about the plan, but
        // its PASS tone is a claim the wire stands behind — beside
        // a warrant that is not freshly LIVE the end prints at
        // UNKNOWN density, never PASS beside BLOCK OFF.
        else -> endsAtText(nowFrame + totalSpanMinutes * 60_000L) to
            if (warrantPrintState == WarrantState.LIVE) StripDensity.PASS
            else StripDensity.UNKNOWN
    }

    // Emil's mid-press latch: at DOWN the host freezes the imprint
    // tenant and the TIME text; they render latched through the
    // whole press and release at the latch or the return to rest.
    // (Declared before the freeze: the seal retires a live latch.)
    var pressLatch by remember { mutableStateOf<PressLatch?>(null) }

    // A committed press retires its latch at the SEAL, not at the
    // view's press-end (Emil E-5): between the latch frame and the
    // sealed composition, the latched print must keep standing.
    var commitInFlight by remember { mutableStateOf(false) }

    // The seal freeze (Emil §3.2, as far as this slice can honestly
    // build it): the ViewModel captures the count and the verdict
    // at the session's SEAL (VM scope — a tab switch cannot
    // re-capture them, review E-1/E-2). For routine/restored
    // sessions the seal is the birth; for a cascade arm the VM
    // re-captures at the SealedRunning transition, because under
    // proving birth is not the seal (Emil, cascade review E-11).
    // For a session born before this process (a restore), this
    // screen falls back to a saveable capture — and only
    // capture-WHEN-KNOWN: a null count never freezes, so a
    // PENDING can never harden into a permanent frozen UNKNOWN
    // (review E-3). The persisted heldAppsAtSeal column (F15,
    // Migration 9→10) is the permanent form and belongs to Slice E.
    var fbFrozenKey by rememberSaveable { mutableStateOf<Int?>(null) }
    var fbFrozenCount by rememberSaveable { mutableStateOf<Int?>(null) }
    var fbFrozenShut by rememberSaveable { mutableStateOf<Int?>(null) }
    var fbFrozenPeek by rememberSaveable { mutableStateOf<Int?>(null) }
    var fbFrozenState by rememberSaveable { mutableStateOf<String?>(null) }
    if (isSessionRunning && activeSession != null) {
        // The seal retires any live press latch: if the slide view
        // detached mid-refusal (a routine armed during the refusal's
        // 300ms), its onPressEnd never fires — the latch must not
        // leak across the seal (Joss, review J-2).
        if (pressLatch != null || commitInFlight) {
            pressLatch = null
            commitInFlight = false
        }
        if (sealFreeze?.sessionId != activeSession.id &&
            fbFrozenKey != activeSession.id && appsCellCount != null
        ) {
            fbFrozenKey = activeSession.id
            fbFrozenCount = appsCellCount
            fbFrozenShut = appsSplit?.shut
            fbFrozenPeek = appsSplit?.peek
            fbFrozenState = liveWarrantState?.name
        }
        // The split can land an emission after the count; fill it
        // in on its own pass so the frozen tenant is not silent
        // for the session's whole life over a one-frame lag.
        if (fbFrozenKey == activeSession.id &&
            (fbFrozenShut == null || fbFrozenPeek == null) && appsSplit != null
        ) {
            fbFrozenShut = appsSplit.shut
            fbFrozenPeek = appsSplit.peek
        }
    } else if (fbFrozenKey != null) {
        fbFrozenKey = null
        fbFrozenCount = null
        fbFrozenShut = null
        fbFrozenPeek = null
        fbFrozenState = null
    }

    // The sealFreeze branches must test the freeze's presence, not
    // only its key: at idle both sealFreeze and activeSession are
    // null, null == null is true, and the !! below threw at
    // composition — the idle Deck crashed in the harness before
    // this guard (found by the lane's completion proof, Oct 29).
    val frozenCount: Int? = when {
        sealFreeze != null && sealFreeze.sessionId == activeSession?.id -> sealFreeze.count
        fbFrozenKey == activeSession?.id -> fbFrozenCount
        else -> null
    }
    val frozenState: WarrantState? = when {
        sealFreeze != null && sealFreeze.sessionId == activeSession?.id -> sealFreeze.warrantState
        fbFrozenKey == activeSession?.id ->
            fbFrozenState?.let { runCatching { WarrantState.valueOf(it) }.getOrNull() }
        else -> null
    }
    val frozenShut: Int? = when {
        sealFreeze != null && sealFreeze.sessionId == activeSession?.id -> sealFreeze.shutCount
        fbFrozenKey == activeSession?.id -> fbFrozenShut
        else -> null
    }
    val frozenPeek: Int? = when {
        sealFreeze != null && sealFreeze.sessionId == activeSession?.id -> sealFreeze.peekCount
        fbFrozenKey == activeSession?.id -> fbFrozenPeek
        else -> null
    }

    // Annulment retires the press like the seal does (step 4):
    // the attempt un-existed, so its frozen print un-exists with
    // it — and the slide view is ordered home by the reset key
    // in its update pass below. The retirement is BODY-LEVEL, in
    // the first composition carrying the new key and before the
    // derivations below (Emil, cascade review E-13): as a
    // LaunchedEffect it ran one-to-two compositions late, and the
    // dead attempt's latched WILL SHUT prints lingered past the
    // annulment frame. The seal path above retires in-body on its
    // frame; this is the same law at the other door — and it
    // covers the latch-frame hand-back too, which increments the
    // same key (Joss CHARGE 2's fix).
    var seenArmResetKey by remember { mutableStateOf(armResetKey) }
    if (armResetKey != seenArmResetKey) {
        seenArmResetKey = armResetKey
        if (pressLatch != null || commitInFlight) {
            pressLatch = null
            commitInFlight = false
        }
    }

    // UNA2-04: the frozen verdict owns the running BLOCK cell
    // while the wire agrees the bind held — but the instant the
    // LIVE wire reads OFF, the cell follows the wire. A frozen ON
    // or HELD beside a live OFF was the audit's impossible frame:
    // one tense, true at the instant it prints.
    val blockCell = if (isSessionRunning) {
        when {
            liveWarrantState == WarrantState.OFF -> blockCellFor(WarrantState.OFF)
            // Issue #6 C5(b) (wave-2 Tech-exec): the degrade was
            // asymmetric — running BLOCK followed the wire down to
            // OFF but never to UNPROVEN, so a lost stamp or an
            // UNSET re-read left the frozen ON standing forever.
            // A live UNPROVEN wire, or a read gone stale for print
            // (UNA2-03), degrades the running cell to UNKNOWN. The
            // frozen verdict attests the seal act; it cannot outrank
            // the wire's present tense. (Issue #4's SealBar encodes
            // this same row as RUNNING_UNPROVEN — lamp dark, bolt
            // thrown.)
            liveWarrantState == WarrantState.UNPROVEN ||
                warrantPrintState == WarrantState.UNPROVEN ->
                blockCellFor(WarrantState.UNPROVEN)
            else -> blockCellFor(frozenState)
        }
    } else idleBlockCell
    val appsCell = when {
        isSessionRunning -> {
            if (frozenCount != null) frozenCount.toString() to StripDensity.PASS
            else "UNKNOWN" to StripDensity.UNKNOWN
        }
        pressLatch != null -> pressLatch!!.appsWord to pressLatch!!.appsDensity
        appsCellCount != null -> appsCellCount.toString() to StripDensity.PASS
        else -> "UNKNOWN" to StripDensity.UNKNOWN
    }
    val timeCell: Pair<String, StripDensity> = when {
        isSessionRunning && activeSession != null -> {
            if (activeSession.isStopwatchMode) {
                "NO END" to StripDensity.PASS
            } else {
                endsAtText(nowFrame + activeSession.spanRemainingSeconds * 1000L) to StripDensity.PASS
            }
        }
        pressLatch != null -> pressLatch!!.timeText to pressLatch!!.timeDensity
        else -> idleTimeCell
    }
    // Issue #4 — the seal bar's face, derived from the SAME cells
    // the retired proof strip printed (blockCell's word, timeCell's
    // text, the session's spanRemainingSeconds). The re-encoding is
    // pure (sealBarStateFor): position + light carry what BLOCK /
    // TIME printed as words, and the BIND / PROOF seam marks render
    // nowhere on this face any more. APPS keeps its one remaining
    // home in the imprint directly below (the tenant still prints
    // the split count); the bar's derivation does not read appsCell.
    val sealState = sealBarStateFor(
        isSessionRunning = isSessionRunning,
        blockWord = blockCell.first,
        remainingSeconds = if (isSessionRunning && activeSession != null &&
            !activeSession.isStopwatchMode
        ) {
            activeSession.spanRemainingSeconds
        } else {
            null
        }
    )
    val sealNumerals = endsAtNumeralsFor(timeCell.first)
    val imprintTenant = (if (!isSessionRunning) pressLatch?.tenant else null) ?: imprintTenantText(
        isSealed = isSessionRunning,
        shut = if (isSessionRunning) frozenShut else appsSplit?.shut,
        peek = if (isSessionRunning) frozenPeek else appsSplit?.peek,
        // One tense per frame (UNA2-04): sealed prints judge the
        // FROZEN verdict; idle prints judge the freshness-checked
        // live one. wireNow carries the live word into the sealed
        // tenant: OFF under a running session earns the print its
        // " · AS SEALED" tense mark (Hale W-24).
        warrantState = if (isSessionRunning) frozenState else warrantPrintState,
        wireNow = liveWarrantState
    )

    // Wrapper (Ren): the scroll column takes the flexible space and the
    // commit bar is PINNED beneath it — adding tasks can grow the content
    // forever and the start control can never be pushed off-screen again.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .testTag("tactile_deck_screen")
    ) {
    val deckScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            // Input death (Zug-II §4.6): the scroll is dead for
            // exactly the shot's span — 0 to 1050 — and alive
            // again at Settle; not a millisecond longer.
            .verticalScroll(deckScrollState, enabled = !shotActive)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // =====================================================================
        // SECTION 1: THE INSTRUMENT (Ren — hero card and dial are ONE object:
        // the readout lives in the same panel as the wheel that sets it.
        // The preset tiles are gone: one value, one control — the wheel.)
        // =====================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                // ESR-13 (chrome C6): the hero card is the Deck's one
                // raised surface — it takes surfaceBright, both
                // modes. The ground token (surface) here was the
                // leaf-paper false close's twin: a raised card
                // painted in the ground's own tone.
                .background(scheme.surfaceBright)
                .border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))
                // The vertigo (Zug-II §5.2): at the seal the room
                // recedes — scale and rise on the transform easing,
                // the blur on its own re-ease, peaking mid-shot and
                // gone at Settle. At rest every term is identity.
                .then(
                    if (shotActive && shot != null) {
                        val vt = shot.vertigoT
                        val blurDp = (6f * 4f * shot.blurT * (1f - shot.blurT)).dp
                        Modifier
                            .graphicsLayer {
                                scaleX = 1f - 0.06f * vt
                                scaleY = 1f - 0.06f * vt
                                // Issue #6 F15: the ground rise is
                                // DELETED, not moved (verdict) — the
                                // page recedes, it does not climb.
                            }
                            .then(if (blurDp > 0.dp) Modifier.blur(blurDp) else Modifier)
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    // The Ellipsis fix (Wren): the engine runs the
                    // 10-minute Safe Breaks between blocks and no screen
                    // said so - the break was invisible. During a break
                    // the eyebrow prints the word and the whole minutes
                    // left; the break is a fact, so it renders.
                    text = when {
                        isSessionRunning && activeSession.isInSafeBreak ->
                            "BREAK · ${(activeSession.safeBreakSecondsRemaining + 59) / 60} MIN"
                        // Issue #6 F4: SESSION RUNNING / TOTAL were
                        // wallpaper and die. A routine-born session
                        // prints its name; a hand-armed running
                        // session needs no label over its countdown.
                        showRunningForm ->
                            if (activeSession?.routineId != null) {
                                "${activeSession.intention.uppercase()} · ROUTINE"
                            } else {
                                ""
                            }
                        // Idle prints the recipe — the hero's
                        // arithmetic, explained on the face that
                        // charges it (audit §3).
                        else -> if (studyBlocksCount > 1) {
                            "$studyBlocksCount × $dialMinutes + BREAKS"
                        } else {
                            // One block has no break (breaks live
                            // BETWEEN blocks — totalSpanMinutes
                            // prices (count − 1) × 10): the recipe
                            // must not promise one (finisher pass).
                            "$studyBlocksCount × $dialMinutes"
                        }
                    },
                    fontFamily = AnonymousProFamily,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = scheme.onSurfaceVariant
                    // Issue #6 F10 family: the eyebrow's fake day
                    // emboss (white 1dp shadow) dies — print is flat.
                )
                // The eyebrow's count is EVICTED (D9/A8): the app
                // count's only homes are the strip's APPS cell and
                // the imprint — one number, LOCKED+PEEK, one rendering
                // per fact.
            }

            // The hero states the TOTAL, once (one fact, one rendering).
            // Span classes (BEYOND W3 / Aldo): N = "50 MIN", H = "2H 30M",
            // W = "FLOW" - digits at H2 72, units at H3 50 on the shared
            // baseline, same ink. The span is the true span (breaks priced
            // in). Night steps the numeral up to glyph (D1). The press
            // (D3): two static ghosts at +-1dp behind the glyph, print not
            // rig - it dies on device if it reads as blur (removal trial).
            val heroAnnotated = when {
                showRunningForm -> buildAnnotatedString { append(primaryReadout) }
                isStopwatchMode -> buildAnnotatedString { append("FLOW") }
                totalSpanMinutes < 60 -> buildAnnotatedString {
                    append(totalSpanMinutes.toString())
                    withStyle(SpanStyle(fontSize = 50.sp, letterSpacing = (-1.5).sp)) { append(" MIN") }
                }
                else -> buildAnnotatedString {
                    append((totalSpanMinutes / 60).toString())
                    withStyle(SpanStyle(fontSize = 50.sp, letterSpacing = (-1.5).sp)) { append("H ") }
                    append(String.format(Locale.US, "%02d", totalSpanMinutes % 60))
                    withStyle(SpanStyle(fontSize = 50.sp, letterSpacing = (-1.5).sp)) { append("M") }
                }
            }
            val heroTracking = if (!showRunningForm && isStopwatchMode) (-0.5).sp else (-2.0).sp
            val heroGlyph = if (effectiveNight) Color(0xFFEDE3D8) else scheme.onBackground
            val heroShadowGhost = if (effectiveNight) Color(0x80000000) else Color(0x99D8D1CB)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                // Issue #6 F10 — SINGLE-PRESS: the catch ghost dies;
                // ONE shadow ghost survives, and ghosts are OFF on
                // FLOW and on the ticking readout (a press does not
                // strike a moving target).
                if (!showRunningForm && !isStopwatchMode) {
                    Text(
                        text = heroAnnotated,
                        style = DialClockTextStyle.copy(color = heroShadowGhost, letterSpacing = heroTracking),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(y = (-1).dp).clearAndSetSemantics {}
                    )
                }
                Text(
                    text = heroAnnotated,
                    style = DialClockTextStyle.copy(color = heroGlyph, letterSpacing = heroTracking),
                    textAlign = TextAlign.Center
                )
            }

            if (!isSessionRunning || shotActive) {
                // The wheel stays composed through the shot as a
                // frozen ghost (Zug-II §4.1): same instance, physics
                // halted by its lock, ground-night pushed at the
                // t=400 step. At Settle the progress track takes
                // the room, as shipped.
                AndroidView(
                    factory = { ctx ->
                        RotaryWheelView(ctx).apply {
                            this.soundEngine = soundEngine
                        }.also { wheelViewRef = it }
                    },
                    update = { view ->
                        wheelViewRef = view
                        view.isNightMode = groundNight
                        view.soundEngine = soundEngine
                        view.minutesValue = dialMinutes
                        view.studyBlocksCount = studyBlocksCount
                        view.isLockedSession = shotActive
                        view.onMinutesChanged = { mins -> onDialMinutesChanged(mins) }
                        view.onCycleBlocksCount = {
                            val nextBlocks = if (studyBlocksCount >= 8) 1 else studyBlocksCount + 1
                            onSetStudyBlocksCount(nextBlocks)
                        }
                        view.onSetBlocksCount = { count -> onSetStudyBlocksCount(count) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(288.dp)
                        .testTag("rotary_wheel_view")
                )
                if (!isSessionRunning) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // FLOW as a key cap (Ren, ET66 lesson): a raised pill with a
                    // hairline edge reads pressable; active state = ink fill.
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isStopwatchMode) inkControlFill() else scheme.surfaceVariant)
                            .border(1.dp, if (isStopwatchMode) inkControlFill() else scheme.outlineVariant, RoundedCornerShape(12.dp))
                            .clickable {
                                onSelectStudyPreset(StudyModePreset.STOPWATCH_FLOW)
                                onDialMinutesChanged(5)
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "FLOW",
                            fontFamily = AnonymousProFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = if (isStopwatchMode) onInkControlFill() else scheme.onBackground
                        )
                    }
                    // Pia: the FINISHES line dies here — TIME's only
                    // home is the strip's TIME cell (a pure function;
                    // this row's copy slid with every recomposition).
                }
                }
            } else {
                // Running: the wheel hands over to a single progress track.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(scheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            // E5b B4 (Horiz §2) governs the fill's
                            // standing form: born scheme.primary
                            // (chrome C7), it EXITS to the ink
                            // control fill at the seal — and since
                            // this track first composes at Settle,
                            // post-exit, ink is its only composed
                            // state. C7's intent (no second, louder
                            // orange) is served, not broken.
                            .background(inkControlFill())
                    )
                }
                // Issue #6 F3: the % text is KILLED (audit §3,
                // unanimous) — the track itself is the reading;
                // a printed percent was the floored fraction's
                // second lie. FLOW renders the empty track only.
            }
        }

        // =====================================================================
        // SECTION 2: THE TASK — one quiet line (Ren). Sub-steps behind a tap.
        // =====================================================================
        if (activeTask != null) {
            var stepsExpanded by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "NOW",
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        letterSpacing = 1.sp,
                        color = scheme.onBackground
                    )
                    Text(
                        text = activeTask.title,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = scheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (activeTask.subSteps.isNotEmpty() && !shotActive) {
                                    Modifier.clickable { stepsExpanded = !stepsExpanded }
                                } else {
                                    Modifier
                                }
                            )
                    )
                    // DONE is a verb, so it wears an action's body
                    // (ESR-07): the same raised key-cap as the FLOW
                    // key — bare mono text beside the static QUEUE
                    // count read as print, declaring an unfinished
                    // task done. (Truth patch, re-applied at the
                    // slide merge over this lane's older Deck.)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(scheme.surfaceVariant)
                            .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                            // Input death (Zug-II §4.6): the task
                            // verbs' HANDLERS are removed by state
                            // during the shot — not greyed, gone.
                            .clickable(enabled = !shotActive) { onCompleteActiveTask(activeTask.id) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "DONE",
                            fontFamily = AnonymousProFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = scheme.onBackground
                        )
                    }
                    Text(
                        text = "QUEUE ${queuedTasks.size}",
                        fontFamily = AnonymousProFamily,
                        fontSize = 11.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.then(
                            if (!shotActive) Modifier.clickable { onOpenTaskSequencer() } else Modifier
                        )
                    )
                }
                if (stepsExpanded && activeTask.subSteps.isNotEmpty()) {
                    activeTask.subSteps.forEachIndexed { idx, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !shotActive) { onToggleTaskSubStep(activeTask.id, idx) }
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                // The shared two-digit voice (DRW-08,
                                // Truth Car 2): the tenth sub-step
                                // prints "10", never "010" — one
                                // formatter cures Deck, Tasks leaf,
                                // and Drawer detail together.
                                text = com.example.data.twoDigitCount(idx + 1),
                                fontFamily = AnonymousProFamily,
                                fontSize = 11.sp,
                                // E5b B3 (Horiz §2): a done step's
                                // numeral exits the accent at the
                                // seal — from exit phase 1 it reads
                                // as ground print, permanently for
                                // this session's frame.
                                color = if (step.isDone && exitPhase < 1) {
                                    scheme.primaryContainer
                                } else {
                                    scheme.onSurfaceVariant
                                }
                            )
                            Text(
                                text = step.title,
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontSize = 13.sp,
                                color = if (step.isDone) scheme.onSurfaceVariant else scheme.onBackground,
                                textDecoration = if (step.isDone) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
            }
        }

        // =====================================================================
        // SECTION 3: ARMED — session utilities (desk clock, held notifications)
        // =====================================================================
        // F-Z3: the utilities row takes its place at Settle — during
        // the shot's detention the row is absent, not disabled.
        if (isSessionRunning && !shotActive) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BlockitActionChip(
                    wordLabel = "DESK CLOCK",
                    isPrimaryInk = true,
                    onClick = onOpenAlwaysOnDeskClock,
                    modifier = Modifier.weight(1f),
                    testTag = "open_desk_clock_button"
                )
                BlockitActionChip(
                    wordLabel = "HELD",
                    monoBadge = capturedNotificationsCount.toString(),
                    onClick = onOpenNotificationDigest,
                    modifier = Modifier.weight(1f)
                )
            }
            // ST-02 (Truth Car 3): the break's own verb, seated in
            // the utilities' room for exactly the break's life. The
            // register's charge was plain — the break ended only
            // when its timer expired, and a man ready early waited
            // out dead minutes. The chip names the act, not the
            // countdown (the eyebrow already prints the minutes
            // left); the tap performs the expiry transition now
            // and books the taken part — see endSafeBreakEarly in
            // the repository. Full width, primary ink: during a
            // break this is the Deck's one forward verb.
            if (activeSession?.isInSafeBreak == true) {
                BlockitActionChip(
                    wordLabel = "END BREAK",
                    isPrimaryInk = true,
                    onClick = onEndSafeBreak,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "end_break_button"
                )
            }
        }

    }
        // =====================================================================
        // SECTION 5: THE PINNED STACK (Slice D) — outside the scroll:
        // the seal bar (issue #4 — THE BOLT's 60dp housing, grown
        // from the retired proof strip's 53dp address per FEILE's
        // grid; the pinned stack below it is unchanged), the slide's 72dp register,
        // the reserved sentence slot, and the imprint. The slide zone
        // holds the grace / valve chips at the same address while
        // sealed (Δy = 0 across tenancies), so the zone under the
        // wheel never changes height by a pixel across the seal.
        // =====================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            SealBar(
                state = sealState,
                endsAtNumerals = sealNumerals
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isSessionRunning || shotActive || heldFinish || receiptState != null) {
                    // The slide holds this address through the seal
                    // (as the seated ghost), through the finish's
                    // detention, and through its own return — the
                    // chips take the address only for a settled,
                    // receipt-free running session (F-Z3's room,
                    // read by Kato's fallback: the utilities keep
                    // the Δy = 0 address the zone has always had).
                    AndroidView(
                        factory = { ctx ->
                            SlideToStartView(ctx).apply {
                                this.soundEngine = soundEngine
                                this.initialFraction = sledInitialFraction()
                            }.also { slideViewRef = it }
                        },
                        update = { view ->
                            slideViewRef = view
                            view.isNightMode = groundNight
                            view.soundEngine = soundEngine
                            view.isArmedState = false
                            if (view.resetKey != armResetKey) {
                                view.resetKey = armResetKey
                                if (armResetKey > 0) view.resetToRest()
                            }
                            view.warrantProvider = warrantProvider
                            view.onPressStart = { downState ->
                                // The latch captures from the slide's
                                // own DOWN-frame read (Emil E-4), never
                                // from this screen's async flow.
                                pressLatch = PressLatch(
                                    tenant = imprintTenantText(
                                        isSealed = false,
                                        shut = appsSplit?.shut,
                                        peek = appsSplit?.peek,
                                        warrantState = downState
                                    ),
                                    timeText = idleTimeCell.first,
                                    timeDensity = idleTimeCell.second,
                                    appsWord = appsCell.first,
                                    appsDensity = appsCell.second
                                )
                            }
                            view.onPressEnd = {
                                if (!commitInFlight) pressLatch = null
                            }
                            view.onSlideCommitted = {
                                commitInFlight = true
                                onArmSession()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .testTag("slide_to_start_view")
                    )
                } else {
                    val withinGraceWindow = activeSession != null && activeSession.elapsedSeconds <= 10
                    if (withinGraceWindow) {
                        BlockitActionChip(
                            wordLabel = "GRACE WINDOW ABORT",
                            monoBadge = "${10 - (activeSession?.elapsedSeconds ?: 0)}s",
                            // E5b B6 (Horiz §2): the grace chip
                            // exits the accent with the rest — it
                            // takes the primary-ink form, and its
                            // badge follows the app-wide badge rule.
                            isPrimaryInk = true,
                            onClick = onCancelWithinGraceWindow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp), // Issue #6 F6: the chair fills its 72dp zone in every state
                            testTag = "grace_abort_button"
                        )
                    } else {
                        BlockitActionChip(
                            wordLabel = "EMERGENCY BREAK VALVE (60S HOLD)",
                            monoBadge = "$weeklyEmergencyTokensRemaining/1 WEEK",
                            isPrimaryInk = true,
                            onClick = onRequestDisarm,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp), // Issue #6 F6: the chair fills its 72dp zone in every state
                            testTag = "request_disarm_button"
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            RefusalSentenceSlot(
                // The slot's tenant by phase (step 4): a JAMMED
                // attempt names its unanswering link; PROVING is
                // silent (the machine is working, and the wire's
                // standing sentence would be a second rendering of
                // a fact the cascade is already judging); an
                // annulment's past-tense sentence stands until the
                // controller clears it; a running session is
                // silent; otherwise the wire's standing sentence.
                sentence = when {
                    armPhase is ArmPhase.Jammed -> jamSentenceFor(armPhase)
                    armPhase is ArmPhase.Proving -> null
                    isSessionRunning -> null
                    armSentence != null -> armSentence
                    else -> refusalSentenceFor(deckWarrant, nowMono)
                },
                onAction = { action ->
                    when (action) {
                        RefusalAction.FIX_ACCESSIBILITY -> onFixBlockingAccess()
                        RefusalAction.FIX_USAGE -> onFixUsageAccess()
                        RefusalAction.RETRY_CHECK -> {
                            // Suki: an UNSET usage grant makes "retry"
                            // a lie — the tap routes to the pane that
                            // owns the fact instead.
                            if (deckWarrant?.usage == UsageAccess.UNSET) {
                                onFixUsageAccess()
                            } else {
                                onRetryBlockingCheck()
                            }
                        }
                    }
                },
                onHandBackTick = onHandBackTick
            )
            ImprintSlot(
                tenantText = imprintTenant,
                isSealed = isSessionRunning,
                strikeKey = if (isSessionRunning) activeSession?.id?.toLong() else null,
                struckKey = struckSessionId?.toLong(),
                onStruck = { key -> onSessionStruck(key.toInt()) }
            )
        }
    }
}

/**
 * Slice E (Zug-II §5.3): the seal shot's ONE state object per
 * session. Its phases are never stored — DEPARTURE / RECESSION /
 * SETTLE are derived from (now − t0) — and its three booleans are
 * LATCHES the driver sets as boundaries pass, so a shot first
 * observed late starts late (derive-don't-replay). [vertigoT] and
 * [blurT] are snapshot state written only by the Deck's driver
 * loop; at rest (preExisting) every latch stands set and both
 * easings read their terminal.
 */
class SealShotState(
    val sessionId: Int,
    val t0Mono: Long,
    val preExisting: Boolean
) {
    var vertigoT: Float by mutableStateOf(if (preExisting) 1f else 0f)
    var blurT: Float by mutableStateOf(if (preExisting) 1f else 0f)
    var departed: Boolean by mutableStateOf(preExisting)
    var groundStepped: Boolean by mutableStateOf(preExisting)
    var heroLanded: Boolean by mutableStateOf(preExisting)

    /** The phase name, for the record: <180 DEPARTURE, <780
     *  RECESSION, <1050 SETTLE, then landed. */
    fun phaseName(nowMono: Long): String = when {
        heroLanded -> "SETTLED"
        preExisting -> "SETTLED"
        nowMono - t0Mono < 180L -> "DEPARTURE"
        nowMono - t0Mono < 780L -> "RECESSION"
        else -> "SETTLE"
    }
}

/** Emil's mid-press latch (§3.1d): the imprint tenant, the TIME
 *  text AND its density, and the APPS cell as they stood at DOWN,
 *  rendered frozen through the press — all three prints of the
 *  press's facts, not two of three (review E-5/E-6). */
private data class PressLatch(
    val tenant: String?,
    val timeText: String,
    val timeDensity: StripDensity,
    val appsWord: String,
    val appsDensity: StripDensity
)

