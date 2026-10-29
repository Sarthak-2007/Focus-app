package com.example.ui.components

import android.os.SystemClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
    "WHEEL SETS TIME · SLIDE ARMS · THE HOLD BREAKS",
    "HOLD: 60 SECONDS, BANKED EVERY 15 · ONE BREAK A WEEK",
    "PEEK: 2 MINUTES, THEN SHUT AGAIN",
    "COLOUR IS A VERB: ORANGE = COMMIT · INK = SHUT · GREY = PEEK · PORCELAIN = OPEN",
    // UNA-05 (Truth Car 3, words half; Hale W-11, Una's brief):
    // the "FIRED AS SET" clause is STRUCK in this lane. It was
    // falsified on his phone (Oct 29, 20:00 SLEEP no-fire) and the
    // merged tree may print it again only when Wache's F1–F5 are in
    // the same build AND a live fire has been re-verified — that
    // restoration is a merge-time act on that evidence, not before.
    "ROUTINES: SET BY HAND ON SHIELD",
    "SITES: KEPT WITH APPS ON SHIELD · DATA: ON THIS PHONE ONLY",
    "STUDY HOURS: IN YPT",
    "SOUND: FLOAT-PCM, 4 PARTIALS · POWER: YOUR PHONE"
)

@Composable
fun ParkDrawer(
    visible: Boolean,
    snapshot: WatchSnapshot?,
    isSealed: Boolean,
    isNight: Boolean,
    soundEnabled: Boolean,
    shortsReelsSniperEnabled: Boolean,
    soundEngine: TactileSoundEngine?,
    themeReadout: String,
    parkReturnTick: Int,
    onDismiss: () -> Unit,
    onFixClick: (WatchItemId) -> Unit,
    onToggleSound: () -> Unit,
    onToggleShortsReelsSniper: () -> Unit,
    socialSitesEnabled: Boolean,
    onToggleSocialSites: () -> Unit,
    onOpenReception: () -> Unit = {},
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

    // The FIX key's dip: pressed on DOWN, held for the whole absence,
    // resolved on the return (see the return effect below).
    var fixPressed by remember { mutableStateOf(false) }
    var fixPressedItem by remember { mutableStateOf<WatchItemId?>(null) }
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
                    .padding(PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp))
            ) {
                // (1) Header + the LAST PROVED telltale. The instant
                // lives HERE and nowhere else; the slot is blank
                // until the first complete set lands.
                Text(
                    text = "GUARD",
                    style = DrawerHeaderTextStyle,
                    color = scheme.onBackground
                )
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
                            text = SimpleDateFormat("HH:mm", Locale.US).format(Date(proved)) +
                                " · SELF-REPORT",
                            fontFamily = AnonymousProFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp,
                            color = scheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // (2) The board: nine ruled lines, fixed order.
                if (snapshot != null) {
                    WatchItemId.PERMISSIONS.forEach { item ->
                        ParkBoardRow(
                            name = item.boardName,
                            word = when (WatchStore.permPrintAt(snapshot, item, nowMono)) {
                                PermPrint.GRANTED -> "GRANTED"
                                PermPrint.MISSING -> "MISSING"
                                PermPrint.STALE -> "STALE"
                                PermPrint.UNMEASURED -> null
                            },
                            wordIsLack = when (WatchStore.permPrintAt(snapshot, item, nowMono)) {
                                PermPrint.GRANTED -> false
                                else -> true
                            },
                            subLine = if (
                                setupStampMask != null &&
                                item in WatchItemId.COUNTED &&
                                setupStampMask and
                                    (1 shl WatchItemId.COUNTED.indexOf(item)) != 0 &&
                                WatchStore.permPrintAt(snapshot, item, nowMono) ==
                                    PermPrint.MISSING
                            ) {
                                // The Setup Ledger's PARK print
                                // (§3 Proposal 2): MISSING only —
                                // STALE is aged proof, not loss,
                                // and the board already prints it
                                // as the word. Standing print while
                                // the regression stands; no date,
                                // no count — capability memory. The
                                // mask itself is fresh-GRANTED-only
                                // by construction (doorsMaskAt), so
                                // this line can never testify to a
                                // loss nobody measured (C-14).
                                "WAS SET AT SETUP — SINCE LOST"
                            } else {
                                null
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
                                wordIsLack = m?.interceptor != InterceptorState.RUNNING,
                                enabled = socialSitesEnabled,
                                soundEngine = soundEngine,
                                isSealed = isSealed,
                                onToggle = onToggleSocialSites
                            )
                        } else {
                            ParkBoardRow(
                                name = item.boardName,
                                word = word,
                                wordIsLack = m?.interceptor != InterceptorState.RUNNING
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
                                wordIsLack = true,
                                enabled = socialSitesEnabled,
                                soundEngine = soundEngine,
                                isSealed = isSealed,
                                onToggle = onToggleSocialSites
                            )
                        } else {
                            ParkBoardRow(name = item.boardName, word = null, wordIsLack = true)
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
                                                    // The engine's quietest
                                                    // existing voice: the seat
                                                    // knock at its floor.
                                                    soundEngine?.playSeatKnock(
                                                        durationMs = 10,
                                                        amplitude01 = if (isNight) 0.06f else 0.1f,
                                                        pitchHz = 300f
                                                    )
                                                    onFixClick(item)
                                                    tryAwaitRelease()
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
 *  A never-measured line prints its NAME ONLY — no word, no fill. */
@Composable
private fun ParkBoardRow(
    name: String,
    word: String?,
    wordIsLack: Boolean,
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
    wordIsLack: Boolean,
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
                    color = if (wordIsLack) scheme.onBackground else scheme.onSurfaceVariant
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
