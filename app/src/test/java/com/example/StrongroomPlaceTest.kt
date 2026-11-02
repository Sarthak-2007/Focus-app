package com.example

import com.example.session.StrongroomPlace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BUILD-SR-34 — the enum-death migration, pinned (A-1 rider):
 * PreArmStore persists the standing place's NAME under the key
 * the dead MonolithDestination enum used. Every name an old build
 * could have written, and every unknown or absent name, resolves
 * to the signing place — never a crash, never a dead route, never
 * a conjured seal.
 */
class StrongroomPlaceTest {

    @Test
    fun everyDeadEnumNameResolvesToTheDoorway() {
        for (name in listOf("DECK", "TASKS", "SHIELD", "VAULT")) {
            assertEquals(
                "persisted $name must land on the signing place",
                StrongroomPlace.DOORWAY,
                StrongroomPlace.fromPersistedName(name)
            )
        }
    }

    @Test
    fun unknownAndAbsentNamesResolveToTheDoorway() {
        assertEquals(StrongroomPlace.DOORWAY, StrongroomPlace.fromPersistedName(null))
        assertEquals(StrongroomPlace.DOORWAY, StrongroomPlace.fromPersistedName(""))
        assertEquals(StrongroomPlace.DOORWAY, StrongroomPlace.fromPersistedName("GUARD"))
        assertEquals(StrongroomPlace.DOORWAY, StrongroomPlace.fromPersistedName("not-a-place"))
    }

    @Test
    fun sealedFamilyNamesAreNeverHonouredAsAStandingFact() {
        // A stored word must not conjure a seal that does not
        // exist: even the new vocabulary's sealed names seed only
        // the doorway; the live engine state decides the rest.
        for (name in listOf("SEALED_FACE", "ANTECHAMBER", "BREAK", "LIFTING")) {
            assertEquals(
                StrongroomPlace.DOORWAY,
                StrongroomPlace.fromPersistedName(name)
            )
        }
        assertEquals(
            StrongroomPlace.DOORWAY,
            StrongroomPlace.fromPersistedName("DOORWAY")
        )
    }

    @Test
    fun sealedFamilyMembershipIsAsRuled() {
        assertTrue(StrongroomPlace.SEALED_FACE.isSealedFamily)
        assertTrue(StrongroomPlace.ANTECHAMBER.isSealedFamily)
        assertTrue(StrongroomPlace.BREAK.isSealedFamily)
        assertFalse(StrongroomPlace.DOORWAY.isSealedFamily)
        assertFalse(StrongroomPlace.LIFTING.isSealedFamily)
    }

    @Test
    fun theOrderHasExactlyFivePlaces() {
        assertEquals(5, StrongroomPlace.entries.size)
    }
}
