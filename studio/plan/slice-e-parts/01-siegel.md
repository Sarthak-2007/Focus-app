# Siegel — Improvers (the seal) · SLICE E BUILD-READY · E1 + E2: THE BIT-IDENTICAL SEAL

*Joint: E1 (the Throw, Orson's part `plan/slice-de-parts/orson.md`) + E2
(struck by Ren's B-5 ruling — the Laid Seal is dead; this file is the
build-ready form of what survives: ONE seal voice, E1's, fired
bit-identically at every seal). All line numbers are against TODAY's
trunk (`~/workspace/clone-inspect/Focus-app`, read this run, no code
edited, no gradle run). B-5 governs placement, not volume: every E1
parameter below is Orson's, untouched.*

---

## 0. Verdict

**BUILDS, no fork in the voice, the cadence, or the call-site law.**
One new fork raised from trunk evidence Ren's merge could not see
(F-S1, §7): the routine arm path never passes through the VM at all
and today sounds **no seal whatsoever** — bit-identity "at every
seal" needs a ruling on whether it converges in this slice (+≈18)
or in the routines rebuild (recommended). Everything else in
Orson's filing stands as written; this file pins today's exact
lines, answers the haptic question, closes F13's boundary by
enumeration, makes bit-identity a testable property, and re-prices.

---

## 1. Q1 — The Throw's haptic: NOT the shipped waveform. A twin with one amplitude zeroed.

Trunk evidence: `playHarmonicSeal(arming = true)` at
`service/TactileSoundEngine.kt:336`, waveform at `:340–344`:

```kotlin
triggerWaveform(
    timingsMs   = longArrayOf(0, 24, 38, 16),
    amplitudes  = intArrayOf(0, 255, 110, 175),
    fallbackMs  = HAPTIC_SESSION_LOCK_8MS)
```

Orson's part spec (`orson.md` §3.5, amendment A4) does **not** carry
this waveform as its twin. `playSealThrow()` fires:

```kotlin
triggerWaveform(
    timingsMs   = longArrayOf(0, 24, 38, 16),   // identical skeleton
    amplitudes  = intArrayOf(0, 255, 0, 175),   // middle segment 110 → 0
    fallbackMs  = HAPTIC_SESSION_LOCK_8MS)      // retained
```

The timings are the shipped timings; the middle 38 ms segment drops
from amplitude 110 (≈0.43 — a settling shoulder, felt) to **0 — a
true gap**, which is the filed reference waveform (24 @ 1.0 / 38 gap
/ 16 @ 0.69; 175/255 = 0.686 ✓). So: same skeleton, one amplitude
changed, deliberately — the shipped seal *thuds through* its middle;
the Throw *breathes* through it. A builder who copies `:340–344`
verbatim has shipped the wrong haptic and no audio test will catch
it; the haptic-capture test in §5 exists for exactly this.

---

## 2. The primitive (E1, unchanged from Orson's filing)

```kotlin
// service/TactileSoundEngine.kt — new public entry, zero parameters
fun playSealThrow()
fun primeForSeal()   // focus pre-request at latch, orson.md §4.2
```

- One new command kind, `SealThrowCommand` — a sibling of
  `SynthCommand` (`TactileSoundEngine.kt:72–77`), **zero fields**
  (see §5: the absence of fields *is* the bit-identity mechanism).
  The queue at `:80` is `LinkedBlockingQueue<SynthCommand>(64)`
  today; it widens to a sealed command supertype — no other voice
  gains fields it doesn't use.
- One render pass, **44 100 samples = 1000 ms**, allocated per seal
  in the synth loop; the 160 ms `floatBuffer` cap (`:110`) and the
  136 ms `enqueueTransient` coercion (`:280` in Orson's tree; the
  coercion sits in `enqueueTransient`, today `:278–290`) stay for
  every legacy command. Sample 0 = the attack = t=0; **zero
  pre-silence** — the breath is never padded into the buffer.
- **Body (Layer A), shipped voice unmodified:** f0 = 360 Hz,
  partials 360 / 1080 / 1080 / 1692 Hz (chordRatio 1.5 — the 1080 Hz
  duplication IS the timbre; never "correct" to 2.0), weights
  {0.6, 0.28, 0.37, 2.2} (`:57`), decay pair exp(−0.0114) /
  exp(−0.0313) (`:60–61`), gain 0.62, no attack ramp, clamp
  ±0.95 (`:167`).
- **Bolt (Layer B), built new:** fixed-seed xorshift32 noise at
  **sample 1323 (+30 ms)**, one-pole LP 900 Hz (α = 0.1203), gain
  −6 dB fixed (peak 0.311), 55 ms envelope (2 ms linear onset,
  τ = 18 ms exp decay, final 5 ms linear fade). Bolt ends **+85 ms**
  — inside the cadence's audio-complete ≤ +90 landmark.
- **Room (Layer C), built new:** pre-delay 18 ms (794 samples);
  combs 29.7 ms (1310) / 31.1 ms (1372), summed 0.5 each; allpass
  5 ms (221), coefficient 0.5; loop LP 1.8 kHz (α = 0.2262);
  feedback **g = 0.836** both combs (−46 dB at ≈900 ms); wet send
  −12 dB (0.251); samples 39 690–44 100 (900–1000 ms) fade linearly
  to zero under Otis's bed catch (contract: orson.md §5 — level-
  triggered, 400 ms floor, 2.5 s ceiling).
- **Call behaviour:** haptic (§1) fires on the caller's thread;
  `commandQueue.clear()` (queue-depth-1 law — a still-pending
  approach transient at t=0 is dropped, never allowed to delay the
  Throw); one `SealThrowCommand` offered. `soundEnabled == false`
  → haptic still fires, audio skipped (S0 degradation unchanged).
- **Cadence landmarks (properties of the one voice, B-5):** intent
  −210 ms, breath −120 ms, attack onset t=0, audio complete ≤ +90 ms
  (bolt ends +85). Breath enforcement is upstream (controller cuts
  approach audio at the seat frame) + the queue-clear; the engine
  adds no silence primitive.

---

## 3. The ONE lawful call site, and the two deaths (today's exact lines)

### 3.1 Lawful site

```
soundEngine.playSealThrow()
```

called **once**, inside the function executing the PROVING →
SEALED-RUNNING transition (Slice D build-order step 4: the §F2
cascade lands that transition; E1's site hangs off it), **after the
session insert returns**, as the transition's last statement before
the shield starts. Against today's code shape — `armMonolithSession()`
is the transition's owner — the exact insertion is:

`ui/MonolithViewModel.kt:909–921` (`armMonolithSession`):
inside the `viewModelScope.launch` at `:912`, **after**
`repository.startSession(...)` returns (`:913–918` — it returns the
`FocusSessionEntity`, `data/MonolithRepository.kt:950–955`, so
"after the insert returns" is a real, checkable point) and **before**
`MonolithShieldService.startShield(appContext)` (`:919`).

If the cascade lands the transition as its own function/file, the
call lives there and `armMonolithSession()` carries none. Never in
a view, never in `requestArmWithPreFlightCheck()` (today
`:896–903`), never before the session record exists, never on a
gesture callback. Refusal paths call nothing — no t=0, no Throw.

`primeForSeal()` is called by the controller **at latch**
(t=0 − 210 ms), so the synth loop's per-command
`requestTransientAudioFocus()` (`TactileSoundEngine.kt:143`) does
not gate the seal's render; the seal branch skips it when primed
(`focusPrimed` flag), requests as today if priming was missed —
late, never silent.

### 3.2 Death 1 — the view site

`ui/views/BlockitCoreViews.kt:1184` (SlideToStartView, ACTION_UP,
`dragFraction >= 0.82f`):

```kotlin
if (eng != null) eng.playHarmonicSeal(true) else fireFeel(10L)
```

**Delete the whole line** — the seal call *and* its `else` fallback,
which spends `playCompletionPulse()` (the reserved completion
voice) on an arm. The comment block at `:1180–1183` is rewritten to
say the view sounds nothing for the seal (2 lines). The view keeps
`dragFraction = 1f`, `invalidate()`, `onSlideCommitted?.invoke()`,
`animateSpringBack(0f)` unchanged; `onSlideCommitted` becomes pure
intent.

### 3.3 Death 2 — the VM pre-launch site

`ui/MonolithViewModel.kt:911`, head of `armMonolithSession()`:

```kotlin
soundEngine.playHarmonicSeal(arming = true)   // DELETE
```

(the brief's "~886–913" range resolves to exactly **:911** today;
:886 is `createCustomProfile`'s seal, a survivor — §4.) Its
replacement is §3.1's relocated call: same function, moved from
before the launch to after the insert returns. Net effect: today
every arm sounds the seal **twice, both times before the session
exists**; after E1 it sounds **exactly once, after proof**.

---

## 4. Q2 — F13's boundary: every `playHarmonicSeal` site in today's trunk, classified

Grep this run: **17 call sites** (16 VM + 1 view) + the definition
at `TactileSoundEngine.kt:336`, which is **not edited, not renamed**
by this slice.

### 4.1 DIES in E1 — arm path only (2)

| Site | Owner today |
|---|---|
| `ui/views/BlockitCoreViews.kt:1184` | SlideToStartView ACTION_UP (§3.2) |
| `ui/MonolithViewModel.kt:911` | `armMonolithSession()` pre-launch (§3.3) |

### 4.2 E4's mis-spend — named, left untouched (1)

| Site | What it is |
|---|---|
| `ui/MonolithViewModel.kt:344` | `startTickerLoop()` (`:330`): session completion discovered on the 1 s ticker poll (`repository.updateSessionProgress`, `:343`) and answered with `playHarmonicSeal(arming = false)` — the *seal* voice spent on a *completion*, up to 1000 ms late. This is **E4's joint (Nocke's, per the build-ready seating; E4 = Release Proof, SLICE-D-E §2)** — E4 replaces the ticker discovery with the StateFlow-edge E0 and re-voices completion to `playCompletionPulse` (`TactileSoundEngine.kt:360`, itself mis-spent today by the ratchet warning at VM `:159`). **E1 does not touch :344, :159, or the ticker.** |

### 4.3 SURVIVES untouched — the reserved-voice sweep is a SEPARATE build (14)

`arming = true` — furniture wearing the seal's voice; the F13 debt,
swept by the separate reserved-voice build with Ivo holding
replacement voices (SLICE-D-E §6-F13 recommendation, adopted).
**None of these is edited, re-voiced, or deleted by E1/E2:**

| Site | Function |
|---|---|
| VM `:655` | `undoDeleteTask()` |
| VM `:689` | `splitActiveTaskAndReturn()` |
| VM `:722` | `autoSplitTaskIntoMicroSteps()` |
| VM `:740` | `cueTaskAndNavigateToFocus()` |
| VM `:819` | `applySmartLoadout()` |
| VM `:845` | `addCustomApp()` |
| VM `:852` | `selectAndApplyProfile()` |
| VM `:886` | `createCustomProfile()` |
| VM `:1056` | `end2MinutePeekEarly()` |
| VM `:1065` | `transmuteImpulseAndReturn()` |
| VM `:1096` | `tapSerialPlateEasterEgg()` |

`arming = false` — the disarm sibling voice itself; not the sweep's
subject, not this slice's:

| Site | Function |
|---|---|
| VM `:931` | `requestDisarmChallenge()` |
| VM `:955` | `confirmDisarmSession()` |
| VM `:979` | `completeEmergencyBreakFromValve()` |

**Drift note (trunk has moved since Orson's §8 census):** two of
Orson's thirteen are already gone — `completeTaskAndAdvance`
(VM `:587` today) carries an in-code comment (`:588–591`) recording
a prior merge ruling that removed its seal as a mis-spend, and
`toggleAppBlocked` (VM `:814`) no longer calls the seal
(`applySmartLoadout` at `:818–819` does). Orson's count "~13 other
places" is **11 `arming=true` survivors today**. The sweep's owner
should re-grep, not trust either census.

---

## 5. Q3 — Bit-identity as a testable property

Bit-identity is guaranteed **structurally first, tested second**:
`playSealThrow()` takes **zero parameters** and `SealThrowCommand`
has **zero fields**. No provenance (manual arm / routine arm / FLOW
arm), no duration, no velocity, no clock, no `Random` can enter the
render — there is no input for a caller to vary. The renderer reads
only compile-time constants and the fixed xorshift seed. Two seals
cannot differ because there is nothing in the system that *could*
make them differ. The tests then pin that structure against future
"helpful" edits:

**T1 — Buffer-hash identity (the headline assertion).**
The render branch is extracted as a pure internal function
`renderSealThrowBuffer(): FloatArray` (no Android types, JVM-
testable). In `app/src/test/java/com/example/SealThrowIdentityTest.kt`
(new file, unit source set — alongside the existing JVM tests,
e.g. `EmergencyBreakLedgerTest.kt`):

```kotlin
val a = renderSealThrowBuffer()   // "manual arm"
val b = renderSealThrowBuffer()   // "routine arm"
val c = renderSealThrowBuffer()   // "FLOW arm"
assertEquals(44_100, a.size)
val ha = sha256OfRawFloatBits(a)
assertEquals(ha, sha256OfRawFloatBits(b))
assertEquals(ha, sha256OfRawFloatBits(c))
assertEquals(GOLDEN_SHA256, ha)   // constant pinned at first green run
```

The three renders *are* the three arms: since the entry point has
no parameters, calling the renderer three times is the strongest
provenance simulation that exists. The golden hash makes any
parameter drift (a weight, the seed, g, the bolt offset) a build
failure, not a review opinion.

**T2 — Parameter capture at the seam.** A Robolectric test (style
of `ExampleRobolectricTest.kt`) drives `playSealThrow()` with a
capturing queue/vibrator shadow and asserts: (a) exactly **one**
command is offered and it is the zero-field `SealThrowCommand`;
(b) the captured haptic waveform is exactly
`[0, 24, 38, 16] @ [0, 255, 0, 175]` — this is the only test that
can catch a builder pasting the shipped `[0, 255, 110, 175]`;
(c) a pre-loaded queue is empty after the call (queue-clear law).

**T3 — Singularity receipt (acceptance, not a unit test).**
Post-build grep, filed with the build's proof:
`grep -rn "playSealThrow" app/src/main` returns exactly **2 hits** —
the definition and §3.1's one call site — and
`grep -rn "playHarmonicSeal" app/src/main` returns §4's survivors
with the two §4.1 sites absent. Any third call site is a failed
build, whatever it sounds like.

**T4 — Orson's render assertions, kept:** bolt energy onset at
sample 1323 ± 44; tail level at 900 ms within −46 ± 3 dB; no sample
above clamp; buffer length exactly 44 100.

**What the tests do NOT claim:** on-device acoustic identity
(speaker, AudioTrack buffering, room) — that's Orson's §7 device
risks, unchanged. The hash proves the *buffer*; bit-identical
buffers through one code path is the whole of the law B-5 states.

---

## 6. Q4 — Re-price against today's engine (was ≈210 net)

Counting method (same as Orson's, stated for audit): **net =
gross additions − deletions, per file, production and test lines
counted in one total** — the convention under which E1 was filed
at ≈210 (his: +150 engine, −2 view, +2 VM, +60 tests ≈ +210).

Today's engine is line-for-line the engine Orson priced (378
lines; every fact in his §2 re-verified this run at the lines
cited in §2/§4 above), so his mechanism prices stand — with **one
B-5 delta**: his render branch was shaped to also serve Entry 2 as
a parameter set (his §9 interface: attack-ramp field, body-LP
field, per-voice gains). Under B-5 there is no second parameter
set, ever; the renderer hard-codes the one voice. That abstraction
is deleted before it is built: **−12 engine lines**. The saving is
spent on §5's identity tests (golden-hash + haptic capture are new
assertions Orson's five did not include): **+12 test lines**.

| File | Work | Net |
|---|---|---|
| `service/TactileSoundEngine.kt` (378) | `SealThrowCommand` (zero-field) + queue supertype widening +14; `playSealThrow()` (haptic true-gap, queue-clear, offer, S0 path) +22; `primeForSeal()` + `focusPrimed` skip +12; seal render branch (body reuse, seeded bolt @1323, room combs g=0.836, wet −12 dB, 1000 ms buffer, 900→1000 ms fade) +90 (was +98; −8 of the B-5 delta) | **+138** |
| `ui/views/BlockitCoreViews.kt` | Delete `:1184` line (−1) + fallback; rewrite `:1180–1183` comment (+2/−3) | **−2** |
| `ui/MonolithViewModel.kt` | Delete `:911` (−1); add `playSealThrow()` at §3.1 (+1); add `primeForSeal()` at latch (+1); *(if the cascade owns the transition in its own file, these +2 live there and the VM nets −1; total unchanged)* | **+2** |
| `app/src/test/…/SealThrowIdentityTest.kt` (new) | T1 hash identity + golden pin, T4 render assertions (+52); T2 Robolectric capture in `ExampleRobolectricTest.kt` (+20) | **+72** |
| **TOTAL** | 3 production files + 2 test files | **≈ +210 net** |

**Re-price verdict: ≈210 net confirmed** — same total as filed,
different composition (−12 engine generality killed by B-5, +12
identity tests B-5 demands). Production-only: **≈138 net**. No DB
change, no migration, no new production file, no settings surface.
F-S1 convergence, if ruled into this slice: **+≈18 → ≈228 total**
(§7).

---

## 7. Forks

**One new fork — F-S1 · ROUTINE-ARM CONVERGENCE (trunk-found).**
There are exactly two session-birth sites in today's trunk:
VM `:913` and `service/RoutineAlarmReceiver.kt:45`
(`repository.startSession(...)` inside `BOUNDARY_START`, followed
by `startShield` at `:54`). The receiver constructs a repository
directly and **never touches a `TactileSoundEngine`** — a routine
arm today sounds no seal at all: not the wrong seal, *silence*.
B-5's law ("fired bit-identically at every seal, whatever window
or hand armed it") therefore has a hole no E1 call site in the VM
can close. Options:
- **(a) Converge in this slice (+≈18 net):** the receiver routes
  its START branch through the same sealed transition — construct
  the engine, call `playSealThrow()` after its `startSession`
  returns (`RoutineAlarmReceiver.kt:45–53`), before `startShield`.
  Cost beyond lines: a per-fire engine instance (its synth thread
  is daemon; `stopAllAudio()` cannot be called immediately without
  killing the queued Throw — the instance must be held by a
  process-level holder, which is the real design work in the +18).
- **(b) Converge in the routines rebuild (RECOMMENDED):** the
  routines rebuild (Migration 8→9) already re-owns this receiver's
  window machinery; B-5 itself struck E2's dependency on it — the
  seal's *cadence* must not wait for it, but the routine *path* is
  that build's file. Until it lands, routine arms stay silent —
  named here as a known gap, not discovered later as a bug.
- **Not an option:** a second, different seal for routines. That is
  the Laid Seal wearing a trench coat; B-5 killed it.

**No other forks.** F13 is ruled (separate sweep, §4.3) — not
re-opened. F14 died with the Laid Seal — not re-opened. The voice,
gains, cadence, and call-site law carry no fork, per Orson §1,
confirmed against today's trunk.

---

## 8. Risks (Orson's §7, carried; plus two trunk-found)

1. **±8 ms audio-vs-haptic lock is not constructible from code**
   (haptic on caller's thread; audio via synth thread +
   `WRITE_BLOCKING` + device buffer). Device test and honest fixes
   exactly as Orson §7.1 — haptic delay compensation if it fails,
   never moving the audio off t=0.
2. **Synth-thread readiness at t=0** — breath + queue-clear make it
   a corner, not a path; device-measurable only (Orson §7.2).
3. **Comb colouration on the real speaker** — g = 0.836 is solved
   arithmetic, not a heard result; Orson §5's failure gate binds
   (ring/buzz/pitch → g → 0.78 once, retest → fader ships, priced
   by the bed slice).
4. **Bolt audibility vs startle on device** — tune band only:
   bolt −6 dB ± 2, wet send −12 dB ± 3; outside = spec amendment.
5. **Fixed-seed recognisability** — fallback pre-priced at zero
   extra lines: per-install constant seed (still identical every
   arm on his phone).
6. **`primeForSeal()` side effect** — transient focus duck may
   begin at latch, ≤210 ms before t=0 (Orson §7.6). Accepted,
   flagged.
7. **NEW — Transition ownership drift.** §3.1 pins the call to
   `armMonolithSession()` *as today's owner*. Slice D's cascade
   (build-order step 4) lands before E1 and may move the
   PROVING→SEALED-RUNNING transition; the builder must place the
   call in **whatever function executes that transition at build
   time** and re-pin the line in the build's proof. If both the
   cascade's function *and* the VM carry a call, T3 fails the build
   — that is what T3 is for.
8. **NEW — Routine silence misread as E1 failure.** Until F-S1 is
   resolved, a tester arming by routine hears nothing and may file
   it against E1. The build's proof note must state the gap in one
   line (this section, §7) so the silence is expected, not chased.

---

## 9. Acceptance tests (the build is done when all hold)

1. T1–T4 (§5) green in `testDebugUnitTest`; golden SHA-256 pinned.
2. T3 grep receipt filed: `playSealThrow` = 2 hits in `app/src/main`;
   §4.1's two sites absent from `playHarmonicSeal` results; §4.3's
   14 survivors present, byte-identical.
3. Manual arm: seal sounds **once**, after the session row exists
   (log/print of insert-return preceding the call is the proof
   shape the cascade build already produces); release of the slide
   sounds **nothing**.
4. Refused arm: no Throw; refusal voices untouched.
5. `soundEnabled == false`: haptic fires, no audio, no crash.
6. Device gate (Orson §5) run once on Sarthak's phone before the
   slice is called shipped: no ring/buzz/pitch in the tail; the
   ±8 ms measurement (risk 1) reported as a number, either way.

— Siegel, Improvers · the seal (seated by Ilya)
