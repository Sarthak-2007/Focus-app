package com.example.ui.strongroom.layout

/**
 * SLICE 2 — LAYOUT (BUILD-SR-7, T24). The Strongroom's spatial law as pure,
 * testable fact. No colours, no type sizes, no wording live here: colour is
 * P0/Slice 4's, type steps are Stage 5's (consumed by surfaces from P0),
 * words are Slice 3's. This file carries ONLY where things stand, in what
 * order, and what may never share a field — so a surface cannot improvise
 * the room's composition and get it wrong quietly.
 *
 * Sources (firsthand): Ledger B-9 (antechamber list law) · B-16 (plate stack
 * never at the signing place) · B-17 (the Doorway's count is four) · E-2
 * (the plate pile is the progress display, as B-16 bounds it) · D-8 (the
 * drawer: stored, quiet, never scored) · Stage 5 type-system §2 (screen
 * rhythm: baseline unit 4; the grid Stage 8's skeletons stand on) · §3.3
 * (the face's hierarchy map) · §3.4.2 (antechamber: people outrank apps).
 */

/** The one grid. Every spatial measure in the room is a whole number of
 *  these units (Stage 5 §2: baseline unit 4 — 4sp of type, 4dp of space,
 *  one grid). A measure that is not a multiple of [BASE_DP] does not exist
 *  in the room's layout. */
object RoomGrid {
    const val BASE_DP: Int = 4

    /** [steps] whole grid units, in dp. */
    fun dp(steps: Int): Int {
        require(steps >= 0) { "grid steps are never negative" }
        return steps * BASE_DP
    }

    fun isOnGrid(dpValue: Int): Boolean = dpValue >= 0 && dpValue % BASE_DP == 0

    // Named bands, priced once here so no surface invents its own spacing.
    /** Between the printings on the face's field. */
    const val FACE_BAND_STEPS: Int = 3

    /** Between the antechamber's list bands. */
    const val LIST_BAND_STEPS: Int = 2

    /** The second step's separation: the held pile stands a full extra band
     *  behind the allowed list — a step of its own, not a continuation. */
    const val SECOND_STEP_BAND_STEPS: Int = 4

    /** The tray rail's width, on the room's side — never central. */
    const val TRAY_RAIL_STEPS: Int = 22

    /** The drawer's quiet cell floor (adaptive above this). */
    const val DRAWER_CELL_MIN_STEPS: Int = 40

    /** Outer field inset for room surfaces. */
    const val FIELD_INSET_STEPS: Int = 4
}

/** The room's zones. A piece of content belongs to exactly one zone; the
 *  zones are the layout's vocabulary, and two of them — the signing place
 *  and the tray side — may never share a field (B-16). */
enum class RoomZone {
    /** At the open door: the composition and the signing's own materials. */
    SIGNING_PLACE,

    /** The sealed face's one field, composed once at closing. */
    FACE_FIELD,

    /** Behind the grille door: the allowed, the calling, the drum. */
    ANTECHAMBER,

    /** Deeper in the antechamber, behind its own second step. */
    HELD_PILE,

    /** The room's side, where the record rests: plates + snapped seals. */
    TRAY_SIDE,

    /** The quiet drawer: finished tasks stored, plates browsed. */
    DRAWER,
}

/** What the tray side holds (B-16 / E-2 / U-03 / S3-4-25). The plate pile
 *  standing here IS the progress display — in the room, browsed away from
 *  the signing, never among the signing's materials, where the record
 *  would argue at the composing hour. */
enum class TrayContent {
    DAY_PLATES,
    SNAPPED_GOVERNOR_SEAL,
}

/** The signing place's lawful contents — and nothing else. In particular
 *  no [TrayContent] is a signing material: the separation is stated here,
 *  in the law, not left to a surface's good behaviour. */
enum class SigningMaterial {
    COMPOSITION,
    LOCKED_APP_CHOICE,
    LIFTING_HOUR_CHOICE,
    SENTENCE_OR_KEPT_PLACE,
    COMBINATION_SPIN,
}

object TraySeparationLaw {
    /** B-16, as a function: tray content never stands at the signing place. */
    fun mayStandAtSigningPlace(content: TrayContent): Boolean = false

    /** The tray's home zone, and the only zone it may be composed in. */
    fun homeZone(content: TrayContent): RoomZone = RoomZone.TRAY_SIDE

    /** The signing materials' home zone. */
    fun homeZone(material: SigningMaterial): RoomZone = RoomZone.SIGNING_PLACE
}

/** The antechamber's list, in this order and no other (B-9). The order is
 *  a spatial fact: rank 1 stands first and biggest; the held pile stands
 *  behind a second step of its own; and SPENT THINGS HAVE NO SLOT AT ALL —
 *  when the day's open minutes are spent, spent things are gone, never
 *  shown dimmed (B-9 limb 4). An enum with no SPENT entry is the law's
 *  enforcement: there is nowhere in this type to put one. */
enum class AntechamberSlot(val rank: Int) {
    /** (1) Calling out — first and biggest. People outrank apps
     *  (Stage 5 §3.4.2: the calling-out head stands first and biggest). */
    CALLING_OUT(1),

    /** (2) The allowed things as plain words, exactly as signed, nothing
     *  added, nothing suggested, in the signed order — type and layout
     *  may not re-rank them. */
    ALLOWED_AS_SIGNED(2),

    /** (3) The held pile, behind a second step of its own. */
    HELD_PILE_SECOND_STEP(3),
    ;

    companion object {
        /** B-9 limb 5: if it is not on that list, it is not behind the door.
         *  A proposed slot list is lawful only if it is exactly a prefix-free
         *  subsequence of the ruled order — no additions, no reordering. */
        fun isLawfulOrder(slots: List<AntechamberSlot>): Boolean =
            slots == slots.sortedBy { it.rank } && slots.distinct() == slots

        val ruledOrder: List<AntechamberSlot> = entries.sortedBy { it.rank }
    }
}

/** The face's four printings, in Stage 5 §3.3's ruled hierarchy. Rank is
 *  hierarchy, not size (the sentence's step PRT-4 is larger than the
 *  door-word's PRT-3, and ranks below it — rank and size are different
 *  axes). The layout carries rank + the filed step NAME; the step's size
 *  is P0's to render. Nothing else stands on the face's field — no score,
 *  no tally, no live indicator (Stage 5 §4 negative figure law). */
enum class FacePrinting(val rank: Int, val stage5Step: String) {
    /** 1 — first fact: the lifting hour, engraved (ENG-1). */
    LIFTING_HOUR(1, "ENG-1"),

    /** 2 — the weight-carrying word: the door-word SLOT (PRT-3). Its
     *  content does not exist (G-1); the slot stands sized and empty. */
    DOOR_WORD_SLOT(2, "PRT-3"),

    /** 3 — his words, by place: his sentence or its kept place (PRT-4). */
    SENTENCE_OR_KEPT_PLACE(3, "PRT-4"),

    /** 4 — testimony, never announcement: the held-mark SLOT (PRT-2, the
     *  printed floor). Its form is undecided (G-2); the slot stands. */
    HELD_MARK_SLOT(4, "PRT-2"),
    ;

    companion object {
        val ruledOrder: List<FacePrinting> = entries.sortedBy { it.rank }
    }
}

/** The Doorway's count is four (B-17) — in the room's furniture: the
 *  composition at the open door · one plain line · the held waiting · the
 *  striking. The layout carries the count and the order; the plain line's
 *  words are Slice 3's, the striking's rendering is P6's. */
enum class DoorwayElement(val order: Int) {
    COMPOSITION_AT_OPEN_DOOR(1),
    ONE_PLAIN_LINE(2),
    HELD_WAITING(3),
    STRIKING(4),
    ;

    companion object {
        const val DOORWAY_COUNT: Int = 4
        val ruledOrder: List<DoorwayElement> = entries.sortedBy { it.order }
    }
}

/** The drawer's lawful contents (D-8): finished tasks stored, plates
 *  browsed. There is deliberately no slot for a score, a total, a streak
 *  or a grade — a quiet grid has nowhere to put one (Audit A §6: no
 *  tracker behaviour anywhere). */
enum class DrawerContent {
    FINISHED_TASKS,
    PLATES,
}
