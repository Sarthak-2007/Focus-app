package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MonolithDao {
    // Blocked Apps (3-Tier: LOCKED, PEEK_2M, OPEN)
    @Query("SELECT * FROM blocked_apps ORDER BY isBlocked DESC, interceptCount DESC, appName ASC")
    fun getAllApps(): Flow<List<BlockedAppEntity>>

    @Query("SELECT * FROM blocked_apps")
    suspend fun getAllAppsSnapshot(): List<BlockedAppEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<BlockedAppEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: BlockedAppEntity)

    @Update
    suspend fun updateApp(app: BlockedAppEntity)

    @Query("UPDATE blocked_apps SET isBlocked = :isBlocked, blockTier = :blockTier WHERE packageName = :packageName")
    suspend fun setAppTier(packageName: String, isBlocked: Boolean, blockTier: String)

    @Query("UPDATE blocked_apps SET activePeekUntilElapsedRealtime = :activeUntilMono, lastPeekEndElapsedRealtime = :peekEndMono WHERE packageName = :packageName")
    suspend fun updatePeekLeaseAndCooldown(packageName: String, activeUntilMono: Long, peekEndMono: Long)
    /** Batch-10 (pill Flag B companion): the one-live-lease law —
     *  granting a lease zeroes every OTHER app's live lease. */
    @Query("UPDATE blocked_apps SET activePeekUntilElapsedRealtime = 0 WHERE packageName != :packageName AND activePeekUntilElapsedRealtime > 0")
    suspend fun clearOtherPeekLeases(packageName: String)

    /** The prune (Ordner lane): rows for packages the phone no longer
     *  has leave the table at sync — the catalog is the phone's truth,
     *  not an archive of everything it once held. */
    @Query("DELETE FROM blocked_apps WHERE packageName IN (:packageNames)")
    suspend fun deleteAppsByPackage(packageNames: List<String>)

    @Query("UPDATE blocked_apps SET interceptCount = interceptCount + 1 WHERE packageName = :packageName")
    suspend fun incrementInterceptCount(packageName: String)

    @Query("UPDATE blocked_apps SET dailyLimitMinutes = :limitMinutes WHERE packageName = :packageName")
    suspend fun updateDailyLimit(packageName: String, limitMinutes: Int)

    // Focus Profiles & Schedules
    @Query("SELECT * FROM focus_profiles ORDER BY id ASC")
    fun getAllProfiles(): Flow<List<FocusProfileEntity>>

    @Query("SELECT * FROM focus_profiles")
    suspend fun getAllProfilesSnapshot(): List<FocusProfileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<FocusProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: FocusProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: FocusProfileEntity)

    // Focus Sessions
    @Query("SELECT * FROM focus_sessions WHERE isActive = 1 ORDER BY id DESC LIMIT 1")
    fun getActiveSessionFlow(): Flow<FocusSessionEntity?>

    @Query("SELECT * FROM focus_sessions WHERE isActive = 1 ORDER BY id DESC LIMIT 1")
    suspend fun getActiveSessionSnapshot(): FocusSessionEntity?

    @Query("SELECT * FROM focus_sessions ORDER BY startWallTimeMillis DESC")
    fun getSessionHistoryFlow(): Flow<List<FocusSessionEntity>>

    /** The Drawer cabinet's forged state (chrome C10): the distinct
     *  artifact ids real sessions actually forged. */
    @Query("SELECT DISTINCT forgedArtifactId FROM focus_sessions WHERE forgedArtifactId IS NOT NULL")
    suspend fun getForgedArtifactIds(): List<String>

    @Query("SELECT * FROM focus_sessions ORDER BY startWallTimeMillis DESC")
    suspend fun getSessionHistorySnapshot(): List<FocusSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Query("DELETE FROM focus_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Int)

    // Slice E (9→10): the finish machine's reads. The E0 edge
    // re-reads the ended row by id (its history emission is a list;
    // the classification reads the row itself, fresh).
    @Query("SELECT * FROM focus_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Int): FocusSessionEntity?

    @Query("SELECT * FROM focus_sessions WHERE id = :id LIMIT 1")
    fun getSessionByIdFlow(id: Int): Flow<FocusSessionEntity?>

    // The gap latch (Nocke §2.2): a targeted single-column UPDATE —
    // a latch write can never clobber a tick's row copy landing in
    // the same instant.
    @Query("UPDATE focus_sessions SET shieldGapSeen = 1 WHERE id = :id")
    suspend fun latchShieldGap(id: Int)

    // The release-fault record (Draht): written once per fault by
    // the finish machine; the retry cadence increments its own
    // counter through the same targeted shape.
    @Query(
        "UPDATE focus_sessions SET releaseFaultShield = 1, releaseFaultedAtWallMillis = :wallMillis WHERE id = :id"
    )
    suspend fun stampReleaseFault(id: Int, wallMillis: Long)

    @Query("UPDATE focus_sessions SET releaseRetriesDone = :done WHERE id = :id")
    suspend fun stampReleaseRetries(id: Int, done: Int)

    // The receipt's deflection read (Zettel): intercept instants in
    // the session's own wall window, oldest first — the reduction
    // (30s-per-package dedupe, break-window exclusion) is assembly
    // logic in the repository, not SQL.
    @Query("SELECT * FROM emergency_break_spends WHERE sessionId = :sessionId ORDER BY spentAtWallMillis ASC")
    suspend fun getSpendsForSession(sessionId: Int): List<EmergencyBreakSpendEntity>

    @Query("UPDATE focus_sessions SET isActive = 0 WHERE isActive = 1")
    suspend fun deactivateAllSessions()

    // Emergency Break Spends (TOKEN-SPEC §2 — append-only ledger; the
    // weekly token is DERIVED from these rows, never stored as a count)
    @Insert
    suspend fun insertEmergencyBreakSpend(spend: EmergencyBreakSpendEntity)

    @Query("SELECT * FROM emergency_break_spends WHERE weekAnchorMillis = :anchor")
    suspend fun getSpendsForWeek(anchor: Long): List<EmergencyBreakSpendEntity>

    @Query("SELECT * FROM emergency_break_spends WHERE weekAnchorMillis = :anchor")
    fun observeSpendsForWeek(anchor: Long): Flow<List<EmergencyBreakSpendEntity>>

    @Query("SELECT * FROM emergency_break_spends ORDER BY spentAtWallMillis DESC")
    suspend fun getAllSpendsSnapshot(): List<EmergencyBreakSpendEntity>

    // Task Accrual Segments (#110 — how a task's stored seconds
    // distribute over days; the lifetime scalar on focus_tasks stays
    // the total of record, and post-v13 the segments sum to it by
    // construction: both are written from the same observed credit)
    @Query(
        "INSERT INTO task_accrual_segments (taskId, sessionId, dateIso, seconds) " +
        "VALUES (:taskId, :sessionId, :dateIso, :seconds) " +
        "ON CONFLICT(taskId, sessionId, dateIso) DO UPDATE SET seconds = seconds + excluded.seconds"
    )
    suspend fun addTaskAccrualSegment(taskId: Int, sessionId: Int, dateIso: String, seconds: Int)

    @Query("SELECT * FROM task_accrual_segments WHERE taskId = :taskId ORDER BY dateIso ASC")
    suspend fun getAccrualSegmentsForTask(taskId: Int): List<TaskAccrualSegmentEntity>

    @Query("SELECT * FROM task_accrual_segments WHERE dateIso = :dateIso ORDER BY taskId ASC")
    suspend fun getAccrualSegmentsForDate(dateIso: String): List<TaskAccrualSegmentEntity>

    // Intercept Logs
    @Query("SELECT * FROM intercept_logs ORDER BY timestampMillis DESC")
    fun getInterceptLogsFlow(): Flow<List<InterceptLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterceptLog(log: InterceptLogEntity): Long

    /** C-11 (Fein, Tor round 7): the arrival row's outcome is
     *  written into the SAME row when it becomes known — a
     *  targeted single-column update, so a resolution can never
     *  mint a second row or move the arrival's timestamp. */
    @Query("UPDATE intercept_logs SET outcome = :outcome WHERE id = :id")
    suspend fun updateInterceptOutcome(id: Long, outcome: String)

    // Captured Notifications (Silent Notification Catcher & Persistent Digest)
    @Query("SELECT * FROM captured_notifications ORDER BY timestampMillis DESC LIMIT 100")
    fun getCapturedNotificationsFlow(): Flow<List<CapturedNotificationEntity>>

    /**
     * One-shot snapshot for the FROZEN digest: the same rows the
     * flow would print, taken once, at the freeze. Deliberately NO
     * LIMIT — a held item past any cap is still held, and a frozen
     * read that silently dropped it would lie by omission. (The
     * live flow keeps its shipped cap; only the snapshot — the
     * sealed truth surface — is unbounded.)
     */
    @Query("SELECT * FROM captured_notifications ORDER BY timestampMillis DESC")
    suspend fun getCapturedNotificationsSnapshot(): List<CapturedNotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapturedNotification(notification: CapturedNotificationEntity)

    @Query("DELETE FROM captured_notifications WHERE id = :id")
    suspend fun deleteCapturedNotification(id: Int)

    @Query("DELETE FROM captured_notifications")
    suspend fun clearAllCapturedNotifications()

    // Timeboxed Task Queue (Solar Queue)
    @Query("SELECT * FROM focus_tasks ORDER BY CASE status WHEN 'ACTIVE' THEN 0 WHEN 'QUEUED' THEN 1 WHEN 'DEFERRED' THEN 2 ELSE 3 END, queueOrder ASC, id ASC")
    fun getAllTasksFlow(): Flow<List<FocusTaskEntity>>

    @Query("SELECT * FROM focus_tasks ORDER BY CASE status WHEN 'ACTIVE' THEN 0 WHEN 'QUEUED' THEN 1 WHEN 'DEFERRED' THEN 2 ELSE 3 END, queueOrder ASC, id ASC")
    suspend fun getAllTasksSnapshot(): List<FocusTaskEntity>

    @Query("SELECT * FROM focus_tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Int): FocusTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: FocusTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<FocusTaskEntity>)

    @Update
    suspend fun updateTask(task: FocusTaskEntity)

    @Query("DELETE FROM focus_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)

    // =========================================================================
    // THE DRAWER — stored tasks are COMPLETED rows of focus_tasks.
    // There is no archive table and no delete path for stored rows:
    // retention is permanent. Every read is paged (LIMIT + OFFSET,
    // newest first, id tiebreak for a total order) — no read may
    // silently cap the record. Filing day/month follow the 03:30
    // rule (DrawerDay): the months index subtracts the same 210
    // minutes inside SQLite that DrawerDay subtracts in Kotlin.
    // =========================================================================

    // The page size is the CALLER's (Truth Car 2, DRW-04): the
    // repository asks for page+1 rows and reads the extra row as
    // the only lawful has-earlier signal — a page that merely
    // came back full proves nothing.
    @Query("SELECT * FROM focus_tasks WHERE status = 'COMPLETED' AND completedAtMillis >= :fromMillis AND completedAtMillis < :toMillis ORDER BY completedAtMillis DESC, id DESC LIMIT :limit OFFSET :offset")
    suspend fun getStoredPage(fromMillis: Long, toMillis: Long, limit: Int, offset: Int): List<FocusTaskEntity>

    @Query("SELECT CAST(strftime('%Y', (completedAtMillis / 1000) - 12600, 'unixepoch', 'localtime') AS INTEGER) AS year, CAST(strftime('%m', (completedAtMillis / 1000) - 12600, 'unixepoch', 'localtime') AS INTEGER) AS month FROM focus_tasks WHERE status = 'COMPLETED' AND completedAtMillis > 0 GROUP BY year, month ORDER BY year DESC, month DESC")
    suspend fun getStoredMonths(): List<StoredMonth>

    @Query("SELECT * FROM focus_tasks WHERE status = 'COMPLETED' AND completedAtMillis > 0 AND (title LIKE '%' || :q || '%' OR categoryTag LIKE '%' || :q || '%' OR notesOrSubsteps LIKE '%' || :q || '%') ORDER BY completedAtMillis DESC, id DESC LIMIT :limit OFFSET :offset")
    suspend fun searchStoredTasks(q: String, limit: Int, offset: Int): List<FocusTaskEntity>

    // Truth Car 2 (RC-04): the claimed № stamped on the session
    // row, written only inside the claim transaction — the row
    // becomes the latch the guarantee rests on. Targeted UPDATE,
    // one column, for the same §7.5 reason as the claim's own
    // settings write: nothing else on the row may move.
    @Query("UPDATE focus_sessions SET receiptNumber = :number WHERE id = :sessionId")
    suspend fun stampReceiptNumber(sessionId: Int, number: Int)

    // Blocked Websites (user-editable; enforced by the accessibility service)
    @Query("SELECT * FROM blocked_sites ORDER BY domain ASC")
    fun getAllSitesFlow(): Flow<List<BlockedSiteEntity>>

    @Query("SELECT * FROM blocked_sites ORDER BY domain ASC")
    suspend fun getAllSitesSnapshot(): List<BlockedSiteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSites(sites: List<BlockedSiteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSite(site: BlockedSiteEntity)

    @Query("DELETE FROM blocked_sites WHERE domain = :domain")
    suspend fun deleteSite(domain: String)

    // Guard Settings (single row, id = 1)
    @Query("SELECT * FROM guard_settings WHERE id = 1 LIMIT 1")
    suspend fun getGuardSettingsSnapshot(): GuardSettingsEntity?

    @Query("SELECT * FROM guard_settings WHERE id = 1 LIMIT 1")
    fun getGuardSettingsFlow(): Flow<GuardSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardSettings(settings: GuardSettingsEntity)

    // Slice E (§7.5's REPLACE trap, fixed in the claim's own
    // commit): the settings writers below are targeted UPDATEs —
    // a whole-row REPLACE from a stale read would clobber the
    // receipt claim columns, and the claim's write is targeted
    // for the same reason in reverse.
    @Query("UPDATE guard_settings SET browserBlockerEnabled = :enabled WHERE id = 1")
    suspend fun updateBrowserBlockerEnabled(enabled: Boolean)

    // Truth Car 3 (UNA-01): the sniper switch's targeted UPDATE —
    // same §7.5 discipline as the browser pair above.
    @Query("UPDATE guard_settings SET shortsReelsSniperEnabled = :enabled WHERE id = 1")
    suspend fun updateShortsReelsSniperEnabled(enabled: Boolean)

    @Query("UPDATE guard_settings SET routineReseedV1Done = 1 WHERE id = 1")
    suspend fun markRoutineReseedV1Done()

    @Query("UPDATE guard_settings SET routineReseedNoteDismissed = 1 WHERE id = 1")
    suspend fun markRoutineReseedNoteDismissed()

    @Query(
        "UPDATE guard_settings SET lastCompositeSessionId = :sessionId, " +
            "lastReceiptOutcome = :outcome, receiptSequence = :sequence, " +
            "lastReceiptSessionId = :receiptSessionId WHERE id = 1"
    )
    suspend fun writeReceiptClaim(
        sessionId: Int,
        outcome: String,
        sequence: Int,
        receiptSessionId: Int
    )

    // Targeted stamp writes (routines rebuild): single-column UPDATEs
    // so a stamp can never clobber another settings field written in
    // the same instant (cf. arbitration A6's copy-fix).
    @Query("UPDATE guard_settings SET lastBootAtMillis = :millis WHERE id = 1")
    suspend fun stampLastBoot(millis: Long)

    @Query("UPDATE guard_settings SET lastRoutineTickMillis = :millis WHERE id = 1")
    suspend fun stampLastRoutineTick(millis: Long)

    // Routines (persisted schedule; fired by RoutineAlarmReceiver)
    @Query("SELECT * FROM routines ORDER BY startMinutes ASC")
    fun getAllRoutinesFlow(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines ORDER BY startMinutes ASC")
    suspend fun getAllRoutinesSnapshot(): List<RoutineEntity>

    @Query("SELECT * FROM routines WHERE id = :id LIMIT 1")
    suspend fun getRoutineById(id: Int): RoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<RoutineEntity>)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    // Routines rebuild — the derive() input set (Sten §1.2): every
    // routine session whose window ended within the lookback, plus
    // every unfinalised routine session regardless of age.
    @Query(
        "SELECT * FROM focus_sessions WHERE routineId IS NOT NULL AND " +
            "(windowEndMillis >= :sinceMillis OR (isActive = 0 AND routineOutcome IS NULL))"
    )
    suspend fun getRoutineSessionsForDerive(sinceMillis: Long): List<FocusSessionEntity>

    // Sessions of any kind started since [sinceMillis] — derive()'s
    // overlap evidence (§1.5/§1.6 J3: a window a manual session
    // occupied can never be claimed late).
    @Query("SELECT * FROM focus_sessions WHERE startWallTimeMillis >= :sinceMillis")
    suspend fun getSessionsStartedSince(sinceMillis: Long): List<FocusSessionEntity>

    // Finalise's evidence reads (Sten §1.6.1): intercept instants are
    // associated by timestamp interval ∩ the one active session's
    // span (InterceptLogEntity carries no sessionId).
    @Query("SELECT * FROM intercept_logs WHERE timestampMillis >= :fromMillis AND timestampMillis <= :toMillis ORDER BY timestampMillis ASC")
    suspend fun getInterceptLogsBetween(fromMillis: Long, toMillis: Long): List<InterceptLogEntity>

    @Query("SELECT * FROM focus_tasks WHERE completedInSessionId = :sessionId")
    suspend fun getTasksCompletedInSession(sessionId: Int): List<FocusTaskEntity>

    // Day notes (F-SCH-1): declared day words (REST / ILL /
    // FAMILY — never TEST), drained from the prefs capture into
    // Room at the 8→9 window. One row per date, ever: the date
    // is the primary key and a re-declaration replaces the word.
    @Query("SELECT * FROM day_notes ORDER BY dateIso ASC")
    fun getAllDayNotesFlow(): Flow<List<DayNoteEntity>>

    @Query("SELECT * FROM day_notes ORDER BY dateIso ASC")
    suspend fun getAllDayNotesSnapshot(): List<DayNoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDayNotes(notes: List<DayNoteEntity>)
}
