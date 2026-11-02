package com.example.ui.airlock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.DistractionTriggerReason
import com.example.data.MonolithRepository
import com.example.session.AirlockInterceptState
import com.example.session.PeekWithheld
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType

/**
 * NAV-ALL (VERIFY-SCREENS addition 2) — THE AIRLOCK's plain
 * working surface, and the judgement that seats it: NOT dead
 * code. The sealed face / knock answers reaches under a
 * STRONGROOM seal (the containment doors judge them first and
 * never mint this state). But under a LEGACY session — the
 * engine that still arms from routines, loadouts and his
 * carried data — the shield / accessibility doors fire an
 * intercept, [MonolithViewModel.triggerAirlockIntercept] mints
 * [AirlockInterceptState] visible, the Activity comes forward,
 * and until this surface existed NOTHING rendered it: he stood
 * on the doorway with Back consumed and the door's acts
 * (return, the peek, the reason) unreachable. A reach outside a
 * seal can land here, so the door gets its face.
 *
 * Everything on the face is the state's own fact, and every
 * control is a live VM verb — nothing decorative, nothing dead:
 *  - RETURN TO FOCUS → transmuteImpulseAndReturn (books
 *    "Stayed Focused"). For a LOCKED-tier app the state's wall
 *    gates RETURN itself (FIX-5b/#99: the delay is the point);
 *    the button wakes when the wall's anchored end passes.
 *  - The wall's expiry arms the peek offer's sound through
 *    onAirlockOfferArmed, exactly once per visit (the VM's
 *    nonce guard keeps it honest).
 *  - 2-MINUTE PEEK → start2MinutePeek (re-derives the offer at
 *    grant, launches the app under its lease). When the peek is
 *    withheld the face prints the state's own reason; a
 *    COOLDOWN's expiry calls refreshAirlockDoorState (FIX-5c).
 *  - The reason chips → selectDistractionReason.
 *  - Inside the grace window (the state's intercept-aged
 *    elapsed against the repository's 30 s law) the quiet
 *    cancel stands → confirmGraceAbortFromAirlock.
 * Deliberately NOT on this face: the disarm entry
 * (requestDisarmFromAirlock) — its target, the Disarm challenge
 * face, has no composable of its own yet; a button whose
 * landing does not exist would be a dead end wearing a label.
 * Named in NAV-ALL's return beside Setup / App picker / Guard
 * settings, which are the same class of gap.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Airlock(
    state: AirlockInterceptState,
    onReturn: () -> Unit,
    onPeek: () -> Unit,
    onReason: (DistractionTriggerReason) -> Unit,
    onGraceAbort: () -> Unit,
    onOfferArmed: () -> Unit,
    onCooldownExpired: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The wall and the cooldown are anchored monotonic ENDS on
    // the state (FIX-5b), in SystemClock.elapsedRealtime's
    // domain; every print derives remaining = end − now sampled
    // from that same clock on a short beat. No counter ticks
    // itself here — the ends are the truth, this only reads.
    var nowMono by remember(state.interceptNonce) {
        mutableLongStateOf(android.os.SystemClock.elapsedRealtime())
    }
    LaunchedEffect(state.interceptNonce) {
        while (true) {
            nowMono = android.os.SystemClock.elapsedRealtime()
            kotlinx.coroutines.delay(200L)
        }
    }
    val wallLeftMillis =
        if (state.wallEndsAtMono > 0L) {
            (state.wallEndsAtMono - nowMono).coerceAtLeast(0L)
        } else {
            0L
        }
    val wallStanding = state.wallEndsAtMono > 0L && wallLeftMillis > 0L
    val cooldownLeftMillis =
        if (state.peekCooldownEndsAtMonoMillis > 0L) {
            (state.peekCooldownEndsAtMonoMillis - nowMono)
                .coerceAtLeast(0L)
        } else {
            0L
        }

    // FIX-7: the wall's expiry arms the offer (once per visit —
    // the VM's nonce guard is the lock, this is only the call).
    LaunchedEffect(state.interceptNonce, wallStanding) {
        if (!wallStanding && state.canUse2MinutePeek) onOfferArmed()
    }
    // FIX-5c: a cooldown that runs out under the standing door
    // re-derives the offer in place.
    LaunchedEffect(state.interceptNonce, cooldownLeftMillis) {
        if (state.peekWithheld == PeekWithheld.COOLDOWN &&
            state.peekCooldownEndsAtMonoMillis > 0L &&
            cooldownLeftMillis <= 0L
        ) {
            onCooldownExpired()
        }
    }

    // The grace window, aged from the state's own anchors: the
    // verified elapsed at the intercept plus the monotonic age
    // of the visit, against the repository's grace law.
    val agedElapsedSeconds =
        if (state.sessionElapsedSecondsAtIntercept >= 0 &&
            state.interceptedAtMonoMillis > 0L && nowMono > 0L
        ) {
            state.sessionElapsedSecondsAtIntercept +
                ((nowMono - state.interceptedAtMonoMillis) / 1000L)
                    .toInt()
        } else {
            -1
        }
    val graceStanding = agedElapsedSeconds in
        0..MonolithRepository.GRACE_WINDOW_SECONDS

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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
        ) {
            Text(
                text = state.interceptReasonBadge.uppercase(),
                style = StrongroomType.Anon2,
                color = StrongroomInks.Rose,
                textAlign = TextAlign.Center,
            )
            Text(
                text = if (state.justExpiredLease) {
                    "THE PEEK ENDED"
                } else {
                    state.interceptedAppName.ifBlank { "Blocked App" }
                },
                style = StrongroomType.Scr3,
                color = StrongroomRoles.OnGround,
                textAlign = TextAlign.Center,
            )
            state.nameSubLine?.takeIf { it.isNotBlank() }?.let { sub ->
                Text(
                    text = sub,
                    style = StrongroomType.Anon1,
                    color = StrongroomRoles.OnGround.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
            }
            if (state.sessionIntention.isNotBlank()) {
                Text(
                    text = "“${state.sessionIntention}”",
                    style = StrongroomType.Scr1,
                    color = StrongroomInks.Gold,
                    textAlign = TextAlign.Center,
                )
            }

            // The reason chips: why the hand reached. One tap
            // books the state's selected reason.
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp, Alignment.CenterHorizontally
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DistractionTriggerReason.entries.forEach { reason ->
                    val selected = reason == state.selectedReason
                    Text(
                        text = reason.label,
                        style = StrongroomType.Anon1,
                        color = if (selected) {
                            StrongroomRoles.Field
                        } else {
                            StrongroomRoles.OnGround
                        },
                        modifier = Modifier
                            .background(
                                if (selected) {
                                    StrongroomInks.Gold
                                } else {
                                    StrongroomRoles.Field
                                }
                            )
                            .border(
                                1.dp,
                                if (selected) {
                                    StrongroomInks.Gold
                                } else {
                                    StrongroomRoles.OnGround
                                        .copy(alpha = 0.4f)
                                },
                            )
                            .clickable { onReason(reason) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
            }

            // RETURN — gated by the wall on a locked tier.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (wallStanding) {
                            StrongroomRoles.Field
                        } else {
                            StrongroomInks.Gold
                        }
                    )
                    .border(1.dp, StrongroomInks.Gold)
                    .clickable(enabled = !wallStanding) { onReturn() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (wallStanding) {
                        "WAIT ${airlockCeilSeconds(wallLeftMillis)}"
                    } else {
                        "RETURN TO FOCUS"
                    },
                    style = StrongroomType.Scr2,
                    color = if (wallStanding) {
                        StrongroomInks.Gold
                    } else {
                        StrongroomRoles.Field
                    },
                    textAlign = TextAlign.Center,
                )
            }

            // The peek: the offer, or the state's withheld truth.
            when {
                state.canUse2MinutePeek -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, StrongroomRoles.OnGround)
                        .clickable { onPeek() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "2-MINUTE PEEK",
                        style = StrongroomType.Scr2,
                        color = StrongroomRoles.OnGround,
                        textAlign = TextAlign.Center,
                    )
                }
                state.peekWithheld == PeekWithheld.COOLDOWN -> Text(
                    text = "PEEK AGAIN IN " +
                        airlockMmSs(airlockCeilSeconds(cooldownLeftMillis)),
                    style = StrongroomType.Anon2,
                    color = StrongroomRoles.OnGround.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
                state.peekWithheld == PeekWithheld.ALREADY_ACTIVE -> Text(
                    text = "A PEEK IS ALREADY RUNNING",
                    style = StrongroomType.Anon2,
                    color = StrongroomRoles.OnGround.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
                state.peekWithheld == PeekWithheld.PEEK_OFF -> Text(
                    text = "PEEK IS OFF",
                    style = StrongroomType.Anon2,
                    color = StrongroomRoles.OnGround.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
                state.peekWithheld == PeekWithheld.TIER_LOCKED -> Text(
                    text = "THIS APP IS LOCKED — NO PEEK",
                    style = StrongroomType.Anon2,
                    color = StrongroomRoles.OnGround.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
                else -> Unit
            }

            if (graceStanding) {
                Text(
                    text = "STARTED BY ACCIDENT — CANCEL",
                    style = StrongroomType.Anon2,
                    color = StrongroomInks.Orange,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable { onGraceAbort() }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                )
            }
        }
    }
}

/** Ceiling seconds for a remaining span (a wall never prints 0
 *  while it still stands). */
internal fun airlockCeilSeconds(millisLeft: Long): Int =
    if (millisLeft <= 0L) 0 else ((millisLeft + 999L) / 1000L).toInt()

internal fun airlockMmSs(totalSeconds: Int): String {
    val s = totalSeconds.coerceAtLeast(0)
    return "%d:%02d".format(s / 60, s % 60)
}
