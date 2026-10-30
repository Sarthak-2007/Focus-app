package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.service.PermissionFlowProgress
import com.example.service.PermissionFlowRecord
import com.example.service.PermissionFlowStore
import com.example.service.WatchItemId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #324, finding 4 (BUILD half): the persisted step state,
 * exercised live — the record survives the process that wrote it.
 *
 * The resume promise is the whole finding: a grant sequence
 * interrupted by Settings, Home, or process death resumes at the
 * step it stopped at. That is only true if the facts land on disk
 * (commit(), the Watch Store's LAST PROVED standard) and a fresh
 * process's bind() restores them whole. Each test below wipes the
 * prefs file first, so the sandbox's earlier runs cannot leak a
 * record into the assertions.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PermissionFlowStoreRobolectricTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun reset() {
        PermissionFlowStore.resetForTest()
        context.getSharedPreferences("monolith_permission_flow", Context.MODE_PRIVATE)
            .edit().clear().commit()
        PermissionFlowStore.bind(context)
        assertEquals(PermissionFlowRecord.EMPTY, PermissionFlowStore.record())
    }

    @Test
    fun stepPresented_survivesAProcessRestart() {
        PermissionFlowStore.notePrimerShown(wallMs = 1_000L)
        PermissionFlowStore.recordStepPresented(WatchItemId.USAGE_ACCESS, wallMs = 2_000L)
        PermissionFlowStore.recordStepPresented(WatchItemId.OVERLAY, wallMs = 3_000L)

        // Process death: in-memory state and the binding are gone;
        // only the disk facts remain. A fresh bind must restore the
        // flow exactly where it stopped.
        PermissionFlowStore.resetForTest()
        assertEquals(PermissionFlowRecord.EMPTY, PermissionFlowStore.record())
        PermissionFlowStore.bind(context)

        val restored = PermissionFlowStore.record()
        assertEquals(1_000L, restored.startedWallMs)
        assertEquals(
            setOf(WatchItemId.USAGE_ACCESS, WatchItemId.OVERLAY),
            restored.presentedSteps
        )
        assertEquals(WatchItemId.OVERLAY, restored.lastStep)
        assertNull(restored.completedWallMs)
    }

    @Test
    fun completion_survivesAProcessRestart_andNeverRegresses() {
        val complete = PermissionFlowProgress(
            totalSteps = 6, grantedCount = 6, currentStep = null, complete = true
        )
        PermissionFlowStore.noteProgress(complete, wallMs = 9_000L)
        assertEquals(9_000L, PermissionFlowStore.record().completedWallMs)

        PermissionFlowStore.resetForTest()
        PermissionFlowStore.bind(context)
        assertEquals(9_000L, PermissionFlowStore.record().completedWallMs)

        // A later completion read keeps the first instant.
        PermissionFlowStore.noteProgress(complete, wallMs = 12_000L)
        assertEquals(9_000L, PermissionFlowStore.record().completedWallMs)
    }

    @Test
    fun incompleteProgress_recordsNothing() {
        val incomplete = PermissionFlowProgress(
            totalSteps = 6, grantedCount = 4,
            currentStep = WatchItemId.OVERLAY, complete = false
        )
        PermissionFlowStore.noteProgress(incomplete, wallMs = 9_000L)
        assertEquals(PermissionFlowRecord.EMPTY, PermissionFlowStore.record())
    }

    @Test
    fun bind_isIdempotent_andTheFlowPublishesThroughTheStateFlow() {
        PermissionFlowStore.bind(context)
        PermissionFlowStore.recordStepPresented(WatchItemId.BATTERY, wallMs = 7_000L)
        assertTrue(
            PermissionFlowStore.records.value.presentedSteps
                .contains(WatchItemId.BATTERY)
        )
        assertEquals(7_000L, PermissionFlowStore.records.value.startedWallMs)
    }
}
