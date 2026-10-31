package com.example

import com.example.ui.components.parkAgeWord
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Wave-2 (issue #9) DECLARED tests — written, NOT run (agents never
 * compile; Ren compiles on Sarthak's word).
 *
 * Pins the §4.4 age column's print derivation: an age is a fact
 * about the measurement, never about the grant — NOW inside five
 * seconds, seconds to a minute, minutes beyond, with the stale
 * threshold (45s) reading as seconds, not a rounded minute.
 */
class Wave2ParkPrintTest {

    @Test
    fun ageWord_boundaries() {
        assertEquals("NOW", parkAgeWord(0L))
        assertEquals("NOW", parkAgeWord(4_999L))
        assertEquals("5S AGO", parkAgeWord(5_000L))
        assertEquals("45S AGO", parkAgeWord(45_000L))
        assertEquals("59S AGO", parkAgeWord(59_999L))
        assertEquals("1M AGO", parkAgeWord(60_000L))
        assertEquals("12M AGO", parkAgeWord(12 * 60_000L))
    }
}
