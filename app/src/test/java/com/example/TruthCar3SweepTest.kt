package com.example

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.BlockedBrowserDomains
import com.example.data.LoadoutStore
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.RoutineDerive
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import com.example.service.InterceptPath
import com.example.service.TactileSoundEngine
import com.example.ui.peekOfferFor
import com.example.service.matchBlockedDomain
import com.example.ui.MonolithViewModel
import com.example.ui.components.ParkDrawer
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.os.Looper
import kotlinx.coroutines.runBlocking
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.util.Calendar

/**
 * Truth Car 3 — the five-charge sweep (Bodo): UNA-04, UNA-06, UNA-07,
 * UNA-08, UNA-09. Every test here discriminates: it states the world
 * AFTER its cure and would have failed (or failed to compile) in the
 * world before it. Robolectric-backed cases live in
 * [TruthCar3SweepRoboTest] below.
 */
class TruthCar3SweepTest {

    // ---------------------------------------------------------------
    // UNA-06 — an emptied site list blocks NOTHING. Before the cure,
    // the accessibility service substituted the 8 factory domains
    // whenever the stored list was empty, so Shield could print
    // "0 SITES" while youtube.com was still being intercepted.
    // ---------------------------------------------------------------

    @Test
    fun una06_emptyStoredList_matchesNothing() {
        // A factory-default domain, in an emptied list's world:
        assertNull(matchBlockedDomain("https://www.youtube.com/watch?v=abc", emptyList()))
        assertNull(matchBlockedDomain("https://www.instagram.com/reel/x", emptyList()))
    }

    @Test
    fun una06_storedDomain_matches() {
        assertEquals(
            "youtube.com",
            matchBlockedDomain("https://www.youtube.com/watch?v=abc", listOf("youtube.com"))
        )
        assertNull(
            matchBlockedDomain("https://www.youtube.com/watch?v=abc", listOf("example.org"))
        )
    }

    @Test
    fun una06_factoryDefaults_biteOnlyWhenStored() {
        // The defaults are the SEEDED stored list, his to empty —
        // they enforce only when they are in the list passed in,
        // never by substitution upstream.
        assertEquals(
            "youtube.com",
            matchBlockedDomain(
                "https://www.youtube.com/watch?v=abc",
                BlockedBrowserDomains.defaultDomains
            )
        )
    }

    @Test
    fun una06_noFallbackSubstitutionRemainsInTheService() {
        // Tripwire for the call site the pure seam cannot see: the
        // service must not rebuild a fallback list ever again.
        val source = locateMainSource(
            "com/example/service/MonolithAccessibilityService.kt"
        ).readText()
        assertFalse(
            "the ifEmpty factory fallback is back in the accessibility service",
            source.contains("ifEmpty { BlockedBrowserDomains")
        )
        assertFalse(
            "a fallback is being applied to the stored site list",
            source.contains("userDomains.ifEmpty")
        )
    }

    // ---------------------------------------------------------------
    // UNA-09 — ONE live definition. Before the cure, Shield's
    // "n ACTIVE" counted every enabled routine, including
    // daysMask == 0 rows the scheduler's chain silently skips.
    // ---------------------------------------------------------------

    private fun routine(
        id: Int,
        mask: Int,
        enabled: Boolean,
        start: Int = 540,
        end: Int = 1170
    ) = RoutineEntity(
        id = id, name = "R$id", startMinutes = start, endMinutes = end,
        daysMask = mask, loadoutName = SmartLoadout.STUDY.name, enabled = enabled
    )

    @Test
    fun una09_isLive_truthTable() {
        assertEquals(true, RoutineSchedule.isLive(routine(1, RoutineSchedule.ALL_DAYS, true)))
        assertEquals(false, RoutineSchedule.isLive(routine(2, 0, true)))
        assertEquals(false, RoutineSchedule.isLive(routine(3, RoutineSchedule.ALL_DAYS, false)))
        assertEquals(false, RoutineSchedule.isLive(routine(4, 0, false)))
    }

    @Test
    fun una09_liveCount_countsWhatTheSchedulerCounts() {
        val routines = listOf(
            routine(1, RoutineSchedule.ALL_DAYS, true),   // live
            routine(2, 0, true),                          // enabled but dayless: skipped
            routine(3, RoutineSchedule.MON_FRI, false),   // disabled
            routine(4, RoutineSchedule.MON_FRI, true)     // live
        )
        // The old header expression would have printed 3.
        assertEquals(3, routines.count { it.enabled })
        assertEquals(2, RoutineSchedule.liveCount(routines))

        // The proof desk's exact case: one enabled dayless + one
        // enabled day-carrying routine — the header counts 1.
        val pair = listOf(
            routine(5, 0, true),
            routine(6, RoutineSchedule.ALL_DAYS, true)
        )
        assertEquals(2, pair.count { it.enabled })
        assertEquals(1, RoutineSchedule.liveCount(pair))
    }

    @Test
    fun una09_daylessRoutine_neverMovesTheChainBoundary() {
        // The scheduler-side reader of the same definition: a
        // daysMask == 0 routine contributes no boundary, ever.
        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 3); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val live = routine(1, RoutineSchedule.ALL_DAYS, true)
        val dayless = routine(2, 0, true, start = 400, end = 500)
        val withDayless = RoutineDerive.nextBoundaryMillis(listOf(live, dayless), now)
        val withoutDayless = RoutineDerive.nextBoundaryMillis(listOf(live), now)
        assertEquals(withoutDayless, withDayless)
        assertEquals(null, RoutineDerive.nextBoundaryMillis(listOf(dayless), now))
    }

    companion object {
        /** Locate a main-source file from the test working directory,
         *  whether tests run from the module dir or the repo root. */
        fun locateMainSource(relative: String): File {
            val candidates = listOf(
                File("src/main/java/$relative"),
                File("app/src/main/java/$relative")
            )
            return candidates.firstOrNull { it.exists() }
                ?: error("could not locate $relative from ${File(".").absolutePath}")
        }
    }
}


/**
 * Robolectric half of the sweep: UNA-04 (the manifest and rules the
 * INSTALLED app actually carries), UNA-07 (the loadout store's
 * process-death round-trip), UNA-08 (the plate as rendered).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w450dp-h1000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TruthCar3SweepRoboTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    companion object {
        /** Every backup domain in BOTH storage trees. The
         *  credential-encrypted five alone never covered the app's
         *  Room database, which is built in device-protected storage
         *  (Audit R2 #93): the platform maps device_* domains to that
         *  separate tree. "Exclude everything" iterates all NINE
         *  domains the platform defines — the documented set
         *  (developer.android.com, "Syntax for include and exclude
         *  elements") is exactly these nine: device_* counterparts
         *  exist only for root, file, database and sharedpref;
         *  `external` (getExternalFilesDir) has no device-protected
         *  counterpart, so there is no `device_external` domain to
         *  exclude — an earlier ten-entry list demanded an exclude
         *  line the platform schema cannot express, and both una04
         *  rules tests failed on the correct, fully-excluding files
         *  (the same cure the rule files' own FullBackupContent
         *  note records). The app never writes external storage at
         *  all, so the nine cover every root it uses. */
        val ALL_BACKUP_DOMAINS = listOf(
            "root", "file", "database", "sharedpref", "external",
            "device_root", "device_file", "device_database",
            "device_sharedpref"
        )
    }

    // ---------------------------------------------------------------
    // UNA-04 — "DATA: ON THIS PHONE ONLY" is now true. Before the
    // cure the manifest set allowBackup="true" and both rules files
    // were the untouched samples (nothing excluded anywhere).
    // ---------------------------------------------------------------

    @Test
    fun una04_manifestDisallowsBackup() {
        val info = context.packageManager.getApplicationInfo(context.packageName, 0)
        assertEquals(
            "FLAG_ALLOW_BACKUP must be clear — the plate promises phone-only data",
            0, info.flags and ApplicationInfo.FLAG_ALLOW_BACKUP
        )
    }

    @Test
    fun una04_extractionRulesExcludeEverythingBothWays() {
        val sections = parseExcludeSections(context.resources.getXml(R.xml.data_extraction_rules))
        for (section in listOf("cloud-backup", "device-transfer")) {
            val excluded = sections[section] ?: error("no <$section> section in data_extraction_rules")
            for (domain in ALL_BACKUP_DOMAINS) {
                assertEquals(
                    "$section must exclude domain $domain at path .",
                    ".", excluded[domain]
                )
            }
        }
    }

    @Test
    fun una04_fullBackupRulesExcludeEverything() {
        val sections = parseExcludeSections(context.resources.getXml(R.xml.backup_rules))
        val excluded = sections["full-backup-content"]
            ?: error("no <full-backup-content> section in backup_rules")
        for (domain in ALL_BACKUP_DOMAINS) {
            assertEquals(".", excluded[domain])
        }
    }

    /** section tag -> (domain -> path) for every <exclude> inside it.
     *
     *  K1-W5 cure (#5, Oct 31): the instrument, not the app, was
     *  broken. Both rule files declare their attributes UNPREFIXED —
     *  that is the platform schema for full-backup-content and
     *  data-extraction-rules, and the platform's own parser reads
     *  them under a null namespace. This helper looked them up in
     *  the android: namespace only, so every lookup returned null,
     *  no exclude was ever recorded, and both una04 tests died at
     *  their "no section" error on a tree whose rules exclude
     *  everything — a proof that could not PASS, the mirror image
     *  of a proof that cannot fail (STICH charge 1's shape, one
     *  level down). Attributes are now read null-namespace first,
     *  android: as fallback, and a section is recorded the moment
     *  its tag is seen, so the "no section" error means the section
     *  is truly absent. Discrimination is unchanged: strip the
     *  excludes (the pre-cure sample files) and every assertion in
     *  the una04 tests still fails. */
    private fun parseExcludeSections(parser: XmlPullParser): Map<String, Map<String, String>> {
        val androidNs = "http://schemas.android.com/apk/res/android"
        val result = mutableMapOf<String, MutableMap<String, String>>()
        var section: String? = null
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "cloud-backup", "device-transfer", "full-backup-content" -> {
                        section = parser.name
                        result.getOrPut(parser.name) { mutableMapOf() }
                    }
                    "exclude" -> {
                        val s = section
                        val domain = parser.getAttributeValue(null, "domain")
                            ?: parser.getAttributeValue(androidNs, "domain")
                        val path = parser.getAttributeValue(null, "path")
                            ?: parser.getAttributeValue(androidNs, "path")
                        if (s != null && domain != null) {
                            result.getOrPut(s) { mutableMapOf() }[domain] = path
                        }
                    }
                }
                XmlPullParser.END_TAG -> when (parser.name) {
                    "cloud-backup", "device-transfer", "full-backup-content" -> section = null
                }
            }
            event = parser.next()
        }
        return result
    }

    // ---------------------------------------------------------------
    // UNA-07 — the cap rests on a stored fact. Before the cure the
    // position lived in a composable's remember and died with it.
    // ---------------------------------------------------------------

    @Test
    fun una07_loadoutStore_roundTripsAcrossProcessDeath() {
        context.getSharedPreferences("monolith_loadout", Context.MODE_PRIVATE)
            .edit().clear().commit()

        // No pick yet: the default of record is STUDY, cap seat 0.
        assertNull(LoadoutStore(context).lastHandLoadout())
        assertEquals(0, LoadoutStore(context).capIndex())

        LoadoutStore(context).put(SmartLoadout.SLEEP)

        // A FRESH store instance — the process-death read path.
        val reread = LoadoutStore(context)
        assertEquals(SmartLoadout.SLEEP, reread.lastHandLoadout())
        assertEquals(2, reread.capIndex())

        // The cap mapping is the selector's seat order, all three.
        assertEquals(0, LoadoutStore.capIndexOf(SmartLoadout.STUDY))
        assertEquals(1, LoadoutStore.capIndexOf(SmartLoadout.WORK_SPRINT))
        assertEquals(2, LoadoutStore.capIndexOf(SmartLoadout.SLEEP))
    }

    // ---------------------------------------------------------------
    // UNA-08 — the plate prints the truth about the study record:
    // HOURS live in YPT; the app's own record (the Drawer) is no
    // longer denied by the plate's wording.
    // ---------------------------------------------------------------

    @Test
    fun una08_platePrintsStudyHoursNotStudyRecord() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                ParkDrawer(
                    visible = true,
                    snapshot = null,
                    isSealed = false,
                    isNight = false,
                    soundEnabled = true,
                    soundEngine = null,
                    themeReadout = "AUTO",
                    parkReturnTick = 0,
                    onDismiss = {},
                    onFixClick = {},
                    onCheckClick = {},
                    onToggleSound = {},
                    shortsReelsSniperEnabled = true,
                    onToggleShortsReelsSniper = {},
                    // Wave-2 premise: social pair is scaffolding here —
                    // `true` is the persisted default; the toggle is not under test in this file.
                    socialSitesEnabled = true,
                    onToggleSocialSites = {}
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("STUDY HOURS: IN YEOLPUMTA", substring = true).assertExists()
        composeRule.onNodeWithText("STUDY RECORD: IN YPT", substring = true).assertDoesNotExist()
    }

    // ---------------------------------------------------------------
    // UNA-05, WORDS HALF — the plate prints only what the machinery
    // keeps. "FIRED AS SET" was falsified on his phone (Oct 29,
    // 20:00 SLEEP no-fire); Wache's F1–F5 are not yet in a build
    // nor live-fire-verified, so the clause is struck in this lane
    // (Hale W-11). It returns at merge on that evidence, not before.
    // ---------------------------------------------------------------

    @Test
    fun una05_plateDoesNotClaimFiredAsSet() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                ParkDrawer(
                    visible = true,
                    snapshot = null,
                    isSealed = false,
                    isNight = false,
                    soundEnabled = true,
                    soundEngine = null,
                    themeReadout = "AUTO",
                    parkReturnTick = 0,
                    onDismiss = {},
                    onFixClick = {},
                    onCheckClick = {},
                    onToggleSound = {},
                    shortsReelsSniperEnabled = true,
                    onToggleShortsReelsSniper = {},
                    // Wave-2 premise: social pair is scaffolding here —
                    // `true` is the persisted default; the toggle is not under test in this file.
                    socialSitesEnabled = true,
                    onToggleSocialSites = {}
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("ROUTINES: SET BY HAND ON SHIELD", substring = true)
            .assertExists()
        composeRule.onNodeWithText("FIRED AS SET", substring = true).assertDoesNotExist()
    }

    // ---------------------------------------------------------------
    // UNA-07, the recreation proof (Pruefer's bar): apply SLEEP by
    // hand, then RECREATE the state holder — a fresh ViewModel over
    // the same store, the process-death path — and the cap still
    // rests on SLEEP with no tap. On trunk the cap was a composable
    // remember: recreation always landed back on STUDY.
    // ---------------------------------------------------------------

    @Test
    fun una07_capRestsOnTheAppliedLoadoutAfterRecreation() {
        context.getSharedPreferences("monolith_loadout", Context.MODE_PRIVATE)
            .edit().clear().commit()
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repository = MonolithRepository(context, db.monolithDao())
            fun freshViewModel() =
                MonolithViewModel(context, repository, TactileSoundEngine(context))

            // Before any pick, the cap rests on STUDY — of record.
            assertEquals(0, freshViewModel().loadoutCapIndex.value)

            val vm = freshViewModel()
            vm.applySmartLoadout(SmartLoadout.SLEEP)

            // K1-W5 cure (#5, Oct 31): applySmartLoadout persists
            // ASYNCHRONOUSLY by design — Audit R1 #38 moved the
            // commit() onto IO and rests the cap on the pick only
            // once the store confirms it, so an unpersisted pick is
            // never presented as resting. The old test asserted in
            // the same instant as the tap and raced the very
            // persistence it was proving: on the cured tree it
            // failed at the first post-tap assert (job log, run
            // 41115539923: AssertionError at this test, line 411).
            // Drive the boundary instead: idle the main looper —
            // the viewModelScope resumption posts there — until the
            // store holds the pick and the cap rests on it. The
            // loop is iteration-bounded, not clock-bounded; on a
            // tree where the hand path stops persisting it expires
            // and the asserts below still fail, so the proof keeps
            // its teeth.
            var spins = 0
            while (spins < 400) {
                shadowOf(Looper.getMainLooper()).idle()
                if (LoadoutStore(context).lastHandLoadout() == SmartLoadout.SLEEP &&
                    vm.loadoutCapIndex.value == 2
                ) break
                Thread.sleep(25)
                spins++
            }

            assertEquals(2, vm.loadoutCapIndex.value)

            // Recreation: a NEW ViewModel (new process, new
            // composition) reads the stored fact at birth.
            assertEquals(2, freshViewModel().loadoutCapIndex.value)
        } finally {
            db.close()
        }
    }

    // ---------------------------------------------------------------
    // UNA2-01, the charge-level proof the proof standard named —
    // PeekLeaseForUnlistedPackageArmsNothing — and the merged tree
    // never carried (STICH charge 6; cured here, K1-W5, #5,
    // Oct 31). The sessions proved the offer COMPONENT
    // (TruthCar3InterceptTest.peekOfferNeverDefaultsOnForAnUnlistedPackage)
    // and the joint report entered the charge CURED with NO PROOF
    // EXISTS at charge level. This asserts the charge's end-state
    // itself: for a package with no row, the lease writer arms
    // nothing — no row appears, no lease is set, no cooldown
    // anchor moves on any row — and no door offers it a peek.
    // Discrimination: the gate half does not compile on the
    // proofbase (peekOfferFor IS the cure), and the store half
    // fails against any future upsert-shaped writer.
    // ---------------------------------------------------------------

    @Test
    fun peekLeaseForUnlistedPackageArmsNothing() {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = db.monolithDao()
            val repository = MonolithRepository(context, dao)
            // One listed row, so "nothing moved" has something to bite on.
            runBlocking {
                dao.insertApp(
                    com.example.data.BlockedAppEntity(
                        packageName = "com.listed.app",
                        appName = "Listed",
                        category = "Social",
                        isBlocked = true,
                        blockTier = com.example.data.AppBlockTier.PEEK_2M.name
                    )
                )
            }
            runBlocking { repository.startPeekLease("com.unlisted.app") }
            val rows = runBlocking { dao.getAllAppsSnapshot() }
            assertTrue(
                "the lease writer created a row for an unlisted package",
                rows.none { it.packageName == "com.unlisted.app" }
            )
            assertTrue(
                "a peek lease or cooldown anchor moved for an unlisted package",
                rows.all {
                    it.activePeekUntilElapsedRealtime == 0L &&
                        it.lastPeekEndElapsedRealtime == 0L
                }
            )
            for (path in InterceptPath.entries) {
                assertFalse(
                    "door $path offered a peek to an unlisted package",
                    peekOfferFor(path, null, 0, false)
                )
            }
        } finally {
            db.close()
        }
    }
}
