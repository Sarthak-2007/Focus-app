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
import com.example.widget.MonolithWidgetProvider
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
            MonolithWidgetProvider.requestUpdate(applicationContext)
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
        // Issue #155 (F2): the initial card must not claim a lock that
        // has not happened yet — `protecting` flips only after the
        // monitor loop's first full iteration, and that same iteration
        // overwrites this card with real session facts.
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification("Monolith", "Arming shield\u2026"),
            fgsType
        )
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
                val activeSession = dao.getActiveSessionSnapshot()
                if (activeSession == null || !activeSession.isActive) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    MonolithWidgetProvider.requestUpdate(applicationContext)
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

                // Issue #155 (F5): the expanded card carries the full
                // status, and timed sessions get a SYSTEM chronometer
                // (when = session end) instead of this loop's 2s text
                // snapshot as the only timer. The end is derived from
                // the same remaining-seconds math as the text above.
                val timeLeftLabel = when {
                    updated.isInSafeBreak -> {
                        val bMin = updated.safeBreakSecondsRemaining / 60
                        val bSec = updated.safeBreakSecondsRemaining % 60
                        String.format("Safe Break %02d:%02d left", bMin, bSec)
                    }
                    updated.isStopwatchMode -> {
                        val mins = updated.elapsedSeconds / 60
                        val secs = updated.elapsedSeconds % 60
                        String.format("Stopwatch +%02d:%02d elapsed", mins, secs)
                    }
                    else -> {
                        val effectiveTarget = updated.targetDurationSeconds + updated.penaltySecondsAdded
                        val remainingSec = (effectiveTarget - updated.elapsedSeconds).coerceAtLeast(0)
                        val mins = remainingSec / 60
                        val secs = remainingSec % 60
                        String.format("%02d:%02d left", mins, secs)
                    }
                }
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
                val badgeNumber = heldCount ?: updated.phantomReachesBlocked
                val bigText = "${updated.intention} • $timeLeftLabel • " +
                    "Block ${updated.currentBlockIndex}/${updated.totalStudyBlocks} • " +
                    "${updated.phantomReachesBlocked} urges stopped • " +
                    (if (heldCount != null) "$heldCount held" else "held count unavailable")

                // Audit R3b #149 (from main): the grace window exists
                // off the Deck too. During the window the shield's own
                // notification counts it down — and for a routine-born
                // row it names the booking instead of promising free.
                // Merge resolution (WELLE-4, #155 × #149): the grace
                // subtitle rides on the #155 card — bigText,
                // chronometer and badge number apply in both branches.
                if (updated.elapsedSeconds <= 10 && !updated.isInSafeBreak) {
                    updateNotification(
                        title = "Monolith • ${updated.intention}",
                        subtitle = if (updated.routineId != null) {
                            "GRACE ${10 - updated.elapsedSeconds}S — ABORT BOOKED BY ROUTINE · TAP TO OPEN"
                        } else {
                            "GRACE ${10 - updated.elapsedSeconds}S — CANCEL FREE · TAP TO OPEN"
                        },
                        bigText = bigText,
                        chronometerEndMillis = chronometerEndMillis,
                        badgeNumber = badgeNumber
                    )
                } else {
                    updateNotification(
                        title = "Monolith • ${updated.intention}",
                        subtitle = "$timeFormatted • ${updated.phantomReachesBlocked} urges stopped",
                        bigText = bigText,
                        chronometerEndMillis = chronometerEndMillis,
                        badgeNumber = badgeNumber
                    )
                }

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
                    // Issue #155 (F1): a session is now live — refresh
                    // the home-screen widget at the moment it matters.
                    MonolithWidgetProvider.requestUpdate(applicationContext)
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
                if (foregroundBlockedPkg != null && foregroundBlockedPkg != applicationContext.packageName) {
                    val blockedApp = dao.getAllAppsSnapshot().find { it.packageName == foregroundBlockedPkg }
                    val interceptIntent = Intent(applicationContext, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra(EXTRA_INTERCEPTED_PACKAGE, foregroundBlockedPkg)
                        putExtra(EXTRA_INTERCEPTED_APP_NAME, blockedApp?.appName ?: "Blocked App")
                        putExtra(EXTRA_INTERCEPT_BADGE, blockedApp?.resolvedTier?.badge ?: "Shut")
                        // This door's blocked set already excludes a leased PEEK app
                        // (the filter above), so a lease sold here is honoured (UNA2-01).
                        putExtra(EXTRA_INTERCEPT_PATH, InterceptPath.SHIELD_APP.name)
                        putExtra(EXTRA_INTERCEPT_NONCE, InterceptNonce.mint(applicationContext))
                    }
                    // Issue #94 (F2): never a bare background launch —
                    // a dropped launch stamps itself and falls back
                    // to a tappable notification (InterceptLaunch).
                    InterceptLaunch.launch(
                        applicationContext, interceptIntent,
                        blockedApp?.appName ?: "Blocked App"
                    )
                }

                delay(2000L)
            }
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

    private fun buildNotification(
        title: String,
        subtitle: String,
        ongoing: Boolean = true,
        autoCancel: Boolean = false,
        timeoutAfterMs: Long = 0,
        bigText: String? = null,
        chronometerEndMillis: Long? = null,
        badgeNumber: Int? = null
    ): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                // Issue #155 (F5): a tap on the shield card lands on
                // the SHIELD destination, not the default Deck —
                // MainActivity routes on this extra when no intercept
                // extras are present.
                putExtra(EXTRA_FROM_SHIELD_NOTIFICATION, true)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setOngoing(ongoing)
            .setAutoCancel(autoCancel)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        if (timeoutAfterMs > 0) {
            builder.setTimeoutAfter(timeoutAfterMs)
        }
        if (bigText != null) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
        }
        if (chronometerEndMillis != null) {
            // The countdown is the system's chronometer (real time),
            // not the monitor loop's 2-second text snapshot.
            builder.setUsesChronometer(true)
            builder.setChronometerCountDown(true)
            builder.setWhen(chronometerEndMillis)
            builder.setShowWhen(true)
        }
        if (badgeNumber != null) {
            builder.setNumber(badgeNumber)
        }
        return builder.build()
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
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(
            NOTIFICATION_ID,
            buildNotification(
                title, subtitle, ongoing, autoCancel, timeoutAfterMs,
                bigText, chronometerEndMillis, badgeNumber
            )
        )
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
        // Issue #155 (F4): v2 carries setShowBadge(true); the legacy
        // id is deleted in createNotificationChannel because channels
        // are immutable once created on a device.
        const val CHANNEL_ID = "monolith_shield_channel_v2"
        const val LEGACY_CHANNEL_ID = "monolith_shield_channel"
        const val NOTIFICATION_ID = 4096
        /** Issue #155 (F3): the detached completion card self-expires. */
        const val COMPLETION_TIMEOUT_MS = 45 * 60 * 1000L
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
