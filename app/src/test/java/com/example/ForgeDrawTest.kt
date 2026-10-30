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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * THE FORGE DRAW (issue #8 — SPITZ Charge 2 / Werkbank Pin 2: the
 * forge truth shipped with NO test; this file is the demanded
 * harness, re-ported by DURCH-A8 from PR #403's ForgeLotTest and
 * adapted to main's current law).
 *
 * The truth under test, as main now holds it (R3b #145):
 * [MonolithRepository.drawCurioId] draws fresh-first — while any
 * artifact stands unforged, a completed span mints one of those,
 * never a repeat; once the whole catalogue stands forged, the
 * seeded pick runs over all six and the duplicate is recorded by
 * the cabinet's provenance like any other forge. And (Werkbank
 * Pin 1, cured in this PR): a session that already carries a
 * stamp NEVER re-draws — the draw runs once per session, ever.
 *
 * Every test drives the REAL production path:
 * MonolithRepository.updateSessionProgress completing a span
 * against a seeded cabinet, read back through
 * MonolithRepository.forgedArtifactIds — lot and cabinet must
 * agree, because they are the same set read from two sides.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ForgeDrawTest {

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
     *  see CryptoIntegrityManager.verifySessionState). When
     *  [alreadyForged] is set, the span carries that stamp into
     *  its completing tick. */
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

    // Werkbank Pin 2, first half: with 5 of 6 already forged,
    // completing a span must mint the one missing artifact every
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

    // The whole arc in one walk: from an empty cabinet, six
    // completed spans fill the set with zero repeats (each new
    // forge lands outside everything forged before it), and the
    // cabinet readback then equals the catalog.
    @Test
    fun cabinet_fillsWithNoRepeats() = runBlocking {
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
    }

    // R3b #145's completed-set law: at 6 of 6 a completed span
    // still forges — a duplicate drawn from the full catalogue by
    // the session's own seeded facts, recorded by provenance like
    // any other forge. The draw is a function of the effort, so
    // two identical spans mint the same duplicate.
    @Test
    fun completedSet_forgesDeterministicDuplicate() = runBlocking {
        val dao = db.monolithDao()
        CurioCatalog.artifacts.forEach { dao.insertSession(forgedSession(it.id)) }
        assertEquals(
            CurioCatalog.artifacts.map { it.id }.toSet(),
            repo.forgedArtifactIds()
        )
        val first = completeOneSpan()
        assertTrue(first.completedSuccessfully)
        assertNotNull("a completed set forges a recorded duplicate, not silence", first.forgedArtifactId)
        assertTrue(first.forgedArtifactId in CurioCatalog.artifacts.map { it.id })
        dao.deleteSessionById(first.id)
        val second = completeOneSpan()
        assertEquals(
            "the seeded draw is deterministic for identical span facts",
            first.forgedArtifactId, second.forgedArtifactId
        )
        dao.deleteSessionById(second.id)
    }

    // Werkbank Pin 1 + Pin 2, third test — the one that was
    // MISSING everywhere: a session that already carries a stamp
    // and is re-ticked through completion keeps its stamp. The
    // draw must not run at all, so the filed record cannot be
    // re-struck by a stale-copy re-tick.
    @Test
    fun alreadyStampedSession_reTick_keepsStamp() = runBlocking {
        val dao = db.monolithDao()
        val allIds = CurioCatalog.artifacts.map { it.id }
        val stamp = allIds.first()
        // Seed the cabinet with everything EXCEPT the stamp's
        // neighbours being irrelevant — the point: the stamp is
        // already forged (by this very span, on an earlier tick),
        // and five fresh artifacts remain that an unguarded draw
        // would pick from (the draw can never return the stamp
        // itself, since fresh excludes it — so any re-draw is
        // guaranteed to change the record).
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
