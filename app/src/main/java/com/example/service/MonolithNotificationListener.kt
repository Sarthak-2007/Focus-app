package com.example.service

import android.app.Notification
import android.content.ComponentName
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.CapturedNotificationEntity
import java.util.concurrent.ConcurrentHashMap
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
 * Issue #45 (C5): the pure capture-kind filter. A notification is
 * capturable only if it is a real, user-facing message: group
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
     *  notification matching this is copied to the digest but
     *  NEVER cancelled: the original stays in the shade with its
     *  actions, because the code dies in minutes while the digest
     *  copy carries no Approve button and the session runs far
     *  longer than the code does. Pure, unit-tested. */
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

    /** The content fingerprint an update is compared by: title and
     *  body, nothing else — a re-post whose visible content is
     *  identical to the held copy is the same message. */
    fun contentFingerprint(title: String, body: String): String = "$title\u0000$body"
}

/**
 * Issue #45 (C5): one held capture, remembered by the sbn.key that
 * produced it. [senderTitle] and [messageBody] are the values AS
 * STORED (senderTitle after the app-name fallback), and
 * [postTimeMillis] is the row's timestampMillis — together with
 * the package they are the quadruple the DAO matches stored rows
 * by, for both the dedupe-replace and the removal delete.
 */
data class SeenCapture(
    val fingerprint: String,
    val packageName: String,
    val senderTitle: String,
    val messageBody: String,
    val postTimeMillis: Long
)

/**
 * Issue #45 (C5): the in-memory dedupe tracker, keyed by sbn.key.
 * Pure JVM (a ConcurrentHashMap and nothing Android), so its
 * behaviour is unit-testable directly. It remembers only captures
 * this listener instance actually stored — a notification that
 * was never held (no session, open app) leaves no entry, so a
 * later update of it is still judged on its own merits and
 * capture is never weakened by an earlier non-capture.
 */
class CaptureDedupeTracker {
    private val byKey = ConcurrentHashMap<String, SeenCapture>()

    fun seen(key: String): SeenCapture? = byKey[key]

    fun isUnchanged(key: String, fingerprint: String): Boolean =
        byKey[key]?.fingerprint == fingerprint

    fun record(key: String, capture: SeenCapture) {
        byKey[key] = capture
    }

    fun forget(key: String): SeenCapture? = byKey.remove(key)
}

/**
 * Silent Notification Catcher & Persistent Digest:
 * - Intercepts and suppresses notifications from blocked apps during active sessions.
 * - Explicitly excludes incoming phone/VoIP call notifications (Notification.CATEGORY_CALL)
 *   so urgent calls are never suppressed.
 * - Explicitly excludes media playback notifications (see
 *   [isMediaPlaybackNotification]): a song is not a message, and
 *   cancelling the notification would strip the player's
 *   pause/skip controls from the shade and lock screen.
 * - Saves captured notifications to Room persistent disk storage so they survive background
 *   restarts and can be read, cleared, or tapped to open after studying.
 */
class MonolithNotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO +
            CoroutineExceptionHandler { _, error ->
                Log.e(TAG, "Notification capture coroutine failed", error)
            }
    )

    // Issue #45 (C5) dedupe: updates of a notification this
    // instance already holds, recognised by sbn.key + content
    // fingerprint. In-memory only, by design: after a process
    // restart the worst case is one duplicate row for a still-live
    // notification, never a lost message.
    internal val dedupe = CaptureDedupeTracker()

    companion object {
        private const val TAG = "MonolithNotifListener"

        // Issue #45 (C5) retention bounds for the plaintext digest:
        // a held row lives at most 7 days and the table holds at
        // most the newest 200 rows. Pruned on the insert path
        // after every insert — see pruneCaptured().
        const val RETENTION_MILLIS: Long = 7L * 24 * 60 * 60 * 1000
        const val MAX_CAPTURED_ROWS: Int = 200

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
        // Issue #80 finding 4: a (re)bind is a session door too —
        // notifications a blocked app posted while this listener
        // was unbound are sitting in the shade right now.
        sweepActiveNotifications()
    }

    /**
     * Issue #80 finding 4: sweep the ALREADY-POSTED shade through
     * the exact same capture-and-cancel path as a live post.
     * Before this, onNotificationPosted was the only entry point,
     * so anything posted before the session armed (or while the
     * listener was unbound) stayed readable and actionable —
     * inline reply included — for the whole session.
     * Called on listener connect and once per shield arm (the
     * shield service's protecting flip), which together cover
     * "session starts after the posts" and "listener binds after
     * the posts". No-ops when no session covers a post: the shared
     * handler re-checks the session covering each post time itself
     * (issue #45's post-time rule).
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
     *  identical gates, identical capture, identical cancel. */
    private fun handleNotification(sbn: StatusBarNotification) {
        val notification = sbn.notification ?: return
        val pkg = sbn.packageName ?: return
        if (pkg == applicationContext.packageName || pkg.startsWith("com.android.systemui")) return

        // Opportunistic routine reconcile (throttled to 30 s inside
        // the heartbeat) — same reasoning as the accessibility feed.
        RoutineHeartbeat.tickOpportunistic(applicationContext)

        // Issue #45 (C5): EVERYTHING the capture decision depends
        // on is snapshotted synchronously here, on the binder
        // thread — key, postTime, flags, category, title, body.
        // The coroutine below never re-reads the (mutable, pooled)
        // StatusBarNotification, so a recycled or mutated sbn can
        // no longer change what gets judged or stored.
        val key = sbn.key
        val postTimeMillis = sbn.postTime
        if (!NotificationCapturePolicy.shouldCaptureNotification(notification.flags, notification.category)) {
            return
        }

        // Never suppress media playback (Issue #132): the
        // notification IS the player's pause/skip controls, not a
        // message to hold for the digest — at any tier.
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
        val title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim().orEmpty()
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim().orEmpty()
        if (title.isEmpty() && text.isEmpty()) return

        // Issue #45 (C5) dedupe, first gate: an update whose
        // content is identical to the copy already held inserts
        // nothing and — just as important — does NOT re-cancel:
        // the shade entry is the same one already dealt with.
        val fingerprint = NotificationCapturePolicy.contentFingerprint(title, text)
        if (key != null && dedupe.isUnchanged(key, fingerprint)) return

        // Issue #120: judged here, on the binder thread, from the
        // same snapshot as everything else — the coroutine below
        // never re-reads the (mutable, pooled) notification.
        val otpLike = NotificationCapturePolicy.looksLikeOneTimeCode(title, text)

        scope.launch {
            val dao = com.example.AppContainer.dao(applicationContext) // ISSUE-67: single construction point
            // Issue #45 (C5): the session question is asked about
            // the POST TIME, not about now — a coroutine scheduled
            // after the session ended still captures a message
            // that arrived while the session was running.
            dao.getSessionCoveringInstant(postTimeMillis) ?: return@launch

            val appRule = dao.getAllAppsSnapshot().find { it.packageName == pkg }
            // Issue #120: FINANCE is never captured and never
            // cancelled. A bank/UPI/authenticator notification is
            // time-critical infrastructure: holding a title/text
            // copy while cancelling the original destroyed the
            // Approve action and raced a 30-second code against a
            // session-length hold. The original stays live in the
            // shade, actions intact — the same never-suppress class
            // as calls and alarms.
            if (appRule != null && appRule.category == AppCategory.FINANCE.name) {
                return@launch
            }
            if (appRule != null && appRule.resolvedTier != AppBlockTier.OPEN) {
                val storedTitle = title.ifEmpty { appRule.appName }
                if (key != null) {
                    // Dedupe, second gate (inside the coroutine, so
                    // two rapid identical posts that both passed the
                    // first gate before either recorded cannot both
                    // insert): unchanged content stops here too.
                    val previous = dedupe.seen(key)
                    if (previous?.fingerprint == fingerprint) return@launch
                    // Changed content for a key already held: the
                    // old stored row is superseded — find it by its
                    // stored quadruple and delete it, so the digest
                    // holds exactly one, current, copy.
                    if (previous != null) {
                        dao.findCapturedNotificationMatching(
                            previous.packageName,
                            previous.senderTitle,
                            previous.messageBody,
                            previous.postTimeMillis
                        )?.let { stale -> dao.deleteCapturedNotification(stale.id) }
                    }
                }
                dao.insertCapturedNotification(
                    CapturedNotificationEntity(
                        // The row's timestamp is the capture
                        // snapshot — sbn.postTime — not the time
                        // this coroutine happened to run.
                        timestampMillis = postTimeMillis,
                        packageName = pkg,
                        appName = appRule.appName,
                        senderTitle = storedTitle,
                        // The captured body is the notification's
                        // own text or nothing (Truth Car 3,
                        // appendix): a title-only notification
                        // stores an EMPTY body — the digest prints
                        // the title alone. The old fallback
                        // sentence was invented copy wearing the
                        // sender's name.
                        messageBody = text
                    )
                )
                pruneCaptured(postTimeMillis)
                if (key != null) {
                    dedupe.record(
                        key,
                        SeenCapture(
                            fingerprint = fingerprint,
                            packageName = pkg,
                            senderTitle = storedTitle,
                            messageBody = text,
                            postTimeMillis = postTimeMillis
                        )
                    )
                }
                // Issue #120: a one-time code is copied, never
                // cancelled — the original keeps its shade entry
                // (and any action it carries) because the code
                // expires in minutes while the session runs far
                // longer.
                if (!otpLike) {
                    try {
                        cancelNotification(key)
                    } catch (error: Exception) {
                        Log.w(TAG, "cancelNotification failed for $pkg", error)
                    }
                }
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Legacy 1-arg callback (pre-reason API shape). Removal
        // handling lives in handleRemoval; the 3-arg override
        // below deliberately does NOT call super, because super's
        // default implementation forwards to this method and the
        // row would be deleted twice.
        handleRemoval(sbn)
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification?,
        rankingMap: NotificationListenerService.RankingMap?,
        reason: Int
    ) {
        // Issue #45 (C5): our own cancels must not delete the held
        // copy — cancelling the shade entry after capture is this
        // listener's own suppression doing its job, and the digest
        // copy is the whole point of holding it. Every other
        // reason (the sender recalled it, the app cancelled it,
        // the user dismissed it) means the message is gone, so the
        // held copy goes too instead of resurrecting in the digest.
        if (reason == NotificationListenerService.REASON_LISTENER_CANCEL ||
            reason == NotificationListenerService.REASON_LISTENER_CANCEL_ALL
        ) {
            return
        }
        // No super call: NotificationListenerService's default
        // 3-arg implementation forwards to the 1-arg override,
        // which would run handleRemoval a second time.
        handleRemoval(sbn)
    }

    private fun handleRemoval(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        val pkg = sbn.packageName ?: return
        // Snapshot synchronously, same discipline as the post path.
        val key = sbn.key
        val postTimeMillis = sbn.postTime
        val extras = notification.extras
        val rawTitle = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim().orEmpty()
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim().orEmpty()

        // The held copy's identity: prefer the capture this
        // instance recorded (its senderTitle carries the app-name
        // fallback applied at insert). Fall back to the removed
        // notification's own snapshot — resolved against the app
        // rule below for the same fallback — when this instance
        // never saw the post (e.g. after a process restart).
        val seen = key?.let { dedupe.forget(it) }

        scope.launch {
            val dao = com.example.AppContainer.dao(applicationContext) /* ISSUE-67: single construction point */
            val target = seen ?: run {
                val appRule = dao.getAllAppsSnapshot().find { it.packageName == pkg }
                SeenCapture(
                    fingerprint = NotificationCapturePolicy.contentFingerprint(rawTitle, text),
                    packageName = pkg,
                    senderTitle = rawTitle.ifEmpty { appRule?.appName ?: rawTitle },
                    messageBody = text,
                    postTimeMillis = postTimeMillis
                )
            }
            dao.deleteCapturedNotificationMatching(
                target.packageName,
                target.senderTitle,
                target.messageBody,
                target.postTimeMillis
            )
        }
    }

    /** Issue #45 (C5) retention, run after every insert: drop rows
     *  older than [RETENTION_MILLIS] relative to the newest
     *  capture's own postTime, then trim the table to the newest
     *  [MAX_CAPTURED_ROWS] rows. Failures surface through the
     *  scope's exception handler — logged, never silent. */
    private suspend fun pruneCaptured(latestPostTimeMillis: Long) {
        val dao = com.example.AppContainer.dao(applicationContext) /* ISSUE-67: single construction point */
        dao.pruneCapturedNotificationsOlderThan(latestPostTimeMillis - RETENTION_MILLIS)
        dao.trimCapturedNotificationsToNewest(MAX_CAPTURED_ROWS)
    }

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
