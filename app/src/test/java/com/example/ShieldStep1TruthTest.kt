package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.SightState
import com.example.data.groupedByRule
import com.example.ui.components.shieldControlsLocked
import com.example.ui.components.shieldReceiptLine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Shield Rethink 2, Step 1 — the truth floor's contracts:
 *  - the wedge gate (edits lawful only out of hold, grace excepted);
 *  - the receipt's six permitted forms, generated from proof only —
 *    the partial form is mandatory when partial, a blind loop never
 *    certifies, and no form prints a present-tense claim without a
 *    verified-at time;
 *  - the baseline tally pin: the counts the Sheet prints, over a
 *    fixed catalogue, so the Step-1 split and Step-2 slimming that
 *    follow can be regression-checked against today's figures.
 */
class ShieldStep1TruthTest {

    private val fmt = SimpleDateFormat("HH:mm", Locale.US)
    private fun at(wall: Long): String = fmt.format(Date(wall))

    private val t0 = 1_730_000_000_000L // fixed law-written instant
    private val verified = t0 + 5 * 60_000L
    private val freshBeat = verified + 2_000L // the loop beats ~2s

    // ---- the wedge -------------------------------------------------

    @Test
    fun wedge_openWhenNoSession() {
        assertFalse(shieldControlsLocked(isSessionActive = false, isGracePeriod = false))
    }

    @Test
    fun wedge_lockedInHold() {
        assertTrue(shieldControlsLocked(isSessionActive = true, isGracePeriod = false))
    }

    @Test
    fun wedge_graceWindowStaysOpen() {
        assertFalse(shieldControlsLocked(isSessionActive = true, isGracePeriod = true))
    }

    // ---- the receipt -----------------------------------------------

    @Test
    fun receipt_noSession_open() {
        val line = shieldReceiptLine(
            sessionActive = false, lawWrittenWallMillis = t0,
            sight = SightState.SIGHTED, beatWallMillis = freshBeat,
            lastVerifiedWallMillis = verified, guardsVerified = true,
            nowWallMillis = freshBeat
        )
        assertEquals("OPEN — no block now", line)
    }

    @Test
    fun receipt_sessionBeforeFirstVerifiedBeat_sealedNotYetHeld() {
        val line = shieldReceiptLine(
            sessionActive = true, lawWrittenWallMillis = t0,
            sight = SightState.BLIND_NO_ACCESS, beatWallMillis = null,
            lastVerifiedWallMillis = null, guardsVerified = false,
            nowWallMillis = t0 + 1_000L
        )
        assertEquals("SEALED — law written ${at(t0)} · not yet held", line)
    }

    @Test
    fun receipt_sightedFreshGuardsVerified_held() {
        val line = shieldReceiptLine(
            sessionActive = true, lawWrittenWallMillis = t0,
            sight = SightState.SIGHTED, beatWallMillis = freshBeat,
            lastVerifiedWallMillis = verified, guardsVerified = true,
            nowWallMillis = freshBeat + 1_000L
        )
        assertEquals("HELD — verified ${at(verified)}", line)
    }

    @Test
    fun receipt_sightedFreshGuardsUnverified_partialIsMandatory() {
        val line = shieldReceiptLine(
            sessionActive = true, lawWrittenWallMillis = t0,
            sight = SightState.SIGHTED, beatWallMillis = freshBeat,
            lastVerifiedWallMillis = verified, guardsVerified = false,
            nowWallMillis = freshBeat + 1_000L
        )
        assertEquals("HELD (apps) — verified ${at(verified)} · guards unverified", line)
    }

    @Test
    fun receipt_blindAfterVerified_notVerifiedWithLastHeld() {
        val line = shieldReceiptLine(
            sessionActive = true, lawWrittenWallMillis = t0,
            sight = SightState.BLIND_ERROR, beatWallMillis = freshBeat,
            lastVerifiedWallMillis = verified, guardsVerified = true,
            nowWallMillis = freshBeat + 1_000L
        )
        assertEquals("NOT VERIFIED — last held ${at(verified)}", line)
    }

    @Test
    fun receipt_staleBeat_notVerifiedEvenIfLastSightWasSighted() {
        val line = shieldReceiptLine(
            sessionActive = true, lawWrittenWallMillis = t0,
            sight = SightState.SIGHTED, beatWallMillis = freshBeat,
            lastVerifiedWallMillis = verified, guardsVerified = true,
            nowWallMillis = freshBeat + 11_000L
        )
        assertEquals("NOT VERIFIED — last held ${at(verified)}", line)
    }

    @Test
    fun receipt_usageDoorDownOnLiveBeat_notHeldNamesTheDoor() {
        val line = shieldReceiptLine(
            sessionActive = true, lawWrittenWallMillis = t0,
            sight = SightState.BLIND_NO_ACCESS, beatWallMillis = freshBeat,
            lastVerifiedWallMillis = verified, guardsVerified = false,
            nowWallMillis = freshBeat + 1_000L
        )
        assertEquals("NOT HELD — USAGE ACCESS down · last held ${at(verified)}", line)
    }

    @Test
    fun receipt_usageDoorDownBeforeAnyVerification_notHeldNeverHeld() {
        val line = shieldReceiptLine(
            sessionActive = true, lawWrittenWallMillis = t0,
            sight = SightState.BLIND_NO_ACCESS, beatWallMillis = t0 + 2_000L,
            lastVerifiedWallMillis = null, guardsVerified = false,
            nowWallMillis = t0 + 3_000L
        )
        assertEquals("NOT HELD — USAGE ACCESS down · never held", line)
    }

    @Test
    fun receipt_deadLoopUnderBlindSight_isNotVerifiedNotDoorDown() {
        val line = shieldReceiptLine(
            sessionActive = true, lawWrittenWallMillis = t0,
            sight = SightState.BLIND_NO_ACCESS, beatWallMillis = freshBeat,
            lastVerifiedWallMillis = verified, guardsVerified = false,
            nowWallMillis = freshBeat + 60_000L
        )
        assertEquals("NOT VERIFIED — last held ${at(verified)}", line)
    }

    @Test
    fun receipt_inCallIsNotSight_heldClaimDies() {
        val line = shieldReceiptLine(
            sessionActive = true, lawWrittenWallMillis = t0,
            sight = SightState.IN_CALL, beatWallMillis = freshBeat,
            lastVerifiedWallMillis = verified, guardsVerified = true,
            nowWallMillis = freshBeat + 1_000L
        )
        assertEquals("NOT VERIFIED — last held ${at(verified)}", line)
    }

    // ---- baseline tally pin -----------------------------------------

    private fun row(pkg: String, name: String, tier: AppBlockTier) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = AppCategory.SOCIAL.name,
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name
    )

    @Test
    fun baseline_tallyCountsPinnedBeforeAnySliceMoves() {
        val apps = listOf(
            row("com.a", "Alpha", AppBlockTier.LOCKED),
            row("com.b", "Beta", AppBlockTier.LOCKED),
            row("com.c", "Gamma", AppBlockTier.PEEK_2M),
            row("com.d", "Delta", AppBlockTier.OPEN),
            BlockedAppEntity(
                packageName = "com.e", appName = "Epsilon",
                category = AppCategory.SOCIAL.name,
                isBlocked = true, blockTier = "PEEK_2M_TYPO"
            )
        )
        val rules = apps.groupedByRule()
        assertEquals(2, rules.shutCount)
        assertEquals(1, rules.peekCount)
        assertEquals(1, rules.openCount)
        assertEquals(1, rules.corruptCount)
        assertEquals(apps.size, rules.accountedCount)
    }
}
