package com.example

import android.content.Context
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.InterceptorState
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

    // Floor-lane speed cure (#166, round 6): whether THIS case has
    // composed PARK via setParkContent. The two scheduler drivers
    // below (awaitTrue, closeDb) call composeRule.waitForIdle() to
    // advance the ViewModel's coroutines inside a composition —
    // but in a case that never composes, waitForIdle has no
    // composition to idle and stalls ~2 s per call (measured on
    // the census artifacts: the two VM-only cases that close a DB
    // cost exactly 81.1 s in consecutive censuses = 40 closeDb
    // iterations x the stall, while composed cases with the same
    // closeDb cost ~1 s). For VM-only cases the looper idle is
    // already the driver (round 4's own finding, see awaitTrue);
    // the compose call is skipped there, nothing else changes.
    private var parkComposed = false

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
        // Issue #83: the fact's file moved to device-protected
        // storage — an out-of-band write lands where the bind reads.
        (context.createDeviceProtectedStorageContext() ?: context).getSharedPreferences(
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
    // ISSUE #49 — SOUND ENGINE RESOURCE + FACT HONESTY
    // =================================================================

    /** #49-3: construction is free — no threads until a cue needs
     *  a lane. On the audited tree, init started two threads and
     *  the synth loop opened a PLAYING AudioTrack immediately. */
    @Test
    fun EngineConstructionStartsNoThreads() {
        val engine = TactileSoundEngine(context)
        assertFalse(
            "a freshly constructed engine must not have started its lanes",
            engine.threadsStartedForTest()
        )
        engine.stopAllAudio()
    }

    /** #49-4: the gate is @Volatile (happens-before for the synth
     *  thread's unsynchronized read) — asserted on the field
     *  itself, the exact property the audit found missing. */
    @Test
    fun SoundEnabledGateIsVolatile() {
        val field = TactileSoundEngine::class.java.getDeclaredField("soundEnabled")
        assertTrue(
            "soundEnabled must be @Volatile",
            java.lang.reflect.Modifier.isVolatile(field.modifiers)
        )
        val haptics = TactileSoundEngine::class.java.getDeclaredField("hapticsEnabled")
        assertTrue(
            "hapticsEnabled must be @Volatile",
            java.lang.reflect.Modifier.isVolatile(haptics.modifiers)
        )
    }

    /** #49-2: stopAllAudio is total and final — the engine leaves
     *  the live registry and later cues enqueue nothing. */
    @Test
    fun StopAllAudioRemovesEngineAndSilencesIt() {
        SoundEffectsSetting.setEnabled(context, true)
        val engine = TactileSoundEngine(context)
        val countWhileAlive = TactileSoundEngine.liveEngineCountForTest()
        engine.stopAllAudio()
        assertTrue(
            "a stopped engine must leave the live registry",
            TactileSoundEngine.liveEngineCountForTest() < countWhileAlive
        )
        engine.playRotaryDetent(680.0)
        engine.playSealThrow()
        assertEquals(
            "a stopped engine must accept no further commands",
            0,
            engine.pendingCommandCount()
        )
        engine.stopAllAudio() // idempotent: a second teardown must not throw
    }

    /** #49-5: haptics is a persisted fact with the same push law
     *  as sound — born from the fact, flipped on live engines by
     *  the store, never an always-true stub. */
    @Test
    fun HapticsSettingIsAPersistedFact() {
        assertTrue(SoundEffectsSetting.setHapticsEnabled(context, false))
        assertFalse(SoundEffectsSetting.isHapticsEnabled(context))
        val engine = TactileSoundEngine(context)
        assertFalse("engine must be born from the haptics fact", engine.hapticsEnabled)
        assertTrue(SoundEffectsSetting.setHapticsEnabled(context, true))
        assertTrue("the store's push must reach live engines", engine.hapticsEnabled)
        engine.stopAllAudio()
    }

    /** #49-5: setEnabled reports success truthfully (Boolean) and
     *  a successful write is the one that reaches engines. */
    @Test
    fun SetEnabledReturnsCommitVerdict() {
        val engine = TactileSoundEngine(context)
        assertTrue(SoundEffectsSetting.setEnabled(context, false))
        assertFalse(engine.soundEnabled)
        assertTrue(SoundEffectsSetting.setEnabled(context, true))
        assertTrue(engine.soundEnabled)
        engine.stopAllAudio()
    }

    // =================================================================
    // UNA-02 — SOCIAL SITES HAS ITS SWITCH
    // =================================================================

    /** The VM-level chain, on real objects: PARK's new switch
     *  calls the same toggle that writes guard_settings; the
     *  blocker reads the same column fresh per event; the VM's
     *  published print is that column's mirror.
     *
     *  Floor-lane cure (#166, tenth sitting) — this was the
     *  class's rotating name (red at census #7, run 41182938534:
     *  31.1 s, "Timed out waiting for: and back ON, print
     *  following", while its gated siblings passed in ~1 s).
     *  Mechanism, from the code: the print is born `true`
     *  (MonolithViewModel.kt:696); the init launch assigns it
     *  from the store exactly once, only after the whole seed
     *  pass plus a forced heartbeat tick (:1647); the toggle
     *  negates the PRINT and launches its store write (:2653).
     *  The first poll below is satisfiable born-equal — print
     *  true, stored default true — before that assignment
     *  exists, so under full-suite load the assignment could
     *  land inside the second toggle's write latency, re-read
     *  the store still OFF, and clobber the print back OFF with
     *  no writer left to raise it: the final await's print half
     *  starved for the full 30 s. The seed-settle gate cannot
     *  discharge this case — its equality polls cannot tell a
     *  spent assignment from the born-equal steady state.
     *
     *  The preamble below is a proof, not a bound. The store is
     *  planted OFF before the VM exists; inside this case the
     *  print's only writers are the init assignment and the
     *  toggle, so the print reaching OFF is the assignment's
     *  own signature — it has fired, and it fires once. One
     *  toggle restores ON (print set + write launched), and the
     *  original chain then runs with the assignment dead: each
     *  store write is awaited before the next toggle, and no
     *  third writer exists. No interleave remains. */
    @Test
    fun SocialToggleChainWritesTheFactTheBlockerReads() {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repository = MonolithRepository(context, db.monolithDao())
            // The discharge (see the case doc): plant the fact
            // OFF before the VM exists, so the init publication
            // — the print's one uncontrolled writer — must show
            // itself by following the plant, and is then spent.
            runBlocking { repository.setBrowserBlockerEnabled(false) }
            val vm = MonolithViewModel(context, repository, TactileSoundEngine(context))

            awaitTrue("the init publication lands (the print follows the planted fact OFF)") {
                !vm.browserUrlBlockerEnabled.value
            }
            runBlocking { vm.toggleBrowserUrlBlocker() }
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
            // PARCEL 2 (DIAG-S12-FLAKE-2 §5, step 1): plant the
            // stored fact OFF before the VM exists — the
            // discharge the chain case above already uses against
            // this same writer — so the settle gate can await the
            // init assignment's own footprint (!published)
            // instead of the born-equal pair its old form falsely
            // proved on (see the helper). The gate restores the
            // ON premise through the toggle before returning.
            runBlocking { repository.setBrowserBlockerEnabled(false) }
            val vm = MonolithViewModel(context, repository, TactileSoundEngine(context))
            awaitPublishedFactSettled(vm, repository, db.monolithDao())
            setParkContent(vm)

            composeRule.onNodeWithTag("park_social_sled").assertExists()
            settlePark()

            tapParkSled()
            // PARCEL 2, step 3: the print-flip gate, split off
            // ahead of the stored-fact gate. The print flip is
            // the tap's synchronous consequence; if a tap is ever
            // silently swallowed (the diagnosis's one unexcluded
            // alternative — SledSwitchView's gateCheck returning
            // without toggling), THIS gate takes the timeout and
            // names the tap as the lost event, instead of a
            // downstream state gate.
            awaitTrue("the first tap flipped the published print OFF") {
                !vm.browserUrlBlockerEnabled.value
            }
            awaitTrue("touching the rendered sled writes the stored fact OFF") {
                !runBlocking { repository.isBrowserBlockerEnabled() }
            }

            tapParkSled()
            awaitTrue("the second tap flipped the published print ON") {
                vm.browserUrlBlockerEnabled.value
            }
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
            // PARCEL 2 (DIAG-S12-FLAKE-2 §5, step 1): plant the
            // stored fact OFF before the VM exists, so the settle
            // gate below awaits the init assignment's own
            // footprint (!published) — a value only the
            // assignment's execution can produce — instead of the
            // born-equal pair its old form falsely proved on. The
            // gate restores the ON premise through the toggle
            // before returning, so the sweep gate's derivation is
            // raised to RUNNING by this case's own toggle, not by
            // an assignment that may still be in flight.
            runBlocking { repository.setBrowserBlockerEnabled(false) }
            val vm = MonolithViewModel(context, repository, TactileSoundEngine(context))

            // PARCEL (DIAG-S12-NEWFLAKE §2.2, step 1): the settle
            // gate stands ABOVE the RUNNING gate — moved here from
            // below it, the later duplicate deleted. The VM's init
            // seed assignment is spent and its combine collector
            // has pushed sitesOn = true into the derivation inputs
            // before any derivation is awaited, so the setting
            // input is a settled fact, not a race participant. (The
            // tap below also reads the published print through the
            // toggle — the same reason the rendered case gates on
            // it.)
            awaitPublishedFactSettled(vm, repository, db.monolithDao())

            // PARCEL steps 2–4: one generation-keyed dispatch,
            // coverage premise pinned, pure wait on the completing
            // snapshot (see the helper).
            awaitGrantedSweepDerivesRunning()

            setParkContent(vm)
            settlePark()
            composeRule.onNodeWithTag("park_social_sled").assertExists()
            // The premise, re-pinned to current law (#166, K8's
            // seventh sitting). The original premise — no OFF word
            // anywhere on the composed board before the toggle —
            // was written before issue #109 F1 and has been
            // unmeetable since: the NOTIFICATIONS interceptor is
            // OFF by law whenever no session is capturing (printing
            // RUNNING there was the false print caught on a real
            // phone), so the board lawfully carries that row's OFF
            // from the first frame, session or none. Census #4
            // caught the old premise failing on exactly that row's
            // (NOTIFICATIONS, OFF) texts; a waited form of the same
            // premise (this PR's first run) proved the OFF never
            // leaves in 30 s. What this case actually guards is
            // attribution — the OFF that follows the toggle must be
            // the SOCIAL row's — so premise and verdict are both
            // read on the social row's own (name, word) pair,
            // waited on this class's driven loop like every other
            // condition here. The setParkContent comment records
            // the same discipline for the SHORTS row (its setting
            // staged ON so its word cannot print OFF either).
            awaitTrue("the composed SOCIAL SITES row does not print OFF before the toggle") {
                composeRule.onAllNodes(boardRowPairs("SOCIAL SITES", "OFF"))
                    .fetchSemanticsNodes().isEmpty()
            }

            tapParkSled()

            // PARCEL 2, step 3: the print-flip gate, split off
            // ahead of the derived-OFF gate (whose awaited
            // condition below is byte-untouched). The print flip
            // is the tap's synchronous consequence; if the tap is
            // ever silently swallowed (gateCheck — the
            // diagnosis's one unexcluded alternative for this
            // gate), THIS gate takes the timeout and names the
            // tap as the lost event.
            awaitTrue("the tap flipped the published print OFF") {
                !vm.browserUrlBlockerEnabled.value
            }
            awaitTrue("the derived board word moves to OFF") {
                WatchStore.snapshots.value
                    .measurement(WatchItemId.SOCIAL_SITES)
                    ?.interceptor == InterceptorState.OFF
            }
            awaitTrue("and the stored fact agrees") {
                !runBlocking { repository.isBrowserBlockerEnabled() }
            }
            settlePark()
            // The verdict, on the same pair: the SOCIAL row itself
            // must print OFF. (The old tail — some OFF exists
            // anywhere — could be satisfied by the NOTIFICATIONS
            // row's lawful OFF even if the social row never moved;
            // this form cannot.)
            awaitTrue("the composed SOCIAL SITES row prints OFF after the toggle") {
                composeRule.onAllNodes(boardRowPairs("SOCIAL SITES", "OFF"))
                    .fetchSemanticsNodes().isNotEmpty()
            }
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
        parkComposed = true
        composeRule.setContent {
            val socialOn by vm.browserUrlBlockerEnabled.collectAsState()
            val snapshot by WatchStore.snapshots.collectAsState()
            MyApplicationTheme(darkTheme = false) {
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
                    soundEngine = vm.soundEngine,
                    socialSitesEnabled = socialOn,
                    onToggleSocialSites = { vm.toggleBrowserUrlBlocker() },
                    themeReadout = "AUTO · DAY — NO ROUTINE OWNS IT",
                    parkReturnTick = 0,
                    onDismiss = {},
                    onFixClick = {},
                    onCheckClick = {},
                    onToggleSound = {},
                    shortsReelsSniperEnabled = true,
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
        // Fail loudly (#91): silently giving up let the node
        // assertions below run against a possibly still-rising
        // sheet, passing/failing for timing reasons unrelated to
        // the behaviour under test.
        throw AssertionError("Park sheet did not settle (compose never idled) within 5s")
    }

    /** A board row pairing: a merged node whose texts carry both
     *  the row's name and the given word (census #4's own failure
     *  text shows rows merge exactly so — '[NOTIFICATIONS, OFF…').
     *  Attribution-grade: unlike a bare OFF-text search, it cannot
     *  be satisfied by another row's word. */
    private fun boardRowPairs(name: String, word: String): SemanticsMatcher =
        SemanticsMatcher("board row $name prints $word") { node ->
            if (SemanticsProperties.Text !in node.config) {
                false
            } else {
                val texts = node.config[SemanticsProperties.Text].map { it.text }
                name in texts && word in texts
            }
        }

    /** PARCEL (DIAG-S12-NEWFLAKE §2.2, steps 2–4) — F-01-local:
     *  the wait-restructure for SocialBoardWordFollowsTheToggle's
     *  first gate, replacing the old side-effecting poll (which
     *  re-dispatched a sweep on nearly every 25 ms poll, orphaning
     *  the in-flight generation it was waiting for, and sampled
     *  the transient RUNNING across two different snapshots — the
     *  pair's delivered freshness-window miss).
     *
     *  Step 2 — pin the coverage premise: this case's world is a
     *  phone with a supported browser installed (without one,
     *  SOCIAL SITES lawfully derives OFF and RUNNING is
     *  unmeetable). resetForTest() does not clear
     *  supportedBrowserInstalled, so a false measured by any
     *  earlier case in the JVM would otherwise leak in and veto
     *  RUNNING regardless of sweeps; the pin declares the premise,
     *  the same status as the { true } grant query.
     *
     *  Step 3 — dispatch ONCE, capture the generation, and wait on
     *  a pure condition: the snapshot whose lastCompletedSweepGen
     *  is THIS generation is the very emission whose derivation
     *  was computed at the completing publish from this
     *  generation's fresh, coexisting stamps — reading RUNNING off
     *  it cannot miss the freshness window, because the window's
     *  satisfaction is that snapshot's own completion condition.
     *  One snapshot read per poll; no dispatch, no clock read in
     *  the condition proper.
     *
     *  Step 4 — bounded re-arm for an abandoned set only: if the
     *  dispatched set is abandoned (publish() clears the pending
     *  generation without completing it once its stamps' span
     *  exceeds the stale window), lastCompletedSweepGen never
     *  reaches the captured generation. If 10 s pass from a
     *  dispatch without completion, dispatch once more and adopt
     *  the new generation — at most two re-arms inside the
     *  unchanged 30 s awaitTrue budget (total dispatches ≤ 3,
     *  versus one per poll before). The 30 s deadline does not
     *  move. */
    private fun awaitGrantedSweepDerivesRunning() {
        var sweepGen = -1
        var dispatchWallMs = 0L
        var reArms = 0
        fun dispatch() {
            WatchStore.onBrowserCoverage(true)
            WatchStore.runSweep { true }
            sweepGen = WatchStore.currentSweepGen()
            dispatchWallMs = System.currentTimeMillis()
        }
        dispatch()
        awaitTrue("granted parents + fact ON derive RUNNING") {
            val snap = WatchStore.snapshots.value
            if (snap.lastCompletedSweepGen == sweepGen &&
                snap.measurement(WatchItemId.SOCIAL_SITES)?.interceptor ==
                InterceptorState.RUNNING
            ) {
                true
            } else {
                if (reArms < 2 &&
                    System.currentTimeMillis() - dispatchWallMs >= 10_000L
                ) {
                    reArms++
                    dispatch()
                }
                false
            }
        }
    }

    /** The seed-settle gate for the two rendered social cases,
     *  re-proved by PARCEL 2 (DIAG-S12-FLAKE-2 §5, on the pair
     *  failure of census run 41364375296).
     *
     *  HISTORY, kept because the old text claimed a proof it did
     *  not have: the DIAG-D3 §c2(iii) form of this gate waited
     *  for the seed pass's products (profiles seeded) AND a
     *  published/stored pair reading equal across two consecutive
     *  polls, and concluded "after it, the only writer of the
     *  print in these cases is the toggle itself." That was a
     *  false proof. The print is born `true`
     *  (MonolithViewModel.kt:809) and the stored fact's
     *  fresh-install value is also `true`, so the pair is born
     *  equal: the equality held continuously from the VM's birth
     *  and carried zero information about whether the init
     *  assignment (MonolithViewModel.kt:2302 — executed only
     *  after ensureSeededAndSyncPackages returns whole, plus
     *  five further init steps) had run. Whenever the init
     *  launch's tail outlasted the gate's two polls — exactly
     *  what full-suite load stretches — the assignment landed
     *  mid-chain, re-read the store before a tap's write landed,
     *  and clobbered the print back ON: the rotator's second
     *  touch then re-wrote OFF (its census-#4 mode, at :444) and
     *  F-01's combine collector re-derived RUNNING over the tap
     *  (at :512).
     *
     *  THE PARCEL-2 PROOF is the assignment's own footprint —
     *  the discharge SocialToggleChainWritesTheFactTheBlockerReads
     *  already uses against this same writer: each case plants
     *  the stored fact OFF before the VM exists, so inside this
     *  gate the print's only possible writers are the init
     *  assignment and the toggle, and no toggle has run yet.
     *  Awaiting !published is therefore satisfiable ONLY by the
     *  assignment's own execution: born-equal can no longer
     *  certify anything, because the awaited value differs from
     *  the born value. The false proof is impossible by
     *  construction, not merely less likely.
     *
     *  RESTORE: with the assignment provably spent, the gate
     *  restores the cases' ON premise through the toggle itself
     *  (the tests' own act, not a fixture write) and awaits the
     *  print and the stored fact ON together — a pair check that
     *  is now meaningful, because the only remaining writer is
     *  the toggle. Each case then proceeds with the assignment
     *  dead.
     *
     *  The profiles pre-wait in the body is kept as a cheap
     *  pre-wait only: profiles are seeded inside the same pass
     *  the assignment follows, so awaiting them also drives the
     *  init launch forward — but it is NOT proof of anything and
     *  is never cited as such. (It reads the profiles TABLE,
     *  never the VM's profiles flow: that flow is
     *  collection-driven and stays dark until a composition
     *  collects it — PR #697's first run starved in this gate on
     *  exactly that. The seed pass writes the table directly;
     *  the table is the product.) */
    private fun awaitPublishedFactSettled(
        vm: MonolithViewModel,
        repository: MonolithRepository,
        dao: com.example.data.MonolithDao
    ) {
        awaitTrue("the VM's init seed pass has landed (profiles seeded)") {
            runBlocking { dao.getAllProfilesSnapshot() }.isNotEmpty()
        }
        // PARCEL 2, phase 2 (replaces the born-equal pair proof
        // whose history the doc above keeps): the case planted
        // the stored fact OFF before the VM existed and the print
        // is born true, so only the init assignment's own
        // execution can move the print to OFF. Await exactly that
        // footprint.
        awaitTrue("the init assignment has landed (the print follows the planted fact OFF)") {
            !vm.browserUrlBlockerEnabled.value
        }
        // Restore the ON premise through the toggle itself, and
        // await print and stored fact ON together before
        // returning: from here the only writer of the print in
        // these cases is the toggle.
        vm.toggleBrowserUrlBlocker()
        awaitTrue("the toggle restores the ON premise (print and stored fact ON)") {
            vm.browserUrlBlockerEnabled.value &&
                runBlocking { repository.isBrowserBlockerEnabled() }
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
            if (parkComposed) {
                try {
                    composeRule.waitForIdle()
                } catch (t: Throwable) {
                    // A composition that never idles (infinite
                    // animation) must not stall the poll; the
                    // looper idle above still drove this round.
                }
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
            if (parkComposed) {
                try {
                    composeRule.waitForIdle()
                } catch (t: Throwable) {
                    // See awaitTrue.
                }
            }
            Thread.sleep(25L)
        }
        db.close()
    }
}
