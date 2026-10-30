package com.example

import com.example.data.DistractionTriggerReason
import com.example.data.InterceptLogEntity
import com.example.data.InterceptOutcome
import com.example.data.InterceptOutcomeType
import com.example.data.InterceptStopReason
import com.example.data.countByOutcomeType
import com.example.data.countByStopReason
import com.example.data.interceptStopReasonFor
import com.example.data.interceptStopReasonFromLegacy
import com.example.data.isBlockedInterceptOutcome
import com.example.data.triggerReasonFromRow
import com.example.service.InterceptPath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #324, Finding 3 — BUILD half: the typed intercept-event
 * taxonomy, proven pure.
 *
 *  - Every outcome spelling a writer in the tree produces (plus
 *    the harness spellings) classifies to exactly one type, and
 *    unmatched prose classifies to UNKNOWN — never a guess.
 *  - The typed blocked-allowlist agrees with the legacy string
 *    test ([isBlockedInterceptOutcome] / [InterceptOutcome]) on
 *    every spelling, so receipts read the same verdict from the
 *    column as they ever did from the string.
 *  - Stop reasons stamp only what a door proves; the legacy-row
 *    derivation and the reserved packages are pinned.
 *  - The aggregation helpers count by type, not by prose.
 */
class InterceptEventTypesTest {

    private fun row(
        outcome: String,
        packageName: String = "com.video.app",
        triggerReason: String = "Habit",
        outcomeType: String = "UNKNOWN",
        stopReason: String = "UNKNOWN",
        triggerReasonType: String = "UNKNOWN"
    ) = InterceptLogEntity(
        packageName = packageName,
        appName = "Video App",
        sessionIntention = "Deep Study",
        outcome = outcome,
        triggerReason = triggerReason,
        outcomeType = outcomeType,
        stopReason = stopReason,
        triggerReasonType = triggerReasonType
    )

    @Test
    fun fromLegacy_classifiesEveryWriterSpelling() {
        val expected = mapOf(
            "Intercepted" to InterceptOutcomeType.INTERCEPTED,
            "Blocked" to InterceptOutcomeType.BLOCKED,
            "BLOCKED" to InterceptOutcomeType.BLOCKED,
            "Stayed Focused" to InterceptOutcomeType.STAYED_FOCUSED,
            "Split Into 10m Micro-Step" to InterceptOutcomeType.SPLIT_INTO_MICRO_STEP,
            "Used 2m Peek" to InterceptOutcomeType.USED_PEEK,
            "Left Airlock Without Choosing" to InterceptOutcomeType.LEFT_WITHOUT_CHOOSING,
            "Opened Launcher" to InterceptOutcomeType.OPENED_LAUNCHER,
            "Uninstall Attempt — Returned" to InterceptOutcomeType.UNINSTALL_ATTEMPT_RETURNED,
            "Open exposure" to InterceptOutcomeType.OPEN_EXPOSURE
        )
        for ((spelling, type) in expected) {
            assertEquals(spelling, type, InterceptOutcomeType.fromLegacy(spelling))
            // The derivation is case- and trim-tolerant, exactly as
            // the legacy string test always was.
            assertEquals(spelling, type, InterceptOutcomeType.fromLegacy("  ${spelling.lowercase()}  "))
        }
    }

    @Test
    fun fromLegacy_unmatchedProseIsUnknown_neverAGuess() {
        assertEquals(InterceptOutcomeType.UNKNOWN, InterceptOutcomeType.fromLegacy("Ended Early"))
        assertEquals(InterceptOutcomeType.UNKNOWN, InterceptOutcomeType.fromLegacy("Emergency Break"))
        assertEquals(InterceptOutcomeType.UNKNOWN, InterceptOutcomeType.fromLegacy("Something else"))
        assertEquals(InterceptOutcomeType.UNKNOWN, InterceptOutcomeType.fromLegacy(""))
        assertEquals(InterceptOutcomeType.UNKNOWN, InterceptOutcomeType.fromLegacy(null))
    }

    @Test
    fun blockedAllowlist_typedAndLegacyAgreeOnEverySpelling() {
        val spellings = listOf(
            "Blocked", "BLOCKED", "blocked", "Stayed Focused", "stayed focused",
            "Split Into 10m Micro-Step", "Used 2m Peek", "Intercepted",
            "Left Airlock Without Choosing", "Opened Launcher",
            "Uninstall Attempt — Returned", "Open exposure", "Ended Early", "nonsense"
        )
        for (spelling in spellings) {
            assertEquals(
                spelling,
                isBlockedInterceptOutcome(spelling),
                InterceptOutcomeType.fromLegacy(spelling).isBlocked
            )
            assertEquals(
                spelling,
                InterceptOutcome.BLOCKED_OUTCOMES.any { it.equals(spelling.trim(), ignoreCase = true) },
                InterceptOutcomeType.fromLegacy(spelling).isBlocked
            )
        }
        // The deliberate exclusions, named: a peek is a let-through,
        // and "Left Airlock Without Choosing" records no choice, so
        // neither may ever count as a deflection.
        assertFalse(InterceptOutcomeType.USED_PEEK.isBlocked)
        assertFalse(InterceptOutcomeType.LEFT_WITHOUT_CHOOSING.isBlocked)
        assertTrue(InterceptOutcomeType.STAYED_FOCUSED.isBlocked)
    }

    @Test
    fun storedRoundTrip_everyType_andGarbageDegradesToUnknown() {
        for (type in InterceptOutcomeType.entries) {
            assertEquals(type, InterceptOutcomeType.fromStored(InterceptOutcomeType.storedOf(type)))
        }
        assertEquals(InterceptOutcomeType.UNKNOWN, InterceptOutcomeType.fromStored("NOPE"))
        assertEquals(InterceptOutcomeType.UNKNOWN, InterceptOutcomeType.fromStored(null))
        for (reason in InterceptStopReason.entries) {
            assertEquals(reason, InterceptStopReason.fromStored(reason.name))
        }
        assertEquals(InterceptStopReason.UNKNOWN, InterceptStopReason.fromStored("NOPE"))
    }

    @Test
    fun stopReasonFor_doorAndSessionBirth() {
        assertEquals(
            InterceptStopReason.SESSION_LOCK,
            interceptStopReasonFor(InterceptPath.APP_TIER, sessionIsRoutine = false)
        )
        assertEquals(
            InterceptStopReason.ROUTINE_WINDOW,
            interceptStopReasonFor(InterceptPath.APP_TIER, sessionIsRoutine = true)
        )
        assertEquals(
            InterceptStopReason.ROUTINE_WINDOW,
            interceptStopReasonFor(InterceptPath.SHIELD_APP, sessionIsRoutine = true)
        )
        assertEquals(
            InterceptStopReason.SESSION_LOCK,
            interceptStopReasonFor(InterceptPath.SHIELD_APP, sessionIsRoutine = false)
        )
        assertEquals(
            InterceptStopReason.UNINSTALL_GUARD,
            interceptStopReasonFor(InterceptPath.UNINSTALL_GUARD, sessionIsRoutine = true)
        )
        assertEquals(
            InterceptStopReason.SNIPER,
            interceptStopReasonFor(InterceptPath.SNIPER, sessionIsRoutine = false)
        )
        assertEquals(
            InterceptStopReason.SITE,
            interceptStopReasonFor(InterceptPath.SITE, sessionIsRoutine = false)
        )
        assertEquals(
            InterceptStopReason.UNKNOWN,
            interceptStopReasonFor(InterceptPath.UNKNOWN, sessionIsRoutine = false)
        )
    }

    @Test
    fun stopReasonFromLegacy_onlyWhatTheRowProves() {
        assertEquals(
            InterceptStopReason.SESSION_DISARM,
            interceptStopReasonFromLegacy("com.monolith.system", "Ended Early")
        )
        assertEquals(
            InterceptStopReason.OPEN_EXPOSURE,
            interceptStopReasonFromLegacy("com.monolith.exposure", "Open exposure")
        )
        assertEquals(
            InterceptStopReason.UNINSTALL_GUARD,
            interceptStopReasonFromLegacy("com.android.settings", "Uninstall Attempt — Returned")
        )
        // An ordinary historical row proves no door at all.
        assertEquals(
            InterceptStopReason.UNKNOWN,
            interceptStopReasonFromLegacy("com.video.app", "Stayed Focused")
        )
    }

    @Test
    fun entityAccessors_columnFirst_legacyFallback() {
        // A bare (pre-v16-shaped) row classifies from its strings.
        val bare = row(outcome = "Stayed Focused", triggerReason = "Stuck")
        assertEquals(InterceptOutcomeType.STAYED_FOCUSED, bare.outcomeKind)
        assertEquals(InterceptStopReason.UNKNOWN, bare.stopReasonKind)
        assertEquals(DistractionTriggerReason.STUCK_ON_PROBLEM, bare.triggerReasonKind)

        // A stamped row reads its columns, even where a legacy
        // string alone would classify differently.
        val stamped = row(
            outcome = "Stayed Focused",
            outcomeType = "USED_PEEK",
            stopReason = "ROUTINE_WINDOW",
            triggerReasonType = "NEED_INFO"
        )
        assertEquals(InterceptOutcomeType.USED_PEEK, stamped.outcomeKind)
        assertEquals(InterceptStopReason.ROUTINE_WINDOW, stamped.stopReasonKind)
        assertEquals(DistractionTriggerReason.NEED_INFO, stamped.triggerReasonKind)

        // Reserved packages never state a trigger reason, whatever
        // rides in the legacy column.
        assertNull(row(outcome = "Open exposure", packageName = "com.monolith.exposure", triggerReason = "com.video.app").triggerReasonKind)
        assertNull(row(outcome = "Ended Early", packageName = "com.monolith.system").triggerReasonKind)
        assertEquals(
            InterceptOutcomeType.OPEN_EXPOSURE,
            row(outcome = "Open exposure", packageName = "com.monolith.exposure").outcomeKind
        )
    }

    @Test
    fun triggerReasonFromRow_storedWins_tagFallback_reservedPackagesNull() {
        assertEquals(
            DistractionTriggerReason.BORED_OR_TIRED,
            triggerReasonFromRow("BORED_OR_TIRED", "Habit", "com.video.app")
        )
        assertEquals(
            DistractionTriggerReason.MUSCLE_MEMORY,
            triggerReasonFromRow("UNKNOWN", "Habit", "com.video.app")
        )
        assertNull(triggerReasonFromRow("UNKNOWN", "com.video.app", "com.monolith.exposure"))
        assertNull(triggerReasonFromRow("MUSCLE_MEMORY", "Habit", "com.monolith.system"))
    }

    @Test
    fun aggregation_countsByTypeNotProse() {
        val rows = listOf(
            row(outcome = "Intercepted"),
            row(outcome = "Stayed Focused"),
            row(outcome = "stayed focused"),
            row(outcome = "Used 2m Peek", outcomeType = "USED_PEEK", stopReason = "SESSION_LOCK"),
            row(outcome = "Open exposure", packageName = "com.monolith.exposure")
        )
        val byOutcome = rows.countByOutcomeType()
        assertEquals(1, byOutcome[InterceptOutcomeType.INTERCEPTED])
        assertEquals(2, byOutcome[InterceptOutcomeType.STAYED_FOCUSED])
        assertEquals(1, byOutcome[InterceptOutcomeType.USED_PEEK])
        assertEquals(1, byOutcome[InterceptOutcomeType.OPEN_EXPOSURE])
        val byStop = rows.countByStopReason()
        assertEquals(1, byStop[InterceptStopReason.SESSION_LOCK])
        assertEquals(1, byStop[InterceptStopReason.OPEN_EXPOSURE])
        assertEquals(3, byStop[InterceptStopReason.UNKNOWN])
    }
}
