package com.example.data

import android.content.Context

/**
 * The hand loadout cap's memory (UNA-07, Truth Car 3).
 *
 * The Shield LOADOUT cap used to rest on `remember { 0 }` — STUDY by
 * composition memory: leave Shield and return, or restart the
 * process, and the cap sat on STUDY no matter what he had actually
 * picked, while the app tiers underneath stayed wherever his last
 * pick had put them. The selector lied about the state it selects.
 *
 * The cap now rests on a stored fact: the last loadout HE applied by
 * hand (the ViewModel's applySmartLoadout path — the hand path only;
 * a routine firing its own loadout at 20:00 does not move his hand
 * selector). Stored OUTSIDE Room on purpose, like DayWordStore: a
 * one-string fact with no schema business. Writes COMMIT (the G-02
 * standard): a deferred capture must not lose the pick the cap has
 * already shown as resting.
 *
 * Audit R1 #38 hardened the read/write contract:
 *  - put() returns commit()'s verdict — a failed disk write is
 *    reportable, never silently presented as a durable pick.
 *  - The read distinguishes ABSENT (never picked) from CORRUPT
 *    (a stored value that no longer parses — renamed enum, type
 *    corruption, downgrade). Corruption used to collapse silently
 *    to STUDY, indistinguishable from never-picked: exactly the
 *    selector lie this store exists to kill. A corrupt entry is
 *    healed (removed) on read, but the fact that it WAS corrupt
 *    stays observable through [readStored] until then.
 *  - getString() itself sits inside the guard: a non-String value
 *    under the key throws ClassCastException from
 *    SharedPreferences, and that crash escaped the old read and
 *    looped at ViewModel construction.
 *  - [CAP_ORDER] is the ONE seat order: cap index, inverse lookup,
 *    the Shield selector and the routine-loadout cycle all consume
 *    it. (The claim used to be documentation fiction — the order
 *    was re-hardcoded in the screen and in the ViewModel.)
 */
class LoadoutStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** What is actually under the key — corruption is a distinct,
     *  observable state, never a silent synonym for "never picked". */
    sealed interface Stored {
        /** No value stored: he has never hand-picked. */
        data object Absent : Stored

        /** A live, parseable hand pick. */
        data class Present(val loadout: SmartLoadout) : Stored

        /** A stored value that does not parse (or is not a String
         *  at all — [raw] is then null). [raw] is the offending
         *  string when there is one, kept for diagnosis. */
        data class Corrupt(val raw: String?) : Stored
    }

    /** Read the stored fact in full, healing corruption: a corrupt
     *  entry is removed as it is reported, so the lie cannot
     *  persist across reads — but this read still returns Corrupt,
     *  so the loss of a pick is never invisible to the caller. */
    fun readStored(): Stored {
        val raw: String? = try {
            prefs.getString(KEY_LAST_HAND_LOADOUT, null)
        } catch (e: ClassCastException) {
            prefs.edit().remove(KEY_LAST_HAND_LOADOUT).commit()
            return Stored.Corrupt(null)
        }
        if (raw == null) return Stored.Absent
        val parsed = runCatching { SmartLoadout.valueOf(raw) }.getOrNull()
        return if (parsed != null) {
            Stored.Present(parsed)
        } else {
            prefs.edit().remove(KEY_LAST_HAND_LOADOUT).commit()
            Stored.Corrupt(raw)
        }
    }

    /** The last hand-applied loadout, or null if he has never
     *  picked — or if the stored value was corrupt (healed on
     *  read; [readStored] is the form that reports the loss). */
    fun lastHandLoadout(): SmartLoadout? =
        (readStored() as? Stored.Present)?.loadout

    /** Record a hand pick. Commits synchronously (see class doc) —
     *  call it OFF the main thread. Returns commit()'s verdict:
     *  false means the pick did NOT reach disk and must not be
     *  presented as durable. */
    fun put(loadout: SmartLoadout): Boolean =
        prefs.edit().putString(KEY_LAST_HAND_LOADOUT, loadout.name).commit()

    /** The cap position the stored fact implies; STUDY (0) before
     *  any pick — the same position the old remember default showed,
     *  but now a default of record, not of amnesia. */
    fun capIndex(): Int = capIndexOf(lastHandLoadout() ?: SmartLoadout.STUDY)

    companion object {
        private const val PREFS_NAME = "monolith_loadout"
        private const val KEY_LAST_HAND_LOADOUT = "last_hand_loadout"

        /** The ONE seat order for the cap — ShieldCapSelector's
         *  order. Every mapping between a loadout and its seat
         *  (index-of, look-up, cycling) derives from this list and
         *  nothing else. */
        val CAP_ORDER: List<SmartLoadout> = listOf(
            SmartLoadout.STUDY,
            SmartLoadout.WORK_SPRINT,
            SmartLoadout.SLEEP
        )

        /** A loadout's seat on the cap — derived from [CAP_ORDER]. */
        fun capIndexOf(loadout: SmartLoadout): Int = CAP_ORDER.indexOf(loadout)

        /** The loadout seated at [index]. Out-of-range indices —
         *  which the selector never generates, but restored state
         *  theoretically could — seat SLEEP, the last seat, exactly
         *  as the screen's old hand-written `else` branch did. */
        fun loadoutForCapIndex(index: Int): SmartLoadout =
            CAP_ORDER.getOrElse(index) { SmartLoadout.SLEEP }
    }
}
