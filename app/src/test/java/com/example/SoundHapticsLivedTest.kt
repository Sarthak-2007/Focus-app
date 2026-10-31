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
import com.example.ui.MonolithViewModel
import com.example.ui.completionPulseDelayMs
import com.example.ui.components.ParkDrawer
import com.example.ui.peekWarningTickDue
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.views.SledSwitchView
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

    private fun newVm(engine: TactileSoundEngine): Pair<MonolithViewModel, MonolithDatabase> {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repository = MonolithRepository(context, db.monolithDao())
        return MonolithViewModel(context, repository, engine) to db
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
        val (vm, db) = newVm(engine)
        try {
            setMusicVolume(0)
            setParkContent(vm, soundEnabled = true)
            settlePark()
            composeRule.onNodeWithText("ON · SILENT").assertExists()
        } finally {
            db.close()
        }
    }

    @Test
    fun ParkPrintsPlainOnWhenThePhoneCanBeHeard() {
        val engine = TactileSoundEngine(context)
        val (vm, db) = newVm(engine)
        try {
            setMusicVolume(6)
            setParkContent(vm, soundEnabled = true)
            settlePark()
            composeRule.onAllNodesWithText("ON · SILENT").assertCountEquals(0)
            composeRule.onAllNodesWithText("ON")[0].assertExists()
        } finally {
            db.close()
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
        val (vm, db) = newVm(engine)
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
            db.close()
        }
    }

    // =================================================================
    // F3 — THE SEAL CLUNK IS RESERVED
    // =================================================================

    @Test
    fun CueingATaskSpeaksTheLatchNeverTheSealClunk() {
        val engine = TactileSoundEngine(context)
        val (vm, db) = newVm(engine)
        try {
            vm.cueTaskAndNavigateToFocus(1)
            assertEquals(
                "housekeeping spends the latch (760 Hz x 705), " +
                    "never the seal clunk (360 Hz x 2116)",
                760f to 705, engine.lastEnqueuedTransientForTest()
            )
        } finally {
            db.close()
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
