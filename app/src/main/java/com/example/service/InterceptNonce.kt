package com.example.service

import android.content.Context
import java.util.UUID

/**
 * Issue #68 F5, Tier 2 — provenance for intercept intents.
 *
 * MainActivity is exported (it must be: it is the LAUNCHER
 * activity), so any installed app can send it the intercept
 * extras and fabricate an airlock. The two lawful senders — the
 * shield service and the accessibility service — are inside this
 * app; before launching, each mints a single-use nonce into
 * app-private preferences and puts it on the intent. The
 * activity honours intercept extras only with a nonce that
 * matches the stored one and is at most [MAX_AGE_MILLIS] old.
 * A foreign app cannot mint one (the store is app-private), a
 * captured one cannot be replayed (a match burns it), and a
 * mismatched guess does not burn the real one.
 *
 * The nonce proves the intent was minted inside this app; it
 * does not assert the claim is true — truth is revalidated
 * against the session and the app rows in
 * `MonolithViewModel.triggerAirlockIntercept` (Tier 1).
 */
object InterceptNonce {

    private const val PREFS_NAME = "intercept_nonce"
    private const val KEY_TOKEN = "token"
    private const val KEY_AT_MILLIS = "at_millis"

    const val MAX_AGE_MILLIS = 30_000L

    /** Mint a fresh nonce, replacing any unconsumed one, and
     *  return it for the intent. `commit()` (not `apply()`): the
     *  token must be durable before the intent can be delivered,
     *  even if the process dies in between. Mints are rare — one
     *  per intercept — so the synchronous write costs nothing. */
    fun mint(context: Context): String {
        val token = UUID.randomUUID().toString()
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_AT_MILLIS, System.currentTimeMillis())
            .commit()
        return token
    }

    /** Consume a presented nonce. True only when it matches the
     *  stored token and is fresh; a match burns the token whether
     *  fresh or stale, so it can never be presented twice. A
     *  mismatch leaves the stored token untouched — a junk intent
     *  must not be able to burn a lawful intercept in flight. */
    fun consume(
        context: Context,
        presented: String?,
        nowMillis: Long = System.currentTimeMillis()
    ): Boolean {
        if (presented.isNullOrBlank()) return false
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getString(KEY_TOKEN, null) ?: return false
        if (stored != presented) return false
        val mintedAt = prefs.getLong(KEY_AT_MILLIS, 0L)
        prefs.edit().remove(KEY_TOKEN).remove(KEY_AT_MILLIS).commit()
        return nowMillis - mintedAt in 0..MAX_AGE_MILLIS
    }
}
