package com.example.ui.signing

import com.example.service.haptics.HapticEvent
import com.example.ui.foundation.StrongroomMaximum
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/**
 * THE SIGNING'S FEEL — the touch half of the great wheel's
 * mechanism (FEEL-SEAT, wired under Ren). A pure translator:
 * the wheel's motion stream in, [HapticEvent]s out — the
 * surface fires what this returns through the one
 * [com.example.service.haptics.SensoryBus] (via the bridge's
 * surface door), so the felt, heard and seen halves of each
 * event stay one registry row, exactly as everywhere else.
 *
 * The figures are the filed ones, in physics vocabulary —
 * nothing here is tuned by adjective:
 *
 *  - THE RAIL: the wheel's detents are the spin's stations —
 *    [StrongroomMaximum.SPIN_STATION_SPACING_DEG] = 36°,
 *    ten per revolution (the P4 wheel's filed
 *    DETENTS_PER_REVOLUTION, carried by [CombinationSpin]'s
 *    own station law). The wheel has no second, finer rail:
 *    its ratchet click and the number's notch are the same
 *    crossing, felt once. (The substrate's single-voice form
 *    of that same detent stands in the registry as
 *    ROTARY_DETENT, the legacy row — it is not double-fired.)
 *  - ONE NOTCH PER NUMBER: travel that crosses a station
 *    boundary fires [HapticEvent.COMBINATION_STATION] once
 *    per boundary crossed, in either direction — a pawl
 *    re-clicks its tooth both ways, and so does this.
 *  - HEAVIER AT THE GATES: a gate is a direction reversal
 *    (FEEL-SR-3 C1.2). A reversal is only a gate when the
 *    hand has committed at least one full station (36°) of
 *    travel in the direction it abandons — sub-station
 *    jitter at a boundary is travel changing its mind, not
 *    a gate, and fires nothing. A gate fires
 *    [HapticEvent.COMBINATION_GATE]: a 35 ms felt void,
 *    then one tick at amplitude 165 against the station's
 *    70 — the heaviness is in the registry's numbers.
 *  - THE SEATS: a taken seat fires nothing of its own for
 *    seats one and two — the station tick at the arrival is
 *    the spec's confirmation, and a refused seat is silent
 *    by the same spec (no error event exists for the spin).
 *    The third seat is the signature landing:
 *    [HapticEvent.COMBINATION_FINAL_SEAT].
 *  - THE SEATED DIAL: a drag-end seat attempt against the
 *    completed spin fires [HapticEvent.COMBINATION_END_STOP]
 *    — one flat wall at amplitude 195, no tick, once per
 *    attempt.
 *
 * The stream's terms are the surface's own: [onTravel] takes
 * the governed rotation the drag already computed (ceiling
 * 240°/s, [StrongroomMaximum.SPIN_MAX_VELOCITY_DEG_PER_SEC]),
 * so feel can never be spent by travel the mechanism itself
 * refused. All felt figures UNPROVEN-ON-DEVICE (G-7).
 */
class SigningFeel {

    private var lastRotationDeg: Double? = null
    private var committedDirection: Int = 0
    private var travelInDirectionDeg: Double = 0.0

    /**
     * Feed the wheel's governed rotation after a drag move.
     * Returns the events this movement earned, in firing
     * order (station crossings before a gate the same
     * movement completes — the hand crosses the tooth
     * before it can reverse against it).
     */
    fun onTravel(rotationDeg: Double): List<HapticEvent> {
        val previous = lastRotationDeg
        lastRotationDeg = rotationDeg
        if (previous == null) return emptyList()
        val delta = rotationDeg - previous
        if (delta == 0.0) return emptyList()

        val events = mutableListOf<HapticEvent>()

        // Station crossings: one notch per 36° boundary the
        // travel passed, whichever way it was going.
        repeat(crossedStations(previous, rotationDeg)) {
            events += HapticEvent.COMBINATION_STATION
        }

        // The gate: committed travel abandoned for the
        // opposite direction.
        val direction = if (delta > 0.0) 1 else -1
        if (committedDirection != 0 && direction != committedDirection &&
            travelInDirectionDeg >= StrongroomMaximum.SPIN_STATION_SPACING_DEG
        ) {
            events += HapticEvent.COMBINATION_GATE
            travelInDirectionDeg = 0.0
        }
        if (direction == committedDirection) {
            travelInDirectionDeg += abs(delta)
        } else {
            committedDirection = direction
            travelInDirectionDeg = abs(delta)
        }
        return events
    }

    /** A seat was taken. Only the final seat — the signature
     *  landing — carries its own event. */
    fun onSeatTaken(finalSeat: Boolean): List<HapticEvent> =
        if (finalSeat) listOf(HapticEvent.COMBINATION_FINAL_SEAT) else emptyList()

    /** A drag ended in a seat attempt against the already-
     *  completed spin: the seated dial's flat wall. */
    fun onAttemptAgainstSeatedDial(): List<HapticEvent> =
        listOf(HapticEvent.COMBINATION_END_STOP)

    /**
     * The detents a travel from [fromDeg] to [toDeg] spends:
     * the whole multiples of the 36° spacing the hand ARRIVES
     * at or passes. Arriving onto a tooth counts; departing
     * the tooth the hand already sits on does not recount it
     * — the pawl's own arithmetic, and the reason the two
     * directions use floor and ceiling respectively.
     */
    private fun crossedStations(fromDeg: Double, toDeg: Double): Int {
        val spacing = StrongroomMaximum.SPIN_STATION_SPACING_DEG
        return when {
            toDeg > fromDeg ->
                (floor(toDeg / spacing) - floor(fromDeg / spacing)).toInt()
            toDeg < fromDeg ->
                (ceil(fromDeg / spacing) - ceil(toDeg / spacing)).toInt()
            else -> 0
        }
    }
}
