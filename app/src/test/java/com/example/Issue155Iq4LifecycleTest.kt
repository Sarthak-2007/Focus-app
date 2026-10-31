package com.example

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Issue #155 — IQ-4 lifecycle pins for the WING-F stacked cures
 * (stacked on PR #540, branch agent/issue-155-wingf-iq4-cures).
 *
 * IQ-4's walk found #540's read-side truth rule sound but the
 * RENDERED glance mortal in the wrong way: a SEALED snapshot has
 * no expiry, several lifecycle doors never knock on the widget,
 * the widget has no Safe Break branch, the disarm doors never
 * refresh, and an in-loop A→B handover never re-keys the witness.
 * CRITIC-3's Charge C (LEAD-4 A8) found the beat stamped on blind
 * ticks with the row's gap latch unread at the stamp site.
 *
 * These pins follow the house pattern for cross-file mechanisms
 * (see Issue155CompletionExpiryTest's source pins): each asserts
 * the mechanism's presence at its site, so a merge train cannot
 * quietly un-wire a door. Behavioural truth-table pins for the
 * seal rule itself live in WidgetSealTruthTest; the public-version
 * gate's behavioural pins live in Issue155CompletionExpiryTest.
 */
class Issue155Iq4LifecycleTest {

    private fun source(path: String): String {
        val candidates = listOf(
            File("src/main/java/$path"),
            File("app/src/main/java/$path"),
            File("../app/src/main/java/$path")
        )
        return candidates.firstOrNull { it.exists() }?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }

    private fun providerSource() = source("com/example/widget/MonolithWidgetProvider.kt")
    private fun shieldSource() = source("com/example/service/MonolithShieldService.kt")
    private fun bootSource() = source("com/example/service/MonolithBootReceiver.kt")
    private fun repositorySource() = source("com/example/data/MonolithRepository.kt")

    @Test
    fun sealedRenderArmsItsOwnExpiry() {
        // Edge 1: the provider schedules a one-shot wakeup at the
        // rendered beat's expiry whenever it prints a proven seal.
        val src = providerSource()
        assertTrue(src.contains("armSealedExpiry"))
        assertTrue(src.contains("setAndAllowWhileIdle"))
        assertTrue(src.contains("WidgetSealTruth.expiryWakeMillis"))
        assertTrue(src.contains("sealedBeatWallMillis = session.shieldLastBeatWallMillis"))
    }

    @Test
    fun widgetHasASafeBreakBranchBeforeTheSealedBranches() {
        // Edge 8a: the break branch exists, names the partial
        // state, and is evaluated before the unqualified SEALED
        // branches in the proven when-chain.
        val src = providerSource()
        val breakIdx = src.indexOf("session.isInSafeBreak ->")
        val sealedIdx = src.indexOf("session.isStopwatchMode ->")
        assertTrue("break branch missing", breakIdx >= 0)
        assertTrue("break branch must precede the SEALED branches", breakIdx < sealedIdx)
        assertTrue(src.contains("ON BREAK"))
        assertTrue(src.contains("PEEK APPS OPEN"))
        assertTrue(src.contains("SEAL RESUMES"))
    }

    @Test
    fun widgetReadsTheGapLatch() {
        // Charge C / A8: the provider passes the row's blindness
        // witness into the truth rule.
        assertTrue(providerSource().contains("shieldGapSeen = session.shieldGapSeen"))
    }

    @Test
    fun bootActionsEndWithAWidgetRefresh() {
        // Edges 3/4/5: one requestUpdate at the end of
        // runBootActions, after the shield-after-tick group.
        val src = bootSource()
        val groupIdx = src.indexOf("shield-after-tick")
        val refreshIdx = src.indexOf("MonolithWidgetProvider.requestUpdate", groupIdx)
        assertTrue("boot refresh missing after shield-after-tick", refreshIdx > groupIdx)
    }

    @Test
    fun timeChangedBranchRefreshesTheWidget() {
        // Edge 6: TIME_SET / TIMEZONE_CHANGED re-renders beside
        // the forced heartbeat tick.
        val src = bootSource()
        val branchIdx = src.indexOf("Intent.ACTION_TIMEZONE_CHANGED ->")
        val refreshIdx = src.indexOf("MonolithWidgetProvider.requestUpdate", branchIdx)
        assertTrue("time-changed refresh missing", refreshIdx > branchIdx)
    }

    @Test
    fun breakDoorsAndDisarmDoorsRefreshTheWidget() {
        // Edges 8a/8b: break start, early end, expiry flip,
        // block-flip entry, the emergency valve and the disarm
        // funnel all knock on the glance.
        val src = repositorySource()
        fun after(anchor: String): Boolean {
            val idx = src.indexOf(anchor)
            return idx >= 0 &&
                src.indexOf("MonolithWidgetProvider.requestUpdate", idx) in
                idx..(idx + 2500)
        }
        assertTrue("startSafeBreakMidBlock refresh", after("suspend fun startSafeBreakMidBlock"))
        assertTrue("endSafeBreakEarly refresh", after("suspend fun endSafeBreakEarly"))
        assertTrue("completeEmergencyBreak refresh", after("suspend fun completeEmergencyBreak"))
        assertTrue("disarmActiveSession refresh", after("suspend fun disarmActiveSession"))
        assertTrue(src.contains("break EXPIRY flips"))
        assertTrue(src.contains("block-flip break"))
    }

    @Test
    fun loopRekeysTheWitnessOnAnInLoopSessionChange() {
        // Edge 9: the handover reset stands in the loop, keyed on
        // the active session's id, mirroring KEEP_RUNNING.
        val src = shieldSource()
        assertTrue(
            src.contains("if (protecting && protectingSessionId != activeSession.id)")
        )
    }

    @Test
    fun blindTicksDoNotStampTheBeat() {
        // Charge C, stamp side: the loop stamps only on a seeing
        // iteration, and the repository refuses a fresh beat for
        // a gap-latched row.
        assertTrue(shieldSource().contains("stampShieldBeat = usageGranted"))
        assertTrue(repositorySource().contains("stampShieldBeat && !session.shieldGapSeen"))
    }

    @Test
    fun gapLatchRefreshesTheWidget() {
        // Edge 2: the latch site in the loop re-renders, so the
        // glance falls to UNPROVEN at the latch.
        val src = shieldSource()
        val latchIdx = src.indexOf("repository.latchShieldGapNow()")
        assertTrue(latchIdx >= 0)
        assertTrue(
            src.indexOf("MonolithWidgetProvider.requestUpdate", latchIdx) in
                latchIdx..(latchIdx + 600)
        )
    }

    @Test
    fun publicVersionIsGatedOnTheProtectingWitness() {
        // A7: the lock-screen gate reads the witness and the
        // missing-grant state, not only the card's subtitle.
        // (The decision now lives in the pure shieldPublicText;
        // its inputs are the witness and the missing count.)
        val src = shieldSource()
        assertTrue(src.contains("!protecting -> \"Monolith session in progress\""))
        assertTrue(src.contains("missingCount > 0 -> \"Monolith session in progress\""))
        assertTrue(src.contains("missingCount = missing.size"))
    }

    @Test
    fun safeBreakThreadsIntoThePublicVersion() {
        // IQ-3 reseat (re-close bench): the break's public face is
        // fed by an explicit inSafeBreak threaded from the loop's
        // break branch — and the battery re-post passes the
        // remembered break state, so no post path can revive the
        // standing "protected" line mid-break.
        val src = shieldSource()
        assertTrue(src.contains("inSafeBreak = updated.isInSafeBreak"))
        assertTrue(src.contains("inSafeBreak = lastBaseInSafeBreak"))
        assertTrue(src.contains("inSafeBreak -> \"Monolith \\u2014 on a break \\u00b7 peek apps open\""))
    }
}
