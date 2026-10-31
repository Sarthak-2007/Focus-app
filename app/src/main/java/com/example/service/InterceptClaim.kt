package com.example.service

import android.os.SystemClock
import java.util.concurrent.ConcurrentHashMap

/**
 * Audit R6 #340 F63: the cross-door EPISODE CLAIM.
 *
 * Two independent doors can fire on the same opening — the shield
 * service's 2 s poll and the accessibility service's event path —
 * and neither kept any per-episode state, so one reach could be
 * intercepted twice and the accessibility door could re-fire
 * across the burst of content-changed events a single foreground
 * episode generates. Freedom's answer is a single global 500 ms
 * scanning pause, during which a DIFFERENT blocked app opened in
 * the gap is not scanned at all; this claim is per-package and
 * episode-scoped instead, so duplicates for the same opening are
 * impossible while every other package stays fully armed.
 *
 * The first door to fire claims the package's episode. The claim
 * is voided when the episode ends — the package leaves the
 * foreground (the accessibility door releases every other claim
 * on a window change, and releases all when the Airlock itself
 * takes the foreground) — or when the terms change under it: a
 * peek lease granted or a tier re-ruled explicitly releases the
 * claim, so a sanctioned opening is never treated as a duplicate
 * offence.
 */
object InterceptClaim {

    private val claims = ConcurrentHashMap<String, Long>()

    /** A claim older than this is an orphan (its episode's end
     *  was never observed — e.g. the accessibility door is dead)
     *  and may be taken over. No live episode is silenced longer
     *  than this by a stale claim. */
    const val EPISODE_CLAIM_TTL_MS = 120_000L

    /** True when this door wins the episode and may fire. */
    fun tryClaim(packageName: String): Boolean {
        val now = SystemClock.elapsedRealtime()
        while (true) {
            val existing = claims[packageName]
            if (existing == null) {
                if (claims.putIfAbsent(packageName, now) == null) return true
            } else if (now - existing > EPISODE_CLAIM_TTL_MS) {
                if (claims.replace(packageName, existing, now)) return true
            } else {
                return false
            }
        }
    }

    fun release(packageName: String) {
        claims.remove(packageName)
    }

    /** A window change moved the foreground to [packageName]:
     *  every other package's episode is over. */
    fun releaseExcept(packageName: String) {
        claims.keys.filter { it != packageName }.forEach { claims.remove(it) }
    }

    fun releaseAll() {
        claims.clear()
    }

    fun isClaimed(packageName: String): Boolean = claims.containsKey(packageName)

    /** Test seam only. */
    fun resetForTest() {
        claims.clear()
    }
}
