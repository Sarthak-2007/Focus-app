package com.example.domain

/**
 * P1 — THE GUARD STATE MACHINE (BUILD-SR-3, build-first-usable).
 *
 * Pure transitions over plain values, his Oct 31 rulings carried
 * verbatim (Audit A §3). The three rules:
 *
 *  1. FROZEN ALLOWANCE — tighten instantly anytime; a raise takes
 *     effect only from the next day-start (03:30), never same-day;
 *     once the day's purse is spent, loosening controls sit beyond
 *     the hard stop (a raise cannot even be SCHEDULED from inside
 *     a spent day — the control is unreachable, not merely delayed).
 *  2. SLOW OFF — off takes effect at the next day-start; on is
 *     instant; NO off exists while a sealed block is active (the
 *     request is refused, not queued — there is no off to queue).
 *     Sideways kills are tampering, handled by the engine's
 *     boot-window model (P2), never as a lawful transition here.
 *  3. A STRETCH HAS NO OFF — the end date chosen at setup is the
 *     exit; while a stretch stands there is no off control at all:
 *     [requestOff] has no lawful path that returns anything but
 *     REFUSED_STRETCH. No witness/urge-logging button exists in
 *     this model — the type has no field that could carry one.
 *
 * Exits are advance-chosen + the 60-second crank valve
 * ([StrongroomDay.GovernorCrankWeek]) — no stockpile of spendable
 * exits exists here either.
 *
 * All functions are total and side-effect free: the store applies
 * the returned state. Time is passed in (wall millis + the
 * day-start facts from [StrongroomDay]), never read.
 */
object StrongroomGuard {

    enum class OffRefusal { SEALED_ACTIVE, STRETCH_ACTIVE }

    sealed interface OffRequest {
        /** Off scheduled: takes effect at [effectiveAtMillis]
         *  (the next day-start). Until then the Guard stands ON. */
        data class Scheduled(val effectiveAtMillis: Long) : OffRequest
        data class Refused(val reason: OffRefusal) : OffRequest
    }

    sealed interface RaiseRequest {
        /** Raise scheduled from the next day-start. */
        data class Scheduled(val newAllowanceMinutes: Int, val effectiveAtMillis: Long) : RaiseRequest
        /** Beyond the hard stop: the day's purse is spent, so the
         *  loosening control is unreachable today. */
        object BeyondHardStop : RaiseRequest
    }

    /**
     * The Guard's standing state.
     *
     * [allowanceMinutes] is the allowance IN FORCE now.
     * [pendingAllowanceMinutes] / [pendingAllowanceFromMillis]: a
     * scheduled raise and the day-start it takes effect from
     * (-1 / 0 = none). [pendingOffFromMillis]: a scheduled slow-off
     * and its day-start (0 = none); the Guard is ON until it lands.
     * [stretchEndMillis]: the stretch's chosen end (0 = no stretch);
     * while now < end, the stretch stands and has no off.
     * [spentDateIso] / [spentMinutesThatDay]: the purse fact the
     * hard-stop rule reads, keyed by filing date — the store feeds
     * it from the day row; a spend on another filing date never
     * blocks today's raise.
     */
    data class GuardState(
        val guardOn: Boolean = true,
        val allowanceMinutes: Int,
        val pendingAllowanceMinutes: Int = -1,
        val pendingAllowanceFromMillis: Long = 0L,
        val pendingOffFromMillis: Long = 0L,
        val stretchEndMillis: Long = 0L,
        val spentDateIso: String = "",
        val spentMinutesThatDay: Int = 0
    ) {
        init {
            require(allowanceMinutes >= 0) { "allowance cannot be negative" }
        }

        fun stretchActive(nowMillis: Long): Boolean =
            stretchEndMillis > 0L && nowMillis < stretchEndMillis

        /** The purse is spent when today's spend reached the
         *  allowance in force. An allowance of 0 is spent by
         *  definition (nothing may be drawn). */
        fun purseSpentToday(todayIso: String): Boolean =
            spentDateIso == todayIso && spentMinutesThatDay >= allowanceMinutes
    }

    // ------------------------------------------------------------------
    // Rule 1 — frozen allowance
    // ------------------------------------------------------------------

    /**
     * Tighten: a lower allowance takes effect INSTANTLY, anytime —
     * including inside a sealed day and inside a stretch (tightening
     * is never the weak hour's escape; it is the strong hour's
     * direction). A pending raise is superseded by the tighten: the
     * newest word stands. An equal value is a no-op tighten.
     */
    fun tighten(state: GuardState, newAllowanceMinutes: Int): GuardState {
        require(newAllowanceMinutes in 0..state.allowanceMinutes) {
            "tighten only lowers (or holds) the allowance; a raise goes through requestRaise"
        }
        return state.copy(
            allowanceMinutes = newAllowanceMinutes,
            pendingAllowanceMinutes = -1,
            pendingAllowanceFromMillis = 0L
        )
    }

    /**
     * Raise: scheduled from the NEXT day-start, never same-day —
     * unless the day's purse is already spent, in which case the
     * control sits beyond the hard stop and the raise cannot even
     * be scheduled ([RaiseRequest.BeyondHardStop]). A raise to the
     * current value or below is routed to tighten semantics by the
     * caller; here a raise must actually raise.
     */
    fun requestRaise(
        state: GuardState,
        newAllowanceMinutes: Int,
        todayIso: String,
        nextDayStartMillis: Long
    ): Pair<GuardState, RaiseRequest> {
        require(newAllowanceMinutes > state.allowanceMinutes) {
            "requestRaise is for raises only; tightening is instant via tighten()"
        }
        if (state.purseSpentToday(todayIso)) {
            return state to RaiseRequest.BeyondHardStop
        }
        val next = state.copy(
            pendingAllowanceMinutes = newAllowanceMinutes,
            pendingAllowanceFromMillis = nextDayStartMillis
        )
        return next to RaiseRequest.Scheduled(newAllowanceMinutes, nextDayStartMillis)
    }

    // ------------------------------------------------------------------
    // Rule 2 — slow off / instant on
    // ------------------------------------------------------------------

    /**
     * Request off. Refused while a sealed block is active (no off
     * exists then — refused, never queued) and refused while a
     * stretch stands (a stretch has no off at all). Otherwise the
     * off is SCHEDULED for the next day-start; the Guard stands
     * until it lands ([applyPending]).
     */
    fun requestOff(
        state: GuardState,
        nowMillis: Long,
        sealedBlockActive: Boolean,
        nextDayStartMillis: Long
    ): Pair<GuardState, OffRequest> {
        if (state.stretchActive(nowMillis)) {
            return state to OffRequest.Refused(OffRefusal.STRETCH_ACTIVE)
        }
        if (sealedBlockActive) {
            return state to OffRequest.Refused(OffRefusal.SEALED_ACTIVE)
        }
        val next = state.copy(pendingOffFromMillis = nextDayStartMillis)
        return next to OffRequest.Scheduled(nextDayStartMillis)
    }

    /** On is instant: the Guard stands now, and any pending
     *  slow-off is cancelled by the on (the newest word stands). */
    fun turnOn(state: GuardState): GuardState =
        state.copy(guardOn = true, pendingOffFromMillis = 0L)

    // ------------------------------------------------------------------
    // Rule 3 — a stretch has no off
    // ------------------------------------------------------------------

    /**
     * Begin a stretch: the end chosen at setup is the exit. There
     * is deliberately no endStretch / cancelStretch function in
     * this object — the only way a stretch ends is its end arriving
     * ([applyPending]). Beginning a stretch also cancels any
     * pending slow-off: an off that would land inside the stretch
     * cannot exist.
     */
    fun beginStretch(state: GuardState, endMillis: Long, nowMillis: Long): GuardState {
        require(endMillis > nowMillis) { "a stretch ends in the future; its end is its exit" }
        return state.copy(
            guardOn = true,
            stretchEndMillis = endMillis,
            pendingOffFromMillis = 0L
        )
    }

    // ------------------------------------------------------------------
    // Landing pending changes
    // ------------------------------------------------------------------

    /**
     * Apply whatever has come due at [nowMillis]: a scheduled raise
     * whose day-start has arrived takes effect; a scheduled slow-off
     * whose day-start has arrived turns the Guard off; a stretch
     * whose end has arrived simply ends (its exit was chosen at
     * setup). Idempotent and safe to call on every read.
     */
    fun applyPending(state: GuardState, nowMillis: Long): GuardState {
        var next = state
        if (next.pendingAllowanceMinutes >= 0 && nowMillis >= next.pendingAllowanceFromMillis) {
            next = next.copy(
                allowanceMinutes = next.pendingAllowanceMinutes,
                pendingAllowanceMinutes = -1,
                pendingAllowanceFromMillis = 0L
            )
        }
        if (next.stretchEndMillis > 0L && nowMillis >= next.stretchEndMillis) {
            next = next.copy(stretchEndMillis = 0L)
        }
        if (next.pendingOffFromMillis > 0L && nowMillis >= next.pendingOffFromMillis) {
            next = next.copy(guardOn = false, pendingOffFromMillis = 0L)
        }
        return next
    }
}
