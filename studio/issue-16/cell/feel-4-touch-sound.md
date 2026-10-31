# Issue #16 — The Pill: TOUCH & SOUND

**Cell:** Pastille's pill cell · **Seat:** Feel Specialist-4 (Touch & Sound)
**Scope only:** every moment a finger or an ear meets the pill — tap
targets, drag initiation, the sounds that exist, whether expiry is
felt, and the finished spec for grant / first drag / snap /
early-end / expiry. Motion physics are Feel-1's seat; this doc
prices the touch targets and every sound/haptic, and touches motion
only where a sound or a tap verb hangs off it.
**Sources read:** `PillController.kt`, `PillFace.kt`, `PillTheme.kt`,
`TactileSoundEngine.kt`, `MonolithShieldService.kt` (construction
site only), `MonolithRepository.kt` (peek verbs only), and
`MonolithViewModel.kt` (`start2MinutePeek` :2155, `end2MinutePeekEarly`
:2194, the peek ticker :851, `triggerAirlockIntercept` :2124) — all
under `builds/pill/Focus-app/app/src/main/java/com/example/`.
**Rule honoured:** nothing compiled, nothing run.

---

## 1. Verdict

### THE ONE CONTROL THAT MATTERS IS THE SMALLEST, THE SILENTEST, AND THE EASIEST TO MISS — AND EXPIRY HAS TWO CLOCKS THAT DISAGREE ABOUT WHETHER IT MADE A SOUND.

The early-end ✕ — the control a distracted man reaches for, mid-scroll,
one-handed, to hand the peek back — is a **≈21 × 30 dp** hit box
(27 % of the area of a 48 dp target) whose tap fires **no sound and no
haptic at all**. Miss it by 3 dp and the tap lands on the pill body,
whose verb is not "end peek" but "return to Monolith" — the lease
keeps burning. Meanwhile the sound that *looks* like the early-end
sound (`playHarmonicSeal`, MonolithViewModel.kt:2197) is reachable
from exactly one caller — the ViewModel's own expiry ticker — and
the ViewModel's phantom countdown keeps ticking, detenting, and
finally clunking **after** a hand-back it never heard about,
re-anchoring the 20-minute cooldown late as it goes. The pill the
user actually touches (the overlay, driven by `PillController`) holds
**no reference to the sound engine at all**: in the overlay's world,
grant is unheard, drag is unheard, snap is unheard, hand-back is
unheard, and expiry is a label quietly swapping from the peeked app's
name to `SESSION` while the numbers jump from `00:01` to the session's
remaining time. That morph is a feel decision the code made by
accident. This spec unmakes it.

---

## 2. What exists today — evidence

### 2.1 The ✕ target: ≈21 × 30 dp, asymmetric padding, no expansion

`PillController.kt:204-212` (`buildWindow`):

```kotlin
val close = TextView(context).apply {
    text = "✕"
    setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
    setPadding(dp(10), dp(6), dp(2), dp(6))
    visibility = View.GONE
    setOnClickListener { onHandBack() }
}
```

Size arithmetic (font scale 1.0; the glyph is set in Anonymous Pro via
`PillTheme.figures()`, advance ≈ 0.6 em):

| | Width | Height |
|---|---|---|
| Glyph at 15 sp | ≈ 9 dp | ≈ 18 dp line |
| Padding | 10 + 2 = 12 dp | 6 + 6 = 12 dp |
| **Hit box** | **≈ 21 dp** | **≈ 30 dp** |
| 48 dp minimum target | 48 dp | 48 dp |
| **Shortfall** | **44 % of width** | **62 % of height; 27 % of area** |

- No `TouchDelegate`, no `minWidth`/`minHeight`, no hit-rect inflation
  anywhere in the file. The clickable area is the view bounds, full stop.
- The padding is asymmetric (10 dp left, 2 dp right, against the root's
  own 12 dp right padding at :216), so the *visual* ✕ sits near the
  pill's right edge while its *tappable* sliver is skewed left of where
  the eye aims. Aim and hit disagree by design.
- The ✕ exists only on the Peek face (`render`, :174-178 sets
  `VISIBLE`; Session and Break set `GONE` at :167, :172). Correct —
  but it means the target appears exactly when the user is at his
  most distracted, in a strange app, with the least aim he will have
  all day. That is precisely when 21 dp fails.

### 2.2 The miss penalty: a missed ✕ is a different verb

The root carries `DragListener` (`:225`, :314); the ✕ is a child with
its own click listener, so a tap *inside* the 21 × 30 sliver wins.
A tap 3 dp outside it falls through to the root: `ACTION_UP` with
`moved == false` fires `onBodyTap()` (:344-349 → :298), which launches
Monolith. Consequences of one miss:

1. The peek is **not** ended — `recordPeekEnded` is never called, the
   lease burns on, the peeked app stays admitted.
2. The user is yanked out of the app anyway, so the gesture *feels*
   like it worked. He believes the peek is over; the machine believes
   it is running. A truth failure delivered by a touch failure.
3. He loses the early-end benefit: hand-back re-anchors the cooldown
   to *now* (`MonolithRepository.kt:1197-1205`); a missed tap leaves
   the anchor at the scheduled lease end.

For scale, the body itself is ≈95–130 dp wide × ≈46 dp tall
(label 9 sp + time 16 sp column, root padding 8 dp top/bottom) — an
adequate target carrying the *wrong* verb for a Peek-face miss.

### 2.3 Drag initiation: moves on first touch — after a silent dead zone

`DragListener` (`PillController.kt:314-354`):

- **No press feedback at all.** `ACTION_DOWN` (:324) stores coordinates
  and cancels the snap animator — nothing visual, nothing audible,
  nothing haptic tells the finger it has hold of the object. A press
  that goes nowhere reads as the pill ignoring you until movement
  crosses the slop or `ACTION_UP` fires the tap verb.
- **Slop is `|dx| + |dy| > dp(6)`** (:336) — Manhattan distance.
  Diagonally that trips at ≈4.2 dp per axis, *below* the platform's
  scaled touch slop (≈8 dp). A tremor, a step, a one-handed grip
  shift converts an intended body tap (return-to-focus) into a drag,
  and the tap verb is silently swallowed — release just snaps the
  pill a few dp sideways. The pill "argues" with a shaky tap.
  (Divergence from Feel-1 §2.1, which would keep 6 dp: Feel-1 judged
  it as a drag number; as a *tap-preservation* number it is too
  small, because on this object a tap is a verb, not a selection.
  Pastille to rule; §3.2 prices the change at 2 lines.)
- **Once tripped, tracking is honest:** `params = start + full delta`
  (:338-339), so the 6 dp of dead travel is not lost — the pill
  catches up to the fingertip with no jump. Keep.
- **No bounds during drag** (:338-339 write `params.x/y` unbounded —
  Feel-1 §2.1 has the physics). The touch consequence is mine: the
  ✕ can be dragged partly or wholly off the glass, and since Y is
  never recovered on release, the pill — and its only early-end
  control — can be stranded unreachable until the lease dies.
- `ACTION_CANCEL` correctly routes to snap, never to tap (:344-349).

### 2.4 Sound inventory: every `soundEngine` call in the pill's life

The engine's law (`TactileSoundEngine.kt:39`): every call bundles a
waveform haptic *and* a PCM transient from the same call site. So
"sound" below always means sound + haptic together.

| Moment | Call | File:line | Voice |
|---|---|---|---|
| Airlock opens (context, not the pill) | `playMechanicalLatch(true)` | MonolithViewModel.kt:2124 | Latch: haptic [0,11,26,9] ms @ [215,0,150]; 760 Hz, 16 ms, gain 0.54 |
| **Grant** | `playMechanicalLatch(true)` | MonolithViewModel.kt:**2159** | Identical latch — the same voice twice within one flow, seconds apart |
| **Running** | `playRotaryDetent(760.0)` every 15 s of remaining time (105, 90, … 15) | MonolithViewModel.kt:**859** (ticker :851-865) | Detent: haptic [0,7,5] @ [180,70]; 6 ms @ 760 Hz, gain 0.38 |
| **Expiry (VM path)** | `playHarmonicSeal(arming = true)` via `end2MinutePeekEarly()` | MonolithViewModel.kt:**2197**; sole caller is the ticker at :856 | Seal: haptic [0,24,38,16] @ [255,110,175]; 360 Hz, 48 ms, gain 0.62 — the *session-arming* voice |
| **Early end via pill ✕** | — **nothing** — | PillController.kt:211 → :289-292 (`onHandBack` → `repository.recordPeekEnded`) | Silence. No engine reference exists to call |
| Drag pickup / move / snap / birth / death (overlay) | — **nothing** — | PillController.kt, whole file | Silence. `grep soundEngine\|Vibrat\|haptic` in this file: zero hits |

Three structural facts underneath the table:

1. **`PillController` cannot make a sound.** Its constructor
   (:52-58) takes context, repository, dao, scope, ownPackage — and
   `MonolithShieldService.kt:111-117` constructs it without any
   engine. Every overlay moment is silent *by wiring*, not by choice.
2. **All the sounds that do exist hang off the ViewModel's private
   counter** — `_floatingPeekPill`, the 1 Hz decrementing state that
   `PillFace.kt`'s own header names "the dead contract's state… the
   two clocks could disagree." The detents and the expiry seal are
   only as alive as that counter.
3. **Grant's sound leads its object.** The latch fires synchronously
   at :2159; the lease is written inside a launched coroutine
   (:2167-2168) and the pill itself appears on `PillController`'s
   next 1 s tick. The ear is answered instantly (right — the sound
   answers the GRANT tap in the airlock), but nothing confirms the
   *pill's* arrival in the peeked app up to ~1 s later.

### 2.5 The phantom: hand-back does not stop the ViewModel's pill

`onHandBack` (PillController.kt:289-292) talks only to the repository.
Nothing clears `_floatingPeekPill`. After a ✕ hand-back at, say, 0:30:

- the overlay correctly flips to the Session face (lease zeroed);
- the VM keeps counting its phantom, firing detents at its 15 s marks
  **with no pill on screen to justify them**;
- at its own 120 s the phantom calls `end2MinutePeekEarly()` (:856):
  a full HarmonicSeal clunk out of nowhere, plus a *second*
  `recordPeekEnded` that re-anchors the cooldown to the phantom end —
  stealing up to the entire unused remainder (here 90 s) of cooldown
  credit the hand-back had earned;
- until the phantom dies, `triggerAirlockIntercept` computes
  `canUse2MinutePeek = false` off `!_floatingPeekPill.value.isActive`
  (:2129-2131) — a re-intercept during those seconds offers no peek
  even though the Room lease is dead and gone.

One gesture, three lies, all audible or visible. This is the single
worst touch-&-sound defect in the pill.

### 2.6 Expiry as shipped: two expiries, neither designed

- **VM path:** at its counter's zero, the HarmonicSeal fires (above).
  Wrong voice twice over: it spends the session-seal's reserved
  clunk on a 2-minute peek ending, and `arming = true` sounds like
  something being locked *in* at the moment a lease lapses.
- **Overlay path:** at the tick where the derivation stops seeing the
  lease, `render` simply reprints the pill as a Session face
  (:161-167) — label swaps to `SESSION · BLOCK n/m`, the clock jumps
  from `00:01` to the session's remaining (e.g. `47:32`), the ✕
  vanishes. No sound, no haptic, no exit. If the session has also
  ended, the pill cuts to absent via `removeWindow()`. A user deep
  in the peeked app — the exact person expiry exists for — gets no
  signal from the pill at all; whatever he feels comes from the
  shield re-intercepting the app, a different surface's voice.
- **Nothing warns.** Detents stop at 15 s remaining; the last
  quarter of the fuse — the only part where a warning could change
  behaviour — is the quietest part.
- What *is* right: the per-second countdown itself is silent (no
  1 Hz tick — keep it that way forever), and the 15 s detent cadence
  is a good heartbeat. It is just wired to the wrong clock.

### 2.7 Accessibility footnote

The window is `FLAG_NOT_FOCUSABLE`; the ✕ has no `contentDescription`,
the body tap is a raw touch listener with no `performClick` path.
A screen-reader user can neither find nor fire the early-end control.
Priced in §4 as a two-line partial fix (description + delegate);
full overlay a11y is out of this seat's price.

---

## 3. The finished spec — moment by moment

Engine plumbing assumed by everything below (priced once, §4 #1):
pass a `TactileSoundEngine` into `PillController` (the shield service
creates/passes it at MonolithShieldService.kt:111). Every voice below
is an *existing* engine method — no new synthesis, no new reserved
voice is coined, and the seal / completion voices are explicitly
**not** spent anywhere in the pill's life.

### 3.1 GRANT — keep the latch, let it answer the tap

- **Keep** `playMechanicalLatch(true)` at MonolithViewModel.kt:2159,
  exactly where it is. It is synchronous with the GRANT tap, and the
  two-stage latch *is* the right metaphor: a door deliberately
  unlatched for 120 s. The haptic twin means the grant is felt even
  muted (engine law: muting loses timbre, never the event).
- **No second sound at pill birth.** The peeked app launching *is*
  the visual confirmation; Feel-1's slide-in birth gives the arrival.
  A birth chime on top of latch + launch is noise.
- **De-duplicate the flow (recommendation, Pastille to rule):** the
  airlock's opening latch (:2124) and the grant latch are the same
  voice seconds apart, which teaches the ear that latch = nothing
  in particular. Change the *intercept* opening to
  `playRotaryDetent(580.0)` (the pitch `selectDistractionReason`
  already uses at :2147's neighbourhood) so the latch is heard
  once per peek, at the moment of commitment. 1 line, in the VM,
  outside the pill's files — flagged, not assumed.

### 3.2 FIRST DRAG — acknowledge the grab, protect the tap

- **Pickup tick:** on the first `MOVE` that crosses slop, fire
  `playRotaryDetent(520.0)` once per drag (flag reset on `DOWN`).
  Lower pitch than the 760 Hz heartbeat detents so the ear files it
  as *handling*, not *time*. Bundled haptic [0,7,5] is the grab
  confirmation `ACTION_DOWN` cannot give without lying (a buzz on
  every touch-down, drag or not, would cry wolf).
- **No sound while moving, ever.** No ratchet, no edge-crossing
  ticks. A countdown is an instrument; dragging it is handling,
  not playing it.
- **Slop: raise to the platform's.** Replace `|dx|+|dy| > dp(6)`
  (:336) with Euclidean distance against
  `ViewConfiguration.get(context).scaledTouchSlop` (≈8 dp). A tap
  with 5 dp of tremor must still be a tap — on this pill a swallowed
  tap is a swallowed verb (§2.3). Keep the full-delta catch-up
  (:338-339) unchanged.
- **Reachability clamp (touch half of Feel-1's bounds):** during
  `MOVE`, hard-clamp the *rendered* position so the whole pill —
  ✕ included — never leaves the glass: x ∈ [0, screenW − width],
  y ∈ [0, screenH − height]. Rubber-banding beyond the walls is
  Feel-1's call; the invariant that the early-end control is
  always reachable is this seat's, and it is not negotiable.

### 3.3 SNAP — silent for a nudge, one knock for a journey

- On snap **settle** (animator end / Feel-1 spring rest): if the
  snap travelled **> 120 dp**, fire one `playSeatKnock(10, 0.5f, 320f)`
  — the voice the codebase already uses for gate landings at that
  weight (TactileSoundEngine.kt:624's own comment). Under 120 dp:
  silence; a nudge landing is felt through the motion, and a knock
  per reposition would grate within a day (Feel-1 §3.5's caution,
  adopted — but priced here for the long throw only, where the ear
  expects an arrival the eye may have missed mid-flick).
- Nothing at snap *start*. The release is the finger's moment;
  the landing is the object's.

### 3.4 EARLY-END PRESS — 48 dp, a latch shut, an instant answer

1. **Target: 48 × 48 dp hit area, glyph unchanged.** Use a
   `TouchDelegate` on the root, set after layout, inflating the
   close view's rect by 14 dp left, 13 dp right, 9 dp top,
   9 dp bottom (≈21×30 → ≈48×48). The pill's face does not grow a
   hair; only its forgiveness does. (Growing the view with padding
   instead would fatten the whole pill to ~64 dp tall — rejected.)
   Add `contentDescription = "End peek now"` on the close view.
2. **Voice: `playMechanicalLatch(false)`.** On `onHandBack`, fire
   the latch's release form — haptic [0,9,22,10] @ [165,0,195],
   520 Hz seat — *before* the repository call, on the caller's
   thread, so the finger is answered in the same frame it lifts.
   The latch that opened the peek closes it; the pair is learnable
   in one use. **Explicitly not** `playHarmonicSeal`: the seal is a
   session-scale voice, and §2.6's misuse of it is removed, not
   repeated (see §3.5 #3).
3. **No 1-second doubt:** after `recordPeekEnded`, trigger an
   immediate re-derive (one `tick()` launched on the controller's
   scope) instead of waiting out the 1 s ticker — the Peek face
   must be gone within a frame or two of the press, not up to a
   second later while the user wonders if the ✕ worked.
4. **Miss forgiveness (recommendation, Pastille to rule):** on the
   Peek face, make a body tap do **both** verbs — hand back, then
   return (`onHandBack()` then `onBodyTap()`, 3 lines). Reasoning:
   during a peek, "take me back to focus" and "end this peek" are
   the same human intention; the lease burning on after he has
   returned to Monolith serves nobody, and §2.2's miss becomes
   harmless instead of deceptive. If Pastille keeps the verbs
   separate, item 1 stops being a recommendation and becomes
   mandatory — it is already priced either way.

### 3.5 EXPIRY — felt once, warned once, never a seal

All of this lives in `PillController`, driven by the *derivation's*
remaining seconds — the one true clock — replacing the VM ticker's
role in the pill's life:

1. **Warning at 10 s:** when a rendered Peek face crosses
   `remainingSeconds == 10` (once per lease; a `warnedLeaseEnd`
   stamp guards re-fire), fire `playRotaryDetent(880.0)` — the
   familiar detent raised a step, the fuse's only raised voice.
   One warning, not a 5-4-3-2-1 drumbeat.
2. **Expiry knock:** when the derivation flips Peek → not-Peek and
   the previous face was a *visible* Peek not ended by `onHandBack`
   (a `handedBack` flag set in §3.4 suppresses it), fire
   `playSeatKnock(12, 0.5f, 320f)`: a single time's-up knock with
   its haptic twin. If the pill was Absent at the flip (keyguard,
   in-call — the §2.4 suppressions), fire nothing: a knock for a
   fuse he was not shown is a noise, not a signal. Enforcement
   itself stays the shield's job; this is only the pill keeping
   its promise to be *felt* at the moment it exists for.
3. **Kill the phantom (the §2.5 fix, minimum form):** the VM
   ticker's peek block (MonolithViewModel.kt:851-865) and
   `end2MinutePeekEarly`'s seal are retired from the pill's life:
   - delete the 15 s detents and the expiry call from the VM
     ticker; the heartbeat moves to the controller, fired when the
     *derived* remaining crosses each 15 s mark (105…15, same
     `playRotaryDetent(760.0)`, once-per-mark flag set);
   - `_floatingPeekPill` is cleared by observing the lease: a VM
     collector on `repository.allAppsFlow` that zeroes the state
     when its package's `activePeekUntilElapsedRealtime` reads 0 —
     so a controller hand-back silences the phantom within one
     Room emission, the ghost seal can never fire, the cooldown
     anchor is never re-stolen, and `canUse2MinutePeek` stops lying.
   - `end2MinutePeekEarly()` keeps its repository verb for any
     future caller but loses `playHarmonicSeal(arming = true)`
     (:2197); if it is ever wired to a surface, §3.4's latch(false)
     is its voice.
   The full deletion of the VM counter (the "dead contract" state)
   is the cleaner end-state and `PillFace.kt`'s header already
   argues for it — but it reaches into airlock state reads beyond
   this seat's sources, so the collector above is the priced
   minimum and the deletion is flagged for Pastille.

---

## 4. Price

All changes in `PillController.kt` unless stated. No new
dependencies, no new engine voices, no schema change,
`PillFace.kt` / `PillTheme.kt` untouched.

| # | Item | Lines |
|---|---|---|
| 1 | Engine plumbing: `TactileSoundEngine` param on `PillController` + construction/pass at MonolithShieldService.kt:111 | ~6 |
| 2 | ✕ `TouchDelegate` to 48 × 48 dp (post-layout, refreshed on face change) + `contentDescription` | ~14 |
| 3 | `onHandBack`: `playMechanicalLatch(false)` + immediate re-derive tick + `handedBack` flag | ~8 |
| 4 | Slop: `scaledTouchSlop` + Euclidean distance (replaces :336) | ~2 |
| 5 | Drag reachability clamp on rendered x/y during `MOVE` | ~6 |
| 6 | Pickup detent once per drag (flag on `DragListener`) | ~5 |
| 7 | Snap-settle knock for travel > 120 dp (animator end listener; rides Feel-1's spring rest callback if that lands first) | ~7 |
| 8 | Controller heartbeat + warning + expiry: per-lease flag set (`lastDetentMark`, `warnedLeaseEnd`, previous-face tracking) in `render`/tick + the three firings (§3.5 #1–2, heartbeat) | ~30 |
| 9 | VM phantom kill (MonolithViewModel.kt): lease-observer collector clearing `_floatingPeekPill`; ticker peek block (:851-865) deleted; seal at :2197 deleted | **net ~−6** (+14 / −20) |
| 10 | *Recommendation, separate ruling:* Peek-face body tap = hand-back + return (§3.4 #4) | ~3 |
| 11 | *Recommendation, separate ruling:* intercept-open latch → detent de-duplication (§3.1, MonolithViewModel.kt:2124) | ~1 |
| | **Total (core 1–9)** | **≈ +72 lines net, 3 files** |

For scale: the pill's entire sound implementation today is three
call sites in the wrong file, one of which is a ghost.

---

## 5. Acceptance — how Pastille judges it on the phone

1. **The thumb test:** mid-peek, one-handed, tap the ✕ *sloppily* —
   aim at its edge, not its centre. Fixed build: the peek ends.
   Current build: a 21 dp sliver decides whether you end the peek
   or get yanked to Monolith with the lease still burning.
2. **The answer test:** press ✕. Fixed build: latch-shut haptic in
   the finger the same frame, Peek face gone immediately — not up
   to a second later, not silently.
3. **The ghost test:** hand back at 0:30, then just wait with the
   phone in hand. Fixed build: nothing at 1:45, nothing at 2:00,
   no detents from a pill that isn't there. Current build: detents,
   then a full seal clunk at the phantom's 2:00, and a cooldown
   quietly extended by the time you didn't use.
4. **The fuse test:** let a peek expire while staring at the peeked
   app. Fixed build: one raised detent at 0:10, one knock at 0:00,
   the face flips. Current build: the label silently becomes
   SESSION and the clock jumps — you learn the peek ended from
   the app being taken away, if at all.
5. **The tremor test:** tap the pill body with a deliberate slight
   shake (a walking tap). Fixed build: it still counts as a tap.
   Current build: ≈4.2 dp/axis of shake turns it into a drag and
   the return-to-focus verb never fires.
6. **The stranding test:** drag the pill hard into a corner and
   release. Fixed build: the ✕ is still fully on the glass.
   Current build: it can rest half off-screen, vertically forever.

— Feel-4 · Touch & Sound (issue-16 cell)
