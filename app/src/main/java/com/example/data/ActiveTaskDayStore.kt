package com.example.data

import android.content.Context

/**
 * Which task stood as a filing date's active (NOW) task (R3b #163,
 * morning-after-a-failed-day F5).
 *
 * FocusTaskEntity carries no created-at field, and the Room schema
 * is not this fix's to migrate — so the day boundary is remembered
 * here, in prefs: the first active task the ViewModel sees on a
 * filing date is noted against that date. Tomorrow the Deck can
 * then say honestly that today's NOW task already stood there
 * yesterday — a "FROM YESTERDAY" tag — instead of letting a
 * carried-over task pose as a fresh start chosen this morning.
 *
 * One small record per day, written with COMMIT: a day-boundary
 * fact is exactly the class of fact this app exists not to lose.
 */
class ActiveTaskDayStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun taskIdOn(dateIso: String): Int? {
        val stored = prefs.getInt(dateIso, Int.MIN_VALUE)
        return if (stored == Int.MIN_VALUE) null else stored
    }

    fun note(dateIso: String, taskId: Int) {
        prefs.edit().putInt(dateIso, taskId).commit()
    }

    companion object {
        private const val PREFS_NAME = "monolith_active_task_days"
    }
}
