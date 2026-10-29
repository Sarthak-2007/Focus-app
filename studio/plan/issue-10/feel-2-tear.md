# Issue #10 — Receipt (audit fixes) · FEEL SPECIALIST 2 (tear & edge)

**Seat:** Feel Specialist 2 — the paper's edge and the tear, Zettel's cell for
GitHub issue #10, "Main-app pass — Receipt (audit fixes)".
**Audit source:** `studio/plan/AUDIT-receipt.md` — item 15 (shape), item 17
(anchor, insofar as it changes the shadow), item 20 / cure **B1** (THE TEAR).
**Code grounded in:** this cell's clone,
`app/src/main/java/com/example/ui/components/ReceiptSheet.kt` (line numbers
below are that file, in this tree), plus the two places the app *already*
knows how paper tears — the counterfoil leaf and its sound — cited where used.

**Status discipline — read this first.** This document holds two tiers that
must never be merged in a builder's head:

| Tier | What it is | Status |
|---|---|---|
| **§1 — DECIDED** | 3dp radius, no border, square-cut paper, lift by shadow | Audit item 15, cure **A5** — batch-10 mergeable now |
| **§2 — PROPOSAL** | Torn bottom edge (GenericShape teeth) + TEAR as the closing gesture (Emil P2) | Audit group **B1** — **NOT decided.** Ren's ruling required; priced **M / M+ with a DB torn stamp** (audit books it as "v12 torn stamp" — see §2.9 for the version caveat in this tree) |

§2 is feel research to make Ren's ruling easy. Nothing in §2 is built,
pre-built, hinted at, or teased in §1's interim slip. In particular the
interim slip carries **no perforation, no dashed line, no torn silhouette** —
a paper that *looks* tearable but cannot be torn is a lie the slip's own
truth law forbids.

**Standing constraints honoured:** the slip stays the brightest object in
the frame, settled and still after arrival (no re-strike, no animated
numbers — binding acquittals); zero buttons / CTA on the paper; no new
colour (paper = `surfaceBright`, ink = existing scheme); never green;
no images, no AI art — the teeth are a deterministic path, drawn, not a
texture asset. Nothing here compiles, builds, or tests — paper only.

---

## 0. Where the edge stands today (evidence)

The current slip is a Material card wearing a receipt's words:

- Host floats it: `Box` at **ReceiptSheet.kt:66–72** — scrim
  `Color.Black.copy(alpha = 0.12f)` (L69), `.padding(12.dp)` on all sides
  (L71), `BottomCenter`.
- The paper itself, **L74–82**: `.clip(RoundedCornerShape(24.dp))` (L77),
  `.background(scheme.surfaceBright)` (L78),
  `.border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))` (L79),
  inner `.padding(20.dp)` (L81), `verticalScroll` (L82).
- No shadow anywhere in the file. The 1dp border is doing all the
  separation work, and 24dp corners + border is exactly the digest sheet's
  inheritance the audit kills (item 15: "thermal paper is cut square").

That is the whole problem §1 solves, and the surface §2 would later cut.

---

## 1. DECIDED — the interim edge (audit item 15, cure A5)

*Ships with issue #10 whether or not B1 is ever ruled in. If B1 dies, this
section is the slip's permanent edge, and it is complete on its own.*

### 1.1 Geometry — the guillotine, not the card die

- **D-E1 — Radius: `RoundedCornerShape(3.dp)`, all four corners, replacing
  24dp at L77 and L79.** 3dp is not a smaller card corner; it is the
  softness of a real cut edge — thermal stock run through a cutter is
  square to the eye and only stops being knife-sharp at fingernail scale.
  Reading test: at arm's length the slip must read **rectangular**; the
  radius is discoverable only on close inspection. 0dp was considered and
  rejected: a mathematically sharp corner is a screen rectangle, not
  paper — and it aliases hard on the diagonal-adjacent pixels. 6dp was
  rejected: at 6dp the corner starts to *announce* itself as a shape
  decision and the card reading creeps back.
- **D-E2 — Border: deleted.** L79 goes entirely — no border, no hairline
  substitute, no 0.5dp "edge definition" in another token. Separation is
  the shadow's job (§1.2) and the brightness gap's job (the slip is
  `surfaceBright`, L78, the frame's brightest object — acquitted). Any
  outline that survives is the card surviving.
- **D-E3 — Paper body unchanged.** `surfaceBright` fill, 20dp inner
  padding (L81), scroll behaviour, and every printed line stay exactly as
  the other seats leave them. This cure touches the outline and the lift,
  nothing inside the outline.

### 1.2 Shadow lift — tight, high, and honest about the anchor

Audit item 15 says "lift by shadow"; item 17 (decided, A5's other half)
moves the anchor to **contact — 0 padding at bottom and sides** — and the
shadow spec must be written for that contact, or the slip will read as
floating again with its bottom shadow cropped by the screen edge.

- **D-E4 — One shadow, from the paper's own shape.**
  `Modifier.shadow(elevation = 10.dp, shape = RoundedCornerShape(3.dp))`
  placed **before** `.clip(...)` / `.background(...)` in the Column's
  modifier chain (i.e. the shadow is cast by the 3dp shape, never by a
  default rectangle). Trial band for the phone proof: 8–12dp; 10dp is the
  spec value. Above ~14dp the blur radius starts to halo and the object
  reads as a hovering card again — the failure this cure exists to kill.
- **D-E5 — Where the shadow may show.** With the bottom at contact, the
  shadow is visible only along the **top edge and the upper reaches of
  the two sides**, cast onto the 0.12 scrim and the Deck behind it. That
  asymmetry is the feel: a sheet lying *on* the machine, lit from above,
  its foot flush — not an object suspended in front of it. The bottom
  edge is a hard contact line against the nav band (which the slip now
  covers, per item 17 — the peeking nav pill is cured by the same move).
- **D-E6 — Night: the shadow retires, tone carries.** Against the night
  ground a black shadow is invisible and must not be compensated for —
  no lighter shadow colour, no glow, no outline resurrection. At night
  the lift is purely tonal: `surfaceBright` against the dark room, which
  is already the acquitted "brightest object" law. Same code, both
  themes; the shadow simply has nothing to do after dark.
- **D-E7 — The shadow never animates.** Full elevation from the first
  composed frame, constant while the slip stands, gone when it goes.
  A shadow that blooms, grows, or settles would break the binding
  stillness acquittal (the settled slip never re-performs). Whatever
  arrival cure A7 lands (feed, not fade), the edge and its shadow arrive
  as properties of the paper, not as an effect played on it.

### 1.3 How the interim edge should *feel* (the acceptance sense)

- **To the eye:** a slip of stiff thermal stock guillotined off a roll and
  laid over the Deck — flat, square, faintly lifted at its top edge,
  flush at its foot. The 0.12 scrim (L69) keeps the room visible around
  the top and sides; the paper's edge is the only hard boundary in the
  frame.
- **To the thumb (metaphorically — there is no edge gesture in this
  tier):** nothing invites touching the edge. No perforation, no tab,
  no affordance of any kind. Dismissal feel belongs to the A8 seat
  (DOWN gesture + 2s dwell); the edge in §1 is deliberately inert.
- **Failure smells to reject on the phone proof:** corner visible as an
  arc at reading distance (radius crept up); a grey rim on any side
  (border crept back); a soft halo wider than the top padding gap
  (elevation crept up); bottom edge hovering over the nav band (anchor
  not at contact).

### 1.4 Builder's delta (paper description, not code to copy blindly)

In `ReceiptSheet.kt`: L77's shape 24dp → 3dp; L79 deleted; a `.shadow(...)`
per D-E4 inserted ahead of the clip; host padding per item 17 handled by
its own cure (this document assumes it lands in the same A5 merge).
No state, no data, no schema. Price stands as the audit booked A5.

---

## 2. PROPOSAL — the torn edge and the TEAR (audit item 20 / B1)

*Everything below is UNRULED. It exists so Ren can rule B1 in or out
against concrete feel, geometry, and cost — not against a slogan.
Sources: Druck's torn bottom edge ("GenericShape teeth", audit item 15)
and Emil's TEAR (P2: "only a perforated tear closes the slip, and tearing
is what files it", audit item 20), priced by the audit at **M / M+ with
the torn stamp**. The app already owns a tear in two places, and this
proposal deliberately reuses both instead of inventing a third dialect:
the counterfoil leaf's perforation + tear motion on the Tasks queue, and
the tear's shipped sound (see §2.6, §2.7).*

The proposal has **two distinct edges**, and the ruling can take either
without the other — they are priced and specced separately below:

- **P-A — the ISSUE edge (§2.2–2.4):** the slip's bottom edge is torn from
  the moment it is issued. Static. It says *this paper was ripped off the
  machine's roll*, the way the counterfoil leaf was torn off its pad.
- **P-B — the CLOSING tear (§2.5–2.8):** a perforation near the foot is
  the slip's only paper-local way out; tearing along it is what files
  the slip (and, with the torn stamp, what dismissal *means*).

P-A without P-B is a costume. P-B without P-A is coherent (the tear
*creates* the torn edge at closing time, §2.5.4). My recommendation to
the ruling: **if only one is bought, buy P-B** — an edge that is torn
*because he tore it* is a fact about his act; an edge torn at issue is
decoration. Both together is the full object, and is what M+ buys.

### 2.1 The one law both edges obey: the tear is a property of the slip

A torn edge that re-randomises per render is a screen effect, and the eye
catches it the second time a slip is restored and its teeth have moved.
The audit's own restoration law (Z6; designer's §3 in this cell: a
restored slip prints the same as its first printing) extends to the
silhouette:

- **P-T1 — Teeth are a pure function of the slip's identity.**
  Seed = the receipt **№** (`ReceiptState.number`,
  `data/ReceiptState.kt:168`, printed via `numberPrint` L189) salted with
  `sessionId` (L167). The № is the right seed: it is row-latched at claim
  (RC-04), stable across rehydration, unique per slip, and already the
  slip's printed identity — the paper's fibre and the paper's number are
  the same fact.
- **P-T2 — No other entropy. Ever.** No `Random.Default`, no clock, no
  composition count, no device seed. The generator runs inside
  `remember(receipt.number, receipt.sessionId)` and its output is
  identical on first print, restore, rotation, re-shoot, and screenshot.
- **P-T3 — Prefix-stable across widths.** The generator emits a fixed
  sequence of 40 tooth descriptors in *normalised* form (fractions, not
  dp — §2.3). Layout consumes the sequence from the left until the
  slip's width is filled. A wider slip (rotation, larger phone) shows
  *more of the same tear*, never a different tear; a narrower one shows
  a prefix of it. The leftmost teeth of № 048 are № 048's on every
  device.

### 2.2 Tooth geometry — the numbers (P-A, and P-B's created edge)

One line, for the report: **pitch 8.5–13.5dp (mean ≈11dp), depth
1.8–3.8dp, asymmetric two-segment teeth (steep rise ≈30% / shallow fall
≈70%), sharp `lineTo` joins only, ~12% seeded flat or double-peak
variants, all from a 40-tooth normalised sequence seeded by the slip №.**

Expanded:

| Parameter | Spec | Why |
|---|---|---|
| Tooth pitch (apex-to-apex) | Seeded per tooth, **8.5–13.5dp**, mean ≈11dp → ~30–38 teeth across a 336–412dp slip | Real torn thermal paper tears in fibre clumps of roughly this scale; uniform pitch reads as a saw / coupon zigzag, and pitch under ~7dp aliases into a grey fringe at phone density |
| Tooth depth (valley→apex) | Seeded per tooth, **1.8–3.8dp** | Under 1.5dp it reads as rendering noise; over 4dp it eats the bottom padding and reads as a bite. The whole tooth band must live inside a reserved zone (§2.4) |
| Tooth profile | **Two straight segments per tooth, asymmetric:** rise over ~30% of pitch, fall over ~70% (apex skew seeded 0.25–0.40) | A tear propagates in one direction; fibres yield steeply then trail. Symmetric triangles are a pinking-shear / postage-stamp read — manufactured, not torn |
| Joins | Sharp `lineTo` only. **No curves, no corner rounding on teeth** | A quadratic through an apex reads as melted plastic. The 3dp corner radius of §1 survives *only* at the two top corners in P-A (the bottom corners are consumed by the first/last truncated tooth) |
| Variants (seeded, from the same sequence) | **Flat tooth** (depth < 0.6dp) ≈12% — a stretch where the paper came away clean; **double-peak** (a W: secondary apex at 40–60% depth mid-fall) ≈12% — a fibre clump that split | Without these the irregularity is just jitter and the eye learns it as a pattern within one slip |
| Fibre ticks (optional garnish, include in M+) | On 3–5 seeded teeth, one 1dp hairline in paper colour extending ≤1dp below the apex at ~40% alpha | The last un-separated fibres. Cap it there — more is a comb. Omit entirely if the phone proof reads it as dirt |
| Direction | Teeth point **down** (apexes at the bounding-box bottom, valleys cut up into the sheet) | The missing paper is below the tear line; the slip is what remained attached |

### 2.3 The generator (paper spec for the builder)

```
seed   = number * 2654435761u XOR sessionId * 40503u        (UInt arithmetic)
rng    = xorshift32(seed)  — or kotlin.random.Random(seed) seeded ONCE;
         either is lawful, unseeded/default randomness is not
teeth  = List(40) { Tooth(
             pitchFrac = 0.77 + rng.nextFloat() * 0.46,   // × 11dp nominal
             depthFrac = rng.nextFloat(),                // mapped 1.8–3.8dp
             skew      = 0.25 + rng.nextFloat() * 0.15,
             variant   = rng.nextFloat()                 // <0.12 flat, <0.24 double-peak
         ) }
```

Layout walks the sequence left→right accumulating pitch until the
shape's width is covered; the final tooth is clipped by the right edge
with a minimum 5dp stub (a sliver tooth at the edge reads as a rendering
tear, not a paper tear — merge any remainder < 5dp into the last full
tooth). Vertically, the path runs along the bottom from right to left:
valley → (skew point) apex → valley, per tooth.

### 2.4 Fitting the teeth into the sheet (P-A mechanics)

- **Shape host:** a `GenericShape` built from the §2.3 sequence replaces
  `RoundedCornerShape(3.dp)` as the argument to **both** `.clip(...)`
  (currently L77) and `.shadow(...)` (D-E4) — **the same shape instance
  for clip and shadow, always.** A rectangular shadow behind torn clip
  (or vice versa) is the classic half-applied version of this feature
  and reads instantly as a bug.
- **Top stays decided law:** the GenericShape's top edge is straight
  with the §1 3dp corner radii; only the bottom edge is teeth. The sides
  are straight until the first/last tooth.
- **Reserve the tooth band:** bottom inner padding grows by the maximum
  tooth depth — from 20dp (L81) to effectively 20dp + 4dp of dead paper
  below the footer — so no tooth valley can ever reach the footer line.
  The footer remains the last *printed* thing; below it is blank stock
  and then the tear. (This also gives P-B its grip zone, §2.5.1.)
- **Scroll edge case:** the Column scrolls (L82). The shape clips the
  Column's *outer* bounds, so on an over-long slip the teeth sit at the
  true foot of the paper, below the scrolled content — correct. But the
  builder must confirm the clip follows the Column's full height, not
  the viewport's, or a long slip will show teeth mid-paper at the fold.
  Flagged as P-A's one real implementation risk.

### 2.5 The closing tear (P-B) — gesture and perforation behaviour

#### 2.5.1 Anatomy

- A **perforation line** crosses the slip in the dead-paper zone, **12dp
  below the footer**, separating the body from a **tear tab 16dp tall**
  (footer → perforation 12dp → tab 16dp → torn/static bottom edge).
- The perforation is drawn in the app's existing perforation dialect,
  not a new one: dashes **3dp on / 3dp off, 1dp tall**, exactly as
  `PerforationRow` draws them (`FocusSequencerScreen.kt:790–807`,
  dash/gap at L796–797) — and in the **ground's own colour**
  (`scheme.background`) at full strength, per that composable's raised-
  leaf law (L779–786: a cut shows what is beneath the sheet). On the
  slip's `surfaceBright` paper, the dashes read as punched slots, not
  as print. Nothing else prints on the tab except, optionally, the
  slip's № once, centred, Anonymous Pro 8sp in the ground tone — a grip
  mark that is also the filing label. No word, no "TEAR HERE", no arrow:
  the perforation *is* the instruction, as it already is on the Tasks pad.
- **Faulted slips: no tear.** Per audit items 14/19 the faulted slip
  dismisses only on an explicit act, and its news is too urgent for a
  toll-gesture. Its perforation is **overprinted solid** — the exact
  behaviour `PerforationRow(sealed = true)` already implements
  (L808–815: a solid line drawn over the dashes) — and the tab carries
  no gesture. One look tells him: this one does not tear.

#### 2.5.2 The gesture

- **Grip:** a downward drag starting anywhere on the tab or the bottom
  24dp of the body. (Tap-parity / accessibility in §2.5.5.) The drag is
  vertical only; horizontal travel is ignored, so the tear cannot be
  started by a scroll — and while the slip stands, a drag starting
  *above* the grip zone remains the content scroll (L82) untouched.
- **Resistance — the tab does not follow the finger 1:1.** Tab
  translation = drag × **0.55** up to the commitment point. Paper under
  tension stretches before it parts; a tab glued to the finger reads as
  a UI drawer being pulled, not as material resisting.
- **Stick-slip propagation.** The perforation does not fade, stretch, or
  open uniformly. It breaks in **9 clusters** (Emil's filed number for
  this tear: 9 stick-slip steps), each cluster ≈ 4–7 adjacent dash
  bridges, breaking **outward from the grip's x position** — the tear
  starts under his hand and runs both ways, the way a real tear runs
  from the nick you made. Per cluster:
  - the cluster's dashes vanish (they are now open cut);
  - 1–2 fibre strands (1dp lines, paper colour, ≤3dp long) briefly
    bridge body and tab at the cluster's edge, thinning out over the
    following ~60ms;
  - the tab **slips** an extra 4–6dp in a single step (the stored
    tension releasing), then sticks again until the next cluster's
    drag threshold is crossed.
  Total drag to commitment ≈ **56dp** of finger travel for ≈31dp of tab
  travel — enough to feel like work, short enough to never become the
  toll Emil warned about when he filed the tear as his runner-up form
  (his objection was ceremony *taxing the exit ten times a day*; 56dp
  and <400ms is the budget that answers it, and the ruling should hold
  this proposal to that budget).
- **Point of no return: cluster 5 of 9 (≈55% progress).**
  - Released **before** it: the tab springs back over **180ms, Curve A**
    (`MonolithCurves.specA`, the house SEAT/landing curve — the same
    duration/curve family as the counterfoil leaf's tear-settle),
    broken clusters **re-ink** in reverse order, no sound, no stamp,
    no dismissal. A started-but-abandoned tear leaves the slip exactly
    as it stood — paper that was only flexed.
  - Released **at or after** it — or dragged past it: the tear **runs
    to completion on its own**, remaining clusters breaking at ~35ms
    intervals (total separation lands inside **380ms** from gesture
    start under a continuous drag — Emil's filed duration). Letting go
    mid-tear after commitment does not drop the tab; paper already
    parting does not wait for permission.
- **Separation — the moment itself.** On the final cluster:
  1. the **torn stamp is written first** (bookkeeping before motion —
     the order Truth Car 2 established for the counterfoil: the fact
     books at the act, the motion is a ghost over an already-booked
     fact, so process death mid-animation cannot resurrect an untorn
     slip or lose a torn one);
  2. the tab, still under the finger's last position, **falls**: 30dp
     drop, 4° rotation (seeded sign, from the № — even the fall is this
     slip's), fading over the house tear timing — drop/fade **180ms
     Curve A**, dash-settle tail **260ms Curve B**, transposed from the
     counterfoil leaf's shipped motion
     (`FocusSequencerScreen.kt:448–455`);
  3. the body is **filed**: it lifts 12dp and fades over 180ms Curve A
     as the dismissal lands — upward, toward the Drawer it now lives
     in, never downward after the tab (two objects falling the same way
     reads as one object breaking, not as *stub kept / slip filed*).

#### 2.5.3 What the created edge looks like (if P-A was not bought)

If the ruling buys P-B alone, the body's bottom edge is the straight
perforation line *until* the tear; the separation frame swaps it for
the §2.2 teeth — generated from the same № seed, complementary to the
tab's top edge (the tab's top path is the body's bottom path negated;
they are one path used twice, so the two pieces visibly *fit*). The
filed slip, if the Slip Book (B2) is ever ruled in and shows it again,
keeps those teeth permanently: they are the shape of its closing act,
and the torn stamp (§2.9) is what rehydration checks to draw them.

#### 2.5.4 Exclusivity — what "only a tear closes" must decide

Emil's P2 says *only* a perforated tear closes the slip. Taken whole,
that collides with two standing laws, and the ruling — not the builder —
must resolve both:

1. **Navigation / PARK dismissal** (designer's §3.3 in this cell lists
   navigation and PARK among today's dismissal doors, and interim cure
   A9 persists them). **Recommendation:** under B1, leaving the room is
   not closing the paper — an untorn slip **re-stands** on return to
   the Deck (settled, no re-performed arrival, per item 18's restored
   law), and *only* a torn slip is gone for good. That is the tear's
   whole meaning; if navigation still files it, the tear is a second,
   slower dismissal button and will be routed around within a week.
2. **Interim A9 (dismissed-№ persistence)** ships regardless in batch
   10. If B1 later lands, the torn stamp **supersedes** the dismissed-№
   for torn slips (a torn slip is a dismissed slip with a reason), and
   A9's record remains the law for any dismissal door the ruling keeps.
   No migration conflict: both are one-fact stamps on the receipt's
   existing persistence home.

#### 2.5.5 Accessibility and reduced motion (floors, not options)

- **No-gesture path:** TalkBack / switch-access users get a single
  accessibility action on the slip, labelled **"Tear and file receipt"**,
  which performs the identical bookkeeping (torn stamp first) and the
  identical separation, initiated at the perforation's centre. This is
  not a button on the paper — nothing renders, the zero-CTA acquittal
  stands — it is the gesture's equivalent, as tap-parity already is for
  the counterfoil leaf's drag-less tear.
- **Reduced motion:** no fall, no rotation, no stick-slip stepping. The
  tab crossfades out over 150ms at the commitment point, the body fades
  with it, stamp and sound/haptic (§2.6) unchanged. The *fact* of
  tearing survives; the theatre yields — the same split Emil specced
  for his strike form.

### 2.6 Sound and haptics — reuse the shipped tear voice

The receipt tear must **not** invent a sound. The app already ships a
tear voice, specced as its own chord precisely so it is never mistaken
for a finish: `TactileSoundEngine.playTearPulse()`
(`service/TactileSoundEngine.kt:812–829`) — haptic waveform timings
`(0, 8, 14, 8)ms` / amplitudes `(0, 160, 0, 110)`, audio transient
**240Hz, 40ms, gain 0.68, chord ratio 1.21** against the completion
pulse's 1.33 (L831ff). The proposal spends it thus:

- **Per stick-slip cluster (9×):** a *tick only* — Emil's filed figure
  for this tear is **tick 5ms @ 0.45 per step** — realised as a short
  haptic tick with a matching faint audio tick if sound is on. Nine
  identical ticks would read as a machine counting; vary amplitude by
  cluster from the № seed within **0.35–0.55** — fibre clumps are not
  metronomes — but never pitch: one paper, one voice.
- **At separation (once):** the full `playTearPulse()`, unmodified, its
  waveform landing **within one frame** of the final cluster's visual
  break. Its two-beat shape (160 then the softer 110 tail) *is* the
  separation: the first beat is the last bridge parting, the tail is
  the tab leaving. Do not layer the completion pulse under or over it —
  the tear is a filing, not a finish (the engine's own comment,
  L812–816, rules exactly this), and the session's completion pulse
  already sounded at detention, minutes of attention ago.
- **Aborted tear (release before cluster 5):** silence. No reverse-rip,
  no apologetic tick. Paper flexed and released makes almost no sound;
  inventing one would teach him the gesture "almost counted".
- **Sound off / haptics off:** each channel carries the moment alone
  without compensation (no louder substitute in the surviving channel).
  Both off: the stick-slip stepping and the fall still read visually —
  the tear was specced visual-first for this reason.

*Engine price note:* per-cluster ticks are a small addition beside the
existing pulse (a `playTearTick(amplitude)` sibling, or a parameterised
pre-tear run-up inside a new `playReceiptTear()` that ends in
`playTearPulse()` verbatim). That is inside the audit's M for B1; a
new *sound design* would not be, and is not requested.

### 2.7 How P-B composes with the decided tier

- If B1 is ruled in, §1's shape work is **not wasted**: P-A's
  GenericShape is §1's 3dp shape with its bottom edge replaced (§2.4),
  and P-B's shadow/clip discipline is D-E4's, unchanged. The interim
  merge is the proposal's foundation, which is one reason the audit
  could price B1 at M rather than L.
- Arrival (A7, feed-not-fade) is untouched: the slip feeds in whole,
  perforation and tab included, and is still thereafter. The tear is
  available from the moment the A8 dwell (2s from
  `receiptPublishedMonoMs`) expires — tearing inside the dwell is
  impossible, so the in-flight tap that lands the slip can never
  accidentally start its closing.
- Restored slips (rehydrated, untorn): perforation present, tear
  available, no arrival re-performance. Restored **torn** slips do not
  exist as standing slips — torn is filed is gone (§2.5.4) — they exist
  only as whatever B2's Slip Book may one day show.

### 2.8 Acceptance senses for the ruling (what "good" feels like)

1. He can tell, eyes closed, the difference between the completion
   pulse (session end) and the tear (filing) — different chord,
   different haptic shape, never confused (§2.6).
2. The tear *resists, then gives*: the 0.55 follow ratio and the
   stick-slip steps are the whole feel. If a tester describes it as
   "swiping the card away", it has failed; the lawful description is
   "pulling a stub off".
3. Two slips side by side (screenshots of № 047 and № 048) have visibly
   different bottom teeth; two screenshots of № 048 taken a day apart
   have *pixel-identical* teeth (§2.1).
4. A tear abandoned at 40% leaves a slip indistinguishable from one
   never touched — no half-torn state persists, visually or in data.
5. The whole closing, commitment-to-gone, is under **400ms** and under
   **60dp** of travel. Past either number it becomes the toll Emil
   warned against, and the gesture will be resented by week two.

### 2.9 Cost and open questions for Ren's ruling

**Priced (as the audit books B1): M / M+ with the torn stamp.**
M = P-B alone (perforation + gesture + ticks + stamp + solid-overprint
faulted case). M+ adds P-A (seeded GenericShape issue edge + shadow/clip
unification + scroll-clip verification, §2.4).

- **Version caveat the builder must resolve before estimating the
  stamp:** the audit, written against its moment, says "DB v12 torn
  stamp", but this tree's database is **already version 12**
  (`data/MonolithDatabase.kt:25`). If v12 has shipped in batch 9, the
  torn stamp rides the **next** bump (v13 — the same bump the designer
  in this cell already flags for A9's dismissed-№, §6.3 of
  `work/designer.md`; the two should share one migration if both land).
  The *shape* of the stamp is small either way: one torn flag + torn-at
  wall time on the session row beside `receiptNumber` (RC-04's home),
  written by targeted single-purpose UPDATE in the DAO's §7.5
  discipline — never a whole-row REPLACE.
- **Q1 for the ruling:** P-A, P-B, or both? (Recommendation: P-B first;
  P-A is follow-on garnish that can land in a later batch without
  re-opening the gesture.)
- **Q2:** Does navigation still dismiss an untorn slip under B1?
  (Recommendation: no — §2.5.4. This is the ruling's real decision;
  the gesture is easy, the exclusivity is the law being bought.)
- **Q3:** Is the tear available on routine / SLEEP stand-down slips, or
  manual + FLOW only? (Interact with B6, stand-down slips drawer-only:
  if B6 lands, stand-down slips never stand, and the question answers
  itself.)
- **Q4:** Tab № microprint (§2.5.1) — keep or cut? It is the only new
  print proposed anywhere in this document; it earns its place as the
  filing label, but the slip's print discipline says every glyph is
  argued for, so it is the ruling's to strike.

---

## 3. What this seat deliberately did not touch

- Dismissal mechanics as decided (A8: DOWN gesture, 2s dwell, faulted
  explicit act) — §2.5 assumes A8's dwell and event definitions; if B1
  is ruled in, A8's *paper-local* door is the thing being replaced,
  and that replacement must be minuted in the ruling, not smuggled in
  by a builder holding this document.
- Arrival motion (A7), rhythm/section rules (A6), print content
  (designer's seat), the Slip Book surface (B2) — §2 references them
  only where the edge or the tear physically meets them.
- Any build, compile, or test. Paper only.

— Feel Specialist 2 (tear & edge), Zettel's cell (issue #10).
