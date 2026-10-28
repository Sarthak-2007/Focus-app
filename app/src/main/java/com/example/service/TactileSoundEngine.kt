package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class StudySoundscape(val label: String, val subtitle: String) {
    OFF("Silent", "Pure Float-PCM hardware transients only"),
    BROWN_NOISE("Silent", "Pure Float-PCM hardware transients only"),
    WARM_RAIN("Silent", "Pure Float-PCM hardware transients only"),
    BINAURAL_DRONE("Silent", "Pure Float-PCM hardware transients only")
}

/**
 * MONOLITH HAPTIC & FLOAT-PCM TRANSIENT SYNTHESIZER (DEVELOPER-GUIDE.pdf §6 & §7)
 *
 * 1. Haptics (§6):
 *    - Never calls View.performHapticFeedback().
 *    - Always uses VibrationEffect.createOneShot(ms, DEFAULT_AMPLITUDE) at four calibrated durations:
 *      • 1ms: Rotary wheel tick
 *      • 4ms: Switch flip / button press / slider step
 *      • 8ms: Session lock / major commit
 *      • 10ms: Session completion / 60s emergency valve pulse
 *
 * 2. Procedural Float-PCM Sound Engine (§7):
 *    - Zero audio files in res/raw or assets/.
 *    - Zero ambient sound mixer loops.
 *    - Single dedicated background writer thread driving a float-PCM AudioTrack
 *      (AudioFormat.ENCODING_PCM_FLOAT) with:
 *      • 4 harmonic partials weighted { 0.6f, 0.28f, 0.37f, 2.2f }
 *      • Dual exponential decay envelopes:
 *        - Body decay: exp(-0.0114f) per sample step (~ exp(-0.0114 * dn))
 *        - Tail decay: exp(-0.0313f) per sample step (~ exp(-0.0313 * dn))
 *      • AudioFocusRequest before transient playback.
 *    - Every haptic pulse and acoustic transient fire together from the same call site.
 */
class TactileSoundEngine(private val context: Context) {

    companion object {
        const val HAPTIC_ROTARY_TICK_1MS = 1L
        const val HAPTIC_SWITCH_FLIP_4MS = 4L
        const val HAPTIC_SESSION_LOCK_8MS = 8L
        const val HAPTIC_COMPLETION_10MS = 10L

        private const val SAMPLE_RATE = 44100
        // Exact 4-partial harmonic weights from DEVELOPER-GUIDE.pdf §7
        val PARTIAL_WEIGHTS = floatArrayOf(0.6f, 0.28f, 0.37f, 2.2f)
        // Exact dual exponential decay factors per sample step from DEVELOPER-GUIDE.pdf §7
        val BODY_DECAY_STEP = exp(-0.0114).toFloat()
        val TAIL_DECAY_STEP = exp(-0.0313).toFloat()
    }

    private data class SynthCommand(
        val fundamentalHz: Float,
        val durationSamples: Int,
        val gain: Float,
        val chordRatio: Float = 1.0f
    )

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true
    var activeSoundscape: StudySoundscape = StudySoundscape.OFF
        private set

    @Volatile
    var brownNoiseVolume: Float = 0f
        private set

    @Volatile
    var rainVolume: Float = 0f
        private set

    @Volatile
    var droneVolume: Float = 0f
        private set

    private val commandQueue = LinkedBlockingQueue<SynthCommand>(64)

    @Volatile
    private var running = true

    private var audioFocusRequest: AudioFocusRequest? = null
    private val synthThread: Thread

    init {
        synthThread = Thread({ runFloatPcmSynthLoop() }, "MonolithFloatPcmSynth").apply {
            isDaemon = true
            priority = Thread.MAX_PRIORITY
            start()
        }
    }

    private fun runFloatPcmSynthLoop() {
        var track: AudioTrack? = null
        try {
            val maxSamples = (SAMPLE_RATE * 0.16f).toInt() // Up to 160ms transient buffer
            val minBufBytes = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_FLOAT
            ).coerceAtLeast(maxSamples * 4)

            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufBytes)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            track.play()
            val floatBuffer = FloatArray(maxSamples)
            val weights = PARTIAL_WEIGHTS
            val weightNorm = weights.sum()

            while (running) {
                val cmd = commandQueue.poll(500, TimeUnit.MILLISECONDS) ?: continue
                if (!soundEnabled) continue

                requestTransientAudioFocus()

                val count = cmd.durationSamples.coerceIn(64, maxSamples)
                var envBody = 1.0f
                var envTail = 1.0f
                val f0 = cmd.fundamentalHz.coerceIn(90f, 2400f)
                val twoPiOverSr = (2.0 * PI / SAMPLE_RATE).toFloat()

                for (n in 0 until count) {
                    val phase = n * twoPiOverSr * f0
                    // 4 partials with exact weights { 0.6f, 0.28f, 0.37f, 2.2f }
                    val p1 = sin(phase.toDouble()).toFloat() * weights[0]
                    val p2 = sin((phase * 2.0f * cmd.chordRatio).toDouble()).toFloat() * weights[1]
                    val p3 = sin((phase * 3.0f).toDouble()).toFloat() * weights[2]
                    val p4 = sin((phase * 4.7f).toDouble()).toFloat() * weights[3]

                    // Body uses first 3 partials; crisp transient attack/tail uses 4th partial
                    val sample = (((p1 + p2 + p3) * envBody + p4 * envTail) / weightNorm) * cmd.gain
                    floatBuffer[n] = sample.coerceIn(-0.95f, 0.95f)

                    envBody *= BODY_DECAY_STEP
                    envTail *= TAIL_DECAY_STEP
                }

                track.write(floatBuffer, 0, count, AudioTrack.WRITE_BLOCKING)
            }
        } catch (_: Exception) {
            // Safe fallback in headless unit test / Robolectric JVM
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Exception) {}
        }
    }

    private fun requestTransientAudioFocus() {
        try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val req = audioFocusRequest ?: AudioFocusRequest.Builder(
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                ).setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                ).build().also { audioFocusRequest = it }
                am.requestAudioFocus(req)
            }
        } catch (_: Exception) {}
    }

    /**
     * Strict 4-Duration Haptic Vocabulary (DEVELOPER-GUIDE.pdf §6):
     * Always uses VibrationEffect.createOneShot(ms, DEFAULT_AMPLITUDE).
     * Never calls View.performHapticFeedback().
     */
    fun triggerOneShotHaptic(durationMs: Long) {
        if (!hapticsEnabled) return
        val calibratedMs = when {
            durationMs <= 1L -> HAPTIC_ROTARY_TICK_1MS
            durationMs <= 4L -> HAPTIC_SWITCH_FLIP_4MS
            durationMs <= 8L -> HAPTIC_SESSION_LOCK_8MS
            else -> HAPTIC_COMPLETION_10MS
        }
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            calibratedMs,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(calibratedMs)
                }
            }
        } catch (_: Exception) {}
    }

    private fun enqueueTransient(
        fundamentalHz: Float,
        durationMs: Int,
        gain: Float,
        chordRatio: Float = 1.0f
    ) {
        if (!soundEnabled) return
        val samples = ((SAMPLE_RATE * durationMs) / 1000).coerceIn(96, 6000)
        commandQueue.offer(
            SynthCommand(
                fundamentalHz = fundamentalHz,
                durationSamples = samples,
                gain = gain,
                chordRatio = chordRatio
            )
        )
    }

    // =========================================================================
    // 4 CALIBRATED HAPTIC + FLOAT-PCM CALL SITES (DEVELOPER-GUIDE.pdf §6 & §7)
    // =========================================================================

    /** 1ms rotary wheel tick + crisp 4-partial transient */
    fun playRotaryDetent(frequencyHz: Double = 680.0) {
        triggerOneShotHaptic(HAPTIC_ROTARY_TICK_1MS)
        enqueueTransient(
            fundamentalHz = frequencyHz.toFloat().coerceIn(280f, 1480f),
            durationMs = 8,
            gain = 0.42f
        )
    }

    /** 4ms switch flip / button / slider step + tactile latch transient */
    fun playMechanicalLatch(engaged: Boolean) {
        triggerOneShotHaptic(HAPTIC_SWITCH_FLIP_4MS)
        enqueueTransient(
            fundamentalHz = if (engaged) 760f else 520f,
            durationMs = 16,
            gain = 0.54f,
            chordRatio = if (engaged) 1.25f else 0.9f
        )
    }

    /** 8ms session lock / major commit + harmonic fifth transient */
    fun playHarmonicSeal(arming: Boolean) {
        triggerOneShotHaptic(HAPTIC_SESSION_LOCK_8MS)
        enqueueTransient(
            fundamentalHz = if (arming) 360f else 480f,
            durationMs = 48,
            gain = 0.62f,
            chordRatio = 1.5f
        )
    }

    /** 10ms completion / emergency valve / stamp press */
    fun playStampPressSound() {
        triggerOneShotHaptic(HAPTIC_COMPLETION_10MS)
        enqueueTransient(
            fundamentalHz = 185f,
            durationMs = 64,
            gain = 0.68f,
            chordRatio = 1.33f
        )
    }

    fun playHoldRampPulse(progress: Float) {
        val clamped = progress.coerceIn(0f, 1f)
        val ms = if (clamped > 0.85f) HAPTIC_COMPLETION_10MS else HAPTIC_SWITCH_FLIP_4MS
        triggerOneShotHaptic(ms)
        enqueueTransient(
            fundamentalHz = 360f + clamped * 540f,
            durationMs = 14,
            gain = 0.48f
        )
    }

    fun playPendulumEscapementTick(progress: Float) {
        val clamped = progress.coerceIn(0f, 1f)
        triggerOneShotHaptic(HAPTIC_ROTARY_TICK_1MS)
        enqueueTransient(
            fundamentalHz = 520f + clamped * 360f,
            durationMs = 10,
            gain = 0.40f
        )
    }

    fun playMicroSwitchClick25ms(freqHz: Double = 680.0) {
        playRotaryDetent(freqHz)
    }

    fun performViewTick(view: View?) {
        playRotaryDetent(680.0)
    }

    fun performViewConfirm(view: View?) {
        playMechanicalLatch(true)
    }

    // Stubbed no-ops for removed ambient mixer (preserved for API compatibility)
    fun setMixerVolumes(brown: Float, rain: Float, drone: Float) {
        brownNoiseVolume = 0f
        rainVolume = 0f
        droneVolume = 0f
        activeSoundscape = StudySoundscape.OFF
    }

    fun setStudySoundscape(soundscape: StudySoundscape) {
        activeSoundscape = StudySoundscape.OFF
    }

    fun stopAllAudio() {
        running = false
        commandQueue.clear()
    }
}
