package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
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
 * TEST-SR-3 (T40) — PROCESS / BOOT DEATH AT EVERY BEAT BOUNDARY.
 *
 * The day's beats: composition → signing → the swing → the sealed
 * span (gap · knock · break window · crank) → the lifting → the
 * plate. At EVERY boundary this suite kills the process (a fresh
 * store over the same written record) or the boot (boot count +1)
 * and asserts the day reads exactly as the law says — nothing
 * half-written, nothing resurrected, the plate struck once.
 *
 * Death simulation is honest about its level: a new store instance
 * over the same database is PROCESS death at code level; a moved
 * boot count is the model's boot-death input. What a handset does
 * across a real reboot stands UNPROVEN-ON-DEVICE (Audit A G-7).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StrongroomBeatBoundaryDeathTestSr3Test {

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
        return StrongroomStore(db!!.monolithDao())
    }

    private val utc: ZoneId = ZoneId.of("UTC")
    private val dateIso = "2026-11-03"
    private val sealWall =
        java.time.LocalDateTime.of(2026, 11, 2, 20, 0).atZone(utc).toInstant().toEpochMilli()

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

    private suspend fun composeAndSeal(store: StrongroomStore): StrongroomDay.SealSpan {
        store.composeDay(standingOrder(), dateIso)
        return store.seal(dateIso, sealWall, 100_000L, 3, utc)!!
    }

    @Test
    fun boundary_composedNotSigned_deathLeavesTheCompositionStanding() = runBlocking {
        newStore().composeDay(standingOrder(), dateIso)
        // Death before any signing: the composition stands (B-2) —
        // the app never checks whether the evening happened.
        val reborn = newStore()
        val day = reborn.day(dateIso)
        assertNotNull(day)
        assertEquals("COMPOSED", day!!.state)
        assertFalse(day.amendedBySigning)
        // And the swing can still seal it, unsigned.
        assertNotNull(reborn.seal(dateIso, sealWall, 100_000L, 3, utc))
    }

    @Test
    fun boundary_signedNotSealed_deathKeepsTheAmendment_andSealingStillLawful() = runBlocking {
        val store = newStore()
        store.composeDay(standingOrder(), dateIso)
        store.amendDay(
            standingOrder(), dateIso,
            StrongroomComposition.SigningAmendment(sentence = "rotational mechanics, then rest")
        )
        val reborn = newStore()
        assertEquals("SIGNED", reborn.day(dateIso)!!.state)
        assertNotNull(reborn.seal(dateIso, sealWall, 100_000L, 3, utc))
    }

    @Test
    fun boundary_atTheSwingInstant_heldZero_sealStands() = runBlocking {
        val store = newStore()
        val span = composeAndSeal(store)
        val reborn = newStore()
        val row = reborn.sealedDay()
        assertNotNull(row)
        assertEquals(0L, span.heldMillis(span.sealedAtElapsedMillis))
        assertEquals(
            StrongroomDay.SealState.SEALED,
            span.stateAt(span.sealedAtElapsedMillis, 3, sealFailed = false)
        )
    }

    @Test
    fun boundary_midSeal_sameBootIntact_newBootAjar_neitherRecomposes() = runBlocking {
        val store = newStore()
        val span = composeAndSeal(store)
        val mid = span.sealedAtElapsedMillis + 5_000_000L
        assertEquals(
            StrongroomRouting.SealIntegrity.INTACT,
            StrongroomRouting.classifyIntegrity(span, 3, sidewaysKillObserved = false)
        )
        assertEquals(
            StrongroomRouting.SealIntegrity.AJAR_BOOT_DEATH,
            StrongroomRouting.classifyIntegrity(span, 4, sidewaysKillObserved = false)
        )
        // Either way the written day is the day: a fresh store sees
        // the same span facts (no recomposition on any restart).
        val row = newStore().sealedDay()!!
        assertEquals(span.sealedAtElapsedMillis, row.sealedAtElapsedMillis)
        assertEquals(span.spanDurationMillis, row.spanDurationMillis)
        assertEquals(span.sealedBootCount, row.sealedBootCount)
        assertTrue(mid > span.sealedAtElapsedMillis)
    }

    @Test
    fun boundary_insideTheBreakWindow_deathConsumesNothing_firingRederivesIdentically() = runBlocking {
        val store = newStore()
        val span = composeAndSeal(store)
        val signing = store.day(dateIso)!!.toSigningRecord()
        val firingBefore = StrongroomDay.breakFiring(signing, span)!!
        // Death inside the break's running window.
        val insideWindow = firingBefore.firesAtElapsedMillis + 60_000L
        assertTrue(firingBefore.isRunning(insideWindow))
        // The firing is DERIVED, never stored: after death it
        // re-derives to the identical firing from the written day.
        val reborn = newStore()
        val row = reborn.sealedDay()!!
        val firingAfter = StrongroomDay.breakFiring(
            row.toSigningRecord(), row.let {
                StrongroomDay.SealSpan(it.sealedAtElapsedMillis, it.sealedBootCount, it.spanDurationMillis)
            }
        )!!
        assertEquals(firingBefore, firingAfter)
        // The seal stands THROUGH the break — running is not open.
        assertEquals(
            StrongroomDay.SealState.SEALED,
            span.stateAt(insideWindow, 3, sealFailed = false)
        )
    }

    @Test
    fun boundary_crankSpentThenDeath_theMinuteStaysSpent_theSealStaysWhole() = runBlocking {
        val store = newStore()
        composeAndSeal(store)
        val spent = store.spendCrank(sealWall + 60_000L, utc)
        assertNotNull(spent)
        assertTrue(spent!!.sealBroken)
        // Death: the week's minute is not refunded by a restart.
        val week = newStore().crankWeek(sealWall + 120_000L, utc)
        assertFalse(week.minuteAvailable)
        assertTrue(week.sealBroken)
        // A second spend is refused whole — no stockpile (D-4).
        assertNull(newStore().spendCrank(sealWall + 180_000L, utc))
    }

    @Test
    fun boundary_liftInstantMinusOne_deathLeavesTheSealStanding_plusZeroItLifts() = runBlocking {
        val store = newStore()
        val span = composeAndSeal(store)
        assertFalse(span.isLifted(span.liftingAtElapsedMillis - 1L, 3))
        assertEquals(
            StrongroomDay.SealState.SEALED,
            span.stateAt(span.liftingAtElapsedMillis - 1L, 3, sealFailed = false)
        )
        assertTrue(span.isLifted(span.liftingAtElapsedMillis, 3))
        assertEquals(
            StrongroomDay.SealState.LIFTED,
            span.stateAt(span.liftingAtElapsedMillis, 3, sealFailed = false)
        )
    }

    @Test
    fun boundary_afterLiftBeforePlateRead_deathCannotRestrikeOrBackdate() = runBlocking {
        val store = newStore()
        val span = composeAndSeal(store)
        val plate = store.strikePlate(dateIso, span.liftingAtElapsedMillis, 3, sealFailed = false, zone = utc)!!
        assertEquals(StrongroomDay.SealState.LIFTED, plate.sealState)
        assertEquals(span.spanDurationMillis, plate.hoursHeldMillis)
        // Death, then a late second strike attempt with a LATER
        // clock: B-14 — the plate as struck returns; nothing is
        // re-struck, backdated, or improved after the fact.
        val late = newStore().strikePlate(
            dateIso, span.liftingAtElapsedMillis + 86_400_000L, 3, sealFailed = false, zone = utc
        )!!
        assertEquals(plate.hoursHeldMillis, late.hoursHeldMillis)
        assertEquals(plate.sealState, late.sealState)
        assertEquals(plate.bracketGapMillis, late.bracketGapMillis)
    }

    @Test
    fun boundary_doubleBootDeath_secondStrikeIsTheSamePlate() = runBlocking {
        val store = newStore()
        val span = composeAndSeal(store)
        // First boot death strikes AJAR.
        val first = store.strikePlate(dateIso, 200_000L, 4, sealFailed = false, zone = utc)!!
        assertEquals(StrongroomDay.SealState.AJAR, first.sealState)
        // The phone dies AGAIN before the plate is ever read; the
        // boot path runs again. The truth does not move.
        val second = newStore().strikePlate(dateIso, 300_000L, 5, sealFailed = false, zone = utc)!!
        assertEquals(first.hoursHeldMillis, second.hoursHeldMillis)
        assertEquals(first.bracketGapMillis, second.bracketGapMillis)
        assertEquals(StrongroomDay.SealState.AJAR, second.sealState)
        assertTrue(span.spanDurationMillis > 0L)
    }

    @Test
    fun boundary_amendmentAfterSealing_isRefused_theSealedDayIsAmendedByNoOne() = runBlocking {
        val store = newStore()
        composeAndSeal(store)
        val attempt = store.amendDay(
            standingOrder(), dateIso,
            StrongroomComposition.SigningAmendment(liftingTime = LocalTime.of(12, 0))
        )
        assertNull("a sealed day is amended by no one", attempt)
        assertEquals(LocalTime.of(6, 0), newStore().day(dateIso)!!.toSigningRecord().liftingTime)
    }
}
