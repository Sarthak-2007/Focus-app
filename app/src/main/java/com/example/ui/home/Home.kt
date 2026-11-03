package com.example.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType

/**
 * NAV-ALL (the NORMAL-APP redirect, Sarthak's word 2026-11-03:
 * "Cant u build a normal fucking app") — HOME as a normal app
 * screen. A small door picture at the top (the seven-ink print,
 * the app's icon art), the app's name, today's status in plain
 * words, and five big labelled buttons — SIGN TOMORROW · TODAY ·
 * APPS · SETTINGS · PAST DAYS — each wired to its real place.
 * No full-screen picture, no hidden furniture: what a button
 * says is where it goes. The buttons stand in EVERY unsealed
 * state — signed or not, day born or not — so a fresh install
 * is an app, not a picture with one button (the build-5 lesson:
 * signing does not stand until a day is born, and a home that
 * hides APPS/SETTINGS behind a born day strands the first run).
 *
 * The Strongroom acts are untouched underneath: SIGN TOMORROW
 * stands the signing table, TODAY shows the standing day and
 * carries the seal act, APPS/SETTINGS stand the setup side
 * (picker · permissions · guard), PAST DAYS opens the drawer.
 */
data class HomeState(
    /** Today's standing, one plain line ("Sealed until 05:30" ·
     *  "Today is signed — it seals at the first sitting" ·
     *  "No day signed for today"). */
    val todayLine: String,
    /** Tomorrow's standing, one plain line, or null when nothing
     *  is signed for tomorrow yet. */
    val tomorrowLine: String? = null,
)

data class HomeActions(
    val onSignTomorrow: () -> Unit,
    val onToday: () -> Unit,
    val onApps: () -> Unit,
    val onSettings: () -> Unit,
    val onPastDays: () -> Unit,
)

@Composable
fun HomeScreen(
    state: HomeState,
    actions: HomeActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StrongroomRoles.Field)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Image(
            painter = painterResource(
                id = com.example.R.drawable.strongroom_door_print
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Text(
            text = "Ulysses",
            style = StrongroomType.Scr3,
            color = StrongroomRoles.OnGround,
            modifier = Modifier.padding(top = 14.dp)
        )
        Text(
            text = state.todayLine,
            style = StrongroomType.Anon1,
            color = StrongroomInks.Gold,
            modifier = Modifier.padding(top = 4.dp)
        )
        state.tomorrowLine?.let { line ->
            Text(
                text = line,
                style = StrongroomType.Anon1,
                color = StrongroomRoles.OnGround.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        HomeButton(
            label = "SIGN TOMORROW",
            primary = true,
            onClick = actions.onSignTomorrow
        )
        HomeButton(label = "TODAY", onClick = actions.onToday)
        HomeButton(label = "APPS", onClick = actions.onApps)
        HomeButton(label = "SETTINGS", onClick = actions.onSettings)
        HomeButton(label = "PAST DAYS", onClick = actions.onPastDays)
    }
}

@Composable
private fun HomeButton(
    label: String,
    primary: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (primary) StrongroomInks.Gold
                else StrongroomInks.Violet
            )
            // One merged button node — label and click on the
            // same element, the way a real button reads (and
            // the way a tap, a switch, or a test finds it).
            .semantics(mergeDescendants = true) {
                contentDescription = label
            }
            .clickable { onClick() }
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = StrongroomType.Scr2,
            color = if (primary) StrongroomRoles.Field
            else StrongroomRoles.OnGround
        )
    }
}

/**
 * TODAY — the standing day in plain words: its status, his task
 * line, the lifting hour, how much is locked, and — on the
 * mornings the seal can be struck — the SEAL TODAY button wired
 * to the same activation the swing act fires. Nothing here
 * invents a state: every line is the day record's own fact, and
 * when no day stands the screen says so and points at the
 * evening's act.
 */
data class TodayState(
    val statusLine: String,
    val taskLine: String? = null,
    val liftingHourText: String? = null,
    val lockedCount: Int? = null,
    val canSeal: Boolean = false,
)

data class TodayActions(
    val onSealToday: () -> Unit,
    val onBack: () -> Unit,
)

@Composable
fun TodayScreen(
    state: TodayState,
    actions: TodayActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StrongroomRoles.Field)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "‹ HOME",
            style = StrongroomType.Anon2,
            color = StrongroomInks.Gold,
            modifier = Modifier
                .clickable { actions.onBack() }
                .padding(vertical = 6.dp)
        )
        Text(
            text = "Today",
            style = StrongroomType.Scr3,
            color = StrongroomRoles.OnGround,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = state.statusLine,
            style = StrongroomType.Scr1,
            color = StrongroomInks.Gold,
            modifier = Modifier.padding(top = 14.dp)
        )
        state.taskLine?.takeIf { it.isNotBlank() }?.let { task ->
            Text(
                text = task,
                style = StrongroomType.Scr1,
                color = StrongroomRoles.OnGround,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        state.liftingHourText?.let { hour ->
            Text(
                text = "Lifts at $hour",
                style = StrongroomType.Anon1,
                color = StrongroomRoles.OnGround.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        state.lockedCount?.let { count ->
            Text(
                text = "$count apps locked",
                style = StrongroomType.Anon1,
                color = StrongroomRoles.OnGround.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        if (state.canSeal) {
            Spacer(Modifier.height(22.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(StrongroomInks.Gold)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "SEAL TODAY"
                    }
                    .clickable { actions.onSealToday() }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SEAL TODAY",
                    style = StrongroomType.Scr2,
                    color = StrongroomRoles.Field,
                    textAlign = TextAlign.Center
                )
            }
        }
        if (!state.canSeal && state.lockedCount == null) {
            Text(
                text = "Days are signed the night before. " +
                    "Tonight, use SIGN TOMORROW on the home screen.",
                style = StrongroomType.Anon1,
                color = StrongroomRoles.OnGround.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
