package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.MonolithDatabase
import com.example.data.SetupStore
import com.example.data.StrongroomDayBirth
import com.example.data.StrongroomStore
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomGuard
import java.time.LocalTime
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * SETUP-FLOW — the setup side's seams, proven at the exact joints
 * the screens work:
 *
 *  1. THE PICKER → THE BIRTH. The app picker writes a catalogue
 *     row in ONE form — copy(blockTier, isBlocked).normalized(),
 *     the entity's own write funnel — and [StrongroomDayBirth]
 *     derives a born day from those very rows through
 *     [com.example.data.LegacyCarry]. This test performs the
 *     picker's write (Notes: OPEN → SHUT) over the production
 *     fixtures, runs the production producer, and reads the born
 *     day: the app he shut in setup is shut in the day, and the
 *     peek app stands in the signed allowed set.
 *
 *  2. THE GUARD VERBS AS RULED, through the store the Guard
 *     screen works: tighten lands instantly; a raise schedules
 *     from the next day-start and does NOT move today's
 *     allowance; off schedules to the next day-start; inside a
 *     stretch, off is REFUSED — the stretch has no off.
 *
 *  3. THE FRESH INSTALL (build 5's dead doorway): a catalogue
 *     and guard settings but NO session and NO profile — the
 *     birth honestly refuses (NotBorn, nothing written). He
 *     then sets his standing lifting hour on setup's DAY panel
 *     (the SetupStore write the panel performs), the birth is
 *     re-run with that very read passed in — as all three
 *     production call sites now pass it — and the day STANDS,
 *     carrying his hour and his shut app.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SetupFlowSeamTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val fx = P9ProductionFixtures

    /** The DayBirth walk's evening: 2026-11-04 19:00 UTC. */
    private val eveningMillis: Long = fx.NOW_MILLIS + 7 * 3_600_000L

    private fun seededDb(): MonolithDatabase =
        Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Test
    fun pickerWrite_changesWhatTheBirthDerives() = runBlocking {
        val db = seededDb()
        try {
            val dao = db.monolithDao()
            dao.insertApps(fx.blockedApps)
            dao.insertSite(fx.blockedSite)
            dao.insertProfile(fx.profile)
            dao.insertGuardSettings(fx.guardSettings)
            fx.sessions().forEach { dao.insertSession(it) }

            // THE PICKER'S WRITE, verbatim: he shuts the Notes app.
            val notes = dao.getAppByPackage(fx.OPEN_PACKAGE)!!
            dao.updateApp(
                notes.copy(
                    blockTier = AppBlockTier.LOCKED.name,
                    isBlocked = true
                ).normalized()
            )
            val written = dao.getAppByPackage(fx.OPEN_PACKAGE)!!
            assertEquals(AppBlockTier.LOCKED, written.resolvedTier)
            assertFalse("the write funnel leaves no corrupt row", written.tierIsCorrupt)

            // THE BIRTH, through the production producer.
            val store = StrongroomStore(dao)
            val outcome = StrongroomDayBirth.ensureStandingDays(
                store, dao, eveningMillis, fx.UTC
            )
            val standing = outcome as StrongroomDayBirth.Outcome.Standing
            val record = standing.tomorrow.record
            assertTrue(
                "the fixture's shut app is shut in the born day",
                fx.LOCKED_PACKAGE in record.lockedPackages
            )
            assertTrue(
                "the app shut in setup is shut in the born day",
                fx.OPEN_PACKAGE in record.lockedPackages
            )
            assertFalse(
                "a shut app never stands in the signed allowed set",
                fx.OPEN_PACKAGE in record.allowedPackages
            )
            assertTrue(
                "the peek app stands in the signed allowed set",
                fx.PEEK_PACKAGE in record.allowedPackages
            )
        } finally {
            db.close()
        }
    }

    @Test
    fun freshInstall_birthWaitsForTheSetupHour_thenStands() = runBlocking {
        val db = seededDb()
        try {
            val dao = db.monolithDao()
            // A fresh install's data: the synced catalogue and
            // the guard's settings. No session, no profile —
            // no window end anywhere in his data.
            dao.insertApps(fx.blockedApps)
            dao.insertGuardSettings(fx.guardSettings)
            val store = StrongroomStore(dao)
            val todayIso = StrongroomDay.filingDateIso(eveningMillis, fx.UTC)

            // The birth, exactly as the entry paths run it with
            // nothing set: it refuses, and writes nothing.
            val refused = StrongroomDayBirth.ensureStandingDays(
                store, dao, eveningMillis, fx.UTC
            )
            assertTrue(
                "no lifting hour in his data — no day is born",
                refused is StrongroomDayBirth.Outcome.NotBorn
            )
            assertEquals(null, store.day(todayIso))

            // THE DAY PANEL'S WRITE, verbatim: the store reads
            // unset until his hand sets the hour.
            val setupStore = SetupStore(context)
            assertEquals(null, setupStore.liftingTime())
            setupStore.setLiftingTime(20, 0)
            assertEquals(LocalTime.of(20, 0), setupStore.liftingTime())

            // The birth again, with the setup read passed in as
            // the production call sites pass it: the day stands.
            val outcome = StrongroomDayBirth.ensureStandingDays(
                store, dao, eveningMillis, fx.UTC,
                standingLiftingTime = setupStore.liftingTime()
            )
            val standing = outcome as StrongroomDayBirth.Outcome.Standing
            assertEquals(
                "the born day carries HIS hour, not an invented one",
                LocalTime.of(20, 0), standing.tomorrow.record.liftingTime
            )
            assertTrue(
                "his shut app is shut in the first born day",
                fx.LOCKED_PACKAGE in standing.tomorrow.record.lockedPackages
            )
            assertTrue("today's row now stands", store.day(todayIso) != null)
        } finally {
            db.close()
        }
    }

    @Test
    fun guardVerbs_behaveAsRuled_throughTheStore() = runBlocking {
        val db = seededDb()
        try {
            val dao = db.monolithDao()
            val store = StrongroomStore(dao)
            val now = fx.NOW_MILLIS

            // Born at the shipped default.
            assertEquals(30, store.guardState(now).allowanceMinutes)

            // Rule 1a — tighten lands instantly.
            val tightened = store.tightenGuard(now, 20)
            assertEquals(20, tightened.allowanceMinutes)

            // Rule 1b — a raise schedules from the next day-start;
            // today's allowance does not move.
            val (afterRaise, raiseResult) = store.requestGuardRaise(now, 45, fx.UTC)
            assertTrue(raiseResult is StrongroomGuard.RaiseRequest.Scheduled)
            assertEquals(
                StrongroomDay.nextDayStartMillis(now, fx.UTC),
                (raiseResult as StrongroomGuard.RaiseRequest.Scheduled).effectiveAtMillis
            )
            assertEquals(20, afterRaise.allowanceMinutes)
            assertEquals(45, afterRaise.pendingAllowanceMinutes)

            // Rule 2 — off schedules to the next day-start.
            val (_, offResult) = store.requestGuardOff(now, sealedBlockActive = false, fx.UTC)
            assertTrue(offResult is StrongroomGuard.OffRequest.Scheduled)
            assertEquals(
                StrongroomDay.nextDayStartMillis(now, fx.UTC),
                (offResult as StrongroomGuard.OffRequest.Scheduled).effectiveAtMillis
            )

            // Rule 3 — inside a stretch there is no off.
            val stretchEnd = now + 7 * 24 * 3_600_000L
            val stretched = store.beginGuardStretch(now, stretchEnd)
            assertTrue(stretched.stretchActive(now))
            val (_, stretchOff) = store.requestGuardOff(now, sealedBlockActive = false, fx.UTC)
            assertTrue(stretchOff is StrongroomGuard.OffRequest.Refused)
            assertEquals(
                StrongroomGuard.OffRefusal.STRETCH_ACTIVE,
                (stretchOff as StrongroomGuard.OffRequest.Refused).reason
            )
        } finally {
            db.close()
        }
    }
}
