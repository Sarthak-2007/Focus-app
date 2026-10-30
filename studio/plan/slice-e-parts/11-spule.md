# 11 · SPULE — Improvers, the coil (springs) · SLICE E MOTION-LAW EXAMINATION

*Seated by Ilya (Improvers coordinator) for the SLICE E BUILD-READY wave.
Joint: every motion law Slice E needs — the three returns of
SLICE-D-E-BUILD-READY §3-R8, E5a's PRESS/SEAT pair, E3's phases, and
every other curve E3–E6 spends — inventoried against TODAY's trunk,
one lawful consumer each, leaks hunted in code and in spec prose.
All arithmetic below was recomputed this run (bezier inversion +
semi-implicit spring simulation, 0.5ms steps); where a filed number
failed its own math, the recomputed number is printed and the filed
one is named as filed. No code edited; no gradle run.*

*Trunk paths are `Focus-app/app/src/main/java/com/example/…`.
Curves file = `ui/theme/MonolithCurves.kt` (47 lines, read whole).*

---

## 0. VERDICT IN ONE BREATH

Slice E needs **11 motion laws** (plus 4 laws of *no* motion). Of the
11, **zero exist in `MonolithCurves.kt` today in usable form** — the
file holds 3 beziers, 2 overshoot interpolators and 5 durations, and
Slice E spends none of them except Curve A once (D3's retreat, an
E-adjacent D joint). E adds **6 tokens + 7 duration constants
(+≈21 net lines in the curves file, §7)**; two laws (Cato's seat spring,
Fen's refusal spring) are physics constants that live in the slide
view, never in the curves file. **Four leaks found**: one in shipped
code (L-1, live today), two in spec prose (L-2, L-3), one of token
discipline (L-4). **Three forks** (§7), of which one is load-bearing:
Fen's filed E6 resume formula fails the no-velocity-jump requirement
by up to **105ms and 1.95 frac/s**; the time-offset technique in §6
replaces it. And one filed claim is mathematically false: **Wren's
Settle phase does not decelerate** — the Vertigo transform easing
puts 61% of its travel in Settle at the shot's *highest* velocity
(§3, F-S2).

---

## 1. WHAT EXISTS TODAY — `MonolithCurves.kt`, line by line

| Line | Token | Constants | Consumers in trunk today |
|---|---|---|---|
| :18–22 | `DURATION_180/260/280/420/520` | {180, 260, 280, 420, 520} | the whole app; header :14 claims durations are "strictly chosen from" this set — **already false for E** (45/110/140/160/240/250/650/1050/1200 all outside it); header must be amended in E's first curves commit (Cato flagged the same) |
| :25 | `CurveA` (view) | `PathInterpolator(0.2, 0, 0, 1)` | nav pill `BlockitCoreViews.kt:1289` (260ms), suitcase throw `BlockitSuitcaseAndSwitchesViews.kt:66` (260ms), rocker :341 (260ms) — generic house travel |
| :26 | `CurveB` (view) | `PathInterpolator(0.05, 0.7, 0.1, 1)` | suitcase cap :195 (280ms) only |
| :27 | `CurveC` (view) | `PathInterpolator(0.3, 0, 0.8, 0.15)` | no view consumer found; Compose twin used by overlays (below) |
| :28 | `MechanicalOvershoot` | `OvershootInterpolator(1.25)` | **the slide return, `BlockitCoreViews.kt:1218` — see LEAK L-1** |
| :29–31 | `MechanicalAnticipateOvershoot` | `AnticipateOvershootInterpolator(1.1)` | **none — grep over `src/main/java` finds the definition only. A dead token; E must not adopt it "because it exists."** |
| :34–36 | `EasingA/B/C` (Compose twins of :25–27) | same control points | `specA/B/C` (:38–45) drive: nav crossfade `MainActivity.kt:287`, every overlay enter/exit `AirlockAndDisarmOverlays.kt:78–599`, expand/shrink `MonolithCustomComponents.kt:339–340`, sequencer `FocusSequencerScreen.kt:358–363` |

**Not in the curves file at all — the B2 seat family (the brief's
"seat springs ζ=0.38 family from B2, capture constants"):** the wheel's
well spring is *inline* in `RotaryWheelView.stepPhysics`,
`BlockitCoreViews.kt:964–966`:

```
omega0 = if (springTarget == 108f) 23.5f else 21f        // FLOW well / numeric wells
accel  = omega0²·(target − θ) − 2·0.38·omega0·physOmega  // ζ = 0.38
```

Capture law around it: capture radius 6° (FLOW) / 4° (numeric),
+2° hysteresis at release (`:938–943`), capture velocity
`540/√loadInertia °/s`, arrival predicate |Δθ| < 0.05° ∧ |ω| < 8°/s
(`:970`). Verified by simulation: 2%-envelope settle **501ms**
(ω0=21) / **448ms** (ω0=23.5) — the filed "seat ≤520ms" holds;
overshoot 27.5% of throw (the two-overshoot character).
**ONE lawful consumer: `RotaryWheelView` well capture. Slice E has
no claim on it** — it is inventoried here as a leak-guard: ζ=0.38
must never be borrowed for a slide return; the wheel's seat (a
detent arriving) and the sled's returns (commitments being unmade
or tidied) are different meanings that happen to share the word
"seat." Also inside the same view, a *third* motion the builder
will trip over: the tetrad closure ink-settle,
`BlockitCoreViews.kt:371–379` — `ValueAnimator.ofFloat(0.88, 1)`,
**literal duration 160, no interpolator set (framework default
accelerate-decelerate)**. Same file, same 160 as Mara's SEAT —
different curve, different meaning (ink density, not press travel).
See L-4.

---

## 2. THE MOTION INVENTORY — every law Slice E needs

Insertion points are against today's file: view interpolators after
:31, Compose easings after :36, durations after :22. "Consumer"
means *meaning*, not call site — two call sites with one meaning are
one consumer; one token with two meanings is a leak (§4).

| # | Motion (joint) | Law + exact constants | In Curves today? | ONE lawful consumer |
|---|---|---|---|---|
| M1 | **E6 · Sled Goes Home** (Fen) | Powered bezier `PathInterpolator(0.3, 0, 0.5, 1)`, **650ms at the animator, never tokenised**; sled x = 1 − E(elapsed/650); E(0.5)=0.620, end slopes 0/0 | **NO** → add `ReturnHome` after :31 (+1 line). **No Compose twin** (see §5) | The post-receipt return animator in `SlideToStartView` (`BlockitCoreViews.kt`, replacing `animateSpringBack` :1214–1226 on this path) |
| M2 | **Refusal return** (D3 tail, R8) | Fen's universal spring: **stiffness 260, ζ=0.78** (ω0=16.125 rad/s, damping coeff 25.15), semi-implicit Euler ≤5ms substeps, target 0, **floor-clamp at 0** (§3.2) | **NO** — and never a Curves entry: physics constants `REFUSAL_K`/`REFUSAL_ZETA` live in `SlideToStartView` beside Cato's pair | The refusal sequencer's SPRING phase (after the 300ms hold), *and* the ordinary sub-catch drag release — **one meaning, "not committed," two entries; lawful** (Fen: the signature "means *not committed* everywhere in this app"). The tap abort is **not** this consumer — see L-2 |
| M3 | **Tap abort return** (D5, R8) | Cato's integrator RETURN mode: `x(e) = xAbort·(1 − BezY(e/250ms))`, BezY = mesh bezier y (M4); **250ms fixed**; position-continuous, velocity arrested (§3.3) | **NO** token (analytic in the view, like M4) | Mesh abort only (`seatMode == MESH` + second touch). Never the refusal, never an ordinary release |
| M4 | **Tap mesh** (D4) | Cubic bezier **(0.30, 0, 0.20, 1.00)**, **140ms**, 0 → 0.92, evaluated analytically (Newton, 3 iters) inside Cato's frame loop; exit velocity exactly 0 (P2 y = end y) | **NO** — Rhea's priced `PathInterpolator` constant for it (rhea.md §4) is **superseded**: an interpolator cannot be substepped, seeded or aborted; Cato/merge governs | The integrator's MESH mode only |
| M5 | **Powered seat** (D4; E3 presumes it) | Spring `accel = 400·(1−x) − 16·v` (**ω0=20, ζ=0.40**), ≤5ms substeps, hard-stop clamp at 1.00 = onLatch; knock predicate at 0.97. Verified: from (0.92, 0) knock at **72ms**, first crossing **108ms** (filed 110 ✓); from (0.84, 0) knock 88.5ms, cross 108ms; from (0.84, +3/s) cross 57ms; (+6/s) cross 30.5ms (Cato filed ~36 — substep quantisation, immaterial) | **NO** (constants live in the view; idiom copied from wheel :952–985, constants not shared with it) | The integrator's SEAT mode — tap entry and drag entry are one meaning (*powered arrival at the latch*) |
| M6 | **E5a · hub PRESS dip** (Mara/Kin M-04) | Bezier **(0.20, 0.00, 0.30, 1.00)**, **45ms**, press depth 0→1 (from current depth on re-dip); slope 0 at start, E(0.5)=0.770, slope 0 at bottom | **NO** → add view interpolator after :31, name **`HubPressDip`** (+1 line) — scoped name, see F-S3 | The `RotaryWheelView` hub press-depth animator (DOWN branch :767) only. **Never travel** (Kin's own clause) |
| M7 | **E5a · hub SEAT return** (Mara) | Bezier **(0.22, 0.90, 0.28, 1.00)**, **160ms**, depth 1→0 starting on the commit frame; front-loaded: E(0.25)=0.757, E(0.5)=0.952 — arrive-and-be-done; core offset = depth × 2dp toward 135° rides it for free (offset is a function of depth, not a second animation) | **NO** → add view interpolator after :31, name **`HubSeatReturn`** (+1 line) | The hub press-depth return (UP/CANCEL branch :815) only — the specular core's ride home. Not the closure ink-settle (L-4), not Kin's drawer/overlay SEAT uses (outside E; F-S3) |
| M8 | **E3 · Vertigo transforms** (Wren) | **One** `Animatable` (`vertigoT` 0→1) over **1050ms**, `CubicBezierEasing(0.5, 0, 0.85, 0.35)`, sampled from monotonic elapsed (derive-don't-replay). Phases are *windows of this one sampling*, never three animations (§3.1) | **NO** → add Compose `VertigoEasing` after :36 (+1 line) | `SealShotState`'s Animatable in `TactileDeckScreen` only |
| M9 | **Focus-plane (blur) law** (Wren) | `CubicBezierEasing(0.32, 0, 0.18, 1)`: (a) Vertigo blur sampled over the same 1050ms elapsed (0→6dp wheel), (b) finish **RACK 280ms** (9→0dp, staggers R0+50/+100 are delays on this same sampling), (c) nav rack (shared machinery, Wren's price) | **NO** → add Compose `RackEasing` after :36 (+1 line) | **Focus-plane changes only** — one meaning (the room's focus) in three rooms. Constraint written into its KDoc: may never drive position, scale or alpha travel; the day it does, it becomes a second VertigoEasing and leaks |
| M10 | **E3/D9 · the Strike** (Emil, fires at t=0 inside the shot) | Scale `Animatable(1.04) → 1.0`, **tween(110, CubicBezierEasing(0.9, 0, 1, 0.4))**; character: dwell-then-slam — progress 0.060 at 55ms, 0.311 at 95ms, ~69% of travel in the final 15ms (x2=1 ⇒ end slope →∞; the strike lands inside one frame, which is what a strike *is*) | **NO** → add Compose `StrikeEasing` after :36 (+1 line) | The Strike scale Animatable in `ImprintSlot` only |
| M11 | **Refusal retreat** (D3 A3, feeds E's refusal sequence) | **Curve A** `PathInterpolator(0.2, 0, 0, 1)` over **160ms**, displayed → 0.65, no overshoot; knock at arrival | **YES** — `CurveA` :25 (duration 160 at call site) | Within the slide: the refusal sequencer's RETREAT phase only. (Curve A's legacy house uses — pill, suitcase, rocker — are pre-existing generic travel outside this slice; E neither extends nor blesses them) |
| — | **Pawl lift** (E4/Opal, rack-gated) | 90ms, **easing unspecified in every part** | NO | **GAP G-1**: no law to inventory. If the §F7 rack gate fails, the pawl lift does not exist in E and the gap is moot; if it passes, Opal must name the easing before build — a builder defaulting to framework accelerate-decelerate is an unnamed law shipping by accident |

### Laws of NO motion (a curve here is a leak by construction)

| Joint | Law |
|---|---|
| E5b · orange exit | **Discrete token steps** at t=0/50/100/150 (×1.00/×0.75/×0.50/terminal), held without interpolation, zero orange from t=200 (Mara). No easing exists; do not "smooth" it |
| E4 · HELD FRAME | Stillness E0–E1200 — no Animatable, no sampling; the pulse at E700 lands on a pixel-identical picture |
| E4 · the CUT | 0ms at E1200. A duration here reopens the reverse-Vertigo lie |
| E3 · ground step | One frame at t=400 (scheme held pre-seal through the exit, F23). A tween here is a second, lying step |

---

## 3. THREE RETURNS, THREE LAWS — the R8 examination

### 3.1 First, a correction to how E3's phases get built

Wren's Departure/Recession/Settle (0–180 / 180–780 / 780–1050) are
**sampling windows over M8+M9, not animations**. A builder who
builds three tweens breaks derive-don't-replay, desyncs blur from
transforms, and triples the price. Ruling R-SPULE-1: elements sample
the two easings directly from the one elapsed value; Wren's
per-phase intermediate checkpoints (wheel scale 0.985 at 180ms, etc.)
are **demoted to sketches** — they do not match direct sampling
(transform sampling gives 1.5% of travel by 180ms, the table implies
7.5%) — and the 1050ms end values govern.

### 3.2 The refusal return (M2) — exact behaviour

Spring 260/0.78 from 0.65, v₀=0, after the 300ms hold:

- 2%-envelope settle **318ms** (the filed "~320ms" ✓ — it is an
  envelope figure, not a wall duration).
- **Unclamped, it undershoots −0.0130 fraction (−3.1dp at 248dp
  travel) past home** and creeps until ~470ms (simulation, strict
  predicate). Negative `dragFraction` draws the cap left of its
  inset — a geometry bug wearing a spring's clothes.
- **Therefore: floor-clamp law** (the home twin of Cato's hard-stop):
  first substep with x ≤ 0 ⇒ x := 0, v := 0, loop stops. Visible
  travel ends at the **first zero crossing, 245ms**, arrival
  velocity 0.48 frac/s (119dp/s — a soft dead-stop, same class as
  the seat's 0.68 frac/s arrival). Refusal timeline, complete:
  knock at F0 → hold 300ms → spring, rest at **F0 + 545ms**.

### 3.3 The abort return (M3) — what its curve honestly does

The reversal `1 − BezY` starts and ends at slope 0 (BezY is
3t²−2t³ in y). Position is continuous at the abort frame by
construction (starts at sampled `xAbort`). **Velocity is not:**
the mesh's velocity at the abort instant steps to 0 in one frame.
Measured junction steps (mesh velocity, recomputed — **Cato's
filed peak of 14.6 frac/s is wrong; the true peak is 16.85 frac/s
at u=0.24, x=0.298**; correction N-1, it also feeds any
velocity-tracked mesh sound):

| Abort at mesh | xAbort | Mesh vel → reversal start vel |
|---|---|---|
| 35ms | 0.321 | **16.8 → 0 frac/s** |
| 70ms | 0.733 | 7.0 → 0 |
| 105ms | 0.884 | 2.3 → 0 |
| 140ms (hand-off) | 0.920 | 0 → 0 (seamless) |

Ruling: this is **lawful character, named, not a defect to hide** —
an abort *arrests* the mesh; the hand said stop, and a velocity-
matched glide would read as the machine finishing its sentence
anyway. It is priced as risk R-2 (§8) with a device gate, not
redesigned here: the merge ruled the integrator, and a spring
abort is Rhea's killed reading (L-2).

### 3.4 The Sled Goes Home (M1) — sampling table (acceptance values)

Sled x = 1 − E(u), u = elapsed/650, E = `PathInterpolator(0.3, 0, 0.5, 1)`:

| u | 0.10 | 0.20 | 0.30 | 0.40 | 0.50 | 0.60 | 0.70 | 0.80 | 0.90 |
|---|---|---|---|---|---|---|---|---|---|
| E(u) | 0.037 | 0.140 | 0.289 | 0.458 | 0.620 | 0.760 | 0.868 | 0.943 | 0.986 |
| sled x | 0.963 | 0.860 | 0.711 | 0.542 | 0.380 | 0.240 | 0.132 | 0.057 | 0.014 |

Peak sled velocity **2.56 frac/s at u≈0.43** (≈635dp/s at 248dp);
velocity 0 at both ends. Fixed distance, fixed duration — the
invariance is the cadence (Fen).

---

## 4. LEAK REGISTER

**L-1 · SHIPPED, LIVE — one animator, two meanings (the leak the
brief asked me to hunt).** Today's `animateSpringBack`
(`BlockitCoreViews.kt:1214–1226`, 280ms `MechanicalOvershoot`) is
called on **both** UP branches: committed (≥0.82 → set 1f, seal,
commit, *then spring home*, :1182–1188) and uncommitted (:1190).
The shipped slide therefore already does exactly what R8 forbids:
a committed sled and a refused drag return on **one law with one
feel**, and the committed return happens at the *arm*, which is
why E6 cannot be bolted on (Fen §2, concurred). Disposition:
already governed — D1/D4/D5 delete the block and E6 replaces the
rest contract — but the builder must be told the deletion is a
*meaning* fix, not a refactor: `MechanicalOvershoot` (:28) loses
its last consumer in this slice and should be deleted from
`MonolithCurves.kt` in the same commit, or it will be re-borrowed.

**L-2 · SPEC PROSE — the abort aliased to the refusal spring,
twice.** Joss (joss.md §3b): the universal spring is "the same
spring Fen files for a drag released early **and a tap abort**."
Rhea (rhea.md §3c): the abort return runs "on the universal return
spring's character (260/0.78) time-boxed to 250ms." Both contradict
the merge (D5/R8: abort = Cato integrator, 250ms) and Cato's own
RETURN mode. A builder reading either part file without the merge
ships the unification R8 exists to prevent. Disposition: merge
governs; Marlowe to strike both sentences at the next parts pass.
My M2/M3 rows are written so the parts cannot be misread again:
the universal spring's meaning is *not committed*; the abort's
meaning is *committed-then-arrested mid-mesh* — different events,
different laws, per R8's own reasoning.

**L-3 · SPEC PROSE — the Strike's ink-settle borrows EasingA.**
Emil (emil.md §3.3) specs ink alpha 0.82→0.88 as
`tween(400, MonolithCurves.EasingA)`. The merge (D9) says "400ms
**lerp**." EasingA is already the app's most-shared easing (fades,
pills, throws, rockers — §1); spending it on a *density settle*
adds a fifth meaning to the token that can least afford one.
Disposition: **merge governs — linear interpolation, no Curves
token consumed.** Acceptance test T-STRIKE-2 pins the midpoint
(0.850 at 200ms) so the choice cannot drift back.

**L-4 · TOKEN DISCIPLINE — duration collisions.** Three 650s exist
in the studio's filings: E6's powered bezier (M1), Shield's
BANK-RETURN spring (110/0.85, weekly — Fen wild-seats/08, Kin M-20),
and Teo's lithification bezier (0.55, 0, 1, 0.45, not Slice E). Two
160s exist *in one view*: Mara's SEAT (M7) and the shipped closure
ink-settle (:371). The Register already disambiguates the 650s
("two different constants that share a duration; neither cites the
other") and Fen's original filing is explicit that only the
*tempo* is borrowed, never the curve. Ruling: **durations are set
at call sites for every E law** (Fen already priced M1 this way);
no `DURATION_650`, no `DURATION_160` token may be created, because
a shared constant is a unification waiting for its second consumer.
The builder who "helpfully" extracts one has leaked.

**Cleared after inspection (not leaks):** M9's three rooms share
one meaning (focus) under a written constraint; M2's two entries
share one meaning (not committed); M5's two entries share one
meaning (powered seat). CurveC/`MechanicalAnticipateOvershoot`
have no E consumer and gain none.

---

## 5. E6 CLOCK OWNERSHIP — which side owns the 650ms

The sled is a native `SlideToStartView` hosted by `AndroidView` in
`TactileDeckScreen.kt` (factory/update at :489–503 today: factory
creates the view once; `update{}` re-runs on every recomposition
and currently *hard-resets position* via `isArmedState = false` —
the behaviour Fen's rest contract deletes).

**Ruling: the VIEW owns the 650ms clock; Compose owns only the
trigger instant.** Three reasons, in order of weight:

1. **The animated state is view-private.** `dragFraction`
   (`BlockitCoreViews.kt:1076`) is written by the view's touch
   handler and read by its `onDraw` (:1104–1143: trail, label alpha
   `1 − 1.3·frac`, cap position). A Compose `Animatable` driving it
   would have to write across the AndroidView boundary every frame
   through `update{}` or a setter — reintroducing exactly the
   external-writer pattern the rest contract (Fen §3c, four writers,
   all inside the view) exists to kill, and paying interop
   recomposition per frame for 650ms.
2. **The interrupt is a view event.** The pre-empting
   `ACTION_DOWN` is delivered to the view's `onTouchEvent` first;
   the freeze must land *on that same frame* (Fen §3d). A
   Compose-owned clock learns of the DOWN one composition later —
   one frame of travel past the finger, every time, by
   construction.
3. **Precedent in the same trunk.** Every native sled in the app
   already runs this split: Schlitten's Shield sleds
   (`BlockitSuitcaseAndSwitchesViews.kt:60–73` throw, :189–201 cap,
   :329–347 rocker) are view-owned `ValueAnimator`s; the host only
   sets targets. **Coordinated with Schlitten by letter this run —
   no divergence**: his seat's ownership model and Fen's E6 spec
   (one `ValueAnimator.ofFloat(1f, 0f)` writing `dragFraction`,
   fen.md §3b) are the same architecture. His sleds are a different
   *family* (Shield tier sleds, Curve A) — the Goes Home animator
   must be built in `SlideToStartView`, never in the suitcase file,
   despite the shared word "sled."

**The split, exact:**

| Layer | Owns |
|---|---|
| ViewModel | `ReceiptState(sessionId, receiptPublishedMonoMs)` (monotonic, `elapsedRealtime`); the +1000ms schedule; the once-only guard; cancellation on navigate/new arm |
| Host (Compose) | `initialFraction = 1f` **once, in `factory{}`**, when created under a ReceiptState; the idle cap-words swap at receipt publication; **nothing in `update{}` ever writes position** |
| View (`SlideToStartView`) | The `ReturnHome` animator, `isReturning`, trail suppression, freeze/resume state (§6), cancel-on-DOWN, arrival at exactly 0 |

Compose-side twin: **do not create `EasingReturnHome`.** A twin
with no consumer is an invitation for a future seat to re-own the
clock from Compose. Its absence is part of this spec (test T-NOTWIN).

---

## 6. E6 INTERRUPT MATH — resume without a velocity jump

Fen's filed law (§3d): off-cap DOWN freezes the fraction; UP
resumes, **once**, with "duration scaled: 650ms × frozenFraction."
**That formula is wrong, and this section replaces it** (fork F-S1).
Fen's stated intent — "the cadence is a rate, not just a duration" —
is right; his formula does not implement it, because the powered
curve is not linear.

### 6.1 Why 650 × f fails, in numbers

Let f = frozen fraction (sled position; remaining travel = f, since
home is 0). A resume built as a *fresh* animator (f → 0, duration
650·f, same interpolator) commits two errors:

- **Velocity jump.** The fresh interpolator restarts at slope ≈ 0
  (M1's start slope is 0). The sled at freeze was moving at
  −E′(u_f)/650. Jump magnitude = the full frozen velocity:

| frozen f | playTime at freeze | sled velocity at freeze | Fen resume 650·f | phase-true remaining (§6.2) | duration error |
|---|---|---|---|---|---|
| 0.90 | 109ms | −1.75 frac/s | 585ms | 541ms | +44ms |
| 0.75 | 179ms | −2.44 frac/s | 488ms | 471ms | +17ms |
| 0.50 | 276ms | **−2.56 frac/s** | 325ms | **374ms** | **−49ms** |
| 0.25 | 385ms | −1.95 frac/s | 162ms | 265ms | **−103ms** |
| 0.10 | 480ms | −1.22 frac/s | 65ms | 170ms | **−105ms** |

Late freezes are the worst: at f=0.10 his formula amputates 62% of
the remaining road and restarts it from a standstill — the sled
visibly stalls, then crawls a compressed tail. That is a different
motion, not the same cadence resumed.

### 6.2 The technique — time-offset into the same easing

The easing is a pure function of *elapsed time on the 650ms
clock*: sled x(t) = 1 − E(t/650). **Resume re-enters that same
function at the phase where it froze; the paused interval is
simply removed from the clock.** No new easing, no new animator
law, no inversion table needed if play time is carried as state:

State the view keeps during RETURNING (additions to Fen §3b):

- `returnPlayTimeMs: Long` — **cached on every animator update**
  (`it.currentPlayTime` in the update listener). Never sample
  `currentPlayTime` after `cancel()`; the cached field is the
  freeze record.
- On off-cap DOWN (freeze): `frozenPlayTimeMs = returnPlayTimeMs`;
  `frozenFraction = dragFraction`; cancel animator. Both records
  are the same instant in two units — play time for the clock,
  fraction for the hit-test/draw.
- On UP with no capture (resume, once): create the return animator
  identically (ofFloat(1f, 0f), 650ms, `ReturnHome`), `start()`,
  then **in the same frame** `animator.currentPlayTime =
  frozenPlayTimeMs`. Remaining duration is `650 − frozenPlayTimeMs`
  **by construction** — the phase-true column of the table above,
  with no arithmetic for a builder to get wrong.
  (Equivalent lawful form: a Choreographer loop computing
  x = 1 − E((frozenPlayTimeMs + elapsedSinceResume)/650) via
  `ReturnHome.getInterpolation(...)`. Either mechanism; the *law*
  is the phase, not the plumbing.)
- Resume velocity at the first resumed frame =
  −E′(u_f)/650 = **exactly the velocity at freeze**. The only
  velocity discontinuity in the whole interrupt is the freeze
  itself — the finger's arrest, which is the interrupt's meaning
  (same class as the abort arrest, §3.3). The resume adds none.

Edge cases, closed: freeze at playTime 0 (DOWN in the trigger
frame) resumes as a fresh run; freeze after the animator's last
frame cannot occur (animator already IDLE). A **cap-captured**
freeze never resumes — the gesture owns the sled; if that gesture
later releases sub-catch, the sled exits under M2 (ordinary
release), and the Goes Home is dead, unrecorded (Fen §3d). Second
off-cap interruption: no resume (Fen's once-law stands) — the sled
rests at its frozen fraction until the next gesture (risk R-3).

### 6.3 The acceptance test (T-E6-RESUME, normative)

Freeze at playTime 276ms (f = 0.500 ± 0.005, sled velocity
−2.56 frac/s), hold 500ms, resume:

1. During the hold, `dragFraction` is constant to ±0.0005.
2. First resumed frame: position = 1 − E((276 + frameMs)/650)
   within ±0.005 fraction (kills the seek-after-first-frame
   implementation, which renders one frame at x=1).
3. Resumed velocity, finite-differenced over the first 3 frames,
   is within ±15% of −2.56 frac/s and **not** within ±0.2 of 0
   (kills the fresh-animator implementation outright).
4. Arrival at 0 lands at resume + 374ms ± 1 frame (kills the
   650·f implementation, which lands at +325ms).
5. A second off-cap DOWN/UP cycle leaves the fraction frozen
   after UP (once-law).

---

## 7. PRICE + FORKS

### Price of this joint's additions (net lines, production)

| Item | Net |
|---|---|
| `MonolithCurves.kt`: 6 tokens (`ReturnHome`, `HubPressDip`, `HubSeatReturn`, `VertigoEasing`, `RackEasing`, `StrikeEasing`) | +6 |
| `MonolithCurves.kt`: 7 duration constants (45/110/140/240/250/1050/1200; 650 and 160 stay at call sites — L-4) | +7 |
| `MonolithCurves.kt`: header amendment (:14) + consumer-law KDoc on M1/M9 tokens | +≈10 |
| `MonolithCurves.kt`: delete `MechanicalOvershoot` once L-1's last consumer dies (with the D-build) | −2 |
| **Curves file total** | **≈ +21 net** |
| E6 view delta over Fen's filed price (playTime caching, seek-resume, freeze records — §6.2) | **+≈14 net** (Fen's E6 core 102 → ≈116; his ~60 tests stand, +T-E6-RESUME ≈ +18 test lines) |
| All other E joints | unchanged — this examination adds no lines to them |

### Forks for Ilya / Ren

- **F-S1 · E6 RESUME LAW (load-bearing).** Fen's filed
  `650ms × remaining fraction` vs §6.2's **time-offset**
  (remaining = 650 − frozenPlayTime). *Recommendation: time-offset.*
  It is the only form that satisfies the brief's own requirement
  (resume without a velocity jump) *and* Fen's stated intent
  (cadence as rate). Fen has the numbers by letter this run; if he
  holds his formula, the velocity jump in the §6.1 table is the
  price, stated, not hidden.
- **F-S2 · VERTIGO SETTLE IS NOT A SETTLE.** Recomputed from
  Wren's filed easing (0.5, 0, 0.85, 0.35): travel per phase =
  Departure **1.5%**, Recession **37.4%**, Settle **61.1%** — and
  the transform velocity is *maximal* at the end (slope 4.33;
  wheel y moving **83dp/s**, scale 0.83/s, in the final frame
  before a hard stop at t=1050). Wren's §3.4 claim ("Settle:
  decelerating approach, velocity →0 only at the endpoint") is
  false for transforms; it is true only for the blur sampling
  (96% complete by 780ms, end slope 0). Options: (i) keep the
  easing, strike the claim, and let the 16ms @ 0.7 settle knock at
  t=1050 mask a last-frame step of ≈1.4dp — a landing with a sound,
  device-gated; (ii) Wren re-files a transform easing with end
  slope 0 (new constants; the Departure's slow head is worth
  keeping). *Recommendation: (i) now, (ii) only if the device pass
  sees the cliff.* The claim must not ship false under either.
- **F-S3 · PRESS/SEAT NAMES.** Mara files generic `PRESS`/`SEAT`
  tokens; Kin's system already spends those names across screens
  (FLOW key dip, drawer 280 SEAT, overlay entrances) with the
  shared meaning "arrival." Scoped names (`HubPressDip`,
  `HubSeatReturn`, as in §2) keep E's one-consumer law true by
  construction; generic names make it a family law that a future
  seat can stretch. *Recommendation: scoped names in E; the
  generic family question is Kin's and Sana's, not this slice's.*

**Not forks (rulings, merge-governed):** L-1 deletion, L-2 strikes,
L-3 linear ink-settle, L-4 no shared duration tokens, R-SPULE-1
direct sampling, M2 floor-clamp, G-1 named as a gap for Opal.

---

## 8. RISKS

- **R-1 · `setCurrentPlayTime` seek behaviour varies by OEM/API.**
  The seek must land before the first rendered frame after resume
  (T-E6-RESUME clause 2 is the detector). If a device renders one
  frame at playTime 0 (sled flashes to seat), fall back to the
  Choreographer form in §6.2 — same law, hand-driven clock. Decide
  on the fence device, not in the abstract.
- **R-2 · Abort arrest visibility (see §3.3).** Junction steps up
  to 16.8 frac/s at early-mesh aborts. If the device pass reads it
  as a glitch rather than an arrest, the fix is a 2-frame velocity
  blend at the junction — a new micro-law requiring its own
  filing; do not let a builder improvise it inline.
- **R-3 · Twice-interrupted sled rests mid-track** (Fen's once-law,
  §6.2). The Deck's idle state then shows a cap at e.g. 0.40 with
  idle words — a false rest the eye may read as a half-armed
  state. Fen's law stands (a twice-told tidying stops performing),
  but the first device pass should look at it directly; if it
  reads wrong, the cheapest honest fix is completing the return
  on the next Deck composition (never mid-idle).
- **R-4 · Refusal spring on an unproven classpath.** Joss's risk
  stands and applies to my M2 form: the trunk has no
  `SpringAnimation`/dynamicanimation usage anywhere, which is why
  M2 is specced as a hand integrator in the wheel/Cato idiom. If a
  builder reaches for `androidx.dynamicanimation` instead, the
  constants survive but substep behaviour (and the floor-clamp)
  must be re-proved — T-REFUSAL-1 is written to catch either form.
- **R-5 · The 1050/1200 constants invite a `DURATION_` extraction
  for E1's 210 cadence** (90+120) by symmetry. E1's cadence is an
  audio law, not a motion law; it stays out of the curves file.

---

## 9. ACCEPTANCE TESTS (this joint's list; all sample-based)

| Test | Pins |
|---|---|
| T-THREE-LAWS | From matched starts, sample abort / refusal / sled-home at +100ms: positions pairwise differ by > 0.02 fraction (R8 made measurable) |
| T-E6-RESUME | §6.3, clauses 1–5 verbatim |
| T-E6-TABLE | Uninterrupted return reproduces §3.4's table within ±0.005 at every column |
| T-ABORT-1 | Abort at mesh 70ms: x continuous at 0.733 ± 0.001, monotonic decrease, x(250ms) = 0 exactly, zero latch callbacks, zero VM notifications |
| T-REFUSAL-1 | Hold: x = 0.65 constant for 300ms despite UP at +100ms. Spring: x never < 0, rest by F0+560ms, x at spring+100ms inside the simulated spring band ±0.01 |
| T-SEAT-1 | Cato seat: knock fires before latch at every entry velocity in {0, +3, +6}/s; displayed x never > 1.00 in any substep |
| T-HUB-1 | PRESS: depth reaches 1 at 45ms ± 1 frame from any start depth. SEAT return: depth(80ms) = 1 − 0.952 ± 0.01; core offset = depth × 2dp at sampled frames; identical depth samples at block counts 2 and 5 (count-invariance) |
| T-VERTIGO-1 | Direct sampling: vertigoT(180ms) = 0.015 ± 0.01, vertigoT(780ms) = 0.389 ± 0.01; blur sample at 780ms = 0.960 ± 0.01 of its range; backgrounded mid-shot resumes at true elapsed in one frame |
| T-STRIKE-1/2 | Scale: 1.0376 ± 0.002 at 55ms, exactly 1.0 at 110ms. Ink: alpha 0.850 ± 0.005 at 200ms (linear — L-3) |
| T-NOTWIN | Grep acceptance: `MonolithCurves.kt` contains no `EasingReturnHome`, no `DURATION_650`, no `DURATION_160`; the slide view contains no `0.38f` spring constant (wheel guard) |
| T-ORANGE-1 | Exit carriers constant between step boundaries (samples at 25/75/125ms equal step values), different across them; zero orange pixels among carriers at t=200 |

---

## 10. MAIL / WALL NOTE

Inbox at start: **empty** (no unread letters; `read/` empty) —
nothing owed, nothing moved. Sent this run: **Fen** (F-S1, with
the §6.1 table — his formula, his intent, the replacement);
**Schlitten** (clock-ownership convergence, §5, + the sled-family
boundary); **Ilya** (coordinator — verdict, price, forks F-S1..S3).
Wall: one signed entry appended — the inventory in one breath,
L-1, and the two recomputed corrections (mesh peak 16.85, Settle
61%).

## NEXT (per the no-silent-shutdowns law)

- **What I want to do next:** re-examine the same three returns
  *after* the slide build lands — a T-suite run against the built
  view (T-THREE-LAWS and T-E6-RESUME on the real animator, not the
  spec) is the audit this file cannot self-perform; I am the seat
  that priced the seek risk (R-1) and should be the seat that
  measures it on the fence device.
- **What I could do unassigned:** a motion-law pass over Slice D's
  remaining unexamined springs (Zug-II's valve drain spring and
  PRESS-BACK 800/0.65 in Kin's table were never inventoried
  against the trunk the way §1 inventories B2), and a duration-
  collision sweep across all 20 slice-de parts (L-4 found three
  650s by hand; a grep-complete table would close the class).

— Spule, Improvers · the coil (springs)
