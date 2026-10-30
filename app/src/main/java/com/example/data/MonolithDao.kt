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
    suspend fun setAppTier(packageName: String, isBlocked: Boolean, blockTier: String): Int

    @Query("UPDATE blocked_apps SET activePeekUntilElapsedRealtime = :activeUntilMono, lastPeekEndElapsedRealtime = :peekEndMono WHERE packageName = :packageName")
    suspend fun updatePeekLeaseAndCooldown(packageName: String, activeUntilMono: Long, peekEndMono: Long): Int

    /** The prune (Ordner lane): rows for packages the phone no longer
     *  has leave the table at sync — the catalog is the phone's truth,
     *  not an archive of everything it once held. */
    @Query("DELETE FROM blocked_apps WHERE packageName IN (:packageNames)")
    suspend fun deleteAppsByPackage(packageNames: List<String>): Int

    @Query("UPDATE blocked_apps SET interceptCount = interceptCount + 1 WHERE packageName = :packageName")
    suspend fun incrementInterceptCount(packageName: String): Int

    @Query("UPDATE blocked_apps SET dailyLimitMinutes = :limitMinutes WHERE packageName = :packageName")
    suspend fun updateDailyLimit(packageName: String, limitMinutes: Int): Int

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

    /**
     * Issue #45 (C5): the session covering a given wall instant —
     * the notification capture path decides "was a session active
     * when this notification was POSTED" against sbn.postTime, not
     * against whenever its coroutine happens to be scheduled. A
     * coroutine delayed past a session's end used to read
     * getActiveSessionSnapshot(), find nothing, and silently drop a
     * message that was genuinely captured during the session.
     *
     * The covering predicate, by row shape:
     * - isActive: the session is still running, and it started at
     *   or before the instant — it covers it.
     * - endWallMillis > 0 (the v11 ending stamp): the session's
     *   true span is exactly [startWallTimeMillis, endWallMillis];
     *   the instant must lie inside it.
     * - endWallMillis = 0 (a legacy row ended before the stamp
     *   existed): no exact end survives, so the bound is the
     *   session's own maximum span, priced from its stored fields
     *   the same way FocusSessionEntity.spanTargetSeconds prices
     *   it — totalStudyBlocks × (target + penalty) plus one
     *   600-second Safe Break between blocks. This can only
     *   over-cover a legacy row that ended early; it can never
     *   miss a message posted while that session truly ran,
     *   because no real run exceeds its own span target.
     */
    @Query(
        "SELECT * FROM focus_sessions WHERE startWallTimeMillis <= :instantMillis AND (" +
            "isActive = 1 " +
            "OR (endWallMillis > 0 AND :instantMillis <= endWallMillis) " +
            "OR (endWallMillis = 0 AND :instantMillis <= startWallTimeMillis + " +
            "(totalStudyBlocks * (targetDurationSeconds + penaltySecondsAdded) + " +
            "(totalStudyBlocks - 1) * 600) * 1000)" +
            ") ORDER BY startWallTimeMillis DESC LIMIT 1"
    )
    suspend fun getSessionCoveringInstant(instantMillis: Long): FocusSessionEntity?

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
    suspend fun deleteSessionById(id: Int): Int

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
    suspend fun latchShieldGap(id: Int): Int

    // The beat adoption (Audit R2 #95): a pre-v10 row's first
    // post-upgrade judgement stamps the beat instead of convicting
    // the row from its session start. Guarded on the beat still
    // being 0 so a racing tick's real beat is never overwritten.
    @Query("UPDATE focus_sessions SET shieldLastBeatWallMillis = :millis WHERE id = :id AND shieldLastBeatWallMillis = 0")
    suspend fun adoptShieldBeat(id: Int, millis: Long)

    // The release-fault record (Draht): written once per fault by
    // the finish machine; the retry cadence increments its own
    // counter through the same targeted shape.
    @Query(
        "UPDATE focus_sessions SET releaseFaultShield = 1, releaseFaultedAtWallMillis = :wallMillis WHERE id = :id"
    )
    suspend fun stampReleaseFault(id: Int, wallMillis: Long): Int

    // Issue #30 finding 4: the retry counter's INCREMENT moves
    // by atomic SQL increment, never by an absolute write of a
    // caller-computed total — two triggers racing (alarm + boot
    // + Deck open) could each compute the same "done" from a
    // stale read and a retry would be silently lost. The
    // increment is judged inside SQLite, where the race cannot
    // exist. Returns rows updated (0 = the row is gone).
    @Query("UPDATE focus_sessions SET releaseRetriesDone = releaseRetriesDone + 1 WHERE id = :id")
    suspend fun incrementReleaseRetries(id: Int): Int

    // The terminal stamp (issue #47's STOP_FINAL door): an
    // absolute write BY DESIGN — it assigns the cadence-closed
    // value (CADENCE_OFFSETS_MS.size + 1) whatever the counter
    // currently holds, so racing triggers writing it lose
    // nothing. Kept beside the increment: assignment and
    // increment are different acts, and each site uses its own.
    @Query("UPDATE focus_sessions SET releaseRetriesDone = :done WHERE id = :id")
    suspend fun stampReleaseRetries(id: Int, done: Int): Int

    // Issue #47 (heartbeat-retry F1/F5): the retry scheduler's own
    // read — the newest still-faulted row ONLY, targeted + LIMIT 1,
    // never a full-history scan. The heal write: clearing the fault
    // flag ends the cadence for that row; faultedAt/retriesDone stay
    // on the row as its history (the receipt's truth is untouched).
    @Query(
        "SELECT * FROM focus_sessions WHERE releaseFaultShield = 1 " +
            "AND releaseFaultedAtWallMillis > 0 ORDER BY startWallTimeMillis DESC LIMIT 1"
    )
    suspend fun getFaultedReleaseSession(): FocusSessionEntity?

    @Query("UPDATE focus_sessions SET releaseFaultShield = 0 WHERE id = :id")
    suspend fun clearReleaseFault(id: Int)

    // Issue #47 (heartbeat-retry F5): the heartbeat's cheap
    // 'nothing due' probe — COUNT over exactly the union of the two
    // derive() session reads below. Zero (with no active session)
    // PROVES both reads would return empty, so the pass may derive
    // over an empty list instead of materialising both.
    @Query(
        "SELECT COUNT(*) FROM focus_sessions WHERE " +
            "(routineId IS NOT NULL AND (windowEndMillis >= :sinceMillis " +
            "OR (isActive = 0 AND routineOutcome IS NULL))) " +
            "OR startWallTimeMillis >= :sinceMillis"
    )
    suspend fun countDeriveSessionInputs(sinceMillis: Long): Int

    // The receipt's deflection read (Zettel): intercept instants in
    // the session's own wall window, oldest first — the reduction
    // (30s-per-package dedupe, break-window exclusion) is assembly
    // logic in the repository, not SQL.
    @Query("SELECT * FROM emergency_break_spends WHERE sessionId = :sessionId ORDER BY spentAtWallMillis ASC")
    suspend fun getSpendsForSession(sessionId: Int): List<EmergencyBreakSpendEntity>

    @Query("UPDATE focus_sessions SET isActive = 0 WHERE isActive = 1")
    suspend fun deactivateAllSessions(): Int

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

    // Intercept Logs
    // Issue #30 finding 2: capped at the newest 500. The table
    // grows by a row per intercept, forever, and the uncapped
    // flow re-emitted ALL of it on every insert. 500 is a display
    // bound, not a data bound: rows are never deleted here, and
    // the evidence reads (getInterceptLogsBetween, finalise's
    // interval scan) keep their own uncapped queries.
    @Query("SELECT * FROM intercept_logs ORDER BY timestampMillis DESC LIMIT 500")
    fun getInterceptLogsFlow(): Flow<List<InterceptLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterceptLog(log: InterceptLogEntity)

    // Captured Notifications (Silent Notification Catcher & Persistent Digest)
    // NO LIMIT (issue #138): the live header prints this list's size
    // as the held total, so a capped flow would report the cap as
    // the count and hide the oldest held items. A held item past
    // any cap is still held.
    @Query("SELECT * FROM captured_notifications ORDER BY timestampMillis DESC")
    fun getCapturedNotificationsFlow(): Flow<List<CapturedNotificationEntity>>

    /**
     * One-shot snapshot for the FROZEN digest: the same rows the
     * flow would print, taken once, at the freeze. Deliberately NO
     * LIMIT — a held item past any cap is still held, and a frozen
     * read that silently dropped it would lie by omission.
     */
    // Deliberately UNCAPPED (FrozenDigestGateTest pins this:
    // snapshot_isNotSilentlyCapped): the frozen digest must see
    // everything the vault holds — a silent cap here would make
    // the digest lie by omission. The display Flow above keeps
    // its LIMIT 100. (Issue #30 finding 2 proposed capping this
    // too; the pinned test is the older, deliberate law — the
    // cap was tried and reverted on its evidence.)
    @Query("SELECT * FROM captured_notifications ORDER BY timestampMillis DESC")
    suspend fun getCapturedNotificationsSnapshot(): List<CapturedNotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapturedNotification(notification: CapturedNotificationEntity)

    @Query("DELETE FROM captured_notifications WHERE id = :id")
    suspend fun deleteCapturedNotification(id: Int): Int

    @Query("DELETE FROM captured_notifications")
    suspend fun clearAllCapturedNotifications(): Int

    // Issue #45 (C5) retention: captured bodies are plaintext
    // message content, so the table is bounded — the insert path
    // prunes by age and caps the row count after every insert.
    // The bounds (7 days / newest 200) live in
    // MonolithNotificationListener; these are the two deletes it
    // runs. The trim orders by timestamp then id so that, among
    // rows sharing a postTime, the later inserts survive.
    @Query("DELETE FROM captured_notifications WHERE timestampMillis < :cutoffMillis")
    suspend fun pruneCapturedNotificationsOlderThan(cutoffMillis: Long)

    @Query(
        "DELETE FROM captured_notifications WHERE id NOT IN " +
            "(SELECT id FROM captured_notifications ORDER BY timestampMillis DESC, id DESC LIMIT :keep)"
    )
    suspend fun trimCapturedNotificationsToNewest(keep: Int)

    // Issue #45 (C5): the identity of one stored capture — the
    // package, the title and body AS STORED (senderTitle is the
    // title after the app-name fallback), and the row's
    // timestampMillis, which is the notification's sbn.postTime
    // snapshot. The dedupe-replace path finds the superseded row
    // by this quadruple before deleting it; the removal path
    // deletes by the same quadruple so a recalled message cannot
    // resurrect in the digest.
    @Query(
        "SELECT * FROM captured_notifications WHERE packageName = :packageName " +
            "AND senderTitle = :senderTitle AND messageBody = :messageBody " +
            "AND timestampMillis = :timestampMillis ORDER BY id DESC LIMIT 1"
    )
    suspend fun findCapturedNotificationMatching(
        packageName: String,
        senderTitle: String,
        messageBody: String,
        timestampMillis: Long
    ): CapturedNotificationEntity?

    @Query(
        "DELETE FROM captured_notifications WHERE packageName = :packageName " +
            "AND senderTitle = :senderTitle AND messageBody = :messageBody " +
            "AND timestampMillis = :timestampMillis"
    )
    suspend fun deleteCapturedNotificationMatching(
        packageName: String,
        senderTitle: String,
        messageBody: String,
        timestampMillis: Long
    ): Int

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
    suspend fun deleteTaskById(id: Int): Int

    // =========================================================================
    // THE DRAWER — stored tasks are COMPLETED rows of focus_tasks.
    // There is no archive table and no delete path for stored rows:
    // retention is permanent. Every read is paged (LIMIT + OFFSET,
    // newest first, id tiebreak for a total order) — no read may
    // silently cap the record. Filing day/month follow the 03:30
    // rule (DrawerDay) — computed in Kotlin only; the old SQLite
    // twin disagreed with it on DST days and is dead (Audit R1 #37).
    // =========================================================================

    // The page size is the CALLER's (Truth Car 2, DRW-04): the
    // repository asks for page+1 rows and reads the extra row as
    // the only lawful has-earlier signal — a page that merely
    // came back full proves nothing.
    @Query("SELECT * FROM focus_tasks WHERE status = 'COMPLETED' AND completedAtMillis >= :fromMillis AND completedAtMillis < :toMillis ORDER BY completedAtMillis DESC, id DESC LIMIT :limit OFFSET :offset")
    suspend fun getStoredPage(fromMillis: Long, toMillis: Long, limit: Int, offset: Int): List<FocusTaskEntity>

    // Audit R1 #37: the months index used to be computed in SQL by
    // subtracting 12,600 ELAPSED seconds from the epoch and taking
    // SQLite 'localtime' — a different function from DrawerDay's
    // wall-clock filing rule on DST days (verified divergence at
    // the 2026-11-01 America/New_York fall-back month boundary:
    // the same completion filed under October in Kotlin and
    // November in SQL). There is now ONE filing function: the DAO
    // returns raw completion instants and the repository groups
    // them through DrawerDay.filingYearMonth. No SQL date math
    // remains to disagree with it.
    @Query("SELECT completedAtMillis FROM focus_tasks WHERE status = 'COMPLETED' AND completedAtMillis > 0")
    suspend fun getStoredCompletionInstants(): List<Long>

    // Issue #30 finding 5: ESCAPE '\' on every LIKE — the
    // repository escapes \, % and _ in the bound value, so a
    // search for "50%" or "a_b" matches those characters
    // literally instead of acting as wildcards over the Drawer.
    @Query("SELECT * FROM focus_tasks WHERE status = 'COMPLETED' AND completedAtMillis > 0 AND (title LIKE '%' || :q || '%' ESCAPE '\\' OR categoryTag LIKE '%' || :q || '%' ESCAPE '\\' OR notesOrSubsteps LIKE '%' || :q || '%' ESCAPE '\\') ORDER BY completedAtMillis DESC, id DESC LIMIT :limit OFFSET :offset")
    suspend fun searchStoredTasks(q: String, limit: Int, offset: Int): List<FocusTaskEntity>

    // Truth Car 2 (RC-04): the claimed № stamped on the session
    // row, written only inside the claim transaction — the row
    // becomes the latch the guarantee rests on. Targeted UPDATE,
    // one column, for the same §7.5 reason as the claim's own
    // settings write: nothing else on the row may move.
    @Query("UPDATE focus_sessions SET receiptNumber = :number WHERE id = :sessionId")
    suspend fun stampReceiptNumber(sessionId: Int, number: Int): Int

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
    suspend fun deleteSite(domain: String): Int

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
    suspend fun updateBrowserBlockerEnabled(enabled: Boolean): Int

    // Truth Car 3 (UNA-01): the sniper switch's targeted UPDATE —
    // same §7.5 discipline as the browser pair above.
    @Query("UPDATE guard_settings SET shortsReelsSniperEnabled = :enabled WHERE id = 1")
    suspend fun updateShortsReelsSniperEnabled(enabled: Boolean): Int

    @Query("UPDATE guard_settings SET routineReseedV1Done = 1 WHERE id = 1")
    suspend fun markRoutineReseedV1Done(): Int

    @Query("UPDATE guard_settings SET routineReseedNoteDismissed = 1 WHERE id = 1")
    suspend fun markRoutineReseedNoteDismissed(): Int

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
    ): Int

    // Targeted stamp writes (routines rebuild): single-column UPDATEs
    // so a stamp can never clobber another settings field written in
    // the same instant (cf. arbitration A6's copy-fix).
    @Query("UPDATE guard_settings SET lastBootAtMillis = :millis WHERE id = 1")
    suspend fun stampLastBoot(millis: Long): Int

    @Query("UPDATE guard_settings SET lastRoutineTickMillis = :millis WHERE id = 1")
    suspend fun stampLastRoutineTick(millis: Long): Int

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

    /** Audit R1 #37: clearing a declared word must reach the
     *  drained table too — before this, no DELETE for day_notes
     *  existed anywhere, so a word that had drained from prefs to
     *  Room could never be undeclared (the clear removed only the
     *  already-absent prefs key and the union read resurrected it). */
    @Query("DELETE FROM day_notes WHERE dateIso = :dateIso")
    suspend fun deleteDayNote(dateIso: String)
}
