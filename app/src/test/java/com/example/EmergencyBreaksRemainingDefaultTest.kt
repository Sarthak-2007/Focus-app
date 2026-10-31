package com.example

import android.content.Context
import android.os.SystemClock
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.EmergencyBreakSpendEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.MonolithWeek
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * Issue #82 residual (STURM3-16 tightening 2): the DEFAULT argument
 * of MonolithRepository.emergencyBreaksRemaining must be the honest
 * now, never the raw wall clock. Before the tightening the default
 * was `System.currentTimeMillis()`, so any caller that forgot the
 * argument silently reopened the F3 clock route on this surface.
 *
 * Proof shape: book this (raw-wall) week's token as spent, then pin
 * the persisted clock anchor one week in the past with a live
 * monotonic pair, so the honest now sits in LAST week while the raw
 * wall clock sits in this one. An explicit raw instant still reads
 * this week (0 remaining — explicit instants unchanged), while the
 * no-argument call must read the honest week (1 remaining). On the
 * old raw default the no-argument call returned 0 and this test
 * failed.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EmergencyBreaksRemainingDefaultTest {

    private lateinit var context: Context
    private lateinit var db: MonolithDatabase

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun defaultArgumentResolvesToHonestNowNotRawWallClock() = runBlocking {
        val dao = db.monolithDao()
        val repo = MonolithRepository(context, dao)

        // Give the monotonic clock a positive, stable reading so the
        // seeded anchor pair below is usable by decide().
        ShadowSystemClock.advanceBy(Duration.ofMinutes(5))
        val rawNow = System.currentTimeMillis()
        val rawWeekAnchor = MonolithWeek.weekAnchorMillis(rawNow)

        // This (raw-wall) week's token is spent.
        dao.insertEmergencyBreakSpend(
            EmergencyBreakSpendEntity(
                sessionId = 991,
                spentAtWallMillis = rawNow,
                weekAnchorMillis = rawWeekAnchor,
                heldSeconds = 60
            )
        )

        // Explicit instant: honoured as-is — the raw week reads spent.
        assertEquals(0, repo.emergencyBreaksRemaining(rawNow))

        // Pin the persisted anchor one week back, paired with the
        // current monotonic reading and the current boot count: the
        // wall clock has (in honest terms) travelled +7 days, so the
        // honest now is the anchor wall plus monotonic delta only —
        // last week, where nothing is spent.
        val nowMono = SystemClock.elapsedRealtime()
        assertTrue("monotonic clock must be live for this proof", nowMono > 0L)
        context.getSharedPreferences(MonolithRepository.CLOCK_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(
                MonolithRepository.CLOCK_ANCHOR_WALL_KEY,
                rawNow - 7 * 24 * 3600_000L
            )
            .putLong(MonolithRepository.CLOCK_ANCHOR_MONO_KEY, nowMono)
            .putInt(MonolithRepository.CLOCK_ANCHOR_BOOT_KEY, repo.querySystemBootCount())
            .commit()

        // Sanity: the honest now really is in a different week.
        assertTrue(
            MonolithWeek.weekAnchorMillis(repo.honestNowMillis()) != rawWeekAnchor
        )

        // The default argument: honest week — the token is intact.
        // (On the old raw-wall default this returned 0.)
        assertEquals(1, repo.emergencyBreaksRemaining())

        // And the explicit instant still reads the raw week as spent.
        assertEquals(0, repo.emergencyBreaksRemaining(rawNow))
    }
}
