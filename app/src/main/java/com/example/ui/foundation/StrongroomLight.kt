package com.example.ui.foundation

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * THE REBUILT LIGHT AS THEME LAW — RELIGHT.
 *
 * Authored from zero by BUILD-SR-33 (T24) for the from-zero rebuild.
 * Sources, read firsthand: RELIGHT-S67-1's filing
 * (`stage-06-07-material-colour/eye-sample/rethink/relight-s67.md` —
 * "L5 rethought from the start") · the Grade Book §GB-L (the lamp
 * order + the four lights' behaviour law, ratified whole by its
 * convenor) and §1.1–§1.2 (mood and intent per state) · the ledger's
 * E-10 (his eye KEEP: "the REBUILT LIGHT — relief light derived from
 * the surface's own luminance, distinct per state") · build record
 * G-5/U-6 (the MECHANISM is kept/settled; per-state strength in-app
 * is a tuning matter).
 *
 * ONE MECHANISM, FOUR BEHAVIOURS (RELIGHT §2): relief is computed
 * from the surface's OWN luminance — nothing is drawn in. A mark on
 * the light-facing lip of a cut blends toward its class's LIT partner
 * ink; on the shadow side, toward its SHADOW partner ink. Every blend
 * is quantised to whole EIGHTHS: a derived colour is always a named
 * lawful ink blended toward another named lawful ink at a printed
 * fraction — never a new token, never a flat field, never green
 * (every partner pair below is drawn from the seven; blends between
 * them stay inside the palette's hue fence as filed, Grade Book
 * §1.5/R3 — the gold×blue mixture family is presumptively refused
 * and appears nowhere here).
 *
 * ═══ UNPROVEN — READ THIS BEFORE TUNING ═══
 * The mechanism is SETTLED (his eye kept it, walk record 3's
 * correction, via ledger E-10). The per-state STRENGTH values in
 * [StrongroomLight.paramsFor] are the filing's printed fractions
 * carried into code as the starting law; their in-app strength is
 * UNPROVEN (build record U-6) until his eye settles them on the
 * running build. They are named here, in one place, as data — a
 * lane that tunes a strength silently at a call site has forked
 * the law. Device reads are separately OWED-TO-DEVICE: KB10 NO
 * VERDICT stands; nothing here claims how a device renders these
 * blends. KB01 UNPROBED rides the engraved hour; KB07 UNPROBED
 * rides the plate whole.
 */
enum class StrongroomLightState {
    /** L-SIGN — the signing: lamp full and warm, door open. */
    SIGNING,

    /** L-SEALED — the sealed hours: stillness, two promoted reads. */
    SEALED,

    /** L-DARK — the failure face: lamp out, subtraction only. */
    DARK,

    /** L-DAY — the record read: plain, unwarmed, overhead light. */
    DAY
}

/** What is being lit. The plate is NOT on the sealed face and does
 *  not share the face's descents (RELIGHT §3). */
enum class StrongroomLitSurface { FACE, PLATE }

/** Per-state light behaviour, as data — fractions in eighths, exactly
 *  as RELIGHT §2/§3 prints them. Strengths: UNPROVEN (see head). */
data class StrongroomLightParams(
    /** Light direction (x, y), screen space, y down. */
    val lightDirectionX: Float,
    val lightDirectionY: Float,
    /** Descent of a FACE field toward the graphite floor, in eighths. */
    val faceDescentEighths: Int,
    /** Descent of a PLATE field toward the graphite floor, in eighths. */
    val plateDescentEighths: Int,
    /** Whether a warm lit partner exists in this state at all. In
     *  DARK the lamp is out and NO warm read survives; in DAY no warm
     *  partner exists in the model — relief is recess shadow only. */
    val warmLitPartner: Boolean,
    /** Cap on lit-side blending, in eighths. Enamel's own cap is
     *  lower still — see [StrongroomLight.ENAMEL_LIT_CAP_EIGHTHS]. */
    val maxLitEighths: Int,
    /** DARK's law: subtraction only — nothing is ever brightened. */
    val subtractionOnly: Boolean
)

object StrongroomLight {

    /** Enamel's lit cap: the lamp's warmth reaches the enamel at
     *  ≤ 1/4 (2/8), L-SIGN only (RELIGHT §2, enamel row: "#E47E35
     *  at ≤1/4, L-SIGN only — the lamp's warmth exists only as light
     *  falling on carriers, never as pigment"). */
    const val ENAMEL_LIT_CAP_EIGHTHS: Int = 2

    /** L-DARK's plate recess deepening cap: ≤ 3/8 (RELIGHT §3,
     *  L-DARK: "its recess shadow deepened (≤3/8)"). */
    const val DARK_PLATE_RECESS_CAP_EIGHTHS: Int = 3

    fun paramsFor(state: StrongroomLightState): StrongroomLightParams = when (state) {
        // L-SIGN — raking from the upper left at a low angle
        // (-0.83, -0.55); relief at full strength; no descent.
        StrongroomLightState.SIGNING -> StrongroomLightParams(
            lightDirectionX = -0.83f, lightDirectionY = -0.55f,
            faceDescentEighths = 0, plateDescentEighths = 0,
            warmLitPartner = true, maxLitEighths = 8, subtractionOnly = false
        )
        // L-SEALED — poverty on purpose: the whole face mass descends
        // 3/8 toward graphite; warmth reaches the door only as spill
        // (lit cap 1/8). The plate is carried EXACT and unlit: it lies
        // in the tray, and the tray's light is an open question
        // (Q-L3-BRASS-2), not this law's invention.
        StrongroomLightState.SEALED -> StrongroomLightParams(
            lightDirectionX = -0.83f, lightDirectionY = -0.55f,
            faceDescentEighths = 3, plateDescentEighths = 0,
            warmLitPartner = true, maxLitEighths = 1, subtractionOnly = false
        )
        // L-DARK — lamp out; subtraction only. The headline face
        // descent is per class (see darkFaceDescentEighths: enamel
        // 5/8, gold 1/2); the plate descends only 1/8 and so stands
        // as the frame's lightest object BY ARITHMETIC — nothing was
        // brightened (RELIGHT §3, L-DARK).
        StrongroomLightState.DARK -> StrongroomLightParams(
            lightDirectionX = 0f, lightDirectionY = 0f,
            faceDescentEighths = 5, plateDescentEighths = 1,
            warmLitPartner = false, maxLitEighths = 0, subtractionOnly = true
        )
        // L-DAY — plain, unwarmed, overhead (0, -1). Relief by recess
        // shadow only: no warm partner, no promotion, no theatre.
        // The face is at CHECK duty only in this state (Grade Book
        // §GB-L: L-DAY is not a presentation state for the face).
        StrongroomLightState.DAY -> StrongroomLightParams(
            lightDirectionX = 0f, lightDirectionY = -1f,
            faceDescentEighths = 0, plateDescentEighths = 0,
            warmLitPartner = false, maxLitEighths = 0, subtractionOnly = false
        )
    }

    /** The material classes the filed partner table names. A colour
     *  too far from every classed ink is OTHER — and OTHER's law is
     *  lead's law: no change in any state; it reads by shape
     *  (RELIGHT §2, lead row; Grade Book §GB-L.4(d), the invariant
     *  class). */
    enum class InkClass { ENAMEL, GOLD, GRAPHITE, OTHER }

    private const val CLASSIFY_MAX_DISTANCE: Float = 0.38f

    fun classify(color: Color): InkClass {
        fun dist2(c: Color): Float {
            val dr = color.red - c.red
            val dg = color.green - c.green
            val db = color.blue - c.blue
            return dr * dr + dg * dg + db * db
        }
        val nearest = listOf(
            InkClass.ENAMEL to dist2(StrongroomInks.Violet),
            InkClass.GOLD to dist2(StrongroomInks.Gold),
            InkClass.GRAPHITE to dist2(StrongroomInks.Graphite)
        ).minByOrNull { it.second }!!
        return if (sqrt(nearest.second) <= CLASSIFY_MAX_DISTANCE) nearest.first else InkClass.OTHER
    }

    /** Relative luminance (sRGB-linearised) — the surface's own
     *  value the relief field is derived from. */
    fun luminance(color: Color): Float {
        fun lin(ch: Float): Float =
            if (ch <= 0.04045f) ch / 12.92f
            else Math.pow(((ch + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()
        return 0.2126f * lin(color.red) + 0.7152f * lin(color.green) + 0.0722f * lin(color.blue)
    }

    /** The only derivation the law allows: [from] blended toward
     *  [to] at a whole number of eighths (0..8). Quantised by
     *  construction — the derivation printing RELIGHT §2 requires. */
    fun blendEighth(from: Color, to: Color, eighths: Int): Color {
        val steps = eighths.coerceIn(0, 8)
        if (steps == 0) return from
        if (steps == 8) return to
        return lerp(from, to, steps / 8f)
    }

    /** Descent toward the graphite floor at a printed fraction. */
    fun descend(color: Color, eighths: Int): Color =
        blendEighth(color, StrongroomInks.Graphite, eighths)

    /** The lit partner of a mark, per the filed partner table
     *  (RELIGHT §2): enamel and gold warm toward shellac orange; a
     *  graphite cut's lip is the FIELD ink it is cut into, catching
     *  light; OTHER never moves. Where the state has no warm partner
     *  (DARK, DAY), the lit partner is the mark itself — relief
     *  comes from the shadow side alone. */
    fun litPartner(mark: Color, field: Color, state: StrongroomLightState): Color {
        if (!paramsFor(state).warmLitPartner) return mark
        return when (classify(mark)) {
            InkClass.ENAMEL -> StrongroomInks.Orange
            InkClass.GOLD -> StrongroomInks.Orange
            InkClass.GRAPHITE -> field
            InkClass.OTHER -> mark
        }
    }

    /** The shadow partner: the graphite floor — except graphite
     *  itself, whose floor does not darken past graphite, and OTHER,
     *  which is unmoved. */
    fun shadowPartner(mark: Color): Color = when (classify(mark)) {
        InkClass.GRAPHITE -> mark
        InkClass.OTHER -> mark
        else -> StrongroomInks.Graphite
    }

    /**
     * One mark, lit. [gradient] is the mark's own luminance gradient
     * projected onto the state's light direction, in −1..1 (positive
     * = light-facing lip, negative = shadow side). The blend
     * magnitude is quantised to whole eighths and capped by the
     * state's law — enamel's ≤1/4 cap and DARK's subtraction-only
     * rule included. THE KNOCK LIGHTS NOTHING (Grade Book §1.1):
     * no interaction term exists anywhere in this model, and none
     * may be added at a call site.
     */
    fun relief(mark: Color, field: Color, gradient: Float, state: StrongroomLightState): Color {
        val params = paramsFor(state)
        val g = gradient.coerceIn(-1f, 1f)
        return when {
            g > 0f -> {
                if (params.subtractionOnly || params.maxLitEighths == 0) {
                    mark
                } else {
                    var cap = params.maxLitEighths
                    if (classify(mark) == InkClass.ENAMEL) {
                        cap = minOf(cap, ENAMEL_LIT_CAP_EIGHTHS)
                    }
                    val steps = (g * cap).roundToInt().coerceIn(0, cap)
                    blendEighth(mark, litPartner(mark, field, state), steps)
                }
            }
            g < 0f -> {
                val steps = (-g * 8f).roundToInt().coerceIn(0, 8)
                blendEighth(mark, shadowPartner(mark), steps)
            }
            else -> mark
        }
    }

    /** L-DARK's per-class FACE descents (RELIGHT §3): enamel 5/8,
     *  door gold 1/2, the leaf's orange shellac stipple descending
     *  with the gold family at 5/8 where it stands as a mark's own
     *  colour — exposed as data so no consumer encodes its own
     *  fraction. The PLATE's dark descent is not per-class: it is
     *  paramsFor(DARK).plateDescentEighths (1/8) for every class. */
    fun darkFaceDescentEighths(inkClass: InkClass): Int = when (inkClass) {
        InkClass.ENAMEL -> 5
        InkClass.GOLD -> 4
        InkClass.GRAPHITE -> 0
        InkClass.OTHER -> 0
    }

    /** A whole surface's mass under a state, before per-mark relief:
     *  the filed descents. SEALED descends the face 3/8 and carries
     *  the plate exact; DARK descends the face per class and the
     *  plate 1/8; DAY and SIGNING descend nothing. OTHER-class
     *  colours never move, in any state (lead's law). */
    fun stateSurface(
        base: Color,
        state: StrongroomLightState,
        surface: StrongroomLitSurface
    ): Color {
        val params = paramsFor(state)
        val inkClass = classify(base)
        if (inkClass == InkClass.OTHER) return base
        if (state == StrongroomLightState.DARK && surface == StrongroomLitSurface.FACE) {
            return descend(base, darkFaceDescentEighths(inkClass))
        }
        val eighths = if (surface == StrongroomLitSurface.FACE) {
            params.faceDescentEighths
        } else {
            params.plateDescentEighths
        }
        return descend(base, eighths)
    }

    // ── L-SEALED's two promoted reads — and no third (RELIGHT §3) ──

    /** (i) The engraved lifting hour at resting brightness: exact
     *  graphite, holding while the field around it descends, its
     *  field re-derived after descent so the hour reads first-glance
     *  by its shadow. KB01 UNPROBED. */
    fun sealedHourMark(): Color = StrongroomInks.Graphite

    /** (ii) The pinstripe glint: exact gold, held — gold thin enough
     *  to be scarce is the only leaf that glints; the leaf passage
     *  descends with the field and is NOT a promoted read. */
    fun sealedPinstripe(): Color = StrongroomInks.Gold

    /** The pinstripe's light-facing lip: warmed ONE 1/8 step toward
     *  orange — spill, not lamp. */
    fun sealedPinstripeLitLip(): Color =
        blendEighth(StrongroomInks.Gold, StrongroomInks.Orange, 1)
}

/** The session's light state, provided by the theme
 *  ([StrongroomTheme]). Surfaces read the state and ask
 *  [StrongroomLight] for their colours; a surface baking a per-state
 *  colour of its own has forked the law. */
val LocalStrongroomLightState = compositionLocalOf { StrongroomLightState.SIGNING }
