package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.CapturedNotificationEntity
import com.example.data.DistractionTriggerReason
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
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.views.Emergency60sValveView
import kotlinx.coroutines.delay
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
        //
        // Issue #111 finding 1: KEEP_SCREEN_ON alone dies at the first
        // power press / interruption that lands on the keyguard — the
        // clock had NO lock-screen presence. While (and only while)
        // this overlay is composed, the activity is also show-when-
        // locked + turn-screen-on, so the clock itself stands over the
        // keyguard and an interruption returns to the clock. Scoped
        // deliberately: a static manifest/activity-wide showWhenLocked
        // would ALSO float the Deck over the keyguard after the clock
        // exits — the exact exposure finding 5 names. On dispose both
        // grants are revoked before the Deck can inherit them.
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
                activity?.setShowWhenLocked(true)
                activity?.setTurnScreenOn(true)
            } else {
                @Suppress("DEPRECATION")
                window?.addFlags(
                    android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                )
            }
            onDispose {
                window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
                    activity?.setShowWhenLocked(false)
                    activity?.setTurnScreenOn(false)
                } else {
                    @Suppress("DEPRECATION")
                    window?.clearFlags(
                        android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                            android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    )
                }
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

        // Issue #111 findings 3+4: the glance now NAMES what the
        // number is (REMAINING vs ELAPSED — the two directions were
        // indistinguishable), states the time of day and the
        // session's end, and announces a Safe Break on THIS surface.
        // The span number itself still never renders AS a break
        // (the span law at MonolithModels stands: during a break the
        // span keeps counting) — but the Deck's BREAK eyebrow, the
        // break's other rendering, is COVERED by this full-screen
        // overlay, so a user living on the clock learned nothing.
        // The break line below is an announcement, not a second
        // derivation: it reads isInSafeBreak + the break's own
        // remaining seconds off the same row. The fabricated 0.65
        // idle constant stays dead; every print is derived.
        val progress = deskClockProgress(activeSession)
        val modeLabel = deskClockModeLabel(activeSession)
        val breakLine = deskClockBreakLine(activeSession)
        // Time of day: read at composition. During a session the
        // row ticks every second; idle, the burn-in step still
        // steps every minute — the HH:mm print can never ride
        // more than a minute stale.
        val glanceLine = deskClockGlanceLine(System.currentTimeMillis(), activeSession)

        // Issue #111 finding 2: a real figure-8 (lemniscate) wander
        // in dp — see deskClockBurnInOffsetDp. The old 5x5 raster
        // moved the 96sp numerals ±6 PIXELS, which is no wander at
        // all on the pixels that matter.
        val (burnDx, burnDy) = deskClockBurnInOffsetDp(burnInStep)

        Box(
            modifier = Modifier
                .fillMaxSize()
                // AOD ground is true black (Round 1a) — OLED pixels off,
                // not the block-bar tone.
                .background(Color.Black)
                // Issue #111 finding 5: a stray TAP dismisses
                // nothing. Exit is a deliberate long-press anywhere —
                // a desk bump, a sleeve, a child's tap can neither
                // kill the clock (and its KEEP_SCREEN_ON with it) nor
                // land on the operative Deck. The header prints the
                // gesture so the door is discoverable, not hidden.
                .pointerInput(Unit) {
                    detectTapGestures(onLongPress = { onDismiss() })
                }
                .padding(24.dp)
                .testTag("always_on_desk_clock_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = burnDx.dp, y = burnDy.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "NIGHTSTAND DESK CLOCK • HOLD ANYWHERE TO EXIT",
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = NightColorPrimary.copy(alpha = 0.6f)
                )

                Text(
                    text = glanceLine,
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NightColorPrimary.copy(alpha = 0.75f),
                    modifier = Modifier.testTag("desk_clock_glance_line")
                )

                // The number's name: without it a countdown and a
                // count-up were the same bare digits from across a desk.
                Text(
                    text = modeLabel,
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = NightColorPrimary.copy(alpha = 0.75f),
                    modifier = Modifier.testTag("desk_clock_mode_label")
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

                // The Safe Break announcement (finding 3): while a
                // break runs, this line stands where the task banner
                // would — the user on the clock is told to step away
                // and how much break is left, instead of watching
                // their task title roll on and spending the break
                // at the desk unknowing.
                if (breakLine != null) {
                    Text(
                        text = breakLine,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NightColorPrimary,
                        modifier = Modifier.testTag("desk_clock_break_line")
                    )
                }

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
                // During a Safe Break the break line above replaces
                // the banner: printing the task's name through the
                // break was exactly how the clock kept him working.
                if (bannerText != null && breakLine == null) {
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
 * Issue #111 finding 2 — the burn-in wander, as a pure function.
 * A lemniscate (figure-8): x = 26·sin θ, y = 13·sin 2θ, θ advancing
 * 2π per 60 steps; the VM steps once a minute, so the face traces
 * one full figure-8 per hour, moving several dp every minute and
 * reaching ±26dp horizontally / ±13dp vertically. The old raster
 * was ±6 raw PIXELS on a 5x5 grid — the 96sp numerals effectively
 * never left their pixels. Returns (dxDp, dyDp).
 */
internal fun deskClockBurnInOffsetDp(step: Int): Pair<Float, Float> {
    val theta = ((step % 60) + 60) % 60 * (2.0 * Math.PI / 60.0)
    val dx = 26.0 * kotlin.math.sin(theta)
    val dy = 13.0 * kotlin.math.sin(2.0 * theta)
    return dx.toFloat() to dy.toFloat()
}

/**
 * Issue #111 finding 4 — the hero number's name. A timed session
 * counts DOWN (REMAINING), a stopwatch counts UP (ELAPSED), and
 * with no live session the dial's own setting is what stands
 * (SET TIME). The bare digits never carry the direction again.
 */
internal fun deskClockModeLabel(session: FocusSessionEntity?): String = when {
    session != null && session.isActive && session.isStopwatchMode -> "ELAPSED"
    session != null && session.isActive -> "REMAINING"
    else -> "SET TIME"
}

/**
 * Issue #111 finding 4 — the glance line: day + time of day, and
 * for a timed session its end (start + the whole span, the model's
 * own derivation — never now + remaining, which would slide); a
 * stopwatch honestly states it has no set end; no session, no end
 * is invented.
 */
internal fun deskClockGlanceLine(nowMillis: Long, session: FocusSessionEntity?): String {
    val dayTime = java.text.SimpleDateFormat("EEE d MMM • HH:mm", Locale.US)
        .format(java.util.Date(nowMillis)).uppercase(Locale.US)
    return when {
        session != null && session.isActive && !session.isStopwatchMode ->
            dayTime + " • " + endsAtText(
                session.startWallTimeMillis + session.spanTargetSeconds * 1000L
            )
        session != null && session.isActive -> "$dayTime • NO SET END"
        else -> dayTime
    }
}

/**
 * Issue #111 finding 3 — the Safe Break announcement. Non-null
 * ONLY while the row says a break is actually running; the count
 * is the break's OWN remaining seconds (safeBreakSecondsRemaining),
 * never the span's. Outside a break this surface prints nothing
 * about breaks — no standing decoration, no false alarm.
 */
internal fun deskClockBreakLine(session: FocusSessionEntity?): String? {
    if (session == null || !session.isActive || !session.isInSafeBreak) return null
    val left = session.safeBreakSecondsRemaining.coerceAtLeast(0)
    return String.format(
        Locale.US, "SAFE BREAK • %02d:%02d LEFT — STEP AWAY", left / 60, left % 60
    )
}

/**
 * Issue #111 finding 4 — the progress strip's one derivation.
 * Timed: the span fraction, floored at 0.04 so a live session
 * never reads empty. Stopwatch: there is NO target, so a fill-to-
 * full bar was a lie that pinned at "finished" after 20 minutes —
 * the bar now cycles within each 20-minute lap and can only touch
 * full at the instant a lap rolls over. Idle: honestly empty.
 */
internal fun deskClockProgress(session: FocusSessionEntity?): Float = when {
    session != null && session.isActive && !session.isStopwatchMode ->
        (session.spanElapsedSeconds.toFloat() / session.spanTargetSeconds.coerceAtLeast(1))
            .coerceIn(0.04f, 1f)
    session != null && session.isActive ->
        (session.elapsedSeconds % 1200).toFloat() / 1200f
    else -> 0f
}

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
 * The peek chip's badge (SIL-2, Hale W-29): the SET value, read
 * from the same constant the lease machinery reads — never a
 * hardcoded "2m" on the surface. (Rurik's editable store replaces
 * the constant as the value's source; this function is the one
 * print derivation that changes.)
 */
internal fun peekBadgeText(durationSeconds: Int): String = "${durationSeconds / 60}m"

/**
 * The peek's price, printed on the offer before it is taken
 * (SIL-2, Hale W-29): the cooldown named in full, derived from
 * the cooldown the repository will actually arm — never a
 * hardcoded 20 on the surface.
 */
internal fun peekPriceText(cooldownMillis: Long): String {
    val minutes = cooldownMillis / 60_000L
    val unit = if (minutes == 1L) "MINUTE" else "MINUTES"
    return "TAKING THE PEEK STARTS A $minutes $unit COOLDOWN ON THIS APP"
}

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
 * 5. AIRLOCK INTERCEPTION OVERLAY (Decision #16: 5s Breathing Delay Wall)
 */
@Composable
fun AirlockInterceptOverlay(
    state: AirlockInterceptState,
    onSelectReason: (DistractionTriggerReason) -> Unit,
    onTransmuteParticleTap: () -> Unit,
    onReturnToFocus: () -> Unit,
    onTake2MinutePeek: () -> Unit,
    // The launcher doors (Sarthak's ruling, Oct 30 04:29 IST): the
    // block screen is a launcher — from the door, the sealed phone's
    // allowed and peek apps live one plate away, each on its own
    // screen. Counts print from the rows' own tiers.
    allowedAppsCount: Int = 0,
    peekAppsCount: Int = 0,
    onOpenAllowedApps: () -> Unit = {},
    onOpenPeekApps: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn(MonolithCurves.specB()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        var countdownSecs by remember(state.interceptedPackageName, state.isVisible) {
            mutableIntStateOf(5)
        }
        LaunchedEffect(state.isVisible, state.interceptedPackageName) {
            countdownSecs = 5
            while (countdownSecs > 0) {
                delay(1000L)
                countdownSecs--
            }
        }

        // The withheld peek's cooldown, ticking live while the
        // airlock stands (Truth Car 3, UNA2-01): seeded from the
        // state's snapshot at open, counted down here — the print
        // never quotes a number minutes stale.
        var cooldownSecs by remember(state.interceptedPackageName, state.isVisible) {
            mutableIntStateOf(state.peekCooldownRemainingSeconds)
        }
        LaunchedEffect(state.isVisible, state.interceptedPackageName) {
            cooldownSecs = state.peekCooldownRemainingSeconds
            while (cooldownSecs > 0) {
                delay(1000L)
                cooldownSecs--
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NightColorBackground)
                .padding(20.dp)
                .testTag("airlock_intercept_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(NightColorSurfaceBright)
                    .border(1.dp, NightColorPrimary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "AIRLOCK INTERCEPTION • ${state.interceptReasonBadge.uppercase(Locale.US)}",
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = NightColorPrimary
                )

                Text(
                    text = state.interceptedAppName,
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = NightColorPrimary
                )

                // SIL-1 / Hale W-28: under the app name, the door
                // states the session's end — its own statement,
                // from the session's own numbers.
                airlockSessionLine(state.sessionEndsAtMillis, state.sessionIsStopwatch)?.let { line ->
                    Text(
                        text = line,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 12.5.sp,
                        color = NightColorPrimary.copy(alpha = 0.8f)
                    )
                }

                // Hero Architectural Countdown Numeral (Space Mono)
                Text(
                    text = if (countdownSecs > 0) String.format(Locale.US, "0%d", countdownSecs) else "00",
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 88.sp,
                    color = NightColorPrimary // a readout numeral, not an accent (Round 1c)
                )

                // UNA2-07: the label prints only over a declared
                // intention — never "ACTIVE INTENTION: " standing
                // over a blank.
                if (state.sessionIntention.isNotBlank()) {
                    Text(
                        text = "ACTIVE INTENTION: ${state.sessionIntention}",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 12.5.sp,
                        color = NightColorPrimary.copy(alpha = 0.8f)
                    )
                }

                BlockitActionChip(
                    wordLabel = "RETURN TO FOCUS",
                    isAccentArmed = true,
                    onClick = onReturnToFocus,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "airlock_return_button"
                )

                if (countdownSecs == 0 && state.canUse2MinutePeek) {
                    BlockitActionChip(
                        wordLabel = "EMERGENCY PEEK",
                        // SIL-2 / W-29: the badge is the SET value,
                        // read from the lease machinery's own
                        // constant — no hardcoded minutes here.
                        monoBadge = peekBadgeText(MonolithRepository.PEEK_DURATION_SECONDS),
                        onClick = onTake2MinutePeek,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "airlock_peek_button"
                    )
                    // SIL-2 / W-29: the price stands on the offer,
                    // before the peek is taken — the cooldown the
                    // repository will arm, named in full.
                    Text(
                        text = peekPriceText(MonolithRepository.PEEK_COOLDOWN_MILLIS),
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 12.5.sp,
                        color = NightColorPrimary.copy(alpha = 0.8f)
                    )
                }

                // The withheld peek prints its state AND its reason
                // (Held Door Truth Baseline): cooldown with its
                // live remaining time, or the running pill named.
                // Doors that never owed a peek carry withheld ==
                // null and print nothing here.
                if (countdownSecs == 0 && !state.canUse2MinutePeek) {
                    when (state.peekWithheld) {
                        PeekWithheld.COOLDOWN -> Text(
                            text = peekCooldownPrint(cooldownSecs),
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 12.5.sp,
                            color = NightColorPrimary.copy(alpha = 0.8f)
                        )
                        PeekWithheld.ALREADY_ACTIVE -> Text(
                            text = "A PEEK IS ALREADY RUNNING",
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 12.5.sp,
                            color = NightColorPrimary.copy(alpha = 0.8f)
                        )
                        PeekWithheld.PEEK_OFF -> Text(
                            text = "PEEK IS OFF",
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 12.5.sp,
                            color = NightColorPrimary.copy(alpha = 0.8f)
                        )
                        null -> Unit
                    }
                }

                // THE LAUNCHER DOORS: two plates, two screens —
                // allowed apps on one, peek apps on the other.
                // They open rooms inside the seal; neither is an
                // exit, and neither grants anything by itself.
                BlockitActionChip(
                    wordLabel = "ALLOWED APPS",
                    monoBadge = allowedAppsCount.toString(),
                    onClick = onOpenAllowedApps,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "airlock_allowed_apps_button"
                )

                BlockitActionChip(
                    wordLabel = "PEEK APPS",
                    monoBadge = peekAppsCount.toString(),
                    onClick = onOpenPeekApps,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "airlock_peek_apps_button"
                )
            }
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
    onHoldStarted: () -> Unit = {}
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
                    text = if (tokensRemaining > 0) {
                        "This ends the session early. It is the only break this week."
                    } else {
                        "This week's break is already spent."
                    },
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 13.sp,
                    color = NightColorPrimary
                )

                if (tokensRemaining > 0) {
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

