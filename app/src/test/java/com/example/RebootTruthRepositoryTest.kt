package com.example

import android.content.Context
import android.os.SystemClock
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CryptoIntegrityManager
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * Audit R3 #113, findings 4 + 5 — the reboot's two lies, proven
 * against the repository's own machinery:
 *
 *  - Finding 5: the first post-reboot tick used to credit the
 *    device's ENTIRE uptime as focus time. The banked elapsed is
 *    all a reboot may resume from.
 *  - Finding 4: the re-anchor moved the mono anchor and left the
 *    wall anchor at the session's start, so every later tick's
 *    drift read permanently > 90s, tamper stuck, and the
 *    15-minute penalty stretched the session. An honest reboot
 *    must draw NO penalty, on the re-anchor tick or any after.
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

    /**
     * The honest reboot, end to end: a session 3,490 seconds in
     * when the phone dies; the phone comes back on a new boot.
     * The first tick on the new boot resumes from the banked
     * figure — not banked + uptime — and no tick after it may
     * call the man a tamperer.
     */
    @Test
    fun honestRebootBanksOnlyWitnessedTimeAndDrawsNoPenalty() = runBlocking {
        val machineBoot = repo.querySystemBootCount()
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        val banked = 3_490
        val startWall = wallNow - 3_500_000L
        val startMono = monoNow - 3_500_000L
        val row = insert(
            sessionRow(
                startWall = startWall,
                startMono = startMono,
                // A boot count the machine no longer reports: the
                // phone rebooted while the session ran.
                boot = machineBoot + 7,
                elapsed = banked,
                lastTickMono = monoNow - 10_000L,
                hmac = sign(startWall, startMono, machineBoot + 7, 3_600)
            )
        )

        // Finding 5, at the pure seam: uptime at this tick is the
        // whole virtual clock (~7,200s) — none of it may book.
        val (verified, tamper) = repo.computeVerifiedSessionElapsed(row)
        assertEquals(banked, verified)
        assertFalse(tamper)

        // The re-anchor tick: banked figure stands, no penalty,
        // BOTH anchors move to the new boot's frame, and the
        // re-sign covers the new pair (verification passes).
        val afterReboot = repo.updateSessionProgress(row)
        assertEquals(banked, afterReboot.elapsedSeconds)
        assertFalse(afterReboot.tamperDetected)
        assertEquals(0, afterReboot.penaltySecondsAdded)
        assertEquals(machineBoot, afterReboot.bootCount)
        assertTrue(afterReboot.startWallTimeMillis > startWall + 3_000_000L)
        assertTrue(CryptoIntegrityManager.verifySessionState(afterReboot))

        // The NEXT tick was the false-tamper tick: wall-since-start
        // vs mono-since-reboot disagreed by the session's whole
        // pre-reboot life. With both anchors in one frame the
        // drift is the tick's own 30 seconds, the count advances
        // by exactly those 30, and the penalty never lands.
        ShadowSystemClock.advanceBy(Duration.ofSeconds(30))
        val nextTick = repo.updateSessionProgress(afterReboot)
        assertEquals(banked + 30, nextTick.elapsedSeconds)
        assertFalse(nextTick.tamperDetected)
        assertEquals(0, nextTick.penaltySecondsAdded)
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
