package com.example

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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Slice D step 4 — the cascade's clocks and ordering, proven
 * against fakes (Opal's test list: deadline/jam/annul, held-mark
 * ordering, the t=0 max() terms). The controller reads its
 * monotonic clock from the injected lambda; the sampler's delays
 * run on the test scheduler. Two clocks, advanced in step by
 * [tick], so every deadline lands on the exact iteration meant
 * to observe it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ArmProvingControllerTest {

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

    /** Slice E: the transition fake — the seal act's ordering
     *  (row → publish → Throw-last) is the contract under test. */
    private class FakeTransition(private val events: MutableList<String>) :
        com.example.service.SessionSealTransition {
        private val claimed = mutableSetOf<Int>()
        private val sealed = mutableSetOf<Int>()
        var primeCalls = 0
        var cancelCalls = 0

        override fun primeForSeal() {
            primeCalls++
            events += "prime"
        }

        override fun cancelSealPrime() {
            cancelCalls++
        }

        override suspend fun executeSeal(
            sessionId: Int,
            t0Mono: Long,
            sealRow: suspend () -> Boolean,
            publish: () -> Unit
        ): Boolean {
            if (!claimed.add(sessionId)) return false
            if (!sealRow()) return false
            sealed += sessionId
            events += "marker"
            publish()
            events += "sound"
            return true
        }

        override suspend fun proveAndSeal(sessionId: Int, latchMonoMs: Long?) =
            com.example.service.SealVerdict.ANNULLED

        override suspend fun recover(sessionId: Int, rowStartWallMs: Long, nowWallMs: Long) =
            com.example.service.SealVerdict.ANNULLED

        override fun isSealed(sessionId: Int): Boolean = sessionId in sealed

        override fun isTombstoned(routineId: Int?, windowStartMillis: Long): Boolean = false
    }

    private class FakeWitnesses : ArmWitnesses {
        var shieldMono: Long? = null
        var a11yMono: Long? = null
        override fun shieldProtectingMono(sessionId: Int): Long? = shieldMono
        override fun a11yArmedMono(sessionId: Int): Long? = a11yMono
    }

    private lateinit var transition: FakeTransition

    private fun TestScope.newController(): ArmProvingController {
        port = FakePort(events)
        witnesses = FakeWitnesses()
        transition = FakeTransition(events)
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
        // The re-anchor lands BEFORE the seal sound: the sound
        // attests a sealed row, never an intention.
        assertTrue(events.indexOf("seal") in 0 until events.indexOf("sound"))
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
        assertTrue(!events.contains("sound"))
        assertEquals(1, transition.cancelCalls)
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
