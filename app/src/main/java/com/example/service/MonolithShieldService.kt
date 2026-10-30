package com.example.service

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
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AppBlockTier
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
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
            MonolithDatabase.getInstance(applicationContext).monolithDao()
                .getActiveSessionSnapshot()?.takeIf { it.isActive }?.id
        }
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
                promoteToForeground()
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

                promoteToForeground()
                // Live Wire registry (Ines): the instance exists only once
                // startForeground has actually succeeded - no instance, no
                // flag, no rumour.
                instance = this
                WatchStore.poke()
                startMonitoringLoop()

                START_STICKY
            }
        }
    }

    /** Enters (or re-asserts) the foreground with the standing
     *  shield notification. Re-asserting moves no protection state:
     *  the witness, the loop and the session are untouched. */
    private fun promoteToForeground() {
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
    }

    private fun startMonitoringLoop() {
        monitorJob?.cancel()
        val dao = MonolithDatabase.getInstance(applicationContext).monolithDao()
        val repository = MonolithRepository(applicationContext, dao)

        monitorJob = serviceScope.launch {
            var iteration = 0
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

                val foregroundBlockedPkg = repository.queryForegroundBlockedApp(blockedPackages)
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
                        // Issue #78: every firing carries a unique id so
                        // MainActivity can persist its consumption — a
                        // redelivered copy of THIS intent (process death)
                        // is then recognised and never re-shown.
                        putExtra(EXTRA_INTERCEPT_ID, UUID.randomUUID().toString())
                    }
                    startActivity(interceptIntent)
                }

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
        // Live Wire registry (Ines): in-memory only, never persisted.
        @Volatile
        var instance: MonolithShieldService? = null
            internal set

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
        /** Issue #78: the firing's unique id (UUID, minted by the
         *  firing door). MainActivity persists consumption of this
         *  id BEFORE showing the Airlock; the platform-retained
         *  intent redelivered after process death carries the same
         *  id and is dropped as already consumed. */
        const val EXTRA_INTERCEPT_ID = "extra_intercept_id"

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
