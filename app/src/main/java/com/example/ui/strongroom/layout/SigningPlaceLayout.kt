package com.example.ui.strongroom.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * SLICE 2 — LAYOUT. The signing place at slot depth.
 *
 * Adopted by BUILD-SR-7 from DESIGNER-SR-2's (T05) seam contribution
 * (`work/designer-sr-2/SigningPlaceLayoutContribution.kt`, read firsthand
 * and adopted 2026-11-02), with two amendments, both named in the
 * adoption letter: (1) this REPLACES the original two-slot
 * SigningPlaceLayout outright — one signing primitive, not an "extended"
 * twin two surfaces could split between; (2) the contribution header's
 * "nine ruled slots" is corrected to eight, matching its own enum.
 *
 * Why slot depth: the decided signing order cannot be carried by a
 * controls blob — a blob lets a surface re-sequence clause, break and
 * spin by taste, and C-4's zero-insertion law becomes caller discipline
 * instead of structure. Slots only, as everywhere in this package: no
 * colour, no type sizes, no wording live here; verbatim ledger strings
 * are rendered by the surface (Slice 3's custody). Spacing only, on
 * RoomGrid's 4dp grid and its named bands. There is no tray/record
 * parameter and no summary parameter: B-16 is enforced by absence.
 */

/** The signing place's slots, in the ruled order and no other (C-4:
 *  zero lawful insertions — a closed enum is the enforcement: there is
 *  no slot a later slice can insert a prompt or summary into). */
enum class SigningSlot(val rank: Int) {
    /** 1 — Tomorrow as resolved: day-shape + date, information only.
     *  There is deliberately no shape-picker slot in the head. */
    HEAD_TOMORROW_AS_RESOLVED(1),

    /** 2 — The composition as received (B-2): stands composed before he
     *  arrives; the signing amends it in place, never creates it. Its
     *  internal order (spans · locked set as plain words · shaped gaps
     *  as blanks · break notch · lifting-hour line · sentence/kept
     *  place · per-block task lines, B-3) is the surface's composition
     *  inside this one slot. */
    COMPOSITION_AS_RECEIVED(2),

    /** 3 — The spent-day telling (B-4). CONDITIONAL: present only on a
     *  spent day, read from a named model fact — never from a question
     *  to him. When absent the slot contributes ZERO height and no
     *  counterpart line exists. */
    SPENT_DAY_TELLING(3),

    /** 4 — The clause as a term (B-5). Stood under, never gated: no
     *  checkbox slot, no hold-to-agree slot exists in this type. The
     *  signing place is the only surface carrying a slot for it. */
    CLAUSE_AS_TERM(4),

    /** 5 — The break choice (D-6): free value, presets as equal one-tap
     *  shortcuts in the same row. No cap lives here. */
    BREAK_CHOICE(5),

    /** 6 — The foot-rule: confirmation AS the sheet as amended. Not a
     *  summary, not a screen — a ruled line. Below it, only terms'
     *  consequence and the signature stand. */
    FOOT_RULE(6),

    /** 7 — The combination spin (A-2): gesture-in, signed-fact-out.
     *  There is NO text-field slot in this zone by construction — a
     *  combination cannot be typed here, only spun, and it is never
     *  written anywhere. No skip/bypass slot exists. */
    COMBINATION_SPIN(7),

    /** 8 — The door's telling (B-19): standing furniture at the foot —
     *  rendered from arrival, asking nothing. Last in rank because it
     *  is read last, on the way out; not a step in the signing's act
     *  order. */
    DOOR_TELLING(8),
    ;

    companion object {
        val ruledOrder: List<SigningSlot> = entries.sortedBy { it.rank }

        /** C-4 as a function: a proposed slot list is lawful only if it
         *  is exactly a subsequence of the ruled order — nothing
         *  inserted, nothing re-sequenced. */
        fun isLawfulOrder(slots: List<SigningSlot>): Boolean =
            slots == slots.sortedBy { it.rank } && slots.distinct() == slots

        /** B-4's conditionality: the telling slot stands only when the
         *  model's named spent-day fact stands. Absent = no slot. */
        fun stands(slot: SigningSlot, spentDay: Boolean): Boolean =
            slot != SPENT_DAY_TELLING || spentDay
    }
}

/** The clause, the spent-day telling and the door's telling have exactly
 *  one lawful home: the signing place. They travel nowhere — never the
 *  face field (B-5: the clause is spoken whole only at the signing,
 *  never on the face), never the tray side. */
object SigningOnlyLaw {
    enum class SigningOnlyText {
        CLAUSE, SPENT_DAY_TELLING, DOOR_TELLING,
    }

    fun homeZone(text: SigningOnlyText): RoomZone = RoomZone.SIGNING_PLACE

    fun mayStandIn(text: SigningOnlyText, zone: RoomZone): Boolean =
        zone == RoomZone.SIGNING_PLACE
}

/** The open door's proportions (B-17): the composition is the doorway's
 *  hero — upper three-fifths of the field; the plain line is exactly one
 *  line; the striking band is set apart by the second-step band. */
object OpenDoorProportion {
    const val COMPOSITION_FIFTHS: Int = 3
    const val FIELD_FIFTHS: Int = 5

    /** The plain line's height is one line at its lawful step — the
     *  slot never grows; wording that does not fit returns to Slice 3. */
    const val PLAIN_LINE_LINES: Int = 1

    const val STRIKING_SEPARATION_STEPS: Int = RoomGrid.SECOND_STEP_BAND_STEPS
}

/**
 * The signing place, at slot depth — the eight [SigningSlot]s in ruled
 * order. [spentDayTelling] is a NULLABLE slot: null on an ordinary day,
 * and null contributes nothing — no spacer, no placeholder, no
 * counterpart line. That nullability IS B-4's layout law.
 */
@Composable
fun SigningPlaceLayout(
    modifier: Modifier = Modifier,
    headTomorrowAsResolved: @Composable () -> Unit,
    compositionAsReceived: @Composable () -> Unit,
    spentDayTelling: (@Composable () -> Unit)?,
    clauseAsTerm: @Composable () -> Unit,
    breakChoice: @Composable () -> Unit,
    footRule: @Composable () -> Unit,
    combinationSpin: @Composable () -> Unit,
    doorTelling: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp),
        verticalArrangement = Arrangement.Top,
    ) {
        headTomorrowAsResolved()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
        compositionAsReceived()
        if (spentDayTelling != null) {
            Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
            spentDayTelling()
        }
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
        clauseAsTerm()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
        breakChoice()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
        footRule()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.SECOND_STEP_BAND_STEPS).dp))
        combinationSpin()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
        doorTelling()
    }
}
