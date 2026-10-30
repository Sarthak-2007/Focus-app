package com.example.data

import kotlin.math.abs

/**
 * Tamper-route verdicts (Audit R2 · tamper-routes, issue #81).
 *
 * Pure arithmetic / classification only — no Android, no IO — so every
 * verdict here is unit-testable and the Android-facing callers stay
 * thin adapters. Each object answers one finding of the audit.
 */

/**
 * Finding 3 — Safe Mode boots freeze the session clock for free.
 *
 * Across a reboot the session's elapsed is rebuilt from monotonic
 * pieces (prior accumulated + seconds since this boot). Wall time
 * that passed while NO monotonic clock was running for the app —
 * the phone sitting in Safe Mode (where Monolith cannot run at all),
 * or powered off — is invisible to that rebuild: the old code flagged
 * tamper on HMAC failure only, so hours in Safe Mode neither advanced
 * the session nor registered anywhere.
 *
 * The verdict: unexplained wall time beyond [TOLERANCE_SECONDS] is
 * tamper evidence, exactly as the same-boot branch already treats a
 * >90 s wall/monotonic drift. The tolerance is generous (a normal
 * reboot, even a slow one, is a couple of minutes) so an ordinary
 * restart mid-session stays clean; a Safe-Mode excursion long enough
 * to be worth anything is far outside it.
 */
object RebootDrift {
    const val TOLERANCE_SECONDS = 600L

    /**
     * @param wallElapsedSec wall seconds since the session's start.
     * @param monotonicElapsedSec the reboot-branch rebuild: prior
     *   accumulated seconds + monotonic seconds since this boot.
     * @param hmacValid the session row's signature check.
     */
    fun isTamper(
        wallElapsedSec: Long,
        monotonicElapsedSec: Long,
        hmacValid: Boolean
    ): Boolean {
        if (!hmacValid) return true
        return abs(wallElapsedSec - monotonicElapsedSec) > TOLERANCE_SECONDS
    }
}

/**
 * Finding 5 — `pm disable` / component-disable routes are invisible.
 *
 * The classification half: given each critical component's
 * PackageManager enabled-state, the disabled ones, by name. The
 * states mirror PackageManager's constants (kept as raw ints so this
 * stays Android-free): 0 DEFAULT, 1 ENABLED, 2 DISABLED,
 * 3 DISABLED_USER, 4 DISABLED_UNTIL_USED. Anything that is not
 * DEFAULT/ENABLED is a component someone switched off.
 */
object ComponentGuard {
    fun disabledCritical(states: Map<String, Int>): List<String> =
        states.filterValues { it != 0 && it != 1 }.keys.sorted()

    fun allEnabled(states: Map<String, Int>): Boolean =
        disabledCritical(states).isEmpty()
}

/**
 * Finding 1 — clear-data is a total, undetectable reset.
 *
 * The witness payload half. A tiny record of the live session is kept
 * OUTSIDE the app sandbox (see ClearDataWitnessStore): `pm clear`
 * wipes the sandbox, not the user's Documents. On a later fresh seed,
 * a surviving witness whose session id has no row in the rebuilt
 * database is proof a session existed and was destroyed — the
 * resurrection verdict below. The payload is deliberately plain: the
 * adversary here is the device owner, and the point is that the app
 * RECORDS the destruction, not that it wins a court case.
 */
object ClearDataWitness {
    const val PREFIX = "MNL1"

    fun encode(sessionId: Int, birthWallMillis: Long): String =
        "$PREFIX|$sessionId|$birthWallMillis"

    /** The witnessed session id, or null for anything malformed. */
    fun parseSessionId(payload: String?): Int? {
        if (payload == null) return null
        val parts = payload.trim().split("|")
        if (parts.size != 3 || parts[0] != PREFIX) return null
        val id = parts[1].toIntOrNull() ?: return null
        val birth = parts[2].toLongOrNull() ?: return null
        if (id <= 0 || birth <= 0L) return null
        return id
    }

    /**
     * Resurrection ⇔ a witness survives AND the database being
     * seeded holds no session rows at all. (While any history exists
     * the witness is just the current/last session's own record and
     * proves nothing.)
     */
    fun isResurrection(witnessSessionId: Int?, sessionRowCount: Int): Boolean =
        witnessSessionId != null && sessionRowCount == 0
}
