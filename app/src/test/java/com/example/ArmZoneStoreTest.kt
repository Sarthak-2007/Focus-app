package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ArmZoneStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #152 + its reopened residue: the zone witnesses.
 *
 * The arm slot (landed with #285) records the zone a session was
 * armed in, keyed by the arm instant. The end slot (residue cure)
 * mirrors it exactly for the zone a session ENDED in, keyed by
 * the end instant — before it existed, ReceiptSheet derived
 * ENDED at print time from the render zone. Both slots obey the
 * same law: a slot answers only for the instant it was keyed by,
 * so a stale slot can never be mistaken for another session's
 * zone, and the two slots never disturb each other.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ArmZoneStoreTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val prefs
        get() = context.getSharedPreferences("monolith_arm_zone", Context.MODE_PRIVATE)

    @Before
    fun setup() {
        prefs.edit().clear().commit()
    }

    @Test
    fun armSlot_roundTrips_keyedByArmInstant() {
        val store = ArmZoneStore(context)
        store.put(1_000L, "Asia/Kolkata")
        assertEquals("Asia/Kolkata", store.zoneFor(1_000L))
        assertNull("a different session's instant must not read this slot",
            store.zoneFor(2_000L))
    }

    @Test
    fun endSlot_roundTrips_keyedByEndInstant() {
        val store = ArmZoneStore(context)
        store.putEnd(5_000L, "Europe/London")
        assertEquals("Europe/London", store.endZoneFor(5_000L))
        assertNull("a different session's end must not read this slot",
            store.endZoneFor(6_000L))
    }

    @Test
    fun endSlot_neverAnswersForAnArmInstant_andViceVersa() {
        val store = ArmZoneStore(context)
        store.put(1_000L, "Asia/Kolkata")
        store.putEnd(5_000L, "Europe/London")
        assertEquals("Asia/Kolkata", store.zoneFor(1_000L))
        assertEquals("Europe/London", store.endZoneFor(5_000L))
        assertNull(store.endZoneFor(1_000L))
        assertNull(store.zoneFor(5_000L))
    }

    @Test
    fun slots_startEmpty() {
        val store = ArmZoneStore(context)
        assertNull(store.zoneFor(1_000L))
        assertNull(store.endZoneFor(1_000L))
    }
}
