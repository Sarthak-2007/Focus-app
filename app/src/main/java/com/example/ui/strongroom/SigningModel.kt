package com.example.ui.strongroom

/**
 * P4 — THE SIGNING, as a pure sequence (Slice 1, corrected
 * staging: SUCCESSOR-21's letter of 2026-11-02 + the APPLIED/
 * UNAPPLIED LEDGER's head correction, AL-0056 — Audit A §2
 * compressed the signing and the swing into one act; the decided
 * staging separates them, and this model is built on the decided
 * staging, which governs).
 *
 * SIGNATURE ≠ SWING. The signing is the evening's act only: he
 * chooses tomorrow's locked apps, sets the lifting hour, sets
 * his sentence (or its kept place stands), and spins his own
 * combination as the signature. It ends with everything SET and
 * the door standing ready. The swing — door, wheel, bolts, the
 * three descending clunks (ledger A-3) — is the SEAL's act at
 * the first study sitting, fired by P1/P2's seal act; it is not
 * in this model, not in this surface, and no field here can
 * stand for it. P3's face is printed at that sealing, not here.
 *
 * The sequence (Stage 4, ledger C-4: insertion figure 0): five
 * steps in one fixed order, advanced one at a time, never
 * skipped, never inserted into. Before the signature begins he
 * may leave and start again (the correction path); once the
 * signature has begun the sequence is UNINTERRUPTIBLE —
 * [SigningState.canLeave] is false and stays false.
 *
 * THE COMBINATION IS NEVER WRITTEN ANYWHERE (ledger A-2). It
 * does not exist in this model at all: the dials' digits live
 * only in the surface's hand for the act's duration, and the
 * flow records only that the signature was begun and completed.
 * [SigningResult] — the thing that leaves the signing — carries
 * the locked set, the hour, the sentence, the task line, and
 * nothing else. There is no field a combination could hide in.
 *
 * THE COMPOSITION (ledger B-2): tomorrow stands composed from
 * the week's standing order whether or not he comes to the door;
 * the signing AMENDS, never creates. This flow therefore starts
 * from a [CompositionDraft] — the composed day — and every value
 * in the result defaults to the draft's until he amends it.
 */

/** The signing's steps, in their one order. */
enum class SigningStep {
    COMPOSITION,
    HOUR,
    SENTENCE,
    COMBINATION,
    READY;

    companion object {
        /** The sequence, whole — the conformance test reads it. */
        val SEQUENCE: List<SigningStep> = values().toList()
    }
}

/** One app he may lock or leave, as plain data (his own words for
 *  it: the name his phone gives it, carried unchanged). */
data class SigningAppOption(
    val packageName: String,
    val appName: String
)

/**
 * The composed day, as the standing order composed it (P1's
 * composition is its source; this is the surface's intake shape).
 * [spentDay] is the day's own arithmetic speaking (the purse
 * spent): it gates the B-4 telling on the surface.
 */
data class CompositionDraft(
    val dayShapeName: String,
    val appOptions: List<SigningAppOption>,
    val composedLocked: Set<String>,
    val composedHour: String,
    val composedSentence: String?,
    val spentDay: Boolean,
    /** The break firing's span as composed (minutes, free). The
     *  signing sets it (ledger B-12 + ruling C-4: the choosing
     *  moment is the signing; his Oct 30 ruling: any session ↔
     *  any break, presets as shortcuts only — never a ceiling). */
    val composedBreakMinutes: Int = 20,
    /** R2-8: the swing is TAUGHT at the first signing — one plain
     *  telling, first-run furniture only, owned as copy by
     *  TYPE-SR-1. Null on every ordinary signing; when present it
     *  stands on the composition step as furniture, never as a
     *  step in the sequence (C-4 untouched). */
    val teachingLine: String? = null
) {
    init {
        require(RingGeometry.isLawfulHour(composedHour)) {
            "The composed hour must be the lawful HH:MM form: $composedHour"
        }
        require(appOptions.map { it.packageName }.toSet().containsAll(composedLocked)) {
            "The composed locked set must stand among the day's app options"
        }
    }
}

/**
 * What the signing hands onward: tomorrow, set. Deliberately
 * without any combination, any seal, any swing — the seal act
 * (P1/P2) takes this standing order and fires the swing at the
 * first sitting; P3's face is printed from it at that sealing.
 * [taskLine] is his own words for the block's reason (ledger
 * B-3), attached to the sealing, exempt from any word-bench.
 */
data class SigningResult(
    val lockedPackages: Set<String>,
    val liftingHour: String,
    val sentence: String?,
    val taskLine: String?,
    /** The break firing's span, set at this signing (minutes). */
    val breakMinutes: Int
)

/**
 * THE SIGNING'S STANDING WORDS — the SigningWords mirror is
 * RETIRED (BUILD-SR-23, step 7, named cure): the purpose, the
 * telling, the codes-wait line, and the clause live ONLY in the
 * single copy source (res/values/strongroom_strings.xml, via the
 * StrongroomCopy facade), and every surface reads them there
 * (BUILD-SR-17, completed by BUILD-SR-16/23's compositions). No
 * second literal home stands in production code; the pinning
 * tests were re-pointed at the source in the same act.
 */

/**
 * The flow's state — immutable; every act returns the next state.
 * Steps advance forward only, one at a time (C-4). Amendments are
 * lawful only at their own step (apps at COMPOSITION, the hour
 * at HOUR, the sentence and the task line at SENTENCE): an
 * amendment attempted at the wrong step is refused by standing
 * still, never by error theatre — the sequence simply does not
 * contain that act.
 */
data class SigningState(
    val draft: CompositionDraft,
    val step: SigningStep = SigningStep.COMPOSITION,
    val lockedPackages: Set<String> = draft.composedLocked,
    val liftingHour: String = draft.composedHour,
    val breakMinutes: Int = draft.composedBreakMinutes,
    val sentence: String? = draft.composedSentence,
    val taskLine: String? = null,
    val signatureBegun: Boolean = false,
    val result: SigningResult? = null
) {
    /** He may walk away from the signing — until the signature
     *  begins. From that act on, the sequence is uninterruptible
     *  and this is false for the rest of its life. */
    val canLeave: Boolean get() = !signatureBegun && result == null

    /** The sentence's kept place stands when no sentence is set. */
    val sentenceKeptPlace: Boolean get() = sentence.isNullOrBlank()

    fun toggleApp(packageName: String): SigningState {
        if (step != SigningStep.COMPOSITION || result != null) return this
        if (draft.appOptions.none { it.packageName == packageName }) return this
        val next = if (packageName in lockedPackages) {
            lockedPackages - packageName
        } else {
            lockedPackages + packageName
        }
        return copy(lockedPackages = next)
    }

    fun setHour(hour: String): SigningState {
        if (step != SigningStep.HOUR || result != null) return this
        require(RingGeometry.isLawfulHour(hour)) {
            "The lifting hour must be the lawful HH:MM form: $hour"
        }
        return copy(liftingHour = hour)
    }

    /** The break firing's span, set at the HOUR step (the
     *  time-lock's two settings stand together: the lifting hour
     *  and the firing's span). Free to the minute — any span a
     *  day can hold; no preset caps it, ever (S-6/D-6). */
    fun setBreakMinutes(minutes: Int): SigningState {
        if (step != SigningStep.HOUR || result != null) return this
        require(minutes in 1..720) {
            "The break span must be a span a day can hold: $minutes"
        }
        return copy(breakMinutes = minutes)
    }

    fun setSentence(text: String?): SigningState {
        if (step != SigningStep.SENTENCE || result != null) return this
        return copy(sentence = text?.takeIf { it.isNotBlank() })
    }

    fun setTaskLine(text: String?): SigningState {
        if (step != SigningStep.SENTENCE || result != null) return this
        return copy(taskLine = text?.takeIf { it.isNotBlank() })
    }

    /** One step forward, never more. COMBINATION does not advance
     *  — it completes, by the signature ([completeSignature]);
     *  READY is terminal. */
    fun advance(): SigningState = when (step) {
        SigningStep.COMPOSITION -> copy(step = SigningStep.HOUR)
        SigningStep.HOUR -> copy(step = SigningStep.SENTENCE)
        SigningStep.SENTENCE -> copy(step = SigningStep.COMBINATION)
        SigningStep.COMBINATION -> this
        SigningStep.READY -> this
    }

    /** The signature begins: his hand is on the dials. From here
     *  the sequence cannot be left, paused, or inserted into. */
    fun beginSignature(): SigningState {
        if (step != SigningStep.COMBINATION || result != null) return this
        return copy(signatureBegun = true)
    }

    /** The signature completes: everything is SET, the door
     *  stands ready, and the signing hands [SigningResult]
     *  onward. No swing fires here — none is in this model's
     *  power to fire. */
    fun completeSignature(): SigningState {
        check(step == SigningStep.COMBINATION && signatureBegun) {
            "The signature completes only from its own act, begun"
        }
        return copy(
            step = SigningStep.READY,
            result = SigningResult(
                lockedPackages = lockedPackages,
                liftingHour = liftingHour,
                sentence = sentence?.takeIf { it.isNotBlank() },
                taskLine = taskLine?.takeIf { it.isNotBlank() },
                breakMinutes = breakMinutes
            )
        )
    }
}
