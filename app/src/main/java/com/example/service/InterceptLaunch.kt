package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.R

/**
 * ===========================================================================
 * THE INTERCEPT LAUNCH (issue #94, audit R2 oem-battery-reality F2).
 *
 * Both intercept doors — the shield's polling loop and the accessibility
 * service — launch the Airlock activity from the BACKGROUND. AOSP allows
 * that for these callers; MIUI/HyperOS and ColorOS additionally gate it
 * behind a per-app OEM toggle ("Display pop-up windows while running in
 * the background", off by default for sideloaded apps) that
 * `Settings.canDrawOverlays` does not confer and the app cannot read.
 * When the gate denies the launch, `startActivity` either throws or the
 * intent is silently dropped — before this file, both doors called it
 * bare, so a denied intercept vanished without a trace: the blocked app
 * was detected, and nothing ever appeared or said why.
 *
 * The rule here: a launch is attempted once, and a failure is NEVER
 * silent. It is stamped (wall time + count, readable for diagnosis) and
 * a tappable notification carrying the SAME intercept extras is posted
 * — tapping it opens the Airlock from a user gesture, which no
 * background-launch gate can deny.
 * ===========================================================================
 */
object InterceptLaunch {

    /** Wall time of the most recent dropped intercept launch, 0 = none
     *  this process. The in-app evidence a silent OEM drop used to lack. */
    @Volatile
    var lastDroppedLaunchWallMillis: Long = 0L
        private set

    /** How many intercept launches this process has seen dropped. */
    @Volatile
    var droppedLaunchCount: Int = 0
        private set

    /**
     * Issue #155 F3 residue (BUILDER-2, WING-F): this id was 4097 —
     * the SUMMONS id. Notification identity is (package, id); the
     * channel does not separate two cards, so a summons post and a
     * fallback post silently REPLACED each other in the shade: a
     * BREAK OVER / SESSION COMPLETE summons could erase the only
     * user-gesture door to a dropped Airlock firing, and a fallback
     * could erase the summons. The estate's ids are 4096 (shield),
     * 4097 (summons), 4098 (break alert), 820000 (break-end) — the
     * fallback takes its own: 4099. Public so the pin test can
     * assert the separation directly.
     */
    const val FALLBACK_NOTIFICATION_ID = 4099

    /** Issue #94 (routed stray ex #155): the fallback's OWN channel.
     *  This post used to re-create the shield's standing channel
     *  (MonolithShieldService.CHANNEL_ID) at IMPORTANCE_HIGH while
     *  the shield creates it at IMPORTANCE_LOW — and Android
     *  channels are immutable once created, so whichever path ran
     *  first on a device fixed the shield card's importance
     *  forever: one dropped intercept could permanently turn the
     *  silent glance card into an alerting one. The shield channel
     *  now has exactly one creator (the shield, at LOW); the
     *  fallback alerts on its own channel, at HIGH, from here only. */
    const val FALLBACK_CHANNEL_ID = "monolith_intercept_fallback_channel"

    /**
     * Launch [intent] (the Airlock intercept) from the background.
     * Returns true when the launch was handed to the system; on any
     * failure, stamps the drop and posts the notification fallback,
     * then returns false.
     *
     * A successful launch also retires any standing fallback card:
     * its whole premise was "the block screen could not open on its
     * own" — a launch that just opened proves that premise dead, so
     * the card must not keep posing as the current state of the
     * door (issue #155 F3 residue).
     */
    fun launch(context: Context, intent: Intent, appName: String): Boolean {
        return try {
            context.startActivity(intent)
            cancelFallbackNotification(context)
            true
        } catch (e: Exception) {
            lastDroppedLaunchWallMillis = System.currentTimeMillis()
            droppedLaunchCount++
            postFallbackNotification(context, intent, appName)
            false
        }
    }

    /** The fallback: a high-importance, tappable notification whose
     *  PendingIntent is the very intercept intent that was dropped —
     *  same extras, same Airlock, opened by his tap instead of from
     *  the background. Best-effort: a failure here must never crash
     *  an intercept door, and the stamp above already landed. */
    private fun postFallbackNotification(
        context: Context,
        intent: Intent,
        appName: String
    ) {
        runCatching {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as? NotificationManager ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(
                    NotificationChannel(
                        FALLBACK_CHANNEL_ID,
                        "Intercept alerts",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description =
                            "The tap-to-open card posted when the block screen could not open on its own"
                        setShowBadge(false)
                    }
                )
            }
            val pending = PendingIntent.getActivity(
                context,
                FALLBACK_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notification = NotificationCompat.Builder(
                context, FALLBACK_CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_stat_shield)
                .setContentTitle("$appName blocked")
                .setContentText(
                    "The block screen could not open on its own — tap to open it."
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                // Issue #155 F3 residue: the fallback carried NO
                // expiry — an untapped "<app> blocked" card lingered
                // indefinitely, and a tap hours later would deliver
                // a stale firing's intent as if the intercept were
                // current. It now self-expires on the estate's
                // moment-card clock (the same 45 minutes as the F3
                // completion card and the SESSION COMPLETE summons).
                // While the user stays in the blocked app the doors
                // re-fire and re-post it, so the expiry only ever
                // retires a card whose moment has actually passed.
                .setTimeoutAfter(MonolithShieldService.COMPLETION_TIMEOUT_MS)
                .setContentIntent(pending)
                .build()
            manager.notify(FALLBACK_NOTIFICATION_ID, notification)
        }
    }

    /** Retire the standing fallback card, if any. Best-effort, like
     *  the post: a failure here must never crash an intercept door. */
    private fun cancelFallbackNotification(context: Context) {
        runCatching {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as? NotificationManager ?: return
            manager.cancel(FALLBACK_NOTIFICATION_ID)
        }
    }
}
