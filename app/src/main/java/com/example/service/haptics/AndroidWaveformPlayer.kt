package com.example.service.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.service.SoundEffectsSetting

/**
 * THE ANDROID HANDS of the felt layer (FEEL-SR-5): a thin
 * adapter from the bus's [EffectiveHaptic] to the platform
 * vibrator, and the factory that reads the motor fact honestly.
 *
 * Everything above this file is pure and JVM-provable; this file
 * is the only place in the package that touches Android haptic
 * APIs, and it contains no felt design — every number it plays
 * arrived already degraded from a registry row.
 *
 * Failure posture, inherited from the engine's own discipline:
 * every platform call is guarded; a failure reads as silence for
 * that firing, never as a substitute buzz, never as a crash on
 * the caller's thread.
 *
 * UNPROVEN-ON-DEVICE (G-7): [motorProfile] reports what the
 * platform APIs claim; whether the motor renders it so is a
 * Slice 7 in-hand judgement, not a claim made here.
 */
class AndroidWaveformPlayer(context: Context) : WaveformPlayer {

    private val appContext = context.applicationContext

    override fun play(effective: EffectiveHaptic, event: HapticEvent) {
        val vibrator = resolveVibrator() ?: return
        try {
            if (!vibrator.hasVibrator()) return
            when (effective) {
                is EffectiveHaptic.Silent -> Unit // by law or by capability: nothing

                is EffectiveHaptic.PlayOneShot -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                effective.durationMs,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(effective.durationMs)
                    }
                }

                is EffectiveHaptic.PlayWaveform -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                            VibrationEffect.createWaveform(
                                effective.timingsMs.toLongArray(),
                                effective.amplitudes.toIntArray(),
                                -1
                            )
                        )
                    } else {
                        // Unreachable by construction (LEGACY class
                        // degrades to one-shots); kept total so a
                        // future class cannot fall through silently.
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(effective.timingsMs.sum())
                    }
                }

                is EffectiveHaptic.PlayComposition -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val composition = VibrationEffect.startComposition()
                        effective.segments.forEach { segment ->
                            composition.addPrimitive(
                                primitiveConstant(segment.kind),
                                segment.scale,
                                segment.delayMs
                            )
                        }
                        vibrator.vibrate(composition.compose())
                    }
                }
            }
        } catch (_: Exception) {
            // A failed firing is a silent firing. Never a fallback
            // buzz: substituting a different felt event for the
            // one the registry specced is the drift this package
            // exists to forbid.
        }
    }

    private fun resolveVibrator(): Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Exception) {
        null
    }

    private fun primitiveConstant(kind: PrimitiveKind): Int = when (kind) {
        PrimitiveKind.CLICK -> VibrationEffect.Composition.PRIMITIVE_CLICK
        PrimitiveKind.THUD -> VibrationEffect.Composition.PRIMITIVE_THUD
        PrimitiveKind.SPIN -> VibrationEffect.Composition.PRIMITIVE_SPIN
        PrimitiveKind.TICK -> VibrationEffect.Composition.PRIMITIVE_TICK
        PrimitiveKind.LOW_TICK -> VibrationEffect.Composition.PRIMITIVE_LOW_TICK
        PrimitiveKind.QUICK_RISE -> VibrationEffect.Composition.PRIMITIVE_QUICK_RISE
        PrimitiveKind.QUICK_FALL -> VibrationEffect.Composition.PRIMITIVE_QUICK_FALL
        PrimitiveKind.SLOW_RISE -> VibrationEffect.Composition.PRIMITIVE_SLOW_RISE
    }

    companion object {

        /** The motor fact, cached per process (FEEL-SR-1 seam
         *  term 2): a profile is a property of the DEVICE, never
         *  of the moment — the same hand must meet the same door
         *  every day, and a capability probe is not paid per
         *  firing. */
        @Volatile
        private var cachedProfile: MotorProfile? = null

        /**
         * Prime the felt leg (FEEL-SR-1 R-8): resolve the
         * vibrator and the profile ahead of a ceremony whose
         * first call cannot afford cold latency — the lift's
         * twin of the seal's `primeForSeal`. Fires NOTHING.
         */
        fun prime(context: Context) {
            motorProfile(context)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = context.applicationContext
                        .getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vm?.defaultVibrator?.hasVibrator()
                } else {
                    @Suppress("DEPRECATION")
                    (context.applicationContext
                        .getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
                        ?.hasVibrator()
                }
            } catch (_: Exception) {
            }
        }

        /**
         * The motor fact, read from the live vibrator (first
         * call) and cached thereafter. The classification itself
         * is the pure [classify] below, so the boundary table is
         * JVM-provable; this function only gathers the
         * platform's answers.
         */
        fun motorProfile(context: Context): MotorProfile {
            cachedProfile?.let { return it }
            val profile = readMotorProfile(context)
            cachedProfile = profile
            return profile
        }

        private fun readMotorProfile(context: Context): MotorProfile {
            val vibrator = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = context.applicationContext
                        .getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vm?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.applicationContext
                        .getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }
            } catch (_: Exception) {
                null
            }
            val hasVibrator = try {
                vibrator?.hasVibrator() == true
            } catch (_: Exception) {
                false
            }
            val hasAmplitudeControl = try {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    vibrator?.hasAmplitudeControl() == true
            } catch (_: Exception) {
                false
            }
            val primitivesSupported = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && vibrator != null) {
                    val supported = vibrator.areAllPrimitivesSupported(
                        VibrationEffect.Composition.PRIMITIVE_THUD,
                        VibrationEffect.Composition.PRIMITIVE_CLICK,
                        VibrationEffect.Composition.PRIMITIVE_TICK
                    )
                    supported
                } else {
                    false
                }
            } catch (_: Exception) {
                false
            }
            return classify(
                sdkInt = Build.VERSION.SDK_INT,
                hasVibrator = hasVibrator,
                hasAmplitudeControl = hasAmplitudeControl,
                primitivesSupported = primitivesSupported
            )
        }

        /** The class boundary table, pure: SDK level × the
         *  vibrator's own capability answers → the honest class.
         *  A capability the platform cannot even be asked about
         *  (pre-26 amplitude, pre-30 primitives) reads as absent —
         *  honesty is never an optimistic default. */
        fun classify(
            sdkInt: Int,
            hasVibrator: Boolean,
            hasAmplitudeControl: Boolean,
            primitivesSupported: Boolean
        ): MotorProfile = MotorProfile(
            motorClass = when {
                !hasVibrator -> MotorClass.NO_MOTOR
                sdkInt < 26 -> MotorClass.LEGACY_ONESHOT
                sdkInt >= 30 && primitivesSupported -> MotorClass.COMPOSITION
                hasAmplitudeControl -> MotorClass.AMPLITUDE_WAVEFORM
                else -> MotorClass.FIXED_AMPLITUDE
            }
        )

        /**
         * A bus wired for production: the Android waveform
         * player, the motor fact re-read per firing, and the
         * persisted haptics fact as the gate. Sound and motion
         * players are installed by the surface that owns them —
         * until then they are the recorded [NoOpSoundPlayer] /
         * [NoOpMotionPlayer], and the re-point of the engine's
         * paired call sites onto this bus is the named follow-up
         * piece, not a silent half-wiring.
         */
        fun bus(
            context: Context,
            soundPlayer: SoundPlayer = NoOpSoundPlayer,
            motionPlayer: MotionPlayer = NoOpMotionPlayer
        ): SensoryBus {
            val appContext = context.applicationContext
            return SensoryBus(
                waveformPlayer = AndroidWaveformPlayer(appContext),
                soundPlayer = soundPlayer,
                motionPlayer = motionPlayer,
                profile = { motorProfile(appContext) },
                hapticsEnabled = { SoundEffectsSetting.isHapticsEnabled(appContext) }
            )
        }
    }
}
