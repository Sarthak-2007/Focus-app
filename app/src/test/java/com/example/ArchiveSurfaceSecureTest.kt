package com.example

import com.example.ui.MonolithDestination
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #264 (B-264-31): the archive-privacy matrix. Only the
 * Ledger (VAULT) — the surface holding every receipt, day word
 * and finished session — earns FLAG_SECURE. The working
 * surfaces (Deck, Shield, Tasks) must NOT be flagged: the block
 * flow and the Deck stay capturable exactly as before.
 */
class ArchiveSurfaceSecureTest {

    @Test
    fun ledgerIsSecure() {
        assertTrue(archiveSurfaceSecure(MonolithDestination.VAULT))
    }

    @Test
    fun workingSurfacesAreNotSecure() {
        assertFalse(archiveSurfaceSecure(MonolithDestination.DECK))
        assertFalse(archiveSurfaceSecure(MonolithDestination.SHIELD))
        assertFalse(archiveSurfaceSecure(MonolithDestination.TASKS))
    }
}
