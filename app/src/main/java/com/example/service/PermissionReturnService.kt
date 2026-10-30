package com.example.service

import android.app.AppOpsManager
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.util.Log

/**
 * Issue #308 (Audit R5 · Opal platform execution, finding 1): the
 * return trip from Settings.
 *
 * Before this service, every permission FIX trip was fire-and-forget:
 * the app launched a Settings pane and stranded the user there — the
 * way back was the user's own Back-press archaeology. Opal ships a
 * dedicated ReturnAfterPermissionService that polls the grant and
 * bounces the user back the moment it lands; this is Monolith's
 * equivalent, built on the same grant facts the Watch Store's sweep
 * already measures (MonolithRepository.watchGrantQuery semantics,
 * mirrored here so the service needs no database).
 *
 * Lifecycle: started by MainActivity's openFixPane immediately before
 * a Settings pane opens, with the [WatchItemId] being fixed. A worker
 * thread polls that one grant every [POLL_MS]. The instant it reads
 * granted, the service brings MainActivity (singleTop) back to the
 * front and stops itself. It also stops on [DEADLINE_MS] (the user
 * gave up or is reading the pane — no zombie polling) and whenever
 * MainActivity resumes on its own (the user found their own way
 * back), via [stop].
 *
 * Honest limits, stated rather than hidden: starting an activity from
 * a background service is restricted on Android 10+; the launch is
 * attempted because this app ordinarily holds the overlay grant or a
 * recent-foreground exemption by the time a trip matters, and if the
 * launch is refused the poll simply ends — the user is exactly where
 * they were before this service existed, never worse off.
 */
class PermissionReturnService : Service() {

    private var workerThread: HandlerThread? = null
    private var worker: Handler? = null
    @Volatile private var watching: WatchItemId? = null
    @Volatile private var deadlineMono: Long = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val item = intent?.getStringExtra(EXTRA_ITEM)
            ?.let { runCatching { WatchItemId.valueOf(it) }.getOrNull() }
        if (item == null) {
            stopSelf()
            return START_NOT_STICKY
        }
        watching = item
        deadlineMono = android.os.SystemClock.elapsedRealtime() + DEADLINE_MS
        if (workerThread == null) {
            workerThread = HandlerThread(TAG).also { it.start() }
            worker = Handler(workerThread!!.looper)
        }
        worker?.removeCallbacks(poll)
        worker?.post(poll)
        return START_NOT_STICKY
    }

    private val poll = object : Runnable {
        override fun run() {
            val item = watching ?: return
            if (android.os.SystemClock.elapsedRealtime() > deadlineMono) {
                Log.i(TAG, "return watch for $item expired ungranted")
                stopSelf()
                return
            }
            if (isGranted(item)) {
                Log.i(TAG, "grant landed for $item — returning to Monolith")
                bringAppForward()
                stopSelf()
                return
            }
            worker?.postDelayed(this, POLL_MS)
        }
    }

    private fun bringAppForward() {
        // ISSUE-67: addressed via the InterceptIntents seam (class
        // name, identical flags) — no compile-time MainActivity
        // reference from the service layer.
        val intent = InterceptIntents.returnIntent(this)
        runCatching { startActivity(intent) }
            .onFailure { Log.w(TAG, "return launch refused: ${it.message}") }
    }

    /** The same grant facts the Watch sweep measures, per item. */
    private fun isGranted(item: WatchItemId): Boolean = when (item) {
        WatchItemId.ACCESSIBILITY ->
            MonolithAccessibilityService.instance != null ||
                secureListContains("enabled_accessibility_services")
        WatchItemId.USAGE_ACCESS -> {
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
            appOps?.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(), packageName
            ) == AppOpsManager.MODE_ALLOWED
        }
        WatchItemId.DEVICE_ADMIN -> {
            val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
            dpm?.isAdminActive(ComponentName(this, MonolithDeviceAdminReceiver::class.java)) == true
        }
        WatchItemId.NOTIFICATION_LISTENER ->
            secureListContains("enabled_notification_listeners")
        WatchItemId.OVERLAY -> Settings.canDrawOverlays(this)
        WatchItemId.BATTERY -> {
            val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
            pm?.isIgnoringBatteryOptimizations(packageName) == true
        }
        else -> false
    }

    private fun secureListContains(key: String): Boolean = try {
        Settings.Secure.getString(contentResolver, key)
            ?.contains(packageName, ignoreCase = true) == true
    } catch (_: Exception) {
        false
    }

    override fun onDestroy() {
        watching = null
        worker?.removeCallbacks(poll)
        workerThread?.quitSafely()
        workerThread = null
        worker = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "PermissionReturnSvc"
        private const val EXTRA_ITEM = "permission_return_item"
        private const val POLL_MS = 750L
        private const val DEADLINE_MS = 180_000L

        /** Arm the return trip for [item]; call BEFORE opening Settings. */
        fun arm(context: Context, item: WatchItemId) {
            runCatching {
                context.startService(
                    Intent(context, PermissionReturnService::class.java)
                        .putExtra(EXTRA_ITEM, item.name)
                )
            }
        }

        /** The user is back (MainActivity resumed): end any watch. */
        fun stop(context: Context) {
            runCatching { context.stopService(Intent(context, PermissionReturnService::class.java)) }
        }
    }
}
