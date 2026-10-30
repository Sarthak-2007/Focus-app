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
// The bar reads the SAME derivations the strip printed (the Deck
// host's blockCell / timeCell, built from deckWarrant + the seal
// freeze exactly as before — see TactileDeckScreen's Slice D
// block). No new state plumbing: [sealBarStateFor] is a pure
// re-encoding of the strip's word into position + light.
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
     *  luminous total must stay ≤ this many proxy units while
     *  OFF's exposed bed totals ≈125.7 — a ≥3× read at 10%
     *  brightness. Named in code so the gate is executable. */
    const val jewelLuminousBudget: Float = 41.9f

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

/** The seal's face states — position + light, no words. */
enum class SealBarState {
    /** Seal OPEN (plate at the open end, keeper socket exposed),
     *  lamp unlit. Unmistakable: nothing is shut, nothing is lit. */
    OFF,

    /** Seal CAUGHT (plate at ⅓ travel, never half — §3), lamp
     *  unlit: the wire has proven nothing either way (the strip's
     *  UNKNOWN). */
    UNPROVEN,

    /** Seal SHUT, lamp lit: blocking proven live, no session
     *  running (the strip's idle BLOCK ON). No numerals — there
     *  is no session end to print. */
    BLOCKING,

    /** Seal SHUT, lamp lit, session end numerals integrated. */
    RUNNING,

    /** Seal SHUT, lamp UNLIT, session end numerals integrated:
     *  a session row asserts the seal, but the frozen verdict
     *  never proved the bind (the strip's running BLOCK UNKNOWN).
     *  The plate is shut because the session exists; the dark
     *  lamp refuses to claim the bind. */
    RUNNING_UNPROVEN,

    /** Seal SHUT, pose unchanged (no back-off — pose is truth).
     *  The jewel steps DOWN to brass and carries no halo; the
     *  alarm is the window inverting as one block (brass glass,
     *  dark numerals). Driven by remaining span time at/below
     *  the threshold. */
    ENDING_SOON
}

/**
 * The pure re-encoding of the strip's BLOCK word into the seal's
 * state. [blockWord] is the Deck's blockCell word verbatim — the
 * strip's own vocabulary ("ON" / "OFF" / "UNKNOWN" from
 * blockCellFor, which already carries the freshness check at idle
 * and the live-OFF override while running, UNA2-04). This function
 * invents nothing: same inputs, physical encoding.
 *
 * [remainingSeconds] is the session's spanRemainingSeconds (timed
 * sessions only; null for stopwatch / idle) — the same span the
 * strip's TIME cell priced.
 */
internal fun sealBarStateFor(
    isSessionRunning: Boolean,
    blockWord: String,
    remainingSeconds: Int?,
    endingSoonThresholdSeconds: Int = SealBarParams.endingSoonThresholdSeconds
): SealBarState {
    if (isSessionRunning) {
        return when (blockWord) {
            // The live wire's OFF outranks the frozen verdict
            // (UNA2-04): a running session whose bind is positively
            // dead shows the seal OPEN — the face follows the wire.
            "OFF" -> SealBarState.OFF
            "ON" -> if (remainingSeconds != null &&
                remainingSeconds <= endingSoonThresholdSeconds
            ) {
                SealBarState.ENDING_SOON
            } else {
                SealBarState.RUNNING
            }
            else -> SealBarState.RUNNING_UNPROVEN
        }
    }
    return when (blockWord) {
        "ON" -> SealBarState.BLOCKING
        "OFF" -> SealBarState.OFF
        else -> SealBarState.UNPROVEN
    }
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
    state: SealBarState,
    endsAtNumerals: String?,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val night = scheme.background.luminance() < 0.5f

    val targetFraction = when (state) {
        SealBarState.OFF -> SealBarParams.plateOpenFraction
        SealBarState.UNPROVEN -> SealBarParams.plateUnprovenFraction
        SealBarState.BLOCKING,
        SealBarState.RUNNING,
        SealBarState.RUNNING_UNPROVEN,
        SealBarState.ENDING_SOON -> SealBarParams.plateShutFraction
    }
    val travelFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(SealBarParams.travelAnimMs, easing = MonolithCurves.EasingA),
        label = "seal-travel"
    )

    // THE BOLT §3/§5 jewel law: health is the only thing that
    // lights; fault never lights. BRASS = proven idle (and the
    // ENDING_SOON step-DOWN), MAGENTA = running, dark lens = every
    // unproven / off / fault face. ENDING_SOON's alarm is the
    // window inversion, never a brighter jewel.
    val lampLit = state == SealBarState.BLOCKING ||
        state == SealBarState.RUNNING ||
        state == SealBarState.ENDING_SOON
    val lampColor = when (state) {
        SealBarState.BLOCKING, SealBarState.ENDING_SOON -> SealBarParams.jewelBrass
        SealBarState.RUNNING -> SealBarParams.jewelMagenta(night)
        else -> SealBarParams.windowGlass(night)
    }
    val plateFill = if (targetFraction >= SealBarParams.plateShutFraction) {
        inkControlFill()
    } else {
        scheme.surfaceBright
    }
    val plateGripInk = if (targetFraction >= SealBarParams.plateShutFraction) {
        onInkControlFill()
    } else {
        scheme.onSurfaceVariant
    }
    val showNumerals = endsAtNumerals != null && (
        state == SealBarState.RUNNING ||
            state == SealBarState.RUNNING_UNPROVEN ||
            state == SealBarState.ENDING_SOON
        )
    val description = when (state) {
        SealBarState.OFF -> "Blocking is off. The seal is open."
        SealBarState.UNPROVEN -> "Blocking is not proven. The bolt is caught, not locked."
        SealBarState.BLOCKING -> "Blocking is on. The bolt is thrown and held."
        SealBarState.RUNNING ->
            if (endsAtNumerals != null) "Session running. The seal is shut. Ends $endsAtNumerals."
            else "Session running. The seal is shut."
        SealBarState.RUNNING_UNPROVEN ->
            if (endsAtNumerals != null) "Session running. The seal is shut, but blocking is not proven. Ends $endsAtNumerals."
            else "Session running. The seal is shut, but blocking is not proven."
        SealBarState.ENDING_SOON ->
            if (endsAtNumerals != null) "Session ending soon. Ends $endsAtNumerals."
            else "Session ending soon."
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
            val thrown = targetFraction >= SealBarParams.plateShutFraction
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
                        if (state == SealBarState.OFF) {
                            Modifier.background(SealBarParams.warningOrange)
                        } else {
                            Modifier
                        }
                    )
                    .border(1.dp, scheme.outline, RoundedCornerShape(2.dp))
            )
            // The LOCKING PIN above the bolt's thrown position
            // (THE BOLT §1): the bolt's notch is only under the pin
            // when THROWN, so the pin drops only in thrown poses —
            // geometry enforces the law, no state flag can fake it.
            Box(
                Modifier
                    .size(width = 4.dp, height = if (pinDropped) 12.dp else 7.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-(SealBarParams.plateWidth.value / 2).toInt()).dp)
                    .background(if (pinDropped) plateFill else scheme.outline)
            )
            // The bolt: grip ribs on its face. No stripe, no
            // back-off — pose is truth in every state.
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
                                .background(plateGripInk.copy(alpha = if (night && thrown) 1f else 0.55f))
                        )
                        if (it < 2) Spacer(Modifier.width(3.dp))
                    }
                }
            }
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
            if (state == SealBarState.RUNNING) {
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

        // The time window (THE BOLT §1/§4): glass in both modes,
        // Anonymous Pro Bold 20sp numerals and nothing else inside;
        // END engraved once on the bezel below the glass. Blank
        // whenever no session has sealed (showNumerals gates it) —
        // placeholders are banned. ENDING_SOON inverts the whole
        // window as one block: brass plate, dark numerals.
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
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(2.dp)),
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
            }
            Text(
                // Engraved once on the bezel (§4): an engraving
                // does not vanish in OFF/BLOCKING — it is always
                // "END", with LETTER's −0.8 tracking compensation.
                text = "END",
                modifier = Modifier.clearAndSetSemantics {},
                fontFamily = AtkinsonHyperlegibleFamily,
                fontSize = 9.sp,
                letterSpacing = (-0.8).sp,
                color = scheme.onSurfaceVariant
            )
        }
    }
}
