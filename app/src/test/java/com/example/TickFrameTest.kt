package com.example

import com.example.data.FocusSessionEntity
import com.example.data.MonolithRepository
import com.example.data.tickFrameFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #90 (complexity F5): characterisation tests for the tick
 * frame extracted out of updateSessionProgressLocked — the beat,
 * penalty, reboot re-anchor and #524 update-absorb judgement
 * every branch of the locked tick writes from. Pinned BEFORE the
 * five-write fan-out split (the named F5 remainder): the beat
 * freezes once the gap latch has seen blindness (#155), the drift
 * penalty unwinds on the first clean tick while a forged-state
 * penalty never does (#82), a reboot moves BOTH anchors to the
 * new frame and re-signs over the new pair (#113 finding 4), and
 * an update landing mid-session banks only the last PROVEN
 * elapsed and re-anchors like a reboot (#524).
 *
 * PIN-SCOPE: every test in this file is PLUMBING (pure-function
 * judgement, no rendering, no posting, no source scan).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TickFrameTest {

    private fun session(
        target: Int = 1500,
        penalty: Int = 0,
        bootCount: Int = 3,
        lastTickMono: Long = 50_000L,
        startMono: Long = 10_000L,
        startWall: Long = 1_700_000_000_000L,
        accumulated: Int = 120,
        elapsed: Int = 300,
        beat: Long = 777_000L,
        gapSeen: Boolean = false,
        hmac: String = "STANDING-SIG"
    ) = FocusSessionEntity(
        intention = "FRAME",
        profileName = "Manual",
        targetDurationSeconds = target,
        elapsedSeconds = elapsed,
        startWallTimeMillis = startWall,
        startElapsedRealtimeMillis = startMono,
        bootCount = bootCount,
        accumulatedBeforeRebootSeconds = accumulated,
        lastTickElapsedRealtimeMillis = lastTickMono,
        hmacSignature = hmac,
        isActive = true,
        completedSuccessfully = false,
        penaltySecondsAdded = penalty,
        shieldLastBeatWallMillis = beat,
        shieldGapSeen = gapSeen
    )

    private fun frame(
        s: FocusSessionEntity,
        stampShieldBeat: Boolean = false,
        beatNowWall: Long = 999_000L,
        nowMono: Long = 60_000L,
        nowWall: Long = 1_700_000_100_000L,
        currentBoot: Int? = 3,
        rawVerifiedElapsed: Int = 300,
        tamper: Boolean = false,
        hmacValid: Boolean = true,
        appLastUpdateMillis: Long = 0L
    ) = tickFrameFor(
        session = s,
        stampShieldBeat = stampShieldBeat,
        beatNowWall = beatNowWall,
        nowMono = nowMono,
        nowWall = nowWall,
        currentBoot = currentBoot,
        rawVerifiedElapsed = rawVerifiedElapsed,
        tamper = tamper,
        hmacValid = hmacValid,
        appLastUpdateMillis = appLastUpdateMillis
    )

    // PIN-SCOPE: PLUMBING
    @Test
    fun cleanTick_noReboot_keepsAnchors() {
        // Issue #36 (ruling 3) — RULING-DRIVEN RE-PIN: this test
        // previously also asserted the frame carries the standing
        // signature (f.newHmac == "STANDING-SIG"). The frame
        // carries NO signature any more: the v2 payload covers the
        // persisted elapsed, which only the writer knows, so every
        // writer signs the exact row it persists. The frame's
        // contract is anchors/penalty/target only.
        val f = frame(session())
        assertEquals(0, f.penaltyAdd)
        assertEquals(1500, f.effectiveTarget)
        assertFalse(f.rebootDetected)
        assertFalse(f.updateDetected)
        assertEquals(300, f.verifiedElapsed)
        assertEquals(120, f.newAccumulated)
        assertEquals(10_000L, f.newStartMono)
        assertEquals(1_700_000_000_000L, f.newStartWall)
        assertEquals(3, f.newBootCount)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun vmTicker_neverStampsBeat() {
        val f = frame(session(), stampShieldBeat = false, beatNowWall = 999_000L)
        assertEquals(777_000L, f.beatWall)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun shieldTick_stampsBeatWhileItCanStillSee() {
        val f = frame(session(gapSeen = false), stampShieldBeat = true, beatNowWall = 999_000L)
        assertEquals(999_000L, f.beatWall)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun shieldTick_beatFreezesOnceGapLatchHasSeenBlindness() {
        // #155 CRITIC-3 Charge C: a latched row never earns a fresh
        // beat again — later blind ticks cannot launder the
        // stale-beat evidence away.
        val f = frame(session(gapSeen = true), stampShieldBeat = true, beatNowWall = 999_000L)
        assertEquals(777_000L, f.beatWall)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun forgedState_firstConviction_booksFullPenalty() {
        val f = frame(session(penalty = 0), hmacValid = false)
        assertEquals(MonolithRepository.HMAC_TAMPER_PENALTY_SECONDS, f.penaltyAdd)
        assertEquals(1500 + MonolithRepository.HMAC_TAMPER_PENALTY_SECONDS, f.effectiveTarget)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun forgedState_penaltySetOnce_neverRebooksOrUnwinds() {
        val f = frame(session(penalty = 450), hmacValid = false)
        assertEquals(450, f.penaltyAdd)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun driftTamper_booksPenaltyWhileDriftPersists() {
        val f = frame(session(penalty = 0), tamper = true, hmacValid = true)
        assertEquals(MonolithRepository.HMAC_TAMPER_PENALTY_SECONDS, f.penaltyAdd)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun cleanTick_unwindsDriftPenalty() {
        // #82: session elapsed is monotonic-anchored, so clock
        // travel alone gains nothing — the drift penalty applies
        // only while the drift persists.
        val f = frame(session(penalty = 900), tamper = false, hmacValid = true)
        assertEquals(0, f.penaltyAdd)
        assertEquals(1500, f.effectiveTarget)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun rebootByBootCount_movesBothAnchors() {
        // Issue #36 (ruling 3) — RULING-DRIVEN RE-PIN: the frame
        // no longer mints the re-sign over the moved pair (it
        // cannot — the v2 payload needs the persisted elapsed).
        // The writer signs the re-anchored row it persists; that
        // act is pinned end-to-end by RebootTruthRepositoryTest
        // (the post-reboot row verifies). The frame's half — both
        // anchors move to the new boot's frame — stands as before.
        val s = session(bootCount = 3)
        val f = frame(s, currentBoot = 4, nowMono = 5_000L, nowWall = 1_700_001_000_000L, rawVerifiedElapsed = 280)
        assertTrue(f.rebootDetected)
        assertEquals(280, f.newAccumulated)
        assertEquals(5_000L, f.newStartMono)
        assertEquals(1_700_001_000_000L, f.newStartWall)
        assertEquals(4, f.newBootCount)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun rebootByMonoRunningBackwards_detectedEvenWithoutBootCount() {
        // The battery-death signature: monotonic time cannot
        // account for the downtime (currentBoot unreadable).
        val s = session(lastTickMono = 50_000L)
        val f = frame(s, currentBoot = null, nowMono = 2_000L)
        assertTrue(f.rebootDetected)
        assertEquals(2_000L, f.newStartMono)
        // Boot count unreadable: the standing count is kept.
        assertEquals(3, f.newBootCount)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun sameBootAndMonoAdvancing_isNotAReboot() {
        val f = frame(session(lastTickMono = 50_000L), currentBoot = 3, nowMono = 60_000L)
        assertFalse(f.rebootDetected)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun updateLanded_banksOnlyProvenElapsedAndReanchorsLikeAReboot() {
        // #524: the raw verified figure (900) books the dead
        // install interval as focus time; the absorb banks the
        // last PROVEN elapsed — max(bank 120, persisted 300) —
        // moves both anchors, keeps the boot count (the writer
        // re-signs the persisted row — issue #36, ruling 3).
        val start = 1_700_000_000_000L
        val s = session(startWall = start, beat = start + 165_000L)
        val f = frame(
            s,
            nowMono = 60_000L,
            nowWall = start + 170_000L,
            rawVerifiedElapsed = 900,
            appLastUpdateMillis = start + 60_000L
        )
        assertFalse(f.rebootDetected)
        assertTrue(f.updateDetected)
        assertEquals(300, f.verifiedElapsed)
        assertEquals(300, f.newAccumulated)
        assertEquals(60_000L, f.newStartMono)
        assertEquals(start + 170_000L, f.newStartWall)
        assertEquals(3, f.newBootCount)
        // Issue #36 (ruling 3): the absorb's re-sign is the
        // writer's act over the persisted row (the frame mints
        // nothing) — see rebootByBootCount_movesBothAnchors.
        // The beat is fresh against the implied now: no latch.
        assertFalse(f.updateGapLatched)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun updateLanded_staleBeat_latchesTheGap() {
        // #524 branding half: judged on the PRE-absorb row, a dead
        // interval past the 10 s threshold latches the gap so the
        // finish books ELAPSED_SHIELD_GAP, not ELAPSED_CLEAN.
        // Implied now = start + bank 120 s + (60 s mono - 10 s
        // start mono) = start + 170 s; the beat sits at start + 5 s.
        val start = 1_700_000_000_000L
        val s = session(startWall = start, beat = start + 5_000L)
        val f = frame(
            s,
            nowMono = 60_000L,
            nowWall = start + 170_000L,
            rawVerifiedElapsed = 900,
            appLastUpdateMillis = start + 60_000L
        )
        assertTrue(f.updateDetected)
        assertTrue(f.updateGapLatched)
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun updateLanded_beforeSessionStart_isNotDetected() {
        // A build that landed before this session started is not
        // an absorb event (self-extinguishing by construction).
        val start = 1_700_000_000_000L
        val f = frame(session(startWall = start), appLastUpdateMillis = start - 1_000L)
        assertFalse(f.updateDetected)
        assertEquals(300, f.verifiedElapsed)
    }
}
