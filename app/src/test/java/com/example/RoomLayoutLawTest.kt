package com.example

import com.example.ui.strongroom.layout.AntechamberSlot
import com.example.ui.strongroom.layout.DoorwayElement
import com.example.ui.strongroom.layout.DrawerContent
import com.example.ui.strongroom.layout.FacePrinting
import com.example.ui.strongroom.layout.RoomGrid
import com.example.ui.strongroom.layout.RoomZone
import com.example.ui.strongroom.layout.SigningMaterial
import com.example.ui.strongroom.layout.TrayContent
import com.example.ui.strongroom.layout.TraySeparationLaw
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * SLICE 2 — LAYOUT (BUILD-SR-7). The spatial law as executable tests:
 * if a later change mis-orders the antechamber, seats the tray at the
 * signing place, adds a fifth Doorway element, or gives the face a fifth
 * printing, these tests fail by name.
 */
class RoomLayoutLawTest {

    // B-9: the antechamber's list, in this order and no other.
    @Test
    fun antechamberOrderIsB9() {
        assertEquals(
            listOf(
                AntechamberSlot.CALLING_OUT,
                AntechamberSlot.ALLOWED_AS_SIGNED,
                AntechamberSlot.HELD_PILE_SECOND_STEP,
            ),
            AntechamberSlot.ruledOrder,
        )
        assertEquals(listOf(1, 2, 3), AntechamberSlot.ruledOrder.map { it.rank })
        assertTrue(AntechamberSlot.isLawfulOrder(AntechamberSlot.ruledOrder))
        assertFalse(
            AntechamberSlot.isLawfulOrder(
                listOf(AntechamberSlot.ALLOWED_AS_SIGNED, AntechamberSlot.CALLING_OUT),
            ),
        )
    }

    // B-9 limb 4: spent things are GONE — there is no slot for them at all.
    @Test
    fun spentThingsHaveNoAntechamberSlot() {
        assertEquals(3, AntechamberSlot.entries.size)
        assertTrue(AntechamberSlot.entries.none { it.name.contains("SPENT") })
    }

    // B-16: the plate stack never stands at the signing place.
    @Test
    fun trayNeverStandsAtTheSigningPlace() {
        TrayContent.entries.forEach { content ->
            assertFalse(TraySeparationLaw.mayStandAtSigningPlace(content))
            assertEquals(RoomZone.TRAY_SIDE, TraySeparationLaw.homeZone(content))
        }
        SigningMaterial.entries.forEach { material ->
            assertEquals(RoomZone.SIGNING_PLACE, TraySeparationLaw.homeZone(material))
        }
    }

    // B-17: the Doorway's count is four, in its ruled order.
    @Test
    fun doorwayCountIsFour() {
        assertEquals(DoorwayElement.DOORWAY_COUNT, DoorwayElement.entries.size)
        assertEquals(
            listOf(
                DoorwayElement.COMPOSITION_AT_OPEN_DOOR,
                DoorwayElement.ONE_PLAIN_LINE,
                DoorwayElement.HELD_WAITING,
                DoorwayElement.STRIKING,
            ),
            DoorwayElement.ruledOrder,
        )
    }

    // Stage 5 §3.3: the face's hierarchy — hour, door-word, sentence, held mark.
    @Test
    fun faceHierarchyIsStage5() {
        assertEquals(
            listOf(
                FacePrinting.LIFTING_HOUR,
                FacePrinting.DOOR_WORD_SLOT,
                FacePrinting.SENTENCE_OR_KEPT_PLACE,
                FacePrinting.HELD_MARK_SLOT,
            ),
            FacePrinting.ruledOrder,
        )
        assertEquals(
            listOf("ENG-1", "PRT-3", "PRT-4", "PRT-2"),
            FacePrinting.ruledOrder.map { it.stage5Step },
        )
        assertEquals(4, FacePrinting.entries.size)
    }

    // Stage 5 §2: every named spatial measure sits on the 4dp grid.
    @Test
    fun everyNamedMeasureIsOnTheGrid() {
        val measures = listOf(
            RoomGrid.dp(RoomGrid.FACE_BAND_STEPS),
            RoomGrid.dp(RoomGrid.LIST_BAND_STEPS),
            RoomGrid.dp(RoomGrid.SECOND_STEP_BAND_STEPS),
            RoomGrid.dp(RoomGrid.TRAY_RAIL_STEPS),
            RoomGrid.dp(RoomGrid.DRAWER_CELL_MIN_STEPS),
            RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS),
        )
        measures.forEach { assertTrue(RoomGrid.isOnGrid(it)) }
        assertEquals(4, RoomGrid.BASE_DP)
        assertFalse(RoomGrid.isOnGrid(6))
    }

    // D-8: the drawer stores and browses — it has no scoring content kind.
    @Test
    fun drawerHasNoScoringContent() {
        assertEquals(
            setOf(DrawerContent.FINISHED_TASKS, DrawerContent.PLATES),
            DrawerContent.entries.toSet(),
        )
    }

    // The second step is a step: it separates more than a list band does.
    @Test
    fun secondStepSeparatesMoreThanAListBand() {
        assertTrue(RoomGrid.SECOND_STEP_BAND_STEPS > RoomGrid.LIST_BAND_STEPS)
    }
}
