package com.example

import android.content.Context
import android.os.VibratorManager
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.SessionSealTransitionImpl
import com.example.service.TactileSoundEngine
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Audit R1 tests-1, findings 1+4 — the seal-ordering proof,
 * anchored to PRODUCTION.
 *
 * The old ArmProvingControllerTest asserted ordering against its
 * own hand-written FakeTransition (events it invented: "marker",
 * "sound"), so production could reorder claim → row → marker →
 * publish → Throw and every test stayed green. This class drives
 * the REAL [SessionSealTransitionImpl] with a REAL
 * [TactileSoundEngine] and a REAL [MonolithRepository]:
 *
 * - the row step is observed from inside the sealRow lambda
 *   (marker must NOT exist yet, publish must NOT have run);
 * - the publish step is observed from inside the publish lambda
 *   (the seal marker MUST already be committed in
 *   SharedPreferences, and the Throw's haptic waveform must NOT
 *   have fired yet);
 * - after executeSeal returns, the Throw's exact waveform
 *   (SEAL_THROW_TIMINGS, read off the Robolectric vibrator
 *   shadow) proves the Throw fired, and fired LAST.
 *
 * Any production reordering — marker before row, publish before
 * marker, Throw before publish — fails one of these assertions.
 * Fakes remain only at the transition's own constructor seam
 * (its clock, unused by executeSeal) and the sealRow/publish
 * callbacks the production API itself takes as parameters.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SessionSealTransitionProductionTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository
    private lateinit var engine: TactileSoundEngine
    private lateinit var transition: SessionSealTransitionImpl

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
        engine = TactileSoundEngine(context)
        transition = SessionSealTransitionImpl(context, engine, repository, clock = { 0L })
        // No seal marker may leak into a case.
        com.example.data.DeviceProtectedPrefs.get(context, MonolithRepository.SEAL_PREFS_NAME)
            .edit().clear().commit()
    }

    @After
    fun tearDown() {
        db.close()
    }

    /** The last haptic waveform the real engine fired, or null. */
    private fun lastVibrationPattern(): LongArray? {
        val manager = context.getSystemService(VibratorManager::class.java)
        return Shadows.shadowOf(manager.defaultVibrator).pattern?.copyOf()
    }

    private fun throwHasFired(): Boolean =
        lastVibrationPattern()?.contentEquals(TactileSoundEngine.SEAL_THROW_TIMINGS) == true

    @Test
    fun executeSeal_productionOrdering_rowThenMarkerThenPublishThenThrow() = runBlocking {
        val events = mutableListOf<String>()
        var markerInsideRow = Int.MIN_VALUE
        var throwInsideRow = true
        var markerInsidePublish = Int.MIN_VALUE
        var throwInsidePublish = true

        val sealed = transition.executeSeal(
            sessionId = 7,
            t0Mono = 1234L,
            sealRow = {
                events += "row"
                markerInsideRow = repository.sealedSessionIdFromMarker()
                throwInsideRow = throwHasFired()
                true
            },
            publish = {
                events += "publish"
                markerInsidePublish = repository.sealedSessionIdFromMarker()
                throwInsidePublish = throwHasFired()
            }
        )

        assertTrue(sealed)
        // The callback order production actually executed.
        assertEquals(listOf("row", "publish"), events)
        // Inside the row step: no marker yet, no Throw yet.
        assertEquals(-1, markerInsideRow)
        assertFalse("Throw must not fire before/during the row step", throwInsideRow)
        // Inside publish: the marker is ALREADY committed (marker
        // before publish), and the Throw has STILL not fired
        // (Throw is the act's last statement).
        assertEquals(7, markerInsidePublish)
        assertFalse("Throw must not fire before publish completes", throwInsidePublish)
        // After the act: the Throw has fired, exactly the seal voice.
        assertTrue("the production Throw must fire after publish", throwHasFired())
        assertTrue(transition.isSealed(7))
        assertEquals(7, repository.sealedSessionIdFromMarker())
    }

    @Test
    fun executeSeal_rowRefusal_noMarkerNoPublishNoThrow_andClaimStaysSpent() = runBlocking {
        var rowCalls = 0
        var publishCalls = 0
        val refused = transition.executeSeal(
            sessionId = 9,
            t0Mono = 50L,
            sealRow = { rowCalls++; false },
            publish = { publishCalls++ }
        )
        assertFalse(refused)
        assertEquals(1, rowCalls)
        assertEquals(0, publishCalls)
        assertEquals(-1, repository.sealedSessionIdFromMarker())
        assertFalse(transition.isSealed(9))
        assertFalse("a refused seal must never sound the Throw", throwHasFired())

        // The claim stays spent: a retry for the same session is
        // refused WITHOUT re-running the row or publish steps.
        val retry = transition.executeSeal(
            sessionId = 9,
            t0Mono = 60L,
            sealRow = { rowCalls++; true },
            publish = { publishCalls++ }
        )
        assertFalse(retry)
        assertEquals(1, rowCalls)
        assertEquals(0, publishCalls)
    }

    @Test
    fun executeSeal_exactlyOnce_secondAttemptRunsNothing() = runBlocking {
        var rowCalls = 0
        var publishCalls = 0
        assertTrue(
            transition.executeSeal(11, 1L, { rowCalls++; true }, { publishCalls++ })
        )
        assertFalse(
            transition.executeSeal(11, 2L, { rowCalls++; true }, { publishCalls++ })
        )
        assertEquals(1, rowCalls)
        assertEquals(1, publishCalls)
        assertEquals(11, repository.sealedSessionIdFromMarker())
    }
}
