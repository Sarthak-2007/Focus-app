@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.ui.setup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.AppBlockTier
import com.example.domain.StrongroomGuard
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * SETUP — plain, normal settings screens (Sarthak's redirect,
 * 2026-11-03: "build a normal app"). Standard Android settings
 * patterns throughout, nothing themed, nothing coined: a
 * settings list, switches that show the real state, radio
 * buttons for choices, the system time and date pickers, a
 * plain number field. A screen his mother could read.
 *
 * Four sections behind one doorway entry, reachable in
 * unsealed hours only (the host stands this surface only
 * inside the doorway place's unsealed branch — while a block
 * is on, there is no route into the normal app):
 *
 *  - LIFTING TIME — the time his day ends (the sealed block
 *    lifts). Set with the normal time picker and kept in
 *    SetupStore. A day is born from this standing time when no
 *    session window stands — on a fresh install it is the only
 *    source, so without it no day can start and the home stays
 *    an empty door (build 5). One day's time can still be
 *    changed at that day's signing.
 *  - PERMISSIONS — every permission the app needs, each row
 *    showing its REAL state read from the system (the same
 *    reads the guard sweep trusts), the switch showing that
 *    state, and an "Open settings" button opening the real
 *    system page for it. Autostart is the one Android will not
 *    report: its row says so plainly and takes his word
 *    ("Mark done") — shown as done-by-his-word, never as a
 *    system grant.
 *  - APPS — the installed apps (the catalogue the app keeps
 *    synced — installed apps only), each with its real icon
 *    and name and three radio choices: Blocked · Peek (2 min)
 *    · Allowed. The choice writes the catalogue row the day's
 *    birth reads, so what he sets here is what tomorrow's
 *    signing and the block itself use.
 *  - GUARD — a plain on/off switch, the daily allowance as a
 *    plain number of minutes, and stretch setup with a date
 *    picker. The three rules as ruled, in plain words under
 *    each control: lowering the allowance applies straight
 *    away; raising it starts tomorrow at 3:30 AM, never today,
 *    and can't be set once today's allowance is spent; off
 *    starts at the next day-start and is refused while a block
 *    is running; during a stretch there is no off at all — the
 *    end date he picked is the only way out.
 */

enum class SetupSection { HOME, DAY, PERMISSIONS, APPS, GUARD }

/** One permission row: [key] is the WatchItemId name (or the
 *  NOTIFICATIONS key for the runtime grant), [stateText] is the
 *  row's real standing in plain words, as the host read it. */
data class SetupPermissionRow(
    val key: String,
    val title: String,
    val stateText: String,
    val granted: Boolean
)

/** One app row: the catalogue row's own facts, re-expressed,
 *  plus the app's real icon as the host loaded it (null until
 *  loaded, and in fabricated harness states). */
data class SetupAppRow(
    val packageName: String,
    val appName: String,
    val tier: AppBlockTier,
    val icon: ImageBitmap? = null
)

data class SetupState(
    val section: SetupSection = SetupSection.HOME,
    /** His standing lifting time as read from SetupStore;
     *  [liftingHour] -1 = never set — no day can start. */
    val liftingHour: Int = -1,
    val liftingMinute: Int = 0,
    val permissions: List<SetupPermissionRow> = emptyList(),
    val apps: List<SetupAppRow> = emptyList(),
    val guard: StrongroomGuard.GuardState? = null,
    /** The last guard action's result, in plain words — the
     *  host fills it from the store verbs' own results. */
    val guardNote: String = "",
    /** The stretch's end, formatted for reading ("" = none). */
    val stretchEndText: String = ""
) {
    val grantedCount: Int get() = permissions.count { it.granted }
    val liftingSet: Boolean get() = liftingHour in 0..23
    val liftingTime: LocalTime? get() =
        if (liftingSet) LocalTime.of(liftingHour, liftingMinute) else null
}

data class SetupActions(
    val onOpenSection: (SetupSection) -> Unit = {},
    val onBackToSetupHome: () -> Unit = {},
    val onClose: () -> Unit = {},
    val onSetLiftingTime: (Int, Int) -> Unit = { _, _ -> },
    val onPermissionTap: (String) -> Unit = {},
    val onAttestAutostart: () -> Unit = {},
    val onSetTier: (String, AppBlockTier) -> Unit = { _, _ -> },
    val onSetAllowance: (Int) -> Unit = {},
    val onGuardOn: () -> Unit = {},
    val onGuardOff: () -> Unit = {},
    val onBeginStretch: (String) -> Unit = {},
    val onDone: () -> Unit = {}
)

private val TimeFormat: DateTimeFormatter =
    DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
private val DateFormat: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())

@Composable
fun Setup(state: SetupState, actions: SetupActions, modifier: Modifier = Modifier) {
    val atHome = state.section == SetupSection.HOME
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (state.section) {
                            SetupSection.HOME -> "Setup"
                            SetupSection.DAY -> "Lifting time"
                            SetupSection.PERMISSIONS -> "Permissions"
                            SetupSection.APPS -> "Apps"
                            SetupSection.GUARD -> "Guard"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (atHome) actions.onClose()
                            else actions.onBackToSetupHome()
                        }
                    ) {
                        Icon(
                            imageVector = if (atHome) Icons.Default.Close
                            else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (atHome) "Close" else "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (state.section) {
                SetupSection.HOME -> SetupHome(state, actions)
                SetupSection.DAY -> DayPanel(state, actions)
                SetupSection.PERMISSIONS -> PermissionsList(state, actions)
                SetupSection.APPS -> AppsList(state, actions)
                SetupSection.GUARD -> GuardPanel(state, actions)
            }
        }
    }
}

@Composable
private fun SetupHome(state: SetupState, actions: SetupActions) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        Text(
            "Set these once, here. You can come back and change " +
                "them any time.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        val lifting = state.liftingTime
        SettingsRow(
            title = "Lifting time",
            detail = if (lifting != null) {
                "Your day ends at ${lifting.format(TimeFormat)}"
            } else {
                "Not set — a day can't start until you set this"
            },
            onClick = { actions.onOpenSection(SetupSection.DAY) }
        )
        SettingsRow(
            title = "Permissions",
            detail = if (state.permissions.isEmpty()) "Not read yet"
            else "${state.grantedCount} of ${state.permissions.size} granted",
            onClick = { actions.onOpenSection(SetupSection.PERMISSIONS) }
        )
        SettingsRow(
            title = "Apps",
            detail = if (state.apps.isEmpty()) "No apps read yet"
            else "${state.apps.count { it.tier == AppBlockTier.LOCKED }} blocked · " +
                "${state.apps.count { it.tier == AppBlockTier.PEEK_2M }} peek · " +
                "${state.apps.count { it.tier == AppBlockTier.OPEN }} allowed",
            onClick = { actions.onOpenSection(SetupSection.APPS) }
        )
        SettingsRow(
            title = "Guard",
            detail = state.guard?.let {
                (if (it.guardOn) "On" else "Off") +
                    " · ${it.allowanceMinutes} minutes a day" +
                    if (state.stretchEndText.isNotEmpty()) {
                        " · stretch to ${state.stretchEndText}"
                    } else ""
            } ?: "Not read yet",
            onClick = { actions.onOpenSection(SetupSection.GUARD) }
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { actions.onDone() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Done")
        }
    }
}

@Composable
private fun SettingsRow(title: String, detail: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(detail) },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
    HorizontalDivider()
}

@Composable
private fun DayPanel(state: SetupState, actions: SetupActions) {
    var showPicker by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (!state.liftingSet) {
            Text(
                "No lifting time is set yet.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Until you set one, a day can't start — there " +
                    "would be no time for it to end. This is the " +
                    "one setting a new install needs first.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(20.dp))
        }
        Text(
            "Your day ends at",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = state.liftingTime?.format(TimeFormat) ?: "Not set",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { showPicker = true }) {
            Text(if (state.liftingSet) "Change time" else "Set time")
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "This is when the block lifts and the phone is yours " +
                "again. Days start from this standing time; if a " +
                "session is already running, its own end time " +
                "counts instead. You can also change a single " +
                "day's time when you sign that day.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    if (showPicker) {
        val pickerState = rememberTimePickerState(
            initialHour = state.liftingTime?.hour ?: 20,
            initialMinute = state.liftingTime?.minute ?: 0,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        actions.onSetLiftingTime(
                            pickerState.hour, pickerState.minute
                        )
                        showPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel")
                }
            },
            text = { TimePicker(state = pickerState) }
        )
    }
}

@Composable
private fun PermissionsList(state: SetupState, actions: SetupActions) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        Text(
            "These are the system's real answers, read just now. " +
                "Tap \"Open settings\" and turn the permission on " +
                "there; when you come back, the switch here shows " +
                "the new answer.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        state.permissions.forEach { row ->
            ListItem(
                headlineContent = { Text(row.title) },
                supportingContent = { Text(row.stateText) },
                leadingContent = {
                    // The switch shows the real state. These
                    // grants live in system settings, so the
                    // switch itself does not take a tap.
                    Switch(checked = row.granted, onCheckedChange = null)
                },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (row.key == KEY_AUTOSTART && !row.granted) {
                            TextButton(onClick = { actions.onAttestAutostart() }) {
                                Text("Mark done")
                            }
                        }
                        TextButton(onClick = { actions.onPermissionTap(row.key) }) {
                            Text("Open settings")
                        }
                    }
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun AppsList(state: SetupState, actions: SetupActions) {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Text(
                "Installed apps only. Blocked apps stay shut while " +
                    "a day is running. Peek gives an app a 2-minute " +
                    "check-in. Allowed apps are left alone. This " +
                    "list is what tomorrow's day is built from.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
        if (state.apps.isEmpty()) {
            item {
                Text(
                    "No apps read yet — the list is still loading.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
        items(state.apps, key = { it.packageName }) { row ->
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(row)
                    Spacer(Modifier.size(14.dp))
                    Column {
                        Text(
                            row.appName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            row.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 56.dp, end = 8.dp, bottom = 6.dp)
                ) {
                    TierOption("Blocked", row.tier == AppBlockTier.LOCKED) {
                        actions.onSetTier(row.packageName, AppBlockTier.LOCKED)
                    }
                    TierOption("Peek 2 min", row.tier == AppBlockTier.PEEK_2M) {
                        actions.onSetTier(row.packageName, AppBlockTier.PEEK_2M)
                    }
                    TierOption("Allowed", row.tier == AppBlockTier.OPEN) {
                        actions.onSetTier(row.packageName, AppBlockTier.OPEN)
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun AppIcon(row: SetupAppRow) {
    val icon = row.icon
    if (icon != null) {
        Image(
            bitmap = icon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    } else {
        // No icon loaded (or a fabricated harness state): the
        // normal placeholder — the app's first letter on a
        // plain tile.
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = row.appName.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TierOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun GuardPanel(state: SetupState, actions: SetupActions) {
    val guard = state.guard
    // The field re-seeds whenever the standing allowance moves
    // (a tighten landing, a raise taking effect at day-start).
    var allowanceText by remember(guard?.allowanceMinutes) {
        mutableStateOf((guard?.allowanceMinutes ?: 0).toString())
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var stretchIso by remember { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        if (guard == null) {
            Text(
                "Loading…",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
            return@Column
        }
        ListItem(
            headlineContent = { Text("Guard") },
            supportingContent = {
                Text(if (guard.guardOn) "On" else "Off")
            },
            trailingContent = {
                Switch(
                    checked = guard.guardOn,
                    // During a stretch there is no off — the
                    // switch is disabled and the words below say
                    // why. Otherwise the switch works the verbs:
                    // on is instant; off starts at the next
                    // day-start, and the note below prints what
                    // the verb actually did.
                    enabled = state.stretchEndText.isEmpty(),
                    onCheckedChange = { on ->
                        if (on) actions.onGuardOn() else actions.onGuardOff()
                    }
                )
            }
        )
        if (state.guardNote.isNotEmpty()) {
            Text(
                state.guardNote,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        if (state.stretchEndText.isNotEmpty()) {
            Text(
                "A stretch is running until ${state.stretchEndText}. " +
                    "There is no off switch during a stretch — the " +
                    "end date you picked is the only way out.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        Text(
            "Daily allowance",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            "How many minutes of peeked apps you get each day.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = allowanceText,
                onValueChange = { text ->
                    allowanceText = text.filter { it.isDigit() }.take(3)
                },
                label = { Text("Minutes") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.size(12.dp))
            val parsed = allowanceText.toIntOrNull()
            Button(
                onClick = { parsed?.let { actions.onSetAllowance(it) } },
                enabled = parsed != null && parsed in 0..600 &&
                    parsed != guard.allowanceMinutes
            ) {
                Text("Save")
            }
        }
        Text(
            "Lowering it applies straight away. Raising it starts " +
                "tomorrow at 3:30 AM — never the same day.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (guard.pendingAllowanceMinutes >= 0) {
            Text(
                "A raise to ${guard.pendingAllowanceMinutes} minutes " +
                    "is set to start at the next day-start (3:30 AM).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        Text(
            "Stretch",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (state.stretchEndText.isNotEmpty()) {
            Text(
                "Running until ${state.stretchEndText}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        } else {
            Text(
                "Keep the guard on until a date you pick now. " +
                    "During a stretch there is no off switch — " +
                    "pick the end date like you mean it.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            if (stretchIso.isNotEmpty()) {
                Text(
                    "Ends: ${
                        runCatching {
                            java.time.LocalDate.parse(stretchIso)
                                .format(DateFormat)
                        }.getOrDefault(stretchIso)
                    }",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = { showDatePicker = true }) {
                    Text("Choose end date")
                }
                Spacer(Modifier.size(12.dp))
                Button(
                    onClick = { actions.onBeginStretch(stretchIso) },
                    enabled = stretchIso.isNotEmpty()
                ) {
                    Text("Start stretch")
                }
            }
        }
    }
    if (showDatePicker) {
        val dateState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            stretchIso = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                                .toString()
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = dateState, showModeToggle = false)
        }
    }
}

/** The autostart row's key — not a WatchItemId the sweep can
 *  measure; the host maps it to the vendor pane + his word. */
const val KEY_AUTOSTART = "AUTOSTART"

/** The notifications row's key — the POST_NOTIFICATIONS runtime
 *  grant, read directly (it is not one of the six watch grants). */
const val KEY_NOTIFICATIONS = "NOTIFICATIONS"
