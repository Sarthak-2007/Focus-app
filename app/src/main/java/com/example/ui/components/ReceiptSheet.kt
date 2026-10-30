package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ReceiptState
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.MonolithCurves
import java.text.SimpleDateFormat
import java.util.Calendar
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
 * long-form durations (`50 MIN`, never `50M`); held counts
 * omitted whole when uncaptured (−1 never prints as a figure);
 * the faulted sheet carries NO title (F-W1 — `ENDED-FAULT`
 * never prints anywhere) and exactly one receipt-only line;
 * the death-booked sheet books `THE SHIELD WAS OFF…` in words.
 * The footer is exact.
 *
 * Issue #57 truth cures (audit R1, slice receipt-sheet — the
 * print character above is untouched; only false or missing
 * facts changed): durations print EXACT to the second
 * ([longSpan] — no ceiling, no integer-division truncation);
 * the sealed elapsed prints as the `RAN …` total under ENDED;
 * a span whose local dates differ puts its day on BOTH the
 * ARMED and ENDED lines (same-day slips unchanged); dismissal
 * fires on the press DOWN (the scrim's `onPress`), and the
 * animation spec is honest — a [MutableTransitionState] runs
 * the enter fade for real, and there is NO exit transition:
 * the host unmounts the slip on dismiss, so an exit fade
 * could never play and none is claimed.
 */
@Composable
fun ReceiptSheet(
    receipt: ReceiptState,
    onDismiss: () -> Unit
) {
    // #57: the enter fade actually runs — the state starts
    // false and targets true, where a hardcoded `visible = true`
    // composed the slip already-settled and claimed an exit
    // fade the host's unmount made impossible.
    val enterState = remember { MutableTransitionState(false) }
    enterState.targetState = true
    AnimatedVisibility(
        visibleState = enterState,
        enter = fadeIn(MonolithCurves.specC()),
        exit = ExitTransition.None
    ) {
        val scheme = MaterialTheme.colorScheme
        // Issue #143 (F6): the slip's raised colour cross-fades
        // with the scheme instead of cutting in one frame.
        val animatedSlip by animateColorAsState(
            targetValue = scheme.surfaceBright,
            animationSpec = tween(durationMillis = 450),
            label = "receiptSlipNightFade"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scheme.scrim.copy(alpha = 0.12f))
                // #57: dismissal lands on the DOWN. `clickable`
                // fires on release (UP); `onPress` is the press
                // itself, as the header's spec has always said.
                .pointerInput(receipt.sessionId) {
                    detectTapGestures(onPress = { onDismiss() })
                }
                .testTag("receiptSheetScrim")
                .padding(12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(animatedSlip)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
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
                    } else {
                        // F-W1: the faulted sheet has no title.
                        Box(modifier = Modifier.weight(1f))
                    }
                    Text(
                        text = receipt.numberPrint,
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = scheme.onSurfaceVariant
                    )
                }

                // RC-02: a FLOW slip has no PLANNED segment —
                // the line carries ARMED alone rather than a
                // floor dressed as a plan.
                // #57: when the span's local dates differ
                // (cross-midnight, multi-day), BOTH lines carry
                // their own day — a bare `ENDED 00:10` after
                // `ARMED 23:50` otherwise reads as ending before
                // it started. Same-day slips print as before.
                val crossesDay = !sameLocalDay(
                    receipt.armWallMs, receipt.endedAtWallMs
                )
                val armedPrint = if (crossesDay) {
                    "ARMED ${dayLabel(receipt.armWallMs)} ${hhmm(receipt.armWallMs)}"
                } else {
                    "ARMED ${hhmm(receipt.armWallMs)}"
                }
                val endedPrint = if (crossesDay) {
                    "ENDED ${dayLabel(receipt.endedAtWallMs)} ${hhmm(receipt.endedAtWallMs)}"
                } else {
                    "ENDED ${hhmm(receipt.endedAtWallMs)}"
                }
                val planned = receipt.plannedSeconds
                ReceiptMonoLine(
                    text = if (planned != null) {
                        "$armedPrint · PLANNED ${longSpan(planned)}"
                    } else {
                        armedPrint
                    }
                )
                ReceiptMonoLine(
                    text = "$endedPrint · ${receipt.outcomeWord}"
                )
                // #57: the slip's total — the sealed elapsed,
                // carried in state since Slice E and printed
                // nowhere. A zero seals nothing to total:
                // omitted, never `RAN 0 MIN`.
                if (receipt.sealedElapsedSeconds > 0) {
                    // Nachdruck R1 (Finisher 2 §4.3, landed via
                    // WELLE-6 recovery): the total STRIKES —
                    // Anonymous Pro Bold. Adapted to main's
                    // divider-less sheet: no rule is added, only
                    // the strike the wave-2 line never carried.
                    ReceiptMonoLine(
                        text = "RAN ${longSpan(receipt.sealedElapsedSeconds)}",
                        bold = true
                    )
                }
                receipt.routineLabelWithin?.let { label ->
                    ReceiptMonoLine(text = label)
                }
                if (receipt.heldAppsAtSeal >= 0 && receipt.heldSitesAtSeal >= 0) {
                    ReceiptMonoLine(
                        text = "HELD ${receipt.heldAppsAtSeal} APPS · ${receipt.heldSitesAtSeal} SITES ON THIS PHONE"
                    )
                }
                // Audit R3b #145: the completion moment names its
                // reward — the curio this finish forged, on the slip
                // itself, in the same mono register as the HELD line.
                receipt.forgedArtifactName?.let { forgedName ->
                    ReceiptMonoLine(
                        text = "FORGED — ${forgedName.uppercase(Locale.US)}"
                    )
                }
                if (receipt.bankedBreakSeconds > 0) {
                    // #57: exact span — the old `/ 60` printed a
                    // false `0 MIN` for 1–59 banked seconds and
                    // truncated every other figure.
                    ReceiptMonoLine(
                        text = "BREAK BANKED ${longSpan(receipt.bankedBreakSeconds)}"
                    )
                }
                // Nachdruck R2/R3/R4 (Finisher 3 §2.2/§3/§4,
                // landed via WELLE-6 recovery, adapted to main's
                // divider-less sheet): each deflection is a
                // two-column Row — the time column never wraps;
                // wrapped name continuations align under the `·`
                // (a hanging indent, not a padded string). One
                // ink — the old `dim` second ink dies here, its
                // last caller. A ≥20-minute silence between two
                // printed entries earns a labelled QUIET span
                // rule (ReceiptQuietRule). A site entry prints
                // its domain verbatim plus the `· SITE` tag.
                // Listed, never summed — unchanged.
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
                    Text(
                        text = "THE SHIELD WAS OFF FOR PART OF THAT SESSION",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        // Nachdruck R1 (Finisher 2 §5.3): the
                        // statements wrap on 18sp leading, so a
                        // wrapped statement reads as ONE struck
                        // statement, never two ledger lines.
                        lineHeight = 18.sp,
                        color = scheme.onBackground
                    )
                }
                if (receipt.openExposureApps.isNotEmpty()) {
                    // Issue #129 F3: the slip names what was
                    // actually used through the open door — a
                    // session stamped OPEN APP USED never reads
                    // as clean, and the names are the evidence.
                    Text(
                        text = "OPEN APPS USED IN THAT SESSION: " +
                            receipt.openExposureApps.joinToString(", ") { it.uppercase() },
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = scheme.onBackground
                    )
                }
                if (receipt.faulted) {
                    // The one lawful receipt-only fault line
                    // (build-ready §7.2) — it never prints on the
                    // imprint, the strip, or anywhere else.
                    Text(
                        text = "SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        // Nachdruck R1: same 18sp statement
                        // leading as above.
                        lineHeight = 18.sp,
                        color = scheme.onBackground
                    )
                }
                // The footer is exact AND one line (Ren's ruling
                // on Voss's wart, receipt-renders seating): at
                // 1.sp tracking the 62-char sentence measured
                // ~372dp against the standard card's 348dp inner
                // width and wrapped, orphaning "YPT". Tracking
                // 0.25.sp at the same 9.sp measures ~326dp — the
                // whole sentence lands on one line, still the
                // same quiet mono footer. Do not "restore" the
                // 1.sp without re-measuring.
                Text(
                    text = "THE MACHINE KEEPS ITS OWN LOG · STUDY HOURS LIVE IN YEOLPUMTA",
                    fontFamily = AnonymousProFamily,
                    fontSize = 9.sp,
                    letterSpacing = 0.25.sp,
                    color = scheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ReceiptMonoLine(text: String, bold: Boolean = false) {
    val scheme = MaterialTheme.colorScheme
    // Nachdruck R1 (WELLE-6 recovery): the dead `dim` parameter
    // is DELETED — it was the second ink; its last caller (the
    // deflection list) now prints in full ink via the R2 rows.
    // The signature offers no way to print greyer: one ink,
    // enforced structurally. `bold` replaces it — its only
    // caller is the RAN total.
    Text(
        text = text,
        fontFamily = AnonymousProFamily,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 1.sp,
        color = scheme.onBackground
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
 *  the listed-never-summed acquittal is untouched. Hairlines
 *  are ruled in outlineVariant — the ink of the sheet's own
 *  border — per the improver spec's recorded judgment call. */
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

/** The printed day for a wall instant: `TUE 4 NOV`. */
internal fun dayLabel(wallMillis: Long): String =
    SimpleDateFormat("EEE d MMM", Locale.US)
        .format(Date(wallMillis)).uppercase(Locale.US)

/** Whether two wall instants share one local calendar day. */
internal fun sameLocalDay(aMillis: Long, bMillis: Long): Boolean {
    val a = Calendar.getInstance().apply { timeInMillis = aMillis }
    val b = Calendar.getInstance().apply { timeInMillis = bMillis }
    return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
        a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}

/** The long-form dialect (F-W1): `50 MIN`, `1H 00M` — the same
 *  span classes the Deck's hero prints, never the short form.
 *
 *  #57: EXACT to the second. The old body ceiled minutes
 *  (`(seconds + 59) / 60`), so a 61-second plan printed
 *  `2 MIN`; whole-minute values — the normal case, plans are
 *  set in minutes — print byte-identically to before, and any
 *  sub-minute remainder is spoken, never rounded or dropped:
 *  `45 SEC`, `1 MIN 01 SEC`, `1H 00M 01 SEC`. */
internal fun longSpan(seconds: Int): String {
    val total = seconds.coerceAtLeast(0)
    val remSeconds = total % 60
    val minutes = total / 60
    val secSuffix =
        if (remSeconds == 0) "" else String.format(Locale.US, " %02d SEC", remSeconds)
    return when {
        total < 60 -> "$total SEC"
        minutes < 60 -> "$minutes MIN$secSuffix"
        else -> String.format(
            Locale.US, "%dH %02dM", minutes / 60, minutes % 60
        ) + secSuffix
    }
}
