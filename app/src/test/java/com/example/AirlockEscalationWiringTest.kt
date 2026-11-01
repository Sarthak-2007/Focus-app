package com.example

import android.content.Context
import android.os.Looper
import android.os.SystemClock
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AirlockBreathStore
import com.example.data.FocusSessionEntity
import com.example.data.InterceptLogEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.TactileSoundEngine
import com.example.ui.MonolithViewModel
import com.example.ui.components.escalatedAirlockWallSeconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Audit R6 #340 F17, wiring completed via #254 line 14: the
 * escalation ladder (AirlockEscalationTest pins the pure function)
 * reads AirlockInterceptState.priorReachesToday — a field no
 * writer populated, so every visit priced at zero prior reaches.
 * These tests pin the ViewModel wiring end to end: after a live
 * trigger's post-booking read-back, the state carries the day's
 * PRIOR reaches for the package (the booked arrival excluded),
 * while the F12 attempt mirror keeps its own count (the arrival
 * included) — the two numbers differ by exactly the arrival.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AirlockEscalationWiringTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun seededViewModel(db: MonolithDatabase): MonolithViewModel {
        val viewModel = MonolithViewModel(
            context,
            MonolithRepository(context, db.monolithDao()),
            TactileSoundEngine(context)
        )
        runBlocking {
            db.monolithDao().insertSession(
                FocusSessionEntity(
                    intention = "Airlock escalation wiring fixture",
                    profileName = "Manual",
                    targetDurationSeconds = 1500,
                    elapsedSeconds = 0,
                    startWallTimeMillis = System.currentTimeMillis(),
                    startElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
                    isActive = true,
                    completedSuccessfully = false
                )
            )
        }
        val collectJob = CoroutineScope(Dispatchers.Unconfined).launch {
            viewModel.activeSession.collect { }
        }
        var attempts = 0
        while (viewModel.activeSession.value?.isActive != true && attempts < 200) {
            shadowOf(Looper.getMainLooper()).idle()
            if (viewModel.activeSession.value?.isActive == true) break
            Thread.sleep(5)
            attempts++
        }
        assertTrue(
            "seeded active session must reach the viewModel gate",
            viewModel.activeSession.value?.isActive == true
        )
        collectJob.cancel()
        return viewModel
    }

    private fun awaitState(
        viewModel: MonolithViewModel,
        condition: (com.example.ui.AirlockInterceptState) -> Boolean
    ): com.example.ui.AirlockInterceptState {
        var attempts = 0
        while (!condition(viewModel.airlockState.value) && attempts < 400) {
            shadowOf(Looper.getMainLooper()).idle()
            if (condition(viewModel.airlockState.value)) break
            Thread.sleep(5)
            attempts++
        }
        return viewModel.airlockState.value
    }

    @Test
    fun priorReachesTodayCountsTheDaysEarlierReachesExcludingThisArrival() {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val viewModel = seededViewModel(db)
            // Two reaches at the same package earlier today, straight
            // into the persisted ledger the ladder prices from.
            val now = System.currentTimeMillis()
            runBlocking {
                repeat(2) { i ->
                    db.monolithDao().insertInterceptLog(
                        InterceptLogEntity(
                            timestampMillis = now - (2 - i) * 1_000L,
                            packageName = "com.example.blocked",
                            appName = "Blocked",
                            sessionIntention = "fixture",
                            outcome = "AT DOOR"
                        )
                    )
                }
            }
            viewModel.triggerAirlockIntercept("com.example.blocked", "Blocked")
            val state = awaitState(viewModel) {
                it.priorReachesToday == 2 && it.todayAttempts == 3
            }
            assertEquals(
                "prior reaches exclude the arrival this trigger booked",
                2, state.priorReachesToday
            )
            assertEquals(
                "the F12 mirror counts the same ledger including this reach",
                3, state.todayAttempts
            )
            assertEquals(
                "the ladder prices this visit at two prior reaches",
                20,
                escalatedAirlockWallSeconds(
                    state.priorReachesToday,
                    AirlockBreathStore.DEFAULT_SECONDS
                )
            )
        } finally {
            db.close()
        }
    }

    @Test
    fun firstReachOfTheDayCarriesZeroPriorReaches() {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val viewModel = seededViewModel(db)
            viewModel.triggerAirlockIntercept("com.example.fresh", "Fresh")
            val state = awaitState(viewModel) { it.todayAttempts == 1 }
            assertEquals(
                "no earlier reaches today — the ladder starts at the base",
                0, state.priorReachesToday
            )
            assertEquals(
                AirlockBreathStore.DEFAULT_SECONDS,
                escalatedAirlockWallSeconds(
                    state.priorReachesToday,
                    AirlockBreathStore.DEFAULT_SECONDS
                )
            )
        } finally {
            db.close()
        }
    }
}
