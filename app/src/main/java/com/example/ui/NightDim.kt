package com.example.ui

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.testTag
import com.example.data.BreakDimState
import com.example.data.NightDimMath
import kotlinx.coroutines.delay

/**
 * Issue #143 F1/F5/F6 — the Compose half of the night progression.
 * The pure curve lives in [NightDimMath]; the shield loop publishes
 * the break's phase as [BreakDimState]; this file renders it:
 *
 *  - [rememberBreakDimAlpha]: the live scrim depth. Into the break
 *    it follows the shield's published elapsed seconds; out of the
 *    break it is clocked HERE every 100ms from the break-end stamp,
 *    so the return is a smooth 4-second fade even though the
 *    engine only ticks every 2 seconds.
 *  - [BreakDimScrim]: a black scrim whose alpha IS the progression.
 *    It carries NO pointer handlers — the Deck underneath (its hero
 *    countdown, its END BREAK verb) stays tappable through the
 *    whole descent; the scrim only takes light away.
 *  - [nightDesaturate]: the F5 grayscale, applied as a saturation
 *    colour matrix over a surface. A saturation of 1f is a no-op
 *    fast path, so day surfaces never pay for the night machinery.
 */
fun Modifier.nightDesaturate(saturation: Float): Modifier {
    if (saturation >= 0.999f) return this
    val matrix = ColorMatrix().apply { setToSaturation(saturation.coerceIn(0f, 1f)) }
    val paint = Paint().apply { colorFilter = ColorFilter.colorMatrix(matrix) }
    return this.drawWithContent {
        drawIntoCanvas { canvas ->
            canvas.saveLayer(Rect(Offset.Zero, size), paint)
            drawContent()
            canvas.restore()
        }
    }
}

/** The live dim alpha for a published [BreakDimState] at [nowMono]:
 *  in a break, the descent's depth; after it, the fade's remainder. */
fun breakDimAlphaAt(state: BreakDimState, nowMono: Long): Float = when {
    state.inBreak -> NightDimMath.dimAlphaIntoBreak(state.breakElapsedSeconds)
    state.breakEndedMono > 0L ->
        NightDimMath.dimAlphaOutOfBreak(nowMono - state.breakEndedMono, state.fadeFromAlpha)
    else -> 0f
}

@Composable
fun rememberBreakDimAlpha(state: BreakDimState): Float {
    var nowMono by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(state.breakEndedMono, state.inBreak) {
        if (!state.inBreak && state.breakEndedMono > 0L) {
            while (true) {
                nowMono = SystemClock.elapsedRealtime()
                if (nowMono - state.breakEndedMono >= NightDimMath.FADE_OUT_MILLIS) break
                delay(100L)
            }
        }
    }
    return breakDimAlphaAt(state, nowMono)
}

@Composable
fun BreakDimScrim(alpha: Float, modifier: Modifier = Modifier) {
    if (alpha <= 0.001f) return
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = alpha))
            .testTag("break_dim_scrim")
    )
}
