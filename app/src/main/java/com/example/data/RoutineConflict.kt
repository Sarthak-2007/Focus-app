package com.example.data

/**
 * Audit R6 #340 F13: routine-conflict detection AT SAVE TIME.
 *
 * The pre-cure funnel saved overlapping routines blind: two
 * enabled routines with overlapping windows and different
 * loadouts persisted silently, and RoutineDerive then quietly
 * starved the loser at runtime (OPEN_HELD) — no word to the user
 * at save time or at fire time. AppBlock ships a
 * SchedulingConflictUseCase but parks it on a side explanation
 * screen while still saving the collision; this detector lives
 * in the save path itself, and the save does not land until the
 * collision is resolved by the user's own edit.
 *
 * A conflict is: both routines enabled, at least one shared
 * minute on the weekly circle, and DIFFERENT loadouts. Same-
 * loadout overlaps merge harmlessly in derivation and are not
 * conflicts. Windows that cross midnight are split across the
 * day boundary (and across the week boundary) before comparing,
 * using the same wrap rule as [RoutineSchedule.durationMinutes].
 *
 * Pure and table-testable: no clock, no DAO, no Android types.
 */
data class RoutineConflict(
    val otherRoutineId: Int,
    val otherName: String,
    val otherLoadoutName: String,
    /** Total shared minutes across the whole weekly circle. */
    val overlapMinutesPerWeek: Int,
    /** The first colliding segment, for the printed explanation. */
    val firstOverlapDayBit: Int,
    val firstOverlapStartMinute: Int,
    val firstOverlapEndMinute: Int
)

object RoutineConflictDetector {

    private data class Segment(val dayBit: Int, val startMinute: Int, val endMinute: Int)

    /** Every conflict [candidate] would create against [existing]
     *  (the persisted set, which may include the candidate's own
     *  previous row — matched by id and skipped). */
    fun conflictsFor(
        candidate: RoutineEntity,
        existing: List<RoutineEntity>
    ): List<RoutineConflict> {
        if (!candidate.enabled) return emptyList()
        val candidateSegments = segmentsOf(candidate)
        if (candidateSegments.isEmpty()) return emptyList()
        return existing.mapNotNull { other ->
            if (other.id == candidate.id || !other.enabled) return@mapNotNull null
            if (other.loadoutName == candidate.loadoutName) return@mapNotNull null
            var total = 0
            var first: Triple<Int, Int, Int>? = null
            for (a in candidateSegments) {
                for (b in segmentsOf(other)) {
                    if (a.dayBit != b.dayBit) continue
                    val start = maxOf(a.startMinute, b.startMinute)
                    val end = minOf(a.endMinute, b.endMinute)
                    if (end > start) {
                        total += end - start
                        if (first == null) first = Triple(a.dayBit, start, end)
                    }
                }
            }
            val f = first ?: return@mapNotNull null
            RoutineConflict(
                otherRoutineId = other.id,
                otherName = other.name,
                otherLoadoutName = other.loadoutName,
                overlapMinutesPerWeek = total,
                firstOverlapDayBit = f.first,
                firstOverlapStartMinute = f.second,
                firstOverlapEndMinute = f.third
            )
        }
    }

    /** The candidate's windows as (day, start, end) segments on
     *  the weekly circle, midnight- and week-wrapping split. */
    private fun segmentsOf(routine: RoutineEntity): List<Segment> {
        val r = routine.sanitized()
        if (r.daysMask == 0) return emptyList()
        var span = r.endMinutes - r.startMinutes
        if (span <= 0) span += RoutineSchedule.DAY_MINUTES
        val out = ArrayList<Segment>()
        for (day in 0..6) {
            if (r.daysMask and (1 shl day) == 0) continue
            val end = r.startMinutes + span
            if (end <= RoutineSchedule.DAY_MINUTES) {
                out += Segment(day, r.startMinutes, end)
            } else {
                out += Segment(day, r.startMinutes, RoutineSchedule.DAY_MINUTES)
                out += Segment((day + 1) % 7, 0, end - RoutineSchedule.DAY_MINUTES)
            }
        }
        return out
    }
}
