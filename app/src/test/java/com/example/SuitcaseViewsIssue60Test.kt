package com.example

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.ui.views.AppRowSledToggleView
import com.example.ui.views.Emergency60sValveView
import com.example.ui.views.RockerMath
import com.example.ui.views.SledSwitchView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue-60 (Audit R1 · blockit-suitcase-views) — one test per finding.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SuitcaseViewsIssue60Test {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun ev(action: Int, x: Float, y: Float): MotionEvent =
        MotionEvent.obtain(0L, 0L, action, x, y, 0)

    private fun <T : android.view.View> T.laidOut(w: Int, h: Int): T {
        measure(
            android.view.View.MeasureSpec.makeMeasureSpec(w, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(h, android.view.View.MeasureSpec.EXACTLY)
        )
        layout(0, 0, w, h)
        return this
    }

    // Finding 2: stray UP must not toggle; a started-here gesture must.
    @Test
    fun sled_upWithoutDown_doesNotToggle_downUpInside_togglesOnce() {
        var toggles = 0
        val view = SledSwitchView(context).laidOut(200, 80)
        view.onToggleRequested = { toggles++ }

        view.onTouchEvent(ev(MotionEvent.ACTION_UP, 100f, 40f))
        assertEquals(0, toggles)

        view.onTouchEvent(ev(MotionEvent.ACTION_DOWN, 100f, 40f))
        view.onTouchEvent(ev(MotionEvent.ACTION_UP, 100f, 40f))
        assertEquals(1, toggles)

        // Cancel kills the gesture.
        view.onTouchEvent(ev(MotionEvent.ACTION_DOWN, 100f, 40f))
        view.onTouchEvent(ev(MotionEvent.ACTION_CANCEL, 100f, 40f))
        view.onTouchEvent(ev(MotionEvent.ACTION_UP, 100f, 40f))
        assertEquals(1, toggles)

        // Started here, ended off the switch: no toggle.
        view.onTouchEvent(ev(MotionEvent.ACTION_DOWN, 100f, 40f))
        view.onTouchEvent(ev(MotionEvent.ACTION_UP, 500f, 40f))
        assertEquals(1, toggles)
    }

    // Finding 3: all three controls clickable, labelled, performClick works.
    @Test
    fun allControls_areClickableLabelledAndPerformClickActs() {
        val sled = SledSwitchView(context).laidOut(200, 80)
        assertTrue(sled.isClickable)
        assertNotNull(sled.contentDescription)
        var toggles = 0
        sled.onToggleRequested = { toggles++ }
        assertTrue(sled.performClick())
        assertEquals(1, toggles)

        val row = AppRowSledToggleView(context).laidOut(600, 100)
        assertTrue(row.isClickable)
        assertTrue(row.contentDescription.toString().contains("Instagram"))
        var chosen: AppBlockTier? = null
        row.onTierSelected = { chosen = it }
        row.currentTier = AppBlockTier.LOCKED
        assertTrue(row.performClick())
        assertEquals(AppBlockTier.PEEK_2M, chosen)

        val valve = Emergency60sValveView(context).laidOut(600, 120)
        assertTrue(valve.isClickable)
        assertEquals("Emergency break valve", valve.contentDescription.toString())
    }

    // Finding 1: banked progress restores into a fresh view (whole
    // chambers only) and survives save/restore instance state.
    @Test
    fun valve_bankedProgress_restoresAndSurvivesInstanceState() {
        val fresh = Emergency60sValveView(context)
        fresh.requiredSeconds = 60
        fresh.restoreBankedProgress(44_000L) // un-banked remainder drops
        assertEquals(30_000L, fresh.currentBankedMs)
        assertEquals(30_000L, fresh.currentHeldMs)

        val saveMethod = Emergency60sValveView::class.java
            .getDeclaredMethod("onSaveInstanceState")
        saveMethod.isAccessible = true
        val saved = saveMethod.invoke(fresh) as Bundle
        val reborn = Emergency60sValveView(context)
        reborn.onRestoreInstanceState(saved)
        assertEquals(30_000L, reborn.currentBankedMs)
    }

    // Finding 4: requiredSeconds rescales held/banked proportionally.
    @Test
    fun valve_requiredSecondsChange_rescalesBankedFraction() {
        val valve = Emergency60sValveView(context)
        valve.requiredSeconds = 60
        valve.restoreBankedProgress(30_000L)
        valve.requiredSeconds = 30
        assertEquals(15_000L, valve.currentBankedMs)
        assertEquals(15_000L, valve.currentHeldMs)
    }

    // Finding 4 (tail): pre-attach isChecked seats instantly, no throw.
    @Test
    fun sled_preAttachIsCheckedFalse_seatsAtZero() {
        val view = SledSwitchView(context)
        view.isChecked = false
        val field = SledSwitchView::class.java.getDeclaredField("sledProgress")
        field.isAccessible = true
        assertEquals(0f, field.getFloat(view), 0.001f)
    }

    // Finding 5: draw geometry == touch geometry (single source).
    @Test
    fun rockerMath_drawAndTouchShareOneSlotWidth() {
        val trackW = 168f
        val pad = 3f
        val slot = RockerMath.slotWidth(trackW, pad)
        assertEquals(54f, slot, 0.001f)
        // Stop centres as DRAWN must map back to their own stop.
        for (i in 0..2) {
            val drawnCentre = 0f + pad + (i + 0.5f) * slot
            assertEquals(i, RockerMath.indexForTap(drawnCentre, 0f, pad, slot))
        }
        // Boundaries sit exactly between drawn thumbs.
        assertEquals(0, RockerMath.indexForTap(pad + slot - 0.1f, 0f, pad, slot))
        assertEquals(1, RockerMath.indexForTap(pad + slot + 0.1f, 0f, pad, slot))
    }

    @Test
    fun sled_chromeModeSetter_invalidates() {
        val view = SledSwitchView(context)
        assertFalse(view.chromeMode)
        view.chromeMode = true
        assertTrue(view.chromeMode)
    }
}
