package com.example.ui.strongroom.layout

/**
 * SLICE 2 — LAYOUT, SECOND HALF (BUILD-SR-10, T24). The composition law
 * BUILD-SR-7's first half did not carry: the antechamber as a ROOM (its
 * zones and the drum's place in it), the held pile's interior behind the
 * second grille, and the tray/drawer composition (LEAD-SLICE-2 piece
 * L2-c). Pure spatial law, same discipline as [RoomLayout]: no colours,
 * no type sizes, no wording — where things stand, in what order, and
 * what may never share a field.
 *
 * Seam (lettered to BUILD-SR-7 / BUILD-SR-2 / LEAD-SLICE-2 before this
 * file existed): BUILD-SR-7's files are untouched; Seam S-A (the
 * eight-slot signing extension) was found already landed by BUILD-SR-7
 * at 4c77cfd8 and is NOT duplicated here. Seam S-B is ruled (b) in this
 * file: the drum housing is a SIBLING of the list column under one room
 * root, never a slot in the list primitive (R-C3).
 *
 * Sources (firsthand): Ledger B-9 · B-16 · B-17 · B-18 · D-8 · E-2 ·
 * DESIGNER-SR-3's antechamber design (zones Z1–Z4, shares, the second
 * grille, the crank's absence from the room) · LEAD-SLICE-2's opening
 * page (pieces L2-a/b/c).
 */

/** The antechamber's zones (DESIGNER-SR-3 §1). The room is deeper than
 *  the list: B-9's list governs Z1/Z2/Z4's ORDER, and Z3 — the machine
 *  wall — stands in the room without standing in the list. Shares are
 *  of the room's field height, in hundredths, as filed. */
enum class AntechamberZone(val shareHundredths: Int) {
    /** Z1 — the head wall: calling out, first and biggest. */
    HEAD_WALL(26),

    /** Z2 — the register: the allowed things, exactly as signed. */
    REGISTER(38),

    /** Z3 — the machine wall: the drum + the purse. A mechanism zone,
     *  NEVER a row in the B-9 list (R-C3). */
    MACHINE_WALL(22),

    /** Z4 — the deep end: the held pile's second grille. */
    DEEP_END(14),
    ;

    companion object {
        val ruledOrder: List<AntechamberZone> = entries.toList()

        /** The shares are a composition, not a suggestion: they close
         *  over the whole field. */
        fun sharesCloseOverTheField(): Boolean =
            entries.sumOf { it.shareHundredths } == 100

        /** R-C3 as a function: the machine wall is in the room and is
         *  not, under any reading, a member of the B-9 list. */
        fun isListZone(zone: AntechamberZone): Boolean = zone != MACHINE_WALL
    }
}

/** The drum housing's place (Seam S-B, ruled (b) by BUILD-SR-10 with
 *  DESIGNER-SR-3, lettered to BUILD-SR-7): a sibling of the list
 *  column under one room root. The alternative — a drum slot inside
 *  the list primitive — is refused by construction: there is no such
 *  slot type anywhere in this package. */
object DrumHousingLaw {
    enum class HousingPlace { SIBLING_OF_LIST_COLUMN, SLOT_IN_LIST }

    fun housingPlace(): HousingPlace = HousingPlace.SIBLING_OF_LIST_COLUMN

    /** The governor's crank is not in the antechamber at all
     *  (DESIGNER-SR-3 §6): it hangs on the sealed face's side of the
     *  wall. No zone in this room may host it. */
    fun mayStandInRoom(zone: AntechamberZone): Boolean = false
}

/** The held pile's interior, behind the second grille (C-3 rows
 *  S3-3-21/22/23's place). The chamber's shelves, in the order a reach
 *  meets them: what waits · the digits on their held line (B-18:
 *  digits ONLY, one showing priced, the plate strikes the showing
 *  never a digit) · the taking. There is no shelf for words around a
 *  code, none for a sender's name, and no onward path — B-18's law is
 *  carried here as absent shelves, not as a warning. */
enum class HeldPileShelf(val rank: Int) {
    /** What waits: numbers shown whole, on this surface only. */
    WAITING(1),

    /** The digits line: one code's digits, with the hour, alone. */
    DIGITS_LINE(2),

    /** The taking: per-call / per-showing choice — take it, or leave
     *  it waiting at no cost. */
    TAKING(3),
    ;

    companion object {
        val ruledOrder: List<HeldPileShelf> = entries.sortedBy { it.rank }

        fun isLawfulOrder(shelves: List<HeldPileShelf>): Boolean =
            shelves == shelves.sortedBy { it.rank } && shelves.distinct() == shelves
    }
}

/** The tray/drawer composition (LEAD-SLICE-2 piece L2-c; B-16 · E-2 ·
 *  D-8). The drawer screen is a pile and a list — never a dashboard:
 *  the pile stands first (growth is the display), the browsing list
 *  below it, and there is no header, summary, total, or grade member
 *  in this type for a surface to render one with. */
enum class DrawerComposition(val rank: Int) {
    PLATE_PILE(1),
    BROWSING_LIST(2),
    ;

    companion object {
        val ruledOrder: List<DrawerComposition> = entries.sortedBy { it.rank }
        const val COMPOSITION_COUNT: Int = 2
    }
}

object TrayDrawerCompositionLaw {
    /** B-16 at composition depth: the tray side and the signing place
     *  are sibling zones of one room root — composed side by side,
     *  never nested. A composition that nests the tray inside the
     *  signing field is unlawful whatever it renders. */
    fun areSiblings(trayZone: RoomZone, signingZone: RoomZone): Boolean =
        trayZone == RoomZone.TRAY_SIDE && signingZone == RoomZone.SIGNING_PLACE &&
            trayZone != signingZone

    /** E-2's cap as a picture, never a count (DESIGNER-SR-4 §5): at
     *  most this many plate edges are drawn in the stack; beyond it
     *  the drawn height stays and the list carries the season. */
    const val MAX_DRAWN_STACK_EDGES: Int = 24

    fun drawnEdges(pileSize: Int): Int = minOf(pileSize, MAX_DRAWN_STACK_EDGES)
}
