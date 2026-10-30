package com.example

import android.content.Context
import android.os.VibratorManager
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.SessionSealTransitionImpl
import com.example.service.TactileSoundEngine
import com.example.ui.ArmLink
import com.example.ui.ArmPhase
import com.example.ui.ArmProvingController
import com.example.ui.ArmSessionPort
import com.example.ui.ArmWitnesses
import com.example.ui.LinkMark
import com.example.ui.components.jamSentenceFor
import com.example.ui.provenCount
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Slice D step 4 — the cascade's clocks and ordering (Opal's test
 * list: deadline/jam/annul, held-mark ordering, the t=0 max()
 * terms). The controller reads its monotonic clock from the
 * injected lambda; the sampler's delays run on the test
 * scheduler. Two clocks, advanced in step by [tick], so every
 * deadline lands on the exact iteration meant to observe it.
 *
 * Audit R1 tests-1, findings 1+4: the seal act is NO LONGER a
 * hand-written fake. The controller is wired to the REAL
 * [SessionSealTransitionImpl] over a REAL [TactileSoundEngine]
 * and repository, so the ordering this suite asserts (row →
 * marker → publish → Throw) is production's ordering — the
 * line-by-line proof lives in SessionSealTransitionProductionTest,
 * and the production port/witness/singleton wiring in
 * ArmProductionWiringTest. Fakes remain only at the controller's
 * own injected seams: the session port (Android services + Room
 * timing), the witnesses, the clock, and the vsync poster.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalCoroutinesApi::class)
class ArmProvingControllerTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository
    private lateinit var engine: TactileSoundEngine

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
        engine = TactileSoundEngine(context)
        context.getSharedPreferences(MonolithRepository.SEAL_PREFS_NAME, Context.MODE_PRIVATE)
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

    private var now = 0L
    private val frames = ArrayDeque<() -> Unit>()
    private val events = mutableListOf<String>()
    private lateinit var port: FakePort
    private lateinit var witnesses: FakeWitnesses

    private class FakePort(private val events: MutableList<String>) : ArmSessionPort {
        var nextId = 41
        var snapshotId: Int? = null
        var snapshotMisses = 0
        var startGate: CompletableDeferred<Int>? = null
        val sealCalls = mutableListOf<Pair<Int, Long>>()
        val annulCalls = mutableListOf<Int>()
        var startShieldCalls = 0
        var stopShieldCalls = 0
        var sealResult = true
        /** The production seal marker as it stood INSIDE the row
         *  step (wired to the real repository by the harness):
         *  -1 proves the marker is written after the row, by the
         *  real transition, never before it. */
        var markerProbe: () -> Int = { Int.MIN_VALUE }
        var markerAtSealRow: Int = Int.MIN_VALUE

        override suspend fun startSession(
            intention: String,
            profileName: String,
            durationMinutes: Int,
            studyBlocksCount: Int
        ): Int {
            events += "start"
            val id = startGate?.await() ?: nextId
            snapshotId = id
            return id
        }

        override suspend fun activeSnapshotId(): Int? {
            if (snapshotMisses > 0) {
                snapshotMisses--
                return null
            }
            return snapshotId
        }

        override suspend fun sealSession(sessionId: Int, t0Mono: Long): Boolean {
            sealCalls += sessionId to t0Mono
            markerAtSealRow = markerProbe()
            events += "seal"
            return sealResult
        }

        override suspend fun annulProvingSession(sessionId: Int) {
            annulCalls += sessionId
            if (snapshotId == sessionId) snapshotId = null
        }

        override fun startShield() {
            startShieldCalls++
            events += "shield"
        }

        override fun stopShield() {
            stopShieldCalls++
        }

        override fun onSessionStartedEdge() {
            events += "edge"
        }
    }

    private class FakeWitnesses : ArmWitnesses {
        var shieldMono: Long? = null
        var a11yMono: Long? = null
        override fun shieldProtectingMono(sessionId: Int): Long? = shieldMono
        override fun a11yArmedMono(sessionId: Int): Long? = a11yMono
    }

    private lateinit var transition: SessionSealTransitionImpl

    private fun TestScope.newController(): ArmProvingController {
        port = FakePort(events)
        port.markerProbe = { repository.sealedSessionIdFromMarker() }
        witnesses = FakeWitnesses()
        // The PRODUCTION transition (audit tests-1 #1): if its
        // ordering changes, this suite's seal assertions change
        // with it — there is no fake left to agree with itself.
        transition = SessionSealTransitionImpl(context, engine, repository, clock = { now })
        return ArmProvingController(
            scope = backgroundScope,
            port = port,
            witnesses = witnesses,
            transition = transition,
            playJamHaptics = { events += "jamhaptics" },
            clock = { now },
            framePoster = { callback -> frames.addLast(callback) },
            sampleMs = 10
        )
    }

    /** Pump the frames queued AT ENTRY, one invocation each. A
     *  frame callback that re-posts (the t=0 declaration waiting
     *  out the dwell) queues for the NEXT pump — the fake's clock
     *  only moves when the test moves it, exactly as vsync only
     *  fires when time passes. (Draining until empty spun forever
     *  on the re-post: the dwell test's pump at L+60 re-posted at
     *  the same `now` without limit — that loop, not any class
     *  under it, was the full suite's hang.) */
    private fun pumpFrames() {
        val queuedAtEntry = frames.size
        repeat(queuedAtEntry) {
            if (frames.isNotEmpty()) frames.removeFirst().invoke()
        }
    }

    /** Move the cascade's clock and let the sampler observe it. */
    private fun TestScope.tick(to: Long) {
        now = to
        advanceTimeBy(15)
        runCurrent()
    }

    private fun ArmProvingController.latch() {
        onLatch(
            intention = "Deep Study",
            profileName = "Default",
            durationMinutes = 50,
            studyBlocksCount = 2
        )
    }

    @Test
    fun latchDispatchesOnTheFrameVerdictAlone() = runTest {
        // Settled dispatch law (Joss's §3(c)/§6.5; Ren's
        // Charge-2 ruling, Oct 29): the controller holds no
        // second read — the port seam has no verdict to give
        // it. Entry IS the frame's LIVE verdict (the slide
        // fires its committed chain only on a LIVE latch
        // frame), so entry always dispatches: the phase is
        // Proving and the batch has launched.
        val controller = newController()
        now = 10_000L
        controller.latch()
        runCurrent()
        assertTrue(controller.phase.value is ArmPhase.Proving)
        assertEquals(1, port.startShieldCalls)
        assertTrue(events.contains("start"))
        assertTrue(events.contains("shield"))
    }

    @Test
    fun aDispatchedAttemptThatCannotProveStillHandsTheViewBack() = runTest {
        // CHARGE 2's regression, carried into the settled law
        // (Joss): with the controller-side refusal gone, the
        // strand question becomes — can a dispatched attempt
        // leave the seated view unowned? No. An attempt whose
        // links never answer is annulled at the final
        // deadline, and the annulment increments the hand-back
        // key exactly once: the Deck returns the slide to
        // REST and retires the press latch. Every seated cap
        // has an attempt; every attempt ends in a seal or in
        // this increment.
        val controller = newController()
        val l = 10_000L
        now = l
        controller.latch()
        runCurrent()
        assertTrue(controller.phase.value is ArmPhase.Proving)
        assertEquals(0, controller.annulCount.value)
        tick(l + 100)
        tick(l + 600)
        assertTrue(controller.phase.value is ArmPhase.Jammed)
        tick(l + 1600)
        assertEquals(ArmPhase.Idle, controller.phase.value)
        assertEquals(1, controller.annulCount.value)
    }

    @Test
    fun sealFiresAtTheDwellWhenLinksAnswerInstantly() = runTest {
        val controller = newController()
        val l = 10_000L
        witnesses.shieldMono = l + 30
        witnesses.a11yMono = l + 40
        now = l
        controller.latch()
        runCurrent()
        tick(l + 60)
        // All confirmed well before the dwell: candidate is the
        // dwell floor, L + 210 — frames before it cannot seal.
        pumpFrames()
        assertTrue(port.sealCalls.isEmpty())
        now = l + 210
        pumpFrames()
        runCurrent()
        val phase = controller.phase.value
        assertTrue(phase is ArmPhase.SealedRunning)
        assertEquals(l + 210, (phase as ArmPhase.SealedRunning).t0Mono)
        assertEquals(listOf(41 to l + 210), port.sealCalls)
        // Production ordering, observed on the real transition:
        // inside the row step the seal marker did not exist yet;
        // by the time the phase published, the REAL transition
        // had committed it — row → marker → publish.
        assertEquals(-1, port.markerAtSealRow)
        assertEquals(41, repository.sealedSessionIdFromMarker())
        assertTrue(transition.isSealed(41))
        // And the Throw — the real engine's seal waveform — fired
        // as the act's last statement (the publish had already
        // landed above; SessionSealTransitionProductionTest pins
        // Throw-after-publish from inside the publish lambda).
        assertTrue(
            lastVibrationPattern()?.contentEquals(TactileSoundEngine.SEAL_THROW_TIMINGS) == true
        )
        assertEquals(3, phase.provenCount())
    }

    @Test
    fun t0WaitsForTheLastConfirm() = runTest {
        val controller = newController()
        val l = 10_000L
        witnesses.shieldMono = l + 50
        now = l
        controller.latch()
        runCurrent()
        tick(l + 400)
        // A11Y still silent at L+400: proving, not yet jammed.
        assertTrue(controller.phase.value is ArmPhase.Proving)
        witnesses.a11yMono = l + 450
        tick(l + 450)
        pumpFrames()
        runCurrent()
        val phase = controller.phase.value
        assertTrue(phase is ArmPhase.SealedRunning)
        // lastConfirm (L+450) outranks the dwell (L+210).
        assertEquals(l + 450, (phase as ArmPhase.SealedRunning).t0Mono)
    }

    @Test
    fun heldMarksFlushWithTheirWitnessStamps() = runTest {
        val controller = newController()
        val l = 10_000L
        // The insert's own snapshot read misses, and so does the
        // sampler's first re-read: while L1 is pending, witness
        // confirmations are HELD — and their stamps are the
        // witnesses' own, not the flush's.
        port.snapshotMisses = 2
        witnesses.shieldMono = l + 300
        witnesses.a11yMono = l + 25
        now = l
        controller.latch()
        runCurrent()
        tick(l + 20)
        tick(l + 30)
        // L1 landed on the sampler's second re-read; held marks
        // flushed. lastConfirm is the shield's held stamp L+300,
        // so t=0 is L+300 — not the dwell, not the flush time.
        now = l + 300
        pumpFrames()
        runCurrent()
        val phase = controller.phase.value
        assertTrue(phase is ArmPhase.SealedRunning)
        assertEquals(l + 300, (phase as ArmPhase.SealedRunning).t0Mono)
    }

    @Test
    fun jamNamesTheLinkRetriesTheShieldAndCanStillSeal() = runTest {
        val controller = newController()
        val l = 10_000L
        witnesses.a11yMono = l + 30
        now = l
        controller.latch()
        runCurrent()
        tick(l + 100)
        tick(l + 600)
        val jammed = controller.phase.value
        assertTrue(jammed is ArmPhase.Jammed)
        jammed as ArmPhase.Jammed
        assertEquals(setOf(ArmLink.SHIELD_PROTECTING), jammed.jamSet)
        // Batch dispatch + the machine's own retry = 2 starts.
        assertEquals(2, port.startShieldCalls)
        assertEquals(1, events.count { it == "jamhaptics" })
        val sentence = jamSentenceFor(jammed)
        assertEquals("THE SHIELD DIDN'T ANSWER — STILL CHECKING.", sentence?.text)
        assertNull(sentence?.action)
        // The retry answers: the attempt seals inside its window.
        witnesses.shieldMono = l + 700
        tick(l + 700)
        pumpFrames()
        runCurrent()
        val phase = controller.phase.value
        assertTrue(phase is ArmPhase.SealedRunning)
        assertEquals(l + 700, (phase as ArmPhase.SealedRunning).t0Mono)
        assertTrue(port.annulCalls.isEmpty())
    }

    @Test
    fun annulAtFinalDeadlineDeletesTheRowAndSpeaksPastTense() = runTest {
        val controller = newController()
        val l = 10_000L
        now = l
        controller.latch()
        runCurrent()
        tick(l + 100)
        tick(l + 600)
        assertTrue(controller.phase.value is ArmPhase.Jammed)
        tick(l + 1600)
        // The attempt un-exists: row deleted, shield stopped,
        // phase idle, and the slot holds the past-tense sentence.
        assertEquals(ArmPhase.Idle, controller.phase.value)
        assertEquals(listOf(41), port.annulCalls)
        assertEquals(1, port.stopShieldCalls)
        assertEquals(1, controller.annulCount.value)
        assertTrue(port.sealCalls.isEmpty())
        val sentence = controller.armSentence.value
        assertEquals("NOT SEALED — THE SHIELD DIDN'T ANSWER.", sentence?.text)
        assertNull(sentence?.action)
    }

    @Test
    fun witnessDeathRependsAConfirmedLink() = runTest {
        val controller = newController()
        val l = 10_000L
        witnesses.shieldMono = l + 20
        now = l
        controller.latch()
        runCurrent()
        tick(l + 50)
        var links = (controller.phase.value as ArmPhase.Proving).links
        assertTrue(links[ArmLink.SHIELD_PROTECTING] is LinkMark.Confirmed)
        // The shield dies mid-proving (A11Y has not answered):
        // the confirmed mark is revoked by its witness's death —
        // never by the retry, always by the fact stopping true.
        witnesses.shieldMono = null
        tick(l + 80)
        links = (controller.phase.value as ArmPhase.Proving).links
        assertTrue(links[ArmLink.SHIELD_PROTECTING] is LinkMark.Pending)
        tick(l + 600)
        val jammed = controller.phase.value as ArmPhase.Jammed
        assertEquals(
            setOf(ArmLink.SHIELD_PROTECTING, ArmLink.A11Y_SESSION_ARMED),
            jammed.jamSet
        )
        assertEquals(
            "THE SHIELD DIDN'T ANSWER — STILL CHECKING.",
            jamSentenceFor(jammed)?.text
        )
    }

    @Test
    fun frameReReadRefusesAStaleProof() = runTest {
        val controller = newController()
        val l = 10_000L
        witnesses.shieldMono = l + 30
        witnesses.a11yMono = l + 40
        now = l
        controller.latch()
        runCurrent()
        tick(l + 60)
        // All confirmed; the declaration frame is queued. The
        // a11y witness dies in the gap: the frame re-reads the
        // witnesses themselves and refuses the stale proof.
        witnesses.a11yMono = null
        now = l + 210
        pumpFrames()
        runCurrent()
        assertTrue(port.sealCalls.isEmpty())
        assertTrue(controller.phase.value is ArmPhase.Proving)
        // The witness revives; sampling (resumed by the frame)
        // re-confirms and the seal lands on a fresh frame.
        witnesses.a11yMono = l + 220
        tick(l + 220)
        now = l + 220
        pumpFrames()
        runCurrent()
        assertEquals(listOf(41 to l + 220), port.sealCalls)
    }

    @Test
    fun sealRowRefusalFailsTheAttemptWithoutDeletingTheRow() = runTest {
        // Slice E: the witnesses all answered, t=0 arrived — but
        // the row itself refused the re-anchor (sealSession
        // false). The attempt fails as an annulment-class
        // hand-back with its own sentence; the ROW is not deleted
        // (it may be a live session whose seal merely failed),
        // no Throw sounds, and the prime is cancelled.
        val controller = newController()
        val l = 10_000L
        witnesses.shieldMono = l + 30
        witnesses.a11yMono = l + 40
        port.sealResult = false
        now = l
        controller.latch()
        runCurrent()
        tick(l + 60)
        now = l + 210
        pumpFrames()
        runCurrent()
        assertEquals(ArmPhase.Idle, controller.phase.value)
        assertEquals(1, controller.annulCount.value)
        assertTrue(port.annulCalls.isEmpty())
        // The real transition sealed nothing and sounded nothing:
        // no marker in the real prefs, no claim held as sealed,
        // and the real engine never fired the Throw's waveform.
        assertEquals(-1, repository.sealedSessionIdFromMarker())
        assertFalse(transition.isSealed(41))
        assertFalse(
            lastVibrationPattern()?.contentEquals(TactileSoundEngine.SEAL_THROW_TIMINGS) == true
        )
        val sentence = controller.armSentence.value
        assertEquals("NOT SEALED — THE SESSION DIDN'T ANSWER.", sentence?.text)
    }

    @Test
    fun jamSentenceAndProvenCountArePhasePure() {
        val proving = ArmPhase.Proving(
            attemptId = 1,
            latchMono = 0,
            sessionId = 7,
            links = mapOf(
                ArmLink.SESSION_ROW to LinkMark.Confirmed(5),
                ArmLink.SHIELD_PROTECTING to LinkMark.Confirmed(6),
                ArmLink.A11Y_SESSION_ARMED to LinkMark.Pending
            )
        )
        assertEquals(2, proving.provenCount())
        assertNull(jamSentenceFor(proving))
        assertNull(jamSentenceFor(ArmPhase.Idle))
        assertNull(jamSentenceFor(ArmPhase.SealedRunning(7, 100)))
        assertEquals(0, ArmPhase.Idle.provenCount())
        assertEquals(3, ArmPhase.SealedRunning(7, 100).provenCount())
    }
}
