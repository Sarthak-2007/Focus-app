# SLICE E — NABE (Improvers, the hub): E5a — THE D6 HUB LIGHT

**Seat:** Nabe, Improvers — I own the hub (tetrad closure, press law,
centre geometry; `plan/improvers/03-nabe-hub.md`).
**Joint:** E5a only — the D6 hub specular core + the restored press-dip
that gives it travel. E5b (Mara's orange exit, ≈50) is Mara's file and
is NOT re-priced or re-specced here. Split of E5 as filed
(SLICE-D-E-BUILD-READY §E5): (a) ≈80 + (b) ≈50 = ≈130.
**Fork:** F21 — **RULED by Ren, Oct 29 09:05** (RULINGS.md; Ren's letter
`mail/marlowe/read/0905__from-ren__slice-de-fork-rulings.md`, verbatim:
*"F21 ADOPT: restore the press-dip (PRESS 45ms, 1dp; commit-on-release
untouched) so D6's core and its roll exist."*). This file builds the
adopted branch only. Rest-core-only and kill-D6 are dead forks; the
perceptibility gate in §6 is the only surviving kill switch, and it
kills the travel, never the core.
**Part audited:** `plan/slice-de-parts/mara.md` Joint (a) — Mara's law,
Mara's numbers. Every number below was re-verified this run against
REGISTER.md, DESIGNERS-PLAN §1.4/§3.2, designers-parts/uhr.md §8,
designers-parts/kin.md M-04, MASTER-PLAN §2.4, and today's trunk.
**Method:** trunk read-only, no gradle run. Inbox at start: no unread
letters (3 in `read/` — Rad + Dario on hub-centre-geometry, Ilya's
phase-2 challenges; none touches E5a's numbers).

---

## 0 — VERDICT

**BUILDS as amended by Mara, at ≈85 net production lines (+5 over the
filed ≈80 — the dual-mode gradient and the dedicated-paint discipline
in §3 were underpriced, itemised in §8) + ≈95 test lines.**
No number in the brief moved. Three filed-source divergences exist and
are resolved in §2 so the builder never has to adjudicate them at the
keyboard: Kin's scale and PRESS-BACK return are superseded, and §3.2's
"+1 tonal step" has no day step to take.

---

## 1 — TODAY'S TRUNK, EXACT SITES (verified this run)

File: `Focus-app/app/src/main/java/com/example/ui/views/BlockitCoreViews.kt`
— **1,419 lines today, identical count to Mara's audit.** Class
`RotaryWheelView` opens :346.

### 1.1 The core is not drawn — Mara's claim re-verified

Hub draw, `onDraw` :480, hub section :686–~750:

| Lines | What draws today |
|---|---|
| :686–687 | section comment ("Inner Raised Hub") |
| :688–689 | contact shadow — `0x26000000`, circle at `cy + dp(2f)`, r = `innerHubR` |
| :690–691 | flat cap fill — `paintFill.color = tokSurfaceBright()` (:130 resolver; day `#FAF6F0` / night `#362C27`), circle at `cy` |
| :692–694 | 1dp hairline circle, `tokHairline()` |
| :695–750 | tetrad comment, guide ring (arcs), closure bars, 8 wells — end of `onDraw` (function closes after the wells loop; `onTouchEvent` opens :753) |

Grep over the whole file: **no `SweepGradient`, no specular overlay, no
gradient of any kind on the hub.** The only specular in the view is the
rim's point-sampled form in the knurl loop (:552 comment, :571–582).
D6 is a from-zero draw, exactly as Mara filed. §1.4 territory ("wheel
crown/hub", DESIGNERS-PLAN §1.4) is granted and unspent.

### 1.2 The press is a boolean — the insertion site

- Fields: `hubPressed: Boolean` :398, `hubTarget: Int?` :399, beside
  `barInkFraction` :400 and `closureAnimator: ValueAnimator?` :401.
  **There is no depth field, no hub animator, and `RotaryWheelView` has
  no `onSizeChanged` override anywhere in the file** (grep: zero hits).
- DOWN: `onTouchEvent` :753; hub branch :767 (`dist <= innerHubR`,
  `innerHubR` recomputed :761 as `min(w,h) × 0.45 × 0.494` — same value
  as draw's :490). Sets `hubPressed = true` :774, `hubTarget = null`
  :775, target resolution :776–798 (well direct-set / bar-set, gated
  `minutesValue >= 10`; under FLOW the press registers nothing). **No
  `invalidate()`, no visual change on DOWN** — Mara verified, I
  re-verified: the branch returns `true` at :800 having drawn nothing.
- MOVE: :803–810 — acts only `if (isDragging)`. A hub press in MOVE is
  a no-op today; there is no crossing test to amend, it is added whole.
- UP/CANCEL: :815–838 (branch opens :815). Commit test :818 —
  `ACTION_UP && dist <= innerHubR × 1.1f`; fields cleared :820–821;
  commit fires feel + `onSetBlocksCount`/`onCycleBlocksCount`
  :828–837. Count commits via the `studyBlocksCount` setter :360–382
  (coerce 1..8; closure settle at :369–378, `duration = 160` — the
  160ms precedent Mara's SEAT return borrows, still at :371 today).

### 1.3 Curves — neither PRESS nor SEAT exists

`ui/theme/MonolithCurves.kt` (48 lines): only `CurveA` :25, `CurveB`
:26, `CurveC` :27 — `PathInterpolator(0.2,0,0,1)` / `(0.05,0.7,0.1,1)` /
`(0.3,0,0.8,0.15)`. Its duration vocabulary is `{180,260,280,420,520}`
(:18–22) — 45ms and 160ms are both outside it, which is correct: they
are element timings (Kin M-04; closure settle), passed as animator
durations, never added to the shared vocabulary. Mara's cited
insertion site `MonolithCurves.kt:25-27` verifies exactly.

---

## 2 — NUMBER VERIFICATION (against REGISTER.md + Mara + Uhr §8)

| # | Value | Brief | REGISTER.md | Uhr §8 / DESIGNERS §1.4 | Mara (a) | Status |
|---|---|---|---|---|---|---|
| 1 | Core azimuth | 315° | hub core: "azimuth 315°" (Specular, registered separately) | §8: 315°, "upper-left, the 10:30 position" · §1.4: 315° | 315° | **HELD** |
| 2 | Plateau | ±9° | "core ±9°" | §8: full-strength ±9° (18° wide) · §1.4: ±9° | ±9°, `k=1` | **HELD** |
| 3 | Falloff | cos² to ±34° | "cos² falloff to zero at ±34°" | §8: `k = cos²((π/2)·(|Δ|−9°)/25°)`, 9<|Δ|<34, 0 beyond; footprint 68° | identical formula | **HELD** |
| 4 | Peak lift | 10% day / 8% night | hub core: "lift **10% day / 8% night**" — *registered separately on purpose* | §8: k×10% day, k×8% night | 10% / 8% | **HELD — see trap T1** |
| 5 | Specular tint | (via §1.4/Uhr) | — | §8: day `#FFFFFF`, night `#EDE3D8` | day `#FFFFFF` / night `#EDE3D8` | **HELD** |
| 6 | Cap fill under core | — | day raised `#FAF6F0`, night raised `#362C27` | — | same, via `tokSurfaceBright()` :130 | **HELD** (trunk :90/:112 constants agree) |
| 7 | Core travel | depth × 2dp toward 135° | — (Mara's bounded-linear entry is a different surface's form — do not conflate) | — | A2(ii): depth × 2dp toward 135°, canvas (+0.707,+0.707) | **HELD** — MASTER §2.4: "the core translates 2dp toward 135° over the press travel" |
| 8 | Dip depth | 1dp | "Hub count-commit: on release" entry presumes the dip | §3.2: "DOWN = +1 tonal step + 1dp depress" | A2: fraction of filed 1dp | **HELD** — Ren F21 letter: "PRESS 45ms, 1dp" |
| 9 | Dip duration/curve | PRESS 45ms | Curve registry: PRESS (0.20, 0, 0.30, 1.00) | Kin M-04: PRESS, 45ms to bottom of dip | A2: 45ms, `cubic-bezier(0.20,0.00,0.30,1.00)` | **HELD** |
| 10 | Return | SEAT 160ms from commit frame | Curve registry: SEAT (0.22, 0.90, 0.28, 1.00), **no duration registered** | §1.5: SEAT replaces CurveC, arrival test ≥97% by 85% | A3: SEAT, 160ms (= closure ink-settle, trunk :371) | **HELD as Mara/Build-Ready adoption — see divergence D2** |
| 11 | Commit timing | on release, untouched | "Hub count-commit: on release (§2.4 / DESIGNERS §3.2). Kin M-04's commit-at-the-bottom-of-the-dip is superseded" | §3.2: commits on release | §2/A3: untouched | **HELD** |
| 12 | Night ceiling check | — | "non-accent fills never lighter than `#4A423C` at night" (a cap on lightness) | §8 night peak 8% | Mara: mix lands ≈`#443B35`, under `#4A423C` | **HELD within rounding — see note N1** |
| 13 | Gradient builds | one, in `onSizeChanged` | — | — | A1: once in `onSizeChanged`, reused | **AMENDED in form, §3.2 — two gradients (day+night), both built in `onSizeChanged`** |

**Trap T1 — the 14% tooth peak must not leak onto the hub.** REGISTER's
general specular caps read "peak lift ≤14% day / ≤8% night over cap
fills" and Mara's rim form is `0.16 + 0.66·t³`. The hub core's 10% day
is a *different, separately registered constant*; the Register says so
in words ("both are registered so no builder spends one on both
surfaces"). A builder who "harmonises" the hub to 14% has broken the
registration. The acceptance test in §7.1 pins 10%/8% numerically.

**Note N1 — Mara's night-mix arithmetic is off by one in R.** Recomputed:
`mix(#362C27, #EDE3D8, 0.08)` = R 0.92·54+0.08·237 = 68.6 → `#45`,
G 58.6 → `#3B`, B 53.2 → `#35` ⇒ **`#453B35`**, not Mara's `#443B35`.
Both sit under the `#4A423C` ceiling; nothing moves. Recorded so the
test tolerance (§7.1) is written against the recomputed value.

### Divergences the builder must NOT adjudicate (ruled here)

- **D1 — Kin's scale is NOT restored.** Kin M-04 as filed also carries
  scale 1→0.94 and shadow-to-0dp. Neither MASTER §2.4 (the merged hub
  law) nor Ren's F21 letter ("PRESS 45ms, 1dp") carries the scale.
  Build = translate 1dp + shadow-gap closure only (Mara A2(i)). The
  scale stays dead; do not revive it as "part of M-04".
- **D2 — the return is SEAT 160ms, NOT Kin's PRESS-BACK.** Kin M-04
  filed the return as PRESS-BACK spring (800 · 0.65, settled ≤190ms).
  That filing belongs to the same superseded M-04 form whose commit
  timing the Register already strikes (C-12 pattern). Mara's amendment
  — SEAT curve, 160ms, the hub's own shipped settle duration (trunk
  :371) — is the form SLICE-D-E-BUILD-READY §E5(a) adopted and F21's
  adoption of "D6's core and its roll" carries. If a builder finds
  PRESS-BACK in kin.md and "corrects" to it, the return overshoots
  (a spring past rest = light moving after arrival = a second event)
  and §7.3 fails. PRESS-BACK remains the *key-cap* return elsewhere;
  it is not this joint's.
- **D3 — §3.2's "+1 tonal step" is NOT built.** Mara A2, final bullet:
  the day hub already sits at the staircase top (`#FAF6F0` raised);
  no step above exists to take, and MASTER §2.4's restatement of the
  tonal step is stale on this point. No fill change at any depth.
  Depth + shadow-gap + core travel carry the press. (An omission by
  ruling, printed here so it is never filed as a miss.)

---

## 3 — THE CORE (A1, build-exact)

### 3.1 Where it draws

In `RotaryWheelView.onDraw`, hub section: **after the cap fill (:691),
before the hairline (:692)** — light on the cap's surface, under the
guide ring, closure bars and wells (all drawn :695–750, after it). The
core is drawn in **every** hub state — counts 1–8 and FLOW. In FLOW it
simply never leaves rest (§4.4); the cap is still a cap in the same
room. It is clipped by geometry, not by a clip path: the draw is a
`drawCircle(cx, cy, innerHubR, paintSpecular)` — the circle's own edge
is the clip. No `save/clipPath/restore` is spent on the core itself.

### 3.2 The gradient — two, built once, angle convention nailed

Mara filed "one SweepGradient built once in `onSizeChanged`". Form
amendment, priced in §8: **two** gradients — day and night — both built
in the new `onSizeChanged`, selected at draw by `isNightMode`. Reason:
`isNightMode` is pushed live by the host
(`TactileDeckScreen.kt:281/:494`, `update{}`), so a single gradient
built with day stops would draw a day core (white @ 10%) all night.
Rebuilding on the mode setter instead would put shader construction
on a Compose-driven setter path; two prebuilt gradients keep every
hot path allocation-free, which is this file's own discipline
(zero-allocation header, BlockitCoreViews.kt:52–60).

**Angle convention — the builder trap this section exists to kill.**
Uhr's azimuth is a *bearing*: 0° = north, clockwise — the same
convention the wells already use (`wx = cx + sin(az)·R`,
`wy = cy − cos(az)·R`, :743–745). 315° bearing = upper-left ✓.
`SweepGradient`'s native angle is 0° = east (3 o'clock), clockwise.
Conversion, used everywhere below:

```
gradientAngle(bearing) = (bearing − 90°) mod 360°
core centre: bearing 315° → gradient angle 225°
offset dir:  bearing 135° → unit vector (sin135°, −cos135°) = (+0.7071, +0.7071) in canvas px
```

**Construction (in `onSizeChanged`, once the view has a size — the
stops themselves are size-independent; `onSizeChanged` is the filed
construction site and the first moment construction is lawful):**

```
for mode in {day, night}:
  tint  = day ? 0xFFFFFFFF : 0xFFEDE3D8
  peak  = day ? 0.10f : 0.08f
  for i in 0..120:                       // 3° steps, full circle + wrap stop
    g = i * 3°                           // gradient angle of this stop
    φ = (g + 90°) mod 360°               // back to bearing
    Δ = angularDistance(φ, 315°)         // ∈ [0°, 180°]
    k = Δ ≤ 9°  → 1
        Δ < 34° → cos²( (π/2) · (Δ − 9°) / 25° )
        else    → 0
    colors[i]    = (round(255 · k · peak) shl 24) or (tint and 0x00FFFFFF)
    positions[i] = i / 120f
  gradient[mode] = SweepGradient(0f, 0f, colors, positions)
```

- Sampling at ≤3° is Mara's filed bound; 121 stops is its exact cost.
  The plateau and the cos² are *sampled from the formula*, never
  hand-tuned stops.
- **Seam:** gradient angle 0° (position 0.0/1.0) = bearing 90°, which
  sits inside the 292° zero zone (bearings 34°→281° going forward);
  stops 0 and 120 are both alpha 0. The wrap seam is swallowed by
  arithmetic, as Mara required — verified by construction here, not
  left to care.
- **Composite correctness:** each stop's colour is the tint at alpha
  `k·peak`; drawn over the cap fill this yields exactly Uhr's
  `mix(capTone, tint, k·peak)` — the same law the rim samples per
  tooth, in overlay form. Peak alpha: day 0x1A (26), night 0x14 (20).
- **Per-draw placement:** the gradient is built centred at (0,0). At
  draw, a pre-allocated `Matrix` is set to
  `setTranslate(cx + offX, cy + offY)` (§5) and passed via
  `shader.setLocalMatrix(...)`. No shader is constructed, and no
  colour array is allocated, in `onDraw`.

### 3.3 The dedicated paint

A new `private val paintSpecular = Paint(Paint.ANTI_ALIAS_FLAG)` with
`style = FILL`, living beside the class's paints. **Never reuse
`paintFill` for the core:** `paintFill` is shared by every section of
this `onDraw`; a shader left set on it would tint the wells, and a
shader cleared late would be a ordering bug waiting for a reordering.
One paint, one shader assignment per draw (day or night gradient),
zero leakage. This is +2 of the +5 re-price.

---

## 4 — THE PRESS DEPTH (A2+A3, build-exact)

### 4.1 The state — names, sites, type

Trunk idiom governs: this is a View class animated by `ValueAnimator`
(the closure settle, :369–378), **not** Compose — there is no
`Animatable` in this file's world and none is imported. Insert beside
:398–401:

```kotlin
private var hubPressDepth: Float = 0f            // 0 = at rest, 1 = full filed 1dp
private var hubPressAnimator: ValueAnimator? = null
private var hubPointerInside: Boolean = true     // ×1.1 crossing state, MOVE only
```

Beside them, two interpolators in `MonolithCurves.kt` after :27:

```kotlin
val Press: PathInterpolator by lazy { PathInterpolator(0.20f, 0.00f, 0.30f, 1.00f) }
val Seat:  PathInterpolator by lazy { PathInterpolator(0.22f, 0.90f, 0.28f, 1.00f) }
```

(Control points verbatim from the REGISTER curve registry: PRESS
(0.20, 0, 0.30, 1.00) · SEAT (0.22, 0.90, 0.28, 1.00). Naming follows
the registry's caps, not CurveA/B/C's retired scheme — §1.5 of
DESIGNERS-PLAN already speaks of PRESS and SEAT as the app's curves.)

Two private helpers on `RotaryWheelView`:

```kotlin
private fun startHubDip()    // cancel animator; ofFloat(depth, 1f); duration 45; interpolator Press; update → depth + invalidate(); start
private fun startHubReturn() // no-op if an animator is already running toward 0; else cancel; ofFloat(depth, 0f); duration 160; interpolator Seat; update → depth + invalidate(); start
```

Both animate **from current depth** — a re-dip or return never snaps
to an endpoint first. Arrival per the filed arrival law: first frame
within max(1% of travel, 0.5dp) — with 1dp of travel the 0.5dp term
binds on the dip; the animator still runs its full filed duration
(the law governs when arrival may be *declared*, not a licence to
truncate the curve).

### 4.2 Who consumes depth — the closed list, with the proof

`hubPressDepth` has exactly **two** readers, both inside the hub
section of `onDraw`, and exactly **one** writer (the animator update
listeners in §4.1):

1. **The hub-group translate:** `depth × dp(1f)` in +y (§4.3).
2. **The core's shader offset:** `depth × dp(2f)` toward bearing 135°
(§5).

Nothing else reads it — and the builder proves it, mechanically, as
part of the build: after implementation, `grep -n "hubPressDepth"` on
the file must return exactly: the field declaration, the two update
listeners, the two `onDraw` reads, and the test accessor (§7). Any
sixth production read is a second rendering of the press and reverts.
In particular: **not** the wells, **not** the bars, **not**
`barInkFraction`, **not** `loadInertia()` (:445–446 — count's mass is
a different law and depth must never enter it), **not** any haptic
(the commit feel at :833–836 is unchanged, fired by the commit, not
by depth).

### 4.3 The draw effect

Hub section restructure, exact order:

1. :688–689 contact shadow — **drawn untranslated, exactly as today.**
   It stays at `cy + dp(2f)`; as the group seats down 1dp the shadow
   gap closes 2dp → 1dp. That closing gap *is* Kin's "contact shadow
   collapses", at Canvas scale (D1: shadow does not go to 0 — the
   filed 0dp belonged to the superseded scale form; the gap closing
   by the full travel is the honest residue).
2. `canvas.save()`; `canvas.translate(0f, hubPressDepth * dp(1f))`.
3. Cap fill :690–691, **core (§3)**, hairline :692–694, guide ring,
   closure bars, wells — all as today, all inside the translate.
4. `canvas.restore()` after the wells loop (end of `onDraw`).

At rest depth = 0 and the translate is 0 — the rest frame is
pixel-identical to today's frame **plus the core**. No fill change at
any depth (D3).

### 4.4 Touch wiring — the three branches

- **DOWN (:767 branch):** after `hubPressed = true` (:774) and target
  resolution, **iff `minutesValue >= 10`** call `startHubDip()` and
  set `hubPointerInside = true`. Under FLOW nothing starts: MASTER
  §2.4 — "under FLOW the hub visibly rests (press disabled)". A dip
  with no possible commit would be a tease-motion (Kin's never-animate
  family); FLOW's core stays at rest because its cap stays at rest.
- **MOVE (:803 branch, new sub-branch before/alongside `isDragging`):**
  `if (hubPressed && minutesValue >= 10)`: compute
  `inside = dist <= innerHubR × 1.1f` (the :818 commit radius, same
  constant, no second radius is minted). Edge-triggered on
  `hubPointerInside`: inside→outside calls `startHubReturn()`;
  outside→inside calls `startHubDip()`. MOVE never touches
  `hubTarget`, never commits, never cancels the press — the shipped
  commit rule (UP inside ×1.1 commits, whenever the crossings
  happened) is untouched.
- **UP/CANCEL (:815 branch):** reorder the head of the branch to —
  capture `commit` (:818) and `target` (:819) → clear fields
  (:820–821) → **`startHubReturn()`** → then the existing commit body
  (:828–837) verbatim. Return-first ordering is what makes A3's
  start-frame identity robust: the return is started on the commit
  frame *by construction in this branch*, before any callback can
  re-enter the view (the host's `update{}` pushes the new count into
  the setter, whose `invalidate()` :380 lands on the same frame).
  On CANCEL and on drag-off UP, the same `startHubReturn()` runs —
  one return law for every way a press ends. `startHubReturn()`'s
  already-returning no-op (§4.1) means a MOVE-crossing return is
  never restarted by the UP that follows it.

**Sub-45ms taps:** a release before the dip completes still commits
(shipped law — release commits regardless), and the return starts
from whatever depth the dip had reached. The light stays a pure
function of position even then. **The dip never gates the commit** —
no ARM cost, Mara's clause holds.

**Held press:** depth sits at 1, core at full 2dp offset,
indefinitely, motionless. Hold duration is not an input to anything.

---

## 5 — THE CORE OFFSET (the roll)

```
offMag = hubPressDepth × dp(2f) × CORE_TRAVEL_GATE      // §6; gate ∈ {0, 1}
offX   = offMag × sin(135°)  = offMag × 0.7071f
offY   = offMag × −cos(135°) = offMag × 0.7071f
shaderMatrix.setTranslate(cx + offX, cy + offY)          // pre-allocated Matrix field
paintSpecular.shader = gradient[isNightMode]; shader.setLocalMatrix(shaderMatrix)
canvas.drawCircle(cx, cy, innerHubR, paintSpecular)      // inside the §4.3 translate
```

Two motions compose, and they are different motions: the §4.3 group
translate carries the *cap* (and the core with it) 1dp down; the
shader matrix carries the *light* a further 2dp toward 135° relative
to the cap. Total core displacement from rest at full depth = 1dp
down + 2dp toward 135°. Offset is a function of depression in dp and
of nothing else: same depression ⇒ same offset, at any count, any
hold duration, any approach speed (Mara A2(ii), verbatim law). The
animator moves the cap; the light reads the cap.

---

## 6 — THE PERCEPTIBILITY GATE (build constant, criterion named)

Mara A5, made build-proof. The gate is a single constant in
`RotaryWheelView`'s companion (or file-level, beside the view — one
site, grep-able):

```kotlin
/** E5a device-pass gate (Nabe §6): 1 = core travel ships, 0 = travel dead, rest core ships alone.
 *  Flipped ONLY on the DEVICE_PASS_E5A result below — never on a render, a screenshot, or taste. */
const val CORE_TRAVEL_GATE = 1
```

- **Shipped value: 1** (travel built and on). The pass below is run
  on the built APK; a fail flips this one constant to 0 and rebuilds.
  The builder never improvises a third state, a dimmer travel, or a
  larger offset "to help it show" — 2dp and 10%/8% are registered
  numbers, and a travel that needs rescuing is a travel that failed.
- **What the gate kills:** only the §5 offset term (`offMag` → 0).
  The dip (§4), the shadow-gap closure, and the rest core (§3) are
  NOT gated — the dip is F21's ruling independent of the light, and
  the rest core is §1.4 territory finally spent (Mara A5).
- **The criterion — DEVICE_PASS_E5A, named in full so no one
  re-invents it:** on a physical device (never Robolectric, never an
  emulator render), production Deck, hub at arm's length (~40cm),
  in day mode AND in night mode separately: the tester presses and
  *holds* the hub (depth reaches 1, core at full offset) and
  releases, 3 times per mode. **PASS = the core's shift is seen,
  unprompted, on at least 2 of 3 holds in BOTH modes.** Any other
  result is a FAIL for that build. The pass/fail and the mode that
  failed are recorded in BUILD-CHANGES with the constant's flip if
  it happens — name-by-name record discipline, one line.

---

## 7 — ACCEPTANCE TESTS

New file: `app/src/test/java/com/example/HubSpecularTest.kt`
(Robolectric, `@GraphicsMode(NATIVE)`, modelled on the shipped
`TetradRenderTest.kt` harness: density 3.0, view measured/laid at
864×864 = 288dp, `minutesValue = 50` unless stated). Test-only
accessors: the build adds two `internal` getters on `RotaryWheelView`
— `hubPressDepthForTest` and `hubPressAnimatorForTest` — counted in
the §8 price; they are the *only* sanctioned extra readers of depth
(§4.2's grep proof exempts them by name).

Sampling geometry for the pixel tests (density 3.0): centre
(432,432); `innerHubR` = 864×0.45×0.494 ≈ 192px ≈ 64dp; guide ring /
wells live on R40dp = 120px with well radius 5dp = 15px, i.e. the
band 105–135px is contaminated. Sample radii **0.35·innerHubR ≈
67px** and **0.85·innerHubR ≈ 163px** — both clear of the band, the
hairline (192px), and each other. Bearing→pixel:
`(432 + r·sin φ, 432 − r·cos φ)`.

### 7.1 The rest core EXISTS (fails on today's trunk — that is its job)

Render count 2, day, at rest (no touch). Assert:
- Pixel at bearing 315°, r=163px is the lifted cap:
  expected `mix(#FAF6F0, #FFFFFF, 0.10)` ≈ RGB(250, 247, 242),
  tolerance ±3/channel (native-render rounding).
- Pixel at bearing 135°, r=163px (Δ=180°, k=0) equals the flat cap
  `#FAF6F0` ±1 — and the 315° pixel is strictly lighter in every
  channel than the 135° pixel.
- Plateau: pixel at bearing 315°+8°, r=163px equals the 315° pixel
  ±2 (still inside ±9°). Falloff: pixel at 315°+21°, r=163px sits
  strictly between cap and peak (k = cos²((π/2)·12/25) ≈ 0.55).
- Night render: 315° pixel ≈ `mix(#362C27, #EDE3D8, 0.08)` =
  `#453B35` ±3 (recomputed value, §2-N1 — not Mara's `#443B35`);
  135° pixel = `#362C27` ±1.
On today's trunk every one of these pairs is equal (no core is
drawn) — the test is red before the build and green after, which is
the proof the core exists rather than a claim that it does.

### 7.2 Count changes move NO core parameter (Mara's law, as a test)

Render at rest (depth forced 0, no touch history) for counts
1, 2, 3, 4, 8 and FLOW (`minutesValue = 5`): sample the eight points
{bearing 315°, 135°, 45°, 225°} × {r 67px, 163px} and assert the
sample vectors are **identical across all six renders**. (The points
are chosen off the ring/well band, so wells, bars and FLOW dimming —
which legitimately differ — are never sampled; what remains on the
cap is cap fill + core, and both are count-invariant or the test
fails.) Structural half, run in the same test class as a source
assertion is NOT required — the grep proof in §4.2 is a build-step
check, recorded in BUILD-CHANGES, not a unit test.

### 7.3 The 45ms / 160ms pair, and start-frame identity

Drive the view with `MotionEvent`s (Robolectric, paused looper /
`ShadowSystemClock` advance):
- DOWN at centre (dist 0 ≤ innerHubR, `minutesValue = 50`): assert
  `hubPressAnimatorForTest` is running with `duration == 45` and
  `interpolator == MonolithCurves.Press`; advance 45ms → assert
  `hubPressDepthForTest == 1f` (±0.01 arrival tolerance); assert NO
  commit callback has fired (count law untouched by the dip).
- UP at centre: assert the commit callback fired exactly once AND,
  captured inside that callback, a return animator is already
  running with `duration == 160` and `interpolator ==
  MonolithCurves.Seat` (start-frame identity, §4.4); advance 160ms →
  depth == 0f.
- Fast tap: DOWN, advance 20ms, UP → commit still fires; depth at
  UP ∈ (0, 1); return runs from that depth to 0 under SEAT/160ms.
- Drag-off: DOWN, MOVE to dist > innerHubR×1.1 → return starts
  (animator running toward 0) with NO commit; MOVE back inside →
  dip restarts; UP outside ×1.1 → no commit, depth returns to 0.
- FLOW (`minutesValue = 5`): DOWN starts no animator; depth stays 0;
  UP fires nothing.

---

## 8 — RE-PRICE (E5a only)

Filed: **≈80** (Mara: rest core ≈35 + press machinery ≈45).
Re-priced against today's trunk, net production lines:

| Item | Site | Net |
|---|---|---|
| PRESS + SEAT interpolators | `MonolithCurves.kt` after :27 | +6 |
| Fields: depth, animator, inside-flag, `paintSpecular`, shader `Matrix`, 2 gradient slots | `BlockitCoreViews.kt` beside :398–401 + paints | +11 |
| `onSizeChanged` override + dual-mode stop sampling (121 stops × 2, formula-driven) | `RotaryWheelView`, new | +17 |
| Draw: save/translate/restore + core draw + shader matrix set | onDraw :688–750 restructure | +13 |
| Helpers `startHubDip()` / `startHubReturn()` | beside closure machinery | +16 |
| DOWN: dip start + inside init | :774 region | +5 |
| MOVE: ×1.1 crossing sub-branch | :803 branch | +12 |
| UP/CANCEL: return-first reorder | :815 branch head | +4 |
| Test accessors (2 `internal` getters) | beside fields | +3 |
| **Production total** | | **≈87 → filed as ≈85** (±3 counting slack) |
| Tests (`HubSpecularTest.kt`, §7.1–7.3) | new test file | +≈95 |

**Delta vs filed: +5 production.** Cause, named: Mara specced one
gradient; the live `isNightMode` push (§3.2) forces two (+stops loop
shared, so +6 not +17), and the dedicated specular paint/matrix
discipline (+5) was inside her ≈35 only by optimism; partially offset
by the crossing branch being smaller than feared (−6 across items).
**E5 total re-price: E5a ≈85 + E5b ≈50 (Mara's, untouched) = ≈135**
vs the filed ≈130. No state, no persistence, no new tokens, no new
files in production. If DEVICE_PASS_E5A fails, the flip is one
constant (§6) — the ≈85 does not come back, and is not meant to: the
dip and the rest core are the purchase; the travel was the option.

---

## 9 — RISKS

- **R1 — Angle-convention flip (the live one).** A builder who feeds
  315° straight into SweepGradient positions gets a core at bearing
  45° (upper-right) with its travel toward bearing 225° — a wrong
  room, silently. §3.2's conversion and §7.1's bearing-addressed
  pixels are the guards; the test samples *bearings*, so a flipped
  build is red, not plausible.
- **R2 — Shader leakage via shared paints.** Covered by §3.3's
  dedicated paint; a build that reuses `paintFill` passes §7.1 and
  still tints the first well drawn after the core on some future
  reorder. The §4.2-style grep (`paintSpecular` is the only shader
  site) is part of the build record.
- **R3 — The dip makes DOWN visible for the first time.** Any Slice
  C/D test or sibling spec that priced "hub press is invisible" is
  now wrong (Mara's risk, carried). Specifically the tetrad render
  harness (`TetradRenderTest`) renders at rest and is unaffected;
  anything dispatching DOWN in tests must expect invalidations.
  Flagged for Marlowe's merge, not absorbed here.
- **R4 — Press interrupted without UP/CANCEL** (view detached or
  `isLockedSession` flips mid-press; the host hardcodes
  `isLockedSession = false` today, TactileDeckScreen :285, so the
  live exposure is detach-only): depth could park mid-travel with
  no return running. The closure animator has the same exposure
  today and no guard; I do not gold-plate past trunk precedent.
  Named so the device pass can poke it; a one-line
  `onDetachedFromWindow` return-start is the pre-priced fix (+3)
  if it ever reproduces.
- **R5 — 45ms vs frame cadence.** At 60Hz the dip is 2.7 frames;
  the first frame may already show depth ≈⅓. That is the filed
  curve sampled by a real panel, not a defect — §7.3 asserts
  endpoints and configuration, never intermediate frame values.

---

## 10 — WHAT I DID NOT TOUCH

E5b (orange exit) — Mara's file stands. The hub's target resolution,
commit callbacks, feel calls (:828–837), closure settle (:369–378),
and every wheel-physics line are byte-untouched by this spec. The
tonal step (D3) and Kin's scale (D1) stay unbuilt by ruling, in
print, above.

— Nabe, Improvers · the hub
