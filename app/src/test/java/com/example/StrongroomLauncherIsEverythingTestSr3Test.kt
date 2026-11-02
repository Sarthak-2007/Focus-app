package com.example

import com.example.domain.StrongroomRouting
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TEST-SR-3 (T40) — THE LAUNCHER IS EVERYTHING (ledger D-7),
 * tested as NAVIGATION ASSERTIONS.
 *
 * While a session is sealed, the launcher surfaces are the whole
 * phone: no route to the Deck, the signing table (settings), or
 * the records until the session ends. At the routing level that
 * claim is a CLOSED SET: this suite sweeps the entire Surface
 * enum — a surface added tomorrow is classified here by force,
 * and the lawful set must be exactly the five sealed surfaces.
 *
 * SCOPE STAMP: these assertions prove the routing model's closed
 * set and the reach verdicts. Whether the handset's navigation
 * (accessibility interception, launcher role) enforces the set
 * on Sarthak's OnePlus stands UNPROVEN-ON-DEVICE (Audit A G-7);
 * the surfaces lane's wiring of GlanceRouting to this set is a
 * separate named obligation (see the obligations map, O-D7-3).
 */
class StrongroomLauncherIsEverythingTestSr3Test {

    @Test
    fun navigation_exhaustiveSweep_everySurfaceClassified_lawfulSetIsExactlyFive() {
        val lawful = setOf(
            StrongroomRouting.Surface.SEALED_FACE,
            StrongroomRouting.Surface.ANTECHAMBER,
            StrongroomRouting.Surface.HELD_CALLS,
            StrongroomRouting.Surface.DESK_CLOCK,
            StrongroomRouting.Surface.LOCK_NOTIFICATION
        )
        // Every surface that exists is accounted for by name.
        assertEquals(9, StrongroomRouting.Surface.values().size)
        for (surface in StrongroomRouting.Surface.values()) {
            assertEquals(
                "surface $surface classified wrongly",
                surface in lawful,
                StrongroomRouting.surfaceLawfulWhileSealed(surface)
            )
        }
        assertEquals(lawful, StrongroomRouting.SEALED_SURFACES)
    }

    @Test
    fun navigation_noRouteToDeckSettingsRecordsOrTheNormalAppWhileSealed() {
        assertFalse(StrongroomRouting.surfaceLawfulWhileSealed(StrongroomRouting.Surface.DECK))
        assertFalse(StrongroomRouting.surfaceLawfulWhileSealed(StrongroomRouting.Surface.SETTINGS))
        assertFalse(StrongroomRouting.surfaceLawfulWhileSealed(StrongroomRouting.Surface.RECORDS))
        assertFalse(StrongroomRouting.surfaceLawfulWhileSealed(StrongroomRouting.Surface.NORMAL_APP))
    }

    @Test
    fun navigation_reachVerdicts_neverNameANormalAppSurface() {
        // The reach decision type itself is closed: KNOCK,
        // ANTECHAMBER, NOT_SEALED — there is no verdict a caller
        // could render as "open it in the normal app".
        assertEquals(3, StrongroomRouting.ReachDecision.values().size)
        val locked = setOf("com.video.app")
        val allowed = setOf("com.notes.app")
        val cases = listOf(
            Triple(true, "com.video.app", StrongroomRouting.ReachDecision.KNOCK),
            Triple(true, "com.notes.app", StrongroomRouting.ReachDecision.ANTECHAMBER),
            Triple(true, "com.unlisted.app", StrongroomRouting.ReachDecision.KNOCK),
            Triple(false, "com.video.app", StrongroomRouting.ReachDecision.NOT_SEALED)
        )
        for ((sealed, pkg, expected) in cases) {
            assertEquals(
                expected,
                StrongroomRouting.routeReach(sealed, pkg, locked, allowed)
            )
        }
        // Fail-closed sweep: a hundred unlisted packages, every
        // one knocks — the sealed phone has no discovery path.
        for (i in 0 until 100) {
            assertEquals(
                StrongroomRouting.ReachDecision.KNOCK,
                StrongroomRouting.routeReach(true, "com.unknown.$i", locked, allowed)
            )
        }
    }

    @Test
    fun navigation_routingStepsAsideOnlyWhenNoSealStands() {
        // The single door back to the whole phone is the seal
        // having lifted (or never stood) — routing returns
        // NOT_SEALED and has no say. While sealed it never does,
        // for any package, listed or not.
        assertEquals(
            StrongroomRouting.ReachDecision.NOT_SEALED,
            StrongroomRouting.routeReach(false, "com.notes.app", setOf("com.video.app"), setOf("com.notes.app"))
        )
    }
}
