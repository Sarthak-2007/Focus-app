package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.LoadoutStore
import com.example.data.SmartLoadout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R1 #38: LoadoutStore's contract after the hardening.
 *
 *  - Absent, Present and Corrupt are three distinguishable read
 *    states — a corrupt stored value no longer collapses silently
 *    to STUDY indistinguishable from never-picked.
 *  - A type-corrupted preference (non-String under the key) is
 *    handled, not a startup crash: getString sits inside the guard.
 *  - put() returns commit()'s verdict.
 *  - CAP_ORDER is the one seat order: index-of and look-up are
 *    exact inverses over it.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LoadoutStoreTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val prefs
        get() = context.getSharedPreferences("monolith_loadout", Context.MODE_PRIVATE)

    @Before
    fun setup() {
        prefs.edit().clear().commit()
    }

    @Test
    fun absentReadsAbsent_andCapRestsOnStudyDefault() {
        val store = LoadoutStore(context)
        assertEquals(LoadoutStore.Stored.Absent, store.readStored())
        assertNull(store.lastHandLoadout())
        assertEquals(0, store.capIndex())
    }

    @Test
    fun put_roundTrips_andReportsSuccess() {
        val store = LoadoutStore(context)
        assertTrue(store.put(SmartLoadout.SLEEP))
        assertEquals(
            LoadoutStore.Stored.Present(SmartLoadout.SLEEP),
            store.readStored()
        )
        assertEquals(SmartLoadout.SLEEP, store.lastHandLoadout())
        assertEquals(2, store.capIndex())
    }

    @Test
    fun corruptName_readsCorrupt_heals_andIsDistinguishableFromAbsent() {
        prefs.edit().putString("last_hand_loadout", "MEGA_STUDY").commit()
        val store = LoadoutStore(context)

        // The loss is reported, not silently folded into "never picked".
        assertEquals(
            LoadoutStore.Stored.Corrupt("MEGA_STUDY"),
            store.readStored()
        )
        // Healed: the corrupt entry is gone, and only NOW does the
        // store read Absent.
        assertEquals(LoadoutStore.Stored.Absent, store.readStored())
        assertNull(store.lastHandLoadout())
    }

    @Test
    fun typeCorruptedPreference_isHandledNotACrash() {
        prefs.edit().putInt("last_hand_loadout", 7).commit()
        val store = LoadoutStore(context)
        // Before the fix this ClassCastException escaped the read
        // and crashed ViewModel construction.
        assertTrue(store.readStored() is LoadoutStore.Stored.Corrupt)
        assertEquals(LoadoutStore.Stored.Absent, store.readStored())
        assertEquals(0, store.capIndex())
    }

    @Test
    fun capOrder_isTheOneMapping_inversesAgree() {
        assertEquals(
            listOf(SmartLoadout.STUDY, SmartLoadout.WORK_SPRINT, SmartLoadout.SLEEP),
            LoadoutStore.CAP_ORDER
        )
        for ((index, loadout) in LoadoutStore.CAP_ORDER.withIndex()) {
            assertEquals(index, LoadoutStore.capIndexOf(loadout))
            assertEquals(loadout, LoadoutStore.loadoutForCapIndex(index))
        }
        // The screen's historical `else` semantics survive verbatim:
        // any out-of-range index seats the last seat, SLEEP.
        assertEquals(SmartLoadout.SLEEP, LoadoutStore.loadoutForCapIndex(99))
    }
}
