package com.example

import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.BlockedSiteEntity
import com.example.data.EmergencyBreakSpendEntity
import com.example.data.FocusProfileEntity
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.data.GuardSettingsEntity
import com.example.data.MonolithWeek
import com.example.data.TaskQueueStatus
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * P9 (BUILD-SR-8) — PRODUCTION-SHAPED FIXTURES for the migration
 * proof and the whole-day walk.
 *
 * One named data set shaped like his real install, derived firsthand
 * from the v25 substrate on main (`MonolithModels.kt`, DB version
 * 25, 13 entities): a loadout (apps in three tiers, a blocked site,
 * a profile), the Guard's rules row, and history (tasks, sessions,
 * an emergency-break spend). Every value is distinctive — a silent
 * drop, a tier swap, or a default substituted for his real value
 * fails an assertion BY NAME in [P9MigrationProofTest] instead of
 * passing as a plausible row.
 *
 * The same set exists in two forms, kept equal by the tests that
 * consume both:
 *  - ENTITY form ([blockedApps], [guardSettings], [tasks],
 *    [sessions], [spendThisWeek]) for Room-level proofs;
 *  - raw v25 SQL ([V25_DDL] + [seedV25]) for the file-level proof
 *    that runs P1's MIGRATION_25_26 over a v25-shaped file. The DDL
 *    carries the real table + column names of the v25 entities;
 *    non-asserted columns carry DEFAULTs so seed INSERTs name only
 *    the columns the proof reads back.
 */
object P9ProductionFixtures {

    val UTC: ZoneId = ZoneId.of("UTC")

    /** The proof's "now": Wednesday 2026-11-04, 12:00 UTC. */
    val NOW_MILLIS: Long = LocalDateTime.of(2026, 11, 4, 12, 0)
        .atZone(UTC).toInstant().toEpochMilli()

    const val LOCKED_PACKAGE = "com.video.app"
    const val PEEK_PACKAGE = "com.peek.app"
    const val OPEN_PACKAGE = "com.notes.app"
    const val SITE_DOMAIN = "reels.example.com"
    const val PROFILE_NAME = "NEET Deep"
    const val FINISHED_SESSION_ID = 41
    const val ACTIVE_SESSION_ID = 42
    const val ACTIVE_SESSION_INTENTION = "Evening revision — carried sentence"
    const val FINISHED_SESSION_INTENTION = "Rotational mechanics, no phone"
    const val DONE_TASK_TITLE = "Physics: Rotational Mechanics"
    const val OLD_TASK_TITLE = "Chemistry: GOC notes"
    const val QUEUED_TASK_TITLE = "Biology: NCERT re-read"
    const val GUARD_BREAK_MINUTES = 25
    const val GUARD_PEEK_MINUTES = 2

    // ------------------------------------------------------------------
    // Entity form
    // ------------------------------------------------------------------

    val blockedApps: List<BlockedAppEntity> = listOf(
        BlockedAppEntity(
            packageName = LOCKED_PACKAGE, appName = "Video", category = "VIDEO",
            isBlocked = true, blockTier = AppBlockTier.LOCKED.name,
            dailyLimitMinutes = 30, todayUsageMinutes = 0, interceptCount = 7
        ),
        BlockedAppEntity(
            packageName = PEEK_PACKAGE, appName = "Peek", category = "SOCIAL",
            isBlocked = true, blockTier = AppBlockTier.PEEK_2M.name,
            dailyLimitMinutes = 30, todayUsageMinutes = 1, interceptCount = 3
        ),
        BlockedAppEntity(
            packageName = OPEN_PACKAGE, appName = "Notes", category = "TOOLS",
            isBlocked = false, blockTier = AppBlockTier.OPEN.name,
            dailyLimitMinutes = 30, todayUsageMinutes = 12, interceptCount = 0
        )
    )

    val blockedSite: BlockedSiteEntity = BlockedSiteEntity(domain = SITE_DOMAIN)

    val profile: FocusProfileEntity = FocusProfileEntity(
        id = 3, name = PROFILE_NAME, codeName = "DEEP",
        description = "Morning loadout", defaultMinutes = 300,
        blockedCategoriesCsv = "VIDEO,SOCIAL", scheduleLabel = "Weekday",
        isScheduleEnabled = true, startHour = 4, startMinute = 0,
        endHour = 14, endMinute = 0,
        appTiersCsv = "$LOCKED_PACKAGE:LOCKED", appLimitsCsv = "",
        sitesCsv = SITE_DOMAIN,
        soundEnabled = true, sniperEnabled = false, browserBlockEnabled = true
    )

    val guardSettings: GuardSettingsEntity = GuardSettingsEntity(
        id = 1, browserBlockerEnabled = true,
        routineReseedV1Done = true, routineReseedNoteDismissed = false,
        lastBootAtMillis = 1_762_000_000_000L,
        lastRoutineTickMillis = 1_762_100_000_000L,
        lastCompositeSessionId = FINISHED_SESSION_ID,
        lastReceiptOutcome = "HELD", receiptSequence = 12,
        lastReceiptSessionId = FINISHED_SESSION_ID,
        shortsReelsSniperEnabled = true,
        peekMinutes = GUARD_PEEK_MINUTES, breakMinutes = GUARD_BREAK_MINUTES,
        notificationBodyConsent = "UNSET"
    )

    /** History: one task finished inside a 24 h drawer window, one
     *  finished before it, one still queued. */
    fun tasks(): List<FocusTaskEntity> = listOf(
        FocusTaskEntity(
            id = 11, title = DONE_TASK_TITLE, categoryTag = "PHYSICS",
            queueOrder = 1, status = TaskQueueStatus.COMPLETED.name,
            actualSecondsSpent = 5400,
            completedAtMillis = NOW_MILLIS - 2 * 3_600_000L,
            completedInSessionId = FINISHED_SESSION_ID
        ),
        FocusTaskEntity(
            id = 12, title = OLD_TASK_TITLE, categoryTag = "CHEMISTRY",
            queueOrder = 2, status = TaskQueueStatus.COMPLETED.name,
            actualSecondsSpent = 3600,
            completedAtMillis = NOW_MILLIS - 26 * 3_600_000L,
            completedInSessionId = 40
        ),
        FocusTaskEntity(
            id = 13, title = QUEUED_TASK_TITLE, categoryTag = "BIOLOGY",
            queueOrder = 3, status = TaskQueueStatus.QUEUED.name
        )
    )

    /** History: a finished session and the standing active one whose
     *  intention is the sentence the carry must preserve verbatim. */
    fun sessions(): List<FocusSessionEntity> = listOf(
        FocusSessionEntity(
            id = FINISHED_SESSION_ID, intention = FINISHED_SESSION_INTENTION,
            profileName = PROFILE_NAME, targetDurationSeconds = 10_800,
            elapsedSeconds = 10_740,
            startWallTimeMillis = NOW_MILLIS - 5 * 3_600_000L,
            startElapsedRealtimeMillis = 900_000L,
            isActive = false, completedSuccessfully = true,
            windowStartMillis = NOW_MILLIS - 5 * 3_600_000L,
            windowEndMillis = NOW_MILLIS - 2 * 3_600_000L,
            tasksCompletedCount = 1, completedTasksSummary = DONE_TASK_TITLE
        ),
        FocusSessionEntity(
            id = ACTIVE_SESSION_ID, intention = ACTIVE_SESSION_INTENTION,
            profileName = PROFILE_NAME, targetDurationSeconds = 7_200,
            elapsedSeconds = 600,
            startWallTimeMillis = NOW_MILLIS - 600_000L,
            startElapsedRealtimeMillis = 4_500_000L,
            isActive = true, completedSuccessfully = false
        )
    )

    /** The week's emergency minute, already spent before the
     *  redesign arrives: the crank must be born spent, never
     *  refunded (the carry P1's store implements, proven here at
     *  production shape). */
    fun spendThisWeek(): EmergencyBreakSpendEntity = EmergencyBreakSpendEntity(
        id = 5, sessionId = FINISHED_SESSION_ID,
        spentAtWallMillis = NOW_MILLIS - 3_600_000L,
        weekAnchorMillis = MonolithWeek.weekAnchorMillis(NOW_MILLIS, UTC),
        heldSeconds = 60
    )

    // ------------------------------------------------------------------
    // Raw v25 SQL form — the file the migration proof upgrades
    // ------------------------------------------------------------------

    private const val I = "INTEGER NOT NULL DEFAULT 0"
    private const val T = "TEXT NOT NULL DEFAULT ''"

    /** v25 table shapes for the seven tables the proof seeds and
     *  reads back, column names exactly as the v25 entities declare
     *  them (read firsthand from `MonolithModels.kt` on main). */
    val V25_DDL: List<String> = listOf(
        "CREATE TABLE IF NOT EXISTS `blocked_apps` (" +
            "`packageName` TEXT NOT NULL, `appName` TEXT NOT NULL, `category` TEXT NOT NULL, " +
            "`isBlocked` $I, `blockTier` TEXT NOT NULL, `dailyLimitMinutes` $I, " +
            "`todayUsageMinutes` $I, `interceptCount` $I, " +
            "`activePeekUntilElapsedRealtime` $I, `lastPeekEndElapsedRealtime` $I, " +
            "`isSystemDetected` $I, PRIMARY KEY(`packageName`))",
        "CREATE TABLE IF NOT EXISTS `blocked_sites` (" +
            "`domain` TEXT NOT NULL, `redirectTarget` TEXT NOT NULL, PRIMARY KEY(`domain`))",
        "CREATE TABLE IF NOT EXISTS `focus_profiles` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, " +
            "`codeName` TEXT NOT NULL, `description` TEXT NOT NULL, `defaultMinutes` $I, " +
            "`blockedCategoriesCsv` TEXT NOT NULL, `scheduleLabel` TEXT NOT NULL, " +
            "`isScheduleEnabled` $I, `startHour` $I, `startMinute` $I, `endHour` $I, " +
            "`endMinute` $I, `appTiersCsv` TEXT NOT NULL, `appLimitsCsv` TEXT NOT NULL, " +
            "`sitesCsv` TEXT NOT NULL, `soundEnabled` INTEGER, `sniperEnabled` INTEGER, " +
            "`browserBlockEnabled` INTEGER)",
        "CREATE TABLE IF NOT EXISTS `guard_settings` (" +
            "`id` INTEGER PRIMARY KEY NOT NULL, `browserBlockerEnabled` $I, " +
            "`routineReseedV1Done` $I, `routineReseedNoteDismissed` $I, " +
            "`lastBootAtMillis` $I, `lastRoutineTickMillis` $I, " +
            "`lastCompositeSessionId` $I, `lastReceiptOutcome` TEXT NOT NULL, " +
            "`receiptSequence` $I, `lastReceiptSessionId` $I, " +
            "`shortsReelsSniperEnabled` $I, `peekMinutes` $I, `breakMinutes` $I, " +
            "`notificationBodyConsent` TEXT NOT NULL)",
        "CREATE TABLE IF NOT EXISTS `focus_tasks` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, " +
            "`noteText` TEXT NOT NULL, `notesOrSubsteps` TEXT NOT NULL, " +
            "`estimatedMinutes` $I, `actualSecondsSpent` $I, `categoryTag` TEXT NOT NULL, " +
            "`queueOrder` $I, `status` TEXT NOT NULL, `completedAtMillis` $I, " +
            "`completedInSessionId` $I, `startedAtSessionElapsedSec` INTEGER)",
        "CREATE TABLE IF NOT EXISTS `focus_sessions` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `intention` TEXT NOT NULL, " +
            "`profileName` TEXT NOT NULL, `targetDurationSeconds` $I, `elapsedSeconds` $I, " +
            "`startWallTimeMillis` $I, `startElapsedRealtimeMillis` $I, `bootCount` $I, " +
            "`accumulatedBeforeRebootSeconds` $I, `lastTickElapsedRealtimeMillis` $I, " +
            "`hmacSignature` TEXT NOT NULL, `isStopwatchMode` $I, `totalStudyBlocks` $I, " +
            "`currentBlockIndex` $I, `isInSafeBreak` $I, `safeBreakSecondsRemaining` $I, " +
            "`breakTakenSeconds` $I, `callExcludedSeconds` $I, `penaltySecondsAdded` $I, " +
            "`isActive` $I, `completedSuccessfully` $I, `phantomReachesBlocked` $I, " +
            "`tamperDetected` $I, `forgedArtifactId` TEXT, `completedTasksSummary` TEXT NOT NULL, " +
            "`tasksCompletedCount` $I, `routineId` INTEGER, `windowStartMillis` $I, " +
            "`windowEndMillis` $I, `routineOutcome` TEXT, `evidenceMask` $I, " +
            "`firstEvidenceMillis` $I, `lastEvidenceMillis` $I, `taskId` INTEGER, " +
            "`heldAppsAtSeal` $I, `heldSitesAtSeal` $I, `heldShutAtSeal` $I, " +
            "`heldPeekAtSeal` $I, `releaseFaultShield` $I, `releaseRetriesDone` $I, " +
            "`releaseFaultedAtWallMillis` $I, `outcome` TEXT NOT NULL, " +
            "`shieldLastBeatWallMillis` $I, `shieldGapSeen` $I, `rebootGapSeconds` $I, " +
            "`endWallMillis` $I, `receiptNumber` $I, `failureCausePackage` TEXT NOT NULL, " +
            "`failureCauseKind` TEXT NOT NULL, `reflectionNote` TEXT NOT NULL, " +
            "`finishSignature` TEXT NOT NULL, `prevFinishSignature` TEXT NOT NULL, " +
            "`midBlockSafeBreak` $I, `extraSafeBreakSeconds` $I, `breakSeconds` $I, " +
            "`extensionSecondsAdded` $I, `extensionLog` TEXT NOT NULL, `withinLabel` TEXT, " +
            "`plannedEndWallMillis` $I)",
        "CREATE TABLE IF NOT EXISTS `emergency_break_spends` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` $I, " +
            "`spentAtWallMillis` $I, `weekAnchorMillis` $I, `heldSeconds` $I)"
    )

    /** Seed statements for the v25 file — the same rows as the
     *  entity form above, value for value on every asserted column. */
    fun seedV25Sql(): List<String> {
        val anchor = MonolithWeek.weekAnchorMillis(NOW_MILLIS, UTC)
        return listOf(
            "INSERT INTO blocked_apps (packageName, appName, category, isBlocked, blockTier, " +
                "dailyLimitMinutes, todayUsageMinutes, interceptCount) VALUES " +
                "('$LOCKED_PACKAGE', 'Video', 'VIDEO', 1, 'LOCKED', 30, 0, 7)",
            "INSERT INTO blocked_apps (packageName, appName, category, isBlocked, blockTier, " +
                "dailyLimitMinutes, todayUsageMinutes, interceptCount) VALUES " +
                "('$PEEK_PACKAGE', 'Peek', 'SOCIAL', 1, 'PEEK_2M', 30, 1, 3)",
            "INSERT INTO blocked_apps (packageName, appName, category, isBlocked, blockTier, " +
                "dailyLimitMinutes, todayUsageMinutes, interceptCount) VALUES " +
                "('$OPEN_PACKAGE', 'Notes', 'TOOLS', 0, 'OPEN', 30, 12, 0)",
            "INSERT INTO blocked_sites (domain, redirectTarget) VALUES ('$SITE_DOMAIN', '')",
            "INSERT INTO focus_profiles (id, name, codeName, description, defaultMinutes, " +
                "blockedCategoriesCsv, scheduleLabel, isScheduleEnabled, startHour, startMinute, " +
                "endHour, endMinute, appTiersCsv, sitesCsv, soundEnabled, sniperEnabled, " +
                "browserBlockEnabled) VALUES (3, '$PROFILE_NAME', 'DEEP', 'Morning loadout', 300, " +
                "'VIDEO,SOCIAL', 'Weekday', 1, 4, 0, 14, 0, '$LOCKED_PACKAGE:LOCKED', " +
                "'$SITE_DOMAIN', 1, 0, 1)",
            "INSERT INTO guard_settings (id, browserBlockerEnabled, routineReseedV1Done, " +
                "lastBootAtMillis, lastRoutineTickMillis, lastCompositeSessionId, " +
                "lastReceiptOutcome, receiptSequence, lastReceiptSessionId, " +
                "shortsReelsSniperEnabled, peekMinutes, breakMinutes, notificationBodyConsent) " +
                "VALUES (1, 1, 1, 1762000000000, 1762100000000, $FINISHED_SESSION_ID, 'HELD', 12, " +
                "$FINISHED_SESSION_ID, 1, $GUARD_PEEK_MINUTES, $GUARD_BREAK_MINUTES, 'UNSET')",
            "INSERT INTO focus_tasks (id, title, categoryTag, queueOrder, status, " +
                "actualSecondsSpent, completedAtMillis, completedInSessionId) VALUES " +
                "(11, '$DONE_TASK_TITLE', 'PHYSICS', 1, 'COMPLETED', 5400, " +
                "${NOW_MILLIS - 2 * 3_600_000L}, $FINISHED_SESSION_ID)",
            "INSERT INTO focus_tasks (id, title, categoryTag, queueOrder, status, " +
                "actualSecondsSpent, completedAtMillis, completedInSessionId) VALUES " +
                "(12, '$OLD_TASK_TITLE', 'CHEMISTRY', 2, 'COMPLETED', 3600, " +
                "${NOW_MILLIS - 26 * 3_600_000L}, 40)",
            "INSERT INTO focus_tasks (id, title, categoryTag, queueOrder, status) VALUES " +
                "(13, '$QUEUED_TASK_TITLE', 'BIOLOGY', 3, 'QUEUED')",
            "INSERT INTO focus_sessions (id, intention, profileName, targetDurationSeconds, " +
                "elapsedSeconds, startWallTimeMillis, startElapsedRealtimeMillis, isActive, " +
                "completedSuccessfully, windowStartMillis, windowEndMillis, tasksCompletedCount, " +
                "completedTasksSummary) VALUES ($FINISHED_SESSION_ID, " +
                "'$FINISHED_SESSION_INTENTION', '$PROFILE_NAME', 10800, 10740, " +
                "${NOW_MILLIS - 5 * 3_600_000L}, 900000, 0, 1, " +
                "${NOW_MILLIS - 5 * 3_600_000L}, ${NOW_MILLIS - 2 * 3_600_000L}, 1, " +
                "'$DONE_TASK_TITLE')",
            "INSERT INTO focus_sessions (id, intention, profileName, targetDurationSeconds, " +
                "elapsedSeconds, startWallTimeMillis, startElapsedRealtimeMillis, isActive, " +
                "completedSuccessfully) VALUES ($ACTIVE_SESSION_ID, " +
                "'$ACTIVE_SESSION_INTENTION', '$PROFILE_NAME', 7200, 600, " +
                "${NOW_MILLIS - 600_000L}, 4500000, 1, 0)",
            "INSERT INTO emergency_break_spends (id, sessionId, spentAtWallMillis, " +
                "weekAnchorMillis, heldSeconds) VALUES (5, $FINISHED_SESSION_ID, " +
                "${NOW_MILLIS - 3_600_000L}, $anchor, 60)"
        )
    }
}
