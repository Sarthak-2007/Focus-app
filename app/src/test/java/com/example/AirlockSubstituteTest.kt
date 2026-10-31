package com.example

import com.example.ui.components.AIRLOCK_SUBSTITUTES
import com.example.ui.components.airlockSubstituteFor
import com.example.ui.components.airlockSubstituteLine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #324 (Audit R5, Zario head-to-head): the Airlock pause
 * carries a rotating one-minute substitute. Pure rotation over a
 * fixed local pack — deterministic per intercept instance,
 * consecutive instances never repeat, any id folds in safely.
 */
class AirlockSubstituteTest {

    @Test
    fun packIsNonTrivialAndAllDistinct() {
        assertTrue(AIRLOCK_SUBSTITUTES.size >= 8)
        assertEquals(AIRLOCK_SUBSTITUTES.size, AIRLOCK_SUBSTITUTES.distinct().size)
    }

    @Test
    fun rotationIsDeterministicPerInstance() {
        assertEquals(airlockSubstituteFor(7L), airlockSubstituteFor(7L))
        assertEquals(AIRLOCK_SUBSTITUTES[0], airlockSubstituteFor(0L))
    }

    @Test
    fun consecutiveInterceptsNeverRepeat() {
        for (id in 0L..64L) {
            assertTrue(airlockSubstituteFor(id) != airlockSubstituteFor(id + 1))
        }
    }

    @Test
    fun rotationWrapsAndFoldsNegativeIds() {
        assertEquals(
            AIRLOCK_SUBSTITUTES[0],
            airlockSubstituteFor(AIRLOCK_SUBSTITUTES.size.toLong())
        )
        assertTrue(AIRLOCK_SUBSTITUTES.contains(airlockSubstituteFor(-1L)))
        assertTrue(AIRLOCK_SUBSTITUTES.contains(airlockSubstituteFor(Long.MIN_VALUE)))
    }

    @Test
    fun lineCarriesTheSubstitute() {
        val line = airlockSubstituteLine(3L)
        assertTrue(line.startsWith("INSTEAD, ONE MINUTE: "))
        assertTrue(line.contains(airlockSubstituteFor(3L)))
    }
}
