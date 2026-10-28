package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.FocusProfileEntity
import com.example.data.SmartLoadout
import com.example.service.TactileSoundEngine
import com.example.ui.components.BlockitActionChip
import com.example.ui.components.FullBleedSectionHeader
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.views.AppRowSledToggleView
import com.example.ui.views.BagTilesSuitcaseView
import com.example.ui.views.RombiKnobView
import com.example.ui.views.SledSwitchView

/**
 * TAB 1: SUITCASE & APP SWITCHBOARD (DEVELOPER-GUIDE.pdf §4.2 #4 BagTiles, #6 SledSwitch, #7 RombiKnob)
 * - Full-bleed stacked sections with 12dp universal spacing.
 * - RombiKnobView for discrete 3-position Loadout selection (STUDY / DEEP / SLEEP).
 * - BagTilesSuitcaseView 48dp cell grid for visual suitcase inspection & 1-tap arming.
 * - AppRowSledToggleView ultra-minimal installed app rows with 3-stop sled switches (SHUT / 2m / OPEN).
 */
@Composable
fun ShieldMatrixScreen(
    allApps: List<BlockedAppEntity>,
    filteredApps: List<BlockedAppEntity>,
    searchQuery: String,
    selectedCategory: AppCategory?,
    shortsReelsSniperEnabled: Boolean,
    browserUrlBlockerEnabled: Boolean,
    profiles: List<FocusProfileEntity> = emptyList(),
    selectedProfileName: String = "Study Loadout",
    isSessionActive: Boolean = false,
    ratchetWarningMessage: String? = null,
    soundEngine: TactileSoundEngine? = null,
    isDarkTheme: Boolean = false,
    onSearchQueryChanged: (String) -> Unit,
    onSelectCategory: (AppCategory?) -> Unit,
    onCycleAppTier: (BlockedAppEntity) -> Unit,
    onSetAppTier: (String, AppBlockTier) -> Unit,
    onApplySmartLoadout: (SmartLoadout) -> Unit,
    onSelectProfile: (FocusProfileEntity) -> Unit = {},
    onToggleShortsReelsSniper: () -> Unit,
    onToggleBrowserUrlBlocker: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val effectiveNight = isDarkTheme || isSessionActive
    var selectedLoadoutIndex by remember { mutableIntStateOf(0) }
    var morningRoutineArmed by remember { mutableStateOf(true) }
    var nightRoutineArmed by remember { mutableStateOf(true) }

    val shutCount = remember(allApps) { allApps.count { it.resolvedTier == AppBlockTier.LOCKED } }
    val peekCount = remember(allApps) { allApps.count { it.resolvedTier == AppBlockTier.PEEK_2M } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("shield_matrix_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // One-Way Ratchet warning banner if user tries to weaken rules during active session
        item {
            AnimatedVisibility(visible = !ratchetWarningMessage.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.primaryContainer)
                        .padding(12.dp)
                ) {
                    Text(
                        text = ratchetWarningMessage ?: "",
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFFBF1EC)
                    )
                }
            }
        }

        // =====================================================================
        // SECTION 1: ROMBI KNOB LOADOUT SELECTOR (§4.2 #7 RombiKnob)
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "LOADOUT DETENT KNOB",
                rightWordPrefix = "SHUT ",
                rightMonoNumber = "$shutCount",
                rightWordSuffix = " • PEEK $peekCount",
                useAccentOnNumber = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            AndroidView(
                factory = { ctx ->
                    RombiKnobView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = effectiveNight
                    view.soundEngine = soundEngine
                    view.selectedLevel = selectedLoadoutIndex
                    view.onLevelChanged = { level ->
                        selectedLoadoutIndex = level
                        when (level) {
                            0 -> onApplySmartLoadout(SmartLoadout.STUDY)
                            1 -> onApplySmartLoadout(SmartLoadout.WORK_SPRINT)
                            else -> onApplySmartLoadout(SmartLoadout.SLEEP)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .testTag("rombi_knob_loadout_view")
            )
        }

        // =====================================================================
        // SECTION 2: BAG TILES SUITCASE GRID (§4.2 #4 BagTiles — 48dp Cells)
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "SUITCASE MATRIX (48DP CELLS)",
                rightWordPrefix = "TAP CELL TO ",
                rightMonoNumber = if (isSessionActive) "LOCK" else "CYCLE"
            )
            Spacer(modifier = Modifier.height(8.dp))
            AndroidView(
                factory = { ctx ->
                    BagTilesSuitcaseView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = effectiveNight
                    view.soundEngine = soundEngine
                    view.appsList = allApps
                    view.onAppCellTapped = { app ->
                        onCycleAppTier(app)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(114.dp)
                    .testTag("suitcase_bag_tiles_view")
            )
        }

        // =====================================================================
        // SECTION 3: SCHEDULED AUTO-ROUTINES (Decision #17 — SledSwitchView)
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "SCHEDULED AUTO-ROUTINES",
                rightWordPrefix = "DAILY ",
                rightMonoNumber = "02",
                rightWordSuffix = " TRIGGERS"
            )
            Spacer(modifier = Modifier.height(4.dp))
            AndroidView(
                factory = { ctx ->
                    SledSwitchView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = effectiveNight
                    view.soundEngine = soundEngine
                    view.titleText = "MORNING DEEP WORK • 09:00"
                    view.subtitleText = "Auto-arm Study Suitcase Mon–Fri"
                    view.isChecked = morningRoutineArmed
                    view.onToggleRequested = { morningRoutineArmed = !morningRoutineArmed }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            )
            AndroidView(
                factory = { ctx ->
                    SledSwitchView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = effectiveNight
                    view.soundEngine = soundEngine
                    view.titleText = "NIGHT WIND-DOWN • 22:30"
                    view.subtitleText = "Seal Social & Video apps until 07:00"
                    view.isChecked = nightRoutineArmed
                    view.onToggleRequested = { nightRoutineArmed = !nightRoutineArmed }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            )
        }

        // =====================================================================
        // SECTION 4: SEARCH & CATEGORY FILTER STRIP
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "INSTALLED APPS SWITCHBOARD",
                rightWordPrefix = "SHOWING ",
                rightMonoNumber = filteredApps.size.toString()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Recessed 12dp search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(scheme.surfaceVariant)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Filter installed apps...",
                        fontFamily = PlusJakartaSansFamily,
                        fontSize = 13.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = scheme.onBackground
                    ),
                    cursorBrush = SolidColor(scheme.primaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shield_search_input")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BlockitActionChip(
                    wordLabel = "ALL",
                    isPrimaryInk = selectedCategory == null,
                    onClick = { onSelectCategory(null) },
                    modifier = Modifier.weight(1f)
                )
                AppCategory.entries.forEach { cat ->
                    BlockitActionChip(
                        wordLabel = cat.label.uppercase(),
                        isPrimaryInk = selectedCategory == cat,
                        onClick = { onSelectCategory(if (selectedCategory == cat) null else cat) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // =====================================================================
        // SECTION 5: ULTRA-MINIMAL 3-STOP APP SLED ROWS (Decision #14)
        // =====================================================================
        items(filteredApps, key = { it.packageName }) { app ->
            AndroidView(
                factory = { ctx ->
                    AppRowSledToggleView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = effectiveNight
                    view.soundEngine = soundEngine
                    view.appName = app.appName
                    view.currentTier = app.resolvedTier
                    view.onTierSelected = { newTier ->
                        onSetAppTier(app.packageName, newTier)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("app_row_${app.packageName}")
            )
        }
    }
}
