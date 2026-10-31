# PRISMA — CRAZY IDEAS, ISSUE #9 (PARK)

*Seat: Prisma, crazy-ideas (second seat on this issue). Angle, by brief:
the Guard line and the counted four doors as a **visible instrument**,
and **NOT CHECKED as a designed state** rather than a blank.
Komet (first crazy seat) owns the four silences — proof in the dark,
CHECK as ritual, cold-open biography, waiting-as-signal. Nothing below
touches his ground: no night log, no hold-to-check, no confession line,
no sweep sound. This file is the opposite wall: **what the board LOOKS
like while it tells the truth.***

*Ground read before writing: issue #9 + all comments (Hof's cell close,
Vogt's wave-2, Zahn's charge sheet, Komet's opening), AUDIT-park
(P-1…P-8, §3 ranked doors, §4 merge list), the landed board on
`agent/issue-9` (CHECK key, `GUARD — NOT CHECKED` unblankable,
`GUARD {n}/4` headline, signed foot line, age column, STALE half-ink,
tier eyebrows). The sane work is done. None of it is re-proposed here.*

*Hard laws honoured throughout: fonts Sora / Atkinson Hyperlegible /
Anonymous Pro only; never green; no AI-generated images (every visual
below is typography, rules, and deterministic Compose drawing); an
unproved door can be made beautiful, **never made to look proved** —
each idea states which side of that line it sits on and what stops it
crossing. Nobody compiled anything. Every idea: one-line mechanism +
what it costs.*

---

## 0 · THE THESIS IN ONE BREATH

Today the Guard state is a **sentence**: `GUARD 3/4 · UNPROVEN`.
Sentences get skimmed, misread, and — at 1 AM — read as fine.
An **instrument** cannot be skimmed: a gauge, a level, a needle on a
stop is read by the eye before the brain parses a word, and it is
read *wrong-proof* — you cannot accidentally read a half-empty
chamber as full. PARK already has every number an instrument needs
(per-door stamps, ages, the 45 s freshness law, the count, the
warrant word). It prints them all as prose. **Turn the prose into
the instrument it already describes.**

And its corollary: today NOT CHECKED is a sentence wearing the same
ink as every proved word. It is honest, but it is not *designed* —
it reads like a result. NOT CHECKED is not a result. It is the
instrument **at rest, unmeasured, with its cover on** — and rest
states on real instruments are the most designed states of all:
the pinned zero stop, the capped lens, the unexposed plate.
Give NOT CHECKED its own ink, its own geometry, its own ceremony.
Never smaller, never greyer-and-forgotten — *different in kind*.

---

## 1 · THE INSTRUMENT — GUARD + THE COUNTED FOUR

### 1.1 THE FOUR CHAMBERS *(the flagship)*
Four vertical chambers stand under the Guard headline, tier-ordered
exactly as §3 ranks them — Tier 1 pair (ACCESSIBILITY · USAGE
ACCESS) tallest, then DEVICE ADMIN, then NOTIFICATION LISTENER —
each chamber's fill is that door's **freshness**: full at the stamp,
draining linearly to empty at 45 s, refilled only by a real re-stamp.
- **Mechanism:** one Canvas row; fill height = `1 − age/STALE_AFTER_MS`
  from the stamps WatchStore already buries (`measuredMono`), redrawn
  on PARK's existing 5 s beat; door name + word under each chamber in
  Anonymous Pro.
- **Cost:** M (~120 lines, no store change, no DB, no new permission).
- **Honesty line:** an unmeasured door's chamber is *not* empty — it
  renders as bare etched outline with no floor line at all (see §2.2).
  Empty = measured-and-dead; outline = never measured. The two can
  never be confused, and neither can be confused with full.

### 1.2 THE ZERO STOP
The Guard instrument gets a machined **zero stop** at its left end —
a small pinned block, labelled `REST` — and in the NOT CHECKED state
the count needle (or the headline's count slot) sits visibly *against
the stop*, before the scale even begins. A needle at the stop is not
zero. Zero is a measurement. The stop says: *no measurement exists.*
- **Mechanism:** the headline renders `GUARD — NOT CHECKED` with the
  count slot replaced by a drawn stop glyph (a 2 dp rule + block)
  instead of a numeral; the instant the first complete set lands,
  the slot takes the numeral and the stop stays printed, unused, at
  the scale's edge — part of the furniture, like a real gauge.
- **Cost:** S (~30 lines, print-layer only, rides Hof's total
  Guard-line function — the stop is a fourth print case, never a
  blank and never a fake `0/4`).

### 1.3 THE SPIRIT LEVEL
The Guard line as a **level**: a horizontal track under the headline
with a bubble whose position is the count — hard left at 0/4,
centred only at 4/4 *with* warrant LIVE; UNPROVEN parks the bubble
one notch off-centre even at 4/4, because four granted doors whose
warrant is unproven are *not level*, and the eye should feel that
before the word UNPROVEN is read.
- **Mechanism:** bubble offset = f(countedWarranted, warrantPrint) —
  both already derived in WatchStore; a 24 dp track, bubble drawn in
  Canvas, eased on PARK's 5 s beat (no continuous animation).
- **Cost:** S (~50 lines). Mutually exclusive with 1.1 as the headline
  instrument — pick one flagship; this is the cheaper, quieter one.

### 1.4 THE ABACUS WIRE
The counted four as four beads on a wire beneath the headline. A bead
slides to the COUNTED side only when its door is granted **and**
fresh; STALE slides it back at 45 s in front of the reader — the
decay P-4 complains about becomes a *motion you watch happen*
instead of a word quietly changing while you weren't looking.
- **Mechanism:** bead x-position animated between two detents on the
  existing 5 s tick from `permPrintAt()` per door; wire + beads in
  Canvas, tier order left→right, labels beneath in Anonymous Pro.
- **Cost:** M (~90 lines; the only motion in this file, and it is
  tick-stepped, never free-running — Feel 3's motion law holds).

### 1.5 THE SCANNER LINE *(CHECK made visible, not theatrical)*
While CHECKING, a hairline travels the board **in board order** and
each row's word fills from hollow to solid ink *as its own stamp
lands*. If a query hangs, the hairline **waits at that row** — the
stuck door is located visually before it is named, and the line can
never outrun the evidence because it is driven by stamp arrivals,
not by a timer (Zahn's Q1 and the C-2 ruling are the spec here).
- **Mechanism:** hairline y = first row whose `sweepGen` ≠ current
  sweep's gen; ink fill keyed to the same per-item stamp check
  CHECKING already uses; on completion the hairline parks at the
  foot line and the board signs itself as today.
- **Cost:** M (~100 lines, no machinery change — pure render of
  state the sweep already publishes).
- **Not Komet's ground:** this is not a ritual and costs the user
  nothing; it is the sweep's *readout*, not its theatre.

### 1.6 THE MINIATURE — SAME INSTRUMENT, EVERYWHERE THE LINE PRINTS
Wherever the Guard line appears outside PARK (Deck header band,
reception sheet), it prints as a **four-pip miniature** of the same
instrument — four tiny chambers/pips, same fill law, same hollow
dress for unmeasured — so the count is readable at a glance without
opening the drawer, and there is exactly **one visual language of
proof** in the whole app, learned once, read everywhere.
- **Mechanism:** one shared composable, `GuardInstrument(scale)`,
  fed by the same snapshot the band already reads; PARK renders it
  full-size, the band renders it at pip size.
- **Cost:** S–M (~60 lines + call-site swaps; kills the risk of the
  band and the board drifting into two dialects of the truth).

### 1.7 DOOR APERTURES
Each counted door gets a machined glyph — not an icon, an
**aperture**: a small square frame whose shutter is open (granted +
fresh), half (STALE), closed (MISSING), or *absent — frame only,
no shutter drawn* (UNMEASURED). Four apertures in the headline row
turn `GUARD {n}/4` into something countable by a thumb at arm's
length, in the dark, without reading.
- **Mechanism:** 4-state Canvas glyph per door from `permPrintAt()`;
  states map 1:1 onto existing prints, no new derivation.
- **Cost:** S (~70 lines). Pairs with any flagship above as the
  headline's compact form.

---

## 2 · NOT CHECKED — THE DESIGNED REST STATE

### 2.1 HOLLOW INK *(the cheapest radical idea in this file)*
Unmeasured prints in **outline type** — stroke, no fill. The moment
a door is truly measured, its word inks solid. Ink weight *is*
evidence weight: solid = measured, hollow = not yet, half-ink =
STALE (already law, Linea F-1), full lack-ink = MISSING. Four
states, four inks, zero colour changes, zero new words. NOT CHECKED
becomes the only hollow line on the sheet — spottable across the
room, impossible to mistake for a verdict, and honestly beautiful:
an instrument's face before the first reading.
- **Mechanism:** text style variant with stroke rendering for the
  UNMEASURED print case only (headline + board rows); every other
  case untouched.
- **Cost:** S (~40 lines). **Guardrail:** hollow must never be used
  for anything else, ever — the day hollow means two things, it
  means nothing.

### 2.2 THE ETCHED SCHEMATIC *(cold open as a designed object)*
On cold open, before the first sweep lands, the whole board prints
as an **etched schematic of itself**: every row present, in order,
in outline — names etched, value slots ruled but empty, chambers
(1.1) as bare outlines with no floor. It looks like the instrument's
own blueprint, because that is exactly what it is: the shape of the
proof, before any proof exists. As each stamp lands its row inks
in — the schematic *becomes* the instrument, door by door, in board
order. Nobody waits on a blank, nobody is lied to by a spinner,
and the first two seconds of PARK are the most honest — and most
beautiful — the surface ever gets.
- **Mechanism:** render gate = snapshot has no complete set yet →
  schematic dress (hollow ink + chamber outlines + ruled slots);
  per-row dress flips on that row's first stamp. No timer, no
  staged animation; the sweep's real arrivals are the animation.
- **Cost:** M (~80 lines, composes 2.1 + 1.1; without 1.1 it still
  works as hollow rows + ruled slots, ~50 lines).

### 2.3 THE DOTTED FIELD
NOT CHECKED's native dress is the **printed form's unfilled field**:
`GUARD ···· — NOT CHECKED`, the dotted leader already native to the
foot line's grammar (`GUARD ···· {n}/4 · {HH:mm}`) promoted to the
headline in the unmeasured state. A dotted field is *designed
emptiness* — ruled, bounded, obviously awaiting an entry — the
exact opposite of a blank screen, and it costs the reader zero
learning because every printed form on Earth taught it to him.
As doors land, dots are *replaced* door-by-door in board order —
the field fills like a form being stamped in, four stamps, then
the count.
- **Mechanism:** headline count slot renders `····` → per-door
  replacement keyed to first-stamp arrival (same gate as 2.2).
- **Cost:** S (~25 lines). The zero-learning idea: if only one idea
  in §2 ships, ship this one under 2.1.

### 2.4 THE COVER PLATE
In the NOT CHECKED state the headline wears a **cover plate**: a
thin ruled plate laid over the instrument's face, etched with
`GUARD` in hollow Sora and one line in Atkinson — `NOT CHECKED —
TAP CHECK`. The first completed sweep *removes* the plate (it does
not fade; it is simply gone, the way a removed cover is gone) and
the plate never returns within the residency — NOT CHECKED is a
birth state, honoured once, not a costume the board keeps changing
into. (In-session returns to unmeasured dress use hollow ink, §2.1 —
the plate is for the cold open only. One ceremony, once.)
- **Mechanism:** boolean on first complete-set arrival in the
  residency (WatchStore already tracks the complete-set instant
  for LAST PROVED); plate = one extra composable layer in the
  header, dismissed by that flag, no animation beyond the drawer's
  own motion.
- **Cost:** S (~45 lines). **Honesty line:** the plate must never
  carry a count, a colour, or a partial read — a cover that leaks
  a verdict is a lie with good typography.

### 2.5 NOT CHECKED GETS THE BIG TYPE
Heretical and simple: in the unmeasured state, `NOT CHECKED` is set
**larger** than the proved headline ever is — Sora at headline
scale, the biggest word on the sheet. Every instinct says shrink
the embarrassing state; instruments do the opposite — the rest
state is the face you see most, so it is the face that gets the
design budget. A tired man cannot read big hollow type as "fine."
- **Mechanism:** headline text size keyed to the print case
  (unmeasured → display size; measured → today's size).
- **Cost:** S (~10 lines). Pairs with 2.1/2.3; conflicts with
  nothing except timidity.

---

## 3 · THE COMPOSITION — IF REN BUILDS ONE STACK

Cheapest true stack, in order: **2.3 Dotted Field + 2.1 Hollow Ink**
(S+S — NOT CHECKED becomes a designed state this week, print-layer
only) → **1.7 Door Apertures** (S — the count becomes glanceable) →
**1.5 Scanner Line** (M — CHECK becomes legible as it works) →
**1.1 Four Chambers** (M — freshness becomes a level, STALE becomes
visible decay) → **1.6 Miniature** (S–M — one proof-language, app-
wide). Total ≈ 2 S + 2 M + change. No DB, no new permission, no new
surface, no machinery — every idea above renders state WatchStore
already publishes. 1.3 and 1.4 are alternates for 1.1's flagship
slot, not additions to it: an instrument has one face.

**Named risks, honestly:** (a) hollow/outline text must pass the
1 AM legibility test (Muede's seat) — stroke weight is the whole
game, prototype it first; (b) chamber drain on a 5 s beat steps
rather than flows — acceptable, arguably *correct*: the store's
truth is itself stepped; (c) any of these that ships without its
honesty guardrail (hollow reused, cover leaking a count, hairline
on a timer) is worse than shipping none — the guardrails in each
idea are part of the idea.

**What I deliberately did not propose:** anything that proves in
the dark, times a hold, narrates the past, or makes a sound —
Komet's wall, and a good one. Mine is the wall you *look* at.

## 4 · DIFFERENTIATION FROM TAUMEL (third crazy seat, landed mid-work)

Taumel's wave-1 (`studio/plan/issue-9-taumel-crazy.md`, cce41f0)
landed on this branch while this file was being written. Two of his
ideas live near two of mine; they are not the same ideas:
- His **Ghost Board (T-3)** renders what the board *believed when
  you walked in* — remembered, stale belief, in half-ink, with diff
  ticks. My **Etched Schematic (§2.2)** renders what has *never
  been measured at all* — no belief exists yet; the board is its
  own blueprint until the first stamp. Ghost = memory; schematic =
  absence. They could even compose (schematic on the first-ever
  open, ghost on later opens), but neither replaces the other.
- His **Missing Chair (T-12)** is a dotted outline for a NO DOOR
  *row* (parent missing). My **Dotted Field (§2.3)** is the dotted
  leader for the *headline's* NOT CHECKED state (nothing measured
  yet). Different surface, different state, different cure — his
  holds a seat for a door that should exist; mine holds a field
  for a count that does not exist yet.
Everything else of his (key-turn CHECK, DOUBT key, audible CHECK,
check stubs, prophecy, FIX stamps, biography press, sealed CHECK,
dark-when-healthy, usual suspect) is CHECK-theatre, history, or
sound — none of it is the instrument face or the rest state, and
none of it is repeated above.

— Prisma (crazy-ideas, issue #9)
