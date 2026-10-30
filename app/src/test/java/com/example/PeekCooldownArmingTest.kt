package com.example

import android.content.Context
import android.os.SystemClock
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #118, finding 4: the 20-minute peek cooldown is armed at
 * the peek's END, not pre-stamped at its start.
 *
 * - [MonolithRepository.startPeekLease] must NOT write
 *   `lastPeekEndElapsedRealtime`: the recorded end is stamped only
 *   by `recordPeekEnded`, when the peek actually ends.
 * - The anti-abuse limit must NOT weaken: while a lease is live
 *   (or lapses unrecorded), the cooldown still reads in full,
 *   anchored on the lease end — killing the app mid-peek buys
 *   nothing.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PeekCooldownArmingTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private lateinit var repo: MonolithRepository

    private val pkg = "com.whatsapp"

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = MonolithRepository(context, db.monolithDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    private suspend fun seedApp() {
        db.monolithDao().insertApps(
            listOf(
                BlockedAppEntity(
                    packageName = pkg,
                    appName = "WhatsApp",
                    category = "MESSAGING",
                    isBlocked = true,
                    blockTier = AppBlockTier.PEEK_2M.name
                )
            )
        )
    }

    private suspend fun row() =
        db.monolithDao().getAllAppsSnapshot().first { it.packageName == pkg }

    @Test
    fun startPeekLease_doesNotPreStampTheCooldownAnchor() = runBlocking {
        seedApp()
        assertEquals(0L, row().lastPeekEndElapsedRealtime)

        repo.startPeekLease(pkg)

        val during = row()
        assertTrue("lease end must be written", during.activePeekUntilElapsedRealtime > 0L)
        assertEquals(
            "cooldown anchor must stay unstamped until the peek ends",
            0L, during.lastPeekEndElapsedRealtime
        )
    }

    @Test
    fun liveLease_stillReadsFullCooldown_noLoophole() = runBlocking {
        seedApp()
        repo.startPeekLease(pkg)

        // Even with the anchor unstamped, a re-peek inside the
        // window is refused in full: the reader anchors on the
        // lease end as the fail-safe.
        val remaining = repo.getPeekCooldownRemainingSeconds(pkg)
        assertTrue("cooldown during lease must read ~1200s, was $remaining", remaining in 1190..1200)
    }

    @Test
    fun recordPeekEnded_armsCooldownFromTheActualEnd() = runBlocking {
        seedApp()
        repo.startPeekLease(pkg)
        repo.recordPeekEnded(pkg)

        val after = row()
        val nowMono = SystemClock.elapsedRealtime()
        assertTrue("lease must be cleared", after.activePeekUntilElapsedRealtime == 0L)
        assertTrue(
            "anchor must be the moment the peek ended",
            after.lastPeekEndElapsedRealtime in (nowMono - 5_000L)..nowMono
        )
        val remaining = repo.getPeekCooldownRemainingSeconds(pkg)
        assertTrue("cooldown after end must read ~1200s, was $remaining", remaining in 1190..1200)
    }
}
