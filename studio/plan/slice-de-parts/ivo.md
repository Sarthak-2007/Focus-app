# Ivo — slice D/E part: THE LAID SEAL (seal entry 2)

*Seat: Ivo, Wild, sound (fixed pitch sets, no pitch formulas — nothing
below computes a pitch from anything; the seal's 360 Hz identity is a
literal, inherited from the Throw). Coordinator: Marlowe. Joint: seal
entry 2 only. Orson holds entry 1 (the Throw) and the room layer.
Law as given: two entries, ONE terminal cadence 90 + 120 = 210 ms,
eyes-closed indistinguishability; REGISTER governs (SEAL CADENCE:
210 ms; t=0 = max(last link CONFIRMED-SHUT, latch + 210 ms,
pawl-seated frame)). Line numbers verified this run against the live
tree `Focus-app/` (not the token/counterfoil build copies).*

## (1) Verdict

**BUILDS, as a parameterised variant of the existing seal primitive
plus ONE new sub-primitive (the bolt) — with five exact amendments,
four of which are forced by arithmetic or by the real engine, not by
taste:**

- **A1 — Orson's 200 ms Laid breath is REJECTED; breath stays 120 ms
  for both entries.** A 200 ms breath makes the Laid cadence
  90 + 200 = 290 ms and moves REGISTER's t=0 floor (`latch + 210 ms`).
  The task brief's own Laid table omits the breath change; REGISTER
  outranks both. The dawn house gets its quiet from placement, not
  from a longer held breath only one entry takes.
- **A2 — "Body 48→72 ms" must stretch the decay, not just the buffer.**
  In this engine the body envelope is `exp(-0.0114)`/sample: at
  10 ms it is already at 0.0065 of peak. A 72 ms buffer at the filed
  decay is ~60 ms of silence padding behind a dead transient. Laid
  body = 72 ms duration **and** decay step `exp(-0.0114 / 1.5)` =
  `exp(-0.0076)`/sample (time constant ×1.5, matching 48→72 exactly).
- **A3 — The wet send (−26 dB) is a stored field and a NO-OP in this
  slice.** There is no room in the code — no comb, no allpass, no wet
  path anywhere in the engine (see §2). Faking "wet" as a gain cut
  would be a lie in the one voice that may not lie. Fork F1 for Ren.
- **A4 — The haptic twin's "≈200 ms" is an arithmetic error, and the
  swell cannot sit under the bolt.** The brief's segments sum
  36 + 30 + 22 + 60 = **148 ms**, not ~200. Worse, the swell "under
  the bolt at +18 ms" is unbuildable on one actuator: a
  `VibrationEffect` waveform is serial segments at constant amplitude,
  the body segment already runs at amplitude 1.0 (no headroom to add
  a swell into), and a second `vibrate()` call cancels the first.
  Amended waveform in §3: the swell becomes the post-shadow settle
  (88→148 ms @ 0.35). The mass still arrives in the palm; it arrives
  as a settle, not as an overlay that physics refuses.
- **A5 — The level rule cannot read the records Orson's file says it
  reads, because they do not exist.** There is no
  `routineFireTimestamp`, no event store, no primed-Deck field, and
  no `routineId` on any session row in the shipped tree (§2). The
  predicate in §3 is therefore written against Sten's
  Derive-Don't-Fire derivation plus the two session columns REGISTER
  already books in migration 8→9 (`routineId`, `windowStartMillis`)
  — and it requires one behaviour those columns make possible: a
  manual session sealed on a primed Deck is **stamped** with the
  priming routine's `(routineId, windowStartMillis)`, so "no seal
  yet in that window" has a record to read.

Everything else in the brief's Laid table builds exactly as given:
body −14 dB, attack 3→12 ms, lowpass 2.4 kHz, bright partial −8 dB
further, bolt −10 dB absolute at +18 ms / 70 ms / LP 900 Hz, manual
always Throw, UNKNOWN always Throw, never labelled in the UI.

## (2) Against the real code — what the engine can and cannot do today

`service/TactileSoundEngine.kt` (378 lines, live tree):

**Can, today:**
- One Float-PCM transient per command on a dedicated thread
  (`runFloatPcmSynthLoop`, :85–143), 44.1 kHz mono
  (`SAMPLE_RATE`, :49), zero audio files.
- A 4-partial stack with fixed weights `{0.6, 0.28, 0.37, 2.2}`
  (`PARTIAL_WEIGHTS`, :51): partials at 1.0×f0, 2.0×f0×`chordRatio`,
  3.0×f0, 4.7×f0 (render loop :130–133). Note for the record: these
  are **not** my desk's wood ratios (1 / 2.76 / 5.40) and not
  Orson's prose "same stack" in any other sense — the seal's pitch
  identity lives in these four ratios, and the Laid Seal inherits
  them untouched.
- Dual exponential envelopes: body `exp(-0.0114)`/sample
  (`BODY_DECAY_STEP`, :53) over partials 1–3, tail
  `exp(-0.0313)`/sample (`TAIL_DECAY_STEP`, :54) over partial 4
  only; sample = `((p1+p2+p3)·envBody + p4·envTail) / weightNorm ·
  gain` (:136), envelopes stepped at :139–140.
- Per-command `fundamentalHz`, `durationSamples`, `gain`,
  `chordRatio` — the entire parameter surface (`SynthCommand`,
  :57–62). Buffers capped at 160 ms (`maxSamples`, :85; render
  `count` coerced at :121) and 6,000 samples ≈ 136 ms at enqueue
  (`enqueueTransient`, :235–242). The Laid composite (88 ms, §3)
  fits both caps with room to spare — **no cap change is needed.**
- Waveform haptics with per-segment amplitudes from the same call
  site as the audio (`triggerWaveform`, :190; e.g. the seal at
  :336–355 fires its waveform first, then enqueues its transient —
  that ordering is the existing ±8 ms lock mechanism, such as it
  is; see §6 for its real latency).
- The Throw itself: `playHarmonicSeal(arming = true)` (:336) —
  f0 360 Hz, 48 ms, gain 0.62 (= 0 dB ref), `chordRatio` 1.5;
  haptic `[0, 24, 38, 16] ms @ [0, 255, 110, 175]`. One correction
  to Orson's §1 table: his "reference haptic 24 @ 1.0, **38 ms
  gap**, 16 @ 0.69" misreads this waveform — the 38 ms segment runs
  at amplitude 110/255 ≈ 0.43, a shoulder, not a gap. Total 78 ms
  is the only figure of his that survives.

**Cannot, today (each is a named gap the spec must pay for):**
- **No attack ramp.** Both envelopes initialise at 1.0 (:122–123);
  onset is a single sample. The Throw's filed "~3 ms attack" does
  not exist in code, and the Laid 12 ms attack is a new field, not
  a retune.
- **No lowpass — of any kind, anywhere.** No filter state, no
  coefficient, nothing. Both the 2.4 kHz body LP and the bolt's
  900 Hz LP are new code (§3 prices a one-pole, ~6 dB/oct — see §6
  for whether that slope is enough).
- **No noise source.** No `Random`, no LFSR, no noise buffer. The
  bolt — for **both** entries (Throw +30 ms / −6 dB fixed, Laid
  +18 ms / −10 dB) — is a voice the engine has never made. It is
  the one genuinely new primitive in this joint.
- **No second scheduled voice.** `commandQueue` is a FIFO
  (`LinkedBlockingQueue`, polled at :116); two enqueues render
  back-to-back, so a bolt enqueued after a 48 ms body would land at
  +48 ms, not +30 — and after a 72 ms Laid body at +72, not +18.
  Offsets inside the cadence cannot be expressed by the queue at
  all. §3 therefore renders body + bolt into **one composite
  command buffer**, sample-accurately.
- **No room / wet path.** No comb, allpass, pre-delay, or send
  exists. Wet −26 dB has nothing to attach to (→ A3 / F1).
- **No per-partial gain.** Weights are a companion constant and
  `weightNorm` is their fixed sum (:112–113). "Bright partial
  −8 dB further" needs a per-command partial-4 scale field.
- **The seal is played at least twice per arm, both times before
  the session exists** — the crime scene Ines §0 measured, still in
  the live tree: the view plays it on release
  (`ui/views/BlockitCoreViews.kt:1184`) and the ViewModel plays it
  again inside `armMonolithSession` (`ui/MonolithViewModel.kt:894`),
  before the insert that creates the session; ~15 further
  `playHarmonicSeal` call sites in the ViewModel (:574, :638, :672,
  :705, :723, :802, :828, :835, :869, :1017, :1026, :1057…) spend
  the seal's voice on task completions, undo restores and splits.
  My joint governs **only** the arm path's t=0 call; every other
  caller keeps the Throw, always (a task completion at 05:00 is not
  a dawn seal, and the Laid predicate must never be evaluated for
  it). Collapsing the arm path to the single t=0 call is Opal/
  Orson's standing law — my price (§5) includes only the routing
  of the voice *choice* into that call, not the collapse itself.
- **No routine facts at all.** `RoutineEntity`
  (`data/MonolithModels.kt:373–383`) is id / name / startMinutes /
  endMinutes / daysMask / loadoutName / enabled — nothing else.
  `FocusSessionEntity` (:249–276) carries no `routineId` and no
  window bounds. Worse for Orson's rule as filed: the shipped
  `RoutineAlarmReceiver` (service/, :35–50) does not prime a Deck —
  on START it **auto-starts a full session itself**. If that
  receiver still runs when this slice builds, every FASTED window
  already contains a session and the Laid Seal can never fire.
  This slice is therefore **downstream of Sten's routines rebuild**
  (windows derived, receiver demoted to a content-free doorbell,
  Deck primed instead of session fired) — a dependency, stated
  here so nobody builds the voice before the facts exist.

## (3) Amended spec — build-ready, exact

### (a) The recipe, in this engine's real terms

**Form: a parameterised variant of `playHarmonicSeal`, not a new
voice.** One new sub-primitive (the bolt) rendered inside the same
command. `SynthCommand` gains six fields, all defaulted to Throw
values so every existing caller is bit-identical:

```kotlin
val attackMs: Float = 0f,          // raised-cosine ramp, both envs
val decayScale: Float = 1f,        // body step = exp(-0.0114 / decayScale)
val partial4Scale: Float = 1f,     // bright partial, pre-norm
val lowpassHz: Float = 0f,         // 0 = bypass; one-pole, state reset per command
val boltDelayMs: Int = -1,         // -1 = no bolt (all non-seal callers)
val boltDurationMs: Int = 0,
val boltGain: Float = 0f,
val boltLowpassHz: Float = 900f,
val wetSendDb: Float = 0f          // stored, unrendered until the room lands (A3)
```

Render-loop changes (all inside the existing per-sample loop):
attack multiplier `0.5 − 0.5·cos(π·n / attackSamples)` for
`n < attackSamples`; body step per command as above (tail step
unchanged — the bright partial is supposed to die fast, and at
−8 dB further it dies quieter, which is the same fact twice, once);
partial 4 multiplied by `partial4Scale` **before** the norm, with
the norm recomputed per command as
`0.6 + 0.28 + 0.37 + 2.2·partial4Scale` (so −8 dB on p4 is a true
−8 dB on p4, not −8 dB diluted by a fixed denominator); one-pole LP
on the body sum only, `α = 1 − exp(−2π·fc / 44100)` (fc 2400 →
α ≈ 0.290); bolt rendered from a fixed-seed noise source
(`java.util.Random(0x5EED)`, re-seeded per command so the bolt is
the same object every morning and unit-testable), its own one-pole
at 900 Hz (α ≈ 0.121), envelope `exp(−3.0 · n / boltSamples)`,
summed **after** the body LP at sample offset `boltDelayMs`.
Buffer length = `max(bodyMs, boltDelayMs + boltDurationMs)`.

**The two entries, exact:**

| Field | Throw (entry 1, reference) | Laid Seal (entry 2) |
|---|---|---|
| f0 / chordRatio | 360 Hz / 1.5 | 360 Hz / 1.5 (identity untouched) |
| gain | 0.62 (0 dB ref) | **0.124** (= 0.62 · 10^(−14/20) = 0.1237) |
| attackMs | 0 (today's truth; Orson's "~3 ms" is not in code) | **12** (529 samples) |
| body duration | 48 ms | **72 ms** |
| decayScale | 1.0 | **1.5** (step `exp(-0.0076)`) — A2 |
| lowpassHz | 0 (bypass) | **2400** |
| partial4Scale | 1.0 | **0.398** (= 10^(−8/20)) |
| bolt delay / dur / gain / LP | +30 ms / 55 ms / **0.311** (−6 dB fixed, Moss cut honoured) | **+18 ms / 70 ms / 0.196** (−10 dB **absolute**: 0.62 · 10^(−10/20) = 0.1961) / 900 Hz |
| composite buffer | max(48, 30+55) = **85 ms** | max(72, 18+70) = **88 ms** |
| wetSendDb | native (room's own, Orson's) | −26 stored, unrendered — A3 |
| breath | 120 ms | **120 ms** — A1 |

Public surface: `playHarmonicSeal(arming: Boolean, voice:
SealVoice = SealVoice.THROW)`; disarm (`arming = false`) ignores
`voice` — there is no laid disarm, the SLEEP arm is S0 already and
a release is not a dawn event. Existing call sites compile
unchanged against the default.

**Haptic twin (amended per A4), fired by `triggerWaveform` inside
the same function, before the enqueue, exactly as the Throw does
today:**

| | Waveform (serial, ms @ amplitude) | Total |
|---|---|---|
| Throw (unchanged, today's code) | `[0, 24, 38, 16] @ [0, 255, 110, 175]` | 78 ms |
| Laid Seal | `[0, 36, 30, 22, 60] @ [0, 255, 0, 204, 89]` — 36 @ 1.0 body, 30 true gap, 22 @ 0.80 shadow, **60 @ 0.35 settle** (the brief's swell, repositioned to where a serial actuator can actually play it) | **148 ms** |

Fallback before API 26: one-shot 36 ms (the body segment), as
`triggerWaveform`'s existing `fallbackMs` contract.

### (b) The fact rule as a code predicate

One pure function, one home — `SealVoiceResolver.kt`, no Android
imports, unit-testable on the JVM:

```kotlin
enum class SealVoice { THROW, LAID }

fun resolveSealVoice(
    primedRoutineId: Int?,            // snapshot, see below
    routines: List<RoutineEntity>,    // routines table, as read at latch
    nowMillis: Long,
    sealExistsInWindow: (routineId: Int, windowStartMillis: Long) -> Boolean
): SealVoice
```

It returns `LAID` iff **all** of the following hold, and `THROW`
on any failure, any null, any exception (the caller wraps it in
`runCatching { … }.getOrDefault(THROW)` — UNKNOWN defaults Throw
by construction, not by a branch someone can delete):

1. **Routine-primed Deck:** `primedRoutineId != null`. Source:
   a new ViewModel field `primedRoutineId: Int?`, **derived, never
   fired** — set at Deck composition / resume from Sten's
   derivation (the routine whose window is OPEN at that instant
   and whose loadout/wheel the Deck is displaying), and
   **snapshotted into the arm attempt at ACTION_DOWN** (Joss's
   rule: the primed snapshot is locked for the gesture; a window
   closing mid-drag must not change the voice of a seal already
   being earned). A manual cold-open has `primedRoutineId == null`
   → Throw, at 05:00, at 03:00, always.
2. **First routine fired after the SLEEP window closed:** let
   `sleep` = the enabled routine with `loadoutName ==
   SmartLoadout.SLEEP.name` (no SLEEP routine in the table →
   Throw: the rule's anchor fact is absent, and absent = UNKNOWN).
   `sleepWindowEnd` = the end of the most recent SLEEP window at
   or before `nowMillis`, computed by `RoutineSchedule`
   (`data/MonolithModels.kt:384ff` — `isActiveNow` /
   `nextEndMillis` and the same overnight arithmetic: SLEEP
   20:00–03:30 crosses midnight, and the derivation must walk
   yesterday's start, exactly as `isActiveNow` already does).
   "Fired" under Derive-Don't-Fire is **window-open by
   derivation** — there is no fire event to read, and inventing
   one would resurrect the machinery Sten killed. Candidate =
   the non-SLEEP enabled routine whose derived window contains
   `nowMillis` and whose `windowStartMillis` is the earliest
   window start **strictly after** `sleepWindowEnd`. Require
   `candidate.id == primedRoutineId` (the Deck must be primed by
   that routine, not merely coexist with its window).
3. **No seal yet in that window:** `!sealExistsInWindow(
   candidate.id, candidate.windowStartMillis)`. The record: a
   new DAO query over `focus_sessions` —
   `SELECT EXISTS(SELECT 1 FROM focus_sessions WHERE routineId =
   :rid AND windowStartMillis = :ws)` — reading the two columns
   REGISTER already books for migration 8→9 (Sten's DDL:
   `focus_sessions ADD COLUMN routineId INTEGER; ADD COLUMN
   windowStartMillis INTEGER`). Today's history query
   (`MonolithDao.kt:63`, `LIMIT 60`) is not a substitute: a LIMIT
   window is not a fact. **The stamp (A5):** when a session is
   inserted from a primed Deck, the insert writes the attempt's
   snapshotted `primedRoutineId` and that routine's derived
   `windowStartMillis`, whether the hand or (in any surviving
   auto path) the engine started it. Without the stamp this term
   is unreadable and the whole rule degrades to "any session
   started inside the window," which counts a 06:10 manual seal
   as the window's first and lays the wrong seal at 05:00 the
   next day it matters. With the stamp, the 05:00 FASTED arm is
   the unique satisfier, as filed: SLEEP closed 03:30, FASTED
   opens 05:00, Deck primed by it, no stamped session yet.

**Evaluation instant: at latch, once, frozen on the attempt —
never at t=0, never at DOWN.** DOWN is too early (the drag may
cross a window boundary; the priming snapshot is taken there, but
terms 2–3 are time-of-seal facts). t=0 is too late and too hot:
t=0 is a vsync frame the SEALED transition executes; a Room read
there can miss the frame, and REGISTER's formula makes t=0 a
*consequence* (`max(…, latch + 210 ms, …)`), not a place to do
IO. At latch (`onLatch`, Cato's seat frame) the attempt already
exists, the gesture is committed, the proof cascade is running,
and a single local DAO read costs nothing against the 210 ms
floor. The resolver's result is stored as `attempt.sealVoice`
and the t=0 transition reads that field and nothing else. If the
latch read throws, the field is already initialised to `THROW`.

### (c) The indistinguishability test — shared landmarks

First, a reading that must be stated plainly: the 210 ms cadence
**ends at t=0**. It is snap 90 + breath 120, both *before* the
attack. Everything the Laid Seal changes is either inside the
breath (nothing — A1 keeps it silent and 120 long) or at/after
t=0. Bolt offsets (+30 vs +18) therefore **cannot** break the
cadence; they are interior to the post-t=0 composite, the way
body length (48 vs 72) is interior. The acceptance test is a
test of **landmarks**, not of waveforms — a waveform test would
fail the Laid Seal by definition, since being placed differently
is its entire content (fork F2 if Ren reads it otherwise).

Both entries, drag or tap, Throw or Laid, must hit these five
landmarks at these instants, and nothing else is promised:

| Landmark | Instant | Throw | Laid |
|---|---|---|---|
| L1 intent point (latch arrival / tap DOWN) | **−210 ms** | ✓ | ✓ |
| L2 seat frame — breath/dwell begins, all travel sound cut | **−120 ms** | ✓ | ✓ (breath 120, per A1) |
| L3 attack onset = SEALED write = session record = clock start | **t=0** | onset at t=0, peak ≈ t=0 (no ramp) | onset at t=0, peak at t=0 + 12 ms (the ramp is interior decay-side shape, onset does not move) |
| L4 audio composite complete, silence from here into the room catch | **≤ +90 ms** | bolt ends +85 | bolt ends **+88** — the +18 offset is bought back by the 70 ms bolt ending inside the same 90 ms frame the Throw's +30 + 55 already defined |
| L5 haptic event underway from t=0, issued from the same call site | t=0 | ends +78 | ends +148 — haptic duration is placement interior (the hand receives what the air steps down; Orson's own asymmetry), and is **not** a cadence landmark in any filed source |

Eyes-closed protocol, made testable: a listener who knows both
entries must fail to name the **entry path** (drag vs tap) above
chance, and must place L2 and L3 at the same instants for both
voices; he is *expected* to hear that the Laid Seal is nearer and
drier — that difference is the design, carried entirely between
L3 and L4, touching no landmark.

### (d) The haptic twin's call path

**Engine waveform branch — not `fireFeel`.** The twin fires from
`TactileSoundEngine.triggerWaveform` (:190), called as the first
statement of the parameterised `playHarmonicSeal(arming = true,
voice)`, before the composite enqueue — the identical position
the Throw's waveform occupies today (:336ff), so audio/haptic
issue from one call site by construction. `fireFeel`
(`ui/views/BlockitCoreViews.kt:204–232`) is the wrong road for
three concrete reasons: its ladder is canned (1L detent / 4L
latch / 8L firm click / else completion pulse) and cannot express
a five-segment waveform; its `else` bucket **is the completion
pulse**, a voice REGISTER reserves for verified fully-elapsed
sessions — routing a seal through it would spend the reserved
voice on a seal; and it lives in the view layer, while the seal
belongs to the t=0 state transition (the view's own seal call at
`BlockitCoreViews.kt:1184` is part of the double-play crime scene
§2 names, not a path to extend). Where the engine is absent
(previews only — the real arm path always has the engine), the
existing fallback at :1184 already degrades to `fireFeel(10L)`;
the Laid twin adds no new fallback and inherits that preview-only
degradation unchanged.

## (4) Code sites

| Site | Change |
|---|---|
| `service/TactileSoundEngine.kt:57–62` | `SynthCommand` +9 fields (defaults = Throw) |
| `service/TactileSoundEngine.kt:112–140` | per-command norm; attack ramp; decay scale; p4 scale; body one-pole LP; bolt composite render (noise, own LP, offset sum); buffer length = composite max |
| `service/TactileSoundEngine.kt:336–355` | `playHarmonicSeal(arming, voice = THROW)`; Laid waveform via `triggerWaveform` (:190); disarm ignores voice |
| `ui/MonolithViewModel.kt` (arm path only) | `primedRoutineId` derived at composition/resume; snapshot at DOWN; at latch, evaluate resolver → `attempt.sealVoice`; the single t=0 seal call passes it. All ~15 non-arm call sites untouched (default THROW) |
| new `SealVoiceResolver.kt` (pure) | predicate §3(b), SLEEP-window derivation over `RoutineSchedule` (`data/MonolithModels.kt:384ff`) |
| `data/MonolithDao.kt` | `sealExistsInWindow(routineId, windowStartMillis)` EXISTS query (columns from REGISTER 8→9; no new migration in this slice) |
| `data/MonolithRepository.kt` `startSession` (:869ff) | stamp `routineId` / `windowStartMillis` from the attempt snapshot on primed inserts (A5) |

## (5) Price, in lines

| Work | Lines (net) |
|---|---|
| Engine: command fields + render-loop changes + seal parameterisation + Laid haptic | **≈ 85** |
| `SealVoiceResolver` (pure predicate + window derivation glue) | **≈ 55** |
| ViewModel: primed snapshot, latch evaluation, t=0 routing | **≈ 30** |
| DAO EXISTS query + repository stamp | **≈ 15** |
| **Total, this joint** | **≈ 185 net (≈ 210 gross)** |
| Unit tests (resolver truth table incl. overnight SLEEP, UNKNOWN→Throw; composite buffer landmarks L3/L4 sample positions) | ≈ 60, separate |

Not in this price, by name: the room/wet path (F1), the routines
rebuild this slice depends on (Sten's slice), and the arm-path
single-call collapse (Opal/Orson's standing law — Ines priced the
whole seal-voice subsystem at ~130 inside W4; this joint's
increment above a Throw-only bolt build is ≈ 110 of the 185, the
bolt primitive ≈ 40 being shared with entry 1, and ≈ 35 being the
predicate the Throw never needed).

## (6) Risks / device-only unknowns

1. **One-pole LP may be too gentle to *place* the seal.** 6 dB/oct
   at 2.4 kHz takes the edge off the 4.7× partial (1692 Hz at f0
   360 — note: *below* the cutoff, attenuated mostly by the −8 dB
   scale, barely by the LP) and the bolt's top; whether the result
   reads as "nearer" or merely "duller" on Sarthak's actual
   speaker is a device A/B, unresolvable from this desk. If it
   reads dull, the honest next step is a two-pole (biquad) LP —
   +~10 lines — not a gain cut.
2. **The ±8 ms lock is aspirational in the current engine.** The
   synth thread wakes from `poll(500)` on enqueue (:116) and
   writes blocking into an `AudioTrack` whose buffer is at least
   160 ms of float PCM (:85–90); real audio-onset latency after
   the haptic fires is unmeasured on device and may already
   exceed ±8 ms for the Throw today. The Laid twin inherits the
   risk unchanged; only a device measurement (haptic timestamp vs
   mic capture) settles it, for both entries equally.
3. **Actuator variance.** A 22 ms segment at 0.80 and a 60 ms
   segment at 0.35 sit near many motors' start/stop inertia; on a
   weak actuator the shadow strike may not reach 0.80 and the
   settle may vanish — the twin then reads as one long buzz.
   Device-only; the waveform's *shape* (long-gap-long) is chosen
   to survive partial collapse better than the Throw's does.
4. **Overnight / edited-routine edges in the predicate.** SLEEP
   crossing midnight *and* a weekday boundary (Sunday→Monday
   daysMask change mid-window), a FASTED window edited while open
   (derivation reads the live row; the frozen bounds live only on
   stamped sessions), a second routine opening inside the FASTED
   window (candidate = earliest start strictly after SLEEP end —
   a 04:00 routine would take the Laid Seal from FASTED, by the
   rule as filed; Orson named this failure mode himself and the
   UNKNOWN→Throw default does not cover it, because nothing here
   is UNKNOWN — the facts are simply about a different morning
   than the one in his head).
5. **360 Hz on a phone speaker.** The fundamental is the weakest
   thing the driver reproduces; identity rides on 720 / 1080 /
   1692 Hz. The Laid LP + p4 cut removes proportionally more of
   what the speaker actually emits than of what the recipe
   contains — dawn audibility at arm's length (Orson's physical
   target) must be verified in a silent room, not assumed from
   −14 dB arithmetic.
6. **Dependency risk (restated from §2):** if Sten's rebuild has
   not landed — receiver still auto-starting routine sessions —
   term 3 is always false and this slice ships a voice that can
   never sound. Build order is a fact, not a preference.

## (7) Fork for Ren

**F1 — the wet send.** (i) **Field-only NO-OP in this slice**
(recommended): `wetSendDb` is stored on the command, unrendered,
and the Laid Seal ships dry-by-construction — which is already
90% of its filed intent ("dry = at the surface"); when Orson's
room lands, one render branch wires the send for both entries at
once. (ii) Price the room inside this slice: +~80 lines (2 combs
29.7 / 31.1 ms + allpass 5 ms + ~18 ms pre-delay + catch logic)
and Orson's joint grows into mine. (iii) Strike wet from the
Laid spec entirely and let the room, when it comes, take the
Throw's send for both. My recommendation is (i): the Laid Seal's
truth does not need the room, and building a reverb to satisfy
one row of one table is how slices become subsystems.

*Not a fork, settled by REGISTER and recorded here so it is not
re-litigated as one: the breath stays 120 ms (A1). And a reading
Ren should confirm only if he disagrees: the acceptance test is
landmark-indistinguishability (§3c); read as waveform-
indistinguishability it kills entry 2 by definition, and no
amendment of mine can save a variant whose purpose is to sound
placed differently from being heard as placed differently.*

## (8) Mail / wall note

Inbox at re-seating: no unread letters — the three prior letters
(Orson ×2: citation-lift, engine-room boundary treaty; Mara:
citation-lift) were already in `read/` and are honoured here: the
treaty stands (Orson owns room/engine layer and the Throw; this
file touches the room only to decline it, F1). One letter sent
this run: to Orson (`mail/orson/0846__from-ivo__laid-seal-slice.md`)
— the three corrections his §1 needs from the real code (breath
200 vs REGISTER 210; his reference-haptic table misreads the
38 ms shoulder as a gap; his `routineFireTimestamp` record does
not exist — the predicate is derivation-based, per Sten), plus
the A4 swell repositioning, which is his haptic design amended by
actuator physics, flagged to him rather than buried. Copy to
Marlowe as coordinator. Wall: appended, signed, this run.

— Ivo, Wild · sound
