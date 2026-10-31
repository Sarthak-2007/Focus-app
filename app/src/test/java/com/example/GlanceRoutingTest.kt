package com.example

import com.example.ui.GlanceAbout
import com.example.ui.GlanceRouting
import com.example.ui.MonolithDestination
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #155 F5 (WING-F, BUILDER-4): the glance-tap routing law.
 *
 * Pins, per glance state, the destination a tap must land on —
 * and the two gates that force the table: the seal law in
 * [com.example.ui.MonolithViewModel.navigateTo] (non-DECK refused
 * while a session is active) and the receipt slip's DECK gate in
 * MainActivity (the standing slip renders only on the Deck).
 * A routing row that named SHIELD for a live state would be a
 * promise the ViewModel refuses; a completion row naming
 * anything but DECK would land where the receipt is not.
 */
class GlanceRoutingTest {

    @Test
    fun routing_liveSession_landsOnTheSealedSessionSurface() {
        assertEquals(
            MonolithDestination.DECK,
            GlanceRouting.destinationFor(GlanceAbout.LIVE_SESSION)
        )
    }

    @Test
    fun routing_safeBreak_landsOnTheSealedSessionSurface() {
        assertEquals(
            MonolithDestination.DECK,
            GlanceRouting.destinationFor(GlanceAbout.SAFE_BREAK)
        )
    }

    @Test
    fun routing_arming_landsOnTheArmingDeck() {
        assertEquals(
            MonolithDestination.DECK,
            GlanceRouting.destinationFor(GlanceAbout.ARMING)
        )
    }

    @Test
    fun routing_breakOverSummons_landsOnTheLiveSessionSurface() {
        assertEquals(
            MonolithDestination.DECK,
            GlanceRouting.destinationFor(GlanceAbout.BREAK_OVER)
        )
    }

    @Test
    fun routing_completion_landsWhereTheReceiptStands() {
        // The completion card announces the receipt; the standing
        // slip stands only on the Deck. SHIELD (the #312 route)
        // is the regression this pin exists to forbid.
        assertEquals(
            MonolithDestination.DECK,
            GlanceRouting.destinationFor(GlanceAbout.COMPLETION)
        )
    }

    @Test
    fun routing_noAbout_performsNoRouting() {
        assertNull(GlanceRouting.destinationFor(null))
    }

    @Test
    fun routing_noRowNamesASurfaceTheSealVetoesLive() {
        // Every state that can exist while a session is live must
        // route to DECK — the only destination navigateTo never
        // refuses under a seal.
        val liveStates = listOf(
            GlanceAbout.ARMING,
            GlanceAbout.LIVE_SESSION,
            GlanceAbout.SAFE_BREAK,
            GlanceAbout.BREAK_OVER
        )
        for (state in liveStates) {
            assertEquals(
                "live state $state must route to DECK",
                MonolithDestination.DECK,
                GlanceRouting.destinationFor(state)
            )
        }
    }

    @Test
    fun parseAbout_roundTripsEveryState() {
        for (state in GlanceAbout.entries) {
            assertEquals(state, GlanceRouting.parseAbout(state.name))
        }
    }

    @Test
    fun parseAbout_unknownOrAbsent_isNull() {
        assertNull(GlanceRouting.parseAbout(null))
        assertNull(GlanceRouting.parseAbout(""))
        assertNull(GlanceRouting.parseAbout("SETTINGS"))
        assertNull(GlanceRouting.parseAbout("live_session"))
    }

    @Test
    fun legacyInference_activeSession_isLiveSession() {
        assertEquals(
            GlanceAbout.LIVE_SESSION,
            GlanceRouting.aboutForLegacyShieldTap(sessionActive = true)
        )
    }

    @Test
    fun legacyInference_noSession_isTheDetachedCompletionCard() {
        assertEquals(
            GlanceAbout.COMPLETION,
            GlanceRouting.aboutForLegacyShieldTap(sessionActive = false)
        )
    }

    @Test
    fun source_mainActivity_routesThroughGlanceRouting_neverDirectlyToShield() {
        val source = locateMainSource("com/example/MainActivity.kt").readText()
        assertTrue(
            "MainActivity must route glance taps through GlanceRouting",
            source.contains("GlanceRouting.destinationFor")
        )
        assertFalse(
            "the #312 single route to SHIELD must not return",
            source.contains("navigateTo(MonolithDestination.SHIELD)")
        )
    }

    @Test
    fun source_glanceRouting_carriesNoSessionControlSurface() {
        // Identity law: a glance tap moves the user's eyes, never
        // the seal. The routing file must not reference any
        // session-ending machinery.
        val source = locateMainSource("com/example/ui/GlanceRouting.kt").readText()
        for (forbidden in listOf(
            "ACTION_STOP_SHIELD", "stopShield", "EmergencyBreak", "disarm"
        )) {
            assertFalse(
                "GlanceRouting must never reference $forbidden",
                source.contains(forbidden)
            )
        }
    }

    companion object {
        /** Locate a main-source file from the test working directory,
         *  whether tests run from the module dir or the repo root —
         *  same pattern as BootDeviceAdminTruthTest.locateMainSource. */
        fun locateMainSource(relative: String): File {
            val candidates = listOf(
                File("src/main/java/$relative"),
                File("app/src/main/java/$relative")
            )
            return candidates.firstOrNull { it.exists() }
                ?: error("could not locate $relative from ${File(".").absolutePath}")
        }
    }
}
