package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.CapturedNotificationEntity
import com.example.data.CurioCatalog
import com.example.data.DailyFocusStat
import com.example.data.DistractionTriggerReason
import com.example.data.FocusProfileEntity
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.data.FrictionTier
import com.example.data.InterceptLogEntity
import com.example.data.KineticArtifact
import com.example.data.MonolithRepository
import com.example.data.SmartLoadout
import com.example.data.StudyChecklistItem
import com.example.data.StudyModePreset
import com.example.data.SystemGuardStatus
import com.example.data.TaskQueueStatus
import com.example.service.MonolithShieldService
import com.example.service.StudySoundscape
import com.example.service.TactileSoundEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class MonolithDestination(val route: String, val label: String) {
    DECK("deck", "Focus"),
    TASKS("tasks", "Tasks"),
    SHIELD("shield", "Shield"),
    VAULT("vault", "Receipts"),
    GUARD("guard", "System Guard")
}

data class FloatingPeekPillState(
    val isActive: Boolean = false,
    val packageName: String = "",
    val appName: String = "",
    val secondsRemaining: Int = 120
)

data class AirlockInterceptState(
    val isVisible: Boolean = false,
    val interceptedPackageName: String = "",
    val interceptedAppName: String = "",
    val interceptReasonBadge: String = "Locked",
    val sessionIntention: String = "",
    val frictionTier: FrictionTier = FrictionTier.KINETIC_CIPHER,
    val selectedReason: DistractionTriggerReason = DistractionTriggerReason.MUSCLE_MEMORY,
    val transmutedParticles: Int = 0,
    val canUse2MinutePeek: Boolean = true,
    val peekCooldownRemainingSeconds: Int = 0
)

data class DisarmChallengeState(
    val isVisible: Boolean = false,
    val frictionTier: FrictionTier = FrictionTier.IRON_MONOLITH,
    val isBelowStopwatchFloor: Boolean = false,
    val stopwatchRemainingFloorSeconds: Int = 0,
    val targetPhrase: String = "I AM STOPPING EARLY ON PURPOSE",
    val typedPhrase: String = "",
    val freqSliderA: Float = 0.25f,
    val freqSliderB: Float = 0.80f,
    val weeklyEmergencyTokensRemaining: Int = 1,
    val requiredHoldSeconds: Int = 60
)

enum class StampInkColor(val label: String) {
    CRIMSON("Crimson"),
    ULTRAMARINE("Ultramarine"),
    CANARY("Canary")
}

data class StampPressTicketState(
    val isVisible: Boolean = false,
    val minutesBanked: Int = 25,
    val plannedMinutes: Int = 25,
    val intention: String = "Finish Chapter 4 Problem Set",
    val profileName: String = "Study Loadout",
    val isPartial: Boolean = false,
    val completedTasksSummary: String = "",
    val tasksCompletedCount: Int = 1,
    val distractionsDeflected: Int = 0,
    val hmacDigest: String = "9F4A•C80E•VERIFIED",
    val selectedInk: StampInkColor = StampInkColor.CRIMSON,
    val motifIndex: Int = 0,
    val isStamped: Boolean = true
) {
    val motifName: String
        get() = when (motifIndex % 4) {
            0 -> "Seal: Solar Horizon"
            1 -> "Seal: Weimar Grid"
            2 -> "Seal: Chrono Prism"
            else -> "Seal: Studio Ledger"
        }
}

class MonolithViewModel(
    private val appContext: Context,
    private val repository: MonolithRepository,
    val soundEngine: TactileSoundEngine
) : ViewModel() {

    private val _currentDestination = MutableStateFlow(MonolithDestination.DECK)
    val currentDestination: StateFlow<MonolithDestination> = _currentDestination.asStateFlow()

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val _dialMinutes = MutableStateFlow(50)
    val dialMinutes: StateFlow<Int> = _dialMinutes.asStateFlow()

    private val _studyBlocksCount = MutableStateFlow(2)
    val studyBlocksCount: StateFlow<Int> = _studyBlocksCount.asStateFlow()

    private val _studyPreset = MutableStateFlow(StudyModePreset.POMODORO_25_5)
    val studyPreset: StateFlow<StudyModePreset> = _studyPreset.asStateFlow()

    private val _activeSoundscape = MutableStateFlow(StudySoundscape.OFF)
    val activeSoundscape: StateFlow<StudySoundscape> = _activeSoundscape.asStateFlow()

    private val _brownNoiseVolume = MutableStateFlow(0f)
    val brownNoiseVolume: StateFlow<Float> = _brownNoiseVolume.asStateFlow()

    private val _rainVolume = MutableStateFlow(0f)
    val rainVolume: StateFlow<Float> = _rainVolume.asStateFlow()

    private val _droneVolume = MutableStateFlow(0f)
    val droneVolume: StateFlow<Float> = _droneVolume.asStateFlow()

    private val _selectedFriction = MutableStateFlow(FrictionTier.KINETIC_CIPHER)
    val selectedFriction: StateFlow<FrictionTier> = _selectedFriction.asStateFlow()

    private val _intentionText = MutableStateFlow("Finish Chapter 4 Problem Set")
    val intentionText: StateFlow<String> = _intentionText.asStateFlow()

    private val _shortsReelsSniperEnabled = MutableStateFlow(true)
    val shortsReelsSniperEnabled: StateFlow<Boolean> = _shortsReelsSniperEnabled.asStateFlow()

    private val _browserUrlBlockerEnabled = MutableStateFlow(true)
    val browserUrlBlockerEnabled: StateFlow<Boolean> = _browserUrlBlockerEnabled.asStateFlow()

    private val _preFlightCheckVisible = MutableStateFlow(false)
    val preFlightCheckVisible: StateFlow<Boolean> = _preFlightCheckVisible.asStateFlow()

    private val _isAlwaysOnDeskClockVisible = MutableStateFlow(false)
    val isAlwaysOnDeskClockVisible: StateFlow<Boolean> = _isAlwaysOnDeskClockVisible.asStateFlow()

    private val _burnInFigure8MinuteStep = MutableStateFlow(0)
    val burnInFigure8MinuteStep: StateFlow<Int> = _burnInFigure8MinuteStep.asStateFlow()

    private val _floatingPeekPill = MutableStateFlow(FloatingPeekPillState())
    val floatingPeekPill: StateFlow<FloatingPeekPillState> = _floatingPeekPill.asStateFlow()

    private val _showNotificationDigestSheet = MutableStateFlow(false)
    val showNotificationDigestSheet: StateFlow<Boolean> = _showNotificationDigestSheet.asStateFlow()

    private val _sevenDayStats = MutableStateFlow<List<DailyFocusStat>>(emptyList())
    val sevenDayStats: StateFlow<List<DailyFocusStat>> = _sevenDayStats.asStateFlow()

    private val _encryptedBackupPayload = MutableStateFlow("")
    val encryptedBackupPayload: StateFlow<String> = _encryptedBackupPayload.asStateFlow()

    private val _soundEnabled = MutableStateFlow(soundEngine.soundEnabled)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _deletedTaskForUndo = MutableStateFlow<FocusTaskEntity?>(null)
    val deletedTaskForUndo: StateFlow<FocusTaskEntity?> = _deletedTaskForUndo.asStateFlow()
    private var undoDismissJob: Job? = null

    private val _weeklyEmergencyTokensRemaining = MutableStateFlow(1)
    val weeklyEmergencyTokensRemaining: StateFlow<Int> = _weeklyEmergencyTokensRemaining.asStateFlow()

    private val _ratchetWarningMessage = MutableStateFlow<String?>(null)
    val ratchetWarningMessage: StateFlow<String?> = _ratchetWarningMessage.asStateFlow()
    private var ratchetBannerJob: Job? = null

    private fun triggerRatchetWarning(msg: String) {
        soundEngine.playStampPressSound()
        _ratchetWarningMessage.value = msg
        ratchetBannerJob?.cancel()
        ratchetBannerJob = viewModelScope.launch {
            delay(3600L)
            _ratchetWarningMessage.value = null
        }
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<AppCategory?>(null)
    val selectedCategoryFilter: StateFlow<AppCategory?> = _selectedCategoryFilter.asStateFlow()

    private val _airlockState = MutableStateFlow(AirlockInterceptState())
    val airlockState: StateFlow<AirlockInterceptState> = _airlockState.asStateFlow()

    private val _disarmChallenge = MutableStateFlow(DisarmChallengeState())
    val disarmChallenge: StateFlow<DisarmChallengeState> = _disarmChallenge.asStateFlow()

    private val _stampPressState = MutableStateFlow(StampPressTicketState())
    val stampPressState: StateFlow<StampPressTicketState> = _stampPressState.asStateFlow()

    private val _selectedArtifact = MutableStateFlow(CurioCatalog.artifacts.first())
    val selectedArtifact: StateFlow<KineticArtifact> = _selectedArtifact.asStateFlow()

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

    private val _serialTapCount = MutableStateFlow(0)
    private val _harmonicSynthUnlocked = MutableStateFlow(false)
    val harmonicSynthUnlocked: StateFlow<Boolean> = _harmonicSynthUnlocked.asStateFlow()

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

    val capturedNotifications: StateFlow<List<CapturedNotificationEntity>> = repository.capturedNotificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalFocusedMinutes: StateFlow<Int> = combine(
        sessionHistory,
        activeSession
    ) { history, active ->
        val completedSecs = history.filter { !it.isActive }.sumOf { it.elapsedSeconds }
        val activeSecs = active?.elapsedSeconds ?: 0
        ((completedSecs + activeSecs) / 60).coerceAtLeast(35)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 75)

    val totalInterceptions: StateFlow<Int> = interceptLogs.combine(apps) { logs, appList ->
        val fromApps = appList.sumOf { it.interceptCount }
        maxOf(logs.size, fromApps)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

    init {
        viewModelScope.launch {
            repository.ensureSeededAndSyncPackages()
            refreshSystemGuard()
            _sevenDayStats.value = repository.compute7DayHistory()
        }
        viewModelScope.launch {
            activeTask.collect { task ->
                if (task != null) {
                    _intentionText.value = task.title
                    if (activeSession.value?.isActive != true) {
                        _dialMinutes.value = task.estimatedMinutes.coerceIn(5, 180)
                    }
                }
            }
        }
        startTickerLoop()
    }

    private fun startTickerLoop() {
        var tickSecondCounter = 0
        viewModelScope.launch {
            while (isActive) {
                tickSecondCounter++
                // Shift Always-On Desk Clock figure-8 coordinates every 60 seconds to prevent OLED burn-in
                if (tickSecondCounter % 60 == 0) {
                    _burnInFigure8MinuteStep.value = (_burnInFigure8MinuteStep.value + 1) % 360
                }

                val current = activeSession.value
                if (current != null && current.isActive) {
                    val updated = repository.updateSessionProgress(current)
                    if (!updated.isActive && updated.completedSuccessfully) {
                        soundEngine.playHarmonicSeal(arming = false)
                        soundEngine.setStudySoundscape(StudySoundscape.OFF)
                        _activeSoundscape.value = StudySoundscape.OFF
                        _isAlwaysOnDeskClockVisible.value = false
                        _sevenDayStats.value = repository.compute7DayHistory()
                        val summary = updated.completedTasksSummary.ifBlank {
                            "${updated.intention} (${updated.targetDurationSeconds / 60}m completed)"
                        }
                        _stampPressState.value = StampPressTicketState(
                            isVisible = true,
                            minutesBanked = (updated.elapsedSeconds / 60).coerceAtLeast(1),
                            plannedMinutes = (updated.targetDurationSeconds / 60).coerceAtLeast(1),
                            intention = updated.intention,
                            profileName = updated.profileName,
                            isPartial = false,
                            completedTasksSummary = summary,
                            tasksCompletedCount = updated.tasksCompletedCount.coerceAtLeast(1),
                            distractionsDeflected = updated.phantomReachesBlocked,
                            hmacDigest = updated.hmacSignature.take(12).uppercase().ifBlank { "HMAC•VERIFIED" },
                            selectedInk = StampInkColor.CRIMSON,
                            motifIndex = 0,
                            isStamped = true
                        )
                    }
                }

                // Update Floating 2-Minute Peek Countdown Pill & fire haptic ticks every 15 seconds
                val peek = _floatingPeekPill.value
                if (peek.isActive) {
                    val nextSec = peek.secondsRemaining - 1
                    if (nextSec <= 0) {
                        end2MinutePeekEarly()
                    } else {
                        if (nextSec % 15 == 0) {
                            soundEngine.playRotaryDetent(760.0)
                        }
                        _floatingPeekPill.value = peek.copy(secondsRemaining = nextSec)
                    }
                }

                delay(1000L)
            }
        }
    }

    fun navigateTo(destination: MonolithDestination) {
        soundEngine.playRotaryDetent(540.0)
        _currentDestination.value = destination
        if (destination == MonolithDestination.GUARD) {
            refreshSystemGuard()
        } else if (destination == MonolithDestination.VAULT) {
            viewModelScope.launch {
                _sevenDayStats.value = repository.compute7DayHistory()
            }
        }
    }

    fun toggleTheme() {
        soundEngine.playMechanicalLatch(!_darkTheme.value)
        _darkTheme.value = !_darkTheme.value
    }

    fun toggleSound(): Boolean {
        val next = !soundEngine.soundEnabled
        soundEngine.soundEnabled = next
        _soundEnabled.value = next
        if (next) soundEngine.playRotaryDetent(720.0)
        return next
    }

    fun selectStudySoundscape(soundscape: StudySoundscape) {
        _activeSoundscape.value = soundscape
        soundEngine.setStudySoundscape(soundscape)
        _brownNoiseVolume.value = soundEngine.brownNoiseVolume
        _rainVolume.value = soundEngine.rainVolume
        _droneVolume.value = soundEngine.droneVolume
    }

    fun setSoundMixerVolumes(brown: Float, rain: Float, drone: Float) {
        val b = brown.coerceIn(0f, 1f)
        val r = rain.coerceIn(0f, 1f)
        val d = drone.coerceIn(0f, 1f)
        _brownNoiseVolume.value = b
        _rainVolume.value = r
        _droneVolume.value = d
        soundEngine.setMixerVolumes(b, r, d)
        _activeSoundscape.value = soundEngine.activeSoundscape
    }

    fun selectStudyPreset(preset: StudyModePreset) {
        soundEngine.playMechanicalLatch(true)
        _studyPreset.value = preset
        _dialMinutes.value = preset.workMinutes
        _studyBlocksCount.value = preset.defaultBlocks
    }

    fun setStudyBlocksCount(blocks: Int) {
        soundEngine.playRotaryDetent(640.0)
        _studyBlocksCount.value = blocks.coerceIn(1, 4)
    }

    fun setDialMinutes(minutes: Int) {
        val clamped = minutes.coerceIn(3, 180)
        if (clamped != _dialMinutes.value) {
            val pitch = 440.0 + (clamped * 2.8)
            soundEngine.playRotaryDetent(pitch)
            _dialMinutes.value = clamped
        }
    }

    fun setFrictionTier(tier: FrictionTier) {
        soundEngine.playMechanicalLatch(true)
        _selectedFriction.value = tier
    }

    fun setIntentionText(text: String) {
        _intentionText.value = text
    }

    fun toggleShortsReelsSniper() {
        soundEngine.playMechanicalLatch(!_shortsReelsSniperEnabled.value)
        _shortsReelsSniperEnabled.value = !_shortsReelsSniperEnabled.value
    }

    fun toggleBrowserUrlBlocker() {
        soundEngine.playMechanicalLatch(!_browserUrlBlockerEnabled.value)
        _browserUrlBlockerEnabled.value = !_browserUrlBlockerEnabled.value
    }

    fun toggleAlwaysOnDeskClock(visible: Boolean) {
        soundEngine.playMechanicalLatch(visible)
        _isAlwaysOnDeskClockVisible.value = visible
    }

    fun toggleNotificationDigestSheet(visible: Boolean) {
        soundEngine.playMechanicalLatch(visible)
        _showNotificationDigestSheet.value = visible
    }

    fun clearAllCapturedNotifications() {
        soundEngine.playMechanicalLatch(false)
        viewModelScope.launch {
            repository.clearAllCapturedNotifications()
        }
    }

    fun deleteCapturedNotification(id: Int) {
        soundEngine.playRotaryDetent(520.0)
        viewModelScope.launch {
            repository.deleteCapturedNotification(id)
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

    fun exportEncryptedStateBackup() {
        soundEngine.playHarmonicSeal(true)
        viewModelScope.launch {
            _encryptedBackupPayload.value = repository.exportEncryptedBackup()
        }
    }

    fun toggleChecklistItem(id: Int) {
        completeTaskAndAdvance(id)
    }

    fun addChecklistItem(title: String) {
        addTaskToQueue(title = title, estimatedMinutes = 25, categoryTag = "Study")
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
                if (activeSession.value?.isActive != true) {
                    _dialMinutes.value = created.estimatedMinutes
                }
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
                if (activeSession.value?.isActive != true) {
                    _dialMinutes.value = updated.estimatedMinutes
                }
            }
        }
    }

    fun completeTaskAndAdvance(taskId: Int) {
        soundEngine.playHarmonicSeal(arming = true)
        viewModelScope.launch {
            val nextActive = repository.completeTaskAndAdvance(taskId)
            if (nextActive != null) {
                _intentionText.value = nextActive.title
                if (activeSession.value?.isActive != true) {
                    _dialMinutes.value = nextActive.estimatedMinutes
                }
            }
        }
    }

    fun promoteTaskToActive(taskId: Int) {
        soundEngine.playMechanicalLatch(true)
        viewModelScope.launch {
            val promoted = repository.promoteTaskToActive(taskId)
            if (promoted != null) {
                _intentionText.value = promoted.title
                if (activeSession.value?.isActive != true) {
                    _dialMinutes.value = promoted.estimatedMinutes
                }
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
                _deletedTaskForUndo.value = deleted
                if (nextActive != null) {
                    _intentionText.value = nextActive.title
                }
                undoDismissJob = viewModelScope.launch {
                    delay(5000L)
                    _deletedTaskForUndo.value = null
                }
            }
        }
    }

    fun undoDeleteTask() {
        val deleted = _deletedTaskForUndo.value ?: return
        undoDismissJob?.cancel()
        _deletedTaskForUndo.value = null
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

    fun clearCompletedTasks() {
        soundEngine.playMechanicalLatch(false)
        viewModelScope.launch {
            repository.clearCompletedTasks()
        }
    }

    fun splitActiveTaskAndReturn(microStepTitle: String) {
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
                if (activeSession.value?.isActive != true) {
                    _dialMinutes.value = promoted.estimatedMinutes
                }
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
        val isLockedSession = activeSession.value?.isActive == true
        if (isLockedSession) {
            when (app.resolvedTier) {
                AppBlockTier.OPEN -> setAppBlockTier(app, AppBlockTier.PEEK_2M)
                AppBlockTier.PEEK_2M -> setAppBlockTier(app, AppBlockTier.LOCKED)
                AppBlockTier.LOCKED -> {
                    triggerRatchetWarning("ONE-WAY RATCHET ENGAGED • CANNOT UNLOCK '${app.appName.uppercase()}' UNTIL 00:00")
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
                triggerRatchetWarning("ONE-WAY RATCHET ENGAGED • CANNOT WEAKEN '${app.appName.uppercase()}' DURING ACTIVE VAULT")
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
        if (loadout == SmartLoadout.STUDY) {
            _shortsReelsSniperEnabled.value = true
            _browserUrlBlockerEnabled.value = true
        }
        viewModelScope.launch {
            repository.applySmartLoadout(loadout)
        }
    }

    fun updateAppDailyLimit(app: BlockedAppEntity, minutes: Int) {
        val clamped = minutes.coerceIn(5, 240)
        if (activeSession.value?.isActive == true && clamped > app.dailyLimitMinutes) {
            triggerRatchetWarning("ONE-WAY RATCHET • DAILY QUOTA CAN ONLY BE TIGHTENED DURING LOCKDOWN")
            return
        }
        soundEngine.playRotaryDetent(600.0)
        viewModelScope.launch {
            repository.updateAppDailyLimit(app.packageName, clamped)
        }
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
        val tier = runCatching { FrictionTier.valueOf(profile.frictionTier) }.getOrDefault(FrictionTier.IRON_MONOLITH)
        _selectedFriction.value = tier
        viewModelScope.launch {
            repository.applyProfile(profile)
        }
    }

    fun toggleProfileSchedule(profile: FocusProfileEntity) {
        if (activeSession.value?.isActive == true && profile.isScheduleEnabled) {
            triggerRatchetWarning("ONE-WAY RATCHET • ACTIVE SCHEDULE CANNOT BE DISABLED DURING VAULT LOCKDOWN")
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
        tier: FrictionTier,
        categories: List<AppCategory>,
        startHour: Int,
        endHour: Int
    ) {
        if (name.isBlank()) return
        soundEngine.playHarmonicSeal(true)
        viewModelScope.launch {
            repository.saveCustomProfile(name, minutes, tier, categories, startHour, endHour)
        }
    }

    /**
     * Pre-Flight Accessibility Shield Check + Session Arming:
     * Checks if Accessibility Service is active; if not, offers Pre-Flight prompt or immediate start.
     */
    fun requestArmWithPreFlightCheck() {
        if (!repository.isAccessibilityEnabled()) {
            soundEngine.playMechanicalLatch(true)
            _preFlightCheckVisible.value = true
        } else {
            armMonolithSession()
        }
    }

    fun dismissPreFlightCheck() {
        _preFlightCheckVisible.value = false
    }

    fun armMonolithSession() {
        _preFlightCheckVisible.value = false
        soundEngine.playHarmonicSeal(arming = true)
        viewModelScope.launch {
            repository.startSession(
                intention = _intentionText.value,
                profileName = _selectedProfileName.value,
                frictionTier = _selectedFriction.value,
                durationMinutes = _dialMinutes.value,
                studyBlocksCount = _studyBlocksCount.value
            )
            MonolithShieldService.startShield(appContext)
        }
    }

    fun requestDisarmChallenge() {
        val current = activeSession.value ?: return
        val tier = runCatching { FrictionTier.valueOf(current.frictionTier) }
            .getOrDefault(FrictionTier.KINETIC_CIPHER)
        val isBelowFloor = current.isStopwatchMode &&
            current.elapsedSeconds < MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS
        val remainingFloor = (MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS - current.elapsedSeconds).coerceAtLeast(0)

        // If Stopwatch Mode has passed the mandatory 20-minute floor, allow clean completion
        if (current.isStopwatchMode && !isBelowFloor) {
            soundEngine.playHarmonicSeal(arming = false)
            viewModelScope.launch {
                repository.disarmActiveSession(completed = true, outcomeReason = "Stopwatch Completed")
                MonolithShieldService.stopShield(appContext)
                _sevenDayStats.value = repository.compute7DayHistory()
            }
            return
        }

        soundEngine.playMechanicalLatch(false)
        _disarmChallenge.value = DisarmChallengeState(
            isVisible = true,
            frictionTier = FrictionTier.IRON_MONOLITH,
            isBelowStopwatchFloor = isBelowFloor,
            stopwatchRemainingFloorSeconds = remainingFloor,
            targetPhrase = "I AM STOPPING EARLY ON PURPOSE",
            typedPhrase = "",
            freqSliderA = 0.22f,
            freqSliderB = 0.84f,
            weeklyEmergencyTokensRemaining = _weeklyEmergencyTokensRemaining.value,
            requiredHoldSeconds = 60
        )
    }

    fun updateDisarmPhrase(input: String) {
        _disarmChallenge.value = _disarmChallenge.value.copy(typedPhrase = input.uppercase())
    }

    fun updateDisarmSliders(sliderA: Float, sliderB: Float) {
        _disarmChallenge.value = _disarmChallenge.value.copy(
            freqSliderA = sliderA.coerceIn(0f, 1f),
            freqSliderB = sliderB.coerceIn(0f, 1f)
        )
    }

    fun dismissDisarmChallenge() {
        _disarmChallenge.value = _disarmChallenge.value.copy(isVisible = false)
    }

    fun confirmDisarmSession(reason: String = "Ended Early") {
        val current = activeSession.value
        val isGraceCancel = reason == "Accidental Start Grace Cancel"
        if (!isGraceCancel && _weeklyEmergencyTokensRemaining.value > 0) {
            _weeklyEmergencyTokensRemaining.value = (_weeklyEmergencyTokensRemaining.value - 1).coerceAtLeast(0)
        }
        val wasPartialCredit = current != null &&
            current.elapsedSeconds >= 60 &&
            !isGraceCancel
        soundEngine.playHarmonicSeal(arming = false)
        soundEngine.setStudySoundscape(StudySoundscape.OFF)
        _activeSoundscape.value = StudySoundscape.OFF
        _isAlwaysOnDeskClockVisible.value = false
        _disarmChallenge.value = DisarmChallengeState(
            isVisible = false,
            weeklyEmergencyTokensRemaining = _weeklyEmergencyTokensRemaining.value
        )
        viewModelScope.launch {
            repository.disarmActiveSession(completed = false, outcomeReason = reason)
            MonolithShieldService.stopShield(appContext)
            _sevenDayStats.value = repository.compute7DayHistory()
            if (wasPartialCredit) {
                val summary = current.completedTasksSummary.ifBlank {
                    "${current.intention} (${(current.elapsedSeconds / 60).coerceAtLeast(1)}m partial block)"
                }
                _stampPressState.value = StampPressTicketState(
                    isVisible = true,
                    minutesBanked = (current.elapsedSeconds / 60).coerceAtLeast(1),
                    plannedMinutes = (current.targetDurationSeconds / 60).coerceAtLeast(1),
                    intention = current.intention,
                    profileName = current.profileName,
                    isPartial = true,
                    completedTasksSummary = summary,
                    tasksCompletedCount = current.tasksCompletedCount,
                    distractionsDeflected = current.phantomReachesBlocked,
                    hmacDigest = current.hmacSignature.take(12).uppercase().ifBlank { "HMAC•VERIFIED" },
                    selectedInk = StampInkColor.CANARY,
                    motifIndex = 1,
                    isStamped = true
                )
            }
        }
    }

    fun openStampPressStudio(minutes: Int = _dialMinutes.value, isPartial: Boolean = false) {
        soundEngine.playMechanicalLatch(true)
        val latestSession = sessionHistory.value.firstOrNull()
        _stampPressState.value = StampPressTicketState(
            isVisible = true,
            minutesBanked = latestSession?.let { (it.elapsedSeconds / 60).coerceAtLeast(1) } ?: minutes.coerceAtLeast(1),
            plannedMinutes = latestSession?.let { (it.targetDurationSeconds / 60).coerceAtLeast(1) } ?: minutes.coerceAtLeast(1),
            intention = latestSession?.intention ?: _intentionText.value,
            profileName = latestSession?.profileName ?: _selectedProfileName.value,
            isPartial = isPartial,
            completedTasksSummary = latestSession?.completedTasksSummary?.ifBlank { _intentionText.value } ?: _intentionText.value,
            tasksCompletedCount = latestSession?.tasksCompletedCount?.coerceAtLeast(1) ?: 1,
            distractionsDeflected = latestSession?.phantomReachesBlocked ?: 0,
            hmacDigest = latestSession?.hmacSignature?.take(12)?.uppercase()?.ifBlank { "9F4A•C80E•SEAL" } ?: "9F4A•C80E•SEAL",
            selectedInk = _stampPressState.value.selectedInk,
            motifIndex = _stampPressState.value.motifIndex,
            isStamped = true
        )
    }

    fun selectStampInk(ink: StampInkColor) {
        soundEngine.playRotaryDetent(640.0)
        _stampPressState.value = _stampPressState.value.copy(selectedInk = ink)
    }

    fun cycleStampMotif() {
        soundEngine.playRotaryDetent(720.0)
        _stampPressState.value = _stampPressState.value.copy(
            motifIndex = (_stampPressState.value.motifIndex + 1) % 4
        )
    }

    fun pressStampPlunger() {
        soundEngine.playStampPressSound()
        _stampPressState.value = _stampPressState.value.copy(isStamped = true)
    }

    fun dismissStampPress() {
        soundEngine.playMechanicalLatch(false)
        _stampPressState.value = _stampPressState.value.copy(isVisible = false)
    }

    fun triggerAirlockIntercept(
        packageName: String,
        appName: String,
        reasonBadge: String = "Locked"
    ) {
        soundEngine.playMechanicalLatch(true)
        val active = activeSession.value
        val tier = active?.let {
            runCatching { FrictionTier.valueOf(it.frictionTier) }.getOrNull()
        } ?: _selectedFriction.value

        val appEntity = apps.value.find { it.packageName == packageName }
        val cooldownLeft = if (appEntity != null) repository.getPeekCooldownRemainingSeconds(appEntity) else 0
        val canPeek = (appEntity == null || appEntity.resolvedTier == AppBlockTier.PEEK_2M) &&
            cooldownLeft == 0 &&
            !_floatingPeekPill.value.isActive

        _airlockState.value = AirlockInterceptState(
            isVisible = true,
            interceptedPackageName = packageName,
            interceptedAppName = appName,
            interceptReasonBadge = reasonBadge,
            sessionIntention = active?.intention ?: _intentionText.value,
            frictionTier = tier,
            selectedReason = DistractionTriggerReason.MUSCLE_MEMORY,
            transmutedParticles = 0,
            canUse2MinutePeek = canPeek,
            peekCooldownRemainingSeconds = cooldownLeft
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
        val pkg = state.interceptedPackageName.ifBlank { "com.whatsapp" }
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

    fun selectArtifact(artifact: KineticArtifact) {
        soundEngine.playRotaryDetent(680.0)
        _selectedArtifact.value = artifact
    }

    fun tapSerialPlateEasterEgg() {
        val next = _serialTapCount.value + 1
        _serialTapCount.value = next
        soundEngine.playRotaryDetent(420.0 + next * 95.0)
        if (next >= 5) {
            _harmonicSynthUnlocked.value = !_harmonicSynthUnlocked.value
            _serialTapCount.value = 0
            soundEngine.playHarmonicSeal(true)
        }
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
