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
 * Issue #270 (W9 DAYLIGHT break, comment 6529889886): the app
 * filed every task under a category the user never chose. The
 * composer stopped asking (Issue #53 F4 passes ""), but
 * [MonolithRepository.addTaskToQueue] forced "Study" back onto
 * every blank tag, and the defaults in the repository and the
 * entity fabricated it for any caller that omitted the argument.
 *
 * Sarthak's ruling (Oct 30, 08:55): naming is his own editable
 * words or no name at all — never app-assigned. These tests pin
 * the cure at the storage point:
 *  1. a task added with no tag is stored with NO tag;
 *  2. a task added with a tag keeps exactly that tag, trimmed;
 *  3. a whitespace-only tag is no tag.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NamelessTaskTagTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    /** The queue is shared across the suite: wipe it so each
     *  test owns its rows end to end (the freeze suite's hygiene
     *  precedent). */
    private fun clearTaskQueue(db: MonolithDatabase) {
        db.openHelper.writableDatabase.execSQL("DELETE FROM focus_tasks")
    }

    @Test
    fun taskAddedWithoutTag_isStoredNameless() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        clearTaskQueue(db)
        val repository = MonolithRepository(context, db.monolithDao())

        val added = repository.addTaskToQueue(title = "Read thermodynamics notes")

        assertEquals("", added.categoryTag)
    }

    @Test
    fun taskAddedWithBlankTag_isStoredNameless_notForcedToStudy() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        clearTaskQueue(db)
        val repository = MonolithRepository(context, db.monolithDao())

        val added = repository.addTaskToQueue(
            title = "Revise organic mechanisms",
            categoryTag = "   "
        )

        assertEquals("", added.categoryTag)
    }

    @Test
    fun taskAddedWithChosenTag_keepsItTrimmed() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        clearTaskQueue(db)
        val repository = MonolithRepository(context, db.monolithDao())

        val added = repository.addTaskToQueue(
            title = "Physics problem set",
            categoryTag = "  Physics  "
        )

        assertEquals("Physics", added.categoryTag)
    }
}
