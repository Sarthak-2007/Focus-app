package com.example.service

import android.app.NotificationManager
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
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * MONOLITH HAPTIC & FLOAT-PCM TRANSIENT SYNTHESIZER (DEVELOPER-GUIDE.pdf §6 & §7)
 *
 * 1. Haptics (§6, rebuilt in Ren's revamp):
 *    - Never calls View.performHapticFeedback().
 *    - Waveform patterns with amplitude envelopes, one per physical action:
 *      detent tick, two-stage latch, firm click, session-seal clunk,
 *      softening completion ticks. One-shots survive only as the
 *      pre-API-26 fallback and the legacy triggerOneShotHaptic() API.
 *
 * 2. Procedural Float-PCM Sound Engine (§7):
 *    - Zero audio files in res/raw or assets/.
 *    - Zero ambient sound mixer loops — with ONE ruled exception
 *      (#253 line 1): the session soundscape lane below. It is
 *      not a mixer and holds no files: a single generated loop
 *      ([SoundscapeSynth]) on its own track, under the user's
 *      persisted fact ([SoundscapeSetting], default OFF), living
 *      only for a session's length. Every other voice in this
 *      engine remains a transient under the 160 ms law.
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
        // Truth Car 3 (UNA-03): the live-engine registry. A process
        // can hold more than one engine (MainActivity's, the seal
        // transition's), constructed through different doors at
        // different times; the persisted sound fact reaches ALL of
        // them through here. Weak keys: a dead engine's entry dies
        // with it, nothing to unregister.
        private val liveEngines: MutableSet<TactileSoundEngine> =
            java.util.Collections.newSetFromMap(
                java.util.WeakHashMap<TactileSoundEngine, Boolean>()
            )

        internal fun registerLiveEngine(engine: TactileSoundEngine) {
            synchronized(liveEngines) { liveEngines.add(engine) }
        }

        /** Push a freshly-written sound fact to every live engine.
         *  Called ONLY by [SoundEffectsSetting.setEnabled], after
         *  the commit has landed. */
        fun pushSoundEnabledToLiveEngines(enabled: Boolean) {
            synchronized(liveEngines) {
                liveEngines.forEach { it.soundEnabled = enabled }
            }
        }

        /** Issue #49: a torn-down engine leaves the registry, so a
         *  later fact push can never resurrect it and the WeakHashMap
         *  is not the only removal path. */
        internal fun unregisterLiveEngine(engine: TactileSoundEngine) {
            synchronized(liveEngines) { liveEngines.remove(engine) }
        }

        /** Push a freshly-written haptics fact to every live engine.
         *  Called ONLY by [SoundEffectsSetting.setHapticsEnabled],
         *  after the commit has landed. */
        fun pushHapticsEnabledToLiveEngines(enabled: Boolean) {
            synchronized(liveEngines) {
                liveEngines.forEach { it.hapticsEnabled = enabled }
            }
        }

        /** Test seam: how many engines this process currently holds. */
        internal fun liveEngineCountForTest(): Int =
            synchronized(liveEngines) { liveEngines.size }

        const val HAPTIC_ROTARY_TICK_1MS = 1L
        const val HAPTIC_SWITCH_FLIP_4MS = 4L
        const val HAPTIC_SESSION_LOCK_8MS = 8L
        const val HAPTIC_COMPLETION_10MS = 10L

        private const val SAMPLE_RATE = 44100

        // The soundscape lane (#253 line 1): one writer block is
        // ~93 ms of the loop; the master gain keeps the wash
        // under every transient voice in the engine.
        internal const val SOUNDSCAPE_BLOCK_SAMPLES = 4096
        internal const val SOUNDSCAPE_MASTER_GAIN = 0.55f
        // Exact 4-partial harmonic weights from DEVELOPER-GUIDE.pdf §7
        val PARTIAL_WEIGHTS = floatArrayOf(0.6f, 0.28f, 0.37f, 2.2f)
        // Exact dual exponential decay factors per sample step from DEVELOPER-GUIDE.pdf §7
        val BODY_DECAY_STEP = exp(-0.0114).toFloat()
        val TAIL_DECAY_STEP = exp(-0.0313).toFloat()

        // =====================================================================
        // SLICE E — THE SEAL THROW (B-5: ONE voice, bit-identical at
        // every seal; Siegel's recipe, Riegel's constants). The bolt
        // and room layers contain ZERO transcendental calls: every
        // coefficient is a decimal literal, derivation in its
        // comment, so the grain is bit-exact on every device. The
        // body keeps its shipped sin() path, unmodified — E1's law
        // is "body unmodified".
        // =====================================================================

        /** The Throw's full length: 1000 ms at 44 100 Hz. */
        const val SEAL_SAMPLES = 44_100

        /** xorshift32 seed (Marsaglia shifts 13/17/5), frozen.
         *  Golden vector, pinned by test: the first four raw
         *  outputs are 0x510C4619, 0xE02E553E, 0x7BB98F3A,
         *  0x0183A8B5. Non-zero — the zero state is absorbing. */
        const val SEAL_BOLT_SEED: Int = 0x9E3779B9.toInt()

        /** The bolt enters at +30 ms, exactly. */
        const val BOLT_START_SAMPLE = 1323

        /** The bolt's support: 2426 samples, ending at +85.0 ms. */
        const val BOLT_LENGTH_SAMPLES = 2426

        /** One-pole lowpass for the bolt: 900 Hz at 44.1 kHz. */
        const val BOLT_LP_ALPHA = 0.1203f

        /** Bolt peak gain: 0.62 × 10^(−6/20), Orson's 0.311
         *  unrounded (−6 dB under the body's gain). */
        const val BOLT_PEAK_GAIN = 0.31074f

        /** Bolt envelope decay step: exp(−1/(0.018 × 44100)),
         *  τ = 18 ms. */
        const val BOLT_ENV_STEP = 0.9987410f

        /** Room comb feedback, both combs: −46 dB at ≈900 ms by
         *  construction. A build-time constant (Riegel §4): the
         *  device-gate fallback is a one-line edit to 0.78f with
         *  its dependents, in one commit — never a runtime switch,
         *  because bit-identity forbids two voices. */
        const val COMB_G = 0.836f

        /** The combs' in-loop lowpass: 1.8 kHz at 44.1 kHz. */
        const val LOOP_LP_ALPHA = 0.2262f

        /** Wet send: 10^(−12/20). */
        const val WET_SEND = 0.25119f

        /** Where the tail crosses −46 dB and the linear fade to
         *  zero begins (900 ms). The bed catch (a later build,
         *  unassigned — Riegel §3.4) triggers off this coordinate;
         *  until it lands the Throw ships lawfully uncaught. */
        const val TAIL_CATCH_SAMPLE = 39_690

        /** The Throw's haptic twin (Siegel §1): the shipped seal's
         *  skeleton with the middle segment zeroed — 110 → 0, a
         *  true gap. The shipped seal thuds through its middle;
         *  the Throw breathes through it. */
        val SEAL_THROW_TIMINGS = longArrayOf(0, 24, 38, 16)
        val SEAL_THROW_AMPLITUDES = intArrayOf(0, 255, 0, 175)

        // Issue #66 (audit R1 tests-2): the former
        // SEAL_BUFFER_GOLDEN_SHA256 constant was removed — it was
        // pinned from this code's own first output, so it could
        // detect drift but never a wrong original render. The
        // identity test now compares against a reference render
        // typed independently from the spec constants (see
        // SealThrowIdentityTest.referenceRender).

        /** One xorshift32 step. JVM Int overflow and shift
         *  semantics are spec-defined, so this sequence is
         *  identical on every device, every process, forever. */
        internal fun xorshift32(state: Int): Int {
            var s = state
            s = s xor (s shl 13)
            s = s xor (s ushr 17)
            s = s xor (s shl 5)
            return s
        }

        /** The bolt layer alone, on a silent buffer (test T4's
         *  subject): exactly zero outside [1323, 1323 + 2426). */
        internal fun renderBoltLayer(): FloatArray {
            val out = FloatArray(SEAL_SAMPLES)
            addBolt(out)
            return out
        }

        private fun addBolt(out: FloatArray) {
            var s = SEAL_BOLT_SEED
            var lp = 0f
            var env = 1f
            val onsetSamples = 88 // 2 ms linear onset
            val fadeSamples = 220 // final 5 ms linear fade
            for (k in 0 until BOLT_LENGTH_SAMPLES) {
                s = xorshift32(s)
                // Top 24 bits mapped to [−1, 1) in exact 2^-23 steps.
                val raw = (s ushr 8).toFloat() * (1f / 8388608f) - 1f
                lp += BOLT_LP_ALPHA * (raw - lp)
                val onset = if (k < onsetSamples) k.toFloat() / onsetSamples else 1f
                val fade = if (k >= BOLT_LENGTH_SAMPLES - fadeSamples) {
                    (BOLT_LENGTH_SAMPLES - k).toFloat() / fadeSamples
                } else {
                    1f
                }
                out[BOLT_START_SAMPLE + k] += lp * onset * env * fade * BOLT_PEAK_GAIN
                env *= BOLT_ENV_STEP
            }
        }

        /**
         * THE RENDER (Siegel §2): one pass, 44 100 samples,
         * input-free — no clock, no Random, no stored state; only
         * compile-time constants and the frozen seed. Layer A the
         * shipped body unmodified (360 Hz, partials 360/1080/1080/
         * 1692, weights {0.6, 0.28, 0.37, 2.2}, the decay pair,
         * gain 0.62, clamp ±0.95, sample 0 = the attack, zero
         * pre-silence). Layer B the bolt at sample 1323. Layer C
         * the room: 18 ms pre-delay, two feedback combs
         * (1310/1372 samples) summed 0.5 each, one allpass
         * (221, 0.5), wet send −12 dB. Samples 39 690–44 100 fade
         * linearly to zero.
         */
        internal fun renderSealBuffer(out: FloatArray) {
            require(out.size == SEAL_SAMPLES)
            val weights = PARTIAL_WEIGHTS
            val weightNorm = weights.sum()
            var envBody = 1.0f
            var envTail = 1.0f
            val twoPiOverSr = (2.0 * PI / SAMPLE_RATE).toFloat()
            val f0 = 360f
            for (n in 0 until SEAL_SAMPLES) {
                val phase = n * twoPiOverSr * f0
                val p1 = sin(phase.toDouble()).toFloat() * weights[0]
                val p2 = sin((phase * 2.0f * 1.5f).toDouble()).toFloat() * weights[1]
                val p3 = sin((phase * 3.0f).toDouble()).toFloat() * weights[2]
                val p4 = sin((phase * 4.7f).toDouble()).toFloat() * weights[3]
                val sample = (((p1 + p2 + p3) * envBody + p4 * envTail) / weightNorm) * 0.62f
                out[n] = sample.coerceIn(-0.95f, 0.95f)
                envBody *= BODY_DECAY_STEP
                envTail *= TAIL_DECAY_STEP
            }
            addBolt(out)

            // Room (Layer C), computed off a dry snapshot so the
            // wet path can never feed on its own additions.
            val dry = out.copyOf()
            val comb1 = FloatArray(1310)
            val comb2 = FloatArray(1372)
            var idx1 = 0
            var idx2 = 0
            var lp1 = 0f
            var lp2 = 0f
            val allpass = FloatArray(221)
            var apIdx = 0
            val preDelay = 794 // 18 ms
            for (n in 0 until SEAL_SAMPLES) {
                val inp = if (n >= preDelay) dry[n - preDelay] else 0f
                val prev1 = comb1[idx1]
                lp1 += LOOP_LP_ALPHA * (prev1 - lp1)
                val y1 = inp + COMB_G * lp1
                comb1[idx1] = y1
                idx1 = if (idx1 + 1 == comb1.size) 0 else idx1 + 1
                val prev2 = comb2[idx2]
                lp2 += LOOP_LP_ALPHA * (prev2 - lp2)
                val y2 = inp + COMB_G * lp2
                comb2[idx2] = y2
                idx2 = if (idx2 + 1 == comb2.size) 0 else idx2 + 1
                val wetIn = (y1 + y2) * 0.5f
                val apPrev = allpass[apIdx]
                val apOut = apPrev - 0.5f * wetIn
                allpass[apIdx] = wetIn + 0.5f * apOut
                apIdx = if (apIdx + 1 == allpass.size) 0 else apIdx + 1
                out[n] = (dry[n] + apOut * WET_SEND).coerceIn(-0.95f, 0.95f)
            }
            // The tail fade: linear to zero under the (future)
            // bed catch — with no bed, the Throw ends at −46 dB
            // fading to exact silence, never a truncation click.
            val fadeLen = SEAL_SAMPLES - TAIL_CATCH_SAMPLE
            for (n in TAIL_CATCH_SAMPLE until SEAL_SAMPLES) {
                val k = n - TAIL_CATCH_SAMPLE
                out[n] *= (fadeLen - k).toFloat() / fadeLen
            }
        }
    }

    private data class SynthCommand(
        val fundamentalHz: Float,
        val durationSamples: Int,
        val gain: Float,
        val chordRatio: Float = 1.0f,
        /** Riegel's sequence barrier stamp: commands enqueued
         *  before a seal prime are pre-latch backlog and are
         *  dropped, never allowed to delay the Throw. */
        val seq: Long = 0L
    )

    /** The sound gate every playback path reads. Truth Car 3
     *  (UNA-03): this var is a CACHE of the persisted fact
     *  ([SoundEffectsSetting]), never the fact itself — it is born
     *  from the fact at construction, on whichever thread and
     *  through whichever door the engine is constructed, and
     *  [SoundEffectsSetting.setEnabled] rewrites it on every live
     *  engine when the fact changes. Before this, the initializer
     *  was the literal `true`: a heartbeat-born engine carried ON
     *  while PARK printed OFF. */
    // Issue #49: @Volatile — the synth thread reads this gate with
    // no lock; without the modifier there was no happens-before
    // guarantee a toggle was ever seen. `internal set` — the only
    // writers are the fact store's live push (companion, above)
    // and [resyncSoundEnabledFromFact]; no call site may write the
    // cache directly and bypass the persisted fact again.
    @Volatile
    var soundEnabled: Boolean = SoundEffectsSetting.isEnabled(context)
        internal set

    /** Issue #49: haptics is a persisted fact too (born from
     *  [SoundEffectsSetting.isHapticsEnabled], pushed like sound),
     *  not the unpersisted always-true stub this used to be. */
    @Volatile
    var hapticsEnabled: Boolean = SoundEffectsSetting.isHapticsEnabled(context)
        internal set

    /** Re-read the persisted sound fact into this engine — the
     *  only sanctioned per-engine refresh (bind-time second lock). */
    fun resyncSoundEnabledFromFact() {
        soundEnabled = SoundEffectsSetting.isEnabled(context)
    }

    // =====================================================================
    // THE ROOM'S TWO WORDS (issue #150 — sound-in-quiet-places).
    // Until now the only gate any playback path read was the app's
    // own soundEnabled: silent mode, vibrate and Do Not Disturb
    // changed nothing this engine consulted, so timer-fired voices
    // (break flips, the finish cascade, routine seals) spoke into
    // quiet rooms at fixed gain, and interactive knocks sounded on
    // a phone its owner had explicitly silenced. Two system facts
    // are now read at every playback gate:
    //
    //  1. THE RINGER'S WORD BINDS EVERY VOICE. Ringer mode SILENT
    //     or VIBRATE silences ALL audio this engine makes. The
    //     haptic twin still fires — S0's law is untouched (muting
    //     loses timbre, never the event); the event is carried as
    //     touch, exactly what vibrate mode asks the phone for.
    //
    //  2. DO NOT DISTURB BINDS UNATTENDED VOICES. A sound nobody
    //     is touching the phone to hear (break boundaries, the
    //     finish cascade — call sites pass unattended = true) is
    //     additionally suppressed whenever an interruption filter
    //     other than ALL is in force. Interactive voices under
    //     DND remain the user's own act, gated by the ringer only.
    //
    // A lookup failure reads as "not silenced": a fact the engine
    // cannot read must never invent a silence of its own.
    // =====================================================================

    /** True when the ringer's word is "make no sound" (SILENT or
     *  VIBRATE). Binds every audio path in this engine. */
    internal fun ringerSilencesAudio(): Boolean = try {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        am != null && am.ringerMode != AudioManager.RINGER_MODE_NORMAL
    } catch (_: Exception) {
        false
    }

    /** True when Do Not Disturb bars interruptions (any filter
     *  other than INTERRUPTION_FILTER_ALL). Binds unattended
     *  voices only. */
    internal fun doNotDisturbBarsUnattended(): Boolean = try {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        nm != null &&
            nm.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
    } catch (_: Exception) {
        false
    }

    /** The audio gate, whole: the app's own fact, the ringer's
     *  word, and — for unattended voices — DND's word. */
    private fun audioPermitted(unattended: Boolean): Boolean =
        soundEnabled && !ringerSilencesAudio() &&
            !(unattended && doNotDisturbBarsUnattended())
    private val commandQueue = LinkedBlockingQueue<SynthCommand>(64)

    @Volatile
    private var running = true

    private var audioFocusRequest: AudioFocusRequest? = null

    @Volatile
    private var audioFocusHeld = false

    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener { change ->
        when (change) {
            AudioManager.AUDIOFOCUS_GAIN -> audioFocusHeld = true
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> audioFocusHeld = false
        }
    }

    // Issue #49: threads are LAZY — construction alone starts
    // nothing and opens no AudioTrack. The first cue that actually
    // needs a lane starts it; an engine that is never played (the
    // repository's lazy break engine in a process that never
    // crosses a break) costs no threads and no track, ever.
    @Volatile
    private var synthThread: Thread? = null
    private val threadStartLock = Any()

    private fun ensureThreadsStarted() {
        if (synthThread != null && sealRenderThread != null) return
        synchronized(threadStartLock) {
            if (!running) return
            if (synthThread == null) {
                synthThread = Thread({ runFloatPcmSynthLoop() }, "MonolithFloatPcmSynth").apply {
                    isDaemon = true
                    priority = Thread.MAX_PRIORITY
                    start()
                }
            }
            if (sealRenderThread == null) {
                sealRenderThread = Thread({ runSealRenderLoop() }, "MonolithSealRender").apply {
                    isDaemon = true
                    start()
                }
            }
        }
    }

    // =====================================================================
    // THE SEAL LANE (Riegel §1): the Throw never enters the serial
    // queue, never waits, never mixes. Its own retained buffer and
    // its own AudioTrack; pre-rendered at prime (the latch), so at
    // t=0 the whole audio cost is one play() call. Preempt is three
    // layers: the queue purge, the barrier drop in the synth loop,
    // and the legacy track's pause+flush under a lane epoch.
    // =====================================================================

    private val seqCounter = java.util.concurrent.atomic.AtomicLong(0L)
    private val laneEpoch = java.util.concurrent.atomic.AtomicInteger(0)

    @Volatile
    private var legacyTrack: AudioTrack? = null

    private val sealBuffer = FloatArray(SEAL_SAMPLES)
    @Volatile
    private var sealTrack: AudioTrack? = null

    /** Issue #77: guards sealTrack's check-then-act creation and its
     *  teardown. Concurrent first use used to build two AudioTracks —
     *  one overwritten, leaked, and never released; teardown nulling
     *  the reference mid-creation handed the creator a released
     *  instance. */
    private val sealTrackLock = Any()
    @Volatile
    private var sealReady: Boolean = false
    @Volatile
    private var sealBarrierSeq: Long = -1L

    private val sealPrimeSignal = LinkedBlockingQueue<Unit>(1)

    @Volatile
    private var sealRenderThread: Thread? = null

    init {
        // Truth Car 3 (UNA-03): join the live registry at
        // construction, so a fact written at any moment after
        // construction reaches this engine. Issue #49: threads are
        // NOT started here — see ensureThreadsStarted().
        registerLiveEngine(this)
    }

    /** Test seam (issue #49): have this engine's lanes started? */
    internal fun threadsStartedForTest(): Boolean = synthThread != null

    /** The prime worker: renders the Throw into the retained
     *  buffer and writes it whole into the seal track (stopped),
     *  inside the latch→t=0 window that has nothing else to do.
     *  Only then does sealReady stand. */
    private fun runSealRenderLoop() {
        while (running) {
            try {
                sealPrimeSignal.poll(500, TimeUnit.MILLISECONDS) ?: continue
                val track = ensureSealTrack() ?: continue
                try {
                    track.pause()
                    track.flush()
                } catch (_: Exception) {}
                renderSealBuffer(sealBuffer)
                var off = 0
                var failed = false
                while (off < SEAL_SAMPLES) {
                    val n = try {
                        track.write(sealBuffer, off, SEAL_SAMPLES - off, AudioTrack.WRITE_BLOCKING)
                    } catch (_: Exception) {
                        -1
                    }
                    if (n <= 0) {
                        failed = true
                        break
                    }
                    off += n
                }
                if (!failed && off == SEAL_SAMPLES) {
                    sealReady = true
                }
            } catch (_: InterruptedException) {
                if (!running) return
            } catch (_: Exception) {
                // Headless fallback: sealReady stays false and
                // playSealThrow takes its synchronous path.
            }
        }
    }

    private fun ensureSealTrack(): AudioTrack? {
        sealTrack?.let { return it }
        // Issue #77: double-checked under sealTrackLock — the render
        // thread and playSealThrow's fallback both arrive here; only
        // one may build the track.
        return synchronized(sealTrackLock) {
            sealTrack?.let { return@synchronized it }
            try {
            val track = AudioTrack.Builder()
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
                // The render plus 5% headroom (185 220 B): a track
                // buffer smaller than the render is the one way
                // this lane could silently become a queue again.
                .setBufferSizeInBytes(SEAL_SAMPLES * 4 * 105 / 100)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
                sealTrack = track
                track
            } catch (_: Exception) {
                null
            }
        }
    }

    // =====================================================================
    // THE SOUNDSCAPE LANE (#253 line 1): the session's one
    // continuous voice. Its own retained loop buffer, its own
    // AudioTrack, its own writer thread — the seal lane's shape,
    // at ambient weight. The lane obeys the house laws:
    //  - THE FACT GATES IT: [startSoundscape] reads
    //    [SoundscapeSetting] at every call; OFF means silence.
    //  - THE PERMISSION LAW GATES IT: it sounds only while
    //    [audioPermitted] holds (sound fact + ringer), re-checked
    //    every writer block, so a mid-session ringer flip or a
    //    sound-fact push fades it out by the same ramp.
    //  - THE FADE RULES GOVERN ITS EDGES: gain ramps over
    //    [SoundscapeSynth.FADE_SAMPLES] in and out; it never
    //    starts or stops with a click.
    //  - THE NIGHT GATE SCALES IT: [nightGainScale] (ordinary
    //    voice) applies per block, like every transient.
    // =====================================================================

    @Volatile
    private var soundscapeBuffer: FloatArray? = null

    @Volatile
    private var soundscapeTrack: AudioTrack? = null
    private val soundscapeTrackLock = Any()

    @Volatile
    private var soundscapeThread: Thread? = null

    /** The user's standing wish, set by start/stop. The writer
     *  loop owns the gain; this flag is only the direction. */
    @Volatile
    private var soundscapeDesired: Boolean = false

    private var soundscapeFocusRequest: AudioFocusRequest? = null

    @Volatile
    private var soundscapeFocusHeld: Boolean = false

    /** Start the session soundscape. No-ops unless the persisted
     *  fact is ON and audio is permitted; the ViewModel calls
     *  this on the session's rising edge and the fact — not the
     *  caller — is the authority. Idempotent. */
    fun startSoundscape() {
        if (!running) return
        if (!SoundscapeSetting.isEnabled(context)) return
        if (!audioPermitted(unattended = false)) return
        if (soundscapeBuffer == null) {
            soundscapeBuffer = SoundscapeSynth.renderLoop()
        }
        soundscapeDesired = true
        ensureSoundscapeThread()
    }

    /** Stop the session soundscape: the writer fades it out by
     *  the fade rule, then parks the track and abandons the
     *  lane's focus. Idempotent. */
    fun stopSoundscape() {
        soundscapeDesired = false
    }

    /** Test seam: is the lane's wish currently ON? */
    internal fun soundscapeDesiredForTest(): Boolean = soundscapeDesired

    private fun ensureSoundscapeThread() {
        if (soundscapeThread != null) return
        synchronized(threadStartLock) {
            if (!running || soundscapeThread != null) return
            soundscapeThread = Thread(
                { runSoundscapeLoop() }, "MonolithSoundscape"
            ).apply {
                isDaemon = true
                start()
            }
        }
    }

    private fun ensureSoundscapeTrack(): AudioTrack? {
        soundscapeTrack?.let { return it }
        return synchronized(soundscapeTrackLock) {
            soundscapeTrack?.let { return@synchronized it }
            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                            .setSampleRate(SoundscapeSynth.SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(SOUNDSCAPE_BLOCK_SAMPLES * 4 * 4)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
                soundscapeTrack = track
                track
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun requestSoundscapeFocus(): Boolean {
        if (soundscapeFocusHeld) return true
        return try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                ?: return false
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val req = soundscapeFocusRequest ?: AudioFocusRequest.Builder(
                    AudioManager.AUDIOFOCUS_GAIN
                ).setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                ).setOnAudioFocusChangeListener { change ->
                    if (change == AudioManager.AUDIOFOCUS_LOSS ||
                        change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                    ) {
                        soundscapeFocusHeld = false
                        soundscapeDesired = false
                    }
                }.build()
                    .also { soundscapeFocusRequest = it }
                am.requestAudioFocus(req)
            } else {
                @Suppress("DEPRECATION")
                am.requestAudioFocus(
                    { change ->
                        if (change == AudioManager.AUDIOFOCUS_LOSS ||
                            change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                        ) {
                            soundscapeFocusHeld = false
                            soundscapeDesired = false
                        }
                    },
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
            }
            soundscapeFocusHeld = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            soundscapeFocusHeld
        } catch (_: Exception) {
            false
        }
    }

    private fun abandonSoundscapeFocus() {
        try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                soundscapeFocusRequest?.let { am.abandonAudioFocusRequest(it) }
            } else {
                @Suppress("DEPRECATION")
                am.abandonAudioFocus { }
            }
        } catch (_: Exception) {}
        soundscapeFocusHeld = false
    }

    private fun runSoundscapeLoop() {
        val block = FloatArray(SOUNDSCAPE_BLOCK_SAMPLES)
        var pos = 0
        var fadeElapsed = 0
        var trackLive = false
        while (running) {
            try {
                // The permission law, re-read every pass: a
                // ringer flip or a live sound-fact push retracts
                // the wish and the ramp carries it out.
                if (soundscapeDesired &&
                    !(soundEnabled && !ringerSilencesAudio())
                ) {
                    soundscapeDesired = false
                }
                if (!soundscapeDesired && fadeElapsed <= 0) {
                    if (trackLive) {
                        try {
                            soundscapeTrack?.pause()
                            soundscapeTrack?.flush()
                        } catch (_: Exception) {}
                        trackLive = false
                        abandonSoundscapeFocus()
                    }
                    Thread.sleep(200)
                    continue
                }
                val buffer = soundscapeBuffer ?: run {
                    Thread.sleep(200)
                    continue
                }
                val track = ensureSoundscapeTrack() ?: run {
                    Thread.sleep(200)
                    continue
                }
                if (!trackLive) {
                    if (!requestSoundscapeFocus()) {
                        Thread.sleep(500)
                        continue
                    }
                    try {
                        track.play()
                    } catch (_: Exception) {
                        Thread.sleep(200)
                        continue
                    }
                    trackLive = true
                }
                // Advance the fade toward the wish, then write
                // one block under the resulting gain.
                fadeElapsed = if (soundscapeDesired) {
                    (fadeElapsed + SOUNDSCAPE_BLOCK_SAMPLES)
                        .coerceAtMost(SoundscapeSynth.FADE_SAMPLES)
                } else {
                    (fadeElapsed - SOUNDSCAPE_BLOCK_SAMPLES)
                        .coerceAtLeast(0)
                }
                val gain = SoundscapeSynth.fadeGain(fadeElapsed) *
                    nightGainScale(sealVoice = false) * SOUNDSCAPE_MASTER_GAIN
                if (gain <= 0f && !soundscapeDesired) continue
                for (i in block.indices) {
                    block[i] = buffer[pos] * gain
                    pos++
                    if (pos >= buffer.size) pos = 0
                }
                var off = 0
                while (off < block.size) {
                    val n = try {
                        track.write(block, off, block.size - off, AudioTrack.WRITE_BLOCKING)
                    } catch (_: Exception) {
                        -1
                    }
                    if (n <= 0) break
                    off += n
                }
            } catch (_: InterruptedException) {
                if (!running) return
            } catch (_: Exception) {
                // Headless fallback: the lane idles and retries;
                // a soundscape must never take the engine down.
                try {
                    Thread.sleep(200)
                } catch (_: InterruptedException) {
                    if (!running) return
                }
            }
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
            val floatBuffer = FloatArray(maxSamples)
            val weights = PARTIAL_WEIGHTS
            val weightNorm = weights.sum()
            var lastSeenEpoch = laneEpoch.get()

            while (running) {
                val cmd = commandQueue.poll(500, TimeUnit.MILLISECONDS) ?: continue
                // Riegel's barrier: a command stamped before the
                // seal prime is pre-latch backlog — dropped here
                // even if it was polled a microsecond before the
                // purge, so it can never delay the Throw.
                val barrier = sealBarrierSeq
                if (barrier >= 0L && cmd.seq < barrier) continue
                // Re-read at play time, not only at enqueue: a
                // phone silenced after the command was queued
                // still does not speak (issue #150).
                if (!soundEnabled || ringerSilencesAudio()) continue

                // Issue #49: focus is a real gate, not theatre —
                // a denied request drops this incidental cue, and
                // the AudioTrack is built only now, for a command
                // that will actually sound (never at thread start).
                if (!requestTransientAudioFocus()) continue
                if (track == null) {
                    track = try {
                        AudioTrack.Builder()
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
                            .also { it.play(); legacyTrack = it }
                    } catch (_: Exception) {
                        null
                    }
                    if (track == null) continue
                }
                val liveTrack = track ?: continue

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

                // Chunked writes under the lane epoch (Riegel
                // §1.3 layer 3): if the Throw preempted while this
                // command was being written, the writer abandons
                // it within one 1024-sample chunk; the loop owns
                // the resume — flush once more, then play — so no
                // stale sample can be PLAYED after t=0.
                val epochAtStart = laneEpoch.get()
                if (epochAtStart != lastSeenEpoch) {
                    try {
                        liveTrack.flush()
                        liveTrack.play()
                    } catch (_: Exception) {}
                    lastSeenEpoch = epochAtStart
                }
                var off = 0
                while (off < count) {
                    if (laneEpoch.get() != epochAtStart) break
                    val n = try {
                        liveTrack.write(
                            floatBuffer, off, minOf(1024, count - off),
                            AudioTrack.WRITE_BLOCKING
                        )
                    } catch (_: Exception) {
                        -1
                    }
                    if (n <= 0) break
                    off += n
                }
            }
        } catch (_: Exception) {
            // Safe fallback in headless unit test / Robolectric JVM
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Exception) {}
            if (legacyTrack === track) legacyTrack = null
        }
    }

    /**
     * Issue #49: audio focus, honestly held. Returns whether focus
     * is granted; the result is honoured by callers (a denied
     * incidental cue is dropped), a change listener tracks loss,
     * the pre-O path (minSdk 24–25) uses the legacy overload, and
     * [abandonAudioFocus] is called at teardown. Before this, the
     * result was discarded, no listener existed, API 24–25 got
     * nothing, and focus was never abandoned.
     */
    private fun requestTransientAudioFocus(): Boolean {
        if (audioFocusHeld) return true
        return try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                ?: return false
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val req = audioFocusRequest ?: AudioFocusRequest.Builder(
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                ).setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                ).setOnAudioFocusChangeListener(focusChangeListener).build()
                    .also { audioFocusRequest = it }
                am.requestAudioFocus(req)
            } else {
                @Suppress("DEPRECATION")
                am.requestAudioFocus(
                    focusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                )
            }
            audioFocusHeld = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            audioFocusHeld
        } catch (_: Exception) {
            false
        }
    }

    private fun abandonAudioFocus() {
        try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { am.abandonAudioFocusRequest(it) }
            } else {
                @Suppress("DEPRECATION")
                am.abandonAudioFocus(focusChangeListener)
            }
        } catch (_: Exception) {}
        audioFocusHeld = false
    }

    private fun resolveVibrator(): Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Exception) {
        null
    }

    /**
     * Waveform haptic with an amplitude envelope (Ren's revamp).
     * [timingsMs] / [amplitudes] follow VibrationEffect.createWaveform:
     * segment 0 is the lead-in (amplitude 0). Falls back to a plain
     * one-shot of [fallbackMs] before API 26.
     */
    private fun triggerWaveform(timingsMs: LongArray, amplitudes: IntArray, fallbackMs: Long) {
        if (!running) return // issue #49: a torn-down engine is silent, haptics included
        if (!hapticsEnabled) return
        try {
            val vibrator = resolveVibrator()
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(timingsMs, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(fallbackMs)
                }
            }
        } catch (_: Exception) {}
    }

    /**
     * Legacy one-shot API, kept for compatibility. New feel work uses
     * the named waveform patterns below.
     */
    fun triggerOneShotHaptic(durationMs: Long) {
        if (!running) return // issue #49: dead engines stay dead
        if (!hapticsEnabled) return
        val calibratedMs = when {
            durationMs <= 1L -> HAPTIC_ROTARY_TICK_1MS
            durationMs <= 4L -> HAPTIC_SWITCH_FLIP_4MS
            durationMs <= 8L -> HAPTIC_SESSION_LOCK_8MS
            else -> HAPTIC_COMPLETION_10MS
        }
        try {
            val vibrator = resolveVibrator()
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

    /** Do Not Disturb, read live: any interruption filter other
     *  than ALL means the user (or the phone's bedtime mode) has
     *  asked the room to be quiet. Read failures read as no-DND —
     *  a broken query must never mute the app by accident. */
    private fun isDoNotDisturbActive(): Boolean = try {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as? android.app.NotificationManager
        nm != null &&
            nm.currentInterruptionFilter !=
                android.app.NotificationManager.INTERRUPTION_FILTER_ALL
    } catch (_: Exception) {
        false
    }

    /** The one night/DND gain gate (Audit R3b #116, finding 5):
     *  every transient this engine plays passes through here, so
     *  clicks, knocks, detents and the seal all obey the same law
     *  (NightSignal.gainScale): DND silences the ordinary sounds
     *  and softens the seal; clock night lowers both. The seal
     *  voice is softened, never deleted. */
    private fun nightGainScale(sealVoice: Boolean): Float =
        NightSignal.gainScale(
            night = NightSignal.isNightAt(System.currentTimeMillis()),
            doNotDisturb = isDoNotDisturbActive(),
            sealVoice = sealVoice
        )

    private fun enqueueTransient(
        fundamentalHz: Float,
        durationMs: Int,
        gain: Float,
        chordRatio: Float = 1.0f,
        sealVoice: Boolean = false,
        fullGainAlways: Boolean = false,
        unattended: Boolean = false
    ) {
        if (!running) return // issue #49: no queuing onto a dead engine
        // Issue #150: audioPermitted carries the soundEnabled fact
        // plus the ringer's word (and DND's, for unattended voices).
        if (!audioPermitted(unattended)) return
        // Sarthak's ruling (Oct 30, #14): the tear pulse is FULL at
        // all hours — night/DND softening never touches it. Its
        // call site passes fullGainAlways and bypasses the gate.
        val scale = if (fullGainAlways) 1f else nightGainScale(sealVoice)
        if (scale <= 0f) return
        ensureThreadsStarted()
        val samples = ((SAMPLE_RATE * durationMs) / 1000).coerceIn(96, 6000)
        commandQueue.offer(
            SynthCommand(
                fundamentalHz = fundamentalHz,
                durationSamples = samples,
                gain = gain * scale,
                chordRatio = chordRatio,
                seq = seqCounter.incrementAndGet()
            )
        )
    }

    /** Test seam (Siegel T2): the pending legacy command count —
     *  the Throw's queue-clear law is asserted against it. */
    internal fun pendingCommandCount(): Int = commandQueue.size

    // =========================================================================
    // 4 CALIBRATED HAPTIC + FLOAT-PCM CALL SITES (DEVELOPER-GUIDE.pdf §6 & §7)
    // =========================================================================

    /** Rotary detent: a strike that settles — 7ms at full tick, 5ms tail. */
    fun playRotaryDetent(frequencyHz: Double = 680.0) {
        triggerWaveform(
            timingsMs = longArrayOf(0, 7, 5),
            amplitudes = intArrayOf(0, 180, 70),
            fallbackMs = HAPTIC_ROTARY_TICK_1MS
        )
        enqueueTransient(
            fundamentalHz = frequencyHz.toFloat().coerceIn(280f, 1480f),
            durationMs = 6,
            gain = 0.38f
        )
    }

    /** Firm click for heavy Canvas controls (sled throws, knob detents):
     *  a hard strike, a breath, a softer seat. */
    fun playFirmClick() {
        triggerWaveform(
            timingsMs = longArrayOf(0, 13, 20, 8),
            amplitudes = intArrayOf(0, 225, 0, 135),
            fallbackMs = HAPTIC_SESSION_LOCK_8MS
        )
        enqueueTransient(
            fundamentalHz = 620f,
            durationMs = 12,
            gain = 0.5f,
            chordRatio = 1.1f
        )
    }

    /** Weighted seat knock (Vega's Loaded Wheel): the pawl stopping
     *  the flywheel. Duration and strike amplitude are parameters,
     *  not an effect choice - 16ms @ 0.85 unloaded rising to 24ms
     *  @ 1.0 at eight blocks - and the thock's pitch drops as the
     *  mass rises. The same voice at other weights speaks the
     *  strait's gate landings (FLOW shore 14ms @ 0.8, toll 10ms
     *  @ 0.5). */
    fun playSeatKnock(
        durationMs: Int,
        amplitude01: Float,
        pitchHz: Float = 300f,
        unattended: Boolean = false
    ) {
        val amp = (amplitude01.coerceIn(0f, 1f) * 255f).toInt()
        val strike = durationMs.coerceIn(6, 32).toLong()
        triggerWaveform(
            timingsMs = longArrayOf(0, strike, strike / 2),
            amplitudes = intArrayOf(0, amp, (amp * 0.35f).toInt()),
            fallbackMs = strike
        )
        enqueueTransient(
            fundamentalHz = pitchHz.coerceIn(160f, 420f),
            durationMs = (durationMs + 6).coerceIn(10, 44),
            gain = 0.22f + 0.3f * amplitude01.coerceIn(0f, 1f),
            chordRatio = 0.9f,
            unattended = unattended
        )
    }

    /** Two-stage latch: strike, gap, seat. Engaging snaps sharp;
     *  releasing lands heavier on the seat. [unattended] marks a
     *  machinery-fired latch (the break boundaries): DND then
     *  binds it too (issue #150). */
    fun playMechanicalLatch(engaged: Boolean, unattended: Boolean = false) {
        if (engaged) {
            triggerWaveform(
                timingsMs = longArrayOf(0, 11, 26, 9),
                amplitudes = intArrayOf(0, 215, 0, 150),
                fallbackMs = HAPTIC_SWITCH_FLIP_4MS
            )
        } else {
            triggerWaveform(
                timingsMs = longArrayOf(0, 9, 22, 10),
                amplitudes = intArrayOf(0, 165, 0, 195),
                fallbackMs = HAPTIC_SWITCH_FLIP_4MS
            )
        }
        enqueueTransient(
            fundamentalHz = if (engaged) 760f else 520f,
            durationMs = 16,
            gain = 0.54f,
            chordRatio = if (engaged) 1.2f else 0.9f,
            unattended = unattended
        )
    }

    /** THE BREAK-END VOICE (issue #150): the one cue a user plans
     *  around — the break is over, the next block has begun — was
     *  the plain latch: a single 16 ms click in the same voice as
     *  every settings toggle, fired once, trivially missed by a
     *  user in the kitchen while their block time already ran.
     *  This is its own voice, not a toggle's: a low double strike
     *  (knock-knock at 430 Hz, 80 ms bodies — the queue serialises
     *  the pair), heavier in the hand than any latch, and spoken
     *  by nobody else in the app. Machinery-fired by construction
     *  (the repository's break-boundary seam is its only caller),
     *  so it is born unattended: DND and the ringer bind it. */
    fun playBreakEndCue() {
        triggerWaveform(
            timingsMs = longArrayOf(0, 16, 70, 16),
            amplitudes = intArrayOf(0, 235, 0, 205),
            fallbackMs = HAPTIC_SWITCH_FLIP_4MS
        )
        enqueueTransient(
            fundamentalHz = 430f,
            durationMs = 80,
            gain = 0.60f,
            chordRatio = 0.9f,
            unattended = true
        )
        enqueueTransient(
            fundamentalHz = 430f,
            durationMs = 80,
            gain = 0.60f,
            chordRatio = 0.9f,
            unattended = true
        )
    }

    /**
     * The Held Door's arrival voice (FIX-7c): the seal's recipe at
     * door pitch — 300Hz, 44ms, gain 0.60, chordRatio 1.5 — with
     * the latch's haptics kept. Replaces the 16ms switch tick
     * (playMechanicalLatch's transient) that used to announce the
     * door while the goodbye carried the visit's heaviest sound.
     */
    fun playDoorArrival() {
        triggerWaveform(
            timingsMs = longArrayOf(0, 11, 26, 9),
            amplitudes = intArrayOf(0, 215, 0, 150),
            fallbackMs = HAPTIC_SWITCH_FLIP_4MS
        )
        enqueueTransient(
            fundamentalHz = 300f,
            durationMs = 44,
            gain = 0.60f,
            chordRatio = 1.5f
        )
    }

    /**
     * The offer arms (FIX-7 / Ton's score §5.3, Tor round 9): the
     * arrival's recipe at exactly half gain — 300Hz, 44ms, gain
     * 0.30, chordRatio 1.5 — with a softened detent haptic
     * ([0,150,60]: felt in a held hand, a tap on furniture, never
     * a second latch). Fired ONCE at the VM state transition where
     * the peek offer arms — the wall's expiry and FIX-5c's
     * held→offer resolutions alike. Silence stands everywhere
     * else on the wall: no per-second voice, no held-zero voice.
     * Night law: gain ≤ 0.60, no 255-amplitude haptic — honoured.
     */
    fun playDoorArm() {
        triggerWaveform(
            timingsMs = longArrayOf(0, 7, 5),
            amplitudes = intArrayOf(0, 150, 60),
            fallbackMs = HAPTIC_ROTARY_TICK_1MS
        )
        enqueueTransient(
            fundamentalHz = 300f,
            durationMs = 44,
            gain = 0.30f,
            chordRatio = 1.5f
        )
    }

    /**
     * RETURN's press (FIX-7d, Ton §5.4): 300Hz, 28ms, gain 0.48,
     * chordRatio 0.9 — the engine's RELEASE interval — with the
     * releasing latch's waveform verbatim. This REPLACES
     * playHarmonicSeal(true) on the airlock return path: the
     * seal's voice (0.62 / 48ms, the visit's heaviest sound) is
     * never spent on a goodbye again.
     */
    fun playDoorReturn() {
        triggerWaveform(
            timingsMs = longArrayOf(0, 9, 22, 10),
            amplitudes = intArrayOf(0, 165, 0, 195),
            fallbackMs = HAPTIC_SWITCH_FLIP_4MS
        )
        enqueueTransient(
            fundamentalHz = 300f,
            durationMs = 28,
            gain = 0.48f,
            chordRatio = 0.9f
        )
    }

    /**
     * The peek taken (FIX-7, Ton §5.5): 300Hz, 32ms, gain 0.50,
     * chordRatio 1.5 — the BINDING interval, because taking the
     * peek is the visit's second binding (a lease granted, a
     * cooldown armed) — with the engaging latch's waveform
     * verbatim, shared with arrival: the same hand-motion, a lock
     * turning. Replaces the generic playMechanicalLatch(true) at
     * the grant, which sounded like every switch in the app. A
     * REFUSED grant stays silent — this fires only past the
     * grant's re-derivation.
     */
    fun playDoorPeek() {
        triggerWaveform(
            timingsMs = longArrayOf(0, 11, 26, 9),
            amplitudes = intArrayOf(0, 215, 0, 150),
            fallbackMs = HAPTIC_SWITCH_FLIP_4MS
        )
        enqueueTransient(
            fundamentalHz = 300f,
            durationMs = 32,
            gain = 0.50f,
            chordRatio = 1.5f
        )
    }

    /**
     * The exit to Disarm (FIX-7, Ton §5.6): 240Hz — the score's
     * one pitch step down (300 × 0.8) — 40ms, gain 0.42 (the
     * quietest press-beat: gravity is duration and seat, not
     * volume), chordRatio 0.9, with the releasing latch's
     * waveform verbatim. Fired on the CONFIRMED Disarm handoff
     * from the door only, and it is that handoff's ONLY voice:
     * the door route never also spends requestDisarmChallenge's
     * latch. KEEP FOCUSING's peel-back stays silent.
     */
    fun playDoorExit() {
        triggerWaveform(
            timingsMs = longArrayOf(0, 9, 22, 10),
            amplitudes = intArrayOf(0, 165, 0, 195),
            fallbackMs = HAPTIC_SWITCH_FLIP_4MS
        )
        enqueueTransient(
            fundamentalHz = 240f,
            durationMs = 40,
            gain = 0.42f,
            chordRatio = 0.9f
        )
    }

    /** Break-end summons (Audit R3b #146): the voice that calls the
     *  user BACK from a Safe Break. The single latch was functionally
     *  silence in a pocket — this is three full strikes with a low
     *  body tone, unmistakably a summons and not a boundary tick. It
     *  fires once, at the expiry flip; repetition lives in the
     *  summons notification, not in nagging the buzzer. */
    fun playBreakEndSummons() {
        triggerWaveform(
            timingsMs = longArrayOf(0, 32, 80, 32, 80, 32),
            amplitudes = intArrayOf(0, 255, 0, 255, 0, 230),
            fallbackMs = HAPTIC_SESSION_LOCK_8MS
        )
        // Issue #150: the summons is machinery-fired while the user
        // is away from the phone — unattended by definition, so the
        // ringer's word and Do Not Disturb bind it in the gate.
        enqueueTransient(
            fundamentalHz = 320f,
            durationMs = 64,
            gain = 0.7f,
            chordRatio = 0.9f,
            unattended = true
        )
    }

    /** T-minus warning knock (Audit R3b #146/#148): two soft strikes,
     *  felt in a pocket, clearly a warning and not an ending — fired
     *  once when a countdown (break or block) crosses 60 seconds
     *  remaining. */
    fun playBreakWarningKnock() {
        triggerWaveform(
            timingsMs = longArrayOf(0, 14, 44, 14),
            amplitudes = intArrayOf(0, 200, 0, 170),
            fallbackMs = HAPTIC_SWITCH_FLIP_4MS
        )
        // Issue #150: machinery-fired T-minus-60 warning — unattended,
        // so the ringer and Do Not Disturb bind it in the gate.
        enqueueTransient(
            fundamentalHz = 520f,
            durationMs = 16,
            gain = 0.4f,
            chordRatio = 0.9f,
            unattended = true
        )
    }

    /** Session seal clunk: full-force thunk, a settling shoulder,
     *  then the lock seating. Disarming is the same shape, lighter. */
    fun playHarmonicSeal(arming: Boolean) {
        if (arming) {
            triggerWaveform(
                timingsMs = longArrayOf(0, 24, 38, 16),
                amplitudes = intArrayOf(0, 255, 110, 175),
                fallbackMs = HAPTIC_SESSION_LOCK_8MS
            )
        } else {
            triggerWaveform(
                timingsMs = longArrayOf(0, 18, 30, 12),
                amplitudes = intArrayOf(0, 200, 90, 140),
                fallbackMs = HAPTIC_SESSION_LOCK_8MS
            )
        }
        enqueueTransient(
            fundamentalHz = if (arming) 360f else 480f,
            durationMs = 48,
            gain = 0.62f,
            chordRatio = 1.5f,
            // The seal voice: night/DND soften it (0.65 / 0.35),
            // they never silence it.
            sealVoice = true
        )
    }

    /**
     * Prime the seal lane (Orson §4.2 / Riegel §1.2), called by
     * the transition AT LATCH — inside the cadence, never at t=0:
     * the audio-focus transaction is paid here, the barrier is
     * captured (the latch knock was fired before this call, so
     * its seq is ≤ the barrier and lawfully survives), pre-latch
     * backlog still pending is purged, and the render thread is
     * signalled to pre-render + pre-write the Throw.
     */
    fun primeForSeal() {
        if (!running) return // issue #49: no priming a dead engine
        ensureThreadsStarted()
        requestTransientAudioFocus()
        val barrier = seqCounter.get()
        sealBarrierSeq = barrier
        commandQueue.removeIf { it.seq < barrier }
        sealReady = false
        sealPrimeSignal.offer(Unit)
    }

    /** A primed seal that never fires (annulment, refusal after
     *  latch) must not leave a barrier standing over the queue. */
    fun cancelSealPrime() {
        sealReady = false
        sealBarrierSeq = -1L
        sealPrimeSignal.poll()
    }

    /**
     * THE THROW (B-5): the one seal voice, zero parameters — no
     * provenance, no duration, no clock can enter it, so two
     * seals cannot differ. Fired ONLY by SessionSealTransition,
     * as the seal act's last statement. Haptic on the caller's
     * thread; audio off the dedicated lane: queue purge, legacy
     * pause+flush under a new epoch, then one play(). If the
     * prime was missed, the same pure render runs synchronously
     * and streams in behind a play() that starts the landmarks on
     * time — degraded, never silent, never queued. With sound
     * off: haptic only (S0 — muting loses timbre, never the event).
     */
    fun playSealThrow(unattended: Boolean = false) {
        if (!running) return // issue #49: a stopped engine cannot be resurrected by the Throw
        triggerWaveform(
            timingsMs = SEAL_THROW_TIMINGS,
            amplitudes = SEAL_THROW_AMPLITUDES,
            fallbackMs = HAPTIC_SESSION_LOCK_8MS
        )
        // Issue #150: the ringer's word binds the Throw's audio
        // too — a phone on SILENT or VIBRATE seals in touch alone
        // (the waveform above already fired; the event is never
        // lost, only unspoken). Provenance is now plumbed
        // (recovery closure): the transition's proving doors
        // (routine heartbeat, relaunch recovery) pass
        // unattended = true, so Do Not Disturb binds those Throws;
        // the Deck's own arm cascade passes false — that Throw is
        // the user's own act, gated by the ringer alone.
        if (!audioPermitted(unattended = unattended)) {
            sealReady = false
            sealBarrierSeq = -1L
            return
        }
        commandQueue.clear()
        laneEpoch.incrementAndGet()
        try {
            legacyTrack?.pause()
            legacyTrack?.flush()
        } catch (_: Exception) {}
        val track = ensureSealTrack()
        if (track != null) {
            // Night/DND softening for THE THROW (#116 finding 5):
            // the rendered buffer stays bit-identical (B-5's law —
            // one voice, no clock in the render); only the track's
            // playback volume obeys the night gain law, so a 2 AM
            // seal speaks at 0.65 (0.35 under DND) instead of full
            // daytime gain. The voice is never deleted.
            try {
                track.setVolume(nightGainScale(sealVoice = true))
            } catch (_: Exception) {}
            // Issue #49: the un-primed fallback previously played
            // with NO focus request at all. The Throw is the seal
            // act itself (Q6: one voice, settled), so focus is
            // requested here and the act proceeds — requested,
            // tracked, and abandoned at teardown like every cue.
            if (!audioFocusHeld) requestTransientAudioFocus()
            if (sealReady) {
                try {
                    track.play()
                } catch (_: Exception) {}
            } else {
                // Issue #77: this fallback used to render into the
                // SAME sealBuffer the render thread streams from —
                // two threads rendering into and reading from one
                // array garble the bit-identical Throw (the golden
                // hash exists precisely to forbid two voices). The
                // fallback renders into its own buffer now.
                val fallbackBuffer = FloatArray(SEAL_SAMPLES)
                renderSealBuffer(fallbackBuffer)
                try {
                    track.pause()
                    track.flush()
                    track.play()
                    var off = 0
                    while (off < SEAL_SAMPLES) {
                        val n = track.write(
                            fallbackBuffer, off, minOf(4096, SEAL_SAMPLES - off),
                            AudioTrack.WRITE_BLOCKING
                        )
                        if (n <= 0) break
                        off += n
                    }
                } catch (_: Exception) {}
            }
        }
        sealReady = false
        sealBarrierSeq = -1L
    }

    /** The counterfoil tear's own voice (F-N2): the tear is not a
     *  completion and must not spend the completion pulse. Same
     *  body family, a different chord (1.21 against the pulse's
     *  1.33) and a shorter, ripping haptic — near enough to read
     *  as paper, far enough to never be mistaken for the finish. */
    fun playTearPulse() {
        triggerWaveform(
            timingsMs = longArrayOf(0, 8, 14, 8),
            amplitudes = intArrayOf(0, 160, 0, 110),
            fallbackMs = HAPTIC_ROTARY_TICK_1MS
        )
        enqueueTransient(
            fundamentalHz = 240f,
            durationMs = 40,
            gain = 0.68f,
            chordRatio = 1.21f,
            // FULL at all hours (Sarthak's Oct 30 ruling, #14):
            // the tear never takes the night/DND gain gate.
            fullGainAlways = true
        )
    }

    /** Completion: three ticks that soften as they land —
     *  the machine winding down, not alarming. */
    fun playCompletionPulse(unattended: Boolean = false) {
        triggerWaveform(
            timingsMs = longArrayOf(0, 10, 24, 10, 24, 14),
            amplitudes = intArrayOf(0, 175, 0, 125, 0, 80),
            fallbackMs = HAPTIC_COMPLETION_10MS
        )
        enqueueTransient(
            fundamentalHz = 185f,
            durationMs = 64,
            gain = 0.68f,
            chordRatio = 1.33f,
            unattended = unattended
        )
    }

    /**
     * Issue #49: stop ALL audio — and haptics, focus, threads and
     * registry membership with it. Before this, the legacy track,
     * the audio focus, both threads and the live-engine registry
     * all escaped, and the "stopped" engine could still vibrate
     * and resurrect a seal track. Now: [running] gates every
     * public entry point, both tracks are stopped/released, focus
     * is abandoned, vibration is cancelled, both threads are
     * interrupted (their 500 ms polls wake at once), and the
     * engine leaves the live registry. Idempotent; teardown is
     * final — a stopped engine is never restarted.
     */
    fun stopAllAudio() {
        running = false
        // #253 line 1: the soundscape lane dies with the engine —
        // wish retracted, focus abandoned, track released under
        // its lock, thread interrupted out of its poll.
        soundscapeDesired = false
        abandonSoundscapeFocus()
        synchronized(soundscapeTrackLock) {
            try {
                soundscapeTrack?.stop()
                soundscapeTrack?.release()
            } catch (_: Exception) {}
            soundscapeTrack = null
        }
        soundscapeThread?.interrupt()
        commandQueue.clear()
        sealPrimeSignal.clear()
        sealBarrierSeq = -1L
        sealReady = false
        abandonAudioFocus()
        try {
            legacyTrack?.pause()
            legacyTrack?.flush()
            legacyTrack?.stop()
            legacyTrack?.release()
        } catch (_: Exception) {}
        legacyTrack = null
        // Issue #77: teardown takes sealTrackLock too — nulling the
        // track mid-creation handed the creator a released instance.
        synchronized(sealTrackLock) {
            try {
                sealTrack?.stop()
                sealTrack?.release()
            } catch (_: Exception) {}
            sealTrack = null
        }
        try {
            resolveVibrator()?.cancel()
        } catch (_: Exception) {}
        synthThread?.interrupt()
        sealRenderThread?.interrupt()
        unregisterLiveEngine(this)
    }
}
