# Rosa — Wild, cars · Phase 3 GO-BEYOND: the truth layer

*Seat: the Parallel Bulb-Check and the Deck's truth standard. Filed floor:
WILD-PLAN §1.4 and my plan-seat file (07-rosa.md). Everything below exceeds
that floor or corrects it in the open. The walls stand: ARM ≤10s · HOLD
absolute · TELL THE TRUTH once. Locked foundations honoured throughout:
type ladder 104/72/50/22/15/10 (plate floor 10sp) · accent #F47442 is
COMMIT fill only and never appears in anything below · state is never
carried by colour alone · night ground #171412, recess #100E0C, raised
#362C27, cap #4A423C · never green.*

**Two corrections to my own filed spec, made before the beyonds, because
the ladder demands them:**

1. **My filed type sizes were illegal.** The strip's labels were specced at
   Anonymous Pro 9sp — under the 10sp plate floor — and the interlock line
   at Atkinson 13sp, a tier that does not exist. The ramp's rule is plain:
   plate is furniture, body is truth — the moment a word carries a state
   it is promoted to 15sp. Below, every state word in the truth layer is
   15sp, every label is plate 10sp, the interlock sentence is 15sp. The
   strip grows to pay for it (56dp, §2). A truth layer printed in
   furniture-sized type was a warning label; at lawful tiers it becomes
   an instrument (§2's whole argument).
2. **My clock-zero is withdrawn.** I filed "clock starts at the
   crank-release frame." Phase 2 (Moss) showed four rival zeros; worse,
   Item 1 below proves mine cannot survive the tap: a tap has no crank
   and no release-of-travel frame distinct from the verdict itself.
   Crank-release — and its tap twin, finger-up — are renamed here
   **t-intent**: the frame his instruction became complete. They are
   never printed, never counted as elapsed. **Opal has since named
   the single t=0 — the PROOF-SEAL FRAME — and this file re-times
   against his definition in Amendment A1, appended after the
   Closing.** Mailed to Opal, thread `t-zero-retiming`.

---

## Item 1 — Checks without travel: the Standing Check and the Press-Poll

### Beauty case, in plain words

A check that only exists while your thumb is moving is a check you never
actually see — you see its shadow pass across a drag you were already
making. That is why the filed strip, for all its honesty, felt like a
warning: it woke up when touched and went dark when the session started,
like a dashboard that only lights while the key is turning.

A machine he trusts is checked the way a car is checked: at key-on,
before the hand asks for anything, and the panel is simply *there* —
quiet, current, part of the furniture of the dash — every time he looks
down. Then the start itself re-proves the one thing that would make the
start a lie. Standing truth for the eye, fresh truth for the seal, and
neither one costs the gesture anything. The Deck gets prettier for the
same reason a well-kept dash is prettier than an empty one: nothing on
it is decoration, and nothing on it is asleep.

### The proposal

**The strip becomes a standing Deck citizen, and the press becomes the
poll detent. Both halves are load-bearing; neither works alone.**

**Half A — the Standing Check (for his eye, before any gesture).**
The 3-cell strip (`BLOCK` / `APPS` / `TIME`, geometry in §2) lives above
the slide track permanently — not summoned by touch-down, not dismissed
at seal. Its queries dispatch on:

- Deck entry (composition) and every app resume landing on the Deck;
- any permission-change broadcast, and the Shield service bind/unbind;
- loadout catch-drop (APPS re-queries; TIME recomputes);
- wheel seat (TIME only — a pure recompute, <5ms);
- a standing cadence of one re-poll every 20s, **only while the Deck is
  foreground and no session is running.** Local binder queries, <5ms CPU
  each; the cadence dies the frame the app backgrounds.

**Freshness is printed, truthfully, on the strip's own seam** (the stamp
row, §2): `CHECKED JUST NOW` (age <5s) · `CHECKED 10S AGO` — age always
**ceiling-rounded to the next 5s**, so the stamp never claims the check
is fresher than it is; freshness never flatters itself · `CHECKED OVER
1M AGO` past 55s. If re-polls keep failing past 45s of age, the BLOCK and
APPS state words decay to `STALE` — a word distinct from every other in
the grammar: *was measured, has expired.* TIME never goes STALE on the
Deck (its source is on the same screen; if the routine store is
unreadable it goes `UNKNOWN` like anything else).

The standing check's job is pre-gesture knowledge: a dead permission is
known — and repairable via the chrome health line's repair path
(HPA-03) — *before* he spends his one gesture on it. Prima's trap
(gesture spent, then the modal) dies here, not at the gate.

**Half B — the Press-Poll (for the seal, at the moment of commitment).**
Every commit path — drag and tap alike — dispatches a **fresh re-poll of
all three queries on ACTION_DOWN on the cap**, at t-down + 0ms, in the
same frame as touch handling, before the first move event is processed.
The standing state is never sealed on. It warms the pipe (binder cached,
service awake — which is why press-poll returns run at warm speed); the
press renders the verdict.

- **Drag path (filed, unchanged in cost):** the re-poll runs parallel
  with travel exactly as §1.4 filed. The 35% poll detent is retained:
  crossing it re-polls any cell still `CHECKING`/`UNKNOWN`.
- **Tap path (new):** a tap's contact is 120–250ms of press. That press
  *is* the poll detent — the queries run parallel with the contact,
  exactly as they run parallel with a drag's travel. On finger-up
  (t-intent for the tap) there are exactly three cases:
  1. **Verdict in during the press, BLOCK passes** (the typical case —
     warm queries return in 20–120ms): the seal sequence proceeds from
     the up frame, identical to the filed tap. Nothing added.
  2. **Verdict in during the press, BLOCK failed:** the up commits
     nothing. The refusal renders (§2's interlock sentence, one frame,
     no fade), the cap returns home on the return spring. Refusal lands
     *before* any seal exists.
  3. **No verdict by finger-up (slow query):** the cap enters **the
     Park** — it stays at press depth, dropped 1dp into a poll seat,
     accent **not** flooded (COMMIT fill floods only on a passing
     verdict; a parked cap is a cap still asking), the BLOCK cell reads
     `CHECKING`. The outstanding queries run to their deadlines. On the
     verdict frame: pass → the seal sequence runs from the parked
     position (gate tick, then the filed seal pair); fail → refusal as
     case 2; deadline (1500ms from t-down) with BLOCK unreturned →
     `UNKNOWN`, refusal, sentence variant in §2. The Park always
     resolves; it cannot be left hanging, and while parked a second tap
     is **ignored** (debounced — tremor and impatience are not input),
     and the only cancel is a deliberate drag of the cap downward off
     the track's bottom edge.
- **Drag path, same Park, other door:** a drag that reaches the
  interlock stop (gate −0.03, §2) with BLOCK still unreturned parks
  *at the stop, thumb down*, under the same deadlines and the same
  verdict rules. One Park state, one grammar, reached by travel or by
  press — the tap and the drag are the same instrument's two hands, as
  the tap verdict demands.

### Technique spec

**Queries (identical sources to the filed spec; nothing stored, nothing
assumed):**

| Cell | Query | Warm return | Cold return | Deadline |
|---|---|---|---|---|
| BLOCK | AccessibilityService enabled **and** Shield/blocking service running, queried live | 20–80ms | 60–150ms | 1500ms → `UNKNOWN` |
| APPS | Enforced count vs loadout count, from the blocking service's own report, per package | 30–120ms | 80–250ms | 1500ms → `UNKNOWN` |
| TIME | Finish recomputed from wheel total × routine boundaries (local compute + schedule read) | <5ms + 5–15ms | <20ms | 1500ms → `UNKNOWN` |

**Dispatch:** t-down + 0ms (both paths); standing triggers as listed
above. Per-cell state and timestamp replace atomically on each return —
a cell flips the frame its own data lands, never on a sibling's frame,
never on a stagger (filed, retained).

**States per cell:** pass / fault / `CHECKING` (poll in flight) /
`UNKNOWN` (deadline, no answer) / `STALE` (standing only, age >45s with
re-polls failing). Seal rule, absolute: **the seal may fire only on a
press-poll BLOCK pass returned inside this press.** A standing pass, a
stale pass, a cached pass — none of them can seal anything. APPS and
TIME faults remain advisory (session arms, fault stays lit, §2) on both
paths, exactly as filed.

**Haptics:**
- Dispatch (down, either path): silence. A query is not an event.
- Standing flips (including a pass→fault flip caught mid-idle by the
  20s cadence): silence, no haptic, no sound — background truth does
  not buzz across a room; he reads it when he looks.
- Park entry (finger-up with no verdict, or drag reaching the stop
  unresolved): one tick **5ms @ 0.4** — the hand is told *the machine
  is checking*, in the same voice as the filed poll-detent tick,
  because that is literally what it is.
- Verdict from Park, pass: gate tick **6ms @ 0.45**, then the filed
  seal pair (pre-tick 6ms @ 0.4, 28ms silence, thunk 28ms @ 1.0, 12ms
  @ 0.5, 8ms @ 0.25) — Fen's/Marlowe's seal grammar untouched; the Park
  borrows the dwell's *meaning* (arrival before commitment) without
  adding Fen's 120ms dwell on top: if a Park lasts ≥120ms it **is**
  the dwell, and no second dwell is charged. (Mailed to Fen, thread
  `press-as-poll`.)
- Verdict from Park, refusal: interlock knock **16ms @ 1.0**, once —
  the filed hard-stop knock. Pushing against a drag-Park: the filed
  scrape (4ms @ 0.25 every 30ms, capped 200ms).
- Sound: explicit silence throughout, as filed. 05:00 house rule.

**Motion:** cell flips in one frame, no easing (a bulb does not fade —
filed, retained, now applied to standing flips too). Park entry: cap
drops 1dp over 60ms, cubic-bezier(0.2, 0.9, 0.25, 1.0). Park exit to
seal: the filed crank assist (cap completes under the existing seal
sequence). Park exit to refusal / sub-gate release: return spring
stiffness 380, damping 0.62, ~260ms (filed).

### ARM cost

- **Serial, drag path: 0ms.** Unchanged from filed — the re-poll is
  dispatched on a frame that already exists and runs parallel with
  travel. Typical cold-open-to-t-intent ≈1.2s as filed; worst case is
  a drag parked at the stop until the 1500ms deadline: ≈2.5s to
  resolution, seal or refusal.
- **Serial, tap path: 0ms typical** (verdict lands inside the 120–250ms
  press). **Worst case: the Park remainder — deadline minus press time,
  ≈1250–1380ms** — incurred only on the tap path, only when a query is
  genuinely slow, and it buys the one thing the tap verdict cannot be
  allowed to skip: refusal *before* the seal. Total worst tap arm
  ≈1.6s from down to seal-or-refusal. Every figure is far inside the
  10s wall, and the serial remainder is printed here, not hidden in a
  "typical."
- Standing cadence: 0ms on every path; it runs when no gesture exists.

### HOLD / TRUTH check

- **HOLD:** no mid-session component. At seal the strip freezes at its
  seal-time verdict for the session's first frame, then hands its home
  back: a lit advisory fault (APPS/TIME) stays rendered on the running
  Deck as filed — a readout with no actions on it. The standing cadence
  stops at seal. Nothing here asks the tired user anything.
- **TRUTH:** the seal's evidence is never older than the press that
  caused it — that is the item's whole truth claim, and it is stronger
  than the filed spec, where a long thoughtful drag could seal on a
  touch-down query minutes old in human terms (150ms of data, a 4s
  drag: the filed strip's pass was as old as the drag). One pipeline:
  the strip, the chrome health line (HPA-03), and Suki's Truth Line all
  read the **same readiness store**; chrome renders its ambient tense
  (silent when true), the strip renders the seal tense. Two tenses of
  one fact from one source — the Band/Linea scope ruling's shape, not
  a second rendering of one moment.

### What it replaces, and why that loses

- **The filed drag-only strip** loses on the tap verdict's own terms:
  the tap is an *equal* commit running the *same* sequence. A filed
  strip on a tap either checks nothing (a silent pass — the one
  outcome this layer exists to forbid) or checks serially after the
  press (Prima's celebration-before-the-fact, rebuilt as
  verification-after-the-fact). The filed form was honest for exactly
  one of the two settled gestures.
- **The pure standing strip (Half A alone — Suki's convergence taken
  as the whole answer)** loses as a seal mechanism: any freshness
  window is a trust window, and inside it a permission can die
  unmeasured while the panel prints a present-tense word for a
  past-tense measurement. Standing is the right tense for the eye and
  the wrong tense for the seal.
- **Retiring the strip to chrome (HPA-03's line as sole home)** loses
  because chrome cannot refuse. The health line is silent when true
  and one line when false; the interlock needs a surface at the thumb,
  at the seal frame, that can physically hold a cap. A sentence in the
  nameplate has never stopped a slide.
- **Press-and-hold-to-poll (make the tap a 600ms hold)** loses because
  it revokes the tap verdict in disguise: the tap stops being the
  drag's equal and becomes a third, slower gesture that taxes every
  healthy morning for the sick morning's problem.

### Strongest case against this proposal

The Park makes the centre gesture's timing *variable*, and variable
timing in a daily gesture is a real aesthetic and trust cost: the same
tap that seals in 200ms on Tuesday parks for a full second on Wednesday,
and a man at 03:30 does not experience that as "the instrument was
checking" — he experiences it as *the button didn't work*, and his
corrective instinct is to tap again, harder, on a control I have
specced to ignore him. I have mitigations (the `CHECKING` word, the
park tick, the 120ms floor so a Park never reads as a glitch-flash),
but I cannot mitigate the deepest version of the objection: **the drag
never has this problem, because travel is time the hand expects to
spend.** If the merge decides the tap must be temporally boring above
all else, the honest fallback is Half A plus a hard rule — a tap on a
strip older than 10s or showing any non-pass cell does not commit at
all and prints the interlock line, and the *drag* becomes the only
path that can poll its way out. That fallback keeps every truth in
this item and sacrifices the tap's equality on bad mornings only. I
file against it — equality that evaporates exactly when the machine
is sick is the filed spec's failure wearing a seatbelt — but the merge
should know the price of my version is a gesture that occasionally,
truthfully, hesitates.

---

## Item 2 — The strip as an instrument, not a warning

### Beauty case, in plain words

A warning is designed to be ignored on all the days it is wrong to
heed it — which is most days, which is why every driver alive drives
past a lit check-engine lamp. An instrument is the opposite kind of
object: it is read, not heeded. It sits on the panel in daylight, it
has a rest state as designed as its alarm state, and its quiet is not
emptiness — its quiet is the reading.

That is what the Deck is missing compositionally. The wheel is an
instrument. The slide is an instrument. Between them, the filed strip
was an event — three cells that performed during a drag and vanished
into a session. Give the Deck a standing panel instead and the whole
screen becomes one machine face at rest: readout, wheel, panel, lever,
in that order, every morning, whether anything is wrong or not. And
the panel's deepest beauty is epistemic, and I will say it plainly
because it is the thesis of this seat: **a panel that can say UNKNOWN
makes its ON worth believing.** A gauge with a real zero, a lamp that
can stay lit, a cell that can admit it does not know — the pass reads
as *measured* instead of *defaulted*. Trust is a visual quality. You
can see it in a panel the way you can see it in a face, and at 03:30,
on the two-hundredth morning, it is most of why he will still be
willing to slide.

### Form language — every cell, four channels, none of them colour

The strip must pass with colour removed. Photocopy it in greyscale:
it still reads, because state is carried by **position, fill density,
word, and edge** — in that order of importance.

**Geometry (the panel as furniture):**
- Container: exactly the slide track's width (screen width − 40dp
  margins; 320dp on a 360dp screen), **56dp tall**, sitting **8dp above
  the track** (the S8 beat) — panel and lever share edges, one control
  cluster, aligned like a headstock over its slide.
- Body: recess tone — night **#100E0C** on ground #171412 — closed by a
  1px lighter hairline on the bottom edge only (filed). **Square
  corners.** Instruments are square; the app's radius budget belongs
  to the cap and the keys, and a rounded panel reads as a card, and a
  card reads as a notification, and a notification is a warning again.
- Three equal cells: (320 − 2×1px separators) / 3 ≈ **106dp each**,
  divided by 1px vertical hairlines one tone step above the recess.
  Cell order is fixed forever, left→right, and the order is causal:
  **BLOCK** (can it block at all) → **APPS** (what will it block) →
  **TIME** (until when). A fault's *address* is learned spatially in a
  week; after that he reads position before he reads words, which is
  exactly how a dash is read at speed.
- Cell interior: 10dp side padding, 6dp top. Label row: Anonymous Pro
  700, **10sp**, +2.0 tracking, lh 12, secondary ink — plate, furniture.
  2dp gap. State row: **15sp**, lh 20. Stamp row (§1): a 1px hairline
  across the strip's full width, then plate Anonymous Pro 700 10sp
  +2.0, lh 12, left inset 10dp — the freshness stamp, sitting on the
  panel's seam like a calibration mark stamped into the metal.
  44dp of cells + 12dp of stamp = the 56dp.

**Channel 1 — position.** Fixed cell addresses, above. Plus: the
interlock stop lives at **gate −0.03** of slide travel (0.65 against
Fen's 0.68 gate, as filed) — stated as a formula, not a number, so if
the merge moves the gate the stop moves with it and a refused cap
never tastes the COMMIT fill that floods past the gate.

**Channel 2 — fill density (three densities for three epistemic
states):**
- **Pass:** fill *out* — the cell sits at recess tone, flush, word in
  full ink. Quiet is a density, not an absence.
- **Fault (positive evidence of failure):** fill *in* at cap tone —
  night **#4A423C**, the staircase's capped fill. The cell visibly
  *fills up* to the heaviest tone the night palette allows a
  non-commit surface.
- **`CHECKING` / `UNKNOWN` / `STALE` (absence of evidence):** fill at
  **raised** tone — night **#362C27** — the middle density. Not pass,
  not fault: the panel's third state has a third body. A greyscale
  photocopy separates recess / raised / cap without effort; that is
  the test, and it passes.

**Channel 3 — word (the fact itself, at lawful size):**
- BLOCK: `ON` (pass) · `OFF` (fault) · `CHECKING` · `UNKNOWN` ·
  `STALE`. Set in Atkinson Hyperlegible 700, 15sp, +0.5 tracking —
  the ramp's state-word style. A word-cell gets the word family.
- APPS: `7 OF 7` (pass, real counts) · `6 OF 7` (fault) ·
  `CHECKING` · `UNKNOWN`. Set in Anonymous Pro 700, 15sp — a
  numeral-cell gets the numeral family; measured things are mono in
  this app.
- TIME: `ENDS 20:00` — the finish itself, always the fact, pass or
  crossing (Anonymous Pro 700, 15sp). A crossing is not rendered
  inside this cell at all: it is a sentence, and sentences live in the
  line slot (below), because a cell that must hold both a time and a
  warning will end up holding neither legibly. `UNKNOWN` when the
  schedule cannot be read.

**Channel 4 — edge.** A fault cell's top hairline doubles: a second
1px line prints 2dp above the strip's top edge over that cell's width
only — the cell *steps out of the panel's line* by one hair. Subtle at
rest, unmistakable in peripheral vision, and pure geometry: it
survives greyscale, colour-blindness, and a cracked screen protector.
Pass and unknown cells keep the single line. (This is the strip's one
new drawn element, and it is print, not light: static, state-locked,
no motion ever.)

**Flip behaviour:** one frame, no easing, no stagger, no cross-fade —
a bulb does not fade (filed, retained, extended to standing flips and
to the edge channel: the double hairline appears in the same frame as
the fill and the word). A cell that flips while he is *not* touching
anything (standing cadence catches a permission death mid-idle) flips
silently and stays flipped; the panel does not summon him. Instruments
don't beep at parked cars.

**Rest state, stated because the filed spec never stated it:** all
pass, Deck idle — three flush recessed cells, state words (700, per
the ramp — weight is not a state channel in this app) rendered in the
secondary ink tier, so *tone*, not weight, carries rest-vs-alarm:
a fault word steps up to the glyph tier in the same frame as its
fill. Labels in plate, stamp reading its age. Present, legible, demanding
nothing: the dash of a parked, healthy machine.

**The interlock's one sentence — exact words, exact type:**
- BLOCK positively off: **`BLOCKING IS OFF — NOTHING WILL BE HELD.`**
- BLOCK unverifiable at the deadline: **`BLOCKING UNVERIFIED —
  NOTHING WILL BE HELD.`** Two sentences because they are two facts;
  collapsing UNKNOWN into OFF would be the panel lying about what it
  knows in the very sentence that exists to refuse a lie.
- Set in **Atkinson Hyperlegible 700, 15sp, lh 22, tracking 0**, glyph
  ink at night, in a **reserved 22dp line slot 8dp below the track** —
  reserved *always*, empty when there is nothing to refuse, so a
  refusal appearing moves nothing else on the Deck by a single dp (a
  refusal that shoves the layout reads as a glitch; a refusal that
  fills its waiting slot reads as a verdict). The sentence appears in
  one frame, no fade, at: drag interlock stop, tap refusal, Park
  refusal — one sentence, one slot, every path.
- The same slot, lower priority, carries the advisory sentence when
  no interlock is active — TIME crossing, exact words:
  **`ENDS 21:40, AFTER SLEEP BEGINS AT 20:00.`** (times variable, form
  fixed), Atkinson 400, 15sp, lh 22. Priority in the slot:
  interlock > advisory > empty. APPS faults get no sentence — the
  cell's `6 OF 7` *is* the sentence, and a second rendering of it
  under the track would break truth-once inside my own element.

**Haptics:** the panel itself has none — it is read, not touched (no
cell is a target during or after a press begins; pre-arm, a lit BLOCK
cell may be tapped to open the repair sheet, HPA-03's path — rested
user, no session pending, identical under both Guard outcomes as
filed). All haptics live in the gestures of §1 and the filed slide.

### ARM cost

- **Serial: 0ms.** The panel is standing furniture; it adds no step,
  no wait, no dismissal to any path.
- **Parallel: 0ms added.** §1's queries were already the filed cost;
  the instrument rendering is static tones and one-frame flips.

### HOLD / TRUTH check

- **HOLD:** the panel introduces no mid-session decision — it has no
  live targets while sealed, and its seal-time freeze (§1) means the
  running Deck shows a verdict, not a voting booth.
- **TRUTH:** this item *is* the truth standard given a body. Its
  risks, named: (i) density steps at night are close — recess/raised/
  cap are one staircase apart each; mitigated because density is the
  *second* channel and the word is the first — density never carries
  a fact alone, it corroborates one. (ii) The doubled hairline is the
  weakest channel; it is deliberately redundant, spent on peripheral
  detection only. (iii) The tone-step for pass-vs-fault words must be
  measured, not assumed, at build — Floor's floor applies to the
  glyph that carries the fact, and the fault word at glyph tier is
  the one that must clear it.

### What it replaces, and why that loses

- **The filed strip-as-accessory** (present only during the drag,
  9sp labels, no rest state, no stamp): loses because an element with
  no rest state has no grammar — it can only ever *interrupt*, so it
  reads as a warning no matter how honest its data is. Interruption
  is a genre, and the genre trains ignoring.
- **Warning grammar generally** (appear-on-fault panels, modal
  refusals, red/amber lamps): loses on the car's evidence — a surface
  that exists only when something is wrong spends its whole life
  teaching the eye to slide off it, and the morning it matters is the
  morning the eye has had two hundred practices at sliding. It also
  fails the locked palette outright: there is no legal colour for it
  to shout in (accent rationed, green banned), so a warning here
  would have to shout in size — and size is the hero's.
- **Suki's chrome-only Truth Line as the panel's replacement:** the
  line and the panel are tenses of one store (§1), but the line
  alone loses *as furniture*: it is a sentence in a nameplate, read
  if noticed. The panel is a part of the machine face, aligned to the
  control it guards, impossible to un-see without un-seeing the
  slide. Ambient truth needs an address the eye already visits.

### Strongest case against this proposal

Fifty-six dp is not free. Sana's D5 is fighting for air between the
hero and the wheel — the pair law spends tall-screen reserve on the
number and its instrument — and my panel spends 56dp + two 8dp beats
of the Deck's vertical on a surface that, on perhaps nineteen
mornings in twenty, reports that nothing is wrong, in three cells, at
the largest body size the app allows below a title. The honest name
for that on a good morning is *clutter with a thesis*. Nacht's
eye-order test could convict it: if the strip's cap-filled fault
density is the first thing a half-awake eye finds on a bad morning,
fine — but if its mere standing presence demotes the hero's first-
found status on *every* morning, the panel has taxed the one
relationship the Deck exists for to insure against a rare one. My
defence is the rest-state tone discipline (pass words in secondary
ink, fills out, nothing at glyph tier unless it is a fault) — the
panel at rest is designed to be the *fourth* thing the eye finds,
after hero, wheel, and slide. If the device test says a standing
panel cannot be made that quiet at 15sp — if furniture at truth size
is inherently loud — then the correct cut is not smaller type (the
ladder forbids it) but demoting the standing panel to its stamp row
plus cells that print only their words on the recess, no interior
padding, 44dp — and if that still shouts, Half A of §1 survives with
the strip returning to the slide's edge and this item dies as a
composition that truth's own type size made too expensive. I would
rather lose it to a measurement than win it as a rendering nobody
tested at arm's length in the dark.

---

## Item 3 — My beyond: THE CANARY — proof, not report

*The idea I held back in my seat file, where it survives as one
honest sentence of residual risk: "a service that reports itself
healthy while failing to block an individual app." Every check in
§1–§2 asks the blocking stack how it feels. This one tries its door.*

### Beauty case, in plain words

There is a difference between a lock that says it is locked and a
lock you have rattled. The rattle is not paranoia — it is the oldest
gesture of trust there is: you pull the handle of the door you just
closed, every night, not because you doubt yourself but because the
pulling *is* the knowing. Cars learned this: the bulb check does not
ask the lamp circuit for a status flag, it *lights the lamps* — it
exercises the actual path that will have to work.

Monolith's BLOCK cell currently believes a self-report. A service
can be running, enabled, cheerful — and dead at the one job: a stale
enforced set, a foreground-detection path broken by an OEM update,
an intercept that arrives 900ms late, which is to say a peek sold as
a block. §1 and §2 make that self-report beautifully rendered,
freshly stamped, and grammatically perfect. It is still a report.
The beyond is to make the panel's most important word — `ON` —
something the machine *did*, not something it *said*: once per Deck
arrival, and once per press, the machine attempts to open a door it
has barred, and watches it refuse. A panel that has tried its own
seal, seconds before asking him to trust it with fifty minutes, is
not safer-looking. It is *safer*, and the beauty is that the two are
finally the same thing.

### The proposal

**A sacrificial test target — the Canary — exists only to be
blocked, and the BLOCK check is redefined as its interception.**

- **The target:** a zero-window activity in a dedicated test package
  (or a dedicated activity in the main package, exported under a
  test action — build's choice, one constraint: it must travel the
  *same* foreground/intercept path a real blocked app travels, or it
  proves nothing). It renders nothing (no-display theme), and if it
  ever reaches the foreground it writes one timestamped fact —
  `CANARY BREACHED` — and finishes within one frame (<16ms), never
  painting a pixel.
- **The test:** dispatched with §1's press-poll and with the standing
  Deck-entry poll. The Shield service itself requests the canary's
  launch (system-side, exempt from background-start limits that bind
  ordinary apps). Three outcomes, three epistemic states — and this
  distinction is the item's real contribution, applied back across
  the whole BLOCK grammar:
  1. **Intercept event fires within 300ms** (the accessibility/
     foreground path sees the canary and the shield turns it back):
     BLOCK is **proven** — end-to-end, launch→detect→block, the full
     chain exercised, not sampled.
  2. **`CANARY BREACHED` lands** (the target reached the foreground):
     BLOCK is **OFF** — positive evidence of failure. Not a timeout,
     not an inference: the door opened.
  3. **Neither within 800ms:** BLOCK is **`UNKNOWN`** — the test
     itself was inconclusive (canary package missing, launch
     swallowed by an OEM battery manager, service too wedged to
     answer). Silence is not evidence, and under this grammar
     *absence of evidence can never print as failure or as pass.*
     This rule now governs the plain queries too: `OFF` requires a
     positive dead signal (service-disconnected callback, disabled
     flag read live, or a breach); a query that merely fails to
     return is `UNKNOWN`. My filed spec timed out to UNKNOWN but let
     "service not running" reads blur toward OFF; the beyond makes
     the epistemology explicit and uniform.
- **The proof grade is printed.** The stamp row gains its right-hand
  mark, plate 10sp, sharing the seam with the age stamp:
  `PROOF: END-TO-END` on a canary pass · `PROOF: SELF-REPORT` when
  the canary was unavailable and BLOCK rests on the §1 queries alone.
  The panel now states not only its verdict but the *quality of its
  evidence* — a thing no warning light in any car has ever dared,
  and the single most truthful line this layer can print. A
  SELF-REPORT morning still arms (the §1 queries are real evidence,
  and HPA-03's world runs on them) — but it arms *labelled*, and a
  SELF-REPORT stamp that persists across days is a visible,
  actionable fact for the rested user, not a silent downgrade.
- **Pollution firewall, non-negotiable:** canary launches and
  intercepts are tagged at the source and **excluded from every
  deflection count, receipt line, and Ledger record of his
  behaviour.** The machine's self-test must never mint a deflection
  he didn't make or a fact about him at all. Canary *breaches*, by
  contrast, are machine-fault events: they file exactly where Opal's
  chain-jam events file (his ⚑ D1 home — the rebuilt Ledger tab's
  machine record), because a seal that was tried and failed is a
  fact about the machine that must survive the morning.
- **Cadence discipline:** the canary runs on Deck entry and on
  press-poll — the two moments §1 already dispatches. It never runs
  mid-session (launching test targets inside a seal is sabotage of
  the thing being protected), never on the 20s standing cadence
  (entry + press is fresh enough; a canary every 20s is a machine
  rattling its own door all morning, and the rattle would train the
  intercept path's logs — and any watcher — into noise).

### Technique spec

- **Timings:** launch request at dispatch +0ms, parallel with §1's
  queries. Intercept expected **80–300ms**. Breach write, if it
  happens, <16ms from foreground. Canary deadline **800ms** — inside
  §1's 1500ms query deadline, so the Park's worst case (§1) does not
  move by a millisecond.
- **Interaction with the Park:** a press whose queries pass but whose
  canary is still out waits in the Park for the canary's verdict or
  its 800ms deadline, whichever first. Canary `UNKNOWN` at deadline
  **does not refuse the arm** — it downgrades the stamp to
  SELF-REPORT and the seal proceeds on the queries' pass. Refusing a
  seal because the *test* failed would be the test impersonating the
  system — the precise lie this item exists to kill, run in reverse.
  Only a **breach** refuses, via the §2 interlock, sentence as filed
  for OFF — a breached canary *is* blocking being off, proven.
- **Haptics:** none of its own. A breach discovered at press-poll
  rides the interlock knock (16ms @ 1.0); a breach discovered at
  standing entry flips the cell silently per §2. The self-test must
  be inaudible in the hand, or he will start flinching at his own
  panel.
- **Sound:** silence, total, as everything in this layer.
- **Form:** no new element on the Deck. The canary changes what the
  BLOCK word *means* and adds nine plate characters to a stamp row
  that already exists. Zero dp, zero new tones, zero orange.

### ARM cost

- **Serial: 0ms on the drag path; 0ms added worst-case on the tap
  path** (the 800ms canary deadline nests inside the 1500ms deadline
  §1 already charges; a Park that would have resolved at 1500ms
  still resolves at 1500ms).
- **Parallel: 80–300ms typical**, fully inside travel or press. The
  cost that is real is not time: it is a second installable (or an
  exported test activity) and a permanent maintenance obligation —
  priced in the anti-case below, where it belongs.

### HOLD / TRUTH check

- **HOLD:** pre-seal only, by construction; the cadence discipline
  above is the enforcement, and it is stated as a rule, not a hope:
  *no canary launches while sealed, ever.*
- **TRUTH:** this item closes the filed spec's own named residual
  risk — the self-reporting zombie service — and it does so without
  minting a single user fact (firewall above). Its truth risk is the
  inverse one, also named: a canary path that diverges from the real
  app path (different package, different launch flags) proves the
  wrong thing confidently. The one-constraint spec (same foreground/
  intercept path) is load-bearing; a build that cannot honour it
  must not ship the grade `END-TO-END` — it ships SELF-REPORT
  forever, printed, which is still more truth than the filed spec
  has.

### What it replaces, and why that loses

- **The §1 BLOCK query as the seal's whole evidence** (service
  enabled + service running): loses because both halves are the
  system grading its own homework. Prima's audit already found the
  shipped app arming on state the seal never verified; a zombie
  service is not a hypothetical in this codebase, it is the family
  business. Self-report stays — as the fallback grade, labelled.
- **Opal's per-link CONFIRMED cascade as the only proof layer:**
  Opal proves each *app* was commanded and confirmed shut at seal
  time, link by link — the strongest filed proof in the plan, and it
  still shares one blind spot with my queries: every CONFIRMED
  arrives over the same reporting path whose health is the question.
  The canary is not a rival to the cascade; it is a proof of the
  *path the cascade's proofs travel*, run before the cascade is
  believed. If Opal's t=0 is his pawl, the canary is what entitles
  the pawl's drop to start a clock. (This dependency is in my letter
  to him — his frame, my evidence feeding it.)

### Strongest case against this proposal

It is, I concede in advance, *a second app whose only job is to be
refused entry to the first* — and that sentence contains the whole
attack. It can be uninstalled, battery-killed, or broken by the same
OEM update that broke blocking, and each of those failures lands as
`UNKNOWN` or SELF-REPORT on healthy and sick mornings alike,
indistinguishably — so the grade I print to increase trust could
instead teach him that the panel cries `SELF-REPORT` for a week
after every system update, until the stamp becomes wallpaper and the
one morning it says BREACHED he no longer reads stamps. There is
also a darker failure: a no-display activity is a *claim* about OEM
behaviour, and on the wrong skin the canary flashes — one frame of
black at 05:00, mid-press, reading exactly like the app crashing at
the moment of commitment. That flash, once seen, cannot be unseen,
and it would attach itself to the seal forever. And the maintenance
is forever: two packages drifting apart across versions is how a
proof quietly becomes a ritual — the canary still intercepted, the
real path long changed, `END-TO-END` printing over a test that no
longer tests anything, which is worse than SELF-REPORT because it is
*confident*. My honest answer is the gate, Old Tom's method, same as
Sana's D3: the canary ships behind a device trial — if it flashes
once, if its path cannot be kept identical to a real app's, if its
UNKNOWN rate on healthy mornings exceeds rare — it dies, the stamp
prints SELF-REPORT permanently, and §1–§2 stand complete without
it. I file it anyway because the filed alternative is to spend the
whole app believing a service's word about itself, and somebody in
this studio should have to argue *against* trying the door, in the
open, instead of the app never trying it at all.

---

## Closing — dependencies and letters

- **t=0:** my crank-release zero is withdrawn (head of this file).
  All timings above are anchored to t-down / t-intent / verdict
  frames, none of which I claim as the session's birth. If Opal
  names the proof-pawl drop as t=0, §1's Park becomes, in his
  vocabulary, the interval between intent and proof that the strip
  renders — and Item 3 is the evidence his pawl is entitled to drop
  on. Mailed: Opal, thread `t-zero-retiming`.
- **Press-as-poll:** taken, and specced in §1. Its collision with
  Fen's 120ms dwell (the Park *is* the dwell when it lasts ≥120ms;
  no double dwell is charged) is mailed: Fen, thread
  `press-as-poll`.
- **Ledger:** nothing above depends on the tab's form. The one
  record-shaped fact this file creates — a canary breach — is a
  machine-fault event and files wherever Opal's chain-jam events
  file under Sarthak's KEEP-THE-TAB verdict; it is never a fact
  about the user, and the deflection firewall (§3) keeps it that
  way.
- **Inbox:** read at start — two citation-lift relays (Orson, Mara),
  already in `read/`. Re-checked before finishing: two new letters
  had arrived (Fen 06:05, Opal 06:06) — both read, both answered in
  the Amendment appended below the Closing and by reply mail, both
  moved to `read/`.
- **Wall:** signed on completion.

*— Rosa, Wild (cars), Phase 3. The panel can say UNKNOWN, the
interlock can refuse, and now the lock has been rattled. Argue with
any of it on the wall — the anti-cases are written to be used.*

---

## Amendment — two letters, answered in the file

*Appended in the same run, before signing, on two letters that arrived
while this file was being written: Fen (06:05, thread
`interlock-moves-to-the-catch`) and Opal (06:06, thread
`clock-zero-t0`). Where this amendment names a coordinate, it
supersedes §1–§2 above; everything else in those sections stands.*

### A1 — t=0, named by Opal, adopted and re-timed against

Opal's definition (his file, beyond-deck-parts/opal.md §1), carried
verbatim: **t=0 ≡ the PROOF-SEAL FRAME — the first vsync frame the
controller executes PROVING → SEALED-RUNNING, i.e. t0 = max(last link
CONFIRMED-SHUT, latchTime + 210ms, pawl-seated frame).** Session
record, startMonotonic sample, readout, and seal sound attach to that
frame and no other. Tap commits run the identical sequence.

My layer re-times against it, exactly:

- t-down → checks dispatch (§1). t-intent (gate crossed / finger-up)
  → nothing is born. My Park (§1) resolves on BLOCK's verdict; a pass
  is the **seal-invocation frame** (Fen's name for it, adopted:
  callback + cascade dispatch — a distinct, named frame that is
  *not* t=0). Opal's cascade then proves, and t=0 lands at his
  formula. The interval my Park used to call "waiting for the seal"
  is, in the merged vocabulary, intent-complete → invocation →
  proving → t=0, and not one frame of it is counted as session time.
- **ARM re-priced to t=0, honestly:** drag path — §1's ≈1.2s typical
  to t-intent, plus proving (cascade 0.6–1.1s typical per Opal's
  budget, 210ms latch floor inside his max()) ≈ **2.0–2.3s typical
  cold-open-to-t=0**; worst seal case ≈ 1500ms Park deadline (BLOCK
  returning at the wire) + 1.8s cascade ceiling ≈ **3.3s**. Tap path
  — same proving tail after invocation; my serial Park remainder
  (§1) is unchanged and still nests ahead of it. Refusal cases have
  no t=0 and mint nothing, as filed. All well inside the 10s wall —
  and the ≈2.5s figure in WILD-PLAN §1.4, which priced my layer as
  if it *were* the seal, is superseded by these numbers wherever the
  merge quotes a total.
- Opal's §2 convergence, recorded and adopted: the damped zone's
  visual lag is driven by my checks (6dp × pending-required /
  total-required), and **BLOCK-pending holds the cap at the detent**
  — that hold *is* §1's Park, drag door, in his rendering. Two files,
  one mechanism, no duplicate: the Park's exit haptics remain §1's.

### A2 — The interlock's coordinate: Fen moves it, Opal holds it, the evidence splits it

Fen moves the interlock deadline from 0.65 to the catch (displayed
0.84): at 0.65 the checks get half a gesture, and a healthy-but-slow
check can be refused by the clock instead of by the facts. Opal's
letter, same hour, holds the other end: BLOCK-fail stops the cap at
0.65 pre-latch, so a dead-blocking arm never reaches proving — the
interlock pulls the pawl by never letting it be asked to drop.

Both are right — about different evidence. Item 3's grammar supplies
the split, and I rule it here as this layer's final coordinate:

- **Positive evidence of death (BLOCK returns OFF): the stop is at
  gate −0.03 (0.65, Fen's gate arithmetic as filed), pre-latch,
  pre-flood.** Opal's reason is decisive: a stack *proven* dead must
  never seat a latch, never flood COMMIT orange, never dispatch a
  cascade that would jam link-by-link to rediscover one known fact.
  The refusal renders here — interlock knock 16ms @ 1.0, the OFF
  sentence, return spring — exactly as §1–§2 filed.
- **Absence of evidence (BLOCK still out, or UNKNOWN at deadline):
  the cap travels to the catch and parks in Opal's SEATED-PROVING** —
  Fen's coordinate, Fen's rendering, adopted whole: sled held seated,
  no thock, no clock, no record; the checks keep their full parallel
  run and their **time** deadline (1500ms from t-down — the deadline
  was always a duration, and Fen is right that it was wearing a
  position's clothes in my filed spec). At deadline without a pass:
  refusal from the seat — Fen's dull knock **10ms @ 0.40**, a 300ms
  hold, then return on the universal spring — with §2's second
  sentence, which Fen's letter explicitly invites this layer to
  supply: the timeout case reads **`BLOCKING UNVERIFIED — NOTHING
  WILL BE HELD.`**, the proven-dead case reads **`BLOCKING IS OFF —
  NOTHING WILL BE HELD.`** Two facts, two sentences, one slot.
- **The tap path inherits the split unchanged:** a tap whose press
  returns OFF refuses at the up frame (§1, case 2 — the tap's
  equivalent of the pre-latch stop: nothing was seated); a tap still
  waiting at finger-up parks (§1's Park), and its park *is*
  SEATED-PROVING rendered at press depth — if the deadline resolves
  it UNKNOWN, the refusal is Fen's seated refusal (dull knock, the
  UNVERIFIED sentence) rather than §1's hard-stop knock, because the
  cap was, in every way that matters, seated. §1's Park-entry tick
  (5ms @ 0.4) stands; §1's Park-refusal knock is superseded by this
  paragraph for the UNKNOWN case only, and stands for the OFF case.
- Fen's underlying charge — *a healthy check refused by the clock* —
  is answered by the deadline's nature, and I state it so the merge
  cannot lose it: **no position in this layer refuses anything.** A
  position can only *hold* (0.65 for a corpse, the catch for a
  question). Refusals are issued by exactly two things: a positive
  OFF return, or the 1500ms clock expiring. A slow healthy check is
  held, never refused, at whichever coordinate its evidence earns.

*— Amendment signed, Rosa. Letters from Fen and Opal read, answered
here and by reply mail, and moved to my `read/` folder.*
