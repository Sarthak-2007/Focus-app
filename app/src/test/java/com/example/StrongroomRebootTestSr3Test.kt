package com.example

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import com.example.data.MonolithDatabase
import com.example.data.StrongroomStore
import com.example.data.toSigningRecord
import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomRouting
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId

/**
 * TEST-SR-3 (T40) — REBOOT MID-BLOCK + RESTART, against B-10.
 *
 * B-10 has TWO halves and this suite refuses to blur them:
 *  - PROCESS death (same boot): the composition stands written
 *    down; the face is re-shown from it, never recomposed; the
 *    seal reads INTACT and stands.
 *  - BOOT death (the device rebooted under the seal): the elapsed
 *    span cannot be trusted across the boot — the day reads AJAR,
 *    the plate is struck with the bracket gap, and the record's
 *    back stands FIRST: after the strike, no sealed day stands
 *    for any surface to re-show.
 *
 * Venue-environment proof only (JVM/Robolectric). On-device boot
 * behaviour stands UNPROVEN-ON-DEVICE (Audit A G-7).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StrongroomRebootTestSr3Test {

    private val context: Context get() = ApplicationProvider.getApplicationContext()
    private var db: MonolithDatabase? = null

    @After
    fun teardown() {
        db?.close()
        db = null
    }

    private fun newStore(): StrongroomStore {
        if (db == null) {
            db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        }
        // A NEW store instance over the SAME database is this
        // suite's process-death simulation: the process (and every
        // object it held) is gone; only the written record stands.
        return StrongroomStore(db!!.monolithDao())
    }

    private val utc: ZoneId = ZoneId.of("UTC")

    private fun standingOrder(): StrongroomComposition.WeekStandingOrder {
        val template = StrongroomComposition.DayTemplate(
            lockedPackages = setOf("com.video.app", "com.social.app"),
            liftingTime = LocalTime.of(6, 0),
            breakLengthMinutes = 20,
            breakFiresAtOffsetMillis = 3_600_000L,
            allowanceMinutes = 30,
            gaps = listOf(StrongroomDay.ShapedGap(StrongroomDay.GapKind.GYM, 7_200_000L, 3_600_000L)),
            taskLine = "physics, rotational mechanics"
        )
        return StrongroomComposition.WeekStandingOrder(
            shapeByWeekday = DayOfWeek.values().associateWith { StrongroomComposition.DayShape.ORDINARY },
            templateByShape = mapOf(StrongroomComposition.DayShape.ORDINARY to template)
        )
    }

    // Seal "2026-11-03" at wall 2026-11-02T20:00Z (10h to the 06:00
    // lifting), elapsed 100_000, boot 3.
    private suspend fun sealedStore(): Pair<StrongroomStore, StrongroomDay.SealSpan> {
        val store = newStore()
        store.composeDay(standingOrder(), "2026-11-03")
        val sealWall = java.time.LocalDateTime.of(2026, 11, 2, 20, 0).atZone(utc).toInstant().toEpochMilli()
        val span = store.seal("2026-11-03", sealWall, 100_000L, 3, utc)
        assertNotNull("the swing must strike on a composed day", span)
        return store to span!!
    }

    @Test
    fun bootDeath_midBlock_readsAjar_neverLifts_bracketGapIsTheUnhaldSpan() {
        val seal = StrongroomDay.SealSpan(100_000L, 3, 10_800_000L)
        val midBlock = 100_000L + 4_000_000L
        // The boot died: boot count moved, elapsed clock reset.
        assertEquals(
            StrongroomDay.SealState.AJAR,
            seal.stateAt(midBlock, nowBootCount = 4, sealFailed = false)
        )
        // Even a post-reboot elapsed reading far past the lifting
        // can NEVER fire the lift off a reset clock.
        assertFalse(seal.isLifted(Long.MAX_VALUE / 2, nowBootCount = 4))
        assertEquals(
            StrongroomRouting.SealIntegrity.AJAR_BOOT_DEATH,
            StrongroomRouting.classifyIntegrity(seal, nowBootCount = 4, sidewaysKillObserved = false)
        )
        // The bracket gap is the unheld remainder of the span.
        assertEquals(10_800_000L - 4_000_000L, seal.bracketGapMillis(midBlock, 4, sealFailed = false))
    }

    @Test
    fun processDeath_sameBoot_sealStandsIntact_faceReshownFromTheWrittenComposition() = runBlocking {
        val (store, _) = sealedStore()
        val before = store.day("2026-11-03")!!.toSigningRecord()

        // Process death: a fresh store over the same written record.
        val reborn = newStore()
        val sealedRow = reborn.sealedDay()
        assertNotNull("the sealed day must still stand after process death", sealedRow)
        val reshown = sealedRow!!.toSigningRecord()

        // The face is re-shown from the written composition —
        // field for field, nothing recomposed, nothing re-derived.
        assertEquals(before.lockedPackages, reshown.lockedPackages)
        assertEquals(before.liftingTime, reshown.liftingTime)
        assertEquals(before.sentence, reshown.sentence)
        assertEquals(before.breakLengthMinutes, reshown.breakLengthMinutes)
        assertEquals(before.breakFiresAtOffsetMillis, reshown.breakFiresAtOffsetMillis)
        assertEquals(before.allowanceMinutes, reshown.allowanceMinutes)
        assertEquals(before.gaps, reshown.gaps)
        assertEquals(before.taskLine, reshown.taskLine)
        assertEquals(before.purposeSentence, reshown.purposeSentence)

        // And composing again must NOT recompose over the standing
        // day: a different standing order changes nothing.
        val other = StrongroomComposition.WeekStandingOrder(
            shapeByWeekday = DayOfWeek.values().associateWith { StrongroomComposition.DayShape.REST },
            templateByShape = mapOf(
                StrongroomComposition.DayShape.REST to StrongroomComposition.DayTemplate(
                    lockedPackages = setOf("com.other.app"),
                    liftingTime = LocalTime.of(9, 0),
                    breakLengthMinutes = 5,
                    breakFiresAtOffsetMillis = -1L,
                    allowanceMinutes = 1,
                    gaps = emptyList()
                )
            )
        )
        val recomposed = reborn.composeDay(other, "2026-11-03")
        assertEquals(before.lockedPackages, recomposed.record.lockedPackages)
        assertEquals(before.liftingTime, recomposed.record.liftingTime)
    }

    @Test
    fun bootDeath_recordsBackStandsFirst_ajarPlateStruckBeforeAnyReshow() = runBlocking {
        val (store, span) = sealedStore()
        // Boot death mid-block, as the bridge's onBoot derives it:
        // classify first, strike the ajar plate — the record's back
        // stands before anything else.
        val integrity = StrongroomRouting.classifyIntegrity(span, nowBootCount = 4, sidewaysKillObserved = false)
        assertEquals(StrongroomRouting.SealIntegrity.AJAR_BOOT_DEATH, integrity)
        val plate = store.strikePlate("2026-11-03", 50_000L, 4, sealFailed = false, zone = utc)
        assertNotNull(plate)
        assertEquals(StrongroomDay.SealState.AJAR, plate!!.sealState)
        assertTrue("an ajar day carries its bracket gap", plate.bracketGapMillis > 0L)

        // After the strike, NO sealed day stands — there is nothing
        // left for a surface to re-show as sealed.
        assertNull(newStore().sealedDay())
        assertEquals("AJAR", newStore().day("2026-11-03")!!.state)

        // B-14: struck once, never re-struck — a second strike with
        // different readings returns the plate AS STRUCK.
        val restruck = store.strikePlate("2026-11-03", 9_999_999L, 4, sealFailed = true, zone = utc)
        assertEquals(plate.hoursHeldMillis, restruck!!.hoursHeldMillis)
        assertEquals(plate.bracketGapMillis, restruck.bracketGapMillis)
        assertEquals(StrongroomDay.SealState.AJAR, restruck.sealState)
    }

    @Test
    fun sealDeath_releasesThePhone_theFailureFaceIsTestimonyNeverAGate() = runBlocking {
        // R2-3 (rulings round 2 — "TEST-SR-3 asserts it"): at seal
        // death the block's hold ENDS at that moment; the phone is
        // his in full by any ordinary route; the failure face is
        // testimony shown at next open, never a gate.
        val (store, _) = sealedStore()
        val plate = store.strikePlate("2026-11-03", 150_000L, 3, sealFailed = true, zone = utc)
        assertNotNull(plate)
        assertEquals(StrongroomDay.SealState.BROKEN, plate!!.sealState)
        // The hold has ended: no sealed day stands for any surface
        // or service to enforce against.
        assertNull(newStore().sealedDay())
        assertEquals("BROKEN", newStore().day("2026-11-03")!!.state)
        // The testimony carries facts only — held, bracket gap,
        // the seal's state — and (R4-8) NO cause: the plate type
        // has no cause field, by construction.
        assertTrue(plate.bracketGapMillis > 0L)
        val causeFields = StrongroomDay.DayPlate::class.java.declaredFields
            .map { it.name.lowercase() }
            .filter { it.contains("cause") || it.contains("reason") || it.contains("why") }
        assertTrue("the plate must never state why a seal fell (R4-8)", causeFields.isEmpty())
    }
}
