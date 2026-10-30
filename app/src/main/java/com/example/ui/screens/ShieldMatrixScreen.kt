package com.example.ui.screens

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import com.example.data.groupedForCatalogue
import com.example.service.TactileSoundEngine
import com.example.ui.ShieldBlockingProof
import com.example.ui.ShieldBlockingStatus
import com.example.ui.TierChangeForUndo
import com.example.ui.routineCapWord
import com.example.ui.routineRunningNowSuffix
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
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
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
    // R-5 (issue #7): the status sentence's derivation, supplied by
    // the ViewModel from the living enforcement witnesses (see
    // ShieldBlockingStatus.kt). Null = no proof was supplied to
    // this composition (a render harness, a preview): the screen
    // then prints its unproven fallback — it never derives
    // BLOCKING from isSessionActive alone, because that flag is a
    // session row's claim, not enforcement.
    blockingStatus: ShieldBlockingStatus? = null,
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
    // Issue #140: the catalogue row's hold gesture steps the app's
    // daily limit (the limit is printed on the row; the hold sets it).
    onCycleDailyLimit: (BlockedAppEntity) -> Unit = {},
    onApplySmartLoadout: (SmartLoadout) -> Unit,
    // Issue #107 (#3): correct the category printed on a catalogue
    // row. Default no-op for render harnesses and previews.
    onSetAppCategory: (String, AppCategory) -> Unit = { _, _ -> },
    onSelectProfile: (FocusProfileEntity) -> Unit = {},
    // Issue #139 F4: the profile-undo door — stands only while a
    // pre-apply snapshot exists to restore.
    profileUndoAvailable: Boolean = false,
    onUndoProfileApply: () -> Unit = {},
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
    // #153 change-of-plans verbs: skip today / resume, add / delete /
    // rename, and the one-shot drafted-time apply (F5).
    onSkipRoutineToday: (com.example.data.RoutineEntity) -> Unit = {},
    onResumeRoutine: (com.example.data.RoutineEntity) -> Unit = {},
    onAddRoutine: () -> Unit = {},
    onDeleteRoutine: (com.example.data.RoutineEntity) -> Unit = {},
    onRenameRoutine: (com.example.data.RoutineEntity, String) -> Unit = { _, _ -> },
    onSetRoutineTime: (com.example.data.RoutineEntity, Int, Int) -> Unit = { _, _, _ -> },
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
    onPruneNoteDismissed: () -> Unit = {},
    // Issue #129 F1: the unreviewed-auto-OPEN review prompt. Not
    // dismissible — it stands until each named app has a ruling.
    unreviewedOpenText: String? = null
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

    // R-5 (issue #7): the status sentence. The supplied derivation
    // is the only source. With none supplied (the cold-start
    // instant before the ViewModel's first witness read, a render
    // harness), the screen prints neither BLOCKING NOW nor a
    // proven NOT BLOCKING: it prints the unproven fallback — a
    // session row's isActive flag is a claim, not enforcement, and
    // its absence before the first Room emission is not a measured
    // fact either (critic Salz C-3).
    val statusHeadline = blockingStatus?.headline
        ?: "CAN'T CHECK BLOCKING YET — GIVE IT A SECOND"

    // R-1, second half (issue #7): the catalogue groups by ruling
    // (see groupedForCatalogue) — PEEK before SHUT, OPEN last, a
    // group header per ruling present, so the catalogue reads as
    // the ledger of his decisions. (The audit's first half — the
    // slip's retirement — did NOT land in this composition: the
    // slip was afterwards invested in by issue #107 (its lines are
    // toggles now, with the PEEK terms printed) and issue #141
    // (the OPEN section), and that later work stands.)
    val catalogueGroups = remember(filteredApps) { filteredApps.groupedForCatalogue() }

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
        // =====================================================================
        // SECTION 0: THE STATUS SENTENCE (R-5, issue #7) — the
        // first line on the screen, above every ruling it
        // qualifies: whether the wall is standing right now,
        // derived from the enforcement witnesses, never from a
        // toggle or a session row alone.
        // =====================================================================
        item {
            // Wave-2 finish cure (PANZER/SAUM/POLITUR/LUFT, one voice):
            // the wave-1 form painted every not-blocking state in
            // scheme.error — 3.49:1 by day, the most important line
            // on the screen at its least readable. Now: FULL INK in
            // every state; the state is carried by the verb and a
            // 10dp solidity glyph (solid = the wall stands, hollow =
            // it does not), never by colour alone. The slot rests
            // at 40dp so a re-strike never shoves the screen, but
            // grows rather than clips when a long failure headline
            // wraps at large font scales (GEFUEHL-R: a fixed 40dp
            // slot squeezed three wrapped lines into mush for
            // exactly the large-text users who need it most).
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp)
                    .semantics { contentDescription = statusHeadline },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // The lamp, felt in half a second (GEFUEHL-R):
                // ● the wall stands, ◐ a break (half open, half
                // held), ○ the wall is down, ◌ not yet known —
                // four states the eye separates without reading.
                Text(
                    text = when {
                        blockingStatus == null -> "◌"
                        blockingStatus.proof == ShieldBlockingProof.LIVE &&
                            blockingStatus.headline.startsWith("ON A BREAK") -> "◐"
                        blockingStatus.proof == ShieldBlockingProof.LIVE -> "●"
                        else -> "○"
                    },
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = scheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                // The re-strike: a changed verdict re-prints as a
                // whole line, 260ms Curve A — the tally's governed
                // manners, never a partial swap.
                AnimatedContent(
                    targetState = statusHeadline,
                    transitionSpec = {
                        fadeIn(tween(260, easing = MonolithCurves.EasingA)) togetherWith
                            fadeOut(tween(260, easing = MonolithCurves.EasingA))
                    },
                    label = "shield_blocking_status"
                ) { headline ->
                    // The lead is the half-second read (GEFUEHL-R):
                    // BLOCKING NOW / NOT BLOCKING / ON A BREAK /
                    // CAN'T CHECK at 16sp bold — it never wraps;
                    // the reason tail follows at 13sp normal, same
                    // ink (weight contrast only, never a dimmer
                    // colour). Every headline carries the " — "
                    // split; a headline without one prints whole.
                    val splitAt = headline.indexOf(" — ")
                    val styled = if (splitAt < 0) {
                        AnnotatedString(headline)
                    } else {
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
                                append(headline.substring(0, splitAt))
                            }
                            withStyle(SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)) {
                                append(headline.substring(splitAt))
                            }
                        }
                    }
                    Text(
                        text = styled,
                        fontFamily = AnonymousProFamily,
                        fontSize = 13.sp,
                        color = scheme.onBackground,
                        modifier = Modifier.testTag("shield_blocking_status")
                    )
                }
            }
        }

        // One-Way Ratchet warning banner if user tries to weaken rules during active session
        item { RatchetWarningBanner(message = ratchetWarningMessage, onAddRoutine = onAddRoutine) }

        // Issue #141: the sled commit's receipt + take-back (the
        // banner body lives in TierChangeUndoBanner below).
        item {
            TierChangeUndoBanner(
                change = tierChangeForUndo,
                onUndo = onUndoTierChange
            )
        }

        // Issue #139 F4: the undo strip — a profile application
        // overwrote the hand-set tiers; this restores them.
        if (profileUndoAvailable) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.surfaceVariant)
                        .clickable(onClick = onUndoProfileApply)
                        .padding(12.dp)
                        .testTag("profile_undo_strip")
                ) {
                    Text(
                        text = "A LOADOUT OVERWROTE YOUR HAND-SET TIERS — TAP TO UNDO",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = scheme.onBackground
                    )
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
            // R-4(c) (issue #7): the LOADOUT header is KILLED — the
            // cap self-labels (its detents print STUDY/WORK/SLEEP);
            // the slot pays ~32dp back to the screen.
            ShieldCapSelector(
                selectedIndex = selectedLoadoutIndex,
                soundEngine = soundEngine,
                onSelect = { level ->
                    // Stage, never apply (issue #107 #1) — resolved
                    // through the ONE index↔loadout mapping
                    // (LoadoutStore, #58 F6 / Audit R1 #38): an
                    // index that seats nothing stages nothing — the
                    // hand-written `else -> SLEEP` silently meant
                    // SLEEP for any garbage index and stays deleted.
                    com.example.data.LoadoutStore.loadoutAtCapIndex(level)?.let { loadout ->
                        pendingLoadout = loadout
                    }
                }
            )
            // R-4(b) (issue #7), the honest half this screen can
            // prove: the cap rests on the persisted LAST HAND PICK —
            // it must never wear a running routine's clothes. When
            // a routine session is the live one, the print says so;
            // otherwise it names the cap's source as the hand pick
            // it is.
            Text(
                // Nagel N-4: the routine-governs claim is printed
                // only under a LIVE headline. The derivation carries
                // activeRoutineId only on its LIVE return (every
                // failure branch leaves it null), and this gate
                // stays as the second lock — either one alone keeps
                // the line from contradicting the NOT BLOCKING
                // sentence directly above it.
                text = if (blockingStatus?.proof == ShieldBlockingProof.LIVE &&
                    blockingStatus?.activeRoutineId != null
                ) {
                    "A ROUTINE SESSION IS RUNNING — ITS LOADOUT GOVERNS, NOT THIS CAP"
                } else {
                    "THIS CAP SHOWS YOUR LAST HAND PICK"
                },
                fontFamily = AnonymousProFamily,
                fontSize = 11.sp,
                color = scheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .testTag("loadout_cap_source")
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
            RoutinesSection(
                routines = routines,
                showResetNote = showResetNote,
                showFreshStartNote = showFreshStartNote,
                showUpdateNote = showUpdateNote,
                onDismissResetNote = onDismissResetNote,
                onDismissFreshStartNote = onDismissFreshStartNote,
                onDismissUpdateNote = onDismissUpdateNote,
                activeRoutineId = blockingStatus?.activeRoutineId,
                onToggleRoutine = onToggleRoutine,
                onCycleRoutineLoadout = onCycleRoutineLoadout,
                onAdjustRoutineTime = onAdjustRoutineTime,
                onToggleRoutineDay = onToggleRoutineDay,
                onSkipRoutineToday = onSkipRoutineToday,
                onResumeRoutine = onResumeRoutine,
                onDeleteRoutine = onDeleteRoutine,
                onRenameRoutine = onRenameRoutine,
                onSetRoutineTime = onSetRoutineTime
            )
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
                    // Issue #129 F5: the tally's OPEN figure is
                    // exposure, and the slip now says so in words
                    // — an open app is not bookkeeping, it is a
                    // door nothing guards for a whole session.
                    if (rules.unguardedCount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${rules.unguardedCount} OPEN — NOT HELD. ANY OF THEM CAN BE USED FOR THE WHOLE SESSION.",
                            fontFamily = AnonymousProFamily,
                            fontSize = 11.sp,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier.testTag("open_exposure_line")
                        )
                    }
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
            val peekLines = slipLines(AppBlockTier.PEEK_2M, rules, restingFlips, allApps)
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
            val shutLines = slipLines(AppBlockTier.LOCKED, rules, restingFlips, allApps)
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
            val openLines = slipLines(AppBlockTier.OPEN, rules, restingFlips, allApps)
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

        // #129 F1: the review prompt — apps the keyword guess set
        // OPEN that he never ruled on, named, directly above the
        // catalogue where the ruling is made. No tap target: it
        // leaves only by being answered, app by app.
        if (unreviewedOpenText != null) {
            item {
                Text(
                    text = unreviewedOpenText,
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("unreviewed_open_note")
                )
            }
        }

        // =====================================================================
        // SECTION 3.9: THE GUARDS + BLOCKED WEBSITES (issue #51 F1
        // cure, moved by R-3 of issue #7) — the sniper, the browser
        // blocker and the profiles were wired from MainActivity to
        // real ViewModel functions and rendered NOWHERE; this is
        // their UI. R-3's move: the pair sat below the whole
        // catalogue (~6,100dp deep) — an editor nobody reaches does
        // not exist — so both sections now stand above ALL APPS,
        // the switches directly above BLOCKED WEBSITES, whose
        // header claimed sites were blocked with no switch in
        // reach.
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
        // SECTION 3.95: BLOCKED WEBSITES — the editor transplanted
        // from the dissolved Guard screen (chrome wave). The Guard
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

                // R-3 (issue #7): capped at five rows + SHOW ALL —
                // the full list is one tap away, never a scroll tax
                // on the catalogue below it.
                var showAllSites by remember { mutableStateOf(false) }
                val shownSites = if (showAllSites) blockedSites else blockedSites.take(5)
                shownSites.forEach { domain ->
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

                if (blockedSites.size > 5) {
                    BlockitActionChip(
                        wordLabel = if (showAllSites) "SHOW FEWER" else "SHOW ALL ${blockedSites.size} SITES",
                        onClick = { showAllSites = !showAllSites }
                    )
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

        // =====================================================================
        // SECTION 4: SEARCH & CATEGORY FILTER STRIP
        // =====================================================================
        item {
            // Dens F6 (issue #7): SHOWING prints only when the
            // catalogue is actually filtered — an unfiltered count
            // here was a second home for the tally's numbers.
            if (filteredApps.size != allApps.size) {
                FullBleedSectionHeader(
                    titleWord = "ALL APPS",
                    rightWordPrefix = "SHOWING ",
                    rightMonoNumber = filteredApps.size.toString()
                )
            } else {
                FullBleedSectionHeader(titleWord = "ALL APPS")
            }
            Text(
                text = "APPS ON THIS PHONE ONLY — NOTHING HERE CAN HOLD AN APP ON ANOTHER DEVICE",
                fontFamily = AnonymousProFamily,
                fontSize = 10.sp,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.testTag("shield_catalog_scope_line")
            )
        }

        // Issue #142: the search bar and the category chips PIN to
        // the top of the list (stickyHeader). Finding an app used
        // to start with scrolling back up the page; now the strip
        // stays where the results are, and the query can be refined
        // or cleared in place (the ✕ at the bar's end).
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(scheme.background)
                    .padding(vertical = 8.dp)
            ) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
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
                    if (searchQuery.isNotEmpty()) {
                        Text(
                            text = "✕",
                            fontFamily = AnonymousProFamily,
                            fontSize = 13.sp,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier
                                .clickable { onSearchQueryChanged("") }
                                .padding(start = 8.dp)
                                .testTag("shield_search_clear")
                        )
                    }
                }
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
            // The zero-result line pins WITH the search strip
            // (issue #142, closure fix): the answer stays on screen
            // while the query is refined, instead of living in a
            // list item the catalogue can scroll past. The line's
            // semantics are unchanged - an empty list is answered
            // in print, never with a void (Liv/Gust); a search hit
            // under another category says exactly that (fresh-eyes
            // L7); the existence check uses the SAME match as the
            // filter (appMatchesQuery, #107/#142), and the search
            // line never claims an app is absent from the phone.
            if (filteredApps.isEmpty()) {
                val existsOutsideFilter = searchQuery.isNotBlank() && allApps.any {
                    com.example.ui.appMatchesQuery(it, searchQuery)
                }
                Text(
                    text = when {
                        searchQuery.isNotBlank() && existsOutsideFilter ->
                            "THAT NAME IS ON THIS PHONE — NOT IN THIS CATEGORY"
                        searchQuery.isNotBlank() && selectedCategory != null ->
                            "NO MATCH FOR THAT SEARCH IN ${selectedCategory.label.uppercase()} — TRY THE APP'S NAME OR ITS PACKAGE NAME"
                        searchQuery.isNotBlank() ->
                            "NO MATCH FOR THAT SEARCH — TRY THE APP'S NAME OR ITS PACKAGE NAME"
                        selectedCategory != null ->
                            "NO APPS IN THIS CATEGORY ON THIS PHONE"
                        else -> "NO APPS ON THIS PHONE"
                    },
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .testTag("shield_no_match_line")
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
        // SECTION 5: ULTRA-MINIMAL 3-STOP APP SLED ROWS (Decision #14),
        // grouped by ruling (R-1, second half; issue #7): a group
        // header per ruling present — PEEK before SHUT, OPEN last —
        // the sled rows unchanged inside their groups.
        // =====================================================================
        catalogueGroups.forEach { (tier, groupApps) ->
            item(key = "catalogue_group_${tier.name}") {
                CatalogueGroupHeader(tier = tier)
            }
            items(groupApps, key = { it.packageName }) { app ->
                CatalogueAppRow(
                    app = app,
                    effectiveNight = effectiveNight,
                    soundEngine = soundEngine,
                    onSetAppTier = onSetAppTier,
                    onCycleDailyLimit = onCycleDailyLimit,
                    onSetAppCategory = onSetAppCategory
                )
            }
        }

        // The first sentence the installed-only cure lets the app say
        // honestly (Liv, adopted by Gust): an empty list is answered
        // in print, never with a void. Fresh-eyes L7: the filtered
        // list is query AND category chip, so a search hit that
        // lives under another category must say exactly that — the
        // phone fact is only claimed when the name matches NOTHING
        // in the full catalog. Issue #51 F5 widened the trigger:
        // the non-search empties (a category void, a whole-list
        // void) print their fact too. (Union merged by STOSS-34:
        // L7's cross-category case first, #107's never-deny search
        // line second, #51's two void facts last.)
        if (filteredApps.isEmpty()) {
            item {
                val existsOutsideFilter = allApps.any {
                    it.appName.contains(searchQuery, ignoreCase = true)
                }
                Text(
                    text = when {
                        searchQuery.isNotBlank() && existsOutsideFilter ->
                            "THAT NAME IS ON THIS PHONE — NOT IN THIS CATEGORY"
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
                // #335 F5: the detector-health line — first/last
                // node, stale and slow-scan states, printed from
                // the DetectorHealth record the services write.
                Text(
                    text = com.example.ui.components.detectorLineFor(
                        com.example.service.DetectorHealth.snapshot(),
                        SystemClock.elapsedRealtime()
                    ).text,
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.testTag("shield_detector_health_line")
                )
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
// ------------------------------------------------------------------
// Issue #90 (complexity F4, section extractions): the banner pair
// and the routines section used to live inline in ShieldMatrixScreen's
// LazyColumn. They are standalone composables now, with every input
// passed explicitly; bodies are verbatim, behaviour unchanged.
// ------------------------------------------------------------------

@Composable
private fun RatchetWarningBanner(
    message: String?,
    onAddRoutine: (() -> Unit)? = null
) {
    val scheme = MaterialTheme.colorScheme
    AnimatedVisibility(visible = !message.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(scheme.primaryContainer)
                .padding(12.dp)
        ) {
            Text(
                text = message ?: "",
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TokenColorOnPrimary
            )
        }
        if (onAddRoutine != null) {
            // #153 F4: a day that needs a different block can
            // get one — routines are addable, not just seeded.
            BlockitActionChip(wordLabel = "+ ADD ROUTINE", isPrimaryInk = true, onClick = onAddRoutine)
        }
    }
}

/** Issue #141: the sled commit's receipt + take-back. A tier
 *  change used to land silently and forever; while the undo
 *  window lasts, the banner names what changed and offers
 *  the reversal on the spot. */
@Composable
private fun TierChangeUndoBanner(
    change: TierChangeForUndo?,
    onUndo: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    AnimatedVisibility(visible = change != null) {
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
                        modifier = Modifier.clickable { onUndo() },
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

/** Issue #90 (complexity F4, section extraction): the ROUTINES
 *  section — header, the one-time notes (fresh start / update /
 *  reseed) and the routine rows — as one private composable.
 *  Re-extracted at the #90 merge onto current main: the notes
 *  (R3b #161) and the wider RoutineRow API are main's, verbatim. */
@Composable
private fun RoutinesSection(
    routines: List<com.example.data.RoutineEntity>,
    showResetNote: Boolean,
    showFreshStartNote: Boolean,
    showUpdateNote: Boolean,
    onDismissResetNote: () -> Unit,
    onDismissFreshStartNote: () -> Unit,
    onDismissUpdateNote: () -> Unit,
    onToggleRoutine: (com.example.data.RoutineEntity) -> Unit,
    onCycleRoutineLoadout: (com.example.data.RoutineEntity) -> Unit,
    onAdjustRoutineTime: (com.example.data.RoutineEntity, Boolean, Int) -> Unit,
    onToggleRoutineDay: (com.example.data.RoutineEntity, Int) -> Unit,
    onSkipRoutineToday: (com.example.data.RoutineEntity) -> Unit,
    onResumeRoutine: (com.example.data.RoutineEntity) -> Unit,
    onDeleteRoutine: (com.example.data.RoutineEntity) -> Unit,
    onRenameRoutine: (com.example.data.RoutineEntity, String) -> Unit,
    onSetRoutineTime: (com.example.data.RoutineEntity, Int, Int) -> Unit,
    // Issue #7 R-5: the RUNNING NOW marker's source — the active
    // session's routineId, carried ONLY on the derivation's LIVE
    // return (every failure branch leaves it null), so the marker
    // can never print under a NOT BLOCKING headline (N-4 /
    // DIAGNOSE-1R). Null default keeps other callers honest-idle.
    activeRoutineId: Int? = null
) {
    val scheme = MaterialTheme.colorScheme
    FullBleedSectionHeader(
        titleWord = "ROUTINES",
        rightWordPrefix = "",
        // UNA-09, reworded (R-6; issue #7): count what the
        // scheduler counts — LIVE routines only
        // (RoutineSchedule.liveCount: enabled AND at least one
        // day). The word is ARMED, not ACTIVE: a live routine is
        // armed to fire on its days, it is not presently
        // blocking — BLOCKING NOW is the status sentence's claim,
        // proved separately at the top of this screen.
        rightMonoNumber = com.example.data.RoutineSchedule.liveCount(routines).toString(),
        rightWordSuffix = " ARMED"
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
                activeRoutineId = activeRoutineId,
                onToggle = { onToggleRoutine(routine) },
                onCycleLoadout = { onCycleRoutineLoadout(routine) },
                onAdjustStart = { delta -> onAdjustRoutineTime(routine, true, delta) },
                onAdjustEnd = { delta -> onAdjustRoutineTime(routine, false, delta) },
                onToggleDay = { day -> onToggleRoutineDay(routine, day) },
                onSkipToday = { onSkipRoutineToday(routine) },
                onResume = { onResumeRoutine(routine) },
                onDelete = { onDeleteRoutine(routine) },
                onRename = { name -> onRenameRoutine(routine, name) },
                onSetTime = { start, end -> onSetRoutineTime(routine, start, end) }
            )
        }
    }
       
}

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

/** Issue #152: the current local UTC offset ("+05:30"; UTC is
 *  "+00:00"), printed beside routine times so the zone a printed
 *  wall time lives in is never left implicit. */
private fun localOffsetLabel(): String {
    val offset = java.time.ZoneId.systemDefault().rules
        .getOffset(java.time.Instant.now())
    return if (offset == java.time.ZoneOffset.UTC) "+00:00" else offset.id
}

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
    activeRoutineId: Int? = null,
    onToggle: () -> Unit,
    onCycleLoadout: () -> Unit,
    onAdjustStart: (Int) -> Unit,
    onAdjustEnd: (Int) -> Unit,
    onToggleDay: (Int) -> Unit,
    onSkipToday: () -> Unit = {},
    onResume: () -> Unit = {},
    onDelete: () -> Unit = {},
    onRename: (String) -> Unit = {},
    onSetTime: (Int, Int) -> Unit = { _, _ -> }
) {
    val scheme = MaterialTheme.colorScheme
    // Collapsed by default (Ren): a routine is one calm line — name, window,
    // days, state. The full control set appears only when the row is tapped,
    // the way Blockit keeps its surfaces quiet until touched.
    var expanded by remember { mutableStateOf(false) }
    // #153 F5: the window is DRAFTED, not live-fired. ±30 taps move
    // only this local draft; APPLY persists once through the single
    // funnel; DISCARD throws the draft away. A tap that would leave
    // the 30 min – 18 h clamp says so on the row instead of dying
    // silently.
    var draftStart by remember(routine.id, routine.startMinutes) { mutableIntStateOf(routine.startMinutes) }
    var draftEnd by remember(routine.id, routine.endMinutes) { mutableIntStateOf(routine.endMinutes) }
    var clampNote by remember(routine.id) { mutableStateOf(false) }
    val draftDirty = draftStart != routine.startMinutes || draftEnd != routine.endMinutes
    fun draftAdjust(isStart: Boolean, delta: Int) {
        val ns = if (isStart) (draftStart + delta + 1440) % 1440 else draftStart
        val ne = if (isStart) draftEnd else (draftEnd + delta + 1440) % 1440
        val span = (ne - ns + 1440) % 1440
        if (span !in 30..1080) { clampNote = true; return }
        clampNote = false
        if (isStart) draftStart = ns else draftEnd = ne
    }
    var renameOpen by remember(routine.id) { mutableStateOf(false) }
    var deleteArmed by remember(routine.id) { mutableStateOf(false) }
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
                    text = fmtRoutineTime(draftStart),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = scheme.onBackground
                )
                Text(
                    text = "→ " + fmtRoutineTime(draftEnd),
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
                    // Finish pass (issue #7): the Sora guest is
                    // evicted from routine rows — one axis, Atkinson
                    // Bold 15sp, like every other working name here.
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = scheme.onBackground,
                    maxLines = 1
                )
                Text(
                    // Issue #152: a routine's printed times are
                    // LOCAL wall times in the zone in force — the
                    // line now says so, with the current offset,
                    // so after a landing no printed routine time
                    // is ambiguous about which zone it means.
                    // R-5's marker rides the same meta line: RUNNING
                    // NOW only when the active session's own
                    // routineId names this routine.
                    text = "${routineDaysLabel(routine.daysMask)} · " +
                        "${routineLoadoutLabel(routine.loadoutName)} · LOCAL ${localOffsetLabel()}" +
                        routineRunningNowSuffix(routine, activeRoutineId),
                    fontFamily = AnonymousProFamily,
                    fontSize = 12.sp,
                    color = scheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            // Toggle as a cap (S1): sized to its payload, one cap grammar
            // with the selector band and sleds — not a 42dp box for a word.
            // R-6 (issue #7): the cap word is the row's armed truth —
            // routineCapWord prints WON'T FIRE for an enabled routine
            // with no days, never a bare ON the scheduler won't honour;
            // the cap widens past 48dp for that word rather than
            // clipping the fact.
            val capWord = routineCapWord(routine.enabled, routine.daysMask)
            // WON'T FIRE wears the middle density: the ink fill is
            // ON's clothes, and this row is not on — surface fill +
            // outline, the word itself carries it.
            val wontFire = capWord == "WON'T FIRE"
            val capFilled = routine.enabled && !wontFire
            Box(
                modifier = Modifier
                    .widthIn(min = 48.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (capFilled) inkControlFill() else scheme.surfaceVariant)
                    .border(1.dp, if (capFilled) inkControlFill() else scheme.outlineVariant, RoundedCornerShape(8.dp))
                    .clickable { onToggle() }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = capWord,
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (capWord.length > 3) 10.sp else 11.sp,
                    // WON'T FIRE prints in FULL ink (issue #7): the
                    // fill stays off — this row is not on — but the
                    // warning word itself is never quiet. ON/OFF
                    // keep the quiet tone.
                    color = when {
                        capFilled -> onInkControlFill()
                        wontFire -> scheme.onBackground
                        else -> scheme.onSurfaceVariant
                    }
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
            BlockitActionChip(wordLabel = "−30", onClick = { draftAdjust(true, -30) })
            BlockitActionChip(wordLabel = "+30", onClick = { draftAdjust(true, 30) })
            Text(
                text = "END",
                fontFamily = AnonymousProFamily,
                fontSize = 11.sp,
                color = scheme.onSurfaceVariant
            )
            BlockitActionChip(wordLabel = "−30", onClick = { draftAdjust(false, -30) })
            BlockitActionChip(wordLabel = "+30", onClick = { draftAdjust(false, 30) })
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
        if (clampNote) {
            Text(
                text = "WINDOW MUST STAY BETWEEN 30 MIN AND 18 H",
                fontFamily = AnonymousProFamily,
                fontSize = 11.sp,
                color = scheme.error
            )
        }
        if (draftDirty) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BlockitActionChip(
                    wordLabel = "APPLY TIME",
                    isPrimaryInk = true,
                    onClick = { onSetTime(draftStart, draftEnd) }
                )
                BlockitActionChip(
                    wordLabel = "DISCARD",
                    onClick = {
                        draftStart = routine.startMinutes
                        draftEnd = routine.endMinutes
                        clampNote = false
                    }
                )
            }
        }
        // #153 F2/F3/F4 verbs: forgive today, resume, rename, delete.
        val pausedNow = routine.pausedUntilMillis > System.currentTimeMillis()
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (pausedNow) {
                BlockitActionChip(wordLabel = "PAUSED — RESUME", isPrimaryInk = true, onClick = onResume)
            } else if (routine.enabled) {
                BlockitActionChip(wordLabel = "SKIP TODAY", onClick = onSkipToday)
            }
            BlockitActionChip(wordLabel = "RENAME", onClick = { renameOpen = true })
            BlockitActionChip(
                wordLabel = if (deleteArmed) "DELETE — SURE?" else "DELETE",
                isPrimaryInk = deleteArmed,
                onClick = { if (deleteArmed) onDelete() else deleteArmed = true }
            )
        }
        }
    }
    if (renameOpen) {
        var draftName by remember(routine.id) { mutableStateOf(routine.name) }
        AlertDialog(
            onDismissRequest = { renameOpen = false },
            title = { Text(text = "RENAME ROUTINE") },
            text = {
                TextField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = { onRename(draftName); renameOpen = false }) {
                    Text(text = "APPLY")
                }
            },
            dismissButton = {
                TextButton(onClick = { renameOpen = false }) { Text(text = "CANCEL") }
            }
        )
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
/** Issue #90: hoisted out of ShieldMatrixScreen's body — it was a
 *  nested local helper defined mid-composable; as a top-level
 *  function its three captures (rules, restingFlips, allApps) are
 *  explicit parameters and the derivation is testable in isolation.
 *
 *  The slip's lines for one section: committed lines in place,
 *  plus any line resting here after a flip — and a line resting
 *  elsewhere is suppressed here, so an app is present in exactly
 *  one section at any instant. */
private fun slipLines(
    tier: AppBlockTier,
    rules: com.example.data.AppRules,
    restingFlips: Map<String, RestingFlip>,
    allApps: List<BlockedAppEntity>
): List<SlipLine> {
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
        compareBy<SlipLine> { it.app.appName.lowercase() }.thenBy { it.app.packageName }
    )
}

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


/** One catalogue group header (R-1; issue #7): the ruling word
 *  only, in ink like the rules header above it. The group's COUNT
 *  does not print here — counts have one home on this screen, the
 *  tally, and a number with two homes is a duplication. */
@Composable
private fun CatalogueGroupHeader(tier: AppBlockTier) {
    Column {
        Text(
            text = tierWord(tier),
            fontFamily = AtkinsonHyperlegibleFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp,
            letterSpacing = 0.6.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(top = 4.dp)
                .testTag("catalogue_group_${tier.name}")
        )
        if (tier == AppBlockTier.PEEK_2M) {
            // R-6 (issue #7): PEEK's contract prints ONCE on this
            // screen, here, derived from the constants — never
            // hardcoded, so an editable-times change reprints the
            // truth for free.
            val peekMin = MonolithRepository.PEEK_DURATION_SECONDS / 60
            val coolMin = (MonolithRepository.PEEK_COOLDOWN_MILLIS / 60_000L).toInt()
            Text(
                text = "PEEK — $peekMin MINUTES, THEN SHUT AGAIN · $coolMin MIN BETWEEN PEEKS",
                fontFamily = AnonymousProFamily,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("peek_contract_line")
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
    onCycleDailyLimit: (BlockedAppEntity) -> Unit = {},
    onSetAppCategory: (String, AppCategory) -> Unit = { _, _ -> }
) {
    val scheme = MaterialTheme.colorScheme
    val category = remember(app.category) {
        runCatching { AppCategory.valueOf(app.category) }.getOrNull()
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        // Issue #142 (closure pass, STURM-4): the app's real icon
        // beside the sled — the audit's twin-apps charge ("Clock"
        // vs "Clock", identical truncated prefixes) is answered by
        // the icon AND the package line below, not the package
        // alone. Same PackageManager pattern as the launcher
        // tiles (LauncherBlockScreen); a package with no icon
        // simply leaves the slot empty.
        val rowContext = LocalContext.current
        val rowIcon: Bitmap? = remember(app.packageName) {
            runCatching {
                val drawable = rowContext.packageManager.getApplicationIcon(app.packageName)
                val bmp = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bmp)
                drawable.setBounds(0, 0, 96, 96)
                drawable.draw(canvas)
                bmp
            }.getOrNull()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (rowIcon != null) {
                Image(
                    bitmap = rowIcon.asImageBitmap(),
                    contentDescription = app.appName,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(end = 10.dp)
                        .testTag("app_icon_${app.packageName}")
                )
            }
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
                    // R-11 core (issue #7): the sled is a Canvas —
                    // without this the row is silent to TalkBack and
                    // Shield cannot be operated by ear. The description
                    // carries name + tier.
                    view.contentDescription = "${app.appName}, ${tierWord(app.resolvedTier)}"
                // Issue #140: the daily limit, printed on the row,
                // and the hold that steps it - see MonolithViewModel
                // .cycleAppDailyLimit.
                view.usageCaption = "USED ${app.todayUsageMinutes} OF ${app.dailyLimitMinutes} MIN TODAY"
                view.onDailyLimitLongPress = { onCycleDailyLimit(app) }
                    view.onTierSelected = { newTier ->
                        onSetAppTier(app.packageName, newTier)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("app_row_${app.packageName}")
            )
        }
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
        // Issue #142: the package name, printed on every row.
        // Similar-named apps (and the twin builds a long name
        // truncates into sameness on the sled) are told apart by
        // the one string that is never shared.
        Text(
            text = app.packageName,
            fontFamily = AnonymousProFamily,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            color = scheme.onSurfaceVariant,
            modifier = Modifier
                .padding(vertical = 1.dp)
                .testTag("app_package_${app.packageName}")
        )
    }
}
