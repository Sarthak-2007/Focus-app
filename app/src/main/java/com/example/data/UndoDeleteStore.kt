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
 * Audit R6 #341 F68: the store is a STACK (depth [STACK_DEPTH]),
 * not a single slot. A second delete inside the window used to
 * overwrite the first task's only restore copy — the first
 * deletion became permanent with no warning. Now the displaced
 * snapshot is buried, still restorable when the newer undo is
 * spent; only a delete that overflows the stack's depth forfeits
 * the oldest buried copy, and that forfeiture is RECORDED
 * ([forfeitureCount]) instead of silent. The top of the stack
 * keeps the original single-snapshot on-disk format, so the
 * rehydration and expiry paths below read it exactly as before.
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
        // Audit R6 #341 F68: displace, never overwrite. A standing
        // snapshot whose window is still open is buried first; one
        // whose window already closed was already un-restorable
        // and is simply replaced (no forfeiture — nothing was
        // promised anymore).
        val standing = load()
        if (standing != null) {
            val remaining = remainingMillis(
                standing.second, standing.third,
                deletedAtWallMillis, deletedAtElapsedMillis
            )
            if (remaining > 0L) bury(standing)
        }
        return prefs.edit()
            .putInt(KEY_ID, task.id)
            .putString(KEY_NOTE_TEXT, task.noteText)
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
            noteText = prefs.getString(KEY_NOTE_TEXT, "") ?: "",
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

    // ---- Audit R6 #341 F68: the buried stack ----

    /** One buried snapshot, oldest first. */
    fun buriedSnapshots(): List<Triple<FocusTaskEntity, Long, Long>> =
        decodeStack(prefs.getString(KEY_STACK, null))

    /** How many buried copies have been forfeited to stack
     *  overflow — recorded, never silent (0 on a fresh store). */
    fun forfeitureCount(): Int = prefs.getInt(KEY_FORFEITED, 0)

    /** Spend the top snapshot (an undo landed): promote the
     *  newest buried snapshot into the top slot and return it,
     *  or clear the store when nothing is buried. */
    fun popTopAndPromote(): Triple<FocusTaskEntity, Long, Long>? {
        val buried = buriedSnapshots().toMutableList()
        if (buried.isEmpty()) {
            clear()
            return null
        }
        val next = buried.removeAt(buried.lastIndex)
        prefs.edit().putString(KEY_STACK, encodeStack(buried)).commit()
        writeTop(next)
        return next
    }

    private fun bury(snapshot: Triple<FocusTaskEntity, Long, Long>) {
        val buried = buriedSnapshots().toMutableList()
        buried.add(snapshot)
        var forfeited = forfeitureCount()
        while (buried.size > STACK_DEPTH) {
            buried.removeAt(0)
            forfeited++
        }
        prefs.edit()
            .putString(KEY_STACK, encodeStack(buried))
            .putInt(KEY_FORFEITED, forfeited)
            .commit()
    }

    private fun writeTop(snapshot: Triple<FocusTaskEntity, Long, Long>) {
        val (task, wall, elapsed) = snapshot
        prefs.edit()
            .putInt(KEY_ID, task.id)
            .putString(KEY_TITLE, task.title)
            .putString(KEY_NOTE_TEXT, task.noteText)
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
            .putLong(KEY_DELETED_AT, wall)
            .putLong(KEY_DELETED_AT_ELAPSED, elapsed)
            .commit()
    }

    private fun encodeStack(entries: List<Triple<FocusTaskEntity, Long, Long>>): String =
        entries.joinToString(STACK_SEP) { (task, wall, elapsed) ->
            listOf(
                task.id.toString(), enc(task.title), enc(task.noteText),
                enc(task.notesOrSubsteps), task.estimatedMinutes.toString(),
                task.actualSecondsSpent.toString(), enc(task.categoryTag),
                task.queueOrder.toString(), enc(task.status),
                task.completedAtMillis.toString(), task.completedInSessionId.toString(),
                (task.startedAtSessionElapsedSec != null).toString(),
                (task.startedAtSessionElapsedSec ?: 0).toString(),
                wall.toString(), elapsed.toString()
            ).joinToString(FIELD_SEP)
        }

    private fun decodeStack(raw: String?): List<Triple<FocusTaskEntity, Long, Long>> {
        if (raw.isNullOrEmpty()) return emptyList()
        return raw.split(STACK_SEP).mapNotNull { entry ->
            val f = entry.split(FIELD_SEP)
            if (f.size != 15) return@mapNotNull null
            runCatching {
                Triple(
                    FocusTaskEntity(
                        id = f[0].toInt(), title = dec(f[1]), noteText = dec(f[2]),
                        notesOrSubsteps = dec(f[3]), estimatedMinutes = f[4].toInt(),
                        actualSecondsSpent = f[5].toInt(), categoryTag = dec(f[6]),
                        queueOrder = f[7].toInt(), status = dec(f[8]),
                        completedAtMillis = f[9].toLong(), completedInSessionId = f[10].toInt(),
                        startedAtSessionElapsedSec = if (f[11].toBoolean()) f[12].toInt() else null
                    ),
                    f[13].toLong(), f[14].toLong()
                )
            }.getOrNull()
        }
    }

    private fun enc(value: String): String =
        android.util.Base64.encodeToString(value.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)

    private fun dec(value: String): String =
        String(android.util.Base64.decode(value, android.util.Base64.NO_WRAP), Charsets.UTF_8)

    /** True while a restore copy is on disk (issue #83). */
    fun hasPending(): Boolean = prefs.contains(KEY_ID)

    companion object {
        private const val PREFS_NAME = "monolith_undo_delete"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_NOTE_TEXT = "noteText"
        private const val KEY_STACK = "stackV1"
        private const val KEY_FORFEITED = "forfeitedCount"
        private const val STACK_SEP = "\u0001"
        private const val FIELD_SEP = "\u0002"

        /** Buried snapshots kept under the live top (F68). */
        const val STACK_DEPTH = 3
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
            // F68: buried copies die with their own windows too —
            // an expired buried snapshot is dropped, never revived
            // by a later promotion.
            val nowWall = System.currentTimeMillis()
            val nowElapsed = SystemClock.elapsedRealtime()
            val liveBuried = store.buriedSnapshots().filter {
                remainingMillis(it.second, it.third, nowWall, nowElapsed) > 0L
            }
            if (liveBuried.size != store.buriedSnapshots().size) {
                if (!store.hasPending() && liveBuried.isEmpty()) {
                    store.clear()
                    return
                }
                store.prefs.edit()
                    .putString(KEY_STACK, store.encodeStack(liveBuried))
                    .commit()
            }
            if (!store.hasPending()) return
            val loaded = store.load() ?: return
            val remaining = remainingMillis(
                deletedAtWallMillis = loaded.second,
                deletedAtElapsedMillis = loaded.third,
                nowWallMillis = nowWall,
                nowElapsedMillis = nowElapsed
            )
            if (remaining <= 0L) {
                // Promote a still-live buried snapshot if one
                // stands; otherwise the store empties.
                if (liveBuried.isEmpty()) store.clear() else store.popTopAndPromote()
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
