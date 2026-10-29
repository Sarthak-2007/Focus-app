# 02 — RIEGEL (Improvers, the bolt) — SLICE E · E1 RENDER ENGINEERING

*Joint: E1's render engineering only — the exact render path of
`playSealThrow()` against TODAY's trunk, the fixed-seed bolt, the bed
catch contract, and the device-gate fallback's form. The voice, gains,
offsets and cadence are Orson's (plan/slice-de-parts/orson.md) and are
NOT re-opened here; SLICE-D-E-BUILD-READY.md §2-E1 governs. Where this
file differs from Orson's §3–§6 it is mechanism only, and each delta
is named as a delta. All line citations are against today's trunk
`Focus-app/app/src/main/java/com/example/service/TactileSoundEngine.kt`
(378 lines, re-grepped this run — Orson's citations to a longer tree
have drifted; today's are used throughout).*

**Verdict: BUILDS, with Orson's single-queue render replaced by a
dedicated seal lane (§1). No fork in E1. One provenance correction
(§3.1) and one unassigned build owed outside E1 (the bed, §3.4).**

---

## 0. The problem, restated in today's numbers

Orson's design puts one 44 100-sample / 1000 ms render through an
engine built for the opposite:

| Fact (today) | Site |
|---|---|
| Legacy commands live in one serial `LinkedBlockingQueue<SynthCommand>(64)`; one synth thread polls (500 ms timeout), renders, `WRITE_BLOCKING`-writes, then polls the next. No mixing, no overlap, no priority. | queue `:66`; loop `:116` poll, `:143` write |
| Render cap #1: the shared `floatBuffer` is `maxSamples = SAMPLE_RATE × 0.16` = 7 056 samples / 160 ms; the loop coerces every command to it. | `:85`, `:111`, `:121` |
| Render cap #2: `enqueueTransient` coerces every command to ≤ 6 000 samples = **136.05 ms**. | `:235`, `:242` |
| The AudioTrack's stream buffer is ≥ `maxSamples × 4` bytes = ≥ 7 056 float samples = **160 ms of already-written audio** can sit between the synth thread and the speaker. | `:90` |
| `requestTransientAudioFocus()` runs inside the loop before every render — an unmeasured transaction charged to whatever renders next. | `:119` (def `:155`) |
| Header law: "Zero ambient sound mixer loops." No bed, no loop, no crossfade exists anywhere in the engine. | `:31` |

Consequence Orson's filing understates: his two defences — queue-clear
at t=0 and the 120 ms breath — clear only the *command queue*. They do
nothing about (a) a command the synth thread has already polled and is
mid-`WRITE_BLOCKING` on at t=0, or (b) up to 160 ms of legacy samples
already deposited in the AudioTrack buffer at t=0, which will play
*before* any seal sample written after them, moving the attack off
t=0 by up to that amount. The cadence law (attack onset at t=0, bolt
at +30 ms) is a sample-position law; a serial stream cannot promise
sample positions. §1 is the mechanism that can.

---

## 1. Q1 — The render path: own lane, pre-rendered, preempt — never wait, never mix

**Decision: `playSealThrow()` gets its own buffer and its own
AudioTrack lane. It never enters `commandQueue`. Against the serial
queue the answer is PREEMPT, in three concentric layers (§1.3), each
sufficient for one stratum of backlog. WAIT is forbidden (waiting
moves the attack off t=0 — a falsified frame, Orson §3.1). MIX is
forbidden (a legacy tail summing into the attack breaks the render's
bit-identity and falsifies the −46 dB tail arithmetic the catch in
§3 is solved against — room energy accounting assumes the Throw is
the only signal in its buffer).**

### 1.1 The lane

- **Own buffer:** one retained `FloatArray(44_100)` (176 400 B),
  allocated once at engine init, rewritten at every prime. Not
  Orson's per-command allocation in the loop — a retained buffer is
  what makes pre-rendering (§1.2) possible, and 176 KB retained is
  cheaper than 176 KB churned per arm.
- **Own track:** a second `AudioTrack`, `MODE_STREAM`, same format /
  attributes as the legacy track (`:92–109` shape), buffer size
  **185 220 B** (44 100 × 4 × 1.05 — the render plus 5 % headroom, so
  the entire 1000 ms fits in the track buffer with room; a track
  buffer smaller than the render is the one way this lane could
  silently become a queue again). Created lazily at first
  `primeForSeal()`, kept for the engine's life, released in
  `stopAllAudio()` (`:374`).
- **The legacy track becomes a field:** today it is a loop-local
  (`:92`). Hoist to `@Volatile private var legacyTrack: AudioTrack?`,
  assigned by the synth thread at creation. This is the handle §1.3
  layer 3 preempts through; it changes no legacy behaviour.
- **Sample 0 is still the attack.** The seal track's buffer contains
  ONLY seal samples, written into an empty track (§1.2), so the first
  sample the lane ever plays is buffer[0]. The bolt is index 1323
  *inside the same buffer* — no scheduling, on any thread, at any
  load, can move it relative to the attack. The landmarks are true
  by construction, not by timing luck.

### 1.2 primeForSeal(), rebuilt (this replaces Orson's §4.2 ≈8-line stub)

Called by the controller **at latch, t=0 − 210 ms**, AFTER the latch
knock has been fired (ordering is load-bearing, §1.3 layer 1). In
order, ≈34 lines:

1. `requestTransientAudioFocus()` — Orson's A5, unchanged: the focus
   transaction is paid inside the cadence, never at t=0.
2. Capture the **sequence barrier**: every `SynthCommand` gains one
   field, `seq: Long`, stamped from an `AtomicLong` in
   `enqueueTransient` (`:243`). prime records
   `sealBarrierSeq = seqCounter.get()` — because the knock was fired
   before prime, the knock's seq is ≤ the barrier; everything older
   is pre-latch backlog.
3. **Purge:** `commandQueue.removeIf { it.seq < sealBarrierSeq }` —
   pre-latch backlog still pending is dropped now, at −210 ms, not
   discovered at t=0.
4. Signal the **seal render thread** (one new daemon thread,
   "MonolithSealRender", created in `init` beside the synth thread):
   it runs the pure `renderSealBuffer(out)` (§2) into the retained
   buffer, writes the full buffer to the seal track (track stopped;
   `WRITE_BLOCKING` loop until all 44 100 samples are accepted), and
   only then sets `@Volatile sealReady = true`. Render cost (body
   sines + bolt + combs over 44 100 samples) is paid here, inside a
   210 ms window that has nothing else to do — at t=0 there is no
   render left to pay.

Companion: `cancelSealPrime()` (≈6 lines) — called by the cascade on
any post-latch annul/refusal path (fork F7's L+1500 annulment is the
named one): clears `sealReady`, clears the barrier. A primed seal
that never fires must not leave a barrier standing over the queue.
This is an obligation ON the D8 cascade slice's call sites, stated
here as interface, priced in §5 to the VM line.

### 1.3 playSealThrow() at t=0 (≈26 lines) — the three preempt layers

On the caller's (transition) thread, in order:

1. **Haptic twin** — Orson's §3.5 waveform verbatim (true gap:
   timings `[0, 24, 38, 16]`, amplitudes `[0, 255, 0, 175]`).
2. **Preempt layer 1 — the queue.** `commandQueue.clear()` (Orson's
   queue-depth-1 law, retained): anything still pending at t=0 is
   late approach audio. Backstopped in the synth loop: a polled
   command with `seq < sealBarrierSeq` while a prime is live is
   dropped, never written (covers a command polled a microsecond
   before the purge).
3. **Preempt layer 2 — the drain window (the lawful path, where
   preempt is a no-op).** The latch knock — the last lawful entrant —
   is bounded by cap `:242` at ≤ 136.05 ms and in its filed form is
   16 ms; the track buffer holds ≤ 160 ms; both are < the 210 ms
   latch→t=0 window, and the controller cuts approach audio at the
   seat frame (−120 ms) besides. In every lawful cadence, by t=0 the
   knock finished ≥ 74 ms ago and the legacy track is silent and
   empty. Layers 1 and 3 exist for the unlawful residue — a flooded
   detent backlog, a stalled synth thread — not for the normal arm.
4. **Preempt layer 3 — the track.** `legacyTrack?.pause();
   legacyTrack?.flush()` and `laneEpoch++`. pause+flush discards
   every deposited-but-unplayed legacy sample; the seal never waits
   behind the 160 ms the stream buffer can hold. The one race —
   the synth thread blocked inside `WRITE_BLOCKING` at that instant
   — is closed structurally, not hopefully: the legacy write at
   `:143` becomes **chunked writes of 1 024 samples (23.2 ms) with
   an epoch check before each chunk**; a writer whose epoch went
   stale abandons the command within one chunk, and the synth loop,
   before its next write, re-runs pause+flush itself if the epoch
   changed under it. Invariant: no stale sample can be *played*
   after t=0, because the track only resumes from the loop, and the
   loop flushes first. **Resume is loop-owned:** the legacy track
   stays paused until the synth thread next polls a lawful command,
   flushes once more (idempotent), calls `play()`, and writes. The
   first post-seal transient (minutes later, in any real session)
   pays that; nothing in the seal's first second can.
5. **Fire.** If `soundEnabled`: `sealTrack.play()` — and nothing
   else. No render, no focus call, no queue hop, no write. The t=0
   audio cost is two track-control calls and one `play()`, bounded
   and independent of queue state, backlog, and render load.
   If `soundEnabled == false`: haptic only, layers 2–5 skipped —
   Orson's S0 law (muting loses timbre, never the event).
6. Clear `sealReady` / barrier (the prime is consumed).

**Fallback, if `sealReady == false` at t=0** (prime missed, render
thread faulted, track creation failed): render synchronously on the
caller thread via the same pure `renderSealBuffer()`, then
`sealTrack.play()` FIRST and chunked-write the buffer after it —
the first chunk carries the attack and the bolt, so the landmarks
start on time and the tail streams in behind them. Degraded (attack
late by one render, ≈3–25 ms by device class), never silent, never
queued. One render function, two delivery paths — the fallback
cannot drift from the primed path's samples (test T2 asserts it).

*Delta vs Orson, stated plainly:* Orson priced the seal as a queued
command and carried its t=0 timing as a device-measured risk (his
§7.2). This lane spends ≈+82 net lines (§5) to make the timing
structural. What remains device-measured is only the output
pipeline's own latency (DAC buffer), which no design in this engine
can remove and which Orson's §7.1 test already measures.

---

## 2. Q2 — Fixed-seed noise: xorshift32, our arithmetic, literal constants

**Generator: xorshift32 (Marsaglia), shifts (13, 17, 5), implemented
inline in the render — no library RNG.**

```kotlin
var s = SEAL_BOLT_SEED
fun nextNoise(): Int {
    s = s xor (s shl 13); s = s xor (s ushr 17); s = s xor (s shl 5); return s
}
```

- **Why this and not the alternatives:** `kotlin.random.Random(seed)`
  expressly does not guarantee a stable sequence across platform /
  version — disqualified by the bit-identical law. `java.util.Random`
  is Javadoc-specified, but it is still someone else's algorithm in
  the loop, at 48-bit LCG quality, for zero benefit. xorshift32 is
  six lines of Kotlin `Int` arithmetic; JVM integer overflow and
  shift semantics are spec-defined, so the sequence is identical on
  every device, every process, every ART version, forever — nobody
  can "update" it under us. State is a local in `renderSealBuffer()`,
  re-seeded per render: every seal renders the same grain because
  every render starts from the same state.
- **Seed, frozen:** `SEAL_BOLT_SEED = 0x9E3779B9` (as signed Int,
  −1640531527; non-zero, as xorshift requires — the zero state is
  absorbing). Orson left the value to the builder; leaving it open
  is how two builders ship two bolts, so it is fixed HERE. Golden
  vector, pinned by test T1 — first four raw outputs from this seed:
  `0x510C4619, 0xE02E553E, 0x7BB98F3A, 0x0183A8B5`.
- **Where it lives:** the companion object of `TactileSoundEngine`,
  beside `PARTIAL_WEIGHTS` (`:51`) — one constants block for the
  whole seal: seed, `BOLT_LP_ALPHA = 0.1203f`, `BOLT_PEAK_GAIN =
  0.31074f` (= 0.62 × 10^(−6/20), Orson's 0.311 unrounded),
  `BOLT_ENV_STEP = 0.9987410f` (= exp(−1/(0.018 × 44100)), τ = 18 ms),
  `COMB_G` (§4), `LOOP_LP_ALPHA = 0.2262f`, `WET_SEND = 0.25119f`
  (= 10^(−12/20)), `TAIL_CATCH_SAMPLE = 39_690`. **No table, no
  asset, no prefs, no settings read** — the engine's zero-audio-files
  law (`:31`) stands, and Orson §3.6's input-free renderer is
  extended: the render reads no clock, no device model, no stored
  state of any kind.
- **The literal-constants rule (the bit-identity substrate):** the
  bolt and Room layers contain **zero transcendental calls**. Every
  coefficient above is a decimal literal, derivation in its comment.
  Reason: `Math.exp` / `Math.sin` are only guaranteed to ~1–2 ulp
  across implementations; IEEE-754 multiply/add on literal floats is
  exact and portable. Sample→float mapping is likewise exact:
  `(nextNoise() ushr 8).toFloat() * (1f / 8388608f) − 1f` — the top
  24 bits mapped to [−1, 1) in exact 2⁻²³ steps. The one-pole state
  starts at 0f and is carried across the bolt's 2 426 samples only
  (Orson §3.3). *Scope note, honest:* the shipped BODY keeps its
  `sin()` path and its `exp()`-derived decay constants (`:53–54`)
  untouched — E1's law is "body unmodified," and same-process /
  same-device identity (what the eyes-closed test and T2 exercise)
  is exact; the bolt, the layer whose grain IS its identity, is
  additionally bit-exact across devices by the construction above.
- Orson's risk-5 fallback (per-install seed if the grain reads as a
  recognisable loop) is **rejected as a mechanism**: it would break
  cross-device identity and the golden test. If the device sitting
  ever raises it, it returns as a spec amendment, never a switch.

---

## 3. Q3 — The bed contract, stated exactly — and a provenance correction

### 3.1 Correction first

The brief (and Orson's §5) attribute the bed to "Otis's heartbeat
engine slice." **Otis's filed part (invention/maximpl-parts/09-otis.md)
contains no audio bed at all.** His heartbeat is a *readiness* engine
— a `ReadinessBus` StateFlow fed by service bind hooks and the Shield
loop's 2 s beat — data about permissions, not sound; the strip runs
on it (SLICE-D-E-BUILD-READY.md:17), the room does not. The bed's
actual sources are Orson's own: the recipe in
plan/beyond-deck-parts/orson.md §3 and the catch contract in
plan/slice-de-parts/orson.md §5. The contract below is quoted from
those, exactly. The attribution matters because §3.4's build is
currently assigned, on paper, to a slice that never specced it.

### 3.2 Which bed

The **seeded session bed loop** (Orson's filed S1 recipe, unchanged):
**brown noise lowpassed at 420 Hz + hiss bandpassed at 5.2 kHz at
−58 dB + a 78 Hz partial.** It is the bed every session sits in; at
the finish it is the same bed that fades out 300 ms into 400 ms of
true silence (Slice E HELD FRAME, E4). Suppression, proximity fade
(800 ms out / 1.2 s in) and audio-focus rules apply to tail and bed
identically — by the catch they are one signal path.

### 3.3 What "caught" means at ≈900 ms — the contract, exactly

1. **Level:** the bed is caught at **−46 dB** (relative to the seal's
   0 dB ref, gain 0.62 → absolute amplitude ≈ 0.0031). The Throw's
   comb tail crosses −46 dB at **≈900 ms** by construction (g =
   0.836, §4; sample 39_690).
2. **Envelope law:** at every sample, **room energy = max(tail,
   bed), never the sum**. The bed's crossfade is **level-triggered**
   at the tail's −46 dB crossing, begins underneath the tail at the
   level the tail has reached, and completes by **2.5 s post-seal at
   the latest**. Catch window for the trigger: **floor 400 ms,
   ceiling 2.5 s** after t=0 (Throw nominal ≈900 ms sits inside it).
3. **No arrival swell:** the bed never rises above the tail it
   replaces at any instant — monotonic decay into held level. There
   is no bed-arrival event; the 2.5 s fader ramp of the old S3 spec
   is KILLED, and shipping a fader to fake this catch is forbidden
   (it returns only via the §4 gate failure, owned by the bed slice).
4. **E1's side of the contract (what the Throw guarantees the bed):**
   the tail crosses −46 dB at sample 39_690 ± the T3 tolerance; and
   samples **39_690–44_100 fade linearly to zero** underneath the
   catch, so the render never truncates audibly. Because the tail is
   a deterministic render, the bed slice does not need to *measure*
   the crossing live: `TAIL_CATCH_SAMPLE` is exported from the
   engine's constants block as the trigger coordinate, and T3 keeps
   it honest against the golden render.

### 3.4 If the trunk has no bed — the build that is owed

**The trunk has no bed.** Grep-verified this run: no bed loop, bed
gain, or crossfade anywhere in `TactileSoundEngine.kt`; the header
forbids ambient loops outright (`:31`). E1 neither builds nor fakes
it. The owed build, named for assignment:

> **SESSION-BED build (unassigned — NOT in Otis's part, NOT in E1):**
> the seeded loop of §3.2 on its own looping lane; the
> level-triggered catch of §3.3 driven by `TAIL_CATCH_SAMPLE`; the
> finish fade-out (300 ms → 400 ms silence) that E4's HELD FRAME
> assumes; suppression / proximity / focus behaviour shared with
> the tail. Until it lands, the Throw ships lawfully uncaught:
> the tail ends at −46 dB and fades to zero by 1000 ms — no click,
> no swell, no lie (Orson §5). E4's "bed out 300" line is dead
> machinery until this build exists and should be read that way.

---

## 4. Q4 — The device-gate fallback g → 0.78: a build-time constant

**Decision: build-time constant. `COMB_G = 0.836f`, one literal in
the companion constants block. The fallback is a one-line source
change to `0.78f`, recompiled, gate re-run, goldens re-pinned — in
one commit. No runtime switch, no persisted flag, no settings
surface.**

Why:

1. **Bit-identity forbids the switch.** The render must be a pure
   function of frozen constants (§2). A runtime flag makes the seal
   two voices depending on a stored bit — exactly what E2's
   bit-identical ruling (B-5) killed in selection-machinery form.
2. **The gate is a qualification, not a preference.** It runs once,
   on Sarthak's phone, before ship (Orson §5: ring / buzz / pitch,
   or a narrowband component above −50 dB after 300 ms, or the tail
   plainly audible *as an event* past 1200 ms). Its outcome is a
   fact about the shipping build, like the tune bands (bolt −6 ±
   2 dB, wet −12 ± 3 dB) — all settled in the same sitting, all as
   constants.
3. **A switch is unpriced surface.** Persistence, a toggle home, a
   default, a migration story — Orson's price explicitly carries
   "no settings surface," and a hidden flag is worse than a visible
   one (it can flip without a commit ever knowing).
4. **The derived constants move together — a switch would desync
   them.** At g = 0.78: loss per loop = 2.158 dB, −46 dB reached in
   21.3 loops ≈ **648 ms** (Orson's ≈640), so `TAIL_CATCH_SAMPLE`
   moves 39_690 → ≈28_600 and the golden tail changes. As a commit,
   T3 (measured crossing within ±25 ms of `TAIL_CATCH_SAMPLE`) fails
   the build if g is edited without its dependents. As a runtime
   flag, the catch coordinate and the bed trigger would silently
   disagree with the render — the worst failure this joint can ship.
   (The 900–1000 ms end fade is unaffected: at g = 0.78 the tail is
   ≈ −64 dB by 900 ms, already below bed level; the fade stays as
   harmless insurance.)
5. If the gate fails a second time at 0.78, **the fader ships** —
   that is not a third value of g; it is the SESSION-BED build (§3.4)
   absorbing a 2.5 s ramp, owned and priced there, never in E1.

---

## 5. Re-price — my share of E1

Counting method: net lines = added − deleted non-blank lines in the
eventual diff (`git diff --numstat` convention), comments included
(they carry the frozen derivations), against today's trunk files.
Orson priced E1 at ≈+210 net total (engine +150, views −2, VM +2,
tests +60). This file replaces his engine mechanism; the delta is
mine, itemised:

| # | Item (all `service/TactileSoundEngine.kt` unless noted) | Net |
|---|---|---|
| 1 | Seal lane: `legacyTrack` hoisted to `@Volatile` field; seal track build at first prime (185 220 B buffer) + release in `stopAllAudio()` | +16 |
| 2 | Seal render thread + prime machinery beyond Orson's stub: barrier capture, queue purge, render→write→`sealReady` handshake | +22 |
| 3 | Sequence barrier: `seq` field on `SynthCommand` (`:58ff`), `AtomicLong` stamp in `enqueueTransient` (`:243`), loop drop of pre-barrier commands | +14 |
| 4 | Track preempt: pause+flush at t=0, `laneEpoch`, chunked legacy write (replaces the single write, `:143`), loop-owned flush+resume | +16 |
| 5 | `playSealThrow()` delivery split (primed `play()` vs play-then-write fallback) beyond Orson's offer-based body | +4 |
| 6 | Determinism substrate: constants block (§2) + inline xorshift32 + pure `renderSealBuffer()` extraction shared by both delivery paths, beyond Orson's in-loop render branch | +6 |
| 7 | `cancelSealPrime()` + its one call obligation at the cascade's annul path (`ui/MonolithViewModel.kt` or the controller's home file) | +4 |
| | **Engine/VM delta over Orson** | **+82** |
| 8 | Tests beyond Orson's +60: golden noise vector (T1), primed≡sync identity (T2), catch-sample/g sync (T3), bolt-support exactness (T4), barrier purge (T5) | +35 |
| | **My share of E1, total delta** | **≈ +117 net** |

**E1 re-priced whole: ≈ +210 (Orson) + 117 ≈ +327 net** — engine
≈ +232, views −2, VM ≈ +3, tests ≈ +95. ±10 %. What the +117 buys,
in one line: t=0 stops being a measurement and becomes a property.
No DB change, no migration, no new file, no settings surface — as
Orson priced; the seal render thread is the only new runtime object.

Insertion points, collected: companion constants beside `:51`;
`SynthCommand.seq` at `:58ff`; track field + seal thread in `init`
(beside `:83ff`); loop changes at `:116–143`; `enqueueTransient`
stamp at `:243`; `primeForSeal()` / `cancelSealPrime()` /
`playSealThrow()` / `renderSealBuffer()` as new members beside
`playHarmonicSeal` (`:336`); `stopAllAudio()` extension at `:374`.

---

## 6. Acceptance tests

JVM / Robolectric (pure paths — the render and the queue logic are
Android-free by extraction):

- **T1 · Golden noise.** First four xorshift32 outputs from
  `SEAL_BOLT_SEED` equal `0x510C4619, 0xE02E553E, 0x7BB98F3A,
  0x0183A8B5`; a hash of the full 2 426-sample bolt segment is
  pinned. Any seed/shift/mapping edit fails here first.
- **T2 · Identity.** Two calls to `renderSealBuffer()` produce
  array-identical output; the primed path's buffer and the fallback
  path's buffer are the same array contents (Orson §3.6, extended
  to both delivery paths). Buffer length exactly 44 100; no sample
  exceeds the clamp; sample 0 is the attack (no leading zero run).
- **T3 · Tail & catch sync.** On the golden render: level at 900 ms
  within −46 ± 3 dB (Orson's band), and the measured −46 dB crossing
  within ±25 ms (1 103 samples) of `TAIL_CATCH_SAMPLE`. This is the
  test that makes a g-only edit (§4) unshippable.
- **T4 · Bolt support, exact.** The bolt layer (rendered standalone
  via the pure bolt function) is exactly 0 outside samples
  [1323, 1323 + 2426) — onset at 1323 with zero tolerance, stronger
  than an energy-window test; bolt ends at sample 3 749 = +85.0 ms,
  inside E2's "audio complete ≤ +90" landmark.
- **T5 · Barrier purge.** On a real `LinkedBlockingQueue`: enqueue
  three commands, capture barrier, enqueue one more; purge removes
  exactly the pre-barrier three; the post-barrier command survives.
  After `cancelSealPrime()`, the loop's barrier drop is inert.
- **T6 · Uncaught ending.** With no bed present (always, today):
  samples 39_690–44_100 fade monotonically to exactly 0 at 44_099,
  and no sample after 39_690 exceeds the −46 dB envelope.

Device (Sarthak's phone, quiet room — Orson §5/§7 tests, extended):

- **T7 · Landmark timing.** 240 fps slow-motion + mic recording over
  5 real arms: attack-to-haptic offset (target ≤ 8 ms, report the
  actual number), and attack aligned to the t=0 frame ±1 frame.
- **T8 · Forced-stall preempt (new, this file's mechanism on trial).**
  Via a test trigger, enqueue a maximum-length legacy transient
  (136 ms) 5 ms before a seal: the seal attack must still land
  within one output-buffer period of t=0 and the legacy transient
  must CUT — not tail through, not sum. If T8 fails, §1.3 layer 3
  is wrong on real hardware and this file reopens before E1 ships;
  layers 1–2 passing without layer 3 is not a pass.
- **T9 · Room gate.** Orson §5 verbatim: ring / buzz / pitch at any
  point in the decay, narrowband > −50 dB after 300 ms, or tail
  audible as an event past 1200 ms → `COMB_G` 0.836 → 0.78 (one
  commit, §4), re-run once; second failure → fader, owned by the
  SESSION-BED build.

---

## 7. Named risks

1. **Cross-thread pause/flush vs a blocked `WRITE_BLOCKING`.**
   Android's AudioTrack methods are thread-safe at the native
   handle, but a writer blocked on a full buffer at the flush
   instant is the one behaviour no document pins down. Mitigated
   structurally (chunked writes + epoch + loop-owned re-flush,
   §1.3); proven or refuted by T8 on device. Residual if T8 passes:
   none audible — a stale chunk can be deposited after a flush but
   cannot be played, because resume flushes first.
2. **Pre-write capacity variance.** If a device clamps the seal
   track's buffer below 185 220 B, prime's write cannot complete
   and `sealReady` stays false — the design degrades to the §1.3
   fallback (attack on time via play-then-write), it does not
   deadlock: prime's write runs on the render thread, and t=0 never
   waits on it.
3. **OEM pause() latency** adds to the output-pipeline latency that
   T7 measures. It affects only the unlawful-residue path (layer
   3); the lawful path's legacy track is already silent.
4. **The bed may never be assigned.** §3.4's build has no owner
   today. E1 is lawful without it (T6), but E4's finish choreography
   quotes a bed-out that does not exist — flagged so nobody builds
   E4 against a phantom.
5. **Render-thread priority.** Deliberately default: the prime
   window is 210 ms against ~10 ms of work. If a device class ever
   starves it, `sealReady` misses and the fallback fires — the
   failure is a late-by-one-render seal, never a lost one.

## 8. Forks

**None in E1.** The lane, the generator, and the constant are
decided above with reasons. Two items travel up, neither of which
changes a line of E1 either way they land: (a) the **Otis bed
attribution** (§3.1) — the record should be corrected wherever
SLICE-D-E repeats it; (b) the **SESSION-BED build needs an owner**
(§3.4) — Ilya/Ren to seat it; E1's `TAIL_CATCH_SAMPLE` export is
ready for it whenever it is seated.

## NEXT (Law 10)

- **I want next:** the E2-residue check — B-5 killed the Laid Seal's
  selection machinery, but I have not verified that no part file
  still prices a renderer *parameter set* (Orson §9's interface)
  that the bit-identical ruling makes dead machinery; a grep-and-
  strike pass over slice-de-parts + slice-e-parts, priced at
  reading cost only.
- **I could do, unassigned:** the same lane treatment for
  `playCompletionPulse()` at the finish (E4) — its E0 is discovered
  up to 1000 ms late on the VM's 1 s ticker today, and a primed
  lane at the final block's edge would make the completion attack
  structural too; needs Wren's E4 timing to land first.

— Riegel, Improvers · the bolt (seated by Ilya)
