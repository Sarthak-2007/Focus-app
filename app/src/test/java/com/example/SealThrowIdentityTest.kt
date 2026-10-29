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
 * dies on the filed fade.
 *
 * Issue #66 (audit R1 tests-2) tautology fix: the former
 * goldenHashPinnedFromFirstGreenRun asserted the render's hash
 * equalled a constant in production that was itself pinned from
 * that render's first output — self-consistency dressed as proof.
 * That constant is deleted. In its place, referenceRender() below
 * is an INDEPENDENT re-implementation typed in this file from the
 * spec constants as documented (360 Hz body, weights
 * {0.6, 0.28, 0.37, 2.2}, decay steps exp(-0.0114)/exp(-0.0313),
 * bolt seed/support/gain, room combs 1310/1372 with g=0.836,
 * pre-delay 794, wet send 0.25119, tail catch 39 690): it imports
 * no production constant and calls no production render code, so
 * a production constant nudged — or wrong from the start against
 * the spec — turns this test red.
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

    /** Independent reference render — see class header. Spec
     *  constants are re-typed here on purpose; do NOT "deduplicate"
     *  them into production constants, that would restore the
     *  tautology this test exists to break. */
    private fun referenceRender(): FloatArray {
        val out = FloatArray(44_100)
        val weights = floatArrayOf(0.6f, 0.28f, 0.37f, 2.2f)
        val weightNorm = weights.sum()
        var envBody = 1.0f
        var envTail = 1.0f
        val bodyDecay = kotlin.math.exp(-0.0114).toFloat()
        val tailDecay = kotlin.math.exp(-0.0313).toFloat()
        val twoPiOverSr = (2.0 * kotlin.math.PI / 44100).toFloat()
        val f0 = 360f
        for (n in 0 until 44_100) {
            val phase = n * twoPiOverSr * f0
            val p1 = kotlin.math.sin(phase.toDouble()).toFloat() * weights[0]
            val p2 = kotlin.math.sin((phase * 2.0f * 1.5f).toDouble()).toFloat() * weights[1]
            val p3 = kotlin.math.sin((phase * 3.0f).toDouble()).toFloat() * weights[2]
            val p4 = kotlin.math.sin((phase * 4.7f).toDouble()).toFloat() * weights[3]
            val sample = (((p1 + p2 + p3) * envBody + p4 * envTail) / weightNorm) * 0.62f
            out[n] = sample.coerceIn(-0.95f, 0.95f)
            envBody *= bodyDecay
            envTail *= tailDecay
        }
        // Bolt (spec: seed 0x9E3779B9, xorshift 13/17/5, support
        // [1323, 1323+2426), LP alpha 0.1203, gain 0.31074,
        // env step 0.9987410, 88-sample onset, 220-sample fade).
        var s = 0x9E3779B9.toInt()
        var lp = 0f
        var env = 1f
        for (k in 0 until 2426) {
            s = s xor (s shl 13); s = s xor (s ushr 17); s = s xor (s shl 5)
            val raw = (s ushr 8).toFloat() * (1f / 8388608f) - 1f
            lp += 0.1203f * (raw - lp)
            val onset = if (k < 88) k.toFloat() / 88 else 1f
            val fade = if (k >= 2426 - 220) (2426 - k).toFloat() / 220 else 1f
            out[1323 + k] += lp * onset * env * fade * 0.31074f
            env *= 0.9987410f
        }
        // Room (spec: 794-sample pre-delay, combs 1310/1372 with
        // g=0.836 and in-loop LP alpha 0.2262, allpass 221 @ 0.5,
        // wet send 0.25119), off a dry snapshot.
        val dry = out.copyOf()
        val comb1 = FloatArray(1310); val comb2 = FloatArray(1372)
        var idx1 = 0; var idx2 = 0; var lp1 = 0f; var lp2 = 0f
        val allpass = FloatArray(221); var apIdx = 0
        for (n in 0 until 44_100) {
            val inp = if (n >= 794) dry[n - 794] else 0f
            val prev1 = comb1[idx1]
            lp1 += 0.2262f * (prev1 - lp1)
            val y1 = inp + 0.836f * lp1
            comb1[idx1] = y1; idx1 = if (idx1 + 1 == comb1.size) 0 else idx1 + 1
            val prev2 = comb2[idx2]
            lp2 += 0.2262f * (prev2 - lp2)
            val y2 = inp + 0.836f * lp2
            comb2[idx2] = y2; idx2 = if (idx2 + 1 == comb2.size) 0 else idx2 + 1
            val wetIn = (y1 + y2) * 0.5f
            val apPrev = allpass[apIdx]
            val apOut = apPrev - 0.5f * wetIn
            allpass[apIdx] = wetIn + 0.5f * apOut
            apIdx = if (apIdx + 1 == allpass.size) 0 else apIdx + 1
            out[n] = (dry[n] + apOut * 0.25119f).coerceIn(-0.95f, 0.95f)
        }
        val fadeLen = 44_100 - 39_690
        for (n in 39_690 until 44_100) {
            val k = n - 39_690
            out[n] *= (fadeLen - k).toFloat() / fadeLen
        }
        return out
    }

    private fun pcm16Sha256(buf: FloatArray): String {
        val bytes = ByteArray(buf.size * 2)
        for (i in buf.indices) {
            val v = (buf[i] * 32767f).toInt().coerceIn(-32768, 32767)
            bytes[i * 2] = (v and 0xFF).toByte()
            bytes[i * 2 + 1] = ((v ushr 8) and 0xFF).toByte()
        }
        return MessageDigest.getInstance("SHA-256").digest(bytes)
            .joinToString("") { "%02x".format(it) }
    }

    @Test
    fun renderMatchesIndependentReference() {
        val buf = render()
        val ref = referenceRender()
        // Hand-computed spot check: sample 0 is sin(0) through every
        // partial, no bolt (support starts at 1323), no room input
        // (pre-delay) — it is exactly 0 by arithmetic, not by render.
        assertEquals(0f, buf[0])
        assertTrue(
            "production render must equal the independent reference render sample-for-sample",
            buf.contentEquals(ref)
        )
        assertEquals(
            "PCM16 hash of production must equal hash of the independent reference",
            pcm16Sha256(ref), pcm16Sha256(buf)
        )
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
