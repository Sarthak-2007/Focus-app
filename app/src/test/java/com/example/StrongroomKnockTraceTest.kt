package com.example

import com.example.service.StrongroomKnock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Modifier

/**
 * A-5 + CRITIC-SR-3 K-2 — THE KNOCK LEAVES NO TRACE BUT THE
 * PLATE'S HOLE, asserted on the knock's own spec.
 * Landing: faces tree, with P3 (service/StrongroomKnock.kt).
 * The plate half of this law is DayPlateTraceTest (engine tree).
 *
 * The knock is the sealed face's only event: one dull heavy
 * thud, and NOTHING in the room changes — no count kept, no
 * escalation, no softening, no record on the face. For that to
 * be a law and not a hope, the knock's spec must be incapable
 * of remembering: this test asserts, by census, that it is.
 *
 *  1. STATELESSNESS — the spec object holds constants and
 *     nothing else: every field is final, the only non-static
 *     field is the object's own INSTANCE, and no field's name
 *     can hold a count, a level, or a history. Knock #9 cannot
 *     differ from knock #1 because there is nowhere for the
 *     first eight to have been written.
 *  2. IDENTITY ACROSS REACHES — the full spec snapshot, taken
 *     nine times as the engine would read it at nine reaches,
 *     is byte-identical every time.
 *
 * Watch item (named in this desk's map as GAP-T6, not asserted
 * here): the haptic arrays are final references to mutable
 * arrays — a holder could rewrite a segment in place. The
 * census in (1) pins today's values by name so any such change
 * fails loudly until BUILD-SR-2 hardens the shape.
 */
class StrongroomKnockTraceTest {

    /** The whole spec as one snapshot string — the knock's
     *  entire memory of itself. */
    private fun snapshot(): String = listOf(
        "pitch=${StrongroomKnock.PITCH_HZ}",
        "duration=${StrongroomKnock.DURATION_MS}",
        "gain=${StrongroomKnock.GAIN}",
        "chord=${StrongroomKnock.CHORD_RATIO}",
        "timings=${StrongroomKnock.HAPTIC_TIMINGS_MS.toList()}",
        "amps=${StrongroomKnock.HAPTIC_AMPLITUDES.toList()}",
        "fallback=${StrongroomKnock.HAPTIC_FALLBACK_MS}",
        "thud=${StrongroomKnock.THUD_PRIMITIVE_SCALE}"
    ).joinToString("|")

    @Test
    fun knockSpec_isIdenticalAtTheNinthReachAsTheFirst() {
        val first = snapshot()
        repeat(8) { reach ->
            assertEquals(
                "reach ${reach + 2} must knock exactly as reach 1 knocked " +
                    "(no escalation, no softening, no count kept in the room)",
                first, snapshot()
            )
        }
    }

    @Test
    fun knockSpec_holdsNoMutableState_andNoCounter() {
        val fields = StrongroomKnock::class.java.declaredFields
            .filter { !it.isSynthetic && !it.name.startsWith("$") }
        // The census, by name: the spec is these constants and
        // the object's INSTANCE — a new field is a place for the
        // knock to remember something, and fails here by name.
        assertEquals(
            listOf(
                "CHORD_RATIO", "DURATION_MS", "GAIN",
                "HAPTIC_AMPLITUDES", "HAPTIC_FALLBACK_MS", "HAPTIC_TIMINGS_MS",
                "INSTANCE", "PITCH_HZ", "THUD_PRIMITIVE_SCALE"
            ),
            fields.map { it.name }.sorted()
        )
        fields.forEach { field ->
            assertTrue(
                "field '${field.name}' must be final — a var in the knock " +
                    "is a memory in the room",
                Modifier.isFinal(field.modifiers)
            )
        }
        val forbidden = listOf(
            "count", "total", "level", "history", "last", "streak", "score"
        )
        fields.forEach { field ->
            forbidden.forEach { token ->
                assertFalse(
                    "the knock keeps no '$token' ('${field.name}') — its only " +
                        "trace is the plate's hole",
                    field.name.contains(token, ignoreCase = true)
                )
            }
        }
        // And no method on the spec mutates it: the object's
        // declared methods are its accessors and nothing else.
        val mutators = StrongroomKnock::class.java.declaredMethods
            .filter { m ->
                !m.isSynthetic &&
                    !m.name.startsWith("get") &&
                    m.name != "toString" && m.name != "hashCode" && m.name != "equals"
            }
        assertEquals(
            "the knock spec exposes no mutating method",
            emptyList<String>(), mutators.map { it.name }
        )
    }
}
