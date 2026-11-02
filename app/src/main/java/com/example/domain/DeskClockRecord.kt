package com.example.domain

/**
 * P8a — THE DESK CLOCK'S FORMING RECORD (BUILD-SR-6, build-first-usable).
 *
 * Pure Kotlin, no Android imports — the same law P1's [StrongroomDay]
 * stands under. Every input arrives as a plain value (offsets and
 * durations in the seal's elapsed basis), so the whole record is
 * unit-testable on a bare JVM, and so this file names its seam with
 * P1 instead of importing it: at composition, a thin adapter maps
 * `StrongroomDay.SealSpan` (sealedAt / spanDuration), its `Punch`
 * list (kind + atOffsetMillis) and its `ShapedGap` list onto the
 * facts below. Nothing in P1 needs to change for that adapter.
 *
 * THE LAW THIS FILE CARRIES (Audit A §2.3e + §4 · ledger E-3 ·
 * Stage 5 §7, the forming record TI-12 / FRM-1):
 *
 *  - NO NUMBERS. The record carries no figure of any kind: no
 *    elapsed, no remaining, no count that is ever displayed. Quantity
 *    is carried by EXTENT — the formed length is the amount (FF-1:
 *    figures are prohibited, not styled). The grain count below is
 *    an internal drawing grain, never surfaced: no label, no text,
 *    no API in this file returns a number for display.
 *  - THE RECORD FORMS AS A PREFIX. At every reading, everything the
 *    record shows lies inside the formed extent — the beginning of
 *    the completed record, and nothing beyond it. A mark is
 *    inscribed only once its moment has passed; a shaped gap is
 *    drawn only for the part of it already lived. What is unformed
 *    reads as NOT-YET, never as content (PF-C).
 *  - MONOTONIC. The formed extent is a pure function of elapsed
 *    time inside the span, capped at the span: it never reverses,
 *    never lightens, never migrates (Stage 5 §7). After the span
 *    runs, the record stands complete and still (CF-1).
 *  - IT NEVER JUDGES. Formation is time's passage: the extent is
 *    identical on a kept day and a fallen one. The only differences
 *    a day can make here are its own event marks, inscribed at the
 *    offsets where the day made them. There is no field in this
 *    file that could read as "behind", a grade, or a verdict
 *    (CRITIC-SR-1 W-3b / U-6).
 *  - IN-SESSION ONLY. The record exists only inside a standing
 *    seal: with no span there is no record (the factory refuses a
 *    non-positive span), and the host surface lives behind the
 *    seal, never on the door (ledger A-9 — the regulator is in the
 *    room, never on the sealed face).
 */
object DeskClockRecord {

    /**
     * The drawing grain: the rim is formed in this many units, one
     * at a time, in reading order around the dial. An internal
     * constant of the drawing — it is not a number of minutes, not
     * a score denominator, and it is never displayed or labelled
     * anywhere (FF-1). 48 is chosen so a grain is coarse enough to
     * accrete at PASSIVE moments only (a 12-hour span forms one
     * grain per quarter hour) — the clock never visibly performs
     * while watched.
     */
    const val GRAINS: Int = 48

    /** A day's event kinds, mirroring P1's graded punches by name:
     *  a reach for a locked app, a held call, a floor event. The
     *  grades are P1's ruled design figures (Audit A §2.6): reach
     *  large · held medium · floor small — sizes a finger could
     *  learn. Rendering the grade is this lane's drawing; the grade
     *  itself is the day's truth, carried, never re-priced. */
    enum class MarkKind(val size: MarkSize) {
        REACH(MarkSize.LARGE),
        HELD(MarkSize.MEDIUM),
        FLOOR(MarkSize.SMALL)
    }

    enum class MarkSize { LARGE, MEDIUM, SMALL }

    /** One event of the day, at its elapsed offset into the span. */
    data class Mark(
        val kind: MarkKind,
        val atOffsetMillis: Long
    )

    /**
     * A shaped gap of the day (meditation · gym · sadhana · a meal)
     * as the record inscribes it: a span inside the seal, in elapsed
     * offsets — the same shape P1's `ShapedGap` carries, minus the
     * kind name, which the record does not need: on the rim a gap is
     * a lived shape of the day, drawn warm, whatever its name.
     */
    data class GapSpan(
        val startOffsetMillis: Long,
        val durationMillis: Long
    ) {
        val endOffsetMillis: Long get() = startOffsetMillis + durationMillis
    }

    /**
     * The record at one reading. [formedGrains] is the prefix's
     * length, in grains: everything the surface draws lives at
     * rim positions `< formedGrains`. [marks] holds only the marks
     * already inscribed (their moment has passed), in time order.
     * [gaps] holds the day's gaps CLIPPED to the formed extent —
     * a gap the day has not reached yet is not drawn at all, and a
     * gap being lived is drawn only for its lived part.
     */
    data class FormingRecord(
        val formedGrains: Int,
        val isComplete: Boolean,
        val marks: List<Mark>,
        val gaps: List<GapSpan>
    ) {
        /** The formed fraction of the rim, 0..1 — extent, the only
         *  quantity the record has. For the drawing's use; it is
         *  never formatted, labelled, or shown as a figure. */
        val formedFraction: Float get() = formedGrains.toFloat() / GRAINS.toFloat()
    }

    /**
     * The record for a reading [elapsedMillis] into a span of
     * [spanDurationMillis]. Elapsed is floored at 0 and capped at
     * the span (a reading past the lifting stands complete, still).
     */
    fun record(
        spanDurationMillis: Long,
        elapsedMillis: Long,
        marks: List<Mark>,
        gaps: List<GapSpan>
    ): FormingRecord {
        require(spanDurationMillis > 0L) { "the record exists only inside a standing seal: a positive span" }
        val elapsed = elapsedMillis.coerceIn(0L, spanDurationMillis)
        val formed = ((elapsed * GRAINS) / spanDurationMillis).toInt().coerceIn(0, GRAINS)
        return FormingRecord(
            formedGrains = formed,
            isComplete = elapsed >= spanDurationMillis,
            marks = marks
                .filter { it.atOffsetMillis in 0..elapsed }
                .sortedBy { it.atOffsetMillis },
            gaps = gaps.mapNotNull { gap ->
                val start = gap.startOffsetMillis.coerceAtLeast(0L)
                val livedEnd = minOf(gap.endOffsetMillis, elapsed)
                if (livedEnd <= start) null
                else GapSpan(startOffsetMillis = start, durationMillis = livedEnd - start)
            }.sortedBy { it.startOffsetMillis }
        )
    }

    /** The rim fraction (0..1) at which an event at [atOffsetMillis]
     *  is inscribed — its position in the day, as a place on the rim.
     *  A position, never a figure shown. */
    fun fractionAt(spanDurationMillis: Long, atOffsetMillis: Long): Float {
        require(spanDurationMillis > 0L) { "a position needs a standing span" }
        return (atOffsetMillis.coerceIn(0L, spanDurationMillis).toFloat() / spanDurationMillis.toFloat())
    }

    // ------------------------------------------------------------------
    // The regulator's hands — CRITIC-SR-1 charge C-2, answered here
    // ------------------------------------------------------------------

    /**
     * Where the hands stand for a time of day, as angles in degrees
     * clockwise from twelve. Hands are POSITIONS, not figures: the
     * dial they move on carries tick marks and no numerals, so the
     * time of day is read at a glance — one touch, nothing opened,
     * nothing spent (W-3a) — while the no-numbers law stands whole:
     * no digit exists in this file or on the surface it feeds.
     */
    data class HandPositions(
        val hourAngleDegrees: Float,
        val minuteAngleDegrees: Float
    )

    fun hands(hourOfDay: Int, minute: Int): HandPositions {
        require(hourOfDay in 0..23 && minute in 0..59) { "a time of day" }
        val minuteAngle = minute * 6f
        val hourAngle = ((hourOfDay % 12) + minute / 60f) * 30f
        return HandPositions(hourAngleDegrees = hourAngle, minuteAngleDegrees = minuteAngle)
    }
}
