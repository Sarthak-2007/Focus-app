package com.example.data

import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Process
import android.os.SystemClock
import android.provider.Settings
import android.os.PowerManager
import com.example.service.MonolithAccessibilityService
import com.example.service.MonolithDeviceAdminReceiver
import com.example.service.MonolithShieldService
import com.example.service.RoutineScheduler
import com.example.service.TactileSoundEngine
import com.example.service.UsageAccess
import com.example.service.Warrant
import com.example.service.WatchItemId
import com.example.service.WatchStore
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

data class SystemGuardStatus(
    val hasUsageStatsPermission: Boolean = false,
    val hasOverlayPermission: Boolean = false,
    val isAccessibilityServiceEnabled: Boolean = false,
    val isNotificationListenerEnabled: Boolean = false,
    val isDeviceAdminEnabled: Boolean = false,
    val isMonotonicClockIntact: Boolean = true,
    val isHmacSignatureValid: Boolean = true,
    val clockDriftSeconds: Long = 0L,
    val systemBootCount: Int = 1,
    val installedLauncherAppsCount: Int = 12
) {
    val readinessCount: Int
        get() = listOf(
            isAccessibilityServiceEnabled,
            hasUsageStatsPermission,
            isNotificationListenerEnabled,
            isDeviceAdminEnabled
        ).count { it }
}

/**
 * The three outcomes of a valve-completed emergency break (TOKEN-SPEC
 * §4.1) — exactly these, no fourth. [RefusedNoToken] leaves the session
 * sealed and writes nothing; [Completed] means the spend row and the
 * disarm committed in the same transaction.
 */
sealed interface BreakResult {
    object Completed : BreakResult
    object RefusedNoToken : BreakResult
    object NoActiveSession : BreakResult
}

/**
 * The prune's record (Ordner lane, Mikro M5): how many rows the last
 * unannounced prune(s) took off the app list, and whether any stored
 * ruling went with them. Accumulates in prefs until the Shield strip
 * has been shown and dismissed — the strip prints this record and
 * nothing beyond it.
 */
data class PruneNote(
    val appsRemoved: Int,
    val rulesRemoved: Boolean
)

class MonolithRepository(
    private val context: Context,
    private val dao: MonolithDao
) {
    val allAppsFlow: Flow<List<BlockedAppEntity>> = dao.getAllApps()
    val allProfilesFlow: Flow<List<FocusProfileEntity>> = dao.getAllProfiles()
    val activeSessionFlow: Flow<FocusSessionEntity?> = dao.getActiveSessionFlow()
    val sessionHistoryFlow: Flow<List<FocusSessionEntity>> = dao.getSessionHistoryFlow()
    val interceptLogsFlow: Flow<List<InterceptLogEntity>> = dao.getInterceptLogsFlow()
    val capturedNotificationsFlow: Flow<List<CapturedNotificationEntity>> = dao.getCapturedNotificationsFlow()
    val allTasksFlow: Flow<List<FocusTaskEntity>> = dao.getAllTasksFlow()
    val allSitesFlow: Flow<List<BlockedSiteEntity>> = dao.getAllSitesFlow()
    val allRoutinesFlow: Flow<List<RoutineEntity>> = dao.getAllRoutinesFlow()

    companion object {
        private val seedMutex = Mutex()
        const val PEEK_DURATION_SECONDS = 120 // 2-Minute Peek
        const val PEEK_DURATION_MILLIS = 120 * 1000L // 2-Minute Peek lease in ms
        const val PEEK_COOLDOWN_MILLIS = 20 * 60 * 1000L // 20-Minute Mandatory Cooldown
        const val STOPWATCH_MIN_FLOOR_SECONDS = 20 * 60 // 20-Minute Minimum Floor
        const val SAFE_BREAK_DURATION_SECONDS = 10 * 60 // 10-Minute Safe Break
        const val HMAC_TAMPER_PENALTY_SECONDS = 15 * 60 // 15-Minute Penalty Lock

        /** The outcome-reason constant of the emergency-break path
         *  (TOKEN-SPEC §4.5) — exactly this string, never free text.
         *  The interim "Grace Cancel" string-sniff in
         *  [disarmActiveSession] correctly returns false for it. */
        const val EMERGENCY_BREAK_REASON = "Emergency Break"

        /** What a completed valve hold pays, in the valve's own terms
         *  (TOKEN-SPEC §2 — stored on the spend row). */
        const val EMERGENCY_BREAK_HELD_SECONDS = 60

        /** The shipped routine-session recogniser (TOKEN-SPEC §4.1):
         *  a session started by a routine carries this profile prefix.
         *  Kept for recognising legacy pre-rebuild rows (arbitration
         *  A3); new rows are matched by routineId. */
        private const val ROUTINE_PROFILE_PREFIX = "Routine: "

        // Attendance evidence mask bits (Sten §1.6.1). E1 is set at
        // materialise; E2 on foreground return; E3 by finalise when a
        // blocked attempt landed inside the window; E4 when the 60s
        // valve hold starts.
        const val EVIDENCE_E1_START = 1
        const val EVIDENCE_E2_FOREGROUND = 2
        const val EVIDENCE_E3_INTERCEPT = 4
        const val EVIDENCE_E4_HOLD_START = 8

        /** One evidence instant covers this long in the finalise sum. */
        const val EVIDENCE_COVERAGE_MILLIS = 300_000L

        /** Attendance counts from fifteen verified minutes (J1). */
        const val ATTENDANCE_FLOOR_SECONDS = 900

        /** F-SCH-1: the Drawer capture's prefs file (DayWordStore's
         *  PREFS_NAME, drawer lane) — the drain's source of record
         *  until the words land in `day_notes`. */
        const val DAY_WORDS_PREFS_NAME = "monolith_day_words"

        /** Chrome's prefs file + PARK's first-run bit (chrome C4). */
        const val CHROME_PREFS_NAME = "monolith_chrome"
        const val PARK_INTRO_DONE_KEY = "park_intro_done"

        /** The closed declared-word list (DayWordStore.WORDS).
         *  Never TEST: the drain refuses any other word, so no
         *  test-day row can ever reach `day_notes`. */
        val DAY_WORDS: Set<String> = setOf("REST", "ILL", "FAMILY")

        /** Slice E (Nocke §2.2): the shield-gap law — a beat older
         *  than this (5 missed 2s beats) classifies the session
         *  death-booked and sets the latch. */
        const val SHIELD_GAP_THRESHOLD_MILLIS = 10_000L

        /** The Drawer's page size (DRW-04): reads fetch one row
         *  past it as the has-earlier probe. */
        const val DRAWER_PAGE_SIZE = 50

        /** Slice E (F-K1): the seal marker's prefs home — written
         *  synchronously by SessionSealTransition inside the seal
         *  act, BEFORE the Throw sounds, so a process that dies
         *  mid-seal leaves proof the seal landed. Read by the
         *  recovery path (adopt silently, never re-Throw) and by
         *  the ESR-02 repository refusal below. */
        const val SEAL_PREFS_NAME = "monolith_seal"
        const val SEAL_MARKER_SESSION_KEY = "sealed_session_id"
        const val SEAL_MARKER_WALL_KEY = "sealed_wall_ms"

        /** The prune note's prefs home (Ordner lane, Mikro M5): a
         *  record of what the catalog reconcile removed, kept until
         *  Shield has shown it once. Prefs, not Room — announcing a
         *  table change needs no column in the table. */
        const val APP_LIST_PREFS_NAME = "monolith_app_list"
        const val PRUNE_NOTE_COUNT_KEY = "prune_note_count"
        const val PRUNE_NOTE_RULES_KEY = "prune_note_rules_removed"
    }

    /** The seal marker's session id, or -1 when no seal has ever
     *  landed in this install (Slice E, F-K1 / ESR-02). */
    fun sealedSessionIdFromMarker(): Int =
        context.getSharedPreferences(SEAL_PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(SEAL_MARKER_SESSION_KEY, -1)

    suspend fun ensureSeededAndSyncPackages() = withContext(Dispatchers.IO) {
        seedMutex.withLock {
        if (dao.getAllSitesSnapshot().isEmpty()) {
            dao.insertSites(BlockedBrowserDomains.defaultDomains.map { BlockedSiteEntity(it) })
        }
        if (dao.getGuardSettingsSnapshot() == null) {
            dao.insertGuardSettings(GuardSettingsEntity())
        }
        // Routines: a fresh install seeds his real day directly and is
        // marked reseed-done (it never "migrates" and never sees the
        // reset note); an existing install runs the version-blind
        // reseed transform once (Sten §2).
        if (dao.getAllRoutinesSnapshot().isEmpty()) {
            dao.insertRoutines(routineTargets())
            val fresh = dao.getGuardSettingsSnapshot()
            if (fresh != null && !fresh.routineReseedV1Done) {
                dao.insertGuardSettings(
                    fresh.copy(routineReseedV1Done = true, routineReseedNoteDismissed = true)
                )
            }
        } else {
            reseedRoutinesV1()
        }
        try {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolvedList = pm.queryIntentActivities(mainIntent, 0)
            val installed = resolvedList.mapNotNull { resolveInfo ->
                val pkg = resolveInfo.activityInfo?.packageName ?: return@mapNotNull null
                if (pkg == context.packageName || pkg.startsWith("com.android.settings")) {
                    return@mapNotNull null
                }
                val label = resolveInfo.loadLabel(pm)?.toString()
                    ?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                InstalledPackage(pkg, label)
            }
            // THE CATALOG IS THE PHONE'S TRUTH (Ordner lane, Sarthak's
            // complaint Oct 29 22:40: the list carried apps he never
            // installed). There is no blind factory seed any more:
            // this sync reconciles the table to exactly the installed
            // launchable set — rows for absent packages are PRUNED,
            // present packages are discovered without a cap, and the
            // factory presets survive only as birth rules applied to
            // packages that actually exist on this phone. An empty
            // query result can only mean the query failed (the
            // <queries> declaration in the manifest makes every
            // launcher app visible), so an empty result skips the
            // reconcile entirely rather than pruning his rules away.
            if (installed.isNotEmpty()) {
                val existingRows = dao.getAllAppsSnapshot()
                val plan = AppCatalogReconcile.reconcile(
                    existing = existingRows,
                    installed = installed,
                    usageMinutes = queryRealTodayUsageMinutes(),
                    categorize = ::categorizePackage
                )
                if (plan.toInsert.isNotEmpty()) {
                    dao.insertApps(plan.toInsert)
                }
                plan.usageUpdates.forEach { dao.updateApp(it) }
                if (plan.toPrune.isNotEmpty()) {
                    // M5 (Mikro): the prune announces itself once, so
                    // record what it took BEFORE the rows leave —
                    // how many, and whether any stored ruling went
                    // with them. The Shield strip prints exactly
                    // this record, once, and nothing it can't prove.
                    val prunedRows = existingRows.filter {
                        it.packageName in plan.toPrune
                    }
                    recordPruneNote(
                        appsRemoved = prunedRows.size,
                        rulesRemoved = prunedRows.any {
                            it.resolvedTier != AppBlockTier.OPEN
                        }
                    )
                    dao.deleteAppsByPackage(plan.toPrune)
                }
            }
        } catch (_: Exception) {
            // Safe fallback if package querying is restricted
        }

        val existingProfiles = dao.getAllProfilesSnapshot()
        if (existingProfiles.isEmpty()) {
            val defaultProfiles = listOf(
                FocusProfileEntity(
                    name = "Study Loadout",
                    codeName = "Study Mode",
                    description = "Locks Social & Games, sets YouTube & Messaging to 2m Peek, arms Shorts Sniper.",
                    defaultMinutes = 50,
                    blockedCategoriesCsv = "SHORT_VIDEO,SOCIAL,GAMES",
                    scheduleLabel = "09:00 – 13:00 • Weekdays",
                    isScheduleEnabled = true,
                    startHour = 9,
                    startMinute = 0,
                    endHour = 13,
                    endMinute = 0
                ),
                FocusProfileEntity(
                    name = "Exam Lockdown",
                    codeName = "Exam Mode",
                    description = "Full 90-minute exam shield across all feeds, games, and video apps.",
                    defaultMinutes = 90,
                    blockedCategoriesCsv = "SHORT_VIDEO,SOCIAL,GAMES,MESSAGING,BROWSERS",
                    scheduleLabel = "14:00 – 18:00 • Exam Prep",
                    isScheduleEnabled = true,
                    startHour = 14,
                    startMinute = 0,
                    endHour = 18,
                    endMinute = 0
                ),
                FocusProfileEntity(
                    name = "Sleep Loadout",
                    codeName = "Sleep Guard",
                    description = "Locks every app except Phone, Clock, and Maps so you sleep on time.",
                    defaultMinutes = 60,
                    blockedCategoriesCsv = "SHORT_VIDEO,SOCIAL,GAMES,SHOPPING,BROWSERS",
                    scheduleLabel = "22:30 – 07:00 • Nightly",
                    isScheduleEnabled = true,
                    startHour = 22,
                    startMinute = 30,
                    endHour = 7,
                    endMinute = 0
                ),
                FocusProfileEntity(
                    name = "Quick Homework",
                    codeName = "25m Sprint",
                    description = "Blocks short-form video while keeping messaging on 2m Peek for study groups.",
                    defaultMinutes = 25,
                    blockedCategoriesCsv = "SHORT_VIDEO,GAMES",
                    scheduleLabel = "19:00 – 21:00 • Daily",
                    isScheduleEnabled = false,
                    startHour = 19,
                    startMinute = 0,
                    endHour = 21,
                    endMinute = 0
                )
            )
            dao.insertProfiles(defaultProfiles)
        }

        // Fabricated demo history STRUCK in this lane too (Lie
        // Register 3 F-3; caught by Ren's pre-merge S1 sweep,
        // Oct 29): this copy of the seed block still inserted
        // three invented sessions, invented intercept logs and
        // invented captured notifications on first run — the
        // frozen base predated trunk's scrub. Fresh installs
        // now start with an empty, true history.

        val existingTasks = dao.getAllTasksSnapshot()
        if (existingTasks.isEmpty()) {
            val defaultTasks = listOf(
                FocusTaskEntity(
                    title = "Finish Chapter 4 Problem Set (#1–6)",
                    notesOrSubsteps = FocusTaskEntity.serializeSubSteps(
                        listOf(
                            TaskSubStep(0, "Draw free-body diagrams for problems #1–3", true),
                            TaskSubStep(1, "Solve equilibrium equations for #1–3", false),
                            TaskSubStep(2, "Complete problems #4–6 & verify units", false)
                        )
                    ),
                    estimatedMinutes = 25,
                    actualSecondsSpent = 420,
                    categoryTag = "Study",
                    queueOrder = 0,
                    status = TaskQueueStatus.ACTIVE.name
                ),
                FocusTaskEntity(
                    title = "Synthesize Lecture 9 Architecture Notes",
                    notesOrSubsteps = FocusTaskEntity.serializeSubSteps(
                        listOf(
                            TaskSubStep(0, "Extract L1/L2 cache hierarchy diagrams", false),
                            TaskSubStep(1, "Summarize virtual page table walkthrough", false),
                            TaskSubStep(2, "Write 3 self-test recall prompts", false)
                        )
                    ),
                    estimatedMinutes = 25,
                    actualSecondsSpent = 0,
                    categoryTag = "Writing",
                    queueOrder = 1,
                    status = TaskQueueStatus.QUEUED.name
                ),
                FocusTaskEntity(
                    title = "Debug Dynamic Programming Memo Table",
                    notesOrSubsteps = FocusTaskEntity.serializeSubSteps(
                        listOf(
                            TaskSubStep(0, "Trace base case indices on paper first", false),
                            TaskSubStep(1, "Fix off-by-one transition in memo[i][j]", false),
                            TaskSubStep(2, "Run edge-case unit test suite", false)
                        )
                    ),
                    estimatedMinutes = 45,
                    actualSecondsSpent = 0,
                    categoryTag = "Code",
                    queueOrder = 2,
                    status = TaskQueueStatus.QUEUED.name
                )
            )
            dao.insertTasks(defaultTasks)
        } else {
            // Deduplicate any duplicate tasks or sessions created by prior race conditions.
            // PERMANENCE GUARD: a COMPLETED task is a stored record (the
            // Drawer) — repeat-titled completions are the normal shape of
            // a year, and this pass must never delete one. Stored rows are
            // skipped entirely: not deleted, and not registered as a
            // dedup key against anything.
            val seenTaskTitles = mutableSetOf<String>()
            for (task in existingTasks.sortedBy { it.id }) {
                if (task.resolvedStatus == TaskQueueStatus.COMPLETED) continue
                val key = "${task.title.lowercase()}|${task.status}"
                if (!seenTaskTitles.add(key)) {
                    dao.deleteTaskById(task.id)
                }
            }
            val allSessions = dao.getSessionHistorySnapshot()
            val seenSessionKeys = mutableSetOf<String>()
            for (sess in allSessions.sortedBy { it.id }) {
                if (!sess.isActive) {
                    val key = "${sess.intention}|${sess.targetDurationSeconds}|${sess.completedTasksSummary}"
                    if (!seenSessionKeys.add(key)) {
                        dao.deleteSessionById(sess.id)
                    }
                }
            }
        }
        drainDayWordsLocked()
        }
    }

    /**
     * F-SCH-1 drain: moves the Drawer's declared day words from
     * their prefs capture into the `day_notes` Room table — their
     * durable home from schema v9 (the table rides Migration 8→9).
     * The capture contract is the Drawer lane's DayWordStore:
     * SharedPreferences file [DAY_WORDS_PREFS_NAME], key = dateIso
     * (yyyy-MM-dd), value = the declared word. Both lanes build
     * this repository on the application context, so this reads
     * the very file the capture writes.
     *
     * Runs inside every seed-sync (the first seed-sync after the
     * 8→9 upgrade performs the move) and is idempotent: drained
     * keys are removed from prefs, so a later run finds nothing
     * to move. Only words in [DAY_WORDS] drain. Anything else in
     * the prefs file — an unknown word, a non-string value — is
     * left exactly where it lies: never silently dropped, and
     * never stored, so a TEST row in day_notes is impossible by
     * this refusal (the word list is closed; this app does not
     * know test days). A date already present in the table takes
     * the prefs value: until drained, prefs is the capture of
     * record.
     */
    suspend fun drainDayWordsFromPrefs() = withContext(Dispatchers.IO) {
        seedMutex.withLock { drainDayWordsLocked() }
    }

    // PARK's first-run fact (chrome C4, spec §5.7): whether the
    // store has EVER measured is a first-run-only condition — after
    // the first run, a cold start's unmeasured instant is a sweep
    // that hasn't landed yet, not an invitation. One persisted bit
    // keeps the two apart.
    fun parkIntroPending(): Boolean =
        context.getSharedPreferences(CHROME_PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(PARK_INTRO_DONE_KEY, false).not()

    // ------------------------------------------------------------------
    // THE PRUNE NOTE (Mikro M5): accumulate — read — clear.
    // ------------------------------------------------------------------

    private fun recordPruneNote(appsRemoved: Int, rulesRemoved: Boolean) {
        if (appsRemoved <= 0) return
        val prefs = context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(PRUNE_NOTE_COUNT_KEY, prefs.getInt(PRUNE_NOTE_COUNT_KEY, 0) + appsRemoved)
            .putBoolean(
                PRUNE_NOTE_RULES_KEY,
                rulesRemoved || prefs.getBoolean(PRUNE_NOTE_RULES_KEY, false)
            )
            .apply()
    }

    /** The pending prune record, or null when nothing was taken off
     *  the list since the last dismissal. */
    fun pendingPruneNote(): PruneNote? {
        val prefs = context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt(PRUNE_NOTE_COUNT_KEY, 0)
        if (count <= 0) return null
        return PruneNote(
            appsRemoved = count,
            rulesRemoved = prefs.getBoolean(PRUNE_NOTE_RULES_KEY, false)
        )
    }

    fun clearPruneNote() {
        context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(PRUNE_NOTE_COUNT_KEY)
            .remove(PRUNE_NOTE_RULES_KEY)
            .apply()
    }

    fun markParkIntroDone() {
        context.getSharedPreferences(CHROME_PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(PARK_INTRO_DONE_KEY, true).apply()
    }

    private suspend fun drainDayWordsLocked() {
        val prefs = context.getSharedPreferences(DAY_WORDS_PREFS_NAME, Context.MODE_PRIVATE)
        val captured = prefs.all
        if (captured.isEmpty()) return
        val drained = captured.mapNotNull { (dateIso, value) ->
            val word = value as? String ?: return@mapNotNull null
            if (word in DAY_WORDS) DayNoteEntity(dateIso = dateIso, word = word) else null
        }
        if (drained.isEmpty()) return
        dao.insertDayNotes(drained)
        val editor = prefs.edit()
        drained.forEach { editor.remove(it.dateIso) }
        editor.apply()
    }

    private fun categorizePackage(pkg: String, label: String): AppCategory {
        val lower = "$pkg $label".lowercase()
        return when {
            lower.contains("tiktok") || lower.contains("youtube") || lower.contains("instagram") || lower.contains("reel") ->
                AppCategory.SHORT_VIDEO
            lower.contains("twitter") || lower.contains("reddit") || lower.contains("snapchat") || lower.contains("facebook") ->
                AppCategory.SOCIAL
            lower.contains("game") || lower.contains("clash") || lower.contains("roblox") || lower.contains("play") ->
                AppCategory.GAMES
            lower.contains("whatsapp") || lower.contains("discord") || lower.contains("telegram") || lower.contains("message") ->
                AppCategory.MESSAGING
            lower.contains("chrome") || lower.contains("browser") || lower.contains("firefox") || lower.contains("brave") ->
                AppCategory.BROWSERS
            else -> AppCategory.SHOPPING
        }
    }

    fun queryRealTodayUsageMinutes(): Map<String, Int> {
        if (!hasUsageStatsPermission()) return emptyMap()
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return emptyMap()
            val end = System.currentTimeMillis()
            val start = end - (12 * 60 * 60 * 1000L)
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
            stats?.associate {
                it.packageName to ((it.totalTimeInForeground / 60_000L).toInt().coerceAtLeast(0))
            } ?: emptyMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }

    /**
     * Active Phone & VoIP Call Bypass:
     * Monitors AudioManager call states (MODE_RINGTONE, MODE_IN_CALL, MODE_IN_COMMUNICATION)
     * so incoming cellular, WhatsApp, or Telegram voice and video calls are never blocked mid-ring.
     */
    fun isPhoneOrVoipCallActive(): Boolean {
        return try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
            val mode = am.mode
            mode == AudioManager.MODE_RINGTONE ||
                mode == AudioManager.MODE_IN_CALL ||
                mode == AudioManager.MODE_IN_COMMUNICATION
        } catch (_: Exception) {
            false
        }
    }

    fun queryForegroundBlockedApp(blockedPackages: Set<String>): String? {
        if (!hasUsageStatsPermission() || blockedPackages.isEmpty()) return null
        if (isPhoneOrVoipCallActive()) return null
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return null
            val end = System.currentTimeMillis()
            val start = end - 15_000L
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
            val recent = stats
                ?.filter { it.packageName in blockedPackages && it.lastTimeUsed >= (end - 8_000L) }
                ?.maxByOrNull { it.lastTimeUsed }
            recent?.packageName
        } catch (_: Exception) {
            null
        }
    }

    /** Usage access as AppOps actually reports it: three-valued.
     *  The old Boolean collapsed UNSET (never asked, fresh device)
     *  into DENIED, which lied on first runs. */
    fun usageAccess(): UsageAccess {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
                ?: return UsageAccess.UNSET
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            when (mode) {
                AppOpsManager.MODE_ALLOWED -> UsageAccess.ALLOWED
                AppOpsManager.MODE_DEFAULT -> UsageAccess.UNSET
                else -> UsageAccess.DENIED
            }
        } catch (_: Exception) {
            UsageAccess.UNSET
        }
    }

    fun hasUsageStatsPermission(): Boolean = usageAccess() == UsageAccess.ALLOWED

    /**
     * THE STANDING WARRANT (chrome C3, spec §4.5): the wire verdict
     * re-based on the Watch Store. The inputs are the same living
     * facts the deleted liveWarrant() read — accessibility bound,
     * the residency stamp, usage access, shield-protecting — but the
     * state logic is the store's one derivation
     * ([WatchStore.deriveWarrant]); this function only gathers.
     * Synchronous, no coroutine, no deadline: the slide reads it at
     * DOWN and again at the gate crossing, and the verdict is never
     * older than the gesture.
     */
    fun standingWarrant(): Warrant {
        val a11yBound = MonolithAccessibilityService.instance != null
        val stamped = a11yBound && WatchStore.stampedThisResidency()
        return WatchStore.deriveWarrant(
            a11yBound = a11yBound,
            stamped = stamped,
            usage = usageAccess(),
            shieldProtecting = MonolithShieldService.protecting
        )
    }

    /**
     * One grant-check query for the Watch Store's sweep (chrome C3):
     * the present truth of one permission item, read fresh. The two
     * BIND items answer from their services' existence; the four
     * GRANT CHECK items from their platform queries. Each sweep
     * query lands independently — this function is called once per
     * item, in parallel, by WatchStore.runSweep.
     */
    fun watchGrantQuery(item: WatchItemId): Boolean = when (item) {
        WatchItemId.ACCESSIBILITY -> MonolithAccessibilityService.instance != null
        WatchItemId.USAGE_ACCESS -> usageAccess() == UsageAccess.ALLOWED
        WatchItemId.DEVICE_ADMIN -> isDeviceAdminActive()
        WatchItemId.NOTIFICATION_LISTENER ->
            com.example.service.MonolithNotificationListener.instance != null
        WatchItemId.OVERLAY -> hasOverlayPermission()
        WatchItemId.BATTERY -> isBatteryOptimizationIgnored()
        else -> false
    }

    /** Battery: whether the OS is told to leave Monolith alone.
     *  The read the dissolved Guard screen never had — its BATTERY &
     *  ADMIN row answered with the admin bit for both. */
    fun isBatteryOptimizationIgnored(): Boolean {
        return try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                ?: return false
            pm.isIgnoringBatteryOptimizations(context.packageName)
        } catch (_: Exception) {
            false
        }
    }

    /** The artifact ids actually forged by real sessions — the
     *  Drawer cabinet's forged state (chrome C10). */
    suspend fun forgedArtifactIds(): Set<String> =
        dao.getForgedArtifactIds().toSet()

    fun hasOverlayPermission(): Boolean {
        return try {
            Settings.canDrawOverlays(context)
        } catch (_: Exception) {
            false
        }
    }

    fun isAccessibilityEnabled(): Boolean {
        return try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            enabledServices.contains(context.packageName, ignoreCase = true)
        } catch (_: Exception) {
            false
        }
    }

    fun isNotificationListenerEnabled(): Boolean {
        return try {
            val enabledListeners = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            ) ?: return false
            enabledListeners.contains(context.packageName, ignoreCase = true)
        } catch (_: Exception) {
            false
        }
    }

    fun isDeviceAdminActive(): Boolean {
        return try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
                ?: return false
            val adminComponent = ComponentName(context, MonolithDeviceAdminReceiver::class.java)
            dpm.isAdminActive(adminComponent)
        } catch (_: Exception) {
            false
        }
    }

    fun querySystemBootCount(): Int {
        return try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, 1)
        } catch (_: Exception) {
            1
        }
    }

    /**
     * Tamper-Proof Monotonic Clock + Boot Count + Hardware Keystore HMAC Verification:
     * 1. Cross-checks wall-clock elapsed time against hardware `SystemClock.elapsedRealtime()` and `BOOT_COUNT`.
     *    Advancing the phone's system clock forward by +2 hours is ignored because elapsed time is anchored to
     *    `SystemClock.elapsedRealtime()`.
     * 2. Across device reboots (`LOCKED_BOOT_COMPLETED` where `currentBoot != session.bootCount` or
     *    `nowMono < session.lastTickElapsedRealtimeMillis`), preserves `session.accumulatedBeforeRebootSeconds`
     *    and adds `(nowMono / 1000L).toInt()`, so a reboot never resets or skips the timer.
     * 3. Verifies the AndroidKeyStore `HmacSHA256` signature via `CryptoIntegrityManager.verifySessionState`.
     */
    fun computeVerifiedSessionElapsed(session: FocusSessionEntity): Pair<Int, Boolean> {
        val nowWall = System.currentTimeMillis()
        val nowMono = SystemClock.elapsedRealtime()
        val currentBoot = querySystemBootCount()

        val wallElapsedSec = ((nowWall - session.startWallTimeMillis) / 1000L).coerceAtLeast(0L)
        val hmacValid = CryptoIntegrityManager.verifySessionState(session)

        val rebootOccurred = (session.bootCount > 0 && currentBoot != session.bootCount) ||
            (session.lastTickElapsedRealtimeMillis > 0L && nowMono < session.lastTickElapsedRealtimeMillis)

        return if (!rebootOccurred && session.startElapsedRealtimeMillis > 0L && nowMono >= session.startElapsedRealtimeMillis) {
            val monoElapsedSec = ((nowMono - session.startElapsedRealtimeMillis) / 1000L).coerceAtLeast(0L)
            val drift = abs(wallElapsedSec - monoElapsedSec)
            val tamper = drift > 90L || !hmacValid
            Pair((session.accumulatedBeforeRebootSeconds + monoElapsedSec.toInt()), tamper)
        } else {
            // Device rebooted during an active lock (LOCKED_BOOT_COMPLETED):
            // Resume from accumulated elapsed seconds prior to reboot + seconds elapsed since current boot
            val priorElapsed = max(session.accumulatedBeforeRebootSeconds, session.elapsedSeconds)
            val sinceRebootSec = (nowMono / 1000L).toInt().coerceAtLeast(0)
            Pair(priorElapsed + sinceRebootSec, !hmacValid)
        }
    }

    suspend fun getSystemGuardStatus(): SystemGuardStatus = withContext(Dispatchers.IO) {
        val active = dao.getActiveSessionSnapshot()
        var clockIntact = true
        var hmacIntact = true
        var driftSeconds = 0L

        if (active != null) {
            val nowWall = System.currentTimeMillis()
            val nowMono = SystemClock.elapsedRealtime()
            val wallElapsed = (nowWall - active.startWallTimeMillis) / 1000L
            val monoElapsed = (nowMono - active.startElapsedRealtimeMillis) / 1000L
            driftSeconds = abs(wallElapsed - monoElapsed)
            clockIntact = driftSeconds <= 90L
            hmacIntact = CryptoIntegrityManager.verifySessionState(active)
        }

        val appsCount = dao.getAllAppsSnapshot().size
        SystemGuardStatus(
            hasUsageStatsPermission = hasUsageStatsPermission(),
            hasOverlayPermission = hasOverlayPermission(),
            isAccessibilityServiceEnabled = isAccessibilityEnabled(),
            isNotificationListenerEnabled = isNotificationListenerEnabled(),
            isDeviceAdminEnabled = isDeviceAdminActive(),
            isMonotonicClockIntact = clockIntact,
            isHmacSignatureValid = hmacIntact,
            clockDriftSeconds = driftSeconds,
            systemBootCount = querySystemBootCount(),
            installedLauncherAppsCount = appsCount
        )
    }

    /**
     * 3-Tier Per-App Rule Cycling (1 tap: Locked -> 2m Peek -> Open -> Locked).
     */
    suspend fun addBlockedSite(raw: String): Boolean {
        val domain = BlockedBrowserDomains.normalizeDomain(raw) ?: return false
        dao.insertSite(BlockedSiteEntity(domain))
        return true
    }

    suspend fun removeBlockedSite(domain: String) {
        dao.deleteSite(domain)
    }

    suspend fun setBrowserBlockerEnabled(enabled: Boolean) {
        // Slice E (§7.5): targeted UPDATE — the whole-row REPLACE
        // this replaces could clobber the receipt claim columns
        // from a stale read. The seed-if-absent stays for the
        // never-written singleton.
        if (dao.getGuardSettingsSnapshot() == null) {
            dao.insertGuardSettings(GuardSettingsEntity())
        }
        dao.updateBrowserBlockerEnabled(enabled)
    }

    suspend fun isBrowserBlockerEnabled(): Boolean =
        dao.getGuardSettingsSnapshot()?.browserBlockerEnabled ?: true

    // Truth Car 3 (UNA-01): the sniper switch, persisted with the
    // same discipline as the browser pair — seed-if-absent, then a
    // targeted UPDATE, never a whole-row REPLACE.
    suspend fun setShortsReelsSniperEnabled(enabled: Boolean) {
        if (dao.getGuardSettingsSnapshot() == null) {
            dao.insertGuardSettings(GuardSettingsEntity())
        }
        dao.updateShortsReelsSniperEnabled(enabled)
    }

    suspend fun isShortsReelsSniperEnabled(): Boolean =
        dao.getGuardSettingsSnapshot()?.shortsReelsSniperEnabled ?: true

    suspend fun updateRoutine(routine: RoutineEntity) {
        dao.updateRoutine(routine)
    }

    /** Guard settings as a flow (reset-note strip + tick stamps). */
    val guardSettingsFlow: Flow<GuardSettingsEntity?> = dao.getGuardSettingsFlow()

    /** The three routines of his real day (Sten §2.5 targets). */
    fun routineTargets(): List<RoutineEntity> = listOf(
        RoutineEntity(
            name = "FASTED BLOCK", startMinutes = 300, endMinutes = 480,
            daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.STUDY.name, enabled = true
        ),
        RoutineEntity(
            name = "DAY BLOCKS", startMinutes = 540, endMinutes = 1170,
            daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.STUDY.name, enabled = true
        ),
        RoutineEntity(
            name = "SLEEP", startMinutes = 1200, endMinutes = 210,
            daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.SLEEP.name, enabled = true
        )
    )

    /**
     * Version-blind reseed (Sten §2), run once under the seed lock from
     * [ensureSeededAndSyncPackages] when routines already exist. Exact-
     * match shipped seeds migrate in place (id preserved, enabled
     * carried); user-edited rows are never touched; missing targets are
     * inserted unless a row already carries the target's exact name.
     * The done flag is written LAST, after the alarm cleanup, so an
     * interrupted run simply redoes the (idempotent) transform.
     */
    internal suspend fun reseedRoutinesV1() {
        val settings = dao.getGuardSettingsSnapshot() ?: return
        if (settings.routineReseedV1Done) return
        val now = System.currentTimeMillis()

        fun matches(r: RoutineEntity, name: String, start: Int, end: Int, mask: Int, loadout: String) =
            r.name == name && r.startMinutes == start && r.endMinutes == end &&
                r.daysMask == mask && r.loadoutName == loadout

        // S1: shipped "Morning Deep Work" seed → DAY BLOCKS.
        dao.getAllRoutinesSnapshot().firstOrNull {
            matches(it, "Morning Deep Work", 540, 780, RoutineSchedule.MON_FRI, SmartLoadout.STUDY.name)
        }?.let { s1 ->
            dao.updateRoutine(
                s1.copy(
                    name = "DAY BLOCKS", startMinutes = 540, endMinutes = 1170,
                    daysMask = RoutineSchedule.ALL_DAYS,
                    loadoutName = SmartLoadout.STUDY.name,
                    scheduleEditedAtMillis = now
                )
            )
        }
        // S2: shipped "Night Wind-Down" seed → SLEEP.
        dao.getAllRoutinesSnapshot().firstOrNull {
            matches(it, "Night Wind-Down", 1350, 420, RoutineSchedule.ALL_DAYS, SmartLoadout.SLEEP.name)
        }?.let { s2 ->
            dao.updateRoutine(
                s2.copy(
                    name = "SLEEP", startMinutes = 1200, endMinutes = 210,
                    daysMask = RoutineSchedule.ALL_DAYS,
                    loadoutName = SmartLoadout.SLEEP.name,
                    scheduleEditedAtMillis = now
                )
            )
        }
        // Missing targets: insert, unless an exact match already exists
        // or any row carries the target's exact name (his own hand).
        for (target in routineTargets()) {
            val rows = dao.getAllRoutinesSnapshot()
            val exact = rows.any {
                matches(it, target.name, target.startMinutes, target.endMinutes, target.daysMask, target.loadoutName)
            }
            if (exact || rows.any { it.name == target.name }) continue
            dao.insertRoutines(listOf(target.copy(scheduleEditedAtMillis = now)))
        }
        // Legacy per-routine alarms die here; the chain rearms against
        // the post-transform snapshot.
        val post = dao.getAllRoutinesSnapshot()
        RoutineScheduler.cancelLegacyRoutineAlarms(context, post.map { it.id })
        RoutineScheduler.cancelChain(context)
        RoutineScheduler.rearmChain(context, post, now)
        // Flag last — targeted (§7.5), never a whole-row write.
        dao.markRoutineReseedV1Done()
    }

    /** Any routine edit dismisses the reset note (Freya §3.3). */
    suspend fun dismissRoutineResetNote() {
        val settings = dao.getGuardSettingsSnapshot() ?: return
        if (!settings.routineReseedNoteDismissed) {
            dao.markRoutineReseedNoteDismissed()
        }
    }

    /**
     * Stamps an attendance-evidence bit on the active routine session
     * (E2 foreground return, E4 valve hold start). first/last evidence
     * stamps track the earliest/latest E2/E4 instants. No-ops for
     * manual sessions and when nothing is active.
     */
    suspend fun recordAttendanceEvidence(kindBit: Int, atMillis: Long = System.currentTimeMillis()) {
        val active = dao.getActiveSessionSnapshot() ?: return
        if (active.routineId == null) return
        val first = if (active.firstEvidenceMillis == 0L) atMillis
            else minOf(active.firstEvidenceMillis, atMillis)
        val last = maxOf(active.lastEvidenceMillis, atMillis)
        if ((active.evidenceMask and kindBit) != 0 &&
            first == active.firstEvidenceMillis && last == active.lastEvidenceMillis
        ) return
        dao.updateSession(
            active.copy(
                evidenceMask = active.evidenceMask or kindBit,
                firstEvidenceMillis = first,
                lastEvidenceMillis = last
            )
        )
    }

    /**
     * Evidence-derived attended seconds (Sten §1.6.1/J1): the union of
     * five-minute coverage arcs around every evidence instant, clipped
     * to the session's window. Instants are blocked attempts inside
     * the session's live span, task completions stamped to this
     * session, and the E2/E4 first/last stamps. Session start is NOT
     * an instant — a window nobody showed up to books zero.
     * Returns (seconds, whether any intercept contributed).
     */
    private suspend fun computeAttendedSeconds(session: FocusSessionEntity): Pair<Int, Boolean> {
        val windowStart = session.windowStartMillis
        val windowEnd = session.windowEndMillis
        if (windowEnd <= windowStart) return 0 to false
        val instants = sortedSetOf<Long>()
        val liveFrom = maxOf(windowStart, session.startWallTimeMillis)
        val intercepts = dao.getInterceptLogsBetween(liveFrom, windowEnd)
        intercepts.forEach { instants.add(it.timestampMillis) }
        dao.getTasksCompletedInSession(session.id).forEach { task ->
            if (task.completedAtMillis > 0) instants.add(task.completedAtMillis)
        }
        if (session.firstEvidenceMillis > 0) instants.add(session.firstEvidenceMillis)
        if (session.lastEvidenceMillis > 0) instants.add(session.lastEvidenceMillis)
        if (instants.isEmpty()) return 0 to intercepts.isNotEmpty()
        var covered = 0L
        var curStart = -1L
        var curEnd = -1L
        for (t in instants) {
            val s = maxOf(t, windowStart)
            val e = minOf(t + EVIDENCE_COVERAGE_MILLIS, windowEnd)
            if (e <= s) continue
            if (curStart < 0L) {
                curStart = s; curEnd = e
            } else if (s <= curEnd) {
                if (e > curEnd) curEnd = e
            } else {
                covered += curEnd - curStart; curStart = s; curEnd = e
            }
        }
        if (curStart >= 0L) covered += curEnd - curStart
        val windowSec = (windowEnd - windowStart) / 1000L
        return (covered / 1000L).coerceAtMost(windowSec).toInt() to intercepts.isNotEmpty()
    }

    /**
     * The §5.4 finalise funnel — the ONLY writer of a routine session's
     * booked seconds and outcome. Idempotent: a stamped row is never
     * re-judged. Books attendedSeconds (evidence only, capped at the
     * window) in EVERY arm — coordinator arbitration A2. SLEEP rows
     * are never stamped (J2); they are simply stood down. Returns the
     * outcome name, or null for SLEEP / non-routine / already handled
     * without a stamp.
     */
    suspend fun finaliseRoutineSession(session: FocusSessionEntity, nowMillis: Long): String? {
        if (session.routineId == null) return null
        if (session.routineOutcome != null) return session.routineOutcome
        val routine = dao.getRoutineById(session.routineId)
        if (routine?.loadoutName == SmartLoadout.SLEEP.name) {
            if (session.isActive) {
                dao.updateSession(
                    session.copy(
                        isActive = false,
                        // RC-01: a stood-down SLEEP row ended at
                        // its window's end — the frozen stamp is
                        // the exact end (first stamp wins).
                        endWallMillis = if (session.endWallMillis == 0L) {
                            session.windowEndMillis
                        } else {
                            session.endWallMillis
                        }
                    )
                )
            }
            return null
        }
        val (attended, usedIntercepts) = computeAttendedSeconds(session)
        val windowSec = ((session.windowEndMillis - session.windowStartMillis) / 1000L).coerceAtLeast(0L)
        val lateFire = session.startWallTimeMillis - session.windowStartMillis > 120_000L
        val remainderSec = ((session.windowEndMillis -
            maxOf(session.windowStartMillis, session.startWallTimeMillis)) / 1000L).coerceAtLeast(0L)
        val yardstickSec = if (lateFire) remainderSec else windowSec
        val outcome = when {
            attended < ATTENDANCE_FLOOR_SECONDS ->
                if (lateFire) WindowJudgement.LATE_FIRE else WindowJudgement.MISSED
            attended >= yardstickSec - ATTENDANCE_FLOOR_SECONDS -> WindowJudgement.ATTENDED
            else -> WindowJudgement.PARTIAL
        }
        dao.updateSession(
            session.copy(
                elapsedSeconds = attended,
                completedSuccessfully = outcome == WindowJudgement.ATTENDED,
                forgedArtifactId = if (outcome == WindowJudgement.ATTENDED) session.forgedArtifactId else null,
                routineOutcome = outcome.name,
                evidenceMask = session.evidenceMask or
                    (if (usedIntercepts) EVIDENCE_E3_INTERCEPT else 0),
                isActive = false,
                // RC-01: the funnel is the routine ending write —
                // its instant is the end it books (first stamp
                // wins; a re-judged row is never re-stamped, and
                // the funnel is idempotent by its own law above).
                endWallMillis = if (session.endWallMillis == 0L) {
                    nowMillis
                } else {
                    session.endWallMillis
                }
            )
        )
        return outcome.name
    }

    /**
     * Stands a routine session down without judging it (SLEEP window
     * end). A plain isActive write — deliberately NOT the disarm path,
     * so no booking, stamp, or intercept log is produced.
     */
    suspend fun deactivateRoutineSession(session: FocusSessionEntity) {
        if (session.isActive) {
            dao.updateSession(
                session.copy(
                    isActive = false,
                    // RC-01: stand-down happens at the window's
                    // end; the frozen stamp is the exact end.
                    endWallMillis = if (session.endWallMillis == 0L) {
                        session.windowEndMillis
                    } else {
                        session.endWallMillis
                    }
                )
            )
        }
    }

    /**
     * Materialises a [DesiredSession] from the reconciler: applies the
     * loadout, starts the session with its window snapshot (E1 lands in
     * [startSession]), and raises the shield.
     */
    suspend fun materialiseRoutineSession(desired: DesiredSession): FocusSessionEntity {
        val loadout = runCatching { SmartLoadout.valueOf(desired.loadoutName) }
            .getOrDefault(SmartLoadout.STUDY)
        applySmartLoadout(loadout)
        val session = startSession(
            intention = desired.intention,
            profileName = desired.profileName,
            durationMinutes = desired.durationMinutes,
            studyBlocksCount = 1,
            routineId = desired.routineId,
            windowStartMillis = desired.windowStartMillis,
            windowEndMillis = desired.windowEndMillis
        )
        MonolithShieldService.startShield(context)
        return session
    }

    suspend fun daoSnapshotRoutines(): List<RoutineEntity> = dao.getAllRoutinesSnapshot()

    suspend fun cycleAppBlockTier(app: BlockedAppEntity): AppBlockTier {
        val nextTier = when (app.resolvedTier) {
            AppBlockTier.LOCKED -> AppBlockTier.PEEK_2M
            AppBlockTier.PEEK_2M -> AppBlockTier.OPEN
            AppBlockTier.OPEN -> AppBlockTier.LOCKED
        }
        val isBlockedFlag = nextTier != AppBlockTier.OPEN
        dao.setAppTier(app.packageName, isBlockedFlag, nextTier.name)
        return nextTier
    }

    suspend fun setAppBlockTier(packageName: String, tier: AppBlockTier) {
        val isBlockedFlag = tier != AppBlockTier.OPEN
        dao.setAppTier(packageName, isBlockedFlag, tier.name)
    }

    /**
     * 1-Tap Smart Loadouts:
     * - Study Loadout: Locks Social & Games, sets YouTube & Messaging to 2m Peek, leaves Utility/Docs Open.
     * - Sleep Loadout: Locks everything except Phone, Clock, and Maps.
     */
    suspend fun applySmartLoadout(loadout: SmartLoadout) = withContext(Dispatchers.IO) {
        val allApps = dao.getAllAppsSnapshot()
        val updated = allApps.map { app ->
            val lower = "${app.packageName} ${app.appName}".lowercase()
            val tier = when (loadout) {
                SmartLoadout.STUDY -> {
                    when {
                        lower.contains("youtube") || lower.contains("chrome") || app.category == AppCategory.MESSAGING.name ->
                            AppBlockTier.PEEK_2M
                        app.category == AppCategory.SHORT_VIDEO.name ||
                            app.category == AppCategory.SOCIAL.name ||
                            app.category == AppCategory.GAMES.name ->
                            AppBlockTier.LOCKED
                        else -> AppBlockTier.OPEN
                    }
                }
                SmartLoadout.WORK_SPRINT -> {
                    when {
                        app.category == AppCategory.SHORT_VIDEO.name ||
                            app.category == AppCategory.GAMES.name ->
                            AppBlockTier.LOCKED
                        app.category == AppCategory.SOCIAL.name ->
                            AppBlockTier.PEEK_2M
                        else -> AppBlockTier.OPEN
                    }
                }
                SmartLoadout.SLEEP -> {
                    if (lower.contains("dialer") || lower.contains("phone") ||
                        lower.contains("clock") || lower.contains("alarm") ||
                        lower.contains("maps")
                    ) {
                        AppBlockTier.OPEN
                    } else {
                        AppBlockTier.LOCKED
                    }
                }
            }
            app.copy(
                isBlocked = tier != AppBlockTier.OPEN,
                blockTier = tier.name
            )
        }
        dao.insertApps(updated)
    }

    /**
     * Enforces the Mandatory 20-Minute Cooldown per package using `SystemClock.elapsedRealtime()`:
     * If `lastPeekEndElapsedRealtime > 0`, a new 2m Peek cannot be started until
     * `SystemClock.elapsedRealtime() >= lastPeekEndElapsedRealtime + PEEK_COOLDOWN_MILLIS`.
     */
    fun getPeekCooldownRemainingSeconds(app: BlockedAppEntity): Int {
        val nowMono = SystemClock.elapsedRealtime()
        val anchorEndMono = max(app.lastPeekEndElapsedRealtime, app.activePeekUntilElapsedRealtime)
        if (anchorEndMono <= 0L) return 0
        val remainingMs = (anchorEndMono + PEEK_COOLDOWN_MILLIS - nowMono).coerceAtMost(PEEK_COOLDOWN_MILLIS)
        return if (remainingMs > 0L) (remainingMs / 1000L).toInt() else 0
    }

    suspend fun getPeekCooldownRemainingSeconds(packageName: String): Int {
        val app = dao.getAllAppsSnapshot().firstOrNull { it.packageName == packageName } ?: return 0
        return getPeekCooldownRemainingSeconds(app)
    }

    /**
     * Grants a 2-minute peek lease (`activePeekUntilElapsedRealtime = nowMono + 120_000L`)
     * and pre-schedules `lastPeekEndElapsedRealtime = nowMono + 120_000L` so the 20-minute
     * cooldown is immediately armed in Room storage.
     */
    suspend fun startPeekLease(packageName: String) {
        val nowMono = SystemClock.elapsedRealtime()
        val leaseEndMono = nowMono + PEEK_DURATION_MILLIS
        dao.updatePeekLeaseAndCooldown(
            packageName = packageName,
            activeUntilMono = leaseEndMono,
            peekEndMono = leaseEndMono
        )
    }

    /**
     * Ends a 2-minute peek lease immediately (either when 120s expires or when the user taps ✕)
     * and anchors the mandatory 20-minute cooldown to `SystemClock.elapsedRealtime()`.
     */
    suspend fun recordPeekEnded(packageName: String) {
        val nowMono = SystemClock.elapsedRealtime()
        dao.updatePeekLeaseAndCooldown(
            packageName = packageName,
            activeUntilMono = 0L,
            peekEndMono = nowMono
        )
    }

    suspend fun toggleAppBlocked(app: BlockedAppEntity) {
        cycleAppBlockTier(app)
    }

    suspend fun updateAppDailyLimit(packageName: String, limitMinutes: Int) {
        dao.updateDailyLimit(packageName, limitMinutes)
    }

    suspend fun addCustomApp(appName: String, packageName: String, category: AppCategory) {
        val cleanPkg = packageName.trim().ifEmpty {
            "com.custom.${appName.lowercase().replace(" ", "")}"
        }
        dao.insertApp(
            BlockedAppEntity(
                packageName = cleanPkg,
                appName = appName.trim(),
                category = category.name,
                isBlocked = true,
                blockTier = AppBlockTier.LOCKED.name,
                dailyLimitMinutes = 25,
                todayUsageMinutes = 10,
                interceptCount = 0
            )
        )
    }

    suspend fun applyProfile(profile: FocusProfileEntity) {
        val activeCategories = profile.blockedCategoriesCsv
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

        val currentApps = dao.getAllAppsSnapshot()
        val updated = currentApps.map { app ->
            val shouldBlock = app.category in activeCategories
            val tier = when {
                !shouldBlock -> AppBlockTier.OPEN
                app.category == AppCategory.MESSAGING.name -> AppBlockTier.PEEK_2M
                else -> AppBlockTier.LOCKED
            }
            app.copy(isBlocked = shouldBlock, blockTier = tier.name)
        }
        dao.insertApps(updated)
    }

    suspend fun toggleProfileSchedule(profile: FocusProfileEntity) {
        dao.updateProfile(profile.copy(isScheduleEnabled = !profile.isScheduleEnabled))
    }

    suspend fun updateProfileTimeWindow(profile: FocusProfileEntity, startHour: Int, endHour: Int) {
        val safeStart = startHour.coerceIn(0, 23)
        val safeEnd = endHour.coerceIn(1, 24)
        val scheduleStr = String.format(Locale.US, "%02d:00 – %02d:00 • Active Window", safeStart, safeEnd)
        dao.updateProfile(
            profile.copy(
                startHour = safeStart,
                startMinute = 0,
                endHour = safeEnd,
                endMinute = 0,
                scheduleLabel = scheduleStr
            )
        )
    }

    suspend fun saveCustomProfile(
        name: String,
        minutes: Int,
        categories: List<AppCategory>,
        startHour: Int,
        endHour: Int
    ) {
        val csv = categories.joinToString(",") { it.name }
        val code = name.take(10).trim().ifEmpty { "Custom" }
        val scheduleStr = String.format("%02d:00 – %02d:00 • Custom", startHour, endHour)
        dao.insertProfile(
            FocusProfileEntity(
                name = name.trim(),
                codeName = code,
                description = "Custom ${minutes}m block shielding ${categories.size} categories.",
                defaultMinutes = minutes,
                blockedCategoriesCsv = csv,
                scheduleLabel = scheduleStr,
                isScheduleEnabled = true,
                startHour = startHour,
                startMinute = 0,
                endHour = endHour,
                endMinute = 0
            )
        )
    }

    /**
     * Starts a focus session:
     * - Dialing below 10 minutes activates Count-Up Stopwatch Mode with a mandatory 20-minute minimum lock floor.
     * - Signs state with AndroidKeyStore HmacSHA256 (`CryptoIntegrityManager`).
     */
    /** The held-apps count (Fader): the apps the shield actually
     *  holds for a session born now — every app whose resolved
     *  tier is not OPEN. One derivation, shared by the birth
     *  capture and every surface that ever prints the figure. */
    fun countHeldApps(apps: List<BlockedAppEntity>): Int =
        apps.count { it.resolvedTier != AppBlockTier.OPEN }

    suspend fun startSession(
        intention: String,
        profileName: String,
        durationMinutes: Int,
        studyBlocksCount: Int = 1,
        routineId: Int? = null,
        windowStartMillis: Long = 0L,
        windowEndMillis: Long = 0L
    ): FocusSessionEntity {
        val isStopwatch = durationMinutes < 10
        val targetSecs = if (isStopwatch) {
            STOPWATCH_MIN_FLOOR_SECONDS
        } else {
            (durationMinutes * 60).coerceAtLeast(60)
        }
        val wallNow = System.currentTimeMillis()
        val monoNow = SystemClock.elapsedRealtime()
        val bootCount = querySystemBootCount()
        val hmac = CryptoIntegrityManager.signSessionState(
            startWallTimeMillis = wallNow,
            startElapsedRealtimeMillis = monoNow,
            bootCount = bootCount,
            targetDurationSeconds = targetSecs,
            isStopwatch = isStopwatch
        )

        val session = FocusSessionEntity(
            intention = intention.ifBlank { "Deep Uninterrupted Study" },
            profileName = profileName,
            targetDurationSeconds = targetSecs,
            elapsedSeconds = 0,
            startWallTimeMillis = wallNow,
            startElapsedRealtimeMillis = monoNow,
            bootCount = bootCount,
            accumulatedBeforeRebootSeconds = 0,
            lastTickElapsedRealtimeMillis = monoNow,
            hmacSignature = hmac,
            isStopwatchMode = isStopwatch,
            totalStudyBlocks = studyBlocksCount.coerceIn(1, 8),
            currentBlockIndex = 1,
            isInSafeBreak = false,
            safeBreakSecondsRemaining = 0,
            penaltySecondsAdded = 0,
            isActive = true,
            completedSuccessfully = false,
            phantomReachesBlocked = 0,
            tamperDetected = false,
            forgedArtifactId = null,
            routineId = routineId,
            windowStartMillis = windowStartMillis,
            windowEndMillis = windowEndMillis,
            // E1: the window is open and his kit is on (Sten §1.6.1).
            // first/last evidence stamps stay 0 until E2/E4 land.
            evidenceMask = if (routineId != null) EVIDENCE_E1_START else 0
        )
        // Slice E (Fader's writer law): the held counts are captured
        // INSIDE the birth transaction, from the same snapshot that
        // deactivates the predecessor — the set the shield will
        // actually hold for this row, provable later from the row
        // itself. Exactly one writer exists for these columns
        // (this insert); no DAO update for them will ever exist.
        val database = MonolithDatabase.getInstance(context)
        return database.withTransaction {
            val heldApps = countHeldApps(dao.getAllAppsSnapshot())
            val heldSites = dao.getAllSitesSnapshot().size
            dao.deactivateAllSessions()
            val id = dao.insertSession(
                session.copy(heldAppsAtSeal = heldApps, heldSitesAtSeal = heldSites)
            ).toInt()
            session.copy(id = id, heldAppsAtSeal = heldApps, heldSitesAtSeal = heldSites)
        }
    }

    /** The active session row as a one-shot read (the cascade's
     *  L1 snapshot witness reads through this). */
    suspend fun activeSessionSnapshot(): FocusSessionEntity? = dao.getActiveSessionSnapshot()

    /** Any session row by id (Slice E: the finish machine's and
     *  the seal transition's reads). */
    suspend fun sessionById(id: Int): FocusSessionEntity? = dao.getSessionById(id)

    /**
     * THE SEAL (Slice D step 4, Opal §F2): at t=0 the session row
     * is re-anchored to the seal instant and re-signed. startSession
     * anchored the row at insert; without this the session would
     * count up to the whole proving window (≤500ms) as proven
     * session time it never was. The re-sign is REQUIRED, not
     * polish: computeVerifiedSessionElapsed verifies the HMAC over
     * exactly these anchors, so an un-signed re-anchor would read
     * as tamper and draw the penalty. Both anchors move to the
     * same frame (mono = t0, wall = the wall clock at that frame)
     * so the mono/wall cross-check stays consistent.
     */
    suspend fun sealSession(sessionId: Int, t0Mono: Long): Boolean {
        // Klinke §4.3.2 (Slice E): the Boolean IS the guard — the
        // transition may sound the Throw only on a true return, so
        // a row that died under the attempt (annulment won the
        // race, a superseding birth deactivated it) refuses here
        // and no seal ever sounds over an un-re-anchored row.
        // Read+write run in one transaction (his R-K3 mitigation):
        // a shield tick landing between them can no longer be
        // clobbered by the stale copy.
        val database = MonolithDatabase.getInstance(context)
        return database.withTransaction {
            val row = dao.getActiveSessionSnapshot() ?: return@withTransaction false
            if (row.id != sessionId || !row.isActive) return@withTransaction false
            val wallNow = System.currentTimeMillis()
            val hmac = CryptoIntegrityManager.signSessionState(
                startWallTimeMillis = wallNow,
                startElapsedRealtimeMillis = t0Mono,
                bootCount = row.bootCount,
                targetDurationSeconds = row.targetDurationSeconds,
                isStopwatch = row.isStopwatchMode
            )
            dao.updateSession(
                row.copy(
                    startWallTimeMillis = wallNow,
                    startElapsedRealtimeMillis = t0Mono,
                    lastTickElapsedRealtimeMillis = t0Mono,
                    hmacSignature = hmac
                )
            )
            true
        }
    }

    /**
     * ANNULMENT (Slice D step 4, Opal §F4): a proving attempt that
     * never reached t=0 un-exists - the row is DELETED, not
     * deactivated. disarmActiveSession is forbidden here: it writes
     * an outcome and a history row for a session that, in law,
     * never was. Pre-t0 only; the controller is the only caller
     * and it annuls solely from inside a live proving attempt.
     */
    suspend fun annulProvingSession(sessionId: Int) {
        dao.deleteSessionById(sessionId)
    }

    // =====================================================================
    // SLICE E — THE RECEIPT (Nocke's claim, Zettel's contract,
    // Reihe's assembly). The claim transaction is the publish
    // latch: ONE withTransaction on the guard_settings singleton
    // decides, atomically, whether this session's receipt mints a
    // № or returns the one already standing. No second path mints.
    // =====================================================================

    /** The guard row as it stands (created with defaults if the
     *  singleton was never written). */
    private suspend fun guardSettingsRow(): GuardSettingsEntity =
        dao.getGuardSettingsSnapshot() ?: GuardSettingsEntity()

    /**
     * Nocke §3's claim, Zettel Z1's latch: if the finish composite
     * has already fired for [sessionId], or its receipt already
     * stands, returns the standing № with NO increment; otherwise
     * mints: receiptSequence += 1, all three pointers move
     * together, in this one transaction. Returns the № the slip
     * prints. Valve / grace / early endings never call this —
     * they publish nothing and leave no gap in the sequence.
     */
    suspend fun claimReceiptNumber(sessionId: Int, outcomeWord: String): Int {
        val database = MonolithDatabase.getInstance(context)
        return database.withTransaction {
            // Seed-if-absent INSIDE the transaction: the claim's
            // targeted UPDATE matches zero rows on a settings row
            // that was never written, and the № would exist only
            // in memory (the repository test caught exactly this
            // on a fresh sandbox). The claim mints the sequence —
            // it must own its row's existence.
            if (dao.getGuardSettingsSnapshot() == null) {
                dao.insertGuardSettings(GuardSettingsEntity())
            }
            val settings = guardSettingsRow()
            // Truth Car 2 (RC-04): the ROW is the latch. A session
            // that already claimed carries its № on its own row —
            // return it, mint nothing, whatever the settings slot
            // holds by now. (The old guard read only the slot: a
            // session re-published after a later session claimed
            // would have minted a second №, forbidden only by the
            // arithmetic coincidence of two span constants in two
            // files. The row ends the coincidence.)
            val row = dao.getSessionById(sessionId)
            if (row != null && row.receiptNumber > 0) {
                return@withTransaction row.receiptNumber
            }
            if (settings.lastReceiptSessionId == sessionId && settings.receiptSequence > 0) {
                return@withTransaction settings.receiptSequence
            }
            val next = settings.receiptSequence + 1
            // Targeted write (§7.5): the claim touches its four
            // columns and no other — a whole-row REPLACE here
            // would clobber settings written since the read.
            dao.writeReceiptClaim(
                sessionId = sessionId,
                outcome = outcomeWord,
                sequence = next,
                receiptSessionId = sessionId
            )
            // The № is stamped on the session row in the SAME
            // transaction (RC-04): claim and stamp commit or roll
            // back together, so a № can never exist without its
            // row wearing it.
            dao.stampReceiptNumber(sessionId, next)
            next
        }
    }

    /** The standing receipt's identity, for rehydration (Z6 case
     *  b): (№, sessionId, outcomeWord) when a receipt stands. */
    suspend fun standingReceipt(): Triple<Int, Int, String>? {
        val settings = dao.getGuardSettingsSnapshot() ?: return null
        if (settings.receiptSequence <= 0 || settings.lastReceiptSessionId <= 0) return null
        return Triple(settings.receiptSequence, settings.lastReceiptSessionId, settings.lastReceiptOutcome)
    }

    /** HH:mm in the device's zone, for the WITHIN line. */
    private fun hhmm(wallMillis: Long): String =
        java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).format(java.util.Date(wallMillis))

    private fun hhmm(minutesOfDay: Int): String =
        String.format(java.util.Locale.US, "%02d:%02d", minutesOfDay / 60, minutesOfDay % 60)

    /**
     * Reihe §3's assembly: the pending receipt's facts, from pure
     * reads of stored rows only — the ended row's own fields, the
     * frozen routine stamps, the spend ledger, the intercept
     * window. [kind] is the finish machine's classification.
     * Returns null when the row is gone or not in a publishable
     * state (the claim gate re-checks under the transaction's
     * own read in [publishReceipt]).
     */
    suspend fun assemblePendingReceipt(
        sessionId: Int,
        kind: String,
        endedAtWallMs: Long
    ): PendingReceipt? {
        val row = dao.getSessionById(sessionId) ?: return null
        if (row.isActive) return null

        // Truth Car 2 (RC-01): the slip's ENDED reads the row's
        // own stamped end whenever the ending write recorded one —
        // on EVERY path (witnessed, discovered, rehydrated). The
        // caller's [endedAtWallMs] is only the fallback for rows
        // the stamp never reached (pre-v11 rows): on the branch-U
        // discovery path it is the discovery instant, and printing
        // it as the end was the lie this closes.
        val trueEndedAtWallMs = if (row.endWallMillis > 0L) row.endWallMillis else endedAtWallMs

        // The routine line (Z5 + F-Z5). Routine rows: frozen at
        // insert — the name stripped from profileName's "Routine:
        // <name>", the window from the row's frozen stamps, never
        // re-evaluated. Hand-armed rows: §5.7 honoured at assembly
        // (F-Z5, +8) — a routine whose window contains the arm
        // instant lends its line; the BY… form never prints.
        val routineLabel: String? = if (row.routineId != null) {
            val name = row.profileName.removePrefix(ROUTINE_PROFILE_PREFIX).trim()
            if (name.isNotEmpty() && row.windowEndMillis > row.windowStartMillis) {
                "WITHIN ${name.uppercase(java.util.Locale.US)} · " +
                    "${hhmm(row.windowStartMillis)}–${hhmm(row.windowEndMillis)}"
            } else {
                null
            }
        } else {
            routineWindowLabelAt(row.startWallTimeMillis)
        }

        // Banked break seconds: the spend ledger's held seconds
        // for this session. On every publishable receipt today the
        // honest figure is 0 — a session the valve broke publishes
        // nothing — and the field exists so the day a banked break
        // can lawfully close a session, the slip already carries
        // its price. Named truth, not a placeholder.
        val spends = dao.getSpendsForSession(sessionId)
        val banked = spends.sumOf { it.heldSeconds }

        // Deflections (Zettel): intercept instants inside the
        // session's wall window, intention-matched, system rows
        // excluded, break-window openings excluded, 30s-per-
        // package dedupe keeping the first. A list, never a sum.
        val windowEnd = if (row.routineId != null && row.windowEndMillis > 0L) {
            maxOf(trueEndedAtWallMs, row.windowEndMillis)
        } else {
            trueEndedAtWallMs
        }
        val breakWindows = spends
            .map { it.spentAtWallMillis..(it.spentAtWallMillis + 60_000L) }
        val lastByPackage = mutableMapOf<String, Long>()
        val deflections = dao.getInterceptLogsBetween(row.startWallTimeMillis, windowEnd)
            .filter { log ->
                log.sessionIntention == row.intention &&
                    log.packageName != "com.monolith.system" &&
                    breakWindows.none { log.timestampMillis in it }
            }
            .filter { log ->
                val prev = lastByPackage[log.packageName]
                if (prev == null || log.timestampMillis - prev >= 30_000L) {
                    lastByPackage[log.packageName] = log.timestampMillis
                    true
                } else {
                    false
                }
            }
            .map { ReceiptDeflection(it.appName, it.packageName, it.timestampMillis) }

        return PendingReceipt(
            sessionId = sessionId,
            kind = kind,
            outcomeWord = receiptOutcomeWord(row, kind),
            title = row.intention,
            armWallMs = row.startWallTimeMillis,
            endedAtWallMs = trueEndedAtWallMs,
            // Truth Car 2 (RC-02): a FLOW session has no plan —
            // its stored target is the stopwatch's 20-minute
            // FLOOR, and running it through the block formula
            // printed PLANNED 20 MIN … 3H 50M for a span that was
            // never planned. FLOW slips carry no planned figure
            // at all (the HELD line's omission law, applied to
            // PLANNED): null, and the sheet omits the segment.
            plannedSeconds = if (kind == FinishKind.FLOW_FINISH) null else row.spanTargetSeconds,
            sealedElapsedSeconds = row.spanElapsedSeconds,
            heldAppsAtSeal = row.heldAppsAtSeal,
            heldSitesAtSeal = row.heldSitesAtSeal,
            routineLabelWithin = routineLabel,
            bankedBreakSeconds = banked,
            shieldWasOff = row.outcome == SessionOutcome.ELAPSED_SHIELD_GAP,
            deflections = deflections
        )
    }

    /** F-Z5's assembly evaluation for a hand-armed row: the line
     *  of an enabled routine whose window contains the arm
     *  instant (today's or yesterday's instance, for windows that
     *  cross midnight). Null when no window contains it. */
    private suspend fun routineWindowLabelAt(armWallMs: Long): String? {
        val routines = dao.getAllRoutinesSnapshot().filter { it.enabled }
        if (routines.isEmpty()) return null
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = armWallMs }
        val minutesOfDay = cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 +
            cal.get(java.util.Calendar.MINUTE)
        val dayBit = (cal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
        val prevDayBit = (dayBit + 6) % 7
        for (routine in routines) {
            val wraps = routine.endMinutes <= routine.startMinutes
            val contains = when {
                wraps ->
                    (routine.daysMask and (1 shl dayBit) != 0 && minutesOfDay >= routine.startMinutes) ||
                        (routine.daysMask and (1 shl prevDayBit) != 0 && minutesOfDay < routine.endMinutes)
                else ->
                    routine.daysMask and (1 shl dayBit) != 0 &&
                        minutesOfDay >= routine.startMinutes && minutesOfDay < routine.endMinutes
            }
            if (contains) {
                return "WITHIN ${routine.name.uppercase(java.util.Locale.US)} · " +
                    "${hhmm(routine.startMinutes)}–${hhmm(routine.endMinutes)}"
            }
        }
        return null
    }

    /** The slip's outcome word: the long-form dialect (F-W1) —
     *  the row's classification in words a man reads, never the
     *  stored token. */
    fun receiptOutcomeWord(row: FocusSessionEntity, kind: String): String = when (kind) {
        FinishKind.ROUTINE_ATTENDED -> "ATTENDED"
        FinishKind.ROUTINE_PARTIAL -> "PARTIAL"
        FinishKind.ROUTINE_SLEEP_STANDDOWN -> "SLEPT THROUGH"
        FinishKind.FLOW_FINISH -> "FLOW ENDED"
        else -> if (row.outcome == SessionOutcome.ELAPSED_SHIELD_GAP) "ENDED · SHIELD GAP" else "ENDED"
    }

    /**
     * The publish: gate + claim + state, one call. The gate is
     * the terminal predicate re-evaluated on a FRESH row read —
     * a finish the predicate no longer admits (a stale emission,
     * a re-opened row) publishes nothing and returns null. The
     * claim mints or returns the standing №; the returned state
     * is whole. [faulted] is the detention's verdict (Draht): a
     * faulted publish also stamps the row's release-fault record
     * in the same act.
     */
    suspend fun publishReceipt(pending: PendingReceipt, faulted: Boolean): ReceiptState? {
        val row = dao.getSessionById(pending.sessionId) ?: return null
        val isSleep = row.routineId?.let { dao.getRoutineById(it)?.loadoutName } ==
            SmartLoadout.SLEEP.name
        val kindNow = finishKindOf(row, isSleep, System.currentTimeMillis()) ?: return null
        if (kindNow != pending.kind) return null
        // The once-gate (Zettel Z1): a session whose receipt
        // already stands publishes nothing twice — the claim's
        // latch alone would hand back the standing № and let a
        // second slip be built over it.
        if (standingReceipt()?.second == pending.sessionId) return null
        val number = claimReceiptNumber(pending.sessionId, pending.outcomeWord)
        if (faulted) {
            dao.stampReleaseFault(pending.sessionId, System.currentTimeMillis())
        }
        return ReceiptState(
            sessionId = pending.sessionId,
            number = number,
            kind = pending.kind,
            outcomeWord = pending.outcomeWord,
            title = pending.title,
            armWallMs = pending.armWallMs,
            endedAtWallMs = pending.endedAtWallMs,
            plannedSeconds = pending.plannedSeconds,
            sealedElapsedSeconds = pending.sealedElapsedSeconds,
            heldAppsAtSeal = pending.heldAppsAtSeal,
            heldSitesAtSeal = pending.heldSitesAtSeal,
            routineLabelWithin = pending.routineLabelWithin,
            bankedBreakSeconds = pending.bankedBreakSeconds,
            shieldWasOff = pending.shieldWasOff,
            faulted = faulted,
            deflections = pending.deflections,
            restored = false,
            receiptPublishedMonoMs = 0L
        )
    }

    /**
     * Rehydration (Z6 case b): rebuild the standing slip from the
     * stored № + the row, marked restored. Ended-at for routine
     * rows is the row's windowEndMillis exactly (Reihe §3 rule
     * 2 — never the observation time); for manual rows it is the
     * same-boot derivation startWall + (lastTickMono −
     * startMono), named here as the rehydration approximation.
     *
     * Truth Car 2 (RC-01): the derivation below is now only the
     * FALLBACK for rows whose ending write predates the stamp.
     * Where the row carries endWallMillis, assemblePendingReceipt
     * reads the stamp over any derived value — the true E0 wall
     * IS persisted now, and a restored slip prints the witnessed
     * end, not the approximation.
     */
    suspend fun rehydrateReceipt(): ReceiptState? {
        val (number, sessionId, _) = standingReceipt() ?: return null
        val row = dao.getSessionById(sessionId) ?: return null
        if (row.isActive) return null
        val isSleep = row.routineId?.let { dao.getRoutineById(it)?.loadoutName } ==
            SmartLoadout.SLEEP.name
        val kind = finishKindOf(row, isSleep, System.currentTimeMillis()) ?: return null
        val endedAt = if (row.routineId != null && row.windowEndMillis > 0L) {
            row.windowEndMillis
        } else {
            row.startWallTimeMillis +
                (row.lastTickElapsedRealtimeMillis - row.startElapsedRealtimeMillis)
                    .coerceAtLeast(0L)
        }
        val pending = assemblePendingReceipt(sessionId, kind, endedAt) ?: return null
        return ReceiptState(
            sessionId = sessionId,
            number = number,
            kind = pending.kind,
            outcomeWord = pending.outcomeWord,
            title = pending.title,
            armWallMs = pending.armWallMs,
            endedAtWallMs = pending.endedAtWallMs,
            plannedSeconds = pending.plannedSeconds,
            sealedElapsedSeconds = pending.sealedElapsedSeconds,
            heldAppsAtSeal = pending.heldAppsAtSeal,
            heldSitesAtSeal = pending.heldSitesAtSeal,
            routineLabelWithin = pending.routineLabelWithin,
            bankedBreakSeconds = pending.bankedBreakSeconds,
            shieldWasOff = pending.shieldWasOff,
            faulted = row.releaseFaultShield,
            deflections = pending.deflections,
            restored = true,
            receiptPublishedMonoMs = 0L
        )
    }

    /** The gap latch's repository door (Nocke §2.2): set the
     *  latch when the active row's beat is stale past the law.
     *  Called from the VM ticker and the relaunch path; the
     *  latch, once set, is never cleared. */
    suspend fun latchShieldGapIfStale(nowWallMillis: Long): Boolean {
        val active = dao.getActiveSessionSnapshot() ?: return false
        if (active.shieldGapSeen || active.outcome.isNotEmpty()) return false
        val beatRef = maxOf(active.shieldLastBeatWallMillis, active.startWallTimeMillis)
        if (nowWallMillis - beatRef <= SHIELD_GAP_THRESHOLD_MILLIS) return false
        dao.latchShieldGap(active.id)
        return true
    }

    /** ST-03 (Truth Car 3): the break boundaries' voice, fired AT
     *  the flips in this machinery — the block→break entry below
     *  and [advancePastBreak], the one exit both the timer and the
     *  hand cross. The boundary belongs to the machinery that
     *  crosses it (Una's verification brief): a voice hung on a UI
     *  observer would be silent-by-construction whenever the app
     *  is not alive mid-break, and this machine crosses breaks
     *  from the shield's tick as readily as from the screen's.
     *
     *  The voice is the engine's existing latch pair — release
     *  (false) as the block lets go into the break, engage (true)
     *  as the next block takes hold. No new sound machinery. The
     *  player is a seam for exactly one reason: the transition
     *  tests count knock REQUESTS at the flip (Pruefer's proof
     *  standard — call level, never an acoustic claim). In
     *  production the seam's default is the engine itself, whose
     *  own soundEnabled gate decides — the same gate the seal
     *  reads, never routed around. The engine is lazy and
     *  repository-held: it is born only in a process that actually
     *  crosses a break, born FROM the persisted sound fact
     *  (UNA-03's initializer reads it at construction) and
     *  registered for the toggle's live pushes, so an engine this
     *  door creates honours the setting exactly as the screen's
     *  engine does. */
    internal var breakBoundaryKnock: (engaged: Boolean) -> Unit = { engaged ->
        breakSoundEngine.playMechanicalLatch(engaged)
    }

    /** Audit R3b #148: the T-60 block warning — one latch knock,
     *  fired by the countdown branch at the 60-seconds-left
     *  crossing. Its own seam (tests and the summons work on the
     *  #146 branch can re-voice it without touching the boundary
     *  knock's contract). */
    internal var blockWarningKnock: () -> Unit = {
        breakSoundEngine.playMechanicalLatch(true)
    }

    private val breakSoundEngine: TactileSoundEngine by lazy {
        TactileSoundEngine(context)
    }

    /** The Safe Break length in force for [session]. Today the one
     *  named constant; when Truth Car 3's stamp lands (Rurik —
     *  breakSeconds written on the row at creation, PILL-AND-TIMES
     *  §3.2), this reader re-points at the row's own stamp and the
     *  constant survives only as the default's ancestor. Every
     *  break-length read in the progress machine and the break
     *  verbs goes through here, so the stamp drops in at exactly
     *  one place. */
    private fun breakLengthSeconds(session: FocusSessionEntity): Int =
        SAFE_BREAK_DURATION_SECONDS

    /** The one write that carries a session out of a Safe Break
     *  into its next block: fresh anchors, a fresh signature over
     *  them, the index advanced. The expiry flip in
     *  [updateSessionProgress] and the end-early verb below are the
     *  same transition and share this write, so the two exits can
     *  never drift apart; they differ only in what the taken
     *  ledger books ([breakTakenSeconds] — the full length at
     *  expiry, the wall-derived part at an early end). The shield
     *  beat is carried, never stamped: a beat written by anyone
     *  but the shield would be a forged witness (Slice E). */
    private fun advancePastBreak(
        session: FocusSessionEntity,
        nowMono: Long,
        currentBoot: Int,
        beatWall: Long,
        breakTakenSeconds: Int
    ): FocusSessionEntity {
        val wallNow = System.currentTimeMillis()
        val freshHmac = CryptoIntegrityManager.signSessionState(
            wallNow, nowMono, currentBoot, session.targetDurationSeconds, false
        )
        // ST-03: the exit speaks here, at the flip — the one
        // point every break exit crosses exactly once, so the
        // timer's end and the hand's end sound identically,
        // once, and a countdown tick (which never reaches this
        // function) stays silent.
        breakBoundaryKnock(true)
        return session.copy(
            currentBlockIndex = (session.currentBlockIndex + 1).coerceAtMost(session.totalStudyBlocks),
            isInSafeBreak = false,
            safeBreakSecondsRemaining = 0,
            breakTakenSeconds = breakTakenSeconds,
            elapsedSeconds = 0,
            startWallTimeMillis = wallNow,
            startElapsedRealtimeMillis = nowMono,
            bootCount = currentBoot,
            accumulatedBeforeRebootSeconds = 0,
            lastTickElapsedRealtimeMillis = nowMono,
            hmacSignature = freshHmac,
            shieldLastBeatWallMillis = beatWall
        )
    }

    /** ST-02 (Truth Car 3): end the active session's Safe Break
     *  NOW — the verb the register found missing (a man ready at
     *  minute four waited out six dead minutes; the break ended
     *  only when its timer expired). Performs the identical
     *  transition the expiry flip performs, at the tap, and books
     *  the break's ACTUALLY taken seconds — the length in force
     *  minus the wall-derived remainder at this instant — into
     *  breakTakenSeconds, so the record the session leaves (the
     *  span derivation, the desk clock, the receipt) never claims
     *  the untaken remainder ran.
     *
     *  Reads the stored row fresh and refuses quietly (returns the
     *  row unchanged) when there is nothing to end: no active
     *  session, a different session, a session not in a break. A
     *  routine session's break ends the same way its expiry flip
     *  already ends it — advancing a block declares no success,
     *  so the routines fence is not engaged. */
    suspend fun endSafeBreakEarly(sessionId: Int): FocusSessionEntity? {
        val row = dao.getActiveSessionSnapshot() ?: return null
        if (row.id != sessionId || !row.isActive || !row.isInSafeBreak) return row
        val nowMono = SystemClock.elapsedRealtime()
        val remaining = breakRemainingAfter(
            storedRemaining = row.safeBreakSecondsRemaining,
            lastTickMonoMillis = row.lastTickElapsedRealtimeMillis,
            nowMonoMillis = nowMono
        )
        val taken = (breakLengthSeconds(row) - remaining)
            .coerceIn(0, breakLengthSeconds(row))
        val next = advancePastBreak(
            session = row,
            nowMono = nowMono,
            currentBoot = querySystemBootCount(),
            beatWall = row.shieldLastBeatWallMillis,
            breakTakenSeconds = row.breakTakenSeconds + taken
        )
        dao.updateSession(next)
        return next
    }

    /**
     * Updates session progress, enforcing:
     * 1. Monotonic clock, reboot recovery & HMAC signature verification (+15m Penalty Lock on tamper)
     * 2. Count-Up Stopwatch Mode (counts upward past the 20m minimum lock floor)
     * 3. Multi-Block Study Cycles (1 to 4 blocks) with automatic 10-minute Safe Breaks in between
     */
    suspend fun updateSessionProgress(
        session: FocusSessionEntity,
        stampShieldBeat: Boolean = false
    ): FocusSessionEntity {
        val nowMono = SystemClock.elapsedRealtime()
        val currentBoot = querySystemBootCount()
        // Slice E (Nocke §2.2): the shield's own tick stamps its
        // beat into every copy this function persists — the beat
        // is the finish machine's gap evidence. Other callers
        // (the VM ticker) never stamp it: a beat written by
        // anyone but the shield would be a forged witness.
        val beatWall = if (stampShieldBeat) System.currentTimeMillis() else session.shieldLastBeatWallMillis
        val (verifiedElapsed, tamper) = computeVerifiedSessionElapsed(session)
        val penaltyAdd = if (tamper && session.penaltySecondsAdded == 0) HMAC_TAMPER_PENALTY_SECONDS else session.penaltySecondsAdded
        val effectiveTarget = session.targetDurationSeconds + penaltyAdd

        // Re-anchor if device rebooted so subsequent ticks on the new boot stay monotonic
        val rebootDetected = (session.bootCount > 0 && currentBoot != session.bootCount) ||
            (session.lastTickElapsedRealtimeMillis > 0L && nowMono < session.lastTickElapsedRealtimeMillis)
        val newAccumulated = if (rebootDetected) verifiedElapsed else session.accumulatedBeforeRebootSeconds
        val newStartMono = if (rebootDetected) nowMono else session.startElapsedRealtimeMillis
        val newBootCount = if (rebootDetected) currentBoot else session.bootCount
        val newHmac = if (rebootDetected) {
            CryptoIntegrityManager.signSessionState(
                session.startWallTimeMillis,
                newStartMono,
                newBootCount,
                session.targetDurationSeconds,
                session.isStopwatchMode
            )
        } else {
            session.hmacSignature
        }

        accrueActiveTaskSeconds(session, nowMono)

        if (session.isStopwatchMode) {
            val updated = session.copy(
                elapsedSeconds = verifiedElapsed,
                startElapsedRealtimeMillis = newStartMono,
                bootCount = newBootCount,
                accumulatedBeforeRebootSeconds = newAccumulated,
                lastTickElapsedRealtimeMillis = nowMono,
                hmacSignature = newHmac,
                penaltySecondsAdded = penaltyAdd,
                tamperDetected = tamper,
                shieldLastBeatWallMillis = beatWall
            )
            dao.updateSession(updated)
            return updated
        }

        // Handle active 10-minute Safe Break countdown between multi-block study cycles.
        // The remaining seconds are WALL-DERIVED from the stored
        // (remaining, lastTick) pair — never decremented per call:
        // two tickers drive this function, and a per-call decrement
        // drained a 600s break in ~400 wall seconds (Bus, 09:30 IST).
        if (session.isInSafeBreak) {
            val nextBreakRemaining = breakRemainingAfter(
                storedRemaining = session.safeBreakSecondsRemaining,
                lastTickMonoMillis = session.lastTickElapsedRealtimeMillis,
                nowMonoMillis = nowMono
            )
            return if (nextBreakRemaining == 0) {
                // Expiry: the break ran its full length in force,
                // and the taken ledger books exactly that (ST-02's
                // honesty condition — see breakTakenSeconds).
                val nextBlockSession = advancePastBreak(
                    session = session,
                    nowMono = nowMono,
                    currentBoot = currentBoot,
                    beatWall = beatWall,
                    breakTakenSeconds = session.breakTakenSeconds +
                        breakLengthSeconds(session)
                )
                dao.updateSession(nextBlockSession)
                nextBlockSession
            } else {
                val updatedBreak = session.copy(
                    safeBreakSecondsRemaining = nextBreakRemaining,
                    lastTickElapsedRealtimeMillis = nowMono,
                    shieldLastBeatWallMillis = beatWall
                )
                dao.updateSession(updatedBreak)
                updatedBreak
            }
        }

        // Audit R3b #148: the block's last minute announces
        // itself. The flip into the break was the first signal a
        // block was ending — this crossing (the row's own stamped
        // elapsed passing the 60-seconds-left mark, read against
        // this tick's verified elapsed) fires ONE warning knock,
        // once per crossing; a penalty extension that pushes the
        // deadline back out re-arms it, truthfully. The "+2 MIN"
        // extension verb the persona asked for cannot live here:
        // the target is inside the session's HMAC, and mutating it
        // mid-session would invalidate the seal — that piece is
        // priced and parked, not improvised.
        val remainingNow = effectiveTarget - verifiedElapsed
        val remainingPrev = effectiveTarget - session.elapsedSeconds
        if (remainingPrev > 60 && remainingNow in 1..60) {
            blockWarningKnock()
        }

        val blockFinished = verifiedElapsed >= effectiveTarget
        if (blockFinished && session.currentBlockIndex < session.totalStudyBlocks) {
            val safeBreakSession = session.copy(
                elapsedSeconds = effectiveTarget,
                isInSafeBreak = true,
                safeBreakSecondsRemaining = SAFE_BREAK_DURATION_SECONDS,
                lastTickElapsedRealtimeMillis = nowMono,
                penaltySecondsAdded = penaltyAdd,
                tamperDetected = tamper,
                shieldLastBeatWallMillis = beatWall
            )
            dao.updateSession(safeBreakSession)
            // ST-03: the entry speaks here, at the flip — the
            // latch releases as the block lets go into the break.
            // This branch runs once per entry (the row it writes
            // routes every later tick to the countdown branch),
            // so the voice fires exactly once.
            breakBoundaryKnock(false)
            return safeBreakSession
        }

        val allBlocksCompleted = blockFinished && session.currentBlockIndex >= session.totalStudyBlocks
        // Routine-session fence (Sten §1.6.1): a routine session's own
        // timer may deactivate it, but may NEVER declare success or mint
        // an artifact — booked seconds and outcome come only from the
        // finalise funnel, which runs off evidence, not off the clock.
        val isRoutineSession = session.routineId != null
        val forgedId = if (allBlocksCompleted && !isRoutineSession) {
            CurioCatalog.artifacts.shuffled().first().id
        } else null

        // Slice E (Nocke §2.3): the ending write classifies the
        // outcome, once, from the row's own facts. The gap test
        // reads the INCOMING beat (this tick's own stamp must not
        // launder a dead shield's absence): gap ⇔ the latch was
        // ever set, or the last beat is older than the 10s law
        // (5 missed 2s beats) measured from the later of the beat
        // and the session's start. Routine rows keep '' — their
        // judgement is routineOutcome, booked by the funnel.
        val outcomeStamp = if (!allBlocksCompleted) {
            session.outcome
        } else if (isRoutineSession) {
            session.outcome
        } else {
            val nowWall = System.currentTimeMillis()
            val beatRef = maxOf(session.shieldLastBeatWallMillis, session.startWallTimeMillis)
            if (session.shieldGapSeen || nowWall - beatRef > SHIELD_GAP_THRESHOLD_MILLIS) {
                SessionOutcome.ELAPSED_SHIELD_GAP
            } else {
                SessionOutcome.ELAPSED_CLEAN
            }
        }

        val updated = session.copy(
            elapsedSeconds = verifiedElapsed.coerceAtMost(effectiveTarget),
            startElapsedRealtimeMillis = newStartMono,
            bootCount = newBootCount,
            accumulatedBeforeRebootSeconds = newAccumulated,
            lastTickElapsedRealtimeMillis = nowMono,
            hmacSignature = newHmac,
            penaltySecondsAdded = penaltyAdd,
            isActive = !allBlocksCompleted,
            completedSuccessfully = allBlocksCompleted && !isRoutineSession,
            tamperDetected = tamper,
            forgedArtifactId = if (isRoutineSession) null else forgedId ?: session.forgedArtifactId,
            shieldLastBeatWallMillis = beatWall,
            outcome = outcomeStamp,
            // Truth Car 2 (RC-01): the true end, stamped in the
            // same write that classifies the outcome — first
            // stamp wins, like the outcome itself. This tick is
            // the ending write; its wall instant is the witnessed
            // end the receipt will print on every path.
            endWallMillis = if (allBlocksCompleted && session.endWallMillis == 0L) {
                System.currentTimeMillis()
            } else {
                session.endWallMillis
            }
        )
        dao.updateSession(updated)
        return updated
    }

    suspend fun recordIntercept(
        packageName: String,
        appName: String,
        sessionIntention: String,
        outcome: String,
        triggerReason: String = "Habit"
    ) {
        dao.incrementInterceptCount(packageName)
        dao.insertInterceptLog(
            InterceptLogEntity(
                packageName = packageName,
                appName = appName,
                sessionIntention = sessionIntention,
                outcome = outcome,
                triggerReason = triggerReason
            )
        )
        val active = dao.getActiveSessionSnapshot()
        if (active != null) {
            dao.updateSession(active.copy(phantomReachesBlocked = active.phantomReachesBlocked + 1))
        }
    }

    suspend fun saveCapturedNotification(
        packageName: String,
        appName: String,
        senderTitle: String,
        messageBody: String
    ) {
        dao.insertCapturedNotification(
            CapturedNotificationEntity(
                packageName = packageName,
                appName = appName,
                senderTitle = senderTitle,
                messageBody = messageBody
            )
        )
    }

    /** One-shot snapshot for the FROZEN digest (see the DAO). */
    suspend fun capturedNotificationsSnapshot(): List<CapturedNotificationEntity> =
        dao.getCapturedNotificationsSnapshot()

    /**
     * THE SEALED GATE for digest acts. While a session is active,
     * the held world can be read but never acted on — so both
     * delete paths refuse here, at the repository, regardless of
     * what any surface asks. A hidden chip was never the
     * requirement; a dead chain is. Returns false (and touches
     * nothing) while sealed; runs and returns true otherwise.
     */
    suspend fun deleteCapturedNotification(id: Int): Boolean {
        if (dao.getActiveSessionSnapshot()?.isActive == true) return false
        dao.deleteCapturedNotification(id)
        return true
    }

    suspend fun clearAllCapturedNotifications(): Boolean {
        if (dao.getActiveSessionSnapshot()?.isActive == true) return false
        dao.clearAllCapturedNotifications()
        return true
    }

    // =========================================================================
    // WEEKLY EMERGENCY BREAK — THE TOKEN (TOKEN-SPEC §§2–5)
    // The token is a fact derived from the append-only spend ledger,
    // never a stored counter: remaining = 1 − spends booked against the
    // current week anchor (MonolithWeek). Enforcement lives HERE — the
    // repository refuses a break at zero regardless of what any UI
    // asks; every surface's number is a rendering of this truth.
    // =========================================================================

    /** Remaining breaks for the week containing [nowMillis]. */
    suspend fun emergencyBreaksRemaining(nowMillis: Long = System.currentTimeMillis()): Int {
        val anchor = MonolithWeek.weekAnchorMillis(nowMillis)
        return emergencyBreaksRemainingFromSpendCount(dao.getSpendsForWeek(anchor).size)
    }

    /**
     * The anchor of the current week, re-emitted when it changes.
     * Amendment C (TOKEN-SPEC §5): the flow must emit on a week-anchor
     * change WHILE COLLECTED — an overlay left open across Monday 00:00
     * must never render last week's count. The one-minute re-check also
     * picks up timezone changes on the same tick.
     */
    private fun weekAnchorTickFlow(): Flow<Long> = flow {
        var lastAnchor: Long? = null
        while (true) {
            val anchor = MonolithWeek.weekAnchorMillis(System.currentTimeMillis())
            if (anchor != lastAnchor) {
                lastAnchor = anchor
                emit(anchor)
            }
            delay(60_000L)
        }
    }

    /** Live remaining-count: re-anchors on collection and on rollover. */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeEmergencyBreaksRemaining(): Flow<Int> =
        weekAnchorTickFlow()
            .flatMapLatest { anchor ->
                dao.observeSpendsForWeek(anchor).map { spends ->
                    emergencyBreaksRemainingFromSpendCount(spends.size)
                }
            }
            .distinctUntilChanged()

    /**
     * The ONLY write of a spend, and the only lawful break-class disarm
     * (TOKEN-SPEC §4.1). One transaction: check the week's spends,
     * insert the spend row, disarm — dispatched on the session's kind.
     * Atomicity is the point: no interleaving where the session ends
     * but the spend is lost, the spend lands but the session stays
     * sealed, or two rapid completion callbacks spend twice (the check
     * runs inside the transaction). A hold that completes across the
     * Monday boundary spends against the COMPLETION instant's week
     * (§4.2). Banks, slips and abandoned attempts never reach this
     * function and write nothing (§4.3); the grace-cancel path never
     * passes through here (§4.4).
     */
    suspend fun completeEmergencyBreak(): BreakResult {
        val database = MonolithDatabase.getInstance(context)
        return database.withTransaction {
            val active = dao.getActiveSessionSnapshot()
                ?: return@withTransaction BreakResult.NoActiveSession
            val now = System.currentTimeMillis()
            val anchor = MonolithWeek.weekAnchorMillis(now)
            if (dao.getSpendsForWeek(anchor).size >= 1) {
                return@withTransaction BreakResult.RefusedNoToken
            }
            dao.insertEmergencyBreakSpend(
                EmergencyBreakSpendEntity(
                    sessionId = active.id,
                    spentAtWallMillis = now,
                    weekAnchorMillis = anchor,
                    heldSeconds = EMERGENCY_BREAK_HELD_SECONDS
                )
            )
            if (active.routineId != null || active.profileName.startsWith(ROUTINE_PROFILE_PREFIX)) {
                disarmRoutineSessionByBreak(active)
            } else {
                disarmActiveSession(completed = false, outcomeReason = EMERGENCY_BREAK_REASON)
            }
            BreakResult.Completed
        }
    }

    /**
     * Routine branch of the break dispatch — inside the break
     * transaction, so the token spend and this booking commit or roll
     * back together (TOKEN-SPEC §8 item 9 / B3).
     *
     * Three shapes:
     * - Legacy row (routineId null, recognised by the profile prefix —
     *   coordinator arbitration A3): the interim body stands; it books
     *   tick-accrued elapsed only, never the generic path's wall-clock
     *   max, and writes no Partial Stamp.
     * - SLEEP routine (J2): never judged — the generic disarm runs, in
     *   this transaction, per Sten §4.1.
     * - Any other routine session: the real §5.4 finalise funnel —
     *   [finaliseRoutineSession] books evidence-derived seconds only.
     *   No Partial Stamp is written for a routine session, ever.
     */
    private suspend fun disarmRoutineSessionByBreak(active: FocusSessionEntity) {
        if (active.routineId == null) {
            val (_, tamper) = computeVerifiedSessionElapsed(active)
            dao.updateSession(
                active.copy(
                    elapsedSeconds = active.elapsedSeconds,
                    isActive = false,
                    completedSuccessfully = false,
                    tamperDetected = tamper,
                    outcome = SessionOutcome.VALVE_BROKE,
                    // RC-01: an ending write (unpublishable, but
                    // the row's end is the row's end).
                    endWallMillis = if (active.endWallMillis == 0L) {
                        System.currentTimeMillis()
                    } else {
                        active.endWallMillis
                    }
                )
            )
            return
        }
        val routine = dao.getRoutineById(active.routineId)
        if (routine?.loadoutName == SmartLoadout.SLEEP.name) {
            disarmActiveSession(completed = false, outcomeReason = EMERGENCY_BREAK_REASON)
            return
        }
        finaliseRoutineSession(active, System.currentTimeMillis())
    }

    suspend fun disarmActiveSession(completed: Boolean, outcomeReason: String) {
        val active = dao.getActiveSessionSnapshot() ?: return
        // Routine sessions never take the generic path (Sten §4.1):
        // non-SLEEP routine rows are booked only by the finalise funnel
        // (this also seals a session whose routine row was deleted);
        // SLEEP rows fall through — never judged (J2).
        if (active.routineId != null) {
            val routine = dao.getRoutineById(active.routineId)
            if (routine?.loadoutName != SmartLoadout.SLEEP.name) {
                finaliseRoutineSession(active, System.currentTimeMillis())
                return
            }
        }
        val (verifiedElapsed, tamper) = computeVerifiedSessionElapsed(active)
        val isGraceCancel = outcomeReason.contains("Grace Cancel", ignoreCase = true)
        val bankedElapsed = if (isGraceCancel) 0 else max(verifiedElapsed, active.elapsedSeconds)
        val partialStampId = if (!completed && !isGraceCancel && bankedElapsed >= 60) {
            "partial_stamp"
        } else {
            active.forgedArtifactId
        }
        // Slice E (Nocke §2.3): the disarm doors classify the
        // outcome at this same ending write. The valve's string
        // is the constant, never free text; FLOW's past-floor
        // door is the only completed stopwatch ending.
        val outcomeStamp = when {
            outcomeReason == EMERGENCY_BREAK_REASON -> SessionOutcome.VALVE_BROKE
            completed && active.isStopwatchMode -> SessionOutcome.STOPWATCH_ENDED
            isGraceCancel -> SessionOutcome.GRACE_CANCEL
            else -> SessionOutcome.ENDED_EARLY
        }
        dao.updateSession(
            active.copy(
                elapsedSeconds = bankedElapsed,
                isActive = false,
                completedSuccessfully = completed,
                tamperDetected = tamper,
                forgedArtifactId = partialStampId,
                outcome = outcomeStamp,
                // RC-01: the disarm doors are ending writes too —
                // the FLOW door's stamp is what its slip's ENDED
                // reads (first stamp wins).
                endWallMillis = if (active.endWallMillis == 0L) {
                    System.currentTimeMillis()
                } else {
                    active.endWallMillis
                }
            )
        )
        if (!completed && !isGraceCancel) {
            dao.insertInterceptLog(
                InterceptLogEntity(
                    packageName = "com.monolith.system",
                    appName = if (bankedElapsed >= 60) "Partial Stamp (${bankedElapsed / 60}m)" else "Unlatched Early",
                    sessionIntention = active.intention,
                    outcome = outcomeReason
                )
            )
        }
    }

    /**
     * Computes 7-Day Focus History broken down into daily minutes and 25-minute Focus Blocks.
     */
    suspend fun compute7DayHistory(): List<DailyFocusStat> = withContext(Dispatchers.IO) {
        // D7/A3 exclusions: an unfinalised routine row is worth zero in
        // every total, and a legacy pre-rebuild routine row (snapshot
        // window 0) never enters history at all.
        val sessions = dao.getSessionHistorySnapshot().filter { s ->
            when {
                s.routineId != null && s.routineOutcome == null -> false
                s.routineId == null && s.windowStartMillis == 0L &&
                    s.profileName.startsWith(ROUTINE_PROFILE_PREFIX) -> false
                else -> true
            }
        }
        val dayFmt = SimpleDateFormat("EEE", Locale.US)
        val isoFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val result = mutableListOf<DailyFocusStat>()

        for (daysAgo in 6 downTo 0) {
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -daysAgo)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = cal.timeInMillis
            val endOfDay = startOfDay + 86_400_000L
            val daySessions = sessions.filter { it.startWallTimeMillis in startOfDay until endOfDay }
            val totalSecs = daySessions.sumOf { it.elapsedSeconds }
            val totalMins = totalSecs / 60
            val blocks25 = (totalMins / 25).coerceAtLeast(if (totalMins >= 15) 1 else 0)
            val intercepts = daySessions.sumOf { it.phantomReachesBlocked }

            result.add(
                DailyFocusStat(
                    dayLabel = dayFmt.format(Date(startOfDay)).uppercase(Locale.US),
                    dateIso = isoFmt.format(Date(startOfDay)),
                    totalMinutes = totalMins,
                    blocks25mCount = blocks25,
                    interceptionsCount = intercepts
                )
            )
        }
        result
    }

    // =========================================================================
    // TIMEBOXED TASK QUEUE (SOLAR QUEUE) OPERATIONS
    // =========================================================================

    private val taskAccrual = WallAccrual()

    /**
     * Credits the active task with WALL time, never with tick calls.
     * The old increment added +1 per call of updateSessionProgress —
     * and two tickers call it (the ViewModel loop and the Shield
     * service), so stored "actual" seconds were a call count:
     * ≈90 credited per 60 wall seconds (Hale, LR2-S2-24). Now both
     * tickers observe monotonic deltas through [WallAccrual]; any
     * number of callers sums to the time that actually passed.
     * During a Safe Break the anchor advances without crediting —
     * break minutes never land in a task's stored seconds.
     */
    private suspend fun accrueActiveTaskSeconds(session: FocusSessionEntity, nowMono: Long) {
        val tasks = dao.getAllTasksSnapshot()
        val activeTask = tasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
            ?: tasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.QUEUED }
        val credit = taskAccrual.observe(
            sessionId = session.id,
            accruingTaskId = if (session.isInSafeBreak) null else activeTask?.id,
            nowMonoMillis = nowMono
        )
        if (activeTask != null && !session.isInSafeBreak &&
            (credit > 0 || activeTask.resolvedStatus != TaskQueueStatus.ACTIVE)
        ) {
            dao.updateTask(
                activeTask.copy(
                    status = TaskQueueStatus.ACTIVE.name,
                    actualSecondsSpent = activeTask.actualSecondsSpent + credit
                )
            )
        }
    }

    suspend fun addTaskToQueue(
        title: String,
        estimatedMinutes: Int = 25,
        categoryTag: String = "Study",
        notesOrSubsteps: String = "",
        promoteToActive: Boolean = false
    ): FocusTaskEntity = withContext(Dispatchers.IO) {
        val cleanTitle = title.trim().ifEmpty { "Focused Work Block" }
        // MINUTES DIE (Truth Car 2): 0 is a lawful value now —
        // it means no estimate was given, and it is stored as 0
        // so no surface can print an estimate the man never made.
        // A positive estimate (only pre-order rows and the stored
        // history carry one) keeps the historical clamp.
        val clampedMins = if (estimatedMinutes <= 0) 0 else estimatedMinutes.coerceIn(5, 180)
        val existing = dao.getAllTasksSnapshot()
        val hasActive = existing.any { it.resolvedStatus == TaskQueueStatus.ACTIVE }

        val shouldBeActive = promoteToActive || !hasActive
        if (shouldBeActive && hasActive) {
            existing.filter { it.resolvedStatus == TaskQueueStatus.ACTIVE }.forEach { prevActive ->
                dao.updateTask(
                    prevActive.copy(
                        status = TaskQueueStatus.QUEUED.name,
                        queueOrder = 0
                    )
                )
            }
        }

        val nextOrder = if (shouldBeActive) {
            0
        } else {
            (existing.filter { it.resolvedStatus == TaskQueueStatus.QUEUED }
                .maxOfOrNull { it.queueOrder } ?: 0) + 1
        }

        val newTask = FocusTaskEntity(
            title = cleanTitle,
            notesOrSubsteps = notesOrSubsteps.trim(),
            estimatedMinutes = clampedMins,
            actualSecondsSpent = 0,
            categoryTag = categoryTag.ifBlank { "Study" },
            queueOrder = nextOrder,
            status = if (shouldBeActive) TaskQueueStatus.ACTIVE.name else TaskQueueStatus.QUEUED.name
        )
        val id = dao.insertTask(newTask).toInt()
        newTask.copy(id = id)
    }

    suspend fun updateTaskDetails(
        taskId: Int,
        newTitle: String,
        newEstimatedMinutes: Int,
        newCategoryTag: String,
        newNotes: String
    ): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val existing = dao.getAllTasksSnapshot().firstOrNull { it.id == taskId } ?: return@withContext null
        val updated = existing.copy(
            title = newTitle.trim().ifEmpty { existing.title },
            estimatedMinutes = newEstimatedMinutes.coerceIn(5, 180),
            categoryTag = newCategoryTag.ifBlank { existing.categoryTag },
            notesOrSubsteps = newNotes.trim()
        )
        dao.updateTask(updated)
        updated
    }

    suspend fun extendTaskEstimate(taskId: Int, extraMinutes: Int = 5): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val existing = dao.getAllTasksSnapshot().firstOrNull { it.id == taskId } ?: return@withContext null
        val updated = existing.copy(
            estimatedMinutes = (existing.estimatedMinutes + extraMinutes).coerceIn(5, 240)
        )
        dao.updateTask(updated)
        updated
    }

    /**
     * Marks the target task as COMPLETED (or toggles back to QUEUED if already COMPLETED),
     * records its completion in the active FocusSessionEntity receipt summary if a session is running,
     * and automatically promotes the next QUEUED task to ACTIVE.
     */
    suspend fun completeTaskAndAdvance(taskId: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        val target = allTasks.firstOrNull { it.id == taskId } ?: return@withContext null

        if (target.resolvedStatus == TaskQueueStatus.COMPLETED) {
            // Completion is a LATCH, never a toggle (Lie Register 3
            // MO-1/MO-2): this path once un-completed a finished
            // task without retracting the session summary or count,
            // so a double fire resurrected the task under a record
            // that still counted it — and a re-tear booked it twice.
            // Completing an already-completed task writes nothing
            // and returns the standing active task. Undoing a
            // mistaken finish is a separate, explicit verb (the
            // Last-Finished REOPEN in the Drawer build), never a
            // side effect of completing.
            return@withContext allTasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        }

        var currentSession = dao.getActiveSessionSnapshot()
        val nowWall = System.currentTimeMillis()
        val completedTask = target.copy(
            status = TaskQueueStatus.COMPLETED.name,
            completedAtMillis = nowWall,
            completedInSessionId = currentSession?.id ?: 0
        )
        dao.updateTask(completedTask)

        // Append to active session's receipt summary
        if (currentSession != null) {
            val entry = completedTaskSummaryEntry(target)
            val updatedSummary = if (currentSession.completedTasksSummary.isBlank()) {
                entry
            } else {
                "${currentSession.completedTasksSummary} • $entry"
            }
            currentSession = currentSession.copy(
                completedTasksSummary = updatedSummary,
                tasksCompletedCount = currentSession.tasksCompletedCount + 1
            )
            dao.updateSession(currentSession)
        }

        // Promote next queued task if the completed task was ACTIVE (or if no ACTIVE task remains)
        val remaining = dao.getAllTasksSnapshot()
        val existingActive = remaining.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        if (existingActive != null) {
            return@withContext existingActive
        }

        val nextUp = remaining
            .filter { it.resolvedStatus == TaskQueueStatus.QUEUED }
            .minByOrNull { it.queueOrder }
            ?: remaining.filter { it.resolvedStatus == TaskQueueStatus.DEFERRED }.minByOrNull { it.queueOrder }

        if (nextUp != null) {
            val promoted = nextUp.copy(status = TaskQueueStatus.ACTIVE.name, queueOrder = 0)
            dao.updateTask(promoted)
            if (currentSession != null) {
                currentSession = currentSession.copy(intention = promoted.title)
                dao.updateSession(currentSession)
            }
            return@withContext promoted
        }
        null
    }

    /**
     * The session-summary entry for one completed task — written
     * by [completeTaskAndAdvance] into the session's receipt
     * summary and reconstructed EXACTLY by [reopenStoredTask]'s
     * repair, which strips by string match. One formatter for
     * both sites, so the pair can never drift apart. MINUTES DIE
     * (Truth Car 2): a task that carries no estimate (0) writes
     * no estimate segment — "(0m est …)" would book a figure the
     * man never gave.
     */
    private fun completedTaskSummaryEntry(task: FocusTaskEntity): String {
        val actualMins = (task.actualSecondsSpent / 60).coerceAtLeast(1)
        return if (task.estimatedMinutes > 0) {
            "${task.title} (${task.estimatedMinutes}m est / ${actualMins}m actual)"
        } else {
            "${task.title} (${actualMins}m actual)"
        }
    }

    /**
     * REOPEN — the Last-Finished Line's own verb (Register 3,
     * MO-1/MO-2). completeTaskAndAdvance is COMPLETION-ONLY — its
     * old un-complete toggle branch is deleted ground — so
     * un-completing lives here, explicitly:
     *
     * A COMPLETED task returns to the queue (ACTIVE if none is
     * active), its completion stamp and completing-session id are
     * cleared, and the RECEIPT REPAIR rides: if the completion was
     * booked into the still-active session's receipt, the exact
     * entry it wrote is stripped (last occurrence) and the count
     * decremented — a mistaken DONE, once undone, leaves no false
     * entry on the finish receipt, and an honest later re-finish
     * books exactly one entry. The entry string is reproducible
     * exactly because reopening never changes actualSecondsSpent.
     * A task that is not COMPLETED is left untouched. Returns the
     * task that should be ACTIVE afterwards, mirroring
     * completeTaskAndAdvance's contract with the ViewModel.
     */
    suspend fun reopenStoredTask(taskId: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        val target = allTasks.firstOrNull { it.id == taskId } ?: return@withContext null
        if (target.resolvedStatus != TaskQueueStatus.COMPLETED) {
            return@withContext allTasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        }

        val hasActive = allTasks.any { it.resolvedStatus == TaskQueueStatus.ACTIVE && it.id != taskId }
        val restoredStatus = if (hasActive) TaskQueueStatus.QUEUED else TaskQueueStatus.ACTIVE
        val restored = target.copy(
            status = restoredStatus.name,
            completedAtMillis = 0L,
            completedInSessionId = 0
        )
        dao.updateTask(restored)

        val session = dao.getActiveSessionSnapshot()
        if (session != null && target.completedInSessionId == session.id &&
            session.completedTasksSummary.isNotBlank()
        ) {
            val entry = completedTaskSummaryEntry(target)
            val entries = session.completedTasksSummary.split(" • ").toMutableList()
            val idx = entries.lastIndexOf(entry)
            if (idx >= 0) {
                entries.removeAt(idx)
                dao.updateSession(
                    session.copy(
                        completedTasksSummary = entries.joinToString(" • "),
                        tasksCompletedCount = (session.tasksCompletedCount - 1).coerceAtLeast(0)
                    )
                )
            }
        }

        if (restoredStatus == TaskQueueStatus.ACTIVE) restored
        else allTasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
    }

    suspend fun promoteTaskToActive(taskId: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        val target = allTasks.firstOrNull { it.id == taskId } ?: return@withContext null
        if (target.resolvedStatus == TaskQueueStatus.ACTIVE) return@withContext target

        allTasks.filter { it.resolvedStatus == TaskQueueStatus.ACTIVE }.forEachIndexed { idx, prev ->
            dao.updateTask(
                prev.copy(
                    status = TaskQueueStatus.QUEUED.name,
                    queueOrder = idx + 1
                )
            )
        }

        val promoted = target.copy(
            status = TaskQueueStatus.ACTIVE.name,
            queueOrder = 0
        )
        dao.updateTask(promoted)

        val activeSession = dao.getActiveSessionSnapshot()
        if (activeSession != null) {
            // ESR-02 (Hale's S1, dying in Slice E): a SEALED
            // session's intention is the receipt's title source —
            // rewriting it under the seal would falsify the slip.
            // The seal marker (F-K1) is the repository-level
            // witness: while the active row is the sealed row, the
            // intention write is refused. The promotion itself —
            // the Tasks surface's own bookkeeping — proceeds.
            if (sealedSessionIdFromMarker() != activeSession.id) {
                dao.updateSession(activeSession.copy(intention = promoted.title))
            }
        }
        promoted
    }

    suspend fun moveTaskOrder(taskId: Int, moveUp: Boolean) = withContext(Dispatchers.IO) {
        val queued = dao.getAllTasksSnapshot()
            .filter { it.resolvedStatus == TaskQueueStatus.QUEUED }
            .sortedWith(compareBy<FocusTaskEntity> { it.queueOrder }.thenBy { it.id })
            .toMutableList()
        val index = queued.indexOfFirst { it.id == taskId }
        if (index == -1) return@withContext
        val targetIndex = if (moveUp) index - 1 else index + 1
        if (targetIndex < 0) {
            // Moving up from the very top of QUEUED promotes it to ACTIVE!
            promoteTaskToActive(taskId)
            return@withContext
        }
        if (targetIndex >= queued.size) return@withContext

        val item = queued.removeAt(index)
        queued.add(targetIndex, item)
        queued.forEachIndexed { orderIdx, task ->
            dao.updateTask(task.copy(queueOrder = orderIdx + 1))
        }
    }

    suspend fun deferTask(taskId: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        val target = allTasks.firstOrNull { it.id == taskId } ?: return@withContext null

        val newStatus = if (target.resolvedStatus == TaskQueueStatus.DEFERRED) {
            TaskQueueStatus.QUEUED
        } else {
            TaskQueueStatus.DEFERRED
        }
        val maxOrder = (allTasks.maxOfOrNull { it.queueOrder } ?: 0) + 1
        dao.updateTask(target.copy(status = newStatus.name, queueOrder = maxOrder))

        val remaining = dao.getAllTasksSnapshot()
        val active = remaining.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        if (active != null) return@withContext active

        val nextQueued = remaining.filter { it.resolvedStatus == TaskQueueStatus.QUEUED }.minByOrNull { it.queueOrder }
        if (nextQueued != null) {
            val promoted = nextQueued.copy(status = TaskQueueStatus.ACTIVE.name, queueOrder = 0)
            dao.updateTask(promoted)
            return@withContext promoted
        }
        null
    }

    suspend fun deleteTaskById(taskId: Int): Pair<FocusTaskEntity?, FocusTaskEntity?> = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        val target = allTasks.firstOrNull { it.id == taskId } ?: return@withContext Pair(null, null)

        // PERMANENCE GUARD: a COMPLETED task is a stored record (the
        // Drawer). This path is the Tasks queue's per-task
        // delete-with-undo for UNCOMPLETED rows only — it refuses a
        // stored row outright, so no present or future caller can
        // delete the record through it. (Reopening a stored task is
        // reopenStoredTask's verb, never a delete.)
        if (target.resolvedStatus == TaskQueueStatus.COMPLETED) {
            return@withContext Pair(null, allTasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE })
        }
        dao.deleteTaskById(taskId)

        val remaining = dao.getAllTasksSnapshot()
        var currentActive = remaining.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        if (currentActive == null) {
            val nextQueued = remaining.filter { it.resolvedStatus == TaskQueueStatus.QUEUED }.minByOrNull { it.queueOrder }
            if (nextQueued != null) {
                currentActive = nextQueued.copy(status = TaskQueueStatus.ACTIVE.name, queueOrder = 0)
                dao.updateTask(currentActive)
            }
        }
        Pair(target, currentActive)
    }

    suspend fun restoreDeletedTask(task: FocusTaskEntity): FocusTaskEntity = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        if (task.resolvedStatus == TaskQueueStatus.ACTIVE) {
            allTasks.filter { it.resolvedStatus == TaskQueueStatus.ACTIVE }.forEach { prev ->
                dao.updateTask(prev.copy(status = TaskQueueStatus.QUEUED.name, queueOrder = prev.queueOrder + 1))
            }
        }
        dao.insertTask(task)
        task
    }

    // =========================================================================
    // THE DRAWER — stored tasks are COMPLETED rows of focus_tasks.
    // Reads are status-filtered and paged; there is NO clear, delete
    // or prune for stored rows anywhere in this repository —
    // retention is permanent. (The shipped clear-completed chain
    // was deleted outright at this merge, not merely unrendered:
    // a live chain one chip away from resurfacing is a deletion
    // waiting for a UI.)
    // =========================================================================

    /** One page of stored rows plus the only lawful has-earlier
     *  signal (Truth Car 2, DRW-04): the read asks for PAGE_SIZE+1
     *  rows; a (PAGE_SIZE+1)th row IS an earlier record, and its
     *  absence is proof there is none. The old contract returned
     *  a bare list and the ViewModel inferred "earlier exists"
     *  from page.size == 50 — at an exact multiple of 50 the
     *  Drawer asserted records that did not exist. */
    data class StoredPage(
        val rows: List<FocusTaskEntity>,
        val hasEarlier: Boolean
    )

    suspend fun storedPage(year: Int, month: Int, offset: Int): StoredPage {
        val (from, to) = DrawerDay.monthBoundsMillis(year, month)
        val fetched = dao.getStoredPage(from, to, DRAWER_PAGE_SIZE + 1, offset)
        return StoredPage(
            rows = fetched.take(DRAWER_PAGE_SIZE),
            hasEarlier = fetched.size > DRAWER_PAGE_SIZE
        )
    }

    /** Task-months ∪ declared-word months, newest first — a month
     *  holding only a declared word is still an address in the
     *  index, or the word would be filed nowhere. Word months come
     *  from BOTH word stores (see allDayWords). */
    suspend fun storedMonths(): List<StoredMonth> {
        val fromTasks = dao.getStoredMonths()
        val fromWords = allDayWords().keys.mapNotNull { iso ->
            runCatching {
                val date = java.time.LocalDate.parse(iso)
                StoredMonth(date.year, date.monthValue)
            }.getOrNull()
        }
        return (fromTasks + fromWords).distinct().sortedWith(
            compareByDescending<StoredMonth> { it.year }.thenByDescending { it.month }
        )
    }

    suspend fun searchStoredTasks(query: String, offset: Int): StoredPage {
        val fetched = dao.searchStoredTasks(query.trim(), DRAWER_PAGE_SIZE + 1, offset)
        return StoredPage(
            rows = fetched.take(DRAWER_PAGE_SIZE),
            hasEarlier = fetched.size > DRAWER_PAGE_SIZE
        )
    }

    // Declared day words (REST / ILL / FAMILY) live in TWO stores
    // by design of the two builds that carry them: DayWordStore
    // (SharedPreferences — the Drawer's capture surface writes
    // there) and the day_notes table (the routines build's drain
    // moves prefs words into the table at seed-sync and removes
    // them from prefs). A reader of only one store would watch
    // words vanish at the drain. allDayWords() is the union —
    // prefs (freshest, undrained) wins over the table on conflict,
    // matching the drain's own precedence.
    private val dayWordStore by lazy { DayWordStore(context) }

    fun dayWords(): Map<String, String> = dayWordStore.all()

    suspend fun allDayWords(): Map<String, String> {
        val fromTable = dao.getAllDayNotesSnapshot().associate { it.dateIso to it.word }
        return fromTable + dayWordStore.all()
    }

    fun declareDayWord(dateIso: String, word: String?) {
        dayWordStore.put(dateIso, word)
    }

    /**
     * Used from the Airlock "Stuck on a Problem" coaching flow:
     * Breaks the current intimidating task into a 10-minute micro-step and promotes it to #1 ACTIVE immediately.
     */
    suspend fun splitActiveTaskIntoMicroStep(microStepTitle: String): FocusTaskEntity = withContext(Dispatchers.IO) {
        val cleanStep = microStepTitle.trim().ifEmpty { "Outline first 3 bullet points (10m micro-step)" }
        addTaskToQueue(
            title = cleanStep,
            estimatedMinutes = 10,
            categoryTag = "Micro-Step",
            notesOrSubsteps = FocusTaskEntity.serializeSubSteps(
                listOf(
                    TaskSubStep(0, "Open notes & clear workspace (2m)", false),
                    TaskSubStep(1, cleanStep, false)
                )
            ),
            promoteToActive = true
        )
    }

    suspend fun toggleTaskSubStep(taskId: Int, stepIndex: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val target = dao.getTaskById(taskId)
            ?: dao.getAllTasksSnapshot().firstOrNull { it.id == taskId }
            ?: return@withContext null
        val steps = target.subSteps.toMutableList()
        if (stepIndex !in steps.indices) return@withContext target
        val current = steps[stepIndex]
        steps[stepIndex] = current.copy(isDone = !current.isDone)
        val updated = target.copy(notesOrSubsteps = FocusTaskEntity.serializeSubSteps(steps))
        dao.updateTask(updated)
        updated
    }

    suspend fun addTaskSubStep(taskId: Int, stepTitle: String): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val clean = stepTitle.trim()
        if (clean.isEmpty()) return@withContext null
        val target = dao.getTaskById(taskId)
            ?: dao.getAllTasksSnapshot().firstOrNull { it.id == taskId }
            ?: return@withContext null
        val steps = target.subSteps.toMutableList()
        steps.add(TaskSubStep(index = steps.size, title = clean, isDone = false))
        val updated = target.copy(notesOrSubsteps = FocusTaskEntity.serializeSubSteps(steps))
        dao.updateTask(updated)
        updated
    }

    suspend fun autoSplitTaskIntoMicroSteps(taskId: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val target = dao.getTaskById(taskId)
            ?: dao.getAllTasksSnapshot().firstOrNull { it.id == taskId }
            ?: return@withContext null
        val generated = FocusTaskEntity.defaultSubStepsForCategory(target.title, target.categoryTag)
        val updated = target.copy(notesOrSubsteps = FocusTaskEntity.serializeSubSteps(generated))
        dao.updateTask(updated)
        updated
    }

    suspend fun updateTaskDuration(taskId: Int, estimatedMinutes: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val target = dao.getTaskById(taskId)
            ?: dao.getAllTasksSnapshot().firstOrNull { it.id == taskId }
            ?: return@withContext null
        val clamped = estimatedMinutes.coerceIn(5, 180)
        val updated = target.copy(estimatedMinutes = clamped)
        dao.updateTask(updated)
        updated
    }
}

