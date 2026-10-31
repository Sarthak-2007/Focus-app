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
            // Issue #121: calendar reminders and events stand with
            // calls and alarms in the time-critical class — a
            // reminder captured and cancelled here is an alert the
            // user never sees, and calendar apps do not post
            // reminders as CATEGORY_ALARM. Spared HERE, at the
            // capture decision, so they never enter storage and are
            // never cancelled; this composes with any later removal
            // of the content-storage path (#100) because the
            // exemption sits strictly upstream of it.
            Notification.CATEGORY_REMINDER,
            Notification.CATEGORY_EVENT,
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
 * produced it. [postTimeMillis] is the row's timestampMillis —
 * together with the package it is the pair the DAO matches stored
 * rows by, for both the dedupe-replace and the removal delete.
 * The [fingerprint] lives ONLY in this in-memory tracker (it dies
 * with the process and is never persisted): since the issue #138
 * CUT the stored row itself carries no content to match against.
 */
data class SeenCapture(
    val fingerprint: String,
    val packageName: String,
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
 * - Saves a held notification's METADATA (app + capture time) to Room persistent
 *   storage so the held count and digest survive background restarts. Notification
 *   CONTENT — sender and body — is never stored (issue #138 CUT, Sarthak's ruling):
 *   title/text are read in memory only to judge the hold (dedupe fingerprint,
 *   one-time-code exemption) and are never written to disk.
 */
/**
 * The hold rule (Audit R3, safe-break-lived #2): a notification is
 * held — digested and cancelled — when its app's tier is not OPEN,
 * EXCEPT that a Safe Break releases the apps the break itself
 * unlocks: a PEEK-tier app is freely open for the break's length
 * (the accessibility service's Safe Break rule), so holding its
 * notifications while its owner chats inside it made the "unlock"
 * half a lie — messages arrived silently into the HELD digest.
 * LOCKED apps stay held through the break, exactly as they stay
 * locked. Pure, so the rule is unit-tested without the service.
 */
internal fun notificationHeldDuringSession(isInSafeBreak: Boolean, tier: AppBlockTier): Boolean =
    tier != AppBlockTier.OPEN && !(isInSafeBreak && tier == AppBlockTier.PEEK_2M)

class MonolithNotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO +
            CoroutineExceptionHandler { _, error ->
                Log.e(TAG, "Notification capture coroutine failed", error)
            }
    )

    // Issue #45 (C5) dedupe: updates of a notification this
    // instance already holds, recognised by sbn.key + content
    // fingerprint. In-memory only, by design — a restart loses the
    // tracker, never a message; the duplicate row that used to be
    // the restart's worst case is now stopped by the durable
    // quadruple check on the insert path (issue #155 WING-F).
    internal val dedupe = CaptureDedupeTracker()

    companion object {
        private const val TAG = "MonolithNotifListener"

        // Issue #45 (C5) retention bounds for the held-metadata table:
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

        /**
         * Issue #121: the categories of [packageName]'s currently
         * active notifications, read from the bound listener — the
         * notification proof the screen doors consult through
         * [TimeCriticalAlertGate] before covering a ringing alarm
         * or a tapped calendar reminder. Reads only the LIVE shade
         * (never the stored digest), so it is independent of the
         * captured-content storage path. Empty when no listener is
         * bound (no proof, never a guess).
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
            val coveringSession = dao.getSessionCoveringInstant(postTimeMillis) ?: return@launch

            // Issue #138 CUT (supersedes Audit R6 #340 F68's
            // consent-gated body storage): notification content is
            // NEVER stored — no sender, no body, under any consent.
            // Title/text were snapshotted above only to judge the
            // hold in memory (dedupe fingerprint, OTP exemption);
            // the row written below is metadata: package, app, time.

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
            if (appRule != null &&
                notificationHeldDuringSession(coveringSession.isInSafeBreak, appRule.resolvedTier)
            ) {
                if (key != null) {
                    // Dedupe, second gate (inside the coroutine, so
                    // two rapid identical posts that both passed the
                    // first gate before either recorded cannot both
                    // insert): unchanged content stops here too.
                    val previous = dedupe.seen(key)
                    if (previous?.fingerprint == fingerprint) return@launch
                    // Changed content for a key already held: the
                    // old metadata row is superseded — find it by
                    // its stored identity (package + postTime) and
                    // delete it, so the digest holds exactly one,
                    // current, row per held notification.
                    if (previous != null) {
                        dao.findCapturedNotificationMatching(
                            previous.packageName,
                            previous.postTimeMillis
                        )?.let { stale -> dao.deleteCapturedNotification(stale.id) }
                    }
                }
                // Issue #155 WING-F (SMALL-2): the two dedupe gates
                // above ride the IN-MEMORY tracker, which dies with
                // this process. The class doc already concedes the
                // consequence — "after a process restart the worst
                // case is one duplicate row" — but the digest has a
                // reader that turns that duplicate into a false
                // claim: the launcher badge (MonolithShieldService)
                // and the Deck's HELD chip both print this table's
                // SIZE, so every re-capture of a still-live held
                // notification (the rebind sweep re-walks the shade;
                // a never-cancelled OTP entry stays live by design)
                // inflated the badge by one per restart. The table
                // itself is the durable dedupe: the same stored
                // identity (package + postTime) the replace/removal
                // paths already treat as a capture's identity,
                // checked before inserting. An existing identical
                // row means this exact capture is already held —
                // record the tracker and fall through to the cancel
                // below, but insert nothing.
                val alreadyHeld = dao.findCapturedNotificationMatching(
                    pkg, postTimeMillis
                ) != null
                if (!alreadyHeld) {
                    dao.insertCapturedNotification(
                        CapturedNotificationEntity(
                        // The row's timestamp is the capture
                        // snapshot — sbn.postTime — not the time
                        // this coroutine happened to run. Metadata
                        // only (issue #138 CUT): no sender, no body
                        // is stored — Truth Car 3's "never invent a
                        // body" law is now the stronger "never
                        // store content at all".
                        timestampMillis = postTimeMillis,
                        packageName = pkg,
                        appName = appRule.appName
                        )
                    )
                }
                pruneCaptured(postTimeMillis)
                if (key != null) {
                    dedupe.record(
                        key,
                        SeenCapture(
                            fingerprint = fingerprint,
                            packageName = pkg,
                            postTimeMillis = postTimeMillis
                        )
                    )
                }
                // Issue #120: a one-time code is recorded as held
                // (metadata only), never cancelled — the original
                // keeps its shade entry (and any action it carries)
                // because the code expires in minutes while the
                // session runs far longer.
                if (!otpLike) {
                    try {
                        cancelNotification(key)
                    } catch (error: Exception) {
                        // #338 F2: diagnostics stay content-free —
                        // no package names in logcat. Freedom's
                        // release build logs blocked packages and
                        // URLs; this line was Monolith's one leak
                        // of that kind.
                        Log.w(TAG, "cancelNotification failed", error)
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
        sbn?.notification ?: return
        val pkg = sbn.packageName ?: return
        // Snapshot synchronously, same discipline as the post path.
        val key = sbn.key
        val postTimeMillis = sbn.postTime

        // The held row's identity is package + postTime (issue
        // #138 CUT: no content is stored, so none is needed to
        // match). Prefer the capture this instance recorded; the
        // fallback is the removed notification's own package +
        // postTime, which is the same identity whether or not this
        // instance ever saw the post (e.g. after a process
        // restart) — so no extras are read here at all.
        val seen = key?.let { dedupe.forget(it) }

        scope.launch {
            val dao = com.example.AppContainer.dao(applicationContext) /* ISSUE-67: single construction point */
            dao.deleteCapturedNotificationMatching(
                seen?.packageName ?: pkg,
                seen?.postTimeMillis ?: postTimeMillis
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
