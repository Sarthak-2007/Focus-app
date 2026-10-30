package com.example.data

import android.content.Context
import android.os.SystemClock

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
 * The window itself is five seconds from the delete, judged on
 * the monotonic clock whenever its stamp survives (wall clock
 * only across a reboot — Audit R1 #41): on relaunch the
 * ViewModel rehydrates the banner only if the window is still
 * open; a snapshot whose window closed while the process was
 * dead is discarded, never resurrected — the delete was booked,
 * and an expired reversal is not revived by a relaunch. The
 * store holds exactly one snapshot (the undo banner is a single
 * slot, like the delete it serves).
 *
 * Every entity column is stored (as primitives; the entity is
 * flat), so the restored row is the deleted row, field for field.
 */
class UndoDeleteStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Persist [task] as the undoable delete taken at
     *  [deletedAtWallMillis] (wall) / [deletedAtElapsedMillis]
     *  (monotonic, same boot). Synchronous (commit()): the copy
     *  must be durable before the UI presents the delete as
     *  reversible.
     *
     *  Audit R1 #41: returns commit()'s verdict. The caller
     *  presents the undo banner ONLY on true — before this, a
     *  failed write left the banner promising a reversal whose
     *  only restore copy did not exist. */
    fun save(
        task: FocusTaskEntity,
        deletedAtWallMillis: Long,
        deletedAtElapsedMillis: Long
    ): Boolean {
        return prefs.edit()
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
            .putLong(KEY_DELETED_AT_ELAPSED, deletedAtElapsedMillis)
            .commit()
    }

    /** The persisted copy + its deletion instants (wall,
     *  monotonic), or null. The monotonic stamp is -1 for copies
     *  written before it existed. */
    fun load(): Triple<FocusTaskEntity, Long, Long>? {
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
        return Triple(
            task,
            prefs.getLong(KEY_DELETED_AT, 0L),
            prefs.getLong(KEY_DELETED_AT_ELAPSED, -1L)
        )
    }

    fun clear() {
        prefs.edit().clear().commit()
    }

    /** True while a restore copy is on disk (issue #83). */
    fun hasPending(): Boolean = prefs.contains(KEY_ID)

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
        private const val KEY_DELETED_AT_ELAPSED = "deletedAtElapsed"

        /** The undo window's length — one value, shared by the
         *  delete path and the rehydration path. */
        const val WINDOW_MILLIS = 5_000L

        /**
         * Issue #83: reap a restore copy whose undo window has
         * closed. The copy is a deleted task's full contents in
         * cleartext prefs; if the app is never relaunched through
         * the ViewModel, nothing ever cleared it and it outlived
         * its five-second window indefinitely. Any process start
         * — ViewModel init, or a boot-started process via the
         * boot receiver — calls this so the copy dies with its
         * window regardless of which door started the process.
         * Staleness is judged by [remainingMillis] — the store's
         * one window law (monotonic when usable, wall fallback).
         */
        fun expireIfStale(context: Context) {
            val store = UndoDeleteStore(context)
            if (!store.hasPending()) return
            val loaded = store.load() ?: return
            val remaining = remainingMillis(
                deletedAtWallMillis = loaded.second,
                deletedAtElapsedMillis = loaded.third,
                nowWallMillis = System.currentTimeMillis(),
                nowElapsedMillis = SystemClock.elapsedRealtime()
            )
            if (remaining <= 0L) {
                store.clear()
            }
        }

        /**
         * The window's remaining milliseconds (Audit R1 #41),
         * judged on the MONOTONIC clock whenever its stamp is
         * usable — same boot, i.e. the stamp exists and does not
         * lie ahead of now. The wall clock is the cross-reboot
         * fallback only: judged by wall alone, a clock rollback
         * resurrected expired undos and a forward jump stole
         * live ones, against this store's own "never
         * resurrected" law.
         */
        fun remainingMillis(
            deletedAtWallMillis: Long,
            deletedAtElapsedMillis: Long,
            nowWallMillis: Long,
            nowElapsedMillis: Long
        ): Long {
            val monoUsable = deletedAtElapsedMillis >= 0L &&
                deletedAtElapsedMillis <= nowElapsedMillis
            val elapsed = if (monoUsable) {
                nowElapsedMillis - deletedAtElapsedMillis
            } else {
                nowWallMillis - deletedAtWallMillis
            }
            return WINDOW_MILLIS - elapsed
        }
    }
}
