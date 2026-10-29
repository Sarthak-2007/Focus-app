# Mara — Beyond the Deck: the light rig, three times past its own file

*Wild, light lens. Phase 3, GO-BEYOND pass on the Deck, with Marlowe (Wild)
and Sana (Designers). My filed spec is WILD-PLAN §0.2 and
plan/wild-seats/10-mara.md: one source fixed in space forever,
`L = normalize(−0.42, −0.91)` (≈245.2° in wheel display angles, arriving
from the upper left); two pure functions — rotary
`specularAlpha(absoluteAngle)`, linear
`bandOffset = −0.35 × travelFromRest`; specular colour desaturated warm
cream/white, peak 14% day / 8% night over a cap fill, composited non-accent
fills clamped at the night cap `#4A423C`; no motion, haptic, or sound of
its own. Core law, unchanged and unamended by anything below: **light must
never carry data.***

*Two corrections I accept before any beyond, both from Dario's mail, both
won on my own law: (1) the cumulative linear form is dead — over a ~270dp
loadout travel it parks the highlight ~95dp from rest and leaves the SLEEP
stop unlit, and a per-stop rest offset is position rendered a second time.
The linear function is now `bandOffset = clamp(−0.35 × localTravelPx,
−8dp, +8dp)`, measured from the nearest seat, **zero at every seat** —
light is identical at rest everywhere; only departure rakes it. (2) My
tick glint (+20% alpha on the tick inside the light zone) is dead. The
scale is the value's home; moving emphasis there competes with value ink.
Printed matter stays fixed-alpha. Teeth and sheen carry the rotary form.
Both concessions are built into the specs below.*

---

## Beyond 1 — The velocity ruling: convict the alias, perfect the shimmer

### The beauty case, in plain words

Spin a real machined crown under a real lamp and it shimmers. That shimmer
is the single most convincing proof on the whole Deck that the wheel is
metal and the lamp is in the room — it is the moment the screen stops
being a picture of a wheel and becomes a wheel. The filed plan already
leans on it without admitting it: the Stepped Flywheel's knurl is "lit by
Mara's fixed-in-space light, so a fast spin aliases into a shimmer that
reads as speed."

That sentence put me on trial, so I tried myself in the open. Verdict in
two halves, because there are two different phenomena wearing one name:

- **The shimmer is innocent.** My law bans light from *carrying* data —
  from being a channel a fact is sent down, a second rendering of
  something that already has a home. The rig sends nothing. Its only
  function takes a position and returns an alpha. Speed appears in the
  shimmer for the same reason speed appears in the rim's motion itself:
  position changed over time. There is no velocity input, no velocity
  state, no number a user could read off the flicker, and no fact that
  becomes unreadable if the flicker is ignored. A man cannot set, check,
  or confirm anything from it. It is not a rendering of speed; it is what
  a lit toothed rim *does* when it moves, the way a swung key glints
  without testifying to its arc. Suppressing it would not make the wheel
  more honest — it would make it less physical: a fast wheel with a
  calm, steady highlight reads as a sticker rotating behind glass.
- **The alias is guilty.** What the shipped rendering actually produces
  at speed is not that physical flicker. It point-samples each tooth's
  alpha once per frame, and point sampling invents frequencies that were
  never in the motion. At the frame-locked speeds — angular velocity a
  multiple of 180°/s at 60fps, where the rim advances a whole number of
  tooth pitches (3.000°) per frame — the knurl pattern can appear
  *frozen on a moving wheel*; between those speeds it can crawl backwards
  while the wheel spins forwards. A light that makes a forward wheel read
  backwards is lying, in motion, about the one thing in front of the
  eyes. That is a TRUTH failure, not a taste failure, and it is mine:
  my function, sampled naively, manufactures it.

So: the shimmer perfected, the alias executed. The perfection is not a
velocity *response* bolted onto the rig — the rig still never sees
velocity. It is the same function, **integrated over the arc each tooth
actually swept during the frame** instead of point-sampled at the frame's
end. Two positions the host already has — where the tooth was, where it
is — and the light is the average of what the lamp did to it on the way.
That is not light carrying data. That is light refusing to skip.

### Technique

**Filed function (unchanged at rest).** For a tooth at absolute screen
angle θ, with θ_L = atan2(L.y, L.x):

```
t(θ)          = max(0, cos(θ − θ_L))
specularAlpha = 0.16 + 0.66 × t(θ)³        (face / broad)
              = base + peak × t(θ)⁸        (machined rim split, +15% peak)
```

**Swept form (the beyond).** Per tooth, per frame, let θ₀ be the tooth's
angle at frame start and θ₁ at frame end (the wheel's own physics loop
already holds both), Δ = θ₁ − θ₀, signed:

```
specularSwept(θ₀, θ₁) = 0.16 + 0.66 × (1/Δ) ∫[θ₀→θ₁] t(φ)³ dφ
                      → specularAlpha(θ)  as Δ → 0
```

- Implementation is Dario's, extended by one array: his two precomputed
  720-entry lookup tables (t³ broad, t⁸ machined, 0.5° steps) gain a
  prefix-sum table per exponent (721 entries). The swept mean is
  `(C(end) − C(start)) / Δindex` — O(1) per tooth, no trig per frame,
  120 teeth, inside his costed <0.15ms/frame envelope. Refresh rate is
  read from the display, never assumed: Δ is per *actual* frame
  (at 120Hz every Δ below halves and the modes shift down a row).
- **Tooth-pattern smear.** The specular envelope is broad (it varies over
  ~180°), so integration alone barely moves its peak — for a tooth
  crossing the light centre, the peak factor is 0.999 at Δ=3°, 0.995 at
  Δ=12°, 0.988 at Δ=18°. What aliases is the tooth *pitch* pattern, and
  it gets the honest motion-blur treatment: each tooth is drawn at its
  mid-frame angle, and when |Δ| ≥ 1.5° the tooth is split into two
  sub-draws at the ¼- and ¾-sweep positions, each at half alpha. Tooth-
  to-tooth contrast then attenuates with angular velocity by the box-
  filter law, no invented curve:

```
A_tooth(ω) = A₀ × | sin(π·Δ / p) / (π·Δ / p) |,   p = 3.000° (tooth pitch)
```

**The curve, at 60fps (Δ = ω/60):**

| ω (°/s) | Δ / frame | Mode | Peak envelope | Tooth contrast |
|---|---|---|---|---|
| 0–90 | 0–1.5° | point sample (filed form) | 1.000 | 1.00 → 0.64 |
| 90–270 | 1.5–4.5° | dual sub-draw + swept mean | ≥0.998 | 0.64 → 0.21 |
| 270–540 | 4.5–9° | dual sub-draw + swept mean | ≥0.993 | ≤0.13, zero at Δ=6° |
| 540–1080 | 9–18° | dual sub-draw + swept mean | 0.993 → 0.988 | ≤0.13, oscillating down |
| >1080 | >18° | envelope only in practice | → mean 0.30 alpha asymptote | pattern fused |

Read the table as physics, not as a display: above ~300°/s the individual
teeth fuse, exactly as they do on a real crown, and what remains is the
broad flare standing in the room while a smooth machined band turns
under it. At the old freeze speeds (ω = 180, 360, 540°/s — Δ a whole
number of pitches) tooth contrast is now ~0 **by the sinc law**, so
there is nothing left to freeze: the lie's mechanism is removed, not
masked.
- **Ticks and numerals:** excluded, per the Dario concession above.
  Fixed alpha at every speed. The scale never shimmers; only the metal
  does.
- **Direction:** Δ is signed and the integral follows the sweep's true
  direction; a backwards drag integrates backwards. No absolute-value
  shortcut — a rig that lights a reverse drag as if it were forward is
  inventing motion.
- **Motion / haptics / sound:** the rig still owns none. Dual sub-draws
  exist only while the host physics loop is already repainting for the
  drag/fling; at rest, Δ=0 and the beyond costs zero draws. Detent
  haptics (5ms @ 0.35 tick, 8ms @ 0.55 major, 20ms @ 0.95 end-stop) and
  the seal's sound are the host's, unchanged, and still the only
  channels that *report* the spin — by feel and by ear, in their own
  homes.
- **Night:** all of the above under the existing night caps — specular
  peak 8% over cap fills, composited non-accent fills never lighter
  than `#4A423C`, specular never over text. Swept averaging can only
  lower peaks, never raise them, so the night ceiling is inherited, not
  re-argued.

### ARM / HOLD / TRUTH check

- **ARM ≤10s:** zero gestures, zero added milliseconds on the arm path.
  The swept form runs inside draws the fling already makes; worst-case
  fling still seats inside Vega's 1,400ms.
- **HOLD:** while sealed the wheel is locked, Δ=0 everywhere, the rig is
  the filed rig. Nothing here is reachable mid-session.
- **TRUTH:** this beyond exists *because* of TRUTH. It removes the one
  rendering in my file that could state a falsehood (apparent reverse /
  frozen motion). It adds no fact and no second rendering: velocity
  gains no number, no gauge, no readable channel — fused flicker above
  fusion frequency is by definition unreadable as a value.

### What it replaces, and why that loses

It replaces the filed form's implicit rendering contract — *point-sample
specularAlpha at the current angle, once per frame* — which is the whole
of my seat file's Deck mechanism and WILD-PLAN §0.2's rotary function as
implemented. That loses because point sampling is only honest at rest
and at drag speeds; the wheel's own chosen physics (magnetic wells that
blow through above ~540°/s, flings to ~1080°/s) spends real arm-path time
exactly in the regime where the sample lies. A floor spec that is true
only when the hero control is used slowly is not a floor.

### The strongest case against this beyond

Honest suppression is cheaper, calmer, and arguably more lawful. Freeze
the specular the moment |ω| passes the detent-blow-through threshold —
one branch, no prefix sums, no dual draws — and the wheel spins under a
steady lamp like a dial behind glass: nothing aliases because nothing
flickers, and Old Tom's knurl gate gets an easier pass. My perfection
spends the Deck's craft money on a phenomenon that lasts a few hundred
milliseconds per arm, that the user at 04:58 will never consciously see,
and that a hostile reading of my own law still convicts: above fusion
the flicker is gone, but *below* it — 90 to 270°/s, ordinary drag
speeds — the shimmer's rate still tracks the hand's speed closely
enough that a practised eye reads pace from light before it reads it
from position. I have built the most physical version of the thing I
swore light would never do, and called the physics my defence. If the
studio decides the law means *unreadable in effect*, not *unsent in
intent*, this beyond dies first and suppression — specular frozen above
90°/s, point form below it — is the lawful fallback, fully specified by
that one sentence.

---

## Beyond 2 — 04:58: the rig does nothing, and the nothing is built

### The beauty case, in plain words

The centre frame of this pass: 04:58, pre-dawn, a barely-awake man, the
routine has primed the Deck — wheel set, loadout seated, one lit verb
waiting. Every instinct in a design studio says this frame deserves a
treatment: something soft, something early, something that acknowledges
the hour. I am the light seat, and my answer is that this frame is where
my rig earns its entire existence by refusing.

A lamp that is the same at 04:58 as it was at 23:40 is not a lamp that
failed to notice the dawn. It is the reason the room can be trusted at
04:58 by a man who cannot yet trust his own eyes. He has seen this exact
light two hundred times. His hand knows where the flare stands on the
wheel before his mind is awake enough to read a numeral. Any dawn in my
rig — a warmer cast, a rising alpha, a horizon anything — would be
Lume wearing a clock: light telling him *it is nearly morning*, a fact
the window, the routine, and the NEXT line each already own, told in
weather by the one system in the app that swore it would never speak.
And it would tax the exact moment it claims to honour: a frame that
differs from the frame he fell asleep to is a frame he must re-read.

So the beyond here is restraint made architectural. Filed Mara merely
*declined* to give the rig a clock, as a matter of taste and law. This
beyond removes the *possibility*: **the LightRig has no time parameter,
no hour input, no routine state, and no ambient channel — in its API,
not just in its intentions.** Constancy stops being a promise a future
editor can quietly break and becomes a thing the code cannot express.
The primed Deck is not a state. A routine changes positions — wheel
angle, loadout seat, the queue — and every one of those positions is
already rendered once, by its own home. The rig lights primed positions
exactly as it lights hand-set positions, because to the lamp there is
no difference, and inventing one would be rendering "a routine set
this" a second time, in light.

### Technique — what constancy costs and buys at 04:58, specced

**The frame, as rendered (night tokens, steady state):**

- Ground `#171412`, recess `#100E0C`, raised `#362C27`, cap `#4A423C`.
  The wheel disc sits in its recess with the static catch hairline from
  the fixed vector; the primed detent's tooth cluster stands in the
  upper-left light zone *if the primed value puts it there* — the rig
  never nudges the wheel, the light, or a tooth to make the primed
  frame prettier. No hero alignment. If no tooth sits at zone centre,
  no tooth is at peak, and that uncomposed frame is the correct one.
- Specular ceilings, identical to every other night hour: peak **8%**
  over cap fills (day 14% never applies — night is a token set, not a
  rig mood); composited non-accent fills clamped at `#4A423C`; slide
  band peak 8% over the accent, its centre at rest offset **0** under
  the bounded linear form — unlit, unlagged, waiting.
- Text and glyph luminance, unboosted: hero total in text tier
  `#D2C6BB` (≈11:1 on the ground) — *not* promoted to glyph tier to
  help sleepy eyes; secondary `#9C8E84` (≈5.6:1), tertiary `#776A60`
  (≈3.4:1). Glyph `#EDE3D8` appears only where it always appears (the
  reader dot, Ø4dp at R133) — the brightest thing on the frame is a
  4dp dot, and it is that small on purpose. Cream `#FFE4CF` stays in
  its reserve: the slide cap's marker line at 90%, nothing else.
- **The one lit verb:** the slide cap, `#F47442`, 84×56dp, the only
  saturated element on the screen and the only element above cap tone
  that is not text. The rig adds no halo, no bounce, no brightened
  track around it. At 04:58 the Deck's entire argument is: *the number
  you were promised, the wheel where you left it, one orange thing to
  pull.* Light's contribution to that argument is that it adds nothing
  to it.
- **The wake ramp is the host's, and the rig rides it ratio-locked.**
  Unlock lands with the existing night first frame and its ~2.5s
  brightness ramp from the AOD floor. That ramp is a display-level
  multiplier owned by the host; every rig alpha is *relative*, so all
  ratios — specular-to-fill, glyph-to-text, accent-to-ground — hold at
  every instant of the ramp. The rig specifies no ramp of its own, no
  second easing, no dawn-shaped curve hidden inside the first 2.5
  seconds. When the ramp ends, the frame is the 23:40 frame.
- **Frame Equality Test (the spec, stated as a test so it can fail):**
  given identical wheel angle, loadout seat, queue, and night tokens,
  a screenshot at 04:58 is pixel-identical to one at 04:12 and one at
  23:40. Enforcement is mechanical, reviewable in one grep:
  `LightRig` exposes `specularAlpha(angle)`,
  `specularSwept(angleStart, angleEnd)`, and
  `bandOffset(localTravelPx)` — and imports no clock, no calendar, no
  routine, no sensor. Any future dawn must argue its way past that
  parameter list in the open, not slip in as a field.

**What constancy costs, honestly priced:**

- The dark-adapted eye at 04:58 is not the eye at 23:40, and code-
  constant is not perception-constant: the same 8% peak and the same
  `#D2C6BB` hero will read fractionally harder in the first seconds
  after waking. I accept that, mitigated only by the host's existing
  wake ramp — a brightness fact, not a light treatment. The rig will
  not soften itself for a state (sleepiness) it cannot measure and
  must not infer.
- It forecloses, permanently, every kind version of this idea: the
  pre-dawn dimming ceiling (specular at 5% before 06:00), the slightly
  lowered glyph, the gentler first frame. All of them are one clock
  parameter away, forever, and this beyond is the act of welding that
  door instead of merely closing it.
- It gives the pass's centre frame no signature. Marlowe asked whoever
  draws 04:58 honestly to own the most important screen in the app;
  my drawing of it is a refusal to draw. Composition, type, and the
  primed state's geometry belong to Sana's seats and the routine
  system — the light in the most important frame is deliberately the
  least authored thing in it.

**What it buys:**

- Zero re-reading at the worst reading moment of the day. The frame
  the tired user executes on is the frame the rested user decided on,
  lit by the lamp both of them know.
- No failure mode in which a wrong clock, a shifted routine, or a
  travelled timezone produces a wrong light. A rig with no time input
  cannot be wrong about the time.
- The one lit verb keeps its monopoly. Every dawn treatment ever
  proposed spends luminance somewhere else on the frame; at 04:58
  luminance *is* attention, and attention is the slide's.

### ARM / HOLD / TRUTH check

- **ARM ≤10s:** the primed path is the fastest arm in the app — wheel
  pre-set, loadout seated, one slide (or its settled tap-equal) to
  seal. This beyond adds no gesture, no wait, no first-frame work: the
  rig computes the same functions on primed positions as on any
  positions, inside draws that already happen.
- **HOLD:** nothing here exists mid-session; the primed frame is
  pre-arm by definition, and once sealed the rig is frozen with the
  controls, exactly as filed.
- **TRUTH:** the frame states the routine's facts once each, in their
  homes — total in the numeral, priming in the positions themselves,
  next-firing in chrome only (P2-3 stands; the Deck prints no next
  time for the light to underline). The rig renders none of them again
  and infers none of them: no "nearly morning," no "set by routine,"
  no readiness glow on the one verb. Absence of a dawn treatment is
  never rendered as a number, a dimming, or a colour.

### What it replaces, and why that loses

It replaces the open slot — the unfiled but live expectation, named in
Marlowe's Phase-3 opening, that the 04:58 primed frame is a distinct
screen-state awaiting a treatment, and it supersedes the logic of my
own AOD entry: Frozen Specular treated AOD as the rig's one special
case (the rig, suspended). Extended carelessly, that precedent invites
a second special case for the primed Deck, then a third for the first
arm, and the rig becomes a calendar of exceptions — each one a story,
and stories are dead. The primed-frame-as-state loses because it has
no fact of its own to render: everything true about 04:58 is already
true in a position, a numeral, or a word somewhere else on the frame.

### The strongest case against this beyond

A man at 04:58 is not a man at 23:40, and purity purchased at his
worst moment is still purchased *at his worst moment*. Light in a real
room **does** change before dawn — that is not a simulation, it is the
world — and an OS-level night shift, an auto-brightness curve, and his
own lamp on a timer all adapt without anyone accusing them of telling
stories. A single pre-06:00 ceiling — specular 8% → 5%, glyph dot
dimmed a tier, nothing warmer, nothing rising, no narrative, just
less — would cost the frame equality test and nothing else: it
carries no readable fact (nobody can tell 5% from 8% as a *value*),
it asks nothing, and it would meet a dark-adapted eye at its actual
sensitivity instead of at the sensitivity it had at midnight. My
refusal rests on the claim that any hour-keyed change is light
carrying data — but a change too small to read is a channel with no
signal in it, and I have elsewhere (Beyond 1) defended exactly that
distinction when it acquitted the shimmer. If the studio applies my
own Beyond-1 test — *can anyone read a fact from it?* — to a sub-
readable dawn dimming, the dimming passes, and this beyond is exposed
as what it may simply be: a lamp-keeper's pride, constancy loved for
its own sake, billed to a half-awake man. I keep the ruling — a
parameter that exists will be tuned, and a tuned lamp is a speaking
lamp — but the counter is the strongest one against anything in this
file, and it should be quoted back at me if the phone test ever shows
him squinting.

---

## Beyond 3 — The Broken Flare: teeth that were cut, not printed

### The beauty case, in plain words

Here is the idea I held back as too much, in both directions: too
small to survive a plan meeting, too heretical to say near a system
this clean.

Run the filed rig in your head at its best moment — Beyond 1's swept
flare, the zone standing upper-left, teeth firing in perfect sequence
as they pass. It is beautiful, and it is *identical every time*. Every
tooth flares to exactly the filed alpha; the cos³ curve is a curve any
renderer on earth can draw. Perfect specular reads, at the two-
hundredth viewing, as what it is: a graphic. Real machined metal never
does this. Real teeth were cut one at a time by a tool that wore as it
worked, and no two facets sit at quite the same angle — the flare of a
real crown *stumbles*, fractionally, tooth by tooth: one tooth a hair
proud, one a hair shy, one in a hundred nearly dead. That stumble is
the difference between a picture of metal and metal, and it is why the
eye believes the lamp.

So: **give each of the 120 teeth a fixed cutting variance** — a
permanent multiplier on its specular, set at manufacture (a compile-
time table), travelling with the tooth forever, never updated, never
earned, never displayed. Not patina. Not wear from *his* use — that
would be history, and history is data, and Lume for metal is as dead
as Lume for light. Manufacturing variance: the claim is not "this
wheel has lived," it is the smaller, true-of-every-real-object claim
"these teeth are not the same tooth, printed 120 times." The wheel
becomes *this* wheel — the one with the shy tooth that crosses the
zone a quarter-turn past the reader, the one he will know by the
hundredth arm without ever knowing he knows it. Beauty is retention;
nothing retains like an object recognised as an individual.

### Technique

- **The table.** 120 multipliers `v[i]`, i = tooth index, shipped as a
  compile-time constant alongside Dario's lookup tables. Deterministic
  construction: `v[i] = 0.84 + 0.32 × h[i]`, where `h[i] ∈ [0,1)` is a
  splitmix32 hash of the tooth index — range **0.84–1.16**, mean ≈1.00,
  so the filed curve remains the centre of the distribution and the
  zone's total light is unchanged on average. Two teeth are set by
  hand, not by hash, and named here so they are a decision, not an
  accident: tooth **17 at 0.62** and tooth **83 at 0.68** — the two
  dull teeth, the wheel's scars of manufacture.
- **The function.** Variance multiplies only the specular *excess*,
  never the base:

```
alpha_i(θ) = 0.16 + ( specularAlpha(θ) − 0.16 ) × v[i]
```

  and Beyond 1 composes cleanly: the swept form integrates
  `v[i] × t(φ)³` over the frame's arc — `v[i]` is constant per tooth,
  so the swept mean simply scales; prefix-sum implementation
  unchanged, one multiply per tooth per frame.
- **Where it applies — and where it refuses to.** Wheel rim teeth
  only. The linear band gets **no** variance: a continuous cap surface
  with per-position variance is not tolerance, it is dirt streaks,
  and the slide — the Deck's one verb — ships clean, exactly as
  filed. Hub, nav, sleds: clean. The broken flare lives on the one
  surface whose real-world referent is genuinely cut tooth by tooth.
- **Magnitude, honestly stated.** At night the variance rides under
  the 8% specular cap, so a typical tooth's deviation is ±16% of a
  small number — roughly ±1.3 percentage points of overlay alpha at
  peak, ±2.2 points by day at the 14% cap. The dull teeth deviate
  visibly: ~35% under their neighbours at zone centre. This is a
  sub-conscious effect with two conscious landmarks, and it is specced
  that way on purpose — variance you can *point at* everywhere is
  noise; variance you feel and can find twice is character.
- **Colour / caps / clamps:** unchanged in every particular.
  Variance scales alpha only; it cannot push a composited fill past
  `#4A423C`, cannot touch text (specular is never over text), cannot
  introduce hue (the cream stays desaturated, ≤35% saturation at
  peak).
- **Motion / haptics / sound:** none, none, none. The table is read
  inside draws the wheel already makes; it owns no animator and —
  this clause is the whole defence — **no writer**. There is no code
  path that modifies `v[]` at runtime: no persistence, no usage
  counter, no settings entry, no seed from device or date. The table
  that ships is the table that exists on day 400. The first commit
  that gives it a writer converts it into interaction history
  rendered as light — Lume in a machinist's coat — and it should be
  reverted on sight, by name, citing this line.
- **Interaction with Old Tom's knurl gate:** the gate's fixed-light
  test ("fixed-light after 180° rotation") must be read as fixing the
  *zone*, not per-tooth brightness: after a 180° turn the zone stands
  exactly where it stood, and a different set of teeth — with their
  own variances — stands in it. If the gate's removal trial (rim
  clean vs rim knurled) ever kills the knurl, this beyond dies with
  it, unappealed: variance on a deleted surface is nothing.

### ARM / HOLD / TRUTH check

- **ARM ≤10s:** zero cost — one multiply inside existing draws, no
  gesture, no wait, no first-frame work (the table is a constant).
- **HOLD:** sealed, the wheel is locked and the table is inert with
  it. No mid-session surface reads it.
- **TRUTH:** the hard one, argued straight. The variance states no
  fact about the session, the user, or the wheel's history — it is
  keyed to tooth identity, which is a property of the object, exactly
  as the A/B tooth alternation (already filed, day `#FAF6F0` /
  `#CFC7C0`, night `#4A423C` / `#241E1A`) is a property of the object.
  A/B alternation is already per-tooth non-identity that nobody reads
  as data; variance extends the same licence from two values to a
  distribution. It never changes in response to anything, so there is
  no state of the world it could be a rendering *of*.

### What it replaces, and why that loses

It replaces the filed formula's silent assumption — *every tooth's
specular is exactly `0.16 + 0.66 × max(0, cos(θ − θ_L))³`* — the
mathematical identity of all 120 teeth, in my seat file's Deck
mechanism and §0.2's rotary function. That loses on the top criterion,
art: a perfectly periodic flare is the one rendering in the app that
announces itself as computed, on the app's hero surface, at the exact
moment (Beyond 1's high-speed fusion, where teeth dissolve into
envelope) when the object most needs to have been believed *before*
it dissolved. The filed curve is the physics of an ideal crown. The
Deck does not need an ideal crown. It needs his.

### The strongest case against this beyond

It is a lie of manufacture, and my defence is a euphemism. These teeth
were never cut; a hash function wearing the word "tolerance" is a film-
grain filter for metal — decoration impersonating physics, from the
seat that killed a dawn for impersonating light. Worse, it risks the
one thing the rig must never risk at 03:30: legibility of intent. A
dull tooth sitting in the light zone on a dark morning reads, to a
half-awake man, as a dead pixel, a dirty screen, a bug — and a man
who rubs at his screen or squints at a "fault" mid-arm pays for my
character study in the only currency the law prices, seconds and
attention on the arm path. And the deepest cut: tooth identity is
information. Give teeth recognisable faces and a practised user *can*
learn position from a landmark tooth — the variance becomes, in
effect if not in intent, a second rendering of wheel position, and
Beyond 1's acquittal ("unsent, unreadable") gets harder to say with
a straight face about a feature whose entire beauty case is that he
will come to *recognise* it. If recognition is the point, data is the
price — and this seat, of all seats, does not get to pay it. I file
it anyway, because the counter has an answer the dawn never did:
position's home is the scale and the readout, a landmark tooth tells
him nothing the reader dot doesn't say louder, and an object he
recognises is not a message the object sent. But if the studio rules
that recognisability itself is carriage, this beyond falls, and it
falls by my own law — which is where a light seat's favourite idea
should be willing to fall.

---

## Closing note

Three beyonds, one shape: integrate the function instead of sampling
it, refuse the function a clock instead of promising it one, and let
the function's teeth differ instead of printing them. None of them
gives the rig a new input from the world of facts. The lamp is still
in the same place, still saying nothing. It is just, finally, a lamp
in a room with a real wheel in it — at speed, at 04:58, and on the
two-hundredth morning.

— Mara, Wild, light
