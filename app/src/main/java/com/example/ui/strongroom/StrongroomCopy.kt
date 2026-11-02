package com.example.ui.strongroom

import androidx.annotation.StringRes
import com.example.R

/**
 * THE COPY FACADE — Stage 9 (BUILD-SR-17, LEAD-SLICE-3 S3-1).
 *
 * Surfaces consume copy through this facade, by deck address —
 * never by holding a literal of their own. The words themselves
 * live in exactly one home: `res/values/strongroom_strings.xml`.
 *
 * Only authored fixed strings have keys here (23). Deck rows
 * ruled ∅ have NO key — absence is enforced by having nothing to
 * consume — and carried content (his words, signed names, arrived
 * figures) is bound as values at runtime, never through here.
 */
object StrongroomCopy {

    // §1.2 — the four day-shapes
    @StringRes val DayShapeOrdinary: Int = R.string.copy_1_2_day_shape_ordinary
    @StringRes val DayShapeTest: Int = R.string.copy_1_2_day_shape_test
    @StringRes val DayShapeSmall: Int = R.string.copy_1_2_day_shape_small
    @StringRes val DayShapeRest: Int = R.string.copy_1_2_day_shape_rest

    // §1.5–§1.8 — the signing's VERBATIM-LOCKED lines
    @StringRes val Purpose: Int = R.string.copy_1_5_purpose
    @StringRes val CodesWaitTonight: Int = R.string.copy_1_6_codes_wait_tonight
    @StringRes val Clause: Int = R.string.copy_1_7_clause
    @StringRes val Telling: Int = R.string.copy_1_8_telling

    // §1.9 — the exit names, whole, in ruled order
    @StringRes val ExitCharger: Int = R.string.copy_1_9_exit_charger
    @StringRes val ExitPresetPin: Int = R.string.copy_1_9_exit_preset_pin
    @StringRes val ExitTemporaryUnlock: Int = R.string.copy_1_9_exit_temporary_unlock
    @StringRes val ExitDeactivationCooldown: Int = R.string.copy_1_9_exit_deactivation_cooldown
    @StringRes val ExitTimeItself: Int = R.string.copy_1_9_exit_time_itself

    /** The five exit names in the deck's ruled order (§1.9). */
    val ExitNamesInOrder: List<Int> = listOf(
        ExitCharger, ExitPresetPin, ExitTemporaryUnlock,
        ExitDeactivationCooldown, ExitTimeItself
    )

    // §3.2 — the doorway's one plain line
    @StringRes val DayStandsComposed: Int = R.string.copy_3_2_day_stands_composed

    // §4.4 — the antechamber's standing state line
    @StringRes val SealStands: Int = R.string.copy_4_4_seal_stands

    // §5.3 — the per-call choice
    @StringRes val TakeIt: Int = R.string.copy_5_3_take_it
    @StringRes val LeaveIt: Int = R.string.copy_5_3_leave_it

    // §5.6 — the showing's price line
    @StringRes val OneMinute: Int = R.string.copy_5_6_one_minute

    // §7.6 — RECORD-ONLY (A-12 bracket words): no surface consumes
    // this key; it is the settled words' one code home (deck §7.6).
    @StringRes val BracketWordsRecordOnly: Int = R.string.copy_7_6_bracket_words

    // §8.2 — the break's state line
    @StringRes val BreakStands: Int = R.string.copy_8_2_break_stands

    // §10.2 — the one plain notification (ruled, A-6.2)
    @StringRes val Notification: Int = R.string.copy_10_2_notification

    // §12.2 — every pending loosening's one line
    @StringRes val StandsFromNextDayStart: Int = R.string.copy_12_2_stands_from_next_day_start

    // §12.4 — the stretch's term, at setup only
    @StringRes val EndDateIsTheExit: Int = R.string.copy_12_4_end_date_is_the_exit
}
