package com.example

import com.example.service.TactileSoundEngine
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Issue #288 (Audit R5 supplement — Forest, visual design): the
 * verified state of the four findings, stated as tests so the
 * record cannot drift from the code again.
 *
 *  F2 (type system): the audit recommends consolidating to one
 *     family and retiring the third. STRUCK — the three families
 *     are Sarthak's own explicit picks (Oct 18, 2026: Sora for
 *     display, Atkinson Hyperlegible for body, Anonymous Pro for
 *     telemetry/numbers — "use these for everything"). The
 *     numerals ARE a dedicated face by his law: Anonymous Pro is
 *     the telemetry face. This test pins his law, not the audit's.
 *  F3 (stage-keyed sound): the claim "one frozen waveform for
 *     every outcome" is false for the engine as a whole — latch,
 *     break-warning, break-end, tear and completion are distinct
 *     stage-keyed sounds. Only the seal THROW is frozen, by
 *     deliberate identity law (SealThrowIdentityTest's golden
 *     hash). This test pins the stage-keyed surface.
 *  F4 (tower land skins): VERIFIED-FALSE on current main — the
 *     tower view the finding describes was deleted whole in the
 *     issue #61 truth fixes; no tower surface exists to theme.
 *  F1 (Rive/Lottie runtime): no animation runtime is present —
 *     pinned as the current truth; adoption is a design-programme
 *     ruling, not a drive-by dependency.
 */
class ForestVisualDesignTruthTest {

    private fun locateMainFile(relative: String): File {
        val candidates = listOf(
            File("src/main/$relative"),
            File("app/src/main/$relative")
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("could not locate $relative from ${File(".").absolutePath}")
    }

    private fun locateResDir(): File {
        val candidates = listOf(File("src/main/res/font"), File("app/src/main/res/font"))
        return candidates.firstOrNull { it.isDirectory }
            ?: error("could not locate res/font from ${File(".").absolutePath}")
    }

    private fun locateProjectFile(relative: String): File {
        val candidates = listOf(File(relative), File("app/$relative"))
        return candidates.firstOrNull { it.exists() }
            ?: error("could not locate $relative from ${File(".").absolutePath}")
    }

    // F2 ------------------------------------------------------------

    @Test
    fun f2_theThreeFamiliesAreSarthaksPicksAndAllPresent() {
        val fonts = locateResDir().list()?.toList().orEmpty()
        assertTrue("Sora cuts missing: $fonts", fonts.any { it.startsWith("sora") })
        assertTrue("Atkinson cuts missing: $fonts", fonts.any { it.startsWith("atkinson") })
        assertTrue("Anonymous Pro cuts missing: $fonts", fonts.any { it.startsWith("anonymous_pro") })
        val type = locateMainFile("java/com/example/ui/theme/Type.kt").readText()
        assertTrue(type.contains("SoraFamily"))
        assertTrue(type.contains("AtkinsonHyperlegibleFamily"))
        assertTrue(type.contains("AnonymousProFamily"))
    }

    // F3 ------------------------------------------------------------

    @Test
    fun f3_soundEngineKeysSoundsToSessionStages() {
        val methods = TactileSoundEngine::class.java.methods.map { it.name }.toSet()
        for (stageSound in listOf(
            "playMechanicalLatch",   // arm / disarm
            "playBreakWarningKnock", // break running out
            "playBreakEndSummons",   // break over
            "playHarmonicSeal",      // sealing
            "playSealThrow",         // the throw itself
            "playTearPulse",         // tear / break of the seal
            "playCompletionPulse"    // finish
        )) {
            assertTrue("stage sound $stageSound missing", methods.contains(stageSound))
        }
    }

    // F4 ------------------------------------------------------------

    @Test
    fun f4_noTowerSurfaceExistsOnMain() {
        val javaRoot = locateMainFile("java/com/example/MainActivity.kt").parentFile.parentFile
        val towerOffenders = javaRoot.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .filter { it.readText().contains("class Tower") || it.readText().contains("TowerView(") }
            .map { it.name }
            .toList()
        assertTrue("a tower surface returned: $towerOffenders", towerOffenders.isEmpty())
        val towerFile = locateMainFile(
            "java/com/example/ui/views/BlockitTowerFadersAndCatalogViews.kt"
        ).readText()
        assertTrue(towerFile.contains("TowerStreakView was DELETED"))
    }

    // F1 ------------------------------------------------------------

    @Test
    fun f1_noAnimationRuntimeDependencyIsActive() {
        val gradle = locateProjectFile("build.gradle.kts").readText()
        val activeLines = gradle.lines()
            .map { it.trim() }
            .filter { it.startsWith("implementation") || it.startsWith("api(") || it.startsWith("api ") }
            .filterNot { it.startsWith("//") }
            .joinToString("\n")
            .lowercase()
        assertFalse(activeLines.contains("rive"))
        assertFalse(activeLines.contains("lottie"))
    }
}
