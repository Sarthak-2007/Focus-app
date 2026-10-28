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
import com.example.service.MonolithDeviceAdminReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    companion object {
        private val seedMutex = Mutex()
        const val PEEK_DURATION_SECONDS = 120 // 2-Minute Peek
        const val PEEK_DURATION_MILLIS = 120 * 1000L // 2-Minute Peek lease in ms
        const val PEEK_COOLDOWN_MILLIS = 20 * 60 * 1000L // 20-Minute Mandatory Cooldown
        const val STOPWATCH_MIN_FLOOR_SECONDS = 20 * 60 // 20-Minute Minimum Floor
        const val SAFE_BREAK_DURATION_SECONDS = 10 * 60 // 10-Minute Safe Break
        const val HMAC_TAMPER_PENALTY_SECONDS = 15 * 60 // 15-Minute Penalty Lock
    }

    suspend fun ensureSeededAndSyncPackages() = withContext(Dispatchers.IO) {
        seedMutex.withLock {
        val existingApps = dao.getAllAppsSnapshot()
        if (existingApps.isEmpty()) {
            val defaultCatalog = listOf(
                BlockedAppEntity("com.zhiliaoapp.musically", "TikTok", AppCategory.SHORT_VIDEO.name, true, AppBlockTier.LOCKED.name, 15, 42, 7),
                BlockedAppEntity("com.instagram.android", "Instagram", AppCategory.SHORT_VIDEO.name, true, AppBlockTier.LOCKED.name, 20, 38, 5),
                BlockedAppEntity("com.google.android.youtube", "YouTube", AppCategory.SHORT_VIDEO.name, true, AppBlockTier.PEEK_2M.name, 30, 54, 4),
                BlockedAppEntity("com.twitter.android", "X (Twitter)", AppCategory.SOCIAL.name, true, AppBlockTier.LOCKED.name, 15, 29, 6),
                BlockedAppEntity("com.reddit.frontpage", "Reddit", AppCategory.SOCIAL.name, true, AppBlockTier.LOCKED.name, 20, 31, 3),
                BlockedAppEntity("com.snapchat.android", "Snapchat", AppCategory.SOCIAL.name, true, AppBlockTier.LOCKED.name, 20, 12, 1),
                BlockedAppEntity("com.whatsapp", "WhatsApp", AppCategory.MESSAGING.name, true, AppBlockTier.PEEK_2M.name, 45, 19, 2),
                BlockedAppEntity("com.discord", "Discord", AppCategory.MESSAGING.name, true, AppBlockTier.PEEK_2M.name, 45, 27, 2),
                BlockedAppEntity("com.supercell.clashroyale", "Clash Royale", AppCategory.GAMES.name, true, AppBlockTier.LOCKED.name, 15, 22, 3),
                BlockedAppEntity("com.roblox.client", "Roblox", AppCategory.GAMES.name, true, AppBlockTier.LOCKED.name, 20, 8, 0),
                BlockedAppEntity("com.android.chrome", "Chrome", AppCategory.BROWSERS.name, false, AppBlockTier.OPEN.name, 60, 41, 1),
                BlockedAppEntity("com.google.android.apps.docs", "Google Docs", AppCategory.SHOPPING.name, false, AppBlockTier.OPEN.name, 120, 35, 0)
            )
            dao.insertApps(defaultCatalog)
        }

        try {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolvedList = pm.queryIntentActivities(mainIntent, 0)
            val currentMap = dao.getAllAppsSnapshot().associateBy { it.packageName }
            val newlyDiscovered = mutableListOf<BlockedAppEntity>()
            val usageMap = queryRealTodayUsageMinutes()

            for (resolveInfo in resolvedList) {
                val pkg = resolveInfo.activityInfo?.packageName ?: continue
                if (pkg == context.packageName || pkg.startsWith("com.android.settings")) continue
                val label = resolveInfo.loadLabel(pm)?.toString()?.takeIf { it.isNotBlank() } ?: continue
                val existing = currentMap[pkg]
                if (existing == null && newlyDiscovered.size < 18) {
                    val category = categorizePackage(pkg, label)
                    val realUsage = usageMap[pkg] ?: 0
                    newlyDiscovered.add(
                        BlockedAppEntity(
                            packageName = pkg,
                            appName = label,
                            category = category.name,
                            isBlocked = category == AppCategory.SHORT_VIDEO || category == AppCategory.GAMES,
                            blockTier = when (category) {
                                AppCategory.SHORT_VIDEO, AppCategory.GAMES -> AppBlockTier.LOCKED.name
                                AppCategory.MESSAGING -> AppBlockTier.PEEK_2M.name
                                else -> AppBlockTier.OPEN.name
                            },
                            dailyLimitMinutes = 30,
                            todayUsageMinutes = realUsage,
                            interceptCount = 0,
                            isSystemDetected = true
                        )
                    )
                } else if (existing != null && usageMap.containsKey(pkg)) {
                    val realMinutes = usageMap[pkg] ?: existing.todayUsageMinutes
                    if (realMinutes > 0 && realMinutes != existing.todayUsageMinutes) {
                        dao.updateApp(existing.copy(todayUsageMinutes = realMinutes))
                    }
                }
            }
            if (newlyDiscovered.isNotEmpty()) {
                dao.insertApps(newlyDiscovered)
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
                    frictionTier = FrictionTier.KINETIC_CIPHER.name,
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
                    frictionTier = FrictionTier.IRON_MONOLITH.name,
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
                    frictionTier = FrictionTier.IRON_MONOLITH.name,
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
                    frictionTier = FrictionTier.MINDFUL_PAUSE.name,
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

        val existingSessions = dao.getSessionHistorySnapshot()
        if (existingSessions.isEmpty()) {
            val nowWall = System.currentTimeMillis()
            val nowMono = SystemClock.elapsedRealtime()
            val bootCount = querySystemBootCount()
            val dayMs = 86_400_000L

            dao.insertSession(
                FocusSessionEntity(
                    intention = "Organic Chemistry Problem Set",
                    profileName = "Study Loadout",
                    frictionTier = FrictionTier.KINETIC_CIPHER.name,
                    targetDurationSeconds = 50 * 60,
                    elapsedSeconds = 50 * 60,
                    startWallTimeMillis = nowWall - (2 * dayMs),
                    startElapsedRealtimeMillis = max(1L, nowMono - 18_000_000L),
                    bootCount = bootCount,
                    totalStudyBlocks = 2,
                    currentBlockIndex = 2,
                    isActive = false,
                    completedSuccessfully = true,
                    phantomReachesBlocked = 4,
                    forgedArtifactId = "artifact_lissajous",
                    completedTasksSummary = "Mechanism synthesis drills (25m / 24m) • Stereochemistry review cards (25m / 26m)",
                    tasksCompletedCount = 2
                )
            )
            dao.insertSession(
                FocusSessionEntity(
                    intention = "Algorithms & Data Structures Lab",
                    profileName = "Exam Lockdown",
                    frictionTier = FrictionTier.IRON_MONOLITH.name,
                    targetDurationSeconds = 75 * 60,
                    elapsedSeconds = 75 * 60,
                    startWallTimeMillis = nowWall - dayMs,
                    startElapsedRealtimeMillis = max(1L, nowMono - 9_600_000L),
                    bootCount = bootCount,
                    totalStudyBlocks = 3,
                    currentBlockIndex = 3,
                    isActive = false,
                    completedSuccessfully = true,
                    phantomReachesBlocked = 3,
                    forgedArtifactId = "artifact_gyro",
                    completedTasksSummary = "Implement Dijkstra priority queue (35m / 33m) • Red-black tree rotation invariants (25m / 27m) • Benchmark edge cases (15m / 15m)",
                    tasksCompletedCount = 3
                )
            )
            dao.insertSession(
                FocusSessionEntity(
                    intention = "Read Constitutional Law Ch. 6",
                    profileName = "Quick Homework",
                    frictionTier = FrictionTier.MINDFUL_PAUSE.name,
                    targetDurationSeconds = 25 * 60,
                    elapsedSeconds = 25 * 60,
                    startWallTimeMillis = nowWall - 7_200_000L,
                    startElapsedRealtimeMillis = max(1L, nowMono - 7_200_000L),
                    bootCount = bootCount,
                    totalStudyBlocks = 1,
                    currentBlockIndex = 1,
                    isActive = false,
                    completedSuccessfully = true,
                    phantomReachesBlocked = 2,
                    forgedArtifactId = "artifact_loom",
                    completedTasksSummary = "Annotate Commerce Clause case briefs (25m / 25m)",
                    tasksCompletedCount = 1
                )
            )

            dao.insertInterceptLog(
                InterceptLogEntity(
                    timestampMillis = nowWall - 5_400_000L,
                    packageName = "com.zhiliaoapp.musically",
                    appName = "TikTok",
                    sessionIntention = "Algorithms & Data Structures Lab",
                    outcome = "Stayed Focused",
                    triggerReason = "Habit"
                )
            )
            dao.insertInterceptLog(
                InterceptLogEntity(
                    timestampMillis = nowWall - 3_200_000L,
                    packageName = "com.google.android.youtube",
                    appName = "YouTube Shorts",
                    sessionIntention = "Read Constitutional Law Ch. 6",
                    outcome = "Shorts Blocked",
                    triggerReason = "Stuck"
                )
            )

            dao.insertCapturedNotification(
                CapturedNotificationEntity(
                    timestampMillis = nowWall - 2_800_000L,
                    packageName = "com.whatsapp",
                    appName = "WhatsApp",
                    senderTitle = "Study Group • Physics 202",
                    messageBody = "Uploaded the lab worksheet PDF to shared drive."
                )
            )
            dao.insertCapturedNotification(
                CapturedNotificationEntity(
                    timestampMillis = nowWall - 1_500_000L,
                    packageName = "com.discord",
                    appName = "Discord",
                    senderTitle = "CS Study Server",
                    messageBody = "Office hours moved to Room 304 today."
                )
            )
        }

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
                ),
                FocusTaskEntity(
                    title = "Review Spaced-Repetition Deck (30 Cards)",
                    notesOrSubsteps = FocusTaskEntity.serializeSubSteps(
                        listOf(
                            TaskSubStep(0, "Complete first 15 cards under 12s each", true),
                            TaskSubStep(1, "Complete remaining 15 cards & flag hard items", true)
                        )
                    ),
                    estimatedMinutes = 15,
                    actualSecondsSpent = 900,
                    categoryTag = "Exam",
                    queueOrder = 3,
                    status = TaskQueueStatus.COMPLETED.name,
                    completedAtMillis = System.currentTimeMillis() - 3_600_000L
                )
            )
            dao.insertTasks(defaultTasks)
        } else {
            // Deduplicate any duplicate tasks or sessions created by prior race conditions
            val seenTaskTitles = mutableSetOf<String>()
            for (task in existingTasks.sortedBy { it.id }) {
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
        }
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

    fun hasUsageStatsPermission(): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
                ?: return false
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
            mode == AppOpsManager.MODE_ALLOWED
        } catch (_: Exception) {
            false
        }
    }

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
        frictionTier: FrictionTier,
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
                frictionTier = frictionTier.name,
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
    suspend fun startSession(
        intention: String,
        profileName: String,
        frictionTier: FrictionTier,
        durationMinutes: Int,
        studyBlocksCount: Int = 1
    ): FocusSessionEntity {
        dao.deactivateAllSessions()
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
            frictionTier = frictionTier.name,
            targetDurationSeconds = targetSecs,
            elapsedSeconds = 0,
            startWallTimeMillis = wallNow,
            startElapsedRealtimeMillis = monoNow,
            bootCount = bootCount,
            accumulatedBeforeRebootSeconds = 0,
            lastTickElapsedRealtimeMillis = monoNow,
            hmacSignature = hmac,
            isStopwatchMode = isStopwatch,
            totalStudyBlocks = studyBlocksCount.coerceIn(1, 4),
            currentBlockIndex = 1,
            isInSafeBreak = false,
            safeBreakSecondsRemaining = 0,
            penaltySecondsAdded = 0,
            isActive = true,
            completedSuccessfully = false,
            phantomReachesBlocked = 0,
            tamperDetected = false,
            forgedArtifactId = null
        )
        val id = dao.insertSession(session).toInt()
        return session.copy(id = id)
    }

    /**
     * Updates session progress, enforcing:
     * 1. Monotonic clock, reboot recovery & HMAC signature verification (+15m Penalty Lock on tamper)
     * 2. Count-Up Stopwatch Mode (counts upward past the 20m minimum lock floor)
     * 3. Multi-Block Study Cycles (1 to 4 blocks) with automatic 10-minute Safe Breaks in between
     */
    suspend fun updateSessionProgress(session: FocusSessionEntity): FocusSessionEntity {
        val nowMono = SystemClock.elapsedRealtime()
        val currentBoot = querySystemBootCount()
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

        if (!session.isInSafeBreak) {
            incrementActiveTaskSecond()
        }

        if (session.isStopwatchMode) {
            val updated = session.copy(
                elapsedSeconds = verifiedElapsed,
                startElapsedRealtimeMillis = newStartMono,
                bootCount = newBootCount,
                accumulatedBeforeRebootSeconds = newAccumulated,
                lastTickElapsedRealtimeMillis = nowMono,
                hmacSignature = newHmac,
                penaltySecondsAdded = penaltyAdd,
                tamperDetected = tamper
            )
            dao.updateSession(updated)
            return updated
        }

        // Handle active 10-minute Safe Break countdown between multi-block study cycles
        if (session.isInSafeBreak) {
            val nextBreakRemaining = (session.safeBreakSecondsRemaining - 1).coerceAtLeast(0)
            return if (nextBreakRemaining == 0) {
                val wallNow = System.currentTimeMillis()
                val freshHmac = CryptoIntegrityManager.signSessionState(
                    wallNow, nowMono, currentBoot, session.targetDurationSeconds, false
                )
                val nextBlockSession = session.copy(
                    currentBlockIndex = (session.currentBlockIndex + 1).coerceAtMost(session.totalStudyBlocks),
                    isInSafeBreak = false,
                    safeBreakSecondsRemaining = 0,
                    elapsedSeconds = 0,
                    startWallTimeMillis = wallNow,
                    startElapsedRealtimeMillis = nowMono,
                    bootCount = currentBoot,
                    accumulatedBeforeRebootSeconds = 0,
                    lastTickElapsedRealtimeMillis = nowMono,
                    hmacSignature = freshHmac
                )
                dao.updateSession(nextBlockSession)
                nextBlockSession
            } else {
                val updatedBreak = session.copy(
                    safeBreakSecondsRemaining = nextBreakRemaining,
                    lastTickElapsedRealtimeMillis = nowMono
                )
                dao.updateSession(updatedBreak)
                updatedBreak
            }
        }

        val blockFinished = verifiedElapsed >= effectiveTarget
        if (blockFinished && session.currentBlockIndex < session.totalStudyBlocks) {
            val safeBreakSession = session.copy(
                elapsedSeconds = effectiveTarget,
                isInSafeBreak = true,
                safeBreakSecondsRemaining = SAFE_BREAK_DURATION_SECONDS,
                lastTickElapsedRealtimeMillis = nowMono,
                penaltySecondsAdded = penaltyAdd,
                tamperDetected = tamper
            )
            dao.updateSession(safeBreakSession)
            return safeBreakSession
        }

        val allBlocksCompleted = blockFinished && session.currentBlockIndex >= session.totalStudyBlocks
        val forgedId = if (allBlocksCompleted) {
            CurioCatalog.artifacts.shuffled().first().id
        } else null

        val updated = session.copy(
            elapsedSeconds = verifiedElapsed.coerceAtMost(effectiveTarget),
            startElapsedRealtimeMillis = newStartMono,
            bootCount = newBootCount,
            accumulatedBeforeRebootSeconds = newAccumulated,
            lastTickElapsedRealtimeMillis = nowMono,
            hmacSignature = newHmac,
            penaltySecondsAdded = penaltyAdd,
            isActive = !allBlocksCompleted,
            completedSuccessfully = allBlocksCompleted,
            tamperDetected = tamper,
            forgedArtifactId = forgedId ?: session.forgedArtifactId
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

    suspend fun deleteCapturedNotification(id: Int) {
        dao.deleteCapturedNotification(id)
    }

    suspend fun clearAllCapturedNotifications() {
        dao.clearAllCapturedNotifications()
    }

    suspend fun disarmActiveSession(completed: Boolean, outcomeReason: String) {
        val active = dao.getActiveSessionSnapshot() ?: return
        val (verifiedElapsed, tamper) = computeVerifiedSessionElapsed(active)
        val isGraceCancel = outcomeReason.contains("Grace Cancel", ignoreCase = true)
        val bankedElapsed = if (isGraceCancel) 0 else max(verifiedElapsed, active.elapsedSeconds)
        val partialStampId = if (!completed && !isGraceCancel && bankedElapsed >= 60) {
            "partial_stamp"
        } else {
            active.forgedArtifactId
        }
        dao.updateSession(
            active.copy(
                elapsedSeconds = bankedElapsed,
                isActive = false,
                completedSuccessfully = completed,
                tamperDetected = tamper,
                forgedArtifactId = partialStampId
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
        val sessions = dao.getSessionHistorySnapshot()
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

    /**
     * Exports AES-GCM (256-bit) encrypted backup of 3-tier app rules and 7-day focus statistics.
     */
    suspend fun exportEncryptedBackup(): String = withContext(Dispatchers.IO) {
        val apps = dao.getAllAppsSnapshot()
        val sessions = dao.getSessionHistorySnapshot()
        val root = JSONObject().apply {
            put("version", 2)
            put("exportedAtMillis", System.currentTimeMillis())
            put("bootCount", querySystemBootCount())
            val appsArray = JSONArray()
            apps.forEach { app ->
                appsArray.put(
                    JSONObject().apply {
                        put("pkg", app.packageName)
                        put("name", app.appName)
                        put("tier", app.blockTier)
                        put("limit", app.dailyLimitMinutes)
                    }
                )
            }
            put("apps", appsArray)
            put("completedSessionsCount", sessions.count { it.completedSuccessfully })
            put("totalFocusedMinutes", sessions.sumOf { it.elapsedSeconds } / 60)
        }
        CryptoIntegrityManager.encryptBackupAesGcm(root.toString())
    }

    // =========================================================================
    // TIMEBOXED TASK QUEUE (SOLAR QUEUE) OPERATIONS
    // =========================================================================

    private suspend fun incrementActiveTaskSecond() {
        val tasks = dao.getAllTasksSnapshot()
        val activeTask = tasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
            ?: tasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.QUEUED }
        if (activeTask != null) {
            dao.updateTask(
                activeTask.copy(
                    status = TaskQueueStatus.ACTIVE.name,
                    actualSecondsSpent = activeTask.actualSecondsSpent + 1
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
        val clampedMins = estimatedMinutes.coerceIn(5, 180)
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
            // Un-complete back to queue (or active if none active)
            val hasActive = allTasks.any { it.resolvedStatus == TaskQueueStatus.ACTIVE && it.id != taskId }
            val restoredStatus = if (hasActive) TaskQueueStatus.QUEUED else TaskQueueStatus.ACTIVE
            val restored = target.copy(
                status = restoredStatus.name,
                completedAtMillis = 0L
            )
            dao.updateTask(restored)
            return@withContext if (restoredStatus == TaskQueueStatus.ACTIVE) restored else allTasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        }

        var currentSession = dao.getActiveSessionSnapshot()
        val nowWall = System.currentTimeMillis()
        val actualMins = (target.actualSecondsSpent / 60).coerceAtLeast(1)
        val completedTask = target.copy(
            status = TaskQueueStatus.COMPLETED.name,
            completedAtMillis = nowWall,
            completedInSessionId = currentSession?.id ?: 0
        )
        dao.updateTask(completedTask)

        // Append to active session's receipt summary
        if (currentSession != null) {
            val entry = "${target.title} (${target.estimatedMinutes}m est / ${actualMins}m actual)"
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
            dao.updateSession(activeSession.copy(intention = promoted.title))
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

    suspend fun clearCompletedTasks() = withContext(Dispatchers.IO) {
        dao.clearCompletedTasks()
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

