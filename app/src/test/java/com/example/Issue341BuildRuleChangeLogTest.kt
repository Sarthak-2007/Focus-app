package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDao
import com.example.data.MonolithDatabase
import com.example.data.RuleChangeKind
import com.example.data.RuleChangeLogEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R6 #341 BUILD — F20: the append-only rule-change log.
 * Every mutating rule edit writes exactly one row (old → new,
 * timestamped); a failed mutation writes none (the repository
 * logs only after the mutation lands, inside its transaction
 * where one exists). These cases pin the storage contract +
 * the DAO surface: no update/delete path exists for the log.
 *
 * PIN scope: PLUMBING.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue341BuildRuleChangeLogTest {

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
    fun tierChange_appendsRuleChangeRow_withOldNewAndTimestamp() = runBlocking {
        val dao = db.monolithDao()
        dao.insertRuleChange(
            RuleChangeLogEntity(
                atWallMillis = 123_456L,
                kind = RuleChangeKind.APP_TIER,
                target = "com.example.app",
                oldValue = "LOCKED",
                newValue = "OPEN"
            )
        )
        val rows = dao.getRuleChangeLogSnapshot()
        assertEquals(1, rows.size)
        assertEquals("LOCKED", rows[0].oldValue)
        assertEquals("OPEN", rows[0].newValue)
        assertEquals(123_456L, rows[0].atWallMillis)
        assertEquals("com.example.app", rows[0].target)
    }

    @Test
    fun routineEdit_andSiteListEdit_eachAppendExactlyOneRow() = runBlocking {
        val dao = db.monolithDao()
        dao.insertRuleChange(
            RuleChangeLogEntity(atWallMillis = 1L, kind = RuleChangeKind.SITE_ADD, target = "youtube.com")
        )
        dao.insertRuleChange(
            RuleChangeLogEntity(atWallMillis = 2L, kind = RuleChangeKind.ROUTINE_UPDATE, target = "routine:3")
        )
        val rows = dao.getRuleChangeLogSnapshot()
        assertEquals(2, rows.size)
        assertEquals(
            listOf(RuleChangeKind.SITE_ADD, RuleChangeKind.ROUTINE_UPDATE),
            rows.map { it.kind }
        )
    }

    @Test
    fun ruleChangeLog_isAppendOnly_noUpdateOrDeletePathExposed() {
        // DAO-surface guard: no @Update / @Delete method on
        // MonolithDao takes a RuleChangeLogEntity — history cannot
        // be rewritten through the app's own data layer.
        val offenders = MonolithDao::class.java.methods.filter { method ->
            method.parameterTypes.any { it == RuleChangeLogEntity::class.java } &&
                method.annotations.any {
                    it.annotationClass.simpleName == "Update" ||
                        it.annotationClass.simpleName == "Delete"
                }
        }
        assertTrue("rule-change log must be append-only, found: $offenders", offenders.isEmpty())
    }

    @Test
    fun ruleChangeKind_vocabularyCoversTheMutatingSurfaces() {
        assertEquals("APP_TIER", RuleChangeKind.APP_TIER)
        assertEquals("SITE_ADD", RuleChangeKind.SITE_ADD)
        assertEquals("SITE_REMOVE", RuleChangeKind.SITE_REMOVE)
        assertEquals("ROUTINE_ADD", RuleChangeKind.ROUTINE_ADD)
        assertEquals("ROUTINE_UPDATE", RuleChangeKind.ROUTINE_UPDATE)
        assertEquals("ROUTINE_DELETE", RuleChangeKind.ROUTINE_DELETE)
    }
}
