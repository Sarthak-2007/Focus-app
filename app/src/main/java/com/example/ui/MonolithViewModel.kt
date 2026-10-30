package com.example.ui

import android.util.Log
import com.example.MLog
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppBlockTier
import com.example.data.ClockText
import com.example.data.AppCategory
import com.example.data.BreakResult
import com.example.data.BlockedAppEntity
import com.example.data.CapturedNotificationEntity
import com.example.data.CurioCatalog
import com.example.data.DailyFocusStat
import com.example.data.DistractionTriggerReason
import com.example.data.FocusProfileEntity
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.data.InterceptLogEntity
import com.example.data.KineticArtifact
import com.example.data.MonolithRepository
import com.example.data.PruneNote
import com.example.data.SmartLoadout
import com.example.data.StudyChecklistItem
import com.example.data.StudyModePreset
import com.example.data.SystemGuardStatus
import com.example.data.TaskQueueStatus
import com.example.service.InterceptPath
import com.example.service.MonolithShieldService
import com.example.service.PeekPillOverlay
import com.example.service.SoundEffectsSetting
import com.example.service.TactileSoundEngine
import com.example.service.Warrant
import com.example.service.WarrantState
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Issue #107 (Audit R3, shield-setup-lived #5): the Shield search
 * match. It used to test the display name only, while the screen
 * printed "NO APP BY THAT NAME ON THIS PHONE" for any zero result —
 * a false claim of absence for an app installed under another name
 * or findable by its package. The match now also covers the package
 * name and the category (name and printed label), and the screen's
 * no-match line no longer claims absence.
 */
fun appMatchesQuery(app: BlockedAppEntity, query: String): Boolean {
    val q = query.trim()
    if (q.isBlank()) return true
    if (app.appName.contains(q, ignoreCase = true)) return true
    if (app.packageName.contains(q, ignoreCase = true)) return true
    val category = runCatching { AppCategory.valueOf(app.category) }.getOrNull()
    if (category != null) {
        if (category.name.contains(q, ignoreCase = true)) return true
        if (category.label.contains(q, ignoreCase = true)) return true
    }
    return app.category.contains(q, ignoreCase = true)
}

enum class MonolithDestination(val route: String, val label: String) {
    DECK("deck", "Deck"),
    TASKS("tasks", "Tasks"),
    SHIELD("shield", "Shield"),
    VAULT("vault", "Ledger")
    // GUARD is DISSOLVED (chrome wave): the destination, its screen,
    // and its -1 tab mapping are deleted. What Guard held lives in
    // PARK (the rail's end zone) and on Shield (the websites editor).
}

/**
 * The theme evaluation (chrome C4, Sten's precedence, §8.10): ONE
 * evaluation drives both the colour scheme and PARK's printed
 * readout, so the two can never disagree. (1) a session is active
 * AND its shot has GROUNDED → NIGHT, the session owns it (Issue
 * #143: the caller passes the GROUNDED-stepped value — night
 * never lands at the row's birth or during proving); (2) else a
 * finish is being lived through (detention + receipt, until the
 * finish state clears) → NIGHT, the finish holds it; (3) else an
 * enabled, unpaused SLEEP window is active → NIGHT, that routine
 * owns it and is named as printed on its Shield row; (4) else
 * CLOCK/SYSTEM night → NIGHT, the night itself owns it (Audit
 * R3b #116: darkness was hostage to accepting a blocking
 * routine — pausing or disabling SLEEP to stop its blocking also
 * cost the dark UI, and at 04:00 the app opened in full light;
 * the one [com.example.service.NightSignal] now owns this step,
 * the same signal the desk clock, the seal shot and the sound
 * engine read); (5) else DAY, owned by nothing — and the
 * readout says so. Window state comes from the one
 * derivation (RoutineDerive), never a second computation.
 *
 * Because step (4) holds night until 06:00, a session or SLEEP
 * window ending at night can no longer cut to the light scheme in
 * a dark room: night is held till morning by construction.
 */
data class ThemeEvaluation(val isNight: Boolean, val readout: String)

fun evaluateTheme(
    sessionActive: Boolean,
    routines: List<com.example.data.RoutineEntity>,
    nowMillis: Long,
    systemNight: Boolean = false,
    finishHeld: Boolean = false
): ThemeEvaluation {
    if (sessionActive) {
        return ThemeEvaluation(true, "AUTO · NIGHT — SESSION ACTIVE")
    }
    if (finishHeld) {
        return ThemeEvaluation(true, "AUTO · NIGHT — FINISH HELD")
    }
    val owner = routines.firstOrNull { routine ->
        routine.loadoutName == SmartLoadout.SLEEP.name &&
            com.example.data.RoutineDerive.deriveOne(
                routine, nowMillis, null, emptyList(),
                com.example.data.GuardSettingsEntity()
            ).state in setOf(
                com.example.data.WindowState.OPEN_IDLE,
                com.example.data.WindowState.OPEN_RUNNING,
                com.example.data.WindowState.OPEN_HELD
            )
    }
    if (owner != null) {
        val start = String.format(
            Locale.US, "%02d:%02d", owner.startMinutes / 60, owner.startMinutes % 60
        )
        return ThemeEvaluation(true, "AUTO · NIGHT — ${owner.name} $start")
    }
    if (systemNight) {
        return ThemeEvaluation(true, "AUTO · NIGHT — SYSTEM DARK MODE")
    }
    if (com.example.service.NightSignal.isNightAt(nowMillis)) {
        return ThemeEvaluation(true, "AUTO · NIGHT — CLOCK 20:00–06:00")
    }
    return ThemeEvaluation(false, "AUTO · DAY — NO ROUTINE OWNS IT")
}

/**
 * The SEALED digest: the held notifications for one session, keyed
 * to that session. While sealed, the digest renders through this
 * holder in frozen MODE — read-only, no row opens, no delete — but
 * the snapshot itself REFRESHES (issue #118, finding 3): opening the
 * sheet remints it, and while the sheet stands open every new held
 * item remints it again, so mid-session triage can see what has
 * arrived since the session started. Refreshing what is SHOWN
 * unlocks nothing: the repository's sealed gate still refuses every
 * delete/clear while a session is active. It survives the session's
 * end until dismissed unsealed.
 */
data class FrozenDigest(
    val items: List<CapturedNotificationEntity>,
    val frozenAtMillis: Long,
    val sessionId: Int
)

data class FloatingPeekPillState(
    val isActive: Boolean = false,
    val packageName: String = "",
    val appName: String = "",
    val secondsRemaining: Int = 120
)

/**
 * Why a peek was withheld on a door that could have honoured one
 * (Truth Car 3, UNA2-01; Ren's Held Door Truth Baseline: a withheld
 * peek prints its cooldown state AND its reason). Null when the
 * door never owed a peek — a non-honouring door, an unlisted
 * package: those doors print no peek line at all, never a refusal
 * for an offer that was never on the table.
 *
 * Issue #154 amendment: TIER_LOCKED joins the enum — NOT as a
 * refusal (no offer was made to refuse) but as the door's own
 * state line. The baseline's silence for a locked app read to
 * the person holding the phone as breakage: a legitimate need
 * met a blank wall, no fact printed, and the only visible exit
 * was the week's single emergency token. The line now names the
 * tier the user himself set and where tiers are set — a fact,
 * not a refusal.
 */
enum class PeekWithheld { COOLDOWN, ALREADY_ACTIVE, PEEK_OFF, TIER_LOCKED }

/**
 * The one peek-offer computation (Truth Car 3, UNA2-01). The offer
 * exists ONLY where a lease can actually be honoured:
 *  - the door must read the lease (APP_TIER / SHIELD_APP / SITE —
 *    the sniper and the uninstall guard never do, and the guard's
 *    package is unlisted: startPeekLease would write to no row and
 *    the "peek" would protect nothing),
 *  - the package must have a row (a null entity never defaults ON),
 *  - its cooldown spent, no pill active,
 *  - and, on the app doors, its tier must be PEEK_2M.
 * W3 #130: the SITE door's lease is domain-scoped, not tier-scoped —
 * the browser's own tier governs the app door (an OPEN Chrome is a
 * legitimate study browser), while the site list is what the site
 * door holds. Requiring PEEK_2M there would leave blocked sites
 * with no peek at all in exactly the sessions where the browser is
 * open: unlimited-or-nothing, the finding's inverted web.
 * Pure, so the offer matrix is unit-tested door by door.
 */
internal fun peekOfferFor(
    path: InterceptPath,
    entity: BlockedAppEntity?,
    cooldownLeftSeconds: Int,
    pillActive: Boolean
): Boolean {
    if (!path.canHonourPeekLease || entity == null || pillActive) return false
    // W3 #130: the SITE door's lease is domain-scoped, not tier-scoped —
    // on that door the offer turns on the cooldown alone (the browser's
    // own tier governs the app door, never the site door).
    if (path == InterceptPath.SITE) return cooldownLeftSeconds == 0
    // Issue #120 (otp-banking-mid-session): FINANCE is essential
    // infrastructure. A LOCKED bank/UPI/authenticator still offers
    // the peek — the alternative was ending the session through
    // the once-a-week emergency valve to approve one payment —
    // and the 20-minute cooldown never rations a finance peek: a
    // second OTP inside the window (login, then payment) is the
    // normal case, not abuse. The 5s wall and the 2-minute lease
    // still gate every finance peek exactly as before.
    if (entity.category == AppCategory.FINANCE.name) {
        return entity.resolvedTier != AppBlockTier.OPEN
    }
    return entity.resolvedTier == AppBlockTier.PEEK_2M &&
        cooldownLeftSeconds == 0
}

/**
 * Issue #78 (Audit R2, ipc-intents): may an Airlock raised from an
 * INTENT be shown at all? The intent is evidence that a door fired
 * at some point in the past — possibly before a process death — so
 * showing is re-derived from the LIVE facts, never from the
 * intent's extras:
 *  - no active session, no Airlock (every firing door is itself
 *    session-gated; a session that has since ended voids the
 *    firing with it);
 *  - on the two app-tier doors the package must still carry a
 *    non-OPEN tier, must not be free under the Safe Break's peek
 *    pass, and must not already hold the live peek lease the
 *    enforcement is honouring;
 *  - the sniper / site / guard / unknown doors intercept packages
 *    that legitimately have no row or an OPEN row (a browser, the
 *    Settings app), so for them the live session IS the whole
 *    revalidation — their peek offer is already always off
 *    (see [peekOfferFor]).
 * Pure, so the matrix is unit-tested door by door.
 */
internal fun airlockIntentRevalidated(
    path: InterceptPath,
    tier: AppBlockTier?,
    sessionActive: Boolean,
    inSafeBreak: Boolean,
    peekLeaseLive: Boolean
): Boolean {
    if (!sessionActive) return false
    return when (path) {
        InterceptPath.APP_TIER, InterceptPath.SHIELD_APP ->
            tier != null &&
                tier != AppBlockTier.OPEN &&
                !(inSafeBreak && tier == AppBlockTier.PEEK_2M) &&
                !peekLeaseLive
        else -> true
    }
}

/**
 * Issue #55: the withheld reason for a peek offer, computed from
 * live facts — used at intercept time AND when a grant is refused,
 * so the standing overlay always prints the current truth. A
 * running pill binds before the cooldown (verifier, pill race):
 * naming COOLDOWN while a pill also runs would let the overlay
 * locally unlock into a chip the grant would refuse.
 */
internal fun peekWithheldFor(
    canPeek: Boolean,
    path: InterceptPath,
    appEntity: BlockedAppEntity?,
    cooldownLeft: Int,
    pillActive: Boolean
): PeekWithheld? = when {
    canPeek -> null
    !path.canHonourPeekLease -> null
    // Issue #154 composed with issue #120 (STURM-4 merge): a LOCKED
    // tier on an honouring door names itself (TIER_LOCKED — a state
    // line, not a refusal; see the enum) — except finance rows,
    // which #120 carved out: a finance row owes the withheld reason
    // below (its tier may be LOCKED while the peek is still on the
    // table), so finance rows fall through instead of stopping here.
    // W3 #130 (recomposed at the DURCH-D landing): the SITE door is
    // exempt from BOTH tier branches — its offer is not tier-gated
    // (see peekOfferFor), so a withheld site peek is owed its reason
    // (PEEK_OFF / ALREADY_ACTIVE / COOLDOWN below) regardless of the
    // browser's app tier, never TIER_LOCKED and never silence.
    path != InterceptPath.SITE &&
        appEntity?.resolvedTier == AppBlockTier.LOCKED &&
        appEntity?.category != AppCategory.FINANCE.name -> PeekWithheld.TIER_LOCKED
    path != InterceptPath.SITE &&
        appEntity?.resolvedTier != AppBlockTier.PEEK_2M &&
        appEntity?.category != AppCategory.FINANCE.name -> null
    // Hale W-30: a peek length of zero is its own reason — the peek
    // is OFF, not cooling down.
    MonolithRepository.PEEK_DURATION_SECONDS <= 0 -> PeekWithheld.PEEK_OFF
    pillActive -> PeekWithheld.ALREADY_ACTIVE
    cooldownLeft > 0 -> PeekWithheld.COOLDOWN
    else -> null

}

data class AirlockInterceptState(
    val isVisible: Boolean = false,
    val interceptedPackageName: String = "",
    val interceptedAppName: String = "",
    val interceptReasonBadge: String = "Shut",
    val sessionIntention: String = "",
    val selectedReason: DistractionTriggerReason = DistractionTriggerReason.MUSCLE_MEMORY,
    val transmutedParticles: Int = 0,
    val canUse2MinutePeek: Boolean = true,
    /** Issue #55: a fresh identity for EVERY intercept — even a
     *  same-package re-intercept while the overlay already stands.
     *  The overlay keys its 5s wall and cooldown on this, never on
     *  (package, visibility), which a re-intercept left unchanged. */
    val interceptInstanceId: Long = 0L,
    /** Audit R6 #340 F17: how many times this package was already
     *  reached for TODAY (the DrawerDay filing day), counted by
     *  the ViewModel from the persisted intercept log. The overlay
     *  prices the wall from it: the configured breath base
     *  (issue #318 F4) doubled per prior reach, capped at 120.
     *  0 = the first reach of the day. */
    val priorReachesToday: Int = 0,
    /** Issue #55: when this intercept fired, in the MONOTONIC
     *  `SystemClock.elapsedRealtime()` domain — the overlay's wall
     *  is deadline-derived from this instant, so a wall-clock jump
     *  can never zero or stretch the 5 seconds. */
    val interceptedAtMonoMillis: Long = 0L,
    /** Issue #55: the peek cooldown's absolute deadline in the
     *  same monotonic domain (repository anchor + 20 min), 0 when
     *  no cooldown is armed. The overlay derives its remaining
     *  time from this instant with ceiling math, mirroring
     *  [peekSecondsRemaining] — its unlock instant is never before
     *  the repository's grant instant. */
    val peekCooldownEndsAtMonoMillis: Long = 0L,
    val peekCooldownRemainingSeconds: Int = 0,
    /** The door that fired (UNA2-01): carried from the firing
     *  service through the intent into this state, never inferred
     *  from the badge or the package. */
    val interceptPath: InterceptPath = InterceptPath.UNKNOWN,
    /** Non-null only when this door could have honoured a peek
     *  and the tier owed one: the withheld line's reason. */
    val peekWithheld: PeekWithheld? = null,
    /** Issue #99 F5: true when the intercepted app's tier is LOCKED.
     *  For LOCKED apps the 5s wall gates RETURN TO FOCUS itself
     *  (the delay is the point of the wall) — before this, the wall
     *  counted down to nothing: RETURN was live from second 5 and
     *  no option ever appeared at 00. */
    val isLockedTier: Boolean = false,
    /** The session's span end (SIL-1, Hale W-28): the door states
     *  the end from the session's own numbers — the same
     *  now + remaining derivation the Deck's TIME cell prints.
     *  Null when there is no timed end to state (stopwatch, or no
     *  session): the door never invents one. */
    val sessionEndsAtMillis: Long? = null,
    val sessionIsStopwatch: Boolean = false,
    /** Issue #325 (Audit R5, Zario visual): the stake, carried onto
     *  the door itself — the week's emergency-break balance read
     *  from the repository's spend ledger at the intercept instant
     *  (the same flow the Deck renders). -1 = not carried (previews,
     *  tests): the stake line prints nothing rather than inventing
     *  a balance. */
    val emergencyBreaksRemaining: Int = -1,
    /** Audit R3b #147: a stopwatch session has no set end, but it
     *  DOES have a floor moment — the wall instant its clean exit
     *  opens (start + the 20-minute floor, on the session's own
     *  banked elapsed). The door states it instead of the bare
     *  "NO SET END". Null for timed sessions and no session. */
    val stopwatchFloorAtMillis: Long? = null
)

/**
 * The peek's remaining seconds, derived from the lease's STORED
 * END (Truth Car 3, UNA2-06): the only arithmetic any peek print
 * may use — a counter that ticks itself can disagree with the
 * lease, and a print that can disagree with the truth it names
 * is a lie waiting for its frame. Null = no live lease (no row,
 * no end, or the end has passed): the peek is over.
 */
internal fun peekSecondsRemaining(leaseEndMono: Long, nowMono: Long): Int? {
    val remainingMs = leaseEndMono - nowMono
    if (remainingMs <= 0L) return null
    return ((remainingMs + 999L) / 1000L).toInt()
}

data class DisarmChallengeState(
    val isVisible: Boolean = false,
    val requiredHoldSeconds: Int = 60,
    // Audit R3b #147: the stopwatch floor, carried into the valve
    // overlay so it can state the wait-to-free path instead of
    // presenting the token as the only door. These two are FACTS
    // about the session at open time (its mode, its banked
    // distance to the floor) — not a token count; the count rule
    // below stands untouched.
    val sessionIsStopwatch: Boolean = false,
    val remainingFloorSeconds: Int = 0
    // No token count lives here (TOKEN-SPEC §5, Amendment A): a count
    // copied into this state at open time is a snapshot that can lie
    // for the visit's whole life. The overlay derives its count from
    // the repository flow (weeklyEmergencyTokensRemaining below) for
    // as long as it is visible.
)

/**
 * Issue #141: one sled tier commit, staged for its undo window.
 * A rocker mistap used to be instant and permanent; the commit now
 * leaves this record on the Shield banner ("'X' SET TO OPEN — UNDO")
 * for a few seconds, and [MonolithViewModel.undoLastTierChange]
 * restores [fromTier]. In-memory only, by design: the window is
 * seconds long and the change it reverses is the one the hand just
 * made — a process death inside the window loses the undo, never
 * the ruling itself.
 */
data class TierChangeForUndo(
    val packageName: String,
    val appName: String,
    val fromTier: AppBlockTier,
    val toTier: AppBlockTier
)

class MonolithViewModel(
    private val appContext: Context,
    private val repository: MonolithRepository,
    val soundEngine: TactileSoundEngine
) : ViewModel() {

    private val _currentDestination = MutableStateFlow(MonolithDestination.DECK)
    val currentDestination: StateFlow<MonolithDestination> = _currentDestination.asStateFlow()

    // The manual theme is DEAD (chrome C4): _darkTheme and
    // toggleTheme() are deleted with the DAY/NIGHT pill. The theme
    // is an evaluation — see themeEvaluation below (declared after
    // activeSession, which it reads).

    private val _dialMinutes = MutableStateFlow(50)
    val dialMinutes: StateFlow<Int> = _dialMinutes.asStateFlow()

    private val _studyBlocksCount = MutableStateFlow(2)
    val studyBlocksCount: StateFlow<Int> = _studyBlocksCount.asStateFlow()

    private val _studyPreset = MutableStateFlow(StudyModePreset.POMODORO_25_5)
    val studyPreset: StateFlow<StudyModePreset> = _studyPreset.asStateFlow()

    // Issue #124 (typing-intention): the intention draft is born
    // BLANK and is filled only by the user's own typing through
    // setIntentionText. It used to be born pre-filled with a
    // factory default ("Finish Chapter 4 Problem Set") and
    // silently overwritten by every task operation — words put in
    // the user's mouth and later read back as their declaration.
    // Sarthak's ruling (Oct 30, 08:55): the intention is his own
    // editable words, or no name at all — never fixed buckets,
    // never app-assigned. Task machinery still seeds the DIAL from
    // the active task (seedDialFromTask); it never touches these
    // words again.
    private val _intentionText = MutableStateFlow("")
    val intentionText: StateFlow<String> = _intentionText.asStateFlow()

    private val _shortsReelsSniperEnabled = MutableStateFlow(true)
    val shortsReelsSniperEnabled: StateFlow<Boolean> = _shortsReelsSniperEnabled.asStateFlow()

    private val _browserUrlBlockerEnabled = MutableStateFlow(true)
    val browserUrlBlockerEnabled: StateFlow<Boolean> = _browserUrlBlockerEnabled.asStateFlow()

    /** The user's real routines (persisted; fired by RoutineAlarmReceiver). */
    val routines: StateFlow<List<com.example.data.RoutineEntity>> = repository.allRoutinesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** The user's own blocked-websites list (persisted; the service enforces THIS). */
    val blockedSites: StateFlow<List<String>> = repository.allSitesFlow
        .map { sites -> sites.map { it.domain } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // The pre-flight check is DELETED (Slice D): its modal's
    // "COMMIT & ARM ANYWAY" path was the register's LR2-S1-18 —
    // the slide reads the live warrant itself now, in the frame,
    // and refuses on a dead wire. No gate stands before the arm.

    private val _isAlwaysOnDeskClockVisible = MutableStateFlow(false)
    val isAlwaysOnDeskClockVisible: StateFlow<Boolean> = _isAlwaysOnDeskClockVisible.asStateFlow()

    private val _burnInFigure8MinuteStep = MutableStateFlow(0)
    val burnInFigure8MinuteStep: StateFlow<Int> = _burnInFigure8MinuteStep.asStateFlow()

    private val _floatingPeekPill = MutableStateFlow(FloatingPeekPillState())
    val floatingPeekPill: StateFlow<FloatingPeekPillState> = _floatingPeekPill.asStateFlow()

    private val _frozenDigest = MutableStateFlow<FrozenDigest?>(null)
    val frozenDigest: StateFlow<FrozenDigest?> = _frozenDigest.asStateFlow()

    private val _showNotificationDigestSheet = MutableStateFlow(false)
    val showNotificationDigestSheet: StateFlow<Boolean> = _showNotificationDigestSheet.asStateFlow()



    // Truth Car 3 (UNA-03): the printed state is born from the
    // persisted fact itself, not from whichever engine this VM
    // was handed — the print and the fact are one read, so PARK
    // cannot open on a value the machinery doesn't hold.
    private val _soundEnabled = MutableStateFlow(SoundEffectsSetting.isEnabled(appContext))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _deletedTaskForUndo = MutableStateFlow<FocusTaskEntity?>(null)
    val deletedTaskForUndo: StateFlow<FocusTaskEntity?> = _deletedTaskForUndo.asStateFlow()
    private var undoDismissJob: Job? = null

    // Truth Car 2 (G-03): the undo window's restore copy is
    // persisted — process death inside the five seconds must not
    // destroy the only copy of a delete the UI called reversible.
    private val undoDeleteStore = com.example.data.UndoDeleteStore(appContext)

    /**
     * The weekly emergency-break count, DERIVED from the repository's
     * spend ledger (TOKEN-SPEC §§2/5). The old bare in-memory
     * MutableStateFlow(1) is deleted: it was never persisted, so any
     * process death silently refilled the week's break, and the VM
     * decremented it while disarming regardless of the count. The
     * repository is the law now; this flow is only its rendering.
     */
    val weeklyEmergencyTokensRemaining: StateFlow<Int> =
        repository.observeEmergencyBreaksRemaining()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    private val _ratchetWarningMessage = MutableStateFlow<String?>(null)
    val ratchetWarningMessage: StateFlow<String?> = _ratchetWarningMessage.asStateFlow()
    private var ratchetBannerJob: Job? = null

    // Issue #141: the last out-of-session sled commit, staged for
    // its undo window (see TierChangeForUndo). Mirrors the
    // task-delete undo's shape: a StateFlow the Shield renders plus
    // a dismiss job that closes the window.
    private val _tierChangeForUndo = MutableStateFlow<TierChangeForUndo?>(null)
    val tierChangeForUndo: StateFlow<TierChangeForUndo?> = _tierChangeForUndo.asStateFlow()
    private var tierUndoDismissJob: Job? = null

    private fun triggerRatchetWarning(msg: String) {
        // Slice E (E4 §7.9 census): the completion pulse is the
        // finish's reserved voice — the ratchet warning's spend
        // of it dies here. The warning is a print; it sounds
        // nothing.
        // Issue #140: the print used to live 3.6 seconds — a user
        // who looked up a second too late got a tap that silently
        // did nothing. Ten seconds is a readable sentence's life.

        _ratchetWarningMessage.value = msg
        ratchetBannerJob?.cancel()
        ratchetBannerJob = viewModelScope.launch {
            delay(10_000L)
            _ratchetWarningMessage.value = null
        }
    }

    /**
     * Issue #140: the session's end as the door itself derives it
     * (the span law — whole-span remaining, never the per-block
     * remainder). " (ENDS 18:00)" for a timed session, "" for a
     * stopwatch or no session: a ratchet sentence may only promise
     * what the model knows. The old sentence promised 00:00, which
     * was false in both directions.
     */
    private fun sessionEndsSuffix(): String {
        val session = activeSession.value ?: return ""
        if (session.isStopwatchMode) return ""
        val endsAt = System.currentTimeMillis() + session.spanRemainingSeconds * 1000L
        return " (ENDS " + ClockText.timeOfDay(appContext, endsAt) + ")"
    }

    val tasks: StateFlow<List<FocusTaskEntity>> = repository.allTasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTask: StateFlow<FocusTaskEntity?> = tasks.map { list ->
        list.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
            ?: list.firstOrNull { it.resolvedStatus == TaskQueueStatus.QUEUED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val queuedTasks: StateFlow<List<FocusTaskEntity>> = combine(tasks, activeTask) { list, currentActive ->
        list.filter {
            it.resolvedStatus == TaskQueueStatus.QUEUED && it.id != currentActive?.id
        }.sortedWith(compareBy<FocusTaskEntity> { it.queueOrder }.thenBy { it.id })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTasks: StateFlow<List<FocusTaskEntity>> = tasks.map { list ->
        list.filter { it.resolvedStatus == TaskQueueStatus.COMPLETED }
            .sortedByDescending { it.completedAtMillis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deferredTasks: StateFlow<List<FocusTaskEntity>> = tasks.map { list ->
        list.filter { it.resolvedStatus == TaskQueueStatus.DEFERRED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Issue #28 (finding 5): the total combines BOTH sources —
    // the old tasks-only map read _dialMinutes.value as a stale
    // snapshot, so turning the dial alone never recomputed the
    // total until an unrelated task emission happened.
    val totalQueueMinutes: StateFlow<Int> = combine(tasks, _dialMinutes) { list, dial ->
        list.filter {
            it.resolvedStatus == TaskQueueStatus.ACTIVE || it.resolvedStatus == TaskQueueStatus.QUEUED
        }.sumOf { it.estimatedMinutes }.coerceAtLeast(dial)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 50)

    // Issue #28: an empty queue is an empty checklist. The old
    // code invented three hard-coded tasks ("Finish Chapter 4
    // Problem Set", "Synthesize Lecture 9…", "Debug Dynamic
    // Programming…") whenever the real list was empty — fabricated
    // work presented as his. Now the map is the truth, only.
    val checklist: StateFlow<List<StudyChecklistItem>> = tasks.map { list ->
        list.map { task ->
            StudyChecklistItem(
                id = task.id,
                title = task.title,
                isDone = task.resolvedStatus == TaskQueueStatus.COMPLETED
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _selectedProfileName = MutableStateFlow("Study Loadout")
    val selectedProfileName: StateFlow<String> = _selectedProfileName.asStateFlow()

    // Issue #139 F4: applying a profile rewrites the dial and every
    // per-app tier in one tap. The dial/name from before the tap are
    // kept here (the tiers themselves are snapshotted by the
    // repository) so one undo restores the setup he had by hand.
    private var preProfileDialMinutes: Int? = null
    private var preProfileName: String? = null
    private val _profileUndoAvailable = MutableStateFlow(false)
    val profileUndoAvailable: StateFlow<Boolean> = _profileUndoAvailable.asStateFlow()

    // UNA-07 (Truth Car 3): the Shield LOADOUT cap rests on the
    // stored fact of his last HAND pick (LoadoutStore), never on a
    // composable's remember. Seeded from the store at birth; written
    // only by applySmartLoadout below (the hand path — a routine
    // applying its own loadout does not move his selector).
    private val loadoutStore = com.example.data.LoadoutStore(appContext)
    private val _loadoutCapIndex = MutableStateFlow(loadoutStore.capIndex())
    val loadoutCapIndex: StateFlow<Int> = _loadoutCapIndex.asStateFlow()

    // Audit R1 #40: the receipt dismissal is persisted — the
    // host's remember{} alone let process death resurrect a
    // receipt he had already put away. MainActivity seeds its
    // gate from here and writes every dismissal through.
    private val receiptDismissalStore =
        com.example.data.ReceiptDismissalStore(appContext)

    fun dismissedReceiptSessionId(): Int =
        receiptDismissalStore.dismissedSessionId()

    fun persistReceiptDismissal(sessionId: Int) {
        receiptDismissalStore.dismiss(sessionId)
        // Issue #143: the slip has been read and put away — the
        // finish's night hold retires with it.
        _finishNightHold.value = false
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<AppCategory?>(null)
    val selectedCategoryFilter: StateFlow<AppCategory?> = _selectedCategoryFilter.asStateFlow()

    private var airlockInstanceSeq: Long = 0L
    /** Issue #99 F3: the intercept instance whose dishonest exit
     *  (Home/Recents while the Airlock stood) was last logged — one
     *  trace per standing Airlock, never a duplicate per pause. */
    private var airlockAbandonLoggedInstanceId: Long = 0L
    private val _airlockState = MutableStateFlow(AirlockInterceptState())
    val airlockState: StateFlow<AirlockInterceptState> = _airlockState.asStateFlow()

    private val _disarmChallenge = MutableStateFlow(DisarmChallengeState())
    val disarmChallenge: StateFlow<DisarmChallengeState> = _disarmChallenge.asStateFlow()

    private val _systemGuardStatus = MutableStateFlow(
        // Issue #28: the pre-check state must not invent facts —
        // integrity unproven (false), boot count and launcher
        // count unknown (0) until refreshSystemGuard measures.
        SystemGuardStatus(
            hasUsageStatsPermission = false,
            hasOverlayPermission = false,
            isAccessibilityServiceEnabled = false,
            isNotificationListenerEnabled = false,
            isDeviceAdminEnabled = false,
            isMonotonicClockIntact = false,
            isHmacSignatureValid = false,
            clockDriftSeconds = 0L,
            systemBootCount = 0,
            installedLauncherAppsCount = 0
        )
    )
    val systemGuardStatus: StateFlow<SystemGuardStatus> = _systemGuardStatus.asStateFlow()

    /** The prune note (Mikro M5): the record of what the catalog
     *  reconcile took off the app list, loaded once after the
     *  seed-sync has run. Dismissed by the first row tap anywhere
     *  (the tier commits below) or by a tap on the strip itself —
     *  it prints once, in the one-time-strip grammar, and is gone. */
    private val _pruneNote = MutableStateFlow<PruneNote?>(null)
    val pruneNote: StateFlow<PruneNote?> = _pruneNote.asStateFlow()

    fun dismissPruneNote() {
        if (_pruneNote.value != null) {
            _pruneNote.value = null
        }
        repository.clearPruneNote()
    }

    /** Issue #129 F1: the review prompt's content — the names of
     *  apps the category guess birthed OPEN that the man has not
     *  ruled on yet. There is NO dismiss: the list empties only
     *  when a ruling lands on each app (the repository clears the
     *  record at the tier commit), so the prompt cannot be tapped
     *  away into the same silence the audit found. Refreshed after
     *  the seed-sync and after every tier commit in this VM. */
    private val _unreviewedOpenNames = MutableStateFlow<List<String>>(emptyList())
    val unreviewedOpenNames: StateFlow<List<String>> = _unreviewedOpenNames.asStateFlow()

    fun refreshUnreviewedOpen() {
        viewModelScope.launch {
            _unreviewedOpenNames.value = repository.unreviewedOpenApps().map { it.appName }
        }
    }

    val apps: StateFlow<List<BlockedAppEntity>> = repository.allAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredApps: StateFlow<List<BlockedAppEntity>> = combine(
        apps,
        _searchQuery,
        _selectedCategoryFilter
    ) { allApps, query, category ->
        allApps.filter { app ->
            val matchesCategory = category == null || app.category == category.name
            appMatchesQuery(app, query) && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profiles: StateFlow<List<FocusProfileEntity>> = repository.allProfilesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSession: StateFlow<FocusSessionEntity?> = repository.activeSessionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sessionHistory: StateFlow<List<FocusSessionEntity>> = repository.sessionHistoryFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Issue #135 F2: the most recent session that claimed a receipt
     *  № (history is start-DESC) — the Deck's LAST RECEIPT door
     *  stands on this, even after arming cleared the standing slip. */
    val lastReceiptSessionId: StateFlow<Int?> = sessionHistory.map { rows ->
        rows.firstOrNull { it.receiptNumber > 0 }?.id
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val interceptLogs: StateFlow<List<InterceptLogEntity>> = repository.interceptLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Issue #143: the finish + ground flows are declared BEFORE
    // the theme evaluation on purpose — its combine reads them
    // (below), and a flow declared under the combine is still
    // null when the first transform can run (the Slice E lesson;
    // see the note at _receiptState further down).
    private val _finishState = MutableStateFlow<FinishState?>(null)
    val finishState: StateFlow<FinishState?> = _finishState.asStateFlow()

    // Issue #143 (closure pass, STURM-4): the night hold after a
    // finish. Set ONLY at a witnessed E0 whose session was lived
    // in night (latched below, while the session ran — by E0 the
    // theme has already flipped, so the fact must be captured
    // during the session, never re-derived after it). It holds
    // the night through the detention AND the receipt's reading
    // (the finish state itself retires at the rack's end, long
    // before the slip is dismissed), and clears when the receipt
    // is dismissed or the next session arms. A DAY session's
    // finish never sets it — no night blast in reverse.
    private val _finishNightHold = MutableStateFlow(false)
    private var nightLatchedSessionId: Int? = null
    private var nightLatchedForSession: Boolean = false

    /** F-Z1 (a) — the VM mirror of the shot's ground step, and
     *  F23's pin in the same state: SEALED_PRE_GROUND is the
     *  400ms where the scheme holds its day values while the
     *  orange exits; GROUNDED is the step itself. */
    enum class ShotGround { NOT_SEALED, SEALED_PRE_GROUND, GROUNDED }

    private val _shotGround = MutableStateFlow(ShotGround.NOT_SEALED)
    val shotGround: StateFlow<ShotGround> = _shotGround.asStateFlow()
    private var shotGroundJob: Job? = null

    // ------------------------------------------------------------------
    // THE THEME EVALUATION (chrome C4). One evaluation, two readers:
    // the scheme (through darkTheme) and PARK's printed readout.
    // Re-derived on a 1-second stamp so a boundary swaps the scheme
    // within a second of its frame. Issue #143: session-night is
    // stepped by the GROUND (never at the row's birth, never
    // during proving — the t=400ms pin MainActivity/Theme.kt
    // always claimed), and a finish being lived through HOLDS
    // the night until it clears, so the detention and the
    // receipt are not spent under a day blast. The Deck and
    // receipt surfaces animate the colour step (F6).
    // (Audit R3b #116: the old note here boasted "a 0ms cut, never
    // a fade" — that cut, fired when a session/SLEEP window ended
    // at night, floodlit a half-asleep user. Clock night in the
    // evaluation now holds the night scheme until 06:00, so no
    // boundary inside the night can cut to light at all.)
    // ------------------------------------------------------------------
    private val _themeNow = MutableStateFlow(System.currentTimeMillis())

    val themeEvaluation: StateFlow<ThemeEvaluation> = combine(
        routines,
        activeSession,
        _themeNow,
        _shotGround,
        _finishNightHold
    ) { routineList, session, now, ground, finishHold ->
        // The system signal, read live at each derivation: the
        // phone's own dark mode is a night witness beside the clock.
        val systemNight = (appContext.resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        evaluateTheme(
            session?.isActive == true && ground == ShotGround.GROUNDED,
            routineList,
            now,
            systemNight = systemNight,
            finishHeld = finishHold
        )
    }.stateIn(
        viewModelScope, SharingStarted.Eagerly,
        ThemeEvaluation(false, "AUTO · DAY — NO ROUTINE OWNS IT")
    )

    /** The scheme's night flag — DERIVED from the evaluation, never
     *  stored, never toggled. (The session-active disjunct the host
     *  used to compose by hand is inside the evaluation now.) */
    val darkTheme: StateFlow<Boolean> = themeEvaluation
        .map { it.isNight }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /** The Watch Store's published snapshot (chrome C3): PARK's
     *  board and the header's Guard line derive from it. */
    val watchSnapshots: StateFlow<com.example.service.WatchSnapshot> =
        com.example.service.WatchStore.snapshots

    /** Otis's heartbeat, exposed for the strip and the chrome line. */
    val wireSnapshots: StateFlow<com.example.service.WatchSnapshot> = com.example.service.WatchStore.snapshots

    val capturedNotifications: StateFlow<List<CapturedNotificationEntity>> = repository.capturedNotificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalFocusedMinutes: StateFlow<Int> = combine(
        sessionHistory,
        activeSession
    ) { history, active ->
        // D7/A3 exclusions, same as compute7DayHistory: unfinalised
        // routine rows are worth zero; legacy pre-rebuild routine rows
        // (recognised by prefix + zero window snapshot) never count.
        val completedSecs = history.filter { s ->
            !s.isActive && when {
                s.routineId != null && s.routineOutcome == null -> false
                s.routineId == null && s.windowStartMillis == 0L &&
                    s.profileName.startsWith("Routine: ") -> false
                else -> true
            }
        }.sumOf { it.elapsedSeconds }
        val activeSecs = active?.elapsedSeconds ?: 0
        // Issue #28: the real sum, nothing else. The old
        // coerceAtLeast(35) floor and the stateIn initial of 75
        // printed focus minutes nobody ever studied.
        (completedSecs + activeSecs) / 60
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /**
     * The reset-note strip state (Freya §3.3; R3b #161): shown after
     * the reseed until HE dismisses it by tapping the note — edits
     * no longer erase it and it no longer ages out in silence.
     * Fresh installs never see it (reseed flag and dismiss flag are
     * both set at seed time); they get the fresh-start note instead.
     */
    val showRoutineResetNote: StateFlow<Boolean> = combine(
        repository.guardSettingsFlow,
        routines
    ) { settings, _ ->
        settings != null && settings.routineReseedV1Done && !settings.routineReseedNoteDismissed
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Install-day disclosures (R3b #161): prefs-backed one-time
    // notes, read at init; registerInstallOpen compares this
    // build's version against the last version this phone opened.
    private val _freshStartNoteVisible = MutableStateFlow(false)
    val freshStartNoteVisible: StateFlow<Boolean> = _freshStartNoteVisible
    private val _updateNoteVisible = MutableStateFlow(false)
    val updateNoteVisible: StateFlow<Boolean> = _updateNoteVisible

    fun dismissFreshStartNote() {
        repository.dismissFreshStartNote()
        _freshStartNoteVisible.value = false
    }

    fun dismissUpdateNote() {
        repository.dismissUpdateNote()
        _updateNoteVisible.value = false
    }

    fun dismissRoutineResetNote() {
        viewModelScope.launch { repository.dismissRoutineResetNote() }
    }

    val totalInterceptions: StateFlow<Int> = interceptLogs.combine(apps) { logs, appList ->
        val fromApps = appList.sumOf { it.interceptCount }
        maxOf(logs.size, fromApps)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

    /**
     * The strip's APPS cell count (Uma, Slice D): LOCKED + PEEK_2M —
     * the same number the imprint prints, in every tense (F18). An
     * Int? on purpose: null is UNKNOWN (no Room emission yet), never
     * a fabricated 0 — the shipped apps flow's emptyList() initial
     * must not feed this derivation. Eagerly, so a subscription lapse
     * can never reset a known count back to UNKNOWN on re-entry.
     * Room invalidation is the only recompute trigger: no timer, no
     * poll, no guard sweep anywhere in this cell's path.
     */
    val appsCellCount: StateFlow<Int?> = repository.allAppsFlow
        .map { all -> all.count { it.resolvedTier != AppBlockTier.OPEN } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /**
     * The honest SHUT/PEEK split behind the summed count (Truth
     * Car 3, UNA2-02): appsCellCount's sum is the Deck's skeleton
     * number (F20 slate, kept), but every PRINTED count — the
     * imprint tenant above all — reads this split and names its
     * parts, the way the Shield already does. Null = UNKNOWN,
     * same contract as the count.
     */
    data class AppsSplit(val shut: Int, val peek: Int)

    val appsSplit: StateFlow<AppsSplit?> = repository.allAppsFlow
        .map { all ->
            AppsSplit(
                shut = all.count { it.resolvedTier == AppBlockTier.LOCKED },
                peek = all.count { it.resolvedTier == AppBlockTier.PEEK_2M }
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Otis's heartbeat, exposed for the strip and the chrome line. */

    /**
     * The standing Live Wire verdict for the Deck's print (the strip,
     * the sentence slot, the imprint) — refreshed on every heartbeat
     * emission, on Deck entry, and on resume (Suki's hand-back sweep
     * triggers). Null until the first read in this process: the
     * imprint's PENDING silence, never a healthy count printed on a
     * store nobody has read. The slide itself never consumes this
     * flow — it reads armWarrantNow() synchronously, in the frame.
     */
    private val _deckWarrant = MutableStateFlow<Warrant?>(null)
    val deckWarrant: StateFlow<Warrant?> = _deckWarrant.asStateFlow()

    fun refreshDeckWarrant() {
        _deckWarrant.value = repository.standingWarrant()
    }

    /** The synchronous verdict the slide reads at DOWN, at the gate,
     *  on every MOVE, and at the latch frame (Ines: ≤5ms, no wait). */
    fun armWarrantNow(): Warrant = repository.standingWarrant()

    /** The Watch Store's grant-check sweep (chrome C3): six queries
     *  dispatched in parallel; each item re-stamps as its own query
     *  lands. Triggers: Deck entry, Deck resume, FIX return. */
    fun runWatchSweep() {
        com.example.service.WatchStore.runSweep { item ->
            repository.watchGrantQuery(item)
        }
    }

    /** PARK's first-run bit (chrome C4, §5.7). */
    fun parkIntroPending(): Boolean = repository.parkIntroPending()
    fun markParkIntroDone() = repository.markParkIntroDone()

    /** Issue #70 F3: the Deck primer's first-run bit. */
    fun deckPrimerPending(): Boolean = repository.deckPrimerPending()
    fun markDeckPrimerDone() = repository.markDeckPrimerDone()

    /**
     * The seal freeze (Emil §3.2): the APPS count and the wire
     * verdict captured at the session's SEAL — at VM scope, so the
     * sealed strip and imprint print the seal-act facts for the
     * session's whole life and a tab switch can never re-capture
     * them from the live flow (review E-1/E-2). For routine and
     * restored sessions the seal act IS the birth, and the capture
     * happens at the first emission. For a cascade arm, birth is
     * NOT the seal (Step 4 severed them: the row is born active at
     * insert so L2's witness survives, and t=0 can land up to
     * 1500ms later on the jam-retry path, across apps-table writes
     * the cascade itself fires at L1) — so the freeze is
     * RE-CAPTURED at the SealedRunning transition, from the DAO
     * snapshot, superseding the birth capture (Emil, cascade
     * review E-11). The permanent form is the persisted
     * heldAppsAtSeal column (F15, Migration 9→10, Slice E); this
     * in-memory capture covers every session born in this process,
     * by hand or by routine.
     */
    data class SealFreeze(
        val sessionId: Int,
        val count: Int?,
        /** The seal-instant SHUT/PEEK split of [count] (Truth Car 3,
         *  UNA2-02): the frozen tenant prints the split captured at
         *  the seal, never a live re-read — one tense, the seal's. */
        val shutCount: Int? = null,
        val peekCount: Int? = null,
        val warrantState: WarrantState,
        /** The header's Guard line, captured at the seal frame
         *  (chrome C2): the band freezes at this print for the
         *  session's whole life. */
        val guardLine: com.example.ui.components.GuardLinePrint? = null
    )

    private val _sealFreeze = MutableStateFlow<SealFreeze?>(null)
    val sealFreeze: StateFlow<SealFreeze?> = _sealFreeze.asStateFlow()

    /**
     * The struck record (Emil, cascade review E-12): the id of the
     * session whose Strike has fired — or whose seal this process
     * never presented, and which therefore stands settled — held
     * at VM scope beside the freeze. It lived in the imprint's
     * rememberSaveable, but strikeKey is a LEVEL (derived from
     * isSessionRunning), not a pulse from t=0: any cold first
     * composition after the seal — relaunch after the saved bundle
     * is discarded, a routine arm first seen on a later Deck visit
     * — replayed the Strike on a seal hours old. In-memory VM
     * scope is the honest scope: a fresh process loses this with
     * its bundle, and the restore path seeds it struck for any
     * session it did not watch seal (see the init collectors).
     */
    private val _struckSessionId = MutableStateFlow<Int?>(null)
    val struckSessionId: StateFlow<Int?> = _struckSessionId.asStateFlow()

    /** The Deck's imprint reports its Strike here, at the seal
     *  frame it fires on. */
    fun markSessionStruck(sessionId: Int) {
        _struckSessionId.value = sessionId
    }

    /** The header's Guard line (chrome C2): derived from the Watch
     *  Store. The seal freeze is the band's fallback for the
     *  UNMEASURED case only — a live measured print outranks the
     *  captured one in both directions, so a permission revoked
     *  mid-session drops the band off its frozen `GUARD 4/4` the
     *  moment the store measures the loss (Audit R3 #113,
     *  finding 2; resolveGuardLinePrint carries the law). */
    val guardLinePrint: StateFlow<com.example.ui.components.GuardLinePrint?> = combine(
        com.example.service.WatchStore.snapshots,
        _sealFreeze,
        activeSession
    ) { snap, freeze, session ->
        com.example.ui.components.resolveGuardLinePrint(
            sessionActive = session?.isActive == true,
            frozen = freeze?.guardLine,
            live = com.example.ui.components.guardLineFor(
                snap, android.os.SystemClock.elapsedRealtime()
            )
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // =========================================================================
    // THE CASCADE (Slice D step 4): the arm's proving controller.
    // The slide's latch enters here; the controller dispatches the
    // batch, proves the three links against their witnesses, and
    // declares t=0 — re-anchoring the row, firing the seal sound,
    // and un-gating the Deck's running rendering in that order.
    // The seal sound's only arm-path call is the controller's, at
    // t=0. Jam haptics as filed (Opal §F4): the seat-knock voice
    // at 20ms @ 1.0, three strikes at 90ms spacing.
    // =========================================================================

    /** Slice E (F-S1(i)): the one seal transition — the Deck's
     *  cascade and the routine heartbeat seal through this same
     *  instance, so a seal is a seal whichever door armed it. */
    private val sealTransition = com.example.service.SessionSealTransitions.get(
        appContext, soundEngine, repository
    )

    private val armProving = ArmProvingController(
        scope = viewModelScope,
        port = RepositoryArmSessionPort(repository, appContext),
        witnesses = LiveArmWitnesses,
        transition = sealTransition,
        playJamHaptics = {
            viewModelScope.launch {
                repeat(3) { strike ->
                    soundEngine.playSeatKnock(20, 1.0f, 320f)
                    if (strike < 2) delay(90)
                }
            }
        }
    )

    val armPhase: StateFlow<ArmPhase> = armProving.phase
    val armSentence: StateFlow<com.example.ui.components.RefusalSentence?> = armProving.armSentence
    val armAnnulCount: StateFlow<Int> = armProving.annulCount

    // Slice E state, declared BEFORE init on purpose: init starts
    // the finish controller, the ground driver, and the recovery,
    // and their collectors may run on the first frame — a flow
    // declared below init is still null at that moment (the
    // harness caught exactly that NPE). The data classes and the
    // machines themselves live further down; state lives here.
    // (_finishState and _shotGround moved ABOVE the theme
    // evaluation under Issue #143 — its combine reads them.)
    private val _receiptState = MutableStateFlow<com.example.data.ReceiptState?>(null)
    val receiptState: StateFlow<com.example.data.ReceiptState?> = _receiptState.asStateFlow()

    /** Issue #157: the Vault receipts index — every claimed
     *  session's row-proven facts, re-read with the Drawer. */
    private val _receiptIndex = MutableStateFlow<List<com.example.data.ReceiptIndexEntry>>(emptyList())
    val receiptIndex: StateFlow<List<com.example.data.ReceiptIndexEntry>> = _receiptIndex.asStateFlow()

    /** Issue #157: a past slip re-presented from the Vault index.
     *  Independent of the standing receipt and its dismissal flag:
     *  it lives until its own scrim dismisses it, and dismissing
     *  it touches nothing else. */
    private val _presentedReceipt = MutableStateFlow<com.example.data.ReceiptState?>(null)
    val presentedReceipt: StateFlow<com.example.data.ReceiptState?> = _presentedReceipt.asStateFlow()

    /** Re-derive the slip for [sessionId] from its stored row and
     *  present it — the same ReceiptState the sheet always renders,
     *  rebuilt by the repository's rehydration machinery. */
    fun presentReceipt(sessionId: Int) {
        viewModelScope.launch {
            _presentedReceipt.value =
                runCatching { repository.rehydrateReceipt(sessionId) }
                    .onFailure { MLog.w("rehydrateReceipt($sessionId) failed; receipt not presented", it) }
                    .getOrNull()
        }
    }

    fun dismissPresentedReceipt() {
        _presentedReceipt.value = null
    }

    /** Schlitten's gate: the sled return's once-only law, pure
     *  and unit-tested. The VM owns its state law; the Deck host
     *  owns the firing. */
    internal val sledReturnGate = SledReturnGate()
    private var sledExpiryJob: Job? = null

    // Issue #28 (finding 4a): this set is touched from the
    // sessionHistory collector, onFinishE0's launched coroutines
    // and the public publishReceipt facade — a plain MutableSet
    // raced. The synchronized set makes every add/remove/read
    // atomic; the persisted claim latch remains the cross-process
    // guarantee, this is the in-process one.
    private val handledFinishIds: MutableSet<Int> =
        java.util.Collections.synchronizedSet(mutableSetOf())
    private var finishControllerStarted = false

    init {
        // Issue #139 F4: a profile-undo snapshot survives process
        // death in prefs; if one is on disk, the undo door stands.
        _profileUndoAvailable.value = repository.hasProfileUndoSnapshot()
        // Rehydrate the pre-tap dial + name from the same prefs
        // entry (issue #139 F4): without this, an undo after
        // process death restored the tiers but silently kept the
        // applied profile's dial and name.
        if (_profileUndoAvailable.value) {
            preProfileDialMinutes = repository.profileUndoDialMinutes()
            preProfileName = repository.profileUndoName()
        }
        // G-03 rehydration: a delete taken just before process
        // death left its restore copy on disk. If its five-second
        // window is still open, the banner returns with a working
        // UNDO for the remainder; if the window closed while the
        // process was dead, the snapshot is discarded — the delete
        // was booked at its tap and an expired reversal is not
        // revived by a relaunch.
        undoDeleteStore.load()?.let { (snapshot, deletedAtWall, deletedAtElapsed) ->
            // Audit R1 #41: judged on the monotonic stamp when
            // it survives (same boot) — a wall-clock rollback no
            // longer resurrects an expired undo, nor a forward
            // jump steals a live one.
            val remaining = com.example.data.UndoDeleteStore.remainingMillis(
                deletedAtWallMillis = deletedAtWall,
                deletedAtElapsedMillis = deletedAtElapsed,
                nowWallMillis = System.currentTimeMillis(),
                nowElapsedMillis = android.os.SystemClock.elapsedRealtime()
            )
            if (remaining > 0L) {
                _deletedTaskForUndo.value = snapshot
                undoDismissJob = viewModelScope.launch {
                    delay(remaining)
                    _deletedTaskForUndo.value = null
                    undoDeleteStore.clear()
                }
            } else {
                undoDeleteStore.clear()
            }
        }
        // Issue #83: reap any stale undo copy on this process
        // start as well — the rehydration above revives or drops
        // the snapshot for the banner, and expireIfStale is the
        // store-level guarantee that a copy whose window closed
        // while no process lived does not linger in prefs.
        com.example.data.UndoDeleteStore.expireIfStale(appContext)
        // Issue #143: latch "this session is being lived in night"
        // while it runs — E0 reads the latch, because by then the
        // evaluation has already flipped back to day.
        viewModelScope.launch {
            combine(activeSession, themeEvaluation) { session, theme ->
                session to theme
            }.collect { (session, theme) ->
                if (session?.isActive == true) {
                    if (nightLatchedSessionId != session.id) {
                        nightLatchedSessionId = session.id
                        nightLatchedForSession = false
                    }
                    if (theme.isNight) nightLatchedForSession = true
                }
            }
        }
        _freshStartNoteVisible.value = repository.freshStartNotePending()
        _updateNoteVisible.value = repository.registerInstallOpen() || repository.updateNotePending()
        viewModelScope.launch {
            repository.ensureSeededAndSyncPackages()
            _pruneNote.value = repository.pendingPruneNote()
            refreshUnreviewedOpen()
            // Routines rebuild: no per-routine alarm arming — one
            // forced reconcile derives the truth and rearms the chain.
            com.example.service.RoutineHeartbeat.tick(
                appContext, com.example.service.RoutineHeartbeat.Source.RESUME, force = true
            )
            _browserUrlBlockerEnabled.value = repository.isBrowserBlockerEnabled()
            // Truth Car 3 (UNA-01): the sniper switch is seeded
            // from its persisted row, like the browser pair — the
            // flow is a print of the store, never the store.
            _shortsReelsSniperEnabled.value = repository.isShortsReelsSniperEnabled()
            refreshSystemGuard()
        }
        // Issue #152: the Drawer's current month follows the
        // traveller. _drawerMonth is born at ViewModel creation
        // and was otherwise re-derived only when the VAULT opens,
        // so an app left open across a landing kept printing the
        // departure month as current indefinitely (a timezone
        // change re-anchors every filing date). This slow ticker
        // re-derives it — only while the user has not pinned a
        // month by browsing; refreshDrawer() clears the pin.
        viewModelScope.launch {
            while (true) {
                delay(60_000L)
                if (!drawerMonthPinnedByUser) {
                    val current = com.example.data.DrawerDay.filingYearMonth(
                        System.currentTimeMillis()
                    )
                    if (current != _drawerMonth.value) {
                        refreshDrawer()
                        // Composed with #158: refreshDrawer now
                        // defaults the month only before any
                        // selection, so a traveller whose month was
                        // selected by an earlier refresh would stay
                        // stranded — the ticker moves the month
                        // itself in that case, then clears its own
                        // pin (this move is the default view, not
                        // a user's browse).
                        if (_drawerMonth.value != current) {
                            selectDrawerMonth(current.first, current.second)
                            drawerMonthPinnedByUser = false
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            activeTask.collect { task ->
                if (task != null) {
                    // Issue #124: the dial still seeds from the
                    // active task; the intention draft does NOT —
                    // it is the user's own words or blank, never a
                    // live mirror of the queue.
                    seedDialFromTask(task)
                }
                refreshActiveTaskCarryover(task)
            }
        }
        // Issue #135 F1: the arm setup (dial, loadout/profile, block
        // count, intention) lived only in this VM's memory — a
        // process death during the break silently re-armed session 2
        // with factory parameters. When NO session is active, restore
        // the setup from the most recent finished session's persisted
        // row (it stores profileName, targetDurationSeconds,
        // totalStudyBlocks, intention). Routine rows are skipped:
        // their profile/duration are the window's, not his hand's.
        // A FLOW (stopwatch) row's stored target is its floor, not
        // the dial, so the dial is left alone for those.
        viewModelScope.launch {
            if (repository.activeSessionSnapshot() == null) {
                repository.lastFinishedSessionSnapshot()?.let { last ->
                    if (last.routineId == null) {
                        if (!last.isStopwatchMode) {
                            _dialMinutes.value =
                                (last.targetDurationSeconds / 60).coerceIn(5, 180)
                        }
                        _studyBlocksCount.value = last.totalStudyBlocks.coerceIn(1, 8)
                        _selectedProfileName.value = last.profileName
                        _intentionText.value = last.intention
                    }
                }
            }
        }
        // The Watch Store's emissions are a bus, never a verdict:
        // every emission re-reads the Deck's standing warrant
        // through the one derivation (Joss: two derivations of one
        // fact will disagree).
        viewModelScope.launch {
            com.example.service.WatchStore.snapshots.collect {
                refreshDeckWarrant()
            }
        }
        // The cascade's phase relaxes when the sealed row goes.
        viewModelScope.launch {
            activeSession.collect { session ->
                if (session == null) {
                    armProving.onActiveSessionGone()
                }
            }
        }
        // The seal freeze: capture at the session's birth, once —
        // and, for cascade arms, RE-capture at the seal itself
        // (the collector below), because birth is not the seal
        // under proving (E-11). The same first-sighting seeds the
        // struck record for any session this process did not watch
        // seal (E-12): a session first seen while no cascade
        // attempt is in flight and no SealedRunning names it was
        // born by routine or restored from disk — its seal was
        // never presented here, so it stands settled and the
        // Strike never replays for it.
        viewModelScope.launch {
            activeSession.collect { session ->
                // The Watch Store's suspension input: the Safe Break
                // is his suspension of the blocking interceptors.
                // Issue #109, F1: the same flow carries the session-
                // active fact the NOTIFICATIONS line now requires —
                // the listener captures only while a session is
                // active, so the board may print RUNNING only then.
                com.example.service.WatchStore.updateInterceptorInputs(
                    safeBreak = session?.isInSafeBreak == true,
                    sessionActive = session?.isActive == true
                )
                if (session == null) {
                    _sealFreeze.value = null
                } else if (_sealFreeze.value?.sessionId != session.id) {
                    _sealFreeze.value = SealFreeze(
                        sessionId = session.id,
                        // Slice E (Emil E-2's persisted form):
                        // the row's own heldAppsAtSeal is the
                        // count of record; the live cell count is
                        // the fallback for a row born before the
                        // column existed (-1 = uncaptured).
                        count = session.heldAppsAtSeal.takeIf { it >= 0 }
                            ?: appsCellCount.value,
                        shutCount = apps.value.count { it.resolvedTier == AppBlockTier.LOCKED },
                        peekCount = apps.value.count { it.resolvedTier == AppBlockTier.PEEK_2M },
                        warrantState = repository.standingWarrant().state,
                        guardLine = com.example.ui.components.guardLineFor(
                            com.example.service.WatchStore.snapshots.value,
                            android.os.SystemClock.elapsedRealtime()
                        )
                    )
                    val phaseNow = armProving.phase.value
                    val watchedSeal = when (phaseNow) {
                        is ArmPhase.Proving, is ArmPhase.Jammed -> true
                        is ArmPhase.SealedRunning -> phaseNow.sessionId == session.id
                        ArmPhase.Idle -> false
                    }
                    if (!watchedSeal && _struckSessionId.value != session.id) {
                        _struckSessionId.value = session.id
                    }
                }
            }
        }
        // E-11: the cascade's seal re-captures the freeze at t=0,
        // from the DAO snapshot — the set the shield actually
        // holds at the seal, including any tier write that landed
        // inside the proving window (the SESSION_EDGE reconcile
        // the cascade fires at L1 among them), and the wire
        // verdict as it stands on the seal's own frame.
        viewModelScope.launch {
            var lastSealedId: Int? = null
            armProving.phase.collect { phase ->
                if (phase is ArmPhase.SealedRunning && phase.sessionId != lastSealedId) {
                    lastSealedId = phase.sessionId
                    val apps = runCatching { repository.allAppsFlow.first() }.getOrNull()
                    // The snapshot read is async: if the sealed
                    // session has already ended and another taken
                    // its place, this capture is stale — drop it
                    // rather than overwrite the live freeze.
                    if (activeSession.value?.id == phase.sessionId) {
                        _sealFreeze.value = SealFreeze(
                            sessionId = phase.sessionId,
                            count = activeSession.value?.heldAppsAtSeal?.takeIf { it >= 0 }
                                ?: apps?.let { repository.countHeldApps(it) },
                            shutCount = apps?.count { it.resolvedTier == AppBlockTier.LOCKED },
                            peekCount = apps?.count { it.resolvedTier == AppBlockTier.PEEK_2M },
                            warrantState = repository.standingWarrant().state,
                            guardLine = com.example.ui.components.guardLineFor(
                                com.example.service.WatchStore.snapshots.value,
                                android.os.SystemClock.elapsedRealtime()
                            )
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            combine(
                _shortsReelsSniperEnabled,
                _browserUrlBlockerEnabled
            ) { shorts, sites -> shorts to sites }.collect { (shorts, sites) ->
                com.example.service.WatchStore.updateInterceptorInputs(
                    shortsOn = shorts,
                    sitesOn = sites
                )
            }
        }
        // THE DIGEST WATCH: while the digest sheet is visible under
        // an active session, the sealed snapshot (re)mints whenever
        // the session the holder carries changes — a routine arming
        // under an open live sheet (RoutineAlarmReceiver fires with
        // no UI), or a new session replacing one that ended under an
        // open sheet — AND whenever a new notification is held
        // (issue #118, finding 3: the sealed digest refreshes what it
        // shows; it still opens and deletes nothing). Transitions to
        // inactive are ignored on purpose: the holder latches past
        // the session's end until dismissed unsealed.
        viewModelScope.launch {
            combine(activeSession, showNotificationDigestSheet, capturedNotifications) { session, visible, held ->
                Triple(session, visible, held)
            }.collect { (session, visible, held) ->
                val holder = _frozenDigest.value
                // A remint is owed when the holder belongs to another
                // session, or when the live held list carries an item
                // the holder has never shown (a new arrival). Ids, not
                // list equality: the snapshot is uncapped, the flow is
                // capped, and the two must not chase each other.
                val owesMint = holder == null || holder.sessionId != session?.id ||
                    held.any { item -> holder.items.none { it.id == item.id } }
                if (visible && session != null && session.isActive && owesMint) {
                    mintFrozenDigest(session.id)
                }
            }
        }
        // Issue #114 finding 1: the peek pill finally gets a face.
        // The overlay window (over the peeked app) and the in-app
        // Compose pill both render THIS state; the overlay's END
        // button routes back to the one hand-back verb, so state,
        // lease row and faces retire together. Before this wiring
        // the state was collected nowhere and the pill never drew.
        PeekPillOverlay.onEndPeekRequest = { end2MinutePeekEarly() }
        viewModelScope.launch {
            _floatingPeekPill.collect { pill ->
                if (pill.isActive) {
                    PeekPillOverlay.showOrUpdate(
                        appContext, pill.appName, pill.secondsRemaining
                    )
                } else {
                    PeekPillOverlay.hide(appContext)
                }
            }
        }
        // ST-03 (Truth Car 3): the break boundaries speak. A single
        startTickerLoop()
        // Slice E: the finish machine, the shot-ground driver,
        // and the birth recovery (rehydration + F-K1 + Draht).
        startFinishController()
        startShotGroundDriver()
        runSliceERecovery()
    }

    /** Take a snapshot for [sessionId] and hold it. The write is
     *  guarded: if the session changed again while the read was in
     *  flight, the stale mint is dropped and the digest watch will
     *  mint for the session that is actually active. Reminting for
     *  the SAME session is the refresh path (issue #118, finding 3):
     *  it replaces the holder's rows and stamp with the held world
     *  as it stands now. */
    private fun mintFrozenDigest(sessionId: Int) {
        viewModelScope.launch {
            val items = repository.capturedNotificationsSnapshot()
            if (activeSession.value?.id == sessionId && activeSession.value?.isActive == true) {
                _frozenDigest.value = FrozenDigest(
                    items = items,
                    frozenAtMillis = System.currentTimeMillis(),
                    sessionId = sessionId
                )
            }
        }
    }

    private fun startTickerLoop() {
        var tickSecondCounter = 0
        viewModelScope.launch {
            while (isActive) {
                tickSecondCounter++
                // The theme evaluation's clock stamp (chrome C4):
                // a routine boundary re-derives the evaluation —
                // and with it the scheme — within a second of its
                // frame. Inside clock night the evaluation holds
                // NIGHT (see evaluateTheme step 3), so a boundary
                // at night changes the readout's owner, never the
                // scheme: no floodlight cut in a dark room.
                _themeNow.value = System.currentTimeMillis()
                // Shift Always-On Desk Clock figure-8 coordinates every 60 seconds to prevent OLED burn-in
                if (tickSecondCounter % 60 == 0) {
                    _burnInFigure8MinuteStep.value = (_burnInFigure8MinuteStep.value + 1) % 360
                }

                val current = activeSession.value
                if (current != null && current.isActive) {
                    // Slice E (Nocke §2.2): the gap latch is
                    // checked BEFORE progress — a shield dead past
                    // the 10s law is latched as evidence before
                    // this tick's own write can bury it. The flip
                    // itself is judged by the finish machine off
                    // the history Flow (E0): the ticker's old flip
                    // branch — its seal-voice spend on a
                    // completion, its stats, its desk-clock hide —
                    // is CUT (E4); the finish machine owns all
                    // three now.
                    repository.latchShieldGapIfStale(SystemClock.elapsedRealtime())
                    repository.updateSessionProgress(current)
                }

                // The peek pill's remaining time is DERIVED from the
                // Room lease, never counted (Truth Car 3, UNA2-06):
                // the row's activePeekUntilElapsedRealtime is the
                // one end every reader — this state, Pastille's
                // pill face, the services — agrees on. The old
                // ticker decremented its own counter: a second
                // clock that process death reset while the lease
                // lived on. Row gone or end passed retires the
                // state through the same end the pill's hand-back
                // uses, so state, row and face retire together.
                val peek = _floatingPeekPill.value
                if (peek.isActive) {
                    val nowMono = SystemClock.elapsedRealtime()
                    val leaseEnd = apps.value
                        .find { it.packageName == peek.packageName }
                        ?.activePeekUntilElapsedRealtime ?: 0L
                    val nextSec = peekSecondsRemaining(leaseEnd, nowMono)
                    if (nextSec == null) {
                        expirePeekAndEvict(peek)
                    } else {
                        if (nextSec % 15 == 0 && nextSec != peek.secondsRemaining) {
                            soundEngine.playRotaryDetent(760.0)
                        }
                        if (nextSec != peek.secondsRemaining) {
                            _floatingPeekPill.value = peek.copy(secondsRemaining = nextSec)
                        }
                    }
                }

                // The Deck warrant's freshness law (Truth Car 3,
                // UNA2-03): a warrant older than the print threshold
                // is re-read on this per-second beat, so no printed
                // warrant rides unbounded age. Each refresh re-stamps
                // readMono, making the check self-limiting — at most
                // one re-read per threshold window.
                val warrantNow = _deckWarrant.value
                if (warrantNow == null ||
                    com.example.service.WatchStore.warrantStaleForPrint(
                        warrantNow, SystemClock.elapsedRealtime()
                    )
                ) {
                    refreshDeckWarrant()
                }

                delay(1000L)
            }
        }
    }

    // =====================================================================
    // SLICE E — THE FINISH MACHINE (E4: Nocke's release proof,
    // Zettel's receipt, Draht's retry, Schlitten's sled gate) AND
    // THE SHOT GROUND (F-Z1's VM mirror + F23's scheme pin, one
    // state: the ground steps at t0+400, and that step IS the
    // pin's release).
    // =====================================================================

    /** The finish the Deck is living through: E0's identity and
     *  the detention's progress. Phases are DERIVED from e0Mono
     *  by the Deck (derive-don't-replay); this state carries only
     *  what cannot be derived — the kind, the fault, the №. */
    data class FinishState(
        val sessionId: Int,
        val kind: String,
        val e0Mono: Long,
        val faulted: Boolean = false,
        val issued: Boolean = false,
        val receiptNumber: Int? = null,
        /** The held frame's frozen readout (Nocke §3.2): the
         *  count-up total for a FLOW finish, the remaining count
         *  for a timed one — frozen at E0, never re-derived from
         *  a ticker that has already stopped. */
        val frozenDisplaySeconds: Int = 0,
        /** The ended row itself, for the frozen progress print. */
        val frozenElapsedSeconds: Int = 0,
        val frozenTargetSeconds: Int = 0,
        val frozenIsStopwatch: Boolean = false
    )

    /** The seal's ground driver: on the SealedRunning edge the
     *  ground holds pre-step; at t0Mono + 400 it steps, once.
     *  A session this process did not watch seal (routine-born,
     *  restored) stands GROUNDED from its first sighting — no
     *  deferral theatre for a shot nobody was present for. */
    private fun startShotGroundDriver() {
        viewModelScope.launch {
            armProving.phase.collect { phase ->
                if (phase is ArmPhase.SealedRunning) {
                    _shotGround.value = ShotGround.SEALED_PRE_GROUND
                    shotGroundJob?.cancel()
                    val t0 = phase.t0Mono
                    shotGroundJob = viewModelScope.launch {
                        val remaining = (t0 + 400L) - SystemClock.elapsedRealtime()
                        if (remaining > 0L) delay(remaining)
                        if (_shotGround.value == ShotGround.SEALED_PRE_GROUND) {
                            _shotGround.value = ShotGround.GROUNDED
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            activeSession.collect { session ->
                if (session == null) {
                    shotGroundJob?.cancel()
                    _shotGround.value = ShotGround.NOT_SEALED
                } else if (_shotGround.value == ShotGround.NOT_SEALED &&
                    armProving.phase.value !is ArmPhase.SealedRunning &&
                    armProving.phase.value !is ArmPhase.Proving &&
                    armProving.phase.value !is ArmPhase.Jammed
                ) {
                    _shotGround.value = ShotGround.GROUNDED
                }
            }
        }
    }

    /** F-K1 recovery + the standing receipt's rehydration, once,
     *  at this process's birth: an active row no live attempt
     *  owns goes to the transition's recovery; a published
     *  receipt re-renders settled (Z6 case b — publishReceipt is
     *  NOT called for it, and E6 never arms from it). */
    private fun runSliceERecovery() {
        viewModelScope.launch {
            runCatching { repository.rehydrateReceipt() }
                .onFailure { MLog.w("Slice-E receipt rehydrate failed", it) }
                .getOrNull()?.let { restored ->
                if (_receiptState.value == null) _receiptState.value = restored
            }
            val active = runCatching { repository.activeSessionSnapshot() }
                .onFailure { MLog.w("Slice-E active-session snapshot failed; recovery skipped this launch", it) }
                .getOrNull()
            if (active != null && armProving.phase.value == ArmPhase.Idle) {
                // Issue #126 F1: gate the Deck on the recovery's
                // verdict for exactly the rows recover() will
                // re-prove (marker absent + young — recover's own
                // forks in SessionSealTransition, mirrored here
                // only to decide the gate; recover() remains the
                // judge and re-derives both facts itself). A
                // marker-sealed or older-build row adopts without
                // ever entering PROVING, so a lawful relaunch of a
                // running session renders settled, unchanged.
                // Truth (issue #76): the runCatching used to discard
                // its Result — if recovery itself threw, nothing was
                // logged, surfaced, or remembered. Failures now speak
                // (the onFailure below), and the verdict still
                // reaches the #126 gate.
                val nowWall = System.currentTimeMillis()
                val needsProof = repository.sealedSessionIdFromMarker() != active.id &&
                    nowWall - active.startWallTimeMillis <
                    com.example.service.SessionSealTransitionImpl.RECOVERY_YOUNG_WINDOW_MS
                if (needsProof) armProving.beginRecovery(active.id)
                val verdict = runCatching {
                    sealTransition.recover(
                        active.id, active.startWallTimeMillis, nowWall
                    )
                }.onFailure {
                    Log.e("MonolithVM", "Slice-E recovery failed for session ${active.id}", it)
                }.getOrNull()
                if (needsProof) armProving.resolveRecovery(active.id, verdict)
            }
            // Draht: a faulted finish from a previous process
            // resumes its cadence from the row's own stamps.
            runCatching {
                com.example.service.ReleaseRetryScheduler.runDueRetry(appContext)
            }.onFailure {
                Log.e("MonolithVM", "runDueRetry failed at process birth", it)
            }
        }
    }

    /** Draht's third trigger: entering the Deck retries a
     *  faulted release whose cadence is due. */
    fun onDeckOpenRetry() {
        viewModelScope.launch {
            runCatching {
                com.example.service.ReleaseRetryScheduler.runDueRetry(appContext)
            }.onFailure {
                // Truth (issue #76): the Deck-open retry used to be a
                // silent no-op when the retry itself threw — pressing
                // it again reproduced the same invisible failure.
                Log.e("MonolithVM", "runDueRetry failed on Deck-open retry", it)
            }
        }
    }

    private fun isSleepLoadout(routineId: Int?): Boolean {
        if (routineId == null) return false
        val routine = routines.value.firstOrNull { it.id == routineId } ?: return false
        return routine.loadoutName == com.example.data.SmartLoadout.SLEEP.name
    }

    /** A row's derived end, wall clock — branch U's honesty
     *  bound reads it: routine rows end at their window's end;
     *  manual rows at start + the span they actually lived. */
    private fun derivedEndWall(row: com.example.data.FocusSessionEntity): Long =
        if (row.routineId != null && row.windowEndMillis > 0L) {
            row.windowEndMillis
        } else {
            row.startWallTimeMillis + row.spanElapsedSeconds * 1000L
        }

    /**
     * The finish controller (E4): sessionHistory is the witness.
     * Every emission re-evaluates the terminal predicate on
     * recent rows (finalise stamps land AFTER the isActive flip,
     * so a row can become a finish on a later emission than the
     * flip itself). A finish handled once is handled forever
     * (in-memory set + the persisted claim latch).
     *
     * First-sighting rows are branch U: foregrounded ∧ within
     * 1800ms of the derived end ⇒ the witnessed path with
     * E0 := discovery; older ⇒ claim + issue at discovery, no
     * held frame, no pulse, at ANY age. The 10-minute discard
     * this replaces (Audit R3 #113, finding 3) threw the finish
     * away whole — no receipt, no notice, and the handled mark
     * barred the publishReceipt facade from ever recovering it.
     * Issuing late fabricates nothing: the slip's facts are the
     * row's own (its stamped end, its sealed elapsed), and the
     * № is minted at discovery, which is when it truthfully
     * came into being.
     */
    private fun startFinishController() {
        if (finishControllerStarted) return
        finishControllerStarted = true
        val seenIds = mutableSetOf<Int>()
        viewModelScope.launch {
            sessionHistory.collect { rows ->
                val nowWall = System.currentTimeMillis()
                for (row in rows) {
                    val firstSighting = row.id !in seenIds
                    seenIds += row.id
                    if (row.id in handledFinishIds) continue
                    val kind = com.example.data.finishKindOf(
                        row, isSleepLoadout(row.routineId), nowWall
                    ) ?: continue
                    if (firstSighting) {
                        val endWall = derivedEndWall(row)
                        val ageMs = nowWall - endWall
                        when {
                            ageMs <= 1_800L -> onFinishE0(row, kind, witnessed = true)
                            else -> onFinishE0(row, kind, witnessed = false)
                        }
                    } else {
                        onFinishE0(row, kind, witnessed = true)
                    }
                }
            }
        }
    }

    /** E0, the finish's zero: the collector's handling frame.
     *  The claim comes FIRST (Nocke §3 — before any animation);
     *  then the one commanded link (the shield's stop) is
     *  commanded; P is the frame MonolithShieldService.instance
     *  reads null; the receipt issues on Detention's formula,
     *  at E1800 exactly on the faulted branch. */
    private fun onFinishE0(
        row: com.example.data.FocusSessionEntity,
        kind: String,
        witnessed: Boolean
    ) {
        handledFinishIds += row.id
        val sessionId = row.id
        // Issue #143: a witnessed finish of a night-lived session
        // holds the night from this frame until the receipt is
        // dismissed — detention and reading both stay in night.
        if (witnessed && nightLatchedSessionId == sessionId && nightLatchedForSession) {
            _finishNightHold.value = true
        }
        val e0Mono = SystemClock.elapsedRealtime()
        val e0Wall = System.currentTimeMillis()
        if (activeSession.value?.id == sessionId) {
            // Zeile's coarse ended edge: the desk clock's hide
            // lives here now, not in the ticker.
            _isAlwaysOnDeskClockVisible.value = false
        }
        viewModelScope.launch {
            val pending = repository.assemblePendingReceipt(sessionId, kind, e0Wall)
            if (pending == null) {
                handledFinishIds -= sessionId
                return@launch
            }
            if (!witnessed) {
                // Branch U, late: claim + issue at discovery. No
                // held frame, no pulse, no detention — the
                // receipt simply stands when he next looks.
                val receipt = repository.publishReceipt(pending, faulted = false)
                if (receipt != null) emitReceipt(receipt)
                return@launch
            }

            _finishState.value = FinishState(
                sessionId = sessionId,
                kind = kind,
                e0Mono = e0Mono,
                frozenDisplaySeconds = if (row.isStopwatchMode) {
                    row.spanElapsedSeconds
                } else {
                    (row.spanTargetSeconds - row.spanElapsedSeconds).coerceAtLeast(0)
                },
                frozenElapsedSeconds = row.spanElapsedSeconds,
                frozenTargetSeconds = row.spanTargetSeconds,
                frozenIsStopwatch = row.isStopwatchMode
            )
            // The reverse cascade's ONE commanded link.
            com.example.service.MonolithShieldService.stopShield(appContext)

            // The E-clock's sounds, fired against e0Mono by this
            // job — a Deck that isn't composed doesn't mute the
            // room (Nocke §3). E0 settle knock; E700 pulse (the
            // completion voice's only lawful spend); E880 knock
            // pair. Death-booked: the E700 slot stands EMPTY.
            val pulses = com.example.data.finishPulses(kind, row)
            launch {
                // Issue #150: the whole cascade is timer-fired
                // against e0Mono — unattended voices, so Do Not
                // Disturb binds them in the engine (the ringer's
                // word already binds every voice).
                soundEngine.playSeatKnock(24, 1.0f, 320f, unattended = true)
                if (pulses) {
                    val toPulse = 700L - (SystemClock.elapsedRealtime() - e0Mono)
                    if (toPulse > 0L) delay(toPulse)
                    if (SystemClock.elapsedRealtime() - e0Mono < 900L) {
                        soundEngine.playCompletionPulse(unattended = true)
                    }
                }
                val toPair = 880L - (SystemClock.elapsedRealtime() - e0Mono)
                if (toPair > 0L) delay(toPair)
                soundEngine.playSeatKnock(24, 1.0f, 320f, unattended = true)
                delay(90L)
                soundEngine.playSeatKnock(12, 0.5f, 320f, unattended = true)
            }

            var pawlLiftMs: Long? = null
            var halted = false
            var issuedReceipt: com.example.data.ReceiptState? = null
            // Issue #28 (finding 4b): the pawl lift is waited on
            // REACTIVELY — a child suspends on the shield's
            // instance-presence flow and stamps the offset when it
            // flips. The old loop busy-polled `instance == null`
            // on every 25ms detention tick. (The 25ms cadence
            // below remains, but it now only advances the
            // detention's own issue arithmetic, not a spin-check
            // on another component's field.)
            val pawlWait = launch {
                com.example.service.MonolithShieldService.instancePresent
                    .first { present -> !present }
                if (pawlLiftMs == null) {
                    pawlLiftMs = SystemClock.elapsedRealtime() - e0Mono
                }
            }
            while (true) {
                val offset = SystemClock.elapsedRealtime() - e0Mono
                val issueAt = com.example.data.Detention.issueOffsetMs(pawlLiftMs, halted, offset)
                if (issueAt != null && offset >= issueAt) {
                    val faulted = com.example.data.Detention.isFaulted(pawlLiftMs, halted, offset)
                    issuedReceipt = repository.publishReceipt(pending, faulted)
                    if (faulted) {
                        val faultWall = System.currentTimeMillis()
                        com.example.service.ReleaseRetryScheduler.scheduleNext(
                            appContext, faultWall + 30_000L
                        )
                    }
                    if (issuedReceipt != null) emitReceipt(issuedReceipt)
                    _finishState.value = _finishState.value?.copy(
                        faulted = faulted,
                        issued = true,
                        receiptNumber = issuedReceipt?.number
                    )
                    break
                }
                if (offset >= com.example.data.Detention.RECEIPT_CEILING_MS + 400L) {
                    halted = true
                    // The ceiling passed with no issue computed —
                    // issue now, faulted, rather than never.
                    issuedReceipt = repository.publishReceipt(pending, faulted = true)
                    if (issuedReceipt != null) emitReceipt(issuedReceipt)
                    _finishState.value = _finishState.value?.copy(
                        faulted = true, issued = true,
                        receiptNumber = issuedReceipt?.number
                    )
                    break
                }
                delay(25L)
            }
            pawlWait.cancel()
            // The detention's tail: the finish state stands until
            // the rack has completed (R0 + 280), then retires —
            // the receipt state carries the slip from here.
            val rackEnd = com.example.data.Detention.rackStartMs(pawlLiftMs) +
                com.example.data.Detention.RACK_AFTER_PAWL_MS
            val remain = rackEnd - (SystemClock.elapsedRealtime() - e0Mono)
            if (remain > 0L) delay(remain)
            if (_finishState.value?.sessionId == sessionId) {
                _finishState.value = null
            }
        }
    }

    /** The receipt becomes visible: the VM samples the mono
     *  INSIDE the emission (Schlitten C2) and arms the sled gate
     *  with its expiry. A restored receipt never passes through
     *  here (Z6). */
    private fun emitReceipt(receipt: com.example.data.ReceiptState) {
        if (_receiptState.value?.sessionId == receipt.sessionId &&
            _receiptState.value?.number == receipt.number
        ) {
            return // the first emission stands
        }
        val stamped = receipt.copy(
            receiptPublishedMonoMs = SystemClock.elapsedRealtime()
        )
        _receiptState.value = stamped
        sledReturnGate.arm(
            stamped.sessionId,
            deadlineMono = stamped.receiptPublishedMonoMs + 1000L
        )
        sledExpiryJob?.cancel()
        sledExpiryJob = viewModelScope.launch {
            delay(1050L)
            sledReturnGate.expire(stamped.sessionId)
        }
    }

    /**
     * Schlitten C1's facade — the receipt's ONLY entry point.
     * The discovery path (branch U / a door that finds a finish
     * with no controller running): derive the kind, assemble,
     * claim, issue — immediately, with no detention.
     */
    fun publishReceipt(sessionId: Int) {
        if (_receiptState.value?.sessionId == sessionId) return
        viewModelScope.launch {
            // Issue #135 F2 re-issue: a session that already claimed
            // its № rebuilds SETTLED from its row (repository
            // receiptForSession) — it never re-claims and never
            // re-enters the finish controller's handled set. This is
            // the door the Deck's LAST RECEIPT opens after the next
            // arm cleared the standing slip from memory.
            repository.receiptForSession(sessionId)?.let { reissued ->
                _receiptState.value = reissued
                return@launch
            }
            if (sessionId in handledFinishIds) return@launch
            val row = repository.sessionById(sessionId) ?: return@launch
            val kind = com.example.data.finishKindOf(
                row, isSleepLoadout(row.routineId), System.currentTimeMillis()
            ) ?: return@launch
            handledFinishIds += sessionId
            val pending = repository.assemblePendingReceipt(
                sessionId, kind, System.currentTimeMillis()
            ) ?: return@launch
            val receipt = repository.publishReceipt(pending, faulted = false)
            if (receipt != null) emitReceipt(receipt)
        }
    }

    /** The Deck host's gate doors (Schlitten §3.2/§3.3). */
    fun witnessSledReturn(sessionId: Int) = sledReturnGate.witness(sessionId)

    fun tryStartSledReturn(sessionId: Int): Boolean = sledReturnGate.tryStart(sessionId)


    /** The slide factory's fraction (Schlitten §2.3): seated
     *  while a session runs or a return is pending; home in
     *  every other world. */
    fun sledInitialFraction(sessionRunning: Boolean): Float =
        sledReturnGate.initialFraction(
            sessionRunning = sessionRunning,
            receipt = _receiptState.value?.let { SledReceiptMark(it.sessionId) },
            nowMono = SystemClock.elapsedRealtime()
        )

    /** Band's lock (E3 §4.7's other half): navigation is refused
     *  — dropped, never queued — while an arm is proving, while
     *  the seal shot runs (t0 → t0+1050), and while a finish
     *  detains the Deck (E0 → issuance). The refusal precedes the
     *  detent sound: a refused tap must not sound like travel. */
    fun navigationLocked(): Boolean {
        val phase = armProving.phase.value
        if (phase is ArmPhase.Proving || phase is ArmPhase.Jammed) return true
        if (phase is ArmPhase.SealedRunning &&
            SystemClock.elapsedRealtime() - phase.t0Mono < 1050L
        ) {
            return true
        }
        val finish = _finishState.value
        if (finish != null && !finish.issued) return true
        return false
    }

    private var lastNavRefusalMono = 0L

    fun navigateTo(destination: MonolithDestination) {
        if (destination != _currentDestination.value && navigationLocked()) {
            val now = SystemClock.elapsedRealtime()
            if (now - lastNavRefusalMono >= 250L) {
                lastNavRefusalMono = now
                // The refusal tick: tries and stays (Band's
                // grammar — a short knock at low amplitude, never
                // the detent that means travel happened).
                soundEngine.playSeatKnock(6, 0.2f, 320f)
            }
            return
        }
        if (destination != MonolithDestination.DECK) {
            // Schlitten §3.3: navigation away voids the sled
            // return (the receipt itself stands).
            _receiptState.value?.let { sledReturnGate.void(it.sessionId) }
        }
        soundEngine.playRotaryDetent(540.0)
        _currentDestination.value = destination
        if (destination == MonolithDestination.VAULT) {
            refreshDrawer()
        }
    }

    /** Truth Car 3 (UNA-03): the toggle writes the FACT —
     *  [SoundEffectsSetting.setEnabled] commits it and pushes it
     *  to every live engine (this VM's and the seal transition's
     *  alike, whichever door built them) — and the print follows
     *  the fact. It never writes an engine's var directly again:
     *  the per-engine copy is exactly how OFF once printed over a
     *  sounding routine seal. The confirmation detent fires only
     *  when turning ON, and by then the fact already says ON. */
    fun toggleSound(): Boolean {
        val next = !SoundEffectsSetting.isEnabled(appContext)
        // Issue #49: the print follows the DISK, not the attempt —
        // a failed commit leaves the flow on the persisted fact.
        val committed = SoundEffectsSetting.setEnabled(appContext, next)
        _soundEnabled.value = if (committed) next else SoundEffectsSetting.isEnabled(appContext)
        if (_soundEnabled.value) soundEngine.playRotaryDetent(720.0)
        return _soundEnabled.value
    }

    /** ST-02 (Truth Car 3): the Deck's END BREAK chip. Ends the
     *  Safe Break at the tap — the repository performs the same
     *  transition the expiry flip performs and books the actually
     *  taken seconds, so no record claims the untaken remainder
     *  ran. The boundary's voice is deliberately NOT fired here:
     *  it fires inside the repository's flip itself (ST-03), so
     *  a hand-ended break and an expired one speak identically,
     *  exactly once, whoever crossed the boundary. */
    fun endSafeBreakEarly() {
        val session = activeSession.value ?: return
        if (!session.isActive || !session.isInSafeBreak) return
        viewModelScope.launch {
            repository.endSafeBreakEarly(session.id)
        }
    }

    fun selectStudyPreset(preset: StudyModePreset) {
        soundEngine.playMechanicalLatch(true)
        _studyPreset.value = preset
        _dialMinutes.value = preset.workMinutes
        _studyBlocksCount.value = preset.defaultBlocks
    }

    fun setStudyBlocksCount(blocks: Int) {
        soundEngine.playRotaryDetent(640.0)
        _studyBlocksCount.value = blocks.coerceIn(1, 8)
    }

    fun setDialMinutes(minutes: Int) {
        // Issue #123 (finding 4): the wheel is the SINGLE voice for a
        // dial change — it already fired its detent on
        // DialPrecision's one pitch ladder before invoking this. This
        // setter used to fire a SECOND playRotaryDetent at an
        // unrelated pitch (440 + value x 2.8), so every detent
        // sounded twice and the ladder contradicted itself. It now
        // records the value in silence. Non-wheel callers (FLOW pill,
        // precise-entry steppers) sound their own single effect at
        // their own call sites, as they always have.
        val clamped = minutes.coerceIn(3, 180)
        if (clamped != _dialMinutes.value) {
            _dialMinutes.value = clamped
        }
    }

    fun setIntentionText(text: String) {
        // Issue #124: the one authored writer. Length discipline
        // only — capping per keystroke is safe, but trimming here
        // would eat the space between words mid-typing; the trim
        // happens once, at the arm snapshot in
        // MonolithRepository.startSession.
        _intentionText.value = text.take(MonolithRepository.MAX_INTENTION_CHARS)
    }

    fun toggleShortsReelsSniper() {
        // Truth Car 3 (UNA-01): the flip persists, exactly as the
        // browser toggle below — before this car the value died
        // with the process and PARK's board printed the corpse.
        val newValue = !_shortsReelsSniperEnabled.value
        soundEngine.playMechanicalLatch(newValue)
        _shortsReelsSniperEnabled.value = newValue
        viewModelScope.launch { repository.setShortsReelsSniperEnabled(newValue) }
    }

    fun toggleBrowserUrlBlocker() {
        val newValue = !_browserUrlBlockerEnabled.value
        soundEngine.playMechanicalLatch(newValue)
        _browserUrlBlockerEnabled.value = newValue
        viewModelScope.launch { repository.setBrowserBlockerEnabled(newValue) }
    }

    // Audit R6 #340 F13: the routine-conflict notice — set when a
    // save is HELD for collision, cleared by the user's own tap.
    private val _routineConflictNotice = MutableStateFlow<String?>(null)
    val routineConflictNotice: StateFlow<String?> = _routineConflictNotice.asStateFlow()

    fun dismissRoutineConflictNotice() {
        _routineConflictNotice.value = null
    }

    private fun persistRoutine(routine: com.example.data.RoutineEntity) {
        viewModelScope.launch {
            // Audit R6 #340 F13: conflict detection IN the save
            // path. An enabled routine whose window collides with
            // another enabled routine under a DIFFERENT loadout is
            // NOT saved silently — the save is held and the exact
            // collision is printed, so no routine is ever starved
            // in silence by RoutineDerive. The user's own edit
            // (window, days, or loadout) is the resolution; there
            // is no hidden precedence rule to discover at fire time.
            val conflicts = com.example.data.RoutineConflictDetector.conflictsFor(
                routine, repository.daoSnapshotRoutines()
            )
            if (conflicts.isNotEmpty()) {
                val c = conflicts.first()
                _routineConflictNotice.value =
                    "ROUTINE HELD — \"${routine.name}\" overlaps \"${c.otherName}\" " +
                    "by ${c.overlapMinutesPerWeek} min/week under a different loadout " +
                    "(${routine.loadoutName} vs ${c.otherLoadoutName}). " +
                    "The colliding save was not written. Change the window, " +
                    "the days, or the loadout."
                soundEngine.playFirmClick()
                return@launch
            }
            _routineConflictNotice.value = null
            // Every edit re-stamps the schedule, then reconciles
            // immediately (CRUD trigger). Issue #82 (clock-attacks
            // F4): the stamp is the HONEST now — stamping raw wall
            // time let a +30-day clock jump mint a future edit
            // horizon that suppressed every MISSED judgement for a
            // month. The reset note is NOT dismissed here (R3b
            // #161): obeying the note must not erase the record
            // that a reset happened — only the user's tap on the
            // note itself dismisses it.
            repository.updateRoutine(
                routine.copy(scheduleEditedAtMillis = repository.honestNowMillis())
            )
            com.example.service.RoutineHeartbeat.tick(
                appContext, com.example.service.RoutineHeartbeat.Source.CRUD, force = true
            )
        }
    }

    fun toggleRoutine(routine: com.example.data.RoutineEntity) {
        // Audit R5 #306 F2: a routine whose window is live is
        // forced-on. The bare flip let the person being blocked kill
        // the scheduled protection in one tap at exactly the moment
        // it started to bite; while the window runs, disabling is
        // refused (the row is state-driven, so it simply stays on).
        // Enabling is never refused, and a routine that is paused or
        // outside its window stays freely editable.
        if (routine.enabled &&
            com.example.data.RoutineSchedule.isEnforcingAt(routine, System.currentTimeMillis())
        ) {
            soundEngine.playMechanicalLatch(true)
            return
        }
        val updated = routine.copy(enabled = !routine.enabled)
        soundEngine.playMechanicalLatch(updated.enabled)
        persistRoutine(updated)
    }

    fun cycleRoutineLoadout(routine: com.example.data.RoutineEntity) {
        // Audit R1 #38: the seat order lives in ONE place —
        // LoadoutStore.CAP_ORDER — not re-hardcoded here.
        val order = com.example.data.LoadoutStore.CAP_ORDER
        val current = runCatching { SmartLoadout.valueOf(routine.loadoutName) }.getOrDefault(SmartLoadout.STUDY)
        val next = order[(order.indexOf(current) + 1) % order.size]
        soundEngine.playFirmClick()
        persistRoutine(routine.copy(loadoutName = next.name))
    }

    fun adjustRoutineTime(routine: com.example.data.RoutineEntity, isStart: Boolean, deltaMinutes: Int) {
        val start = if (isStart) {
            (routine.startMinutes + deltaMinutes + 1440) % 1440
        } else routine.startMinutes
        val end = if (isStart) routine.endMinutes else {
            (routine.endMinutes + deltaMinutes + 1440) % 1440
        }
        val candidate = routine.copy(startMinutes = start, endMinutes = end)
        val duration = com.example.data.RoutineSchedule.durationMinutes(candidate)
        val rawSpan = (end - start + 1440) % 1440
        // Keep windows sane: 30 minutes to 18 hours (the model clamp —
        // DAY BLOCKS runs 10.5 h and SLEEP 7.5 h of his real day).
        if (rawSpan !in 30..1080) return
        soundEngine.playRotaryDetent()
        persistRoutine(candidate)
    }

    fun toggleRoutineDay(routine: com.example.data.RoutineEntity, dayIndex: Int) {
        // Audit R5 #306 F2: removing TODAY from a routine whose
        // window is live is a disable by another name — refused
        // while the window runs, on the same forced-on rule as
        // toggleRoutine. Adding a day, or editing any other day,
        // stays free.
        val removing = routine.daysMask and (1 shl dayIndex) != 0
        if (removing) {
            val todayBit = (java.util.Calendar.getInstance()
                .get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
            if (dayIndex == todayBit &&
                com.example.data.RoutineSchedule.isEnforcingAt(routine, System.currentTimeMillis())
            ) {
                soundEngine.playRotaryDetent()
                return
            }
        }
        val mask = routine.daysMask xor (1 shl dayIndex)
        soundEngine.playRotaryDetent()
        persistRoutine(routine.copy(daysMask = mask))
    }

    /** #153 F2+F3: SKIP TODAY — the forgive button. Sets the
     *  pause-until to the end of today's (current or next) window:
     *  the window never materialises, judgement J6 never calls it
     *  a miss, and the weekly mask is NOT touched — every future
     *  same-weekday still runs. One tap, one day, forgiven. */
    fun skipRoutineToday(routine: com.example.data.RoutineEntity) {
        val now = System.currentTimeMillis()
        val until = com.example.data.RoutineSchedule.skipWindowEndMillis(routine, now) ?: return
        if (until <= now) return
        soundEngine.playMechanicalLatch(false)
        persistRoutine(routine.copy(pausedUntilMillis = until))
    }

    /** #153 F2: undo a pause — the routine runs on its mask again. */
    fun resumeRoutine(routine: com.example.data.RoutineEntity) {
        if (routine.pausedUntilMillis == 0L) return
        soundEngine.playMechanicalLatch(true)
        persistRoutine(routine.copy(pausedUntilMillis = 0L))
    }

    /** #153 F4: add a routine — a plain new block he then shapes
     *  with the same row controls (time, days, loadout, name). */
    fun addRoutine() {
        viewModelScope.launch {
            repository.addRoutine(
                com.example.data.RoutineEntity(
                    name = "NEW BLOCK",
                    startMinutes = 540,
                    endMinutes = 660,
                    daysMask = com.example.data.RoutineSchedule.ALL_DAYS,
                    loadoutName = com.example.data.SmartLoadout.STUDY.name,
                    enabled = true
                )
            )
            repository.dismissRoutineResetNote()
            com.example.service.RoutineHeartbeat.tick(
                appContext, com.example.service.RoutineHeartbeat.Source.CRUD, force = true
            )
        }
    }

    /** #153 F4: delete a routine. The heartbeat's orphan pass
     *  finalises any of its sessions by their stored windows. */
    fun deleteRoutine(routine: com.example.data.RoutineEntity) {
        soundEngine.playMechanicalLatch(false)
        viewModelScope.launch {
            repository.deleteRoutine(routine.id)
            repository.dismissRoutineResetNote()
            com.example.service.RoutineHeartbeat.tick(
                appContext, com.example.service.RoutineHeartbeat.Source.CRUD, force = true
            )
        }
    }

    /** #153 F4: rename a routine — the name is his word, not a
     *  seed's. Blank names are refused (a nameless block is a bug). */
    fun renameRoutine(routine: com.example.data.RoutineEntity, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isEmpty() || trimmed == routine.name) return
        soundEngine.playFirmClick()
        persistRoutine(routine.copy(name = trimmed.uppercase()))
    }

    /** #153 F5: apply a drafted window in ONE persist — the draft
     *  lives in the row's UI state; nothing reaches the engine
     *  until APPLY. Out-of-clamp spans are refused whole (the row
     *  shows the clamp note; a silent no-op is a lie). */
    fun setRoutineTime(
        routine: com.example.data.RoutineEntity,
        startMinutes: Int,
        endMinutes: Int
    ): Boolean {
        val candidate = routine.copy(startMinutes = startMinutes, endMinutes = endMinutes)
        val rawSpan = (endMinutes - startMinutes + 1440) % 1440
        if (rawSpan !in 30..1080) return false
        soundEngine.playRotaryDetent()
        persistRoutine(candidate)
        return true
    }

    fun addBlockedSite(raw: String) {
        viewModelScope.launch {
            if (repository.addBlockedSite(raw)) soundEngine.playMechanicalLatch(true)
        }
    }

    fun removeBlockedSite(domain: String) {
        // Issue #151 (friend-holds-the-phone): the site list now
        // carries the same one-way ratchet as the app tiers.
        // Removing a site WEAKENS the seal — the accessibility
        // service re-reads the list per event, so the domain
        // stopped matching inside the very session that was
        // supposed to be sealed — and anyone holding the phone
        // could do it: no session check existed on this verb,
        // unlike tiers (setAppBlockTier) and daily limits. During
        // an active session the verb refuses with the ratchet's
        // own warning. Adding a site only tightens, and stays
        // lawful mid-session, exactly as a tightening tier does.
        if (activeSession.value?.isActive == true) {
            triggerRatchetWarning("ONE-WAY RATCHET ENGAGED • CANNOT UNBLOCK '${domain.uppercase()}' DURING ACTIVE VAULT")
            return
        }
        soundEngine.playMechanicalLatch(false)
        viewModelScope.launch { repository.removeBlockedSite(domain) }
    }

    fun toggleAlwaysOnDeskClock(visible: Boolean) {
        soundEngine.playMechanicalLatch(visible)
        _isAlwaysOnDeskClockVisible.value = visible
    }

    fun toggleNotificationDigestSheet(visible: Boolean) {
        soundEngine.playMechanicalLatch(visible)
        if (visible) {
            val session = activeSession.value
            if (session != null && session.isActive) {
                // Sealed open: ALWAYS remint (issue #118, finding 3)
                // — reopening the sheet shows the held world as it
                // stands at this open, including everything held
                // since the last look; the old reuse-the-holder path
                // made reopening show the same frozen rows forever.
                mintFrozenDigest(session.id)
            } else {
                // Unsealed open is live mode; no holder may linger.
                _frozenDigest.value = null
            }
        } else {
            // Dismiss clears the freeze ONLY when unsealed. A dismiss
            // while sealed keeps the holder for the session's next
            // open; a freeze latched past the session's end clears
            // here, on the first unsealed dismiss.
            if (activeSession.value?.isActive != true) {
                _frozenDigest.value = null
            }
        }
        _showNotificationDigestSheet.value = visible
    }

    fun clearAllCapturedNotifications() {
        viewModelScope.launch {
            // The repository refuses while sealed; a sealed attempt
            // is SILENT — no latch sound rewards it.
            if (repository.clearAllCapturedNotifications()) {
                soundEngine.playMechanicalLatch(false)
            }
            // Issue #83: the same erasure clears the intercept
            // behaviour log — it had no production delete path at
            // all, so a "clear" that left it standing was not an
            // erasure.
            repository.clearInterceptLogs()
        }
    }

    fun deleteCapturedNotification(id: Int) {
        viewModelScope.launch {
            if (repository.deleteCapturedNotification(id)) {
                soundEngine.playRotaryDetent(520.0)
            }
        }
    }


    fun toggleChecklistItem(id: Int) {
        completeTaskAndAdvance(id)
    }

    fun addChecklistItem(title: String) {
        // MINUTES DIE: the checklist add asks nothing about
        // duration either — a task is a name (0 = no estimate).
        addTaskToQueue(title = title, estimatedMinutes = 0, categoryTag = "Study")
    }

    /** MINUTES DIE (Truth Car 2): a task carrying an estimate
     *  seeds the wheel from it, as it always has; a task with NO
     *  estimate (0 — every task created since Sarthak's order)
     *  never touches the wheel. Session length lives on the
     *  wheel, set by his hand — it is not inherited from a task. */
    private fun seedDialFromTask(task: FocusTaskEntity) {
        if (task.estimatedMinutes > 0 && activeSession.value?.isActive != true) {
            _dialMinutes.value = task.estimatedMinutes.coerceIn(5, 180)
        }
    }

    // =========================================================================
    // SOLAR QUEUE (TIMEBOXED TASK QUEUE) ACTIONS
    // =========================================================================

    fun addTaskToQueue(
        title: String,
        // Issue #32 F1: default 0 = no estimate given (was 25).
        estimatedMinutes: Int = 0,
        categoryTag: String = "Study",
        notesOrSubsteps: String = "",
        promoteToActive: Boolean = false
    ) {
        val clean = title.trim()
        if (clean.isEmpty()) return
        soundEngine.playRotaryDetent(680.0)
        viewModelScope.launch {
            val created = repository.addTaskToQueue(
                title = clean,
                estimatedMinutes = estimatedMinutes,
                categoryTag = categoryTag,
                notesOrSubsteps = notesOrSubsteps,
                promoteToActive = promoteToActive
            )
            if (created.resolvedStatus == TaskQueueStatus.ACTIVE) {
                seedDialFromTask(created)
            }
        }
    }

    fun editTask(
        taskId: Int,
        newTitle: String,
        newEstimatedMinutes: Int,
        newCategoryTag: String = "Study",
        newNotes: String = ""
    ) {
        val clean = newTitle.trim()
        if (clean.isEmpty()) return
        soundEngine.playMechanicalLatch(true)
        viewModelScope.launch {
            val updated = repository.updateTaskDetails(
                taskId = taskId,
                newTitle = clean,
                newEstimatedMinutes = newEstimatedMinutes,
                newCategoryTag = newCategoryTag,
                newNotes = newNotes
            )
            if (updated != null && updated.resolvedStatus == TaskQueueStatus.ACTIVE) {
                seedDialFromTask(updated)
            }
        }
    }

    fun completeTaskAndAdvance(taskId: Int) {
        // No sound here (merge ruling on Linea's flag): the seal is
        // the SESSION sound and was misspent on a task act - and it
        // fired before the completion even ran. The tear's
        // completion pulse in the view is the sole owner of this act.
        viewModelScope.launch {
            val nextActive = repository.completeTaskAndAdvance(taskId)
            if (nextActive != null) {
                seedDialFromTask(nextActive)
            }
        }
    }

    /**
     * REOPEN for the Tasks screen's Last-Finished Line — the
     * explicit un-complete verb (Register 3: completeTaskAndAdvance
     * is completion-only; it does NOT toggle). Adopts the returned
     * active task exactly as a completion does, minus the seal
     * sound — undoing a tap is not a finish.
     */
    fun reopenTask(taskId: Int) {
        viewModelScope.launch {
            val nextActive = repository.reopenStoredTask(taskId)
            if (nextActive != null) {
                seedDialFromTask(nextActive)
            }
            refreshDrawer()
        }
    }

    /** ESR-02's window (Hale's S1): while a seal is PROVING, the
     *  mid-seal verbs are absent — a verb fired into the proving
     *  window would rewrite the session the machine is sealing.
     *  The repository's marker refusal covers the sealed life
     *  after t=0; this covers the window before it. */
    private fun midSeal(): Boolean =
        armProving.phase.value is ArmPhase.Proving ||
            armProving.phase.value is ArmPhase.Jammed

    fun promoteTaskToActive(taskId: Int) {
        if (midSeal()) return
        soundEngine.playMechanicalLatch(true)
        viewModelScope.launch {
            val promoted = repository.promoteTaskToActive(taskId)
            if (promoted != null) {
                seedDialFromTask(promoted)
            }
        }
    }

    fun moveTaskOrder(taskId: Int, moveUp: Boolean) {
        soundEngine.playRotaryDetent(if (moveUp) 740.0 else 560.0)
        viewModelScope.launch {
            repository.moveTaskOrder(taskId, moveUp)
        }
    }

    fun deferTask(taskId: Int) {
        soundEngine.playRotaryDetent(510.0)
        viewModelScope.launch {
            repository.deferTask(taskId)
        }
    }

    fun deleteTaskWithUndo(taskId: Int) {
        soundEngine.playMechanicalLatch(false)
        undoDismissJob?.cancel()
        viewModelScope.launch {
            val (deleted, nextActive) = repository.deleteTaskById(taskId)
            if (deleted != null) {
                // G-03: the restore copy hits disk (commit())
                // BEFORE the banner presents the delete as
                // reversible — the promise and its backing are
                // never out of order.
                // Audit R1 #41: the banner is presented only when
                // the copy actually landed. A failed commit used
                // to leave the banner promising a reversal whose
                // restore copy did not exist.
                val persisted = undoDeleteStore.save(
                    deleted,
                    System.currentTimeMillis(),
                    android.os.SystemClock.elapsedRealtime()
                )
                if (persisted) {
                    _deletedTaskForUndo.value = deleted
                    undoDismissJob = viewModelScope.launch {
                        delay(com.example.data.UndoDeleteStore.WINDOW_MILLIS)
                        _deletedTaskForUndo.value = null
                        undoDeleteStore.clear()
                    }
                }
            }
        }
    }

    fun undoDeleteTask() {
        val deleted = _deletedTaskForUndo.value ?: return
        undoDismissJob?.cancel()
        _deletedTaskForUndo.value = null
        undoDeleteStore.clear()
        soundEngine.playHarmonicSeal(arming = true)
        viewModelScope.launch {
            repository.restoreDeletedTask(deleted)
        }
    }

    fun dismissUndoDeleteBanner() {
        undoDismissJob?.cancel()
        _deletedTaskForUndo.value = null
        undoDeleteStore.clear()
    }

    fun extendActiveTaskOvertime(extraMinutes: Int = 5) {
        val currentActive = activeTask.value ?: return
        soundEngine.playRotaryDetent(760.0)
        // Issue #28 (finding 2): ONE writer for the dial. The old
        // code wrote _dialMinutes synchronously (+extra, clamped)
        // AND again from the launched coroutine (the repository's
        // updated estimate) — whichever landed last won, so the
        // extension could double-apply or be silently overwritten.
        // Now the tap writes the dial exactly once (instant, as
        // before), the coroutine only persists the task's new
        // estimate, and the session guard the async path used to
        // carry now guards the one write: while a session is
        // active the dial is the session's, not the task's.
        if (activeSession.value?.isActive != true) {
            _dialMinutes.value = (_dialMinutes.value + extraMinutes).coerceIn(5, 180)
        }
        viewModelScope.launch {
            repository.extendTaskEstimate(currentActive.id, extraMinutes)
        }
    }

    /** Issue #287 F7: add time to the ACTIVE session itself
     *  (never the task) — the session-extension verb. The
     *  repository re-signs the row in the same write. */
    fun extendActiveSession(extraMinutes: Int = 10) {
        viewModelScope.launch {
            repository.extendActiveSession(extraMinutes)
        }
    }

    /** Issue #287 F8: store his one reflection line against a
     *  finished session's row. */
    fun saveSessionReflection(sessionId: Int, note: String) {
        viewModelScope.launch {
            repository.setSessionReflection(sessionId, note)
        }
    }

    // =========================================================================
    // THE DRAWER — permanent storage for finished tasks. State is
    // pages of stored rows accumulated by EARLIER, the months index,
    // paged all-years search, and the declared day words. There is
    // deliberately NO clear/delete function here for stored rows:
    // reopening lives in reopenTask (its own verb), deleting a
    // stored row exists nowhere.
    // =========================================================================

    private val _storedMonths = MutableStateFlow<List<com.example.data.StoredMonth>>(emptyList())
    val storedMonths: StateFlow<List<com.example.data.StoredMonth>> = _storedMonths.asStateFlow()

    /** The Drawer cabinet's forged state (chrome C10): the artifact
     *  ids real sessions actually forged, re-read with the Drawer. */
    private val _forgedArtifactIds = MutableStateFlow<Set<String>>(emptySet())
    val forgedArtifactIds: StateFlow<Set<String>> = _forgedArtifactIds.asStateFlow()

    /** Audit R3b #145: the cabinet's provenance — forging sessions
     *  per artifact id, re-read with the Drawer like the id set. */
    private val _curioForgeRecords =
        MutableStateFlow<Map<String, List<FocusSessionEntity>>>(emptyMap())
    val curioForgeRecords: StateFlow<Map<String, List<FocusSessionEntity>>> =
        _curioForgeRecords.asStateFlow()

    /** The cabinet's forge record (Issue #110, finding 5): per
     *  artifact, how many sessions forged it and when the most
     *  recent forge happened — re-read with the Drawer, from the
     *  same session rows as the forged-id set. */
    private val _forgeStats = MutableStateFlow<Map<String, com.example.data.ForgeStat>>(emptyMap())
    val forgeStats: StateFlow<Map<String, com.example.data.ForgeStat>> = _forgeStats.asStateFlow()

    private val _drawerMonth = MutableStateFlow(
        com.example.data.DrawerDay.filingYearMonth(System.currentTimeMillis())
    )
    val drawerMonth: StateFlow<Pair<Int, Int>> = _drawerMonth.asStateFlow()

    private val _drawerRows = MutableStateFlow<List<FocusTaskEntity>>(emptyList())
    val drawerRows: StateFlow<List<FocusTaskEntity>> = _drawerRows.asStateFlow()

    private val _drawerHasEarlier = MutableStateFlow(false)
    val drawerHasEarlier: StateFlow<Boolean> = _drawerHasEarlier.asStateFlow()

    // Issue #52 F3 cure: a month read is a state, not a void.
    // Loading runs from the select tap until the page lands or
    // fails; a failure lands in drawerError instead of leaving a
    // blank screen indistinguishable from an empty month.
    private val _drawerLoading = MutableStateFlow(false)
    val drawerLoading: StateFlow<Boolean> = _drawerLoading.asStateFlow()

    private val _drawerError = MutableStateFlow<String?>(null)
    val drawerError: StateFlow<String?> = _drawerError.asStateFlow()

    private val _drawerQuery = MutableStateFlow("")
    val drawerQuery: StateFlow<String> = _drawerQuery.asStateFlow()

    private val _drawerResults = MutableStateFlow<List<FocusTaskEntity>>(emptyList())
    val drawerResults: StateFlow<List<FocusTaskEntity>> = _drawerResults.asStateFlow()

    private val _drawerSearchHasEarlier = MutableStateFlow(false)
    val drawerSearchHasEarlier: StateFlow<Boolean> = _drawerSearchHasEarlier.asStateFlow()

    /** #158 F2: the day words the current Drawer search stands on —
     *  every date carrying a word the query names (from
     *  allDayWords(), word-only days included), merged with the
     *  declared words of the dates the task hits file under, so a
     *  found day keeps its word in the search rendering. Empty
     *  exactly when neither a word nor a task-hit date has one. */
    private val _drawerSearchDayWords = MutableStateFlow<Map<String, String>>(emptyMap())
    val drawerSearchDayWords: StateFlow<Map<String, String>> = _drawerSearchDayWords.asStateFlow()

    private val _dayWords = MutableStateFlow<Map<String, String>>(emptyMap())
    val dayWords: StateFlow<Map<String, String>> = _dayWords.asStateFlow()

    private var drawerOffset = 0

    /** Issue #152: true once the user pins a Drawer month by
     *  browsing (selectDrawerMonth). While unpinned, the slow
     *  ticker in init keeps _drawerMonth on the current filing
     *  month — across timezone changes included; refreshDrawer()
     *  clears the pin (the landing is the ruling's default view). */
    private var drawerMonthPinnedByUser = false
    private var drawerSearchOffset = 0
    private var drawerSearchJob: Job? = null

    /** #158 F3: true once a Drawer month has been selected (by a
     *  first refreshDrawer or by hand). refreshDrawer may default
     *  the month only before that — afterwards the place the user
     *  is reading is theirs, and re-entering the Drawer reloads
     *  THAT month instead of resetting to the current one. */
    private var drawerMonthSelected = false

    /** The declared words of the filing dates [rows] sit on, read
     *  from the already-loaded day-words map. */
    private fun dayWordsForRows(
        rows: List<FocusTaskEntity>,
        source: Map<String, String>
    ): Map<String, String> {
        if (rows.isEmpty() || source.isEmpty()) return emptyMap()
        val isos = rows.mapTo(HashSet()) {
            com.example.data.DrawerDay.dateIso(
                com.example.data.DrawerDay.filingDate(it.completedAtMillis)
            )
        }
        return source.filterKeys { it in isos }
    }

    /** Refresh everything the Drawer renders from. Called when the
     *  VAULT destination opens: on FIRST entry the month defaults
     *  to the CURRENT filing month (the ruling's default view);
     *  once a month is selected, refresh keeps it (#158 F3) and
     *  reloads that month, with the months index and day words
     *  re-read. Day words read through allDayWords() — the union
     *  of the prefs capture and the drained day_notes table, so a
     *  word survives its own drain. */
    fun refreshDrawer() {
        // Composed (#152 × #158): the #152 traveller pin clears on
        // refresh; the month defaults to the current filing month
        // only until a month has been selected (#158 F3).
        drawerMonthPinnedByUser = false
        if (!drawerMonthSelected) {
            _drawerMonth.value = com.example.data.DrawerDay.filingYearMonth(System.currentTimeMillis())
        }
        viewModelScope.launch {
            var indexFailed = false
            try {
                _storedMonths.value = repository.storedMonths()
                _dayWords.value = repository.allDayWords()
                _forgedArtifactIds.value = repository.forgedArtifactIds()
                _receiptIndex.value = repository.claimedReceipts()
                _curioForgeRecords.value = repository.curioForgeRecords()
                _forgeStats.value = repository.forgeStats()
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                indexFailed = true
            }
            selectDrawerMonth(_drawerMonth.value.first, _drawerMonth.value.second)
            // The refresh's own selection is the default view,
            // not a user's pin — clear the pin selectDrawerMonth
            // just set, so the issue-#152 ticker keeps following
            // the current filing month after this refresh.
            drawerMonthPinnedByUser = false
            if (indexFailed) {
                // Issue #52 F3: a failed index read prints as an
                // error on the Drawer, never as a silent empty room.
                // Set after selectDrawerMonth, whose start clears
                // the previous error; the page read's own failure
                // path writes the same line.
                _drawerError.value = "THE DRAWER COULD NOT BE READ — TRY AGAIN"
            }
        }
    }

    fun selectDrawerMonth(year: Int, month: Int) {
        // Composed (#152 × #158): a selection is both the #158
        // selection and the #152 traveller pin.
        drawerMonthPinnedByUser = true
        drawerMonthSelected = true
        _drawerMonth.value = year to month
        drawerOffset = 0
        _drawerRows.value = emptyList()
        _drawerHasEarlier.value = false
        _drawerLoading.value = true
        _drawerError.value = null
        viewModelScope.launch {
            try {
                val page = repository.storedPage(year, month, 0)
                // Guard: the month may have changed again mid-read.
                if (_drawerMonth.value == (year to month)) {
                    _drawerRows.value = page.rows
                    drawerOffset = page.rows.size
                    // DRW-04: the repository's probe row decides —
                    // never the returned page's size.
                    _drawerHasEarlier.value = page.hasEarlier
                    _drawerLoading.value = false
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                if (_drawerMonth.value == (year to month)) {
                    _drawerError.value = "THE DRAWER COULD NOT BE READ — TRY AGAIN"
                    _drawerLoading.value = false
                }
            }
        }
    }

    fun loadEarlierDrawer() {
        if (!_drawerHasEarlier.value) return
        val (year, month) = _drawerMonth.value
        val offset = drawerOffset
        viewModelScope.launch {
            try {
                val page = repository.storedPage(year, month, offset)
                if (_drawerMonth.value == (year to month)) {
                    _drawerRows.value = _drawerRows.value + page.rows
                    drawerOffset += page.rows.size
                    _drawerHasEarlier.value = page.hasEarlier
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                if (_drawerMonth.value == (year to month)) {
                    _drawerError.value = "THE DRAWER COULD NOT BE READ — TRY AGAIN"
                }
            }
        }
    }

    fun setDrawerQuery(query: String) {
        _drawerQuery.value = query
        drawerSearchJob?.cancel()
        if (query.isBlank()) {
            // The blank path still clears synchronously (#158 F4) —
            // no debounce stands between clearing and a blank field.
            _drawerResults.value = emptyList()
            _drawerSearchDayWords.value = emptyMap()
            _drawerSearchHasEarlier.value = false
            drawerSearchOffset = 0
            return
        }
        drawerSearchJob = viewModelScope.launch {
            // #158 F4: settle before scanning. The job above is
            // cancelled by the next keystroke, so intermediate
            // keystrokes never reach the leading-wildcard LIKE
            // scan at all; only the query that survives 300 ms
            // of quiet runs it.
            delay(300)
            val page = repository.searchStoredTasks(query, 0)
            // #158 F2: a query naming a day word also stands for
            // every date carrying that word (word-only days
            // included); task-hit dates keep their own words.
            val wordDays = repository.searchDayWords(query)
            if (_drawerQuery.value == query) {
                _drawerResults.value = page.rows
                _drawerSearchDayWords.value =
                    dayWordsForRows(page.rows, _dayWords.value) + wordDays
                drawerSearchOffset = page.rows.size
                _drawerSearchHasEarlier.value = page.hasEarlier
            }
        }
    }

    fun loadEarlierDrawerSearch() {
        if (!_drawerSearchHasEarlier.value) return
        val query = _drawerQuery.value
        val offset = drawerSearchOffset
        viewModelScope.launch {
            val page = repository.searchStoredTasks(query, offset)
            if (_drawerQuery.value == query) {
                _drawerResults.value = _drawerResults.value + page.rows
                // Later pages can file under further worded dates.
                _drawerSearchDayWords.value = _drawerSearchDayWords.value +
                    dayWordsForRows(page.rows, _dayWords.value)
                drawerSearchOffset += page.rows.size
                _drawerSearchHasEarlier.value = page.hasEarlier
            }
        }
    }

    /** Declare (or clear, with null) today's filing-day word. The
     *  word is changeable all day; only today's filing date is
     *  declarable from here — the Drawer files words, it does not
     *  rewrite past days. */
    fun declareTodayWord(word: String?) {
        val iso = com.example.data.DrawerDay.dateIso(
            com.example.data.DrawerDay.filingDate(System.currentTimeMillis())
        )
        viewModelScope.launch {
            repository.declareDayWord(iso, word)
            _dayWords.value = repository.allDayWords()
            _storedMonths.value = repository.storedMonths()
        }
    }

    /**
     * R3b #163 F1: the morning after, yesterday's word must still
     * be declarable — the store always accepted any date; only the
     * picker was gated to today. The Drawer offers this for
     * yesterday's group while it carries no word.
     */
    fun declareDayWordFor(date: java.time.LocalDate, word: String?) {
        val iso = com.example.data.DrawerDay.dateIso(date)
        viewModelScope.launch {
            repository.declareDayWord(iso, word)
            _dayWords.value = repository.allDayWords()
            _storedMonths.value = repository.storedMonths()
        }
    }

    // R3b #163 F5: the NOW task's day boundary. FocusTaskEntity
    // has no created-at and this fix does not migrate the schema;
    // the first active task seen on a filing date is noted in
    // prefs, so the Deck can tag a carried-over NOW task
    // "FROM YESTERDAY" instead of letting it pose as today's.
    private val activeTaskDayStore by lazy { com.example.data.ActiveTaskDayStore(appContext) }
    private val _activeTaskFromYesterday = MutableStateFlow(false)
    val activeTaskFromYesterday: StateFlow<Boolean> = _activeTaskFromYesterday

    private fun refreshActiveTaskCarryover(task: FocusTaskEntity?) {
        if (task == null) {
            _activeTaskFromYesterday.value = false
            return
        }
        val today = com.example.data.DrawerDay.filingDate(System.currentTimeMillis())
        val todayIso = com.example.data.DrawerDay.dateIso(today)
        if (activeTaskDayStore.taskIdOn(todayIso) == null) {
            activeTaskDayStore.note(todayIso, task.id)
        }
        val yesterdayIso = com.example.data.DrawerDay.dateIso(today.minusDays(1))
        _activeTaskFromYesterday.value =
            activeTaskDayStore.taskIdOn(yesterdayIso) == task.id
    }


    fun toggleTaskSubStep(taskId: Int, stepIndex: Int) {
        soundEngine.playRotaryDetent(640.0)
        viewModelScope.launch {
            repository.toggleTaskSubStep(taskId, stepIndex)
        }
    }

    fun addTaskSubStep(taskId: Int, stepTitle: String) {
        val clean = stepTitle.trim()
        if (clean.isEmpty()) return
        soundEngine.playRotaryDetent(690.0)
        viewModelScope.launch {
            repository.addTaskSubStep(taskId, clean)
        }
    }

    fun autoSplitTaskIntoMicroSteps(taskId: Int) {
        if (midSeal()) return
        soundEngine.playHarmonicSeal(arming = true)
        viewModelScope.launch {
            repository.autoSplitTaskIntoMicroSteps(taskId)
        }
    }

    fun updateTaskDuration(taskId: Int, estimatedMinutes: Int) {
        val clamped = estimatedMinutes.coerceIn(5, 180)
        soundEngine.playRotaryDetent(480.0 + clamped * 2.5)
        viewModelScope.launch {
            val updated = repository.updateTaskDuration(taskId, clamped)
            if (updated != null && updated.resolvedStatus == TaskQueueStatus.ACTIVE && activeSession.value?.isActive != true) {
                _dialMinutes.value = updated.estimatedMinutes
            }
        }
    }

    fun cueTaskAndNavigateToFocus(taskId: Int) {
        soundEngine.playHarmonicSeal(arming = true)
        viewModelScope.launch {
            val promoted = repository.promoteTaskToActive(taskId)
            if (promoted != null) {
                seedDialFromTask(promoted)
            }
            _currentDestination.value = MonolithDestination.DECK
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategoryFilter(category: AppCategory?) {
        soundEngine.playRotaryDetent(580.0)
        _selectedCategoryFilter.value = if (_selectedCategoryFilter.value == category) null else category
    }

    /**
     * 3-Tier Per-App Rule Cycling with One-Way Ratchet enforcement during active sessions:
     * During an active session, rules can only be tightened (OPEN -> PEEK_2M -> LOCKED),
     * never loosened from LOCKED -> PEEK_2M or OPEN.
     */
    fun cycleAppBlockTier(app: BlockedAppEntity) {
        dismissPruneNote()
        val isLockedSession = activeSession.value?.isActive == true
        if (isLockedSession) {
            when (app.resolvedTier) {
                AppBlockTier.OPEN -> setAppBlockTier(app, AppBlockTier.PEEK_2M)
                AppBlockTier.PEEK_2M -> setAppBlockTier(app, AppBlockTier.LOCKED)
                AppBlockTier.LOCKED -> {
                    triggerRatchetWarning("ONE-WAY RATCHET ENGAGED • CANNOT UNLOCK '${app.appName.uppercase()}' UNTIL THIS SESSION ENDS${sessionEndsSuffix()}")
                }
            }
            return
        }
        soundEngine.playMechanicalLatch(true)
        viewModelScope.launch {
            repository.cycleAppBlockTier(app)
            refreshUnreviewedOpen()
        }
    }

    /** Issue #107 (shield-setup-lived #3): correct the printed
     *  category guess on a catalogue row. A plain row update in the
     *  repository — no schema change. */
    fun setAppCategory(packageName: String, category: AppCategory) {
        viewModelScope.launch {
            repository.setAppCategory(packageName, category)
        }
    }

    fun setAppBlockTier(app: BlockedAppEntity, tier: AppBlockTier) {
        dismissPruneNote()
        val isLockedSession = activeSession.value?.isActive == true
        if (isLockedSession) {
            val currentRank = when (app.resolvedTier) {
                AppBlockTier.OPEN -> 0
                AppBlockTier.PEEK_2M -> 1
                AppBlockTier.LOCKED -> 2
            }
            val targetRank = when (tier) {
                AppBlockTier.OPEN -> 0
                AppBlockTier.PEEK_2M -> 1
                AppBlockTier.LOCKED -> 2
            }
            if (targetRank < currentRank) {
                triggerRatchetWarning("ONE-WAY RATCHET ENGAGED • CANNOT WEAKEN '${app.appName.uppercase()}' WHILE A SESSION RUNS — END THE SESSION TO CHANGE IT")
                return
            }
        }
        val freq = when (tier) {
            AppBlockTier.LOCKED -> 320.0
            AppBlockTier.PEEK_2M -> 560.0
            AppBlockTier.OPEN -> 820.0
        }
        soundEngine.playRotaryDetent(freq)
        // Issue #141: stage the take-back. Only out-of-session
        // commits get a window — in-session changes are deliberate
        // tightenings under the ratchet, and an undo there could
        // only ever be a weakening (refused in undoLastTierChange).
        if (tier != app.resolvedTier && !isLockedSession) {
            _tierChangeForUndo.value = TierChangeForUndo(
                packageName = app.packageName,
                appName = app.appName,
                fromTier = app.resolvedTier,
                toTier = tier
            )
            tierUndoDismissJob?.cancel()
            tierUndoDismissJob = viewModelScope.launch {
                delay(6000L)
                _tierChangeForUndo.value = null
            }
        }
        viewModelScope.launch {
            repository.setAppBlockTier(app.packageName, tier)
            refreshUnreviewedOpen()
        }
    }

    private fun tierRankOf(tier: AppBlockTier): Int = when (tier) {
        AppBlockTier.OPEN -> 0
        AppBlockTier.PEEK_2M -> 1
        AppBlockTier.LOCKED -> 2
    }

    /**
     * Issue #141: take back the staged sled commit inside its
     * window. The undo is itself a tier change, so it obeys the
     * one-way ratchet: if a session has armed since the commit, an
     * undo that would weaken the app's current rule is refused
     * with the ratchet warning instead of applied.
     */
    fun undoLastTierChange() {
        val change = _tierChangeForUndo.value ?: return
        _tierChangeForUndo.value = null
        tierUndoDismissJob?.cancel()
        val current = apps.value.firstOrNull { it.packageName == change.packageName }?.resolvedTier
        if (activeSession.value?.isActive == true && current != null &&
            tierRankOf(change.fromTier) < tierRankOf(current)
        ) {
            triggerRatchetWarning("ONE-WAY RATCHET ENGAGED • CANNOT WEAKEN '${change.appName.uppercase()}' DURING ACTIVE VAULT")
            return
        }
        viewModelScope.launch {
            repository.setAppBlockTier(change.packageName, change.fromTier)
        }
    }

    fun toggleAppBlocked(app: BlockedAppEntity) {
        cycleAppBlockTier(app)
    }

    fun applySmartLoadout(loadout: SmartLoadout) {
        // Issue #304 F1: mid-session, the repository lands this
        // loadout tightening-only (SessionTierGate) — say so with
        // the ratchet's own warning instead of letting the tap
        // pretend the whole preset applied.
        if (activeSession.value?.isActive == true &&
            com.example.data.LoadoutRules.anyLoosening(
                loadout, apps.value,
                // Issue #121: judge loosening with the same
                // structural time-critical set the commit path
                // uses, so the warning matches the landing.
                if (loadout == SmartLoadout.SLEEP) {
                    com.example.data.TimeCriticalApps.resolveTimeCriticalPackages(
                        appContext, apps.value.map { it.packageName }
                    )
                } else {
                    emptySet()
                }
            )
        ) {
            triggerRatchetWarning("ONE-WAY RATCHET ENGAGED • LOADOUT APPLIES TIGHTENING-ONLY DURING ACTIVE VAULT")
        }
        soundEngine.playHarmonicSeal(true)
        _selectedProfileName.value = loadout.title
        if (loadout == SmartLoadout.STUDY) {
            _shortsReelsSniperEnabled.value = true
            _browserUrlBlockerEnabled.value = true
        }
        viewModelScope.launch {
            // UNA-07: a hand pick is a fact — persist it and rest the
            // cap on it, so the selector survives navigation and
            // process death. Audit R1 #38: the synchronous commit()
            // used to run HERE, on the tap's (main) thread, and its
            // result was discarded — the cap moved even when the
            // pick never reached disk. The commit now runs on IO,
            // and the cap rests on the pick only when the store
            // confirms it; on failure the cap re-reads the stored
            // fact, so it can never present an unpersisted pick.
            val persisted = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                loadoutStore.put(loadout)
            }
            _loadoutCapIndex.value = if (persisted) {
                com.example.data.LoadoutStore.capIndexOf(loadout)
            } else {
                loadoutStore.capIndex()
            }
            repository.applySmartLoadout(loadout)
            if (loadout == SmartLoadout.STUDY) {
                repository.setBrowserBlockerEnabled(true)
                // UNA-01: the loadout's promise ("arms Shorts
                // Sniper") persists too — a loadout that only
                // moved memory was the same lie in a second coat.
                repository.setShortsReelsSniperEnabled(true)
            }
        }
    }

    fun updateAppDailyLimit(app: BlockedAppEntity, minutes: Int) {
        val clamped = minutes.coerceIn(5, 240)
        if (activeSession.value?.isActive == true && clamped > app.dailyLimitMinutes) {
            triggerRatchetWarning("ONE-WAY RATCHET • '${app.appName.uppercase()}' DAILY LIMIT IS ${app.dailyLimitMinutes} MIN — $clamped MIN WOULD LOOSEN IT; LIMITS ONLY TIGHTEN WHILE A SESSION RUNS")
            return
        }
        soundEngine.playRotaryDetent(600.0)
        viewModelScope.launch {
            repository.updateAppDailyLimit(app.packageName, clamped)
        }
    }

    /**
     * Issue #140: step an app's daily limit up the ladder — the
     * catalogue row's hold gesture. The limit used to be a number
     * the user never chose, shown nowhere, changeable nowhere;
     * now it is printed on the row and this steps it
     * (15→20→30→45→60→90→120→180→240, wrapping to 15). The
     * one-way ratchet in [updateAppDailyLimit] still governs
     * mid-session loosening, so a hold during a session can only
     * tighten or be refused with the reason printed.
     */
    fun cycleAppDailyLimit(app: BlockedAppEntity) {
        val ladder = listOf(15, 20, 30, 45, 60, 90, 120, 180, 240)
        val next = ladder.firstOrNull { it > app.dailyLimitMinutes } ?: ladder.first()
        updateAppDailyLimit(app, next)
    }

    fun addCustomApp(appName: String, packageName: String, category: AppCategory) {
        if (appName.isBlank()) return
        soundEngine.playHarmonicSeal(true)
        viewModelScope.launch {
            repository.addCustomApp(appName, packageName, category)
        }
    }

    fun selectAndApplyProfile(profile: FocusProfileEntity) {
        soundEngine.playHarmonicSeal(true)
        // Snapshot the dial + name before the tap overwrites them
        // (issue #139 F4); the repository snapshots the tiers.
        preProfileDialMinutes = _dialMinutes.value
        preProfileName = _selectedProfileName.value
        // Capture for the persist below (issue #139 F4): the vars
        // themselves are read again at undo time in-session.
        val undoDial = preProfileDialMinutes
        val undoName = preProfileName
        _selectedProfileName.value = profile.name
        _dialMinutes.value = profile.defaultMinutes
        viewModelScope.launch {
            // Persist the pre-tap dial + name next to the tiers
            // snapshot, so undo after process death restores all
            // three, not just the tiers.
            if (undoDial != null && undoName != null) {
                repository.saveProfileUndoContext(undoDial, undoName)
            }
            repository.applyProfile(profile)
            _profileUndoAvailable.value = repository.hasProfileUndoSnapshot()
        }
    }

    /** Issue #139 F4: one-tap undo of the last profile application —
     *  restores the hand-set per-app tiers and the dial he had. */
    fun undoLastProfileApply() {
        viewModelScope.launch {
            if (repository.restoreProfileUndoSnapshot()) {
                preProfileDialMinutes?.let { _dialMinutes.value = it }
                preProfileName?.let { _selectedProfileName.value = it }
                preProfileDialMinutes = null
                preProfileName = null
                _profileUndoAvailable.value = false
                soundEngine.playMechanicalLatch(false)
            }
        }
    }

    fun toggleProfileSchedule(profile: FocusProfileEntity) {
        if (activeSession.value?.isActive == true && profile.isScheduleEnabled) {
            triggerRatchetWarning("ONE-WAY RATCHET • ACTIVE SCHEDULE CANNOT BE DISABLED WHILE A SESSION RUNS — END THE SESSION TO CHANGE IT")
            return
        }
        soundEngine.playMechanicalLatch(!profile.isScheduleEnabled)
        viewModelScope.launch {
            repository.toggleProfileSchedule(profile)
        }
    }

    fun updateProfileTimeWindow(profile: FocusProfileEntity, startHour: Int, endHour: Int) {
        soundEngine.playRotaryDetent(520.0 + startHour * 14.0)
        viewModelScope.launch {
            repository.updateProfileTimeWindow(profile, startHour, endHour)
        }
    }


    /** Suki's hand-back tick: one 5ms @ 0.4 seat-knock as the
     *  sentence slot returns to empty on the wire's recovery. Fired
     *  by the slot's stability beat, never by a state swap. */
    fun playHandBackTick() {
        soundEngine.playSeatKnock(5, 0.4f, 320f)
    }

    /** The slide's latch frame enters here (via onSlideCommitted).
     *  The cascade owns everything from this instant: dispatch,
     *  proving, t=0, the re-anchor, the seal sound. The pre-proof
     *  seal this function used to play before the insert is
     *  DELETED — a seal sound before proof is intent wearing
     *  proof's clothes (Opal §F2). The SESSION_EDGE reconcile the
     *  old body fired after startShield now fires inside the
     *  controller, once L1 lands. */
    fun armMonolithSession() {
        // Schlitten §3.1: the standing receipt and its sled gate
        // clear at the next arm — the slip stood until now.
        // Issue #157 note: clearing the STANDING slip at arm no
        // longer loses the receipt — every claimed slip persists
        // in the Vault receipts index (claimedReceipts) and can be
        // re-presented from there at any time.
        _receiptState.value = null
        _finishNightHold.value = false
        sledReturnGate.clear()
        armProving.onLatch(
            intention = _intentionText.value,
            profileName = _selectedProfileName.value,
            durationMinutes = _dialMinutes.value,
            studyBlocksCount = _studyBlocksCount.value
        )
    }

    /**
     * MainActivity ON_RESUME hook (routines rebuild): stamps E2
     * (foreground return) on the active routine session and runs a
     * RESUME reconcile — the cheapest reliable wake the app gets.
     */
    fun onAppResumed() {
        viewModelScope.launch {
            repository.recordAttendanceEvidence(
                MonolithRepository.EVIDENCE_E2_FOREGROUND, System.currentTimeMillis()
            )
            com.example.service.RoutineHeartbeat.tick(
                appContext, com.example.service.RoutineHeartbeat.Source.RESUME, force = true
            )
        }
    }

    /** The 60 s valve's hold-start hook: stamps E4 on the active
     *  routine session (the hold itself is attendance evidence). */
    fun recordValveHoldStarted() {
        viewModelScope.launch {
            repository.recordAttendanceEvidence(
                MonolithRepository.EVIDENCE_E4_HOLD_START, System.currentTimeMillis()
            )
        }
    }

    /** Issue #283: +15 minutes on the running session — the cap
     *  and the HMAC re-sign live in the repository verb. */
    fun extendActiveSession() {
        viewModelScope.launch { repository.extendActiveSessionBySeconds() }
    }

    fun requestDisarmChallenge() {
        val current = activeSession.value ?: return
        val isBelowFloor = current.isStopwatchMode &&
            current.elapsedSeconds < MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS
        val remainingFloor = (MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS - current.elapsedSeconds).coerceAtLeast(0)

        // If Stopwatch Mode has passed the mandatory 20-minute floor, allow clean completion
        if (current.isStopwatchMode && !isBelowFloor) {
            soundEngine.playHarmonicSeal(arming = false)
            viewModelScope.launch {
                repository.disarmActiveSession(completed = true, outcomeReason = "Stopwatch Completed")
                MonolithShieldService.stopShield(appContext)
                com.example.service.RoutineHeartbeat.tickAsync(
                    appContext, com.example.service.RoutineHeartbeat.Source.SESSION_EDGE
                )
            }
            return
        }

        soundEngine.playMechanicalLatch(false)
        _disarmChallenge.value = DisarmChallengeState(
            isVisible = true,
            requiredHoldSeconds = 60,
            sessionIsStopwatch = current.isStopwatchMode,
            remainingFloorSeconds = if (current.isStopwatchMode) remainingFloor else 0
        )
    }

    fun dismissDisarmChallenge() {
        _disarmChallenge.value = _disarmChallenge.value.copy(isVisible = false)
    }

    fun confirmDisarmSession(reason: String = "Ended Early") {
        // TOKEN-SPEC §5: this path never touches the token again. The
        // grace cancel (§4.4) is free and unrecorded by the ledger;
        // a real break goes through completeEmergencyBreakFromValve().
        soundEngine.playHarmonicSeal(arming = false)
        _isAlwaysOnDeskClockVisible.value = false
        _disarmChallenge.value = DisarmChallengeState(isVisible = false)
        viewModelScope.launch {
            repository.disarmActiveSession(completed = false, outcomeReason = reason)
            MonolithShieldService.stopShield(appContext)
            com.example.service.RoutineHeartbeat.tickAsync(
                appContext, com.example.service.RoutineHeartbeat.Source.SESSION_EDGE
            )
        }
    }

    /**
     * The valve's completion callback (TOKEN-SPEC §5): the repository
     * decides. Only on [BreakResult.Completed] — after the spend +
     * disarm transaction has committed — does the seal sound, the
     * overlay close and the shield stop. On
     * [BreakResult.RefusedNoToken] the session stays sealed and the
     * overlay stays open: its count is derived from the repository
     * flow, so it transitions in place to the SPENT rendering
     * (Amendment B) — silently, no seal.
     */
    fun completeEmergencyBreakFromValve() {
        viewModelScope.launch {
            when (repository.completeEmergencyBreak()) {
                BreakResult.Completed -> {
                    soundEngine.playHarmonicSeal(arming = false)
                    _isAlwaysOnDeskClockVisible.value = false
                    _disarmChallenge.value = DisarmChallengeState(isVisible = false)
                    MonolithShieldService.stopShield(appContext)
                    com.example.service.RoutineHeartbeat.tickAsync(
                        appContext, com.example.service.RoutineHeartbeat.Source.SESSION_EDGE
                    )
                }
                BreakResult.RefusedNoToken, BreakResult.NoActiveSession -> {
                    // Session stays sealed; the overlay remains open and
                    // re-renders from the repository flow. Nothing sounds.
                }
            }
        }
    }

    /**
     * Issue #78: the INTENT door into the Airlock. Unlike the
     * in-app doors (the Launcher's peek list, warm and on-screen),
     * an intent may be a stale redelivery, so the showing is
     * revalidated against one-shot store reads — the live session
     * row and the package's live tier/lease — before the Airlock
     * state is touched. A firing that no longer holds is dropped
     * silently: the enforcement doors will fire a fresh one if the
     * user is actually standing in a blocked app.
     */
    fun triggerAirlockInterceptFromIntent(
        packageName: String,
        appName: String,
        reasonBadge: String = "Locked",
        path: InterceptPath = InterceptPath.UNKNOWN
    ) {
        viewModelScope.launch {
            val session = runCatching { repository.activeSessionSnapshot() }
                .onFailure { MLog.w("intercept-from-intent: session snapshot read failed; Airlock dropped", it) }
                .getOrNull()
            val entity = runCatching { repository.appsSnapshot() }
                .onFailure { MLog.w("intercept-from-intent: apps snapshot read failed; Airlock dropped", it) }
                .getOrNull()
                ?.find { it.packageName == packageName }
            val leaseLive = entity != null &&
                entity.resolvedTier == AppBlockTier.PEEK_2M &&
                SystemClock.elapsedRealtime() < entity.activePeekUntilElapsedRealtime
            if (!airlockIntentRevalidated(
                    path = path,
                    tier = entity?.resolvedTier,
                    sessionActive = session?.isActive == true,
                    inSafeBreak = session?.isInSafeBreak == true,
                    peekLeaseLive = leaseLive
                )
            ) return@launch
            triggerAirlockIntercept(packageName, appName, reasonBadge, path)
        }
    }

    fun triggerAirlockIntercept(
        packageName: String,
        appName: String,
        reasonBadge: String = "Shut",
        path: InterceptPath = InterceptPath.UNKNOWN
    ) {
        // Issue #68 F5, Tier 1 — revalidation: an airlock is a
        // claim about a live session. With no active session there
        // is nothing to intercept, whoever asks; every lawful
        // caller fires only inside one (the shield loop, the
        // accessibility doors, the sealed launcher's peek key), so
        // the gate costs them nothing and a fabricated call dies
        // here, before even the latch sound.
        val active = activeSession.value
        if (active?.isActive != true) return

        val appEntity = apps.value.find { it.packageName == packageName }
        // Issue #78, warm-door half of the same law: the showing
        // must ALSO still hold against the live tier, the Safe
        // Break's peek pass and a live peek lease (the intent door
        // above runs the same predicate against one-shot store
        // snapshots). Main's Tier-1 gate already proved the session
        // active, so the predicate's session term is true here; its
        // per-door tier/lease terms are the part this adds.
        val leaseLive = appEntity != null &&
            appEntity.resolvedTier == AppBlockTier.PEEK_2M &&
            SystemClock.elapsedRealtime() < appEntity.activePeekUntilElapsedRealtime
        if (!airlockIntentRevalidated(
                path = path,
                tier = appEntity?.resolvedTier,
                sessionActive = true,
                inSafeBreak = active.isInSafeBreak,
                peekLeaseLive = leaseLive
            )
        ) return
        // On the two per-app doors (the only lease-honouring ones)
        // the claim is about a ruled row: the row must exist, must
        // not be OPEN, and the badge prints from the row — never
        // from the caller's word. The guard, sniper and site doors
        // legitimately name unlisted packages (Settings, a
        // browser) with door-defined badges; their provenance is
        // the Tier-2 nonce at the intent boundary, and the session
        // gate above binds them too.
        val effectiveBadge = if (path.canHonourPeekLease) {
            if (appEntity == null || appEntity.resolvedTier == AppBlockTier.OPEN) return
            appEntity.resolvedTier.badge
        } else {
            reasonBadge
        }
        soundEngine.playMechanicalLatch(true)
        val cooldownLeft = if (appEntity != null) repository.getPeekCooldownRemainingSeconds(appEntity) else 0
        val pillActive = _floatingPeekPill.value.isActive
        // UNA2-01: the offer is computed per-path by the one pure
        // function — tier alone never sells a peek, and an unlisted
        // package (the uninstall guard's whole caseload) never
        // defaults the offer ON.
        val canPeek = peekOfferFor(path, appEntity, cooldownLeft, pillActive)
        // The withheld reason is owed only where the door could
        // have honoured a peek and the tier is PEEK: elsewhere no
        // peek line prints at all (Held Door Truth Baseline) —
        // with the issue-#154 exception carried inside
        // peekWithheldFor: a LOCKED tier on an honouring door
        // prints its state line (TIER_LOCKED), because total
        // silence there read as a broken door.
        val withheld = peekWithheldFor(canPeek, path, appEntity, cooldownLeft, pillActive)

        // Audit R6 #340 F17: the escalated wall — prior reaches at
        // this package TODAY (the DrawerDay filing day), counted
        // from the persisted intercept log the VM already holds,
        // so the price survives process death and never resets
        // mid-day. This reach's own log lands after the wall is
        // set, so the ladder prices reach k+1 from k prior rows.
        val dayStart = com.example.data.DrawerDay.dayStartMillis(System.currentTimeMillis())
        val priorReachesToday = interceptLogs.value.count {
            it.packageName == packageName && it.timestampMillis >= dayStart
        }

        _airlockState.value = AirlockInterceptState(
            isVisible = true,
            interceptedPackageName = packageName,
            interceptedAppName = appName,
            interceptReasonBadge = effectiveBadge,
            // Issue #124: the airlock quotes the SESSION's frozen
            // intention or nothing. The old fallback printed the
            // unarmed draft when no session row backed it — words
            // never declared for any session, labelled ACTIVE.
            sessionIntention = active?.intention ?: "",
            selectedReason = DistractionTriggerReason.MUSCLE_MEMORY,
            interceptInstanceId = ++airlockInstanceSeq,
            priorReachesToday = priorReachesToday,
            // ANCHOR (Issue #55, DECISION-NEEDED recorded in the
            // wave-3 report): the wall is anchored to the INTERCEPT
            // instant (deadline truth); service→overlay latency
            // therefore subtracts from the visible wall. The
            // alternative — anchoring at the first visible frame —
            // is Sarthak's/Ren's call, not silently changed here.
            interceptedAtMonoMillis = SystemClock.elapsedRealtime(),
            peekCooldownEndsAtMonoMillis =
                appEntity?.let { repository.getPeekCooldownDeadlineMono(it) } ?: 0L,
            canUse2MinutePeek = canPeek,
            peekCooldownRemainingSeconds = cooldownLeft,
            interceptPath = path,
            peekWithheld = withheld,
            isLockedTier = appEntity?.resolvedTier == AppBlockTier.LOCKED,
            // SIL-1 / W-28: the door states the session's end from
            // the session's own numbers — now + the WHOLE-SPAN
            // remaining (Tor FIX-2: the per-block remainingSeconds
            // announced the session over at the first block boundary
            // and "ends now" through every Safe Break; the span law
            // at MonolithModels is the model's own derivation, the
            // same one the desk clock reads) — never for a stopwatch.
            sessionEndsAtMillis = active
                ?.takeIf { !it.isStopwatchMode }
                ?.let { System.currentTimeMillis() + it.spanRemainingSeconds * 1000L },
            sessionIsStopwatch = active?.isStopwatchMode == true,
            // Issue #325: the stake rides onto the door — the
            // ledger-derived balance at this instant, never a
            // remembered or decremented-here copy.
            emergencyBreaksRemaining = weeklyEmergencyTokensRemaining.value,
            stopwatchFloorAtMillis = active
                ?.takeIf { it.isStopwatchMode }
                ?.let {
                    System.currentTimeMillis() +
                        (MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS - it.elapsedSeconds)
                            .coerceAtLeast(0) * 1000L
                }
        )
    }

    fun selectDistractionReason(reason: DistractionTriggerReason) {
        soundEngine.playRotaryDetent(580.0)
        _airlockState.value = _airlockState.value.copy(selectedReason = reason)
    }

    /**
     * Grants a 2-Minute Peek lease with its countdown pill — the
     * floating overlay face (PeekPillOverlay, over the peeked app)
     * plus the in-app PeekCountdownPill, both rendering
     * [floatingPeekPill] — and arms the mandatory 20-minute
     * cooldown in Room storage immediately.
     *
     * Issue #114 finding 1, second half: the grant now TAKES the
     * user to the peeked app. Before, the lease anchored at
     * tap-time while the user stood on the Deck and burned lease
     * seconds navigating home and relaunching by hand. The launch
     * fires inside the lease coroutine, after the lease row is
     * written — never before it; the accessibility door honours
     * the lease by its stored end, and a failed launch simply
     * leaves the user on the Deck with the in-app pill standing.
     */
    fun start2MinutePeek() {
        val state = _airlockState.value
        // UNA2-01, second gate: the grant re-derives the offer from
        // the CURRENT row, cooldown and pill — a stale airlock
        // state can never sell a lease its door couldn't honour,
        // and a blank package no longer defaults to an unrelated
        // app. A refused grant leaves the airlock standing.
        val entity = apps.value.find { it.packageName == state.interceptedPackageName }
        val cooldownNow = entity?.let { repository.getPeekCooldownRemainingSeconds(it) } ?: 0
        if (!peekOfferFor(state.interceptPath, entity, cooldownNow, _floatingPeekPill.value.isActive)) {
            // Issue #55 (fresh-eyes): a refused grant must never be a
            // silent no-op under a tappable chip. Recompute the offer
            // from the same live facts and re-present the truth in
            // place — the chip is replaced by the real withheld
            // reason (or by nothing, where the door owes no peek).
            // The intercept instance and wall stamps are preserved:
            // this is a correction of the offer, not a new intercept.
            _airlockState.value = state.copy(
                canUse2MinutePeek = false,
                peekCooldownRemainingSeconds = cooldownNow,
                peekCooldownEndsAtMonoMillis =
                    entity?.let { repository.getPeekCooldownDeadlineMono(it) } ?: 0L,
                peekWithheld = peekWithheldFor(
                    false, state.interceptPath, entity, cooldownNow,
                    _floatingPeekPill.value.isActive
                )
            )
            return
        }
        val pkg = state.interceptedPackageName
        val name = state.interceptedAppName.ifBlank { "2m Peek App" }
        // W3 #130: a peek granted on the SITE door is scoped to the
        // one domain that door intercepted (the airlock's app name
        // IS the matched domain there) — the lease rides the
        // browser's row below, the tag carries its scope.
        if (state.interceptPath == InterceptPath.SITE && state.interceptedAppName.isNotBlank()) {
            com.example.service.SitePeekStore.grant(state.interceptedAppName, pkg)
        }
        soundEngine.playMechanicalLatch(true)
        _airlockState.value = state.copy(isVisible = false)
        // Issue #99 F4 + Issue #114 F1 (merge de-duplication by
        // STURM3-11): the launch itself lives below, after
        // startPeekLease writes the lease row — see the comment
        // there. Main's synchronous copy of the same launch is
        // removed so the app is launched exactly once, and never
        // before the lease exists.
        _floatingPeekPill.value = FloatingPeekPillState(
            isActive = true,
            packageName = pkg,
            appName = name,
            secondsRemaining = MonolithRepository.PEEK_DURATION_SECONDS
        )
        viewModelScope.launch {
            repository.startPeekLease(pkg)
            repository.recordIntercept(
                packageName = pkg,
                appName = name,
                // Issue #124: the frozen session intention as
                // recorded at intercept time — never backfilled
                // from the live draft.
                sessionIntention = state.sessionIntention,
                outcome = "Used 2m Peek",
                triggerReason = state.selectedReason.shortTag
            )
            // Take him to the app the lease is for (issue #114,
            // merging issue #99 F4's identical cure): fired HERE,
            // after startPeekLease has written the lease row —
            // never synchronously before it — so the lease cannot
            // burn on hand navigation and the door honours the
            // stored lease from the first moment. A missing or
            // dead launch intent is not an error — he stays on
            // the Deck, where the in-app pill still shows the clock.
            runCatching {
                val launch = appContext.packageManager.getLaunchIntentForPackage(pkg)
                if (launch != null) {
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    appContext.startActivity(launch)
                }
            }.onFailure {
                // Issue #75 F1 wiring (preserved from main's removed
                // duplicate block): a granted peek whose launch
                // throws must not pass silently.
                MLog.w("peek launch intent failed for $pkg", it)
            }
        }
    }

    fun end2MinutePeekEarly() {
        val peek = _floatingPeekPill.value
        if (!peek.isActive) return
        soundEngine.playHarmonicSeal(arming = true)
        // W3 #130: the site peek's scope tag dies with its lease.
        com.example.service.SitePeekStore.clearIf(peek.packageName)
        _floatingPeekPill.value = FloatingPeekPillState(isActive = false)
        viewModelScope.launch {
            repository.recordPeekEnded(peek.packageName)
        }
    }

    /**
     * Issue #99 F1: the ticker's expiry path — distinct from the
     * pill's ✕ (that user is already in Monolith). Expiry closes the
     * lease, then asks the foreground question with the staleness
     * blindness waived for this package: if the user is still inside
     * the peeked app, Monolith comes to the front and the Airlock
     * stands again — a real eviction, not a paper lapse. If they
     * already left, nothing fires.
     */
    private fun expirePeekAndEvict(peek: FloatingPeekPillState) {
        val pkg = peek.packageName
        val name = peek.appName
        end2MinutePeekEarly()
        if (pkg.isBlank()) return
        viewModelScope.launch {
            val foreground = repository.queryForegroundBlockedApp(setOf(pkg), setOf(pkg))
            if (foreground == pkg) {
                runCatching {
                    appContext.startActivity(
                        Intent(appContext, com.example.MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }
                    )
                }.onFailure {
                    // Issue #75 F1 wiring: the post-peek eviction
                    // relaunch failed — the intercept below still
                    // fires, but the failed relaunch now speaks.
                    MLog.w("expirePeekAndEvict relaunch failed for $pkg", it)
                }
                val entity = apps.value.find { it.packageName == pkg }
                triggerAirlockIntercept(
                    pkg, name.ifBlank { entity?.appName ?: "Blocked App" },
                    entity?.resolvedTier?.badge ?: "Locked",
                    InterceptPath.SHIELD_APP
                )
            }
        }
    }

    /**
     * Issue #99 F3: the dishonest exit leaves a trace. Called when
     * the host activity stops while an Airlock still stands
     * unresolved (RETURN and peek both hide the Airlock before any
     * stop, so a standing Airlock at stop time means Home/Recents).
     * One log entry per intercept instance: the intercept log, the
     * app's intercept count and the session's reaches counter all
     * move — the escape is recorded, repeatable no longer for free.
     * (A true lock-task needs device-owner; that remainder is named
     * STANDS-BLOCKED in the issue-99 report.)
     */
    fun noteAirlockAbandonedIfStanding() {
        val state = _airlockState.value
        if (!state.isVisible) return
        if (state.interceptInstanceId == airlockAbandonLoggedInstanceId) return
        airlockAbandonLoggedInstanceId = state.interceptInstanceId
        viewModelScope.launch {
            repository.recordIntercept(
                packageName = state.interceptedPackageName,
                appName = state.interceptedAppName.ifBlank { "Blocked App" },
                // Issue #124: the frozen session intention as
                // recorded at intercept time — never backfilled
                // from the live draft.
                sessionIntention = state.sessionIntention,
                outcome = "Left Airlock Without Choosing",
                triggerReason = state.selectedReason.shortTag
            )
        }
    }

    /**
     * Issue #119, finding 2: the Airlock's own emergency-break entry.
     * The airlock closes and the SAME disarm challenge the Deck chip
     * opens takes its place — identical token cost, identical 60s
     * continuous hold, identical session-ending effect, decided by
     * the same repository path. No new exit machinery: placement
     * only. (No "Stayed Focused" intercept is recorded — the user
     * did not stay; the break's own ledger records what happened.)
     */
    fun requestDisarmFromAirlock() {
        _airlockState.value = _airlockState.value.copy(isVisible = false)
        requestDisarmChallenge()
    }


    /**
     * The airlock's honest door (issue #114, finding 2): closes
     * the sheet and books the stayed-focused intercept. The
     * RETURN itself is the host's half — MainActivity calls
     * moveTaskToBack(true) on this same tap, sending Monolith's
     * task behind the task the intercept interrupted. This verb
     * alone never claimed to move the user; the pair together
     * are the return the chip's name promises.
     */
    fun transmuteImpulseAndReturn() {
        val state = _airlockState.value
        soundEngine.playHarmonicSeal(arming = true)
        _airlockState.value = state.copy(isVisible = false)
        viewModelScope.launch {
            // R3b #162 F4: a return from the Uninstall Guard door is
            // not "Stayed Focused" and not a phantom reach — it is an
            // uninstall attempt that turned back, and the log now
            // says exactly that.
            val uninstallAttempt = state.interceptPath ==
                com.example.service.InterceptPath.UNINSTALL_GUARD
            repository.recordIntercept(
                packageName = state.interceptedPackageName.ifBlank { "com.zhiliaoapp.musically" },
                appName = state.interceptedAppName.ifBlank { "Blocked App" },
                // Issue #124: the frozen session intention as
                // recorded at intercept time — never backfilled
                // from the live draft.
                sessionIntention = state.sessionIntention,
                outcome = if (uninstallAttempt) "Uninstall Attempt — Returned" else "Stayed Focused",
                triggerReason = state.selectedReason.shortTag,
                countAsReach = !uninstallAttempt
            )
        }
    }

    /** The launcher doors on the Airlock (design-revamp salvage,
     *  D-02): the user leaves the intercept through a launcher door,
     *  so the intercept record says exactly that. Never "Stayed
     *  Focused" — that would be a lie — and never silence, which
     *  would erase an attempt that did happen. Ported from
     *  agent/design-revamp 2dac73e (REVAMP-CRITIC2 D-02). */
    fun leaveAirlockViaLauncherDoors() {
        val state = _airlockState.value
        if (!state.isVisible) return
        _airlockState.value = state.copy(isVisible = false)
        viewModelScope.launch {
            repository.recordIntercept(
                packageName = state.interceptedPackageName.ifBlank { "com.zhiliaoapp.musically" },
                appName = state.interceptedAppName.ifBlank { "Blocked App" },
                sessionIntention = state.sessionIntention.ifBlank { _intentionText.value },
                outcome = "Opened Launcher",
                triggerReason = state.selectedReason.shortTag
            )
        }
    }

    fun incrementAirlockParticlePulse() {
        val current = _airlockState.value
        soundEngine.playRotaryDetent(500.0 + (current.transmutedParticles * 35.0))
        _airlockState.value = current.copy(transmutedParticles = current.transmutedParticles + 1)
    }

    fun refreshSystemGuard() {
        viewModelScope.launch {
            repository.ensureSeededAndSyncPackages()
            _systemGuardStatus.value = repository.getSystemGuardStatus()
        }
    }

    override fun onCleared() {
        soundEngine.stopAllAudio()
        super.onCleared()
    }

    class Factory(
        private val appContext: Context,
        private val repository: MonolithRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Issue #27: the sound engine is born HERE, inside
            // create() — ViewModelProvider calls create() only when
            // no retained instance exists, so an Activity recreation
            // can never mint an orphaned engine the retained
            // ViewModel will ignore, and the engine's death stays
            // where it belongs: onCleared().
            return MonolithViewModel(
                appContext, repository, TactileSoundEngine(appContext)
            ) as T
        }
    }
}

/** The minimal receipt mark the sled gate reads (Schlitten §3.1:
 *  the gate needs the session's identity, nothing of the slip). */
data class SledReceiptMark(val sessionId: Int)

/**
 * Schlitten §3.3's gate — the sled return's once-only law, pure
 * (no Android, no Compose; JUnit-tested in SledReturnGateTest).
 * Stages: NONE → ARMED (publish) → FIRED (the host's tryStart,
 * first caller wins) or VOID (navigation, or expiry unwitnessed).
 * FIRED and VOID are terminal for that session. All callers are
 * Main-confined; no synchronisation, by the class's contract.
 */
internal class SledReturnGate {

    enum class Stage { NONE, ARMED, FIRED, VOID }

    var sessionId: Int = -1
        private set
    var stage: Stage = Stage.NONE
        private set
    private var deadlineMono: Long = 0L
    private var witnessed: Boolean = false

    fun arm(id: Int, deadlineMono: Long) {
        sessionId = id
        this.deadlineMono = deadlineMono
        stage = Stage.ARMED
        witnessed = false
    }

    fun witness(id: Int) {
        if (id == sessionId && stage == Stage.ARMED) witnessed = true
    }

    fun tryStart(id: Int): Boolean {
        if (id == sessionId && stage == Stage.ARMED) {
            stage = Stage.FIRED
            return true
        }
        return false
    }

    fun void(id: Int) {
        if (id == sessionId && stage == Stage.ARMED) stage = Stage.VOID
    }

    /** Expiry voids ONLY an unwitnessed gate: a receipt nobody
     *  composed a Deck for must not fire its return hours later
     *  on a stale ARMED. A witnessed gate survives to its firing
     *  (the host effect is already counting). */
    fun expire(id: Int) {
        if (id == sessionId && stage == Stage.ARMED && !witnessed) {
            stage = Stage.VOID
        }
    }

    fun clear() {
        sessionId = -1
        stage = Stage.NONE
        witnessed = false
    }

    /** Schlitten §2.3's factory rule: seated while a session
     *  runs, seated while a return is pending (receipt + ARMED),
     *  home in every other world. [nowMono] is accepted for the
     *  contract's shape and deliberately unread — the stage, not
     *  a clock, decides. */
    fun initialFraction(sessionRunning: Boolean, receipt: SledReceiptMark?, nowMono: Long): Float =
        when {
            sessionRunning -> 1f
            receipt != null && receipt.sessionId == sessionId && stage == Stage.ARMED -> 1f
            else -> 0f
        }
}

