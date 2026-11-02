package com.example.ui.antechamber

import com.example.data.StrongroomHeldArrivalEntity
import com.example.domain.StrongroomDay
import com.example.ui.foundation.HardStopAttempt
import com.example.ui.foundation.StrongroomMaximum
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * THE ANTECHAMBER'S MECHANISMS — the pure half of BUILD-SR-38's
 * slice (census C-08 · C-09 · C-14 · C-15, INVENTORY-SR-1, derived
 * firsthand). No Android, no Compose: every law the room obeys is
 * stated here where the venue's Targeted job can pin it, and the
 * surface in Antechamber.kt only renders what this file derives.
 *
 * The laws, by census row:
 *
 *  - C-08's LIST LAW (ledger B-9), as structure and in no other
 *    order: (1) calling out first and biggest · (2) the allowed
 *    things as plain words, exactly as signed, in signed order ·
 *    (3) the held pile behind a second step of its own · (4) spent
 *    things GONE when the day's purse is spent, never dimmed ·
 *    (5) if it is not on that list, it is not behind the door.
 *  - C-09 THE DRUM + route-book Law R-1 (the drum's host law,
 *    TRUE in this text): the turned name resolves ONLY through
 *    the drum stock — the signing's allowed set, in signed order,
 *    restricted to candidates neither locked nor unsigned. There
 *    is NO fallback resolution of any wider kind standing behind
 *    it: a name the stock does not hold cannot be turned to him,
 *    whatever any other catalogue contains. The spend fires only
 *    behind the model's TURNED outcome, through the kept store's
 *    purse — spend persists, the purse never resurrects (the
 *    purse is re-derived from the store on every entry; this file
 *    holds no purse of its own).
 *  - THE SHUTTER (intake §3 APPLY, Gemini routing to the drum):
 *    when the drum reaches 00:00 — the purse spent whole — a brass
 *    shutter closes over the metered apps. In this model the
 *    shutter is a DERIVATION, not a state anyone sets: it stands
 *    closed exactly when the purse is hard-stopped, and the token
 *    stock reads EMPTY behind it (gone, never dimmed — B-9(4)).
 *  - C-14 THE HELD PILE + S4-C27's aggregation (route book §3.1):
 *    the pile is the day's WAITING arrivals only, first-waited
 *    first, re-derived from the kept DB-27 store rows on every
 *    entry — a reboot cannot empty it. Its only signal is ONE
 *    quiet dot: present iff anything waits, IDENTICAL for one
 *    waiting arrival or nine, identical at hour 1 as at hour 9 —
 *    count- and age-invariant, carrying no word, no count, no
 *    name (TX-SHOW-09). The dot clears on entering the pile.
 *  - C-14's DIGITS LINE: a held code stands as digits alone, with
 *    the hour (TX-SHOW-07); a held call stands as the number shown
 *    whole (TX-SHOW-08). The per-call choice is exactly
 *    "take it / leave it" (TX-DO-01) — leaving is complete: no
 *    escalation, no count, no debt.
 *  - C-04 THE GOVERNOR'S CRANK, at the room's wall (route book
 *    R-3 seats it in this slice): the week's minute is available
 *    exactly while the crank week's seal stands whole; once spent,
 *    the wall holds an empty hook and the snapped seal is KEPT in
 *    the tray — visible proof, never thrown away.
 */

/** One allowed app as a physical token in the room: the phone's
 *  own name for it, carried verbatim (TX-SHOW-05 — the app mints
 *  no name, adds none, ranks none). */
data class AntechamberToken(val packageName: String, val label: String)

/** One waiting arrival as a slip in the held pile. [line] is the
 *  arrival's own line — a stranger's number as it arrived, or a
 *  code's digits alone; nothing else about the arrival exists in
 *  this app and none is invented here. */
data class HeldSlip(
    val id: Int,
    val kind: Kind,
    val line: String,
    val arrivedAtWallMillis: Long
) {
    enum class Kind { CALL, CODE }
}

/** The room's derivation: everything the surface renders, derived
 *  in one act from the standing facts (the sealed day's signed
 *  record · the store's purse · the crank week · the store's held
 *  rows). A pure function of its inputs — the same facts derive
 *  the same room on every entry, which is what makes a reboot
 *  inside the seal a non-event for the room (B-10). */
data class AntechamberRoom(
    /** The drum stock: allowed, in signed order, minus the locked
     *  (Law R-1's candidate restriction). Behind a closed shutter
     *  this reads EMPTY — the things are gone, not dimmed. */
    val drumStock: List<String>,
    /** The purse as the store stands it at this entry. */
    val purse: StrongroomDay.AllowancePurse,
    /** The shutter's standing: closed exactly at the spent purse. */
    val shutterClosed: Boolean,
    /** The waiting pile, first-waited first. */
    val waiting: List<HeldSlip>,
    /** The one quiet dot: [waiting] non-empty, and nothing more
     *  about it is ever true (S4-C27). */
    val quietDot: Boolean,
    /** The governor's minute stands unspent on the wall. */
    val crankAvailable: Boolean,
    /** The snapped crank seal is kept in the tray. */
    val snappedSealKept: Boolean
)

object AntechamberModel {

    /** B-9's list, as the room's section order — the surface
     *  renders exactly these sections, in exactly this order, and
     *  no fifth section exists. */
    enum class Section { CALLING_OUT, TOKENS, HELD_PILE }

    val LIST_ORDER: List<Section> =
        listOf(Section.CALLING_OUT, Section.TOKENS, Section.HELD_PILE)

    /** The pile from the store's rows: WAITING only, in the
     *  store's own order (first-waited first — the DAO's order is
     *  the arrival order). TAKEN and LEFT rows are not the pile:
     *  a taken line was discharged, a left line leaves complete. */
    fun waitingPile(rows: List<StrongroomHeldArrivalEntity>): List<HeldSlip> =
        rows.filter { it.state == StrongroomHeldArrivalEntity.STATE_WAITING }
            .map { row ->
                HeldSlip(
                    id = row.id,
                    kind = if (row.kind == StrongroomHeldArrivalEntity.KIND_CODE) {
                        HeldSlip.Kind.CODE
                    } else {
                        HeldSlip.Kind.CALL
                    },
                    line = row.line,
                    arrivedAtWallMillis = row.arrivedAtWallMillis
                )
            }

    /** S4-C27, executed: ONE dot, aggregated over all waiting
     *  arrivals — a function of non-emptiness and of nothing else.
     *  There is no count form, no age form, and no word form of
     *  this signal anywhere in the slice (its face-word stands
     *  ruled ∅, census GAP-R10). */
    fun quietDotVisible(waiting: List<HeldSlip>): Boolean = waiting.isNotEmpty()

    /** Derive the room whole from the standing facts. */
    fun room(
        signing: StrongroomDay.SigningRecord,
        purse: StrongroomDay.AllowancePurse,
        crank: StrongroomDay.GovernorCrankWeek,
        heldRows: List<StrongroomHeldArrivalEntity>
    ): AntechamberRoom {
        val waiting = waitingPile(heldRows)
        val shutter = purse.isHardStopped
        return AntechamberRoom(
            drumStock = if (shutter) emptyList()
            else RevolvingDrum.stock(signing),
            purse = purse,
            shutterClosed = shutter,
            waiting = waiting,
            quietDot = quietDotVisible(waiting),
            crankAvailable = crank.minuteAvailable,
            snappedSealKept = crank.sealBroken
        )
    }

    /** The held line as it stands on its slip (TX-SHOW-07/08):
     *  a CODE stands as digits alone, with the hour it arrived;
     *  a CALL stands as the number, shown whole, and nothing else.
     *  No sender name, no words around them, no way onward. */
    fun heldLine(slip: HeldSlip, zone: ZoneId = ZoneId.systemDefault()): String =
        when (slip.kind) {
            HeldSlip.Kind.CODE -> "${slip.line}  ${hourOf(slip.arrivedAtWallMillis, zone)}"
            HeldSlip.Kind.CALL -> slip.line
        }

    /** The hour of an arrival, as the slip carries it — the
     *  phone's own clock reading of when it landed, HH:mm. */
    fun hourOf(wallMillis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
        DateTimeFormatter.ofPattern("HH:mm").withZone(zone)
            .format(Instant.ofEpochMilli(wallMillis))

    /** The per-call choice's exact words (TX-DO-01) — the only
     *  words the pile carries. */
    const val TAKE_WORD: String = "take it"
    const val LEAVE_WORD: String = "leave it"
}

/**
 * THE REVOLVING DRUM (census C-09) — a drum in the door's flank,
 * turned by its crank: one turn brings ONE app to him for its
 * minute ([MINUTES_PER_TAKING], priced against the day's purse —
 * the same purse a code's showing pays from, ledger B-18: one
 * purse across drum + showing). Turn it back sooner, or it
 * returns itself; the minute's end has NO separate event — the
 * drum's self-return IS the end (foundation's ExcludedByLaw).
 * When the purse is spent the drum locks: the crank meets the
 * filed hard stop (strain travel 4.0°, one refusal per attempt,
 * re-arming only on leaving the approach zone — the foundation's
 * [HardStopAttempt] over [StrongroomMaximum.spentPurseStop]).
 */
object RevolvingDrum {

    /** One taking = one minute of the day's allowance. */
    const val MINUTES_PER_TAKING: Int = 1

    /** The drum stock (Law R-1): the drum-stock resolution is the
     *  ONLY resolution a turned name may pass through — the
     *  signing's allowed set in signed order, restricted to
     *  candidates that are neither locked nor absent from the
     *  signing. No wider catalogue is consulted, here or behind
     *  here: what the signing did not allow is not in the drum,
     *  and a locked name signed into both sets resolves toward
     *  the seal (the routing's own fail-closed rule). */
    fun stock(signing: StrongroomDay.SigningRecord): List<String> =
        signing.allowedPackages.filter { it !in signing.lockedPackages }

    /** Station angles: the stock stands evenly around the drum,
     *  station 0 at the index (top). Angles are degrees clockwise
     *  from the index, the drum's own travel coordinates. */
    fun stationAngle(index: Int, stockSize: Int): Double {
        require(stockSize > 0) { "an empty drum has no stations" }
        return index * (360.0 / stockSize)
    }

    /** The station the drum's rotation presents at the index: the
     *  station whose angle the rotation has carried nearest to
     *  the index, ties to the lower station (the detent rail's
     *  own tie law). Rotation is taken modulo the circle, in
     *  either direction — the drum turns both ways (turn it back
     *  is a lawful act, census C-09). */
    fun stationAt(rotationDeg: Double, stockSize: Int): Int {
        if (stockSize <= 0) return -1
        val step = 360.0 / stockSize
        val turned = ((-rotationDeg) % 360.0 + 360.0) % 360.0
        val raw = turned / step
        var idx = kotlin.math.floor(raw + 0.5).toInt()
        if (idx >= stockSize) idx -= stockSize
        // A tie (exactly half a step) resolves DOWN, to the lower
        // station — floor(+0.5) rounds a .5 UP, so correct it.
        if (raw - kotlin.math.floor(raw) == 0.5) idx = kotlin.math.floor(raw).toInt() % stockSize
        return idx
    }

    /** The rotation nearest to [fromDeg] that seats [index] at
     *  the index — the settle target the hand's release is caught
     *  by (the detent's capture, read off the body's projection
     *  by the surface; the target itself is this pure fact). */
    fun settleTarget(fromDeg: Double, index: Int, stockSize: Int): Double {
        val station = stationAngle(index, stockSize)
        val base = -station
        val turns = kotlin.math.round((fromDeg - base) / 360.0)
        return base + turns * 360.0
    }

    /** The turned name at a rotation, resolved ONLY through the
     *  stock (Law R-1) — or null when the drum stands empty. */
    fun turnedPackage(rotationDeg: Double, signing: StrongroomDay.SigningRecord): String? {
        val stock = stock(signing)
        if (stock.isEmpty()) return null
        return stock[stationAt(rotationDeg, stock.size)]
    }

    /** The outcome of a taking. The spend exists only inside
     *  [Turned] — there is no outcome in which a minute is spent
     *  and no app is turned to him, and none in which an app is
     *  turned and no minute is spent. */
    sealed interface Turn {
        /** The app turned to him, and the purse AFTER its minute
         *  was spent through the store — the host persists exactly
         *  this spend and launches exactly this name. */
        data class Turned(val packageName: String, val purseAfter: StrongroomDay.AllowancePurse) : Turn

        /** The purse cannot cover the minute: refused whole, the
         *  crank will not turn — the hard stop, felt in the wrist.
         *  Nothing is spent, nothing is turned, nothing is shown
         *  dimmed: behind this outcome the shutter stands closed. */
        data object HardStop : Turn

        /** The drum stands empty (nothing was signed into it, or
         *  the shutter has taken the stock): no turn exists. */
        data object Empty : Turn
    }

    /**
     * Take the turned app for its minute. The purse offered is
     * the store's purse as read at this entry — the spend is
     * computed by the DOMAIN purse (refused whole past its end)
     * and persisted by the host through the store's own verb, so
     * what this function grants is exactly what the store will
     * stand: spend persists, and no purse is ever re-derived from
     * anywhere but the store (the K2 law: the purse never
     * resurrects).
     */
    fun turn(
        rotationDeg: Double,
        signing: StrongroomDay.SigningRecord,
        purse: StrongroomDay.AllowancePurse
    ): Turn {
        val pkg = turnedPackage(rotationDeg, signing) ?: return Turn.Empty
        val after = purse.spend(MINUTES_PER_TAKING) ?: return Turn.HardStop
        return Turn.Turned(pkg, after)
    }

    /** A fresh hard-stop attempt for the spent drum: the filed
     *  stop (4.0° strain travel) over the drum's remaining travel
     *  — zero, which is the point: the crank strains against the
     *  stop and the refusal fires once per approach. */
    fun spentStopAttempt(): HardStopAttempt =
        HardStopAttempt(StrongroomMaximum.spentPurseStop(travelLimit = 0.0))

    /** A code's showing rides this same drum and pays this same
     *  purse (ledger B-18): one minute, refused whole at the hard
     *  stop — a spent day tells it at the signing ("codes wait
     *  tonight"), and inside the room the cover simply stays on. */
    fun showingSpend(purse: StrongroomDay.AllowancePurse): StrongroomDay.AllowancePurse? =
        purse.spend(MINUTES_PER_TAKING)
}
