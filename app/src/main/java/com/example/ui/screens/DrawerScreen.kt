package com.example.ui.screens

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
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import kotlinx.coroutines.delay
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

private enum class DrawerView { LANDING, MONTH }

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
    dayWords: Map<String, String>,
    isSealed: Boolean,
    isDarkTheme: Boolean = false,
    onSelectMonth: (Int, Int) -> Unit,
    onLoadEarlier: () -> Unit,
    onQueryChange: (String) -> Unit,
    onLoadEarlierSearch: () -> Unit,
    onDeclareDayWord: (LocalDate, String?) -> Unit
    // Issue #8 (AUDIT-drawer §2A): the cabinet is EVICTED from the
    // Drawer — no CURIOS row, no cabinet view, no cabinet params.
    // Its data stays in code (CurioCatalog, the DISTINCT forged-id
    // read, the VM's forged flow) for its future home (issue #21);
    // nothing is deleted — it simply has no Drawer address.
) {
    val scheme = MaterialTheme.colorScheme
    // AUDIT-drawer §2 (wave-2): view state is FORMAL and saveable —
    // the view name and the selected task's ID survive process
    // death; a mid-browse man is no longer reset to LANDING by the
    // OS. The task object itself is re-read from the accumulated
    // rows at composition, never stored in the bundle.
    var viewName by rememberSaveable { mutableStateOf(DrawerView.LANDING.name) }
    val view = runCatching { DrawerView.valueOf(viewName) }.getOrDefault(DrawerView.LANDING)
    var selectedTaskId by rememberSaveable { mutableStateOf(-1) }
    val selectedTask: FocusTaskEntity? = if (selectedTaskId > 0) {
        (drawerRows + drawerResults).firstOrNull { it.id == selectedTaskId }
    } else {
        null
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
                        text = "STORED",
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        color = scheme.onSurfaceVariant
                    )
                    Text(
                        text = "BACK",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = scheme.onBackground,
                        modifier = Modifier
                            .clickable { selectedTaskId = -1 }
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
                    Text(
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SPENT ${spentLabel(detail.actualSecondsSpent)}",
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
        return
    }

    // ------------------------------------------------------------------
    // LIST VIEWS — landing (current month + MONTHS shelf) / one month / search results.
    // ------------------------------------------------------------------
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
            Text(
                text = "STORED",
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
                            text = "TYPE TO FIND A TASK",
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
                        text = "BACK",
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
                        text = "NO STORED TASK UNDER “$drawerQuery”",
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
                        Text(
                            text = headingLabel(group.date, withYear = true),
                            fontFamily = AnonymousProFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
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
            DrawerView.MONTH -> {
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
                            text = "BACK",
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = scheme.onBackground,
                            modifier = Modifier
                                .clickable { viewName = DrawerView.LANDING.name }
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
                        DayHeading(group = group)
                    }
                    items(group.rows, key = { "month-${it.id}" }) { task ->
                        StoredRow(task = task, onClick = { selectedTaskId = task.id })
                    }
                }
                if (drawerHasEarlier) {
                    item { EarlierRow(onClick = onLoadEarlier) }
                }
            }

            DrawerView.LANDING -> {
                // ---------- LANDING: current month + MONTHS shelf ----------
                if (globallyEmpty) {
                    item {
                        Text(
                            text = "NOTHING STORED YET",
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
                            DayHeading(group = group)
                        }
                        items(group.rows, key = { "land-${it.id}" }) { task ->
                            StoredRow(task = task, onClick = { selectedTaskId = task.id })
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
                    val earlierMonths = storedMonths.filterNot {
                        it.year == currentYearMonth.first &&
                            it.month == currentYearMonth.second
                    }
                    if (earlierMonths.isNotEmpty()) {
                        item {
                            Text(
                                text = "MONTHS",
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
                                        viewName = DrawerView.MONTH.name
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
 */
@Composable
private fun DayHeading(group: DrawerDayGroup) {
    val scheme = MaterialTheme.colorScheme
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
        modifier = Modifier.padding(top = 6.dp)
    )
}

/**
 * One filing-zone day-line (AUDIT-drawer §2/§4, wave-2): a date and
 * its three words. Undeclared and unsealed: the words are tappable
 * (tapping the active word clears it). Declared: the line retires
 * to pure print — date · WORD, read-only, exactly the DayHeading
 * voice. Sealed: print only; filing is an edit.
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
            text = "${headingLabel(date, withYear = false)} · $word",
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
        Text(
            text = headingLabel(date, withYear = false),
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp,
            letterSpacing = 1.sp,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
        )
        if (!isSealed) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DayWordStore.WORDS.forEach { w ->
                    Text(
                        text = w,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable { onDeclare(w) }
                            .padding(vertical = 6.dp)
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
            Text(
                text = task.categoryTag,
                fontFamily = AnonymousProFamily,
                fontSize = 10.5.sp,
                color = scheme.onSurfaceVariant
            )
            Text(
                text = clockTime(task.completedAtMillis),
                fontFamily = AnonymousProFamily,
                fontSize = 10.5.sp,
                color = scheme.onSurfaceVariant
            )
            Text(
                text = spentLabel(task.actualSecondsSpent),
                fontFamily = AnonymousProFamily,
                fontSize = 10.5.sp,
                color = scheme.onSurfaceVariant
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
            .clickable { onClick() }
            .padding(vertical = 10.dp)
            .testTag("drawer_earlier"),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "EARLIER",
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            color = scheme.onBackground
        )
    }
}
