package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
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
import com.example.ui.views.TowerStreakView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

// FLOW §2.4.4 — the light cure, in constants (Salz's desk,
// plan/REFINE-salz-aod.md §§1–3). Nacht's ceiling for the night
// face is 0.25 relative luminance; NightColorPrimary (#FFE4CF)
// prints 0.8125 at full strength — a 3.3× breach on a face that
// now raises ITSELF and may stand all night. Composited over
// the overlay's true-black ground: the content prints at 0.55
// land at ≈0.216, and the exit line at 0.58 lands at ≈0.246.
// The auto-raise ships only under this cure; these two numbers
// are the cure.
private const val DESK_CLOCK_PRINT_ALPHA = 0.55f
private const val DESK_CLOCK_EXIT_ALPHA = 0.58f

// Issue #13, A-2 — the descent (item 14 / V4), completed:
// DIM BY AREA. The hero and the tear rule descend 0.55 → 0.36
// across the span; every number-carrying small line (sub-line,
// block line, banner incl. the break line) FLOORS at 0.50 — a
// number too dim to read is a number withheld. The EXIT line is
// excepted from that floor: it carries no figure, and keeps its
// descended 0.40.
// THE DOOR INVARIANT, pinned (Mond, ratio form): at every span
// fraction, exit luminance ≥ 1.10 × hero-print luminance. Both
// prints are the same ink, so the invariant is kept by
// construction: exitAlpha = max(0.40, heroAlpha × 1.10).
// Wave-2 honesty fix (Seat Overlay-Review): that sentence was
// FALSE at the standing alphas — the exit is also capped at
// its ruled ceiling DESK_CLOCK_EXIT_ALPHA (0.58), and
// 0.58 / 0.55 = 1.055 < 1.10, so the cap binds wherever the
// hero stands above 0.58 / 1.10 ≈ 0.527 (fraction ≲ 0.12, the
// 60 s suspension, and the dawn's return). The invariant as
// constructed holds through the descent; at standing, the
// ruled ceiling wins. Which of the two constants yields is a
// ruling for Ren — this merge changes neither number, and no
// longer claims both.
private const val DESK_CLOCK_HERO_FLOOR_ALPHA = 0.36f
private const val DESK_CLOCK_NUMBER_FLOOR_ALPHA = 0.50f
private const val DESK_CLOCK_EXIT_FLOOR_ALPHA = 0.40f
private const val DOOR_INVARIANT_RATIO = 1.10f

/** A-4's entry causes: the arrival motion is keyed to WHY the
 *  face rose, and the witness discriminator is the settle
 *  machine's own lastInteraction stamp (carried by the VM). */
enum class DeskClockEntryCause { UNWATCHED, WATCHED, HAND, SEEDED }

/** The face's print alphas at one instant (A-2/A-3, pure). */
internal data class DeskClockAlphas(
    val hero: Float,
    val exit: Float,
    val numberLine: Float
)

/**
 * The descent's whole law in one pure function (A-2 + A-3):
 * - the descent variable is NAMED: [spanElapsedFraction], the
 *   span-elapsed fraction, clamped to [0, 1];
 * - hero descends standing → 0.36 by that fraction; number
 *   lines descend standing → their 0.50 floor; the exit line
 *   follows the door invariant (≥ 1.10 × hero, floor 0.40,
 *   capped at the 0.58 ceiling — see the invariant's note at
 *   DOOR_INVARIANT_RATIO for where the cap binds);
 * - a raise or re-raise SUSPENDS the descent for 60 s
 *   ([descentSuspended]): the face opens at standing alphas;
 * - the DAWN RAMP (A-3): in the final 60 s every descended
 *   alpha lerps back to its standing constant, reaching full
 *   exactly at T-0 ([spanRemainingSeconds] in 1..60).
 */
internal fun deskClockPrintAlphas(
    spanElapsedFraction: Float,
    descentSuspended: Boolean,
    spanRemainingSeconds: Int?,
    isStopwatch: Boolean
): DeskClockAlphas {
    val fraction = spanElapsedFraction.coerceIn(0f, 1f)
    var hero = DESK_CLOCK_PRINT_ALPHA +
        (DESK_CLOCK_HERO_FLOOR_ALPHA - DESK_CLOCK_PRINT_ALPHA) * fraction
    var number = DESK_CLOCK_PRINT_ALPHA +
        (DESK_CLOCK_NUMBER_FLOOR_ALPHA - DESK_CLOCK_PRINT_ALPHA) * fraction
    if (descentSuspended) {
        hero = DESK_CLOCK_PRINT_ALPHA
        number = DESK_CLOCK_PRINT_ALPHA
    }
    if (!isStopwatch && spanRemainingSeconds != null && spanRemainingSeconds in 1..60) {
        val dawn = (60 - spanRemainingSeconds) / 60f
        hero += (DESK_CLOCK_PRINT_ALPHA - hero) * dawn
        number += (DESK_CLOCK_PRINT_ALPHA - number) * dawn
    }
    val exit = maxOf(DESK_CLOCK_EXIT_FLOOR_ALPHA, hero * DOOR_INVARIANT_RATIO)
        .coerceAtMost(DESK_CLOCK_EXIT_ALPHA)
    return DeskClockAlphas(hero = hero, exit = exit, numberLine = number)
}

/** A-7: the tear fraction never prints below a hair and never
 *  past the span — elapsed can run past target in a delayed-
 *  finish window while the hero sits at 0:00. */
internal const val TEAR_HAIR_FRACTION = 0.01f
internal fun tearFraction(spanElapsedFraction: Float): Float =
    spanElapsedFraction.coerceIn(TEAR_HAIR_FRACTION, 1f)

/**
 * 1. ALWAYS-ON NIGHTSTAND DESK CLOCK OVERLAY (Decision #8)
 *
 * Issue #13 merge notes: keep-awake ownership MOVED to the VM's
 * session machinery (A-10(v)) — this face no longer touches
 * FLAG_KEEP_SCREEN_ON; the VM's surrender law (deadline, fade,
 * yield) drives it. Arrival/exit motion is A-4's cause table,
 * carried in [entryCause] / [exitIsFinish]; the surrender fade
 * (A-10(v): print fades 520 Curve C to 0 at true-off − 520 ms)
 * arrives as [surrenderFade].
 */
@Composable
fun AlwaysOnDeskClockOverlay(
    visible: Boolean,
    activeSession: FocusSessionEntity?,
    activeTask: FocusTaskEntity?,
    dialMinutes: Int,
    burnInStep: Int,
    entryCause: DeskClockEntryCause = DeskClockEntryCause.HAND,
    exitIsFinish: Boolean = false,
    surrenderFade: Boolean = false,
    // Wave-2 (Samt D-1): the descent belongs to the SLEEP span
    // alone; the VM derives this from the stand-down's own
    // isSleepLoadout path and hands it in. Default false: a
    // caller that does not know keeps the face at standing.
    descentApplies: Boolean = false,
    onDismiss: () -> Unit
) {
    // A-4 — arrival and exit, keyed to cause. Ground and print
    // animate separately: the ground is true black, the print
    // lands on completed black and never blends over a lit Deck.
    val groundAlpha = remember { Animatable(0f) }
    val printAlpha = remember { Animatable(0f) }
    LaunchedEffect(visible, entryCause) {
        if (visible) {
            when (entryCause) {
                DeskClockEntryCause.SEEDED -> {
                    // SEEDED (cold open; battery-yield return):
                    // NO fade — frame one at full lawful alpha.
                    // THE FACE FADES ONLY ON ITS FIRST ARRIVAL
                    // IN A SESSION (Mond's law).
                    groundAlpha.snapTo(1f)
                    printAlpha.snapTo(1f)
                }
                DeskClockEntryCause.UNWATCHED -> {
                    // Machine fire ≥ 10 s after the last
                    // interaction: ground 260 Curve A, print
                    // 520 Curve C at +140.
                    // Wave-2 fix (Salz C-15): ground and print run
                    // in PARALLEL child coroutines — animateTo
                    // suspends, so the old serial calls started
                    // the print's delay only after the ground
                    // finished (~40% long, and a completed black
                    // slab on the WATCHED row). Both delays now
                    // run from the same t=0 the table is written
                    // against.
                    launch { groundAlpha.animateTo(1f, tween(260, easing = MonolithCurves.EasingA)) }
                    launch {
                        printAlpha.animateTo(
                            1f,
                            tween(520, delayMillis = 140, easing = MonolithCurves.EasingC)
                        )
                    }
                }
                DeskClockEntryCause.WATCHED -> {
                    // Machine fire < 10 s after the last
                    // interaction (incl. V1's set-down fire):
                    // ground 180 Curve A; print 420 Curve B at
                    // +180 — total 600 ms, no black slab.
                    launch { groundAlpha.animateTo(1f, tween(180, easing = MonolithCurves.EasingA)) }
                    launch {
                        printAlpha.animateTo(
                            1f,
                            tween(420, delayMillis = 180, easing = MonolithCurves.EasingB)
                        )
                    }
                }
                DeskClockEntryCause.HAND -> {
                    // The DESK CLOCK chip: ground 180 Curve A;
                    // print 260 Curve A at +180; total 440 ms.
                    launch { groundAlpha.animateTo(1f, tween(180, easing = MonolithCurves.EasingA)) }
                    launch {
                        printAlpha.animateTo(
                            1f,
                            tween(260, delayMillis = 180, easing = MonolithCurves.EasingA)
                        )
                    }
                }
            }
        } else {
            if (exitIsFinish) {
                // EXIT, FINISH (A-4): print fades 260 Curve A
                // while the true-black ground HOLDS over the
                // Deck through the detention; the ground fades
                // 260 Curve A as the receipt fades in. No surface
                // brighter than the clock stands between the
                // clock and the receipt (item 19, extended).
                // Wave-2 (C-15): parallel — the ground's 1200 ms
                // hold is measured from the exit's t=0, not from
                // the print fade's end. (C-23's residual stands:
                // the hold is still a constant against a
                // detention that can run to 1800 ms on the
                // faulted branch — keying it to the VM's issued
                // flag is a named debt, not done here.)
                launch { printAlpha.animateTo(0f, tween(260, easing = MonolithCurves.EasingA)) }
                launch {
                    groundAlpha.animateTo(
                        0f,
                        tween(260, delayMillis = 1200, easing = MonolithCurves.EasingA)
                    )
                }
            } else {
                // EXIT, DISMISSAL: 180 EasingThrow, ground and
                // print together (item 17 stands). Wave-2 (C-15):
                // "together" is now literally true — parallel
                // launches, not two serial 180 ms fades.
                launch {
                    groundAlpha.animateTo(
                        0f, tween(180, easing = com.example.ui.theme.MonolithKin.EasingThrow)
                    )
                }
                launch {
                    printAlpha.animateTo(
                        0f, tween(180, easing = com.example.ui.theme.MonolithKin.EasingThrow)
                    )
                }
            }
        }
    }
    // A-10(v): the surrender fade — at true-off − 520 ms the
    // print fades 520 Curve C to 0 over the black ground.
    val surrenderAlpha = remember { Animatable(1f) }
    LaunchedEffect(surrenderFade) {
        if (surrenderFade) {
            surrenderAlpha.animateTo(0f, tween(520, easing = MonolithCurves.EasingC))
        } else {
            surrenderAlpha.snapTo(1f)
        }
    }
    if (!visible && groundAlpha.value <= 0f && printAlpha.value <= 0f) return

    // FLOW §2.4.2 — the idle lie dies (ST-01 / FL-03). The hero
    // is derived by deskClockHeroText: a live session reads its
    // span; no live session, the hero is the CURRENT TIME,
    // labelled THE TIME NOW directly above it (K-8).
    // dialMinutes is no longer read here at all.
    val nowWallMillis = System.currentTimeMillis()
    val running = activeSession != null && activeSession.isActive
    val timeText = deskClockHeroText(activeSession, nowWallMillis)

    // A-2(ii): THE VARIABLE IS NAMED — span-elapsed fraction.
    // Wave-2 fixes (Samt D-1 + D-2): the fraction exists ONLY
    // for the SLEEP span. D-1: the old code descended on every
    // timed span — a 25-minute afternoon session sank to vigil
    // alphas in daylight, and the descent (whose meaning is
    // "the night is deepening") came to mean "a timer runs".
    // D-2: a stopwatch divided by an invented 1200 s span that
    // exists in no ruling; the stopwatch is the awake
    // instrument and never descends.
    val spanElapsedFraction = when {
        descentApplies && activeSession != null && activeSession.isActive &&
            !activeSession.isStopwatchMode ->
            activeSession.spanElapsedSeconds.toFloat() /
                activeSession.spanTargetSeconds.coerceAtLeast(1)
        else -> 0f
    }

    // A-2(iii): any raise or re-raise opens at the standing
    // alphas; the descent suspends 60 s from the raise stamp the
    // VM's interaction clock guarantees (the composition's first
    // frame of this raise), then resumes from span progress.
    var raisedAtMono by remember(activeSession?.id) {
        mutableStateOf(android.os.SystemClock.elapsedRealtime())
    }
    LaunchedEffect(visible) {
        if (visible) raisedAtMono = android.os.SystemClock.elapsedRealtime()
    }
    val descentSuspended =
        android.os.SystemClock.elapsedRealtime() - raisedAtMono < 60_000L
    val alphas = deskClockPrintAlphas(
        spanElapsedFraction = spanElapsedFraction,
        descentSuspended = descentSuspended,
        // Wave-2 (DAE-1): the dawn ramp keys to the same end
        // the hero and ENDS read — the window's end for a
        // routine row, the span's remaining otherwise.
        spanRemainingSeconds = activeSession?.let { s ->
            if (s.routineId != null && s.windowEndMillis > 0L) {
                ((s.windowEndMillis - nowWallMillis) / 1000L).coerceAtLeast(0L).toInt()
            } else {
                s.spanRemainingSeconds
            }
        },
        isStopwatch = activeSession?.isStopwatchMode == true
    )

    val dx = ((burnInStep % 5) - 2) * 3
    val dy = (((burnInStep / 5) % 5) - 2) * 3

    Box(
        modifier = Modifier
            .fillMaxSize()
            // AOD ground is true black (Round 1a) — OLED pixels
            // off, not the block-bar tone. Its alpha is A-4's
            // ground animation.
            .background(Color.Black.copy(alpha = groundAlpha.value))
            .clickable(onClick = onDismiss)
            .padding(24.dp)
            .testTag("always_on_desk_clock_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(dx, dy) }
                .graphicsLayer {
                    alpha = printAlpha.value * surrenderAlpha.value
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // A-6(i): ENDS's top-line seat is DELETED. The top
            // line is the constant "TAP ANYWHERE TO EXIT" with
            // no conditional (§C-2's line, unamended per E-4);
            // ENDS lives only in the sub-line (§C-1's seat).
            Text(
                text = "TAP ANYWHERE TO EXIT",
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = NightColorPrimary.copy(alpha = alphas.exit)
            )

            // The hero and its companion lines, bound in one
            // tight cell (K-8: label and figure inseparable).
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!running) {
                    Text(
                        text = "THE TIME NOW",
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        color = NightColorPrimary.copy(alpha = alphas.numberLine)
                    )
                }
                // Hero architectural Space Mono numerals (112sp)
                Text(
                    text = timeText,
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 96.sp,
                    letterSpacing = (-3).sp,
                    color = NightColorPrimary.copy(alpha = alphas.hero)
                )
                if (running) {
                    // §C-1 / A-6(ii): the sub-line. Timed: "NOW
                    // HH:MM · ENDS HH:MM"; the last minute drops
                    // NOW, ENDS alone; stopwatch: "NOW HH:MM"
                    // alone (an ENDS derived from elapsed would
                    // be a fabricated end). Idle has none.
                    Text(
                        text = deskClockSubLine(activeSession!!, nowWallMillis),
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        color = NightColorPrimary.copy(alpha = alphas.numberLine)
                    )
                    // A-1: the block line — STATE-ONLY (suppressed
                    // on a break, the break line owns those
                    // minutes), its figure the block's end in wall
                    // time, the final block figureless.
                    deskClockBlockLine(activeSession!!, nowWallMillis)?.let { blockLine ->
                        Text(
                            text = blockLine,
                            fontFamily = AnonymousProFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 2.sp,
                            color = NightColorPrimary.copy(alpha = alphas.numberLine)
                        )
                    }
                }
            }

            // A-7 — the tear rule, bounded and grounded. Solid
            // is spent, dashed is still attached; the tear
            // fraction is coerceIn(hair, 1f); block boundaries
            // cut 3 dp full-height notches whose gaps are GROUND
            // — true black, drawn as absence over this face's
            // black ground. Verified at the merge: NO
            // NightColorSurfaceVariant bed (#100E0C) sits under
            // this rule anywhere — the old capsule's bed is gone
            // with the capsule, so every notch is truly black.
            if (running && activeSession?.isStopwatchMode != true) {
                val tear = tearFraction(spanElapsedFraction)
                val blockCount = activeSession?.totalStudyBlocks ?: 1
                val ink = NightColorPrimary.copy(alpha = alphas.hero)
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .testTag("desk_clock_tear_rule")
                ) {
                    val w = size.width
                    val h = size.height
                    // Spent: solid to the tear fraction.
                    drawRect(
                        color = ink,
                        topLeft = Offset(0f, 0f),
                        size = Size(w * tear, h)
                    )
                    // Attached: dashed to the span's end.
                    drawLine(
                        color = ink,
                        start = Offset(w * tear, h / 2f),
                        end = Offset(w, h / 2f),
                        strokeWidth = h,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(8f, 6f), 0f
                        )
                    )
                    // Notches: 3 dp ground-black gaps at the
                    // TRUE block-boundary fractions of the span.
                    // Wave-2 fix (Salz C-19): the old code cut at
                    // equal fractions (k/n) — but a span is n
                    // blocks + (n−1) breaks, so block k ends at
                    // (k·block + (k−1)·600) / spanTarget, the
                    // same span arithmetic the hero reads
                    // (MonolithModels.spanTargetSeconds). At
                    // 25-min blocks, n=4, the equal cut stood
                    // ~5.8% of the rule's width inside block 2.
                    if (blockCount > 1) {
                        val spanTarget = (activeSession?.spanTargetSeconds ?: 0).coerceAtLeast(1)
                        val blockSeconds = activeSession?.effectiveTargetSeconds ?: 0
                        val notchWidth = 3.dp.toPx()
                        for (k in 1 until blockCount) {
                            val boundarySeconds = k * blockSeconds + (k - 1) * 600
                            val x = w * (boundarySeconds.toFloat() / spanTarget)
                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(x - notchWidth / 2f, 0f),
                                size = Size(notchWidth, h)
                            )
                        }
                    }
                }
            }

            // A-5 + A-8 — the banner slot. A-8: the banner
            // derives ONLY inside a live session (the VM's
            // activeTask falls back to the first QUEUED task —
            // an idle face must never name a session that is not
            // running). A-5: while a session runs the slot's
            // height is FIXED, text top-aligned; the absent banner
            // leaves the slot empty and still. Idle wears no slot.
            // Wave-2 fix (Salz C-24): the slot is measured from
            // the text it actually holds — 14 sp Atkinson Bold,
            // two lines (≈2 × 20 dp) + 12 dp vertical padding
            // each side = 64 dp, set at 68 for the font's
            // leading — and the text carries a LAW for line 3:
            // maxLines 2 + ellipsis. A long task title truncates
            // by decision, never clips in silence.
            if (running) {
                val bannerLine = deskClockBreakLine(
                    isInSafeBreak = activeSession?.isInSafeBreak == true,
                    safeBreakSecondsRemaining = activeSession?.safeBreakSecondsRemaining ?: 0,
                    nowWallMillis = nowWallMillis
                ) ?: deskClockBannerText(
                    activeTask?.title, activeSession?.intention, activeSession?.profileName
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (bannerLine != null) NightColorSurfaceBright else Color.Transparent
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (bannerLine != null) {
                        Text(
                            text = bannerLine,
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            color = NightColorPrimary.copy(alpha = alphas.numberLine)
                        )
                    }
                }
            }
        }
    }
}

/** The wall clock as this app prints it everywhere else:
 *  HH:mm, 24-hour (the ENDS / WITHIN grammar). */
internal fun wallClockTimeText(wallMillis: Long): String =
    java.text.SimpleDateFormat("HH:mm", Locale.US).format(java.util.Date(wallMillis))

/**
 * The desk clock's hero (FLOW §2.4.1–2 — ST-01 / FL-03). A live
 * session reads its span: the span remaining for a timed
 * session (never the per-block remainder — it is 0 through
 * every Safe Break and this clock has no block context), the
 * elapsed for a stopwatch. NO live session: the hero is the
 * current time — never the composer's dial setting.
 */
internal fun deskClockHeroText(activeSession: FocusSessionEntity?, nowWallMillis: Long): String {
    if (activeSession != null && activeSession.isActive) {
        // Wave-2 fix (Daemmer DAE-1, HIGH): for a routine row
        // the hero counts to the STAND-DOWN's instant — the
        // frozen windowEndMillis, the same derivation ENDS
        // prints (A-12 C-1) — not to the span's arithmetic.
        // The span target is built from whole FLOORED minutes
        // at materialisation, so spanRemaining hit 00:00 up to
        // 59 s before the window's end, every night, while the
        // sub-line still promised ENDS 03:30: two ends at once
        // inside the last minute, and dawn's peak spent early.
        val displaySecs =
            if (activeSession.isStopwatchMode) {
                activeSession.elapsedSeconds
            } else if (activeSession.routineId != null && activeSession.windowEndMillis > 0L) {
                ((activeSession.windowEndMillis - nowWallMillis) / 1000L)
                    .coerceAtLeast(0L).toInt()
            } else {
                activeSession.spanRemainingSeconds
            }
        // Wave-2 fix (Salz C-8): the L1 dual grammar. The old
        // single %02d:%02d of TOTAL minutes printed figures no
        // other surface prints ("125:49" for a 2 h stopwatch,
        // "65:00" for a 65-minute span). L1: ≥ 60 min prints
        // H:MM (hours unpadded, minutes zero-padded, no
        // seconds); < 60 min prints MM:SS.
        return if (displaySecs >= 3600) {
            String.format(Locale.US, "%d:%02d", displaySecs / 3600, (displaySecs % 3600) / 60)
        } else {
            String.format(Locale.US, "%02d:%02d", displaySecs / 60, displaySecs % 60)
        }
    }
    return wallClockTimeText(nowWallMillis)
}

/** The current-time line: NOW HH:mm — the figure wears its
 *  label in the same string (K-8). */
internal fun deskClockNowLine(nowWallMillis: Long): String = "NOW " + wallClockTimeText(nowWallMillis)

/**
 * A-12 / C-1, answered from the code: a routine row's frozen
 * windowEndMillis (MonolithModels.kt — the stand-down reads
 * the same stamp) IS the end the machine will keep, and a
 * routine span's target CAN differ from its window (the span
 * derives from block targets + breaks; the window is the
 * schedule's frozen snapshot). So for routine-born rows ENDS
 * derives from windowEndMillis; for hand rows, from now + the
 * span remaining — the face promises the end the stand-down
 * will keep.
 */
internal fun deskClockEndsAtMillis(session: FocusSessionEntity, nowWallMillis: Long): Long =
    if (session.routineId != null && session.windowEndMillis > 0L) {
        session.windowEndMillis
    } else {
        nowWallMillis + session.spanRemainingSeconds * 1000L
    }

internal fun deskClockEndsLine(session: FocusSessionEntity, nowWallMillis: Long): String =
    "ENDS " + wallClockTimeText(deskClockEndsAtMillis(session, nowWallMillis))

/**
 * §C-1 / A-6(ii) — the sub-line, whole: timed sessions read
 * "NOW HH:MM · ENDS HH:MM"; in the last minute NOW drops and
 * ENDS stands alone; a stopwatch reads "NOW HH:MM" alone.
 */
internal fun deskClockSubLine(session: FocusSessionEntity, nowWallMillis: Long): String {
    if (session.isStopwatchMode) return deskClockNowLine(nowWallMillis)
    if (session.spanRemainingSeconds in 1..60) return deskClockEndsLine(session, nowWallMillis)
    return deskClockNowLine(nowWallMillis) + " · " + deskClockEndsLine(session, nowWallMillis)
}

/**
 * A-1 — the block line, state-only: suppressed during a break
 * (the row's per-block remaining is 0 for the whole break and
 * the break line owns those minutes); suppressed for a
 * single-block span (no block context to name). For k < n its
 * figure is the block's end in wall time — "BLOCK k OF n ·
 * UNTIL HH:MM" (UNTIL is the house's phase-end word; ENDS
 * belongs to the span alone). The FINAL block prints no
 * figure: there the block's end IS the span's ENDS, already
 * standing in the sub-line — one instant never prints twice.
 */
internal fun deskClockBlockLine(session: FocusSessionEntity, nowWallMillis: Long): String? {
    if (!session.isActive || session.isStopwatchMode) return null
    if (session.isInSafeBreak) return null
    val n = session.totalStudyBlocks
    if (n <= 1) return null
    val k = session.currentBlockIndex.coerceIn(1, n)
    if (k >= n) return "BLOCK $n OF $n"
    val blockEndWall = nowWallMillis + session.remainingSeconds.coerceAtLeast(0) * 1000L
    return "BLOCK $k OF $n · UNTIL " + wallClockTimeText(blockEndWall)
}

/**
 * The break's one honest line on the clock (FLOW §2.3, as cured
 * by §C-3): during a Safe Break the banner says the doors are
 * open and until when, in wall time (A-11(c)'s one sentence,
 * room side). Not in a break: null.
 *
 * Wave-2 fix (Salz C-18): the merged code printed a countdown
 * ("n MIN LEFT") while this comment claimed a wall-time UNTIL.
 * There is no stored break-end field on the row — the end is
 * DERIVED at the derivation's instant (now + the remaining
 * seconds the row does carry), which is the same instant the
 * shade's countdown reads, so the two surfaces cannot drift
 * apart by more than the beat that separates them. The code
 * now does what the comment always said.
 */
internal fun deskClockBreakLine(
    isInSafeBreak: Boolean,
    safeBreakSecondsRemaining: Int,
    nowWallMillis: Long
): String? {
    if (!isInSafeBreak) return null
    val breakEndWall = nowWallMillis + safeBreakSecondsRemaining.coerceAtLeast(0) * 1000L
    return "BREAK — PEEK APPS OPEN · UNTIL " + wallClockTimeText(breakEndWall)
}

/**
 * The desk clock's banner text (Truth Car 3, appendix / FLOW
 * FL-06) under A-12 / C-2, answered from the code: L7's fallback
 * chain is task → intention → routine name → absent. The chain
 * gains its truer floor at this merge: a hand-started session
 * with neither task nor intention still carries its loadout —
 * profileName is stamped on every row at birth (the seal's
 * snapshot; routine rows wear "Routine: <name>", hand rows the
 * loadout's name) — so the floor is the row's own profile name,
 * stripped of the routine prefix. Anonymity now stands only
 * when even that is blank, which no arming path produces.
 */
internal fun deskClockBannerText(
    taskTitle: String?,
    intention: String?,
    profileName: String? = null
): String? =
    taskTitle?.takeIf { it.isNotBlank() }
        ?: intention?.takeIf { it.isNotBlank() }
        ?: profileName
            ?.removePrefix("Routine:")
            ?.trim()
            ?.takeIf { it.isNotBlank() }

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
    onTake2MinutePeek: () -> Unit
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

