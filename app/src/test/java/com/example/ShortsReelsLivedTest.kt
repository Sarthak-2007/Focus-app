package com.example

import com.example.service.SHORTS_PLAYER_VIEW_IDS
import com.example.service.isBottomNavBounds
import com.example.service.isFeedTabLabel
import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * #131 — shorts-reels-lived (Audit R3b). Four charges, four seams:
 *
 *  1. A live peek lease silenced the sniper, turning the one
 *     sanctioned YouTube entry (factory tier PEEK_2M) into a
 *     2-minute Shorts pass. The cure is at the call site — the
 *     sniper gate no longer consults the lease — so it is guarded
 *     here by a source tripwire in the TruthCar3Sweep style, plus
 *     the assertion that the SITE door still honours the lease
 *     (the amendment is sniper-only, not a blanket lease repeal).
 *     COMPOSITION NOTE (W3 #130, on top of #131): #130 is the
 *     separately-ordered site-door amendment #131's text foresaw
 *     as out of its scope — the site gate no longer honours the
 *     BLANKET lease either; it honours only a domain-scoped site
 *     peek (sitePeekExempts + SitePeekStore). The site-door
 *     tripwire below was updated to pin that composed truth; the
 *     sniper tripwires are #131's, unchanged.
 *  2. The PARK toggle was a lived no-op in default config: cured
 *     by the same wiring (the toggle now decides whether Shorts
 *     may play inside the default YouTube peek). The gate function
 *     itself is already truth-tabled in ShortsSniperTruthTest.
 *  3. The board claimed more than the sniper polices: the PARK
 *     plate now says FEED, and the service header states the
 *     scope. Guarded by the plate tripwire below.
 *  4. Detection punished proximity: the shelf ID
 *     (reel_recycler) fired from anywhere in the tree, and any
 *     selected 'Shorts' node fired wherever it sat. The pure
 *     seams — player-ID set, tab label, bottom-nav region — are
 *     truth-tabled here; the service additionally requires a
 *     player node to be visibleToUser.
 */
class ShortsReelsLivedTest {

    // --- Finding 4: player surfaces, not proximity ---------------

    @Test
    fun playerIds_arePlayerSurfaces_shelfIdExcluded() {
        assertTrue(SHORTS_PLAYER_VIEW_IDS.contains("com.google.android.youtube:id/shorts_player"))
        assertTrue(SHORTS_PLAYER_VIEW_IDS.contains("com.google.android.youtube:id/reel_player_page_container"))
        assertTrue(SHORTS_PLAYER_VIEW_IDS.contains("com.instagram.android:id/clips_viewer_view_pager"))
        assertTrue(SHORTS_PLAYER_VIEW_IDS.contains("com.instagram.android:id/reel_viewer_fragment"))
        // The shelf container is proximity, not watching.
        assertFalse(SHORTS_PLAYER_VIEW_IDS.contains("com.google.android.youtube:id/reel_recycler"))
    }

    @Test
    fun feedTabLabel_matchesFeedTabsOnly() {
        assertTrue(isFeedTabLabel("shorts", ""))
        assertTrue(isFeedTabLabel("", "reels"))
        assertTrue(isFeedTabLabel("", "shorts, 1 of 5"))
        assertTrue(isFeedTabLabel("reels", ""))
        assertFalse(isFeedTabLabel("home", ""))
        assertFalse(isFeedTabLabel("shortshelf", ""))
        assertFalse(isFeedTabLabel("", "subscriptions"))
    }

    @Test
    fun bottomNavBounds_bottomFifthOnly() {
        val screen = 2400
        // Bottom navigation: tab bottom edge at/near screen bottom.
        assertTrue(isBottomNavBounds(2400, screen))
        assertTrue(isBottomNavBounds(2000, screen))
        assertTrue(isBottomNavBounds(1920, screen))
        // A channel tab strip / search chip in the upper screen.
        assertFalse(isBottomNavBounds(600, screen))
        assertFalse(isBottomNavBounds(1200, screen))
        assertFalse(isBottomNavBounds(1919, screen))
        // No measured window height: no proof, no fire.
        assertFalse(isBottomNavBounds(2400, 0))
    }

    // --- Finding 1/2: the lease no longer silences the sniper ----

    @Test
    fun sniperGate_doesNotConsultTheLease_siteDoorHonoursOnlyScopedPeek() {
        val source = locateMainSource(
            "com/example/service/MonolithAccessibilityService.kt"
        ).readText()
        assertFalse(
            "the sniper gate consults the peek lease again — a peek is back to being a Shorts pass",
            source.contains("if (!leaseLive && shortsSniperArmed")
        )
        assertTrue(
            "the sniper gate must still exist, armed by the persisted switch alone",
            // #130 composition repair: the #42 pipeline merge
            // re-shaped this gate to `... && snapshot.facts.…`
            // after #131 wrote this tripwire, so the original
            // substring no longer exists on main and this
            // assertion was already red there. The pinned shape
            // below is the gate's current form — same intent:
            // the switch (plus the captured feed fact), no lease.
            // #166 U2 re-pin (W2 BUILDER-B4): the gate has since
            // gained its surface-store parameter
            // (`shortsSniperArmed(guardSettings, pkgName, surfaceStore)`),
            // so the two-argument pin died again; the pin below is
            // the gate's full current prefix — stricter, same
            // intent: the switch + the store + the captured feed
            // fact, still no lease anywhere in it.
            // #166 re-pin 3 (K8, seventh sitting): Audit R6 #340
            // F62 composed a second trigger fact onto the gate —
            // the tab TAP is itself an offence — so the condition
            // is now a two-line head ending in a disjunction of
            // the two captured feed facts. PR #587's re-pin died
            // on exactly that reshape. The pin below is the gate's
            // full current head, verbatim; below it, the invariant
            // itself is asserted shape-free, so the next honest
            // reshape of the condition cannot silently re-open
            // #131's charge: whatever the head's shape, no lease
            // term may appear inside the sniper gate's condition.
            source.contains(
                "if (shortsSniperArmed(guardSettings, pkgName, surfaceStore) &&\n" +
                    "            (snapshot.facts.sniperFeedVisible || snapshot.facts.sniperTabClicked)"
            )
        )
        val gateStart = source.indexOf(
            "if (shortsSniperArmed(guardSettings, pkgName, surfaceStore)"
        )
        assertTrue(
            "the sniper gate must still exist, armed by the persisted switch alone",
            gateStart >= 0
        )
        val gateHead = source.substring(
            gateStart, source.indexOf(") {", gateStart)
        )
        assertFalse(
            "the sniper gate's condition consults the lease — #131's charge re-opened",
            gateHead.contains("lease")
        )
        // #130 composition (see the class doc): the site gate must
        // NOT be blanket-silenced by a package lease any more —
        // its only exemption is the domain-scoped site peek.
        assertFalse(
            "the site blocker is blanket-silenced by the lease again — a browser peek is back to being a web pass (#130)",
            source.contains("if (!leaseLive && (guardSettings?.browserBlockerEnabled")
        )
        assertTrue(
            "the site blocker must honour the domain-scoped site peek (sitePeekExempts, #130)",
            source.contains("sitePeekExempts(")
        )
    }

    // --- Finding 3: the board's claim matches the sniper's scope -

    @Test
    fun parkPlate_claimsFeedScopeOnly() {
        val source = locateMainSource(
            "com/example/ui/components/ParkDrawer.kt"
        ).readText()
        assertTrue(
            "the PARK sniper plate must claim FEED scope, not whole-app policing",
            // Recomposition repair (PR #445): the design-revamp
            // reworded the plate to carry its setting word
            // ("SHORTS + REELS FEED \u2014 SETTING", ParkDrawer.kt)
            // after #131 wrote this tripwire, so the exact-match
            // string no longer exists on main and this assertion
            // was already red there. The pinned shape below is
            // the plate's current form \u2014 same intent: the
            // claim is FEED scope, not whole-app policing.
            source.contains("text = \"SHORTS + REELS FEED \u2014 SETTING\"")
        )
    }

    companion object {
        /** Locate a main-source file from the test working directory,
         *  whether tests run from the module dir or the repo root
         *  (TruthCar3SweepTest's helper, copied). */
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
