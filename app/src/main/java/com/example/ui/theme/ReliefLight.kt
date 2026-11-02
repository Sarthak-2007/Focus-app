package com.example.ui.theme

/**
 * THE RELIGHT RULE (Audit A §4, KEPT — sitting walk record 3: the
 * rebuilt light was KEPT by Sarthak's eye after the first render's
 * light was sent back for performing "only in the dark").
 *
 * The mechanism, as settled: relief light is DERIVED FROM THE
 * SURFACE'S OWN LUMINANCE, distinct per state. It is a rendering
 * law for the app's surfaces, not a render trick on one sample:
 * given a surface colour, its raised edge is that colour moved
 * toward light by an amount the surface's own luminance governs,
 * and its cut edge is that colour moved toward shade — no foreign
 * light colour is ever imported onto the face.
 *
 * Audit A G-5: the per-state strength in-app is a build tuning
 * matter, set so the SEALED (home) state visibly carries it.
 * Strengths below are that tuning, recorded: SEALED carries the
 * relief at full working strength; SIGNING (evening, calm) a
 * breath under it; DAY (the door open, room lit) softer, because
 * the room does the work; DARK (the failed day, the ajar door in
 * a dark room) faintest — present, never performed.
 *
 * Pure arithmetic on 0..1 RGB triples — no Android, no Compose —
 * so the law is unit-testable off-device. UNPROVEN-ON-DEVICE
 * (Audit A G-7): the tuning is judged on the build, not claimed
 * proven on his OnePlus.
 *
 * STAMP (KEEPER-SR-5, register N-2, carried by name): the
 * strengths are also UNPROVEN AS TUNED — no eye has judged them
 * on a build — and they DIVERGE FROM THE FILED LAW they sit
 * beside: P0's Relight.kt ports RELIGHT-S67-1's eighths, under
 * which SEALED is the descended, poorest-lit state; under this
 * table SEALED is the strongest. Both cannot be the kept
 * mechanism's tuning. Ruling R2-6: P0's tuning is canonical and
 * this table is DELETED at composition unless argued with
 * evidence beforehand — this lane has none and consents
 * (lettered to SUCCESSOR-21 + BUILD-SR-1, 2026-11-02).
 */
enum class FaceLightState(val strength: Float) {
    SIGNING(0.70f),
    SEALED(0.85f),
    DARK(0.30f),
    DAY(0.55f)
}

/** An RGB triple in 0..1 floats. */
data class Srgb(val r: Float, val g: Float, val b: Float) {
    /** Rec. 601 luma — the surface's own luminance, the one
     *  input the relief is derived from. */
    fun luminance(): Float = 0.299f * r + 0.587f * g + 0.114f * b
}

object ReliefLight {

    /** How far the raised edge travels toward light, as a share
     *  of the headroom the luminance leaves. */
    private const val HIGHLIGHT_TRAVEL = 0.45f

    /** How far the cut edge travels toward shade, as a share of
     *  the luminance itself. */
    private const val SHADE_TRAVEL = 0.55f

    /**
     * The relief pair for [base] under [state]: (highlight, shade).
     * Both are the base colour's own hues moved along its own
     * luminance — highlight preserves channel ratios toward white,
     * shade scales the colour down — so the relief can never
     * introduce a hue the surface does not already carry.
     */
    fun reliefPair(base: Srgb, state: FaceLightState): Pair<Srgb, Srgb> {
        val l = base.luminance()
        val s = state.strength
        val kh = (s * (1f - l) * HIGHLIGHT_TRAVEL).coerceIn(0f, 1f)
        val ks = (s * l * SHADE_TRAVEL + s * 0.08f).coerceIn(0f, 0.95f)
        val highlight = Srgb(
            r = base.r + (1f - base.r) * kh,
            g = base.g + (1f - base.g) * kh,
            b = base.b + (1f - base.b) * kh
        )
        val shade = Srgb(
            r = base.r * (1f - ks),
            g = base.g * (1f - ks),
            b = base.b * (1f - ks)
        )
        return highlight to shade
    }

    /** The raised edge alone. */
    fun highlight(base: Srgb, state: FaceLightState): Srgb = reliefPair(base, state).first

    /** The cut (engraved / struck) edge alone. */
    fun shade(base: Srgb, state: FaceLightState): Srgb = reliefPair(base, state).second
}
