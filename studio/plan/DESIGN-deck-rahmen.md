# THE DECK — COMPOSITION DESIGN
## RAHMEN, Designer, issue #6 cell (lead: Rost) · Fri Oct 30, 2026
## Source of record: `studio/plan/AUDIT-deck.md` (§1 job, §8 alternative, §9 F1–F22)
## Status: DESIGN ONLY. No code. Nobody compiles.

My one job: the Deck screen's **composition** — what stands where, in
which state, at what size and volume — for the audit's winning
direction:

> **One Question Per State (Plan §7) + The Fusion (Vega A).**
> The Deck asks exactly one question per state, and the dial
> becomes its own clock.

I do not reopen any audit verdict. Where the audit left a form open
("form to beat", address, choreography, compression on short phones),
I close it here and say so. Where I invent, the invention is marked
**NEW** with its cost, so Rost can strike it without touching the rest.

---

## 0 · THE TOP STRIP — NOT MINE, DESIGNED AROUND (issue #4's cell)

The top strip belongs to issue #4's cell. I do not redesign it — not
its height, contents, words, or behaviour. This composition treats it
exactly as it treats the header band and the rail: **host chrome.**

Four consumption rules, binding on everything below:

1. **Variable, not constant.** The strip's height enters my budget as
   `S` (unknown, owned elsewhere). Every Deck figure below is stated
   for the Deck-owned column only, so the composition balances at any
   `S`. If `S` grows, the Deck does **not** grow a compensating element.
2. **Zero duplication.** If the strip prints blocking health, the Deck
   prints it zero additional times in health (the drawer law, §4).
   If it does not, the Deck still grows no standing BLOCK cell —
   BLOCK is state-only on this face either way.
3. **No visual pairing.** No Deck element aligns to, echoes, frames,
   or colour-matches the strip. Cover the strip with a thumb and the
   Deck must read identically — that is an acceptance test (§9, T1),
   not a metaphor.
4. **First Deck pixel = the instrument.** The Deck's scroll column
   opens on the wheel. There is no Deck-owned title, greeting, date,
   or summary above it. Words only where the object cannot speak —
   and the wheel speaks first.

---

## 1 · THE GOVERNING NUMBERS

Reference phone: 740dp tall (the audit's measure). Deck-owned zones:

| Zone | Today (audit §2/§4) | This composition |
|---|---|---|
| Pinned stack, permanent | ≈216.5dp (strip 53 + slide 72 + sentence slot 22 reserved-empty + imprint ≈39.5 + spacing) | **≈120dp** — slide zone 72 + imprint ≈39.5 + one 8dp breath |
| Pinned, conditional | — (all of it is always on) | Refusal sentence ≈22dp, **fault only**, above the slide (§5) |
| Scroll column | ≈1.23 screens at idle; the instrument card does not fit its own window | Idle fits whole, no scroll (§3, IDLE) |
| Hero | Card + 72sp glyph printed 3× + wheel 288dp + track + % | **No card.** The wheel alone, 288dp, hub = the numeral (§2) |

**The ≈120dp returned by the pinned collapse (F16) is spent in this
order, and on nothing else:**

1. The wheel keeps its full **288dp**. The 288→230 shrink stays held
   as the next lever, unspent (audit §3, Wheel).
2. Idle fits on one screen with no scroll.
3. Silence between objects (§6 spacing). Returned dp is **air**, not
   furniture. Any future element asking for permanent pinned tenancy
   must name which of 1–3 it takes the dp from, in its own filing.

**Truth guard on the collapse:** a smaller pinned stack must never
become a hidden-fault stack. Fault is the one state allowed to *grow*
the pinned zone (§5). Dens's law, kept: fault gets more room than health.

---

## 2 · THE FUSION — THE DIAL IS ITS OWN CLOCK (F18)

The hero panel dies. There is no card, no eyebrow line, no separate
readout row, no progress track, no `%`, and no `progressFraction`
derivation left composable. If any of those five still exists after
this lands, the Fusion has not landed.

### 2.1 One object, one address, three lives

The wheel stands on the ground — no card under it, no lift claimed,
no shadow owed (F11 becomes moot after the Fusion; it is an interim
cure for the current room only). Its hub is the readout, at **one
fixed centre**, in **one glyph family (Sora, display)**, across:

| Life | Hub primary | Hub sub-line (Anonymous Pro, quiet) | Ring |
|---|---|---|---|
| **IDLE** | True span, breaks priced in — `1H 50M` | The recipe remainder: `3 × 50` and, when blocks > 1, `+10 BREAK` | Ticks + numerals, at rest |
| **SEALING** | The span as dialled, unchanged | Unchanged | Locks at its seat. No swap, no hand-over |
| **RUNNING** | Ticking readout `MM:SS`, same size class as the idle span | `ENDS HH:MM` (stored end only) moves to the foot line, §3 | **Rim ink** = progress (§2.2) |
| **BREAK** | The break countdown — the largest print answers the state's one question | `BACK AT HH:MM` | Rim ink continues on the session, not the break |
| **GRACE (≤10s)** | The grace countdown | — | Unchanged |

- **Δ-address = 0** across the seal and across every state change:
  wheel centre, hub glyph centre, and slide address do not move.
  The eye never re-finds its clock. This is a composition law, not
  a nicety.
- **The hub gesture stops being secret.** The hub still cycles block
  count, but the count is now printed in the hub's own sub-line
  (`3 × 50`) — what the hub hides today, the hub's face declares.
- **FLOW:** hub primary reads `FLOW`; running, the hub counts **up**.
  There is no end, so there is no denominator (§2.2). Foot line
  reads `NO END`. The mode is printed at the instrument, never
  hidden in a number range, and never a second door — the FLOW
  key-cap is struck as a control (F2); this print is its only successor.
- **Single-press (F10):** one tokenised shadow ghost, one direction.
  Ghosts **OFF** entirely on FLOW and on any ticking readout — a press
  does not strike a moving target, and 1 AM eyes read ghosts as blur.
  Fallback if it still reads soft on device: kill both ghosts.
- **Burn-in:** a ticking hub standing for hours inherits the AOD
  clock's shift discipline. The *requirement* is mine; the mechanism
  is the builder's to confirm view-side.

### 2.2 Rim ink — the only progress (F3, restated for the Fusion)

- Progress is ink filling the wheel's **tick ring**, in rotational
  order, elapsed / target, **filling**. No floors, no inversion,
  no invented denominator.
- **Timed:** the ink's leading edge is the elapsed fraction of the
  stored target. What is printed is what has happened.
- **FLOW:** the ring **never completes and never fills toward
  anything** — no rim progress at all. A ring that cannot complete
  must not pretend to approach completion. The ticking-up hub is
  the whole readout.
- No other progress rendering exists on the face. One fact, one rendering.

### 2.3 **NEW — the ink's leading edge is the only moving mark**

Cost: zero new elements. At the rim ink's leading edge, the single
tick it currently occupies renders at full hero ink; every tick
behind it at instrument ink, every tick ahead at rest ink. Reason:
at a glance — and at 1 AM — he should find *where the session is*
without reading the hub at all; position is the statement, the hub
is the precision. This adds no element, no colour, no motion of its
own (it moves only because elapsed time moves). If Rost reads it as
a second progress rendering, strike this paragraph — §2.2 stands
without it.

---

## 3 · ONE QUESTION PER STATE — THE FIVE FRAMES

Deck-owned elements only, top → bottom. Host chrome (§0) sits outside
every frame. Fonts in every frame: **Sora** for display, **Atkinson
Hyperlegible** for body/title, **Anonymous Pro** for every number,
recipe, and telemetry print. **No green or near-green, ever.**
Hand-built only; no AI imagery anywhere in this composition.

### IDLE — *"How long, and into what?"*

Scroll column (must fit whole at 740dp — an acceptance test, §9 T2):

1. **The instrument** (§2): wheel 288dp, hub = span + recipe.
2. **Foot line** — centred, Anonymous Pro 11sp, quiet ink, one line:
   `ENDS 14:30 · PLANNED` for a timed set (PLANNED is F21's cure for
   the sliding idle ENDS — a plan is labelled a plan), or `NO END`
   for FLOW.
3. **Ghost task title** (§4.3): one line, read-only.
4. *Nothing else.* No utilities, no strip cells, no sentence, no totals.

Pinned: **slide** (72dp) + **imprint**. The imprint line beneath the
slide is a shared tenancy (§5): LIVE wire → the seal's promise;
non-LIVE wire → the refusal sentence takes that same line instead.
A healthy wire is silence: the drawer is shut (§4) and BLOCK is
printed nowhere on this face.

### PROVING — *"Is it holding?"*

1. Instrument, **frozen exactly as set** — wheel locked, hub span as
   dialled. Nothing starts a readout on an unproven seal.
2. **Proof drawer OPEN in the ghost title's room** (§4) — displaced,
   not stacked: the title leaves, the drawer takes its room, the
   title returns at settle.
3. The drawer holds for the proving seconds **plus one settle beat**
   after success, then collapses to 0dp. Proof gets the whole stage
   for exactly the seconds it is the question — not one frame longer.

Pinned: slide still at its address (Δy = 0); imprint latched from the press.

### JAMMED / FAULT — *"Is it holding?" (answer: not proven)*

As PROVING, with the hierarchy deliberately inverted (§5):

- The jam/refusal sentence prints **inside the drawer**.
- If the fault survives into idle (drawer shut, wire still not LIVE),
  the sentence is **promoted above the slide** in the pinned stack —
  the pinned zone grows by ≈22dp, in fault only. The growth *is* the
  alarm; a fault that costs no room reads as decoration.
- Instrument stays frozen. Fault is the loudest Deck state by design;
  health is the quietest. That is the hierarchy working.

### RUNNING — *"How long left, and what's in the way?"*

1. **The instrument, sealed in place** (§2): hub = ticking readout,
   rim ink = progress. No track, no `%`, anywhere.
2. **Foot line:** `ENDS HH:MM` from the stored end only — running,
   it is a fact, so no PLANNED label.
3. **Ghost title + DONE** (§4.3) — DONE keeps its key-cap body.
4. **Utilities row** (§4.2): DESK CLOCK · HELD — settled-running only.

Pinned: the **STOP chair** at the slide's address, full 72dp (§4.4);
imprint as the foot — tenant struck at full alpha, carrying the app
count's only standing rendering on the Deck.

### BREAK — a running sub-state: *"How long is my break?"*

The audit's 1 AM test convicts the current form: the break's length
is the smallest print on the card while the hero answers a different
question. Here:

1. **The hub IS the break** — hub = break countdown (§2.1).
2. Foot line: `BACK AT HH:MM`.
3. **END BREAK** chip, full width, in the utilities' room (its
   existing seating is correct; it stays).
4. Everything else as RUNNING. The grace countdown likewise lives
   in the hub (§2.1) — a countdown belongs to the instrument, never
   to a chip badge.

### FINISH-DETAINED / RECEIPT — KEEP as now (both acquitted, audit §8)

Held last frame; the held instrument is the wheel-as-clock, frozen.
Receipt slip dismissible by slip-swipe or PARK/rail only — a stray
scrim tap shakes the slip, never kills it (F19, **S**).

### ROUTINE-BORN sessions

Born RUNNING, and must not be indistinguishable from a hand-armed
session (audit §6). With the eyebrow dead, the name takes the foot
line's first position for its whole life, one line, quiet ink,
Anonymous Pro 10sp: `SLEEP · ROUTINE — ENDS HH:MM`. No badge, no
second rendering.

---

## 4 · THE SUPPORTING ELEMENTS

### 4.1 The proof drawer (F16) — the strip's successor, state-gated

The standing proof band **dies as a permanent object**. Its facts
split by owner — the split is the design:

| Fact | Standing home in this composition |
|---|---|
| TIME / ENDS | The Deck's foot line (§3), all states. Never in the drawer |
| App count | The imprint tenant — its only standing rendering. The current twice-print ≈100dp apart dies strip-side |
| BLOCK | **State-only:** the drawer, while proving / jammed / idle-fault. In health: printed zero times on the Deck face |

The drawer itself: the strip's three cells + seam, in one frame,
occupying the ghost title's room in the scroll column. It exists
**only** in PROVING / JAMMED / idle-fault and for one settle beat.
It never takes pinned tenancy and never reserves height while shut —
**a shut drawer is 0dp, not a reserved slot.** Seam stamps do not
come with it: they move to PARK (F8, §4.5).

*Fallback, recorded not ruled:* if the drawer is refused at merge,
Dens's Two-Height Strip is the priced floor. It is not this design.

### 4.2 Utilities row — KEEP, state-gated, quietened (F12)

Settled-running only; the gating was acquitted by every seat.

- HELD learns its noun, printed once: `HELD · n NOTIFICATIONS`.
  A bare count beside a bare word is telemetry, not print.
- Night fill steps **down** (token, never a raw hex — §6): the chips
  must never outshine the instrument they serve.

### 4.3 The task line → a ghost (F7, **M**)

- **Idle:** active task's title only. Read-only, one line, Atkinson
  Hyperlegible 15sp, quiet ink, ellipsis on overflow. It answers
  *"what am I sealing myself into?"* — part of the set, so it earns
  home tenancy. No verb, no key-cap body, no count, no `NOW` shout.
- **Running:** title at full ink (it is now the live fact) + DONE in
  its key-cap body — a verb in a verb's body was right.
- **Struck in all states:** the sub-step expansion and the `QUEUE n`
  print + its secret tap-door — both move to Tasks. The rail is the
  door; a count whose door is elsewhere is telemetry.

### 4.4 The slide and the STOP chair (F6, **M**)

- **The slide: KEEP, promoted** — the Deck's one verb. 72dp zone,
  pinned, same address in every state it holds. Its words **learn
  state**: at 90% travel the print must no longer instruct him to
  start sliding. Print that cannot see the hand is wallpaper.
- **The chair: the grace/valve swap is STRUCK.** One chair at the
  slide's address, one verb, the whole session: **STOP**.
  - Inside 10s: the tap is free and instant.
  - After: the same tap opens the valve challenge, and the chair's
    own words carry the price — form to beat:
    `STOP — USES YOUR 1 BREAK THIS WEEK`. The price is printed on
    the chair itself, never only in an overlay.
  - No label may describe a gesture the element does not perform —
    the `(60S HOLD)` print dies with the swap.
  - The chair **fills its full 72dp zone** in every running state.
    A smaller chip floating in a 72dp zone is a height lie the eye
    reads as unfinished.
  - Grace countdown: in the hub (§2.1), never on the chair.

### 4.5 Seam stamps — STRIKE from the Deck (F8, **S**)

`BIND HELD · THIS VISIT / PROOF: BIND ONLY` is the studio talking to
itself in public. Both move to PARK with the warrant's detail. They
leave **no forwarding print** on the Deck — PARK's door already
stands in the host chrome, and a forwarding note would be a new
rendering of a fact the door owns.

### 4.6 The imprint — KEEP, tenant and all

The seal's fixed record, and the app count's standing home (§4.1).
Double rule, ≈39.5dp — the pinned stack's floor; it does not shrink
to fund anything else. Tenant ink at **full alpha when struck**.
One amendment: at idle its line is shared tenancy with the refusal
sentence (§5) — either/or, never stacked.

---

## 5 · THE REFUSAL SENTENCE — RANK, NOT JUST WORDS (F9 + F16)

Wording is F9's (plain words; the misrouted FIX branch corrected to
the pane that owns the lack — form to beat:
`NOT SURE BLOCKING IS ON — TAP TO CHECK AGAIN`). **Placement is mine:**

1. **Live jam:** inside the drawer (§3, JAMMED).
2. **Idle, non-LIVE wire:** it takes the imprint line's tenancy
   beneath the slide (§3, IDLE) — one line, two possible tenants,
   never both, never a second reserved slot.
3. **Fault standing at idle:** promoted to **first element of the
   pinned stack, above the slide** (Muede, adopted by the audit).
   The pinned zone grows ≈22dp, in fault only.

The old standing reservation (22dp held empty in health) dies: the
mercy it bought (no jump when it fires) is preserved by rule 2 —
the line it lands on is already occupied by a tenant of the same height.

---

## 6 · HIERARCHY — ONE HERO, EVERYTHING ELSE ≥40% QUIETER

The finish law, made measurable. **Brightness and size order must
equal importance order in every frame:**

```
hub glyph  >  instrument (ring/ticks)  >  controls (slide, DONE, chips)  >  ground
```

**The one exception is fault** (§3/§5), where drawer + sentence
deliberately outrank the instrument. That inversion is the design.

How "≥40% quieter" is enforced, element by element:

| Tier | Elements | Quieting mechanism |
|---|---|---|
| **Hero (100%)** | Hub glyph only — span, readout, break countdown | Largest type on the face (Sora display, hub size class), brightest ink token, single-press ghost (§2.1) |
| **Instrument (≤60%)** | Tick ring, rim numerals, rim ink | Numerals in the secondary voice at full opacity of *their* token — never a percentage of the hero's ink, never the hero's size class |
| **Controls (≤60%)** | Slide, STOP chair, DONE key-cap, utilities chips | Control ink/fill tokens only; night chip fill steps down (§4.2); key-cap radius registered **12dp** (F13 — two radii app-wide on this screen, no third) |
| **Print (≤60%)** | Foot line, ghost title (idle), recipe sub-line | 10–11sp Anonymous Pro / quiet ink; the ghost title steps to full ink only when running makes it the live fact |
| **Ground** | Everything else | No element may carry a colour that cannot be named as one of the four ranks above — that naming is the token vocabulary the F17 sweep must produce for this screen (heroGlyph / instrument / control / ground, per scheme, + the single ghost token) |

- **Vertigo (F15, M) moves to the ground:** at the seal the *page*
  recedes — ground scale 0.98, blur peak 3dp, same curves, same
  1050ms — and the instrument seals **in focus**. Under the Fusion:
  ghost title, utilities, and ground recede; the wheel never blurs,
  never scales. A shot that blurs the thing being sealed is a
  gimmick; a shot that stills the room around it is a ceremony.
- **Acceptance instrument:** Schein's **Lamp Test** — three
  pure-luminance frames (idle / sealed / fault). Idle and sealed must
  read as a monotonic staircase in the order above; fault must show
  drawer/sentence on top. Tonight's Deck fails it at the sealed
  frame (audit §8); this composition is built to pass it.

---

## 7 · SPACING, THE SCROLL EDGE, AND SHORT PHONES

- **Group by silence (F14, S):** spacing re-priced tighter *inside*
  the instrument object, **20dp between objects**. Proximity — not
  rules, boxes, or labels — says what belongs together.
- **The scroll's bottom edge fades:** content retires under a fade,
  never guillotined against the pinned stack. Post-eviction, idle
  should not scroll at all (§3); the fade governs the states that
  still can (drawer open, expanded running content).
- **NEW — the compression ladder for short phones.** Cost: layout
  rules only; no element changes. At 740dp the idle frame fits with
  air (§1). Below that, height is surrendered in this exact order,
  and the wheel is touched **last, never first**:
  1. Inter-object spacing 20dp → 12dp.
  2. The reserve air above the pinned stack → 0 (idle may scroll;
     the fade, above, governs the edge).
  3. The ghost title truncates to its one line (already its form) —
     it never wraps to two.
  4. Only if the instrument still clips: the held 288→230 shrink
     (audit §3) — and that landing must be reported as the lever
     being spent, not slipped in.

---

## 8 · SEQUENCING — WHAT LANDS WITH WHAT

1. **Landable now, on the current room, independent:** the recipe
   print interim (F4/F5 — the eyebrow's transitional tenancy, struck
   when the Fusion lands), true progress interim (F3), single-press
   (F10), utilities noun + night ink (F12), radii (F13), spacing/fade
   (F14), seam stamps out (F8), receipt dismissal (F19), sentence
   words + FIX routing (F9).
2. **The skeleton, together: F16 (§1/§4.1/§5) + F18 (§2).** The drawer
   and the Fusion are one composition; landing either alone leaves a
   room designed for the other.
3. **F17's token sweep precedes any look touching this screen**;
   §6 fixes the token vocabulary it must produce.
4. **Held, not spent:** the 288→230 shrink (§7, rung 4), Dens's
   Two-Height Strip (§4.1 fallback), kill-both ghosts (§2.1 fallback).
5. **Phase 2, not in this design:** Vega B (proof inside the slide)
   and Muede's 05:00 door — separate lanes, Ren's separate call.
   Nothing here pre-builds for them.

**Standing notes for the builder:**

- **No Deck element may print a state it has not verified.** The
  three places this bites: foot-line `PLANNED` vs stored-end (§3),
  BLOCK's silence in health (§4.1), FLOW's absent denominator (§2.2).
  If a derivation cannot be made truthful, the print is struck,
  not floored.
- **F22 (accessibility)** is the builder's bar debt, but composition
  must not make it harder: the Fusion concentrates the Deck's primary
  control *and* its primary readout in one view. That view's semantics
  (name, value, action) are a single, larger obligation — and the
  hub's printed span/readout is the value those semantics must speak.
- All work closes DECLARED. Nobody compiles.

---

## 9 · ACCEPTANCE TESTS (composition-level, render-checkable)

- **T1 · Cover-the-strip:** strip masked → every frame in §3 reads
  identically; no Deck fact is lost, none was duplicated.
- **T2 · Idle fits:** at 740dp, IDLE renders whole with no scroll
  and the wheel at full 288dp.
- **T3 · One numeral:** no frame contains both a hero numeral and a
  hub numeral — there is only ever the hub.
- **T4 · Δy = 0:** wheel centre, hub centre, slide address identical
  across IDLE → SEALING → RUNNING frames.
- **T5 · Healthy silence:** in health, BLOCK is printed zero times
  on the Deck face; the drawer occupies 0dp.
- **T6 · Fault grows:** JAMMED/FAULT pinned height > IDLE pinned
  height, by the sentence's ≈22dp, and the sentence stands above
  the slide.
- **T7 · Lamp Test:** §6's three luminance frames pass as specified.
- **T8 · 1 AM break:** in BREAK, the largest print on the face is
  the break countdown, and `BACK AT HH:MM` is on the same frame.
- **T9 · FLOW honesty:** in FLOW, no progress arc, bar, or `%`
  exists in any frame; the hub counts up; the foot line reads `NO END`.
- **T10 · Short phone:** at the smallest supported height, the §7
  ladder's rungs are consumed in order; the wheel is still 288dp
  unless rung 4 is explicitly reported as spent.

---

*What I want to do next: the same composition treatment, at this
depth, for the drawer's interior — the three cells' internal order,
weights, and the settle-beat choreography — once Rost rules whether
the drawer (§4.1) or Dens's fallback stands.*
*What I could do unassigned: a Lamp Test frame set spec for the
Deck (exact luminance sampling points per frame) so the finisher
can run §9 T7 mechanically instead of by eye.*

— RAHMEN, Designer, issue #6 cell
