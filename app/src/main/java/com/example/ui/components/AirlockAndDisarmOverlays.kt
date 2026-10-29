package com.example.ui.components

import android.os.SystemClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.CapturedNotificationEntity
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.service.TactileSoundEngine
import com.example.data.MonolithRepository
import com.example.ui.AirlockInterceptState
import com.example.ui.DisarmChallengeState
import com.example.ui.PeekWithheld
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.NightBlockBarSurface
import com.example.ui.theme.NightColorBackground
import com.example.ui.theme.NightColorPrimary
import com.example.ui.theme.NightColorPrimaryContainer
import com.example.ui.theme.NightColorSurfaceBright
import com.example.ui.theme.NightColorSurfaceVariant
import com.example.ui.theme.NightColorOnPrimaryContainer
import com.example.ui.theme.NightColorSecondaryContainer
import com.example.ui.theme.NightTextColorPrimary
import com.example.ui.theme.PillBadgeTextStyle
import com.example.ui.theme.PrimaryButtonTextStyle
import com.example.ui.theme.SoraFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.views.Emergency60sValveView
import com.example.ui.views.TowerStreakView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * 1. ALWAYS-ON NIGHTSTAND DESK CLOCK OVERLAY (Decision #8)
 */
@Composable
fun AlwaysOnDeskClockOverlay(
    visible: Boolean,
    activeSession: FocusSessionEntity?,
    activeTask: FocusTaskEntity?,
    dialMinutes: Int,
    burnInStep: Int,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(MonolithCurves.specB()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        // The desk clock's premise (Round 0 fix): the screen stays on while
        // the clock shows. No FLAG_KEEP_SCREEN_ON existed anywhere in the
        // app, so the "always-on" clock died to the screen timeout. The
        // flag is restored on dispose.
        val clockView = LocalView.current
        DisposableEffect(visible) {
            var ctx: android.content.Context? = clockView.context
            var activity: android.app.Activity? = null
            while (ctx is android.content.ContextWrapper) {
                if (ctx is android.app.Activity) { activity = ctx; break }
                ctx = ctx.baseContext
            }
            val window = activity?.window
            window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            onDispose {
                window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
        val displaySecs = if (activeSession != null && activeSession.isActive) {
            // Timed sessions read the SPAN remaining, never the
            // per-block remaining: the block's remainder is 0 for
            // the whole of a Safe Break, and this clock has no block
            // context - it would read 00:00 (FINISH) through every
            // break. The span counts the break as session time.
            if (activeSession.isStopwatchMode) activeSession.elapsedSeconds else activeSession.spanRemainingSeconds
        } else {
            dialMinutes * 60
        }
        val mm = displaySecs / 60
        val ss = displaySecs % 60
        val timeText = String.format(Locale.US, "%02d:%02d", mm, ss)

        // The old idle branch rendered a fabricated constant (0.65f) -
        // a made-up progress on the nightstand clock, the surface he
        // actually reads at night. Every branch below is a real
        // derivation from the session, or honestly empty.
        val progress = when {
            // The desk clock tells SESSION time only, on the span
            // derivation: a Safe Break earns no special rendering
            // (Nacht's law - the break's one rendering is the Deck's
            // BREAK eyebrow) and the bar never reads empty mid-
            // session. The fabricated 0.65 constant stays dead.
            activeSession != null && activeSession.isActive && !activeSession.isStopwatchMode ->
                (activeSession.spanElapsedSeconds.toFloat() / activeSession.spanTargetSeconds.coerceAtLeast(1)).coerceIn(0.04f, 1f)
            activeSession != null && activeSession.isActive ->
                (activeSession.elapsedSeconds.toFloat() / 1200f).coerceIn(0.04f, 1f)
            else -> 0f
        }

        val dx = ((burnInStep % 5) - 2) * 3
        val dy = (((burnInStep / 5) % 5) - 2) * 3

        Box(
            modifier = Modifier
                .fillMaxSize()
                // AOD ground is true black (Round 1a) — OLED pixels off,
                // not the block-bar tone.
                .background(Color.Black)
                .clickable(onClick = onDismiss)
                .padding(24.dp)
                .testTag("always_on_desk_clock_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(dx, dy) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT",
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = NightColorPrimary.copy(alpha = 0.6f)
                )

                // Hero architectural Space Mono numerals (112sp)
                Text(
                    text = timeText,
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 96.sp,
                    letterSpacing = (-3).sp,
                    color = NightColorPrimary
                )

                // Full-bleed TimelineBar progress strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NightColorSurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            // Session progress in cream — orange is the
                            // arm commit only (Round 1c).
                            .background(NightColorPrimary)
                    )
                }

                // Active task banner — absence is the honest
                // print (Truth Car 3, appendix / FL-06): a real
                // task title, else a declared intention, else NO
                // banner. The old "Deep Focus Session" default
                // fabricated a name for a session nobody named,
                // and a blank intention printed an empty box.
                val bannerText = deskClockBannerText(activeTask?.title, activeSession?.intention)
                if (bannerText != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NightColorSurfaceBright)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = bannerText,
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NightColorPrimary
                        )
                    }
                }
            }
        }
    }
}

/**
 * The desk clock's banner text (Truth Car 3, appendix / FLOW
 * FL-06): a real task title wins; a declared intention stands in;
 * both absent or blank → null and NO banner is composed. There is
 * no default name — "Deep Focus Session" was fabricated.
 */
internal fun deskClockBannerText(taskTitle: String?, intention: String?): String? =
    taskTitle?.takeIf { it.isNotBlank() } ?: intention?.takeIf { it.isNotBlank() }

/**
 * The withheld peek's cooldown print (Truth Car 3, SIL-3; Hale
 * W-30's words): whole minutes, ceiling — 61 seconds is "AGAIN
 * IN 2 MIN", never a rounded-down promise.
 */
internal fun peekCooldownPrint(secondsRemaining: Int): String {
    val minutes = (secondsRemaining.coerceAtLeast(0) + 59) / 60
    return "PEEK COOLDOWN — AGAIN IN $minutes MIN"
}

/**
 * The door's own statement of the session's end (SIL-1, Hale
 * W-28): a timed session states its span end — "SESSION " over
 * [endsAtText]'s instant; a stopwatch states that it has no set
 * end; no session, no line. The door never invents an end.
 */
internal fun airlockSessionLine(endsAtMillis: Long?, isStopwatch: Boolean): String? = when {
    endsAtMillis != null -> "SESSION " + endsAtText(endsAtMillis)
    isStopwatch -> "SESSION RUNNING · NO SET END"
    else -> null
}

/**
 * The peek chip's badge (SIL-2, Hale W-29; FIX-8): the SET value,
 * read from the same constant the lease machinery reads — never
 * a hardcoded "2m" on the surface, and never a truncation:
 * minutes and seconds render honestly (90s → "1m30s", 45s →
 * "45s", 125s → "2m5s"). (Rurik's editable store replaces the
 * constant as the value's source; this function is the one
 * print derivation that changes.)
 */
internal fun peekBadgeText(durationSeconds: Int): String {
    val total = durationSeconds.coerceAtLeast(0)
    val minutes = total / 60
    val seconds = total % 60
    return when {
        minutes > 0 && seconds > 0 -> "${minutes}m${seconds}s"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
    }
}

/**
 * The peek's price, printed on the offer from frame one — before
 * it is taken (FIX-8, re-timing C-11/B-2; Hale W-29's supplied
 * text is replaced along with the name): the machine anchors
 * the cooldown at the lease's END, so from the tap the real
 * exclusion is lease + cooldown, and the sentence now says when
 * the clock starts. Derived from the cooldown the repository
 * will actually arm — never a hardcoded 20 on the surface.
 */
internal fun peekPriceText(cooldownMillis: Long): String {
    val minutes = cooldownMillis / 60_000L
    val unit = if (minutes == 1L) "MINUTE" else "MINUTES"
    return "WHEN THE PEEK ENDS, A $minutes $unit COOLDOWN STARTS ON THIS APP"
}

/**
 * The gate line (FIX-6): the wall as a captioned print, not a
 * hero numeral — "PEEK OPENS IN {M:SS}", minutes unpadded,
 * seconds zero-padded. It names what it gates: the peek, and
 * only the peek.
 */
internal fun peekGateLineText(secondsRemaining: Int): String {
    val secs = secondsRemaining.coerceAtLeast(0)
    return String.format(Locale.US, "PEEK OPENS IN %d:%02d", secs / 60, secs % 60)
}

/**
 * The door's memory line (FIX-8, designer §5.7): the ordinal of
 * THIS visit in the gate line's family — {prior booked + 1}
 * TIME THIS SESSION. The scope word is THIS SESSION (Tor's
 * ratification): the taper counts this session's booked
 * intercepts, and a session is not a night. Printed only when
 * at least one prior booking exists; the door claims no memory
 * it doesn't have.
 */
internal fun doorMemoryLineText(priorInterceptsThisSession: Int): String? {
    if (priorInterceptsThisSession < 1) return null
    return "${ordinalWord(priorInterceptsThisSession + 1)} TIME THIS SESSION"
}

internal fun ordinalWord(n: Int): String = when (n) {
    1 -> "FIRST"
    2 -> "SECOND"
    3 -> "THIRD"
    4 -> "FOURTH"
    5 -> "FIFTH"
    6 -> "SIXTH"
    7 -> "SEVENTH"
    8 -> "EIGHTH"
    9 -> "NINTH"
    10 -> "TENTH"
    else -> {
        val suffix = when {
            n % 100 in 11..13 -> "TH"
            n % 10 == 1 -> "ST"
            n % 10 == 2 -> "ND"
            n % 10 == 3 -> "RD"
            else -> "TH"
        }
        "$n$suffix"
    }
}

/**
 * Seconds until an anchored monotonic end, derived at the
 * frame that prints (FIX-5b): the ceiling form, the same
 * arithmetic as peekSecondsRemaining — a counter that ticks
 * itself can disagree with its anchor; this cannot. An end of
 * 0 (never anchored) or a passed end derives 0.
 */
internal fun derivedSecondsUntil(endMono: Long, nowMono: Long): Int {
    if (endMono <= 0L) return 0
    val remainingMs = endMono - nowMono
    if (remainingMs <= 0L) return 0
    return ((remainingMs + 999L) / 1000L).toInt()
}

/**
 * The break appointment (FIX-8, the Errand's kernel; designer
 * §4.5 wording, Tor-ratified cell spec): the instant the
 * session's own schedule already guarantees, at which the Safe
 * Break's tier law opens this app — no lease, no cooldown.
 * Derived from the carried instant; never a literal.
 */
internal fun breakAppointmentText(nextBreakAtMillis: Long): String =
    "BREAK AT " +
        java.text.SimpleDateFormat("HH:mm", Locale.US).format(java.util.Date(nextBreakAtMillis)) +
        " OPENS THIS APP"

/** The capture's own time, for a frozen row: HH:mm when the
 *  capture is from today, d MMM · HH:mm when it is not — yesterday's
 *  held rows legitimately sit in this morning's snapshot, and the
 *  AS OF stamp dates the snapshot, not the rows. */
private fun captureTimeLabel(timestampMillis: Long): String {
    val zone = java.time.ZoneId.systemDefault()
    val captureDate = java.time.Instant.ofEpochMilli(timestampMillis).atZone(zone).toLocalDate()
    val pattern = if (captureDate == java.time.LocalDate.now(zone)) "HH:mm" else "d MMM · HH:mm"
    return java.text.SimpleDateFormat(pattern, java.util.Locale.US)
        .format(java.util.Date(timestampMillis))
}

/**
 * 2. CAPTURED NOTIFICATIONS DIGEST SHEET
 *
 * Two modes. LIVE (unsealed): the shipped sheet — live list, live
 * count, CLEAR ALL — plus a per-row delete, so one capture can be
 * triaged without wiping the table. FROZEN (sealed): a snapshot,
 * not a feed — the header stamps HELD · AS OF {HH:mm}, there is no
 * count and no CLEAR ALL (absent, not disabled), rows carry their
 * own capture times and no affordance of any kind, and acting is
 * refused at the repository besides. Reading is free; acting stays
 * sealed. Closing is not an act on the held world and works in
 * both modes.
 */
@Composable
fun CapturedNotificationsDigestSheet(
    visible: Boolean,
    notifications: List<CapturedNotificationEntity>,
    frozenAtMillis: Long? = null,
    frozenMode: Boolean = false,
    onClearAll: () -> Unit,
    onDeleteItem: (Int) -> Unit = {},
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        val scheme = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(onClick = onDismiss)
                .padding(12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(scheme.surfaceBright)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (frozenMode) {
                        if (frozenAtMillis != null) {
                            Text(
                                text = rememberMixedTypographyText(
                                    wordPrefix = "HELD · AS OF ",
                                    monoNumber = java.text.SimpleDateFormat("HH:mm", java.util.Locale.US)
                                        .format(java.util.Date(frozenAtMillis)),
                                    wordColor = scheme.onBackground,
                                    monoColor = scheme.primaryContainer
                                ),
                                fontSize = 14.sp
                            )
                        } else {
                            // The freeze is still landing: the frozen
                            // frame with no stamp and no rows yet —
                            // never one frame of the live list.
                            Text(
                                text = "HELD",
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = scheme.onBackground
                            )
                        }
                    } else {
                        Text(
                            text = rememberMixedTypographyText(
                                wordPrefix = "HELD NOTIFICATIONS • ",
                                monoNumber = notifications.size.toString(),
                                wordColor = scheme.onBackground,
                                monoColor = scheme.primaryContainer
                            ),
                            fontSize = 14.sp
                        )
                        BlockitActionChip(
                            wordLabel = "CLEAR ALL",
                            onClick = onClearAll
                        )
                    }
                }
                if (frozenMode && frozenAtMillis != null) {
                    Text(
                        text = "Everything here is as of the time above. Nothing here opens. Anything newer waits until the session ends.",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 13.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)

                if (notifications.isEmpty()) {
                    if (!frozenMode || frozenAtMillis != null) {
                        Text(
                            text = if (frozenMode) {
                                "Nothing has been held."
                            } else {
                                "Zero notifications intercepted during focus."
                            },
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 13.sp,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.height(220.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications, key = { it.id }) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(scheme.surfaceVariant)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (frozenMode) {
                                            "${item.appName} • ${item.senderTitle} • ${captureTimeLabel(item.timestampMillis)}"
                                        } else {
                                            "${item.appName} • ${item.senderTitle}"
                                        },
                                        fontFamily = AtkinsonHyperlegibleFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = scheme.onBackground,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (!frozenMode) {
                                        Text(
                                            text = "DEL",
                                            fontFamily = AtkinsonHyperlegibleFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = scheme.onSurfaceVariant,
                                            modifier = Modifier.clickable { onDeleteItem(item.id) }
                                        )
                                    }
                                }
                                // A title-only capture has no body
                                // (Truth Car 3, appendix): the row
                                // prints the title alone, never an
                                // invented sentence under it.
                                if (item.messageBody.isNotBlank()) {
                                    Text(
                                        text = item.messageBody,
                                        fontFamily = AtkinsonHyperlegibleFamily,
                                        fontSize = 12.sp,
                                        color = scheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                BlockitActionChip(
                    wordLabel = "CLOSE DIGEST",
                    isPrimaryInk = true,
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 4. SHIELD PERMISSION PRIMER MODAL
 */
@Composable
fun ShieldPermissionPrimerModal(
    visible: Boolean,
    systemGuardStatus: com.example.data.SystemGuardStatus,
    onOpenAccessibilitySettings: () -> Unit,
    onOpenUsageAccessSettings: () -> Unit,
    onConfirmAndArmAnyway: () -> Unit,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        val scheme = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(onClick = onDismiss)
                .padding(12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(scheme.surfaceBright)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "SYSTEM GUARD PRE-FLIGHT",
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = scheme.onBackground
                )
                Text(
                    text = "Grant Accessibility & Usage Access so Monolith can intercept distracting apps, or arm the session immediately.",
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 12.5.sp,
                    color = scheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BlockitActionChip(
                        wordLabel = "ACCESSIBILITY",
                        onClick = onOpenAccessibilitySettings,
                        modifier = Modifier.weight(1f)
                    )
                    BlockitActionChip(
                        wordLabel = "USAGE STATS",
                        onClick = onOpenUsageAccessSettings,
                        modifier = Modifier.weight(1f)
                    )
                }

                BlockitActionChip(
                    wordLabel = "COMMIT & ARM SESSION NOW",
                    isAccentArmed = true,
                    onClick = onConfirmAndArmAnyway,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "confirm_arm_anyway_button"
                )
            }
        }
    }
}

/**
 * 5. AIRLOCK INTERCEPTION OVERLAY — THE HELD DOOR (issue #11;
 * designer skeleton, docs/designer-skeleton.md).
 *
 * THE SKELETON LAW: nothing on this face changes position, size,
 * or existence based on countdown state. Every slot is composed
 * at frame one, at its final bounds, for the whole visit;
 * countdown state may change a slot's contents (crossfade in
 * place) — never its bounds, never a sibling's. The card column
 * carries NO spacedBy (spacedBy collapsing around absent
 * children was the original defect's mechanism): all spacing is
 * explicit fixed slots and gaps. RETURN's bounds at wall-max,
 * wall-1, wall-0 and offer-armed are pixel-identical, and are
 * asserted so in HeldDoorStationarityTest.
 *
 * Slot stack (top → bottom): NAME 32 · gap 4 · INTENTION 24 ·
 * gap 4 · SESSION BLOCK 40 (two 20dp lines) · gap 12 · PEEK ZONE
 * 110 or 0 · gap 16 (peek-owing only) · RETURN 56 · gap 12 ·
 * HONEST EXIT 48 (Tor round 2: the innovator's 48dp touch height
 * at the designer's bare-text weight) · gap 8 · FOOTER BADGE 16.
 * Card padding 24 all sides. The card is BOTTOM-anchored (Tor
 * round 8): the peek zone's presence grows the card upward, so
 * RETURN's screen-space address — centre 136dp above the card's
 * bottom edge — is identical on every branch and every visit.
 */
@Composable
fun AirlockInterceptOverlay(
    state: AirlockInterceptState,
    onReturnToFocus: () -> Unit,
    onTake2MinutePeek: () -> Unit,
    onEndSessionInstead: () -> Unit,
    onDoorStateRefresh: () -> Unit,
    // FIX-7 (Ton §5.3): the overlay reports the derived instant
    // the offer arms; the VM owns the engine call and its
    // once-per-visit guard (onAirlockOfferArmed). Defaulted so
    // other callers (tests, previews) need no wire.
    onOfferArmed: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = state.isVisible,
        // FIX-7b: entry is specA at 180ms (was specB's 420ms
        // ghost); exit keeps specA 260ms. Opacity only — the full
        // skeleton is composed at final geometry underneath.
        enter = fadeIn(MonolithCurves.specA(180)),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        // FIX-5b: the clock state is a REDRAW TRIGGER ONLY. Every
        // print below derives from the state's anchored ends at
        // the recomposition this state provokes; no value is
        // counted locally (both old self-ticking counters die).
        // Keyed by the arrival nonce (P-4c): a same-package
        // re-fire starts a fresh visit under its own anchors.
        var nowMono by remember(state.interceptNonce) {
            mutableLongStateOf(SystemClock.elapsedRealtime())
        }
        LaunchedEffect(state.isVisible, state.interceptNonce) {
            if (!state.isVisible) return@LaunchedEffect
            while (true) {
                delay(250L)
                nowMono = SystemClock.elapsedRealtime()
            }
        }

        val wallSecs = derivedSecondsUntil(state.wallEndsAtMono, nowMono)
        val wallRemainingMs = (state.wallEndsAtMono - nowMono).coerceAtLeast(0L)
        val cooldownSecs = derivedSecondsUntil(state.peekCooldownEndsAtMono, nowMono)

        // FIX-5c: at derived 0 the face re-runs the offer
        // derivation (VM side) — "AGAIN IN 0 MIN" never composes,
        // because a cooldown at 0 has no cooldown tenant (below).
        LaunchedEffect(state.peekWithheld, cooldownSecs, state.interceptNonce) {
            if (state.peekWithheld == PeekWithheld.COOLDOWN && cooldownSecs == 0) {
                onDoorStateRefresh()
            }
        }

        val peekOwing = state.canUse2MinutePeek || state.peekWithheld != null

        // Z1's tenant for this frame. Tenants crossfade inside
        // Z1's unchanged 48dp bounds; the zone and every slot
        // below it never move, whichever tenant stands.
        val z1Tenant = when {
            state.canUse2MinutePeek && wallSecs > 0 -> AirlockZ1Tenant.GATE
            state.canUse2MinutePeek -> AirlockZ1Tenant.CHIP
            state.peekWithheld == PeekWithheld.COOLDOWN && cooldownSecs == 0 ->
                AirlockZ1Tenant.RESOLVING
            state.peekWithheld != null -> AirlockZ1Tenant.WITHHELD
            else -> AirlockZ1Tenant.EMPTY
        }

        // TalkBack (Tor round 3): announcements at door OPEN and
        // at offer ARM only — never per-second, never assertive.
        // One polite live region (composed inside the root Box
        // below) whose text changes at exactly those two moments.
        var announcement by remember(state.interceptNonce) { mutableStateOf("") }
        LaunchedEffect(state.isVisible, state.interceptNonce) {
            if (state.isVisible) {
                announcement =
                    "${state.interceptedAppName} is held. Return to focus is available."
            }
        }
        LaunchedEffect(z1Tenant) {
            if (z1Tenant == AirlockZ1Tenant.CHIP) {
                announcement = "Peek is now available."
                // FIX-7 (Ton §5.3): the arm beat is REPORTED from
                // here, never sounded — the VM owns the engine
                // call and its once-per-visit nonce guard. On a
                // FIX-5c resolution the VM already fired it inside
                // refreshAirlockDoorState and marked the nonce, so
                // this report is a no-op there.
                onOfferArmed()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NightColorBackground)
                .padding(20.dp)
                .testTag("airlock_intercept_overlay"),
            // BOTTOM-ANCHORED (Tor round 8, superseding the
            // skeleton's §3.4 top-anchor): the thumb's frame is
            // screen space, so the card grows UPWARD into empty
            // night and RETURN's screen address is invariant
            // across states, branches (peek-owing or never-owed)
            // and visits — its centre sits 136dp above the card's
            // bottom edge (24 padding + 16 footer + 8 + 48 exit +
            // 12 + 28 half-slab) on every branch, because the slot
            // stack below RETURN never changes height. The stack
            // itself is untouched by the anchor.
            contentAlignment = Alignment.BottomCenter
        ) {
            // The TalkBack live region: visually absent, present
            // in the semantics tree, polite — it speaks only when
            // its text changes (door open, offer arm; see above).
            Text(
                text = announcement,
                fontSize = 1.sp,
                color = Color.Transparent,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .alpha(0f)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(NightColorSurfaceBright)
                    .border(1.dp, NightColorPrimary.copy(alpha = 0.38f), RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // H1 — NAME (32dp): the face's one hero. What he
                // opened, composed at the firing door (FIX-6 name
                // slot): the app's real name, the feed's name, the
                // browser's name, or what the guard caught him
                // opening — never a door label restated.
                Box(
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.interceptedAppName,
                        fontFamily = SoraFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = NightColorPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // H2 — INTENTION (24dp): his words BARE, directly
                // under the app name — the "ACTIVE INTENTION:"
                // prefix dies (FIX-6). Blank intention → the slot
                // stands empty and prints nothing (acquitted
                // silence; FIX-3e killed the draft fallback).
                Box(
                    modifier = Modifier.fillMaxWidth().height(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.sessionIntention.isNotBlank()) {
                        Text(
                            text = state.sessionIntention,
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 16.sp,
                            color = NightColorPrimary.copy(alpha = 0.92f),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // H3 — SESSION BLOCK (40dp, two 20dp lines).
                // Line 1: the session line (SIL-1 form acquitted;
                // FIX-2's span value). Line 2: the break
                // appointment on PEEK-tier doors (FIX-8 kernel),
                // or the site door's matched domain — the two
                // never contend (a site door owes no break line).
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        airlockSessionLine(state.sessionEndsAtMillis, state.sessionIsStopwatch)?.let { line ->
                            Text(
                                text = line,
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontSize = 12.5.sp,
                                color = NightColorPrimary.copy(alpha = 0.92f),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().height(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val subLine = state.nextBreakAtMillis?.let { breakAppointmentText(it) }
                            ?: state.nameSubLine
                        if (subLine != null) {
                            Text(
                                text = subLine,
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontSize = 12.5.sp,
                                color = NightColorPrimary.copy(alpha = 0.92f),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Z — THE PEEK ZONE (110dp peek-owing, absent
                // never-owed). Composed from frame one at full
                // height on every peek-owing door — offer AND
                // withheld alike (Tor's ratification of the
                // designer's deviation from Schliff's 34dp: the
                // FIX-5c resolution crossfades inside Z1, it never
                // grows the zone). Never-owed doors print no peek
                // line of any kind (Baseline law 4): the zone is
                // absent, not blanked.
                if (peekOwing) {
                    Column(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                        // Z1 — ACTION (48dp).
                        Box(
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Crossfade(
                                targetState = z1Tenant,
                                animationSpec = MonolithCurves.specA(180),
                                label = "airlock_z1_tenant"
                            ) { tenant ->
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (tenant) {
                                        AirlockZ1Tenant.GATE -> {
                                            // The gate stack: the wall
                                            // as a captioned print over
                                            // the draining rule — born
                                            // occupying the slot the
                                            // chip will inherit (P-1).
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = peekGateLineText(wallSecs),
                                                    fontFamily = AnonymousProFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = NightColorPrimary,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(2.dp)
                                                        .background(NightColorPrimary.copy(alpha = 0.20f))
                                                ) {
                                                    val fraction = if (state.wallTotalSeconds > 0) {
                                                        (wallRemainingMs.toFloat() /
                                                            (state.wallTotalSeconds * 1000f))
                                                            .coerceIn(0f, 1f)
                                                    } else {
                                                        0f
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth(fraction)
                                                            .height(2.dp)
                                                            .background(NightColorPrimaryContainer)
                                                    )
                                                }
                                            }
                                        }
                                        AirlockZ1Tenant.CHIP -> PeekHoldChip(
                                            badgeText = peekBadgeText(
                                                MonolithRepository.PEEK_DURATION_SECONDS
                                            ),
                                            nonce = state.interceptNonce,
                                            onTake = onTake2MinutePeek
                                        )
                                        AirlockZ1Tenant.WITHHELD -> {
                                            // The withheld lines print
                                            // from SECOND 0 — never
                                            // gated by any wall (FIX-6).
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                // P-5: a homecoming is
                                                // greeted as what it is
                                                // before its price line.
                                                if (state.justExpiredLease) {
                                                    Text(
                                                        text = "THE PEEK ENDED",
                                                        fontFamily = AtkinsonHyperlegibleFamily,
                                                        fontSize = 12.5.sp,
                                                        color = NightColorPrimary.copy(alpha = 0.92f),
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 1,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }
                                                when (state.peekWithheld) {
                                                    PeekWithheld.COOLDOWN -> if (cooldownSecs > 0) {
                                                        Text(
                                                            text = peekCooldownPrint(cooldownSecs),
                                                            fontFamily = AtkinsonHyperlegibleFamily,
                                                            fontSize = 12.5.sp,
                                                            color = NightColorPrimary.copy(alpha = 0.92f),
                                                            textAlign = TextAlign.Center,
                                                            maxLines = 1,
                                                            modifier = Modifier.fillMaxWidth()
                                                        )
                                                    }
                                                    PeekWithheld.ALREADY_ACTIVE -> Text(
                                                        text = "A PEEK IS ALREADY RUNNING",
                                                        fontFamily = AtkinsonHyperlegibleFamily,
                                                        fontSize = 12.5.sp,
                                                        color = NightColorPrimary.copy(alpha = 0.92f),
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 1,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                    PeekWithheld.PEEK_OFF -> Text(
                                                        text = "PEEK IS OFF",
                                                        fontFamily = AtkinsonHyperlegibleFamily,
                                                        fontSize = 12.5.sp,
                                                        color = NightColorPrimary.copy(alpha = 0.92f),
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 1,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                    null -> Unit
                                                }
                                            }
                                        }
                                        AirlockZ1Tenant.RESOLVING,
                                        AirlockZ1Tenant.EMPTY -> Unit
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Z2 — PRICE (34dp): the re-timed price
                        // sentence, standing from frame one on the
                        // offer branch — during counting as well
                        // as at offer, and through the hold (P-3a).
                        // Held branch: empty-held.
                        Box(
                            modifier = Modifier.fillMaxWidth().height(34.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedVisibility(
                                visible = state.canUse2MinutePeek,
                                enter = fadeIn(MonolithCurves.specA(180)),
                                exit = fadeOut(MonolithCurves.specA(180))
                            ) {
                                Text(
                                    text = peekPriceText(MonolithRepository.PEEK_COOLDOWN_MILLIS),
                                    fontFamily = AtkinsonHyperlegibleFamily,
                                    fontSize = 12.5.sp,
                                    color = NightColorPrimary.copy(alpha = 0.92f),
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Z3 — MEMORY (18dp): the taper's count in
                        // the gate line's family, or empty-held.
                        // A homecoming prints none — its ordinal
                        // would count the lease's own expiry as a
                        // reach (P-5).
                        Box(
                            modifier = Modifier.fillMaxWidth().height(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!state.justExpiredLease) {
                                doorMemoryLineText(state.priorInterceptsThisSession)?.let { line ->
                                    Text(
                                        text = line,
                                        style = PillBadgeTextStyle,
                                        color = NightColorPrimary.copy(alpha = 0.92f),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // R — RETURN (56dp slab): the face's only accent
                // fill, its label in the scheme's own
                // on-container ink #471602 (F-3, 5.37:1 — the
                // shared chip's forced cream is not inherited).
                // Live and tappable from frame one in every state;
                // never gated, never dimmed, never re-labelled.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NightColorPrimaryContainer)
                        .border(1.dp, NightColorPrimary.copy(alpha = 0.38f), RoundedCornerShape(12.dp))
                        .clickable(onClick = onReturnToFocus)
                        .testTag("airlock_return_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RETURN TO FOCUS",
                        style = PrimaryButtonTextStyle,
                        color = NightColorOnPrimaryContainer,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // X — THE HONEST EXIT (48dp touch row): one quiet
                // bare-text line under RETURN (FIX-8). It ends
                // nothing, spends nothing, books nothing — it
                // routes to the Disarm face, which carries the
                // gravity. Quiet by form, never by illegibility.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable(onClick = onEndSessionInstead)
                        .testTag("airlock_end_session_instead"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "END THE SESSION INSTEAD",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = NightColorPrimary.copy(alpha = 0.92f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // F — FOOTER BADGE (16dp): the badge word ALONE —
                // "AIRLOCK INTERCEPTION •" dies (FIX-6). On
                // peek-tier doors the footer prints PEEK alone
                // (Tor's ratification): the tier badge's literal
                // "2m Peek" is the same hardcoded-duration lie
                // FIX-8 cures on the chip, and never prints here.
                // K-1 (Kante, Tor round 5): the ink is
                // NightTextColorPrimary #D2C6BB ON THIS FACE ONLY —
                // the shared NightTextColorSecondary #9C8E84
                // measures 4.28:1 on this card and fails AA; the
                // shared token itself is NOT changed.
                Box(
                    modifier = Modifier.fillMaxWidth().height(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (peekOwing) {
                            "PEEK"
                        } else {
                            state.interceptReasonBadge.uppercase(Locale.US)
                        },
                        style = PillBadgeTextStyle,
                        color = NightTextColorPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/** Z1's tenants (designer §5): one slot, four lawful contents and
 *  the resolving instant between cooldown and offer. Tenancy is a
 *  crossfade inside fixed bounds — never a layout change. */
private enum class AirlockZ1Tenant { GATE, CHIP, WITHHELD, RESOLVING, EMPTY }

/**
 * The PEEK chip (innovator P-3a, adopted): taken by a 600ms
 * HOLD-TO-FILL, not a tap — a travelling thumb cannot spend a
 * lease. Press-and-hold fills the chip over 600ms; release early
 * drains the fill in 120ms and books nothing; reaching full
 * fires [onTake], whose grant-time re-derivation
 * (peekOfferFor, acquitted) remains the final word. RETURN is a
 * tap, PEEK is a hold, END is a 60-second valve: three acts,
 * three weights, each proportional to what the act spends.
 * Door-local by design — the shared BlockitActionChip stays
 * tap-only for every other face. TalkBack carries a custom
 * "Take the peek" action, so the gesture is never the only door.
 */
@Composable
private fun PeekHoldChip(
    badgeText: String,
    nonce: Long,
    onTake: () -> Unit
) {
    val fill = remember(nonce) { Animatable(0f) }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(NightColorSecondaryContainer)
            .border(1.dp, NightColorPrimary.copy(alpha = 0.38f), RoundedCornerShape(12.dp))
            .semantics {
                onClick(label = "Take the peek") {
                    onTake()
                    true
                }
            }
            .pointerInput(nonce) {
                detectTapGestures(
                    onPress = {
                        var fired = false
                        val fillJob = scope.launch {
                            fill.animateTo(
                                1f,
                                tween(durationMillis = 600, easing = LinearEasing)
                            )
                            if (fill.value >= 0.999f) {
                                fired = true
                                onTake()
                            }
                        }
                        tryAwaitRelease()
                        if (!fired) {
                            fillJob.cancel()
                            scope.launch {
                                fill.animateTo(
                                    0f,
                                    tween(durationMillis = 120, easing = LinearEasing)
                                )
                            }
                        }
                    }
                )
            }
            .testTag("airlock_peek_button"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fill.value)
                .fillMaxHeight()
                .background(NightColorPrimaryContainer.copy(alpha = 0.45f))
                .align(Alignment.CenterStart)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "PEEK",
                fontFamily = SoraFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NightColorPrimary
            )
            Text(
                text = " $badgeText",
                fontFamily = AnonymousProFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NightColorPrimary
            )
        }
    }
}

/**
 * 6. DISARM CHALLENGE OVERLAY (Unbreakable Lock + the weekly valve)
 *
 * Surface contract is TOKEN-SPEC §7 / Freya's buildspec point 2, one
 * job per element: title EMERGENCY BREAK · body = consequence (worded
 * by the count) · the valve = the mechanism · rule line UNDER the
 * valve = mechanics · chip KEEP FOCUSING. The count itself arrives as
 * [tokensRemaining], derived live from the repository's spend ledger
 * (Amendment A — never a snapshot written into the state at open).
 * Ledger-Direct (Freya, invention 1): the valve receives NO count —
 * at 0 remaining it is simply not composed, and this overlay renders
 * the SPENT face (buildspec point 8) in its place.
 */
@Composable
fun DisarmChallengeOverlay(
    state: DisarmChallengeState,
    tokensRemaining: Int,
    soundEngine: TactileSoundEngine? = null,
    onDismissKeepFocusing: () -> Unit,
    onCompleteDisarm: () -> Unit,
    onHoldStarted: () -> Unit = {},
    onCompleteCleanSession: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NightColorBackground.copy(alpha = 0.94f))
                .padding(16.dp)
                .testTag("disarm_challenge_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(NightColorSurfaceBright)
                    .border(1.dp, NightColorPrimary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "EMERGENCY BREAK",
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NightColorPrimary
                )

                Text(
                    text = when {
                        // P-2c: the door-originated face's lawful
                        // second body — a stopwatch past its floor
                        // completes, it does not break.
                        state.cleanCompletionAvailable ->
                            "This session has run its floor. Ending it now is a completion, not a break."
                        tokensRemaining > 0 ->
                            "This ends the session early. It is the only break this week."
                        else ->
                            "This week's break is already spent."
                    },
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 13.sp,
                    color = NightColorPrimary
                )

                if (state.cleanCompletionAvailable) {
                    // The completion's verb is a deliberate slab
                    // tap standing between the door's footer line
                    // and the act — never the footer's own tap
                    // (P-2c). No valve, no token: nothing is spent
                    // by finishing what already ran its floor.
                    BlockitActionChip(
                        wordLabel = "END THE SESSION",
                        isPrimaryInk = true,
                        onClick = onCompleteCleanSession,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "disarm_end_session_button"
                    )
                } else if (tokensRemaining > 0) {
                    AndroidView(
                        factory = { ctx ->
                            Emergency60sValveView(ctx).apply {
                                isNightMode = true
                                this.soundEngine = soundEngine
                            }
                        },
                        update = { view ->
                            view.requiredSeconds = state.requiredHoldSeconds
                            view.onEmergencyBreakCompleted = onCompleteDisarm
                            view.onHoldStarted = onHoldStarted
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .testTag("emergency_60s_valve")
                    )

                    Text(
                        text = "Hold to fill 60 seconds. Every 15 seconds banks. If your hand slips, what isn't banked drains away. The banked part stays.",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 11.sp,
                        color = NightColorPrimary.copy(alpha = 0.75f)
                    )
                }

                BlockitActionChip(
                    wordLabel = "KEEP FOCUSING",
                    isPrimaryInk = true,
                    onClick = onDismissKeepFocusing,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "keep_focusing_button"
                )
            }
        }
    }
}

