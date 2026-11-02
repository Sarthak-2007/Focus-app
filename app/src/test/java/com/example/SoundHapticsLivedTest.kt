package com.example

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.AudibilityProbe
import com.example.service.SessionSealTransitions
import com.example.service.SoundAudibility
import com.example.service.SoundEffectsSetting
import com.example.service.TactileSoundEngine
import com.example.service.WatchStore
import com.example.ui.MonolithDestination
import com.example.ui.MonolithViewModel
import com.example.ui.completionPulseDelayMs
import com.example.ui.components.ParkDrawer
import com.example.ui.peekWarningTickDue
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.views.SledSwitchView
import kotlinx.coroutines.Job
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
import org.robolectric.shadows.ShadowLooper

/**
 * Issue #112 — Audit R3, journey sound-haptics-lived: the five
 * findings, each with its discriminating test. Written against
 * the trunk failures they cure:
 *
 *  F1  SOUND: ON printed over a provably silent phone — no
 *      volume/DND check existed anywhere.
 *  F2  Every PARK sled flip spoke twice (the view's firm click,
 *      then the handler's voice), and the sound sled was
 *      asymmetric: muting clicked loudly under the still-ON
 *      gate, unmuting's view click was swallowed by it.
 *  F3  The session-seal clunk (360 Hz × 2116 samples) was spent
 *      on undo/cue/loadout housekeeping while the real seal
 *      speaks the Throw.
 *  F4  The completion pulse fired only inside a 900 ms window —
 *      finish a moment too slowly and it was silently deleted.
 *  F5  A peek buzzed a sounding detent every 15 s from an
 *      invisible app and "sealed" (arming clunk) at expiry.
 *
 * Voice signatures asserted below come from the engine's own
 * recipes: latch engaged 760 Hz × 705 samples, latch released
 * 520 Hz × 705, seal clunk 360 Hz × 2116, rotary detent (720 Hz
 * confirmation) 720 Hz × 264.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SoundHapticsLivedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var context: Context

    // Set by setParkContent: awaitTrue drives the compose
    // scheduler on each poll only while a composition is live
    // (the SoundAndSocialTruthTest parkComposed discipline).
    private var parkComposed = false

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        SessionSealTransitions.resetForTest()
        WatchStore.resetForTest()
        SoundEffectsSetting.setEnabled(context, true)
    }

    @After
    fun tearDown() {
        SessionSealTransitions.resetForTest()
        WatchStore.resetForTest()
    }

    /**
     * Test-local factory — SUCCESSOR-12's ruling, option (a), on
     * DIAG-S12-HAPTICS §5. The production Factory mints its own
     * TactileSoundEngine inside create() (MonolithViewModel.kt,
     * Issue #27) and the VM's soundEngine is an immutable val, but
     * the two engine-observing cases below assert on the engine
     * THEY construct: under the production Factory,
     * vm.soundEngine !== engine, the per-instance transient
     * counters they read would never move, and both cases would
     * fail deterministically. This factory constructs the VM with
     * the test's own engine — exactly the instance the old direct
     * construction passed — so the observed subject of every
     * assertion is unchanged. What changes is only OWNERSHIP: the
     * VM is obtained through a ViewModelProvider over a per-test
     * ViewModelStore, so teardown can end its lifecycle for real
     * instead of the VM outliving its test with its scope live.
     */
    private class TestEngineFactory(
        private val appContext: Context,
        private val repository: MonolithRepository,
        private val engine: TactileSoundEngine
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(MonolithViewModel::class.java)) {
                "TestEngineFactory builds only MonolithViewModel, not $modelClass"
            }
            return MonolithViewModel(appContext, repository, engine) as T
        }
    }

    private fun newVm(
        engine: TactileSoundEngine
    ): Triple<MonolithViewModel, MonolithDatabase, ViewModelStore> {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repository = MonolithRepository(context, db.monolithDao())
        val store = ViewModelStore()
        val vm = ViewModelProvider(store, TestEngineFactory(context, repository, engine))
            .get(MonolithViewModel::class.java)
        return Triple(vm, db, store)
    }

    /** Poll-with-looper-idle until [condition] holds — the
     *  SoundAndSocialTruthTest awaitTrue idiom, in its form:
     *  under Robolectric the looper is parked, so wall-clock
     *  polling alone stalls the coroutines being awaited; driving
     *  the compose scheduler on every poll (while a composition
     *  is live) advances that work deterministically. The 30 s
     *  deadline is the suite standard, not widened. */
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

    /**
     * THE TEARDOWN — the one helper every VM-owning case in this
     * class closes through (DIAG-S12-HAPTICS §5, as ruled by
     * SUCCESSOR-12). It cures the harness defect the diagnosis
     * proved: this fixture built the VM bare, so onCleared()
     * never fired and viewModelScope was never cancelled, while
     * the VM launched real Dispatchers.IO database work (the init
     * seed pass, the init collectors, the cue promotion); each
     * case then closed its DB in a bare finally with zero drain,
     * and any still-pending IO coroutine touching the closed pool
     * threw the SQLite closed-pool family as an uncaught
     * exception into whichever test was then executing. The
     * phases, in order:
     *
     * 1. DRAIN THE LAUNCHED WORK, BY CONDITION, scope still live:
     *    the init seed pass's observable product (profiles seeded
     *    — the sibling class's settled condition), plus the
     *    case's own launched work via [launchedWorkDone] (the cue
     *    case passes the promotion's observable completion: the
     *    destination flow reaching DECK, a value the cue launch
     *    sets only after promoteTaskToActive has returned). These
     *    waits observe FINISHED WORK; they must precede the cancel
     *    because a cancelled scope can never complete its
     *    launches — the seed would never land and the destination
     *    would never reach DECK.
     * 2. CANCEL: store.clear() fires onCleared() and the framework
     *    cancels viewModelScope. From this instant no coroutine
     *    can START a database call — every suspending DAO entry
     *    is a cancellation checkpoint — and the standing flow
     *    collectors die with the scope.
     * 3. DRAIN THE CANCELLED SCOPE, BY CONDITION: poll the scope
     *    Job's own children until every one is completed. A child
     *    completes only when its coroutine has fully unwound, so
     *    a coroutine physically inside a DB call at the cancel
     *    instant holds this condition open until that call has
     *    returned — the condition IS the in-flight work, not a
     *    sleep that hopes it finished (the fixed-repeat drain
     *    this replaces only narrowed the race; this closes it).
     * 4. CLOSE: only now may the pool close — no live scope, no
     *    in-flight work, no pending launch remains that could
     *    touch it.
     */
    private fun closeVmAndDb(
        vm: MonolithViewModel,
        store: ViewModelStore,
        db: MonolithDatabase,
        launchedWorkDone: () -> Boolean = { true }
    ) {
        val scopeJob = vm.viewModelScope.coroutineContext[Job]
        awaitTrue("the VM's init seed pass has landed (profiles seeded)") {
            runBlocking { db.monolithDao().getAllProfilesSnapshot() }.isNotEmpty()
        }
        awaitTrue("the case's launched work has completed") { launchedWorkDone() }
        store.clear()
        awaitTrue("the cancelled viewModelScope has drained (no launched work in flight)") {
            scopeJob == null || scopeJob.children.all { it.isCompleted }
        }
        db.close()
    }

    private fun setMusicVolume(volume: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // Public API only: ShadowAudioManager.setStreamVolume is
        // protected in this Robolectric version (CI compile error,
        // run 40986339981); the real call is intercepted by the
        // shadow all the same.
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    // =================================================================
    // F1 — THE AUDIBILITY FACT
    // =================================================================

    @Test
    fun AudibilityTruthTable() {
        // Volume 0 silences, whatever the ringer says.
        assertEquals(
            SoundAudibility.SILENT_VOLUME,
            AudibilityProbe.classify(
                AudioManager.RINGER_MODE_NORMAL, 0,
                NotificationManager.INTERRUPTION_FILTER_ALL
            )
        )
        // Total-silence DND and alarms-only DND both mute media.
        assertEquals(
            SoundAudibility.SILENT_DND,
            AudibilityProbe.classify(
                AudioManager.RINGER_MODE_NORMAL, 7,
                NotificationManager.INTERRUPTION_FILTER_NONE
            )
        )
        assertEquals(
            SoundAudibility.SILENT_DND,
            AudibilityProbe.classify(
                AudioManager.RINGER_MODE_NORMAL, 7,
                NotificationManager.INTERRUPTION_FILTER_ALARMS
            )
        )
        // Priority DND admits sounds; ordinary state is audible.
        assertEquals(
            SoundAudibility.AUDIBLE,
            AudibilityProbe.classify(
                AudioManager.RINGER_MODE_NORMAL, 7,
                NotificationManager.INTERRUPTION_FILTER_PRIORITY
            )
        )
        // The ringer alone canNOT silence this engine's stream:
        // vibrate/silent with media volume up still plays, and the
        // probe must not print a silence that isn't true.
        assertEquals(
            SoundAudibility.AUDIBLE,
            AudibilityProbe.classify(
                AudioManager.RINGER_MODE_SILENT, 7,
                NotificationManager.INTERRUPTION_FILTER_ALL
            )
        )
        assertEquals(
            SoundAudibility.AUDIBLE,
            AudibilityProbe.classify(
                AudioManager.RINGER_MODE_VIBRATE, 3,
                NotificationManager.INTERRUPTION_FILTER_ALL
            )
        )
    }

    @Test
    fun EngineAudibilityReadsThePhonesVolume() {
        val engine = TactileSoundEngine(context)
        setMusicVolume(0)
        assertEquals(SoundAudibility.SILENT_VOLUME, engine.currentAudibility())
        setMusicVolume(6)
        assertEquals(SoundAudibility.AUDIBLE, engine.currentAudibility())
    }

    // Split into one setContent per test (WELLE-3 surge fix): the
    // combined version called setContent twice in one test, which
    // this compose-test version rejects with IllegalStateException
    // (CI run 41012970418, failure at the second setParkContent).

    @Test
    fun ParkPrintsSilentWhenThePhoneCannotBeHeard() {
        val engine = TactileSoundEngine(context)
        val (vm, db, store) = newVm(engine)
        try {
            setMusicVolume(0)
            setParkContent(vm, soundEnabled = true)
            settlePark()
            composeRule.onNodeWithText("ON · SILENT").assertExists()
        } finally {
            closeVmAndDb(vm, store, db)
        }
    }

    @Test
    fun ParkPrintsPlainOnWhenThePhoneCanBeHeard() {
        val engine = TactileSoundEngine(context)
        val (vm, db, store) = newVm(engine)
        try {
            setMusicVolume(6)
            setParkContent(vm, soundEnabled = true)
            settlePark()
            composeRule.onAllNodesWithText("ON · SILENT").assertCountEquals(0)
            composeRule.onAllNodesWithText("ON")[0].assertExists()
        } finally {
            closeVmAndDb(vm, store, db)
        }
    }

    // =================================================================
    // F2 — ONE SLED, ONE VOICE
    // =================================================================

    @Test
    fun SledViewFiresNoVoiceOfItsOwn() {
        val engine = TactileSoundEngine(context)
        var handlerCalls = 0
        val sled = SledSwitchView(context)
        sled.soundEngine = engine
        sled.onToggleRequested = { handlerCalls++ }
        val before = engine.enqueuedTransientCountForTest()
        // Main's complete-press gesture law (#56/#60, preserved by the
        // #112 recomposition): a press is DOWN on the sled, then UP on
        // it. The original test sent a bare UP, which main's law now
        // rightly ignores — the voice assertions below are unchanged.
        sled.dispatchTouchEvent(
            MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_DOWN, 0f, 0f, 0)
        )
        sled.dispatchTouchEvent(
            MotionEvent.obtain(0L, 1L, MotionEvent.ACTION_UP, 0f, 0f, 0)
        )
        assertEquals("the gesture must reach the handler exactly once", 1, handlerCalls)
        assertEquals(
            "the sled itself must spend no voice — the handler owns it",
            before, engine.enqueuedTransientCountForTest()
        )
    }

    @Test
    fun SoundToggleSpeaksExactlyOncePerFlipUnderTheNewFact() {
        val engine = TactileSoundEngine(context)
        val (vm, db, store) = newVm(engine)
        try {
            val before = engine.enqueuedTransientCountForTest()
            // ON -> OFF: the latch is attempted once; its SOUND is
            // gated out by the fact just written (sound is now
            // off — the silence is the truth), its haptic fires.
            assertFalse(vm.toggleSound())
            assertEquals(
                "turning OFF must enqueue no sounding voice under the new OFF fact",
                before, engine.enqueuedTransientCountForTest()
            )
            // OFF -> ON: exactly ONE voice — the handler's latch
            // (760 Hz x 705). Trunk spoke twice here in effect:
            // the view's firm click was swallowed by the old gate
            // and the detent (720 Hz x 264) sounded alone.
            assertTrue(vm.toggleSound())
            assertEquals(
                "turning ON must enqueue exactly one voice",
                before + 1, engine.enqueuedTransientCountForTest()
            )
            assertEquals(
                "the one voice is the latch, not the old detent",
                760f to 705, engine.lastEnqueuedTransientForTest()
            )
        } finally {
            closeVmAndDb(vm, store, db)
        }
    }

    // =================================================================
    // F3 — THE SEAL CLUNK IS RESERVED
    // =================================================================

    @Test
    fun CueingATaskSpeaksTheLatchNeverTheSealClunk() {
        val engine = TactileSoundEngine(context)
        val (vm, db, store) = newVm(engine)
        try {
            vm.cueTaskAndNavigateToFocus(1)
            assertEquals(
                "housekeeping spends the latch (760 Hz x 705), " +
                    "never the seal clunk (360 Hz x 2116)",
                760f to 705, engine.lastEnqueuedTransientForTest()
            )
        } finally {
            // The cue launch's observable completion (§5.1(b)):
            // cueTaskAndNavigateToFocus sets the destination to
            // DECK only after promoteTaskToActive has returned, so
            // DECK standing proves the promotion's IO is finished
            // — the teardown then cancels, drains, and closes over
            // completed work, never over this launch in flight.
            closeVmAndDb(vm, store, db) {
                vm.currentDestination.value == MonolithDestination.DECK
            }
        }
    }

    // =================================================================
    // F4 — THE COMPLETION PULSE IS OWED, NOT WINDOWED
    // =================================================================

    @Test
    fun CompletionPulseDelayClampsToLateNeverDropped() {
        assertEquals(700L, completionPulseDelayMs(0L))
        assertEquals(50L, completionPulseDelayMs(650L))
        assertEquals(0L, completionPulseDelayMs(700L))
        // The trunk failure: past 900 ms the pulse was deleted.
        // Now any lateness still fires — the delay is 0 (fire
        // immediately), never a skip.
        assertEquals(0L, completionPulseDelayMs(900L))
        assertEquals(0L, completionPulseDelayMs(5_000L))
    }

    // =================================================================
    // F5 — THE PEEK WARNS BY FEEL, TWICE; IT NEVER "SEALS"
    // =================================================================

    @Test
    fun PeekWarningCadenceIsThirtyAndFifteenOnly() {
        assertTrue(peekWarningTickDue(30))
        assertTrue(peekWarningTickDue(15))
        // The trunk cadence: every 15 s from 105 down.
        assertFalse(peekWarningTickDue(105))
        assertFalse(peekWarningTickDue(90))
        assertFalse(peekWarningTickDue(75))
        assertFalse(peekWarningTickDue(60))
        assertFalse(peekWarningTickDue(45))
        assertFalse(peekWarningTickDue(14))
        assertFalse(peekWarningTickDue(1))
    }

    @Test
    fun PeekWarningTickSpendsNoVoice() {
        val engine = TactileSoundEngine(context)
        val before = engine.enqueuedTransientCountForTest()
        engine.playPeekWarningTick()
        assertEquals(
            "the peek warning is felt, never heard: nothing enqueued",
            before, engine.enqueuedTransientCountForTest()
        )
    }

    // =================================================================
    // PARK harness (the SoundAndSocialTruthTest idiom)
    // =================================================================

    private fun setParkContent(vm: MonolithViewModel, soundEnabled: Boolean) {
        parkComposed = true
        composeRule.setContent {
            val socialOn by vm.browserUrlBlockerEnabled.collectAsState()
            val snapshot by WatchStore.snapshots.collectAsState()
            MyApplicationTheme(darkTheme = false) {
                ParkDrawer(
                    visible = true,
                    snapshot = snapshot,
                    isSealed = false,
                    isNight = false,
                    soundEnabled = soundEnabled,
                    shortsReelsSniperEnabled = true,
                    soundEngine = vm.soundEngine,
                    socialSitesEnabled = socialOn,
                    onToggleSocialSites = { vm.toggleBrowserUrlBlocker() },
                    themeReadout = "AUTO · DAY — NO ROUTINE OWNS IT",
                    parkReturnTick = 0,
                    onDismiss = {},
                    onFixClick = {},
                    // Main added the Guard CHECK verb (onCheckClick,
                    // required) after this harness was written; the
                    // harness passes it as a no-op like every other
                    // callback here — assertions unchanged.
                    onCheckClick = {},
                    onToggleSound = {},
                    onToggleShortsReelsSniper = {}
                )
            }
        }
    }

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
}
