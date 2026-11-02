package com.example.session

/**
 * THE STRONGROOM'S OWN ORDER — the rebuild's routing state
 * (BUILD-SR-34, ruling A-1's rebuild obligation executed).
 *
 * This type replaces [MonolithDestination] (DECK / TASKS / SHIELD /
 * VAULT), which died with the old shell: there are no destinations
 * in the fresh app, no rail, no tabs, no places to navigate between
 * in the app sense (census GAP-R01: navigation glue
 * DOES-NOT-EXIST). There is a day, in an order, and these are its
 * places — hosts the surface slices author inside, never screens a
 * user picks from a menu:
 *
 *  - [DOORWAY] — the doorway / signing place. The app opens here;
 *    unsealed hours live here (the signing, the plate's reading,
 *    the drawer's room — furniture of this place, not destinations).
 *  - [SEALED_FACE] — the sealed face. While a seal stands, the
 *    launcher IS this face and nothing else (ledger D-7).
 *  - [ANTECHAMBER] — the room behind the grille, reached from the
 *    sealed face by the deliberate second touch alone, and left by
 *    returning to the face. A step inside the seal, not a route
 *    around it.
 *  - [BREAK] — the break's span, inside the sealed day. No one
 *    navigates here: the time-lock fires it (census C-16).
 *  - [LIFTING] — the lifting, the seal's end as an event: handing
 *    back first, then the plate strike, then the doorway again
 *    (census C-17 / C-11 / C-06). Engine-driven, never chosen.
 *
 * Movement law is enforced in [MonolithViewModel.moveToPlace];
 * the day's engine-driven shape is derived by the shell host from
 * the kept engine's session state. The two never disagree: a place
 * the state does not support cannot be stood in.
 */
enum class StrongroomPlace {
    DOORWAY,
    SEALED_FACE,
    ANTECHAMBER,
    BREAK,
    LIFTING;

    /** True for the places that exist only inside a standing seal. */
    val isSealedFamily: Boolean
        get() = this == SEALED_FACE || this == ANTECHAMBER || this == BREAK

    companion object {
        /**
         * THE ENUM-DEATH MIGRATION (A-1 rider, hard obligation):
         * [com.example.data.PreArmStore] persists the standing
         * place's NAME under the same key the dead enum used. A
         * name written by an old build — DECK, TASKS, SHIELD,
         * VAULT — and any unknown or absent name resolves to
         * [DOORWAY], the signing place: never a crash (the old
         * `valueOf` seed could only be saved by its runCatching),
         * never a dead route, never a silent remap onto a sealed
         * place. A sealed-family name is likewise never honoured
         * as a standing fact on its own: the live engine state
         * decides whether a seal stands (the shell host derives
         * it); the persisted name seeds only the unsealed return,
         * and every sealed-family name falls back to [DOORWAY]
         * here for the same reason — a stored word must not be
         * able to conjure a seal that does not exist.
         */
        fun fromPersistedName(name: String?): StrongroomPlace = when (name) {
            DOORWAY.name -> DOORWAY
            else -> DOORWAY
        }
    }
}
