# Rhea — Wild, toys · Phase 3 GO-BEYOND: the Block Stack, re-argued

**Seat:** Rhea (Wild, toys lens) · **Element:** the Deck hub / block-count
**Standing:** my stack is the filed runner-up to Vega's Counterweight Hub,
and the filed first-revisit if the hub feels abstract on the phone.
**Verdict, up front:** the stack **as filed is withdrawn** — it was guilty
of the duplication charge, and §1 says exactly how. What survives is a
different stack built on the one fact pips own and the total does not:
*how many blocks, in what order, with how many joints between them.*
It lives only as a **replacement** for the pips, in the pips' home,
rendering the pips' fact. If the merge keeps pips *and* slabs, kill the
slabs on sight — two renderings of the count is the crime, whoever
commits it.

---

## 0. The beauty case, in plain words

Pips are a code. You learn that dots mean a number, and then you read
the dots forever. At three in the morning, on the two-hundredth
morning, a code is still a code — it never becomes a thing.

A stack is a thing. Three wooden slabs in a shallow tray is what three
blocks *feels* like before it is what three blocks *means*: a small
pile, a light morning. Eight slabs is a morning with weight — you see
the height before you count anything, the way you see a pile of books
is tall without counting the books. Setting the morning is stacking it.
Changing your mind is knocking the top slab off the pile with one
finger, and hearing it go. Nothing glows, nothing celebrates, nothing
counts up. The pile just stands there, honest, in the tray, and when
the slide seals it the pile goes still and stays exactly as built —
the shape of the work, sitting in the middle of the instrument, for
as long as the work lasts.

That is the whole claim: the Deck's other elements are read. The stack
is *handled*. An app he must touch 186 mornings in a row should have
one part that rewards the hand instead of taxing the eye — and the
count is the only fact on the Deck that can be a physical object
without lying, because a count is already a pile of things.

---

## 1. Charge (a) — duplication: the filed stack concedes, the beyond stack answers

### 1.1 The concession, in print

The filed stack was defined as *one slab per 50 minutes of the set
duration, plus a thinner remainder slab ∝ remainder/50*. That is the
total, re-chunked in fifties and drawn as wood. Worse than duplication,
it was **false segmentation**: set the wheel to 25-minute blocks ×4
and the filed stack would have drawn two 50-minute slabs for a session
that contains no 50-minute anything. The remainder slab was a second,
fractional duration readout competing with the hero. Vega's charge —
*the stack re-renders duration the total already closes over* — is
correct against the filed form, completely.

**Withdrawn, by name:** the 50-minute slab definition · the remainder
slab · slab drops triggered by wheel detents crossing 50 · any slab
behaviour derived from `totalMinutes`. If any of those four ever
reappear in a build, this file does not defend them.

### 1.2 What a slab can encode that the total does not

The total is a product with its working hidden: `Σ blocks + the breaks
between them`. One number, closed. It cannot say how the span is
*jointed* — and jointing is a real fact of the session he is about to
seal, rendered nowhere else on the Deck at arm time. Three encodings,
each checked against the source of truth:

1. **Count as a factor, not as duration.** One slab = **one actual
   block** — one unit of `blockCount`, whatever length the wheel has
   set. `2H 50M` on the hero could be one 170-minute block with no
   joints, or three 50-minute blocks with two breaks inside it. The
   hero cannot distinguish those mornings. The stack can, at a glance:
   one slab, or three. This is the same fact Vega's pips render — count
   is a *factor* of the total, not the total — and it is why pips pass
   the truth law. The stack claims pips' fact, pips' home, and nothing
   else. The wheel keeps block length; the hero keeps the span; the
   stack keeps the count. Three facts, three homes, no overlaps.
2. **The breaks, as seams.** Between consecutive slabs there is a 2dp
   gap where the tray's recess shows through. Seam count = `count − 1`
   = the number of 10-minute safe breaks the sealed span contains —
   breaks the total *includes in its arithmetic and renders nowhere*.
   A seam is not an added element and not a second rendering of
   anything printed: it is the negative space between the count's
   units, and it is the only place on the arm path where he can see
   *how many times this session will breathe*. A 6-block arm is not
   just longer than a 3-block arm; it is interrupted five times
   instead of two. The hero flattens that difference into minutes.
   The stack keeps it as structure.
3. **Order, and a last block with an identity.** Slabs stack in
   execution order, bottom = first block, top = last block. A pip ring
   has positions; a stack has a *top* — a specific, grabbable block,
   which is the last one he will live through. The flick (§2.3) removes
   that block and no other: it is an operation on an identified segment
   ("shed the last block"), where the hub's wrap-cycle is an operation
   on an abstract counter. When his day chains several 50-minute
   blocks into one arm, the difference between "count: 5" and "a pile
   whose top one I can knock off" is the difference between editing a
   number and editing the morning.

And the perceptual point the live question actually turns on:
**subitising dies above 4, and pips at 5–8 die with it** — a ring of
eight Ø10dp pips must be counted serially, and its filled arc barely
changes between 6 and 8. A stack does not ask to be subitised. Height
is an analogue channel: 3 vs 5 vs 8 reads instantly as *more pile*,
comparatively, peripherally, with no enumeration at all. Exact
enumeration is still available (count the edges) when he wants it.
The stack's honest offer is **count as weight** — magnitude first,
enumeration on demand. Pips offer enumeration only, and lose it
exactly where the range extends past 4.

### 1.3 The boundary of the claim

If blocks within one arm ever become *unequal* in length, uniform
slabs would assert an equality that is false, and this encoding dies
with that model change (see §6). Under the app as planned — one block
length per arm, set on the wheel, multiplied by count — every slab is
the same block, and the uniform slab is the truth.

---

## 2. Full beyond spec — THE TRAY STACK

### 2.1 Geometry and material

- **Home:** the hub only. Ø128dp hub (Designers' Uhr dimension stands),
  its face becomes a shallow **tray**: inner well Ø112dp, floor at
  recess `#100E0C` (night), one tone step below the hub face, closed by
  a 1px catch-light on the top edge. No other surface in the app gets a
  slab, a mini-stack, or an echo.
- **Slab:** width **72dp**, height **7dp**, corner 2dp (an edge break,
  like a tick cap — not a panel radius; panels keep radius 8/16).
  Slabs centred in the well. Fill alternates by exactly one tone step
  per slab — `#362C27` (raised) / `#4A423C` (cap) — so every edge stays
  legible at any height; the top slab is whichever tone its parity
  lands on, never a special colour. **Zero orange, zero green, no
  numerals, no text, no per-slab labels, ever.**
- **Seam:** 2dp of exposed well floor between slabs (the break joint,
  §1.2.2). No spacer element is drawn; remove a slab and its seam
  closes with the stack's settle.
- **Stack height:** `n × 7dp + (n−1) × 2dp` — n=1: 7dp · n=3: 25dp ·
  n=4: 34dp · n=8: **70dp**, inside the well's 86dp chord at slab width
  with air above and below. The stack grows upward from the tray floor;
  a 4dp raised lip strip across the well's bottom chord sells "in a
  tray" **without occluding any slab** — the filed form's 18% lip hid
  the lowest slab, and a hidden slab is an undercount, i.e. a lie.
  That lip is cut to 4dp and sits below the first slab's base line.
- **Rest position:** stack base on the tray floor, bottom-centred in
  the hub. The hub's torsional lag behind the rim (Vega's coupling)
  applies to the tray as a rigid body exactly as it applied to the pip
  field — pips' physics, inherited whole.
- **The press:** the hub remains a button. DOWN depresses tray + stack
  together 1dp over 90ms (one rigid body — slabs never depress
  individually), catch-light flips to the bottom edge; count commits
  **on release**, slide-off cancels, per the settled hub law. The stack
  does not change the press; the press grows the stack (§3).

### 2.2 Count range it honestly serves

**1–8** — the Designers' printed range, adopted, not extended.
- **1–4:** exact at a glance, and the stack is at its most toy-like;
  this is also where pips are strongest, and I concede the margin here
  is feel, not legibility.
- **5–8:** the stack's actual case. Height carries magnitude
  pre-attentively where the pip ring forces serial counting. Exact
  count by edges costs ~1 fixation longer than at 4 — stated, not
  hidden (see §6).
- **At 8 the tray is full.** The hub press wraps 8→1 under the settled
  law; the wrap renders as the whole stack sinking into the well
  (translate down 70dp behind the tray floor line, 200ms ease-in,
  silent) and one slab dropping per §3. A wrap is a rested-user act,
  never part of a primed morning.
- **FLOW:** count is meaningless, so the tray says so — well renders
  empty, floor only, press disabled with no dip and no haptic. An
  empty tray under FLOW is a fact; a dimmed stack would be a ghost
  count.

### 2.3 Removing a block — the flick

- **Dispatch:** a hub DOWN that moves **>12dp horizontally before
  release** converts from press to grab — the press cancels under the
  existing slide-off law, no count change is pending, and the finger
  now holds the **top slab only**. Slabs below the top are never
  grabbable: they are physically buried, and the geometry says so.
  The top slab's grab zone is its own 7dp plus the seam and air above
  it — a 24dp tall band across the stack's 72dp width.
- **Carry:** slab follows the finger 1:1 in x, seated on the slab
  below; rotation ∝ slide distance, pivot at its trailing bottom
  corner, max **10°**. Scrape tick per 12dp travelled (§2.5).
- **Commit:** release with the slab's centre past the well's rim
  (|x offset| > 56dp) **or** fling velocity > 400dp/s outward commits
  `count − 1` **at the release frame** — the hero re-strikes in that
  same frame under the total law (one block *and* one seam's break
  leave the span, except at n=1→… the flick at n=1 is refused: the
  last slab does not flick, it is the session's minimum; it springs
  back like a failed flick). The freed slab then falls out of the tray
  — gravity ease-in², 300ms, fading only in its last 60ms inside the
  well's clip. The animation follows the commit; it never gates it.
- **Failed flick:** release below both thresholds → slab springs home,
  stiffness 500 / damping 26, one ≤4% overshoot, re-land thud (§2.5).
  Nothing is recorded. A failed flick is a wobble, not an error state.

### 2.4 Adding a block — drops, compression, sway

- **Source events, only two:** a hub press commit (release frame) and
  a state restore (priming, §4). **The wheel never adds slabs.** Wheel
  detents change block length; the stack's count is untouched by
  spinning, however fast. The filed form's detent-queue (one drop per
  90ms, slot-machine well) is dead with the rest of §1.1.
- **Drop:** the new slab enters 14dp above the stack top and falls,
  ease-in² (gravity), **110ms**. State is already committed at the
  release frame and the hero has already re-struck — the slab is
  catching up with a truth the number printed instantly, the same
  licence Vega's pip ink-settle (120ms) already holds. Only the
  topmost new slab ever animates; if rapid presses (≥250ms apart in
  human use, so this should not occur) outrun a drop, any slab beneath
  the falling one renders at rest — spawn-at-rest — rather than
  queueing a cascade.
- **Landing = a stack event, not a slab event:** on impact the whole
  stack compresses **1.5dp** and recovers on one spring —
  **stiffness 420 / damping 28 (ζ ≈ 0.68)**, top-of-stack sway
  amplitude **capped** at ±0.8dp for n ≤ 4 and ±1.5dp for n = 5–8,
  pivot at the tray floor, rotation ≤ 0.8°. Settle to <0.2dp residual
  in **≤340ms** at every height. The filed values (stiffness 260 /
  damping 13, amplitude ∝ height, ~900ms at n=10) are superseded in
  full: sway no longer grows without bound, because amplitude is a
  cap, not a consequence.

### 2.5 Haptics and sound

**Haptics** (all ms @ amplitude):
- Hub DOWN bottom: `4 @ 0.5` (the settled press tick, unchanged).
- Slab landing: `12 @ 0.6` at the landing frame (+110ms after commit)
  — this **replaces** a separate release-commit tick for count
  changes; one event (a block arrived) gets one voice, and the voice
  is the landing, not a buzz on the button. Press-cancel: silence.
- Grab (press→flick conversion at 12dp): `6 @ 0.4` lift tick.
- Slab sliding: `4 @ 0.3` scrape tick per 12dp travelled.
- Flick commit: `10 @ 0.6` at release, `8 @ 0.4` as the slab clears
  the rim. Failed-flick re-land: `12 @ 0.5`.
- Sway and compression recovery: silence. Wrap sink: silence.

**Sound** (FLOAT-PCM synth, wood vocabulary, only where the app's
sound set is on; the Deck's default silence otherwise):
- Drop landing: lowpassed knock, fundamental ~190Hz, 40ms decay.
- Flick: the same knock pitched +15% under a 25ms noise scrape.
- Spring-back: knock at −20%. No sound for sway, seams, the wrap, or
  any numeral change. At 04:58 (§4): **no stack sound at all** — the
  stack is already built when he arrives; nothing falls in that frame.

### 2.6 Type, spacing, palette compliance

No type on the stack (the ladder 104/72/50/22/15/10 is untouched; the
hub's static `BLOCKS` micro-label, if the merge keeps Vega's, is plate
10 and never carries a value). Hub sits inside the Deck's filed
composition — margin 20dp, spacing tokens unchanged, no layout shift
at any count: the tray is fixed-size, the stack grows *inside* it, so
count changes move nothing else on the screen. Night palette exactly
as locked: ground `#171412`, recess `#100E0C`, raised `#362C27`, cap
`#4A423C`; accent `#F47442` stays on the slide, its only home.

---

## 3. Charge (b) — settle cost: the choreography that makes it free

The filed charge: ~900ms of sway at tall stacks, sitting on the arm
path, taxing ARM ≤10s. The beyond answer is not "sway is faster now"
(though it is: ≤340ms, §2.4). It is that **the sway never stands
between the man's hand and the seal**, by construction, in four moves:

1. **Commits are frames, not animations.** Count state, hero
   re-strike, and FINISHES all update at the press-release frame.
   Nothing in the app — not the slide, not the seal, not the next
   press — reads or waits for the stack's spring. The spring is a
   rendering decay of an already-true count, exactly like the readout's
   120ms ink-settle, which no audit has ever charged to ARM.
2. **The fall hides inside the hand's own travel.** After a hub
   release, the hand's next act is the slide: reposition + drag is
   250–450ms of hand time on the fastest morning, plus the slide's own
   filed gate-to-session ≤280ms. The slab's full life — 110ms fall +
   ≤340ms settle tail, overlapping, ≈380ms end-to-end — completes
   inside that window with room to spare. On the wheel path the same
   trick holds in reverse: the wheel's own settle (≤520ms, Vega) is
   dead time the hand already pays; nothing about the stack adds a
   frame to it, because the wheel never moves the stack at all (§2.4).
3. **The seal discards the spring.** If he slides while a tail is
   still decaying, the seal frame snaps the stack to its exact rest
   geometry and the session opens on a still tray. Sway is not state;
   it is not carried into HOLD, not frozen mid-pose, not resumed after.
4. **Removal is post-commit.** A flick's 300ms fall-out happens after
   its release-frame commit, concurrent with whatever his hand does
   next. It gates nothing.

**ARM cost, stated as a number: +0ms.** Priced per path —
primed morning: 0ms (stack pre-built, §4) · manual count change: 0ms
added to the path (state at release frame; ≈380ms of visual settle
fully concurrent with hand travel + slide, both already budgeted) ·
flick removal: 0ms (commit at release) · cold first paint: 0ms
(stack renders in the first frame at rest). The remaining honest cost
is not time but *attention*: a man who chooses to watch the pile
settle before sliding spends his own looking. That is the same
looking the ink-settle asks for, and it is not a gate.

---

## 4. The 04:58 frame — the stack, pre-stacked (Joss's primed frame)

The routine has primed the Deck before he picks the phone up: wheel
seated, loadout seated, hero printed. In that frame (Humans' §3
drawing, which I adopt) the hub's three unfilled pips become **three
slabs at rest** — 25dp of pile in the tray, base on the floor, seams
showing the morning's two joints. Behaviour, exactly:

- **First paint is final.** The stack renders in its rest geometry on
  the first frame. No drop, no sway, no sound, no haptic — nothing in
  the primed frame moves, and the stack obeys that law first. Priming
  writes state; it never performs it. (Spawn-at-rest, §2.4, is what
  makes a cold process start equally honest: state loads, slabs are
  simply *there*.)
- **What it tells him without reading:** a three-high pile is a light
  morning, taken in peripherally while the hero gives him the one
  number. If a future fasted set chains five blocks, the pile is
  visibly taller before he has read anything — the shape of the
  morning, in the Humans file's own words, except the shape now has
  height.
- **If his hand edits:** one flick sheds the last block (hero
  re-strikes under the total law, one seam closes), one hub press adds
  one. The slide remains the frame's only verb and only orange; the
  stack offers no word, no prompt, no suggestion — it is furniture
  that happens to be true.
- **Variants:** permission dead — stack unchanged at rest, slide inert
  as filed; the pile does not mint, warn, or dim. Mid-window arrival —
  the tray holds the blocks that *remain*, at rest, hero restating the
  time the window still holds; the stack never pretends the morning is
  unspent, because it is rendered from the same remaining-blocks state
  as every other element. He never comes — nothing was animated,
  sounded, or recorded; the offer cost him nothing, and the pile's
  unmaking at window close happens unobserved or not at all.

*Mailed to Joss with this file: the primed frame pre-stacks the tray —
his Prep Bench seeds and the resolver's set must write `blockCount` as
state the Deck loads at rest, never as an animation the morning has to
watch.*

---

## 5. HOLD / TRUTH check

**HOLD — absolute.** On the seal frame: hub input dies (press and
grab dispatch both), the spring is discarded and the stack snaps to
rest (§3.3), and the tray becomes a still object for the session's
whole life. No slab is spent, dimmed, or dropped per completed block —
that would be a second progress rendering competing with the running
countdown, and it is forbidden here as firmly as it was in the filed
form. The stack introduces zero mid-session decisions, zero mid-session
motion, zero mid-session sound.

**TRUTH — once, answered head-on.** The duplication charge is answered
in §1, not around it: the filed form was guilty and is withdrawn in
print, with its four mechanisms named. The beyond stack renders
**blockCount, only blockCount, in the hub, only in the hub** — the
identical fact, home, and exclusivity that the truth census already
grants pips (`Block count | hub pips only`). Its seams are the count's
own negative space rendering the break-joints the total's arithmetic
contains but no arm-path surface prints. Its order is the session's
execution order, which no other Deck element renders at all. Three
conditions keep it lawful, and each is a kill condition if broken:
(1) pips die — the stack *replaces* them, never joins them;
(2) no slab is ever derived from `totalMinutes`, block length, or any
duration; (3) no slab appears outside the hub, in any scope, including
receipts and the record. Single source of truth throughout: the Deck's
`blockCount` state, which the press, the flick, and priming all write,
and from which the hero's total is computed — never the reverse.

**ARM — ≤10s.** +0ms by the choreography in §3, priced per path there.

---

## 6. The strongest case against my own stack

Argued as hard as I can, because a stack that can't survive its own
seat shouldn't survive the merge:

1. **It does not actually fix exact counting above 4.** Height gives
   magnitude, not number. At 7 vs 8 slabs, a tired glance can be wrong
   by one — and "wrong by one block" is wrong by a block *plus a
   break* in the span he seals. Pips at 7 vs 8 are also countable-only,
   but counting eight dots on a ring may genuinely be faster and more
   reliable than counting eight 7dp edges in a stack. If his real need
   at 5–8 is *exactness*, the stack trades a code he must count for a
   pile he must count, and charges physics for the exchange.
2. **The gesture dispatch will misfire on tired hands.** Press vs grab
   turns on a 12dp threshold crossed by exactly the hands this app
   serves at 03:40 — slow, weak-gripped, imprecise. A sloppy press
   becomes a grab, the slab wobbles home, and the morning's first
   touch is a small failure performed in wood. Vega's press has no
   failure mode at all. Every misfire is a tax on trust that a pip
   never levies.
3. **The flick is a destructive gesture living next to the commit.**
   An accidental flick that connects sheds the last block; the only
   announcement is the hero re-striking, which a half-awake eye may
   not catch before the slide seals a shorter morning than he meant.
   Pips fail safer: their worst accident is a wrap, equally visible on
   the same hero — but the wrap takes a full press, not a slip of the
   thumb sideways.
4. **Perceptual duplication survives formal innocence.** I have proved
   the stack renders count, not duration. But a user does not perceive
   in state variables: a taller pile *feels* like "more time", and he
   will read it as the duration, warmly and approximately, every
   morning. The charge may be dead in the census and alive in the
   hand. If that reading ever competes with the hero — if he trusts
   the pile over the number — the stack has become the second
   rendering TRUTH forbids, by perception rather than by spec.
5. **It bakes in the uniform-block assumption.** §1.3 names it: the
   day per-block lengths vary within an arm, uniform slabs become a
   lie told in geometry, while pips — pure count, no size claim —
   stay honest under any block model. The stack's truth is rented
   from the current model; the pips' truth is owned.
6. **Cost where pips are nearly free.** Springs, dispatch conversion,
   clip-and-fall rendering, spawn-at-rest rules — a real build and a
   real jank surface on his handset, to replace eight dots and a
   120ms ink-settle. If the hub on the phone does *not* feel abstract
   (the only condition under which this revisit was filed), every line
   of that cost bought nothing. The stack's own filing condition is
   its sharpest enemy: it exists for a feeling that may never occur.

**What would make me withdraw the beyond stack, too:** a device trial
where he miscounts a 6-stack, or misfires the grab twice in a week, or
reads the pile as the duration against the hero once. Any one of those
and the pips stand, this file becomes the record of a good toy that
didn't belong on an instrument, and I will sign that withdrawal the
way I signed §1.1 — in print, without being asked twice.

— Rhea, Wild (toys), Phase 3. Mailed to Vega (the opposition case,
either way) and to Joss (the primed frame, §4). Wall signed on filing.
