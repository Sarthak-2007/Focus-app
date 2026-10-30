package com.example.service

import android.content.Context
import android.content.SharedPreferences

/**
 * Truth Car 3 (UNA-03): SOUND EFFECTS as a persisted FACT.
 *
 * Until this file, the setting lived only in a var on whichever
 * [TactileSoundEngine] happened to exist: `toggleSound` flipped
 * the VM's engine, PARK printed the VM's flow, and a process born
 * through the routine heartbeat — which binds the seal transition
 * with NO engine — constructed a fresh engine whose default was
 * ON. PARK could print OFF while a routine seal sounded. There
 * was no fact; there were copies.
 *
 * The fact lives here, in SharedPreferences, for one mechanical
 * reason: it must be readable SYNCHRONOUSLY, on any thread, at
 * engine construction time — the seal marker's precedent
 * (MonolithRepository.SEAL_PREFS_NAME). A Room read cannot be
 * made at a constructor on an unknown thread; a prefs read can.
 *
 * The three laws this store enforces:
 *  1. BORN TRUE-TO-FACT — every engine's `soundEnabled`
 *     initializer reads [isEnabled]; no engine anywhere in the
 *     process can start from a stale class default again.
 *  2. WRITES COMMIT — [setEnabled] uses `commit()`, never
 *     `apply()` (Car 2, G-02: a fact that must survive process
 *     death is never left in apply()'s in-memory window).
 *  3. ONE WRITE, EVERY ENGINE — [setEnabled] pushes the new
 *     value to every live engine in the same call, so the VM's
 *     engine and the seal transition's engine can never disagree
 *     after a toggle, whichever door constructed them.
 */
object SoundEffectsSetting {

    // Internal, not private: the discriminating tests write the
    // fact out-of-band (bypassing setEnabled's push) to prove the
    // bind-time re-read, which requires the literal address.
    internal const val PREFS_NAME = "monolith_sound_effects"
    internal const val KEY_ENABLED = "sound_effects_enabled"
    internal const val KEY_HAPTICS_ENABLED = "haptics_enabled"

    /** One-shot guard for the credential→device-protected
     *  migration in [prefs] (issue #83). */
    @Volatile
    private var hasMigratedSoundSetting = false

    /**
     * The one accessor for the fact's prefs file (issue #83).
     *
     * The file lives in DEVICE-PROTECTED storage: a process born
     * through direct boot (routine heartbeat before first unlock)
     * binds an engine and reads this fact pre-unlock, where a
     * credential-protected file is unreadable and the read fell
     * back to the ON default — a seal could sound over an OFF
     * fact (the split-brain the Room DB already avoided by living
     * in device-protected storage). The first access migrates the
     * legacy credential-protected file across
     * ([Context.moveSharedPreferencesFrom], guarded one-shot by
     * [hasMigratedSoundSetting]) so a stored OFF survives the
     * move; the move is best-effort — on a fresh install there is
     * nothing to move and the device-protected file is the fact
     * from birth.
     */
    private fun prefs(context: Context): SharedPreferences {
        val appContext = context.applicationContext
        val deviceContext = appContext.createDeviceProtectedStorageContext() ?: appContext
        if (!hasMigratedSoundSetting) {
            synchronized(this) {
                if (!hasMigratedSoundSetting) {
                    runCatching {
                        deviceContext.moveSharedPreferencesFrom(appContext, PREFS_NAME)
                    }
                    hasMigratedSoundSetting = true
                }
            }
        }
        return deviceContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /** The fact. Default ON — the shipped behaviour before the
     *  setting existed, and the value a fresh install prints. */
    fun isEnabled(context: Context): Boolean =
        prefs(context)
            .getBoolean(KEY_ENABLED, true)

    /** Write the fact and make every live engine agree with it in
     *  the same instant. Engines constructed later read the fact
     *  at birth (law 1); the seal transition additionally re-reads
     *  it at bind time (SessionSealTransitions.get).
     *
     *  Issue #49: the commit result is HONOURED — a failed commit
     *  pushes nothing, changes nothing, and returns false, so no
     *  surface can print a setting the disk never took (previously
     *  the Boolean was discarded and every engine flipped anyway,
     *  silently reverting at process death). Returns true only
     *  when the fact landed AND was pushed. */
    fun setEnabled(context: Context, enabled: Boolean): Boolean {
        val committed = prefs(context)
            .edit()
            .putBoolean(KEY_ENABLED, enabled)
            .commit()
        if (!committed) return false
        TactileSoundEngine.pushSoundEnabledToLiveEngines(enabled)
        return true
    }

    /** The haptics fact (issue #49). Default ON — the shipped
     *  behaviour while `hapticsEnabled` was an unpersisted stub. */
    fun isHapticsEnabled(context: Context): Boolean =
        prefs(context)
            .getBoolean(KEY_HAPTICS_ENABLED, true)

    /** Write the haptics fact with the same commit law as sound:
     *  a failed commit pushes nothing and returns false. */
    fun setHapticsEnabled(context: Context, enabled: Boolean): Boolean {
        val committed = prefs(context)
            .edit()
            .putBoolean(KEY_HAPTICS_ENABLED, enabled)
            .commit()
        if (!committed) return false
        TactileSoundEngine.pushHapticsEnabledToLiveEngines(enabled)
        return true
    }
}
