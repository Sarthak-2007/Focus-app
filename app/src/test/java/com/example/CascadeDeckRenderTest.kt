package com.example

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.StudyModePreset
import com.example.service.TactileSoundEngine
import com.example.ui.ArmLink
import com.example.ui.ArmPhase
import com.example.ui.LinkMark
import com.example.ui.screens.TactileDeckScreen
import com.example.ui.theme.MyApplicationTheme
import java.io.File
import java.io.FileOutputStream
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper

/**
 * Slice D step 4 — the cascade's Deck frames, rendered. The
 * harness cannot drive a real latch (no services exist under
 * Robolectric, so the wire never reads LIVE), so this test
 * composes the Deck directly with fabricated phases — the same
 * honesty rule as the lane's other render tests: the frames are
 * evidence for the phase gate's rendering, not for the arm
 * itself. What each frame must show:
 *  - PROVING (row exists, t=0 not declared): the slide tenancy
 *    still stands, no running chips, no sentence, no strike.
 *  - JAMMED: the slot names the unanswering link.
 *  - SEALED (t=0 declared): the running tenancy takes the zone.
 *  - A row with the phase Idle (a routine-born session): runs
 *    exactly as it did before the cascade existed.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CascadeDeckRenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var context: Context

    private val shotsDir: File = run {
        val redirect = listOf(
            File("shots-redirect.txt"),
            File("../shots-redirect.txt"),
            File("app/shots-redirect.txt")
        ).firstOrNull { it.isFile }
        val target = redirect?.readText()?.trim()?.takeIf { it.isNotEmpty() }
        File(target ?: "/home/hatch/workspace/clone-inspect/shots")
    }

    @Before
    fun setup() {
        shotsDir.mkdirs()
        context = ApplicationProvider.getApplicationContext()
    }

    private fun fakeSession() = FocusSessionEntity(
        id = 7,
        intention = "Deep Study",
        profileName = "Default",
        targetDurationSeconds = 3000,
        elapsedSeconds = 30,
        startWallTimeMillis = System.currentTimeMillis(),
        startElapsedRealtimeMillis = 1_000L,
        isActive = true,
        completedSuccessfully = false
    )

    private fun setDeck(phase: ArmPhase, dark: Boolean) {
        val session = fakeSession()
        composeRule.setContent {
            MyApplicationTheme(darkTheme = dark, dynamicColor = false) {
                TactileDeckScreen(
                    activeSession = session,
                    dialMinutes = 50,
                    studyBlocksCount = 2,
                    studyPreset = StudyModePreset.POMODORO_25_5,
                    checklist = emptyList(),
                    activeTask = null,
                    queuedTasks = emptyList(),
                    completedTasksCount = 0,
                    totalQueueMinutes = 0,
                    selectedProfileName = "Default",
                    intentionText = "Deep Study",
                    totalFocusedMinutes = 0,
                    totalInterceptions = 0,
                    profiles = emptyList(),
                    apps = emptyList(),
                    shortsReelsSniperEnabled = true,
                    browserUrlBlockerEnabled = true,
                    capturedNotificationsCount = 0,
                    soundEngine = TactileSoundEngine(context),
                    isDarkTheme = dark,
                    onDialMinutesChanged = {},
                    onSelectStudyPreset = {},
                    onSetStudyBlocksCount = {},
                    onToggleChecklistItem = {},
                    onAddChecklistItem = {},
                    onCompleteActiveTask = {},
                    onDeferActiveTask = {},
                    onExtendActiveTaskOvertime = {},
                    onAddQuickTaskToQueue = { _, _ -> },
                    onToggleTaskSubStep = { _, _ -> },
                    onOpenTaskSequencer = {},
                    onOpenCurioVault = {},
                    onIntentionChanged = {},
                    onProfileSelected = {},
                    onArmSession = {},
                    onRequestDisarm = {},
                    onCancelWithinGraceWindow = {},
                    onOpenAlwaysOnDeskClock = {},
                    onOpenNotificationDigest = {},
                    armPhase = phase
                )
            }
        }
        settle()
    }

    private fun settle(millis: Long = 700) {
        ShadowLooper.idleMainLooper()
        Thread.sleep(millis)
        ShadowLooper.idleMainLooper()
        try {
            composeRule.waitForIdle()
        } catch (_: Throwable) {
            // Capture anyway — the slide's integrator can keep
            // the rule non-idle.
        }
        ShadowLooper.idleMainLooper()
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
        println("CASCADE-RENDER captured $name size=${out.length()}")
    }

    private fun proving(sessionId: Int?) = ArmPhase.Proving(
        attemptId = 1,
        latchMono = 1_000L,
        sessionId = sessionId,
        links = mapOf(
            ArmLink.SESSION_ROW to LinkMark.Confirmed(1_010L),
            ArmLink.SHIELD_PROTECTING to LinkMark.Pending,
            ArmLink.A11Y_SESSION_ARMED to LinkMark.Pending
        )
    )

    private fun jammed() = ArmPhase.Jammed(
        attemptId = 1,
        latchMono = 1_000L,
        sessionId = 7,
        links = mapOf(
            ArmLink.SESSION_ROW to LinkMark.Confirmed(1_010L),
            ArmLink.SHIELD_PROTECTING to LinkMark.Pending,
            ArmLink.A11Y_SESSION_ARMED to LinkMark.Confirmed(1_030L)
        ),
        jamSet = setOf(ArmLink.SHIELD_PROTECTING),
        retryCount = 1
    )

    @Test
    fun provingKeepsTheSlideTenancyAndPrintsNothing() {
        setDeck(proving(sessionId = 7), dark = false)
        // The row exists but t=0 has not fired: the slide zone
        // still holds the slide (not the running chips), and the
        // sentence slot is silent. The running chips are asserted
        // by their testTags, not their text: a chip's label lives
        // inside its clickable's merged semantics, where the text
        // finder cannot see it in ANY state (proven the hard way —
        // the sealed frame renders the valve in pixels while the
        // text query finds nothing); the tag is the chip's
        // designed handle, and the captures carry the text-level
        // evidence.
        composeRule.onNodeWithTag("slide_to_start_view").assertExists()
        composeRule.onNodeWithTag("request_disarm_button").assertDoesNotExist()
        composeRule.onNodeWithTag("grace_abort_button").assertDoesNotExist()
        composeRule.onNodeWithText("THE SHIELD DIDN'T ANSWER — STILL CHECKING.").assertDoesNotExist()
        capture("cascade_proving_day.png")
    }

    @Test
    fun jammedNamesTheUnansweringLink() {
        setDeck(jammed(), dark = false)
        composeRule.onNodeWithText("THE SHIELD DIDN'T ANSWER — STILL CHECKING.").assertIsDisplayed()
        capture("cascade_jammed_day.png")
    }

    @Test
    fun jammedNamesTheUnansweringLinkAtNight() {
        setDeck(jammed(), dark = true)
        composeRule.onNodeWithText("THE SHIELD DIDN'T ANSWER — STILL CHECKING.").assertIsDisplayed()
        capture("cascade_jammed_night.png")
    }

    @Test
    fun sealedTakesTheRunningTenancy() {
        setDeck(ArmPhase.SealedRunning(sessionId = 7, t0Mono = 1_300L), dark = false)
        // Capture first: if a tenancy assertion fails, the frame
        // itself is the evidence of what the zone actually held.
        capture("cascade_sealed_day.png")
        composeRule.onNodeWithTag("request_disarm_button").assertExists()
        composeRule.onNodeWithTag("slide_to_start_view").assertDoesNotExist()
    }

    @Test
    fun routineBornSessionRunsWithThePhaseIdle() {
        setDeck(ArmPhase.Idle, dark = false)
        capture("cascade_routine_day.png")
        composeRule.onNodeWithTag("request_disarm_button").assertExists()
    }
}
