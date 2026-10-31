package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.CryptoIntegrityManager
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * Issue #36 — the four #21 rulings at the repository seam.
 *
 * Ruling 1 (MIGRATE, then FAIL CLOSED): a session row born
 * unsigned under the old blank-accepting semantics is signed over
 * its standing fields by the migration (seed-sync sweep and the
 * per-row sweep at the tick head) — it is never convicted as
 * tamper for the old semantics' omission, and from the migration
 * on its signature is a real v2 signature that verifies.
 *
 * Ruling 3 (the signature covers the time fields): a stored row
 * whose elapsedSeconds is edited behind the signature's back —
 * the exact edit the v1 payload could not see — now reads as
 * tamper end to end: the verified-clock derivation flags it and
 * the tick books the standing tamper penalty.
 *
 * PIN-SCOPE: POST — these tests pin what the repository's write
 * paths persist (the migrated/re-signed rows as stored), read
 * back through the DAO.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue36SignatureMigrationTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Before
    fun advanceShadowClock() {
        // The ForgeLot idiom (AUDIT-CLOCK #166): the fixtures
        // backdate mono anchors by 600 s; advance past the
        // backdate first so every anchor is real.
        ShadowSystemClock.advanceBy(Duration.ofSeconds(1200))
    }

    private fun clearForeignActiveSessions(db: MonolithDatabase) {
        db.openHelper.writableDatabase.execSQL(
            "UPDATE focus_sessions SET isActive = 0 WHERE isActive = 1"
        )
    }

    /** An honest legacy row: anchors agreeing, 600 s in, and —
     *  the legacy fact — NO signature at all. */
    private fun unsignedLegacyRow(boot: Int): FocusSessionEntity {
        val monoNow = SystemClock.elapsedRealtime()
        val wallNow = System.currentTimeMillis()
        return FocusSessionEntity(
            intention = "LEGACY UNSIGNED",
            profileName = "Manual",
            targetDurationSeconds = 3600,
            elapsedSeconds = 600,
            startWallTimeMillis = wallNow - 600_000L,
            startElapsedRealtimeMillis = monoNow - 600_000L,
            bootCount = boot,
            lastTickElapsedRealtimeMillis = monoNow - 1_000L,
            hmacSignature = "",
            isActive = true,
            completedSuccessfully = false
        )
    }

    @Test
    fun unsignedLegacyRow_isMigratedByTheTick_andVerifies() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        val boot = repository.querySystemBootCount()
        val id = dao.insertSession(unsignedLegacyRow(boot)).toInt()
        val inserted = dao.getSessionById(id)!!

        val ticked = repository.updateSessionProgress(inserted)

        assertFalse(
            "A migrated legacy row must not be stamped tampered",
            ticked.tamperDetected
        )
        assertEquals(
            "A migrated legacy row draws no tamper penalty",
            0, ticked.penaltySecondsAdded
        )
        val persisted = dao.getSessionById(id)!!
        assertTrue(
            "The migration stamps a v2 signature on the stored row (ruling 1 + 3)",
            persisted.hmacSignature.startsWith("v2:")
        )
        assertTrue(
            "The migrated row's signature verifies against the row as stored",
            CryptoIntegrityManager.verifySessionState(persisted)
        )
        dao.deleteSessionById(id)
    }

    @Test
    fun seedSyncMigration_signsEveryStandingUnsignedRow() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        val boot = repository.querySystemBootCount()
        val activeId = dao.insertSession(unsignedLegacyRow(boot)).toInt()
        val historyId = dao.insertSession(
            unsignedLegacyRow(boot).copy(isActive = false, completedSuccessfully = true)
        ).toInt()

        repository.ensureSeededAndSyncPackages()

        for (id in listOf(activeId, historyId)) {
            val row = dao.getSessionById(id)!!
            assertTrue(
                "Row $id carries a v2 signature after the seed-sync migration",
                row.hmacSignature.startsWith("v2:")
            )
            assertTrue(
                "Row $id verifies after the migration",
                CryptoIntegrityManager.verifySessionState(row)
            )
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun editedElapsedBehindTheSignature_readsAsTamper() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        val session = repository.startSession(
            intention = "TAMPER PROBE",
            profileName = "Manual",
            durationMinutes = 25,
            studyBlocksCount = 1
        )
        assertTrue(
            "A session is born v2-signed (ruling 3)",
            session.hmacSignature.startsWith("v2:")
        )

        // The F3 charge, exactly: the stored elapsed is edited
        // without re-signing. Under the v1 payload this edit was
        // invisible; under v2 it is tamper, by name.
        val tampered = session.copy(elapsedSeconds = 300)
        dao.updateSession(tampered)
        val (_, tamper) = repository.computeVerifiedSessionElapsed(tampered)
        assertTrue(
            "An elapsed edited behind its signature must read as tamper (ruling 3)",
            tamper
        )

        val ticked = repository.updateSessionProgress(tampered)
        assertTrue("The tick stamps the tamper on the row", ticked.tamperDetected)
        assertEquals(
            "The tick books the standing tamper penalty",
            MonolithRepository.HMAC_TAMPER_PENALTY_SECONDS,
            ticked.penaltySecondsAdded
        )
        dao.deleteSessionById(session.id)
    }
}
