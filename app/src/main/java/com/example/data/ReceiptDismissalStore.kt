package com.example.data

import android.content.Context

/**
 * The receipt sheet's dismissal, persisted (Audit R1 #40).
 *
 * The dismissal used to live only in MainActivity `remember{}`
 * host state: process death reset it to -1 and the standing
 * receipt — already dismissed by hand — resurrected on the Deck
 * via rehydration, as if it had never been put away. The receipt
 * itself is durable (the claim stamps it on the session row);
 * its dismissal now is too: the session id of the last dismissed
 * receipt, in its own tiny preferences file, written with
 * commit() so the put-away is durable before the sheet animates
 * out. -1 = nothing dismissed.
 */
class ReceiptDismissalStore(context: Context) {

    private val prefs =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun dismissedSessionId(): Int = prefs.getInt(KEY_DISMISSED_SESSION, -1)

    /** Record [sessionId]'s receipt as dismissed. Returns
     *  commit()'s verdict — the caller may keep its in-memory
     *  dismissal either way (the sheet is already gone), but a
     *  false here means the put-away will not survive process
     *  death, and that is the caller's fact to hold. */
    fun dismiss(sessionId: Int): Boolean =
        prefs.edit().putInt(KEY_DISMISSED_SESSION, sessionId).commit()

    companion object {
        const val PREFS_NAME = "monolith_receipt_dismissal"
        const val KEY_DISMISSED_SESSION = "dismissed_receipt_session_id"
    }
}
