package com.example

import com.example.service.TactileSoundEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Slice E, E1 (Siegel T1–T4): the Bit-Identical Seal, proven as
 * arithmetic. The Throw buffer is a pure function — two renders
 * are byte-identical, the bolt's PRNG matches its golden vector,
 * the bolt's support is exact, the clamp holds, and the tail
 * dies on the filed fade. The GOLDEN_SHA256 below is pinned from
 * the first green run's own output (Siegel's method: the golden
 * is defined by the render the spec produces, then pinned so
 * any later drift is a red test, not a debate).
 */
class SealThrowIdentityTest {

    private fun render(): FloatArray {
        val out = FloatArray(44_100)
        TactileSoundEngine.renderSealBuffer(out)
        return out
    }

    @Test
    fun xorshiftGoldenVector() {
        var state = 0x9E3779B9.toInt()
        val expected = longArrayOf(0x510C4619L, 0xE02E553EL, 0x7BB98F3AL, 0x0183A8B5L)
        for (want in expected) {
            state = TactileSoundEngine.xorshift32(state)
            assertEquals(want, state.toLong() and 0xFFFFFFFFL)
        }
    }

    @Test
    fun bufferIsDeterministicClampedAndFullLength() {
        val a = render()
        val b = render()
        assertEquals(44_100, a.size)
        assertTrue("two renders of the Throw must be identical", a.contentEquals(b))
        var peak = 0f
        for (s in a) peak = maxOf(peak, abs(s))
        assertTrue("clamp ±0.95 must hold (peak was $peak)", peak <= 0.95f + 1e-6f)
        assertTrue("the attack must be real (peak was $peak)", peak > 0.3f)
        // The tail fade lands on zero: the last sample is silence.
        assertTrue("tail must end at zero (was ${a.last()})", abs(a.last()) < 1e-4f)
    }

    @Test
    fun goldenHashPinnedFromFirstGreenRun() {
        val buf = render()
        val bytes = ByteArray(buf.size * 2)
        for (i in buf.indices) {
            val v = (buf[i] * 32767f).toInt().coerceIn(-32768, 32767)
            bytes[i * 2] = (v and 0xFF).toByte()
            bytes[i * 2 + 1] = ((v ushr 8) and 0xFF).toByte()
        }
        val hash = MessageDigest.getInstance("SHA-256").digest(bytes)
            .joinToString("") { "%02x".format(it) }
        println("SEAL_BUFFER_SHA256=$hash")
        assertEquals(TactileSoundEngine.SEAL_BUFFER_GOLDEN_SHA256, hash)
    }

    @Test
    fun tailLevelAt900ms() {
        val buf = render()
        fun rms(from: Int, until: Int): Double {
            var acc = 0.0
            for (i in from until until) acc += buf[i].toDouble() * buf[i]
            return sqrt(acc / (until - from))
        }
        // Siegel T3's −46 dB names the ROOM's own decay: g = 0.836
        // is filed as "(−46 dB at ≈900 ms)" — the repeats' level
        // against the room's established level, not against the
        // dry body's attack (the body is dead by ~50 ms; comparing
        // the tail to it measures the body's envelope, not the
        // room). Reference window: 110–165 ms, after the pre-delay
        // and the bolt, where the signal is the room itself.
        val roomRef = rms(4_860, 7_260)
        val tailWindow = rms(39_690, 40_690)
        val db = 20.0 * kotlin.math.log10(tailWindow / roomRef)
        println("SEAL_TAIL_DB_AT_900MS=$db (room ref rms $roomRef, tail rms $tailWindow)")
        assertTrue("room tail at 900ms must sit in −55…−35 dB (was $db)", db in -55.0..-35.0)
    }

    @Test
    fun boltSupportIsExact() {
        val bolt = TactileSoundEngine.renderBoltLayer()
        assertEquals(44_100, bolt.size)
        var outsideNonZero = 0
        var insideNonZero = 0
        for (i in bolt.indices) {
            if (bolt[i] != 0f) {
                if (i in 1323 until 3749) insideNonZero++ else outsideNonZero++
            }
        }
        assertEquals("bolt energy outside [1323, 3749) must be zero", 0, outsideNonZero)
        assertTrue("the bolt must actually sound (nonzero $insideNonZero)", insideNonZero > 1_000)
    }

    @Test
    fun sealHapticPatternHasTheTrueGap() {
        assertTrue(
            TactileSoundEngine.SEAL_THROW_TIMINGS.contentEquals(longArrayOf(0, 24, 38, 16))
        )
        assertTrue(
            TactileSoundEngine.SEAL_THROW_AMPLITUDES.contentEquals(intArrayOf(0, 255, 0, 175))
        )
    }
}
