package com.example.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.data.AppBlockTier
import com.example.domain.StrongroomGuard
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomType

/**
 * SETUP-FLOW — the setup side of the app, plain and working
 * (Sarthak's order, 2026-11-03: working functionality first,
 * design later; build all the screens and navigation).
 *
 * Three plain screens behind one doorway entry, all reachable in
 * unsealed hours and nowhere else (the host stands this surface
 * only inside the doorway place's unsealed branch — while a seal
 * stands there is no route into the normal app, D-7):
 *
 *  - PERMISSIONS — every grant the engine needs, each row showing
 *    its REAL state read from the system (the repository's own
 *    watch queries — the same reads the Guard sweep trusts), and
 *    each tap opening the real Settings pane for that grant.
 *    AUTOSTART is the one door Android will not report: its row
 *    opens the vendor's pane where one exists and takes his word
 *    ("MARK DONE") as the sworn state, printed as SWORN, never
 *    GRANTED — the Watch Store's own law (F44).
 *  - APPS — the installed catalogue the repository keeps synced
 *    (installed apps only), each row carrying the three tiers the
 *    engine itself runs on: SHUT (LOCKED) · PEEK (PEEK_2M) ·
 *    OPEN. A tap writes the row through the entity's own
 *    normalisation — the exact rows [com.example.data.LegacyCarry]
 *    reads when a day is born, so what he sets here is what the
 *    signing and the seal inherit.
 *  - DAY — his standing lifting hour: the hour a sealed day
 *    lifts (ends), set by his hand here and kept in SetupStore.
 *    The day's birth reads it when no session window stands —
 *    on a fresh install it is the ONLY lifting source, so
 *    without it no day can be born and the home stays an empty
 *    door (build 5). The evening signing still moves a single
 *    day's hour; this is the standing hour days are born from.
 *  - GUARD — the standing guard, worked through the store's own
 *    verbs, the three rules behaving as ruled: tighten lands
 *    instantly; a raise is scheduled from the next day-start
 *    (03:30) and refused outright once the day's purse is spent;
 *    off is scheduled to the next day-start, refused while a
 *    sealed block stands, and inside a stretch there is NO off
 *    at all — the stretch's chosen end date is the only exit.
 *
 * Plain look by order: flat graphite field, gold lines, the
 * house type. No richness work lives here; the design pass is a
 * later programme and changes nothing in the wiring.
 */

enum class SetupSection { HOME, DAY, PERMISSIONS, APPS, GUARD }

/** One permission row: [key] is the WatchItemId name (or the
 *  NOTIFICATIONS key for the runtime grant), [stateText] is the
 *  row's real standing as the host read it. */
data class SetupPermissionRow(
    val key: String,
    val title: String,
    val stateText: String,
    val granted: Boolean
)

/** One app row: the catalogue row's own facts, re-expressed. */
data class SetupAppRow(
    val packageName: String,
    val appName: String,
    val tier: AppBlockTier
)

data class SetupState(
    val section: SetupSection = SetupSection.HOME,
    /** His standing lifting hour as read from SetupStore;
     *  [liftingHour] -1 = never set — no day can be born. */
    val liftingHour: Int = -1,
    val liftingMinute: Int = 0,
    val permissions: List<SetupPermissionRow> = emptyList(),
    val apps: List<SetupAppRow> = emptyList(),
    val guard: StrongroomGuard.GuardState? = null,
    /** The last Guard act's outcome, in plain words — the host
     *  fills it from the store verbs' own results, never invented. */
    val guardNote: String = "",
    /** The stretch's end, formatted for the row ("" = no stretch). */
    val stretchEndText: String = ""
) {
    val grantedCount: Int get() = permissions.count { it.granted }
    val liftingSet: Boolean get() = liftingHour in 0..23
    val liftingText: String get() = if (liftingSet) {
        "${liftingHour.toString().padStart(2, '0')}:" +
            liftingMinute.toString().padStart(2, '0')
    } else ""
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

private val Field = StrongroomInks.Graphite
private val Line = StrongroomInks.Gold
private val InkOnField = Color(0xFFE8E6E0)
private val DimInk = Color(0xFFA8A6B2)

@Composable
fun Setup(state: SetupState, actions: SetupActions, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Field)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = when (state.section) {
                    SetupSection.HOME -> "SETUP"
                    SetupSection.DAY -> "SETUP — DAY"
                    SetupSection.PERMISSIONS -> "SETUP — PERMISSIONS"
                    SetupSection.APPS -> "SETUP — APPS"
                    SetupSection.GUARD -> "SETUP — GUARD"
                },
                style = StrongroomType.Scr3,
                color = Line,
                modifier = Modifier.weight(1f)
            )
            PlainButton(
                text = if (state.section == SetupSection.HOME) "CLOSE" else "BACK",
                onClick = {
                    if (state.section == SetupSection.HOME) actions.onClose()
                    else actions.onBackToSetupHome()
                }
            )
        }
        Spacer(Modifier.height(14.dp))
        when (state.section) {
            SetupSection.HOME -> SetupHome(state, actions)
            SetupSection.DAY -> DayPanel(state, actions)
            SetupSection.PERMISSIONS -> PermissionsList(state, actions)
            SetupSection.APPS -> AppsList(state, actions)
            SetupSection.GUARD -> GuardPanel(state, actions)
        }
    }
}

@Composable
private fun SetupHome(state: SetupState, actions: SetupActions) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Text(
            "What the app needs before a day can run. Set it once; " +
                "change it here any evening.",
            style = StrongroomType.Scr1, color = DimInk
        )
        Spacer(Modifier.height(16.dp))
        EntryRow(
            title = "DAY",
            detail = if (state.liftingSet) "day lifts at ${state.liftingText}"
            else "no lifting hour set — no day can be born yet",
            onClick = { actions.onOpenSection(SetupSection.DAY) }
        )
        EntryRow(
            title = "PERMISSIONS",
            detail = if (state.permissions.isEmpty()) "not read yet"
            else "${state.grantedCount} of ${state.permissions.size} granted",
            onClick = { actions.onOpenSection(SetupSection.PERMISSIONS) }
        )
        EntryRow(
            title = "APPS",
            detail = if (state.apps.isEmpty()) "no apps read yet"
            else "${state.apps.count { it.tier == AppBlockTier.LOCKED }} shut · " +
                "${state.apps.count { it.tier == AppBlockTier.PEEK_2M }} peek · " +
                "${state.apps.count { it.tier == AppBlockTier.OPEN }} open",
            onClick = { actions.onOpenSection(SetupSection.APPS) }
        )
        EntryRow(
            title = "GUARD",
            detail = state.guard?.let {
                (if (it.guardOn) "on" else "off") +
                    " · allowance ${it.allowanceMinutes} min/day" +
                    if (state.stretchEndText.isNotEmpty()) " · stretch to ${state.stretchEndText}" else ""
            } ?: "not read yet",
            onClick = { actions.onOpenSection(SetupSection.GUARD) }
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Line, RoundedCornerShape(6.dp))
                .clickable { actions.onDone() }
                .semantics { contentDescription = "finish setup" }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "DONE — BACK TO HOME",
                style = StrongroomType.Scr3,
                color = Field
            )
        }
    }
}

@Composable
private fun EntryRow(title: String, detail: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, Line, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .semantics { contentDescription = title.lowercase() }
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Text(title, style = StrongroomType.Scr3, color = InkOnField)
        Spacer(Modifier.height(2.dp))
        Text(detail, style = StrongroomType.Scr1, color = DimInk)
    }
}

@Composable
private fun DayPanel(state: SetupState, actions: SetupActions) {
    // The steppers work from the standing hour when one stands,
    // else from 20:00 — the first tap on a fresh install sets
    // the hour it lands on, printed on the plate before he taps.
    val baseMinutes = if (state.liftingSet) {
        state.liftingHour * 60 + state.liftingMinute
    } else 20 * 60
    fun shift(delta: Int) {
        val total = ((baseMinutes + delta) % 1440 + 1440) % 1440
        actions.onSetLiftingTime(total / 60, total % 60)
    }
    Column(Modifier.verticalScroll(rememberScrollState())) {
        if (!state.liftingSet) {
            Text(
                "No lifting hour is set. Without one, no day can " +
                    "be born — the home stays an empty door with " +
                    "nothing to sign. Set it here and the day stands.",
                style = StrongroomType.Scr2, color = StrongroomInks.Rose
            )
            Spacer(Modifier.height(14.dp))
        }
        Text(
            "YOUR DAY LIFTS AT",
            style = StrongroomType.Scr3, color = InkOnField
        )
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Line, RoundedCornerShape(6.dp))
                .semantics { contentDescription = "lifting hour" }
                .padding(vertical = 22.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (state.liftingSet) state.liftingText else "20:00",
                style = StrongroomType.Scr6,
                color = Field
            )
        }
        Spacer(Modifier.height(12.dp))
        Row {
            PlainButton("−1H") { shift(-60) }
            Spacer(Modifier.padding(horizontal = 4.dp))
            PlainButton("−15M") { shift(-15) }
            Spacer(Modifier.padding(horizontal = 4.dp))
            PlainButton("+15M") { shift(15) }
            Spacer(Modifier.padding(horizontal = 4.dp))
            PlainButton("+1H") { shift(60) }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "The hour your sealed day lifts — the block ends and " +
                "the phone is yours again. Days are born from this " +
                "standing hour; a running session's own window end " +
                "outranks it, and the evening signing can still " +
                "move one single day's hour.",
            style = StrongroomType.Scr1, color = DimInk
        )
        if (!state.liftingSet) {
            Spacer(Modifier.height(10.dp))
            Text(
                "The plate shows 20:00 — tap any step and that " +
                    "becomes your hour.",
                style = StrongroomType.Scr1, color = Line
            )
        }
    }
}

@Composable
private fun PermissionsList(state: SetupState, actions: SetupActions) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Text(
            "Each row is the system's real answer, read now. " +
                "Tap OPEN and grant it in Settings; come back and it re-reads.",
            style = StrongroomType.Scr1, color = DimInk
        )
        Spacer(Modifier.height(12.dp))
        state.permissions.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .border(1.dp, Line, RoundedCornerShape(6.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(row.title, style = StrongroomType.Scr2, color = InkOnField)
                    Text(
                        row.stateText,
                        style = StrongroomType.Scr1,
                        color = if (row.granted) Line else StrongroomInks.Rose
                    )
                }
                if (row.key == KEY_AUTOSTART && !row.granted) {
                    PlainButton("MARK DONE") { actions.onAttestAutostart() }
                    Spacer(Modifier.padding(horizontal = 4.dp))
                }
                PlainButton("OPEN") { actions.onPermissionTap(row.key) }
            }
        }
    }
}

@Composable
private fun AppsList(state: SetupState, actions: SetupActions) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Text(
            "Installed apps only. SHUT locks it while a day is sealed, " +
                "PEEK gives it a 2-minute check-in, OPEN leaves it alone. " +
                "This is the list tomorrow's signing is built from.",
            style = StrongroomType.Scr1, color = DimInk
        )
        Spacer(Modifier.height(12.dp))
        if (state.apps.isEmpty()) {
            Text(
                "No apps read yet — the catalogue is still syncing.",
                style = StrongroomType.Scr2, color = InkOnField
            )
        }
        state.apps.forEach { row ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .border(1.dp, Line, RoundedCornerShape(6.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(row.appName, style = StrongroomType.Scr2, color = InkOnField)
                Text(row.packageName, style = StrongroomType.Scr0, color = DimInk)
                Spacer(Modifier.height(8.dp))
                Row {
                    TierButton("SHUT", row.tier == AppBlockTier.LOCKED) {
                        actions.onSetTier(row.packageName, AppBlockTier.LOCKED)
                    }
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    TierButton("PEEK", row.tier == AppBlockTier.PEEK_2M) {
                        actions.onSetTier(row.packageName, AppBlockTier.PEEK_2M)
                    }
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    TierButton("OPEN", row.tier == AppBlockTier.OPEN) {
                        actions.onSetTier(row.packageName, AppBlockTier.OPEN)
                    }
                }
            }
        }
    }
}

@Composable
private fun GuardPanel(state: SetupState, actions: SetupActions) {
    val guard = state.guard
    var stretchDate by remember { mutableStateOf("") }
    Column(Modifier.verticalScroll(rememberScrollState())) {
        if (guard == null) {
            Text("Reading the guard…", style = StrongroomType.Scr2, color = InkOnField)
            return@Column
        }
        Text(
            if (guard.guardOn) "The guard is ON." else "The guard is OFF.",
            style = StrongroomType.Scr3, color = InkOnField
        )
        if (state.guardNote.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(state.guardNote, style = StrongroomType.Scr1, color = Line)
        }
        Spacer(Modifier.height(14.dp))
        Row {
            PlainButton("TURN ON") { actions.onGuardOn() }
            Spacer(Modifier.padding(horizontal = 4.dp))
            if (state.stretchEndText.isEmpty()) {
                PlainButton("TURN OFF") { actions.onGuardOff() }
            }
        }
        if (state.stretchEndText.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(
                "A stretch is running — there is no off switch inside " +
                    "a stretch. It ends ${state.stretchEndText}.",
                style = StrongroomType.Scr1, color = DimInk
            )
        }
        Spacer(Modifier.height(22.dp))
        Text(
            "DAILY ALLOWANCE — ${guard.allowanceMinutes} MIN",
            style = StrongroomType.Scr3, color = InkOnField
        )
        Text(
            "Lowering it takes effect now. Raising it starts tomorrow " +
                "at 03:30 — never the same day.",
            style = StrongroomType.Scr1, color = DimInk
        )
        if (guard.pendingAllowanceMinutes >= 0) {
            Text(
                "A raise to ${guard.pendingAllowanceMinutes} min is set " +
                    "to start at the next day-start.",
                style = StrongroomType.Scr1, color = Line
            )
        }
        Spacer(Modifier.height(10.dp))
        Row {
            PlainButton("−10") { actions.onSetAllowance((guard.allowanceMinutes - 10).coerceAtLeast(0)) }
            Spacer(Modifier.padding(horizontal = 4.dp))
            PlainButton("−5") { actions.onSetAllowance((guard.allowanceMinutes - 5).coerceAtLeast(0)) }
            Spacer(Modifier.padding(horizontal = 4.dp))
            PlainButton("+5") { actions.onSetAllowance(guard.allowanceMinutes + 5) }
            Spacer(Modifier.padding(horizontal = 4.dp))
            PlainButton("+10") { actions.onSetAllowance(guard.allowanceMinutes + 10) }
        }
        Spacer(Modifier.height(22.dp))
        Text("STRETCH", style = StrongroomType.Scr3, color = InkOnField)
        if (state.stretchEndText.isNotEmpty()) {
            Text(
                "Running until ${state.stretchEndText}. The end date " +
                    "you chose is the only way out.",
                style = StrongroomType.Scr1, color = DimInk
            )
        } else {
            Text(
                "Lock the guard on until a date you pick now. Inside " +
                    "a stretch there is no off — choose the end like " +
                    "you mean it. Date as 2026-11-10.",
                style = StrongroomType.Scr1, color = DimInk
            )
            Spacer(Modifier.height(10.dp))
            BasicTextField(
                value = stretchDate,
                onValueChange = { stretchDate = it },
                textStyle = TextStyle(color = InkOnField, fontSize = StrongroomType.Scr2.fontSize),
                cursorBrush = SolidColor(Line),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Line, RoundedCornerShape(6.dp))
                    .semantics { contentDescription = "stretch end date" }
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            )
            Spacer(Modifier.height(10.dp))
            PlainButton("BEGIN STRETCH") { actions.onBeginStretch(stretchDate) }
        }
    }
}

@Composable
private fun PlainButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(1.dp, Line, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .semantics { contentDescription = text.lowercase() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = StrongroomType.Scr1, color = Line)
    }
}

@Composable
private fun TierButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .then(
                if (selected) Modifier.background(Line, RoundedCornerShape(6.dp))
                else Modifier.border(1.dp, Line, RoundedCornerShape(6.dp))
            )
            .clickable { onClick() }
            .semantics { contentDescription = text.lowercase() }
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = StrongroomType.Scr1,
            color = if (selected) Field else Line
        )
    }
}

/** The autostart row's key — not a WatchItemId the sweep can
 *  measure; the host maps it to the vendor pane + the oath. */
const val KEY_AUTOSTART = "AUTOSTART"

/** The notifications row's key — the POST_NOTIFICATIONS runtime
 *  grant, read directly (it is not one of the six watch grants). */
const val KEY_NOTIFICATIONS = "NOTIFICATIONS"
