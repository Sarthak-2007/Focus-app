package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.StrongroomDayBirth
import com.example.data.StrongroomStore
import com.example.domain.StrongroomDay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * DAYBIRTH — the production producer's proof.
 *
 * FLOW-SEAT's whole-day walk found the day could never be born
 * on the phone: composeDay / LegacyCarry had no production
 * caller and the Strongroom tables are born empty. This test
 * drives the SAME producer the composed app runs at its entry
 * paths ([StrongroomDayBirth.ensureStandingDays]) over
 * production-shaped legacy data (P9ProductionFixtures) and walks
 * the chain his hand walks:
 *
 *   evening open → today + tomorrow stand COMPOSED from his
 *   loadout → the signing table's day (tomorrow) is signed →
 *   re-ensuring never recomposes a signed day → next morning the
 *   seal stands on the very record he signed.
 *
 * And the refusal: with no lifting hour anywhere in his data,
 * no day is born and nothing is invented.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DayBirthTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val fx = P9ProductionFixtures
    private val utc = fx.UTC

    /** The evening of the walk: Wednesday 2026-11-04, 19:00 UTC
     *  — filing day 2026-11-04 (the 03:30 seam), tomorrow the
     *  5th. The fixture's active session runs 11:50 + 2 h, so the
     *  carried window end is 13:50. */
    private val eveningMillis: Long = fx.NOW_MILLIS + 7 * 3_600_000L
    private val todayIso = "2026-11-04"
    private val tomorrowIso = "2026-11-05"

    private fun seededDb(): MonolithDatabase {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        return db
    }

    private suspend fun seedLegacy(dao: com.example.data.MonolithDao) {
        dao.insertApps(fx.blockedApps)
        dao.insertSite(fx.blockedSite)
        dao.insertProfile(fx.profile)
        dao.insertGuardSettings(fx.guardSettings)
        fx.sessions().forEach { dao.insertSession(it) }
    }

    @Test
    fun birth_eveningOpen_todayAndTomorrowComposedFromHisLoadout() = runBlocking {
        val db = seededDb()
        try {
            val dao = db.monolithDao()
            seedLegacy(dao)
            val store = StrongroomStore(dao)

            val outcome = StrongroomDayBirth.ensureStandingDays(
                store, dao, eveningMillis, utc
            )
            val standing = outcome as StrongroomDayBirth.Outcome.Standing

            // Both days stand, filed under their own dates.
            assertEquals(todayIso, standing.today.filingDateIso)
            assertEquals(tomorrowIso, standing.tomorrow.filingDateIso)

            // The composition is HIS loadout, carried: only the
            // LOCKED tier locks; the allowed set is exactly the
            // non-LOCKED rows (the snapshot query carries no
            // ORDER BY, so the set is asserted, not the order);
            // the lifting hour is his active session's window
            // end; the break length is his Guard's; the allowance
            // is the Guard's standing default. The sentence stands
            // at its kept place — a composition carries no word.
            val record = standing.tomorrow.record
            assertEquals(setOf(fx.LOCKED_PACKAGE), record.lockedPackages)
            assertEquals(
                setOf(fx.OPEN_PACKAGE, fx.PEEK_PACKAGE),
                record.allowedPackages.toSet()
            )
            assertEquals(2, record.allowedPackages.size)
            assertEquals(LocalTime.of(13, 50), record.liftingTime)
            assertEquals(fx.GUARD_BREAK_MINUTES, record.breakLengthMinutes)
            assertEquals(30, record.allowanceMinutes)
            assertNull(record.sentence)

            // Persisted COMPOSED, and the birth is not a signing:
            // the first-signing fact survives it untouched.
            val todayRow = store.day(todayIso)!!
            val tomorrowRow = store.day(tomorrowIso)!!
            assertEquals("COMPOSED", todayRow.state)
            assertEquals("COMPOSED", tomorrowRow.state)
            assertFalse(todayRow.amendedBySigning)
            assertFalse(tomorrowRow.amendedBySigning)
            assertTrue(store.isFirstSigning())
        } finally {
            db.close()
        }
    }

    @Test
    fun birth_signTomorrow_reEnsureNeverRecomposes_nextMorningSealStands() = runBlocking {
        val db = seededDb()
        try {
            val dao = db.monolithDao()
            seedLegacy(dao)
            val store = StrongroomStore(dao)

            val standing = StrongroomDayBirth.ensureStandingDays(
                store, dao, eveningMillis, utc
            ) as StrongroomDayBirth.Outcome.Standing

            // THE SIGNING — the host's own act on the table's day:
            // he moves the lifting hour and signs the record whole
            // through the store's sign verb.
            store.sign(
                standing.tomorrow.record.copy(liftingTime = LocalTime.of(14, 30)),
                amended = true
            )
            assertEquals("SIGNED", store.day(tomorrowIso)!!.state)

            // Re-ensuring (every entry load does) never recomposes
            // a signed day: the settled record stands as settled.
            val again = StrongroomDayBirth.ensureStandingDays(
                store, dao, eveningMillis + 3_600_000L, utc
            ) as StrongroomDayBirth.Outcome.Standing
            assertEquals("SIGNED", store.day(tomorrowIso)!!.state)
            assertEquals(
                LocalTime.of(14, 30),
                again.tomorrow.record.liftingTime
            )
            assertTrue(again.tomorrow.amendedBySigning)

            // NEXT MORNING — the first sitting, 05:00 on the 5th:
            // the filing seam has rolled, the signed day IS
            // today's day, and the swing seals it where it stands.
            val morningMillis = LocalDateTime.of(2026, 11, 5, 5, 0)
                .atZone(utc).toInstant().toEpochMilli()
            assertEquals(
                tomorrowIso,
                StrongroomDay.filingDateIso(morningMillis, utc)
            )
            StrongroomDayBirth.ensureStandingDays(store, dao, morningMillis, utc)
            val span = store.seal(
                dateIso = tomorrowIso,
                sealedAtWallMillis = morningMillis,
                sealedAtElapsedMillis = 600_000L,
                bootCount = 3,
                zone = utc
            )
            assertNotNull("the seal must stand on the signed day", span)
            assertEquals(34_200_000L, span!!.spanDurationMillis) // 05:00 → 14:30
            assertEquals("SEALED", store.day(tomorrowIso)!!.state)
        } finally {
            db.close()
        }
    }

    @Test
    fun birth_noLiftingHourInHisData_noDayBorn_nothingInvented() = runBlocking {
        val db = seededDb()
        try {
            val dao = db.monolithDao()
            // Apps and guard settings stand, but NO session and
            // NO profile window: his data holds no lifting hour,
            // and the carry — by its law — invents none.
            dao.insertApps(fx.blockedApps)
            dao.insertGuardSettings(fx.guardSettings)
            val store = StrongroomStore(dao)

            val outcome = StrongroomDayBirth.ensureStandingDays(
                store, dao, eveningMillis, utc
            )
            assertEquals(StrongroomDayBirth.Outcome.NotBorn, outcome)
            assertNull(store.day(todayIso))
            assertNull(store.day(tomorrowIso))
        } finally {
            db.close()
        }
    }
}
