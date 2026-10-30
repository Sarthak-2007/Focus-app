# MAX-IMPL — Issue #4: THE STRIP AS ONE PHYSICAL INSTRUMENT, AT MAXIMUM

**Seat:** MIP-02 (Maximum-Implementation-Potential, issue #4) — dedicated-100 programme.
**Job, and only job:** take the idea — *the Deck's top strip as ONE physical
instrument whose form / light / position carries the state, words only where
the object cannot speak* — and work out the fullest version that can actually
be built in this app, with mechanism, cost, and demands. Nothing else.
**Floor, not ceiling:** `studio/plan/DESIGN-issue-4-the-bolt.md` (THE BOLT,
ANTLITZ cell) + the v0 code already on this branch
(`app/src/main/java/com/example/ui/components/SealBar.kt`,
`SealBarStateTest.kt`). Everything the Bolt ruled stands unless this file
names the clause it exceeds and prices the excess. Sources read whole:
`studio/plan/AUDIT-deck.md`, `studio/plan/HEADER-BAND-AUDIT.md`, the Bolt
design, `SealBar.kt` as built, `TactileDeckScreen.kt` (pinned stack +
derivations), `ArmProvingController.kt`, `WatchStore.kt`, `GuardLine.kt`,
`TactileSoundEngine.kt`, theme `Color.kt` / `Type.kt`.
**Law held throughout:** Sora / Atkinson Hyperlegible / Anonymous Pro only;
never green or near-green (every colour below satisfies the house zero-green
check, green < max(red, blue), or is a named existing token); never
AI-generated images (all renders in §13 are deterministic hand-built SVG /
Compose harness frames); no telemetry words on the face — BIND, PROOF,
POSITIVE, WARRANT, LEASE, SEALED never render; judged against Blockit's
*discipline* (the object carries the state) and never its skin.
**Nobody compiles.** This is a spec. Costs are studio-priced (S = a sitting's
edit, M = a lane, L = a restructure) plus honest LOC estimates.

---

## 0 · The maximised idea, in one paragraph

The strip stops being a bar with a moving plate and becomes **the phone's
lock, whole**: one machined housing in which a deadbolt's *travel* is the
session's biography — open, caught, creeping link-by-link through proving,
thrown at t=0, pinned, degraded in place when proof dies mid-session, unpinned
then retracted in two witnessed stages when the session is released — while a
≤9dp jewel carries *only* what is proven right now, and a glass window carries
*only* the one number the object cannot mime, the absolute clock end. The
maximum is not more states drawn; it is **closing the Bolt's two admitted
blind spots** (B-C1 shield-staleness, B-C2 finish/detention), **giving proving
and release a body** (the two moments the current instrument goes silent
precisely because state is changing), and **moving the whole face onto one
custom-draw node** so pose, bed, pin, shutter and light are pixel-exact,
allocation-free, token-bound for all seven looks, and mechanically testable
against the glance battery — at a total build cost of one L lane, no DB
migration, no new permission, no new sound buffer, and no new word on the face.

### Tier map (what "maximum" is made of)

| Tier | Contents | Status |
|---|---|---|
| MAX-0 | Bolt v0 as built on this branch (6-state `SealBarState`, Box-tree render, 60dp housing) | exists, DECLARED |
| MAX-1 | Bolt §8 amendment backlog (FEILE geometry, TON table, bed/socket, choreography, LETTER semantics) | already specced in the Bolt file |
| **MAX-2** | **This file's core: axis state machine (§2), proving creep + jam tooth (§3.3), two-stage release (§3.4), break/stale shutters (§3.5), single-Canvas renderer (§4), closed sound/haptic table (§7), housing-as-door (§8), executable glance battery (§13)** | **the ask** |
| MAX-3 | Gated extras, each with its own kill gate: canary evidence bezel (§3.6), long-press re-sweep (§8.3), final-60-seconds window experiment (§6.3, recommended AGAINST) | optional, off by default |

MAX-2 is the fullest version that survives the truth laws today. MAX-3 items
are buildable and priced, but each ships only through its named gate.

---

## 1 · What the maximum must fix that the Bolt left open

Named in the Bolt's own §8, carried here as requirements, not re-argued:

1. **B-C1 — no honest full-strength RUNNING while the shield gap stands.**
   The host fold has no `shieldStale` input, so the jewel can claim magenta
   on a shield nobody has re-witnessed. (§2.2 adds the input.)
2. **B-C2 — no finish input.** Without it the Bolt drops to idle up to
   ~1.8s before a release is proven — a lie in the kind direction, at the
   exact moment a tired user is watching the lock open. (§2.2, §3.4.)
3. **Proving is invisible on the instrument.** `ArmPhase.Proving/Jammed`
   already exists (`ArmProvingController.kt`: 3 links — `SESSION_ROW`,
   `SHIELD_PROTECTING`, `A11Y_SESSION_ARMED` — with a derived
   `provenCount()`), and the Deck spends that whole window with the strip
   still showing its idle pose. The seal's most consequential half-second
   is the one the object says nothing about. (§3.3.)
4. **The render is a ~15-node Box tree** (`SealBar.kt` as built: nested
   `Box`/`Row`/`Column` for bed, collar, socket, pin, bolt, bezel, halo,
   window). It works; it cannot guarantee the Bolt §6 glance arithmetic
   (exposed-bed area, jewel luminous total) because the geometry is spread
   across layout passes, alignment and offsets. The maximum renders the
   instrument the way Blockit-class objects are rendered: one draw node,
   one geometry function, testable to the pixel. (§4.)
5. **Header-band evidence gap (HB-4) applies by analogy:** no harness frame
   has ever shown this strip in proving, jammed, detention or stale states.
   The maximum ships its frames as part of the build, not as homework. (§13.)

---

## 2 · The state machine — three axes, one fold, no fourth derivation

### 2.1 The design move: stop enumerating faces, start composing axes

The Bolt's table has 10 rows and v0 code already collapsed them into 6 enum
values, losing JAMMED, STALE_WHILE_RUNNING, DETENTION and FLOW as distinct
faces. Enumerating harder is the trap (every new session fact doubles the
enum). The maximum composes the face from **three orthogonal axes**, each
with one owner and one derivation — the Bolt §2 one-derivation law, factored:

```
Face = Pose × Lamp × Window   (+ at most one Overlay, §2.4)
```

- **POSE — geometry. What physically happened.** Owner: session + arm phase.
  `OPEN (0) · CAUGHT (1/3) · PROVING(p) (creep, §3.3) · THROWN (1)`.
  The pin is *not* an axis: pin position is a pure function of pose and
  release stage (§3.4) — geometry enforces it, no flag can fake it (Bolt §1).
- **LAMP — proof. What is proven *now*.** Owner: the live fold only.
  `DARK (dead lens) · BRASS (proven, idle) · MAGENTA (proven, running)`.
  Health is the only thing that lights; fault never lights (Bolt §5, held).
- **WINDOW — strength of the end claim.** Owner: session end + proof.
  `BLANK · LIVE_END · DIM_END · INVERTED_END · SHUTTERED · BREAK_END` (§6).

Any combination the axes can produce is a legal face; any combination the
*laws* forbid is made unrepresentable in the fold (§2.3, rule L2), not
filtered at render. Renderer receives a finished `StripFace` value and
contains **zero** state logic — it can be golden-tested dumb.

### 2.2 Inputs — the closed contract (this is the B-C1/B-C2 fix)

One host-side fold, computed per frame in the ViewModel, from exactly these
inputs and no others. `boltStateFor`'s purity grep gate (Bolt §8.6) extends
to the new fold: it may not touch `WatchStore`, `seamMarksFor`, the warrant,
or a clock directly — time arrives as a parameter.

```kotlin
// NEW — ui/components/StripFold.kt (pure, no Android, no flows)
data class StripInputs(
    val verdict: FoldedVerdict,        // §2.3 — the Bolt §2 fold, unchanged, plus:
    val shieldStale: Boolean,          // B-C1: WatchStore snapshot age for the
                                       //   shield interceptor > STALE_AFTER_MS (45_000)
    val phase: ArmPhase,               // Idle | Proving(links) | Jammed(jamSet) | SealedRunning
    val session: SessionSlice?,        // id, isActive, isStopwatchMode, isInSafeBreak,
                                       //   safeBreakEndsAtWall, storedEndsAtWall (never now-derived),
                                       //   spanRemainingSeconds
    val finish: FinishSlice?,          // B-C2: detained (issued=false) | releaseProven | null
    val nowMono: Long, val nowWall: Long,
)
data class StripFace(
    val pose: Pose, val pinDropped: Boolean,
    val lamp: Lamp, val lampHalo: Boolean,        // halo iff MAGENTA
    val window: Window, val endsAt: String?,      // "HH:mm", 24h, or null
    val overlay: Overlay,                          // NONE | JAM_TOOTH | DETENTION_RIM
    val spoken: SpokenKey,                         // §9 — index into the closed sentence set
)
```

Plumbing cost of the two new inputs, honestly stated: `shieldStale` is
derivable from the `WatchSnapshot` the host already collects
(`WatchStore.permPrintAt(...) == STALE` for the shield item, or
`nowMono − measurement.measuredMono > 45_000`); `finish` is the
`_finishState` StateFlow the VM already owns (`MonolithViewModel.kt:720`).
**No new store, no new flow source — one new `combine` in the VM (+~40 LOC)
emitting `StripInputs`, and the Deck's current ad-hoc derivation block
(`TactileDeckScreen.kt:363–551`: `blockCellFor` / `timeCell` /
`sealBarStateFor` / `endsAtNumeralsFor`) is deleted into the fold.**
Net plumbing is negative LOC. No DB change, no migration (DB stays v11),
no new permission, no service change.

### 2.3 The fold — precedence, first-match-wins (extends Bolt §2 verbatim)

```
verdict:  liveRaw == OFF → OFF · read == null → NO_READ ·
          stale ∧ raw LIVE → STALE_LIVE · print UNPROVEN → UNPROVEN ·
          print LIVE ∧ frozen ≠ LIVE → FROZEN_UNPROVEN ·
          print LIVE ∧ frozen LIVE → LIVE            (unchanged)

POSE:     finish.detained            → THROWN (the row still holds; §3.4)
          phase == Jammed            → CAUGHT (recoiled, §3.3)
          phase == Proving           → PROVING(provenCount / 3)
          session?.isActive          → THROWN
          verdict == LIVE            → THROWN
          verdict == OFF             → OPEN
          else                       → CAUGHT
LAMP:     verdict != LIVE            → DARK
          shieldStale                → DARK      (B-C1: stale shield kills
                                                  the lamp, pose untouched)
          session?.isActive          → MAGENTA
          else                       → BRASS
          [detention exception, Bolt §3: finish.detained ∧ verdict was LIVE
           at finish start → MAGENTA held for the detention's life]
WINDOW:   §6 table (blank unless a session has sealed; shuttered when a
          running session's proof goes stale; inverted ≤300s to live end;
          BREAK_END during Safe Break)
```

**Anti-flap law (new, and it is asymmetric on purpose):**
- *Downgrades are instant, one frame.* Live OFF, revocation, staleness —
  the lamp dies / pose recoils the frame the fold sees it. A fade or a
  delay here is a lie with good manners (Bolt §7.1, held and extended).
- *Upgrades require a stability beat:* lamp DARK→lit and window
  SHUTTERED→LIVE_END require the proving verdict to survive **two
  consecutive sweeps or 500ms**, whichever is later — the same measured
  hand-back discipline the imprint already uses (≤890ms total). Without
  this, a flapping warrant strobes the jewel at sweep cadence and the
  instrument trains the eye to ignore it. Pose upgrades are exempt:
  pose follows acts (latch, t=0, release), and acts don't flap.
- *Stale boundary hysteresis:* `STALE_AFTER_MS` is 45_000 in
  `WatchStore`; entering STALE at 45s, leaving it only on a *fresh
  measurement* (never on age arithmetic running backwards). One-way
  door per measurement — no 44.9/45.1 oscillation is representable.

### 2.4 The canonical face table (16 faces — every one reachable, every one tested)

| # | Face | Pose | Pin | Lamp | Window | Overlay | Reads as |
|---|---|---|---|---|---|---|---|
| 1 | OFF | OPEN | up | DARK | BLANK | — | open, orange showing, dead |
| 2 | OFF-TOTED | OPEN | up | DARK | BLANK | — | identical to 1 on the face (distinction lives in the sentence + Shield — Bolt §4, held) |
| 3 | NO_READ | CAUGHT ⅓ | up | DARK | BLANK | — | never measured; caught, not locked |
| 4 | UNPROVEN | CAUGHT ⅓ | up | DARK | BLANK | — | audited, not held |
| 5 | FROZEN_UNPROVEN | CAUGHT ⅓ | up | DARK | BLANK | — | seal act unproven |
| 6 | PROVING (p=0,1,2) | creep 0.55/0.70/0.85 | up | DARK | BLANK | — | *it is being locked, link by link* |
| 7 | JAMMED | CAUGHT + recoil | up | DARK | BLANK | JAM_TOOTH | it broke mid-throw — sentence below says which link |
| 8 | ARMED (proven idle) | THROWN | dropped | BRASS | BLANK | — | locked, holding, no session |
| 9 | RUNNING (timed) | THROWN | dropped | MAGENTA + halo | LIVE_END | — | session locked in; end on glass |
| 10 | RUNNING (FLOW) | THROWN | dropped | MAGENTA + halo | BLANK | — | locked in, no end exists — blank is the truth |
| 11 | RUNNING_UNPROVEN | THROWN | dropped | DARK | DIM_END | — | hardware seated, no claim lit |
| 12 | STALE_WHILE_RUNNING | THROWN | dropped | DARK | SHUTTERED | — | was holding; proof gone dark (§3.5) |
| 13 | ENDING_SOON (≤300s) | THROWN | dropped | BRASS (step-down) | INVERTED_END | — | still holds; the window shouts once (§6) |
| 14 | BREAK (Safe Break) | THROWN | dropped | BRASS | BREAK_END | — | still locked; break's back-at time (§3.5) |
| 15 | DETENTION (finish unissued) | THROWN | dropped | MAGENTA (exception) | LIVE_END frozen | DETENTION_RIM | it refused to open, on purpose |
| 16 | RELEASING (release proven, animating) | THROWN→OPEN staged | lifts first | DARK at pin-lift | fades to BLANK at retract | — | the lock opening, witnessed (§3.4) |

Faces 6, 7, 12, 14, 15, 16 are what MAX-2 adds over v0's six. Note what is
*not* here: no per-app face, no count face, no peek face, no grace-countdown
face (the grace countdown stays in the hero, Bolt §3). Quiet in-session
events (peek token banked, check-timer fired) fold to **no face change**
(Bolt §3 quiet-swap law, held).

### 2.5 Timers the machine needs — and their budget

| Tick | Rate | Lives only while | Cost |
|---|---|---|---|
| Session end / ending-soon / stale re-evaluation | 1s | Deck foreground ∧ (session active ∨ verdict LIVE) | one `LaunchedEffect` loop, cancelled off-Deck |
| Proof-age (for §8.3 long-press only) | none standing | — | computed on demand from `readMono` |
| Travel animations | frame clock | during a transition only | §7; zero idle animation, ever |

No standing polling is added: warrant sweeps keep their existing cadence
(Deck entry / resume / broadcasts / 20s foreground, per the standing strip
law in BEYOND-deck W4).

---

## 3 · The instrument, maximised — part by part

Housing (unchanged from Bolt §1, restated so this file stands alone):
**60dp band**, radius 0, 1dp hairlines top/bottom, first element of the
Deck's pinned stack above the 72dp slide register; height never varies by
1dp in any state or transition (Δy = 0 law). Inside, left→right:
**channel + bolt** (flexible width), **jewel in bezel** (fixed), **time
window** (fixed 102×36dp). Density-only scaling; font scale never rescales
the object (Bolt §11, held).

### 3.1 Bolt and channel

- Bolt **90×34dp, never grows**; channel width = housing inner width −
  window (102) − jewel cluster (21 halo + 12 gap) − numerals gap (10) −
  housing padding (24) ⇒ at 360dp reference, channel ≈ **193dp**,
  travel ≈ **103dp**. All width growth on wider phones goes to the
  channel; detents are fractions, so every phone throws the same *read*.
- Detents: OPEN 0 · CAUGHT ⅓ · THROWN 1 · proving creep §3.3 · jam
  recoil §3.3. Exactly two resting positions; CAUGHT is a pose, never
  a home (⅓, never ½ — Bolt §3, held).
- **Warning bed:** matte `#D63E06` (`TokenAlert`, mode-invariant), channel
  floor at the shut end, bolt-footprint-sized. Covered exactly when
  THROWN, exposed in every other pose — including, critically, exposed
  by exactly the creep amount during proving and by the recoil in jam.
  OFF's interruptiveness is exposed *area*, never brightness (§5).
- **Keeper socket + collar** at the shut end (Bolt §1): socket interior
  painted `#D63E06` in OFF only; thrown bolt overlaps the collar by 4dp.
- **Travelling specular rib (new, cheap, Blockit-honest):** one soft
  light rib is *fixed in screen space* across the channel; the bolt's
  face shading is sampled from its position under that fixed light, so
  the bolt visibly moves *through* light instead of carrying a painted
  highlight with it. Implementation: a single linear-gradient brush
  whose offset is `−travelPx` inside the draw (§4) — zero per-frame
  allocation, no rig, no trig. Light carries no data (it is identical
  in every state); it only makes travel legible at arm's length.
- Grip ribs (3, as built), contact shadow drawn *inside* the channel
  (1dp south line, no blur; Compose `.shadow()` banned in this bar,
  Bolt §5). Night weight comes from occlusion → collar overlap → the
  proven-thrown rim step (`#807A7A`, night only, THROWN ∧ lamp lit)
  → cream grips, in that rank (Bolt §5, held).

### 3.2 Pin and notch

Pin above the thrown position; notch machined in the bolt (12dp wide,
2dp clearance per side, pin tip stops 1dp above the notch floor). The
notch is under the pin **only** at THROWN — the pin *cannot* drop in any
other pose, and in the Canvas renderer this is not a rule but a drawing
fact: pin Y is drawn from the same geometry function that places the
notch (§4). Pin drop is a 1-frame step at the seat +62ms haptic point
(§7), never eased — a pin falls, it doesn't glide.

### 3.3 NEW — Proving creep and the jam tooth (the seal gets a body)

The maximum's biggest single addition. Today the cascade proves three
links in silence; the maximum lets the bolt *be* the cascade's progress,
without ever claiming the seal early:

- On latch, the bolt leaves OPEN and **creeps in three discrete,
  uneased steps** as links confirm: fraction =
  `0.40 + 0.15 × provenCount` → 0.40 / 0.55 / 0.70, then the final throw
  to 1.0 happens **only at t=0** (all links confirmed), inside the
  Throw's ≤85ms sound window (Bolt §7.1: 50ms keyframed travel
  0/24/62/94% + pin step + 2dp seated sink). Creep steps are *steps*,
  one frame each — a step is a witness arriving, not a loading bar;
  easing it would print a percentage the machine doesn't know.
- Creep is capped at 0.70: the bolt is visibly *not home*, the bed is
  still ~30% exposed, the pin cannot drop. No frame of proving can be
  misread as sealed — pose, bed and pin all agree (the screwdriver
  test, Bolt §3: no forced state can fake this face).
- Three shallow **witness notches** are engraved in the channel's top
  wall at the creep fractions — static print, no words, no numbers.
  They are how a returning eye learns the throw has stages; they carry
  no label and need none.
- **JAMMED:** the bolt recoils from its creep position to CAUGHT in a
  2-frame snap, and a **jam tooth** — a 6×4dp block in the channel floor
  at the recoil point, drawn only in JAMMED — drops into the bolt's
  path. The face reads *something is physically stuck here*; the
  refusal sentence below the housing (existing slot, promoted in fault
  per AUDIT-deck F9) says which link in plain words. Tooth retracts
  the frame the retry starts; confirmed links keep their creep on
  retry (the controller never revokes a confirmed mark — the bolt
  re-creeps only over the links still missing, which is exactly the
  controller's semantics drawn).
- Cost: creep + tooth ≈ 60 LOC in the fold/renderer, 4 unit tests,
  2 harness frames. No new input — `ArmPhase` already carries `links`.

### 3.4 NEW — Two-stage release (the opening gets a body, and B-C2 dies)

Release today: finish detains ≤1.8s, receipt issues, bolt teleports to
idle. The maximum stages the opening on the same witnessed logic as
the closing (Release Proof, BEYOND-deck W4 — the opening is proved,
not assumed):

1. **DETENTION** (finish requested, unissued): face 15 — pose THROWN,
   pin dropped, lamp MAGENTA held (Bolt detention exception), window
   frozen at the seal-frame end, and a **detention rim**: the housing's
   bottom hairline thickens to 2dp in ink for the detention's life.
   One new silhouette, no words, no motion. The strip now *shows* the
   detention the VM already enforces (`navigationLocked()` covers it)
   instead of pretending nothing is happening.
2. **PIN LIFTS FIRST** (release proven): pin steps up one frame, lamp
   dies the same frame. A lifted pin over a still-thrown bolt is the
   universal read of *unlocking has begun* — and it is sequenced
   first because in the machinery it *is* first: links confirm open
   before the row goes.
3. **BOLT RETRACTS** powered, 650ms, silent, accent-free — the Sled
   Goes Home precedent (BEYOND-deck W4) applied to the bolt: it
   returns over CurveB, interruptible by a new latch (a re-arm during
   retract snaps inputs, creep restarts from the retract position —
   fractions compose, no teleport). Window blanks at retract start.
   No reverse Throw sound exists or may be created (Bolt §7.2, held).

Cost: ≈ 80 LOC (fold stage + renderer rim + retract animator), 3 tests,
2 frames. Demand: the VM must expose finish stage as §2.2 `FinishSlice`
— it already owns `_finishState`; this is a mapping, not machinery.

### 3.5 NEW — Shutters: stale-while-running and break, drawn not written

- **STALE_WHILE_RUNNING (face 12):** window gets a **shutter** — three
  diagonal slats drawn across the glass (drawn, §4, not an asset), end
  numerals removed. Pose stays THROWN, lamp DARK. The silhouette is
  unmistakable and un-reusable elsewhere: *the number was here and has
  been withdrawn.* A dimmed number would still be a claim; a shuttered
  window is a withdrawn one. (Bolt §3 named this face "window
  shuttered"; the maximum specifies the shutter's actual geometry:
  3 slats, 45°, 6dp pitch, glass tone stepped one shade toward ground.)
- **BREAK (face 14):** during Safe Break the session still holds, so
  pose/pin stay THROWN/dropped — but the lamp steps down to BRASS
  (the break is proven-idle *inside* a running session; magenta would
  claim full running) and the window shows the **break's back-at
  `HH:mm`** with a single horizontal blind-line across the glass's
  mid-height. Same window, different tenant, one drawn line of
  difference — readable as "paused, returns at —" without a word.
  `END` engraving stays (it is the bezel's furniture); the blind-line
  is what marks the numeral as a return, and §9's spoken sentence
  disambiguates fully for TalkBack.
- Both shutters are pure draw states of the window layer — ~30 LOC.

### 3.6 MAX-3 (gated) — Canary evidence bezel

If Rosa's Canary (BEYOND-deck W4) ever ships — end-to-end intercept
proof, device-gated there — the jewel's bezel gains its evidence grade
in geometry: **solid bezel ring = end-to-end proven; dashed bezel ring
(4 dashes) = self-report only.** No word, no icon; a bezel is furniture
until you learn it, and the learning is one glance at PARK, where the
grade *is* written. Until the Canary exists, the bezel is solid always
and this section is inert. Cost if gated in: ~15 LOC + 1 test. Kill
gate: the Canary's own device gate, verbatim.

---

## 4 · Rendering mechanism — one Canvas node (the MAX-2 core swap)

### 4.1 Decision

**Replace the Box-tree `SealBar` composable with a single custom-draw
node:** a `Box` (for semantics + optional press, §8) containing one
`Canvas`/`Spacer` with a `DrawScope` renderer, plus exactly two `Text`
nodes (window numerals, bezel `END`) — text stays text (font shaping,
accessibility scale behaviour and Anonymous Pro metrics are not
re-invented in paths). Everything else — housing, hairlines, channel,
bed, socket, collar, witness notches, jam tooth, bolt, grips, specular
rib, pin, bezel, jewel, halo, shutters, detention rim — is drawn.

Why this is the maximum and not a refactor for its own sake:
- **Pixel-exact glance arithmetic.** §5's budget (bed area, jewel
  luminous total) becomes a function of named constants in one file,
  unit-testable offscreen — in the Box tree it was emergent.
- **Zero allocation in draw.** All brushes/paths are remembered or
  precomputed from `StripFace` + size; the draw reads floats and draws.
  Budget: < 0.5ms draw on a mid phone, no recomposition of children —
  the node recomposes only when `StripFace` changes (state equality),
  and animates travel via a single `Animatable<Float>` snapshot read
  inside draw, so a throw costs invalidations of one node, not a tree.
- **Geometry is law, once.** Pin-under-notch, bed-under-bolt,
  collar overlap, shutter pitch are computed in one `StripGeometry`
  function shared by draw, semantics bounds, press hit area and the
  golden tests. In the Box tree each was a separate offset that could
  drift independently (and, per MASSSTAB's register, did).

### 4.2 Geometry (single source of truth)

```kotlin
// NEW — ui/components/StripGeometry.kt (pure)
data class StripGeometry(
    val housingW: Float, val housingH: Float,          // dp→px at draw
    val channel: RectF, val boltW: Float, val boltH: Float,
    val travelPx: Float,                               // channelW − boltW
    val bedRect: RectF, val socketRect: RectF, val collarRect: RectF,
    val pinX: Float, val notchX: Float,                // notchX == pinX iff fraction == 1
    val jewelCentre: OffsetF, val jewelR: Float,       // r ≤ 4.5dp — the §5 cap
    val windowRect: RectF,
) {
    fun boltLeft(fraction: Float) = channel.left + travelPx * fraction
    companion object { fun of(widthDp: Float, density: Float): StripGeometry }
}
```

Fractions: OPEN 0 · CAUGHT 1f/3f · PROVING 0.40/0.55/0.70 · THROWN 1.
CAUGHT keeps the detent contact cue (bolt tail touches the open stop's
line) and the full-shut ink flip (bolt fill = `inkControlFill()` iff
fraction == 1, else `surfaceBright`) — both as built, now in one place.

### 4.3 Draw order (back → front, fixed)

1. Housing ground (scheme `surfaceVariant`) + top/bottom hairlines.
2. Detention rim (if face 15: bottom hairline at 2dp ink).
3. Channel recess (scheme `background`) + inner border.
4. Warning bed (`TokenAlert` #D63E06, invariant).
5. Witness notches (3, channel top wall, outlineVariant).
6. Jam tooth (if JAMMED).
7. Keeper socket (interior `TokenAlert` iff OFF) + collar.
8. Bolt contact shadow rib (1dp, inside channel).
9. Bolt body (fill per §4.2, specular rib clipped to bolt, grips,
   notch cut, proven-thrown rim step at night).
10. Pin (above channel, length by `pinDropped`).
11. Jewel cluster: halo ring (iff MAGENTA) → bezel disc → jewel.
12. Window glass (or brass inversion plate, face 13) + shutters
    (faces 12/14) — numerals and `END` are the two Text nodes laid
    over this rect by the parent Box.

First composition always SNAPs to the folded face (Bolt B-C5: no
travel animated into existence on cold open). `prefersReducedMotion`
collapses every transition to its final pose at the anchor frame.

### 4.4 What this demands of the codebase

- `SealBar.kt` is rewritten around `StripFold.kt` + `StripGeometry.kt`
  + one draw function (~450 LOC replacing ~380, net +70 for three new
  faces' worth of drawing). `SealBarParams` survives as the token/
  constant home; the §5 luminous budget stays a named constant, now
  asserted by a draw-level test instead of prose.
- The seven looks inherit automatically **iff** every colour resolves
  through scheme tokens or the two invariant pairs (bed orange, ending-
  soon brass pair). Per-look demand: each look supplies exactly five
  material slots for this housing (ground, recess, bolt fill, bezel,
  glass) through the existing scheme mapping — no look may fork the
  geometry, the fractions, or the jewel cap. A look that recolours the
  bed or enlarges the jewel fails §13's battery by construction.
- SVG: the acceptance mocks are hand-built deterministic SVGs derived
  from `StripGeometry` constants (a small generator script in
  `studio/max-impl/` may emit them from the same numbers — code, not
  AI imagery), refreshed when constants change; harness frames (§13)
  supersede them as evidence once the renderer exists.

---

## 5 · OFF, made unmistakable — five channels, one load-bearing

OFF is the state that can hurt him, so OFF is over-determined on
purpose. In face 1, five independent channels say the same thing; any
four can be destroyed (greyscale, blur, darkness, colour-blindness,
peripheral vision) and the fifth still carries it:

1. **AREA (load-bearing, peripheral):** the exposed warning bed —
   90×36dp of matte `#D63E06` ≈ 644dp² of orange at the shut end,
   plus the painted socket. Bolt §6 arithmetic, held and now
   draw-guaranteed: OFF orange luminous total ≈ **125.7 proxy units**
   vs RUNNING's jewel ≤ 33.3 → **≥3.77× at 10% brightness**, the
   ship gate. Any change cutting exposed bed >20% re-triggers the
   whole battery (§13).
2. **POSITION (foveal + peripheral):** bolt at the far end from the
   keeper, channel visibly empty at the shut end. Position is the
   channel that survives total colour loss — the greyscale twin in
   §13 must pass on position + bed luminance alone (bed vs channel
   recess is a luminance step in both modes by the TON table).
3. **ABSENCE OF LIGHT:** jewel is a dark lens with an outline ring —
   drawn *unlit*, not merely dark-coloured. In a dark room at 10%
   brightness, RUNNING announces a lit point; OFF announces none.
   Health lights; fault is matte (Bolt §5 salience law).
4. **PIN UP + SOCKET EMPTY:** the keeper is visibly unoccupied and
   the pin visibly un-dropped — two small foveal confirmations that
   agree with the big two. (Confirmation, never the primary read.)
5. **SOUND/FEEL ON TOUCH (only if §8 ships):** pressing an OFF
   housing speaks the house refusal knock and routes to the pane that
   owns the lack. OFF you *touch* refuses differently from OFF you
   glance at — the glance channels above never depend on this one.

Anti-case, named: **OFF-TOTED is face-identical to OFF** (Bolt §4).
That is deliberate — what the *object* knows is identical; the
difference (apps toted to Shield) is a fact about another room and is
spoken there and in the sentence, not drawn here. One fact, one home.

---

## 6 · The session end, readable at a glance

### 6.1 Window laws (Bolt §4, held, completed)

- Numerals are the **absolute clock end** `HH:mm` (24h, Anonymous Pro
  Bold 20sp) in every window state that shows numerals — derived from
  the session's **stored end** (`startWall + target`, re-anchored at
  t=0), never from `now + remaining`. The idle-ENDS slide (AUDIT-deck
  §5.5) is unrepresentable here: pre-seal, the window is BLANK.
  Placeholders (`--:--`) banned. AM/PM, day words, countdowns banned.
- **Presence table:** LIVE_END (9, 15, 16-until-retract) · DIM_END
  (11 — numerals at window-ink stepped one tone toward glass: the end
  is a session fact even when proof is not, but it may not shine) ·
  INVERTED_END (13) · BREAK_END (14) · SHUTTERED (12) · BLANK
  (1–8, 10). FLOW's blank is a *truth*, not a missing feature.
- `END` engraved once on the bezel below the glass (Atkinson
  Hyperlegible Regular 9sp) and present only when numerals are —
  an engraving that labels an empty window would be furniture lying
  about a tenant. (v0 code prints `END` conditionally already; the
  maximum makes the condition the window state, not `showNumerals`
  ad-hoc.)

### 6.2 ENDING_SOON — the one flood, engineered

≤300s to the live end (fixed seconds, never a span fraction — Bolt
§3.1): the **whole window inverts** to the mode-invariant measured
pair — numerals `#252323` on brass `#E2BC39` (8.55:1) — and the jewel
steps *down* MAGENTA→BRASS. Pose does not move (a backed-off bolt
would claim release). The inversion is a block of lit mass entering
the face's lit cluster: it survives blur, where hue-only changes
failed the Bolt §6 pairwise test. It rises once (150ms EasingA on
entry), falls as a snap, never re-fires within a session, never
pulses. At a glance across a room: *brass window = ending*; nothing
else in the app inverts a window, so the read has no competitor.

### 6.3 MAX-3, recommended AGAINST — final-60s `MM:SS` window

Buildable in ~20 LOC (window swaps to remaining `MM:SS` under 60s).
**MIP-02's verdict: do not build.** It breaks numeral invariance
(the window's grammar changes at the moment attention is scarcest),
re-opens "the end slides" in a new tense, and duplicates the hero's
remaining readout — one fact, two renderings. Priced and recorded so
the refusal is a decision, not an oversight.

### 6.4 The glance sentence for the end

A cold reader at arm's length, 2 seconds: **lit jewel + numerals =
running, ends then · brass window = ending now · shutter = was
running, proof lost · blank window + thrown bolt = locked, no end
exists.** §13 tests exactly these four reads, pairwise, blurred.

---

## 7 · Motion, sound, haptics — the closed voice table

### 7.1 Motion (all transitions; nothing else moves, ever)

| Transition | Motion | Duration / curve |
|---|---|---|
| Seal throw (t=0) | keyframed travel from creep/open fraction: 0 / 24 / 62 / 94% → 100%, then pin step (1 frame), then 2dp seated sink, stepped rigid | 50ms travel inside the Throw's ≤85ms buffer; sink at +62ms |
| Proving creep | 1-frame steps at link confirmations | 0ms (steps) |
| Jam | recoil to CAUGHT, 2-frame snap; tooth drops same frame | 2 frames |
| Lamp change (any) | none — 1-frame step | 0ms |
| Degradation mid-session | lamp step + window shutter draws in 1 frame | 0ms (a fade reads as *failing*; the truth is *fallen*) |
| Ending-soon entry | window inversion plate rises | 150ms EasingA, once |
| Ending-soon exit / detention end | snap | 0ms |
| Release | pin step up (1 frame) → bolt retract CurveB | 650ms retract |
| Press (§8) | housing dip 1dp, return | 90ms in / ≤150ms out, EasingA |
| Cold open / reduced motion | snap to folded face | 0ms |

Loops, breathing, idle animation, lamp pulsing: banned outright
(Bolt §5 never-dim list extended to never-animate).

### 7.2 Sound — zero new buffers, two reuses, one hash guard

The composable/draw node is silent. All sound is host/VM-side, from
`TactileSoundEngine`'s existing voices only:

| Event | Voice | Source |
|---|---|---|
| Seal (t=0) | the Throw, bit-identical (its buffer already contains the layer named THE BOLT; `SEAL_BUFFER_GOLDEN_SHA256` guards it) | existing `playSealThrow()` |
| Pin drop | — none: it lands *inside* the Throw's +62ms haptic seat; a second voice would double-speak one event | — |
| Proving creep steps | — none: witnesses arriving are read, not announced (a tick per link is a progress-bar with sound) | — |
| Jam | refusal knock `playSeatKnock(6, 0.2f, 320f)`, throttled 250ms | existing call, new call-site |
| Lamp degradation / stale | — none (the Deck's knock owns staleness discovery, once, at Deck threshold — Bolt §7.2) | — |
| Ending-soon | — none (the brass inversion is the alarm; alarms are singular) | — |
| Release retract | — none (no reverse Throw may be created, Bolt §7.2) | — |
| Housing press (§8) | destination's own seat knock, or PARK's quietest seat knock `playSeatKnock(10, 0.10f day / 0.06f night, 300f)` in the host handler on DOWN; locked press = refusal knock above | existing calls |
| Idle warrant flips (either direction) | — none (no actor acted) | — |

**Net new sound work: two call-sites of existing functions, ~10 LOC.**
No buffer is rendered, re-rendered, or pitch-shifted for this design.

### 7.3 Haptics — the house table is closed; the strip invents none

Haptics ride the existing mechanisms only: the Throw's waveform twin
(`SEAL_THROW_TIMINGS = [0, 24, 38, 16]`, amplitudes `[0, 255, 0, 175]`)
at t=0; `fireFeel`-class one-shots (1/4/8/10ms ladder in
`BlockitCoreViews.kt`) for press feedback *if* §8 ships (dip = 1L
class via the engine's seat-knock pairing, never a raw `Vibrator`
call from the composable). Creep, jam (beyond its knock's paired
feel), lamp, shutter, retract: **no haptic** — a haptic per state
change would make the instrument buzz at the warrant's sweep cadence,
and §2.3's anti-flap law exists precisely to prevent that class.

---

## 8 · Press — the housing as a door (MAX-2, HAUT's spec completed)

v0 is honestly non-interactive. The maximum makes the housing a door
**without making the bolt a handle** — the finger moves the case;
proof moves the bolt (Bolt §7.3, adopted and completed):

### 8.1 Routing — destination is a function of the face

| Face pressed | Route | Why this pane owns it |
|---|---|---|
| OFF (lack = accessibility) | system Accessibility pane | the pane that owns the lack (AUDIT-deck F9 misroute fix, applied to the door) |
| OFF (lack = usage / other grant) | that grant's pane | same law |
| NO_READ / UNPROVEN / FROZEN_UNPROVEN | re-sweep (`WatchStore.runSweep`) + PARK | retry the check, show the board |
| JAMMED | PARK, jam detail | the board owns the warrant's detail (Guard line's job, shared) |
| ARMED / RUNNING* / ENDING_SOON | PARK (read-only while sealed, as PARK already degrades) | the strip's facts' standing home |
| STALE_WHILE_RUNNING | re-sweep + PARK | as UNPROVEN, running tense |
| DETENTION / PROVING | **no route — totally dead press** | a door that opens mid-act lies about the act |

*Running press never interrupts, ends, or peeks the session. The
strip is never a session control. STOP lives in the slide register's
chair (AUDIT-deck F6), not here.

### 8.2 Press physics and gating

- Whole-housing dip: 1dp translate + contact-rib shift, 90ms in /
  ≤150ms return, EasingA. Bolt, jewel, window **do not move** — a
  press that moves the bolt teaches the hand the bolt is a handle.
- Gate re-read **synchronously at DOWN and at UP** from a live
  provider (`sealed || navigationLocked()`, the HB-1/HB-2 fix shape
  applied here from birth — this door is phase-keyed, never
  session-only). A gated press is totally dead: no dip, no tick, no
  semantics change.
- Single-voice rule: at most one sound per press — destination's or
  the knock, never both (§7.2).
- Hit area: the full 60dp housing (the Guard line's text-tight target
  is the anti-example, HEADER-BAND A6). No ripple. No long-press in
  MAX-2 (see §8.3). No drag, no swipe, no double-tap.

### 8.3 MAX-3 (gated) — long-press re-sweep with proof-age answer

600ms hold on an idle face triggers a fresh sweep; on release the
window *briefly* (1.2s) shows the warrant's proof age as `M:SS`
before returning to its lawful tenant. Buildable (~50 LOC), but it
puts a second grammar in the window and a hidden gesture on a
no-hidden-gestures screen. Gate: ships only if the §13 1 AM battery
shows users failing to find RETRY — otherwise the sentence slot's
existing RETRY action already does this job in the open. Default: off.

### 8.4 Open consequence, flagged upward (not decided here)

If the housing is a door, HAUT's argument (Bolt §7.3) strengthens:
the refusal sentence below becomes partly a *second* door to the same
lack. Deck-level ruling for Ren — sentence demoted to print-only when
the housing door ships, or sentence keeps its action and the housing
routes only to PARK. This spec builds the door so either ruling is a
one-line routing change; it does not smuggle the decision in.

---

## 9 · Semantics — the spoken instrument (LETTER's set, completed)

One merged semantic node for the whole housing; numerals and `END`
are removed from the semantics tree (the sentence carries the end);
no live region; no `stateDescription`. The closed set grows from the
Bolt's 11 to **16 sentences**, one per §2.4 face (faces 1/2 share a
node shape, differing in the toted clause), each beginning with its
state claim, none flattering:

OFF · OFF-TOTED · OFF-WHILE-RUNNING (theoretical, fold-defensive) ·
NO_READ · UNPROVEN · PROVING-n-of-3 ("Locking. Two of three checks
confirmed.") · JAMMED (+ link name in plain words) · ARMED ·
RUNNING (with end) · FLOW · RUNNING-UNPROVEN · STALE-WHILE-RUNNING ·
ENDING-SOON (with end) · BREAK (with back-at) · DETENTION ·
RELEASING. Custom accessibility actions on the node mirror §8.1's
routes (labelled by destination, e.g. "Fix blocking", "Open Park",
"Check again") — this closes the eyes-free half of AUDIT-deck's F22
bar debt *for this surface* (wheel/slide/rail remain F22's own lane).
TalkBack merges the housing node with the refusal sentence's node
when the sentence shows (REL-TB-1/2, held).

Cost: ~90 LOC + a banned-words test (no sentence may contain BIND /
PROOF / POSITIVE / WARRANT / LEASE / SEALED as a user-facing claim —
"Blocking is not proven" is the lawful shape, as v0 already speaks).

---

## 10 · Colour & material — the binding table (TON's, extended by the new parts)

All values are existing tokens or the Bolt's ratified pairs; **no new
hex enters the app in MAX-2.** New parts inherit as follows:

| Part | Day | Night | Note |
|---|---|---|---|
| Bed / socket paint | `#D63E06` | `#D63E06` | `TokenAlert`, invariant; `NightAlert #FF8A5C` banned here (Bolt §5) |
| Bolt fill, thrown | `inkControlFill()` | cap `#4A423C` | never cream as fill |
| Bolt fill, open/caught | `surfaceBright` | `surfaceBright` (night raised) | ink flip at full shut only |
| Jewel BRASS | `#E2BC39` | `#E2BC39` | one metal, both modes |
| Jewel MAGENTA | `#C81FA8` | `#F04FCB` | house split; palette-native |
| Jewel DARK | lens = bezel glass tone + outline ring | same | drawn unlit |
| Bezel disc | ink | cap | rescues brass from day camouflage (1.21:1 → housed) |
| Window glass / ink | `#2E2A26` / `#FAF6F0` | `#161311` / `#FFE4CF` | dark glass both modes |
| Window INVERTED | ink `#252323` on `#E2BC39` (8.55:1) | identical | mode-invariant pair, hardcoded on purpose |
| Window DIM_END ink | glass ink stepped 1 tone toward glass | same rule | §6.1 |
| Shutter slats / blind-line | window ink at glass-step tone | same rule | drawn, never a new colour |
| Jam tooth | bolt ink | bolt ink | it is machinery, not alarm — no orange (orange is the bed's alone) |
| Detention rim | ink hairline 2dp | cream hairline 2dp | thickness carries it, not colour |
| Specular rib | white at 6% over bolt fill | white at 4% | shading operation on a named fill, not a token (H-7 rule) |

Zero-green check: every row's green channel < max(red, blue) — brass
(226,188,57) ✓, magenta day (200,31,168) ✓ / night (240,79,203) ✓,
orange (214,62,6) ✓. Sora appears nowhere on this face (no display
word exists here to set); Atkinson sets `END` and the sentences;
Anonymous Pro sets every numeral. That *is* the font law applied, not
an exception to it.

---

## 11 · Demands on the app — the whole bill, up front

1. **VM:** one new combined `StripInputs` flow (§2.2, +~40 LOC, two
   mappings of flows already owned). Deck derivation block deleted
   into the fold (net plumbing ≈ −120 LOC in `TactileDeckScreen.kt`).
2. **WatchStore:** no change. `shieldStale` is read off the existing
   snapshot. (If a future shield heartbeat lands, this input is where
   it plugs in — named seam, not built.)
3. **ArmProvingController:** no change. `links`/`provenCount()` are
   already the creep's input.
4. **Sound engine:** no change, no new buffer, hash untouched.
5. **DB / services / permissions:** none. DB stays v11.
6. **Pinned stack:** housing stays 60dp at the same address; pinned
   total unchanged; Δy = 0 across every transition in §7.1 including
   retract (the retract is *inside* the housing).
7. **Looks programme:** five scheme slots per look (§4.4); geometry
   and fractions are look-invariant. The strip is the first surface
   where "looks cannot fork the instrument" is test-enforced (§13).
8. **Tests/harness:** §13's frames + battery are part of the build,
   not follow-ups (HB-4's lesson). Existing harness
   (`CascadeDeckRenderTest` pattern) composes the Deck directly — the
   strip frames compose `SealBar` standalone with folded faces, which
   the pure fold makes trivially drivable.
9. **Issue #6 boundary:** the other Deck fixes (dead parameters,
   eyebrow, task ghost, drawer…) are #6's cell's. This spec touches
   the Deck file only at the derivation block and the `SealBar` call
   site; if both lanes land, the fold is the merge seam — #6 consumes
   `StripInputs`, it does not re-derive.

---

## 12 · Implementation plan & cost

| Phase | Work | Price | LOC (est.) |
|---|---|---|---|
| P1 | `StripFold.kt`: axes fold, §2.3 precedence, anti-flap, `StripFace`; exhaustive unit tests (every table row × flap cases) | M | ~220 + ~250 test |
| P2 | `StripGeometry.kt` + Canvas renderer replacing the Box tree; specular rib; shutters; jam tooth; witness notches; detention rim | M | ~450 (replaces ~380) |
| P3 | Proving creep + jam wiring (ArmPhase → fold); two-stage release (FinishSlice → fold, retract animator) | S–M | ~140 |
| P4 | Housing door (§8: routing, dip, gate re-read, single voice) + semantics (§9: 16 sentences, custom actions, banned-words test) | M | ~180 |
| P5 | Frames + glance battery (§13) as executable tests; SVG mocks regenerated from geometry constants | S–M | ~150 test/tooling |
| | **MAX-2 total** | **one L lane, phased P1–P5, each phase independently landable after P1** | **~990 prod / ~400 test** |
| MAX-3a | Canary bezel (if Canary ships) | S | ~15 |
| MAX-3b | Long-press re-sweep (if §8.3 gate passes) | S | ~50 |

For scale: MAX-1 (the Bolt backlog alone) was already ~5 priced items
across two M's. MAX-2's premium over MAX-1 is roughly **one extra M** —
and it buys the two blocking charges (B-C1/B-C2), both witnessed
transitions (proving, release), and a renderer the glance battery can
actually convict or acquit. Phases P1+P2 alone deliver the honest
instrument; P3 is the biography; P4 is the door.

**Risks, named:** (a) Canvas text-adjacent layout (numerals over glass
rect) needs one careful density pass — mitigated by keeping numerals
as real Text nodes positioned from `StripGeometry`. (b) The retract/
re-arm composition (fractions composing mid-animation) is the only
genuinely fiddly logic — it gets its own unit tests in P3. (c) If the
glance battery fails the Canvas render where the Box tree passed,
geometry constants — not the architecture — are what get tuned; the
battery is the judge either way (§13).

---

## 13 · Acceptance — the glance battery, made executable (ship gate)

The Bolt §6 battery (BLICK T1–T9) stands and is extended; **no
battery, no swap** — and MAX-2's renderer exists partly so these run
as code, not as a review meeting:

- **T1–T9 (Bolt, held):** cold reads · pairwise forced choice · 1 AM
  simulation (10% brightness, dark room, no re-aiming) · greyscale
  twin · OFF-grep · the count question (answer: not on this face) ·
  two-channel (sight/TalkBack) parity · paired-screenshot adjacency ·
  cold-read-of-record. Acceptance frame is the OFF/RUNNING pair,
  Night, with greyscale twins — a lone RUNNING shot proves nothing.
- **T10 (new) Luminance budget as a unit test:** computed from
  `StripGeometry` + token constants in code — OFF bed luminous total
  ≥ 3× RUNNING jewel total; jewel ≤ 41.9 proxy units; fails the build
  of the *test suite* if a constant drifts. Prose gates die; this one
  executes.
- **T11 (new) Proving honesty:** frames at p=0/1/2 + t=0 — no proving
  frame may be cold-read as "locked" (pin up + bed exposed must read
  as not-yet in ≥95% of reads); the t=0 frame must read locked.
- **T12 (new) Release honesty:** detention frame cold-read as "still
  locked" ≥95%; pin-lifted frame read as "opening"; no frame in the
  retract may read as OFF-with-session (lamp/window sequencing, §3.4).
- **T13 (new) Shutter reads:** STALE_WHILE_RUNNING vs RUNNING_UNPROVEN
  pairwise — shutter vs dim numerals must not be confused ≥90%;
  BREAK vs RUNNING pairwise on the blind-line + brass step-down.
- **T14 (new) Sentence parity for the new faces:** PROVING, JAMMED,
  DETENTION, BREAK, STALE spoken sentences (§9) each cold-matched to
  their face by a listener who cannot see it.
- **Frames owed (HB-4 closure):** standalone harness frames for all
  16 faces × day/night, generated from the fold in tests — the strip
  never again ships a state no frame has shown.

SALZ's 21 acceptance tests (Bolt §10) bind unchanged alongside:
one-derivation grep gate, both-directions disagreement, mid-session
revocation same-frame, idle-tense (blank window), rent (60dp buys a
state change, a threshold, an evidence channel — MAX-2's rent buys
two witnessed transitions and a door on top).

---

## 14 · What the maximum refuses (recorded so the refusals are decisions)

- **Bolt as handle / drag / swipe** — the object is moved by proof,
  never by the finger (§8). A draggable bolt is a second slide, and
  the Deck already has its one verb.
- **Per-link lamps, per-app segments, count on the face** — telemetry
  in metal is still telemetry. The count lives in Shield + imprint.
- **A countdown window, a remaining-time window, a % anything** (§6.3).
- **A lit fault, a pulsing anything, a breathing lamp** — health
  lights; fault is matte and still.
- **New sounds, a reverse Throw, a per-link tick** (§7.2).
- **Words where the object speaks:** no state label joins `END` +
  `HH:mm` on this face, in any state, including JAMMED — the jam's
  words live in the sentence slot built for them, one line below.
- **A green "OK" jewel** — banned by palette law and by sense: this
  house proves with brass and magenta or not at all.

## 15 · Dissents & open flags carried, not buried

1. DUNKEL's 5dp ending-soon backoff (Bolt §11) — still dissented,
   still rejected: pose is truth; §6.2's inversion passes the battery
   without it.
2. HAUT vs KLANG on the locked press (silence vs refusal knock,
   Bolt §7.3) — knock governs in §7.2; HAUT's objection stands here.
3. §8.4's sentence-demotion question — flagged to Ren, deliberately
   undecided; the build is ruling-agnostic.
4. Day-glass extension (Bolt §5, DUNKEL) — still an open TON/Ren
   ratification; §10's table is the fallback and is what P2 builds.
5. This design exceeds AUDIT-deck §3's eviction exactly as the Bolt
   §9 disposition stated, and inherits its flag: **Ren's
   countersignature** on the disposition is still the standing ask —
   issue #4 is Sarthak's direct order and supersedes the eviction for
   this band, but a cell (or a MIP seat) does not overrule an audit on
   its own authority.

— **MIP-02** (Maximum-Implementation-Potential, issue #4).
Spec: `studio/max-impl/issue-4-strip-max.md` on `agent/issue-4`.
DECLARED as spec only — no code written, nobody compiles.
