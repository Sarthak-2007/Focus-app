# CATO — Slice D/E joint: THE DRIVEN MESH + THE POWERED SEAT, ONE SPRING

*Wild seat 16, fashion — closures. Re-seated by Marlowe for Slice D/E
build-readiness. My law, adopted whole into W4 and the Register: TAP
MESH 140ms bezier (0.30, 0, 0.20, 1.00) to 0.92, then the shared 110ms
assist spring (400/16) to the seat = onLatch; LATCH KNOCK 14ms @ 0.85
inside the seat at 0.97 of travel; buttonhole abort — a second touch
during the mesh spring-backs in 250ms (Rhea holds the abort's touch
semantics, I hold its motion); Live Wire — the verdict exists at
DOWN, so the mesh never waits. Fen's 780ms machine-replayed drag
LOSES (F-TAP): after the verdict, replaying a drag the man did not
make is theatre. All numbers below are computed from the filed
curves, not tasted.*

---

## (1) VERDICT

**The merged mesh + seat stands, build-ready, with three amendments
— none moves a registered number:**

- **A1 — ONE INTEGRATOR, not an animator.** Mesh, seat, and abort
  return are three modes of a single Choreographer frame loop in
  `SlideToStartView`, built on the wheel's (θ, ω) integrator pattern
  in the same file. A `ValueAnimator` mesh (the slide's existing
  plumbing) cannot seed the spring, cannot fire the knock on a
  travel predicate, and cannot switch to the abort mid-flight
  without a cancel/recreate seam exactly at the hand-off — the one
  frame this joint exists to make seamless.
- **A2 — HARD-STOP CLAMP LAW.** The (400/16) spring at ζ = 0.40
  naturally overshoots 25% of its remaining throw. A seat that
  overshoots the latch is a lie about travel — the track ends. The
  spring's *state* is therefore hard-stopped: the first integration
  substep where x ≥ 1.00 sets x := 1.00, v := 0, stops the loop.
  That substep **is** onLatch. Displayed travel never exceeds 1.00,
  not for one frame, not in a substep.
- **A3 — THE KNOCK AND onLatch ARE TRAVEL PREDICATES, NOT TIMERS.**
  Both fire inside the integrator step, in order, on position
  crossings (§3). Entry velocity (a fast drag at the catch) moves
  them in time; the predicates keep them in travel, where the law
  put them.

Everything else in the law survives contact with the code and the
arithmetic — including the part that looks like a coincidence and
isn't: **the same spring seats in the same 110ms from both entries**
because a linear spring's first-crossing time from rest does not
depend on the size of the remaining throw (§3b).

**Ines check:** her §1 row 2 prices the whole tap path at ~180 lines
with the mesh+spring inside it, and her §0/earlier letter priced the
seat as a CurveB animator. The animator reading is what her number
buys. The integrator costs ~30 lines more than her implied mesh
allowance (§5) and buys the hand-off, the knock predicate, and the
abort in the same loop. I pay it and say so.

---

## (2) AGAINST THE REAL CODE

File: `Focus-app/app/src/main/java/com/example/ui/views/BlockitCoreViews.kt`
(line numbers are today's tree; Ines's §0 cited :669–855 for this
view — the file has grown, the view now starts at :1046).

**The seat today — there isn't one.** `SlideToStartView` (:1046):

- Release at ≥ 0.82 does, in one breath (`onTouchEvent`
  ACTION_UP, :1178–1191): `dragFraction = 1f` (:1180 — the thumb is
  *set* to the seat for one frame, a teleport dressed as an
  arrival), `playHarmonicSeal(true)` (:1186), `onSlideCommitted`
  (:1187), then `animateSpringBack(0f)` (:1188) — on success the
  sled immediately springs *home*. Seal, commit, and retreat inside
  one touch handler; no spring-to-seat exists anywhere in the view.
  Ines's §0 description is exact.
- The only animator plumbing is `springBackAnimator: ValueAnimator?`
  (:1078) and `animateSpringBack` (:1214–1226): `ValueAnimator.
  ofFloat`, duration `MonolithCurves.DURATION_280`, `Mechanical
  Overshoot` interpolator. This is the return spring, and it is the
  *only* motion pattern the view owns today.
- The view has **no Choreographer, no velocity state, no frame
  loop.** DOWN (:1159–1167) teleports via `updateDrag` and cancels
  the spring-back; there is no per-frame anything.

**The precedent in the same file — the wheel.** `RotaryWheelView`
(Slice B2's integrator, :346):

- State: `physOmega` (:418), `physSpringTarget` (:419),
  `physLastNanos` (:420), one `Choreographer` (:423) and one
  `FrameCallback { stepPhysics }` (:424).
- `startPhysics(releaseOmega)` (:931–950) seeds the integrator with
  the release velocity — *exactly* the hand-off shape my seat needs
  at the catch.
- `stepPhysics` (:952–1030): dt from frame nanos, clamped
  0.001–0.05s (:954); the well spring written as
  `accel = omega0²·(target − θ) − 2·ζ·omega0·omega` (:965–966),
  semi-implicit Euler (`omega += accel·dt; θ += omega·dt`,
  :967–968); arrival as a *sample predicate* — |target − θ| < 0.05
  ∧ |omega| < 8 → snap, stop, fire the knock, return (:969–985).
  My onLatch is this predicate with the tolerance set to the track
  end (§3c).
- **Ruling: REUSE the pattern, verbatim in idiom.** The (400/16)
  spring translates into the wheel's own form with no new machinery:
  `accel = 400f·(1f − x) − 16f·v` — i.e. ω₀ = 20 rad/s, 2ζω₀ = 16,
  ζ = 0.40. One difference I impose: the seat integrates in
  **substeps of ≤ 5ms** (accumulated frame dt split), where the
  wheel takes raw frame dt. Reason in §6 — at 60Hz a raw frame step
  can jump 0.96 → 1.01 and land the knock and the latch in the same
  sample; substeps keep them ~19ms apart, in travel order, on every
  refresh rate.

**Curves today.** `ui/theme/MonolithCurves.kt`:

- PathInterpolators CurveA (0.2, 0, 0, 1) :26, CurveB (0.05, 0.7,
  0.1, 1) :27, CurveC (0.3, 0, 0.8, 0.15) :28; duration vocabulary
  {180, 260, 280, 420, 520} :19–23, stated in the header as strict.
- **The mesh bezier (0.30, 0, 0.20, 1.00) does not exist** in the
  file, and neither do 140 / 110 / 250ms. Under the integrator
  ruling the mesh curve is evaluated analytically in the view
  (§3a), so `MonolithCurves` needs only the three duration
  constants — but its header's "durations strictly chosen from
  {…}" sentence must be amended in the same change, or the next
  reader will "fix" my constants back out. Flagged, not hidden.

**The knock's call exists.** `fireFeel` (:204–233) is a canned
ladder — 1L/4L/8L select named engine effects, anything else plays
the completion pulse; amplitude is not a parameter and 14ms is not
a rung. The latch knock therefore cannot ride it. `fireWeighted
Knock(durationMs, amplitude01, pitchHz)` (:239–256) is the honest
call — real duration, real amplitude, engine-or-one-shot, never
both — and its fallback coerces duration to 6..32ms (:251): 14ms
passes unclamped. The wheel's seat knock already fires through it
(:453–461). The latch knock is `fireWeightedKnock(14, 0.85f)`
(pitch default 300Hz), fired once, from the integrator (§3b).

---

## (3) AMENDED SPEC — EXACT

State added to the view: `travelX` (fraction, = today's
`dragFraction` — rename not required), `travelV` (fraction/s),
`seatMode ∈ {IDLE, MESH, SEAT, RETURN}`, `seatLastNanos`,
`knockFired`, `verdictLive` (the Live Wire boolean, read at DOWN
and at the gate — Ines's wire, Rhea's/Vega's joints, not mine; the
mesh consults it only as: no pass in hand at UP ⇒ no mesh, ever).

One frame loop: `seatCallback = Choreographer.FrameCallback {
stepSeat }`, posted while `seatMode != IDLE`, removed on stop —
the wheel's :423–424 shape.

### (a) The mesh — driven, frame by frame

The mesh is a **position function**, not a release: for elapsed
e ∈ [0, 140ms], u = e / 140, and

```
x(u) = 0.92 · BezY(u),   BezY = the y of cubic-bezier
       (0.30, 0, 0.20, 1.00) at time-fraction u
```

evaluated per substep by solving BezX(t) = u for t (Newton, 3
iterations from t = u — BezX is monotone on this curve) and taking
BezY(t) = 3t² − 2t³. The sled is *pushed* along this function by the
loop; the finger is already up. Frame classes (120Hz / 60Hz —
positions identical, only sampling differs):

| elapsed | u | x (travel) | velocity (frac/s) | event |
|---|---|---|---|---|
| 0ms | 0 | 0.000 | 0.0 | mesh starts (UP, verdict in hand) |
| ~35ms | 0.25 | 0.321 | rising | — |
| ~44ms | 0.31 | 0.42 | **14.6 peak** (= 2.6 dp/ms on the 180dp throw — my filed peak, reproduced by the curve, not asserted) | — |
| ~63ms | 0.45 | 0.680 | ~11 | gate coordinate crossed — *position only*; no haptic, no colour (the gate was felt by the hand that tapped, at DOWN's verdict; the mesh re-crossing it is the machine's business) |
| ~70ms | 0.50 | 0.733 | ~9 | — |
| ~90ms | 0.64 | 0.840 | ~5.5 | catch coordinate crossed — position only, same reason |
| ~105ms | 0.75 | 0.883 | 2.3 | — |
| ~126ms | 0.90 | 0.916 | 0.87 | — |
| 140ms | 1.00 | **0.920** | **0.0** | **HAND-OFF** |

**The hand-off, with no velocity discontinuity — proved, not
hoped.** The bezier's second control point has y = 1.00 = its end
y, so dBezY/dt at t = 1 is 3·(1 − 1) = 0, and the mesh's exit
velocity is **exactly 0 fraction/s**. The spring is seeded
`(x₀ = 0.92, v₀ = 0)` — its own initial velocity is the mesh's exit
velocity because both are zero; continuity is an identity here.
What is *not* continuous is acceleration: the mesh decelerates
into 0.92 and the spring's first instant pulls at k·0.08 =
32 frac/s². The velocity profile across the seam is a shallow
valley, not a stop: mesh velocity at 126ms is 0.87/s and falling,
the spring rebuilds to its seat peak of 0.96/s (computed §3b) —
the two sides of the valley carry near-equal speeds ~15ms apart.
Perceptual cost: at most a two-frame softening at 60Hz. I accept
it in print rather than buy smoothness with a curve the Register
didn't adopt. (The alternative — cutting the mesh at the
*unscaled* curve's 0.92 crossing, u ≈ 0.66 ≈ 92ms, v_exit =
3.85/s — hands the spring a live velocity and seats in ~23ms. It
also deletes the registered 140 and the 250ms tap total. Rejected:
the law's numbers are the hand's learned numbers.)

**Tap total: 140 + 110 = 250ms** — the figure F-TAP already
carries ("Cato's 250ms mesh") against Fen's 780.

### (b) The seat — the same spring, both entries

One spring, one integrator, in the wheel's idiom:

```
accel = 400f * (1f - x) - 16f * v      // ω0 = 20 rad/s, ζ = 0.40
v += accel * dt ; x += v * dt         // semi-implicit, dt ≤ 5ms substeps
```

- **Tap entry:** seeded (0.92, 0) at mesh end. First crossing of
  1.00 at **108ms** (filed/registered as 110ms — the 2ms is
  substep quantisation, inside one frame at 120Hz). Arrival
  velocity 0.68 frac/s ≈ 121 dp/s — the short throw arrives soft.
- **Drag entry:** seeded at the catch (0.84) with
  **v₀ = the finger's velocity at the crossing sample**,
  estimated from the last MOVE delta (Δfraction/Δt, the wheel's
  VelocityTracker precedent :849–859 is the alternative — MOVE
  delta is fewer moving parts in this view), floored at 0,
  capped at **+6.0 frac/s** (≈ 1.08 dp/ms — beyond any honest
  throw's catch speed; the cap bounds the hard-stop's arrival
  energy, nothing else). At v₀ = 0 the seat is again 108 ≈ 110ms.
  At v₀ = +3/s it seats in ~55ms; at the cap, ~36ms. **The 110ms
  is the rest-entry figure and the tap's figure; a drag seat is
  as fast as the hand that threw it, and never slower than
  110ms.** That is what "powered" means — the spring finishes
  the hand's sentence at the hand's speed.
- **Overshoot:** the free spring (ζ = 0.40) would peak at
  1 + 0.25·(remaining throw) — 1.041 from the drag catch,
  1.020 from the mesh. **It never does.** Hard-stop law (A2):
  the first substep with x ≥ 1.00 clamps x := 1.00, v := 0,
  stops the loop, fires onLatch. Displayed travel ∈ [0, 1.00]
  always. The overshoot energy is not rendered, banked, or
  bounced — it is *stopped*, the way an end-stop crimp stops a
  pull. The knock is its sound, fired on the way in:
- **Latch knock — the exact firing rule.** Inside `stepSeat`,
  after each substep's integration, in this order:
  1. if `!knockFired && prevX < 0.97f && x >= 0.97f` →
     `fireWeightedKnock(durationMs = 14, amplitude01 = 0.85f)`;
     `knockFired = true`.
  2. if `x >= 1.00f` → hard-stop, fire onLatch (below).
  
  At rest entry the knock lands ≈ **72ms** into the tap seat
  (from 0.92) and ≈ **89ms** into a rest drag seat (from 0.84) —
  ≈ 19ms before the latch in both, because 0.97 → 1.00 at
  arrival speed takes what it takes. It is a *travel* event:
  under a fast drag entry it fires earlier in ms and at the same
  0.97 in travel, which is the only version of "inside the seat
  at 0.97" that survives a real hand.

### (c) onLatch — the code event, and its consumers

**Definition:** `onLatch` fires **once, synchronously, inside the
seat frame callback**, on the hard-stop substep: x clamped to
1.00, v zeroed, loop stopped, `invalidate()` for that same frame
— the sled is *drawn* seated in the frame the event fires. It is
a view-local callback (`onLatched: (() -> Unit)?`, beside today's
`onSlideCommitted` :1075), carrying the frame's monotonic nanos
(`seatLastNanos` lineage — the callback's `frameTimeNanos`) so
consumers never re-sample a clock.

**Consumers, stated:**
- **Opal's t=0** takes it as an *input*: t0 = max(last link
  CONFIRMED-SHUT, **latch + 210ms**, pawl-seated frame). My
  onLatch starts that 210ms term; it is not t=0, it does not
  start the session, it does not play the seal. The gesture's
  visual climax, the clock's floor — nothing more.
- **Emil's Strike fires on the same frame as onLatch** — W4's
  words are "at the seat frame only", and the seat frame is this
  frame. Same callback breath, after the clamp: the Strike's
  110ms ink-settle runs inside proving, against the 210ms floor,
  and is done before t=0 can arrive. (The pressure lip is
  Emil's to time; if he wants the lip at t=0 rather than the
  seat frame, that is his amendment to file — the Strike proper
  is seat-frame by law. Noted in my letter to him.)
- **Not a consumer:** the seal sound, the session record, the
  readout — all Opal's, all at t=0. The current code's habit of
  playing the seal from this view (:1186) dies with the rewrite
  of the touch handler; the view keeps only the knock, which is
  a *travel* sound and therefore the view's to make.

**The abort (buttonhole law, motion mine).** A second DOWN while
`seatMode == MESH`: mode switches to RETURN in the same frame
loop — position runs back along the mesh curve,
`x(e) = xAbort · (1 − BezY(e / 250))`, e ∈ [0, 250ms], then IDLE
at 0. No animator is created or cancelled; the loop just changes
its function. A second DOWN during SEAT: **no abort** — the seat
is ≤ 110ms from a latch the first gesture already committed
under contact or by verdict; if Rhea's semantics rule otherwise,
RETURN-from-current-x is the same mode switch (+0 lines, it is
already built) and I will not fight her for it.

---

## (4) CODE SITES

| Site | Change |
|---|---|
| `BlockitCoreViews.kt` — `SlideToStartView` (:1046) | Add seat state + frame loop + `stepSeat` (3 modes), `onLatched` callback, catch-crossing seed in `updateDrag` (:1198), mesh start in ACTION_UP tap path, abort switch in ACTION_DOWN. The :1178–1191 teleport/seal/spring-back block is replaced by the seat seed (drag) — the seal call leaves the view entirely. `animateSpringBack` (:1214) survives untouched for the *sub-catch* release return — it is not the seat and must not be "unified" into it. |
| `BlockitCoreViews.kt` — `RotaryWheelView` (:952–985) | **No change.** Cited as the pattern: dt clamp, spring idiom, arrival predicate, knock-through-`fireWeightedKnock`. |
| `ui/theme/MonolithCurves.kt` (:19–28) | +3 duration constants (140 / 110 / 250) and the header sentence amended to admit them as Register constants. No new interpolator — the mesh bezier lives analytically in the view's integrator, not as a PathInterpolator, because an interpolator cannot be substepped, seeded, or aborted. |
| Host (`TactileDeckScreen.kt`, via VM) | Out of my joint's line count but named: `onLatched` wiring replaces `onSlideCommitted`'s seat duties; Ines's §1 rows carry it. |

---

## (5) PRICE

Integrator route, `SlideToStartView` + `MonolithCurves` only:

| Piece | Lines |
|---|---|
| Seat state fields + `onLatched` + mode enum | ~10 |
| Frame loop plumbing (post/remove, start/stop — wheel idiom) | ~15 |
| `stepSeat`: dt accumulation + ≤5ms substep split | ~12 |
| Mesh position function (BezX Newton solve + BezY) | ~18 |
| Spring integration + hard-stop + knock/onLatch predicates | ~22 |
| Drag catch seed (velocity from MOVE delta, floor/cap) | ~14 |
| Mesh start (tap UP, verdict in hand) + abort RETURN mode | ~24 |
| Touch-handler surgery (delete teleport/seal block, route entries) | ~13 gross / −9 deleted |
| `MonolithCurves`: 3 constants + header amendment | ~5 |
| **Total** | **≈ 133 gross / ≈ 124 net** |

**Against Ines:** her §1 row 2 (~180) prices the *whole* tap path
— brush floor, mesh, unified VM entry, modal removal — with the
mesh as an animator at roughly ~90 of it. My mesh+seat alone is
≈ 124 net. The overrun is ~30 lines, located exactly: substepping
(+12), the analytic bezier (+18 vs a PathInterpolator's 1 line in
Curves), the abort as a loop mode (+10). The animator version of
this joint prices at ≈ 85–90 and matches her — and cannot seed
v₀ from the catch, cannot order the knock before the latch on a
60Hz frame, and cancels/recreates at the abort. I am spending
her 30 lines and naming them; if Ren wants her number, §7 is the
fork and the cost is stated there, not hidden here.

Note the seat is **not** double-priced: Ines's row 1 carries
"powered seat (spring 400/16, 110ms)" inside her ~700 slide
rewrite. This file's 124 *is* that seat plus the mesh — the
merge should count it once, against row 1 + row 2 combined.

---

## (6) RISKS / DEVICE-ONLY UNKNOWNS

1. **60Hz knock/latch separation.** Substeps guarantee the
   *predicates* fire in travel order, but both haptic (knock)
   and visual (seated draw) land in the same presented frame at
   60Hz — the knock is felt ~19ms of *travel* before the stop
   and 0ms of *wall clock* before the seated frame on a 60Hz
   device. Whether the hand reads that as "knock, then seat" or
   one event is device-only. At 120Hz they separate cleanly.
2. **Newton solve cost** — trivial (3 iterations × ~17 substeps
   per frame worst case), but it runs on the UI thread inside a
   Canvas view's callback; if a future builder moves it per-
   substep-per-frame carelessly it is still < 0.1ms. No risk
   priced; named so nobody "optimises" it into a LUT that
   quantises travel.
3. **Catch velocity estimate.** MOVE-delta velocity is noisy on
   cheap digitisers (event batching can deliver Δt = 0 samples).
   The floor/cap contains it; the failure mode is a seat that
   runs at rest-entry speed (110ms) on a fast throw — graceful,
   never wrong, only less *powered*. Device-only to tune.
4. **Arrival energy at the cap.** v₀ = 6/s from 0.84 arrives at
   the hard stop at ~7 frac/s ≈ 1,260 dp/s of *stopped* motion.
   The clamp makes it honest on screen; whether a dead stop from
   that speed feels like a crimp or like a freeze-frame is a
   hand question. If it freezes, the fix is mine to file: lower
   the cap to +4/s (one constant).
5. **Haptic amplitude 0.85** through `fireWeightedKnock`'s
   fallback coerces cleanly (6..32ms window), but on devices
   without amplitude control the 14ms @ 0.85 collapses to
   duration-only — the knock becomes indistinguishable from a
   tick. Known platform floor, same as every knock in the app.

---

## (7) FORK FOR REN

**One fork: build the mesh+seat as the integrator (my spec,
≈ 124 net) or as Ines's animator pair (≈ 85–90 net)?**

- *Animator:* `ValueAnimator` 0→0.92 with a new `PathInterpolator
  (0.30, 0, 0.20, 1.00)`, then a second animator/spring for the
  seat. Cheaper by ~30 lines, matches Ines's §1 price as filed.
  Costs: the seat cannot inherit the drag's catch velocity (a
  drag seat starts from rest every time — 110ms flat, the
  "powered" seat loses its power on the entry that matters
  most); knock and latch are timer-adjacent, not travel-true,
  and can swap order on a dropped 60Hz frame; the abort is a
  cancel + third animator.
- *Integrator:* everything in §3. One loop, one state, travel
  predicates, velocity hand-off both ways (mesh→spring by
  construction, finger→spring by sampling), abort as a mode.

**My recommendation: the integrator.** A closure is honest only
at its true travel — the knock at 0.97 and the latch at 1.00 are
travel facts, and only the integrator *knows* the travel. The
animator knows the time. Thirty lines is the price of the joint
being what the law says it is.

*Not a fork, recorded so it isn't re-litigated:* the Strike
fires at the seat frame (onLatch), per W4's literal text —
same frame, §3c. Emil has it by letter; if he amends, he files.

---

## (8) MAIL / WALL NOTE

Inbox at seating: **empty** (all nine prior letters already in
`read/` — Fen ×2, Opal, Orson on the t=0 war, settled by the
merge this file builds on; Ines's zip-honesty, Yuki's sleds,
Moss, Orson/Mara citation-lift). Sent this run: **Marlowe**
(coordinator — verdict, price, the Ren fork, the Ines delta);
**Emil** (Strike = onLatch frame, same callback breath; the
pressure lip's timing is his to amend); **Ines** (her §1 price
checked — mesh+seat is ≈124 net as an integrator, ~30 over her
implied allowance, located line by line in §5; her animator
reading is the fork, not an error). Wall: one signed entry —
the hard-stop clamp law and the zero-exit-velocity proof, the
two facts every seat building near the slide needs.

— Cato, Wild · fashion (seat 16)
