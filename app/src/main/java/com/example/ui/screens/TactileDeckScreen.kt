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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.FocusProfileEntity
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.data.FrictionTier
import com.example.data.StudyChecklistItem
import com.example.data.StudyModePreset
import com.example.service.StudySoundscape
import com.example.service.TactileSoundEngine
import com.example.ui.components.BlockitActionChip
import com.example.ui.components.FullBleedSectionHeader
import com.example.ui.components.rememberMixedTypographyText
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.views.BagTilesSuitcaseView
import com.example.ui.views.HapticPresetStripView
import com.example.ui.views.HeroTimerCardView
import com.example.ui.views.RotaryWheelView
import com.example.ui.views.SlideToStartView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * TAB 0: FOCUS COMMAND CONSOLE (DEVELOPER-GUIDE.pdf §1–§11)
 * - Full-bleed stacked sections with 12dp universal spacing.
 * - Single raised card (`HeroTimerCardView` in `colorSurfaceBright`, 24dp radius) at the top.
 * - Self-contained `RotaryWheelView` hero control + `HapticPresetStripView` + `SlideToStartView`.
 * - When armed, transforms into the dark Armed Lockdown state with One-Way Ratchet enforcement.
 */
@Composable
fun TactileDeckScreen(
    activeSession: FocusSessionEntity?,
    dialMinutes: Int,
    studyBlocksCount: Int,
    studyPreset: StudyModePreset,
    activeSoundscape: StudySoundscape,
    checklist: List<StudyChecklistItem>,
    activeTask: FocusTaskEntity?,
    queuedTasks: List<FocusTaskEntity>,
    completedTasksCount: Int,
    totalQueueMinutes: Int,
    selectedFriction: FrictionTier,
    selectedProfileName: String,
    intentionText: String,
    totalFocusedMinutes: Int,
    totalInterceptions: Int,
    profiles: List<FocusProfileEntity>,
    apps: List<BlockedAppEntity>,
    shortsReelsSniperEnabled: Boolean,
    browserUrlBlockerEnabled: Boolean,
    capturedNotificationsCount: Int,
    soundEngine: TactileSoundEngine? = null,
    isDarkTheme: Boolean = false,
    onDialMinutesChanged: (Int) -> Unit,
    onSelectStudyPreset: (StudyModePreset) -> Unit,
    onSetStudyBlocksCount: (Int) -> Unit,
    onSelectStudySoundscape: (StudySoundscape) -> Unit,
    onToggleChecklistItem: (Int) -> Unit,
    onAddChecklistItem: (String) -> Unit,
    onCompleteActiveTask: (Int) -> Unit,
    onDeferActiveTask: (Int) -> Unit,
    onExtendActiveTaskOvertime: () -> Unit,
    onAddQuickTaskToQueue: (String, Int) -> Unit,
    onToggleTaskSubStep: (Int, Int) -> Unit,
    onOpenTaskSequencer: () -> Unit,
    onOpenCurioVault: () -> Unit,
    onFrictionSelected: (FrictionTier) -> Unit,
    onIntentionChanged: (String) -> Unit,
    onProfileSelected: (FocusProfileEntity) -> Unit,
    onArmSession: () -> Unit,
    onRequestDisarm: () -> Unit,
    onCancelWithinGraceWindow: () -> Unit,
    onSimulateIntercept: () -> Unit,
    onOpenAlwaysOnDeskClock: () -> Unit,
    onOpenNotificationDigest: () -> Unit,
    weeklyEmergencyTokensRemaining: Int = 1,
    brownNoiseVolume: Float = 0f,
    rainVolume: Float = 0f,
    droneVolume: Float = 0f,
    onSetSoundMixerVolumes: (Float, Float, Float) -> Unit = { _, _, _ -> }
) {
    val scheme = MaterialTheme.colorScheme
    val isSessionRunning = activeSession != null && activeSession.isActive
    val effectiveNight = isDarkTheme || isSessionRunning
    val blockedCount = remember(apps) { apps.count { it.resolvedTier == AppBlockTier.LOCKED } }

    val isStopwatchMode = if (isSessionRunning) {
        activeSession.isStopwatchMode
    } else {
        studyPreset == StudyModePreset.STOPWATCH_FLOW || dialMinutes < 10
    }

    val displaySeconds = if (isSessionRunning) {
        if (activeSession.isStopwatchMode) activeSession.elapsedSeconds else activeSession.remainingSeconds
    } else {
        dialMinutes * 60
    }

    val minsPart = displaySeconds / 60
    val secsPart = displaySeconds % 60
    val primaryReadout = String.format(Locale.US, "%02d:%02d", minsPart, secsPart)

    val progressFraction = if (isSessionRunning) {
        if (activeSession.isStopwatchMode) {
            (activeSession.elapsedSeconds.toFloat() / 1200f).coerceIn(0.05f, 1f)
        } else {
            val target = activeSession.targetDurationSeconds.coerceAtLeast(1)
            (activeSession.remainingSeconds.toFloat() / target.toFloat()).coerceIn(0.03f, 1f)
        }
    } else {
        ((dialMinutes - 5).toFloat() / 175f).coerceIn(0.04f, 1f)
    }

    val finishPair = remember(dialMinutes, isSessionRunning, displaySeconds, isStopwatchMode) {
        if (isStopwatchMode) {
            "OPEN FLOW • " to "20M FLOOR"
        } else {
            val targetDate = Date(System.currentTimeMillis() + displaySeconds * 1000L)
            val formatter = SimpleDateFormat("h:mm a", Locale.US)
            "FINISHES " to formatter.format(targetDate)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("tactile_deck_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // =====================================================================
        // SECTION 1: THE ONE RAISED CARD — HERO ARCHITECTURAL TIMER CARD (24dp)
        // =====================================================================
        AndroidView(
            factory = { ctx ->
                HeroTimerCardView(ctx).apply {
                    this.soundEngine = soundEngine
                }
            },
            update = { view ->
                view.isNightMode = effectiveNight
                view.soundEngine = soundEngine
                view.primaryReadout = primaryReadout
                view.statusWord = if (isSessionRunning) "ARMED LOCKDOWN • RATCHET SEALED" else "FOCUS DURATION"
                view.finishWordPrefix = finishPair.first
                view.finishMonoTime = finishPair.second
                view.activeTaskTitle = activeTask?.title ?: intentionText
                view.progressFraction = progressFraction
                view.isSessionArmed = isSessionRunning
                view.blockedAppsCount = blockedCount
                view.onTaskBannerClicked = onOpenTaskSequencer
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(216.dp)
                .testTag("hero_timer_card_view")
        )

        // =====================================================================
        // SECTION 2: ACTIVE TASK & MICRO-STEPS BAND (Decision #4)
        // =====================================================================
        if (activeTask != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(scheme.surfaceVariant)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = rememberMixedTypographyText(
                                wordPrefix = "CUED TASK • ",
                                monoNumber = "${activeTask.estimatedMinutes}m",
                                wordSuffix = " EST",
                                wordColor = scheme.onSurfaceVariant,
                                monoColor = scheme.primaryContainer
                            ),
                            fontSize = 10.5.sp
                        )
                        Text(
                            text = activeTask.title,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = scheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BlockitActionChip(
                            wordLabel = "DONE",
                            isPrimaryInk = true,
                            onClick = { onCompleteActiveTask(activeTask.id) },
                            testTag = "complete_active_task_button"
                        )
                        BlockitActionChip(
                            wordLabel = "QUEUE",
                            monoBadge = queuedTasks.size.toString(),
                            onClick = onOpenTaskSequencer
                        )
                    }
                }

                if (activeTask.subSteps.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        activeTask.subSteps.take(3).forEachIndexed { idx, step ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (step.isDone) scheme.primary else scheme.background)
                                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                                    .clickable { onToggleTaskSubStep(activeTask.id, idx) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = step.title,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.5.sp,
                                    color = if (step.isDone) scheme.onPrimary else scheme.onBackground,
                                    textDecoration = if (step.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // =====================================================================
        // SECTION 3: HERO ROTARY WHEEL OR ARMED SUITCASE TELEMETRY
        // =====================================================================
        if (!isSessionRunning) {
            AndroidView(
                factory = { ctx ->
                    RotaryWheelView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = effectiveNight
                    view.soundEngine = soundEngine
                    view.minutesValue = dialMinutes
                    view.studyBlocksCount = studyBlocksCount
                    view.isLockedSession = false
                    view.onMinutesChanged = { mins -> onDialMinutesChanged(mins) }
                    view.onCycleBlocksCount = {
                        val nextBlocks = if (studyBlocksCount >= 4) 1 else studyBlocksCount + 1
                        onSetStudyBlocksCount(nextBlocks)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(216.dp)
                    .testTag("rotary_wheel_view")
            )

            // =================================================================
            // SECTION 4: 5 HAPTIC PRESET TILES (FLOW, 15m, 25m, 45m, 90m)
            // =================================================================
            AndroidView(
                factory = { ctx ->
                    HapticPresetStripView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = effectiveNight
                    view.soundEngine = soundEngine
                    view.selectedMinutes = dialMinutes
                    view.isLockedSession = false
                    view.onPresetSelected = { mins ->
                        if (mins < 10) {
                            onSelectStudyPreset(StudyModePreset.STOPWATCH_FLOW)
                            onDialMinutesChanged(5)
                        } else {
                            onDialMinutesChanged(mins)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("haptic_preset_strip_view")
            )
        } else {
            // =================================================================
            // ARMED LOCKDOWN SUITCASE MATRIX & TELEMETRY CONSOLE
            // =================================================================
            FullBleedSectionHeader(
                titleWord = "SEALED SUITCASE MATRIX",
                rightWordPrefix = "ONE-WAY RATCHET • ",
                rightMonoNumber = "$blockedCount",
                rightWordSuffix = " SHUT",
                useAccentOnNumber = true
            )

            AndroidView(
                factory = { ctx ->
                    BagTilesSuitcaseView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = true
                    view.soundEngine = soundEngine
                    view.appsList = apps
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .testTag("armed_bag_tiles_view")
            )

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
                    wordLabel = "TEST AIRLOCK",
                    onClick = onSimulateIntercept,
                    modifier = Modifier.weight(1f),
                    testTag = "simulate_intercept_button"
                )
                BlockitActionChip(
                    wordLabel = "HELD",
                    monoBadge = capturedNotificationsCount.toString(),
                    onClick = onOpenNotificationDigest,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // =====================================================================
        // SECTION 5: PHYSICAL COMMIT BAR (SLIDE TO START / EMERGENCY VALVE)
        // =====================================================================
        Spacer(modifier = Modifier.height(2.dp))
        if (!isSessionRunning) {
            AndroidView(
                factory = { ctx ->
                    SlideToStartView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = effectiveNight
                    view.soundEngine = soundEngine
                    view.isArmedState = false
                    view.labelText = "SLIDE TO ARM SESSION"
                    view.subMonoBadge = if (dialMinutes < 10) "FLOW" else "${dialMinutes}M"
                    view.onSlideCommitted = onArmSession
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .testTag("slide_to_start_view")
            )
        } else {
            val withinGraceWindow = activeSession != null && activeSession.elapsedSeconds <= 10
            if (withinGraceWindow) {
                BlockitActionChip(
                    wordLabel = "GRACE WINDOW ABORT",
                    monoBadge = "${10 - (activeSession?.elapsedSeconds ?: 0)}s",
                    isAccentArmed = true,
                    onClick = onCancelWithinGraceWindow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
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
                        .height(58.dp),
                    testTag = "request_disarm_button"
                )
            }
        }
    }
}
