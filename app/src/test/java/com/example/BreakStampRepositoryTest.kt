package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #264 B-264-18 — the break length is stamped at birth.
 *
 * The charge (verified by S5-16R + DURCH-E on #264): the preset
 * badges print "5m Break" / "10m Break" / "15m Break" while the
 * Safe Break mechanism always ran a fixed 10 minutes —
 * `breakMinutes` had zero readers. These tests pin the cure at
 * the funnel: a session born with a break choice carries it on
 * its row, its span prices it, and a session born without one
 * keeps the pre-stamp truth (the named 10-minute constant).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BreakStampRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    /** The suite shares one database file: take the active slot
     *  clean, the token suite's hygiene precedent. */
    private fun clearForeignActiveSessions(db: MonolithDatabase) {
        db.openHelper.writableDatabase.execSQL(
            "UPDATE focus_sessions SET isActive = 0 WHERE isActive = 1"
        )
    }

    @Test
    fun stampedBreak_isCarriedOnTheRow_andPricesTheSpan() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)

        val session = repository.startSession(
            intention = "STAMPED",
            profileName = "Manual",
            durationMinutes = 25,
            studyBlocksCount = 3,
            breakMinutes = 5
        )
        try {
            assertEquals(5 * 60, session.breakSeconds)
            assertEquals(5 * 60, session.effectiveBreakSeconds)
            // Span target: 3 blocks of 1500 + 2 breaks of 300.
            assertEquals(3 * 1500 + 2 * 300, session.spanTargetSeconds)
            // The persisted row carries the same stamp.
            val persisted = dao.getSessionById(session.id)
            assertEquals(5 * 60, persisted?.breakSeconds)
        } finally {
            dao.deleteSessionById(session.id)
        }
    }

    @Test
    fun unstampedBreak_fallsBackToTheNamedConstant() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)

        val session = repository.startSession(
            intention = "UNSTAMPED",
            profileName = "Manual",
            durationMinutes = 25,
            studyBlocksCount = 3
        )
        try {
            assertEquals(0, session.breakSeconds)
            assertEquals(
                MonolithRepository.SAFE_BREAK_DURATION_SECONDS,
                session.effectiveBreakSeconds
            )
            assertEquals(
                3 * 1500 + 2 * MonolithRepository.SAFE_BREAK_DURATION_SECONDS,
                session.spanTargetSeconds
            )
        } finally {
            dao.deleteSessionById(session.id)
        }
    }

    @Test
    fun stampedBreak_isBoundedByTheCeiling() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)

        val session = repository.startSession(
            intention = "CEILING",
            profileName = "Manual",
            durationMinutes = 25,
            studyBlocksCount = 2,
            breakMinutes = 24 * 60
        )
        try {
            assertEquals(MonolithRepository.MAX_BREAK_SECONDS, session.breakSeconds)
        } finally {
            dao.deleteSessionById(session.id)
        }
    }
}
