package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.TactileSoundEngine
import com.example.ui.MonolithViewModel
import com.example.parkIntroSpendsOnDismiss
import com.example.PARK_INTRO_READ_DWELL_MS
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Issue #534 — first-run honesty, proven at the level each cure
 * lives at:
 *
 *  - F2's spend verdict is pure arithmetic over (delivered,
 *    fix-trip, dwell): tabled here against the issue's two
 *    acceptance cases — (a) rise -> immediate scrim-tap dismissal
 *    must NOT spend; (b) rise -> read (dwell elapsed) or a FIX
 *    trip -> dismissal spends.
 *  - F3's record is a prefs round-trip on the real repository.
 *  - F1 is a ViewModel-level fact: on a virgin install (empty DB,
 *    no install notes), the fresh-start note must be visible
 *    after init's seed completes — the exact read-before-set the
 *    issue filed.
 */
class ParkIntroSpendVerdictTest {

    @Test
    fun acceptanceA_riseThenImmediateScrimTap_doesNotSpend() {
        assertFalse(
            parkIntroSpendsOnDismiss(
                deliveredThisOpen = true,
                fixTripTakenThisOpen = false,
                deliveredAtMonoMs = 10_000L,
                nowMonoMs = 10_500L
            )
        )
        assertFalse(
            parkIntroSpendsOnDismiss(
                deliveredThisOpen = true,
                fixTripTakenThisOpen = false,
                deliveredAtMonoMs = 10_000L,
                nowMonoMs = 10_000L + PARK_INTRO_READ_DWELL_MS - 1L
            )
        )
    }

    @Test
    fun acceptanceB_riseThenReadThenDismiss_spends() {
        assertTrue(
            parkIntroSpendsOnDismiss(
                deliveredThisOpen = true,
                fixTripTakenThisOpen = false,
                deliveredAtMonoMs = 10_000L,
                nowMonoMs = 10_000L + PARK_INTRO_READ_DWELL_MS
            )
        )
    }

    @Test
    fun acceptanceB_riseThenFixTripThenDismiss_spendsEvenInsideDwell() {
        assertTrue(
            parkIntroSpendsOnDismiss(
                deliveredThisOpen = true,
                fixTripTakenThisOpen = true,
                deliveredAtMonoMs = 10_000L,
                nowMonoMs = 10_100L
            )
        )
    }

    @Test
    fun neverDelivered_neverSpends() {
        assertFalse(
            parkIntroSpendsOnDismiss(
                deliveredThisOpen = false,
                fixTripTakenThisOpen = true,
                deliveredAtMonoMs = 0L,
                nowMonoMs = 999_999L
            )
        )
        assertFalse(
            parkIntroSpendsOnDismiss(
                deliveredThisOpen = true,
                fixTripTakenThisOpen = false,
                deliveredAtMonoMs = 0L,
                nowMonoMs = 999_999L
            )
        )
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue534RepositoryAndViewModelTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository

    @Before
    fun setup() {
        context.getSharedPreferences("monolith_install_notes", Context.MODE_PRIVATE)
            .edit().clear().commit()
        context.getSharedPreferences("monolith_chrome", Context.MODE_PRIVATE)
            .edit().clear().commit()
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun f3_postNotificationsAnswer_roundTripsAndStartsUnrecorded() {
        assertEquals(null, repository.postNotificationsGranted())
        repository.recordPostNotificationsResult(false)
        assertEquals(false, repository.postNotificationsGranted())
        repository.recordPostNotificationsResult(true)
        assertEquals(true, repository.postNotificationsGranted())
    }

    @Test
    fun f1_freshStartNote_isVisibleOnFirstOpenAfterSeed() {
        val viewModel = MonolithViewModel(context, repository, TactileSoundEngine(context))
        // The seed runs on IO from init; wait for the re-read the
        // cure added at the end of that launch to land.
        var visible = false
        repeat(100) {
            ShadowLooper.idleMainLooper()
            if (viewModel.freshStartNoteVisible.value) {
                visible = true
                return@repeat
            }
            Thread.sleep(50)
        }
        ShadowLooper.idleMainLooper()
        visible = viewModel.freshStartNoteVisible.value
        assertTrue(
            "the fresh-start note must show on the FIRST open: the seed sets " +
                "the flag and VM init re-reads it after the seed completes",
            visible
        )
        assertTrue(repository.freshStartNotePending())
    }
}
