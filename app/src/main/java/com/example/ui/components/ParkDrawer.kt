package com.example.ui.components
import com.example.data.ClockText
import androidx.compose.ui.platform.LocalContext

import android.os.SystemClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.service.InterceptorState
import com.example.service.PermPrint
import com.example.service.TactileSoundEngine
import com.example.service.WatchItemId
import com.example.service.WatchSnapshot
import com.example.service.WatchStore
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AppTitleTextStyle
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.BodyTextStyle
import com.example.ui.theme.DrawerHeaderTextStyle
import com.example.ui.theme.MonolithKin
import com.example.ui.theme.PillBadgeTextStyle
import com.example.ui.theme.PrimaryButtonTextStyle
import com.example.ui.views.SledSwitchView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ============================================================================
 * PARK (chrome C4, spec §5) — the workshop drawer where the dissolved
 * Guard went. Hosted in the viewport host, never the root: it rises
 * over the current screen, under the header band, above the rail
 * frame — chrome geometry never moves for it.
 *
 * Material law (§8.10): one tone (the ground), everything print
 * except the FIX key, radii 16 (panel) and 8 (key) only, orange
 * zero. The board is nine ruled lines over the Watch Store; the
 * only object is the FIX key, present only while a permission lack
 * exists. (Truth Car 3, UNA-02, seats ONE exception on the board
 * itself: the SOCIAL SITES line carries its sled — the setting's
 * own switch on the line it governs; see ParkSocialSitesRow.)
 * Below the board: the Sound sled (the shipped control,
 * moved whole), the Shorts + Reels sled (Truth Car 3, UNA-01 —
 * the control the board's third interceptor line always implied),
 * the Theme readout (an evaluation, printed), and
 * the spec plate — furniture, byte-identical in day and night.
 *
 * Motion is KIN-DRAWER: rise 280 SEAT, leave 160 THROW, exactly two
 * rest heights. Rows are inoperable mid-rise. Dismissal has exactly
 * three inputs, all owned by the host/rail/scrim wiring: a stop
 * press or cap drag out (the rail reports it), a sheet drag past
 * 40%, and a scrim tap or PARK re-tap. While sealed the drawer —
 * if open at the seal — is read-only: print and reading, no FIX,
 * no sled — and since issue #109 (F2) it PRINTS that state at its
 * head instead of going silently inert. A second key, RE-CHECK,
 * stands where FIX stands when the only lack is a STALE reading
 * (issue #109, F3): it fires a fresh Watch sweep from the host.
 * ============================================================================
 */

/** The spec plate (§8.10, Teen's text verbatim; the LEDGER clause
 *  struck in the app by chair ruling 08:58 #12 — kept in paper,
 *  never printed here). Lines 1–2 primary ink, the rest secondary. */
private val PLATE_HEAD = listOf(
    "MONOLITH ML-01",
    "UNIT Nº 01 · BUILT FOR ONE USER · NOT FOR SALE"
)
private val PLATE_BODY = listOf(
    "WHEEL SETS TIME · SLIDE ARMS · THE HOLD BREAKS",
    "HOLD: 60 SECONDS, BANKED EVERY 15 · ONE BREAK A WEEK",
    "PEEK: 2 MINUTES, THEN SHUT AGAIN",
    "COLOUR IS A VERB: ORANGE = COMMIT · INK = SHUT · GREY = PEEK · PORCELAIN = OPEN",
    "BIND: MONOLITH IS ATTACHED · PROOF: IT JUST CHECKED · SEAL: THE SESSION IS SHUT",
    // UNA-05 (Truth Car 3, words half; Hale W-11, Una's brief):
    // the "FIRED AS SET" clause is STRUCK in this lane. It was
    // falsified on his phone (Oct 29, 20:00 SLEEP no-fire) and the
    // merged tree may print it again only when Wache's F1–F5 are in
    // the same build AND a live fire has been re-verified — that
    // restoration is a merge-time act on that evidence, not before.
    "ROUTINES: SET BY HAND ON SHIELD",
    "SITES: KEPT WITH APPS ON SHIELD · DATA: ON THIS PHONE ONLY",
    // Issue #115: the timer app is named in full — a bare acronym
    // ("YPT") sent users to a place that did not exist under any
    // name they could find; DURCH-C rebase ruling: no bare acronym
    // as a destination name, the study timer named plainly (the
    // W-43 direction on the receipt footer). The SOUND line's
    // synthesis jargon (FLOAT-PCM, 4 PARTIALS) is likewise struck
    // for plain words.
    "STUDY HOURS: IN YEOLPUMTA, THE STUDY TIMER",
    "SOUND: MADE ON THIS PHONE · POWER: YOUR PHONE"
)

/** Issue #56, finding 2: the sheet-drag dismiss law's threshold. */
internal const val PARK_DISMISS_FRACTION = 0.4f

/**
 * Issue #56, finding 2: one grip-drag step, as pure arithmetic.
 * The drag handler accumulates into a plain snapshot state on the
 * pointer thread — events arrive in order and each step reads the
 * value the previous step wrote — so the fraction the 40% law
 * judges at drag-end is never a stale Animatable read raced by
 * per-event coroutines.
 */
internal fun gripDraggedFraction(current: Float, dragAmountPx: Float, sheetHeightPx: Int): Float =
    (current + dragAmountPx / sheetHeightPx.coerceAtLeast(1)).coerceIn(0f, 1f)

/**
 * Issue #56, finding 4: a FIX return verdict may judge ONLY the
 * trip that was in flight when its return tick landed. A second
 * press inside the 800ms stability window advances the trip (and
 * replaces the item), and the first trip's timer must then stand
 * down — it neither judges nor clears the newer press.
 */
/**
 * Issue #56, fresh-eyes A: the FIX dip holds for the return
 * verdict ONLY when the press completed with a clean release.
 * An interrupted press (tryAwaitRelease() == false) clears its
 * state at once — it must never be judged by a later return tick.
 */
internal fun fixPressHoldsForReturn(released: Boolean): Boolean = released

internal fun fixReturnStillOwns(
    capturedTrip: Int,
    capturedItem: WatchItemId?,
    currentTrip: Int,
    currentItem: WatchItemId?,
    stillPressed: Boolean
): Boolean =
    stillPressed && capturedItem != null && capturedTrip == currentTrip && capturedItem == currentItem

@Composable
fun ParkDrawer(
    visible: Boolean,
    snapshot: WatchSnapshot?,
    introPending: Boolean = false,
    isSealed: Boolean,
    isNight: Boolean,
    soundEnabled: Boolean,
    // Defaults on the sled facts (issue #56, test-compile unblock):
    // several pre-existing test renders were written against a
    // ParkDrawer whose sled parameters had defaults and no longer
    // compiled once the parameters lost them — the whole unit-test
    // suite failed to compile on main. The one production caller
    // (MainActivity) passes every argument explicitly, so these
    // defaults change nothing at runtime; they only let the older
    // renders compile again.
    shortsReelsSniperEnabled: Boolean = false,
    soundEngine: TactileSoundEngine?,
    themeReadout: String,
    parkReturnTick: Int,
    onDismiss: () -> Unit,
    onFixClick: (WatchItemId) -> Unit,
    onRecheck: () -> Unit = {},
    onToggleSound: () -> Unit,
    onToggleShortsReelsSniper: () -> Unit = {},
    socialSitesEnabled: Boolean = false,
    onToggleSocialSites: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    // KIN-DRAWER travel: fraction 1 = shut (sheet below the host),
    // 0 = seated open. Exactly two rest heights.
    val fraction = remember { Animatable(1f) }
    // Issue #56, finding 2: the grip drag's accumulated fraction.
    // Non-null only between drag-start and drag-end/cancel; written
    // synchronously per drag event (never via a launched coroutine),
    // so it is always the latest, in-order drag position.
    var gripDragFraction by remember { mutableStateOf<Float?>(null) }
    var sheetHeightPx by remember { mutableStateOf(0) }
    LaunchedEffect(visible) {
        if (visible) {
            fraction.animateTo(0f, tween(MonolithKin.DRAWER_OPEN_MS, easing = MonolithKin.EasingSeat))
        } else if (fraction.value < 1f) {
            fraction.animateTo(1f, tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow))
        }
    }
    if (!visible && fraction.value >= 1f) return

    // While the grip owns the sheet, the RENDERED fraction is the
    // drag accumulator, not the Animatable (which is parked until
    // the gesture resolves in one ordered coroutine at drag-end).
    val effectiveFraction = gripDragFraction ?: fraction.value
    // Issue #56, finding 1: `interactive` is THE gate for every
    // control on this sheet — the FIX key (as before) and all three
    // sleds (previously gated only by the seal, so they fired
    // mid-rise, mid-throw, and throughout the close). A grip drag
    // in flight also un-settles the sheet.
    val settled = effectiveFraction == 0f && gripDragFraction == null
    val interactive = visible && settled && !isSealed
    val interactiveState = rememberUpdatedState(interactive)

    // Staleness is derived at render; while PARK stands open the
    // print re-derives on a slow beat so a line can go STALE in
    // front of him rather than only on the next store emission.
    var nowMono by remember { mutableStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(visible) {
        while (visible) {
            nowMono = SystemClock.elapsedRealtime()
            delay(5_000L)
        }
    }

    // The FIX key's target: the first line that lacks — MISSING, or
    // UNPROVED (issue #94: the BATTERY line on an OEM-autostart-gated
    // skin, whose FIX trip is the autostart pane, not a dead end).
    val firstMissing = snapshot?.let { snap ->
        WatchItemId.PERMISSIONS.firstOrNull {
            // A stale denial is still a denial (#58 F1): the FIX
            // key keeps targeting it, it just no longer PRINTS as
            // current on the board below. UNPROVED (#94) targets too.
            val print = WatchStore.permPrintAt(snap, it, nowMono)
            print == PermPrint.MISSING || print == PermPrint.MISSING_STALE ||
                print == PermPrint.UNPROVED
        }
    }

    // Issue #109, F3: STALE was a dead end — styled and named as a
    // lack, with no remedy on the board. The first stale line (only
    // when nothing is MISSING — a missing grant outranks a stale
    // reading) gets the RE-CHECK key below: a fresh Watch sweep
    // re-measures it on the spot.
    val firstStale = snapshot?.let { snap ->
        if (firstMissing != null) {
            null
        } else {
            WatchItemId.PERMISSIONS.firstOrNull {
                WatchStore.permPrintAt(snap, it, nowMono) == PermPrint.STALE
            }
        }
    }
    var recheckPressed by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (!visible) recheckPressed = false
    }

    // The FIX key's dip: pressed on DOWN, held for the whole absence,
    // resolved on the return (see the return effect below).
    var fixPressed by remember { mutableStateOf(false) }
    var fixPressedItem by remember { mutableStateOf<WatchItemId?>(null) }
    // Issue #56, finding 4: the FIX trip identity. Every press
    // advances it; a return verdict is bound to the trip value it
    // captured when its tick landed (see fixReturnStillOwns).
    var fixTrip by remember { mutableStateOf(0) }
    LaunchedEffect(visible) {
        if (!visible) {
            fixPressed = false
            fixPressedItem = null
        }
    }
    LaunchedEffect(parkReturnTick) {
        if (parkReturnTick <= 0 || !fixPressed) return@LaunchedEffect
        // Issue #56, finding 4: capture THIS trip's identity before
        // the wait. The old body read fixPressedItem only after the
        // delay, so a second press inside the window was judged —
        // and cleared — by the first trip's timer.
        val tripAtReturn = fixTrip
        val item = fixPressedItem ?: return@LaunchedEffect
        // Return is proving: the host's sweep lands within ~300ms;
        // the stability beat runs 500ms past it before any verdict.
        // The verdict reads the store LIVE at that instant — the
        // composed snapshot may predate the sweep's landings.
        delay(800L)
        if (!fixReturnStillOwns(tripAtReturn, item, fixTrip, fixPressedItem, fixPressed)) {
            // A newer trip owns the verdict now; its own return
            // tick will judge it. This timer stands down silently.
            return@LaunchedEffect
        }
        val snap = WatchStore.snapshots.value
        val repaired = WatchStore.permPrintAt(snap, item, SystemClock.elapsedRealtime()) ==
            PermPrint.GRANTED
        if (repaired) {
            // The repaired line alone gets the firm click; the hand
            // goes back in silence — no word prints, ever.
            soundEngine?.playFirmClick()
        }
        // Either the key now names the next lack or it absents
        // itself; in both cases the dip releases (PRESS-BACK).
        fixPressed = false
        fixPressedItem = null
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Scrim over the content, under the sheet. A tap here is
        // dismissal path (c).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f * (1f - effectiveFraction)))
                .then(
                    // Issue #56, finding 5: the scrim blocks for as
                    // long as it is DRAWN — the old `if (visible)`
                    // dropped the blocker the instant dismissal
                    // started, so taps fell through the still-visible
                    // scrim for the whole 160ms close throw. It now
                    // consumes touches until the sheet is fully shut;
                    // only a tap while open still dismisses.
                    if (fraction.value < 1f) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { if (visible) onDismiss() }
                    } else Modifier
                )
        )

        // The sheet.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .offset { IntOffset(0, (effectiveFraction * sheetHeightPx).roundToInt()) }
                .onSizeChanged { sheetHeightPx = it.height }
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(scheme.background)
                .testTag("park_sheet")
        ) {
            // Grab strip: 32dp visual, 48dp touch. The grip answers a
            // DOWN by growing 24 → 28dp; the drag carries the sheet
            // 1:1 and seats by the 40% law.
            var gripPressed by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .pointerInput(visible) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                gripPressed = true
                                // Issue #56, finding 2: the drag
                                // starts from the sheet's true
                                // position, accumulated from here.
                                if (visible) gripDragFraction = fraction.value
                            },
                            onDragEnd = {
                                gripPressed = false
                                val dragged = gripDragFraction
                                if (dragged == null || !visible) {
                                    gripDragFraction = null
                                    return@detectVerticalDragGestures
                                }
                                // The 40% law judges `dragged` —
                                // the synchronously accumulated,
                                // in-order final position, never a
                                // raced Animatable read. Exactly ONE
                                // coroutine resolves the gesture:
                                // it seats the Animatable on the
                                // dragged position first, then the
                                // throw/seat runs from there, in order.
                                if (dragged > PARK_DISMISS_FRACTION) {
                                    scope.launch {
                                        fraction.snapTo(dragged)
                                        gripDragFraction = null
                                        // Dismissal (b): sheet thrown
                                        // shut, cap returns to origin.
                                        onDismiss()
                                    }
                                } else {
                                    scope.launch {
                                        fraction.snapTo(dragged)
                                        gripDragFraction = null
                                        fraction.animateTo(
                                            0f,
                                            tween(MonolithKin.DRAWER_OPEN_MS, easing = MonolithKin.EasingSeat)
                                        )
                                    }
                                }
                            },
                            onDragCancel = {
                                gripPressed = false
                                // A cancelled drag re-seats from the
                                // accumulated position, in the same
                                // single ordered coroutine.
                                val dragged = gripDragFraction
                                if (dragged != null) {
                                    scope.launch {
                                        fraction.snapTo(dragged)
                                        gripDragFraction = null
                                        if (visible) {
                                            fraction.animateTo(
                                                0f,
                                                tween(MonolithKin.DRAWER_OPEN_MS, easing = MonolithKin.EasingSeat)
                                            )
                                        }
                                    }
                                }
                            },
                            onVerticalDrag = { _, dragAmount ->
                                // No coroutine per event (finding 2):
                                // pointer events are delivered in
                                // order on one thread, so this plain
                                // accumulation IS the ordered truth.
                                if (visible) {
                                    gripDragFraction = gripDraggedFraction(
                                        gripDragFraction ?: fraction.value,
                                        dragAmount,
                                        sheetHeightPx
                                    )
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                gripPressed = true
                                tryAwaitRelease()
                                gripPressed = false
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(if (gripPressed) 28.dp else 24.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(scheme.onSurfaceVariant)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp))
            ) {
                // First-run intro (issue-156, F5): plain words at
                // the TOP of the board, only while the intro is
                // pending — what PARK is and what FIX does, in the
                // board's normal title/body type. The spec plate
                // at the foot stays untouched.
                if (introPending) {
                    Text(
                        text = "PARK — THE MACHINE'S OWN REPORT CARD.",
                        style = AppTitleTextStyle,
                        color = scheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Every line names a guard and whether it is SET or MISSING. " +
                            "FIX takes you to the first one that is missing. " +
                            "Monolith asks for six system grants, one at a time; " +
                            "each FIX trip first says what the grant does and what it never does. " +
                            "THE WAY OUT, BEFORE ANY GRANT: every session carries an emergency " +
                            "break — a 60-second hold, once a week. You are never sealed in without an exit.",
                        style = BodyTextStyle,
                        color = scheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // (1) Header + the LAST PROVED telltale. The instant
                // lives HERE and nowhere else; the slot is blank
                // until the first complete set lands.
                Text(
                    text = "GUARD",
                    style = DrawerHeaderTextStyle,
                    color = scheme.onBackground
                )
                if (isSealed) {
                    // Issue #109, F2: a seal landing under an open
                    // drawer used to hide the FIX key and freeze the
                    // sleds in silence — the drawer never said it had
                    // gone read-only or why. Now it prints it, at the
                    // head, where the change is lived. The lock itself
                    // is HOLD-absolutely and stays; only the silence
                    // was the defect.
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "SEALED — READ-ONLY. A SESSION IS RUNNING: " +
                            "THE BOARD CAN BE READ, NOT CHANGED, UNTIL IT ENDS.",
                        style = PillBadgeTextStyle,
                        color = scheme.onBackground,
                        modifier = Modifier.testTag("park_sealed_note")
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "LAST PROVED",
                        style = PillBadgeTextStyle,
                        color = scheme.onSurfaceVariant
                    )
                    val proved = snapshot?.lastProvedWallMillis
                    if (proved != null) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = ClockText.timeOfDay(LocalContext.current, proved) +
                                " · SELF-REPORT",
                            fontFamily = AnonymousProFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp,
                            color = scheme.onBackground
                        )
                    }
                }

                // Issue #339 (Second-pass F4): the criticality
                // line — which single grant is fatal, stated as a
                // state, above the board's per-item rows.
                val availabilityLine = snapshot?.let {
                    com.example.ui.components.availabilityLineFor(it, nowMono)
                }
                if (availabilityLine != null) {
                    Text(
                        text = availabilityLine.text,
                        style = PillBadgeTextStyle,
                        color = scheme.onBackground,
                        modifier = Modifier.testTag("park_blocking_availability")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // (2) The board: nine ruled lines, fixed order.
                if (snapshot != null) {
                    WatchItemId.PERMISSIONS.forEach { item ->
                        // Issue #122, rebased onto #94's UNPROVED print:
                        // the BATTERY line prints the battery itself
                        // (level \u00b7 saver/charging \u00b7 NOT EXEMPT) from the
                        // snapshot's battery read \u2014 the exemption bit
                        // alone read "GRANTED" over an 8% battery with
                        // saver on. The perm print is NOT discarded:
                        // an UNPROVED / STALE exemption verdict still
                        // appends and still counts as a lack, so the
                        // line can never read satisfied while either
                        // the battery or the proof is lacking. No
                        // battery read yet \u2192 the perm print, as before.
                        val battery = snapshot.battery
                        val permPrint = WatchStore.permPrintAt(snapshot, item, nowMono)
                        if (item == WatchItemId.BATTERY && battery != null) {
                            val suffix = when (permPrint) {
                                PermPrint.UNPROVED -> " \u00b7 UNPROVED"
                                PermPrint.STALE, PermPrint.MISSING_STALE -> " \u00b7 STALE"
                                else -> ""
                            }
                            ParkBoardRow(
                                name = item.boardName,
                                word = com.example.data.batteryBoardWord(battery) + suffix,
                                wordIsLack = com.example.data.batteryBoardIsLack(battery) ||
                                    permPrint != PermPrint.GRANTED
                            )
                        } else {
                            ParkBoardRow(
                                name = item.boardName,
                                word = when (permPrint) {
                                    PermPrint.GRANTED -> "GRANTED"
                                    PermPrint.MISSING -> "MISSING"
                                    PermPrint.STALE -> "STALE"
                                    PermPrint.UNPROVED -> "UNPROVED"
                                    PermPrint.MISSING_STALE -> "STALE"
                                    PermPrint.UNMEASURED -> null
                                },
                                wordIsLack = when (permPrint) {
                                    PermPrint.GRANTED -> false
                                    else -> true
                                }
                            )
                        }
                    }
                    // The doubled gap: 16dp, no hairline, no eyebrow —
                    // the family break is whitespace, not a heading.
                    Spacer(modifier = Modifier.height(16.dp))
                    WatchItemId.INTERCEPTORS.forEach { item ->
                        val m = snapshot.measurement(item)
                        val word = m?.interceptor?.let {
                            when (it) {
                                InterceptorState.RUNNING -> "RUNNING"
                                InterceptorState.PAUSED -> "PAUSED"
                                InterceptorState.OFF -> "OFF"
                            }
                        }
                        if (item == WatchItemId.SOCIAL_SITES) {
                            // Truth Car 3 (UNA-02): the social
                            // line carries its own sled — see
                            // ParkSocialSitesRow.
                            ParkSocialSitesRow(
                                name = item.boardName,
                                word = word,
                                wordIsLack = m?.interceptor != InterceptorState.RUNNING,
                                enabled = socialSitesEnabled,
                                soundEngine = soundEngine,
                                interactive = interactive,
                                onToggle = onToggleSocialSites
                            )
                        } else {
                            ParkBoardRow(
                                // Issue #109, F5: the SHORTS + REELS
                                // board line is the MEASURED outcome;
                                // the sled row below is the SETTING.
                                // The two lines used to carry the same
                                // bare name and could print OFF over
                                // ON with nothing saying why both were
                                // true. The qualifier is the link.
                                name = if (item == WatchItemId.SHORTS_REELS) {
                                    "${item.boardName} — MEASURED"
                                } else {
                                    item.boardName
                                },
                                word = word,
                                wordIsLack = m?.interceptor != InterceptorState.RUNNING
                            )
                        }
                    }
                } else {
                    // Never measured: the board prints its names
                    // only — except the two sled-backed rows
                    // (SHORTS + REELS, SOCIAL SITES), whose sleds
                    // stand on persisted settings even here. Those
                    // rows print SETTING (issue-156, F4): a
                    // persisted fact, not a measurement, so the
                    // board never presents a default as a measured
                    // state. Print only — no default or enforcement
                    // changes.
                    WatchItemId.entries.forEachIndexed { idx, item ->
                        if (idx == WatchItemId.PERMISSIONS.size) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        if (item == WatchItemId.SOCIAL_SITES) {
                            // Never measured: the line prints its
                            // name, its SETTING word, and its
                            // switch — the setting is a persisted
                            // fact, not a measurement, so the sled
                            // stands even here (UNA-02).
                            ParkSocialSitesRow(
                                name = item.boardName,
                                word = "SETTING",
                                wordIsLack = true,
                                enabled = socialSitesEnabled,
                                soundEngine = soundEngine,
                                interactive = interactive,
                                onToggle = onToggleSocialSites
                            )
                        } else {
                            ParkBoardRow(
                                // Issue #109 F5 (merge resolution):
                                // this is the NEVER-MEASURED board —
                                // main's form is the truthful one
                                // (bare name + SETTING word badge);
                                // the branch's "— MEASURED" suffix
                                // belongs to the measured rows above
                                // and would lie here, where nothing
                                // has been measured yet.
                                name = item.boardName,
                                word = if (item == WatchItemId.SHORTS_REELS) "SETTING" else null,
                                wordIsLack = true
                            )
                        }
                    }
                }

                // (3) The FIX key — the board's only object. Present
                // only while a permission lack exists; sealed, it is
                // print (absent here: the gate is !isSealed).
                AnimatedVisibility(
                    visible = firstMissing != null && !isSealed,
                    enter = expandVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingSeat)) +
                        fadeIn(tween(MonolithKin.DRAWER_CLOSE_MS)),
                    exit = shrinkVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow)) +
                        fadeOut(tween(MonolithKin.DRAWER_CLOSE_MS))
                ) {
                    val item = firstMissing ?: return@AnimatedVisibility
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .offset(y = if (fixPressed) 1.dp else 0.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(inkControlFill())
                                .then(
                                    if (interactive) {
                                        Modifier.pointerInput(item) {
                                            detectTapGestures(
                                                onPress = {
                                                    fixPressed = true
                                                    fixPressedItem = item
                                                    // Issue #56, finding 4:
                                                    // a new trip begins —
                                                    // any pending return
                                                    // timer for an older
                                                    // trip is now stale.
                                                    fixTrip++
                                                    // The engine's quietest
                                                    // existing voice: the seat
                                                    // knock at its floor.
                                                    soundEngine?.playSeatKnock(
                                                        durationMs = 10,
                                                        amplitude01 = if (isNight) 0.06f else 0.1f,
                                                        pitchHz = 300f
                                                    )
                                                    onFixClick(item)
                                                    // Issue #56, fresh-eyes A:
                                                    // a press INTERRUPTED before
                                                    // release (gesture stolen by
                                                    // the sheet's scroll/drag, or
                                                    // the composition tearing
                                                    // down) must not leave the
                                                    // dip armed — otherwise the
                                                    // next unrelated return tick
                                                    // (every ON_RESUME bumps it)
                                                    // would judge a press that
                                                    // never completed. A clean
                                                    // release keeps the state:
                                                    // the Settings absence is
                                                    // the hold, and the return
                                                    // effect is its only judge.
                                                    // This composes with
                                                    // issue-9's release-commit:
                                                    // there a cancelled press
                                                    // never fires onFixClick,
                                                    // and this cleanup is what
                                                    // guarantees its state.
                                                    val released = try {
                                                        tryAwaitRelease()
                                                    } catch (ce: CancellationException) {
                                                        fixPressed = false
                                                        fixPressedItem = null
                                                        throw ce
                                                    }
                                                    if (!fixPressHoldsForReturn(released)) {
                                                        fixPressed = false
                                                        fixPressedItem = null
                                                    }
                                                }
                                            )
                                        }
                                    } else Modifier
                                )
                                .testTag("park_fix_key"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "FIX — ${item.boardName}",
                                style = PrimaryButtonTextStyle,
                                color = onInkControlFill()
                            )
                        }
                        // The trip's one warning, printed before he
                        // leaves (issue #94, F3 + F1): two of these
                        // panes can be dead ends on a sideloaded
                        // install, and the battery pane is only half
                        // the trip on a gated skin — say so here, in
                        // plain words, where the FIX is pressed.
                        val fixNote = when (item) {
                            WatchItemId.ACCESSIBILITY,
                            WatchItemId.NOTIFICATION_LISTENER ->
                                "IF THE SWITCH WON'T MOVE, THE INSTALL IS " +
                                    "RESTRICTED: SETTINGS → APPS → MONOLITH → " +
                                    "⋮ MENU → ALLOW RESTRICTED SETTINGS, " +
                                    "THEN COME BACK AND FIX AGAIN."
                            WatchItemId.BATTERY ->
                                // Issue #296 (Audit R5, ScreenZen
                                // second-pass F10): the battery step
                                // teaches, it does not just link — a
                                // named purpose and the OEM-killer
                                // pointer (dontkillmyapp.com, the
                                // public database of per-maker
                                // background-killing behaviour), on
                                // every skin, not only gated ones.
                                if (com.example.data.OemPolicy.gatesAutostart()) {
                                    "THIS PHONE ALSO GATES RESTARTS BEHIND " +
                                        "AUTOSTART: FIX OPENS THE AUTOSTART " +
                                        "LIST — SWITCH MONOLITH ON THERE. " +
                                        "THE APP CANNOT READ THAT SWITCH " +
                                        "BACK, SO THIS LINE STAYS UNPROVED. " +
                                        "DONTKILLMYAPP.COM LISTS THIS " +
                                        "MAKER'S EXTRA STEPS."
                                } else {
                                    "REQUIRED SO THE PHONE DOES NOT " +
                                        "SWITCH BLOCKING OFF IN THE " +
                                        "BACKGROUND: SET MONOLITH TO " +
                                        "UNRESTRICTED. DONTKILLMYAPP.COM " +
                                        "LISTS EACH MAKER'S EXTRA STEPS."
                                }
                            else -> null
                        }
                        if (fixNote != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = fixNote,
                                fontFamily = AnonymousProFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 10.sp,
                                lineHeight = 16.sp,
                                letterSpacing = 1.2.sp,
                                color = scheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Issue #280: the rationale for the grant the FIX
                // key targets — printed under the key, before any
                // Settings trip, in the board's body type.
                if (firstMissing != null && !isSealed) {
                    WatchStore.boardRationale(firstMissing!!)?.let { rationale ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = rationale,
                            style = BodyTextStyle,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier.testTag("park_fix_rationale")
                        )
                    }
                }

                // (3b) The RE-CHECK key (issue #109, F3): STALE's
                // remedy. Present only while a permission line is
                // STALE and nothing is MISSING; a press fires a fresh
                // Watch sweep from the host and the lines re-measure
                // where he stands — no leaving the app and coming
                // back. Sealed, it is absent like the FIX key: the
                // drawer is read-only and prints so at its head.
                AnimatedVisibility(
                    visible = firstStale != null && !isSealed,
                    enter = expandVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingSeat)) +
                        fadeIn(tween(MonolithKin.DRAWER_CLOSE_MS)),
                    exit = shrinkVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow)) +
                        fadeOut(tween(MonolithKin.DRAWER_CLOSE_MS))
                ) {
                    val item = firstStale ?: return@AnimatedVisibility
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .offset(y = if (recheckPressed) 1.dp else 0.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(inkControlFill())
                                .then(
                                    if (interactive) {
                                        Modifier.pointerInput(item) {
                                            detectTapGestures(
                                                onPress = {
                                                    recheckPressed = true
                                                    // Same quiet seat knock
                                                    // as the FIX key.
                                                    soundEngine?.playSeatKnock(
                                                        durationMs = 10,
                                                        amplitude01 = if (isNight) 0.06f else 0.1f,
                                                        pitchHz = 300f
                                                    )
                                                    // Issue #9, Zahn Z-11
                                                    // (RE-CHECK form): the
                                                    // dispatch commits on
                                                    // RELEASE, like FIX —
                                                    // a scroll that merely
                                                    // starts on this key
                                                    // fires no sweep. The
                                                    // knock stays on DOWN:
                                                    // it is furniture, not
                                                    // a commit.
                                                    val released =
                                                        tryAwaitRelease()
                                                    if (released) onRecheck()
                                                    recheckPressed = false
                                                }
                                            )
                                        }
                                    } else Modifier
                                )
                                .testTag("park_recheck_key"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RE-CHECK — ${item.boardName}",
                                style = PrimaryButtonTextStyle,
                                color = onInkControlFill()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // (4) Sound — the shipped sled, moved whole. PARK's
                // row grammar: name plate left, margin word (the
                // state's one rendering — it flips on arrival, when
                // the throw lands, never mid-throw), sled right.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .semantics(mergeDescendants = true) {},
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SOUND EFFECTS",
                        style = PillBadgeTextStyle,
                        color = scheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    var displayedSound by remember { mutableStateOf(soundEnabled) }
                    LaunchedEffect(soundEnabled) {
                        delay(260L)
                        displayedSound = soundEnabled
                    }
                    Text(
                        text = if (displayedSound) "ON" else "OFF",
                        style = AppTitleTextStyle,
                        color = scheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box {
                        AndroidView(
                            factory = { ctx ->
                                SledSwitchView(ctx).apply {
                                    chromeMode = true
                                    contentDescription = "Sound"
                                    this.soundEngine = soundEngine
                                    isChecked = soundEnabled
                                    // Issue #56, finding 1: the sled
                                    // obeys the same gate as the FIX
                                    // key — no toggle mid-rise,
                                    // mid-throw, or while closing.
                                    onToggleRequested = { if (interactiveState.value) onToggleSound() }
                                    // Fresh-eyes B: the view itself
                                    // consults this before any haptic,
                                    // so a gated press stays silent.
                                    gateCheck = { interactiveState.value }
                                }
                            },
                            update = { view ->
                                view.soundEngine = soundEngine
                                if (view.isChecked != soundEnabled) {
                                    view.isChecked = soundEnabled
                                }
                                view.onToggleRequested = { if (interactiveState.value) onToggleSound() }
                                view.gateCheck = { interactiveState.value }
                            },
                            modifier = Modifier
                                .width(76.dp)
                                .height(48.dp)
                                .testTag("park_sound_sled")
                        )
                        if (!interactive) {
                            // Issue #56, finding 1: whenever the
                            // sheet is not settled-and-open (sealed,
                            // mid-rise, mid-throw, closing) the sled
                            // is print; the overlay takes the touch.
                            Box(
                                modifier = Modifier
                                    .width(76.dp)
                                    .height(48.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {}
                            )
                        }
                    }
                }
                // Issue #150: the sled's word must not lie by
                // omission — OFF silences the voice, never the
                // event: the haptic twin still fires at full
                // amplitude by design (S0). One caption line in
                // the plate's own grammar, printed always, so the
                // fact is read before it is discovered on a
                // library desk.
                Text(
                    text = "SOUND OFF KEEPS THE TOUCH — THE BUZZ CARRIES THE EVENT",
                    style = PillBadgeTextStyle,
                    color = scheme.onSurfaceVariant
                )
                ParkHairline()

                // (5) Shorts + Reels — the sniper's sled, seated
                // with the Sound sled in the same row grammar
                // (Truth Car 3, UNA-01). Until this car the board's
                // SHORTS + REELS line printed a state whose only
                // source was the ViewModel's memory; the sled reads
                // and writes the persisted Guard row, and the board
                // above now prints the same fact the sled holds.
                // Issue #109, F5: this row is the SETTING and prints
                // so — the board's line above is the MEASURED
                // outcome, and the two qualifiers are what keep an
                // honest OFF-over-ON from reading as a contradiction.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .semantics(mergeDescendants = true) {},
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // #131 truth scope: the sled arms the FEED
                    // sniper only — it never policed long-form,
                    // home feeds, Stories or DMs (those are the
                    // app tier's job), so the plate says FEED.
                    // Issue #109 F5 (merge resolution): the plate
                    // also carries — SETTING, the link to the
                    // board's "— MEASURED" line above; the two
                    // lines must never carry the same bare name.
                    Text(
                        text = "SHORTS + REELS FEED — SETTING",
                        style = PillBadgeTextStyle,
                        color = scheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    var displayedShorts by remember { mutableStateOf(shortsReelsSniperEnabled) }
                    LaunchedEffect(shortsReelsSniperEnabled) {
                        delay(260L)
                        displayedShorts = shortsReelsSniperEnabled
                    }
                    Text(
                        text = if (displayedShorts) "ON" else "OFF",
                        style = AppTitleTextStyle,
                        color = scheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box {
                        AndroidView(
                            factory = { ctx ->
                                SledSwitchView(ctx).apply {
                                    chromeMode = true
                                    contentDescription = "Shorts and Reels blocker"
                                    this.soundEngine = soundEngine
                                    isChecked = shortsReelsSniperEnabled
                                    // Issue #56, finding 1: same gate
                                    // as the Sound sled and FIX key.
                                    onToggleRequested = { if (interactiveState.value) onToggleShortsReelsSniper() }
                                    gateCheck = { interactiveState.value }
                                }
                            },
                            update = { view ->
                                view.soundEngine = soundEngine
                                if (view.isChecked != shortsReelsSniperEnabled) {
                                    view.isChecked = shortsReelsSniperEnabled
                                }
                                view.onToggleRequested = { if (interactiveState.value) onToggleShortsReelsSniper() }
                                view.gateCheck = { interactiveState.value }
                            },
                            modifier = Modifier
                                .width(76.dp)
                                .height(48.dp)
                                .testTag("park_shorts_sled")
                        )
                        if (!interactive) {
                            // Issue #56, finding 1: not settled-and-
                            // open — print, not a control, as the
                            // Sound sled above.
                            Box(
                                modifier = Modifier
                                    .width(76.dp)
                                    .height(48.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {}
                            )
                        }
                    }
                }
                ParkHairline()
                // (6) Theme — one ruled line, an evaluation printed.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "THEME",
                        style = PillBadgeTextStyle,
                        color = scheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = themeReadout,
                        style = AppTitleTextStyle,
                        color = scheme.onBackground
                    )
                }
                ParkHairline()

                // (7) The spec plate — furniture at the foot.
                Spacer(modifier = Modifier.height(32.dp))
                ParkHairline()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = PLATE_HEAD.joinToString("\n"),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 1.2.sp,
                    color = scheme.onBackground
                )
                Text(
                    text = PLATE_BODY.joinToString("\n"),
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 1.2.sp,
                    color = scheme.onSurfaceVariant
                )
            }
        }
    }
}

/** One board line: name plate left, state word right, ruled under.
 *  A never-measured line prints its NAME ONLY — no word, no fill —
 *  except a sled-backed row's SETTING word, which names a persisted
 *  setting, never a measurement (issue-156, F4). */
@Composable
private fun ParkBoardRow(name: String, word: String?, wordIsLack: Boolean) {
    val scheme = MaterialTheme.colorScheme
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .semantics(mergeDescendants = true) {},
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            if (word != null) {
                Text(
                    text = word,
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp,
                    color = if (wordIsLack) scheme.onBackground else scheme.onSurfaceVariant
                )
            }
        }
        ParkHairline()
    }
}

/** Truth Car 3 (UNA-02): the SOCIAL SITES board line, carrying
 *  its own sled — the setting's switch, seated on the line it
 *  governs. Until this row, the setting had NO surface anywhere:
 *  its only writer besides a dead Deck/Shield parameter was the
 *  STUDY loadout forcing it ON.
 *
 *  Two facts share the line and neither impersonates the other:
 *  the WORD still prints the measured interceptor state (the
 *  Watch Store's derivation — setting + parent warrants +
 *  suspension), and the SLED prints and writes the setting
 *  itself: the persisted `browserBlockerEnabled` fact the
 *  accessibility service reads fresh per event. The sled is the
 *  shipped control (chrome mode), gated exactly like the sound
 *  sled — under the seal it is print; the overlay takes the
 *  touch. */
@Composable
private fun ParkSocialSitesRow(
    name: String,
    word: String?,
    wordIsLack: Boolean,
    enabled: Boolean,
    soundEngine: TactileSoundEngine?,
    interactive: Boolean,
    onToggle: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    // Issue #56, finding 1: this row receives the drawer's one
    // `interactive` gate (not merely the seal) — see ParkDrawer.
    val interactiveState = rememberUpdatedState(interactive)
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .semantics(mergeDescendants = true) {},
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            if (word != null) {
                Text(
                    text = word,
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp,
                    color = if (wordIsLack) scheme.onBackground else scheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Box {
                AndroidView(
                    factory = { ctx ->
                        SledSwitchView(ctx).apply {
                            chromeMode = true
                            contentDescription = "Social sites blocker"
                            this.soundEngine = soundEngine
                            isChecked = enabled
                            onToggleRequested = { if (interactiveState.value) onToggle() }
                            gateCheck = { interactiveState.value }
                        }
                    },
                    update = { view ->
                        view.soundEngine = soundEngine
                        if (view.isChecked != enabled) {
                            view.isChecked = enabled
                        }
                        view.onToggleRequested = { if (interactiveState.value) onToggle() }
                        view.gateCheck = { interactiveState.value }
                    },
                    modifier = Modifier
                        .width(76.dp)
                        .height(48.dp)
                        .testTag("park_social_sled")
                )
                if (!interactive) {
                    // Not settled-and-open: print, not a control —
                    // the overlay takes the touch, as the sound sled.
                    Box(
                        modifier = Modifier
                            .width(76.dp)
                            .height(48.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {}
                    )
                }
            }
        }
        ParkHairline()
    }
}

/** PARK's ruling: 1dp of ink at 18% — print ruling, not a depth line. */
@Composable
private fun ParkHairline() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f))
    )
}
