package com.example.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * P1 — THE COMPOSITION (BUILD-SR-3; ledger B-2, refit Floor 1
 * Beat 1 — governing over Audit A's compression).
 *
 * Tomorrow stands COMPOSED from the WEEK'S STANDING ORDER whether
 * or not he comes to the door. The standing order is a week's
 * order, composed once in a calm hour, with its day-shapes inside
 * it — ordinary · test · small · rest. Each night (in model terms:
 * each filing date) prints tomorrow's page from the order.
 *
 * The signing AMENDS the composition; it never creates the day.
 * The app NEVER checks whether the evening happened: nothing in
 * this file or the store gates a seal on a signing existing — an
 * unsigned morning still has its Composition standing in the room,
 * and the swing seals it as it stands (refit Beat 2).
 *
 * The task line (B-3) rides on the composition: the reason for a
 * block, in his own words, attached to the block's sealing, spent
 * when that sealing lifts; his words exempt from any word-bench.
 */
object StrongroomComposition {

    /** The week's day-shapes, as named in the standing order. */
    enum class DayShape { ORDINARY, TEST, SMALL, REST }

    /**
     * One day-shape's standing template: what a day of this shape
     * holds before any amendment. Composed once, in a calm hour —
     * a shape composed once is not a setting struck every day.
     */
    data class DayTemplate(
        val lockedPackages: Set<String>,
        val allowedPackages: List<String> = emptyList(),
        val liftingTime: LocalTime,
        val breakLengthMinutes: Int,
        val breakFiresAtOffsetMillis: Long,
        val allowanceMinutes: Int,
        val gaps: List<StrongroomDay.ShapedGap>,
        val taskLine: String? = null
    )

    /**
     * The week's standing order: a shape for each weekday and a
     * template for each shape. Every weekday must carry a shape
     * and every used shape a template — an incomplete order cannot
     * compose, and the failure is at the order's composing, in the
     * calm hour, never discovered on a morning.
     */
    data class WeekStandingOrder(
        val shapeByWeekday: Map<DayOfWeek, DayShape>,
        val templateByShape: Map<DayShape, DayTemplate>
    ) {
        init {
            require(shapeByWeekday.keys.containsAll(DayOfWeek.values().toSet())) {
                "the standing order names a shape for every weekday"
            }
            require(templateByShape.keys.containsAll(shapeByWeekday.values.toSet())) {
                "every shape the week uses has a template in the order"
            }
        }

        fun shapeOn(date: LocalDate): DayShape = shapeByWeekday.getValue(date.dayOfWeek)
    }

    /**
     * A day standing composed: the effective record the swing will
     * seal, its shape, and whether an evening signing amended it.
     * [amendedBySigning] is a fact for the record — it gates
     * NOTHING (the app never checks whether the evening happened).
     */
    data class ComposedDay(
        val filingDateIso: String,
        val shape: DayShape,
        val record: StrongroomDay.SigningRecord,
        val amendedBySigning: Boolean
    )

    /** Compose the day for [filingDateIso] from the standing order.
     *  This stands whether or not he ever comes to the door. */
    fun compose(order: WeekStandingOrder, filingDateIso: String): ComposedDay {
        val date = LocalDate.parse(filingDateIso)
        val shape = order.shapeOn(date)
        val template = order.templateByShape.getValue(shape)
        return ComposedDay(
            filingDateIso = filingDateIso,
            shape = shape,
            record = StrongroomDay.SigningRecord(
                filingDateIso = filingDateIso,
                lockedPackages = template.lockedPackages,
                allowedPackages = template.allowedPackages,
                liftingTime = template.liftingTime,
                sentence = null, // the composition carries the kept place; a sentence is the signing's word
                breakLengthMinutes = template.breakLengthMinutes,
                breakFiresAtOffsetMillis = template.breakFiresAtOffsetMillis,
                allowanceMinutes = template.allowanceMinutes,
                gaps = template.gaps,
                taskLine = template.taskLine
            ),
            amendedBySigning = false
        )
    }

    /**
     * An evening signing's amendment: every field optional, and
     * ONLY the named fields change — an amendment names what it
     * moves and leaves the composition's rest standing exactly as
     * composed. It never creates the day: it applies to a
     * composition that already stands.
     */
    data class SigningAmendment(
        val lockedPackages: Set<String>? = null,
        val allowedPackages: List<String>? = null,
        val liftingTime: LocalTime? = null,
        val sentence: String? = null,
        val breakLengthMinutes: Int? = null,
        val breakFiresAtOffsetMillis: Long? = null,
        val allowanceMinutes: Int? = null,
        val gaps: List<StrongroomDay.ShapedGap>? = null,
        val taskLine: String? = null
    )

    fun amend(day: ComposedDay, amendment: SigningAmendment): ComposedDay {
        val r = day.record
        return day.copy(
            record = r.copy(
                lockedPackages = amendment.lockedPackages ?: r.lockedPackages,
                allowedPackages = amendment.allowedPackages ?: r.allowedPackages,
                liftingTime = amendment.liftingTime ?: r.liftingTime,
                sentence = amendment.sentence ?: r.sentence,
                breakLengthMinutes = amendment.breakLengthMinutes ?: r.breakLengthMinutes,
                breakFiresAtOffsetMillis = amendment.breakFiresAtOffsetMillis
                    ?: r.breakFiresAtOffsetMillis,
                allowanceMinutes = amendment.allowanceMinutes ?: r.allowanceMinutes,
                gaps = amendment.gaps ?: r.gaps,
                taskLine = amendment.taskLine ?: r.taskLine
            ),
            amendedBySigning = true
        )
    }
}
