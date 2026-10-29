# Vega — slice D/E part: THE COMMIT (slide crossing → latch)

*Seat: Vega, Wild, physics. Coordinator: Marlowe. Law as given: Live Wire
(Ines §3 + Otis heartbeat) — synchronous verdicts ≤5ms at DOWN and at the
0.68 gate; Park / 1500ms / UNKNOWN / Canary / 20s poll / Check Drag /
Proving Creep are dead. Register governs: GATE 0.68 · CATCH/COMMIT 0.84 ·
OFF-STOP 0.65 · TRAVEL 180dp · TAP MESH 140ms→0.92 + 110ms assist ·
SEAT SPRING Cato (400/16), 110ms · LATCH KNOCK 14ms @ 0.85 at 0.97 ·
BRUSH FLOOR 90ms. Line numbers verified this run against the live tree
`Focus-app/` (not the token/counterfoil build copies).*

## (1) Verdict line

**BUILDS AS SPECCED, with two exact amendments where the spec meets the
real code and loses:** (A) TRAVEL — zones are fractions of the view's
actual `maxTravel`, with 180dp demoted to a minimum-travel floor (the
real travel is ~248dp on a 360dp-wide phone; a literal 180dp track
would not fill the bar the host draws); (B) the DAMPED law, which no
source file actually writes down, is fixed here as a pure position
function with gain 2/3 (finger commit coordinate 0.92). Everything else
— commit under contact at displayed 0.84, powered seat, latch knock at
0.97, release-commit dead — builds exactly as registered.

## (2) The joint against the real code (today)

`SlideToStartView`, `ui/views/BlockitCoreViews.kt:1046–1225` (Ines
measured :669–855; the wheel's physics rewrite grew the file above it).

- **"Displayed progress" is one field:** `dragFraction` (`:1078`).
  `onDraw` renders from it and nothing else — thumb left edge =
  `inset + dragFraction * maxTravel` (`:1104`), trail to
  `thumbRect.right` (`:1108–1114`), label/badge alpha =
  `1 − 1.3·dragFraction` (`:1118`). `maxTravel = width − 2·inset(6dp)
  − thumbW(76dp)` (`:1103`, recomputed identically in touch at
  `:1155–1157`). So the commit predicate must test **this field, after
  the damping transform** — never raw `event.x`. That is what
  "displayed" means in this codebase; there is no second progress.
- **Today `dragFraction` is the finger, undamped:**
  `updateDrag` (`:1198–1212`) sets it to
  `((touchX − inset − thumbW/2) / maxTravel).coerceIn(0,1)` — 1:1,
  centre-mapped, no zones, no resistance anywhere.
- **DOWN has no hit-test and teleports:** `ACTION_DOWN` (`:1160–1168`)
  sets `isDraggingThumb = true` unconditionally and calls
  `updateDrag(event.x)` — the thumb jumps to the finger. A tap landing
  far right therefore *is* a drag to that fraction, free of charge.
- **Commit is release-only at the wrong number:** `ACTION_UP`
  (`:1174–1191`) commits iff `dragFraction >= 0.82f` **at lift-off** —
  superseded 0.82, evaluated at the gesture's noisiest instant, and a
  drag that crossed and retreated under contact commits nothing.
- **On success the thumb visibly returns:** UP sets `dragFraction = 1f`
  for one frame, plays the seal **in the view** (`:1184`,
  `playHarmonicSeal` — under Live Wire law the seal belongs to
  Opal's t=0 proof frame, not the gesture), invokes
  `onSlideCommitted`, then `animateSpringBack(0f)` (`:1186`) — a 280ms
  `MechanicalOvershoot` ride home (`:1214–1225`). The gesture of
  no-return, returning. This block is deleted, not tuned.
- **The only mid-drag feel is at the commit line itself:**
  `updateDrag` fires `fireFeel(8L)` on crossing 0.82 in either
  direction (`:1200–1205`) — no 0.68 gate, no hysteresis, and the
  "gate" and the commit are the same coordinate today.
- **No seat, no knock, no latch state, no brush floor, no tap mesh.**
  The view's whole state is `dragFraction` + `isDraggingThumb` +
  one animator. There is no Cato (400/16) spring anywhere in the tree
  (grep: no stiffness-400 spring exists; `MonolithCurves.kt` offers
  only beziers + overshoot interpolators). The nearest reusable
  pattern is the wheel's own per-frame spring integrator in this same
  file (`:957–975`, semi-implicit, settle-tested) — the seat copies
  that pattern, it does not import a Compose `SpringSpec` into a
  Canvas view.
- **Host wiring:** `TactileDeckScreen.kt:484–504` — slide is 68dp
  tall, full width inside a 12dp-padded box (this is where the
  ~248dp real travel comes from), `isArmedState` hardcoded `false`
  in the update block, badge = `"${dialMinutes}M"` (per-block, not
  total — another seat's joint, noted, not touched here),
  `onSlideCommitted = onArmSession` passed straight through.
- **Session insert:** `MonolithRepository.startSession` (`:869–918`)
  is one insert, `isActive = true` from birth (`:910`). Nothing in
  my joint changes it: the latch (below) is what calls it, via the
  existing host callback, exactly once.

## (3) Amended spec — the commit, buildable

### State

Add to the view: `enum SlidePhase { REST, DRAG, MESH, TAKEN, SEATED,
REFUSED }`, `grabOffsetPx: Float`, `latched: Boolean`,
`gateArmed: Boolean` (haptic re-arm), `wireLiveAtDown: Boolean`,
`seatVelocity: Float`. No other new state.

### Finger progress vs displayed progress (the damping law, exact)

At DOWN, **hit-test first** (amendment to today's teleport): the drag
starts only if `event.x` is inside the drawn thumb rect expanded by
12dp slop. Capture `grabOffsetPx = event.x − thumbCentreX`. If DOWN
misses the thumb, this is the tap path (§Tap) or nothing — never a
teleport.

Finger progress (pure, from the real geometry):

```
p = clamp( (event.x − grabOffsetPx − inset − thumbW/2) / maxTravel, 0, 1 )
```

Displayed progress `d = D(p)` — a **pure function of finger position**
(no frame integration, no history), so a reverse drag retraces the
identical curve and the thumb can never lag, wind up, or lie:

```
FREE    p ≤ 0.68:            d = p                                   (gain 1)
DAMPED  0.68 < p:            d = 0.68 + (2/3)·(p − 0.68)             (gain 2/3)
```

with the gain kink at the gate softened over `p ∈ [0.68, 0.70]` by
blending gain 1 → 2/3 with smoothstep on `(p − 0.68)/0.02` (position
stays continuous and monotone; only the slope rounds). Consequences,
all exact:

- **Commit finger coordinate: p = 0.92.** `0.68 + (2/3)(0.92 − 0.68)
  = 0.84`. The hand travels 0.24 of travel inside the damped zone to
  buy 0.16 of displayed progress — a 3:2 gear reduction. That *is*
  the resistance: not a velocity filter, a gear. It is felt as the
  thumb falling behind the finger by up to `0.08 · maxTravel`
  (≈20dp at 248dp travel) — large enough to feel, small enough to
  never read as lag, and it closes back to zero if he retreats into
  FREE, because the function has no memory.
- **Why gain 2/3 and not less:** at gain 1/2 the displayed maximum
  under contact is exactly 0.84 at p = 1.0 — commit would live at
  the very end-stop and the TAKEN zone would be unreachable by hand.
  At 2/3, commit lands at p = 0.92 with 8% of travel still in the
  hand as margin against end-stop fumble. Gain is one named constant
  (`DAMP_GAIN = 0.6667f`); device tuning may move it to 0.70
  (p_commit = 0.909) without touching any other number here.
- **TAKEN 0.84–1.00 is the spring's zone, not the finger's.** The
  instant `d` crosses 0.84 the finger's ownership ends (next bullet);
  displayed 0.84 → 1.00 is traversed by the powered seat in 110ms.
  No finger position maps into TAKEN — `D(1.0) = 0.893` is the most
  a held finger can ever display, and it is never reached, because
  the crossing at p = 0.92 fires first.

### The crossing (commit itself)

Evaluated **inside `updateDrag`, after `dragFraction = D(p)` is
written**, on every MOVE (and on mesh animator frames, §Tap):

```
if (phase == DRAG && !latched && wireLive && dragFraction >= 0.84f) latch()
```

Edge-triggered, upward, **under contact** (`isDraggingThumb` still
true — that is the whole law). `latch()`:

1. `latched = true; phase = TAKEN`. All further MOVE/UP events are
   ignored for position. Dragging back, lifting off, a second
   finger — none of it un-commits. There is no code path from TAKEN
   except forward.
2. The **powered seat** starts from the crossing value (0.84 ≤ d <
   0.85 in practice): a per-frame spring integrator copied from the
   wheel's pattern (`:957–975`), semi-implicit Euler, dt clamped to
   [1, 32]ms, **stiffness k = 400, damping c = 16** (Cato; ζ =
   16/(2·√400) = 0.40), target 1.0. Position clamps at 1.0; any
   overshoot energy is absorbed as the cap's 2dp visual compression
   against the end wall, never as travel past it. Expected seat
   duration ≈110ms (register); the spring, not a timer, decides —
   if a 120Hz vs 60Hz frame cadence moves it ±15ms, that is inside
   tolerance and no number changes.
3. **Latch knock by position, never by timer:** the first frame
   `d ≥ 0.97` fires the knock — **14ms @ 0.85** — through the
   existing feel channel. Position-triggering makes it land at
   0.97 of travel on every device and every refresh rate.
4. First frame `d == 1.0` (clamped seat): `phase = SEATED`;
   `onSlideCommitted?.invoke()` — **the host callback fires at the
   latch, not at the crossing.** The ~110ms between crossing and
   latch is the gesture's climax; `startSession` (`Repository:869`)
   and everything downstream of it (Live Wire proving, t=0 =
   max(last link CONFIRMED-SHUT, latch + 210ms, pawl frame)) hang
   off this frame, unchanged. The view plays **no seal sound** —
   `:1184`'s in-view `playHarmonicSeal` is deleted with the
   release-commit block; the seal sounds once, at t=0, from the
   host side.
5. The thumb **stays seated**. No spring-back on success, ever.
   Return home is post-session furniture (Fen's Sled Goes Home,
   receipt +1000ms, 650ms powered) and belongs to that joint; this
   view only needs to accept an external reset — which already
   exists: the `isArmedState` setter zeroes `dragFraction`
   (`:1064–1071`); extend it to also clear `latched/phase`.

### The gate at 0.68 (inside this joint, because the verdict rides it)

First upward crossing of **displayed** 0.68 per drag: gate haptic
(the existing `fireFeel(8L)` feel, moved from 0.82 to 0.68) +
synchronous Live Wire re-read (≤5ms, Ines's `liveWarrant()` /
Otis registries — an injected `() -> Boolean` on the view; this
joint consumes it, does not build it). Re-arm the haptic only when
displayed falls below 0.65 (0.03 hysteresis — the same 0.03 the
register spends on OFF-STOP, reused, no new constant). If the wire
was dead **at DOWN**, travel is limited from the first frame:
`D(p)` output clamps at **0.65** and no crossing can occur (the
refusal sentence + TAP TO FIX routing are the strip/host joint;
this view exposes the clamp and a refusal callback). If the wire
**dies between DOWN and the gate** (service killed mid-drag), the
gate re-read catches it: clamp target becomes 0.65, displayed eases
0.68 → 0.65 in 60ms (a 0.03·maxTravel ≈ 7dp refusal jolt), dull
knock, phase = REFUSED; release then springs home on the existing
return path. A corpse never seats a latch, including a fresh one.

### Why release-commit loses (the physics, for the record)

Lift-off is the noisiest instant of any slide: as normal force
drops, the fingertip rolls and the contact centroid translates
**3–6dp** (W4's figure; ≈0.5–1.0mm of skin roll) in a direction set
by grip, not intent. At real travel (≈248dp) that is 0.012–0.024 of
progress — wider than the distance between "crossed" and "didn't"
for any drag ending near the line. Release-commit therefore samples
the verdict from the one frame the hand cannot control: a drag that
honestly crossed 0.84 can roll back under it at lift and be refused;
a drag that honestly stopped at 0.81 can roll into 0.82 and commit.
Under-contact crossing samples the verdict mid-gesture, at full
contact pressure, where the finger's position noise is smallest —
and the powered seat then makes the remaining travel independent
of the lift entirely. Release-commit isn't a worse threshold; it's
a worse *instrument*.

### Tap path (same latch, no second machine)

DOWN that misses the thumb but lands on the track, held ≥ **90ms**
(brush floor — shorter commits nothing, phase returns to REST on
UP): on UP, if the wire was live at DOWN, the **driven mesh** runs
`d`: 0 → 0.92 in **140ms**, bezier (0.30, 0, 0.20, 1.00) — the tap
mesh curve from W4. The mesh's animator frames feed the **same**
crossing predicate: `d` crosses 0.84 partway through the mesh
(≈102ms in, by the bezier), `latch()` fires, the seat spring takes
over from wherever the mesh had reached and finishes 0.92 → 1.0
in the shared **110ms assist** — no, precisely: the seat spring
takes over *at the crossing*, so the mesh is cut at 0.84 and the
spring runs 0.84 → 1.0; the register's "140ms to 0.92, then 110ms
assist" and this reading differ by which segment owns 0.84–0.92.
**This file's ruling: the crossing owns it** — one latch path for
drag and tap, one knock at 0.97, one seat. Total tap press-to-seat
≈ 90 (floor, only if he holds that long) + ~102 + ~110 ≈ 300ms
worst, inside the press on a normal tap-and-release ≥140ms.
A second touch during the mesh aborts it (buttonhole law):
cancel animator, spring home 250ms, nothing committed — abort is
only possible *before* the crossing; after it, TAKEN law applies.

### Release without commit (unchanged machinery, kept honest)

UP with `d < 0.84` and no latch: existing `animateSpringBack(0f)`
(280ms, `MechanicalOvershoot`, `:1214–1225`) stands — it was never
the problem. One fix inside it: it must start from the *displayed*
value and the finger's damped offset must not snap the thumb on
the first return frame; since `D(p)` is stateless and the animator
owns `dragFraction` from UP onward, this is already true if
`updateDrag` stops being called after UP (it does — `isDraggingThumb`
gate at `:1170`). No change beyond the deletion above.

### Amendment A, stated plainly (TRAVEL)

Register says TRAVEL 180dp. The code's travel is
`width − 88dp` ≈ 248dp at 360dp width, more on wider phones.
Zones, gate, catch and OFF-stop are all **fractions** in every
source file, so they survive intact; only the absolute number
dies. Amended law: **zones are fractions of actual `maxTravel`;
180dp is the floor** — if a device/foldable yields `maxTravel <
180dp`, the host must widen the bar (drop the 12dp box padding)
before any zone number is re-cut. Finger-commit distance is then
`0.92 · maxTravel` ≈ 228dp on the reference phone — long, and the
damped gain is what makes the last third of it feel short.

## (4) Code sites touched

| Site | Change |
|---|---|
| `ui/views/BlockitCoreViews.kt` — `SlideToStartView.onTouchEvent` (`:1153–1196`) | Hit-test + grab offset on DOWN; wire-limit clamp; UP no longer commits — latch or spring-back only; second-touch mesh abort |
| same — `updateDrag` (`:1198–1212`) | `D(p)` damping transform; gate crossing (0.68, hysteresis 0.65) + verdict re-read hook; commit crossing predicate → `latch()` |
| same — new code in the view | `D(p)` (~15 lines), seat spring integrator + knock-at-0.97 + `latch()` (~70), mesh animator (~45), phase/latch fields (~15) |
| same — deletions | Release-commit block incl. in-view seal (`:1177–1188`, ~12 lines) |
| same — `isArmedState` setter (`:1064–1071`) | Also resets `latched`/`phase` |
| `ui/screens/TactileDeckScreen.kt` (`:484–504`) | Pass `liveWireLive: () -> Boolean` + refusal callback into the view in factory/update; `onSlideCommitted` semantics unchanged (now latch-fired) |
| `data/MonolithRepository.kt` | **None.** `startSession` (`:869`) is called as-is, once, at the latch |

Explicitly NOT touched by this joint: the registries / `liveWarrant()`
(Ines/Otis joint — consumed here as one injected lambda), the strip
and refusal sentences (host joint — this view only clamps at 0.65
and reports), the badge/total (hub joint), the seal sound's t=0
re-homing (host/VM joint — this joint only deletes the view's copy).

## (5) Price in lines

| Piece | Gross | Net |
|---|---|---|
| Hit-test, grab offset, DOWN wire-limit | ~35 | ~35 |
| `D(p)` + gate edge + hysteresis + re-read hook | ~45 | ~40 (0.82 gate feel deleted) |
| Latch state machine + Cato seat integrator + knock at 0.97 | ~90 | ~78 (release-commit block deleted) |
| Tap mesh animator (shared latch) | ~55 | ~55 |
| Refusal clamp 0.65 + mid-drag death ease | ~25 | ~25 |
| Host wiring (lambda + callbacks) | ~20 | ~20 |
| **Total, this joint** | **≈270** | **≈250** |

One file rewritten in place + ~20 lines in the host. No new files,
no DB, no migration. (For scale: Ines priced the *whole* W4 slide
rewrite at ~700 gross in the same view; this joint is the commit
third of it. Tests — crossing-under-contact, roll-back-after-crossing
still commits, mesh abort before/after crossing, mid-drag wire
death — ≈60 lines, counted outside the joint, owed by whoever
builds it.)

## (6) Risks / device-only unknowns

- **Gain feel is a phone question.** 2/3 is derived, not felt. If
  on-device the damped zone reads as lag/stiction (Moss's named
  failure for the wheel), the one-constant fix is `DAMP_GAIN`
  0.667 → 0.70 — pre-authorised range, no re-spec needed. If it
  reads as *nothing*, drop to 0.60 (p_commit = 0.947 — too close
  to the end-stop; that direction has no headroom, which is itself
  the finding).
- **Spring frame cadence.** The (400/16) integrator is dt-based
  and clamps dt, but 60Hz vs 120Hz panels will seat a few ms
  apart and the knock's *time* moves with its position trigger —
  by design. What must be verified on device: no visible overshoot
  past the end wall (the 2dp compression absorbing it is a drawing
  claim until it's seen).
- **Lift-off roll 3–6dp is W4's number, never measured on his
  handset.** The under-contact law does not depend on its exact
  value — any roll > 0 breaks release-commit the same way — but
  the *margin* argument (commit at p = 0.92, 8% spare) assumes
  roll stays ≪ 0.08·maxTravel (≈20dp). Safe by an order of
  magnitude; still an assumption.
- **Haptic amplitude 0.85.** The existing feel channel is
  duration-bucketed (`fireFeel(8L)` etc.); true amplitude control
  depends on the device's vibrator supporting it. The knock may
  land as a plain 14ms pulse on his phone — timing and position
  survive, weight may not. Device-only.
- **Hit-test changes a shipped behaviour:** today any DOWN starts
  a drag, including a palm-edge DOWN far right that then commits
  on release. After this joint that touch does nothing (or starts
  the tap path). That is the intent — but it is a behaviour change
  a tired hand may notice before it understands it.
- **Accessibility:** the view has no custom accessibility action
  today and this joint adds none; a switch-access user cannot
  drag. The tap path (≥90ms) is the only non-drag route in. Flagged,
  not solved — solving it is a fork, not a line item.

## (7) Fork for Ren

**FORK — TRAVEL: fractions (recommended) vs literal 180dp.**
(A) Zones as fractions of actual `maxTravel`, 180dp as floor —
full-width bar, ≈248dp travel, commit at ≈228dp of finger travel.
(B) Literal: constrain the track so travel is exactly 180dp on
every device — the bar stops filling its host box (≈68dp of dead
width on the reference phone, thumb still 76dp), commit at 166dp.
**My recommendation: (A), unambiguously.** The register's 180dp
was written against an imagined geometry; the code's geometry is
real, the fractions are the actual law in every source, and (B)
buys a round number with a visibly short bar and a *shorter,
cheaper-feeling* gesture — the opposite of this joint's physics.
Register amendment needed if (A) is ratified: "TRAVEL: fractions
of maxTravel, floor 180dp."

No other fork. The tap-mesh segment ruling in §3 (crossing owns
0.84–0.92) is stated as a ruling, not a fork — if Marlowe prefers
the register's literal split (mesh completes to 0.92, spring only
assists 0.92 → 1.0), it costs ~10 lines and changes no number the
hand can feel; I recommend against it only because two latch paths
is how the current 0.82/release mess started.

## (8) Mail / wall note

Inbox at start: one letter — Rad (0718, ratchet-vs-symmetric-detent),
his counter on my invention-wave flagship (settle-phase symmetry;
kill-test proposed to Marlowe: 20 eyes-closed landings on 90 from
FLOW per mechanism). Read and moved to `read/`. It does not touch
this joint — the wheel's detent grain and the slide's commit are
different instruments — and his kill-test stands as the right way
to settle it when the wheel slice is built; no reply sent from
this seat in this run (the thread's next move is Marlowe's, and a
reply from a commit-slice seating would be noise). Posted one
signed note to the Commons wall: the commit ruling in one breath —
displayed = `dragFraction`, damped gain 2/3, commit under contact
at p = 0.92, TAKEN belongs to the spring, release-commit dies on
the lift-off roll — with the TRAVEL fork named for Ren.

— Vega, Wild · physics
