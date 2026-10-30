package com.example.ui.components

import android.os.SystemClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
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
import com.example.service.Warrant
import com.example.service.WarrantState
import com.example.service.WatchStore
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AppTitleTextStyle
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.DrawerHeaderTextStyle
import com.example.ui.theme.MonolithKin
import com.example.ui.theme.PillBadgeTextStyle
import com.example.ui.theme.PrimaryButtonTextStyle
import com.example.ui.views.SledSwitchView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
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
 * exists. (Hof P-1, issue #9, seats the board's second object: the
 * CHECK key — the drawer's verb of proof, present whenever the
 * drawer is operable, firing the Watch sweep on demand; opening
 * PARK fires the same sweep from the host.) (Truth Car 3, UNA-02, seats ONE exception on the board
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
 * no sled.
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
    "HOLD: 60 SECONDS, BANKED EVERY 15 · ONE BREAK A WEEK",
    // The PEEK line is NOT static text: plateBodyFor() substitutes
    // the set length (W-08) for any line starting with "PEEK:".
    // This entry is the bare slot token — no length literal lives
    // here (batch-10 verifier cure: a stale "2 MINUTES" default
    // form survived the W-08 composition as source text).
    "PEEK:",
    // Wave-2 plate cut (AUDIT-park §1.11/§4.9, lead ruling): the
    // plate is cut toward the ruled four lines (head 2 + HOLD +
    // PEEK). CUT: the WHEEL line, the COLOUR line (it contradicted
    // this file's own orange-zero law), the ROUTINES line (its
    // "FIRED AS SET" clause remains STRUCK under W-11 — UNA-05:
    // falsified on his phone Oct 29, 20:00 SLEEP no-fire; it may
    // return only when Wache's F1–F5 land in the same build AND a
    // live fire is re-verified), the SOUND/POWER line, and the
    // "DATA: ON THIS PHONE ONLY" clause (it contradicted the YPT
    // line it stood beside). KEPT as one combined fifth line —
    // against the literal count, on the record — because SITES and
    // STUDY HOURS are truth facts this plate uniquely carries on
    // this surface; deleting them to hit a number would trade
    // truth for arithmetic.
    "SITES: KEPT WITH APPS ON SHIELD · STUDY HOURS: IN YPT"
)

@Composable
fun ParkDrawer(
    visible: Boolean,
    snapshot: WatchSnapshot?,
    // §4.2 (wave-2): the wire warrant, supplied by the host from the
    // same deckWarrant flow the Deck prints — Park derives its word
    // ONLY through WatchStore.printState, never a second derivation.
    // Null = no warrant read supplied: the line prints NOT CHECKED.
    warrant: Warrant? = null,
    isSealed: Boolean,
    isNight: Boolean,
    soundEnabled: Boolean,
    shortsReelsSniperEnabled: Boolean,
    soundEngine: TactileSoundEngine?,
    themeReadout: String,
    parkReturnTick: Int,
    onDismiss: () -> Unit,
    onFixClick: (WatchItemId) -> Unit,
    onCheckClick: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleShortsReelsSniper: () -> Unit,
    socialSitesEnabled: Boolean,
    onToggleSocialSites: () -> Unit,
    onOpenReception: () -> Unit = {},
    // W-08 (batch-10): the spec plate's PEEK line reads the SET
    // peek length from the same GuardSettings row the lease reads
    // — never the retired 2-minute constant. 0 prints PEEK: OFF.
    peekMinutes: Int = 2,
    setupStampMask: Int? = null,
    handoverText: String? = null,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    // KIN-DRAWER travel: fraction 1 = shut (sheet below the host),
    // 0 = seated open. Exactly two rest heights.
    val fraction = remember { Animatable(1f) }
    var sheetHeightPx by remember { mutableStateOf(0) }
    LaunchedEffect(visible) {
        if (visible) {
            fraction.animateTo(0f, tween(MonolithKin.DRAWER_OPEN_MS, easing = MonolithKin.EasingSeat))
        } else if (fraction.value < 1f) {
            fraction.animateTo(1f, tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow))
        }
    }
    if (!visible && fraction.value >= 1f) return

    val settled = fraction.value == 0f
    val interactive = visible && settled && !isSealed

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

    val firstMissing = snapshot?.let { snap ->
        WatchItemId.PERMISSIONS.firstOrNull {
            WatchStore.permPrintAt(snap, it, nowMono) == PermPrint.MISSING
        }
    }
    // The Healing Staircase's print half (P-5, wave-2): how many
    // doors lack, in board order — the FIX key names its place in
    // the walk (FIX 1 OF 3). The walk itself is sequential by
    // construction: on return the key re-names the next lack.
    val missingItems = snapshot?.let { snap ->
        WatchItemId.PERMISSIONS.filter {
            WatchStore.permPrintAt(snap, it, nowMono) == PermPrint.MISSING
        }
    } ?: emptyList()

    // The CHECK key's sweep generation (Hof P-1 cure, issue #9;
    // derivation tightened under Critic C-1): the press dispatches
    // a fresh Watch sweep and records ITS generation; the key is
    // CHECKING until the snapshot's lastCompletedSweepGen reaches
    // that generation — a derivation from the store, never a
    // timer. The first form read raw stamp times, and the shield
    // loop's gen-0 re-stamps (every 2s, presence alone) plus an
    // earlier sweep's late landings could "answer" a press they
    // never served. A generation cannot be laundered: only the
    // completing set of the sweep the press dispatched releases
    // the key, in parity with LAST PROVED (the same completion
    // advances both). -1 = no check in flight.
    var checkPressGen by remember { mutableStateOf(-1) }
    val checking = checkPressGen >= 0 &&
        (snapshot?.lastCompletedSweepGen ?: 0) < checkPressGen
    // The last completed check's verdict, when it landed unwhole
    // (Designer's failed state, issue-9 spec): a persistent line
    // under the CHECK key naming the first unproved counted item.
    // The name is the MEASURED lack — every item answered; a
    // MISSING is an answer, not a silence — so the line says
    // NOT PROVED and names the door, nothing more. It clears on
    // the next press, on close, and whenever the board reads
    // whole by any road (a FIX return included).
    var lastCheckLackName by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(checking) {
        if (!checking && checkPressGen >= 0) {
            // The set has landed. Two wholes live on this
            // surface, named (Critic C-11): CHECKING completed
            // on the PROVED SET (all six — LAST PROVED's own
            // definition); the verdict below — the firm click
            // and the NOT PROVED name — is computed on the
            // COUNTED FOUR, the doors the warrant gates on.
            // If the four landed whole, the firm click — the
            // same voice the FIX return uses for a repaired
            // line; a set with a lack stays silent and the
            // board prints the lack in full ink.
            val snap = snapshot
            val now = SystemClock.elapsedRealtime()
            val firstLack = snap?.let { s ->
                WatchItemId.COUNTED.firstOrNull {
                    WatchStore.permPrintAt(s, it, now) != PermPrint.GRANTED
                }
            }
            if (snap != null && firstLack == null) {
                soundEngine?.playFirmClick()
                lastCheckLackName = null
            } else {
                lastCheckLackName = firstLack?.boardName
            }
            checkPressGen = -1
        }
    }
    LaunchedEffect(snapshot) {
        val snap = snapshot ?: return@LaunchedEffect
        val now = SystemClock.elapsedRealtime()
        if (WatchItemId.COUNTED.all {
                WatchStore.permPrintAt(snap, it, now) == PermPrint.GRANTED
            }
        ) {
            lastCheckLackName = null
        }
    }
    LaunchedEffect(visible) {
        if (!visible) {
            checkPressGen = -1
            lastCheckLackName = null
        }
    }

    // The FIX key's press state — declared BEFORE the dip
    // animators that read it (wave-2 named-seats MUST-1: fixDip
    // read fixPressed ~50 lines before its declaration; the move
    // is free and zero-behaviour-change, and Ren's one compile
    // should not be spent discovering it).
    var fixPressed by remember { mutableStateOf(false) }
    var fixPressedItem by remember { mutableStateOf<WatchItemId?>(null) }

    // The keys' dip motion (Finisher 2, §2): a gallery key
    // travels — 90 SEAT in on the press, 160 THROW back on the
    // release. One animator spec for both keys; the verbs part
    // only in what holds the dip (FIX: the Settings absence;
    // CHECK: the sweep's landings).
    val fixDip by animateDpAsState(
        targetValue = if (fixPressed) 1.dp else 0.dp,
        animationSpec = tween(
            durationMillis = if (fixPressed) MonolithKin.PRESS_MS
            else MonolithKin.DRAWER_CLOSE_MS,
            easing = if (fixPressed) MonolithKin.EasingSeat
            else MonolithKin.EasingThrow
        ),
        label = "park_fix_dip"
    )
    val checkDip by animateDpAsState(
        targetValue = if (checking) 1.dp else 0.dp,
        animationSpec = tween(
            durationMillis = if (checking) MonolithKin.PRESS_MS
            else MonolithKin.DRAWER_CLOSE_MS,
            easing = if (checking) MonolithKin.EasingSeat
            else MonolithKin.EasingThrow
        ),
        label = "park_check_dip"
    )

    // The CHECK key's dress ladder (Designer §1.4, Finisher 2
    // §4.1): print dress at rest on a fresh board; ink exactly
    // when pressing CHECK is the answer — a row STALE or
    // UNMEASURED, or a sweep in flight. MISSING does not promote
    // it: a lack's answer is the FIX key above it.
    val checkNeeded = if (snapshot == null) {
        true
    } else {
        WatchItemId.PERMISSIONS.any { item ->
            val p = WatchStore.permPrintAt(snapshot, item, nowMono)
            p == PermPrint.STALE || p == PermPrint.UNMEASURED
        }
    }
    val checkInk = checking || checkNeeded

    // The FIX return's PRINTED verdict (AUDIT-park P-5, wave-2):
    // Muede overruled the silent verdict for the drawer's real
    // hour — the firm click stays for sound, and a word now prints
    // too: FIXED or STILL OPEN, naming the door. Silence read
    // identical for fixed and still-broken, so he took the trip
    // twice. Cleared on the next press and on close.
    var fixVerdict by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    LaunchedEffect(visible) {
        if (!visible) fixVerdict = null
    }

    LaunchedEffect(visible) {
        if (!visible) {
            fixPressed = false
            fixPressedItem = null
        }
    }
    LaunchedEffect(parkReturnTick) {
        if (parkReturnTick <= 0 || !fixPressed) return@LaunchedEffect
        // Return is proving: the host's sweep lands within ~300ms;
        // the stability beat runs 500ms past it before any verdict.
        // The verdict reads the store LIVE at that instant — the
        // composed snapshot may predate the sweep's landings.
        delay(800L)
        val snap = WatchStore.snapshots.value
        val item = fixPressedItem ?: return@LaunchedEffect
        val repaired = WatchStore.permPrintAt(snap, item, SystemClock.elapsedRealtime()) ==
            PermPrint.GRANTED
        if (repaired) {
            // The repaired line alone gets the firm click (the
            // sound acquittal stands) — and, since wave-2, the
            // printed verdict below the key (P-5).
            soundEngine?.playFirmClick()
        }
        fixVerdict = item.boardName to repaired
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
                .background(Color.Black.copy(alpha = 0.32f * (1f - fraction.value)))
                .then(
                    if (visible) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onDismiss() }
                    } else Modifier
                )
        )

        // The sheet.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .offset { IntOffset(0, (fraction.value * sheetHeightPx).roundToInt()) }
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
                            onDragStart = { gripPressed = true },
                            onDragEnd = {
                                gripPressed = false
                                if (fraction.value > 0.4f) {
                                    // Dismissal (b): sheet thrown shut,
                                    // cap returns to its origin stop.
                                    onDismiss()
                                } else {
                                    scope.launch {
                                        fraction.animateTo(
                                            0f,
                                            tween(MonolithKin.DRAWER_OPEN_MS, easing = MonolithKin.EasingSeat)
                                        )
                                    }
                                }
                            },
                            onDragCancel = { gripPressed = false },
                            onVerticalDrag = { _, dragAmount ->
                                scope.launch {
                                    val h = sheetHeightPx.coerceAtLeast(1)
                                    fraction.snapTo(
                                        (fraction.value + dragAmount / h).coerceIn(0f, 1f)
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
                    .padding(PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp)) // §4.11: the 100dp void is cut to 24
            ) {
                // (1) Header (wave-2, §4.3 + §4.2): the headline
                // finally prints the count it everywhere else
                // printed — GUARD {n}/4, from the store's own
                // countedWarranted. LAST PROVED is DEMOTED from
                // here to the foot colophon (§1.3/§4.8): it is a
                // six-permission complete-set instant and may not
                // sit as the four's headline.
                val countedNow = snapshot?.let { WatchStore.countedWarranted(it, nowMono) }
                Text(
                    text = if (countedNow != null) "GUARD $countedNow/4" else "GUARD",
                    style = DrawerHeaderTextStyle,
                    color = scheme.onBackground,
                    modifier = Modifier.testTag("park_guard_headline")
                )
                Spacer(modifier = Modifier.height(8.dp))
                // The warrant line (§1.7/§4.2, P-2 cheap half): the
                // board that answers "is blocking on?" can now say
                // LIVE — the word derives ONLY through
                // WatchStore.printState on the host-supplied
                // warrant (a stale LIVE reads UNPROVEN there).
                val warrantPrint = WatchStore.printState(warrant, nowMono)
                ParkBoardRow(
                    name = "WARRANT",
                    word = warrantPrint?.name ?: "NOT CHECKED",
                    wordInk = when (warrantPrint) {
                        WarrantState.LIVE -> ParkWordInk.QUIET
                        WarrantState.UNPROVEN -> ParkWordInk.HALF
                        else -> ParkWordInk.FULL
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // (2) The board: nine ruled lines, fixed order.
                if (snapshot != null) {
                    WatchItemId.PERMISSIONS.forEachIndexed { permIdx, item ->
                        // The doors are RANKED (§3, wave-2 §4.7):
                        // tier eyebrows in board order — Tier 1 the
                        // warrant's only conjuncts, Tier 2 the
                        // anchor, Tier 3 the quiet; OVERLAY +
                        // BATTERY are false citizens of the counted
                        // family and sit under their own eyebrow.
                        when (permIdx) {
                            0 -> ParkTierEyebrow("TIER 1 — THE PROOF ITSELF")
                            2 -> ParkTierEyebrow("TIER 2 — THE ANCHOR")
                            3 -> ParkTierEyebrow("TIER 3 — THE QUIET")
                            4 -> ParkTierEyebrow("NOT COUNTED — THE REST")
                        }
                        // One derivation per row per frame (F-B3):
                        // word and ink read the SAME print — two
                        // calls could straddle the 45s boundary and
                        // tear (word GRANTED, ink STALE) for a frame.
                        val print = WatchStore.permPrintAt(snapshot, item, nowMono)
                        ParkBoardRow(
                            name = item.boardName,
                            // §4.4 age column (wave-2): every row
                            // prints the age of its own measurement,
                            // from the stamp the store already
                            // buries — a GRANTED from an hour ago
                            // and one from a second ago no longer
                            // wear the same face.
                            ageWord = snapshot.measurement(item)?.let { m ->
                                parkAgeWord(nowMono - m.measuredMono)
                            },
                            word = when (print) {
                                PermPrint.GRANTED -> "GRANTED"
                                PermPrint.MISSING -> "MISSING"
                                PermPrint.STALE -> "STALE"
                                PermPrint.UNMEASURED -> null
                            },
                            wordInk = when (print) {
                                PermPrint.GRANTED,
                                PermPrint.UNMEASURED -> ParkWordInk.QUIET
                                PermPrint.STALE -> ParkWordInk.HALF
                                PermPrint.MISSING -> ParkWordInk.FULL
                            }
                        )
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
                                wordInk = if (m?.interceptor == InterceptorState.RUNNING) {
                                    ParkWordInk.QUIET
                                } else {
                                    ParkWordInk.FULL
                                },
                                enabled = socialSitesEnabled,
                                soundEngine = soundEngine,
                                isSealed = isSealed,
                                onToggle = onToggleSocialSites
                            )
                        } else {
                            ParkBoardRow(
                                name = item.boardName,
                                word = word,
                                wordInk = if (m?.interceptor == InterceptorState.RUNNING) {
                                    ParkWordInk.QUIET
                                } else {
                                    ParkWordInk.FULL
                                }
                            )
                        }
                    }
                } else {
                    // Never measured: the board prints its names only.
                    WatchItemId.entries.forEachIndexed { idx, item ->
                        if (idx == WatchItemId.PERMISSIONS.size) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        if (item == WatchItemId.SOCIAL_SITES) {
                            // Never measured: the line prints its
                            // name and its switch — the setting is
                            // a persisted fact, not a measurement,
                            // so the sled stands even here (UNA-02).
                            ParkSocialSitesRow(
                                name = item.boardName,
                                word = null,
                                wordInk = ParkWordInk.FULL,
                                enabled = socialSitesEnabled,
                                soundEngine = soundEngine,
                                isSealed = isSealed,
                                onToggle = onToggleSocialSites
                            )
                        } else {
                            ParkBoardRow(name = item.boardName, word = null, wordInk = ParkWordInk.FULL)
                        }
                    }
                }

                // (3) The FIX key — the board's only object. Present
                // only while a permission lack exists; sealed, it is
                // print (absent here: the gate is !isSealed).
                AnimatedVisibility(
                    visible = firstMissing != null && !isSealed,
                    // Entry at drawer tempo (audit §1.8: 280 SEAT);
                    // exit stays at throw tempo — arrivals land,
                    // departures are thrown (Finisher 2, §3.1).
                    enter = expandVertically(tween(MonolithKin.DRAWER_OPEN_MS, easing = MonolithKin.EasingSeat)) +
                        fadeIn(tween(MonolithKin.DRAWER_OPEN_MS)),
                    exit = shrinkVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow)) +
                        fadeOut(tween(MonolithKin.DRAWER_CLOSE_MS))
                ) {
                    val item = firstMissing ?: return@AnimatedVisibility
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        // The escort (P-5, wave-2 print half): FIX
                        // used to abandon him at the OS border —
                        // the general Accessibility list, "find
                        // MONOLITH" said nowhere. The drawer now
                        // says where the trip goes and that the
                        // verdict waits here on return.
                        Text(
                            text = if (item == WatchItemId.ACCESSIBILITY) {
                                "FIX OPENS SETTINGS — FIND MONOLITH IN THE LIST, TURN IT ON, THEN COME BACK HERE"
                            } else {
                                "FIX OPENS THIS DOOR'S SETTINGS PAGE — MAKE THE CHANGE, THEN COME BACK HERE FOR THE VERDICT"
                            },
                            fontFamily = AnonymousProFamily,
                            fontSize = 11.sp,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier.testTag("park_fix_escort")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .offset(y = fixDip)
                                .clip(RoundedCornerShape(8.dp))
                                .background(inkControlFill())
                                .semantics { contentDescription = "Fix ${item.boardName}" }
                                .then(
                                    if (interactive) {
                                        Modifier.pointerInput(item) {
                                            detectTapGestures(
                                                onPress = {
                                                    fixPressed = true
                                                    fixPressedItem = item
                                                    fixVerdict = null
                                                    // The engine's quietest
                                                    // existing voice: the seat
                                                    // knock at its floor.
                                                    soundEngine?.playSeatKnock(
                                                        durationMs = 10,
                                                        amplitude01 = if (isNight) 0.06f else 0.1f,
                                                        pitchHz = 300f
                                                    )
                                                    // Commit on RELEASE,
                                                    // not on DOWN (Feel 1,
                                                    // T-1): FIX costs a
                                                    // trip to Settings, so
                                                    // a scroll that merely
                                                    // starts on the key —
                                                    // or a press that
                                                    // slides off — must
                                                    // launch nothing. The
                                                    // knock and the dip
                                                    // stay on DOWN (they
                                                    // cost nothing); a
                                                    // cancelled press
                                                    // releases the dip and
                                                    // the matter ends.
                                                    // CHECK's DOWN-commit
                                                    // stands by the same
                                                    // rule: its sweep is
                                                    // idempotent and
                                                    // costs nothing.
                                                    if (tryAwaitRelease()) {
                                                        onFixClick(item)
                                                    } else {
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
                                // The staircase's label (P-5): with
                                // several lacks, the key names its
                                // place in the walk — FIX 1 OF 3.
                                text = if (missingItems.size > 1) {
                                    "FIX 1 OF ${missingItems.size} — ${item.boardName}"
                                } else {
                                    "FIX — ${item.boardName}"
                                },
                                style = PrimaryButtonTextStyle,
                                color = onInkControlFill(),
                                maxLines = 1
                            )
                        }
                    }
                }

                // The FIX return's printed verdict (P-5, wave-2):
                // FIXED or STILL OPEN, naming the door — persists
                // until the next press or the drawer closing.
                val verdict = fixVerdict
                if (verdict != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ParkBoardRow(
                        name = if (verdict.second) "FIXED" else "STILL OPEN",
                        word = verdict.first,
                        wordInk = if (verdict.second) ParkWordInk.QUIET else ParkWordInk.FULL
                    )
                }

                // (3b) The CHECK key (Hof P-1 cure, issue #9) — the
                // board's second object, and its only verb of proof.
                // The FIX key repairs a named lack; this key re-proves
                // the whole board on demand: the host re-runs the
                // Watch sweep, each line re-stamps as its query
                // lands, and LAST PROVED advances when the complete
                // set lands. Present in every state the FIX key is
                // not — including never-measured, where it is the
                // only honest move on the surface — and absent
                // under the seal, where the drawer is print.
                if (!isSealed) {
                    // Spacing on the audit's module (Finisher 2,
                    // §7): 8 between the two keys, 16 from the
                    // board when CHECK stands alone.
                    Spacer(
                        modifier = Modifier.height(
                            if (firstMissing != null) 8.dp else 16.dp
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .offset(y = checkDip)
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (checkInk) {
                                    Modifier.background(inkControlFill())
                                } else {
                                    Modifier.border(
                                        1.dp, scheme.outline, RoundedCornerShape(8.dp)
                                    )
                                }
                            )
                            .semantics { contentDescription = "Check guard state" }
                            .then(
                                if (interactive) {
                                    Modifier.pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                // Dispatch first, then
                                                // record the NEW sweep's
                                                // generation — the press
                                                // is answered only by
                                                // the sweep it caused.
                                                // A press while CHECKING
                                                // re-dispatches and
                                                // re-bases (Critic C-2):
                                                // the verb itself is the
                                                // escape from a query
                                                // that never returns; no
                                                // timer ever declares
                                                // anything here.
                                                onCheckClick()
                                                checkPressGen =
                                                    WatchStore.currentSweepGen()
                                                lastCheckLackName = null
                                                soundEngine?.playSeatKnock(
                                                    durationMs = 10,
                                                    amplitude01 = if (isNight) 0.06f else 0.1f,
                                                    pitchHz = 300f
                                                )
                                                tryAwaitRelease()
                                            }
                                        )
                                    }
                                } else Modifier
                            )
                            .testTag("park_check_key"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (checking) "CHECKING" else "CHECK",
                            style = PrimaryButtonTextStyle,
                            color = if (checkInk) onInkControlFill() else scheme.onBackground,
                            maxLines = 1
                        )
                    }
                    // The failed check's verdict line: print, not a
                    // control — full ink, the board's own lack dress,
                    // ruled under like a board line. It persists
                    // until the board reads whole or the drawer
                    // closes; it never blames a door that answered.
                    val lackName = lastCheckLackName
                    if (lackName != null && !checking) {
                        Spacer(modifier = Modifier.height(16.dp))
                        ParkBoardRow(
                            name = "NOT PROVED",
                            word = lackName,
                            wordInk = ParkWordInk.FULL
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // (4) Sound — the shipped sled, moved whole. PARK's
                // row grammar: name plate left, margin word (the
                // state's one rendering — it flips on arrival, when
                // the throw lands, never mid-throw), sled right.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
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
                                    this.soundEngine = soundEngine
                                    isChecked = soundEnabled
                                    onToggleRequested = { onToggleSound() }
                                }
                            },
                            update = { view ->
                                view.soundEngine = soundEngine
                                if (view.isChecked != soundEnabled) {
                                    view.isChecked = soundEnabled
                                }
                                view.onToggleRequested = { onToggleSound() }
                            },
                            modifier = Modifier
                                .width(76.dp)
                                .height(34.dp)
                                .testTag("park_sound_sled")
                        )
                        if (isSealed) {
                            // Read-only under the seal: the sled is
                            // print; the overlay takes the touch.
                            Box(
                                modifier = Modifier
                                    .width(76.dp)
                                    .height(34.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {}
                            )
                        }
                    }
                }
                ParkHairline()

                // (5) Shorts + Reels — the sniper's sled, seated
                // with the Sound sled in the same row grammar
                // (Truth Car 3, UNA-01). Until this car the board's
                // SHORTS + REELS line printed a state whose only
                // source was the ViewModel's memory; the sled reads
                // and writes the persisted Guard row, and the board
                // above now prints the same fact the sled holds.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SHORTS + REELS",
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
                                    this.soundEngine = soundEngine
                                    isChecked = shortsReelsSniperEnabled
                                    onToggleRequested = { onToggleShortsReelsSniper() }
                                }
                            },
                            update = { view ->
                                view.soundEngine = soundEngine
                                if (view.isChecked != shortsReelsSniperEnabled) {
                                    view.isChecked = shortsReelsSniperEnabled
                                }
                                view.onToggleRequested = { onToggleShortsReelsSniper() }
                            },
                            modifier = Modifier
                                .width(76.dp)
                                .height(34.dp)
                                .testTag("park_shorts_sled")
                        )
                        if (isSealed) {
                            // Read-only under the seal, as the
                            // Sound sled: print, not a control.
                            Box(
                                modifier = Modifier
                                    .width(76.dp)
                                    .height(34.dp)
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
                // F-13 (second half): the rule above the stub is
                // dashed at the tear's spec — the stub reads as
                // torn in PARK's own grammar.
                ParkDashedRule()

                // (6b) The reception's stub (Empfang lane, grafted
                // onto HEAD at the issue-#14 merge — C-12): the
                // introduction itself lives here — this row is the
                // torn slip's stub, printing the standing Guard
                // total as its word; tapping re-opens the sheet,
                // which reprints with live states. One document at
                // two moments, never a link to a tour.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .then(
                            if (interactive) {
                                Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onOpenReception() }
                            } else Modifier
                        )
                        .testTag("park_reception_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SETUP + INTRODUCTION",
                        style = PillBadgeTextStyle,
                        color = scheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        // One datum, one dress (F-11's logic): the
                        // Guard word is telemetry — Anonymous Pro
                        // Bold 15, as on the sheet's own total. The
                        // "OPEN" fallback is the row's invitation
                        // word when no Guard line stands (acquitted
                        // by name, Pforte's ruling on Glatt G-2) —
                        // never a Guard fact. While the Armed
                        // Handover stands it speaks here first
                        // (N-2): one fact, one print, extended to
                        // the stub — its own slot, per C-13's
                        // amendment of §6.9.
                        text = handoverText
                            ?: guardLineFor(snapshot, nowMono)?.text ?: "OPEN",
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp,
                        color = scheme.onBackground
                    )
                }
                ParkHairline()

                // (6b) The foot colophon (§1.3/§4.8 + §4.3, wave-2):
                // LAST PROVED lives here now, demoted from the
                // header — a six-permission complete-set instant is
                // a colophon fact, not the four's headline. Beside
                // it the board signs its work like the receipt signs
                // its: GUARD ···· {n}/4 · {HH:mm}.
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "LAST PROVED",
                        style = PillBadgeTextStyle,
                        color = scheme.onSurfaceVariant
                    )
                    val proved = snapshot?.lastProvedWallMillis
                    if (proved != null) {
                        Spacer(modifier = Modifier.width(10.dp))
                        val provedDate = Date(proved)
                        val sameDay = SimpleDateFormat("yyyyMMdd", Locale.US)
                            .format(provedDate) ==
                            SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
                        Text(
                            text = SimpleDateFormat("HH:mm", Locale.US).format(provedDate) +
                                (if (sameDay) "" else
                                    " · " + SimpleDateFormat("dd MMM", Locale.US)
                                        .format(provedDate)) +
                                " · SELF-REPORT",
                            fontFamily = AnonymousProFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp,
                            color = scheme.onBackground
                        )
                    }
                }
                if (snapshot != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "GUARD ···· ${WatchStore.countedWarranted(snapshot, nowMono)}/4 · " +
                            SimpleDateFormat("HH:mm", Locale.US).format(Date()),
                        fontFamily = AnonymousProFamily,
                        fontSize = 11.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.testTag("park_guard_footline")
                    )
                }

                // (7) The spec plate — furniture at the foot.
                Spacer(modifier = Modifier.height(24.dp))
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
                    text = PLATE_BODY.map { line ->
                        if (line.startsWith("PEEK:")) {
                            if (peekMinutes == 0) "PEEK: OFF"
                            else "PEEK: $peekMinutes MINUTES, THEN SHUT AGAIN"
                        } else {
                            line
                        }
                    }.joinToString("\n"),
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

/** The board word's ink tier (F-B2) — form first: the weight of
 *  the ink says which kind of state it is before the word is read.
 *  QUIET = proved/at rest, HALF = granted but stale (audit P-4,
 *  Linea F-1: 0.60 of primary ink), FULL = a measured lack. */
private enum class ParkWordInk { QUIET, HALF, FULL }

/** One board line: name plate left, state word right, ruled under.
 *  A never-measured line prints its NAME ONLY — no word, no fill. */
@Composable
private fun ParkBoardRow(
    name: String,
    word: String?,
    wordInk: ParkWordInk,
    ageWord: String? = null,
    // Issue #14's Setup Ledger print: a second, quieter line under
    // the row (e.g. "WAS SET AT SETUP — SINCE LOST"); null = no line.
    subLine: String? = null
) {
    val scheme = MaterialTheme.colorScheme
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            if (ageWord != null) {
                Text(
                    text = ageWord,
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            if (word != null) {
                Text(
                    text = word,
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp,
                    color = when (wordInk) {
                        ParkWordInk.QUIET -> scheme.onSurfaceVariant
                        ParkWordInk.HALF -> scheme.onBackground.copy(alpha = 0.60f)
                        ParkWordInk.FULL -> scheme.onBackground
                    }
                )
            }
        }
        ParkHairline()
        if (subLine != null) {
            Text(
                text = subLine,
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.testTag("park_board_sub_$name")
            )
        }
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
    wordInk: ParkWordInk,
    enabled: Boolean,
    soundEngine: TactileSoundEngine?,
    isSealed: Boolean,
    onToggle: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
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
                    color = when (wordInk) {
                        ParkWordInk.QUIET -> scheme.onSurfaceVariant
                        ParkWordInk.HALF -> scheme.onBackground.copy(alpha = 0.60f)
                        ParkWordInk.FULL -> scheme.onBackground
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Box {
                AndroidView(
                    factory = { ctx ->
                        SledSwitchView(ctx).apply {
                            chromeMode = true
                            this.soundEngine = soundEngine
                            isChecked = enabled
                            onToggleRequested = { onToggle() }
                        }
                    },
                    update = { view ->
                        view.soundEngine = soundEngine
                        if (view.isChecked != enabled) {
                            view.isChecked = enabled
                        }
                        view.onToggleRequested = { onToggle() }
                    },
                    modifier = Modifier
                        .width(76.dp)
                        .height(34.dp)
                        .testTag("park_social_sled")
                )
                if (isSealed) {
                    // Read-only under the seal, as the sound sled:
                    // the sled is print; the overlay takes the touch.
                    Box(
                        modifier = Modifier
                            .width(76.dp)
                            .height(34.dp)
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

/** A tier eyebrow (§3): Atkinson bold small caps in quiet ink —
 *  rank is designed by order and label, never by shrinking a row. */
@Composable
private fun ParkTierEyebrow(text: String) {
    Text(
        text = text,
        fontFamily = AtkinsonHyperlegibleFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 0.6.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}

/** The age of a measurement, printed (§4.4): NOW inside five
 *  seconds, seconds to a minute, minutes beyond. An age is a fact
 *  about the measurement, never about the grant. */
internal fun parkAgeWord(ageMs: Long): String = when {
    ageMs < 5_000L -> "NOW"
    ageMs < 60_000L -> "${ageMs / 1_000L}S AGO"
    else -> "${ageMs / 60_000L}M AGO"
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

/** The tear's rule, PARK-side (F-13): dashed at the reception
 *  tear's spec — 1dp of ink at 35%, dash 3dp / gap 3dp — drawn
 *  above the reception stub row only; every other PARK hairline
 *  stays solid. */
@Composable
private fun ParkDashedRule() {
    val ink = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        val dash = 3.dp.toPx()
        drawLine(
            color = ink,
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = size.height,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash), 0f)
        )
    }
}
