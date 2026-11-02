package com.example

import com.example.service.StrongroomKnock
import com.example.ui.strongroom.SealedFaceState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Modifier
import java.security.MessageDigest

/**
 * A-4 — THE FACE IS ENGRAVED ONCE AND NEVER ALTERED WHILE THE
 * SEAL STANDS, asserted as a state hash across the block.
 * Landing: faces tree, with P3 (ui/strongroom/SealedFaceModel.kt).
 *
 * The lane's StrongroomSealedFaceModelTest proves the model's
 * shape. This test proves the immutability the shape exists
 * for, in the strongest form a JVM test can state it:
 *
 *  1. THE HASH ACROSS THE BLOCK — a face struck at sealing
 *     carries one digest; the block's own events (the knock
 *     firing again and again, the face read at every hour) are
 *     then simulated around it, and the digest is re-taken
 *     after each: it never moves. A face that could change
 *     under the block would need a field to change in, or a
 *     mutator to change it through —
 *  2. NO MUTATOR EXISTS — every field is final, no setter-shaped
 *     method exists, and the field census carries no live-state
 *     field (no clock, no count, no purse, no held tally): the
 *     face cannot learn about the block it stands in.
 *  3. CHANGE IS A NEW ENGRAVING, NEVER AN ALTERATION — the only
 *     road to a different face is a new instance (copy); the
 *     struck instance's digest survives the copy untouched.
 *
 * The render-level half of this law (the composed face on
 * screen) rides BUILD-SR-2's GAP-1 cure in VERIFY-SR-6's map;
 * this model hash is the seam that proof plugs into.
 */
class SealedFaceModelImmutabilityTest {

    /** The face's canonical digest: the engraved facts and
     *  nothing else — the hour, and the sentence or its kept
     *  place's mark. */
    private fun digestOf(face: SealedFaceState): String {
        // The engraved sentence is the carried one: a blank
        // sentence stands the kept place, exactly as null does
        // (the model's own hasSentence law) — same face, same hash.
        val carried = if (face.hasSentence) face.sentence else null
        val canonical = "hour=${face.liftingHour}|sentence=${carried ?: "<kept-place>"}"
        return MessageDigest.getInstance("SHA-256")
            .digest(canonical.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    @Test
    fun hash_isConstantAcrossTheWholeBlock() {
        val struck = SealedFaceState(liftingHour = "06:30", sentence = "Hold the morning.")
        val atSealing = digestOf(struck)
        val hashAtSealing = struck.hashCode()

        // The block happens around the face: reaches knock on it
        // (the knock's spec is read, as the engine reads it, at
        // every reach), the face is read back at every pass.
        repeat(64) { reach ->
            // A reach fires the knock voice — the face's only event.
            val voice = StrongroomKnock.PITCH_HZ + StrongroomKnock.GAIN
            assertTrue(voice > 0f)
            // After every reach the struck face is the same face:
            // same instance facts, same digest, same hash.
            assertEquals("reach $reach altered the face's digest", atSealing, digestOf(struck))
            assertEquals("reach $reach altered the face's hash", hashAtSealing, struck.hashCode())
            assertEquals("06:30", struck.liftingHour)
            assertEquals("Hold the morning.", struck.sentence)
            assertTrue(struck.hasSentence)
        }
    }

    @Test
    fun hash_twoFacesStruckAlike_areTheSameFace_andUnlikeFacesDiffer() {
        val a = SealedFaceState("06:30", null)
        val b = SealedFaceState("06:30", null)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(digestOf(a), digestOf(b))
        // The kept place and a sentence are different engravings.
        assertTrue(digestOf(a) != digestOf(SealedFaceState("06:30", "words")))
        // A different hour is a different engraving.
        assertTrue(digestOf(a) != digestOf(SealedFaceState("07:00", null)))
        // A blank sentence stands the kept place — same engraving,
        // same digest (though the raw values differ, the face the
        // room sees is one).
        assertEquals(digestOf(a), digestOf(SealedFaceState("06:30", "   ")))
    }

    @Test
    fun noMutatorExists_everyFieldFinal_noSetter_noLiveStateField() {
        val fields = SealedFaceState::class.java.declaredFields
            .filter {
                !it.isSynthetic &&
                    !Modifier.isStatic(it.modifiers) &&
                    !it.name.startsWith("$")
            }
        assertEquals(
            listOf("hasSentence", "liftingHour", "sentence"),
            fields.map { it.name }.sorted()
        )
        fields.forEach { field ->
            assertTrue(
                "field '${field.name}' must be final — a var on the face is a door in it",
                Modifier.isFinal(field.modifiers)
            )
        }
        val setters = SealedFaceState::class.java.declaredMethods
            .filter { !it.isSynthetic && it.name.startsWith("set") }
        assertEquals(
            "the face exposes no setter of any kind",
            emptyList<String>(), setters.map { it.name }
        )
        // No live-state field: the face cannot learn the block's
        // news (held calls, purse, progress, the hour now).
        val forbidden = listOf(
            "time", "clock", "count", "held", "purse",
            "progress", "battery", "now", "remaining"
        )
        fields.forEach { field ->
            forbidden.forEach { token ->
                assertFalse(
                    "the face carries no live field ('${field.name}' ~ '$token')",
                    field.name.contains(token, ignoreCase = true)
                )
            }
        }
    }

    @Test
    fun change_isANewEngraving_theStruckFaceNeverChanges() {
        val struck = SealedFaceState("06:30", "Hold the morning.")
        val before = digestOf(struck)
        val reStruck = struck.copy(liftingHour = "07:00")
        assertNotSame(struck, reStruck)
        assertEquals("the struck face is untouched by the copy", before, digestOf(struck))
        assertEquals("06:30", struck.liftingHour)
        assertTrue(digestOf(reStruck) != before)
    }
}
