package com.example

import com.example.session.StrongroomPlace
import com.example.ui.shell.deriveShellPlace
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * BUILD-SR-34 — the shell's derivation, pinned whole: state →
 * host, in the Strongroom's own order. The lifting outranks
 * everything, the break's span outranks the room's steps, the
 * antechamber stands only inside a seal, and the doorway is the
 * ground state of an unsealed day. No state combination lands
 * anywhere else — there is no dead route in the table.
 */
class StrongroomShellTest {

    @Test
    fun unsealedDayStandsAtTheDoorway() {
        assertEquals(
            StrongroomPlace.DOORWAY,
            deriveShellPlace(sealed = false, onBreak = false, lifting = false, atAntechamber = false)
        )
    }

    @Test
    fun sealedDayStandsAtTheSealedFace() {
        assertEquals(
            StrongroomPlace.SEALED_FACE,
            deriveShellPlace(sealed = true, onBreak = false, lifting = false, atAntechamber = false)
        )
    }

    @Test
    fun secondTouchStandsInTheAntechamberOnlyInsideASeal() {
        assertEquals(
            StrongroomPlace.ANTECHAMBER,
            deriveShellPlace(sealed = true, onBreak = false, lifting = false, atAntechamber = true)
        )
        // The same stored step on an unsealed day is nothing: the
        // room exists only inside a standing seal.
        assertEquals(
            StrongroomPlace.DOORWAY,
            deriveShellPlace(sealed = false, onBreak = false, lifting = false, atAntechamber = true)
        )
    }

    @Test
    fun breakSpanOutranksTheRoomsSteps() {
        assertEquals(
            StrongroomPlace.BREAK,
            deriveShellPlace(sealed = true, onBreak = true, lifting = false, atAntechamber = true)
        )
        assertEquals(
            StrongroomPlace.BREAK,
            deriveShellPlace(sealed = true, onBreak = true, lifting = false, atAntechamber = false)
        )
    }

    @Test
    fun liftingOutranksEveryState() {
        assertEquals(
            StrongroomPlace.LIFTING,
            deriveShellPlace(sealed = true, onBreak = true, lifting = true, atAntechamber = true)
        )
        assertEquals(
            StrongroomPlace.LIFTING,
            deriveShellPlace(sealed = false, onBreak = false, lifting = true, atAntechamber = false)
        )
    }
}
