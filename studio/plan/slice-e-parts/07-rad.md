# SLICE E — RAD (Improvers, the wheel): E3 GHOST SUBJECT 1 — THE WHEEL

**Seat:** Rad, Improvers — I own the wheel (B2's physics: one (θ,ω)
integrator, unwrapped travel, the FLOW basin, the knurl).
**Joint:** E3 (Vertigo), ghost subject 1 of 2 — fork F17, ratified:
at the seal frame the shipped code deletes the wheel and sled,
leaving the shot no subject; the subjects persist as GHOSTS through
Settle. Zug-II owns ghost subject 2 (the sled) + input death
generally; this file is the wheel half, priced separately.
**Method:** every line below verified this run against today's
trunk (`Focus-app/…/ui/screens/TactileDeckScreen.kt`, 534 lines;
`…/ui/views/BlockitCoreViews.kt`, 1419 lines). No code edited, no
gradle run. Inbox at start: no unread letters (six, all in `read/`).

---

## 1 — THE FINDINGS (today's trunk, exact sites)

### 1.1 The deletion — the D/E citation verifies, and it is worse than cited

`TactileDeckScreen.kt:273` — `if (!isSessionRunning) {` opens the
branch that composes the wheel. Verified at today's line, exactly
as D/E cited. The branch runs **:273–336** and contains not just
the `AndroidView` (:274–295, `testTag("rotary_wheel_view")`,
288dp) but also the FLOW key + FINISHES row (:296–335). The
`else` branch (:337–365) composes the running replacement: the
6dp progress track + finish/% row.

`isSessionRunning` itself is `:118` —
`activeSession != null && activeSession.isActive`. **Timing fact
the shot must respect:** this flips at the session-row *insert*
emission (cascade link L1), which precedes Wren's t=0
(`max(last link CONFIRMED-SHUT, latch + 210ms, pawl frame)`).
So the deletion today happens *before* t=0 — during PROVING.
Any ghost mechanism keyed to t=0 is already too late; the ghost
must be born at the **flip frame F** (first frame
`isSessionRunning` renders true) and be present at t0−1 with
Departure values, per Wren's table. F ≤ t0 always.

### 1.2 The AndroidView host sites

Wheel host: **:274–295**. Its `update{}` block, :280–294, pushes:

| Push | Line | Today |
|---|---|---|
| `view.isNightMode = effectiveNight` | :281 | live flag |
| `view.soundEngine = soundEngine` | :282 | — |
| `view.minutesValue = dialMinutes` | :283 | live dial |
| `view.studyBlocksCount = studyBlocksCount` | :284 | live count |
| `view.isLockedSession = false` | :285 | **hardcoded false** |
| `onMinutesChanged / onCycleBlocksCount / onSetBlocksCount` | :286–293 | live callbacks into the VM |

The sled host is :487–506 (Zug-II's site; its pushes are
`isNightMode` :494 and `isArmedState = false` :496, as the brief
stated). Note the symmetry of the crime: both hosts hardcode
their lock/arm flags to the *idle* value, because both views are
only ever composed while idle.

`effectiveNight` is `:119` — `isDarkTheme || isSessionRunning` —
so at frame F the wheel's `isNightMode` push flips day→night in
the same recomposition that deletes it. Under Wren's amendment
A3 the tone step is the shot's (glyph at t=0, ground at t=400);
§3.4 gives the wheel's share of that deferral.

### 1.3 What the wheel view holds at the seal instant

`RotaryWheelView` (`BlockitCoreViews.kt:346`). The complete
state that exists at frame F and nowhere else:

- **θ = `wheelAngleDeg`** (:403) — UNWRAPPED, range 108..432
  (comment block :408–416): FLOW seats at 108, the strait runs
  108..126 (centreline 117), numeric stops 10..180 at 9° per
  5 min from 126 to 432. Total travel FLOW→180 = 324°.
- **ω = `physOmega`** (:418) — deg/s, signed. In coast it is
  clamped to ±1200 (:947); in a well spring it is whatever the
  spring equation holds. **ω at F is not zero in the general
  case:** the arm gesture is a slide, not a wheel touch, so the
  wheel is untouched during the arm — but a flick *into* the arm
  (spin the wheel, immediately slide) leaves the integrator in
  coast or capture-spring at F, with up to 1400ms of flight
  still owed (the fling deadline, :1023). `inPhysics` (:417)
  and `physSpringTarget` (:419) say which mode.
- **The value pair:** `minutesValue` (:348, dial 5..180) and
  **`studyBlocksCount` n** (:360, 1..8).
- **`barInkFraction`** (:400) — if n just landed on 4 or 8, the
  tetrad closure animator (:401, 160ms, 0.88→1.0) may still be
  running at F. This is live ink motion owned by the count —
  see §3.3, it is the ghost's one real light trap.
- Housekeeping: `isDragging` (:405, false at F — the finger is
  on the slide), `velocityTracker` (:406, null when not
  dragging), `hubPressed`/`hubTarget` (:398–399).

What onDraw actually derives from θ (:480–753): the sweep arc
(:591–601, `sweepNormalized = (wheelAngleDeg − 108)/324`, drawn
in `tokPrimary()`) and the slider dot (:666–686, at
`wheelAngleDeg` on rimR, ink — `tokPrimary()` day /
`tokOnSurface()` night; **the wheel carries no orange** — D1
already moved it to ink, so Mara's E5b carrier list correctly
does not name the wheel). Everything else — socket, chamfer,
knurl, detent scale, numerals, hub wells — is θ-independent.

**The knurl, for the record (Mara's law, §3.3):** the teeth are
SCREEN-FIXED (:562–599) — drawn at absolute `toothDeg`, 3°
pitch, specular `0.16 + 0.66·t³ · toothFlare[tooth]` under the
one fixed lamp at canvas 220°. The rim does not rotate in the
shipped draw; only arc + dot travel. A frozen θ therefore
changes the knurl's light by exactly nothing, by construction.

---

## 2 — THE VERDICT ON ω: THE GHOST IS **CAUGHT**, NOT COASTED, NOT SEATED

Three candidates for what the integrator does at F:

- **Coast through the shot, freeze at Settle** — REJECTED, three
  counts. (1) *Truth:* `stepPhysics` calls
  `applyValueFromAngle` every frame (:1026) and fires
  `onMinutesChanged` on detent crossings — the dial would mutate
  *after* the session row was inserted from it; the sealed span
  and the screen's value would part ways mid-shot. (2) *Sound:*
  a capture during flight fires `fireSeatKnockForCount()`
  (:981) — an unscripted knock inside Orson's cadence and inside
  Wren's 170–760ms haptic-only train. (3) *Determinism:* Wren
  §3.1 requires both entries frame-identical from t=0 with
  sampled values; a coasting θ makes t0's picture depend on
  PROVING's duration, which is a network of service timings.
- **Seat it (snap to nearest detent at F)** — REJECTED. A snap
  teleports the dot up to 4.5° (half the 9° detent spacing) in
  one frame — a visible jump with no knock, i.e. a seat that
  never happened, printed. The shot's first frame must not lie
  about where the wheel was.
- **CAUGHT (ruled):** the integrator halts at F with θ exactly
  where the frame caught it — mid-flight, off-detent if that is
  where it was. An off-detent ghost angle is honest: it is where
  the hand's last act left the wheel, and the *value* (hero,
  session) comes from state, never from the ghost's angle
  (Wren A4's clause, adopted). No snap, no knock, no callback.

The halt machinery already exists: **`stopPhysics()`
(:918–925)** — sets `inPhysics = false`, removes the
Choreographer callback, nulls `physSpringTarget`, zeroes
`physOmega`, and — critically — does **not** touch
`wheelAngleDeg`. It is `private` today and already called from
`onDetachedFromWindow` (:1030–1033). My build wraps it, §3.2.

---

## 3 — THE GHOST SPEC

### 3.1 Composition mechanism — hoist, never duplicate (build-critical)

The `AndroidView` must be **one node, one call site**, composed
continuously from idle through the ghost's exit. If the builder
instead writes a second `AndroidView` inside a running/ghost
branch, Compose disposes the idle view and constructs a fresh
`RotaryWheelView` whose `wheelAngleDeg` initialises to
`minutesToAngle(50)` (:403) and is then snapped by the
`minutesValue` setter (:348–358) to the *detent* angle — θ_F,
ω, and the mid-flight catch are all destroyed, and §2's ruling
is unimplementable. **This is the single ordering the whole
joint stands on.**

Concretely, in `TactileDeckScreen.kt`:

1. Hoist the `AndroidView` (:274–295) out of the
   `if (!isSessionRunning)` branch to panel level. Compose
   condition becomes:
   `if (!isSessionRunning || shotPhase != ShotPhase.GONE)`
   where `shotPhase` derives from `SealShotState` (§3.5).
2. The FLOW/FINISHES row (:296–335) stays idle-only (it is a
   verb + a fact the hero owns; it does not ghost).
3. The running track branch (:337–365) composes only when the
   ghost has exited (§3.6) — during PROVING and the shot its
   slot is the ghost's.

### 3.2 The halt — `freezeAtSeal()` (new, `BlockitCoreViews.kt`, after :925)

```kotlin
/** E3 ghost: caught, not seated. Halts the integrator where the
 *  seal frame found it; severs every path back into state. */
fun freezeAtSeal() {
    stopPhysics()                    // :918 — θ untouched, ω := 0
    closureAnimator?.cancel()        // bar ink caught mid-settle
    closureAnimator = null
    velocityTracker?.recycle(); velocityTracker = null
    isDragging = false; hubPressed = false; hubTarget = null
    invalidate()
}
```
≈14 lines. `stopPhysics()` is idempotent, so a second call
(detach, abort path) is free. Note what is NOT here: no write
to `wheelAngleDeg`, no `applyValueFromAngle`, no knock.

### 3.3 Light — Mara's law, discharged point by point

The ghost does **nothing** with light, and the spec proves the
nothing:

- **Knurl specular** — screen-fixed teeth, lamp fixed at 220°
  (§1.3). θ is not an input to any tooth's alpha. Frozen or
  flying, the knurl cannot render the count, the seal, or
  elapsed time. Compliant by construction; no code.
- **Sweep arc** — a function of θ only (:591). Frozen θ ⇒ a
  frozen arc: the set value's *position*, held. It does not
  advance during PROVING or the shot, so it cannot render the
  seal's progress. The container's uniform α (0.97→0.50, §3.4)
  fades the whole view as one object — recession, not data.
- **Hub wells / closure bar** — the count's geometry. The one
  trap: `closureAnimator` completing 0.88→1.0 *after* F would
  render the count finishing *at* the seal. `freezeAtSeal`
  cancels it (§3.2); `barInkFraction` stays at its caught value
  for the ghost's whole life. A fractionally-inked bar on the
  ghost is honest — it is where the ink was caught.
- **D6 specular core (E5a), if it lands** — its offset is press
  depth × 2dp toward 135°, count never an input (Mara's own
  amendment). `freezeAtSeal` forces `hubPressed = false`, so
  press depth is 0 and the core sits at rest on the ghost.
- **Orange** — the wheel has no orange carrier (§1.2). Zero
  E5b work on this subject; stated so Mara's list stays closed.

### 3.4 What renders, F → exit (transforms are Wren's table, re-homed)

Container = the hoisted `AndroidView`'s modifier chain:
`graphicsLayer` (scale/translationY/alpha) + `blur` sampled from
the one `vertigoT` (Wren §3.2, derive-don't-replay). The view
inside never redraws differently — same `onDraw`, frozen state.

| Window | Wheel ghost renders |
|---|---|
| **F → t0 (PROVING)** | Composed, frozen at θ_F. Identity transform (scale 1.0, y+0, α 1.0, blur 0). Input dead (§3.5). `isNightMode` push = `shotNight` (below), not `effectiveNight`. |
| **t0 → t0+180 (Departure)** | → scale 0.985 · y+3dp · α 0.97 · blur 0.7dp |
| **t0+180 → t0+780 (Recession)** | → scale 0.83 · y+17dp · α 0.58 · blur 5.5dp |
| **t0+780 → t0+1050 (Settle)** | → scale 0.80 · y+20dp · α 0.50 · blur 6dp — the running ghost at its filed end-values |
| **t0+1050** | **Exit — CUT 0** (§3.6). |

**Night deferral (my share of Wren A3):** the wheel host's
`isNightMode` push (:281) changes from `effectiveNight` to the
shot's deferred flag — call it `shotNight`, owned by
`SealShotState`: false through Departure/early Recession, steps
once at **t0+400** with the panel ground. The wheel is a subject
standing *on* the ground; its paints (chamfer :525–529, dot
:679, ticks :635–636) stepping with the ground's one step is
the honest read — stepping at F (today's behaviour) would give
the shot two tone steps, and the first would be an accident of
flow timing. If A3 slips in the build, the fallback is today's
flip-at-F: degraded, not broken — the ghost still renders.

### 3.5 Input death + state severance (wheel's share)

Three independent layers, because each alone has a hole:

1. **View lock:** ghost `update{}` pushes
   `view.isLockedSession = true` (replacing the hardcoded
   `false` at :285) → `onTouchEvent` early-returns at :754.
   *Hole:* a false return on DOWN lets the parent scroll column
   (:174–194) take the gesture — the picture would scroll
   mid-shot under a "dead" wheel.
2. **Compose shield:** an overlay sibling in a `Box` wrapping
   the `AndroidView`, composed only in ghost state,
   `Modifier.matchParentSize().pointerInput(Unit) { … consume
   every change … }` — swallows drags, taps, and flings before
   either the view or the scroll column sees them. ≈10 lines.
3. **Callback severance:** ghost `update{}` pushes
   `onMinutesChanged = null; onCycleBlocksCount = null;
   onSetBlocksCount = null`, and pushes `minutesValue` /
   `studyBlocksCount` from the **seal snapshot** (§3.6 of the
   state shape, below) — not the live VM values. Even a stray
   invalidate or an impossible touch then has no path back into
   state. (The setters are safe under snapshot pushes: field ==
   pushed value, so the :352 snap and the :367 closure animator
   never re-fire during the session's per-second recompositions.)

### 3.6 The shared shape — `SealShotState` wheel fields (coordination)

Wren owns `SealShotState` (remembered in `TactileDeckScreen`,
keyed by `sessionId`, deriving from `t0Mono`). My fields,
captured **at frame F** — the same composition pass that first
renders `isSessionRunning`, by reading the view in the ghost
branch's first `update{}` *before* `freezeAtSeal()` runs
(order inside that lambda: snapshot → freeze → pushes):

```kotlin
data class WheelGhost(
    val thetaDeg: Float,        // wheelAngleDeg at F, unwrapped
    val omegaDegPerSec: Float,  // physOmega at F — recorded, never rendered
    val minutes: Int,           // minutesValue at F
    val blocks: Int,            // studyBlocksCount (n) at F
    val barInkFraction: Float,  // caught closure-ink, §3.3
)
```
Needs three `@VisibleForTesting`-grade internal getters on the
view (θ, ω, bar fraction) or the capture reads them via the new
`freezeAtSeal()` returning the snapshot — I prefer the latter:
**`fun freezeAtSeal(): WheelGhostSnapshot`** in the view file,
one return, no new public surface beyond the one function.
≈+8 lines over §3.2's count, included in the price.

Phases the Deck derives: `PROVING` (F, no t0 yet) →
`RUNNING` (t0..t0+1050, vertigoT live) → `GONE` (≥ t0+1050, or
session inactive). Zug-II's sled ghost hangs off the same
phases and the same F edge — letter sent (thread
`sealshotstate-shape`), proposing he nest a `SledGhost`
alongside `WheelGhost` under one `SealShotState`, and flagging
the one collision I can see: we share `shotNight` (:281 mine,
:494 his) — one flag, owned by the shot state, consumed by both
hosts; neither of us may step it privately.

### 3.7 The exit — at Settle's end, by CUT, into the running branch

**When:** the first frame with `elapsed ≥ t0+1050`. Not a fade:
`vertigoT` has ended, and an α tail would be a fifth, unfiled
motion competing with Wren's settle knock (16ms @ 0.7 at
t=1050). The exit is the same CUT 0 grammar as the utilities
row's entry at t=1050 (Wren A2) — one room change, one frame,
on the knock.

**Into what:** the panel's running branch — today's
progress track + finish/% row (:337–365), unchanged, takes the
slot in the same composition pass. The FLOW/FINISHES row does
not return (its finish fact already lives in the track row's
`finishPair` text, :354). The view instance is disposed;
`onDetachedFromWindow` calls the already-dead `stopPhysics()`
— a no-op. Nothing of the ghost persists in the view, because
nothing needs to: θ's truth (dial, n) lives in VM state, which
never moved after F.

**And the return, for free:** at the finish's E1200 CUT, the
idle Deck composes a *fresh* `RotaryWheelView`; its first
`update{}` pushes `minutesValue = dialMinutes`, whose setter
(:352) sets `wheelAngleDeg = minutesToAngle(dial)` — the wheel
is *found* seated at its detent, scale 1.0, defocused, exactly
as Wren §4.4 requires ("its angle and value never moved during
the session"). If the ghost was caught off-detent, the fresh
wheel sits on-detent: the detent is the value's truthful
position, and no observer can diff a frame from before a
multi-hour session against it. No code — a consequence of the
setter that already ships.

**Abort edge:** if the session goes inactive *during* the shot
(grace cancel ≤10s can land mid-shot), the ghost condition
fails and the whole shot aborts by CUT to the idle Deck — Wren:
emergency exits never enter the E-clock, the splice is 0ms and
sharp. `SealShotState` is keyed by `sessionId`; a dead session's
state is simply never read again. Process death mid-shot:
state is runtime-only; on relaunch the elapsed derivation puts
the phase at GONE — no ghost, no replay (Wren §3.2).

---

## 4 — ACCEPTANCE TESTS (the ghost, proven)

Harness: Robolectric + Compose test rule against
`TactileDeckScreen` with a fake VM state; captures via the Deck
bench's render harness. θ/ω read through the snapshot
`freezeAtSeal()` returns, stored on `SealShotState` — the tests
assert on state, not on screenshots of a Canvas, except T3.

- **T1 — Retention + identity.** Idle: `rotary_wheel_view` tag
  present; capture the hosted view instance. Drive state to
  running (session active, shot in PROVING). Assert: tag still
  present, **same view instance** (identity, not equality) —
  proves §3.1's hoist; a two-AndroidView build fails this.
- **T2 — Freeze.** Pre-flip, put the view in coast
  (test seam: dispatch a fling release, ω ≈ +900°/s). Flip.
  Assert at F, t0+500, t0+1049: snapshot θ identical to the
  last drawn θ_F to 0.01°, ω recorded at F > 100°/s (the flick
  was real) and the view's live ω == 0 thereafter;
  `onMinutesChanged` invocation count frozen at its F value
  after 2000ms of advanced clock.
- **T3 — Seal-frame capture (the render proof).** Bitmap
  capture at the flip frame: assert an ink pixel at the dot
  locus computed from θ_F — centre
  `(cx + cos(θ_F)·rimR, cy + sin(θ_F)·rimR)`, tolerance 1px —
  i.e. the ghost is *in* the seal-frame capture at its caught
  angle. Capture at t0+1049: ghost still present (tag + dot
  locus under the Settle transform). Capture one frame after
  t0+1050: tag **absent**, progress-track slot composed.
  **Present in the seal frame, gone after Settle — the two
  captures are the deliverable proof.**
- **T4 — Caught, not seated.** Flick so θ_F lands off-detent
  (|θ_F − nearestStopAngle(θ_F)| > 0.5°). Assert the ghost's θ
  == θ_F exactly — no snap (§2). And assert no seat knock in
  the sound/haptic fake's log between F and t0+1050.
- **T5 — No resurrection.** After exit (T3's third frame),
  advance 5000ms + force recompositions (per-second session
  ticks): tag never reappears while the session is active;
  after session end + E1200 cut, the fresh wheel's θ ==
  `minutesToAngle(dialMinutes)`.

---

## 5 — PRICE (my share of E3's ≈350)

Net production lines, against today's trunk:

| Piece | Site | Net |
|---|---|---|
| Branch restructure: hoist `AndroidView`, ghost condition, track deferred to exit | Deck :273–365 | ≈ +55 |
| `freezeAtSeal()` returning the snapshot (wraps `stopPhysics` :918) | Views, after :925 | ≈ +22 |
| Ghost `update{}` block: lock push, snapshot pushes, callback severance, `shotNight` | Deck :280–294 | ≈ +20 |
| Consume-all shield overlay (ghost state only) | Deck, wheel `Box` | ≈ +12 |
| `WheelGhost` fields + F-frame capture wiring in `SealShotState` | Deck / shot state | ≈ +18 |
| Wheel-container `graphicsLayer`/`blur` sampling from `vertigoT` (wheel's slice of Wren's transform wiring) | Deck, wheel modifier | ≈ +15 |
| **Subtotal, production** | | **≈ +142 net** |
| Tests T1–T5 | test source set | ≈ +70 |

**My share of E3: ≈142 net production + ≈70 tests** (call it
≈145 of the merged ≈350; the remainder is Wren's clock/hero/
task-line/ring/nav machinery and Zug-II's sled ghost, priced in
their files). No migration, no persistence, no new tokens, no
new easings (the Vertigo/blur curves are Wren's line in the
same joint).

## 6 — RISKS

1. **Two-AndroidView build (fatal, silent).** §3.1: if the
   builder duplicates the host instead of hoisting, everything
   compiles, the shot plays — and the ghost is a fresh wheel
   snapped to a detent, θ_F gone. T1's identity assertion is
   the only guard; it must land *with* the build, not after.
2. **Blur over the AndroidView (Wren's risk 1, inherited).**
   0.7→6dp live RenderEffect blur over a heavy Canvas view for
   1050ms may jank. Pre-named fallback stands: freeze the
   wheel's blur at its Departure value (0.7dp); scale/translate/
   alpha carry the Recession. The freeze/catch spec is
   untouched by the fallback.
3. **Snapshot ordering inside `update{}`.** Capture must read
   θ/ω *before* `freezeAtSeal()` zeroes ω (§3.6) — the function
   returning the snapshot makes the order structural; a builder
   who inlines it can invert it and record ω = 0 forever,
   failing T2 invisibly in production. Keep the single function.
4. **PROVING duration variance.** F→t0 can be ~1 frame (fast
   cascade) or ~1500ms (jam path to annulment). If the cascade
   annuls (pre-t0 deletion, D8/F7), the session never activates
   — but `isSessionRunning` may have flickered true on insert.
   The ghost then composes for a session that gets deleted:
   on the annulment edge the phase derivation (session inactive)
   cuts the ghost with everything else — correct, but the
   builder must derive phase from the *live* session flow, not
   latch it at F. Named so it is built that way.
5. **`shotNight` coupling.** Two hosts consume one flag (:281,
   :494). If Zug-II's sled build steps night privately, the two
   ghosts step on different frames and the ground reads torn.
   The flag lives in `SealShotState` or nowhere — letter sent.

— Rad, Improvers · the wheel
