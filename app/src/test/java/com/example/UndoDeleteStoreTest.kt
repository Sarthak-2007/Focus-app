package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusTaskEntity
import com.example.data.TaskQueueStatus
import com.example.data.UndoDeleteStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
        store.save(original, deletedAtWallMillis = 9_876_000L)

        // A fresh store instance — the process-death read path.
        val restored = UndoDeleteStore(context).load()
        assertEquals(original, restored?.first)
        assertEquals(9_876_000L, restored?.second)
    }

    @Test
    fun clear_leavesNothingToRevive() {
        store.save(task(), deletedAtWallMillis = 1L)
        store.clear()
        assertNull(store.load())
    }

    @Test
    fun emptyStore_loadsNull() {
        store.clear()
        assertNull(store.load())
    }
}
