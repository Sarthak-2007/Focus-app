package com.example.service.haptics

/**
 * THE CENSUS — every felt event of the Strongroom day, as an
 * enum (FEEL-SR-5). This is the index FEEL-SR-4's census fills:
 * an event that is not here cannot be fired, and the registry
 * refuses to construct while any event here lacks a row — so
 * the census and the registry can never quietly disagree.
 *
 * Sources, per family: the ledger's day (A-3 · A-5 · A-7 · A-8 ·
 * A-13 · B-18) · the shipped engine's voices on main
 * (`TactileSoundEngine`, censused firsthand this leg) ·
 * BUILD-SR-2's `StrongroomKnock` · FEEL-SR-1's door shapes ·
 * FEEL-SR-3's control finalities · SOUND-SR-4's silence map and
 * CRITIC-SR-1's H-4 (the events whose law is SILENCE are events
 * here too — they carry SILENCE rows, they are not omissions).
 *
 * [weightRank] is H-6's dynamic range made checkable: a heavier
 * mechanism outranks a lighter one, and the registry test holds
 * felt energy monotonic against it across families.
 */
enum class HapticEvent(val family: Family, val weightRank: Int) {

    // ── The door (heaviest things in the app) ──────────────────
    /** Swing + three bolts driving home, one atomic ceremony
     *  (A-3 · B-7 — the Seal at the first sitting, NOT the
     *  evening signing). FEEL-SR-1's DOOR-SEAL. */
    DOOR_SEAL(Family.DOOR, 100),

    /** Bolts withdrawing one by one, then the door opening on
     *  its own weight — no terminal impact (A-13). DOOR-LIFT. */
    DOOR_LIFT(Family.DOOR, 98),

    /** The substrate's single-voice seal (the Throw). Stands for
     *  the pre-Strongroom seal path until DOOR_SEAL replaces it;
     *  one voice, bit-identical, by its own law. */
    SEAL_THROW(Family.DOOR, 95),

    /** The disarm-family clunk (engine reservation law: this
     *  voice belongs to the session boundary and nowhere else). */
    SEAL_CLUNK_DISARM(Family.DOOR, 90),

    // ── The signing (his signature, in the hand) ───────────────
    /** One detent of the combination spin / any rotary wheel —
     *  the engine's rotary voice, spent by the spin in P4. */
    ROTARY_DETENT(Family.SIGNING, 40),

    /** One station of the combination spin — one tick per
     *  number, at the pass's weight (FEEL-SR-3 C1.1's ladder
     *  70 → 95 → 120 across passes; the row carries pass 1, the
     *  ladder is the caller's pass count, as the seat knock's
     *  weight is its caller's). A wrong combination completed
     *  produces THIS tick and nothing else — no error event
     *  exists for the spin, by spec. */
    COMBINATION_STATION(Family.SIGNING, 30),

    /** A spin gate, at a direction reversal: a felt void, then
     *  one heavy tick (FEEL-SR-3 C1.2). */
    COMBINATION_GATE(Family.SIGNING, 48),

    /** The combination's final detent: the signature lands —
     *  ramp, thud, settle (FEEL-SR-3 C1.3, finality shape 1). */
    COMBINATION_FINAL_SEAT(Family.SIGNING, 62),

    /** A reverse attempt against the seated dial: one flat
     *  wall, no tick, identical every time (FEEL-SR-3 C1.3). */
    COMBINATION_END_STOP(Family.SIGNING, 56),

    /** A firm click for heavy controls — the signing's latch. */
    SIGNATURE_LATCH(Family.SIGNING, 58),

    /** The lifting-hour setter's quarter detent: the lightest
     *  firm tick on any setting control (FEEL-SR-3 C2.1). */
    HOUR_DETENT_QUARTER(Family.SIGNING, 14),

    /** The hour setter's whole-hour crossing — one size up, so
     *  a fast drag can be counted in hours by feel (C2.1). */
    HOUR_DETENT_WHOLE(Family.SIGNING, 20),

    /** SILENCE by spec — an hour release away from a quarter
     *  places the hour and fires nothing; only the spin locks
     *  (FEEL-SR-3 C2.2). */
    HOUR_RELEASED(Family.SIGNING, 0),

    /** The break setter's per-minute grain — the quietest
     *  recurring event in the app (FEEL-SR-3 C5.1). */
    BREAK_GRAIN_MINUTE(Family.SIGNING, 6),

    /** The break grain at a 5-minute mark (C5.1). */
    BREAK_GRAIN_FIVE(Family.SIGNING, 10),

    /** Crossing a break preset: one soft swell — topography,
     *  never a wall (D-6 · FEEL-SR-3 C5.2). */
    BREAK_PRESET_NOTCH(Family.SIGNING, 16),

    /** Resting on a preset (≥400 ms dwell) is answered, once
     *  (C5.2's resting pulse). */
    BREAK_PRESET_REST(Family.SIGNING, 15),

    /** Arriving at the break setter's zero end: a flat floor
     *  (C5.2's end-stop). */
    BREAK_ZERO_STOP(Family.SIGNING, 34),

    /** SILENCE by spec — a non-zero break release fires
     *  nothing; the break is priced at the spin (C5.2). */
    BREAK_RELEASED(Family.SIGNING, 0),

    // ── The sealed face (its ONLY felt event is the knock) ─────
    /** A reach for a locked app: one dull heavy thud, felt more
     *  than heard; nothing opens (A-5). */
    KNOCK(Family.SEALED_FACE, 85),

    /** SILENCE by law — a held arrival makes no felt event
     *  (D-5 · SO-5 · H-4). */
    HELD_ARRIVAL(Family.SEALED_FACE, 0),

    /** SILENCE by law — the desk clock forms without a felt
     *  event; a glance costs no attention (E-3 · H-4). */
    DESK_CLOCK_FORMING(Family.SEALED_FACE, 0),

    /** SILENCE by law — a shaped gap arrives inside a standing
     *  seal; the seal does not mark it (C-2 · GAP-SILENCE). */
    GAP_ARRIVAL(Family.SEALED_FACE, 0),

    /** SILENCE by law — the hand-back announces nothing; people
     *  are found back, never announced (B-13). */
    HAND_BACK(Family.SEALED_FACE, 0),

    /** SILENCE by law — after the carve, the strip answers
     *  touches with nothing (FEEL-SR-3, position 4). */
    CARVED_STRIP_TOUCH(Family.SEALED_FACE, 0),

    // ── The antechamber (the drum, the crank, the grille) ──────
    /** Opening the antechamber: one low mechanical touch, for
     *  his hand at his ear's distance (B-9's second touch). */
    GRILLE_TOUCH(Family.ANTECHAMBER, 12),

    /** One detent of the revolving drum's crank. */
    DRUM_DETENT(Family.ANTECHAMBER, 34),

    /** The drum lands: one app rolled to him (A-7). */
    DRUM_LAND(Family.ANTECHAMBER, 52),

    /** The drum minute ends; the app recedes by itself. */
    DRUM_RETURN(Family.ANTECHAMBER, 30),

    /** The purse is spent: the crank WILL NOT TURN. Resistance,
     *  never an error buzz (A-7 · H-3). */
    DRUM_HARD_STOP(Family.ANTECHAMBER, 66),

    /** The governor's crank turning — heavier than the drum's,
     *  by what it costs (A-8). */
    GOVERNOR_CRANK_TURN(Family.ANTECHAMBER, 56),

    /** The lead seal SNAPS: one dry, irreversible crack. */
    GOVERNOR_SEAL_SNAP(Family.ANTECHAMBER, 72),

    /** A spent week: the crank will not turn, identically
     *  silent in shape every time (H-3). */
    GOVERNOR_HARD_STOP(Family.ANTECHAMBER, 68),

    /** Bolts re-drive after the governor's minute is spent —
     *  the seal whole again (FEEL-SR-4 F-13; its figures are
     *  that seat's C-05 PROPOSAL, unruled, carried as such). */
    GOVERNOR_BOLTS_REDRIVE(Family.ANTECHAMBER, 88),

    /** Arming a code showing: rising resistance, deliberately
     *  heavier than the drum's first movement, no click
     *  (B-18 · FEEL-SR-4 F-10). */
    CODE_CRANK_ARM(Family.ANTECHAMBER, 44),

    /** THE SPEND: past the resistance, one hard detent click —
     *  the purse spends one minute at that instant (F-11). */
    CODE_DETENT_SPEND(Family.ANTECHAMBER, 58),

    /** The code crank spent: zero travel, from the first
     *  pressure (F-17). */
    CODE_HARD_STOP(Family.ANTECHAMBER, 60),

    // ── The Guard (tighten / raise / carve) ────────────────────
    /** One detent of the tightening ratchet (D-1: tighten is
     *  instant, and the hand knows it). */
    GUARD_TIGHTEN_DETENT(Family.GUARD, 36),

    /** The tightening lands: one hard seat-click. */
    GUARD_TIGHTEN_SEAT(Family.GUARD, 60),

    /** A raise detent: late, soft-attacked, under-weight —
     *  a notch not yet cut (D-1 · FEEL-SR-3 C4.1, the Hollow
     *  Seat's first half). */
    GUARD_RAISE_DETENT(Family.GUARD, 12),

    /** Lifting after a raise: three dying pulses and NO
     *  terminal event — the withheld seat is the deferral
     *  (FEEL-SR-3 C4.2, the Unseated Rock). */
    GUARD_RAISE_ROCK(Family.GUARD, 18),

    /** Cancelling a resting raise: one soft release (C4.2). */
    GUARD_RAISE_CANCEL(Family.GUARD, 10),

    /** SILENCE by law — the 03:30 boundary fires nothing: a
     *  raise seats, a purse renews, loosenings take hold, all
     *  unwatched and unfelt (R-C1 · FEEL-SR-4 N-05). */
    DAY_START_BOUNDARY(Family.GUARD, 0),

    /** One day-step of the stretch's end-date stepper: the
     *  heaviest recurring detent in the app, rising with
     *  distance (D-3 · FEEL-SR-3 C6.1). */
    STRETCH_DAY_STEP(Family.GUARD, 50),

    /** The carve's scrape: ~1.2 s of rising texture under a
     *  press-and-hold; a released hold gives nothing else
     *  (FEEL-SR-3 C6.2). */
    STRETCH_SCRAPE(Family.GUARD, 24),

    /** The stretch's end-date is carved: one cut, then
     *  silence (D-3 · FEEL-SR-3 C6.2). */
    STRETCH_CARVE(Family.GUARD, 64),

    // ── The break (the time-lock's own firing) ─────────────────
    /** The break arrives: the clockwork eases — lower, shorter,
     *  unhurried; felt more than heard (B-12). */
    BREAK_ARRIVAL(Family.BREAK, 46),

    /** Bolts re-drive when a break's / crank's span is spent:
     *  three seatings, each a step below the swing's clunks. */
    BOLTS_REDRIVE(Family.BREAK, 74),

    /** The break is over: its own double-knock voice, spoken by
     *  nobody else (engine, issue #150). */
    BREAK_END_CUE(Family.BREAK, 70),

    /** The summons back from a break: three full strikes. */
    BREAK_END_SUMMONS(Family.BREAK, 76),

    /** T-minus warning: two soft strikes, clearly a warning and
     *  not an ending. */
    BREAK_WARNING(Family.BREAK, 48),

    // ── The plate & the drawer (the day's telling) ─────────────
    /** The day plate is struck at the lifting (A-10). */
    PLATE_STRIKE(Family.PLATE, 82),

    /** A fallen day's strike: lower, damped, a dead gap cut
     *  into its resonance (A-12 · SOUND-SR-4 FAILURE-STRIKE). */
    PLATE_STRIKE_FAILURE(Family.PLATE, 78),

    /** The drawer slides: low, soft, paper-and-wood weight. */
    DRAWER_OPEN(Family.DRAWER, 22),

    /** A finished task is stored: a small, quiet seating —
     *  storage, never celebration (D-8 · E-5). */
    TASK_STORED(Family.DRAWER, 18),

    // ── The legacy engine voices (subsumed, not rewritten) ────
    // The shipped engine's named voices, carried as rows so the
    // census is whole over the app as it stands. Their waveforms
    // are the engine's own, verbatim; re-pointing their call
    // sites onto the bus is a later piece, not this one.
    LATCH_ENGAGE(Family.LEGACY, 42),
    LATCH_RELEASE(Family.LEGACY, 42),
    SEAT_KNOCK(Family.LEGACY, 54),
    DOOR_ARRIVAL(Family.LEGACY, 50),
    DOOR_ARM(Family.LEGACY, 26),
    DOOR_RETURN(Family.LEGACY, 44),
    DOOR_PEEK(Family.LEGACY, 50),
    DOOR_EXIT(Family.LEGACY, 44),
    PEEK_WARNING_TICK(Family.LEGACY, 28),
    TEAR_PULSE(Family.LEGACY, 38),
    COMPLETION_PULSE(Family.LEGACY, 32);

    enum class Family {
        DOOR, SIGNING, SEALED_FACE, ANTECHAMBER, GUARD, BREAK, PLATE, DRAWER, LEGACY
    }

    /** True when this event's law is a designed silence — its
     *  registry row must be [HapticWaveform.SILENCE]. */
    val isSilentByLaw: Boolean get() = weightRank == 0
}
