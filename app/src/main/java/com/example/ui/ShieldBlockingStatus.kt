package com.example.ui

import com.example.data.FocusSessionEntity
import com.example.data.MonolithRepository
import com.example.data.RoutineEntity
import com.example.service.UsageAccess
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
 * only when THREE witnesses attest THIS session, each fresh
 * under its OWN aging law — no law is borrowed across witnesses:
 *  - SHIELD: MonolithShieldService is running, its `protecting`
 *    flag has flipped, and the flip was for this session's id.
 *    The flip lands only after a full monitor iteration has
 *    completed — blocked set built, foreground queried, and the
 *    intercept launched if one was due; the 2s cadence sleep is
 *    not iteration work. Freshness: the loop's persisted
 *    heartbeat on the row, aged on the WALL clock by the
 *    repository's own 10s gap law (SHIELD_GAP_THRESHOLD_MILLIS).
 *  - ACCESS: MonolithAccessibilityService is bound and its own
 *    session collector has armed for this session's id — and
 *    its pipeline is alive: `lastRootMono`, stamped whenever a
 *    root window is acquired (accessibility events, plus the
 *    service's own 30s armed self-check), aged on the MONOTONIC
 *    clock by ACCESS_PROOF_FRESH_MILLIS below.
 *    The armed stamp alone proves only that a database row was
 *    observed once; without a fresh root stamp the pipeline may
 *    have died at arming and this sentence would lie (Nagel N-2).
 *  - USAGE: usage access is ALLOWED. The shield door's only
 *    intercept path (MonolithRepository.queryForegroundBlockedApp)
 *    returns null unconditionally without it, so a shield that
 *    beats and flips over a revoked grant intercepts nothing —
 *    BLOCKING NOW over that door was the N-1 lie. The signal is
 *    sampled live by the ViewModel in the same frame as the
 *    other two witnesses.
 * Anything less is not dressed up: the headline names the one
 * failed witness, ranked USAGE, then ACCESS, then SHIELD — the
 * grant only the user can give leads, the app's own machinery
 * last — and re-derives on the next read, so fixing the named
 * failure surfaces the next one if it exists.
 */
enum class ShieldBlockingProof {
    /** All three witnesses attest this session, each fresh
     *  under its own aging law. */
    LIVE,

    /** No session row, or the row is not active: nothing enforces. */
    NO_SESSION,

    /** A session runs but at least one witness cannot attest it. */
    UNPROVEN,

    /** Witnesses attest it, but the shield's beat is stale. */
    STALE,

    /** Accessibility attests this session, but its pipeline
     *  stamp (lastRootMono) is stale under its own law. */
    ACCESS_STALE
}

/**
 * The ACCESS leg's own aging law: how old the accessibility
 * service's last acquired-root stamp (`lastRootMono`, monotonic)
 * may be before the pipeline is no longer proven alive.
 *
 * 90s = three consecutive missed 30s self-checks. While a
 * session is armed, the accessibility service re-acquires a
 * root window every 30s (ACCESS_SELF_CHECK_MILLIS in
 * MonolithAccessibilityService) and re-stamps lastRootMono —
 * so this stamp's cadence is GUARANTEED by the service itself,
 * never by the user's motion: a student sitting still on one
 * page reading (the posture this app exists for) keeps the
 * proof fresh, and a service that cannot acquire a root three
 * beats running has honestly lost its verdict. (Before the
 * self-check, the stamp refreshed only on window events and
 * this law false-staled exactly that reader — KRITIK-1R
 * attack 2; the threshold was not the bug, the missing
 * heartbeat was.) This law is NOT the warrant's 45s monotonic
 * law (that ages permission MEASUREMENTS for the Deck, a
 * different subject) and NOT the shield's 10s wall law (that
 * ages a persisted heartbeat on a different stamp, clock, and
 * cadence). Each witness is aged on the clock its stamp is
 * written in, by a threshold its own cadence justifies.
 */
const val ACCESS_PROOF_FRESH_MILLIS: Long = 90_000L

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
            // Strip the prefix BEFORE the trim: "Routine: " trimmed
            // first becomes "Routine:", the "Routine: " prefix no
            // longer matches, and a blank-named routine session
            // printed "ROUTINE: SESSION" instead of falling back
            // (PRUEF-3's full-log read, run 41021933859).
            val name = session.profileName.removePrefix(ROUTINE_PROFILE_PREFIX)
                .removePrefix("Routine:").trim()
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
    usageAccess: UsageAccess,
    accessibilityLastRootMono: Long,
    nowWallMillis: Long,
    nowMonoMillis: Long
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
    val usageProved = usageAccess == UsageAccess.ALLOWED
    // Every failed witness, for the diagnostic field; the printed
    // headline below names only the highest-ranked one.
    val missing = listOfNotNull(
        if (shieldProved) null else "SHIELD",
        if (accessProved) null else "ACCESSIBILITY",
        if (usageProved) null else "USAGE ACCESS"
    ).joinToString(" + ")
    // activeRoutineId stays null on this and every other failure
    // return: it feeds the routine row's RUNNING NOW marker, and
    // that marker under a NOT BLOCKING headline would contradict
    // the sentence (DIAGNOSE-1R census, Oct 30). Only the LIVE
    // return at the bottom carries it.
    fun unproven(headline: String) = ShieldBlockingStatus(
        proof = ShieldBlockingProof.UNPROVEN,
        headline = headline,
        missingProof = missing
    )
    // Ranked USAGE → ACCESS → SHIELD (see the header): the grant
    // only the user can give leads; the app's own machinery last.
    if (!usageProved) {
        return unproven("NOT BLOCKING — NOT PROVEN: THE APP CAN'T SEE WHICH APP IS OPEN")
    }
    if (!accessProved) {
        return unproven("NOT BLOCKING — NOT PROVEN: NO FRESH SIGN THE APP IS WATCHING YOUR SCREEN")
    }
    // The ACCESS leg's own staleness clock: the armed stamp proves
    // a database row was observed; only a fresh root stamp proves
    // the event pipeline processed anything recently. A stamp of
    // 0 means no root window was ever acquired this residency,
    // and a stamp in the future (negative age — a clock-source
    // mismatch, never a sign of life) is no proof either.
    val accessAge = nowMonoMillis - accessibilityLastRootMono
    if (accessibilityLastRootMono <= 0L || accessAge < 0L ||
        accessAge > ACCESS_PROOF_FRESH_MILLIS
    ) {
        return ShieldBlockingStatus(
            proof = ShieldBlockingProof.ACCESS_STALE,
            headline = "NOT BLOCKING — NOT PROVEN: NO FRESH SIGN THE APP IS WATCHING YOUR SCREEN",
            // The diagnostic names every failed witness, even
            // though the headline names only the ranked one.
            missingProof = listOfNotNull(
                if (shieldProved) null else "SHIELD",
                "ACCESSIBILITY"
            ).joinToString(" + ")
        )
    }
    if (!shieldProved) {
        return unproven("NOT BLOCKING — THE APP'S CHECKS HAVEN'T STARTED")
    }
    // The shield beat is the loop's persisted heartbeat on this
    // row; the repository's gap law (10s, five missed 2s beats) is
    // the staleness line, with the session's start as the
    // never-yet-beaten row's reference — the same reference
    // latchShieldGapIfStale uses.
    val beatRef = maxOf(session.shieldLastBeatWallMillis, session.startWallTimeMillis)
    // A beat in the future (negative age) is a wall clock that
    // travelled backwards after the beat was written: its age is
    // unmeasurable, so it proves nothing — stale, not fresh. The
    // loop re-beats within 2s of any honest clock correction, so
    // strictness costs a flicker; leniency would hand a
    // clock-travel cheat a LIVE print over a dead loop.
    val beatAge = nowWallMillis - beatRef
    if (beatAge < 0L || beatAge > MonolithRepository.SHIELD_GAP_THRESHOLD_MILLIS) {
        return ShieldBlockingStatus(
            proof = ShieldBlockingProof.STALE,
            headline = "NOT BLOCKING — THE SHIELD'S OWN CHECKS HAVE STOPPED"
        )
    }
    val endsAt = shieldSessionEndsAtMillis(session, nowWallMillis)
    val endsSuffix = if (endsAt != null) " · ENDS ${hhmm(endsAt)}" else ""
    // In a safe break both doors carve out PEEK by design, so the
    // break line leads with what is open — BLOCKING NOW as the
    // lead would be false for exactly the apps it names (N-5).
    val headline = if (session.isInSafeBreak) {
        "ON A BREAK — PEEK APPS OPEN, THE REST STILL BLOCKED$endsSuffix"
    } else {
        "BLOCKING NOW — $label$endsSuffix"
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
 * ever materialised for a daysMask == 0 routine). The derivation
 * carries activeRoutineId ONLY on its LIVE return — every failure
 * branch leaves it null — so this marker can never print under a
 * NOT BLOCKING headline.
 */
fun routineRunningNowSuffix(routine: RoutineEntity, activeRoutineId: Int?): String =
    if (activeRoutineId != null && activeRoutineId == routine.id) " · RUNNING NOW" else ""
