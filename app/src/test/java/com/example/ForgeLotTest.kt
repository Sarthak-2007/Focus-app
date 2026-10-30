package com.example

import android.content.Context
import android.os.SystemClock
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CurioCatalog
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * THE FORGE LOT (AUDIT-drawer §2A; SPITZ Charge 2 — the fix shipped
 * with NO test, and this file is the demanded harness).
 *
 * The truth under test: a completed span forges ONLY from the
 * complement of the DISTINCT forged set the cabinet reads
 * (MonolithDao.getForgedArtifactIds) — so a second forge can never
 * repeat the first, and once all six stand forged a completed span
 * forges nothing (null), instead of minting an invisible repeat
 * forever. Every test drives the REAL production path:
 * MonolithRepository.updateSessionProgress completing a span
 * against a seeded cabinet, and reads the cabinet back through
 * MonolithRepository.forgedArtifactIds — lot and cabinet must
 * agree, because they are the same set read from two sides.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ForgeLotTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private lateinit var repo: MonolithRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = MonolithRepository(context, db.monolithDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    /** A past session that already forged [artifactId]. */
    private fun forgedSession(artifactId: String): FocusSessionEntity =
        FocusSessionEntity(
            intention = "FORGE SEED",
            profileName = "Manual",
            targetDurationSeconds = 60,
            elapsedSeconds = 60,
            startWallTimeMillis = System.currentTimeMillis() - 3_600_000L,
            startElapsedRealtimeMillis = 1L,
            isActive = false,
            completedSuccessfully = true,
            forgedArtifactId = artifactId
        )

    /** A one-block span one tick from completion: 61 verified
     *  seconds against a 60-second target, wall and monotonic
     *  clocks in agreement, unsigned (a blank signature verifies —
     *  see CryptoIntegrityManager.verifySessionState). */
    private fun completingSpan(): FocusSessionEntity {
        val nowWall = System.currentTimeMillis()
        val nowMono = SystemClock.elapsedRealtime()
        return FocusSessionEntity(
            intention = "FORGE SPAN",
            profileName = "Manual",
            targetDurationSeconds = 60,
            elapsedSeconds = 60,
            startWallTimeMillis = nowWall - 61_000L,
            startElapsedRealtimeMillis = (nowMono - 61_000L).coerceAtLeast(1L),
            lastTickElapsedRealtimeMillis = nowMono,
            totalStudyBlocks = 1,
            currentBlockIndex = 1,
            isActive = true,
            completedSuccessfully = false
        )
    }

    /** Insert a fresh span and run the completing tick on it. */
    private suspend fun completeOneSpan(): FocusSessionEntity {
        val dao = db.monolithDao()
        val id = dao.insertSession(completingSpan()).toInt()
        return repo.updateSessionProgress(dao.getSessionById(id)!!)
    }

    // Demand 2, first half: with 5 of 6 already forged, completing
    // a span twenty times must mint the one missing artifact every
    // single time — never a repeat of the five the cabinet holds.
    @Test
    fun secondForge_neverRepeats_whileOneRemains() = runBlocking {
        val dao = db.monolithDao()
        val allIds = CurioCatalog.artifacts.map { it.id }
        val missing = allIds.last()
        allIds.dropLast(1).forEach { dao.insertSession(forgedSession(it)) }
        // Lot and cabinet read the same set: the cabinet readback
        // is exactly the five seeded ids.
        assertEquals(allIds.dropLast(1).toSet(), repo.forgedArtifactIds())
        repeat(20) {
            val done = completeOneSpan()
            assertTrue(done.completedSuccessfully)
            assertEquals(
                "a forge with one unforged artifact left must mint that one",
                missing, done.forgedArtifactId
            )
            // Retire the span so the next completion faces the
            // same 5-of-6 cabinet.
            dao.deleteSessionById(done.id)
        }
        assertEquals(allIds.dropLast(1).toSet(), repo.forgedArtifactIds())
    }

    // Demand 2, second half: at 6 of 6 the completed-set forge is
    // null — the span still completes, it simply forges nothing
    // new, twenty times over.
    @Test
    fun completedSet_forgesNothing() = runBlocking {
        val dao = db.monolithDao()
        CurioCatalog.artifacts.forEach { dao.insertSession(forgedSession(it.id)) }
        assertEquals(
            CurioCatalog.artifacts.map { it.id }.toSet(),
            repo.forgedArtifactIds()
        )
        repeat(20) {
            val done = completeOneSpan()
            assertTrue(done.completedSuccessfully)
            assertNull(
                "a completed set must forge nothing, not an invisible repeat",
                done.forgedArtifactId
            )
            dao.deleteSessionById(done.id)
        }
    }

    // The whole arc in one walk: from an empty cabinet, six
    // completed spans fill the set with zero repeats (each new
    // forge lands outside everything forged before it), the
    // cabinet readback then equals the catalog, and the seventh
    // span forges nothing.
    @Test
    fun cabinet_fillsWithNoRepeats_thenSilence() = runBlocking {
        val seen = mutableSetOf<String>()
        repeat(CurioCatalog.artifacts.size) {
            val done = completeOneSpan()
            val id = done.forgedArtifactId
            assertNotNull("span ${seen.size + 1} of an incomplete set must forge", id)
            assertTrue(id in CurioCatalog.artifacts.map { it.id })
            assertTrue("forge repeated an artifact already in the cabinet", seen.add(id!!))
        }
        assertEquals(
            CurioCatalog.artifacts.map { it.id }.toSet(),
            repo.forgedArtifactIds()
        )
        val seventh = completeOneSpan()
        assertTrue(seventh.completedSuccessfully)
        assertNull(seventh.forgedArtifactId)
    }
}
