package com.example.service.haptics

import kotlin.math.roundToInt

/**
 * COMPOSITION PRIMITIVES — the reusable felt effects the feel
 * seats' numbers plug into (FEEL-SR-5). Pure builders: every
 * function returns a [HapticWaveform] whose shape carries the
 * argument, so FEEL-SR-1's door tables, FEEL-SR-3's control
 * detents and FEEL-SR-4's census rows are data built from these,
 * never new code per control. UNPROVEN-ON-DEVICE throughout.
 *
 * The house laws built into the shapes themselves:
 *  - A detent is a strike that settles (strike > tail, always).
 *  - The seal's clunks DESCEND in pitch (the sound row's fact)
 *    while their felt MASS ASCENDS (FEEL-SR-1's correction: an
 *    amplitude-descending clunk set makes the last bolt feel
 *    like the weakest — a send-back, refused here by construction).
 *  - A resistance stop is ONE rising load cut abruptly, then
 *    silence. Its total length is capped in the builder so no
 *    caller can stretch it into an error buzz (H-3).
 */
object HapticPrimitives {

    /** One detent: [strikeMs] at [amplitude], a settling tail at
     *  a fixed fraction. The substrate's rotary shape (7 ms @ 180,
     *  5 ms @ 70) is this builder at its defaults. */
    fun tick(
        amplitude: Int = 180,
        strikeMs: Long = 7L,
        tailMs: Long = 5L,
        tailFraction: Float = 0.39f,
        fallbackMs: Long = 1L
    ): HapticWaveform {
        require(amplitude in 1..255)
        val tail = (amplitude * tailFraction).roundToInt().coerceIn(1, 255)
        return HapticWaveform(
            timingsMs = listOf(0L, strikeMs, tailMs),
            amplitudes = listOf(0, amplitude, tail),
            fallbackMs = fallbackMs
        )
    }

    /** One mass strike with no bounce: a full blow, a settle at
     *  body weight, a final seat — amplitudes only fall. The
     *  knock ([com.example.service.StrongroomKnock], BUILD-SR-2's
     *  spec) is this shape at 255/96/48 over 42/30/16 ms. */
    fun thud(
        strikeAmplitude: Int = 255,
        strikeMs: Long = 42L,
        settleAmplitude: Int = 96,
        settleMs: Long = 30L,
        seatAmplitude: Int = 48,
        seatMs: Long = 16L,
        fallbackMs: Long = 42L
    ): HapticWaveform {
        require(strikeAmplitude >= settleAmplitude && settleAmplitude >= seatAmplitude) {
            "a thud only falls: strike >= settle >= seat"
        }
        return HapticWaveform(
            timingsMs = listOf(0L, strikeMs, settleMs, seatMs),
            amplitudes = listOf(0, strikeAmplitude, settleAmplitude, seatAmplitude),
            fallbackMs = fallbackMs
        )
    }

    /** A seat: strike, breath (a true gap), softer landing. The
     *  two-stage latch and the final combination detent are seats
     *  at different weights. */
    fun seat(
        strikeAmplitude: Int = 215,
        strikeMs: Long = 11L,
        gapMs: Long = 26L,
        seatAmplitude: Int = 150,
        seatMs: Long = 9L,
        fallbackMs: Long = 4L
    ): HapticWaveform = HapticWaveform(
        timingsMs = listOf(0L, strikeMs, gapMs, seatMs),
        amplitudes = listOf(0, strikeAmplitude, 0, seatAmplitude),
        fallbackMs = fallbackMs
    )

    /**
     * A train of [count] detents, amplitude travelling from
     * [startAmplitude] to [endAmplitude] (either direction — the
     * spin thickens, the completion softens), each detent a strike
     * followed by a silence gap. [gapMs] is the hand's travel
     * between detents; the train's skeleton (count · order · gaps)
     * is what degradation preserves on every motor class.
     */
    fun detentTrain(
        count: Int,
        startAmplitude: Int,
        endAmplitude: Int,
        strikeMs: Long = 8L,
        gapMs: Long = 18L,
        fallbackMs: Long = 8L
    ): HapticWaveform {
        require(count >= 1) { "a train has at least one detent" }
        require(startAmplitude in 1..255 && endAmplitude in 1..255)
        val timings = mutableListOf(0L)
        val amplitudes = mutableListOf(0)
        for (i in 0 until count) {
            val amp = if (count == 1) startAmplitude else {
                startAmplitude + ((endAmplitude - startAmplitude).toFloat() *
                    i / (count - 1)).roundToInt()
            }
            timings += strikeMs
            amplitudes += amp
            if (i < count - 1) {
                timings += gapMs
                amplitudes += 0
            }
        }
        return HapticWaveform(timings, amplitudes, fallbackMs)
    }

    /**
     * The seal's [count] clunks as ONE atomic waveform: strikes
     * separated by latch silence, felt mass ASCENDING from
     * [firstAmplitude] to [lastAmplitude] while strike bodies
     * lengthen — pitch descent lives in the sound row; weight
     * descent onto the door lives here (FEEL-SR-1). Gaps widen
     * slightly per bolt, as the sound score's Seal gaps do.
     */
    fun descendingClunks(
        count: Int = 3,
        firstAmplitude: Int = 235,
        lastAmplitude: Int = 255,
        firstBodyMs: Long = 46L,
        lastBodyMs: Long = 78L,
        firstGapMs: Long = 120L,
        gapGrowthMs: Long = 30L,
        fallbackMs: Long = 90L
    ): HapticWaveform {
        require(count >= 2) { "a clunk set is plural" }
        require(lastAmplitude >= firstAmplitude) {
            "felt mass ascends across the clunks (FEEL-SR-1) — pitch descends, weight does not"
        }
        require(lastBodyMs >= firstBodyMs)
        val timings = mutableListOf(0L)
        val amplitudes = mutableListOf(0)
        for (i in 0 until count) {
            val f = i.toFloat() / (count - 1)
            timings += (firstBodyMs + (lastBodyMs - firstBodyMs) * f).roundToInt().toLong()
            amplitudes += (firstAmplitude + (lastAmplitude - firstAmplitude) * f).roundToInt()
            if (i < count - 1) {
                timings += firstGapMs + gapGrowthMs * i
                amplitudes += 0
            }
        }
        return HapticWaveform(
            timingsMs = timings,
            amplitudes = amplitudes,
            fallbackMs = fallbackMs,
            kind = WaveformKind.CEREMONY
        )
    }

    /**
     * The lifting's [count] bolt withdrawals — the time-reverse
     * of a clunk, per bolt: STRAIN (amplitude ramping up in three
     * steps as the bolt loads), RELEASE (one short snap), SLIDE
     * (a low free run-out). Heaviest bolt first: the set's strain
     * peaks DESCEND, where the seal's strikes ascended — that
     * asymmetry is what a blind hand sorts sealing from lifting
     * on (FEEL-SR-1, H-1).
     */
    fun withdrawals(
        count: Int = 3,
        firstPeakAmplitude: Int = 235,
        lastPeakAmplitude: Int = 195,
        strainStepMs: Long = 22L,
        snapMs: Long = 8L,
        slideMs: Long = 30L,
        gapMs: Long = 110L,
        fallbackMs: Long = 80L
    ): HapticWaveform {
        require(count >= 2)
        require(firstPeakAmplitude >= lastPeakAmplitude) {
            "withdrawals run heaviest-first — the seal read backwards"
        }
        val timings = mutableListOf(0L)
        val amplitudes = mutableListOf(0)
        for (i in 0 until count) {
            val f = if (count == 1) 0f else i.toFloat() / (count - 1)
            val peak = (firstPeakAmplitude +
                (lastPeakAmplitude - firstPeakAmplitude) * f).roundToInt()
            // strain: three rising steps to the peak
            for (step in 1..3) {
                timings += strainStepMs
                amplitudes += (peak * step / 3).coerceAtLeast(1)
            }
            // release snap, then the free slide
            timings += snapMs
            amplitudes += peak
            timings += slideMs
            amplitudes += (peak * 0.3f).roundToInt().coerceAtLeast(1)
            if (i < count - 1) {
                timings += gapMs
                amplitudes += 0
            }
        }
        return HapticWaveform(
            timingsMs = timings,
            amplitudes = amplitudes,
            fallbackMs = fallbackMs,
            kind = WaveformKind.CEREMONY
        )
    }

    /**
     * THE RESISTANCE STOP (H-3): the hand meets a load that
     * rises in [steps] and then the mechanism simply does not
     * move — one short brake, then nothing. There is no tail, no
     * repetition, and the total is hard-capped: a stop that lasts
     * is a buzz, and a buzz is a lie about what happened.
     */
    fun resistanceStop(
        peakAmplitude: Int = 210,
        steps: Int = 3,
        stepMs: Long = 26L,
        brakeMs: Long = 14L,
        fallbackMs: Long = 24L
    ): HapticWaveform {
        require(peakAmplitude in 1..255)
        require(steps >= 2)
        val total = stepMs * steps + brakeMs
        require(total <= MAX_RESISTANCE_TOTAL_MS) {
            "a resistance stop over $MAX_RESISTANCE_TOTAL_MS ms reads as an error buzz (H-3)"
        }
        val timings = mutableListOf(0L)
        val amplitudes = mutableListOf(0)
        for (step in 1..steps) {
            timings += stepMs
            amplitudes += (peakAmplitude * step / (steps + 1)).coerceAtLeast(1)
        }
        timings += brakeMs
        amplitudes += peakAmplitude
        return HapticWaveform(
            timingsMs = timings,
            amplitudes = amplitudes,
            fallbackMs = fallbackMs,
            kind = WaveformKind.RESISTANCE
        )
    }

    /**
     * THE HOLLOW SEAT (FEEL-SR-3's invention, for the Guard
     * raise): the control travels, the swell rises toward a seat
     * — and the seat never lands. A true gap where the landing
     * belongs, then a faint echo of the travel. Deferral you can
     * feel: the raise is filed for the next day-start, and the
     * hand learns that from the shape, not from a message.
     */
    fun hollowSeat(
        swellAmplitude: Int = 140,
        swellMs: Long = 60L,
        hollowMs: Long = 70L,
        echoAmplitude: Int = 55,
        echoMs: Long = 18L,
        fallbackMs: Long = 30L
    ): HapticWaveform {
        require(swellAmplitude > echoAmplitude) { "the echo is fainter than the swell" }
        return HapticWaveform(
            timingsMs = listOf(0L, swellMs / 2, swellMs / 2, hollowMs, echoMs),
            amplitudes = listOf(
                0,
                (swellAmplitude * 0.6f).roundToInt(),
                swellAmplitude,
                0,
                echoAmplitude
            ),
            fallbackMs = fallbackMs
        )
    }

    /**
     * THE CARVE (FEEL-SR-3, for the stretch's end-date): a scrape
     * of short strokes that thin out, ONE cut, then permanent
     * silence — the waveform ends at the cut; what follows it in
     * the registry is a SILENCE row (the carved strip answers
     * touches with nothing, ever).
     */
    fun carve(
        strokes: Int = 4,
        firstAmplitude: Int = 150,
        lastAmplitude: Int = 90,
        strokeMs: Long = 16L,
        gapMs: Long = 12L,
        cutAmplitude: Int = 205,
        cutMs: Long = 12L,
        fallbackMs: Long = 24L
    ): HapticWaveform {
        require(strokes >= 2)
        val timings = mutableListOf(0L)
        val amplitudes = mutableListOf(0)
        for (i in 0 until strokes) {
            val f = i.toFloat() / (strokes - 1)
            timings += strokeMs
            amplitudes += (firstAmplitude + (lastAmplitude - firstAmplitude) * f).roundToInt()
            timings += gapMs
            amplitudes += 0
        }
        timings += cutMs
        amplitudes += cutAmplitude
        return HapticWaveform(timings, amplitudes, fallbackMs)
    }

    /** The hard cap on a resistance stop's length (H-3). */
    const val MAX_RESISTANCE_TOTAL_MS = 160L
}
