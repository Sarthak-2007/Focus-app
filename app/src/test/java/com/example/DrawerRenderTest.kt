package com.example

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CapturedNotificationEntity
import com.example.data.DrawerDay
import com.example.data.FocusTaskEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.TaskQueueStatus
import com.example.service.TactileSoundEngine
import com.example.ui.MonolithDestination
import com.example.ui.MonolithViewModel
import com.example.ui.components.CapturedNotificationsDigestSheet
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate

/**
 * Renders for the Drawer build (whole-studio review set): populated
 * landing + detail + shelf-opened month, Tasks with the Last-Finished
 * Line, the globally-empty Drawer, and the digest sheet frozen and
 * live — day and night. Writes dr_* files into the lane's own
 * shots-own dir (Pipeline Law 4 render isolation).
 *
 * One setContent per test method (the compose rule forbids a
 * second), so day and night are separate tests over identical
 * seeds.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w450dp-h1000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DrawerRenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var context: Context
    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository
    private lateinit var viewModel: MonolithViewModel
    private val viewModelStore = ViewModelStore()

    // Render isolation (Pipeline Law 4): this lane renders ONLY
    // into its own shots-own dir, resolved from the lane's
    // shots-redirect.txt — never the shared shots folder.
    private val shotsDir: File = run {
        val redirect = listOf(File("shots-redirect.txt"), File("../shots-redirect.txt"), File("app/shots-redirect.txt")).firstOrNull { it.isFile } // issue #66 cluster-priority: was a hardcoded author-machine path
        val target = redirect?.readText()?.trim()?.takeIf { it.isNotEmpty() }
        // Finisher cure (issue #66): a redirect target that cannot be created on this
        // machine falls through to the next candidate instead of poisoning every capture.
        listOfNotNull(target, System.getenv("MONOLITH_SHOTS_DIR"), "build/outputs/drawer-shots")
            .map { File(it) }
            .first { it.mkdirs() || it.isDirectory }
    }

    @Before
    fun setup() {
        shotsDir.mkdirs()
        context = ApplicationProvider.getApplicationContext()
        // Day words live in SharedPreferences: start every render
        // test from a clean word store.
        context.getSharedPreferences("monolith_day_words", Context.MODE_PRIVATE)
            .edit().clear().commit()
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
        // Chrome wave: PARK's first-run invitation opens over the
        // root at first composition (designed, CHROME-BUILD-READY
        // §5.1) and would cover the Drawer under test. Mark the
        // introduction done, as a dismissed first open would.
        kotlinx.coroutines.runBlocking { repository.markParkIntroDone() }
        viewModel = MonolithViewModel(context, repository, TactileSoundEngine(context))
        viewModelStore.put("main", viewModel)
    }

    @After
    fun tearDown() {
        // KONTO-2 round 8 (diagnosed, run 41069616930): this class's
        // directly-constructed VMs were never cleared, so their
        // immortal viewModelScope collectors (the stateIn trio over
        // getAllRoutinesFlow / getActiveSessionFlow / getAllApps)
        // outlived db.close(); stale Main-looper continuations later
        // executed queued queries against the closed pool and the
        // escape surfaced as UncaughtExceptionsBeforeTest in
        // TruthCar3SweepRoboTest.una07 - the round-4 poison in its
        // cross-class form (same w450dp sandbox). Quiesce in the
        // only safe order: clear the store (onCleared cancels
        // viewModelScope), drain the paused looper so the
        // cancellations actually execute, and only then close.
        viewModelStore.clear()
        ShadowLooper.idleMainLooper()
        composeRule.waitForIdle()
        db.close()
    }

    private fun settle(millis: Long = 700) {
        ShadowLooper.idleMainLooper()
        Thread.sleep(millis)
        ShadowLooper.idleMainLooper()
        try {
            composeRule.waitForIdle()
        } catch (_: Throwable) {
            // Infinite animations/ticker can keep the rule non-idle; capture anyway.
        }
        ShadowLooper.idleMainLooper()
    }

    private fun waitForSeed() {
        val deadline = System.currentTimeMillis() + 45_000
        while (System.currentTimeMillis() < deadline) {
            ShadowLooper.idleMainLooper()
            if (viewModel.apps.value.isNotEmpty() && viewModel.profiles.value.isNotEmpty()) return
            Thread.sleep(250)
        }
        // Fail loudly (#91): silently proceeding after the timeout
        // rendered an EMPTY drawer and passed green — this class
        // has no assertions of its own, so a swallowed seed timeout
        // was undetectable. Same idiom as
        // WatchStoreRobolectricTest's awaitSnapshot.
        throw AssertionError(
            "seed never landed within 45s: apps=${viewModel.apps.value.size}, " +
                "profiles=${viewModel.profiles.value.size}"
        )
    }

    private fun capture(name: String) {
        val out = File(shotsDir, name)
        composeRule.runOnUiThread {
            val decor = composeRule.activity.window.decorView
            val w = if (decor.width > 0) decor.width else 1318
            val h = if (decor.height > 0) decor.height else 2928
            if (decor.width == 0) {
                decor.measure(
                    android.view.View.MeasureSpec.makeMeasureSpec(w, android.view.View.MeasureSpec.EXACTLY),
                    android.view.View.MeasureSpec.makeMeasureSpec(h, android.view.View.MeasureSpec.EXACTLY)
                )
                decor.layout(0, 0, w, h)
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            decor.draw(Canvas(bitmap))
            FileOutputStream(out).use { fos -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos) }
            bitmap.recycle()
        }
    }

    private fun completed(
        title: String,
        completedAt: Long,
        category: String,
        spentSeconds: Int,
        notes: String = ""
    ) = FocusTaskEntity(
        title = title,
        notesOrSubsteps = notes,
        estimatedMinutes = 45,
        actualSecondsSpent = spentSeconds,
        categoryTag = category,
        queueOrder = 0,
        status = TaskQueueStatus.COMPLETED.name,
        completedAtMillis = completedAt
    )

    private fun at(date: LocalDate, hour: Int, minute: Int): Long =
        date.atTime(hour, minute).atZone(java.time.ZoneId.systemDefault())
            .toInstant().toEpochMilli()

    /** The populated Drawer fixture: five completions across four
     *  filing groups and two filing months, plus the current
     *  filing day's declared word.
     *
     *  Reproducible on any run date/time (#91): every instant is a
     *  deterministic function of the CURRENT FILING DAY — the
     *  app's own 03:30 rule (DrawerDay.filingDate), not the
     *  calendar date or the wall clock. Each fixture uses a wall
     *  time after 03:30, so its filing date is exactly the date
     *  named here. The old shape anchored to LocalDate.now() /
     *  System.currentTimeMillis(): the day-5 task was future-dated
     *  on the 1st-4th, the minusMonths fixture shifted on the 1st,
     *  and 00:00-03:30 runs filed the "today" tasks under the
     *  previous DrawerDay — a different fixture every day.
     *  Guaranteed on every run date: filingToday and
     *  filingToday-1d are distinct groups in (possibly) two months,
     *  filingToday-10d is a third distinct date never in the
     *  future, and the 12th of the month before filingToday's
     *  month is a fourth — so the months present are always
     *  exactly filingToday's month and the one before it. */
    private suspend fun seedPopulated() {
        val dao = db.monolithDao()
        val filingToday = DrawerDay.filingDate(System.currentTimeMillis())
        dao.insertTask(
            completed(
                "Electrostatics Problem Set 12", at(filingToday, 14, 30),
                "PHYSICS", 2_640,
                notes = "Coulomb force drills\nField lines sketch\nGauss law set"
            )
        )
        dao.insertTask(
            completed(
                "Botany NCERT Re-Read — Cell Cycle", at(filingToday, 11, 15),
                "BIOLOGY", 1_500
            )
        )
        dao.insertTask(
            completed(
                "Physics Mock — Full Syllabus", at(filingToday.minusDays(1), 21, 14),
                "PHYSICS", 10_800
            )
        )
        dao.insertTask(
            completed(
                "Organic Chemistry — GOC Worksheet", at(filingToday.minusDays(10), 9, 15),
                "CHEMISTRY", 3_300
            )
        )
        dao.insertTask(
            completed(
                "Human Physiology MCQ Set",
                at(filingToday.minusMonths(1).withDayOfMonth(12), 18, 40),
                "BIOLOGY", 4_200
            )
        )
        repository.declareDayWord(DrawerDay.dateIso(filingToday), "REST")
    }

    private fun setRoot(dark: Boolean) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = dark) {
                MonolithRootApp(viewModel = viewModel)
            }
        }
        waitForSeed()
    }

    @Test
    fun drawerPopulated_day() = runBlocking {
        setRoot(dark = false)
        seedPopulated()
        settle()

        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.VAULT) }
        settle(900)
        capture("dr_drawer_day.png")

        composeRule.onNodeWithText("Electrostatics Problem Set 12").performClick()
        settle(600)
        capture("dr_drawer_detail_day.png")
        composeRule.onNodeWithTag("drawer_detail_back").performClick()
        settle(600)

        // INDEX is dead (issue #8): the earlier month opens from the
        // landing's MONTHS shelf row. seedPopulated files one task in
        // last month, so that shelf row exists.
        val shelfMonth = LocalDate.now().minusMonths(1)
        composeRule.onNodeWithTag(
            "drawer_month_${shelfMonth.year}_${shelfMonth.monthValue}"
        ).performClick()
        settle(600)
        capture("dr_drawer_month_day.png")

        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.TASKS) }
        settle(900)
        capture("dr_tasks_day.png")
    }

    @Test
    fun drawerPopulated_night() = runBlocking {
        setRoot(dark = true)
        seedPopulated()
        settle()

        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.VAULT) }
        settle(900)
        capture("dr_drawer_night.png")

        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.TASKS) }
        settle(900)
        capture("dr_tasks_night.png")
    }

    @Test
    fun drawerEmpty_day() {
        setRoot(dark = false)
        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.VAULT) }
        settle(900)
        capture("dr_drawer_empty_day.png")
    }

    @Test
    fun drawerEmpty_night() {
        setRoot(dark = true)
        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.VAULT) }
        settle(900)
        capture("dr_drawer_empty_night.png")
    }

    private fun sampleCaptures(): List<CapturedNotificationEntity> {
        val today = LocalDate.now()
        return listOf(
            CapturedNotificationEntity(
                id = 1, timestampMillis = at(today, 9, 12),
                packageName = "com.whatsapp", appName = "WhatsApp",
                senderTitle = "Maa", messageBody = "Beta khana kha liya? Take a break when the block ends."
            ),
            CapturedNotificationEntity(
                id = 2, timestampMillis = at(today, 8, 47),
                packageName = "com.google.android.youtube", appName = "YouTube",
                senderTitle = "Physics Wallah", messageBody = "New one-shot is live: Rotational Mechanics in 90 minutes."
            ),
            CapturedNotificationEntity(
                id = 3, timestampMillis = at(today.minusDays(1), 22, 31),
                packageName = "org.telegram.messenger", appName = "Telegram",
                senderTitle = "Study Group", messageBody = "Tomorrow's mock is at 9. Bring your own OMR sheets."
            )
        )
    }

    private fun setSheet(dark: Boolean, frozen: Boolean) {
        val captures = sampleCaptures()
        val frozenAt = at(LocalDate.now(), 9, 26)
        composeRule.setContent {
            MyApplicationTheme(darkTheme = dark) {
                CapturedNotificationsDigestSheet(
                    visible = true,
                    notifications = captures,
                    frozenAtMillis = if (frozen) frozenAt else null,
                    frozenMode = frozen,
                    onClearAll = {},
                    onDeleteItem = {},
                    onDismiss = {}
                )
            }
        }
        settle(600)
    }

    @Test
    fun digestFrozen_day() {
        setSheet(dark = false, frozen = true)
        capture("dr_digest_frozen_day.png")
    }

    @Test
    fun digestLive_day() {
        setSheet(dark = false, frozen = false)
        capture("dr_digest_live_day.png")
    }

    @Test
    fun digestFrozen_night() {
        setSheet(dark = true, frozen = true)
        capture("dr_digest_frozen_night.png")
    }
}
