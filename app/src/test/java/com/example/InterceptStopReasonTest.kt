package com.example

import com.example.data.InterceptLogEntity
import com.example.data.InterceptStopReason
import com.example.data.countByStopReason
import com.example.data.interceptStopReasonFor
import com.example.data.interceptStopReasonFromLegacy
import com.example.service.InterceptPath
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Issue #785 (the #324 follow-on, sequenced after #341 PR-B):
 * the typed stop reason, proven pure — the stop-reason slice of
 * closed PR #599's taxonomy tests, ported to the landed house
 * names (PR #594's fromKindName / *Typed shape).
 *
 *  - The door matrix stamps only what a door proves: the guard,
 *    sniper and site doors name themselves; the tier doors are
 *    told apart by the session's birth; an unprovenanced door
 *    stamps UNKNOWN rather than guessing.
 *  - The legacy derivation reads only the row's own hard facts
 *    (the two reserved packages, the uninstall outcome).
 *  - The row accessor is column-first, derivation second — a
 *    stamped row reads its column even where its prose alone
 *    would say nothing.
 *  - The aggregation counts by type, never by prose.
 *
 * PIN scope: SOURCE.
 */
class InterceptStopReasonTest {

    private fun row(
        outcome: String,
        packageName: String = "com.video.app",
        stopReason: String = "UNKNOWN"
    ) = InterceptLogEntity(
        packageName = packageName,
        appName = "Video App",
        sessionIntention = "Deep Study",
        outcome = outcome,
        triggerReason = "UNSTATED",
        stopReason = stopReason
    )

    @Test
    fun fromKindName_everyReason_andGarbageDegradesToUnknown() {
        for (reason in InterceptStopReason.entries) {
            assertEquals(reason, InterceptStopReason.fromKindName(reason.name))
        }
        assertEquals(InterceptStopReason.UNKNOWN, InterceptStopReason.fromKindName("NOPE"))
        assertEquals(InterceptStopReason.UNKNOWN, InterceptStopReason.fromKindName(null))
        assertEquals(InterceptStopReason.UNKNOWN, InterceptStopReason.fromKindName(""))
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
        // An ordinary historical row proves no door at all — not
        // even an exposure-worded outcome on an ordinary package:
        // the package is the evidence, never the prose alone.
        assertEquals(
            InterceptStopReason.UNKNOWN,
            interceptStopReasonFromLegacy("com.video.app", "Stayed Focused")
        )
        assertEquals(
            InterceptStopReason.UNKNOWN,
            interceptStopReasonFromLegacy("com.video.app", "Open exposure")
        )
    }

    @Test
    fun entityAccessor_columnFirst_legacyFallback() {
        // A bare row (column UNKNOWN) on an ordinary package
        // proves nothing — UNKNOWN is the truthful read.
        assertEquals(
            InterceptStopReason.UNKNOWN,
            row(outcome = "Stayed Focused").stopReasonTyped
        )
        // A stamped row reads its column.
        assertEquals(
            InterceptStopReason.ROUTINE_WINDOW,
            row(outcome = "Stayed Focused", stopReason = "ROUTINE_WINDOW").stopReasonTyped
        )
        // A bare row on hard evidence derives it.
        assertEquals(
            InterceptStopReason.SESSION_DISARM,
            row(outcome = "Ended Early", packageName = "com.monolith.system").stopReasonTyped
        )
        assertEquals(
            InterceptStopReason.OPEN_EXPOSURE,
            row(outcome = "Open exposure", packageName = "com.monolith.exposure").stopReasonTyped
        )
        assertEquals(
            InterceptStopReason.UNINSTALL_GUARD,
            row(
                outcome = "Uninstall Attempt — Returned",
                packageName = "com.android.settings"
            ).stopReasonTyped
        )
    }

    @Test
    fun countByStopReason_countsByTypeNotProse() {
        val rows = listOf(
            row(outcome = "Stayed Focused", stopReason = "SESSION_LOCK"),
            row(outcome = "Blocked", stopReason = "SESSION_LOCK"),
            row(outcome = "Used 2m Peek", stopReason = "ROUTINE_WINDOW"),
            row(outcome = "Ended Early", packageName = "com.monolith.system"),
            row(outcome = "Stayed Focused")
        )
        assertEquals(
            mapOf(
                InterceptStopReason.SESSION_LOCK to 2,
                InterceptStopReason.ROUTINE_WINDOW to 1,
                InterceptStopReason.SESSION_DISARM to 1,
                InterceptStopReason.UNKNOWN to 1
            ),
            rows.countByStopReason()
        )
        assertEquals(emptyMap<InterceptStopReason, Int>(), emptyList<InterceptLogEntity>().countByStopReason())
    }
}
