package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FloatingPeekPillState
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Issue #102 (peek-lived, finding 5): the peek offer's terms, in
 * the app's own words, printed BEFORE the peek is taken. The peek
 * chip is labelled "EMERGENCY PEEK" while the token-spending door
 * is "EMERGENCY BREAK" — this line is what tells the user the two
 * are different doors: the peek spends no weekly token and does
 * not end the session. Pure, so the wording is unit-tested.
 */
internal fun peekTermsText(): String =
    "THE PEEK DOES NOT SPEND YOUR EMERGENCY BREAK AND DOES NOT END THE SESSION"

/** The pill face's clock: M:SS from the lease's remaining seconds. */
internal fun peekCountdownText(secondsRemaining: Int): String {
    val s = secondsRemaining.coerceAtLeast(0)
    return String.format(Locale.US, "%d:%02d", s / 60, s % 60)
}

/**
 * Issue #102 (peek-lived, finding 1): the floating countdown pill,
 * rendered inside Monolith. This is the in-app face of the lease —
 * the same pill the shield service draws as a system overlay over
 * the peeked app (PeekPillOverlay), so the user has a clock and a
 * working ✕ wherever they are. The countdown is the VM's state,
 * which is DERIVED from the Room lease's stored end (UNA2-06) —
 * never a second clock. Draggable; the ✕ hands the peek back
 * early via [onEndPeek] (MonolithViewModel.end2MinutePeekEarly,
 * which closes the lease row and anchors the cooldown).
 */
@Composable
fun FloatingPeekPill(
    state: FloatingPeekPillState,
    onEndPeek: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!state.isActive) return
    var offsetX by remember(state.packageName) { mutableIntStateOf(0) }
    var offsetY by remember(state.packageName) { mutableIntStateOf(0) }
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .offset { IntOffset(offsetX, offsetY) }
            .pointerInput(state.packageName) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x.roundToInt()
                    offsetY += dragAmount.y.roundToInt()
                }
            }
            .clip(RoundedCornerShape(999.dp))
            .background(scheme.primary)
            .border(1.dp, scheme.primaryContainer, RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("floating_peek_pill"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "PEEK",
            fontFamily = AtkinsonHyperlegibleFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = scheme.onPrimary
        )
        Text(
            text = peekCountdownText(state.secondsRemaining),
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = scheme.onPrimary,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Text(
            text = "✕",
            fontFamily = AtkinsonHyperlegibleFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = scheme.primaryContainer,
            modifier = Modifier
                .clickable { onEndPeek() }
                .padding(horizontal = 4.dp)
                .testTag("peek_pill_end_button")
        )
    }
}
