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
import com.example.MainActivity
import com.example.R
import com.example.data.AppBlockTier
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
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

class MonolithShieldService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var monitorJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_SHIELD) {
            protecting = false
            protectingSessionId = -1
            protectingMono = 0L
            WatchStore.onShieldIteration(protecting = false)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        // Cascade witness reset (Slice D step 4, Opal L2): a fresh
        // start must re-prove protection for the session this loop
        // is about to read - a stale flip from a previous session
        // would let the cascade confirm a shield that has not yet
        // run one iteration over the latched row.
        protecting = false
        protectingSessionId = -1
        protectingMono = 0L

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
        // On failure: clear every witness, stopSelf, and return
        // START_NOT_STICKY so nothing advertises protection that
        // does not exist.
        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                buildNotification("Monolith Locked", "Protecting your focus session..."),
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
            return START_NOT_STICKY
        }
        // Live Wire registry (Ines): the instance exists only once
        // startForeground has actually succeeded - no instance, no
        // flag, no rumour.
        instance = this
        WatchStore.bind(applicationContext)
        WatchStore.poke()
        startMonitoringLoop()

        return START_STICKY
    }

    private fun startMonitoringLoop() {
        monitorJob?.cancel()
        val dao = MonolithDatabase.getInstance(applicationContext).monolithDao()
        val repository = MonolithRepository(applicationContext, dao)

        monitorJob = serviceScope.launch {
            var iteration = 0
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
                }
                val activeSession = dao.getActiveSessionSnapshot()
                if (activeSession == null || !activeSession.isActive) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    break
                }

                // Slice E (Nocke §2.2): the shield's own loop is
                // the beat's only lawful writer — its tick stamps
                // shieldLastBeatWallMillis into the row it already
                // persists, so the finish machine can later prove
                // whether the shield was present or gone.
                val updated = repository.updateSessionProgress(activeSession, stampShieldBeat = true)
                if (!updated.isActive) {
                    // Session-edge tick BEFORE standing down: a routine
                    // session that just timer-completed gets finalised
                    // by derive() now, not at some later wake.
                    RoutineHeartbeat.tickAsync(
                        applicationContext, RoutineHeartbeat.Source.SESSION_EDGE
                    )
                    updateNotification("Session Complete", "Unlocked desk toy ready in your Break tab.")
                    stopForeground(STOP_FOREGROUND_DETACH)
                    stopSelf()
                    break
                }

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

                updateNotification(
                    title = "Monolith • ${updated.intention}",
                    subtitle = "$timeFormatted • ${updated.phantomReachesBlocked} urges stopped"
                )

                // Live Wire: the shield is PROTECTING only once a
                // full monitor iteration has run under a live
                // session - started is not protecting. The cascade
                // witness (Opal L2) is stamped at the same flip:
                // the session this iteration read, and the mono it
                // flipped - CONFIRMED stamp for SHIELD-PROTECTING.
                if (!protecting) {
                    protecting = true
                    protectingSessionId = updated.id
                    protectingMono = SystemClock.elapsedRealtime()
                    WatchStore.poke()
                }
                // Watch Store (chrome C3): ONE publication per
                // iteration — the engine heartbeat plus a re-read of
                // the two BIND items' present state.
                WatchStore.onShieldIteration(protecting)

                val nowMono = SystemClock.elapsedRealtime()
                val blockedPackages = dao.getAllAppsSnapshot()
                    .filter { app ->
                        val tier = app.resolvedTier
                        when {
                            tier == AppBlockTier.OPEN -> false
                            updated.isInSafeBreak && tier == AppBlockTier.PEEK_2M -> false
                            tier == AppBlockTier.PEEK_2M && nowMono < app.activePeekUntilElapsedRealtime -> false
                            else -> true
                        }
                    }
                    .map { it.packageName }
                    .toSet()

                // Foreground detection (issue #43, finding 2):
                // prefer the accessibility service's real
                // TYPE_WINDOW_STATE_CHANGED witness when that
                // service is bound; UsageStats is only the fallback
                // when no witness exists (see ForegroundSelection).
                // The call bypass is checked here as well because
                // the witness path does not go through the
                // repository's UsageStats query that checked it
                // before — a call must never be blocked on either
                // path. The 2 s cadence is unchanged.
                val a11yForeground = if (MonolithAccessibilityService.instance != null) {
                    MonolithAccessibilityService.foregroundPackage
                } else {
                    null
                }
                val foregroundBlockedPkg = if (repository.isPhoneOrVoipCallActive()) {
                    null
                } else {
                    ForegroundSelection.selectBlockedForeground(
                        accessibilityForeground = a11yForeground,
                        blockedPackages = blockedPackages,
                        usageStatsFallback = { repository.queryForegroundBlockedApp(blockedPackages) }
                    )
                }
                if (foregroundBlockedPkg != null && foregroundBlockedPkg != applicationContext.packageName) {
                    val blockedApp = dao.getAllAppsSnapshot().find { it.packageName == foregroundBlockedPkg }
                    val interceptIntent = Intent(applicationContext, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra(EXTRA_INTERCEPTED_PACKAGE, foregroundBlockedPkg)
                        putExtra(EXTRA_INTERCEPTED_APP_NAME, blockedApp?.appName ?: "Blocked App")
                        putExtra(EXTRA_INTERCEPT_BADGE, blockedApp?.resolvedTier?.badge ?: "Locked")
                        // This door's blocked set already excludes a leased PEEK app
                        // (the filter above), so a lease sold here is honoured (UNA2-01).
                        putExtra(EXTRA_INTERCEPT_PATH, InterceptPath.SHIELD_APP.name)
                    }
                    startActivity(interceptIntent)
                }

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
     * Local stop on the live instance (issue #43, finding 3): the
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
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun updateNotification(title: String, subtitle: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(title, subtitle))
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
        const val TAG = "MonolithShield"
        const val RESTART_REQUEST_CODE = 0x5211
        const val CHANNEL_ID = "monolith_shield_channel"
        const val NOTIFICATION_ID = 4096
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
                // Issue #43, finding 3: dispatch like startShield —
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
