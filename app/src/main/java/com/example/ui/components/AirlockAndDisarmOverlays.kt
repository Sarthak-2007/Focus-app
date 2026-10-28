package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.CapturedNotificationEntity
import com.example.data.DistractionTriggerReason
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.service.TactileSoundEngine
import com.example.ui.AirlockInterceptState
import com.example.ui.DisarmChallengeState
import com.example.ui.StampInkColor
import com.example.ui.StampPressTicketState
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.NightBlockBarSurface
import com.example.ui.theme.NightColorBackground
import com.example.ui.theme.NightColorPrimary
import com.example.ui.theme.NightColorPrimaryContainer
import com.example.ui.theme.NightColorSurfaceBright
import com.example.ui.theme.NightColorSurfaceVariant
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.views.Emergency60sValveView
import com.example.ui.views.FallingSquaresView
import com.example.ui.views.TowerStreakView
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * 1. ALWAYS-ON NIGHTSTAND DESK CLOCK OVERLAY (Decision #8)
 */
@Composable
fun AlwaysOnDeskClockOverlay(
    visible: Boolean,
    activeSession: FocusSessionEntity?,
    activeTask: FocusTaskEntity?,
    dialMinutes: Int,
    burnInStep: Int,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(MonolithCurves.specB()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        val displaySecs = if (activeSession != null && activeSession.isActive) {
            if (activeSession.isStopwatchMode) activeSession.elapsedSeconds else activeSession.remainingSeconds
        } else {
            dialMinutes * 60
        }
        val mm = displaySecs / 60
        val ss = displaySecs % 60
        val timeText = String.format(Locale.US, "%02d:%02d", mm, ss)

        val progress = if (activeSession != null && activeSession.isActive && !activeSession.isStopwatchMode) {
            (activeSession.remainingSeconds.toFloat() / activeSession.targetDurationSeconds.coerceAtLeast(1)).coerceIn(0.04f, 1f)
        } else {
            0.65f
        }

        val dx = ((burnInStep % 5) - 2) * 3
        val dy = (((burnInStep / 5) % 5) - 2) * 3

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NightBlockBarSurface)
                .clickable(onClick = onDismiss)
                .padding(24.dp)
                .testTag("always_on_desk_clock_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(dx, dy) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = NightColorPrimary.copy(alpha = 0.6f)
                )

                // Hero architectural Space Mono numerals (112sp)
                Text(
                    text = timeText,
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 96.sp,
                    letterSpacing = (-3).sp,
                    color = NightColorPrimary
                )

                // Full-bleed TimelineBar progress strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NightColorSurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NightColorPrimaryContainer)
                    )
                }

                // Active task banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NightColorSurfaceBright)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = activeTask?.title ?: (activeSession?.intention ?: "Deep Focus Session"),
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NightColorPrimary
                    )
                }
            }
        }
    }
}

/**
 * 2. CAPTURED NOTIFICATIONS DIGEST SHEET
 */
@Composable
fun CapturedNotificationsDigestSheet(
    visible: Boolean,
    notifications: List<CapturedNotificationEntity>,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        val scheme = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(onClick = onDismiss)
                .padding(12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(scheme.surfaceBright)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rememberMixedTypographyText(
                            wordPrefix = "HELD NOTIFICATIONS • ",
                            monoNumber = notifications.size.toString(),
                            wordColor = scheme.onBackground,
                            monoColor = scheme.primaryContainer
                        ),
                        fontSize = 14.sp
                    )
                    BlockitActionChip(
                        wordLabel = "CLEAR ALL",
                        onClick = onClearAll
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)

                if (notifications.isEmpty()) {
                    Text(
                        text = "Zero notifications intercepted during focus.",
                        fontFamily = PlusJakartaSansFamily,
                        fontSize = 13.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(220.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications, key = { it.id }) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(scheme.surfaceVariant)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "${item.appName} • ${item.senderTitle}",
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = scheme.onBackground
                                )
                                Text(
                                    text = item.messageBody,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontSize = 12.sp,
                                    color = scheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                BlockitActionChip(
                    wordLabel = "CLOSE DIGEST",
                    isPrimaryInk = true,
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 3. STREAK TOWER MONUMENT OVERLAY (TowerView 7 bricks/floor + FallingSquaresView)
 */
@Composable
fun StreakTowerMonumentOverlay(
    visible: Boolean,
    totalFocusedMinutes: Int,
    completedSessionsCount: Int,
    totalDeflectedCount: Int,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        val scheme = MaterialTheme.colorScheme
        val bricks = ((totalFocusedMinutes / 25) + completedSessionsCount).coerceAtLeast(14)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
                .clickable(onClick = onDismiss)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(scheme.surfaceBright)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {}
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "STREAK TOWER ACCUMULATION",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = scheme.onBackground
                )

                AndroidView(
                    factory = { ctx -> TowerStreakView(ctx) },
                    update = { view ->
                        view.totalBricksCount = bricks
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BlockitActionChip(
                        wordLabel = "FOCUSED",
                        monoBadge = "${totalFocusedMinutes}m",
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                    BlockitActionChip(
                        wordLabel = "DEFLECTED",
                        monoBadge = totalDeflectedCount.toString(),
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                }

                BlockitActionChip(
                    wordLabel = "CLOSE TOWER",
                    isPrimaryInk = true,
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 4. SHIELD PERMISSION PRIMER MODAL
 */
@Composable
fun ShieldPermissionPrimerModal(
    visible: Boolean,
    systemGuardStatus: com.example.data.SystemGuardStatus,
    onOpenAccessibilitySettings: () -> Unit,
    onOpenUsageAccessSettings: () -> Unit,
    onConfirmAndArmAnyway: () -> Unit,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        val scheme = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(onClick = onDismiss)
                .padding(12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(scheme.surfaceBright)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "SYSTEM GUARD PRE-FLIGHT",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = scheme.onBackground
                )
                Text(
                    text = "Grant Accessibility & Usage Access for hardware-level app interception, or arm session immediately.",
                    fontFamily = PlusJakartaSansFamily,
                    fontSize = 12.5.sp,
                    color = scheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BlockitActionChip(
                        wordLabel = "ACCESSIBILITY",
                        onClick = onOpenAccessibilitySettings,
                        modifier = Modifier.weight(1f)
                    )
                    BlockitActionChip(
                        wordLabel = "USAGE STATS",
                        onClick = onOpenUsageAccessSettings,
                        modifier = Modifier.weight(1f)
                    )
                }

                BlockitActionChip(
                    wordLabel = "COMMIT & ARM SESSION NOW",
                    isAccentArmed = true,
                    onClick = onConfirmAndArmAnyway,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "confirm_arm_anyway_button"
                )
            }
        }
    }
}

/**
 * 5. AIRLOCK INTERCEPTION OVERLAY (Decision #16: 5s Breathing Delay Wall)
 */
@Composable
fun AirlockInterceptOverlay(
    state: AirlockInterceptState,
    onSelectReason: (DistractionTriggerReason) -> Unit,
    onTransmuteParticleTap: () -> Unit,
    onReturnToFocus: () -> Unit,
    onTake2MinutePeek: () -> Unit
) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn(MonolithCurves.specB()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        var countdownSecs by remember(state.interceptedPackageName, state.isVisible) {
            mutableIntStateOf(5)
        }
        LaunchedEffect(state.isVisible, state.interceptedPackageName) {
            countdownSecs = 5
            while (countdownSecs > 0) {
                delay(1000L)
                countdownSecs--
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NightColorBackground)
                .padding(20.dp)
                .testTag("airlock_intercept_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(NightColorSurfaceBright)
                    .border(1.dp, NightColorPrimary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "AIRLOCK INTERCEPTION • ${state.interceptReasonBadge.uppercase(Locale.US)}",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = NightColorPrimaryContainer
                )

                Text(
                    text = state.interceptedAppName,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = NightColorPrimary
                )

                // Hero Architectural Countdown Numeral (Space Mono)
                Text(
                    text = if (countdownSecs > 0) String.format(Locale.US, "0%d", countdownSecs) else "00",
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 88.sp,
                    color = if (countdownSecs > 0) NightColorPrimaryContainer else NightColorPrimary
                )

                Text(
                    text = "ACTIVE INTENTION: ${state.sessionIntention}",
                    fontFamily = PlusJakartaSansFamily,
                    fontSize = 12.5.sp,
                    color = NightColorPrimary.copy(alpha = 0.8f)
                )

                BlockitActionChip(
                    wordLabel = "RETURN TO FOCUS",
                    isAccentArmed = true,
                    onClick = onReturnToFocus,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "airlock_return_button"
                )

                if (countdownSecs == 0 && state.canUse2MinutePeek) {
                    BlockitActionChip(
                        wordLabel = "EMERGENCY PEEK",
                        monoBadge = "2m",
                        onClick = onTake2MinutePeek,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "airlock_peek_button"
                    )
                }
            }
        }
    }
}

/**
 * 6. DISARM CHALLENGE OVERLAY (Unbreakable Lock + 1/Week 60s Continuous Hold Valve)
 */
@Composable
fun DisarmChallengeOverlay(
    state: DisarmChallengeState,
    soundEngine: TactileSoundEngine? = null,
    onPhraseChanged: (String) -> Unit,
    onSlidersChanged: (Float, Float) -> Unit,
    onDismissKeepFocusing: () -> Unit,
    onCompleteDisarm: () -> Unit
) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NightColorBackground.copy(alpha = 0.94f))
                .padding(16.dp)
                .testTag("disarm_challenge_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(NightColorSurfaceBright)
                    .border(1.dp, NightColorPrimary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "ONE-WAY RATCHET • EMERGENCY VALVE",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NightColorPrimaryContainer
                )

                Text(
                    text = "Sessions are unbreakable by tap. You have ${state.weeklyEmergencyTokensRemaining} weekly emergency break token remaining. Hold the physical valve continuously for ${state.requiredHoldSeconds} seconds to abort.",
                    fontFamily = PlusJakartaSansFamily,
                    fontSize = 13.sp,
                    color = NightColorPrimary
                )

                AndroidView(
                    factory = { ctx ->
                        Emergency60sValveView(ctx).apply {
                            isNightMode = true
                            this.soundEngine = soundEngine
                        }
                    },
                    update = { view ->
                        view.requiredSeconds = state.requiredHoldSeconds
                        view.tokensRemaining = state.weeklyEmergencyTokensRemaining
                        view.onEmergencyBreakCompleted = onCompleteDisarm
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .testTag("emergency_60s_valve")
                )

                BlockitActionChip(
                    wordLabel = "KEEP FOCUSING (STAY ARMED)",
                    isPrimaryInk = true,
                    onClick = onDismissKeepFocusing,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "keep_focusing_button"
                )
            }
        }
    }
}

/**
 * 7. SESSION RECEIPT & FALLING SQUARES CELEBRATION MODAL (Decision #11 & §4.2 #10)
 */
@Composable
fun StampPressStudioModal(
    state: StampPressTicketState,
    onSelectInk: (StampInkColor) -> Unit,
    onCycleMotif: () -> Unit,
    onTriggerStampPress: () -> Unit,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        val scheme = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
                .padding(16.dp)
                .testTag("stamp_press_receipt_modal"),
            contentAlignment = Alignment.Center
        ) {
            // FallingSquaresView celebration rain over receipt
            AndroidView(
                factory = { ctx ->
                    FallingSquaresView(ctx).apply {
                        triggerCelebrationRain()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(scheme.surfaceBright)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.isPartial) "PARTIAL SESSION RECEIPT" else "VERIFIED SESSION RECEIPT",
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = scheme.primaryContainer
                    )
                    Text(
                        text = state.hmacDigest,
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = scheme.onSurfaceVariant
                    )
                }

                // Hero Architectural Banked Minutes Numeral (Space Mono)
                Text(
                    text = rememberMixedTypographyText(
                        wordPrefix = "+",
                        monoNumber = state.minutesBanked.toString(),
                        wordSuffix = " MIN BANKED",
                        wordColor = scheme.onBackground,
                        monoColor = scheme.primary
                    ),
                    fontSize = 42.sp
                )

                Text(
                    text = state.intention,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = scheme.onBackground
                )

                HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BlockitActionChip(
                        wordLabel = "TASKS DONE",
                        monoBadge = state.tasksCompletedCount.toString(),
                        onClick = onCycleMotif,
                        modifier = Modifier.weight(1f)
                    )
                    BlockitActionChip(
                        wordLabel = "DEFLECTED",
                        monoBadge = state.distractionsDeflected.toString(),
                        onClick = onTriggerStampPress,
                        modifier = Modifier.weight(1f)
                    )
                }

                BlockitActionChip(
                    wordLabel = "BANK RECEIPT TO LEDGER",
                    isAccentArmed = true,
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "dismiss_receipt_button"
                )
            }
        }
    }
}
