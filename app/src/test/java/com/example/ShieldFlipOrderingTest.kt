package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The shield witness's flip ordering (critic Nagel N-3; issue #7,
 * ZEUGE). `protecting` is the Shield sentence's SHIELD witness and
 * the seal cascade's L2 stamp: it must mean a full monitor
 * iteration has run. That claim is about ORDER inside
 * MonolithShieldService's loop — the pure derivation tests cannot
 * see it, so this test reads the service source (the same
 * source-structure pattern as TruthCar3SweepTest) and pins the
 * order inside the loop body itself: blocked set built, foreground
 * queried, intercept launched if due, the flip only after all
 * three, and the flip before the cadence delay (the cascade's
 * annul deadline cannot wait out a 2s sleep).
 *
 * Hardened per BAU-1's audit: assertions run on the loop's slice
 * of the file (not the whole file), and every needle must occur
 * exactly once in the file — a duplicate in a comment or a second
 * function fails here instead of silently re-pointing the indices.
 * Re-pointed once, Oct 30 (ZEUGE, PR #482): the lane's loop grew
 * around the flip — the foreground query now rides a
 * ForegroundSelection lambda with an expiredPeeks argument, the
 * intercept launches through InterceptLaunch (issue #94), and the
 * exception path added a second cadence delay. The N-3 order
 * itself was verified unchanged in the merged source before the
 * needles moved; the delay needle is now pinned as exactly two
 * occurrences in the loop, both after the flip, with the witness
 * cleared before the retry sleep.
 * What this test still cannot see, stated plainly: iteration
 * identity and exception paths. It pins text order in the one
 * loop; execution is CI's and the phone's to prove.
 */
class ShieldFlipOrderingTest {

    private fun shieldServiceSource(): String {
        val relative = "com/example/service/MonolithShieldService.kt"
        val candidates = listOf(
            File("src/main/java/$relative"),
            File("app/src/main/java/$relative")
        )
        val file = candidates.firstOrNull { it.exists() }
            ?: error("MonolithShieldService.kt not found from ${candidates.map { it.absolutePath }}")
        return file.readText()
    }

    private fun occurrences(haystack: String, needle: String): Int =
        haystack.split(needle).size - 1

    @Test
    fun protectingFlip_landsAfterTheEnforcementAct_beforeTheCadenceDelay() {
        val src = shieldServiceSource()
        val loopStart = src.indexOf("private fun startMonitoringLoop")
        val loopEnd = src.indexOf("private fun createNotificationChannel")
        assertTrue("the monitoring loop must exist", loopStart >= 0)
        assertTrue("the loop slice must be bounded", loopEnd > loopStart)
        val loop = src.substring(loopStart, loopEnd)

        val needles = listOf(
            "val blockedPackages",
            "queryForegroundBlockedApp(blockedPackages, expiredPeeks)",
            "InterceptLaunch.launch(",
            "protecting = true"
        )
        for (needle in needles) {
            assertEquals(
                "'$needle' must occur exactly once in the file — " +
                    "a duplicate would re-point this test's indices",
                1, occurrences(src, needle)
            )
            assertTrue("'$needle' must occur inside the loop", loop.contains(needle))
        }
        // The cadence delay is the one needle allowed to repeat:
        // the loop sleeps 2s per pass AND 2s before retrying a
        // failed pass (the exception path). Both sleeps must sit
        // after the flip — see the ordering assertions below.
        assertEquals(
            "'delay(2000L)' must occur exactly twice in the loop — cadence + exception retry",
            2, occurrences(loop, "delay(2000L)")
        )

        val setIdx = loop.indexOf("val blockedPackages")
        val queryIdx = loop.indexOf("queryForegroundBlockedApp(blockedPackages)")
        val interceptIdx = loop.indexOf("InterceptLaunch.launch(")
        val guardIdx = loop.indexOf("if (!protecting || protectingSessionId != updated.id)")
        val flipIdx = loop.indexOf("protecting = true")
        val delayIdx = loop.indexOf("delay(2000L)")

        assertTrue("the blocked set is built before the foreground query", setIdx < queryIdx)
        assertTrue(
            "N-3: the flip must land AFTER the foreground query — " +
                "a flip before it attests a pass that enforced nothing",
            flipIdx > queryIdx
        )
        assertTrue("N-3: the flip must land AFTER the intercept launch", flipIdx > interceptIdx)
        assertTrue(
            "the flip's guard sits between the intercept and the flip",
            guardIdx > interceptIdx && guardIdx < flipIdx
        )
        assertTrue(
            "the flip must land BEFORE the 2s cadence delay — " +
                "the seal cascade's annul deadline cannot wait it out",
            flipIdx < delayIdx
        )
        // The exception path's retry delay: the witness is cleared
        // before that sleep, so a failed pass never leaves
        // `protecting` standing while the loop backs off.
        val retryDelayIdx = loop.indexOf("delay(2000L)", delayIdx + 1)
        val clearIdx = loop.indexOf("protecting = false", flipIdx)
        assertTrue(
            "the exception path clears the witness before its retry delay",
            clearIdx > flipIdx && clearIdx < retryDelayIdx
        )
        // The flip's companions are stamped at the same flip,
        // inside the same guarded block, before the delay.
        val sessionStampIdx = loop.indexOf("protectingSessionId = updated.id")
        val monoStampIdx = loop.indexOf("protectingMono = SystemClock.elapsedRealtime()")
        assertTrue("the session stamp rides the flip", sessionStampIdx in (flipIdx + 1) until delayIdx)
        assertTrue("the mono stamp rides the flip", monoStampIdx in (flipIdx + 1) until delayIdx)
    }
}
