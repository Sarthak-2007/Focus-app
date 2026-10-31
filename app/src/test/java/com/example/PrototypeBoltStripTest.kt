package com.example

import com.example.ui.components.PrototypeBoltFace
import com.example.ui.components.PrototypeBoltGeometry
import com.example.ui.components.PrototypeLamp
import com.example.ui.components.PrototypeWindow
import com.example.ui.components.prototypeEndingSoonThresholdFor
import com.example.ui.components.prototypeFaceFor
import com.example.ui.components.prototypePoseFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PROTOTYPE gate for BUILDER-3's Bolt strip (issue #4): the
 * geometry of record, the glance arithmetic computed from the
 * elements that render, and the fold's precedence — pinned so
 * the panel judges numbers, not adjectives.
 */
class PrototypeBoltStripTest {

    private val refWidth = 360f
    private val g = PrototypeBoltGeometry

    @Test
    fun `geometry of record at reference width`() {
        // Inner 336; channel is the flexible element: 189 at ref.
        assertEquals(336f, g.innerWidthDp(refWidth), 0.01f)
        assertEquals(189f, g.channelWidthDp(refWidth), 0.01f)
        assertEquals(99f, g.travelDp(refWidth), 0.01f)
        // Bolt never grows; wider housings grow only the channel.
        assertEquals(241f, g.channelWidthDp(412f), 0.01f)
        assertEquals(90f, g.boltWidthDp, 0.01f)
    }

    @Test
    fun `detents are open caught-at-a-third thrown`() {
        assertEquals(0f, prototypePoseFor(PrototypeBoltFace.OFF).fraction, 0.001f)
        assertEquals(
            1f / 3f,
            prototypePoseFor(PrototypeBoltFace.UNPROVEN).fraction,
            0.001f
        )
        assertEquals(1f, prototypePoseFor(PrototypeBoltFace.RUNNING).fraction, 0.001f)
        // CAUGHT is never a half — half reads as a resting state.
        assertTrue(
            kotlin.math.abs(
                prototypePoseFor(PrototypeBoltFace.UNPROVEN).fraction - 0.5f
            ) > 0.1f
        )
    }

    @Test
    fun `thrown tip is swallowed by the jaw face plate`() {
        // WOW-1: at THROWN the bolt's last >=8dp passes behind
        // the face plate; at CAUGHT the tip stops far short.
        assertTrue(g.swallowDp() >= 8f)
        val thrownTip = g.boltLeftDp(refWidth, 1f) + g.boltWidthDp
        val plateLeft = g.insetDp + g.channelWidthDp(refWidth) - g.jawFacePlateDp
        assertTrue(thrownTip > plateLeft)
        val caughtTip = g.boltLeftDp(refWidth, 1f / 3f) + g.boltWidthDp
        assertTrue(g.insetDp + g.channelWidthDp(refWidth) - caughtTip > 50f)
    }

    @Test
    fun `pin notch aligns only at thrown`() {
        // The notch centre carried by the bolt equals the pin's
        // fixed centre exactly at fraction 1, and is displaced
        // by the full remaining travel at every other detent.
        val pin = g.pinCentreXDp(refWidth)
        for (f in listOf(0f, 1f / 3f)) {
            val notchCentre = g.boltLeftDp(refWidth, f) +
                g.notchBoltLocalStartDp + g.notchWidthDp / 2f
            assertTrue(kotlin.math.abs(notchCentre - pin) > 20f)
        }
        val thrownNotch = g.boltLeftDp(refWidth, 1f) +
            g.notchBoltLocalStartDp + g.notchWidthDp / 2f
        assertEquals(pin, thrownNotch, 0.01f)
    }

    @Test
    fun `glance gate - OFF orange at least 3x the running jewel`() {
        // Computed from rendered elements (KRALL K-5): bed +
        // painted slot vs magenta disc + halo annulus at night.
        assertTrue(g.offLuminousTotal() > 0f)
        assertTrue(g.jewelLuminousTotal(PrototypeBoltFace.RUNNING, night = true) > 0f)
        assertTrue(g.offToRunningRatio() >= 3f)
        // Dark faces contribute no lamp light at all.
        assertEquals(
            0f,
            g.jewelLuminousTotal(PrototypeBoltFace.RUNNING_UNPROVEN, night = true),
            0.001f
        )
        // The jewel stays under its hard cap.
        assertTrue(g.jewelDiameterDp <= 9f)
    }

    @Test
    fun `ending soon threshold is floored by the span - HOLLIS H-3`() {
        // The shortest legal session (5 min = 300s span) must
        // not be born ENDING_SOON: threshold is half its span.
        assertEquals(150, prototypeEndingSoonThresholdFor(300))
        assertEquals(300, prototypeEndingSoonThresholdFor(3600))
        assertEquals(300, prototypeEndingSoonThresholdFor(null))
        val faceAtOpen = prototypeFaceFor(
            sessionRunning = true, detentionHeld = false, liveOff = false,
            hasRead = true, proofStale = false, provenNow = true,
            isStopwatch = false, remainingSeconds = 300, spanSeconds = 300
        )
        assertEquals(PrototypeBoltFace.RUNNING, faceAtOpen)
        val faceNearEnd = prototypeFaceFor(
            sessionRunning = true, detentionHeld = false, liveOff = false,
            hasRead = true, proofStale = false, provenNow = true,
            isStopwatch = false, remainingSeconds = 140, spanSeconds = 300
        )
        assertEquals(PrototypeBoltFace.ENDING_SOON, faceNearEnd)
    }

    @Test
    fun `fold precedence - detention, live off, stale, unproven`() {
        fun face(
            running: Boolean = true, detention: Boolean = false,
            off: Boolean = false, read: Boolean = true,
            stale: Boolean = false, proven: Boolean = true
        ) = prototypeFaceFor(
            sessionRunning = running, detentionHeld = detention,
            liveOff = off, hasRead = read, proofStale = stale,
            provenNow = proven, isStopwatch = false,
            remainingSeconds = 1200, spanSeconds = 3600
        )
        assertEquals(PrototypeBoltFace.DETENTION, face(detention = true, off = true))
        assertEquals(PrototypeBoltFace.OFF, face(off = true, proven = false))
        assertEquals(PrototypeBoltFace.STALE, face(stale = true, proven = false))
        assertEquals(PrototypeBoltFace.RUNNING_UNPROVEN, face(proven = false))
        assertEquals(PrototypeBoltFace.RUNNING_UNPROVEN, face(read = false))
        assertEquals(PrototypeBoltFace.RUNNING, face())
        assertEquals(PrototypeBoltFace.BLOCKING, face(running = false))
        assertEquals(PrototypeBoltFace.UNPROVEN, face(running = false, proven = false))
        assertEquals(PrototypeBoltFace.OFF, face(running = false, off = true))
    }

    @Test
    fun `window law - numerals only where an end exists`() {
        assertEquals(
            PrototypeWindow.SHUTTERED,
            prototypePoseFor(PrototypeBoltFace.STALE).window
        )
        assertEquals(
            PrototypeWindow.BLANK,
            prototypePoseFor(PrototypeBoltFace.DETENTION).window
        )
        assertEquals(
            PrototypeWindow.INVERTED_END,
            prototypePoseFor(PrototypeBoltFace.ENDING_SOON).window
        )
        // ENDING_SOON never moves the pose and steps the lamp DOWN.
        val soon = prototypePoseFor(PrototypeBoltFace.ENDING_SOON)
        assertEquals(1f, soon.fraction, 0.001f)
        assertEquals(PrototypeLamp.BRASS, soon.lamp)
        // Fault never lights beyond the running family exception:
        // DETENTION wears plain magenta, never the halo.
        assertEquals(
            PrototypeLamp.MAGENTA,
            prototypePoseFor(PrototypeBoltFace.DETENTION).lamp
        )
    }
}
