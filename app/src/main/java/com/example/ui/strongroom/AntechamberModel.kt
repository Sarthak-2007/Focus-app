package com.example.ui.strongroom

/**
 * P5 — THE ANTECHAMBER, as a pure model (Slice 1; RETHOUGHT
 * under coordinator ruling C-3, 2026-11-02 — the first filing
 * priced the signed set through the drum, and the ruling names
 * that as the error it is: the purse never pays for the day's
 * own shape).
 *
 * TWO SETS, MODELLED DISTINCTLY (ruling C-3):
 *
 *  SET 1 — THE SIGNED ALLOWED SET. Apps settled at the signing,
 *  living here as plain words exactly as signed (the list law,
 *  ledger B-9, governs their presentation), reachable WITHOUT
 *  the purse — they were decided while he was strong. The purse
 *  cannot spend them, dim them, or remove them: this model's
 *  [visibleAllowed] is the signed list at every purse state.
 *
 *  SET 2 — THE DRUM (ledger A-7). Any reach BEYOND the signed
 *  set: one app at a time, for its minute, purse-priced,
 *  self-returning. The drum is a separate mechanism, never a
 *  row in Set 1's list. Its stock is [drumCandidates] — the
 *  room's other names, as plain words. When the purse is spent
 *  the crank will not turn ([CrankOutcome.HARD_STOP_SPENT] —
 *  a hard stop the surface puts in his hand) and the drum's
 *  stock is gone from the room: [visibleDrumStock] is empty.
 *  That — the drum's offerings, not the signed set — is where
 *  B-9.4's "spent things gone, never dimmed" now reads, under
 *  C-3's scoping of the list law to Set 1's presentation.
 *
 * THE LIST LAW (B-9) still governs the room's order whole:
 * calling out first and biggest · Set 1 as plain words exactly
 * as signed, nothing added, nothing suggested, signed order
 * untouched · the held pile behind a second step of its own ·
 * if it is not on a list, it is not behind the door — the drum
 * refuses names that stand on neither list, and a signed name
 * refused by the drum points back at Set 1, where it lives.
 *
 * THE PURSE'S FORM (ruling R2-4): one purse for the drum and
 * the code-showings alike; its state is readable from the
 * drum mechanism's own form — never as a number on any surface
 * (this model carries the count because arithmetic needs it;
 * no surface in this lane prints it). A code-taking states its
 * price AT the taking, before he takes it (the surface's part).
 *
 * THE HELD PILE carries strangers, held silently (contacts ring
 * through natively and never wait here): a held call is a
 * number, whole, with his choice per call — take it or leave
 * it (TI-35: two handles, neither the lesser). A held CODE
 * stands on its own held line under the digits fork (B-18):
 * digits alone, with the hour, no sender's name, no words
 * around, no way onward — every SHOWING costs one minute of
 * the purse, priced once per taking (re-looks inside the same
 * taking spend nothing; the pile closed by his act and
 * re-opened is a new showing; a taking survives a restart
 * unpriced again — R2-5 — because the taking is written state,
 * and this state is that writing's shape). The record of a
 * showing is a count of showings — the plate strikes the
 * showing, never a digit.
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

/** The room's opening answers — one enum for both sets, so a
 *  refusal always says which law refused. */
enum class CrankOutcome {
    /** The drum turned the app to him; one minute is spent. */
    TURNED,

    /** A signed app opened directly — no purse, no drum. */
    OPENED_FROM_THE_SIGNED_SET,

    /** The purse is spent: the crank will not turn. A hard stop
     *  felt in the hand — the surface's answer, not a dialog. */
    HARD_STOP_SPENT,

    /** One app already stands open, from either set. */
    ONE_ALREADY_OPEN,

    /** The name lives in the SIGNED set — it is not drum stock;
     *  it opens from its own list, without the purse. */
    IN_THE_SIGNED_SET,

    /** On neither list: it is not behind the door (B-9.5). */
    NOT_IN_THE_ROOM
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
 * [drumCandidates] is the room's beyond-set names, plain words.
 * [purseMinutes] is the day's remaining open minutes (P1's purse
 * arithmetic is its source; this is the surface's intake shape).
 */
data class AntechamberState(
    val callingOut: List<String> = emptyList(),
    val allowedAsSigned: List<String>,
    val drumCandidates: List<String> = emptyList(),
    val purseMinutes: Int,
    val openApp: String? = null,
    val openViaDrum: Boolean = false,
    val heldCalls: List<HeldCall> = emptyList(),
    val heldCodes: List<HeldCode> = emptyList(),
    val openShowingId: String? = null,
    val showingsTaken: Int = 0
) {
    /** The purse is spent — the drum's stock is gone. */
    val purseSpent: Boolean get() = purseMinutes <= 0

    /**
     * SET 1, at every purse state (ruling C-3): the signed list,
     * exactly as signed. The purse never buys the day's own
     * shape, so nothing here is spent, dimmed, or removed by it.
     */
    val visibleAllowed: List<String> get() = allowedAsSigned

    /** SET 2's stock: the drum's candidates — or, when the purse
     *  is spent, GONE (B-9.4 as C-3 scopes it). Never dimmed:
     *  there is no dimmed form anywhere in this model. */
    val visibleDrumStock: List<String>
        get() = if (purseSpent) emptyList() else drumCandidates

    /** The pile's whole weight, for its second grille's face. */
    val heldCount: Int get() = heldCalls.size + heldCodes.size

    /**
     * Open a SIGNED app (Set 1): directly, without the purse —
     * it was decided while he was strong. One thing stands open
     * at a time across both sets.
     */
    fun openSigned(app: String): Pair<AntechamberState, CrankOutcome> = when {
        app !in allowedAsSigned ->
            this to CrankOutcome.NOT_IN_THE_ROOM
        openApp != null ->
            this to CrankOutcome.ONE_ALREADY_OPEN
        else ->
            copy(openApp = app, openViaDrum = false) to
                CrankOutcome.OPENED_FROM_THE_SIGNED_SET
    }

    /**
     * Crank the drum to [app] (Set 2): a reach beyond the signed
     * set, one minute per turning. A signed name is refused by
     * name — it lives in Set 1 and opens there, purse-free.
     */
    fun crankTo(app: String): Pair<AntechamberState, CrankOutcome> = when {
        app in allowedAsSigned ->
            this to CrankOutcome.IN_THE_SIGNED_SET
        app !in drumCandidates ->
            this to CrankOutcome.NOT_IN_THE_ROOM
        purseSpent ->
            this to CrankOutcome.HARD_STOP_SPENT
        openApp != null ->
            this to CrankOutcome.ONE_ALREADY_OPEN
        else ->
            copy(
                openApp = app,
                openViaDrum = true,
                purseMinutes = purseMinutes - 1
            ) to CrankOutcome.TURNED
    }

    /** Turn the open app back, whichever set it came from (the
     *  drum's also returns itself when its minute is spent —
     *  the caller fires this at the minute). */
    fun returnOpen(): AntechamberState =
        copy(openApp = null, openViaDrum = false)

    /** Take a held call: it leaves the pile, into his hand. */
    fun takeCall(id: String): AntechamberState =
        copy(heldCalls = heldCalls.filterNot { it.id == id })

    /** Leave a held call: his choice, per call — it leaves the
     *  pile unanswered. */
    fun leaveCall(id: String): AntechamberState =
        copy(heldCalls = heldCalls.filterNot { it.id == id })

    /**
     * Show a held code's line (B-18): one minute per TAKING,
     * drawn from the same one purse as the drum (R2-4). Re-looks
     * inside the open taking are free; a code shown while
     * another taking stands open closes that taking and pays
     * its own minute. A taking survives restart as written
     * state (R2-5) — restart is not a closing by his act.
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

    /** Close the pile BY HIS ACT: the open taking ends.
     *  Re-opening the pile for the same code afterwards is a
     *  new showing (B-18). */
    fun closePile(): AntechamberState = copy(openShowingId = null)
}
