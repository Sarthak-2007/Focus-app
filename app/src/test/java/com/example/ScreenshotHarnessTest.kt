package com.example

import android.content.Context
import androidx.activity.ComponentActivity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import java.io.FileOutputStream
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.PermState
import com.example.service.TactileSoundEngine
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchSnapshot
import com.example.ui.MonolithDestination
import com.example.ui.MonolithViewModel
import com.example.ui.components.ParkDrawer
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.views.RotaryWheelView
import java.io.File
import javax.imageio.ImageIO
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * Screenshot harness added by Ren (Oct 28, 2026) purely to render the app's real
 * current UI under Robolectric native graphics. Not part of the shipped app.
 * Renders MonolithRootApp with a real ViewModel + in-memory Room DB (which the
 * ViewModel seeds with its default catalog on init), then captures each
 * destination to the lane's shots dir (shots-redirect.txt).
 *
 * Chrome wave (Oct 29): GUARD is dissolved — the destinations are the
 * rail's four stops. The night pass no longer calls a theme toggle
 * (the manual theme is deleted): it advances the shadow clock into
 * the seeded SLEEP routine's window (20:00–03:30) so the app's own
 * theme evaluation turns the scheme, exactly as the phone would.
 * PARK is captured by composing ParkDrawer directly over a
 * fabricated Watch Store snapshot (one lack -> the FIX key stands).
 * zzAccentCensus runs last (name order) and applies the accent
 * law's census to the PNGs this class captured.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ScreenshotHarnessTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var context: Context
    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository
    private lateinit var viewModel: MonolithViewModel
    private lateinit var soundEngine: TactileSoundEngine

    // Lane redirect (pipeline law, Oct 29): a lane tree drops a
    // one-line shots-redirect.txt (module root or repo root) naming
    // its OWN shots dir, so parallel lanes never overwrite each
    // other's renders. No file -> the shared trunk default below.
    private val shotsDir: File = run {
        val redirect = listOf(
            File("shots-redirect.txt"),
            File("../shots-redirect.txt"),
            File("app/shots-redirect.txt")
        ).firstOrNull { it.isFile }
        val target = redirect?.readText()?.trim()?.takeIf { it.isNotEmpty() }
        // KONTO-2 round 5 (lettered from run 40995686279): the
        // committed redirect names a lane-box path; on the CI runner
        // that path cannot be created and every capture died
        // FileNotFound. Honour the redirect only where it can
        // actually be written; otherwise the tmpdir default.
        val dir = File(target ?: System.getProperty("java.io.tmpdir") + "/monolith-shots")
        dir.mkdirs()
        if (dir.isDirectory && dir.canWrite()) dir
        else File(System.getProperty("java.io.tmpdir") + "/monolith-shots").also { it.mkdirs() }
    }

    @Before
    fun setup() {
        shotsDir.mkdirs()
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
        // The first-run PARK invitation is the app's own behaviour
        // (proven by this harness's first chrome run: every frame
        // arrived PARK-covered, Guard line live at GUARD 1/4). For
        // the destination captures the rig marks the introduction
        // done, exactly as a dismissed first open would.
        kotlinx.coroutines.runBlocking { repository.markParkIntroDone() }
        // The reception's stamp, the same standing as the park bit
        // above (Empfang lane, Oct 30): without it every RootApp
        // capture would arrive sheet-covered. The sheet itself is
        // captured directly below (setReception).
        com.example.data.SetupStore(context).markCompleted()
        soundEngine = TactileSoundEngine(context)
        viewModel = MonolithViewModel(context, repository, soundEngine)
    }

    @After
    fun tearDown() {
        // KONTO-2 round 5: the in-memory DB is deliberately NOT
        // closed. The view model's immortal DAO collectors re-query
        // after a close and the escape ("connection pool has been
        // closed") lands as UncaughtExceptionsBeforeTest on the
        // fork's next test (zzAccentCensus, run 40995686279). The
        // sandbox owns this DB's lifetime.
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

    private fun captureTo(out: File) {
        out.parentFile?.mkdirs() // batch-10 CI cure: the shots dir must exist on a clean runner, not only on the lane box
        composeRule.runOnUiThread {
            val decor = composeRule.activity.window.decorView
            val w = if (decor.width > 0) decor.width else 1318
            val h = if (decor.height > 0) decor.height else 2928
            if (decor.width == 0) {
                decor.measure(
                    View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY)
                )
                decor.layout(0, 0, w, h)
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            decor.draw(Canvas(bitmap))
            FileOutputStream(out).use { fos -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos) }
            bitmap.recycle()
        }
        println("HARNESS captured ${out.name} -> ${out.absolutePath} size=${out.length()}")
    }

    private fun waitForSeed() {
        val deadline = System.currentTimeMillis() + 45_000
        while (System.currentTimeMillis() < deadline) {
            ShadowLooper.idleMainLooper()
            if (viewModel.apps.value.isNotEmpty() && viewModel.profiles.value.isNotEmpty()) break
            Thread.sleep(250)
        }
        println(
            "HARNESS seeded apps=${viewModel.apps.value.size} profiles=${viewModel.profiles.value.size} " +
                "tasks queued=${viewModel.queuedTasks.value.size} routines=${viewModel.routines.value.size}"
        )
    }

    private fun setRootApp() {
        composeRule.setContent {
            // Mirror MainActivity exactly: the scheme's dark flag is
            // the ViewModel's derived darkTheme (the evaluation).
            val dark by viewModel.darkTheme.collectAsState()
            MyApplicationTheme(darkTheme = dark, dynamicColor = false) {
                MonolithRootApp(viewModel = viewModel)
            }
        }
        waitForSeed()
    }

    @Test
    fun captureAllDestinations() {
        setRootApp()

        val shots = listOf(
            MonolithDestination.DECK to "01_deck.png",
            MonolithDestination.SHIELD to "02_shield.png",
            MonolithDestination.TASKS to "03_tasks.png",
            MonolithDestination.VAULT to "04_vault.png"
        )
        for ((dest, name) in shots) {
            composeRule.runOnUiThread { viewModel.navigateTo(dest) }
            settle(900)
            captureTo(File(shotsDir, name))
        }

        // Cabinet evicted from the Drawer (issue #8) — its captures end here.
    }

    @Test
    fun captureAllDestinationsNight() {
        val nightDir = File(shotsDir, "night")
        nightDir.mkdirs()
        // Night, the phone's own way: a SLEEP routine whose window
        // owns the current wall time. (The shadow-clock advance does
        // not reach the ViewModel's ticker under the paused looper;
        // an owning window is the same fact the evaluation reads on
        // device at 21:30, with no clock games.) The seeded
        // 20:00-03:30 SLEEP routine does not own this hour, so the
        // inserted window is the only owner.
        val nowMin = java.util.Calendar.getInstance().let {
            it.get(java.util.Calendar.HOUR_OF_DAY) * 60 + it.get(java.util.Calendar.MINUTE)
        }
        kotlinx.coroutines.runBlocking {
            db.monolithDao().insertRoutines(
                listOf(
                    com.example.data.RoutineEntity(
                        name = "SLEEP",
                        startMinutes = (nowMin - 30 + 1440) % 1440,
                        endMinutes = (nowMin + 90) % 1440,
                        daysMask = 127,
                        loadoutName = "SLEEP",
                        enabled = true
                    )
                )
            )
        }
        setRootApp()
        settle(1200)
        println("HARNESS night darkTheme=${viewModel.darkTheme.value} eval=${viewModel.themeEvaluation.value.readout}")

        val shots = listOf(
            MonolithDestination.DECK to "n01_deck.png",
            MonolithDestination.SHIELD to "n02_shield.png",
            MonolithDestination.TASKS to "n03_tasks.png",
            MonolithDestination.VAULT to "n04_vault.png"
        )
        for ((dest, name) in shots) {
            composeRule.runOnUiThread { viewModel.navigateTo(dest) }
            settle(900)
            captureTo(File(nightDir, name))
        }
    }

    /** The empty-queue Tasks still (§10.3's owed proof): the ruled
     *  blank leaf at 112dp with its rule line, day form. The rig
     *  deletes the seeded queue through the DAO after seeding. */
    @Test
    fun captureTasksEmpty() {
        setRootApp()
        // ALL tasks go (the active leaf is derived from the full
        // list, not the queue) — only then is the queue empty and
        // the ruled blank leaf the screen's whole stack.
        kotlinx.coroutines.runBlocking {
            db.monolithDao().getAllTasksSnapshot().forEach { db.monolithDao().deleteTaskById(it.id) }
        }
        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.TASKS) }
        settle(900)
        captureTo(File(shotsDir, "03b_tasks_empty.png"))
    }

    @Test
    fun captureTasksEmptyNight() {
        val nightDir = File(shotsDir, "night")
        nightDir.mkdirs()
        val nowMin = java.util.Calendar.getInstance().let {
            it.get(java.util.Calendar.HOUR_OF_DAY) * 60 + it.get(java.util.Calendar.MINUTE)
        }
        kotlinx.coroutines.runBlocking {
            db.monolithDao().insertRoutines(
                listOf(
                    com.example.data.RoutineEntity(
                        name = "SLEEP",
                        startMinutes = (nowMin - 30 + 1440) % 1440,
                        endMinutes = (nowMin + 90) % 1440,
                        daysMask = 127,
                        loadoutName = "SLEEP",
                        enabled = true
                    )
                )
            )
        }
        setRootApp()
        kotlinx.coroutines.runBlocking {
            db.monolithDao().getAllTasksSnapshot().forEach { db.monolithDao().deleteTaskById(it.id) }
        }
        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.TASKS) }
        settle(900)
        captureTo(File(nightDir, "n03b_tasks_empty.png"))
    }

    /** PARK open, composed directly over a fabricated snapshot:
     *  five grants standing, DEVICE ADMIN missing (the FIX key
     *  stands), a complete set proved at the current wall time. */
    private fun fabricatedSnapshot(): WatchSnapshot {
        val mono = SystemClock.elapsedRealtime()
        val wall = System.currentTimeMillis()
        fun m(item: WatchItemId, perm: PermState) =
            WatchMeasurement(item, perm = perm, measuredMono = mono, measuredWall = wall)
        return WatchSnapshot(
            items = mapOf(
                WatchItemId.ACCESSIBILITY to m(WatchItemId.ACCESSIBILITY, PermState.GRANTED),
                WatchItemId.USAGE_ACCESS to m(WatchItemId.USAGE_ACCESS, PermState.GRANTED),
                WatchItemId.DEVICE_ADMIN to m(WatchItemId.DEVICE_ADMIN, PermState.MISSING),
                WatchItemId.NOTIFICATION_LISTENER to m(WatchItemId.NOTIFICATION_LISTENER, PermState.GRANTED),
                WatchItemId.OVERLAY to m(WatchItemId.OVERLAY, PermState.GRANTED),
                WatchItemId.BATTERY to m(WatchItemId.BATTERY, PermState.MISSING)
            ),
            engineHeartbeatMono = mono,
            engineHeartbeatWall = wall,
            engineProtecting = false,
            lastProvedWallMillis = wall
        )
    }

    private fun setPark(dark: Boolean) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = dark, dynamicColor = false) {
                ParkDrawer(
                    visible = true,
                    snapshot = fabricatedSnapshot(),
                    isSealed = false,
                    isNight = dark,
                    soundEnabled = true,
                    shortsReelsSniperEnabled = true,
                    soundEngine = soundEngine,
                    themeReadout = if (dark) {
                        "AUTO · NIGHT — SLEEP 20:00"
                    } else {
                        "AUTO · DAY — NO ROUTINE OWNS IT"
                    },
                    parkReturnTick = 0,
                    onDismiss = {},
                    onFixClick = {},
                    onCheckClick = {},
                    onToggleSound = {},
                    onToggleShortsReelsSniper = {},
                    // Wave-2 premise: social pair is scaffolding here —
                    // `true` is the persisted default; the toggle is not under test in this file.
                    socialSitesEnabled = true,
                    onToggleSocialSites = {}
                )
            }
        }
        settle(1200)
    }

    @Test
    fun parkSheetDay() {
        setPark(dark = false)
        captureTo(File(shotsDir, "05_park.png"))
    }

    @Test
    fun parkSheetNight() {
        val nightDir = File(shotsDir, "night")
        nightDir.mkdirs()
        setPark(dark = true)
        captureTo(File(nightDir, "n05_park.png"))
    }

    /** THE RECEPTION (Empfang lane, Oct 30), composed directly
     *  over the PARK capture's fabricated snapshot — DEVICE ADMIN
     *  missing, so its FIX key stands and the total counts 3/4.
     *  The routine line is a fabricated input, the setPark
     *  pattern; every door word derives from the snapshot by
     *  production code. */
    private fun setReception(dark: Boolean) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = dark, dynamicColor = false) {
                com.example.ui.components.ReceptionSheet(
                    visible = true,
                    snapshot = fabricatedSnapshot(),
                    isFirstOpen = true,
                    isNight = dark,
                    returnTick = 0,
                    soundEngine = soundEngine,
                    firstRoutineLine = "NEXT ROUTINE — FASTED BLOCK · 05:00 · SET ON SHIELD",
                    onShown = {},
                    onFixClick = {},
                    onCheckClick = {},
                    onOpenShield = {},
                    onDone = {}
                )
            }
        }
        settle(1200)
    }

    @Test
    fun receptionSheetDay() {
        setReception(dark = false)
        captureTo(File(shotsDir, "11_reception.png"))
    }

    @Test
    fun receptionSheetNight() {
        val nightDir = File(shotsDir, "night")
        nightDir.mkdirs()
        setReception(dark = true)
        captureTo(File(nightDir, "n11_reception.png"))
    }

    /** The mid-drag wheel render (chrome C8's verify-first): a DOWN
     *  + MOVE dispatched to the real RotaryWheelView with no UP —
     *  the capture is taken mid-drag. The knurl teeth are
     *  screen-fixed material (they do not rotate with the rim), so
     *  the specular pattern must sit at the same azimuths as the
     *  rest render while the reader has moved. */
    @Test
    fun wheelMidDrag() {
        setRootApp()
        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.DECK) }
        settle(900)
        runCatching {
            composeRule.runOnUiThread {
                val decor = composeRule.activity.window.decorView
                val wheel = findView(decor) { it is RotaryWheelView } as? RotaryWheelView
                    ?: error("no RotaryWheelView in the Deck")
                val loc = IntArray(2)
                wheel.getLocationOnScreen(loc)
                val cx = loc[0] + wheel.width / 2f
                val cy = loc[1] + wheel.height / 2f
                val rim = minOf(wheel.width, wheel.height) * 0.45f * 0.93f
                val downT = SystemClock.uptimeMillis()
                val x0 = cx + rim * 0.94f
                val y0 = cy + rim * 0.34f
                wheel.dispatchTouchEvent(
                    MotionEvent.obtain(downT, downT, MotionEvent.ACTION_DOWN, x0, y0, 0)
                )
                wheel.dispatchTouchEvent(
                    MotionEvent.obtain(downT, downT + 120, MotionEvent.ACTION_MOVE, x0 - rim * 0.35f, y0 + rim * 0.30f, 0)
                )
            }
            ShadowLooper.idleMainLooper()
            captureTo(File(shotsDir, "00_wheel_drag.png"))
            composeRule.runOnUiThread {
                val decor = composeRule.activity.window.decorView
                val wheel = findView(decor) { it is RotaryWheelView } as? RotaryWheelView
                wheel?.dispatchTouchEvent(
                    MotionEvent.obtain(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_CANCEL, 0f, 0f, 0
                    )
                )
            }
        }.onFailure { println("HARNESS wheel drag capture failed: $it") }
    }

    private fun findView(root: View, pred: (View) -> Boolean): View? {
        if (pred(root)) return root
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                findView(root.getChildAt(i), pred)?.let { return it }
            }
        }
        return null
    }

    // ------------------------------------------------------------------
    // THE RECEIPT SLIP (Slice E) — the harness finish seed (Voss,
    // post-build commission, Oct 29): a finished manual session row
    // is inserted directly — test-side data, the same standing as
    // this harness's other seeds — and the PRODUCTION finish
    // machine does everything else: the finish controller's
    // terminal predicate, detention on the witnessed path, the
    // claim transaction that mints the №, assembly (the held
    // counts off the row's seal columns, the deflections off the
    // intercept log), and the Deck's slip over the real root app.
    // What is fabricated: the session's history — no real fifty
    // minutes ran, and no services exist under the rig, so on the
    // witnessed path the shield pawl reads lifted at the first
    // poll (the stop was never really commanded). What is NOT
    // fabricated: every figure on the slip is computed by
    // production code from the seeded row. The pump prints which
    // branch actually fired (the witnessed path is the only one
    // that ever raises a FinishState; a slow first sighting falls
    // to branch U, claim + issue at discovery — same slip).
    // ------------------------------------------------------------------

    private val receiptIntention = "Finish Chapter 4 Problem Set (#1–6)"

    /** A finished 50-minute manual session whose derived end
     *  (startWall + spanElapsed) sits [endOffsetMs] from now, its
     *  seal-held counts persisted on the row, and two
     *  intention-matched deflections inside its window. */
    private fun seedFinishedSession(outcome: String, endOffsetMs: Long) {
        val endWall = System.currentTimeMillis() + endOffsetMs
        val startWall = endWall - 3_000_000L
        val mono = SystemClock.elapsedRealtime()
        kotlinx.coroutines.runBlocking {
            val dao = db.monolithDao()
            val id = dao.insertSession(
                com.example.data.FocusSessionEntity(
                    intention = receiptIntention,
                    profileName = "STUDY",
                    targetDurationSeconds = 3000,
                    elapsedSeconds = 3000,
                    startWallTimeMillis = startWall,
                    startElapsedRealtimeMillis = mono,
                    lastTickElapsedRealtimeMillis = mono,
                    isActive = false,
                    completedSuccessfully = true,
                    heldAppsAtSeal = 10,
                    heldSitesAtSeal = 3,
                    outcome = outcome
                )
            )
            dao.insertInterceptLog(
                com.example.data.InterceptLogEntity(
                    timestampMillis = startWall + 12 * 60_000L,
                    packageName = "com.zhiliaoapp.musically",
                    appName = "TikTok",
                    sessionIntention = receiptIntention,
                    outcome = "BLOCKED"
                )
            )
            dao.insertInterceptLog(
                com.example.data.InterceptLogEntity(
                    timestampMillis = startWall + 31 * 60_000L,
                    packageName = "com.instagram.android",
                    appName = "Instagram",
                    sessionIntention = receiptIntention,
                    outcome = "BLOCKED"
                )
            )
            println("HARNESS receipt seed: session id=$id outcome=$outcome endWall=$endWall")
        }
    }

    /** Pump the paused looper — advancing the shadow clock in
     *  steps so the detention loop's 25ms polls and the E-clock's
     *  delays can run — until the VM publishes a receipt. Prints
     *  the branch evidence either way. */
    private fun pumpForReceipt(timeoutMs: Long = 90_000): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        var sawFinishState = false
        while (System.currentTimeMillis() < deadline) {
            ShadowLooper.idleMainLooper()
            if (viewModel.finishState.value != null) sawFinishState = true
            val receipt = viewModel.receiptState.value
            if (receipt != null) {
                println(
                    "HARNESS receipt issued: number=${receipt.numberPrint} kind=${receipt.kind} " +
                        "outcome=${receipt.outcomeWord} faulted=${receipt.faulted} " +
                        "shieldWasOff=${receipt.shieldWasOff} held=${receipt.heldAppsAtSeal}/${receipt.heldSitesAtSeal} " +
                        "routineLabel=${receipt.routineLabelWithin} deflections=${receipt.deflections.size} " +
                        "witnessedPath=$sawFinishState"
                )
                return true
            }
            ShadowSystemClock.advanceBy(Duration.ofMillis(100))
            Thread.sleep(50)
        }
        println("HARNESS receipt NEVER issued within ${timeoutMs}ms (finishState seen=$sawFinishState)")
        return false
    }

    @Test
    fun receiptSlipDay() {
        seedFinishedSession(com.example.data.SessionOutcome.ELAPSED_CLEAN, endOffsetMs = 400)
        setRootApp()
        assertTrue(
            "the finish machine must issue the seeded finish's receipt",
            pumpForReceipt()
        )
        settle(900)
        captureTo(File(shotsDir, "08_receipt.png"))
    }

    @Test
    fun receiptSlipNight() {
        val nightDir = File(shotsDir, "night")
        nightDir.mkdirs()
        // Night the phone's own way (the standing rig pattern): a
        // SLEEP routine whose window owns the current wall time.
        val nowMin = java.util.Calendar.getInstance().let {
            it.get(java.util.Calendar.HOUR_OF_DAY) * 60 + it.get(java.util.Calendar.MINUTE)
        }
        kotlinx.coroutines.runBlocking {
            db.monolithDao().insertRoutines(
                listOf(
                    com.example.data.RoutineEntity(
                        name = "SLEEP",
                        startMinutes = (nowMin - 30 + 1440) % 1440,
                        endMinutes = (nowMin + 90) % 1440,
                        daysMask = 127,
                        loadoutName = "SLEEP",
                        enabled = true
                    )
                )
            )
        }
        seedFinishedSession(com.example.data.SessionOutcome.ELAPSED_CLEAN, endOffsetMs = 400)
        setRootApp()
        assertTrue(
            "the finish machine must issue the seeded finish's receipt",
            pumpForReceipt()
        )
        settle(1200)
        println("HARNESS receipt night darkTheme=${viewModel.darkTheme.value}")
        captureTo(File(nightDir, "n08_receipt.png"))
    }

    /** The death-booked slip: the shield was gone for part of the
     *  span, so the books are BROKEN — the E700 pulse slot stands
     *  empty and the slip carries the shield-off line in words. */
    @Test
    fun receiptSlipShieldGapDay() {
        seedFinishedSession(com.example.data.SessionOutcome.ELAPSED_SHIELD_GAP, endOffsetMs = 400)
        setRootApp()
        assertTrue(
            "the finish machine must issue the seeded finish's receipt",
            pumpForReceipt()
        )
        settle(900)
        captureTo(File(shotsDir, "09_receipt_shield_gap.png"))
    }

    /** FABRICATED STATE, named as such: under the rig the faulted
     *  branch cannot occur through the real flow — no shield
     *  service exists, so the pawl reads lifted at the first poll
     *  and the E1520 fault deadline never bites. The faulted slip
     *  (the one with NO title, F-W1) is composed directly over a
     *  hand-built ReceiptState — the PARK sheet's standing
     *  pattern. Evidence for the faulted layout only. */
    @Test
    fun receiptSheetFaultedFabricated() {
        val now = System.currentTimeMillis()
        val receipt = com.example.data.ReceiptState(
            sessionId = 99,
            number = 2,
            kind = com.example.data.FinishKind.MANUAL_FINISH,
            outcomeWord = "ENDED",
            title = receiptIntention,
            armWallMs = now - 3_000_000L,
            endedAtWallMs = now,
            plannedSeconds = 3000,
            sealedElapsedSeconds = 3000,
            heldAppsAtSeal = 10,
            heldSitesAtSeal = 3,
            routineLabelWithin = null,
            bankedBreakSeconds = 0,
            shieldWasOff = false,
            faulted = true,
            deflections = emptyList(),
            restored = false,
            receiptPublishedMonoMs = 0L
        )
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false, dynamicColor = false) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    com.example.ui.components.ReceiptSheet(receipt = receipt, onDismiss = {})
                }
            }
        }
        settle(1200)
        captureTo(File(shotsDir, "10_receipt_faulted_fabricated.png"))
    }

    // ------------------------------------------------------------------
    // HB-4 + TRUTH CAR 2 PROOF (Oct 29) — Band's header-band audit
    // asked for three RootApp frames the rig never produced.
    // Two are produced below. The third — the mid-shot RootApp at
    // t0+525 — is NOT PRODUCED, and the reason is structural, not
    // effort: the shot plays only for a session whose t0 the arm
    // cascade declared in this process (TactileDeckScreen §5.3 —
    // a pre-existing session is born settled and no shot ever
    // replays for it), and t0 requires all three witnesses to
    // answer. The witnesses read the live service singletons
    // (MonolithShieldService.instance,
    // MonolithAccessibilityService.instance — LiveArmWitnesses),
    // which are null under Robolectric because no services run.
    // An arm in the rig therefore jams (proven below) and can
    // never seal. Filing that frame would require fabricating
    // witness state — the one thing this harness does not do.
    // ------------------------------------------------------------------

    /** Pump the shadow clock in 25ms steps until [cond] holds. */
    private fun pumpUntil(timeoutMs: Long = 60_000, cond: () -> Boolean): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            ShadowLooper.idleMainLooper()
            if (cond()) return true
            ShadowSystemClock.advanceBy(Duration.ofMillis(25))
            Thread.sleep(20)
        }
        ShadowLooper.idleMainLooper()
        return cond()
    }

    /** HB-4 frame 2 — the detention reproduction: a finish
     *  detains (FinishState standing, no receipt yet), the Guard
     *  line is tapped mid-detention, and PARK must NOT open over
     *  the held frame. HB-1/HB-2's proof: pre-fix, the header's
     *  door bit read `sealed` alone (false — the session already
     *  ended) and openPark's guard agreed, so this tap covered
     *  the detention with PARK. Post-fix both drink from
     *  doorLocked = sealed || navigationLocked(). */
    @Test
    fun hb4DetentionGuardTap() {
        seedFinishedSession(com.example.data.SessionOutcome.ELAPSED_CLEAN, endOffsetMs = 400)
        setRootApp()
        val detained = pumpUntil {
            viewModel.finishState.value != null && viewModel.receiptState.value == null
        }
        assertTrue(
            "HB-4 setup: the seeded finish must detain before it issues",
            detained
        )
        settle(150)
        captureTo(File(shotsDir, "hb4_detention_before_tap.png"))
        runCatching {
            composeRule.onNodeWithTag("header_guard_line").performClick()
        }.onFailure { println("HARNESS hb4 guard tap raised: $it") }
        settle(300)
        captureTo(File(shotsDir, "hb4_detention_after_guard_tap.png"))
        composeRule.onNodeWithTag("park_sheet").assertDoesNotExist()
        assertTrue(
            "the detained finish must still issue lawfully after the tap",
            pumpForReceipt()
        )
    }

    /** HB-4 frame 3 — the Jammed RootApp: a real arm through the
     *  VM's own entry (the slide's latch target). The witnesses
     *  never answer under the rig, so at L+500 the machine
     *  declares the jam; the frame is captured inside the jam
     *  window, before the L+1500 annulment — whose arrival the
     *  test then proves. */
    @Test
    fun hb4JammedRootApp() {
        setRootApp()
        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.DECK) }
        settle(700)
        val annulBefore = viewModel.armAnnulCount.value
        composeRule.runOnUiThread { viewModel.armMonolithSession() }
        val jammed = pumpUntil {
            viewModel.armPhase.value is com.example.ui.ArmPhase.Jammed
        }
        assertTrue(
            "HB-4: the arm must jam — no witness can answer under the rig",
            jammed
        )
        captureTo(File(shotsDir, "hb4_jammed_rootapp.png"))
        val annulled = pumpUntil(30_000) {
            viewModel.armAnnulCount.value > annulBefore
        }
        assertTrue("HB-4: the jammed arm must annul at L+1500", annulled)
    }

    /** Truth Car 2's Drawer/Tasks proof renders: a stored detail
     *  WITH a historical estimate (the line prints — history is
     *  kept), a stored detail finished at 01:15 with NO estimate
     *  (no ESTIMATE line; the date-line shows the finish's own
     *  date beside 01:15 — DRW-03), and an eleven-sub-step task
     *  on the Tasks screen (the counters run 01…11, never 010 —
     *  DRW-08). The composer state rides the standing captures
     *  (03); the cabinet captures ended with its eviction from the
     *  Drawer (issue #8). */
    @Test
    fun tcDrawerDetailsAndSubSteps() {
        val zone = java.time.ZoneId.systemDefault()
        val today = java.time.LocalDate.now()
        var estimateRowId = -1
        var smallHoursRowId = -1
        kotlinx.coroutines.runBlocking {
            val dao = db.monolithDao()
            estimateRowId = dao.insertTask(
                com.example.data.FocusTaskEntity(
                    title = "Estimate Kept As History",
                    estimatedMinutes = 45,
                    actualSecondsSpent = 2700,
                    categoryTag = "PHYSICS",
                    status = com.example.data.TaskQueueStatus.COMPLETED.name,
                    completedAtMillis = System.currentTimeMillis() - 30 * 60_000
                )
            ).toInt()
            smallHoursRowId = dao.insertTask(
                com.example.data.FocusTaskEntity(
                    title = "Finished Past Midnight",
                    estimatedMinutes = 0,
                    actualSecondsSpent = 1800,
                    categoryTag = "BIOLOGY",
                    status = com.example.data.TaskQueueStatus.COMPLETED.name,
                    completedAtMillis = today.atTime(1, 15).atZone(zone)
                        .toInstant().toEpochMilli()
                )
            ).toInt()
            dao.insertTask(
                com.example.data.FocusTaskEntity(
                    title = "Eleven Step Ladder",
                    notesOrSubsteps = (1..11).joinToString("\n") { "[ ] Step $it" },
                    estimatedMinutes = 0,
                    categoryTag = "CHEMISTRY",
                    // First in the queue: the active leaf, fully
                    // rendered with all eleven sub-step rows.
                    queueOrder = -100,
                    status = com.example.data.TaskQueueStatus.QUEUED.name
                )
            )
        }
        setRootApp()
        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.VAULT) }
        settle(900)
        composeRule.onNodeWithTag("drawer_row_$estimateRowId").performClick()
        settle(700)
        captureTo(File(shotsDir, "tc_drawer_detail_estimate.png"))
        composeRule.onNodeWithTag("drawer_detail_back").performClick()
        settle(700)
        // The small-hours row files under yesterday — reach it
        // through search, which reads every stored row.
        composeRule.onNodeWithTag("drawer_search_input")
            .performTextInput("Finished Past Midnight")
        settle(700)
        composeRule.onNodeWithTag("drawer_row_$smallHoursRowId").performClick()
        settle(700)
        captureTo(File(shotsDir, "tc_drawer_detail_small_hours.png"))
        composeRule.onNodeWithTag("drawer_detail_back").performClick()
        settle(500)
        composeRule.runOnUiThread { viewModel.navigateTo(MonolithDestination.TASKS) }
        settle(900)
        captureTo(File(shotsDir, "tc_tasks_eleven_steps.png"))
    }

    // ------------------------------------------------------------------
    // THE CENSUS (chrome C7 §8.2) — a permanent test utility over the
    // harness's own PNG output. The accent set is the locked four:
    // day accent #F55314, night accent #F47442, deep step #D63E06,
    // and #FF8A5C (banned outright — it left the scheme with C7).
    // Budgets are per screen per mode: the Deck carries TWO lawful
    // accent clusters (the slide cap + the SealBar warning bed —
    // re-based in KONTO-2 round 6, see the deck law below); commit-
    // verb TEXT (START, UNDO) is glyph area;
    // every other surface is zero beyond AA fringe.
    // ------------------------------------------------------------------
    @Test
    fun zzAccentCensus() {
        val accent = setOf(0xFFF55314.toInt(), 0xFFF47442.toInt(), 0xFFD63E06.toInt())
        val banned = 0xFFFF8A5C.toInt()

        fun countIn(file: File, region: (Int, Int, Int, Int) -> Boolean): Pair<Int, Int> {
            val img = ImageIO.read(file) ?: error("census: cannot read ${file.name}")
            var accentCount = 0
            var bannedCount = 0
            var y = 0
            while (y < img.height) {
                var x = 0
                while (x < img.width) {
                    if (region(x, y, img.width, img.height)) {
                        val px = img.getRGB(x, y)
                        if (px in accent) accentCount++
                        if (px == banned) bannedCount++
                    }
                    x += 2
                }
                y += 2
            }
            // Sampling every 2px: counts are quarter-density.
            return (accentCount * 4) to (bannedCount * 4)
        }

        data class Case(val file: File, val label: String, val maxAccent: Int)
        val everywhere = { _: Int, _: Int, _: Int, _: Int -> true }
        val cases = mutableListOf<Case>()
        val nightDir = File(shotsDir, "night")
        // Zero-fill screens: Shield, Drawer, PARK — fringe allowance only.
        for ((f, label) in listOf(
            File(shotsDir, "02_shield.png") to "shield day",
            File(nightDir, "n02_shield.png") to "shield night",
            File(shotsDir, "04_vault.png") to "drawer day",
            File(nightDir, "n04_vault.png") to "drawer night",
            File(shotsDir, "05_park.png") to "park day",
            File(nightDir, "n05_park.png") to "park night",
            File(shotsDir, "03b_tasks_empty.png") to "tasks-empty day",
            File(nightDir, "n03b_tasks_empty.png") to "tasks-empty night"
        )) {
            if (f.isFile) cases += Case(f, label, 120)
        }
        val failures = mutableListOf<String>()
        for (case in cases) {
            val (a, b) = countIn(case.file, everywhere)
            println("CENSUS ${case.label}: accent=$a banned=$b (allowance ${case.maxAccent})")
            if (b > 0) failures += "${case.label}: #FF8A5C present ($b px) — banned colour on screen"
            if (a > case.maxAccent) failures += "${case.label}: accent $a px over allowance ${case.maxAccent}"
        }

        // The Deck (KONTO-2 round 6, re-based for THE BOLT #4 wave 2):
        // TWO lawful accent clusters, not one — the slide cap
        // (84x56dp, slide zone) and the SealBar warning bed (#D63E06,
        // 90x36dp at the track's shut end, mode-invariant by design,
        // SealBar.kt §1/§6). The C7 census law predates the bed; its
        // one-band accounting was never re-based and read the bed's
        // designed paint as a leak (run 41027322051: 28,564px across
        // a 117dp two-cluster span, foot 255dp). The law now
        // measures row-clusters: accent occupies exactly two
        // clusters, each within its own budget and height, joined by
        // an accent-free gap of >= 20dp, and the foot is re-pinned
        // to the bed top (260dp bound).
        for ((f, label) in listOf(
            File(shotsDir, "01_deck.png") to "deck day",
            File(nightDir, "n01_deck.png") to "deck night"
        )) {
            if (!f.isFile) continue
            val img = ImageIO.read(f)
            val ppd = img.width / 412f
            var total = 0
            var bannedCount = 0
            // Row-scan (2px sampling): maximal runs of accent-bearing
            // rows, joining runs whose gap is <= 1 sampled row space.
            data class Cluster(var top: Int, var bottom: Int, var px: Int)
            val clusters = mutableListOf<Cluster>()
            var y = 0
            while (y < img.height) {
                var rowPx = 0
                var x = 0
                while (x < img.width) {
                    val px = img.getRGB(x, y)
                    if (px in accent) rowPx += 4
                    if (px == banned) bannedCount += 4
                    x += 2
                }
                if (rowPx > 0) {
                    total += rowPx
                    val cur = clusters.lastOrNull()
                    if (cur != null && y - cur.bottom <= 4) {
                        cur.bottom = y
                        cur.px += rowPx
                    } else {
                        clusters += Cluster(y, y, rowPx)
                    }
                }
                y += 2
            }
            val heights = clusters.map { (it.bottom - it.top) / ppd }
            val gapDp = if (clusters.size >= 2)
                (clusters[1].top - clusters[0].bottom) / ppd else 0f
            val footDp = if (clusters.isNotEmpty())
                (img.height - clusters.first().top) / ppd else 0f
            println("CENSUS $label: deck accent total=$total clusters=${clusters.size} " +
                "heights=${heights.map { "%.0f".format(it) }} gap=${gapDp}dp " +
                "foot=${footDp}dp banned=$bannedCount")
            if (bannedCount > 0) failures += "$label: #FF8A5C present"
            if (total < 500) failures += "$label: the slide cap is missing (accent $total px)"
            if (clusters.size != 2) failures +=
                "$label: ${clusters.size} accent clusters — the cap and the Bolt bed are the only two lawful"
            if (clusters.any { it.px > 26_000 }) failures +=
                "$label: a cluster over the 26k budget (cap ceiling): ${clusters.map { it.px }}"
            if (clusters.size >= 2 && clusters[0].px > 12_100) failures +=
                "$label: the warning bed cluster is ${clusters[0].px}px — over its ~12.1k budget"
            if (heights.any { it > 80f }) failures +=
                "$label: accent cluster ${heights.max()}dp tall — over the 80dp-cluster bound"
            if (clusters.size >= 2 && gapDp < 20f) failures +=
                "$label: cap and bed clusters are only ${gapDp}dp apart — the seat gap is gone"
            if (footDp > 260f) failures +=
                "$label: accent reaches ${footDp}dp up — above the pinned stack (bed top)"
        }

        // Tasks: START's text is the only accent (glyph area, small);
        // the rail band itself is accent-free.
        for ((f, label) in listOf(
            File(shotsDir, "03_tasks.png") to "tasks day",
            File(nightDir, "n03_tasks.png") to "tasks night"
        )) {
            if (!f.isFile) continue
            val (a, b) = countIn(f, everywhere)
            println("CENSUS $label: accent=$a banned=$b (START text only)")
            if (b > 0) failures += "$label: #FF8A5C present ($b px)"
            if (a > 4000) failures += "$label: accent $a px — more than a verb's glyphs"
        }

        assertEquals(
            "accent census failures:\n" + failures.joinToString("\n"),
            0, failures.size
        )
    }
}
