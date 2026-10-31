package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.service.TilePlacement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #274: the placement testimony behind MonolithTileService.
 * The service's own lifecycle is the fact's only source —
 * MonolithTileService.onTileAdded records the placement and
 * onTileRemoved clears it (both delegate to TilePlacement,
 * the store this test exercises against a real context) — and
 * PARK's QUICK SETTINGS TILE line prints ADDED only on that
 * recorded testimony. A placement that was never recorded must
 * read as not added: the board never claims a tile the system
 * never confirmed.
 *
 * PIN scope: PLUMBING (the SharedPreferences store round-trip
 * the lifecycle callbacks write and the board reads).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MonolithTileServiceTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        TilePlacement.recordRemoved(context)
    }

    @Test
    fun `unrecorded placement reads as not added`() {
        assertFalse(TilePlacement.isAdded(context))
        assertNull(TilePlacement.word(TilePlacement.isAdded(context)))
    }

    @Test
    fun `recorded add is what the tile service lifecycle writes on tile added`() {
        TilePlacement.recordAdded(context)
        assertTrue(TilePlacement.isAdded(context))
        assertEquals("ADDED", TilePlacement.word(TilePlacement.isAdded(context)))
    }

    @Test
    fun `recorded removal clears the word again`() {
        TilePlacement.recordAdded(context)
        TilePlacement.recordRemoved(context)
        assertFalse(TilePlacement.isAdded(context))
        assertNull(TilePlacement.word(TilePlacement.isAdded(context)))
    }
}
