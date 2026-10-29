package com.example.data

import android.content.Context
import android.os.SystemClock
import com.example.service.PermPrint
import com.example.service.PermState
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchStore

/**
 * The reception's stamp (Empfang lane, Oct 30): whether the
 * first-open setup + introduction has been completed — DONE was
 * pressed on the sheet and the man was handed the Deck.
 *
 * Stored OUTSIDE Room on purpose, in the DayWordStore pattern:
 * the fact is chrome, not syllabus or session data, and stamping
 * it must never wait for a schema window. No DB version moves
 * for this lane.
 *
 * The write COMMITS (Truth Car 2, G-02): the stamp is a fact the
 * app has already acted on — the sheet dismissed, the Deck handed
 * over. apply()'s async flush would leave a window where a hard
 * process death silently un-stamps a completed setup and the
 * sheet interrupts again, which the flow's law forbids: once
 * stamped, it never interrupts again.
 */
class SetupStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** True once DONE has been pressed on the reception sheet. */
    fun isCompleted(): Boolean = prefs.getBoolean(SETUP_DONE_KEY, false)

    /** Stamp the setup complete. Committed synchronously (G-02). */
    fun markCompleted() {
        prefs.edit().putBoolean(SETUP_DONE_KEY, true).commit()
    }

    // ---------------------------------------------------------------
    // THE SETUP LEDGER (audit §3, Proposal 2 core; §6.8) — the
    // doors-at-stamp fact is unrecoverable once lost, so it is
    // written in the same transaction as the DONE stamp itself.
    // WRITE-ONCE: a day-40 reprint's DONE never rewrites day
    // one's fact. The ledger is capability memory, never
    // performance memory: no dates print from it, no counts.
    // ---------------------------------------------------------------

    /** The DONE write. Sets setup_done, the stamp wall clock and
     *  the doors-at-stamp mask in ONE edit, commit() — the two
     *  ledger fields only when never stamped before. */
    fun stampSetup(doorsAtStampMask: Int) {
        val e = prefs.edit().putBoolean(SETUP_DONE_KEY, true)
        if (prefs.getLong(STAMPED_WALL_KEY, 0L) == 0L) {
            e.putLong(STAMPED_WALL_KEY, System.currentTimeMillis())
                .putInt(DOORS_AT_STAMP_MASK_KEY, doorsAtStampMask)
        }
        e.commit()
    }

    /** Wall clock of the first DONE; null while unstamped. */
    fun stampedWall(): Long? =
        prefs.getLong(STAMPED_WALL_KEY, 0L).takeIf { it != 0L }

    /** COUNTED bits that stood GRANTED at the stamp instant;
     *  null while unstamped. */
    fun doorsAtStampMask(): Int? =
        if (prefs.getLong(STAMPED_WALL_KEY, 0L) == 0L) null
        else prefs.getInt(DOORS_AT_STAMP_MASK_KEY, 0)

    /** COUNTED bits seen GRANTED at any sweep since the stamp. */
    fun everSetMask(): Int = prefs.getInt(EVER_SET_MASK_KEY, 0)

    /** OR bits into the ever-set mask. One edit, commit(). */
    fun noteEverSet(bits: Int) {
        val merged = everSetMask() or bits
        if (merged != everSetMask()) {
            prefs.edit().putInt(EVER_SET_MASK_KEY, merged).commit()
        }
    }

    /** COUNTED bits whose refusal-slot call-in has fired (§2.3a
     *  of the mechanism spec — the wiring lands with the Deck
     *  composition; the mask is kept from this merge on). */
    fun calledInMask(): Int = prefs.getInt(CALLED_IN_MASK_KEY, 0)

    /** Mark one door's call-in spent. One edit, commit(). */
    fun markCalledIn(door: WatchItemId) {
        val bit = 1 shl WatchItemId.COUNTED.indexOf(door)
        if (bit == 0) return
        val merged = calledInMask() or bit
        if (merged != calledInMask()) {
            prefs.edit().putInt(CALLED_IN_MASK_KEY, merged).commit()
        }
    }

    // ---------------------------------------------------------------
    // THE RETURN CHIT (audit §3, Proposal 1; §6.7) — a departure
    // record written at the FIX tap, judged only by a measurement
    // taken AFTER the departure. It survives process death in
    // Settings — that survival is its whole reason to exist. The
    // clocks are read HERE, inside the write, so a (mono, wall)
    // pair can never be assembled from two different instants.
    // ---------------------------------------------------------------

    /** At the FIX tap. One edit, commit(). Overwrites any prior
     *  chit whole — one excursion at a time; the latest wins. */
    fun writeDepartureChit(door: WatchItemId, departurePrint: PermPrint) {
        prefs.edit()
            .putString(CHIT_STATE_KEY, ReturnChitState.DEPARTED.name)
            .putString(CHIT_DOOR_KEY, door.name)
            .putString(CHIT_DEPARTURE_PRINT_KEY, departurePrint.name)
            .putLong(CHIT_DEPARTURE_MONO_KEY, SystemClock.elapsedRealtime())
            .putLong(CHIT_DEPARTURE_WALL_KEY, System.currentTimeMillis())
            .putLong(CHIT_VERDICT_MONO_KEY, 0L)
            .putLong(CHIT_VERDICT_WALL_KEY, 0L)
            .putString(CHIT_NEXT_DOOR_KEY, "")
            .commit()
    }

    /** The standing chit; null when absent or unparseable. */
    fun chit(): ReturnChit? {
        val stateName = prefs.getString(CHIT_STATE_KEY, null) ?: return null
        val doorName = prefs.getString(CHIT_DOOR_KEY, null) ?: return null
        val printName = prefs.getString(CHIT_DEPARTURE_PRINT_KEY, null) ?: return null
        return runCatching {
            ReturnChit(
                door = WatchItemId.valueOf(doorName),
                departurePrint = PermPrint.valueOf(printName),
                departureMono = prefs.getLong(CHIT_DEPARTURE_MONO_KEY, 0L),
                departureWall = prefs.getLong(CHIT_DEPARTURE_WALL_KEY, 0L),
                state = ReturnChitState.valueOf(stateName),
                verdictMono = prefs.getLong(CHIT_VERDICT_MONO_KEY, 0L),
                verdictWall = prefs.getLong(CHIT_VERDICT_WALL_KEY, 0L),
                nextDoor = prefs.getString(CHIT_NEXT_DOOR_KEY, "")
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { WatchItemId.valueOf(it) }
            )
        }.getOrNull()
    }

    /** DEPARTED → a RETURNED_* state, stamping the verdict
     *  clocks from this call. One edit, commit(). No-op unless
     *  the stored state is DEPARTED — a late second resolver
     *  can never rewrite a printed verdict. */
    fun resolveChit(state: ReturnChitState, nextDoor: WatchItemId?) {
        val current = chit() ?: return
        if (current.state != ReturnChitState.DEPARTED) return
        prefs.edit()
            .putString(CHIT_STATE_KEY, state.name)
            .putLong(CHIT_VERDICT_MONO_KEY, SystemClock.elapsedRealtime())
            .putLong(CHIT_VERDICT_WALL_KEY, System.currentTimeMillis())
            .putString(CHIT_NEXT_DOOR_KEY, nextDoor?.name ?: "")
            .commit()
    }

    /** RETURNED_* → SPENT. One edit, commit(). No-op from
     *  DEPARTED (an unjudged excursion is never silently spent)
     *  and from SPENT. */
    fun spendChit() {
        val current = chit() ?: return
        if (current.state == ReturnChitState.DEPARTED ||
            current.state == ReturnChitState.SPENT
        ) return
        prefs.edit().putString(CHIT_STATE_KEY, ReturnChitState.SPENT.name).commit()
    }

    /** Clear every chit key. One edit, commit(). */
    fun clearChit() {
        prefs.edit()
            .remove(CHIT_STATE_KEY).remove(CHIT_DOOR_KEY)
            .remove(CHIT_DEPARTURE_PRINT_KEY).remove(CHIT_DEPARTURE_MONO_KEY)
            .remove(CHIT_DEPARTURE_WALL_KEY).remove(CHIT_VERDICT_MONO_KEY)
            .remove(CHIT_VERDICT_WALL_KEY).remove(CHIT_NEXT_DOOR_KEY)
            .commit()
    }

    companion object {
        private const val PREFS_NAME = "monolith_setup"
        private const val SETUP_DONE_KEY = "setup_done"
        private const val STAMPED_WALL_KEY = "setup_stamped_wall"
        private const val DOORS_AT_STAMP_MASK_KEY = "setup_doors_at_stamp_mask"
        private const val EVER_SET_MASK_KEY = "setup_ever_set_mask"
        private const val CALLED_IN_MASK_KEY = "setup_called_in_mask"
        private const val CHIT_STATE_KEY = "chit_state"
        private const val CHIT_DOOR_KEY = "chit_door"
        private const val CHIT_DEPARTURE_PRINT_KEY = "chit_departure_print"
        private const val CHIT_DEPARTURE_MONO_KEY = "chit_departure_mono"
        private const val CHIT_DEPARTURE_WALL_KEY = "chit_departure_wall"
        private const val CHIT_VERDICT_MONO_KEY = "chit_verdict_mono"
        private const val CHIT_VERDICT_WALL_KEY = "chit_verdict_wall"
        private const val CHIT_NEXT_DOOR_KEY = "chit_next_door"
    }
}

/** The chit's states. DEPARTED is the only state a verdict can
 *  be written from; SPENT is the tombstone that keeps a verdict
 *  from ever re-printing. */
enum class ReturnChitState {
    DEPARTED, RETURNED_REPAIRED, RETURNED_UNCHANGED,
    RETURNED_UNREAD, RETURNED_DARK, SPENT
}

/** One excursion's record, as stored. */
data class ReturnChit(
    val door: WatchItemId,
    val departurePrint: PermPrint,
    val departureMono: Long,
    val departureWall: Long,
    val state: ReturnChitState,
    val verdictMono: Long = 0L,
    val verdictWall: Long = 0L,
    val nextDoor: WatchItemId? = null
)

/** The verdict — the chit's only judge (mechanism spec §1.4).
 *  Pure: a function of the chit, the door's latest measurement,
 *  and the verdict instant. Returns null while nothing measured
 *  AFTER departure exists — a pre-departure stamp, however
 *  fresh it looks, judges nothing. First match wins:
 *  1. mono went backwards (reboot in Settings) → DARK;
 *  2. no post-departure measurement → null (keep waiting);
 *  3. measurement carries no perm → UNREAD;
 *  4. print at the verdict instant is GRANTED → REPAIRED;
 *  5. print is MISSING → UNCHANGED;
 *  6. print is STALE (granted after departure, already aged) →
 *     UNREAD. */
fun chitVerdictFor(
    chit: ReturnChit,
    measurement: WatchMeasurement?,
    verdictMono: Long
): ReturnChitState? {
    if (verdictMono < chit.departureMono) return ReturnChitState.RETURNED_DARK
    if (measurement == null || measurement.measuredMono <= chit.departureMono) {
        return null
    }
    val perm = measurement.perm ?: return ReturnChitState.RETURNED_UNREAD
    return when {
        perm == PermState.GRANTED &&
            verdictMono - measurement.measuredMono > WatchStore.STALE_AFTER_MS ->
            ReturnChitState.RETURNED_UNREAD
        perm == PermState.GRANTED -> ReturnChitState.RETURNED_REPAIRED
        else -> ReturnChitState.RETURNED_UNCHANGED
    }
}
