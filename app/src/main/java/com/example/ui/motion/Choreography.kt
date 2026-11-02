package com.example.ui.motion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive

/**
 * CHOREOGRAPHY — multi-track scores on ONE clock (MOTION-SR-5, T24).
 *
 * A score is the unit of composed motion (the lifting is one: bolts
 * withdraw, the door opens by weight, the plate is struck). Tracks run
 * in parallel, each a body travelling between decided values; SOUND
 * and HAPTIC cues are sync points on the score itself, fired by the
 * same frame clock that advances the tracks. A cue can therefore
 * never drift from its motion — there is no second timer to drift on.
 *
 * Every track's endpoints are decided values: a score, like a single
 * animation, can only ever come to rest at its tracks' declared ends.
 */

/** What a sync point fires. */
enum class SyncKind { SOUND, HAPTIC, SOUND_AND_HAPTIC }

/** A sound/haptic cue pinned to the score's clock. */
data class SyncPoint(
    val atMs: Long,
    val kind: SyncKind,
    /** The cue's name — sound/haptic lanes bind names to their assets. */
    val name: String,
    /** Relative weight against the score's loudest cue (1 = full). */
    val weight: Float = 1f
) {
    init {
        require(atMs >= 0) { "a sync point cannot precede the score" }
        require(weight in 0f..1f) { "sync weight must be within 0..1" }
    }
}

/** One track of a score: one body, one decided travel, one start offset. */
data class MotionTrack(
    val id: String,
    val body: PhysicsBody,
    val startAtMs: Long = 0,
    val fromValue: Float,
    val toValue: Float
) {
    init {
        require(startAtMs >= 0) { "a track cannot start before the score" }
    }

    /** Simulated seconds from this track's start until its body settles on its end value. */
    val settleSeconds: Float by lazy {
        SpringIntegrator.simulateUntilSettled(fromValue, toValue, body).second
    }

    /** The score-clock millisecond at which this track comes to rest. */
    val endMs: Long get() = startAtMs + (settleSeconds * 1000f).toLong()
}

/** A named, complete piece of choreography. */
data class MotionScore(
    val name: String,
    val tracks: List<MotionTrack>,
    val syncPoints: List<SyncPoint> = emptyList()
) {
    init {
        require(tracks.isNotEmpty()) { "a score needs at least one track" }
        require(tracks.map { it.id }.distinct().size == tracks.size) { "track ids must be unique" }
    }

    /** Sync points in firing order (stable for equal times). */
    val orderedSyncPoints: List<SyncPoint> = syncPoints.sortedBy { it.atMs }

    /** When the last track settles — the score's true length, derived from physics, never declared. */
    val durationMs: Long get() = tracks.maxOf { it.endMs }
}

/**
 * The pure player: a score as a function of elapsed time. Pose queries
 * integrate deterministically ([SpringIntegrator]), so tests, previews
 * and the live runner all read the same motion.
 */
class ScorePlayer(val score: MotionScore) {

    /** Every track's pose at [elapsedMs] — before its start a track holds its from-value; settled, its exact end. */
    fun valuesAt(elapsedMs: Long): Map<String, Float> = score.tracks.associate { track ->
        val localMs = elapsedMs - track.startAtMs
        track.id to when {
            localMs <= 0L -> track.fromValue
            else -> SpringIntegrator.valueAt(track.fromValue, track.toValue, track.body, localMs / 1000f)
        }
    }

    /** Sync points due in (fromExclusiveMs, toInclusiveMs], in firing order. */
    fun syncPointsDue(fromExclusiveMs: Long, toInclusiveMs: Long): List<SyncPoint> =
        score.orderedSyncPoints.filter { it.atMs > fromExclusiveMs && it.atMs <= toInclusiveMs }

    fun isFinished(elapsedMs: Long): Boolean = elapsedMs >= score.durationMs
}

/** Live state of a running score, exposed by [RunMotionScore]. */
class ScoreState internal constructor() {
    /** Track poses by track id, updated on the score's clock. */
    val values = mutableStateMapOf<String, Float>()
    var finished: Boolean by mutableStateOf(false)
        internal set
}

/**
 * Run a score in composition. ONE frame clock drives everything:
 * track poses are read from the pure player at the frame's elapsed
 * time, and every sync point crossed since the previous frame fires
 * through [onSyncPoint] in score order, in that same frame pass —
 * the cue and the pose it belongs to are never a frame apart.
 *
 * [onSyncPoint] is the seam for the sound/haptic lanes (Slices 6/7):
 * they bind cue names to TactileSoundEngine assets there; the engine
 * itself makes no sound.
 */
@Composable
fun RunMotionScore(
    score: MotionScore,
    onSyncPoint: (SyncPoint) -> Unit = {},
    onFinished: () -> Unit = {}
): ScoreState {
    val state = remember(score) { ScoreState() }
    val player = remember(score) { ScorePlayer(score) }

    LaunchedEffect(score) {
        // Pose every track at its from-value before the first frame.
        state.values.putAll(player.valuesAt(0L))
        state.finished = false
        var startNanos = -1L
        var lastElapsedMs = -1L
        while (isActive) {
            val frameNanos = withFrameNanos { it }
            if (startNanos < 0L) startNanos = frameNanos
            val elapsedMs = (frameNanos - startNanos) / 1_000_000L
            state.values.putAll(player.valuesAt(elapsedMs))
            for (point in player.syncPointsDue(lastElapsedMs, elapsedMs)) onSyncPoint(point)
            lastElapsedMs = elapsedMs
            if (player.isFinished(elapsedMs)) {
                state.values.putAll(player.valuesAt(score.durationMs))
                state.finished = true
                onFinished()
                break
            }
        }
    }
    return state
}

/**
 * The canonical scores — the sealing and the lifting, built from P0's
 * landed clunk rhythms (BUILD-SR-1: bolts at 0/190/420 ms, sealing
 * weights descending 1 / 0.82 / 0.64, lifting ascending) with each
 * clunk as a SOUND_AND_HAPTIC sync point on the same clock as its
 * bolt's track. Slice 5/6/7 lanes re-tune bodies and cue bindings;
 * the score shapes are the design's: three bolts, one by one, then
 * the door.
 */
object MonolithScores {

    private val boltOffsetsMs = listOf(0L, 190L, 420L)
    private val sealingWeights = listOf(1f, 0.82f, 0.64f)
    private val liftingWeights = sealingWeights.reversed()

    private fun boltTracks(from: Float, to: Float): List<MotionTrack> =
        boltOffsetsMs.mapIndexed { i, at ->
            MotionTrack(id = "bolt-${i + 1}", body = MonolithBodies.Bolt, startAtMs = at, fromValue = from, toValue = to)
        }

    private fun clunks(weights: List<Float>): List<SyncPoint> =
        boltOffsetsMs.mapIndexed { i, at ->
            SyncPoint(atMs = at, kind = SyncKind.SOUND_AND_HAPTIC, name = "bolt-clunk-${i + 1}", weight = weights[i])
        }

    /** THE SEALING: bolts drive home one by one; the door swings shut behind them. */
    val Sealing: MotionScore = MotionScore(
        name = "sealing",
        tracks = boltTracks(from = 0f, to = 1f) + MotionTrack(
            id = "door", body = MonolithBodies.DoorWeight, startAtMs = 0, fromValue = 0f, toValue = 1f
        ),
        syncPoints = clunks(sealingWeights)
    )

    /** THE LIFTING: bolts withdraw one by one; the door opens by its own weight and strikes the plate. */
    val Lifting: MotionScore = MotionScore(
        name = "lifting",
        tracks = boltTracks(from = 1f, to = 0f) + MotionTrack(
            id = "door", body = MonolithBodies.DoorWeight, startAtMs = 420, fromValue = 1f, toValue = 0f
        ),
        syncPoints = clunks(liftingWeights) + SyncPoint(
            atMs = 420, kind = SyncKind.SOUND_AND_HAPTIC, name = "plate-strike", weight = 1f
        )
    )

    /** THE KNOCK: one dull thud — a single dead-mass track, its cue at the instant of contact. */
    val Knock: MotionScore = MotionScore(
        name = "knock",
        tracks = listOf(
            MotionTrack(id = "door", body = MonolithBodies.Knock, fromValue = 0f, toValue = 1f)
        ),
        syncPoints = listOf(SyncPoint(atMs = 0, kind = SyncKind.SOUND_AND_HAPTIC, name = "knock-thud", weight = 1f))
    )
}
