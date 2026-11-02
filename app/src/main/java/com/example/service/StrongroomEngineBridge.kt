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
import com.example.service.haptics.SensoryBus
import com.example.service.haptics.StrongroomSoundAdapter

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
     * THE ONE SENSORY BUS (composition — BUILD-SR-23, step 6;
     * the one-bus union both lanes lettered): FEEL-SR-5's
     * [SensoryBus] is the single event table and the only public
     * door (R8-4). This bridge holds ONE standing bus for the
     * process, built once through BUILD-SR-20's factory form with
     * BUILD-SR-19's [StrongroomSoundAdapter] installed as its
     * sound player — the adapter resolves the bus's sound leg
     * through SOUND-SR-5's voice table behind it (the twin law:
     * the voice table's own haptic half is disabled there, so a
     * row fires felt + heard exactly once). Every ceremony below
     * fires the UNION of both lanes' call sites ONCE per event —
     * never two buses, never two fires. A sensory failure never
     * fails the ceremony: the span is the truth, the felt/heard
     * event is its testimony (L-5 — never replayed, never a
     * substitute buzz). All felt/heard figures UNPROVEN-ON-DEVICE.
     */
    @Volatile
    private var sensoryBus: SensoryBus? = null

    private fun sensory(context: Context): SensoryBus? {
        sensoryBus?.let { return it }
        return synchronized(this) {
            sensoryBus ?: try {
                val appContext = context.applicationContext
                AndroidWaveformPlayer.bus(
                    appContext,
                    soundPlayer = StrongroomSoundAdapter.production(appContext)
                ).also { sensoryBus = it }
            } catch (_: Exception) {
                null
            }
        }
    }

    /** Fire one event through the one bus — felt + heard from
     *  the one registry row, in the bus's order. Never throws. */
    private fun fireSensory(context: Context, event: HapticEvent) {
        try {
            sensory(context)?.fire(event)
        } catch (_: Exception) {
            // Silence for this firing; the ceremony stands.
        }
    }

    /**
     * THE SURFACES' DOOR (SOUND seat, 2026-11-03): a surface
     * fires its own event — a detent crossed, a seat taken, a
     * drum landed, a drawer opened — through this same one bus,
     * so felt + heard still come from the one registry row and
     * no surface ever builds a second bus or touches the voice
     * table directly. The ceremonies above keep their private
     * path; this door is the same fire, opened to the rooms the
     * hand actually works in. Never throws.
     */
    fun fireSurfaceEvent(context: Context, event: HapticEvent) =
        fireSensory(context, event)

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

    /**
     * THE HELD ARRIVAL, RECORDED (BUILD-SR-28, the W-6 cure).
     * Before this cure [callDecision] was a verdict with no
     * production caller and no home: the routing held an unknown
     * call "quietly" into nothing, and the antechamber's pile —
     * built from no store — was empty by construction. Now the
     * listener's Strongroom path calls here at the arrival:
     * a CONTACT rings through and records nothing; an UNKNOWN
     * under a standing seal is written to the day's held record
     * (strongroom_held_arrivals) and struck as a HELD punch at
     * its offset, so the plate carries the held hole (B-13/A-10)
     * and the pile renders the real arrival. No seal stands →
     * nothing is held and nothing is written: the phone's own
     * lists own the call, exactly as on an unsealed phone.
     * Returns true when the arrival was recorded as held.
     */
    suspend fun recordHeldCallIfUnknown(
        context: Context,
        number: String,
        isContact: Boolean,
        arrivedAtWallMillis: Long
    ): Boolean {
        if (callDecision(isContact) != StrongroomRouting.CallDecision.HELD_QUIETLY) {
            return false
        }
        val store = store(context.applicationContext)
        val row = store.sealedDay() ?: return false
        store.recordHeldArrival(
            dateIso = row.filingDateIso,
            kind = com.example.data.StrongroomHeldArrivalEntity.KIND_CALL,
            line = number,
            arrivedAtWallMillis = arrivedAtWallMillis
        )
        val offset = (nowElapsedMillis() - row.sealedAtElapsedMillis).coerceAtLeast(0L)
        store.punch(
            row.filingDateIso,
            StrongroomDay.Punch(StrongroomDay.PunchKind.HELD, offset, "")
        )
        return true
    }

    /**
     * A held CODE, recorded (BUILD-SR-28, W-6's other half): the
     * digits alone, into the same held record, under the same
     * standing-seal condition. The message the code arrived in
     * is never read into any store (Issue #100's cut); [digits]
     * is extracted by the caller from the notification it held,
     * and this record is the digits' only home inside the seal
     * (B-18 / UX-SR-3 X-3). Struck as a HELD punch, like a call.
     */
    suspend fun recordHeldCode(
        context: Context,
        digits: String,
        arrivedAtWallMillis: Long
    ): Boolean {
        if (digits.isBlank()) return false
        val store = store(context.applicationContext)
        val row = store.sealedDay() ?: return false
        store.recordHeldArrival(
            dateIso = row.filingDateIso,
            kind = com.example.data.StrongroomHeldArrivalEntity.KIND_CODE,
            line = digits,
            arrivedAtWallMillis = arrivedAtWallMillis
        )
        val offset = (nowElapsedMillis() - row.sealedAtElapsedMillis).coerceAtLeast(0L)
        store.punch(
            row.filingDateIso,
            StrongroomDay.Punch(StrongroomDay.PunchKind.HELD, offset, "")
        )
        return true
    }

    /** The digits of a held code, from a notification's own
     *  words: the first run of 4–8 digits standing alone. Pure,
     *  so the extraction is unit-tested without a Notification.
     *  A chat full of numbers is not a code — the caller gates
     *  this on the code-word sniff (NotificationCapturePolicy
     *  .looksLikeOneTimeCode), never on digits alone. */
    fun extractHeldCodeDigits(text: String): String? =
        Regex("""(?<!\d)(\d{4,8})(?!\d)""").find(text)?.groupValues?.get(1)

    /** The number a call notification names: the first digit run
     *  (with a leading +) long enough to be a number, from the
     *  notification's title/text. Pure, unit-tested. A contact's
     *  call notification names the contact — no such run stands
     *  in it — so a null here already leans contact; the caller
     *  still confirms with the phone's own contact lookup when
     *  it may. */
    fun extractCallerNumber(text: String): String? =
        Regex("""(\+?\d[\d\s-]{6,}\d)""").find(text)?.groupValues?.get(1)
            ?.replace(Regex("""[\s-]"""), "")
            ?.takeIf { it.removePrefix("+").length in 7..15 }

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
        val store = store(appContext)
        val row = store.day(dateIso) ?: return false
        val span = row.toSealSpan() ?: return false
        val nowElapsed = nowElapsedMillis()
        val boot = bootCount(appContext)
        if (kind != StrongroomRouting.FiringKind.LIFT) {
            // The break's firings, consumed here for their senses
            // (BUILD-SR-19, carried into the union by BUILD-SR-23):
            // the span is re-derived through firingDue
            // (boot-guarded, CH-2 — no firing sounds in an ajar
            // day), and only the firing actually due speaks. The
            // break's START is a NAMED_SILENCE in the voice table:
            // the bus fires its felt easing and the adapter
            // records the silence. Its END has its own voice. The
            // surfaces still re-derive the break's state
            // themselves, exactly as before.
            val signing = row.toSigningRecord()
            val due = StrongroomRouting.firingDue(signing, span, nowElapsed, boot)
            if (due?.kind == kind) {
                when (kind) {
                    StrongroomRouting.FiringKind.BREAK_START ->
                        fireSensory(appContext, HapticEvent.BREAK_ARRIVAL)
                    StrongroomRouting.FiringKind.BREAK_END ->
                        fireSensory(appContext, HapticEvent.BREAK_END_CUE)
                    StrongroomRouting.FiringKind.LIFT -> Unit
                }
            }
            return false
        }
        if (!span.isLifted(nowElapsed, boot)) {
            // Woken early (the wall moved, the hint lied): the span
            // is the truth — re-arm and stand down.
            armFirings(appContext, dateIso)
            return false
        }
        store.strikePlate(dateIso, nowElapsed, boot, sealFailed = false)
        // THE LIFTING, felt + heard (the union, BUILD-SR-23):
        // bolts withdrawing + the door opening on its own weight
        // (FEEL-SR-1's canonical 1,197 ms array verbatim in the
        // registry; no terminal impact), then the plate strike —
        // SOUND-SR-1's mirror set behind the adapter. Each event
        // fires ONCE through the one bus.
        fireSensory(appContext, HapticEvent.DOOR_LIFT)
        fireSensory(appContext, HapticEvent.PLATE_STRIKE)
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
