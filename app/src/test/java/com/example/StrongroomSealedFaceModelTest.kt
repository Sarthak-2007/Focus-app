package com.example

import com.example.ui.strongroom.FacePrinting
import com.example.ui.strongroom.SealedFaceState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * THE SEALED FACE'S MODEL contract (ui/strongroom/SealedFaceModel.kt):
 *
 *  - The hierarchy is Stage 5 §3.3's map, in its ranks: the hour
 *    first (ENG-1), the door-word slot second (PRT-3), his
 *    sentence third (PRT-4), the held-mark slot last (PRT-2) —
 *    four printings, no fifth.
 *  - A face can only be struck with the lawful HH:MM hour.
 *  - The sentence's kept place: null and blank both stand the
 *    kept place; a real sentence stands itself.
 *  - The model carries NO field for the door-word or the held
 *    mark (G-1/G-2: no word exists, no glyph is invented) — the
 *    data class's whole surface is the hour and the sentence.
 */
class StrongroomSealedFaceModelTest {

    @Test
    fun hierarchy_isTheFiledMap_inRankOrder() {
        assertEquals(
            listOf(
                FacePrinting.LIFTING_HOUR,
                FacePrinting.DOOR_WORD_SLOT,
                FacePrinting.SENTENCE,
                FacePrinting.HELD_MARK_SLOT
            ),
            FacePrinting.HIERARCHY
        )
        assertEquals(listOf(1, 2, 3, 4), FacePrinting.HIERARCHY.map { it.rank })
        assertEquals(
            listOf("ENG-1", "PRT-3", "PRT-4", "PRT-2"),
            FacePrinting.HIERARCHY.map { it.stageStep }
        )
    }

    @Test
    fun face_requiresTheLawfulHour() {
        SealedFaceState(liftingHour = "06:30", sentence = null)
        try {
            SealedFaceState(liftingHour = "25:00", sentence = null)
            fail("an unlawful hour cannot be engraved on a face")
        } catch (expected: IllegalArgumentException) {
            // refused at the model's door
        }
    }

    @Test
    fun sentence_keptPlaceStandsForNullAndBlank() {
        assertFalse(SealedFaceState("06:30", null).hasSentence)
        assertFalse(SealedFaceState("06:30", "   ").hasSentence)
        assertTrue(SealedFaceState("06:30", "What I settle while I am strong.").hasSentence)
    }

    @Test
    fun model_carriesNoDoorWordAndNoHeldMarkField() {
        // G-1/G-2 as a type-level fact: the data class's components
        // are exactly the hour and the sentence — a slot that could
        // hold a value would invite one to be invented.
        val components = SealedFaceState::class.java.declaredFields
            .filter {
                !it.isSynthetic &&
                    !java.lang.reflect.Modifier.isStatic(it.modifiers) &&
                    !it.name.startsWith("$")
            }
            .map { it.name }
            .sorted()
        assertEquals(listOf("hasSentence", "liftingHour", "sentence"), components)
    }
}
