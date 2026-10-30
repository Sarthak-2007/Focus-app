package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SystemGuardStatus
import com.example.data.ClockText
import com.example.service.UsageAccess
import com.example.service.WatchStore
import com.example.ui.ArmPhase
import com.example.service.Warrant
import com.example.service.WarrantState
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.NightColorSurfaceBright
import com.example.ui.theme.NightColorSurfaceSheet
import com.example.ui.theme.PillBadgeTextStyle
import com.example.ui.theme.TokenColorSurfaceBright
import com.example.ui.theme.TokenColorSurfaceSheet
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

/**
 * Helper to build mixed-typography strings where words render in Google Sans / PlusJakartaSans
 * and every number renders in Space Mono / JetBrainsMono (DEVELOPER-GUIDE.pdf §2).
 */
@Composable
fun rememberMixedTypographyText(
    wordPrefix: String,
    monoNumber: String,
    wordSuffix: String = "",
    wordColor: Color = MaterialTheme.colorScheme.onBackground,
    monoColor: Color = MaterialTheme.colorScheme.primary
) = buildAnnotatedString {
    if (wordPrefix.isNotEmpty()) {
        withStyle(
            SpanStyle(
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                color = wordColor
            )
        ) {
            append(wordPrefix)
        }
    }
    if (monoNumber.isNotEmpty()) {
        withStyle(
            SpanStyle(
                fontFamily = AnonymousProFamily,
                fontWeight = FontWeight.Bold,
                color = monoColor
            )
        ) {
            append(monoNumber)
        }
    }
    if (wordSuffix.isNotEmpty()) {
        withStyle(
            SpanStyle(
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                color = wordColor
            )
        ) {
            append(wordSuffix)
        }
    }
}

/**
 * Full-bleed Section Header with hairline top rule (DEVELOPER-GUIDE.pdf §1.1 & §3).
 */
@Composable
fun FullBleedSectionHeader(
    titleWord: String,
    rightWordPrefix: String = "",
    rightMonoNumber: String = "",
    rightWordSuffix: String = "",
    useAccentOnNumber: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        // Sections separate by gap + label, not a rule (S7): hairlines are
        // reserved for "next item" inside lists.
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titleWord,
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 0.6.sp,
                color = scheme.onSurfaceVariant
            )
            if (rightWordPrefix.isNotEmpty() || rightMonoNumber.isNotEmpty() || rightWordSuffix.isNotEmpty()) {
                Text(
                    text = rememberMixedTypographyText(
                        wordPrefix = rightWordPrefix,
                        monoNumber = rightMonoNumber,
                        wordSuffix = rightWordSuffix,
                        wordColor = scheme.onSurfaceVariant,
                        monoColor = if (useAccentOnNumber) scheme.primaryContainer else scheme.onBackground
                    ),
                    fontSize = 11.5.sp,
                    modifier = Modifier.padding(end = 3.dp)
                )
            }
        }
    }
}

/**
 * Blockit-disciplined 12dp Tactile Action Chip (Ink primary or recessed variant, zero rainbow colors).
 */
@Composable
fun BlockitActionChip(
    wordLabel: String,
    monoBadge: String = "",
    isPrimaryInk: Boolean = false,
    isAccentArmed: Boolean = false,
    isUndoStyle: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val scheme = MaterialTheme.colorScheme
    // The UNDO mode (chrome C7, the accent law): the accent budget
    // buys the commit verbs their TEXT, never a second fill. An
    // undo chip is an ink fill carrying accent text — the verb is
    // the text, so the text wears the accent and the body is ink.
    val bg = when {
        isUndoStyle -> inkControlFill()
        isAccentArmed -> scheme.primaryContainer
        isPrimaryInk -> inkControlFill()
        else -> scheme.surfaceVariant
    }
    val fg = when {
        isUndoStyle -> scheme.primaryContainer
        isAccentArmed -> Color(0xFFFBF1EC)
        isPrimaryInk -> onInkControlFill()
        else -> scheme.onBackground
    }
    val mod = if (testTag.isNotEmpty()) modifier.testTag(testTag) else modifier

    Box(
        modifier = mod
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rememberMixedTypographyText(
                wordPrefix = wordLabel,
                monoNumber = if (monoBadge.isNotEmpty()) " $monoBadge" else "",
                wordColor = fg,
                // E5b B6 (Horiz §2), the app-wide badge rule: a
                // chip's mono badge prints in the chip's own
                // foreground — never the accent. (The primary-ink
                // branch that printed badges in primaryContainer
                // was the accent's last quiet leak; the UNDO style
                // is untouched — its fg IS the accent, by the
                // accent law's text clause.)
                monoColor = fg
            ),
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

/**
 * THE HEADER BAND (chrome C2, spec §2) — a printed nameplate, not a
 * console. 48dp, invariant in both modes and both session states;
 * it never grows, never wraps, never changes height: a 47dp row
 * over a 1dp hairline at y48, over the app's ground.
 *
 * Left: the brand — MONOLITH, Anonymous Pro 700 10sp tracked +2.0,
 * primary ink, on a 20dp margin. Print, not a control: the brand-row
 * console click is dead with the console.
 *
 * Right: the Guard line ([GuardLinePrint]) — the ONE input this
 * composable takes besides the seal flag. It is pure print over the
 * Watch Store's counted four; a tap (unsealed) is the third door
 * into PARK. While sealed the line is frozen at its seal-frame
 * value and the tap is gated — print, not a door.
 *
 * Nothing else lives in the band: no HELD pill (the digest's count
 * lives on the Deck's own HELD chip), no DAY/NIGHT pill (the theme
 * is an evaluation now, printed in PARK), no brand dot, no Armed
 * suffix — the session's fact lives on the Deck's strip.
 */
@Composable
fun MonolithPullDownHeader(
    guardLine: GuardLinePrint?,
    isSealed: Boolean,
    onGuardLineTap: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(scheme.background)
            .testTag("monolith_header_band")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(47.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MONOLITH",
                fontFamily = AnonymousProFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = scheme.onBackground,
                modifier = Modifier.padding(start = 20.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            if (guardLine != null) {
                Text(
                    text = guardLine.text,
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp,
                    color = scheme.onBackground,
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .then(
                            if (isSealed) Modifier
                            else Modifier.clickable { onGuardLineTap() }
                        )
                        .testTag("header_guard_line")
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(scheme.outlineVariant)
        )
    }
}

/**
 * Ink fill for filled controls (X1): in day it is the ink; at night the ink
 * token flips to cream, which would make every filled control the brightest
 * mass on the screen — so night fills use a warm dark step instead, with
 * cream kept for the text on top.
 */
@Composable
fun inkControlFill(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (scheme.background.luminance() < 0.5f) Color(0xFF4A423C) else scheme.onBackground
}

@Composable
fun onInkControlFill(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (scheme.background.luminance() < 0.5f) Color(0xFFFFE4CF) else Color(0xFFFBF1EC)
}

// ============================================================================
// SLICE D — THE PROOF STRIP, THE SENTENCE SLOT, THE IMPRINT (D6/D7/D9)
// ============================================================================
// The strip is the wire's printed face: three cells (BLOCK/APPS/TIME)
// on the heartbeat's word, a seam stamp on the hairline beneath them,
// the one blocking-off sentence in its reserved slot under the slide,
// and the imprint — the seal's fixed assembly at the foot of the Deck.
// Every string these print derives from the ONE verdict function
// (MonolithRepository.liveWarrant, surfaced as the deckWarrant flow);
// the pure helpers below are unit-tested directly.

/** A strip cell's density (Kato): tones, never hues — a state steps
 *  between staircase tones, never to a colour. */
enum class StripDensity { PASS, FAULT, UNKNOWN }

/** The seam stamp's two marks for a warrant — Rosa's table and her
 *  grade definitions, which are binding on this print:
 *  - LIVE BIND: the enforcer is held by a live system bind in this
 *    process AND its pipeline root-stamped this residency's
 *    foreground AND the usage grant cross-check reads live. A claim
 *    about liveness and pipeline, explicitly NOT a claim that an
 *    intercept fired. (Rosa's filed definition also named a fourth
 *    leg — the accessibility grant string read live. SETTLED at
 *    the slide merge (Ren, Review F2): the definition is amended
 *    to the three legs actually read. The fourth is refused with
 *    reason: the system instantiates the accessibility service
 *    only when it is enabled and bound, so the instance's
 *    existence proves the grant more directly than re-reading
 *    the Settings string the substrate was built to retire.
 *    Settled alongside (Review F1): an unbound service maps to
 *    OFF by definition in liveWarrant — the UNPROVEN-unbound
 *    room is definitionally unreachable in production, not
 *    accidentally. Its seam branch STAYS: the function must
 *    remain total and truthful for any warrant handed to it —
 *    an earlier merge pass removed the branch and the lane's
 *    own table test caught the function printing BIND HELD
 *    for an unbound warrant. Reachability is liveWarrant's
 *    fact to state; print truth is this function's.)
 *  - BIND ONLY: liveness proven, pipeline (or a grant) unproven.
 *  - POSITIVE: the OFF it accompanies rests on a directly observed
 *    dead signal, not on silence or timeout.
 *  - NONE: nothing has been proven yet either way.
 *  The grade is taken over the wire's legs only — the system's
 *  service list, a system-delivered root-stamp event, a live
 *  AppOps read. No leg is the service's self-assertion (the
 *  shield-protecting leg is excluded from the LIVE conjuncts for
 *  exactly this reason), so the per-link SELF-REPORT machinery
 *  the chair's F6 note imagined is not built (PLAN-AUDIT, Orig
 *  F6: unenforceable as written; Rosa's definition already
 *  solved it). `END-TO-END` stays forbidden.
 *  Two of Rosa's rows degrade honestly on this substrate, which
 *  records neither an ever-held bit nor a bind-loss instant:
 *  `BIND LOST HH:MM` and `BIND NEVER HELD` are NOT printed — the
 *  left mark states the present-tense fact the legs prove
 *  (`BIND NOT HELD`). Null warrant → null: before this process's
 *  first read, the stamp line is blank. */
internal fun seamMarksFor(warrant: Warrant?): Pair<String, String>? {
    if (warrant == null) return null
    return when (warrant.state) {
        WarrantState.LIVE -> "BIND HELD · THIS VISIT" to "PROOF: LIVE BIND"
        WarrantState.UNPROVEN -> when {
            // Room (ii): unconfirmed — nothing proven either way.
            // Production-unreachable (liveWarrant maps every
            // unbound read to OFF by definition — settled at the
            // slide merge, Review F1), but the function stays
            // total and truthful for any warrant it is handed:
            // an unbound warrant must never print BIND HELD.
            !warrant.a11yBound -> "BIND UNCONFIRMED" to "PROOF: NONE"
            // Held and stamped; a grant is what is unproven.
            warrant.a11yStampedThisResidency -> "BIND HELD · THIS VISIT" to "PROOF: BIND ONLY"
            // Room (i): held, unstamped this visit.
            else -> "BIND HELD · UNSEEN" to "PROOF: BIND ONLY"
        }
        WarrantState.OFF -> when {
            !warrant.a11yBound -> "BIND NOT HELD" to "PROOF: POSITIVE"
            warrant.a11yStampedThisResidency -> "BIND HELD · THIS VISIT" to "PROOF: POSITIVE"
            else -> "BIND HELD · UNSEEN" to "PROOF: POSITIVE"
        }
    }
}

/** The seam at a named instant (Truth Car 3, UNA2-03): while the
 *  read is fresh this is exactly [seamMarksFor]. Once the read is
 *  older than the board's own staleness threshold, the residency
 *  facts on the left do not age — bound is bound, a stamp is a
 *  stamp — but the PROOF does: the right mark admits the read's age
 *  ("PROOF: STALE") instead of claiming a currency it no longer
 *  holds. The warrant carries its read instant now; the print
 *  finally can tell. */
internal fun seamMarksFor(warrant: Warrant?, nowMono: Long): Pair<String, String>? {
    if (warrant == null) return null
    if (!WatchStore.warrantStaleForPrint(warrant, nowMono)) return seamMarksFor(warrant)
    val left = when {
        !warrant.a11yBound -> "BIND NOT HELD"
        warrant.a11yStampedThisResidency -> "BIND HELD · THIS VISIT"
        else -> "BIND HELD · UNSEEN"
    }
    return left to "PROOF: STALE"
}

/** TIME's print (Uma): a pure function of a stored end-milli — the
 *  shipped ever-sliding finish line dies here; nothing about this
 *  text is remembered, and nothing here ticks. */
internal fun endsAtText(is24Hour: Boolean, endMillis: Long): String =
    "ENDS " + ClockText.timeOfDay(is24Hour, endMillis)

/** The imprint's tenant (Emil): the seal keeps its count in every
 *  tense. Idle prints the future ONLY on a LIVE wire — over a dead
 *  or unproven wire the box is EMPTY (space held): printing WILL
 *  SHUT there is the register's worst lie, committed in type.
 *  Sealed prints the press-frozen count, never a live recount.
 *
 *  Truth Car 3 (UNA2-02): the count arrives SPLIT — [shut] the
 *  LOCKED tier, [peek] the PEEK_2M tier — and the tenant prints the
 *  split, as the Shield's header does. A PEEK app does not shut and
 *  is not held: it meets the airlock and a 2-minute lease. The old
 *  single count (LOCKED+PEEK under WILL SHUT / HELD) named the sum
 *  with the stronger verb; the number was real, the noun was the
 *  lie. Either figure unknown → the box is empty, never a guess.
 *
 *  Truth Car 3 (UNA2-04, Hale W-24): sealed, when the live wire
 *  has fallen to OFF ([wireNow]), the tenant keeps the seal's
 *  words but marks their tense on its face — the suffix
 *  " · AS SEALED" — while the BLOCK cell follows the seam's live
 *  word. When live and frozen agree, no suffix: a standing
 *  qualifier teaches the eye to skip it. */
internal fun imprintTenantText(
    isSealed: Boolean,
    shut: Int?,
    peek: Int?,
    warrantState: WarrantState?,
    wireNow: WarrantState? = null
): String? {
    if (isSealed) {
        val s = shut ?: return null
        val p = peek ?: return null
        val split = splitTenantText(s, p, sealed = true) ?: return null
        return if (wireNow == WarrantState.OFF) "$split · AS SEALED" else split
    }
    if (warrantState != WarrantState.LIVE) return null
    val s = shut ?: return null
    val p = peek ?: return null
    return splitTenantText(s, p, sealed = false)
}

/** The split in Hale's W-23 grammar, mirroring the Shield's nouns:
 *  idle "{n} WILL SHUT · {m} PEEK", sealed "{n} HELD · {m} PEEK".
 *  Omission law: a zero segment never prints — n == 0 leaves
 *  "{m} PEEK" alone, and "0 WILL SHUT" is never a sentence. Both
 *  zero → null: the box stands empty rather than print a count
 *  of nothing under a verb. */
private fun splitTenantText(shut: Int, peek: Int, sealed: Boolean): String? {
    val shutPart = when {
        shut > 0 -> if (sealed) "$shut HELD" else "$shut WILL SHUT"
        else -> null
    }
    val peekPart = if (peek > 0) "$peek PEEK" else null
    val parts = listOfNotNull(shutPart, peekPart)
    return if (parts.isEmpty()) null else parts.joinToString(" · ")
}

/** What the sentence slot's tap does (Suki): FIX goes to the pane
 *  that owns the lack; RETRY re-reads the wire. The cascade's
 *  sentences (step 4) carry a null action: a jam offers no verb —
 *  retry is the machine's, never his — and an annulment is a
 *  fact to read, not a door to tap. */
enum class RefusalAction { FIX_ACCESSIBILITY, FIX_USAGE, RETRY_CHECK }

data class RefusalSentence(val text: String, val action: RefusalAction?)

/**
 * Suki's cause table, ONE sentence, causal priority — the a11y row
 * outranks the usage row because enforcement dies at the first leg.
 * The two standing sentences are Ren's chair ruling #4 verbatim;
 * the usage row is Suki's as filed. Healthy (LIVE) → null: the slot
 * renders its reserved emptiness.
 */
internal fun refusalSentenceFor(warrant: Warrant?): RefusalSentence? {
    if (warrant == null) return null
    return when {
        !warrant.a11yBound ->
            RefusalSentence("BLOCKING IS OFF — TAP TO FIX", RefusalAction.FIX_ACCESSIBILITY)
        warrant.state == WarrantState.UNPROVEN && !warrant.a11yStampedThisResidency ->
            RefusalSentence("CAN'T CHECK BLOCKING — TAP TO RETRY", RefusalAction.RETRY_CHECK)
        warrant.usage == UsageAccess.DENIED ->
            RefusalSentence("USAGE ACCESS IS OFF — TAP TO FIX", RefusalAction.FIX_USAGE)
        warrant.usage == UsageAccess.UNSET ->
            RefusalSentence("CAN'T CHECK BLOCKING — TAP TO RETRY", RefusalAction.RETRY_CHECK)
        warrant.state == WarrantState.OFF ->
            RefusalSentence("BLOCKING IS OFF — TAP TO FIX", RefusalAction.FIX_ACCESSIBILITY)
        else -> null
    }
}

/** The sentence slot at a named instant (Truth Car 3, UNA2-03): a
 *  sentence that orders a fix must ride a CURRENT read. On a stale
 *  read the slot keeps its reserved emptiness — the seam carries
 *  the STALE mark, and the Deck's ticker re-reads the wire within a
 *  tick of the threshold, so the silence is bounded. Demanding a fix
 *  off a minutes-old warrant (or withholding one) would be the
 *  print lying about the wire's present tense. */
internal fun refusalSentenceFor(warrant: Warrant?, nowMono: Long): RefusalSentence? {
    if (warrant == null) return null
    if (WatchStore.warrantStaleForPrint(warrant, nowMono)) return null
    return refusalSentenceFor(warrant)
}

/**
 * The cascade's jam sentence (step 4, Opal §F4): while an attempt
 * is JAMMED, the slot names the first unanswering link — names,
 * never a count — in the present tense of a machine still
 * working. It carries no action: the retry fires on the
 * machine's own clock and offers him no verb. Non-jammed phases
 * print nothing here (PROVING's silence is the machine working;
 * the annulment's past-tense sentence is the controller's own
 * armSentence, not this function's).
 */
internal fun jamSentenceFor(phase: ArmPhase): RefusalSentence? {
    if (phase !is ArmPhase.Jammed) return null
    val first = phase.jamSet.minByOrNull { it.ordinal } ?: return null
    return RefusalSentence(
        text = "${first.displayName} DIDN'T ANSWER — STILL PROVING.",
        action = null
    )
}

@Composable
private fun stripToneColors(): StripTones {
    val scheme = MaterialTheme.colorScheme
    val night = scheme.background.luminance() < 0.5f
    return StripTones(
        recess = scheme.surfaceVariant,
        raised = if (night) NightColorSurfaceBright else TokenColorSurfaceBright,
        cap = if (night) NightColorSurfaceSheet else TokenColorSurfaceSheet,
        divider = scheme.outlineVariant,
        faultEdge = scheme.outline,
        ordinaryInk = if (night) Color(0xFF9C8E84) else scheme.onSurfaceVariant,
        glyphInk = scheme.onSurface,
        secondaryInk = if (night) Color(0xFF9C8E84) else scheme.onSurfaceVariant
    )
}

private data class StripTones(
    val recess: Color,
    val raised: Color,
    val cap: Color,
    val divider: Color,
    val faultEdge: Color,
    val ordinaryInk: Color,
    val glyphInk: Color,
    val secondaryInk: Color
)

private fun StripTones.fill(density: StripDensity): Color = when (density) {
    StripDensity.PASS -> recess
    StripDensity.FAULT -> cap
    StripDensity.UNKNOWN -> raised
}

private fun StripTones.ink(density: StripDensity): Color = when (density) {
    StripDensity.PASS -> ordinaryInk
    else -> glyphInk
}

@Composable
private fun StripCell(
    label: String,
    state: String,
    density: StripDensity,
    mono: Boolean,
    tones: StripTones,
    modifier: Modifier = Modifier
) {
    Box(modifier.background(tones.fill(density))) {
        if (density == StripDensity.FAULT) {
            // The doubled top hairline, over this cell's width only.
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(tones.faultEdge)
            )
        }
        Column(Modifier.padding(start = 10.dp, top = 4.dp)) {
            Text(text = label, style = PillBadgeTextStyle, color = tones.secondaryInk)
            Spacer(Modifier.height(1.dp))
            Text(
                text = state,
                style = if (mono) {
                    TextStyle(
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.sp
                    )
                } else {
                    TextStyle(
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.5.sp
                    )
                },
                color = tones.ink(density)
            )
        }
    }
}

/**
 * The 53dp proof band (Kato's corrected sheet): three cells on a
 * recess body, square corners (radius 0 — the band is architecture,
 * not a card), 1px vertical hairlines one tone above recess, and the
 * seam stamp riding the hairline at the band's foot. Density flips
 * land in one frame — no easing, no pulse, ever: the strip is print.
 */
@Composable
fun ProofStripBand(
    blockWord: String,
    blockDensity: StripDensity,
    appsWord: String,
    appsDensity: StripDensity,
    timeText: String,
    timeDensity: StripDensity,
    stampLeft: String?,
    stampRight: String?,
    modifier: Modifier = Modifier
) {
    val tones = stripToneColors()
    Column(
        modifier
            .fillMaxWidth()
            .height(53.dp)
            .background(tones.recess)
    ) {
        Row(Modifier.fillMaxWidth().weight(1f)) {
            StripCell("BLOCK", blockWord, blockDensity, mono = false, tones = tones, modifier = Modifier.weight(1.05f).fillMaxHeight())
            Box(Modifier.width(1.dp).fillMaxHeight().background(tones.divider))
            StripCell("APPS", appsWord, appsDensity, mono = true, tones = tones, modifier = Modifier.weight(0.9f).fillMaxHeight())
            Box(Modifier.width(1.dp).fillMaxHeight().background(tones.divider))
            StripCell("TIME", timeText, timeDensity, mono = true, tones = tones, modifier = Modifier.weight(1.3f).fillMaxHeight())
        }
        // The seam: the stamp rides the hairline. Left mark inset
        // 10dp; the right grade ends 10dp from the right edge; on
        // collision the left degrades first (single line, clipped).
        Box(Modifier.fillMaxWidth().height(12.dp)) {
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(tones.divider)
            )
            if (stampLeft != null && stampRight != null) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stampLeft,
                        style = PillBadgeTextStyle,
                        color = tones.secondaryInk,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = stampRight,
                        style = PillBadgeTextStyle,
                        color = tones.secondaryInk,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * The sentence slot (Suki): 22dp, reserved always, directly under the
 * slide. Healthy = nothing stands here. A standing sentence is a tap
 * target wired to the pane that owns its lack. The hand-back is
 * Suki's: on the wire's return the sentence does NOT swap for a
 * restored line — after a stability beat it fades (90ms) under one
 * 5ms tick, and the slot returns to its reserved emptiness. A wire
 * that flickers back to fault inside the beat keeps the sentence.
 */
@Composable
fun RefusalSentenceSlot(
    sentence: RefusalSentence?,
    onAction: (RefusalAction) -> Unit,
    onHandBackTick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tones = stripToneColors()
    var displayed by remember { mutableStateOf<RefusalSentence?>(null) }
    var fading by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (fading) 0f else 1f,
        animationSpec = tween(90),
        label = "handback-fade"
    )

    LaunchedEffect(sentence) {
        if (sentence != null) {
            displayed = sentence
            fading = false
        } else if (displayed != null) {
            // The stability beat: the healthy state must persist
            // 500ms before the sentence is allowed to leave.
            delay(500)
            fading = true
            onHandBackTick()
            delay(120)
            displayed = null
            fading = false
        }
    }

    Box(modifier.fillMaxWidth().height(22.dp)) {
        val current = displayed
        if (current != null) {
            Text(
                text = current.text,
                style = TextStyle(
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.5.sp
                ),
                color = tones.glyphInk.copy(alpha = alpha),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .then(
                        when (val action = current.action) {
                            null -> Modifier
                            else -> Modifier.clickable { onAction(action) }
                        }
                    )
            )
        }
    }
}

/**
 * The imprint (Emil's fixed assembly): a double rule, the count in a
 * fixed 20dp box, and a lip zone that exists in every state — the
 * zone directly under the pinned strip never changes height by one
 * pixel across the seal. The Strike is a one-time scale/settle on
 * the seal's first composition with a new session; the lip is the
 * seal's one light change, a 1dp line present exactly while sealed.
 */
@Composable
fun ImprintSlot(
    tenantText: String?,
    isSealed: Boolean,
    strikeKey: Long?,
    modifier: Modifier = Modifier,
    // The struck record (Emil, cascade review E-12): held by the
    // host at VM scope, not in this composition's saved state.
    // strikeKey is a level, not a pulse — a cold first composition
    // after the seal (relaunch without a bundle, a routine arm
    // first seen on a later Deck visit) must find the record
    // already carrying the key and stand settled, never replay
    // the Strike on an old seal. The host seeds the record for
    // sessions it did not watch seal.
    struckKey: Long? = null,
    onStruck: (Long) -> Unit = {}
) {
    val tones = stripToneColors()
    val scale = remember { Animatable(1f) }
    val inkAlpha = remember { Animatable(0.88f) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(strikeKey, struckKey) {
        val key = strikeKey
        if (key != null && key != struckKey) {
            onStruck(key)
            // Reduced motion (animator scale 0): the Strike is a
            // single-frame step, filed in §3.3 — snap, don't tween.
            val animatorScale = runCatching {
                android.provider.Settings.Global.getFloat(
                    context.contentResolver,
                    android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
                    1f
                )
            }.getOrDefault(1f)
            if (animatorScale == 0f) {
                scale.snapTo(1f)
                inkAlpha.snapTo(0.88f)
            } else {
                scale.snapTo(1.04f)
                inkAlpha.snapTo(0.82f)
                // Both settle from the same frame, concurrently
                // (E-8): the scale lands at 110ms, the ink at
                // 400ms on EasingA — the settle ends at 400.
                launch {
                    scale.animateTo(1f, tween(110, easing = CubicBezierEasing(0.9f, 0f, 1f, 0.4f)))
                }
                launch {
                    inkAlpha.animateTo(0.88f, tween(400, easing = MonolithCurves.EasingA))
                }
            }
        }
    }

    // The lip (E-9): struck IN with the tenant — present at full
    // alpha from the seal frame, no fade-in; the 240ms fade is
    // only the session-end exit.
    val lipAlpha = remember { Animatable(0f) }
    LaunchedEffect(isSealed) {
        if (isSealed) {
            lipAlpha.snapTo(1f)
        } else {
            lipAlpha.animateTo(0f, tween(240))
        }
    }
    val night = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val lipColor = if (night) {
        NightColorSurfaceSheet.copy(alpha = 0.35f * lipAlpha.value)
    } else {
        TokenColorSurfaceBright.copy(alpha = 0.5f * lipAlpha.value)
    }
    // A physical 1px line (E-10), not a 1dp box.
    val onePx = with(androidx.compose.ui.platform.LocalDensity.current) { 1.toDp() }

    Column(modifier.fillMaxWidth()) {
        // The double rule: 1dp @22% over 0.5dp @12% of the filed
        // outlineVariant ink, 2dp apart (E-10).
        Box(Modifier.fillMaxWidth().height(1.dp).background(tones.divider.copy(alpha = 0.22f)))
        Spacer(Modifier.height(2.dp))
        Box(Modifier.fillMaxWidth().height(0.5.dp).background(tones.divider.copy(alpha = 0.12f)))
        Spacer(Modifier.height(6.dp))
        // The text box: 20dp fixed, in every state, tenant or none.
        Box(Modifier.fillMaxWidth().height(20.dp)) {
            if (tenantText != null) {
                Text(
                    text = tenantText,
                    style = PillBadgeTextStyle.copy(lineHeight = 20.sp),
                    color = tones.secondaryInk.copy(alpha = inkAlpha.value),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .graphicsLayer {
                            scaleX = scale.value
                            scaleY = scale.value
                            // Struck from the left margin at
                            // mid-height (E-8): origin (0, 0.5).
                            transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
                        }
                )
            }
        }
        // The lip zone: reserved always; the 1px line sits 2dp
        // into the zone, present exactly while sealed.
        Box(Modifier.fillMaxWidth().height(4.dp)) {
            if (lipAlpha.value > 0f) {
                Box(
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 2.dp)
                        .fillMaxWidth()
                        .height(onePx)
                        .background(lipColor)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
    }
}
