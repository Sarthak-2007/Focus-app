# DESIGN — THE HELD DOOR: FIXED SKELETON (layout + interaction)

**Designer:** Schwelle · **Cell:** issue #11, lead Tor · **Branch:** `agent/issue-11`
**Ground:** `studio/plan/AUDIT-held-door.md` FIX-6 / FIX-7 / FIX-8, Conflicts 1·2·6;
Held Door Truth Baseline. **Scope:** spatial + interaction design only — zones,
thumb travel, what moves and what never moves. No machinery.
**Status:** DECLARED, uncompiled — paper design, nobody compiled (standing law).

---

## 1. THE BRIEF IN ONE LINE

> **Under panic, nothing under the thumb may move.**

The defect this skeleton kills (audit F-2, the audit's worst interaction
defect): in the old face the peek zone was composed only when it armed. At
countdown 0 the card grew ≈110dp and **RETURN jumped 55dp — more than its
own height — under a thumb already travelling toward it, landing the thumb
on the peek chip**. A man reaching for the safe act was handed the spending
act. Every rule below exists to make that geometry impossible, not unlikely.

## 2. THE SKELETON LAW

1. **Every slot is composed at frame one, at its final bounds, for the whole
   visit.** Countdown state may change a slot's *contents* (crossfade in
   place) — never its bounds, never a sibling's bounds.
2. **No collapsing spacing.** The card column carries no `spacedBy`;
   collapsing gaps around absent children were the original defect's
   mechanism. All spacing is explicit, fixed slots and gaps.
3. **The card is bottom-anchored.** Where a zone is lawfully absent
   (never-owed doors, §5), the card grows *upward* into empty night.
   RETURN's address is defined in **screen space**, not card space,
   because the thumb lives in screen space.
4. **RETURN's bounds at wall-max, wall−1, wall-0 and offer-armed are
   pixel-identical** — and asserted so by test (§9), not by intention.

## 3. THE SLOT STACK (top → bottom, exact)

Card: full width inside 20dp screen padding, 24dp corner radius, **24dp
padding on all sides**, 1dp border at alpha 0.38.

| Slot | Height | Content | Type |
|---|---|---|---|
| H1 · NAME | 32dp | What he opened — the face's one hero. 1 line, ellipsis. | Sora Bold 24sp |
| gap | 4dp | — | — |
| H2 · INTENTION | 24dp | His words, bare, directly under the name. No prefix. Blank → slot stands empty, prints nothing. | Atkinson Hyperlegible 16sp, alpha 0.92 |
| gap | 4dp | — | — |
| H3 · SESSION BLOCK | 40dp | Line 1 (20dp): session line — end instant only, never a remaining duration. Line 2 (20dp): break appointment on peek-tier doors, or the site door's matched domain. | Atkinson 12.5sp, alpha 0.92 |
| gap | 12dp | — | — |
| **Z · PEEK ZONE** | **110dp or absent** | §4. Present on every peek-owing door (offer *and* withheld). Absent — not blanked — where no peek was ever owed (Baseline law 4). | — |
| gap (peek-owing only) | 16dp | — | — |
| **R · RETURN** | **56dp slab** | "RETURN TO FOCUS" — the face's only accent fill. | §7 |
| gap | 12dp | — | — |
| X · HONEST EXIT | 48dp touch row | "END THE SESSION INSTEAD" — bare text, a route, not a button. | Atkinson Bold 12.5sp, alpha 0.92 |
| gap | 8dp | — | — |
| F · FOOTER BADGE | 16dp | The badge word alone ("PEEK" on peek-tier doors). The eyebrow "AIRLOCK INTERCEPTION •" is dead. | Badge style |

**Card totals:** peek-owing **430dp**; never-owed **304dp** (430 − 110 − 16).
The 126dp difference is absorbed entirely above RETURN by the bottom anchor.

**RETURN's screen-space address (the number the whole design protects):**
its centre sits **136dp above the card's bottom edge** on every branch and
every visit — 24 padding + 16 footer + 8 gap + 48 exit + 12 gap + 28
half-slab. The stack *below* RETURN never changes height, so the address
cannot change.

## 4. ZONE Z — THE PEEK ZONE (110dp, fixed)

Composed from frame one at full height on every peek-owing door. Internal
stack: **Z1 48 + gap 6 + Z2 34 + gap 4 + Z3 18 = 110dp exactly.**

- **Z1 · ACTION (48dp)** — one slot, five lawful tenants, crossfading
  *inside unchanged bounds* (180ms, specA):
  - **GATE** (counting): the wall demoted to a captioned gate line —
    "PEEK OPENS IN 0:0X", Anonymous Pro Bold 15sp, tabular figures —
    over Schwelle's **draining 2dp rule**. The numeral is dead as hero;
    it survives only as this caption, naming what it gates. It never
    pretended to gate the stay, and now it doesn't look like it does.
  - **CHIP** (armed): "PEEK" + derived badge (honest m+s — 90s prints
    "1m30s", never "1m"). The words "EMERGENCY PEEK" are dead.
  - **WITHHELD**: state *and* reason, printed from **second 0** — a known
    refusal is never withheld behind the wall. Tenants: cooldown print
    (derived, ceiling form; "AGAIN IN 0 MIN" is unrepresentable),
    "A PEEK IS ALREADY RUNNING", "PEEK IS OFF". A homecoming is greeted
    first: "THE PEEK ENDED".
  - **RESOLVING**: the derived-0 instant between cooldown and offer —
    an empty frame inside fixed bounds, never a growth.
  - **EMPTY**: nothing owed in this slot; the slot still stands.
- **Z2 · PRICE (34dp, 2 lines max)** — the re-timed price sentence:
  "WHEN THE PEEK ENDS, A {n} MINUTES COOLDOWN STARTS ON THIS APP".
  Standing from frame one on the offer branch — *during* counting, at
  offer, and through the hold. The price is read before the temptation
  exists. Withheld branch: empty-held (the slot stands, prints nothing).
- **Z3 · MEMORY (18dp)** — the taper's count in the gate line's family
  ("THIRD TIME TONIGHT"): the wall is 5 + this session's booked
  intercepts of this package, capped at 10, and the count prints — the
  rule is never silent about itself. Empty-held when there is no count;
  a homecoming prints none (a lease's own expiry is not a reach).

## 5. THE THREE BRANCHES

| | Offer | Withheld | Never-owed |
|---|---|---|---|
| Zone Z | 110dp, GATE→CHIP | 110dp, WITHHELD tenant | **absent** |
| Price (Z2) | from frame one | empty-held | — |
| RETURN centre | 136dp above card bottom | 136dp | 136dp |
| Card height | 430dp | 430dp | 304dp |

Withheld doors keep the full 110dp zone (Tor's ratification, against the
audit's first 34dp sketch): the FIX-5c resolution crossfades inside Z1 —
if withheld were shorter, the resolution would grow the zone and RETURN
would move on exactly the doors where the man has already waited out a
cooldown. One zone height for every peek-owing door, no exceptions.

## 6. THUMB TRAVEL — THE MAP

The visit is a panic visit. The thumb arrives at the bottom third of the
screen, already moving, aimed from muscle memory of the *last* visit.

- **RETURN is the closest act.** Bottom-anchored, centre 136dp above the
  card's bottom edge, 56dp tall, full width. Zero search, zero travel
  correction, live and tappable **from frame one in every state** —
  never gated, never dimmed, never re-labelled. The wall gates the peek
  offer's appearance only; it never gates RETURN, never the withheld lines.
- **PEEK is deliberately farther and heavier.** Chip centre sits **130dp
  above RETURN's centre** (28 half-slab + 16 gap + 18 Z3 + 4 gap + 34 Z2
  + 6 gap + 24 half-Z1) — a deliberate upward journey, *and* a different
  gesture (§7). Even a thumb that travels cannot land on it by accident:
  at the arm instant the chip fades in **in the slot the gate line already
  occupied** — the eye has been watching that exact rectangle drain for
  the whole wall. Nothing appears anywhere the eye wasn't already looking,
  and nothing appears under the thumb at all.
- **END is farthest in weight, not distance.** Directly under RETURN —
  48dp row, bare text, no fill. Reachable in one short drop, but it
  *routes* to the Disarm face; the gravity lives there, not here.

## 7. THREE ACTS, THREE WEIGHTS

Each act's gesture is priced proportional to what the act spends:

| Act | Gesture | Spends |
|---|---|---|
| RETURN TO FOCUS | **Tap** on the 56dp slab | Nothing. The free act is the easy act. |
| PEEK | **600ms hold-to-fill**; release early drains the fill in 120ms and books nothing | A lease + the cooldown that starts when it ends. A travelling thumb cannot spend it. |
| END THE SESSION INSTEAD | Tap → routes to Disarm's continuous **60-second hold** valve | The session. The door itself ends nothing, spends nothing, books nothing. |

RETURN's ink: label in the scheme's own on-container **#471602 on the
container fill — 5.37:1**, the highest-stakes word on the face carrying
the face's strongest contrast (the old cream-on-vermilion 2.55:1, the
lowest contrast on the face, is dead). Body lines at alpha **0.92**
(was 0.80). Border alpha **0.38** (was 0.20). Wrapped lines centre-aligned.
Night palette kept at all hours (Conflicts 3): the sealed app is night on
every surface; the door matches the building it locks.

## 8. WHAT MOVES / WHAT NEVER MOVES

**Never moves — not at countdown 0, not at resolution, not ever:**
RETURN's bounds · the honest exit row · the footer · every slot height ·
every gap · the name · the intention · the session block · zone Z's height
on peek-owing doors · the card's bottom edge.

**May move — only inside fixed bounds, never a layout change:**
the gate rule's drain width (inside its 2dp track, redrawn at 50ms so it
drains smoothly, while printed seconds *derive* from the anchored end —
the ticker is a redraw trigger, never a counter) · Z1 tenant crossfades
(180ms) · the price's fade-in · the chip's hold-fill · the countdown's
digits (tabular, zero width jitter).

**Arrival / exit:** entry fade 180ms (specA) over the fully-composed
skeleton — no ghost, no growth-in; exit 260ms. Arrival sound at door
pitch (300Hz, 44ms, gain 0.60). TalkBack: one polite live region,
announcing at door-open and at offer-arm only — never per-second,
never assertive.

## 9. ACCEPTANCE — HOW THIS DESIGN IS PROVEN

1. **Stationarity test:** RETURN's pixel bounds asserted equal across
   wall-max, wall−1, wall-0, offer-armed, withheld, and resolving frames —
   including the three variable-content frames (memory line present,
   errand sub-line present, homecoming greeting present). Any frame that
   moves RETURN by 1px fails.
2. **No-growth audit:** no `spacedBy` in the card column; every spacer a
   fixed dp literal from the §3 stack; zone Z height literal 110dp on
   peek-owing doors, absent otherwise.
3. **Second-0 check:** withheld lines and the price sentence are composed
   in the first frame; only the chip waits for the wall.
4. **Dead-words check:** "EMERGENCY PEEK" has zero hits on this face;
   the footer prints the badge word alone.
5. **Contrast check:** RETURN label ≥ 5.37:1; body alpha 0.92; border 0.38.

## 10. PROPOSALS (designer's, priced — not rulings)

- **The skeleton law as a cross-face pattern (SMALL):** any face that can
  appear under a travelling thumb (Disarm first — it shares this card)
  gets the same treatment: fixed slots, bottom anchor, a stationarity
  test. Tor's audit NEXT (b) already carries this to the other
  coordinators; this file is the reusable spec for it.
- **Panic-frame screenshot pair (SMALL):** two rendered frames — wall−1
  and offer-armed — diffed in review. The 55dp jump was invisible in code
  review for a whole audit cycle; a pixel diff makes the class of defect
  impossible to re-ship quietly.
- **Filed, not re-proposed:** Schwelle's knock at HOLD 00 and Schliff's
  per-second detent already sit on Ton's bench (audit, Proposals
  Disposed). The Leaf — the full edge-to-edge recomposition — remains
  Ren's call; this skeleton is adopted either way and does not wait for it.

---

*Next from Schwelle: defend this stack against Schliff's finish pass and
the stationarity test's frame list; if the 110dp zone's price line ever
needs a third line on device, the cure is a taller Z2 inside a re-totalled
stack — never a wrap that pushes RETURN.*

— Schwelle, designer, #11 Held Door cell
