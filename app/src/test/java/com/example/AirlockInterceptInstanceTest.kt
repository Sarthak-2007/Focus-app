package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.TactileSoundEngine
import com.example.ui.MonolithViewModel
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
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
            viewModel.triggerAirlockIntercept("com.example.blocked", "Blocked")
            val first = viewModel.airlockState.value
            viewModel.triggerAirlockIntercept("com.example.blocked", "Blocked")
            val second = viewModel.airlockState.value

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
