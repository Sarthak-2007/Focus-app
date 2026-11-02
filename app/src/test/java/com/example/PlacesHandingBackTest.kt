package com.example

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.StrongroomHeldArrivalEntity
import com.example.data.StrongroomStore
import com.example.domain.StrongroomDay
import com.example.service.StrongroomEngineBridge
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId

/**
 * PLACES — THE HANDING BACK as a real act (B-13).
 *
 * FLOW-SEAT's whole-day walk (step 17) found it dead: the domain
 * prepared it, but no production path delivered it — WAITING
 * arrivals under a struck day stayed WAITING forever. These
 * tests prove the delivery at its two production homes: the
 * store verb itself, and the bridge's lift act, where delivery
 * runs as the firing's precondition before the plate is struck.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PlacesHandingBackTest {

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

    private val utc: ZoneId = ZoneId.of("UTC")
    private val dateIso = "2026-11-05"

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

    @Test
    fun storeDelivery_resolvesWaitingOnly_inArrivalOrder_idempotent() = runBlocking {
        val store = store()
        val t = 1_700_000_000_000L
        store.recordHeldArrival(dateIso, StrongroomHeldArrivalEntity.KIND_CALL, "+919000000001", t)
        val codeId = store.recordHeldArrival(
            dateIso, StrongroomHeldArrivalEntity.KIND_CODE, "482910", t + 1_000L
        )
        store.recordHeldArrival(dateIso, StrongroomHeldArrivalEntity.KIND_CALL, "+919000000002", t + 2_000L)
        // His own per-arrival choice on the code, made during the
        // day: delivery must not touch it.
        store.resolveHeldArrival(codeId.toInt(), taken = true)

        val delivered = store.deliverHandingBack(dateIso)

        // Each waited person once, in the order they first waited;
        // the taken code is not among them (it was never waiting
        // at the lift).
        assertEquals(
            listOf("+919000000001", "+919000000002"),
            delivered.waiting.map { it.displayName }
        )
        val rows = store.heldArrivals(dateIso)
        assertEquals(
            listOf(
                StrongroomHeldArrivalEntity.STATE_HANDED_BACK,
                StrongroomHeldArrivalEntity.STATE_TAKEN,
                StrongroomHeldArrivalEntity.STATE_HANDED_BACK
            ),
            rows.map { it.state }
        )
        assertTrue(rows.none { it.state == StrongroomHeldArrivalEntity.STATE_WAITING })

        // A second delivery finds nobody waiting and changes
        // nothing.
        val again = store.deliverHandingBack(dateIso)
        assertTrue(again.isEmpty)
        assertEquals(rows, store.heldArrivals(dateIso))
    }

    @Test
    fun bridgeLift_deliversTheHandingBackWithTheStrike() = runBlocking {
        Settings.Global.putInt(context.contentResolver, Settings.Global.BOOT_COUNT, 7)
        val store = store()
        store.sign(
            StrongroomDay.SigningRecord(
                filingDateIso = dateIso,
                lockedPackages = setOf("com.video.app"),
                allowedPackages = listOf("com.notes.app"),
                liftingTime = LocalTime.of(4, 1),
                sentence = null,
                breakLengthMinutes = 0,
                breakFiresAtOffsetMillis = -1L,
                allowanceMinutes = 30,
                gaps = emptyList()
            )
        )
        // Sealed at elapsed-now, wall 04:00 UTC for a 04:01
        // lifting: a 60-second span on the elapsed clock.
        val sealWall = java.time.LocalDateTime.of(2026, 11, 5, 4, 0)
            .atZone(utc).toInstant().toEpochMilli()
        val sealedAtElapsed = SystemClock.elapsedRealtime()
        val span = store.seal(dateIso, sealWall, sealedAtElapsed, 7, utc)
        assertTrue(span != null)

        store.recordHeldArrival(
            dateIso, StrongroomHeldArrivalEntity.KIND_CALL, "+919000000009",
            sealWall + 5_000L
        )

        // The span runs out on the elapsed clock; the lift fires.
        ShadowSystemClock.advanceBy(Duration.ofMillis(61_000L))
        val struck = StrongroomEngineBridge.onFiringWake(context, dateIso, "LIFT")
        assertTrue(struck)

        // The day stands struck — and its waited person stands
        // HANDED_BACK, delivered by the same act (the bridge
        // delivers before it strikes: B-13's order, in one wake).
        val row = store.day(dateIso)
        assertEquals(
            com.example.domain.StrongroomDay.SealState.LIFTED.name, row?.state
        )
        val arrivals = store.heldArrivals(dateIso)
        assertEquals(1, arrivals.size)
        assertEquals(
            StrongroomHeldArrivalEntity.STATE_HANDED_BACK, arrivals.single().state
        )
    }
}
