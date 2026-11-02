package com.example.ui.strongroom

/**
 * THE SEALED FACE'S CONTENT MODEL — the face as it was engraved
 * at sealing, and nothing else.
 *
 * The concept's law (LEAD-7, the locked Strongroom): what is cut
 * at closing is all the face ever says; the face is engraved
 * once and NEVER ALTERED while the seal stands. This model is
 * that law as a type: an immutable snapshot taken at sealing —
 * the lifting hour and his sentence (or neither) — with no clock,
 * no progress, no live state of any kind. A face that could
 * change would need a field to change in; there is none.
 *
 * THE FOUR PRINTINGS (Stage 5 §3.3, the binding hierarchy map):
 *  1. The lifting hour — the face's first fact (ENG-1).
 *  2. The door-word slot — its KEPT PLACE (Audit A G-1: no word
 *     exists and none is invented; the slot is structural, so
 *     this model carries no field for it — a field would invite
 *     a value).
 *  3. His sentence, or its kept place (PRT-4).
 *  4. The held-mark slot — a quiet struck empty place (Audit A
 *     G-2: form undecided, no glyph invented; likewise no field).
 *
 * THE SEAL-GONE FORM (B-11, as amended by Ruling R7-1 — the
 * follow-on landed by BUILD-SR-14): if the seal dies mid-block,
 * the failure face is THIS face with the seal gone and NOTHING
 * added, its hours carried as the bracket pair — "standing at
 * the last check, fallen by the first check that found it"
 * (A-12's settled words; the pair's two hours are real check
 * hours of a real day, never a smoothed single figure and never
 * a fall instant — the fall is a finding, not an event). That
 * form is a state of this model, carried by [BracketPair]: the
 * same snapshot, the same sentence, the same kept places — the
 * seal absent, the hour printing carrying the pair instead of
 * the one lifting hour. Per R2-3 the form is testimony at the
 * next open, never a gate: the seal's death already released
 * the phone on P2's side, and nothing in this type can hold,
 * demand, or route anything.
 */
enum class FacePrinting(val rank: Int, val stageStep: String) {
    LIFTING_HOUR(1, "ENG-1"),
    DOOR_WORD_SLOT(2, "PRT-3"),
    SENTENCE(3, "PRT-4"),
    HELD_MARK_SLOT(4, "PRT-2");

    companion object {
        /** The hierarchy, in rank order — the face's one order. */
        val HIERARCHY: List<FacePrinting> = values().sortedBy { it.rank }
    }
}

/**
 * The bracket pair of a fallen day (B-11 / A-12): the hour the
 * seal was standing at the last check, and the hour it was
 * fallen by the first check that found it — earliest to latest,
 * both lawful HH:MM check hours. The pair is the whole temporal
 * truth of the fall: neither end may be tightened, widened, or
 * filled in, and no third value (a fall instant, a cause, a
 * duration) exists in this type to be invented from.
 */
data class BracketPair(
    val standingHour: String,
    val fallenHour: String
) {
    init {
        require(RingGeometry.isLawfulHour(standingHour)) {
            "The bracket's standing hour must be the lawful HH:MM form: $standingHour"
        }
        require(RingGeometry.isLawfulHour(fallenHour)) {
            "The bracket's fallen hour must be the lawful HH:MM form: $fallenHour"
        }
    }
}

/**
 * The sealed face's whole content. [liftingHour] is the lawful
 * HH:MM form (FIG-1) — anything else cannot be a face. [sentence]
 * is his sentence as signed; null (or blank — a blank sentence is
 * no sentence) means the kept place stands in its stead, at the
 * same rank and the same block size.
 *
 * [sealGoneBracket] is null while the seal stands. When the seal
 * has died it carries the day's [BracketPair] and the face stands
 * in its seal-gone form (B-11 / R7-1): seal absent, the bracket
 * pair at the hour's rank, and every other printing exactly as
 * engraved — nothing added, nothing softened, nothing removed
 * but the seal itself.
 */
data class SealedFaceState(
    val liftingHour: String,
    val sentence: String?,
    val sealGoneBracket: BracketPair? = null
) {
    init {
        require(RingGeometry.isLawfulHour(liftingHour)) {
            "The face's hour must be the lawful HH:MM form: $liftingHour"
        }
    }

    /** True when a sentence stands on the face; false when its
     *  kept place does. */
    val hasSentence: Boolean = !sentence.isNullOrBlank()

    /** True when the seal is gone and this face stands as the
     *  fallen day's testimony (B-11). */
    val sealGone: Boolean get() = sealGoneBracket != null

    companion object {
        /** The seal-gone form of a day's face, struck from its
         *  standing snapshot: same lifting hour, same sentence,
         *  the bracket pair of its fall. */
        fun sealGone(
            liftingHour: String,
            sentence: String?,
            standingHour: String,
            fallenHour: String
        ): SealedFaceState = SealedFaceState(
            liftingHour = liftingHour,
            sentence = sentence,
            sealGoneBracket = BracketPair(standingHour, fallenHour)
        )
    }
}
