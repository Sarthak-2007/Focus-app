package com.example.data

import android.content.Context

/**
 * The undo window's restore copy, persisted (Truth Car 2, G-03).
 *
 * deleteTaskWithUndo books the delete at the tap (correct — the
 * booking is not the problem) and presents it as reversible for
 * five seconds. The reversal's only copy of the row used to live
 * in a StateFlow: process death inside the window destroyed the
 * copy and turned a presented-as-reversible delete permanent.
 * This store keeps the copy on disk, written with commit() — the
 * G-02 standard — before the banner ever shows.
 *
 * The window itself stays wall-clock five seconds from the
 * delete: on relaunch the ViewModel rehydrates the banner only
 * if the window is still open; a snapshot whose window closed
 * while the process was dead is discarded, never resurrected —
 * the delete was booked, and an expired reversal is not revived
 * by a relaunch. The store holds exactly one snapshot (the undo
 * banner is a single slot, like the delete it serves).
 *
 * Every entity column is stored (as primitives; the entity is
 * flat), so the restored row is the deleted row, field for field.
 */
class UndoDeleteStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Persist [task] as the undoable delete taken at
     *  [deletedAtWallMillis]. Synchronous (commit()): the copy
     *  must be durable before the UI presents the delete as
     *  reversible. */
    fun save(task: FocusTaskEntity, deletedAtWallMillis: Long) {
        prefs.edit()
            .putInt(KEY_ID, task.id)
            .putString(KEY_TITLE, task.title)
            .putString(KEY_NOTES_OR_SUBSTEPS, task.notesOrSubsteps)
            .putInt(KEY_ESTIMATED_MINUTES, task.estimatedMinutes)
            .putInt(KEY_ACTUAL_SECONDS_SPENT, task.actualSecondsSpent)
            .putString(KEY_CATEGORY_TAG, task.categoryTag)
            .putInt(KEY_QUEUE_ORDER, task.queueOrder)
            .putString(KEY_STATUS, task.status)
            .putLong(KEY_COMPLETED_AT_MILLIS, task.completedAtMillis)
            .putInt(KEY_COMPLETED_IN_SESSION_ID, task.completedInSessionId)
            .putBoolean(KEY_HAS_STARTED_OFFSET, task.startedAtSessionElapsedSec != null)
            .putInt(KEY_STARTED_OFFSET, task.startedAtSessionElapsedSec ?: 0)
            .putLong(KEY_DELETED_AT, deletedAtWallMillis)
            .commit()
    }

    /** The persisted copy + its deletion instant, or null. */
    fun load(): Pair<FocusTaskEntity, Long>? {
        if (!prefs.contains(KEY_ID)) return null
        val task = FocusTaskEntity(
            id = prefs.getInt(KEY_ID, 0),
            title = prefs.getString(KEY_TITLE, "") ?: "",
            notesOrSubsteps = prefs.getString(KEY_NOTES_OR_SUBSTEPS, "") ?: "",
            estimatedMinutes = prefs.getInt(KEY_ESTIMATED_MINUTES, 0),
            actualSecondsSpent = prefs.getInt(KEY_ACTUAL_SECONDS_SPENT, 0),
            categoryTag = prefs.getString(KEY_CATEGORY_TAG, "Study") ?: "Study",
            queueOrder = prefs.getInt(KEY_QUEUE_ORDER, 0),
            status = prefs.getString(KEY_STATUS, TaskQueueStatus.QUEUED.name)
                ?: TaskQueueStatus.QUEUED.name,
            completedAtMillis = prefs.getLong(KEY_COMPLETED_AT_MILLIS, 0L),
            completedInSessionId = prefs.getInt(KEY_COMPLETED_IN_SESSION_ID, 0),
            startedAtSessionElapsedSec = if (prefs.getBoolean(KEY_HAS_STARTED_OFFSET, false)) {
                prefs.getInt(KEY_STARTED_OFFSET, 0)
            } else {
                null
            }
        )
        return task to prefs.getLong(KEY_DELETED_AT, 0L)
    }

    fun clear() {
        prefs.edit().clear().commit()
    }

    companion object {
        private const val PREFS_NAME = "monolith_undo_delete"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_NOTES_OR_SUBSTEPS = "notesOrSubsteps"
        private const val KEY_ESTIMATED_MINUTES = "estimatedMinutes"
        private const val KEY_ACTUAL_SECONDS_SPENT = "actualSecondsSpent"
        private const val KEY_CATEGORY_TAG = "categoryTag"
        private const val KEY_QUEUE_ORDER = "queueOrder"
        private const val KEY_STATUS = "status"
        private const val KEY_COMPLETED_AT_MILLIS = "completedAtMillis"
        private const val KEY_COMPLETED_IN_SESSION_ID = "completedInSessionId"
        private const val KEY_HAS_STARTED_OFFSET = "hasStartedOffset"
        private const val KEY_STARTED_OFFSET = "startedOffset"
        private const val KEY_DELETED_AT = "deletedAt"

        /** The undo window's length — one value, shared by the
         *  delete path and the rehydration path. */
        const val WINDOW_MILLIS = 5_000L
    }
}
