package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.OpenExposure
import com.example.data.SessionOutcome
import com.example.data.finishKindOf
import com.example.data.finishPulses
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #129 F3: the ending write's honesty test, proven pure —
 * an unguarded distraction app actually used inside the window
 * is exposure; held apps, ruled-open utilities, system surfaces
 * and tap-throughs are not.
 */
class OpenExposureTest {

    private fun row(pkg: String, name: String, category: AppCategory, tier: AppBlockTier) =
        BlockedAppEntity(
            packageName = pkg,
            appName = name,
            category = category.name,
            isBlocked = tier != AppBlockTier.OPEN,
            blockTier = tier.name,
            dailyLimitMinutes = 30,
            todayUsageMinutes = 0,
            interceptCount = 0
        )

    private val apps = listOf(
        row("com.news.app", "News App", AppCategory.BROWSERS, AppBlockTier.OPEN),
        row("com.unknown.game", "Unknown Game", AppCategory.SHOPPING, AppBlockTier.OPEN),
        row("com.docs", "Docs", AppCategory.SHOPPING, AppBlockTier.OPEN),
        row("com.locked.game", "Locked Game", AppCategory.GAMES, AppBlockTier.LOCKED),
        row("com.peek.chat", "Chat", AppCategory.MESSAGING, AppBlockTier.PEEK_2M)
    )

    @Test
    fun openDistractionCategoryUsedInWindow_isExposure() {
        val exposed = OpenExposure.exposedApps(
            foregroundMillisByPackage = mapOf("com.news.app" to 120_000L),
            apps = apps,
            unreviewedOpenPackages = emptySet(),
            ownPackage = "com.example"
        )
        assertEquals(listOf("com.news.app"), exposed.map { it.packageName })
    }

    @Test
    fun noRowAppUsedInWindow_isExposure_andMisfiledUnreviewedToo() {
        val exposed = OpenExposure.exposedApps(
            foregroundMillisByPackage = mapOf(
                "com.brand.new" to 90_000L,
                "com.unknown.game" to 60_000L
            ),
            apps = apps,
            unreviewedOpenPackages = setOf("com.unknown.game"),
            ownPackage = "com.example"
        )
        assertEquals(
            listOf("com.brand.new", "com.unknown.game"),
            exposed.map { it.packageName }
        )
    }

    @Test
    fun held_ruledOpenUtility_systemSurface_andTapThrough_areNotExposure() {
        val exposed = OpenExposure.exposedApps(
            foregroundMillisByPackage = mapOf(
                "com.locked.game" to 300_000L,   // held — cannot be exposure
                "com.peek.chat" to 300_000L,     // peek tier — held world
                "com.docs" to 300_000L,          // ruled OPEN utility (SHOPPING, reviewed)
                "com.android.systemui" to 300_000L,
                "com.google.android.dialer" to 300_000L,
                "com.example" to 300_000L,       // Monolith itself
                "com.news.app" to 5_000L         // tap-through, under the floor
            ),
            apps = apps,
            unreviewedOpenPackages = emptySet(),
            ownPackage = "com.example"
        )
        assertTrue(exposed.isEmpty())
    }

    @Test
    fun unreviewedShoppingAppWithoutRecord_isNotExposure() {
        // SHOPPING + OPEN alone is not enough — without the
        // unreviewed record (F1) the app is presumed ruled.
        val exposed = OpenExposure.exposedApps(
            foregroundMillisByPackage = mapOf("com.unknown.game" to 300_000L),
            apps = apps,
            unreviewedOpenPackages = emptySet(),
            ownPackage = "com.example"
        )
        assertTrue(exposed.isEmpty())
    }

    @Test
    fun exposedOutcomeIsAPublishableFinishThatNeverPulses() {
        val row = com.example.data.FocusSessionEntity(
            intention = "Study",
            profileName = "Study Loadout",
            targetDurationSeconds = 3000,
            elapsedSeconds = 3000,
            startWallTimeMillis = 1_000L,
            startElapsedRealtimeMillis = 1_000L,
            isActive = false,
            completedSuccessfully = false,
            outcome = SessionOutcome.ELAPSED_OPEN_EXPOSURE
        )
        assertEquals(
            com.example.data.FinishKind.MANUAL_FINISH,
            finishKindOf(row, isSleepLoadout = false, nowWallMillis = 10_000L)
        )
        assertFalse(finishPulses(com.example.data.FinishKind.MANUAL_FINISH, row))
    }
}
