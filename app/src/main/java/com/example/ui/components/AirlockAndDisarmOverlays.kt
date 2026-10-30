package com.example.ui.components
import androidx.compose.ui.res.stringResource
import com.example.R

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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.BatterySnapshot
import com.example.data.CapturedNotificationEntity
import com.example.data.ClockText
import androidx.compose.ui.platform.LocalContext
import com.example.data.DistractionTriggerReason
import com.example.data.deskClockBatteryNotice
import com.example.data.deskClockKeepsScreenOn
import com.example.data.readBatterySnapshot
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
 * The nightstand clock's window brightness (Audit R3b #116,
 * finding 3): the clock is a KEPT nightstand display, but until
 * now nothing in the app ever lowered brightness — it burned all
 * night at whatever daytime brightness the phone was left on, in
 * full-alpha cream. While the clock shows, the window is held at
 * 5% brightness; on dismiss the override is released back to the
 * system brightness. 0.05 is dim enough to read in a dark room
 * and far too dim to light one.
 */
const val DESK_CLOCK_SCREEN_BRIGHTNESS = 0.05f

/** The desk clock's dimmed cream: the time at 72% of the night
 *  cream, the progress bar at 50% — with the window itself at 5%
 *  brightness the face recedes instead of glowing. */
const val DESK_CLOCK_TIME_ALPHA = 0.72f
const val DESK_CLOCK_BAR_ALPHA = 0.50f

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
        // Issue #122 (finding 3): the premise now has a battery law.
        // The battery is re-read every 30s while the clock shows;
        // at a battery in risk (low / critical / saver on, not
        // charging) the flag is NEVER added — the system timeout
        // governs and the clock prints why — because holding an
        // OLED on at 8% is the fastest route to the death the
        // shield never survives. Before the first read lands the
        // premise holds as before (the read is synchronous inside
        // the same effect's first pass).
        val clockView = LocalView.current
        var batterySnap by remember { mutableStateOf<BatterySnapshot?>(null) }
        LaunchedEffect(visible) {
            if (visible) {
                while (true) {
                    batterySnap = readBatterySnapshot(clockView.context)
                    delay(30_000L)
                }
            }
        }
        // WELLE-5 / #253-10: the clock also tells the time of day.
        // The hero numeral stays the session span (the span law
        // below is untouched); the wall time rides as a small
        // secondary line under it, ticked once a second while the
        // overlay is visible.
        var nowWallMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
        LaunchedEffect(visible) {
            while (visible) {
                nowWallMs = System.currentTimeMillis()
                delay(1000)
            }
        }
        val keepScreenOn = batterySnap?.let { deskClockKeepsScreenOn(it) } ?: true
        DisposableEffect(visible, keepScreenOn) {
            var ctx: android.content.Context? = clockView.context
            var activity: android.app.Activity? = null
            while (ctx is android.content.ContextWrapper) {
                if (ctx is android.app.Activity) { activity = ctx; break }
                ctx = ctx.baseContext
            }
            val window = activity?.window
            // Issue #122: at battery risk the flag is NEVER added
            // (this effect is keyed on keepScreenOn, so a drop to
            // risk re-runs it and the dispose above has already
            // cleared the flag) — the system timeout governs.
            if (keepScreenOn) {
                window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            // Nightstand dimming (#116 finding 3): hold the window
            // at 5% while the clock shows; release the override on
            // dispose so the rest of the app gets system brightness.
            window?.attributes = window.attributes.apply {
                screenBrightness = DESK_CLOCK_SCREEN_BRIGHTNESS
            }
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
                window?.attributes = window.attributes.apply {
                    screenBrightness =
                        android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                }
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
        // Audit R3b #147: a stopwatch clock counts UP — the bare
        // MM:SS was indistinguishable from a countdown at a glance.
        // The + prefix is the direction marker, here and on the
        // Deck's hero readout alike.
        // #127 finding 4 (format half): the digits ride the shared
        // duration print — H:MM:SS past an hour, so a 170-minute span
        // no longer prints as "170:00". The label half is already
        // cured on main by #111 (deskClockModeLabel above the digits);
        // the branch's deskClockLabel/EXIT-tap mechanism is NOT
        // restored over it.
        val timeText = if (activeSession != null && activeSession.isActive &&
            activeSession.isStopwatchMode
        ) {
            "+" + com.example.data.formatSessionDuration(displaySecs)
        } else {
            com.example.data.formatSessionDuration(displaySecs)
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

                // Issue #122: when the battery law refuses the
                // always-on hold, the clock says so on its face —
                // never a silent early death to the screen timeout.
                val batteryNotice = batterySnap?.let { deskClockBatteryNotice(it) }
                if (batteryNotice != null) {
                    Text(
                        text = batteryNotice,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = NightColorPrimary,
                        modifier = Modifier.testTag("desk_clock_battery_notice")
                    )
                }

                // Hero architectural Space Mono numerals (112sp)
                Text(
                    text = timeText,
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 96.sp,
                    letterSpacing = (-3).sp,
                    // Dimmed cream (#116 finding 3): the kept
                    // nightstand face, at night luminance.
                    color = NightColorPrimary.copy(alpha = DESK_CLOCK_TIME_ALPHA)
                )

                // The time of day, small under the span (WELLE-5 / #253-10).
                Text(
                    text = "LOCAL TIME " + deskClockWallTimeText(nowWallMs),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 2.sp,
                    color = NightColorPrimary.copy(alpha = 0.75f),
                    modifier = Modifier.testTag("desk_clock_wall_time")
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
                            // arm commit only (Round 1c). Dimmed to
                            // half alpha at night (#116 finding 3):
                            // the old full-width full-alpha cream bar
                            // was a light strip across the bedside.
                            .background(NightColorPrimary.copy(alpha = DESK_CLOCK_BAR_ALPHA))
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
 * The desk clock's time-of-day print (WELLE-5 / #253-10): the
 * overlay's hero numeral is the session span, and until this
 * helper the surface never told the time of day at all — a
 * "desk clock" that could not answer "what time is it?".
 * 24-hour HH:mm, the slip's own convention (ReceiptSheet.hhmm).
 */
internal fun deskClockWallTimeText(nowMillis: Long): String =
    java.text.SimpleDateFormat("HH:mm", java.util.Locale.US)
        .format(java.util.Date(nowMillis))

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

/**
 * Issue #325 (Audit R5, Zario visual): the stake line the door
 * prints under the session's end — the week's emergency-break
 * balance, on the surface where the decision happens (Zario draws
 * its economy on every intercept; Monolith's balance lived only
 * on the Deck). At zero the state is STATED, not hidden: a spent
 * valve is a fact the user is owed at the wall, in words. A
 * negative balance means the state was not carried (previews,
 * tests) and the door prints nothing rather than inventing one.
 */
internal fun airlockStakeLine(emergencyBreaksRemaining: Int): String? = when {
    emergencyBreaksRemaining < 0 -> null
    emergencyBreaksRemaining == 0 ->
        "EMERGENCY BREAK: NONE LEFT THIS WEEK — THE VALVE IS SPENT"
    emergencyBreaksRemaining == 1 ->
        "EMERGENCY BREAK: 1 LEFT THIS WEEK"
    else ->
        "EMERGENCY BREAK: $emergencyBreaksRemaining LEFT THIS WEEK"
}

/** English ordinal for the attempt mirror ("1ST", "2ND", "3RD",
 *  "4TH" — the 11/12/13 teens take TH, never ST/ND/RD). */
internal fun attemptOrdinal(n: Int): String {
    val suffix = when {
        n % 100 in 11..13 -> "TH"
        n % 10 == 1 -> "ST"
        n % 10 == 2 -> "ND"
        n % 10 == 3 -> "RD"
        else -> "TH"
    }
    return "$n$suffix"
}

/**
 * Issue #254 F12 (Audit R5): the attempt mirror the door prints
 * under the app name — which reach today this is, counted from
 * the intercept ledger. The per-app count used to be buried in
 * a buried row (BlockedAppEntity.interceptCount) and never stood
 * at the decision point. 0 = not carried (previews, tests, or
 * the count not yet read back): the door prints nothing rather
 * than inventing a number.
 */
internal fun airlockAttemptLine(todayAttempts: Int): String? = when {
    todayAttempts <= 0 -> null
    else -> "${attemptOrdinal(todayAttempts)} REACH TODAY"
}

/**
 * The cooldown's SCOPE, printed beside its price (issue #114,
 * finding 3): the cooldown is per app — this app's timer says
 * nothing about any other peek app's. Until this line existed the
 * scope was discoverable only by rotating to the next app and
 * finding a fresh wall there. Pure text; the scope itself is a
 * policy Sarthak has not ruled on changing (see the issue-114
 * report's DECISION-NEEDED), so the surface's job is to state it,
 * never to imply a global lock it does not deliver.
 */
internal fun peekCooldownScopeText(): String =
    "THE COOLDOWN IS PER APP — OTHER PEEK APPS KEEP THEIR OWN TIMERS"

/**
 * Issue #102 (peek-lived, finding 5): the peek offer's terms, in
 * the app's own words, printed BEFORE the peek is taken. The peek
 * chip is labelled "2-MINUTE PEEK" while the token-spending door
 * is "EMERGENCY BREAK" — this line is what tells the user the two
 * are different doors: the peek spends no weekly break and does
 * not end the session. Pure, so the wording is unit-tested.
 * (Ported from PR #351's peekTermsText — its pill/enforcer build
 * was superseded by #114/#99 on main; this sentence was not.)
 */
internal fun peekTermsText(): String =
    "THE PEEK DOES NOT SPEND YOUR EMERGENCY BREAK AND DOES NOT END THE SESSION"

/**
 * Issue #102 (peek-lived, finding 4): the withheld peek's reason
 * stands on the door from the FIRST second — it is not gated on
 * the breath countdown finishing. Before this, the reason block
 * waited for `countdownSecs == 0`, so a user who returned from a
 * peek (or hit a cooldown) stared at a wall that explained
 * nothing for a second countdown. Pure decision, unit-tested;
 * the countdown deliberately plays no part in it.
 */
internal fun peekWithheldReasonVisible(peekReady: Boolean): Boolean = !peekReady

/**
 * What waits at the end of the breath, printed DURING the breath
 * (issue #114, finding 3): the wall's whole exchange — wait, then
 * one tap buys the lease — stated before the countdown reaches
 * zero, not revealed by it. [durationSeconds] is the lease's own
 * constant, read at the call site, never hardcoded here.
 */
internal fun peekOfferPreviewText(durationSeconds: Int): String {
    val minutes = durationSeconds / 60
    return "AFTER THE BREATH: ONE TAP OPENS THIS APP FOR $minutes MINUTES"
}

/**
 * Issue #324 (Audit R5, Zario head-to-head): the pause carries a
 * SUBSTITUTE, not dead air. Zario feeds its Friction pause a
 * micro-challenge pack — do this instead — while Monolith's wall
 * offered two verbs and a numeral to wait out. The pack is local
 * and fixed (no backend, no download, no tracking of whether it
 * was done — the app is a blocker, not a tracker): one-minute
 * off-phone actions, rotated deterministically by the intercept
 * instance so consecutive reaches do not repeat the same line.
 */
internal val AIRLOCK_SUBSTITUTES: List<String> = listOf(
    "DRINK A GLASS OF WATER",
    "STAND UP AND STRETCH YOUR ARMS OVERHEAD",
    "LOOK OUT OF THE WINDOW AT THE FARTHEST POINT YOU CAN SEE",
    "SPLASH COLD WATER ON YOUR FACE",
    "TAKE TEN SLOW BREATHS, OUT LONGER THAN IN",
    "WALK TO THE DOOR AND BACK, TWICE",
    "ROLL YOUR SHOULDERS AND NECK, SLOWLY",
    "TIDY ONE CORNER OF THE DESK",
    "STEP INTO DAYLIGHT AND LOOK AT THE SKY",
    "WASH YOUR HANDS IN COLD WATER",
    "SHAKE OUT YOUR HANDS AND ARMS",
    "WATER A PLANT, OR OPEN A WINDOW FOR AIR"
)

/** The substitute for THIS intercept: a pure rotation over
 *  [AIRLOCK_SUBSTITUTES] keyed on the per-intercept instance id
 *  (issue #55's identity), so the same reach never re-rolls and
 *  the next reach moves on. Negative ids fold in safely. */
internal fun airlockSubstituteFor(interceptInstanceId: Long): String =
    AIRLOCK_SUBSTITUTES[
        Math.floorMod(interceptInstanceId, AIRLOCK_SUBSTITUTES.size.toLong()).toInt()
    ]

/** The substitute line as the wall prints it. */
internal fun airlockSubstituteLine(interceptInstanceId: Long): String =
    "INSTEAD, ONE MINUTE: ${airlockSubstituteFor(interceptInstanceId)}"

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
                                text = stringResource(R.string.label_held),
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
                                            text = stringResource(R.string.key_del),
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
 * 4. SHIELD PERMISSION PRIMER MODAL (issue #324, finding 4 — REBUILT)
 *
 * The original primer was deleted with the dead-code layer (issue
 * #74): two grants only, and a commit-and-arm-anyway path that armed
 * a session with no interception behind it (register LR2-S1-18).
 * This
 * rebuild is the BUILD half of finding 4's cure, and it is NOT the
 * old body restored:
 *
 *  - It presents ONE step of the six-grant sequence — the flow's
 *    current (or resumed) step, derived by PermissionFlow from the
 *    Watch snapshot's own prints — with its position in the sequence
 *    and the board's own explainer for that grant
 *    (WatchStore.boardRationale, issue #280): what the grant does
 *    and what it never does, BEFORE the Settings trip fires.
 *  - Its only action is that step's FIX trip (the host records the
 *    presentation in PermissionFlowStore BEFORE the pane opens, so
 *    the sequence resumes at this step after any interruption) and
 *    NOT NOW, which dismisses for this process. The standing paths
 *    remain: PARK's board and the arm slide's refusal FIX routes.
 *  - There is NO arm-anyway path, by construction: no callback for
 *    one exists. Arming reads the warrant itself, in the slide,
 *    and refuses in the open — that law is not re-litigated here.
 *
 * The DESIGN half of finding 4 (the flow's final presentation) is
 * owned by the guard/warrant programme on issue #58; this surface
 * is the truthful machinery it stands on.
 */
@Composable
fun ShieldPermissionPrimerModal(
    visible: Boolean,
    step: com.example.service.WatchItemId?,
    stepLine: String?,
    countLine: String?,
    explainer: String?,
    fixLabel: String?,
    onFixStep: () -> Unit,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible && step != null,
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
                if (stepLine != null) {
                    Text(
                        text = stepLine,
                        fontFamily = AnonymousProFamily,
                        fontSize = 12.5.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
                if (explainer != null) {
                    Text(
                        text = explainer,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 12.5.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
                if (countLine != null) {
                    Text(
                        text = countLine,
                        fontFamily = AnonymousProFamily,
                        fontSize = 12.5.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
                if (fixLabel != null) {
                    BlockitActionChip(
                        wordLabel = fixLabel,
                        isAccentArmed = true,
                        onClick = onFixStep,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "permission_primer_fix_button"
                    )
                }
                BlockitActionChip(
                    wordLabel = "NOT NOW",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "permission_primer_dismiss_button"
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
                    text = stringResource(R.string.airlock_interception_badge, state.interceptReasonBadge.uppercase(Locale.US)),
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

                // Issue #254 F12 (Audit R5): the attempt mirror —
                // under the app name, the door states which reach
                // today this is, counted from the intercept ledger.
                airlockAttemptLine(state.todayAttempts)?.let { line ->
                    Text(
                        text = line,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = NightColorPrimary,
                        modifier = Modifier.testTag("airlock_attempt_line")
                    )
                }

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

                // Issue #325 (Audit R5, Zario): the stake stands on
                // the door — the week's break balance at the moment
                // of decision, including the spent state at zero.
                airlockStakeLine(state.emergencyBreaksRemaining)?.let { line ->
                    Text(
                        text = line,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 12.5.sp,
                        color = NightColorPrimary.copy(alpha = 0.8f),
                        modifier = Modifier.testTag("airlock_stake_line")
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
                        text = stringResource(R.string.active_intention, state.sessionIntention),
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 12.5.sp,
                        color = NightColorPrimary.copy(alpha = 0.8f),
                        // Issue #124: bounded at the moment of
                        // temptation — a legacy over-long stored
                        // intention ellipsises at two lines instead
                        // of flooding the wall. (New rows are capped
                        // at the arm snapshot in startSession.)
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Issue #324 (Audit R5, Zario): the pause is spent,
                // not just charged — one rotating off-phone
                // substitute stands on the wall itself, keyed to
                // this intercept so it never repeats back-to-back.
                Text(
                    text = airlockSubstituteLine(state.interceptInstanceId),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 12.5.sp,
                    color = NightColorPrimary.copy(alpha = 0.8f),
                    modifier = Modifier.testTag("airlock_substitute_line")
                )

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

                if (peekReady) {
                    // Audit R6 #341 F60: composed from the FIRST frame,
                    // disabled and priced while the countdown runs.
                    // Before this the chip (and its price line) only
                    // appeared at second 0's end — the card grew
                    // mid-decision, switch scanners restarted their
                    // scan, and the offer could not even be READ
                    // before the timer ran out.
                    // Issue #114, finding 3 (composed with F60 above): the offer
                    // line also stands during the breath — what waits at zero is
                    // printed from the first second, while the price, scope and terms
                    // print with the chip itself below, so the whole exchange is
                    // readable during the countdown without duplicated lines.
                    if (countdownSecs > 0) {
                        Text(
                            text = peekOfferPreviewText(MonolithRepository.PEEK_DURATION_SECONDS),
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 12.5.sp,
                            color = NightColorPrimary.copy(alpha = 0.8f)
                        )
                    }
                BlockitActionChip(
                    wordLabel = "2-MINUTE PEEK",
                    // Issue #114, finding 5: this door is NOT an
                    // emergency — it is the routine, repeatable,
                    // cooldown-priced peek. The EMERGENCY name now
                    // belongs only to the session-ending weekly
                    // break, so the two doors can never again be
                    // confused at the moment of least capacity.
                    // SIL-2 / W-29: the badge is the SET value,
                    // read from the lease machinery's own
                    // constant — no hardcoded minutes here.
                    monoBadge = peekBadgeText(MonolithRepository.PEEK_DURATION_SECONDS),
                    onClick = { if (countdownSecs == 0) onTake2MinutePeek() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (countdownSecs == 0) Modifier else Modifier.alpha(0.45f)),
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
                Text(
                    text = peekCooldownScopeText(),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 12.5.sp,
                    color = NightColorPrimary.copy(alpha = 0.8f)
                )
                // Issue #102, finding 5: the terms stand on
                // the offer itself, before the tap.
                Text(
                    text = peekTermsText(),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 12.5.sp,
                    color = NightColorPrimary.copy(alpha = 0.8f)
                )
                }

                // The withheld peek prints its state AND its reason
                // (Held Door Truth Baseline): cooldown with its
                // live remaining time, or the running pill named.
                // Doors that never owed a peek carry withheld ==
                // null and print nothing here. Issue #102, finding
                // 4: the reason is NOT gated on the breath countdown
                // — it shows from the first second, not after a
                // second countdown (see peekWithheldReasonVisible).
                if (peekWithheldReasonVisible(peekReady)) {
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
                            text = stringResource(R.string.peek_already_running),
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 12.5.sp,
                            color = NightColorPrimary.copy(alpha = 0.8f)
                        )
                        PeekWithheld.PEEK_OFF -> Text(
                            text = stringResource(R.string.peek_off),
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 12.5.sp,
                            color = NightColorPrimary.copy(alpha = 0.8f)
                        )
                        // Issue #154: not a refusal — the door's
                        // own state line for a LOCKED tier, naming
                        // the tier and where tiers are set, so a
                        // legitimate need meets a fact instead of
                        // a blank wall.
                        PeekWithheld.TIER_LOCKED -> Text(
                            text = "LOCKED TIER — NO PEEK EXISTS HERE · PEEK APPS ARE SET IN THE SHIELD",
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
            // #336: the close glyph is an ICON from the one system
            // (MonolithIcons), not a text character.
            androidx.compose.material3.Icon(
                imageVector = com.example.ui.theme.MonolithIcons.Close,
                contentDescription = "End peek early",
                tint = NightColorPrimary,
                modifier = Modifier
                    .clickable { onEndEarly() }
                    .testTag("peek_pill_end_button")
                    .padding(horizontal = 4.dp)
                    .size(16.dp)
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
                    text = stringResource(R.string.emergency_break),
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

                    // Issue #114, finding 5: the mechanics of the
                    // week's only break are consequential print —
                    // they stand at the same size and weight as the
                    // consequence line above, not in the smallest,
                    // dimmest type on the screen.
                    Text(
                        // Issue #55 (fresh-eyes): the seconds are the
                        // valve's own requiredHoldSeconds, not a
                        // hardcoded 60 that only agreed by accident.
                        text = "Hold to fill ${state.requiredHoldSeconds} seconds. Every 15 seconds banks. If your hand slips, what isn't banked drains away. The banked part stays.",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 13.sp,
                        color = NightColorPrimary
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

