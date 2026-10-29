package com.example

import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.FocusProfileEntity
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.data.MonolithRepository
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.TaskQueueStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #32 (models slice) — the entity invariants, pinned.
 *
 * The cure's shape is the trap-avoidance: NO init{}/require()
 * anywhere, because Room loads legacy rows through these same
 * constructors and a throwing init would crash on stored data.
 * Validation lives at write funnels (sanitized()/normalized())
 * and coercion at derivation points — both pinned here as pure
 * logic. Constructing garbage rows must NEVER throw; that is
 * itself a test below.
 */
class ModelsInvariantsWave3Test {

    // ---- F1: estimatedMinutes default -------------------------------

    @Test
    fun task_defaultEstimate_isZero_noEstimateGiven() {
        val t = FocusTaskEntity(title = "x")
        assertEquals(0, t.estimatedMinutes)
    }

    // ---- F3: stringly-typed resolvers -------------------------------

    @Test
    fun task_corruptStatus_resolvesDeferred_andIsMarked() {
        val t = FocusTaskEntity(title = "x", status = "GARBAGE")
        assertTrue(t.statusIsCorrupt)
        assertEquals(TaskQueueStatus.DEFERRED, t.resolvedStatus)
    }

    @Test
    fun task_validStatus_resolvesItself_andIsNotMarked() {
        val t = FocusTaskEntity(title = "x", status = "COMPLETED")
        assertFalse(t.statusIsCorrupt)
        assertEquals(TaskQueueStatus.COMPLETED, t.resolvedStatus)
    }

    @Test
    fun app_garbageTier_withBlockClaim_failsClosedToLocked() {
        val a = BlockedAppEntity(
            packageName = "com.x", appName = "X", category = "SOCIAL",
            isBlocked = true, blockTier = "GARBAGE"
        )
        assertTrue(a.tierIsCorrupt)
        assertEquals(AppBlockTier.LOCKED, a.resolvedTier)
    }

    @Test
    fun app_garbageTier_withoutBlockClaim_resolvesOpen_butMarked() {
        val a = BlockedAppEntity(
            packageName = "com.x", appName = "X", category = "SOCIAL",
            isBlocked = false, blockTier = "GARBAGE"
        )
        assertTrue(a.tierIsCorrupt)
        assertEquals(AppBlockTier.OPEN, a.resolvedTier)
    }

    // ---- F4: BlockedAppEntity dual truth ----------------------------

    @Test
    fun app_divergentCopy_normalizedTiesBothFactsToTheTier() {
        // The audit's exact divergence: copy(blockTier=OPEN) on a
        // blocked row leaves isBlocked=true with tier=OPEN.
        val blocked = BlockedAppEntity(
            packageName = "com.x", appName = "X", category = "SOCIAL",
            isBlocked = true, blockTier = "LOCKED"
        )
        val diverged = blocked.copy(blockTier = "OPEN")
        assertTrue(diverged.tierIsCorrupt)
        val fixed = diverged.normalized()
        assertEquals(AppBlockTier.OPEN, fixed.resolvedTier)
        assertFalse(fixed.isBlocked)
        assertEquals("OPEN", fixed.blockTier)
        assertFalse(fixed.tierIsCorrupt)
    }

    @Test
    fun app_peekTier_normalizedKeepsIsBlockedTrue() {
        val a = BlockedAppEntity(
            packageName = "com.x", appName = "X", category = "CHAT",
            isBlocked = false, blockTier = "PEEK_2M"
        ).normalized()
        assertEquals(AppBlockTier.PEEK_2M, a.resolvedTier)
        assertTrue(a.isBlocked)
    }

    // ---- F2: validation without bricking stored rows ----------------

    @Test
    fun garbageRows_constructWithoutThrowing() {
        // Room's read path: these constructions ARE the load path.
        // If any threw, a legacy DB would brick on open.
        FocusProfileEntity(
            name = "n", codeName = "c", description = "d", defaultMinutes = -5,
            blockedCategoriesCsv = "", scheduleLabel = "", isScheduleEnabled = true,
            startHour = 99, startMinute = -1, endHour = 99, endMinute = 99
        )
        FocusSessionEntity(
            intention = "i", profileName = "p", targetDurationSeconds = -100,
            elapsedSeconds = -100, startWallTimeMillis = 0L, startElapsedRealtimeMillis = 0L,
            totalStudyBlocks = 0, currentBlockIndex = 999,
            isActive = true, completedSuccessfully = false
        )
        RoutineEntity(
            name = "r", startMinutes = -30, endMinutes = 5000, daysMask = -1,
            loadoutName = "STUDY"
        )
    }

    @Test
    fun profile_sanitized_clampsClockFields_keeps24HourEndSentinel() {
        val p = FocusProfileEntity(
            name = "n", codeName = "c", description = "d", defaultMinutes = 0,
            blockedCategoriesCsv = "", scheduleLabel = "", isScheduleEnabled = true,
            startHour = 99, startMinute = -1, endHour = 24, endMinute = 99
        ).sanitized()
        assertEquals(23, p.startHour)
        assertEquals(0, p.startMinute)
        assertEquals(24, p.endHour) // lawful midnight-end sentinel
        assertEquals(59, p.endMinute)
        assertEquals(1, p.defaultMinutes)
    }

    @Test
    fun routine_sanitized_wrapsMinutes_andMasksDaysToSevenBits() {
        val r = RoutineEntity(
            name = "r", startMinutes = -30, endMinutes = 1500, daysMask = -1,
            loadoutName = "STUDY"
        ).sanitized()
        assertEquals(1410, r.startMinutes)
        assertEquals(60, r.endMinutes)
        assertEquals(RoutineSchedule.ALL_DAYS, r.daysMask)
    }

    @Test
    fun routineSchedule_corruptRow_pricesSaneWindow() {
        // daysMask=-1 (all bits) must behave as ALL_DAYS, not crash
        // or vanish; startMinutes out of range wraps.
        val r = RoutineEntity(
            name = "r", startMinutes = 540, endMinutes = 600, daysMask = -1,
            loadoutName = "STUDY", enabled = true
        )
        assertTrue(RoutineSchedule.isLive(r))
        assertEquals(60, RoutineSchedule.durationMinutes(r))
    }

    @Test
    fun session_garbageBlockCounts_spanDerivationsStaySane() {
        val s = FocusSessionEntity(
            intention = "i", profileName = "p", targetDurationSeconds = 1500,
            elapsedSeconds = -50, startWallTimeMillis = 0L, startElapsedRealtimeMillis = 0L,
            totalStudyBlocks = 0, currentBlockIndex = 999,
            isActive = true, completedSuccessfully = false
        )
        assertEquals(1, s.safeTotalBlocks)
        assertEquals(1, s.safeCurrentBlockIndex)
        // 1 block, no breaks: target = 1500, elapsed floored at 0.
        assertEquals(1500, s.spanTargetSeconds)
        assertEquals(0, s.spanElapsedSeconds)
        val clean = s.sanitized()
        assertEquals(1, clean.totalStudyBlocks)
        assertEquals(1, clean.currentBlockIndex)
        assertEquals(0, clean.elapsedSeconds)
    }

    // ---- F5: span target uses the named break constant --------------

    @Test
    fun spanTarget_pricesBreaksAtTheNamedConstant() {
        val s = FocusSessionEntity(
            intention = "i", profileName = "p", targetDurationSeconds = 1500,
            elapsedSeconds = 0, startWallTimeMillis = 0L, startElapsedRealtimeMillis = 0L,
            totalStudyBlocks = 3, currentBlockIndex = 1,
            isActive = true, completedSuccessfully = false
        )
        assertEquals(
            3 * 1500 + 2 * MonolithRepository.SAFE_BREAK_DURATION_SECONDS,
            s.spanTargetSeconds
        )
    }
}
