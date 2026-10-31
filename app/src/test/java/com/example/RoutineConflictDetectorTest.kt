package com.example

import com.example.data.RoutineConflictDetector
import com.example.data.RoutineEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Audit R6 #340 F13: save-time routine-conflict detection.
 * Pure weekly-circle overlap: different loadouts + shared minutes
 * = conflict; everything else passes.
 */
class RoutineConflictDetectorTest {

    private fun routine(
        id: Int,
        name: String = "R$id",
        start: Int,
        end: Int,
        days: Int = 0b1111111,
        loadout: String = "STUDY",
        enabled: Boolean = true
    ) = RoutineEntity(
        id = id,
        name = name,
        startMinutes = start,
        endMinutes = end,
        daysMask = days,
        loadoutName = loadout,
        enabled = enabled
    )

    @Test
    fun `overlapping windows with different loadouts conflict`() {
        val a = routine(1, start = 540, end = 780, loadout = "STUDY")
        val b = routine(2, start = 720, end = 900, loadout = "SLEEP")
        val conflicts = RoutineConflictDetector.conflictsFor(b, listOf(a))
        assertEquals(1, conflicts.size)
        // 60 shared minutes (12:00-13:00) on all 7 days.
        assertEquals(420, conflicts[0].overlapMinutesPerWeek)
        assertEquals("R1", conflicts[0].otherName)
    }

    @Test
    fun `same loadout overlap is not a conflict`() {
        val a = routine(1, start = 540, end = 780, loadout = "STUDY")
        val b = routine(2, start = 720, end = 900, loadout = "STUDY")
        assertTrue(RoutineConflictDetector.conflictsFor(b, listOf(a)).isEmpty())
    }

    @Test
    fun `disjoint days never conflict`() {
        val a = routine(1, start = 540, end = 780, days = 0b0000001, loadout = "STUDY")
        val b = routine(2, start = 540, end = 780, days = 0b0000010, loadout = "SLEEP")
        assertTrue(RoutineConflictDetector.conflictsFor(b, listOf(a)).isEmpty())
    }

    @Test
    fun `overnight window wraps across midnight`() {
        // 22:00-02:00 SLEEP vs 01:00-03:00 STUDY: share 01:00-02:00 daily.
        val night = routine(1, start = 1320, end = 120, loadout = "SLEEP")
        val early = routine(2, start = 60, end = 180, loadout = "STUDY")
        val conflicts = RoutineConflictDetector.conflictsFor(early, listOf(night))
        assertEquals(1, conflicts.size)
        assertEquals(420, conflicts[0].overlapMinutesPerWeek)
    }

    @Test
    fun `disabled routines and self are ignored`() {
        val a = routine(1, start = 540, end = 780, loadout = "STUDY")
        val off = routine(2, start = 540, end = 780, loadout = "SLEEP", enabled = false)
        assertTrue(RoutineConflictDetector.conflictsFor(off, listOf(a)).isEmpty())
        assertTrue(RoutineConflictDetector.conflictsFor(a, listOf(a)).isEmpty())
        val disabledOther = routine(3, start = 540, end = 780, loadout = "SLEEP", enabled = false)
        assertTrue(RoutineConflictDetector.conflictsFor(a, listOf(disabledOther)).isEmpty())
    }

    @Test
    fun `touching windows do not overlap`() {
        val a = routine(1, start = 540, end = 720, loadout = "STUDY")
        val b = routine(2, start = 720, end = 900, loadout = "SLEEP")
        assertTrue(RoutineConflictDetector.conflictsFor(b, listOf(a)).isEmpty())
    }
}
