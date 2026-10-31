package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.BlockedAppEntity
import com.example.data.EditableTimesProvider
import com.example.data.GuardSettingsEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #517 (4-A): the peek length's SETTER, end to end.
 *
 * The EditableTimes seam shipped with readers only — the
 * provider, the offer, the seed and the PARK plate all read
 * `peekMinutes`, and nothing in production could write it, so
 * the set length could never leave its migration default of 2
 * and his word that peek time "should be editable" stopped at
 * a print. This suite pins the write half:
 *
 *  1. A set value persists on the Guard row and the provider —
 *     the lease's single read-point — returns it.
 *  2. A granted lease's end is stamped from the SET length.
 *  3. Zero is PEEK OFF: the grant refuses at the source and no
 *     lease is written.
 *  4. The setter clamps to the seam's bounds on write (0–30),
 *     as the entity's own comment assigns to the setters.
 *  5. The write is the §7.5 targeted UPDATE: the row's other
 *     columns (the receipt claim's among them) stand untouched.
 *
 * The Guard row is shared by the whole class sandbox, so every
 * test sets its value explicitly and the class restores the
 * shipped default (2) after each test.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PeekMinutesSetterTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun repository(): MonolithRepository {
        val db = MonolithDatabase.getInstance(context)
        return MonolithRepository(context, db.monolithDao())
    }

    private fun dao() = MonolithDatabase.getInstance(context).monolithDao()

    private fun app(pkg: String) = BlockedAppEntity(
        packageName = pkg,
        appName = pkg.substringAfterLast('.').uppercase(),
        category = "MESSAGING",
        isBlocked = true,
        blockTier = "PEEK_2M"
    )

    @After
    fun restoreShippedDefault() = runBlocking {
        repository().setPeekMinutes(2)
    }

    @Test
    fun setterPersistsAndTheProviderReadsItBack() = runBlocking {
        val repo = repository()

        repo.setPeekMinutes(5)

        assertEquals(5, dao().getGuardSettingsSnapshot()?.peekMinutes)
        assertEquals(
            5 * 60_000L,
            EditableTimesProvider(dao()).peekDurationMillis()
        )
    }

    @Test
    fun leaseEndIsStampedFromTheSetLength() = runBlocking {
        val repo = repository()
        repo.setPeekMinutes(5)
        dao().insertApp(app("com.peekset.lease"))

        assertTrue(repo.startPeekLease("com.peekset.lease"))

        val row = dao().getAllAppsSnapshot().first { it.packageName == "com.peekset.lease" }
        val remainingMs = row.activePeekUntilElapsedRealtime - SystemClock.elapsedRealtime()
        assertTrue(
            "lease end is ~300 s out, from the SET length (was ${remainingMs}ms)",
            remainingMs in 280_000L..300_000L
        )
    }

    @Test
    fun zeroPeekMinutesRefusesTheGrant() = runBlocking {
        val repo = repository()
        repo.setPeekMinutes(0)
        dao().insertApp(app("com.peekset.off"))

        assertFalse(
            "peekMinutes = 0 is PEEK OFF — the grant refuses at the source",
            repo.startPeekLease("com.peekset.off")
        )
        val row = dao().getAllAppsSnapshot().first { it.packageName == "com.peekset.off" }
        assertEquals(0L, row.activePeekUntilElapsedRealtime)
    }

    @Test
    fun setterClampsToTheSeamBoundsOnWrite() = runBlocking {
        val repo = repository()

        repo.setPeekMinutes(45)
        assertEquals(30, dao().getGuardSettingsSnapshot()?.peekMinutes)

        repo.setPeekMinutes(-3)
        assertEquals(0, dao().getGuardSettingsSnapshot()?.peekMinutes)
    }

    @Test
    fun targetedWriteLeavesTheRestOfTheRowStanding() = runBlocking {
        dao().insertGuardSettings(
            GuardSettingsEntity(
                browserBlockerEnabled = false,
                shortsReelsSniperEnabled = false,
                lastReceiptSessionId = 9,
                lastReceiptOutcome = "STOOD",
                receiptSequence = 41,
                notificationBodyConsent = "GRANTED"
            )
        )

        repository().setPeekMinutes(7)

        val row = dao().getGuardSettingsSnapshot()
        assertEquals(7, row?.peekMinutes)
        assertEquals(41, row?.receiptSequence)
        assertEquals("STOOD", row?.lastReceiptOutcome)
        assertEquals(9, row?.lastReceiptSessionId)
        assertEquals("GRANTED", row?.notificationBodyConsent)
        assertEquals(false, row?.browserBlockerEnabled)
        assertEquals(false, row?.shortsReelsSniperEnabled)
    }
}
