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
 * The sealed face's whole content. [liftingHour] is the lawful
 * HH:MM form (FIG-1) — anything else cannot be a face. [sentence]
 * is his sentence as signed; null (or blank — a blank sentence is
 * no sentence) means the kept place stands in its stead, at the
 * same rank and the same block size.
 */
data class SealedFaceState(
    val liftingHour: String,
    val sentence: String?
) {
    init {
        require(RingGeometry.isLawfulHour(liftingHour)) {
            "The face's hour must be the lawful HH:MM form: $liftingHour"
        }
    }

    /** True when a sentence stands on the face; false when its
     *  kept place does. */
    val hasSentence: Boolean = !sentence.isNullOrBlank()
}
