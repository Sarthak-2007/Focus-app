package com.example.ui.foundation

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * THE TYPE SYSTEM IN CODE — Stage 5's system, whole.
 *
 * Authored from zero by BUILD-SR-33 (T24) for the from-zero rebuild.
 * Sole source: `ascent/stage-05-type/type-system.md` — the composed
 * system including its amendments A-1…A-9 as they stand on disk.
 * Every size, weight, and tracking value below cites its line there.
 * A size not in this file does not exist in the app: a context that
 * cannot fit at its step WRAPS OR GROWS ITS MEASURE — it never drops
 * a step (type-system §1). No surface invents a size, because the
 * foundation offers none outside the scales.
 *
 * Element-first (Addendum 3 §2): this type labels the machine and
 * marks its moments — the hour, the plate's lines, the digits. It
 * carries no content; the words themselves are the copy census's,
 * decided elsewhere, and no string lives in this file.
 *
 * FONT LAW (type-system head): Sora (display) · Atkinson Hyperlegible
 * (interface/body) · Anonymous Pro (telemetry) are Sarthak's defaults,
 * kept in every context of this system.
 * WEIGHT TRUTH (§1): Atkinson ships 400 and 700 ONLY — no medium or
 * semibold body weight is invented anywhere; Sora carries 600/700;
 * Anonymous Pro carries 400/700. The font binaries stand in
 * `res/font` (verified on main, build record U-8): only the lawful
 * weights are registered here, so an unlawful weight request resolves
 * to the nearest lawful weight instead of founding an unfiled tier.
 */
object StrongroomFonts {

    /** Sora — display. Lawful weights in this system: 600/700 only. */
    val Sora: FontFamily = FontFamily(
        Font(R.font.sora_semibold, FontWeight.SemiBold),
        Font(R.font.sora_bold, FontWeight.Bold)
    )

    /** Atkinson Hyperlegible — interface & body. 400/700 only, ever. */
    val Atkinson: FontFamily = FontFamily(
        Font(R.font.atkinson_hyperlegible, FontWeight.Normal),
        Font(R.font.atkinson_hyperlegible_bold, FontWeight.Bold)
    )

    /** Anonymous Pro — telemetry & figures. 400/700. Its advance is a
     *  single fixed 0.5459em for every glyph (Amendment A-2, verified
     *  from the font file: 1118/2048 units) — figures are tabular by
     *  construction and can never shift width as they form (§4). */
    val AnonymousPro: FontFamily = FontFamily(
        Font(R.font.anonymous_pro, FontWeight.Normal),
        Font(R.font.anonymous_pro_bold, FontWeight.Bold)
    )

    /** Anonymous Pro's one true advance, in em (Amendment A-2). The
     *  0.60em figure sometimes quoted is another face's — never this
     *  one's — and is not used anywhere in this foundation. */
    const val ANONYMOUS_PRO_ADVANCE_EM: Float = 0.5459f
}

/**
 * A step the scale names but no fixed screen style can honestly carry
 * — its law is a relation, a physical cell, or the OS's own
 * presentation. It is recorded as law, never faked as a style.
 */
data class StrongroomStepDescriptor(
    val step: String,
    val face: String,
    val weight: Int,
    val law: String
)

/**
 * The scales. Names are the system's own: SCR (screen) · ANON
 * (telemetry) · PRT (printed, the sealed face) · PLT (struck, the
 * plate) · HLD (held-live, the notification) · FRM (forming, the
 * desk clock's record) — plus ENG-1, the engraved hour.
 *
 * Tracking is filed in em in the system and priced here in sp
 * (em × step size), per §5. Screen rhythm: baseline unit 4sp, every
 * leading a multiple of it (§2). Printed rhythm: the 5pt grid (§2);
 * in-app, the printed steps are carried at 1pt = 1sp — the face is
 * composed once, at its printed scale.
 */
object StrongroomType {

    // ── §1.1 SCREEN — Atkinson body, Sora display ──────────────────

    /** SCR-0 · 12/16 · Atkinson 400 · +0.06em — micro-label floor:
     *  secondary standing matter only. Nothing carrying meaning sits
     *  below this step (§5.4 floor). */
    val Scr0 = TextStyle(
        fontFamily = StrongroomFonts.Atkinson, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.72.sp
    )

    /** SCR-1 · 14/20 · Atkinson 400 · +0.02em — secondary body. */
    val Scr1 = TextStyle(
        fontFamily = StrongroomFonts.Atkinson, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.28.sp
    )

    /** SCR-2 · 16/24 · Atkinson 400 · +0.01em — PRIMARY BODY: the
     *  composition, the proposal, the signing text. */
    val Scr2 = TextStyle(
        fontFamily = StrongroomFonts.Atkinson, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.16.sp
    )

    /** SCR-3 · 18/24 · Atkinson 700 · 0 — emphasis in body surfaces:
     *  the calling-out head, per-call handles. Atkinson 700 is spent
     *  only where the maps name it (§3.4). */
    val Scr3 = TextStyle(
        fontFamily = StrongroomFonts.Atkinson, fontWeight = FontWeight.Bold,
        fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = 0.sp
    )

    /** SCR-4 · 20/28 · Sora 600 · −0.01em — subheads at the signing. */
    val Scr4 = TextStyle(
        fontFamily = StrongroomFonts.Sora, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = (-0.20).sp
    )

    /** SCR-5 · 24/32 · Sora 600 · −0.01em — signing heads. */
    val Scr5 = TextStyle(
        fontFamily = StrongroomFonts.Sora, fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = (-0.24).sp
    )

    /** SCR-6 · 32/40 · Sora 700 · −0.02em — largest screen display:
     *  at most once per surface. */
    val Scr6 = TextStyle(
        fontFamily = StrongroomFonts.Sora, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = (-0.64).sp
    )

    // ── §1.1 TELEMETRY — Anonymous Pro, tabular by construction ────

    /** ANON-0 · 13/16 · 400 · +0.03em — telemetry floor. */
    val Anon0 = TextStyle(
        fontFamily = StrongroomFonts.AnonymousPro, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 0.39.sp
    )

    /** ANON-1 · 15/20 · 400 · 0 — figure body. */
    val Anon1 = TextStyle(
        fontFamily = StrongroomFonts.AnonymousPro, fontWeight = FontWeight.Normal,
        fontSize = 15.sp, lineHeight = 20.sp, letterSpacing = 0.sp
    )

    /** ANON-2 · 17/24 · 400 · +0.02em — BELIEVED FIGURES: the held
     *  number, the hour beside digits. Believed figures never sit
     *  below this step (§6 screen floor). */
    val Anon2 = TextStyle(
        fontFamily = StrongroomFonts.AnonymousPro, fontWeight = FontWeight.Normal,
        fontSize = 17.sp, lineHeight = 24.sp, letterSpacing = 0.34.sp
    )

    /** ANON-3 · 20/28 · 400 · 0 — composition hours: figures that align. */
    val Anon3 = TextStyle(
        fontFamily = StrongroomFonts.AnonymousPro, fontWeight = FontWeight.Normal,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.sp
    )

    /** ANON-4 · 28/32 · 700 · +0.02em — the digits line: read once,
     *  under pressure. */
    val Anon4 = TextStyle(
        fontFamily = StrongroomFonts.AnonymousPro, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 32.sp, letterSpacing = 0.56.sp
    )

    // ── §1.2 PRINTED — the sealed face ─────────────────────────────
    // Printed tracking is NEVER negative, on any context, ever (§5.3).
    // PRT-0/PRT-1 (10pt/14pt) are named and FORBIDDEN on the face —
    // they exist so their refusal has a step to cite; they are not
    // offered here, and the printed floor is PRT-2.

    /** PRT-2 · 18/25 · Sora 600 · +0.06em — THE PRINTED FLOOR: the
     *  held-mark slot (§3 rank 4 — testimony, never announcement).
     *  Its Sora assignment is provisional on the held-mark form forum
     *  (Amendment A-3.9): step and place stand regardless. */
    val Prt2 = TextStyle(
        fontFamily = StrongroomFonts.Sora, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 25.sp, letterSpacing = 1.08.sp
    )

    /** PRT-3 · 24/30 · Sora 700 · +0.05em — the door-word slot (§3
     *  rank 2). The slot's style stands ready; NO WORD EXISTS for it
     *  (GAP U-3) and none is invented by this foundation — the slot
     *  renders as its kept place until a word is decided elsewhere. */
    val Prt3 = TextStyle(
        fontFamily = StrongroomFonts.Sora, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = 1.20.sp
    )

    /** PRT-4 · 32/40 · Sora 600 · +0.04em — his sentence, or its kept
     *  place (§3 rank 3): up to 3 lines; a longer sentence grows
     *  lines, never drops step. */
    val Prt4 = TextStyle(
        fontFamily = StrongroomFonts.Sora, fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 1.28.sp
    )

    /** PRT-5 · 48pt/50 · Anonymous Pro 700 · +0.02em — a CITATION,
     *  not a step (Amendment A-6.6): it survives only as the figure
     *  size-class ENG-1 binds to. The hour is engraved once; there is
     *  no printed twin, and no printed surface may cite this. */
    val Prt5 = StrongroomStepDescriptor(
        step = "PRT-5", face = "Anonymous Pro", weight = 700,
        law = "The 48pt size-class citation ENG-1 binds to (48pt / 50 leading, " +
            "+0.02em as filed for the figure hand). NOT an available printed step " +
            "and not a style: the hour has no printed twin."
    )

    /** ENG-1 (STEP-RING) · the PRT-5 figure class · Anonymous Pro 700
     *  as ENGRAVED TRANSLATION — the lifting hour on the ring (§3
     *  RANK 1: the face's first fact; the type layer's verdict line).
     *  KB01 UNPROBED rides it whole.
     *  The 0.96sp letterSpacing here is §5.3's printed hour-figure
     *  tracking (+0.02em) carried as the IN-APP approximation only:
     *  engraved law (EL-1/EL-2) files no letterSpacing — a cut's
     *  tracking is its fixed cell advance, opened one step over the
     *  flat on the curve, measured as arc length. The hour's arc at
     *  this class measures 46.2mm; the cut proceeds only at a filed
     *  ring radius ≥ 24mm (Amendment A-3.4) — a radius this
     *  foundation does not file, because none is decided. */
    val Eng1 = TextStyle(
        fontFamily = StrongroomFonts.AnonymousPro, fontWeight = FontWeight.Bold,
        fontSize = 48.sp, lineHeight = 50.sp, letterSpacing = 0.96.sp
    )

    // ── §1.3 PHYSICAL / HELD / FORMING — carried as law ────────────

    /** PLT-2 (STEP-PLATE-LINE) — the day plate's hours-held line:
     *  Anonymous Pro at plate reading step, arm's length; never below
     *  PLT-1. FIG-4: pattern HH:MM = 5 fixed cells. KB07 UNPROBED.
     *  No absolute size is filed — the plate's cell is Stage 6/7's
     *  substrate number, still a named gap; this foundation files no
     *  substitute. The weight stands as the system's same-hand
     *  assumption (400, shared with PLT-1 per §6's same-hand rule)
     *  until a filing names it. */
    val Plt2 = StrongroomStepDescriptor(
        step = "PLT-2", face = "Anonymous Pro", weight = 400,
        law = "The plate's hours-held line (TI-19): 5 cells, HH:MM, one figure " +
            "per cell, tabular. Never below PLT-1. KB07 UNPROBED."
    )

    /** PLT-1 (STEP-BRACKET) — the bracket floor (§6): one scale step
     *  ABOVE the smallest plate step — that +1 IS the margin, counted
     *  once, never twice (the L-4 Lock Rule, Amendment A-12.3). Same
     *  face, same hand, never lighter than the hours-held line.
     *  FIG-5: 15 cells — [ HH:MM — HH:MM ] with em-dash U+2014
     *  (Amendment A-9). KB07 UNPROBED. */
    val Plt1 = StrongroomStepDescriptor(
        step = "PLT-1", face = "Anonymous Pro", weight = 400,
        law = "The fallen day's bracket pair (TI-22): 15 cells at one step above " +
            "the smallest plate step; same hand as PLT-2; never squeezed to fit — " +
            "overflow returns as a conflict, never a smaller step. KB07 UNPROBED."
    )

    /** HLD-1 — the one plain notification: Atkinson 400 at the OS
     *  lock-screen presentation size. The OS renders the surface;
     *  the type law governs FORM, not spacing (§5.5): one plain
     *  notification and nothing more. KB10 NO VERDICT rides it. */
    val Hld1 = StrongroomStepDescriptor(
        step = "HLD-1", face = "Atkinson Hyperlegible", weight = 400,
        law = "One plain notification on the plain lock screen, OS-presented; " +
            "no tracking value is borrowed from screen law. KB10 NO VERDICT."
    )

    /** FRM-1 — the forming record (the desk clock's live record,
     *  which carries NO NUMBERS — §7, FF-1: figures prohibited, not
     *  styled; quantity is carried by extent). Atkinson 400 at the
     *  SCR-2 size class — a size equivalence only: no screen law
     *  reaches the forming kind through it. Formation law is FL-1/
     *  FL-2: advance and tracking are FINAL FROM FIRST APPEARANCE,
     *  set once at the completed-form value — the static body line
     *  at this size is SCR-2, whose tracking is +0.01em = 0.16sp,
     *  carried here exactly, inherited unchanged by partial forms. */
    val Frm1 = TextStyle(
        fontFamily = StrongroomFonts.Atkinson, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.16.sp
    )

    /** The sealed face's one hierarchy (§3.3 THE RULING), as data —
     *  rank is NOT size: 1 the lifting hour (ENG-1) · 2 the door-word
     *  slot (PRT-3) · 3 his sentence or its kept place (PRT-4) ·
     *  4 the held-mark slot (PRT-2). The door-word outranks the
     *  sentence while remaining the smaller step; the hour outranks
     *  every label around it and is the face's first fact. */
    val SealedFaceHierarchy: List<String> = listOf("ENG-1", "PRT-3", "PRT-4", "PRT-2")
}
