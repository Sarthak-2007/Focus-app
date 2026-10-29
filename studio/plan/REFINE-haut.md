# REFINE — HAUT · the sensory layer across all six directions

*Haut, Feel/sensory specialist (Finishers' craft), seated directly
under Ren in the Refinement Ring, Oct 29, 2026. Charter (Sarthak,
~20:25 IST): "how app feels smell and everything. And everything
intentional." This file prices every feel claim in the art wave
against the machinery that actually exists. Written against the
three landed art-direction letters (Obsidian, Dawn, Noir) and the
three territories (Signal, Temple, Blueprint); the PRESENTATION
specs had not landed at writing — where a designer's own FEEL
section later differs from a sheet here, the merge is Ren's.*

---

## §0 THE REAL INSTRUMENT (ground truth, read from trunk)

**Sound** — `service/TactileSoundEngine.kt`. Fully procedural
float-PCM at 44.1 kHz, zero audio files, zero loops (the engine's
own charter, and his Oct 28 verdict removed the soundscape mixer).
Every voice is one parameter set into one synth: 4 partials
{0.6, 0.28, 0.37, 2.2}, a chord ratio, a fundamental (90–2400 Hz),
a duration (≤160 ms on the queue path), a gain. The decay envelope
is FIXED for all queue voices — dual exponentials, per-sample
steps exp(−0.0114) / exp(−0.0313). No world can buy a longer
ringing tail through the queue; the envelope family is shared.

Existing named voices (the palette any world must speak with):
- `playRotaryDetent(freq)` — wheel ticks; call sites already pitch
  them 420–760 Hz, one site pitches by value (480 + value×2.5).
- `playSeatKnock(ms, amp01, pitch)` — the pawl stopping the
  flywheel; the ONLY feel that scales with load: 16 ms @ 0.85 at
  one block → 24 ms @ 1.0 at eight, pitch falls as mass rises.
- `playMechanicalLatch(engaged)` — two-stage: strike, gap, seat.
  760 Hz engaging, 520 Hz releasing.
- `playFirmClick()` — hard strike, breath, softer seat (620 Hz).
- `playTearPulse()` — the counterfoil tear (240 Hz, chord 1.21).
- `playCompletionPulse()` — three softening ticks (185 Hz, 1.33).
- `playHarmonicSeal()` — legacy 48 ms seal body (360/480 Hz).

**The Throw is frozen law.** The seal's real voice is a 1-second
pre-rendered buffer — 360 Hz body + a deterministic-noise bolt at
+30 ms + a two-comb room — bit-identical at every seal, pinned by
a golden SHA-256 (`d204b95a…`), fired from exactly one call site
(`SessionSealTransition.executeSeal`, its last statement). Hand
arm and routine arm sound the same BECAUSE they are the same
samples. **Any direction proposing its own seal sound is
proposing to break a shipped truth law** ("a seal is a seal");
that is a law amendment for Ren to price (new render, re-pinned
hash, full seal-suite re-proof), never a design token. Worlds
speak in the APPROACH — the latch, the knocks, the prime's
silence — never at the seal.

**Haptics** — `VibrationEffect.createWaveform` amplitude
envelopes, fired from the same call sites as the sounds. Limits,
named not wished away:
- minSdk 24; amplitude envelopes exist only API 26+. Below that,
  every shape degrades to a plain timed buzz.
- The code NEVER queries `hasAmplitudeControl()`. On motors
  without amplitude control — much of the Android fleet — the
  envelope's amplitudes are silently flattened to on/off. Timing
  survives everywhere; amplitude is best-effort everywhere.
- No `VibrationEffect.Composition` primitives are used anywhere.
  Practical segment floor ≈5 ms; cheap ERM motors smear or skip
  shorter/softer segments, and may not even start under ~40%
  amplitude inside a 4 ms segment. Continuous modulated vibration
  (a drag "hum") is not renderable — stepped segments only.

**Motion** — `ui/theme/MonolithCurves.kt` + `MonolithKin` + the
wheel integrator in `BlockitCoreViews.kt`. Duration vocabulary
{110, 140, 180, 250, 260, 280, 420, 520, 1050} ms; three master
beziers (A seat-exact, B fast-rise/long-settle, C slow-in/long-
tail) + named specials (HubPressDip 45 ms, HubSeatReturn 160 ms,
ReturnHome 650 ms, vertigo pair, press dip 90 ms / press-back
≤150 ms, rail 180+60 ms per stop, drawer 280 seat / 160 throw).
The wheel is ONE hand-written (θ, ω) integrator: knurl 3°/120
teeth, viscous coast τ 0.34→0.48 s with load, well spring ζ=0.38
— exactly two overshoots, seat ≤520 ms, fling deadline ≤1400 ms
at every count. Those numbers are REGISTERED; a world that wants
different wheel physics is asking for a register amendment and a
re-proof at all eight counts, not a theme token.

**"Smell"** — no olfactory hardware exists. Any literal smell
claim is FRAUD. Smell is lawful only as atmosphere specified
through real channels: colour temperature, sound brightness,
motion viscosity, event density. A direction whose atmosphere
cannot be restated in those four terms is selling vapour.

Verdict tags used below: **LAWFUL** (existing params/call sites) ·
**PRICED** (real cost named) · **AT RISK** (renders on good
hardware, degrades silently on the fleet) · **FRAUD** (the phone
or the law cannot produce it).

---

## §1 OBSIDIAN — volcanic black glass, one ember

*Grammar: restraint, mass, darkness. Feel must be the sparsest
of the six: few events, low voices, long shadows.*

- **Haptics.** The house voice is the firm click, not the tick.
  Detents drop in amplitude (180→~120 strike) and read as a bead
  rolling in a dark slot; the seat knock stays full — in a quiet
  world the knock IS the furniture. Latch: keep two-stage, deepen
  the gap (26→34 ms) so the seat lands like a stone set down.
  LAWFUL (waveform arrays + call-site params).
- **Sound.** Run the synth dark: detent fundamentals 420–540 Hz
  (the low end of the existing ladder), latch chord ratio 1.2→0.9
  (less shimmer), seat-knock pitch toward 220–260 Hz. The ember
  is spent once visually; sonically Obsidian spends NOTHING at
  the seal — the prime's silence before the Throw is its
  signature: a held breath in a dark room. LAWFUL.
- **Motion.** Curve C character throughout; HubSeatReturn's long
  settle is the house rhythm. The wheel asks the one real price
  in this sheet: coast τ up (0.34→~0.5 s base) so the dial feels
  like glass rotating in oil. **PRICED** — register amendment +
  fling-deadline re-proof at all counts (the deadline is a law,
  not a mood).
- **Atmosphere ("smell").** A basalt room after rain: cold,
  mineral, still. Specified: lowest colour temperature of the
  six, lowest event density, lowest sound fundamentals. At
  3:30 AM this face asks nothing of tired eyes OR tired ears.
- **Feasibility note.** Obsidian survives the amplitude-less
  fallback best of all six: its shapes are timing-led (gaps,
  seats, silences), amplitude only shades them. This is the
  sheet the fleet degrades most gracefully.

## §2 SIGNAL — Swiss poster print, flat fields, giant type

*Grammar: print. Flat, binary, no gradients, no resonance —
a poster does not fade and ink does not ring.*

- **Haptics.** Square character: single-segment strikes at full
  amplitude, NO envelopes — detent becomes a 6 ms @ 255 stamp,
  the latch a single stamp + a printed gap + a stamp. Envelopes
  are off-grammar; Signal's discipline is that every event is a
  full stop or an absence. LAWFUL (and cheap).
- **Sound.** Dry, high, short: fundamentals 900–1200 Hz,
  durations cut to 4–8 ms, chord ratio 1.0 everywhere (no chord
  shimmer — print has no resonance). The completion pulse's
  three softening ticks are off-grammar (softening is a fade);
  Signal re-voices completion as three EQUAL stamps. LAWFUL.
- **Motion.** The expensive sheet. Poster grammar forbids
  overshoot — but the wheel's well spring is ζ=0.38, exactly two
  overshoots, one machine for every world. A ζ≈1.0 wheel for
  Signal alone is a mechanism fork: separate physics per skin,
  double re-proof, and a hand that relearns the wheel when the
  skin changes. **PRICED — highest feel cost in the wave.**
  Ren's cheaper lawful reading: Signal keeps the shared wheel
  (the wheel is machinery, and machinery is not skinned) and
  takes its flatness in chrome motion only — tab/drawer/rail on
  Curve A, zero decorative overshoot anywhere else. LAWFUL.
- **Atmosphere.** A print shop at deadline: paper, solvent, a
  hot press. Specified: highest event pitch, shortest durations,
  zero decay theatre.
- **Feasibility note.** Signal is the poorest feel-carrier of
  the six despite its visual force: its grammar forbids
  envelopes, resonance and overshoot — most of what the
  machinery can say. Not a defect of execution; a property of
  the world. Judging should weigh it as such.

## §3 DAWN — luminous morning glass

*Grammar: light arriving. Attacks soften, decays breathe —
but the tool must still command at 3:30 AM.*

- **Haptics.** Ramped onsets instead of strikes: detent as
  4 ms @ ~60 → 6 ms @ ~140 → tail; latch seats swell rather
  than snap. **AT RISK** — the ramp's first segment is exactly
  what cheap ERM motors cannot start (low amplitude, 4 ms).
  Timing-led repair, per the feel law in §7: the MEANING moves
  into segment timing (a two-beat swell: short–gap–longer),
  amplitude only shades it. With that repair: LAWFUL.
- **Sound.** Mid fundamentals (520–760 Hz), chord ratio 1.5 —
  the open ratio the legacy seal body already uses — as house
  warmth; durations at the long end of the queue's range so
  decays breathe. LAWFUL. One boundary: Dawn may not soften
  the seat knock below legibility — the knock is a proof
  event (the pawl seated), and a proof you cannot feel at
  3:30 AM is a lie of omission. Floor: knock amplitude ≥0.7.
- **Motion.** Curve B (fast rise, long settle) as the house
  curve; small overshoot tolerated in chrome only. LAWFUL.
- **Atmosphere.** Cool air through a window, wet earth, first
  light. Dawn owns the wave's one genuine atmosphere
  innovation: the sky keyed to his actual timeline (03:30
  banked → FASTED sunrise → DAY full light → 20:00 ember) —
  her own proposed study. It is atmosphere with a clock, and
  it is the only "smell" in the wave that CHANGES as his day
  does. **PRICED** — theme-on-a-schedule is new behaviour
  (rides the routine engine's day, not new machinery, but it
  is a feature, not a token).
- **Feasibility note.** Dawn's risk is not fraud, it is
  tenderness: every softening must be re-proven against the
  3:30 AM test — half-asleep, phone across the room's return,
  motor unknown. Soft that reads as absent fails the app.

## §4 NOIR — wet-black night city, neon signage

*Grammar: signs in rain. Bright, electric, dangerous — light
that hums, but only in flashes; this app has no loops.*

- **Haptics.** Sign-flicker reserved for warnings only: two
  4 ms strikes 12 ms apart (a failing sign), never decorative.
  House events stay single and hard — neon is either on or
  off. The slide's drag gets stepped ticks (segments, not a
  hum). LAWFUL.
- **Sound.** Bright transients: fundamentals high, the synth's
  4.7× partial (weight 2.2) already gives the neon edge when
  the chord ratio pushes it forward; durations short. A
  continuous neon buzz or rain bed is **FRAUD** — ambient
  loops are banned by the engine charter and his Oct 28
  verdict; Noir's rain must fall as discrete events or not
  at all.
- **Motion.** Quick-attack curves (A family); the seal's
  vertigo stands. Noir's own wish — the magenta bloom
  flaring once at the seal — is LAWFUL and cheap: it is
  light, not sound; the Throw is untouched; one flare rides
  the existing 1050 ms shot. The wheel, however, does not
  flicker: sign grammar yields to the integrator, always.
- **Atmosphere.** Rain on hot asphalt, ozone, a city that
  never lets you in. Specified: highest sound brightness of
  the six, event density low but every event lit. At 3:30 AM
  this is the face that matches the hour — its feel claim
  and its visual claim are the same claim, which is rare.
- **Feasibility note.** No fraud in the landed letter; the
  fraud watch is on whatever the presentation spec adds —
  any ambient city sound, any animated "hum", dies on sight.

## §5 TEMPLE — carved stone, brass, engraved hairlines

*Grammar: weight and ritual. The materials are literal: this
machinery was already speaking Temple before Temple existed —
a seat knock priced by mass, a two-stage latch, a heavy wheel.*

- **Haptics.** The richest lawful grammar of the six. Seat
  knock at the floor of its range: pitch toward 160–220 Hz,
  duration 24–32 ms, full amplitude — the pawl as a bronze
  bolt dropping. Latch: engaged stays two-stage; released
  lands HEAVIER than engaged (the shipped shapes already lean
  this way) — a temple door does not click open, it is
  unbarred. Detents become sparse and deep (a bead of stone
  in a groove): fewer, lower, never chatty. LAWFUL.
- **Sound.** Low fundamentals, chord ratio 1.5 (open,
  bronze), gains honest. The fraud line: a long temple-bell
  decay is **FRAUD via the queue** — the decay constants are
  fixed and the queue buffer caps at 160 ms; beyond ~150 ms
  of audible tail the synth cannot ring, whatever the spec
  says. Temple's honest resonance is MASS, not duration:
  pitch + knock + the room the ear imagines from the Throw
  (whose two-comb room is already, literally, a stone
  space — 1310/1372-sample combs at −46 dB ≈ 900 ms).
- **Motion.** The slowest house: Curve C and the 520 ms spec
  as defaults; the press dip becomes a press (90 ms dip is a
  floor — Temple may deepen the dip's AMPLITUDE of travel,
  not its time; the press answers on DOWN or it lies).
  The wheel needs no amendment: ζ=0.38's two overshoots in a
  heavy coast already read as a great dial settling. LAWFUL.
- **Atmosphere.** Cool stone, lamp oil, dust in still air —
  the most completely cross-sensory world of the six: its
  smell restates exactly (lowest fundamentals, longest
  lawful durations, sparsest events, heaviest knocks).
- **Feasibility note.** Temple is the only direction whose
  feel sheet required NO priced machinery beyond call-site
  parameters. Its grammar and the engine's parameter space
  are the same shape.

## §6 BLUEPRINT — deep indigo schematic, white line-work

*Grammar: specification. Everything measured, everything
labelled — feel as metrology.*

- **Haptics.** Uniform ticks, flat amplitude: a spec does
  not swell. Every detent identical (the same 7 ms shape at
  every tooth), every latch at one weight. The discipline
  is the identity: if two events differ, the drawing says
  why. LAWFUL — and the cheapest sheet to build.
- **Sound.** The pitch ladder becomes law: the VM already
  pitches one detent family by value (480 + value×2.5);
  Blueprint generalises it — pitch = f(value) everywhere,
  so the ear can read the dial blind. Fundamentals mid-high,
  chord ratio 1.0, durations uniform. LAWFUL. Blueprint is
  the only world where MORE detent sound is grammar: at
  3°/120 teeth, every tooth speaking is the schematic
  reading itself aloud.
- **Motion.** Near-linear easings (Curve A is the closest
  lawful resident), durations from the small end of the
  vocabulary; nothing anticipates, nothing overshoots in
  chrome. The wheel's two overshoots survive (shared
  machinery) and Blueprint prices that honestly as the one
  un-schematic motion in its world. LAWFUL.
- **Atmosphere.** A drafting room: paper, graphite, indigo
  ink. Specified: uniform event character, pitch carrying
  data, silence between measurements. Its "smell" is the
  driest of the six — intentionally; precision is its
  perfume.
- **Feasibility note.** Zero fraud risk, one feel risk:
  monotony. A world where every event is identical must
  earn attention through information (the ladder), not
  variation — if the ladder is cut in judging, Blueprint's
  feel collapses to a metronome.

---

## §7 CROSS-DIRECTION VERDICT

**Richest feel-carrier: TEMPLE.** Not because stone is
prettiest, but because its grammar and the engine's
parameter space coincide: the seat knock's mass pricing,
the two-stage latch, the heavy integrator, the Throw's
stone-room combs — Temple spends the machinery's full
vocabulary without one priced amendment or one fraud.
Obsidian is second (restraint that degrades most gracefully
on cheap hardware). Dawn owns the wave's one atmosphere
innovation (the clock-keyed sky). Noir's feel and look are
one claim, honestly priced. Blueprint is the most legible.
Signal is the weakest feel-carrier — a property of its
grammar, which forbids most of what feel is made of.

This verdict ranks FEEL only. The pick is Sarthak's eye,
over whole designs; a world can win on sight and take its
feel sheet from this file as its build spec.

**THE ONE FEEL LAW, imposed on whichever world wins:**
*Shape by time; colour by amplitude.* Every haptic and
every motion must carry its meaning in its timing pattern
— segment lengths, gaps, order, duration — because timing
is the only channel every phone in the fleet renders
faithfully. Amplitude, pitch-shading and envelope swells
may colour a meaning, never carry it. Any feel that reads
wrong on a motor with no amplitude control is not a feel;
it is a rendering bug filed as design. (Above this law
stands the older one, unamended: the Throw is never
re-voiced. Worlds differ in the approach, never at the
seal.)

— Haut, Oct 29, 2026, ~20:27 IST
