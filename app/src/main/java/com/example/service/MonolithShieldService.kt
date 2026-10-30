package com.example.service

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
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AppBlockTier
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
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
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification("Monolith Locked", "Protecting your focus session..."),
            fgsType
        )
        // Live Wire registry (Ines): the instance exists only once
        // startForeground has actually succeeded - no instance, no
        // flag, no rumour.
        instance = this
        WatchStore.bind(applicationContext)
        WatchStore.poke()
        blindEpisodeActive = false
        registerScreenReceiver()
        startMonitoringLoop()

        return START_STICKY
    }

    private fun startMonitoringLoop() {
        monitorJob?.cancel()
        val dao = MonolithDatabase.getInstance(applicationContext).monolithDao()
        val repository = MonolithRepository(applicationContext, dao)

        monitorJob = serviceScope.launch {
            var iteration = 0
            var parkedPasses = 0
            // Audit R3b #146: break-transition witness for the
            // summons — the loop remembers the last tick's break
            // state so the flip out of a break posts exactly once.
            var wasInSafeBreak = false
            while (isActive) {
                iteration++
                // Routine heartbeat cadence (rebuild §2.2): first pass,
                // then ~every 30 s (15 iterations of the 2 s loop), so a
                // window boundary is reconciled within half a minute
                // even if every alarm was lost.
                if (iteration == 1 || iteration % 15 == 0) {
                    RoutineHeartbeat.tick(
                        applicationContext, RoutineHeartbeat.Source.SHIELD_LOOP
                    )
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
                    // Audit R3b #146: the ending is news for the
                    // user who stayed away — it also sounds on the
                    // summons channel, not only as a silent edit to
                    // the detached LOW notification below.
                    postSummons(
                        "SESSION COMPLETE",
                        "Your session has ended. The receipt is on the Deck."
                    )
                    updateNotification("Session Complete", "Unlocked desk toy ready in your Break tab.")
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
                        subtitle = "$timeFormatted • ${updated.phantomReachesBlocked} urges stopped"
                    )
                }

                // Live Wire: the shield is PROTECTING only once a
                // full monitor iteration has run under a live
                // session - started is not protecting. The cascade
                // witness (Opal L2) is stamped at the same flip:
                // the session this iteration read, and the mono it
                // flipped - CONFIRMED stamp for SHIELD-PROTECTING.
                //
                // Audit R6 #340 F27: the witness is no longer
                // self-issued. An iteration whose door cannot SEE
                // (Usage Access revoked or never granted — the
                // AppOps probe the sweep itself uses, re-read HERE,
                // per iteration, not at arm time) does not flip the
                // witness and drops a standing one: blindness and
                // "all clear" used to be the same null, so a blind
                // shield attested protection it could not deliver.
                // The first blind pass of an episode summons the
                // user once on the HIGH channel and latches the
                // session's shield-gap fact, so the receipt can
                // never present the unwatched stretch as watched.
                val usageSight = repository.watchGrantQuery(WatchItemId.USAGE_ACCESS)
                if (!usageSight) {
                    if (!blindEpisodeActive) {
                        blindEpisodeActive = true
                        postSummons(
                            "SHIELD BLIND",
                            "Usage Access is off — Monolith cannot see the foreground. " +
                                "This stretch is booked as a shield gap, not as protection."
                        )
                        if (!updated.shieldGapSeen) {
                            dao.updateSession(updated.copy(shieldGapSeen = true))
                        }
                    }
                    if (protecting) {
                        protecting = false
                        protectingSessionId = -1
                        protectingMono = 0L
                    }
                } else {
                    blindEpisodeActive = false
                    if (!protecting) {
                        protecting = true
                        protectingSessionId = updated.id
                        protectingMono = SystemClock.elapsedRealtime()
                        WatchStore.poke()
                    }
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

                // Issue #99 F1: a package whose peek lease just
                // expired is evicted on this poll even though its
                // lastTimeUsed is minutes stale (stay-put user).
                val expiredPeeks = com.example.data.expiredPeekPackages(
                    dao.getAllAppsSnapshot(), nowMono
                )
                val foregroundBlockedPkg = repository.queryForegroundBlockedApp(blockedPackages, expiredPeeks)
                if (foregroundBlockedPkg != null && foregroundBlockedPkg != applicationContext.packageName &&
                    // Audit R6 #340 F63: the cross-door episode
                    // claim — if the accessibility door already
                    // fired on this opening, this door stands down
                    // (and vice versa). One opening, one intercept.
                    InterceptClaim.tryClaim(foregroundBlockedPkg)
                ) {
                    val blockedApp = dao.getAllAppsSnapshot().find { it.packageName == foregroundBlockedPkg }
                    val interceptIntent = Intent(applicationContext, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra(EXTRA_INTERCEPTED_PACKAGE, foregroundBlockedPkg)
                        putExtra(EXTRA_INTERCEPTED_APP_NAME, blockedApp?.appName ?: "Blocked App")
                        putExtra(EXTRA_INTERCEPT_BADGE, blockedApp?.resolvedTier?.badge ?: "Locked")
                        // This door's blocked set already excludes a leased PEEK app
                        // (the filter above), so a lease sold here is honoured (UNA2-01).
                        putExtra(EXTRA_INTERCEPT_PATH, InterceptPath.SHIELD_APP.name)
                        putExtra(EXTRA_INTERCEPT_NONCE, InterceptNonce.mint(applicationContext))
                    }
                    startActivity(interceptIntent)
                }

                releaseTickWakeLock()
                delay(2000L)
            }
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
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
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
        if (screenReceiverRegistered) {
            try {
                unregisterReceiver(screenStateReceiver)
            } catch (_: Exception) {
            }
            screenReceiverRegistered = false
        }
        releaseTickWakeLock()
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
        /** Audit R6 #340 F25: the tick wake lock's hard timeout —
         *  a pass that wedges releases the CPU within 15 s, and a
         *  lock is never held across the 2 s cadence gap. */
        const val TICK_WAKE_LOCK_TIMEOUT_MS = 15_000L
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
            } catch (_: Exception) {
                // Safe ignore in test environments
            }
        }

        fun stopShield(context: Context) {
            try {
                val intent = Intent(context, MonolithShieldService::class.java).apply {
                    action = ACTION_STOP_SHIELD
                }
                context.startService(intent)
            } catch (_: Exception) {
                // Safe ignore
            }
        }
    }
}
