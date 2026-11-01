package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusTaskEntity
import com.example.data.MonolithDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R6 #341 BUILD — F67: the task note and the substeps are
 * separate columns now (DB 23→24). Before the split they shared
 * one delimiter-encoded column, so ordinary note text was parsed
 * as substeps on every read — a note line reading "[x] ..." came
 * back as a completed substep, and delimiters inside a note
 * corrupted both halves.
 *
 * PIN scope: PLUMBING.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue341BuildTaskColumnsTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun taskNote_containingDelimiterText_roundTripsUncorrupted() = runBlocking {
        val dao = db.monolithDao()
        val note = "revise [x] cell cycle; step 1|step 2 — see \"notes:sub\" marker"
        val id = dao.insertTask(
            FocusTaskEntity(
                title = "Biology",
                noteText = note,
                notesOrSubsteps = "[ ] mitochondria|[x] nucleus"
            )
        ).toInt()
        val back = dao.getTaskById(id)!!
        // The note comes back VERBATIM — never parsed as substeps.
        assertEquals(note, back.noteText)
        // And the substeps column keeps only the substeps.
        assertEquals("[ ] mitochondria|[x] nucleus", back.notesOrSubsteps)
    }

    @Test
    fun substeps_doNotLeakIntoNoteText_onRead() = runBlocking {
        val dao = db.monolithDao()
        val id = dao.insertTask(
            FocusTaskEntity(
                title = "Physics",
                notesOrSubsteps = "[ ] kinematics|[ ] optics"
            )
        ).toInt()
        val back = dao.getTaskById(id)!!
        assertEquals("", back.noteText)
        assertEquals("[ ] kinematics|[ ] optics", back.notesOrSubsteps)
    }
}
