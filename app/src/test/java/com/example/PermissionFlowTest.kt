package com.example

import com.example.service.PermState
import com.example.service.PermissionFlow
import com.example.service.PermissionFlowRecord
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchSnapshot
import com.example.service.WatchStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Issue #324 (Audit R5, Zario second pass), finding 4 — BUILD half:
 * the permission flow's sequencing core and its persisted step
 * state.
 *
 * At the finding's closure the charge stood verified: the primer
 * was dead code (its body has since been deleted entirely, issue
 * #74) and zero persisted permission-step state existed — a grant
 * sequence interrupted anywhere restarted from the user's memory.
 * The cure's machinery, pinned here:
 *
 *  - the current step is DERIVED from the Watch snapshot's own
 *    prints (first measured lack, cheapest-first board order), so
 *    it can never disagree with the truth the board prints;
 *  - the record persists only facts (start, presented steps, the
 *    step stopped at, first completion) — never measurements;
 *  - a resume returns the step the user stopped at while it still
 *    stands missing, not the board's first lack;
 *  - the primer's rebind carries NO arm-anyway path (LR2-S1-18),
 *    pinned by source below.
 */
class PermissionFlowTest {

    private fun perm(item: WatchItemId, state: PermState, mono: Long = 10_000L) =
        WatchMeasurement(
            item = item, perm = state,
            measuredMono = mono, measuredWall = 1_700_000_000_000L, sweepGen = 1
        )

    private fun snapOf(vararg ms: WatchMeasurement): WatchSnapshot =
        WatchSnapshot(
            items = ms.associateBy { it.item },
            engineHeartbeatMono = 0L, engineHeartbeatWall = 0L,
            engineProtecting = false, lastProvedWallMillis = null
        )

    /** All six measured; [missing] (and any in [alsoMissing]) read MISSING. */
    private fun fullSnap(
        missing: WatchItemId? = null,
        alsoMissing: Set<WatchItemId> = emptySet(),
        mono: Long = 10_000L
    ): WatchSnapshot =
        snapOf(*WatchItemId.PERMISSIONS.map { item ->
            perm(
                item,
                if (item == missing || item in alsoMissing) {
                    PermState.MISSING
                } else {
                    PermState.GRANTED
                },
                mono
            )
        }.toTypedArray())

    private val now = 20_000L

    // ------------------------------------------------------------------
    // The derived step
    // ------------------------------------------------------------------

    @Test
    fun progress_allGrantedFresh_isCompleteWithNoStep() {
        val progress = PermissionFlow.progressAt(fullSnap(), now)
        assertTrue(progress.complete)
        assertNull(progress.currentStep)
        assertEquals(6, progress.grantedCount)
        assertEquals(6, progress.totalSteps)
    }

    @Test
    fun progress_firstMissingInBoardOrder_isTheStep() {
        // Board order is cheapest-first (issue #309 F5): USAGE_ACCESS
        // leads, ACCESSIBILITY closes the dear end. Both missing —
        // the step is the cheap one, not the scary one.
        val progress = PermissionFlow.progressAt(
            fullSnap(
                missing = WatchItemId.ACCESSIBILITY,
                alsoMissing = setOf(WatchItemId.USAGE_ACCESS)
            ),
            now
        )
        assertFalse(progress.complete)
        assertEquals(WatchItemId.USAGE_ACCESS, progress.currentStep)
        assertEquals(4, progress.grantedCount)
    }

    @Test
    fun progress_emptySnapshot_hasNoCountNoStepAndIsNotComplete() {
        // A cold open: nothing measured. A step or a count here
        // would be a fabrication (the Guard line's law).
        val progress = PermissionFlow.progressAt(WatchSnapshot.EMPTY, now)
        assertFalse(progress.complete)
        assertNull(progress.currentStep)
        assertNull(progress.grantedCount)
    }

    @Test
    fun progress_oneUnmeasured_blocksCompletionAndTheCount() {
        val partial = fullSnap().copy(
            items = fullSnap().items - WatchItemId.BATTERY
        )
        val progress = PermissionFlow.progressAt(partial, now)
        assertFalse(progress.complete)
        assertNull(progress.grantedCount)
        assertNull(progress.currentStep)
    }

    @Test
    fun progress_staleDenial_isStillAnActionableStep() {
        // OVERLAY denied long ago (MISSING_STALE): PARK's FIX key
        // treats an old denial as missing; so does the flow.
        val snap = snapOf(
            *WatchItemId.PERMISSIONS.map { item ->
                if (item == WatchItemId.OVERLAY) {
                    perm(item, PermState.MISSING, mono = 0L)
                } else {
                    perm(item, PermState.GRANTED, mono = 100_000L)
                }
            }.toTypedArray()
        )
        val progress = PermissionFlow.progressAt(snap, 100_000L)
        assertFalse(progress.complete)
        assertEquals(WatchItemId.OVERLAY, progress.currentStep)
    }

    @Test
    fun progress_staleGrant_isNotCompleteAndNotAStep() {
        // ACCESSIBILITY granted but the measurement is ancient
        // (STALE): not proved complete, and no FIX trip cures an
        // unmeasured read — the flow waits for the sweep.
        val snap = snapOf(
            *WatchItemId.PERMISSIONS.map { item ->
                if (item == WatchItemId.ACCESSIBILITY) {
                    perm(item, PermState.GRANTED, mono = 0L)
                } else {
                    perm(item, PermState.GRANTED, mono = 100_000L)
                }
            }.toTypedArray()
        )
        val progress = PermissionFlow.progressAt(snap, 100_000L)
        assertFalse(progress.complete)
        assertNull(progress.currentStep)
    }

    // ------------------------------------------------------------------
    // Resume
    // ------------------------------------------------------------------

    @Test
    fun resume_returnsTheStoppedAtStepWhileItStillStandsMissing() {
        // He stopped at DEVICE_ADMIN (presented via PARK, say) while
        // USAGE_ACCESS also stands missing: the funnel's promise is
        // the SAME step he stopped at, not the board's first lack.
        val record = PermissionFlowRecord.EMPTY
            .withStepPresented(WatchItemId.DEVICE_ADMIN, 5_000L)
        val snap = fullSnap(
            missing = WatchItemId.DEVICE_ADMIN,
            alsoMissing = setOf(WatchItemId.USAGE_ACCESS)
        )
        assertEquals(
            WatchItemId.USAGE_ACCESS,
            PermissionFlow.progressAt(snap, now).currentStep
        )
        assertEquals(
            WatchItemId.DEVICE_ADMIN,
            PermissionFlow.resumeStep(record, snap, now)
        )
    }

    @Test
    fun resume_stoppedAtStepSinceGranted_fallsToTheCurrentStep() {
        val record = PermissionFlowRecord.EMPTY
            .withStepPresented(WatchItemId.DEVICE_ADMIN, 5_000L)
        val snap = fullSnap(missing = WatchItemId.OVERLAY)
        assertEquals(
            WatchItemId.OVERLAY,
            PermissionFlow.resumeStep(record, snap, now)
        )
    }

    @Test
    fun resume_completeSet_resumesNowhere() {
        val record = PermissionFlowRecord.EMPTY
            .withStepPresented(WatchItemId.DEVICE_ADMIN, 5_000L)
        assertNull(PermissionFlow.resumeStep(record, fullSnap(), now))
    }

    @Test
    fun resume_noRecord_takesTheCurrentStep() {
        val snap = fullSnap(missing = WatchItemId.BATTERY)
        assertEquals(
            WatchItemId.BATTERY,
            PermissionFlow.resumeStep(PermissionFlowRecord.EMPTY, snap, now)
        )
    }

    // ------------------------------------------------------------------
    // The persisted record's transitions
    // ------------------------------------------------------------------

    @Test
    fun record_stepPresented_startsAccumulatesAndMarksTheStop() {
        val first = PermissionFlowRecord.EMPTY
            .withStepPresented(WatchItemId.USAGE_ACCESS, 1_000L)
        assertEquals(1_000L, first.startedWallMs)
        assertEquals(setOf(WatchItemId.USAGE_ACCESS), first.presentedSteps)
        assertEquals(WatchItemId.USAGE_ACCESS, first.lastStep)

        val second = first.withStepPresented(WatchItemId.OVERLAY, 2_000L)
        // The start never moves; the stop advances.
        assertEquals(1_000L, second.startedWallMs)
        assertEquals(
            setOf(WatchItemId.USAGE_ACCESS, WatchItemId.OVERLAY),
            second.presentedSteps
        )
        assertEquals(WatchItemId.OVERLAY, second.lastStep)
    }

    @Test
    fun record_completion_isForwardOnly() {
        val done = PermissionFlowRecord.EMPTY.withCompletion(9_000L)
        assertEquals(9_000L, done.completedWallMs)
        // A second completion keeps the FIRST instant — the fact is
        // "the set first stood complete at T", and T never regresses.
        assertEquals(9_000L, done.withCompletion(12_000L).completedWallMs)
    }

    @Test
    fun record_flowStarted_stampsOnce() {
        val started = PermissionFlowRecord.EMPTY.withFlowStarted(3_000L)
        assertEquals(3_000L, started.startedWallMs)
        assertEquals(3_000L, started.withFlowStarted(4_000L).startedWallMs)
    }

    @Test
    fun record_inProgress_onlyForABegunUncompletedFlow() {
        val incomplete = PermissionFlow.progressAt(
            fullSnap(missing = WatchItemId.OVERLAY), now
        )
        val complete = PermissionFlow.progressAt(fullSnap(), now)
        assertFalse(PermissionFlowRecord.EMPTY.inProgress(incomplete))
        val begun = PermissionFlowRecord.EMPTY.withFlowStarted(1_000L)
        assertTrue(begun.inProgress(incomplete))
        assertFalse(begun.inProgress(complete))
        assertFalse(begun.withCompletion(2_000L).inProgress(incomplete))
    }

    // ------------------------------------------------------------------
    // The primer's prints
    // ------------------------------------------------------------------

    @Test
    fun prints_stepLineFixLabelAndCount() {
        val progress = PermissionFlow.progressAt(
            fullSnap(missing = WatchItemId.OVERLAY), now
        )
        assertEquals(
            "STEP 2 OF 6 — OVERLAY",
            PermissionFlow.primerStepLine(WatchItemId.OVERLAY, progress)
        )
        assertEquals(
            "FIX OVERLAY",
            PermissionFlow.primerFixLabel(WatchItemId.OVERLAY)
        )
        assertEquals("5 OF 6 GRANTED", PermissionFlow.primerCountLine(progress))
        assertNull(PermissionFlow.primerStepLine(null, progress))
        assertNull(PermissionFlow.primerFixLabel(null))
    }

    @Test
    fun prints_countLineAbsentWhileTheCountDoesNotExist() {
        val progress = PermissionFlow.progressAt(WatchSnapshot.EMPTY, now)
        assertNull(PermissionFlow.primerCountLine(progress))
    }

    @Test
    fun prints_explainerIsTheBoardsOwnRationale() {
        // One explainer text, owned by the board (issue #280) — the
        // primer must not grow a second, drifting copy.
        assertEquals(
            WatchStore.boardRationale(WatchItemId.USAGE_ACCESS),
            PermissionFlow.primerExplainer(WatchItemId.USAGE_ACCESS)
        )
        assertNull(PermissionFlow.primerExplainer(null))
        assertNull(PermissionFlow.primerExplainer(WatchItemId.SHORTS_REELS))
    }

    @Test
    fun stepIndex_boardOrderAndNonFlowItems() {
        assertEquals(1, PermissionFlow.stepIndex(WatchItemId.USAGE_ACCESS))
        assertEquals(4, PermissionFlow.stepIndex(WatchItemId.ACCESSIBILITY))
        assertEquals(6, PermissionFlow.stepIndex(WatchItemId.BATTERY))
        assertEquals(0, PermissionFlow.stepIndex(WatchItemId.SHORTS_REELS))
        assertEquals(0, PermissionFlow.stepIndex(null))
    }

    // ------------------------------------------------------------------
    // The rebind, pinned by source
    // ------------------------------------------------------------------

    private fun sourceText(path: String): String =
        listOf(
            File("src/main/$path"),
            File("app/src/main/$path"),
            File("../app/src/main/$path")
        ).firstOrNull { it.exists() }?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")

    @Test
    fun primer_isBoundInMainActivity_andTheStoreBindsBesideWatchStore() {
        val main = sourceText("java/com/example/MainActivity.kt")
        assertTrue(main.contains("ShieldPermissionPrimerModal("))
        assertTrue(main.contains("PermissionFlowStore.bind(applicationContext)"))
        assertTrue(main.contains("PermissionFlowStore.recordStepPresented(step)"))
        // The historical note stays on record beside the rebind.
        assertTrue(main.contains("binding stays REMOVED"))
    }

    @Test
    fun primer_hasNoArmAnywayPath_anywhere() {
        // LR2-S1-18: the deleted primer's sin was arming with no
        // interception behind it. The rebuilt surface must carry no
        // arm-anyway affordance — no callback, no chip, no test tag.
        val overlays = sourceText(
            "java/com/example/ui/components/AirlockAndDisarmOverlays.kt"
        )
        val main = sourceText("java/com/example/MainActivity.kt")
        assertFalse(overlays.contains("onConfirmAndArmAnyway"))
        assertFalse(overlays.contains("confirm_arm_anyway"))
        assertFalse(overlays.contains("COMMIT & ARM"))
        assertFalse(main.contains("confirm_arm_anyway"))
        assertTrue(overlays.contains("fun ShieldPermissionPrimerModal("))
        assertTrue(overlays.contains("permission_primer_fix_button"))
    }
}
