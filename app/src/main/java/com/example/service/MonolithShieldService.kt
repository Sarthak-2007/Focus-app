package com.example.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.R
import com.example.data.AppBlockTier
import com.example.data.MonolithDatabase
import com.example.MLog
import java.util.UUID
import kotlinx.coroutines.CancellationException
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

class MonolithShieldService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var monitorJob: Job? = null

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
                WatchStore.onShieldIteration(protecting = false)
                stopForeground(STOP_FOREGROUND_REMOVE)
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
        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                // No blocking claim in the birth notification
                // (issue #7, KRITIK-1R attack 4): "Protecting..." here
                // would assert enforcement before the first monitor
                // iteration has run — the loop's own per-iteration
                // notification replaces this within one pass, and if
                // the loop wedges, this text must not go on claiming.
                buildNotification("Monolith", "Starting the session checks…"),
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
            WatchStore.onShieldIteration(protecting = false)
            stopSelf()
            return false
        }
        // Live Wire registry (Ines): the instance exists only once
        // startForeground has actually succeeded - no instance, no
        // flag, no rumour.
        instance = this
        // Main's WatchStore.bind (Live Wire registry) preserved:
        // binding is idempotent and every other door binds on entry.
        WatchStore.bind(applicationContext)
        return true
        WatchStore.bind(applicationContext)
    }

    private fun startMonitoringLoop() {
        monitorJob?.cancel()
        // ISSUE-67: construction via the single point; the DAO
        // comes from the same container for the loop's remaining
        // direct reads.
        val dao = com.example.AppContainer.dao(applicationContext)
        val repository = com.example.AppContainer.repository(applicationContext)

        monitorJob = serviceScope.launch {
            var iteration = 0
            // Audit R3b #146: break-transition witness for the
            // summons — the loop remembers the last tick's break
            // state so the flip out of a break posts exactly once.
            var wasInSafeBreak = false
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
                }
                val activeSession = repository.activeSessionSnapshot() // ISSUE-67(d): via repository, not direct DAO
                if (activeSession == null || !activeSession.isActive) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    break
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
                val updated = repository.updateSessionProgress(activeSession, stampShieldBeat = true)
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
                        "Your session has ended. The receipt is on the Deck."
                    )
                    // R3b #164 F2: the low notification's old line
                    // promised an "Unlocked desk toy" in a "Break
                    // tab" — neither exists anywhere in the app.
                    updateNotification("Session Complete", "Your receipt is waiting in the Ledger.")
                    stopForeground(STOP_FOREGROUND_DETACH)
                    stopSelf()
                    break
                }

                // Audit R3b #146: the break just expired under a
                // user who is away by design — summon them on the
                // HIGH channel as the next block starts, once.
                if (wasInSafeBreak && !updated.isInSafeBreak) {
                    postSummons(
                        "BREAK OVER",
                        "Block ${updated.currentBlockIndex} of ${updated.totalStudyBlocks} has started."
                    )
                }
                wasInSafeBreak = updated.isInSafeBreak

                val timeFormatted = when {
                    updated.isInSafeBreak -> {
                        val bMin = updated.safeBreakSecondsRemaining / 60
                        val bSec = updated.safeBreakSecondsRemaining % 60
                        String.format("Safe Break %02d:%02d • Block %d/%d", bMin, bSec, updated.currentBlockIndex, updated.totalStudyBlocks)
                    }
                    updated.isStopwatchMode -> {
                        val mins = updated.elapsedSeconds / 60
                        val secs = updated.elapsedSeconds % 60
                        String.format("Stopwatch +%02d:%02d (20m floor)", mins, secs)
                    }
                    else -> {
                        val effectiveTarget = updated.targetDurationSeconds + updated.penaltySecondsAdded
                        val remainingSec = (effectiveTarget - updated.elapsedSeconds).coerceAtLeast(0)
                        val mins = remainingSec / 60
                        val secs = remainingSec % 60
                        String.format("%02d:%02d left • Block %d/%d", mins, secs, updated.currentBlockIndex, updated.totalStudyBlocks)
                    }
                }

                // Audit R3b #149: the grace window exists off the
                // Deck too. During the window the shield's own
                // notification counts it down — its tap already
                // opens the app to the Deck chip — and for a
                // routine-born row it names the booking instead of
                // promising free.
                if (updated.elapsedSeconds <= 10 && !updated.isInSafeBreak) {
                    updateNotification(
                        title = "Monolith • ${updated.intention}",
                        subtitle = if (updated.routineId != null) {
                            "GRACE ${10 - updated.elapsedSeconds}S — ABORT BOOKED BY ROUTINE · TAP TO OPEN"
                        } else {
                            "GRACE ${10 - updated.elapsedSeconds}S — CANCEL FREE · TAP TO OPEN"
                        }
                    )
                } else {
                    updateNotification(
                        title = "Monolith • ${updated.intention}",
                        subtitle = "$timeFormatted • ${updated.phantomReachesBlocked} reaches blocked"
                    )
                }

                val nowMono = SystemClock.elapsedRealtime()
                val blockedPackages = repository.allAppsSnapshot() /* ISSUE-67(d): via repository */
                    .filter { app ->
                        val tier = app.resolvedTier
                        // Issue #140: the daily limit is real. An app
                        // whose today's usage has reached its limit is
                        // blocked whatever its tier — except inside a
                        // lawful peek already running (Safe Break or a
                        // live 2-minute lease), which is never yanked
                        // mid-peek. Usage is the reconciled
                        // todayUsageMinutes on the row.
                        val overLimit = app.dailyLimitMinutes > 0 &&
                            app.todayUsageMinutes >= app.dailyLimitMinutes
                        when {
                            updated.isInSafeBreak && tier == AppBlockTier.PEEK_2M -> false
                            tier == AppBlockTier.PEEK_2M && nowMono < app.activePeekUntilElapsedRealtime -> false
                            overLimit -> true
                            tier == AppBlockTier.OPEN -> false
                            else -> true
                        }
                    }
                    .map { it.packageName }
                    .toSet()

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
                val expiredPeeks = com.example.data.expiredPeekPackages(
                    dao.getAllAppsSnapshot(), nowMono
                )
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
                        usageStatsFallback = { repository.queryForegroundBlockedApp(blockedPackages, expiredPeeks) }
                    )
                    ?.takeUnless { com.example.data.SafetyFloor.isFloorPackage(it) }
                // Issue #290 F5: a dark/locked screen vetoes the
                // verdict (see screenInteractive above).
                val foregroundBlockedPkg = if (screenInteractive) selectedForeground else null
                if (foregroundBlockedPkg != null && foregroundBlockedPkg != applicationContext.packageName) {
                    val blockedApp = repository.allAppsSnapshot() /* ISSUE-67(d): via repository */.find { it.packageName == foregroundBlockedPkg }
                    // ISSUE-67: intercept intent via the seam (no
                    // MainActivity import; the seam mints the #68
                    // nonce and the #78 firing id). This door's
                    // blocked set already excludes a leased PEEK
                    // app (the filter above), so a lease sold here
                    // is honoured (UNA2-01) — InterceptPath.SHIELD_APP,
                    // as before.
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

                // Live Wire: the shield is PROTECTING only once a
                // full monitor iteration has run under a live
                // session - started is not protecting. A full
                // iteration means: blocked set built, foreground
                // queried, and the intercept launched if one was
                // due — everything above this line. The flip used
                // to sit before the blocked set was built, so the
                // witness (and the status sentence, and the seal
                // cascade reading the same stamp) could attest a
                // pass that had enforced nothing (critic Nagel
                // N-3). It sits here now, after the enforcement
                // act and before the cadence sleep — the 2s delay
                // is cadence, not iteration work, and the cascade's
                // annul deadline cannot wait it out. The cascade
                // witness (Opal L2) is stamped at the same flip:
                // the session this iteration read, and the mono it
                // flipped - CONFIRMED stamp for SHIELD-PROTECTING.
                // The guard re-stamps on a session change, not only
                // on the first flip (KRITIK-1R attack 8): if the
                // loop ever crosses to a new session id without a
                // service restart, the witness must attest the NEW
                // session's first completed pass — a stamp frozen
                // on the dead session would strand the new one
                // UNPROVEN forever over a loop actively enforcing it.
                if (!protecting || protectingSessionId != updated.id) {
                    protecting = true
                    protectingSessionId = updated.id
                    protectingMono = SystemClock.elapsedRealtime()
                    WatchStore.poke()
                    // Issue #80 finding 4 (from main's composition):
                    // the session just armed — sweep notifications
                    // blocked apps posted BEFORE this moment through
                    // the listener's capture path. Main stamped this
                    // at its early flip; it rides the N-3 late flip
                    // here, the first moment protecting is true.
                    try {
                        MonolithNotificationListener.instance?.sweepActiveNotifications()
                    } catch (_: Exception) {}
                }
                // Watch Store (chrome C3): ONE publication per
                // iteration — the engine heartbeat plus a re-read of
                // the two BIND items' present state (and, #81 F4,
                // this iteration's USAGE ACCESS measurement).
                WatchStore.onShieldIteration(protecting, usageGranted)

                delay(2000L)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "monitor iteration $iteration failed; loop continues", e)
                    protecting = false
                    protectingSessionId = -1
                    protectingMono = 0L
                    WatchStore.onShieldIteration(protecting = false)
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
        WatchStore.onShieldIteration(protecting = false)
        stopForeground(STOP_FOREGROUND_REMOVE)
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
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, subtitle: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            // ISSUE-67: via the seam, same SINGLE_TOP semantics.
            InterceptIntents.notificationIntent(this),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Issue #274 (R5 Platform): lock-screen redaction. The live
        // title/subtitle carry the user's private intention text and
        // urge count — on a locked phone anyone holding it could read
        // both. VISIBILITY_PRIVATE + a generic public version: the
        // owner (unlocked) sees the real session line, the lock screen
        // sees only that Monolith is protecting a session.
        val publicVersion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Monolith")
            .setContentText("Focus session protected")
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicVersion)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun updateNotification(title: String, subtitle: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(title, subtitle))
    }

    /** Audit R3b #146: post an ALERTING summons on the HIGH channel.
     *  Distinct id from the standing foreground notification, not
     *  ongoing, not only-alert-once — it arrives like news, because
     *  for the user who walked away, it is. */
    private fun postSummons(title: String, text: String) {
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
            .build()
        manager.notify(SUMMONS_NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        if (instance === this) {
            instance = null
        }
        protecting = false
        protectingSessionId = -1
        protectingMono = 0L
        WatchStore.onShieldIteration(protecting = false)
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

        // Live Wire registry (Ines): in-memory only, never persisted.
        @Volatile
        var instance: MonolithShieldService? = null
            internal set(value) {
                field = value
                _instancePresent.value = value != null
            }

        /** True only after startForeground succeeded AND a full
         *  monitor iteration ran — full meaning the iteration's
         *  enforcement tail completed: blocked set built,
         *  foreground queried, intercept launched if due (the
         *  flip sits after that act, before the cadence delay).
         *  The Live Wire reads this as a post-latch cascade link
         *  and a strip fact - never as a pre-arm conjunct (the
         *  shield starts WITH the session). */
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
        const val TAG = "MonolithShield"
        const val RESTART_REQUEST_CODE = 0x5211
        const val CHANNEL_ID = "monolith_shield_channel"
        const val NOTIFICATION_ID = 4096
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
