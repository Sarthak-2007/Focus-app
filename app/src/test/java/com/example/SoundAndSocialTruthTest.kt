package com.example

import android.content.Context
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.InterceptorState
import com.example.service.PermPrint
import com.example.service.SessionSealTransitions
import com.example.service.SoundEffectsSetting
import com.example.service.TactileSoundEngine
import com.example.service.WatchItemId
import com.example.service.WatchStore
import com.example.ui.MonolithViewModel
import com.example.ui.components.ParkDrawer
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper

/**
 * Truth Car 3 — UNA-03 (sound) + UNA-02 (social sites): the
 * discriminating tests (Pruefer's proof standard, plan/
 * TRUTH3-PROOF-STANDARD.md). Every case here is written against
 * the trunk failure it cures:
 *
 *  - UNA-03: on trunk, toggleSound flipped one engine's var and
 *    nothing persisted; a heartbeat-born process bound a fresh
 *    default-ON engine and PARK printed OFF over sounding seals.
 *  - UNA-02: on trunk, the social-sites switch rendered nowhere
 *    (a dead parameter on Deck/Shield), so no surface path to
 *    the persisted fact existed at all.
 *
 * The sound fact's storage medium is a SharedPreferences store
 * (SoundEffectsSetting — Ren's ratified deviation from the
 * joint-migration manifest; the charge is behavioural:
 * persistence + every engine honouring the fact, medium
 * notwithstanding). These tests observe behaviour only.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
// Test-floor cures (rounds 2+4, proven against run
// 40994003589's test XMLs): the PARK sleds are raw AndroidViews
// whose switch fires only on a real MotionEvent ACTION_UP, so the
// class runs in NATIVE graphics (real interop rendering) and the
// taps are dispatched straight to the SledSwitchView
// (tapParkSled). The ViewModel's coroutines — the toggle's Room
// write, the WatchStore wiring — are driven by waitForIdle inside
// awaitTrue; wall-clock polling alone left them unadvanced for
// the full 30 s timeout. And every DB is closed through closeDb,
// which drains both schedulers first: closing under in-flight
// coroutines threw pool-closed exceptions that poisoned the next
// case with UncaughtExceptionsBeforeTest.
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SoundAndSocialTruthTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        SessionSealTransitions.resetForTest()
        WatchStore.resetForTest()
        // Normalise the fact to its shipped default (ON) so each
        // case starts from the same world.
        SoundEffectsSetting.setEnabled(context, true)
    }

    @After
    fun tearDown() {
        SessionSealTransitions.resetForTest()
        WatchStore.resetForTest()
    }

    // =================================================================
    // UNA-03 — SOUND EFFECTS IS A FACT
    // =================================================================

    /** Pruefer 03-1: set OFF; fresh engine + fresh VM — the print
     *  state is OFF and the engine's flag is false. On trunk the
     *  flag defaults true and the VM re-opens ON. */
    @Test
    fun SoundSettingPersistsAcrossProcessDeath() {
        SoundEffectsSetting.setEnabled(context, false)
        assertFalse(SoundEffectsSetting.isEnabled(context))

        // A new process is, for the engine, a new construction:
        // it must be born from the persisted fact.
        val rebornEngine = TactileSoundEngine(context)
        assertFalse(
            "a fresh engine must be born OFF when the fact is OFF",
            rebornEngine.soundEnabled
        )

        // And a fresh VM's print state (PARK's word) is the fact.
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repository = MonolithRepository(context, db.monolithDao())
            val vm = MonolithViewModel(context, repository, rebornEngine)
            assertFalse(
                "a fresh VM must print OFF when the fact is OFF",
                vm.soundEnabled.value
            )
            assertFalse(vm.soundEngine.soundEnabled)
        } finally {
            closeDb(db)
        }
    }

    /** Pruefer 03-2 (behavioural): bind the seal transition via
     *  the HEARTBEAT path first — get() with no engine, the exact
     *  call at RoutineHeartbeat:142 — then the VM path. The one
     *  engine the seals sound through honours the stored OFF.
     *  On trunk, first-caller-wins bound a sound-on engine. */
    @Test
    fun SoundEngineBirthOrder() {
        SoundEffectsSetting.setEnabled(context, false)
        SessionSealTransitions.resetForTest()

        // First caller: the heartbeat, engine-less.
        SessionSealTransitions.get(context)
        assertEquals(
            "the heartbeat-born transition must bind a silent engine when the fact is OFF",
            false,
            SessionSealTransitions.boundEngineSoundEnabledForTest()
        )

        // Second caller: the VM path — a VM constructed now hands
        // its own engine to get(); the standing transition (and
        // the VM's print) must still honour OFF.
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repository = MonolithRepository(context, db.monolithDao())
            val vmEngine = TactileSoundEngine(context)
            val vm = MonolithViewModel(context, repository, vmEngine)
            assertEquals(
                false,
                SessionSealTransitions.boundEngineSoundEnabledForTest()
            )
            assertFalse(vmEngine.soundEnabled)
            assertFalse("PARK's word (the VM flow) must agree", vm.soundEnabled.value)
        } finally {
            closeDb(db)
        }
    }

    /** Pruefer 03-3: every get() shape returns an engine matching
     *  the persisted flag — including a bind AFTER an out-of-band
     *  fact change the push never saw (the bind re-read). */
    @Test
    fun EveryEngineGetReadsTheSetting() {
        SoundEffectsSetting.setEnabled(context, false)
        SessionSealTransitions.resetForTest()

        SessionSealTransitions.get(context)
        assertEquals(false, SessionSealTransitions.boundEngineSoundEnabledForTest())

        val handedEngine = TactileSoundEngine(context)
        SessionSealTransitions.get(context, handedEngine)
        assertEquals(false, SessionSealTransitions.boundEngineSoundEnabledForTest())
        assertFalse(handedEngine.soundEnabled)

        // Out-of-band write (bypasses setEnabled's push — e.g. a
        // restore): the standing engine's cache is stale until
        // the next bind re-reads the fact into it.
        context.getSharedPreferences(
            SoundEffectsSetting.PREFS_NAME, Context.MODE_PRIVATE
        ).edit().putBoolean(SoundEffectsSetting.KEY_ENABLED, true).commit()
        SessionSealTransitions.get(context)
        assertEquals(
            "a bind must re-read the persisted fact into the standing engine",
            true,
            SessionSealTransitions.boundEngineSoundEnabledForTest()
        )
    }

    /** The toggle's write path: one fact-write reaches EVERY live
     *  engine — the VM-door engine and the transition's engine
     *  alike — in both directions. */
    @Test
    fun FactWriteReachesEveryLiveEngine() {
        val vmDoorEngine = TactileSoundEngine(context)
        val transitionEngine = TactileSoundEngine(context)
        SessionSealTransitions.resetForTest()
        SessionSealTransitions.get(context, transitionEngine)

        SoundEffectsSetting.setEnabled(context, false)
        assertFalse(vmDoorEngine.soundEnabled)
        assertFalse(transitionEngine.soundEnabled)

        SoundEffectsSetting.setEnabled(context, true)
        assertTrue(vmDoorEngine.soundEnabled)
        assertTrue(transitionEngine.soundEnabled)
    }

    /** The gate every playback path reads: while the fact is OFF,
     *  no transient enters the engine's queue at all (the synth
     *  loop's and playSealThrow's `soundEnabled` checks read the
     *  same gate). */
    @Test
    fun TransientsDroppedWhileFactOff() {
        SoundEffectsSetting.setEnabled(context, false)
        val engine = TactileSoundEngine(context)
        val before = engine.pendingCommandCount()
        engine.playRotaryDetent(680.0)
        engine.playSeatKnock(20, 1.0f, 320f)
        assertEquals(
            "with the fact OFF, no transient may enter the queue",
            before,
            engine.pendingCommandCount()
        )
    }

    // =================================================================
    // UNA-02 — SOCIAL SITES HAS ITS SWITCH
    // =================================================================

    /** The VM-level chain, on real objects: PARK's new switch
     *  calls the same toggle that writes guard_settings; the
     *  blocker reads the same column fresh per event; the VM's
     *  published print is that column's mirror. */
    @Test
    fun SocialToggleChainWritesTheFactTheBlockerReads() {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repository = MonolithRepository(context, db.monolithDao())
            val vm = MonolithViewModel(context, repository, TactileSoundEngine(context))

            awaitTrue("the VM publishes the persisted fact (fresh install: ON)") {
                vm.browserUrlBlockerEnabled.value &&
                    runBlocking { repository.isBrowserBlockerEnabled() }
            }
            runBlocking { vm.toggleBrowserUrlBlocker() }
            awaitTrue("the toggle writes the stored fact OFF") {
                !runBlocking { repository.isBrowserBlockerEnabled() }
            }
            awaitTrue("and the published print follows the fact") {
                !vm.browserUrlBlockerEnabled.value
            }
            runBlocking { vm.toggleBrowserUrlBlocker() }
            awaitTrue("and back ON, print following") {
                runBlocking { repository.isBrowserBlockerEnabled() } &&
                    vm.browserUrlBlockerEnabled.value
            }
        } finally {
            closeDb(db)
        }
    }

    /** Pruefer 02-1: the toggle physically RENDERED in PARK —
     *  touching it changes the stored Room value, both ways.
     *  There is no such node on trunk: the parameter rendered
     *  nowhere. */
    @Test
    fun SocialToggleRenderedAndWritesThrough() {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repository = MonolithRepository(context, db.monolithDao())
            val vm = MonolithViewModel(context, repository, TactileSoundEngine(context))
            awaitTrue("the VM publishes the persisted fact before render") {
                vm.browserUrlBlockerEnabled.value ==
                    runBlocking { repository.isBrowserBlockerEnabled() }
            }
            setParkContent(vm)

            composeRule.onNodeWithTag("park_social_sled").assertExists()
            settlePark()

            tapParkSled()
            awaitTrue("touching the rendered sled writes the stored fact OFF") {
                !runBlocking { repository.isBrowserBlockerEnabled() }
            }

            tapParkSled()
            awaitTrue("and a second touch writes it back ON") {
                runBlocking { repository.isBrowserBlockerEnabled() }
            }
        } finally {
            closeDb(db)
        }
    }

    /** Pruefer 02-2: with parents granted, the board's SOCIAL
     *  SITES word is RUNNING while the toggle is on and moves to
     *  OFF when the rendered switch is touched off — the board,
     *  the print and the stored fact agree. */
    @Test
    fun SocialBoardWordFollowsTheToggle() {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repository = MonolithRepository(context, db.monolithDao())
            val vm = MonolithViewModel(context, repository, TactileSoundEngine(context))

            // Parents granted by a full sweep (the sweep's query
            // decides per item). The VM's own init sweep may land
            // real (ungranted) prints first — re-sweep until the
            // granted set holds and the word derives RUNNING.
            awaitTrue("granted parents + fact ON derive RUNNING") {
                val snap = WatchStore.snapshots.value
                val nowMono = SystemClock.elapsedRealtime()
                if (WatchStore.permPrintAt(snap, WatchItemId.ACCESSIBILITY, nowMono) !=
                    PermPrint.GRANTED ||
                    WatchStore.permPrintAt(snap, WatchItemId.USAGE_ACCESS, nowMono) !=
                    PermPrint.GRANTED
                ) {
                    WatchStore.runSweep { true }
                }
                WatchStore.snapshots.value
                    .measurement(WatchItemId.SOCIAL_SITES)
                    ?.interceptor == InterceptorState.RUNNING
            }

            setParkContent(vm)
            settlePark()
            composeRule.onNodeWithTag("park_social_sled").assertExists()
            composeRule.onNodeWithText("OFF").assertDoesNotExist()

            tapParkSled()

            awaitTrue("the derived board word moves to OFF") {
                WatchStore.snapshots.value
                    .measurement(WatchItemId.SOCIAL_SITES)
                    ?.interceptor == InterceptorState.OFF
            }
            awaitTrue("and the stored fact agrees") {
                !runBlocking { repository.isBrowserBlockerEnabled() }
            }
            settlePark()
            composeRule.onNodeWithText("OFF").assertExists()
        } finally {
            closeDb(db)
        }
    }

    // =================================================================
    // Helpers
    // =================================================================

    /** PARK as MainActivity composes it for this slice: the
     *  social sled wired to the VM's existing toggle (the one
     *  write path to the fact), the board fed reactively. */
    private fun setParkContent(vm: MonolithViewModel) {
        composeRule.setContent {
            val socialOn by vm.browserUrlBlockerEnabled.collectAsState()
            val snapshot by WatchStore.snapshots.collectAsState()
            MyApplicationTheme(darkTheme = false, dynamicColor = false) {
                // shorts = true below is a premise, not filler: with
                // the sniper OFF, the shorts row would print OFF from
                // the first frame and break
                // SocialBoardWordFollowsTheToggle's pre-toggle
                // "OFF does not exist" assertion.
                ParkDrawer(
                    visible = true,
                    snapshot = snapshot,
                    isSealed = false,
                    isNight = false,
                    soundEnabled = true,
                    shortsReelsSniperEnabled = true,
                    soundEngine = vm.soundEngine,
                    socialSitesEnabled = socialOn,
                    onToggleSocialSites = { vm.toggleBrowserUrlBlocker() },
                    themeReadout = "AUTO · DAY — NO ROUTINE OWNS IT",
                    parkReturnTick = 0,
                    onDismiss = {},
                    onFixClick = {},
                    onToggleSound = {},
                    onToggleShortsReelsSniper = {}
                )
            }
        }
    }

    /** Let the sheet's rise animation finish before touching:
     *  parked looper idles + wall-clock, same idiom as the
     *  harness's settle(). */
    private fun settlePark() {
        val deadline = System.currentTimeMillis() + 5_000L
        while (System.currentTimeMillis() < deadline) {
            ShadowLooper.idleMainLooper()
            try {
                composeRule.waitForIdle()
                return
            } catch (t: Throwable) {
                Thread.sleep(50L)
            }
        }
    }

    /** Poll-with-looper-idle until [condition] holds (the
     *  WatchStoreRobolectricTest idiom: under Robolectric the
     *  looper is parked, so wall-clock polling alone stalls the
     *  coroutines being awaited).
     *
     *  Test-floor cure, round 4 (proven on run 40994003589's test
     *  XMLs): looper-idle alone does NOT drive the ViewModel's
     *  coroutines under the Compose test environment — the
     *  toggle's launched Room write and the VM's WatchStore
     *  wiring collectors sat unadvanced for the full 30 s while
     *  the poller slept in wall-clock time, and the failures
     *  rotated between cases run to run. Driving the compose
     *  test scheduler (waitForIdle) on every poll advances that
     *  work deterministically. */
    private fun awaitTrue(what: String, condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + 30_000L
        while (System.currentTimeMillis() < deadline) {
            if (condition()) return
            ShadowLooper.idleMainLooper()
            try {
                composeRule.waitForIdle()
            } catch (t: Throwable) {
                // VM-only cases have no composition to idle;
                // the looper idle above is their driver.
            }
            Thread.sleep(25L)
        }
        throw AssertionError("Timed out waiting for: $what")
    }

    /** Test-floor cure, round 4: tap the PARK social sled by
     *  dispatching real MotionEvents straight to the live
     *  SledSwitchView. The view's onTouchEvent fires the toggle
     *  on any ACTION_UP (BlockitSuitcaseAndSwitchesViews.kt:168);
     *  Compose-test touch injection into the AndroidView interop
     *  surface proved unreliable in CI, so deliver the events to
     *  the view itself, on the UI thread, as a finger would. */
    private fun tapParkSled() {
        composeRule.onNodeWithTag("park_social_sled").assertExists()
        var sled: android.view.View? = null
        composeRule.runOnUiThread {
            fun find(v: android.view.View) {
                if (sled != null) return
                if (v is com.example.ui.views.SledSwitchView) {
                    sled = v
                    return
                }
                if (v is android.view.ViewGroup) {
                    for (i in 0 until v.childCount) find(v.getChildAt(i))
                }
            }
            find(composeRule.activity.window.decorView)
        }
        val view = sled
            ?: throw AssertionError("SledSwitchView not found in the PARK hierarchy")
        val down = SystemClock.uptimeMillis()
        composeRule.runOnUiThread {
            val cx = view.width / 2f
            val cy = view.height / 2f
            val d = android.view.MotionEvent.obtain(
                down, down, android.view.MotionEvent.ACTION_DOWN, cx, cy, 0
            )
            view.dispatchTouchEvent(d)
            d.recycle()
            val u = android.view.MotionEvent.obtain(
                down, SystemClock.uptimeMillis(),
                android.view.MotionEvent.ACTION_UP, cx, cy, 0
            )
            view.dispatchTouchEvent(u)
            u.recycle()
        }
        ShadowLooper.idleMainLooper()
        try {
            composeRule.waitForIdle()
        } catch (t: Throwable) {
            // See awaitTrue.
        }
    }

    /** Test-floor cure, round 4: drain before closing the DB.
     *  Run 40994003589's XMLs proved the poison path: a case's
     *  `finally { db.close() }` landed while its ViewModel's
     *  coroutines (the init fact-load, the wiring collectors)
     *  were still in flight; they then hit the closed pool
     *  (IllegalStateException: connection pool has been closed)
     *  as UNCAUGHT exceptions and the next case died at birth
     *  with UncaughtExceptionsBeforeTest. Idling both schedulers
     *  for a settle window lets the in-flight work finish
     *  against the live pool before it closes. */
    private fun closeDb(db: MonolithDatabase) {
        repeat(40) {
            ShadowLooper.idleMainLooper()
            try {
                composeRule.waitForIdle()
            } catch (t: Throwable) {
                // See awaitTrue.
            }
            Thread.sleep(25L)
        }
        db.close()
    }
}
