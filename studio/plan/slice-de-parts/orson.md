# Orson — Wild, synths · SLICE-DE · SEAL CADENCE, ENTRY 1: THE THROW

*Joint: the Throw only — its synth recipe in the shipped engine's terms, its
single lawful call site at t=0, the disposition of the two existing seal call
sites on the arm path, and the Room (the Throw's tail). Entry 2 (the Laid
Seal) is Ivo's; §9 gives him the renderer interface so both entries are one
primitive with two parameter sets, never two voices. Law obeyed throughout:
Live Wire form (synchronous verdicts ≤5 ms at DOWN and at 0.68; Park /
1500 ms / UNKNOWN / Canary / standing poll dead), REGISTER.md constants,
t=0 ≡ proof-seal frame = max(last link CONFIRMED-SHUT, latch + 210 ms,
pawl-seated frame), terminal cadence 90 + 120 = 210 ms, breath = the final
120 ms ending at t=0, bolt at +30 ms, acceptance = eyes closed, entries
indistinguishable.*

---

## 1. Verdict

**BUILDS AS AMENDED.** The merged prose (BEYOND-deck W4, "the seal's
voice") cannot be built as written against the shipped engine, because it
assumes four primitives the engine does not have: a noise source, a
lowpass, a feedback-comb room, and sample-offset scheduling inside one
event — and it assumes a render length the engine caps at 160 ms. The
voice itself — identity, gains, offsets, cadence — survives untouched.
Every amendment below is a mechanism, with exact numbers; none is a
change to what the Throw sounds like on paper. Amendments:

- **A1 — One new primitive, `SealThrowCommand`, rendered in ONE pass.**
  Body + bolt (+30 ms) + comb tail are synthesised into a single
  1000 ms buffer by one new render branch. Two queued commands can
  never produce the +30 ms bolt: the synth loop renders its queue
  strictly serially (§2), so a second command lands at +48 ms (the
  first command's buffer length) at best, never +30.
- **A2 — The bolt is built, not called.** No noise or lowpass exists in
  the engine. The renderer gains a fixed-seed noise source and a
  one-pole 900 Hz lowpass (α = 0.1203), bolt only.
- **A3 — The Room is built, not assumed.** No comb, allpass, wet send,
  or bed exists in the engine today. The renderer gains the filed room
  (pre-delay ≈18 ms, combs 29.7 / 31.1 ms, allpass 5 ms, loop lowpass
  1.8 kHz, α = 0.2262) with comb feedback **g = 0.836**, solved below
  for −46 dB at ≈900 ms.
- **A4 — The haptic "gap" is made real.** The filed reference waveform
  is 24 ms @ 1.0 / 38 ms gap / 16 ms @ 0.69. The shipped waveform's
  middle segment is 38 ms @ amplitude 110 (≈0.43), not a gap
  (`TactileSoundEngine.kt:340–344`). `playSealThrow()` carries its own
  waveform with the middle amplitude **0**.
- **A5 — Audio focus is pre-requested at latch, not on the seal's
  frame.** The synth loop calls `requestTransientAudioFocus()` before
  rendering every command (`TactileSoundEngine.kt:143`); on the seal
  that cost lands on t=0. New `primeForSeal()` (§4) moves it inside
  the cadence, where time is free.

No fork in the voice, the cadence, the gains, or the call-site law.
One fork for Ren, §7 (a reserved-voice debt found in the code, outside
this joint).

---

## 2. Against the real code (verified this run)

Engine: `Focus-app/app/src/main/java/com/example/service/TactileSoundEngine.kt`
(378 lines today; Ines's §0 citations are to the same file).

| # | Fact | Evidence |
|---|---|---|
| 1 | `SynthCommand` carries exactly four fields: `fundamentalHz`, `durationSamples`, `gain`, `chordRatio` (`:72–77`). No kind, no offset, no filter, no per-layer data. A multi-layer event cannot be expressed. | `:72–77` |
| 2 | The render loop is **serial**: one command is polled, rendered, and `WRITE_BLOCKING`-written before the next is polled. No mixing, no overlap, no scheduling. | `:137–176` |
| 3 | Render length is capped twice: `floatBuffer` is sized `maxSamples = SAMPLE_RATE × 0.16` = 7056 samples / **160 ms** (`:110`), and `enqueueTransient` coerces every command to ≤ 6000 samples / **136 ms** (`:280`). A 900 ms tail cannot be enqueued, let alone rendered. | `:110`, `:280` |
| 4 | Synthesis is sines only: 4 partials, weights `{0.6, 0.28, 0.37, 2.2}` (`:57`), body = partials 1–3 under `envBody`, bright partial 4 under `envTail` (`:158–168`). **No noise source, no filter of any kind, no delay line, no feedback — no Room.** The file header says so itself: "Zero ambient sound mixer loops" (`:34`). | `:57`, `:158–168` |
| 5 | The filed decay pair kills the shipped seal almost immediately: `BODY_DECAY_STEP = exp(−0.0114)`/sample reaches −46 dB in 465 samples ≈ **10.5 ms**; `TAIL_DECAY_STEP = exp(−0.0313)` reaches −46 dB in ≈ **3.8 ms**. The "48 ms body" of `playHarmonicSeal` is buffer length, not audible life: today's seal is a ≈15 ms event. (Computed from `:60–61` at 44 100 Hz.) | `:59–61` |
| 6 | `playHarmonicSeal(arming = true)`: f0 = **360 Hz**, duration **48 ms**, gain **0.62** (= 0 dB ref), `chordRatio = 1.5` (`:336–358`). Note what 1.5 does: partial 2 renders at 2 × 1.5 = 3.0 × f0, exactly duplicating partial 3 at 3 × f0 — the 1080 Hz component carries combined weight 0.28 + 0.37 = 0.65. That duplication IS the shipped timbre. The recipe in §3 reproduces it; a builder must not "correct" chordRatio to 2.0. | `:352–357` |
| 7 | `requestTransientAudioFocus()` runs inside the synth loop before every render (`:143`). Unmeasured latency, charged to whichever command is rendering — today, potentially to the seal. | `:143`, `:182–200` |
| 8 | Seal call site 1 — the view: `SlideToStartView` fires `eng.playHarmonicSeal(true)` on **ACTION_UP release** at `dragFraction >= 0.82f`, one frame after snapping the thumb to 1f, **before** `onSlideCommitted?.invoke()` (Ines cited `:807` in the pre-slice tree; the file has since grown). The fallback on the same line, `fireFeel(10L)`, spends `playCompletionPulse()` — the reserved completion voice — on arming, per the in-code comment at `:1180–1183`. | `ui/views/BlockitCoreViews.kt:1176–1186` |
| 9 | Seal call site 2 — the ViewModel: `armMonolithSession()` calls `soundEngine.playHarmonicSeal(arming = true)` at `:894`, **before** `viewModelScope.launch { repository.startSession(...) }` at `:895–904` (Ines's `:892–907`). The sound precedes the session's existence, exactly as Ines §0 charges. Between the two sites, every arm today sounds the seal **twice, both times before proof of anything**. | `ui/MonolithViewModel.kt:892–905` |
| 10 | The queue is a `LinkedBlockingQueue(64)` fed by `offer()` (`:80`, `:282`) — pending approach transients (latch knock, detents, Assay strikes) can sit ahead of a seal command. Queue-depth-1 for the seal must be enforced by the seal itself (§4). | `:80`, `:278–290` |
| 11 | **The bed does not exist.** No bed loop, no bed gain, no crossfade anywhere in the engine (fact 4). The "catch" at −46 dB is therefore an interface owed by the session-bed layer (Otis's heartbeat engine slice), specified as a contract in §5 — not a thing this slice can render. | whole file |

---

## 3. Amended spec — the Throw, exact (one render pass)

New public entry point in `TactileSoundEngine`:

```kotlin
fun playSealThrow()   // Entry 1. Entry 2 calls the same renderer with Laid parameters (§9).
```

It does three things, in order, on the caller's thread:
(1) fires the haptic waveform below; (2) `commandQueue.clear()` — drops
any still-pending approach transient (queue-depth-1 law; anything still
queued at t=0 is late approach audio and a late sound is a wrong sound);
(3) offers one `SealThrowCommand`. If `soundEnabled == false`, (1) still
fires and (2)–(3) are skipped — S0 degradation unchanged: muting loses
timbre, never the event.

### 3.1 The command and the buffer

- `SealThrowCommand` is a sibling of `SynthCommand`, not a widening of
  it: no other voice gains fields it doesn't use.
- Render length: **44 100 samples = 1000 ms**, allocated per seal
  command in the synth loop (the 160 ms `floatBuffer` cap at `:110`
  stays for every legacy command; the seal branch allocates its own
  buffer — 176 KB, once per arm, no retained state).
- **Sample 0 of the buffer is the attack, and the attack is t=0.**
  The buffer contains **zero samples of pre-silence**. The breath is
  not in the buffer and must never be padded into it: a builder who
  prepends 120 ms of silence has moved the seal to t=0 + 120 and
  falsified the frame. The breath is enforced upstream (§4).
- Output clamp `coerceIn(−0.95f, 0.95f)` retained from `:167`.

### 3.2 Layer A — the body (the shipped voice, unmodified)

Rendered exactly as `runFloatPcmSynthLoop` renders
`playHarmonicSeal(true)` today (`:155–172`), same code path reused:

- f0 = **360 Hz**; partials at 1×, (2 × chordRatio)×, 3×, 4.7× with
  chordRatio = **1.5** → rendered partials **360 / 1080 / 1080 / 1692 Hz**,
  weights **{0.6, 0.28, 0.37, 2.2}**, normalised by weight sum 3.45.
- Body partials (1–3) under `envBody`, step `exp(−0.0114)`; bright
  partial (4) under `envTail`, step `exp(−0.0313)` — the filed decay
  pair, unchanged.
- Gain **0.62 = 0 dB ref**. Nothing in this event exceeds it: the bolt
  peaks after the body has decayed by exp(−0.0114 × 1323) ≈ 3 × 10⁻⁷
  (dead), and the wet path is delayed 18 ms (also dead) — the layers
  never sum against the ceiling. No limiter change.
- No attack ramp. The sine stack starts at phase 0; onset is the
  shipped onset. The Throw keeps its snap — attack softening belongs
  to Entry 2 alone, and adding it here would break the eyes-closed
  test in the other direction (a softened Throw is a third voice).

### 3.3 Layer B — the bolt (Wren's layer, +30 ms, built new)

- **Offset: sample 1323** (= 30.0 ms at 44 100 Hz) from buffer start.
  Sample-accurate by construction — it is an index inside one render,
  not a second enqueue.
- **Source: deterministic noise.** A xorshift32 generator with a fixed
  seed constant in the renderer (same seed every seal — the bolt's
  grain is part of the voice's identity; a fresh random seed per arm
  would make every seal microscopically a different object, and the
  cadence's law is verbatim repetition). Seed value is the builder's
  choice, frozen once chosen, recorded in a code comment.
- **Lowpass: one-pole, fc = 900 Hz, α = 1 − exp(−2π·900/44100) =
  0.1203**, state carried across the bolt's samples only.
- **Gain: −6 dB fixed at every duration** (Moss V-14; the duration
  slope is dead) → peak gain **0.62 × 10^(−6/20) = 0.311**.
- **Envelope: 55 ms long** (2426 samples): 2 ms linear onset ramp
  (samples 0–88 of the bolt), then exp decay with τ = 18 ms, final
  5 ms faded linearly to zero (truncation is at ≈ −24 dB of bolt peak;
  an unfaded cut clicks, a click is a second transient the spec
  didn't order).
- The bolt's output also feeds the Room (§3.4) at the same send as the
  body — the mass's decay is part of the arriving air (filed, §3 of
  beyond-deck-parts/orson.md).

### 3.4 Layer C — the Room (the Throw's tail; §5 has the full ruling)

- **Pre-delay 18 ms** (794 samples) on the wet path.
- **Two feedback combs, 29.7 ms (1310 samples) and 31.1 ms
  (1372 samples)**, summed at 0.5 each; **one allpass, 5 ms
  (221 samples), coefficient 0.5**, after the comb sum. Filed geometry,
  from my seat file — not re-derived here.
- **Loop lowpass:** one-pole 1.8 kHz (α = 0.2262) inside each comb's
  feedback path — this is what keeps a sustained tail from reading as
  a pitch (the failure gate, §5).
- **Feedback gain g = 0.836, both combs.** Derivation, shown so it can
  be re-solved if the geometry ever changes: target −46 dB in 900 ms;
  mean loop time 30.4 ms → 29.6 loops in 900 ms → 46 / 29.6 =
  1.554 dB loss per loop → g = 10^(−1.554/20) = **0.836**.
- **Wet send: −12 dB (gain 0.251)** applied to the dry body+bolt sum
  feeding the delay line. The tail's first arrival (≈18 ms) is
  therefore ≥12 dB under an attack that is itself already decaying —
  room, never a second attack. Device-tune band ±3 dB (§6); outside
  that band it is a spec change, not a tuning.
- **End of buffer:** the tail crosses −46 dB at ≈900 ms by
  construction; samples **39 690–44 100 (900–1000 ms) fade linearly to
  zero** underneath the bed catch (§5), so the render never truncates
  audibly whether the bed is there or not. If no bed exists yet on a
  given build, the Throw simply ends at −46 dB and fades — no click,
  no swell, no lie (the tail claims nothing the bed would contradict).

### 3.5 Haptic twin (same call, A4 applied)

`triggerWaveform(timingsMs = [0, 24, 38, 16], amplitudes = [0, 255, 0,
175], fallbackMs = HAPTIC_SESSION_LOCK_8MS)` — the filed reference
waveform (24 @ 1.0 / 38 gap / 16 @ 0.69), with the middle segment at
amplitude **0**, a true gap, where the shipped
`playHarmonicSeal` puts 110. Haptic and audio issue from this one
function, as the engine's standing ±8 ms lock requires (§6 for what
that lock can and cannot promise).

### 3.6 Determinism — the acceptance test, made structural

The eyes-closed test (drag-sealed vs tap-sealed indistinguishable) is
guaranteed by construction, not by discipline: one call site (§4), one
command, one fixed seed, and **no input** — the renderer reads no
duration, no velocity, no entry kind, no clock. Two renders of
`SealThrowCommand` are sample-identical. A unit test asserts exactly
that (§5 pricing), so a future "helpful" parameter breaks the build,
not the law.

---

## 4. Code sites — the single lawful call, and the two deaths

### 4.1 The lawful call site (one, only)

```
soundEngine.playSealThrow()
```

called **once, inside the function that executes the PROVING →
SEALED-RUNNING transition** — the Live Wire controller's transition
(Ines §3 / Otis heartbeat slice) — in the same frame-execution that
writes the session record and captures the single monotonic start
sample, after those writes succeed, as the transition's last
statement. That frame is t=0 by definition (REGISTER.md); the sound
attaches to it and to no other.

Concretely, against today's code shape: if `armMonolithSession()`
remains the transition's owner in the shipped build, the call sits
**inside its coroutine, after `repository.startSession(...)` returns**
(and before `MonolithShieldService.startShield(appContext)` —
the receipt before the machinery's noise, both in the same frame's
work). If the controller lands as its own function/file, the call
lives there and `armMonolithSession()` carries none. Either way:

- **Never** in a view. **Never** in `requestArmWithPreFlightCheck()`.
  **Never** before the session record exists. **Never** on a gesture
  callback (DOWN, UP, release, threshold, spring end).
- Refusal paths call nothing: a wire-dead arm produces no t=0, hence
  no Throw — the filed refusal voices (dull knock, latch-home) are
  untouched and remain the only sounds a refused arm makes.

### 4.2 Priming (inside the cadence, costs the seal nothing)

New engine function `primeForSeal()` (≈8 lines): performs
`requestTransientAudioFocus()` early and nothing else — no sound, no
queue change. The controller calls it **at latch** (t=0 − 210 ms, the
cadence's first frame), so the seal command's render is not gated on
a focus transaction (fact 7). The synth loop's per-command focus
request stays for every legacy voice; the seal branch skips it when
primed (a `focusPrimed` flag on the command, set by
`playSealThrow()` — if priming was somehow missed, the loop requests
as today and the seal is late by that cost, never silent: fail toward
the filed behaviour).

Breath enforcement, stated once so no builder invents a second
mechanism: the breath (t=0 − 120 → t=0) is the controller cutting
approach audio at the seat frame, as merged law already orders, plus
`playSealThrow()`'s queue-clear at t=0 (§3). The engine adds no
silence primitive, because none is needed and a padded buffer is a
falsified frame (§3.1).

### 4.3 Death 1 — the view call

`ui/views/BlockitCoreViews.kt:1184` (SlideToStartView, ACTION_UP):
**delete the whole seal line** — both the `eng.playHarmonicSeal(true)`
and its `else fireFeel(10L)` fallback (which spends the completion
pulse on an arm; §2 fact 8). The view keeps `dragFraction = 1f`,
`invalidate()`, `onSlideCommitted?.invoke()`, and the spring-back,
unchanged. `onSlideCommitted` becomes pure intent under Live Wire;
the view sounds nothing for the seal, ever. The comment block at
`:1180–1183` is rewritten to say so (2 lines).

### 4.4 Death 2 — the ViewModel pre-launch call

`ui/MonolithViewModel.kt:894`: **delete** `soundEngine.
playHarmonicSeal(arming = true)` from the head of
`armMonolithSession()`. Its replacement is §4.1's relocated call —
same function under today's shape, moved from before the coroutine
launch to after the insert returns inside it. Net effect on the arm
path: `playHarmonicSeal(true)` has **zero** remaining call sites on
it, and the seal sounds **exactly once**, after the session exists.

### 4.5 Explicitly untouched

- `playHarmonicSeal(arming = false)` — the disarm sibling
  (VM `:330`, `:914`, `:941`) and its renderer. Not this joint.
- `playHarmonicSeal` itself: not edited, not renamed. Legacy non-arm
  callers keep compiling; §7 prices why that's a debt, not a virtue.
- Every approach voice (detents, latch knock, Assay strikes): the
  renderer does not touch them; the queue-clear reaches only commands
  still pending at t=0, which the breath already forbids.

---

## 5. The Room — ruling

**The 2.5 s bed-ramp is KILLED, as merged law orders.** No fader, no
bed-arrival event in this slice. The comb sustain of §3.4 is the
mechanism: the Throw's own decay falls to bed level (−46 dB) in
≈900 ms and is caught there.

**The catch is a contract on the session-bed layer** (Otis's
heartbeat-engine slice owns the bed; it does not exist in this engine,
§2 fact 11), stated exactly so that slice can build to it:

- Bed level at catch: **−46 dB**, never above the tail it replaces at
  any sample — room energy = max(tail, bed), never the sum; no
  arrival swell, monotonic decay into held level.
- Catch window: level-triggered at the tail's −46 dB crossing,
  **floor 400 ms, ceiling 2.5 s** after t=0 (the filed rule, both
  voices; the Throw's nominal crossing is ≈900 ms).
- Until that slice lands, §3.4's 900–1000 ms fade is the whole story:
  the tail ends clean at bed level into silence. Shipping the Throw
  before the bed exists is lawful; shipping a fader to fake the catch
  is not.

**Device test (the failure gate, Pure-Seal precedent — filed, kept
verbatim in force):** on Sarthak's phone, in a quiet room, record the
Throw at arm's length (second device or the phone's own mic in a
recorder app, seal played from a test trigger or a real arm). Fail
conditions, any one: the sustained tail **rings, buzzes, or reads as
a pitch** at any point in its decay; a narrowband component sits
above −50 dB after 300 ms in the recording's spectrogram; or the tail
is still plainly audible as an *event* (rather than room) past
1200 ms. On failure, in order: (1) g drops **0.836 → 0.78** (decay to
−46 dB in ≈640 ms) and the test re-runs once; (2) if it still fails,
**the fader ships** — comb feedback returns to a short filed tail and
the bed arrives by the old 2.5 s ramp, owned and priced by the bed
slice, not this one. Never ship a ringing room.

---

## 6. Price (lines, against the files as they stand today)

| File | Work | Gross | Net |
|---|---|---|---|
| `service/TactileSoundEngine.kt` (378) | `SealThrowCommand` type + queue handling for the new kind | +18 | |
| | `playSealThrow()` — haptic (true gap), queue-clear, offer; S0 path | +22 | |
| | `primeForSeal()` + `focusPrimed` skip in the loop | +12 | |
| | Seal render branch: body reuse, seeded noise + 900 Hz one-pole bolt at sample 1323, 18 ms pre-delay + 2 combs (g = 0.836, loop LP 1.8 kHz) + allpass, wet send −12 dB, 1000 ms buffer, 900→1000 ms fade | +98 | |
| | **Engine subtotal** | **+150** | **+150** |
| `ui/views/BlockitCoreViews.kt` | Delete view seal call + fallback (§4.3); rewrite its comment | +2 / −4 | **−2** |
| `ui/MonolithViewModel.kt` | Delete `:894` pre-launch call; add `playSealThrow()` at the transition (§4.1 shape) + `primeForSeal()` at latch — *if the controller lands in its own file, these +3 live there and the VM nets −1* | +3 / −1 | **+2** |
| Test source set | Pure-render extraction is already the render branch's shape: assert (a) two renders sample-identical, (b) bolt energy onset at sample 1323 ± 44, (c) tail level at 900 ms within −46 ± 3 dB, (d) no sample above 0.62 + wet headroom / clamp, (e) buffer length exactly 44 100 | +60 | **+60** |
| **Total** | **3 code files + 1 test file** | **≈ +215** | **≈ +210** |

Context for the merge: Ines §1 priced the whole seal-voice subsystem
(Throw **and** Laid) at ~130 lines, low end "assuming the existing
sound engine untouched." §2 shows that assumption is the one thing
that isn't true — the engine lacks every primitive the voice needs.
Throw alone is +150 in the engine; Entry 2 then rides this renderer
as parameters for ≈ +25 (Ivo's slice to confirm), so both voices
land ≈ +175 engine-side against her ~130 for the pair — the delta is
the Room and the bolt, priced honestly rather than assumed away.

No DB change, no migration, no new file, no settings surface.

---

## 7. Risks / device-only unknowns

1. **Audio-vs-haptic offset (the ±8 ms lock) is not constructible
   from code.** The haptic fires on the caller's thread at t=0; the
   audio reaches the speaker through the synth thread, a
   `WRITE_BLOCKING` stream write, and the AudioTrack's device buffer
   — a latency no file in this repo measures. *Device test:* 240 fps
   slow-motion video of a real arm with the phone's mic (or a second
   phone) recording; measure attack-to-first-buzz offset over 5 arms.
   Target ≤8 ms; report the actual number either way. If it fails,
   the honest fixes are haptic delay compensation (delay the waveform
   by the measured offset — a constant in the engine, one line plus
   the measurement) — never moving the audio earlier, which would
   detach it from t=0.
2. **Synth-thread readiness at t=0.** If the thread is mid-write of a
   legacy transient when the seal is offered, the seal renders behind
   it in the stream. The breath (120 ms with approach audio cut) plus
   the queue-clear make this a corner, not a path — but buffer
   occupancy at t=0 is device-measurable only (same test as risk 1:
   attack must land in the t=0 frame's audio, ±1 frame at 60 fps
   video / ±44 samples in a mic recording aligned to the session's
   start sample).
3. **Comb colouration on the real speaker** — §5's gate. g = 0.836 is
   solved arithmetic, not a heard result; comb spacing 29.7/31.1 ms
   puts teeth every ≈33 Hz and the 1.8 kHz loop lowpass is the only
   thing between the tail and a metallic read on a small driver.
4. **Bolt audibility vs startle on the real speaker.** 900 Hz-lowpassed
   noise at −6 dB is the layer most likely to read differently on
   device than on paper (speaker resonance near 900 Hz varies by
   unit). Tune band: bolt gain −6 dB ± 2 dB and wet send −12 dB ± 3 dB
   in the §5 sitting; outside those bands, it's a spec amendment.
5. **Fixed-seed noise and repetition.** The same grain every arm is
   the law's intent (verbatim cadence), but if the identical noise
   burst becomes *recognisable as a loop* on device, the identity
   argument inverts — a recognisable sample reads as a file, not a
   mechanism. Fallback, pre-priced in the render branch at no extra
   lines: seed from a per-install constant (still identical every
   arm on his phone; different across installs).
6. **Audio focus pre-request side effect.** `primeForSeal()` at latch
   requests `AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK` up to 210 ms before
   the seal; if he is mid-call or media is playing, the duck window
   starts at latch, not t=0. The cadence makes this ≤210 ms and
   refusal paths simply never play — the focus is released by the
   existing transient semantics. Flagged, accepted, not hidden.

---

## 8. Fork for Ren (one, with recommendation)

**The reserved voice is spent in at least thirteen other places.**
`playHarmonicSeal(true)` is also called from task completion
(`completeTaskAndAdvance`, VM `:574`), undo-delete (`:638`), task
split (`:672`), auto-split (`:705`), cue-task (`:723`), app-block
toggle (`:802`), add-custom-app (`:828`), profile apply
(`selectAndApplyProfile`, `:835`), profile creation
(`createCustomProfile`, `:869`), peek end (`:1017`), impulse
transmute (`:1026`), and the serial-plate easter egg (`:1057`) —
furniture wearing the seal's voice, in direct breach of
the treaty (seal and completion are reserved; a seal sound on a task
row spends the seal). This slice does **not** touch them.

**Fork:** (a) sweep them in this slice, or (b) leave them and sweep
separately. **My recommendation: (b).** Those call sites sit on Ivo's
desk (Tasks-local acoustics) and on Deck/Shield furniture owned by
other slices; re-voicing them from the synths seat, in the cadence
slice, would collide with at least two other builds and decide Ivo's
sounds for him. Ren to assign a named reserved-voice sweep as its own
slice, after Entry 1 + Entry 2 land, with Ivo holding the replacement
voices. Until then the debt is named here, in the open, so nobody
mistakes silence for approval.

---

## 9. Interface for Entry 2 (Ivo, the Laid Seal)

The renderer takes a parameter set, not a second code path. Filed
Laid parameters (beyond-deck-parts/orson.md §1) map onto §3's fields
one-for-one: body gain 0.62 → **0.124 (−14 dB)**, attack ramp **12 ms**
(the renderer's Throw path uses ramp 0; the field exists), body
duration 48 → **72 ms** (decay pair re-solved in his slice — the
filed pair will not stretch a body to 72 ms audibly, same finding as
§2 fact 5), lowpass **2.4 kHz** (one-pole α = 0.2896) on the body,
partial 4 **−8 dB further**, wet send **−26 dB**, bolt **+18 ms /
70 ms / −10 dB absolute**, haptic per his table. Catch rule is shared:
level-triggered, 400 ms floor, 2.5 s ceiling (§5). If any Laid
parameter cannot be expressed in these fields, that is a renderer
amendment to raise with me by letter, not a second renderer.

---

## 10. Mail / wall note

- **Inbox at start:** 5 letters, all read in full and moved to
  `mail/orson/read/` (none deleted): Cato (cadence wraps the 110 ms
  spring; eyes-closed test stands as the cadence's law — honoured:
  §3.6 makes it structural, and the spring's number is the slide
  slice's, not the sound layer's), Wren ×2 (two-note-road narrowing —
  approach audio is pre-t=0 and outside this joint; ellipsis-bell-sync
  — **not ruled here**: the Safe Break bell pair is a separate voice
  decision, owed by me outside this slice, and this file neither
  grants nor spends it), Fen (refusal of the road on room/address
  grounds — the road is not in this slice; nothing here pitches the
  damped zone), Ivo (the Assay — its strikes are fenced out of the
  breath in his filing, and §3's queue-clear is the enforcement from
  my side: an Assay strike still pending at t=0 is dropped, never
  allowed to delay the Throw).
- **Mail sent:** one letter to **Ivo** (`mail/ivo/0813__from-orson__
  seal-cadence-entry1.md`) carrying §9's renderer interface for
  Entry 2 and the A4 haptic-gap finding. No other letters — the
  ellipsis bell and the Assay fence need no reply beyond what §3 and
  this note already enforce.
- **Wall:** appended to COMMONS.md under my name this run: verdict,
  the two deaths, g = 0.836, and the §8 reserved-voice debt flagged
  for Ren.

— Orson, Wild · synths
