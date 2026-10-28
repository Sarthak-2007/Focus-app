package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.DailyFocusStat
import com.example.data.FocusSessionEntity
import com.example.data.InterceptLogEntity
import com.example.data.KineticArtifact
import com.example.service.TactileSoundEngine
import com.example.ui.components.BlockitActionChip
import com.example.ui.components.FullBleedSectionHeader
import com.example.ui.components.rememberMixedTypographyText
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.views.DayFadersView
import com.example.ui.views.TileNumberCounterView
import com.example.ui.views.TowerStreakView

/**
 * TAB 3: LEDGER & STREAK TOWER (DEVELOPER-GUIDE.pdf §4.2 #8 TileNumberView, #9 TowerView, #10 DayFaders)
 * - Full-bleed stacked sections with 12dp universal spacing.
 * - 2x2 `TileNumberCounterView` 4dp dot-matrix counter readouts.
 * - `TowerStreakView` (7 bricks per floor physical streak monument).
 * - `DayFadersView` (7-day vertical fader columns with Space Mono numerals).
 * - Verified Session Receipts ledger & AES-256-GCM encrypted backup export.
 */
@Composable
fun CurioVaultScreen(
    totalFocusedMinutes: Int,
    totalInterceptions: Int,
    selectedArtifact: KineticArtifact,
    sessionHistory: List<FocusSessionEntity>,
    interceptLogs: List<InterceptLogEntity>,
    sevenDayStats: List<DailyFocusStat>,
    harmonicSynthUnlocked: Boolean,
    encryptedBackupPayload: String,
    soundEngine: TactileSoundEngine? = null,
    isDarkTheme: Boolean = false,
    onSelectArtifact: (KineticArtifact) -> Unit,
    onPlayHarmonicChord: (Int) -> Unit,
    onRefreshBackup: () -> Unit,
    onImportBackupJson: (String) -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    var selectedDayIdx by remember { mutableIntStateOf(6) }
    val completedCount = remember(sessionHistory) { sessionHistory.count { it.completedSuccessfully } }
    val totalBricks = remember(totalFocusedMinutes, completedCount) {
        ((totalFocusedMinutes / 25) + completedCount).coerceAtLeast(16)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("curio_vault_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // =====================================================================
        // SECTION 1: 4DP DOT-MATRIX COUNTER MACHINE READOUTS (TileNumberView)
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "COUNTER MACHINE TELEMETRY",
                rightWordPrefix = "DOT GRID ",
                rightMonoNumber = "4DP"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AndroidView(
                    factory = { ctx -> TileNumberCounterView(ctx) },
                    update = { view ->
                        view.isNightMode = isDarkTheme
                        view.labelWord = "FOCUSED MIN"
                        view.numberValue = totalFocusedMinutes
                        view.useAccentColor = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(74.dp)
                )
                AndroidView(
                    factory = { ctx -> TileNumberCounterView(ctx) },
                    update = { view ->
                        view.isNightMode = isDarkTheme
                        view.labelWord = "DEFLECTED"
                        view.numberValue = totalInterceptions
                        view.useAccentColor = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(74.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AndroidView(
                    factory = { ctx -> TileNumberCounterView(ctx) },
                    update = { view ->
                        view.isNightMode = isDarkTheme
                        view.labelWord = "SESSIONS"
                        view.numberValue = sessionHistory.size.coerceAtLeast(4)
                        view.useAccentColor = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(74.dp)
                )
                AndroidView(
                    factory = { ctx -> TileNumberCounterView(ctx) },
                    update = { view ->
                        view.isNightMode = isDarkTheme
                        view.labelWord = "TOWER BRICKS"
                        view.numberValue = totalBricks
                        view.useAccentColor = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(74.dp)
                )
            }
        }

        // =====================================================================
        // SECTION 2: STREAK TOWER ACCUMULATION (TowerView — 7 Bricks / Floor)
        // =====================================================================
        item {
            AndroidView(
                factory = { ctx ->
                    TowerStreakView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = isDarkTheme
                    view.soundEngine = soundEngine
                    view.totalBricksCount = totalBricks
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(196.dp)
                    .testTag("tower_streak_view")
            )
        }

        // =====================================================================
        // SECTION 3: 7-DAY VERTICAL FADERS (DayFadersView)
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "7-DAY FOCUS FADERS",
                rightWordPrefix = "DAY ",
                rightMonoNumber = "0${selectedDayIdx + 1}/07",
                useAccentOnNumber = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            AndroidView(
                factory = { ctx ->
                    DayFadersView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = isDarkTheme
                    view.soundEngine = soundEngine
                    view.statsList = sevenDayStats
                    view.selectedDayIndex = selectedDayIdx
                    view.onDaySelected = { idx -> selectedDayIdx = idx }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(164.dp)
                    .testTag("day_faders_view")
            )
        }

        // =====================================================================
        // SECTION 4: VERIFIED SESSION RECEIPT LEDGER & AES-GCM BACKUP
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "VERIFIED SESSION RECEIPTS",
                rightWordPrefix = "TOTAL ",
                rightMonoNumber = sessionHistory.size.toString()
            )
        }

        items(sessionHistory.take(8), key = { it.id }) { session ->
            val mins = (session.elapsedSeconds / 60).coerceAtLeast(1)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(scheme.surfaceVariant)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = session.intention,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = scheme.onBackground
                    )
                    Text(
                        text = "+${mins}m",
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (session.completedSuccessfully) scheme.primaryContainer else scheme.onBackground
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = rememberMixedTypographyText(
                            wordPrefix = "${session.profileName} • TASKS ",
                            monoNumber = session.tasksCompletedCount.toString(),
                            wordColor = scheme.onSurfaceVariant,
                            monoColor = scheme.onBackground
                        ),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "HMAC•${session.hmacSignature.take(8).uppercase()}",
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 10.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            BlockitActionChip(
                wordLabel = "EXPORT AES-256-GCM LEDGER BACKUP",
                monoBadge = if (encryptedBackupPayload.isNotEmpty()) "READY" else "",
                isPrimaryInk = true,
                onClick = onRefreshBackup,
                modifier = Modifier.fillMaxWidth(),
                testTag = "export_backup_button"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
