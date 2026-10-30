# ISSUE #6 — FEEL-1 (TOUCH) — THE DECK'S TOUCH FEEL, END TO END
## Cell: Rost (lead) · FEEL-1 seat · Fri Oct 30, 2026
## Status: DECLARED, paper only. Nothing compiled. Nothing committed (Rost commits).
## Report to Rost. This file prices feel only; it re-opens no audit ruling.

Read whole, as briefed: `AUDIT-deck.md` (§3 slide/chair, §6 tests
stand as law), `BlockitCoreViews.kt` (RotaryWheelView,
SlideGestureMath, SlideToStartView, the fireFeel/fireWeightedKnock
ladder), `TactileDeckScreen.kt` (the chair's current chips, the
slide's host wiring), `ISSUE-6-innovator.md` P5, `ISSUE-6-designer.md`
D11. Engine checked: `TactileSoundEngine` offers exactly four
pitched/weighted voices — `playRotaryDetent(freq)`,
`playMechanicalLatch`, `playFirmClick`, `playSeatKnock(dur, amp,
pitch)` — and every spec below speaks only through them. No new
engine voice is proposed anywhere in this file.

**The one law this file adds:** on a touchscreen there is no real
resistance. A finger cannot be pushed back. So "resistance" on
this Deck is lawful only in three channels — **(a)** the travel a
position costs the finger (gear ratios, ceilings, stops),
**(b)** what the object does the instant the finger leaves it
(coast, capture, springs), and **(c)** the knock family (pitch,
weight, timing). Any fourth channel — above all a finger↔object
position offset dressed up as "drag" — is a position lie, and
this file forbids it wherever it was tempting. The wheel's own
standing comment already holds this law ("under the finger the
wheel is 1:1 at every load — mass never fakes position"); FEEL-1
generalises it to every gesture below.

---

## §0 · GROUND TRUTH — WHAT THE HAND FEELS TODAY (audited from code)

| Gesture | Travel law today | Detents / haptics today | Failure feel today |
|---|---|---|---|
| Wheel rim drag | 1:1, unwrapped 108°–432°, hard clamp at both ends; dead sector 72°–108° clamps to nearer shore | 1ms detent per 5-min crossing, pitch ladder 340→880Hz by value; 4ms latch-class tick at %15 stops; strait = 9° of silence, landing speaks | End clamps are **silent** — the finger runs off the scale and nothing says so |
| Wheel coast | Viscous tau 0.34s·√inertia (→~0.48s at 8 blocks) + eddy brake 300·√I; wells capture ≤540/√I °/s within 4° (FLOW 6°); spring ζ=0.38, ω0 21 (FLOW 23.5), seats ≤520ms; strait refuses <180°/s, tolls 90°/s + knock above | Seat knock by count: 16ms@0.85/320Hz → 24ms@1.0/230Hz; strait crossing knock 10ms@0.5/280Hz | Deadline 1,400ms springs home; refusal at the strait sounds like an ordinary arrival (deliberate, kept) |
| Hub press (blocks) | Dip 1dp/45ms (HubPressDip), specular core travels 2dp toward 135°; return 160ms (HubSeatReturn); commit on release inside ×1.1 hub; MOVE never retargets | Commit: 4ms latch click; closure commits (→4/→8 from below): 8ms firm click. Drag-off/cancel: silent return | Under FLOW: **no dip, no click, nothing** — a press on a dead-flat hub with zero report |
| Slide drag | gear(p): 1:1 to displayed 0.68, gain 2/3 beyond — catch 0.84 sits at finger 0.92, full throw displays 0.8933 | DOWN: 4ms latch. Gate Schmitt 0.68/0.66: 1ms @ 620Hz up, 340Hz retreat (wire re-read *before* the feel; suppressed on OFF) | — (failures are the refusal family, below) |
| Slide by wire state | LIVE: limit 1.0 · UNPROVEN: hard ceiling at catch 0.84 · OFF: hard clamp 0.65, F0 fires the refusal on the first frame raw ≥ stop | Ceiling/clamp arrivals are **silent** — the cap stops and the finger doesn't know whether it hit a wall or a bug | See refusal |
| Slide tap | ≥90ms on cap = order: mesh 140ms bezier (exit velocity exactly 0) → seat spring ω0=20, ζ=0.40, one-way commutator floor | Words flip to TAP AGAIN TO UNDO in the mesh window; second touch aborts (spring ω0=22, ζ=0.90) | Abort is **haptically silent** — the undo registers only visually |
| Slide refusal | Knock → 300ms hold → universal return spring (stiffness 260, ζ=0.78, ~320ms); mid-drag death: powered retreat to 0.65 in 160ms, knock at the stop | Refusal knock 12ms@0.70/220Hz | Latch-frame refusal (seat completed, wire dead at the read): seat knock fired at 0.97, then refusal knock ~a frame later — **two knocks stutter** |
| Slide seat/seal | Seat knock predicate at displayed 0.97; latch at 1.00 dispatches | 14ms@0.85/320Hz | Sled return home: total silence by construction (Schlitten) — kept, §5 |
| STOP chair (running) | **None.** Two Compose chips (`GRACE WINDOW ABORT` / `EMERGENCY BREAK VALVE (60S HOLD)`) at 58dp in the 72dp zone, plain `.clickable` | **None — no dip, no sink, no haptic, no pressed state of any kind** | None. The Deck's most consequential tap is its least felt |

Three gestures already feel like machinery (wheel coast, hub,
slide drag/seat). The gaps are exactly four: wheel end-stops,
the FLOW hub, the slide's blind print and silent walls, and the
chair — which has no feel at all. The rest of this file is those
gaps, the per-state curves that must not regress, and P5 judged.

---

## §1 · THE WHEEL — RIM DRAG

**Resistance curve (per state).** Under the finger: 1:1 in every
state, forever (§0 law). Resistance is expressed at release and
in the knock ladder — the spec is to *keep and name* the built
curves so no future lane "improves" the drag into a lie:

- **Timed, 1 block:** inertia 1.0 — the reference wheel.
- **Timed, n blocks (2–8):** inertia 1 + 0.14·(n−1) → 1.98 max.
  Felt as: coast tau 0.34→0.48s, capture velocity 540→384°/s,
  seat knock 16→24ms / pitch 320→230Hz. The count is weight the
  hand learns at the *seat*, never drag at the rim. **Built. Keep.**
- **FLOW (seat at 108°):** inertia 1.0 (lightest), deeper well
  (ω0 23.5 vs 21), wider capture (6° vs 4°). An open session
  should feel like the wheel's resting place, not its edge.
  **Built. Keep.**
- **The strait (108°–126°):** under the finger, 9° of haptic
  silence and no value — the break in grip *is* the resistance;
  the landing speaks (FLOW shore: weighted knock 14ms@0.8/300Hz;
  10 shore: 1ms @ 340Hz). **Built. Keep.**
- **Locked (sealed):** consumes and drops every touch, no feel
  at all. Correct and kept: a sealed wheel that buzzes when
  poked teaches poking.

**Detents.** 35 true stops, 9° per 5 min; tick ladder 340→880Hz
across the scale; %15 stops step up to the 4ms latch class;
majors/minors are print classes, not extra haptics. One detent
family, no per-stop novelty. **Built. Keep.**

**Failure feels — one gap. W1 · End-stop knock (S).** Dragging
into the 108° or 432° clamp is silent today: the finger keeps
moving, the wheel doesn't, and for ~a degree the hand can't
tell clamp from lag. Spec: on the first drag frame that clamps
(edge-triggered, once per arrival, re-armed only after the
angle leaves the end by ≥2°), one dull knock — 10ms @ 0.55,
220Hz, the brush family (§6). A coast that arrives at an end
already springs home and seats with the seat knock; no second
knock there. *Failure feel named:* hitting the end of the scale
should feel like a pawl meeting a stop, not like the view died.

**Priced: W1 = S** (one edge-trigger in `updateMinutesFromTouch`
+ one knock call; pure-predicate test beside SlideGestureMath's).

---

## §2 · THE WHEEL — COAST, WELLS, THE HUB PRESS

**Coast feel (record, no change priced).** The integrator is
the best feel in the app and this file protects it: ζ=0.38 —
exactly two overshoots — is the wheel's signature and must not
be "fixed" toward dead-beat; the 1,400ms fling deadline and the
strait's under-180°/s refusal (which sounds like an ordinary
arrival, never an alarm) are ruled feels, not bugs. Any lane
that retunes these numbers re-opens this section with Rost.

**Hub press (blocks count).** The dip is the resistance: 45ms
down, 1dp, core light travelling with it; return 160ms; commit
only on release-inside; drag-off cancels in silence (a cancel
that knocks would teach fear of touching the hub — kept
silent). Closure commits land heavier (8ms) than loose sets
(4ms); de-closures stay light ("breaking a hand must not feel
like a warning" — kept verbatim as the rule). Tetrad bar ink
settling 0.88→1.0 over 160ms at counts 4/8 is the one place the
hub feels heavier without a haptic — kept.

**Failure feel — one gap. H1 · The FLOW hub (S).** Under FLOW
the hub is display-only: no dip starts, nothing commits — and
today nothing reports, either. A 1 AM press on a flat,
unanswering hub reads as a frozen screen. Spec: on release
(inside or out — no commit exists to protect), one dull knock,
10ms @ 0.55 / 220Hz, brush family; **no dip, no motion** — the
tease-motion ban stands (a dip with no possible commit is a
lie), the knock alone says "seen, and this surface is print
right now." If Rost judges even the knock a tease, the fallback
is total silence and H1 dies on paper — flagged, not buried.

**Priced: H1 = S.** Long-press on the hub: deliberately nothing
— the dip simply stays dipped. No new verb is invented here.

---

## §3 · THE SLIDE — TRAVEL, AND PRINT THAT SEES THE HAND (T-7)

**Resistance curve (per wire state).** The gear is the
resistance and it is already honest: free 1:1 while the gesture
is cheap, gain 2/3 past the gate so the last stretch to the
catch (finger 0.92) costs 1.5× travel — commitment gets
physically more expensive exactly where it gets real.

- **LIVE:** gear as built, limit 1.0. **Keep.**
- **UNPROVEN:** same gear, hard ceiling at displayed 0.84. The
  cap stops; the finger may travel on into dead travel. That
  dead travel *is* the state's feel — an unproven wire lets
  you arrive at the catch and not one pixel past it.
- **OFF:** hard clamp at 0.65; F0 starts the refusal on the
  first frame raw travel reaches the stop. The stop sits
  *before* the gate's full meaning — you cannot even arm the
  latch on a dead wire.
- **PROVING / SEATED / return:** the slide is a ghost (sealed
  gate consumes and drops) or a sled (return). No drag exists
  in these states; §5 covers their feel (silence).

**Detents.** Exactly one: the gate Schmitt latch (0.68 up /
0.66 retreat), pitched 620Hz up / 340Hz down, wire re-read
before the feel, feel suppressed entirely on a dead read. One
threshold, one tick pair — more ticks would fake granularity
the gesture doesn't have. **Built. Keep.**

**Failure feel — S2 · The silent wall (S).** Both non-LIVE
ceilings arrive silently today. A cap that stops mid-track
with no report reads as lag or a hung view at exactly the
moment the machine is telling the truth. Spec: edge-triggered,
once per attempt, when the finger's raw fraction first exceeds
the active travel limit by ≥0.03 — one knock at the wall:
UNPROVEN ceiling: 10ms @ 0.5 / 280Hz (the strait-toll voice —
a boundary crossed into resistance); OFF stop: the refusal
sequence already owns that frame (F0), no second knock.
Re-arms only when raw falls back below the limit. Drag-release
at the UNPROVEN ceiling still refuses from the catch as built.

**The words (T-7) — S1 · Word-state, priced S.** Vega's charge
stands and the code convicts it precisely: the track words are
two compile-time constants (`SLIDE OR TAP TO START` /
`TAP AGAIN TO UNDO`) with, in the file's own words, "no state
input at all." The hand-drag fade (1 − 1.3·frac) hides the
instruction past displayed ~0.77, which *mitigates* the drag —
but three frames still print a start-instruction over a
gesture that has already happened:

1. **REFUSAL_HOLD at 0.65 / at the catch:** fade is 1 outside
   DRAG — full-bright `SLIDE OR TAP TO START` printed on a
   machine that is actively refusing. The worst T-7 frame on
   the screen.
2. **The sled return:** mode is neither DRAG nor SEAT-from-drag,
   fade is 1 — the cap travels *home* under a start instruction.
3. **SEAT:** the machine has taken the gesture; the print must
   stop instructing the hand that no longer owns it.

Spec — words become a pure function of (mode, gate latch,
displayed), changing **exactly at the gate's Schmitt crossings**
so print and detent can never disagree, and the dead-band
(0.68/0.66) doubles as the word latch (no boundary flicker):

| State | Words |
|---|---|
| REST, displayed < gate | `SLIDE OR TAP TO START` (fade as built in drag) |
| DRAG, gate latched (≥0.68, until ≤0.66 retreat) | `KEEP SLIDING` |
| SEAT (drag or mesh hand-off) | `SEALING…` — the machine owns it now |
| MESH | `TAP AGAIN TO UNDO` (kept) |
| ABORT_RETURN / spring-back / REFUSAL_* / sled return / SEATED | **blank** — no words. In refusal the sentence above the slide owns language; the slide speaks knock and motion only. A returning or refused cap prints nothing rather than re-instructing |

The "constants, no state input" property (Teo) was the bug's
armour; it is replaced by a pure function in the
SlideGestureMath family with a truth-table unit test — the
same discipline, aimed at the truth this time. The fade law
survives unchanged for hand-drags.

**Priced: S1 = S** (one pure function + the draw switch + the
test) · **S2 = S**.

---

## §4 · THE SLIDE — TAP, ABORT, REFUSAL, SEAT

**Tap path (record).** Brush floor 90ms; a classified tap runs
the 140ms mesh (bezier exit velocity exactly 0) into the seat
spring; on UNPROVEN the mesh drives only to the catch and the
refusal runs from there. A brush: 1dp sink for 140ms + dull
knock 10ms @ 0.55 / 220Hz, zero travel — the vocabulary for
"a contact that was not an order." **Built. Keep.**

**Failure feel — S3 · The silent abort (S).** The buttonhole
abort (second touch during the mesh) springs home on the
integrator and dispatches nothing — provably correct, and
haptically mute: the man who tapped again to undo gets no
confirmation the undo registered until the cap visibly lands.
Spec: at abort start, the brush knock (10ms @ 0.55 / 220Hz) —
same voice as the brush, because an abort *is* the gesture
class "not an order after all." One knock, no new family.

**Refusal family (record + one fix).** Knock 12ms @ 0.70 /
220Hz → 300ms hold at the stop → the universal return spring
(stiffness 260, ζ=0.78, ~320ms) — "the app's one not-committed
voice." Mid-drag death retreats powered (160ms) to the stop
and knocks on arrival; the revival pin (a refused attempt stays
refused until the next press) is a feel law as much as a truth
law: a gesture the machine has refused must not quietly become
acceptable mid-press. **Built. Keep**, except:

**S4 · The latch-frame stutter (S).** Worst-case sequence in
the app: finger drives the seat, seat knock fires at 0.97
(14ms @ 0.85), the latch frame reads a dead wire, refusal
knock fires (12ms @ 0.70) — two heavy knocks within ~a frame
of travel read as a stutter, not a sentence. Spec: if the
latch knock fired <150ms before a latch-frame refusal, the
refusal knock is *replaced* by a single heavier close —
16ms @ 0.85 / 180Hz — so the sequence reads seat…**no**:
one knock that lands lower and heavier than the seat's.
Otherwise the refusal knock stands as built.

**Seat & seal (record).** Commutator floor (the seat never
walks back below its crossing), knock at 0.97 before the latch
at 1.00, dispatch only at the latch on a fresh LIVE read.
**Built. Keep.**

**Priced: S3 = S · S4 = S.**

---

## §5 · THE SLED RETURN AND THE SEALED SLIDE (record)

Total silence by construction — no fireFeel exists on the
return path, and an adopted mid-return grab is seeded "minus
every sound." This is ruled feel, protected here: the return
is the machine tidying up after a receipt, not an event in the
man's life, and the sealed slide's consume-and-drop (no
response to any touch) is the same silence the locked wheel
keeps. Any lane adding a knock to either re-opens this section
with Rost. **No price — a fence, not a feature.**

---

## §6 · THE STOP CHAIR — A PRESS, SPECIFIED (inside audit F6, M)

Today the chair is two swapping Compose chips with a bare
`.clickable`: no dip, no haptic, no pressed state, 58dp
floating in a 72dp zone — and one of its labels (`(60S HOLD)`)
describes a gesture the chip does not perform (audit §3,
§5.3). F6 rules the form (one chair, one verb STOP, whole
session, full 72dp, price in its own words, countdown in the
hero). This section specs only its **press**:

**Body & resistance.** Full 72dp zone, key-cap body, 12dp
radius (F13's register). Resistance for a tap is a *dip*: the
chair sinks 1dp over 45ms on `HubPressDip` and returns over
160ms on `HubSeatReturn`, shadow gap closing as it seats —
**the hub's press physics, reused whole.** The app gets ONE
press feel; a second dip curve for the chair is a dialect.

**Commit timing — on DOWN, both eras.** The hub law (commit on
release-inside, drag-off cancels) is wrong here, deliberately:
inside the 10s grace the stop is free and instant (Muede) and
speed is the mercy — a panicking man must not lose his stop to
a sloppy release. After grace, the tap only *opens* the valve
challenge; nothing is spent until the overlay's 60s hold
completes, so a DOWN-commit risks nothing either. The dip
still plays in full — it is feel, not a gate. The accidental-
brush objection is priced honestly: a brush in grace stops a
session that costs one slide to re-arm; a brush after grace
opens a challenge that costs nothing to walk away from.
Brush risk ≈ 0 in both eras; DOWN-commit stands.

**Haptic events — the era must be felt, not just read.**

- **Grace era (≤10s):** DOWN fires the firm click (8ms class,
  `playFirmClick`) — light, free, instant, matching a verb
  that costs nothing.
- **Priced era (>10s):** DOWN fires the weighted knock —
  14ms @ 0.85, seat-knock family, pitch at the low end
  (~230Hz). The tap gets *heavier at the exact boundary where
  it starts costing something*, so the price is in the hand
  before it is in the eye. The chair's words carry the price
  in print (F6); the knock carries it in weight. Same tap,
  two weights, divided by the same 10 seconds that divides
  the words.
- **Era crossing itself** (no finger down): no haptic — the
  hero's grace countdown owns that moment in print; a buzz at
  10s with no touch would read as an alarm from nowhere.

**Failure feels.**

- **Zero tokens (priced era, 0/1 remaining):** the chair must
  not open a challenge that cannot complete. Tap = dip plays,
  then the refusal voice — dull knock 10ms @ 0.55 / 220Hz
  (brush family), nothing opens, the sentence slot explains.
  Same knock class as every other "seen, not possible" in
  this file (§8's grammar).
- **Double-tap / tap while stopping:** second tap lands on a
  session already ending — consume and drop, silent (the
  locked-wheel law). A STOP that acknowledges twice teaches
  double-tapping the most panic-adjacent control in the app.
- **No disabled state, ever:** while a session runs, STOP is
  live. A greyed STOP is a lie about the one verb that must
  always work; states change its words and its weight, never
  its availability.

**Priced:** the press physics + era haptics + zero-token feel
are the feel increment **inside F6's M** — if F6 lands without
them, they stand alone at **S**. The Compose side needs a
press-state (interaction source driving the dip), not a new
view; the knock calls are one-liners at the DOWN handler the
host already owns.

---

## §7 · FELT TRUTH (INNOVATOR P5) — JUDGED AS TOUCH

**The proposal, fairly stated:** wire state as feel during the
set gesture — LIVE detents land normal; UNPROVEN/stale detents
drop in pitch and the wheel goes "faintly gritty"; OFF leaves
the wheel free but the slide refuses (already built). Three
coarse classes, redundant with print, priced M, admitted by
its own author as the proposal most likely to die on device.

**Verdict: SHIP IT — wheel only, in the reduced form below.
The slide half is REJECTED, and the "gritty resistance" half
is rejected as stated and rebuilt on lawful channels.**

Three judgments, in order:

**1. The wheel is the right surface; the slide is the wrong
one.** The wheel is touched first, every time, often eyes-half-
shut — a felt wire there arrives *before* the slide is reached,
which is P5's whole thesis, and the wheel's physics/sound seams
(tau, capture, knock pitch) already exist and are already
parameterised by load. The slide, by contrast, **already
carries the wire in its travel limits** — OFF stop at 0.65,
UNPROVEN ceiling at the catch — hard, semantic, acquitted
("truth you can feel," Fremd). Innovator's slide spring
multiplier would add a second, softer encoding of the same
fact on the same gesture, and two encodings of one fact on one
surface is the felt dialect of the six-prints disease: when
the soft stiffness and the hard stop disagree in emphasis, the
hand believes neither. **Slide spring multiplier: REJECTED.
The slide's wire feel is its limits (§3), full stop.**

**2. "Gritty drag resistance" as stated is a position lie
waiting for an implementer.** The wheel's drag has no lawful
resistance channel (§0 law): any implementer handed "make the
drag grittier" will reach for tracking lag or gain, and both
fake position. The honest rebuild of "gritty" lives in the
knock ladder and the coast, which is where this spec puts it:

**3. The mapping (pure function of the freshness-checked print
state — `WatchStore.printState`, never the raw wire; a stale
LIVE must feel UNSURE, that is the point):**

| Class | Wire | Detent feel | Coast / seat feel |
|---|---|---|---|
| `SOLID` | freshly LIVE | As built: ladder 340→880Hz, %15 latch class | As built: tau per load, seat knock per count |
| `UNSURE` | UNPROVEN / stale | Pitch ladder × 0.72 (≈245→635Hz) **plus rattle**: every *other* detent fires a second tick at −40% amplitude, +18ms — a loose pawl, felt not heard-about | tau × 0.80 (runs down sooner — less held); capture velocity × 0.85; seat knock pitch −60Hz, duration +2ms (lands softer, less certain) |
| `DEAD` | OFF | Ladder **frozen flat at 240Hz** — the scale stops singing its rise; value stops sounding like progress | tau × 0.65; seat knock replaced by the dull knock (10ms @ 0.55 / 220Hz) |

The rattle is the one genuinely new feel in this file and it
is buildable today *only if* the engine's detent can be fired
twice on a schedule the view controls — flagged as the item's
first build question; if the engine cannot double-fire cleanly,
UNSURE ships pitch-only and the rattle dies without ceremony.

**Change-timing law.** Class is re-evaluated at warrant edges
only; mid-drag, a new class applies at the **next detent
crossing, never mid-detent** (Innovator's own clause, adopted).
No class ever changes travel: the wheel turns identically in
all three — he may always still mean FLOW, still mean to look.
**Feel sleeps while running/locked** (a sealed session's wire
drop is P1's latched print + the interrupt path, never a
vibration gimmick). **Redundancy is constitutional:** P2's
printed token always stands and F22 semantics announce the
same three classes in words; feel deletes duplication, it
never becomes the only carrier.

**Kill criteria (the device pass is the judge, paper is not).**
Blind test on device: 9 trials, classes shuffled, Sarthak
sorts by hand alone. ≥8/9 per class → ships. Below that → the
class collapses to SOLID-only (pitch factor and tau multipliers
are constants behind one enum; killing it is deleting a
mapping, not a mechanism). Tuning steps offered at the pass:
pitch factor 0.85 / 0.72 / 0.60; rattle on/off. **Priced: M**
(view param + mapping + detent/coast multipliers — matches
Innovator's price; the engine double-fire question may add S
if it needs an engine seam, flagged now, not discovered later).

---

## §8 · THE GRAMMAR — EVERY FAILURE FEELS THE SAME WAY TWICE, NEVER

So no lane invents a seventh dialect of *no*:

- **Commit** = pitched or heavy and *rising*: detent ladder,
  seat knock 14ms @ 0.85, closure click, priced-era STOP knock.
- **Refusal** = dull, low, short, and *motion home*: 220Hz
  family (brush 10 @ 0.55, refusal 12 @ 0.70, latch-refusal
  close 16 @ 0.85/180Hz as the single heavier exception),
  always followed by a spring or retreat the eye can verify.
- **Cancel / not-an-order** = silent, or the one dull brush
  knock where silence would read as a dead screen (abort,
  FLOW hub). Silence is a feel; spend it deliberately.
- **Boundary** = one edge-triggered knock, re-armed only by
  leaving (end-stops, ceilings, the strait toll).
- New feels must join a family above or argue to Rost in
  writing before they exist. The knock census stays countable
  on one hand: detent, latch, firm click, seat knock, dull
  knock. That smallness *is* the vocabulary.

**Tests (§6's discipline — pure JVM, the
SliceDGestureAndStripTest pattern):** word-state truth table
(mode × gate × displayed) for S1; wall edge-trigger predicate
for S2; WireFeel mapping (printState → class → multipliers)
for §7; chair era function (elapsed ≤ 10s → grace weight) for
§6. Each rides inside its item's price; none needs a device.

---

## PRICE LEDGER

| Item | What | Price |
|---|---|---|
| Record | Wheel drag/coast/hub curves, slide gear/gate/tap/refusal/seat as built — kept, named, fenced | — (already built) |
| W1 | Wheel end-stop knock, edge-triggered | **S** |
| H1 | FLOW-hub dull release knock (or ruled silence) | **S** |
| S1 | Slide word-state (T-7 cure): pure function + test | **S** |
| S2 | UNPROVEN ceiling wall knock | **S** |
| S3 | Mesh-abort brush knock | **S** |
| S4 | Latch-frame refusal knock merge (anti-stutter) | **S** |
| C1 | STOP chair press: hub dip physics + era haptics + zero-token feel | inside F6's **M**; standalone **S** |
| F1 | Felt Truth, wheel-only (mapping, multipliers, rattle if the engine allows) | **M** (+ possible S engine seam, flagged) |
| F2 | Felt Truth, slide spring multiplier | **REJECTED** — priced at the M it saves |

Every S above is a sitting's edit inside machinery that
already exists; the only M is Felt Truth, and it carries its
own executioner (§7 kill criteria). Nothing here touches DB,
host wiring, or the header Guard line (issue #4's cell).

— FEEL-1 (TOUCH), Rost's cell, issue #6. DECLARED, uncompiled,
uncommitted. File: `studio/plan/ISSUE-6-feel-1.md`.
