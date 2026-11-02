package com.example.ui.foundation

import androidx.compose.ui.graphics.Color

/**
 * THE TOKEN LAYER — the seven inks as the Strongroom's colour law.
 *
 * Authored from zero by BUILD-SR-33 (T24) for the from-zero UI rebuild
 * (SUCCESSOR-22, Addendum 2: "Delete all ui files and start over").
 * Every figure below is taken from the DECISION FILINGS, cited inline —
 * never from the dying UI tree this package will outlive. Where a filing
 * and the dying tree ever differed, the filing governed (AL-0056).
 *
 * The palette is CLOSED. Sources: Sarthak's palette line of 2026-10-18
 * (MEMORY.md) · the Applied/Unapplied Ledger F-1 ("Palette ONLY (seven
 * inks), never green") · the Stage 6/7 Grade Book's palette of record
 * (its convenor's head, ruling C-GB-1). Exactly seven inks exist. No
 * eighth value, at any alpha, in any role, is lawful in this app.
 * NEVER GREEN: for every ink, green < max(red, blue) — asserted by
 * [StrongroomInkLaw] at class load and by the foundation's tests.
 *
 * Element-first (Addendum 3 §2): these tokens exist to carry the
 * machine — the door's ground, its leaf and line-work, the plate's
 * metal. They carry no content of their own.
 */
object StrongroomInks {

    /** #F04FCB — magenta. A mark ink only: never a field (Grade Book
     *  §1.3: withheld from every field; on graphite ground only, §SW P-9
     *  pairs it with violet solely as a failure-tell, never a grade). */
    val Magenta = Color(0xFFF04FCB)

    /** #493EA4 — deep violet. THE GROUND: the door's enamelled-steel
     *  finish (Ledger E-7, his eye KEEP: "the deep-violet ground &
     *  finish, gold leaf + pinstripe"; Grade Book §SW P-1, paired on
     *  enamelled steel). */
    val Violet = Color(0xFF493EA4)

    /** #E2BC39 — gold. Gold leaf + the hand-laid pinstripe on the door
     *  (Ledger E-7), and the brass day plate struck from this ink ALONE
     *  — light and shadow on the plate are light behaviour, never a
     *  second hue (Grade Book §1.5, curing D-06; §SW P-2/P-6). */
    val Gold = Color(0xFFE2BC39)

    /** #C84481 — rose. A mark ink only: no carried field claim stands
     *  filed for it (Grade Book §SW P-11, UNPAIRED); lawful at mark
     *  scale on the graphite ground where a settled element carries it
     *  (Grade Book §1.3). */
    val Rose = Color(0xFFC84481)

    /** #E47E35 — orange. Shellac warmth. This ink exists AS LIGHT
     *  falling on carriers and as tone over the leaf — never as a
     *  pigment, never as a flat fill (Grade Book §1.5, D-03; §SW
     *  P-3/P-8). [StrongroomLight] is its only lawful spender. */
    val Orange = Color(0xFFE47E35)

    /** #377ED0 — blue. The room's one cool accent, held in reserve at
     *  mark scale (Grade Book §1.1/§1.3). NOTE, firsthand (AL-0056):
     *  a forged near-copy of this ink, #377E4B, circulated in a Stage
     *  6/7 brief and was ruled VOID on the Grade Book's face (C-GB-1)
     *  — it leans green and exists nowhere in the decisions. It is
     *  named here only so no later hand re-imports it. */
    val Blue = Color(0xFF377ED0)

    /** #34323B — graphite. The printings' dark line-work (Ledger E-8,
     *  his eye KEEP), the steel of case/wheel/dial/bolts, the room's
     *  mass, lead's dullness rendered inside its depths, and the floor
     *  every RELIGHT descent walks toward (Grade Book §SW P-4/P-5/P-7;
     *  §1.5: consumed verbatim — "#34323B depths" names no shades). */
    val Graphite = Color(0xFF34323B)

    /** The closed census, in palette order. Exactly seven. */
    val All: List<Color> = listOf(Magenta, Violet, Gold, Rose, Orange, Blue, Graphite)
}

/**
 * The roles — the only colour names a rebuilt surface may consume.
 * A surface naming a raw ink has forked the grade. Roles are assigned
 * per the decided grade: Grade Book §1.2 (intent per substance) and
 * §1.3 (the relational map: what leads, recedes, is withheld, per
 * state — the per-state behaviour itself is [StrongroomLight]'s law,
 * not a second set of tokens).
 */
object StrongroomRoles {

    /** The door's ground: deep-violet enamelled steel. */
    val Ground = StrongroomInks.Violet

    /** The room around the door: graphite mass (Grade Book §1.3 —
     *  graphite LEADS as room & steel mass in L-SIGN and L-SEALED). */
    val Field = StrongroomInks.Graphite

    /** The printings' dark line-work on the violet ground (Ledger E-8). */
    val LineWork = StrongroomInks.Graphite

    /** Exposed metal — case, great wheel, dial, bolts: graphite and
     *  no other dark (Grade Book §SW P-5). */
    val Metal = StrongroomInks.Graphite

    /** Gold leaf + pinstripe, laid on the enamel (Ledger E-7). */
    val Leaf = StrongroomInks.Gold

    /** The brass day plate: the gold ink ALONE (Grade Book §1.5). */
    val Plate = StrongroomInks.Gold

    /** Lead's containment: dullness inside the graphite depths; lead
     *  has no hue of its own (Grade Book §SW P-7). */
    val Lead = StrongroomInks.Graphite

    /** Warmth — RELIGHT's lit partner only. Never a surface colour. */
    val WarmthAsLight = StrongroomInks.Orange

    /** Mark inks, spent at mark scale only, on the graphite ground,
     *  where a settled element carries them (Grade Book §1.3). */
    val MarkMagenta = StrongroomInks.Magenta
    val MarkRose = StrongroomInks.Rose
    val MarkBlue = StrongroomInks.Blue

    /** Words on the ground: the gold leaf ink is the face's reading
     *  voice against both dark grounds — the only ink of the seven
     *  that carries printings against violet and graphite alike
     *  (Grade Book §1.4 CT-3/CT-8 orderings). */
    val OnGround = StrongroomInks.Gold

    /** Words on the leaf/plate: graphite line-work on gold
     *  (Grade Book §1.3: gold × graphite PERMITTED — "the record's
     *  contrast backbone"). */
    val OnLeaf = StrongroomInks.Graphite
}

/** The never-green law, checked — not asserted — over the census. */
object StrongroomInkLaw {

    /** True iff [color]'s green channel sits below both its red and
     *  its blue maxima line: green < max(red, blue). Every one of the
     *  seven inks satisfies it (verified per ink in the record). */
    fun isNeverGreen(color: Color): Boolean =
        color.green < maxOf(color.red, color.blue)

    init {
        check(StrongroomInks.All.size == 7) { "the palette is seven inks, no more" }
        check(StrongroomInks.All.all { isNeverGreen(it) }) {
            "never green: an ink in the census leans green"
        }
        // The void forgery must never enter the census (Grade Book C-GB-1).
        check(StrongroomInks.All.none { it == Color(0xFF377E4B) }) {
            "#377E4B is void (Grade Book C-GB-1) and may not stand in the palette"
        }
    }
}
