package com.example.service

import android.app.Notification
import android.content.ComponentName
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** Notification.CATEGORY_TRANSPORT's value (media playback). */
internal const val MEDIA_TRANSPORT_CATEGORY = "transport"

/** Notification.EXTRA_TEMPLATE value for a media-style notification. */
internal const val MEDIA_STYLE_TEMPLATE = "android.app.Notification\$MediaStyle"

/**
 * Issue #132 (music-during-focus): a media PLAYBACK notification
 * is not a held message — it is the player's transport controls
 * (pause / skip), and its "text" is a song title, not a message.
 * Identified only by markers the posting app itself supplies:
 * the transport category, a media-session token in the extras,
 * or the MediaStyle template. Pure, so the exemption is
 * unit-testable without a Notification instance. A player's
 * OTHER notifications (a new-episode promo with none of these
 * markers) are not exempt and are still held as before.
 */
internal fun isMediaPlaybackNotification(
    category: String?,
    hasMediaSession: Boolean,
    template: String?
): Boolean =
    category == MEDIA_TRANSPORT_CATEGORY || hasMediaSession || template == MEDIA_STYLE_TEMPLATE

/**
 * Issue #45 (C5): the pure hold-kind filter. A notification is
 * holdable only if it is a real, user-facing message: group
 * summaries merely restate their children, ongoing and
 * foreground-service notifications are machinery, and the
 * progress / service / transport categories are status readouts,
 * not messages. Call and alarm categories keep the listener's
 * long-standing never-suppress rule. Pure (ints and strings only)
 * so the truth table is unit-testable without Android.
 */
object NotificationCapturePolicy {

    fun shouldCaptureNotification(flags: Int, category: String?): Boolean {
        if (flags and Notification.FLAG_GROUP_SUMMARY != 0) return false
        if (flags and Notification.FLAG_ONGOING_EVENT != 0) return false
        if (flags and Notification.FLAG_FOREGROUND_SERVICE != 0) return false
        return when (category) {
            Notification.CATEGORY_PROGRESS,
            Notification.CATEGORY_SERVICE,
            Notification.CATEGORY_TRANSPORT,
            Notification.CATEGORY_CALL,
            // Issue #121: calendar reminders and events stand with
            // calls and alarms in the time-critical class — a
            // reminder held here is an alert the user never sees,
            // and calendar apps do not post reminders as
            // CATEGORY_ALARM. Spared HERE, at the hold decision,
            // strictly upstream of any holding mechanism.
            Notification.CATEGORY_REMINDER,
            Notification.CATEGORY_EVENT,
            Notification.CATEGORY_ALARM -> false
            else -> true
        }
    }

    /** Issue #120 (otp-banking-mid-session): one-time-code
     *  recognition, deliberately narrow — code-words only, never
     *  digits alone (a chat full of numbers is still a chat).
     *  A notification matching this is NEVER held: the original
     *  stays in the shade with its actions, because the code
     *  dies in minutes while a session runs far longer. Pure,
     *  unit-tested. (Under the old storage regime the code was
     *  copied-but-not-cancelled; since Issue #100's CUT the
     *  exemption is simpler — it is simply never held.) */
    private val ONE_TIME_CODE_PATTERNS = listOf(
        Regex("""\botp\b""", RegexOption.IGNORE_CASE),
        Regex("""one[- ]time""", RegexOption.IGNORE_CASE),
        Regex("""verification code""", RegexOption.IGNORE_CASE),
        Regex("""security code""", RegexOption.IGNORE_CASE),
        Regex("""login code""", RegexOption.IGNORE_CASE),
        Regex("""sign[- ]?in code""", RegexOption.IGNORE_CASE),
        Regex("""auth code""", RegexOption.IGNORE_CASE),
        Regex("""\bpasscode\b""", RegexOption.IGNORE_CASE),
        Regex("""\b2fa\b""", RegexOption.IGNORE_CASE)
    )

    fun looksLikeOneTimeCode(title: String, text: String): Boolean {
        val haystack = "$title $text"
        return ONE_TIME_CODE_PATTERNS.any { it.containsMatchIn(haystack) }
    }
}

/**
 * Issue #100 (notifications-lived): the session's end on the wall
 * clock, by the same formula the DAO's covering query uses — a
 * recorded end when one exists, else start + the full span
 * (blocks × (target + penalty) + the 10-minute gaps between
 * blocks). Pure, unit-tested.
 */
internal fun sessionEndWallMillis(session: FocusSessionEntity): Long {
    if (session.endWallMillis > 0L) return session.endWallMillis
    val spanSeconds = session.totalStudyBlocks *
        (session.targetDurationSeconds + session.penaltySecondsAdded) +
        (session.totalStudyBlocks - 1) * 600
    return session.startWallTimeMillis + spanSeconds * 1000L
}

/** Stopwatch sessions and already-ended rows snooze in chunks:
 *  when the chunk lapses mid-session the re-post is judged again
 *  and re-snoozed; after the session it is left in the shade. */
internal const val SNOOZE_CHUNK_MILLIS: Long = 30L * 60 * 1000
internal const val MIN_SNOOZE_MILLIS: Long = 60_000L
internal const val MAX_SNOOZE_MILLIS: Long = 12L * 60 * 60 * 1000

/**
 * Issue #100: how long a held notification sleeps. A timed
 * session snoozes to its own end — the original notification,
 * with its actions and thread link intact, resurfaces in the
 * shade when the session is over. Never negative, never absurd.
 */
internal fun snoozeDurationMillis(session: FocusSessionEntity, nowMillis: Long): Long {
    if (session.isStopwatchMode) return SNOOZE_CHUNK_MILLIS
    val untilEnd = sessionEndWallMillis(session) - nowMillis
    if (untilEnd <= 0L) return SNOOZE_CHUNK_MILLIS
    return untilEnd.coerceIn(MIN_SNOOZE_MILLIS, MAX_SNOOZE_MILLIS)
}

/** One app's cached hold facts: its tier and category. */
internal data class CachedAppRule(val tier: AppBlockTier, val category: String)

/**
 * Issue #100 F1 mitigation: the in-memory hold decision. The
 * platform delivers (buzz / heads-up) before any listener
 * callback can run — that part no code can un-ring. What code
 * CAN do is stop adding two database reads between delivery
 * and suppression: once the async path has judged one post,
 * the session window and app-rule map live here, and later
 * posts from LOCKED apps are snoozed synchronously on the
 * binder thread. The cache is only ever a copy of the DAO's
 * facts, refreshed on every async judgement; a miss or a
 * stale window falls back to the async path, never a guess.
 */
object NotificationHoldCache {
    @Volatile private var sessionStartMillis: Long = 0L
    @Volatile private var sessionEndMillis: Long = 0L
    @Volatile private var stopwatch: Boolean = false
    @Volatile private var rules: Map<String, CachedAppRule> = emptyMap()

    fun update(session: FocusSessionEntity, apps: List<BlockedAppEntity>) {
        sessionStartMillis = session.startWallTimeMillis
        sessionEndMillis = sessionEndWallMillis(session)
        stopwatch = session.isStopwatchMode
        rules = apps.associate {
            it.packageName to CachedAppRule(it.resolvedTier, it.category)
        }
    }

    fun clear() {
        sessionStartMillis = 0L
        sessionEndMillis = 0L
        stopwatch = false
        rules = emptyMap()
    }

    /** The cached rule for [pkg] if a cached session window
     *  covers [postTimeMillis], else null (judge async). */
    fun ruleCovering(pkg: String, postTimeMillis: Long): CachedAppRule? {
        if (sessionStartMillis <= 0L) return null
        if (postTimeMillis < sessionStartMillis || postTimeMillis > sessionEndMillis) return null
        return rules[pkg]
    }

    fun snoozeDuration(nowMillis: Long): Long {
        if (stopwatch) return SNOOZE_CHUNK_MILLIS
        val untilEnd = sessionEndMillis - nowMillis
        if (untilEnd <= 0L) return SNOOZE_CHUNK_MILLIS
        return untilEnd.coerceIn(MIN_SNOOZE_MILLIS, MAX_SNOOZE_MILLIS)
    }
}

/**
 * Silent Notification Holder (Issue #100 — Sarthak's ruling:
 * notification-content storage is CUT):
 * - During an active session, notifications from non-OPEN apps
 *   are SNOOZED, not captured: nothing about their content is
 *   stored, copied, or written to disk. The original
 *   notification — PendingIntent, reply actions, thread link —
 *   sleeps in the system and resurfaces in the shade when the
 *   session ends. "Held" no longer means "deleted and replaced
 *   by a dead transcript" (the captured_notifications table is
 *   dropped by Migration 14→15; there is no digest anymore).
 * - Calls, alarms, calendar reminders/events (Issue #121),
 *   media playback (Issue #132), FINANCE apps and one-time
 *   codes (Issue #120) are never held.
 * - The hold decision is not gated on the notification's body:
 *   a message whose content lives in EXTRA_BIG_TEXT /
 *   MessagingStyle extras is held exactly like any other
 *   (the old EXTRA_TEXT gate let those walk straight through —
 *   Issue #100 F4). Title/text are read ONLY for the one-time
 *   code exemption, never for storage and never as a hold gate.
 * - During a Safe Break, PEEK-tier apps are unlocked by design
 *   and their notifications are not held.
 */
class MonolithNotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO +
            CoroutineExceptionHandler { _, error ->
                Log.e(TAG, "Notification hold coroutine failed", error)
            }
    )

    companion object {
        private const val TAG = "MonolithNotifListener"

        // Watch Store BIND registry (chrome C3): the listener proves
        // its binding by EXISTING, stamped by its own lifecycle.
        @Volatile
        var instance: MonolithNotificationListener? = null
            internal set

        /** Issue #100: how many notifications this listener
         *  instance has snoozed — in-memory only, the one held
         *  count that still exists now that storage is cut. The
         *  shield badge reads it; it dies with the process and
         *  never pretends to be a stored total. */
        @Volatile
        var snoozedCount: Int = 0
            internal set

        /**
         * Issue #121: the categories of [packageName]'s currently
         * active notifications, read from the bound listener — the
         * notification proof the screen doors consult through
         * [TimeCriticalAlertGate] before covering a ringing alarm
         * or a tapped calendar reminder. Reads only the LIVE shade.
         * Empty when no listener is bound (no proof, never a guess).
         */
        fun activeAlertCategoriesFor(packageName: String): Set<String> {
            val live = instance ?: return emptySet()
            return try {
                live.activeNotifications
                    ?.filter { it.packageName == packageName }
                    ?.mapNotNull { it.notification?.category }
                    ?.toSet()
                    ?: emptySet()
            } catch (_: Exception) {
                emptySet()
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        WatchStore.bind(applicationContext)
        WatchStore.onNotificationListenerBound(true)
        // Issue #80 finding 4: a (re)bind is a session door too —
        // notifications a blocked app posted while this listener
        // was unbound are sitting in the shade right now.
        sweepActiveNotifications()
    }

    /**
     * Issue #80 finding 4: sweep the ALREADY-POSTED shade through
     * the exact same hold path as a live post. Before this,
     * onNotificationPosted was the only entry point, so anything
     * posted before the session armed (or while the listener was
     * unbound) stayed readable and actionable — inline reply
     * included — for the whole session. Called on listener
     * connect and once per shield arm (the shield service's
     * protecting flip). No-ops when no session covers a post:
     * the shared handler re-checks the session covering each
     * post time itself (issue #45's post-time rule).
     */
    fun sweepActiveNotifications() {
        val posted = try { activeNotifications } catch (_: Exception) { null } ?: return
        for (sbn in posted) {
            handleNotification(sbn)
        }
    }

    override fun onListenerDisconnected() {
        if (instance === this) {
            instance = null
            WatchStore.onNotificationListenerBound(false)
        }
        super.onListenerDisconnected()
        // Issue #45 (C5) rebind recovery: ask the system to rebind
        // this listener instead of staying dead until the user
        // happens to toggle the permission. A failed request is
        // logged, never swallowed.
        runCatching {
            NotificationListenerService.requestRebind(
                ComponentName(this, MonolithNotificationListener::class.java)
            )
        }.onFailure { error ->
            Log.w(TAG, "requestRebind after disconnect failed", error)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        handleNotification(sbn ?: return)
    }

    /** One path for live posts AND the pre-posted sweep (#80-4):
     *  identical gates, identical hold. */
    private fun handleNotification(sbn: StatusBarNotification) {
        val notification = sbn.notification ?: return
        val pkg = sbn.packageName ?: return
        if (pkg == applicationContext.packageName || pkg.startsWith("com.android.systemui")) return

        // Opportunistic routine reconcile (throttled to 30 s inside
        // the heartbeat) — same reasoning as the accessibility feed.
        RoutineHeartbeat.tickOpportunistic(applicationContext)

        // Everything the hold decision depends on is snapshotted
        // synchronously here, on the binder thread — key, postTime,
        // flags, category. The coroutine below never re-reads the
        // (mutable, pooled) StatusBarNotification.
        val key = sbn.key ?: return
        val postTimeMillis = sbn.postTime
        if (!NotificationCapturePolicy.shouldCaptureNotification(notification.flags, notification.category)) {
            return
        }

        // Never suppress media playback (Issue #132): the
        // notification IS the player's pause/skip controls, not a
        // message to hold — at any tier.
        if (isMediaPlaybackNotification(
                category = notification.category,
                hasMediaSession = notification.extras
                    ?.containsKey(Notification.EXTRA_MEDIA_SESSION) == true,
                template = notification.extras
                    ?.getString(Notification.EXTRA_TEMPLATE)
            )
        ) {
            return
        }

        // Issue #120 exemption, judged on the binder thread from
        // the same snapshot discipline as everything else. Title
        // and text are read ONLY for this exemption — never
        // stored, never a hold gate (Issue #100 F4): a
        // notification with no EXTRA_TEXT at all (content in
        // EXTRA_BIG_TEXT / MessagingStyle) simply fails the
        // code-word sniff and is held like any other message.
        val extras = notification.extras
        val title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim().orEmpty()
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim().orEmpty()
        if (NotificationCapturePolicy.looksLikeOneTimeCode(title, text)) return

        // Issue #100 F1 fast path: a cached session window + rule
        // judges a LOCKED, non-FINANCE app's post with zero
        // database reads and snoozes it on this thread. PEEK-tier
        // posts always take the async path (the Safe Break
        // question is live state, not cache material).
        val cached = NotificationHoldCache.ruleCovering(pkg, postTimeMillis)
        if (cached != null) {
            if (cached.tier == AppBlockTier.OPEN) return
            if (cached.tier == AppBlockTier.LOCKED &&
                cached.category != AppCategory.FINANCE.name
            ) {
                holdNotification(key, NotificationHoldCache.snoozeDuration(System.currentTimeMillis()))
                return
            }
        }

        scope.launch {
            val dao = com.example.AppContainer.dao(applicationContext) // ISSUE-67: single construction point
            // The session question is asked about the POST TIME,
            // not about now — a coroutine scheduled after the
            // session ended still holds a message that arrived
            // while the session was running.
            val session = dao.getSessionCoveringInstant(postTimeMillis)
            if (session == null) {
                NotificationHoldCache.clear()
                return@launch
            }
            val apps = dao.getAllAppsSnapshot()
            NotificationHoldCache.update(session, apps)
            val appRule = apps.find { it.packageName == pkg } ?: return@launch
            // Issue #120: FINANCE is never held. A bank/UPI/
            // authenticator notification is time-critical
            // infrastructure — the original stays live in the
            // shade, actions intact.
            if (appRule.category == AppCategory.FINANCE.name) return@launch
            if (appRule.resolvedTier == AppBlockTier.OPEN) return@launch
            // During a Safe Break the PEEK apps are unlocked by
            // design (the shield's own blocked-set logic); their
            // notifications are the user's to receive, not holds.
            if (session.isInSafeBreak && appRule.resolvedTier == AppBlockTier.PEEK_2M) return@launch
            holdNotification(key, snoozeDurationMillis(session, System.currentTimeMillis()))
        }
    }

    /** Snooze where the platform supports it (API 26+); on
     *  API 24–25 fall back to the old cancel — degraded, named
     *  here, and the only path that still destroys an original.
     *  Diagnostics stay content-free (#338 F2): no package names
     *  in logcat. */
    private fun holdNotification(key: String, durationMillis: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                snoozeNotification(key, durationMillis)
            } else {
                cancelNotification(key)
            }
            snoozedCount++
        } catch (error: Exception) {
            Log.w(TAG, "holdNotification failed", error)
        }
    }

    // No onNotificationRemoved override: with storage cut there
    // is no held copy to keep in sync — a recalled or dismissed
    // notification is the system's own business again, and a
    // snoozed one the sender recalls simply never resurfaces.

    override fun onDestroy() {
        // Chrome wave: the Watch Store's listener stamp dies with
        // the binding (merged into this, the class's one onDestroy).
        if (instance === this) {
            instance = null
            WatchStore.onNotificationListenerBound(false)
        }
        scope.cancel()
        super.onDestroy()
    }
}
