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
 *    the first post-reboot tick credits the CURRENT boot's
 *    witnessed uptime (the shield stands from BOOT_COMPLETED, so
 *    that span is guarded time — #81's doctrine, which superseded
 *    this branch's original never-credit-uptime form in the
 *    merge), and an UNWITNESSED boot span — a boot-count delta
 *    of 2+ against the row's anchor (Safe Mode, a force-stop
 *    spanning a reboot: Monolith never ran, so the span is
 *    unproven) — convicts as tamper and draws the penalty. What
 *    finding 5 killed stands dead: unproven time never passes
 *    as a free, invisible freeze.
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

    private fun setMachineBootCount(count: Int): Int {
        Settings.Global.putInt(context.contentResolver, Settings.Global.BOOT_COUNT, count)
        val read = repo.querySystemBootCount()
        assertEquals("the test must control the machine boot count", count, read)
        return read
    }

    /**
     * The honest reboot, end to end, under the composed law: a
     * session 3,490 seconds in when the phone dies; the phone
     * comes back on the NEXT boot (delta 1 — witnessed: the
     * shield stood from that boot's first second). The first
     * tick resumes from banked + this boot's witnessed uptime,
     * and no tick after it may call the man a tamperer — the
     * both-anchors re-anchor (finding 4) keeps the drift test
     * segment-local, so the formerly false-tamper next tick
     * stays clean.
     */
    @Test
    fun honestRebootCreditsTheWitnessedBootAndDrawsNoPenalty() = runBlocking {
        val machineBoot = setMachineBootCount(10)
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        val uptimeSec = (monoNow / 1_000L).toInt()
        val banked = 3_490
        val startWall = wallNow - 3_500_000L
        val startMono = monoNow - 3_500_000L
        val anchoredBoot = machineBoot - 1
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

        // Finding 5/#81 at the pure seam: banked + the witnessed
        // current boot's uptime; no tamper.
        val (verified, tamper) = repo.computeVerifiedSessionElapsed(row)
        assertEquals(banked + uptimeSec, verified)
        assertFalse(tamper)

        // The re-anchor tick: the composed figure stands, no
        // penalty, BOTH anchors move to the new boot's frame,
        // and the re-sign covers the new pair.
        val afterReboot = repo.updateSessionProgress(row)
        assertEquals(banked + uptimeSec, afterReboot.elapsedSeconds)
        assertFalse(afterReboot.tamperDetected)
        assertEquals(0, afterReboot.penaltySecondsAdded)
        assertEquals(machineBoot, afterReboot.bootCount)
        assertTrue(afterReboot.startWallTimeMillis > startWall + 3_000_000L)
        assertTrue(CryptoIntegrityManager.verifySessionState(afterReboot))

        // Finding 4's formerly false-tamper tick: with both
        // anchors in one frame the drift is the tick's own 30
        // seconds, the count advances by exactly those 30, and
        // the penalty never lands.
        ShadowSystemClock.advanceBy(Duration.ofSeconds(30))
        val nextTick = repo.updateSessionProgress(afterReboot)
        assertEquals(banked + uptimeSec + 30, nextTick.elapsedSeconds)
        assertFalse(nextTick.tamperDetected)
        assertEquals(0, nextTick.penaltySecondsAdded)
    }

    /**
     * Finding 5's abuse case in its composed (#81 F3) form: the
     * row's anchor is SEVEN boots behind the machine — the span
     * crossed boots Monolith never witnessed (Safe Mode, a
     * force-stop spanning reboots). The figure still books the
     * witnessed current boot, but the span convicts: tamper at
     * the seam, and the penalty on the tick. Unproven time is
     * not a free, invisible freeze.
     */
    @Test
    fun unwitnessedBootSpanStillConvicts() = runBlocking {
        val machineBoot = setMachineBootCount(10)
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        val uptimeSec = (monoNow / 1_000L).toInt()
        val banked = 3_490
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
        assertEquals(banked + uptimeSec, verified)
        assertTrue("an unwitnessed boot span must read as tamper", tamper)

        val ticked = repo.updateSessionProgress(row)
        assertTrue(ticked.tamperDetected)
        assertEquals(
            MonolithRepository.HMAC_TAMPER_PENALTY_SECONDS,
            ticked.penaltySecondsAdded
        )
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
