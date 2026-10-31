package com.example.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.MLog
import com.example.MainActivity
import com.example.R
import com.example.data.AppBlockTier
import com.example.data.BreakDimState
import com.example.widget.MonolithWidgetProvider
import com.example.data.batteryShieldWarning
import com.example.data.readBatterySnapshot
import com.example.data.MonolithRepository
import com.example.data.NightDimMath
import com.example.data.SightState
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * The shield's command dispatch (Issue #78, Audit R2 ipc-intents):
 * fail-CLOSED. Only [MonolithShieldService.ACTION_START_SHIELD]
 * starts protection; only [MonolithShieldService.ACTION_STOP_SHIELD]
 * stops it. A null intent (START_STICKY system restart) may resume
 * ONLY when a live session is re-derived from the store — with no
 * live session it stops itself and never enters the foreground. An
 * unknown action never starts anything and never tears down a
 * running loop. Pure, so the matrix is unit-tested action by action.
 */
internal enum class ShieldDispatch { STOP, START_FULL, KEEP_RUNNING, STOP_SELF }

internal fun shieldDispatchFor(
    action: String?,
    monitorRunning: Boolean,
    hasLiveSession: Boolean
): ShieldDispatch = when {
    action == MonolithShieldService.ACTION_STOP_SHIELD -> ShieldDispatch.STOP
    action == MonolithShieldService.ACTION_START_SHIELD ->
        if (monitorRunning) ShieldDispatch.KEEP_RUNNING else ShieldDispatch.START_FULL
    action == null -> when {
        !hasLiveSession -> ShieldDispatch.STOP_SELF
        monitorRunning -> ShieldDispatch.KEEP_RUNNING
        else -> ShieldDispatch.START_FULL
    }
    else -> if (monitorRunning) ShieldDispatch.KEEP_RUNNING else ShieldDispatch.STOP_SELF
}

/**
 * Issue #69 F1: the standing shield notification's identity for
 * change-guarding. Everything the user can read on the card —
 * title, subtitle, expanded text and badge number — is in the
 * key; the chronometer end is NOT: it names a fixed wall instant
 * (the session/break end), so re-posting only ever shifted it by
 * sub-second jitter, and the first post's value stays the truth.
 */
internal fun shieldNotificationKey(
    title: String,
    subtitle: String,
    bigText: String?,
    badgeNumber: Int?,
    repairKey: String = ""
): String = listOf(
    title, subtitle, bigText ?: "", badgeNumber?.toString() ?: "", repairKey
).joinToString("\u0001")

/**
 * Issue #69 F1: re-post guard for the standing notification.
 * The shield loop iterates every second (#332's cadence), but
 * the card's text changes at most once a minute (the countdown)
 * or when a reach is stopped / a notification is held. The old
 * loop rebuilt and re-posted the notification on EVERY
 * iteration. [shouldPost] returns true exactly when the key
 * differs from the last posted key, and records it.
 */
internal class ShieldNotificationGuard {
    private var lastPostedKey: String? = null
    fun shouldPost(key: String): Boolean {
        if (key == lastPostedKey) return false
        lastPostedKey = key
        return true
    }
}

/**
 * Issue #69 F1: one iteration's view of the apps table — the
 * ONE snapshot the iteration read, and everything derived from
 * it. Every consumer of the apps table in the shield loop reads
 * through this object, so an iteration can never drift into a
 * second (or third) full-table scan.
 */
internal class ShieldIterationApps(
    val snapshot: List<com.example.data.BlockedAppEntity>,
    val blockedPackages: Set<String>,
    val expiredPeeks: Set<String>
) {
    fun appFor(packageName: String): com.example.data.BlockedAppEntity? =
        snapshot.find { it.packageName == packageName }
}

/**
 * Issue #69 F1: derive the iteration's blocked set and its
 * expired-peek eviction set from the SAME [snapshot] — same
 * rows, same [nowMono], identical sets to the old three-scan
 * shape. Pure, so the one-snapshot law is unit-tested with a
 * counting provider at the call site in the loop.
 */
internal fun shieldIterationApps(
    snapshot: List<com.example.data.BlockedAppEntity>,
    isInSafeBreak: Boolean,
    nowMono: Long,
    isDialerPackage: (String) -> Boolean = { false }
): ShieldIterationApps {
    val blockedPackages = snapshot
        .filter { app ->
            // W3 #134 (composed at the merge with main): the
            // dialer is never in the blocked set — this door's
            // usage-stats sweep would otherwise intercept the
            // Phone app mid-dial, locking the user out of placing
            // a call. The predicate is the repository's, passed
            // in so this derivation stays pure and testable.
            if (isDialerPackage(app.packageName)) return@filter false
            val tier = app.resolvedTier
            // Issue #140: the daily limit is real. An app whose
            // today's usage has reached its limit is blocked
            // whatever its tier — except inside a lawful peek
            // already running (Safe Break or a live 2-minute
            // lease), which is never yanked mid-peek. Usage is
            // the reconciled todayUsageMinutes on the row.
            val overLimit = app.dailyLimitMinutes > 0 &&
                app.todayUsageMinutes >= app.dailyLimitMinutes
            when {
                isInSafeBreak && tier == AppBlockTier.PEEK_2M -> false
                tier == AppBlockTier.PEEK_2M && nowMono < app.activePeekUntilElapsedRealtime -> false
                overLimit -> true
                tier == AppBlockTier.OPEN -> false
                else -> true
            }
        }
        .map { it.packageName }
        .toSet()
    return ShieldIterationApps(
        snapshot = snapshot,
        blockedPackages = blockedPackages,
        expiredPeeks = com.example.data.expiredPeekPackages(snapshot, nowMono)
    )
}

/** Issue #337 (Freedom, Platform F2): a grant whose absence the
 *  session notification can repair, with the label its action
 *  button carries and the settings screen it opens. Accessibility
 *  is the fatal one (Freedom's taxonomy: only Accessibility is
 *  'disabled'; anything else missing is 'restricted') — its label
 *  is Freedom's own: 'Enable blocking'. */
enum class RepairAction(val label: String) {
    ACCESSIBILITY("Enable blocking"),
    USAGE_ACCESS("Usage access")
}

/** Which repair actions a notification rendered RIGHT NOW must
 *  carry: one per missing grant, fatal first. Empty when every
 *  grant the shield depends on is present — the notification then
 *  stays the plain session announcement it always was. */
internal fun missingRepairActions(
    accessibilityBound: Boolean,
    usageAccessGranted: Boolean
): List<RepairAction> {
    val missing = mutableListOf<RepairAction>()
    if (!accessibilityBound) missing += RepairAction.ACCESSIBILITY
    if (!usageAccessGranted) missing += RepairAction.USAGE_ACCESS
    return missing
}

class MonolithShieldService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var monitorJob: Job? = null

    // Audit R6 #340 F26: the interactivity gate. SCREEN_OFF parks
    // the enforcement loop (no scans, no usage queries, no
    // notification rebuilds behind a locked screen); USER_PRESENT
    // un-parks it and the very next pass is a full scan — the
    // rescan lands at the one moment blocking matters, the unlock.
    @Volatile
    private var screenInteractive = true

    private var screenReceiverRegistered = false
    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> screenInteractive = false
                Intent.ACTION_USER_PRESENT -> screenInteractive = true
            }
        }
    }

    // Audit R6 #340 F27: one summons per blindness EPISODE, never
    // one per iteration — the witness drops the moment sight is
    // lost and the user is told once, loudly.
    @Volatile
    private var blindEpisodeActive = false

    // Audit R6 #340 F25: a PARTIAL wake lock held only across a
    // single enforcement tick (15 s timeout backstop), never for
    // the session's whole length — Freedom's session-long hold is
    // the documented failure this refuses to repeat. WAKE_LOCK is
    // declared in the manifest for exactly this holder.
    private var tickWakeLock: PowerManager.WakeLock? = null

    private fun acquireTickWakeLock() {
        try {
            val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return
            val lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "monolith:shield-tick")
            lock.setReferenceCounted(false)
            lock.acquire(TICK_WAKE_LOCK_TIMEOUT_MS)
            tickWakeLock = lock
        } catch (_: Exception) {
            // A lock that cannot be taken never stops enforcement.
        }
    }

    private fun releaseTickWakeLock() {
        try {
            tickWakeLock?.let { if (it.isHeld) it.release() }
        } catch (_: Exception) {
        }
        tickWakeLock = null
    }

    private fun registerScreenReceiver() {
        if (screenReceiverRegistered) return
        try {
            registerReceiver(
                screenStateReceiver,
                IntentFilter().apply {
                    addAction(Intent.ACTION_SCREEN_OFF)
                    addAction(Intent.ACTION_USER_PRESENT)
                }
            )
            screenReceiverRegistered = true
        } catch (_: Exception) {
            // Without the receiver the loop simply never parks —
            // the pre-F26 behaviour, never a crash.
        }
    }
    // The Safe Break's announcer (Audit R3, safe-break-lived
    // #3/#4): fed the updated row every iteration; its alerts ride
    // their own HIGH-importance channel — the persistent shield
    // notification is IMPORTANCE_LOW + onlyAlertOnce by design and
    // can never announce anything.
    private val breakAnnouncer = BreakAnnouncer()

    /** The live session's id re-derived from the store, or null
     *  when there is none (or the read failed — fail closed). The
     *  only lawful answer to "should a command with no explicit
     *  START resume protection?": the store's row, never the
     *  intent's say-so. Read synchronously because onStartCommand
     *  must decide before it returns; the read is a single Room
     *  snapshot on Room's own executor. */
    private fun liveActiveSessionId(): Int? = runCatching {
        runBlocking {
            // ISSUE-67: via the single construction point
            // (AppContainer resolves the same getInstance singleton).
            com.example.AppContainer.dao(applicationContext)
                .getActiveSessionSnapshot()?.takeIf { it.isActive }?.id
        }
    }.onFailure {
        // Issue #75 F1 wiring: a thrown read reads as "no live
        // session" to the dispatch — that conversion must not be
        // silent, or a wedged DB stands the shield down invisibly.
        MLog.w("liveActiveSessionId read failed; dispatch treats it as no live session", it)
    }.getOrNull()

    // Issue #122: the last notification this service posted, in
    // its BASE form (no battery segment) — a battery broadcast
    // re-posts it with the fresh warning without inventing a
    // title or a countdown of its own.
    @Volatile private var lastBaseTitle: String? = null
    @Volatile private var lastBaseSubtitle: String? = null
    // Issue #155 (IQ-3 reseat, re-close bench): the standing card's
    // break state, remembered beside its base title/subtitle so a
    // battery re-post during a Safe Break rebuilds the card with the
    // break's public face — never the standing "protected" line
    // while PEEK apps are lawfully open.
    @Volatile private var lastBaseInSafeBreak: Boolean = false

    /** Battery events that change what the shield may honestly
     *  promise: the battery running out, the charger arriving or
     *  leaving, saver flipping. Each one re-reads the battery and
     *  re-posts the standing notification with the fresh segment.
     *  (The monitor loop also re-reads every iteration; this makes
     *  the warning land at the event, not at the next tick.) */
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            refreshBatteryNotification()
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        }
        ContextCompat.registerReceiver(
            this, batteryReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    /** Re-post the standing notification with a fresh battery
     *  read. Called by the battery receiver and by the manifest
     *  MonolithBootReceiver's battery branch. No-op before the
     *  first post — there is no promise on screen to correct. */
    fun refreshBatteryNotification() {
        // Issue #155 (WING-F, gate completeness): the re-post is
        // LATCH-GATED — while the latch is down the stored base is
        // the full session card, and re-posting it on a battery
        // event would revive a protection claim the loop has
        // already retracted. The loop owns the screen while
        // unproven (it posts the arming card); a battery event
        // never speaks over it.
        if (!protecting) return
        val title = lastBaseTitle ?: return
        val subtitle = lastBaseSubtitle ?: return
        updateNotification(title, subtitle, inSafeBreak = lastBaseInSafeBreak)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        // A null delivery (sticky restart) is dispatched on the
        // re-derived session fact, read BEFORE any state moves.
        val liveSessionId = if (action == null) liveActiveSessionId() else null
        return when (
            shieldDispatchFor(action, monitorJob?.isActive == true, liveSessionId != null)
        ) {
            ShieldDispatch.STOP -> {
                protecting = false
                protectingSessionId = -1
                protectingMono = 0L
                sightState = SightState.BLIND_NO_ACCESS
                sightMono = 0L
                lastVerifiedWallMillis = null
                WatchStore.onShieldIteration(protecting = false)
                stopForeground(STOP_FOREGROUND_REMOVE)
                // Issue #155 (F1): the shield is down — refresh
                // the home-screen widget at the moment it matters.
                MonolithWidgetProvider.requestUpdate(applicationContext)
                stopSelf()
                START_NOT_STICKY
            }
            ShieldDispatch.STOP_SELF -> {
                // Fail closed: an unknown action, or a sticky
                // restart with no live session behind it. No witness
                // reset, no foreground, no loop — the service simply
                // declines to exist for this command.
                stopSelf()
                START_NOT_STICKY
            }
            ShieldDispatch.KEEP_RUNNING -> {
                // Duplicate START / unknown action / sticky restart
                // over a live loop: the loop and the protection
                // witness stand untouched. Foreground is re-asserted
                // (a startForegroundService delivery carries its own
                // promote-or-crash deadline; re-posting the standing
                // notification satisfies it without moving any
                // protection state). The ONE lawful witness reset
                // here is a session change — the witness attests the
                // OLD session, so the new one must re-prove on the
                // running loop's next iteration.
                if (!promoteToForeground()) return START_NOT_STICKY
                val currentId = liveSessionId ?: liveActiveSessionId()
                if (protecting && currentId != null && protectingSessionId != currentId) {
                    protecting = false
                    protectingSessionId = -1
                    protectingMono = 0L
                    sightState = SightState.BLIND_NO_ACCESS
                    sightMono = 0L
                    lastVerifiedWallMillis = null
                    WatchStore.poke()
                }
                START_STICKY
            }
            ShieldDispatch.START_FULL -> {
                // Cascade witness reset (Slice D step 4, Opal L2): a
                // fresh start must re-prove protection for the
                // session this loop is about to read - a stale flip
                // from a previous session would let the cascade
                // confirm a shield that has not yet run one iteration
                // over the latched row. This branch is reached only
                // when no loop is running, so the witness it resets
                // attests no live loop.
                protecting = false
                protectingSessionId = -1
                protectingMono = 0L

                // promoteToForeground registers the Live Wire
                // instance itself, and only on a startForeground
                // that actually succeeded; a refusal stands the
                // service down instead of starting a loop behind a
                // foreground that does not exist.
                if (!promoteToForeground()) return START_NOT_STICKY
                WatchStore.poke()
                startMonitoringLoop()

                START_STICKY
            }
        }
    }

    /** Enters (or re-asserts) the foreground with the standing
     *  shield notification. Re-asserting moves no protection state:
     *  the witness, the loop and the session are untouched.
     *  Returns false when the platform refused the foreground
     *  start — the caller must stand down (START_NOT_STICKY). */
    private fun promoteToForeground(): Boolean {
        createNotificationChannel()
        createBreakAlertChannel()
        val fgsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        } else {
            0
        }
        // Guarded startForeground (issue #43, finding 3): on
        // targetSdk 36 a specialUse FGS start can throw
        // (ForegroundServiceStartNotAllowedException /
        // SecurityException). An unguarded throw here would leave
        // a zombie — the caller believes a shield was requested,
        // the system has a dying service, and no loop ever runs.
        // On failure: clear every witness, stopSelf, and report
        // false so the dispatch in onStartCommand returns
        // START_NOT_STICKY — nothing advertises protection that
        // does not exist. (Merge, STURM3-15: main's guarded body
        // adopted whole into #78's promoteToForeground; the
        // Boolean replaces main's Int return because #78's
        // dispatch has four call paths, not one.)
        // Issue #155 (F2): the guarded call below carries the
        // honest arming text ("Monolith" / "Arming shield\u2026"),
        // not a claim of a lock that has not happened yet.
        // Issue #122: remember that BASE form so a battery
        // broadcast can re-post it with a fresh warning segment.
        lastBaseTitle = "Monolith"
        lastBaseSubtitle = "Arming shield\u2026"
        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                buildNotification(lastBaseTitle!!, withBatteryWarning(lastBaseSubtitle!!)),
                fgsType
            )
        } catch (e: Exception) {
            Log.e(TAG, "startForeground failed; standing down instead of zombifying", e)
            if (instance === this) {
                instance = null
            }
            protecting = false
            protectingSessionId = -1
            protectingMono = 0L
            sightState = SightState.BLIND_NO_ACCESS
            sightMono = 0L
            lastVerifiedWallMillis = null
            WatchStore.onShieldIteration(protecting = false)
            stopSelf()
            return false
        }
        // Live Wire registry (Ines): the instance exists only once
        // startForeground has actually succeeded - no instance, no
        // flag, no rumour.
        instance = this
        // Audit R6 #340: the failure log binds and the blindness
        // witness resets at promotion; the screen receiver
        // registers here so the loop's first pass already knows
        // the interactivity state. (The poke + loop start live in
        // onStartCommand's dispatch, as main's structure has it.)
        FailureLog.bind(applicationContext)
        blindEpisodeActive = false
        registerScreenReceiver()
        // Main's WatchStore.bind (Live Wire registry) preserved:
        // binding is idempotent and every other door binds on entry.
        WatchStore.bind(applicationContext)
        return true
    }

    private fun startMonitoringLoop() {
        monitorJob?.cancel()
        // ISSUE-67: construction via the single point; the DAO
        // comes from the same container for the loop's remaining
        // direct reads.
        val dao = com.example.AppContainer.dao(applicationContext)
        val repository = com.example.AppContainer.repository(applicationContext)

        // Audit R6 #340 F61: an uncaught death of the monitor loop
        // leaves a scrubbed persistent line instead of vanishing.
        val monitorFailureHandler = CoroutineExceptionHandler { _, e ->
            FailureLog.record("shield", "monitorLoop", e)
        }
        monitorJob = serviceScope.launch(monitorFailureHandler) {
            var iteration = 0
            var parkedPasses = 0
            // Audit R3b #146: break-transition witness for the
            // summons — the loop remembers the last tick's break
            // state so the flip out of a break posts exactly once.
            var wasInSafeBreak = false
            // Issue #69 F1: the standing notification is re-posted
            // only when its readable text actually changes.
            val notificationGuard = ShieldNotificationGuard()
            // Issue #143 F1/F6: the break-dim witness — the loop
            // remembers the descent's depth so the flip out of a
            // break can stamp the fade OUT the UI runs (breakDim).
            var lastBreakElapsedSeconds = 0
            var breakFadeStampMono = 0L
            while (isActive) {
                iteration++
                // Per-iteration guard (issue #43, finding 1): one
                // Room / usage-stats / heartbeat exception must NOT
                // cancel this job while the foreground notification
                // and a stale protecting=true stay alive — that was
                // the lying-zombie failure. A failed iteration
                // publishes protecting=false for its witness, logs,
                // waits one cadence, and the loop CONTINUES. Only
                // CancellationException escapes (structured
                // cancellation is not a failure).
                try {
                // Routine heartbeat cadence (rebuild §2.2): first pass,
                // then ~every 30 s (15 iterations of the 2 s loop), so a
                // window boundary is reconciled within half a minute
                // even if every alarm was lost.
                if (iteration == 1 || iteration % 15 == 0) {
                    RoutineHeartbeat.tick(
                        applicationContext, RoutineHeartbeat.Source.SHIELD_LOOP
                    )
                    // Issue #129 F2: the catalogue resyncs on the
                    // same ~30s cadence, mid-session. An app
                    // installed AFTER this session sealed previously
                    // stayed invisible to both enforcement doors
                    // until the session ended; now its row (and its
                    // birth tier) lands within one cadence, and the
                    // blocked-set derivation below reads the fresh
                    // table on the next iteration. A failed sync
                    // leaves the standing table untouched — the
                    // reconcile itself refuses an empty query.
                    runCatching { repository.ensureSeededAndSyncPackages() }
                        .onFailure {
                            // Issue #75 doctrine (MLog): a swallowed
                            // failure is logged, never silent — a
                            // resync that keeps failing must not read
                            // as a catalogue that simply has no news.
                            MLog.w("catalogue resync failed; standing table untouched", it)
                        }
                }
                // Audit R6 #340 F26: PARKED while the screen is off.
                // No usage query, no app-table scan, no notification
                // rebuild — the loop asks only, every ~5 s, whether
                // the session it guards still exists (a session that
                // ended while parked must not leave a foreground
                // service standing over nothing). Expiry processing
                // itself resumes with the first full pass, which
                // lands within a second of USER_PRESENT because the
                // parked cadence is 1 s and work-free. This is NOT
                // Freedom's pattern (a 2 s loop that wakes to check
                // a flag and run anyway): parked passes run nothing.
                if (!screenInteractive) {
                    parkedPasses++
                    if (parkedPasses % 5 == 0) {
                        val s = dao.getActiveSessionSnapshot()
                        if (s == null || !s.isActive) {
                            stopForeground(STOP_FOREGROUND_REMOVE)
                            stopSelf()
                            break
                        }
                    }
                    delay(1000L)
                    continue
                }
                parkedPasses = 0
                // Audit R6 #340 F25: the tick's wake lock — held
                // only across this one enforcement pass.
                acquireTickWakeLock()
                val activeSession = repository.activeSessionSnapshot() // ISSUE-67(d): via repository, not direct DAO
                if (activeSession == null || !activeSession.isActive) {
                    // Issue #143: no session, no dim — retire the
                    // phase before standing down.
                    publishBreakDim(BreakDimState())
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    MonolithWidgetProvider.requestUpdate(applicationContext)
                    stopSelf()
                    break
                }

                // Issue #155 (IQ-4 edge 9): the in-loop A→B handover.
                // The heartbeat reconcile at this loop's own top can
                // finalise session A and materialise routine session
                // B inside the SAME loop — the loop keeps running
                // and really does protect B, but a witness still
                // keyed to A can never flip for B (the flip guard
                // requires !protecting), so the widget would print
                // UNPROVEN for B's whole session. Mirror the
                // KEEP_RUNNING rule here: an id change re-keys the
                // witness, and this same iteration's flip re-proves
                // it for the session actually being protected.
                if (protecting && protectingSessionId != activeSession.id) {
                    protecting = false
                    protectingSessionId = -1
                    protectingMono = 0L
                    WatchStore.poke()
                }

                // Issue #81 F4: the shield's sight IS Usage Access.
                // Revoked mid-session, queryForegroundBlockedApp
                // returns null forever — indistinguishable from an
                // empty foreground — while this loop kept beating on
                // schedule and the finish certified ELAPSED_CLEAN.
                // The blindness is now measured every iteration (see
                // the latch just after the progress write below),
                // and the protecting flip further down only happens
                // for an iteration that could actually see.
                val usageGranted = repository.hasUsageStatsPermission()

                // Slice E (Nocke §2.2): the shield's own loop is
                // the beat's only lawful writer — its tick stamps
                // shieldLastBeatWallMillis into the row it already
                // persists, so the finish machine can later prove
                // whether the shield was present or gone.
                // Audit R6 #341 F31: a full disk must not kill the
                // shield. Before this, an SQLiteFullException out of
                // the tick's write escaped the loop and took the
                // session's only protection (and its elapsed record)
                // with it. On a full-disk tick we log, skip the
                // persistence for this beat and keep shielding; the
                // next tick retries the write.
                // Issue #155 (CRITIC-3 Charge C): only a SEEING
                // iteration stamps the beat. The beat is the
                // freshness witness WidgetSealTruth and the finish
                // machine read; stamping it on a usage-blind tick
                // made a blind shield look freshly present and
                // defeated the stale-beat gap law. A blind tick
                // still persists its progress — it just does not
                // sign the witness it could not earn.
                val updated = try {
                    repository.updateSessionProgress(activeSession, stampShieldBeat = usageGranted)
                } catch (e: android.database.sqlite.SQLiteFullException) {
                    Log.e(TAG, "storage full: progress tick not persisted", e)
                    null
                }
                if (updated == null) {
                    delay(2000L)
                    continue
                }
                if (!usageGranted) {
                    // Latched AFTER the progress write on purpose:
                    // updateSession is a full-row @Update, so a latch
                    // set before it would be overwritten by this
                    // iteration's own snapshot (taken while the row
                    // was unlatched). From here the latch persists:
                    // every later iteration re-reads the row fresh,
                    // and the finish classifies ELAPSED_SHIELD_GAP.
                    // (A session completing on THIS tick is covered
                    // by the fresh usage read inside
                    // updateSessionProgress's outcome classification.)
                    repository.latchShieldGapNow()
                    // Issue #155 (IQ-4 edge 2): the glance must fall
                    // to UNPROVEN at the latch, not at the next
                    // scheduled update — and the recovery flip below
                    // already refreshes it when sight returns.
                    MonolithWidgetProvider.requestUpdate(applicationContext)
                }

                // Break boundaries announce themselves from here —
                // the loop that is alive in every app, screen on or
                // off — never from a UI observer that may not exist
                // mid-break (Audit R3, safe-break-lived #3/#4).
                when (breakAnnouncer.onTick(updated.id, updated.isInSafeBreak, updated.safeBreakSecondsRemaining)) {
                    BreakAlert.STARTED -> postBreakAlert(
                        title = "SAFE BREAK STARTED",
                        text = "Break clock ${breakClockText(updated.safeBreakSecondsRemaining)} — " +
                            "PEEK apps are open. Locked apps, Shorts, Reels and blocked sites stay shut."
                    )
                    BreakAlert.FINAL_MINUTE -> postBreakAlert(
                        title = "SAFE BREAK ENDING",
                        text = "Under a minute left — finish up. Blocking resumes when the break clock hits 00:00."
                    )
                    // ENDED is deliberately NOT posted from here:
                    // Audit R3b #146's summons (same loop, the
                    // wasInSafeBreak witness below) is the break-end
                    // voice — "BREAK OVER · Block n has started" —
                    // and two heads-up alerts for one flip would be
                    // noise, not truth. The announcer still returns
                    // ENDED (its lifecycle is unit-tested); this loop
                    // owns STARTED + FINAL_MINUTE, the summons owns
                    // the end.
                    BreakAlert.ENDED -> Unit
                    null -> Unit
                }

                if (!updated.isActive) {
                    // Session-edge tick BEFORE standing down: a routine
                    // session that just timer-completed gets finalised
                    // by derive() now, not at some later wake.
                    RoutineHeartbeat.tickAsync(
                        applicationContext, RoutineHeartbeat.Source.SESSION_EDGE
                    )
                    // Audit R3b #146: the ending is news for the
                    // user who stayed away — it also sounds on the
                    // summons channel, not only as a silent edit to
                    // the detached LOW notification below.
                    postSummons(
                        "SESSION COMPLETE",
                        // Issue #155 (IQ-5 F-1): one story on both
                        // completion cards — the Ledger line, as on
                        // the shield card below (main's #164 wording).
                        "Your session has ended. Your receipt is waiting in the Ledger.",
                        // Issue #155, verifier residue (3b): the
                        // completion moment posts TWO cards — the
                        // expiring shield card below (F3) and this
                        // summons. The summons previously had no
                        // timeout and could linger forever,
                        // re-creating the F3 ghost on the HIGH
                        // channel. Same 45-minute expiry as F3.
                        timeoutAfterMs = COMPLETION_TIMEOUT_MS
                    )
                    // Issue #155 (F3) + R3b #164 F2 (composed at the
                    // STURM-4 recovery merge): the completion receipt
                    // stays findable via DETACH, but it is no longer an
                    // ongoing card — it dismisses on tap and expires
                    // after 45 minutes instead of ghosting forever;
                    // and its line is main's #164 truthful one (the old
                    // "Unlocked desk toy / Break tab" promise named
                    // things that exist nowhere in the app).
                    updateNotification(
                        "Session Complete",
                        "Your receipt is waiting in the Ledger.",
                        ongoing = false,
                        autoCancel = true,
                        timeoutAfterMs = COMPLETION_TIMEOUT_MS
                    )
                    // Issue #143: the session ended — any break dim
                    // retires with it (no fade into a dead session).
                    publishBreakDim(BreakDimState())
                    stopForeground(STOP_FOREGROUND_DETACH)
                    MonolithWidgetProvider.requestUpdate(applicationContext)
                    stopSelf()
                    break
                }

                // Audit R3b #146: the break just expired under a
                // user who is away by design — summon them on the
                // HIGH channel as the next block starts, once.
                if (wasInSafeBreak && !updated.isInSafeBreak) {
                    postSummons(
                        "BREAK OVER",
                        "Block ${updated.currentBlockIndex} of ${updated.totalStudyBlocks} has started.",
                        // Issue #155 (IQ-2 VC-4, wing-added cure):
                        // the break-over summons carries a 10-minute
                        // expiry — untimed, it could sit in the shade
                        // asserting "has started" until tapped; the
                        // completion cards' 45 minutes is not its
                        // term (the block it announces is already
                        // running, so its news is shorter-lived).
                        timeoutAfterMs = BREAK_OVER_TIMEOUT_MS
                    )
                }
                // Issue #143 F1/F6: publish the break's dim phase
                // on the same tick that knows it — in-break elapsed
                // for the descent INTO the break, and at the flip
                // out, the end stamp + the depth the descent had
                // reached, so the UI can run the fade OUT on its
                // own clock. Once the fade window has passed the
                // state retires to rest.
                if (updated.isInSafeBreak) {
                    lastBreakElapsedSeconds =
                        (MonolithRepository.SAFE_BREAK_DURATION_SECONDS -
                            updated.safeBreakSecondsRemaining).coerceAtLeast(0)
                    publishBreakDim(
                        BreakDimState(
                            inBreak = true,
                            breakElapsedSeconds = lastBreakElapsedSeconds
                        )
                    )
                } else if (wasInSafeBreak) {
                    breakFadeStampMono = SystemClock.elapsedRealtime()
                    publishBreakDim(
                        BreakDimState(
                            inBreak = false,
                            breakEndedMono = breakFadeStampMono,
                            fadeFromAlpha = NightDimMath.dimAlphaIntoBreak(lastBreakElapsedSeconds)
                        )
                    )
                } else if (breakFadeStampMono > 0L &&
                    SystemClock.elapsedRealtime() - breakFadeStampMono >= NightDimMath.FADE_OUT_MILLIS
                ) {
                    breakFadeStampMono = 0L
                    publishBreakDim(BreakDimState())
                }
                wasInSafeBreak = updated.isInSafeBreak

                // Issue #274: every live iteration marks the mirror
                // active; the countdown branch below adds the end wall.
                SessionMirror.publish(this@MonolithShieldService, 0L)
                val timeFormatted = when {
                    updated.isInSafeBreak -> {
                        // #127 finding 2: the break count is labelled
                        // as the break's, and the session's remaining
                        // rides beside it — never a bare 00:00-style
                        // finished reading anywhere during a break.
                        val breakLeft = com.example.data.formatSessionDuration(updated.safeBreakSecondsRemaining)
                        val sessionLeft = com.example.data.formatSessionDuration(updated.spanRemainingSeconds)
                        String.format(
                            "Safe Break %s left • %s left in session • Block %d/%d done",
                            breakLeft, sessionLeft, updated.currentBlockIndex, updated.totalStudyBlocks
                        )
                    }
                    updated.isStopwatchMode -> {
                        val mins = updated.elapsedSeconds / 60
                        val secs = updated.elapsedSeconds % 60
                        String.format("Stopwatch +%02d:%02d (20m floor)", mins, secs)
                    }
                    else -> {
                        // Merge resolution (FIX-R3, #274 × #127): main's
                        // #127 text wins for the glance — both scopes,
                        // both labelled, from the model's own derivations
                        // (remainingSeconds / span) — and the PR's
                        // SessionMirror publish is kept, fed with the
                        // same end wall-clock the #155 chronometer
                        // below derives (effectiveTarget − elapsed).
                        val effectiveTarget = updated.targetDurationSeconds + updated.penaltySecondsAdded
                        val remainingSec = (effectiveTarget - updated.elapsedSeconds).coerceAtLeast(0)
                        SessionMirror.publish(
                            this@MonolithShieldService,
                            System.currentTimeMillis() + remainingSec * 1000L
                        )
                        val blockLeft = com.example.data.formatSessionDuration(updated.remainingSeconds)
                        val sessionLeft = com.example.data.formatSessionDuration(updated.spanRemainingSeconds)
                        if (updated.totalStudyBlocks > 1) {
                            String.format(
                                "%s left in block %d/%d • %s left in session",
                                blockLeft, updated.currentBlockIndex, updated.totalStudyBlocks, sessionLeft
                            )
                        } else {
                            String.format("%s left in session", sessionLeft)
                        }
                    }
                }

                // Issue #155 (F5): the expanded card carries the full
                // status, and timed sessions get a SYSTEM chronometer
                // (when = session end) instead of this loop's 2s text
                // snapshot as the only timer. The end is derived from
                // the same remaining-seconds math as the text above.
                val chronometerEndMillis: Long? = when {
                    updated.isStopwatchMode -> null
                    updated.isInSafeBreak ->
                        System.currentTimeMillis() + updated.safeBreakSecondsRemaining * 1000L
                    else -> {
                        val effectiveTarget = updated.targetDurationSeconds + updated.penaltySecondsAdded
                        val remainingSec = (effectiveTarget - updated.elapsedSeconds).coerceAtLeast(0)
                        System.currentTimeMillis() + remainingSec * 1000L
                    }
                }
                // Issue #155 (F4): the badge's one number is the held
                // (captured) notification count — the same count the
                // Deck's HELD chip prints. If that read ever fails,
                // fall back to this session's urges stopped.
                val heldCount = runCatching {
                    dao.getCapturedNotificationsSnapshot().size
                }.getOrNull()
                // Issue #155 (IQ-2 M-5): NO fallback swap — the
                // badge's one number keeps one meaning (held
                // notifications). When the held count is unreadable
                // the badge shows no number, never the urges count
                // wearing the held count's slot.
                val badgeNumber = heldCount
                // Issue #155 (IQ-2 M-8): the bigText's time segment
                // is the LABELLED format (timeFormatted, #127's two
                // labelled scopes), never the bare per-block label;
                // and a blank intention drops its segment instead
                // of leaving a doubled separator at the head.
                val bigText = listOfNotNull(
                    updated.intention.takeIf { it.isNotBlank() },
                    timeFormatted,
                    "Block ${updated.currentBlockIndex}/${updated.totalStudyBlocks}",
                    "${updated.phantomReachesBlocked} urges stopped",
                    if (heldCount != null) "$heldCount held" else "held count unavailable"
                ).joinToString(" • ")

                // Audit R3b #149 (from main): the grace window exists
                // off the Deck too. During the window the shield's own
                // notification counts it down — and for a routine-born
                // row it names the booking instead of promising free.
                // Merge resolution (WELLE-4, #155 × #149): the grace
                // subtitle rides on the #155 card — bigText,
                // chronometer and badge number apply in both branches.
                // Issue #155, verifier residue (3a): the flip runs
                // BEFORE the card posts, and the card follows the
                // flip's truth. The full session card previously
                // posted unconditionally at this point in the loop —
                // on an iteration where the flip could not happen
                // (usage access revoked), the card went on implying
                // protection that was not running and never reverted
                // to the arming text. Now: protecting -> the full
                // card; not protecting -> the honest arming card.
                // Issue #122: PARK's battery stays fresh while a
                // session runs — the shield loop is already awake
                // every 2s; publishing here costs one sticky read.
                WatchStore.publishBattery(readBatterySnapshot(applicationContext))

                val nowMono = SystemClock.elapsedRealtime()
                // Issue #69 F1: ONE apps snapshot per iteration,
                // shared by every consumer below — the blocked-set
                // derivation, the expired-peek eviction and the
                // intercepted app's row. The old loop read the full
                // table THREE times per second (repository + DAO +
                // repository). The rows are still read fresh every
                // iteration — never cached across iterations — so
                // a peek lease granted mid-session is honoured on
                // the very next pass (the rejected 30 s cache would
                // have broken exactly that peek-lease truth).
                val appsSnapshot = repository.allAppsSnapshot() /* ISSUE-67(d): via repository */
                val iterationApps = shieldIterationApps(
                    appsSnapshot, updated.isInSafeBreak, nowMono,
                    isDialerPackage = { repository.isDialerPackage(it) }
                )
                val blockedPackages = iterationApps.blockedPackages

                // Issue #290 F5: screen state is a first-class
                // input to this loop. While the screen is off /
                // the device is locked-idle there is no foreground
                // to police — the session's clock above still
                // ticks (locked-away study counts), but no
                // foreground verdict is acted on, so a stale
                // witness/stat can never fire an intercept at a
                // dark screen or the instant of unlock.
                val screenInteractive = try {
                    (getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager)
                        ?.isInteractive ?: true
                } catch (_: Exception) {
                    true
                }
                // Foreground detection (issue #43, finding 2):
                // prefer the accessibility service's real
                // TYPE_WINDOW_STATE_CHANGED witness when that
                // service is bound; UsageStats is only the fallback
                // when no witness exists (see ForegroundSelection).
                // The call bypass is checked here as well because
                // the witness path does not go through the
                // repository's UsageStats query that checked it
                // before — a call must never be blocked on either
                // path. The cadence is 1 s (issue #332 F1).
                // Issue #99 F1 composes on the fallback path: a
                // package whose peek lease just expired is evicted
                // on this poll even though its lastTimeUsed is
                // minutes stale (stay-put user) — the two-arg query
                // waives the freshness filter for expired peeks.
                // On the witness path the same eviction is natural:
                // an expired lease puts the package back in
                // blockedPackages and the witness IS that package.
                // Issue #69 F1: the eviction set is derived from
                // the SAME single snapshot as the blocked set
                // above (see shieldIterationApps) — same rows,
                // same nowMono, identical eviction set.
                val expiredPeeks = iterationApps.expiredPeeks
                // Shield Rethink 2, Step 1 (Door A): the probe runs
                // BEFORE any certification, and its sight verdict is
                // stamped on the beat. The pre-Step-1 order flipped
                // `protecting` first and queried afterwards over a
                // collapsed null — a loop with its usage access
                // revoked certified itself forever while seeing
                // nothing. Now: no sight, no flip; and the sight of
                // every beat is published with the heartbeat, so a
                // receipt can tell a held shield from a blind one
                // within one probe cycle. Sight has two lawful
                // sources: this loop's own usage-stats probe, and
                // the accessibility door's live foreground stamp —
                // either one seeing is sight. A call no longer
                // blinds either door (#332 F2): its surfaces are
                // exempt per-package through the safety floor, and
                // sight during a call is real sight.
                val probe = repository.probeForegroundApp(blockedPackages, expiredPeeks)
                val sight = when {
                    probe.sight == SightState.IN_CALL -> SightState.IN_CALL
                    probe.sight == SightState.SIGHTED ||
                        MonolithAccessibilityService.foregroundPackage != null ->
                        SightState.SIGHTED
                    else -> probe.sight
                }
                sightState = sight
                sightMono = nowMono
                if (sight == SightState.SIGHTED) {
                    lastVerifiedWallMillis = System.currentTimeMillis()
                }

                // Live Wire: the shield is PROTECTING only once a
                // full monitor iteration has run under a live
                // session - started is not protecting. The cascade
                // witness (Opal L2) is stamped at the same flip:
                // the session this iteration read, and the mono it
                // flipped - CONFIRMED stamp for SHIELD-PROTECTING.
                // Issue #155, unified latch (IQ-1 verdict, WING-F):
                // the flip requires BOTH doors standing — usage
                // access AND the accessibility door armed for THIS
                // session (service bound and armedSessionId == this
                // session's id), measured here, per iteration, at
                // the flip site. Because the flip requires both
                // doors and the demote below fires on either door
                // closing, the protecting witness itself implies
                // the accessibility door: the widget's truth rule
                // needs no second derivation and no second reader
                // (the fork IQ-1 rejected is removed, not re-homed).
                // Demote on EITHER door closing —
                // Race 1b's usage half plus the accessibility half —
                // with a falling-edge widget refresh, in the same
                // iteration that measures the closed door. Before
                // the usage half landed, the latch only ever flipped
                // up: once protecting, a revoked door left it
                // stale-true forever and both the card below and
                // the widget kept claiming protection that was not
                // running.
                // Audit R6 #340 F27, composed onto the #155 latch:
                // the witness is no longer self-issued — the first
                // blind pass of an episode summons the user once on
                // the HIGH channel (one summons per blindness
                // EPISODE, never one per iteration), and the latch's
                // demote below drops the standing witness the moment
                // sight is lost.
                if (!usageGranted && !blindEpisodeActive) {
                    blindEpisodeActive = true
                    postSummons(
                        "SHIELD BLIND",
                        "Usage Access is off — Monolith cannot see the foreground. " +
                            "This stretch is booked as a shield gap, not as protection."
                    )
                }
                if (usageGranted) {
                    blindEpisodeActive = false
                }
                val accessibilityStanding =
                    MonolithAccessibilityService.instance != null &&
                        MonolithAccessibilityService.armedSessionId == updated.id
                if (protecting && (!usageGranted || !accessibilityStanding)) {
                    protecting = false
                    protectingSessionId = -1
                    protectingMono = 0L
                    // Falling edge: the widget re-reads NOW, not at
                    // the platform's 30-minute cadence.
                    MonolithWidgetProvider.requestUpdate(applicationContext)
                }
                // Composed (FIX-R2, PR #388): Shield Rethink 2
                // Step 1's law rides the latch — no sight, no
                // flip. The probe above runs BEFORE this
                // certification on every beat; a beat that could
                // not see (usage revoked, probe error) cannot
                // certify, exactly as a closed door cannot.
                if (!protecting && usageGranted && accessibilityStanding && sightState == SightState.SIGHTED) {
                    protecting = true
                    protectingSessionId = updated.id
                    protectingMono = SystemClock.elapsedRealtime()
                    WatchStore.poke()
                    // Issue #155 (F1): a session is now live — refresh
                    // the home-screen widget at the moment it matters.
                    MonolithWidgetProvider.requestUpdate(applicationContext)
                    // Issue #80 finding 4: the session just armed —
                    // sweep notifications blocked apps posted BEFORE
                    // this moment through the listener's capture path.
                    try {
                        MonolithNotificationListener.instance?.sweepActiveNotifications()
                    } catch (_: Exception) {}
                }

                // Issue #69 F1: ONE guarded post for both branches.
                // The grace subtitle counts down per second, so it
                // re-posts through the window exactly as before;
                // the standing subtitle changes at most once a
                // minute. Everything readable is in the guard key,
                // so a stopped reach (count change) or a held
                // notification (badge change) still re-prints on
                // the very iteration it happens.
                val standingSubtitle = if (updated.elapsedSeconds <= 10 && !updated.isInSafeBreak) {
                    if (updated.routineId != null) {
                        "GRACE ${10 - updated.elapsedSeconds}S — ABORT BOOKED BY ROUTINE · TAP TO OPEN"
                    } else {
                        "GRACE ${10 - updated.elapsedSeconds}S — CANCEL FREE · TAP TO OPEN"
                    }
                } else {
                    // Issue #155 (IQ-2 M-8): the counter carries ONE
                    // name in both scopes — "urges stopped", as the
                    // bigText below already prints.
                    "$timeFormatted • ${updated.phantomReachesBlocked} urges stopped"
                }
                // Issue #155 (WING-F): a blank intention names no
                // session — the title drops the segment instead of
                // carrying a dangling separator.
                val standingTitle = if (updated.intention.isBlank()) {
                    "Monolith"
                } else {
                    "Monolith • ${updated.intention}"
                }
                // Merge composition with #337 (from main): the
                // repair actions are probed at build time inside
                // buildNotification, so the missing-grant set is
                // part of the guard key — a grant lost mid-session
                // re-posts on the very next iteration and its
                // repair button appears with the truth, exactly as
                // the unguarded loop behaved.
                val repairKey = missingRepairActions(
                    accessibilityBound = MonolithAccessibilityService.instance != null,
                    usageAccessGranted = hasUsageAccessNow()
                ).joinToString(",") { it.name }
                if (!protecting) {
                    // Issue #155 gate (3a) composed with #69's
                    // guard: the arming card is a guarded post too.
                    // Its key differs from every standing key, so a
                    // demote posts it once, the loop does not spam
                    // it per iteration, and a later re-flip re-posts
                    // the standing card (its key differs from the
                    // arming key the guard last recorded).
                    if (notificationGuard.shouldPost(
                            shieldNotificationKey("Monolith", "Arming shield\u2026", null, null, repairKey)
                        )
                    ) {
                        updateNotification("Monolith", "Arming shield\u2026")
                    }
                } else if (notificationGuard.shouldPost(
                        shieldNotificationKey(standingTitle, standingSubtitle, bigText, badgeNumber, repairKey)
                    )
                ) {
                    updateNotification(
                        title = standingTitle,
                        subtitle = standingSubtitle,
                        bigText = bigText,
                        chronometerEndMillis = chronometerEndMillis,
                        badgeNumber = badgeNumber,
                        inSafeBreak = updated.isInSafeBreak
                    )
                }

                // Watch Store (chrome C3): ONE publication per
                // iteration — the engine heartbeat plus a re-read of
                // the two BIND items' present state (and, #81 F4,
                // this iteration's USAGE ACCESS measurement).
                WatchStore.onShieldIteration(protecting, sightState, lastVerifiedWallMillis, usageGranted)

                val a11yForeground = if (MonolithAccessibilityService.instance != null) {
                    MonolithAccessibilityService.foregroundPackage
                } else {
                    null
                }
                // Issue #332 F2: a live call no longer suspends
                // this door wholesale (the pre-cure shape made
                // every call a complete enforcement holiday on
                // both paths, uninstall guard included). Selection
                // always runs; the call's own surfaces are exempt
                // per-package through the safety floor (dialer /
                // incallui / telecom), and every other blocked app
                // stays blocked mid-call — Flipd's construction.
                // The VoIP-owner exemption lives in the
                // accessibility door (shouldBypassForCall); this
                // fallback door fails closed, as a fallback must.
                val selectedForeground = ForegroundSelection
                    .selectBlockedForeground(
                        accessibilityForeground = a11yForeground,
                        blockedPackages = blockedPackages,
                        usageStatsFallback = { probe.blockedPackage }
                    )
                    ?.takeUnless { com.example.data.SafetyFloor.isFloorPackage(it) }
                // The flip + the one WatchStore publication for
                // this beat live at the composed latch above
                // (FIX-R2, PR #388 recomposition): the latch's
                // doors AND this beat's sight gate it together.

                // Issue #290 F5: a dark/locked screen vetoes the
                // verdict (see screenInteractive above).
                val foregroundBlockedPkg = if (screenInteractive) selectedForeground else null
                if (foregroundBlockedPkg != null && foregroundBlockedPkg != applicationContext.packageName &&
                    // Audit R6 #340 F63: the cross-door episode
                    // claim — if the accessibility door already
                    // fired on this opening, this door stands down
                    // (and vice versa). One opening, one intercept.
                    InterceptClaim.tryClaim(foregroundBlockedPkg)
                ) {
                    val blockedApp = iterationApps.appFor(foregroundBlockedPkg)
                    // ISSUE-67: intercept intent via the seam (no
                    // MainActivity import; the seam mints the #68
                    // nonce and the #78 firing id). This door's
                    // blocked set already excludes a leased PEEK
                    // app (the filter above), so a lease sold here
                    // is honoured (UNA2-01) — InterceptPath.SHIELD_APP,
                    // as before.
                    // Issue #339 (Second-pass F2): eject first,
                    // via the accessibility service when it is
                    // bound (GLOBAL_ACTION_* exists only there);
                    // unbound, this door cannot eject and the
                    // launch below stands alone, as before.
                    MonolithAccessibilityService.instance
                        ?.ejectForShield(foregroundBlockedPkg)
                    // Issue #94 (F2): never a bare background launch —
                    // a dropped launch stamps itself and falls back
                    // to a tappable notification (InterceptLaunch).
                    InterceptLaunch.launch(
                        applicationContext,
                        InterceptIntents.interceptIntent(
                            applicationContext, foregroundBlockedPkg,
                            blockedApp?.appName ?: "Blocked App",
                            blockedApp?.resolvedTier?.badge ?: "Shut",
                            InterceptPath.SHIELD_APP
                        ),
                        blockedApp?.appName ?: "Blocked App"
                    )
                }

                // Audit R6 #340 F25: the tick's wake lock is
                // released before the cadence sleep — it is never
                // held across a delay. (The catch path below skips
                // this release; the lock's 15 s timeout backstop
                // and onDestroy's release bound that case.)
                releaseTickWakeLock()
                // Issue #332 F1: 1-second cadence, matching the
                // UsageEvents window the fallback door now reads.
                delay(1000L)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "monitor iteration $iteration failed; loop continues", e)
                    protecting = false
                    protectingSessionId = -1
                    protectingMono = 0L
                    sightState = SightState.BLIND_NO_ACCESS
                    sightMono = 0L
                    lastVerifiedWallMillis = null
                    WatchStore.onShieldIteration(protecting = false)
                    // Issue #155 (WING-F, gate completeness): the
                    // exception demoted the latch — the full session
                    // card from the last good iteration must not
                    // stay on screen claiming protection. Replace
                    // it with the honest arming card and refresh
                    // the widget on the falling edge.
                    runCatching {
                        updateNotification("Monolith", "Arming shield\u2026")
                        MonolithWidgetProvider.requestUpdate(applicationContext)
                    }
                    // One cadence before the next attempt. If the
                    // scope was cancelled during this wait, the
                    // CancellationException from delay propagates —
                    // it is thrown from the catch block, not caught
                    // by it.
                    delay(2000L)
                }
            }
        }
    }

    /**
     * Local stop on the live instance (issue #43, finding 5): the
     * fallback when stopShield's service dispatch itself fails.
     * Performs exactly the ACTION_STOP_SHIELD branch's work.
     */
    internal fun requestLocalStop() {
        protecting = false
        protectingSessionId = -1
        protectingMono = 0L
        sightState = SightState.BLIND_NO_ACCESS
        sightMono = 0L
        lastVerifiedWallMillis = null
        SessionMirror.clear(this)
        WatchStore.onShieldIteration(protecting = false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        // Issue #155 (WING-F): a local stop is a shield-down
        // moment exactly like the STOP dispatch — the widget
        // refreshes now, not at the 30-minute cadence.
        MonolithWidgetProvider.requestUpdate(applicationContext)
        stopSelf()
    }

    /**
     * Task removal (issue #43, finding 4): START_STICKY alone was
     * the whole resurrection plan. When the user swipes the app
     * away mid-session, schedule a near-immediate one-shot alarm
     * that re-dispatches the shield start, in addition to
     * START_STICKY. Only a protecting shield reschedules — a
     * stopped shield stays stopped. Force-stop recovery is NOT
     * claimed: after a force-stop no code runs until the user
     * relaunches, by platform design.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        if (ShieldRestartPolicy.shouldScheduleRestart(protecting)) {
            scheduleRestartAlarm()
        }
        super.onTaskRemoved(rootIntent)
    }

    private fun scheduleRestartAlarm() {
        try {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            if (alarmManager == null) {
                Log.e(TAG, "onTaskRemoved: no AlarmManager; cannot schedule shield restart")
                return
            }
            val intent = Intent(applicationContext, MonolithShieldService::class.java).apply {
                action = ACTION_START_SHIELD
            }
            val pending = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(
                    applicationContext, RESTART_REQUEST_CODE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getService(
                    applicationContext, RESTART_REQUEST_CODE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + ShieldRestartPolicy.RESTART_DELAY_MS,
                pending
            )
        } catch (e: Exception) {
            Log.e(TAG, "onTaskRemoved: scheduling shield restart failed", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // Issue #155 (F4): channels are immutable on-device, so
            // the badge fix cannot flip the legacy channel's
            // setShowBadge(false) in place. Create the v2 channel
            // with the badge ON and delete the legacy id — existing
            // installs migrate on the shield's next start.
            manager.deleteNotificationChannel(LEGACY_CHANNEL_ID)
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
                setShowBadge(true)
            }
            manager.createNotificationChannel(channel)
        }
    }

    private fun createBreakAlertChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                BREAK_ALERT_CHANNEL_ID,
                "Safe Break announcements",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Heads-up alerts when a Safe Break starts, nears its end, and ends"
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
    }

    private fun breakClockText(totalSeconds: Int): String {
        val s = totalSeconds.coerceAtLeast(0)
        return String.format("%d:%02d", s / 60, s % 60)
    }

    /**
     * One heads-up break announcement: its own channel + id (it
     * never touches the persistent shield notification), auto-cancel
     * and a 90 s timeout so a stale alert never lingers into the
     * next block pretending to be news. Information only — tapping
     * it opens the app; it is never an exit from anything.
     *
     * Issue #104 (STURM3-20 Feel verdict, one voice per moment):
     * this post carries NO vibration of its own. The repository
     * already spoke at this boundary in this same loop iteration —
     * the mechanical latch at break start, the two-strike warning
     * knock at the final minute — and every vibrate() cancels the
     * previous one, so the old raw one-shot buzz (180/260 ms)
     * erased the designed knock within milliseconds. The visual
     * posts; the repository knock is the boundary's sole voice.
     */
    private fun postBreakAlert(title: String, text: String) {
        try {
            val pendingIntent = PendingIntent.getActivity(
                this,
                1,
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notification = NotificationCompat.Builder(this, BREAK_ALERT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setAutoCancel(true)
                .setTimeoutAfter(BREAK_ALERT_TIMEOUT_MS)
                .setContentIntent(pendingIntent)
                .build()
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(BREAK_ALERT_NOTIFICATION_ID, notification)
        } catch (_: Exception) {
            // Announcement is best-effort; protection never depends on it.
        }
    }


    /** #337 F2: is Usage Access granted to this app right now?
     *  (AppOps verdict, the same probe the repository uses.) */
    private fun hasUsageAccessNow(): Boolean {
        return try {
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as? android.app.AppOpsManager
                ?: return false
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    packageName
                )
            }
            mode == android.app.AppOpsManager.MODE_ALLOWED
        } catch (_: Exception) {
            false
        }
    }

    private fun repairPendingIntent(action: RepairAction): PendingIntent {
        val intent = when (action) {
            RepairAction.ACCESSIBILITY ->
                Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
            RepairAction.USAGE_ACCESS ->
                Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
        }.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        return PendingIntent.getActivity(
            this,
            10 + action.ordinal,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun buildNotification(
        title: String,
        subtitle: String,
        ongoing: Boolean = true,
        autoCancel: Boolean = false,
        timeoutAfterMs: Long = 0,
        bigText: String? = null,
        chronometerEndMillis: Long? = null,
        badgeNumber: Int? = null,
        inSafeBreak: Boolean = false
    ): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            // ISSUE-67: via the seam, same SINGLE_TOP semantics —
            // composed with issue #155 (F5): a tap on the shield
            // card lands on the SHIELD destination, not the default
            // Deck; MainActivity routes on this extra when no
            // intercept extras are present.
            InterceptIntents.notificationIntent(this).apply {
                putExtra(EXTRA_FROM_SHIELD_NOTIFICATION, true)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // #337 F2's grant probe, computed BEFORE the public
        // version so the lock-screen form can honour it (the repair
        // actions below consume the same read).
        val missing = missingRepairActions(
            accessibilityBound = MonolithAccessibilityService.instance != null,
            usageAccessGranted = hasUsageAccessNow()
        )

        // Issue #274 (R5 Platform): lock-screen redaction. The live
        // title/subtitle carry the user's private intention text and
        // urge count — on a locked phone anyone holding it could read
        // both. VISIBILITY_PRIVATE + a generic public version.
        // Issue #155 (IQ-5 F-2, deepened by IQ-2 M-1): the public
        // version is STATE-MATCHED, mirroring the private card —
        // it used to be one hardcoded ongoing "Focus session
        // protected" attached to every card: F2's claim verbatim
        // on the lock screen during arming, and on the completion
        // card a public form that never expired (the F3 ghost,
        // lock-screen edition). Now: completion -> not ongoing,
        // the same timeout, "Session complete"; arming -> the
        // non-claim line; "Focus session protected" only on the
        // post-flip standing card it describes.
        // Issue #155 (LEAD-4 A7, WING-F stack): state-matching by
        // subtitle alone still let two cards claim protection on
        // the lock screen — a DEGRADED standing card (a grant
        // missing, its own face saying blocking is OFF) and any
        // standing card re-posted while the witness was down
        // (battery refresh after a failed iteration). The public
        // version is now gated on the SAME protecting truth as
        // the card it shadows: no standing witness, or a missing
        // grant, means no protection claim — ever.
        // Issue #155 (IQ-3 reseat, re-close bench): the same gate
        // left one face unmatched — a Safe Break. During a break
        // the witness stands and no grant is missing, so the old
        // chain fell through to "Focus session protected" while
        // PEEK apps were lawfully open: a whole claim for a partial
        // seal, on the lock screen. The break now has its own
        // branch in shieldPublicText, fed by the explicit
        // inSafeBreak this builder is handed by the loop's break
        // branch (and by the battery re-post's remembered state) —
        // never by a subtitle substring.
        val publicText = shieldPublicText(
            ongoing = ongoing,
            arming = subtitle.contains("Arming shield"),
            protecting = protecting,
            missingCount = missing.size,
            inSafeBreak = inSafeBreak
        )
        val publicVersion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Monolith")
            .setContentText(publicText)
            .setOngoing(ongoing)
            .apply {
                if (timeoutAfterMs > 0) setTimeoutAfter(timeoutAfterMs)
            }
            .setContentIntent(pendingIntent)
            .build()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setOngoing(ongoing)
            .setAutoCancel(autoCancel)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicVersion)
            .setContentIntent(pendingIntent)
        if (timeoutAfterMs > 0) {
            builder.setTimeoutAfter(timeoutAfterMs)
        }
        if (bigText != null) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
        }
        if (chronometerEndMillis != null) {
            // The countdown is the system's chronometer (real time),
            // not the monitor loop's 1-second text snapshot.
            builder.setUsesChronometer(true)
            builder.setChronometerCountDown(true)
            builder.setWhen(chronometerEndMillis)
            builder.setShowWhen(true)
        }
        if (badgeNumber != null) {
            builder.setNumber(badgeNumber)
        }

        // #337 F2: when a grant the session depends on is missing
        // MID-SESSION, the one surface the user can always reach —
        // this notification — becomes the repair tool: a big-text
        // warning naming what is missing plus one action button
        // per grant, deep-linking to the exact settings screen.
        // Grants are probed at render time, so the actions appear
        // and disappear with the truth. (The probe itself moved
        // above the public version — A7 — so both read one truth.)
        if (missing.isNotEmpty()) {
            // Issue #155 (IQ-2 M-8): the warning APPENDS to the
            // card's normal bigText — it never replaces it (the
            // session's own status line stays readable under the
            // warning). Only a card with no bigText of its own
            // (the arming card) builds the warning on its subtitle.
            val warning = buildString {
                append(bigText ?: subtitle)
                append(" — Missing: ")
                append(
                    missing.joinToString(", ") {
                        when (it) {
                            RepairAction.ACCESSIBILITY -> "Accessibility (blocking is OFF)"
                            RepairAction.USAGE_ACCESS -> "Usage access (off — Monolith may miss app launches)"
                        }
                    }
                )
            }
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(warning))
            builder.setContentText(warning)
            for (action in missing) {
                builder.addAction(0, action.label, repairPendingIntent(action))
            }
        }
        // Issue #274: the standing notification was a dead label -
        // no actions at all. One explicit action: open Monolith.
        builder.addAction(0, "Open Monolith", pendingIntent)
        // Issue #274: a live countdown must be a system chronometer,
        // not hand-rendered static text re-posted every second. When
        // the caller passed no explicit end (#155 seam above), fall
        // back to the mirror: when it knows the session's end
        // wall-clock, the system itself ticks the countdown -
        // accurate on the lock screen and AOD even between
        // shield-loop iterations.
        if (chronometerEndMillis == null) {
            val endWallMs = SessionMirror.read(this).endWallMs
            if (endWallMs > System.currentTimeMillis()) {
                builder.setWhen(endWallMs)
                builder.setUsesChronometer(true)
                builder.setChronometerCountDown(true)
                builder.setShowWhen(true)
            }
        }
        return builder.build()
    }

    /** Issue #122: the battery segment, appended to a base
     *  subtitle when the battery cannot keep the shield's
     *  promise. The words state the mechanism plainly:
     *  protection ends with the phone. No risk \u2192 no segment. */
    private fun withBatteryWarning(subtitle: String): String {
        val warning = batteryShieldWarning(readBatterySnapshot(applicationContext))
            ?: return subtitle
        return "$subtitle \u2022 $warning"
    }

    private fun updateNotification(
        title: String,
        subtitle: String,
        ongoing: Boolean = true,
        autoCancel: Boolean = false,
        timeoutAfterMs: Long = 0,
        bigText: String? = null,
        chronometerEndMillis: Long? = null,
        badgeNumber: Int? = null
    ) {
        // Issue #122: only the standing (ongoing) protection
        // promise is remembered for battery re-posts \u2014 a detached
        // completion card is not a promise and is never revived
        // by a battery broadcast.
        if (ongoing) {
            lastBaseTitle = title
            lastBaseSubtitle = subtitle
        }
        val warnedSubtitle = withBatteryWarning(subtitle)
        val warnedBigText = when {
            bigText != null -> withBatteryWarning(bigText)
            warnedSubtitle != subtitle -> warnedSubtitle
            else -> null
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(
            NOTIFICATION_ID,
            buildNotification(
                title, warnedSubtitle, ongoing, autoCancel, timeoutAfterMs,
                warnedBigText, chronometerEndMillis, badgeNumber, inSafeBreak
            )
        )
    }

    /** Audit R3b #146: post an ALERTING summons on the HIGH channel.
     *  Distinct id from the standing foreground notification, not
     *  ongoing, not only-alert-once — it arrives like news, because
     *  for the user who walked away, it is. */
    private fun postSummons(title: String, text: String, timeoutAfterMs: Long = 0) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SUMMONS_CHANNEL_ID,
                "Session summons",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Break-over and session-end calls back to the desk"
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            // ISSUE-67: via the seam, same SINGLE_TOP semantics
            // (this summons site landed with #146 after the seam).
            InterceptIntents.notificationIntent(this),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, SUMMONS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .apply {
                // Issue #155 (3b): a timed summons expires like the
                // F3 completion card instead of ghosting.
                if (timeoutAfterMs > 0) setTimeoutAfter(timeoutAfterMs)
            }
            .build()
        manager.notify(SUMMONS_NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Issue #324 (Audit R5, Zario): the Recents-swipe resurrection
    // override lives earlier in this file — main landed it via
    // issue #43 as the ShieldRestartPolicy alarm re-dispatch while
    // this lane was in flight, so this lane's own duplicate
    // override was removed at merge reconciliation. What this lane
    // adds on top is the manifest surface: stopWithTask=false and
    // MainActivity excludeFromRecents, pinned by
    // ShieldResurrectionTest.

    override fun onDestroy() {
        if (screenReceiverRegistered) {
            try {
                unregisterReceiver(screenStateReceiver)
            } catch (_: Exception) {
            }
            screenReceiverRegistered = false
        }
        releaseTickWakeLock()
        runCatching { unregisterReceiver(batteryReceiver) }
        if (instance === this) {
            instance = null
        }
        protecting = false
        protectingSessionId = -1
        protectingMono = 0L
        sightState = SightState.BLIND_NO_ACCESS
        sightMono = 0L
        lastVerifiedWallMillis = null
        SessionMirror.clear(this)
        WatchStore.onShieldIteration(protecting = false)
        // Issue #155 (WING-F): the shield is gone — the widget
        // refreshes now, not at the 30-minute cadence, so a stale
        // SEALED cannot outlive the service that proved it.
        MonolithWidgetProvider.requestUpdate(applicationContext)
        // Issue #143: a dying shield takes its dim phase with it.
        publishBreakDim(BreakDimState())
        monitorJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        // Issue #28: the instance's presence as a FLOW, so waiters
        // (the finish detention) suspend on the flip instead of
        // busy-polling the raw field every 25ms.
        private val _instancePresent = MutableStateFlow(false)
        val instancePresent: StateFlow<Boolean> = _instancePresent.asStateFlow()

        // Issue #143 F1/F6: the break-dim phase — the end-of-break
        // signal the finding found missing. The shield loop is the
        // only writer (publishBreakDim); the UI reads it for the
        // dimming progression INTO the break and the fade OUT at
        // break end. In-memory only, never persisted.
        private val _breakDim = MutableStateFlow(BreakDimState())
        val breakDim: StateFlow<BreakDimState> = _breakDim.asStateFlow()

        internal fun publishBreakDim(state: BreakDimState) {
            _breakDim.value = state
        }

        // Live Wire registry (Ines): in-memory only, never persisted.
        @Volatile
        var instance: MonolithShieldService? = null
            internal set(value) {
                field = value
                _instancePresent.value = value != null
            }

        /** True only after startForeground succeeded AND a full
         *  monitor iteration ran. The Live Wire reads this as a
         *  post-latch cascade link and a strip fact - never as a
         *  pre-arm conjunct (the shield starts WITH the session). */
        @Volatile
        var protecting: Boolean = false
            internal set

        /** Cascade witness (Slice D step 4, Opal L2): the session
         *  id the first full iteration read when `protecting`
         *  flipped, and the monotonic instant of the flip. -1 / 0
         *  when no iteration has flipped under the current start.
         *  Reset on every start and stop so the witness can only
         *  ever attest the latched session's own loop. */
        @Volatile
        var protectingSessionId: Int = -1
            internal set

        @Volatile
        var protectingMono: Long = 0L
            internal set

        /** Shield Rethink 2, Step 1 (Door A): the sight verdict of
         *  the loop's latest beat, the mono it was taken at, and the
         *  wall instant of the latest SIGHTED beat under a live
         *  session. Reset wherever `protecting` is reset: the stamp
         *  may only ever attest the current start's own loop. */
        @Volatile
        var sightState: SightState = SightState.BLIND_NO_ACCESS
            internal set

        @Volatile
        var sightMono: Long = 0L
            internal set

        @Volatile
        var lastVerifiedWallMillis: Long? = null
            internal set

        /** Audit R6 #340 F25: the tick wake lock's hard timeout —
         *  a pass that wedges releases the CPU within 15 s, and a
         *  lock is never held across the cadence gap. */
        const val TICK_WAKE_LOCK_TIMEOUT_MS = 15_000L
        const val TAG = "MonolithShield"
        const val RESTART_REQUEST_CODE = 0x5211
        // Issue #155 (F4): v2 carries setShowBadge(true); the legacy
        // id is deleted in createNotificationChannel because channels
        // are immutable once created on a device.
        const val CHANNEL_ID = "monolith_shield_channel_v2"
        const val LEGACY_CHANNEL_ID = "monolith_shield_channel"
        const val NOTIFICATION_ID = 4096
        const val BREAK_ALERT_CHANNEL_ID = "monolith_break_alert_channel"
        // 4098, NOT 4097: #146's summons owns 4097 on its own channel;
        // sharing the id would make break alerts and summonses
        // overwrite each other in the shade.
        const val BREAK_ALERT_NOTIFICATION_ID = 4098
        /** A break alert self-expires after 90 s (see postBreakAlert). */
        const val BREAK_ALERT_TIMEOUT_MS = 90_000L
        /** Issue #155 (F3): the detached completion card self-expires. */
        const val COMPLETION_TIMEOUT_MS = 45 * 60 * 1000L
        /** Issue #155 (IQ-2 VC-4): the BREAK OVER summons
         *  self-expires after 10 minutes — its news (the next
         *  block has started) is shorter-lived than the
         *  completion receipt's. */
        const val BREAK_OVER_TIMEOUT_MS = 600_000L

        /**
         * Issue #155: the shield card's LOCK-SCREEN (public version)
         * line — the one sentence about the session a stranger
         * holding the phone may read. Pure, so the whole state table
         * is pinned by plain unit tests (Issue155PublicTextTest);
         * buildNotification feeds it the live probes.
         *
         * The claim "Focus session protected" is earned by exactly
         * one state: ongoing, past arming, the protecting witness
         * standing, no grant missing, and NOT in a Safe Break. A
         * break is a partial seal — PEEK apps are lawfully open —
         * so the break prints its own line and never the whole
         * claim (IQ-3 reseat consult, re-close bench). Every
         * not-yet / no-longer / degraded state prints the non-claim
         * line; a finished session prints its completion line.
         */
        fun shieldPublicText(
            ongoing: Boolean,
            arming: Boolean,
            protecting: Boolean,
            missingCount: Int,
            inSafeBreak: Boolean
        ): String = when {
            !ongoing -> "Session complete"
            arming -> "Monolith session in progress"
            !protecting -> "Monolith session in progress"
            missingCount > 0 -> "Monolith session in progress"
            inSafeBreak -> "Monolith \u2014 on a break \u00b7 peek apps open"
            else -> "Focus session protected"
        }
        /** Issue #155 (F5): present (true) on the shield card's content
         *  intent; MainActivity routes such a launch to SHIELD. */
        const val EXTRA_FROM_SHIELD_NOTIFICATION = "com.example.EXTRA_FROM_SHIELD_NOTIFICATION"
        // Audit R3b #146: the summons channel — HIGH importance, its
        // own notification id, used ONLY for the two moments the user
        // is away from the phone by design (break expiry, session
        // end). The standing countdown stays on the silent LOW
        // channel; a summons must be capable of being heard.
        const val SUMMONS_CHANNEL_ID = "monolith_summons_channel"
        const val SUMMONS_NOTIFICATION_ID = 4097
        const val ACTION_START_SHIELD = "com.example.action.START_SHIELD"
        const val ACTION_STOP_SHIELD = "com.example.action.STOP_SHIELD"
        const val EXTRA_INTERCEPTED_PACKAGE = "extra_intercepted_package"
        const val EXTRA_INTERCEPTED_APP_NAME = "extra_intercepted_app_name"
        const val EXTRA_INTERCEPT_BADGE = "extra_intercept_badge"
        /** The firing door's [InterceptPath] name (Truth Car 3,
         *  UNA2-01): the badge says WHAT stopped him; the path says
         *  WHICH door — and only lease-honouring doors may sell a
         *  peek. Written by both intercept launchers. */
        const val EXTRA_INTERCEPT_PATH = "extra_intercept_path"
        /** Issue #78: the firing's unique id (UUID, minted by the
         *  firing door). MainActivity persists consumption of this
         *  id BEFORE showing the Airlock; the platform-retained
         *  intent redelivered after process death carries the same
         *  id and is dropped as already consumed. */
        const val EXTRA_INTERCEPT_ID = "extra_intercept_id"

        /** Issue #68 F5, Tier 2: the single-use provenance nonce
         *  (see [InterceptNonce]). Written by both intercept
         *  launchers; required by MainActivity before any
         *  intercept extra is honoured. */
        const val EXTRA_INTERCEPT_NONCE = "extra_intercept_nonce"

        fun startShield(context: Context) {
            try {
                val intent = Intent(context, MonolithShieldService::class.java).apply {
                    action = ACTION_START_SHIELD
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                // Issue #43, finding 3: failures are LOGGED, never
                // swallowed — a silent catch here hid real FGS
                // start failures from every caller (boot included).
                // The catch itself stays: a start helper must not
                // crash its caller (e.g. a boot receiver).
                Log.e(TAG, "startShield dispatch failed", e)
            }
        }

        fun stopShield(context: Context) {
            try {
                val intent = Intent(context, MonolithShieldService::class.java).apply {
                    action = ACTION_STOP_SHIELD
                }
                // Issue #43, finding 5: dispatch like startShield —
                // startForegroundService on API 26+. The old plain
                // startService threw IllegalStateException from the
                // background, the catch swallowed it, and the stop
                // silently never reached the service.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "stopShield dispatch failed; trying direct local stop", e)
                // Direct local-stop fallback on the live instance:
                // if the dispatch cannot reach the service, stop
                // the instance we can already see.
                try {
                    val live = instance
                    if (live != null) {
                        live.requestLocalStop()
                    } else {
                        Log.e(TAG, "stopShield: no live instance for local-stop fallback")
                    }
                } catch (fallbackError: Exception) {
                    Log.e(TAG, "stopShield local-stop fallback failed", fallbackError)
                }
            }
        }
    }
}
