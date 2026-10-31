package com.example

import android.content.Context
import android.os.Looper
import android.os.SystemClock
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.TactileSoundEngine
import com.example.ui.MonolithViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Issue #55, finding 5 (verifier test gap): two consecutive
 * same-package intercepts must produce strictly increasing instance
 * ids and fresh monotonic stamps — the identity the overlay keys
 * its per-intercept 5s wall on.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AirlockInterceptInstanceTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun samePackageReInterceptGetsFreshInstanceAndStamp() {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val viewModel = MonolithViewModel(
                context,
                MonolithRepository(context, db.monolithDao()),
                TactileSoundEngine(context)
            )
            // Issue #68 F5 (Tier 1): triggerAirlockIntercept lawfully
            // refuses to fire without a live session, so the fixture
            // seeds the active session the gate checks — then collects
            // the WhileSubscribed activeSession flow, idling the main
            // looper, until the seeded row actually reaches the gate.
            runBlocking {
                db.monolithDao().insertSession(
                    FocusSessionEntity(
                        intention = "Airlock instance fixture",
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
            viewModel.triggerAirlockIntercept("com.example.blocked", "Blocked")
            val first = viewModel.airlockState.value
            viewModel.triggerAirlockIntercept("com.example.blocked", "Blocked")
            val second = viewModel.airlockState.value
            collectJob.cancel()

            assertTrue(
                "instance ids must strictly increase across re-intercepts",
                second.interceptInstanceId > first.interceptInstanceId
            )
            assertTrue(
                "monotonic stamp must never go backwards",
                second.interceptedAtMonoMillis >= first.interceptedAtMonoMillis
            )
            assertTrue("both intercepts stand visible", first.isVisible && second.isVisible)
        } finally {
            db.close()
        }
    }
}
