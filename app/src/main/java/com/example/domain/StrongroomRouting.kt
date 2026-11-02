package com.example.domain

/**
 * P2 — STRONGROOM ROUTING (BUILD-SR-3, engine wiring's pure half).
 *
 * Every decision the engine makes while a seal stands, as pure
 * functions over the P1 model — the services execute these
 * verdicts, they never invent their own:
 *
 *  - CONTAINMENT: while sealed, the launcher surfaces are
 *    everything — the lawful surface set is closed and the normal
 *    app (Deck, settings, records) is not in it, by construction.
 *  - THE KNOCK: a reach for a locked app produces the knock event
 *    and NOTHING else — no screen, no apology, no opening.
 *  - EXITS land in the antechamber ONLY: PIN, temporary unlock,
 *    charger, cooldown (the #683 set) — through every one of them
 *    the session stays sealed and the door never opens.
 *  - HELD CALLS: contacts RING THROUGH; unknown numbers are held
 *    quietly, his choice per call — a taken call connects THAT
 *    call only and grants nothing else.
 *  - FIRINGS: break start / break end / the lifting are events of
 *    the elapsed span — derived here, armed by the wiring, and
 *    re-derived on every fire (an alarm is a wake-up hint; the
 *    span is the truth).
 *  - TAMPER: a sideways kill under a standing seal is tampering
 *    under the boot-window model, never a lawful exit; a boot
 *    death is the ajar truth, a different verdict from tamper.
 *
 * Device behaviour stands UNPROVEN-ON-DEVICE (Audit A G-7): these
 * are the decisions, proven in code; their on-device effect is the
 * held test order's business, not claimed here.
 */
object StrongroomRouting {

    // ------------------------------------------------------------------
    // Containment — the sealed-hours surface set
    // ------------------------------------------------------------------

    enum class Surface {
        SEALED_FACE, ANTECHAMBER, HELD_CALLS, DESK_CLOCK, LOCK_NOTIFICATION,
        NORMAL_APP, DECK, SETTINGS, RECORDS
    }

    /** The complete lawful surface set while a seal stands (his
     *  Nov 1 order: the launcher is everything). */
    val SEALED_SURFACES: Set<Surface> = setOf(
        Surface.SEALED_FACE, Surface.ANTECHAMBER, Surface.HELD_CALLS,
        Surface.DESK_CLOCK, Surface.LOCK_NOTIFICATION
    )

    fun surfaceLawfulWhileSealed(surface: Surface): Boolean = surface in SEALED_SURFACES

    // ------------------------------------------------------------------
    // The knock
    // ------------------------------------------------------------------

    enum class ReachDecision {
        /** The knock event and nothing else (a REACH punch is struck). */
        KNOCK,
        /** An allowed/peek app: the antechamber's grille door owns it. */
        ANTECHAMBER,
        /** No seal stands: the normal app lives; routing has no say. */
        NOT_SEALED
    }

    /**
     * Route a reach for [packageName] while a day stands sealed.
     * Locked → KNOCK. Allowed (the antechamber set, peek apps
     * included) → ANTECHAMBER. Unlisted → KNOCK: the substrate's
     * fail-closed rule is that an unlisted app is born LOCKED, so
     * its reach is a locked reach. The locked set wins over the
     * allowed set if a package somehow stands in both (a signing
     * contradiction resolves toward the seal, never away from it).
     */
    fun routeReach(
        sealed: Boolean,
        packageName: String,
        lockedPackages: Set<String>,
        allowedPackages: Set<String>
    ): ReachDecision {
        if (!sealed) return ReachDecision.NOT_SEALED
        if (packageName in lockedPackages) return ReachDecision.KNOCK
        if (packageName in allowedPackages) return ReachDecision.ANTECHAMBER
        return ReachDecision.KNOCK
    }

    // ------------------------------------------------------------------
    // Exits — the antechamber only
    // ------------------------------------------------------------------

    /** The lawful exit kinds (the #683 set). */
    enum class ExitKind { PIN, TEMP_UNLOCK, CHARGER, COOLDOWN }

    /** Where every lawful exit lands. [doorOpened] is false and
     *  [sessionSealed] is true for every kind, by construction —
     *  there is no exit value in this model that opens the door. */
    data class ExitLanding(
        val surface: Surface,
        val doorOpened: Boolean,
        val sessionSealed: Boolean
    )

    fun routeExit(kind: ExitKind): ExitLanding = when (kind) {
        ExitKind.PIN, ExitKind.TEMP_UNLOCK, ExitKind.CHARGER, ExitKind.COOLDOWN ->
            ExitLanding(surface = Surface.ANTECHAMBER, doorOpened = false, sessionSealed = true)
    }

    // ------------------------------------------------------------------
    // Held calls — contacts ring, unknowns held quietly
    // ------------------------------------------------------------------

    enum class CallerClass { CONTACT, UNKNOWN }

    enum class CallDecision {
        /** Rings through, answered or declined as any call. */
        RING_THROUGH,
        /** Never rings: lands on the held surface with a quiet dot. */
        HELD_QUIETLY
    }

    fun routeCall(caller: CallerClass): CallDecision = when (caller) {
        CallerClass.CONTACT -> CallDecision.RING_THROUGH
        CallerClass.UNKNOWN -> CallDecision.HELD_QUIETLY
    }

    enum class HeldCallChoice { TAKE, LEAVE }

    /** The reach of a per-call choice: TAKE connects this call
     *  only — it is not an unlock, not a lease, not a door. */
    data class HeldCallResolution(
        val connectsThisCallOnly: Boolean,
        val doorOpened: Boolean,
        val sessionSealed: Boolean
    )

    fun resolveHeldCall(choice: HeldCallChoice): HeldCallResolution = when (choice) {
        HeldCallChoice.TAKE -> HeldCallResolution(
            connectsThisCallOnly = true, doorOpened = false, sessionSealed = true
        )
        HeldCallChoice.LEAVE -> HeldCallResolution(
            connectsThisCallOnly = false, doorOpened = false, sessionSealed = true
        )
    }

    // ------------------------------------------------------------------
    // Firings — events of the elapsed span
    // ------------------------------------------------------------------

    enum class FiringKind { BREAK_START, BREAK_END, LIFT }

    data class FiringEvent(val kind: FiringKind, val atElapsedMillis: Long)

    /**
     * The span's firing schedule, in elapsed order: the break's
     * start and end (when a break was priced at signing) and the
     * lifting. The wiring arms alarms as wake-up hints at the wall
     * estimates of these instants; every fire re-derives from the
     * span before acting, so a moved wall clock can wake the engine
     * early or late but can never move a firing.
     */
    fun firingSchedule(
        signing: StrongroomDay.SigningRecord,
        seal: StrongroomDay.SealSpan
    ): List<FiringEvent> {
        val events = mutableListOf<FiringEvent>()
        StrongroomDay.breakFiring(signing, seal)?.let { firing ->
            events += FiringEvent(FiringKind.BREAK_START, firing.firesAtElapsedMillis)
            events += FiringEvent(FiringKind.BREAK_END, firing.endsAtElapsedMillis)
        }
        events += FiringEvent(FiringKind.LIFT, seal.liftingAtElapsedMillis)
        return events.sortedBy { it.atElapsedMillis }
    }

    /** The next firing strictly after [nowElapsedMillis], or null
     *  when the span's events have all run. */
    fun nextFiring(
        signing: StrongroomDay.SigningRecord,
        seal: StrongroomDay.SealSpan,
        nowElapsedMillis: Long
    ): FiringEvent? = firingSchedule(signing, seal)
        .firstOrNull { it.atElapsedMillis > nowElapsedMillis }

    // ------------------------------------------------------------------
    // Integrity — tamper vs the ajar truth
    // ------------------------------------------------------------------

    enum class SealIntegrity {
        /** The seal stands as struck. */
        INTACT,
        /** The boot died under the seal (power death in the night):
         *  the door ajar, the plate struck with the bracket gap. */
        AJAR_BOOT_DEATH,
        /** A sideways kill under a standing seal: tampering under
         *  the boot-window model — never a lawful exit, never ajar's
         *  innocent truth. */
        BROKEN_TAMPER
    }

    /**
     * Classify a standing seal. A sideways kill outranks boot
     * death: tamper is the stronger truth and the plate must not
     * launder it into an innocent ajar. [sidewaysKillObserved] is
     * the engine's boot-window evidence (a component/service killed
     * out from under the seal, per the substrate's tamper witness).
     */
    fun classifyIntegrity(
        seal: StrongroomDay.SealSpan,
        nowBootCount: Int,
        sidewaysKillObserved: Boolean
    ): SealIntegrity = when {
        sidewaysKillObserved -> SealIntegrity.BROKEN_TAMPER
        nowBootCount != seal.sealedBootCount -> SealIntegrity.AJAR_BOOT_DEATH
        else -> SealIntegrity.INTACT
    }
}
