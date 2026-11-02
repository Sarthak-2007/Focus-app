package com.example.ui.guard

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * P7 — THE GUARD SURFACES' MODEL (BUILD-SR-5, build-first-usable).
 *
 * Pure Kotlin, no Android imports — the same law P1's domain
 * stands under. This file is the surface's whole law: which
 * controls EXIST on the Guard surface at a given moment, and
 * the exact words it stands under. The Compose screen renders
 * this model and nothing else; a control that is not in the
 * inventory is not greyed, not dimmed, not hinted — it is
 * ABSENT, past the seal, exactly as the rulings file it.
 *
 * THE CONTRACT IT STANDS OVER (P1, BUILD-SR-3 — read firsthand
 * in that lane's worktree; the files are not on main yet, so the
 * seam is named, not imported): `StrongroomGuard` transitions —
 * tighten instant · requestRaise → Scheduled(nextDayStart) or
 * BeyondHardStop when the purse is spent · requestOff →
 * Scheduled(nextDayStart) or Refused(SEALED_ACTIVE /
 * STRETCH_ACTIVE) · turnOn instant · beginStretch (the type
 * carries NO end/cancel function — the end date is the exit).
 *
 * SEAM (wiring, P9 composition): [GuardSurfaceSnapshot] mirrors
 * `StrongroomGuard.GuardState` field-for-field; the wiring layer
 * builds it from `StrongroomStore.guardState()` plus facts only
 * the engine/substrate can supply (sealedBlockActive from P2,
 * the day's purse fact the store already feeds the Guard,
 * routine enforcingNow from `RoutineSchedule.isEnforcingAt`),
 * and routes each [GuardIntent] back to the store call of the
 * same name (tightenGuard / requestGuardRaise / requestGuardOff
 * / turnGuardOn / beginGuardStretch). No ViewModel is invented
 * here to guess at theirs.
 *
 * G-a — UNRESOLVED, CARRIED BY NAME (never settled by this seat):
 * ledger item G-a / OBJ-T7-4 asks whether a frozen-allowance
 * RAISE travels the FX-29 two-signature road (B-6: a loosening
 * lands at the first day-start after the SECOND signature) or
 * the Guard's own next-day-start road (D-1). The decider is Ren.
 * This surface applies D-1 AS FILED — a raise stands from the
 * next day-start — and builds NO two-signature control, prompt,
 * or counter anywhere. If the decider rules for FX-29, the
 * raise section gains a signature step at composition; nothing
 * here pre-builds or pre-denies it.
 */

// ----------------------------------------------------------------------
// The snapshot — the Guard's facts, as they stand at one reading
// ----------------------------------------------------------------------

/** One routine, as the Guard surface may show it (D-9: routines
 *  settle inside this programme, under the Guard direction).
 *  [pendingOffFromMillis] is the routine's own slow-off landing
 *  (0 = none) — a routine's off travels the Guard's slow road,
 *  the only off-timing the rulings contain (see the D-9 reading
 *  in this lane's opening line). */
data class GuardRoutineSnapshot(
    val id: Int,
    val name: String,
    val enabled: Boolean,
    val enforcingNow: Boolean = false,
    val pendingOffFromMillis: Long = 0L
)

/**
 * The Guard surface's whole input. Field names mirror
 * `StrongroomGuard.GuardState` one-for-one where the Guard owns
 * the fact; the rest are the named seam facts above.
 *
 * There is deliberately NO field that could hold a count of
 * exits, a stockpile, a witness, or an urge: D-4's exits are
 * advance-chosen NAMES plus the week's valve fact, and D-3
 * forbids the witness outright — a type with no field for a
 * thing is the strongest absence this lane can build.
 */
data class GuardSurfaceSnapshot(
    val guardOn: Boolean,
    val allowanceMinutes: Int,
    val pendingAllowanceMinutes: Int = -1,
    val pendingAllowanceFromMillis: Long = 0L,
    val pendingOffFromMillis: Long = 0L,
    val stretchEndMillis: Long = 0L,
    val nowMillis: Long,
    val nextDayStartMillis: Long,
    val sealedBlockActive: Boolean,
    val purseSpentToday: Boolean,
    val crankMinuteSpent: Boolean,
    val advanceChosenExits: List<String> = emptyList(),
    val routines: List<GuardRoutineSnapshot> = emptyList()
) {
    init {
        require(allowanceMinutes >= 0) { "allowance cannot be negative" }
        require(nextDayStartMillis > nowMillis) { "the next day-start stands ahead of the reading" }
    }

    /** A stretch stands while its chosen end is still ahead —
     *  P1's `GuardState.stretchActive`, restated at the seam. */
    val stretchActive: Boolean
        get() = stretchEndMillis > 0L && nowMillis < stretchEndMillis
}

// ----------------------------------------------------------------------
// The control inventory — every control this surface may ever render
// ----------------------------------------------------------------------

/**
 * The closed census of Guard-surface controls. The screen may
 * render a control ONLY when this model's inventory contains
 * it for the standing snapshot. There is no entry for a witness
 * or urge button (D-3), none for spending the emergency valve
 * from this calm surface (the valve is spent inside the sealed
 * day, P5's ground — D-4 presents it here, it is not spent
 * here), and none for ending a stretch early (D-3: its end
 * date is the exit; P1's type has no such function either).
 */
enum class GuardControl {
    /** The tightening wheel: always present — tightening is the
     *  strong hour's direction, lawful anytime, even sealed,
     *  even inside a stretch (P1 tighten carries no refusal). */
    ALLOWANCE_TIGHTEN_WHEEL,

    /** The raising wheel + its confirmation. Present ONLY while
     *  today's purse is unspent; once spent, the loosening side
     *  sits beyond the hard stop and leaves the inventory whole
     *  (D-1 — absent, never greyed). */
    ALLOWANCE_RAISE_WHEEL,
    ALLOWANCE_RAISE_CONFIRM,

    /** On is instant (D-2): present when the Guard is off, and
     *  while a slow-off is pending (the on cancels it — the
     *  newest word stands, per P1 turnOn). */
    GUARD_TURN_ON,

    /** The off request (D-2): present only while the Guard
     *  stands with no off already pending, no sealed block
     *  active, and no stretch standing. In every other state it
     *  does not exist on this surface. */
    GUARD_REQUEST_OFF,

    /** Stretch setup: the end-date stepper + the begin plate.
     *  Present only while no stretch stands — inside a stretch
     *  there is nothing to set up and nothing to switch off. */
    STRETCH_END_STEPPER,
    STRETCH_BEGIN_CONFIRM,

    /** Per-routine plates (D-9). ON is instant; OFF travels the
     *  slow road and is absent while that routine's window is
     *  enforcing — the substrate's own refusal, carried up. */
    ROUTINE_TURN_ON,
    ROUTINE_TURN_OFF
}

// ----------------------------------------------------------------------
// Intents — what the surface asks the wiring layer to do
// ----------------------------------------------------------------------

/** One intent per lawful act, named for the P1/store call it
 *  routes to. No intent exists for ending a stretch, spending
 *  the valve here, or logging a witness/urge — by construction. */
sealed interface GuardIntent {
    data class Tighten(val newAllowanceMinutes: Int) : GuardIntent
    data class RequestRaise(val newAllowanceMinutes: Int) : GuardIntent
    object RequestOff : GuardIntent
    object TurnOn : GuardIntent
    data class BeginStretch(val endMillis: Long) : GuardIntent
    data class SetRoutineEnabled(val routineId: Int, val enabled: Boolean) : GuardIntent
}

// ----------------------------------------------------------------------
// The derived surface
// ----------------------------------------------------------------------

/** One routine's row on the Guard surface: its words and the
 *  controls that exist FOR THIS ROUTINE at this reading. */
data class GuardRoutineRow(
    val routine: GuardRoutineSnapshot,
    val controls: Set<GuardControl>,
    val line: String?
)

/**
 * The Guard surface, derived whole from one snapshot: the
 * control inventory, every line it stands under, and the
 * per-routine rows. The screen is a rendering of this value.
 */
data class GuardSurfaceModel(
    val controls: Set<GuardControl>,
    val standingLine: String,
    val allowanceLine: String,
    val raiseLine: String?,
    val pendingRaiseLine: String?,
    val hardStopLine: String?,
    val pendingOffLine: String?,
    val stretchLine: String?,
    val valveLine: String,
    val exitNames: List<String>,
    val routineRows: List<GuardRoutineRow>
) {
    fun has(control: GuardControl): Boolean = control in controls
}

/** The surface's words, in one place (Slice 3 owns final copy;
 *  these are the build's plain truths, each traceable to its
 *  ledger row — no word here promises what a ruling refuses). */
object GuardCopy {
    const val TITLE: String = "THE GUARD"
    const val STANDING_ON: String = "The Guard stands."
    const val STANDING_OFF: String = "The Guard is off."
    const val TIGHTEN_TITLE: String = "Tighten — it stands at once"
    const val RAISE_TITLE: String = "Raise — from the next day-start"

    /** The raise control says its own truth, plainly (D-1):
     *  never same-day, from the next day-start, 03:30. */
    const val RAISE_LAW: String =
        "A raise never lands today. It stands from the next day-start — 03:30 — and not an hour before."

    /** Once the purse is spent there is no loosening control to
     *  point at; this line is testimony, not a control (D-1). */
    const val HARD_STOP: String =
        "Today's minutes are spent. What would loosen this stands beyond the hard stop — there is no loosening control on this surface today."

    const val OFF_LAW: String =
        "Off lands at the next day-start — 03:30. Until then the Guard stands."
    const val ON_LAW: String = "On stands at once."
    const val STRETCH_SETUP_LAW: String =
        "Choose the end. That date is the exit — inside a stretch there is no off."
    const val EXITS_TITLE: String = "The ways out, chosen in advance"
    const val EXITS_LAW: String =
        "Only what was chosen in advance stands here. Nothing is stockpiled; nothing can be drawn on the day."
    // No collective header stands over the routines (TYPE-SR-1's
    // word dispute, deck §12.7: the collective noun is scheduler
    // vocabulary the word-bench already killed as room voice).
    // They stand under their own names only, grouped by place on
    // the surface — there is deliberately no ROUTINES_TITLE here.
    const val ROUTINES_LAW: String =
        "A routine's on stands at once. Its off travels the Guard's slow road, from the next day-start."

    fun allowanceLine(minutes: Int): String =
        "The day's allowance stands at ${minutesText(minutes)}."

    fun pendingRaiseLine(minutes: Int, fromMillis: Long, zone: ZoneId): String =
        "A raise to ${minutesText(minutes)} stands from ${dayStartText(fromMillis, zone)}."

    fun pendingOffLine(fromMillis: Long, zone: ZoneId): String =
        "Off stands from ${dayStartText(fromMillis, zone)} — until then the Guard holds."

    fun stretchLine(endMillis: Long, zone: ZoneId): String =
        "This stretch ends ${dateText(endMillis, zone)}. That date is the exit. There is no off inside it."

    fun valveLine(spent: Boolean): String =
        if (spent) "The week's emergency minute is spent. Its snapped seal stands in the tray, where it stays."
        else "The week's emergency minute stands whole, under its lead seal."

    fun routinePendingOffLine(fromMillis: Long, zone: ZoneId): String =
        "Off stands from ${dayStartText(fromMillis, zone)}."

    const val ROUTINE_ENFORCING_LINE: String =
        "Its window is enforcing now — there is no off to offer."

    fun minutesText(minutes: Int): String = when {
        minutes <= 0 -> "no minutes"
        minutes == 1 -> "1 minute"
        minutes < 60 -> "$minutes minutes"
        minutes % 60 == 0 -> if (minutes == 60) "1 hour" else "${minutes / 60} hours"
        else -> "${minutes / 60} h ${minutes % 60} min"
    }

    private val DATE_FORMAT: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.UK)

    private val DAY_START_FORMAT: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEEE d MMMM, 03:30", Locale.UK)

    fun dateText(millis: Long, zone: ZoneId): String =
        Instant.ofEpochMilli(millis).atZone(zone).format(DATE_FORMAT)

    fun dayStartText(millis: Long, zone: ZoneId): String =
        Instant.ofEpochMilli(millis).atZone(zone).format(DAY_START_FORMAT)
}

/** The surface derivation — the piece's whole law in one place. */
object GuardSurface {

    fun surfaceFor(
        snapshot: GuardSurfaceSnapshot,
        zone: ZoneId = ZoneId.systemDefault()
    ): GuardSurfaceModel {
        val controls = mutableSetOf<GuardControl>()

        // D-1 — tightening is always lawful.
        controls += GuardControl.ALLOWANCE_TIGHTEN_WHEEL

        // D-1 — the loosening side exists only while today's
        // purse is unspent. Spent: beyond the hard stop, absent.
        if (!snapshot.purseSpentToday) {
            controls += GuardControl.ALLOWANCE_RAISE_WHEEL
            controls += GuardControl.ALLOWANCE_RAISE_CONFIRM
        }

        // D-2 — on is instant: offered when off, and while an
        // off is pending (the on is the cancelling word).
        if (!snapshot.guardOn || snapshot.pendingOffFromMillis > 0L) {
            controls += GuardControl.GUARD_TURN_ON
        }

        // D-2 + D-3 — the off exists only on calm ground: the
        // Guard standing, no off already pending, no sealed
        // block active, no stretch standing.
        if (snapshot.guardOn &&
            snapshot.pendingOffFromMillis <= 0L &&
            !snapshot.sealedBlockActive &&
            !snapshot.stretchActive
        ) {
            controls += GuardControl.GUARD_REQUEST_OFF
        }

        // D-3 — stretch setup exists only outside a stretch.
        if (!snapshot.stretchActive) {
            controls += GuardControl.STRETCH_END_STEPPER
            controls += GuardControl.STRETCH_BEGIN_CONFIRM
        }

        val routineRows = snapshot.routines.map { routine ->
            val rowControls = mutableSetOf<GuardControl>()
            when {
                !routine.enabled -> rowControls += GuardControl.ROUTINE_TURN_ON
                routine.pendingOffFromMillis > 0L -> Unit // off already travelling; no plate
                routine.enforcingNow -> Unit // enforcing: the off does not exist for this row
                else -> rowControls += GuardControl.ROUTINE_TURN_OFF
            }
            controls += rowControls
            GuardRoutineRow(
                routine = routine,
                controls = rowControls,
                line = when {
                    routine.pendingOffFromMillis > 0L ->
                        GuardCopy.routinePendingOffLine(routine.pendingOffFromMillis, zone)
                    routine.enabled && routine.enforcingNow -> GuardCopy.ROUTINE_ENFORCING_LINE
                    else -> null
                }
            )
        }

        return GuardSurfaceModel(
            controls = controls,
            standingLine = if (snapshot.guardOn) GuardCopy.STANDING_ON else GuardCopy.STANDING_OFF,
            allowanceLine = GuardCopy.allowanceLine(snapshot.allowanceMinutes),
            raiseLine = if (GuardControl.ALLOWANCE_RAISE_WHEEL in controls) GuardCopy.RAISE_LAW else null,
            pendingRaiseLine = if (snapshot.pendingAllowanceMinutes >= 0) {
                GuardCopy.pendingRaiseLine(
                    snapshot.pendingAllowanceMinutes,
                    snapshot.pendingAllowanceFromMillis,
                    zone
                )
            } else null,
            hardStopLine = if (snapshot.purseSpentToday) GuardCopy.HARD_STOP else null,
            pendingOffLine = if (snapshot.pendingOffFromMillis > 0L) {
                GuardCopy.pendingOffLine(snapshot.pendingOffFromMillis, zone)
            } else null,
            stretchLine = if (snapshot.stretchActive) {
                GuardCopy.stretchLine(snapshot.stretchEndMillis, zone)
            } else null,
            valveLine = GuardCopy.valveLine(snapshot.crankMinuteSpent),
            exitNames = snapshot.advanceChosenExits,
            routineRows = routineRows
        )
    }

    /** The tightening wheel's lawful detents: 5-minute stops
     *  from nothing up to the allowance in force, the allowance
     *  itself always a stop (a legacy allowance off the 5-grid
     *  must still be reachable as the wheel's standing point). */
    fun tightenDetents(allowanceMinutes: Int): List<Int> {
        val stops = (0..allowanceMinutes step GuardWheel.STEP_MINUTES).toMutableList()
        if (stops.lastOrNull() != allowanceMinutes) stops += allowanceMinutes
        return stops
    }

    /** A stretch's end, at its day-start: the chosen date's
     *  03:30 — the same seam every Guard change lands on. */
    fun stretchEndMillis(date: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Long =
        date.atTime(3, 30).atZone(zone).toInstant().toEpochMilli()
}
