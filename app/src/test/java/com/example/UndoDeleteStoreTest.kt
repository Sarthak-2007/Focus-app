package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusTaskEntity
import com.example.data.TaskQueueStatus
import com.example.data.UndoDeleteStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * G-03 (Truth Car 2): the delete-with-undo window's restore copy
 * survives process death because it is PERSISTED (commit()), not
 * held in a ViewModel field. The store is a pure round-trip: the
 * exact entity and its deletion instant come back, and a clear
 * leaves nothing to revive.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UndoDeleteStoreTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val store: UndoDeleteStore
        get() = UndoDeleteStore(context)

    private fun task() = FocusTaskEntity(
        id = 42,
        title = "GOC — Basics",
        notesOrSubsteps = "SUBSTEPS:\n[ ] Step A",
        estimatedMinutes = 45,
        actualSecondsSpent = 900,
        categoryTag = "CHEMISTRY",
        queueOrder = 7,
        status = TaskQueueStatus.QUEUED.name,
        startedAtSessionElapsedSec = 1234
    )

    @Test
    fun roundTrip_restoresEveryField() {
        store.clear()
        val original = task()
        // Audit R1 #41: save reports commit()'s verdict.
        assertTrue(
            store.save(
                original,
                deletedAtWallMillis = 9_876_000L,
                deletedAtElapsedMillis = 555_000L
            )
        )

        // A fresh store instance — the process-death read path.
        val restored = UndoDeleteStore(context).load()
        assertEquals(original, restored?.first)
        assertEquals(9_876_000L, restored?.second)
        assertEquals(555_000L, restored?.third)
    }

    @Test
    fun clear_leavesNothingToRevive() {
        store.save(task(), deletedAtWallMillis = 1L, deletedAtElapsedMillis = 1L)
        store.clear()
        assertNull(store.load())
    }

    /**
     * Audit R1 #41: the window is judged on the monotonic stamp
     * when it survives — the wall clock only across a reboot.
     */
    @Test
    fun remaining_judgedByMonotonicClock_whenStampUsable() {
        // Wall rolled BACK an hour; monotonic says 6 s passed:
        // the undo stays expired (wall alone resurrected it).
        assertTrue(
            UndoDeleteStore.remainingMillis(
                deletedAtWallMillis = 10_000_000L,
                deletedAtElapsedMillis = 500_000L,
                nowWallMillis = 10_000_000L - 3_600_000L,
                nowElapsedMillis = 506_000L
            ) < 0L
        )
        // Wall jumped FORWARD an hour; monotonic says 2 s
        // passed: the undo stays alive (wall alone stole it).
        assertEquals(
            3_000L,
            UndoDeleteStore.remainingMillis(
                deletedAtWallMillis = 10_000_000L,
                deletedAtElapsedMillis = 500_000L,
                nowWallMillis = 10_000_000L + 3_600_000L,
                nowElapsedMillis = 502_000L
            )
        )
    }

    @Test
    fun remaining_fallsBackToWall_acrossRebootOrLegacy() {
        // New boot: the monotonic stamp lies AHEAD of the fresh
        // monotonic clock — unusable; the wall clock judges.
        assertEquals(
            3_000L,
            UndoDeleteStore.remainingMillis(
                deletedAtWallMillis = 10_000_000L,
                deletedAtElapsedMillis = 900_000L,
                nowWallMillis = 10_002_000L,
                nowElapsedMillis = 2_000L
            )
        )
        // A pre-monotonic copy (stamp -1): wall judges, and an
        // expired window stays expired.
        assertTrue(
            UndoDeleteStore.remainingMillis(
                deletedAtWallMillis = 10_000_000L,
                deletedAtElapsedMillis = -1L,
                nowWallMillis = 10_006_000L,
                nowElapsedMillis = 2_000L
            ) < 0L
        )
    }

    @Test
    fun emptyStore_loadsNull() {
        store.clear()
        assertNull(store.load())
    }
}
