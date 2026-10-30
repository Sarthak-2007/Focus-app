package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ReceiptState
import com.example.ui.theme.AnonymousProFamily
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
 * long-form durations (`50 MIN`, never `50M`); held counts
 * omitted whole when uncaptured (−1 never prints as a figure);
 * the faulted sheet carries NO title (F-W1 — `ENDED-FAULT`
 * never prints anywhere) and exactly one receipt-only line;
 * the death-booked sheet books `THE SHIELD WAS OFF…` in words.
 * The footer is exact.
 */
@Composable
fun ReceiptSheet(
    receipt: ReceiptState,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        val scheme = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.12f))
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
                val planned = receipt.plannedSeconds
                ReceiptMonoLine(
                    text = if (planned != null) {
                        "ARMED ${hhmm(receipt.armWallMs)} · PLANNED ${longSpan(planned)}"
                    } else {
                        "ARMED ${hhmm(receipt.armWallMs)}"
                    }
                )
                ReceiptMonoLine(
                    text = "ENDED ${hhmm(receipt.endedAtWallMs)} · ${receipt.outcomeWord}"
                )
                receipt.routineLabelWithin?.let { label ->
                    ReceiptMonoLine(text = label)
                }
                if (receipt.heldAppsAtSeal >= 0 && receipt.heldSitesAtSeal >= 0) {
                    ReceiptMonoLine(
                        text = "HELD ${receipt.heldAppsAtSeal} APPS · ${receipt.heldSitesAtSeal} SITES"
                    )
                }
                if (receipt.bankedBreakSeconds > 0) {
                    ReceiptMonoLine(
                        text = "BREAK BANKED ${receipt.bankedBreakSeconds / 60} MIN"
                    )
                }
                receipt.deflections.forEach { d ->
                    ReceiptMonoLine(
                        text = "${hhmm(d.atWallMillis)} · ${d.appName}",
                        dim = true
                    )
                }
                if (receipt.shieldWasOff) {
                    Text(
                        text = "THE SHIELD WAS OFF FOR PART OF THAT SESSION",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = scheme.onBackground
                    )
                }
                if (receipt.penaltySecondsAdded > 0) {
                    // Audit R3 #113, finding 4: the penalty was
                    // already inside the PLANNED figure above (the
                    // span prices it in) — this line names what the
                    // plan silently included, in the same bold
                    // register as the shield-gap line.
                    Text(
                        text = "A ${receipt.penaltySecondsAdded / 60} MIN TAMPER PENALTY WAS ADDED TO THIS SESSION",
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
                    text = "THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT",
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
private fun ReceiptMonoLine(text: String, dim: Boolean = false) {
    val scheme = MaterialTheme.colorScheme
    Text(
        text = text,
        fontFamily = AnonymousProFamily,
        fontSize = 11.sp,
        letterSpacing = 1.sp,
        color = if (dim) scheme.onSurfaceVariant else scheme.onBackground
    )
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
