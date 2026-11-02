package com.example

import com.example.ui.strongroom.layout.OpenDoorProportion
import com.example.ui.strongroom.layout.RoomGrid
import com.example.ui.strongroom.layout.RoomZone
import com.example.ui.strongroom.layout.SigningOnlyLaw
import com.example.ui.strongroom.layout.SigningSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * SLICE 2 — LAYOUT. The signing place at slot depth, as executable law
 * (adopted from DESIGNER-SR-2's seam contribution, BUILD-SR-7 adoption):
 * C-4's zero insertions, B-4's conditional telling, B-5/B-19's
 * signing-only texts, B-17's open-door proportions.
 */
class SigningPlaceLayoutLawTest {

    @Test
    fun signingOrderIsTheRuledEight() {
        assertEquals(
            listOf(
                SigningSlot.HEAD_TOMORROW_AS_RESOLVED,
                SigningSlot.COMPOSITION_AS_RECEIVED,
                SigningSlot.SPENT_DAY_TELLING,
                SigningSlot.CLAUSE_AS_TERM,
                SigningSlot.BREAK_CHOICE,
                SigningSlot.FOOT_RULE,
                SigningSlot.COMBINATION_SPIN,
                SigningSlot.DOOR_TELLING,
            ),
            SigningSlot.ruledOrder,
        )
        assertEquals((1..8).toList(), SigningSlot.ruledOrder.map { it.rank })
        assertTrue(SigningSlot.isLawfulOrder(SigningSlot.ruledOrder))
        assertFalse(
            SigningSlot.isLawfulOrder(
                listOf(SigningSlot.COMBINATION_SPIN, SigningSlot.CLAUSE_AS_TERM),
            ),
        )
    }

    // B-4: the spent-day telling stands only on a spent day; every other
    // slot stands unconditionally.
    @Test
    fun spentDayTellingIsConditional() {
        assertTrue(SigningSlot.stands(SigningSlot.SPENT_DAY_TELLING, spentDay = true))
        assertFalse(SigningSlot.stands(SigningSlot.SPENT_DAY_TELLING, spentDay = false))
        SigningSlot.entries.filter { it != SigningSlot.SPENT_DAY_TELLING }.forEach {
            assertTrue(SigningSlot.stands(it, spentDay = false))
        }
    }

    // B-5/B-19: clause and tellings live at the signing place only —
    // never on the face, never on the tray.
    @Test
    fun signingOnlyTextsTravelNowhere() {
        SigningOnlyLaw.SigningOnlyText.entries.forEach { text ->
            assertEquals(RoomZone.SIGNING_PLACE, SigningOnlyLaw.homeZone(text))
            assertTrue(SigningOnlyLaw.mayStandIn(text, RoomZone.SIGNING_PLACE))
            assertFalse(SigningOnlyLaw.mayStandIn(text, RoomZone.FACE_FIELD))
            assertFalse(SigningOnlyLaw.mayStandIn(text, RoomZone.TRAY_SIDE))
        }
    }

    // B-17: composition is three of five fifths; the plain line is one
    // line; the striking's separation is the second-step band, on grid.
    @Test
    fun openDoorProportionsAreB17() {
        assertEquals(3, OpenDoorProportion.COMPOSITION_FIFTHS)
        assertEquals(5, OpenDoorProportion.FIELD_FIFTHS)
        assertEquals(1, OpenDoorProportion.PLAIN_LINE_LINES)
        assertEquals(
            RoomGrid.SECOND_STEP_BAND_STEPS,
            OpenDoorProportion.STRIKING_SEPARATION_STEPS,
        )
        assertTrue(RoomGrid.isOnGrid(RoomGrid.dp(OpenDoorProportion.STRIKING_SEPARATION_STEPS)))
    }

    // The adopted primitive exists under the one name surfaces bind to.
    @Test
    fun signingPrimitiveExistsAtSlotDepth() {
        val cls = Class.forName("com.example.ui.strongroom.layout.SigningPlaceLayoutKt")
        val method = cls.declaredMethods.single { it.name == "SigningPlaceLayout" }
        // 8 slots + Modifier + Composer + flags: bounded, and more than
        // the old two-slot form — slot depth is visible in the shape.
        assertTrue(method.parameterCount >= 11)
    }
}
