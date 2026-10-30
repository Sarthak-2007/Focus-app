package com.example.service

import android.content.Context

/**
 * Issue #78 (Audit R2, ipc-intents): persisted consumption of
 * intercept intents.
 *
 * Before this store, MainActivity "consumed" an intercept by
 * removeExtra() on the in-memory Intent copy alone. The platform
 * retains the LAUNCHING intent and redelivers it intact when the
 * activity is recreated after process death / task restore, so the
 * same stale Airlock fired again — for an intercept whose session
 * or tier may have long since moved on.
 *
 * Now every firing door mints a unique intercept id (see
 * [MonolithShieldService.EXTRA_INTERCEPT_ID]); MainActivity marks
 * the id consumed HERE — synchronously committed to disk — BEFORE
 * the Airlock is shown, and drops any delivery whose id is already
 * recorded. Intents that arrive with no id (a pre-cure firing, or a
 * foreign one — MainActivity is exported) get a content fingerprint
 * instead: a redelivery of the same stale extras is likewise
 * recognised, while the fingerprint is only ever minted by doors
 * that no longer exist in this build.
 *
 * Entries are timestamps + ids in one preference string, pruned to
 * the newest [MAX_ENTRIES] within [MAX_AGE_MS] on every write — an
 * id need only outlive the platform's willingness to redeliver its
 * intent, which is bounded by the task's own lifetime.
 */
object InterceptConsumptionStore {

    private const val PREFS_NAME = "monolith_intercept_consumption"
    private const val KEY_ENTRIES = "consumed_intercept_ids_v1"
    private const val MAX_ENTRIES = 64
    private const val MAX_AGE_MS = 48L * 60L * 60L * 1000L

    /** Fingerprint for a delivery carrying no intercept id: the
     *  four extras the Airlock would have been built from. */
    fun fingerprint(packageName: String, appName: String, badge: String, pathName: String): String =
        "legacy|$packageName|$appName|$badge|$pathName"

    internal fun parseEntries(raw: String?, nowMillis: Long): List<Pair<String, Long>> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split('\n').mapNotNull { line ->
            val tab = line.indexOf('\t')
            if (tab <= 0) return@mapNotNull null
            val stamp = line.substring(0, tab).toLongOrNull() ?: return@mapNotNull null
            val id = line.substring(tab + 1)
            if (id.isBlank() || nowMillis - stamp > MAX_AGE_MS) null else id to stamp
        }
    }

    internal fun encodeEntries(entries: List<Pair<String, Long>>): String =
        entries.joinToString("\n") { (id, stamp) -> "$stamp\t$id" }

    internal fun pruned(
        entries: List<Pair<String, Long>>,
        nowMillis: Long
    ): List<Pair<String, Long>> =
        entries.filter { (_, stamp) -> nowMillis - stamp <= MAX_AGE_MS }
            .sortedByDescending { (_, stamp) -> stamp }
            .take(MAX_ENTRIES)

    private fun readEntries(context: Context, nowMillis: Long): List<Pair<String, Long>> =
        parseEntries(
            context.applicationContext
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_ENTRIES, null),
            nowMillis
        )

    fun isConsumed(context: Context, interceptId: String): Boolean =
        readEntries(context, System.currentTimeMillis()).any { (id, _) -> id == interceptId }

    /**
     * Records [interceptId] as consumed, committed synchronously.
     * Returns the commit result: a caller that cannot persist the
     * consumption must NOT show the Airlock (fail closed) — showing
     * it unrecorded is exactly the resurrection this store exists
     * to prevent.
     */
    fun markConsumed(
        context: Context,
        interceptId: String,
        nowMillis: Long = System.currentTimeMillis()
    ): Boolean {
        val prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = readEntries(context, nowMillis).filterNot { (id, _) -> id == interceptId }
        val next = pruned(current + (interceptId to nowMillis), nowMillis)
        return prefs.edit().putString(KEY_ENTRIES, encodeEntries(next)).commit()
    }
}
