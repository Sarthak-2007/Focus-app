package com.example

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.example.service.WatchItemId
import com.example.ui.components.PARK_DISMISS_FRACTION
import com.example.ui.components.fixPressHoldsForReturn
import com.example.ui.components.fixReturnStillOwns
import com.example.ui.components.gripDraggedFraction
import com.example.ui.views.SledSwitchView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #56 (Audit R1, slice park-drawer) — the gate, drag, sled
 * and FIX-return cures, proven at the level each one lives at:
 *
 *  - Finding 2's drag arithmetic and the 40% law: pure, tabled.
 *  - Finding 4's trip binding: pure, tabled.
 *  - Finding 3's sled press discipline: the real SledSwitchView
 *    under Robolectric, fed raw MotionEvents — an UP with no DOWN
 *    on the sled (a scroll ending over it) must never toggle.
 *
 * Findings 1 and 5 are composition gates in ParkDrawer (the
 * `interactive` gate on all three sleds; the scrim blocking while
 * drawn) — their logic is the single `interactive` derivation and
 * the `fraction < 1f` scrim condition, verified against the code
 * in the issue report; the sled half of finding 1 is additionally
 * backstopped here by finding 3's press discipline.
 */
class ParkGripDragMathTest {

    @Test
    fun dragAccumulatesInOrderAndClamps() {
        // Each step reads the previous step's result — the exact
        // accumulation the gesture handler performs synchronously.
        var f = 0f
        f = gripDraggedFraction(f, 100f, 1000)
        assertEquals(0.1f, f, 0.0001f)
        f = gripDraggedFraction(f, 250f, 1000)
        assertEquals(0.35f, f, 0.0001f)
        f = gripDraggedFraction(f, -50f, 1000)
        assertEquals(0.30f, f, 0.0001f)
        // Clamped at both rest heights, never beyond.
        assertEquals(1f, gripDraggedFraction(0.9f, 500f, 1000), 0f)
        assertEquals(0f, gripDraggedFraction(0.1f, -500f, 1000), 0f)
    }

    @Test
    fun dismissLawBoundaryIsStrictlyPastFortyPercent() {
        assertEquals(0.4f, PARK_DISMISS_FRACTION, 0f)
        val at = gripDraggedFraction(0f, 400f, 1000)
        assertFalse("exactly 40% re-seats (the law is > 40%)", at > PARK_DISMISS_FRACTION)
        val past = gripDraggedFraction(0f, 401f, 1000)
        assertTrue("past 40% dismisses", past > PARK_DISMISS_FRACTION)
    }

    @Test
    fun zeroHeightSheetCannotDivideByZero() {
        // Height coerces to at least 1px: the step saturates and
        // clamps to the shut rest height instead of dividing by 0.
        assertEquals(1f, gripDraggedFraction(0.5f, 10f, 0), 0f)
        assertEquals(0f, gripDraggedFraction(0.5f, -10f, 0), 0f)
    }
}

class ParkFixReturnBindingTest {

    private val itemA = WatchItemId.PERMISSIONS[0]
    private val itemB = WatchItemId.PERMISSIONS[1]

    @Test
    fun sameTripSameItemStillPressed_judges() {
        assertTrue(fixReturnStillOwns(1, itemA, 1, itemA, stillPressed = true))
    }

    @Test
    fun secondPressInsideWindow_standsTheFirstTimerDown() {
        // Trip 2 began (and named its item) before trip 1's 800ms
        // timer fired: the timer must neither judge nor clear it.
        assertFalse(fixReturnStillOwns(1, itemA, 2, itemA, stillPressed = true))
        assertFalse(fixReturnStillOwns(1, itemA, 2, itemB, stillPressed = true))
    }

    @Test
    fun freshEyesA_onlyACleanReleaseHoldsThePressForItsVerdict() {
        // A completed press (clean release) keeps the dip armed for
        // the return effect — the Settings absence IS the hold.
        assertTrue(fixPressHoldsForReturn(released = true))
        // An interrupted press (gesture stolen, tryAwaitRelease
        // false) clears at once: no later ON_RESUME tick may judge
        // a press the user never completed.
        assertFalse(fixPressHoldsForReturn(released = false))
    }

    @Test
    fun clearedOrReplacedItem_isNotJudged() {
        assertFalse(fixReturnStillOwns(1, itemA, 1, null, stillPressed = true))
        assertFalse(fixReturnStillOwns(1, itemA, 1, itemB, stillPressed = true))
        assertFalse(fixReturnStillOwns(1, itemA, 1, itemA, stillPressed = false))
        assertFalse(fixReturnStillOwns(1, null, 1, itemA, stillPressed = true))
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SledSwitchPressDisciplineTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun laidOutSled(onToggle: () -> Unit): SledSwitchView {
        val view = SledSwitchView(context)
        view.onToggleRequested = onToggle
        val w = View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY)
        val h = View.MeasureSpec.makeMeasureSpec(50, View.MeasureSpec.EXACTLY)
        view.measure(w, h)
        view.layout(0, 0, 200, 50)
        return view
    }

    private fun event(action: Int, x: Float, y: Float): MotionEvent =
        MotionEvent.obtain(0L, 0L, action, x, y, 0)

    private fun send(view: View, action: Int, x: Float = 20f, y: Float = 25f) {
        val e = event(action, x, y)
        view.onTouchEvent(e)
        e.recycle()
    }

    @Test
    fun upWithNoDownOnSled_neverToggles() {
        // Finding 3's exact case: a sheet scroll ending over the
        // sled delivers an UP the sled never earned.
        var toggles = 0
        val view = laidOutSled { toggles++ }
        send(view, MotionEvent.ACTION_UP)
        assertEquals(0, toggles)
    }

    @Test
    fun completePressOnSled_togglesExactlyOnce() {
        var toggles = 0
        val view = laidOutSled { toggles++ }
        send(view, MotionEvent.ACTION_DOWN)
        send(view, MotionEvent.ACTION_UP)
        assertEquals(1, toggles)
        // And a stray second UP does not re-fire.
        send(view, MotionEvent.ACTION_UP)
        assertEquals(1, toggles)
    }

    @Test
    fun cancelledPress_neverToggles() {
        // The scroller took the gesture (ACTION_CANCEL): the
        // following UP must find no armed press.
        var toggles = 0
        val view = laidOutSled { toggles++ }
        send(view, MotionEvent.ACTION_DOWN)
        send(view, MotionEvent.ACTION_CANCEL)
        send(view, MotionEvent.ACTION_UP)
        assertEquals(0, toggles)
    }

    @Test
    fun pressThatSlidesOff_neverToggles() {
        var toggles = 0
        val view = laidOutSled { toggles++ }
        send(view, MotionEvent.ACTION_DOWN, x = 20f)
        send(view, MotionEvent.ACTION_UP, x = 500f)
        assertEquals(0, toggles)
    }

    @Test
    fun pressThatStartedOutside_neverToggles() {
        var toggles = 0
        val view = laidOutSled { toggles++ }
        send(view, MotionEvent.ACTION_DOWN, x = -20f)
        send(view, MotionEvent.ACTION_UP, x = 20f)
        assertEquals(0, toggles)
    }

    @Test
    fun freshEyesB_gateRejectsSilently_gateAcceptsToggles() {
        // The gate (Park's `interactive`) rejecting a complete
        // press: no toggle — and in production no haptic either,
        // since fireFeel sits behind the same check in the view.
        var toggles = 0
        val rejected = laidOutSled { toggles++ }
        rejected.gateCheck = { false }
        send(rejected, MotionEvent.ACTION_DOWN)
        send(rejected, MotionEvent.ACTION_UP)
        assertEquals(0, toggles)

        val accepted = laidOutSled { toggles++ }
        accepted.gateCheck = { true }
        send(accepted, MotionEvent.ACTION_DOWN)
        send(accepted, MotionEvent.ACTION_UP)
        assertEquals(1, toggles)

        // No gate seated (Shield's usage): original behaviour.
        val ungated = laidOutSled { toggles++ }
        send(ungated, MotionEvent.ACTION_DOWN)
        send(ungated, MotionEvent.ACTION_UP)
        assertEquals(2, toggles)
    }

    @Test
    fun freshEyesB_gateIsConsultedOnlyByACompletePress_exactlyOnce() {
        // Event order: the gate must not be polled by stray UPs,
        // cancelled presses, or slide-offs — only a complete press
        // consults it, and exactly once (at its UP).
        var consultations = 0
        var toggles = 0
        val view = laidOutSled { toggles++ }
        view.gateCheck = { consultations++; true }
        send(view, MotionEvent.ACTION_UP)
        assertEquals(0, consultations)
        send(view, MotionEvent.ACTION_DOWN)
        send(view, MotionEvent.ACTION_CANCEL)
        send(view, MotionEvent.ACTION_UP)
        assertEquals(0, consultations)
        send(view, MotionEvent.ACTION_DOWN, x = 20f)
        send(view, MotionEvent.ACTION_UP, x = 500f)
        assertEquals(0, consultations)
        send(view, MotionEvent.ACTION_DOWN)
        send(view, MotionEvent.ACTION_UP)
        assertEquals(1, consultations)
        assertEquals(1, toggles)
    }
}
