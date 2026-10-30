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
 *    heartbeat plus a re-read of the two BIND items' present state;
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
    val residencyStartMono: Long = 0L,
    /** The grace anchor (FIX-1 Addendum 3, bind-epoch law, DAY-1
     *  D-2): stamped by [WatchStore.deriveWarrant] — the one
     *  construction site — as the store's graceDebtStartMono when
     *  a bind-epoch grace debt is running, else residencyStartMono.
     *  Grace age is measured from THIS instant, never from the
     *  current residency alone: the debt is inherited across
     *  consecutive unstamped residencies of the same bind epoch,
     *  so Deck re-entry cannot mint fresh silence. 0 = no anchor
     *  carried (a warrant built by hand); readers fall back to
     *  residencyStartMono when this is 0. */
    val graceAnchorMono: Long = 0L
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
        /** The six permission-family items, board order. */
        val PERMISSIONS: List<WatchItemId> = listOf(
            ACCESSIBILITY, USAGE_ACCESS, DEVICE_ADMIN,
            NOTIFICATION_LISTENER, OVERLAY, BATTERY
        )
        /** The counted four, board order. */
        val COUNTED: List<WatchItemId> = listOf(
            ACCESSIBILITY, USAGE_ACCESS, DEVICE_ADMIN, NOTIFICATION_LISTENER
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
    val pokeGen: Long = 0L
) {
    fun measurement(item: WatchItemId): WatchMeasurement? = items[item]

    companion object {
        val EMPTY = WatchSnapshot(emptyMap(), 0L, 0L, false, null)
    }
}

/** What a permission line prints at a given instant (spec §4.2):
 *  never-measured, the measured value, or its stale derivative. */
enum class PermPrint { UNMEASURED, GRANTED, MISSING, STALE, MISSING_STALE }

object WatchStore {

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

    /** Freshness doctrine (FIX-1, answering CRIT-1 C-2 + DAY-1 D-2):
     *  a pipeline stamp proves the wire only while it is FRESH —
     *  at most this old. Aligned with [STALE_AFTER_MS] on purpose:
     *  one staleness law for the whole store, not two clocks. A
     *  stamp older than this is no proof at all, however it was
     *  minted; the bound service's 15 s renewal loop (C-2/C-4)
     *  keeps a healthy pipeline's stamp perpetually inside it. */
    const val STAMP_FRESH_MS: Long = 45_000L

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

    /** The in-flight sweep's dispatch instant. A permission item
     *  counts toward the completing set when its measurement landed
     *  at or after this wall time — from the sweep's own query or
     *  from a fresher lifecycle/heartbeat stamp of the same era. */
    @Volatile private var pendingSweepStartWall: Long = 0L

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
    // a Deck residency starts when the Deck is entered. Residency
    // start is now ONLY the probe trigger and the grace anchor —
    // stamp validity is freshness, not residency comparison
    // (FIX-1, C-2/D-2: see [pipelineProvenFresh]).
    @Volatile private var residencyStartMono: Long = 0L

    // Bind-epoch grace debt (FIX-1 Addendum 3, DAY-1 D-2): the
    // first instant THIS BIND EPOCH was observed unproven. The
    // grace age is inherited across consecutive unstamped
    // residencies of the same bind epoch — per-residency grace
    // let tab-bouncing mint a fresh 4 s of silence on every Deck
    // re-entry and mask the OFF verdict indefinitely. Set/cleared
    // ONLY in deriveWarrant (observed proof clears it, observed
    // unproof starts it) and at the epoch boundary
    // onAccessibilityBound(false). Deliberately NOT cleared in
    // noteResidencyStart — that is the entire point.
    @Volatile private var graceDebtStartMono: Long = 0L

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
        // Decaying re-probe series (FIX-1 Addendum 1, MAX-1 ADOPT-NOW —
        // replaces the single +1500 ms re-probe): further probes at
        // +600 / +1500 / +3000 ms, stop-on-stamp. The old landed pair
        // (0, +1500) could both lose to a root-null transient; the
        // series makes a false past-grace OFF on a healthy phone
        // near-impossible. Each step checks fresh proof FIRST and
        // does not fire if already proven; the residency-identity
        // guard is unchanged (a superseded residency is never probed
        // on its predecessor's schedule), and the binding-generation
        // guard (FIX-1 Addendum 2, TECH-1 MAJOR-1) stops the series
        // when the binding it was scheduled under has died — a stale
        // generation's scheduled probes no-op.
        val scheduledStartMono = residencyStartMono
        val scheduledBindingGen = MonolithAccessibilityService.bindingGeneration
        sweepScope.launch {
            var waitedMs = 0L
            for (stepMs in listOf(600L, 1_500L, 3_000L)) {
                delay(stepMs - waitedMs)
                waitedMs = stepMs
                if (residencyStartMono != scheduledStartMono) return@launch
                if (MonolithAccessibilityService.bindingGeneration != scheduledBindingGen) {
                    return@launch
                }
                if (pipelineProvenFresh()) return@launch
                MonolithAccessibilityService.instance?.requestPipelineProbe()
            }
        }
    }

    /**
     * The stamp test under the freshness doctrine (FIX-1, answering
     * CRIT-1 C-2 + DAY-1 D-2 — replaces stampedThisResidency):
     * the pipeline is proven when its last root stamp exists and is
     * no older than [STAMP_FRESH_MS] right now. Validity is the
     * stamp's AGE, never a comparison against residency start —
     * the old residency comparison minted a stamp once and trusted
     * it for the residency's whole life (a zombie-bound service
     * read LIVE all day, C-2) and re-opened the fog race on every
     * Deck re-entry (D-2). Anti-inheritance survives by
     * construction: onUnbind zeroes lastRootMono, and the stamp is
     * process-lifetime state, so no predecessor's proof can be
     * inherited — fresh or otherwise. That claim is load-bearing
     * on the service's binding-generation guard (FIX-1 Addendum 2,
     * TECH-1 MAJOR-1): a probe launched before an unbind can
     * complete its binder wait after onUnbind zeroed the stamp and
     * would otherwise resurrect the dead binding's proof into the
     * next binding, where this freshness window would read it as
     * the new binding's own. The generation guard in
     * probePipelineNow is what makes "no inheritance" true.
     */
    fun pipelineProvenFresh(): Boolean {
        val last = MonolithAccessibilityService.lastRootMono
        if (last <= 0L) return false
        val age = SystemClock.elapsedRealtime() - last
        return age in 0L..STAMP_FRESH_MS
    }

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
    fun onAccessibilityBound(bound: Boolean) {
        // Bind-epoch boundary (FIX-1 Addendum 3, D-2): an unbind
        // ends the bind epoch, so its grace debt dies with it — a
        // rebind earns a fresh grace honestly. A bind (true)
        // clears nothing: the debt is the epoch's, from its first
        // unproven observation.
        if (!bound) graceDebtStartMono = 0L
        stampPermission(WatchItemId.ACCESSIBILITY, bound)
    }

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
     */
    fun onShieldIteration(protecting: Boolean) {
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
            next
        }
    }

    /**
     * The grant-check sweep: six permission queries dispatched in
     * parallel at t=0; each item re-stamps when its own query lands
     * (no fixed stagger — a fast grant never waits for a slow one).
     * When all six carry this sweep's generation, LAST PROVED
     * advances to the set's completion instant.
     */
    fun runSweep(query: (WatchItemId) -> Boolean) {
        val gen = ++sweepGen
        pendingSweepStartWall = System.currentTimeMillis()
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
     * read the same three inputs and ran this same table). The
     * state table itself is a pure function of its inputs; the one
     * side effect is the bind-epoch grace debt below, which is a
     * store observation, recorded here because this is the one
     * place every verdict passes through.
     *
     * Conjuncts: accessibility bound AND pipeline proven fresh
     * ([pipelineProvenFresh], passed in as [stamped]) AND usage
     * ALLOWED. Shield-protecting rides along as a fact for the
     * cascade and the strip but is NOT a conjunct — the shield
     * starts with the session, so requiring it first would be
     * circular.
     *
     * Unbound grace (FIX-1, answering DAY-1 D-1 / FEEL-1 F-1 /
     * CRIT-1 C-5): !bound is OFF, EXCEPT when the accessibility
     * grant is enabled ([a11yGrantEnabled] — the grant fact
     * gathered via MonolithRepository.isAccessibilityEnabled(),
     * DAY-1's named helper, Addendum 3 item 1) and the grace age
     * is still inside [RESIDENCY_GRACE_MS] — a binding-in-progress
     * is a checking state (UNPROVEN), not a death. Past grace, or
     * with no grant at all, unbound is OFF as it always was. Every
     * other row is unchanged.
     *
     * Bind-epoch grace (FIX-1 Addendum 3, answering DAY-1 D-2):
     * the grace age is measured from the EFFECTIVE ANCHOR —
     * graceDebtStartMono when a debt is running, else
     * residencyStartMono — never from the current residency alone.
     * An unbound-granted read inherits the debt exactly as a
     * bound-but-unstamped read does. No anchor carried (both 0)
     * means no grace.
     */
    fun deriveWarrant(
        a11yBound: Boolean,
        stamped: Boolean,
        usage: UsageAccess,
        shieldProtecting: Boolean,
        readMono: Long = SystemClock.elapsedRealtime(),
        a11yGrantEnabled: Boolean = false
    ): Warrant {
        // Bind-epoch debt bookkeeping (Addendum 3, D-2), before the
        // table so this very read is judged by the debt it starts:
        //  - observes fresh proof (stamped) → the epoch is proven;
        //    the debt clears.
        //  - observes bound-but-unstamped, or unbound-granted-
        //    unstamped, with no debt running → the debt starts at
        //    this read instant.
        // An unbound read with NO grant starts no debt: it is OFF
        // at once, a fact with a door, not a grace consumer.
        if (stamped) {
            graceDebtStartMono = 0L
        } else if ((a11yBound || a11yGrantEnabled) && graceDebtStartMono == 0L) {
            graceDebtStartMono = readMono
        }
        // The effective grace anchor: the debt's start when a debt
        // is running, else this residency's start.
        val graceAnchorMono: Long =
            if (graceDebtStartMono > 0L) graceDebtStartMono else residencyStartMono
        val graceAgeMs: Long? =
            if (graceAnchorMono > 0L) {
                (readMono - graceAnchorMono).coerceAtLeast(0L)
            } else {
                null
            }
        val unboundWithinGrace =
            a11yGrantEnabled && graceAgeMs != null &&
                graceAgeMs <= RESIDENCY_GRACE_MS
        val state = when {
            !a11yBound && unboundWithinGrace -> WarrantState.UNPROVEN
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
            residencyStartMono = residencyStartMono,
            // Bind-epoch law (Addendum 3, D-2): the warrant carries
            // the effective grace anchor it was judged under, so
            // pure readers (sentence/seam) measure grace age from
            // the anchor — falling back to residencyStartMono only
            // when a hand-built warrant carries anchor 0.
            graceAnchorMono = graceAnchorMono
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
     *  showing OVERLAY / BATTERY MISSING). */
    fun countedWarranted(snapshot: WatchSnapshot, nowMono: Long): Int =
        WatchItemId.PERMISSIONS.count { permPrintAt(snapshot, it, nowMono) == PermPrint.GRANTED }

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
        // measurement from the in-flight sweep's era (at or after
        // its dispatch), the set is complete and the instant is the
        // last landing's wall time. A partial set never advances it.
        val sweepStart = pendingSweepStartWall
        if (sweepStart > 0L) {
            val perms = WatchItemId.PERMISSIONS.mapNotNull { next.items[it] }
            if (perms.size == WatchItemId.PERMISSIONS.size &&
                perms.all { it.measuredWall >= sweepStart }
            ) {
                val completedAt = perms.maxOf { it.measuredWall }
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
            }
        }
        _snapshots.value = next
    }

    /**
     * The interceptor derivation (§4.3), computed at every emission:
     *  RUNNING — setting on, every parent currently-warranted, no
     *            suspension in force;
     *  PAUSED  — setting on, warranted, and HIS suspension in force
     *            (the Safe Break, for the two blocking interceptors);
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
        val suspended = when (item) {
            WatchItemId.NOTIFICATIONS -> false
            else -> safeBreakActive
        }
        return if (suspended) InterceptorState.PAUSED else InterceptorState.RUNNING
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
        shortsSettingOn = true
        sitesSettingOn = true
        safeBreakActive = false
        sessionCapturing = false
        residencyStartMono = 0L
        graceDebtStartMono = 0L
    }
}
