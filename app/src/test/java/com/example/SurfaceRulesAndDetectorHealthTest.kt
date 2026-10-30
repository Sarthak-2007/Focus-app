package com.example

import com.example.data.DEFAULT_SURFACE_RULES
import com.example.data.DEFAULT_URL_BAR_SUFFIXES
import com.example.data.decodeSurfaceRules
import com.example.data.encodeSurfaceRules
import com.example.data.mergeSurfaceRules
import com.example.data.SurfaceRule
import com.example.data.surfaceViewIdsFor
import com.example.data.withLearnedViewId
import com.example.service.DetectorHealthSnapshot
import com.example.ui.components.detectorLineFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Issue #335 (Freedom F2/F3/F5): surface knowledge as data, and
 *  the detector-health record. */
class SurfaceRulesAndDetectorHealthTest {

    @Test
    fun defaultsExpressTheFormerConstants() {
        assertEquals(
            listOf(
                "com.google.android.youtube:id/reel_player_page_container",
                "com.google.android.youtube:id/shorts_player"
            ),
            surfaceViewIdsFor(DEFAULT_SURFACE_RULES, "com.google.android.youtube")
        )
        assertEquals(
            listOf(
                "com.instagram.android:id/clips_viewer_view_pager",
                "com.instagram.android:id/reel_viewer_fragment"
            ),
            surfaceViewIdsFor(DEFAULT_SURFACE_RULES, "com.instagram.android")
        )
        assertTrue(DEFAULT_URL_BAR_SUFFIXES.contains(":id/url_bar"))
    }

    @Test
    fun learnedRuleAddsAThirdAppWithoutCodeChange() {
        val merged = withLearnedViewId(
            DEFAULT_SURFACE_RULES,
            "com.tiktok.android",
            "com.tiktok.android:id/feed_player"
        )
        assertEquals(
            listOf("com.tiktok.android:id/feed_player"),
            surfaceViewIdsFor(merged, "com.tiktok.android")
        )
        // The shipped two are untouched by the extension.
        assertEquals(2, surfaceViewIdsFor(merged, "com.google.android.youtube").size)
    }

    @Test
    fun learnedRuleExtendsAKnownPackageAndDedupes() {
        val merged = mergeSurfaceRules(
            DEFAULT_SURFACE_RULES,
            listOf(
                SurfaceRule(
                    "com.google.android.youtube",
                    listOf(
                        "com.google.android.youtube:id/shorts_player", // duplicate
                        "com.google.android.youtube:id/new_player"
                    )
                )
            )
        )
        assertEquals(3, surfaceViewIdsFor(merged, "com.google.android.youtube").size)
    }

    @Test
    fun encodeDecodeRoundTripsAndToleratesCorruption() {
        val rules = listOf(
            SurfaceRule("com.example.a", listOf("com.example.a:id/x", "com.example.a:id/y")),
            SurfaceRule("com.example.b", listOf("com.example.b:id/z"))
        )
        assertEquals(rules, decodeSurfaceRules(encodeSurfaceRules(rules)))
        assertEquals(emptyList<SurfaceRule>(), decodeSurfaceRules("garbage line"))
        assertEquals(emptyList<SurfaceRule>(), decodeSurfaceRules(null))
    }

    @Test
    fun detectorHealthTracksFirstLastAndSlowScans() {
        var s = DetectorHealthSnapshot()
        assertFalse(s.hasProved)
        s = s.copy(firstNodeMono = 1_000, lastNodeMono = 1_000, nodeCount = 1)
        assertTrue(s.hasProved)
        val slow = s.copy(lastScanDurationMs = 150)
        assertTrue(slow.lastScanWasSlow)
        assertEquals(DetectorHealthSnapshot.SLOW_SCAN_THRESHOLD_MS, 100L)
    }

    @Test
    fun detectorLineNamesNeverProvedStaleLiveAndSlow() {
        val never = DetectorHealthSnapshot()
        assertEquals("DETECTOR — NEVER PROVED", detectorLineFor(never, 10_000).text)

        val stale = DetectorHealthSnapshot(firstNodeMono = 1_000, lastNodeMono = 1_000)
        assertTrue(detectorLineFor(stale, 61_000).text.startsWith("DETECTOR — STALE"))

        val live = DetectorHealthSnapshot(firstNodeMono = 1_000, lastNodeMono = 59_000)
        assertEquals("DETECTOR — LIVE", detectorLineFor(live, 61_000).text)

        val slow = live.copy(lastScanDurationMs = 240)
        assertEquals(
            "DETECTOR — LIVE · LAST SCAN 240MS SLOW",
            detectorLineFor(slow, 61_000).text
        )
    }

    @Test
    fun staleRequiresHavingProved() {
        val never = DetectorHealthSnapshot()
        assertFalse(never.isStale(999_999, DetectorHealthSnapshot.STALE_AFTER_MS))
    }
}
