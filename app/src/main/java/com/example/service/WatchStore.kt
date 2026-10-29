package com.example.service

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
 * Persistence: NONE this wave. The store is in-memory behind this
 * writer interface; the Readiness Log implementation (an append-only
 * measurement table, Migration 9→10) is Slice E's, and swaps in
 * behind these same writers. The no-op is named, not hidden: nothing
 * in this file touches Room.
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
    val readMono: Long = 0L
)

/** The nine watch items, in board order (spec §4.1). Items 1–4 are
 *  the counted four of the header's Guard line. */
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
    val lastProvedWallMillis: Long?
) {
    fun measurement(item: WatchItemId): WatchMeasurement? = items[item]

    companion object {
        val EMPTY = WatchSnapshot(emptyMap(), 0L, 0L, false, null)
    }
}

/** What a permission line prints at a given instant (spec §4.2):
 *  never-measured, the measured value, or its stale derivative. */
enum class PermPrint { UNMEASURED, GRANTED, MISSING, STALE }

object WatchStore {

    /** A PASS measurement older than this (monotonic) is stale. */
    const val STALE_AFTER_MS: Long = 45_000L

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
    // capture has no setting — it is on by design — and no suspension
    // input exists for it in the shipped app, so its PAUSED is
    // unreachable by construction, not by omission.
    @Volatile private var shortsSettingOn = true
    @Volatile private var sitesSettingOn = true
    @Volatile private var safeBreakActive = false

    // The wire's residency machinery (moved whole from LiveWire):
    // a Deck residency starts when the Deck is entered; the wire can
    // read LIVE only when the accessibility pipeline has stamped an
    // event at or after that instant — proof it processed THIS visit.
    @Volatile private var residencyStartMono: Long = 0L

    fun noteResidencyStart() {
        residencyStartMono = SystemClock.elapsedRealtime()
        poke()
    }

    /** The stamp test, exactly as the wire has always run it. */
    fun stampedThisResidency(): Boolean =
        residencyStartMono > 0L &&
            MonolithAccessibilityService.lastRootMono >= residencyStartMono

    /** A nudge with no data: forces a snapshot re-emission so derived
     *  readers (the Deck warrant) re-read the living facts. */
    fun poke() {
        _snapshots.value = _snapshots.value.copy()
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
                val granted = runCatching { query(item) }.getOrDefault(false)
                stampPermission(item, granted, gen)
            }
        }
    }

    /** Interceptor inputs, pushed by the ViewModel. A change is a
     *  derivation trigger: the snapshot re-emits with fresh derived
     *  interceptor measurements. */
    fun updateInterceptorInputs(
        shortsOn: Boolean? = null,
        sitesOn: Boolean? = null,
        safeBreak: Boolean? = null
    ) {
        var changed = false
        if (shortsOn != null && shortsOn != shortsSettingOn) { shortsSettingOn = shortsOn; changed = true }
        if (sitesOn != null && sitesOn != sitesSettingOn) { sitesSettingOn = sitesOn; changed = true }
        if (safeBreak != null && safeBreak != safeBreakActive) { safeBreakActive = safeBreak; changed = true }
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
            readMono = readMono
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
     *  never stored. A PASS older than [STALE_AFTER_MS] is STALE;
     *  MISSING never stales. */
    fun permPrintAt(snapshot: WatchSnapshot, item: WatchItemId, nowMono: Long): PermPrint {
        val m = snapshot.items[item] ?: return PermPrint.UNMEASURED
        val perm = m.perm ?: return PermPrint.UNMEASURED
        return when {
            perm == PermState.GRANTED && nowMono - m.measuredMono > STALE_AFTER_MS ->
                PermPrint.STALE
            perm == PermState.GRANTED -> PermPrint.GRANTED
            else -> PermPrint.MISSING
        }
    }

    /** "Currently warranted GRANTED" for the Guard line's count:
     *  GRANTED and not stale (spec §2.3). */
    fun countedWarranted(snapshot: WatchSnapshot, nowMono: Long): Int =
        WatchItemId.COUNTED.count { permPrintAt(snapshot, it, nowMono) == PermPrint.GRANTED }

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
                if (completedAt != next.lastProvedWallMillis) {
                    next = next.copy(lastProvedWallMillis = completedAt)
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
     * both); NOTIFICATIONS rides the listener binding alone.
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
        sweepGen = 0
        pendingSweepStartWall = 0L
        shortsSettingOn = true
        sitesSettingOn = true
        safeBreakActive = false
        residencyStartMono = 0L
    }
}
