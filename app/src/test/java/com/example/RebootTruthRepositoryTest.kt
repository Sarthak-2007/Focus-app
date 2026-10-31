package com.example

import android.content.Context
import android.os.SystemClock
import android.provider.Settings
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CryptoIntegrityManager
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.SessionOutcome
import com.example.data.ShieldRaiseStore
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * PIN scope: SOURCE — pins the verified-elapsed derivation and
 * the reboot booking at the repository seam.
 *
 * Audit R3 #113, findings 4 + 5 — the reboot's two lies, proven
 * against the repository's own machinery, in the form the surge
 * merge composed them (STURM3-11 port):
 *
 *  - Finding 4 (this branch): the re-anchor moved the mono anchor
 *    and left the wall anchor at the session's start, so every
 *    later tick's drift read permanently > 90s, tamper stuck, and
 *    the 15-minute penalty stretched the session. Both anchors
 *    now move; an honest reboot draws NO penalty, on the
 *    re-anchor tick or any after.
 *  - Finding 5, as composed with main's later Issue #81 F3 law:
 *    an UNWITNESSED boot span — a boot-count delta of 2+ against
 *    the row's anchor (Safe Mode, a force-stop spanning a
 *    reboot: Monolith never ran, so the span is unproven) —
 *    convicts as tamper and draws the penalty. What finding 5
 *    killed stands dead: unproven time never passes as a free,
 *    invisible freeze.
 *  - Issue #525 (the correction of #81's crediting half): the
 *    first post-reboot tick credits ONLY the span the current
 *    boot's shield actually witnessed — from its first
 *    protecting flip (ShieldRaiseStore's stamp) to the tick.
 *    #81's doctrine that "the shield stands from BOOT_COMPLETED"
 *    was false in the code: the shield stands from the flip,
 *    and the uptime before it ran unshielded. That span is now
 *    BOOKED on the row (rebootGapSeconds), latches the shield
 *    gap past the 10s law, and prints on the receipt — never
 *    credited. With no stamp standing, the tick credits
 *    nothing past the banked figure.
 *
 * And the security side of the same coin, unmoved: a wall clock
 * jumped inside one boot segment, and a forged HMAC, still read
 * as tamper and still draw the penalty.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RebootTruthRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private lateinit var repo: MonolithRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = MonolithRepository(context, db.monolithDao())
        // No shield-raise stamp may leak between scenarios.
        ShieldRaiseStore.clear(context)
        // Push the virtual mono clock far enough that session
        // anchors hours in the past stay positive.
        ShadowSystemClock.advanceBy(Duration.ofSeconds(7_200))
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun sign(
        startWall: Long,
        startMono: Long,
        boot: Int,
        target: Int,
        stopwatch: Boolean = false
    ): String = CryptoIntegrityManager.signSessionState(
        startWallTimeMillis = startWall,
        startElapsedRealtimeMillis = startMono,
        bootCount = boot,
        targetDurationSeconds = target,
        isStopwatch = stopwatch
    )

    private fun sessionRow(
        startWall: Long,
        startMono: Long,
        boot: Int,
        target: Int = 3_600,
        elapsed: Int = 0,
        accumulated: Int = 0,
        lastTickMono: Long,
        hmac: String
    ): FocusSessionEntity = FocusSessionEntity(
        intention = "Deep Study",
        profileName = "Default",
        targetDurationSeconds = target,
        elapsedSeconds = elapsed,
        startWallTimeMillis = startWall,
        startElapsedRealtimeMillis = startMono,
        bootCount = boot,
        accumulatedBeforeRebootSeconds = accumulated,
        lastTickElapsedRealtimeMillis = lastTickMono,
        hmacSignature = hmac,
        isActive = true,
        completedSuccessfully = false
    )

    private suspend fun insert(row: FocusSessionEntity): FocusSessionEntity {
        val id = db.monolithDao().insertSession(row).toInt()
        return row.copy(id = id)
    }

    private fun setMachineBootCount(count: Int): Int {
        Settings.Global.putInt(context.contentResolver, Settings.Global.BOOT_COUNT, count)
        val read = repo.querySystemBootCount()
        assertEquals("the test must control the machine boot count", count, read)
        return read
    }

    /**
     * Stamp [boot]'s first shield raise at [monoMillis] — the
     * write MonolithShieldService performs at its first
     * protecting flip — and prove the stamp stands for exactly
     * that boot before the scenario reads it.
     */
    private fun stampShieldRaise(boot: Int, monoMillis: Long) {
        setMachineBootCount(boot)
        ShieldRaiseStore.stampFirstRaise(context, monoMillis)
        assertEquals(
            "the raise stamp must stand for the boot it was written in",
            monoMillis,
            ShieldRaiseStore.raiseMonoForBoot(context, boot)
        )
    }

    /**
     * The honest reboot, end to end: a session 3,490 seconds in
     * when the phone dies; the phone comes back on the NEXT boot
     * (delta 1 — witnessed). The shield's first raise lands 300
     * seconds before the first tick: those 300 are the ONLY
     * post-reboot seconds credited (#525). The uptime before the
     * raise — ~6,900 s in this rig — ran unshielded: it is
     * BOOKED as the session's reboot gap, and past the 10s law
     * it latches the shield gap. No tick may call the man a
     * tamperer — the both-anchors re-anchor (finding 4) keeps
     * the drift test segment-local, so the formerly false-tamper
     * next tick stays clean.
     */
    @Test
    fun honestRebootCreditsOnlyTheWitnessedSpanAndDrawsNoPenalty() = runBlocking {
        val machineBoot = setMachineBootCount(10)
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        val banked = 3_490
        val witnessedSec = 300
        val raiseMono = monoNow - witnessedSec * 1_000L
        val gapSec = (raiseMono / 1_000L).toInt()
        stampShieldRaise(machineBoot, raiseMono)
        val startWall = wallNow - 3_500_000L
        val startMono = monoNow - 3_500_000L
        val anchoredBoot = machineBoot - 1
        // The target is derived from the witnessed figure itself
        // (banked + 300 + headroom): this scenario is a MID-SESSION
        // resume, and deriving the target keeps it one regardless
        // of how much virtual uptime the rig has lived through
        // (floor lane census #6, row 15's lesson, kept).
        val target = banked + witnessedSec + 600
        val row = insert(
            sessionRow(
                startWall = startWall,
                startMono = startMono,
                boot = anchoredBoot,
                target = target,
                elapsed = banked,
                lastTickMono = monoNow - 10_000L,
                hmac = sign(startWall, startMono, anchoredBoot, target)
            )
        )

        // #525 at the pure seam: banked + the witnessed span
        // ONLY — never the boot's whole uptime; no tamper.
        val (verified, tamper) = repo.computeVerifiedSessionElapsed(row)
        assertEquals(banked + witnessedSec, verified)
        assertFalse(tamper)

        // The re-anchor tick: the witnessed figure stands, no
        // penalty, BOTH anchors move to the new boot's frame,
        // the re-sign covers the new pair — and the unshielded
        // span is booked on the row, latch set.
        val afterReboot = repo.updateSessionProgress(row)
        assertEquals(banked + witnessedSec, afterReboot.elapsedSeconds)
        assertFalse(afterReboot.tamperDetected)
        assertEquals(0, afterReboot.penaltySecondsAdded)
        assertEquals(machineBoot, afterReboot.bootCount)
        assertTrue(afterReboot.startWallTimeMillis > startWall + 3_000_000L)
        assertTrue(CryptoIntegrityManager.verifySessionState(afterReboot))
        assertEquals(gapSec, afterReboot.rebootGapSeconds)
        assertTrue(
            "a ~${gapSec}s unshielded boot span must latch the gap",
            afterReboot.shieldGapSeen
        )

        // Finding 4's formerly false-tamper tick: with both
        // anchors in one frame the drift is the tick's own 30
        // seconds, the count advances by exactly those 30, the
        // penalty never lands — and the booked gap neither
        // grows (same boot: no new crossing) nor clears.
        ShadowSystemClock.advanceBy(Duration.ofSeconds(30))
        val nextTick = repo.updateSessionProgress(afterReboot)
        assertEquals(banked + witnessedSec + 30, nextTick.elapsedSeconds)
        assertFalse(nextTick.tamperDetected)
        assertEquals(0, nextTick.penaltySecondsAdded)
        assertEquals(gapSec, nextTick.rebootGapSeconds)
        assertTrue(nextTick.shieldGapSeen)
    }

    /**
     * Issue #525's core regression pin: a reboot the shield
     * NEVER raised in — no stamp stands for the new boot (the
     * service never flipped, or the stamp was lost). The tick
     * credits NOTHING past the banked figure: the whole uptime
     * is unwitnessed, however long the phone sat on. The honest
     * delta-1 reboot still draws no penalty (the span is a gap,
     * not a forgery), but it books the full uptime as the
     * reboot gap and latches the shield gap.
     */
    @Test
    fun rebootWithNoShieldRaiseCreditsNothingPastTheBank() = runBlocking {
        val machineBoot = setMachineBootCount(10)
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        val uptimeSec = (monoNow / 1_000L).toInt()
        val banked = 3_490
        val startWall = wallNow - 3_500_000L
        val startMono = monoNow - 3_500_000L
        val anchoredBoot = machineBoot - 1
        val target = banked + 600
        val row = insert(
            sessionRow(
                startWall = startWall,
                startMono = startMono,
                boot = anchoredBoot,
                target = target,
                elapsed = banked,
                lastTickMono = monoNow - 10_000L,
                hmac = sign(startWall, startMono, anchoredBoot, target)
            )
        )
        assertNull(ShieldRaiseStore.raiseMonoForBoot(context, machineBoot))

        val (verified, tamper) = repo.computeVerifiedSessionElapsed(row)
        assertEquals(
            "no raise stamp: the gifted-uptime figure must be dead",
            banked, verified
        )
        assertFalse(tamper)

        val afterReboot = repo.updateSessionProgress(row)
        assertEquals(banked, afterReboot.elapsedSeconds)
        assertFalse(afterReboot.tamperDetected)
        assertEquals(0, afterReboot.penaltySecondsAdded)
        assertEquals(machineBoot, afterReboot.bootCount)
        assertEquals(uptimeSec, afterReboot.rebootGapSeconds)
        assertTrue(afterReboot.shieldGapSeen)
    }

    /**
     * Issue #525's fast path: the shield raised 8 seconds after
     * power-on — inside the 10s gap law. The witnessed span is
     * nearly the whole uptime and IS credited; the 8 unshielded
     * seconds are booked on the row but latch nothing.
     */
    @Test
    fun fastShieldRaiseBooksTheSmallGapAndLatchesNothing() = runBlocking {
        val machineBoot = setMachineBootCount(10)
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        val banked = 3_490
        val raiseMono = 8_000L
        val witnessedSec = ((monoNow - raiseMono) / 1_000L).toInt()
        stampShieldRaise(machineBoot, raiseMono)
        val startWall = wallNow - 3_500_000L
        val startMono = monoNow - 3_500_000L
        val anchoredBoot = machineBoot - 1
        val target = banked + witnessedSec + 600
        val row = insert(
            sessionRow(
                startWall = startWall,
                startMono = startMono,
                boot = anchoredBoot,
                target = target,
                elapsed = banked,
                lastTickMono = monoNow - 10_000L,
                hmac = sign(startWall, startMono, anchoredBoot, target)
            )
        )

        val (verified, tamper) = repo.computeVerifiedSessionElapsed(row)
        assertEquals(banked + witnessedSec, verified)
        assertFalse(tamper)

        val afterReboot = repo.updateSessionProgress(row)
        assertEquals(banked + witnessedSec, afterReboot.elapsedSeconds)
        assertEquals(0, afterReboot.penaltySecondsAdded)
        assertEquals(8, afterReboot.rebootGapSeconds)
        assertFalse(
            "an 8s boot-to-shield span is inside the 10s law: no latch",
            afterReboot.shieldGapSeen
        )
    }

    /**
     * Issue #525 at the finish line: the session crosses its
     * target ON the reboot tick itself (banked + the witnessed
     * 300 = the target exactly), after a ~6,900 s unshielded
     * boot span. The ending write must carry the booking with
     * it — gap on the row, outcome ELAPSED_SHIELD_GAP, never
     * ELAPSED_CLEAN for time nobody watched.
     */
    @Test
    fun finishOnTheRebootTickBooksTheGapAndCannotBookClean() = runBlocking {
        val machineBoot = setMachineBootCount(10)
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        val banked = 3_490
        val witnessedSec = 300
        val raiseMono = monoNow - witnessedSec * 1_000L
        val gapSec = (raiseMono / 1_000L).toInt()
        stampShieldRaise(machineBoot, raiseMono)
        val startWall = wallNow - 3_500_000L
        val startMono = monoNow - 3_500_000L
        val anchoredBoot = machineBoot - 1
        val target = banked + witnessedSec
        val row = insert(
            sessionRow(
                startWall = startWall,
                startMono = startMono,
                boot = anchoredBoot,
                target = target,
                elapsed = banked,
                lastTickMono = monoNow - 10_000L,
                hmac = sign(startWall, startMono, anchoredBoot, target)
            )
        )

        val ended = repo.updateSessionProgress(row)
        assertFalse(ended.isActive)
        assertEquals(target, ended.elapsedSeconds)
        assertEquals(0, ended.penaltySecondsAdded)
        assertEquals(gapSec, ended.rebootGapSeconds)
        assertTrue(ended.shieldGapSeen)
        assertEquals(SessionOutcome.ELAPSED_SHIELD_GAP, ended.outcome)
    }

    /**
     * Finding 5's abuse case in its composed (#81 F3) form: the
     * row's anchor is SEVEN boots behind the machine — the span
     * crossed boots Monolith never witnessed (Safe Mode, a
     * force-stop spanning reboots). The figure books only the
     * current boot's witnessed span (#525), the unshielded
     * remainder is booked as the gap, and the span convicts:
     * tamper at the seam, and the penalty on the tick. Unproven
     * time is not a free, invisible freeze.
     */
    @Test
    fun unwitnessedBootSpanStillConvicts() = runBlocking {
        val machineBoot = setMachineBootCount(10)
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        val banked = 3_490
        val witnessedSec = 300
        val raiseMono = monoNow - witnessedSec * 1_000L
        val gapSec = (raiseMono / 1_000L).toInt()
        stampShieldRaise(machineBoot, raiseMono)
        val startWall = wallNow - 3_500_000L
        val startMono = monoNow - 3_500_000L
        val anchoredBoot = machineBoot - 7
        val row = insert(
            sessionRow(
                startWall = startWall,
                startMono = startMono,
                boot = anchoredBoot,
                elapsed = banked,
                lastTickMono = monoNow - 10_000L,
                hmac = sign(startWall, startMono, anchoredBoot, 3_600)
            )
        )

        val (verified, tamper) = repo.computeVerifiedSessionElapsed(row)
        assertEquals(banked + witnessedSec, verified)
        assertTrue("an unwitnessed boot span must read as tamper", tamper)

        val ticked = repo.updateSessionProgress(row)
        assertTrue(ticked.tamperDetected)
        assertEquals(
            MonolithRepository.HMAC_TAMPER_PENALTY_SECONDS,
            ticked.penaltySecondsAdded
        )
        assertEquals(banked + witnessedSec, ticked.elapsedSeconds)
        assertEquals(gapSec, ticked.rebootGapSeconds)
        assertTrue(ticked.shieldGapSeen)
    }

    /**
     * The security the fix must NOT spend: inside one boot
     * segment, a wall clock dragged two hours ahead of the mono
     * clock is still tamper, and still draws the 15-minute
     * penalty.
     */
    @Test
    fun wallClockJumpInsideOneBootStillDrawsThePenalty() = runBlocking {
        val machineBoot = repo.querySystemBootCount()
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        // Mono says 600s of session; the wall clock claims 7,800s.
        val startWall = wallNow - 7_800_000L
        val startMono = monoNow - 600_000L
        val row = insert(
            sessionRow(
                startWall = startWall,
                startMono = startMono,
                boot = machineBoot,
                elapsed = 600,
                lastTickMono = monoNow - 5_000L,
                hmac = sign(startWall, startMono, machineBoot, 3_600)
            )
        )

        val (verified, tamper) = repo.computeVerifiedSessionElapsed(row)
        assertEquals(600, verified)
        assertTrue(tamper)

        val ticked = repo.updateSessionProgress(row)
        assertTrue(ticked.tamperDetected)
        assertEquals(
            MonolithRepository.HMAC_TAMPER_PENALTY_SECONDS,
            ticked.penaltySecondsAdded
        )
    }

    /** A forged signature is still tamper, reboot or not. */
    @Test
    fun forgedSignatureStillReadsAsTamper() = runBlocking {
        val machineBoot = repo.querySystemBootCount()
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        val row = insert(
            sessionRow(
                startWall = wallNow - 600_000L,
                startMono = monoNow - 600_000L,
                boot = machineBoot,
                elapsed = 600,
                lastTickMono = monoNow - 1_000L,
                hmac = "forged"
            )
        )
        val (verified, tamper) = repo.computeVerifiedSessionElapsed(row)
        assertEquals(600, verified)
        assertTrue(tamper)
    }

    /** The plain honest tick, untouched by any of this. */
    @Test
    fun honestTickInsideOneBootVerifiesClean() = runBlocking {
        val machineBoot = repo.querySystemBootCount()
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        val startWall = wallNow - 600_000L
        val startMono = monoNow - 600_000L
        val row = insert(
            sessionRow(
                startWall = startWall,
                startMono = startMono,
                boot = machineBoot,
                elapsed = 595,
                lastTickMono = monoNow - 5_000L,
                hmac = sign(startWall, startMono, machineBoot, 3_600)
            )
        )
        val (verified, tamper) = repo.computeVerifiedSessionElapsed(row)
        assertEquals(600, verified)
        assertFalse(tamper)
    }
}
