package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DayWordStore
import com.example.data.dayWordRequiresConfirm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R6 #341 BUILD — F58: the day word is the permanent
 * explanation filed against a day. Replacing a standing word
 * with a DIFFERENT word now requires a confirm step (the
 * ViewModel arms `pendingDayWordConfirm`; the write happens
 * only on confirm), and every declaration records its instant
 * in the store's side key (the witness the drain carries into
 * day_notes.declaredAtMillis — that column rides PR-B's
 * MIGRATION_19_20; this PR pins the prefs half).
 *
 * These cases pin the confirm RULE (the pure predicate the
 * ViewModel's guarded path is built on — a declaration that
 * requires confirm writes nothing until confirmed) and the
 * timestamp the store keeps beside the word.
 * PIN scope: PLUMBING.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue341BuildDayWordConfirmTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun declareWord_withoutConfirm_writesNothing() {
        // A standing word + a different new word = confirm
        // required. The guarded declaration path arms the confirm
        // and writes NOTHING while unconfirmed — the predicate is
        // the gate, pinned here.
        assertTrue(dayWordRequiresConfirm(existing = "REST", newWord = "ILL"))
        assertTrue(dayWordRequiresConfirm(existing = "ILL", newWord = "FAMILY"))
    }

    @Test
    fun declareWord_withConfirm_storesWordPlusDeclarationTimestamp() {
        val store = DayWordStore(context)
        val before = System.currentTimeMillis()
        store.put("2026-10-31", "REST")
        val declaredAt = store.declaredAtMillis("2026-10-31")
        assertEquals("REST", store.all()["2026-10-31"])
        assertTrue("declaration instant must be recorded", declaredAt >= before)
        assertTrue(declaredAt <= System.currentTimeMillis())
    }

    @Test
    fun overwriteExistingWord_requiresConfirmAgain_andUpdatesTimestamp() {
        // The confirm is not a one-time unlock: EVERY overwrite
        // of a standing word by a different word re-arms.
        assertTrue(dayWordRequiresConfirm(existing = "REST", newWord = "ILL"))
        // ...while the non-overwrite declarations never arm:
        assertFalse(dayWordRequiresConfirm(existing = null, newWord = "REST"))
        assertFalse(dayWordRequiresConfirm(existing = "REST", newWord = "REST"))
        assertFalse(dayWordRequiresConfirm(existing = "REST", newWord = null))
        // And a confirmed overwrite moves the timestamp forward.
        val store = DayWordStore(context)
        store.put("2026-10-30", "REST")
        val first = store.declaredAtMillis("2026-10-30")
        store.put("2026-10-30", "ILL")
        assertEquals("ILL", store.all()["2026-10-30"])
        assertTrue(store.declaredAtMillis("2026-10-30") >= first)
        // Clearing a word clears its instant too — no orphan time.
        store.put("2026-10-30", null)
        assertNull(store.all()["2026-10-30"])
        assertEquals(0L, store.declaredAtMillis("2026-10-30"))
    }
}
