package com.example

import com.example.data.AppBlockTier
import com.example.data.EssentialApps
import com.example.data.MonolithWeek
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * PIN scope: SOURCE.
 *
 * Issue #119, findings 4 + 5 — the essentials are defined by package,
 * and the dialler can never be classified LOCKED. Pure, no Android.
 */
class EssentialAppsTest {

    // ---- Finding 4: SLEEP loadout essentials, by package ----

    @Test
    fun sleepLoadoutKeepsStockCameraOpen() {
        assertEquals(
            AppBlockTier.OPEN,
            EssentialApps.sleepLoadoutTierFor("com.android.camera2", "Camera")
        )
        assertEquals(
            AppBlockTier.OPEN,
            EssentialApps.sleepLoadoutTierFor("com.google.android.GoogleCamera", "Camera")
        )
        assertEquals(
            AppBlockTier.OPEN,
            EssentialApps.sleepLoadoutTierFor("com.sec.android.app.camera", "Camera")
        )
    }

    @Test
    fun sleepLoadoutKeepsWazeOpen() {
        assertEquals(
            AppBlockTier.OPEN,
            EssentialApps.sleepLoadoutTierFor("com.waze", "Waze")
        )
    }

    @Test
    fun sleepLoadoutKeepsDialerMapsAndClockOpen() {
        assertEquals(AppBlockTier.OPEN, EssentialApps.sleepLoadoutTierFor("com.google.android.dialer", "Phone"))
        assertEquals(AppBlockTier.OPEN, EssentialApps.sleepLoadoutTierFor("com.google.android.apps.maps", "Maps"))
        assertEquals(AppBlockTier.OPEN, EssentialApps.sleepLoadoutTierFor("com.google.android.deskclock", "Clock"))
    }

    @Test
    fun sleepLoadoutStillLocksDistractions() {
        assertEquals(AppBlockTier.LOCKED, EssentialApps.sleepLoadoutTierFor("com.instagram.android", "Instagram"))
        assertEquals(AppBlockTier.LOCKED, EssentialApps.sleepLoadoutTierFor("com.zhiliaoapp.musically", "TikTok"))
        assertEquals(AppBlockTier.LOCKED, EssentialApps.sleepLoadoutTierFor("com.spotify.music", "Spotify"))
    }

    @Test
    fun sleepLoadoutLegacySubstringFallbackStillOpensVendorVariants() {
        // A vendor alarm app outside the package sets, previously OPEN
        // by substring, must not close because of this fix.
        assertEquals(
            AppBlockTier.OPEN,
            EssentialApps.sleepLoadoutTierFor("com.vendor.wakeme", "Alarm Plus")
        )
    }

    // ---- Finding 5: the dialler floor ----

    @Test
    fun dialerCanNeverBeClassifiedLocked() {
        assertEquals(
            AppBlockTier.PEEK_2M,
            EssentialApps.floorTier("com.google.android.dialer", AppBlockTier.LOCKED)
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            EssentialApps.floorTier("com.android.incallui", AppBlockTier.LOCKED)
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            EssentialApps.floorTier("com.samsung.android.dialer", AppBlockTier.LOCKED)
        )
    }

    @Test
    fun floorLeavesEverythingElseUntouched() {
        // Non-dialler packages keep exactly the tier they were given.
        assertEquals(
            AppBlockTier.LOCKED,
            EssentialApps.floorTier("com.instagram.android", AppBlockTier.LOCKED)
        )
        // The floor only raises LOCKED; it never widens a dialler to OPEN.
        assertEquals(
            AppBlockTier.PEEK_2M,
            EssentialApps.floorTier("com.google.android.dialer", AppBlockTier.PEEK_2M)
        )
        assertEquals(
            AppBlockTier.OPEN,
            EssentialApps.floorTier("com.google.android.dialer", AppBlockTier.OPEN)
        )
    }

    @Test
    fun cameraAndWazeAreEssentialButNotDialers() {
        assertTrue(EssentialApps.isEssentialPackage("com.android.camera2"))
        assertTrue(EssentialApps.isEssentialPackage("com.waze"))
        assertFalse(EssentialApps.isDialerPackage("com.android.camera2"))
        assertFalse(EssentialApps.isDialerPackage("com.waze"))
    }

    // ---- Finding 1 (truth half): when the token returns ----

    @Test
    fun nextWeekAnchorIsTheFollowingMonday0330() {
        // Issue #523 (C-6): the token returns Monday 03:30 — his day
        // seam — not Monday midnight. The probe instant (Fri 03:00)
        // is filing-Thursday under the seam and belongs to the week
        // that opened Mon Oct 26 03:30 either way.
        val ist = ZoneId.of("Asia/Kolkata")
        val friday = ZonedDateTime.of(2026, 10, 30, 3, 0, 0, 0, ist)
        val expected = ZonedDateTime.of(2026, 11, 2, 3, 30, 0, 0, ist)
        assertEquals(
            expected.toInstant().toEpochMilli(),
            MonolithWeek.nextWeekAnchorMillis(friday.toInstant().toEpochMilli(), ist)
        )
        // Exactly one week after this week's anchor.
        assertEquals(
            MonolithWeek.weekAnchorMillis(friday.toInstant().toEpochMilli(), ist) + 7L * 24 * 3600 * 1000,
            MonolithWeek.nextWeekAnchorMillis(friday.toInstant().toEpochMilli(), ist)
        )
    }
}
