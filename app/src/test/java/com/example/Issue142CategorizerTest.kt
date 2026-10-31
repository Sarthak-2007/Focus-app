package com.example

import com.example.data.AppCategorizer
import com.example.data.AppCategory
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Issue #142 (finding-an-app-in-shield, category finding): the
 * classifier's token discipline and its widened field. Substring
 * collisions must NOT misfile ("Freelance" is not short video,
 * "Display" is not games, YouTube Music is music), and the
 * browsers/social/messaging field reaches past the first four
 * brands so the long tail stops dumping into "Other Apps".
 */
class Issue142CategorizerTest {

    @Test
    fun substringCollisions_doNotMisfile() {
        assertEquals(
            AppCategory.SHOPPING,
            AppCategorizer.categorize("com.freelance.app", "Freelance")
        )
        assertEquals(
            AppCategory.SHOPPING,
            AppCategorizer.categorize("com.example.displaytuner", "Display Tuner")
        )
        assertEquals(
            AppCategory.MUSIC,
            AppCategorizer.categorize("com.google.android.apps.youtube.music", "YouTube Music")
        )
    }

    @Test
    fun widenedField_filesPastFourBrands() {
        assertEquals(
            AppCategory.BROWSERS,
            AppCategorizer.categorize("com.opera.browser", "Opera")
        )
        assertEquals(
            AppCategory.BROWSERS,
            AppCategorizer.categorize("com.duckduckgo.mobile.android", "DuckDuckGo")
        )
        assertEquals(
            AppCategory.BROWSERS,
            AppCategorizer.categorize("com.example.dailynews", "Daily News")
        )
        assertEquals(
            AppCategory.SOCIAL,
            AppCategorizer.categorize("com.instagram.barcelona", "Threads")
        )
        assertEquals(
            AppCategory.MESSAGING,
            AppCategorizer.categorize("org.thoughtcrime.securesms", "Signal")
        )
    }

    @Test
    fun playGames_stillFilesGames() {
        assertEquals(
            AppCategory.GAMES,
            AppCategorizer.categorize("com.google.android.play.games", "Play Games")
        )
    }
}
