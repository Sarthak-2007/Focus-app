package com.example.ui.strongroom.grade

/**
 * STAGE 10 PRE-BUILD — THE DAY PLATE'S RENDERING, AT GRADE
 * (BUILD-SR-10, T24). DESIGNER-SR-4's plate-and-tray spec as data +
 * law, ready for BUILD-SR-4's P6 seam to consume at composition:
 * the brass shade ladder, the graded holes at their figures, the
 * failure plate's capped descent, the tray stack's deterministic
 * offsets, and the strike moment's constants.
 *
 * The fence, in code as in the spec: a plate is PROOF, never a
 * score — nothing here totals, ranks, or counts a pile; the stack
 * renders growth, never a number. Every physical figure is a DESIGN
 * value, UNPROVEN (G-4); plate-artefact persistence KB07 UNPROVEN.
 */
object PlateGradeRender {

    // ---- The brass shade ladder (spec §1.4, BR-06) -----------------
    // Brass renders in GOLD alone: every tone is the gold ink carried
    // toward the line ink (GRAPHITE) at a printed fraction. No ember,
    // no rose, no second hue — Slice 1's gold→ember highlight is
    // retired by this ladder (DESIGNER-SR-4 deviation 2).

    private fun brass(lerpToLine: Float): GradeColour {
        val gold = GradeColour.of(GradeInk.GOLD)
        val line = GradeColour.of(GradeInk.GRAPHITE)
        fun ch(a: Int, b: Int): Int = (a + (b - a) * lerpToLine).toInt().coerceIn(0, 255)
        return GradeColour(ch(gold.r, line.r), ch(gold.g, line.g), ch(gold.b, line.b))
    }

    val BRASS_FIELD: GradeColour = brass(0.18f)
    val BRASS_RAISED: GradeColour = brass(0.06f)
    val BRASS_RECESS: GradeColour = brass(0.38f)
    val BRASS_DEEP: GradeColour = brass(0.55f)

    /** A fallen day's field — the descent CAPPED at 0.34 (spec §4):
     *  the plate stays the frame's lightest object and its bracket
     *  pair reads at full line-work contrast. Dark, not buried. */
    val BRASS_FAILURE_FIELD: GradeColour = brass(0.34f)
    const val FAILURE_FIELD_LERP: Float = 0.34f

    /** The plate's thickness band (spec §1.3): what each plate in the
     *  stack contributes to the pile's height. */
    const val PLATE_THICKNESS_DP: Float = 3f

    /** The field's grain: fine horizontal striations, alpha ±0.03 of
     *  the line ink, 1 dp period — deterministic in y, never animated. */
    const val GRAIN_PERIOD_DP: Float = 1f
    const val GRAIN_ALPHA: Float = 0.03f

    // ---- The graded holes (spec §2; §MB-NUMBERS N-4) ---------------
    // Figures: REACH Ø 2.8 mm · HELD Ø 2.0 mm · FLOOR Ø 1.5 mm, at
    // Stage 5's conversion 1 mm ≈ 6.3 dp. CONSISTENCY LAW: the ratios
    // 1.87 : 1.33 : 1 ARE the grading — no kind may be re-scaled
    // except by re-scaling all three by the same factor.

    const val MM_TO_DP: Float = 6.3f

    enum class HoleKind(val figureMm: Float) {
        REACH(2.8f),
        HELD(2.0f),
        FLOOR(1.5f),
    }

    fun punchDiameterDp(kind: HoleKind): Float = kind.figureMm * MM_TO_DP

    fun ratioToFloor(kind: HoleKind): Float = kind.figureMm / HoleKind.FLOOR.figureMm

    // ---- One hole's anatomy (spec §2, BR-10: through) --------------
    // Four concentric facts. DEPTH IS NOT GRADED (SL-3): every
    // fraction is identical for all three kinds — diameter alone
    // carries the kind, so the hand learns one alphabet.

    /** The wall's radius as a fraction of the hole's radius. */
    const val WALL_FRACTION: Float = 0.86f

    /** The through-dark's radius fraction: the centre is the
     *  darkness behind the plate, not a floor. */
    const val THROUGH_DARK_FRACTION: Float = 0.62f

    /** The raised lip's offset toward the light, as a radius fraction. */
    const val LIP_OFFSET_FRACTION: Float = 0.10f

    /** The far rim's stroke width fraction (day presentation only:
     *  light through the hole catches the lower inner wall). */
    const val FAR_RIM_FRACTION: Float = 0.10f

    // ---- The strike moment (spec §3) --------------------------------
    // Struck ONCE, in one frame, at the lifting — before it, the
    // plate's place stands empty through bolts, door, and the handing
    // back (B-13 gates it). The settle is the blow's visible half.

    /** The plate drops the last 3 dp into the tray, hard-stop. */
    const val STRIKE_SETTLE_DP: Float = 3f

    /** Settle duration: fast attack, no overshoot, no bounce. */
    const val STRIKE_SETTLE_MS: Int = 90

    /** Failure days have NO strike moment: found, already struck. */
    fun strikeMomentExists(failureDay: Boolean): Boolean = !failureDay

    /** The unstruck seal place (spec §4): on a failure plate the
     *  seal's place stands EMPTY — subtraction, never a crack
     *  pictogram (B-11: the seal gone, and NOTHING added). */
    enum class SealPlace { STRUCK_RING, EMPTY_PLACE }

    fun sealPlace(failureDay: Boolean): SealPlace =
        if (failureDay) SealPlace.EMPTY_PLACE else SealPlace.STRUCK_RING

    // ---- The tray stack (spec §5) -----------------------------------
    // Plates lie in struck order, each at a hand-stacked offset that
    // is a PURE FUNCTION of its filing date: the same pile renders
    // identically on every visit. Growth is the display; beyond the
    // drawn-edge cap the picture stays and the list carries the season.

    const val MAX_DRAWN_EDGES: Int = 24
    const val STACK_OFFSET_X_RANGE_DP: Int = 3
    const val STACK_OFFSET_Y_RANGE_DP: Int = 1

    /** Rotation in tenths of a degree, range ±4 (±0.4°). */
    fun stackRotationTenths(filingEpochDay: Long): Int =
        (((filingEpochDay * 13L) % 9L) - 4L).toInt()

    fun stackOffsetX(filingEpochDay: Long): Int =
        (((filingEpochDay * 31L + 17L) % 7L) - 3L).toInt()

    fun stackOffsetY(filingEpochDay: Long): Int =
        (((filingEpochDay * 7L + 3L) % 3L) - 1L).toInt()

    fun drawnEdges(pileSize: Int): Int = minOf(pileSize, MAX_DRAWN_EDGES)
}
