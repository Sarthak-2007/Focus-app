package com.example.service

import android.util.Log
import android.content.Context
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ============================================================================
 * THE WATCH STORE (chrome wave C3, spec §4) — every watch item the app
 * keeps, its value, and the instant it was last measured, in ONE store.
 *
 * The dissolved Guard screen read six permission booleans fresh on
 * every open and threw the readings away; the Deck's warrant read the
 * living services directly. Both are replaced by this store: every
 * reading lands here with its monotonic + wall stamps, and every
 * surface that prints a watch fact — the header Guard line, PARK's
 * board, the Deck's warrant — derives from this one snapshot.
 *
 * Writers (and only these):
 *  - service lifecycle hooks stamp the two BIND items (accessibility,
 *    notification listener) and the device-admin GRANT CHECK;
 *  - the shield loop publishes ONE snapshot per iteration: the engine
 *    heartbeat plus a re-read of the two BIND items' present state
 *    (and, since issue #81 F4, the USAGE ACCESS grant it measured);
 *  - the grant-check sweep (Deck entry, Deck resume, FIX return)
 *    dispatches the six permission queries in parallel at t=0; each
 *    item re-stamps only when its own query lands.
 *
 * STALE is derived at render from stamp age, never stored: a PASS
 * measurement older than [STALE_AFTER_MS] of monotonic time is stale.
 * For the BIND items this is the same fact said twice — was bound,
 * and is not confirmed bound now. A MISSING measurement never goes
 * stale: a grant that was absent stays absent until re-measured.
 *
 * The interceptor family (items 7–9) is DERIVED at every emission
 * from (setting, parent items, suspension) — never written directly,
 * and its value words never cross into the permission family.
 *
 * Persistence (issue #48, audit R1 seal-watchstore): exactly ONE
 * fact is durable — LAST PROVED, a wall-clock historical fact ("a
 * complete set was proved at T") that stays true forever once
 * landed. It is written to SharedPreferences the instant it
 * advances (forward only — it never regresses) and restored by
 * [bind] in the next process. The measurements themselves are
 * deliberately NOT persisted: every writer re-measures from the
 * living services (BIND hooks on connect, the shield loop every
 * iteration, the sweep on Deck entry/resume), so the store
 * converges to current truth within one pass — while a restored
 * measurement would print old proof as fresh, the exact lie this
 * store exists to prevent. Residency stays process-scoped by
 * definition: a new process is a new residency.
 * ============================================================================
 */

/** The wire verdict's states (moved here from the dead LiveWire). */
enum class WarrantState { LIVE, OFF, UNPROVEN }

/** Usage access as AppOps reports it: three-valued (moved from LiveWire). */
enum class UsageAccess { ALLOWED, DENIED, UNSET }

/**
 * The one wire verdict. Every field is a fact read from the living
 * services or derived from this store; the state logic lives in
 * [WatchStore.deriveWarrant] and nowhere else.
 */
data class Warrant(
    val state: WarrantState,
    val a11yBound: Boolean,
    val a11yStampedThisResidency: Boolean,
    val shieldProtecting: Boolean,
    val usage: UsageAccess,
    /** The read instant (Truth Car 3, UNA2-03): the monotonic time
     *  this verdict was gathered, stamped by [WatchStore.deriveWarrant]
     *  — the one construction site. 0 = no instant carried (a warrant
     *  built by hand: a test, a preview default); print derivations
     *  treat 0 as age-unknown, never as fresh proof. Before this
     *  field, the Deck's prints could ride a warrant of unbounded
     *  age and nothing on the glass could tell. */
    val readMono: Long = 0L,
    /** The residency this verdict was derived under (cold-start
     *  cure, issue #7): stamped by [WatchStore.deriveWarrant] from
     *  the store's current residencyStartMono — the one construction
     *  site — so a pure reader (the cured sentence table) can compute
     *  the residency's age from the warrant itself, with no live
     *  store access. 0 = no residency carried (a warrant built by
     *  hand: a test, a preview default), never a fresh residency. */
    val residencyStartMono: Long = 0L
)

/** The nine watch items, in board order (spec §4.1). The header's
 *  Guard line counts all six PERMISSIONS (issue #109, F4);
 *  [WatchItemId.COUNTED] survives as the original four the warrant
 *  story was built on, no longer the header's denominator. */
enum class WatchItemId(val boardName: String, val counted: Boolean) {
    ACCESSIBILITY("ACCESSIBILITY", true),
    USAGE_ACCESS("USAGE ACCESS", true),
    DEVICE_ADMIN("DEVICE ADMIN", true),
    NOTIFICATION_LISTENER("NOTIFICATION LISTENER", true),
    OVERLAY("OVERLAY", false),
    BATTERY("BATTERY", false),
    SHORTS_REELS("SHORTS + REELS", false),
    SOCIAL_SITES("SOCIAL SITES", false),
    NOTIFICATIONS("NOTIFICATIONS", false);

    companion object {
        /** The six permission-family items, board order.
         *  Issue #309 F5 (Audit R5, AppBlock second pass): the board
         *  used to LEAD with its scariest grants (ACCESSIBILITY,
         *  DEVICE ADMIN). The order is now cheapest-first — the
         *  read-only USAGE ACCESS ask leads, the invasive grants
         *  (ACCESSIBILITY, DEVICE ADMIN) close the board — so the
         *  first thing a new user is asked for is the least
         *  frightening one, and the "first missing" surfaces that
         *  read this list meet the cheap asks before the dear ones. */
        val PERMISSIONS: List<WatchItemId> = listOf(
            USAGE_ACCESS, OVERLAY, NOTIFICATION_LISTENER,
            ACCESSIBILITY, DEVICE_ADMIN, BATTERY
        )
        /** The counted four, board order (same relative order as
         *  [PERMISSIONS]). */
        val COUNTED: List<WatchItemId> = listOf(
            USAGE_ACCESS, NOTIFICATION_LISTENER, ACCESSIBILITY, DEVICE_ADMIN
        )
        /** The three derived interceptor items, board order. */
        val INTERCEPTORS: List<WatchItemId> = listOf(SHORTS_REELS, SOCIAL_SITES, NOTIFICATIONS)
    }
}

/** Permission-family value. The two families never cross (spec §4.2). */
enum class PermState { GRANTED, MISSING }

/** Interceptor-family value. */
enum class InterceptorState { RUNNING, PAUSED, OFF }

/** One measurement: a value and the instant it was taken, on both
 *  clocks. [sweepGen] ties permission measurements to the sweep that
 *  produced them; lifecycle/heartbeat stamps carry gen 0 and never
 *  complete a set. */
data class WatchMeasurement(
    val item: WatchItemId,
    val perm: PermState? = null,
    val interceptor: InterceptorState? = null,
    val measuredMono: Long,
    val measuredWall: Long,
    val sweepGen: Int = 0
)

/** The store's published face: the latest measurement per item, the
 *  engine heartbeat, and the LAST PROVED instant — the wall time of
 *  the latest COMPLETE permission set (all six from one sweep). It
 *  advances only when a complete set lands, never on a partial. */
data class WatchSnapshot(
    val items: Map<WatchItemId, WatchMeasurement>,
    val engineHeartbeatMono: Long,
    val engineHeartbeatWall: Long,
    val engineProtecting: Boolean,
    val lastProvedWallMillis: Long?,
    /** Emission generation (issue #48): bumped by [WatchStore.poke]
     *  so a poke is a REAL emission — StateFlow suppresses equal
     *  values, and a bare copy() of a data class is equal to the
     *  value it copies, so the old poke could never notify a
     *  collector. */
    val pokeGen: Long = 0L,
    /** Issue #122: the latest battery snapshot (level / saver /
     *  charging / exemption), published by the BATTERY sweep
     *  query and by the shield loop. PARK's BATTERY line prints
     *  THIS, not the exemption bit alone. Null = never read —
     *  the line then falls back to the perm print, as before. */
    val battery: com.example.data.BatterySnapshot? = null
) {
    fun measurement(item: WatchItemId): WatchMeasurement? = items[item]

    companion object {
        val EMPTY = WatchSnapshot(emptyMap(), 0L, 0L, false, null)
    }
}

/** What a permission line prints at a given instant (spec §4.2):
 *  never-measured, the measured value, or its stale derivative.
 *  UNPROVED (issue #94): a measurement exists and reads granted, but
 *  a fact the app cannot measure stands behind it — the BATTERY line
 *  on an OEM-autostart-gated skin, where the AOSP exemption is only
 *  half the survival fact and the autostart half is unreadable. */
enum class PermPrint { UNMEASURED, GRANTED, MISSING, STALE, MISSING_STALE, UNPROVED }

object WatchStore {

    /**
     * Issue #280 (R5 supplement): the one-line rationale the PARK
     * board prints under the FIX key for the grant it targets —
     * what the grant does, and what it never does — BEFORE the
     * Settings trip fires. The original explains each grant before
     * asking; Monolith's board fired a bare FIX key. Pure and
     * total over the six PERMISSIONS; interceptor items and
     * unknown items carry no rationale (null).
     */
    fun boardRationale(item: WatchItemId): String? = when (item) {
        WatchItemId.ACCESSIBILITY ->
            "ACCESSIBILITY — brings you back when a blocked app opens. It never reads what is on your screen."
        WatchItemId.USAGE_ACCESS ->
            "USAGE ACCESS — tells Monolith which app is in front, so the seal knows what to hold. It never leaves this phone."
        WatchItemId.DEVICE_ADMIN ->
            "DEVICE ADMIN — adds friction if the seal is switched off mid-session. It cannot stop you uninstalling, ever."
        WatchItemId.NOTIFICATION_LISTENER ->
            "NOTIFICATIONS ACCESS — holds a blocked app's notifications during a session and shows them to you after. Calls and alarms are never held."
        WatchItemId.OVERLAY ->
            "OVERLAY — lets the block surface stand over a blocked app. It draws nothing outside a session."
        WatchItemId.BATTERY ->
            "BATTERY EXEMPTION — keeps Android from sleeping the shield mid-session. Nothing else changes."
        else -> null
    }

    /** A PASS measurement older than this (monotonic) is stale. */
    const val STALE_AFTER_MS: Long = 45_000L

    /** Cold-start grace (issue #7): how long a fresh residency may
     *  rest UNPROVEN while the accessibility pipeline is actively
     *  probed before the Deck's sentence hardens. The stamp races
     *  residency start on a cold open — the service may bind after
     *  the Deck is already showing, and a quiet Deck generates no
     *  a11y events to stamp it — so the warrant's readers give the
     *  active self-probe this window to land its proof. */
    const val RESIDENCY_GRACE_MS: Long = 4_000L

    // Issue #48: LAST PROVED's durable home — a tiny wall-clock
    // fact needs no table (the seal marker's SharedPreferences
    // precedent, MonolithRepository SEAL_PREFS).
    private const val PREFS_NAME = "monolith_watch"
    private const val KEY_LAST_PROVED_WALL = "last_proved_wall_ms"

    @Volatile private var boundContext: Context? = null

    /**
     * Bind the store to this process (idempotent): restores the
     * persisted LAST PROVED, so a fresh process opens with the
     * historical fact intact instead of "never proved". Called by
     * the store's own writers' homes — the two BIND services, the
     * shield service — and by MainActivity.
     */
    fun bind(context: Context) {
        val app = context.applicationContext
        if (boundContext === app) return
        boundContext = app
        val persisted = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_LAST_PROVED_WALL, 0L)
        if (persisted > 0L) {
            publish { current ->
                if ((current.lastProvedWallMillis ?: 0L) < persisted) {
                    current.copy(lastProvedWallMillis = persisted)
                } else {
                    current
                }
            }
        }
    }

    private fun persistLastProved(wallMillis: Long) {
        val app = boundContext ?: return
        // commit(), not apply() (verifier pass, issue #48): the
        // fact must be on disk when this returns — the seal
        // marker's own standard. Advances are rare (one per
        // completed sweep), so the synchronous write is cheap.
        app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LAST_PROVED_WALL, wallMillis)
            .commit()
    }

    private val _snapshots = MutableStateFlow(WatchSnapshot.EMPTY)
    val snapshots: StateFlow<WatchSnapshot> = _snapshots.asStateFlow()

    private val sweepScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var sweepGen = 0

    /** The in-flight sweep's dispatch instant, kept for the
     *  coexistence window (Critic C-5): a completing set must have
     *  landed within [STALE_AFTER_MS] of dispatch. It is NOT the
     *  completion test — see [pendingSweepGen]. */
    @Volatile private var pendingSweepStartWall: Long = 0L

    /** The in-flight sweep's generation (issue #9, Zahn Z-1). THE
     *  completion test reads THIS, on the stamps themselves: a
     *  permission item counts toward the completing set only when
     *  its measurement's [WatchMeasurement.sweepGen] equals this
     *  generation — i.e. the sweep's own query produced it.
     *  Lifecycle/heartbeat stamps carry gen 0 and earlier sweeps'
     *  late landings carry their own generation, so neither can
     *  complete a set they never served. The wall-clock era test
     *  this replaces let exactly those stamps "complete" a press's
     *  proof and then badged the set with the press's generation. */
    @Volatile private var pendingSweepGen: Int = 0

    // Interceptor inputs: the settings and the suspension, pushed by
    // the ViewModel (the settings live in guard_settings; the
    // suspension is the session's Safe Break). The notification
    // capture has no setting — it is on by design — but it has one
    // hard precondition the board used to ignore (issue #109, F1):
    // the listener captures ONLY while a session is active
    // (MonolithNotificationListener returns early otherwise), so a
    // bound listener at rest holds nothing. [sessionCapturing] is
    // that precondition, pushed from the same active-session flow
    // that feeds the Safe Break input. NOTIFICATIONS may print
    // RUNNING only when it is true; its PAUSED stays unreachable —
    // the listener does not suspend during a Safe Break.
    @Volatile private var shortsSettingOn = true
    @Volatile private var sitesSettingOn = true
    @Volatile private var safeBreakActive = false
    @Volatile private var sessionCapturing = false

    // The wire's residency machinery (moved whole from LiveWire):
    // a Deck residency starts when the Deck is entered; the wire can
    // read LIVE only when the accessibility pipeline has stamped an
    // event at or after that instant — proof it processed THIS visit.
    @Volatile private var residencyStartMono: Long = 0L

    /** Whether a Deck residency is in force in this process. */
    fun residencyActive(): Boolean = residencyStartMono > 0L

    fun noteResidencyStart() {
        residencyStartMono = SystemClock.elapsedRealtime()
        poke()
        // Cold-start cure (issue #7, active self-probe): the stamp
        // races residency start — on a cold open the service may
        // not be bound yet when the Deck appears, and a quiet Deck
        // generates no a11y events, so waiting for the event path
        // to stamp lastRootMono can leave the warrant UNPROVEN
        // forever. Probe the pipeline NOW instead of waiting for
        // an event that may never come.
        MonolithAccessibilityService.instance?.requestPipelineProbe()
        // One delayed re-probe at +1500 ms: the first probe can
        // still lose the race when the service binds just after
        // residency start. It fires only if this same residency is
        // still unstamped — a stamp that already landed (event path
        // or first probe) needs no second proof, and a superseded
        // residency (start changed since scheduling) is never
        // probed on its predecessor's schedule.
        val scheduledStartMono = residencyStartMono
        sweepScope.launch {
            delay(1_500L)
            if (residencyStartMono == scheduledStartMono && !stampedThisResidency()) {
                MonolithAccessibilityService.instance?.requestPipelineProbe()
            }
        }
    }

    /** The stamp test, exactly as the wire has always run it. */
    fun stampedThisResidency(): Boolean =
        residencyStartMono > 0L &&
            MonolithAccessibilityService.lastRootMono >= residencyStartMono

    /** A nudge with no data: forces a snapshot re-emission so derived
     *  readers (the Deck warrant) re-read the living facts. Routed
     *  through [publish] (issue #48): the generation bump makes the
     *  emission real — StateFlow suppresses equal values, and the
     *  old bare copy() was equal by construction, so collectors were
     *  never notified — and the @Synchronized publish lock ends the
     *  old poke's second sin: it wrote the flow directly, outside
     *  the lock, where it could clobber a concurrent publication. */
    fun poke() {
        publish { current -> current.copy(pokeGen = current.pokeGen + 1) }
    }

    // ------------------------------------------------------------------
    // Writers
    // ------------------------------------------------------------------

    /** Accessibility BIND hook (service onServiceConnected/onUnbind/onDestroy). */
    fun onAccessibilityBound(bound: Boolean) =
        stampPermission(WatchItemId.ACCESSIBILITY, bound)

    /** Notification-listener BIND hook (onListenerConnected/Disconnected). */
    fun onNotificationListenerBound(bound: Boolean) =
        stampPermission(WatchItemId.NOTIFICATION_LISTENER, bound)

    /** Device-admin GRANT CHECK hook (receiver onEnabled/onDisabled). */
    fun onDeviceAdminChanged(active: Boolean) =
        stampPermission(WatchItemId.DEVICE_ADMIN, active)

    /**
     * The shield loop's per-iteration publication: the engine
     * heartbeat (behind the strip, never a board line) plus a
     * re-read of the two BIND items' present state, stamped as
     * measurements of this instant.
     *
     * Issue #81 F4: the loop also hands over the USAGE ACCESS fact
     * it measured this iteration ([usageGranted], null = not
     * measured — the stop/destroy publications, which must not
     * fabricate a reading while dying). Before this, USAGE_ACCESS
     * was re-measured only by the Deck-triggered sweep, so a
     * mid-session revocation stayed invisible to every surface
     * while the shield kept beating; now the board re-reads it on
     * the shield's own 2s cadence for as long as a session runs.
     */
    fun onShieldIteration(protecting: Boolean, usageGranted: Boolean? = null) {
        val mono = SystemClock.elapsedRealtime()
        val wall = System.currentTimeMillis()
        val a11y = MonolithAccessibilityService.instance != null
        val listener = MonolithNotificationListener.instance != null
        publish { current ->
            var next = current.copy(
                engineHeartbeatMono = mono,
                engineHeartbeatWall = wall,
                engineProtecting = protecting
            )
            next = next.withMeasurement(
                WatchMeasurement(
                    WatchItemId.ACCESSIBILITY,
                    perm = if (a11y) PermState.GRANTED else PermState.MISSING,
                    measuredMono = mono, measuredWall = wall
                )
            )
            next = next.withMeasurement(
                WatchMeasurement(
                    WatchItemId.NOTIFICATION_LISTENER,
                    perm = if (listener) PermState.GRANTED else PermState.MISSING,
                    measuredMono = mono, measuredWall = wall
                )
            )
            if (usageGranted != null) {
                next = next.withMeasurement(
                    WatchMeasurement(
                        WatchItemId.USAGE_ACCESS,
                        perm = if (usageGranted) PermState.GRANTED else PermState.MISSING,
                        measuredMono = mono, measuredWall = wall
                    )
                )
            }
            next
        }
    }

    /** Issue #122: publish the latest battery snapshot. The perm
     *  measurement for BATTERY stays the exemption bit (that is
     *  the FIX door's business); this carries the battery itself
     *  for PARK's line. */
    fun publishBattery(snapshot: com.example.data.BatterySnapshot) {
        publish { current -> current.copy(battery = snapshot) }
    }

    /**
     * The grant-check sweep: six permission queries dispatched in
     * parallel at t=0; each item re-stamps when its own query lands
     * (no fixed stagger — a fast grant never waits for a slow one).
     * When all six carry this sweep's generation, LAST PROVED
     * advances to the set's completion instant.
     */
    fun runSweep(query: (WatchItemId) -> Boolean) {
        // Issue #77: sweepGen and pendingSweepStartWall are read and
        // cleared by publish() under its @Synchronized monitor; they
        // were written here OUTSIDE that monitor, so overlapping
        // sweeps (Deck entry + ON_RESUME) could mix generations and
        // complete each other's sets with stale stamps. The dispatch
        // state now takes the same monitor publish() holds.
        val gen: Int
        synchronized(this) {
            gen = ++sweepGen
            pendingSweepGen = gen
            pendingSweepStartWall = System.currentTimeMillis()
        }
        for (item in WatchItemId.PERMISSIONS) {
            sweepScope.launch {
                // Truth (issue #76): a query that THREW is not a
                // measurement. The old getOrDefault(false) stamped a
                // fresh MISSING with now-timestamps, so a sweep whose
                // every query failed still completed the set and
                // advanced LAST PROVED — a fabricated proof. A thrown
                // query now writes NO stamp: the item keeps its older
                // measurement and ages to STALE honestly.
                runCatching { query(item) }
                    .onSuccess { granted -> stampPermission(item, granted, gen) }
                    .onFailure {
                        Log.w("WatchStore", "sweep query threw for $item; no stamp written", it)
                    }
            }
        }
    }

    /** Interceptor inputs, pushed by the ViewModel. A change is a
     *  derivation trigger: the snapshot re-emits with fresh derived
     *  interceptor measurements. */
    fun updateInterceptorInputs(
        shortsOn: Boolean? = null,
        sitesOn: Boolean? = null,
        safeBreak: Boolean? = null,
        sessionActive: Boolean? = null
    ) {
        var changed = false
        if (shortsOn != null && shortsOn != shortsSettingOn) { shortsSettingOn = shortsOn; changed = true }
        if (sitesOn != null && sitesOn != sitesSettingOn) { sitesSettingOn = sitesOn; changed = true }
        if (safeBreak != null && safeBreak != safeBreakActive) { safeBreakActive = safeBreak; changed = true }
        if (sessionActive != null && sessionActive != sessionCapturing) { sessionCapturing = sessionActive; changed = true }
        if (changed) publish { it }
    }

    // ------------------------------------------------------------------
    // Derivation
    // ------------------------------------------------------------------

    /**
     * THE warrant derivation — the only place the verdict's state
     * logic exists (the repository's liveWarrant() is deleted; it
     * read the same three inputs and ran this same table). Pure:
     * same inputs, same verdict, always.
     *
     * Conjuncts: accessibility bound AND stamped from this Deck
     * residency AND usage ALLOWED. Shield-protecting rides along as
     * a fact for the cascade and the strip but is NOT a conjunct —
     * the shield starts with the session, so requiring it first
     * would be circular.
     */
    fun deriveWarrant(
        a11yBound: Boolean,
        stamped: Boolean,
        usage: UsageAccess,
        shieldProtecting: Boolean,
        readMono: Long = SystemClock.elapsedRealtime()
    ): Warrant {
        val state = when {
            !a11yBound -> WarrantState.OFF
            usage == UsageAccess.DENIED -> WarrantState.OFF
            !stamped -> WarrantState.UNPROVEN
            usage != UsageAccess.ALLOWED -> WarrantState.UNPROVEN
            else -> WarrantState.LIVE
        }
        return Warrant(
            state = state,
            a11yBound = a11yBound,
            a11yStampedThisResidency = stamped,
            shieldProtecting = shieldProtecting,
            usage = usage,
            readMono = readMono,
            // Cold-start cure (issue #7): the warrant carries the
            // residency it was derived under, so its age is readable
            // from the warrant itself.
            residencyStartMono = residencyStartMono
        )
    }

    /** The warrant's age at [nowMono], or null when no instant was
     *  carried (readMono 0) or the caller hands no clock (nowMono 0).
     *  Age-unknown is not age-zero: it degrades nothing and proves
     *  nothing. */
    fun warrantAgeMs(warrant: Warrant, nowMono: Long): Long? {
        if (warrant.readMono <= 0L || nowMono <= 0L) return null
        return (nowMono - warrant.readMono).coerceAtLeast(0L)
    }

    /** Stale for print (UNA2-03): the read is older than the board's
     *  own threshold ([STALE_AFTER_MS], the PARK derivation). The
     *  Deck's prints degrade on this exactly as the board's do. */
    fun warrantStaleForPrint(warrant: Warrant, nowMono: Long): Boolean =
        warrantAgeMs(warrant, nowMono)?.let { it > STALE_AFTER_MS } ?: false

    /** The print-effective state at [nowMono]: a stale LIVE reads as
     *  UNPROVEN — the wire may still be live, but this READ no longer
     *  proves it, and a future-tense promise (the imprint's WILL
     *  SHUT) may only ride a proof. Other states print as they are:
     *  OFF rests on a directly observed dead signal and UNPROVEN
     *  already claims nothing. Null in, null out. */
    fun printState(warrant: Warrant?, nowMono: Long): WarrantState? {
        if (warrant == null) return null
        return if (warrant.state == WarrantState.LIVE &&
            warrantStaleForPrint(warrant, nowMono)
        ) {
            WarrantState.UNPROVEN
        } else {
            warrant.state
        }
    }

    /** The permission print at [nowMono]: staleness derived here,
     *  never stored. ANY measurement older than [STALE_AFTER_MS]
     *  is stale — a PASS prints STALE and a denial prints
     *  MISSING_STALE (issue #58 F1). The old asymmetry — GRANTED
     *  expired after 45s while a MISSING measured hours ago printed
     *  bare and current forever — presented an ancient denial as
     *  current fact on the Guard line. MISSING_STALE stays a
     *  distinct print, not a collapse into STALE, so consumers
     *  that act on a lack (PARK's FIX key) still know the item is
     *  missing, not merely old. */
    fun permPrintAt(snapshot: WatchSnapshot, item: WatchItemId, nowMono: Long): PermPrint {
        val m = snapshot.items[item] ?: return PermPrint.UNMEASURED
        val perm = m.perm ?: return PermPrint.UNMEASURED
        val stale = nowMono - m.measuredMono > STALE_AFTER_MS
        return when {
            // Issue #94 (F1): on a skin whose OEM autostart toggle gates
            // receiver restarts, a granted AOSP battery read never
            // prints GRANTED — the toggle cannot be read back, so the
            // honest word is UNPROVED. A missing AOSP read stays
            // MISSING below: that half is known, not proved-absent.
            item == WatchItemId.BATTERY && perm == PermState.GRANTED &&
                !com.example.data.OemPolicy.batteryGrantedIsProved(
                    com.example.data.OemPolicy.currentGate()
                ) -> PermPrint.UNPROVED
            perm == PermState.GRANTED && stale -> PermPrint.STALE
            perm == PermState.GRANTED -> PermPrint.GRANTED
            stale -> PermPrint.MISSING_STALE
            else -> PermPrint.MISSING
        }
    }

    /** "Currently warranted GRANTED" for the Guard line's count:
     *  GRANTED and not stale, over ALL SIX permission lines the
     *  board prints (issue #109, F4 — the header used to count only
     *  the four COUNTED and could print wholeness over a board
     *  showing OVERLAY / BATTERY MISSING).
     *
     *  GATED on measurement (issue #9, Zahn Z-2): if ANY of the six
     *  is UNMEASURED the count does not exist and this returns
     *  null — a count computed off a partial set is a fabrication
     *  (a cold open's `GUARD 0/6` on an EMPTY snapshot reads as
     *  "all six failed"; the truth is "nothing checked yet").
     *  Callers print their NOT CHECKED form on null, never a
     *  number. */
    fun countedWarranted(snapshot: WatchSnapshot, nowMono: Long): Int? {
        val prints = WatchItemId.PERMISSIONS.map { permPrintAt(snapshot, it, nowMono) }
        if (prints.any { it == PermPrint.UNMEASURED }) return null
        return prints.count { it == PermPrint.GRANTED }
    }

    // ------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------

    private fun stampPermission(item: WatchItemId, granted: Boolean, gen: Int = 0) {
        val mono = SystemClock.elapsedRealtime()
        val wall = System.currentTimeMillis()
        publish { current ->
            current.withMeasurement(
                WatchMeasurement(
                    item = item,
                    perm = if (granted) PermState.GRANTED else PermState.MISSING,
                    measuredMono = mono,
                    measuredWall = wall,
                    sweepGen = gen
                )
            )
        }
    }

    /** Every publication re-derives the interceptor family and
     *  re-checks the complete-set rule for LAST PROVED.
     *  Serialized: the sweep's six queries land truly in parallel,
     *  and an unsynchronized read-modify-write here loses stamps
     *  (two publishers transform the same base; the last writer
     *  wins and the other's item vanishes — caught by
     *  WatchStoreRobolectricTest's sweep test, Oct 29). */
    @Synchronized
    private fun publish(transform: (WatchSnapshot) -> WatchSnapshot) {
        val base = transform(_snapshots.value)
        val mono = SystemClock.elapsedRealtime()
        val wall = System.currentTimeMillis()
        var next = base
        for (item in WatchItemId.INTERCEPTORS) {
            val derived = deriveInterceptor(item, base, mono)
            next = if (derived != null) {
                next.withMeasurement(
                    WatchMeasurement(
                        item, interceptor = derived,
                        measuredMono = mono, measuredWall = wall
                    )
                )
            } else {
                next.withoutItem(item)
            }
        }
        // LAST PROVED: when every permission item carries a
        // measurement stamped by the in-flight sweep ITSELF —
        // sweepGen == the pending generation, tested on the stamp,
        // not inferred from wall-clock era (issue #9, Zahn Z-1:
        // the era test let gen-0 shield-loop re-stamps, presence
        // alone every ~2s, and earlier sweeps' late landings
        // "complete" a set they never served). A partial set never
        // advances it. The set must also COEXIST (Critic C-5): a
        // set whose span from dispatch exceeds STALE_AFTER_MS is
        // not a set — past the window the sweep is abandoned; its
        // late stamps remain ordinary measurements and complete
        // nothing.
        val sweepStart = pendingSweepStartWall
        if (sweepStart > 0L) {
            val perms = WatchItemId.PERMISSIONS.mapNotNull { next.items[it] }
            val completedAt = if (perms.size == WatchItemId.PERMISSIONS.size &&
                perms.all { it.sweepGen == pendingSweepGen }
            ) {
                perms.maxOf { it.measuredWall }
            } else {
                null
            }
            if (completedAt != null && completedAt - sweepStart <= STALE_AFTER_MS) {
                // Forward only (issue #48): LAST PROVED is a
                // historical fact — a late-completing set from an
                // older era never regresses it. On a real advance
                // the fact is persisted in the same breath, so it
                // survives the process that proved it.
                if (completedAt > (next.lastProvedWallMillis ?: 0L)) {
                    next = next.copy(lastProvedWallMillis = completedAt)
                    persistLastProved(completedAt)
                }
                pendingSweepStartWall = 0L
                pendingSweepGen = 0
            } else if (wall - sweepStart > STALE_AFTER_MS) {
                // Abandoned: the sweep never answered as a set.
                // Its stamps stay as ordinary measurements; the
                // pending generation is cleared so a late landing
                // can never complete it after the fact.
                pendingSweepStartWall = 0L
                pendingSweepGen = 0
            }
        }
        _snapshots.value = next
    }

    /**
     * The interceptor derivation (§4.3), computed at every emission:
     *  RUNNING — setting on, every parent currently-warranted;
     *  PAUSED  — retained in the vocabulary for a suspension that is
     *            ever REAL (measured at the enforcement door); no
     *            current input derives it. The Safe Break is NOT a
     *            suspension of either blocking interceptor — the
     *            sniper and the site blocker are documented, in the
     *            accessibility service's own class doc, as strictly
     *            active during Safe Breaks, and Audit R3
     *            (safe-break-lived #1) caught this derivation
     *            printing PAUSED for them while they intercepted;
     *  OFF     — setting off, or any parent MISSING or STALE.
     * A parent never measured makes the line unmeasured (it prints
     * its name only) — OFF would be a claim nobody measured.
     * Parents: SHORTS + REELS rides the accessibility binding (the
     * sniper is the a11y pipeline); SOCIAL SITES rides accessibility
     * + usage access (the browser watch reads the foreground through
     * both); NOTIFICATIONS rides the listener binding AND an active
     * capturing session — the listener holds nothing without one
     * (issue #109, F1).
     */
    private fun deriveInterceptor(
        item: WatchItemId,
        snapshot: WatchSnapshot,
        nowMono: Long
    ): InterceptorState? {
        val (settingOn, parents) = when (item) {
            WatchItemId.SHORTS_REELS ->
                shortsSettingOn to listOf(WatchItemId.ACCESSIBILITY)
            WatchItemId.SOCIAL_SITES ->
                sitesSettingOn to listOf(WatchItemId.ACCESSIBILITY, WatchItemId.USAGE_ACCESS)
            WatchItemId.NOTIFICATIONS ->
                true to listOf(WatchItemId.NOTIFICATION_LISTENER)
            else -> return null
        }
        if (!settingOn) return InterceptorState.OFF
        val parentPrints = parents.map { permPrintAt(snapshot, it, nowMono) }
        if (parentPrints.any { it == PermPrint.UNMEASURED }) return null
        if (parentPrints.any { it != PermPrint.GRANTED }) return InterceptorState.OFF
        // Issue #109, F1: the listener binding alone proves nothing
        // is being held — the listener captures only inside an
        // active session. Bound but no session capturing: the
        // capture is OFF, and printing RUNNING here was the false
        // print Sarthak caught on his own phone.
        if (item == WatchItemId.NOTIFICATIONS && !sessionCapturing) {
            return InterceptorState.OFF
        }
        // Audit R3 (safe-break-lived #1) — the suspension term stays
        // DELETED even though the safeBreak input survives above (the
        // ViewModel pushes it; nothing truthful derives from it): the
        // accessibility service keeps intercepting SHORTS + REELS and
        // SOCIAL SITES through a Safe Break, exactly as its class doc
        // promises, so PAUSED from safeBreakActive was a false warrant.
        // RUNNING is the truth in and out of a break alike.
        return InterceptorState.RUNNING
    }

    private fun WatchSnapshot.withMeasurement(m: WatchMeasurement): WatchSnapshot =
        copy(items = items + (m.item to m))

    private fun WatchSnapshot.withoutItem(item: WatchItemId): WatchSnapshot =
        if (item in items) copy(items = items - item) else this

    /** Test seam: the store is a process singleton; unit tests reset
     *  it between cases. Production code never calls this. */
    fun resetForTest() {
        _snapshots.value = WatchSnapshot.EMPTY
        boundContext = null
        sweepGen = 0
        pendingSweepStartWall = 0L
        pendingSweepGen = 0
        shortsSettingOn = true
        sitesSettingOn = true
        safeBreakActive = false
        sessionCapturing = false
        residencyStartMono = 0L
    }
}
