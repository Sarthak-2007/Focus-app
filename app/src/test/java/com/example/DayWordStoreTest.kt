package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DayWordStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R1 #37: DayWordStore's guarantees are now reportable and
 * enforced on both sides of the file.
 *
 *  - put() returns commit()'s verdict instead of discarding it —
 *    a write that did not reach disk must not present as declared.
 *  - put() validates the DATE as well as the word: a string that
 *    is not a real yyyy-MM-dd calendar date is refused.
 *  - all() validates on the way out: a stray or corrupted entry
 *    (bad key, out-of-list value) already sitting in the file is
 *    not served to the UI as a declared word.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DayWordStoreTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val prefs
        get() = context.getSharedPreferences("monolith_day_words", Context.MODE_PRIVATE)

    @Before
    fun setup() {
        prefs.edit().clear().commit()
    }

    @Test
    fun put_roundTrips_andReportsSuccess() {
        val store = DayWordStore(context)
        assertTrue(store.put("2026-10-26", "REST"))
        assertEquals(mapOf("2026-10-26" to "REST"), store.all())
        assertTrue(store.put("2026-10-26", null))
        assertTrue(store.all().isEmpty())
    }

    @Test
    fun put_refusesImpossibleAndMalformedDates() {
        val store = DayWordStore(context)
        assertFalse(store.put("2026-13-40", "REST"))
        assertFalse(store.put("2026-02-30", "ILL"))
        assertFalse(store.put("garbage", "REST"))
        assertFalse(store.put("2026-1-1", "REST"))
        assertFalse(store.put("", "REST"))
        assertTrue(store.all().isEmpty())
        assertTrue(prefs.all.isEmpty())
    }

    @Test
    fun put_recordsDeclarationZone_clearRemovesIt() {
        // Issue #152: the declaration's zone is witnessed beside
        // the word (never served as a word itself), and a clear
        // takes the witness with it.
        val store = DayWordStore(context)
        assertTrue(store.put("2026-10-26", "REST"))
        assertEquals(
            java.time.ZoneId.systemDefault().id,
            store.declaredZone("2026-10-26")
        )
        assertEquals(mapOf("2026-10-26" to "REST"), store.all())
        assertTrue(store.put("2026-10-26", null))
        assertEquals(null, store.declaredZone("2026-10-26"))
    }

    @Test
    fun put_refusesOutOfListWord() {
        val store = DayWordStore(context)
        assertFalse(store.put("2026-10-26", "TEST"))
        assertTrue(store.all().isEmpty())
    }

    @Test
    fun isExcuse_coversExactlyTheClosedList() {
        // Issue #137, F1: the amnesty keys off isExcuse — every
        // declarable word excuses, nothing else does.
        assertTrue(DayWordStore.isExcuse("ILL"))
        assertTrue(DayWordStore.isExcuse("REST"))
        assertTrue(DayWordStore.isExcuse("FAMILY"))
        assertFalse(DayWordStore.isExcuse("TEST"))
        assertFalse(DayWordStore.isExcuse(null))
        assertEquals(DayWordStore.WORDS.toSet(), DayWordStore.EXCUSE_WORDS)
    }

    @Test
    fun all_filtersStrayAndCorruptedEntries() {
        // Poison the file directly, as legacy/corruption would.
        prefs.edit()
            .putString("2026-10-26", "REST")
            .putString("2026-10-27", "TEST")
            .putString("not-a-date", "ILL")
            .putString("2026-13-40", "FAMILY")
            .putInt("2026-10-28", 42)
            .commit()
        val store = DayWordStore(context)
        assertEquals(mapOf("2026-10-26" to "REST"), store.all())
    }
}
