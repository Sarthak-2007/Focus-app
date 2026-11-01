package com.example.data

import android.content.Context
import android.provider.Settings

/**
 * Issue #525: the shield-raise stamp — the monotonic instant of
 * the CURRENT boot's first protecting flip of the shield.
 *
 * The reboot elapsed derivation used to credit a session with
 * the phone's ENTIRE boot uptime at the first post-reboot tick,
 * on the doctrine that "the shield stands from BOOT_COMPLETED".
 * It does not: the shield stands only when the service's monitor
 * loop first flips `protecting` — after the service dispatches,
 * permissions witness, and a full iteration certifies sight.
 * Every second of uptime before that flip ran unshielded, and
 * booking it as focus let a session complete ELAPSED_CLEAN on
 * boot time nobody watched.
 *
 * This store is the honest witness for that instant. The flip
 * site (MonolithShieldService) stamps it; the first flip of a
 * boot wins and later demote/re-flip cycles in the same boot
 * never move it. It lives in device-protected prefs (the boot
 * receiver's world, readable pre-unlock like the seal marker),
 * keyed by the boot it belongs to: a stamp from an older boot
 * reads as NO stamp, never as evidence about this one. Unknown
 * stays unknown — a missing stamp credits nothing, which is
 * the safe direction: unwitnessed time is never focus.
 */
object ShieldRaiseStore {

    private const val PREFS_NAME = "monolith_shield_raise"
    private const val KEY_BOOT = "shield_raise_boot"
    private const val KEY_MONO = "shield_raise_mono"

    /**
     * Stamp [monoMillis] as this boot's first shield raise.
     * No-op when a stamp for the SAME boot already stands (the
     * first flip wins), when the boot count cannot be read, or
     * when the instant is not a real monotonic reading. The
     * write commits synchronously: the stamp must be on disk
     * when the flip returns, because the very next tick may be
     * the one that prices this boot's witnessed span.
     */
    fun stampFirstRaise(context: Context, monoMillis: Long) {
        if (monoMillis <= 0L) return
        val boot = runCatching {
            Settings.Global.getInt(
                context.contentResolver, Settings.Global.BOOT_COUNT, -1
            )
        }.getOrDefault(-1)
        if (boot < 0) return
        val prefs = DeviceProtectedPrefs.get(context, PREFS_NAME)
        if (prefs.getInt(KEY_BOOT, -1) == boot && prefs.contains(KEY_MONO)) return
        prefs.edit()
            .putInt(KEY_BOOT, boot)
            .putLong(KEY_MONO, monoMillis)
            .commit()
    }

    /**
     * The stamped raise instant for [bootCount], or null when
     * no stamp stands for THIS boot (never raised, stamp lost,
     * or the stamp belongs to another boot). Null is the honest
     * unknown: callers credit nothing past the banked figure.
     */
    fun raiseMonoForBoot(context: Context, bootCount: Int): Long? {
        val prefs = DeviceProtectedPrefs.get(context, PREFS_NAME)
        if (prefs.getInt(KEY_BOOT, -1) != bootCount) return null
        if (!prefs.contains(KEY_MONO)) return null
        return prefs.getLong(KEY_MONO, 0L).takeIf { it > 0L }
    }

    /** Test hygiene: forget every stamp. */
    fun clear(context: Context) {
        DeviceProtectedPrefs.get(context, PREFS_NAME).edit().clear().commit()
    }
}

/**
 * Issue #525: the witnessed side of a reboot — the monotonic
 * span between this boot's first shield raise and [nowMonoMillis]
 * is the ONLY post-reboot span a session may be credited with.
 * No stamp (the shield never raised this boot, or the stamp is
 * lost) witnesses nothing: 0. A stamp ahead of the tick (a
 * monotonic frame the tick has not reached) witnesses nothing
 * either — the future is not evidence.
 */
internal fun witnessedPostRebootMillis(
    nowMonoMillis: Long,
    shieldRaisedMonoMillis: Long?
): Long {
    if (shieldRaisedMonoMillis == null) return 0L
    return (nowMonoMillis - shieldRaisedMonoMillis).coerceAtLeast(0L)
}

/**
 * Issue #525: the unshielded side of a reboot — this boot's
 * uptime before the shield's first raise. With a stamp it is
 * the span from power-on to the raise; without one, ALL uptime
 * up to the tick ran unwitnessed. Booked on the session row
 * (never credited) and printed on the receipt. Identity: for
 * any stamp at or before the tick, witnessed + unshielded =
 * the uptime the old derivation gifted whole.
 */
internal fun unshieldedRebootMillis(
    nowMonoMillis: Long,
    shieldRaisedMonoMillis: Long?
): Long {
    val unshieldedUntil = when {
        shieldRaisedMonoMillis == null -> nowMonoMillis
        shieldRaisedMonoMillis > nowMonoMillis -> nowMonoMillis
        else -> shieldRaisedMonoMillis
    }
    return unshieldedUntil.coerceAtLeast(0L)
}
