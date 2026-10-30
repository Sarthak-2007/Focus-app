package com.example.ui.screens
import androidx.compose.ui.res.stringResource
import com.example.R

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import com.example.data.CurioCatalog
import com.example.data.ClockText
import androidx.compose.ui.platform.LocalContext
import com.example.data.DayWordStore
import com.example.data.DrawerDay
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.data.ForgeStat
import com.example.data.KineticArtifact
import com.example.data.PARTIAL_STAMP_ARTIFACT
import com.example.data.PARTIAL_STAMP_ID
import com.example.data.ReceiptIndexEntry
import com.example.data.SessionOutcome
import com.example.data.StoredMonth
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import kotlinx.coroutines.delay
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
 * fully, search input and every edit are inert.
 *
 * Days file by the 03:30 rule (DrawerDay); row finish times print
 * true clock. A declared day word (REST / ILL / FAMILY) prints
 * beside its date heading as his authored word — never a number.
 *
 * SPENT is a task's LIFETIME total (Issue #110, finding 2): the
 * stored row carries one accrual scalar and no per-session or
 * per-day segments — accrual segments were never written (the
 * startedAtSessionElapsedSec column landed unwritten), so no
 * truthful per-day split can be computed from stored data. The
 * shelves file a task under its FINISH day and every SPENT print
 * says IN TOTAL / TOTAL, so the shelf never claims the finish
 * day contained time that was actually spread over earlier days.
 * A real per-day split needs segment storage — a new table and
 * migration, owned by the schema train, not this screen.
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

/** Audit R3b #145, reopened defect B: the provenance shield-gap
 *  mark for one forge record, derived from the shield-gap FACT —
 *  never from outcome precedence alone. A timed forge's gap is
 *  stamped as its outcome (ELAPSED_SHIELD_GAP), but a FLOW forge's
 *  outcome is always STOPWATCH_ENDED (the disarm when-chain's
 *  completed-stopwatch branch wins regardless of shield state, and
 *  that stamp must stay: finishKindOf requires it for a FLOW
 *  finish). The gap fact a FLOW row still carries is its
 *  shieldGapSeen latch — set when any observer saw the shield's
 *  beat stale past the law or its sight revoked, and never
 *  cleared. Either witness earns the mark; neither, no mark. */
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
    // R3b #163 F2/F3 STRUCK (Purpose lens, Sarthak's settled
    // definition): the Drawer stores finished tasks in a quiet
    // drawer — NO scores, NO day-grading, NO judgement. It does
    // not print routine outcomes ("MISSED") and it does not
    // manufacture a group for a day that stored nothing: a day
    // with no finished tasks kept nothing, so nothing vanished.
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
    // Issue #52 F4 cure: scopeMonth scopes ROW dates as well as
    // day-word dates — a scoped call no longer renders rows from
    // outside the month it names.
    val dates = byDate.keys.filter { date ->
        scopeMonth == null ||
            (date.year == scopeMonth.first && date.monthValue == scopeMonth.second)
    }.toMutableSet()
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

/** #158 F2: does [query] name the declared day [word]? Same rule
 *  as the repository's: the trimmed, upper-cased query equals the
 *  word or is a prefix of it ("IL" names ILL). */
private fun dayWordMatchesQuery(word: String, query: String): Boolean {
    val q = query.trim().uppercase(Locale.US)
    return q.isNotEmpty() && (word == q || word.startsWith(q))
}

private fun headingLabel(date: LocalDate, withYear: Boolean): String {
    val pattern = if (withYear) "EEE d MMM yyyy" else "EEE d MMM"
    return DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).format(date).uppercase(Locale.getDefault())
}

private fun monthLabel(year: Int, month: Int): String =
    DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        .format(LocalDate.of(year, month, 1)).uppercase(Locale.getDefault())

private fun clockTime(context: android.content.Context, millis: Long): String =
    ClockText.timeOfDay(context, millis)

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
    // Issue #52 F3 cure: the month read's state, from the
    // ViewModel — loading while a page is in flight, an error
    // line when the read failed. An empty month, a loading
    // month and a broken month are three different prints.
    drawerLoading: Boolean = false,
    drawerError: String? = null
) {
    val scheme = MaterialTheme.colorScheme
    // Issue #52 F1 cure: the navigation stack is saveable.
    // Entities are stored by key and resolved against the live
    // lists, so rotation/process recreation restores the open
    // view instead of resetting to LANDING.
    var viewName by rememberSaveable { mutableStateOf(DrawerView.LANDING.name) }
    val view = runCatching { DrawerView.valueOf(viewName) }
        .getOrDefault(DrawerView.LANDING)
    var selectedTaskId by rememberSaveable { mutableStateOf(-1) }
    val selectedTask: FocusTaskEntity? =
        (drawerRows + drawerResults).firstOrNull { it.id == selectedTaskId }
    var cabinetArtifactId by rememberSaveable { mutableStateOf("") }
    val cabinetArtifact: KineticArtifact? =
        artifacts.firstOrNull { it.id == cabinetArtifactId }
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
            // Issue #152: the sleep is capped at a minute.
            // nextBoundaryMillis is computed under the zone in
            // force at composition; a timezone change mid-sleep
            // (a landing) does not interrupt a boundary-length
            // delay, so the heading — and the declaration row's
            // isToday — stayed on the departure filing day until
            // the old zone's 03:30-equivalent passed, while the
            // write path already stamped in the new zone. Waking
            // at least once a minute re-derives the filing day
            // under the zone actually in force.
            delay(
                ((next - System.currentTimeMillis()).coerceAtLeast(0L) + 250L)
                    .coerceAtMost(60_000L)
            )
            nowMillis = System.currentTimeMillis()
        }
    }
    val today = DrawerDay.filingDate(nowMillis)
    // Issue #8: tomorrow under the same 03:30 filing rule — the
    // day that begins at the next boundary, fileable tonight.
    val tomorrow = today.plusDays(1)
    val currentYearMonth = DrawerDay.filingYearMonth(nowMillis)
    val searching = drawerQuery.isNotBlank()
    val globallyEmpty = storedMonths.isEmpty() && dayWords.isEmpty()
    // Issue #8, Regal G7 (via PR #403's shelf cure, re-ported by
    // DURCH-A8 to the kept INDEX): month-index membership is
    // strictly-before-current under the 03:30 filing rule. The
    // repository union deliberately keeps a future word-month (the
    // filing zone needs it the night tomorrow's word is filed
    // across a month seam) — but a future month must never stand
    // in the index of earlier months, presented as the nearest
    // past. The repository is NOT touched; the index was the
    // wrong consumer, so the index is what filters.
    val indexedMonths = storedMonths.filter { month ->
        month.year * 12 + month.month <
            currentYearMonth.first * 12 + currentYearMonth.second
    }

    // #158 F2: the words the search rendering stands on, derived
    // from the REAL dayWords map — never emptyMap(), never the
    // whole map either: the whole map would file every declared
    // day under every query, a print that isn't true. A search
    // day keeps its word when (a) a task hit files under it, or
    // (b) its word is the word the query names (the ViewModel's
    // word-aware state, drawerSearchDayWords, carries exactly
    // those dates, word-only ones included). Union of the three
    // sources — all read the same declared words.
    val effectiveSearchDayWords = remember(drawerQuery, drawerResults, dayWords, drawerSearchDayWords) {
        val resultIsos = drawerResults.mapTo(HashSet()) {
            DrawerDay.dateIso(DrawerDay.filingDate(it.completedAtMillis))
        }
        dayWords.filterKeys { it in resultIsos } +
            dayWords.filterValues { dayWordMatchesQuery(it, drawerQuery) } +
            drawerSearchDayWords
    }

    // #158 F5(ii): every regrouping memoised on exactly the
    // inputs its call site builds from — recomposition no longer
    // re-groups the whole accumulated list from scratch.
    val searchGroups = remember(drawerResults, effectiveSearchDayWords, today) {
        buildDayGroups(
            rows = drawerResults,
            dayWords = effectiveSearchDayWords,
            scopeMonth = null,
            includeTodayGroup = false,
            today = today
        )
    }
    val monthGroups = remember(drawerRows, dayWords, drawerMonth, isSealed, today, currentYearMonth) {
        buildDayGroups(
            rows = drawerRows,
            dayWords = dayWords,
            scopeMonth = drawerMonth,
            includeTodayGroup = !isSealed && drawerMonth == currentYearMonth,
            today = today
        )
    }
    val landingGroups = remember(drawerRows, dayWords, currentYearMonth, isSealed, today) {
        buildDayGroups(
            rows = drawerRows,
            dayWords = dayWords,
            scopeMonth = currentYearMonth,
            includeTodayGroup = !isSealed,
            today = today
        )
    }

    // #158 F5(i): scroll state for the list views + the auto-load
    // trigger — when the last visible item approaches the end and
    // an earlier page exists, fetch it; the EARLIER row stays as
    // the manual fallback. The row/size keys re-arm the effect
    // after each landed page, so a still-near-end list keeps
    // paging until hasEarlier runs out. loadEarlierDrawer's own
    // month guard keeps a stale trigger from appending a
    // previous month's page.
    val listState = rememberLazyListState()
    val nearListEnd by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            info.totalItemsCount > 0 && lastVisible >= info.totalItemsCount - 3
        }
    }
    LaunchedEffect(
        nearListEnd, searching, drawerHasEarlier, drawerSearchHasEarlier,
        drawerRows, drawerResults
    ) {
        if (!nearListEnd) return@LaunchedEffect
        if (searching) {
            if (drawerSearchHasEarlier) onLoadEarlierSearch()
        } else if (drawerHasEarlier) {
            onLoadEarlier()
        }
    }

    // ------------------------------------------------------------------
    // DETAIL — one stored task, read-only. BACK is the only action.
    // ------------------------------------------------------------------
    val detail = selectedTask
    if (detail != null) {
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
            "${headingLabel(finishDate, withYear = false)} · ${clockTime(LocalContext.current, detail.completedAtMillis)}"
        } else {
            "${headingLabel(finishDate, withYear = true)} · ${clockTime(LocalContext.current, detail.completedAtMillis)}"
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
                        text = stringResource(R.string.label_back),
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = scheme.onBackground,
                        modifier = Modifier
                            .clickable(
                                role = androidx.compose.ui.semantics.Role.Button
                            ) { selectedTaskId = -1 }
                            .sizeIn(minHeight = 48.dp)
                            .padding(vertical = 14.dp)
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
                        // Issue #8 wave-2 (via PR #428, re-ported by
                        // DURCH-A8): the estimate rides the provenance
                        // voice — category line, meta ink — instead of
                        // standing as a second body-size fact stacked
                        // against SPENT. It prints only where one was
                        // actually given (MINUTES DIE): a name-only
                        // task has no estimate, never an invented
                        // "ESTIMATE 0 MIN".
                        text = if (detail.estimatedMinutes > 0) {
                            "${detail.categoryTag} · ESTIMATE ${detail.estimatedMinutes} MIN"
                        } else {
                            detail.categoryTag
                        },
                        fontFamily = AnonymousProFamily,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = scheme.onSurfaceVariant
                    )
                    Text(
                        text = dateLine,
                        fontFamily = AnonymousProFamily,
                        fontSize = 12.sp,
                        color = scheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // SPENT stands alone as the detail's one body
                    // fact — never a delta, never a percentage.
                    Text(
                        // Issue #110, finding 2: SPENT IN TOTAL —
                        // the lifetime accrual, filed under the
                        // finish day; never that day's own time.
                        text = "SPENT IN TOTAL ${spentLabel(detail.actualSecondsSpent)}",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 13.sp,
                        color = scheme.onBackground,
                        modifier = Modifier.testTag("drawer_detail_spent")
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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = step.title,
                                    fontFamily = AtkinsonHyperlegibleFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.5.sp,
                                    color = if (step.isDone) scheme.onSurfaceVariant else scheme.onBackground,
                                    // Issue #8: strike-through is a
                                    // working surface's verb — in the
                                    // archive it makes finished work
                                    // look graded. Killed; the ink
                                    // distinction alone carries done.
                                    textDecoration = TextDecoration.None,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    // DRW-08: the shared two-digit
                                    // voice — never "010".
                                    text = com.example.data.twoDigitCount(stepIdx + 1),
                                    fontFamily = AnonymousProFamily,
                                    fontSize = 10.5.sp,
                                    color = scheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        return
    }

    // ------------------------------------------------------------------
    // CABINET — the curios, re-homed (chrome C10). Ruled rows like
    // everything else in the Drawer; forged state from real
    // sessions; sealed = read-only (the detail reads, SELECT does
    // not answer).
    // ------------------------------------------------------------------
    if (view == DrawerView.CABINET) {
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
                                    cabinetArtifactId = ""
                                } else {
                                    viewName = DrawerView.LANDING.name
                                }
                            }
                            .sizeIn(minHeight = 48.dp)
                            .padding(vertical = 14.dp)
                            .testTag("cabinet_back")
                    )
                }
            }
            if (artifact == null) {
                // Issue #110, finding 5: the Partial Stamp joins
                // the cabinet's list only when a real session
                // carries it — an honest row for a real record,
                // never a seventh curio on offer.
                val cabinetArtifacts = if (
                    PARTIAL_STAMP_ID in forgedArtifactIds || forgeStats.containsKey(PARTIAL_STAMP_ID)
                ) {
                    artifacts + PARTIAL_STAMP_ARTIFACT
                } else {
                    artifacts
                }
                items(cabinetArtifacts, key = { "cabinet-${it.id}" }) { item ->
                    val forged = item.id in forgedArtifactIds
                    val stat = forgeStats[item.id]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                role = androidx.compose.ui.semantics.Role.Button
                            ) { cabinetArtifactId = item.id }
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
                                text = "NOT YET FORGED",
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
                                text = "$forgeDay · ${record.intention}$gapMark",
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
        return
    }

    // ------------------------------------------------------------------
    // RECEIPTS — the index of claimed slips (issue #157). Ruled
    // rows like everything else in the Drawer. A row prints its
    // №, its date, its outcome word — and, where the session's
    // own record proves it, the warning the slip carried, in
    // Drawer type: the warnings live in the furniture now, so
    // they survive the slip. Tapping a row re-presents the slip.
    // ------------------------------------------------------------------
    if (view == DrawerView.RECEIPTS) {
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
                        text = "RECEIPTS",
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
                            .clickable { viewName = DrawerView.LANDING.name }
                            .testTag("receipts_back")
                    )
                }
            }
            if (receipts.isEmpty()) {
                item {
                    Text(
                        text = "NO RECEIPTS YET",
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
        return
    }

    // ------------------------------------------------------------------
    // LIST VIEWS — landing / month index / one month / search results.
    // ------------------------------------------------------------------
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("drawer_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
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

        // Search — one entry line. Live under seal (issue #8):
        // reading is free and a query edits nothing stored.
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
            // Issue #8 wave-2 (AUDIT-drawer §2, via PR #428 /
            // W6-BAU-8, re-ported by DURCH-A8): the capsule is
            // dead — the field is a ruled ENTRY LINE: 0dp radius,
            // no fill, no border box, one 1dp bottom rule, 44dp,
            // typed text keeping the placeholder's costume
            // (Anonymous Pro 12sp). Search is ENABLED while
            // sealed: a query mutates nothing stored, so
            // "findable in seconds" holds in the sealed state;
            // the old inert box invited typing it refused while
            // printing TYPE TO FIND A TASK.
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                if (drawerQuery.isEmpty()) {
                    Text(
                        text = stringResource(R.string.drawer_search_hint),
                        fontFamily = AnonymousProFamily,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = drawerQuery,
                    onValueChange = onQueryChange,
                    enabled = true,
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
            // Issue #52 F2 cure, placement corrected by the C7
            // art panel: the escape sits BESIDE the field as a
            // Row sibling — never over the query it rescues —
            // at the full 48dp target with a Button role. The
            // escape is always live: clearing is not editing,
            // so the seal does not gate it.
            if (searching) {
                Text(
                    text = "CLEAR SEARCH",
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = scheme.onBackground,
                    modifier = Modifier
                        .clickable(
                            role = androidx.compose.ui.semantics.Role.Button
                        ) { onQueryChange("") }
                        .sizeIn(minHeight = 48.dp)
                        .padding(horizontal = 4.dp, vertical = 14.dp)
                        .testTag("drawer_search_clear")
                )
            }
            }
        }

        if (searching) {
            // ---------- SEARCH RESULTS (all years, paged) ----------
            // The empty line prints only when NEITHER a task nor a
            // named day word matched (#158 F2): a word match alone
            // yields groups (possibly row-less, word-only days).
            if (searchGroups.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.drawer_nothing_under, drawerQuery),
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 13.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                searchGroups.forEach { group ->
                    item(key = "search-head-${group.date}") {
                        // #158 F2: the real DayHeading, so a found
                        // day keeps its declared word in search too.
                        // (R3b #163 fix-forward: this site kept the
                        // pre-F1 parameter names through the #378
                        // merge and broke main's compile — it now
                        // takes the same canDeclare form as every
                        // other DayHeading site.)
                        DayHeading(
                            group = group,
                            canDeclare = !isSealed && (group.date == today ||
                                (group.date == today.minusDays(1) && group.word == null)),
                            onDeclareWord = { word ->
                                if (group.date == today) onDeclareTodayWord(word)
                                else onDeclareDayWord(group.date, word)
                            }
                        )
                    }
                    items(group.rows, key = { "search-${it.id}" }) { task ->
                        // A hit must show its reason (issue #8,
                        // AUDIT-drawer §2, via PR #428): when the
                        // match lives in a substep — not the title
                        // or category — the row prints that step as
                        // a second line.
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
                            onClick = { selectedTaskId = task.id }
                        )
                    }
                }
                if (drawerSearchHasEarlier) {
                    item {
                        EarlierRow(onClick = onLoadEarlierSearch)
                    }
                }
            }
        } else when (view) {
            // Unreachable: the CABINET and RECEIPTS pages return
            // early above, before this column exists. Present for
            // exhaustiveness.
            DrawerView.CABINET, DrawerView.RECEIPTS -> {
                item { Spacer(Modifier.height(0.dp)) }
            }
            DrawerView.INDEX -> {
                // ---------- ALL MONTHS ----------
                item {
                    DrawerNavRow(
                        label = "BACK",
                        onClick = { viewName = DrawerView.LANDING.name }
                    )
                }
                items(indexedMonths, key = { "month-${it.year}-${it.month}" }) { month ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                role = androidx.compose.ui.semantics.Role.Button
                            ) {
                                onSelectMonth(month.year, month.month)
                                monthEnteredFromName = DrawerView.INDEX.name
                                viewName = DrawerView.MONTH.name
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

            DrawerView.MONTH -> {
                // ---------- ONE MONTH ----------
                // #158 F3(ii): prev/next walk the months actually
                // present in storedMonths (newest first): ‹ goes
                // to the previous (older) stored month, › to the
                // next (newer) one, both through selectDrawerMonth.
                val monthPos = storedMonths.indexOfFirst {
                    it.year == drawerMonth.first && it.month == drawerMonth.second
                }
                val olderMonth = if (monthPos >= 0) storedMonths.getOrNull(monthPos + 1) else null
                val newerMonth = if (monthPos >= 0) storedMonths.getOrNull(monthPos - 1) else null
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
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (olderMonth != null) {
                                // #336: month paging glyphs are ICONS
                                // from the one system, not text chars.
                                androidx.compose.material3.Icon(
                                    imageVector = com.example.ui.theme.MonolithIcons.ChevronLeft,
                                    contentDescription = "Previous month",
                                    tint = scheme.onBackground,
                                    modifier = Modifier
                                        .clickable(
                                            role = androidx.compose.ui.semantics.Role.Button
                                        ) { onSelectMonth(olderMonth.year, olderMonth.month) }
                                        .sizeIn(minHeight = 48.dp, minWidth = 48.dp)
                                        .padding(vertical = 14.dp)
                                        .testTag("drawer_month_prev")
                                        .size(16.dp)
                                )
                            }
                            if (newerMonth != null) {
                                androidx.compose.material3.Icon(
                                    imageVector = com.example.ui.theme.MonolithIcons.ChevronRight,
                                    contentDescription = "Next month",
                                    tint = scheme.onBackground,
                                    modifier = Modifier
                                        .clickable(
                                            role = androidx.compose.ui.semantics.Role.Button
                                        ) { onSelectMonth(newerMonth.year, newerMonth.month) }
                                        .sizeIn(minHeight = 48.dp, minWidth = 48.dp)
                                        .padding(vertical = 14.dp)
                                        .testTag("drawer_month_next")
                                        .size(16.dp)
                                )
                            }
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
                                        // #158 F3(i): back to the
                                        // surface entered from, and
                                        // NOTHING else — no
                                        // onSelectMonth(current…):
                                        // going back must not reload
                                        // the current month or throw
                                        // away the place being read.
                                        viewName = monthEnteredFrom.name
                                    }
                                    .sizeIn(minHeight = 48.dp)
                                    .padding(vertical = 14.dp)
                                    .testTag("drawer_month_back")
                            )
                        }
                    }
                }
                if (monthGroups.isEmpty()) {
                    // Issue #52 F3 cure: three different prints for
                    // three different facts — a read that FAILED, a
                    // read still IN FLIGHT, and a month that is
                    // genuinely EMPTY. Never a blank screen.
                    item {
                        when {
                            drawerError != null -> Text(
                                text = drawerError,
                                fontFamily = AnonymousProFamily,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = scheme.error,
                                modifier = Modifier
                                    .padding(vertical = 24.dp)
                                    .testTag("drawer_error")
                            )
                            drawerLoading -> Text(
                                text = "READING THE DRAWER…",
                                fontFamily = AnonymousProFamily,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = scheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(vertical = 24.dp)
                                    .testTag("drawer_loading")
                            )
                            else -> Text(
                                text = "NOTHING FILED IN " +
                                    monthLabel(drawerMonth.first, drawerMonth.second),
                                fontFamily = AnonymousProFamily,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = scheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 24.dp)
                            )
                        }
                    }
                }
                monthGroups.forEach { group ->
                    item(key = "month-head-${group.date}") {
                        DayHeading(
                            group = group,
                            canDeclare = !isSealed && (group.date == today ||
                                (group.date == today.minusDays(1) && group.word == null)),
                            onDeclareWord = { word ->
                                if (group.date == today) onDeclareTodayWord(word)
                                else onDeclareDayWord(group.date, word)
                            }
                        )
                    }
                    items(group.rows, key = { "month-${it.id}" }) { task ->
                        StoredRow(task = task, onClick = { selectedTaskId = task.id })
                    }
                }
                if (drawerError != null && monthGroups.isNotEmpty()) {
                    item {
                        Text(
                            text = drawerError,
                            fontFamily = AnonymousProFamily,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = scheme.error,
                            modifier = Modifier.testTag("drawer_error")
                        )
                    }
                }
                if (drawerHasEarlier) {
                    item { EarlierRow(onClick = onLoadEarlier) }
                }
            }

            DrawerView.LANDING -> {
                // ---------- LANDING: ALL MONTHS + current month ----------
                // Issue #52 F3: while the first read is in flight,
                // or when it failed, the landing prints that fact
                // instead of a premature "NOTHING STORED YET".
                if (drawerError != null && drawerRows.isEmpty()) {
                    item {
                        Text(
                            text = drawerError,
                            fontFamily = AnonymousProFamily,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = scheme.error,
                            modifier = Modifier
                                .padding(vertical = 24.dp)
                                .testTag("drawer_error")
                        )
                    }
                } else if (drawerLoading && drawerRows.isEmpty()) {
                    item {
                        Text(
                            text = "READING THE DRAWER…",
                            fontFamily = AnonymousProFamily,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(vertical = 24.dp)
                                .testTag("drawer_loading")
                        )
                    }
                } else if (globallyEmpty) {
                    item {
                        Text(
                            text = stringResource(R.string.drawer_nothing_yet),
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 13.sp,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                    // The day-word capture must exist even here: a
                    // sick day in the first week, before anything was
                    // ever finished, is exactly when a word needs
                    // declaring (Selbst × Neu). Today's heading
                    // carries it under the empty line — and
                    // tomorrow's stands beside it (issue #8): the
                    // first-week sick night files the coming day
                    // too, before anything was ever finished.
                    item {
                        DayHeading(
                            group = DrawerDayGroup(
                                date = today,
                                rows = emptyList(),
                                word = dayWords[DrawerDay.dateIso(today)]
                            ),
                            canDeclare = !isSealed,
                            onDeclareWord = onDeclareTodayWord
                        )
                    }
                    item {
                        DayHeading(
                            group = DrawerDayGroup(
                                date = tomorrow,
                                rows = emptyList(),
                                word = dayWords[DrawerDay.dateIso(tomorrow)]
                            ),
                            canDeclare = !isSealed,
                            onDeclareWord = { word ->
                                onDeclareDayWord(tomorrow, word)
                            }
                        )
                    }
                    item {
                        // Issue #115: the row prints the name of
                        // the place it opens — CABINET, matching the
                        // screen's own header. "CURIOS" (trinkets in
                        // a shop) told a cold reader nothing about
                        // earned records living behind it. The
                        // testTag keeps its name (internal only).
                        DrawerNavRow(
                            label = "CABINET",
                            onClick = { viewName = DrawerView.CABINET.name },
                            modifier = Modifier.testTag("drawer_curios_row")
                        )
                        HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
                    }
                    item {
                        DrawerNavRow(
                            label = "RECEIPTS",
                            onClick = { viewName = DrawerView.RECEIPTS.name },
                            modifier = Modifier.testTag("drawer_receipts_row")
                        )
                        HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    role = androidx.compose.ui.semantics.Role.Button
                                ) { viewName = DrawerView.INDEX.name }
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
                    // The cabinet's door: ONE furniture row under
                    // ALL MONTHS (chrome C10).
                    item {
                        DrawerNavRow(
                            label = "CABINET",
                            onClick = { viewName = DrawerView.CABINET.name },
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
                            onClick = { viewName = DrawerView.RECEIPTS.name },
                            modifier = Modifier.testTag("drawer_receipts_row")
                        )
                        HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
                    }
                    // Issue #8 (AUDIT-drawer §4 — the audit's worst
                    // finding, via PR #428): TOMORROW is fileable
                    // the night before. When tomorrow carries no
                    // group of its own on this landing (nothing
                    // stored, no word yet — or it falls in the next
                    // filing month), its heading stands at the head
                    // of the day-lines, newest first like the groups
                    // under it, so a sick man at 01:00 files ILL for
                    // the coming day in 2 taps and sleeps. Once
                    // declared, the word retires to print like any
                    // filed day.
                    if (landingGroups.none { it.date == tomorrow }) {
                        item(key = "land-head-tomorrow") {
                            DayHeading(
                                group = DrawerDayGroup(
                                    date = tomorrow,
                                    rows = emptyList(),
                                    word = dayWords[DrawerDay.dateIso(tomorrow)]
                                ),
                                canDeclare = !isSealed,
                                onDeclareWord = { word ->
                                    onDeclareDayWord(tomorrow, word)
                                }
                            )
                        }
                    }
                    // The current month, newest first. An empty current
                    // month renders in SILENCE under the ALL MONTHS row —
                    // no absence line greets a month that has barely begun.
                    landingGroups.forEach { group ->
                        item(key = "land-head-${group.date}") {
                            DayHeading(
                                group = group,
                                canDeclare = !isSealed && (group.date == today ||
                                    group.date == tomorrow ||
                                    (group.date == today.minusDays(1) && group.word == null)),
                                onDeclareWord = { word ->
                                    if (group.date == today) onDeclareTodayWord(word)
                                    else onDeclareDayWord(group.date, word)
                                }
                            )
                        }
                        items(group.rows, key = { "land-${it.id}" }) { task ->
                            StoredRow(task = task, onClick = { selectedTaskId = task.id })
                        }
                    }
                    if (drawerHasEarlier) {
                        item { EarlierRow(onClick = onLoadEarlier) }
                    }
                }
            }
        }
    }
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

/**
 * A date heading: a shelf label only — nothing follows it, no total,
 * no grade. A declared word rides beside it as print. On TODAY's
 * heading, while unsealed, the declaration itself lives here: the
 * three words, tappable, changeable all day — tapping the active
 * word clears it. While sealed the words are print and answer
 * nothing.
 */
@Composable
private fun DayHeading(
    group: DrawerDayGroup,
    // R3b #163 F1: the picker is offered for today, and for
    // yesterday while yesterday still carries no word — the call
    // sites compute that; the heading just renders the offer.
    canDeclare: Boolean,
    onDeclareWord: (String?) -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = if (group.word != null) {
                "${headingLabel(group.date, withYear = false)} · ${group.word}"
            } else {
                headingLabel(group.date, withYear = false)
            },
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold,
            // Issue #8 (SPITZ Charge 10a, via PR #428): one heading
            // voice — 10.5sp, the size every other Drawer heading
            // print uses; the search headings already share it.
            fontSize = 10.5.sp,
            letterSpacing = 1.sp,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
        )
        if (canDeclare) {
            // Issue #8 (SPITZ Charge 9 / Demand 4, via PR #403):
            // the ruled mis-tap geometry — 24dp between words and
            // 4dp of horizontal padding per word, so a 1 AM
            // fat-finger lands on the word it meant.
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                DayWordStore.WORDS.forEach { word ->
                    val active = group.word == word
                    Text(
                        text = word,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        // Issue #8 (SPITZ Charge 10b): bright =
                        // asking. An undeclared line prints at full
                        // ink; once a word is declared the line
                        // retires to print (variant) — except the
                        // declared word itself, which stays full.
                        color = if (group.word == null || active) {
                            scheme.onBackground
                        } else {
                            scheme.onSurfaceVariant
                        },
                        modifier = Modifier
                            .clickable(
                                role = androidx.compose.ui.semantics.Role.Button
                            ) { onDeclareWord(if (active) null else word) }
                            .sizeIn(minHeight = 48.dp)
                            .padding(horizontal = 4.dp, vertical = 12.dp)
                            .testTag("declare_word_${word.lowercase(java.util.Locale.ROOT)}")
                    )
                }
            }
        }
    }
}

/** One stored row: title, category, finish time, lifetime total
 *  spent (printed TOTAL — see the file header, Issue #110). No
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
            .clickable(
                role = androidx.compose.ui.semantics.Role.Button
            ) { onClick() }
            .sizeIn(minHeight = 48.dp)
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
        // Search only: the step that matched, printed as the
        // row's reason (issue #8, AUDIT-drawer §2). Null
        // everywhere else.
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
            Text(
                text = task.categoryTag,
                fontFamily = AnonymousProFamily,
                fontSize = 10.5.sp,
                color = scheme.onSurfaceVariant
            )
            Text(
                text = clockTime(LocalContext.current, task.completedAtMillis),
                fontFamily = AnonymousProFamily,
                fontSize = 10.5.sp,
                color = scheme.onSurfaceVariant
            )
            Text(
                // Issue #110, finding 2: TOTAL — the lifetime
                // accrual riding under the finish day's shelf,
                // not time that day contained.
                text = "TOTAL ${spentLabel(task.actualSecondsSpent)}",
                fontFamily = AnonymousProFamily,
                fontSize = 10.5.sp,
                color = scheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
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
                text = "SHIELD FAULT — SEE RECEIPT",
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
                text = "THE SHIELD WAS OFF FOR PART OF THAT SESSION",
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
