# RHEA — SLICE DE: THE TAP AS A FIRST-CLASS TOY

**Seat:** Rhea (Wild, seat 15, lens: toys — direct manipulation; what you
moved stays moved) · **Joint:** the tap path of the Deck slide, under the
law as handed down · **Coordinator:** Marlowe (Wild)

**The law I build under (not re-litigated):** TAP CAN COMMIT, with the
same gate/seal sequence as the drag · BRUSH FLOOR **90ms** (Orson's floor
over Cato's 40ms) · TAP MESH **140ms to 0.92** on cubic-bezier
(0.30, 0, 0.20, 1) + **110ms assist spring** to the seat (Cato's mesh,
REGISTER's numbers) · second touch during the mesh = spring-back in
**250ms** (buttonhole law — the abort) · LIVE WIRE: the verdict exists at
DOWN, synchronously (Ines §3, REGISTER: ≤5ms, four volatile/AppOps reads,
no deadline, no Park) · enforcement commands dispatch **only at LATCH**
(Opal's dispatch law, W4) · t=0 ≡ the PROOF-SEAL frame,
`t0 = max(last link CONFIRMED-SHUT, latch + 210ms, pawl-seated frame)`
(REGISTER) · GATE **0.68** (haptic only) · CATCH **0.84** · OFF-STOP
**0.65** · seat spring (400/16), latch knock **14ms @ 0.85 at 0.97 of
travel**.

---

## 1. VERDICT

**The tap is lawful and buildable as a first-class toy — as an ORDER
placed on the cap itself, never as a short drag and never as a track
press.** It stands on five rulings, all specced exactly in §3:

1. **A tap lives or dies at DOWN, on the cap.** The hit-test is the
   drawn cap (`thumbRect` on the DOWN frame) inflated 8dp in x, full
   track height in y. A down anywhere else on the track is a drag start
   and moves nothing until the finger moves — the teleport dies.
2. **A brush is contact without consequence.** Under 90ms the cap dips
   1dp in z, ticks once, and recovers. x-travel is exactly 0 for the
   whole press. It feels like touching the toy; it commits nothing,
   dispatches nothing, records nothing.
3. **The abort is always clean — provably.** The Live Wire verdict is a
   read, not a command; enforcement dispatches only at latch; an aborted
   mesh has by definition not latched. There is nothing to annul, no
   session row to delete, no sound to take back. Abort is a view-local
   animator cancel plus a 250ms spring home.
4. **Repeat taps carry zero state debt.** No counters, no lockout, no
   cooldown. A fumbled morning costs time (≈250ms per abort) and
   nothing else — a toy you can drop and pick up again is the whole
   point of a toy.
5. **Tap and drag are one machine, not two gestures sharing a track.**
   One DOWN dispatch, one verdict, one `dragFraction`, one fraction-
   setter, one assist spring, one latch, one callback, one t=0, one
   refusal. §3(e) enumerates the shared endpoints in code; if a builder
   writes a second copy of any of them, the slice has failed even if
   both copies work.

**Amendments to the handed law (A1–A6, §3.6):** the hit-test and
anti-teleport grab-offset are new (the law assumed them; the code lacks
both) · Cato's 300ms tap ceiling **dies** — no upper bound on a held
press · the seat knock is REGISTER's 14ms @ 0.85 at 0.97, not Cato's
18ms @ 1.0 · the 250ms abort spring-back is mesh-only and does not
replace the drag's ordinary early-release return · the view's seal
sound and the VM's pre-session seal sound both die — the seal fires
once, at t=0, for both entries · the pre-flight modal leaves the arm
path entirely; the tap's VM entry is not a new function, it is the
drag's entry, because there is only one entry left.

---

## 2. AGAINST THE REAL CODE (file:line, verified this run)

The current tree has moved since Ines §0 was measured (her slide was
669–855; Round-0/1 fixes have grown the file). All lines below are the
tree as read today; the crimes she named are all still present.

| # | Fact | Evidence |
|---|---|---|
| C1 | `SlideToStartView` is one Canvas view; cap geometry is computed identically in draw and touch: `inset = dp(6f)`, `thumbW = dp(76f)`, `maxTravel = width − 2·inset − thumbW`, `thumbRect = [inset + dragFraction·maxTravel, inset, +76dp, height − inset]` | `ui/views/BlockitCoreViews.kt:1046` (class); draw geometry `:1096–1099`; touch geometry `:1155–1157` |
| C2 | **No thumb hit-test on DOWN.** `ACTION_DOWN` sets `isDraggingThumb = true`, fires the DOWN tick, and calls `updateDrag(event.x)` unconditionally — any down anywhere on the view grabs the cap | `BlockitCoreViews.kt:1160–1167` (`fireFeel(4L)` at `:1164`, `updateDrag` at `:1165`) |
| C3 | **The teleport.** `updateDrag` centres the cap on the finger: `newFrac = (touchX − inset − thumbW·0.5) / maxTravel`, clamped 0..1. A down at the right end puts `dragFraction ≈ 1.0` in one frame | `BlockitCoreViews.kt:1198–1199` |
| C4 | **Commit is release-only at 0.82.** `ACTION_UP/CANCEL`: if `dragFraction >= 0.82f`, the view snaps to 1f, plays the seal itself, invokes the callback, then springs back to 0 **on success**. Below 0.82 it just springs back. So C2+C3+C4 compose the accidental tap: down at ≥0.82 + release = commit with zero drag, zero intent | `BlockitCoreViews.kt:1174–1194`; test at `:1177`, snap `:1179–1180`, seal `:1184`, callback `:1185`, spring-back `:1186` |
| C5 | The 0.82 constant is also the mid-drag "gate feel" (both directions, no hysteresis) and the tick-bucket source. REGISTER's 0.68 gate / 0.84 catch exist nowhere in the view yet | `BlockitCoreViews.kt:1200–1210` (`wasPastGate` at `:1200`, `fireFeel(8L)` crossing, buckets `(newFrac·8)`) |
| C6 | Spring-back is one animator: 280ms, `MechanicalOvershoot`, used for success and failure alike. No mesh animator, no abort path, no multi-pointer handling exists — `ACTION_POINTER_DOWN` is not handled at all, and a second finger's MOVE/UP corrupts the single-pointer stream the code assumes (`event.x` is pointer 0's x only by accident) | `BlockitCoreViews.kt:1214–1226`; `onTouchEvent` whole `:1153–1196` |
| C7 | Host wiring hardcodes the un-armed state and the per-block badge, and routes the callback to the VM's pre-flight entry: `view.isArmedState = false` on every recomposition update, `subMonoBadge = "${dialMinutes}M"`, `view.onSlideCommitted = onArmSession`, view height **68dp** | `ui/screens/TactileDeckScreen.kt:489–503` (badge `:498`, callback `:499`, height `:503`) |
| C8 | The VM's arm entry is the pre-flight modal gate: accessibility string-check → modal, else arm. The gesture pays first, the check happens after (Prima/Zug's kill-shot, in code) | `ui/MonolithViewModel.kt:879–887` (`requestArmWithPreFlightCheck`); modal state `:111–112`; host binding `MainActivity.kt:318` (`onArmSession = viewModel::requestArmWithPreFlightCheck`), modal composed `:530–539` |
| C9 | **Two seals, both before proof.** The view plays `playHarmonicSeal(true)` at release (`BlockitCoreViews.kt:1184`) and `armMonolithSession` plays it again **before** launching the coroutine that inserts the session | `ui/MonolithViewModel.kt:892–907` (`armMonolithSession` at `:892`, seal at `:895`) |
| C10 | Today's "readiness" is a Settings string (`isAccessibilityEnabled`, `MonolithRepository.kt:525`) and an AppOps check (`hasUsageStatsPermission`, `:493`) — the two synchronous ingredients Live Wire keeps; the service registries it needs (`instance` / `lastRootMono` / `protecting`) **do not exist yet** (Ines §3's price, not this slice's — see §5) | `data/MonolithRepository.kt:493`, `:525` |
| C11 | The helpers this slice needs already exist in the base view: `fireFeel` canned ladder (4L = latch tick) and `fireWeightedKnock(durationMs, amplitude01, pitchHz)` for real-parameter knocks (the dull brush/abort tick, the latch knock) | `BlockitCoreViews.kt:204` (`fireFeel`), `:240` (`fireWeightedKnock`) |
| C12 | Curve vocabulary has no 140ms and no (0.30, 0, 0.20, 1) interpolator: durations are {180, 260, 280, 420, 520}, curves A/B/C + overshoots. The mesh needs one new `PathInterpolator` constant — a 2-line addition, not a new system | `ui/theme/MonolithCurves.kt:16–36` |

**Geometry the spec is written against (shipped, computed from C1+C7):**
view height H = 68dp → cap height = 68 − 2·6 = **56dp**; cap width
**76dp**; at rest (`dragFraction = 0`) the cap occupies, in view-local
dp: **x ∈ [6, 82], y ∈ [6, 62]**. On a 360dp-wide handset the host's
12dp horizontal padding (TactileDeckScreen.kt, commit-bar Box) makes
view width W = 336dp, so `maxTravel = 336 − 12 − 76 = 248dp`. Filed
W4 geometry (cap 84×56, travel 180dp) is **not** shipped; every rule
below is therefore stated against `thumbRect` as drawn, with shipped
numbers as the worked example, so the spec survives the geometry
change without re-litigation. Derived positions on shipped geometry
(cap centre = 6 + 38 + f·248 = 44 + 248f): OFF-stop 0.65 → centre
205.2dp · gate 0.68 → 212.6dp · catch 0.84 → 252.3dp · seat 1.00 →
292dp (cap right edge flush at 330dp = W − inset).

---

## 3. AMENDED SPEC — THE TAP, EXACT

### 3(a) THE HIT-TEST — a tap is an order placed on the cap

Evaluated **once, at `ACTION_DOWN`, against `thumbRect` as drawn on
that frame** (not the rest rect — if the cap is mid-spring the test
uses where the cap *is*, because direct manipulation tests the thing,
not its home).

- **TAP-CANDIDATE** iff the DOWN point satisfies both:
  - `x ∈ [thumbLeft − 8dp, thumbRight + 8dp]` — the drawn cap inflated
    by exactly the touch slop, **8dp**, left and right; and
  - `y ∈ [0, H]` — the full track height. The view is 68dp tall and
    the cap already fills 56dp of it; vertical discrimination inside
    6dp margins is precision the hand doesn't have and the toy
    doesn't need. x is the discriminating axis; y is not.
  - Worked example, shipped geometry at rest: **x ∈ [0, 90dp]**
    (6 − 8 clipped at the view edge; 82 + 8), y anywhere in
    [0, 68dp]. The tap zone is 90/336 ≈ 27% of the track, ending
    115dp short of the OFF-stop centre and 162dp short of the catch
    centre — it cannot overlap any commit coordinate by construction.
  - Filed geometry (inset 8dp, cap 84dp) if built: x ∈ [0, 100dp] by
    the same formula. Formula governs; numbers follow.
- **DRAG START** iff DOWN is inside the track rect `[0, W] × [0, H]`
  but outside the tap-candidate zone. A drag start **moves nothing**:
  `dragFraction` is unchanged on the DOWN frame; the handler records
  `grabOffsetPx = event.x − capCentrePx` and MOVE sets the fraction
  from `(event.x − grabOffsetPx)` through the *same* centre formula
  `updateDrag` uses today (`BlockitCoreViews.kt:1199`) with the offset
  subtracted. The teleport (C3) dies for drags as well as taps — a
  down at the catch no longer puts the cap at the catch; the finger
  must *carry* it there, which is what makes the accidental commit
  (C4) impossible rather than merely discouraged.
- **Eligibility, additionally:** a tap-candidate requires the cap **at
  rest** at DOWN — `dragFraction == 0f` and no mesh/spring animator
  running. A DOWN on a moving cap is drag-only (§3(d)). A
  tap-candidate **converts irrevocably to a drag** on the first MOVE
  where `max(|dx|, |dy|) > 8dp` from the DOWN point (Cato's slop,
  kept); after conversion the gesture is judged by drag law alone
  (catch 0.84 under contact) and can never become a tap again.
- **Pointer ownership:** only the first pointer (`pointerId` of the
  initial DOWN) owns the gesture. Every later pointer is §3(c)
  material, never a co-dragger, never a second tap.

### 3(b) THE BRUSH — the 90ms floor, measured on the monotonic clock

- **Measurement:** `downUptime = event.eventTime` of the owning
  pointer's `ACTION_DOWN`; `heldMs = upEvent.eventTime − downUptime`
  at its `ACTION_UP`. `MotionEvent.eventTime` is `uptimeMillis` —
  monotonic, immune to wall-clock changes. Never
  `System.currentTimeMillis()`, never the second pointer's timestamps.
- **Boundary (REGISTER's words, made inclusive-exact):** `heldMs <
  90` → brush. `heldMs ≥ 90` → tap-eligible. **89ms commits nothing;
  90ms commits.** The floor is a named constant
  `TAP_COMMIT_FLOOR_MS = 90` (Orson's token) so the device trial can
  retune it once, in one place.
- **What the press renders from DOWN (tap-candidate and drag alike —
  contact is contact):** the cap sinks **1dp in z only**: `thumbRect`
  top +1dp, its pooled contact shadow offset 2.5dp → 1.5dp, top lit
  edge swaps to the bottom catch-line (Cato's press). **x-travel is
  exactly 0** for a tap-candidate until UP decides — the cap is
  pressed, never pulled. The existing DOWN tick stays
  (`fireFeel(4L)`, `BlockitCoreViews.kt:1164`). Live Wire is read on
  this same frame (§3(e)) — the verdict costs ≤5ms and the press
  never waits for it, because there is nothing to wait for.
- **What a brush (UP at <90ms) renders:** no mesh, no x-motion ever,
  no seal, no callback, no record. The 1dp sink recovers over **90ms**
  (the floor's own duration — the press unwinds at the speed it
  wound), and one dull tick lands on the UP frame:
  `fireWeightedKnock(10, 0.40f)` (Orson's brush strike; the helper
  exists, C11). The cached verdict is dropped unread-in-effect — it
  was a value in a field, and the field is cleared. A brush must feel
  like the toy noticed your hand and declined; a brush that renders
  nothing teaches the hand the surface is dead, and a brush that
  moves in x lies about travel that didn't happen. Z-dip + dull tick
  is the whole rendering, and it is enough.
- **No ceiling:** a tap-candidate held 300ms, 900ms, 3s — still a tap
  on UP, provided displacement never exceeded 8dp. Cato's 300ms
  ceiling dies (A3): it punished the hesitant press, and hesitation
  at 03:30 is not ambiguity — the finger stayed on the cap the whole
  time. Stillness on the cap is intent, however long it lasts.

### 3(c) THE ABORT — second touch during the mesh, and why it is always clean

- **The window:** the mesh interval — from the UP frame that launched
  it (G0) to the seat frame (G0 + 250ms), **exclusive of the seat
  frame**. An abort touch timestamped at or after the seat frame is
  not an abort; the latch has fired, HOLD is absolute, the touch is
  ignored by this view.
- **Which touch counts — both of these, either alone:**
  1. `ACTION_POINTER_DOWN` — a second finger coming down while the
     first press's mesh is running (the first finger is already up;
     in practice this is a second hand, a palm edge, a fumble); or
  2. a fresh `ACTION_DOWN` — the **same finger re-downing** after its
     UP, inside the window (the double-tap impulse — the likeliest
     abort on a real morning). Today the view doesn't handle
     `ACTION_POINTER_DOWN` at all (C6); both cases must route to the
     one abort handler. `ACTION_CANCEL` delivered during the mesh is
     also an abort (the system took the gesture; the machine does
     not finish what the system interrupted).
  The aborting touch is **consumed by the abort**: it does not read
  the wire, does not fire a DOWN tick, does not start a drag, does
  not become a new tap-candidate. It is a hand saying *stop*, and
  stop is all it says.
- **Start state:** the spring-back starts from **`dragFraction` as
  displayed on the abort frame** — the mesh animator is cancelled and
  its current animated value sampled (e.g. abort at G0+70ms ≈ 0.55 on
  the bezier; at G0+140ms exactly 0.92; inside the assist segment,
  wherever the spring had reached). No snap to 0.92, no snap to the
  abort finger's x, no jump of any kind: what you saw is where it
  returns from.
- **The return:** `dragFraction → 0` over **exactly 250ms**
  (buttonhole law), on the universal return spring's character
  (stiffness 260 / dampingRatio 0.78, Fen) time-boxed to 250ms —
  this replaces the shipped 280ms `MechanicalOvershoot` animator
  (`BlockitCoreViews.kt:1214–1226`) **for the abort path only** (A4).
  Teeth/trail/label all un-render for free: they are pure functions
  of `dragFraction` (§3(e), endpoint 3). One landing tick when the
  cap reaches rest: `fireWeightedKnock(10, 0.40f)` — the brush's
  dull strike, deliberately: abort and brush are the same word,
  *not committed*, in both entries.
- **What the abort does to the dispatched press state — nothing,
  because there is nothing to undo. Proof, from the dispatch law:**
  1. The Live Wire verdict (Ines §3) is a **synchronous read** of
     volatile fields and one AppOps check. It writes nothing,
     persists nothing, dispatches nothing — Ines prices it at zero
     DB writes by construction. A read cannot be annulled; it can
     only be forgotten, and the view forgets it by clearing one
     field.
  2. Enforcement commands — session insert, `startShield` — dispatch
     **only at LATCH** (Opal's law; W4: *"Acting pre-latch is acting
     on uncommitted intent"*). The latch **is** the seat frame. An
     abort exists only before the seat frame. Therefore, at every
     instant an abort can fire, the set of dispatched commands is
     **empty** — not pending, not prepared, empty.
  3. The seal sound attaches to t=0 (proof-seal frame), which is
     strictly after latch. No latch → no t=0 → no sound was played.
     (This is why A5 matters: under today's code the view plays the
     seal at *release*, `BlockitCoreViews.kt:1184` — an aborted mesh
     under that wiring would have a sound to take back. Under the
     law it cannot.)
  4. **Consequence, stated as the slice's theorem: abort is always
     clean before latch.** No annulment window (Ines C) is needed on
     the tap path, no tombstone, no compensating transaction. The
     entire abort cleanup is view-local: cancel mesh animator, clear
     `downUptime` / cached verdict / candidate flag, run the 250ms
     return, land the tick. The ViewModel is never notified that a
     tap was attempted, and that silence is correct — an aborted tap
     is a non-event in the data, exactly like a drag released before
     the catch.

### 3(d) REPEAT TAPS — tap, abort, tap again: no debt

- **State inventory after an abort completes** (the 250ms return has
  landed): `dragFraction == 0f` · mesh animator null · spring
  animator null · press depth 0 · candidate/owner fields cleared ·
  tick bucket reset (the existing DOWN handler already resets
  `lastTickBucket`, `BlockitCoreViews.kt:1163`). The view is
  bit-for-bit in its rest state. The VM was never touched (§3(c)).
  The session layer has no attempt record, because attempts are not
  recorded — only latches are.
- **The next tap** may begin the frame after the return lands, under
  the identical rules. **No cooldown, no strike count, no escalating
  friction, ever.** Price of a fumble, total: the partial press +
  partial mesh + 250ms return ≈ ≤590ms, then a fresh minimal tap
  (90ms press + 250ms mesh) seats at ≈930ms after the first DOWN.
  A man who aborts three times at 03:30 has spent ~2s and lost
  nothing else. That is the toy standard: dropping it costs you the
  drop, not a punishment.
- **The one guarded seam:** a DOWN landing **during** the 250ms
  return (cap in motion) is drag-only per §3(a) — it cancels the
  return and grabs with offset, but its UP can never be judged a
  tap. Without this rule, abort → instant re-press on a moving cap
  becomes a rhythm exploit and, worse, an accident path: the fumble
  that caused the abort continues into a commit. If that drag-only
  DOWN is simply pressed and released without moving, its UP is a
  non-event: the return spring resumes to 0 and nothing is recorded
  — the brush's grammar, reused.
- **Sound/haptic debt:** none accumulates. Mesh ticks coalesce at
  ≥12ms (Cato) and the abort kills the mesh's tick feed on the abort
  frame; the return is silent except its landing tick. Two aborts in
  a row cannot stack buzzes.

### 3(e) ONE MACHINE — the shared endpoints, in code

Tap and drag are the same machine with two drivers (finger, profile)
writing one state. These endpoints are shared **by construction** —
one function each, called by both paths:

1. **One DOWN dispatch.** `SlideToStartView.onTouchEvent`
   `ACTION_DOWN` (`BlockitCoreViews.kt:1153`): cancel running
   animator → hit-test (§3(a)) → branch candidate/drag → **read the
   wire**: `liveWarrant()` synchronously (Ines §3; repository, fed
   by the service registries). Both entries read it here, on the
   gesture's own thread, ≤5ms. There is no tap-specific entry point
   in the view and none may be added.
2. **One verdict, two readings.** Drag re-reads the wire at the
   **0.68 gate crossing** (the verdict is never older than the
   gesture). The tap has no travel, so it re-reads at **UP (mesh
   launch, G0)**: if the wire died during the press, the mesh does
   not run — dull knock, refusal sentence, spring home from press
   depth, the drag's mid-drag wire-death degraded identically
   (travel limit collapses to OFF-stop 0.65 for the drag; for the
   tap at fraction 0 the limit collapse *is* "no mesh").
3. **One state, one setter.** `dragFraction` is the only travel
   state. Finger MOVEs and the mesh animator both write it through a
   single `setFraction(f)` (extracted from `updateDrag`,
   `BlockitCoreViews.kt:1198`) which alone owns: the gate tick
   (`fireFeel(8L)` on crossing the catch coordinate — migrated from
   0.82 to REGISTER's numbers, C5), the per-bucket ticks, and
   `invalidate()`. Because the mesh passes through 0.68 and 0.84 on
   the same setter, a tap *feels* the gate and the catch in its
   rattle exactly as a drag does — the ear cannot tell the driver
   apart, which is Cato's acceptance test restated in code.
   Everything drawn — trail, label fade (`1 − 1.3·frac`), marker —
   is already a pure function of this one field in `onDraw`.
4. **One final segment.** At 0.92 the mesh hands to the **same
   assist spring** the drag enters at the catch: spring (400/16),
   110ms, to 1.00, 1.5dp overshoot. One `startAssistSpring()`
   function; the drag reaches it from under the finger, the tap
   from the profile. Byte-identical frames from 0.92 on.
5. **One latch.** The seat frame (first frame rendered at travel
   1.0) invokes one `onLatch()`: latch knock
   `fireWeightedKnock(14, 0.85f)` fired inside the seat at **0.97 of
   travel** (REGISTER — this knock belongs to the shared segment,
   so tap and drag knock identically), then the view's single
   callback `onSlideCommitted` (`BlockitCoreViews.kt:1185` — kept,
   one lambda, gesture-blind). Enforcement commands dispatch here
   and only here (§3(c)).
6. **One VM entry — and the pre-flight modal dies.** Host:
   `view.onSlideCommitted = onArmSession`
   (`TactileDeckScreen.kt:499`) → `MainActivity.kt:318` currently
   binds `onArmSession` to `requestArmWithPreFlightCheck`
   (`MonolithViewModel.kt:879`). Under Live Wire that function has
   no job: the check it performed after the gesture now happens
   synchronously at DOWN, in the hand, for free — a modal after a
   latched gesture is a second decision HOLD forbids. **The tap
   entry becomes: nothing.** `onArmSession` rebinds directly to the
   (renamed, seal-stripped) `armMonolithSession` body — insert
   session + `startShield` — and that single function is the entry
   for tap and drag alike. `requestArmWithPreFlightCheck`,
   `dismissPreFlightCheck`, `_preFlightCheckVisible`
   (`MonolithViewModel.kt:111–112, 879–890`) and the modal wiring
   (`MainActivity.kt:530–539`) leave the arm path; the modal's
   content survives only as the *repair* surface behind the refusal
   sentence's TAP TO FIX, reached from chrome, never from a
   committed gesture.
7. **One t=0, one seal.** Neither entry starts the session clock or
   plays the seal. t=0 is the controller's PROOF-SEAL frame
   (REGISTER's formula) and the seal (`playHarmonicSeal`) fires
   exactly once, there, from the VM/controller — for both entries.
   Today's double seal (view `:1184` + VM `:895`, both pre-proof,
   C9) dies for both entries in the same edit. Eyes closed, the two
   entries are indistinguishable from DOWN-tick to seal: same press
   tick, same gate/catch ticks (endpoint 3), same latch knock, same
   210ms seal cadence, same t=0.
8. **One refusal.** Wire dead at DOWN: drag travel limited to 0.65,
   tap mesh refused — same dull knock (10ms @ 0.40), same 300ms
   hold, same reserved-slot sentence, same spring home. A tap never
   seals what a drag would refuse; a drag never refuses what a tap
   would seal. Same wire, same words.

### 3.6 AMENDMENTS LEDGER (A1–A6)

| # | Amendment | Displaces |
|---|---|---|
| A1 | Hit-test (§3(a)) + grab-offset drag; DOWN never moves the cap | Shipped teleport (C2/C3); the law assumed a hit-test nobody had specced against real geometry |
| A2 | Press renders z-only: 1dp sink, x frozen for tap-candidates until UP | Cato's press (kept in spirit; his x-freeze was implicit — here it is law, because x-motion before UP would make every press a drag) |
| A3 | No tap ceiling: held ≥90ms + slop ≤8dp commits on UP at any duration | Cato's 40–300ms window (40ms already dead by the floor; the 300ms ceiling dies here) |
| A4 | Abort return = 250ms, abort-path only; ordinary early-release drag return keeps the universal spring (~320ms) | Shipped single 280ms overshoot animator for all returns (C6) |
| A5 | Both pre-proof seal sounds die; seal fires once at t=0 from the controller | `BlockitCoreViews.kt:1184`, `MonolithViewModel.kt:895` |
| A6 | Latch knock = 14ms @ 0.85 at 0.97 (REGISTER) on the shared segment | Cato's 18ms @ 1.0 seat knock on the tap table — REGISTER governs where the parts disagree; the knock's *placement* (inside the seat) is Cato/Fen's shared ground and stands |

Numbers **not** amended, re-stated so the builder never has to
choose: brush floor 90ms · mesh 140ms bezier (0.30, 0, 0.20, 1) to
0.92 · assist 110ms (400/16) · abort window = mesh interval · abort
return 250ms · slop 8dp · hit-test inflation 8dp · press sink 1dp ·
gate 0.68 · catch 0.84 (commit under contact, drag) · OFF-stop 0.65 ·
refusal hold 300ms · dull tick 10ms @ 0.40 (brush, abort landing,
refusal — one sound for *not committed* in every entry).

---

## 4. CODE SITES

| Site | Change |
|---|---|
| `ui/views/BlockitCoreViews.kt` — `SlideToStartView.onTouchEvent` (`:1153–1196`) | Hit-test + pointer ownership + grab-offset; DOWN stores `downUptime`, candidate flag, cached verdict via callback; MOVE conversion at 8dp; UP brush/tap/drag judgement; `ACTION_POINTER_DOWN`/`ACTION_CANCEL` → abort handler during mesh |
| `ui/views/BlockitCoreViews.kt` — `updateDrag` (`:1198–1212`) | Split into `setFraction(f)` (shared: gate/catch ticks, buckets, invalidate) + drag driver (offset formula); thresholds 0.82 → gate 0.68 tick / catch 0.84 (shared with the drag slice — see §5 note) |
| `ui/views/BlockitCoreViews.kt` — `onDraw` (`:1085–1150`) | Press sink: thumbRect/shadow offset by press depth (0/1dp) for a held candidate |
| `ui/views/BlockitCoreViews.kt` — `animateSpringBack` (`:1214–1226`) | Parameterised (duration + interpolator); abort path passes 250ms; new `meshAnimator` + `startAssistSpring()` beside it |
| `ui/theme/MonolithCurves.kt` (`:16–36`) | +1 `PathInterpolator(0.30f, 0f, 0.20f, 1f)` mesh profile constant |
| `ui/MonolithViewModel.kt` (`:879–907`) | `requestArmWithPreFlightCheck`/`dismissPreFlightCheck`/pre-flight flow deleted from arm path; arm entry = insert + `startShield`, pre-session seal removed (seal moves to t=0, controller's job — priced in the seal slice, not here) |
| `ui/screens/TactileDeckScreen.kt` (`:489–503`) | Pass the wire-verdict supplier into the view (one callback property); callback binding unchanged in shape (`:499`) |
| `MainActivity.kt` (`:318`, `:530–539`) | Rebind `onArmSession` to the direct arm entry; modal recomposed as repair surface only (or deleted with its state if the repair surface is specced elsewhere — Ren's call, §7) |

**Not in this slice's sites (dependencies, priced by their owners):**
the Live Wire registries + `liveWarrant()` (Ines §3: ≈175 lines
across AccessibilityService / ShieldService / Repository) — this
slice consumes its verdict through one `() -> Boolean` supplier and
is indifferent to how the wire is built; the PROVING controller /
t=0 gating (Ines §1 item 7).

## 5. PRICE (lines, against the real files)

| Piece | Gross | Net |
|---|---|---|
| Hit-test, pointer ownership, grab-offset drag, DOWN state (view) | ~55 | ~40 |
| Press-depth rendering in `onDraw` | ~15 | ~15 |
| Brush judgement + recovery (UP path) | ~20 | ~20 |
| Mesh animator (140ms profile → 0.92 via `setFraction`) | ~40 | ~40 |
| Shared assist spring extraction (drag + tap call one function) | ~35 | ~10 |
| Abort handler (POINTER_DOWN/CANCEL, sample-and-return 250ms) | ~40 | ~35 |
| Verdict supplier property + two readings (DOWN, gate/UP) | ~15 | ~15 |
| VM: pre-flight deletion + arm entry strip (−35 of modal plumbing) | ~30 | **−20** |
| Host + MainActivity rewiring | ~20 | ~10 |
| **Total, this slice** | **≈270 gross** | **≈165 net** |
| Tests (hit-test boundary at 90dp ±1, brush 89/90, abort sampling mid-mesh, repeat-tap state cleanliness, teleport regression) | ~90 | ~90 |

Cross-check: Ines §1 priced the W4 tap path at ~180 lines inside the
old committee machine; under Live Wire the Park/poll handling she had
to include is gone and the hit-test/anti-teleport work she didn't
price is in — the totals agree within noise, which is the honest
sign. Files touched: 4 (+ tests). No migration, no schema, no new
colour, no new sound — every haptic above routes through `fireFeel` /
`fireWeightedKnock` (C11).

## 6. RISKS / DEVICE-ONLY UNKNOWNS

1. **His real tap speed vs the 90ms floor** (Orson's own anti-case,
   inherited): a practised 200-morning tap may land under 90ms and
   read as a brush. Mitigation is built in (single constant,
   retune-once on device measurement) — but the measurement is
   device-only and the floor must move to his hand, not to argument.
2. **Second-pointer delivery to a Canvas view inside Compose.**
   `ACTION_POINTER_DOWN` reaching `SlideToStartView` depends on the
   AndroidView interop and the parent's intercept behaviour
   (`requestDisallowInterceptTouchEvent(true)` is already set on
   DOWN, `:1161`). If the second finger's down is stolen by a parent
   gesture, the abort silently doesn't fire and the mesh completes —
   fail-safe direction (commit, not loss), but the buttonhole law
   would be unproven until tried on hardware.
3. **Palm aborts.** A palm edge landing on the track during the mesh
   is, by §3(c), an abort — indistinguishable from intent at the
   event level. If device trials show grip-palms aborting real taps,
   the narrowing in §7 is the pre-agreed fix.
4. **Abort velocity pop.** The spring-back starts from the sampled
   fraction with zero initial velocity; the mesh at mid-bezier is
   moving fast (≈2.6× travel/s at peak). The eye may catch a one-
   frame velocity discontinuity at the turn. A velocity-seeded
   spring fixes it if visible — not specced sight-unseen.
5. **Clock alignment.** `heldMs` uses `eventTime` (uptime); animator
   frames run on the choreographer clock. The abort window's edge
   (abort at exactly the seat frame) is decided by animator state,
   not by comparing the two clocks — specced that way deliberately,
   but the seat-frame race (abort DOWN and seat callback in the same
   frame batch) must resolve latch-wins; verify in tests.
6. **1dp press sink at low refresh / heavy jank** may render as 0–2
   frames — the dip could be invisible on a bad morning while the
   tick still lands. Acceptable (haptic carries it), unproven on
   device.
7. **Wire death during a 90ms press** is a rare-enough event that
   its refusal path (mesh refused at UP) may never be exercised
   outside tests; it is specced by symmetry with the drag, not by
   observed need.

## 7. FORK FOR REN (one, with my recommendation)

**Fork — abort strictness: any second touch on the view aborts
(§3(c) as specced), vs only a second touch landing inside the track's
inner bounds (inset by 8dp) aborts, with edge/palm contacts ignored.**
The law says "second touch during the mesh"; both readings honour
it. **My recommendation: ship §3(c) strict (any second touch), and
let the device trial earn the narrowing.** Strict is the buttonhole's
whole conscience — a closure you can stop with any part of either
hand — and a false abort costs ≤590ms and nothing else (§3(d)),
while a missed abort costs a seal he tried to stop, which HOLD's
spirit prices far higher. If palm aborts show up on his hand, narrow
to inner-bounds in one constant. Do not pre-narrow on my guess.

No other fork. In particular, not a fork: the VM entry (§3(e)6). The
modal's death on the arm path follows from Live Wire + HOLD by
construction; if Ren wants the repair-surface question decided
separately, it is severable from this slice without touching a
number above.

## 8. MAIL / WALL NOTE

- **Inbox:** read at start — one unread letter, Teo (0711, tray-stack
  standing), plus three already in `read/`. Teo's letter is a hub
  fight (his charge 2: the hand judges the stack as duration whatever
  the census says) — outside this slice's joint and not silently
  dropped: replied in his inbox this run, conceding nothing, noting
  the charge stands for the hub merge and that this seating was the
  tap only. All read letters moved to `mail/rhea/read/`, none
  deleted.
- **Mail sent:** Marlowe (coordinator) — verdict + price + fork, one
  letter, thread `tap-first-class-toy`. Teo — reply as above, thread
  `tray-stack-standing`.
- **Wall:** one signed entry appended (`## [08:14 IST] Rhea — Wild,
  toys`) with the verdict in five lines: hit-test on the cap, brush
  as z-only contact, abort provably clean, zero debt on repeat, one
  machine — and the fork named for Ren.

— Rhea, Wild (toys) · Slice DE · filed to
`plan/slice-de-parts/rhea.md`
