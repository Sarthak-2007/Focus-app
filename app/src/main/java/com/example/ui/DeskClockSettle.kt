package com.example.ui

/**
 * FLOW §2 — the desk clock's auto-raise, decided one ticker beat
 * at a time (plan/FLOW-REGISTER.md §2.1–2.3; Ablauf's founding
 * edition). Pure on purpose: every input arrives as an argument,
 * so the whole law is provable without a phone.
 *
 * The three laws it carries (§2.2):
 *  1. RAISE ONCE PER SESSION — on the settle for a session this
 *     process watched seal, at once for a session already live
 *     when the process was born (the cold open: the clock is the
 *     first surface, §2.3). Never repeatedly.
 *  2. A DISMISSAL IS A DECISION — a hand dismissal (or a hand
 *     raise) decides the session; the machine does not argue
 *     with the hand for the rest of that session.
 *  3. IT IS A READ FACE — this machine only ever answers "raise
 *     now?" with yes once; every verb stays on the Deck.
 *
 * The settle (§2.1): 20 s after the seal or after his last
 * interaction, whichever is later. The working seconds after a
 * seal are his — a face that seizes the screen at the Throw
 * would fight the hand that just armed it; the clock arrives
 * when the hand leaves.
 *
 * The cold open is told from a watched seal by the row itself:
 * a session whose startWallTimeMillis predates the process's
 * birth was born before us (routine-born, or the app was swiped
 * away mid-session) — §2.6 prices its raise as a derivation,
 * and §2.2 states the one acceptable roughness plainly: a cold
 * process may re-raise once over a session a warmer process saw
 * dismissed. The dismissal bit is in-memory by design.
 *
 * V1 AS AMENDED (issue #13, Ren's ruling on audit E-2 — Fen's
 * discriminator): Vega's ~5 Hz stillness sampler aliased
 * physiological tremor (8–12 Hz) into desk-stillness and seized
 * held phones in ~4–8 s, and the unconditional 20 s backstop
 * re-seized a reader the sensors had acquitted. The amended
 * machine therefore samples at ~50 Hz (SENSOR_DELAY_GAME) ONLY
 * while armed-undecided, reads band energy in the 7–14 Hz
 * tremor band, and takes a THREE-STATE verdict:
 *  - REST (band at the noise floor): raise once the verdict
 *    has SUSTAINED for REST_DWELL_BEATS consecutive beats
 *    (Wave-2, Samt F-1 — a single sampled REST can be a braced
 *    reader's breath trough).
 *  - HELD (band above floor, however still the orientation):
 *    suppresses BOTH the sensor raise path and the time
 *    backstop while the signature persists; once HELD persists
 *    the sampler duty-cycles 6 s in every 30 s.
 *  - UNKNOWN (bag, car, no signature either way) and
 *    sensor-absent: the 20 s backstop alone, exactly as before.
 * What a resting phone knows that a held phone doesn't: a
 * living hand shakes; furniture does not.
 *
 * The wake re-raise (the battery-yield return, A-10(v)) RE-ARMS
 * this sampler: [noteWakeReRaise] clears the decided bit and the
 * held signature for the session, so the wake path is built on
 * the sensor law again, never time-only.
 */
internal class DeskClockSettleMachine(
    private val settleMillis: Long = SETTLE_MILLIS
) {
    private var armedSessionId: Int = NO_SESSION
    private var armStartMono: Long = 0L
    private var decidedSessionId: Int = NO_SESSION
    private var heldSinceMono: Long = 0L
    private var heldSessionId: Int = NO_SESSION
    // Wave-2 (Samt F-1): consecutive REST beats toward the
    // dwell — see the REST branch in tick.
    private var restBeats: Int = 0

    /** True when the most recent raise this machine granted was
     *  a cold open (A-4's SEEDED entry cause reads it). */
    var lastRaiseWasColdOpen: Boolean = false
        private set

    /** A hand decision on the clock — raised by the chip or
     *  tapped away on the face — settles [sessionId]: the
     *  machine will neither raise it nor re-raise it. */
    fun noteHandDecision(sessionId: Int) {
        decidedSessionId = sessionId
    }

    /** The wake re-raise RE-ARMS the sampler (A-10(v) / Nacht's
     *  condition on V1): a battery-yield wake is not a hand
     *  decision and not a dismissal — the session is undecided
     *  again, the held signature forgotten, the settle re-arms
     *  from this beat. */
    fun noteWakeReRaise(sessionId: Int) {
        if (decidedSessionId == sessionId) decidedSessionId = NO_SESSION
        if (heldSessionId == sessionId) {
            heldSessionId = NO_SESSION
            heldSinceMono = 0L
        }
        restBeats = 0
        if (armedSessionId == sessionId) armedSessionId = NO_SESSION
    }

    /** Whether [sessionId] is decided (hand or a granted raise). */
    fun isDecided(sessionId: Int): Boolean = sessionId == decidedSessionId

    /**
     * The sampler's duty law (V1 as amended): sample only while
     * armed-undecided. While the verdict is not HELD, sampling is
     * continuous (~50 Hz at the registration site). Once HELD has
     * persisted past [HELD_DUTY_AFTER_MILLIS], the sampler runs
     * 6 s in every 30 s — enough to notice the phone being put
     * down, cheap enough to leave running all night.
     */
    fun sensorSamplingActive(sessionId: Int, nowMono: Long): Boolean {
        if (sessionId == NO_SESSION || sessionId == decidedSessionId) return false
        if (heldSessionId != sessionId || heldSinceMono == 0L) return true
        val heldFor = nowMono - heldSinceMono
        if (heldFor < HELD_DUTY_AFTER_MILLIS) return true
        val cyclePos = (heldFor - HELD_DUTY_AFTER_MILLIS) % HELD_DUTY_PERIOD_MILLIS
        return cyclePos < HELD_DUTY_ON_MILLIS
    }

    /**
     * One ticker beat. Returns true exactly on the beat the
     * desk-clock visibility flag should be written true; the
     * caller (the VM's per-second ticker) performs the write —
     * this class holds no flag (§2.6: one flag write, in the
     * existing ticker).
     *
     * @param sessionId the live session's id, or null when no
     * session is active this beat.
     * @param sessionStartWallMillis the live row's birth (wall).
     * @param processBirthWallMillis this process's birth (wall).
     * @param visible the flag's current value — a clock already
     * up (by any hand) decides its session silently.
     * @param nowMono this beat's monotonic stamp.
     * @param lastInteractionMono the last user interaction's
     * monotonic stamp (the Activity's onUserInteraction).
     * @param sensorVerdict the tremor sampler's current verdict
     * (V1 as amended). Defaults to UNKNOWN — the pre-V1 call
     * shape — which takes the 20 s backstop alone.
     */
    fun tick(
        sessionId: Int?,
        sessionStartWallMillis: Long,
        processBirthWallMillis: Long,
        visible: Boolean,
        nowMono: Long,
        lastInteractionMono: Long,
        sensorVerdict: DeskClockVerdict = DeskClockVerdict.UNKNOWN
    ): Boolean {
        val id = sessionId ?: NO_SESSION
        if (id == NO_SESSION) {
            // No live session: the arm dies with it. (The finish
            // machine and the disarm paths hide the clock
            // themselves; nothing here may resurrect it.)
            armedSessionId = NO_SESSION
            heldSessionId = NO_SESSION
            heldSinceMono = 0L
            restBeats = 0
            return false
        }
        if (id == decidedSessionId) return false
        if (visible) {
            // Up by hand (the DESK CLOCK chip) or by an earlier
            // beat of this machine: the session is decided.
            decidedSessionId = id
            return false
        }
        if (sensorVerdict == DeskClockVerdict.HELD) {
            // HELD suppresses BOTH raise paths while the tremor
            // signature persists (E-2): no sensor raise, and the
            // 20 s backstop does not run against a held phone.
            restBeats = 0
            if (heldSessionId != id) {
                heldSessionId = id
                heldSinceMono = nowMono
            }
            return false
        }
        if (heldSessionId == id) {
            // The signature lifted: the phone was put down (or
            // the hand stilled past the band). The session arms
            // fresh from this beat under the new verdict.
            heldSessionId = NO_SESSION
            heldSinceMono = 0L
            armedSessionId = NO_SESSION
        }
        if (sessionStartWallMillis < processBirthWallMillis) {
            // Cold open into a live session (§2.3): the row
            // predates us. The clock is the first surface.
            decidedSessionId = id
            lastRaiseWasColdOpen = true
            return true
        }
        if (id != armedSessionId) {
            armedSessionId = id
            armStartMono = nowMono
        }
        if (sensorVerdict == DeskClockVerdict.REST) {
            // REST: the tremor band sits at the noise floor —
            // furniture, not a hand. The settle's 20 s was only
            // ever a proxy for this fact.
            // Wave-2 fix (Samt F-1): REST must be SUSTAINED, not
            // sampled. The old code raised on the first REST
            // beat — but the verdict under it is an EMA with a
            // ~0.4 s memory, so a braced reader's breath trough
            // (or one still instant in a held hand) read REST
            // and seized the phone mid-read: the exact seizure
            // V1-as-amended exists to prevent. Two consecutive
            // REST beats (this ticker's grain: ~2 s of sustained
            // furniture signature) raise; one beat is a trough
            // until proven otherwise.
            restBeats++
            if (restBeats >= REST_DWELL_BEATS) {
                decidedSessionId = id
                lastRaiseWasColdOpen = false
                restBeats = 0
                return true
            }
            return false
        }
        restBeats = 0
        // UNKNOWN (or sensor-absent): the 20 s backstop, alone.
        val deadline = maxOf(armStartMono, lastInteractionMono) + settleMillis
        if (nowMono >= deadline) {
            decidedSessionId = id
            lastRaiseWasColdOpen = false
            return true
        }
        return false
    }

    companion object {
        /** §2.1: longer than a task-tear and a glance, shorter
         *  than any screen-timeout race that matters. */
        const val SETTLE_MILLIS: Long = 20_000L
        const val NO_SESSION: Int = -1

        /** Wave-2 (Samt F-1): consecutive REST beats required
         *  before the sensor path raises — sustained furniture,
         *  never a sampled instant. */
        const val REST_DWELL_BEATS: Int = 2

        /** V1 as amended: HELD this long earns the duty cycle. */
        const val HELD_DUTY_AFTER_MILLIS: Long = 30_000L
        const val HELD_DUTY_PERIOD_MILLIS: Long = 30_000L
        const val HELD_DUTY_ON_MILLIS: Long = 6_000L
    }
}

/** V1 as amended — the settle machine's three-state verdict. */
internal enum class DeskClockVerdict { REST, HELD, UNKNOWN }

/**
 * The tremor discriminator (V1 as amended, pure): band energy in
 * the 7–14 Hz physiological-tremor band, read against the noise
 * floor. Energy at or below [REST_CEILING_RATIO] × the floor is
 * REST (furniture); anything clearly above is HELD (a living
 * hand, however still the orientation reads); no usable
 * signature at all (no sensor, no samples, a bag/car trace with
 * energy in neither pattern) is UNKNOWN.
 *
 * @param bandEnergy running variance of the band-passed
 * acceleration magnitude over the current window, or null when
 * the sampler produced no usable window (sensor absent).
 * @param noiseFloor the sampler's calibrated floor for a phone
 * at rest on furniture.
 */
internal fun deskClockTremorVerdict(bandEnergy: Double?, noiseFloor: Double): DeskClockVerdict {
    val energy = bandEnergy ?: return DeskClockVerdict.UNKNOWN
    if (noiseFloor <= 0.0) return DeskClockVerdict.UNKNOWN
    return if (energy <= noiseFloor * REST_CEILING_RATIO) {
        DeskClockVerdict.REST
    } else {
        DeskClockVerdict.HELD
    }
}

/** REST's ceiling: band energy within 1.5× the furniture floor
 *  is still furniture (a desk transmits footsteps; a hand does
 *  not stop at 1.5×). */
internal const val REST_CEILING_RATIO: Double = 1.5

/** Wave-2 (Salz C-17): the production noise floor for the
 *  two-one-pole bandpass estimator in the VM. This is an
 *  ESTIMATOR CONSTANT, not a calibrated floor — no bench has
 *  tuned it (§7 debt 9, Vega's traces, stands). It exists so
 *  the verdict function has a denominator; any sentence
 *  claiming the sampler is "tuned" is false until the bench
 *  lands. Units: (m/s²)² of 7–14 Hz band-signal variance. */
internal const val TREMOR_FLOOR_UNCALIBRATED: Double = 0.0004

/** The tremor band V1 reads (Hz): physiological tremor lives at
 *  8–12 Hz; the band is cut 7–14 to hold the whole signature. */
internal const val TREMOR_BAND_LOW_HZ: Double = 7.0
internal const val TREMOR_BAND_HIGH_HZ: Double = 14.0

/**
 * A-10(v)'s surrender deadline, pure: the keep-awake yield comes
 * at max(settle stamp, last interaction, last charger
 * disconnect) + 10 min still. A plug-in before the deadline
 * cancels the yield (the caller re-derives with charging =
 * true); a plug-in after it does not resurrect — resurrection is
 * the wake path's job (noteWakeReRaise). All stamps monotonic;
 * a stamp of 0 means "never happened" and loses the max honestly.
 */
internal fun deskClockSurrenderDeadlineMono(
    settleStampMono: Long,
    lastInteractionMono: Long,
    lastChargerDisconnectMono: Long
): Long = maxOf(settleStampMono, lastInteractionMono, lastChargerDisconnectMono) +
    SURRENDER_STILL_MILLIS

/** A-10(v): ten minutes still on battery before the face yields. */
internal const val SURRENDER_STILL_MILLIS: Long = 600_000L

/** A-10(v): at true-off − 520 ms the print fades 520 Curve C to
 *  0 over the black ground; the flag then clears into OLED black. */
internal const val SURRENDER_FADE_MILLIS: Long = 520L

/**
 * Wave-2 fix (Salz C-16, CRITICAL) — the charger-disconnect
 * stamp, pure. The surrender deadline is only as honest as this
 * stamp's provenance: the ticker samples the plug state EVERY
 * beat (clock visible or not) and folds it through here.
 *  - plugged → unplugged edge: stamp = this beat.
 *  - first-ever observation, already unplugged: stamp = this
 *    beat (a process born on battery prices stillness from its
 *    first beat, never from a stamp it never took).
 *  - plugged beat: the stamp is KEPT, never zeroed — a plug-in
 *    does not erase the fact of the last disconnect (the old
 *    hidden-branch reset did exactly that, and the deadline
 *    then fell back to the evening's settle stamp).
 *  - still unplugged: the original edge stamp stands.
 */
internal fun chargerDisconnectStamp(
    prevPlugged: Boolean?,
    nowPlugged: Boolean,
    prevStampMono: Long,
    nowMono: Long
): Long = when {
    nowPlugged -> prevStampMono
    prevPlugged != false -> nowMono
    else -> prevStampMono
}
