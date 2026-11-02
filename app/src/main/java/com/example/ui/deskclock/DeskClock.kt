package com.example.ui.deskclock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType

/**
 * NAV-ALL (VERIFY-SCREENS addition 1) — THE DESK CLOCK's plain
 * working surface. The ViewModel's desk-clock machine
 * (DeskClockSettle + the ticker beat in MonolithViewModel) has
 * carried the standing law all along — raise after 20 s untouched
 * in-session, keep-awake, the battery surrender — and raised
 * `isAlwaysOnDeskClockVisible` into a tree with no composable
 * rendering it. This face is that composable; the machine stays
 * the law, this only prints what it decided.
 *
 * His ruling (#37, standing): during a session the clock shows
 * ELAPSED + REMAINING — the numbers stay. The figures are the
 * session's whole-span derivations (FocusSessionEntity
 * .spanElapsedSeconds / spanTargetSeconds: the one derivation
 * every surface reads, so the clock can never disagree with the
 * enforcement), interpolated between the row's own ticks on the
 * frame clock from the last anchored snapshot. With no session
 * standing (the hand-raised idle clock) the face is a plain
 * nightstand clock: the wall time, nothing else.
 *
 * READ-ONLY by construction: no control exists on this face.
 * The root consumes touches (it is the face while it stands);
 * the Activity's onUserInteraction still stamps every touch for
 * the machine's wake/yield law. The host wires the two platform
 * halves of the standing: keep-awake from the VM's
 * deskClockKeepAwake flow, and setShowWhenLocked while a session
 * stands, so a power press during a session shows this face
 * over the lock screen.
 */
@Composable
fun DeskClock(
    elapsedSeconds: Int?,
    remainingSeconds: Int?,
    wallTimeText: String,
    modifier: Modifier = Modifier,
) {
    // The anchored interpolation: the session row re-stamps its
    // elapsed on the VM's beat; between beats the face advances
    // from the anchor on the monotonic frame clock, so the
    // seconds move like a clock and not like a poll.
    var shownElapsed by remember { mutableIntStateOf(elapsedSeconds ?: 0) }
    var shownRemaining by remember { mutableIntStateOf(remainingSeconds ?: 0) }
    var anchorMono by remember { mutableLongStateOf(0L) }
    LaunchedEffect(elapsedSeconds, remainingSeconds) {
        shownElapsed = elapsedSeconds ?: 0
        shownRemaining = remainingSeconds ?: 0
        anchorMono = 0L
        if (elapsedSeconds != null) {
            while (true) {
                withFrameMillis { frame ->
                    if (anchorMono == 0L) anchorMono = frame
                    val deltaSeconds =
                        ((frame - anchorMono) / 1000L).toInt()
                    shownElapsed = (elapsedSeconds + deltaSeconds)
                        .coerceAtLeast(0)
                    if (remainingSeconds != null) {
                        shownRemaining =
                            (remainingSeconds - deltaSeconds)
                                .coerceAtLeast(0)
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StrongroomRoles.Field)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {},
        contentAlignment = Alignment.Center,
    ) {
        if (elapsedSeconds != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp),
                modifier = Modifier.padding(horizontal = 32.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ELAPSED",
                        style = StrongroomType.Anon2,
                        color = StrongroomInks.Gold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = deskClockHms(shownElapsed),
                        style = StrongroomType.Anon4.copy(fontSize = 64.sp),
                        color = StrongroomRoles.OnGround,
                        textAlign = TextAlign.Center,
                    )
                }
                if (remainingSeconds != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "REMAINING",
                            style = StrongroomType.Anon2,
                            color = StrongroomInks.Gold,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = deskClockHms(shownRemaining),
                            style = StrongroomType.Anon3.copy(fontSize = 40.sp),
                            color = StrongroomRoles.OnGround,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        } else {
            Text(
                text = wallTimeText,
                style = StrongroomType.Anon4.copy(fontSize = 64.sp),
                color = StrongroomRoles.OnGround,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** H:MM:SS / MM:SS — the desk clock's one print of a span. */
internal fun deskClockHms(totalSeconds: Int): String {
    val s = totalSeconds.coerceAtLeast(0)
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, sec)
    else "%02d:%02d".format(m, sec)
}
