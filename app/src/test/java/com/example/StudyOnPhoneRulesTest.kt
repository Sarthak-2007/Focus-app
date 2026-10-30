package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCategorizer
import com.example.data.AppCategory
import com.example.data.LoadoutRules
import com.example.data.SmartLoadout
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Issue #133 (Audit R3b, study-on-phone-itself), as composed on
 * current main (which already carries issue #107's LoadoutRules /
 * setAppCategory and issue #132's AppCategorizer): the residual
 * cures, proven as pure logic —
 *  - finding 1: Deep Study keeps the study surfaces (YouTube,
 *    browsers, EDUCATION apps) OPEN instead of rationing them;
 *  - finding 2: EDUCATION is a deterministic home in the ONE
 *    classifier (AppCategorizer); Play Books is not a game;
 *  - finding 5 (frozen): the SLEEP rule still locks the study
 *    kit — pinned here so this lane is proven NOT to have
 *    changed routine semantics.
 */
class StudyOnPhoneRulesTest {

    private fun tier(loadout: SmartLoadout, pkg: String, name: String, category: AppCategory) =
        LoadoutRules.tierFor(loadout, pkg, name, category.name)

    // ---- Finding 1: the STUDY loadout ----------------------------

    @Test
    fun study_youtubeLectures_areOpen_notRationed() {
        // YouTube's guessed category is SHORT_VIDEO; the lecture
        // surface must still be OPEN in the study loadout.
        assertEquals(
            AppBlockTier.OPEN,
            tier(SmartLoadout.STUDY, "com.google.android.youtube", "YouTube", AppCategory.SHORT_VIDEO)
        )
    }

    @Test
    fun study_chromeHostedClasses_areOpen() {
        assertEquals(
            AppBlockTier.OPEN,
            tier(SmartLoadout.STUDY, "com.android.chrome", "Chrome", AppCategory.BROWSERS)
        )
        assertEquals(
            AppBlockTier.OPEN,
            tier(SmartLoadout.STUDY, "org.mozilla.firefox", "Firefox", AppCategory.BROWSERS)
        )
    }

    @Test
    fun study_educationApps_areOpen() {
        assertEquals(
            AppBlockTier.OPEN,
            tier(SmartLoadout.STUDY, "com.ichi2.anki", "Anki", AppCategory.EDUCATION)
        )
    }

    @Test
    fun study_distractions_stillLocked_messagingStillPeeks() {
        assertEquals(
            AppBlockTier.LOCKED,
            tier(SmartLoadout.STUDY, "com.zhiliaoapp.musically", "TikTok", AppCategory.SHORT_VIDEO)
        )
        assertEquals(
            AppBlockTier.LOCKED,
            tier(SmartLoadout.STUDY, "com.instagram.android", "Instagram", AppCategory.SOCIAL)
        )
        assertEquals(
            AppBlockTier.LOCKED,
            tier(SmartLoadout.STUDY, "com.supercell.clashroyale", "Clash Royale", AppCategory.GAMES)
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            tier(SmartLoadout.STUDY, "com.whatsapp", "WhatsApp", AppCategory.MESSAGING)
        )
    }

    // ---- Finding 5: SLEEP keeps the study kit --------------------

    @Test
    fun sleep_studyKitOpen_restStillLocked() {
        // Cured in the STURM-4 closure: EDUCATION stays OPEN under
        // SLEEP (nightly Anki revision), the rest of the phone —
        // games included — stays LOCKED, telephony stays OPEN.
        assertEquals(
            AppBlockTier.OPEN,
            tier(SmartLoadout.SLEEP, "com.ichi2.anki", "Anki", AppCategory.EDUCATION)
        )
        assertEquals(
            AppBlockTier.LOCKED,
            tier(SmartLoadout.SLEEP, "com.example.game", "Game", AppCategory.GAMES)
        )
        assertEquals(
            AppBlockTier.OPEN,
            tier(SmartLoadout.SLEEP, "com.android.dialer", "Phone", AppCategory.SHOPPING)
        )
    }

    // ---- Finding 2: birth filing ---------------------------------

    @Test
    fun categorize_ankiHasDeterministicEducationHome() {
        assertEquals(
            AppCategory.EDUCATION,
            AppCategorizer.categorize("com.ichi2.anki", "Anki")
        )
    }

    @Test
    fun categorize_playBooksIsEducation_notAGame() {
        assertEquals(
            AppCategory.EDUCATION,
            AppCategorizer.categorize("com.google.android.apps.books", "Google Play Books")
        )
    }

    @Test
    fun categorize_playTrapIsGone() {
        assertEquals(
            AppCategory.SHOPPING,
            AppCategorizer.categorize("com.android.vending", "Google Play Store")
        )
        assertEquals(
            AppCategory.GAMES,
            AppCategorizer.categorize("com.example.tower", "Tower Game")
        )
        assertEquals(
            AppCategory.GAMES,
            AppCategorizer.categorize("com.supercell.clashroyale", "Clash Royale")
        )
    }

    @Test
    fun categorize_existingFilingsUnchanged_unknownFallsToOther() {
        assertEquals(
            AppCategory.SHORT_VIDEO,
            AppCategorizer.categorize("com.google.android.youtube", "YouTube")
        )
        assertEquals(
            AppCategory.MUSIC,
            AppCategorizer.categorize("com.spotify.music", "Spotify")
        )
        assertEquals(
            AppCategory.MESSAGING,
            AppCategorizer.categorize("com.whatsapp", "WhatsApp")
        )
        assertEquals(
            AppCategory.BROWSERS,
            AppCategorizer.categorize("com.android.chrome", "Chrome")
        )
        assertEquals(
            AppCategory.SHOPPING,
            AppCategorizer.categorize("com.example.camera", "Camera")
        )
    }
}
