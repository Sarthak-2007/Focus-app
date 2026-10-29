package com.example.ui

import com.example.data.FocusSessionEntity
import com.example.data.MonolithRepository
import com.example.data.RoutineEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * SHIELD'S STATUS SENTENCE (Shield audit R-5, merge item #1;
 * issue #7 builder pass, MONTEUR).
 *
 * The audit's headline charge: Shield printed SHUT as a state while
 * the code enforces it only during a live session, and the screen
 * never printed whether the wall is standing right now. This file
 * is the ONE derivation of that sentence. It is pure — every fact
 * arrives as a parameter — so the truth table is unit-tested
 * without Android, and no surface may invent its own version.
 *
 * WHAT COUNTS AS PROOF. A session row's `isActive` flag is a
 * database claim, not enforcement: the accessibility service
 * early-returns on it (MonolithAccessibilityService: a missing or
 * inactive row means no intercept ever fires), and the shield
 * service's loop stands down on it. So BLOCKING NOW is printed
 * only when BOTH enforcing witnesses attest THIS session — the
 * same two witnesses the arm cascade requires before it declares
 * t=0 (ArmProvingController's LiveArmWitnesses):
 *  - SHIELD: MonolithShieldService is running, its `protecting`
 *    flag has flipped (a full monitor iteration has run), and the
 *    flip was for this session's id;
 *  - ACCESS: MonolithAccessibilityService is bound and its own
 *    session collector has armed for this session's id.
 * Anything less is not dressed up: a running session without both
 * witnesses prints NOT BLOCKING with ENFORCEMENT NOT PROVEN, and a
 * session whose shield beat (the loop's persisted heartbeat on the
 * row, judged by the repository's own 10s gap law) has gone stale
 * prints NOT BLOCKING with SHIELD PROOF STALE. The witness mono
 * stamps themselves are flip/observation instants, never ages —
 * they are not aged out here; the beat is the staleness clock.
 */
enum class ShieldBlockingProof {
    /** Both witnesses attest this session; the beat is fresh. */
    LIVE,

    /** No session row, or the row is not active: nothing enforces. */
    NO_SESSION,

    /** A session runs but at least one witness cannot attest it. */
    UNPROVEN,

    /** Witnesses attest it, but the shield's beat is stale. */
    STALE
}

data class ShieldBlockingStatus(
    val proof: ShieldBlockingProof,
    /** The one printed line, exactly as Shield renders it. */
    val headline: String,
    /** Which witness(es) failed, for UNPROVEN; empty otherwise.
     *  A diagnostic word, never printed as the headline. */
    val missingProof: String = "",
    /** The routine id of the active session, when the active
     *  session is a routine session; null otherwise. This is the
     *  RUNNING NOW marker's source (see routineRunningNowSuffix):
     *  the session row itself, never window arithmetic. */
    val activeRoutineId: Int? = null
) {
    val isBlocking: Boolean get() = proof == ShieldBlockingProof.LIVE
}

private const val ROUTINE_PROFILE_PREFIX = "Routine: "

private fun hhmm(wallMillis: Long): String =
    SimpleDateFormat("HH:mm", Locale.US).format(Date(wallMillis))

/** The session's printed name: a routine session names its
 *  routine ("SLEEP SESSION"); a hand session names its loadout
 *  profile; a nameless row is just a session. Never invented. */
internal fun shieldSessionLabel(session: FocusSessionEntity): String {
    val profile = session.profileName.trim()
    return when {
        session.routineId != null || profile.startsWith(ROUTINE_PROFILE_PREFIX) -> {
            val name = profile.removePrefix(ROUTINE_PROFILE_PREFIX).trim()
            if (name.isNotEmpty()) "${name.uppercase(Locale.US)} SESSION" else "ROUTINE SESSION"
        }
        profile.isNotEmpty() -> profile.uppercase(Locale.US)
        session.intention.isNotBlank() -> "SESSION"
        else -> "SESSION"
    }
}

/** The session's end, from the session's own numbers only: a
 *  routine row ends at its frozen window end; a timed hand
 *  session at now + its whole-span remaining (the desk clock's
 *  derivation); a stopwatch session has no end to state. */
internal fun shieldSessionEndsAtMillis(
    session: FocusSessionEntity,
    nowWallMillis: Long
): Long? = when {
    session.windowEndMillis > session.windowStartMillis && session.windowEndMillis > 0L ->
        session.windowEndMillis
    session.isStopwatchMode -> null
    else -> nowWallMillis + session.spanRemainingSeconds * 1000L
}

fun computeShieldBlockingStatus(
    session: FocusSessionEntity?,
    shieldServiceRunning: Boolean,
    shieldProtecting: Boolean,
    shieldProtectingSessionId: Int,
    accessibilityBound: Boolean,
    accessibilityArmedSessionId: Int,
    nowWallMillis: Long
): ShieldBlockingStatus {
    if (session == null || !session.isActive) {
        return ShieldBlockingStatus(
            proof = ShieldBlockingProof.NO_SESSION,
            headline = "NOT BLOCKING — NO SESSION RUNNING"
        )
    }
    val label = shieldSessionLabel(session)
    val shieldProved = shieldServiceRunning && shieldProtecting &&
        shieldProtectingSessionId == session.id
    val accessProved = accessibilityBound &&
        accessibilityArmedSessionId == session.id
    if (!shieldProved || !accessProved) {
        val missing = when {
            !shieldProved && !accessProved -> "SHIELD + ACCESSIBILITY"
            !shieldProved -> "SHIELD"
            else -> "ACCESSIBILITY"
        }
        return ShieldBlockingStatus(
            proof = ShieldBlockingProof.UNPROVEN,
            headline = "NOT BLOCKING — $label RUNNING, ENFORCEMENT NOT PROVEN",
            missingProof = missing,
            activeRoutineId = session.routineId
        )
    }
    // The shield beat is the loop's persisted heartbeat on this
    // row; the repository's gap law (10s, five missed 2s beats) is
    // the staleness line, with the session's start as the
    // never-yet-beaten row's reference — the same reference
    // latchShieldGapIfStale uses.
    val beatRef = maxOf(session.shieldLastBeatWallMillis, session.startWallTimeMillis)
    if (nowWallMillis - beatRef > MonolithRepository.SHIELD_GAP_THRESHOLD_MILLIS) {
        return ShieldBlockingStatus(
            proof = ShieldBlockingProof.STALE,
            headline = "NOT BLOCKING — SHIELD PROOF STALE",
            activeRoutineId = session.routineId
        )
    }
    val endsAt = shieldSessionEndsAtMillis(session, nowWallMillis)
    val breakSuffix = if (session.isInSafeBreak) " · SAFE BREAK — PEEK APPS OPEN" else ""
    val headline = if (endsAt != null) {
        "BLOCKING NOW — $label · ENDS ${hhmm(endsAt)}$breakSuffix"
    } else {
        "BLOCKING NOW — $label$breakSuffix"
    }
    return ShieldBlockingStatus(
        proof = ShieldBlockingProof.LIVE,
        headline = headline,
        activeRoutineId = session.routineId
    )
}

/**
 * A routine row's cap word (Shield audit R-6): the cap prints the
 * row's armed truth, never a bare ON the scheduler will not honour.
 * A routine switched on with no days (daysMask == 0) can never
 * fire — RoutineSchedule.nextStartMillis returns null for it — so
 * it prints WON'T FIRE, not ON, and never silence.
 */
fun routineCapWord(enabled: Boolean, daysMask: Int): String = when {
    !enabled -> "OFF"
    daysMask == 0 -> "WON'T FIRE"
    else -> "ON"
}

/**
 * The RUNNING NOW marker for a routine row (Shield audit R-5,
 * derivation corrected per critic Salz C-5): the audit named
 * RoutineSchedule.isActiveNow, but that is window arithmetic —
 * it cannot distinguish a running session from a paused, held,
 * already-claimed, tombstoned, or not-yet-materialised window.
 * The truthful source is the session row itself: the marker
 * prints only when the ACTIVE session's frozen routineId names
 * this routine. A WON'T FIRE row never carries it (no session is
 * ever materialised for a daysMask == 0 routine).
 */
fun routineRunningNowSuffix(routine: RoutineEntity, activeRoutineId: Int?): String =
    if (activeRoutineId != null && activeRoutineId == routine.id) " · RUNNING NOW" else ""
