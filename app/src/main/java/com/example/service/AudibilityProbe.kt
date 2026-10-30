package com.example.service

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager

/**
 * Issue #112 (Audit R3, sound-haptics-lived, finding 1): THE
 * AUDIBILITY FACT. Until this file, nothing in the app ever asked
 * whether a phone that prints SOUND: ON can actually be HEARD:
 * the engine synthesised into a fixed-gain AudioTrack and PARK
 * printed the setting, so ON and OFF were indistinguishable on a
 * muted phone — the setting was true and the experience was a lie.
 *
 * What actually gates THIS engine's voice, checked one by one:
 *  - STREAM_MUSIC volume: the engine's tracks carry
 *    USAGE_ASSISTANCE_SONIFICATION, which the platform routes to
 *    the music stream. Volume 0 ⇒ nothing can be heard, whatever
 *    the setting says.
 *  - Do Not Disturb: INTERRUPTION_FILTER_NONE silences every
 *    stream; INTERRUPTION_FILTER_ALARMS admits alarms only and
 *    mutes media by policy. Both ⇒ inaudible.
 *  - Ringer mode: SILENT / VIBRATE gate the RING and SYSTEM
 *    streams; they do NOT mute the music stream on stock Android,
 *    so they are read, classified AUDIBLE, and deliberately NOT
 *    printed as silence — a warning that fires when sound still
 *    plays would be the same lie in the other direction.
 *  - Output routing: a routed output (headphones, Bluetooth)
 *    still reaches the user's ears; routing is not a silence
 *    cause and is not treated as one.
 *
 * The classification is a pure function of three ints so the
 * whole truth table is unit-testable off-device; [read] is the
 * only Android-touching door.
 */
enum class SoundAudibility {
    AUDIBLE,
    SILENT_VOLUME,
    SILENT_DND;

    fun isAudible(): Boolean = this == AUDIBLE
}

object AudibilityProbe {

    /** The truth table. [ringerMode] is read for the record (see
     *  the class doc: it cannot silence this engine's stream, so
     *  it never decides the result). */
    fun classify(
        ringerMode: Int,
        musicStreamVolume: Int,
        interruptionFilter: Int
    ): SoundAudibility {
        @Suppress("UNUSED_VARIABLE") val ringerRead = ringerMode
        if (musicStreamVolume <= 0) return SoundAudibility.SILENT_VOLUME
        if (interruptionFilter == NotificationManager.INTERRUPTION_FILTER_NONE ||
            interruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALARMS
        ) {
            return SoundAudibility.SILENT_DND
        }
        return SoundAudibility.AUDIBLE
    }

    /** Read the phone as it stands. Every service read is
     *  guarded: a service that cannot be read (or throws, as
     *  shadows and OEM builds occasionally do) answers with its
     *  AUDIBLE component — an unreadable phone must not print a
     *  silence it cannot prove, and a probe must never crash the
     *  surface that prints it. */
    fun read(context: Context): SoundAudibility {
        val appContext = context.applicationContext
        val audio = appContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            ?: return SoundAudibility.AUDIBLE
        val notifications =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val filter = runCatching { notifications?.currentInterruptionFilter }
            .getOrNull() ?: NotificationManager.INTERRUPTION_FILTER_ALL
        val volume = runCatching { audio.getStreamVolume(AudioManager.STREAM_MUSIC) }
            .getOrNull() ?: return SoundAudibility.AUDIBLE
        val ringer = runCatching { audio.ringerMode }.getOrNull()
            ?: AudioManager.RINGER_MODE_NORMAL
        return classify(
            ringerMode = ringer,
            musicStreamVolume = volume,
            interruptionFilter = filter
        )
    }
}
