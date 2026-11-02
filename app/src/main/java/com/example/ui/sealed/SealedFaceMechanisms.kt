package com.example.ui.sealed

import com.example.data.DoorWordLaw
import com.example.domain.StrongroomDay
import com.example.ui.foundation.BodyIntegrator
import com.example.ui.foundation.BodyState
import com.example.ui.foundation.BoltBodies
import com.example.ui.foundation.CeremonyScores
import com.example.ui.foundation.DoorBody
import com.example.ui.foundation.HandTracker
import com.example.ui.foundation.MechanismBodies
import com.example.ui.foundation.PressBody
import com.example.ui.foundation.StrongroomMaximum
import kotlin.math.abs

/**
 * THE SEAL'S MECHANISMS — the pure machinery of the swing, the
 * sealed face's printings and the knock (census C-03 THE SWING ·
 * C-12 THE FOUR PRINTINGS · C-10 THE KNOCK · C-08's grille second
 * touch), authored by BUILD-SR-37.
 *
 * Every figure here is read off the foundation's filed bodies and
 * scores ([HandTracker] · [BoltBodies] · [PressBody] ·
 * [CeremonyScores.Seal] · [MechanismBodies.Knock]) — no figure is
 * invented in this file, and none is imported from a foreign spec.
 * All felt/motion figures remain UNPROVEN-ON-DEVICE, as the
 * foundation's head stamps them.
 *
 * STAGING LAW (ledger head correction; census C-03): the swing is
 * the Seal's act at the FIRST SITTING, on the standing signing —
 * the signature (the evening's act, BUILD-SR-36) seals nothing.
 * The swing fires from the standing signing exactly as composed;
 * an unsigned day stands open and seals nothing (GAP-COORD-04 as
 * ruled by SUCCESSOR-22 at PR #873: surfaces derive from a
 * standing seal only — see [SealedFaceState.standing]).
 */

// ────────────────────────────────────────────────────────────────
// THE FOUR PRINTINGS (census C-12) — cut once at the closing,
// never altered while the seal stands (the print-once law).
// Hierarchy is Stage 5's ruling (type-system §3.3, carried as
// StrongroomType.SealedFaceHierarchy): rank is NOT size —
//   1 · the lifting hour, ENGRAVED on the dial ring (ENG-1)
//   2 · the door-word slot (PRT-3)
//   3 · his sentence, or its kept place (PRT-4)
//   4 · the held-mark slot (PRT-2): a static printed mark —
//       testimony, never announcement
// A fifth printing demotes the Door by definition (census C-01).
// ────────────────────────────────────────────────────────────────

/** One printing on the sealed face, in hierarchy rank order. */
sealed interface Printing {

    /** Rank 1 — the lifting hour as engraving: the hour he set,
     *  as a figure (HH:MM, his figure), no countdown, no live form. */
    data class LiftingHour(val text: String) : Printing

    /** Rank 2 — the door-word: a single word of HIS choosing, set
     *  at the signing (GAP-COORD-01 as ruled at PR #873). Null =
     *  unset: the slot stands as a KEPT PLACE, on the same law as
     *  his sentence's kept place — a deliberate standing blank
     *  that is itself the form, never a placeholder word. The app
     *  supplies, suggests and defaults NO word, ever. */
    data class DoorWord(val word: String?) : Printing

    /** Rank 3 — his sentence, engraved verbatim; null = its kept
     *  place stands (a deliberate ruled blank, itself the form). */
    data class Sentence(val text: String?) : Printing

    /** Rank 4 — the held mark: a printed mark, static. It is NOT
     *  a live indicator, never the quiet dot (that lives on the
     *  held surface only, census §2.4), and carries no fixed words.
     *  Its whole statement, made once at the closing: a held place
     *  exists in this room. It takes NO input from the held pile —
     *  structurally, this type has no field that could carry one,
     *  so no arrival, count or state can ever move it mid-seal. */
    data object HeldMark : Printing
}

/** The face's whole printed content: exactly four printings, in
 *  hierarchy rank order, derived ONCE from the sealed day's
 *  standing record. Immutable by construction — while the seal
 *  stands there is no derivation path that re-reads a live fact
 *  into this type, so the face cannot alter. */
data class SealedPrintings(
    val liftingHour: Printing.LiftingHour,
    val doorWord: Printing.DoorWord,
    val sentence: Printing.Sentence,
    val heldMark: Printing.HeldMark
) {
    /** The four, in the decided hierarchy (Stage 5): lifting hour ·
     *  door-word slot · his sentence/kept place · held-mark slot. */
    val inHierarchyOrder: List<Printing> =
        listOf(liftingHour, doorWord, sentence, heldMark)
}

/**
 * Derive the printings from the sealed day's standing signing
 * record and the door-word as it stood at the closing. Pure and
 * deterministic: the same standing facts derive the same face, on
 * every read, in every process (the 100 ms paint bar's ground —
 * intake §1: the face is a struck record rendered whole from a
 * snapshot, never assembled from live sources at paint time).
 */
fun deriveSealedPrintings(
    record: StrongroomDay.SigningRecord,
    doorWord: String?
): SealedPrintings = SealedPrintings(
    liftingHour = Printing.LiftingHour(
        "%02d:%02d".format(record.liftingTime.hour, record.liftingTime.minute)
    ),
    doorWord = Printing.DoorWord(DoorWordLaw.lawfulWord(doorWord)),
    sentence = Printing.Sentence(record.sentenceOrKeptPlace),
    heldMark = Printing.HeldMark
)

/** The sealed face's standing state: the printings, and nothing
 *  else. The face carries no count, no elapsed figure, no live
 *  fact of any kind (census C-01/C-12). */
data class SealedFaceState(val printings: SealedPrintings) {
    companion object {
        /**
         * The face stands ONLY from a standing seal (GAP-COORD-04
         * as ruled): [sealedRecord] is the sealed day's signing
         * record as persisted at the swing — null (no seal stands)
         * derives NO face, and the shell rests at the doorway. An
         * unsigned day stands open: no seal forms, nothing fires,
         * no failure face is struck.
         */
        fun standing(
            sealedRecord: StrongroomDay.SigningRecord?,
            doorWord: String?
        ): SealedFaceState? = sealedRecord?.let {
            SealedFaceState(deriveSealedPrintings(it, doorWord))
        }
    }
}

// ────────────────────────────────────────────────────────────────
// THE SWING (census C-03) — his hand swings the door, pulls the
// great wheel; the bolts drive home one by one, three descending
// clunks; the press seats the door; silence. The ceremony clock
// is the foundation's Seal score (CeremonyScores.Seal, 3,100 ms
// whole): frame-contact at 780 · clunks at 1,160 / 1,660 / 2,340 ·
// press 2,580 → settled-silence 3,011. Offsets below are read off
// that score, relative to frame contact — the moment his hand's
// swing ends and the machine's act begins.
// ────────────────────────────────────────────────────────────────

object SwingCeremony {

    private fun scoreMs(name: String): Int =
        CeremonyScores.Seal.first { it.name == name }.atMs

    /** The score's frame-contact: the hand's swing ends here. */
    val FrameContactMs: Int = scoreMs("frame-contact")

    /** Bolt seat (clunk) offsets from frame contact, in seat order. */
    val BoltSeatOffsetsMs: List<Int> = listOf(
        scoreMs("clunk-1"), scoreMs("clunk-2"), scoreMs("clunk-3")
    ).map { it - FrameContactMs }

    /** Each bolt's own travel, from its body under the one drive
     *  ([BoltBodies] filed travels: 167 / 196 / 239 ms). */
    val BoltTravelMs: List<Int> = BoltBodies.MASSES.map { BoltBodies.travelMs(it) }

    /** When each bolt's drive must begin (relative to frame
     *  contact) for its seat to land on its clunk: seat − travel.
     *  Bolt 1's drive start IS the score's commit point
     *  (CeremonyScores.SEAL_COMMIT_MS − FrameContactMs = 213):
     *  before it an interrupted swing resolves open-unsealed; at
     *  or after it, sealed completed-unwitnessed. */
    val BoltDriveStartOffsetsMs: List<Int> =
        BoltSeatOffsetsMs.zip(BoltTravelMs) { seat, travel -> seat - travel }

    /** The press: begins at the score's press-begins, settles at
     *  its filed settle (431 ms — [PressBody.FILED_SETTLE_MS]). */
    val PressBeginsOffsetMs: Int = scoreMs("press-begins") - FrameContactMs
    val PressSettledOffsetMs: Int = scoreMs("press-settled-silence") - FrameContactMs

    /** The whole ceremony's end, relative to frame contact. */
    val CeremonyEndOffsetMs: Int = scoreMs("ceremony-end") - FrameContactMs

    /**
     * One bolt's travel fraction (0..1) at [msSinceContact]: the
     * bolt's own body integrated under the one drive (DRIVE /
     * RESISTANCE / its mass — the travel gradient is CAUSED by the
     * mass gradient, never keyframed), seat-clamped at 1 exactly
     * like the foundation's travel simulation. Before its drive
     * starts the bolt stands at 0; at its seat it stands at 1.
     */
    fun boltTravelFraction(boltIndex: Int, msSinceContact: Int): Double {
        val start = BoltDriveStartOffsetsMs[boltIndex]
        val elapsed = msSinceContact - start
        if (elapsed <= 0) return 0.0
        val mass = BoltBodies.MASSES[boltIndex]
        var x = 0.0
        var v = 0.0
        val h = BodyIntegrator.SUBSTEP_SECONDS
        var ms = 0
        while (ms < elapsed) {
            val a = (BoltBodies.DRIVE - BoltBodies.RESISTANCE * v) / mass
            v += a * h
            x += v * h
            ms++
            if (x >= 1.0) return 1.0
        }
        return x.coerceIn(0.0, 1.0)
    }

    /** True when bolt [boltIndex] has seated (its clunk's moment). */
    fun boltSeated(boltIndex: Int, msSinceContact: Int): Boolean =
        msSinceContact >= BoltSeatOffsetsMs[boltIndex]

    /** The seated bolts' clunk weights, ascending by seat order
     *  (0.64 / 0.82 / 1.0 — the last bolt the heaviest fact), read
     *  off the score's own weights. */
    val ClunkWeights: List<Double> = listOf("clunk-1", "clunk-2", "clunk-3")
        .map { name -> CeremonyScores.Seal.first { it.name == name }.weight }

    /**
     * The press's travel fraction (0..1) at [msSinceContact]: the
     * press body itself ([PressBody.Body] — overdamped, ζ ≈ 1.361,
     * no overshoot, never a fourth clunk), integrated by the
     * foundation's integrator from its score onset.
     */
    fun pressFraction(msSinceContact: Int): Double {
        val elapsed = msSinceContact - PressBeginsOffsetMs
        if (elapsed <= 0) return 0.0
        return BodyIntegrator.valueAt(0.0, 1.0, PressBody.Body, elapsed / 1000.0)
            .coerceIn(0.0, 1.0)
    }

    /** True when the press has settled and the ceremony's silence
     *  stands: the seal is complete, the face is printed. */
    fun isCeremonyComplete(msSinceContact: Int): Boolean =
        msSinceContact >= CeremonyEndOffsetMs
}

/**
 * THE HAND-CLOSED DOOR — the swing's hand phase. While his hand is
 * on the door it follows the hand through the foundation's
 * critically damped tracker on the door's own inertia
 * ([HandTracker.Body]: ζ = 1.000, no overshoot past his hand,
 * ever). The hand works against real mass: the door is where the
 * tracker has carried it, never where the finger merely is.
 */
class HandClosedDoor(startAngleDeg: Double = DoorBody.REST_OPEN_DEG) {

    private var state = BodyState(startAngleDeg, 0.0)

    /** The hand's target angle (deg, 0 = shut, 82 = open at rest). */
    var handTargetDeg: Double = startAngleDeg
        private set

    /** The door's present angle. */
    val angleDeg: Double get() = state.value

    /** The door's present angular velocity (deg/s, signed). */
    val velocityDegPerSec: Double get() = state.velocity

    /** Move the hand: the target is clamped to the door's travel;
     *  the tracker — not the finger — decides how the mass answers. */
    fun moveHandTo(angleDeg: Double) {
        handTargetDeg = angleDeg.coerceIn(0.0, DoorBody.REST_OPEN_DEG)
    }

    /** Advance the tracker by [dtSeconds] toward the hand. */
    fun step(dtSeconds: Double) {
        if (dtSeconds <= 0.0) return
        state = BodyIntegrator.step(state, handTargetDeg, HandTracker.Body, dtSeconds)
    }

    /** True when the door meets its frame: angle inside the
     *  contact band ([HandTracker.CONTACT_ANGLE_DEG]). */
    fun atFrameContact(): Boolean = angleDeg <= HandTracker.CONTACT_ANGLE_DEG

    /** A contact above the slam line still seals — the door does
     *  not punish (foundation HandTracker law); the classification
     *  is recorded by the caller, never answered on the face. */
    fun contactIsSlam(): Boolean = HandTracker.isSlam(abs(velocityDegPerSec))

    /** Contact is a clamp: θ := 0, ω := 0, one frame, no rebound. */
    fun clampAtContact() {
        state = BodyState(0.0, 0.0)
        handTargetDeg = 0.0
    }
}

// ────────────────────────────────────────────────────────────────
// THE KNOCK (census C-10) — a reach for a locked app lands at the
// Door and is answered by ONE dull, heavy thud, felt more than
// heard. Nothing moves (past the blow's own absorption), nothing
// opens, nothing apologises; attempts leave NOTHING on the face —
// no mark, no count, no wear. Its sound/haptic substance is the
// engine's standing knock voice (service/StrongroomKnock, fired
// by the bridge at the routing's verdict); the Stage 13/14 passes
// ride this mechanism later. Here: the face's own answer — the
// door absorbing the blow through its deadest body.
// ────────────────────────────────────────────────────────────────

object KnockAnswer {

    /** The knock body's settle, read off [MechanismBodies.Knock]
     *  (critically damped dead mass, mass 3.2, ζ = 1.0): the blow
     *  is absorbed and the door is still again within this span. */
    val SettleMs: Int =
        (BodyIntegrator.settleSeconds(1.0, 0.0, MechanismBodies.Knock) * 1000.0).toInt()

    /**
     * The door's displacement at [msSinceKnock], as a fraction of
     * the blow (1 = the blow's full travel, 0 = still). The knock
     * body is critically damped: the displacement decays
     * MONOTONICALLY to rest — no overshoot, no rebound, ever, and
     * the answer is identical at reach #1 and reach #9 (this
     * function has no memory and no counter, by construction).
     */
    fun displacementAt(msSinceKnock: Int): Double {
        if (msSinceKnock <= 0) return 1.0
        return BodyIntegrator.valueAt(
            1.0, 0.0, MechanismBodies.Knock, msSinceKnock / 1000.0
        ).coerceIn(0.0, 1.0)
    }

    /**
     * THE KNOCK'S WHOLE STATE LAW: the answer changes nothing.
     * A knocked face is the same face — same printings, same
     * standing — with the blow absorbed. (Pinned by test: the
     * printings after any number of knocks are the printings
     * before them, exactly.)
     */
    fun answer(face: SealedFaceState): SealedFaceState = face
}

// ────────────────────────────────────────────────────────────────
// THE GRILLE'S SECOND TOUCH (census C-08) — the antechamber is
// reached from the sealed face by the deliberate second touch
// alone, and by nothing else: no reach lands there, no Back, no
// route around. Two phases, distinct BY VALUE (foundation
// StrongroomMaximum): the LATCH seats first (peak ≤150, ≤40 ms,
// progressive seat-approach curve), then the WEIGHTED SWING
// (viscous curve on the door's own body family). A single touch
// only seats the latch; the second touch — a separate, later act
// of the hand — swings the grille. BUILD-SR-38's antechamber
// stands behind this mechanism; this slice authors the touch and
// its state law, on the face.
// ────────────────────────────────────────────────────────────────

class GrilleSecondTouch {

    /** True while the latch stands seated (first touch taken, the
     *  grille not yet swung). */
    var latchSeated: Boolean = false
        private set

    private var latchSeatedAtMs: Long = Long.MIN_VALUE

    enum class Result {
        /** The first touch: the latch seats; nothing opens. */
        LATCH_SEATED,

        /** The second touch arrived as part of the same act (too
         *  soon after the first): it is not a second touch, and
         *  the grille does not swing. The latch stands seated. */
        SAME_ACT,

        /** The deliberate second touch: the grille swings. */
        OPENED
    }

    /**
     * One touch of the hand, at [nowMs] (monotonic). The two
     * touches must be separate acts: a second touch inside
     * [MIN_SEPARATION_MS] of the first is the first act still
     * landing, never a deliberate second act.
     */
    fun touch(nowMs: Long): Result {
        if (!latchSeated) {
            latchSeated = true
            latchSeatedAtMs = nowMs
            return Result.LATCH_SEATED
        }
        if (nowMs - latchSeatedAtMs < MIN_SEPARATION_MS) return Result.SAME_ACT
        latchSeated = false
        latchSeatedAtMs = Long.MIN_VALUE
        return Result.OPENED
    }

    /** The seal's end (or the grille's own closing) unseats the
     *  latch: no half-open grille survives its moment. */
    fun reset() {
        latchSeated = false
        latchSeatedAtMs = Long.MIN_VALUE
    }

    companion object {
        /** The least separation between two touches for the second
         *  to count as its own act: a held finger's landing and a
         *  deliberate second touch are different things, and this
         *  gap is what tells them apart. */
        const val MIN_SEPARATION_MS: Long = 150L

        /** The latch's felt figures (foundation, census E-06):
         *  peak ≤150, ≤40 ms — carried as data for the haptics
         *  pass; the curves are the foundation's own objects. */
        val LatchSpec = StrongroomMaximum.GrilleLatchSpec
        val LatchCurve = StrongroomMaximum.GrilleLatchCurve
        val SwingCurve = StrongroomMaximum.GrilleSwingCurve
    }
}
