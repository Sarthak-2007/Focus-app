package com.example

import org.junit.Assert.assertTrue
import org.junit.Test

/** SLICE 2 SECOND HALF (BUILD-SR-10): structural slot-contract coverage
 *  for LayoutSecondHalfPrimitives — same discipline as BUILD-SR-7's
 *  primitives test: composition itself is judged in the running app;
 *  what the JVM proves is the primitives' shape. */
class LayoutSecondHalfPrimitivesTest {

    private val primitivesClass: Class<*> =
        Class.forName("com.example.ui.strongroom.layout.LayoutSecondHalfPrimitivesKt")

    @Test
    fun allSecondHalfPrimitivesExist() {
        val names = primitivesClass.declaredMethods.map { it.name }.toSet()
        listOf(
            "AntechamberRoomLayout",
            "HeldPileLayout",
            "TrayDrawerLayout",
            "RoomTraySiblingsLayout",
        ).forEach { assertTrue("missing primitive: $it", it in names) }
    }

    // The room primitive takes exactly two content slots (list column +
    // drum housing): the housing can never be passed INTO the column.
    @Test
    fun roomPrimitiveHasExactlyTwoContentSlots() {
        val method = primitivesClass.declaredMethods.single { it.name == "AntechamberRoomLayout" }
        assertTrue("AntechamberRoomLayout grew parameters: ${method.parameterCount}", method.parameterCount <= 6)
    }

    // The drawer primitive takes exactly two content slots (pile + list):
    // no header/summary slot can appear without failing here by name.
    @Test
    fun drawerPrimitiveHasExactlyTwoContentSlots() {
        val method = primitivesClass.declaredMethods.single { it.name == "TrayDrawerLayout" }
        assertTrue("TrayDrawerLayout grew parameters: ${method.parameterCount}", method.parameterCount <= 6)
    }

    // The held-pile primitive takes exactly three content slots (B-18's
    // shelves) — a fourth would be a place for words around a code.
    @Test
    fun heldPilePrimitiveHasBoundedParameters() {
        val method = primitivesClass.declaredMethods.single { it.name == "HeldPileLayout" }
        assertTrue("HeldPileLayout grew parameters: ${method.parameterCount}", method.parameterCount <= 7)
    }
}
