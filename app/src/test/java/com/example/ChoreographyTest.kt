package com.example

import com.example.ui.motion.MonolithScores
import com.example.ui.motion.MotionScore
import com.example.ui.motion.MotionTrack
import com.example.ui.motion.MonolithBodies
import com.example.ui.motion.ScorePlayer
import com.example.ui.motion.SyncKind
import com.example.ui.motion.SyncPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MOTION-SR-5 — choreography as executable law: tracks and cues share
 * one clock; a score's length is derived from physics; the canonical
 * sealing/lifting scores carry P0's landed clunk rhythms.
 */
class ChoreographyTest {

    @Test
    fun `sealing and lifting carry the landed clunk rhythm and weights`() {
        val sealingCues = MonolithScores.Sealing.syncPoints.filter { it.name.startsWith("bolt-clunk") }
        assertEquals(listOf(0L, 190L, 420L), sealingCues.map { it.atMs })
        assertEquals(listOf(1f, 0.82f, 0.64f), sealingCues.map { it.weight })
        assertTrue(sealingCues.all { it.kind == SyncKind.SOUND_AND_HAPTIC })

        val liftingCues = MonolithScores.Lifting.syncPoints.filter { it.name.startsWith("bolt-clunk") }
        assertEquals(listOf(0L, 190L, 420L), liftingCues.map { it.atMs })
        assertEquals(listOf(0.64f, 0.82f, 1f), liftingCues.map { it.weight })
        assertTrue(MonolithScores.Lifting.syncPoints.any { it.name == "plate-strike" })
    }

    @Test
    fun `a track holds its from-value before its start and rests exactly on its end`() {
        val player = ScorePlayer(MonolithScores.Sealing)
        assertEquals(0f, player.valuesAt(0L)["bolt-3"]!!, 0f) // bolt-3 starts at 420ms
        assertEquals(0f, player.valuesAt(200L)["bolt-3"]!!, 0f)
        val end = player.valuesAt(MonolithScores.Sealing.durationMs + 1000L)
        for ((_, value) in end) assertEquals(1f, value, 0f) // every track decided at its end
        assertTrue(player.isFinished(MonolithScores.Sealing.durationMs))
    }

    @Test
    fun `sync points fire once, in order, from the same clock as the poses`() {
        val player = ScorePlayer(MonolithScores.Sealing)
        var cursor = -1L
        val fired = mutableListOf<String>()
        // Walk the clock in 50ms frames, as the runner does.
        var t = 0L
        while (t <= MonolithScores.Sealing.durationMs) {
            fired += player.syncPointsDue(cursor, t).map { it.name }
            cursor = t
            t += 50L
        }
        assertEquals(listOf("bolt-clunk-1", "bolt-clunk-2", "bolt-clunk-3"), fired)
    }

    @Test
    fun `score duration is derived from physics - the door outlasts the bolts`() {
        // DoorWeight is the softest body: the sealing is not over when the last clunk lands.
        assertTrue(MonolithScores.Sealing.durationMs > 420L)
        val boltOnly = MotionScore(
            name = "bolts-only",
            tracks = listOf(MotionTrack(id = "b", body = MonolithBodies.Bolt, fromValue = 0f, toValue = 1f))
        )
        assertTrue(boltOnly.durationMs < MonolithScores.Sealing.durationMs)
    }

    @Test
    fun `the knock is one cue at the instant of contact`() {
        assertEquals(1, MonolithScores.Knock.syncPoints.size)
        assertEquals(0L, MonolithScores.Knock.syncPoints.single().atMs)
        val player = ScorePlayer(MonolithScores.Knock)
        assertEquals(listOf("knock-thud"), player.syncPointsDue(-1L, 0L).map { it.name })
        assertTrue(player.syncPointsDue(0L, 10_000L).isEmpty())
    }

    @Test
    fun `a score with duplicate track ids is refused`() {
        var refused = false
        try {
            MotionScore(
                name = "bad",
                tracks = listOf(
                    MotionTrack(id = "x", body = MonolithBodies.Bolt, fromValue = 0f, toValue = 1f),
                    MotionTrack(id = "x", body = MonolithBodies.Bolt, fromValue = 0f, toValue = 1f)
                )
            )
        } catch (e: IllegalArgumentException) { refused = true }
        assertTrue(refused)
    }
}
