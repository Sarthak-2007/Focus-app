package com.example.ui.maximum

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * THE COMBINATION SPIN, AS A SURFACE — the proof piece's hand
 * (BUILD-SR-11, T24, Stage 11).
 *
 * One centred dial; the thumb orbits the rim annulus (UX-SR-1 §5,
 * adopted by FEEL-SR-3 C1). All physics and every felt event live
 * in [CombinationSpin] — this file only translates touch into
 * orbit deltas and renders the dial's angle. It is the assembly
 * that replaces the signing's +/− steppers: one call, with the
 * caller's event binding (sound/haptics bind at [onEvent]; the
 * haptics slice owns the motor, held by plan §8) and [onSeated]
 * as the signature's completion.
 *
 * The dial renders its travel, never a number being "entered":
 * there is no digit readout, no combination display, nothing to
 * store (ledger A-2).
 */

/** The orbit angle of point (x, y) around centre (cx, cy), degrees in [0, 360). */
fun orbitAngleDeg(cx: Float, cy: Float, x: Float, y: Float): Float {
    val deg = Math.toDegrees(atan2((y - cy).toDouble(), (x - cx).toDouble())).toFloat()
    return (deg + 360f) % 360f
}

/** The signed smallest turn from [previousDeg] to [currentDeg], degrees in (−180, 180]. */
fun orbitDeltaDeg(previousDeg: Float, currentDeg: Float): Float {
    var delta = (currentDeg - previousDeg) % 360f
    if (delta > 180f) delta -= 360f
    if (delta < -180f) delta += 360f
    return delta
}

@Composable
fun CombinationSpinDial(
    seatAtDeg: Float,
    seatDirection: Int,
    onEvent: (SpinEvent) -> Unit = {},
    onSeated: () -> Unit = {},
    modifier: Modifier = Modifier,
    groundColor: Color = Color(0xFF493EA4),
    ringColor: Color = Color(0xFFE2BC39),
    lineColor: Color = Color(0xFF34323B)
) {
    val spin = remember(seatAtDeg, seatDirection) {
        CombinationSpin(seatAtDeg = seatAtDeg, seatDirection = seatDirection, onEvent = onEvent)
    }
    var angleDeg by remember(spin) { mutableFloatStateOf(spin.angleDeg) }
    var lastMillis = remember(spin) { longArrayOf(0L) }

    Box(
        modifier = modifier
            .size(280.dp)
            .testTag("maximum_combination_spin_dial")
            .pointerInput(spin) {
                detectDragGestures(
                    onDragStart = {
                        spin.beginGesture()
                        lastMillis[0] = 0L
                    },
                    onDrag = { change, _ ->
                        val centre = size.width / 2f
                        val now = change.uptimeMillis
                        val dtMs = if (lastMillis[0] == 0L) 16L
                        else (now - lastMillis[0]).coerceIn(8L, 50L)
                        lastMillis[0] = now
                        val previous = orbitAngleDeg(
                            centre, centre, change.previousPosition.x, change.previousPosition.y
                        )
                        val current = orbitAngleDeg(
                            centre, centre, change.position.x, change.position.y
                        )
                        spin.rotate(orbitDeltaDeg(previous, current), dtMs / 1000f)
                        angleDeg = spin.angleDeg
                        if (spin.seated) onSeated()
                        change.consume()
                    },
                    onDragEnd = {
                        spin.endGesture()
                        angleDeg = spin.angleDeg
                    },
                    onDragCancel = {
                        spin.endGesture()
                        angleDeg = spin.angleDeg
                    }
                )
            }
    ) {
        Canvas(Modifier.size(280.dp)) {
            val centre = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 12f
            // The ground: the kept violet, as a filled disc.
            drawCircle(color = groundColor, radius = radius, center = centre)
            // The ring: gold, one stroke — the dial's rim the thumb orbits.
            drawCircle(
                color = ringColor, radius = radius - 6f, center = centre,
                style = Stroke(width = 3f)
            )
            // The stations: ten line-work ticks, one per number.
            for (i in 0 until 10) {
                val a = Math.toRadians((i * MaximumPresets.SPIN_STATION_SPACING_DEG - 90f).toDouble())
                val outer = Offset(
                    centre.x + (radius - 14f) * cos(a).toFloat(),
                    centre.y + (radius - 14f) * sin(a).toFloat()
                )
                val inner = Offset(
                    centre.x + (radius - 30f) * cos(a).toFloat(),
                    centre.y + (radius - 30f) * sin(a).toFloat()
                )
                drawLine(color = lineColor, start = inner, end = outer, strokeWidth = 3f)
            }
            // The hand's place: a gold marker riding the rim at the dial's angle.
            val markerAngle = Math.toRadians((angleDeg % 360f - 90f).toDouble())
            val marker = Offset(
                centre.x + (radius - 22f) * cos(markerAngle).toFloat(),
                centre.y + (radius - 22f) * sin(markerAngle).toFloat()
            )
            drawCircle(color = ringColor, radius = 9f, center = marker)
            drawLine(color = ringColor, start = centre, end = marker, strokeWidth = 3f)
        }
    }
}
