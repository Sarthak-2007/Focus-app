# FEEL-1 — Touch — Issue #12: Tasks + F-1

**Issue:** Sarthak-2007/Focus-app #12 — Tasks + F-1
**Role:** Feel-1 · touch (issue #12 cell)
**Status:** DECLARED — touch spec only, uncompiled, no code written, no Gradle/tests run
**Sources:** `studio/plan/AUDIT-tasks.md` (Amt's Tasks audit; Muede's 1 AM walk
is this seat's ground truth) and `studio/plan/PLAN-issue-12-tasks-f1.md`
(Amt, cell lead) in the cell clone on branch `agent/issue-12`

— Feel-1 · touch (issue #12 cell)

---

## 0. The hand this is specced for

Not a demo hand. The hand at ~1 AM, in the dark, phone held one-handed
from below, thumb doing everything:

- **The thumb is extended and drifting.** The head leaf sits far from the
  grip; a thumb reaching up-screen loses precision and drifts inward as
  it extends. Targets at the top of the pad must be wider than targets
  at the foot, not the same size and harder to reach.
- **The contact patch is fat and the aim is honest but loose.** A tired
  thumb lands a wide oval, not a point, and its centre can be a full
  word off from where the eye aimed. Any two live targets that touch
  each other will, some night, both be touched.
- **The thumb covers what it presses.** Feedback printed *under* the
  thumb is feedback never received. Every press state below must be
  visible *around* the thumb — whole plate, whole zone — or be felt.
- **It scrolls more than it taps.** Most contacts on this screen are
  drags through the queue looking for a leaf, or a resting touch while
  he reads. A tap target that cannot tell a scroll, a rest, or a brush
  from an intention is a trap laid in a walkway.
- **It double-fires.** A tired tap bounces. Any booking that can be
  doubled by a bounce — two leaves, a CUE whose second landing hits
  START — will eventually be doubled at 1 AM.

And the other appointment has no hand at all: **05:00, the phone sits
untouched** and the routine seals wearing the head leaf's name (F-1).
Touch's law for that hour is in §5: a seal that arrives with no hand on
the phone must leave no touch debt behind — no press state stored, no
gesture half-armed, no verb still warm under a resting thumb.

One law over the whole file, the audit's own: **booking precedes
everything (G-01).** The tear books at the tap; keys book at the tap.
Touch feedback is the receipt, never the gate.

---

## 1. Every tap target on the Tasks screen — the complete map

Sizes are minimums for the *hit rect*, not the printed word. A bare
word is print; the target is the ground around it a tired thumb can
land on. Universal rules first, then the table.

**Universal target rules:**

- **48dp is the floor for every verb's height**, and for the width of
  anything a thumb must hit while the phone is moving in the hand.
- **Inert gutters:** live targets never share an edge. Minimum 8dp of
  dead ground between any two targets; minimum 16dp between DEL and
  any commitment verb (TEAR / START / CUE), because DEL is the one
  landing that costs a leaf.
- **Slide-off cancels.** Press-down shows the press state (§4); if the
  finger slides off the target before release, the state restores and
  release books nothing and ticks nothing. A thumb that changes its
  mind mid-press has changed its mind — the screen must let it.
- **Press, release inside, book.** No verb in this pass books on
  press-down alone, and none needs a hold. (The tear's existing
  book-at-the-tap order is acquitted and untouched by this; "the tap"
  is the completed tap.)
- **No long-press targets anywhere on this screen.** No hidden menus,
  no press-and-hold verbs, no secret second layer. At 1 AM a long
  press is indistinguishable from a resting thumb.
- **Tap ≠ scroll ≠ rest.** Any contact that travels past the system
  touch slop with vertical dominance is a scroll from its first dp and
  can never mature into a tap on release. Any contact that simply
  rests books nothing, ever, however long it rests.

| # | Target | Where | Minimum hit rect | Notes for the 1 AM thumb |
|---|---|---|---|---|
| T1 | **Composer field** (blank leaf at the foot, F-2) | The pad's next blank leaf, full width | Full leaf width × ≥52dp | The largest target on the screen, correctly — it is the errand. A tap anywhere on the blank leaf's ruled body focuses it, cursor at the end of any existing text. Tapping the leaf must never require aiming at a line. |
| T2 | **ADD** (end-key on the composer leaf) | Right end of the composer leaf | ≥72dp wide × 48dp | §4 for its tick. A tap with a blank/whitespace title books nothing and ticks nothing — the field, not the key, is what's missing. This blank-guard is also the bounce-guard: a doubled tap cannot mint a second leaf, because the first booking clears the title. |
| T3 | **FIRST** (second key on the composer leaf) | Beside ADD, separated by a ≥12dp dead gutter; visually subordinate (Feel-5's ruling, adopted) | ≥72dp wide × 48dp | Same size as ADD, less loud. Equal size because a smaller FIRST would be missable on the one night it matters; subordinate *print* because ADD is the default full-stop. The gutter is non-negotiable: these two keys differ by where tomorrow starts, and a fat thumb must never be able to straddle them. If a press begins in the gutter, it is dead — it does not snap to the nearer key. |
| T4 | **TEAR** (renamed DONE, audit item 13) | Perforation end of the leaf's verb row | **48dp zone as ruled — specified here as ≥88dp wide × 48dp high**, right-anchored | The audit's 48dp is the floor, not the aim. TEAR is hit by the most extended thumb on the screen, so it gets the widest rect, anchored to the screen's right edge where the drifting thumb lands, with **no live target to its right and no live target within 16dp to its left**. Its pressed state darkens the perforation across the whole zone (§4), so the thumb sees the cut respond around itself. The ghost owns no input: during the 300ms tear window the zone is inert, and the successor's zone arms only when the successor lands as head. |
| T5 | **START** | Head leaf only, the screen's one accent verb | ≥80dp wide × 48dp | START must not inherit CUE's old footprint (below): after a promotion lands, the new head's targets arm only at landing, never mid-travel, so a bounced second tap on the CUE spot falls on a still-inert leaf, not on a freshly-armed START. |
| T6 | **CUE** | Queued leaves only — make this leaf the head, STAY on Tasks (item 14) | ≥64dp wide × 48dp | Absent on the head leaf (the leaf *is* the head; a dead CUE there would be a lie). Absent — not greyed — under seal (§5). |
| T7 | **DEL** | The far end of the verb row from TEAR/START, isolated by ≥16dp of dead ground on both sides | ≥56dp wide × 48dp | No confirmation — the undo banner is its safety net, and that bargain stands. But DEL never sits at the right edge (the extended thumb's first landing), never neighbours the tear, and its press state is visually distinct (§4): a leaf about to be *removed*, never dressed as a leaf about to be *finished*. TEAR and DEL sharing a press feel would share a meaning at exactly the hour meanings blur. |
| T8 | **SPLIT / HAND-SPLIT** | Verb row (F-6: opens blank ruled lines via `onAddSubStep`) | ≥64dp wide × 48dp | Booking = the lines open. After they open, each blank ruled line is itself a writing target: full leaf width × ≥48dp while empty, tapping it focuses that line alone. A half-asleep man adding his own step should never have to thread a 20dp line. Once a step is written and the line settles to print height, its touch contract changes to T9. |
| T9 | **Sub-step strike** | **The step's number only** — ruled in §2 | **48dp wide** centred on the number; vertical rect = the step row, extended over neighbouring *non-target print* up to the midpoint between numbers, nearest-number wins on overlap | Full ruling and reasoning in §2. Tapping the step's words does nothing. Tapping the number strikes (or un-strikes). |
| T10 | **UNDO** (undo banner) | Banner's verb | ≥72dp wide × 48dp (full banner height) | The banner is a 5,000ms window (audit item 3) and a 1 AM thumb spends the first of those seconds noticing it. The target is therefore as tall as the banner and wide enough to hit while still reading it. The target's size is the only mercy touch can give the window; extending the window itself is machinery, charged by Muede, and this seat seconds the charge in §6. |
| T11 | **Banner dismiss** | Banner's far end from UNDO | 48dp × 48dp | Wired by F-9 (`onDismissUndoBanner`). Separated from UNDO by dead ground; tapping the banner's *body* does nothing — a brush while reaching for UNDO must not throw the undo away. |
| T12 | **REOPEN** (LAST-FINISHED line) | That line's verb, unsealed only | ≥72dp wide × 48dp | Composed only while unsealed, as built (audit item 5). The line's print is not a target; only the verb is. |

**Explicit non-targets** (taps here are inert or scroll, and must feel
that way — no press state, no tick, no ripple):

- QUEUE header, its `N TASKS` count, and the `SEALED` word — read-only
  print. A header that depresses when touched teaches a false affordance.
- Leaf title, plate number `#NN`, sub-step *words*, LAST-FINISHED print.
- The blank leaf's `NOTHING QUEUED` ground (item 6) — a held address,
  not a button.
- The pad edge / stack lines (item 16) — paper, not a handle. Slip-Sort
  is an escalation for Ren, not this pass; until it is built and ruled,
  the edge must stay honestly inert rather than look draggable. A
  surface that feels like a handle and does nothing is a small lie
  told to the hand every scroll.
- The leaf body generally: it is the scroll surface and the reading
  surface. Only the verbs, the number (T9), and the composer's body
  (T1) take taps.
- A sealed leaf, entire — §5.

**Reach note:** the recomposition puts the night's real work — T1/T2/T3
— at the foot, inside the thumb's home arc, which is exactly right for
the 1 AM errand. The morning's verbs (T4/T5) sit high, where the thumb
is extended and sloppy; that is why T4/T5 carry the widest rects on the
screen. And ADD/FIRST must never be covered by the keyboard they summon:
the composer leaf and its keys ride above the keyboard, always in the
thumb's arc, or the errand ends with a hunt.

---

## 2. The sub-step strike — ruled: NARROW to the number

The audit (item 10) left the builder a choice: the strike gains the
tear's tick, **or** the toggle narrows to the step's number. Touch
rules, and the ruling is not a coin-flip:

**Ruling: narrow the target to the step's number (T9). The tick rides
along as confirmation (§4) but is not, and must never be shipped as,
the cure.**

**Why — in the hand, not in principle:**

1. **Feedback reports an accident; narrowing prevents it.** A tick on a
   full-width strike tells a half-asleep man, *after the fact*, that
   the leaf he brushed while scrolling has changed. At 1 AM "after the
   fact" means he must now stop, work out which step struck, and
   un-strike it — three acts of wakefulness bought by one scroll. A
   narrowed target means the scroll never strikes at all. Prevention
   is the only cure that costs no sleep.
2. **The full-width target sits in the scroll's path.** The queue is
   scrolled *through the leaves*. Every upward drag to find tomorrow's
   leaf crosses sub-step rows on the leaves it passes; tap/scroll
   discrimination saves most of these, but the resting-then-lifting
   touch — thumb down to read, thumb up to think — is a tap by every
   technical definition, and on a full-width row it is also a strike.
   He did not decide anything. The print changed anyway. Paper that
   changes under a resting hand cannot be trusted, and an untrusted
   pad gets re-read every night — the errand grows.
3. **The number is where intent already lives.** Nobody strikes a step
   by aiming at its middle. The hand goes to the step's mark — the
   number, the margin, the box-position — the way a pencil goes to the
   margin of a list. Narrowing does not ask the hand to learn a new
   place; it stops punishing every other place.
4. **Narrow is not small.** T9's rect is 48dp wide — a full thumb-width
   — centred on a small glyph, its vertical span quietly borrowing the
   dead air over neighbouring print (print is inert, so the borrowing
   costs nothing; where two numbers' rects overlap, nearest centre
   wins). The target he *sees* is quiet print. The target he *hits* is
   the biggest one the step can honestly offer. That asymmetry — quiet
   to the eye, generous to the thumb — is the whole trick of this
   screen, and the strike is where it matters most.

**Corollaries:**

- The step's words are inert (§1). If he taps the words and nothing
  happens, that is correct: the words are for reading.
- Un-strike is the same target, same tick register (§4). There is no
  second, harder gesture for undoing — a strike he made by accident
  through the number (rare, now) is undone through the number.
- Strike state must be legible at a glance *before* any tap: the drawn
  rule across the words (motion's province) is how he audits the leaf;
  touch's part is only that auditing requires no touching.
- Under seal, the number is as dead as every other verb — §5.

---

## 3. ZIP-TEAR — the house standard, specced for the hand

Status, per the plan, stated so this section cannot be misread as this
pass's build: **ZIP-TEAR is the ruled standard for the NEXT tear build**
(audit escalation #2, Vega, ~180 lines). This pass ships the F-7 respin.
What follows is the touch contract the next build must satisfy — how
the gesture *feels*, where it forgives, and where it refuses.

The ruled shape (audit; plan ruling 2): **the finger traces the
perforation in a 32dp band; dashes consume behind the finger; booking
at 85% crossing, in G-01 order; the tap (T4) retained as the
accessibility floor. The threshold is 85% — not 92%.**

### 3.1 Entering the trace

- **The band:** 32dp tall, centred on the perforation, running the
  leaf's full width. Entry is a touch-down inside the band.
- **Direction lock, first 12dp:** the first 12dp of travel decide what
  the gesture *is*. Horizontal-dominant → the trace claims the gesture:
  scroll freezes, pointer is captured, dashes begin consuming.
  Vertical-dominant → the gesture is a scroll, irrevocably for that
  touch; the band yields without consuming a single dash and without a
  tick. This lock is the accidental-tear protection that matters most:
  the band sits across every leaf, square in the scroll's road, and
  nine of ten touches that cross it are going somewhere else.
- **Direction of tear:** either end may start. Progress is *coverage* —
  the span of perforation consumed — not travel in one approved
  direction. A right thumb entering from the right edge tears right-to-
  left; the 85% is measured on consumed span, not on distance from the
  left.
- **A still finger tears nothing.** Holding a thumb in the band —
  reading, resting, thinking — consumes zero dashes, however long it
  sits. Consumption requires horizontal travel over intact dashes.

### 3.2 How the finger FEELS the dashes consume — the resistance illusion

A thumb cannot feel paper. What it can feel is a *rhythm against its
travel*, and the illusion of resistance is built from rhythm plus a
strain the eye can see just ahead of the fingertip — never from lag.
(Motion's law, adopted: consumption is 1:1 with the finger, zero lag,
zero lead. The tear front is never behind the finger; what bends is the
paper *between* breaks, by at most 2dp, visible ahead of the thumb
where the eye is already looking.)

- **One tick per dash, at the dash's break.** Each dash, at the moment
  the finger's leading edge completes it, fires one short haptic tick
  (§4's tear register). The ticks are spaced by the dash pitch, so the
  thumb feels the perforation's actual spacing as a fine roughness
  under its travel — the same information the eye gets from the dash
  pattern, delivered to the finger that is covering the pattern up.
  This is why the ticks exist: under the thumb, the dashes are
  invisible; the tick train *is* the perforation, felt.
- **The effort curve:** real perforation resists most at the first
  break, runs easier through the middle, and fights once more at the
  last bridge. Mirror it in amplitude, not in travel: the first dash's
  tick is the firmest of the trace; mid-span ticks sit at the base
  level; from ~70% coverage the ticks firm up gradually toward the
  booking tick — the thumb feels the remaining bridge narrowing as a
  thickening rhythm, and learns to expect the give at 85% before it
  arrives. No change to speed, no artificial drag on the finger —
  the travel stays honest; only the report thickens.
- **Backtracking is smooth.** Travel back over already-consumed dashes
  fires *no* ticks: torn is smooth, intact is rough. The thumb can
  therefore feel the tear front's position without lifting — rough
  ahead, smooth behind — and a half-asleep hand always knows which
  side of its own finger the tear is on.
- **Ahead of the finger, the next dash strains.** The dash about to
  break shows the ≤2dp deflection. The eye, looking ahead of the
  thumb as eyes do, sees the paper working; the thumb feels the tick
  when it gives. Sight and touch tell one story in two registers,
  a half-beat apart, which is exactly how real tearing reads.

### 3.3 Slip-off behaviour — the thumb will leave the band; plan for it

At 1 AM the travelling thumb drifts, especially vertically as it
extends. A trace that cancels on drift teaches the hand that tearing
is fiddly — and a fiddly standard loses to the tap within a week.

- **The forgiving corridor:** once the direction lock claims the
  trace, the live corridor widens from the 32dp band to **±24dp around
  the perforation line**. Inside the corridor, travel still consumes.
  The band is the door; the corridor is the road.
- **Leaving the corridor pauses; it does not cancel.** If the finger
  drifts beyond the corridor while still touching, consumption stops
  and — the important half — **the ticks stop**. Silence under the
  thumb is the signal: no buzzer, no error haptic, just the roughness
  going away, which a hand notices the way it notices a road going
  smooth. Consumed dashes stay consumed. Re-entering the corridor on
  the same touch resumes the trace from the tear front.
- **Lifting ends the touch, not necessarily the tear's memory — but
  under 85%, it ends the tear.** On release before 85% coverage: **no
  booking, ever** — and the consumed dashes reprint. Reprint by cut
  after a short beat (the leaf is whole again when he looks back at
  it), with no tick and no apology state. A partial tear that stayed
  torn would be a leaf claiming damage nobody booked; a partial tear
  that books would be theft. Whole or booked — nothing between
  survives the finger leaving the glass.
- **A second touch kills a sub-threshold trace.** Palm contact or a
  second finger landing mid-trace aborts it (dashes reprint as above).
  A trace is a one-finger act; a grip adjusting itself is not a tear.

### 3.4 The crossing — 85%

- At the instant consumed span reaches **85%**, **booking fires —
  under the still-touching finger, in G-01 order, before the remaining
  15% completes.** The booking tick is the firm single thock of the
  tap-tear (§4), distinct from the trace's dash ticks: roughness, then
  one solid note — *it has gone*.
- After booking, the finger is a passenger: the untraced remainder
  completes on the tear's own motion (≤120ms, motion's province), the
  finger may lift or keep travelling, and nothing it does can un-book,
  re-book, or interact with the ghost. Release after crossing is a
  non-event.
- **Why 85 and not higher, in the hand:** the last 15% of a real tear
  is never carefully traced — the paper gives and the hand is already
  leaving. A 92% threshold (the Designer's, overruled in the plan)
  would demand precision travel in exactly the zone where a tired,
  extending thumb is least precise, converting the final bridge into
  a fumbled second attempt at the moment of triumph. 85% books while
  the gesture still has momentum. The standard keeps the audit's
  number because the hand agrees with it.

### 3.5 Accidental-tear protection — the full list

1. Direction lock (§3.1): vertical travel can never tear.
2. No travel, no consumption: rests and holds are safe.
3. No booking below 85% coverage — a swipe that dies at 60% leaves a
   whole leaf.
4. Sub-threshold release reprints; second-touch aborts.
5. The band on a **sealed leaf is dead** — no trace begins, no tick
   fires, the perforation there is print (§5).
6. The tap floor (T4) is unchanged and remains the way a tear is made
   by anyone who cannot or will not trace — switch access, motor
   difficulty, or simple preference at 1 AM. ZIP-TEAR is an addition
   to the hand's vocabulary, never a replacement that strands the tap.

---

## 4. Press states and haptic ticks — ADD / FIRST / CUE, and the rest

### 4.1 The press state (all verbs)

Paper does not ripple and does not shrink. Every verb's press state is
the same physical story, so the hand learns it once:

- **On press-down:** the key's whole plate inks — the ground of the
  target deepens one step (visible *around* the covering thumb, per
  §0), the word's ink firms to full strength, and any raise on the
  plate settles by 1dp, as if the paper were pressed flatter under the
  thumb. No scale transform, no circular ripple, no colour change.
  The state appears within one frame; a press state that arrives late
  feels like the screen considering whether to obey.
- **On slide-off:** the state restores fully; release out there books
  nothing (§1).
- **On release inside:** booking, the state releases, and the tick
  below fires **at the booking instant** — the tick is the booking's
  sound, not the press's. Nothing ticks on press-down for any verb:
  a tick before the commitment teaches the thumb that touching is
  doing, and at 1 AM that lesson produces phantom bookings he then
  hunts for.
- **Haptics are never the only receipt.** Every booking below also has
  a visible consequence (a leaf lands, a leaf becomes head, a strike
  draws). If the system silences haptics, the screen loses a register,
  not the information.

### 4.2 The tick vocabulary — deliberately small

Three reports, no more. A tired thumb cannot learn a chord chart; it
can learn *light, firm, and doubled*:

| Report | Physical character | Means |
|---|---|---|
| **Paper tick** | One light, short tick — a stamp kissing paper | A leaf-level fact booked: ADD, a strike/un-strike, HAND-SPLIT's lines opening, UNDO accepted |
| **Thock** | One firm, heavier tick | A commitment booked: TEAR, START (arming), and ZIP-TEAR's 85% crossing |
| **Tick…tick** | Paper tick at booking, second paper tick at landing | A leaf changed *place*: FIRST, CUE |

### 4.3 ADD, FIRST, CUE — the three queueing keys, distinguished by thumb

These three sit within one thumb's travel of each other, at the
tiredest hour, and two of them move a leaf to the head. If they felt
identical, the thumb could not audit them; the eye would have to check
every booking. So:

- **ADD — paper tick, once.** Press: the end-key's plate inks (§4.1).
  Booking: the new leaf settles at the foot (motion's 200ms settle) and
  one paper tick lands with it. The whole report says *filed, in
  order, nothing preempted*. If the title is blank: no tick, no settle,
  the field keeps focus — silence is the correct report for a booking
  that did not happen.
- **FIRST — tick…tick, and the second tick is the point.** Press: same
  plate ink, on the quieter key. Booking fires the first paper tick
  immediately; the second fires when the leaf *lands* at the head —
  after the promotion travel, in the landing frame, with the raise and
  the `#01`. The thumb thus feels the whole errand in two beats: *sent*
  … *it is on top*. A single tick would report only the sending and
  leave the landing to eyes that are already closing. If FIRST is
  tapped when the queue is empty and the leaf lands as head by simple
  arrival, the two ticks collapse into one paper tick — no fake travel,
  no fake second beat.
- **CUE — tick…tick, identical to FIRST, deliberately.** CUE and FIRST
  are one act (make this leaf the head, stay here) performed on an old
  leaf and a new one. One act gets one report; the thumb learns
  *promotion* once. Booking tick at the tap, landing tick when the
  leaf arrives at the head with its honours. CUE never navigates
  (item 14): the second tick must be feelable *on this screen*, on the
  pad — a promotion whose receipt arrives after a teleport is a
  receipt for a different room.
- **START — thock.** It arms a session; it is not queueing. The firm
  report keeps arming and arranging in different registers of the
  thumb: light ticks arrange tomorrow, the thock begins it. START's
  press plate is the accent plate (as built); its inking under press
  deepens the accent ground rather than the paper ground, so even the
  press *state* is recognisable with the word covered by the thumb.
- **TEAR (tap) — thock**, the same commitment register as START,
  fired at booking; the ghost's travel then plays with no further
  ticks. Completion and arming are the screen's two irreversible-feeling
  acts and they share the heavy note. (ZIP-TEAR's dash ticks are a
  separate, lighter train under the trace, resolving into this same
  thock at 85% — §3.4.)
- **DEL — one muted tick, lower than the paper tick**, fired at
  booking, plus the banner's appearance as its visible receipt. DEL
  must be *noticeable* (a silent deletion at 1 AM is discovered in the
  morning) but never *satisfying in TEAR's register*: removal is not
  completion and must not feel like it. UNDO then answers with a full
  paper tick — the leaf is back, at full register.
- **Strike / un-strike (T9) — paper tick**, both directions. Per §2
  the tick is confirmation, not protection; it exists so a strike made
  through the number is never silent, and so the thumb can strike a
  run of steps — tick, tick, tick — without looking down between them.

**Bounce discipline (all keys):** a booking disables its own target
until its visible consequence lands (leaf settled, leaf landed, ghost
begun). Combined with T5's arm-at-landing rule, no doubled tap on this
screen can book twice, promote-then-arm, or tear the successor by
accident. The second contact of a bounce lands on inert paper and is
forgotten.

---

## 5. The sealed leaf — what must never be touch-live

At 05:00 the FASTED block seals with the head leaf's title as its
intention (F-1: a snapshot string; the leaf is not consumed, promoted,
or bonded — plan rulings, and touch's spec assumes them). The phone
was untouched. What the hand finds when it next arrives is a *record*.

**The law (audit item 12, fix F-5), in touch terms: on a sealed leaf,
nothing is touch-live. Not greyed-live, not long-press-live, not
hidden-live. Absent.**

Never touch-live on a sealed leaf — the complete list:

- **TEAR** — the zone gone entirely, not shrunk, not disabled-drawn.
  The perforation under seal takes no tap and — in the next build —
  **no ZIP-TEAR trace**: the band is print; a finger crossing it gets
  exactly what a finger crossing any other print gets — a scroll, or
  nothing.
- **START and CUE** — no arming, no promotion. A sealed leaf cannot be
  re-headed, and the head it names cannot be re-armed from the leaf.
- **DEL** — a sealed leaf cannot be deleted by the hand it is sealed
  against. (Its undo economics are irrelevant here; there is no tap
  to undo.)
- **SPLIT / HAND-SPLIT** — no lines open; the ruled lines that exist
  are print.
- **Sub-step numbers (T9)** — the strike is dead. Sealed steps are a
  record of the plan as sealed; a strike toggled mid-block would
  rewrite the record the block is running on.
- **The title, `#NN`, and the leaf body** — already inert (§1); under
  seal they stay inert with no new secret: **no long-press edit, no
  tap-to-expand, no swipe verb** (no swipe-to-delete, no swipe-to-tear
  backdoor of any kind). The seal must have no handle. Any gesture
  that "still works, if you know it" makes F-5 a coat of paint.
- **REOPEN** is composed only while unsealed, as built — the
  LAST-FINISHED line keeps the same discipline from its side.

**How dead must feel:** a tap on a sealed leaf produces **no press
state, no tick, no depression, no ripple** — the complete absence of
response that §1 specifies for print, because that is what the leaf
now is. Greyed verbs fail touch twice over: they still depress/answer
like controls (or read as broken controls), and they advertise the
levers at the hour — mid-block, resolve thinnest — when advertising
them is the harm. Absent verbs under a resting thumb feel like paper
under glass: consultable, legible, finished. He can read every word
on the leaf; his thumb simply finds no machinery in it, and the
glance ends (Feel-5's beat 7, in the hand).

**F-1's seal-instant touch rules** (the hand may, once, be present):

- **No touch debt:** the seal activates nothing, queues nothing, and
  stores no press. The first tap after a seal is an ordinary tap on a
  sealed screen, with no chance of discharging a verb that was live
  when the finger last left the glass.
- **A finger down at the seal instant:** if a press on a verb is in
  flight — down, not yet released — when the seal lands, that press
  is cancelled by the seal: plate restores, release books nothing,
  no tick. A booking completed *before* the seal instant stands (it
  was booked on an unsealed leaf, honestly). The line is the release/
  booking instant, and it must be exactly there — a press that spans
  the seal cannot be allowed to land a verb on a sealed leaf "because
  it started earlier."
- **A ZIP-TEAR trace in flight (next build):** if 85% was crossed, it
  booked — before the seal — and stands. If not, the trace dies with
  the seal: dashes reprint, no booking on any later motion of that
  same touch.
- **A scroll in flight:** untouched. The seal moves no leaf by a
  single dp (motion's seal law); a scrolling or resting thumb must
  never feel the pad shift under it at 05:00. Positions are the
  queue's truth, and the seal does not reach into the queue — in the
  hand exactly as in the machinery.
- **The composer leaf is not the sealed leaf:** queueing *new* work
  for later is not a verb *on* the sealed leaf, and this spec does
  not kill it; F-5's scope is the sealed leaf's own verbs. (Whether
  ADD/FIRST may run during a seal is a machinery ruling above this
  seat if contested; touch flags only that FIRST-during-seal would
  change the head a *later* routine reads — never the sealed
  session's snapshotted intention.)

---

## 6. Charges this seat leaves on the record

1. **The 5,000ms undo window is a touch defect, not a footnote**
   (audit items 3/15; Muede charged it; Dens acquitted the banner, not
   the window). T10 makes UNDO as hittable as paper allows, but no
   target size fixes a window that can expire during the reach.
   Extending it is a machinery price Amt noted and did not rule; this
   seat records that for the hand that actually uses it, the window
   *is* the target's fourth dimension, and five seconds at 1 AM is a
   miss designed in. It should not drift into batch-10 unpriced.
2. **TEAR's 48dp is a floor the build should beat** (T4: ≥88 × 48dp,
   right-anchored, neighbours exiled). If geometry forces a choice
   between the audit's letter (48dp) and a wider zone, take the wider
   zone — the charge behind the number is the extended thumb, and it
   does not read rulings.
3. **Nothing in this file authorises a target the audit killed:** the
   horizon bar's ghost must not return as a tappable summary, and the
   pad edge stays inert until Slip-Sort is ruled and built (§1).

## Consolidated touch table (for Builder / Finisher)

| # | Contract | Hard never |
|---|---|---|
| X1 | Every verb ≥48dp high; DEL exiled ≥16dp from TEAR/START/CUE; gutters ≥8dp dead ground | Two live targets sharing an edge; a press starting in a gutter snapping to a key |
| X2 | Press = whole-plate ink + 1dp settle, one frame; slide-off cancels; tick at booking, never at press-down | Ripple, scale, colour-change press states; ticks before commitment |
| X3 | Strike narrowed to the number (48dp-wide rect, nearest-centre) + paper tick | Full-width strike, with or without a tick as its excuse; live step *words* |
| X4 | Tick vocabulary: paper tick (leaf facts) / thock (TEAR, START, ZIP crossing) / tick…tick at booking+landing (FIRST, CUE); DEL muted, UNDO full | A fourth report; DEL in TEAR's register; FIRST/CUE ticking only once and leaving the landing unreported |
| X5 | Bounce discipline: a booked target is inert until its consequence lands; promoted leaf's START arms at landing only | Double-booking from a bounced tap; CUE→START fall-through |
| X6 | ZIP-TEAR (next build): 32dp band, 12dp direction lock, ±24dp corridor, dash-tick train (firm first, base middle, firming from 70%), smooth backtrack, pause-on-slip by tick-silence, reprint on sub-85% release, abort on second touch, **booking at 85% under the finger** | Booking below 85% or only on release; consumption on rests, holds, or vertical travel; a partial tear surviving lift-off; the tap floor removed |
| X7 | Sealed leaf: TEAR (tap and band), START, CUE, DEL, SPLIT, strike numbers, REOPEN, and every long-press/swipe backdoor — absent, no press state, no tick | Greyed-but-present verbs; any secret gesture that survives the seal; a press spanning the seal instant landing a verb |
| X8 | Seal instant: no stored press, in-flight presses cancelled, in-flight scroll undisturbed, no leaf moves | The pad shifting under a resting thumb at 05:00 |

Nothing in this file requires a schema change or a new surface. It
sizes what the audit enlarged, rules what the audit left as a builder's
choice, gives the three queueing keys three distinguishable reports
under one thumb, writes ZIP-TEAR's touch contract for the build that
will carry it, and holds F-5's seal at the level of the finger, where
greyed-versus-absent is actually decided.

**DECLARED — touch complete, uncompiled, ready for Amt.**

— Feel-1 · touch (issue #12 cell)

---

## Ready-to-post comment for issue #12 (AMT posts this)

> **Touch — Feel-1, issue #12 cell**
>
> I specced this screen for the hand that actually uses it: one thumb, extended and drifting, at 1 AM in the dark — and for the other appointment, 05:00, when the seal lands with no hand on the phone at all. Full spec: `feel-1.md` in the cell work dir.
>
> **The complete target map.** Every verb gets a ≥48dp-high hit rect — the printed word is never the target. TEAR takes the widest rect on the screen (≥88 × 48dp, right-anchored, no live target to its right and none within 16dp left): it is hit by the most extended, least precise thumb position there is, so the audit's 48dp is treated as a floor, not the aim. ADD and FIRST sit on the composer leaf at ≥72 × 48dp each, split by a ≥12dp dead gutter a fat thumb cannot straddle — a press starting in the gutter is dead, it never snaps to the nearer key. DEL is exiled to the far end of the verb row with 16dp of dead ground around it; it never takes the right-edge landing. UNDO is full banner height and ≥72dp wide, because the 5,000ms window is spent first on noticing, then on reaching. Explicit non-targets matter as much: header, count, titles, `#NN`, sub-step words, the pad edge (inert until Slip-Sort is actually built — a surface that feels like a handle and does nothing is a lie told to the hand on every scroll), and no long-press targets anywhere — at 1 AM a long press is just a resting thumb. Universal rules: press shows within one frame, slide-off cancels cleanly, bounce can't double-book (a booked target stays inert until its consequence lands, and a promoted leaf's START arms only at landing, so a bounced CUE can't fall through into arming).
>
> **The sub-step strike: I rule NARROW — the tick is not the cure.** The audit left builder's choice between narrowing the toggle to the step's number or adding the tear's tick to the full-width target. Touch rules narrow, with the tick riding as confirmation only. Reason, in the hand: feedback *reports* an accident, narrowing *prevents* it. A tick on a full-width strike tells a half-asleep man — after the fact, mid-scroll, thumb resting-then-lifting on a leaf he was only reading — that the print changed, and buys him three acts of wakefulness to fix it. The number is where intent already lives; the hand goes to the margin mark the way a pencil does. And narrow is not small: the rect is 48dp wide centred on the number, vertically borrowing the inert air over neighbouring print, nearest-centre wins on overlap. Quiet to the eye, generous to the thumb. The step's words are inert — they are for reading.
>
> **ZIP-TEAR's touch contract (next build; the audit's 85% stands, not 92%).** Entry in the 32dp band, decided by a 12dp direction lock: vertical-dominant is a scroll, irrevocably, and consumes nothing — the band lies across every leaf, square in the scroll's road, and most touches crossing it are going somewhere else. A still finger tears nothing, however long it rests. The resistance illusion is rhythm, never lag: one tick per dash at its break, so the thumb feels the perforation's true spacing under the finger that is covering it up; firmest at the first dash, base level mid-span, firming again from ~70% as the bridge narrows; backtracking over consumed dashes is tick-silent — rough ahead, smooth behind, so the hand always knows where its own tear front is. Slip-off: once locked, the corridor forgives ±24dp of drift; beyond it, consumption pauses and the *ticks stop* — silence is the signal, no error buzz. Sub-85% release books nothing and the dashes reprint; a second finger or palm aborts. At 85% coverage, booking fires under the still-touching finger in G-01 order, announced by the one firm thock, and the finger becomes a passenger. Why 85 in the hand: the last stretch of a real tear is never carefully traced — a 92% threshold would demand precision travel exactly where the extending thumb has none, fumbling the final bridge at the moment of triumph. The tap remains the accessibility floor, unchanged.
>
> **ADD / FIRST / CUE feel different because the thumb must audit them without eyes.** Press states are paper physics — whole-plate ink deepening one step (visible *around* the covering thumb) and a 1dp settle; no ripple, no scale, no tick on press-down for anything, because a tick before commitment teaches that touching is doing. The vocabulary is three reports only: a light **paper tick** for leaf facts (ADD, strike, HAND-SPLIT, UNDO), a firm **thock** for commitments (TEAR, START, ZIP's crossing), and **tick…tick** for promotions — FIRST and CUE share it deliberately, one act one report: first tick at booking, second in the landing frame as the leaf takes the head's raise and `#01`, so the thumb feels *sent … it is on top* while the eyes are already closing. DEL gets a deliberately muted tick, never TEAR's register — removal must be noticeable at 1 AM but must not feel like completion.
>
> **The sealed leaf must be dead to the hand, completely.** Never touch-live under seal: TEAR (tap *and*, in the next build, the perforation band — no trace begins), START, CUE, DEL, SPLIT, the strike numbers, REOPEN — and no backdoors: no long-press edit, no swipe verb, no secret gesture that "still works if you know it." A tap on a sealed leaf yields no press state and no tick at all; it is print, consultable and finished, and greyed verbs fail twice — they still answer like controls, and they advertise the levers at the hour resolve is thinnest. At the seal instant itself: no touch debt (nothing stored, nothing queued), a press in flight across the instant is cancelled by the seal (a booking completed *before* it stands), an in-flight ZIP trace below 85% dies and reprints, and a scrolling or resting thumb must never feel the pad shift by a dp. One charge left on the record: the 5,000ms undo window is a touch defect, not a footnote — target size cannot fix a window that expires during the reach, and it should not drift into batch-10 unpriced.
>
> **DECLARED** — touch only, uncompiled, nothing run.
>
> — Feel-1 · touch (issue #12 cell)
