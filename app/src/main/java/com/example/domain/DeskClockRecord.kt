package com.example.domain

/**
 * P8a — THE FORMING PLATE (BUILD-SR-6, build-first-usable; drawing
 * model by DESIGNER-SR-6, `work/designer-sr-6/forming-plate-design.md`
 * — the plate prefigured, §6 seam; this file is that seam's record
 * function).
 *
 * Pure Kotlin, no Android imports — the same law P1's [StrongroomDay]
 * stands under. Inputs are plain session facts (offsets/durations in
 * the seal's elapsed basis), never P1 types: at composition a thin
 * adapter maps `SealSpan`, its punch facts and its gap spans onto
 * the facts below.
 *
 * THE LAW THIS FILE CARRIES (Audit A §2.3e + §4 · ledger A-9/E-3 ·
 * Stage 5 §7 TI-12/FRM-1 · Ruling R-C2):
 *
 *  - NO NUMBERS, NO TIME-OF-DAY. The record carries no figure and
 *    no readout of any kind: no elapsed, no remaining, no hands, no
 *    dial (R-C2: time-of-day inside the seal is none, by decision —
 *    an earlier draft of this lane carried hands; the ruling and
 *    DESIGNER-SR-6's send-back removed them, and their absence here
 *    is a decision, named). Quantity is EXTENT and WEIGHT (FF-1).
 *  - THE RECORD FORMS AS A PREFIX (Stage 5 §7): the formed portion
 *    is always exactly the beginning of the completed field, in
 *    reading order; a punch is inscribed only once its moment has
 *    passed; unformed columns hold their full advance and read as
 *    not-yet (FL-1 / PF-C).
 *  - MONOTONIC: formed count is a pure function of elapsed inside
 *    the span, capped at the span — it never reverses, lightens,
 *    or migrates. At completion the model equals the static full
 *    field (CF-1).
 *  - IT NEVER JUDGES: formation is time's passage, identical on a
 *    good day and a bad one except for the event marks the day
 *    itself made (U-6). If the seal dies, formation simply stops —
 *    the frozen prefix is the host's halted tick (§8 of the spec),
 *    and this model adds nothing to it.
 *  - THE KEPT PLACES STAY EMPTY: the hours margin carries no figure
 *    here (figures are the strike's business, P6) and the seal
 *    place is EMPTY by constant — the ring-impression is P6's
 *    strike, never drawn by this surface (PF-A).
 *
 * FIGURE STAMP (KEEPPER-SR-5's naming, register N-9): the column
 * count below is a design figure from DESIGNER-SR-6's spec (§3/§6),
 * carried stamped **UNPROVEN** (Audit A G-4) until Slice 4 judges
 * the grain in the running app — it is an internal constant, never
 * displayed, never labelled, never recoverable from the surface by
 * any marking.
 */
object DeskClockRecord {

    /** The forming grain: columns across the field. DESIGNER-SR-6
     *  §3: at 116, counting is not forbidden, it is pointless — the
     *  eye reads fullness instead. UNPROVEN design figure (above). */
    const val COLUMNS: Int = 116

    /** A column's kind, by its centre (spec §6): GRAIN for study
     *  time, SMOOTH inside a shaped gap, SMOOTH_FIRING inside the
     *  break's firing span — the day's shape as texture, the firing
     *  distinct by its twin edge-lines at drawing time. */
    enum class ColumnKind { GRAIN, SMOOTH, SMOOTH_FIRING }

    /** The punch species (spec §5): form AND zone both — three
     *  lanes, three graded diameters in the plate's finger-learnable
     *  scale (E-9). Diameters live in the drawing constants
     *  (FormingPlateGeometry, the seam file P6 shares at
     *  composition): reach 11 · held 7.5 · showing 4.5 at the
     *  reference width. */
    enum class PunchKind(val lane: Lane, val diameterClass: DiameterClass) {
        REACH(Lane.UPPER, DiameterClass.LARGE),
        HELD(Lane.MIDDLE, DiameterClass.MEDIUM),
        SHOWING(Lane.LOWER, DiameterClass.SMALL)
    }

    enum class Lane { UPPER, MIDDLE, LOWER }

    enum class DiameterClass { LARGE, MEDIUM, SMALL }

    /** One event of the day, at its elapsed offset into the span. */
    data class PunchFact(
        val kind: PunchKind,
        val atOffsetMillis: Long
    )

    /**
     * A shaped gap of the day (meditation · gym · sadhana · a meal)
     * inside the seal, in elapsed offsets; [isBreakFiring] marks
     * the break's firing span instead of a gap — the same span
     * shape, a different species at drawing.
     */
    data class GapSpan(
        val startOffsetMillis: Long,
        val durationMillis: Long,
        val isBreakFiring: Boolean = false
    ) {
        val endOffsetMillis: Long get() = startOffsetMillis + durationMillis
    }

    /** A punch as the drawing consumes it: a POSITION across the
     *  field (0..1), its lane, its diameter class. Position is
     *  data; drawing is the screen's (§5 of the spec). */
    data class Punch(
        val kind: PunchKind,
        val xFraction: Float,
        val lane: Lane,
        val diameterClass: DiameterClass
    )

    /** The seal place in-session: EMPTY, by constant. */
    enum class SealPlace { EMPTY }

    /**
     * The forming plate at one reading. [formedColumns] is the
     * prefix's length; [columnKinds] names every column's kind
     * (length [COLUMNS]); [punches] holds only punches already
     * inscribed (their moment has passed), in time order.
     * [completed] is true exactly when the field stands whole.
     */
    data class FormingPlate(
        val formedColumns: Int,
        val columnKinds: List<ColumnKind>,
        val punches: List<Punch>,
        val sealPlace: SealPlace,
        val completed: Boolean
    )

    /**
     * The record for a reading [elapsedMillis] into a span of
     * [spanDurationMillis]. Elapsed is floored at 0 and capped at
     * the span. Column kinds are computed for the whole field (the
     * day's shape is signed at the signing); what is DRAWN formed
     * is only the prefix — kinds ahead of the frontier sit in the
     * ghost and are never rendered as content (PF-C).
     */
    fun record(
        spanDurationMillis: Long,
        elapsedMillis: Long,
        punches: List<PunchFact>,
        gaps: List<GapSpan>
    ): FormingPlate {
        require(spanDurationMillis > 0L) { "the record exists only inside a standing seal: a positive span" }
        val elapsed = elapsedMillis.coerceIn(0L, spanDurationMillis)
        val formed = ((elapsed * COLUMNS) / spanDurationMillis).toInt().coerceIn(0, COLUMNS)
        val kinds = (0 until COLUMNS).map { column ->
            val centreMillis = ((column + 0.5) * spanDurationMillis / COLUMNS).toLong()
            val span = gaps.firstOrNull { centreMillis >= it.startOffsetMillis && centreMillis < it.endOffsetMillis }
            when {
                span == null -> ColumnKind.GRAIN
                span.isBreakFiring -> ColumnKind.SMOOTH_FIRING
                else -> ColumnKind.SMOOTH
            }
        }
        return FormingPlate(
            formedColumns = formed,
            columnKinds = kinds,
            punches = punches
                .filter { it.atOffsetMillis in 0..elapsed }
                .sortedBy { it.atOffsetMillis }
                .map { fact ->
                    Punch(
                        kind = fact.kind,
                        xFraction = fact.atOffsetMillis.toFloat() / spanDurationMillis.toFloat(),
                        lane = fact.kind.lane,
                        diameterClass = fact.kind.diameterClass
                    )
                },
            sealPlace = SealPlace.EMPTY,
            completed = formed == COLUMNS
        )
    }

    /** The static full-field model for the same facts — CF-1's
     *  comparison object: the completed forming plate must equal
     *  this (the field whole, every punch in place). */
    fun completedField(
        spanDurationMillis: Long,
        punches: List<PunchFact>,
        gaps: List<GapSpan>
    ): FormingPlate = record(spanDurationMillis, spanDurationMillis, punches, gaps)
}
