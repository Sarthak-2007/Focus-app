package com.example

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.StrongroomStore
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomRouting
import com.example.service.StrongroomEngineBridge
import com.example.service.StrongroomLiftReceiver
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalTime

/**
 * P2 (BUILD-SR-3) — StrongroomEngineBridge against the production
 * database (Robolectric): the knock path punches and opens
 * nothing, the boot path strikes the ajar truth, held-call
 * verdicts pass through, and the StrongroomLiftReceiver's firing
 * contract (action + extras) is the one the bridge arms.
 *
 * The INTACT boot path (shield re-raise) and seal activation's
 * service start are NOT exercised here — they start a foreground
 * service, a device-behaviour surface that stands
 * UNPROVEN-ON-DEVICE (Audit A G-7); the paths exercised are the
 * store-and-routing truths.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StrongroomEngineBridgeP2Test {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val storageContext: Context
        get() {
            val appCtx = context.applicationContext
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                appCtx.createDeviceProtectedStorageContext() ?: appCtx
            } else {
                appCtx
            }
        }

    @Before
    fun coldStart() {
        MonolithDatabase.closeAndResetForTests()
        storageContext.deleteDatabase("monolith_tactile_db")
        context.deleteDatabase("monolith_tactile_db")
    }

    @After
    fun teardown() {
        MonolithDatabase.closeAndResetForTests()
        storageContext.deleteDatabase("monolith_tactile_db")
        context.deleteDatabase("monolith_tactile_db")
    }

    private fun store(): StrongroomStore =
        StrongroomStore(com.example.AppContainer.dao(context))

    private suspend fun seedSealedDay(dateIso: String, bootCount: Int) {
        val store = store()
        store.sign(
            StrongroomDay.SigningRecord(
                filingDateIso = dateIso,
                lockedPackages = setOf("com.video.app"),
                liftingTime = LocalTime.of(23, 59),
                sentence = null,
                breakLengthMinutes = 0,
                breakFiresAtOffsetMillis = -1L,
                allowanceMinutes = 30,
                gaps = emptyList()
            )
        )
        val span = store.seal(
            dateIso,
            sealedAtWallMillis = System.currentTimeMillis(),
            sealedAtElapsedMillis = SystemClock.elapsedRealtime(),
            bootCount = bootCount
        )
        assertTrue(span != null)
    }

    @Test
    fun reach_withNoSealedDay_isNotSealed() = runBlocking {
        assertEquals(
            StrongroomRouting.ReachDecision.NOT_SEALED,
            StrongroomEngineBridge.onLockedAppReach(context, "com.video.app", emptySet())
        )
    }

    @Test
    fun reach_lockedKnocksAndPunches_allowedPassesToAntechamber() = runBlocking {
        seedSealedDay("2026-11-03", bootCount = StrongroomEngineBridge.bootCount(context))
        assertEquals(
            StrongroomRouting.ReachDecision.KNOCK,
            StrongroomEngineBridge.onLockedAppReach(context, "com.video.app", setOf("com.notes.app"))
        )
        assertEquals(
            StrongroomRouting.ReachDecision.ANTECHAMBER,
            StrongroomEngineBridge.onLockedAppReach(context, "com.notes.app", setOf("com.notes.app"))
        )
        // Exactly one punch: the knock, and nothing else.
        val punches = store().punches("2026-11-03")
        assertEquals(1, punches.size)
        assertEquals(StrongroomDay.PunchKind.REACH, punches.single().kind)
        assertEquals("com.video.app", punches.single().packageName)
    }

    @Test
    fun boot_withChangedBoot_strikesTheAjarTruth() = runBlocking {
        // Seal under a boot the device no longer stands in: the
        // bridge's boot read sees a different count than the seal's.
        seedSealedDay("2026-11-04", bootCount = StrongroomEngineBridge.bootCount(context) + 7)
        StrongroomEngineBridge.onBoot(context)
        val row = store().day("2026-11-04")!!
        assertEquals("AJAR", row.state)
        assertTrue(row.struckBracketGapMillis > 0L)
    }

    @Test
    fun heldCallVerdicts_passThrough() {
        assertEquals(
            StrongroomRouting.CallDecision.RING_THROUGH,
            StrongroomEngineBridge.callDecision(isContact = true)
        )
        assertEquals(
            StrongroomRouting.CallDecision.HELD_QUIETLY,
            StrongroomEngineBridge.callDecision(isContact = false)
        )
    }

    @Test
    fun liftReceiverContract_actionAndExtrasAreTheArmedOnes() {
        val intent = Intent().apply {
            action = StrongroomLiftReceiver.ACTION_FIRING
            putExtra(StrongroomLiftReceiver.EXTRA_DATE_ISO, "2026-11-03")
            putExtra(StrongroomLiftReceiver.EXTRA_FIRING_KIND, "LIFT")
        }
        assertEquals("com.example.action.STRONGROOM_FIRING", intent.action)
        assertEquals("2026-11-03", intent.getStringExtra(StrongroomLiftReceiver.EXTRA_DATE_ISO))
        assertEquals("LIFT", intent.getStringExtra(StrongroomLiftReceiver.EXTRA_FIRING_KIND))
        // A wake for a day that does not stand strikes nothing.
        assertFalse(runBlocking { StrongroomEngineBridge.onFiringWake(context, "1999-01-01", "LIFT") })
    }
}
