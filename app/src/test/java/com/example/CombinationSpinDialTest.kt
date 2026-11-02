package com.example

import com.example.ui.maximum.orbitAngleDeg
import com.example.ui.maximum.orbitDeltaDeg
import org.junit.Assert.assertEquals
import org.junit.Test

/** BUILD-SR-11 — the spin surface's orbit maths: angle around the centre, smallest signed turn. */
class CombinationSpinDialTest {

    @Test
    fun `orbit angle reads the four quarters around the centre`() {
        assertEquals(0f, orbitAngleDeg(100f, 100f, 200f, 100f), 0.001f)
        assertEquals(90f, orbitAngleDeg(100f, 100f, 100f, 200f), 0.001f)
        assertEquals(180f, orbitAngleDeg(100f, 100f, 0f, 100f), 0.001f)
        assertEquals(270f, orbitAngleDeg(100f, 100f, 100f, 0f), 0.001f)
    }

    @Test
    fun `orbit delta takes the smallest signed turn across the wrap`() {
        assertEquals(20f, orbitDeltaDeg(350f, 10f), 0.001f)
        assertEquals(-20f, orbitDeltaDeg(10f, 350f), 0.001f)
        assertEquals(45f, orbitDeltaDeg(0f, 45f), 0.001f)
        assertEquals(-45f, orbitDeltaDeg(45f, 0f), 0.001f)
        assertEquals(180f, orbitDeltaDeg(0f, 180f), 0.001f)
    }
}
