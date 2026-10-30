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
            "queryForegroundBlockedApp(blockedPackages)",
            "startActivity(interceptIntent)",
            "protecting = true",
            "delay(2000L)"
        )
        for (needle in needles) {
            assertEquals(
                "'$needle' must occur exactly once in the file — " +
                    "a duplicate would re-point this test's indices",
                1, occurrences(src, needle)
            )
            assertTrue("'$needle' must occur inside the loop", loop.contains(needle))
        }

        val setIdx = loop.indexOf("val blockedPackages")
        val queryIdx = loop.indexOf("queryForegroundBlockedApp(blockedPackages)")
        val interceptIdx = loop.indexOf("startActivity(interceptIntent)")
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
        // The flip's companions are stamped at the same flip,
        // inside the same guarded block, before the delay.
        val sessionStampIdx = loop.indexOf("protectingSessionId = updated.id")
        val monoStampIdx = loop.indexOf("protectingMono = SystemClock.elapsedRealtime()")
        assertTrue("the session stamp rides the flip", sessionStampIdx in (flipIdx + 1) until delayIdx)
        assertTrue("the mono stamp rides the flip", monoStampIdx in (flipIdx + 1) until delayIdx)
    }
}
