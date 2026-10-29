package com.example

import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.ui.components.launcherPartition
import com.example.ui.components.launcherProgress
import com.example.ui.components.launcherRemainingPrint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * UMBAU's launcher partition contract (LauncherBlockScreen.kt,
 * com.example.ui.components): the block screen is a launcher, so
 * its three shelves — allowed (OPEN), peek (PEEK_2M) and shut
 * (LOCKED) — are a pure partition of the app list by each entity's
 * resolvedTier, each shelf sorted by display name (appName),
 * case-insensitive. Plus the two desk-clock derivations the
 * launcher prints: progress as a clamped fraction and the
 * remaining time in the app's one printed form.
 *
 * Entities are built with BlockedAppEntity's real constructor
 * (MonolithModels.kt): packageName / appName / category /
 * isBlocked have no defaults and are always supplied; blockTier
 * defaults from isBlocked when omitted.
 */
class LauncherPartitionTest {

    private fun app(
        name: String,
        blockTier: String,
        isBlocked: Boolean,
        packageName: String = "com.test." + name.lowercase().replace(" ", "_")
    ): BlockedAppEntity = BlockedAppEntity(
        packageName = packageName,
        appName = name,
        category = "Social",
        isBlocked = isBlocked,
        blockTier = blockTier
    )

    private fun open(name: String): BlockedAppEntity =
        app(name, AppBlockTier.OPEN.name, isBlocked = false)

    private fun peek(name: String): BlockedAppEntity =
        app(name, AppBlockTier.PEEK_2M.name, isBlocked = true)

    private fun shut(name: String): BlockedAppEntity =
        app(name, AppBlockTier.LOCKED.name, isBlocked = true)

    private fun names(apps: List<BlockedAppEntity>): List<String> = apps.map { it.appName }

    // ------------------------------------------------------------------
    // launcherPartition — tier routing
    // ------------------------------------------------------------------

    @Test
    fun partition_routesEachTierToItsOwnShelf() {
        val partition = launcherPartition(
            listOf(open("Notes"), peek("Messages"), shut("Instagram"))
        )
        assertEquals(listOf("Notes"), names(partition.allowed))
        assertEquals(listOf("Messages"), names(partition.peek))
        assertEquals(listOf("Instagram"), names(partition.shut))
    }

    @Test
    fun partition_isCompleteAndDisjoint_nothingLostNothingDoubled() {
        val apps = listOf(
            open("Notes"), open("Calculator"),
            peek("Messages"), peek("Mail"),
            shut("Instagram"), shut("Chess"), shut("News")
        )
        val partition = launcherPartition(apps)
        assertEquals(2, partition.allowed.size)
        assertEquals(2, partition.peek.size)
        assertEquals(3, partition.shut.size)
        val reunited = partition.allowed + partition.peek + partition.shut
        assertEquals(apps.toSet(), reunited.toSet())
        assertEquals(apps.size, reunited.size)
    }

    @Test
    fun partition_allInOneTier_leavesTheOtherShelvesEmpty() {
        val partition = launcherPartition(listOf(shut("Instagram"), shut("Chess")))
        assertTrue(partition.allowed.isEmpty())
        assertTrue(partition.peek.isEmpty())
        assertEquals(2, partition.shut.size)
    }

    @Test
    fun partition_emptyList_givesThreeEmptyShelves() {
        val partition = launcherPartition(emptyList())
        assertTrue(partition.allowed.isEmpty())
        assertTrue(partition.peek.isEmpty())
        assertTrue(partition.shut.isEmpty())
    }

    @Test
    fun partition_followsResolvedTier_notTheRawIsBlockedFlag() {
        // A stored OPEN tier wins even on a row whose isBlocked flag
        // is still set, and a stored LOCKED tier wins on a row whose
        // flag is clear: resolvedTier is the one source of truth.
        val partition = launcherPartition(
            listOf(
                app("FlaggedButOpen", AppBlockTier.OPEN.name, isBlocked = true),
                app("UnflaggedButShut", AppBlockTier.LOCKED.name, isBlocked = false),
                app("UnflaggedButPeek", AppBlockTier.PEEK_2M.name, isBlocked = false)
            )
        )
        assertEquals(listOf("FlaggedButOpen"), names(partition.allowed))
        assertEquals(listOf("UnflaggedButPeek"), names(partition.peek))
        assertEquals(listOf("UnflaggedButShut"), names(partition.shut))
    }

    @Test
    fun partition_omittedBlockTier_defaultsFromIsBlocked() {
        // Constructor default: no blockTier stored -> LOCKED when
        // blocked, OPEN when not. The partition must honour it.
        val blocked = BlockedAppEntity(
            packageName = "com.test.defaulted_shut",
            appName = "Defaulted Shut",
            category = "Social",
            isBlocked = true
        )
        val unblocked = BlockedAppEntity(
            packageName = "com.test.defaulted_open",
            appName = "Defaulted Open",
            category = "Social",
            isBlocked = false
        )
        val partition = launcherPartition(listOf(blocked, unblocked))
        assertEquals(listOf("Defaulted Open"), names(partition.allowed))
        assertEquals(listOf("Defaulted Shut"), names(partition.shut))
        assertTrue(partition.peek.isEmpty())
    }

    // ------------------------------------------------------------------
    // launcherPartition — garbage stored tier (resolvedTier fallback)
    // ------------------------------------------------------------------

    @Test
    fun partition_garbageTierOnBlockedRow_fallsBackToShut() {
        val partition = launcherPartition(
            listOf(app("Mystery", "NOT_A_TIER", isBlocked = true))
        )
        assertEquals(listOf("Mystery"), names(partition.shut))
        assertTrue(partition.allowed.isEmpty())
        assertTrue(partition.peek.isEmpty())
    }

    @Test
    fun partition_garbageTierOnUnblockedRow_fallsBackToAllowed() {
        val partition = launcherPartition(
            listOf(app("Mystery", "NOT_A_TIER", isBlocked = false))
        )
        assertEquals(listOf("Mystery"), names(partition.allowed))
        assertTrue(partition.peek.isEmpty())
        assertTrue(partition.shut.isEmpty())
    }

    @Test
    fun partition_everyGarbageSpelling_fallsBackNeverToPeek() {
        // valueOf is exact: empty, lower-case, truncated and padded
        // spellings are all garbage, so each row lands by its
        // isBlocked fallback — and never accidentally on peek.
        val garbage = listOf("", "locked", "peek_2m", "PEEK", " OPEN ", "OPEN!", "2M")
        val blockedRows = garbage.mapIndexed { i, tier ->
            app("Blocked$i", tier, isBlocked = true, packageName = "com.test.garb_b$i")
        }
        val unblockedRows = garbage.mapIndexed { i, tier ->
            app("Unblocked$i", tier, isBlocked = false, packageName = "com.test.garb_u$i")
        }
        val partition = launcherPartition(blockedRows + unblockedRows)
        assertEquals(garbage.size, partition.shut.size)
        assertEquals(garbage.size, partition.allowed.size)
        assertTrue(partition.peek.isEmpty())
    }

    // ------------------------------------------------------------------
    // launcherPartition — sorting (by appName, case-insensitive)
    // ------------------------------------------------------------------

    @Test
    fun partition_sortsEachShelfByAppName() {
        val partition = launcherPartition(
            listOf(
                shut("YouTube"), shut("Chess"), shut("Instagram"),
                open("Notes"), open("Alarm"), open("Maps"),
                peek("WhatsApp"), peek("Gmail"), peek("Messages")
            )
        )
        assertEquals(listOf("Alarm", "Maps", "Notes"), names(partition.allowed))
        assertEquals(listOf("Gmail", "Messages", "WhatsApp"), names(partition.peek))
        assertEquals(listOf("Chess", "Instagram", "YouTube"), names(partition.shut))
    }

    @Test
    fun partition_sortIsCaseInsensitive() {
        // Case-sensitive ordering would put every capitalised name
        // before "banana"; the contract is case-insensitive.
        val partition = launcherPartition(
            listOf(open("banana"), open("Apple"), open("cherry"), open("Apricot"))
        )
        assertEquals(listOf("Apple", "Apricot", "banana", "cherry"), names(partition.allowed))
    }

    @Test
    fun partition_sortIgnoresInputOrder() {
        val forward = listOf(peek("WhatsApp"), peek("Gmail"), peek("Messages"))
        val partitionForward = launcherPartition(forward)
        val partitionReverse = launcherPartition(forward.reversed())
        assertEquals(names(partitionForward.peek), names(partitionReverse.peek))
        assertEquals(listOf("Gmail", "Messages", "WhatsApp"), names(partitionReverse.peek))
    }

    @Test
    fun partition_sortUsesAppName_notPackageName() {
        // Package order and display-name order disagree here on
        // purpose: the shelf must follow the name a person reads.
        val partition = launcherPartition(
            listOf(
                app("Zebra", AppBlockTier.OPEN.name, isBlocked = false, packageName = "com.aaa.zebra"),
                app("Alpha", AppBlockTier.OPEN.name, isBlocked = false, packageName = "com.zzz.alpha")
            )
        )
        assertEquals(listOf("Alpha", "Zebra"), names(partition.allowed))
    }

    @Test
    fun partition_doesNotReorderTheCallersList() {
        val apps = listOf(shut("YouTube"), open("Alarm"), peek("Gmail"))
        val before = apps.toList()
        launcherPartition(apps)
        assertEquals(before, apps)
    }

    // ------------------------------------------------------------------
    // launcherProgress — elapsed / target, clamped to [0f, 1f]
    // ------------------------------------------------------------------

    @Test
    fun progress_zeroElapsed_isZero() {
        assertEquals(0f, launcherProgress(0, 3600), 0.001f)
        assertEquals(0f, launcherProgress(0, 1), 0.001f)
    }

    @Test
    fun progress_partialElapsed_isThePlainFraction() {
        assertEquals(0.5f, launcherProgress(1800, 3600), 0.001f)
        assertEquals(0.25f, launcherProgress(900, 3600), 0.001f)
        assertEquals(0.75f, launcherProgress(2700, 3600), 0.001f)
        assertEquals(1f / 3f, launcherProgress(1, 3), 0.001f)
    }

    @Test
    fun progress_exactlyAtTarget_isOne() {
        assertEquals(1f, launcherProgress(3600, 3600), 0.001f)
        assertEquals(1f, launcherProgress(1, 1), 0.001f)
    }

    @Test
    fun progress_overTarget_clampsToOne() {
        assertEquals(1f, launcherProgress(3601, 3600), 0.001f)
        assertEquals(1f, launcherProgress(7200, 3600), 0.001f)
        assertEquals(1f, launcherProgress(Int.MAX_VALUE, 3600), 0.001f)
    }

    @Test
    fun progress_negativeElapsed_clampsToZero() {
        assertEquals(0f, launcherProgress(-1, 3600), 0.001f)
        assertEquals(0f, launcherProgress(-3600, 3600), 0.001f)
        assertEquals(0f, launcherProgress(Int.MIN_VALUE, 3600), 0.001f)
    }

    @Test
    fun progress_zeroTarget_isZeroWhateverElapsed() {
        assertEquals(0f, launcherProgress(0, 0), 0.001f)
        assertEquals(0f, launcherProgress(600, 0), 0.001f)
        assertEquals(0f, launcherProgress(-600, 0), 0.001f)
    }

    @Test
    fun progress_negativeTarget_isZeroWhateverElapsed() {
        assertEquals(0f, launcherProgress(0, -3600), 0.001f)
        assertEquals(0f, launcherProgress(600, -3600), 0.001f)
        assertEquals(0f, launcherProgress(600, Int.MIN_VALUE), 0.001f)
    }

    // ------------------------------------------------------------------
    // launcherRemainingPrint — negative "00:00", MM:SS, then H:MM:SS
    // ------------------------------------------------------------------

    @Test
    fun remaining_zero_printsDoubleZero() {
        assertEquals("00:00", launcherRemainingPrint(0))
    }

    @Test
    fun remaining_negative_printsDoubleZeroNeverASign() {
        assertEquals("00:00", launcherRemainingPrint(-1))
        assertEquals("00:00", launcherRemainingPrint(-59))
        assertEquals("00:00", launcherRemainingPrint(-3600))
        assertEquals("00:00", launcherRemainingPrint(Int.MIN_VALUE))
    }

    @Test
    fun remaining_underAMinute_isZeroPaddedSeconds() {
        assertEquals("00:01", launcherRemainingPrint(1))
        assertEquals("00:09", launcherRemainingPrint(9))
        assertEquals("00:59", launcherRemainingPrint(59))
    }

    @Test
    fun remaining_atAndPastAMinute_rollsToMinutes() {
        assertEquals("01:00", launcherRemainingPrint(60))
        assertEquals("01:01", launcherRemainingPrint(61))
        assertEquals("09:59", launcherRemainingPrint(599))
        assertEquals("10:00", launcherRemainingPrint(600))
    }

    @Test
    fun remaining_justUnderAnHour_staysMinutesSeconds() {
        assertEquals("59:00", launcherRemainingPrint(3540))
        assertEquals("59:59", launcherRemainingPrint(3599))
    }

    @Test
    fun remaining_atAnHour_switchesToHoursMinutesSeconds() {
        assertEquals("1:00:00", launcherRemainingPrint(3600))
    }

    @Test
    fun remaining_pastAnHour_padsMinutesAndSecondsButNotTheHour() {
        assertEquals("1:01:01", launcherRemainingPrint(3661))
        assertEquals("1:00:09", launcherRemainingPrint(3609))
        assertEquals("2:02:05", launcherRemainingPrint(7325))
        assertEquals("10:00:00", launcherRemainingPrint(36000))
        assertEquals("12:34:56", launcherRemainingPrint(45296))
    }
}
