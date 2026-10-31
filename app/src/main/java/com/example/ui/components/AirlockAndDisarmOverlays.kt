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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.BatterySnapshot
import com.example.data.CapturedNotificationEntity
import com.example.data.ClockText
import androidx.compose.ui.platform.LocalContext
import com.example.data.NightDimMath
import com.example.ui.nightDesaturate
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
import com.example.ui.taperWallSeconds
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
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import com.example.ui.theme.NightColorOnPrimaryContainer
import com.example.ui.theme.NightColorSecondaryContainer
import com.example.ui.theme.NightTextColorPrimary
import com.example.ui.theme.PillBadgeTextStyle
import com.example.ui.theme.PrimaryButtonTextStyle
import com.example.ui.theme.SoraFamily
import kotlinx.coroutines.launch

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

// FLOW §2.4.4 — the light cure, in constants (Salz's desk,
// plan/REFINE-salz-aod.md §§1–3). Nacht's ceiling for the night
// face is 0.25 relative luminance; NightColorPrimary (#FFE4CF)
// prints 0.8125 at full strength — a 3.3× breach on a face that
// now raises ITSELF and may stand all night. Composited over
// the overlay's true-black ground: the content prints at 0.55
// land at ≈0.216, and the exit line at 0.61 lands at ≈0.259
// (Finisher cure, Kante K-5: the exit ceiling was raised from
// 0.58 so the door invariant holds at standing too — see the
// A-2 note below; the one verb line runs ~4% over Nacht's 0.25
// ceiling, the priced cost Samt's D-4 recommended paying).
// The auto-raise ships only under this cure; these two numbers
// are the cure.
private const val DESK_CLOCK_PRINT_ALPHA = 0.55f
private const val DESK_CLOCK_EXIT_ALPHA = 0.61f

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
// FALSE at the standing alphas under the old 0.58 ceiling —
// 0.58 / 0.55 = 1.055 < 1.10, so the cap bound wherever the
// hero stood above ≈ 0.527 (fraction ≲ 0.12, the 60 s
// suspension, and the dawn's return).
// Finisher cure (Kante K-5, Samt D-4's priced cure): the
// ceiling is now DESK_CLOCK_EXIT_ALPHA = 0.61 = 0.55 × 1.10
// rounded up, so the cap never binds below the ratio and the
// invariant holds at EVERY fraction by construction, as the
// audit's ruled form requires. Cost, named: the exit line
// lands at ≈ 0.259 relative luminance, ~4% over Nacht's 0.25
// night ceiling — the door outranks the ceiling (Samt D-4).
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

// Wave-3 (Schliff F-4): the banner slot's measure is DERIVED,
// never a magic height — two lines at this explicit line
// height, plus the slot's 12 dp vertical padding each side
// (the padding lives in the slot's modifier below).
private val BANNER_LINE_HEIGHT = 19.sp

/**
 * 1. ALWAYS-ON NIGHTSTAND DESK CLOCK OVERLAY (Decision #8)
 */
@Composable
fun AlwaysOnDeskClockOverlay(
    visible: Boolean,
    activeSession: FocusSessionEntity?,
    activeTask: FocusTaskEntity?,
    // Wave-3 (Glaette G-6): dialMinutes is DELETED from the
    // face's contract. The idle lie (ST-01 / FL-03) ran on this
    // wire; the print has not read it since the merge, and a
    // finished signature does not keep a killed lie's dead
    // input for the next hand to reconnect. The VM's own
    // _dialMinutes flow (the composer's) is untouched.
    burnInStep: Int,
    entryCause: DeskClockEntryCause = DeskClockEntryCause.HAND,
    exitIsFinish: Boolean = false,
    surrenderFade: Boolean = false,
    // Wave-2 (Samt D-1): the descent belongs to the SLEEP span
    // alone; the VM derives this from the stand-down's own
    // isSleepLoadout path and hands it in. Default false: a
    // caller that does not know keeps the face at standing.
    descentApplies: Boolean = false,
    // Wave-3 (Schimmer FE-2): the VM's raise generation + the
    // seeded-alpha fact. The animatables below are keyed on
    // the generation and SEEDED from the fact, so a SEEDED
    // raise's first composition already carries 1f.
    raiseGeneration: Int = 0,
    entryStartsAtFull: Boolean = false,
    // Wave-3 (Daemmer DAE-8 / Glanz G-2): the finish ground
    // releases when the VM's issuance event flips this —
    // the fixed 1,200 ms hold is now only a 2,200 ms failsafe.
    groundReleased: Boolean = false,
    // Wave-3 (Glanz G-1 / Kante K-2): the yield is its own
    // exit — no animation, no throw, no resurrection.
    exitIsYield: Boolean = false,
    // Wave-3 (Glanz G-3): a branch-U finish hides AFTER its
    // publish, with this snap exit.
    exitSnap: Boolean = false,
    // Issue #143 F1/F5 (main): the break-dim depth at this
    // instant, fed by the host from the shield's published
    // phase — the sealed face drains toward monochrome on the
    // same curve as the dim.
    dimAlpha: Float = 0f,
    onDismiss: () -> Unit
) {
    // A-4 — arrival and exit, keyed to cause. Ground and print
    // animate separately: the ground is true black, the print
    // lands on completed black and never blends over a lit Deck.
    // Wave-3 (Schimmer FE-2): seeded by construction — keyed on
    // the raise generation, initialised at 1f for a SEEDED
    // raise. The wave-1/2 form was born at 0f for every cause
    // and reached SEEDED's 1f only through this effect's
    // snapTo: frame one held by effect scheduling, not by
    // construction.
    val groundAlpha = remember(raiseGeneration) {
        Animatable(if (entryStartsAtFull) 1f else 0f)
    }
    val printAlpha = remember(raiseGeneration) {
        Animatable(if (entryStartsAtFull) 1f else 0f)
    }

    // Main's premise scaffolding (ported per M-A3 S12 — premise
    // law): the battery read (30s cadence), the keep-screen-on
    // flag under the #122 battery law, the 5% nightstand
    // brightness (#116), and show-when-locked + turn-screen-on
    // while — and only while — the clock shows (#111). Without
    // it the clock dies to the screen timeout and holds no
    // keyguard presence.
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
    val keepScreenOn = batterySnap?.let { deskClockKeepsScreenOn(it) } ?: true
    DisposableEffect(visible, keepScreenOn) {
        var ctx: android.content.Context? = clockView.context
        var activity: android.app.Activity? = null
        while (ctx is android.content.ContextWrapper) {
            if (ctx is android.app.Activity) { activity = ctx; break }
            ctx = ctx.baseContext
        }
        val window = activity?.window
        if (keepScreenOn) {
            window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
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
    LaunchedEffect(visible, entryCause, raiseGeneration) {
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
            when {
                exitIsYield -> {
                    // EXIT, YIELD (wave-3, Glanz G-1 / Kante K-2):
                    // the yield animates NOTHING. The print has
                    // already faded in its own curve (the
                    // surrender fade); the ground holds black.
                    // There is no dismissal throw and no
                    // resurrection — the composition below
                    // returns early on this flag and the host's
                    // yielded black surface holds the room.
                }
                exitSnap -> {
                    // EXIT, BRANCH-U FINISH (wave-3, Glanz G-3):
                    // the receipt was published BEFORE this hide
                    // and already stands (or is filed) — the
                    // ground leaves instantly, in the same order.
                    groundAlpha.snapTo(0f)
                    printAlpha.snapTo(0f)
                }
                exitIsFinish -> {
                    // EXIT, FINISH (A-4): print fades 260 Curve A
                    // while the true-black ground HOLDS over the
                    // Deck through the detention; the ground fades
                    // 260 Curve A as the receipt fades in. No
                    // surface brighter than the clock stands
                    // between the clock and the receipt (item 19,
                    // extended).
                    // Wave-3 (Daemmer DAE-8 / Glanz G-2): the
                    // ground now releases on the VM's ISSUANCE
                    // event (the groundReleased effect below),
                    // not on the wave-1 fixed 1,200 ms clock —
                    // that clock left 20–340 ms of uncovered,
                    // unpapered Deck inside the seam (issuance
                    // runs to 1,800 ms on the faulted branch).
                    // This launch is only the FAILSAFE: 2,200 ms,
                    // so a halted chain can never trap the face
                    // under black.
                    launch { printAlpha.animateTo(0f, tween(260, easing = MonolithCurves.EasingA)) }
                    launch {
                        groundAlpha.animateTo(
                            0f,
                            tween(260, delayMillis = 2200, easing = MonolithCurves.EasingA)
                        )
                    }
                }
                else -> {
                    // EXIT, DISMISSAL: 180 EasingThrow, ground and
                    // print together (item 17 stands). Wave-2
                    // (C-15): "together" is now literally true —
                    // parallel launches, not two serial 180 ms
                    // fades.
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
    }
    // Wave-3 (DAE-8): the issuance event itself releases the
    // finish ground — 260 Curve A, the moment the VM's publish
    // lands, for standing and filed issuances alike.
    LaunchedEffect(groundReleased, visible, exitIsFinish) {
        if (groundReleased && !visible && exitIsFinish) {
            groundAlpha.animateTo(0f, tween(260, easing = MonolithCurves.EasingA))
        }
    }
    // A-10(v): the surrender fade — at true-off − 520 ms the
    // print fades 520 Curve C to 0 over the black ground.
    // Wave-3 (Kante K-2): on the yield beat the flag is never
    // reset (the VM holds it), and this effect must not snap
    // back either — when the hide is a yield, surrenderAlpha
    // HOLDS 0. The snap-to-1 arm fires only on a raise / a
    // cancelled surrender.
    val surrenderAlpha = remember { Animatable(1f) }
    LaunchedEffect(surrenderFade, exitIsYield) {
        if (surrenderFade) {
            surrenderAlpha.animateTo(0f, tween(520, easing = MonolithCurves.EasingC))
        } else if (!exitIsYield) {
            surrenderAlpha.snapTo(1f)
        }
    }
    // Wave-3 (K-1/G-1): while yielded the face composes
    // nothing — the host's true-black surface stands over the
    // Deck in its place.
    if (!visible && exitIsYield) return
    if (!visible && groundAlpha.value <= 0f && printAlpha.value <= 0f) return

    // FLOW §2.4.2 — the idle lie dies (ST-01 / FL-03). The hero
    // is derived by deskClockHeroText: a live session reads its
    // span; no live session, the hero is the CURRENT TIME,
    // labelled THE TIME NOW directly above it (K-8).
    val nowWallMillis = System.currentTimeMillis()
    val running = activeSession != null && activeSession.isActive

    // Wave-3 (Glaette G-3): the idle hero's repaint is aligned
    // to the WALL MINUTE. Idle has no session row to re-emit,
    // and the only other guaranteed input (burnInStep) ticks
    // on the VM ticker's birth phase — so the idle hero could
    // read a minute stale for up to ~60 s of every minute. A
    // minute-boundary clock inside the composition now drives
    // the idle derivations; running faces keep reading the
    // row-driven instant.
    var idleNowMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(visible, running) {
        if (visible && !running) {
            while (true) {
                val now = System.currentTimeMillis()
                idleNowMillis = now
                // Sleep to 50 ms past the next wall-minute turn.
                delay(60_000L - (now % 60_000L) + 50L)
            }
        }
    }
    val faceNowMillis = if (running) nowWallMillis else idleNowMillis
    val timeText = deskClockHeroText(activeSession, faceNowMillis)

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
    // Wave-3 (Daemmer DAE-2, lead's merge ruling: DAE-2's form
    // lands): the composed alphas are ANIMATED, not assigned.
    // The pure function still computes the targets exactly as
    // before (its signature is unchanged); three
    // animateFloatAsState chases on 520 Curve C carry them, so
    // the dawn ramp's per-second retargets move as light, not
    // as a brightness setting being stepped — and the 60 s
    // suspension's resume (Samt D-3 / Kante K-7's one-frame
    // curtain-drop) is absorbed by the same chase: when the
    // suspension lifts, the target falls to the descended
    // value and the animation walks it down over 520 Curve C
    // instead of cutting. ONE of the two priced forms lands —
    // this one; D-3's resumeFraction input is not also built.
    val targetAlphas = deskClockPrintAlphas(
        spanElapsedFraction = spanElapsedFraction,
        descentSuspended = descentSuspended,
        // Wave-2 (DAE-1): the dawn ramp keys to the same end
        // the hero and ENDS read — the window's end for a
        // routine row, the span's remaining otherwise.
        spanRemainingSeconds = activeSession?.let { s ->
            if (s.isStopwatchMode) s.spanRemainingSeconds
            else deskClockRemainingSeconds(s, faceNowMillis)
        },
        isStopwatch = activeSession?.isStopwatchMode == true
    )
    val heroAlphaAnim by animateFloatAsState(
        targetValue = targetAlphas.hero,
        animationSpec = tween(520, easing = MonolithCurves.EasingC),
        label = "desk_clock_hero_alpha"
    )
    val numberAlphaAnim by animateFloatAsState(
        targetValue = targetAlphas.numberLine,
        animationSpec = tween(520, easing = MonolithCurves.EasingC),
        label = "desk_clock_number_alpha"
    )
    val exitAlphaAnim by animateFloatAsState(
        targetValue = targetAlphas.exit,
        animationSpec = tween(520, easing = MonolithCurves.EasingC),
        label = "desk_clock_exit_alpha"
    )
    val alphas = DeskClockAlphas(
        hero = heroAlphaAnim, exit = exitAlphaAnim, numberLine = numberAlphaAnim
    )

    // Wave-3 (Glaette G-1 + G-2): the burn-in glide is measured
    // in DP and TRAVELS. The wave-1/2 code spent the offsets as
    // IntOffset PIXELS (3 px ≈ 1 dp on his phone — the grid ran
    // at a third of its ruled amplitude) and recomposed at the
    // new position instantly. Now: dx/dy are dp counts on the
    // ruled 3 dp step / ±6 dp grid, converted at the density,
    // and an animateIntOffsetAsState carries the column over
    // 520 Curve C — the room's one repeated motion moves like
    // light, not like a setting being flipped.
    val dx = ((burnInStep % 5) - 2) * 3
    val dy = (((burnInStep / 5) % 5) - 2) * 3
    val density = LocalDensity.current
    val glide by animateIntOffsetAsState(
        targetValue = IntOffset(
            with(density) { dx.dp.roundToPx() },
            with(density) { dy.dp.roundToPx() }
        ),
        animationSpec = tween(520, easing = MonolithCurves.EasingC),
        label = "desk_clock_burn_in_glide"
    )

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
                .offset { glide }
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
                // K-8 / DESIGN §1: the label and the figure are
                // inseparable in EVERY state — LEFT (timed),
                // ELAPSED (stopwatch), THE TIME NOW (idle).
                // Finisher cure (Politur P-1): the merged code
                // printed the label only when idle, so a running
                // hero was an unlabelled number all night.
                Text(
                    text = deskClockHeroLabel(activeSession),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    color = NightColorPrimary.copy(alpha = alphas.numberLine)
                )
                // Hero architectural Space Mono numerals (96sp)
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
                        text = deskClockSubLine(activeSession!!, faceNowMillis),
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
                    // Wave-3 (Schliff F-3): the line's SEAT is
                    // reserved — a fixed-height box stands
                    // whenever a timed multi-block span runs, and
                    // the text prints inside it only when the
                    // derivation returns non-null. A-5 cured this
                    // jump class for the banner slot; A-1's
                    // suppression re-opened it one seat up: at
                    // every break start/end the tear rule and
                    // banner jumped ~one line in 0 ms. Single-
                    // block, stopwatch and idle faces compose no
                    // seat, as before.
                    if (!activeSession!!.isStopwatchMode &&
                        activeSession!!.totalStudyBlocks > 1
                    ) {
                        Box(modifier = Modifier.height(17.dp)) {
                            deskClockBlockLine(activeSession!!, faceNowMillis)?.let { blockLine ->
                                Text(
                                    text = blockLine,
                                    fontFamily = AnonymousProFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    lineHeight = 17.sp,
                                    letterSpacing = 2.sp,
                                    color = NightColorPrimary.copy(alpha = alphas.numberLine)
                                )
                            }
                        }
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
                            // Wave-3 (Schliff F-5): the dash is
                            // measured in DP like the notch beside
                            // it — the wave-1/2 raw floats were
                            // PIXELS (≈2.7 dp on xxhdpi), one rule
                            // in two unit systems. Phase stays 0f
                            // (Mond J3's no-doubling condition).
                            floatArrayOf(8.dp.toPx(), 6.dp.toPx()), 0f
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
            // the text it actually holds, and the text carries a
            // LAW for line 3: maxLines 2 + ellipsis. A long task
            // title truncates by decision, never clips in
            // silence. Wave-3 (Schliff F-4): the measure is now
            // an explicit line height the height derives from —
            // see BANNER_LINE_HEIGHT at the slot.
            if (running) {
                // The break line, kept by name (composition, K8):
                // when the banner IS the break line, the banner
                // carries the break line's tag — both parents'
                // DeskClockLivedTest reads the announcement by it.
                val breakLine = deskClockBreakLine(
                    isInSafeBreak = activeSession?.isInSafeBreak == true,
                    safeBreakSecondsRemaining = activeSession?.safeBreakSecondsRemaining ?: 0,
                    nowWallMillis = faceNowMillis
                )
                var bannerLine = breakLine
                // Finisher cure (Kante K-4): on the SLEEP span the
                // banner IS "SLEEP" (DESIGN §1.7 — the routine
                // row's own name; no task/intention derivation
                // stands in front of it). The old chain ran
                // task-first, and the VM's activeTask falls back
                // to the first QUEUED task — so on any night with
                // a task queued for tomorrow, the SLEEP face named
                // that task instead. descentApplies is the VM's
                // isSleepLoadout derivation for this very row.
                // (SLEEP has no breaks — Moench MA-1 — so the
                // break line above never pre-empts this in
                // practice; the order here keeps it sovereign
                // anyway.)
                if (bannerLine == null && descentApplies) bannerLine = "SLEEP"
                if (bannerLine == null) {
                    bannerLine = deskClockBannerText(
                        activeTask?.title, activeSession?.intention, activeSession?.profileName
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Wave-3 (Schliff F-4): the height is
                        // DERIVED from the text's own measure —
                        // two lines at BANNER_LINE_HEIGHT (19 sp)
                        // + the 12 dp vertical padding each side
                        // below. The wave-2 68.dp (and the merged
                        // 62.dp before it) were asserted constants
                        // that matched no derivation from the text
                        // actually set.
                        .height(with(density) { (BANNER_LINE_HEIGHT * 2).toDp() } + 24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            // Finisher cure (Kante K-6, Samt D-5):
                            // the slab descends with the hero — A-2
                            // is dim BY AREA, and at full strength
                            // this card was the brightest, largest
                            // lit object on the descended face.
                            // 1.0 at standing, ≈ 0.65 at the floor.
                            if (bannerLine != null) {
                                NightColorSurfaceBright.copy(
                                    alpha = alphas.hero / DESK_CLOCK_PRINT_ALPHA
                                )
                            } else {
                                Color.Transparent
                            }
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
                            modifier = if (breakLine != null && bannerLine == breakLine) {
                                Modifier.testTag("desk_clock_break_line")
                            } else {
                                Modifier
                            },
                            // Schliff F-4: the text carries the
                            // measure the slot derives from.
                            lineHeight = BANNER_LINE_HEIGHT,
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
 * Issue #143 F2 — the desk clock's eyebrow: the face NAMES its
 * state. Idle keeps the standing nightstand line; sealed, the
 * eyebrow says SEALED (and names a running Safe Break), so the
 * two states can never be mistaken for each other across a dark
 * room. Pure — unit-tested.
 */
internal fun deskClockEyebrow(sealed: Boolean, inBreak: Boolean): String = when {
    sealed && inBreak -> "SEALED — SAFE BREAK • HOLD ANYWHERE TO EXIT"
    sealed -> "SEALED — NIGHT FACE • HOLD ANYWHERE TO EXIT"
    else -> "NIGHTSTAND DESK CLOCK • HOLD ANYWHERE TO EXIT"
}

/**
 * Issue #143 F2 — the desk clock's state lines. Sealed and timed:
 * BOTH numbers, elapsed and remaining, from the span derivation
 * the hero already trusts. Sealed stopwatch: elapsed alone — a
 * stopwatch has no remaining and none is invented. Idle: the one
 * honest line, that this is the dial and not a session — no
 * session numbers are fabricated for a session that does not
 * exist. Pure — unit-tested.
 */
internal fun deskClockSealedLines(
    sealed: Boolean,
    elapsedSeconds: Int,
    remainingSeconds: Int?
): List<String> {
    if (!sealed) return listOf("NO SESSION SEALED — THIS IS THE DIAL, NOT A SESSION")
    val elapsed = String.format(
        Locale.US, "ELAPSED %02d:%02d",
        elapsedSeconds.coerceAtLeast(0) / 60, elapsedSeconds.coerceAtLeast(0) % 60
    )
    if (remainingSeconds == null) return listOf(elapsed)
    val remaining = String.format(
        Locale.US, "REMAINING %02d:%02d",
        remainingSeconds.coerceAtLeast(0) / 60, remainingSeconds.coerceAtLeast(0) % 60
    )
    return listOf(elapsed, remaining)
}

/**
 * Issue #143 F3 — the sealed Yield sheet's own answer. The sheet
 * that stands when a held app is tapped used to speak only in
 * generic wall words; it now answers IN THE SEALED STATE'S OWN
 * WORDS: which block holds the phone, or that a Safe Break is
 * running and how much quiet is left, and that the tapped app
 * stays held either way. A session with no blocks to name
 * (stopwatch) falls back to the session itself — never an
 * invented block. Pure — unit-tested.
 */
internal fun sealedYieldLine(
    appName: String,
    inBreak: Boolean,
    breakRemainingSeconds: Int,
    blockIndex: Int,
    blockCount: Int
): String {
    val app = appName.ifBlank { "THIS APP" }.uppercase(Locale.US)
    return when {
        inBreak -> {
            val left = breakRemainingSeconds.coerceAtLeast(0)
            String.format(
                Locale.US,
                "SEALED — SAFE BREAK RUNNING · %02d:%02d OF QUIET LEFT · %s STAYS HELD UNTIL THE NEXT BLOCK SEALS",
                left / 60, left % 60, app
            )
        }
        blockCount > 0 && blockIndex > 0 ->
            "SEALED — BLOCK $blockIndex OF $blockCount · $app STAYS HELD UNTIL THIS BLOCK ENDS"
        else -> "SEALED — $app STAYS HELD UNTIL THE SESSION ENDS"
    }
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

/**
 * Audit R6 #340 F17: the ESCALATING wall. The pre-cure wall was a
 * fixed 5 seconds for the first reach and the hundredth alike.
 * The price now doubles per prior reach at the same package
 * TODAY (the DrawerDay filing day, counted from the persisted
 * intercept log — so the price survives process death and can
 * never reset mid-day): 5, 10, 20, 40, 80, then capped at 120.
 * Locally rendered from the session's own record; no media, no
 * network, nothing to degrade.
 */
internal fun escalatedAirlockWallSeconds(priorReachesToday: Int, baseSeconds: Int = 5): Int {
    // The base is the configured breath length (issue #318 F4:
    // AirlockBreathStore, 5 by Decision #16, at most 30); the
    // ladder doubles FROM it, capped at 120.
    var wall = baseSeconds.coerceAtLeast(1)
    repeat(priorReachesToday.coerceIn(0, 10)) {
        wall = (wall * 2).coerceAtMost(120)
    }
    return wall.coerceAtMost(120)
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

/**
 * The door's second memory (issue #264, B-264-25): how long
 * since THIS APP was last reached for, read from the persisted
 * intercept log — the count line says how often this session;
 * this line says how long the urge waited between tries, which
 * is the self-knowledge the moment can actually teach from.
 * Prints whenever a prior reach exists in the log, even on a
 * session's first visit to this door. No prior reach (0) prints
 * nothing: the door claims no memory it doesn't have.
 */
internal fun doorLastReachLine(lastReachAtMillis: Long, nowMillis: Long): String? {
    if (lastReachAtMillis <= 0L) return null
    val agoSeconds = ((nowMillis - lastReachAtMillis) / 1000L).coerceAtLeast(0L)
    return when {
        agoSeconds < 60L -> "LAST REACH UNDER A MINUTE AGO"
        agoSeconds < 3_600L -> "LAST REACH ${agoSeconds / 60L} MIN AGO"
        agoSeconds < 86_400L -> "LAST REACH ${agoSeconds / 3_600L} H AGO"
        else -> "LAST REACH ${agoSeconds / 86_400L} D AGO"
    }
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

internal fun peekBadgeText(durationSeconds: Int): String {
    // FIX-8 (issue #11): the badge derives honestly — 90s prints
    // "1m30s", never a rounded "1m" the lease does not grant.
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

/**
 * Issue #125 (armed-wrong F5 remainder, pure, unit-tested): the
 * Airlock's grace window, derived — never counted. The state
 * carries the session's verified elapsed at the intercept instant;
 * the live elapsed is that snapshot aged by the monotonic delta
 * from the intercept's anchor (the overlay's resolved intercept
 * instant — the same anchor its wall counts from), so the door's
 * print can never drift from the repository's own grace law
 * ([MonolithRepository.GRACE_WINDOW_SECONDS], verified elapsed
 * ≤ window). Returns the badge seconds the user can still use —
 * the Deck chip's own formula (window + 1 − elapsed, coerced to
 * 1..window: 10 on the first usable frame, 1 on the last) — or
 * null when no abort stands: no session snapshot carried
 * (elapsedAtIntercept < 0), or the window is already spent.
 */
internal fun airlockGraceBadgeSeconds(
    elapsedAtInterceptSeconds: Int,
    interceptedAtMonoMillis: Long,
    nowMonoMillis: Long,
    windowSeconds: Int = MonolithRepository.GRACE_WINDOW_SECONDS
): Int? {
    if (elapsedAtInterceptSeconds < 0) return null
    val agedSeconds = ((nowMonoMillis - interceptedAtMonoMillis)
        .coerceAtLeast(0L) / 1000L).toInt()
    val liveElapsed = elapsedAtInterceptSeconds + agedSeconds
    if (liveElapsed > windowSeconds) return null
    return (windowSeconds + 1 - liveElapsed).coerceIn(1, windowSeconds)
}

/**
 * Issue #125: the door's grace chip words. A routine-born session
 * never takes the free path — its tap is booked by the routine's
 * judging funnel — so its chip carries the Deck's judged label,
 * never the free abort's promise. Every other session in its
 * window carries the identical words the Deck and off-Deck chips
 * carry: one door, one name.
 */
internal fun airlockGraceChipLabel(sessionIsRoutine: Boolean): String =
    if (sessionIsRoutine) "END ROUTINE · WILL BE JUDGED" else "GRACE WINDOW ABORT"

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
    // Issue #125 (armed-wrong F5 remainder): the grace abort, at
    // the door. Inside the session's opening window the free exit
    // stands on the Airlock too — the same call the Deck's chip
    // makes — so checking a blocked app no longer burns the
    // window invisibly. Outside the window the chip does not
    // render at all; the valve below remains the priced door.
    onGraceAbort: () -> Unit = {},
    // Issue #11 (FIX-8): the honest exit — a quiet text row under
    // RETURN that routes to the door-originated Disarm entry. It
    // ends nothing itself; the Disarm face carries the gravity.
    onEndSessionInstead: () -> Unit = {},
    // Issue #11 (FIX-7): reports the arm instant (the wall's
    // expiry with the offer standing) so the VM can sound the
    // arm voice once per intercept.
    onOfferArmed: () -> Unit = {},
    // Issue #318 F4: the breathing wall's length is a stored,
    // per-configuration value (AirlockBreathStore), no longer a
    // composable-local literal. Default = Decision #16's 5 seconds.
    breathSeconds: Int = com.example.data.AirlockBreathStore.DEFAULT_SECONDS,
    // Issue #517 (4-A): the SET peek length, from the same Guard
    // row the lease reads — the offer's preview, badge and label
    // print it, never the retired PEEK_DURATION_SECONDS constant
    // (the EditableTimes seam's §4.3 law). Default 2 = the
    // settings default, so render harnesses print the shipped
    // truth.
    peekMinutes: Int = 2
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
        // full wall and drift can never stretch it. The wall's LENGTH
        // is the configured breath (issue #318 F4, [breathSeconds])
        // escalated per prior reach today (Audit R6 #340 F17): the
        // ladder doubles from the breath base, capped at 120 —
        // state.priorReachesToday carries the ViewModel's count.
        val effectiveWallSeconds =
            escalatedAirlockWallSeconds(state.priorReachesToday, breathSeconds)
        val wallStartMono = remember(state.interceptInstanceId) {
            state.interceptedAtMonoMillis.takeIf { it > 0L } ?: SystemClock.elapsedRealtime()
        }
        var countdownSecs by remember(state.interceptInstanceId) {
            mutableIntStateOf(
                airlockCountdownRemaining(wallStartMono, SystemClock.elapsedRealtime(), effectiveWallSeconds)
            )
        }
        // Issue #125 (F5 remainder): the grace chip's clock. The
        // window is only 10 seconds, so while an abort could stand
        // this face re-derives it four times a second from the
        // monotonic anchor — the badge steps down per second and
        // the chip vanishes on the first frame past the window,
        // never lingering as a stale promise. The loop ends the
        // moment no abort can stand; it never ticks a counter of
        // its own (the derivation is airlockGraceBadgeSeconds).
        var graceNowMono by remember(state.interceptInstanceId) {
            mutableLongStateOf(SystemClock.elapsedRealtime())
        }
        // The anchor is wallStartMono — the overlay's own resolved
        // intercept instant (the state's monotonic stamp, or the
        // first-frame fallback the wall itself uses): live-trigger
        // states carry no stamp, and ageing from 0 would read as
        // an aeon and stand no abort at all.
        LaunchedEffect(state.interceptInstanceId, state.isVisible) {
            while (
                airlockGraceBadgeSeconds(
                    state.sessionElapsedSecondsAtIntercept,
                    wallStartMono,
                    graceNowMono
                ) != null
            ) {
                delay(200L)
                graceNowMono = SystemClock.elapsedRealtime()
            }
        }
        val graceBadgeSeconds = airlockGraceBadgeSeconds(
            state.sessionElapsedSecondsAtIntercept,
            wallStartMono,
            graceNowMono
        )
        LaunchedEffect(state.interceptInstanceId, state.isVisible) {
            countdownSecs =
                airlockCountdownRemaining(wallStartMono, SystemClock.elapsedRealtime(), effectiveWallSeconds)
            while (countdownSecs > 0) {
                delay(200L)
                countdownSecs =
                    airlockCountdownRemaining(wallStartMono, SystemClock.elapsedRealtime(), effectiveWallSeconds)
            }
            // Issue #11 (FIX-7): the wall has expired — report the
            // arm instant; the VM sounds the arm voice once per
            // intercept instance, and only if the offer stands.
            if (state.canUse2MinutePeek) onOfferArmed()
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

                // Issue #11 (FIX-8): the door's memory — this
                // visit's ordinal, THIS SESSION only, printed
                // only when a prior booking exists.
                doorMemoryLineText(state.priorInterceptsThisSession)?.let { line ->
                    Text(
                        text = line,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontSize = 12.5.sp,
                        color = NightColorPrimary.copy(alpha = 0.8f)
                    )
                }

                // Issue #264 (B-264-25): the door's second
                // memory — how long since this app was last
                // reached for, from the persisted intercept log.
                // Prints whenever a prior reach exists, even on
                // this session's first visit to the door.
                doorLastReachLine(state.lastReachAtMillis, System.currentTimeMillis())?.let { line ->
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
                // Issue #143 F3: the sealed state's own answer —
                // the sheet the held app's tap raises now speaks
                // AS the seal: which block holds the phone, or
                // that a Safe Break is running and how much quiet
                // is left, and that this app stays held either
                // way. Before this the sheet answered a sealed
                // tap with generic wall words only.
                Text(
                    text = sealedYieldLine(
                        appName = state.interceptedAppName,
                        inBreak = state.sessionInSafeBreak,
                        breakRemainingSeconds = state.safeBreakSecondsRemaining,
                        blockIndex = state.sessionBlockIndex,
                        blockCount = state.sessionBlockCount
                    ),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = NightColorPrimary.copy(alpha = 0.9f),
                    modifier = Modifier.testTag("airlock_sealed_yield_line")
                )

                // Hero Architectural Countdown Numeral (Space Mono)
                // Issue #55: the transmute pulse is WIRED, not dead —
                // tapping the numeral during the wall fires it (the
                // ViewModel answers with a pitch-rising detent and
                // counts the pulse). It grants nothing and shortens
                // nothing; it is the wall's one permitted fidget.
                Text(
                    // Audit R6 #340 F17: the escalated wall runs to
                    // 120 — the numeral zero-pads to two, never
                    // prefixes a fixed "0" onto a 3-digit count.
                    text = if (countdownSecs > 0) String.format(Locale.US, "%02d", countdownSecs) else "00",
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

                // Issue #11 (FIX-8): THE HONEST EXIT — one quiet
                // bare-text line under RETURN. It ends nothing,
                // spends nothing, books nothing — it routes to the
                // door-originated Disarm entry, which carries the
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

                // F60 composition (Audit R6 #341, recomposed
                // FIX-R3): main's chip-from-the-first-frame stands
                // INSIDE the unified zone below — the zone's fixed
                // height keeps RETURN stationary (FIX-6) while the
                // chip is present and scrollable at every second
                // (F60). Both mechanisms, one slot.
                // THE UNIFIED ZONE (issue #11 FIX-6, composed with
                // #114/#102 at the main merge): everything the door
                // may print under the honest exit lives in ONE
                // fixed-height slot — the #114 exchange preview
                // during the breath, the offer at zero, the #102
                // withheld reason from the first second — so the
                // centred card's height, and RETURN's place in it,
                // never changes between the counting, offer and
                // held frames (the invariant
                // HeldDoorStationarityTest guards). The slot is
                // sized for its tallest tenant, the four-line
                // preview stack; shorter tenants stand at its top.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(224.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Issue #114, finding 3 + Audit R6 #341 F60
                    // (composed FIX-R3): inside the zone the offer
                    // is composed from the FIRST frame — the preview
                    // line stands during the breath, the chip itself
                    // is present from frame one (dimmed, its tap
                    // gated to zero), and the price, scope and terms
                    // print once with the chip — so the whole
                    // exchange is readable during the countdown, the
                    // chip can be scrolled to on a short screen at
                    // any second, and nothing is revealed by the
                    // wall reaching zero. The zone's fixed height
                    // keeps RETURN's bounds invariant across frames.
                    if (peekReady) {
                        if (countdownSecs > 0) {
                            Text(
                                text = peekOfferPreviewText(peekMinutes * 60),
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontSize = 12.5.sp,
                                color = NightColorPrimary.copy(alpha = 0.8f)
                            )
                        }
                        BlockitActionChip(
                            wordLabel = "$peekMinutes-MINUTE PEEK",
                            // Issue #114, finding 5: this door is
                            // NOT an emergency — it is the routine,
                            // repeatable, cooldown-priced peek. The
                            // EMERGENCY name belongs only to the
                            // session-ending weekly break.
                            // SIL-2 / W-29: the badge is the SET
                            // value — since issue #517 (4-A) it is
                            // the Guard row's peekMinutes passed to
                            // this overlay, not the retired constant:
                            // the label and badge print the length
                            // the lease will actually grant.
                            monoBadge = peekBadgeText(peekMinutes * 60),
                            onClick = { if (countdownSecs == 0) onTake2MinutePeek() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (countdownSecs == 0) Modifier else Modifier.alpha(0.45f)),
                            testTag = "airlock_peek_button"
                        )
                        // SIL-2 / W-29: the price stands on the
                        // offer, before the peek is taken — the
                        // cooldown the repository will arm, named
                        // in full.
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
                        // Issue #102, finding 5: the terms stand
                        // on the offer itself, before the tap.
                        Text(
                            text = peekTermsText(),
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 12.5.sp,
                            color = NightColorPrimary.copy(alpha = 0.8f)
                        )
                    }

                    // The withheld peek prints its state AND its
                    // reason (Held Door Truth Baseline): cooldown
                    // with its live remaining time, or the running
                    // pill named. Doors that never owed a peek carry
                    // withheld == null and print nothing here. Issue
                    // #102, finding 4: the reason is NOT gated on
                    // the breath countdown — it shows from the first
                    // second (see peekWithheldReasonVisible).
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
                                // Issue #11 (FIX-8, the Errand's
                                // kernel): when the schedule already
                                // guarantees a break that opens this
                                // app, the door prints the appointment.
                                state.nextBreakAtMillis?.let { instant ->
                                    Text(
                                        text = breakAppointmentText(instant),
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
                            // the tier and where tiers are set.
                            PeekWithheld.TIER_LOCKED -> Text(
                                text = "LOCKED TIER — NO PEEK EXISTS HERE · PEEK APPS ARE SET IN THE SHIELD",
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontSize = 12.5.sp,
                                color = NightColorPrimary.copy(alpha = 0.8f)
                            )
                            null -> Unit
                        }
                    }
                }

                // THE GRACE ABORT, AT THE DOOR (issue #125,
                // armed-wrong F5 remainder): for the session's
                // opening window only, the free exit stands above
                // the priced valve — checking what you armed wrong
                // by tapping a blocked app no longer burns the
                // window invisibly on a face with no countdown.
                // Same words, same live badge, same call as the
                // Deck's chip; a routine-born session wears the
                // Deck's judged label, never the free promise.
                if (graceBadgeSeconds != null) {
                    BlockitActionChip(
                        wordLabel = airlockGraceChipLabel(state.sessionIsRoutine),
                        monoBadge = "${graceBadgeSeconds}s",
                        isPrimaryInk = true,
                        onClick = onGraceAbort,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "airlock_grace_abort_button"
                    )
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
 * Ledger-Direct (Freya, invention 1): the valve receives NO count —
 * at 0 remaining it is simply not composed, and this overlay renders
 * the SPENT face (buildspec point 8) in its place.
 * Issue #103 F5: a refused completed hold prints its refusal
 * sentence (state.refusalMessage) on either face — never in silence.
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
    onHoldBanked: () -> Unit = {},
    onHoldStarted: () -> Unit = {},
    onCompleteCleanSession: () -> Unit = {}
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

                // Issue #103 F5: a refused hold announces itself.
                // Printed OUTSIDE the valve conditional so it shows
                // on both faces (valve composed, and SPENT at 0) —
                // the valve's own progress has already reset to
                // zero, so this sentence is the only sign the 60
                // seconds did not count.
                state.refusalMessage?.let { refusal ->
                    Text(
                        text = refusal,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NightColorPrimary,
                        modifier = Modifier.testTag("valve_refusal_message")
                    )
                }

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
                                requiredSeconds = state.requiredHoldSeconds
                                restoreBankedProgress(hoistedBankedMs)
                            }
                        },
                        update = { view ->
                            view.requiredSeconds = state.requiredHoldSeconds
                            view.onHoldBanked = onHoldBanked
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
// Wave-3 (Schliff F-4): the banner slot's measure is DERIVED,
// never a magic height — two lines at this explicit line
// height, plus the slot's 12 dp vertical padding each side
// (the padding lives in the slot's modifier below).

/** The wall clock as this app prints it everywhere else:
 *  HH:mm, 24-hour (the ENDS / WITHIN grammar). */
internal fun wallClockTimeText(wallMillis: Long): String =
    java.text.SimpleDateFormat("HH:mm", Locale.US).format(java.util.Date(wallMillis))

/**
 * Finisher cure (Politur P-2): THE one remaining-seconds
 * derivation for a timed session, shared by the hero, the
 * sub-line's last-minute promotion and the dawn ramp's key.
 * For a routine row it counts to the STAND-DOWN's instant —
 * the frozen windowEndMillis, the same derivation ENDS
 * prints (A-12 C-1) — not to the span's arithmetic: the span
 * target is built from whole FLOORED minutes at
 * materialisation, so spanRemainingSeconds hits 0 up to
 * 59 s before the window's end, every night (Daemmer DAE-1).
 * Wave-2 keyed the hero and the ramp to the window but left
 * deskClockSubLine reading spanRemainingSeconds, so inside
 * the window's true last minute NOW either dropped up to
 * ~59 s early or never dropped at all.
 */

/**
 * Finisher cure (Politur P-2): THE one remaining-seconds
 * derivation for a timed session, shared by the hero, the
 * sub-line's last-minute promotion and the dawn ramp's key.
 * For a routine row it counts to the STAND-DOWN's instant —
 * the frozen windowEndMillis, the same derivation ENDS
 * prints (A-12 C-1) — not to the span's arithmetic: the span
 * target is built from whole FLOORED minutes at
 * materialisation, so spanRemainingSeconds hits 0 up to
 * 59 s before the window's end, every night (Daemmer DAE-1).
 * Wave-2 keyed the hero and the ramp to the window but left
 * deskClockSubLine reading spanRemainingSeconds, so inside
 * the window's true last minute NOW either dropped up to
 * ~59 s early or never dropped at all.
 */
internal fun deskClockRemainingSeconds(session: FocusSessionEntity, nowWallMillis: Long): Int =
    if (session.routineId != null && session.windowEndMillis > 0L) {
        ((session.windowEndMillis - nowWallMillis) / 1000L).coerceAtLeast(0L).toInt()
    } else {
        session.spanRemainingSeconds
    }

/** K-8 / DESIGN §1: the hero's label, inseparable from the
 *  figure — LEFT over a timed remainder, ELAPSED over a
 *  stopwatch, THE TIME NOW over the idle wall clock. */

/** K-8 / DESIGN §1: the hero's label, inseparable from the
 *  figure — LEFT over a timed remainder, ELAPSED over a
 *  stopwatch, THE TIME NOW over the idle wall clock. */
internal fun deskClockHeroLabel(activeSession: FocusSessionEntity?): String =
    if (activeSession == null || !activeSession.isActive) {
        "THE TIME NOW"
    } else if (activeSession.isStopwatchMode) {
        "ELAPSED"
    } else {
        "LEFT"
    }

/**
 * The desk clock's hero (FLOW §2.4.1–2 — ST-01 / FL-03). A live
 * session reads its span: the span remaining for a timed
 * session (never the per-block remainder — it is 0 through
 * every Safe Break and this clock has no block context), the
 * elapsed for a stopwatch. NO live session: the hero is the
 * current time — never the composer's dial setting.
 */

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
        val displaySecs =
            if (activeSession.isStopwatchMode) {
                activeSession.elapsedSeconds
            } else {
                deskClockRemainingSeconds(activeSession, nowWallMillis)
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

/**
 * §C-1 / A-6(ii) — the sub-line, whole: timed sessions read
 * "NOW HH:MM · ENDS HH:MM"; in the last minute NOW drops and
 * ENDS stands alone; a stopwatch reads "NOW HH:MM" alone.
 */
internal fun deskClockSubLine(session: FocusSessionEntity, nowWallMillis: Long): String {
    if (session.isStopwatchMode) return deskClockNowLine(nowWallMillis)
    // The promotion keys to the SAME remaining the hero counts
    // (Politur P-2) — for a routine row that is the window's
    // remaining, never the floored span's.
    if (deskClockRemainingSeconds(session, nowWallMillis) in 1..60) {
        return deskClockEndsLine(session, nowWallMillis)
    }
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
 * Seconds until an anchored monotonic end, derived at the
 * frame that prints (FIX-5b): the ceiling form, the same
 * arithmetic as peekSecondsRemaining — a counter that ticks
 * itself can disagree with its anchor; this cannot. An end of
 * 0 (never anchored) or a passed end derives 0.
 */

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

/** The capture's own time, for a frozen row: HH:mm when the
 *  capture is from today, d MMM · HH:mm when it is not — yesterday's
 *  held rows legitimately sit in this morning's snapshot, and the
 *  AS OF stamp dates the snapshot, not the rows. */

const val TEAR_HAIR_FRACTION = 0.01f

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
    val rawEndWall = nowWallMillis + safeBreakSecondsRemaining.coerceAtLeast(0) * 1000L
    // Salz C-18's cured sentence, kept truthful (batch-10 fix):
    // UNTIL names the first minute mark AT OR AFTER the true end.
    // A truncating HH:mm would print the minute BEFORE the end
    // whenever seconds remain — the clock claiming the door
    // shuts earlier than it does. Exact marks print as-is.
    val rem = rawEndWall % 60_000L
    val breakEndWall = if (rem == 0L) rawEndWall else rawEndWall + (60_000L - rem)
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
