package com.example

import com.example.service.GlanceAbout
import com.example.service.GlanceRouting
import com.example.session.StrongroomPlace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * BUILD-SR-34 — GlanceRouting re-targeted with the enum's death:
 * the per-state table's rows stay distinct, and every row lands
 * at the doorway, the dead Deck's successor room. A tap that is
 * not a glance tap routes nowhere.
 */
class GlanceRoutingTest {

    @Test
    fun everyGlanceStateLandsAtTheDoorway() {
        for (about in GlanceAbout.entries) {
            assertEquals(
                "glance about $about must land at the doorway",
                StrongroomPlace.DOORWAY,
                GlanceRouting.placeFor(about)
            )
        }
    }

    @Test
    fun aNonGlanceTapRoutesNowhere() {
        assertNull(GlanceRouting.placeFor(null))
    }

    @Test
    fun aboutParsingAndLegacyInferenceStand() {
        assertEquals(GlanceAbout.COMPLETION, GlanceRouting.parseAbout("COMPLETION"))
        assertNull(GlanceRouting.parseAbout("SHIELD"))
        assertNull(GlanceRouting.parseAbout(null))
        assertEquals(
            GlanceAbout.LIVE_SESSION,
            GlanceRouting.aboutForLegacyShieldTap(sessionActive = true)
        )
        assertEquals(
            GlanceAbout.COMPLETION,
            GlanceRouting.aboutForLegacyShieldTap(sessionActive = false)
        )
    }
}
