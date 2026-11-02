package com.example

import com.example.data.DoorWordLaw
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * BUILD-SR-37 covering tests: the door-word's form law
 * (GAP-COORD-01 as ruled at PR #873 — a single word of his
 * choosing; the app supplies, suggests and defaults none).
 */
class DoorWordStoreTest {

    @Test
    fun oneWordStands_asWritten() {
        assertEquals("anchor", DoorWordLaw.lawfulWord("anchor"))
        assertEquals("anchor", DoorWordLaw.lawfulWord("  anchor  "))
    }

    @Test
    fun unsetIsTheKeptPlace_aCompleteState() {
        assertNull(DoorWordLaw.lawfulWord(null))
        assertNull(DoorWordLaw.lawfulWord(""))
        assertNull(DoorWordLaw.lawfulWord("   "))
    }

    @Test
    fun moreThanOneWordIsNotADoorWord() {
        assertNull(DoorWordLaw.lawfulWord("two words"))
        assertNull(DoorWordLaw.lawfulWord("a\tb"))
    }

    @Test
    fun contentIsHis_exemptFromAnyBench() {
        // Form only is judged: any single token stands, whatever
        // it says — like his sentence, the word is his.
        assertEquals("zz9!", DoorWordLaw.lawfulWord("zz9!"))
        assertEquals("Élan", DoorWordLaw.lawfulWord("Élan"))
    }
}
