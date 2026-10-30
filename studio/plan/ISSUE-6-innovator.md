# ISSUE #6 — INNOVATOR — THE DECK, BEYOND THE ALTERNATIVE
## Cell: Rost (lead) · Innovator seat · Fri Oct 30, 2026
## Status: DECLARED, paper only. Nothing compiled. Nothing committed (Rost commits).

Read first, as briefed: `AUDIT-deck.md` whole (22 items, Rost's
verdicts stand — nothing below re-opens a ruling) and
`TactileDeckScreen.kt` whole (1,069 lines). Machinery checked
against the tree before pricing: `WatchStore.printState` exists,
`MonolithDao.getSessionHistoryFlow()` exists, the wheel and slide
are Canvas views in `BlockitCoreViews.kt` already holding a
`TactileSoundEngine` reference, and small standalone stores
(`DayWordStore`, `LoadoutStore`) are an established pattern.

**Boundary honoured:** the top status strip / header Guard line
is issue #4's cell. No proposal below touches it, rewords it, or
re-homes it. Where a mechanism governs "every rendering of the
blocking truth," it governs **Deck-owned surfaces only** — the
host strip is explicitly out of its claim space, and the
mechanism is designed to work even if #4's cell never adopts it.

**Where the adopted alternative stops.** "One Question Per State"
(Plan §7) + "Fusion" (Vega A) rearrange the room: facts get one
home each, gated by the current state, and the dial becomes its
own clock. That is a better room. But all three diseases are
*regenerative*, and a rearrangement does not touch the
regenerator:

1. **Six prints of one truth** will grow back, because the law
   against it is enforced by comment and review (Vega's U-3,
   charged in the audit itself). The seventh rendering is one
   tired merge away.
2. **43% permanent chrome** is priced from *today's* state map.
   State-gating answers "is this fact live now?" — it never asks
   "has this fact earned standing room over the last month?"
3. **21 dead parameters** are a symptom of a contract shape that
   *invites* dead wires: one flat 61-parameter signature through
   which the host must spray the whole VM at every state. Cut
   the 21 (F1) and the shape grows 21 more.

The seven proposals below attack the regenerators. Each names
the mechanism in plain words, what it replaces, a spec sketch
(states / inputs / outputs), and a price. They compose — P1+P2
are one lane, P3 is independent, P4–P7 each stand alone — but
none depends on another to be worth building.

---

## P1 · EDGE-TRIGGERED TRUTH — print the change, not the condition

**Mechanism, plain.** The Deck currently renders the warrant as
a *level*: every frame re-asserts the standing condition, in six
places, forever. Switch it to an *edge*: the Deck speaks about
blocking only when the truth **changes** — wire proved, wire
dropped, wire went stale — or when a fault is live and
unresolved. Steady health is not news and prints nothing. A
change gets one utterance, in one place, in the present tense,
with its clock time (`BLOCK DROPPED · 14:02`); when the next
frame confirms the new state is the standing one, the utterance
retires into the imprint's tense mark, which is where history
already lives. Faults are the exception by design: a fault
utterance **latches** and does not retire until the wire heals —
edge-triggered for health, level-triggered for harm.

**What it replaces.** The standing renderings, as a class: the
proof strip's BLOCK cell as a permanent object, the seam stamps,
the standing refusal sentence (it becomes an event utterance
that promotes on fault, per the audit's own Muede ruling), and
the frozen-vs-live tense juggling in the current code
(`blockCellFor(frozenState)` vs the live OFF override — an
impossible frame the current architecture had to patch by hand,
UNA2-04). Under P1 that patch is unrepresentable: there is one
utterance, stamped at its edge, and its tense is its timestamp.

**Spec sketch.**
- *Inputs:* `deckWarrant` as a Flow, passed through
  `distinctUntilChanged` on (state, a11yBound, usage) in the VM;
  `WatchStore.printState` freshness verdict (already exists);
  `armPhase`; active session id.
- *States:* `SILENT` (steady state, no utterance) → `EVENT`
  (one utterance, ≤ one settle beat after the edge, or until
  the next recomposition confirms the new steady state) →
  back to `SILENT`; `FAULT-LATCHED` (entered on any edge into
  OFF / stale-past-threshold while sealed; exits only on an
  edge back to freshly LIVE).
- *Outputs:* a single `TruthUtterance(text, density, edgeMonoMs)`
  consumed by exactly one slot — the proof drawer's mouth when
  open, the imprint's tense mark when retired. No other Deck
  surface reads the warrant for print at all (that half is P2).
- *DB:* none. The edge detector is a VM-side Flow operator;
  the last-edge stamp can live in the existing freeze record's
  neighbourhood in VM scope, exactly as `SealFreeze` does today.

**Price.** **M** — one lane: VM Flow reshaping + one utterance
slot + deleting standing render paths (deletion-heavy, which is
the cheap direction). **Risk: medium-low.** The failure mode is
a missed transient (an edge that fired and retired while he
wasn't looking). Mitigations are structural, not hopeful:
faults latch, and the retired utterance remains readable in the
imprint's tense mark for the session's whole life — the record
is never lost, only demoted. Needs one device pass to tune the
retire beat; paper cannot settle that number.

---

## P2 · THE TRUTH TOKEN — one mouth, enforced by the type system

**Mechanism, plain.** Give the blocking truth a physical rule
inside the code: it is a **token that exactly one Deck surface
may hold per frame**. Surfaces don't each read the warrant and
print their own dialect; they *request the token*, in a fixed
priority order, and only the holder may render warrant words.
Losers render nothing — not a dimmer copy, nothing — and their
space collapses or is inherited by the holder. Priority follows
the audit's own law (fault gets more room than health): fault
utterance > proving drawer > slide (during a press) > imprint.
The four dialects die at the same stroke, because the token
carries the words with it: there is one vocabulary function,
`speakWarrant(state, tense)`, and the token is the only way to
obtain its output. A new surface that wants to print "BLOCK…"
has to steal the token visibly in review — duplication stops
being an accident and becomes a deliberate, diff-visible act.
This is U-3's cure: the one-fact-one-rendering law stops being
a comment and becomes a type.

**What it replaces.** The independent derivations scattered
through the current body: `blockCellFor`, `idleBlockCell`,
`seamMarksFor`, `imprintTenantText`'s warrant branch, and
`refusalSentenceFor` each computing their own print from the
raw wire. Under P2 they receive either the token (words +
density, pre-spoken) or `null`.

**Spec sketch.**
- *Inputs:* warrant print state (post-freshness), `armPhase`,
  `pressLatch != null`, session running flag.
- *States / arbitration:* a pure function
  `claimTruth(snapshot): TruthClaim` evaluated once per
  composition at the Deck's root: `Fault(sentence)` |
  `Proving(drawer)` | `Press(slideLatch)` | `Record(imprint)` |
  `Unclaimed` (healthy idle — nobody speaks, which is the
  correct sentence for health).
- *Outputs:* the sealed `TruthClaim` passed down; each surface's
  warrant parameter type becomes `TruthClaim.<ItsOwnVariant>?` —
  the strip-shaped surfaces literally cannot receive words that
  belong to the imprint's variant.
- *Vocabulary:* one `WarrantVocabulary` object owns every
  warrant string on the Deck. A grep gate (script, CI or
  pre-merge, same family as the existing ESR greps) fails a
  merge that introduces the literals `BIND`/`PROOF:`/a second
  `BLOCKING` string outside that object.
- *DB:* none. Pure presentation-layer restructure.

**Price.** **M**, and it rides free inside P1's lane if the two
land together (same deletions). **Risk: low.** Worst case is an
arbitration order dispute — which is a one-line priority list,
argued once on paper, not a behaviour that emerges at runtime.
Composes with, but does not require, P1: P2 alone kills the six
prints; P1 alone kills their permanence. Together they kill
both the chorus and the standing shout.

---

## P3 · THE MODE-TYPED CONTRACT — dead wires become unrepresentable

**Mechanism, plain.** F1 cuts 21 dead parameters. The disease
is the signature that grew them: one flat list of 61 parameters
through which MainActivity sprays the entire ViewModel at a
screen that, in any given state, can use perhaps fifteen. The
cure is to make the host pass **the state, typed**: the Deck's
entry point takes one sealed payload per mode — `DeckSet`
(idle: dial, blocks, task ghost, echoes, arm verb), `DeckProving`
(frozen hero facts, drawer facts), `DeckRunning` (session,
readout source, STOP verb, utilities verbs), `DeckReceipt`
(the slip's facts). A checklist, a profile list, an intention
field *cannot be passed to the idle Deck*, because the idle
payload has no field shaped to hold them. Dead parameters stop
being a cleanup chore and become a compile error at the host.
Add the graveyard gate: a unit test (plain JVM, no device) that
reflects over the payload classes and fails if any field is
never read by its mode's composable — the audit, automated, run
on every merge instead of once a season by a six-seat crew.

**What it replaces.** The 61-parameter `TactileDeckScreen`
signature and its host call site; F1's one-time cut becomes the
first payload harvest instead of a standalone edit. Also
replaces the default-parameter camouflage the current signature
relies on (`onEndSafeBreak = {}`, `warrantProvider = { …UNPROVEN }`,
sled seams defaulted to no-ops) — defaults are how dead wires
learned to look alive; payloads carry real values or the mode
doesn't compose.

**Spec sketch.**
- *Inputs:* the VM exposes three/four small state flows (or
  one `DeckUiState` sealed flow) instead of the screen pulling
  from a wide snapshot; MainActivity maps VM state → payload.
- *States:* the sealed type *is* the state map — `DeckSet`,
  `DeckProving`, `DeckRunning(session, inBreak: Boolean)`,
  `DeckHeldFinish`, `DeckReceipt`. The screen's internal
  `isSessionRunning / provingThisSession / heldFinish` boolean
  algebra (three flags, eight combinations, several impossible)
  collapses into the payload's type.
- *Outputs:* unchanged pixels. This is a contract restructure;
  any visual change during its landing is a bug by definition.
- *DB:* none. DB v12 untouched.

**Price.** **L** — the largest item in this file: signature,
host, previews/render harnesses, and every test that seats the
Deck must be re-seated on payloads. **Risk: medium** — wide
blast radius, but mechanically checkable at every step (the
screen either composes from its payload or it doesn't), and it
is the only proposal that makes the other six *stay* fixed:
P1/P2's single utterance, the drawer's gating, the ghost task
line all become payload-shaped facts instead of conventions the
next lane can quietly widen. Sequence it after F16+F18 land (the
alternative's skeleton defines the modes; the payloads then
freeze them).

---

## P4 · EARNED SILENCE — chrome that pays rent in proved days

**Mechanism, plain.** State-gating (the adopted alternative)
asks: is proof live business *right now*? It never asks the
longitudinal question: this phone has sealed 40 sessions in a
row on a wire that never dropped — why does the Deck still
pay the full ceremony of a phone that has never proved
anything? **Earned Silence** gives the Deck a trust ledger.
Every clean seal (proved LIVE, no mid-session wire drop, no
valve use) earns credit; every fault, jam, or valve spends it
back to zero. At full trust the Deck runs *quieter than the
alternative's idle*: the proof drawer doesn't merely stay shut,
it is not composed; the imprint's tenant thins to the count
alone; the proving beat at seal compresses to the slide's own
travel (proof still happens — it is simply not staged). At
zero trust — first week, or the morning after a fault — the
Deck runs the audit's full ceremony, every print standing.
Trust is always inspectable and always overridable: a
long-press on the imprint forces the full proof staging for
that visit, regardless of the ledger. The screen literally
gets quieter as the machine earns it, and loud again the day
it hasn't.

**What it replaces.** The binary state-gate on the proof drawer
(F16) becomes a three-level density dial — `CEREMONY` /
`STANDARD` / `QUIET` — driven by history, not just the instant.
It also replaces the assumption baked into the pinned stack:
that permanence is free. Under P4, permanence is the thing a
fact *buys* with proved days.

**Spec sketch.**
- *Inputs:* seal outcomes (the cascade already knows them:
  sealed-LIVE, jammed, annulled), mid-session wire drops
  (P1's edge detector sees them), valve uses (VM knows).
- *States:* `Trust(counter, level)` — level derived:
  0 clean seals = `CEREMONY`; 1–13 = `STANDARD` (the audit's
  alternative, unchanged); ≥14 consecutive clean = `QUIET`.
  Any fault edge resets counter to 0.
- *Outputs:* a `deckDensity` value threaded to the pinned
  stack and drawer only. Wheel, hero, slide geometry never
  change with trust — the instrument is not the thing being
  trusted less carefully; the *staging* is.
- *Storage:* a tiny standalone store in the established
  pattern (`DayWordStore` / `LoadoutStore` family — a counter
  and a last-fault stamp), **not** a Room table. DB v12 stands.

**Price.** **M** (VM outcome hooks + store + density threading).
**Risk: medium, and it is the honest kind** — the failure mode
is a user who *wants* the ceremony on a phone the ledger trusts.
That's why the override is a physical gesture on the imprint
itself, and why `QUIET` never removes the fault path: P1's
latched fault utterance ignores density entirely. If Rost
judges the appetite wrong, the whole mechanism degrades
gracefully to the audit's ruling by pinning the level at
`STANDARD` — one constant.

---

## P5 · FELT TRUTH — the hand learns the wire before the eye reads it

**Mechanism, plain.** The Deck's job sentence is ordered: *set
the session, see that the machine can hold it, seal it.* The
screen currently performs step two entirely in print — which is
why print about it metastasised to six surfaces. Move step two
into the **setting gesture itself**. The wheel already clicks
through a `TactileSoundEngine`; give the wire a feel: when the
warrant is freshly LIVE, the wheel's detents land with the
normal knock; when it is UNPROVEN/stale, every detent knock
drops in pitch and the wheel's rotational resistance (the
view's existing fling/drag physics) goes faintly gritty — the
dial feels *unsure in the hand*; when the wire is OFF, the
wheel still turns (he may still mean FLOW, still mean to look)
but the slide at the bottom answers with the refusal stiffness
the audit already acquitted ("truth you can feel," Fremd).
One felt channel, three coarse classes, never a continuous
scale. The eye then needs exactly one printed token (P2) as
the record; the other five prints were the screen shouting
what the hand can now know mid-gesture, before the slide is
even touched.

**What it replaces.** The *standing* need for visual warrant
redundancy at idle: the BLOCK cell's healthy-state print and
the seam stamps' reason to exist (PARK keeps the detail; the
Deck keeps the token). It extends Vega B (the slide carries
the proof) backwards into the set gesture — Vega B priced the
slide; P5's first surface is the wheel, which he touches
first, every time, including at 1 AM with eyes half shut.

**Spec sketch.**
- *Inputs:* warrant print state (freshness-checked, as today)
  mapped to an enum `WireFeel { SOLID, UNSURE, REFUSED }` —
  three classes, deliberately no more.
- *States:* feel class is a pure function of the print state;
  it changes only at warrant edges (P1), never mid-drag except
  on a genuine edge, and a mid-drag change applies at the next
  detent, never mid-detent.
- *Outputs:* `RotaryWheelView.wireFeel` (knock pitch class +
  drag resistance multiplier — the view already owns both
  physics and sound seams) and `SlideToStartView` spring
  multiplier at idle. Running state: feel channel sleeps; a
  sealed session's wire drop is P1's latched print + the
  existing interrupt path, not a vibration gimmick.
- *Accessibility:* the felt channel is *redundant by design* —
  P2's printed token always stands, eyes-free semantics (F22)
  announce the same three classes in words. Feel removes
  duplication; it never becomes the only carrier.
- *DB:* none.

**Price.** **M** — view-level physics/sound parameters + the
enum mapping; no host change beyond passing the class the
Deck already derives. **Risk: medium** — subtlety is the whole
gamble: too faint and it's a placebo, too strong and the dial
feels broken. Cannot be settled on paper; it needs one device
pass with three pitch/resistance steps and Sarthak's hand as
the judge. Priced honestly as the proposal most likely to be
killed by its own proof — and worth the attempt, because it is
the only one that deletes a *sense's worth* of print instead
of rearranging print.

---

## P6 · THE DARK DECK — a running session is a locked door, not a dashboard

**Mechanism, plain.** The adopted alternative keeps the running
Deck a furnished room: hero, ghost title, DONE, utilities,
HELD, the chair — for sessions that run *hours*, while the
idle Deck is visited for *minutes*. That ratio is upside down.
The running Deck's furnishings are a standing invitation to
fiddle with the phone the session exists to keep him off.
Invert it: **sealed means dark.** The running Deck's resting
form is the Fusion hub readout and the STOP chair — nothing
else composed, nothing else lit. Everything else (task DONE,
DESK CLOCK, HELD, end-break) lives behind one deliberate
**peek**: a 600 ms press-and-hold on the hub raises the
furnishings for exactly 5 seconds (a visible draining rim on
the hub, Vega's ink running backwards), then the room darkens
again on its own. The break is the ruled exception: during a
Safe Break the furnishings stand lit for the break's whole
life, because the break is the one running interval where the
phone is legitimately in his hand — the current design already
half-knows this (END BREAK sits in the utilities' room); P6
finishes the thought by making the break the *only* lit room.

**What it replaces.** The standing utilities row and standing
task line in running state (Section 3 and the running half of
Section 2 as permanent composition). DONE does not move to
Tasks — it moves behind the peek, one hold away, which at
1 AM is still fewer decisions than the current furnished room
presents. It also replaces the settle-then-furnished beat
after the seal shot: the shot now lands on the dark room,
which is the vertigo's correct destination — the room doesn't
recede and then hand him a dashboard; it recedes and *closes*.

**Spec sketch.**
- *Inputs:* session state + break flag (both already derived);
  peek gesture on the hub; a 5 s countdown driven by the same
  frame clock family as the seal shot (elapsedRealtime).
- *States:* `DARK` (hub + chair) ⇄ `PEEK` (furnishings,
  auto-exit at 5 s or on any verb's completion) ; `BREAK-LIT`
  (furnishings standing, peek timer suspended) for exactly
  `isInSafeBreak`.
- *Outputs:* composition gate only. No verb changes its
  target, price, or wiring — GRACE/STOP, DONE, END BREAK all
  keep the audit's ruled forms.
- *DB:* none.

**Price.** **M** — one gesture, one timer, composition gates;
the furnishings themselves are untouched. **Risk: medium** —
the contested kind: it taxes DONE and DESK CLOCK with a hold.
The defence is the ratio (minutes of idle furnishing vs hours
of sealed darkness) and the break exception; the honest
uncertainty is whether DESK CLOCK's users open it mid-session
often enough to resent the hold. If the peek taxes wrong on
device, the fallback is a two-tier peek (DESK CLOCK promoted
to standing, rest behind the hold) — a one-line tier list, not
a redesign.

---

## P7 · SEAL ECHO — the wheel remembers what he actually seals

**Mechanism, plain.** Every setting session is currently an act
of arithmetic: wheel says 50, hero says 2H 30M, eyebrow (after
F4) explains why. But in practice he seals a small repertoire —
the morning block, the night span, the routine's shape. Give
the wheel a memory you can feel: the **last two distinct sealed
spans** (and the next routine span, when one exists) stand on
the tick ring as echo marks — a slightly longer tick, a
different knock when the detent passes over them, a soft snap
(within ±2 minutes) when he lands on one. Setting stops being
calculation and becomes *recognition*: the dial says "this one,
again?" in the hand. The eyebrow's recipe (F4) then only needs
to exist for genuinely novel spans — the arithmetic is
explained exactly when it's new, and silent when it's the
span he seals every day.

**What it replaces.** The standing recipe print for repertoire
spans, and part of the hub-cycle's discovery problem (block
count hidden in the hub — Muede's hidden control): an echo of
a 3-block span *shows* its block structure as grouped marks on
the ring, so the repertoire teaches the wheel's grammar by
example. Nothing else moves.

**Spec sketch.**
- *Inputs:* `MonolithDao.getSessionHistoryFlow()` — **already
  exists**; take the last two distinct (dialMinutes × blocks)
  pairs actually sealed, plus the next routine-derived span
  when the routine engine offers one.
- *States:* none of its own — echoes are derived data, recomputed
  when history changes, frozen during a drag except the snap.
- *Outputs:* `RotaryWheelView.echoMarks: List<EchoMark(minutes,
  blocks)>` — rendered as ring ticks in the scheme's existing
  tick ink at one weight up (tokenised per F17, no new colour),
  a distinct knock class on pass-over, snap magnetism ≤2 min.
  FLOW (dial < 10) carries no echoes.
- *DB:* none — reads only, from the existing history Flow.

**Price.** **S–M** — one DAO read, one view parameter, tick
rendering in a Canvas view that already draws the ring.
**Risk: low.** The ceiling on harm is two extra ticks; if they
read as clutter on device, the marks die and the knock/snap
can stand alone. The one real care: routine-born spans must
not masquerade as hand-sealed repertoire — mark routine echoes
in the sub-line print, per the audit's routine-naming ruling.

---

## WRONG-TARGET FLAGS — audit items aimed beside the disease

**F1 (cut the 21 dead parameters, priced S) — aimed at the
list, not the shape.** The audit's own evidence convicts the
framing: a comment in this very file celebrates an eviction
("one rendering per fact") that had only moved a fact from
three homes to two. Cleanups in this codebase decay. The 21
will regrow through the same flat signature that grew them,
host spraying the whole VM because the contract asks it to.
*Better target:* the contract itself (P3). F1 should land as
P3's harvest, not as a standalone S that declares the disease
cured at its cheapest symptom.

**F4 (eyebrow prints the recipe) — right words, wrong address.**
The unexplained arithmetic (Truth charge 6) is a crime
committed *at the wheel*: the hand sets 50 and a distant
numeral says 2H 30M. The cure is posted on the eyebrow — the
element furthest from the hand, at 10sp, in the screen's
quietest voice. Fusion (F18) already moves the span into the
hub with "+10 BREAK" as its sub-line; that *is* the recipe,
at the scene of the crime. *Better target:* F4's recipe should
be demoted to novel spans only (P7 covers the repertoire case)
and the hub sub-line should be ruled the recipe's primary
home; the eyebrow keeps routine-naming and break words, which
are genuinely eyebrow-shaped facts.

**F12 (unlight the utilities with a prescribed hex, #2A2320) —
prescribes the disease as the cure.** F17 exists because raw
hex in three files made the screen un-recolourable; F12 then
specifies the fix *as a raw hex*. Even tokenised at landing,
a luminance relationship ("controls sit one step below the
card") frozen into a single value will drift the first time a
look re-tones the card. *Better target:* Schein's own Lamp
Test, which the audit endorses as an instrument — rule the
*staircase ratio* (glyph > card > controls > ground, checked
mechanically on the three frames) and let each scheme's
tokens satisfy it. F12's hex is one scheme's answer, filed as
if it were the law.

**F22 (accessibility semantics, priced L, parked as "bar
debt" after the fix list) — right item, wrong sequence.** It is
priced and scheduled as a retrofit onto the wheel that F18 is
about to rebuild into the Fusion hub. Semantics bolted onto
the old wheel's Canvas will be re-derived, re-argued, and
partly discarded when the hub becomes the readout. *Better
target:* F22's requirements should be written into F18's
acceptance criteria — the hub ships with its name, value, and
actions as part of the Fusion landing, and the slide/rail
semantics ride F16's restructure the same way. The debt isn't
the work; it's paying for the work twice.

---

## RANKING, IF ONLY SOME OF THIS IS EVER BUILT

| # | Proposal | Disease it kills | Price | Regret if skipped |
|---|----------|------------------|-------|-------------------|
| P2 | Truth Token | six prints, four dialects | M | the chorus regrows |
| P1 | Edge-Triggered Truth | standing shout, tense patches | M | proof stays a board, not news |
| P3 | Mode-Typed Contract | dead wires, forever | L | every other fix decays |
| P7 | Seal Echo | setting as arithmetic | S–M | repertoire never becomes reflex |
| P5 | Felt Truth | a sense's worth of print | M | print stays the only carrier |
| P4 | Earned Silence | chrome tax, longitudinally | M | ceremony never earns retirement |
| P6 | Dark Deck | furnished running room | M | sealed hours stay lit & fiddly |

— INNOVATOR, Rost's cell, issue #6. DECLARED, uncompiled,
uncommitted. File: `studio/plan/ISSUE-6-innovator.md`.
