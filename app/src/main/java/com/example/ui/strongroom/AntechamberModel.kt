package com.example.ui.strongroom

/**
 * P5 — THE ANTECHAMBER, as a pure model (Slice 1).
 *
 * The room behind the grille door, governed whole by THE LIST
 * LAW (ledger B-9, from the re-fit's Beat 3, which governs over
 * summaries) — this order and no other:
 *
 *  1. **Calling out first and biggest** — people outrank apps
 *     (Stage 5 §3.4 carries the same order for type).
 *  2. **The allowed things as plain words, EXACTLY as signed** —
 *     nothing added, nothing suggested; the signed order stands
 *     and nothing here re-ranks it.
 *  3. **The held pile behind a second step of its own** — in the
 *     room's furniture, a second grille (the re-fit's wording);
 *     the pile is never part of the main list.
 *  4. **Spent things GONE** when the day's open minutes are
 *     spent — never shown dimmed, never greyed. This model has
 *     no dimmed form of anything: a thing is on the list, or it
 *     is not in the room.
 *  5. **If it is not on that list, it is not behind the door** —
 *     the drum refuses any name the signing did not sign.
 *
 * THE DRUM (ledger A-7): crank it and the drum turns ONE app to
 * him for its minute (one minute of the purse per turning — the
 * drum's minute is the purse's smallest unit, B-18); turn it
 * back, or it returns itself; when the purse is spent the crank
 * will not turn — [CrankOutcome.HARD_STOP_SPENT], a hard stop
 * the surface puts in his hand, not an error message.
 *
 * THE HELD PILE carries strangers, held silently (contacts ring
 * through natively and never wait here — the decided contacts
 * rule): a held call is a number, whole, with his choice per
 * call — take it or leave it (TI-35: two handles, neither the
 * lesser). A held CODE stands on its own held line under the
 * digits fork (ledger B-18): digits alone, with the hour, no
 * sender's name, no words around, no way onward — and every
 * SHOWING of it costs one minute of the purse, priced once per
 * taking (re-looks inside the same taking spend nothing; the
 * pile closed and re-opened for the same code is a new showing).
 * The record of a showing is a count of showings — the plate
 * strikes the showing, never a digit.
 */

/** A stranger's call, held: the number as it arrived, and the
 *  hour it arrived at. No name — a stranger has none here. */
data class HeldCall(
    val id: String,
    val number: String,
    val hour: String
)

/** A code, held: digits alone, with the hour (B-18's fence —
 *  no sender's name exists in this type, so none can be shown). */
data class HeldCode(
    val id: String,
    val digits: String,
    val hour: String
)

/** The crank's four answers. */
enum class CrankOutcome {
    /** The drum turned the app to him; one minute is spent. */
    TURNED,

    /** The purse is spent: the crank will not turn. A hard stop
     *  felt in the hand — the surface's answer, not a refusal
     *  dialog. */
    HARD_STOP_SPENT,

    /** Not on the signed list: it is not behind the door (B-9.5). */
    NOT_BEHIND_THE_DOOR,

    /** One app already stands open; the drum turns one at a time. */
    ONE_ALREADY_OPEN
}

/** The showing's four answers (B-18). */
enum class ShowingOutcome {
    /** Shown, and one minute paid from the purse. */
    SHOWN,

    /** The same taking, looked at again: spends nothing. */
    RELOOK_FREE,

    /** The purse cannot pay: the codes wait for the lift. */
    CANNOT_SPEND,

    /** No such code stands in the pile. */
    NO_SUCH_CODE
}

/**
 * The antechamber's whole state. [allowedAsSigned] is the
 * signing's own list, in the signing's own order, carried
 * unchanged — this type never adds, suggests, sorts, or dims.
 * [purseMinutes] is the day's remaining open minutes (P1's purse
 * arithmetic is its source; this is the surface's intake shape).
 */
data class AntechamberState(
    val callingOut: List<String> = emptyList(),
    val allowedAsSigned: List<String>,
    val purseMinutes: Int,
    val openApp: String? = null,
    val heldCalls: List<HeldCall> = emptyList(),
    val heldCodes: List<HeldCode> = emptyList(),
    val openShowingId: String? = null,
    val showingsTaken: Int = 0
) {
    /** The purse is spent — the drum's stock is gone. */
    val purseSpent: Boolean get() = purseMinutes <= 0

    /**
     * B-9.2 + B-9.4, as one getter so no surface can get it
     * wrong: the allowed things, exactly as signed — or, when
     * the day's open minutes are spent, GONE. There is no third
     * form (no dimmed, no greyed) anywhere in this model.
     */
    val visibleAllowed: List<String>
        get() = if (purseSpent) emptyList() else allowedAsSigned

    /** The pile's whole weight, for its second grille's face. */
    val heldCount: Int get() = heldCalls.size + heldCodes.size

    /** Crank the drum to [app]. One minute per turning. */
    fun crankTo(app: String): Pair<AntechamberState, CrankOutcome> = when {
        app !in allowedAsSigned ->
            this to CrankOutcome.NOT_BEHIND_THE_DOOR
        purseSpent ->
            this to CrankOutcome.HARD_STOP_SPENT
        openApp != null ->
            this to CrankOutcome.ONE_ALREADY_OPEN
        else ->
            copy(openApp = app, purseMinutes = purseMinutes - 1) to CrankOutcome.TURNED
    }

    /** Turn the open app back (it also returns itself when its
     *  minute is spent — the caller fires this at the minute). */
    fun returnOpen(): AntechamberState = copy(openApp = null)

    /** Take a held call: it leaves the pile, into his hand. */
    fun takeCall(id: String): AntechamberState =
        copy(heldCalls = heldCalls.filterNot { it.id == id })

    /** Leave a held call: his choice, per call — it leaves the
     *  pile unanswered. */
    fun leaveCall(id: String): AntechamberState =
        copy(heldCalls = heldCalls.filterNot { it.id == id })

    /**
     * Show a held code's line (B-18): one minute per TAKING.
     * Re-looks inside the open taking are free; a code shown
     * while another taking stands open closes that taking and
     * pays its own minute.
     */
    fun showCode(id: String): Pair<AntechamberState, ShowingOutcome> = when {
        heldCodes.none { it.id == id } ->
            this to ShowingOutcome.NO_SUCH_CODE
        openShowingId == id ->
            this to ShowingOutcome.RELOOK_FREE
        purseSpent ->
            this to ShowingOutcome.CANNOT_SPEND
        else ->
            copy(
                openShowingId = id,
                purseMinutes = purseMinutes - 1,
                showingsTaken = showingsTaken + 1
            ) to ShowingOutcome.SHOWN
    }

    /** Close the pile: the open taking ends. Re-opening the pile
     *  for the same code afterwards is a new showing (B-18). */
    fun closePile(): AntechamberState = copy(openShowingId = null)
}
