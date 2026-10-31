package com.example.ui.screens
import androidx.compose.ui.res.stringResource
import com.example.R
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import com.example.data.CurioCatalog
import com.example.data.ClockText
import androidx.compose.ui.platform.LocalContext
import com.example.data.FocusSessionEntity
import com.example.data.HistoryCsv
import com.example.data.ForgeStat
import com.example.data.KineticArtifact
import com.example.data.PARTIAL_STAMP_ARTIFACT
import com.example.data.PARTIAL_STAMP_ID
import com.example.data.ReceiptIndexEntry
import com.example.data.SessionOutcome

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import com.example.data.DayWordStore
import com.example.data.DrawerDay
import com.example.data.FocusTaskEntity
import com.example.data.StoredMonth
import com.example.ui.components.BlockitActionChip
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * THE DRAWER — the kept Ledger stop, rebuilt as storage.
 *
 * Permanent storage for finished tasks, read back by month and by
 * search. It is not a dashboard, report card or attendance book:
 * no totals, no streaks, no percentages, no counts, no summons.
 * A stored task is a COMPLETED row of focus_tasks; it is never
 * auto-deleted, and nothing on this screen can change one. While
 * sealed the Drawer is read-only furniture — rows and detail read
 * fully and SEARCH stays live (AUDIT-drawer §2, wave-2): a query
 * mutates nothing stored, so the sealed box is never a painted
 * door that invites typing it refuses. Day-word filing is an
 * edit and stays inert under seal.
 *
 * Days file by the 03:30 rule (DrawerDay); row finish times print
 * true clock. A declared day word (REST / ILL / FAMILY) prints
 * beside its date heading as his authored word — never a number.
 */

private enum class DrawerView { LANDING, INDEX, MONTH, CABINET, RECEIPTS }

/** Audit R3b #145, reopened defect A: the cabinet's standing forge
 *  rule, as ONE named print so a test can pin the exact sentence.
 *  The sentence is scoped to what the forge gates actually do
 *  (MonolithRepository): MANUAL — a routine-born span forges
 *  nothing (the finalise gate is `allBlocksCompleted &&
 *  !isRoutineSession`, and the disarm door fences routine rows
 *  too); IN FULL — a timed span hand-ended before all its blocks
 *  complete forges nothing (at most a Partial Stamp), and a FLOW
 *  span forges only at its completed hand finish. The earlier
 *  print ("A SPAN YOU FINISH YOURSELF FORGES ONE") claimed both
 *  exceptions away. */
internal val CABINET_FORGE_RULE: String =
    "A MANUAL SPAN YOU FINISH IN FULL FORGES ONE — THE FORGE CHOOSES WHICH."

internal fun curioForgeGapMark(record: FocusSessionEntity): String =
    if (record.outcome == SessionOutcome.ELAPSED_SHIELD_GAP ||
        record.shieldGapSeen
    ) {
        " · SHIELD GAP"
    } else {
        ""
    }

private data class DrawerDayGroup(
    val date: LocalDate,
    val rows: List<FocusTaskEntity>,
    val word: String?
)

/** Group rows by filing date and merge declared words + (optionally)
 *  today's group, newest date first. Grouping runs over the whole
 *  accumulated list, so a day straddling a page seam merges when
 *  EARLIER lands. */
private fun buildDayGroups(
    rows: List<FocusTaskEntity>,
    dayWords: Map<String, String>,
    scopeMonth: Pair<Int, Int>?,
    includeTodayGroup: Boolean,
    today: LocalDate
): List<DrawerDayGroup> {
    val byDate = rows.groupBy { DrawerDay.filingDate(it.completedAtMillis) }.toMutableMap()
    val dates = byDate.keys.toMutableSet()
    for (iso in dayWords.keys) {
        val date = runCatching { LocalDate.parse(iso) }.getOrNull() ?: continue
        if (scopeMonth == null || (date.year == scopeMonth.first && date.monthValue == scopeMonth.second)) {
            dates.add(date)
        }
    }
    if (includeTodayGroup) dates.add(today)
    return dates.sortedDescending().map { date ->
        DrawerDayGroup(
            date = date,
            rows = byDate[date].orEmpty(),
            word = dayWords[DrawerDay.dateIso(date)]
        )
    }
}

private fun headingLabel(date: LocalDate, withYear: Boolean): String {
    val pattern = if (withYear) "EEE d MMM yyyy" else "EEE d MMM"
    return DateTimeFormatter.ofPattern(pattern, Locale.US).format(date).uppercase(Locale.US)
}

private fun monthLabel(year: Int, month: Int): String =
    DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)
        .format(LocalDate.of(year, month, 1)).uppercase(Locale.US)

private fun clockTime(millis: Long): String =
    SimpleDateFormat("HH:mm", Locale.US).format(Date(millis))

/** The app's duration voice for a stored "spent": 45 SEC · 24 MIN · 1H 05M. */
private fun spentLabel(totalSeconds: Int): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return when {
        h > 0 -> "${h}H ${String.format(Locale.US, "%02d", m)}M"
        m > 0 -> "$m MIN"
        else -> "$s SEC"
    }
}

@Composable
fun DrawerScreen(
    storedMonths: List<StoredMonth>,
    drawerMonth: Pair<Int, Int>,
    drawerRows: List<FocusTaskEntity>,
    drawerHasEarlier: Boolean,
    drawerQuery: String,
    drawerResults: List<FocusTaskEntity>,
    drawerSearchHasEarlier: Boolean,
    // #158 F2: the ViewModel's word-aware search state — every
    // date carrying a word the query names (word-only days
    // included), merged with the words of the task-hit dates.
    // Defaults empty so a caller without it still gets the
    // dayWords-derived search rendering below.
    drawerSearchDayWords: Map<String, String> = emptyMap(),
    dayWords: Map<String, String>,
    isSealed: Boolean,
    isDarkTheme: Boolean = false,
    onSelectMonth: (Int, Int) -> Unit,
    onLoadEarlier: () -> Unit,
    onQueryChange: (String) -> Unit,
    onLoadEarlierSearch: () -> Unit,
    onDeclareTodayWord: (String?) -> Unit,
    // R3b #163 F1: yesterday's word stays declarable the morning
    // after. (F2/F3 struck — see DrawerDayGroup.)
    onDeclareDayWord: (LocalDate, String?) -> Unit = { _, _ -> },
    // Audit R6 #341 F58: an armed day-word overwrite awaiting its
    // confirm tap (null = nothing armed). Defaults keep render
    // harnesses unchanged.
    pendingDayWordConfirm: com.example.data.PendingDayWordDeclaration? = null,
    onConfirmPendingDayWord: () -> Unit = {},
    onCancelPendingDayWord: () -> Unit = {},
    // The cabinet (chrome C10): the curios' new home. The Drawer is
    // storage; the cabinet is its furniture — the six artifacts as
    // ruled rows, their forged state read from real sessions, their
    // detail carrying the lore the Vault screen printed. The
    // kinetic toy VISUALS were never built as views in the shipped
    // app and are not built here; the cabinet is the catalog and
    // the record — not the toys. (Truth Car 2: the requirements
    // print, the SELECT verb, and the serial-plate synth egg were
    // removed with DRW-01/06/07 — each printed a fact the app does
    // not hold.)
    artifacts: List<KineticArtifact> = CurioCatalog.artifacts,
    forgedArtifactIds: Set<String> = emptySet(),
    // Audit R3b #145: forge provenance per artifact — the sessions
    // that forged it (date, intention, outcome ride the row), so
    // the cabinet can count duplicates and name their spans.
    curioForgeRecords: Map<String, List<FocusSessionEntity>> = emptyMap(),
    // Issue #110, finding 5: the forge record per artifact — count
    // of forging sessions + most recent forge instant, derived by
    // the repository from stored session rows. The cabinet detail
    // prints it, so a forged artifact names real days instead of
    // only standing there. The Partial Stamp row joins the list
    // whenever a session actually carries the stamp.
    forgeStats: Map<String, ForgeStat> = emptyMap(),
    // Issue #157: the Vault IS the "Receipts" destination, so the
    // receipts live here — an index of every claimed slip, each
    // row carrying only what its session row proves. Tapping a
    // row re-presents that session's slip (the host renders the
    // same ReceiptSheet); the slip itself is never stored and
    // nothing is printed on it from here.
    receipts: List<ReceiptIndexEntry> = emptyList(),
    onReceiptClick: (Int) -> Unit = {},
    // Issue #253 line 39: session-history CSV export. The host
    // supplies the document text on demand (a suspend read
    // through the pure HistoryCsv formatter); null = no export
    // door stands on this landing (previews, tests).
    historyCsvProvider: (suspend () -> String)? = null,
    // Issue #52 F3 cure: the month read's state, from the
    // ViewModel — loading while a page is in flight, an error
    // line when the read failed. An empty month, a loading
    // month and a broken month are three different prints.
    drawerLoading: Boolean = false,
    drawerError: String? = null
) {
    val scheme = MaterialTheme.colorScheme
    // Issue #253 line 39: the export door's machinery. The tap
    // reads the history document through the host's provider,
    // then the system document picker chooses where it lands;
    // the picked document receives the CSV byte-for-byte. A
    // cancelled picker writes nothing, and a failed read opens
    // no picker — the door never fabricates a file.
    val exportProvider = historyCsvProvider
    val exportContext = LocalContext.current
    val exportScope = rememberCoroutineScope()
    var pendingExportCsv by remember { mutableStateOf<String?>(null) }
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        val csv = pendingExportCsv
        pendingExportCsv = null
        if (uri != null && csv != null) {
            runCatching {
                exportContext.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(csv.toByteArray(Charsets.UTF_8))
                }
            }
        }
    }
    // AUDIT-drawer §2 (wave-2): view + selection IDs are saveable —
    // the view name and the selected task's ID survive process
    // death; a mid-browse man is no longer reset to LANDING by the
    // OS. The task object itself is re-read from the accumulated
    // rows at composition, never stored in the bundle. TRUE SCOPE
    // (closer correction, SPITZ Charge 8): these are two
    // rememberSaveable scalars — there is NO formal DrawerViewState
    // type and DETAIL is not a named view state; both are unbuilt
    // and routed to #15, as the wave-2 report's own REMAINDERS say.
    var viewName by rememberSaveable { mutableStateOf(DrawerView.LANDING.name) }
    val view = runCatching { DrawerView.valueOf(viewName) }.getOrDefault(DrawerView.LANDING)
    var selectedTaskId by rememberSaveable { mutableStateOf(-1) }
    val selectedTask: FocusTaskEntity? =
        (drawerRows + drawerResults).firstOrNull { it.id == selectedTaskId }
    var cabinetArtifactId by rememberSaveable { mutableStateOf("") }
    // Issue #110, finding 5: the Partial Stamp joins the cabinet
    // only when a real session carries it — an honest row for a
    // real record, never a seventh curio on offer. The joined list
    // is computed ONCE, here: the cabinet's list renders it and the
    // detail lookup below resolves against it. (Floor lane, #166
    // row 29: the lookup previously read the curios-only list
    // while the stamp was appended to a second, list-local copy —
    // the stamp's row rendered and took the tap, the detail could
    // never resolve, and the detail the finding built was
    // unreachable. One list, both readers.)
    val cabinetArtifacts = if (
        PARTIAL_STAMP_ID in forgedArtifactIds || forgeStats.containsKey(PARTIAL_STAMP_ID)
    ) {
        artifacts + PARTIAL_STAMP_ARTIFACT
    } else {
        artifacts
    }
    val cabinetArtifact: KineticArtifact? =
        cabinetArtifacts.firstOrNull { it.id == cabinetArtifactId }
    // #158 F3, kept on the saveable stack: the surface the MONTH
    // view was entered from, so its BACK returns there — never a
    // silent month reload.
    var monthEnteredFromName by rememberSaveable {
        mutableStateOf(DrawerView.LANDING.name)
    }
    val monthEnteredFrom = runCatching { DrawerView.valueOf(monthEnteredFromName) }
        .getOrDefault(DrawerView.LANDING)

    // System Back pops ONE internal level (detail -> its list,
    // cabinet detail -> cabinet list, any view -> LANDING)
    // instead of ejecting to DECK via MainActivity's handler.
    BackHandler(
        enabled = selectedTaskId != -1 || cabinetArtifactId.isNotEmpty() ||
            view != DrawerView.LANDING
    ) {
        when {
            selectedTaskId != -1 -> selectedTaskId = -1
            cabinetArtifactId.isNotEmpty() -> cabinetArtifactId = ""
            else -> viewName = DrawerView.LANDING.name
        }
    }

    // DRW-05 (Truth Car 2): "today" keys to the day boundary, not
    // to an unkeyed remember frozen at first composition. The
    // ticker re-derives the filing day as each 03:30 boundary
    // passes, so a Drawer held open across it keeps its heading —
    // and its declaration row — on the same filing day the write
    // path stamps at tap time.
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            val next = DrawerDay.nextBoundaryMillis(System.currentTimeMillis())
            delay((next - System.currentTimeMillis()).coerceAtLeast(0L) + 250L)
            nowMillis = System.currentTimeMillis()
        }
    }
    val today = DrawerDay.filingDate(nowMillis)
    val currentYearMonth = DrawerDay.filingYearMonth(nowMillis)
    val searching = drawerQuery.isNotBlank()
    val globallyEmpty = storedMonths.isEmpty() && dayWords.isEmpty()

    // ------------------------------------------------------------------
    // DETAIL — one stored task, read-only. BACK is the only action.
    // ------------------------------------------------------------------
    val detail = selectedTask

    // M1 (closer, issue #8): one system press steps ONE level —
    // DETAIL → list, SEARCH → landing, MONTH → landing. The Drawer
    // had no BackHandler at all: one press in DETAIL or MONTH
    // ejected the man to the DECK, two rooms away from the page he
    // was reading, on the exact night-walk the audit priced (end of
    // day, tired thumb, re-reading a stored task). At LANDING the
    // handler disables and the press belongs to the host again.
    // (The app-wide back-stack generalization stays with #15, per
    // the wave-2 REMAINDERS; this is the Drawer-local cure.)
    BackHandler(
        enabled = detail != null || searching || view == DrawerView.MONTH
    ) {
        when {
            detail != null -> selectedTaskId = -1
            searching -> onQueryChange("")
            else -> viewName = DrawerView.LANDING.name
        }
    }

    if (detail != null) {
        DrawerDetailView(
            scheme = scheme,
            detail = detail,
            onBack = { selectedTaskId = -1 }
        )
        return
    }

    if (view == DrawerView.CABINET) {
        DrawerCabinetView(
            scheme = scheme,
            cabinetArtifacts = cabinetArtifacts,
            cabinetArtifact = cabinetArtifact,
            forgedArtifactIds = forgedArtifactIds,
            forgeStats = forgeStats,
            curioForgeRecords = curioForgeRecords,
            onCabinetArtifactIdChanged = { cabinetArtifactId = it },
            onBackToLanding = { viewName = DrawerView.LANDING.name }
        )
        return
    }

    if (view == DrawerView.RECEIPTS) {
        DrawerReceiptsView(
            scheme = scheme,
            receipts = receipts,
            onReceiptClick = onReceiptClick,
            onBackToLanding = { viewName = DrawerView.LANDING.name }
        )
        return
    }

    // ------------------------------------------------------------------
    // LIST VIEWS — landing / month index / one month / search results.
    // ------------------------------------------------------------------
    // SPITZ Charge 7 (closer): NO column gap in the list — the
    // audit ruled rows breathe by their own padding + hairline
    // only, and the wave-2 report's claim that the 12dp group
    // column was already dead was false at HEAD. Killed here;
    // day-group separation is the DayHeading's 18dp shelf break.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("drawer_screen"),
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Audit R6 #341 F58: the armed day-word overwrite, when one
        // stands — the standing word is still the record; only the
        // CONFIRM chip files the new word (with its own declaration
        // instant). KEEP leaves the standing word untouched.
        if (pendingDayWordConfirm != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.drawer_replace_day_word, pendingDayWordConfirm.existingWord, pendingDayWordConfirm.word),
                        fontFamily = AnonymousProFamily,
                        fontSize = 12.sp,
                        color = scheme.onBackground
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        BlockitActionChip(
                            wordLabel = "CONFIRM — DECLARE ${pendingDayWordConfirm.word}",
                            isPrimaryInk = true,
                            onClick = onConfirmPendingDayWord,
                            testTag = "drawer_day_word_confirm"
                        )
                        BlockitActionChip(
                            wordLabel = "KEEP ${pendingDayWordConfirm.existingWord}",
                            isPrimaryInk = false,
                            onClick = onCancelPendingDayWord,
                            testTag = "drawer_day_word_keep"
                        )
                    }
                }
            }
        }
        item {
            Text(
                text = stringResource(R.string.drawer_stored),
                fontFamily = AnonymousProFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 2.sp,
                color = scheme.onSurfaceVariant
            )
        }

        // Search — one ENTRY LINE, not a capsule (AUDIT-drawer §2,
        // wave-2): 0dp radius, no fill, no border box; one 1dp
        // bottom rule, 44dp tall, text starting at the page's own
        // 12dp (the LazyColumn's horizontal padding). Typed text
        // keeps the placeholder's costume (Anonymous Pro 12sp) —
        // the field no longer changes voice when typed in. Search
        // is LIVE under seal: a query mutates nothing stored, so
        // "findable in seconds" stays true in the sealed state;
        // the old inert box invited typing it refused.
        item {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (drawerQuery.isEmpty()) {
                        Text(
                            text = stringResource(R.string.drawer_type_to_find),
                            fontFamily = AnonymousProFamily,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = scheme.onSurfaceVariant
                        )
                    }
                    BasicTextField(
                        value = drawerQuery,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = AnonymousProFamily,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = scheme.onBackground
                        ),
                        cursorBrush = SolidColor(scheme.onSurfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("drawer_search_input")
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
            }
        }

        // FILING ZONE (AUDIT-drawer §2/§4, wave-2) — directly under
        // the search line, before the stored work, LANDING only:
        // today's and TOMORROW's day-lines. Tomorrow is the audit's
        // worst single finding made curable: at 01:00 a sick man
        // files tomorrow before sleep, in two taps. A word tapped
        // files that day; once declared, the line retires to pure
        // print — the ask never stands anywhere else, and a filing
        // question never outlives its answer. Under seal the zone
        // prints and answers nothing (filing is an edit).
        if (!searching && view == DrawerView.LANDING) {
            item(key = "filing-zone") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilingDayLine(
                        date = today,
                        word = dayWords[DrawerDay.dateIso(today)],
                        isSealed = isSealed,
                        onDeclare = { w -> onDeclareDayWord(today, w) }
                    )
                    FilingDayLine(
                        date = today.plusDays(1),
                        word = dayWords[DrawerDay.dateIso(today.plusDays(1))],
                        isSealed = isSealed,
                        onDeclare = { w -> onDeclareDayWord(today.plusDays(1), w) }
                    )
                }
            }
        }

        if (searching) {
            drawerSearchResults(
                scheme = scheme,
                drawerQuery = drawerQuery,
                drawerResults = drawerResults,
                today = today,
                drawerSearchHasEarlier = drawerSearchHasEarlier,
                onLoadEarlierSearch = onLoadEarlierSearch,
                onQueryChange = onQueryChange,
                onSelectTask = { selectedTaskId = it }
            )
        } else when (view) {
            // Unreachable: the CABINET and RECEIPTS pages return
            // early above, before this column exists. Present for
            // exhaustiveness.
            DrawerView.CABINET, DrawerView.RECEIPTS -> {
                item { Spacer(Modifier.height(0.dp)) }
            }
            DrawerView.INDEX -> {
                drawerMonthIndex(
                    scheme = scheme,
                    storedMonths = storedMonths,
                    onSelectMonth = onSelectMonth,
                    onBackToLanding = { viewName = DrawerView.LANDING.name },
                    onEnteredFromIndex = { monthEnteredFromName = DrawerView.INDEX.name },
                    onNavigateToMonth = { viewName = DrawerView.MONTH.name }
                )
            }

            DrawerView.MONTH -> {
                drawerOneMonth(
                    scheme = scheme,
                    drawerMonth = drawerMonth,
                    drawerRows = drawerRows,
                    dayWords = dayWords,
                    currentYearMonth = currentYearMonth,
                    today = today,
                    isSealed = isSealed,
                    onDeclareDayWord = onDeclareDayWord,
                    drawerHasEarlier = drawerHasEarlier,
                    onLoadEarlier = onLoadEarlier,
                    onBackToLanding = { viewName = DrawerView.LANDING.name },
                    onSelectTask = { selectedTaskId = it }
                )
            }

            DrawerView.LANDING -> {
                drawerLandingBody(
                    scheme = scheme,
                    globallyEmpty = globallyEmpty,
                    drawerRows = drawerRows,
                    dayWords = dayWords,
                    currentYearMonth = currentYearMonth,
                    today = today,
                    isSealed = isSealed,
                    onDeclareDayWord = onDeclareDayWord,
                    drawerHasEarlier = drawerHasEarlier,
                    onLoadEarlier = onLoadEarlier,
                    storedMonths = storedMonths,
                    onSelectMonth = onSelectMonth,
                    onNavigateToMonth = { viewName = DrawerView.MONTH.name },
                    onSelectTask = { selectedTaskId = it }
                )
                drawerLandingDoors(
                    scheme = scheme,
                    globallyEmpty = globallyEmpty,
                    exportProvider = exportProvider,
                    exportScope = exportScope,
                    exportLauncher = exportLauncher,
                    onPendingExportCsv = { pendingExportCsv = it },
                    onNavigate = { viewName = it.name }
                )
            }
        }
    }
}

/**
 * A date heading: a shelf label only — nothing follows it, no total,
 * no grade, and (AUDIT-drawer §2, wave-2) NO ASK. A declared word
 * rides beside its date as pure print, forever read-only. The
 * declaration itself lives only in the filing zone; a question that
 * stays standing after it is answered is a summons, and storage
 * asks nothing. Heading type sits in the meta family (10.5sp).
 *
 * Issue #158 F1 (Option A, ratified — past day words are
 * declarable/correctable, filed under the day described): a PAST
 * day's heading carries one quiet exception to the no-ask rule —
 * a collapsed DECLARE / CHANGE DAY WORD toggle in the meta voice.
 * It is not a standing question: the words themselves appear only
 * when the man opens the toggle himself, on the day he is reading.
 * Opening it offers the three words; tapping a word files it for
 * that past date, tapping the standing word clears it — declare,
 * correct and clear after the 03:30 rollover, through the same
 * date-generic write the filing zone uses. Today and tomorrow are
 * never offered here (the filing zone owns them), and under seal
 * the heading is print only — filing is an edit.
 */
/**
 * Issue #90 (complexity F2 slice): the CABINET view — extracted
 * out of DrawerScreen's body as its own composable, in the pattern
 * of the already-extracted DayHeading/FilingDayLine/StoredRow.
 * Behaviour-identical: same rows, same keys, same testTags; the
 * host's captures are explicit parameters and the two state
 * writes (artifact selection, back-to-landing) ride callbacks to
 * the host's saveable state holders.
 */
@Composable
private fun DrawerCabinetView(
    scheme: androidx.compose.material3.ColorScheme,
    cabinetArtifacts: List<KineticArtifact>,
    cabinetArtifact: KineticArtifact?,
    forgedArtifactIds: Set<String>,
    forgeStats: Map<String, ForgeStat>,
    curioForgeRecords: Map<String, List<FocusSessionEntity>>,
    onCabinetArtifactIdChanged: (String) -> Unit,
    onBackToLanding: () -> Unit
) {
    // ------------------------------------------------------------------
    // CABINET — the curios, re-homed (chrome C10). Ruled rows like
    // everything else in the Drawer; forged state from real
    // sessions; sealed = read-only (the detail reads, SELECT does
    // not answer).
    // ------------------------------------------------------------------
    val artifact = cabinetArtifact
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("drawer_cabinet"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.drawer_cabinet),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 2.sp,
                    color = scheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.label_back),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = scheme.onBackground,
                    modifier = Modifier
                        .clickable(
                            role = androidx.compose.ui.semantics.Role.Button
                        ) {
                            if (cabinetArtifact != null) {
                                onCabinetArtifactIdChanged("")
                            } else {
                                onBackToLanding()
                            }
                        }
                        .sizeIn(minHeight = 48.dp)
                        .padding(vertical = 14.dp)
                        .testTag("cabinet_back")
                )
            }
        }
        if (artifact == null) {
            items(cabinetArtifacts, key = { "cabinet-${it.id}" }) { item ->
                val forged = item.id in forgedArtifactIds
                val stat = forgeStats[item.id]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            role = androidx.compose.ui.semantics.Role.Button
                        ) { onCabinetArtifactIdChanged(item.id) }
                        .sizeIn(minHeight = 48.dp)
                        .padding(vertical = 6.dp)
                        .testTag("cabinet_row_${item.id}"),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.serialCode.uppercase(Locale.US),
                            fontFamily = AnonymousProFamily,
                            fontSize = 10.5.sp,
                            letterSpacing = 1.sp,
                            color = scheme.onSurfaceVariant
                        )
                        if (forged) {
                            Text(
                                // The stamp is STAMPED, never
                                // FORGED; a repeat forge counts.
                                text = when {
                                    item.id == PARTIAL_STAMP_ID -> "STAMPED"
                                    stat != null && stat.count > 1 -> "FORGED ×${stat.count}"
                                    else -> "FORGED"
                                },
                                fontFamily = AnonymousProFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                letterSpacing = 1.sp,
                                color = scheme.onBackground
                            )
                        }
                    }
                    // Audit R3b #145: an un-forged artifact's
                    // name and subtitle are the reward's text —
                    // they are earned, not previewed. The row
                    // shows the serial (its public identity) and
                    // the unforged fact; the name prints only
                    // once a real span has forged it.
                    if (forged) {
                        Text(
                            text = item.name,
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = scheme.onBackground
                        )
                        Text(
                            text = item.subtitle,
                            fontFamily = AnonymousProFamily,
                            fontSize = 10.5.sp,
                            color = scheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.drawer_not_yet_forged),
                            fontFamily = AnonymousProFamily,
                            fontSize = 10.5.sp,
                            letterSpacing = 1.sp,
                            color = scheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
            }
            // DRW-01: the true rule, printed once as the
            // cabinet's standing line — exactly what the
            // forge does (Repository: a manual span finished
            // in full forges one artifact, the forge chooses
            // which; routine-born spans forge nothing). No
            // per-artifact prices, no thresholds nothing
            // enforces.
            item {
                Text(
                    // Audit R3b #145, reopened defect A: the
                    // sentence lives in CABINET_FORGE_RULE so
                    // its exact wording is test-pinned; its
                    // MANUAL / IN FULL scoping is the truth the
                    // earlier unscoped print denied.
                    text = CABINET_FORGE_RULE,
                    fontFamily = AnonymousProFamily,
                    fontSize = 10.5.sp,
                    letterSpacing = 1.sp,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.testTag("cabinet_forge_rule")
                )
            }
        } else {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // The serial plate is print. (Truth Car 2,
                    // DRW-07: the five-tap egg that toggled a
                    // "HARMONIC SYNTH UNLOCKED" line is gone —
                    // the synth it named was removed from the
                    // app, and an achieved state for a feature
                    // that does not exist is not an easter egg,
                    // it is a false print.)
                    Text(
                        text = artifact.serialCode.uppercase(Locale.US),
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.testTag("cabinet_serial_plate")
                    )
                    // Audit R3b #145: name, subtitle and lore
                    // are the earned text — an un-forged
                    // artifact's detail is its serial plate and
                    // the unforged fact, nothing more. What is
                    // not yet earned is not previewed.
                    if (artifact.id in forgedArtifactIds) {
                        Text(
                            text = artifact.name,
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = scheme.onBackground
                        )
                        Text(
                            text = artifact.subtitle,
                            fontFamily = AnonymousProFamily,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = scheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = artifact.lore,
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 13.sp,
                            color = scheme.onBackground
                        )
                    }
                    // DRW-01: the "REQUIRES … MIN · …
                    // INTERCEPTIONS" price tag is gone — no
                    // code ever read those numbers. The true
                    // rule prints once, on the cabinet's list
                    // (below), not as a per-artifact price.
                    Text(
                        text = when {
                            artifact.id == PARTIAL_STAMP_ID &&
                                artifact.id in forgedArtifactIds ->
                                "STAMPED — IT STANDS IN THE CABINET"
                            artifact.id == PARTIAL_STAMP_ID ->
                                "NOT YET STAMPED"
                            artifact.id in forgedArtifactIds ->
                                "FORGED — IT STANDS IN THE CABINET"
                            else -> "NOT YET FORGED"
                        },
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = scheme.onBackground
                    )
                    // Issue #110, finding 5: the forge record —
                    // how many sessions forged (or stamped) it,
                    // and the most recent one's date, from the
                    // stored session rows. A cabinet that names
                    // real days is a record; one that doesn't
                    // was decoration.
                    val detailStat = forgeStats[artifact.id]
                    if (detailStat != null && detailStat.count > 0) {
                        val lastDate = java.time.Instant
                            .ofEpochMilli(detailStat.lastForgedAtMillis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        Text(
                            text = if (artifact.id == PARTIAL_STAMP_ID) {
                                "STAMPED ×${detailStat.count} · LAST ${headingLabel(lastDate, withYear = true)}"
                            } else {
                                "FORGED ×${detailStat.count} · LAST ${headingLabel(lastDate, withYear = true)}"
                            },
                            fontFamily = AnonymousProFamily,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier.testTag("cabinet_forge_record_${artifact.id}")
                        )
                    }
                    // Audit R3b #145: provenance under the
                    // summary — every forge of this artifact,
                    // dated, with the span's intention and its
                    // outcome. A duplicate forge is a second
                    // line, never a silent no-op; a shield-gap
                    // forge says so.
                    val forgeRecords = curioForgeRecords[artifact.id].orEmpty()
                    forgeRecords.forEach { record ->
                        val forgeDay = java.text.SimpleDateFormat(
                            "d MMM yyyy", Locale.US
                        ).format(java.util.Date(record.startWallTimeMillis))
                        // Reopened defect B: the mark derives
                        // from the shield-gap fact via
                        // curioForgeGapMark — a FLOW forge's
                        // outcome is STOPWATCH_ENDED even when
                        // its shieldGapSeen latch is set, so an
                        // outcome-only test marked nothing.
                        val gapMark = curioForgeGapMark(record)
                        Text(
                            text = stringResource(R.string.drawer_forge_day_line, forgeDay, record.intention, gapMark),
                            fontFamily = AnonymousProFamily,
                            fontSize = 10.5.sp,
                            color = scheme.onSurfaceVariant
                        )
                    }
                    // DRW-06: no SELECT verb, no SELECTED
                    // state — the selection was consumed by
                    // nothing, survived nothing, and chose
                    // between toys that do not exist. The
                    // detail now ends at the record.
                }
            }
        }
    }
}

/**
 * Issue #90 (complexity F2 slice): the RECEIPTS view — extracted
 * out of DrawerScreen's body as its own composable, in the pattern
 * of DrawerCabinetView. Behaviour-identical: same rows, same keys,
 * same testTags; the host's captures are explicit parameters and
 * the back-to-landing state write rides a callback to the host's
 * saveable state holder.
 */
@Composable
private fun DrawerReceiptsView(
    scheme: androidx.compose.material3.ColorScheme,
    receipts: List<ReceiptIndexEntry>,
    onReceiptClick: (Int) -> Unit,
    onBackToLanding: () -> Unit
) {
    // ------------------------------------------------------------------
    // RECEIPTS — the index of claimed slips (issue #157). Ruled
    // rows like everything else in the Drawer. A row prints its
    // №, its date, its outcome word — and, where the session's
    // own record proves it, the warning the slip carried, in
    // Drawer type: the warnings live in the furniture now, so
    // they survive the slip. Tapping a row re-presents the slip.
    // ------------------------------------------------------------------
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("drawer_receipts"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.drawer_receipts),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 2.sp,
                    color = scheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.label_back),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = scheme.onBackground,
                    modifier = Modifier
                        .clickable { onBackToLanding() }
                        .testTag("receipts_back")
                )
            }
        }
        if (receipts.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.drawer_no_receipts_yet),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 13.sp,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else {
            items(receipts, key = { "receipt-${it.sessionId}" }) { entry ->
                ReceiptIndexRow(
                    entry = entry,
                    onClick = { onReceiptClick(entry.sessionId) }
                )
            }
        }
    }
}

/**
 * Issue #90 (complexity F2 slice): the DETAIL view — extracted
 * out of DrawerScreen's body as its own composable, in the pattern
 * of DrawerCabinetView. Behaviour-identical: same rows, same keys,
 * same testTags; the host's captures are explicit parameters and
 * the back state write rides a callback to the host's saveable
 * state holder. The section header and the M1 BackHandler stay in
 * the host — they serve the dispatch, not the view.
 */
@Composable
private fun DrawerDetailView(
    scheme: androidx.compose.material3.ColorScheme,
    detail: FocusTaskEntity,
    onBack: () -> Unit
) {
    // DRW-03 (Truth Car 2): the detail's date-line derives
    // from the finish's own instant — its true calendar date
    // beside its true clock time, a single moment that
    // actually happened. The old line composed the FILING
    // date with the true clock: every finish between 00:00
    // and 03:29 printed a date-time pair that never occurred.
    // The filing date still governs the shelves (the list
    // headings); a shelf label and a timestamp are different
    // facts, and only the timestamp was lying.
    val finishDate = java.time.Instant.ofEpochMilli(detail.completedAtMillis)
        .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    val dateLine = if (finishDate.year == LocalDate.now().year) {
        "${headingLabel(finishDate, withYear = false)} · ${clockTime(detail.completedAtMillis)}"
    } else {
        "${headingLabel(finishDate, withYear = true)} · ${clockTime(detail.completedAtMillis)}"
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("drawer_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.drawer_stored),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 2.sp,
                    color = scheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.drawer_back),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = scheme.onBackground,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("drawer_detail_back")
                )
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = detail.title,
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = scheme.onBackground
                )
                Text(
                    text = dateLine,
                    fontFamily = AnonymousProFamily,
                    fontSize = 12.sp,
                    color = scheme.onSurfaceVariant
                )
                // Provenance block (AUDIT-drawer §2, wave-2):
                // category and ESTIMATE stand together as what
                // the task WAS; SPENT stands alone after a
                // spacer as what it COST — the subtraction
                // column the stacked pair formed is broken.
                // Estimate and actual remain two separate plain
                // facts — never a delta, never a percentage. The
                // estimate prints only where one was actually
                // given (MINUTES DIE): never "ESTIMATE 0 MIN".
                // Issue #270: a tag prints only when one exists —
                // a blank tag is nameless by the user's ruling,
                // never an empty line or a dangling separator
                // standing for a category. The estimate keeps its
                // own law above: it prints only where given.
                if (detail.categoryTag.isNotBlank() || detail.estimatedMinutes > 0) {
                    Text(
                        text = when {
                            detail.categoryTag.isNotBlank() && detail.estimatedMinutes > 0 ->
                                "${detail.categoryTag} · ESTIMATE ${detail.estimatedMinutes} MIN"
                            detail.categoryTag.isNotBlank() -> detail.categoryTag
                            else -> "ESTIMATE ${detail.estimatedMinutes} MIN"
                        },
                        fontFamily = AnonymousProFamily,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    // Issue #110, finding 2 (main's form,
                    // restored at the composition, K8): SPENT
                    // IN TOTAL — the detail's spent is the
                    // task's lifetime accrual, same law as the
                    // row's TOTAL print.
                    text = stringResource(R.string.drawer_spent_in_total, spentLabel(detail.actualSecondsSpent)),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 13.sp,
                    color = scheme.onBackground
                )
            }
        }
        if (detail.subSteps.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    detail.subSteps.forEachIndexed { stepIdx, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // AUDIT-drawer §2 (wave-2): ONE voice
                            // for every substep — strike-through
                            // is a working surface's verb imported
                            // into the archive, and it made
                            // finished work read unfinished. The
                            // two-digit number (DRW-08) leads.
                            Text(
                                text = com.example.data.twoDigitCount(stepIdx + 1),
                                fontFamily = AnonymousProFamily,
                                fontSize = 10.5.sp,
                                color = scheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Text(
                                text = step.title,
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.5.sp,
                                color = scheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Issue #90 (complexity F2 slice): the SEARCH RESULTS block of
 * the Drawer's list column — extracted as a LazyListScope
 * extension, in the pattern of F4's standingOrdersSlip.
 * Behaviour-identical: same items, same keys, same testTags; the
 * host's captures are explicit parameters and the row-tap state
 * write rides a callback to the host's saveable state holder.
 */
private fun LazyListScope.drawerSearchResults(
    scheme: androidx.compose.material3.ColorScheme,
    drawerQuery: String,
    drawerResults: List<FocusTaskEntity>,
    today: LocalDate,
    drawerSearchHasEarlier: Boolean,
    onLoadEarlierSearch: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSelectTask: (Int) -> Unit
) {
    // ---------- SEARCH RESULTS (all years, paged) ----------
    item(key = "search-back") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // SEARCH gains the house BACK (AUDIT-drawer §2):
            // a mode you leave only by erasing your own
            // typing is a trap — BACK clears the query.
            Text(
                text = stringResource(R.string.drawer_back),
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = scheme.onBackground,
                modifier = Modifier
                    .clickable { onQueryChange("") }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .testTag("drawer_search_back")
            )
        }
    }
    if (drawerResults.isEmpty()) {
        item {
            // Re-scoped (AUDIT-drawer §2): the promise and
            // the territory match — this search looks at
            // stored TASKS only, and now says so.
            Text(
                text = stringResource(R.string.drawer_no_stored_task_under, drawerQuery),
                fontFamily = AtkinsonHyperlegibleFamily,
                fontSize = 13.sp,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }
    } else {
        val groups = buildDayGroups(
            rows = drawerResults,
            dayWords = emptyMap(),
            scopeMonth = null,
            includeTodayGroup = false,
            today = today
        )
        groups.forEach { group ->
            item(key = "search-head-${group.date}") {
                // SPITZ Charge 10a (closer): the search day
                // headings share the DayHeading constant —
                // 10.5sp meta voice, not a surviving 11sp.
                Text(
                    text = headingLabel(group.date, withYear = true),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp,
                    letterSpacing = 1.sp,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            items(group.rows, key = { "search-${it.id}" }) { task ->
                // A hit must show its reason (AUDIT-drawer
                // §2): when the match lives in a substep —
                // not the title or category — the row prints
                // that step as a second line. A result that
                // cannot show why it matched is a small
                // false print.
                val q = drawerQuery.trim()
                val matchedStep = if (
                    q.isNotEmpty() &&
                    !task.title.contains(q, ignoreCase = true) &&
                    !task.categoryTag.contains(q, ignoreCase = true)
                ) {
                    task.subSteps.firstOrNull {
                        it.title.contains(q, ignoreCase = true)
                    }?.title
                } else {
                    null
                }
                StoredRow(
                    task = task,
                    matchedStepTitle = matchedStep,
                    onClick = { onSelectTask(task.id) }
                )
            }
        }
        if (drawerSearchHasEarlier) {
            item {
                EarlierRow(onClick = onLoadEarlierSearch)
            }
        }
    }
}

/**
 * Issue #90 (complexity F2 slice): the INDEX arm (all months) of
 * the Drawer's list column — extracted as a LazyListScope
 * extension, in the pattern of drawerSearchResults.
 * Behaviour-identical: same items, same keys, same testTags; the
 * host's captures are explicit parameters and the arm's state
 * writes (back-to-landing, entered-from record, month
 * navigation) ride callbacks to the host's saveable holders, in
 * the arm's standing order.
 */
private fun LazyListScope.drawerMonthIndex(
    scheme: androidx.compose.material3.ColorScheme,
    storedMonths: List<StoredMonth>,
    onSelectMonth: (Int, Int) -> Unit,
    onBackToLanding: () -> Unit,
    onEnteredFromIndex: () -> Unit,
    onNavigateToMonth: () -> Unit
) {
        // ---------- ALL MONTHS ----------
        item {
            DrawerNavRow(
                label = "BACK",
                onClick = { onBackToLanding() }
            )
        }
        items(storedMonths, key = { "month-${it.year}-${it.month}" }) { month ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        role = androidx.compose.ui.semantics.Role.Button
                    ) {
                        onSelectMonth(month.year, month.month)
                        onEnteredFromIndex()
                        onNavigateToMonth()
                    }
                    .sizeIn(minHeight = 48.dp)
                    .padding(vertical = 10.dp)
                    .testTag("drawer_month_${month.year}_${month.month}"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthLabel(month.year, month.month),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = scheme.onBackground
                )
            }
            HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
        }
}

/**
 * Issue #90 (complexity F2 slice): the MONTH arm (one month) of
 * the Drawer's list column — extracted as a LazyListScope
 * extension, in the pattern of drawerSearchResults.
 * Behaviour-identical: same items, same keys, same testTags; the
 * host's captures are explicit parameters and the arm's state
 * writes (back-to-landing, row selection) ride callbacks to the
 * host's saveable state holders.
 */
private fun LazyListScope.drawerOneMonth(
    scheme: androidx.compose.material3.ColorScheme,
    drawerMonth: Pair<Int, Int>,
    drawerRows: List<FocusTaskEntity>,
    dayWords: Map<String, String>,
    currentYearMonth: Pair<Int, Int>,
    today: LocalDate,
    isSealed: Boolean,
    onDeclareDayWord: (LocalDate, String?) -> Unit,
    drawerHasEarlier: Boolean,
    onLoadEarlier: () -> Unit,
    onBackToLanding: () -> Unit,
    onSelectTask: (Int) -> Unit
) {
        // ---------- ONE MONTH ----------
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthLabel(drawerMonth.first, drawerMonth.second),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = scheme.onSurfaceVariant
                )
                // BACK — one costume, true parent (AUDIT-
                // drawer §2): the parent is LANDING, and
                // going back no longer silently resets the
                // VM's month — the landing re-derives its
                // own current month; the viewed month is
                // simply left, not rewritten.
                Text(
                    text = stringResource(R.string.drawer_back),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = scheme.onBackground,
                    modifier = Modifier
                        .clickable { onBackToLanding() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("drawer_month_back")
                )
            }
        }
        val groups = buildDayGroups(
            rows = drawerRows,
            dayWords = dayWords,
            scopeMonth = drawerMonth,
            includeTodayGroup = !isSealed &&
                drawerMonth == currentYearMonth,
            today = today
        )
        groups.forEach { group ->
            item(key = "month-head-${group.date}") {
                DayHeading(
                    group = group,
                    today = today,
                    isSealed = isSealed,
                    onDeclareDayWord = onDeclareDayWord
                )
            }
            items(group.rows, key = { "month-${it.id}" }) { task ->
                StoredRow(task = task, onClick = { onSelectTask(task.id) })
            }
        }
        if (drawerHasEarlier) {
            item { EarlierRow(onClick = onLoadEarlier) }
        }
}

/**
 * Issue #90 (complexity F2 slice): the LANDING arm's stored-work
 * body (empty print / current-month day groups / MONTHS shelf)
 * of the Drawer's list column — extracted as a LazyListScope
 * extension, in the pattern of drawerSearchResults. The arm's
 * doors block stays in the host for its own slice.
 * Behaviour-identical: same items, same keys, same testTags; the
 * host's captures are explicit parameters and the body's state
 * writes (shelf navigation, row selection) ride callbacks to the
 * host's saveable state holders.
 */
private fun LazyListScope.drawerLandingBody(
    scheme: androidx.compose.material3.ColorScheme,
    globallyEmpty: Boolean,
    drawerRows: List<FocusTaskEntity>,
    dayWords: Map<String, String>,
    currentYearMonth: Pair<Int, Int>,
    today: LocalDate,
    isSealed: Boolean,
    onDeclareDayWord: (LocalDate, String?) -> Unit,
    drawerHasEarlier: Boolean,
    onLoadEarlier: () -> Unit,
    storedMonths: List<StoredMonth>,
    onSelectMonth: (Int, Int) -> Unit,
    onNavigateToMonth: () -> Unit,
    onSelectTask: (Int) -> Unit
) {
    // ---------- LANDING: current month + MONTHS shelf ----------
    if (globallyEmpty) {
        item {
            Text(
                text = stringResource(R.string.drawer_nothing_stored_yet),
                fontFamily = AtkinsonHyperlegibleFamily,
                fontSize = 13.sp,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }
        // The day-word capture exists even here — in
        // the FILING ZONE above, not on a heading: a
        // sick day in the first week, before anything
        // was ever finished, is exactly when a word
        // needs declaring (Selbst × Neu; AUDIT-drawer
        // §2 wave-2: the ask lives in the zone only).
    } else {
        // The current month, newest first. An empty current
        // month renders in SILENCE above the MONTHS shelf —
        // no absence line greets a month that has barely begun.
        // (The filing zone stands above, outside this branch,
        // so a first-week sick day is fileable even here.)
        val groups = buildDayGroups(
            rows = drawerRows,
            dayWords = dayWords,
            scopeMonth = currentYearMonth,
            includeTodayGroup = !isSealed,
            today = today
        )
        groups.forEach { group ->
            item(key = "land-head-${group.date}") {
                DayHeading(
                    group = group,
                    today = today,
                    isSealed = isSealed,
                    onDeclareDayWord = onDeclareDayWord
                )
            }
            items(group.rows, key = { "land-${it.id}" }) { task ->
                StoredRow(task = task, onClick = { onSelectTask(task.id) })
            }
        }
        if (drawerHasEarlier) {
            item { EarlierRow(onClick = onLoadEarlier) }
        }
        // MONTHS shelf (AUDIT-drawer §2): the INDEX
        // corridor is dead. Earlier stored months stand
        // inline on the landing itself — one ruled row
        // per month (name incl. year, NO counts), each
        // opening MONTH exactly as the index row did.
        // The current month is the landing's own body
        // above, never a shelf row. A word-only month is
        // still an address and still shelves.
        // Shelf membership is STRICTLY BEFORE the
        // current month (Regal's G7 ruling, closer-built):
        // the old filterNot(current) also shelved a
        // FUTURE month — file tomorrow's word on the
        // 31st and next month stood at the top of the
        // "earlier months" shelf tonight, a future month
        // presented as the nearest past. The repository
        // union keeps returning the future word-month
        // (the filing zone needs it); the shelf is the
        // wrong consumer, so the shelf is what changes.
        val earlierMonths = storedMonths.filter {
            it.year < currentYearMonth.first ||
                (it.year == currentYearMonth.first &&
                    it.month < currentYearMonth.second)
        }
        if (earlierMonths.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.drawer_months),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            items(
                earlierMonths,
                key = { "shelf-${it.year}-${it.month}" }
            ) { month ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectMonth(month.year, month.month)
                            onNavigateToMonth()
                        }
                        .padding(vertical = 10.dp)
                        .testTag("drawer_month_${month.year}_${month.month}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthLabel(month.year, month.month),
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = scheme.onBackground
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
            }
        }
    }
}

/**
 * Issue #90 (complexity F2 slice): the LANDING arm's doors block
 * (ALL MONTHS / CABINET / RECEIPTS / EXPORT doors) of the
 * Drawer's list column — extracted as a LazyListScope extension,
 * in the pattern of drawerSearchResults. With this slice the F2
 * list-view extractions are complete.
 * Behaviour-identical: same items, same keys, same testTags; the
 * host's captures are explicit parameters (the export machinery
 * included) and the block's state writes (three navigations, the
 * pending-export hand-off) ride callbacks to the host's holders,
 * in the block's standing order.
 */
private fun LazyListScope.drawerLandingDoors(
    scheme: androidx.compose.material3.ColorScheme,
    globallyEmpty: Boolean,
    exportProvider: (suspend () -> String)?,
    exportScope: kotlinx.coroutines.CoroutineScope,
    exportLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    onPendingExportCsv: (String) -> Unit,
    onNavigate: (DrawerView) -> Unit
) {
    // The drawers doors (main, C10 — restored at
    // the composition, K8): the composed landing had
    // kept the train shelf but lost every entry row,
    // stranding the CABINET / RECEIPTS / INDEX views
    // the sections above implement. ALL MONTHS keeps
    // main's case split (populated landings only).
    if (!globallyEmpty) {
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    role = androidx.compose.ui.semantics.Role.Button
                ) { onNavigate(DrawerView.INDEX) }
                .sizeIn(minHeight = 48.dp)
                .padding(vertical = 6.dp)
                .testTag("drawer_all_months"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.drawer_all_months),
                fontFamily = AnonymousProFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = scheme.onBackground
            )
            androidx.compose.material3.Icon(
                imageVector = com.example.ui.theme.MonolithIcons.ChevronRight,
                contentDescription = null,
                tint = scheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
        HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
    }
    }
    // The cabinet's door: ONE furniture row under
    // ALL MONTHS (chrome C10).
    item {
        DrawerNavRow(
            label = "CABINET",
            onClick = { onNavigate(DrawerView.CABINET) },
            modifier = Modifier.testTag("drawer_curios_row")
        )
        HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
    }
    // The receipts index's door: ONE furniture row
    // under CABINET (issue #157) — this destination
    // is named "Receipts"; the receipts are here.
    item {
        DrawerNavRow(
            label = "RECEIPTS",
            onClick = { onNavigate(DrawerView.RECEIPTS) },
            modifier = Modifier.testTag("drawer_receipts_row")
        )
        HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
    }
    // Issue #253 line 39: the history leaves the app —
    // one furniture door under RECEIPTS, standing only
    // when the host supplies the export provider.
    exportProvider?.let { provider ->
        item {
            DrawerNavRow(
                label = "EXPORT HISTORY (CSV)",
                onClick = {
                    exportScope.launch {
                        val csv = runCatching { provider() }
                            .getOrNull()
                        if (csv != null) {
                            onPendingExportCsv(csv)
                            exportLauncher.launch(HistoryCsv.FILE_NAME)
                        }
                    }
                },
                modifier = Modifier.testTag("drawer_export_history")
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = scheme.outlineVariant
            )
        }
    }
}

@Composable
private fun DayHeading(
    group: DrawerDayGroup,
    today: LocalDate? = null,
    isSealed: Boolean = true,
    onDeclareDayWord: (LocalDate, String?) -> Unit = { _, _ -> }
) {
    val scheme = MaterialTheme.colorScheme
    var pastPickerOpen by remember { mutableStateOf(false) }
    Column {
        Text(
            text = if (group.word != null) {
                "${headingLabel(group.date, withYear = false)} · ${group.word}"
            } else {
                headingLabel(group.date, withYear = false)
            },
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp,
            letterSpacing = 1.sp,
            color = scheme.onSurfaceVariant,
            // SPITZ Charge 10c (closer): the audit's shelf-break cure —
            // day groups separate by an 18dp break above the heading,
            // not the 6dp that stood at HEAD.
            modifier = Modifier.padding(top = 18.dp)
        )
        if (today != null && group.date < today && !isSealed) {
            Text(
                text = if (group.word == null) "DECLARE DAY WORD" else "CHANGE DAY WORD",
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = scheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable { pastPickerOpen = !pastPickerOpen }
                    .padding(horizontal = 4.dp, vertical = 6.dp)
                    .testTag(
                        "declare_past_word_toggle_${DrawerDay.dateIso(group.date)}"
                    )
            )
            if (pastPickerOpen) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    DayWordStore.WORDS.forEach { w ->
                        val active = group.word == w
                        Text(
                            text = w,
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (active) {
                                scheme.onBackground
                            } else {
                                scheme.onSurfaceVariant
                            },
                            modifier = Modifier
                                .clickable {
                                    onDeclareDayWord(
                                        group.date,
                                        if (active) null else w
                                    )
                                }
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                                .testTag(
                                    "declare_past_word_" +
                                        "${DrawerDay.dateIso(group.date)}_" +
                                        w.lowercase()
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * One filing-zone day-line (AUDIT-drawer §2/§4, wave-2): a date and
 * its three words. Undeclared and unsealed: the words are tappable;
 * a tap files that word and the line retires. Declared: the line
 * retires to pure print — date · WORD, read-only, exactly the
 * DayHeading voice. Sealed: print only; filing is an edit.
 */
@Composable
private fun FilingDayLine(
    date: LocalDate,
    word: String?,
    isSealed: Boolean,
    onDeclare: (String?) -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    if (word != null) {
        Text(
            text = stringResource(R.string.drawer_heading_word, headingLabel(date, withYear = false), word),
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp,
            letterSpacing = 1.sp,
            color = scheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 6.dp)
                .testTag("filing_line_${DrawerDay.dateIso(date)}")
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // SPITZ Charges 9 + 10b (closer): the UNDECLARED line prints
        // at full ink (onBackground — bright = asking); once a word
        // is declared the line drops to variant print above. And the
        // mis-tap mitigations the feel seat priced are built as
        // ruled: word gap 16 → 24dp, 4dp horizontal padding per
        // word — an ILL filed as REST is a false record, not a
        // typo, in the one room whose law is that its record is
        // true.
        Text(
            text = headingLabel(date, withYear = false),
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp,
            letterSpacing = 1.sp,
            color = scheme.onBackground,
            modifier = Modifier.padding(top = 6.dp)
        )
        if (!isSealed) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                DayWordStore.WORDS.forEach { w ->
                    Text(
                        text = w,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = scheme.onBackground,
                        modifier = Modifier
                            .clickable { onDeclare(w) }
                            .padding(horizontal = 4.dp, vertical = 6.dp)
                            .testTag("filing_word_${DrawerDay.dateIso(date)}_${w.lowercase()}")
                    )
                }
            }
        }
    }
}

/** One stored row: title, category, finish time, time spent. No
 *  estimate, no checkbox, no bar, no rank. */
@Composable
private fun StoredRow(
    task: FocusTaskEntity,
    matchedStepTitle: String? = null,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp)
            .testTag("drawer_row_${task.id}"),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = task.title,
            fontFamily = AtkinsonHyperlegibleFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = scheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        // Search only: the step that matched, printed as the row's
        // reason (AUDIT-drawer §2). Null everywhere else.
        if (matchedStepTitle != null) {
            Text(
                text = matchedStepTitle,
                fontFamily = AnonymousProFamily,
                fontSize = 10.5.sp,
                color = scheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Issue #270: a tag prints only when one exists.
            if (task.categoryTag.isNotBlank()) {
                Text(
                    text = task.categoryTag,
                    fontFamily = AnonymousProFamily,
                    fontSize = 10.5.sp,
                    color = scheme.onSurfaceVariant
                )
            }
            Text(
                text = clockTime(task.completedAtMillis),
                fontFamily = AnonymousProFamily,
                fontSize = 10.5.sp,
                color = scheme.onSurfaceVariant
            )
            Text(
                // Issue #110, finding 2 (main's form, restored at
                // the composition, K8): TOTAL — the lifetime
                // accrual riding under the finish day's shelf,
                // not time that day contained. Both parents'
                // VaultHistoryTruthTest pins this print; the
                // train's bare-label row contradicted its own.
                text = stringResource(R.string.drawer_total_spent, spentLabel(task.actualSecondsSpent)),
                fontFamily = AnonymousProFamily,
                fontSize = 10.5.sp,
                color = scheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun DrawerNavRow(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Text(
        text = label,
        fontFamily = AtkinsonHyperlegibleFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = scheme.onBackground,
        modifier = modifier
            .clickable(role = androidx.compose.ui.semantics.Role.Button) { onClick() }
            .sizeIn(minHeight = 48.dp)
            .padding(vertical = 14.dp)
    )
}

/** One receipts-index row (issue #157): № · date · outcome word,
 *  in the Drawer's furniture. The warnings print here — in Drawer
 *  type, on the furniture, never on the slip — only where the
 *  session's own record proves them: the release-fault stamp
 *  proves "SHIELD FAULT — SEE RECEIPT"; the re-derived slip's
 *  shieldWasOff fact (the row's shield-gap outcome) proves "THE
 *  SHIELD WAS OFF FOR PART OF THAT SESSION". */
@Composable
private fun ReceiptIndexRow(entry: ReceiptIndexEntry, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val date = java.time.Instant.ofEpochMilli(entry.endedAtWallMs)
        .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp)
            .testTag("receipt_row_${entry.sessionId}"),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // GESTALT receipts audit F7 (filed on PR #250): numerals are
        // Anonymous Pro by law — the № segment carries its own span;
        // date + outcome stay Atkinson.
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontFamily = AnonymousProFamily)) {
                    append(entry.numberPrint)
                }
                append(" · ${headingLabel(date, withYear = true)} · ${entry.outcomeWord}")
            },
            fontFamily = AtkinsonHyperlegibleFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = scheme.onBackground
        )
        if (entry.releaseFaultShield) {
            Text(
                text = stringResource(R.string.drawer_shield_fault),
                fontFamily = AnonymousProFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                letterSpacing = 1.sp,
                color = scheme.onBackground,
                modifier = Modifier.testTag("receipt_fault_${entry.sessionId}")
            )
        }
        if (entry.shieldWasOff) {
            Text(
                text = stringResource(R.string.drawer_shield_was_off),
                fontFamily = AnonymousProFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                letterSpacing = 1.sp,
                color = scheme.onBackground,
                modifier = Modifier.testTag("receipt_shield_off_${entry.sessionId}")
            )
        }
    }
    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun EarlierRow(onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                role = androidx.compose.ui.semantics.Role.Button
            ) { onClick() }
            .sizeIn(minHeight = 48.dp)
            .padding(vertical = 10.dp)
            .testTag("drawer_earlier"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.drawer_earlier),
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            color = scheme.onBackground
        )
    }
}
