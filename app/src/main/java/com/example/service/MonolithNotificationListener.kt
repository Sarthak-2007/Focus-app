package com.example.service

import android.app.Notification
import android.content.ComponentName
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.FocusSessionEntity
import java.util.concurrent.atomic.AtomicInteger
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
 * Issue #45 (C5): the pure suppression-kind filter. A notification is
 * suppressible only if it is a real, user-facing message: group
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
            Notification.CATEGORY_ALARM -> false
            else -> true
        }
    }

    /** Issue #120 (otp-banking-mid-session): one-time-code
     *  recognition, deliberately narrow — code-words only, never
     *  digits alone (a chat full of numbers is still a chat). A
     *  notification matching this is NEVER cancelled: the original
     *  stays in the shade with its actions, because the code dies
     *  in minutes while the session runs far longer than the code
     *  does. The check is transient — title and body are judged in
     *  memory at post time and never stored (issue #100 cut).
     *  Pure, unit-tested. */
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
 * Issue #100 (Sarthak's ruling, Oct 30): notification-content
 * storage is CUT. Held means SUPPRESSED — the original is
 * cancelled out of the shade and nothing about it is written
 * anywhere: no title, no body, no transcript, no digest. The
 * only fact the app keeps is THIS counter: how many
 * notifications were suppressed in the current session. It is
 * in-memory only, scoped to one session id (a new session
 * resets it), uncapped (a counter, not a list), and dies with
 * the process. It carries no content of any kind.
 */
object HeldSuppressionCounter {
    private val count = AtomicInteger(0)

    @Volatile
    private var sessionId: Int? = null

    @Synchronized
    fun noteSuppressed(forSessionId: Int) {
        if (sessionId != forSessionId) {
            sessionId = forSessionId
            count.set(0)
        }
        count.incrementAndGet()
    }

    @Synchronized
    fun countFor(forSessionId: Int): Int =
        if (sessionId == forSessionId) count.get() else 0
}

/**
 * The session-covering test, mirroring the DAO's
 * getSessionCoveringInstant SQL exactly, so the listener's
 * in-memory fast path judges a post by the same rule the
 * database slow path does: the session question is asked about
 * the POST TIME, not about now (issue #45, C5).
 */
internal fun sessionCoversInstant(session: FocusSessionEntity, instantMillis: Long): Boolean {
    if (session.startWallTimeMillis > instantMillis) return false
    if (session.isActive) return true
    if (session.endWallMillis > 0L) return instantMillis <= session.endWallMillis
    val spanMillis = (session.totalStudyBlocks.toLong() *
        (session.targetDurationSeconds + session.penaltySecondsAdded) +
        (session.totalStudyBlocks - 1) * 600L) * 1000L
    return instantMillis <= session.startWallTimeMillis + spanMillis
}

/**
 * The fullest body a notification carries, wherever it lives
 * (issue #100, finding 4): visible content often sits OUTSIDE
 * EXTRA_TEXT — big-text expansions, inbox-style line stacks,
 * MessagingStyle messages, the sub-text line. Read them all,
 * fullest first. The result is used transiently (the one-time
 * code judgement) and never stored.
 */
internal fun extractNotificationBody(extras: Bundle?): String {
    if (extras == null) return ""
    extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim()
        ?.takeIf { it.isNotEmpty() }?.let { return it }
    extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()?.trim()
        ?.takeIf { it.isNotEmpty() }?.let { return it }
    extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
        ?.mapNotNull { it?.toString()?.trim() }
        ?.filter { it.isNotEmpty() }
        ?.joinToString("\n")
        ?.takeIf { it.isNotEmpty() }?.let { return it }
    @Suppress("DEPRECATION")
    val messages = extras.getParcelableArray(Notification.EXTRA_MESSAGES)
    val fromStyle = messages
        ?.mapNotNull { (it as? Bundle)?.getCharSequence("text")?.toString()?.trim() }
        ?.filter { it.isNotEmpty() }
        ?.joinToString("\n")
        .orEmpty()
    if (fromStyle.isNotEmpty()) return fromStyle
    return extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()?.trim().orEmpty()
}

/**
 * Silent Notification Suppressor (issue #100 — storage CUT):
 * - Intercepts and suppresses notifications from blocked apps during
 *   active sessions. Suppression is cancellation ONLY: the original
 *   is removed from the shade and NOTHING is stored — no captured
 *   row, no transcript, no digest. Held = suppressed, not transcribed.
 * - Explicitly excludes incoming phone/VoIP call notifications
 *   (Notification.CATEGORY_CALL) so urgent calls are never suppressed.
 * - Explicitly excludes media playback notifications (see
 *   [isMediaPlaybackNotification]): a song is not a message, and
 *   cancelling the notification would strip the player's
 *   pause/skip controls from the shade and lock screen.
 * - Finance apps and one-time codes are never suppressed (issue
 *   #120): the original stays live in the shade, actions intact.
 * - The platform delivers a notification (sound / heads-up) BEFORE
 *   any listener callback runs; no listener can un-ring that. What
 *   this class controls is how fast the shade entry is removed:
 *   the decision runs on a small in-memory cache of the covering
 *   session + app rules, refreshed in the background, so a warm
 *   cancel fires inside the posted callback itself instead of
 *   behind two async database reads (issue #100, finding 1). A
 *   cold or stale cache falls back to the database slow path,
 *   which warms the cache from its own reads.
 */
class MonolithNotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO +
            CoroutineExceptionHandler { _, error ->
                Log.e(TAG, "Notification suppression coroutine failed", error)
            }
    )

    /** One app's cached suppression rule: its tier and category. */
    private data class CachedRule(val tier: AppBlockTier, val category: String)

    // Fast-path decision cache (issue #100, finding 1): the session
    // covering now + the full app-rule map, kept warm by background
    // refreshes. cacheKnown = false means "not yet known" — the
    // slow path then decides from the database.
    @Volatile private var cacheSession: FocusSessionEntity? = null
    @Volatile private var cacheKnown: Boolean = false
    @Volatile private var cacheRules: Map<String, CachedRule> = emptyMap()
    @Volatile private var cacheStampMs: Long = 0L
    @Volatile private var cacheRefreshInFlight: Boolean = false

    companion object {
        private const val TAG = "MonolithNotifListener"
        private const val CACHE_TTL_MS = 10_000L

        // Watch Store BIND registry (chrome C3): the listener proves
        // its binding by EXISTING, stamped by its own lifecycle.
        @Volatile
        var instance: MonolithNotificationListener? = null
            internal set
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        WatchStore.bind(applicationContext)
        WatchStore.onNotificationListenerBound(true)
        refreshCache()
        // Issue #80 finding 4: a (re)bind is a session door too —
        // notifications a blocked app posted while this listener
        // was unbound are sitting in the shade right now.
        sweepActiveNotifications()
    }

    /**
     * Issue #80 finding 4: sweep the ALREADY-POSTED shade through
     * the exact same suppression path as a live post. Before this,
     * onNotificationPosted was the only entry point, so anything
     * posted before the session armed (or while the listener was
     * unbound) stayed readable and actionable — inline reply
     * included — for the whole session. Called on listener connect
     * and once per shield arm (the shield service's protecting
     * flip), which together cover "session starts after the posts"
     * and "listener binds after the posts". No-ops when no session
     * covers a post: the shared handler re-checks the session
     * covering each post time itself (issue #45's post-time rule).
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

    private fun cacheFresh(): Boolean =
        cacheKnown && (System.currentTimeMillis() - cacheStampMs) <= CACHE_TTL_MS

    private fun refreshCache() {
        if (cacheRefreshInFlight) return
        cacheRefreshInFlight = true
        scope.launch {
            try {
                val dao = com.example.AppContainer.dao(applicationContext) // ISSUE-67: single construction point
                val session = dao.getSessionCoveringInstant(System.currentTimeMillis())
                val apps = dao.getAllAppsSnapshot()
                cacheSession = session
                cacheRules = apps.associate {
                    it.packageName to CachedRule(it.resolvedTier, it.category)
                }
                cacheStampMs = System.currentTimeMillis()
                cacheKnown = true
            } catch (_: Exception) {
                // A failed refresh leaves the old cache in place;
                // the TTL keeps it from being trusted for long.
            } finally {
                cacheRefreshInFlight = false
            }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        handleNotification(sbn ?: return)
    }

    /** One path for live posts AND the pre-posted sweep (#80-4):
     *  identical gates, identical suppression. */
    private fun handleNotification(sbn: StatusBarNotification) {
        val notification = sbn.notification ?: return
        val pkg = sbn.packageName ?: return
        if (pkg == applicationContext.packageName || pkg.startsWith("com.android.systemui")) return

        // Opportunistic routine reconcile (throttled to 30 s inside
        // the heartbeat) — same reasoning as the accessibility feed.
        RoutineHeartbeat.tickOpportunistic(applicationContext)

        // Issue #45 (C5): EVERYTHING the suppression decision depends
        // on is snapshotted synchronously here, on the binder
        // thread — key, postTime, flags, category, title, body.
        // The slow path never re-reads the (mutable, pooled)
        // StatusBarNotification, so a recycled or mutated sbn can
        // no longer change what gets judged.
        val key = sbn.key
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

        val extras = notification.extras
        val title = (extras?.getCharSequence(Notification.EXTRA_TITLE)
            ?: extras?.getCharSequence(Notification.EXTRA_TITLE_BIG))
            ?.toString()?.trim().orEmpty()
        // Finding 4: the body is read from EVERY carrier, not
        // EXTRA_TEXT alone — a notification whose body lives in
        // big-text / inbox lines / MessagingStyle is judged (and
        // suppressed) exactly like one that uses EXTRA_TEXT.
        val text = extractNotificationBody(extras)
        if (title.isEmpty() && text.isEmpty()) return

        // Issue #120: judged here, on the binder thread, from the
        // same snapshot as everything else — the slow path never
        // re-reads the (mutable, pooled) notification.
        val otpLike = NotificationCapturePolicy.looksLikeOneTimeCode(title, text)

        // FAST PATH (finding 1): a fresh cache decides NOW, in the
        // posted callback itself — no async hop, no database reads
        // between delivery and removal.
        if (cacheFresh()) {
            val session = cacheSession ?: return
            if (!sessionCoversInstant(session, postTimeMillis)) return
            val rule = cacheRules[pkg] ?: return
            // Issue #120: FINANCE is never suppressed.
            if (rule.category == AppCategory.FINANCE.name) return
            if (rule.tier == AppBlockTier.OPEN) return
            if (otpLike) return
            cancelSuppressed(key, pkg, session.id)
            return
        }

        // SLOW PATH: cold or stale cache — decide from the database
        // (the session question is asked about the POST TIME, not
        // about now — issue #45) and warm the cache for the next post.
        refreshCache()
        scope.launch {
            val dao = com.example.AppContainer.dao(applicationContext) // ISSUE-67: single construction point
            val session = dao.getSessionCoveringInstant(postTimeMillis) ?: return@launch

            val appRule = dao.getAllAppsSnapshot().find { it.packageName == pkg }
            // Issue #120: FINANCE is never suppressed. A bank/UPI/
            // authenticator notification is time-critical
            // infrastructure: cancelling the original destroyed the
            // Approve action and raced a 30-second code against a
            // session-length hold. The original stays live in the
            // shade, actions intact — the same never-suppress class
            // as calls and alarms.
            if (appRule != null && appRule.category == AppCategory.FINANCE.name) {
                return@launch
            }
            if (appRule != null && appRule.resolvedTier != AppBlockTier.OPEN) {
                // Issue #120: a one-time code is never cancelled —
                // the original keeps its shade entry (and any action
                // it carries) because the code expires in minutes
                // while the session runs far longer. And under the
                // issue #100 cut nothing is copied either: the code
                // simply stays where the system put it.
                if (!otpLike) {
                    cancelSuppressed(key, pkg, session.id)
                }
            }
        }
    }

    /** The whole act of holding, after the cut: cancel the shade
     *  entry and count it. Nothing is stored — no row, no file, no
     *  copy of the content anywhere. */
    private fun cancelSuppressed(key: String?, pkg: String, sessionId: Int) {
        try {
            cancelNotification(key)
            HeldSuppressionCounter.noteSuppressed(sessionId)
        } catch (error: Exception) {
            Log.w(TAG, "cancelNotification failed for $pkg", error)
        }
    }

    // No onNotificationRemoved handling: with nothing stored there
    // is no held copy to keep in step with the shade. A recalled or
    // dismissed notification simply stays gone.

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
