package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCatalogReconcile
import com.example.data.AppCategorizer
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.InstalledPackage
import com.example.service.InterceptPath
import com.example.service.MEDIA_STYLE_TEMPLATE
import com.example.service.isMediaPlaybackNotification
import com.example.ui.peekOfferFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #132 — Audit R3b, angle "music-during-focus" (persona: a
 * student who studies with music). Each test fails on the
 * pre-cure behaviour named in the audit:
 *
 *  F1 — a media playback notification was filed as a held
 *       message and cancelled, stripping pause/skip controls.
 *  F2 — a LOCKED player got no peek and Safe Break never freed
 *       it, so music could not lawfully be started mid-session.
 *  F3 — no music category existed: YouTube Music was born
 *       LOCKED as SHORT_VIDEO, "…Player" apps were GAMES,
 *       Spotify was OPEN only by substring accident.
 *  F4 — SLEEP routine re-tiering is STANDS-BLOCKED on the
 *       routines-rethink decision; no test here changes or
 *       blesses routine behaviour.
 */
class MusicDuringFocusTest {

    // -------------------------------------------------------------
    // F3 — the classifier gives media players a deterministic home.
    // -------------------------------------------------------------

    @Test
    fun youTubeMusic_isMusic_notShortVideo() {
        assertEquals(
            AppCategory.MUSIC,
            AppCategorizer.categorize("com.google.android.apps.youtube.music", "YouTube Music")
        )
        // The main YouTube app keeps its own category.
        assertEquals(
            AppCategory.SHORT_VIDEO,
            AppCategorizer.categorize("com.google.android.youtube", "YouTube")
        )
    }

    @Test
    fun spotify_isMusicByRule_notShoppingByAccident() {
        assertEquals(
            AppCategory.MUSIC,
            AppCategorizer.categorize("com.spotify.music", "Spotify")
        )
    }

    @Test
    fun playerLabelledApps_areMusic_notGames() {
        assertEquals(
            AppCategory.MUSIC,
            AppCategorizer.categorize("com.example.musicplayer", "Music Player")
        )
        assertEquals(
            AppCategory.MUSIC,
            AppCategorizer.categorize("com.example.podcasts", "Podcast Player")
        )
        assertEquals(
            AppCategory.MUSIC,
            AppCategorizer.categorize("au.com.shiftyjelly.pocketcasts", "Pocket Casts")
        )
    }

    @Test
    fun tiktok_isNotStolenByTheMusicToken() {
        // "musically" contains the substring "music"; token
        // matching must not reclassify TikTok as a music player.
        assertEquals(
            AppCategory.SHORT_VIDEO,
            AppCategorizer.categorize("com.zhiliaoapp.musically", "TikTok")
        )
    }

    @Test
    fun otherCategories_surviveTheMusicCheck() {
        assertEquals(
            AppCategory.MESSAGING,
            AppCategorizer.categorize("com.whatsapp", "WhatsApp")
        )
        assertEquals(
            AppCategory.BROWSERS,
            AppCategorizer.categorize("com.android.chrome", "Chrome")
        )
        assertEquals(
            AppCategory.GAMES,
            AppCategorizer.categorize("com.supercell.clashroyale", "Clash Royale")
        )
        assertEquals(
            AppCategory.SHOPPING,
            AppCategorizer.categorize("com.example.notes", "Notes")
        )
    }

    // -------------------------------------------------------------
    // F3 + F2 — a discovered player is born PEEK_2M, and PEEK is
    // exactly tier the existing peek mechanism honours: the
    // airlock offer exists on both lease-reading doors, and the
    // Safe Break / lease gates in both services key on PEEK_2M.
    // -------------------------------------------------------------

    private fun discovered(pkg: String, label: String): BlockedAppEntity {
        val plan = AppCatalogReconcile.reconcile(
            existing = emptyList(),
            installed = listOf(InstalledPackage(pkg, label)),
            usageMinutes = emptyMap(),
            categorize = AppCategorizer::categorize
        )
        assertEquals(1, plan.toInsert.size)
        return plan.toInsert.single()
    }

    @Test
    fun discoveredPlayers_areBornPeek_neverLocked() {
        for ((pkg, label) in listOf(
            "com.google.android.apps.youtube.music" to "YouTube Music",
            "com.spotify.music" to "Spotify",
            "com.example.musicplayer" to "Music Player"
        )) {
            val row = discovered(pkg, label)
            assertEquals(AppCategory.MUSIC.name, row.category)
            assertEquals("$pkg must be born PEEK_2M", AppBlockTier.PEEK_2M, row.resolvedTier)
        }
    }

    @Test
    fun bornPeekPlayer_getsTheExistingPeekOffer_onHonouringDoors() {
        val player = discovered("com.spotify.music", "Spotify")
        // The lawful start: a 2-minute peek to press play, after
        // which background playback (never blocked) continues.
        assertTrue(peekOfferFor(InterceptPath.APP_TIER, player, 0, false))
        assertTrue(peekOfferFor(InterceptPath.SHIELD_APP, player, 0, false))
        // And the LOCKED row it used to be born as still gets no
        // offer — LOCKED semantics are untouched for other apps.
        val locked = player.copy(blockTier = AppBlockTier.LOCKED.name, isBlocked = true)
        assertFalse(peekOfferFor(InterceptPath.APP_TIER, locked, 0, false))
    }

    // -------------------------------------------------------------
    // F1 — playback notifications are controls, not messages.
    // -------------------------------------------------------------

    @Test
    fun mediaPlaybackMarkers_areExempt() {
        assertTrue(isMediaPlaybackNotification("transport", false, null))
        assertTrue(isMediaPlaybackNotification(null, true, null))
        assertTrue(isMediaPlaybackNotification(null, false, MEDIA_STYLE_TEMPLATE))
        assertTrue(isMediaPlaybackNotification("transport", true, MEDIA_STYLE_TEMPLATE))
    }

    @Test
    fun ordinaryNotifications_areNotExempt() {
        // A message, and even a music app's non-playback post
        // (no transport category, no session, no MediaStyle),
        // stay held exactly as before.
        assertFalse(isMediaPlaybackNotification("msg", false, null))
        assertFalse(isMediaPlaybackNotification(null, false, null))
        assertFalse(isMediaPlaybackNotification("promo", false, "android.app.Notification\$BigTextStyle"))
        assertFalse(isMediaPlaybackNotification("call", false, null))
    }
}
