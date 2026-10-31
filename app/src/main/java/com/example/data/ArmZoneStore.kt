package com.example.data

import android.content.Context

/**
 * The zone a session was ARMED in (issue #152) — captured at the
 * single session-birth funnel (MonolithRepository.startSession)
 * so the receipt can print its ARMED time in the zone where the
 * arming was actually lived, with the offset named, instead of
 * silently re-rendering the instant in whatever zone the phone
 * is in when the slip is read.
 *
 * Stored OUTSIDE Room on purpose, like DayWordStore: the capture
 * must not touch the Room schema. Single-slot by design — only
 * one receipt can stand at a time, and the slot is keyed by the
 * arm instant itself, so a stale slot can never be mistaken for
 * another session's zone (the reader checks the instant matches).
 *
 * Issue #152 residue (reopened): the same store also carries
 * ONE end-zone slot, keyed by the session's end instant and
 * mirroring the arm slot exactly — the zone the session ENDED
 * in, captured at the session-ending write. Before this slot
 * existed, ReceiptSheet printed ENDED from the render-time
 * zone, so a slip re-issued after a landing in a third zone
 * silently re-rendered ENDED in a zone the end was never lived
 * in. No new store, no schema change: one more slot in this
 * file, written and read under the same keyed-by-instant law.
 *
 * The write COMMITS, for the same reason DayWordStore's does:
 * this is a witnessed fact about a session, exactly the class of
 * fact this app exists not to lose to an async flush.
 */
class ArmZoneStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Record that the session armed at [armWallMs] was armed in
     *  [zoneId] (an IANA id, e.g. "Asia/Kolkata"). */
    fun put(armWallMs: Long, zoneId: String) {
        prefs.edit()
            .putLong(KEY_ARM_WALL_MS, armWallMs)
            .putString(KEY_ZONE_ID, zoneId)
            .commit()
    }

    /** The recorded arm zone for the session armed at
     *  [armWallMs], or null when the slot names a different
     *  session (or none was ever recorded — e.g. a session armed
     *  before this capture existed). */
    fun zoneFor(armWallMs: Long): String? {
        if (prefs.getLong(KEY_ARM_WALL_MS, Long.MIN_VALUE) != armWallMs) return null
        return prefs.getString(KEY_ZONE_ID, null)
    }

    /** Record that the session ending at [endWallMs] ended in
     *  [zoneId]. Written at the session-ending write, in the
     *  same act that first stamps the row's endWallMillis. */
    fun putEnd(endWallMs: Long, zoneId: String) {
        prefs.edit()
            .putLong(KEY_END_WALL_MS, endWallMs)
            .putString(KEY_END_ZONE_ID, zoneId)
            .commit()
    }

    /** The recorded end zone for the session that ended at
     *  [endWallMs], or null when the slot names a different
     *  session (or none was ever recorded — e.g. a session that
     *  ended before this capture existed). */
    fun endZoneFor(endWallMs: Long): String? {
        if (prefs.getLong(KEY_END_WALL_MS, Long.MIN_VALUE) != endWallMs) return null
        return prefs.getString(KEY_END_ZONE_ID, null)
    }

    companion object {
        private const val PREFS_NAME = "monolith_arm_zone"
        private const val KEY_ARM_WALL_MS = "arm_wall_ms"
        private const val KEY_ZONE_ID = "zone_id"
        private const val KEY_END_WALL_MS = "end_wall_ms"
        private const val KEY_END_ZONE_ID = "end_zone_id"
    }
}
