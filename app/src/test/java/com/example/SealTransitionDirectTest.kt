package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.SealVerdict
import com.example.service.SessionSealTransitionImpl
import com.example.service.SoundEffectsSetting
import com.example.service.TactileSoundEngine
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #48 (audit R1, seal-watchstore): the seal transition's own
 * bookkeeping, exercised directly — the audit's verdict noted
 * SessionSealTransitionImpl's executeSeal / prove had NO direct
 * tests (only a fake in ArmProvingControllerTest). Three charges:
 *
 *  - a throwing publish() must not leave the committed seal marker
 *    testifying "sealed and heard" over a seal that never sounded;
 *  - the persistent marker must hold EVERY sealed session, not
 *    exactly one — and the in-memory set's writes are synchronized;
 *  - an annulment tombstone must survive process death: annulment
 *    deletes the row, so the persisted tombstone is the only
 *    record that can refuse the window in the next process.
 *
 * A "fresh process" is simulated by a fresh
 * [SessionSealTransitionImpl] over the same prefs + database.
 * Sound is OFF throughout, so playSealThrow returns at its gate.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SealTransitionDirectTest {

    private lateinit var context: Context
    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        com.example.data.DeviceProtectedPrefs.get(context, MonolithRepository.SEAL_PREFS_NAME)
            .edit().clear().commit()
        SoundEffectsSetting.setEnabled(context, false)
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
    }

    @After
    fun tearDown() {
        db.close()
        com.example.data.DeviceProtectedPrefs.get(context, MonolithRepository.SEAL_PREFS_NAME)
            .edit().clear().commit()
    }

    private fun freshTransition(clock: () -> Long = { 0L }) =
        SessionSealTransitionImpl(context, TactileSoundEngine(context), repository, clock)

    @Test
    fun executeSeal_publishThrows_markerRolledBack_noFalseTestimony() = runBlocking {
        val transition = freshTransition()
        val ok = transition.executeSeal(
            sessionId = 41,
            t0Mono = 0L,
            sealRow = { true },
            publish = { throw IllegalStateException("surface died mid-publish") }
        )
        assertFalse("a seal whose publish threw did not complete", ok)
        assertFalse("the transition must not claim the seal", transition.isSealed(41))
        assertNotEquals(
            "the on-disk marker must not testify for the un-published seal",
            41, repository.sealedSessionIdFromMarker()
        )
        assertFalse(
            "the persisted id set must not testify either",
            41 in repository.sealedSessionIdsFromMarker()
        )
        // Recovery in a fresh process reads marker-absent truth:
        // it must NOT silently adopt session 41 as sealed-and-heard.
        val reborn = freshTransition()
        assertFalse(reborn.isSealed(41))
    }

    @Test
    fun executeSeal_happyPath_markerTestifies() = runBlocking {
        val transition = freshTransition()
        val ok = transition.executeSeal(
            sessionId = 42,
            t0Mono = 0L,
            sealRow = { true },
            publish = {}
        )
        assertTrue(ok)
        assertTrue(transition.isSealed(42))
        assertEquals(42, repository.sealedSessionIdFromMarker())
        assertTrue(42 in repository.sealedSessionIdsFromMarker())
    }

    @Test
    fun sealMarker_holdsEverySession_acrossProcesses() = runBlocking {
        freshTransition().executeSeal(51, 0L, sealRow = { true }, publish = {})
        freshTransition().executeSeal(52, 0L, sealRow = { true }, publish = {})

        // The legacy single int names only the latest seal…
        assertEquals(52, repository.sealedSessionIdFromMarker())
        // …but the first session's proof is NOT erased (pre-fix it
        // was: the single slot was its only on-disk testimony).
        val reborn = freshTransition()
        assertTrue("session 51's seal must survive session 52's", reborn.isSealed(51))
        assertTrue(reborn.isSealed(52))
        assertEquals(setOf(51, 52), repository.sealedSessionIdsFromMarker())
    }

    @Test
    fun annulmentTombstone_survivesProcessDeath() = runBlocking {
        val windowStart = System.currentTimeMillis()
        val sessionId = db.monolithDao().insertSession(
            FocusSessionEntity(
                intention = "routine proving",
                profileName = "ROUTINE",
                targetDurationSeconds = 3600,
                elapsedSeconds = 0,
                startWallTimeMillis = windowStart,
                startElapsedRealtimeMillis = 0L,
                isActive = true,
                completedSuccessfully = false,
                routineId = 7,
                windowStartMillis = windowStart,
                windowEndMillis = windowStart + 3_600_000L
            )
        ).toInt()

        // A clock that leaps past the annul deadline in one read:
        // no witness ever confirms, so the proving annuls at once.
        var t = 0L
        val transition = freshTransition(clock = { t += 2_000L; t })
        val verdict = transition.proveAndSeal(sessionId, latchMonoMs = 0L)
        assertEquals(SealVerdict.ANNULLED, verdict)

        // The row is gone (annulment deletes)…
        assertNull(repository.sessionById(sessionId))
        // …the tombstone refuses the window in THIS process…
        assertTrue(transition.isTombstoned(7, windowStart))
        // …and — the charge — in the NEXT process too.
        val reborn = freshTransition()
        assertTrue(
            "a refused routine window must stay refused after process death",
            reborn.isTombstoned(7, windowStart)
        )
        assertFalse(reborn.isTombstoned(7, windowStart + 1L))
        assertFalse(reborn.isTombstoned(8, windowStart))
    }
}
