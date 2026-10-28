package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SystemGuardStatus
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.PlusJakartaSansFamily
import java.util.Locale

/**
 * Helper to build mixed-typography strings where words render in Google Sans / PlusJakartaSans
 * and every number renders in Space Mono / JetBrainsMono (DEVELOPER-GUIDE.pdf §2).
 */
@Composable
fun rememberMixedTypographyText(
    wordPrefix: String,
    monoNumber: String,
    wordSuffix: String = "",
    wordColor: Color = MaterialTheme.colorScheme.onBackground,
    monoColor: Color = MaterialTheme.colorScheme.primary
) = buildAnnotatedString {
    if (wordPrefix.isNotEmpty()) {
        withStyle(
            SpanStyle(
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = wordColor
            )
        ) {
            append(wordPrefix)
        }
    }
    if (monoNumber.isNotEmpty()) {
        withStyle(
            SpanStyle(
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                color = monoColor
            )
        ) {
            append(monoNumber)
        }
    }
    if (wordSuffix.isNotEmpty()) {
        withStyle(
            SpanStyle(
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                color = wordColor
            )
        ) {
            append(wordSuffix)
        }
    }
}

/**
 * Full-bleed Section Header with hairline top rule (DEVELOPER-GUIDE.pdf §1.1 & §3).
 */
@Composable
fun FullBleedSectionHeader(
    titleWord: String,
    rightWordPrefix: String = "",
    rightMonoNumber: String = "",
    rightWordSuffix: String = "",
    useAccentOnNumber: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            thickness = 1.dp,
            color = scheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titleWord,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 0.6.sp,
                color = scheme.onSurfaceVariant
            )
            if (rightWordPrefix.isNotEmpty() || rightMonoNumber.isNotEmpty() || rightWordSuffix.isNotEmpty()) {
                Text(
                    text = rememberMixedTypographyText(
                        wordPrefix = rightWordPrefix,
                        monoNumber = rightMonoNumber,
                        wordSuffix = rightWordSuffix,
                        wordColor = scheme.onSurfaceVariant,
                        monoColor = if (useAccentOnNumber) scheme.primaryContainer else scheme.onBackground
                    ),
                    fontSize = 11.5.sp
                )
            }
        }
    }
}

/**
 * Blockit-disciplined 12dp Tactile Action Chip (Ink primary or recessed variant, zero rainbow colors).
 */
@Composable
fun BlockitActionChip(
    wordLabel: String,
    monoBadge: String = "",
    isPrimaryInk: Boolean = false,
    isAccentArmed: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val scheme = MaterialTheme.colorScheme
    val bg = when {
        isAccentArmed -> scheme.primaryContainer
        isPrimaryInk -> scheme.primary
        else -> scheme.surfaceVariant
    }
    val fg = when {
        isAccentArmed -> Color(0xFFFBF1EC)
        isPrimaryInk -> scheme.onPrimary
        else -> scheme.onBackground
    }
    val mod = if (testTag.isNotEmpty()) modifier.testTag(testTag) else modifier

    Box(
        modifier = mod
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rememberMixedTypographyText(
                wordPrefix = wordLabel,
                monoNumber = if (monoBadge.isNotEmpty()) " $monoBadge" else "",
                wordColor = fg,
                monoColor = if (isPrimaryInk && !isAccentArmed) scheme.primaryContainer else fg
            ),
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

/**
 * Top Full-Bleed Architectural Bar (DEVELOPER-GUIDE.pdf §1 & §3).
 */
@Composable
fun MonolithPullDownHeader(
    isDrawerOpen: Boolean,
    heldNotificationsCount: Int,
    guardReadinessCount: Int,
    isSessionActive: Boolean,
    isDarkTheme: Boolean = false,
    onToggleDrawer: () -> Unit,
    onOpenSystemGuardScreen: () -> Unit,
    onToggleTheme: (() -> Unit)? = null
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(scheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Brand dot-matrix red pip + MONOLITH architectural title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggleDrawer)
                    .padding(vertical = 4.dp, horizontal = 4.dp)
                    .testTag("header_drawer_toggle")
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isSessionActive) scheme.primaryContainer else scheme.error)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSessionActive) "MONOLITH • ARMED" else "MONOLITH",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.8.sp,
                    color = scheme.onBackground
                )
            }

            // Right: Held notifications + System Guard + Theme toggle in 12dp recessed pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Held notifications pill
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.surfaceVariant)
                        .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                        .clickable(onClick = onToggleDrawer)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rememberMixedTypographyText(
                            wordPrefix = "HELD ",
                            monoNumber = String.format(Locale.US, "%02d", heldNotificationsCount),
                            wordColor = scheme.onSurfaceVariant,
                            monoColor = if (heldNotificationsCount > 0) scheme.primaryContainer else scheme.onBackground
                        ),
                        fontSize = 11.sp
                    )
                }

                // System Guard readiness pill
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.surfaceVariant)
                        .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenSystemGuardScreen)
                        .padding(horizontal = 10.dp)
                        .testTag("header_guard_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rememberMixedTypographyText(
                            wordPrefix = "GUARD ",
                            monoNumber = "$guardReadinessCount/4",
                            wordColor = scheme.onSurfaceVariant,
                            monoColor = scheme.onBackground
                        ),
                        fontSize = 11.sp
                    )
                }

                if (onToggleTheme != null) {
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(scheme.primary)
                            .clickable(onClick = onToggleTheme)
                            .padding(horizontal = 10.dp)
                            .testTag("header_theme_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDarkTheme || isSessionActive) "NIGHT" else "DAY",
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            color = scheme.onPrimary
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
    }
}

/**
 * Pull-Down System Guard & Telemetry Drawer (Curve C 420ms reveal).
 */
@Composable
fun UnifiedStatusGuardDrawer(
    visible: Boolean,
    systemGuardStatus: SystemGuardStatus,
    shortsSniperEnabled: Boolean,
    browserShieldEnabled: Boolean,
    heldNotificationsCount: Int,
    totalFocusedMinutes: Int,
    totalInterceptions: Int,
    onToggleShortsSniper: () -> Unit,
    onToggleBrowserShield: () -> Unit,
    onOpenNotificationDigest: () -> Unit,
    onOpenStreakMonument: () -> Unit,
    onOpenFullGuardScreen: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(MonolithCurves.specC(MonolithCurves.DURATION_420)) + fadeIn(MonolithCurves.specA()),
        exit = shrinkVertically(MonolithCurves.specA(MonolithCurves.DURATION_260)) + fadeOut(MonolithCurves.specA())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(scheme.surfaceBright)
                .border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SYSTEM CONSOLE",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = scheme.onSurfaceVariant
                )
                Text(
                    text = rememberMixedTypographyText(
                        wordPrefix = "BANKED ",
                        monoNumber = "${totalFocusedMinutes}m",
                        wordSuffix = " • DEFLECTED ",
                        wordColor = scheme.onSurfaceVariant,
                        monoColor = scheme.onBackground
                    ),
                    fontSize = 11.5.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BlockitActionChip(
                    wordLabel = "SHORTS SNIPER",
                    monoBadge = if (shortsSniperEnabled) "ON" else "OFF",
                    isPrimaryInk = shortsSniperEnabled,
                    onClick = onToggleShortsSniper,
                    modifier = Modifier.weight(1f)
                )
                BlockitActionChip(
                    wordLabel = "URL SHIELD",
                    monoBadge = if (browserShieldEnabled) "ON" else "OFF",
                    isPrimaryInk = browserShieldEnabled,
                    onClick = onToggleBrowserShield,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BlockitActionChip(
                    wordLabel = "HELD DIGEST",
                    monoBadge = heldNotificationsCount.toString(),
                    onClick = {
                        onCloseDrawer()
                        onOpenNotificationDigest()
                    },
                    modifier = Modifier.weight(1f)
                )
                BlockitActionChip(
                    wordLabel = "STREAK TOWER",
                    monoBadge = "${(totalFocusedMinutes / 25).coerceAtLeast(1)}",
                    onClick = {
                        onCloseDrawer()
                        onOpenStreakMonument()
                    },
                    modifier = Modifier.weight(1f)
                )
                BlockitActionChip(
                    wordLabel = "PERMISSIONS",
                    isPrimaryInk = true,
                    onClick = {
                        onCloseDrawer()
                        onOpenFullGuardScreen()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
