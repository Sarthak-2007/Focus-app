package com.example.data

import android.content.Context

/**
 * Issue #13, A-10(ii)/(iii) — the receipt's ONE persisted
 * filed-marker, beside the standing pointer.
 *
 * The standing receipt's pointer lives in guard_settings (the
 * receiptSequence / lastReceiptSessionId columns, Slice E Z1).
 * A DB column for the filed fact would be a schema change, and
 * this merge carries none (DB stays v12) — so the marker lives
 * in this small store instead, written with commit() (the G-02
 * standard: durable before the UI acts on it), exactly like
 * UndoDeleteStore beside the tables it serves.
 *
 * The marker names ONE session: the slip that must never stand.
 * Two writers, one mechanism (A-10(iii)):
 *  - BIRTH: publishReceipt files a routine-born slip the moment
 *    it is claimed — it is born filed, never a first surface.
 *  - THE HAND: dismissing ANY standing receipt writes the same
 *    fact, so process death cannot re-stand a slip he already
 *    put away (the V2 disease, one surface over).
 * Rehydration and the standing composition both refuse the
 * marked session. The №, the row, the record all survive —
 * filed is a posture, not a deletion.
 *
 * SINGLE-SLOT INVARIANT (Wave-2, Salz C-21 — named, not
 * admitted silently): this store holds ONE session id, sound
 * only because the standing receipt pointer is itself single
 * (one slip can stand at a time, so one filed marker covers
 * the standing question). If the standing pointer ever
 * becomes plural, this store must become a set in the same
 * change. Ordering law: writers mark BEFORE the № claim
 * commits (see MonolithRepository.publishReceipt), so the
 * two stores' crash window fails filed, never standing.
 */
class ReceiptFiledStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** File [sessionId]'s receipt: it will never stand again in
     *  this or any later process. Synchronous (commit()). */
    fun markFiled(sessionId: Int) {
        prefs.edit().putInt(KEY_FILED_SESSION_ID, sessionId).commit()
    }

    /** The filed session's id, or 0 when nothing is filed. */
    fun filedSessionId(): Int = prefs.getInt(KEY_FILED_SESSION_ID, 0)

    fun isFiled(sessionId: Int): Boolean = sessionId > 0 && filedSessionId() == sessionId

    companion object {
        private const val PREFS_NAME = "receipt_filed_store"
        private const val KEY_FILED_SESSION_ID = "filed_receipt_session_id"
    }
}
