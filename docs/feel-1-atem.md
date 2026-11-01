# FEEL — THE HOLD + THE COUNTDOWN (Issue #11)

Seat: **ATEM, Feel Specialist 1**, Tor's cell · For: Tor (lead)
Slice: the wall only — the hold, the gate line, the draining rule,
the arm. Not the skeleton (Designer's), not the ledger, not the peek
act once taken.
Ground read, whole or in the relevant part:
`studio/plan/AUDIT-held-door.md` (FIX-5, FIX-6, FIX-7, FIX-8,
Conflicts 1 & 6, Proposals Disposed — the knock/detent fragments);
`docs/designer-skeleton.md` §5 (the peek zone Z, the gate stack in
Z1, the 110dp zone, the anchored derivation in §5.8);
`app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`
(`AirlockInterceptOverlay` as shipped: the free-running countdown at
:562–571, the 88sp hero numeral at :636, offer/withheld composed only
at `countdownSecs == 0` at :663/:690, entry `fadeIn(specB())` at :559);
`app/src/main/java/com/example/ui/MonolithViewModel.kt`
(`peekOfferFor` :141, `AirlockInterceptState` :153,
`peekSecondsRemaining`'s stored-end doctrine :180–188,
`triggerAirlockIntercept` :2315, grant re-derivation :2393);
`ui/theme/MonolithCurves.kt` (`EasingA` = CubicBezier(0.2, 0, 0, 1),
`specA`, the duration vocabulary {180, 260, …});
`ui/theme/Color.kt` (Night tokens cited below).
**Nobody compiles.** Every number here is either quoted from the
audit/skeleton or flagged as mine for Tor's ratification.

---

## 0. The one-sentence verdict

> **The wall is an exhale, not a fuse.** One edge moves, at one
> speed, in silence, and the most important thing about its last
> second and its ending is that the door does not mark them.

The man at this door is mid-craving at 1 AM. Every countdown he has
ever met — oven timers, bomb timers, "your download starts in…",
an opponent's respawn clock — has trained him that a draining
bar is a threat that accelerates toward a climax he must brace
for. If this door borrows that tempo, it does not hold him, it
*fights* him: it gives the craving a metronome, a climax to wait
out, and a reward ding at the end to condition the next reach.
The tempo below is built as the opposite in every particular:
constant speed, no climax, no ding, no change of face — so the
five seconds are time he *spends standing somewhere calm*, not
time he survives.

Four laws carry the whole spec. The rest is their application.

- **F-LAW 1 — ONE MOVING EDGE.** During the entire wall, exactly
  one thing in the world moves continuously (the rule's right
  edge) and exactly one thing changes discretely (the gate
  figure, once per second). Nothing else moves, pulses, breathes,
  glows, scales, or re-colours — not the card, not RETURN, not
  the gate line's own type.
- **F-LAW 2 — CONSTANT VELOCITY.** The rule's edge never speeds
  up and never slows down inside a visit. Its speed is set once,
  by the wall's total length, and the last second is drained at
  precisely the speed of the first.
- **F-LAW 3 — THE ENDGAME IS IDENTICAL.** At any given
  *remaining* time, a 5-second wall and a 10-second tapered wall
  are the same door (§5 gives the pixel-level form of this). The
  taper adds its seconds at the front, where he is reading, never
  at the back, where he is bracing.
- **F-LAW 4 — THE ARM IS SILENT AND UNMARKED.** No sound, no
  haptic, no flash, no scale, no colour event accompanies the
  offer arming. The visit's loudest moment is its first 180ms
  (the door arriving); its most consequential moment — the chip
  standing up — is its quietest transition.

---

## 1. What the shipped code feels like today (the ground truth)

So the builder knows what feeling is being *replaced*, not just
what code:

- The countdown is a `LaunchedEffect` loop — `delay(1000L);
  countdownSecs--` (`AirlockAndDisarmOverlays.kt:566–571`). It is
  a free-running counter, unanchored (FIX-5's charge): its tempo
  is whatever the scheduler gives it, it can drift late, and its
  "second" is a loop iteration, not a second. A man cannot settle
  into a rhythm that is not actually regular.
- What it drives is an **88sp hero numeral** (:636) — the largest
  glyph on the face, larger than the name of the app he reached
  for. At that size a digit change is not a reading update, it is
  an *event*: five full-screen heartbeats, each one demanding the
  eye. That is a fuse's tempo by typography alone.
- At zero, the offer and the withheld lines *insert* (:663, :690),
  the centred card grows ≈110dp and RETURN jumps ≈55dp under a
  travelling thumb (F-2, the audit's worst interaction defect).
  The countdown's climax is therefore literally a physical jolt —
  the floor moves at the exact second he acts.
- Entry is `fadeIn(specB())` — 420ms on `EasingB`, a ghost
  arriving slowly (FIX-7b cures it to specA 180ms). For nearly
  half of the first second the door is not fully *there*, while
  its unanchored counter is already running.

Every element of the new tempo below exists to kill one of these
four feelings: drift, heartbeat, jolt, ghost.

---

## 2. The drain — easing, direction, texture

### 2.1 Easing: linear, and linear is a truth claim, not a style

The rule's filled width at any frame is

```
fraction = wallRemainingMono / wallTotalMono
```

derived from the stored monotonic end at recomposition — the
skeleton's §5.8 anchoring law, the VM's own stored-end doctrine
(`MonolithViewModel.kt:180–188`), the ticker demoted to a redraw
trigger. **The fill is not an animation of the proportion; it IS
the proportion.** That fact settles the easing question before
taste gets a vote:

- Any ease-in (accelerating) makes the final seconds drain
  *faster than time is passing*. The rule would lie, and the lie
  has a feeling: urgency. It is the fuse shape. **Banned.**
- Any ease-out (fast start, crawling end) makes the last sliver
  hang. Five honest seconds acquire a sixth, felt second of
  stalling at the exact moment his patience is thinnest. It
  reads as the door *moving the goalposts* — the one accusation
  this face can never afford, because its entire authority is
  that its numbers are true. **Banned.**
- `EasingA` / `EasingB` / `EasingC`, the overshoots, the
  mechanical interpolators in `MonolithCurves.kt` — all banned
  *for the drain*, however house-styled they are elsewhere. They
  remain the vocabulary of the door's fades (§4), which are
  opacity events, not measurements.

**Spec: `LinearEasing` over the derived fraction, no exceptions,
no per-visit variation.** Calm is what constant velocity feels
like from inside: after the first second he can predict where
the edge will be at every future moment, and a thing he can
predict stops being a thing he must watch. The rule's job is to
become ignorable. A draining rule he has stopped watching is the
wall working; a draining rule he cannot look away from is the
wall performing.

### 2.2 One moving edge

- The fill is **anchored at the left** — flush with the card's
  content-left edge, under the gate line's slot — and its **right
  edge recedes leftward** at constant velocity until it meets the
  anchor. The left edge never moves.
- **No centre-shrink** (both edges travelling reads as a thing
  closing in on itself — a closing aperture, a narrowing exit),
  **no right-anchored drain** (the receding edge would travel
  toward the gate figure's least significant digit and pull the
  eye right-to-left against the reading direction of the line
  above it).
- Exactly one moving edge is the calmest motion a rectangle can
  make: half the visual velocity of a centre-shrink at the same
  rate, and a stable landmark (the anchor) for the eye to rest
  against.

### 2.3 Texture and constants (all from the skeleton, felt here)

- Height **2dp for the whole visit** — it never thickens as it
  empties. A rule that grows in emphasis as it runs out is an
  alarm changing its voice. It never thins either.
- Track: `NightColorPrimary` #FFE4CF @ α0.20. Fill:
  `NightColorPrimaryContainer` #F47442 at full strength, for the
  whole visit. **No colour journey**: it does not warm toward
  `NightAlert` #FF8A5C, it does not fade toward the track, its
  alpha does not ramp in either direction. The fill at 0:01 is
  the fill at 0:05, only shorter.
- **No minimum-width clamp.** If the rendered width is floored
  (e.g. to a few dp) for the last fraction of a second, the edge
  visibly *stops* and then vanishes — a freeze followed by a
  jump, the two banned sensations in sequence. The width is the
  raw fractional width down to 0; sub-pixel rendering softens
  the final sliver, and the §4 crossfade replaces the stack
  before an empty track can stand alone for a perceivable frame.
- **No rounded-cap metamorphosis**: the fill's corners are
  whatever the skeleton's rule carries at full width, unchanged
  as it shrinks; a shrinking pill-shape that becomes a dot in its
  last second is a shape event in the banned last second.
- **Refresh**: the fraction derives at recomposition, driven by
  the frame clock / the redraw ticker — it must *glide* at
  display refresh, never step at the figure's 1Hz and never at a
  compromise rate like 10Hz. The contrast is the design: the
  figure steps (it is a reading), the rule glides (it is a
  feeling). A stepped rule would tick; a gliding figure would
  smear. Each gets the motion of its kind.

### 2.4 Stillness around it — no breathing

Decision #16 named this the "5s Breathing Delay Wall," and the
temptation will come to make something breathe. **Resist it on
this face.** No oscillation of card alpha, rule alpha, scale, or
elevation on any period: a breathing surface sets a second
rhythm running against the drain's one tempo, and at 1 AM a
slowly breathing card reads as a thing alive and watching him.
The door is furniture, not a creature. Except for the one edge
and the stepping figures, the face is a still photograph of
itself for the entire visit — name, intention, session line,
price, memory line, RETURN, honest exit, all printed from frame
one (the skeleton's SECOND-0 law) and all motionless.

### 2.5 Silence during the wall — the knock and the detent, answered

The audit files *"Schwelle's knock at HOLD 00 / Schliff's
per-second detent"* as raw fragments to Ton's bench, not ruled.
This seat's ruling, for Tor to ratify: **both are rejected for
the wall, by the same argument.**

- **No per-second detent.** Five (or ten) ticks turn the wall
  into a metronome, and a metronome is something a craving can
  *count along with* — it organises the wait into beats to be
  endured, each tick a small promise that the end is being
  approached on schedule, each tick re-summoning his attention
  to the gate at the moment it had drifted to the price, the
  intention, his own breathing. The wall wants his attention to
  *leave* the gate. Silence lets it.
- **No knock at 00, no arm sound of any kind** (§4.3).
- **No haptics during the wall** — no per-second buzz, no
  end-of-wall buzz. The latch haptics of the arrival (FIX-7c)
  are the visit's only touch event until he himself taps
  something. A phone that vibrates in a pocket or on a nightstand
  at the moment a peek becomes available is a notification that
  he may peek. This door does not send notifications.
- The wall's only voice is therefore the arrival voice FIX-7c
  already specs (the seal's recipe at door pitch: 300Hz, 44ms,
  gain 0.60, chordRatio 1.5, latch haptics kept), after which the
  door is silent until *he* makes a sound happen by acting.

**Accessibility corollary (mine, for Tor):** the gate line must
not be a per-second live region. A screen reader announcing
"0:04… 0:03… 0:02…" is the detent reborn as speech, at maximum
urgency. Spec: one polite announcement at open ("Peek opens in
{N} seconds," N the tapered total) and one at arm ("Peek
available"). Nothing between. `assertive` is banned on this face
in every modality.

---

## 3. Second by second — the 5-second wall

Times are *elapsed since the anchored wall start* (the trigger-
time end-mono of FIX-5/§5.8, not "since the fade finished" — see
§3.0). The gate figure is the ceiling-derived `M:SS` of the
designer's §5.2: it reads `0:05` for the entire first second and
never reads `0:00` (§4.1).

### 3.0 Frame zero — the wall does not steal its first moment

The wall's end is anchored at trigger time, and the entry fade
(180ms, §4.2's curve) runs *inside* the first second. Two
consequences, both deliberate:

- Because the figure is ceiling-derived, `0:05` stands for a
  full 1000ms of wall-clock regardless of the fade overlapping
  its first 180ms. He never watches a second he was promised get
  shortened by the door's own entrance.
- The full skeleton is composed at final geometry underneath
  the fade (skeleton §2). The fade is the whole card's opacity,
  once — there is no staggered entrance, no element arriving
  after another. A face that assembles itself piece by piece in
  front of a craving man is a face still deciding what it is.

### 3.1 The table

| Elapsed | Gate reads | Rule fill | What it should feel like — and what must NOT happen |
|---|---|---|---|
| 0.00–1.00s | `0:05` | 100% → 80% | **The settling second.** He is reading, not waiting yet: name (the face's one hero), his own intention bare under it, the session line, and — already standing, not arriving later — the price in Z2 and the memory line in Z3 if owed. RETURN is live and full-strength from the first frame (acquitted, six seats). The arrival voice has already ended at 44ms; the rest of this second is silent. Nothing about this second is a "start": no edge-in of the rule from 0% (it is born full — a rule that fills up first would add a false prologue and make the wall feel like 6 seconds), no figure warmup, no chip ghost preview. |
| 1.00–2.00s | `0:04` | 80% → 60% | **The door becomes furniture.** The figure's first step (instant replacement, §3.2) is the only event. By now he knows the whole face; there is nothing new to learn from it, by design. This is the second the shipped 88sp numeral hurt most — a glyph that size re-demands the eye on every change. At the gate line's 15sp statement rank (skeleton §5.2), the step is peripheral-vision information: available, not summoning. |
| 2.00–3.00s | `0:03` | 60% → 40% | **The midpoint, unmarked.** The rule crosses 50% with no notch, no detent, no change of any property — a marked midpoint would split the wall into "first half survived / second half to survive," giving the craving a progress narrative to negotiate with. There is no halfway on this door. There is only the same door, later. |
| 3.00–4.00s | `0:02` | 40% → 20% | **The familiar second.** For a returning man this is the stretch where the 5s wall has always ended soon; his body knows it, the door does not need to say it. Tempo, ink, silence: identical to second one. If any element were going to start "preparing" him for the offer — a chip fading up underneath, the gate line brightening — it would be here, and it is banned here: preparation is anticipation manufactured by the door, and manufactured anticipation is urgency wearing a kind face. Z1 contains the gate stack and only the gate stack until the arm instant. |
| 4.00–5.00s | `0:01` | 20% → 0% | **The last second — §3.3, the whole law of this slice.** |
| 5.00s | — (gate gone) | — (stack replaced) | **The arm — §4.** |

### 3.2 The figure's step

Each figure change is an **instant text replacement** at the
second boundary: no roll, no slide, no flip, no crossfade between
digits, no odometer. Rationale: a rolling digit is motion spent
on a reading (and a half-rolled digit is illegible for its
duration — at the one moment the print's job is to be read at a
glance); a fading digit double-prints (`0:03` ghosting through
`0:02`) for its fade's length. The tabular Anonymous Pro figures
(skeleton §5.2, Schliff's A-4) hold constant width, so the
replacement moves nothing — the line's bounds, the rule's
bounds, and every slot below are pixel-still across the step.
The step is the one permitted instantaneous change in the wall,
and it is a *print* change, not a layout, scale, colour, or
position change. It must land on the true anchored boundary:
because the figure derives from `end − now` at recomposition, a
stall or a busy frame makes the *next* figure late-but-true,
never early, never double-stepped to catch up theatrically —
and a stall that carries the reading past expiry skips the
intermediate figures entirely and arms (§4.4). The door never
re-performs elapsed time.

### 3.3 The last second — the law of nothing

From elapsed 4.00s to the arm instant, **no property of any
element changes except the two the laws permit**: the rule's
right edge continues at *exactly* its established velocity
(F-LAW 2), and at 4.00s the figure has stepped to `0:01` by the
same instant replacement as every other step.

Everything else is an enumerated ban, because "don't startle
him" is not buildable and this list is:

- **No speed change** — not in the drain (no ease-in tail), not
  in any animation anywhere on the face.
- **No colour change** — gate line, fill, card, RETURN: same
  tokens, same alphas as at 0:05. Specifically no migration
  toward `NightAlert`, no brightening of the gate figure as if
  to say *nearly there*.
- **No scale, weight, or size change** — the `0:01` is the same
  15sp Anonymous Pro Bold as the `0:05`. The shipped numeral's
  sin was making each digit an event; a last digit that swells
  would reinstate it for the worst digit.
- **No blink, pulse, flash, or opacity oscillation** on the gate
  line, the rule, or anywhere else — not even once.
- **No sound, no haptic** (§2.5). The last second is silent.
- **No new print** — no "almost," no preparation text, no chip
  preview, no price emphasis. Z2's price has stood since frame
  one; it does not get bolder now.
- **No behavioural change underneath** — RETURN and the honest
  exit are exactly as live, exactly as placed, as at frame one.
  The skeleton's stationarity law already guarantees their
  bounds; this law extends it to their *character*: the door
  does not lean toward the peek in its final second by making
  the alternatives feel any different.

Why so absolute: his anticipation at 0:01 is already maximal —
the craving supplies all the climax this moment can hold. Any
signal the door adds lands on a nervous system with no headroom,
and reads not as information but as a shove. The door's respect,
at the second where respect is hardest, is to be
indistinguishable from itself at 0:04. **The test is crude and
total (§6, V-2): screenshot the face at 0:02 and at 0:01, mask
the gate figure and the rule, and the two frames must be
pixel-identical.**

---

## 4. The arm — the exact moment, and how the 180ms lands

### 4.1 The instant

The offer arms at the **anchored wall end**: the first
recomposition at which the derived `wallEndsAtElapsedRealtime −
now <= 0`. Not when a delay loop wakes, not when a fade
finishes, not a frame of grace later. In that same recomposition:

1. The gate stack ceases to be composed. There is **no frame**
   in which the gate line reads `0:00`, and no frame in which an
   emptied rule stands alone under a dead line. `PEEK OPENS IN
   0:00` is a lie in form (it would name a gate that is open as
   still shut) and a *dead second* in feeling — the shipped code
   printed `00` and stood on it; that standing-on-zero is part
   of what made the old wall feel like a sentence being served.
   Zero is not a state this face prints. The gate is counting,
   or the chip is standing. There is no third frame.
2. The PEEK chip is composed in Z1's unchanged 48dp bounds, and
   the crossfade (§4.2) begins.
3. The grant machinery's word remains `peekOfferFor`,
   re-derived at tap time as today (`MonolithViewModel.kt:2393`,
   acquitted) — the arm changes the *offer's visibility*, never
   its truth. A chip whose world changed mid-wall still cannot
   sell a dishonoured lease, and its refusal remains silent
   (skeleton §8, PEEK TAKEN).

### 4.2 The 180ms fade — how it lands

FIX-6 prices the contents' fade at 180ms; FIX-7b settles the
door's entry at `fadeIn(specA(180))`; the skeleton's §5.4 makes
the counting→offer change a Z1-internal crossfade at the same
number. This seat's spec of *how* those 180ms run:

- **One crossfade, simultaneous, total 180ms** — gate stack
  fading out and chip fading in over the *same* 180ms window,
  both on `specA` (`EasingA`, CubicBezier(0.2, 0, 0, 1): fast
  commitment, no overshoot — a curve that arrives and settles,
  in the house's own SEAT register). Never sequential (out-then-
  in would double the event to 360ms and open a gap frame where
  Z1 stands empty — a blink at the climax). Never longer: at
  420ms (the old entry's specB ghost) the chip would spend
  nearly half a second as a rumour of itself, tappable-or-not
  ambiguous, while the man watches his reward *materialise* —
  that slow materialisation is a tease, and a tease at the arm
  instant is cruelty with good typography.
- **Opacity only.** The chip is composed at its final size,
  final position, final fill (#4A423C), final label and badge
  from its first frame; only its alpha travels. No scale from
  0.96, no rise from below, no overshoot settle
  (`MechanicalOvershoot` is banned anywhere in this transition).
  A chip that *pops* announces itself; a chip that *arrives in
  place* simply turns out to have been the slot's next tenant —
  the Innovator's born-occupied slot (P-1a) read as a feeling:
  one object changing state, not a second object arriving.
- **Input honesty — the fade does not extend the wall.** The
  chip is tappable **from the arm instant**, through the fade.
  A chip that becomes visible at 5.00s but refuses taps until
  5.18s makes the true wall 5.18s wearing a 5.00s print — the
  floor-then-ceiling disease in temporal form, the one direction
  this face never lies. The man who has waited, watching the
  slot, finger ready, and taps at 5.05s mid-fade has tapped a
  chip that is *there*; honouring it is the truth. (The
  accident-risk this might raise is answered by geometry, not
  by dead input: the chip sits in Z1 where his thumb has never
  been aimed — RETURN, the travelling thumb's target, is a
  different, stationary rect, per the skeleton's §3.4 addresses.)
- **The landing is a stop, not a settle-into-invitation.** At
  180ms the chip is at full alpha and thereafter **utterly
  static**: no glow pulse, no "now available" shimmer, no
  attention loop of any period, ever, for the rest of the visit.
  An armed chip that keeps asking to be noticed converts the
  offer into a solicitation. It stands the way RETURN stands —
  available, silent, his move.

### 4.3 No reward signal — the conditioning argument

Nothing in §4.1–4.2 makes a sound or a vibration, and this is
the most deliberate choice in this document. A success-chime or
buzz at the arm instant is a reward cue delivered at the peak of
a craving, paired with the appearance of the thing craved, on a
schedule he will meet nightly. That is a conditioning loop built
into a door whose stated purpose is the opposite. The latch
sound he *does* hear belongs to acts he chose (arrival, RETURN,
the grant — the machinery's existing voices, untouched), never
to the door granting him something. **The offer's arrival must
be the least celebrated event on the face.**

### 4.4 Late frames and skipped time

If the frame that crosses the end is late (stall, jank, the
activity returning from background), the door arms on that frame
directly: no replay of missed figures, no fast-forward drain
catch-up animation, no shortened "courtesy" crossfade apology —
the standard 180ms from the late frame, or, if the derivation
finds the wall *long* past (he left and returned), the chip
simply standing at full alpha with no transition at all. The
wall happened in anchored time whether or not he watched it;
the door does not re-perform it for an audience.

---

## 5. The tapered wall — how 10 seconds feels different from 5, without punishing

FIX-8: the wall is `5 + this session's booked intercepts of this
package`, capped at 10, gating the offer chip only — never
RETURN, never the withheld lines, never the price — with the
count printed in Z3 ("{ORDINAL} TIME THIS SESSION," the
Designer's truth-scoped wording, skeleton §5.7). The taper is a
fact before it is a feeling: it will be *perceived* as the door
remembering him. Whether that memory reads as respect or as
punishment is decided entirely by tempo, in four rulings.

### 5.1 The only mechanical difference: velocity, set once

A 10s wall's rule drains at half the dp/sec of a 5s wall's —
because the fraction is remaining/total and the total doubled.
That is the *entire* difference in the drain. Not a different
easing (linear, F-LAW 2, both), not a different colour, not a
thicker rule, not a second rule, not a segmented rule with ten
ticks where five stood before. **Segments are banned on the
tapered wall specifically**: a 10-segment rule would print his
count as architecture, make each segment's death a sub-climax,
and turn the memory line into a scoreboard he can watch being
filled. The rule stays one unbroken bar at every wall length.

Perceived honestly, the slower edge is *calmer per second*, not
heavier: in the first second of a 10s wall the edge moves half
as far, and the eye — which reads the rule's speed, not its
total — registers a broader, slower room. The taper's extra time
is experienced as spaciousness before it is experienced as
duration, provided nothing else escalates (§5.3).

### 5.2 Where the extra seconds live: the front

F-LAW 3 in narrative form. Plot the two walls by *remaining*
time and they coincide from 0:05 down; plot them by *elapsed*
and the 10s wall has five extra seconds at the start that the
5s wall never had. Spec the feeling accordingly:

| Phase (10s wall) | Gate reads | Rule | The feeling, and its discipline |
|---|---|---|---|
| The long settling | `0:10` → `0:06` | 100% → 50% | He is reading the face — and on a tapered visit, reading the memory line in Z3, which is new information the 5s visit didn't carry. The extra seconds are spent on the door's one new sentence: *third time this session*. That is information arriving while he has attention to give it, in the phase where a longer wall costs least. No property distinguishes these seconds except the slower edge and the figures themselves. Specifically **no midpoint event at `0:05`**: a tapered wall must not twitch, notch, brighten, or detent at the moment a plain wall would have armed — a phantom arm at halfway would teach him to brace twice, and the second brace is the punishment this section exists to prevent. |
| The familiar stretch | `0:05` → `0:02` | 50% → 20% | From `0:05` downward he is in the wall he knows. Figures step at the same 1Hz, the silence is the same silence, RETURN is the same slab. The rule's *fraction* differs from a 5s wall's at the same reading (50% vs 100% at `0:05`) — and that is correct and even kind: the bar honestly shows the whole visit's proportion, while everything he *acts* on by tempo — the figures, the stillness, the endgame — is the familiar one. |
| The last second | `0:01` | 10% → 0% | §3.3 applies **verbatim**, unamended. At 10s the final second drains the final 10% at the same constant velocity the wall has kept since `0:10` — slower in dp/sec than a 5s wall's final second (which drains 20% → 0%), identical in character: no change, no marking, no climax. A tapered wall whose last second *sped up to match* the plain wall's dp/sec would be an ease-in wearing a fairness argument; F-LAW 2 forbids it. What must be identical across wall lengths is the last second's *conduct*, not its geometry. |
| The arm | — | — | §4 applies verbatim: same instant discipline, same single 180ms crossfade, same silent landing. A longer wait must not buy a bigger arrival. If ten seconds earned a more emphatic chip — brighter, sounded, celebrated — the door would be paying interest on his craving. It pays nothing. |

### 5.3 No punishment, enumerated

What a punishing taper would do, each item banned by name so a
future hand cannot reinvent it as thoroughness:

- **No escalating ink.** The gate line, memory line, and rule
  carry the same tokens at wall 10 as at wall 5. The count line
  does not redden, bolden, or enlarge as the count grows. It is
  a fact in Anonymous Pro 10sp (skeleton §5.7) at every count.
- **No extra friction inside the visit.** RETURN's liveness,
  the honest exit's liveness, the price's wording, the chip's
  48dp — identical. The taper spends its whole price on the one
  thing it is: the chip's arrival time. A taper that also
  dimmed RETURN "until he's reflected" would be a second,
  unpriced wall hiding inside the first.
- **No compounding tempo.** Successive tapered visits do not
  change the drain's character — visit four at 8s feels like
  visit two at 6s plus two front seconds, never like a door
  growing impatient. The door has no impatience to grow; its
  velocity is arithmetic, not attitude.
- **No audible or tactile count.** §2.5's silence holds at every
  length. Ten seconds of detents would be ten summonses; the
  longer wall needs *more* silence, and gets it by getting the
  same.
- **The count never gates the feeling of the end.** He may know
  it is the third time; the door's tempo must not act as if it
  knows. Memory lives in Z3's print and in the wall's length —
  nowhere in its manner.

### 5.4 The cap, felt

At the cap, wall = 10, further intercepts this session lengthen
nothing. Felt consequence worth speccing: a man on his sixth
reach meets the *same* 10s wall as his fifth — the door's
memory saturates exactly where its print does (the ordinal in
Z3 keeps counting honestly past the cap; only the length stops
growing). There is no "max-level" change of face at the cap —
no new line, no final-warning register. Saturation is silent,
like everything else on this door.

---

## 6. Verification the builder can encode (paper, for Ren's compile)

Feel claims that cannot fail a test are decoration. These can:

- **V-1 Constant velocity.** Sample the rule's rendered width at
  anchored elapsed 1s, 2s, 3s, 4s on a 5s wall (and 2s-spaced
  samples on a 10s wall): successive deltas are equal within
  rendering tolerance. Any easing, stall-clamp, or min-width
  floor fails this.
- **V-2 Last-second invariance.** Compose COUNTING at remaining
  2s and at remaining 1s; mask the gate figure's glyphs and the
  rule's fill region; assert pixel-equality of everything else
  (colours, alphas, bounds, RETURN, exit, price, memory line).
  This is §3.3 as an assertion.
- **V-3 Zero is unrepresentable.** Drive the derivation across
  the arm boundary at fine steps; assert no composed frame
  contains `0:00` in the gate line, and the first frame with the
  chip composed is the first frame whose derived remaining
  is <= 0.
- **V-4 The fade is not wall.** At arm, assert the chip's
  clickable is enabled in the same composition that first
  composes it (crossfade start), and the crossfade's total
  duration spec is 180ms on `specA`, opacity-only (no scale /
  translation values anywhere in Z1's transition spec).
- **V-5 Endgame equivalence (F-LAW 3).** Compose a 5s wall and a
  10s wall both at remaining 3s: assert identical gate figure,
  identical slot bounds everywhere, identical tokens/alphas on
  every element; the *only* permitted differences are the rule's
  fill fraction and Z3's memory line. This test is the taper's
  non-punishment, made executable.
- **V-6 Silence.** Assert no sound-engine or haptics call is
  reachable from the wall's ticking/arm path: the arm transition
  invokes neither `TactileSoundEngine` nor any haptic feedback.
  (Static reachability over the overlay + the state transition,
  in the spirit of the Innovator's P-4 pure-print matrix.)
- **V-7 Born full, born composed.** At the first composed frame
  of COUNTING: rule fraction == 1.0 (within the first frame's
  derived elapsed), price composed (Z2), memory line composed
  if owed (Z3), RETURN enabled. No frame exists with the rule
  partially "filling in."

---

## 7. Rulings this seat needs from Tor

1. **§2.5 — the knock and the detent, rejected.** The audit left
  both fragments to Ton's bench unruled; this slice says the
  wall is silent end to end. If Tor rules a detent in, it
  contradicts F-LAW 4's conditioning argument and §3.3's ban
  list, and I ask that the contradiction be recorded, not
  smoothed.
2. **§4.2 — tappable from the arm instant, through the fade.**
  Input-honesty over accident-caution, argued from geometry
  (Z1 is not the travelling thumb's rect). The alternative —
  input live at fade end — makes every printed wall length a
  180ms understatement, and I ask Tor to price that lie
  explicitly if he rules it.
3. **§2.5 corollary — TalkBack announces twice, never per
  second.** Mine, unpriced by the audit; SMALL (two
  announcements on existing state transitions).

---

## NEXT (Law 10)

(a) **What I want to do next:** feel-spec the *other* end of the
peek — the taken lease's 120 seconds inside the app and the pill
that carries them (the Innovator's P-3b flag: whether that pill
is rendered anywhere in this tree is still open). The wall's
tempo law (constant velocity, silent arm) has a mirror problem
there: a lease that *ends* is a countdown too, and its last
second and its expiry re-intercept are where a homecoming man
(Proposal P-5) meets this door again. The two specs should share
one tempo doctrine, written once, by one seat.

(b) **What I could do, unassigned:** carry F-LAW 3 (endgame
equivalence) and V-2/V-5's masked-screenshot method to the
Disarm face's 60-second valve — the app's other long hold, where
the same question (does the last stretch change character to
manufacture climax?) is currently unasked, and where the valve's
banked-quarters mechanics make a tempo audit genuinely
different work, not a copy.

— ATEM, Feel Specialist 1, Tor's cell, Issue #11.
Report to Tor; nobody compiled; the user was not messaged.
