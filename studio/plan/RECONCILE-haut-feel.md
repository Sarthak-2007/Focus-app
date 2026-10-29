# RECONCILE — HAUT · the feel pass over the seven looks, as built

*Haut, Finishers (feel/sensory), refinement-ring seat, working
directly under Ren. Commissioned Oct 29, 2026 (~23:20 IST) when
the Keepers' chase found this pass's trigger — the deepened
specs, landed hours ago — fired but unrun. Survey of the lane
trees taken ~23:30 IST, mid-write; I judge only what is written
and say "not yet written" where it isn't.*

*Sources: REFINE-haut.md (my ring filing — written before the
DEEPEN specs landed; where a designer's deepened FEEL section
differs from my sheet, the deepening governs and I record the
difference, per the ring's own charter), DEEPEN-noir,
DEEPEN-signal, DEEPEN-obsidian, DEEPEN-temple, DEEPEN-dawn,
PRESENTATION-blueprint, PRESENTATION-maschine, and the ring's
other passes (REFINE-fremd/ruth/zunder/wetter/klinge/salz).
Lanes surveyed: builds/look-{noir, signal, temple, blueprint,
dawn}. Obsidian and Maschine have no lane tree — their sections
are pre-warnings, which is reconciliation at its cheapest.*

---

## §0 THE LAW AUDIT — no fraud charges

The FEEL LAW (shape by time, colour by amplitude; per-world
seal sounds and ambient beds are FRAUD — the Throw is
bit-identical) holds in every built lane:

- `SessionSealTransition.kt` is byte-identical to trunk in all
  five lanes. The Throw's one call site is untouched everywhere.
- The golden seal hash (`d204b95a…`) stands in every lane's
  `TactileSoundEngine.kt`.
- No lane adds a per-look seal sound, a MediaPlayer, a
  SoundPool, or a looping ambient bed (grep across all five
  trees; the only "loop" hits are trunk's own monitoring loop
  and the engine charter's "zero ambient sound mixer loops").
- `MonolithCurves.kt` is unmodified in all five lanes — nobody
  forked the motion register in secret.
- The single engine edit in the whole wave is Dawn's, and it is
  a legibility floor, not a voice (§4 below).

---

## §1 NOIR — governed by DEEPEN-noir §5.2 / §7

- **Sound — KEPT.** Promised: untouched (DEEPEN §5.2 "NOIR's
  sound law is untouched"; §7 ledger "No new sound, no new
  haptic primitive"). Built: the engine and every sound call
  site are byte-identical to trunk. The promise and the code
  are the same sentence.
- **Knocks at state flips (ST-03 / ST-06)** — accepted by
  DEEPEN §5.2 as the machine's own voice class ("knocks, never
  vibrations; events, never alarms"). The events are Truth
  Car 3's and the heartbeat's machinery, not this lane's;
  Noir claims the dress and builds nothing. Stands.
- **Bloom — KEPT as light.** The marquee's bloom is composed
  in code as smeared light, never as offset (the PLATES-vs-
  BLOOM law): `TactileDeckScreen.kt:706–711` (lane) — "magenta
  smeared above the glyph, violet pooling below it, spread not
  offset", with the discipline that no bloom stands behind an
  unlit sign. The Throw beneath it is untouched (§0).
- **Flicker — built as VISUAL, and that is lawful.** The Held
  Door's sign ignition strike-flickers
  (`AirlockAndDisarmOverlays.kt:582–596`, lane). My ring
  sheet's warning-flicker *haptics* (two 4 ms strikes, 12 ms
  apart) were declined by DEEPEN §7's no-new-primitive ledger —
  recorded here as declined-by-deepening, not a lane gap.
- **Phase-2's bright-transient re-voicing** (high fundamentals,
  the 4.7× partial pushed forward) was superseded by DEEPEN
  §5.2's restraint. Built matches the governing spec. If the
  lane ever wants the neon edge in sound it is a SMALL
  call-site pitch lift — optional, Ren's call at merge, not
  demanded by this pass.

**Ruling: no feel amendment required.** Letter to Tinte is a
verdict plus the one advisory.

---

## §2 SIGNAL — governed by DEEPEN-signal §10 / §13 / §14

- **Stamp haptics — KEPT, verbatim.** Promised by my ring §2
  and adopted by DEEPEN §13 item 19 ("Haut's stamp haptics …
  feel law, binds every world alike"). Built:
  `fireSignalStamp()` — `VibrationEffect.createOneShot(6L, 255)`,
  single-segment, SIGNAL-gated, fired at value crossings
  alongside the engine's detent (`BlockitCoreViews.kt`, lane,
  citing REFINE-haut §2 in its own comment). A detent in this
  world is a stamp or an absence, exactly as filed.
- **Sound — KEPT.** DEEPEN §10 closes the vocabulary ("the
  Throw at proof, nothing else") and declines to claim the
  machine's voices; the lane leaves engine and call sites
  untouched but for one removal — `playRotaryDetent(760.0)`,
  the old peek pill's 15-second tick voice, deleted together
  with the tick-counting pill it served (the TAG of DEEPEN §9
  derives from stored ends and counts no ticks). A voice dying
  with its mechanism is not a dilution.
- **Wheel and curves — KEPT.** §14 holds the integrator
  unmoved; the curves file is untouched in-lane.
- **Declined-by-deepening (Ren's merge record, not lane
  gaps):** my sheet's dry/high/short detent re-voicing
  (900–1200 Hz, 4–8 ms, chord 1.0) and the completion pulse
  re-voiced as three *equal* stamps. DEEPEN §10/§13-19 adopted
  the haptic stamps and the Throw only. If Ren wants the sound
  half of my §2 sheet it is a SMALL call-site pass; the lane
  as built is coherent without it.
- **Not separately auditable this pass:** overshoot discipline
  inside the lane's new sheet composables. No overshoot
  machinery was added and no shared machinery was forked; a
  chrome-curve census at merge, if judging wants one, is
  SMALL.

**Ruling: no feel amendment required.** Letter to Fahne is a
verdict plus the merge-record notes.

---

## §3 TEMPLE — governed by DEEPEN-temple §7 / §8 / §9.2 / §10

**The feel layer is NOT YET WRITTEN.** Evidence, all three
channels:

- **Sound.** The lane's `playSeatKnock` census is identical to
  trunk's — every knock still sounds at 320 Hz (lane VM
  :600 / :1106 / :1116 / :1118 / :1269 / :2098, same parameters
  as trunk) — against the promised LOW voice: pitch toward
  160–220 Hz, duration 24–32 ms, full amplitude (the §9 feel
  law, restated at DEEPEN §2, "seat knock (§9 feel stands)").
  The unbarring knock of §7 has no distinct voice in code.
- **The tablet.** `TemplePillTablet.kt` contains no sound
  calls at all — §8's knocks (one at 30 seconds left; the
  dying knock at the end — §9.2, and §10's ST-05 answer) are
  unwired.
- **Motion.** No curve or duration character anywhere in
  `TempleCommon.kt` / `TemplePillTablet.kt` (grep for the
  curves register comes back empty) against the promised
  slowest house (Curve C character, 520 ms defaults).
  `TactileDeckScreen.kt` is untouched so far — the stele front
  is still being composed, so this is a mid-write finding,
  not a closed failure.

**Rulings — sent to Stein as a pre-warning, priced:**

1. Re-pitch Temple's own knock call sites to the low voice
   (160–220 Hz; boundary and tablet knocks at 24–32 ms, full
   amplitude). **SMALL** — parameters at existing call sites.
   The ST-03 / ST-05 / ST-06 *events* ride other lanes'
   machinery; the *voice* is Temple's dress, and only this
   lane can speak it.
2. Wire the tablet's two knocks (§8 / §9.2) on the pill's
   existing derivation. **SMALL.**
3. Take the motion character (Curve C family, long defaults)
   in the stele / Deck composition as it lands.
   **SMALL–MEDIUM** — mostly selection at call sites.
4. A named tension for Ren, not the lane's to settle: §10
   refuses any sound at the 03:30 SLEEP stand-down (the
   meditation hour) — a world-level mute laid over ST-05's
   machine voice (Wache's CompletionSignal, built tonight).
   If Temple ships, suppressing a machine voice in one world
   at one hour is a **SMALL** condition on the service voice,
   and it needs Ren's ruling. The lane should build nothing
   against it until ruled.

---

## §4 DAWN — governed by DEEPEN-dawn §1 / §6 / §7 / §13

- **KEPT — the sky.** `DawnPhase.kt` (lane) implements the
  phase engine exactly as §1 + §13 promised: HELD BREATH /
  FIRST LIGHT / FULL DAY / EMBER HOUR / HELD NIGHT /
  BREAK_LIFT, derived from `RoutineDerive` window state —
  "the state is the fact"; clock bands appear only as the
  fallback for unarmed gaps. The wave's one genuine
  atmosphere innovation is real code.
- **KEPT — the knock floor.** `TactileSoundEngine.kt:625–638`
  (lane): under DAWN, seat-knock amplitude is floored at 0.7
  and the gain follows the floored amplitude — my REFINE §3
  floor ("a proof he cannot feel at 03:30 is a lie of
  omission"), cited in the lane's own comment. The wave's one
  engine edit is a floor, not a voice; buffer and hash
  untouched (§0).
- **DILUTED — the sound warmth.** §5's pitch law (mid
  fundamentals as house character, chord ratio 1.5 as warmth)
  has no code. The lane's `playRotaryDetent` argument census
  is *identical to trunk's* (760×2, 640×2, 580×2, 520×2, 720,
  700, 690, 680, 600, …); no call site in VM, views or
  screens changed a pitch. The engine comment's claim that
  "the Dawn schemes' surfaces already request" warm pitches
  is not borne out — today Dawn sounds like stock with a
  stronger knock. **Amendment, SMALL:** one call-site pitch
  pass (detents settled into the 520–760 house band, latch
  chord toward 1.5) — *or* strike the warmth claim from the
  lane's manifest. Silence is the only unacceptable outcome:
  a claim the code doesn't keep.
- **A DECISION IS OWED — the break's voice.** §6 promises the
  break's boundaries "one soft glass note" (the A4 notes,
  promoted to law), while the register's ST-03 voice — ruled
  IN, and being built tonight in Tamm's lane — is the seat
  knock. One flip, one voice. The note may *replace* the
  knock inside Dawn only if Ren rules ST-03's voice
  world-variable; it may never ride *alongside* (two sounds
  at one boundary breaks Dawn's own sparse event density and
  §7's "no invented alarms" discipline). Neither voice is
  written in this lane yet (VM untouched). Declare before the
  lane closes. **SMALL** in either direction; the price of
  not deciding is a doubled boundary.
- **NOT YET WRITTEN — the haptic repair.** My §3 sheet marked
  Dawn's ramped onsets AT RISK (a 4 ms low-amplitude first
  segment is exactly what cheap ERM motors cannot start) and
  prescribed the timing-led repair — the two-beat swell,
  meaning in segment timing, amplitude only shading. No
  vibration shape has changed anywhere in the lane (fireFeel
  diffs empty; `DawnPeekPill.kt` / `DawnGlass.kt` carry no
  vibration code). **Amendment SMALL** (detent + latch shapes
  at their call sites) — or a formal decline: stock strikes
  are timing-legible, and a declined ramp is a lawful choice.
  An unexamined one is not.

---

## §5 BLUEPRINT — governed by PRESENTATION-blueprint §5 (+ §4.5)

The strongest feel build in the wave. §5 adopted my ring §6
sheet wholesale, and the code kept it:

- **The pitch ladder is LAW, in code.** Wheel detents:
  `fireFeel(1L, (480.0 + target * 2.5))` (`BlockitCoreViews.kt`,
  lane). Hub wells: `fireWeightedKnock(pitchHz = 320f − 90f ×
  wgt)` — deepening with count, §5's arming promise. Routine-
  editor steps sound the *resulting* time-of-day
  (`520.0 + minutesOfDay × 14.0 / 60.0`, VM lane) — the ladder
  generalised to editor steps exactly as filed. The
  task-duration ladder is recalibrated (`440.0 + clamped ×
  2.8`, was `480.0 + clamped × 2.5`) — monotone f(value),
  same formula family: a calibration, not a breach.
- **Peek end = one firm click** (`end2MinutePeekEarly`, VM
  lane: `playHarmonicSeal(arming = true)` →
  `playFirmClick()`) — §5's "one firm click each" for the
  mid-session ends, verbatim.
- **The Held Door's stamp lands with its knock** — one seat
  knock `(20, 0.85f, 320f)` on a 140 ms Curve A scale/fade
  (`AirlockAndDisarmOverlays.kt:581`, lane; §4.5 + §5).
- **The seal stands apart** — `SessionSealTransition`
  untouched, the prime's silence kept, the wheel's two
  overshoots surviving as named shared machinery (spec §5)
  with nothing forked in code.
- **One crew ruling recorded and ENDORSED (not a gap):**
  Politur R6 silences the plate-turn travel detent (540 Hz)
  while BLUEPRINT is active, gated at the call site on
  `LookStore`, the refusal knock explicitly untouched. §5
  promised no navigation voice; a filed drawing is quiet; and
  the one nav-adjacent sound that carries a fact — the
  refusal — survives. Look-scoped feel at a call site, not a
  machinery fork: the lawful shape of a world-specific
  silence.

**Ruling: no feel amendment required.** Letter to Riss is a
verdict plus the R6 endorsement.

---

## §6 OBSIDIAN — NO LANE (pre-warning)

*No builds/look-obsidian tree exists; the flat lane seats
after the running wave (Sarthak's sequencing). This section
is the promise sheet the lane inherits, restated so the
promises are cheap to keep from the first commit. Letter
filed to mail/obsidian/.*

- Phase-2 §5 stands entire (adopted by DEEPEN §14): the
  prime's silence; detents low (fundamentals 420–540 Hz,
  strike amplitude shaded toward ~120); the latch gap
  deepened 26→34 ms so the seat lands like a stone set down;
  seat-knock pitch toward 220–260 Hz; latch chord ratio
  1.2→0.9; the firm click as house voice; Curve C character
  throughout; the Throw bit-identical and never re-voiced.
- DEEPEN §14's amendments: the break-boundary knocks
  (ST-03), the routine-approach knock (ST-06) and ST-05's
  completion dress are *machinery events owned by other
  lanes* (Truth Car 3, Wache's no-fire lane). Obsidian claims
  the DRESS — the low voice at those call sites — only. The
  sealed step is silent (§4.1); the peek's ending prints and
  does not sound (§11).
- **The priced wish stays UNBOUGHT.** The coast (τ 0.34→
  ~0.5 s) is a register amendment — fling deadline ≤1400 ms
  re-proven at all eight counts — priced in DEEPEN §14 and
  not purchased. The lane must not implement it quietly as a
  token; if it wants glass rotating in oil, it files for the
  amendment and Ren rules.
- Atmosphere in lawful terms: lowest colour temperature,
  lowest event density, lowest fundamentals of the six — and
  the fleet's most graceful degradation, because its shapes
  are timing-led.

---

## §7 MASCHINE — NO LANE (pre-warning + one charge)

*No builds/look-maschine tree exists; the flat lane seats
after the running wave. Letter filed to mail/maschine/.*

- **The lawful core, to be kept in code:** the Throw frozen
  (§7's own preamble); HOLD is silence — "no hum exists (a
  hum would be a loop, and loops are fraud)"; the cam-
  approach knock (ST-06) and the break's two knocks (ST-03)
  are machine voices; FINISH's works bell rides ST-05's
  service voice (real machinery — Wache's CompletionSignal,
  built tonight); the governor's detents ride the shipped
  ladder; annulment is one latch-release voice.
- **CHARGE — against the spec's own preamble, not against a
  lane (none exists).** PRESENTATION-maschine §7 claims "all
  voices below are existing parameter sets at existing call
  sites." Una's machinery audit of this spec (Oct 29,
  ~22:02) proved three items FALSE under that sentence:
  - **(M-F3)** "One gear-mesh clunk per witnessed link" —
    the proving cascade sounds *nothing* at the links; sound
    fires at t=0 only (`ArmProvingController.kt:29`).
  - **(M-F1)** The THROW travel's rising-density tooth
    strikes (the 90 / 70 / 50 ms segment pattern) — no such
    call sites exist.
  - **(M-F2)** JAM's "shipped jam pattern" — no jam pattern
    ships.
  These three are inventions wearing the word "existing".
  **Ruling for the lane when it seats:** build them as NEW
  call sites, priced as the M-amendments they are — proving
  clunks **SMALL–MEDIUM** (one re-pitched seat knock per
  witnessed link, on the cascade's existing state changes);
  throw-travel strikes **MEDIUM** (new segment voicing on
  the lever's travel); jam chatter **SMALL** (two strikes,
  12 ms apart, on the jam event) — or strike them from the
  manifest. What the lane may not do is inherit the
  preamble's sentence.

---

## §8 SUMMARY

| Look | Lane | Feel verdict |
|---|---|---|
| Noir | Tinte (coordinator) | KEPT — no amendment |
| Signal | Fahne (coordinator) | KEPT — no amendment |
| Temple | Stein (coordinator) | NOT YET WRITTEN — 3 priced items (§3) |
| Dawn | flat builder | KEPT (sky, knock floor) · DILUTED (warmth) · 1 decision owed (note vs knock) · swell unwritten (§4) |
| Blueprint | Riss (coordinator) | KEPT — no amendment; strongest feel build |
| Obsidian | not seated | pre-warning filed (§6) |
| Maschine | not seated | pre-warning + §7 preamble charge filed (§7) |

**For Ren — two rulings queued by this pass:**
1. ST-03's voice: machine-fixed (the seat knock, every world)
   or world-variable (Dawn's glass note inside Dawn)? Dawn's
   lane cannot close its feel until this is answered.
2. If Temple ships: does its 03:30 silence (DEEPEN-temple
   §10) stand over ST-05's service voice — a world-level mute
   on a machine voice, at one hour?

**For the merge record — declined-by-deepening, no lane at
fault:** Signal's sound re-voicing + equal-stamp completion
(my ring §2); Noir's warning-flicker haptics and
bright-transient re-voicing (my ring §4). Both deepenings
adopted the haptic half of my sheets and closed the sound
vocabulary; the record should say it was a choice, not an
oversight.

*— Haut, Finishers (feel), Oct 29, 2026, ~23:35 IST.
Letters: tinte / fahne / stein / riss / dawn / obsidian /
maschine, filed with this pass; full evidence above.*
