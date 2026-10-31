package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.MonolithAccessibilityService
import com.example.service.MonolithShieldService
import com.example.service.SessionSealTransitionImpl
import com.example.service.SessionSealTransitions
import com.example.service.TactileSoundEngine
import com.example.ui.ArmPhase
import com.example.ui.ArmProvingController
import com.example.ui.LiveArmWitnesses
import com.example.ui.RepositoryArmSessionPort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * Audit R1 tests-1, finding 4 — production wiring, exercised.
 *
 * The old suite's clocks and witnesses were entirely fake and the
 * objects MonolithViewModel actually wires (RepositoryArmSessionPort,
 * LiveArmWitnesses, the SessionSealTransitions singleton, the
 * controller's DEFAULT SystemClock) appeared nowhere. This class
 * drives exactly those production objects:
 *
 * - the real port against a real repository/DB (row birth,
 *   snapshot, seal re-anchor, annulment deletion);
 * - the real LiveArmWitnesses against the real services' witness
 *   statics (right session answers, wrong session and dead
 *   witness answer null);
 * - the real process singleton (two binds, one instance);
 * - a full controller seal built ONLY from production
 *   collaborators + the controller's default clock — fakes remain
 *   solely at the vsync frame poster and the test scheduler,
 *   the two true system boundaries of a unit test.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalCoroutinesApi::class)
class ArmProductionWiringTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
        com.example.data.DeviceProtectedPrefs.get(context, MonolithRepository.SEAL_PREFS_NAME)
            .edit().clear().commit()
        resetWitnessStatics()
        SessionSealTransitions.resetForTest()
    }

    @After
    fun tearDown() {
        resetWitnessStatics()
        SessionSealTransitions.resetForTest()
        db.close()
    }

    private fun resetWitnessStatics() {
        MonolithShieldService.instance = null
        MonolithShieldService.protecting = false
        MonolithShieldService.protectingSessionId = -1
        MonolithShieldService.protectingMono = 0L
        MonolithAccessibilityService.instance = null
        MonolithAccessibilityService.armedSessionId = -1
        MonolithAccessibilityService.armedMono = 0L
    }

    @Test
    fun repositoryPort_realRowLifecycle_startSnapshotSealAnnul() = runBlocking {
        val port = RepositoryArmSessionPort(repository, context)
        val id = port.startSession("Deep Study", "Default", 50, 2)
        assertTrue(id > 0)
        assertEquals(id, port.activeSnapshotId())
        val born = repository.sessionById(id)
        assertNotNull(born)
        assertTrue(born!!.isActive)

        // The production seal re-anchors the real row to t=0.
        assertTrue(port.sealSession(id, 4242L))
        assertEquals(4242L, repository.sessionById(id)?.startElapsedRealtimeMillis)

        port.annulProvingSession(id)
        assertNull(port.activeSnapshotId())
        assertNull(repository.sessionById(id))
    }

    @Test
    fun liveWitnesses_readTheRealServiceStatics() {
        MonolithShieldService.instance = MonolithShieldService()
        MonolithShieldService.protecting = true
        MonolithShieldService.protectingSessionId = 9
        MonolithShieldService.protectingMono = 111L
        MonolithAccessibilityService.instance = MonolithAccessibilityService()
        MonolithAccessibilityService.armedSessionId = 9
        MonolithAccessibilityService.armedMono = 222L

        assertEquals(111L, LiveArmWitnesses.shieldProtectingMono(9))
        assertEquals(222L, LiveArmWitnesses.a11yArmedMono(9))
        // Keyed by session: another session gets no proof.
        assertNull(LiveArmWitnesses.shieldProtectingMono(10))
        assertNull(LiveArmWitnesses.a11yArmedMono(10))
        // A dead witness re-pends: protection flag drops → null.
        MonolithShieldService.protecting = false
        assertNull(LiveArmWitnesses.shieldProtectingMono(9))
        MonolithAccessibilityService.instance = null
        assertNull(LiveArmWitnesses.a11yArmedMono(9))
    }

    @Test
    fun sealTransitionsSingleton_twoBindsOneProductionInstance() {
        val engine = TactileSoundEngine(context)
        val first = SessionSealTransitions.get(context, engine, repository)
        val second = SessionSealTransitions.get(context)
        assertSame(first, second)
        assertTrue(first is SessionSealTransitionImpl)
    }

    @Test
    fun controller_fullSealThroughProductionWiring_defaultClock() = runTest {
        val engine = TactileSoundEngine(context)
        val transition = SessionSealTransitionImpl(context, engine, repository)
        val frames = ArrayDeque<() -> Unit>()
        val controller = ArmProvingController(
            scope = backgroundScope,
            port = RepositoryArmSessionPort(repository, context),
            witnesses = LiveArmWitnesses,
            transition = transition,
            playJamHaptics = {},
            // No clock argument: the production default
            // (SystemClock.elapsedRealtime) is part of the wiring
            // under test. Only the vsync poster is faked.
            framePoster = { callback -> frames.addLast(callback) },
            sampleMs = 10
        )
        // Live witness services exist (their statics are set once
        // the session id is known, inside the pump loop).
        MonolithShieldService.instance = MonolithShieldService()
        MonolithAccessibilityService.instance = MonolithAccessibilityService()

        controller.onLatch("Deep Study", "Default", 50, 2)

        var sessionId: Int? = null
        var latchMono = 0L
        var sealedPhase: ArmPhase.SealedRunning? = null
        // Bounded real-time pump: Room's real executor threads
        // must interleave with the test scheduler.
        repeat(200) {
            runCurrent()
            advanceTimeBy(20)
            runCurrent()
            val phase = controller.phase.value
            if (phase is ArmPhase.Proving) {
                latchMono = phase.latchMono
                val sid = phase.sessionId
                if (sid != null && sessionId == null) {
                    sessionId = sid
                    MonolithShieldService.protecting = true
                    MonolithShieldService.protectingSessionId = sid
                    MonolithShieldService.protectingMono = latchMono + 30
                    MonolithAccessibilityService.armedSessionId = sid
                    MonolithAccessibilityService.armedMono = latchMono + 40
                }
            }
            // The shadow clock does not advance until the session
            // id has been observed (issue #678, diagnosed flake):
            // the controller judges its jam/annul deadlines on
            // this clock, and the witness statics above can only
            // be set once Room's real threads land the session
            // row. Advancing the clock from the first pump spends
            // the latch+1500ms annul budget (~25 pumps) whether or
            // not the row has landed, so a slow runner annuls a
            // healthy attempt before its witnesses can exist.
            // Frozen until observed, no deadline can pass before
            // the witnesses are set in the block above; from that
            // pump on the clock advances exactly as before and
            // the seal path is unchanged.
            if (sessionId != null) {
                ShadowSystemClock.advanceBy(Duration.ofMillis(60))
            }
            val queued = frames.size
            repeat(queued) { if (frames.isNotEmpty()) frames.removeFirst().invoke() }
            runCurrent()
            (controller.phase.value as? ArmPhase.SealedRunning)?.let {
                sealedPhase = it
                return@repeat
            }
            Thread.sleep(10)
        }

        val sealed = sealedPhase
        assertNotNull("production-wired controller never sealed; phase=${controller.phase.value}", sealed)
        val sid = sessionId
        assertNotNull(sid)
        // t=0 honoured the dwell floor on the production clock.
        assertTrue(sealed!!.t0Mono >= latchMono + ArmProvingController.DWELL_MS)
        // The REAL row was re-anchored to t=0 by the REAL seal.
        assertEquals(sealed.t0Mono, repository.sessionById(sid!!)?.startElapsedRealtimeMillis)
        // The REAL transition committed its marker and claims it.
        assertEquals(sid, repository.sealedSessionIdFromMarker())
        assertTrue(transition.isSealed(sid))
    }
}
