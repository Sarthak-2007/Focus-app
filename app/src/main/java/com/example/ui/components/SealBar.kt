package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.NightColorSurfaceSheet
import com.example.ui.theme.TokenAlert
import com.example.service.WarrantState

// ============================================================================
// ISSUE #4 — THE SEAL BAR (the Deck strip's replacement)
// ============================================================================
// ONE horizontal instrument replaces the old ProofStripBand's text
// dump (BLOCK ON/OFF · APPS n · TIME ENDS HH:mm, over a seam line
// printing BIND … / PROOF: …). Nothing textual about the wire's
// proof grades renders here any more: the state is carried by a
// physical latch — a seal plate that travels a track — and by its
// lamp. The words BIND and PROOF never appear on this face.
//
// ONE DERIVATION (THE BOLT §2, landed by the issue-#4 closer pass
// after KRALL K-1 / VEX §A proved the v0 re-encoding false): the
// host folds its ONE warrant read — plus the session facts and
// the seal freeze — into a single [BoltInputs] value, and
// [boltStateFor] alone turns that value into the face (pose +
// lamp + window). No string word, no second derivation: this file
// never touches WatchStore, blockCellFor, or seamMarksFor, and
// the host no longer derives any BLOCK word for this face at all
// (the strip's blockCellFor/blockCell are deleted with the fold).
// The two stale laws stay separate, as they must: the warrant's
// 45 s monotonic print law (WatchStore.STALE_AFTER_MS) reaches
// the fold as [BoltInputs.printState] / [BoltInputs.readStale];
// the shield's 10 s beat law (MonolithRepository's
// SHIELD_GAP_THRESHOLD_MILLIS, over the shield loop's heartbeat)
// reaches it as [BoltInputs.shieldStale] — B-C1, landed by the
// DURCH-A4 port pass. Neither law is computed from the other's
// clock: the host derives each boolean from its own source and
// the fold only consumes them.
//
// The bar takes a 60dp address in the pinned stack (THE BOLT
// §8.1: housing 53→60dp, FEILE's grid — see SealBarParams), and
// the pinned stack's comment in TactileDeckScreen carries the
// same figure; the zone under the wheel does not change height
// by a pixel across the seal (the standing Δy = 0 law).
//
// The bar is NOT a control: the old strip's area had no tap action
// (the tappable sentence lives in RefusalSentenceSlot directly
// below, untouched), so the bar carries no click handler and no
// button styling — flat print, a raised plate, a lamp. Its state
// is spoken through semantics for accessibility instead.

/**
 * Every visual parameter of the instrument, in ONE place — the
 * designer re-skins the bar here without touching the composable
 * or the derivation. Colours that must follow the mode are read
 * from the Material scheme inside the composable; the two lamp
 * colours that are NOT scheme roles (the alert lamp) are pinned
 * here from the existing zero-green tokens.
 */
object SealBarParams {
    /** THE BOLT §8.1: housing 53→60dp (FEILE's grid) — the rent
     *  buys a state change, a threshold and an evidence channel
     *  the 53dp strip never carried (SALZ rent test). */
    val barHeight: Dp = 60.dp
    val barPaddingHorizontal: Dp = 12.dp

    val trackHeight: Dp = 36.dp
    val trackCorner: Dp = 3.dp

    /** THE BOLT §1: the bolt is 90×34dp and never grows — all
     *  width growth in a wider housing goes to the channel. */
    val plateWidth: Dp = 90.dp
    val plateHeight: Dp = 34.dp
    val plateCorner: Dp = 4.dp

    /** Plate travel, as a fraction of (track width − plate width). */
    const val plateOpenFraction: Float = 0f
    /** CAUGHT is ⅓, never ½ (THE BOLT §3): half reads as a resting
     *  state; the instrument has two resting positions and one
     *  caught pose. */
    const val plateUnprovenFraction: Float = 1f / 3f
    const val plateShutFraction: Float = 1f

    /** ENDING SOON carries NO pose change (THE BOLT §11, pose is
     *  truth): the v0 5dp back-off is DELETED — a backed-off bolt
     *  claims the seal is releasing. The carrier is the window
     *  inversion below. DUNKEL's dissent is on record in the design. */

    /** The jewel: ≤9dp HARD CAP (THE BOLT §6 glance gate — the
     *  luminous budget below is only passable if the jewel loses
     *  area while OFF's bed gains it). It rides in a bezel disc. */
    val lampDiameter: Dp = 9.dp
    val lampBezelDiameter: Dp = 17.dp
    /** RUNNING only: a 1dp-class halo ring in jewel tone gives the
     *  lit states a size difference hue alone failed at blur. */
    val lampHaloDiameter: Dp = 21.dp
    val lampGap: Dp = 12.dp

    /** OFF vs RUNNING glance budget (THE BOLT §6): the jewel's
     *  luminous total must stay ≤ this many proxy units — a ≥3×
     *  read at 10% brightness against OFF's exposed bed. Named in
     *  code so the gate is executable. */
    const val jewelLuminousBudget: Float = 41.9f

    /** The glance gate's arithmetic, computed over the elements
     *  that actually render (KRALL K-5 / HOLLIS H-4: the design's
     *  published figures priced a 644dp² bed that never existed
     *  and a bare brass disc RUNNING never wears). Areas are the
     *  params above; luminance is the colours' own relative
     *  luminance. These are MODEL totals — the render-side
     *  measurement lives in BoltRenderGateTest's pixel sums; no
     *  document may call either one "measured on renders" for the
     *  other. */
    fun bedExposedAreaDp2(): Float = plateWidth.value * plateHeight.value

    /** OFF's orange total: the fully exposed bed plus the painted
     *  socket interior (8dp × 8dp, OFF only). */
    fun offLuminousTotal(): Float =
        (bedExposedAreaDp2() + 64f) * warningOrange.luminance()

    /** A lit jewel's total for [state] at [night]: disc area ×
     *  its tone's luminance, plus the halo ring (annulus between
     *  the halo and bezel diameters) for the two states that
     *  carry it (RUNNING, FLOW). DETENTION_FAULT wears magenta
     *  with no halo; ENDING_SOON steps down to brass, no halo. */
    fun jewelLuminousTotal(state: SealBarState, night: Boolean): Float {
        val disc = (Math.PI * 4.5 * 4.5).toFloat()
        val halo = (Math.PI * (10.5 * 10.5 - 9.5 * 9.5)).toFloat()
        return when (state) {
            SealBarState.RUNNING, SealBarState.FLOW ->
                disc * jewelMagenta(night).luminance() +
                    halo * jewelMagenta(night).luminance()
            SealBarState.DETENTION_FAULT ->
                disc * jewelMagenta(night).luminance()
            SealBarState.BLOCKING, SealBarState.ENDING_SOON ->
                disc * jewelBrass.luminance()
            else -> 0f
        }
    }

    /** The time window (THE BOLT §1/§4): 102×36dp glass, Anonymous
     *  Pro Bold 20sp numerals and nothing else inside; END is
     *  engraved on the bezel below the glass. */
    val windowWidth: Dp = 102.dp
    val windowHeight: Dp = 36.dp
    val numeralsGap: Dp = 10.dp
    const val numeralsSizeSp: Float = 20f

    /** Travel (THE BOLT §7.1): the throw runs inside the existing
     *  Throw sound's ≤85ms window — a 50ms tween today; the
     *  keyframed seat (0/24/62/94) is merge-pass, not yet landed.
     *  The v0 260ms glide is retired. Verdict (lamp) changes are
     *  never animated at all. */
    const val travelAnimMs: Int = 50

    /** ENDING SOON begins at this many seconds remaining (span
     *  seconds — the same spanRemainingSeconds the strip's TIME
     *  cell priced its ENDS from). */
    const val endingSoonThresholdSeconds: Int = 300

    /** The ONE orange, mode-invariant (THE BOLT §5): #D63E06 as
     *  TokenAlert in BOTH modes. NightAlert (#FF8A5C) is the exiled
     *  fourth orange — it glows at night and is never used here. */
    val warningOrange: Color = TokenAlert

    /** Jewel tones (THE BOLT §5): BRASS is one value both modes —
     *  metal does not change at dusk; MAGENTA follows the house
     *  split (day #C81FA8 / night #F04FCB, the elite palette). */
    val jewelBrass: Color = Color(0xFFE2BC39)
    fun jewelMagenta(night: Boolean): Color =
        if (night) Color(0xFFF04FCB) else Color(0xFFC81FA8)

    /** Window glass pair (THE BOLT §5): dark glass both modes. */
    fun windowGlass(night: Boolean): Color =
        if (night) Color(0xFF161311) else Color(0xFF2E2A26)
    fun windowInk(night: Boolean): Color =
        if (night) Color(0xFFFFE4CF) else Color(0xFFFAF6F0)

    /** ENDING_SOON inversion pair (THE BOLT §5): the ONLY alert
     *  register — numerals #252323 on brass #E2BC39 (8.55:1),
     *  mode-invariant, the whole window flips as one block. */
    val endingSoonPlate: Color = Color(0xFFE2BC39)
    val endingSoonInk: Color = Color(0xFF252323)
}

/** The seal's face states — position + light, no words. Every
 *  state is produced by exactly one derivation: [boltStateFor]. */
enum class SealBarState {
    /** Seal OPEN (plate at the open end, keeper socket exposed),
     *  lamp unlit. Unmistakable: nothing is shut, nothing is lit. */
    OFF,

    /** Seal OPEN while a session row runs (KRALL K-2's erased
     *  fact, restored as its own face): the live wire reads OFF
     *  and outranks the frozen verdict (UNA2-04) — the face
     *  follows the wire. Renders exactly as OFF; its spoken
     *  sentence carries the running fact. */
    OFF_WHILE_RUNNING,

    /** Seal CAUGHT (plate at ⅓ travel, never half — §3), lamp
     *  unlit: the wire has proven nothing either way. */
    UNPROVEN,

    /** Seal CAUGHT, lamp unlit: no warrant read exists at all
     *  (THE BOLT §2's NO_READ row, named by the fold instead of
     *  arriving at the caught pose by accident). Renders as
     *  UNPROVEN; its spoken sentence names the missing read. */
    NO_READ,

    /** Seal SHUT, lamp lit: blocking proven live, no session
     *  running (ARMED). The window may carry the dial's planned
     *  end, labelled as planned (HOLLIS H-1 — see [BoltWindow]). */
    BLOCKING,

    /** Seal SHUT, lamp lit, session end numerals integrated. */
    RUNNING,

    /** Seal SHUT, lamp UNLIT, session end numerals integrated:
     *  a session row asserts the seal, but the bind is not
     *  proven now (live UNPROVEN, or the frozen verdict never
     *  proved it). The plate is shut because the session exists;
     *  the dark lamp refuses to claim the bind. The series law
     *  (HOLLIS H-6) holds here by construction: this face's lamp
     *  can only be lit by a live-fold input, never by the frozen
     *  verdict alone — [boltStateFor] requires print LIVE ∧
     *  frozen LIVE before any running lamp lights. */
    RUNNING_UNPROVEN,

    /** Seal SHUT, pin dropped, lamp DARK, window SHUTTERED
     *  (KRALL K-2's missing face): the session was sealed and
     *  proof has gone dark — either the raw wire still reads
     *  LIVE but the read has aged past the warrant's 45 s print
     *  law, or the shield's own beat has died (B-C1: not
     *  protecting, or heartbeat past the 10 s gap threshold).
     *  It was holding; proof has gone dark. Distinct from
     *  RUNNING_UNPROVEN in pixels (the shutter) and in tense
     *  (proof existed and expired, vs never proven now). */
    STALE,

    /** Seal SHUT, pose unchanged (no back-off — pose is truth).
     *  The jewel steps DOWN to brass and carries no halo; the
     *  alarm is the window inverting as one block (brass glass,
     *  dark numerals). Driven by remaining span time at/below
     *  the span-relative threshold (HOLLIS H-3 — see
     *  [endingSoonThresholdFor]). */
    ENDING_SOON,

    /** Stopwatch session, proven: THROWN, magenta + halo, window
     *  blank (its end is infinity; no fake end is printed). */
    FLOW,

    /** Stopwatch session, unproven: THROWN, lamp dark, window
     *  blank. */
    FLOW_UNPROVEN,

    /** The finish was denied its release and is held (B-C2's
     *  input, host-side since the finish machine landed): THROWN,
     *  magenta (the KANTE exception — it refused to open, the
     *  running family), window blank. */
    DETENTION_FAULT
}

/** What the time window may show — the fold decides this too,
 *  so the window can never disagree with the pose or the lamp. */
enum class BoltWindow {
    /** The running session's live end numerals. */
    LIVE_END,

    /** The dial's planned end at idle, labelled PLANNED END on
     *  the bezel and dimmed in the glass (HOLLIS H-1: the issue's
     *  acceptance — "the session end time must stay readable at
     *  a glance" — in the screenshotted idle state. The design
     *  §4 presence law's blank window deleted this datum
     *  unflagged; the audit's own cure was "label it planned or
     *  freeze the derivation". This is the labelled cure, filed
     *  on the record in the closer disposition for Ren's
     *  countersignature — not a silent amendment). */
    PLANNED_END,

    /** Nothing: no end exists (stopwatch, OFF, no valid dial). */
    BLANK,

    /** The shutter: diagonal hatch over the glass, no numerals —
     *  an end exists but its proof has gone stale (STALE only). */
    SHUTTERED
}

/** The fold's one output: the state and its window, together. */
data class BoltFace(val state: SealBarState, val window: BoltWindow)

/**
 * Everything the fold may read — and nothing else. All warrant
 * fields derive from the host's ONE warrant read (the same
 * `deckWarrant` object): [liveRaw] is its raw state, [printState]
 * is WatchStore.printState over it (the 45 s monotonic print
 * law), [readStale] is warrantStaleForPrint over it ∧ raw LIVE
 * (kept separate because printState alone cannot distinguish
 * "stale LIVE" from "raw UNPROVEN" — that distinction IS the
 * STALE face). [shieldStale] is B-C1's input: the host's
 * shield-beat verdict — while a session runs, the shield loop
 * is not protecting, or its heartbeat (WatchStore's
 * engineHeartbeatMono, stamped by onShieldIteration) is older
 * than the repository's 10 s shield-gap threshold. It is a
 * SEPARATE law from [readStale]: a fresh warrant print cannot
 * cure a dead shield beat, and a live shield beat cannot cure
 * a stale warrant read. [frozenState] is the seal freeze: it
 * attests the seal act only, carries no read instant, and can
 * never light a lamp by itself (the series law, H-6).
 */
data class BoltInputs(
    val sessionRunning: Boolean,
    val detentionHeld: Boolean,
    val hasRead: Boolean,
    val liveRaw: WarrantState?,
    val readStale: Boolean,
    val shieldStale: Boolean = false,
    val printState: WarrantState?,
    val frozenState: WarrantState?,
    val isStopwatch: Boolean,
    val remainingSeconds: Int?,
    val spanSeconds: Int?
)

/** HOLLIS H-3: ENDING_SOON's threshold, floored by the span.
 *  The absolute 300 s rule made the shortest legal session
 *  (5 min dial floor = 300 s span) born ENDING_SOON, RUNNING
 *  unreachable for it. The effective threshold is the lesser of
 *  300 s and half the span: a session is "ending soon" only in
 *  its own second half, and never for more than 300 s. */
fun endingSoonThresholdFor(
    spanSeconds: Int?,
    capSeconds: Int = SealBarParams.endingSoonThresholdSeconds
): Int =
    if (spanSeconds != null && spanSeconds > 0) {
        minOf(capSeconds, spanSeconds / 2)
    } else {
        capSeconds
    }

/**
 * THE FOLD (THE BOLT §2) — the bar's only derivation. Pure:
 * no WatchStore, no seamMarksFor, no warrant object; the host
 * computes the inputs once per frame from its one read and
 * passes the resulting [BoltFace] to the composable. Precedence
 * is first-match-wins, running rows first (the design's order):
 *
 * detention held → DETENTION_FAULT · live OFF (running) →
 * OFF_WHILE_RUNNING (UNA2-04: frozen never consulted) · stale ∧
 * raw LIVE (running) → STALE · shield beat stale (running,
 * B-C1) → STALE · live/print UNPROVEN or frozen ≠
 * LIVE (running) → RUNNING_UNPROVEN / FLOW_UNPROVEN · stopwatch
 * proven → FLOW · remaining ≤ span-floored threshold →
 * ENDING_SOON · proven timed → RUNNING · idle: OFF → OFF ·
 * no read → NO_READ · print LIVE → BLOCKING · else UNPROVEN
 * (a stale LIVE at idle degrades through printState exactly as
 * the print law rules — no session pose exists to hold).
 *
 * Pose is geometry (the session, the seal act). Lamp is proof
 * (live fold inputs only). Window is strength (live end /
 * labelled planned end / blank / shuttered).
 */
fun boltStateFor(inputs: BoltInputs): BoltFace {
    if (inputs.detentionHeld) {
        return BoltFace(SealBarState.DETENTION_FAULT, BoltWindow.BLANK)
    }
    if (inputs.sessionRunning) {
        // UNA2-04: the live wire's OFF outranks the frozen
        // verdict instantly; the frozen state is never consulted.
        if (inputs.liveRaw == WarrantState.OFF) {
            return BoltFace(SealBarState.OFF_WHILE_RUNNING, BoltWindow.BLANK)
        }
        // STALE before UNPROVEN: a stale LIVE read is proof that
        // existed and expired — the shuttered face — not the
        // never-proven face. (printState has already degraded to
        // UNPROVEN at this point; readStale is what tells them
        // apart.)
        if (inputs.readStale && inputs.liveRaw == WarrantState.LIVE) {
            return BoltFace(SealBarState.STALE, BoltWindow.SHUTTERED)
        }
        // B-C1 (KANTE, DESIGN §8 — BLOCKING input charge): the
        // shield beat is dead while a session runs — the loop
        // stopped protecting, or its heartbeat aged past the
        // 10 s shield-gap threshold. The warrant read may still
        // be fresh; it proves the bind, not the shield's beat.
        // Same shuttered face as warrant-staleness (proof gone
        // dark while the pose holds), reached by its own row so
        // the two stale laws never merge into one threshold.
        if (inputs.shieldStale) {
            return BoltFace(SealBarState.STALE, BoltWindow.SHUTTERED)
        }
        val unproven = !inputs.hasRead ||
            inputs.liveRaw == WarrantState.UNPROVEN ||
            inputs.printState == WarrantState.UNPROVEN ||
            inputs.frozenState != WarrantState.LIVE
        if (unproven) {
            return if (inputs.isStopwatch) {
                BoltFace(SealBarState.FLOW_UNPROVEN, BoltWindow.BLANK)
            } else {
                BoltFace(SealBarState.RUNNING_UNPROVEN, BoltWindow.LIVE_END)
            }
        }
        if (inputs.isStopwatch) {
            return BoltFace(SealBarState.FLOW, BoltWindow.BLANK)
        }
        val remaining = inputs.remainingSeconds
        if (remaining != null &&
            remaining <= endingSoonThresholdFor(inputs.spanSeconds)
        ) {
            return BoltFace(SealBarState.ENDING_SOON, BoltWindow.LIVE_END)
        }
        return BoltFace(SealBarState.RUNNING, BoltWindow.LIVE_END)
    }
    // Idle rows.
    if (inputs.liveRaw == WarrantState.OFF ||
        inputs.printState == WarrantState.OFF
    ) {
        return BoltFace(SealBarState.OFF, BoltWindow.PLANNED_END)
    }
    if (!inputs.hasRead) {
        return BoltFace(SealBarState.NO_READ, BoltWindow.PLANNED_END)
    }
    if (inputs.printState == WarrantState.LIVE) {
        return BoltFace(SealBarState.BLOCKING, BoltWindow.PLANNED_END)
    }
    return BoltFace(SealBarState.UNPROVEN, BoltWindow.PLANNED_END)
}

/**
 * The session end as bare numerals for the instrument's face:
 * the strip's TIME text ("ENDS HH:mm") with its word stripped —
 * the lamp and the shut seal already say what the number is.
 * Anything that is not an ENDS print ("NO END" for stopwatch,
 * "INVALID" for a broken dial) yields null: no numerals stand
 * where no end exists.
 */
internal fun endsAtNumeralsFor(timeText: String): String? =
    if (timeText.startsWith("ENDS ")) timeText.removePrefix("ENDS ") else null

/**
 * The seal bar: one horizontal instrument — track, travelling
 * seal plate, keeper socket, lamp, and (while running) the
 * session end in Anonymous Pro numerals. Non-interactive by
 * design (see the header note); the state is carried in
 * [Modifier.semantics] for accessibility.
 */
@Composable
fun SealBar(
    face: BoltFace,
    endsAtNumerals: String?,
    plannedEndsAtNumerals: String?,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val night = scheme.background.luminance() < 0.5f
    val state = face.state

    val targetFraction = when (state) {
        SealBarState.OFF, SealBarState.OFF_WHILE_RUNNING ->
            SealBarParams.plateOpenFraction
        SealBarState.UNPROVEN, SealBarState.NO_READ ->
            SealBarParams.plateUnprovenFraction
        SealBarState.BLOCKING,
        SealBarState.RUNNING,
        SealBarState.RUNNING_UNPROVEN,
        SealBarState.STALE,
        SealBarState.ENDING_SOON,
        SealBarState.FLOW,
        SealBarState.FLOW_UNPROVEN,
        SealBarState.DETENTION_FAULT -> SealBarParams.plateShutFraction
    }
    // Reduced motion (THE BOLT §7.1): at animator scale 0 the
    // sequence collapses to the anchor frame — the tween runs
    // 0 ms and the pose snaps. (Compose tweens are clock-driven,
    // not scale-driven; the scale is read explicitly.)
    val context = androidx.compose.ui.platform.LocalContext.current
    val reduceMotion = androidx.compose.runtime.remember {
        android.provider.Settings.Global.getFloat(
            context.contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f
    }
    val travelFraction by animateFloatAsState(
        targetValue = targetFraction,
        // INTERIM (KRALL K-8, named honestly): a 50 ms tween
        // stands in for the §7.1 keyframed throw (0/24/62/94)
        // until the merge pass lands keyframes; no summary may
        // call this motion "done". Verdict changes themselves
        // are never animated — only the plate's travel is.
        animationSpec = tween(
            if (reduceMotion) 0 else SealBarParams.travelAnimMs,
            easing = MonolithCurves.EasingA
        ),
        label = "seal-travel"
    )

    // THE BOLT §3/§5 jewel law: health is the only thing that
    // lights; fault never lights. BRASS = proven idle (and the
    // ENDING_SOON step-DOWN), MAGENTA = running family, dark
    // lens = every unproven / off / stale / fault face.
    // ENDING_SOON's alarm is the window inversion, never a
    // brighter jewel.
    val lampLit = state == SealBarState.BLOCKING ||
        state == SealBarState.RUNNING ||
        state == SealBarState.FLOW ||
        state == SealBarState.DETENTION_FAULT ||
        state == SealBarState.ENDING_SOON
    val lampColor = when (state) {
        SealBarState.BLOCKING, SealBarState.ENDING_SOON -> SealBarParams.jewelBrass
        SealBarState.RUNNING, SealBarState.FLOW,
        SealBarState.DETENTION_FAULT -> SealBarParams.jewelMagenta(night)
        else -> SealBarParams.windowGlass(night)
    }
    // VEX V-3: the bolt's material is keyed to its POSITION (the
    // animated travel), never its target — the cure Kappe landed
    // for the pin, extended to the bolt's body. During the throw
    // the bolt wears the ink of where it is, not where it is
    // going.
    val seatedNow = travelFraction >= 0.99f
    val plateFill = if (seatedNow) {
        inkControlFill()
    } else {
        scheme.surfaceBright
    }
    val plateGripInk = if (seatedNow) {
        onInkControlFill()
    } else {
        scheme.onSurfaceVariant
    }
    // The window obeys the fold, not a state list: LIVE_END with
    // numerals, PLANNED_END with the labelled planned numerals,
    // SHUTTERED with neither (the shutter is the message), BLANK
    // with neither.
    val showNumerals = face.window == BoltWindow.LIVE_END && endsAtNumerals != null
    val showPlanned = face.window == BoltWindow.PLANNED_END &&
        plannedEndsAtNumerals != null
    val shuttered = face.window == BoltWindow.SHUTTERED
    // The spoken set (LETTER §3, KRALL K-7): one sentence per
    // fold state, numerals NEVER embedded (the numerals are out
    // of the semantics tree — by sentence as by node).
    val description = when (state) {
        SealBarState.OFF -> "Blocking is off. The seal is open."
        SealBarState.OFF_WHILE_RUNNING ->
            "Blocking is off while a session runs. The seal is open."
        SealBarState.UNPROVEN -> "Blocking is not proven. The bolt is caught, not locked."
        SealBarState.NO_READ -> "Blocking is not proven. No reading has arrived."
        SealBarState.BLOCKING -> "Blocking is on. The bolt is thrown and held."
        SealBarState.RUNNING -> "Session running. The seal is shut."
        SealBarState.RUNNING_UNPROVEN ->
            "Session running. The seal is shut, but blocking is not proven."
        SealBarState.STALE ->
            "Session running. The seal is shut, but the proof has gone stale."
        SealBarState.ENDING_SOON -> "Session ending soon. The seal is shut."
        SealBarState.FLOW -> "Session running without an end. The seal is shut."
        SealBarState.FLOW_UNPROVEN ->
            "Session running without an end. The seal is shut, but blocking is not proven."
        SealBarState.DETENTION_FAULT ->
            "The session ended, but the release was refused. The seal stays shut."
    }

    val hairline = scheme.outlineVariant
    Row(
        modifier
            .fillMaxWidth()
            .height(SealBarParams.barHeight)
            .background(scheme.surfaceVariant)
            .drawBehind {
                // §1/§5: the housing is a band with 1dp hairlines
                // at both edges, inside the 60dp.
                val sw = 1.dp.toPx()
                drawLine(hairline, Offset(0f, sw / 2), Offset(size.width, sw / 2), sw)
                drawLine(hairline, Offset(0f, size.height - sw / 2), Offset(size.width, size.height - sw / 2), sw)
            }
            .padding(horizontal = SealBarParams.barPaddingHorizontal)
            .semantics(mergeDescendants = true) { contentDescription = description }
            .testTag("seal_bar"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoxWithConstraints(Modifier.weight(1f)) {
            val travel = (maxWidth - SealBarParams.plateWidth).coerceAtLeast(0.dp)
            // The pin may only drop once the bolt has ARRIVED
            // (finisher pass): derived from the animated travel,
            // never the target — a pin dropped on the target
            // claims "seated and held" while the bolt is still
            // in transit, and lifts while it is still seated.
            val pinDropped = travelFraction >= 0.99f
            // The track: a recess slot the plate rides in.
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(SealBarParams.trackHeight)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(SealBarParams.trackCorner))
                    .background(scheme.background)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(SealBarParams.trackCorner))
            )
            // The WARNING BED (THE BOLT §1/§6, DUNKEL render order:
            // bed → collar → socket → plate → rim): matte #D63E06
            // paint on the channel floor at the shut end, sized to
            // the bolt's footprint. The thrown bolt covers it
            // exactly; every other pose exposes it — OFF's
            // interruptiveness is exposed area, never light.
            Box(
                Modifier
                    .size(width = SealBarParams.plateWidth, height = SealBarParams.plateHeight)
                    .align(Alignment.CenterEnd)
                    .background(SealBarParams.warningOrange)
            )
            // The keeper collar at the shut end: the thrown bolt
            // overlaps it by 4dp (its travel ends 4dp past the
            // collar's left edge is expressed by the collar sitting
            // under the plate's final 4dp — the plate renders last).
            Box(
                Modifier
                    .size(width = 6.dp, height = SealBarParams.trackHeight)
                    .align(Alignment.CenterEnd)
                    .offset(x = (-SealBarParams.plateWidth.value + 10).dp)
                    .background(scheme.surfaceBright)
                    .border(1.dp, scheme.outlineVariant)
            )
            // The keeper socket at the shut end: exposed while the
            // seal is open or caught, covered by the thrown bolt.
            // Its interior is painted the warning orange in OFF
            // only (THE BOLT §1).
            Box(
                Modifier
                    .size(8.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = (-6).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .then(
                        if (state == SealBarState.OFF ||
                            state == SealBarState.OFF_WHILE_RUNNING
                        ) {
                            Modifier.background(SealBarParams.warningOrange)
                        } else {
                            Modifier
                        }
                    )
                    .border(1.dp, scheme.outline, RoundedCornerShape(2.dp))
            )
            // The bolt: grip ribs on its face, and the NOTCH
            // machined into its top edge (THE BOLT §1, KRALL K-3):
            // a real recess — track-coloured, 12dp wide, riding
            // WITH the bolt — that sits under the pin only when
            // the bolt is THROWN. No stripe, no back-off — pose
            // is truth in every state.
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = travel * travelFraction)
                    .size(width = SealBarParams.plateWidth, height = SealBarParams.plateHeight)
                    .clip(RoundedCornerShape(SealBarParams.plateCorner))
                    .background(plateFill)
                    .border(
                        1.dp,
                        if (night && lampLit) scheme.outline else scheme.outlineVariant,
                        RoundedCornerShape(SealBarParams.plateCorner)
                    )
            ) {
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .size(width = 12.dp, height = 3.dp)
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 2.dp, bottomEnd = 2.dp
                            )
                        )
                        .background(scheme.background)
                )
                Row(
                    Modifier
                        .align(Alignment.Center)
                        .height(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) {
                        Box(
                            Modifier
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(plateGripInk.copy(alpha = if (night && pinDropped) 1f else 0.55f))
                        )
                        if (it < 2) Spacer(Modifier.width(3.dp))
                    }
                }
            }
            // The LOCKING PIN, rendered AFTER the bolt (KRALL
            // K-3: drawn before it, the bolt painted over the
            // drop and the eye could not see it). The pin drops
            // into the notch only once the bolt has ARRIVED —
            // pinDropped derives from the animated travel, never
            // the target — and the notch is under it only in the
            // thrown pose. Geometry enforces the law.
            Box(
                Modifier
                    .size(width = 4.dp, height = if (pinDropped) 12.dp else 7.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-(SealBarParams.plateWidth.value / 2).toInt()).dp)
                    .background(if (pinDropped) plateFill else scheme.outline)
            )
        }

        Spacer(Modifier.width(SealBarParams.lampGap))
        // The jewel in its bezel (THE BOLT §1/§5): a ≤9dp jewel in
        // a bezel disc (ink by day, cap by night — the disc rescues
        // brass from day camouflage). RUNNING alone carries the
        // 1dp halo ring in jewel tone; ENDING_SOON steps the jewel
        // DOWN to brass — its alarm lives in the window, not here.
        Box(
            Modifier.size(SealBarParams.lampHaloDiameter),
            contentAlignment = Alignment.Center
        ) {
            if (state == SealBarState.RUNNING || state == SealBarState.FLOW) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(1.dp, lampColor, CircleShape)
                )
            }
            Box(
                Modifier
                    .size(SealBarParams.lampBezelDiameter)
                    .clip(CircleShape)
                    .background(if (night) NightColorSurfaceSheet else scheme.onSurface)
                    .border(1.dp, scheme.outlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(SealBarParams.lampDiameter)
                        .clip(CircleShape)
                        .background(lampColor)
                        .then(
                            if (lampLit) Modifier
                            else Modifier.border(1.dp, scheme.outline, CircleShape)
                        )
                )
            }
        }

        Spacer(Modifier.width(SealBarParams.numeralsGap))

        // The time window (THE BOLT §1/§4 + the H-1 cure): glass
        // in both modes, Anonymous Pro Bold 20sp numerals and
        // nothing else inside. The fold's window rules it:
        // LIVE_END prints the running end at full ink; PLANNED_END
        // prints the dial's planned end DIMMED under a PLANNED
        // END bezel label (a plan in plan's grammar — never the
        // old strip's sliding fact); SHUTTERED draws the hatch
        // and no numerals; BLANK prints nothing — placeholders
        // are banned. ENDING_SOON inverts the whole window as
        // one block: brass plate, dark numerals.
        val endingSoon = state == SealBarState.ENDING_SOON
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(SealBarParams.windowWidth)
        ) {
            Box(
                Modifier
                    .size(width = SealBarParams.windowWidth, height = SealBarParams.windowHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (endingSoon) SealBarParams.endingSoonPlate
                        else SealBarParams.windowGlass(night)
                    )
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(2.dp))
                    .then(
                        if (shuttered) {
                            Modifier.drawBehind {
                                // The shutter: diagonal hatch bars
                                // across the glass — the window is
                                // closed, not empty. STALE's pixel
                                // signature, distinct from every
                                // blank window in the set.
                                val ink = SealBarParams.windowInk(night)
                                    .copy(alpha = 0.35f)
                                val step = 10.dp.toPx()
                                val sw = 2.dp.toPx()
                                var x = -size.height
                                while (x < size.width) {
                                    drawLine(
                                        ink,
                                        Offset(x, size.height),
                                        Offset(x + size.height, 0f),
                                        sw
                                    )
                                    x += step
                                }
                            }
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (showNumerals) {
                    Text(
                        text = endsAtNumerals.orEmpty(),
                        modifier = Modifier.clearAndSetSemantics {},
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = SealBarParams.numeralsSizeSp.sp,
                        color = if (endingSoon) {
                            SealBarParams.endingSoonInk
                        } else {
                            SealBarParams.windowInk(night)
                        }
                    )
                }
                if (showPlanned) {
                    Text(
                        text = plannedEndsAtNumerals.orEmpty(),
                        modifier = Modifier.clearAndSetSemantics {},
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = SealBarParams.numeralsSizeSp.sp,
                        color = SealBarParams.windowInk(night).copy(alpha = 0.55f)
                    )
                }
            }
            Text(
                // Engraved on the bezel (§4): the engraving names
                // what the glass holds — END for a live end,
                // PLANNED END while the glass holds the dial's
                // plan (H-1's label, in the metal, not a caption).
                // LETTER's −0.8 tracking compensation is a dp
                // figure (VEX V-4): letterSpacing is sp-denominated
                // and would scale with font scale, so it is
                // divided back by the density's fontScale — the
                // compensation is now the same physical size at
                // every font scale.
                text = if (showPlanned) "PLANNED END" else "END",
                modifier = Modifier.clearAndSetSemantics {},
                fontFamily = AtkinsonHyperlegibleFamily,
                fontSize = 9.sp,
                letterSpacing = (-0.8f /
                    androidx.compose.ui.platform.LocalDensity.current.fontScale).sp,
                color = scheme.onSurfaceVariant
            )
        }
    }
}
