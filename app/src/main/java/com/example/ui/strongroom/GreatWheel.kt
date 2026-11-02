package com.example.ui.strongroom

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Relight
import com.example.ui.theme.RelightState
import com.example.ui.theme.RelightSurface
import com.example.ui.theme.StrongroomGold
import com.example.ui.theme.StrongroomGround
import com.example.ui.theme.StrongroomLineWork
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToLong

/**
 * A-16 — THE GREAT WHEEL (BUILD-SR-22, the wheel lane).
 *
 * The concept's furniture, made mechanism: ONE great wheel stands
 * on the door. At rest it is the sealed face's furniture (the
 * concept's sealed hours: "the wheel at rest") — drawn by
 * [GreatWheelCanvas], still, never a control while sealed. At the
 * signing, his combination is worked THROUGH the wheel: turning
 * it turns the combination's tumblers ([WheelTurn.dials]) — the
 * spin IS the signature act's mechanism. Signature ≠ swing
 * stands whole: the wheel's turning SETS the day; it seals
 * nothing, swings nothing, drives no bolt. The swing remains the
 * seal's act at the first sitting (P1/P2), untouched by this file.
 *
 * THE COMBINATION IS NEVER WRITTEN ANYWHERE (ledger A-2) — the
 * wheel's turn lives in the signing surface's hand for the act's
 * duration exactly as the dials did, and is wiped at completion.
 * Nothing in this file persists, logs, or carries a position
 * anywhere.
 *
 * ALL FEEL FIGURES ARE UNPROVEN-ON-DEVICE. They are working-form
 * values in ONE named place — [WheelPhysics] — so BUILD-SR-18
 * (E-02, the wheel's maximum treatment) takes them to maximum by
 * editing that object alone, never by forking the mechanism.
 */

/**
 * The wheel's physics parameters — THE single named place.
 * BUILD-SR-18 owns E-02's maximum pass over exactly these values.
 * Working form now (LEAD-STAGE-11 E-02): mass in rotation —
 * continuous resistance rising toward the throw's end, governed
 * inertia, a mechanical seat at settle, no free spin at any
 * flick velocity, and no detent ticks of its own in the felt
 * channel's grammar (ticks here are POSITION events the surface
 * spends through its existing detent voice; the wheel itself
 * speaks in resistance and one terminal seat).
 */
object WheelPhysics {
    /** Combination stations around one revolution: ten, one per
     *  digit — the tumbler reads the wheel. */
    const val DETENTS_PER_REVOLUTION = 10
    const val DETENT_DEGREES = 360f / DETENTS_PER_REVOLUTION

    /** Drag resistance: the fraction of a drag delta the wheel
     *  answers immediately; the rest is felt as load. Rises with
     *  accumulated turn within a pass (the wheel pulls a
     *  mechanism, it does not spin a dial). */
    const val DRAG_RESPONSE_BASE = 0.82f
    const val DRAG_RESPONSE_FLOOR = 0.55f
    const val DRAG_RESPONSE_RISE_PER_REV = 0.10f

    /** Governed inertia: a released wheel carries at most this
     *  far, whatever the flick — free spin is impossible by
     *  construction (velocity is capped BEFORE travel is read). */
    const val MAX_RELEASE_TRAVEL_DEG = 54f
    const val MAX_RELEASE_VELOCITY_DEG_PER_SEC = 540f

    /** Settle: a released wheel comes to rest ON a detent,
     *  always — the seat. It never rests between stations, so an
     *  interrupted turn lands on a lawful side, never a
     *  half-state (E-02 / M-2). */
    const val SETTLE_SNAP_FRACTION = 0.5f

    /** The seat's felt weight, as data for the surface's voice:
     *  one terminal seat, heavier than any station (the surface
     *  spends it through onLatch-class voices; the figure is
     *  named here so E-02's maximum pass tunes it in one place). */
    const val SEAT_WEIGHT = 1.35f
}

/**
 * The wheel's turn, as a pure mechanism (no Compose, no store):
 * angle + the tumbler it drives. [dragBy] returns the detent
 * indices crossed, in order, so the surface fires its detent
 * voice keyed to TRAVEL POSITION — a slow turn and a fast turn
 * through the same arc produce the identical sequence (E-01's
 * law, applied to the wheel).
 */
class WheelTurn(initialDetents: Long = 0L) {

    /** Net detents traversed from the act's start. The tumbler's
     *  whole memory — and it leaves this hand only as [dials],
     *  read by eye, never written anywhere. */
    var totalDetents: Long = initialDetents
        private set

    /** Resting angle: always exactly on a detent (the seat). */
    val angleDegrees: Float
        get() = totalDetents * WheelPhysics.DETENT_DEGREES

    /** The combination as the tumbler stands: three dials, an
     *  odometer carry — ones turn with the wheel, tens carry at
     *  ten, hundreds at a hundred. Negative turns wrap lawfully. */
    fun dials(): List<Int> {
        val wrapped = Math.floorMod(totalDetents, 1000L).toInt()
        return listOf(wrapped / 100, (wrapped / 10) % 10, wrapped % 10)
    }

    /**
     * Turn the wheel by [deltaDegrees] of hand travel. Resistance
     * (rising within the pass) governs how much of the travel the
     * wheel answers; whole detents crossed are committed in
     * order; the fractional remainder is carried as pending
     * travel so no motion is lost or invented. Returns the
     * detent indices crossed (signed direction preserved by
     * order: forward crossings ascend, backward descend).
     */
    private var pendingDegrees: Float = 0f

    fun dragBy(deltaDegrees: Float): List<Long> {
        if (deltaDegrees == 0f) return emptyList()
        val revs = abs(totalDetents) / WheelPhysics.DETENTS_PER_REVOLUTION.toFloat()
        val response = (WheelPhysics.DRAG_RESPONSE_BASE -
            WheelPhysics.DRAG_RESPONSE_RISE_PER_REV * revs)
            .coerceAtLeast(WheelPhysics.DRAG_RESPONSE_FLOOR)
        pendingDegrees += deltaDegrees * response
        val crossed = mutableListOf<Long>()
        while (pendingDegrees >= WheelPhysics.DETENT_DEGREES) {
            pendingDegrees -= WheelPhysics.DETENT_DEGREES
            totalDetents += 1
            crossed += totalDetents
        }
        while (pendingDegrees <= -WheelPhysics.DETENT_DEGREES) {
            pendingDegrees += WheelPhysics.DETENT_DEGREES
            totalDetents -= 1
            crossed += totalDetents
        }
        return crossed
    }

    /**
     * The hand leaves the wheel at [velocityDegPerSec]. Inertia
     * carries it a governed distance — capped first by velocity,
     * then by travel — and it settles ON a detent: the seat.
     * Returns the detents crossed during the carry, in order.
     * A flick buys nothing the hand did not earn: maximum travel
     * is [WheelPhysics.MAX_RELEASE_TRAVEL_DEG] at any velocity.
     */
    fun release(velocityDegPerSec: Float): List<Long> {
        val capped = velocityDegPerSec.coerceIn(
            -WheelPhysics.MAX_RELEASE_VELOCITY_DEG_PER_SEC,
            WheelPhysics.MAX_RELEASE_VELOCITY_DEG_PER_SEC
        )
        // Travel proportional to the capped velocity, hard-capped:
        // 540°/s earns the full 54°; nothing earns more.
        val travel = (capped / WheelPhysics.MAX_RELEASE_VELOCITY_DEG_PER_SEC) *
            WheelPhysics.MAX_RELEASE_TRAVEL_DEG
        val crossed = dragBy(travel / responseAtRest())
        settle()
        return crossed
    }

    private fun responseAtRest(): Float = WheelPhysics.DRAG_RESPONSE_BASE

    /** Settle onto the nearest detent. Pending fractional travel
     *  past the snap fraction completes its detent; short of it,
     *  the wheel falls back to the detent it rests on. After
     *  this, the wheel is exactly on a station — the seat. */
    fun settle() {
        if (pendingDegrees >= WheelPhysics.DETENT_DEGREES * WheelPhysics.SETTLE_SNAP_FRACTION) {
            totalDetents += 1
        } else if (pendingDegrees <= -WheelPhysics.DETENT_DEGREES * WheelPhysics.SETTLE_SNAP_FRACTION) {
            totalDetents -= 1
        }
        pendingDegrees = 0f
    }

    /** Forget the turn whole — the signing's wipe at completion:
     *  the combination existed in this hand only, and now it
     *  does not exist anywhere. */
    fun reset() {
        totalDetents = 0L
        pendingDegrees = 0f
    }

    /** The interrupted turn (E-02's named test): whatever the
     *  interruption — call, touch, the surface dying — the wheel
     *  is left exactly on a detent. There is no half-state to
     *  resume from because there is no half-state at all. */
    fun interrupt() = settle()

    /** Fractional progress toward the next detent, −1..1 — the
     *  drawing reads it so the wheel's drawn position is the
     *  mechanism's position, never a decoration beside it. */
    fun pendingFraction(): Float =
        pendingDegrees / WheelPhysics.DETENT_DEGREES
}

/** Whole detents in a drag arc, for the surface's gesture maths. */
internal fun detentsIn(degrees: Float): Long =
    floor(abs(degrees) / WheelPhysics.DETENT_DEGREES).roundToLong()

/**
 * THE WHEEL, DRAWN — the face's craft applied to the wheel
 * (DESIGNER-SR-1's heightfield discipline, in the standing P3
 * idiom): rim and spokes are not flat fills. Every member is
 * drawn twice — a graphite groove pass and a lit-lip pass offset
 * TOWARD THE STATE'S LIGHT (Relight.paramsFor direction, not a
 * constant offset) — over gold read through
 * [Relight.stateSurface], so SEALED's 3/8 descent falls on the
 * wheel exactly as on the rest of the door: the wheel is
 * furniture OF the face, lit by the face's one law.
 *
 * [angleDegrees] turns the spokes + station pips with the
 * mechanism (at rest on the face it is the sealing-time angle,
 * fixed — the face never alters while sealed; the drawing holds
 * no state and runs no clock). This composable carries NO touch
 * handling of its own: on the sealed face a touch is the knock
 * (the face's law); at the signing the surface wraps it in its
 * own gesture layer and the wheel is turned through [WheelTurn].
 */
@Composable
fun GreatWheelCanvas(
    angleDegrees: Float,
    lightState: RelightState,
    modifier: Modifier = Modifier
) {
    val gold = Relight.stateSurface(StrongroomGold, lightState, RelightSurface.FACE)
    val groove = StrongroomLineWork
    val params = Relight.paramsFor(lightState)
    // The lit lip sits toward the light, scaled to the drawing:
    // direction from the law, magnitude a best-judgement software
    // figure (Audit A G-4 class, UNPROVEN).
    val lip = 1.6f
    val lipX = params.lightDirectionX * lip
    val lipY = params.lightDirectionY * lip

    Canvas(modifier.testTag("great_wheel")) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val rimR = size.minDimension / 2f - 3.dp.toPx()
        val hubR = rimR * 0.16f
        val spokeInner = hubR * 0.9f
        val spokeOuter = rimR * 0.94f
        val turn = Math.toRadians(angleDegrees.toDouble())

        fun polar(r: Float, a: Double): Offset = Offset(
            center.x + (r * Math.cos(a)).toFloat(),
            center.y + (r * Math.sin(a)).toFloat()
        )

        // Station pips: ten, one per detent, cut into the rim's
        // inner edge — the combination's stations, visible.
        repeat(WheelPhysics.DETENTS_PER_REVOLUTION) { i ->
            val a = turn + i * (2.0 * Math.PI / WheelPhysics.DETENTS_PER_REVOLUTION)
            val p = polar(rimR * 0.86f, a)
            drawCircle(color = groove, radius = 2.dp.toPx(), center = p)
            drawCircle(
                color = gold, radius = 2.dp.toPx(),
                center = Offset(p.x + lipX, p.y + lipY)
            )
        }
        // Spokes: six, groove first, lit lip toward the light.
        repeat(6) { i ->
            val a = turn + i * (Math.PI / 3.0)
            val s = polar(spokeInner, a)
            val e = polar(spokeOuter, a)
            drawLine(
                color = gold, strokeWidth = 5.dp.toPx(),
                start = Offset(s.x + lipX, s.y + lipY),
                end = Offset(e.x + lipX, e.y + lipY)
            )
            drawLine(
                color = groove, strokeWidth = 5.dp.toPx(), start = s, end = e
            )
        }
        // The rim: a gold rule pair, the door's own gold — an
        // outer rule and an inner rule, the band between them the
        // ground itself (no fill, by the ring's standing law).
        drawCircle(
            color = gold, radius = rimR, center = center,
            style = Stroke(width = 2.5.dp.toPx())
        )
        drawCircle(
            color = gold.copy(alpha = 0.75f), radius = rimR * 0.78f,
            center = center, style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = groove, radius = rimR - 1.25.dp.toPx(), center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        // The hub: a graphite seat with its gold lip — where the
        // hand's work gathers.
        drawCircle(color = groove, radius = hubR, center = center)
        drawCircle(
            color = gold, radius = hubR * 0.55f,
            center = Offset(center.x + lipX, center.y + lipY)
        )
        drawCircle(color = StrongroomGround, radius = hubR * 0.34f, center = center)
    }
}
