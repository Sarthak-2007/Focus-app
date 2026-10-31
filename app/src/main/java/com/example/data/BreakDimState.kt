package com.example.data

/**
 * Issue #143 F1/F6: the shield loop's published break phase — the
 * end-of-break signal the finding found missing (the accessibility
 * service had nothing to hang a fade on, and the UI had no break
 * clock of its own).
 *
 * Published by MonolithShieldService on every monitor iteration:
 *  - [inBreak] while the session sits in a Safe Break, with
 *    [breakElapsedSeconds] counting up from the break's start —
 *    the dimming progression's input.
 *  - At the iteration where the break flips off, the service
 *    stamps [breakEndedMono] (monotonic) and [fadeFromAlpha] (the
 *    depth the descent had reached), so the UI can run the fade
 *    OUT on its own 100ms clock and retire the state after
 *    [NightDimMath.FADE_OUT_MILLIS].
 *  - All-default = no break, no fade: the night is not dimming.
 *
 * In-memory only, never persisted — a process death mid-fade just
 * ends the fade, which is the truth of a dead process anyway.
 */
data class BreakDimState(
    val inBreak: Boolean = false,
    val breakElapsedSeconds: Int = 0,
    val breakEndedMono: Long = 0L,
    val fadeFromAlpha: Float = 0f
)
