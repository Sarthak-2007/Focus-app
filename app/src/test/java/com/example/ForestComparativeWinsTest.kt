package com.example

import com.example.data.FocusSessionEntity
import com.example.data.GuardSettingsEntity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Issue #289 (Audit R5 supplement — Forest, comparative audit):
 * the four findings in this section are WINS — places Monolith
 * already out-builds Forest. A win that is not pinned is a win
 * that can silently regress, so each one is stated here as a
 * test that fails the day the edge is given away:
 *
 *  W1 enforcement is non-optional (Forest's default plant is
 *     honour-system; Deep Focus is an opt-in layer). Monolith
 *     has no enforcement on/off setting anywhere in its guard
 *     settings, and both intercept doors exist in the manifest.
 *  W2 failure records are free (Forest charges coins to erase
 *     a withered record its own detector caused). Monolith has
 *     no currency, coin, or billing surface in its main code.
 *  W3 zero trackers (Forest ships 5 analytics stacks + 4 ad
 *     networks in a 257 MB APK). Monolith's active dependency
 *     lines name none of them, and the manifest hard-disables
 *     backup (allowBackup=false).
 *  W4 web blocking is session-scoped (a Forest plant cannot
 *     block a website at all). Monolith's session row freezes
 *     its held-sites count at the seal, and the accessibility
 *     service carries a site door.
 */
class ForestComparativeWinsTest {

    private fun locateMainFile(relative: String): File {
        val candidates = listOf(
            File("src/main/$relative"),
            File("app/src/main/$relative")
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("could not locate $relative from ${File(".").absolutePath}")
    }

    private fun locateProjectFile(relative: String): File {
        val candidates = listOf(
            File(relative),
            File("app/$relative")
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("could not locate $relative from ${File(".").absolutePath}")
    }

    private fun manifest(): String = locateMainFile("AndroidManifest.xml").readText()

    // W1 ------------------------------------------------------------

    @Test
    fun w1_guardSettingsCarryNoEnforcementToggle() {
        val fieldNames = GuardSettingsEntity::class.java.declaredFields.map { it.name }
        assertFalse(
            "an enforcement on/off switch appeared in guard settings: $fieldNames",
            fieldNames.any { it.contains("enforcement", ignoreCase = true) && it.contains("enabled", ignoreCase = true) }
        )
    }

    @Test
    fun w1_bothInterceptDoorsAreDeclared() {
        val m = manifest()
        assertTrue(m.contains("MonolithShieldService"))
        assertTrue(m.contains("MonolithAccessibilityService"))
        assertTrue(m.contains("foregroundServiceType=\"specialUse\""))
    }

    // W2 ------------------------------------------------------------

    @Test
    fun w2_noCurrencyOrBillingSurfaceInMainCode() {
        val mainRoot = locateMainFile("java/com/example/data/MonolithModels.kt").parentFile
            .parentFile.parentFile // com/example -> java root's parent chain guard below
        val javaRoot = locateMainFile("java/com/example/MainActivity.kt").parentFile.parentFile
        assertTrue(javaRoot.isDirectory)
        val coinWord = Regex("\\bcoins?\\b", RegexOption.IGNORE_CASE)
        val offenders = javaRoot.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .filter { file ->
                val text = file.readText()
                coinWord.containsMatchIn(text) ||
                    text.contains("BillingClient") ||
                    text.contains("com.android.billingclient")
            }
            .map { it.name }
            .toList()
        assertTrue("currency/billing surface appeared: $offenders", offenders.isEmpty())
        assertTrue(mainRoot.exists())
    }

    // W3 ------------------------------------------------------------

    @Test
    fun w3_activeDependenciesCarryNoTrackersOrAds() {
        val gradle = locateProjectFile("build.gradle.kts").readText()
        val activeLines = gradle.lines()
            .map { it.trim() }
            .filter { it.startsWith("implementation") || it.startsWith("api(") || it.startsWith("api ") }
            .filterNot { it.startsWith("//") }
            .joinToString("\n")
            .lowercase()
        for (banned in listOf(
            "firebase", "admob", "applovin", "amplitude", "appsflyer",
            "snowplow", "intercom", "vungle", "inmobi", "audience_network",
            "play.services.ads", "analytics"
        )) {
            assertFalse("tracker/ad dependency '$banned' is active", activeLines.contains(banned))
        }
    }

    @Test
    fun w3_manifestHardDisablesBackupAndDeclaresNoVpn() {
        val m = manifest()
        assertTrue(m.contains("android:allowBackup=\"false\""))
        assertFalse(m.contains("VpnService"))
        assertFalse(m.contains("BIND_VPN_SERVICE"))
    }

    // W4 ------------------------------------------------------------

    @Test
    fun w4_sessionRowFreezesHeldSitesAtSeal() {
        val fieldNames = FocusSessionEntity::class.java.declaredFields.map { it.name }
        assertTrue("heldSitesAtSeal vanished from the session row", fieldNames.contains("heldSitesAtSeal"))
        assertTrue("heldAppsAtSeal vanished from the session row", fieldNames.contains("heldAppsAtSeal"))
    }

    @Test
    fun w4_accessibilityServiceCarriesTheSiteDoor() {
        val source = locateMainFile(
            "java/com/example/service/MonolithAccessibilityService.kt"
        ).readText()
        assertTrue(source.contains("site door"))
        assertTrue(source.contains("blocked site"))
    }
}
