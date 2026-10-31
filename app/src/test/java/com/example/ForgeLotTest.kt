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
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

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
 * And the draw is idempotent (issue #8, Werkbank Pin 1): a
 * session that already carries a stamp never re-draws — the
 * re-tick test at the foot of this file is its harness.
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
        // Shadow mono clock starts near zero: advance past the 61s
        // anchor offset in completingSpan() so it is not clamped to 1.
        ShadowSystemClock.advanceBy(Duration.ofSeconds(120))
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
     *  see CryptoIntegrityManager.verifySessionState). When
     *  [alreadyForged] is set, the span carries that stamp into
     *  its completing tick (the re-tick scenario). */
    private fun completingSpan(alreadyForged: String? = null): FocusSessionEntity {
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
            completedSuccessfully = false,
            forgedArtifactId = alreadyForged
        )
    }

    /** Insert a fresh span and run the completing tick on it. */
    private suspend fun completeOneSpan(alreadyForged: String? = null): FocusSessionEntity {
        val dao = db.monolithDao()
        val id = dao.insertSession(completingSpan(alreadyForged)).toInt()
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

    // Werkbank Pin 2, the missing third (issue #8): a session that
    // already carries a stamp and is re-ticked through completion
    // keeps its stamp — the draw must not run at all, so the filed
    // record cannot be re-struck by a later tick. The cabinet here
    // holds nothing but the span's own stamp, so an unguarded draw
    // is guaranteed to land on a different artifact (the draw's
    // complement excludes the stamp itself) and this test fails
    // on the unguarded write.
    @Test
    fun alreadyStampedSession_reTick_keepsStamp() = runBlocking {
        val dao = db.monolithDao()
        val stamp = CurioCatalog.artifacts.first().id
        val done = completeOneSpan(alreadyForged = stamp)
        assertTrue(done.completedSuccessfully)
        assertEquals(
            "an already-stamped session must keep its stamp through completion",
            stamp, done.forgedArtifactId
        )
        assertEquals(setOf(stamp), repo.forgedArtifactIds())
        dao.deleteSessionById(done.id)
    }
}
