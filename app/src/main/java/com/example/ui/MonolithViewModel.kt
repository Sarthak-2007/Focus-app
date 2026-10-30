package com.example.ui

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppBlockTier
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

enum class MonolithDestination(val route: String, val label: String) {
    DECK("deck", "Focus"),
    TASKS("tasks", "Tasks"),
    SHIELD("shield", "Shield"),
    VAULT("vault", "Receipts")
    // GUARD is DISSOLVED (chrome wave): the destination, its screen,
    // and its -1 tab mapping are deleted. What Guard held lives in
    // PARK (the rail's end zone) and on Shield (the websites editor).
}

/**
 * The theme evaluation (chrome C4, Sten's precedence, §8.10): ONE
 * evaluation drives both the colour scheme and PARK's printed
 * readout, so the two can never disagree. (1) a session is active →
 * NIGHT, the session owns it; (2) else an enabled, unpaused SLEEP
 * window is active → NIGHT, that routine owns it and is named as
 * printed on its Shield row; (3) else DAY, owned by nothing — and
 * the readout says so. Window state comes from the one derivation
 * (RoutineDerive), never a second computation.
 */
data class ThemeEvaluation(val isNight: Boolean, val readout: String)

fun evaluateTheme(
    sessionActive: Boolean,
    routines: List<com.example.data.RoutineEntity>,
    nowMillis: Long
): ThemeEvaluation {
    if (sessionActive) {
        return ThemeEvaluation(true, "AUTO · NIGHT — SESSION ACTIVE")
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
    return if (owner != null) {
        val start = String.format(
            Locale.US, "%02d:%02d", owner.startMinutes / 60, owner.startMinutes % 60
        )
        ThemeEvaluation(true, "AUTO · NIGHT — ${owner.name} $start")
    } else {
        ThemeEvaluation(false, "AUTO · DAY — NO ROUTINE OWNS IT")
    }
}

/**
 * A FROZEN digest: the held notifications as they stood at one
 * instant, keyed to the session that froze them. While sealed, the
 * digest is this snapshot — never the live flow. Reopening inside
 * the same session reuses it (same rows, same stamp); a new session
 * arming under an open sheet remints it; it survives the session's
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
 * door never owed a peek — a locked app, a non-honouring door, an
 * unlisted package: those doors print no peek line at all, never a
 * refusal for an offer that was never on the table.
 */
enum class PeekWithheld { COOLDOWN, ALREADY_ACTIVE, PEEK_OFF }

/**
 * The one peek-offer computation (Truth Car 3, UNA2-01). The offer
 * exists ONLY where a lease can actually be honoured:
 *  - the door must read the lease (APP_TIER / SHIELD_APP — the
 *    sniper, the site blocker and the uninstall guard never do, and
 *    the guard's package is unlisted: startPeekLease would write
 *    to no row and the "peek" would protect nothing),
 *  - the package must have a row (a null entity never defaults ON),
 *  - its tier must be PEEK_2M, its cooldown spent, no pill active.
 * Pure, so the offer matrix is unit-tested door by door.
 */
internal fun peekOfferFor(
    path: InterceptPath,
    entity: BlockedAppEntity?,
    cooldownLeftSeconds: Int,
    pillActive: Boolean
): Boolean =
    path.canHonourPeekLease &&
        entity != null &&
        entity.resolvedTier == AppBlockTier.PEEK_2M &&
        cooldownLeftSeconds == 0 &&
        !pillActive

data class AirlockInterceptState(
    val isVisible: Boolean = false,
    val interceptedPackageName: String = "",
    val interceptedAppName: String = "",
    val interceptReasonBadge: String = "Locked",
    val sessionIntention: String = "",
    val selectedReason: DistractionTriggerReason = DistractionTriggerReason.MUSCLE_MEMORY,
    val transmutedParticles: Int = 0,
    val canUse2MinutePeek: Boolean = true,
    val peekCooldownRemainingSeconds: Int = 0,
    /** The door that fired (UNA2-01): carried from the firing
     *  service through the intent into this state, never inferred
     *  from the badge or the package. */
    val interceptPath: InterceptPath = InterceptPath.UNKNOWN,
    /** Non-null only when this door could have honoured a peek
     *  and the tier owed one: the withheld line's reason. */
    val peekWithheld: PeekWithheld? = null,
    /** The session's span end (SIL-1, Hale W-28): the door states
     *  the end from the session's own numbers — the same
     *  now + remaining derivation the Deck's TIME cell prints.
     *  Null when there is no timed end to state (stopwatch, or no
     *  session): the door never invents one. */
    val sessionEndsAtMillis: Long? = null,
    val sessionIsStopwatch: Boolean = false
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
    val requiredHoldSeconds: Int = 60
    // No token count lives here (TOKEN-SPEC §5, Amendment A): a count
    // copied into this state at open time is a snapshot that can lie
    // for the visit's whole life. The overlay derives its count from
    // the repository flow (weeklyEmergencyTokensRemaining below) for
    // as long as it is visible.
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

    private val _intentionText = MutableStateFlow("Finish Chapter 4 Problem Set")
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

    private val _sevenDayStats = MutableStateFlow<List<DailyFocusStat>>(emptyList())
    val sevenDayStats: StateFlow<List<DailyFocusStat>> = _sevenDayStats.asStateFlow()


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
        return " (ENDS " + java.text.SimpleDateFormat("HH:mm", java.util.Locale.US)
            .format(java.util.Date(endsAt)) + ")"
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

    val totalQueueMinutes: StateFlow<Int> = tasks.map { list ->
        list.filter {
            it.resolvedStatus == TaskQueueStatus.ACTIVE || it.resolvedStatus == TaskQueueStatus.QUEUED
        }.sumOf { it.estimatedMinutes }.coerceAtLeast(_dialMinutes.value)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 50)

    val checklist: StateFlow<List<StudyChecklistItem>> = tasks.map { list ->
        if (list.isEmpty()) {
            listOf(
                StudyChecklistItem(1, "Finish Chapter 4 Problem Set (#1–6)", false),
                StudyChecklistItem(2, "Synthesize Lecture 9 Architecture Notes", false),
                StudyChecklistItem(3, "Debug Dynamic Programming Memo Table", false)
            )
        } else {
            list.map { task ->
                StudyChecklistItem(
                    id = task.id,
                    title = task.title,
                    isDone = task.resolvedStatus == TaskQueueStatus.COMPLETED
                )
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        listOf(
            StudyChecklistItem(1, "Finish Chapter 4 Problem Set (#1–6)", false),
            StudyChecklistItem(2, "Synthesize Lecture 9 Architecture Notes", false),
            StudyChecklistItem(3, "Debug Dynamic Programming Memo Table", false)
        )
    )

    private val _selectedProfileName = MutableStateFlow("Study Loadout")
    val selectedProfileName: StateFlow<String> = _selectedProfileName.asStateFlow()

    // UNA-07 (Truth Car 3): the Shield LOADOUT cap rests on the
    // stored fact of his last HAND pick (LoadoutStore), never on a
    // composable's remember. Seeded from the store at birth; written
    // only by applySmartLoadout below (the hand path — a routine
    // applying its own loadout does not move his selector).
    private val loadoutStore = com.example.data.LoadoutStore(appContext)
    private val _loadoutCapIndex = MutableStateFlow(loadoutStore.capIndex())
    val loadoutCapIndex: StateFlow<Int> = _loadoutCapIndex.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<AppCategory?>(null)
    val selectedCategoryFilter: StateFlow<AppCategory?> = _selectedCategoryFilter.asStateFlow()

    private val _airlockState = MutableStateFlow(AirlockInterceptState())
    val airlockState: StateFlow<AirlockInterceptState> = _airlockState.asStateFlow()

    private val _disarmChallenge = MutableStateFlow(DisarmChallengeState())
    val disarmChallenge: StateFlow<DisarmChallengeState> = _disarmChallenge.asStateFlow()

    private val _systemGuardStatus = MutableStateFlow(
        SystemGuardStatus(
            hasUsageStatsPermission = false,
            hasOverlayPermission = false,
            isAccessibilityServiceEnabled = false,
            isNotificationListenerEnabled = false,
            isDeviceAdminEnabled = false,
            isMonotonicClockIntact = true,
            isHmacSignatureValid = true,
            clockDriftSeconds = 0L,
            systemBootCount = 1,
            installedLauncherAppsCount = 12
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

    val apps: StateFlow<List<BlockedAppEntity>> = repository.allAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredApps: StateFlow<List<BlockedAppEntity>> = combine(
        apps,
        _searchQuery,
        _selectedCategoryFilter
    ) { allApps, query, category ->
        allApps.filter { app ->
            val matchesQuery = query.isBlank() ||
                app.appName.contains(query, ignoreCase = true)
            val matchesCategory = category == null || app.category == category.name
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profiles: StateFlow<List<FocusProfileEntity>> = repository.allProfilesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSession: StateFlow<FocusSessionEntity?> = repository.activeSessionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sessionHistory: StateFlow<List<FocusSessionEntity>> = repository.sessionHistoryFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val interceptLogs: StateFlow<List<InterceptLogEntity>> = repository.interceptLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ------------------------------------------------------------------
    // THE THEME EVALUATION (chrome C4). One evaluation, two readers:
    // the scheme (through darkTheme) and PARK's printed readout.
    // Re-derived on a 1-second stamp so a routine boundary swaps the
    // scheme on its frame — a 0ms cut, never a fade.
    // ------------------------------------------------------------------
    private val _themeNow = MutableStateFlow(System.currentTimeMillis())

    val themeEvaluation: StateFlow<ThemeEvaluation> = combine(
        routines,
        activeSession,
        _themeNow
    ) { routineList, session, now ->
        evaluateTheme(session?.isActive == true, routineList, now)
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
        ((completedSecs + activeSecs) / 60).coerceAtLeast(35)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 75)

    /**
     * The reset-note strip state (Freya §3.3): shown after the reseed
     * until he edits any routine (which dismisses it) or it ages out
     * at 14 days. Fresh installs never see it (reseed flag and dismiss
     * flag are both set at seed time).
     */
    val showRoutineResetNote: StateFlow<Boolean> = combine(
        repository.guardSettingsFlow,
        routines
    ) { settings, routineList ->
        if (settings == null || !settings.routineReseedV1Done || settings.routineReseedNoteDismissed) {
            false
        } else {
            val targetNames = setOf("FASTED BLOCK", "DAY BLOCKS", "SLEEP")
            val newestEdit = routineList.filter { it.name in targetNames }
                .maxOfOrNull { it.scheduleEditedAtMillis } ?: 0L
            newestEdit == 0L ||
                System.currentTimeMillis() - newestEdit < 14L * 24L * 60L * 60L * 1000L
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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
    val wireSnapshots: StateFlow<com.example.service.WatchSnapshot> = com.example.service.WatchStore.snapshots

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
     *  Store; while a session seals the phone it prints the freeze's
     *  captured line, untouched by anything the store does after. */
    val guardLinePrint: StateFlow<com.example.ui.components.GuardLinePrint?> = combine(
        com.example.service.WatchStore.snapshots,
        _sealFreeze,
        activeSession
    ) { snap, freeze, session ->
        if (session?.isActive == true && freeze?.guardLine != null) {
            freeze.guardLine
        } else {
            com.example.ui.components.guardLineFor(
                snap, android.os.SystemClock.elapsedRealtime()
            )
        }
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
    private val _finishState = MutableStateFlow<FinishState?>(null)
    val finishState: StateFlow<FinishState?> = _finishState.asStateFlow()

    private val _receiptState = MutableStateFlow<com.example.data.ReceiptState?>(null)
    val receiptState: StateFlow<com.example.data.ReceiptState?> = _receiptState.asStateFlow()

    /** Schlitten's gate: the sled return's once-only law, pure
     *  and unit-tested. The VM owns its state law; the Deck host
     *  owns the firing. */
    internal val sledReturnGate = SledReturnGate()
    private var sledExpiryJob: Job? = null

    /** F-Z1 (a) — the VM mirror of the shot's ground step, and
     *  F23's pin in the same state: SEALED_PRE_GROUND is the
     *  400ms where the scheme holds its day values while the
     *  orange exits; GROUNDED is the step itself. */
    enum class ShotGround { NOT_SEALED, SEALED_PRE_GROUND, GROUNDED }

    private val _shotGround = MutableStateFlow(ShotGround.NOT_SEALED)
    val shotGround: StateFlow<ShotGround> = _shotGround.asStateFlow()
    private var shotGroundJob: Job? = null

    private val handledFinishIds = mutableSetOf<Int>()
    private var finishControllerStarted = false

    init {
        // G-03 rehydration: a delete taken just before process
        // death left its restore copy on disk. If its five-second
        // window is still open, the banner returns with a working
        // UNDO for the remainder; if the window closed while the
        // process was dead, the snapshot is discarded — the delete
        // was booked at its tap and an expired reversal is not
        // revived by a relaunch.
        undoDeleteStore.load()?.let { (snapshot, deletedAt) ->
            val remaining = com.example.data.UndoDeleteStore.WINDOW_MILLIS -
                (System.currentTimeMillis() - deletedAt)
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
        viewModelScope.launch {
            repository.ensureSeededAndSyncPackages()
            _pruneNote.value = repository.pendingPruneNote()
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
            _sevenDayStats.value = repository.compute7DayHistory()
        }
        viewModelScope.launch {
            activeTask.collect { task ->
                if (task != null) {
                    _intentionText.value = task.title
                    seedDialFromTask(task)
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
                com.example.service.WatchStore.updateInterceptorInputs(
                    safeBreak = session?.isInSafeBreak == true
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
        // THE FREEZE WATCH: while the digest sheet is visible, any
        // session that becomes active with an id the holder doesn't
        // carry (re)mints the freeze — a routine arming under an
        // open live sheet (RoutineAlarmReceiver fires with no UI),
        // or a new session replacing one that ended under a frozen
        // sheet. Transitions to inactive are ignored on purpose:
        // the freeze latches past the session's end until dismissed.
        viewModelScope.launch {
            combine(activeSession, showNotificationDigestSheet) { session, visible ->
                session to visible
            }.collect { (session, visible) ->
                if (visible && session != null && session.isActive &&
                    _frozenDigest.value?.sessionId != session.id
                ) {
                    mintFrozenDigest(session.id)
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

    /** Take the one-shot snapshot for [sessionId] and hold it. The
     *  write is guarded: if the session changed again while the
     *  read was in flight, the stale mint is dropped and the freeze
     *  watch will mint for the session that is actually active. */
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
                // frame, as a cut.
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
                    repository.latchShieldGapIfStale(System.currentTimeMillis())
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
                        end2MinutePeekEarly()
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
            runCatching { repository.rehydrateReceipt() }.getOrNull()?.let { restored ->
                if (_receiptState.value == null) _receiptState.value = restored
            }
            val active = runCatching { repository.activeSessionSnapshot() }.getOrNull()
            if (active != null && armProving.phase.value == ArmPhase.Idle) {
                runCatching {
                    sealTransition.recover(
                        active.id, active.startWallTimeMillis, System.currentTimeMillis()
                    )
                }
            }
            // Draht: a faulted finish from a previous process
            // resumes its cadence from the row's own stamps.
            runCatching {
                com.example.service.ReleaseRetryScheduler.runDueRetry(appContext)
            }
        }
    }

    /** Draht's third trigger: entering the Deck retries a
     *  faulted release whose cadence is due. */
    fun onDeckOpenRetry() {
        viewModelScope.launch {
            runCatching {
                com.example.service.ReleaseRetryScheduler.runDueRetry(appContext)
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
     * E0 := discovery; within 10 minutes ⇒ claim + issue at
     * discovery, no held frame, no pulse; older ⇒ the moment is
     * gone — marked handled, never fabricated into a late №.
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
                            ageMs <= 600_000L -> onFinishE0(row, kind, witnessed = false)
                            else -> handledFinishIds += row.id
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
        val e0Mono = SystemClock.elapsedRealtime()
        val e0Wall = System.currentTimeMillis()
        if (activeSession.value?.id == sessionId) {
            // Zeile's coarse ended edge: the desk clock's hide
            // lives here now, not in the ticker.
            _isAlwaysOnDeskClockVisible.value = false
        }
        viewModelScope.launch {
            _sevenDayStats.value = repository.compute7DayHistory()
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
                soundEngine.playSeatKnock(24, 1.0f, 320f)
                if (pulses) {
                    val toPulse = 700L - (SystemClock.elapsedRealtime() - e0Mono)
                    if (toPulse > 0L) delay(toPulse)
                    if (SystemClock.elapsedRealtime() - e0Mono < 900L) {
                        soundEngine.playCompletionPulse()
                    }
                }
                val toPair = 880L - (SystemClock.elapsedRealtime() - e0Mono)
                if (toPair > 0L) delay(toPair)
                soundEngine.playSeatKnock(24, 1.0f, 320f)
                delay(90L)
                soundEngine.playSeatKnock(12, 0.5f, 320f)
            }

            var pawlLiftMs: Long? = null
            var halted = false
            var issuedReceipt: com.example.data.ReceiptState? = null
            while (true) {
                val offset = SystemClock.elapsedRealtime() - e0Mono
                if (pawlLiftMs == null &&
                    com.example.service.MonolithShieldService.instance == null
                ) {
                    pawlLiftMs = offset
                }
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
        if (sessionId in handledFinishIds) return
        viewModelScope.launch {
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

    fun voidSledReturn(sessionId: Int) = sledReturnGate.void(sessionId)

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
        SoundEffectsSetting.setEnabled(appContext, next)
        _soundEnabled.value = next
        if (next) soundEngine.playRotaryDetent(720.0)
        return next
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
        val clamped = minutes.coerceIn(3, 180)
        if (clamped != _dialMinutes.value) {
            val pitch = 440.0 + (clamped * 2.8)
            soundEngine.playRotaryDetent(pitch)
            _dialMinutes.value = clamped
        }
    }

    fun setIntentionText(text: String) {
        _intentionText.value = text
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

    private fun persistRoutine(routine: com.example.data.RoutineEntity) {
        viewModelScope.launch {
            // Every edit re-stamps the schedule and dismisses the
            // reset note, then reconciles immediately (CRUD trigger).
            repository.updateRoutine(
                routine.copy(scheduleEditedAtMillis = System.currentTimeMillis())
            )
            repository.dismissRoutineResetNote()
            com.example.service.RoutineHeartbeat.tick(
                appContext, com.example.service.RoutineHeartbeat.Source.CRUD, force = true
            )
        }
    }

    fun toggleRoutine(routine: com.example.data.RoutineEntity) {
        val updated = routine.copy(enabled = !routine.enabled)
        soundEngine.playMechanicalLatch(updated.enabled)
        persistRoutine(updated)
    }

    fun cycleRoutineLoadout(routine: com.example.data.RoutineEntity) {
        val order = listOf(SmartLoadout.STUDY, SmartLoadout.WORK_SPRINT, SmartLoadout.SLEEP)
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
        val mask = routine.daysMask xor (1 shl dayIndex)
        soundEngine.playRotaryDetent()
        persistRoutine(routine.copy(daysMask = mask))
    }

    fun addBlockedSite(raw: String) {
        viewModelScope.launch {
            if (repository.addBlockedSite(raw)) soundEngine.playMechanicalLatch(true)
        }
    }

    fun removeBlockedSite(domain: String) {
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
                // Sealed open: freeze — reusing the holder when it
                // already belongs to this session (same rows, same
                // stamp; reopening pays nothing), minting otherwise.
                if (_frozenDigest.value?.sessionId != session.id) {
                    mintFrozenDigest(session.id)
                }
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
        }
    }

    fun deleteCapturedNotification(id: Int) {
        viewModelScope.launch {
            if (repository.deleteCapturedNotification(id)) {
                soundEngine.playRotaryDetent(520.0)
            }
        }
    }

    fun simulateCapturedNotification() {
        soundEngine.playRotaryDetent(700.0)
        viewModelScope.launch {
            repository.saveCapturedNotification(
                packageName = "com.whatsapp",
                appName = "WhatsApp",
                senderTitle = "Lab Partner • CS 301",
                messageBody = "Just pushed the test suite fix—check it after your focus block!"
            )
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
        estimatedMinutes: Int = 25,
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
                _intentionText.value = created.title
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
                _intentionText.value = updated.title
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
                _intentionText.value = nextActive.title
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
                _intentionText.value = nextActive.title
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
                _intentionText.value = promoted.title
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
            val nextActive = repository.deferTask(taskId)
            if (nextActive != null) {
                _intentionText.value = nextActive.title
            }
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
                undoDeleteStore.save(deleted, System.currentTimeMillis())
                _deletedTaskForUndo.value = deleted
                if (nextActive != null) {
                    _intentionText.value = nextActive.title
                }
                undoDismissJob = viewModelScope.launch {
                    delay(com.example.data.UndoDeleteStore.WINDOW_MILLIS)
                    _deletedTaskForUndo.value = null
                    undoDeleteStore.clear()
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
            val restored = repository.restoreDeletedTask(deleted)
            if (restored.resolvedStatus == TaskQueueStatus.ACTIVE) {
                _intentionText.value = restored.title
            }
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
        viewModelScope.launch {
            val updated = repository.extendTaskEstimate(currentActive.id, extraMinutes)
            if (updated != null && activeSession.value?.isActive != true) {
                _dialMinutes.value = updated.estimatedMinutes
            }
        }
        _dialMinutes.value = (_dialMinutes.value + extraMinutes).coerceIn(5, 180)
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

    private val _drawerMonth = MutableStateFlow(
        com.example.data.DrawerDay.filingYearMonth(System.currentTimeMillis())
    )
    val drawerMonth: StateFlow<Pair<Int, Int>> = _drawerMonth.asStateFlow()

    private val _drawerRows = MutableStateFlow<List<FocusTaskEntity>>(emptyList())
    val drawerRows: StateFlow<List<FocusTaskEntity>> = _drawerRows.asStateFlow()

    private val _drawerHasEarlier = MutableStateFlow(false)
    val drawerHasEarlier: StateFlow<Boolean> = _drawerHasEarlier.asStateFlow()

    private val _drawerQuery = MutableStateFlow("")
    val drawerQuery: StateFlow<String> = _drawerQuery.asStateFlow()

    private val _drawerResults = MutableStateFlow<List<FocusTaskEntity>>(emptyList())
    val drawerResults: StateFlow<List<FocusTaskEntity>> = _drawerResults.asStateFlow()

    private val _drawerSearchHasEarlier = MutableStateFlow(false)
    val drawerSearchHasEarlier: StateFlow<Boolean> = _drawerSearchHasEarlier.asStateFlow()

    private val _dayWords = MutableStateFlow<Map<String, String>>(emptyMap())
    val dayWords: StateFlow<Map<String, String>> = _dayWords.asStateFlow()

    private var drawerOffset = 0
    private var drawerSearchOffset = 0
    private var drawerSearchJob: Job? = null

    /** Refresh everything the Drawer renders from. Called when the
     *  VAULT destination opens: the landing is always the CURRENT
     *  filing month (the ruling's default view), with the months
     *  index and day words re-read. Day words read through
     *  allDayWords() — the union of the prefs capture and the
     *  drained day_notes table, so a word survives its own drain. */
    fun refreshDrawer() {
        _drawerMonth.value = com.example.data.DrawerDay.filingYearMonth(System.currentTimeMillis())
        viewModelScope.launch {
            _storedMonths.value = repository.storedMonths()
            _dayWords.value = repository.allDayWords()
            _forgedArtifactIds.value = repository.forgedArtifactIds()
            selectDrawerMonth(_drawerMonth.value.first, _drawerMonth.value.second)
        }
    }

    fun selectDrawerMonth(year: Int, month: Int) {
        _drawerMonth.value = year to month
        drawerOffset = 0
        _drawerRows.value = emptyList()
        _drawerHasEarlier.value = false
        viewModelScope.launch {
            val page = repository.storedPage(year, month, 0)
            // Guard: the month may have changed again mid-read.
            if (_drawerMonth.value == (year to month)) {
                _drawerRows.value = page.rows
                drawerOffset = page.rows.size
                // DRW-04: the repository's probe row decides —
                // never the returned page's size.
                _drawerHasEarlier.value = page.hasEarlier
            }
        }
    }

    fun loadEarlierDrawer() {
        if (!_drawerHasEarlier.value) return
        val (year, month) = _drawerMonth.value
        val offset = drawerOffset
        viewModelScope.launch {
            val page = repository.storedPage(year, month, offset)
            if (_drawerMonth.value == (year to month)) {
                _drawerRows.value = _drawerRows.value + page.rows
                drawerOffset += page.rows.size
                _drawerHasEarlier.value = page.hasEarlier
            }
        }
    }

    fun setDrawerQuery(query: String) {
        _drawerQuery.value = query
        drawerSearchJob?.cancel()
        if (query.isBlank()) {
            _drawerResults.value = emptyList()
            _drawerSearchHasEarlier.value = false
            drawerSearchOffset = 0
            return
        }
        drawerSearchJob = viewModelScope.launch {
            val page = repository.searchStoredTasks(query, 0)
            if (_drawerQuery.value == query) {
                _drawerResults.value = page.rows
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

    fun splitActiveTaskAndReturn(microStepTitle: String) {
        if (midSeal()) return
        soundEngine.playHarmonicSeal(arming = true)
        val state = _airlockState.value
        _airlockState.value = state.copy(isVisible = false)
        viewModelScope.launch {
            val microTask = repository.splitActiveTaskIntoMicroStep(microStepTitle)
            _intentionText.value = microTask.title
            repository.recordIntercept(
                packageName = state.interceptedPackageName.ifBlank { "com.google.android.youtube" },
                appName = state.interceptedAppName.ifBlank { "Blocked App" },
                sessionIntention = microTask.title,
                outcome = "Split Into 10m Micro-Step",
                triggerReason = DistractionTriggerReason.STUCK_ON_PROBLEM.shortTag
            )
        }
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
                _intentionText.value = promoted.title
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
        viewModelScope.launch {
            repository.setAppBlockTier(app.packageName, tier)
        }
    }

    fun toggleAppBlocked(app: BlockedAppEntity) {
        cycleAppBlockTier(app)
    }

    fun applySmartLoadout(loadout: SmartLoadout) {
        soundEngine.playHarmonicSeal(true)
        _selectedProfileName.value = loadout.title
        // UNA-07: a hand pick is a fact — persist it and rest the cap
        // on it, so the selector survives navigation and process death.
        loadoutStore.put(loadout)
        _loadoutCapIndex.value = com.example.data.LoadoutStore.capIndexOf(loadout)
        if (loadout == SmartLoadout.STUDY) {
            _shortsReelsSniperEnabled.value = true
            _browserUrlBlockerEnabled.value = true
        }
        viewModelScope.launch {
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
        _selectedProfileName.value = profile.name
        _dialMinutes.value = profile.defaultMinutes
        viewModelScope.launch {
            repository.applyProfile(profile)
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

    fun createCustomProfile(
        name: String,
        minutes: Int,
        categories: List<AppCategory>,
        startHour: Int,
        endHour: Int
    ) {
        if (name.isBlank()) return
        soundEngine.playHarmonicSeal(true)
        viewModelScope.launch {
            repository.saveCustomProfile(name, minutes, categories, startHour, endHour)
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
        _receiptState.value = null
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
                _sevenDayStats.value = repository.compute7DayHistory()
            }
            return
        }

        soundEngine.playMechanicalLatch(false)
        _disarmChallenge.value = DisarmChallengeState(
            isVisible = true,
            requiredHoldSeconds = 60
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
            _sevenDayStats.value = repository.compute7DayHistory()
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
                    _sevenDayStats.value = repository.compute7DayHistory()
                }
                BreakResult.RefusedNoToken, BreakResult.NoActiveSession -> {
                    // Session stays sealed; the overlay remains open and
                    // re-renders from the repository flow. Nothing sounds.
                }
            }
        }
    }

    fun triggerAirlockIntercept(
        packageName: String,
        appName: String,
        reasonBadge: String = "Locked",
        path: InterceptPath = InterceptPath.UNKNOWN
    ) {
        soundEngine.playMechanicalLatch(true)
        val active = activeSession.value

        val appEntity = apps.value.find { it.packageName == packageName }
        val cooldownLeft = if (appEntity != null) repository.getPeekCooldownRemainingSeconds(appEntity) else 0
        val pillActive = _floatingPeekPill.value.isActive
        // UNA2-01: the offer is computed per-path by the one pure
        // function — tier alone never sells a peek, and an unlisted
        // package (the uninstall guard's whole caseload) never
        // defaults the offer ON.
        val canPeek = peekOfferFor(path, appEntity, cooldownLeft, pillActive)
        // The withheld reason is owed only where the door could
        // have honoured a peek and the tier is PEEK: elsewhere no
        // peek line prints at all (Held Door Truth Baseline).
        val withheld = when {
            canPeek -> null
            !path.canHonourPeekLease -> null
            appEntity?.resolvedTier != AppBlockTier.PEEK_2M -> null
            // Hale W-30: a peek length of zero is its own reason —
            // the peek is OFF, not cooling down. (Trunk's constant
            // is 120 s; the branch arms the day Rurik's editable
            // store can set it to 0.)
            MonolithRepository.PEEK_DURATION_SECONDS <= 0 -> PeekWithheld.PEEK_OFF
            cooldownLeft > 0 -> PeekWithheld.COOLDOWN
            pillActive -> PeekWithheld.ALREADY_ACTIVE
            else -> null
        }

        _airlockState.value = AirlockInterceptState(
            isVisible = true,
            interceptedPackageName = packageName,
            interceptedAppName = appName,
            interceptReasonBadge = reasonBadge,
            sessionIntention = active?.intention ?: _intentionText.value,
            selectedReason = DistractionTriggerReason.MUSCLE_MEMORY,
            transmutedParticles = 0,
            canUse2MinutePeek = canPeek,
            peekCooldownRemainingSeconds = cooldownLeft,
            interceptPath = path,
            peekWithheld = withheld,
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
            sessionIsStopwatch = active?.isStopwatchMode == true
        )
    }

    fun selectDistractionReason(reason: DistractionTriggerReason) {
        soundEngine.playRotaryDetent(580.0)
        _airlockState.value = _airlockState.value.copy(selectedReason = reason)
    }

    /**
     * Grants a 2-Minute Peek lease with the Draggable Edge-Snapping Countdown Pill
     * and arms the mandatory 20-minute cooldown in Room storage immediately.
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
            return
        }
        val pkg = state.interceptedPackageName
        val name = state.interceptedAppName.ifBlank { "2m Peek App" }
        soundEngine.playMechanicalLatch(true)
        _airlockState.value = state.copy(isVisible = false)
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
                sessionIntention = state.sessionIntention.ifBlank { _intentionText.value },
                outcome = "Used 2m Peek",
                triggerReason = state.selectedReason.shortTag
            )
        }
    }

    fun end2MinutePeekEarly() {
        val peek = _floatingPeekPill.value
        if (!peek.isActive) return
        soundEngine.playHarmonicSeal(arming = true)
        _floatingPeekPill.value = FloatingPeekPillState(isActive = false)
        viewModelScope.launch {
            repository.recordPeekEnded(peek.packageName)
        }
    }

    fun transmuteImpulseAndReturn() {
        val state = _airlockState.value
        soundEngine.playHarmonicSeal(arming = true)
        _airlockState.value = state.copy(isVisible = false)
        viewModelScope.launch {
            repository.recordIntercept(
                packageName = state.interceptedPackageName.ifBlank { "com.zhiliaoapp.musically" },
                appName = state.interceptedAppName.ifBlank { "Blocked App" },
                sessionIntention = state.sessionIntention.ifBlank { _intentionText.value },
                outcome = "Stayed Focused",
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
            _sevenDayStats.value = repository.compute7DayHistory()
        }
    }

    override fun onCleared() {
        soundEngine.stopAllAudio()
        super.onCleared()
    }

    class Factory(
        private val appContext: Context,
        private val repository: MonolithRepository,
        private val soundEngine: TactileSoundEngine
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MonolithViewModel(appContext, repository, soundEngine) as T
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

