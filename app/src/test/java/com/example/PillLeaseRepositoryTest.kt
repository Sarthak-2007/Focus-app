package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.BlockedAppEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The peek lease's write side, at the repository (PILL-AND-TIMES
 * §2.1, §4.4's lease cases at today's defaults):
 *
 *  1. A grant stamps the end from the single read-point's length
 *     (today the shipped 120 s) and pre-arms the cooldown anchor
 *     to the same instant.
 *  2. A grant zeroes every OTHER row's lease — storage can never
 *     hold two live fuses — while leaving the zeroed row's earned
 *     cooldown anchor standing.
 *  3. The hand-back (recordPeekEnded — the pill ✕'s verb, and
 *     Route B's END PEEK action's verb) zeroes the lease and
 *     re-anchors the cooldown to NOW, not to the granted end.
 *
 * Packages are unique per test: the sandbox database is shared
 * by the whole class.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PillLeaseRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun repository(): MonolithRepository {
        val db = MonolithDatabase.getInstance(context)
        return MonolithRepository(context, db.monolithDao())
    }

    private fun app(pkg: String) = BlockedAppEntity(
        packageName = pkg,
        appName = pkg.substringAfterLast('.').uppercase(),
        category = "MESSAGING",
        isBlocked = true,
        blockTier = "PEEK_2M"
    )

    @Test
    fun grantStampsEndAndPreArmsCooldown() = runBlocking {
        val repo = repository()
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        dao.insertApp(app("com.pillg.grant"))

        repo.startPeekLease("com.pillg.grant")

        val row = dao.getAllAppsSnapshot().first { it.packageName == "com.pillg.grant" }
        val now = SystemClock.elapsedRealtime()
        val remainingMs = row.activePeekUntilElapsedRealtime - now
        assertTrue(
            "lease end is ~120 s out (was ${remainingMs}ms)",
            remainingMs in 100_000L..120_000L
        )
        assertEquals(
            "cooldown anchor pre-armed to the lease's own end",
            row.activePeekUntilElapsedRealtime,
            row.lastPeekEndElapsedRealtime
        )
    }

    @Test
    fun grantZeroesEveryOtherRowsLease() = runBlocking {
        val repo = repository()
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        dao.insertApps(
            listOf(
                app("com.pillz.first"),
                app("com.pillz.second"),
                app("com.pillz.bystander")
            )
        )

        repo.startPeekLease("com.pillz.first")
        val firstGrant = dao.getAllAppsSnapshot()
            .first { it.packageName == "com.pillz.first" }
        assertTrue(firstGrant.activePeekUntilElapsedRealtime > 0L)

        repo.startPeekLease("com.pillz.second")

        val rows = dao.getAllAppsSnapshot()
            .filter { it.packageName.startsWith("com.pillz.") }
            .associateBy { it.packageName }
        assertEquals(
            "the earlier lease is zeroed by the later grant",
            0L,
            rows.getValue("com.pillz.first").activePeekUntilElapsedRealtime
        )
        assertEquals(
            "…but its earned cooldown anchor stands",
            firstGrant.lastPeekEndElapsedRealtime,
            rows.getValue("com.pillz.first").lastPeekEndElapsedRealtime
        )
        assertTrue(
            rows.getValue("com.pillz.second").activePeekUntilElapsedRealtime > 0L
        )
        assertEquals(
            0L,
            rows.getValue("com.pillz.bystander").activePeekUntilElapsedRealtime
        )
        val liveCount = rows.values.count { it.activePeekUntilElapsedRealtime > 0L }
        assertEquals("at most one live lease in storage", 1, liveCount)
    }

    @Test
    fun handBackZeroesTheLeaseAndAnchorsCooldownToNow() = runBlocking {
        val repo = repository()
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        dao.insertApp(app("com.pillh.handback"))

        repo.startPeekLease("com.pillh.handback")
        val granted = dao.getAllAppsSnapshot()
            .first { it.packageName == "com.pillh.handback" }
        assertTrue(granted.activePeekUntilElapsedRealtime > SystemClock.elapsedRealtime())

        val beforeEnd = SystemClock.elapsedRealtime()
        repo.recordPeekEnded("com.pillh.handback")
        val afterEnd = SystemClock.elapsedRealtime()

        val row = dao.getAllAppsSnapshot()
            .first { it.packageName == "com.pillh.handback" }
        assertEquals("lease zeroed", 0L, row.activePeekUntilElapsedRealtime)
        assertTrue(
            "cooldown re-anchored to the hand-back instant, not the granted end",
            row.lastPeekEndElapsedRealtime in beforeEnd..afterEnd &&
                row.lastPeekEndElapsedRealtime != granted.activePeekUntilElapsedRealtime
        )
    }
}
