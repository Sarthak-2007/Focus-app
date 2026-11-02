package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * 1. Sora — Display & Section Headers (Sarthak's standing pick for display type):
 *    Used for the Rotary Dial numerals, Bento metrics, Desk Clock, and tactile headers.
 */
// Stage 5 weight truth (P0, verified firsthand by BUILD-SR-1 against the
// binaries' OS/2 tables): Sora carries 600/700 ONLY in this system.
// The repo also bundles sora.ttf (400) and sora_medium.ttf (500) — real
// files, but weights the law does not carry; they are deliberately NOT
// registered, so a request for Sora 400/500 resolves to the nearest lawful
// weight (600) instead of quietly founding an unfiled tier. The binaries
// stay on disk for the outgoing screens; FoundationP0Test reads their
// OS/2 weight classes so a swapped file fails the build, not the eye.
val SoraFamily = FontFamily(
    Font(R.font.sora_semibold, FontWeight.SemiBold),
    Font(R.font.sora_bold, FontWeight.Bold)
)

/**
 * 2. Atkinson Hyperlegible — Interface & Body (Sarthak's standing pick for UI text):
 *    Built for legibility; used for app rows, 3-stop switches, and explanatory copy.
 */
// Atkinson ships only Regular + Bold; Medium/SemiBold requests resolve
// to the nearest real weight instead of a fake one (Round 0b fix).
val AtkinsonHyperlegibleFamily = FontFamily(
    Font(R.font.atkinson_hyperlegible, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_bold, FontWeight.Bold)
)

/**
 * 3. Anonymous Pro — Telemetry & Numbers (Sarthak's standing pick for mono):
 *    Used for timers, serial numbers, peek tags, and quota badges.
 */
// Anonymous Pro ships only Regular + Bold (Round 0b fix, as above).
val AnonymousProFamily = FontFamily(
    Font(R.font.anonymous_pro, FontWeight.Normal),
    Font(R.font.anonymous_pro_bold, FontWeight.Bold)
)


val TightNumericPlatformStyle = PlatformTextStyle(includeFontPadding = false)
val CenteredNumericLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both
)

// =========================================================================
// THE STAGE 5 TYPE SYSTEM IN CODE — P0 FOUNDATION (BUILD-SR-1 · Slice 1)
// Source of law: ascent/stage-05-type/type-system.md (§1 scale, §3 map,
// §5 tracking, amendments A-4/A-6). Every size allowed to exist is below;
// a context that cannot fit at its step wraps or grows its measure — it
// never drops a step, and no later surface invents a size. Tracking is
// filed in em and priced here in sp (em x step size), per §5.4/§5.3.
// =========================================================================

/** A step the scale names but Compose cannot set as a fixed style —
 *  its law is a relation or the OS's presentation, recorded, not faked. */
data class StepDescriptor(
    val step: String,
    val face: String,
    val weight: Int,
    val law: String
)

object MonolithType {

    // ---- Screen steps (§1.1) — Atkinson body, Sora display ----
    /** SCR-0 — micro-label floor: secondary standing matter only. */
    val Scr0 = TextStyle(fontFamily = AtkinsonHyperlegibleFamily, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.72.sp)
    /** SCR-1 — secondary body. */
    val Scr1 = TextStyle(fontFamily = AtkinsonHyperlegibleFamily, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.28.sp)
    /** SCR-2 — primary body: composition, proposal, signing text. */
    val Scr2 = TextStyle(fontFamily = AtkinsonHyperlegibleFamily, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.16.sp)
    /** SCR-3 — emphasis in body surfaces: calling-out head, per-call handles.
     *  Atkinson 700 is spent here and only where the maps name it. */
    val Scr3 = TextStyle(fontFamily = AtkinsonHyperlegibleFamily, fontWeight = FontWeight.Bold,
        fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = 0.sp)
    /** SCR-4 — subheads at the signing. */
    val Scr4 = TextStyle(fontFamily = SoraFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = (-0.20).sp)
    /** SCR-5 — signing heads. */
    val Scr5 = TextStyle(fontFamily = SoraFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = (-0.24).sp)
    /** SCR-6 — largest screen display: at most once per surface. */
    val Scr6 = TextStyle(fontFamily = SoraFamily, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = (-0.64).sp)

    // ---- Telemetry steps (§1.1) — Anonymous Pro, tabular by construction ----
    /** ANON-0 — telemetry floor. */
    val Anon0 = TextStyle(fontFamily = AnonymousProFamily, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 0.39.sp)
    /** ANON-1 — figure body. */
    val Anon1 = TextStyle(fontFamily = AnonymousProFamily, fontWeight = FontWeight.Normal,
        fontSize = 15.sp, lineHeight = 20.sp, letterSpacing = 0.sp)
    /** ANON-2 — believed figures: held number, hour beside digits. */
    val Anon2 = TextStyle(fontFamily = AnonymousProFamily, fontWeight = FontWeight.Normal,
        fontSize = 17.sp, lineHeight = 24.sp, letterSpacing = 0.34.sp)
    /** ANON-3 — composition hours: figures that align. */
    val Anon3 = TextStyle(fontFamily = AnonymousProFamily, fontWeight = FontWeight.Normal,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.sp)
    /** ANON-4 — the digits line: read once, under pressure. */
    val Anon4 = TextStyle(fontFamily = AnonymousProFamily, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 32.sp, letterSpacing = 0.56.sp)

    // ---- Printed steps (§1.2) — the sealed face, rendered in-app at the
    // filed pt sizes (1pt priced as 1sp for the in-app rendering; the
    // physical print figures stay Stage 6/7's, stamped UNPROVEN there).
    // Printed tracking is never negative, ever (§5.3). ----
    /** PRT-2 — the printed floor: the held-mark slot (rank 4). Its Sora
     *  assignment is provisional on the B-U1 forum (A-6.5): if the forum
     *  rules glyph, step and place stand, letterform law falls to T50. */
    val Prt2 = TextStyle(fontFamily = SoraFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 25.sp, letterSpacing = 1.08.sp)
    /** PRT-3 — the door-word slot (rank 2): a kept place; no word exists
     *  (GAP U-3 / Audit A G-1) and none is invented here. */
    val Prt3 = TextStyle(fontFamily = SoraFamily, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = 1.20.sp)
    /** PRT-4 — his sentence or its kept place (rank 3): up to 3 lines; a
     *  longer sentence grows lines, never drops step. */
    val Prt4 = TextStyle(fontFamily = SoraFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 1.28.sp)
    /** PRT-5 — a CITATION, not a step (A-6.6; TYPE-SR-5 cure D-01): it
     *  survives only as the 48pt size-class ENG-1 binds to. The hour is
     *  engraved once; there is no printed twin, and this citation is not
     *  a consumable style — a surface reaching for the hour's figures
     *  consumes Eng1. (First form shipped Prt5 as a full TextStyle twin
     *  of Eng1; demoted on TYPE-SR-5's anchored cure.) */
    val Prt5 = StepDescriptor("PRT-5", "Anonymous Pro", 700,
        "The 48pt size-class citation ENG-1 binds to (48pt / 50 leading, +0.02em as filed for the " +
        "figure hand). NOT an available printed step and not a style: no printed surface may cite it.")
    /** ENG-1 (STEP-RING) — the lifting hour on the ring: Anonymous Pro 700
     *  as engraved translation, at the PRT-5 figure class. Rank 1, the
     *  face's first fact. KB01 UNPROBED rides it.
     *  Provenance of its tracking (TYPE-SR-5 cure D-02): the 0.96sp below
     *  is §5.3's printed hour-figure tracking (+0.02em) carried as the
     *  IN-APP approximation only. Engraved law (EL-1/EL-2) files no
     *  letterSpacing: the cut's tracking is the fixed cell advance, set
     *  once per surface, opened one step over the flat on the curve. */
    val Eng1 = TextStyle(fontFamily = AnonymousProFamily, fontWeight = FontWeight.Bold,
        fontSize = 48.sp, lineHeight = 50.sp, letterSpacing = 0.96.sp)

    /** FRM-1 — the forming record (the desk clock's numberless record):
     *  Atkinson 400 at the SCR-2 size class. The parenthetical is a size
     *  equivalence only (A-6.7): no screen law — tracking, rhythm, floor —
     *  reaches the forming kind through it; formation law is FL-1/FL-2
     *  (advance final from first appearance), owned by the record's lane. */
    // Tracking (TYPE-SR-5 cure D-03): FL-2 — the tracking value is the
    // completed-form value, set once; CF-1 — the completed record is
    // indistinguishable from a statically set line of the same face,
    // step, weight and tracking. The static body line at this size is
    // SCR-2, whose tracking is +0.01em = 0.16sp: Frm1 carries exactly
    // that, set once here; partial forms inherit it unchanged.
    val Frm1 = TextStyle(fontFamily = AtkinsonHyperlegibleFamily, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.16.sp)

    // ---- Steps carried as descriptors: the law is a relation or the
    // OS's own presentation, so no fixed style is honestly settable. ----
    /** PLT-2 (STEP-PLATE-LINE) — the plate's hours-held line. */
    val Plt2 = StepDescriptor("PLT-2", "Anonymous Pro", 400,
        "The day plate's hours-held line, read at arm's length; never below PLT-1. " +
        "No absolute size is filed — the plate's cell is priced by Stage 6/7 (FIG-4: 5 cells, HH:MM). " +
        "Weight 400 is ASSUMED by P0 (TYPE-SR-5 D-04): no filing names the plate steps' weight; " +
        "§6 lever 2 files equality of hand, which the shared assumed 400 honours — a filing naming the weight supersedes.")
    /** PLT-1 (STEP-BRACKET) — the bracket floor. */
    val Plt1 = StepDescriptor("PLT-1", "Anonymous Pro", 400,
        "One step above the smallest plate step — that +1 IS the margin, counted once (L-4 Lock Rule). " +
        "Never lighter than the hours-held line: same face, same hand as PLT-2. FIG-5: 15 cells, [HH:MM — HH:MM]. KB07 UNPROBED. " +
        "Weight: the assumed 400 of PLT-2 (see its note, TYPE-SR-5 D-04).")
    /** HLD-1 — the one plain notification. */
    val Hld1 = StepDescriptor("HLD-1", "Atkinson Hyperlegible", 400,
        "One plain notification on the plain lock screen, OS-presented size and tracking; " +
        "the type law governs form, not spacing. KB10 NO VERDICT.")

    // The notification's wording (RULED at type-system Amendment
    // A-6.2: "Locked until the hour") — the Hld1Wording mirror is
    // RETIRED (BUILD-SR-23, step 7, named cure): the line lives
    // only in the single copy source (copy_10_2_notification in
    // res/values/strongroom_strings.xml), where P8's poster reads
    // it. The type system keeps the HLD-1 FORM (above); it no
    // longer keeps a second literal home for the words.
}

// =========================================================================
// LEGACY LADDER — RETIRED BY P0, KEPT COMPILED, NO NEW CONSUMER.
// The named styles below (104/72/50/22/15/10 ladder) belong to the
// outgoing generation and stay byte-identical for its screens and tests
// (PillBadgeTextStyle alone has 42 call sites). New surfaces set type
// ONLY from MonolithType above. Deletion is the surface lanes' act.
// =========================================================================
// Monolith type ladder (TYPE-RAMP-TABLE, Set's audit, Oct 29 2026):
// six sizes only - 104 / 72 / 50 / 22 / 15 / 10 - no two tiers within 1.4x.
// Issue #63: the 50 tier is REAL now — DisplayUnitTextStyle below backs
// displaySmall and the Deck hero's unit spans; before, the ladder claim
// was false (50.sp existed only as an inline SpanStyle in TactileDeck).
// The old lying font aliases (SpaceGrotesk / PlusJakartaSans / JetBrainsMono
// / DmSans) are dead; call sites name the real families. BentoMetric (36sp,
// off-ladder, unused) is dead. The Atkinson "Medium" is dead - the family
// ships 400/700 only, so body text is honest 400.

/** H2 - the one big number in hand (running time, Deck total).
 *  Issue #256: tabular figures ("tnum") — Sora is a proportional
 *  face, so without this the ticking hero number changed width
 *  every second and the centred readout reflowed around it. */
val DialClockTextStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 72.sp,
    lineHeight = 72.sp,
    letterSpacing = (-2.0).sp,
    fontFeatureSettings = "tnum",
    platformStyle = TightNumericPlatformStyle,
    lineHeightStyle = CenteredNumericLineHeightStyle
)

/** H1 - across-the-room time. AOD only. Tabular figures, as above. */
val DeskClockTextStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 104.sp,
    lineHeight = 100.sp,
    letterSpacing = (-3.0).sp,
    fontFeatureSettings = "tnum",
    platformStyle = TightNumericPlatformStyle,
    lineHeightStyle = CenteredNumericLineHeightStyle
)

/** H3 - display unit tier: the 50sp unit span riding the 72sp hero digits. */
val DisplayUnitTextStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 50.sp,
    lineHeight = 50.sp,
    letterSpacing = (-1.5).sp,
    platformStyle = TightNumericPlatformStyle,
    lineHeightStyle = CenteredNumericLineHeightStyle
)

/** Dense B1 - same 15sp tier, tighter leading for stacked rows. */
val BodySmallTextStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 20.sp
)

/** L1 - the engraved plate: every micro-label, and nothing else. */
val PillBadgeTextStyle = TextStyle(
    fontFamily = AnonymousProFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 10.sp,
    lineHeight = 12.sp,
    letterSpacing = 2.0.sp,
    platformStyle = TightNumericPlatformStyle
)

/** T1 - section titles. */
val DrawerHeaderTextStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = 26.sp,
    letterSpacing = (-0.3).sp,
    platformStyle = TightNumericPlatformStyle
)

/** Button words - Sora 700, tracked +0.5. */
val PrimaryButtonTextStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp,
    platformStyle = TightNumericPlatformStyle
)

/** App names / row titles at B1 - Atkinson 700. */
val AppTitleTextStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 15.sp,
    lineHeight = 20.sp
)

/** B1 body - Atkinson 400 (the only honest body weight the family ships). */
val BodyTextStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp
)

/** The reception sheet's second voice (Fein §B/§C, FINISH-type-print):
 *  the door's job-adjacent lines — chit line, STALE gloss, prepare
 *  escort — in BodyTextStyle's values, worn in onSurfaceVariant at
 *  the call sites. Wave-2 fix (Pforte): ReceptionSheet used this
 *  name at four sites with no definition and no import anywhere in
 *  the tree — a compile-blocker caught by the Wave-2 finisher
 *  cluster, not by Wave-1's "print CLOSED" record. */
val DoorJobTextStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp
)

/** T1 in Atkinson - the ACTIVE task title. */
val TaskActiveTitleTextStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = 27.sp,
    platformStyle = TightNumericPlatformStyle
)

// The Material Typography val lives in Theme.kt, NOT here — on purpose:
// its initialiser reads MonolithType's styles, and MonolithType's styles
// read this file's font families. Two classes whose initialisers read
// each other are a class-init cycle: whichever starts second hands the
// first a field that is still null (the venue caught exactly this —
// every Typography slot read null in the test JVM). Theme.kt's
// initialisers never read back into this file, so the cycle cannot form.
