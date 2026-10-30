package com.example.ui.components

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.data.ClockText
import androidx.compose.ui.platform.LocalContext
import com.example.data.DistractionTriggerReason
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.service.InterceptPath
import com.example.service.TactileSoundEngine
import com.example.data.MonolithRepository
import com.example.data.MonolithWeek
import com.example.ui.AirlockInterceptState
import com.example.ui.DisarmChallengeState
import com.example.ui.PeekWithheld
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.NightBlockBarSurface
import com.example.ui.theme.TokenTrueBlack
import com.example.ui.theme.NightColorBackground
import com.example.ui.theme.NightColorPrimary
import com.example.ui.theme.NightColorPrimaryContainer
import com.example.ui.theme.NightColorSurfaceBright
import com.example.ui.theme.NightColorSurfaceVariant
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.views.Emergency60sValveView
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
            // During a Safe Break the hero IS the break clock
            // (Audit R3, safe-break-lived #5): the desk clock is a
            // kept study display — elapsed + remaining is its job —
            // and a user who props the phone up as their break
            // clock must be able to tell break from work and spot
            // its end on THIS screen. Outside a break, timed
            // sessions read the SPAN remaining, never the per-block
            // remaining: the block's remainder is 0 for the whole
            // of a Safe Break — it would read 00:00 (FINISH)
            // through every break. The span counts the break as
            // session time.
            when {
                activeSession.isInSafeBreak -> activeSession.safeBreakSecondsRemaining
                activeSession.isStopwatchMode -> activeSession.elapsedSeconds
                else -> activeSession.spanRemainingSeconds
            }
        } else {
            dialMinutes * 60
        }
        val mm = displaySecs / 60
        val ss = displaySecs % 60
        // Audit R3b #147: a stopwatch clock counts UP — the bare
        // MM:SS was indistinguishable from a countdown at a glance.
        // The + prefix is the direction marker, here and on the
        // Deck's hero readout alike.
        val timeText = if (activeSession != null && activeSession.isActive &&
            activeSession.isStopwatchMode
        ) {
            String.format(Locale.US, "+%02d:%02d", mm, ss)
        } else {
            String.format(Locale.US, "%02d:%02d", mm, ss)
        }

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
        val glanceLine = deskClockGlanceLine(ClockText.is24Hour(LocalContext.current), System.currentTimeMillis(), activeSession)

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
                .background(TokenTrueBlack)
                // Issue #111 finding 5: a stray TAP dismisses
                // nothing. Exit is a deliberate long-press anywhere —
                // a desk bump, a sleeve, a child's tap can neither
                // kill the clock (and its KEEP_SCREEN_ON with it) nor
                // land on the operative Deck. The header prints the
                // gesture so the door is discoverable, not hidden.
                // (Merge vs main: TokenTrueBlack kept — main's
                // tokenised true black supersedes the branch's
                // Color.Black; tap-dismiss does NOT return.)
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
 * The desk clock's break line (Audit R3, safe-break-lived #5):
 * during a Safe Break the clock names the break and the block
 * that resumes when its countdown (the hero, in break state)
 * hits zero; outside a break there is no line — the clock never
 * prints a break that is not running. [nextBlockIndex] is the
 * 1-based block the break hands over to, clamped to the session's
 * own block count so the line can never promise a block the
 * session does not have.
 */
internal fun deskClockBreakLine(
    isInSafeBreak: Boolean,
    nextBlockIndex: Int,
    totalBlocks: Int
): String? {
    if (!isInSafeBreak) return null
    val next = nextBlockIndex.coerceIn(1, totalBlocks.coerceAtLeast(1))
    return "SAFE BREAK · BLOCK $next RESUMES AT 00:00"
}

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
internal fun deskClockGlanceLine(is24Hour: Boolean, nowMillis: Long, session: FocusSessionEntity?): String {
    // #88: the day names follow the device locale and the time the
    // system 12/24-hour setting (ClockText's pure core) — this line
    // landed via #111 with a pinned Locale.US "HH:mm".
    val dayTime = java.text.SimpleDateFormat("EEE d MMM", Locale.getDefault())
        .format(java.util.Date(nowMillis)).uppercase(Locale.getDefault()) +
        " • " + ClockText.timeOfDay(is24Hour, nowMillis)
    return when {
        session != null && session.isActive && !session.isStopwatchMode ->
            dayTime + " • " + endsAtText(
                is24Hour,
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
    // Issue #55: an expired cooldown never prints "AGAIN IN 0 MIN" —
    // that self-contradictory line was the cooldown trap's dead end.
    if (secondsRemaining <= 0) return "PEEK COOLDOWN — OVER"
    val minutes = (secondsRemaining + 59) / 60
    return "PEEK COOLDOWN — AGAIN IN $minutes MIN"
}

/**
 * Issue #55: the 5-second wall is DEADLINE-based, not tick-counted.
 * Remaining seconds are always derived from the intercept's start
 * instant, so scheduling drift can never stretch or shrink the wall,
 * and a re-intercept (new instance) restarts from the full wall.
 */
internal fun airlockCountdownRemaining(
    startedAtMillis: Long,
    nowMillis: Long,
    wallSeconds: Int = 5
): Int {
    val elapsedSeconds = ((nowMillis - startedAtMillis).coerceAtLeast(0L) / 1000L).toInt()
    return (wallSeconds - elapsedSeconds).coerceIn(0, wallSeconds)
}

/** Issue #55: whole seconds until [deadlineMillis], ceiling — a
 *  deadline-derived remainder that reaches 0 exactly at the deadline. */
internal fun cooldownRemainingSeconds(deadlineMillis: Long, nowMillis: Long): Int {
    val remainingMillis = deadlineMillis - nowMillis
    if (remainingMillis <= 0L) return 0
    return ((remainingMillis + 999L) / 1000L).toInt()
}

/**
 * Issue #55: the peek unlocks the moment its ONLY blocker — the
 * cooldown — has actually expired, even though the standing state's
 * snapshot still says withheld. Any other withheld reason
 * (a peek already running, peek off) never unlocks locally; the
 * ViewModel's grant re-derives the offer from the repository
 * regardless, so this local unlock can never sell a stale lease.
 */
internal fun peekUnlocked(
    canUse2MinutePeek: Boolean,
    withheld: PeekWithheld?,
    cooldownRemainingSeconds: Int
): Boolean =
    canUse2MinutePeek ||
        (withheld == PeekWithheld.COOLDOWN && cooldownRemainingSeconds <= 0)

/**
 * The door's own statement of the session's end (SIL-1, Hale
 * W-28): a timed session states its span end — "SESSION " over
 * [endsAtText]'s instant; a stopwatch states that it has no set
 * end; no session, no line. The door never invents an end.
 */
internal fun airlockSessionLine(
    is24Hour: Boolean,
    endsAtMillis: Long?,
    isStopwatch: Boolean,
    stopwatchFloorAtMillis: Long? = null
): String? = when {
    endsAtMillis != null -> "SESSION " + endsAtText(is24Hour, endsAtMillis)
    // Audit R3b #147: "NO SET END" was true and useless — a
    // stopwatch session's one scheduled fact is its floor moment,
    // when stopping becomes free. State it; once it has passed,
    // state that the clean exit stands open.
    isStopwatch && stopwatchFloorAtMillis != null &&
        stopwatchFloorAtMillis > System.currentTimeMillis() ->
        "SESSION RUNNING · CLEAN EXIT FROM " +
            ClockText.timeOfDay(is24Hour, stopwatchFloorAtMillis)
    isStopwatch && stopwatchFloorAtMillis != null ->
        "SESSION RUNNING · CLEAN EXIT OPEN"
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
    return "WHEN THE PEEK ENDS, A $minutes $unit COOLDOWN RUNS ON THIS APP"
}

/** The capture's own time, for a frozen row: HH:mm when the
 *  capture is from today, d MMM · HH:mm when it is not — yesterday's
 *  held rows legitimately sit in this morning's snapshot, and the
 *  AS OF stamp dates the snapshot, not the rows. */
private fun captureTimeLabel(is24Hour: Boolean, timestampMillis: Long): String {
    val zone = java.time.ZoneId.systemDefault()
    val captureDate = java.time.Instant.ofEpochMilli(timestampMillis).atZone(zone).toLocalDate()
    // #88: the time half follows the system 12/24-hour setting; the
    // day half names the day in the device locale when it is not today.
    return if (captureDate == java.time.LocalDate.now(zone)) {
        ClockText.timeOfDay(is24Hour, timestampMillis)
    } else {
        ClockText.dayAndTime(is24Hour, timestampMillis)
    }
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
    // CLEAR ALL is the one irreversible mass action in this sheet:
    // it arms on the first tap and fires only on the second
    // (issue #138). The arm resets whenever the sheet hides.
    var clearAllArmed by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (!visible) clearAllArmed = false
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        val scheme = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scheme.scrim.copy(alpha = 0.65f))
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
                                    monoNumber = ClockText.timeOfDay(LocalContext.current, frozenAtMillis),
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
                            wordLabel = if (clearAllArmed) "TAP AGAIN TO CLEAR" else "CLEAR ALL",
                            onClick = {
                                if (clearAllArmed) {
                                    clearAllArmed = false
                                    onClearAll()
                                } else {
                                    clearAllArmed = true
                                }
                            }
                        )
                    }
                }
                if (frozenMode && frozenAtMillis != null) {
                    Text(
                        text = "Everything held so far, as of the time above — this list refreshes as new items are held. Nothing here opens.",
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
                                        // Both modes print the capture
                                        // time (issue #138): a live row
                                        // without its time hides whether
                                        // it is fresh or hours old.
                                        text = "${item.appName} • ${item.senderTitle} • ${captureTimeLabel(ClockText.is24Hour(LocalContext.current), item.timestampMillis)}",
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
    onOpenPeekApps: () -> Unit = {},
    // Issue #119, finding 2: the emergency exit stands where the
    // emergency happens. This chip opens the SAME DisarmChallenge
    // overlay the Deck chip opens — identical cost (the week's one
    // token), identical mechanism (the continuous 60s hold),
    // identical effect (the session ends). Placement only; the
    // valve's price and power are untouched.
    onRequestEmergencyBreak: () -> Unit = {},
    // Issue #318 F4: the breathing wall's length is a stored,
    // per-configuration value (AirlockBreathStore), no longer a
    // composable-local literal. Default = Decision #16's 5 seconds.
    breathSeconds: Int = com.example.data.AirlockBreathStore.DEFAULT_SECONDS
) {
    // Issue #55 (HIGH): System Back must never bypass the wall toward
    // the blocked app. While the airlock stands, Back is swallowed —
    // the only exits are the composed RETURN TO FOCUS / peek actions.
    BackHandler(enabled = state.isVisible) { }
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn(MonolithCurves.specB()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        // Issue #55 (MEDIUM): the wall is PER-INTERCEPT. Both timers are
        // keyed on the intercept INSTANCE (not package+visibility, which
        // a same-package re-intercept left unchanged) and are
        // deadline-derived, so a fresh intercept always restarts the
        // full 5 seconds and drift can never stretch the wall.
        val wallStartMono = remember(state.interceptInstanceId) {
            state.interceptedAtMonoMillis.takeIf { it > 0L } ?: SystemClock.elapsedRealtime()
        }
        var countdownSecs by remember(state.interceptInstanceId) {
            mutableIntStateOf(
                airlockCountdownRemaining(wallStartMono, SystemClock.elapsedRealtime(), breathSeconds)
            )
        }
        LaunchedEffect(state.interceptInstanceId, state.isVisible) {
            countdownSecs =
                airlockCountdownRemaining(wallStartMono, SystemClock.elapsedRealtime(), breathSeconds)
            while (countdownSecs > 0) {
                delay(200L)
                countdownSecs =
                    airlockCountdownRemaining(wallStartMono, SystemClock.elapsedRealtime(), breathSeconds)
            }
        }

        // The withheld peek's cooldown, ticking live while the
        // airlock stands (Truth Car 3, UNA2-01) — now derived from
        // the state's absolute MONOTONIC deadline (Issue #55,
        // verifier: the repository's own anchor + 20 min, ceiling
        // math mirroring peekSecondsRemaining), so the on-screen
        // unlock instant is never before the repository's grant
        // instant. When it reaches zero the peek genuinely unlocks
        // below (see peekUnlocked), instead of stranding the user
        // on a frozen canUse flag at "0 MIN". States built without
        // a deadline (tests, previews) fall back to the snapshot.
        val cooldownDeadlineMono = remember(state.interceptInstanceId) {
            state.peekCooldownEndsAtMonoMillis.takeIf { it > 0L }
                ?: (SystemClock.elapsedRealtime() + state.peekCooldownRemainingSeconds * 1000L)
        }
        var cooldownSecs by remember(state.interceptInstanceId) {
            mutableIntStateOf(state.peekCooldownRemainingSeconds)
        }
        LaunchedEffect(state.interceptInstanceId, state.isVisible) {
            cooldownSecs = cooldownRemainingSeconds(cooldownDeadlineMono, SystemClock.elapsedRealtime())
            while (cooldownSecs > 0) {
                delay(200L)
                cooldownSecs = cooldownRemainingSeconds(cooldownDeadlineMono, SystemClock.elapsedRealtime())
            }
        }
        val peekReady = peekUnlocked(state.canUse2MinutePeek, state.peekWithheld, cooldownSecs)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NightColorBackground)
                // Issue #55 (CRITICAL): the wall is modal — the root
                // consumes every tap in the empty area around the card
                // (same guard the digest sheet and primer already use),
                // so nothing falls through to the UI underneath.
                .clickable(enabled = false) {}
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
                    .clickable(enabled = false) {}
                    // Issue #55 (fresh-eyes): the card SCROLLS — with
                    // the reason rows the content can outgrow short /
                    // landscape / large-font screens, and Back is
                    // deliberately swallowed, so an unscrollable card
                    // would trap its own exits off-screen.
                    .verticalScroll(rememberScrollState())
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
                airlockSessionLine(
                    ClockText.is24Hour(LocalContext.current),
                    state.sessionEndsAtMillis,
                    state.sessionIsStopwatch,
                    state.stopwatchFloorAtMillis
                )?.let { line ->
                    Text(
                        text = line,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 12.5.sp,
                        color = NightColorPrimary.copy(alpha = 0.8f)
                    )
                }

                // Hero Architectural Countdown Numeral (Space Mono)
                // Issue #55: the transmute pulse is WIRED, not dead —
                // tapping the numeral during the wall fires it (the
                // ViewModel answers with a pitch-rising detent and
                // counts the pulse). It grants nothing and shortens
                // nothing; it is the wall's one permitted fidget.
                Text(
                    text = if (countdownSecs > 0) String.format(Locale.US, "0%d", countdownSecs) else "00",
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 88.sp,
                    color = NightColorPrimary, // a readout numeral, not an accent (Round 1c)
                    modifier = Modifier
                        .clickable { onTransmuteParticleTap() }
                        .testTag("airlock_countdown_numeral")
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

                // Issue #55 (MEDIUM): reason selection is REAL wiring,
                // not a dead parameter. The user picks why they opened
                // the app; the pick is stored via onSelectReason and is
                // the triggerReason actually recorded on return/peek —
                // no longer a silently fabricated MUSCLE_MEMORY default
                // (it remains only the visibly pre-selected first chip).
                Text(
                    text = "WHY DID YOU OPEN IT?",
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = NightColorPrimary.copy(alpha = 0.8f)
                )
                DistractionTriggerReason.values().toList().chunked(2).forEach { rowReasons ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowReasons.forEach { reason ->
                            BlockitActionChip(
                                wordLabel = reason.shortTag.uppercase(Locale.US),
                                isPrimaryInk = state.selectedReason == reason,
                                onClick = { onSelectReason(reason) },
                                modifier = Modifier.weight(1f),
                                testTag = "airlock_reason_${reason.name.lowercase(Locale.US)}"
                            )
                        }
                    }
                }

                // Issue #99 F5: for LOCKED-tier apps the wall now
                // gates something real — RETURN TO FOCUS itself.
                // Before this the chip was live from second 5 and
                // the countdown opened nothing at 00 for LOCKED
                // apps. The delay IS the wall for the locked tier;
                // PEEK-tier behaviour is unchanged (RETURN was and
                // stays live, the peek offer is what its wall gates).
                val returnLive = returnToFocusLive(state.isLockedTier, countdownSecs)
                // R3b #162 F4: the Uninstall Guard door can honour no
                // peek (InterceptPath.UNINSTALL_GUARD), so beside its
                // single RETURN offer it prints the lawful road out —
                // the same road the device-admin warning prescribes:
                // end the block first; the emergency break is the door
                // the app itself sanctions. A signpost to an existing
                // valve, not a new exit.
                if (state.interceptPath == InterceptPath.UNINSTALL_GUARD) {
                    Text(
                        text = "TO REMOVE MONOLITH: END THE BLOCK FIRST — THE EMERGENCY BREAK ON THE DECK IS THE LAWFUL DOOR.",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 12.5.sp,
                        color = NightColorPrimary.copy(alpha = 0.8f)
                    )
                }
                BlockitActionChip(
                    wordLabel = "RETURN TO FOCUS",
                    isAccentArmed = true,
                    onClick = { if (returnLive) onReturnToFocus() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (returnLive) Modifier else Modifier.alpha(0.45f)),
                    testTag = "airlock_return_button"
                )

                if (countdownSecs == 0 && peekReady) {
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
                if (countdownSecs == 0 && !peekReady) {
                    when (state.peekWithheld) {
                        PeekWithheld.COOLDOWN -> {
                            if (cooldownSecs > 0) {
                                Text(
                                    text = peekCooldownPrint(cooldownSecs),
                                    fontFamily = AtkinsonHyperlegibleFamily,
                                    fontSize = 12.5.sp,
                                    color = NightColorPrimary.copy(alpha = 0.8f)
                                )
                            }
                        }
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

                // THE VALVE, AT THE DOOR (issue #119, finding 2):
                // one tap from the intercept to the same emergency
                // break the Deck offers — no multi-screen detour
                // through the Deck while the emergency is happening
                // here. Always rendered: an emergency does not wait
                // for the 5-second wall.
                BlockitActionChip(
                    wordLabel = "EMERGENCY BREAK VALVE (60S HOLD)",
                    onClick = onRequestEmergencyBreak,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "airlock_emergency_break_button"
                )

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

/** Issue #99 F5 (pure, unit-tested): RETURN TO FOCUS is live
 *  immediately for non-LOCKED tiers, and only once the 5s wall has
 *  run for LOCKED-tier apps — the wall gates the return itself. */
internal fun returnToFocusLive(isLockedTier: Boolean, countdownSecs: Int): Boolean =
    !isLockedTier || countdownSecs <= 0

/** Issue #99 F2 (pure, unit-tested): the pill's countdown print. */
internal fun peekPillCountdownText(secondsRemaining: Int): String {
    val s = secondsRemaining.coerceAtLeast(0)
    return String.format(Locale.US, "%d:%02d", s / 60, s % 60)
}

/**
 * Issue #99 F2: THE PEEK COUNTDOWN PILL — the canonical rendering of
 * [com.example.ui.FloatingPeekPillState] (this cures #101-F3 by
 * cross-reference: one pill, rendered here, collected in
 * MainActivity). App name, remaining time derived from the lease,
 * and a ✕ that ends the peek early via end2MinutePeekEarly.
 */
@Composable
fun PeekCountdownPill(
    state: com.example.ui.FloatingPeekPillState,
    onEndEarly: () -> Unit
) {
    if (!state.isActive) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(NightColorSurfaceBright)
                .border(1.dp, NightColorPrimary.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("peek_countdown_pill"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = state.appName.ifBlank { "Peek" },
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NightColorPrimary
            )
            Text(
                text = peekPillCountdownText(state.secondsRemaining),
                fontFamily = AnonymousProFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NightColorPrimary,
                modifier = Modifier.testTag("peek_pill_countdown")
            )
            Text(
                text = "✕",
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NightColorPrimary,
                modifier = Modifier
                    .clickable { onEndEarly() }
                    .testTag("peek_pill_end_button")
                    .padding(horizontal = 4.dp)
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
 * Ledger-Direct (Freya, invention 1): the valve receives NO count.
 * Issue #119, finding 1 (truth half): at 0 remaining the INTERACTIVE
 * valve is not composed, but the valve's place is never left empty —
 * an inert SPENT plate stands there, naming the state and the day
 * the token returns (next Monday 00:00, MonolithWeek). The spent
 * plate grants nothing: no hold, no tap, no exit. The one-token law
 * itself is Sarthak's settled design and is not changed here.
 */

/**
 * The SPENT valve's return line: the Monday the week's token comes
 * back, from the one week definition. Pure java.time.
 */
internal fun emergencyTokenReturnText(nowMillis: Long = System.currentTimeMillis()): String {
    val next = MonolithWeek.nextWeekAnchorMillis(nowMillis)
    val date = Instant.ofEpochMilli(next).atZone(ZoneId.systemDefault())
    return "Returns " + date.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale.US))
}
@Composable
fun DisarmChallengeOverlay(
    state: DisarmChallengeState,
    tokensRemaining: Int,
    soundEngine: TactileSoundEngine? = null,
    onDismissKeepFocusing: () -> Unit,
    onCompleteDisarm: () -> Unit,
    onHoldStarted: () -> Unit = {}
) {
    // Issue-60 (finding 1): banked valve progress is HOISTED here in
    // rememberSaveable — it survives this overlay being dismissed and
    // recomposed, rotation, and process death, and is handed back to
    // each fresh valve view in its factory. Completion consumes it.
    var hoistedBankedMs by rememberSaveable { mutableLongStateOf(0L) }
    // Issue #55 (HIGH): Back must never exit toward the blocked app.
    // On the disarm challenge, Back means exactly what the composed
    // KEEP FOCUSING chip means — dismiss the challenge, stay in
    // Monolith — never an activity finish.
    BackHandler(enabled = state.isVisible) { onDismissKeepFocusing() }
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NightColorBackground.copy(alpha = 0.94f))
                // Issue #55 (CRITICAL): modal — consume every tap
                // outside the card, same guard as digest/primer.
                .clickable(enabled = false) {}
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
                    .clickable(enabled = false) {}
                    .verticalScroll(rememberScrollState())
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

                // Audit R3b #147: below the stopwatch floor, the
                // valve is not the only door — waiting is. The
                // overlay names the floor, the minutes left to it,
                // and that reaching it makes the stop free, so the
                // token is never presented as the sole way out.
                if (state.sessionIsStopwatch && state.remainingFloorSeconds > 0) {
                    val floorMinutes =
                        (state.remainingFloorSeconds + 59) / 60
                    Text(
                        text = "Or wait: this is a stopwatch session with a " +
                            "20-minute floor. $floorMinutes more minutes and " +
                            "stopping is free — no break spent.",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 13.sp,
                        color = NightColorPrimary
                    )
                }

                if (tokensRemaining > 0) {
                    AndroidView(
                        factory = { ctx ->
                            Emergency60sValveView(ctx).apply {
                                isNightMode = true
                                this.soundEngine = soundEngine
                                requiredSeconds = state.requiredHoldSeconds
                                restoreBankedProgress(hoistedBankedMs)
                            }
                        },
                        update = { view ->
                            view.requiredSeconds = state.requiredHoldSeconds
                            view.onBankedProgressChanged = { banked ->
                                hoistedBankedMs = banked
                            }
                            view.onEmergencyBreakCompleted = {
                                hoistedBankedMs = 0L
                                onCompleteDisarm()
                            }
                            view.onHoldStarted = onHoldStarted
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .testTag("emergency_60s_valve")
                    )

                    Text(
                        // Issue #55 (fresh-eyes): the seconds are the
                        // valve's own requiredHoldSeconds, not a
                        // hardcoded 60 that only agreed by accident.
                        text = "Hold to fill ${state.requiredHoldSeconds} seconds. Every 15 seconds banks. If your hand slips, what isn't banked drains away. The banked part stays.",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 11.sp,
                        color = NightColorPrimary.copy(alpha = 0.75f)
                    )
                } else {
                    // SPENT face (issue #119, finding 1): the valve's
                    // place, rendered inert — visible, truthful, and
                    // granting nothing. No AndroidView, no hold, no
                    // click target: the week's break is used, and the
                    // plate says when the token returns.
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                NightColorPrimary.copy(alpha = 0.35f),
                                RoundedCornerShape(12.dp)
                            )
                            .testTag("emergency_valve_spent"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "VALVE SPENT — THIS WEEK'S BREAK IS USED",
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = NightColorPrimary.copy(alpha = 0.8f)
                        )
                        Text(
                            text = emergencyTokenReturnText(),
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 11.sp,
                            color = NightColorPrimary.copy(alpha = 0.75f)
                        )
                    }
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

