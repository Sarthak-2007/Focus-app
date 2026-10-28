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

    @Query("UPDATE blocked_apps SET activePeekUntilElapsedRealtime = 0, lastPeekEndElapsedRealtime = :peekEndMono WHERE packageName = :packageName")
    suspend fun updateLastPeekEnd(packageName: String, peekEndMono: Long)

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

    @Query("DELETE FROM focus_profiles WHERE id = :id")
    suspend fun deleteProfile(id: Int)

    // Focus Sessions
    @Query("SELECT * FROM focus_sessions WHERE isActive = 1 ORDER BY id DESC LIMIT 1")
    fun getActiveSessionFlow(): Flow<FocusSessionEntity?>

    @Query("SELECT * FROM focus_sessions WHERE isActive = 1 ORDER BY id DESC LIMIT 1")
    suspend fun getActiveSessionSnapshot(): FocusSessionEntity?

    @Query("SELECT * FROM focus_sessions ORDER BY startWallTimeMillis DESC LIMIT 60")
    fun getSessionHistoryFlow(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions ORDER BY startWallTimeMillis DESC LIMIT 60")
    suspend fun getSessionHistorySnapshot(): List<FocusSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Query("DELETE FROM focus_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Int)

    @Query("UPDATE focus_sessions SET isActive = 0 WHERE isActive = 1")
    suspend fun deactivateAllSessions()

    // Intercept Logs
    @Query("SELECT * FROM intercept_logs ORDER BY timestampMillis DESC LIMIT 80")
    fun getInterceptLogsFlow(): Flow<List<InterceptLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterceptLog(log: InterceptLogEntity)

    // Captured Notifications (Silent Notification Catcher & Persistent Digest)
    @Query("SELECT * FROM captured_notifications ORDER BY timestampMillis DESC LIMIT 100")
    fun getCapturedNotificationsFlow(): Flow<List<CapturedNotificationEntity>>

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

    @Query("DELETE FROM focus_tasks WHERE status = 'COMPLETED'")
    suspend fun clearCompletedTasks()
}
