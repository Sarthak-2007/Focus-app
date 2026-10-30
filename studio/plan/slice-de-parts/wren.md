# SLICE E — WREN (Wild, cinema): THE SHOT + RELEASE PROOF, AS TIMING

**Seat:** Wren, Wild 17, cinema — attention arranged in time.
**Joint owned:** how Slice E moves in time — (a) the Vertigo at the seal,
(b) Release Proof + the finish at session end. One joint, two clocks:
the **t-clock** (seal) and the **E-clock** (finish). Links are Opal's,
light values are Mara's, sentences are Emil's/Suki's; the timestamps,
the order, and the state transitions are mine.
**Standing carried in:** the Ellipsis is built (the BREAK word ships —
the break plane's two CUT 0 frames are this grammar's proof that a cut
can carry a transition; the finish below spends the same discipline).
My bolt lands **+30ms** inside Orson's 210ms cadence. My Vertigo is
the seal's shot. Inbox read at start: empty (four letters already in
`read/`). No code edited; no gradle run.

---

## 1 — VERDICTS

**(a) THE VERTIGO — SHIPS AS AMENDED.** Frame-identical for both
entries from t=0, three movements, 1050ms whole. Amendments A1–A5
below are forced by the real Deck composition and by REGISTER's
t=0, which supersedes my own part-file definition. Spectacle audit:
**zero freezes spent** — argued phase by phase in §3.4, not asserted.

**(b) RELEASE PROOF — SHIPS AS AMENDED.** The reverse cascade runs
*inside* the held frame, not after it; detention is a formula, not a
hope (§4.3), and in every branch — happy, slow, faulted — the
receipt issues at or before **E1800**. The finish is the held frame,
the 0ms cut, the rack, in that order, with no reverse Vertigo. The
shipped code today has **none** of this: no E-clock, no held frame,
no receipt state, the wrong sound at the wrong time (§2.3). This
joint is a build, not a tune.

---

## 2 — AGAINST THE REAL CODE

All paths `Focus-app/app/src/main/java/com/example/…`, read this run.

### 2.1 Transition machinery that exists

- **Curves** — `ui/theme/MonolithCurves.kt` (whole file, 47 lines):
  durations strictly `{180, 260, 280, 420, 520}`; easings A
  `(0.2,0,0,1)`, B `(0.05,0.7,0.1,1)`, C `(0.3,0,0.8,0.15)` as
  PathInterpolators + Compose twins; two mechanical overshoot
  interpolators. **Absent:** the Vertigo easing
  `CubicBezier(0.5,0,0.85,0.35)`, the blur/rack curve
  `CubicBezier(0.32,0,0.18,1)`, and any 1050/1200 duration. The
  closed duration vocabulary is already exceeded by REGISTER
  (HELD FRAME 1200, SEAL CADENCE 210) — adding constants is plan
  law catching up, not a new liberty.
- **Screen transitions** — `MainActivity.kt:272–278`: one
  `AnimatedContent` on destination, `fadeIn(specB 420) togetherWith
  fadeOut(specA 260)`. A crossfade. **No rack focus exists anywhere:**
  no `Modifier.blur`, no `RenderEffect`, no scale/plane machinery in
  the codebase (grep: blur appears only as `blurRadius = 0f` text
  shadows, `TactileDeckScreen.kt:216,224`). RACK 280 must be built;
  §4 prices it once, shared by nav and the finish.
- **Overlays** — `ui/components/AirlockAndDisarmOverlays.kt:64–68`:
  the AOD desk clock is `AnimatedVisibility` + `fadeIn(specB)` /
  `fadeOut(specA)`. Fades only; its burn-in patrol is a position
  offset (`:120–121`, dx/dy from `burnInStep`), stepped by the VM's
  60s ticker counter — the only stepped motion in the app, and it is
  not on the Deck.

### 2.2 The Deck at the seal — what flips today

- `ui/screens/TactileDeckScreen.kt:118–119`:
  `isSessionRunning = activeSession != null && activeSession.isActive`;
  `effectiveNight = isDarkTheme || isSessionRunning`. **This is the
  tone step at seal the brief pointed at** — the entire screen
  (hero glyph tier `:250`, ghosts `:251–252`, every token downstream
  of `effectiveNight`) flips day→night in one recomposition frame,
  unanimated, at whatever frame the session flow emits. The shot
  must take ownership of this flip (amendment A3) or the ground
  steps at t≈0 by accident and my t=400 step is a second, lying step.
- **Composition (idle):** instrument panel (eyebrow row — state word
  + `WILL SHUT n APPS` — hero, wheel `AndroidView` 288dp at `:273`),
  task line, pinned commit bar. **Composition (running):** the wheel
  is *removed* (`:273` is `if (!isSessionRunning)`) and replaced by a
  6dp progress track + % (`:337–365`); the slide is *removed*
  (`:486` branch) and replaced by grace-abort / emergency chips;
  a utilities row (DESK CLOCK / HELD) appears (`:454`). There is no
  shared element between the two compositions except the hero and
  the task line — **the shipped Deck has no subject for a Vertigo**:
  at the seal frame, the wheel and sled cease to exist instead of
  receding. Amendment A2 is the fix.
- **No Animatable, no graphicsLayer, no ring field** anywhere in the
  Deck. The concentric ring field of my filed shot exists only
  inside `RotaryWheelView`'s hub guide ring
  (`ui/views/BlockitCoreViews.kt:716–718`) — the expanding field
  behind the readout was never built. Priced as new scenery in §5,
  and explicitly cuttable (A5).
- **Breath — stated absence.** There is no idle/breathing animation
  in the app: no `rememberInfiniteTransition`, no `Animatable`, no
  breath composable under `ui/` (the only "breathing" in code is the
  Airlock comment, `AirlockAndDisarmOverlays.kt:469` — a 5s
  countdown wall, not an animation). "Breath ends at t=0" therefore
  binds the *audio/haptic* breath — Orson's 120ms pre-t=0 window
  (drag: the 110ms assist-spring window; tap: the standing silence)
  — and the stillness law: the idle Deck is already perfectly still,
  so there is nothing visual to end. The clause is satisfied by
  absence + the sound cut, and I will not invent a breathing
  animation to have something to stop.
- **Input death today** is removal: wheel gone (`:273`), slide gone
  (`:486`), and `RotaryWheelView.onTouchEvent` early-returns under
  `isLockedSession`. But nav is *not* dead while running —
  `MainActivity.kt:272` switches destinations freely mid-session —
  and the running Deck still offers DONE / QUEUE / step toggles /
  DESK CLOCK / HELD as live taps. The shot's input law (A4) covers
  the arm-path + nav; the mid-session verbs are Linea's/HOLD's
  joint, flagged here, not priced by me.
- **The seal's crime scene (Ines §0, confirmed):** the view plays
  the seal at release (`BlockitCoreViews.kt`, `SlideToStartView.
  onTouchEvent`, commit at `dragFraction >= 0.82f`) and
  `ui/MonolithViewModel.kt:892–905` `armMonolithSession()` plays
  `playHarmonicSeal(arming = true)` *before* the coroutine that
  inserts the session. Two seals, both before proof. The shot's
  t=0 (§3.1) is where the single cadence must move; the view's
  pre-emptive seal dies with the slide rewrite, but the *timing*
  fact is mine to state: **nothing sounds at t=0 that has not been
  proved by t=0.**

### 2.3 The finish — what fires today

- **E-zero today does not exist.** `MonolithRepository.kt:926`
  `updateSessionProgress` flips the record at its end
  (`isActive = !allBlocksCompleted, completedSuccessfully =
  allBlocksCompleted`, ≈:1026–1035) — that flip is the only honest
  E0 candidate in code. But the VM discovers it by polling:
  `MonolithViewModel.kt:316–335` `startTickerLoop`, `delay(1000L)`,
  and at `:329–330`: `if (!updated.isActive &&
  updated.completedSuccessfully) soundEngine.
  playHarmonicSeal(arming = false)`. So today's finish is:
  **up to 1000ms late, quantized to the ticker, announced with the
  SEAL sound played a second time in its life** (disarming variant),
  the AOD hidden, stats recomputed — and the Deck recomposes
  instantly to idle. A 0ms cut with no defocus, no rack, no held
  frame, no receipt.
- **The completion pulse exists and is never spent on completion:**
  `service/TactileSoundEngine.kt:360` `playCompletionPulse()`; its
  only VM call site is `triggerRatchetWarning`
  (`MonolithViewModel.kt:145`) — a *warning* spends the reserved
  voice today. §4 reclaims it for E700, gated exactly as REGISTER
  reserves it (verified fully-elapsed sessions only).
- **No receipt state exists** — no pending-receipt field, no sheet
  state, no detention concept in the VM. (The VAULT destination's
  label "Receipts", `MonolithViewModel.kt:43`, is the Ledger tab,
  a different object.) Release Proof's detention (§4.3) is new
  state, priced.
- **No release verification exists.** Natural completion doesn't
  even call `MonolithShieldService.stopShield` on the ticker path
  (only the disarm paths do, `MonolithViewModel.kt:916,949`);
  services notice the flipped row on their own cadence (Shield
  monitor polls at 2000ms — Ines §0). Commanding is currently
  *assumed* to be release, everywhere. Opal's charge stands in code.
- **No pawl exists in code** — no PROVING/RELEASING state, no link,
  no cascade in the repository (Ines §0). Opal defines the links;
  §4 defines only their timing.

---

## 3 — JOINT (a): THE VERTIGO — AMENDED SPEC

### 3.1 t=0 — amendment A1 (my own definition, superseded in the open)

My part file defined t=0 as "the first vsync the state flow reads
SEALED." REGISTER closes the question and governs: **t=0 ≡ the
PROOF-SEAL FRAME** — `t0 = max(last link CONFIRMED-SHUT, latch +
210ms, pawl-seated frame)`, the first frame the controller executes
PROVING → SEALED-RUNNING. Consequences, all favourable: the shot
never photographs an unproven seal; the orange does not begin to
drain during PROVING (a drain during proving would spend the commit
before the proof — the seal-sound crime in colour); Orson's cadence
floor (`latch + 210ms`) is *inside* the max(), so my bolt at
**t0+30ms** lands ≥240ms after latch, inside the cadence, as
ordered. Under Ines's Live Wire (REGISTER: verdict ≤5ms at DOWN and
at the gate), PROVING after latch is short — session insert +
shield acknowledgement — and the shot is unchanged: it starts when
proof lands, whenever that is. **Approach differs, shot does not:**
drag arrives via the 110ms assist spring → latch → proving; tap
arrives via Cato's mesh (140ms to 0.92 + the same 110ms spring) →
latch → proving. Orson's tap catch-note is approach audio, pre-t0
(my addendum on his letter stands: it creates no tap-only first
frame). From t=0 the two entries are frame-identical — same
Animatable, same sampled values, acceptance-tested eyes-closed and
now also frame-diffed.

### 3.2 The clock and its owner

- **One `Animatable<Float>` (`vertigoT`, 0→1), owned by a
  `SealShotState` holder remembered in `TactileDeckScreen`, keyed
  by `sessionId`.** It is launched once, by a `LaunchedEffect` on
  the PROVING → SEALED-RUNNING edge of the session StateFlow — the
  same edge that defines t=0 — and animates over **1050ms** with
  `CubicBezierEasing(0.5, 0, 0.85, 0.35)` (transforms) while blur
  samples `CubicBezierEasing(0.32, 0, 0.18, 1)` over the same
  elapsed time. Two pure functions of one monotonic elapsed value;
  picture and defocus cannot desync.
- **Derive, don't replay:** every frame's value is
  `clamp((nowMono − t0Mono) / 1050)` eased — `t0Mono` is the single
  monotonic start sample W4 already attaches to t=0, plumbed from
  the VM. Backgrounded/killed mid-shot: on recomposition the shot
  resumes at its true elapsed position in one frame, or is already
  over. No catch-up travel, ever (the Ellipsis discipline).
- Reduced motion (filed, kept): VERTIGO → 200ms peripheral fade,
  readout static, all sync points collapse to t=0.

### 3.3 What moves — against the real composition

Elements present at t0−1 and their fates. Values from my filed
table, re-homed onto the shipped Deck's actual elements:

| Element (real home) | Departure 0–180 | Recession 180–780 | Settle 780–1050 |
|---|---|---|---|
| **Hero readout** (panel, `TactileDeckScreen.kt:253–271`) | scale **1.0**; content hard-cuts at t=0 from span form (`170`/`2H 50M`) to running form (counting from t=0) | scale **1.0**; strikes each true second | scale **1.0** — the invariant, all 1050ms |
| **Wheel** (`AndroidView` 288dp, `:273`) | →0.985 · y+3dp · α0.97 · blur 0.7dp | →0.83 · y+17dp · α0.58 · blur 5.5dp | →**0.80 · y+20dp · α0.50 · blur 6dp** — the running ghost |
| **Hub** (inside wheel view) | rides the wheel transform; its light is Mara's/D6 — position function only, untouched by the shot's clock | — | — |
| **Task line** (`:372ff`) — **amendment A2b** | →0.997 · α0.96 · blur 0.5dp | →0.985 · α0.88 · blur 2dp | →**0.97 · α0.85 · blur 2dp, PERSISTS** as the running micro-line |
| **Slide cap** (seated at latch) | orange step 1–2 (below) | steps 3–4; ink by t=200 | seated, ink, inert — goes home only at receipt+1000 (W4) |
| **Ground / panel** | — | **one staircase tone step at t=400, one frame** (a cut inside the dolly) | stepped |
| **Ring field** (unbuilt — A5) | spacing ×1.00→×1.03 | →×1.31 | →**×1.38** |
| **Imprint** (Emil's slot) | **the Strike fires AT t=0** (Emil §2: on the proved seat frame — under A1 that frame *is* t=0; simultaneity law holds) | pressure lip lives | lip lives (whole seal) |
| **Strip / chrome** | static, sharp, outside the shot (body law) | static | static |

**Amendment A2 — ghost retention (the load-bearing one).** My filed
table sent the task card to α0 and the wheel to a ghost — but the
shipped code *deletes* both subjects at the seal frame (§2.2). The
shot therefore requires the running composition to **retain** the
wheel and the seated sled as inert ghosts at their settle values,
and the task line to persist as micro-line (A2b: my filed α→0 for
the task card is **withdrawn** — Linea's frozen line and the filed
running Deck both keep it; a vanished task line would also strand
the held frame's micro-line, which is printed from it). The code's
6dp progress track + % (`:337–365`) is a second rendering of
elapsed and is **not** composed during the shot; its fate past
settle is the running-Deck owner's call, flagged, not mine.
Utilities row (DESK CLOCK / HELD) **enters at t=1050 by CUT 0** —
absent during the shot, present at settle, no fade: during the
1050ms the Deck offers no live surface at all (see A4).

**Amendment A3 — the tone flip is the shot's, not the state's.**
`effectiveNight` (`:119`) must stop being a direct function of
`isSessionRunning`. Under the shot: hero glyph tier (D1) lands at
t=0 *with the content cut* (the number is the brightest object from
the first frame — hierarchy cannot wait 400ms), and the ground/
panel tone steps **once, at t=400**, driven by `vertigoT`. A builder
who leaves `:119` as-is ships two tone steps (one accidental at the
flow-emission frame, one at t=400) and the second one lies.

**Amendment A4 — input dies at t=0, enumerated.** At the t=0 frame,
killed in the composition (not faded, not greyed — dead):
(1) wheel drag/fling and hub press — wheel's physics halted
mid-angle if a coast is in flight (`stopPhysics()` on the SEALED
edge; the readout's count comes from state, never from angle), the
view's existing `isLockedSession` early-return becomes the standing
state, and its container takes no pointer input for the session's
life; (2) slide — cap input dead via the same sealed lock (the view
stays *visible*, seated: a removed sled would teleport the room);
(3) FLOW key and every idle Deck verb; (4) task-line verbs (DONE /
QUEUE / step toggles) — removed from the line at t=0 by state, per
A2b (their *line* persists; their *verbs* die); (5) **nav** — taps
absorbed for the shot's duration and the session's (filed sealed
regime: detent tick tries and stays, 4ms @ 0.2; code today absorbs
nothing — `MainActivity.kt:272` — priced in §5). What is alive
during 0–1050: **nothing on the Deck.** The emergency path and the
desk-clock entry compose at settle (t=1050) with the utilities row;
a session 1 second old has no door yet, and the grace window
(10s, code `:498`) is not shortened by this — its clock starts at
t=0 and its control arrives at t=1050, leaving ≥8.95s of usable
grace. Stated plainly because it is the one real cost A4 carries.

**The orange exit — steps, timestamps, and an honest gap.** Drain
starts at t=0 (not at latch, not during proving), four discrete
token swaps at **t = 0 / 50 / 100 / 150**, ink confirmed at **t=200**
— zero orange anywhere on the running Deck from t=200. Day steps,
all existing locked tokens (`ui/theme/Color.kt`): `#F55314`
(accent) → `#D63E06` (alert, the hue's deepest filed step) →
`#615B5A` (cap/controlFill) → `#252323` (ink). **Night: Mara's
published values do not exist in the files I was given** — her
beyond file and seat file name the cap, the ceilings and the accent
`#F47442`, but no four-step drain table (searched: no drain hexes
anywhere in the swarm plan). I will not invent hexes under H-7.
Night amendment, built only from locked night tokens, monotonic in
lightness, hue surrendered at step 1 (at night the orange leaves
faster — same step count, same timestamps): `#F47442` → `#4A423C`
(cap) → `#362C27` (raised) → `#100E0C` (recess — the seated sled's
unlit hardware tone). This is fork F1 for Ren/Mara to ratify or
replace with her true table.

**Haptics/sound sync (unchanged from my part, re-timed to A1):**
tick train haptic-only at **170 / 320 / 460 / 580 / 680 / 760ms**,
amps 0.28→0.12 decaying; settle knock **16ms @ 0.7 at t=1050**;
Throw attacks **at t=0**, bolt at **t0+30ms**; no audio event inside
170–760 except the bed's arrival at settle (Orson's channel; the
shot is silent).

### 3.4 Spectacle audit — why no phase is a freeze (S2)

S2's freeze is a *hit-stop*: state and picture intentionally held
for effect, input returning after (the four: slide commit 40 ·
final sub-step 50 · DONE 40 · bank catch 40). Each phase fails
every clause of that definition:
- **Departure (0–180):** state is not held — the timer counts from
  t=0 and the readout strikes its first second *inside* this phase;
  picture is not held — the Vertigo easing
  `(0.5,0,0.85,0.35)` has strictly positive velocity on the whole
  open interval (slow departure is low velocity, not zero: wheel
  Δscale 0.015, Δy 3dp, blur Δ0.7dp across 180ms); input is not
  "paused" — it died by state change (HOLD), permanently.
- **Recession (180–780):** the fastest motion in the shot; the
  t=400 ground step is a 1-frame *cut inside continuous motion* —
  transforms sample straight across it; a cut is the opposite of a
  freeze.
- **Settle (780–1050):** decelerating approach to filed end-values,
  velocity →0 only at the endpoint, which is where motion *ends*,
  not where it is suspended; the settle knock lands on arrival.
- The one true stillness: the readout's **scale** is invariant at
  1.0 for 1050ms. An invariant is not a hit-stop — its *content*
  changes every second throughout. If the studio ever rules an
  invariant a freeze, the price is named here honestly: it would
  be a fifth freeze, and under S2's rule one of the four must die
  for it. My ruling: it is not, and none dies. **Ledger stays at
  four. Freezes spent by this joint: 0.**

---

## 4 — JOINT (b): RELEASE PROOF + THE FINISH — AMENDED SPEC

### 4.1 The E-clock's zero

**E0 = the first frame the controller executes SEALED-RUNNING →
RELEASING**, keyed to the persisted, once-ever completion
transition (Fen's law, inherited): in code, the frame
`updateSessionProgress` first returns `isActive = false ∧
completedSuccessfully = true` (`MonolithRepository.kt:926`,
flip ≈:1026–1035) **as observed by the controller**. Amendment
E1: observation must ride the session StateFlow's emission, *not*
the 1s ticker (`MonolithViewModel.kt:319`, `delay(1000L)`) — a
finish discovered up to 1000ms late is an E0 that lies about when
the session ended, and every timestamp below inherits the lie.
Acceptance: E0 lands ≤1 frame after the repository flip on device.
Emergency exits never enter this clock (the splice is its own
form, 0ms, sharp immediately — filed, untouched). Stopwatch-early
completions and grace cancels are not E0 either: the held frame
and the pulse belong to verified fully-elapsed sessions only
(REGISTER), and the gate is the code's existing condition —
`!isActive && completedSuccessfully` — moved to the right sound
at the right time.

### 4.2 The held frame (E0 → E1200) — every timestamp a transition

| t | State transition / event |
|---|---|
| **E0** | Controller: SEALED-RUNNING → **RELEASING**. Elapsed counting stops; the session's duration is final. Held frame composes: final total alone at full giant size (scale continuous 1.0 from the running readout), micro-line under it (`PHYSICS — 50 MIN` form: task title + true length). **Settle knock 24ms @ 1.0.** Bed fade-out begins. **Reverse cascade starts this same frame** (§4.3) — it runs *inside* the hold, invisible, behind the stillness. Imprint: SEALED tenant + pressure lip **persist** (amendment E2, §4.5). |
| **E0–300** | Bed → out (300ms fade). Picture: total stillness — no element moves, including the colon; no progress, no spinner. |
| **E300–700** | **True silence** — digital zero, haptically silent. Picture unchanged. |
| **E700** | **Completion pulse, alone, 0 dB ref** — `playCompletionPulse()` (`TactileSoundEngine.kt:360`), reclaimed from the ratchet warning. Heard *inside* the stillness, never illustrated by it: the picture at E700 is pixel-identical to E699. |
| **E880** | Power-down knock pair (Orson's Room Stops): 24ms @ 1.0, then 12ms @ 0.5 — lands inside the hold, after the pulse. |
| **E1200** | **CUT 0** (§4.4). Held frame's 1200ms whole: 300 bed-out + 400 true silence + 500 pulse-in-stillness. |

**Skip law (filed, kept):** one tap anywhere during E0–1200 jumps
straight to the E1200 cut. Taps during the rack are navigation
taps under rack law (a reverse tap reverses it; nothing queues).

### 4.3 Release Proof — the reverse cascade's timing and the detention formula

Opal defines the links and their checks (per-link states
`SHUT-PROVEN → COMMANDED-OPEN → CONFIRMED-OPEN`, real enforcement-
absence checks). Mine is their timing:

- **Order:** exact reverse of the seal chain — last-proven-shut
  opens first (the jam-unwind order, already filed).
- **Pacing: event-driven, never clock-driven.** Link n+1 is
  commanded on the *frame* link n returns CONFIRMED-OPEN. No
  stagger table, no metronome: bunching confirmations bunch the
  cascade. Per-link budget (Opal's numbers, adopted): **60–150ms
  typical, 400ms timeout**. A timeout *halts* the cascade at that
  link (downstream links stay shut and render shut; already-opened
  links stay open — no re-shutting, ever).
- **The pawl** stays seated, visibly, for all of RELEASING. On the
  final CONFIRMED-OPEN it **lifts: 90ms**, haptic **8ms @ 0.50** at
  lift start, tick a semitone below the gate click (Orson/Opal
  interval law). Pawl-lift frame = controller enters RELEASED.
- **Detention, defined:** the receipt's *facts* are final at E0
  (true elapsed, deflections, fault lines) but the receipt is
  **not composed — invisible, nowhere on screen, not
  visible-but-unstamped**. A visible unstamped receipt would claim
  a release not yet proved: the seal-sound crime at the exit.
  During detention the screen shows only the held frame
  (E0–1200) and then the defocused idle Deck (E1200 onward).
  The receipt is held *in the VM* as a pending-receipt state —
  data, not pixels.
- **The formula (every branch ≤ E1800):** let P = pawl-lift frame.
  Rack start **R0 = max(E1200, P)**. Receipt t=0 =
  **min( max(E1480, R0 + 280), E1800 )**.
  - *Happy* (P ≤ E1200; cascade typical 0.4–1.1s, inside the hold):
    R0 = E1200, receipt t=0 = **E1480** — 280ms of rack after the
    cut, inside the 1800 ceiling with 320ms to spare.
  - *Slow* (E1200 < P ≤ E1520): rack waits for proof — R0 = P,
    receipt t=0 = P + 280 ≤ E1800. The defocused Deck simply stays
    defocused longer; the held frame is *not* extended (1200ms is
    its whole life, in every branch).
  - *Faulted* (halt, or P > E1520): at **E1800** the receipt issues
    anyway as **ENDED-FAULT**, its t=0 = E1800, rising as an
    aperture sheet over the still-defocused Deck — the sheet's
    native grammar is a defocused room, so nothing is faked; the
    Deck's rack completes behind the sheet (by ≤ E2080). The pawl
    stays seated. Detention ceiling honoured: **the receipt is
    never issued later than E1800, in any branch.**
- **STILL BLOCKED — home, count, voice.** Printed **once**, on the
  receipt at issuance, per faulted link, in Opal's grammar:
  `INSTAGRAM — STILL BLOCKED (PERMISSION)` (cause-worded per
  Suki's classes; `STILL BLOCKED` is the line family, not a bare
  string). **Not in the imprint slot** — §4.5. Never reprinted.
- **Silent retry:** the blocking service retries *only the faulted
  links*, on its own schedule — **first retry at +30s after the
  halt, second at +5min, thereafter on next Deck open** (Opal's
  filed cadence, adopted whole) — zero dialogs, zero decisions,
  zero user-visible events. On a retry's CONFIRMED-OPEN the pawl
  lifts silently wherever the app is; a healed fault is never
  announced, never amends the issued receipt (the receipt was true
  at issuance); it simply stops being true, and files to the
  record. Re-arming over a still-shut link needs no special case:
  the new seal chain reads reality and that link's CONFIRMED-SHUT
  returns immediately (Opal).
- **Process death in RELEASING:** on relaunch, release state is
  re-derived per link from the stack (Opal); the held frame is
  **not** replayed — a relaunched app shows the idle Deck (sharp,
  no rack: nobody is arriving, the room was never left) and the
  pending receipt issues on its formula against the *original*
  E0 if still inside the ceiling, else immediately.

### 4.4 The cut and the rack (E1200 → receipt t=0)

- **E1200, CUT 0ms:** the whole idle Deck, fully composed at final
  positions — wheel at rest scale 1.0 showing the dial value as
  left, task line and NEXT line true, sled still seated (it goes
  home at receipt+1000) — rendered **defocused: blur 9dp, scale
  1.012, one tone step down**. Ring field: gone at the cut, with
  the running Deck. Readout scale continuous at 1.0 through the
  cut — the number stays; the room changes.
- **RACK 280** (from R0, §4.3): the inter-screen token, unmodified
  — blur 9→0 on `CubicBezier(0.32, 0, 0.18, 1)`; primary plane
  (wheel + slide) sharpens from R0, secondary (task line, receipt
  line) at R0+50, tertiary meta at R0+100; settle hunt
  1.0→0.9985→1.0 in the last 50ms; landing tick **6ms @ 0.35** at
  blur passing 1dp (≈R0+230); nav detent jumps (0ms) at rack end.
- **NO reverse Vertigo — restated as a timing law:** nothing in
  E1200–receipt-t0 samples `vertigoT` backwards; no element travels
  0.80→1.0, +20dp→0, α0.50→1.0 by animation. The wheel is *found*
  at scale 1.0 in defocus because its angle and value never moved
  during the session (input was dead — there is nothing to rewind).
  A rewind would claim a restoration; nothing was restored;
  something was completed.
- **Sled goes home:** at **receipt t=0 + 1000ms** — anchored to the
  receipt, not to E (in the slow/fault branches the anchor moves
  with the receipt). Form (650ms powered travel per W4 merge law)
  is Fen's fight with my Attention Law, still open — §7, F3. The
  *timestamp* is mine and stands either way.

### 4.5 The imprint at the finish — tenancy, settled for this joint

Emil's law: SEALED evicts even TRUTH mid-session; the sealed tenant
freezes at the seat-frame count for the session's life. The finish
is post-session, and the tenant sequence is:
- **E0–E1200 (held frame):** SEALED tenant + pressure lip **persist
  unchanged** — amendment E2. Emil's §1.4 fires the SEALED → IDLE
  swap "at the session-end event"; under this joint's clock the
  swap is timed to the **E1200 cut**, not to E0: printing IDLE over
  a held frame that still shows the sealed world would put two
  contradictory facts on one screen for 1200ms. The lip's 240ms
  fade (Emil §2) runs E1200–1440, *under* the rack's defocus —
  the mark leaves while the room is still soft, and the sharp
  Deck that lands has unmarked ground.
- **At E1200:** slug swap SEALED → IDLE (Emil's 150ms swap,
  completed by E1350, inside the rack, before the receipt strikes
  at E1480 — Emil's own ordering condition, satisfied exactly).
  The IDLE tenant is **re-measured** at the swap (current loadout),
  never restored from a pre-seal snapshot.
- **STILL BLOCKED never tenants this slot** (see §4.3): its home
  is the receipt, once. If blocking health is independently broken
  at the next idle measurement, the TRUTH tenant will say so in
  its own grammar — a different fact (standing health) from the
  release fault (a past event), each in its one home.

---

## 5 — PRICE (lines, per joint; gross, against the shipped tree)

**(a) Vertigo ≈ 350 lines**
- `MonolithCurves.kt`: Vertigo + blur easings, 1050/1200 constants — ~10
- `TactileDeckScreen.kt`: `SealShotState` + Animatable + LaunchedEffect
  on the SEALED edge; transform/blur wiring on wheel container, task
  line, panel — ~150
- `effectiveNight` deferral (A3: glyph at t=0, ground step at t=400,
  driven by `vertigoT`) — ~20
- Input lock (A4): sealed flags on wheel/slide containers + Deck-level
  absorption during shot — ~25
- `BlockitCoreViews.kt`: `SlideToStartView` cap drain-step property
  (4 token swaps, Canvas paint) — ~35; wheel `stopPhysics()` on seal
  edge + ghost retention plumbing — ~30
- Ring field scenery (A5, cuttable): static concentric Canvas behind
  hero + spacing factor from `vertigoT` — ~45
- VM: `t0Mono` + sessionId plumbing to the Deck — ~25
- Nav absorption during session (shared with HOLD's joint; my share:
  the shot-duration gate) — ~10

**(b) Release Proof + finish ≈ 410 lines** *(my timing slice only —
Opal's per-link open-check implementations and Emil's receipt
composition are their joints' prices, excluded)*
- VM: finish state machine (E-clock, RELEASING/RELEASED/ENDED-FAULT,
  pending-receipt detention, formula §4.3) + StateFlow-edge E0 (E1) +
  pulse re-gating to E700 — ~150
- Reverse-cascade sequencer (reverse order, event-driven pacing,
  400ms timeout/halt, pawl-lift event) over Opal's checks — ~110
- Silent-retry scheduler (30s / 5min / Deck-open, faulted links only) — ~40
- Deck: held-frame composition (total + micro-line branch) + CUT/RACK
  host — ~70 (rack machinery itself built once, ~110, shared with
  nav's RACK — counted here, credited to both)
- Imprint swap/lip timing hooks at E1200 — ~10
- Ticker: completion edge removed from the 1s poll (display ticking
  stays) — ~−10 net

**Slice E timing total ≈ 760 gross lines.** No migration, no new
persistence (E0/t0Mono are runtime samples; the persisted record
already carries everything the formulas re-derive from).

---

## 6 — RISKS

1. **Blur over an `AndroidView` wheel.** `Modifier.blur` on the
   wheel's container composites via RenderEffect (API 31+); the
   wheel is a heavy self-drawing Canvas view and 0.7→6dp of live
   blur across 1050ms may jank on device. Fallback, pre-named: if
   the device pass shows dropped frames in Recession, the wheel's
   blur freezes at its Departure value (0.7dp) and the recession is
   carried by scale/translate/alpha alone — the shot survives; the
   *timing* is untouched. Gate it like knurl: one device A/B, no
   retune rounds.
2. **E0 latency if the builder keeps the ticker.** E1 is the whole
   game: a 1s-quantized E0 makes the held frame start late against
   a session that has already ended — apps already releasing while
   the screen still counts. Acceptance must *measure* E0 against
   the repository flip, not eyeball it.
3. **120Hz frames.** All timestamps are wall-clock ms sampled per
   frame — safe. The orange steps (50ms apart) are 6 frames at
   120Hz, 3 at 60Hz: discrete at both, as specced.
4. **The grace-window cost (A4).** No live Deck surface for the
   shot's 1050ms, including the grace abort. Priced and stated in
   A4 (≥8.95s of grace remains usable). If Ren rules any part of
   the grace path must be live from t=0, the exception is the
   grace chip only, composing at t=0 by CUT over the shot — the
   shot's transforms do not touch its slot. I recommend against:
   a live verb inside the Departure is a door in the shot's first
   frame.
5. **ENDED-FAULT panic read (Opal's own anti-case).** A pawl still
   seated on an ended session can read as "still trapped." My
   timing mitigates what timing can: the receipt — the truthful
   sentence — is guaranteed by E1800, and the held frame never
   extends to wait for proof. The rest is the receipt's wording
   (Emil/Opal), not this joint.
6. **`playCompletionPulse` is currently spent by a warning**
   (`MonolithViewModel.kt:145`). If that call site survives, E700's
   voice is no longer reserved and the pulse's meaning is spent
   before the finish. One-line fix, but it must actually happen.

---

## 7 — FORKS FOR REN

- **F1 — Night orange-exit steps.** Mara's four values are not in
  any file I was given. My amendment (§3.3) builds the night drain
  from locked tokens only, hue surrendered at step 1.
  **Recommendation: ratify my table, or have Mara publish hers and
  swap the values — the timestamps (0/50/100/150, ink by 200) do
  not move either way.** Day table is fully sourced and not forked.
- **F2 — Ghost retention (A2) changes the running Deck's
  composition** (wheel + sled stay as ghosts; code deletes them;
  the 6dp progress track is displaced during the shot). This is
  partly Fen's/Linea's territory. **Recommendation: ratify** — it
  is my filed running state, and without it the Vertigo's subject
  ceases to exist at Departure; the alternative (readout-only shot)
  is a different, lesser film and would need re-speccing, not
  re-timing.
- **F3 — Sled goes home: 650ms travel (W4 merge law) vs CUT 0 (my
  Attention Law fight, invention file).** Timing anchor is
  receipt+1000 under both. **Recommendation: CUT**, settled by the
  day-2 recognition A/B I proposed to Fen — if any viewer hunts
  for the sled, travel returns at Fen's number and the law takes
  its first named exception. Ren/Marlowe settle; I do not block
  Slice E on it.
- **No fork on t=0:** REGISTER's proof-seal definition governs and
  my part-file definition is amended in §3.1, in the open. No fork
  on the detention formula: it is Opal's ceiling (1.8s) and my
  held frame (1200ms) composed, and every branch was derived, not
  chosen.

---

## 8 — MAIL / WALL NOTE

Inbox empty at start and at wrap-up; nothing to move, nothing owed.
One letter sent: to **Marlowe** (coordinator), thread
`slice-e-shot-timing` — verdicts, the A1 self-amendment, forks
F1–F3, and the E1 acceptance warning. Wall: appended under my name
with the two clocks' headline numbers (t0 = proof-seal frame; bolt
+30; E700 pulse; receipt ≤ E1800 in every branch) so any seat
building against Slice E times can read them without opening this
file. No letter to Orson: the bolt/cadence sync is unchanged from
the contract he already holds; the tap catch-note addendum stands.

— Wren, Wild · cinema
