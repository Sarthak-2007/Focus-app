# Scout Finds 3 — Haptics + Sound Design for Controls

**Best serves: GitHub Issue #11 — Held Door** (the 60-second emergency hold is the hardest haptic problem in Monolith: sustained feedback that must build tension without becoming a 60-second buzz). The session-seal material in Finds 1, 4 and 5 also feeds #13 Session Room directly, but #11 is where this set earns its keep.

Scout: Klang. Hunting ground only: vibration patterns, tactile feedback, mechanical switch / seal / click / latch audio, haptic design guidelines and research. Only elite, concrete, parameter-bearing sources made the cut.

---

## 1. Android's primitive vocabulary — with actual durations, not vibes

**Sources:**
- https://developer.android.com/develop/ui/views/haptics/custom-haptic-effects
- https://developer.android.com/reference/kotlin/android/os/VibrationEffect

**What makes it elite:** This is the platform Monolith actually ships on, and Google publishes the estimated duration of every `VibrationEffect.Composition` primitive — a ready-made tactile alphabet with timings:

| Primitive | Est. duration | Character |
|---|---|---|
| `PRIMITIVE_TICK` | 5 ms | lightest tap |
| `PRIMITIVE_CLICK` | 12 ms | standard mechanical click |
| `PRIMITIVE_LOW_TICK` | 12 ms | softer, lower-frequency click |
| `PRIMITIVE_QUICK_FALL` | 100 ms | falling-off sensation |
| `PRIMITIVE_QUICK_RISE` | 150 ms | fast swell |
| `PRIMITIVE_SPIN` | 150 ms | wobble |
| `PRIMITIVE_THUD` | 300 ms | heavy, low settle |
| `PRIMITIVE_SLOW_RISE` | 500 ms | slow swell |

Three hard rules from the same docs: (a) intensity steps are only perceptible if scales differ by a ratio of **≥1.4** — use 0.5 / 0.7 / 1.0 for low/medium/high, never more than 3 levels of one primitive; (b) a gap of 5–10 ms between primitives is **undetectable** — use **≥50 ms** for a gap the finger can actually read; (c) from Android 16, `VibrationEffect.Builder` (timeline-anchored presets + `BasicEnvelopeBuilder`) gives automatic framework fallback, whereas raw `startComposition()` fails *entirely* if one primitive is unsupported — so build Monolith's signature effects on Builder, not Composition.

**For Monolith:**
- **Session seal at t=0:** `SLOW_RISE (500 ms, scale ~0.7) → CLICK (12 ms, scale 1.0)` — Google's own example composition is literally a slow rise followed by a sharp click. It reads as *pressure building, then the latch drops*. That is a seal, not a notification.
- **Switches:** press = `CLICK @ 1.0`, release = `CLICK @ 0.5–0.7` or `LOW_TICK` (see Find 3 for why the asymmetry is mandatory).
- **Held Door:** do NOT invent intermediate intensities in fine steps — the finger cannot tell them apart. Three escalation tiers max (0.5 → 0.7 → 1.0), spaced by real ≥50 ms gaps.

---

## 2. Apple Core Haptics / AHAP — intensity and sharpness are independent axes, and patterns can be driven live

**Sources:**
- https://dev.to/donnywals/adding-haptics-to-your-app-1l83
- https://danielbuettner.medium.com/10-things-you-should-know-about-designing-for-apple-core-haptics-9219fdebdcaa
- https://www.kodeco.com/10608020-getting-started-with-core-haptics/page/3?page=2

**What makes it elite:** Apple solved the exact design problem Android leaves open: a haptic is not one knob. Core Haptics separates **Intensity (0.0–1.0, how strong)** from **Sharpness (0.0–1.0, how crisp vs. round — effectively a low-pass character)**. Two event types only: **Transient** (~10–30 ms tap) and **Continuous** (sustained, with `attackTime` / `decayTime` / `releaseTime` envelope shaping — e.g. a 0.6 s continuous event at intensity 0.5, sharpness 0.1, attack 0.1 s, decay 0.3 s, release 0.2 s reads as a *wave*, not a buzz). Crucially, AHAP patterns support **dynamic parameters**: intensity/sharpness can be updated *while a pattern is playing*, driven by live input. Apple's predefined notification patterns also encode sentiment in rhythm: success, warning and error are the same generator family differentiated by pulse count and direction, learned once and recognised forever.

**For Monolith:**
- **Held Door (the killer application):** model the 60-second hold as ONE continuous effect whose intensity *and* sharpness are driven by hold progress — soft/round at second 0, gradually sharper and stronger as the thumb stays down. The user should *feel the door getting heavier*, not get ticked at like a kitchen timer. On Android this maps to a `BasicEnvelopeBuilder` / waveform envelope with control points, not a loop of `createOneShot()`.
- **Vocabulary discipline:** adopt the intensity × sharpness grid as Monolith's design language even on Android: a seal is high-intensity + high-sharpness (a latch), a warning is high-intensity + low-sharpness (a thud), a tick is low + high. Two axes give Monolith maybe six distinguishable feelings; one axis (amplitude only) gives three on a good day.
- **Sentiment rule (cross-checked by Android, Find 3):** negative outcomes get the *stronger* sensation. A failed/broken hold must feel heavier than a completed one.

---

## 3. Android haptics UX principles — press must outweigh release, and long vibrations must ramp or they buzz

**Source:** https://source.android.com/docs/core/interaction/haptics/haptics-ux-design?authuser=6

**What makes it elite:** Google's AOSP UX foundation makes three non-obvious, testable claims. (1) **Pairwise asymmetry:** a mechanical button's press impulse carries more perceived energy than its release, so haptic press feedback must be stronger than release feedback — equal press/release haptics feel *wrong* in a way users can't articulate. (2) **Clear vs. rich:** discrete state changes want one short, sharp, strong effect ("clear haptics"); repeated/gesture textures must be deliberately *subtle*, because repetition compounds perceived energy — a repeated effect that feels right once will feel overwhelming at the tenth repetition. (3) **Ramp rule:** long attentional vibrations must accelerate smoothly (ease-in amplitude curve via `createWaveform(timings, amplitudes, repeat)`) — a long vibration that starts at full amplitude produces audible buzz and reads as an alarm, not an intention. Google's published ramp example steps amplitudes 33 → 51 → 75 → 113 → 170 → 255 in 50 ms slices.

**For Monolith:**
- **Every switch/toggle in Monolith:** strong down-click, weaker up-click. This single asymmetry is the cheapest way to make a glass rectangle feel like it has moving parts.
- **Held Door design constraint:** any ticking/escalation during the 60 s hold is a *repeated texture* — keep individual ticks at `TICK`/`LOW_TICK` scale (≤0.5) or by second 40 the user will hate the door. Save full amplitude for the two moments that matter: the break-through and the release-without-breaking.
- **Session start:** if Monolith ever uses a longer "arming" vibration, it must ease in on the 33→255-style curve. A blocker that buzzes like a phone call at arm time has already lost its ritual character.

---

## 4. The knocks-vs-two-notes question, settled by meta-analysis: auditory icons beat earcons

**Sources:**
- https://psycnet.apa.org/record/2023-83012-001 (2023 systematic review & meta-analysis of brief audio alerts: auditory icons, earcons, spearcons, speech)
- https://core.ac.uk/download/161909652.pdf (field study: auditory icons vs. earcons for mobile notifications)

**What makes it elite:** This is the live Monolith question — *a knock/latch sound or an abstract two-note chime for the seal?* — and the research answers it directly. Definitions: an **auditory icon** is a real-world sound with an ecological relationship to what it means (a latch clicking = something latching). An **earcon** is an abstract musical motif with no ecological link (two notes = "done", by convention only). The meta-analysis found earcons **inferior to every other alert type** on subjective ratings, and inferior to auditory icons on accuracy and reaction time. The mobile field study is blunter: auditory icons performed significantly better on intuitiveness, learnability, memorability *and* user preference; some abstract earcons were described by participants as sounds they "came to hate" precisely because they kept misreading them.

**For Monolith — verdict:** **Knock wins as the primary seal sound.** A latch/knock is understood on first hearing, with zero learning, by a user who may be half-asleep at 3:30 AM — exactly Monolith's user. A two-note motif can still exist, but only as a *secondary brand signature layered after* the latch lands (latch first = meaning, notes after = identity), never as the sole carrier of "you are now blocked." Reversing that order builds the whole ritual on the weakest-researched sound type.

---

## 5. A complete UI sound family, synthesised from code — exact frequencies included

**Source:** https://itch.io/devlog/1730801/synthesizing-game-ui-sound-effects-with-code.amp

**What makes it elite:** A full, runnable synthesis recipe (oscillator + envelope, no samples) where every sound in the family shares one timbre, so confirm and cancel audibly belong to the same object. The parameters are the find:

- **Click** = two layers: a **12 ms burst of low-passed noise** (one-pole filter, ~4 kHz cutoff — the "contact") + a **40 ms tone gliding 1800 → 1200 Hz** (the "body"). Noise alone sounds thin; tone alone sounds electronic. Together: a physical click.
- **Confirm** = two soft-square notes (fundamental + 3rd harmonic at 1/3 + 5th at 1/5 amplitude), **660 Hz for 70 ms → 990 Hz for 160 ms** — up a perfect fifth.
- **Cancel** = same recipe, **520 Hz → 350 Hz**, second note drooping slightly. Direction alone carries meaning; timbre stays identical.
- **Envelope law:** 2 ms linear attack (zero attack creates a spurious click on top of your click), exponential release, 3 ms fade-to-zero tail, everything normalised to **−3 dBFS peak**, harmonics above ~8 kHz suppressed (they fatigue).

**For Monolith:**
- **The seal sound can be generated, not hunted:** latch = noise-burst contact (12 ms) + low body tone gliding down (e.g. ~400 → 200 Hz region for weight, vs. the bright 1800→1200 UI click) + the thud tail. One script produces the whole Monolith sound family — switch, seal, break, deny — guaranteed mutually consistent, no licence/sample archaeology, and re-tunable in an afternoon ("everything feels too bright" = one filter value, re-export all).
- **Direction grammar for the Held Door:** completed hold = rising interval (the fifth-up recipe); released-early / denied = falling interval with the droop. The user learns the grammar from the seal screen and reads the door without looking.

---

## 6. Google's multisensory team — sound and haptics are one waveform, and the best ones are under 8 ms

**Sources:**
- https://design.google/library/ux-sound-haptic-material-design
- https://blog.google/products-and-platforms/devices/pixel/google-haptic-design/

**What makes it elite:** Google maintains a 12-person sound-and-haptics team on the principle that *"sound consists of vibrations that you hear, and haptics consist of vibrations that you feel"* — one design act, two outputs, composed together as a single waveform, never bolted on separately. Two quotable standards: they spend serious craft on events **"less than eight milliseconds long"**, because good multisensory design "often disappears" — felt as quality, never noticed as an effect; and Pixel's vocabulary explicitly spans *flavours* — sharp vs. "round" vibrations, click vs. tick vs. low-tick ("crisper" vs. "lower frequency… more pleasant, more organic") vs. thud vs. quick-fall — each a distinct material feeling, not a volume level.

**For Monolith:**
- **Compose seal audio + seal haptic as one event with one timestamp**, not a sound file and a vibration that happen to fire together. Even a few ms of drift between the audible latch and the felt click breaks the "one physical object" illusion — this is the whole game for a Teenage-Engineering-feeling blocker.
- **Design target for Monolith's ordinary switches: disappear.** If users *notice* a routine toggle's sound, it's too long/loud. Reserve noticeability for exactly two moments: the seal (t=0) and the door.
- **Name Monolith's effects by material, not function** — following Pixel's flavour vocabulary (click / tick / low-tick / thud) keeps the team designing *what it feels like*, which is the stated product goal.

---

## 7. The physics underneath: LRA resonance is why "louder" is the wrong dial

**Sources:**
- https://developer.android.com/develop/ui/views/haptics/actuators
- https://www.engadget.com/2017-01-13-nintendos-hd-rumble-will-be-the-best-unused-switch-feature-of-2.html

**What makes it elite:** Android's actuator primer publishes the hardware truth: a crisp click lasts **10–20 ms**; LRAs resonate at either **~150 Hz (felt as "softer and fuller")** or **200–300 Hz (the band human skin is most sensitive to; felt as "smooth, sharp, penetrating")**; output falls off exponentially away from resonance; and drivers with **overdrive + active braking** are what separate a clean *click* from a mushy *bzzzt* (ringing). Android 16 exposes this as FOAM (frequency-to-output-acceleration mapping) via `VibratorFrequencyProfile`, so envelopes can be authored in frequency, not just amplitude. Nintendo's HD Rumble is the consumer proof of the same physics: a wideband LRA varying frequency/amplitude convinced players they could count ice cubes in a glass and marbles in a box — *material illusion from frequency variation alone*, with a cheap eccentric motor incapable of any of it.

**For Monolith:**
- **Vary frequency/sharpness for meaning, amplitude for urgency only.** On the Held Door, a slow drift from the ~150 Hz "full/soft" character toward the 200–300 Hz "sharp/penetrating" band as seconds pass would feel like rising tension *in the material itself* — no extra loudness, no extra annoyance. This is the envelope-API use case (Find 1/2), and it's the difference between a door that feels alive and a phone vibrating on a table.
- **Reality check to design against:** on a low-end ERM device none of this exists — every effect collapses to on/off buzz. Monolith's haptic signatures need an explicitly designed on/off fallback pattern (Android's own guidance, Find 1), or the ritual silently degrades on exactly the cheap phones many students own.
- **Test criterion:** if a Monolith "click" rings or can be *heard* across a quiet room, it's the actuator ringing, not design — shorten toward the 10–20 ms window before touching anything else.

---

## 8. A game sound designer's three-layer taxonomy for any button

**Source:** https://readmedium.com/a-game-sound-designers-guide-to-button-interactions-6837dd5cc977

**What makes it elite:** It decomposes every control interaction into three sounds with three different jobs, and most apps only design one of them: (1) **Consequential sound** — the noise the physical device itself makes (a real switch's click); on a silent touchscreen this layer is *missing by default*, and the designer's job is to replace it, because its absence is exactly what makes glass feel dead. (2) **Input reaction** — the immediate, minimal confirmation "your command reached the hardware," critical for precise-timing and sequence interactions. (3) **System response** — the later, richer sound of what the system *did* about it. Conflate (2) and (3) and the control feels laggy even at 0 ms latency; skip (1)'s replacement and it feels fake even when fast.

**For Monolith — a ready-made timing spec:**
- **Finger down on any Monolith control → input reaction only:** the lightest tick (`PRIMITIVE_TICK`, 5 ms) + the faintest contact noise from Find 5's recipe. Nothing else. It says "registered," instantly.
- **State actually changes (switch flips, seal engages, door breaks or holds) → system response:** the full designed event (Find 1's rise→click, Find 5's latch). It must be audibly/haptically a *different species* from the input tick, or users will double-tap a blocker thinking nothing happened.
- **The Held Door is a sequence interaction** (the taxonomy's stated high-stakes case): each escalation tier (Find 2) is an input-reaction-class cue; only completion/failure earns a system-response sound. Mixing the tiers up — big sounds mid-hold — spends the seal's authority before the seal.

---

— Klang (Scout 3)
