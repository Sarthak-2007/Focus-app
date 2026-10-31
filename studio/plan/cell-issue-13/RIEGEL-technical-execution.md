# ISSUE #13 — TECHNICAL EXECUTION (Riegel)

**Seat:** Riegel, technical-execution specialist, dedicated-100 programme.
**Scope:** the machinery UNDER the Session Room merge — the tremor sampler
and settle machine, keep-awake surrender, and the shield/accessibility
substrate the room stands on. Paper evidence only (file + line, issue-13
branch tip, CI verdict as reported in the wave-2 addendum); nobody compiled.
**Posture:** synchronous — this file lands while the cell works, not after.

---

## 1. THE SAMPLER — what "~50 Hz" actually buys, and what it silently assumes

**Technique (as built).** `MonolithViewModel.updateTremorSampler`
registers a `TYPE_ACCELEROMETER` listener at `SENSOR_DELAY_GAME` only
while the settle machine says the session is armed-undecided
(`DeskClockSettleMachine.sensorSamplingActive`). Each sample: vector
magnitude → two one-pole low-passes (α = 0.36 slow, α = 0.58 fast) →
band signal = fast − slow → running variance (EMA β = 0.05) → verdict
from `deskClockTremorVerdict(energy, TREMOR_FLOOR_UNCALIBRATED =
0.0004)`, ceiling ratio 1.5×. Poles seed from the first sample (C-17
cure: no birth HELD). Unregister resets all estimator state; `onCleared`
unregisters (leak cured). HELD persists 30 s → duty cycle 6 s on /
24 s off (`HELD_DUTY_*`). The VM's ~1 s ticker reads the verdict.

**The deepest technical fact in this merge: the filter constants are
per-sample, but the sample rate is a hint.** `SENSOR_DELAY_GAME` asks
for ~20 ms; Android guarantees nothing. Shipping devices deliver
anywhere from ~50 Hz to 200+ Hz at GAME delay, and some OEM stacks
throttle or batch differently. The pole coefficients (0.36 / 0.58) and
the variance EMA (0.05) are fixed per sample, so the filter's actual
cutoff frequencies scale linearly with the delivered rate:

- At the assumed ~50 Hz: poles ≈ the intended sub-7 Hz / sub-14 Hz
  split; band ≈ 7–14 Hz. The design's claim holds.
- At 100 Hz (common): the same coefficients put the band at ≈ 14–28 Hz.
  Physiological tremor (8–12 Hz) now falls in the *slow* pole's range
  and is subtracted out of the band signal → a held phone reads closer
  to furniture → **false REST → the clock seizes a held phone** — the
  exact seizure V1-as-amended exists to prevent, caused by arithmetic,
  not by the floor constant.
- At 25 Hz (throttled stacks): band ≈ 3.5–7 Hz; hand tremor is above
  the fast pole and attenuated; desk vibrations (footsteps, a fan on
  the same table, ~5–20 Hz content) leak *into* the band →
  false HELD → the clock never raises on a real desk.

Nobody in the merge measures the delivered rate. The cure is standard
and cheap: compute α from measured dt (`event.timestamp` deltas,
α = 1 − exp(−2πfc·dt)) or resample to a fixed 50 Hz grid before the
poles. Priced: ~30–40 LOC in the listener + tests driven at synthetic
25/50/100 Hz streams (the estimator must be extracted pure to be
testable at all — today it lives inside an anonymous listener in the
VM, which is also why no unit test can convict it). Filed as R-13-1.
Until it lands, every sentence about "the 7–14 Hz band" is a sentence
about one assumed phone.

**Battery price — honest version.** The accelerometer is the cheapest
sensor on the device and, registered without batching parameters
(`registerListener(listener, sensor, delay)` — no
`maxReportLatencyUs`), every sample wakes the application processor:
~50 wakeups/s while armed-undecided. The duty law is what makes this
defensible: continuous sampling only until decision (REST decides in
2 ticker beats ≈ 2 s of sustained verdict; HELD earns the 20%-duty
cycle after 30 s; decided sessions sample nothing). Expected real
cost: seconds-to-minutes of 50 Hz sampling per session arm — small.
The bad case is a session that stays armed-undecided for hours with
verdict UNKNOWN (bag/car): UNKNOWN never duty-cycles (only HELD
does), so a phone in a bag on a bus samples at full rate until the
20 s backstop decides the session — which it does (UNKNOWN →
backstop raise → decided → unregister). Bounded by the backstop:
~20 s + dwell. Acceptable, and now stated.

**What silently breaks it.**
- **Screen-off stall.** The accelerometer is a non-wake-up sensor on
  most devices: with the screen off and the AP suspended, delivery
  stalls; the verdict freezes at its last value. A frozen HELD keeps
  suppressing (safe direction — no seizure); a frozen REST is mostly
  harmless because REST needs 2 *ticker beats* and the ticker is also
  stalling. The dangerous freeze would be mid-EMA energy with the
  ticker alive but the sensor dead (OEM sensor-hub quirks): energy
  decays only per-sample, so a stale high energy reads HELD forever
  at 1 s grain. Detection exists in principle (sample-count watchdog
  in `updateTremorSampler`: if registered and no sample for >2 s,
  verdict → UNKNOWN); not built. Filed as R-13-2, ~10 LOC.
- **The sampler lives in the ViewModel.** Registration is driven from
  the VM's ticker; if the process is swiped away or the Activity's VM
  is cleared mid-session, sampling stops and the machine runs on the
  UNKNOWN backstop alone — by design (V1's own text), but it means
  "the sensor law protected the raise" is only ever true while
  Monolith's UI process lives. The cold-open remainder in the wave-2
  report (first-beat raise with verdict still UNKNOWN) is the sharp
  edge of the same fact: on a cold open the machine raises *before a
  single sample exists*, so a cold-opened phone in a hand gets seized
  by §2.3's derivation, lawfully, with the sensor law never consulted.
  The report names it a ruling question; the technical note is that
  no sampling change can fix it — only delaying the cold-open raise
  by one dwell (~2 s of sampling) can, at the price of §2.3's
  "clock is the first surface" immediacy. Ren's trade to make, priced
  here: 2 s of samples ≈ the entire cost.
- **The floor is a constant wearing a calibration's clothes — and the
  code now says so** (`TREMOR_FLOOR_UNCALIBRATED`, §7 debt 9). Good.
  The remaining dishonesty risk is downstream: any summary that says
  "the sampler distinguishes held from desk" without "on an
  uncalibrated floor, at an assumed sample rate" is overclaiming.
  The panel's Nightstand Calibration (3 s at SLEEP arm, per-night
  floor + set-down signature, 7-night expiry) is the right discharge
  and I second it — with one amendment: calibration must also record
  the *delivered sample rate* that night, or R-13-1's rate problem
  quietly re-enters through the calibrated door.

## 2. HELD SUPPRESSING BOTH RAISE PATHS — verified as a machine property

Read in `DeskClockSettle.tick`, in order: (1) no-session clears all
arm/held state; (2) decided → false; (3) visible → decide silently;
(4) **HELD → restBeats = 0, held stamp set/refreshed, return false** —
this branch sits *before* both the cold-open derivation and the
backstop deadline computation, and the REST branch is unreachable
from it. So while the verdict is HELD: no sensor raise (the branch
returns), no backstop raise (the deadline code is never reached),
no cold-open raise (same). The ruling is implemented as control-flow
order, which is the strongest form — there is no flag combination to
get wrong. Two honest qualifications:

- **Suppression is only as live as the verdict.** The ticker reads a
  `@Volatile tremorVerdict` written by the sensor thread. Between
  "hand picks the phone up" and "EMA energy crosses 1.5× floor" there
  is the EMA's ~0.4 s memory (β = 0.05/sample at ~50 Hz) plus up to
  1 s of ticker grain: a phone snatched and read within ~1 s of a
  REST dwell completing can still be seized by the already-earned
  second REST beat. The 2-beat dwell (Samt F-1) makes this a ~2 s
  sustained-furniture requirement, so the seizure window needs the
  phone to have genuinely read as furniture for ~2 s first. Residual:
  real, small, correctly shaped. Not zero — say so.
- **HELD's duty cycle re-opens a 24 s blind window every 30 s** once
  HELD has persisted: during the off-phase the listener is
  *unregistered* (registration follows `sensorSamplingActive`), so a
  set-down inside the off-phase is invisible until the next 6 s
  on-window, and the estimator restarts clean (C-17 cure) — verdict
  UNKNOWN until the poles re-seed and energy accumulates (~0.4–1 s
  into the window). Worst-case set-down→raise latency ≈ 24 s (blind)
  + up to 6 s (window) + 2 beats ≈ **~32 s**, versus the 20 s backstop
  the user was promised by the pre-V1 machine. That is a fair price
  for not seizing held phones, but it should be a *stated* price:
  after HELD, the clock can be half a minute late to a desk. Filed
  as R-13-3 (documentation/print honesty, no code change proposed).

## 3. KEEP-AWAKE SURRENDER (M-4 amended / C-16) — the stamp is now honest; the grain is the ticker's

C-16's cure is mechanically sound: plug state sampled every ticker
beat through `chargerDisconnectStamp` (edge-stamping, sticky,
first-observation stamps), deadline =
max(settle, lastInteraction, lastDisconnect) + 600 s, fade flag led
by one ticker grain so the 520 ms Curve C fade can actually render
(wave-2's grain fix). Remaining technical prices:

- The surrender deadline's resolution is the ~1 s ticker: on a
  SLEEP span the face can stand up to ~1 s past the deadline. Fine.
- `chargerPlugged()` reads the sticky `ACTION_BATTERY_CHANGED` intent
  — correct and cheap; unreadable → treated as plugged (never
  surrender on a guess). Correct failure direction.
- `FLAG_KEEP_SCREEN_ON` is applied by MainActivity from the VM flow:
  keep-awake therefore exists only while that Activity is alive and
  in the foreground stack. If the user is *on another app* when the
  desk clock should stand (the clock is an overlay in this app's own
  window), keep-awake is moot — the clock isn't visible either. The
  surrender law's real domain is: Monolith foregrounded, clock
  standing, SLEEP span, on battery. Inside that domain the machinery
  is now truthful. Outside it, nothing in this merge keeps anything
  awake, and no sentence should imply otherwise.
- Battery: a kept-awake OLED face at vigil alphas for a SLEEP span is
  the single largest battery line item in the Session Room — larger
  than the sampler by orders of magnitude. The surrender law *is* the
  price control (10 min still on battery → yield). Unmeasured on
  device like everything else here; the bench owed in my #11 spec
  (batterystats over a SLEEP span) covers this too.

## 4. THE SUBSTRATE THE ROOM STANDS ON (shared with #11 — short form)

The room's session row, and therefore every derivation in the merge,
is refreshed by the shield loop and the VM ticker. Two substrate
facts bound everything above (full treatment in my #11 spec):

- **Shield loop:** FGS, 2 s grain, Room write per iteration,
  UsageStats retrospective detection. If the shield is dead or
  usage-access is revoked, rows go stale and the room derives from
  stale rows *consistently* — every surface agrees, nothing looks
  broken. The wave-2 report's DAE-3 remainder (stand-down
  discovered by the ticker, not at the hardware instant) is one
  visible tip of this.
- **Accessibility service:** the room itself doesn't depend on it,
  but session liveness emissions do (FIX-5d's bounded residual on
  #11 is the same physics). OEM process kills degrade both.

## 5. CROSS-ISSUE NOTE (found while reading #13's tree)

Issue-13's `MonolithShieldService.kt:184` still carries the
fabricated fallback name `?: "Blocked App"` that #11's wave-2 cured
on its branch (`?: foregroundBlockedPkg`). When the batch merges,
whichever shield file wins decides whether the cure survives.
Flagged here so the merge can't silently resurrect it — the merge
order should take #11's line. (One line; certain.)

## 6. FINDINGS FILED

- **R-13-1 (the real one):** filter coefficients are per-sample while
  `SENSOR_DELAY_GAME`'s delivered rate varies ~25–200 Hz by device →
  the "7–14 Hz band" is only 7–14 Hz at ~50 Hz; at 100 Hz the machine
  can seize held phones, at 25 Hz it can miss desks. Cure: dt-derived
  α (or fixed-grid resampling) + extract the estimator pure + synthetic-
  rate tests. ~30–40 LOC + tests.
- **R-13-2:** no sample-starvation watchdog — a registered-but-silent
  sensor freezes the verdict; >2 s without a sample should fall back
  to UNKNOWN (backstop alone). ~10 LOC.
- **R-13-3:** HELD duty cycle's worst-case set-down→raise latency is
  ~32 s, not the 20 s backstop figure. State the price wherever the
  backstop's 20 s is quoted. No code change.
- **R-13-4 (endorsement + amendment):** Nightstand Calibration is
  the right discharge of the uncalibrated floor — record delivered
  sample rate alongside the floor, or R-13-1 re-enters.
- **Cross-issue:** shield fallback name (§5) — merge must take
  #11's cured line.

## 7. WHAT I DID NOT VERIFY

The CI verdict in the wave-2 addendum (DeskClockFlowTest all passing,
assemble green) is taken as reported. The estimator's behaviour on
real silicon — delivered rates, hub throttling, the floor's distance
from any real nightstand — is unmeasured by anyone; §1's rate math is
deterministic given a delivered rate, but which rate Sarthak's phone
delivers is a phone fact. Nobody compiled; Ren compiles on Sarthak's
word.

— Riegel, technical execution
