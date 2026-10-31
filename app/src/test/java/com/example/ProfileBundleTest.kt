package com.example

import com.example.data.AppBlockTier
import com.example.data.ProfileBundle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #253 lines 8 + 9: the profile bundle codec — the pure
 * half of the full-context profile. Formats are byte-stable
 * (sorted) so capture → apply → capture cannot drift, and
 * parses are total: a junk entry is skipped, never thrown on.
 * PIN: PLUMBING.
 */
class ProfileBundleTest {

    @Test
    fun appTiers_roundTrip_byteStable() {
        val tiers = mapOf(
            "com.zed.app" to AppBlockTier.OPEN,
            "com.alpha.app" to AppBlockTier.LOCKED,
            "com.mid.app" to AppBlockTier.PEEK_2M
        )
        val csv = ProfileBundle.formatAppTiers(tiers)
        assertEquals(
            "com.alpha.app=LOCKED,com.mid.app=PEEK_2M,com.zed.app=OPEN",
            csv
        )
        assertEquals(tiers, ProfileBundle.parseAppTiers(csv))
        // Formatting the parse returns the same bytes.
        assertEquals(csv, ProfileBundle.formatAppTiers(ProfileBundle.parseAppTiers(csv)))
    }

    @Test
    fun appLimits_roundTrip_andJunkSkipped() {
        val csv = ProfileBundle.formatAppLimits(
            mapOf("com.b" to 45, "com.a" to 10)
        )
        assertEquals("com.a=10,com.b=45", csv)
        assertEquals(
            mapOf("com.a" to 10, "com.b" to 45),
            ProfileBundle.parseAppLimits(csv)
        )
        // Junk: no equals, non-numeric, negative — all skipped.
        assertEquals(
            mapOf("com.ok" to 5),
            ProfileBundle.parseAppLimits("junk,com.x=abc,com.y=-3,com.ok=5,=9")
        )
        assertTrue(ProfileBundle.parseAppLimits("").isEmpty())
    }

    @Test
    fun appTiers_junkSkipped_unknownTierSkipped() {
        assertEquals(
            mapOf("com.ok" to AppBlockTier.LOCKED),
            ProfileBundle.parseAppTiers("com.bad=WOBBLE,plainstring,com.ok=LOCKED,=OPEN")
        )
        assertTrue(ProfileBundle.parseAppTiers("").isEmpty())
    }

    @Test
    fun sites_normalisedSortedDeduped() {
        assertEquals(
            listOf("a.com", "b.com"),
            ProfileBundle.parseSites(" B.com ,a.com,b.com,,A.COM ")
        )
        assertEquals(
            "a.com,b.com",
            ProfileBundle.formatSites(listOf("B.com", "a.com", "b.com"))
        )
        assertTrue(ProfileBundle.parseSites("").isEmpty())
    }
}
