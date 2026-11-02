package com.example

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.StrongroomStore
import com.example.data.toSealSpan
import com.example.data.toSigningRecord
import com.example.domain.StrongroomDay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalTime
import java.time.ZoneId

/**
 * PLACES — the break becomes a place for a Strongroom day.
 *
 * The two standings the shell now derives (ShellHost's
 * loadSpanBreakFacts → StrongroomShell's strongroomBreakStanding
 * / crankMinuteStanding), proven at the exact chain the host
 * runs: store rows → the domain's own arithmetic. Before this
 * wiring the shell read the legacy session's isInSafeBreak
 * alone, so a Strongroom firing stood no room and the crank's
 * minute opened nothing (GAP-COORD-CRANK-CONSUMPTION).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PlacesBreakStandingTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val storageContext: Context
        get() {
            val appCtx = context.applicationContext
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                appCtx.createDeviceProtectedStorageContext() ?: appCtx
            } else {
                appCtx
            }
        }

    private val utc: ZoneId = ZoneId.of("UTC")

    @Before
    fun coldStart() {
        MonolithDatabase.closeAndResetForTests()
        storageContext.deleteDatabase("monolith_tactile_db")
        context.deleteDatabase("monolith_tactile_db")
    }

    @After
    fun teardown() {
        MonolithDatabase.closeAndResetForTests()
        storageContext.deleteDatabase("monolith_tactile_db")
        context.deleteDatabase("monolith_tactile_db")
    }

    private fun store(): StrongroomStore =
        StrongroomStore(com.example.AppContainer.dao(context))

    @Test
    fun crankMinute_windowIsExactlyTheSpendsSixtySeconds() = runBlocking {
        val store = store()
        // Monday 2026-11-02 05:00 UTC — inside one crank week.
        val t0 = java.time.LocalDateTime.of(2026, 11, 2, 5, 0)
            .atZone(utc).toInstant().toEpochMilli()

        // Unspent: no window stands at any reading.
        val fresh = store.crankWeek(t0, utc)
        assertTrue(fresh.minuteAvailable)
        assertFalse(fresh.minuteRunningAt(t0))
        assertFalse(fresh.minuteRunningAt(t0 + 30_000L))

        val spent = store.spendCrank(t0, utc)
        assertNotNull(spent)
        val week = store.crankWeek(t0, utc)

        // The window: [spend, spend + 60 s) — open inside the
        // block for exactly the minute, closed at its end.
        assertTrue(week.minuteRunningAt(t0))
        assertTrue(week.minuteRunningAt(t0 + 30_000L))
        assertTrue(week.minuteRunningAt(t0 + 59_999L))
        assertFalse(week.minuteRunningAt(t0 + 60_000L))
        assertFalse(week.minuteRunningAt(t0 - 1L))
        assertEquals(30_000L, week.minuteRemainingMillisAt(t0 + 30_000L))
        assertEquals(0L, week.minuteRemainingMillisAt(t0 + 60_000L))
    }

    @Test
    fun firingStanding_derivedFromTheSealedSpanNotTheSession() = runBlocking {
        val store = store()
        val dateIso = "2026-11-05"
        store.sign(
            StrongroomDay.SigningRecord(
                filingDateIso = dateIso,
                lockedPackages = setOf("com.video.app"),
                allowedPackages = listOf("com.notes.app"),
                liftingTime = LocalTime.of(14, 0),
                sentence = null,
                breakLengthMinutes = 20,
                breakFiresAtOffsetMillis = 3_600_000L, // 1 h into the span
                allowanceMinutes = 30,
                gaps = emptyList()
            )
        )
        // Sealed 04:00 UTC for a 14:00 lifting (a 10 h span),
        // at elapsed 500_000 in boot 7.
        val sealWall = java.time.LocalDateTime.of(2026, 11, 5, 4, 0)
            .atZone(utc).toInstant().toEpochMilli()
        val sealedAtElapsed = 500_000L
        val span = store.seal(dateIso, sealWall, sealedAtElapsed, 7, utc)
        assertNotNull(span)

        // The host's exact chain: sealedDay row → span → firing.
        val row = store.sealedDay()
        assertNotNull(row)
        val firing = StrongroomDay.breakFiring(
            row!!.toSigningRecord(), row.toSealSpan()!!
        )
        assertNotNull(firing)

        val firesAt = sealedAtElapsed + 3_600_000L
        val endsAt = firesAt + 20L * 60_000L
        assertFalse(firing!!.isRunning(firesAt - 1L))
        assertTrue(firing.isRunning(firesAt))
        assertTrue(firing.isRunning(endsAt - 1L))
        assertFalse(firing.isRunning(endsAt))
    }
}
