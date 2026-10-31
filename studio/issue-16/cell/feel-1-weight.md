# Issue #16 — The Pill: WEIGHT

**Cell:** Pastille's pill cell · **Seat:** Feel Specialist-1 (Weight)
**Scope only:** drag, edge-snap, settle — how the floating 2-minute-peek
countdown feels in the hand. Not face copy, not colour, not lifetime.
**Sources read:** `PillController.kt`, `PillFace.kt`, `PillTheme.kt`
(all under `builds/pill/Focus-app/app/src/main/java/com/example/service/`).
**Rule honoured:** nothing compiled, nothing run. No new colour is proposed
anywhere in this document.

---

## 1. Verdict

### WEIGHTLESS.

The pill has exactly one motion in its entire body — a 140 ms fixed-duration
tween to the nearest edge — and that tween knows nothing about how the hand
let go. No velocity is ever measured, no spring exists, nothing overshoots,
nothing carries momentum. Drag is a raw 1:1 offset glued to the fingertip,
release is a teleport decision ("which half are you in?") followed by the
same short glide whether the user nudged the pill 10 dp or flicked it
across the screen. A flick and a nudge are indistinguishable to this code.
It does not feel like moving an object; it feels like moving a cursor.

Birth is also a teleport and death is an instant cut (see §2.4), so the
object never arrives or leaves — it just appears and vanishes.

---

## 2. What exists today — evidence

### 2.1 Drag: raw offsets, no physics, no bounds

`PillController.kt:333-342` (`DragListener.onTouch`, `ACTION_MOVE`):

```kotlin
val dx = (event.rawX - downRawX).toInt()
val dy = (event.rawY - downRawY).toInt()
if (kotlin.math.abs(dx) + kotlin.math.abs(dy) > dp(6)) moved = true
if (moved) {
    params.x = startX + dx
    params.y = startY + dy
    runCatching { windowManager.updateViewLayout(view, params) }
}
```

- 1:1 integer offset from the `ACTION_DOWN` point (`:324-331` stores
  `downRawX/Y`, `startX/Y`). No smoothing, no trailing, no lag — and, to be
  fair, no lag is wanted (see §3.1). The problem is not the drag itself,
  it is that the drag records **nothing**: no timestamps are kept, no
  `VelocityTracker` is ever created, `event.eventTime` is never read.
  The release therefore has no momentum to inherit. Weight is impossible
  downstream because the data for it is thrown away here.
- **No clamping at all.** `params.x` / `params.y` are written unbounded:
  the pill can be dragged fully off-screen left, right, top, or bottom
  mid-drag, and only the horizontal snap on release rescues X. Y is
  never rescued (see §2.3).
- Touch slop is Manhattan distance `|dx|+|dy| > dp(6)` at `:336` — fine,
  keep it. It is the one number in this listener that needs no change.

### 2.2 Snap: one fixed tween, default easing, X only

`PillController.kt:356-372` (`snapToEdge`) — the **only** animation in
the pill. A full-text search of `PillController.kt`, `PillFace.kt`, and
`PillTheme.kt` for animation / interpolator / velocity / spring /
overshoot finds nothing else: the sole import is
`android.animation.ValueAnimator` (`PillController.kt:3`), held in the
sole field `snapAnimator` (`:74`).

```kotlin
val target = if (params.x + view.width / 2 < screenWidth / 2) dp(6)
             else (screenWidth - view.width - dp(6)).coerceAtLeast(0)
val from = params.x
snapAnimator = ValueAnimator.ofInt(from, target).apply {
    duration = 140L
    addUpdateListener { animator ->
        params.x = animator.animatedValue as Int
        runCatching { windowManager.updateViewLayout(view, params) }
    }
    start()
}
```

What this means, property by property:

| Property | Reality |
|---|---|
| Spring | **None.** No `SpringAnimation`, no hand-rolled integrator, no stiffness/damping anywhere. |
| Duration | **Fixed 140 ms** (`:365`) regardless of distance. A 12 dp settle and a 500 dp cross-screen settle take the same time — the long one therefore moves ~4× faster, which reads as frantic, not heavy. |
| Easing | **Unset.** No `interpolator =` line exists, so `ValueAnimator`'s default `AccelerateDecelerateInterpolator` applies: symmetric ease-in/ease-out, zero overshoot, zero character. |
| Velocity handoff | **None.** Release velocity is never computed, so the tween always starts from rest. Every release visibly *stops* the pill for a beat, then glides it. That stop-then-glide is the single biggest weightless tell. |
| Target decision | Release centre vs screen midpoint (`:358`) only. Direction and speed of the throw are ignored — flick the pill hard right while its centre is still 1 dp left of the midpoint and it snaps **left, against the throw**. |
| Overshoot / settle | **None.** The value lands exactly on `target` and the animator ends. No arrival, no thunk, no micro-bounce. |
| Interruptibility | The one good habit present: `ACTION_DOWN` cancels the running snap (`:325`), and `removeWindow()` cancels it too (`:267-268`). Keep both. |

### 2.3 Y: dropped, never settled

Y is set during drag (`:339`) and then abandoned. There is no vertical
snap, no vertical clamp, and no bounds recovery — drag the pill above
the top of the screen or below the bottom and release it there (with X
snapped) and it stays stranded there until the next drag. An object with
mass rests somewhere legal; this one can rest half-off the glass.

### 2.4 Birth and death: teleport in, cut out

- **Birth:** the window is added at `x = 0` (`buildWindow`, params set at
  `PillController.kt:242-246`-equivalent gravity block, `x = 0`,
  `y = 22%` of height), then `root.post { … }` at `:257-262` jumps X
  instantly to the right-edge margin (`screenWidth − width − dp(10)`).
  No slide, no fade, no scale. The pill blinks into existence.
- **Death:** `removeWindow()` at `:266-280` cancels the animator and
  calls `removeView` immediately. No exit motion of any kind.

### 2.5 The other two files contribute no motion

- `PillFace.kt` is a pure derivation (sealed faces + `PillDerivation.derive`);
  it contains no rendering or motion code at all, by design.
- `PillTheme.kt` is palette + typeface data only. Relevant note for this
  seat: it loads **Anonymous Pro** for the pill's figures
  (`PillTheme.figures()`), which is the correct standing numbers font —
  this spec proposes **no font change**. (Sora / Atkinson Hyperlegible
  are the app's display/body faces; the pill's time figures staying in
  Anonymous Pro is right and is not a weight question.)

---

## 3. The finished feel — specification

**The metaphor, in one line:** a small dense puck — a brass token slid
across glass — not a balloon, not a bubble. Direct under the finger,
heavy the instant it leaves the finger: momentum carries it, one edge
claims it, it arrives with a single small overshoot and a stop you can
almost hear. Never bouncy. One overshoot, maximum, and a small one.

Implementation constraint that shapes everything below: the pill is a
plain `View` in a `WindowManager` overlay (`LinearLayout` + `TextView`s),
**not** Compose, so Compose `spring()` specs do not apply, and
`androidx.dynamicanimation` is **not** currently a dependency. Do not add
it for this. The spec below is a hand-rolled spring integrator driving
`layoutParams.x` through the same `updateViewLayout` call the code
already makes — zero new dependencies, zero new files.

### 3.1 While held: stay glued, add grip cues, bound the field

Keep the drag itself 1:1 — lag or trailing under the finger reads as
*slippery*, not heavy. Weight is added around the drag, not inside it:

1. **Pickup cue (new):** on the first `MOVE` that crosses the existing
   `dp(6)` slop, animate the root view `scaleX/scaleY 1.0 → 1.04` over
   **110 ms** (decelerate-out) and raise `view.elevation` by `dp(4)`.
   On release, scale returns `1.04 → 1.0` over **160 ms**. A thing you
   pick up gets slightly bigger in the hand; a cursor does not.
2. **Bounds with rubber band (new):** during `MOVE`, clamp the *legal*
   field to X ∈ `[dp(6), screenW − width − dp(6)]`,
   Y ∈ `[dp(24), screenH − height − dp(24)]` (24 dp keeps it clear of
   the status bar and the gesture/nav zone). Allow overdrag past the X
   edges and the Y bounds at **0.35× resistance** (finger moves 10 dp,
   pill moves 3.5 dp), hard-capped at **18 dp** of overdrag. Resistance
   at the wall is felt mass; today the wall does not exist.
3. **Start measuring (new):** add a `VelocityTracker`: obtain on
   `ACTION_DOWN`, `addMovement` on every event, recycle on
   `UP`/`CANCEL`. Compute with a 100 ms window, read X velocity in
   px/s, convert to dp/s for the thresholds in §3.2. This is the entire
   enabling change — without it nothing else in §3 exists.

### 3.2 On release: flick and nudge are different animals

Decide the target from **where the pill is heading**, not only where
it was dropped:

```
projectedCentreX = releaseCentreX + vxDpPerSec * 0.18   // 180 ms projection
target = LEFT  if projectedCentreX < screenW/2 else RIGHT
```

| | **NUDGE** | **FLICK** |
|---|---|---|
| Definition | `\|vx\| < 400 dp/s` | `\|vx\| ≥ 400 dp/s` |
| Target edge | Projected centre (≈ release centre — projection is tiny at this speed, so today's midpoint rule survives in practice) | Projected centre — a hard throw released left of the midpoint still lands **right**. The throw outranks the drop point. |
| Spring stiffness `k` | **500** | **300** |
| Damping ratio `ζ` | **0.88** | **0.72** |
| Spring damping `c = 2ζ√k` | ≈ 39.4 | ≈ 24.9 |
| Initial spring velocity | `vx × 0.6` (damped handoff — a placed puck must not jump) | `vx × 1.0`, clamped to **±2 500 dp/s** (full handoff — the hand's speed *is* the pill's speed; no stop-then-glide beat) |
| Expected settle time | **240–360 ms** | **450–650 ms** |
| Overshoot | ≤ 2 dp — effectively invisible; it just stops *firmly* | 3–5 % of travel distance, bumper-clamped per §3.3 — one visible arrival, never a second bounce worth naming |
| Feels like | Placing a token down and sliding it home the last centimetre | Throwing the token across the glass and watching the far edge catch it |

Why two parameter sets and not one: one spring for both is exactly
today's mistake in a better costume. A stiff, near-critically-damped
spring on a flick feels like the pill is on a leash; a loose spring on
a nudge feels like jelly for a 15 dp trip. The 400 dp/s split is the
only branch needed.

`ACTION_CANCEL` is always a **nudge** (velocity discarded): the system
took the gesture away, the pill should park, not fly. Today's code
already routes `CANCEL` to `snapToEdge` rather than the tap
(`PillController.kt:344-349`) — preserve that routing exactly.

### 3.3 The spring itself

Replace the body of `snapToEdge` (`:356-372`) with a
`Choreographer`-posted frame callback running semi-implicit Euler on
float state, writing `(x)` back as `params.x = x.roundToInt()`:

```
a  = −k · (x − target) − c · v          // per second², dp units
v += a · dt ;  x += v · dt              // dt = frame dt, capped at 32 ms
done when |x − target| < 0.5 dp AND |v| < 15 dp/s → x = target exactly, stop
```

- **Bumper clamp on the rendered value only:** clamp the *written*
  `params.x` to `[dp(2), screenW − width − dp(2)]` while the integrator
  state runs unclamped. On a long flick the pill therefore touches the
  2 dp bumper for a frame or two, then eases back out to rest at the
  6 dp margin — a thunk against a felt stop, not a clip through the
  screen edge and not a cartoon bounce.
- **Frame cost:** one `updateViewLayout` per vsync for ≤ ~650 ms is
  the same cost class as today's 140 ms tween already paying it; this
  is not a new performance category. The spring must cancel on
  `ACTION_DOWN` and in `removeWindow()`, exactly where `snapAnimator`
  is cancelled today (`:325`, `:267`).
- **Y on release:** Y never edge-snaps (vertical placement is the
  user's and the current design keeps it). If release Y is inside the
  §3.1 bounds, Y does not move at all. If it is in rubber-band overdrag,
  tween Y back to the bound over **160 ms**, decelerate-out, alongside
  the X spring. No other Y motion, ever.

### 3.4 Birth and death (secondary, but part of weight)

An object that teleports in cannot feel heavy while dragged; the
entrance betrays it.

- **Birth:** replace the instant `root.post` jump (`:257-262`) with:
  start at `x = screenW − dp(8)` (tucked 2 dp further toward the edge
  than its rest margin, `alpha = 0`), then run the §3.3 spring
  (nudge parameters, `k = 500, ζ = 0.88`) to the right-edge rest X
  while alpha animates `0 → 1` over **160 ms**. Total arrival
  ≈ **240 ms**. It slides out of the edge it lives on.
- **Death:** keep it instant. Deliberately. A pill whose lease died or
  whose service is being destroyed must not linger for an exit
  animation — a fading pill would keep printing a fuse that is no
  longer true. Truth outranks feel here; `removeWindow()` stays a cut.

### 3.5 Explicitly NOT in this spec

- No haptics in v1. A single tick on settle is tempting, but an
  overlay service firing haptics on every reposition will grate
  within a day; revisit only after the spring is lived with.
  (Priced below as a deferred option, not a recommendation.)
- No rotation, no tilt, no squash. A countdown is an instrument;
  instruments do not wobble.
- No position persistence — unchanged from the existing residency-scoped
  decision documented at `PillController.kt:309-313`.
- No colour, palette, or typeface change. `PillTheme.kt` is untouched
  by this spec.

---

## 4. Price

All work is inside `PillController.kt` unless stated. No new
dependencies, no schema change, `PillFace.kt` untouched.

| # | Item | Lines |
|---|---|---|
| 1 | `VelocityTracker` lifecycle (obtain / feed / compute / recycle) + velocity fields | ~18 |
| 2 | Spring integrator + `Choreographer` frame loop replacing `snapToEdge`'s `ValueAnimator` body (net: ~45 written, ~17 removed) | **net ~+28** |
| 3 | Projected-landing target decision + flick/nudge branch + parameter selection | ~16 |
| 4 | Drag bounds: legal-field clamp + 0.35× rubber-band overdrag (X and Y) | ~20 |
| 5 | Pickup/drop scale + elevation cue | ~12 |
| 6 | Y out-of-bounds recovery tween (rides the same frame loop) | ~8 |
| 7 | Birth slide-in replacing the `root.post` teleport | ~14 |
| 8 | Settle-exact finish, bumper clamp, cancel wiring (down + `removeWindow`) | ~10 |
| | **Total** | **≈ +105 lines, one file** |
| — | *Recommended companion:* extract the target decision + one spring step as pure functions (~30 lines of the above reshaped) so flick-vs-nudge and settle are unit-testable; 3 tests | ~60 test lines |
| — | *Deferred option, not recommended for v1:* one haptic tick on settle | ~6 |

For scale: today's entire motion implementation is the 17 lines of
`snapToEdge`. Finished weight costs roughly six times that, all of it
in the controller, none of it in the derivation or the theme.

---

## 5. Acceptance — how Pastille judges it on the phone

1. **The stop test:** drag slowly, release mid-screen. Current build:
   the pill pauses, then glides 140 ms. Fixed build: motion is
   continuous through the release — no dead beat.
2. **The throw test:** flick hard right from the left third. Current
   build: snaps left, against the throw, in 140 ms. Fixed build:
   crosses the screen, touches the right bumper, settles once at the
   6 dp margin in about half a second.
3. **The nudge test:** move the pill 20 dp and let go. Fixed build:
   home in under ~360 ms with no visible bounce. If it jellies, `ζ`
   is too low; if it slams, `k` is too high.
4. **The wall test:** drag past any edge. Fixed build: resistance,
   max 18 dp of give, and on release it recovers inside the field —
   including vertically, where today it can be stranded forever.
5. **The arrival test:** start a peek from cold. Fixed build: the pill
   slides out of the right edge and fades in; it never pops.

— Feel-1 · Weight (issue-16 cell)
