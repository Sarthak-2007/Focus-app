# 22 — REGAL (Improvers, the shelf — the librarian) · SLICE E ACCEPTANCE LIBRARY

*Seated by Ilya (Improvers coordinator) for the SLICE E BUILD-READY
wave. My joint is the acceptance library itself: the master test
skeleton the merged Slice E file will carry. Sources:
SLICE-D-E-BUILD-READY.md §2 (E1–E6 as amended), MASTER-PLAN
§2.7 / §5 (esp. §5.6–§5.7, §5.9), REGISTER.md's checkable
constants as amended (cadence 210 · HELD FRAME 1200 · detention
≤1.8s · sled receipt+1000 / 650ms · pulse reserved · hub core
10% day / 8% night registered separately from the 14% tooth
peak), the trunk's existing test shapes (§1 below), and the
Slice E seat files filed before this one (§7 index). No code
edited, no gradle run; trunk read only.*

---

## 0 — HOW TO READ THIS SKELETON

Every row carries, in order: **ID** (E-T01…) · **joint** ·
**surface** · **assertion** (one sentence, no more) · **evidence
class** · **trunk file the test lands in**.

Evidence classes — the whole honesty of this library lives here:

- **UNIT** — JVM / Robolectric logic tests in
  `app/src/test/java/com/example/`, run by `testDebugUnitTest`.
  A row tagged **UNIT (grep)** is a source-assertion / grep gate:
  it runs as a build-step check with its receipt filed in
  BUILD-CHANGES, not as a `@Test` — named as such, never dressed
  as a unit test.
- **RENDER** — Robolectric render/capture tests (native graphics
  or Compose harness, §1 patterns): pixels, composition, UI-tree
  facts. Robolectric is not a device; nothing in this class
  claims a real speaker, a real vibrator, or real frame timing
  on hardware.
- **MIGRATION** — a hand-built old-version fixture opened under
  the new version, TokenMigrationTest pattern (§1).
- **DEVICE** — a pass on Sarthak's phone, by name. A DEVICE row
  is a named device obligation with a recorded result in
  BUILD-CHANGES; it is **never** silently promised as a unit or
  render test, and no UNIT/RENDER row may be cited as covering it.

Ownership marks:

- **REF** — the assertion is owned by a seat's own filed test
  (seat + test number given). This row is the *index entry*:
  one-sentence summary + pointer. The full assertion, fixtures
  and tolerances live in the seat's file and are **not**
  re-asserted here; if the seat's test changes, this row's
  pointer is what Ilya updates at merge.
- **OWN** — Regal-authored: no seat filed this assertion, and
  the joint's law (build-ready §2 / MASTER-PLAN / REGISTER)
  demands it. These are the gaps the library exists to catch.
- **CONFLICT** — two filed positions cannot both pass; the row
  is shelved here with both citations until Ren/Ilya rule (§8).

Where a seat filed tests without naming the file, I assign one
and mark it *(Regal-named for \<seat\> §n)* — the name is mine,
the tests are theirs.

---

## 1 — NATIVE TRUNK PATTERNS (so E's tests look like they were born here)

Cited from `Focus-app/app/src/test/java/com/example/` as shipped:

- **`ScreenshotHarnessTest.kt`** — the render-harness shape:
  `@RunWith(RobolectricTestRunner::class)`,
  `@Config(sdk = [36], qualifiers = "w412dp-h915dp-xhdpi")`,
  `@GraphicsMode(GraphicsMode.Mode.NATIVE)`,
  `createAndroidComposeRule<ComponentActivity>()`, a real
  `MonolithViewModel` over `Room.inMemoryDatabaseBuilder(...)`,
  `ShadowLooper.idleMainLooper()` settle discipline, and capture
  by drawing the decor view into an ARGB_8888 `Bitmap` → PNG.
  Every RENDER row that needs a composed screen uses this shape
  (day pass + night pass, mirroring its two capture tests).
- **`TetradRenderTest.kt`** — the direct-view render shape:
  density forced to 3.0, view measured/laid at 864×864px
  (= 288dp), drawn on a `Canvas` over the mode's ground tone,
  one render per (count × mode) cell. Every RENDER row against
  `RotaryWheelView` / the slide view alone uses this shape.
- **`TokenMigrationTest.kt`** — the migration shape: a
  `FrameworkSQLiteOpenHelperFactory` fixture database built at
  the *old* version with known rows, reopened under a callback
  that asserts `oldVersion`/`newVersion` and runs the registered
  `MonolithDatabase.MIGRATION_x_y.migrate(db)`; assertions via
  `sqlite_master` + per-row reads; its header comment is the
  house style — the missing `fallbackToDestructiveMigration` is
  "a source-level fact, verified in MonolithDatabase.kt".
  E-T69 copies this shape exactly, one version up.
- **`ExampleRobolectricTest.kt` / `EmergencyBreakLedgerTest.kt`** —
  the controller/repository shape: in-memory Room +
  `MonolithRepository(context, db.monolithDao())` in `@Before`,
  `runBlocking`, `Flow.first()`, hermetic fixtures whose stored
  keys make test cases independent. Every UNIT row against the
  VM, repository, or a controller seam uses this shape.
- House voice: test-class KDoc states the law being pinned and
  cites its spec section (TOKEN-SPEC §8 in the two above). E's
  classes cite build-ready §2 / this file's E-T ID the same way.

---

## 2 — E1 + E2 · THE BIT-IDENTICAL SEAL (one voice, one call site)

| ID | Joint | Surface | Assertion | Class | Trunk file |
|---|---|---|---|---|---|
| E-T01 | E1/E2 | Seal engine (buffer) | Three renders of the zero-parameter `renderSealThrowBuffer()` are SHA-256-identical to each other and to the pinned golden hash, at exactly 44 100 samples — provenance cannot enter the render. REF: Siegel T1 (§5). | UNIT | `SealThrowIdentityTest.kt` (new, Siegel §5) |
| E-T02 | E1 | Seal engine (command seam) | `playSealThrow()` offers exactly one zero-field `SealThrowCommand`, clears a pre-loaded queue, and captures the haptic waveform `[0, 24, 38, 16] @ [0, 255, 0, 175]` — not the shipped waveform. REF: Siegel T2 (§5). | RENDER | `ExampleRobolectricTest.kt` (Siegel §6: T2 lands there) |
| E-T03 | E1 | Whole tree (source) | `grep -rn "playSealThrow" app/src/main` returns exactly 2 hits — the definition and the one lawful call site inside the PROVING→SEALED-RUNNING transition — and both dead `playHarmonicSeal(true)` arm sites are absent. REF: Siegel T3 (§5) + Klinke T12 (§8), concurred. | UNIT (grep) | — (grep gate; receipt filed in BUILD-CHANGES) |
| E-T04 | E1 | Seal engine (bolt noise) | The first four xorshift32 outputs from `SEAL_BOLT_SEED` equal Siegel/Riegel's pinned vector and the full bolt-segment hash matches its pin. REF: Riegel T1 (§6). | UNIT | `SealThrowIdentityTest.kt` (Regal filing: Riegel's pure tests share Siegel's new class) |
| E-T05 | E1/E2 | Seal engine (bolt placement) | The bolt layer is exactly 0 outside samples [1323, 1323+2426), ending at sample 3 749 = +85.0ms — the cadence landmark "audio complete ≤ +90" holds for the bolt/body segment it names. REF: Riegel T4 (§6). | UNIT | `SealThrowIdentityTest.kt` |
| E-T06 | E1 | Seal engine (room tail) | The golden render's level at 900ms is −46 ± 3dB, its −46dB crossing sits within ±25ms of `TAIL_CATCH_SAMPLE`, and with no bed present samples 39 690–44 100 fade monotonically to exactly 0. REF: Riegel T3 + T6 (§6). | UNIT | `SealThrowIdentityTest.kt` |
| E-T07 | E1/E2 | Seal transition (controller) | `t0 == max(lastConfirm, L+210, pawlSeated)` with each of the three terms binding in turn — the L+210 term is the testable shadow of the cadence landmark "intent −210, attack at t=0". REF: Klinke T3 (§8). | UNIT | `SealTransitionControllerTest.kt` (new, Regal-named for Klinke §8) |
| E-T08 | E1 | Seal transition (controller) | With all links confirmed, the sealing frame's invocation order is exactly `sealSession(id, t0)` → publish SEALED_RUNNING → `playSealThrow()`, each once, and no Throw fires while any link is PENDING at frames ≥ L+210. REF: Klinke T1 + T2 (§8). | UNIT | `SealTransitionControllerTest.kt` |
| E-T09 | E1 | Seal transition (controller) | Double latch, L2 jam-retry, and the annul race each produce exactly one Throw (jam/annul-before-t0: zero Throws, claim spent exactly as filed). REF: Klinke T5 + T6 + T7 (§8). | UNIT | `SealTransitionControllerTest.kt` |
| E-T10 | E1 | Seal on hardware | Over 5 real arms at 240fps + mic, the attack aligns to the t=0 frame ±1 frame and the attack-to-haptic offset is measured and reported as a number (target ≤ 8ms — a measurement, not a gate; see §8-U1). REF: Riegel T7 (§6). | DEVICE | — (device pass; result in BUILD-CHANGES) |
| E-T11 | E1 | Seal on hardware | A maximum-length legacy transient enqueued 5ms before a seal is CUT (not tailed, not summed) and the seal attack still lands within one output-buffer period of t=0. REF: Riegel T8 (§6). | DEVICE | — (device pass; result in BUILD-CHANGES) |
| E-T12 | E1 | Seal tail on hardware | The room gate runs once: any ring/buzz/pitch, narrowband > −50dB after 300ms, or tail audible past 1200ms flips `COMB_G` 0.836 → 0.78 exactly once and re-runs; a second failure ships the fader. REF: Riegel T9 (§6) + Siegel §9.6. | DEVICE | — (device pass; result in BUILD-CHANGES) |
| E-T13 | E2 | Routine arm path | **CONFLICT — shelved, §8-U3:** a routine arm produces exactly one bit-identical Throw after its insert (Reihe T-R1 + T-R4, §6 of 04-reihe.md) versus a routine START arm in this slice produces no Throw and no `sealSession` call at all (Klinke T13, §8). Both are filed; they cannot both pass; no assertion is authored here until F-S1 / the Reihe interface is ruled. | UNIT | `RoutineSealConvergenceTest.kt` (new, Regal-named — lands only with the ruling) |

## 3 — E3 · THE SHOT (Vertigo, ghosts, the two-step night)

| ID | Joint | Surface | Assertion | Class | Trunk file |
|---|---|---|---|---|---|
| E-T14 | E3 | Deck wheel (ghost) | At the running flip the `rotary_wheel_view` tag is still present and the hosted view is the *same instance* as at idle — the ghost is a hoist, never a second AndroidView. REF: Rad T1 (§4). | RENDER | `SealShotGhostTest.kt` (new, Regal-named for Rad §4) |
| E-T15 | E3 | Deck wheel (ghost) | After a fling release (ω ≈ +900°/s) and the flip, the ghost's θ is identical to the last drawn θ_F at F, t0+500 and t0+1049, live ω is 0 thereafter, and `onMinutesChanged` never fires again. REF: Rad T2 (§4). | RENDER | `SealShotGhostTest.kt` |
| E-T16 | E3 | Deck wheel (ghost) | A bitmap capture at the seal frame contains ink at the dot locus computed from θ_F, the ghost is still present at t0+1049, and one frame after t0+1050 the tag is absent and the progress-track slot is composed — present at the seal frame, gone after Settle. REF: Rad T3 (§4). | RENDER | `SealShotGhostTest.kt` |
| E-T17 | E3 | Deck wheel (ghost) | A flick landing off-detent leaves the ghost at θ == θ_F exactly (caught, not seated) and no seat knock appears in the sound/haptic fake's log between F and t0+1050. REF: Rad T4 (§4). | RENDER | `SealShotGhostTest.kt` |
| E-T18 | E3 | Deck wheel (ghost) | After the exit, 5000ms of advanced clock plus per-second session ticks never recompose the wheel tag while the session is active, and the post-session fresh wheel sits at `minutesToAngle(dialMinutes)`. REF: Rad T5 (§4). | RENDER | `SealShotGhostTest.kt` |
| E-T19 | E3 | Deck sled (ghost) | At the SEALED edge the captured sled fraction is exactly 1.00f with pixel position identical at t=0/400/1050, and throughout 0–1050 the composition holds exactly one wheel view, one sled view and zero progress-track branch. REF: Zug-II T4 + T5 (§7). | RENDER | `SealShotStateTest.kt` (new, Regal-named for Zug-II §7) |
| E-T20 | E3 | Shot clock (source) | `t0Mono` is the only seal-time sample Slice E reads, `SystemClock.uptimeMillis` is the named clock at controller and Deck, and no `delay(` exists in any E3 read path. REF: Zug-II T1 (§7). | UNIT (grep) | — (grep gate; receipt in BUILD-CHANGES) |
| E-T21 | E3 | Shot on hardware | Killing and relaunching mid-shot at elapsed ≈500ms recomposes the shot at fraction ≈0.48 ± 1 frame with no travel from 0, and relaunch after 1050ms shows no shot, night ground already stepped, ghosts at settle values. REF: Zug-II T2 (§7). | DEVICE | — (device pass; result in BUILD-CHANGES) |
| E-T22 | E3 | Deck on running session | Opening the Deck on an already-running session shows both night signals true on frame 1 with no animation and no 400ms step. REF: Zug-II T3 (§7). | RENDER | `SealShotStateTest.kt` |
| E-T23 | E3 | Deck night (two-step) | Frame captures at t=0/399/400/401 show the hero glyph trio night from t=0 while eyebrows, panel scheme, wheel and sled stay day at 399 and step exactly once at t=400 — `effectiveNight` lands as glyph t=0, ground t=400, never one instant flip. REF: Zug-II T6 (§7); the root scheme half is E-T34. | RENDER | `SealShotStateTest.kt` |
| E-T24 | E3 | Deck input (death) | Taps/swipes at t=100/500/1000 on slide, wheel, hub, FLOW key, task line, scroll, every nav tab and header buttons change zero state during the shot and queue nothing after Settle, and at the SEALED edge all pending press/coast/drag state is cleared. REF: Zug-II T7 + T8 (§7). | RENDER | `SealShotStateTest.kt` |
| E-T25 | E3 | Shot frame-diff | Across the full shot there is no run of ≥2 consecutive identical full-screen frames between t=0 and t=1050 — zero freezes, the spectacle ledger stays at four. REF: Zug-II T11 (§7). | RENDER | `SealShotStateTest.kt` |
| E-T26 | E3 | Shot on hardware | Across 10 seals the t=400 recomposition frame costs ≤ 2× the shot's median frame time; a miss routes to the filed blur fallback, never to retiming the step. REF: Zug-II T9 (§7). | DEVICE | — (device pass; result in BUILD-CHANGES) |

## 4 — E5a · THE HUB CORE (D6 light)

| ID | Joint | Surface | Assertion | Class | Trunk file |
|---|---|---|---|---|---|
| E-T27 | E5a | Hub cap (rest core) | At rest, day and night, the pixel at bearing 315° is the core-lifted cap (10% day / 8% night mix, ±3/channel), the 135° pixel is the flat cap, the +8° pixel is still on the ±9° plateau, and the +21° pixel sits strictly between cap and peak on the cos² falloff to ±34°. REF: Nabe §7.1. | RENDER | `HubSpecularTest.kt` (new, Nabe §7 — TetradRenderTest shape) |
| E-T28 | E5a | Hub cap (count law) | Eight sample points {315°, 135°, 45°, 225°} × {0.35, 0.85 · innerHubR} are vector-identical across renders at counts 1, 2, 3, 4, 8 and FLOW — the core exists and the count is never an input to it. REF: Nabe §7.2. | RENDER | `HubSpecularTest.kt` |
| E-T29 | E5a | Hub press (travel) | A hub DOWN runs the 45ms PRESS dip to depth 1 with no commit callback, UP fires the commit exactly once with the 160ms SEAT return already running on that same frame, and FLOW starts no dip at all. REF: Nabe §7.3. | RENDER | `HubSpecularTest.kt` |
| E-T30 | E5a | Hub on hardware | DEVICE_PASS_E5A as filed: at arm's length, day and night separately, the core's travel is seen unprompted on ≥2 of 3 holds per mode, or the gate constant flips to 0 and only the travel dies (rest core and dip survive). REF: Nabe §6/§7 gate. | DEVICE | — (device gate; pass/fail + mode recorded in BUILD-CHANGES) |

## 5 — E5b · THE ORANGE EXIT (per-carrier, day + night)

| ID | Joint | Surface | Assertion | Class | Trunk file |
|---|---|---|---|---|---|
| E-T31 | E5b | Shading resolver | The resolver returns §1's table hex-for-hex in both modes at phases 0–3, including the round-half-up edge (night green 66·.75 = 49.5 → 50); a truncating implementation fails this test. REF: Horiz T1 (§6). | UNIT | `OrangeExitTest.kt` (new, Regal-named for Horiz §6) |
| E-T32 | E5b | Exit carriers B1–B3 | With steps expanded and ≥1 step done, cap and trail pixels read ×1.00 at t=0, ×0.75 at t=50, ×0.50 at t=100, the terminal token at t=150 (cap/trail → `tokControlFill` at unchanged α55 for the trail; numerals → `onSurfaceVariant`) in the day column and the night column alike. REF: Horiz T2 (§6). | RENDER | `OrangeExitTest.kt` |
| E-T33 | E5b | Whole Deck + header | Full Deck + header frames at t=200 and t=399 contain no pixel equal to any accent or shaded-accent value — zero orange, born carriers (progress fill, session dot, grace chip + badge) included in the scan. REF: Horiz T3 (§6). | RENDER | `OrangeExitTest.kt` |
| E-T34 | E5b | Theme scheme (F23 pin) | In a day seal the live scheme is still the day scheme at t=100 (header dot `#2C2A29`, cap step-2 `#B83E0F`) and scheme identity changes exactly once, at the first frame ≥ t=400; a night-user seal never sets the hold. REF: Horiz T4 (§6); Deck-signal half is E-T23. | RENDER | `OrangeExitTest.kt` |
| E-T35 | E5b | Born carriers B4–B6 | On the first running frame the progress fill, chrome session dot and grace chip are already composed at their ink tones (never born orange), badge included, with the EMERGENCY BREAK chip's badge asserted non-accent in the same test. REF: Horiz T5 (§6). | RENDER | `OrangeExitTest.kt` |
| E-T36 | E5b | Scheme hold (safety) | An annulled seal and a simulated process death before t=400 both clear the hold, so the theme follows live session state on the next composition with no stuck day scheme. REF: Horiz T6 (§6). | RENDER | `OrangeExitTest.kt` |
| E-T37 | E5b | Carrier sites (source) | If `builds/slide` merges before E5b builds, B1/B2 sites and the host's `isArmedState = false` forcing are re-grepped in the merged tree and §2's pins amended before the first E5b line is written. REF: Horiz T7 (§6). | UNIT (grep) | — (grep gate; receipt in BUILD-CHANGES) |

## 6 — E4 · RELEASE PROOF + THE FINISH (E0, detention, pulse, receipt)

| ID | Joint | Surface | Assertion | Class | Trunk file |
|---|---|---|---|---|---|
| E-T38 | E4 | Finish controller (E0 edge) | E0 is stamped from the session StateFlow edge — not the 1s ticker — within one frame of the repository flip (`!isActive && completedSuccessfully`), and fires exactly once for that sessionId across recomposition, re-entry and collector restart. OWN (Wren §4.1's acceptance, unfiled as a test by any seat; routine-session form is Reihe T-R6, cross-ref). | UNIT | `ReleaseProofTest.kt` (new, Regal) |
| E-T39 | E4 | Release Proof (happy detention) | A shield confirmed dead at +120ms yields RELEASED with P = +120 ≤ E1200 and the receipt formula issuing at E1480. REF: Draht T1 (§7). | UNIT | `ReleaseProofTest.kt` (Regal-named, shared with Draht §7) |
| E-T40 | E4 | Release Proof (faulted detention) | With `shieldInstanceAlive` held true past the 400ms halt, no RELEASED and no pawl lift occur (the `shieldProtecting` flag alone never lifts it), and ENDED-FAULT issues at exactly E1800 with a fault list of exactly one line — SHIELD — in every branch of every test. REF: Draht T2 + T3 (§7). | UNIT | `ReleaseProofTest.kt` |
| E-T41 | E4 | Release Proof (slow detention) | For a pawl-lift frame P with E1200 < P ≤ E1520, receipt t=0 equals P + 280 (≤ E1800) and the held frame is not extended past its 1200ms life. OWN (Wren §4.3 slow branch; Draht tests the two boundaries, not this branch). | UNIT | `ReleaseProofTest.kt` |
| E-T42 | E4 | Release Proof (unwitnessed detention) | If the Deck is uncomposed for the whole detention (user navigated away, process alive), the pending receipt still issues exactly once on its formula, is settled — not replayed — when the Deck next composes, and no held frame is re-shown. OWN (Wren §4.3 detention definition + Fen's unwitnessed law applied to E4; the process-death variant is E-T43). | UNIT | `ReleaseProofTest.kt` |
| E-T43 | E4 | Finish on hardware | A real process kill mid-RELEASING, followed by relaunch, issues the receipt exactly once (immediately if the ceiling has passed), never replays the held frame, and never re-fires the completion composite for that sessionId. OWN (MASTER-PLAN §5.9-10's "across process death and reopen"; the simulated form is E-T57). | DEVICE | — (device pass; result in BUILD-CHANGES) |
| E-T44 | E4 | Silent retry (persistence) | A faulted finish persists `releaseFaultShield = true, retriesDone = 0` on the session row, survives process death, re-arms alarm 1 at boot, and a +30s receiver pass over a now-dead instance clears the columns without a second receipt issuance. REF: Draht T6 (§7). | UNIT | `ReleaseProofTest.kt` |
| E-T45 | E4 | Silent retry (supersede + heal + no-poll) | A new seal cancels a prior fault's retry alarms and stale receiver fires no-op, a Deck-open pass heals a dead instance silently, and during a 400ms unanswered wait the controller initiates zero reads beyond the suspended `first`. REF: Draht T7 + T8 + T9 (§7). | UNIT | `ReleaseProofTest.kt` |
| E-T46 | E4 | Reverse cascade | Links are commanded in exact reverse seal order, each on the frame the previous returns CONFIRMED-OPEN, a 400ms timeout halts the cascade at that link, already-opened links are never re-shut, and downstream links stay shut. OWN (Wren §4.3 timing law; Draht T4 pins the 399/401 boundary for the single commanded link — the multi-link order itself was filed by no E seat). | UNIT | `ReleaseProofTest.kt` |
| E-T47 | E4 | Held frame (timeline) | Captures at E0/E300/E700/E880/E1200 show the filed timeline — bed out by 300, true silence 300–700, pulse at E700 with the picture pixel-identical to E699, knock pair at E880, CUT 0 at E1200 — and one tap anywhere in E0–E1200 jumps straight to the cut. OWN (Wren §4.2 table; the E699/E700 diff half is Linea T6, cross-ref). | RENDER | `FinishHeldFrameTest.kt` (new, Regal) |
| E-T48 | E4 | Completion pulse (eligible) | For a verified fully-elapsed clean session, a counting fake engine records exactly one `playCompletionPulse()` call, at E700, and zero calls at E0 and at the old ratchet-warning site. OWN (REGISTER: pulse reserved for verified fully-elapsed sessions only). | UNIT | `ReleaseProofTest.kt` |
| E-T49 | E4 | Completion pulse (death-booked) | A session booked BROKEN under the §5.6 death law that nevertheless elapsed fully records zero `playCompletionPulse()` calls — no pulse, ever (REGISTER settlement B-12, ratified). OWN (MASTER-PLAN §5.7/§5.9-8 state the law; no seat filed the test). | UNIT | `ReleaseProofTest.kt` |
| E-T50 | E4 | Completion pulse (valve-ended) | A valve-ended (BROKE) session records zero `playCompletionPulse()` calls and takes the 0ms splice form, never the held frame. OWN (MASTER-PLAN §5.7 BROKE; the routine PARTIAL form is Reihe T-R8, cross-ref). | UNIT | `ReleaseProofTest.kt` |
| E-T51 | E4 | Receipt (STILL BLOCKED — render half) | On a faulted finish, captures of the receipt, the idle Deck, the imprint slot, the running screen and the AOD face show the STILL BLOCKED line exactly once — on the receipt at issuance — and on no other surface. OWN (Wren §4.3/§4.5 home law; grep half is E-T52 — the pair is the test, neither half alone passes the joint). | RENDER | `ReceiptRenderTest.kt` (new, Regal — ScreenshotHarnessTest shape) |
| E-T52 | E4 | Whole tree (STILL BLOCKED — grep half) | `grep -rn "STILL BLOCKED" app/src/main` returns hits only inside the receipt's composition site — no imprint, Deck, Shield, chrome or AOD source renders the line family. OWN (pair with E-T51). | UNIT (grep) | — (grep gate; receipt in BUILD-CHANGES) |
| E-T53 | E4 | Imprint at finish | The SEALED tenant is present at E1199, the re-measured IDLE tenant is in place by E1350, the pressure lip's alpha is 0 by E1440, and the receipt strikes at E1480. REF: Linea T8 (§7). | RENDER | `ReceiptRenderTest.kt` (Regal-named, shared with Linea §7) |
| E-T54 | E4 | Finish (no reverse Vertigo) | No frame in E1200–receipt-t0 samples `vertigoT` backwards: the wheel is found at scale 1.0 in the defocused Deck, no element travels 0.80→1.0, +20dp→0 or α0.50→1.0 by animation. OWN (Wren §4.4 timing law, unfiled as a test). | RENDER | `ReceiptRenderTest.kt` |
| E-T55 | E4 | Receipt (snapshot truth) | Completing the session's task from Tasks during E1200–E2480, or emitting a new activeTask mid-detention, changes neither the receipt's E0-frozen title/counts nor the hero/dial before rest. REF: Linea T1 + T2 (§7). | UNIT | `FinishEndStateTest.kt` (new, Regal-named for Linea §7) |
| E-T56 | E4 | Task line at finish | In sealed and end-state branches the task-line subtree contains zero clickable/semantics targets (DONE absent, not disabled), and `completeTaskAndAdvance` / `toggleTaskSubStep` / `deferTask` during RELEASING and RECEIPT write nothing and raise no banner. REF: Linea T3 + T4 (§7). | RENDER | `FinishEndStateTest.kt` |
| E-T57 | E4 | Finish (once-ever, simulated death) | A simulated process death at E1500 (fresh controller/VM over the same database) relaunches to the receipt settled with its strike not replayed, the sled at its ReceiptState-derived position, and the sequence № unchanged. REF: Linea T10 (§7); the real-kill form is E-T43. | UNIT | `FinishEndStateTest.kt` |

## 7 — E6 · THE SLED GOES HOME (rest contract, silence)

| ID | Joint | Surface | Assertion | Class | Trunk file |
|---|---|---|---|---|---|
| E-T58 | E6 | Return gate (once-only) | The gate's first `tryStart` is true and its second false, `void`-then-`tryStart` is false, `expire` unwitnessed yields VOID while witnessed stays ARMED, and a duplicate `publishReceipt` keeps the first deadline. REF: Schlitten T1 (§8). | UNIT | `SledReturnGateTest.kt` (new, Schlitten §8) |
| E-T59 | E6 | Sled rest contract (truth table) | `initialFraction` resolves as: running → 1f; receipt+ARMED → 1f; receipt+FIRED → 0f; receipt+VOID → 0f; null receipt and not running → 0f — an unwitnessed return therefore composes at rest (0f) on next Deck entry, with nothing scheduled. REF: Schlitten T2 (§8) + T9 (process-death construction). | UNIT | `SledReturnGateTest.kt` |
| E-T60 | E6 | Sled host (single writer) | During RETURNING, every host `update{}` assignment the Deck performs leaves the fraction trajectory unchanged frame-by-frame, and `initialFraction` appears only in the factory block and the view constructor — never in `update{}`; `ReceiptState` is referenced in exactly {VM, Deck, MainActivity} and in no persistence file. REF: Schlitten T5 + T6 (§8). | UNIT (grep) | `SledReturnGateTest.kt` + grep receipt in BUILD-CHANGES |
| E-T61 | E6 | Sled return (completion) | A view created at `initialFraction = 1f` and sent `animateReturnHome()` reaches fraction exactly 0f with `isReturning` false after 650ms of 16ms shadow-choreographer steps. REF: Schlitten T3 (§8). | RENDER | `SledGoesHomeViewTest.kt` (new, Regal-named for Schlitten §8 Robolectric block) |
| E-T62 | E6 | Sled return (silence, vibrator half) | With `soundEngine = null` and the vibrator fallback armed, `ShadowVibrator` records zero vibrations across the full return, cap-capture freeze, off-cap freeze + resume, and second-interrupt rest. REF: Schlitten T4 (§8). | RENDER | `SledGoesHomeViewTest.kt` |
| E-T63 | E6 | Sled return (silence, engine-spy half) | With a counting fake sound engine injected (non-null), the return window receipt+1000 → +1650 — including resume and arrival — records zero `play*` / feel calls of any name; arrival is marked only by the animator ending. OWN (the spy shape Schlitten's null-engine T4 and source-gate T6 do not cover: a live engine that *could* sound, proving it doesn't). | UNIT | `SledReturnGateTest.kt` |
| E-T64 | E6 | Sled return (interrupt) | At frozen fraction 0.5, a DOWN at the slab centre captures with the fraction unchanged by the DOWN itself and the first MOVE origin-relative, a DOWN at x = 100dp captures nothing and its UP resumes at exactly 325ms (650 × 0.5), and a second off-cap interruption leaves no animator running. REF: Schlitten T7 (§8); Linea T9 re-asserts the cap capture from the host frame, cross-ref. | RENDER | `SledGoesHomeViewTest.kt` |
| E-T65 | E6 | Sled cap (tones) | The cap fill equals `tokControlFill()` through detention and `tokAccent()` on the arrival frame, day and night. REF: Schlitten T8 (§8). | RENDER | `SledGoesHomeViewTest.kt` |
| E-T66 | E6 | Sled return (accent-free) | During RETURNING, captures show no accent trail pixels beyond the cap itself (trail suppressed under `isReturning`), the label's alpha follows the existing `1 − 1.3·frac` law, and the cap already wears the idle words — swapped at ReceiptState publication, not at return end. OWN (Fen §3b interface needs 2–3, slice-de-parts/fen.md; no E-wave seat filed the pixel half). | RENDER | `SledGoesHomeViewTest.kt` |
| E-T67 | E6 | Sled return (motion law) | The return's sampled fraction matches Spule's table for `PathInterpolator(0.3, 0, 0.5, 1)` over 650ms (u 0.10 → x 0.963 … u 0.50 → x 0.380 … u 0.90 → x 0.014, velocity 0 at both ends), and the interpolator is neither the seat spring nor the refusal spring. REF: Spule §3.4 (11-spule.md). | UNIT | `SledReturnGateTest.kt` |
| E-T68 | E6 | Sled on hardware | One device pass, sound on, phone on a hard surface: receipt → sled holds 1000ms → travels 650ms → rest, interrupted once by cap and once off-cap, with nothing heard and nothing buzzed at return, resume or arrival. REF: Schlitten §8 device pass (shared with the F2 A/B gate). | DEVICE | — (device pass; result in BUILD-CHANGES) |

## 8 — MIGRATION 9→10 (the plumbing batch + the F15 ride)

| ID | Joint | Surface | Assertion | Class | Trunk file |
|---|---|---|---|---|---|
| E-T69 | E4/E6 data | Room database 9→10 | A v9-shaped fixture (focus_sessions rows with known values in every pre-existing column, no E columns) opened through `MIGRATION_9_10` gains `heldAppsAtSeal` defaulting to −1 on every legacy row plus Draht's fault columns at their defaults (`releaseFaultShield` = 0, `releaseRetriesDone` = 0, `releaseFaultedAtWallMillis` = 0), every legacy row and column value survives unchanged, the batch's append/child tables exist, and no `fallbackToDestructiveMigration` appears in the builder. OWN — TokenMigrationTest's v7→v8 shape carried one chain up; **conditional on §8-U6**: the expected schema is the recommended one (F15 + F-DRAHT-2) until Voss registers it. | MIGRATION | `SessionPlumbingMigrationTest.kt` (new, Regal — TokenMigrationTest pattern) |

---

## 9 — COUNTS, COVERAGE, AND WHAT IS NOT TESTABLE AS WRITTEN

**Counts (69 rows):** UNIT **31** (of which 5 are UNIT (grep)
gates: E-T03, E-T20, E-T37, E-T52, E-T60) · RENDER **29** ·
DEVICE **8** (E-T10, E-T11, E-T12, E-T21, E-T26, E-T30,
E-T43, E-T68) · MIGRATION **1** (E-T69). REF rows: 53 ·
OWN rows: 15 · CONFLICT rows: 1 (E-T13).

**Brief-minimum coverage map** (every item the brief named,
and where it lives): bit-identity across arm provenances →
E-T01 (+ E-T13, conflicted for the routine path) · one-call-site
grep → E-T03 · cadence landmarks (intent −210 / attack t=0 /
audio ≤ +90) → E-T07, E-T08, E-T05, E-T10 · ghost at the seal
frame + absent after Settle → E-T16 (+ E-T14/T15/T17/T18/T19) ·
effectiveNight two-step → E-T23 + E-T34 · hub core existence +
count-independence → E-T27 + E-T28 · orange exit per-carrier at
t=0/50/100/150/200, day + night → E-T32 (+ E-T31/T33/T35) ·
E0 edge + composite never re-fires per sessionId → E-T38,
E-T43, E-T57 · detention ≤ E1800 in happy / faulted /
unwitnessed / process-death branches → E-T39, E-T40, E-T41,
E-T42, E-T43 (with §8-U4's correction) · pulse eligibility →
E-T48, E-T49, E-T50 · STILL BLOCKED receipt-only home →
E-T51 + E-T52 (the pair) · Migration 9→10 → E-T69 · sled rest
contract → E-T59 + E-T60 (+ E-T58) · silence of the sled
return → E-T62 + E-T63 (+ E-T68 on hardware).

**Untestable as written — named, not smoothed over:**

- **U1 · The ±8ms audio-vs-haptic lock is a measurement, not a
  test.** Siegel (risk 1), Riegel (T7) and Klinke (§8 tail) all
  agree it is not constructible from code — haptic on the
  caller's thread, audio through the synth thread and device
  buffer. E-T10 therefore *reports a number*; any merged text
  that turns ≤8ms into a pass/fail gate is untestable as
  written.
- **U2 · On-device acoustic bit-identity does not exist as a
  claim.** Siegel §5 disclaims it in terms: the hash proves the
  buffer; speaker, AudioTrack buffering and room are outside
  the law B-5 states. E-T01 is the whole of bit-identity; do
  not let the merge upgrade it.
- **U3 · The routine-arm seal is a live contradiction, and
  E-T13 is shelved for it.** Reihe (04, T-R1/T-R4) files a
  routine arm firing one bit-identical Throw through a shared
  transition; Klinke (03, T13) pins the routine path *silent*
  in this slice; Siegel's F-S1 prices convergence at +≈18 or
  defers it to the routines rebuild (recommended). B-5's words
  ("fired at every seal, whatever window or hand armed it")
  are testable only after Ren rules which seat's slice this
  is. Until then the strongest honest statement is Siegel's:
  provenance *simulation* by parameterlessness (E-T01) passes;
  the routine path itself is unproven either way.
- **U4 · "Detention ≤ E1800 in any branch" is false as written
  for the process-death branch.** Wren §4.3's own text: on
  relaunch the receipt issues "on its formula against the
  original E0 if still inside the ceiling, **else
  immediately**" — a relaunch hours later issues hours late,
  lawfully. The testable law is: ≤ E1800 *while the process
  lives* (E-T39–E-T42), and *immediately on first relaunch,
  exactly once* after death (E-T43, E-T57). The merged file
  should print the law in that form.
- **U5 · Wren's per-app STILL BLOCKED example is unreachable.**
  `` `INSTAGRAM — STILL BLOCKED (PERMISSION)` `` presupposes
  per-app fault lines; Draht §2.4/T3 proves the fault grammar
  is one link — SHIELD or nothing — and a SESSION line is
  likewise unreachable. E-T40/E-T51 test the one-link grammar;
  the per-app form is dead text, untestable because unbuildable
  under F6 as ruled.
- **U6 · Migration 9→10's expected schema is not yet fully
  determined by the REGISTER.** The Register's 9→10 entry
  enumerates the plumbing batch (step-rows + completion-events
  + readiness log) and does **not** list `heldAppsAtSeal`
  (Emil, riding on F15's *recommendation*) or Draht's three
  fault columns (riding on F-DRAHT-2's *recommendation*).
  E-T69 is written against the recommended schema; if either
  ride is refused, the fixture's expected columns change and
  the row must be re-shelved at merge — Voss to register.
- **U7 · The finish's bed-out (E0–300) has no bed to test.**
  Riegel §7.4: the trunk has no session bed at all and the
  SESSION-BED build is unassigned. E-T47 tests the held frame's
  picture, silence and timestamps; the bed's *level* curve is
  untestable until that build lands and exports its catch
  contract (Riegel's `TAIL_CATCH_SAMPLE` export is ready).
- **U8 · Two "tests" in the wave are human judgments wearing
  test clothes.** DEVICE_PASS_E5A (E-T30) is a person seeing a
  2dp shift unprompted, ≥2 of 3 holds, per mode — no render
  test can stand in for it, and E-T27–E-T29 must never be
  cited as its substitute. Likewise the seal room gate
  (E-T12) is ears in a quiet room. Both are filed here as
  DEVICE obligations with recorded outcomes, which is the
  honest form; neither automates.
- **U9 · The pulse's "0 dB ref" is not checkable.** No
  calibrated reference level exists anywhere in the trunk or
  the plan; E-T48 tests the pulse's timing, uniqueness and
  site — its absolute level is untestable as written and
  should be read as "the engine's existing reference gain",
  or struck at merge.
- **U10 · "Intent −210" names a moment no code can observe.**
  The user's intent has no timestamp in the system; the only
  observable is its shadow in the t0 formula (latch + 210ms,
  E-T07). Any acceptance text claiming to measure the cadence
  from *intent* is untestable; from *latch* it is exact.

## 10 — OPEN SLOTS FOR ILYA AT MERGE

Filed before this skeleton and indexed above: 01-siegel,
02-riegel, 03-klinke, 04-reihe, 05-draht, 07-rad, 08-zug-ii,
09-nabe, 10-horiz, 11-spule, 13-schlitten, 19-linea.
Not yet filed / not indexable from here, in the order the
merge will meet them: any Fen E6 wave file (E6's rows above
rest on Schlitten + Fen's slice-de filing + Spule; if Fen
files, his test list takes REF precedence over E-T66);
24-fenja is a price count, no tests to index; the SESSION-BED
build (unassigned — its tests, when seated, slot under E4
beside E-T47 and close U7); Opal's D8 link-check tests and
Emil's receipt-composition tests are Slice D / receipt-build
property, excluded from E4's price in the build-ready file —
they index into the merged library only if Ilya rules the
merged file carries D's library too. Seat files arriving
after this one append REF rows at the next free E-T number;
no existing ID moves. *Post-filing arrivals, noted the same
run: 20-band (nav under E3/E4 — its assertions slot in at
E-T70+, beside E-T24's nav half), 25-fach and 26-taste
(price counts — no tests to index).*

## NEXT (Law 10)

- **I want next:** the merge pass itself — sit with Ilya while
  these 69 rows are folded into the merged file, so REF
  pointers are re-pinned against the seats' final test numbers
  (several seats renumber at merge; a stale pointer is a lie
  with a citation).
- **I could do, unassigned:** the same librarian pass over
  Slice D's acceptance material (Teo's A1–A10, Opal's §F2
  tests, the D8 cascade's ~130 filed tests in Klinke's price)
  so the merged D+E library is one shelf, one numbering law,
  no assertion filed twice under two names.

*— Regal, Improvers · the shelf*
