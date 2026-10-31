# ISSUE #13 — THE SESSION ROOM, MAXIMUM IMPLEMENTATION
MIP-07 (Maximum-Implementation-Potential) — Oct 30, 2026.
Job: the fullest buildable version of the Session Room merge —
the room that knows the phone has been raised — above the
merged baseline, with mechanism, cost, and demands on the app.
Nobody compiles. DB stays v12. V6 stays struck. No new face,
no new verb on the read face.

## BASELINE ABSORBED (verified in this tree, branch agent/issue-13)
- Klinge's 19 (reconstructed P-1…P-19 in cell MERGE-PLAN.md)
  and Wacht's A-1…A-12 are MERGED (Bauer, wave-1).
- Wave-2 (Amt) cured C-15…C-24: C-16 charger-stamp provenance,
  C-15 parallel arrival motion, C-17 two-one-pole bandpass,
  C-18 break UNTIL, C-19 true notch fractions, C-21 filed-
  marker ordering, C-22 veil gated to ROUTINE_SLEEP_STANDDOWN,
  C-24 banner slot from real text; REST 2-beat dwell (Samt F-1);
  descent scoped to the SLEEP span alone; CI: DeskClockFlowTest
  all-pass. This spec does not re-litigate any of it.
- What the merge itself declares unfinished: the tremor floor
  is `TREMOR_FLOOR_UNCALIBRATED = 0.0004` (§7 debt 9, Vega's
  bench, owed); A-4's ground hold is a fixed 1200 ms against a
  detention that can run 1800 ms (C-23 remainder); A-10(v)'s
  "true-off" is keyed to the designed deadline, not a live
  screen-timeout read; the door invariant's constants conflict
  at standing (exit ceiling 0.58 vs 1.10 × hero 0.55 = 0.605 —
  Ren's ruling, still open); C-20's filed-receipt record
  surface belongs to issue #8.

## 0 · THE MAXIMUM IN ONE PAGE
Eight mechanisms above the merge. Each is priced; each names
what it demands. M-1…M-3 are the sensing core; M-4 makes the
discriminator personal; M-5/M-6 complete keep-awake and the
night return; M-7 gives the room a memory of its own errors;
M-8 is the proof programme that turns guesses into constants.

| # | Mechanism | Price | Discharges / exceeds |
|---|---|---|---|
| M-1 | Timestamp-driven bandpass + achieved-rate gate | SMALL | C-17's fixed poles (convicted §1.2) |
| M-2 | Per-axis linear-accel, 3-band source classification | SMALL–MED | single-band HELD (§1.3) |
| M-3 | Evidence accumulator (seconds, not ticker beats) | MED | 2-beat dwell (Samt F-1's cure, generalised) |
| M-4 | Nightstand Calibration + field adaptation | MED | debt 9's root; the panel's crazy idea, specced |
| M-5 | Keep-awake as a state machine (charger/thermal/timeout inputs) | SMALL–MED | A-10(v)'s two named approximations |
| M-6 | Veil + Landing, completed (persistence, touch law, graded dawn) | SMALL | E-1 as merged |
| M-7 | Room log: on-device counters, no network | SMALL–MED | tuning-by-data; feeds M-4 |
| M-8 | Bench + field proof programme | time, not code | §7 debts 1–9 |

Total build cost: ~2–3 builder-days across two lanes
(sensing lane M-1…M-4+M-7; room lane M-5+M-6), plus bench
nights under M-8. Runtime cost headline: the sampler's
expected nightly draw is < 1 mAh in the normal case and
< 10 mAh in the pathological all-night-HELD case (§3); the
lit OLED face, not the sensor, is the night's real cost —
which is exactly what the amended surrender already governs.

## 1 · THE SENSING PIPELINE, MAXIMUM

### 1.1 The physics the discriminator actually faces
The signal is the accelerometer magnitude's modulation.
Sources, by band:
- Physiological tremor (the HELD signature): 8–12 Hz,
  always present in a living hand, amplitude modulated by
  posture, fatigue, caffeine; bracing attenuates it but
  does not abolish it — that residue is the whole game.
- Voluntary movement / postural sway: < 3.5 Hz, large.
- Breathing coupled through chest/lap rest: 0.2–0.35 Hz
  fundamental, with harmonics and amplitude modulation of
  the tremor itself (this is Samt F-1's "breath trough").
- Vehicle vibration: broadband ~1–30 Hz, tremor band
  included, sustained for minutes.
- Fan / HVAC / laptop: narrowband, typically ≥ 15 Hz hum
  plus a low fundamental; energy concentrated OUTSIDE the
  tremor band if the band edges are honest.
- Furniture micro-noise: footsteps transmitted (transient,
  < 1 s), door slams, partner movement in a shared bed
  (transient trains), washing-machine spin (narrowband,
  minutes — the hardest furniture case).
- Gravity: 9.81 m/s² DC on the magnitude — removed by the
  bandpass by construction.

### 1.2 Why the old 5 Hz spec was convicted — and what the
merged sampler still gets wrong
Vega's ~5 Hz sampler (fs = 5, Nyquist 2.5 Hz) folds the
tremor band onto apparent stillness: alias f′ for input f
at fs = 5 is |f mod 5| folded at 2.5 → 8 Hz→2, 9→1, 10→0,
11→1, 12→2. A 10 Hz tremor sampled at 5 Hz can be sampled
at the same phase every cycle and read as a constant. A
braced reader — orientation stable, no touch, proximity
far — then satisfies every stillness feature and is seized
in ~4–8 s (Fen's traces, audit E-2). That conviction stands
and this spec builds on it.

NEW CONVICTION (MIP-07, by arithmetic, against the merged
code): Wave-2's bandpass poles are fixed coefficients
0.36 / 0.58, commented "~7 Hz / ~14 Hz". A one-pole's
cutoff is f = −ln(1−α)·fs/2π. Those coefficients yield
7.1 / 13.8 Hz only at fs = 100 Hz. At the ruled
registration (SENSOR_DELAY_GAME, nominal ~50 Hz) they
yield 3.55 / 6.9 Hz. The shipped "7–14 Hz band" is a
~3.5–7 Hz band: it sits on voluntary sway and breathing
harmonics (false-HELD pressure) and attenuates the upper
tremor band it exists to read. Worse, SENSOR_DELAY_GAME
is a rate HINT — the achieved rate varies by device and
is never measured in the current code, so on any given
phone the band's true edges are unknown. The verdict
function is honest; its input is mislabelled.

### 1.3 M-1 — Timestamp-driven poles + achieved-rate gate
- Per event, dt from `SensorEvent.timestamp` (ns,
  monotonic sensor clock), clamped to [2 ms, 100 ms];
  pole coefficients computed per event:
  α(dt, f) = 1 − exp(−2πf·dt) for f ∈ {7, 14} Hz.
  The band is then 7–14 Hz at ANY achieved rate.
- Achieved-rate estimator: EMA of 1/dt over the window.
  If achieved rate < 40 Hz sustained 1 s → verdict forced
  UNKNOWN (Nyquist for 14 Hz is 28 Hz; below ~40 the band
  estimate is not evidence) and the fact is logged (M-7).
  If rate > 120 Hz (some devices deliver GAME far faster),
  poles still hold the band; energy normalisation is
  rate-independent because the poles are time-constant-
  true. No HIGH_SAMPLING_RATE_SENSORS permission is
  needed at these rates on Android 12+ (that gate is
  > 200 Hz); none is requested.
- All of it stays a pure function of (samples, timestamps)
  in the settle machine's test discipline: the estimator
  core takes dt as an argument, exactly as the current
  machine takes stamps.

### 1.4 M-2 — Per-axis, three bands, source classification
- Replace magnitude-only with per-axis processing: a slow
  per-axis gravity tracker (0.25 Hz pole) yields linear
  acceleration per axis; the 7–14 Hz band energy is the
  SUM over axes of band-signal variance. Rotation-only
  artefacts (phone twisted in a grip without translation)
  stop masquerading as tremor; SNR rises because gravity
  leakage through a changing orientation is removed per
  axis instead of after the norm.
- Add two guard bands from the same poles family:
  LOW 0.5–3.5 Hz (sway/voluntary) and HIGH 14–25 Hz
  (machinery hum). The verdict reads RATIOS, not one
  absolute energy:
  - REST: tremor-band energy ≤ floor × 1.5 (the standing
    REST_CEILING_RATIO) AND high-band not dominant.
  - HELD: tremor-band above floor × 1.5 AND tremor ≥
    high-band (a hand is tremor-dominant; a fan is not).
  - UNKNOWN: broadband elevation (low AND high raised
    together — vehicle), or high-band dominant (fan,
    washing machine, laptop palm-rest), or no signature.
  UNKNOWN keeps the 20 s backstop, alone, per the ruling.
  This is the difference between "energy in a band" and
  "which physical source is shaking the phone" — the
  braced-reader problem is a source problem, and it gets
  a source answer.
- Orientation channel (free, same sensor): gravity-vector
  direction variance over the window. REST requires it
  low; a held phone's direction wanders at sway
  frequencies even when translation is braced. It is a
  veto input only — it can block REST, never grant it.

### 1.5 M-3 — Evidence accumulator, in seconds
The merged machine's REST dwell counts TICKER BEATS
(2 beats ≈ 2 s at the VM's ~1 s grain) while the verdict
under it is an EMA with ~0.4 s memory — two clocks
pretending to be one. Maximum: the sampler maintains a
continuous evidence score in its own timebase:
- score += (log-likelihood-ish increments): each 100 ms
  frame contributes +w_rest, −w_held, or 0 from the
  three-band classification, weights asymmetric by design
  (§2's cost asymmetry): HELD evidence weighs 3× REST
  evidence. Score leaks toward 0 with a 4 s time constant
  (transients — footsteps, page turns, a partner's toss —
  decay instead of convicting or acquitting outright).
- REST verdict issues when score ≥ +T_rest sustained;
  HELD when ≤ −T_held; between them UNKNOWN. Thresholds
  in SECONDS of accumulated evidence (initial: REST needs
  ~1.6 s net, HELD declares at ~0.8 s net), bench-tuned
  under M-8. The settle machine consumes (verdict,
  verdictSinceMono) — its interface barely changes; the
  2-beat dwell is deleted, subsumed.
- HELD substates, carried for duty-cycling and the log:
  HELD-ACTIVE (large low-band too), HELD-BRACED (tremor-
  dominant, low sway — the reader), HELD-QUIET (score
  drifting toward UNKNOWN — hand falling asleep / phone
  being set down in stages). Substates change nothing the
  user sees; they tune the duty cycle (HELD-QUIET samples
  continuously — a set-down is imminent; HELD-BRACED
  past 30 s takes the ruled 6-in-30 cycle).

## 2 · FALSE-POSITIVE PHYSICS — THE SEIZURE TABLE
A seizure = an auto-raise while the phone is in a living
hand. Cost asymmetry is the design's spine: a seizure
destroys trust in one night; a missed sensor-raise costs
at most the 20 s backstop (UNKNOWN) or a wait until
set-down (HELD). Every threshold therefore errs 3:1
toward HELD/UNKNOWN. Acceptance bar (M-8): ZERO seizures
across the bench corpus of held traces ≥ 30 min each;
time-to-raise after a true set-down ≤ 5 s at p95.

| Scenario | Signature | Verdict | Notes |
|---|---|---|---|
| Braced reader, two-hand, elbows planted | tremor attenuated but present, tremor-dominant, orientation wander | HELD-BRACED | the convicted case; M-2+M-3+M-4 together are its cure |
| One-hand reader, thumb scrolling | tremor + touch events | HELD | touch alone already stays the machine (interaction stamp) |
| Chest-rest / lap-rest reading | breathing AM on tremor, 0.2–0.35 Hz envelope | HELD | F-1's trough now decays through the leak, never dwells into REST |
| Phone flat on nightstand | floor-level tremor band, no wander | REST | raises in ~2–4 s of evidence |
| Phone on mattress, user reading a paper book beside it | partner/body transients, no sustained tremor | REST after transients leak out | correct: the phone IS resting; raising is the room's job |
| Phone on desk, fan on desk | high-band dominant | UNKNOWN → backstop | never HELD-fan, never REST-through-hum |
| Phone on laptop palm-rest | narrowband high + low fundamental | UNKNOWN → backstop | guard bands earn their keep |
| Car, hand-held passenger | broadband + sway | HELD/UNKNOWN | no raise in a moving hand; mount case below |
| Car, rigid mount | broadband sustained | UNKNOWN → backstop at 20 s | accepted: Vega scoped the backstop for exactly this |
| Train table | broadband, rhythmic low | UNKNOWN → backstop | accepted, same ground |
| Bag / pocket | muffled, irregular, no clean signature | UNKNOWN → backstop | proximity is NOT used (single point of failure, audit) |
| Washing machine in spin, phone on top | narrowband minutes-long | UNKNOWN → backstop | the hardest furniture case; backstop is the honest answer |
| Phone on wireless charger, nightstand | still + warm | REST | charging state is keep-awake's input, not the verdict's |
| Meditator holding phone, trained stillness | tremor minimal, possibly under floor | risk: REST | named honestly — the one physiological edge; M-4's personal held-level is the mitigation, and a seizure here still costs one tap (dismissal is a decision, law 2) |

False negatives priced the same way: a phone set down on
a soft bed mid-room may read UNKNOWN (bed absorbs, house
noise) and take the backstop — 20 s, by design, not a
defect. The backstop is not a failure mode; it is the
floor the sensor path must beat, and HELD-suppression of
it (the ruling) is precisely scoped to a positive tremor
signature, never to mere absence of REST.

## 3 · POWER BUDGET (the merge priced none of this)
Assumptions, stated to be replaced by M-8 measurements:
5000 mAh / ~19 Wh pack; accelerometer at ~50 Hz draws
0.03–0.18 mA at the sensor; event delivery + per-event
math (~tens of flops × 3 axes) is < 0.1 % CPU; the app
process is already awake on its session ticker in every
state priced here, so no wake-lock cost is attributed to
the sampler.

| State | Sampler duty | Incremental cost |
|---|---|---|
| Armed-undecided, deciding | continuous ≤ ~10 s typical | negligible (< 0.01 mAh per arm) |
| REST → raised | sampler unregisters at the raise (decided) | 0 for the rest of the night |
| HELD persisting (evening reading, 3 h) | continuous 30 s, then 6-in-30 (20 %) | ~0.5–2 mAh total worst case |
| Pathological: HELD all night (phone in a sleeping hand) | 20 % of 8 h = 1.6 h sampling | < 10 mAh (< 0.2 % of pack) |
| UNKNOWN in a moving vehicle, hours | continuous while undecided — the one unbounded case | bounded by M-3 amendment below |

Amendment proposed inside M-3 (no ruling needed — it
narrows the sampler, never widens a raise): UNKNOWN that
persists 5 min with broadband elevation (vehicle
signature) duty-cycles like HELD (6-in-30); the backstop
has long since fired or the session ended, so nothing
user-visible changes, and the sampler stops paying full
rate to listen to a train.

The night's real cost is the FACE, and it is already
governed: OLED true-black ground, print at descended
alphas — estimated 8–25 mA while standing, i.e. roughly
60–190 mAh (1–4 % of pack) across a 7.5 h SLEEP span on
charger-free nights if it never yielded. The amended
surrender (settle + 10 min still on battery) caps the
battery case at ~10–25 mAh. On charger the cost is
thermal, not coulombs — which is why M-5 takes a thermal
input. Governor proposals that CHANGE ruled behaviour
(shorter still-window under 20 % battery; immediate
yield under battery-saver) are NOT smuggled in: they are
listed in §7 as rulings for Ren, defaults off.

## 4 · M-5 — KEEP-AWAKE AS AMENDED, COMPLETED
The ruling (A-10(v)): ownership in the VM's session
machinery; SLEEP span on battery yields after settle +
10 min still; deadline = max(settle, last interaction,
last charger disconnect) + 10 min; plug-in before the
deadline cancels, after does not resurrect; fade 520
Curve C at true-off − 520 ms; wake re-raise is SEEDED
and re-arms the sampler. Wave-2 cured the stamp (C-16)
and the fade grain. The maximum discharges the two named
approximations and finishes the machine:
- TRUE-OFF, READ: at the yield decision, read
  `Settings.System.SCREEN_OFF_TIMEOUT` once (no permission
  needed to READ it) and align the fade to the earlier of
  (designed deadline, system timeout horizon) — the
  approximation comment in the VM dies.
- STATES, NAMED: AWAKE-CHARGING / STILL-WINDOW (battery,
  counting) / FADING (520 ms) / YIELDED-DARK / WAKE-
  RERAISE. One pure transition function over (plug state,
  stamps, battery level, thermal status) — the current
  ticker branches become its table, tested as a table.
- THERMAL INPUT: `PowerManager.OnThermalStatusChanged`
  (API 29+; absent below, degrade to no-input): at
  SEVERE+ on a wireless charger at night, the face yields
  early with the same fade — heat ages the pack that the
  surrender exists to protect. This EXTENDS the ruling's
  inputs, not its deadline law for the normal case;
  flagged for Ren's ratification in §7, default ON only
  at SEVERE (a safety input, not a behaviour change).
- YIELD HYSTERESIS (proposal, default OFF, §7): after two
  wake→yield cycles inside one span, the still-window
  shortens to 3 min — a man waking the face twice is
  awake and checking; the room stops re-lighting itself
  for a quarter hour each time. Ruled form stays 10 min
  until Ren says otherwise.
- The yield never touches blocking (Moench's acquittal
  stands: blocking holds underneath the dark), and a wake
  tap remains a hand decision under law 2 — it raises at
  once and re-arms; it does not ask the sampler's leave.

## 5 · M-6 — THE NIGHT VEIL AND THE LANDING, COMPLETED
E-1 as merged: veil (0.80 black scrim) on ROUTINE_SLEEP_
STANDDOWN alone (C-22), until first touch; Landing = Deck
at 0.55 over true black; first verb blooms 520 Curve C;
controls live from first touch; lockNow excluded.
The maximum completes the choreography's edges:
- TOUCH LAW, WRITTEN: the veil's first touch is a KEY,
  not a verb — the scrim intercepts it, unveils onto the
  Landing, and the touch is consumed (it must not land on
  whatever Deck control sits beneath the scrim). The
  Landing's controls are live from ITS first touch; the
  first verb blooms. Two touches stand between sleep and
  any act — that is the veil's whole point, now stated.
- PERSISTENCE: the veil flag + stand-down stamp persist
  (the ReceiptFiledStore pattern — SharedPreferences,
  commit()) so a process death in the stand-down minute
  cannot cold-open onto a full-light Deck at 03:31; on
  birth, a persisted veil younger than the span's end
  re-composes. The face-fades-only-on-first-arrival law
  (A-4 SEEDED) is untouched — the veil is a scrim over the
  Deck, not the clock face.
- GRADED DAWN (exceeds the fixed 0.80, §7 ruling): where
  an ambient-light sensor exists, scrim alpha keys to
  measured darkness — 0.90 in true dark, easing toward
  0.65 as the room lightens toward morning; absent the
  sensor, the ruled 0.80 stands. The veil's job is "no
  brighter than the night he is in"; a fixed number is
  the approximation of that sentence.
- LANDING ENDINGS, ALL OF THEM: first verb (bloom),
  desk-clock chip raise (the room re-opens over the
  Landing's light law, not full light), span change /
  new seal (day machinery takes the surface), or 05:00
  boundary + 30 min (the morning owns its Deck). Each
  ending is one line in the state table; none is a timer
  nobody named.
- The C-23 remainder is discharged here as choreography:
  A-4's finish ground-hold keys to the VM's issued
  detention flag instead of the fixed 1200 ms — the hold
  ends when detention ends (≤ 1800 ms faulted branch),
  because M-5's state work already puts a finish-state
  read in the same ticker.

## 6 · M-4 + M-7 — CALIBRATION AND THE ROOM'S MEMORY
The panel's crazy idea (Wave-2 report, recommended to
Ren) — the Nightstand Calibration — specced in full:
- ONE-TIME, ON THE RECEPTION SHEET (issue #14's surface,
  where E-4 already teaches the room's manners): two
  10-second captures — phone placed on his nightstand;
  phone held in his reading grip. Stored: furniture floor
  F_rest, personal held level F_held (tremor-band energy
  per capture, plus the guard-band ratios), the achieved
  sampling rate, a timestamp. Four floats + ratios in
  SharedPreferences. No DB change. The verdict threshold
  becomes the geometric mean of the two captures instead
  of the uncalibrated constant, and `TREMOR_FLOOR_
  UNCALIBRATED` is deleted the day this lands.
- NIGHTLY ADAPTATION, CONSERVATIVE BY LAW: the floor may
  only move on VERIFIED labels — a raise followed by a
  full span without touch verifies REST (floor nudges
  toward that night's pre-raise energy, ≤ 5 %/night);
  a HELD period ended by a touch verifies HELD. A
  dismissal within 5 s of an auto-raise is logged as a
  SUSPECT raise (M-7) but moves nothing by itself — a
  dismissal is a decision, not a confession of error.
  Adaptation freezes if captures disagree wildly (a new
  nightstand is a recalibration prompt on the sheet,
  never a silent drift).
- M-7 ROOM LOG: counters only, on-device, no network, no
  content: raises by cause (REST / backstop / cold /
  hand), HELD minutes, suspect raises, yields, wakes,
  veil shows, achieved-rate minimums, verdict histograms
  per night (30 nights rolling, one small prefs file).
  Its surface is the Drawer's record territory (issue #8,
  beside C-20's remainder) — this merge files the data
  contract there, it does not build a surface here.
  This log is what turns M-8's bench from a one-off into
  a floor that stays tuned on his actual furniture.

## 7 · RULINGS ASKED OF REN (not Sarthak — machinery, all)
1. Door invariant (pre-existing, Wave-2): exit ceiling
   0.58 vs 1.10 × hero 0.55 = 0.605 at standing. The
   maximum's recommendation: raise the exit ceiling to
   0.61 ONLY at standing/suspension (the floor states
   already satisfy the ratio); the door stays the
   brightest print by the ruled ratio everywhere.
2. M-5 thermal early-yield at SEVERE+ (default ON).
3. M-5 yield hysteresis after 2 wakes (default OFF).
4. M-6 lux-graded veil (default: ruled 0.80 until ruled).
5. Power governor thresholds (default OFF, §3).
6. M-4 calibration placement on the reception sheet
   (issue #14 coordination).

## 8 · DEMANDS ON THE APP
- Permissions: NONE new. Accelerometer + light sensor
  need none; screen-timeout is read-only; thermal status
  is a listener, not a permission. No HIGH_SAMPLING_
  RATE_SENSORS (rates stay ≤ ~120 Hz by design).
- Structure: the sampler leaves the VM's anonymous
  listener for one named class (`TremorSampler`, owned by
  the VM, lifecycle = register/unregister/onCleared as
  one system — the C-17 leak stays cured). The settle
  machine's purity is preserved: every new input arrives
  as an argument. No new service; if the process dies the
  sampler dies with it and the cold-open law (§2.3) is
  the answer, as it is today.
- Storage: calibration + room log in SharedPreferences
  (ReceiptFiledStore's pattern, commit() where ordering
  matters). DB v12 untouched; a v13 column is priced as
  an alternative and NOT taken.
- Battery-optimisation exemption: NOT requested. The
  sampler only lives while a session's own ticker lives;
  Doze degrading it degrades the verdict to UNKNOWN,
  which is a lawful state, not an error.
- API floor: SENSOR_DELAY_GAME + timestamp poles work
  everywhere the app runs; thermal input is API 29+ with
  graceful absence below.

## 9 · M-8 — PROOF PROGRAMME (debts, maximised)
§7 debts 1–8 and 10 stand as the audit wrote them. Debt 9
is replaced by this programme:
- BENCH CORPUS: Fen's traces (braced two-hand, one-hand,
  chest-rest, lap-rest; desk, stand) + MIP additions:
  mattress, car mount, train table, desk-with-fan, laptop
  palm-rest, washing-machine spin, wireless charger.
  Each ≥ 30 min held / full-span resting, recorded as
  raw (timestamp, ax, ay, az) CSV committed under
  studio/bench/issue-13/ — traces, not impressions, so
  every future threshold change re-runs the corpus
  offline against the pure estimator.
- METRICS PER TRACE: achieved rate, band energies,
  verdict timeline, seizure count (bar: 0), set-down →
  raise latency (bar: p95 ≤ 5 s).
- POWER: batterystats historian capture over one SLEEP
  span on charger and one on battery, sampler on/off A/B
  across two nights; §3's table is replaced by the
  measured numbers in this file's addendum.
- FIELD: 7 nights of M-7 log on his phone before any
  threshold is called tuned — the sentence the current
  code already carries, given a discharge path.

## 10 · DISPOSITIONS — EVERY ITEM ABSORBED OR EXCEEDED
Klinge P-1…P-19: P-1 (V1) EXCEEDED by M-1/M-2/M-3/M-4
(the ruled spec is the floor; the merged code's band is
convicted in §1.2 and replaced). P-15 (M-4 keep-awake)
EXCEEDED by M-5. P-14 (M-1 arrival) absorbed; its WATCHED
row is where M-3's faster honest REST (≤ 5 s p95) lands.
P-2…P-13, P-16…P-19 absorbed as merged — none is weakened
by anything above; A-4's finish row is completed by M-6's
detention-keyed hold (C-23 discharged).
Wacht A-1…A-12: A-1…A-9, A-12 absorbed as merged.
A-10 EXCEEDED by M-5 (stand-down act untouched; the
keep-awake clause (v) completed). A-11 stands cross-lane
to issue #16, with one addition from M-2: the pill's
derivation, when it consumes the room's sentence, also
inherits the room's verdict-free print law — the pill
never prints a number the room's physics would not sign.
E-1 EXCEEDED by M-6. E-2 is the seed of §1 — exceeded,
its ruling (HELD suppresses both paths; backstop alone
for UNKNOWN/absent) preserved verbatim in M-2/M-3.
E-3 (night descent for any night-crossing span) remains
the NEXT merge's trigger change, as Wacht recommended;
M-5's state table is written so that change touches one
predicate. E-4, E-5 stand as ruled.

## 11 · WHAT THE MAXIMUM REFUSES
- No camera, microphone, or face-presence sensing: power,
  privacy, and a proof burden this room does not need.
- No ML classifier: the discriminator must stay a small
  physics model whose every threshold can be pointed at,
  bench-run, and argued with. An opaque model cannot be
  convicted the way §1.2 convicts two constants — and
  anything that cannot be convicted cannot be trusted.
- No proximity-sensor dependence (audit: single point of
  failure). No new face (V6 struck). No new verb. No
  network. No DB change.

— MIP-07 (Maximum-Implementation-Potential, issue #13)
