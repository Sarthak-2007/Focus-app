package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.provider.Settings
import com.example.AppContainer
import com.example.data.StrongroomDayEntity
import com.example.data.StrongroomStore
import com.example.data.toSealSpan
import com.example.data.toSigningRecord
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomRouting
import com.example.service.haptics.AndroidWaveformPlayer
import com.example.service.haptics.HapticEvent

/**
 * P2 — ENGINE WIRING (BUILD-SR-3): the Strongroom model, wired to
 * the substrate's proven-in-code engine.
 *
 * Every decision here is [StrongroomRouting]'s or [StrongroomDay]'s;
 * this object only reads the clocks, persists through
 * [StrongroomStore], and drives the existing machinery:
 *
 *  - SEAL ACTIVATION: the swing strikes the elapsed span in the
 *    store, raises the shield (MonolithShieldService's own start),
 *    and arms the firing alarms. While sealed, containment is the
 *    shield + accessibility machinery's standing work, and the
 *    lawful surface set is [StrongroomRouting.SEALED_SURFACES] —
 *    no route to the normal app exists in this wiring.
 *  - ALARMS ARE HINTS, THE SPAN IS THE TRUTH: ExactAlarm arms at
 *    the WALL ESTIMATE of each elapsed firing; [StrongroomLiftReceiver]
 *    re-derives from the span on every wake, so a moved wall clock
 *    can wake the engine early or late but can never move a firing
 *    (the substrate's ExactAlarm doc states the same law:
 *    punctuality, never correctness).
 *  - THE KNOCK: [onLockedAppReach] consults the routing and, on
 *    KNOCK, strikes the REACH punch — the knock event and nothing
 *    else. It opens nothing, starts nothing, shows nothing.
 *  - BOOT: [onBoot] re-derives the standing seal — INTACT re-arms
 *    and re-raises (the face re-shown exactly as cut, never
 *    recomposed, B-10); AJAR strikes the ajar plate (the door
 *    tells the truth first, before anything else); a sideways kill
 *    is classified TAMPER for the boot-window machinery, never
 *    laundered into the ajar truth here.
 *  - HELD CALLS: the notification listener's call handling reads
 *    [StrongroomRouting.routeCall] through [callDecision] — a
 *    CONTACT rings through, an UNKNOWN is held quietly.
 *  - THE ONE NOTIFICATION (E-4; wiring landed by BUILD-SR-14,
 *    the residue BUILD-SR-13's composition record §Cures.6 left
 *    standing): P8's [SessionNotification] is the sealed
 *    session's ONLY lock-screen presence, and this bridge is its
 *    production caller. [SessionNotification.beginSession] fires
 *    at the swing in [activateSeal] — only on a struck span, so
 *    a failed swing posts nothing. [SessionNotification.cancel]
 *    fires at the lifting in [onFiringWake] and at seal death in
 *    [onBoot] (AJAR and BROKEN_TAMPER alike: a dead seal stands
 *    no presence). An INTACT boot calls [SessionNotification.show]
 *    — never beginSession: the session never ended, so its
 *    dismissal bit must survive the reboot exactly as he left it.
 *    The shield service's standing card stays retired as a
 *    lock-screen presence (composition cure 6); nothing here
 *    resurrects a second one.
 *
 * Device behaviour (containment, persistence, exactly-two-openings
 * on his OnePlus) stands UNPROVEN-ON-DEVICE (Audit A G-7). This
 * wiring claims the code paths, never the device.
 */
object StrongroomEngineBridge {

    private fun store(context: Context): StrongroomStore =
        StrongroomStore(AppContainer.dao(context.applicationContext))

    /**
     * STAGE 14 (BUILD-SR-20): the felt layer's one door. Every
     * ceremony this bridge drives fires its [HapticEvent] through
     * [com.example.service.haptics.SensoryBus] — the single event
     * table (R8-4) — with the real Android waveform player. The
     * bus's sound/motion players are the recorded NoOps until
     * BUILD-SR-19's sound adapter lands behind the bus (named in
     * BUILD-SR-20's record, never faked here). A sensory failure
     * never fails the ceremony: the span is the truth, the felt
     * event is its testimony (L-5 — never replayed, never a
     * substitute buzz). All felt figures UNPROVEN-ON-DEVICE.
     */
    private fun fireSensory(context: Context, event: HapticEvent) {
        try {
            AndroidWaveformPlayer.bus(context.applicationContext).fire(event)
        } catch (_: Exception) {
            // Silence for this firing; the ceremony stands.
        }
    }

    /** The monotonic clock the seal's span is struck on. */
    fun nowElapsedMillis(): Long = SystemClock.elapsedRealtime()

    /** The boot count, by the repository's own pattern
     *  (Settings.Global.BOOT_COUNT; -1 = unreadable, which the span
     *  reads as a different boot — fail-closed toward ajar). */
    fun bootCount(context: Context): Int = try {
        Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
    } catch (_: Exception) {
        -1
    }

    // ------------------------------------------------------------------
    // Seal activation + firing alarms
    // ------------------------------------------------------------------

    /**
     * THE SWING, wired: strike the span for [dateIso] at the first
     * sitting, raise the shield, arm the firings. The day may be
     * composed or signed — the swing never checks (B-2). Returns
     * the struck span, or null when no day stands / its gaps do not
     * fit (the caller surfaces the failure; nothing is sealed on a
     * failed swing).
     */
    suspend fun activateSeal(context: Context, dateIso: String): StrongroomDay.SealSpan? {
        val appContext = context.applicationContext
        val store = store(appContext)
        val span = store.seal(
            dateIso = dateIso,
            sealedAtWallMillis = System.currentTimeMillis(),
            sealedAtElapsedMillis = nowElapsedMillis(),
            bootCount = bootCount(appContext)
        ) ?: return null
        // THE DOOR, felt: the swing + three bolts fire as ONE
        // SensoryBus event at the ceremony's t=0 (FEEL-SR-1's
        // canonical 1,204 ms array stands verbatim in the
        // registry). Primed first (R-8) so the first strike is
        // not paid for in cold latency.
        try {
            AndroidWaveformPlayer.prime(appContext)
        } catch (_: Exception) {
        }
        fireSensory(appContext, HapticEvent.DOOR_SEAL)
        MonolithShieldService.startShield(appContext)
        armFirings(appContext, dateIso)
        // The swing has taken: the session's one lock-screen
        // presence begins with the seal (E-4).
        SessionNotification.beginSession(appContext)
        return span
    }

    /**
     * Arm one wake-up hint per firing (break start, break end,
     * lift) at its wall estimate: sealed wall + the elapsed time
     * still to run. Re-arming replaces the prior hints (same
     * request codes + FLAG_UPDATE_CURRENT).
     */
    suspend fun armFirings(context: Context, dateIso: String) {
        val appContext = context.applicationContext
        val store = store(appContext)
        val row = store.day(dateIso) ?: return
        val span = row.toSealSpan() ?: return
        val signing = row.toSigningRecord()
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            ?: return
        val nowElapsed = nowElapsedMillis()
        for (event in StrongroomRouting.firingSchedule(signing, span)) {
            if (event.atElapsedMillis <= nowElapsed) continue
            val wallEstimate = row.sealedAtWallMillis + (event.atElapsedMillis - span.sealedAtElapsedMillis)
            val intent = Intent(appContext, StrongroomLiftReceiver::class.java).apply {
                action = StrongroomLiftReceiver.ACTION_FIRING
                putExtra(StrongroomLiftReceiver.EXTRA_DATE_ISO, dateIso)
                putExtra(StrongroomLiftReceiver.EXTRA_FIRING_KIND, event.kind.name)
            }
            val pending = PendingIntent.getBroadcast(
                appContext,
                requestCodeFor(event.kind),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            ExactAlarm.setWakeup(alarmManager, wallEstimate, pending)
        }
    }

    private fun requestCodeFor(kind: StrongroomRouting.FiringKind): Int = when (kind) {
        StrongroomRouting.FiringKind.BREAK_START -> 0x5301
        StrongroomRouting.FiringKind.BREAK_END -> 0x5302
        StrongroomRouting.FiringKind.LIFT -> 0x5303
    }

    // ------------------------------------------------------------------
    // The knock
    // ------------------------------------------------------------------

    /**
     * A reach for an app while a seal may stand. On KNOCK the
     * reach is punched onto the day and the decision returned —
     * the knock event and nothing else: this function starts no
     * activity, posts no notification, opens no surface.
     * The allowed set is read from the SEALED DAY's signed record
     * (B-9: exactly as signed — never a caller-supplied catalogue,
     * never a complement derived at read time).
     */
    suspend fun onLockedAppReach(
        context: Context,
        packageName: String
    ): StrongroomRouting.ReachDecision {
        val appContext = context.applicationContext
        val store = store(appContext)
        val row = store.sealedDay()
            ?: return StrongroomRouting.ReachDecision.NOT_SEALED
        val signing = row.toSigningRecord()
        val decision = StrongroomRouting.routeReach(
            sealed = true,
            packageName = packageName,
            lockedPackages = signing.lockedPackages,
            allowedPackages = signing.allowedPackages.toSet()
        )
        if (decision == StrongroomRouting.ReachDecision.KNOCK) {
            // THE KNOCK, felt through the one door: the sealed
            // face's only felt event (registry row = BUILD-SR-2's
            // StrongroomKnock verbatim). Fired here, at the
            // routing's own verdict — exactly once per reach.
            fireSensory(appContext, HapticEvent.KNOCK)
            val offset = (nowElapsedMillis() - row.sealedAtElapsedMillis).coerceAtLeast(0L)
            store.punch(
                row.filingDateIso,
                StrongroomDay.Punch(StrongroomDay.PunchKind.REACH, offset, packageName)
            )
        }
        return decision
    }

    // ------------------------------------------------------------------
    // Held calls
    // ------------------------------------------------------------------

    /** The held-call verdict for the notification listener's call
     *  path: contacts ring through; unknowns held quietly (D-5). */
    fun callDecision(isContact: Boolean): StrongroomRouting.CallDecision =
        StrongroomRouting.routeCall(
            if (isContact) StrongroomRouting.CallerClass.CONTACT
            else StrongroomRouting.CallerClass.UNKNOWN
        )

    // ------------------------------------------------------------------
    // Firing consumption + boot
    // ------------------------------------------------------------------

    /**
     * A firing hint woke the engine: re-derive from the span. Only
     * the LIFT acts here, and only when the elapsed span has truly
     * run in this boot — the plate is struck (once, by the store's
     * B-14 rule) and the shield is stood down. Break start/end are
     * consumed by the surfaces (an event they re-derive the same
     * way); a premature wake (wall moved) re-arms and does nothing.
     * Returns true when the lift was struck.
     */
    suspend fun onFiringWake(context: Context, dateIso: String, kindName: String): Boolean {
        val appContext = context.applicationContext
        val kind = runCatching { StrongroomRouting.FiringKind.valueOf(kindName) }.getOrNull()
            ?: return false
        if (kind != StrongroomRouting.FiringKind.LIFT) return false
        val store = store(appContext)
        val row = store.day(dateIso) ?: return false
        val span = row.toSealSpan() ?: return false
        val nowElapsed = nowElapsedMillis()
        val boot = bootCount(appContext)
        if (!span.isLifted(nowElapsed, boot)) {
            // Woken early (the wall moved, the hint lied): the span
            // is the truth — re-arm and stand down.
            armFirings(appContext, dateIso)
            return false
        }
        store.strikePlate(dateIso, nowElapsed, boot, sealFailed = false)
        // THE LIFTING, felt: bolts withdrawing + the door opening
        // on its own weight (FEEL-SR-1's canonical 1,197 ms array
        // verbatim in the registry; no terminal impact).
        fireSensory(appContext, HapticEvent.DOOR_LIFT)
        MonolithShieldService.stopShield(appContext)
        // The lifting takes the session's one card down with it.
        SessionNotification.cancel(appContext)
        return true
    }

    /**
     * Boot (and package-replace) re-derivation for the standing
     * seal, called from MonolithBootReceiver's guarded groups:
     *  - no sealed day: nothing.
     *  - AJAR (the boot died under the seal): strike the ajar
     *    plate — the door tells the truth about itself first — and
     *    RELEASE THE PHONE IMMEDIATELY (R2-3): a dead seal holds
     *    nothing; the failure face is testimony, never a gate, so
     *    the shield stands down in the same act.
     *  - BROKEN_TAMPER (a sideways kill observed): struck BROKEN
     *    and likewise released; tamper is never laundered into
     *    ajar, and a broken seal is not a lock either.
     *  - INTACT: re-raise the shield and re-arm the firings; the
     *    composition is NEVER recomposed here (B-10) — the stored
     *    day is re-shown as cut.
     */
    suspend fun onBoot(context: Context, sidewaysKillObserved: Boolean = false) {
        val appContext = context.applicationContext
        val store = store(appContext)
        val row = store.sealedDay() ?: return
        val span = row.toSealSpan() ?: return
        val boot = bootCount(appContext)
        when (StrongroomRouting.classifyIntegrity(span, boot, sidewaysKillObserved)) {
            StrongroomRouting.SealIntegrity.AJAR_BOOT_DEATH -> {
                store.strikePlate(row.filingDateIso, nowElapsedMillis(), boot, sealFailed = false)
                // A fallen day's strike is felt as such — lower,
                // damped (the registry's failure row).
                fireSensory(appContext, HapticEvent.PLATE_STRIKE_FAILURE)
                MonolithShieldService.stopShield(appContext)
                // Seal death: no session stands, no presence stands.
                SessionNotification.cancel(appContext)
            }
            StrongroomRouting.SealIntegrity.BROKEN_TAMPER -> {
                store.strikePlate(row.filingDateIso, nowElapsedMillis(), boot, sealFailed = true)
                fireSensory(appContext, HapticEvent.PLATE_STRIKE_FAILURE)
                MonolithShieldService.stopShield(appContext)
                // A broken seal is not a session either.
                SessionNotification.cancel(appContext)
            }
            StrongroomRouting.SealIntegrity.INTACT -> {
                MonolithShieldService.startShield(appContext)
                armFirings(appContext, row.filingDateIso)
                // The session still stands; the reboot took the
                // card with it, so re-post it — by show, not
                // beginSession: a dismissal he made before the
                // reboot is honoured, never resurrected past.
                SessionNotification.show(appContext)
            }
        }
    }

    /** True while any Strongroom day stands sealed (containment
     *  reads for the surfaces: the sealed set is lawful, the
     *  normal app is not — [StrongroomRouting.surfaceLawfulWhileSealed]). */
    suspend fun sealedDayStanding(context: Context): StrongroomDayEntity? =
        store(context).sealedDay()
}
