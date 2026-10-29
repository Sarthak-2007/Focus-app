package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.BlockedBrowserDomains
import com.example.data.MonolithRepository
import com.example.data.SmartLoadout
import com.example.data.groupedForCatalogue
import com.example.service.TactileSoundEngine
import com.example.ui.ShieldBlockingProof
import com.example.ui.ShieldBlockingStatus
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
import com.example.ui.views.AppRowSledToggleView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    // R-9 (Shield audit; wave-2): the phantom parameters are STRUCK.
    // shortsReelsSniperEnabled / browserUrlBlockerEnabled / profiles /
    // selectedProfileName and their callbacks each occurred exactly
    // once in this contract — declaration only. PARK owns both
    // switches (its sleds); a parameter that exists so other code
    // compiles is not a surface.
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
    soundEngine: TactileSoundEngine? = null,
    isDarkTheme: Boolean = false,
    onSearchQueryChanged: (String) -> Unit,
    onSelectCategory: (AppCategory?) -> Unit,
    onSetAppTier: (String, AppBlockTier) -> Unit,
    onApplySmartLoadout: (SmartLoadout) -> Unit,
    routines: List<com.example.data.RoutineEntity> = emptyList(),
    showResetNote: Boolean = false,
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
    val scope = rememberCoroutineScope()
    val effectiveNight = isDarkTheme || isSessionActive
    // UNA-07: the cap rests on the STORED FACT (loadoutCapIndex —
    // his last hand pick), not on a bare remember defaulting to
    // STUDY. The local state still answers a tap instantly; the
    // remember key re-seats it whenever the stored fact (re)loads.
    var selectedLoadoutIndex by remember(loadoutCapIndex) { mutableIntStateOf(loadoutCapIndex) }

    val shutCount = remember(allApps) { allApps.count { it.resolvedTier == AppBlockTier.LOCKED } }
    val peekCount = remember(allApps) { allApps.count { it.resolvedTier == AppBlockTier.PEEK_2M } }

    // =====================================================================
    // THE STANDING ORDERS, DE-DUPLICATED (Shield audit R-1; issue
    // #7). The slip's per-app lines are RETIRED: they enumerated
    // every ruled app a second time above the catalogue that
    // already enumerates them, and the duplication cost ~1,230dp
    // of pre-catalogue scroll. What remains of the slip is its
    // tally — three counts that sum to the phone, state only —
    // and the catalogue below now groups by ruling (see
    // groupedForCatalogue), so every app's name and ruling print
    // exactly once on this screen, on the sled row that can also
    // change it.
    // =====================================================================
    val openCount = allApps.size - shutCount - peekCount

    // M6 (Mikro): return is memory — the list reopens where his thumb
    // left it. Saveable, so the place survives this screen being
    // disposed on a tab switch.
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    // M4, governed (Gust): the tally never rolls its digits. A burst
    // of flips coalesces — the whole line re-strikes once, to the
    // latest figures, after they have been stable 250ms.
    val tallyNow = Triple(shutCount, peekCount, openCount)
    var tallyShown by remember { mutableStateOf(tallyNow) }
    LaunchedEffect(tallyNow) {
        delay(250)
        tallyShown = tallyNow
    }

    // R-5: the status sentence. The supplied derivation is the
    // only source. With none supplied (the cold-start instant
    // before the ViewModel's first witness read, a render
    // harness), the screen prints neither BLOCKING NOW nor a
    // proven NOT BLOCKING: it prints the unproven fallback —
    // a session row's isActive flag is a claim, not enforcement,
    // and its absence before the first Room emission is not a
    // measured fact either (critic Salz C-3).
    val statusHeadline = blockingStatus?.headline
        ?: "ENFORCEMENT NOT PROVEN — NO WITNESS READ YET"

    // The catalogue's one enumeration, grouped by ruling.
    val catalogueGroups = remember(filteredApps) { filteredApps.groupedForCatalogue() }

    // R-3 state: the websites cap and the failed-ADD print.
    var showAllSites by remember { mutableStateOf(false) }
    var siteError by remember { mutableStateOf<String?>(null) }

    // R-1 tally jumps (Plan §5.1): the tally's words are jumps.
    // Item index of a catalogue group header, counted in the
    // LazyColumn's own order: status, banner, cap, routines,
    // tally, [prune], websites, search — then per group a header
    // plus its rows.
    fun catalogueGroupIndex(tier: AppBlockTier): Int {
        var idx = 7 + if (pruneNoteText != null) 1 else 0
        for ((t, apps) in catalogueGroups) {
            if (t == tier) return idx
            idx += 1 + apps.size
        }
        return idx
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
        // SECTION 0: THE STATUS SENTENCE (R-5, merge item #1) —
        // the first line on the screen, above every ruling it
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
            // it does not), never by colour alone. The slot is a
            // fixed 40dp so a re-strike never shoves the screen.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .semantics { contentDescription = statusHeadline },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (blockingStatus?.proof == ShieldBlockingProof.LIVE) "●" else "○",
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = scheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                // The re-strike (PANZER/SCHWUNG): a changed verdict
                // re-prints as a whole line, 260ms Curve A — the
                // tally's governed manners, never a partial swap.
                AnimatedContent(
                    targetState = statusHeadline,
                    transitionSpec = {
                        fadeIn(tween(260, easing = MonolithCurves.EasingA)) togetherWith
                            fadeOut(tween(260, easing = MonolithCurves.EasingA))
                    },
                    label = "shield_blocking_status"
                ) { headline ->
                    Text(
                        text = headline,
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = scheme.onBackground,
                        modifier = Modifier.testTag("shield_blocking_status")
                    )
                }
            }
        }

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
                        // R-7: tokenised — the old hardcoded 0xFFFBF1EC was
                        // unprovable in the day theme.
                        color = scheme.onPrimaryContainer
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
            // R-4(c) (Dens F5): the LOADOUT header is KILLED — the
            // cap self-labels (its detents print STUDY/WORK/SLEEP);
            // the slot pays ~32dp back to the screen.
            ShieldCapSelector(
                selectedIndex = selectedLoadoutIndex,
                soundEngine = soundEngine,
                onSelect = { level ->
                    selectedLoadoutIndex = level
                    when (level) {
                        0 -> onApplySmartLoadout(SmartLoadout.STUDY)
                        1 -> onApplySmartLoadout(SmartLoadout.WORK_SPRINT)
                        else -> onApplySmartLoadout(SmartLoadout.SLEEP)
                    }
                }
            )
            // R-4(b), the honest half this screen can prove: the cap
            // rests on the persisted LAST HAND PICK — it must never
            // wear a running routine's clothes. When a routine
            // session is the live one, the print says so; otherwise
            // it names the cap's source as the hand pick it is.
            Text(
                text = if (blockingStatus?.activeRoutineId != null) {
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
        }

        // =====================================================================
        // SECTION 3: ROUTINES — real, persisted, self-arming (Ren)
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "ROUTINES",
                rightWordPrefix = "",
                // UNA-09, reworded (R-6; issue #7): count what the
                // scheduler counts — LIVE routines only
                // (RoutineSchedule.liveCount: enabled AND at least
                // one day). A daysMask == 0 routine the chain skips
                // is never counted. The word is ARMED, not ACTIVE:
                // a live routine is armed to fire on its days, it
                // is not presently blocking — BLOCKING NOW is the
                // status sentence's claim, and it is proved
                // separately, at the top of this screen.
                rightMonoNumber = com.example.data.RoutineSchedule.liveCount(routines).toString(),
                rightWordSuffix = " ARMED"
            )
            if (showResetNote) {
                // One-time reseed strip (Freya §3.3): his schedule was
                // reset to his real day; any row edit dismisses it.
                Text(
                    text = "YOUR DAY, SEEDED — TAP A ROW TO OPEN IT · TIMES YOU SET ARE NEVER CHANGED",
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .testTag("routine_reset_note")
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                routines.forEach { routine ->
                    RoutineRow(
                        routine = routine,
                        // RUNNING NOW's source: the active
                        // session's own routineId, carried on the
                        // status derivation — never window
                        // arithmetic (see routineRunningNowSuffix).
                        activeRoutineId = blockingStatus?.activeRoutineId,
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
        // SECTION 3.5: YOUR RULES — THE TALLY ONLY (R-1; issue #7).
        // Three numbers that sum to the phone — a ledger that
        // doesn't balance is a lie — and nothing else: the slip's
        // per-app lines are retired, and the rulings themselves
        // print once, in the grouped catalogue below, on the sled
        // rows that can change them. Under search the tally folds
        // into its header line (Liv D6): search is a finding tool
        // for the catalogue and never filters the counts, or they
        // would stop being counts of his decisions.
        // =====================================================================
        if (searchQuery.isBlank()) {
            item {
                Column {
                    RulesHeaderRow(right = null)
                    Spacer(modifier = Modifier.height(8.dp))
                    StandingTallyLine(
                        counts = tallyShown,
                        onJump = { tier ->
                            scope.launch {
                                listState.animateScrollToItem(catalogueGroupIndex(tier))
                            }
                        }
                    )
                }
            }
        } else {
            item {
                RulesHeaderRow(right = tallyText(shutCount, peekCount, open = null))
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
        // SECTION 3.9: BLOCKED WEBSITES (R-3: ABOVE ALL APPS) — the editor transplanted from
        // the dissolved Guard screen (chrome wave) — and MOVED by
        // R-3 (wave-2): it sat ~6,100dp deep under the catalogue;
        // an editor nobody reaches does not exist. Same rows, same
        // add field, same wiring — the list below is the list the
        // blocker enforces.
        // =====================================================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FullBleedSectionHeader(
                    titleWord = "BLOCKED WEBSITES",
                    rightWordPrefix = "",
                    rightMonoNumber = blockedSites.size.toString(),
                    rightWordSuffix = " SITES"
                )

                // R-3 (wave-2): capped at five rows + SHOW ALL —
                // the full list is one tap away, never a scroll tax
                // on the catalogue above it.
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

                var newSiteText by remember { mutableStateOf("") }
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
                            // R-3 truth fix (Fremd C9b): a rejected ADD
                            // PRINTS WHY and KEEPS the text. The old
                            // form cleared the field and dropped the
                            // failure silently — a print claiming
                            // success for an add that never happened.
                            // The screen judges with the repository's
                            // own normaliser, so the verdict it prints
                            // is the verdict the store will give.
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
            // Dens F6: SHOWING prints only when the catalogue is
            // actually filtered — an unfiltered count here was a
            // second home for the tally's numbers.
            if (filteredApps.size != allApps.size) {
                FullBleedSectionHeader(
                    titleWord = "ALL APPS",
                    rightWordPrefix = "SHOWING ",
                    rightMonoNumber = filteredApps.size.toString()
                )
            } else {
                FullBleedSectionHeader(titleWord = "ALL APPS")
            }
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
                        text = "SEARCH APPS",
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

        // =====================================================================
        // SECTION 5: ULTRA-MINIMAL 3-STOP APP SLED ROWS (Decision
        // #14), GROUPED BY RULING (R-1; issue #7) — the screen's
        // one enumeration of his decisions: a group header per
        // ruling present (PEEK before SHUT, OPEN last), the sled
        // rows unchanged inside their groups. Book and catalogue
        // are one surface now; a ruling is read and revoked in
        // the same place.
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
                    onSetAppTier = onSetAppTier
                )
            }
        }

        // The first sentence the installed-only cure lets the app say
        // honestly (Liv, adopted by Gust): a search with no match is
        // answered in print, never with a void.
        if (filteredApps.isEmpty() && searchQuery.isNotBlank()) {
            item {
                Text(
                    text = "NO APP BY THAT NAME ON THIS PHONE",
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.testTag("shield_no_match_line")
                )
            }
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
    activeRoutineId: Int? = null,
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
                    // Finish pass (Panzer item 7): the Sora guest is
                    // evicted from routine rows — one axis, Atkinson
                    // Bold 15sp, like every other working name here.
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = scheme.onBackground,
                    maxLines = 1
                )
                Text(
                    // R-5's marker rides the meta line: RUNNING NOW
                    // only when the active session's own routineId
                    // names this routine.
                    text = "${routineDaysLabel(routine.daysMask)} · " +
                        "${routineLoadoutLabel(routine.loadoutName)}" +
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
            // WON'T FIRE wears the middle density (designer §3.3):
            // the ink fill is ON's clothes, and this row is not on —
            // surface fill + outline, the word itself carries it.
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
                    // WON'T FIRE prints in FULL ink (wave-2 seat
                    // cure): the fill stays off — this row is not
                    // on — but the warning word itself is never
                    // quiet. ON/OFF keep the quiet tone.
                    color = when {
                        capFilled -> onInkControlFill()
                        wontFire -> scheme.onBackground
                        else -> scheme.onSurfaceVariant
                    }
                )
            }
        }
        if (expanded) {
        Row(
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
// THE RULES REGION'S FURNITURE — the tally header and line, and
// the catalogue's group headers (Ordner lane, as recomposed by
// R-1). Print only: no icons, no boxes, no accent. Ink. The slip's
// line furniture (RestingFlip, SlipLineRow and their clocks) is
// retired with the lines themselves — the catalogue's sled rows
// are the one place a ruling prints per-app now.
// =====================================================================

private fun tierWord(tier: AppBlockTier): String = when (tier) {
    AppBlockTier.LOCKED -> "SHUT"
    AppBlockTier.PEEK_2M -> "PEEK"
    AppBlockTier.OPEN -> "OPEN"
}

/** The tally's mixed grammar — Anonymous Pro numerals, Atkinson
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

/** The rules header row — mirrors FullBleedSectionHeader's shape
 *  with one deliberate exception (Liv): the title prints in ink.
 *  Every other section on this screen is grey; his decisions are
 *  the one section that speaks at full volume. [right] carries the
 *  folded tally under search, and nothing when the tally is open. */
@Composable
private fun RulesHeaderRow(right: AnnotatedString?) {
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
private fun StandingTallyLine(
    counts: Triple<Int, Int, Int>,
    onJump: (AppBlockTier) -> Unit = {}
) {
    val scheme = MaterialTheme.colorScheme
    AnimatedContent(
        targetState = counts,
        transitionSpec = {
            fadeIn(tween(260, easing = MonolithCurves.EasingA)) togetherWith
                fadeOut(tween(260, easing = MonolithCurves.EasingA))
        },
        label = "standing_tally"
    ) { (shut, peek, open) ->
        // Plan §5.1 (R-1 completion, wave-2): the tally's words are
        // JUMPS — tap a ruling and the catalogue scrolls to its
        // group. Three segments, one line, the tally's own mixed
        // typography (mono numerals, Atkinson words) in each.
        Row(modifier = Modifier.testTag("standing_tally")) {
            TallySegment(shut, " SHUT · ", scheme.onBackground) { onJump(AppBlockTier.LOCKED) }
            TallySegment(peek, " PEEK · ", scheme.onBackground) { onJump(AppBlockTier.PEEK_2M) }
            TallySegment(open, " OPEN", scheme.onBackground) { onJump(AppBlockTier.OPEN) }
        }
    }
}

@Composable
private fun TallySegment(count: Int, word: String, color: Color, onClick: () -> Unit) {
    val text = buildAnnotatedString {
        withStyle(SpanStyle(fontFamily = AnonymousProFamily, fontWeight = FontWeight.Bold)) {
            append(count.toString())
        }
        withStyle(SpanStyle(fontFamily = AtkinsonHyperlegibleFamily, fontWeight = FontWeight.Bold)) {
            append(word)
        }
    }
    Text(
        text = text,
        fontSize = 11.5.sp,
        color = color,
        modifier = Modifier
            .clickable { onClick() }
            .semantics { contentDescription = "$count ${word.trim().trim('·')}" }
    )
}

/** One catalogue group header (R-1): the ruling word only, in
 *  ink like the rules header above it. The group's COUNT does
 *  not print here — counts have one home on this screen, the
 *  tally, and a number with two homes is a duplication
 *  (R-13; critic Salz C-11.1). */
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
            // R-6: PEEK's contract prints ONCE on this screen, here,
            // derived from the constants — never hardcoded, so an
            // editable-times change reprints the truth for free.
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
 *  ruling is printed, made, and revoked — its one home (R-1). */
@Composable
private fun CatalogueAppRow(
    app: BlockedAppEntity,
    effectiveNight: Boolean,
    soundEngine: TactileSoundEngine?,
    onSetAppTier: (String, AppBlockTier) -> Unit
) {
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
            // R-11 core: the sled is a Canvas — without this the
            // row is silent to TalkBack and Shield cannot be
            // operated by ear. The description carries name + tier.
            view.contentDescription = "${app.appName}, ${tierWord(app.resolvedTier)}"

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
