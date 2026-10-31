package com.example

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.PermissionStepLogic
import com.example.data.PermissionStepStore
import com.example.data.permissionExplainer
import com.example.service.WatchItemId
import com.example.ui.components.ShieldPermissionPrimerModal
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #324, finding 4 (build half, relabelled: primer
 * re-creation + binding + persisted step state).
 *
 * Store + logic:
 *  - a fresh install has not begun and holds no in-flight step;
 *  - a begun trip PERSISTS across store instances (process
 *    death): a new store over the same prefs reads the same
 *    in-flight step, and the sequence resumes on it;
 *  - steps advance in board order and never skip an ungranted
 *    step; a granted-then-revoked grant reopens only its own
 *    step; an all-granted board is complete and the standing
 *    step is null.
 *
 * Primer (render):
 *  - renders over an incomplete step, never over a completed
 *    flow; shows the standing step's own explainer and board
 *    name; FIX hands the standing step — and only it — to the
 *    host; and the LR2-S1-18 pin: NO control anywhere in the
 *    primer arms or commits (no "ARM", no "ANYWAY", no
 *    "CONTINUE" text exists to tap).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w400dp-h800dp")
class PermissionStepIssue324Test {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val prefs
        get() = context.getSharedPreferences("monolith_permission_steps", Context.MODE_PRIVATE)

    @Before
    fun setup() {
        prefs.edit().clear().commit()
    }

    private fun grantsOf(vararg granted: WatchItemId): Map<WatchItemId, Boolean> =
        WatchItemId.PERMISSIONS.associateWith { it in granted }

    // ---------- store ----------

    @Test
    fun store_freshInstall_hasNotBegun_andNoInFlight() {
        val store = PermissionStepStore(context)
        assertFalse(store.hasBegun())
        assertNull(store.inFlightItem())
        assertTrue(store.attemptedItems().isEmpty())
    }

    @Test
    fun store_begunTrip_survivesNewStoreInstance_resumeReadsSameStep() {
        PermissionStepStore(context).beginTrip(WatchItemId.ACCESSIBILITY)
        // A new instance over the same prefs = the post-death read.
        val reopened = PermissionStepStore(context)
        assertTrue(reopened.hasBegun())
        assertEquals(WatchItemId.ACCESSIBILITY, reopened.inFlightItem())
        assertEquals(setOf(WatchItemId.ACCESSIBILITY), reopened.attemptedItems())
        // And the logic resumes ON that step while it is ungranted,
        // even though cheaper steps are also ungranted.
        val step = PermissionStepLogic.currentStep(grantsOf(), reopened.inFlightItem())
        assertEquals(WatchItemId.ACCESSIBILITY, step)
    }

    @Test
    fun store_clearInFlight_keepsAttemptedHistory() {
        val store = PermissionStepStore(context)
        store.beginTrip(WatchItemId.OVERLAY)
        store.clearInFlight()
        assertNull(store.inFlightItem())
        assertEquals(setOf(WatchItemId.OVERLAY), store.attemptedItems())
    }

    // ---------- logic ----------

    @Test
    fun logic_advancesInBoardOrder_neverSkipsAnUngrantedStep() {
        assertEquals(
            WatchItemId.USAGE_ACCESS,
            PermissionStepLogic.currentStep(grantsOf(), null)
        )
        assertEquals(
            WatchItemId.OVERLAY,
            PermissionStepLogic.currentStep(grantsOf(WatchItemId.USAGE_ACCESS), null)
        )
        assertEquals(
            WatchItemId.BATTERY,
            PermissionStepLogic.currentStep(
                grantsOf(
                    WatchItemId.USAGE_ACCESS, WatchItemId.OVERLAY,
                    WatchItemId.NOTIFICATION_LISTENER, WatchItemId.ACCESSIBILITY,
                    WatchItemId.DEVICE_ADMIN
                ),
                null
            )
        )
    }

    @Test
    fun logic_grantedThenRevoked_reopensOnlyThatStep() {
        val allButAdmin = grantsOf(
            WatchItemId.USAGE_ACCESS, WatchItemId.OVERLAY,
            WatchItemId.NOTIFICATION_LISTENER, WatchItemId.ACCESSIBILITY,
            WatchItemId.BATTERY
        )
        assertEquals(WatchItemId.DEVICE_ADMIN, PermissionStepLogic.currentStep(allButAdmin, null))
        assertFalse(PermissionStepLogic.isComplete(allButAdmin))
    }

    @Test
    fun logic_allGranted_isComplete_andNoStepStands() {
        val all = grantsOf(*WatchItemId.PERMISSIONS.toTypedArray())
        assertTrue(PermissionStepLogic.isComplete(all))
        assertNull(PermissionStepLogic.currentStep(all, WatchItemId.BATTERY))
    }

    @Test
    fun explainers_arePerPermission_andAdminClaimsNoUninstallProtection() {
        val texts = WatchItemId.PERMISSIONS.map { permissionExplainer(it) }
        assertEquals(texts.size, texts.toSet().size)
        val admin = permissionExplainer(WatchItemId.DEVICE_ADMIN)
        assertTrue(admin.contains("does not prevent uninstalling"))
        assertNotEquals("", permissionExplainer(WatchItemId.ACCESSIBILITY))
    }

    // ---------- primer render ----------

    private fun setPrimer(
        step: WatchItemId?,
        grants: Map<WatchItemId, Boolean>,
        onFix: (WatchItemId) -> Unit = {}
    ) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                ShieldPermissionPrimerModal(
                    visible = true,
                    currentStep = step,
                    grants = grants,
                    onFixClick = onFix,
                    onDismiss = {}
                )
            }
        }
    }

    @Test
    fun primer_rendersStandingStep_withItsOwnExplainer() {
        setPrimer(WatchItemId.OVERLAY, grantsOf(WatchItemId.USAGE_ACCESS))
        composeRule.onNodeWithTag("permission_primer").assertIsDisplayed()
        composeRule.onNodeWithText(permissionExplainer(WatchItemId.OVERLAY)).assertIsDisplayed()
        composeRule.onNodeWithText("STEP 2 OF 6 — OVERLAY").assertIsDisplayed()
    }

    @Test
    fun primer_completeFlow_rendersNothing() {
        setPrimer(null, grantsOf(*WatchItemId.PERMISSIONS.toTypedArray()))
        composeRule.onNodeWithTag("permission_primer").assertDoesNotExist()
    }

    @Test
    fun primer_fix_handsOnlyTheStandingStepToTheHost() {
        var fixed: WatchItemId? = null
        setPrimer(WatchItemId.ACCESSIBILITY, grantsOf(WatchItemId.USAGE_ACCESS)) { fixed = it }
        composeRule.onNodeWithTag("permission_primer_fix").performClick()
        assertEquals(WatchItemId.ACCESSIBILITY, fixed)
    }

    @Test
    fun primer_hasNoArmAnywayPath() {
        setPrimer(WatchItemId.USAGE_ACCESS, grantsOf())
        // LR2-S1-18: the deleted modal's bypass must not exist in
        // the re-creation — no arm/commit/continue-anyway control.
        composeRule.onNodeWithText("COMMIT & ARM ANYWAY").assertDoesNotExist()
        composeRule.onNodeWithText("ARM ANYWAY").assertDoesNotExist()
        composeRule.onNodeWithText("CONTINUE ANYWAY").assertDoesNotExist()
        composeRule.onNodeWithText("SKIP").assertDoesNotExist()
    }
}
