# ISSUE #6 — FEEL-3 (MOTION) — THE DECK'S MOTION LANGUAGE
## Seat: FEEL-3 (MOTION), Rost's cell · Fri Oct 30, 2026
## Status: DECLARED, paper only. Nothing compiled. Nothing committed.
## Report to Rost, never to the user. This file re-opens no audit ruling.

Sources read whole: `AUDIT-deck.md` (§3, §6, §8, §9 F14/F15/F16/F18),
`ISSUE-6-designer.md` (D5, D8–D13, §2 state layouts, §5 standing
notes), `ISSUE-6-builder-map.md` (F14–F18 entries),
`TactileDeckScreen.kt` (working tree, `agent/issue-6` @ 2a1d192 —
the seal-shot driver, the hero card's vertigo modifier, the pinned
stack), `MonolithCurves.kt` (the whole registry),
`MonolithCustomComponents.kt` (strip, sentence slot, imprint),
`BlockitCoreViews.kt` (wheel + slide machinery, sampled at their
animators), `ParkDrawer.kt`, `ReceiptSheet.kt`, `MainActivity.kt`
(tab transition, host seam fade), `slice-e-parts/11-spule.md`
(the motion-law examination — its recomputations are adopted
where cited), `slice-e-parts/08-zug-ii.md` (shot construction),
`HEADER-BAND-AUDIT.md` (acquittal A2). Curve arithmetic in §2 was
recomputed this run (bezier inversion), not copied.

Territory: **only how the Deck moves** — the seal shot (F15), the
proof drawer (F16), the scroll fade (F14), the Fusion seal (F18),
and every state-change transition between the Deck's states.
Touch resistance, knock weights and detents are FEEL-1's file;
this file cites that machinery where it carries a transition and
changes none of it. Composition is the designer's; this file
changes no layout, only the motion each layout change rides.

---

## §0 · GROUND TRUTH — EVERY MOTION ON THE DECK TODAY

| Motion | Law in code today | Curve / duration | Verdict |
|---|---|---|---|
| Seal shot (vertigo) | Driver loop samples elapsed since t0 each frame; `vertigoT`/`blurT` written from two easings over 1050ms | Transform `CubicBezier(0.5,0,0.85,0.35)`; blur `(0.32,0,0.18,1)`; phases DEPARTURE <180 / RECESSION <780 / SETTLE <1050 | Curves + clock **sound**; target + amplitudes **wrong** (§2) |
| Vertigo target | Applied to the **hero card** — the instrument itself: `scale 1−0.06·vt`, `translationY −10dp·vt`, `blur 24·blurT(1−blurT)` (peak **6dp**) | — | **Inverted** vs F15 (§2) |
| Ground night step | `groundStepped` latch at t=400 flips scheme for wheel/slide/rail | One frame, no tween | **Keep** — a step by law (G3) |
| Accent exit phases | Host pushes `sealExitPhase` 0→3 at +50/+100/+150ms into the slide view | Discrete steps, held | **Keep** — steps, never smoothed (Spule) |
| Imprint Strike | At t0: scale `Animatable` 1.04→1.0, ink 0.82→0.88 concurrently | Scale `tween(110, CubicBezier(0.9,0,1,0.4))` **inline, unnamed**; ink `tween(400, EasingA)` | Sound; the scale easing is an **unnamed law** (§7, M-H1) |
| Imprint lip | Sealed: `snapTo(1)` in the seal frame. Session end: fade out | `tween(240)` **no easing named** (framework default) | Snap-in **keep**; exit easing unnamed (§7) |
| Sentence hand-back | On wire return: 500ms stability beat, tick, then fade | `animateFloatAsState(tween(90))`, easing = framework default | Beat + fade **keep**; name it (§7) |
| Proof strip densities | PASS/FAULT/UNKNOWN tones flip | **One frame — "no easing, no pulse, ever"** (file's own law) | **Keep** — print does not move (G3) |
| Wheel ↔ progress track | At settle the wheel `AndroidView` **uncomposes**; a 6dp track + % row compose in its place inside the card | Cut (unavoidable — different composables) | Dies with F18 (§5) |
| Hero glyph content | Span → MM:SS (same `Text`, new string) | Cut | **Keep the cut**, move its address (§5) |
| Utilities row | Composes at settle only (F-Z3: "by CUT 0", absent during the shot) | Cut | **Keep** — settle furniture cuts in |
| Grace/valve chips | Swap into the slide's 72dp zone when running settles | Cut | Swap dies with F6; the address law (Δy=0) is §5's |
| Slide machinery | Mesh 140ms analytic bezier `(0.30,0,0.20,1)` → seat spring ω0=20 ζ=0.40; refusal retreat 160ms CurveA → 300ms hold → return spring k=260 ζ=0.78; abort spring ω0=22 ζ=0.90; sub-catch spring-back 280ms `MechanicalOvershoot` | Physics constants live in the view (Spule: never Curves tokens) | **Record, fenced** — FEEL-1's file; named in §1's registry only |
| Sled return home | `ValueAnimator` 1→0, `ReturnHome (0.3,0,0.5,1)`, **650ms literal at the animator**, total silence; starts ≤1050ms after receipt publication | One scoped curve | **Keep verbatim** |
| Wheel physics | Coast/wells ζ=0.38, hub dip 45ms `HubPressDip`, hub return 160ms `HubSeatReturn`; tetrad closure ink 0.88→1.0 over 160ms with **no interpolator set** (framework accelerate-decelerate) | Scoped pair sound; closure is an **unnamed law shipping** (Spule L-4) | Closure flagged in §7 |
| PARK drawer (host chrome) | `Animatable` fraction: open 280 SEAT, close 160 THROW (`MonolithKin`) | Named, KIN-DRAWER | **Keep** — and §3 adopts its numbers for the proof drawer |
| Tab change (host) | Outgoing fade 120 linear; incoming fade+slide 240 SEAT, 14dp in stop order | KIN-TAB-IN | Host's; not Deck motion, inventoried so nobody "fixes" it here |
| Host seam fade | 24dp gradient to `scheme.background` above the rail, **always on, scroll-blind**, painted (not masked) | Static | Not F14 (§4) |
| Deck scroll fade | **Does not exist.** The Deck's scroll column clips content hard against the pinned stack | — | F14 unbuilt (§4) |
| Proof drawer | **Does not exist.** `ProofStripBand` is pinned permanently; it has no open/close motion of any kind | — | F16 unbuilt (§3) |
| Overlays over the Deck | Desk clock / airlock enter `specB` (420), digest / primer / disarm enter `specC` (280); **all exits `specA` (260)** | A/B/C family | Named exception, §6 G2 note |
| Receipt slip | `fadeIn(specC)` / `fadeOut(specA)`; dismissal = any scrim DOWN | — | F19 adds the shake (§6) |

The pattern in one line: **the Deck's machinery moves beautifully
and its furniture does not move at all** — every state change
above the slide is a cut, a swap, or an absence. Some of those
cuts are law (print, steps, the settle's CUT 0). Three are just
missing motion: the vertigo aims at the wrong object, the drawer
has no motion because it has no existence, and the scroll's edge
is a guillotine. This file fixes exactly those three, names the
grammar the cuts belong to, and fences the rest.

---

## §1 · THE ONE GRAMMAR — NAMED

One motion grammar for the whole Deck. A motion that cannot be
filed under a name below does not ship; a name spent on a second
meaning is a leak (Spule's law, adopted as grammar law).

### The families

| Family | Token(s) | Constants | Lawful consumers (meanings) |
|---|---|---|---|
| **SEAT** | `CurveA` / `EasingA` | `(0.2, 0, 0, 1)`; durations at call site | Landings: things arriving to stay — drawer opens, rail cap, tab-in, ink settles |
| **THROW** | `MonolithKin.EasingThrow` | `(0.5, 0, 1, 0.4)` | Dismissals: things leaving — drawer closes, lip exit (§7) |
| **DRAWER** | `MonolithKin.DRAWER_OPEN_MS/CLOSE_MS` + SEAT/THROW | **280 open / 160 close** | **Both** drawers — PARK (host) and the proof drawer (§3). One drawer speed app-wide; two would be a dialect |
| **VERTIGO** | `VertigoTransformEasing` | `(0.5, 0, 0.85, 0.35)`, 1050ms | The seal shot's ground recession — **only** (§2) |
| **FOCUS-PLANE** | `VertigoBlurEasing` | `(0.32, 0, 0.18, 1)`, 1050ms | Blur only, ever (Spule M9's constraint): may never drive position, scale or alpha |
| **PRESS** | `HubPressDip` / `HubSeatReturn` | 45ms `(0.20,0,0.30,1)` / 160ms `(0.22,0.90,0.28,1)` | The hub's dip and return — scoped to that one joint (F-S3); FEEL-1 already reuses the pair for the STOP chair's dip inside F6, which is the same *meaning* (a key seating), not a leak |
| **RETURN** | `ReturnHome` | `(0.3, 0, 0.5, 1)`, 650ms literal at the animator, no Compose twin | The sled's return home — only; total silence rides it |
| **STRIKE** | *(to tokenise, §7)* | `(0.9, 0, 1, 0.4)`, 110ms | The imprint Strike — only |
| **PHYSICS** | *(constants in the views, never tokens)* | Slide seat ω0=20 ζ=0.40 · refusal k=260 ζ=0.78 · abort ω0=22 ζ=0.90 · wheel wells ζ=0.38 · mesh bezier `(0.30,0,0.20,1)` 140ms analytic | Commitments and refusals — motions a hand caused. Furniture never borrows a spring |
| **PRINT** | *(no curve — the family is the absence)* | 0ms cuts and held steps | Facts changing state: strip densities, warrant words, glyph content, night steps, exit phases, rim-ink ticks (§5) |
| **OVERLAY** | `specB` enter / `specC` enter / `specA` exit | 420 / 280 / 260 | Host overlays + receipt slip. A standing exception to G2, named here so it is a decision, not drift (§8 Q4) |

Duration vocabulary, as actually spent on this screen:
**45 · 90 · 110 · 140 · 160 · 180 · 240 · 250 · 260 · 280 · 420 ·
520 · 650 · 1050.** (`MonolithCurves`' header still claims
durations are "strictly chosen from {180, 260, 280, 420, 520}" —
false since Slice D; flagged to the F21 comment-truth pass, §7.)

### The laws

- **G1 · One meaning, one curve.** A token's consumer is its
  meaning. Borrowing across meanings is a leak and fails review
  on that ground alone — the motion form of the audit's
  one-fact-one-rendering law.
- **G2 · Enter on SEAT, leave on THROW.** Arrivals land (fast,
  exact, zero overshoot); departures accelerate away and never
  land. Springs are not arrivals — they are PHYSICS, and only
  hand-caused commitments use them.
- **G3 · Print does not move.** A verified fact changing state is
  a cut or a held step, in one frame. Never ease a fact: no
  crossfade on a numeral, no tween on a density, no interpolation
  between tick seconds. If it prints a state, it steps.
- **G4 · No reverse Vertigo, no reverse seal.** The shot never
  samples backwards (Wren §4.4). Unsealing, finish and receipt
  travel on RETURN / CUT, never on the ceremony rewound — a
  rewind would claim a restoration; nothing was restored
  (BEYOND-deck's E4 law).
- **G5 · Derive, don't replay; witnessed motion only.** Every
  seal-timed motion samples elapsed time from its t0 — a shot
  first observed late starts late, in one frame. A session not
  witnessed sealing (routine-born, restored, pre-existing) gets
  **no motion at all**: born settled, strike pre-struck, sled
  seated by the factory fraction. A replayed seal is a lie about
  time.
- **G6 · Under the finger, 1:1, never eased.** Scroll position,
  wheel angle, slide travel and any scroll-linked value (the
  fade, §4) are pure position functions of the hand. Easing a
  value the hand owns is a position lie (FEEL-1's §0 law,
  generalised).
- **G7 · Reduced motion = steps survive, travel dies.** Where
  `ANIMATOR_DURATION_SCALE == 0`, every Deck tween snaps to its
  settled frame (the Strike already does this in code; §2–§3
  inherit it), but timed *signals* still step on schedule — the
  ground still steps at t=400, the beat still elapses — because
  those carry information, not decoration (Zug-II's clause).

---

## §2 · F15 — THE VERTIGO, VERIFIED NUMBER BY NUMBER

The audit's F15: *"the page recedes (0.98, blur peak 3dp, same
curves, same 1050ms); the instrument seals in focus."* Designer
D13 restates it: *"ghost title, utilities, and ground recede; the
wheel never blurs, never scales."* Builder-map: *"move scale/blur
to the page ground, peak blur 3dp, same easings and same 1050ms;
the instrument seals in focus, untransformed."*

### Verification against the code as it stands

| Term | Audit / designer | Code today | Verdict |
|---|---|---|---|
| Duration | 1050ms | Driver divides by literal `1050f`, lands at literal `1050L`; `DURATION_1050` token exists but the driver never reads it | **MATCH** (tokenise the literal, §7) |
| Transform curve | "same curves" | `VertigoTransformEasing (0.5, 0, 0.85, 0.35)` | **MATCH — keep verbatim** |
| Blur curve | "same curves" | `VertigoBlurEasing (0.32, 0, 0.18, 1)` | **MATCH — keep verbatim** |
| Target | The **ground**; instrument in focus | The **hero card** — the instrument itself (wheel, glyph, eyebrow all inside it) | **INVERTED — the charge stands** |
| Scale travel | 0.98 terminal (`1 − 0.02·vt`) | `1 − 0.06·vt` → **0.94** | **FAIL — 3× the ruled recession** |
| Blur peak | **3dp** | `6f × 4 × blurT(1−blurT)` → peak **6dp** | **FAIL — 2× the ruled peak** |
| Translation | None named for the ground | `−10dp·vt` rise **on the instrument** | **Dies with the retarget** (spec below) |
| Instrument | Scale 1, blur 0, Δ=0 throughout | Scales, rises and blurs for 1050ms | **FAIL — the thing being sealed is the thing defocused** (audit §6: "a gimmick aim") |

### The curves' true shapes (recomputed this run)

The two easings are **counter-phased**, and the retarget must not
"fix" that — it is the design:

- **Blur is front-loaded:** `blurT` = 0.146 @180ms · 0.639 @400ms ·
  0.803 @525ms · 0.960 @780ms. The blur parabola
  `peak × 4·blurT(1−blurT)` therefore peaks when `blurT = 0.5`,
  at **≈328ms**, and has already fallen to 0.92dp (of today's 6)
  by 780ms, reaching exactly 0 at 1050. The room blurs early,
  while the seal is still being proven, and is **sharp again
  before the settle** — focus returns before the clock starts.
- **Transform is back-loaded:** `vertigoT` = 0.015 @180ms ·
  0.080 @400ms · 0.147 @525ms · 0.389 @780ms · 1.0 @1050ms.
  Travel per phase: DEPARTURE 1.5%, RECESSION 37.4%, **SETTLE
  61.1%** (Spule's F-S2 recompute, confirmed). Wren's filed claim
  that "Settle decelerates" is **mathematically false** — the
  curve arrives at t=1050 at its *highest* velocity into the
  terminal frame. **Disposition (Spule's recommendation, adopted):
  keep the easing, strike the claim.** The Deck file's comments
  must stop calling this a settle-deceleration (F21 pass); the
  curve itself is device-gated, not paper-gated (§8 Q5). What
  the shape *means* once retargeted is in fact right for the
  ground: the room barely moves while the machine proves, then
  falls away late and fast as the seal lands — the recession is
  a consequence of the seal, not a countdown to it.

Sampled values under the F15 amplitudes (for the builder's test
table in §7): at 328ms → scale 0.9987, blur **3.00dp** (peak);
@400ms → 0.9984, 2.77dp; @780ms → 0.9922, 0.46dp; @1050ms →
**0.9800, 0.00dp**.

### The F15 spec (builder-ready)

1. **Layer split is the real work.** `Modifier.blur` and
   `graphicsLayer` act on a subtree; today the instrument *is*
   the subtree. F15 requires the Deck composed as two subtrees:
   **GROUND** (Deck background, scroll content minus the
   instrument, proof strip band, refusal sentence slot) carries
   the transform; **INSTRUMENT** (wheel card now / Fusion wheel
   after F18, the slide and its ghost, the imprint) carries
   **no `graphicsLayer`, no `blur`, in any frame**. Host chrome
   stays out entirely — header band and rail never transform
   (HEADER-BAND acquittal A2 was proved against the old geometry;
   keeping chrome out keeps it true by construction). PARK is
   out (it owns KIN-DRAWER).
2. **Amplitudes, as named constants** (§7): ground
   `scale = 1 − 0.02·vertigoT` about the ground subtree's centre;
   ground `blur = 12·blurT(1−blurT) dp` (= `3dp × 4·blurT(1−blurT)`,
   peak **3dp** @≈328ms, 0 at both ends). Same driver loop, same
   t0, same two easings, same phase boundaries (180/400/780),
   same input death 0→1050 exactly. Nothing else in the driver
   changes.
3. **No rise.** The `−10dp` translation term is **deleted, not
   moved**. A rising ground would move the pinned addresses the
   Δy=0 law protects and buy nothing the scale doesn't already
   say. (If Rost wants a rise, its ceiling is 3dp, ground-only —
   §8 Q1. Recommendation: 0.)
4. **The end is a cut, lawfully.** Blur self-resolves to 0 on
   the parabola. Scale does **not** ease back (G4): at the
   settle frame the ground's transform is removed in the same
   composition that seats the running furniture (STOP chair /
   utilities by CUT 0, F-Z3) — the 2% release rides the busiest
   frame of the transition, at one-third of today's 6% snap,
   which today rides the same frame unremarked. The running
   ground thereafter stands at scale 1, untransformed.
5. **Steps inside the shot stay steps:** ground night at t=400
   (one frame, G3), accent exit phases at +50/+100/+150 (held
   steps), the Strike at t0 (§5). The shot is two easings and
   three steps; a fourth animation inside it is a leak.
6. **Reduced motion (G7):** ground scale/blur read identity for
   the whole shot; the steps still step.

**Price: inside F15's M (audit price stands).** The hygiene
bundle in §7 rides it.

---

## §3 · F16 — THE PROOF DRAWER'S OPEN / CLOSE MOTION

Today there is nothing to verify: `ProofStripBand` is a pinned,
permanent, motionless band, and its cells flip tone in one frame.
The drawer (designer D9) is a new object: it lives **in the
scroll column at the ghost title's address**, exists only in
PROVING / JAMMED / idle-fault plus one settle beat, and reserves
**0dp while shut**. Its motion:

- **OPEN — 280ms, SEAT (`EasingA`), height 0 → natural height,
  unfolding downward from the instrument's foot.** The DRAWER
  family's numbers, shared with PARK (§1): a drawer is a drawer,
  and a second drawer speed would be a dialect the hand would
  have to learn twice. Content fades in on the same spec (one
  spec per direction — PARK's FIX key, which fades on an unnamed
  default easing beside its SEAT height, is the ragged precedent
  not to copy).
- **CLOSE — 160ms, THROW (`EasingThrow`), height → 0,** content
  fading on the same spec. Departures accelerate (G2); the proof
  leaving is not a landing.
- **Driver: a manual fraction, not `AnimatedVisibility`.** PARK's
  precedent (`Animatable` fraction over measured height) is the
  pattern: it guarantees what `AnimatedVisibility` cannot —
  mid-flight retargeting without a restart. A jam landing
  mid-open continues the same travel to the same height with new
  content; a re-proving during close expands again **from the
  current fraction**. Height at every frame = fraction × the
  content's measured natural height; the strip cells inside
  never scale, squash or slide — only the frame's height and
  alpha move (the cells are print, G3, and print does not move).
- **The settle beat — 520ms, held.** On proving success the
  drawer stays open, content frozen at the success frame, for
  one beat of **520ms** (`DURATION_520`, the vocabulary's longest
  beat — one full read of three cells at 1 AM, FEEL-5's counting,
  and not a frame longer), then CLOSE runs. A cell flipping
  during the beat restarts nothing; the beat belongs to the
  success, not to the wire's subsequent weather. (§8 Q2 prices
  the alternative, 420.)
- **JAMMED / FAULT: no auto-close, ever.** The drawer holds open
  while proof is live business. If the jam sentence grows the
  drawer's content (D9: the sentence prints *inside* the drawer
  in a live jam), the height retargets on the OPEN spec (280
  SEAT) — fault gets more room, and the room arrives as a
  landing, not a snap and not a spring. On fault clearing, the
  settle beat runs, then CLOSE.
- **What the drawer never does:** no overshoot, no spring
  (PHYSICS is for commitments, §1), no pinned tenancy, no
  reserved height, no motion in its cells' tones (one-frame
  flips, as today).
- **Pinned stack consequence:** the drawer's travel happens
  entirely inside the scroll column; the pinned slide/imprint
  addresses do not move by a pixel while it opens (Δy=0 applies
  to the pinned zone, and the scroll column absorbing growth is
  what a scroll column is for).
- **Reduced motion:** snap open/shut; the 520ms beat still
  elapses (it is reading time, not decoration).

**Price: inside F16's L.** If the drawer's motion is ever landed
separately from the restructure, it stands alone at **M** (the
fraction driver + beat clock); the numbers cost nothing — they
already exist in `MonolithKin`.

---

## §4 · F14 — THE SCROLL'S BOTTOM-EDGE FADE

**Verification:** F14's fade is **unbuilt on the Deck**. The
scroll column clips content hard at the pinned stack. The only
fade near the Deck is the host's seam fade (`MainActivity`:
24dp gradient to `scheme.background` above the rail) — chrome
C9's, static, scroll-blind, and *painted over* content in the
background's colour. It is not this item, and this item must not
become its twin.

**Spec:**

1. **Owner and address:** the Deck scroll column's **bottom
   edge only** — the edge that meets the pinned stack, where
   content is currently guillotined. The top edge never fades:
   content does not enter from anywhere at the top.
2. **Height 24dp** — the host seam fade's height, one fade
   height app-wide (no new number).
3. **A mask, not a paint-over.** The fade is an alpha mask over
   the scroll content (destination-out gradient in the
   column's draw pass), so the *ground shows through* whatever
   the scheme is. A background-coloured overlay (the host
   pattern) would need a token per scheme, would lie over the
   proof drawer when it opens near the edge, and would break
   under every look (F17). Mask or nothing.
4. **Scroll-aware strength (G6):** fade strength =
   `min(1, remainingScrollDp / 24)` — full while a screenful
   remains, weakening through the last 24dp of travel, **zero
   at the true end of the content**. A permanent fade over the
   last line would print "more below" where there is none — a
   lie in the fade's own grammar. The strength is a pure
   position function of scroll offset: 1:1 with the hand, never
   eased, never animated on state changes.
5. **The no-scroll state is the off state.** Post-eviction the
   idle Deck fits whole (§2 of the designer disposition is an
   acceptance test): `canScrollForward` is false, the fade is
   absent, and the acceptance test and the fade's retirement
   are the same fact. The fade exists for the states that can
   still scroll (drawer open, expanded content, small phones).
6. **Collision with the host seam fade — flagged, with a
   recommendation.** The host's 24dp painted fade sits *inside
   the viewport*, over the same bottom edge of the Deck. Two
   fades over one edge would double-darken it. Recommendation:
   when F14 lands, the host seam fade is destination-gated
   **off for DECK** and the Deck's own mask governs its edge;
   the host fade keeps the other three destinations. The law
   moves to its owner; it is not duplicated. (§8 Q3 — this
   touches `MainActivity`, outside the Deck file; Rost routes.)

**Price: inside F14's S.** The host-gating line is one condition
in `MainActivity`.

---

## §5 · F18 — THE FUSION SEAL: WHAT MOVES WHEN THE DIAL BECOMES THE CLOCK

**Today, the honest answer is: almost everything.** At settle
the wheel uncomposes and a progress track + % row compose in
its place; the hero glyph's string swaps; the card that carried
both was itself scaling and blurring. The slide, the strip and
the imprint keep their addresses (Δy=0 across the seal is
acquitted in code, audit §5) — and that is all that holds still.

**Under the Fusion, the answer must be: almost nothing.** The
spec, element by element, across the whole seal (t0−1 frame →
settle):

| Element | Motion at the seal | Law |
|---|---|---|
| Wheel view identity | **None.** Same `AndroidView` instance, same 288dp box, same centre — never uncomposes, never recomposes. The shot's existing view hoist becomes permanent | Δy=0, Δx=0, Δh=0 |
| Wheel angle | **None.** Physics halts at t0 (built: `isLockedSession` stops coast, kills a half-finished hub press); the wheel locks *at the dialled angle*. No rotation into a "running position", no sweep to zero | Δangle=0 |
| Hub glyph (span → MM:SS; FLOW → count-up) | **CUT at t0** — same glyph, same size class, same centre; the string changes in the seal frame. No crossfade (a crossfade double-prints a numeral — the ghost law), no size tween. Before t0 (proving) the hub holds the dialled span frozen: a readout never starts on an unproven seal | G3 |
| Hub sub-line (recipe → running context) | **CUT at t0**, same frame as the glyph | G3 |
| Rim ink (progress) | **Appears from t0, then steps.** At t0 elapsed ≈ 0, ink ≈ empty. Thereafter it advances **once per tick second, in one frame** — a stepped function of elapsed/target, never interpolated between seconds: a smoothly sweeping rim would print fractional seconds the readout itself denies. FLOW: the ring never appears and never completes — no denominator, no motion toward one | G3 |
| Night ink | Hub/instrument ink steps at **t0**; ground scheme steps at **t+400** (inside the shot) | Steps, G3 |
| Slide | Becomes the seated ghost at t0 (`sealGhost()`: mode SEATED, displayed exactly 1 — a **state set, not an animation**, built today); address unchanged | Δy=0 |
| Imprint | The **Strike** at t0: scale 1.04→1.0 in 110ms STRIKE + ink settling 400ms SEAT — the one small fast motion inside the instrument's stillness, lawful because it is the *record* being struck, not the instrument moving | §1 STRIKE |
| Ground | The F15 recession (§2) — the **only** travelling mass in the whole seal | §2 |
| Hero panel / track / % | **Die.** No successor, no exit animation. A dying element does not get a farewell tween; it is absent from the Fusion's first frame | — |
| Utilities / STOP chair | Compose at settle **by CUT 0** (F-Z3's law, kept) at the slide's address | Δy=0 |

**The sentence for the builder:** across t0, within the
instrument, **nothing translates, rotates, scales, or fades.**
Δy=0 is necessary but not sufficient — the Fusion's law is
Δposition=0, Δangle=0, Δscale=1, Δalpha=1 for every instrument
pixel, with exactly three cuts (hub glyph, hub sub-line, ink at
t0), two steps (ground night at +400; rim ink per second
thereafter), one strike (imprint), and the ground receding
around all of it. The dial does not hand over to the clock.
The dial *is* the clock; only its print changes tense.

**One rider, fenced off:** the ticking hub inherits the AOD
clock's burn-in shift discipline (designer D5). That shift is a
slow positional duty cycle on minute boundaries — **steps,
never eased**, and no part of the seal's motion. If the builder
cannot point at the mechanism in the view, the requirement
stands recorded here and the seal spec is unaffected.

**Tests (inside F18's price):** layout-identity across idle /
proving / sealing / running renders (wheel bounds byte-identical);
the hub-content cut lands in the t0 frame, not the settle frame;
rim fraction as a pure stepped function of (elapsed, target)
with FLOW returning "no rim" at every input.

**Price: inside F18's M (audit price stands).** The seal-motion
increment over the composition work is the rim's step function
and the cut discipline — no new animator is built.

---

## §6 · STATE-CHANGE TRANSITIONS — THE FULL TABLE

Target state of the Deck after F14–F18 land. "Cut" and "step"
are PRINT-family (G3); every eased motion names its family.
Transitions not listed do not exist (a session never passes
from RUNNING directly to IDLE — it goes through FINISH/RECEIPT;
the utilities never need an exit animation because they
uncompose inside that passage).

| Transition | What moves, and how |
|---|---|
| **IDLE → PROVING** (slide commits) | Slide seat PHYSICS (built) to the latch. Wheel + hub freeze — input cut, no visual motion. Proof drawer OPEN (§3, 280 SEAT) at the ghost title's address; ghost title displaced by the drawer's growth, not faded. Sentence slot: proving is silent (cut). Imprint tenant latched (cut). **No vertigo** — the shot starts at t0, and proving is before t0 |
| **PROVING → JAMMED** | Drawer content **cuts** to the jam form (sentence inside the drawer); if the sentence grows the drawer, height retargets on 280 SEAT — fault's extra room arrives as a landing. Slide refusal family runs in-view (built PHYSICS: retreat/hold/return). No shot, no vertigo — a recession over a failed seal would celebrate a failure |
| **JAMMED → IDLE** (annulment) | **ANNUL-CUT, everywhere:** drawer CLOSE is the one eased exception (160 THROW — furniture leaving); slide snaps home via `resetToRest` (built — the attempt un-existed; animating its departure would give it a funeral it didn't earn); the annulment sentence stands past-tense until the controller clears it, then leaves by HANDBACK (§7). Print freezes un-freeze in one frame |
| **PROVING → SEALED** (t0) | The shot (§2: ground VERTIGO + FOCUS-PLANE, steps at 400/+50s, Strike at t0) + the Fusion cuts (§5). Input dead 0→1050 exactly — scroll disabled, task handlers removed, wheel locked, slide ghosted (all built) |
| **RUNNING (internal ticks)** | Readout steps 1/sec; rim steps (§5); nothing eases, ever. A tick that tweened would print time the clock doesn't hold |
| **RUNNING → BREAK** | Hub content **cut**: readout → break countdown (the hub IS the break, designer §2); foot line cut to `BACK AT HH:MM`; END BREAK chip composes by cut in the utilities' room |
| **BREAK → RUNNING** | Reverse content cuts, same frame discipline. No travel in either direction — a break is a tense change, not a place |
| **Grace era crossing** (10s, no touch) | Chair words **cut** to the priced form; grace countdown leaves the hub (cut). **No motion and no haptic** (FEEL-1 §6 concurs) — a buzz at 10s with no finger down reads as an alarm from nowhere |
| **RUNNING → FINISH-DETAINED** | **Held-frame stillness.** Ticking cuts off mid-second and the frame freezes; no reverse vertigo (G4), no fade, no settle-out. Stillness is the transition |
| **FINISH → RECEIPT** | Receipt publishes; after the 1050ms-from-publication beat (built scheduler) the sled RETURN runs: 650ms `ReturnHome`, **total silence** (no feel exists on its path, built). The slip itself fades in on OVERLAY (`specC` 280) over a Deck in which nothing else moves but the returning cap |
| **RECEIPT → IDLE** | Slip dismissal: fade out `specA` 260 (OVERLAY family, kept). Under **F19**, a stray scrim tap no longer dismisses — it **shakes** the slip: offset keyframes −4dp → +3dp → −2dp → 0 over **240ms on `EasingC`**, slip otherwise unmoved, dismissal state untouched (proposed token SLIP-SHAKE, scoped to `ReceiptSheet`; inside F19's S, §8 Q-note). Imprint lip fades out 240 THROW (§7). Slide already home; imprint tenant un-strikes by cut |
| **ROUTINE-BORN / RESTORED → RUNNING** | **No motion at all** (G5). Born settled: no shot, no strike replay, no sled travel, exit phases pre-settled. The strongest transition law on this screen — a restored session that *performed* its seal would be lying about when it happened |
| **IDLE (internal)** | Dial drags: wheel PHYSICS (FEEL-1's file). Hub press: PRESS pair (built). Hub glyph / recipe / foot-line content: cuts per value (print). Ghost title change: cut. Nothing at idle fades, because nothing at idle is arriving from anywhere |
| **Any → PARK open** (host) | KIN-DRAWER (280 SEAT / 160 THROW) over a Deck that does not move for it. Chrome geometry never moves; the Deck is the room PARK visits, not a participant |

---

## §7 · REGISTRY AMENDMENTS — FOR THE BUILDER (one hygiene bundle, S)

Names before numbers: the following already-spent motions get
their tokens, so the grammar in §1 is true in code, not just in
this file. All are renames/re-pointings — **no motion changes
its constants** except where a line says so.

| # | Amendment | Detail |
|---|---|---|
| M-H1a | Tokenise **STRIKE** | `ImprintSlot`'s inline `CubicBezierEasing(0.9f, 0f, 1f, 0.4f)` becomes `MonolithCurves.StrikeEasing` (Spule M10 filed this name; the code never adopted it). Scoped: ImprintSlot only |
| M-H1b | Name **HANDBACK** | The sentence fade: 500ms beat (host logic, unchanged) + `tween(90)` — today on the framework default easing. Tokenise as `HANDBACK_MS = 90` with **Linear** easing: print leaving should not accelerate; at 90ms the delta from default is a ruling about honesty, not visibility — Rost may waive the easing change, the token may not be waived |
| M-H1c | Lip exit gets **THROW** | `ImprintSlot`'s session-end `tween(240)` (no easing today) → `tween(240, easing = MonolithKin.EasingThrow)`. A departure joins the departure family; duration unchanged |
| M-H1d | Driver reads **DURATION_1050** | The shot driver's literals `1050f`/`1050L` → `MonolithCurves.DURATION_1050`. One clock, one name (the sled scheduler's +1050 in the same file follows) |
| M-H1e | Vertigo amplitudes named | Literals `0.06f` / `6f` at the hero modifier die with F15; the ground modifier reads `VERTIGO_GROUND_SCALE_TRAVEL = 0.02f` and `VERTIGO_BLUR_PEAK_DP = 3f` from `MonolithCurves` |
| M-H1f | Closure ink gets a curve | `RotaryWheelView`'s tetrad closure animator (160ms, **no interpolator** — framework accelerate-decelerate by accident) gets `CurveA` explicitly. Same duration; the law becomes chosen, not defaulted (Spule L-4) |
| M-H1g | DRAWER family declared shared | `MonolithKin`'s KDoc amended: KIN-DRAWER's numbers govern PARK **and** the proof drawer (§3). No new constants |
| Flag | Curves header claim | `MonolithCurves`' KDoc ("durations strictly from {180…520}") is false; correct it in the F21 comment-truth pass — flagged here, not edited (not my lane) |

**Price: M-H1 bundle = S**, rideable inside F15's lane (it
touches the same driver) except M-H1f, which is one line in the
wheel whenever that file is next open.

**Acceptance for the whole file (paper-checkable):** the
Lamp Test's sealed frame (designer D13) is also a motion test —
mid-shot at ≈328ms the frame must read: instrument pixel-sharp
at scale 1, ground at 3dp blur and 0.9987 scale, band and rail
untouched. Any frame in which the wheel is the blurred object
fails F15 regardless of what the numbers say.

---

## §8 · OPEN QUESTIONS FOR ROST (decisions, not blockers)

1. **Ground rise: 0 (recommended) vs ≤3dp.** §2 deletes the
   −10dp term outright. A small ground-only rise is the only
   amendment that could be argued; it buys a "lift-away" read
   the scale already gives.
2. **Settle beat: 520 (recommended) vs 420.** §3 prices reading
   time at 1 AM over snappiness. Both are in the vocabulary;
   the choice is a tempo ruling.
3. **Host seam fade gating** (§4.6): Deck-owned mask replaces
   the host fade on DECK only — touches `MainActivity`, needs
   Rost's routing to that file's owner.
4. **OVERLAY exits** (`specA` for every exit, against G2's
   THROW): recommended — **name it as the standing exception**
   (§1) and migrate never, unless a look demands it. Changing
   every overlay exit in the app to appease a Deck grammar
   would be the tail wagging the house.
5. **The transform curve's character** (F-S2): keep
   `(0.5, 0, 0.85, 0.35)` + strike the "settle decelerates"
   claim (recommended, Spule's own), device-gate the result —
   or send the easing back for a re-file that truly decelerates
   (undesigned, unpriced, and it would break the "same curves"
   term of F15 itself).

## PRICE LEDGER

| Item | What | Price |
|---|---|---|
| §2 | F15 vertigo-to-ground: retarget + amplitudes, curves/clock verified unchanged | inside **M** (F15) |
| §3 | Proof-drawer open/close/beat motion | inside **L** (F16); standalone **M** |
| §4 | Scroll bottom-edge fade (mask, scroll-aware) | inside **S** (F14) |
| §5 | Fusion seal motion: cuts, steps, strike, rim step-function | inside **M** (F18) |
| §6 | State-transition table; SLIP-SHAKE proposal | table: record · shake inside **S** (F19) |
| §7 | M-H1 naming bundle (7 amendments, no constant changes except M-H1b's flagged easing) | **S** |
| Record | Sled RETURN, PRESS pair, slide/wheel PHYSICS, PRINT steps — kept, named, fenced | — (already built) |

— FEEL-3 (MOTION), Rost's cell, issue #6. DECLARED, uncompiled,
uncommitted. File: `studio/plan/ISSUE-6-feel-3.md`.
