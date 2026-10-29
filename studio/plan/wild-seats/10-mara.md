# Mara — Wild, light

## The one rig (defined once, obeyed everywhere below)

Everything in this file is a single mechanism wearing five bodies. There is no
second light idea in it.

- **One source, fixed in space, forever.** Direction `L = normalize(−0.42, −0.91)`
  in screen space, ≈25° off vertical, arriving from the upper left. Highlight
  edges are always top/left 1px; shade edges always bottom/right 1px. No element,
  state, loadout, hour, or theme ever moves this vector. Only *position of the
  moving part* changes what the light does.
- **Light carries no data — this is the whole design.** Specular never encodes
  duration, readiness, progress, elapsed time, armed-ness, or selection. Those
  facts each already have exactly one rendering (position, ink, words, numerals).
  Light is a material property of metal moving under a lamp, nothing more. Any
  variant below that asked light to *mean* something was killed for that reason,
  including three of my own wave-1 ideas (see the rejections threaded through the
  runner-ups).
- **Light is not accent.** Specular colour is desaturated warm cream/white,
  ≤35% saturation at peak, alpha-capped (day peak 14% over a cap fill, night
  peak 8%). It is never orange, never displaces orange, and never counts against
  the accent budget: orange stays a COMMIT fill only — Deck slide, Tasks START —
  with zero orange in chrome. At night the resulting composited colour of any
  control fill is clamped so it never renders lighter than the locked night
  fill cap `#4A423C`, and specular is never drawn over text, so the night text
  tiers (≈11 / 5.6 / 3.4:1 on the `#171412` staircase) are untouched.
- **One shared implementation.** A single `LightRig` exposes exactly two pure
  functions and nothing else:
  `specularAlpha(absoluteAngle)` for rotary parts and `bandOffset(travelPx)`
  for linear parts. No control invents its own highlight; inconsistency between
  screens is impossible by construction, not by review.
- **No independent life.** The rig owns no animator, no clock, no timer, no
  state. Every value it returns is a function of a position the host control
  already has. When nothing moves, nothing is repainted for light's sake.

**Explicitly dead in this file, by the assignment and by the truth standard:**
no dawn ceremony in any form (no First Light, no horizon dawn, no sled-pulled
sunrise), no temperature rig (dragging the loadout does not relight the room),
no lume/afterglow (no charge, no decay, no ghost of the last touch), no lit-room
seam that brightens per block. Each is named once more where it lost, so the
rejection is on the record and not just implied.

---

## Deck — wheel and slide-to-arm sled

**Chosen form — Travelling Specular on the wheel, inherited by the sled**

- **Mechanism / data source.** The wheel is drawn in one Canvas in three passes.
  Pass 1: face disc, existing one-tone-step recess, matte. Pass 2: the knurl —
  each existing rim tooth segment is drawn as a 2dp radial line whose alpha is
  `alpha = 0.16 + 0.66 × max(0, cos(toothAngle − lightAngle))³`, where
  `toothAngle` is the tooth's **absolute screen angle** and
  `lightAngle = atan2(L.y, L.x)`. Because the formula reads screen angle, not
  wheel-relative angle, turning the wheel moves teeth *through* a highlight
  that stays put in the room: teeth flare entering the upper-left zone and die
  leaving it, exactly like a machined crown under a lamp. Pass 3: rim ticks and
  numerals at fixed low alpha, except the single tick currently inside the
  light zone, which gets +20% alpha — the printed scale itself glints as it
  passes. The input is `wheelAngleDeg` and nothing else; no new state exists.
  The slide-to-arm sled inherits the linear form (specced fully under Shield,
  since it is the same part): an 18dp cream band inside the sled's clip whose
  centre lags travel by `bandOffset = −0.35 × travelFromRest`. Over the sled's
  orange COMMIT fill the band's alpha is capped at 8% and its top 1px catch
  edge carries the effect, so the orange stays unmistakably orange — the fill
  is the accent, the cream is weather on it.
- **Motion.** No curve of its own, ever. Specular updates 1:1 with the drag at
  frame rate because it *is* the drag, re-lit. On release it rides whatever
  spring the wheel/sled already settles with (wheel settle ≈520ms to detent,
  sled per its host spring) — the glint rocks once with the overshoot and
  stops. That single rock is the entire animation budget of this surface.
- **Haptics.** The rig requests none. It is timed to coincide with the host
  control's existing detent vocabulary and adds nothing to it: 5-minute tick
  5ms @ 0.35, 15-minute major 8ms @ 0.55, end-stop knock 20ms @ 0.95. If the
  host haptics are off, the light is unchanged — proof it was never carrying
  the feedback.
- **Sound.** Explicit silence from the rig. The seal keeps its own existing
  sound; there is no ratchet track, no velocity-scaled clicking. A light that
  makes noise per tooth is a second rendering of spin speed, and spin speed
  already has a rendering: the spin.
- **Depth / material.** This is what turns the knurl from a printed texture
  into a material claim: anisotropic machined metal, brightness as a function
  of facet angle to one source. Face stays matte (exponent ^3, broad and low);
  if the rim pass is split out it goes machined (^8, +15% peak). Recess lines,
  panel edges, and the readout well never animate — the panel is the room;
  only the moving parts catch light. Depth stays tone steps, hairlines, and
  recess; the specular is a 1px/alpha phenomenon, never a blur, glow, or drop
  shadow.

**Runner-up — The Fixed Wedge.** One soft gradient wedge, fixed in screen
space over the rim annulus; the wheel rotates beneath it and teeth brighten by
compositing alone. One draw call, no per-tooth math. *Why it lost:* the wedge
is a gradient looking for a surface — its soft edge reads as glow or shadow
(both banned as depth), it cannot produce the hard per-tooth flare that sells
machined metal, and any clip slip paints light across the face and readout,
where the chosen form structurally cannot go. Cheap, and visibly cheap in
exactly the place Deck cannot afford it: its hero.

**Standards check.** *Arm-path cost:* zero gestures, zero milliseconds —
specular is computed inside draws the arm path already makes. *Mid-session
decisions introduced:* none; in the running state the wheel is locked, so the
light is frozen with it. *Truth risk:* one, named — a glinting tick could be
misread as the selected tick. Mitigated by construction: the glint zone never
moves, selection is said by the wheel's position against its printed scale
and by the total readout, and the glint is alpha-only on a tick that is
already printed. Light flatters the scale; it never points at it.

---

## Shield — caps, app-row sleds, and the loadout band

**Chosen form — The Lagging Band (one linear specular for every sliding part)**

- **Mechanism / data source.** Every linear control — duration caps, the
  loadout cap on its STUDY / DEEP / SLEEP band, each app-row sled
  (SHUT / 2m / OPEN), and the Deck slide sled above — draws the same band
  inside its own clip: a linear gradient, transparent → cream-white at peak
  alpha (14% day / 8% night, composited result clamped to the `#4A423C` night
  fill cap) → transparent, 18dp wide, blended Screen by day and low-alpha
  SrcOver by night so dark fills are lit, never lifted past their step. The
  band's centre is `restCentre + bandOffset(travel)`, with
  `bandOffset = −0.35 × travelFromRest`: for every 1dp the cap travels, the
  highlight slides 0.35dp the other way. Drag the loadout cap STUDY→SLEEP and
  its highlight visibly leans and lags, like light raking across a fader cap
  being pushed through it. At a detent the band sits at its rest offset for
  that stop — three stops, three rest offsets, all computed, none stored.
  Inputs are the caps' animated positions only; when a sled's fill/label keys
  to animated position, the band keys to the same value, so light, fill, and
  label can never disagree mid-flight.
- **What it refuses to do.** The loadout drag changes *nothing else on the
  screen*. No temperature sweep, no re-toning of rows, no warming of hairlines
  elsewhere. Loadout is already rendered once — by the cap's position and the
  word at the margin. A room that relights itself per loadout would be that
  same fact rendered a second time, in weather.
- **Motion.** Pure function of travel, 1:1, no lag filter and no easing of its
  own — the 0.35 counter-slide *is* the perceived lag; adding a temporal lag
  on top would make the highlight trail the finger on release and read as
  latency. On landing, the band arrives on the same frame the cap's spring
  settles; there is no after-settle shimmer.
- **Haptics.** None requested by the rig. Host detent vocabulary it coincides
  with: loadout/app-sled landing double — 6ms @ 0.5, 30ms gap, 16ms @ 1.0 —
  fired by the control, on the frame the band reaches its rest offset.
- **Sound.** Explicit silence. Switch clicks, where they exist, belong to the
  controls and are unchanged in pitch per stop — no warmer/cooler click
  grading, because graded pitch would be the temperature lie wearing ears.
- **Depth / material.** The band lives *inside* the cap's clip and under its
  marker line: the cap's single cream marker line stays constant and crisp on
  top, the specular moves beneath it. State is still silhouette and position
  alone — a cap reads identically in grayscale with the band deleted, which is
  the test every surface here passes. Recesses and slots keep their static
  top/left catch edges from the same vector, so a resting screen and a moving
  screen are lit by the same lamp.

**Runner-up — Edge-Flip.** No travelling band; instead a cap's top edge is lit
at its left stop and its bottom edge lit at its right stop, cross-fading at
mid-travel. Half the code, no gradient. *Why it lost:* at the cross-fade the
light teleports from one edge to the other — a source that jumps is a source
that lies, and the binary flip reads as a *state indicator* (lit edge = which
end you're on), smuggling data back into the light. The chosen band's
continuous counter-slide is the only version that stays a material and never
becomes a signal.

**Guard fork — his call.** The rig is indifferent to Guard's fate, and says so
rather than quietly assuming either outcome. *If Guard dissolves:* its sleds
and switches land in Shield's list and chrome, and they bring the Lagging Band
with them unchanged — same function, same caps, no migration cost. *If Guard
survives as a destination:* its ARM sleds use the identical `bandOffset()`,
with one hard rule — light never becomes Guard's armed indicator. Armed is
said by sled position and word, once; a sled that also *glows* armed would be
the fact twice, and a glow in chrome-adjacent space would smuggle an accent
where chrome is allowed none. Placement is Sarthak's open pick; the light's
behaviour is not.

**Standards check.** *Arm-path cost:* zero — no surface here is on the cold
arm path except the Deck slide, specced above at 0 gestures / 0ms. *Mid-session
decisions:* none; loadout and sleds are locked while sealed, and their light
locks with them, mid-position if that is where the seal caught them — the
light never "completes" a position the control didn't reach. *Truth risk:*
the band's rest offset differs per stop, so in principle it redundantly
rhymes with position. Accepted, because it never *leads* position, is never
readable at a glance as a label, and disappears entirely at night AOD levels —
position remains the only rendering anyone can actually read a state from.

---

## Chrome — bottom navigation

**Chosen form — The Travelling Cap, lit like every other cap**

- **Mechanism / data source.** The bottom nav is a flush recessed rail with a
  travelling cap and printed stops — and the cap is a cap, so it takes the
  rig's linear form unmodified: the same 18dp band, the same
  `bandOffset = −0.35 × travelFromRest`, driven by the cap's animated x during
  a destination switch. Its marker/content stays constant cream; the specular
  moves under it. Input is the nav cap's position animation and nothing else.
  The rig is destination-count agnostic: it neither knows nor cares which
  stops exist, so the killed Ledger tab's absence changes nothing here — the
  rail is lit the same for three stops or four.
- **Motion.** Bound to the host switch: the band exists only inside the cap's
  existing travel animation (≈260ms, host curve) and is still at both ends.
  There is no idle shimmer, no periodic sweep, no "breathing" rail — chrome
  that moves when untouched is chrome asking for attention it hasn't earned.
- **Haptics.** None from the rig; the nav's existing landing tick, where the
  host provides one (6ms @ 0.35 at focus landing), is unchanged.
- **Sound.** Explicit silence.
- **Depth / material.** The rail's recess keeps its static catch edges from
  the same vector; the cap is the only moving object in chrome, so it is the
  only lit object in chrome. **Zero orange** — no armed lamp, no lit dot, no
  exception. The FOCUS stop is found by the cap sitting on it, full stop.

**Runner-up — Printed Stops.** Each stop's label gets a small static specular
tick, pre-lit as if engraved, and the cap itself stays unlit. *Why it lost:*
four pre-lit zones is four light sources — it breaks the one-source law the
whole rig exists to enforce — and a permanently lit destination reads as a
badge or a state, duplicating what the cap's position already says. Chrome
gets the travelling treatment or nothing; it gets the travelling treatment
because the cap genuinely travels.

**Standards check.** *Arm-path cost:* zero; nav is not touched on the normal
arm path, and when it is, the light adds no time. *Mid-session decisions:*
none — during a sealed session the nav is locked down with everything else,
cap and band frozen together. *Truth risk:* none identified beyond the
surface above; the band cannot be read as a count, a badge, or a notification,
because it has no state of its own to read.

---

## AOD — the desk clock, restrained

**Chosen form — Frozen Specular (the rig, suspended)**

- **Mechanism / data source.** On AOD the rig does almost nothing, on purpose,
  and the almost is precisely specced. Nothing on the AOD face moves, so
  nothing on it travels: the travelling functions are simply never called.
  What remains is the source's *existence*, attested once: the clock panel's
  top/left hairline is drawn at its static rest catch (1px, cream at 6%
  alpha), computed from the same fixed vector, in the same coordinate space as
  the anti-burn-in shift, so when the composition shifts the edge shifts with
  it exactly — the light belongs to the object, not the screen. Everything
  else is off: no band on any element, no per-tooth anything, no halo.
  Digits are plain Anonymous Pro at their locked tiers on true black
  `#000000`; specular is never drawn over or behind them. The black stays
  true — the lit pixel budget of the whole face is the hairline and the
  digits, nothing more.
- **Motion.** None. Repaints happen only on the minute tick the clock already
  repaints on, plus its existing position shift. No fade in, no settle, no
  decay curve — a face that animates on a desk at 3:30 AM is a face performing
  for nobody.
- **Haptics.** None, ever. **Sound.** None, ever.
- **Depth / material.** AOD depth is the recess the digits sit in, rendered in
  tone as locked; the single static catch edge is the minimum evidence that
  the recess is a real edge in the same room as the rest of the app. Restraint
  here is not the absence of the rig — it is the rig keeping its one promise
  (same source, same direction) at the lowest energy at which the promise is
  still kept.

**Runner-up — Lume & Afterglow, trimmed.** My own wave-1 night idea, cut down:
elements take a charge on touch and decay exponentially (AOD τ = 10min), so
the clock is brightest just after arming and settles to ember across a block.
*Why it lost, in full:* (1) Charge is interaction history rendered as glow —
a second rendering of facts (what you touched, when you armed) that already
have homes, and a ghostly one that can outlive its truth. (2) Decay requires
repaints AOD discipline forbids, and every extra lit pixel is burn-in risk
purchased with battery. (3) A halo around digits blurs the locked text tiers
and lifts true black. It was the most beautiful idea in my wave-1 file and it
fails all three standards at once. Dead, not deferred.

**Standards check.** *Arm-path cost:* zero — AOD is post-arm by definition,
and the frozen frame adds no wake, no wait, no repaint. *Mid-session
decisions:* none; there is nothing on the AOD face to decide, and the light
asks for none. *Truth risk:* the one real one in this file — a static bright
edge must never be allowed to drift into meaning "session running." It
cannot: it is present identically whether the clock shows a running session
or an idle time, because it is a property of the panel, not the session.
Elapsed and remaining stay where they belong, in the numerals, once.

---

## Too good to lose

1. **The occlusion notch — RAW FRAGMENT, deferred, not planned.** When a sled
   or cap passes *under* a nearer hairline or overlay edge, its band should be
   clipped to zero across exactly that x-range — light blocked by a nearer
   object is the cheapest proof of one source in a room. No current screen
   routes a moving part under an overlay, so there is nowhere honest to ship
   it yet. Spec if a surface ever earns it: clip only, no new element; band
   alpha × 0 across the occluder's span, hard edge, 1:1 with travel; no
   haptic, no sound. It loses today on scope, not on merit.
2. **The composer's catch-light (Tasks, focus only).** When the Tasks composer
   field gains focus, its recess's top inner edge takes the rig's static
   catch at day alpha — the recess catching the same upper-left source more
   strongly because you've leaned in. Still (focus is a state, not a travel),
   no motion, no haptic, no sound, no colour; 180ms fade on focus change only
   because the host field fades. It lost on assignment scope — my brief was
   wheel, caps, sleds, loadout, nav, AOD — not on standards: it costs the arm
   path nothing, decides nothing, and says nothing. It is the one place a
   *static* surface may be lit, because focus is the user arriving, and
   arriving somewhere in the same room should not change the lamp — only your
   angle to it.

## Mail / wall note

Inbox empty at start and at pause. Before finishing, one letter had arrived:
Orson (Wild) forwarding Sarthak's order lifting the citation requirement —
read, moved to `read/`, and, as that order instructs, forwarded verbatim by
me to the inboxes of all 19 other Wild seats under the same thread. This file
was written under that lifted regime: no citations are carried as quota. One
signed thought posted to the Commons wall (05:16 IST): light must never carry
data — the claim this whole file is built on, put where the other seats can
attack it.
