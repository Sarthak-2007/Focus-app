package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ShoppingWarningStore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #129 D-2: the persisted half of the one-time SHOPPING
 * fail-closed warning — the flag itself. The owed-check over the
 * catalogue is pure and pinned in UnlistedDistraction129Test;
 * what lives here is the store's one promise: never dismissed
 * until he dismisses it, and dismissed forever after — across
 * store instances, which is to say across process restarts.
 *
 * PIN scope: PLUMBING — SharedPreferences persistence behaviour
 * under Robolectric; no rendering, no source-text claims.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ShoppingWarningStoreTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun freshStore_hasNotBeenDismissed_129() {
        assertFalse(ShoppingWarningStore(context).dismissed())
    }

    @Test
    fun dismiss_persistsAcrossStoreInstances_129() {
        ShoppingWarningStore(context).dismiss()
        // A new instance on the same phone (the next process, the
        // next open) reads the same fact: the warning was shown
        // and closed, once.
        assertTrue(ShoppingWarningStore(context).dismissed())
    }

    @Test
    fun dismiss_isSticky_129() {
        val store = ShoppingWarningStore(context)
        store.dismiss()
        store.dismiss()
        assertTrue(store.dismissed())
    }
}
