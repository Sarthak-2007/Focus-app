package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #47 (heartbeat-retry F1/F5) — the retry scheduler's DAO
 * surface against the real database:
 *  1. getFaultedReleaseSession returns the NEWEST faulted row only
 *     (the scheduler's old read loaded the entire history to find it).
 *  2. clearReleaseFault ends a row's cadence without erasing its
 *     fault stamps — a healed fault can never be picked up again.
 *  3. countDeriveSessionInputs counts exactly the union of the two
 *     derive() session reads, so the heartbeat's zero-probe is a
 *     proof, not a guess.
 *
 * One sequential story: the sandbox database is shared by the
 * whole class, so counts are asserted relative to a baseline.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ReleaseRetryDaoTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun row(
        start: Long,
        faulted: Boolean = false,
        faultedAt: Long = 0L,
        routineId: Int? = null,
        windowEnd: Long = 0L,
        routineOutcome: String? = null
    ) = FocusSessionEntity(
        intention = "Deep Study",
        profileName = "Default",
        targetDurationSeconds = 3000,
        elapsedSeconds = 3000,
        startWallTimeMillis = start,
        startElapsedRealtimeMillis = 0L,
        isActive = false,
        completedSuccessfully = true,
        releaseFaultShield = faulted,
        releaseFaultedAtWallMillis = faultedAt,
        routineId = routineId,
        windowEndMillis = windowEnd,
        routineOutcome = routineOutcome
    )

    @Test
    fun faultedReadClearAndDeriveProbeStory() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val since = 10_000_000L
        val base = dao.countDeriveSessionInputs(since)

        // A plain old manual row is invisible to every new read.
        dao.insertSession(row(start = 1_000L))
        assertNull(dao.getFaultedReleaseSession()?.takeIf { it.startWallTimeMillis <= 2_000L })
        assertEquals(base, dao.countDeriveSessionInputs(since))

        // Two faulted rows: the targeted read returns the newest.
        val olderId = dao.insertSession(row(start = 2_000_000L, faulted = true, faultedAt = 2_100_000L)).toInt()
        val newerId = dao.insertSession(row(start = 3_000_000L, faulted = true, faultedAt = 3_100_000L)).toInt()
        val faulted = dao.getFaultedReleaseSession()
        assertNotNull(faulted)
        assertEquals(newerId, faulted!!.id)

        // Clearing the newest exposes the older; clearing that ends
        // the cadence for good. Stamps survive the clear (history).
        dao.clearReleaseFault(newerId)
        assertEquals(olderId, dao.getFaultedReleaseSession()!!.id)
        dao.clearReleaseFault(olderId)
        assertNull(dao.getFaultedReleaseSession())
        val clearedRow = dao.getSessionById(olderId)!!
        assertEquals(false, clearedRow.releaseFaultShield)
        assertEquals(2_100_000L, clearedRow.releaseFaultedAtWallMillis)

        // The derive probe counts the union exactly:
        //  +1 a session started since the lookback edge,
        dao.insertSession(row(start = since + 1L))
        assertEquals(base + 1, dao.countDeriveSessionInputs(since))
        //  +1 a finalised routine session whose window ended inside
        //    the lookback (routine read), even started before it,
        dao.insertSession(
            row(start = 500L, routineId = 7, windowEnd = since + 5L, routineOutcome = "ENDED")
        )
        assertEquals(base + 2, dao.countDeriveSessionInputs(since))
        //  +1 an UNFINALISED routine session of any age,
        dao.insertSession(row(start = 600L, routineId = 8, windowEnd = 700L, routineOutcome = null))
        assertEquals(base + 3, dao.countDeriveSessionInputs(since))
        //  +0 a finalised routine session whose window closed before
        //    the lookback and which started before it.
        dao.insertSession(
            row(start = 800L, routineId = 9, windowEnd = 900L, routineOutcome = "ENDED")
        )
        assertEquals(base + 3, dao.countDeriveSessionInputs(since))
    }
}
