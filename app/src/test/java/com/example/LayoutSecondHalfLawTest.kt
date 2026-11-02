package com.example

import com.example.ui.strongroom.layout.AntechamberZone
import com.example.ui.strongroom.layout.DrawerComposition
import com.example.ui.strongroom.layout.DrumHousingLaw
import com.example.ui.strongroom.layout.HeldPileShelf
import com.example.ui.strongroom.layout.RoomZone
import com.example.ui.strongroom.layout.TrayDrawerCompositionLaw
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** SLICE 2 SECOND HALF (BUILD-SR-10): the room composition law. */
class LayoutSecondHalfLawTest {

    @Test
    fun antechamberZonesCloseOverTheField() {
        assertTrue(AntechamberZone.sharesCloseOverTheField())
        assertEquals(100, AntechamberZone.entries.sumOf { it.shareHundredths })
        assertEquals(
            listOf(
                AntechamberZone.HEAD_WALL, AntechamberZone.REGISTER,
                AntechamberZone.MACHINE_WALL, AntechamberZone.DEEP_END,
            ),
            AntechamberZone.ruledOrder,
        )
    }

    // R-C3: the drum is a mechanism, never a B-9 list row.
    @Test
    fun machineWallIsNeverAListZone() {
        assertFalse(AntechamberZone.isListZone(AntechamberZone.MACHINE_WALL))
        assertTrue(AntechamberZone.isListZone(AntechamberZone.REGISTER))
    }

    // Seam S-B ruled (b): sibling, never a slot.
    @Test
    fun drumHousingIsSiblingNeverSlot() {
        assertEquals(
            DrumHousingLaw.HousingPlace.SIBLING_OF_LIST_COLUMN,
            DrumHousingLaw.housingPlace(),
        )
        AntechamberZone.entries.forEach { zone ->
            assertFalse(DrumHousingLaw.mayStandInRoom(zone))
        }
    }

    // B-18: the pile chamber's shelves — waiting, digits alone, taking.
    @Test
    fun heldPileShelvesAreRuled() {
        assertEquals(
            listOf(HeldPileShelf.WAITING, HeldPileShelf.DIGITS_LINE, HeldPileShelf.TAKING),
            HeldPileShelf.ruledOrder,
        )
        assertTrue(HeldPileShelf.isLawfulOrder(HeldPileShelf.ruledOrder))
        assertFalse(
            HeldPileShelf.isLawfulOrder(
                listOf(HeldPileShelf.TAKING, HeldPileShelf.WAITING),
            ),
        )
        assertEquals(3, HeldPileShelf.entries.size)
    }

    // L2-c / D-8: the drawer is a pile and a list — two members, no more.
    @Test
    fun drawerCompositionIsPileAndListOnly() {
        assertEquals(DrawerComposition.COMPOSITION_COUNT, DrawerComposition.entries.size)
        assertEquals(
            listOf(DrawerComposition.PLATE_PILE, DrawerComposition.BROWSING_LIST),
            DrawerComposition.ruledOrder,
        )
    }

    // B-16 at composition depth + E-2's drawn-edge cap.
    @Test
    fun trayAndSigningAreSiblingsAndEdgesAreCapped() {
        assertTrue(
            TrayDrawerCompositionLaw.areSiblings(RoomZone.TRAY_SIDE, RoomZone.SIGNING_PLACE),
        )
        assertFalse(
            TrayDrawerCompositionLaw.areSiblings(RoomZone.SIGNING_PLACE, RoomZone.SIGNING_PLACE),
        )
        assertEquals(24, TrayDrawerCompositionLaw.drawnEdges(60))
        assertEquals(5, TrayDrawerCompositionLaw.drawnEdges(5))
    }
}
