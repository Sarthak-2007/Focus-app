package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ReceiptDismissalStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R1 #40: the receipt dismissal is persisted. Before this
 * store the dismissal lived in MainActivity remember{} state and
 * process death resurrected receipts he had already put away.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ReceiptDismissalStoreTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        context.getSharedPreferences(
            ReceiptDismissalStore.PREFS_NAME, Context.MODE_PRIVATE
        ).edit().clear().commit()
    }

    @Test
    fun nothingDismissedReadsMinusOne() {
        assertEquals(-1, ReceiptDismissalStore(context).dismissedSessionId())
    }

    @Test
    fun dismiss_roundTripsAcrossStoreInstances_andReportsSuccess() {
        assertTrue(ReceiptDismissalStore(context).dismiss(77))
        // A fresh store instance (the process-death stand-in)
        // reads the same dismissal.
        assertEquals(77, ReceiptDismissalStore(context).dismissedSessionId())
        // A later dismissal replaces the earlier one: only the
        // standing receipt's dismissal matters.
        assertTrue(ReceiptDismissalStore(context).dismiss(78))
        assertEquals(78, ReceiptDismissalStore(context).dismissedSessionId())
    }
}
