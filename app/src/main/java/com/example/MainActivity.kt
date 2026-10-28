package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppBlockTier
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.MonolithShieldService
import com.example.service.TactileSoundEngine
import com.example.ui.MonolithDestination
import com.example.ui.MonolithViewModel
import com.example.ui.components.AirlockInterceptOverlay
import com.example.ui.components.AlwaysOnDeskClockOverlay
import com.example.ui.components.CapturedNotificationsDigestSheet
import com.example.ui.components.DisarmChallengeOverlay
import com.example.ui.components.MonolithPullDownHeader
import com.example.ui.components.ShieldPermissionPrimerModal
import com.example.ui.components.StampPressStudioModal
import com.example.ui.components.StreakTowerMonumentOverlay
import com.example.ui.components.UnifiedStatusGuardDrawer
import com.example.ui.screens.CurioVaultScreen
import com.example.ui.screens.FocusSequencerScreen
import com.example.ui.screens.ShieldMatrixScreen
import com.example.ui.screens.SystemGuardScreen
import com.example.ui.screens.TactileDeckScreen
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.views.MonolithBottomNavView

class MainActivity : ComponentActivity() {

    private lateinit var soundEngine: TactileSoundEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )

        val database = MonolithDatabase.getInstance(applicationContext)
        val repository = MonolithRepository(applicationContext, database.monolithDao())
        soundEngine = TactileSoundEngine(applicationContext)

        setContent {
            val viewModel: MonolithViewModel = viewModel(
                factory = MonolithViewModel.Factory(
                    appContext = applicationContext,
                    repository = repository,
                    soundEngine = soundEngine
                )
            )
            val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
            val activeSessionForTheme by viewModel.activeSession.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                handleIncomingInterceptIntent(intent, viewModel)
            }

            MyApplicationTheme(
                darkTheme = darkTheme,
                dynamicColor = false,
                isFocusSessionActive = activeSessionForTheme?.isActive == true
            ) {
                MonolithRootApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun handleIncomingInterceptIntent(intent: Intent?, viewModel: MonolithViewModel) {
        if (intent == null) return
        val pkg = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPTED_PACKAGE)
        if (!pkg.isNullOrBlank()) {
            val name = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPTED_APP_NAME)
                ?: "Short-Form Feed"
            val badge = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPT_BADGE)
                ?: "Locked"
            viewModel.triggerAirlockIntercept(pkg, name, badge)
            intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPTED_PACKAGE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::soundEngine.isInitialized) {
            soundEngine.stopAllAudio()
        }
    }
}

@Composable
fun MonolithRootApp(viewModel: MonolithViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
    val dialMinutes by viewModel.dialMinutes.collectAsStateWithLifecycle()
    val studyBlocksCount by viewModel.studyBlocksCount.collectAsStateWithLifecycle()
    val studyPreset by viewModel.studyPreset.collectAsStateWithLifecycle()
    val activeSoundscape by viewModel.activeSoundscape.collectAsStateWithLifecycle()
    val brownNoiseVolume by viewModel.brownNoiseVolume.collectAsStateWithLifecycle()
    val rainVolume by viewModel.rainVolume.collectAsStateWithLifecycle()
    val droneVolume by viewModel.droneVolume.collectAsStateWithLifecycle()
    val checklist by viewModel.checklist.collectAsStateWithLifecycle()
    val activeTask by viewModel.activeTask.collectAsStateWithLifecycle()
    val queuedTasks by viewModel.queuedTasks.collectAsStateWithLifecycle()
    val completedTasks by viewModel.completedTasks.collectAsStateWithLifecycle()
    val deferredTasks by viewModel.deferredTasks.collectAsStateWithLifecycle()
    val deletedTaskForUndo by viewModel.deletedTaskForUndo.collectAsStateWithLifecycle()
    val totalQueueMinutes by viewModel.totalQueueMinutes.collectAsStateWithLifecycle()
    val selectedFriction by viewModel.selectedFriction.collectAsStateWithLifecycle()
    val selectedProfileName by viewModel.selectedProfileName.collectAsStateWithLifecycle()
    val intentionText by viewModel.intentionText.collectAsStateWithLifecycle()
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val allApps by viewModel.apps.collectAsStateWithLifecycle()
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val shortsReelsSniperEnabled by viewModel.shortsReelsSniperEnabled.collectAsStateWithLifecycle()
    val browserUrlBlockerEnabled by viewModel.browserUrlBlockerEnabled.collectAsStateWithLifecycle()
    val preFlightCheckVisible by viewModel.preFlightCheckVisible.collectAsStateWithLifecycle()
    val isAlwaysOnDeskClockVisible by viewModel.isAlwaysOnDeskClockVisible.collectAsStateWithLifecycle()
    val burnInFigure8MinuteStep by viewModel.burnInFigure8MinuteStep.collectAsStateWithLifecycle()
    val showNotificationDigestSheet by viewModel.showNotificationDigestSheet.collectAsStateWithLifecycle()
    val capturedNotifications by viewModel.capturedNotifications.collectAsStateWithLifecycle()
    val sevenDayStats by viewModel.sevenDayStats.collectAsStateWithLifecycle()
    val encryptedBackupPayload by viewModel.encryptedBackupPayload.collectAsStateWithLifecycle()
    val totalFocusedMinutes by viewModel.totalFocusedMinutes.collectAsStateWithLifecycle()
    val totalInterceptions by viewModel.totalInterceptions.collectAsStateWithLifecycle()
    val selectedArtifact by viewModel.selectedArtifact.collectAsStateWithLifecycle()
    val sessionHistory by viewModel.sessionHistory.collectAsStateWithLifecycle()
    val interceptLogs by viewModel.interceptLogs.collectAsStateWithLifecycle()
    val harmonicSynthUnlocked by viewModel.harmonicSynthUnlocked.collectAsStateWithLifecycle()
    val systemGuardStatus by viewModel.systemGuardStatus.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val airlockState by viewModel.airlockState.collectAsStateWithLifecycle()
    val disarmChallenge by viewModel.disarmChallenge.collectAsStateWithLifecycle()
    val stampPressState by viewModel.stampPressState.collectAsStateWithLifecycle()
    val weeklyEmergencyTokensRemaining by viewModel.weeklyEmergencyTokensRemaining.collectAsStateWithLifecycle()
    val ratchetWarningMessage by viewModel.ratchetWarningMessage.collectAsStateWithLifecycle()

    var showUnifiedStatusDrawer by remember { mutableStateOf(false) }
    var showStreakMonument by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshSystemGuard()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (showUnifiedStatusDrawer) {
        BackHandler { showUnifiedStatusDrawer = false }
    } else if (showStreakMonument) {
        BackHandler { showStreakMonument = false }
    } else if (currentDestination != MonolithDestination.DECK) {
        BackHandler { viewModel.navigateTo(MonolithDestination.DECK) }
    }

    val guardReadinessCount = listOf(
        systemGuardStatus.isAccessibilityServiceEnabled,
        systemGuardStatus.hasUsageStatsPermission,
        systemGuardStatus.isDeviceAdminEnabled,
        systemGuardStatus.isNotificationListenerEnabled
    ).count { it }

    val blockedAppsCount = remember(allApps) {
        allApps.count { it.resolvedTier == AppBlockTier.LOCKED }
    }
    val activeTasksCount = remember(activeTask, queuedTasks) {
        (if (activeTask != null) 1 else 0) + queuedTasks.size
    }
    val effectiveNight = darkTheme || (activeSession?.isActive == true)

    val selectedTabIndex = when (currentDestination) {
        MonolithDestination.DECK -> 0
        MonolithDestination.SHIELD -> 1
        MonolithDestination.TASKS -> 2
        MonolithDestination.VAULT -> 3
        MonolithDestination.GUARD -> 1
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Architectural Bar
                MonolithPullDownHeader(
                    isDrawerOpen = showUnifiedStatusDrawer,
                    heldNotificationsCount = capturedNotifications.size,
                    guardReadinessCount = guardReadinessCount,
                    isSessionActive = activeSession?.isActive == true,
                    isDarkTheme = effectiveNight,
                    onToggleDrawer = { showUnifiedStatusDrawer = !showUnifiedStatusDrawer },
                    onOpenSystemGuardScreen = { viewModel.navigateTo(MonolithDestination.GUARD) },
                    onToggleTheme = { viewModel.toggleTheme() }
                )

                // Pull-Down System Console Drawer
                UnifiedStatusGuardDrawer(
                    visible = showUnifiedStatusDrawer,
                    systemGuardStatus = systemGuardStatus,
                    shortsSniperEnabled = shortsReelsSniperEnabled,
                    browserShieldEnabled = browserUrlBlockerEnabled,
                    heldNotificationsCount = capturedNotifications.size,
                    totalFocusedMinutes = totalFocusedMinutes,
                    totalInterceptions = totalInterceptions,
                    onToggleShortsSniper = { viewModel.toggleShortsReelsSniper() },
                    onToggleBrowserShield = { viewModel.toggleBrowserUrlBlocker() },
                    onOpenNotificationDigest = { viewModel.toggleNotificationDigestSheet(true) },
                    onOpenStreakMonument = { showStreakMonument = true },
                    onOpenFullGuardScreen = { viewModel.navigateTo(MonolithDestination.GUARD) },
                    onCloseDrawer = { showUnifiedStatusDrawer = false }
                )

                // Main Screen Viewport (Curve B / Curve A transitions)
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = currentDestination,
                        transitionSpec = {
                            fadeIn(MonolithCurves.specB()) togetherWith fadeOut(MonolithCurves.specA())
                        },
                        label = "monolith_tab_transition"
                    ) { destination ->
                        when (destination) {
                            MonolithDestination.DECK -> {
                                TactileDeckScreen(
                                    activeSession = activeSession,
                                    dialMinutes = dialMinutes,
                                    studyBlocksCount = studyBlocksCount,
                                    studyPreset = studyPreset,
                                    activeSoundscape = activeSoundscape,
                                    checklist = checklist,
                                    activeTask = activeTask,
                                    queuedTasks = queuedTasks,
                                    completedTasksCount = completedTasks.size,
                                    totalQueueMinutes = totalQueueMinutes,
                                    selectedFriction = selectedFriction,
                                    selectedProfileName = selectedProfileName,
                                    intentionText = intentionText,
                                    totalFocusedMinutes = totalFocusedMinutes,
                                    totalInterceptions = totalInterceptions,
                                    profiles = profiles,
                                    apps = allApps,
                                    shortsReelsSniperEnabled = shortsReelsSniperEnabled,
                                    browserUrlBlockerEnabled = browserUrlBlockerEnabled,
                                    capturedNotificationsCount = capturedNotifications.size,
                                    soundEngine = viewModel.soundEngine,
                                    isDarkTheme = effectiveNight,
                                    onDialMinutesChanged = viewModel::setDialMinutes,
                                    onSelectStudyPreset = viewModel::selectStudyPreset,
                                    onSetStudyBlocksCount = viewModel::setStudyBlocksCount,
                                    onSelectStudySoundscape = viewModel::selectStudySoundscape,
                                    onToggleChecklistItem = viewModel::toggleChecklistItem,
                                    onAddChecklistItem = viewModel::addChecklistItem,
                                    onCompleteActiveTask = viewModel::completeTaskAndAdvance,
                                    onDeferActiveTask = viewModel::deferTask,
                                    onExtendActiveTaskOvertime = { viewModel.extendActiveTaskOvertime(5) },
                                    onAddQuickTaskToQueue = { title, mins ->
                                        viewModel.addTaskToQueue(title, mins, "Study", "", false)
                                    },
                                    onToggleTaskSubStep = viewModel::toggleTaskSubStep,
                                    onOpenTaskSequencer = { viewModel.navigateTo(MonolithDestination.TASKS) },
                                    onOpenCurioVault = { viewModel.navigateTo(MonolithDestination.VAULT) },
                                    onFrictionSelected = viewModel::setFrictionTier,
                                    onIntentionChanged = viewModel::setIntentionText,
                                    onProfileSelected = viewModel::selectAndApplyProfile,
                                    onArmSession = viewModel::requestArmWithPreFlightCheck,
                                    onRequestDisarm = viewModel::requestDisarmChallenge,
                                    onCancelWithinGraceWindow = {
                                        viewModel.confirmDisarmSession("Accidental Start Grace Cancel")
                                    },
                                    onSimulateIntercept = {
                                        viewModel.triggerAirlockIntercept(
                                            "com.zhiliaoapp.musically",
                                            "TikTok",
                                            "Suitcase Locked"
                                        )
                                    },
                                    onOpenAlwaysOnDeskClock = { viewModel.toggleAlwaysOnDeskClock(true) },
                                    onOpenNotificationDigest = { viewModel.toggleNotificationDigestSheet(true) },
                                    weeklyEmergencyTokensRemaining = weeklyEmergencyTokensRemaining,
                                    brownNoiseVolume = brownNoiseVolume,
                                    rainVolume = rainVolume,
                                    droneVolume = droneVolume,
                                    onSetSoundMixerVolumes = viewModel::setSoundMixerVolumes
                                )
                            }

                            MonolithDestination.SHIELD -> {
                                ShieldMatrixScreen(
                                    allApps = allApps,
                                    filteredApps = filteredApps,
                                    searchQuery = searchQuery,
                                    selectedCategory = selectedCategory,
                                    shortsReelsSniperEnabled = shortsReelsSniperEnabled,
                                    browserUrlBlockerEnabled = browserUrlBlockerEnabled,
                                    profiles = profiles,
                                    selectedProfileName = selectedProfileName,
                                    isSessionActive = activeSession?.isActive == true,
                                    ratchetWarningMessage = ratchetWarningMessage,
                                    soundEngine = viewModel.soundEngine,
                                    isDarkTheme = effectiveNight,
                                    onSearchQueryChanged = viewModel::updateSearchQuery,
                                    onSelectCategory = viewModel::selectCategoryFilter,
                                    onCycleAppTier = viewModel::cycleAppBlockTier,
                                    onSetAppTier = { pkg, tier ->
                                        val app = allApps.find { it.packageName == pkg }
                                        if (app != null) {
                                            viewModel.setAppBlockTier(app, tier)
                                        }
                                    },
                                    onApplySmartLoadout = viewModel::applySmartLoadout,
                                    onSelectProfile = viewModel::selectAndApplyProfile,
                                    onToggleShortsReelsSniper = viewModel::toggleShortsReelsSniper,
                                    onToggleBrowserUrlBlocker = viewModel::toggleBrowserUrlBlocker
                                )
                            }

                            MonolithDestination.TASKS -> {
                                FocusSequencerScreen(
                                    activeTask = activeTask,
                                    queuedTasks = queuedTasks,
                                    completedTasks = completedTasks,
                                    deferredTasks = deferredTasks,
                                    deletedTaskForUndo = deletedTaskForUndo,
                                    totalQueueMinutes = totalQueueMinutes,
                                    isSessionActive = activeSession?.isActive == true,
                                    soundEngine = viewModel.soundEngine,
                                    isDarkTheme = effectiveNight,
                                    onAddTask = viewModel::addTaskToQueue,
                                    onEditTask = viewModel::editTask,
                                    onUpdateTaskDuration = viewModel::updateTaskDuration,
                                    onCueTaskToFocus = viewModel::cueTaskAndNavigateToFocus,
                                    onCompleteTask = viewModel::completeTaskAndAdvance,
                                    onPromoteTask = viewModel::promoteTaskToActive,
                                    onMoveTaskOrder = viewModel::moveTaskOrder,
                                    onDeferTask = viewModel::deferTask,
                                    onDeleteTask = viewModel::deleteTaskWithUndo,
                                    onUndoDeleteTask = viewModel::undoDeleteTask,
                                    onDismissUndoBanner = viewModel::dismissUndoDeleteBanner,
                                    onClearCompleted = viewModel::clearCompletedTasks,
                                    onToggleSubStep = viewModel::toggleTaskSubStep,
                                    onAddSubStep = viewModel::addTaskSubStep,
                                    onAutoSplitTask = viewModel::autoSplitTaskIntoMicroSteps,
                                    onNavigateToDeck = { viewModel.navigateTo(MonolithDestination.DECK) }
                                )
                            }

                            MonolithDestination.VAULT -> {
                                CurioVaultScreen(
                                    totalFocusedMinutes = totalFocusedMinutes,
                                    totalInterceptions = totalInterceptions,
                                    selectedArtifact = selectedArtifact,
                                    sessionHistory = sessionHistory,
                                    interceptLogs = interceptLogs,
                                    sevenDayStats = sevenDayStats,
                                    harmonicSynthUnlocked = harmonicSynthUnlocked,
                                    encryptedBackupPayload = encryptedBackupPayload,
                                    soundEngine = viewModel.soundEngine,
                                    isDarkTheme = effectiveNight,
                                    onSelectArtifact = viewModel::selectArtifact,
                                    onPlayHarmonicChord = { idx ->
                                        viewModel.soundEngine.playRotaryDetent(440.0 + idx * 65.0)
                                    },
                                    onRefreshBackup = viewModel::exportEncryptedStateBackup,
                                    onImportBackupJson = { _ ->
                                        viewModel.exportEncryptedStateBackup()
                                    }
                                )
                            }

                            MonolithDestination.GUARD -> {
                                SystemGuardScreen(
                                    status = systemGuardStatus,
                                    soundEnabled = soundEnabled,
                                    shortsReelsSniperEnabled = shortsReelsSniperEnabled,
                                    browserUrlBlockerEnabled = browserUrlBlockerEnabled,
                                    soundEngine = viewModel.soundEngine,
                                    isDarkTheme = effectiveNight,
                                    onRefreshStatus = viewModel::refreshSystemGuard,
                                    onToggleSound = { viewModel.toggleSound() },
                                    onToggleShortsReelsSniper = viewModel::toggleShortsReelsSniper,
                                    onToggleBrowserUrlBlocker = viewModel::toggleBrowserUrlBlocker,
                                    onOpenAccessibilitySettings = {
                                        runCatching {
                                            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                                        }
                                    },
                                    onOpenUsageAccessSettings = {
                                        runCatching {
                                            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                                        }
                                    },
                                    onOpenOverlaySettings = {
                                        runCatching {
                                            context.startActivity(
                                                Intent(
                                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                    Uri.parse("package:${context.packageName}")
                                                )
                                            )
                                        }
                                    },
                                    onOpenBatteryOptimizationSettings = {
                                        runCatching {
                                            context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                                        }
                                    },
                                    onOpenNotificationListenerSettings = {
                                        runCatching {
                                            context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // =============================================================
                // CUSTOM 80DP BOTTOM NAVIGATION CAPSULE (DEVELOPER-GUIDE.pdf §3 & §8)
                // =============================================================
                AndroidView(
                    factory = { ctx ->
                        MonolithBottomNavView(ctx).apply {
                            this.soundEngine = viewModel.soundEngine
                        }
                    },
                    update = { view ->
                        view.isNightMode = effectiveNight
                        view.soundEngine = viewModel.soundEngine
                        view.selectedTabIndex = selectedTabIndex
                        view.blockedCountBadge = blockedAppsCount
                        view.tasksCountBadge = activeTasksCount
                        view.isSessionArmed = activeSession?.isActive == true
                        view.onTabSelected = { idx ->
                            val dest = when (idx) {
                                0 -> MonolithDestination.DECK
                                1 -> MonolithDestination.SHIELD
                                2 -> MonolithDestination.TASKS
                                else -> MonolithDestination.VAULT
                            }
                            viewModel.navigateTo(dest)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("bottom_nav_capsule_view")
                )
            }

            // =================================================================
            // FULL-SCREEN OVERLAYS & MODALS
            // =================================================================
            AlwaysOnDeskClockOverlay(
                visible = isAlwaysOnDeskClockVisible,
                activeSession = activeSession,
                activeTask = activeTask,
                dialMinutes = dialMinutes,
                burnInStep = burnInFigure8MinuteStep,
                onDismiss = { viewModel.toggleAlwaysOnDeskClock(false) }
            )

            CapturedNotificationsDigestSheet(
                visible = showNotificationDigestSheet,
                notifications = capturedNotifications,
                onClearAll = viewModel::clearAllCapturedNotifications,
                onDismiss = { viewModel.toggleNotificationDigestSheet(false) }
            )

            StreakTowerMonumentOverlay(
                visible = showStreakMonument,
                totalFocusedMinutes = totalFocusedMinutes,
                completedSessionsCount = sessionHistory.count { it.completedSuccessfully },
                totalDeflectedCount = totalInterceptions,
                onDismiss = { showStreakMonument = false }
            )

            ShieldPermissionPrimerModal(
                visible = preFlightCheckVisible,
                systemGuardStatus = systemGuardStatus,
                onOpenAccessibilitySettings = {
                    runCatching { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
                },
                onOpenUsageAccessSettings = {
                    runCatching { context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
                },
                onConfirmAndArmAnyway = viewModel::armMonolithSession,
                onDismiss = viewModel::dismissPreFlightCheck
            )

            AirlockInterceptOverlay(
                state = airlockState,
                onSelectReason = viewModel::selectDistractionReason,
                onTransmuteParticleTap = viewModel::incrementAirlockParticlePulse,
                onReturnToFocus = viewModel::transmuteImpulseAndReturn,
                onTake2MinutePeek = viewModel::start2MinutePeek
            )

            DisarmChallengeOverlay(
                state = disarmChallenge,
                soundEngine = viewModel.soundEngine,
                onPhraseChanged = viewModel::updateDisarmPhrase,
                onSlidersChanged = viewModel::updateDisarmSliders,
                onDismissKeepFocusing = viewModel::dismissDisarmChallenge,
                onCompleteDisarm = { viewModel.confirmDisarmSession("60s Emergency Valve Disarm") }
            )

            StampPressStudioModal(
                state = stampPressState,
                onSelectInk = viewModel::selectStampInk,
                onCycleMotif = viewModel::cycleStampMotif,
                onTriggerStampPress = viewModel::pressStampPlunger,
                onDismiss = viewModel::dismissStampPress
            )
        }
    }
}
