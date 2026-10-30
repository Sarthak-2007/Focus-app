package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import android.os.SystemClock
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ReceiptState
import com.example.ui.theme.AnonymousProFamily
import java.time.LocalDate
import java.time.ZoneId
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.MonolithCurves
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * THE RECEIPT SLIP (Slice E, F-L1 as ruled (C)): the aperture
 * sheet — the digest sheet's pattern generalised to every
 * receipt branch. Viewport-hosted (F-B1: it composes over the
 * Deck at the root, never inside the rail's band), defocus scrim
 * at 0.12 (Linea's frame law — the digest's 0.65 is a different
 * room's law), the slip settled and still: it never re-strikes,
 * never animates its numbers, and dismisses on the next DOWN
 * anywhere or on navigation.
 *
 * The print discipline (build-ready §11.3, Wort's lexicon):
 * long-form durations (`50 MIN`, never `50M`); the total line
 * `RAN {longSpan}` prints under ENDED over a rule (receipt
 * audit item 5 — sealedElapsedSeconds was carried in state and
 * printed nowhere); the HELD line prints ONLY from the W-41
 * split figures (ReceiptState.heldLine) — the old summed line
 * booked PEEK apps as held and counted sites blind, so until
 * the persisted SHUT/PEEK columns land it is omitted whole,
 * the same omission law that keeps −1 off the paper; the
 * faulted sheet carries NO title (F-W1 — `ENDED-FAULT`
 * never prints anywhere) and exactly one receipt-only line;
 *
 * Wave-2 (issue #10, receipt audit Group A): the outcome slot
 * prints only when it adds a fact (plain ENDED and the shield-gap
 * compound are suppressed at the sheet — the gap's one carrier is
 * THE SHIELD WAS OFF… below; a legacy SLEPT THROUGH normalises to
 * STOOD DOWN); BREAK BANKED never prints (item 10 — it booked a
 * 60-second hold price as banked minutes); the shape is thermal
 * paper (3dp, no border, contact anchor, lift by shadow); the
 * arrival FEEDS and restored slips render settled; dismissal is
 * a DOWN-gesture on the scrim after a 2-second dwell, and a
 * faulted slip is not dismissible at the sheet at all (the host's
 * navigation/PARK paths remain its exits); the footer carries
 * W-43's cured noun (HOURS) with a clause-boundary split guard.
 * the death-booked sheet books `THE SHIELD WAS OFF…` in words.
 * The footer is exact.
 */
@Composable
fun ReceiptSheet(
    receipt: ReceiptState,
    onDismiss: () -> Unit
) {
    // A7 (audit item 18): the slip FEEDS — a whole-document slide
    // from the bottom edge, the printer's one motion; a crossfade
    // is a UI card appearing, and this is a document. Restored
    // slips never re-perform the arrival (contract Z6): they
    // render settled. The old exit fadeOut was dead code (the
    // host unmounts first) and is deleted, not replaced.
    AnimatedVisibility(
        visible = true,
        enter = if (receipt.restored) {
            EnterTransition.None
        } else {
            slideInVertically(
                animationSpec = MonolithCurves.specC(),
                initialOffsetY = { it }
            ) + fadeIn(MonolithCurves.specC())
        },
        exit = ExitTransition.None
    ) {
        val scheme = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.12f))
                // A8 (audit item 19): dismissal lands on a DOWN
                // gesture, never on the UP a stray tap completes —
                // and only after a 2-second dwell from the publish
                // stamp, so the in-flight tap that was already
                // travelling when the slip arrived passes to its
                // own target and cannot kill an unread slip. A
                // FAULTED slip does not dismiss at the sheet at
                // all: its exits are the host's (navigation, PARK).
                // (detectTapGestures' onPress is the DOWN; the old
                // clickable fired on UP.)
                .pointerInput(receipt.sessionId, receipt.faulted) {
                    detectTapGestures(onPress = {
                        val mono = receipt.receiptPublishedMonoMs
                        val dwellOver = receipt.restored || mono <= 0L ||
                            SystemClock.elapsedRealtime() - mono >= 2_000L
                        if (!receipt.faulted && dwellOver) onDismiss()
                    })
                }
                // A5 (audit item 17): anchor means CONTACT — no
                // float at bottom or sides; the slip covers the
                // nav band instead of peeking it underneath.
                .padding(top = 12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // A5 (audit item 15): thermal paper is cut
                    // square — 3dp radius, NO border; the lift is
                    // shadow alone. (The 24dp card + outline were
                    // inherited whole from the digest sheet, a
                    // different room's furniture.)
                    .shadow(10.dp, RoundedCornerShape(3.dp))
                    .clip(RoundedCornerShape(3.dp))
                    .background(scheme.surfaceBright)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // A9 (audit item 14) + head ruling (item 2): on a
                // faulted slip the row COLLAPSES — no title-height
                // void with the № floating over it (F-W1 still
                // holds: no title prints). The № is a document's
                // head-mark: same ink as the title, never greyer.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (receipt.faulted) {
                        Arrangement.End
                    } else {
                        Arrangement.SpaceBetween
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!receipt.faulted) {
                        Text(
                            text = receipt.title,
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = scheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        text = receipt.numberPrint,
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = scheme.onBackground
                    )
                }

                // RC-02: a FLOW slip has no PLANNED segment —
                // the line carries ARMED alone rather than a
                // floor dressed as a plan.
                // A11 (audit item 3): a span that crosses midnight
                // prints a date somewhere — the ARMED line carries
                // its day. Same-day slips are unchanged.
                val armDate = LocalDate.ofInstant(
                    java.time.Instant.ofEpochMilli(receipt.armWallMs), ZoneId.systemDefault()
                )
                val endDate = LocalDate.ofInstant(
                    java.time.Instant.ofEpochMilli(receipt.endedAtWallMs), ZoneId.systemDefault()
                )
                val armedPrint = if (armDate != endDate) {
                    val day = java.text.SimpleDateFormat("EEE d MMM", Locale.US)
                        .format(Date(receipt.armWallMs)).uppercase(Locale.US)
                    "ARMED $day ${hhmm(receipt.armWallMs)}"
                } else {
                    "ARMED ${hhmm(receipt.armWallMs)}"
                }
                val planned = receipt.plannedSeconds
                ReceiptMonoLine(
                    text = if (planned != null) {
                        "$armedPrint · PLANNED ${longSpan(planned)}"
                    } else {
                        armedPrint
                    }
                )
                // A2 (audit item 7): the outcome slot prints ONLY
                // when it adds a fact. Plain ENDED repeats the line
                // it stands on (tautology) — suppressed. The
                // shield-gap compound double-prints the gap (the
                // THE SHIELD WAS OFF… line below carries it alone)
                // — suppressed. A legacy SLEPT THROUGH (stamped
                // before the cure) normalises to STOOD DOWN here,
                // so a rehydrated slip never re-prints the old
                // word; new slips are stamped STOOD DOWN at the
                // source (MonolithRepository.receiptOutcomeWord).
                val outcomeSegment = when (receipt.outcomeWord) {
                    "ENDED", "ENDED · SHIELD GAP" -> null
                    "SLEPT THROUGH" -> "STOOD DOWN"
                    else -> receipt.outcomeWord
                }
                ReceiptMonoLine(
                    text = if (outcomeSegment != null) {
                        "ENDED ${hhmm(receipt.endedAtWallMs)} · $outcomeSegment"
                    } else {
                        "ENDED ${hhmm(receipt.endedAtWallMs)}"
                    }
                )
                // The slip's total (receipt audit item 5, A1):
                // the lived span, sealed on the session row and
                // carried in state as sealedElapsedSeconds — set
                // as the total line under ENDED, over a rule. On
                // FLOW slips, where PLANNED is lawfully absent,
                // it is the headline figure. A span of zero seals
                // nothing to total: omitted, never `RAN 0 MIN`.
                if (receipt.sealedElapsedSeconds > 0) {
                    HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
                    // Nachdruck R1 (Finisher 2 §4.3): the total
                    // STRIKES — Anonymous Pro Bold 11sp/1sp. Wave-2
                    // implemented the line and its rule but never
                    // the strike, so the slip's one sum read at the
                    // same weight as every figure above it. 11sp,
                    // not 13sp — 13sp is the interruptions' reserved
                    // size and a 13sp total would compete with the
                    // title and both statements.
                    ReceiptMonoLine(
                        text = "RAN ${longSpan(receipt.sealedElapsedSeconds)}",
                        bold = true
                    )
                }
                receipt.routineLabelWithin?.let { label ->
                    ReceiptMonoLine(text = label)
                }
                // W-41 (receipt audit item 9): the HELD line
                // prints only from the split figures, when the
                // persisted SHUT/PEEK columns exist to fill them.
                // Today heldLine is always null and the line is
                // omitted whole — the old line, summed from
                // heldAppsAtSeal/heldSitesAtSeal, printed PEEK
                // apps as HELD and a blind list size as SITES,
                // and is killed as printed. The summed figures
                // stay on the state as record (STATE-ONLY).
                receipt.heldLine?.let { line ->
                    ReceiptMonoLine(text = line)
                }
                // Item 10 (wave-2): BREAK BANKED is KILLED as
                // printed — as named and as arithmeticked. Its one
                // live path booked a completed valve break's
                // 60-second hold PRICE as "BREAK BANKED 1 MIN", a
                // semantic falsehood, and its /60 floor spoke a
                // different dialect from this file's ceiling
                // longSpan. bankedBreakSeconds stays on the state
                // as record (STATE-ONLY); if a lawful banked-break
                // closing ever lands, the line is re-earned under
                // a true name (BREAK HELD {longSpan}), priced then.
                //
                // A6/A10 (audit items 11/16): the deflections are a
                // SECTION — ruled off, one ink (the dim second ink
                // dies; the list was never a lesser class of fact).
                // Listed, never summed — unchanged.
                // Nachdruck R2 (Finisher 3 §2.2): each entry is a
                // two-column Row — wave-2's single padded string
                // was NOT a hanging indent (a wrapped name fell
                // back under the TIME). The time column never
                // wraps; wrapped name continuations align under
                // the `·`. R3 (Finisher 3 §3): a ≥20-minute silence
                // between two printed entries earns a labelled
                // QUIET span rule (see ReceiptQuietRule). R4
                // (Finisher 3 §4): a site entry prints its domain
                // verbatim plus the `· SITE` tag.
                if (receipt.deflections.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
                }
                receipt.deflections.forEachIndexed { index, d ->
                    if (index > 0) {
                        val gapMs = d.atWallMillis -
                            receipt.deflections[index - 1].atWallMillis
                        if (gapMs >= QUIET_GAP_MS) {
                            ReceiptQuietRule(gapSeconds = (gapMs / 1000L).toInt())
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = hhmm(d.atWallMillis),
                            fontFamily = AnonymousProFamily,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = scheme.onBackground,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "· " + d.appName + if (d.isSite) " · SITE" else "",
                            fontFamily = AnonymousProFamily,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = scheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (receipt.shieldWasOff) {
                    // A6 (audit item 16): the machine's interruptions
                    // speak machine — Anonymous Pro Bold 13sp, the
                    // typewriter's double-strike. The human voice
                    // (Atkinson) belongs to his intention alone.
                    Text(
                        text = "THE SHIELD WAS OFF FOR PART OF THAT SESSION",
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        // Nachdruck R1 (Finisher 2 §5.3): the
                        // statements wrap on 18sp leading, so a
                        // wrapped statement reads as ONE struck
                        // statement, never two ledger lines.
                        lineHeight = 18.sp,
                        letterSpacing = 1.sp,
                        color = scheme.onBackground
                    )
                }
                if (receipt.faulted) {
                    // The one lawful receipt-only fault line
                    // (build-ready §7.2) — it never prints on the
                    // imprint, the strip, or anywhere else.
                    Text(
                        text = "SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)",
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        // Nachdruck R1 (Finisher 2 §5.3): same
                        // 18sp statement leading as above.
                        lineHeight = 18.sp,
                        letterSpacing = 1.sp,
                        color = scheme.onBackground
                    )
                }
                // The footer (W-43, cured — audit item 13): clause
                // 1 was always true and is acquitted; clause 2's
                // noun was false as exclusivity — Monolith itself
                // is a study record (session history, the Drawer);
                // what lives in YPT is HOURS. The cured sentence:
                // THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY HOURS
                // LIVE IN YPT (60 chars; at 0.25.sp tracking / 9.sp
                // it measures ~315dp, inside the sheet's inner
                // width — the one-line law of the receipt-renders
                // seating stands, re-measured for the new words).
                // Guard (Druck P-7): where the line cannot fit —
                // narrow width or his larger fontScale — it splits
                // at its own clause boundary into two lines, never
                // wraps mid-clause. Night ink follows the slip's
                // ink law (body ink at footer weight), not the
                // variant grey that went near-illegible in frame
                // n08. The footer is its own section: ruled off.
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
                androidx.compose.foundation.layout.BoxWithConstraints {
                    val footerColor = scheme.onBackground.copy(alpha = 0.72f)
                    // Nachdruck R5: the guard judges fit AT HIS
                    // fontScale (audit item 13's law) — the cured
                    // sentence measures ~315dp at fontScale 1.0, so
                    // at 1.3 it needs ~410dp and the width-only
                    // guard stayed silent while maxLines = 1
                    // clipped the tail. Dividing the available
                    // width by fontScale makes the clause-boundary
                    // split fire on type size as well as on narrow
                    // paper. Text, ink, and the two-line form are
                    // exactly wave-2's; only the trigger learned
                    // the missing variable.
                    val fontScale = androidx.compose.ui.platform.LocalDensity.current.fontScale
                    if (maxWidth / fontScale < 320.dp) {
                        Column {
                            Text(
                                text = "THE MACHINE KEEPS ITS OWN LOG",
                                fontFamily = AnonymousProFamily,
                                fontSize = 9.sp,
                                letterSpacing = 0.25.sp,
                                color = footerColor
                            )
                            Text(
                                text = "YOUR STUDY HOURS LIVE IN YPT",
                                fontFamily = AnonymousProFamily,
                                fontSize = 9.sp,
                                letterSpacing = 0.25.sp,
                                color = footerColor
                            )
                        }
                    } else {
                        Text(
                            text = "THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY HOURS LIVE IN YPT",
                            fontFamily = AnonymousProFamily,
                            fontSize = 9.sp,
                            letterSpacing = 0.25.sp,
                            color = footerColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptMonoLine(
    text: String,
    bold: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    // Nachdruck R1: the dead `dim` parameter is DELETED — it was
    // the second ink Druck P-6 killed; wave-2 removed its last
    // caller but left the parameter, so any future edit could
    // re-dim a line with one argument. The signature now offers
    // no way to print greyer: one ink, enforced structurally.
    // `bold` replaces it — its only caller is the RAN total.
    Text(
        text = text,
        fontFamily = AnonymousProFamily,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 1.sp,
        color = scheme.onBackground,
        modifier = modifier
    )
}

/** Nachdruck R3 (Finisher 3 §3, Emil P5): the silence threshold
 *  — a gap of ≥20 minutes between two consecutive PRINTED
 *  deflections earns a QUIET span rule (boundary included,
 *  pinned by ReceiptNachdruckTest). Gaps measure on the printed
 *  list only (post-filter, post-dedupe — a row the truth filter
 *  removes lengthens the silence honestly); never before the
 *  first entry (ARMED bounds that silence), never after the
 *  last (ENDED bounds it). */
private const val QUIET_GAP_MS = 1_200_000L

/** The QUIET span rule: a Row in the list's own 8dp rhythm —
 *  hairline, 8dp, label in full body ink (never wraps), 8dp,
 *  hairline. The rule is not an entry: no time, never counted —
 *  the listed-never-summed acquittal is untouched.
 *  Judgment call, recorded in the improver spec: Finisher 3 §3
 *  specced the hairlines in solid onBackground; they are ruled
 *  here in outlineVariant — the ink of every other rule on the
 *  shipped slip (RAN's rule, the section rule, the footer's
 *  rule). One rule ink is the paper's character; the label
 *  strikes in full body ink either way. */
@Composable
private fun ReceiptQuietRule(gapSeconds: Int) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(scheme.outlineVariant)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${longSpan(gapSeconds)} QUIET",
            fontFamily = AnonymousProFamily,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = scheme.onBackground,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(scheme.outlineVariant)
        )
    }
}

private fun hhmm(wallMillis: Long): String =
    SimpleDateFormat("HH:mm", Locale.US).format(Date(wallMillis))

/** The long-form dialect (F-W1): `50 MIN`, `1H 00M` — the same
 *  span classes the Deck's hero prints, never the short form. */
private fun longSpan(seconds: Int): String {
    val minutes = (seconds + 59) / 60
    return if (minutes < 60) {
        "$minutes MIN"
    } else {
        String.format(Locale.US, "%dH %02dM", minutes / 60, minutes % 60)
    }
}
