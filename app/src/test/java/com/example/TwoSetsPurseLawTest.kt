package com.example

import com.example.domain.StrongroomDay
import com.example.domain.StrongroomRouting
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * R-C3 — THE TWO SETS ARE DISTINCT, asserted at the model seam
 * that exists today. Landing: engine tree, with P1/P2
 * (domain/StrongroomDay.kt purse · domain/StrongroomRouting.kt).
 *
 * The ruling (SUCCESSOR-21, R-C3): SET 1, the signed allowed
 * set, is reached WITHOUT the purse; SET 2, the drum, is any
 * reach beyond the signed set — one app for its minute,
 * purse-priced. "The purse never pays for the day's own shape."
 *
 * What is provable today, and asserted here:
 *  1. THE DRUM'S PRICE IS EXACT — a drum minute costs exactly
 *     its minute, every time, to the last minute; overspend is
 *     refused whole; a non-positive "spend" never grants time.
 *     (BUILD-SR-3's own purse test proves refusal by example;
 *     this proves the accounting exhaustively, minute by minute.)
 *  2. THE ANTECHAMBER'S ROAD CANNOT SPEND — routing a reach
 *     takes no purse in hand: routeReach's signature carries
 *     no purse type (census), so Set 1 traffic has no spend to
 *     make, in code, not in intention (CRITIC-SR-4 AC-7/AC-10).
 *  3. THE SETS NEVER BLUR — over the whole membership matrix,
 *     a reach lands in the antechamber exactly when its app is
 *     in the allowed set and not in the locked set; standing in
 *     both resolves to the knock (the seal wins, R-C3's sets
 *     stay mechanically different things).
 *
 * What is NOT provable today, and is not claimed here: that the
 * allowed set routed is THE SIGNED set. P1 as landed carries no
 * allowed set in SigningRecord or the composition (GAP-T2 on
 * TEST-SR-2's map; KEEPER-SR-4's X-2) — routeReach's
 * allowedPackages input has no signed source yet. The
 * signed-source tests stand written in tests-spec/S3 against
 * the ruled amendment, and land the day BUILD-SR-3 lands it.
 */
class TwoSetsPurseLawTest {

    // ---------------------------------------------------------------
    // 1 · The drum's price is exact
    // ---------------------------------------------------------------

    @Test
    fun drumReach_costsExactlyItsMinute_everyMinuteToTheStop() {
        var purse = StrongroomDay.AllowancePurse(totalMinutes = 47)
        for (spentSoFar in 1..47) {
            val before = purse.remainingMinutes
            purse = purse.spend(1)!!
            assertEquals(
                "drum minute $spentSoFar must cost exactly one minute",
                before - 1, purse.remainingMinutes
            )
            assertEquals(spentSoFar, purse.spentMinutes)
        }
        assertTrue("a spent purse is the hard stop", purse.isHardStopped)
        assertNull("past the stop the crank will not turn", purse.spend(1))
    }

    @Test
    fun drumSpend_neverPartial_neverGrants() {
        val purse = StrongroomDay.AllowancePurse(totalMinutes = 5, spentMinutes = 4)
        // One minute remains: a two-minute reach is refused WHOLE
        // and the purse is untouched by the refusal.
        assertNull(purse.spend(2))
        assertEquals(1, purse.remainingMinutes)
        // A zero or negative "spend" is no spend at all — above
        // all it never ADDS minutes (a negative price would be a
        // mint inside the seal).
        assertEquals(purse, purse.spend(0))
        assertEquals(purse, purse.spend(-30))
        assertEquals(1, purse.remainingMinutes)
    }

    // ---------------------------------------------------------------
    // 2 · The antechamber's road cannot spend
    // ---------------------------------------------------------------

    @Test
    fun antechamberRouting_takesNoPurseInHand() {
        val routeReach = StrongroomRouting::class.java.getDeclaredMethod(
            "routeReach",
            Boolean::class.javaPrimitiveType,
            String::class.java,
            Set::class.java,
            Set::class.java
        )
        assertTrue(
            "routeReach must never receive the purse — Set 1's road " +
                "has no spend to make (R-C3; AC-7/AC-10)",
            routeReach.parameterTypes.none {
                it == StrongroomDay.AllowancePurse::class.java
            }
        )
        // And in fact: a purse standing beside a hundred Set 1
        // reaches is the same purse after them — routing is a
        // pure verdict, not a transaction.
        val purse = StrongroomDay.AllowancePurse(totalMinutes = 12)
        repeat(100) {
            assertEquals(
                StrongroomRouting.ReachDecision.ANTECHAMBER,
                StrongroomRouting.routeReach(
                    sealed = true,
                    packageName = "com.gym.timer",
                    lockedPackages = setOf("com.video.app"),
                    allowedPackages = setOf("com.gym.timer")
                )
            )
        }
        assertEquals(12, purse.remainingMinutes)
        assertEquals(0, purse.spentMinutes)
    }

    // ---------------------------------------------------------------
    // 3 · The sets never blur
    // ---------------------------------------------------------------

    @Test
    fun twoSets_routeDisjointly_overTheWholeMatrix() {
        val locked = setOf("com.video.app", "com.social.app")
        val allowed = setOf("com.gym.timer", "com.music.app")
        val universe = locked + allowed + setOf("com.unlisted.app")
        universe.forEach { pkg ->
            val decision = StrongroomRouting.routeReach(
                sealed = true, packageName = pkg,
                lockedPackages = locked, allowedPackages = allowed
            )
            val expectAntechamber = pkg in allowed && pkg !in locked
            assertEquals(
                "$pkg: the antechamber is reached exactly by the signed " +
                    "allowed set standing outside the locked set",
                expectAntechamber,
                decision == StrongroomRouting.ReachDecision.ANTECHAMBER
            )
            if (!expectAntechamber) {
                assertEquals(
                    "$pkg: everything else knocks",
                    StrongroomRouting.ReachDecision.KNOCK, decision
                )
            }
        }
        // Standing in BOTH sets (a signing contradiction) resolves
        // toward the seal, never toward the opening: the sets are
        // distinct mechanisms, and where they collide the locked
        // set wins.
        assertEquals(
            StrongroomRouting.ReachDecision.KNOCK,
            StrongroomRouting.routeReach(
                sealed = true, packageName = "com.video.app",
                lockedPackages = locked, allowedPackages = allowed + "com.video.app"
            )
        )
    }
}
