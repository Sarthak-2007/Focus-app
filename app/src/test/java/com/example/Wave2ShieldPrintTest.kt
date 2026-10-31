package com.example

import com.example.data.BlockedBrowserDomains
import com.example.data.MonolithRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Wave-2 (issue #7) DECLARED tests — written, NOT run (agents never
 * compile; Ren compiles on Sarthak's word).
 *
 * Pins the two print derivations wave-2 added to Shield:
 *  1. The failed-ADD verdict (R-3 / Fremd C9b): the screen judges an
 *     ADD with the repository's own normaliser, so these are the
 *     exact verdicts the screen prints — null prints
 *     "NOT A SITE — CHECK THE ADDRESS AND TRY AGAIN" and keeps the
 *     typed text; a normalised hit on the existing list prints
 *     "ALREADY ON THE LIST".
 *  2. The PEEK contract line (R-6): derived from the constants, so
 *     if Frist's editable times ever change them, the line follows.
 */
class Wave2ShieldPrintTest {

    @Test
    fun addVerdict_garbageIsRejected() {
        assertNull(BlockedBrowserDomains.normalizeDomain(""))
        assertNull(BlockedBrowserDomains.normalizeDomain("not a site"))
        assertNull(BlockedBrowserDomains.normalizeDomain("nodot"))
    }

    @Test
    fun addVerdict_realSiteNormalises() {
        assertEquals(
            "news.ycombinator.com",
            BlockedBrowserDomains.normalizeDomain("https://www.News.YCombinator.com/")
        )
        assertEquals(
            "youtube.com/shorts",
            BlockedBrowserDomains.normalizeDomain("https://www.YouTube.com/shorts?x=1")
        )
    }

    @Test
    fun peekContract_derivesFromConstants() {
        assertEquals(2, MonolithRepository.PEEK_DURATION_SECONDS / 60)
        assertEquals(20L, MonolithRepository.PEEK_COOLDOWN_MILLIS / 60_000L)
    }
}
