package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PrefsSchema
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R6 #341 BUILD — F56: every SharedPreferences file carries
 * the schema-version key, stamped toward an append-only migration
 * path.
 *
 * Scope correction (named): the audit counted 7 prefs files and
 * the tests draft enumerated "all seven". Verification wave 3
 * (6534024259) enumerated current main and found 23 distinct
 * files; a 7-file enumeration would pass while 16 files stayed
 * unversioned. These cases pin the 23.
 * PIN scope: PLUMBING.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue341BuildPrefsVersionTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun everyPrefsFile_carriesSchemaVersionKey() {
        assertEquals(23, PrefsSchema.ALL_PREFS_FILES.size)
        assertEquals(23, PrefsSchema.ALL_PREFS_FILES.toSet().size)
        PrefsSchema.stampAll(context)
        for (name in PrefsSchema.ALL_PREFS_FILES) {
            val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            assertTrue(
                "$name must carry the version key",
                prefs.contains(PrefsSchema.VERSION_KEY)
            )
            assertEquals(
                PrefsSchema.CURRENT_VERSION,
                prefs.getInt(PrefsSchema.VERSION_KEY, -1)
            )
        }
    }

    @Test
    fun migrator_upgradesV1FixtureToCurrent_withoutLosingValues() {
        val prefs = context.getSharedPreferences("monolith_watch", Context.MODE_PRIVATE)
        prefs.edit().putString("sentinel", "keep-me").commit()
        PrefsSchema.stampAll(context)
        // The value survives; the stamp lands.
        assertEquals("keep-me", prefs.getString("sentinel", null))
        assertEquals(PrefsSchema.CURRENT_VERSION, prefs.getInt(PrefsSchema.VERSION_KEY, -1))
        // The version decision for an unstamped (v1-by-definition)
        // file is "migrate to current", never "unknown".
        assertEquals(PrefsSchema.CURRENT_VERSION, PrefsSchema.migratedVersion(null))
        assertEquals(PrefsSchema.CURRENT_VERSION, PrefsSchema.migratedVersion(1))
    }

    @Test
    fun migrator_unknownFutureVersion_leavesFileUntouched_neverMisreads() {
        // A stamp from a NEWER build is not a version this build
        // may interpret: hands off (null), never a guess.
        assertNull(PrefsSchema.migratedVersion(PrefsSchema.CURRENT_VERSION + 1))
        assertNull(PrefsSchema.migratedVersion(99))
        val prefs = context.getSharedPreferences("monolith_watch", Context.MODE_PRIVATE)
        prefs.edit().putInt(PrefsSchema.VERSION_KEY, 99).commit()
        PrefsSchema.stampAll(context)
        assertEquals(99, prefs.getInt(PrefsSchema.VERSION_KEY, -1))
    }
}
