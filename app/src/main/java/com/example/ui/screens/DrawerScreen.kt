package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import com.example.data.CurioCatalog
import com.example.data.DayWordStore
import com.example.data.DrawerDay
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.data.KineticArtifact
import com.example.data.SessionOutcome
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
 * fully, search input and every edit are inert.
 *
 * Days file by the 03:30 rule (DrawerDay); row finish times print
 * true clock. A declared day word (REST / ILL / FAMILY) prints
 * beside its date heading as his authored word — never a number.
 */

private enum class DrawerView { LANDING, INDEX, MONTH, CABINET }

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
    onDeclareTodayWord: (String?) -> Unit,
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
    curioForgeRecords: Map<String, List<FocusSessionEntity>> = emptyMap()
) {
    val scheme = MaterialTheme.colorScheme
    var view by remember { mutableStateOf(DrawerView.LANDING) }
    var selectedTask by remember { mutableStateOf<FocusTaskEntity?>(null) }
    var cabinetArtifact by remember { mutableStateOf<KineticArtifact?>(null) }

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
                            .clickable { selectedTask = null }
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
                        text = detail.categoryTag,
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
                    // Estimate and actual as two separate plain facts —
                    // never a delta, never a percentage. The estimate
                    // prints only where one was actually given
                    // (MINUTES DIE): a name-only task has no estimate
                    // line, never an invented "ESTIMATE 0 MIN".
                    if (detail.estimatedMinutes > 0) {
                        Text(
                            text = "ESTIMATE ${detail.estimatedMinutes} MIN",
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 13.sp,
                            color = scheme.onBackground
                        )
                    }
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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = step.title,
                                    fontFamily = AtkinsonHyperlegibleFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.5.sp,
                                    color = if (step.isDone) scheme.onSurfaceVariant else scheme.onBackground,
                                    textDecoration = if (step.isDone) TextDecoration.LineThrough else TextDecoration.None,
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
                        text = "CABINET",
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
                            .clickable {
                                if (cabinetArtifact != null) {
                                    cabinetArtifact = null
                                } else {
                                    view = DrawerView.LANDING
                                }
                            }
                            .testTag("cabinet_back")
                    )
                }
            }
            if (artifact == null) {
                items(artifacts, key = { "cabinet-${it.id}" }) { item ->
                    val forged = item.id in forgedArtifactIds
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { cabinetArtifact = item }
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
                                    text = "FORGED",
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
                // forge does (Repository: a finished span forges
                // one artifact, chosen at random; routine arms
                // never forge). No per-artifact prices, no
                // thresholds nothing enforces.
                item {
                    Text(
                        // Audit R3b #145: the rule, scoped to what
                        // the code now actually does on every hand
                        // finish (timed and FLOW alike). Routine-born
                        // spans forge nothing — the fence stands
                        // while the routines rethink owns them — and
                        // the print no longer claims otherwise.
                        text = "A SPAN YOU FINISH YOURSELF FORGES ONE — THE FORGE CHOOSES WHICH.",
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
                            text = if (artifact.id in forgedArtifactIds) {
                                "FORGED — IT STANDS IN THE CABINET"
                            } else {
                                "NOT YET FORGED"
                            },
                            fontFamily = AnonymousProFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = scheme.onBackground
                        )
                        // Audit R3b #145: provenance — every forge
                        // of this artifact, counted and dated, with
                        // the span's intention and its outcome. A
                        // duplicate forge is a second line, never a
                        // silent no-op; a shield-gap forge says so.
                        val forgeRecords = curioForgeRecords[artifact.id].orEmpty()
                        if (forgeRecords.isNotEmpty()) {
                            Text(
                                text = "FORGED ×${forgeRecords.size}",
                                fontFamily = AnonymousProFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = scheme.onBackground
                            )
                            forgeRecords.forEach { record ->
                                val forgeDay = java.text.SimpleDateFormat(
                                    "d MMM yyyy", Locale.US
                                ).format(java.util.Date(record.startWallTimeMillis))
                                val gapMark = if (
                                    record.outcome == SessionOutcome.ELAPSED_SHIELD_GAP
                                ) {
                                    " · SHIELD GAP"
                                } else {
                                    ""
                                }
                                Text(
                                    text = "$forgeDay · ${record.intention}$gapMark",
                                    fontFamily = AnonymousProFamily,
                                    fontSize = 10.5.sp,
                                    color = scheme.onSurfaceVariant
                                )
                            }
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
    // LIST VIEWS — landing / month index / one month / search results.
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

        // Search — one field. While sealed it is composed but
        // input-disabled: reading is free, typing is an edit.
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(scheme.surfaceVariant)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp),
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
                    enabled = !isSealed,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = scheme.onBackground
                    ),
                    cursorBrush = SolidColor(scheme.onSurfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("drawer_search_input")
                )
            }
        }

        if (searching) {
            // ---------- SEARCH RESULTS (all years, paged) ----------
            if (drawerResults.isEmpty()) {
                item {
                    Text(
                        text = "NOTHING STORED UNDER “$drawerQuery”",
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
                        StoredRow(task = task, onClick = { selectedTask = task })
                    }
                }
                if (drawerSearchHasEarlier) {
                    item {
                        EarlierRow(onClick = onLoadEarlierSearch)
                    }
                }
            }
        } else when (view) {
            // Unreachable: the CABINET page returns early above,
            // before this column exists. Present for exhaustiveness.
            DrawerView.CABINET -> {
                item { Spacer(Modifier.height(0.dp)) }
            }
            DrawerView.INDEX -> {
                // ---------- ALL MONTHS ----------
                item {
                    DrawerNavRow(
                        label = "BACK",
                        onClick = { view = DrawerView.LANDING }
                    )
                }
                items(storedMonths, key = { "month-${it.year}-${it.month}" }) { month ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectMonth(month.year, month.month)
                                view = DrawerView.MONTH
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
                        Text(
                            text = "BACK",
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = scheme.onBackground,
                            modifier = Modifier.clickable {
                                onSelectMonth(currentYearMonth.first, currentYearMonth.second)
                                view = DrawerView.LANDING
                            }
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
                            isToday = group.date == today,
                            isSealed = isSealed,
                            onDeclareTodayWord = onDeclareTodayWord
                        )
                    }
                    items(group.rows, key = { "month-${it.id}" }) { task ->
                        StoredRow(task = task, onClick = { selectedTask = task })
                    }
                }
                if (drawerHasEarlier) {
                    item { EarlierRow(onClick = onLoadEarlier) }
                }
            }

            DrawerView.LANDING -> {
                // ---------- LANDING: ALL MONTHS + current month ----------
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
                    // The day-word capture must exist even here: a
                    // sick day in the first week, before anything was
                    // ever finished, is exactly when a word needs
                    // declaring (Selbst × Neu). Today's heading
                    // carries it under the empty line.
                    item {
                        DayHeading(
                            group = DrawerDayGroup(
                                date = today,
                                rows = emptyList(),
                                word = dayWords[DrawerDay.dateIso(today)]
                            ),
                            isToday = true,
                            isSealed = isSealed,
                            onDeclareTodayWord = onDeclareTodayWord
                        )
                    }
                    item {
                        DrawerNavRow(
                            label = "CURIOS",
                            onClick = { view = DrawerView.CABINET },
                            modifier = Modifier.testTag("drawer_curios_row")
                        )
                        HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { view = DrawerView.INDEX }
                                .padding(vertical = 6.dp)
                                .testTag("drawer_all_months"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ALL MONTHS",
                                fontFamily = AnonymousProFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = scheme.onBackground
                            )
                            Text(
                                text = "›",
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontSize = 14.sp,
                                color = scheme.onSurfaceVariant
                            )
                        }
                        HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
                    }
                    // The cabinet's door: ONE furniture row under
                    // ALL MONTHS (chrome C10).
                    item {
                        DrawerNavRow(
                            label = "CURIOS",
                            onClick = { view = DrawerView.CABINET },
                            modifier = Modifier.testTag("drawer_curios_row")
                        )
                        HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
                    }
                    // The current month, newest first. An empty current
                    // month renders in SILENCE under the ALL MONTHS row —
                    // no absence line greets a month that has barely begun.
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
                                isToday = group.date == today,
                                isSealed = isSealed,
                                onDeclareTodayWord = onDeclareTodayWord
                            )
                        }
                        items(group.rows, key = { "land-${it.id}" }) { task ->
                            StoredRow(task = task, onClick = { selectedTask = task })
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
            .clickable { onClick() }
            .padding(vertical = 6.dp)
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
    isToday: Boolean,
    isSealed: Boolean,
    onDeclareTodayWord: (String?) -> Unit
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
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
        )
        if (isToday && !isSealed) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DayWordStore.WORDS.forEach { word ->
                    val active = group.word == word
                    Text(
                        text = word,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (active) scheme.onBackground else scheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable { onDeclareTodayWord(if (active) null else word) }
                            .testTag("declare_word_${word.lowercase()}")
                    )
                }
            }
        }
    }
}

/** One stored row: title, category, finish time, time spent. No
 *  estimate, no checkbox, no bar, no rank. */
@Composable
private fun StoredRow(task: FocusTaskEntity, onClick: () -> Unit) {
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
