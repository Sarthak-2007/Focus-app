package com.example.ui.screens

import android.os.SystemClock
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.BlockedBrowserDomains
import com.example.data.FocusProfileEntity
import com.example.data.LoadoutRules
import com.example.data.MonolithRepository
import com.example.data.SmartLoadout
import com.example.data.groupedByRule
import com.example.service.TactileSoundEngine
import com.example.ui.TierChangeForUndo
import com.example.ui.components.BlockitActionChip
import com.example.ui.components.ShieldCapSelector
import com.example.ui.components.onInkControlFill
import com.example.ui.components.inkControlFill
import com.example.ui.components.FullBleedSectionHeader
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.SoraFamily
import com.example.ui.theme.TokenColorOnPrimary
import com.example.ui.views.AppRowSledToggleView
import com.example.ui.views.SledSwitchView
import kotlinx.coroutines.delay

/**
 * TAB 1: SUITCASE & APP SWITCHBOARD (DEVELOPER-GUIDE.pdf §4.2 #4 BagTiles, #6 SledSwitch, #7 RombiKnob)
 * - Full-bleed stacked sections with 12dp universal spacing.
 * - ShieldCapSelector for discrete 3-position Loadout selection (STUDY / WORK / SLEEP).
 * - AppRowSledToggleView ultra-minimal installed app rows with 3-stop sled switches (SHUT / PEEK / OPEN).
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
    // UNA-07: the LOADOUT cap's resting position, from the persisted
    // last hand pick (LoadoutStore via the VM). Default 0 (STUDY) is
    // the pre-pick default for render harnesses and previews.
    loadoutCapIndex: Int = 0,
    isSessionActive: Boolean = false,
    ratchetWarningMessage: String? = null,
    // Issue #141: the staged sled commit + its take-back, rendered
    // as a banner under the ratchet warning while the window lasts.
    tierChangeForUndo: TierChangeForUndo? = null,
    onUndoTierChange: () -> Unit = {},
    soundEngine: TactileSoundEngine? = null,
    isDarkTheme: Boolean = false,
    onSearchQueryChanged: (String) -> Unit,
    onSelectCategory: (AppCategory?) -> Unit,
    onSetAppTier: (String, AppBlockTier) -> Unit,
    onApplySmartLoadout: (SmartLoadout) -> Unit,
    // Issue #107 (#3): correct the category printed on a catalogue
    // row. Default no-op for render harnesses and previews.
    onSetAppCategory: (String, AppCategory) -> Unit = { _, _ -> },
    onSelectProfile: (FocusProfileEntity) -> Unit = {},
    onToggleShortsReelsSniper: () -> Unit,
    onToggleBrowserUrlBlocker: () -> Unit,
    routines: List<com.example.data.RoutineEntity> = emptyList(),
    showResetNote: Boolean = false,
    onDismissResetNote: () -> Unit = {},
    // Install-day disclosures (R3b #161): one-time notes owed at a
    // fresh start and after an update, in the Shield's note family.
    showFreshStartNote: Boolean = false,
    showUpdateNote: Boolean = false,
    onDismissFreshStartNote: () -> Unit = {},
    onDismissUpdateNote: () -> Unit = {},
    onToggleRoutine: (com.example.data.RoutineEntity) -> Unit = {},
    onCycleRoutineLoadout: (com.example.data.RoutineEntity) -> Unit = {},
    onAdjustRoutineTime: (com.example.data.RoutineEntity, Boolean, Int) -> Unit = { _, _, _ -> },
    onToggleRoutineDay: (com.example.data.RoutineEntity, Int) -> Unit = { _, _ -> },
    // The websites editor, transplanted from the dissolved Guard
    // screen (chrome wave): a MOVE, not a copy — the Guard screen is
    // deleted and this section is the editor's only home. Wired to
    // the same blockedSites / add / remove the Guard screen used;
    // the list it edits is the list the blocker enforces.
    blockedSites: List<String> = emptyList(),
    onAddSite: (String) -> Unit = {},
    onRemoveSite: (String) -> Unit = {},
    // The prune note (Mikro M5): the one-time strip above ALL APPS
    // announcing what the installed-only reconcile took off the list.
    // Null = nothing pending; the words are the repository's record,
    // composed by the caller, printed exactly.
    pruneNoteText: String? = null,
    onPruneNoteDismissed: () -> Unit = {}
) {
    val scheme = MaterialTheme.colorScheme
    val effectiveNight = isDarkTheme || isSessionActive
    // UNA-07: the cap rests on the STORED FACT (loadoutCapIndex —
    // his last hand pick), not on a bare remember defaulting to
    // STUDY. The local state still answers a tap instantly; the
    // remember key re-seats it whenever the stored fact (re)loads.
    var selectedLoadoutIndex by remember(loadoutCapIndex) { mutableIntStateOf(loadoutCapIndex) }

    // Issue #107 (#1): a cap tap NO LONGER applies a loadout. It
    // stages one — the preview panel under the cap prints what the
    // loadout does (its own subtitle), what else it switches on,
    // and how many hand-set tiers it would replace — and only the
    // panel's APPLY word commits. KEEP MY RULES drops the staging.
    var pendingLoadout by remember { mutableStateOf<SmartLoadout?>(null) }

    // =====================================================================
    // THE STANDING ORDERS (Ordner lane — Liv's placement + catalogue
    // discipline, Gust's slip as the form, Mikro's moments as its
    // manners). His decisions, printed in the receipt's grammar,
    // present tense, directly above the catalogue they govern.
    // =====================================================================
    // Issue #35: the printed tally IS AppRules' counts (the bucket
    // list sizes themselves) — this screen no longer recounts with
    // its own predicates, so a printed count cannot disagree with
    // the lines under it. Corrupt-tier rows count in none of the
    // three: they are not decisions (see AppRules.corrupt).
    val rules = remember(allApps) { allApps.groupedByRule() }
    val shutCount = rules.shutCount
    val peekCount = rules.peekCount
    val openCount = rules.openCount

    // M6 (Mikro): return is memory — the list reopens where his thumb
    // left it. Saveable, so the place survives this screen being
    // disposed on a tab switch.
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    // M3's rest: a flipped line wears its new ruling in its old
    // section, rests 300ms, then leaves — never vanishing under the
    // finger, never flying across the page.
    var restingFlips by remember { mutableStateOf<Map<String, RestingFlip>>(emptyMap()) }
    LaunchedEffect(searchQuery) {
        // A search folds the slip (Liv D4/D6); rests die with the fold.
        if (searchQuery.isNotBlank()) restingFlips = emptyMap()
    }
    // Sweeper: a rest whose lifecycle was interrupted (an outside
    // actor re-tiered the app mid-rest) retires by age — a resting
    // entry may never become a permanent suppression.
    LaunchedEffect(Unit) {
        while (true) {
            delay(700)
            if (restingFlips.isNotEmpty()) {
                val now = SystemClock.elapsedRealtime()
                restingFlips = restingFlips.filterValues { now - it.createdAtMs <= 1200 }
            }
        }
    }

    // M4, governed (Gust): the tally never rolls its digits. A burst
    // of flips coalesces — the whole line re-strikes once, to the
    // latest figures, after they have been stable 250ms.
    val tallyNow = Triple(shutCount, peekCount, openCount)
    var tallyShown by remember { mutableStateOf(tallyNow) }
    LaunchedEffect(tallyNow) {
        delay(250)
        tallyShown = tallyNow
    }

    val slipTap: (BlockedAppEntity) -> Unit = { app ->
        val pkg = app.packageName
        val resting = restingFlips[pkg]
        if (resting != null) {
            // Re-tap before the exit cancels it: flip back, no motion.
            restingFlips = restingFlips - pkg
            onSetAppTier(pkg, resting.from)
        } else {
            val current = app.resolvedTier
            val target = if (current == AppBlockTier.LOCKED) {
                AppBlockTier.PEEK_2M
            } else {
                AppBlockTier.LOCKED
            }
            onSetAppTier(pkg, target)
            // The choreography runs only when the commit will land:
            // the one-way ratchet refuses a weakening mid-session,
            // and a refused flip must not re-strike the line. This
            // gate mirrors the ViewModel's rule exactly; the
            // ViewModel remains the real guard.
            val weakening = tierRank(target) < tierRank(current)
            if (!isSessionActive || !weakening) {
                restingFlips = restingFlips + (pkg to RestingFlip(
                    from = current,
                    to = target,
                    exiting = false,
                    createdAtMs = SystemClock.elapsedRealtime()
                ))
            }
        }
    }

    /** The slip's lines for one section: committed lines in place,
     *  plus any line resting here after a flip — and a line resting
     *  elsewhere is suppressed here, so an app is present in exactly
     *  one section at any instant. */
    fun slipLines(tier: AppBlockTier): List<SlipLine> {
        val committed = when (tier) {
            AppBlockTier.PEEK_2M -> rules.peek
            AppBlockTier.LOCKED -> rules.shut
            AppBlockTier.OPEN -> rules.open
        }
        val lines = mutableListOf<SlipLine>()
        for (app in committed) {
            val resting = restingFlips[app.packageName]
            if (resting != null && resting.from != tier) continue
            lines += SlipLine(app = app, word = app.resolvedTier, resting = null)
        }
        for ((pkg, resting) in restingFlips) {
            if (resting.from != tier) continue
            val app = allApps.firstOrNull { it.packageName == pkg } ?: continue
            if (app.resolvedTier == tier) continue
            lines += SlipLine(app = app, word = resting.to, resting = resting)
        }
        // AppRules' total order (issue #35): name, then package —
        // a name tie can never inherit feed/intercept order here.
        return lines.sortedWith(
            compareBy<SlipLine> { it.app.appName.lowercase(java.util.Locale.ROOT) }.thenBy { it.app.packageName }
        )
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("shield_matrix_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        // F-04 (chrome C9): the floating band's clearance — bottom
        // content is never trapped under the rail.
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
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
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TokenColorOnPrimary
                    )
                }
            }
        }

        // Issue #141: the sled commit's receipt + take-back. A tier
        // change used to land silently and forever; while the undo
        // window lasts, the banner names what changed and offers
        // the reversal on the spot.
        item {
            AnimatedVisibility(visible = tierChangeForUndo != null) {
                val change = tierChangeForUndo
                if (change != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(scheme.primaryContainer)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "'${change.appName.uppercase()}' SET TO ${tierWord(change.toTier)}",
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFFBF1EC)
                            )
                            Text(
                                text = "UNDO",
                                modifier = Modifier.clickable { onUndoTierChange() },
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFFBF1EC)
                            )
                        }
                    }
                }
            }
        }

        // =====================================================================
        // SECTION 1: SLIDING CAP LOADOUT SELECTOR (Sarthak's pick,
        // Oct 29 — Linea's cap over the RombiKnob; the cap occupies
        // the same 84dp slot the knob's AndroidView held)
        // =====================================================================
        item {
            // The counts left this header (Liv D3, adopted by Gust):
            // SHUT / PEEK / OPEN live on the standing-orders tally
            // below — one home per number, or numbers are decoration.
            FullBleedSectionHeader(titleWord = "LOADOUT")
            Spacer(modifier = Modifier.height(8.dp))
            ShieldCapSelector(
                selectedIndex = selectedLoadoutIndex,
                soundEngine = soundEngine,
                onSelect = { level ->
                    // Stage, never apply (issue #107 #1) — resolved
                    // through the ONE index↔loadout mapping
                    // (LoadoutStore, #58 F6): an index that seats
                    // nothing stages nothing — the hand-written
                    // `else -> SLEEP` silently meant SLEEP for any
                    // garbage index and stays deleted.
                    com.example.data.LoadoutStore.loadoutAtCapIndex(level)?.let { loadout ->
                        pendingLoadout = loadout
                    }
                }
            )
            pendingLoadout?.let { loadout ->
                LoadoutPreviewPanel(
                    loadout = loadout,
                    preview = remember(loadout, allApps) {
                        LoadoutRules.preview(loadout, allApps)
                    },
                    onApply = {
                        // Reverse direction of the same ONE mapping
                        // (#58 F6) — no second hand-written table.
                        selectedLoadoutIndex =
                            com.example.data.LoadoutStore.capIndexOf(loadout)
                        pendingLoadout = null
                        onApplySmartLoadout(loadout)
                    },
                    onKeepMine = { pendingLoadout = null }
                )
            }
        }

        // =====================================================================
        // SECTION 3: ROUTINES — real, persisted, self-arming (Ren)
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "ROUTINES",
                rightWordPrefix = "",
                // UNA-09: count what the scheduler counts — LIVE
                // routines only (RoutineSchedule.liveCount: enabled
                // AND at least one day). A daysMask == 0 routine the
                // chain skips is never printed as ACTIVE.
                rightMonoNumber = com.example.data.RoutineSchedule.liveCount(routines).toString(),
                rightWordSuffix = " ACTIVE"
            )
            if (showFreshStartNote) {
                // R3b #161 (update-day): a fresh seed means this
                // phone holds no Monolith past — said once, plainly,
                // at first open, in the open instead of buried in
                // the Drawer's PARK plate. Tap dismisses.
                Text(
                    text = "FRESH START ON THIS PHONE — NO PAST CAME WITH IT. IF YOU HAD MONOLITH BEFORE, ITS RECORD STAYED ON THAT PHONE: MONOLITH KEEPS EVERYTHING ON THE PHONE IT WAS MADE ON. TAP TO CLOSE.",
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clickable { onDismissFreshStartNote() }
                        .testTag("fresh_start_note")
                )
            }
            if (showUpdateNote) {
                // R3b #161: an update used to land in silence (the
                // boot receiver swallowed MY_PACKAGE_REPLACED). Now
                // the first open after an update says so, once.
                Text(
                    text = "MONOLITH UPDATED — THIS PHONE'S TASKS, HISTORY AND SHIELD CAME THROUGH THE UPDATE WITH IT. TAP TO CLOSE.",
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clickable { onDismissUpdateNote() }
                        .testTag("update_note")
                )
            }
            if (showResetNote) {
                // One-time reseed strip (Freya §3.3; R3b #161): his
                // schedule was reset to his real day. The note now
                // says what was NOT touched, and only his tap on
                // the note itself dismisses it — obeying it (an
                // edit) no longer erases the record.
                Text(
                    text = "AN UPDATE RESET YOUR ROUTINE TIMES TO YOUR DAY — YOUR TASKS, HISTORY AND SHIELD WERE NOT TOUCHED. TAP A ROW TO CHANGE A TIME. TAP THIS NOTE TO CLOSE IT.",
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clickable { onDismissResetNote() }
                        .testTag("routine_reset_note")
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                routines.forEach { routine ->
                    RoutineRow(
                        routine = routine,
                        onToggle = { onToggleRoutine(routine) },
                        onCycleLoadout = { onCycleRoutineLoadout(routine) },
                        onAdjustStart = { delta -> onAdjustRoutineTime(routine, true, delta) },
                        onAdjustEnd = { delta -> onAdjustRoutineTime(routine, false, delta) },
                        onToggleDay = { day -> onToggleRoutineDay(routine, day) }
                    )
                }
            }
        }

        // =====================================================================
        // SECTION 3.5: YOUR RULES — THE STANDING ORDERS SLIP.
        // The tally first (three numbers that sum to the phone — a
        // ledger that doesn't balance is a lie), then PEEK before
        // SHUT (the holes are the urgent read), one printed line per
        // ruling. A line tap flips the ruling on the sled's own
        // commit path; revoking to OPEN lives on the catalogue sled,
        // where it belongs — the book amends, the catalogue revokes.
        // Under search the slip folds to its header line (Liv D6):
        // search is a finding tool for the catalogue and never
        // filters the book, or its counts would stop being counts of
        // his decisions.
        // =====================================================================
        if (searchQuery.isBlank()) {
            item {
                Column {
                    SlipHeaderRow(right = null)
                    // Issue #107 (#4): the slip's lines are toggles —
                    // the header now SAYS so, instead of letting a
                    // tap-to-inspect silently flip a ruling.
                    Text(
                        text = "TAP A LINE TO FLIP IT SHUT ↔ PEEK",
                        fontFamily = AnonymousProFamily,
                        fontSize = 11.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .testTag("slip_hint")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StandingTallyLine(counts = tallyShown)
                    // Issue #107 (#2): the PEEK terms, printed where
                    // the rulings are read.
                    Text(
                        text = PEEK_TERMS_LINE,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 11.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .testTag("shield_peek_terms")
                    )
                }
            }
            val peekLines = slipLines(AppBlockTier.PEEK_2M)
            item { SlipSectionHeader(word = "PEEK") }
            if (peekLines.isEmpty()) {
                item { SlipNoneLine() }
            } else {
                items(peekLines, key = { "slip_${it.app.packageName}" }) { line ->
                    SlipLineRow(
                        line = line,
                        onTap = { slipTap(line.app) },
                        onRestElapsed = {
                            restingFlips[line.app.packageName]?.let { entry ->
                                restingFlips = restingFlips +
                                    (line.app.packageName to entry.copy(exiting = true))
                            }
                        },
                        onExitElapsed = {
                            restingFlips = restingFlips - line.app.packageName
                        }
                    )
                }
            }
            val shutLines = slipLines(AppBlockTier.LOCKED)
            item { SlipSectionHeader(word = "SHUT") }
            if (shutLines.isEmpty()) {
                item { SlipNoneLine() }
            } else {
                items(shutLines, key = { "slip_${it.app.packageName}" }) { line ->
                    SlipLineRow(
                        line = line,
                        onTap = { slipTap(line.app) },
                        onRestElapsed = {
                            restingFlips[line.app.packageName]?.let { entry ->
                                restingFlips = restingFlips +
                                    (line.app.packageName to entry.copy(exiting = true))
                            }
                        },
                        onExitElapsed = {
                            restingFlips = restingFlips - line.app.packageName
                        }
                    )
                }
            }
            // Issue #141: OPEN gets a section too. The slip used to
            // print only the rulings that restrict, so an app flipped
            // to OPEN vanished from the book entirely — discoverable
            // only by the absence nobody audits. The open rulings are
            // printed like the others; a line tap shuts the app
            // (the book amends in both directions).
            val openLines = slipLines(AppBlockTier.OPEN)
            item { SlipSectionHeader(word = "OPEN") }
            if (openLines.isEmpty()) {
                item { SlipNoneLine() }
            } else {
                items(openLines, key = { "slip_${it.app.packageName}" }) { line ->
                    SlipLineRow(
                        line = line,
                        onTap = { slipTap(line.app) },
                        onRestElapsed = {
                            restingFlips[line.app.packageName]?.let { entry ->
                                restingFlips = restingFlips +
                                    (line.app.packageName to entry.copy(exiting = true))
                            }
                        },
                        onExitElapsed = {
                            restingFlips = restingFlips - line.app.packageName
                        }
                    )
                }
            }
        } else {
            item {
                SlipHeaderRow(right = tallyText(shutCount, peekCount, open = null))
            }
        }

        // M5 (Mikro): the prune announces itself once — one quiet
        // line in the app's one-time-strip grammar (the routine
        // reseed note is the precedent), directly above the catalogue
        // it describes. Tapping it dismisses it; so does the first
        // row tap anywhere (the ViewModel owns that half).
        if (pruneNoteText != null) {
            item {
                Text(
                    text = pruneNoteText,
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPruneNoteDismissed() }
                        .testTag("app_list_prune_note")
                )
            }
        }

        // =====================================================================
        // SECTION 4: SEARCH & CATEGORY FILTER STRIP
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "ALL APPS",
                rightWordPrefix = "SHOWING ",
                rightMonoNumber = filteredApps.size.toString()
            )
            Text(
                text = "APPS ON THIS PHONE ONLY — NOTHING HERE CAN HOLD AN APP ON ANOTHER DEVICE",
                fontFamily = AnonymousProFamily,
                fontSize = 10.sp,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.testTag("shield_catalog_scope_line")
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Recessed 12dp search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(scheme.surfaceVariant)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Filter apps by name or package…",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 13.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = scheme.onBackground
                    ),
                    // The accent law (chrome C7): a caret is furniture,
                    // not a commit — it wears ink, never the accent.
                    cursorBrush = SolidColor(scheme.onBackground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shield_search_input")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Chips keep their natural width and the row scrolls — full words,
            // never the old 3-letter truncations (Ren).
            // Filters as words (S2/T8): state = ink + an underline bar —
            // position and weight, not five bordered pills.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilterWord(
                    label = "ALL",
                    selected = selectedCategory == null,
                    onClick = { onSelectCategory(null) }
                )
                AppCategory.entries.forEach { cat ->
                    FilterWord(
                        label = cat.label.uppercase(),
                        selected = selectedCategory == cat,
                        onClick = { onSelectCategory(if (selectedCategory == cat) null else cat) }
                    )
                }
            }
        }

        // Issue #107 (#2): the PEEK terms again directly above the
        // sleds that sell it — the middle stop's contract, at the
        // point of choice.
        if (filteredApps.isNotEmpty()) {
            item {
                Text(
                    text = PEEK_TERMS_LINE,
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 11.sp,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.testTag("shield_peek_terms_catalogue")
                )
            }
        }

        // =====================================================================
        // SECTION 5: ULTRA-MINIMAL 3-STOP APP SLED ROWS (Decision #14)
        // =====================================================================
        items(filteredApps, key = { it.packageName }) { app ->
            CatalogueAppRow(
                app = app,
                effectiveNight = effectiveNight,
                soundEngine = soundEngine,
                onSetAppTier = onSetAppTier,
                onSetAppCategory = onSetAppCategory
            )
        }

        // The first sentence the installed-only cure lets the app say
        // honestly (Liv, adopted by Gust): a search with no match is
        // answered in print, never with a void.
        if (filteredApps.isEmpty()) {
            item {
                Text(
                    // Merged cures — issue #107 (#5) for search: never
                    // claim an installed app is absent; a zero result
                    // means the SEARCH matched nothing, and the app may
                    // live under another name or its package name.
                    // Issue #51 F5 for the non-search empties: a category
                    // or whole-list void prints its fact too, never a
                    // silent blank.
                    text = when {
                        searchQuery.isNotBlank() ->
                            "NO MATCH FOR THAT SEARCH — TRY THE APP'S NAME OR ITS PACKAGE NAME"
                        selectedCategory != null ->
                            "NO APPS IN THIS CATEGORY ON THIS PHONE"
                        else -> "NO APPS ON THIS PHONE"
                    },
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.testTag("shield_no_match_line")
                )
            }
        }

        // =====================================================================
        // SECTION 5B: THE GUARDS (issue #51 F1 cure) — the sniper,
        // the browser blocker and the profiles were wired from
        // MainActivity to real ViewModel functions and rendered
        // NOWHERE; this section is their UI. The switches sit
        // directly above BLOCKED WEBSITES, whose header claimed
        // sites were blocked with no switch in reach.
        // =====================================================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FullBleedSectionHeader(
                    titleWord = "GUARDS",
                    rightWordPrefix = "",
                    rightMonoNumber = "",
                    rightWordSuffix = ""
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SHORTS & REELS SNIPER",
                        fontFamily = AnonymousProFamily,
                        fontSize = 11.sp,
                        color = scheme.onBackground
                    )
                    BlockitActionChip(
                        wordLabel = if (shortsReelsSniperEnabled) "ON" else "OFF",
                        isPrimaryInk = shortsReelsSniperEnabled,
                        onClick = onToggleShortsReelsSniper,
                        testTag = "shield_sniper_toggle"
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BROWSER URL BLOCKER",
                        fontFamily = AnonymousProFamily,
                        fontSize = 11.sp,
                        color = scheme.onBackground
                    )
                    BlockitActionChip(
                        wordLabel = if (browserUrlBlockerEnabled) "ON" else "OFF",
                        isPrimaryInk = browserUrlBlockerEnabled,
                        onClick = onToggleBrowserUrlBlocker,
                        testTag = "shield_browser_blocker_toggle"
                    )
                }
                if (profiles.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        profiles.forEach { profile ->
                            FilterWord(
                                label = profile.name.uppercase(),
                                selected = profile.name == selectedProfileName,
                                onClick = { onSelectProfile(profile) }
                            )
                        }
                    }
                }
            }
        }

        // =====================================================================
        // SECTION 6: BLOCKED WEBSITES — the editor transplanted from
        // the dissolved Guard screen (chrome wave). The Guard
        // screen's section, moved whole: same rows, same add field,
        // same wiring — the list below is the list the blocker
        // enforces (the plate in PARK says so: SITES: KEPT WITH
        // APPS ON SHIELD).
        // =====================================================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FullBleedSectionHeader(
                    titleWord = "BLOCKED WEBSITES",
                    rightWordPrefix = "",
                    rightMonoNumber = blockedSites.size.toString(),
                    rightWordSuffix = " SITES"
                )

                blockedSites.forEach { domain ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = domain,
                            fontFamily = AnonymousProFamily,
                            fontSize = 14.sp,
                            color = scheme.onBackground
                        )
                        BlockitActionChip(
                            wordLabel = "REMOVE",
                            onClick = { onRemoveSite(domain) }
                        )
                    }
                }

                // Issue #51 F3 cure: saveable — the draft is no
                // longer destroyed when this LazyColumn item is
                // disposed on scroll.
                var newSiteText by rememberSaveable { mutableStateOf("") }
                var siteError by rememberSaveable { mutableStateOf<String?>(null) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(scheme.surfaceVariant)
                            .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (newSiteText.isEmpty()) {
                            Text(
                                text = "Add a site — e.g. news.ycombinator.com",
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontSize = 13.sp,
                                color = scheme.onSurfaceVariant
                            )
                        }
                        BasicTextField(
                            value = newSiteText,
                            onValueChange = { newSiteText = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = AnonymousProFamily,
                                fontSize = 14.sp,
                                color = scheme.onBackground
                            ),
                            // The accent law (chrome C7): ink caret.
                            cursorBrush = SolidColor(scheme.onBackground),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("shield_site_input")
                        )
                    }
                    BlockitActionChip(
                        wordLabel = "ADD",
                        isPrimaryInk = true,
                        onClick = {
                            // Issue #51 F2 cure (ported from agent/issue-7's
                            // R-3 truth fix): a rejected ADD PRINTS WHY and
                            // KEEPS the text. The old form cleared the field
                            // unconditionally — MonolithRepository.addBlockedSite
                            // rejects an unnormalisable domain (returns false)
                            // and the ViewModel swallows that Boolean, so
                            // garbage vanished as if saved. The screen judges
                            // with the repository's own normaliser, so the
                            // verdict it prints is the verdict the store gives.
                            val normalised = BlockedBrowserDomains.normalizeDomain(newSiteText)
                            when {
                                newSiteText.isBlank() -> Unit
                                normalised == null -> siteError =
                                    "NOT A SITE — CHECK THE ADDRESS AND TRY AGAIN"
                                blockedSites.contains(normalised) -> siteError =
                                    "ALREADY ON THE LIST"
                                else -> {
                                    onAddSite(newSiteText)
                                    newSiteText = ""
                                    siteError = null
                                }
                            }
                        }
                    )
                }
                val err = siteError
                if (err != null) {
                    Text(
                        text = err,
                        fontFamily = AnonymousProFamily,
                        fontSize = 11.sp,
                        color = scheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shield_site_error")
                    )
                }
            }
        }
    }
}


/**
 * Issue #107 (#2): the PEEK contract, printed. The numbers are
 * derived from the repository's own constants — the lease and the
 * mandatory cooldown the code enforces — so the printed terms are
 * the enforced terms.
 */
internal val PEEK_TERMS_LINE: String =
    "PEEK = ${MonolithRepository.PEEK_DURATION_SECONDS / 60} MINUTES, ONCE — " +
        "THEN ${MonolithRepository.PEEK_COOLDOWN_MILLIS / 60_000} MINUTES SHUT BEFORE THE NEXT PEEK"

/**
 * Issue #107 (#1): the staged loadout's preview. Prints the
 * loadout's own subtitle (dead text until tonight), its extra
 * side-effects for STUDY, and the exact cost in hand-set rulings —
 * counted by LoadoutRules, the same function that commits. Nothing
 * applies until APPLY LOADOUT is tapped; KEEP MY RULES stages
 * nothing and changes nothing.
 */
@Composable
private fun LoadoutPreviewPanel(
    loadout: SmartLoadout,
    preview: LoadoutRules.Preview,
    onApply: () -> Unit,
    onKeepMine: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .testTag("loadout_preview"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = loadout.title.uppercase(),
            fontFamily = SoraFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = scheme.onBackground
        )
        Text(
            text = loadout.subtitle,
            fontFamily = AtkinsonHyperlegibleFamily,
            fontSize = 12.sp,
            color = scheme.onBackground,
            modifier = Modifier.testTag("loadout_preview_subtitle")
        )
        if (loadout == SmartLoadout.STUDY) {
            Text(
                text = "ALSO SWITCHES ON: SHORTS & REELS SNIPER + BROWSER URL BLOCKER",
                fontFamily = AnonymousProFamily,
                fontSize = 11.sp,
                color = scheme.onBackground,
                modifier = Modifier.testTag("loadout_preview_side_effects")
            )
        }
        Text(
            text = "REPLACES ${preview.changed} HAND-SET RULING(S) — " +
                "LANDS ON ${preview.shut} SHUT · ${preview.peek} PEEK · ${preview.open} OPEN",
            fontFamily = AnonymousProFamily,
            fontSize = 11.sp,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.testTag("loadout_preview_cost")
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BlockitActionChip(
                wordLabel = "APPLY LOADOUT",
                isPrimaryInk = true,
                onClick = onApply,
                testTag = "loadout_apply"
            )
            BlockitActionChip(
                wordLabel = "KEEP MY RULES",
                onClick = onKeepMine,
                testTag = "loadout_keep_mine"
            )
        }
    }
}

private fun fmtRoutineTime(minutes: Int): String =
    "%02d:%02d".format(minutes / 60, minutes % 60)

private fun routineDaysLabel(mask: Int): String = when (mask) {
    127 -> "DAILY"
    31 -> "MON–FRI"
    0 -> "NO DAYS SET"
    else -> listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
        .filterIndexed { i, _ -> mask and (1 shl i) != 0 }
        .joinToString(" ")
}

private fun routineLoadoutLabel(loadoutName: String): String = when (loadoutName) {
    "WORK_SPRINT" -> "WORK"
    "SLEEP" -> "SLEEP"
    else -> "STUDY"
}

@Composable
private fun RoutineRow(
    routine: com.example.data.RoutineEntity,
    onToggle: () -> Unit,
    onCycleLoadout: () -> Unit,
    onAdjustStart: (Int) -> Unit,
    onAdjustEnd: (Int) -> Unit,
    onToggleDay: (Int) -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    // Collapsed by default (Ren): a routine is one calm line — name, window,
    // days, state. The full control set appears only when the row is tapped,
    // the way Blockit keeps its surfaces quiet until touched.
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time column (Pattern 7 / T11): start over end in the left
            // margin, one axis for every routine — the day reads as a column.
            Column(
                modifier = Modifier.width(54.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = fmtRoutineTime(routine.startMinutes),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = scheme.onBackground
                )
                Text(
                    text = "→ " + fmtRoutineTime(routine.endMinutes),
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = routine.name.uppercase(),
                    fontFamily = SoraFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = scheme.onBackground
                )
                Text(
                    text = "${routineDaysLabel(routine.daysMask)} · ${routineLoadoutLabel(routine.loadoutName)}",
                    fontFamily = AnonymousProFamily,
                    fontSize = 12.sp,
                    color = scheme.onSurfaceVariant
                )
            }
            // Toggle as a cap (S1): sized to its payload, one cap grammar
            // with the selector band and sleds — not a 42dp box for a word.
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (routine.enabled) inkControlFill() else scheme.surfaceVariant)
                    .border(1.dp, if (routine.enabled) inkControlFill() else scheme.outlineVariant, RoundedCornerShape(8.dp))
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (routine.enabled) "ON" else "OFF",
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (routine.enabled) onInkControlFill() else scheme.onSurfaceVariant
                )
            }
        }
        if (expanded) {
        // Issue #51 F4 cure: the editor row scrolls horizontally
        // (the category row's own pattern) instead of pushing the
        // loadout chip off a phone-width screen.
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "START",
                fontFamily = AnonymousProFamily,
                fontSize = 11.sp,
                color = scheme.onSurfaceVariant
            )
            BlockitActionChip(wordLabel = "−30", onClick = { onAdjustStart(-30) })
            BlockitActionChip(wordLabel = "+30", onClick = { onAdjustStart(30) })
            Text(
                text = "END",
                fontFamily = AnonymousProFamily,
                fontSize = 11.sp,
                color = scheme.onSurfaceVariant
            )
            BlockitActionChip(wordLabel = "−30", onClick = { onAdjustEnd(-30) })
            BlockitActionChip(wordLabel = "+30", onClick = { onAdjustEnd(30) })
            BlockitActionChip(
                wordLabel = routineLoadoutLabel(routine.loadoutName),
                isPrimaryInk = true,
                onClick = onCycleLoadout
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEachIndexed { index, letter ->
                BlockitActionChip(
                    wordLabel = letter,
                    isPrimaryInk = routine.daysMask and (1 shl index) != 0,
                    onClick = { onToggleDay(index) }
                )
            }
        }
        }
    }
}

/**
 * A filter word (Ren, S2): Anonymous Pro caps; the selected word is ink +
 * bold with a 2dp ink bar under it. No container, no border.
 */
@Composable
private fun FilterWord(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            fontFamily = AnonymousProFamily,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = if (selected) scheme.onBackground else scheme.onSurfaceVariant
        )
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(2.dp)
                .background(if (selected) scheme.onBackground else androidx.compose.ui.graphics.Color.Transparent)
        )
    }
}


// =====================================================================
// THE STANDING ORDERS SLIP — the rules region's furniture (Ordner
// lane). Print only: no icons, no boxes, no accent. Ink.
// =====================================================================

/** A flip resting in its old section (Mikro M3): the ruling has
 *  committed to [to]; the line wears the new word in the [from]
 *  section, rests 300ms, then exits over 280ms and is present in its
 *  new section when next seen. [createdAtMs] lets the screen's
 *  sweeper retire an entry whose lifecycle was interrupted. */
private data class RestingFlip(
    val from: AppBlockTier,
    val to: AppBlockTier,
    val exiting: Boolean,
    val createdAtMs: Long
)

/** One printed line of the book: the app, the ruling word it wears
 *  right now, and its rest state when it is mid-flip. */
private data class SlipLine(
    val app: BlockedAppEntity,
    val word: AppBlockTier,
    val resting: RestingFlip?
)

private fun tierRank(tier: AppBlockTier): Int = when (tier) {
    AppBlockTier.OPEN -> 0
    AppBlockTier.PEEK_2M -> 1
    AppBlockTier.LOCKED -> 2
}

private fun tierWord(tier: AppBlockTier): String = when (tier) {
    AppBlockTier.LOCKED -> "SHUT"
    AppBlockTier.PEEK_2M -> "PEEK"
    AppBlockTier.OPEN -> "OPEN"
}

/** The slip's mixed grammar — Anonymous Pro numerals, Atkinson
 *  words, the form the section headers print in. With [open] null
 *  this is Liv's D6 folded line: `7 SHUT · 2 PEEK`; with it, the
 *  tally: `9 SHUT · 4 PEEK · 61 OPEN` — three numbers that sum to
 *  the phone, so the book provably covers every app on it. */
private fun tallyText(shut: Int, peek: Int, open: Int?): AnnotatedString =
    buildAnnotatedString {
        val mono = SpanStyle(
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold
        )
        val word = SpanStyle(
            fontFamily = AtkinsonHyperlegibleFamily,
            fontWeight = FontWeight.Bold
        )
        withStyle(mono) { append(shut.toString()) }
        withStyle(word) { append(" SHUT · ") }
        withStyle(mono) { append(peek.toString()) }
        withStyle(word) { append(" PEEK") }
        if (open != null) {
            withStyle(word) { append(" · ") }
            withStyle(mono) { append(open.toString()) }
            withStyle(word) { append(" OPEN") }
        }
    }

/** The slip's header row — mirrors FullBleedSectionHeader's shape
 *  with one deliberate exception (Liv): the title prints in ink.
 *  Every other section on this screen is grey; his decisions are
 *  the one section that speaks at full volume. [right] carries the
 *  folded line under search, and nothing when the slip is open. */
@Composable
private fun SlipHeaderRow(right: AnnotatedString?) {
    val scheme = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth()) {
        // Sections separate by gap + label, not a rule (S7) — the
        // same 16dp the section headers carry.
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "YOUR RULES",
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 0.6.sp,
                color = scheme.onBackground
            )
            if (right != null) {
                Text(
                    text = right,
                    fontSize = 11.5.sp,
                    color = scheme.onBackground,
                    modifier = Modifier.padding(end = 3.dp)
                )
            }
        }
    }
}

/** The tally line. When the figures change, the whole line
 *  re-strikes — old print lifts, new print lands, 260ms Curve A
 *  (Mikro M4 as governed by Gust: a loved slip never animates its
 *  numbers, and a rolling digit is a slot machine's manners). */
@Composable
private fun StandingTallyLine(counts: Triple<Int, Int, Int>) {
    val scheme = MaterialTheme.colorScheme
    AnimatedContent(
        targetState = counts,
        transitionSpec = {
            fadeIn(tween(260, easing = MonolithCurves.EasingA)) togetherWith
                fadeOut(tween(260, easing = MonolithCurves.EasingA))
        },
        label = "standing_tally"
    ) { (shut, peek, open) ->
        Text(
            text = tallyText(shut, peek, open),
            fontSize = 11.5.sp,
            color = scheme.onBackground,
            modifier = Modifier.testTag("standing_tally")
        )
    }
}

@Composable
private fun SlipSectionHeader(word: String) {
    Text(
        text = word,
        fontFamily = AtkinsonHyperlegibleFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.5.sp,
        letterSpacing = 0.6.sp,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 4.dp)
    )
}

/** An empty section prints its fact, not a void (Gust): a missing
 *  section reads as a bug; a printed NONE is a fact. */
@Composable
private fun SlipNoneLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "NONE",
            fontFamily = AnonymousProFamily,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("slip_none_line")
        )
    }
}

/**
 * One line of the book: the app's name, a dot leader, its ruling
 * word. The leader is the ledger's mechanism for binding an entry
 * to its figure — it makes the right edge a single column of
 * rulings, the glance a tired man needs: one vertical pass, no
 * sled-decoding. The whole 40dp line is the tap target; a tap
 * flips the ruling SHUT ↔ PEEK on the sled's commit path.
 */
@Composable
private fun SlipLineRow(
    line: SlipLine,
    onTap: () -> Unit,
    onRestElapsed: () -> Unit,
    onExitElapsed: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val resting = line.resting
    val exiting = resting?.exiting == true

    // M3's clocks: rest 300ms wearing the new word, then the exit —
    // fade + height collapse over 280ms on Curve C, the neighbours
    // closing as the space gives up.
    if (resting != null && !exiting) {
        LaunchedEffect(line.app.packageName) {
            delay(300)
            onRestElapsed()
        }
    }
    if (exiting) {
        LaunchedEffect(line.app.packageName) {
            delay(280)
            onExitElapsed()
        }
    }
    val exitAlpha by animateFloatAsState(
        targetValue = if (exiting) 0f else 1f,
        animationSpec = tween(280, easing = MonolithCurves.EasingC),
        label = "slip_exit_alpha"
    )
    val exitHeight by animateDpAsState(
        targetValue = if (exiting) 0.dp else 40.dp,
        animationSpec = tween(280, easing = MonolithCurves.EasingC),
        label = "slip_exit_height"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(exitHeight)
            .alpha(exitAlpha)
            .clickable(enabled = !exiting) { onTap() }
            .testTag("slip_line_${line.app.packageName}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = line.app.appName.uppercase(),
            fontFamily = AtkinsonHyperlegibleFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = scheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = " .".repeat(90),
            fontFamily = AnonymousProFamily,
            fontSize = 11.sp,
            color = scheme.onSurfaceVariant,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp)
        )
        // The re-strike: the word lifts and lands whole, 260ms
        // Curve A — never a roll (M4's governor rides the tally;
        // the same manners hold at the line).
        AnimatedContent(
            targetState = line.word,
            transitionSpec = {
                fadeIn(tween(260, easing = MonolithCurves.EasingA)) togetherWith
                    fadeOut(tween(260, easing = MonolithCurves.EasingA))
            },
            label = "slip_word_${line.app.packageName}"
        ) { word ->
            Text(
                text = tierWord(word),
                fontFamily = AnonymousProFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = scheme.onBackground
            )
        }
    }
}

/** One catalogue row: the sled, hosted. The catalogue is where a
 *  ruling is made or revoked; the slip above is where it is read.
 *
 *  Issue #107 (#3): under the sled, the row now PRINTS the category
 *  the app was filed under — the guess that drives birth tiers,
 *  filters and loadouts — and tapping that line cycles it to the
 *  next category, so a wrong guess is visible and correctable on
 *  the spot (a plain row update; no schema change). */
@Composable
private fun CatalogueAppRow(
    app: BlockedAppEntity,
    effectiveNight: Boolean,
    soundEngine: TactileSoundEngine?,
    onSetAppTier: (String, AppBlockTier) -> Unit,
    onSetAppCategory: (String, AppCategory) -> Unit = { _, _ -> }
) {
    val scheme = MaterialTheme.colorScheme
    val category = remember(app.category) {
        runCatching { AppCategory.valueOf(app.category) }.getOrNull()
    }
    Column(modifier = Modifier.fillMaxWidth()) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val entries = AppCategory.entries
                    val next = if (category == null) {
                        entries.first()
                    } else {
                        entries[(category.ordinal + 1) % entries.size]
                    }
                    onSetAppCategory(app.packageName, next)
                }
                .padding(vertical = 2.dp)
                .testTag("app_category_${app.packageName}"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FILED AS: ${category?.label?.uppercase() ?: app.category.uppercase()}",
                fontFamily = AnonymousProFamily,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = scheme.onSurfaceVariant
            )
            Text(
                text = "  ·  TAP TO FIX",
                fontFamily = AnonymousProFamily,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = scheme.onSurfaceVariant
            )
        }
    }
}
