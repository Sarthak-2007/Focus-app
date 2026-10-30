package com.example.ui.components

import android.os.SystemClock
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RoutineEntity
import androidx.compose.foundation.relocation.bringIntoViewRequester
import com.example.data.ReturnChit
import com.example.data.ReturnChitState
import com.example.data.RoutineSchedule
import com.example.service.PermPrint
import com.example.service.TactileSoundEngine
import com.example.service.WatchItemId
import com.example.service.WatchSnapshot
import com.example.service.WatchStore
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AppTitleTextStyle
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.BodyTextStyle
import com.example.ui.theme.DoorJobTextStyle
import com.example.ui.theme.DrawerHeaderTextStyle
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.MonolithKin
import com.example.ui.theme.PillBadgeTextStyle
import com.example.ui.theme.PrimaryButtonTextStyle
import kotlin.math.roundToInt
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ============================================================================
 * THE RECEPTION (Empfang lane, Oct 30) — the first-open setup +
 * introduction sheet. Sarthak's order: "Build a setup and
 * introduction stuff that introduces you with app when u open."
 *
 * ONE printed sheet, not a paged wizard — the receipt slip's
 * family: nameplate, ruled movements, a total over a tear rule,
 * one ink key at the foot. Three movements, per the ratified
 * ceiling (Ren, mail/empfang/0058): the identity, the four door
 * cards, ONE confirmation — "how a session runs" and the Shield
 * door live inside the confirmation, never on screens of their
 * own. The four Settings excursions are Android's, not the
 * sheet's.
 *
 * Truth law: every print derives at render from the Watch
 * Store's snapshot — the SAME snapshot and the SAME derivation
 * (WatchStore.permPrintAt) the PARK board reads, so the sheet
 * and the board can never disagree. A state that cannot be read
 * prints NOT CHECKED, never granted. The doors' plain words are
 * a reprint of the board's grammar, never a second fact:
 * SET ≡ GRANTED, NOT SET ≡ MISSING, UNPROVED ≡ STALE (Gust,
 * K-1, locked).
 *
 * The sheet shows on first open until DONE stamps the setup
 * (SetupStore, chrome prefs, committed). Leaving — BACK, or the
 * Shield door — stamps nothing and costs nothing: the sheet
 * re-offers at the next cold start and waits in PARK, whose stub
 * row re-opens THIS sheet (the same document reprinted with live
 * states — day forty's printing is day one's). Once stamped it
 * never interrupts again. DONE is live at any door state: a man
 * who enters with a door unset meets the header's Guard line
 * printing what is missing, never a trap and never a nag.
 *
 * Motion is KIN-DRAWER's: rise 280 SEAT; a stamping DONE
 * leaves 420 (260 tear + 160 THROW); every other leave is
 * 160 THROW.
 * ============================================================================
 */

/** The sheet's door words — plain first-open reprints of the
 *  board's grammar, derived from the same PermPrint (K-1). */
fun receptionDoorWord(print: PermPrint): String = when (print) {
    PermPrint.GRANTED -> "SET"
    PermPrint.MISSING -> "NOT SET"
    PermPrint.STALE -> "UNPROVED"
    // The enum grew on main after this sheet was written (issue
    // #58 F1 + issue #94, both landed while the lane was parked);
    // the two new prints take the sheet's own locked words for
    // the facts they name: a stale denial is still a door NOT
    // SET (the lack is the fact — permPrintAt keeps MISSING_STALE
    // distinct precisely so lack-consumers read it as missing),
    // and an unprovable grant is UNPROVED, the same word the
    // board prints and the word this sheet already gives STALE.
    PermPrint.MISSING_STALE -> "NOT SET"
    PermPrint.UNPROVED -> "UNPROVED"
    PermPrint.UNMEASURED -> "NOT CHECKED"
}

/** First-open gate: the sheet opens itself only while the setup
 *  is unstamped. Once stamped it never interrupts again. */
fun receptionInitiallyOpen(setupCompleted: Boolean): Boolean = !setupCompleted

/** What a door DOES, in his life — never spec words (M-3). The
 *  rank word leads: interceptor / witness / anchor / mail room. */
fun receptionDoorJob(item: WatchItemId): String = when (item) {
    WatchItemId.ACCESSIBILITY ->
        // §5 stranger line (Door 1 privacy) rides the job line.
        // C-16 amendment: the permission sentence alone prints —
        // "NOTHING LEAVES THIS PHONE" does not follow from a
        // manifest (the app hands URLs and intents to Android by
        // design), so the overclaim is struck; the verifiable
        // half stands.
        "THE INTERCEPTOR. Shuts the apps you named, the moment they open. " +
            "MONOLITH HOLDS NO NETWORK PERMISSION."
    WatchItemId.USAGE_ACCESS ->
        "THE WITNESS. Tells Monolith which app is in front, so the seal can be proved."
    WatchItemId.DEVICE_ADMIN ->
        // S-6 job cure + §5 stranger line (Door 3 way-back).
        "THE ANCHOR. While this stands, Android refuses to uninstall Monolith. " +
            "TO TAKE IT BACK: SWITCH MONOLITH OFF IN THAT SAME LIST — " +
            "THEN IT UNINSTALLS LIKE ANY APP."
    WatchItemId.NOTIFICATION_LISTENER ->
        // S-10 job cure + §5 stranger line (Door 4 privacy).
        "THE MAIL ROOM. Holds the shut apps' messages while a session runs — " +
            "you read them in the digest. " +
            "HELD ON THIS PHONE, IN THE DIGEST."
    else -> ""
}

/** The escort line (M-7/M-8): what he will see on the excursion,
 *  and — for the two doors Android warns about — the warning
 *  named BEFORE the jump, so it cannot win at 4 AM. */
fun receptionDoorPrepare(item: WatchItemId): String = when (item) {
    WatchItemId.ACCESSIBILITY ->
        "YOU'LL SEE A LIST OF APPS. FIND MONOLITH, SWITCH IT ON, COME BACK. " +
            "ANDROID WARNS IN STRONG WORDS — THAT WARNING IS STANDARD FOR EVERY " +
            "BLOCKING APP. IT IS THE DOOR WORKING."
    WatchItemId.USAGE_ACCESS ->
        "YOU'LL SEE A LIST OF APPS. FIND MONOLITH, ALLOW USAGE ACCESS, COME BACK."
    WatchItemId.DEVICE_ADMIN ->
        // S-5: the FIX opens the DeviceAdminSettings LIST, not
        // Monolith's own page — the escort names the list.
        "YOU'LL SEE A LIST OF DEVICE ADMINS. FIND MONOLITH, ACTIVATE, COME BACK."
    WatchItemId.NOTIFICATION_LISTENER ->
        "YOU'LL SEE A LIST OF APPS. FIND MONOLITH, ALLOW, COME BACK. " +
            "ANDROID MAY WARN — THAT WARNING IS STANDARD. IT IS THE DOOR WORKING."
    else -> ""
}

/** The confirmation's total: the doors counted as they stand at
 *  this instant, and the sentences that follow from them
 *  (M-5/M-6). [count] is null while any counted door is
 *  unmeasured — a total is never printed over an unread door.
 *
 *  S-1 (audit, merge-blocking): the total prices EVERY lack,
 *  per print KIND, in COUNTED order — composition, never
 *  precedence (C-10; Wave-2 comment cure: this doc previously
 *  described the old branch-wins reading, under which a MISSING
 *  door erased every STALE door; that reading is dead in code,
 *  in tests, and now here). Each MISSING door prices as its
 *  acquitted absence sentence; each STALE door — a granted door
 *  whose proof aged out — prices as aged proof, never as
 *  absence; the two kinds compose in one total (Blank item 6:
 *  every lacking door is priced; the one-lack case prints
 *  exactly as before). All fresh-granted → S-9's full stop. */
data class ReceptionTotal(val count: Int?, val sentence: String)

/** The acquitted absence sentence for one MISSING door (A-2,
 *  verbatim; S-6: the admin branch drops "MID-SESSION"). */
private fun receptionAbsenceSentence(item: WatchItemId): String = when (item) {
    WatchItemId.ACCESSIBILITY, WatchItemId.USAGE_ACCESS ->
        "WITHOUT ${item.boardName}, BLOCKING CANNOT PROVE ITSELF — " +
            "SESSIONS WILL NOT SEAL."
    WatchItemId.DEVICE_ADMIN ->
        "WITHOUT DEVICE ADMIN, MONOLITH CAN BE UNINSTALLED."
    WatchItemId.NOTIFICATION_LISTENER ->
        "WITHOUT THE LISTENER, MESSAGES ARRIVE AS THEY COME — NOTHING IS HELD."
    else -> "THE DOORS ARE STILL BEING READ."
}

/** The aged-proof sentence for one STALE door (S-1, as amended
 *  by Stachel C-09): it asserts ONLY the proof's age — never
 *  "IS SET". STALE means the proof aged, not that the grant is
 *  known to stand; the grant may have been revoked inside those
 *  46 s, which is the entire reason the print exists. Words are
 *  the audit's S-12 gloss, promoted to a full sentence. */
private fun receptionAgedProofSentence(item: WatchItemId): String =
    "THE LAST PROOF OF ${item.boardName} AGED OUT — CHECK RE-PROVES IT."

fun receptionTotal(snapshot: WatchSnapshot?, nowMono: Long): ReceptionTotal {
    if (snapshot == null) {
        return ReceptionTotal(null, "THE DOORS ARE STILL BEING READ.")
    }
    val prints = WatchItemId.COUNTED.map { it to WatchStore.permPrintAt(snapshot, it, nowMono) }
    if (prints.any { it.second == PermPrint.UNMEASURED }) {
        return ReceptionTotal(null, "THE DOORS ARE STILL BEING READ.")
    }
    // The count is the fresh-GRANTED count — the same definition
    // the Guard line's n/4 carries everywhere (countedWarranted).
    val count = prints.count { it.second == PermPrint.GRANTED }
    // EVERY lack is priced, in COUNTED order, each in its own
    // kind's words (C-10: a MISSING branch must never erase a
    // STALE door from the total, nor the reverse). The sentence
    // set beside the count is what keeps 0/4-over-aged-doors
    // from reading as absence: the count says "fresh proofs",
    // the sentences name the rest, door by door.
    val lacks = prints.filter { it.second != PermPrint.GRANTED }
    val sentence = if (lacks.isEmpty()) {
        "ALL FOUR DOORS SET."
    } else {
        lacks.joinToString(" ") { (item, print) ->
            when (print) {
                PermPrint.MISSING -> receptionAbsenceSentence(item)
                PermPrint.STALE -> receptionAgedProofSentence(item)
                else -> "THE DOORS ARE STILL BEING READ."
            }
        }
    }
    return ReceptionTotal(count, sentence)
}

/** The counted doors as a mask (bit i = COUNTED[i] GRANTED at
 *  this instant) — the Setup Ledger's derivation. STALE does
 *  NOT set a bit: aged proof was not a set door at the stamp,
 *  and the ledger never upgrades it. Null snapshot → 0. */
fun doorsMaskAt(snapshot: WatchSnapshot?, nowMono: Long): Int {
    if (snapshot == null) return 0
    var mask = 0
    WatchItemId.COUNTED.forEachIndexed { index, item ->
        if (WatchStore.permPrintAt(snapshot, item, nowMono) == PermPrint.GRANTED) {
            mask = mask or (1 shl index)
        }
    }
    return mask
}

/** The Return Chit's printed line (mechanism spec §1.6) — one
 *  line, in the visited card only. Null for DEPARTED (nothing
 *  prints until judged) and SPENT (nothing re-prints). The
 *  NEXT suffix rides only the two states where a next step is
 *  a known fact. ALL FOUR SET rides only REPAIRED with no
 *  next door: the host's next-door search excludes the visited
 *  door itself, so a null next door proves only the OTHER
 *  three — for UNCHANGED the visited door is itself not set,
 *  and the suffix would contradict the line it closes (the
 *  residual S-1, cured here: an UNCHANGED chit with no next
 *  door prints no suffix; its own card is the next step). */
fun chitLineFor(chit: ReturnChit): String? {
    val dep = receptionDoorWord(chit.departurePrint)
    val next = chit.nextDoor
    val suffix = when {
        next != null -> " · NEXT — ${next.boardName}."
        chit.state == ReturnChitState.RETURNED_REPAIRED -> " · ALL FOUR SET."
        else -> ""
    }
    return when (chit.state) {
        ReturnChitState.RETURNED_REPAIRED -> "WENT OUT $dep · CAME BACK SET.$suffix"
        ReturnChitState.RETURNED_UNCHANGED -> "WENT OUT $dep · CAME BACK NOT SET.$suffix"
        ReturnChitState.RETURNED_UNREAD -> "WENT OUT $dep · CAME BACK NOT CHECKED."
        ReturnChitState.RETURNED_DARK -> "WENT OUT $dep · RETURN NOT READ."
        else -> null
    }
}

/** The short list form (F-5): the shared frame — a list, find
 *  Monolith, come back — is printed ONCE, on the first unset
 *  card. Later cards print only their door-specific clause,
 *  plus their Android warning WHOLE where one exists (A-6:
 *  the warnings' truth is acquitted; their repetition is not
 *  owed). The admin form follows S-5's cured escort noun. */
fun receptionDoorPrepareShort(item: WatchItemId): String = when (item) {
    WatchItemId.ACCESSIBILITY ->
        "FIND MONOLITH IN THE LIST, SWITCH IT ON. " +
            "ANDROID WARNS IN STRONG WORDS — THAT WARNING IS STANDARD " +
            "FOR EVERY BLOCKING APP. IT IS THE DOOR WORKING."
    WatchItemId.USAGE_ACCESS ->
        "FIND MONOLITH IN THE LIST, ALLOW USAGE ACCESS."
    WatchItemId.DEVICE_ADMIN ->
        "FIND MONOLITH IN THE DEVICE ADMIN LIST, ACTIVATE."
    WatchItemId.NOTIFICATION_LISTENER ->
        "FIND MONOLITH IN THE LIST, ALLOW. " +
            "ANDROID MAY WARN — THAT WARNING IS STANDARD. " +
            "IT IS THE DOOR WORKING."
    else -> ""
}

/** The next routine, named from the rows themselves (Muede's
 *  end-feeling: the seeded day is handed over, not discovered).
 *
 *  S-2 (audit): the struck clause "IT FIRES AS SET" dies. The
 *  line derives from RoutineSchedule's next start over LIVE
 *  rows only (enabled && daysMask != 0 — RoutineSchedule.isLive,
 *  the scheduler's own predicate): the routine whose next start
 *  is soonest after [nowMillis], printed as name + its start
 *  time + its home, never a firing claim. Null when no live
 *  routine stands — no routine is ever invented for the line. */
fun receptionFirstRoutineLine(routines: List<RoutineEntity>, nowMillis: Long): String? {
    val next = routines
        .filter { RoutineSchedule.isLive(it) }
        .mapNotNull { routine ->
            RoutineSchedule.nextStartMillis(routine, nowMillis)?.let { routine to it }
        }
        .minByOrNull { it.second }
        ?.first ?: return null
    val hh = (next.startMinutes / 60).toString().padStart(2, '0')
    val mm = (next.startMinutes % 60).toString().padStart(2, '0')
    return "NEXT ROUTINE — ${next.name.uppercase()} · $hh:$mm · SET ON SHIELD"
}

/** The chit's expiry law (Stachel C-15): a judged chit prints
 *  only while the door's CURRENT print still agrees with its
 *  verdict — REPAIRED stands only over a fresh GRANTED,
 *  UNCHANGED only over MISSING. UNREAD and DARK claim nothing
 *  about the door's state and print at any current print.
 *  Without this law the line ages into a contradiction
 *  standing under the very word that refutes it. */
fun chitPrintsAgainst(chit: ReturnChit, current: PermPrint): Boolean =
    when (chit.state) {
        ReturnChitState.RETURNED_REPAIRED -> current == PermPrint.GRANTED
        ReturnChitState.RETURNED_UNCHANGED -> current == PermPrint.MISSING
        ReturnChitState.RETURNED_UNREAD,
        ReturnChitState.RETURNED_DARK -> true
        else -> false
    }

/** The chit line as printed: on RETURNED_REPAIRED its SET
 *  stands bold in ink (the testimony's one emphasis — Funke
 *  §1.6's span, Bauer's ink colour); every other state
 *  prints flat. */
fun chitLineText(line: String, repaired: Boolean, ink: Color): AnnotatedString =
    if (!repaired) {
        AnnotatedString(line)
    } else {
        buildAnnotatedString {
            val idx = line.indexOf("SET.")
            if (idx < 0) {
                append(line)
            } else {
                append(line.substring(0, idx))
                withStyle(
                    SpanStyle(fontWeight = FontWeight.Bold, color = ink)
                ) {
                    append("SET.")
                }
                append(line.substring(idx + 4))
            }
        }
    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReceptionSheet(
    visible: Boolean,
    snapshot: WatchSnapshot?,
    isFirstOpen: Boolean,
    isNight: Boolean,
    returnTick: Int,
    soundEngine: TactileSoundEngine?,
    firstRoutineLine: String?,
    onShown: () -> Unit,
    onFixClick: (WatchItemId) -> Unit,
    onCheckClick: () -> Unit,
    onOpenShield: () -> Unit,
    onStamp: () -> Unit = {},
    onDone: () -> Unit,
    chit: ReturnChit? = null,
    onDeparture: (WatchItemId, PermPrint) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    // KIN-DRAWER travel, full-screen: fraction 1 = below the
    // ground, 0 = seated. Exactly two rest heights.
    val fraction = remember { Animatable(1f) }
    var sheetHeightPx by remember { mutableStateOf(0) }

    // THE TEAR (Letzt F-1): two animatables, the counterfoil's
    // own pair — the torn block drops + fades on specA 180, the
    // rule's dashes halve on specB 260; the tear phase is the
    // longer of the two (260), then the host's 160 THROW runs
    // as always. Exit total for a stamping press: 420.
    val tearDrop = remember { Animatable(0f) }
    val tearDash = remember { Animatable(1f) }
    var tearing by remember { mutableStateOf(false) }
    // Latched at the stamping press BEFORE onStamp() flips the
    // host's setupStamped: the stub note above the tear is
    // gated on isFirstOpen, and the booking would un-print it
    // mid-tear (Letzt Flag B).
    var tearLatchedFirstOpen by remember { mutableStateOf(false) }
    // DONE's dip (Haut §4.4): 1dp of silent travel on DOWN;
    // on a stamping commit it does not return — the torn block
    // continues from it into the drop.
    var donePressed by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            // Every showing starts untorn — a PARK reprint
            // never inherits a spent tear.
            tearDrop.snapTo(0f)
            tearDash.snapTo(1f)
            tearing = false
            tearLatchedFirstOpen = false
            donePressed = false
            onShown()
            fraction.animateTo(0f, tween(MonolithKin.DRAWER_OPEN_MS, easing = MonolithKin.EasingSeat))
        } else if (fraction.value < 1f) {
            fraction.animateTo(1f, tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow))
        }
    }
    if (!visible && fraction.value >= 1f) return
    val settled = fraction.value == 0f
    val interactive = visible && settled && !tearing

    // The tear driver: both animatables run concurrently;
    // coroutineScope returns when the LONGER (dash, 260ms)
    // lands — the drop finishes at 180 and holds for the
    // remaining 80ms. Only then is the exit handed to the
    // host, whose visible flip runs the 160 THROW exactly as
    // always. Fires once per showing (tearing flips once;
    // the visible effect resets it on entry).
    LaunchedEffect(tearing) {
        if (!tearing) return@LaunchedEffect
        coroutineScope {
            launch {
                tearDrop.animateTo(
                    1f,
                    MonolithCurves.specA(MonolithCurves.DURATION_180)
                )
            }
            launch {
                tearDash.animateTo(
                    0.5f,
                    MonolithCurves.specB(MonolithCurves.DURATION_260)
                )
            }
        }
        onDone()
    }

    // Staleness re-derives on PARK's slow beat while the sheet
    // stands, so a door can go UNPROVED in front of him.
    var nowMono by remember { mutableStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(visible) {
        while (visible) {
            nowMono = SystemClock.elapsedRealtime()
            delay(5_000L)
        }
    }

    // The FIX dip is dress, not verdict (audit F-1/F-3): pressed
    // on DOWN, held for the whole absence, released when the
    // Return Chit leaves DEPARTED — the chit (SetupStore) is
    // judged host-side against the first measurement taken
    // after departure; the old 800 ms snapshot-guess is dead.
    // The firm click for a repaired door is the host's, at
    // resolution (Letzt A-5: the second channel, REPAIRED only).
    var fixPressed by remember { mutableStateOf(false) }
    var fixPressedItem by remember { mutableStateOf<WatchItemId?>(null) }
    LaunchedEffect(visible) {
        if (!visible) {
            fixPressed = false
            fixPressedItem = null
        }
    }
    LaunchedEffect(chit?.state) {
        val c = chit ?: return@LaunchedEffect
        if (c.state != ReturnChitState.DEPARTED && c.door == fixPressedItem) {
            fixPressed = false
            fixPressedItem = null
        }
    }

    // The chit's auto-scroll: when a verdict stands and the
    // sheet is seated, the visited card is brought into view —
    // for the man who left from a card now off-screen; already
    // in view is a no-op.
    val scrollState = rememberScrollState()
    val cardRequesters = remember {
        WatchItemId.COUNTED.associateWith { BringIntoViewRequester() }
    }
    LaunchedEffect(chit?.state, settled) {
        val c = chit ?: return@LaunchedEffect
        if (settled && c.state != ReturnChitState.DEPARTED &&
            c.state != ReturnChitState.SPENT
        ) {
            cardRequesters[c.door]?.bringIntoView()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .offset { IntOffset(0, (fraction.value * sheetHeightPx).roundToInt()) }
            .onSizeChanged { sheetHeightPx = it.height }
            .background(scheme.background)
            .testTag("reception_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // Scroll locks during the tear: "everything
                // above the rule motionless" holds against his
                // hand too, not only against the animation.
                .verticalScroll(scrollState, enabled = !tearing)
                .padding(PaddingValues(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 40.dp))
        ) {
            // The nameplate (C-1: the sheet wears the same name as
            // its PARK printing — one document, one name).
            Text(
                // S-13: the plate drops "· FIRST OPEN" — it names
                // the stamp, not the opening.
                text = "MONOLITH ML-01",
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SETUP + INTRODUCTION",
                style = DrawerHeaderTextStyle,
                color = scheme.onBackground
            )
            Spacer(modifier = Modifier.height(14.dp))
            ReceptionHairline()
            Spacer(modifier = Modifier.height(16.dp))

            // ---- MOVEMENT 1 · THE IDENTITY (M-1) ----
            // F-1: "THE DOOR" eyebrow killed — the sheet counts
            // four doors below; this movement is what it is.
            Text(
                text = "WHAT IT IS",
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            // S-7 identity cure, verbatim: the shut scope is the
            // ruled tier + the listed sites + Shorts/Reels.
            // F-2: the one-liner carries AppTitle weight — the
            // identity is the sheet's hero sentence.
            Text(
                text = "While a session runs, Monolith shuts the apps you ruled " +
                    "shut and the sites on your list — and backs you out of " +
                    "Shorts and Reels.",
                style = AppTitleTextStyle,
                color = scheme.onBackground
            )
            // F-3: the second sentence ("You seal a session…") is
            // killed — Movement 3 prints the beats in full.
            Spacer(modifier = Modifier.height(8.dp))
            // The boundary, in the register's replacement words
            // (C-4 seat kept; S-4 words cured).
            Text(
                text = "YOUR STUDY HOURS LIVE IN YPT. MONOLITH KEEPS NO SCORE " +
                    "AND GRADES NOTHING.",
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            // ---- MOVEMENT 2 · THE FOUR DOORS ----
            Text(
                text = "THE FOUR DOORS",
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Precomputed prints (F-5 needs to know which card
            // is the FIRST lack before any card renders).
            val prints = WatchItemId.COUNTED.map { item ->
                item to (snapshot?.let { WatchStore.permPrintAt(it, item, nowMono) }
                    ?: PermPrint.UNMEASURED)
            }
            val firstMissingIdx =
                prints.indexOfFirst { it.second == PermPrint.MISSING }
            prints.forEachIndexed { idx, (item, print) ->
                ReceptionDoorCard(
                    item = item,
                    print = print,
                    isNight = isNight,
                    interactive = interactive,
                    fixPressed = fixPressed && fixPressedItem == item,
                    soundEngine = soundEngine,
                    chit = chit?.takeIf {
                        it.door == item && chitPrintsAgainst(it, print)
                    },
                    requester = cardRequesters[item],
                    escortFull = idx == firstMissingIdx,
                    onFixPress = {
                        // The dip travels on DOWN (Haut §6: one
                        // continuous 1dp from press through the
                        // absence latch — no bounce).
                        fixPressed = true
                        fixPressedItem = item
                    },
                    onFix = {
                        // The commit (release-true): the chit is
                        // written BEFORE the pane intent fires —
                        // the departure is a fact before Android
                        // can take the process.
                        onDeparture(item, print)
                        onFixClick(item)
                    },
                    onFixCancel = {
                        // Dragged off / scroll-cancelled: the dip
                        // lifts, nothing departed.
                        fixPressed = false
                        fixPressedItem = null
                    },
                    onCheck = onCheckClick
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // The rank (M-5): the skip, when it comes, is informed.
            Text(
                // S-6: the third clause is cured — the anchor
                // guards the machine, it does not make blocking
                // unbreakable.
                text = "THE FIRST TWO MAKE BLOCKING REAL. THE THIRD GUARDS THE " +
                    "MACHINE ITSELF. THE FOURTH MAKES IT QUIET.",
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant
            )
            // The section's foot is the header's own derivation —
            // one fact, one print (Gust, acquittal 7).
            val guard = guardLineFor(snapshot, nowMono)
            if (guard != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = guard.text,
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp,
                    color = scheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // ---- MOVEMENT 3 · THE CONFIRMATION ----
            // How a session runs and the Shield door live HERE,
            // inside the confirmation (Ren's ratified ceiling) —
            // they never get screens of their own.
            Text(
                text = "HOW A SESSION RUNS",
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            ReceptionBeatLine(
                label = "ARM",
                line = "The wheel on the Deck sets the span. Nothing is shut yet."
            )
            ReceptionBeatLine(
                label = "SEAL",
                // S-7 SEAL cure, verbatim: shut-tier apps and
                // listed sites only — never "the named apps".
                line = "From that instant, the shut-tier apps and the listed " +
                    "sites are shut."
            )
            ReceptionBeatLine(
                label = "THE ROOM",
                // S-8 — INTERIM FALSEHOOD, DO NOT SHIP SILENTLY
                // (Wave-2, Pforte): this tree has no 20 s untouched
                // settle and its desk clock prints one figure, not
                // this beat's three. The line is true only when
                // issue #13's settle machinery composes with this
                // tree; whoever composes #13 + #14 re-words or
                // strikes this beat in that act. Flagged in
                // MERGE-PLAN.md item 6 since the graft; marked in
                // code here so a code-only review cannot miss it.
                line = "After twenty seconds untouched, the desk clock takes " +
                    "the face: the time, the time left, when it ends."
            )
            // PEEK and BREAK never share a line (C-3): two
            // mechanisms, two prices, printed apart.
            ReceptionBeatLine(
                label = "PEEK",
                line = "One app opens for two minutes, then shuts again. " +
                    "Taking it starts a 20-minute cooldown on that app."
            )
            ReceptionBeatLine(
                label = "BREAK",
                line = "Ten minutes. The peek doors open; the shut doors stay " +
                    "shut. The clock runs through it."
            )
            ReceptionBeatLine(
                label = "FINISH",
                // S-15 FINISH cure, verbatim.
                line = "A session that runs its course prints its receipt: " +
                    "what was held, and when it ended."
            )
            if (firstRoutineLine != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = firstRoutineLine,
                    style = PillBadgeTextStyle,
                    color = scheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Your apps: a door, never a second list (§4 of the
            // brief; M-10 — the first morning is never routed
            // through a list as a setup step).
            Text(
                // S-14: the rules line gains "BETWEEN SESSIONS".
                text = "YOUR RULES LIVE ON SHIELD — THE APPS AND SITES, EACH " +
                    "WITH YOUR RULING. CHANGE THEM ANY TIME BETWEEN SESSIONS.",
                style = BodyTextStyle,
                color = scheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(scheme.surfaceVariant)
                    .then(
                        if (interactive) {
                            Modifier.pointerInput(Unit) {
                                detectTapGestures(onPress = {
                                    // Haut §4.3 (T-2): the
                                    // sheet's largest silent act
                                    // gets its voice — a gate
                                    // knock, heavier than FIX's
                                    // errand whisper because
                                    // OPEN has no return verdict
                                    // to pay its mass later. One
                                    // press, all its mass up
                                    // front; no dip (the surface
                                    // itself leaves). The act
                                    // commits on release-true.
                                    soundEngine?.playSeatKnock(
                                        durationMs = 14,
                                        amplitude01 = if (isNight) 0.30f else 0.50f,
                                        pitchHz = 300f
                                    )
                                    if (tryAwaitRelease()) {
                                        onOpenShield()
                                    }
                                })
                            }
                        } else Modifier
                    )
                    .testTag("reception_open_list"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OPEN THE LIST",
                    style = PrimaryButtonTextStyle,
                    color = scheme.onBackground
                )
            }
            if (isFirstOpen) {
                // H-2's printed half: the excursion's standing
                // truth — the host law (§4.2 of the mechanism
                // spec) is what makes this line true; the two
                // are one fact and die together if either falls.
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "THE LIST LIVES ON SHIELD — COME BACK TO THE " +
                        "DECK AND THIS SHEET RISES AGAIN.",
                    style = PillBadgeTextStyle,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.testTag("reception_open_list_wayback")
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                // S-11 leave-line cure, verbatim.
                text = "LEAVE WHEN YOU LIKE — THE HEADER KEEPS NAMING THE " +
                    "FIRST THING MISSING.",
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            // The total over the tear (Gust's one move): the
            // doors as they stand at this instant, counted in ink
            // above the dashed rule the sheet separates on.
            val total = receptionTotal(snapshot, nowMono)
            if (total.count != null) {
                Text(
                    // F-11: the count is a datum — the telemetry
                    // family carries it (Anonymous Pro Bold 15),
                    // the sentence below stays prose.
                    text = "GUARD ${total.count}/4",
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp,
                    color = scheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = total.sentence,
                style = BodyTextStyle,
                color = scheme.onBackground
            )
            if (isFirstOpen || tearLatchedFirstOpen) {
                // F-12: the stub note moves ABOVE the tear — it
                // describes the part that stays, so it cannot
                // print on the torn-off part. (The latch holds
                // it through the tear, Letzt Flag B.)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "THIS SHEET WAITS IN PARK — SETUP + INTRODUCTION.",
                    style = PillBadgeTextStyle,
                    color = scheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    // Blank item 4 (audit §5), verbatim per Glatt's
                    // host-evidenced spec: PARK named by its real
                    // openings — the rail cap's unmarked end first
                    // (the unconditional door), the Guard line second.
                    text = "PARK OPENS BY TAKING THE RAIL CAP TO THE " +
                        "UNMARKED END OF THE RAIL, OR BY TAPPING THE " +
                        "GUARD LINE AT THE TOP RIGHT.",
                    style = PillBadgeTextStyle,
                    color = scheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            ReceptionTearRule(dashScale = tearDash.value)

            // THE TORN BLOCK (Letzt §1.6): everything below the
            // rule — and only that — drops + fades on a stamping
            // press. At rest tearDrop is 0: alpha 1, offset 0, a
            // reprint renders pixel-identical to before.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = 1f - tearDrop.value
                        translationY = tearDrop.value * 10.dp.toPx()
                    }
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // THE ONE EXIT (C-2: named for what it does — it
                // stamps the setup and hands over the Deck; it
                // seals nothing, and this sheet prints no kind
                // lies). The press books first (onStamp), tears,
                // and only then hands the host the exit (onDone);
                // reprints skip the tear entirely.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .offset(y = if (donePressed) 1.dp else 0.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(inkControlFill())
                        .then(
                            if (interactive) {
                                Modifier.pointerInput(Unit) {
                                    detectTapGestures(onPress = {
                                        // Haut §4.4 × Letzt §1.4 ×
                                        // Klang, composed: the dip
                                        // travels on DOWN in silence
                                        // (a stamp's sound is its
                                        // strike); the act commits
                                        // only on a clean release
                                        // inside the key (T-3's
                                        // release law — a drag-off
                                        // stamps nothing). At the
                                        // commit, G-01's order: the
                                        // stamp books FIRST, then
                                        // the tear pulse replaces
                                        // the firm click (T-4: one
                                        // act, one voice), then the
                                        // tear arms and the driver
                                        // runs it. A reprint's
                                        // press exits at once with
                                        // the click, untorn.
                                        donePressed = true
                                        val released = tryAwaitRelease()
                                        if (!released) {
                                            donePressed = false
                                        } else if (isFirstOpen) {
                                            onStamp()
                                            soundEngine?.playTearPulse()
                                            tearLatchedFirstOpen = true
                                            tearing = true
                                        } else {
                                            onDone()
                                            soundEngine?.playFirmClick()
                                            donePressed = false
                                        }
                                    })
                                }
                            } else Modifier
                        )
                        .testTag("reception_done"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TO THE DECK →",
                        style = PrimaryButtonTextStyle,
                        color = onInkControlFill()
                    )
                }
                // §5 (Blank (a)3): one line of act under DONE —
                // what the key does, and what it does not do.
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "THIS FINISHES THE SETUP. IT STARTS NOTHING, SEALS " +
                        "NOTHING, AND MAY BE PRESSED WITH DOORS UNSET.",
                    style = PillBadgeTextStyle,
                    color = scheme.onSurfaceVariant
                )
            }
        }
    }
}

/** One door card: the board row grown an escort — name + live
 *  word, the job in his life's words, the prepare line under a
 *  MISSING door only (S-12: an unread door carries no escort),
 *  and the door's own key (FIX for a lack, CHECK for an aged
 *  proof and for an unread door — a sweep is the only honest
 *  verb for both; SET carries no key). STALE cards also print
 *  the aged-proof gloss (S-12). */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReceptionDoorCard(
    item: WatchItemId,
    print: PermPrint,
    isNight: Boolean,
    interactive: Boolean,
    fixPressed: Boolean,
    soundEngine: TactileSoundEngine?,
    chit: ReturnChit? = null,
    requester: BringIntoViewRequester? = null,
    escortFull: Boolean = true,
    onFixPress: () -> Unit = {},
    onFix: () -> Unit,
    onFixCancel: () -> Unit = {},
    onCheck: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    // The chit's one printing exception: while an unspent
    // RETURNED_REPAIRED chit names this door, its word SET
    // stands in ink, not the standing dim — this printing only.
    val chitRepaired = chit?.state == ReturnChitState.RETURNED_REPAIRED
    if (print == PermPrint.GRANTED) {
        // F-6: a granted door collapses to one ruled line
        // (37dp against the expanded ~89dp) — name, SET in its
        // standing dim dress (A-3), hairline. The chit yields
        // it one growth (Fibel's interface ruling): its line
        // prints beneath the row for that printing only.
        val line = chit?.let { chitLineFor(it) }
        Column(
            modifier = Modifier
                .testTag("reception_door_${item.name}")
                .then(
                    if (requester != null) {
                        Modifier.bringIntoViewRequester(requester)
                    } else {
                        Modifier
                    }
                )
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.height(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.boardName,
                    style = PillBadgeTextStyle,
                    color = scheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = receptionDoorWord(print),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp,
                    color = if (chitRepaired) {
                        scheme.onBackground
                    } else {
                        scheme.onSurfaceVariant
                    }
                )
            }
            if (line != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chitLineText(line, chitRepaired, scheme.onBackground),
                    style = DoorJobTextStyle,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.testTag("reception_chit_line")
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ReceptionHairline()
        }
        return
    }
    Column(
        modifier = Modifier
            .testTag("reception_door_${item.name}")
            .then(
                if (requester != null) {
                    Modifier.bringIntoViewRequester(requester)
                } else Modifier
            )
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.boardName,
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = receptionDoorWord(print),
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp,
                color = if (print == PermPrint.GRANTED && !chitRepaired) {
                    scheme.onSurfaceVariant
                } else {
                    scheme.onBackground
                }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = receptionDoorJob(item),
            style = BodyTextStyle,
            color = scheme.onBackground
        )
        // The Return Chit's line — this card only, judged
        // excursions only (chitLineFor is null until then).
        val chitLine = chit?.let { chitLineFor(it) }
        if (chitLine != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chitLineText(chitLine, chitRepaired, scheme.onBackground),
                style = DoorJobTextStyle,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.testTag("reception_chit_line")
            )
        }
        if (print == PermPrint.STALE) {
            // S-12 gloss: UNPROVED keeps its word; the gloss was
            // the missing piece — the proof aged, CHECK re-proves.
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "THE LAST PROOF AGED OUT — CHECK RE-PROVES IT",
                // The named ladder, not inline values (Fein §C):
                // DoorJobTextStyle is the sheet's second voice.
                style = DoorJobTextStyle,
                color = scheme.onSurfaceVariant
            )
        }
        if (print == PermPrint.MISSING) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                // F-5: the full escort prints on the first
                // unset card only; later cards carry the short
                // list form (warnings whole, frame once).
                text = if (escortFull) {
                    receptionDoorPrepare(item)
                } else {
                    receptionDoorPrepareShort(item)
                },
                style = DoorJobTextStyle,
                color = scheme.onSurfaceVariant
            )
        }
        // Letzt F-3: PARK's visibility pair on both key blocks —
        // a key never appears or vanishes in one frame; a
        // repaired door's key throws out over 160 while the
        // card settles. The 10dp spacer rides inside, so the
        // animated envelope is the whole 58dp the key band
        // occupies standing (10 + 48). (F-6 note: a GRANTED
        // card takes the collapsed early return above, so the
        // exit pair serves every transition that stays in this
        // expanded form; the collapse glide is the repair
        // return's settle — deviation recorded in the merge
        // plan.)
        AnimatedVisibility(
            visible = print == PermPrint.MISSING,
            enter = expandVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingSeat)) +
                fadeIn(tween(MonolithKin.DRAWER_CLOSE_MS)),
            exit = shrinkVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow)) +
                fadeOut(tween(MonolithKin.DRAWER_CLOSE_MS))
        ) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .offset(y = if (fixPressed) 1.dp else 0.dp)
                        .clip(RoundedCornerShape(8.dp))
                        // F-4 ink discipline: FIX takes the
                        // CHECK keys' dress — ink remains on
                        // exactly one key in this sheet, DONE.
                        .background(scheme.surfaceVariant)
                        .then(
                            if (interactive) {
                                Modifier.pointerInput(item) {
                                    detectTapGestures(onPress = {
                                        // Haut §4.1/§5: feel on
                                        // DOWN (the seat knock +
                                        // the dip), the act on
                                        // release-true only — a
                                        // drag-off or a scroll
                                        // starting on this key
                                        // departs nothing.
                                        soundEngine?.playSeatKnock(
                                            durationMs = 10,
                                            amplitude01 = if (isNight) 0.06f else 0.1f,
                                            pitchHz = 300f
                                        )
                                        onFixPress()
                                        val released = tryAwaitRelease()
                                        if (released) {
                                            onFix()
                                        } else {
                                            onFixCancel()
                                        }
                                    })
                                }
                            } else Modifier
                        )
                        .testTag("reception_fix_${item.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FIX — ${item.boardName}",
                        style = PrimaryButtonTextStyle,
                        color = scheme.onBackground
                    )
                }
            }
        }
        // S-12: UNMEASURED gains the CHECK key beside STALE —
        // for an unread door a sweep is the only honest verb.
        AnimatedVisibility(
            visible = print == PermPrint.STALE || print == PermPrint.UNMEASURED,
            enter = expandVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingSeat)) +
                fadeIn(tween(MonolithKin.DRAWER_CLOSE_MS)),
            exit = shrinkVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow)) +
                fadeOut(tween(MonolithKin.DRAWER_CLOSE_MS))
        ) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(scheme.surfaceVariant)
                        .then(
                            if (interactive) {
                                Modifier.pointerInput(item) {
                                    detectTapGestures(onPress = {
                                        // Haut §4.2 (T-1 cure):
                                        // CHECK knocks — FIX's
                                        // identical call, voice
                                        // only, no dip ever — and
                                        // sweeps on release-true.
                                        soundEngine?.playSeatKnock(
                                            durationMs = 10,
                                            amplitude01 = if (isNight) 0.06f else 0.1f,
                                            pitchHz = 300f
                                        )
                                        if (tryAwaitRelease()) {
                                            onCheck()
                                        }
                                    })
                                }
                            } else Modifier
                        )
                        .testTag("reception_check_${item.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CHECK — ${item.boardName}",
                        style = PrimaryButtonTextStyle,
                        color = scheme.onBackground
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        ReceptionHairline()
    }
}

/** One beat of the confirmation: the beat's name over its one
 *  line. The beats never share a line (C-3). */
@Composable
private fun ReceptionBeatLine(label: String, line: String) {
    val scheme = MaterialTheme.colorScheme
    // F-9: the beats print as a ruled table — label in a fixed
    // 84dp column (Anonymous Pro, per the ladder), the line
    // beside it, the sheet's own hairline beneath each row.
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = label,
                style = PillBadgeTextStyle,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.width(84.dp)
            )
            Text(
                text = line,
                style = BodyTextStyle,
                color = scheme.onBackground,
                modifier = Modifier.weight(1f)
            )
        }
        ReceptionHairline()
    }
}

/** The house ruling: 1dp of ink at 18% — PARK's hairline, the
 *  same print ruling on this sheet. */
@Composable
private fun ReceptionHairline() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f))
    )
}

/** The tear rule: where the sheet separates when DONE is
 *  pressed and the stub stays in PARK. Built on
 *  PerforationRow's geometry (FocusSequencerScreen): dash
 *  3dp / gap 3dp, in dp (the old raw-px 8f/6f pattern shrank
 *  on denser screens and could never halve), the line
 *  centred in its 1dp height, tiled from the left edge.
 *  [dashScale] is the tear's settle — during a stamping DONE
 *  the dashes halve (3dp → 1.5dp) over specB 260 while the
 *  gap holds; at rest it is 1f and the rule prints as it
 *  always has. Ink at 35% stands (acquitted) — geometry is
 *  borrowed, tone is not. */
@Composable
private fun ReceptionTearRule(dashScale: Float = 1f) {
    val ink = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        val y = size.height / 2f
        val dash = 3.dp.toPx() * dashScale
        val gap = 3.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = ink,
                start = Offset(x, y),
                end = Offset(minOf(x + dash, size.width), y),
                strokeWidth = size.height
            )
            x += dash + gap
        }
    }
}
