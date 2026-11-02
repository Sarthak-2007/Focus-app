package com.example

import com.example.domain.StrongroomDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime
import java.time.ZoneId

/**
 * TEST-SR-1 (T40) — THE STATE-MODEL SUITE for P1's day model.
 *
 * The obligations are Stage 2's close (close-certificate §10: the 14
 * surviving states, signed as states only) + the transitions and
 * mechanisms the 49 killed entries live on as, read against the
 * applied/unapplied ledger (C-1 · C-2 · B-2 · B-7 · D-6) and
 * VERIFY-SR-1's proof categories. Every test below names its
 * obligation in its name. Where P1's code carries no surface for a
 * survivor, this file asserts NOTHING about it — the obligation is
 * carried as a named GAP in work/test-sr-1/obligations-map.md,
 * never smoothed into a claim here.
 *
 * Authored under METHODS-SR-4's method: top-level class, own @Test
 * methods, package com.example, no inherited or nested tests.
 * Reflection assertions filter synthetic/$-fields throughout.
 */
class StrongroomDayStateModelTest {

    private val utc: ZoneId = ZoneId.of("UTC")

    private val signing = StrongroomDay.SigningRecord(
        filingDateIso = "2026-11-03",
        lockedPackages = setOf("com.video.app", "com.social.app"),
        liftingTime = LocalTime.of(18, 0),
        sentence = null,
        breakLengthMinutes = 83,
        breakFiresAtOffsetMillis = 5_400_000L, // 90 minutes into the span
        allowanceMinutes = 30,
        gaps = listOf(
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.GYM, 7_200_000L, 1_800_000L),
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.SADHANA, 9_900_000L, 1_800_000L),
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.MEAL, 13_500_000L, 1_800_000L)
        )
    )

    /** A 5-hour span struck at the first sitting, boot 3. */
    private val seal = StrongroomDay.SealSpan(
        sealedAtElapsedMillis = 100_000L,
        sealedBootCount = 3,
        spanDurationMillis = 18_000_000L
    )

    private fun fieldNames(type: Class<*>): Set<String> =
        type.declaredFields.map { it.name }.filter { !it.startsWith("$") }.toSet()

    // ------------------------------------------------------------------
    // B-7 / ledger head correction — SIGNATURE is not the SWING
    // ------------------------------------------------------------------

    @Test
    fun signature_isNotTheSwing_aSigningRecordCarriesNoSealMachinery() {
        // The signature is the evening's act (choose · set · spin).
        // It carries no elapsed reading, no boot count, no sealed
        // instant — those belong to the swing alone, at the first
        // sitting. A record that could answer seal questions would
        // be the Audit A compression the ledger's head corrects.
        val fields = fieldNames(StrongroomDay.SigningRecord::class.java)
        assertTrue(fields.none { it.contains("elapsed", ignoreCase = true) })
        assertTrue(fields.none { it.contains("boot", ignoreCase = true) })
        assertTrue(fields.none { it.contains("sealed", ignoreCase = true) })
        val methods = StrongroomDay.SigningRecord::class.java.declaredMethods.map { it.name }
        assertTrue(methods.none { it in setOf("isLifted", "stateAt", "heldMillis", "remainingMillis") })
    }

    @Test
    fun swing_theSealSpanIsStruckAlone_andOnlyElapsedTimeFiresIt() {
        // The span type consults exactly three facts — the elapsed
        // reading at striking, the boot it was struck in, and the
        // frozen duration. There is no wall field left to consult:
        // the span is wall-clock-proof by construction (B-7).
        assertEquals(
            setOf("sealedAtElapsedMillis", "sealedBootCount", "spanDurationMillis"),
            fieldNames(StrongroomDay.SealSpan::class.java)
        )
        assertEquals(
            seal.sealedAtElapsedMillis + seal.spanDurationMillis,
            seal.liftingAtElapsedMillis
        )
        assertFalse(seal.isLifted(seal.liftingAtElapsedMillis - 1, 3))
        assertTrue(seal.isLifted(seal.liftingAtElapsedMillis, 3))
        // Elapsed time on a RESET clock (new boot) never fires it,
        // however large the reading.
        assertFalse(seal.isLifted(Long.MAX_VALUE / 2, 4))
    }

    // NOTE (cure, 2026-11-02): ledger B-1's purpose sentence and
    // B-3's task line ride BUILD-SR-3's UNCOMMITTED wave (they exist
    // in the engine worktree's StrongroomDay.kt, not in committed
    // c531bf48). Their obligations are carried as TG-7 in the
    // obligations map and their tests file the act that wave lands —
    // nothing about them is asserted against the committed API here.

    // ------------------------------------------------------------------
    // The sealed survivors — SEALED-01 · SEALED-02 · M-09
    // ------------------------------------------------------------------

    @Test
    fun sealedFace_andDeepInTheBlock_areOneSealAtTwoOffsets() {
        // SEALED-01 (the face, at the swing) and SEALED-02 (deep in
        // the block) are the same seal read at two offsets: the
        // state never flickers between them, the held time only grows.
        val atSwing = seal.sealedAtElapsedMillis + 1L
        val deepInBlock = seal.sealedAtElapsedMillis + 12_000_000L
        assertEquals(StrongroomDay.SealState.SEALED, seal.stateAt(atSwing, 3, false))
        assertEquals(StrongroomDay.SealState.SEALED, seal.stateAt(deepInBlock, 3, false))
        assertTrue(seal.heldMillis(deepInBlock) > seal.heldMillis(atSwing))
        assertEquals(0L, seal.bracketGapMillis(deepInBlock, 3, false))
    }

    @Test
    fun fastedStudy_behindTheShutDoor_isSealed_andInNoGap() {
        // M-09 FASTED STUDY BEHIND THE SHUT DOOR: the morning's
        // sealed stretch before the gym gap — sealed, and shaped
        // by no gap.
        val inFastedStudy = seal.sealedAtElapsedMillis + 1_000_000L
        assertEquals(StrongroomDay.SealState.SEALED, seal.stateAt(inFastedStudy, 3, false))
        assertNull(StrongroomDay.gapAt(signing.gaps, 1_000_000L))
    }

    // ------------------------------------------------------------------
    // C-2 — shaped gaps INSIDE the seal: M-10 · LM-02 · LM-05
    // ------------------------------------------------------------------

    @Test
    fun gapWalk_gymThenSadhanaThenMeal_theSealStandsThroughEveryGap() {
        // The day's shaped gaps in order — gym (M-10), the prayer
        // (LM-02), the first table (LM-05) — each inside the seal,
        // the seal standing through every one of them, gaps
        // half-open at their edges and never overlapping.
        assertTrue(StrongroomDay.gapsAreLawful(signing.gaps, seal.spanDurationMillis))
        val walk = listOf(
            1_000_000L to null, // fasted study, before any gap
            7_200_000L to StrongroomDay.GapKind.GYM, // gap start is inside the gap
            8_999_999L to StrongroomDay.GapKind.GYM,
            9_000_000L to null, // gym gap end: the shape is over, the seal is not
            9_900_000L to StrongroomDay.GapKind.SADHANA,
            11_700_000L to null,
            13_500_000L to StrongroomDay.GapKind.MEAL,
            15_300_000L to null
        )
        for ((offset, kind) in walk) {
            assertEquals("gap at offset $offset", kind, StrongroomDay.gapAt(signing.gaps, offset)?.kind)
            assertEquals(
                "seal stands at offset $offset",
                StrongroomDay.SealState.SEALED,
                seal.stateAt(seal.sealedAtElapsedMillis + offset, 3, false)
            )
        }
    }

    @Test
    fun gaps_areShapesNotApps_noPackageNoOpening_kindsExactlyTheFiledFive() {
        // A shaped gap can never enter the locked set: by type it
        // carries a kind and two offsets — no package name exists
        // on it to lock. And the kinds are exactly the four ledger
        // C-2 names inside a standing seal, in the committed
        // model's declared order: meditation, gym, sadhana (the
        // prayer), meal. (BUILD-SR-3's uncommitted wave re-reads
        // the meditation kind against the day-map; when that wave
        // lands, this census is re-read in the same act — TG-7.)
        assertEquals(
            setOf("kind", "startOffsetMillis", "durationMillis"),
            fieldNames(StrongroomDay.ShapedGap::class.java)
        )
        assertEquals(
            // ADOPTION RE-AIM (BUILD-SR-15, C-16): the composed
            // GapKind carries a fifth kind the author's four-name
            // census predates — SLEEP (GapPlacement.TERMINAL):
            // sleep stands outside the seal in his day like the
            // other four shapes, and is no opening either.
            listOf(
                StrongroomDay.GapKind.MEDITATION,
                StrongroomDay.GapKind.GYM,
                StrongroomDay.GapKind.SADHANA,
                StrongroomDay.GapKind.MEAL,
                StrongroomDay.GapKind.SLEEP
            ),
            StrongroomDay.GapKind.values().toList()
        )
    }

    // ------------------------------------------------------------------
    // SEALED-09 — THE DRUM SPENT · the crank's week
    // ------------------------------------------------------------------

    @Test
    fun drumSpent_isAHardStop_refusedWhole_neverPartiallyGranted() {
        var purse = StrongroomDay.AllowancePurse(totalMinutes = 30)
        purse = purse.spend(30)!!
        assertTrue(purse.isHardStopped) // SEALED-09: the drum spent
        assertEquals(0, purse.remainingMinutes)
        assertNull(purse.spend(1)) // the crank will not turn past the purse
        // An overspend mid-purse is refused whole, never clipped.
        val fresh = StrongroomDay.AllowancePurse(totalMinutes = 30)
        assertNull(fresh.spend(31))
        assertEquals(30, fresh.remainingMinutes)
    }

    @Test
    fun crank_oneMinutePerWeek_spentWhole_aFreshWeekIsAFreshMinute() {
        val week = StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 99L)
        val spent = week.spend(atWallMillis = 1_000L)!!
        assertTrue(spent.sealBroken) // the snapped seal is kept, visible
        assertNull(spent.spend(atWallMillis = 2_000L)) // no stockpile of exits
        // A NEW week anchor is a new minute — the week state is the
        // anchor's, not a lifetime ban.
        val nextWeek = StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 100L)
        assertTrue(nextWeek.minuteAvailable)
        assertFalse(nextWeek.sealBroken)
    }

    // ------------------------------------------------------------------
    // X1 — THE BREAK: a firing, unhastenable (B-12)
    // ------------------------------------------------------------------

    @Test
    fun break_triesToBeSummonedEarly_andIsRefused() {
        // THE test the brief charges by name: try to summon the
        // break before its signed offset and prove refusal. The
        // firing type offers NO summoning surface at all — its
        // whole API is two questions (due? running?) and its two
        // instants. Any future method that could hasten, summon,
        // skip to, or trigger the break fails this test on sight.
        val firing = StrongroomDay.breakFiring(signing, seal)!!
        val banned = listOf(
            "summon", "hasten", "early", "skip", "trigger",
            "invoke", "advance", "open"
        )
        val methods = StrongroomDay.BreakFiring::class.java.declaredMethods.map { it.name }
        for (name in methods) {
            assertTrue(
                "BreakFiring method '$name' must not be a summoning surface",
                banned.none { name.contains(it, ignoreCase = true) }
            )
        }
        // And behaviourally: at every instant before the offset,
        // the break is neither due nor running. It cannot be taken
        // early from inside the day.
        for (t in listOf(firing.firesAtElapsedMillis - 3_600_000L, firing.firesAtElapsedMillis - 1L)) {
            assertFalse(firing.isDue(t))
            assertFalse(firing.isRunning(t))
        }
    }

    @Test
    fun break_firesAtItsSignedOffset_forAFreelyChosenLength_thenTheSealResumes() {
        // D-6: the length is FREELY chosen — 83 minutes is no
        // preset (presets are shortcuts only, never consulted).
        assertFalse(StrongroomDay.BREAK_PRESET_SHORTCUTS_MINUTES.contains(83))
        val firing = StrongroomDay.breakFiring(signing, seal)!!
        assertEquals(seal.sealedAtElapsedMillis + 5_400_000L, firing.firesAtElapsedMillis)
        assertEquals(83L * 60_000L, firing.endsAtElapsedMillis - firing.firesAtElapsedMillis)
        assertTrue(firing.isRunning(firing.firesAtElapsedMillis))
        assertFalse(firing.isRunning(firing.endsAtElapsedMillis))
        // Self-returning: while the break runs AND after it ends,
        // the seal stands — the break is a firing inside the seal,
        // never a third opening (B-12). The door did not open.
        val midBreak = firing.firesAtElapsedMillis + 60_000L
        assertEquals(StrongroomDay.SealState.SEALED, seal.stateAt(midBreak, 3, false))
        assertFalse(seal.isLifted(midBreak, 3))
        assertEquals(StrongroomDay.SealState.SEALED, seal.stateAt(firing.endsAtElapsedMillis, 3, false))
    }

    // ------------------------------------------------------------------
    // The lifting transition + the fallen day (transitions, not states)
    // ------------------------------------------------------------------

    @Test
    fun lifting_atTheExactElapsedEnd_andNotOneMillisecondBefore() {
        // The lifting (killed as a state, living as the seal's
        // terminal transition): fires at the elapsed end, only on
        // the boot the seal was struck in.
        assertEquals(
            StrongroomDay.SealState.SEALED,
            seal.stateAt(seal.liftingAtElapsedMillis - 1, 3, false)
        )
        assertEquals(
            StrongroomDay.SealState.LIFTED,
            seal.stateAt(seal.liftingAtElapsedMillis, 3, false)
        )
        val plate = StrongroomDay.strikePlate(
            signing, seal, emptyList(),
            StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 1L),
            nowElapsedMillis = seal.liftingAtElapsedMillis, nowBootCount = 3, sealFailed = false
        )
        assertEquals(seal.spanDurationMillis, plate.hoursHeldMillis)
        assertEquals(0L, plate.bracketGapMillis)
    }

    @Test
    fun fallenDay_atTheVeryLiftingInstantOnANewBoot_readsAjar_neverLifted() {
        // The failure transition at its sharpest edge: elapsed time
        // HAS run out, but the boot changed under the seal — the
        // span cannot be trusted, the day reads AJAR, and the plate
        // carries held + bracket = the whole span, nothing invented.
        assertFalse(seal.isLifted(seal.liftingAtElapsedMillis, 4))
        assertEquals(
            StrongroomDay.SealState.AJAR,
            seal.stateAt(seal.liftingAtElapsedMillis, 4, false)
        )
        val plate = StrongroomDay.strikePlate(
            signing, seal, emptyList(),
            StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 1L),
            nowElapsedMillis = seal.sealedAtElapsedMillis + 3_600_000L,
            nowBootCount = 4, sealFailed = false
        )
        assertEquals(StrongroomDay.SealState.AJAR, plate.sealState)
        assertEquals(seal.spanDurationMillis, plate.hoursHeldMillis + plate.bracketGapMillis)
        // A whole seal carries no bracket; a broken one always does.
        assertEquals(0L, seal.bracketGapMillis(seal.sealedAtElapsedMillis + 1L, 3, false))
        assertTrue(seal.bracketGapMillis(seal.sealedAtElapsedMillis + 1L, 3, true) > 0L)
    }

    // ------------------------------------------------------------------
    // The plate + drawer — no tracker machinery exists in the types
    // ------------------------------------------------------------------

    @Test
    fun plateAndDrawer_carryNoScoreNoTotalNoGrade_byConstruction() {
        // Ledger A-11 / D-8: plates are proof, never scored or
        // totalled; the drawer stores finished tasks quietly. The
        // types cannot grade a day — the fields do not exist.
        assertEquals(
            // ADOPTION RE-AIM (BUILD-SR-15, C-16): the composed
            // DayPlate carries one more field than the author's
            // census — taskLine, the day's task line spent at the
            // striking (B-3), his words verbatim. It is not a
            // score, total or grade; the prohibition this census
            // exists to pin stands untouched.
            setOf(
                "filingDateIso", "hoursHeldMillis", "punches", "sealState",
                "crankSealBroken", "bracketGapMillis", "taskLine"
            ),
            fieldNames(StrongroomDay.DayPlate::class.java)
        )
        assertEquals(
            setOf("taskId", "title", "completedAtMillis"),
            fieldNames(StrongroomDay.DrawerEntry::class.java)
        )
    }

    // ------------------------------------------------------------------
    // N-5 SLEEP — the 03:30 seam the night turns on
    // ------------------------------------------------------------------

    @Test
    fun sleep_theFilingSeamPutsTheSmallHoursOnYesterdaysDay() {
        // N-5 SLEEP — THE DARK ROOM, NO SEAL STANDING (20:00–03:30):
        // the seam that bounds it. 02:30 files under the day that
        // is ending; 20:00 files under its own day; the next
        // day-start after the evening is 03:30, never midnight.
        val at0230 = java.time.LocalDateTime.of(2026, 11, 3, 2, 30).atZone(utc).toInstant().toEpochMilli()
        val at2000 = java.time.LocalDateTime.of(2026, 11, 3, 20, 0).atZone(utc).toInstant().toEpochMilli()
        assertEquals("2026-11-02", StrongroomDay.filingDateIso(at0230, utc))
        assertEquals("2026-11-03", StrongroomDay.filingDateIso(at2000, utc))
        assertEquals(3, StrongroomDay.DAY_START_HOUR)
        assertEquals(30, StrongroomDay.DAY_START_MINUTE)
        val nextStart = java.time.LocalDateTime.of(2026, 11, 4, 3, 30).atZone(utc).toInstant().toEpochMilli()
        assertEquals(nextStart, StrongroomDay.nextDayStartMillis(at2000, utc))
    }
}
