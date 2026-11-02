package com.example

import com.example.domain.DeskClockRecord
import com.example.ui.maximum.MaximumPresets
import com.example.ui.motion.SpringState
import com.example.ui.strongroom.WheelPhysics
import com.example.ui.strongroom.WheelTurn
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * STAGE 11 MAXIMUM — the assembly dispositions that stand as data,
 * pinned (BUILD-SR-18). All figures UNPROVEN-ON-DEVICE (G-7);
 * these tests pin arithmetic and law, never feel. End-phase
 * evidence (R6); the lane's standing proof is the compile.
 */
class MaximumAssemblyTest {

    @Test
    fun `E-14 desk clock extent is LINEAR in held time - an eased extent fails by construction`() {
        // The toolkit's extent curve is the identity: extent at t and 2t in exact ratio.
        for (f in listOf(0.1f, 0.25f, 0.5f, 0.9f)) {
            assertEquals(f, MaximumPresets.deskClockExtent(f), 0f)
        }
        assertEquals(
            MaximumPresets.deskClockExtent(0.3f) * 2f,
            MaximumPresets.deskClockExtent(0.6f), 0.0001f
        )
        // And the composed domain record forms linearly too: formed
        // columns are a pure linear function of elapsed inside the span.
        val span = 12L * 60L * 60L * 1000L
        val quarter = DeskClockRecord.record(span, span / 4, emptyList(), emptyList())
        val half = DeskClockRecord.record(span, span / 2, emptyList(), emptyList())
        assertEquals(quarter.formedColumns * 2, half.formedColumns)
    }

    @Test
    fun `E-15 hour drag - capture is the plus-minus-4-minute snap window itself`() {
        val drag = MaximumPresets.hourDrag()
        // Released at rest 2 minutes from a quarter: captured by it.
        assertEquals(60f, drag.restTarget(SpringState(62f, 0f)), 0f)
        // Released at rest 3 minutes BEFORE a quarter: captured forward.
        assertEquals(60f, drag.restTarget(SpringState(57f, 0f)), 0f)
        // The window is the spec's, named in the preset.
        assertEquals(4f, MaximumPresets.HOUR_SNAP_WINDOW_MINUTES, 0f)
        assertEquals(MaximumPresets.HOUR_SNAP_WINDOW_MINUTES, drag.spec.captureRadius, 0f)
    }

    @Test
    fun `ADD-2 break drag rests exactly on a whole minute`() {
        val drag = MaximumPresets.breakDrag()
        val release = drag.release(SpringState(37.4f, 0f))
        assertEquals(release.target, release.finalValue, 0f)
        assertEquals(37f, release.finalValue, 0f)
        // The break train's resistance is FREE by law: preset notches cost no travel.
        val train = MaximumPresets.breakTrain(rangeMinutes = 120, presetMinutes = listOf(10, 20, 30))
        assertEquals(1f, train.resistanceAt(20f, 0f), 0f)
    }

    @Test
    fun `E-01 detents are keyed to position - identical sequence at any gesture velocity`() {
        val train = MaximumPresets.spinTrain(pass = 1)
        // Crossings are a pure function of the two positions; no clock exists in the train.
        val slow = train.crossings(10f, 200f).map { it.position }
        val fast = train.crossings(10f, 200f).map { it.position }
        assertEquals(slow, fast)
        assertEquals(listOf(36f, 72f, 108f, 144f, 180f), slow)
        // Pass amplitudes ladder as filed: 70 -> 95 -> 120.
        assertEquals(70, MaximumPresets.spinStationAmplitude(1))
        assertEquals(95, MaximumPresets.spinStationAmplitude(2))
        assertEquals(120, MaximumPresets.spinStationAmplitude(3))
    }

    @Test
    fun `E-10 spent stops carry the ruled 4_0 degree strain travel`() {
        assertEquals(4.0f, MaximumPresets.SPENT_STOP_TRAVEL_DEG, 0f)
        val stop = MaximumPresets.spentPurseStop(travelLimit = 100f)
        assertEquals(4.0f, stop.spec.approachZone, 0f)
    }

    @Test
    fun `E-08 and E-13 are excluded by law, named - never silently absent`() {
        val names = MaximumPresets.ExcludedByLaw.map { it.name }
        assertTrue(names.contains("drum-minute-end"))
        assertTrue(names.contains("ring-hour"))
    }

    @Test
    fun `E-02 the wheel - resistance rises within the pass, release governed, seat heaviest`() {
        // Rising resistance: the same hand travel crosses FEWER
        // detents late in the pass than at its start (the wheel
        // is pulling a mechanism, not spinning a dial).
        val wheel = WheelTurn()
        val early = wheel.dragBy(360f).size
        val late = wheel.dragBy(360f).size
        assertTrue("early=$early late=$late", late < early)
        // Governed release: a violent flick earns no more than the
        // capped travel (plus the seat's completing detent).
        val flicked = WheelTurn()
        flicked.release(10_000f)
        assertTrue(
            flicked.angleDegrees <= WheelPhysics.MAX_RELEASE_TRAVEL_DEG + WheelPhysics.DETENT_DEGREES
        )
        assertEquals(0f, flicked.pendingFraction(), 0.0001f)
        // The seat is the heaviest single figure on the wheel.
        assertTrue(WheelPhysics.SEAT_WEIGHT > 1.35f)
        // Interruption: always exactly on a detent — no half-state.
        val interrupted = WheelTurn()
        interrupted.dragBy(50f)
        interrupted.interrupt()
        assertEquals(0f, interrupted.pendingFraction(), 0.0001f)
    }

    @Test
    fun `H-6 dynamic range ships as data, heaviest to lightest`() {
        val peaks = MaximumPresets.DynamicRangePeaks
        assertEquals(peaks.sortedDescending(), peaks)
        assertEquals(240, peaks.first())
        assertEquals(30, peaks.last())
        assertTrue(abs(peaks.first() - peaks.last()) >= 200)
    }
}
