package com.example

import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.data.InterceptLogEntity
import com.example.service.InterceptPath
import com.example.ui.PeekWithheld
import com.example.ui.components.breakAppointmentText
import com.example.ui.components.derivedSecondsUntil
import com.example.ui.components.doorMemoryLineText
import com.example.ui.components.peekGateLineText
import com.example.ui.nextBreakInstantMillis
import com.example.ui.peekOfferFor
import com.example.ui.peekWithheldFor
import com.example.ui.sessionInterceptCount
import com.example.ui.sessionIsLive
import com.example.ui.taperWallSeconds
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #11 — the Held Door fix pack's pure derivations. Every
 * test fails on the pre-fix behaviour it names:
 *
 *  FIX-5b — prints derive from anchored ends, ceiling, never a
 *           locally ticked counter.
 *  FIX-6  — the gate line's M:SS form.
 *  FIX-8  — the taper (5 + this session's booked, cap 10), the
 *           memory line's THIS SESSION scope, the errand kernel's
 *           derived break instant.
 *  P-4b   — the count comes from the session-window log, never
 *           the lifetime counter; P-5 homecomings excluded.
 *  P-5    — a homecoming pays the taper's floor, no increment.
 */
class HeldDoorFixTest {

    // -----------------------------------------------------------------
    // FIX-5b — anchored derivation.
    // -----------------------------------------------------------------

    @Test
    fun derivedSecondsUntilCeilsFromTheAnchor() {
        val now = 500_000L
        assertEquals(2, derivedSecondsUntil(now + 1_500L, now))
        assertEquals(1, derivedSecondsUntil(now + 1_000L, now))
        assertEquals(1, derivedSecondsUntil(now + 1L, now))
        assertEquals(10, derivedSecondsUntil(now + 10_000L, now))
        // End reached, end passed, end never anchored: zero —
        // and a cooldown print at zero is unrepresentable because
        // the overlay holds no cooldown tenant at zero.
        assertEquals(0, derivedSecondsUntil(now, now))
        assertEquals(0, derivedSecondsUntil(now - 1L, now))
        assertEquals(0, derivedSecondsUntil(0L, now))
    }

    // -----------------------------------------------------------------
    // FIX-6 — the gate line.
    // -----------------------------------------------------------------

    @Test
    fun gateLineNamesWhatItGatesInMinuteSecondForm() {
        assertEquals("PEEK OPENS IN 0:05", peekGateLineText(5))
        assertEquals("PEEK OPENS IN 0:10", peekGateLineText(10))
        assertEquals("PEEK OPENS IN 1:05", peekGateLineText(65))
        assertEquals("PEEK OPENS IN 0:00", peekGateLineText(0))
    }

    // -----------------------------------------------------------------
    // FIX-8 — the taper and the memory line.
    // -----------------------------------------------------------------

    @Test
    fun taperIsFivePlusThisSessionsBookedCappedAtTen() {
        assertEquals(5, taperWallSeconds(0, justExpiredLease = false))
        assertEquals(6, taperWallSeconds(1, justExpiredLease = false))
        assertEquals(7, taperWallSeconds(2, justExpiredLease = false))
        assertEquals(10, taperWallSeconds(5, justExpiredLease = false))
        assertEquals(10, taperWallSeconds(9, justExpiredLease = false))
        assertEquals(10, taperWallSeconds(400, justExpiredLease = false))
    }

    @Test
    fun homecomingPaysTheFloorWithNoIncrement() {
        // P-5: the lease's own expiry echo is not a new reach —
        // the wall is the floor whatever the count says.
        assertEquals(5, taperWallSeconds(0, justExpiredLease = true))
        assertEquals(5, taperWallSeconds(4, justExpiredLease = true))
    }

    @Test
    fun memoryLineCountsThisSessionAndStaysSilentOnTheFirstVisit() {
        assertNull(doorMemoryLineText(0))
        assertEquals("SECOND TIME THIS SESSION", doorMemoryLineText(1))
        assertEquals("THIRD TIME THIS SESSION", doorMemoryLineText(2))
        assertEquals("TENTH TIME THIS SESSION", doorMemoryLineText(9))
        assertEquals("11TH TIME THIS SESSION", doorMemoryLineText(10))
    }

    // -----------------------------------------------------------------
    // P-4b — the count's source.
    // -----------------------------------------------------------------

    private fun log(
        pkg: String,
        atMillis: Long,
        outcome: String = "RETURNED AT DOOR"
    ) = InterceptLogEntity(
        timestampMillis = atMillis,
        packageName = pkg,
        appName = "App",
        sessionIntention = "Study",
        outcome = outcome
    )

    @Test
    fun sessionCountReadsOnlyThisSessionsWindowForThisPackage() {
        val start = 1_000_000L
        val logs = listOf(
            log("com.video", start - 1L), // last session's reach
            log("com.video", start),
            log("com.video", start + 60_000L),
            log("com.other", start + 30_000L), // another package
            log("com.video", start + 120_000L, outcome = "Used 2m Peek")
        )
        assertEquals(3, sessionInterceptCount(logs, start, "com.video"))
        assertEquals(1, sessionInterceptCount(logs, start, "com.other"))
        assertEquals(0, sessionInterceptCount(logs, start, "com.absent"))
        assertEquals(0, sessionInterceptCount(emptyList(), start, "com.video"))
    }

    @Test
    fun sessionCountExcludesHomecomingBookings() {
        // P-5: a "Peek Expired" booking is the lease's echo,
        // never a reach — the taper must not punish it.
        val start = 1_000_000L
        val logs = listOf(
            log("com.video", start, outcome = "Peek Expired"),
            log("com.video", start + 1_000L, outcome = "Peek Expired")
        )
        assertEquals(0, sessionInterceptCount(logs, start, "com.video"))
    }

    // -----------------------------------------------------------------
    // FIX-8 — the errand's kernel: the next break, derived.
    // -----------------------------------------------------------------

    private fun session(
        blocks: Int = 3,
        blockIndex: Int = 1,
        elapsed: Int = 600,
        target: Int = 1500,
        inBreak: Boolean = false,
        breakRemaining: Int = 0,
        stopwatch: Boolean = false
    ) = FocusSessionEntity(
        intention = "Study",
        profileName = "Default",
        targetDurationSeconds = target,
        elapsedSeconds = elapsed,
        startWallTimeMillis = 0L,
        startElapsedRealtimeMillis = 0L,
        isStopwatchMode = stopwatch,
        totalStudyBlocks = blocks,
        currentBlockIndex = blockIndex,
        isInSafeBreak = inBreak,
        safeBreakSecondsRemaining = breakRemaining,
        isActive = true,
        completedSuccessfully = false
    )

    @Test
    fun nextBreakIsTheCurrentBlocksEndWhenABlockFollows() {
        val now = 5_000_000L
        // Block 1 of 3, 600 of 1500 elapsed: the break starts
        // when this block ends, 900 seconds from now.
        assertEquals(now + 900_000L, nextBreakInstantMillis(session(), now))
    }

    @Test
    fun nextBreakIsNullWhenNoBreakFollows() {
        val now = 5_000_000L
        // Last block: the session ends, no break is owed.
        assertNull(nextBreakInstantMillis(session(blockIndex = 3), now))
        // Single-block session: no schedule holds a break.
        assertNull(nextBreakInstantMillis(session(blocks = 1, blockIndex = 1), now))
        // Stopwatch: no blocks, no break appointment.
        assertNull(nextBreakInstantMillis(session(stopwatch = true), now))
    }

    @Test
    fun nextBreakMidBreakIsTheFollowingBlocksEnd() {
        val now = 5_000_000L
        // In the break before block 2 of 3, 300s of break left:
        // the NEXT break follows block 2 — break remainder plus
        // the whole block target from now.
        assertEquals(
            now + (300 + 1500) * 1000L,
            nextBreakInstantMillis(session(inBreak = true, breakRemaining = 300), now)
        )
        // In the break after block 2 of 3 (the break before the
        // LAST block), no break follows that block — null.
        assertNull(
            nextBreakInstantMillis(
                session(blockIndex = 2, inBreak = true, breakRemaining = 300),
                now
            )
        )
    }

    @Test
    fun breakAppointmentPrintsTheDerivedInstant() {
        val instant = 1_700_000_000_000L
        val hhmm = SimpleDateFormat("HH:mm", Locale.US).format(Date(instant))
        assertEquals("BREAK AT $hhmm OPENS THIS APP", breakAppointmentText(instant))
    }

    // -----------------------------------------------------------------
    // N-2 (Tor round 11) — the grant must not be session-blind.
    // -----------------------------------------------------------------

    @Test
    fun sessionLivenessMatrix() {
        // No session, or an inactive one: dead.
        assertFalse(sessionIsLive(null))
        assertFalse(sessionIsLive(session().copy(isActive = false)))
        // Timed session with span remaining: live.
        assertTrue(sessionIsLive(session(blocks = 1, elapsed = 600, target = 1500)))
        // Timed session whose span has run out: dead, even while
        // the row still says active — the door must not print the
        // past end instant or sell a peek into the next session.
        assertFalse(sessionIsLive(session(blocks = 1, elapsed = 1500, target = 1500)))
        assertFalse(sessionIsLive(session(blocks = 1, elapsed = 9999, target = 1500)))
        // Stopwatch: no span end exists; active is the whole truth.
        assertTrue(sessionIsLive(session(stopwatch = true, elapsed = 99999)))
        assertFalse(sessionIsLive(session(stopwatch = true).copy(isActive = false)))
    }

    @Test
    fun deadSessionYieldsNoOfferEvenWhenEveryOtherGatePasses() {
        val peekApp = BlockedAppEntity(
            packageName = "com.video",
            appName = "Video",
            category = "Social",
            isBlocked = true,
            blockTier = AppBlockTier.PEEK_2M.name
        )
        // The pre-fix call had no session input at all and granted.
        assertTrue(
            peekOfferFor(InterceptPath.APP_TIER, peekApp, 0, false, sessionLive = true)
        )
        assertFalse(
            peekOfferFor(InterceptPath.APP_TIER, peekApp, 0, false, sessionLive = false)
        )
        assertFalse(
            peekOfferFor(InterceptPath.SHIELD_APP, peekApp, 0, false, sessionLive = false)
        )
    }

    // -----------------------------------------------------------------
    // C-07 (Tor round 7) — a live lease outranks its own cooldown.
    // -----------------------------------------------------------------

    private fun peekApp() = BlockedAppEntity(
        packageName = "com.video",
        appName = "Video",
        category = "Social",
        isBlocked = true,
        blockTier = AppBlockTier.PEEK_2M.name
    )

    @Test
    fun liveLeaseOnThisPackageOutranksItsOwnCooldown() {
        // Mid-lease the cooldown read is lease-remainder + 20min
        // (> 0). The binding constraint is the RUNNING peek, not
        // the cooldown behind it.
        assertEquals(
            PeekWithheld.ALREADY_ACTIVE,
            peekWithheldFor(
                canPeek = false, path = InterceptPath.APP_TIER, entity = peekApp(),
                cooldownLeftSeconds = 1200, pillActive = true,
                pillIsForPackage = true, sessionLive = true
            )
        )
    }

    @Test
    fun anotherPackagesPillDoesNotOutrankThisPackagesCooldown() {
        assertEquals(
            PeekWithheld.COOLDOWN,
            peekWithheldFor(
                canPeek = false, path = InterceptPath.APP_TIER, entity = peekApp(),
                cooldownLeftSeconds = 1200, pillActive = true,
                pillIsForPackage = false, sessionLive = true
            )
        )
    }

    @Test
    fun withheldDerivationStaysSilentWhereNothingIsOwed() {
        // Dead session: no peek line at all (N-2).
        assertNull(
            peekWithheldFor(
                canPeek = false, path = InterceptPath.APP_TIER, entity = peekApp(),
                cooldownLeftSeconds = 1200, pillActive = false,
                pillIsForPackage = false, sessionLive = false
            )
        )
        // An offer standing: nothing withheld.
        assertNull(
            peekWithheldFor(
                canPeek = true, path = InterceptPath.APP_TIER, entity = peekApp(),
                cooldownLeftSeconds = 0, pillActive = false,
                pillIsForPackage = false, sessionLive = true
            )
        )
        // Plain cooldown, no pill anywhere: COOLDOWN.
        assertEquals(
            PeekWithheld.COOLDOWN,
            peekWithheldFor(
                canPeek = false, path = InterceptPath.APP_TIER, entity = peekApp(),
                cooldownLeftSeconds = 300, pillActive = false,
                pillIsForPackage = false, sessionLive = true
            )
        )
    }

    // -----------------------------------------------------------------
    // C-11 (Tor round 7) — arrivals are what the counts read.
    // -----------------------------------------------------------------

    @Test
    fun sessionCountReadsArrivalsNotResolutions() {
        // Since C-11 the row exists from the ARRIVAL (outcome
        // "AT DOOR") and is updated in place at resolution. The
        // taper/memory count must therefore already see a visit
        // that never resolves — the pre-fix ledger saw nothing
        // until RETURN or PEEK booked it.
        val start = 1_000_000L
        val logs = listOf(
            log("com.video", start, outcome = "AT DOOR"),
            log("com.video", start + 5_000L, outcome = "AT DOOR"),
            log("com.video", start + 9_000L, outcome = "RETURNED AT DOOR")
        )
        assertEquals(3, sessionInterceptCount(logs, start, "com.video"))
    }
}
