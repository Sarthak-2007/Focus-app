# SAFT — Game-Feel seat, Designers cohort (coordinator: Sana)
## PLAN WAVE — THE FEEL SPEC: the four gestures that decide whether this app lives. No code.

Sarthak's order stands over this file: citations lifted, full freedom.
What binds is his settled law only — Concept = NONE · ARM ≤10s · HOLD
absolute · TRUTH once · beauty is the grading axis — plus my wave-1
budget, which stands unamended:

> **≤16ms contact response on every control · 1 mover per input ·
> overshoot ≤3% of travel or 2dp, whichever binds · scale floor ≥0.94
> on any press squash · hit-stop (a 30–60ms freeze at the moment of
> consequence) is a real technique — spend it rarely.**

This file is verdicts, not a second motion grammar. Kin owns the
grammar table; where the plan-wave numbers are wrong *for the hand*
I say so here, name my counter-number, and mail Kin the same numbers
(thread: `feel-verdicts-plan-wave`). Nothing below silently forks his
spec — every divergence is flagged **VERDICT vs PLAN** in place.

**The hit-stop ledger — the whole app's freeze budget, stated once.**
A freeze is the loudest thing a screen can do; two loud things a day
and the third is noise. Monolith may spend hit-stop in exactly four
places, and nowhere else, ever:

| # | Where | Freeze | Scope |
|---|---|---|---|
| 1 | Slide-to-arm, at commit | **40ms** | Sled only; Deck behind it stays live |
| 2 | Final sub-step strike (Tasks) | **50ms** | Tasks surface only |
| 3 | DONE landing (Tasks) | **40ms** | The falling row only |
| 4 | Bank catch, emergency hold | **40ms** | The hold bar only |

Ordinary sub-steps get **no freeze** (see §3 — this overrules the
60ms-per-strike spend currently on the table). Session completion
gets **no freeze** — its beat is stillness-after, not a stop-frame.
If a fifth freeze is ever proposed, one of these four dies to pay
for it.

---

## 1. THE SLIDE-TO-ARM — the commitment gesture

**What the hand must learn:** the first two-thirds are free, the last
third is *carrying something*, the gate is a stair edge you feel
before you cross it, and past the gate the machine takes the last
few millimetres out of your hand and seats them itself. Fast arm and
slow arm both pass — cadence is passed through, never tested.

### The numbers

| Parameter | Spec |
|---|---|
| Contact response | Sled depresses +1.5dp into slot, shadow 2.5→1dp, top lit edge α 0x66→0x33 — **≤16ms** from DOWN, inside the thumb only |
| Grab | Thumb-only, with grab offset recorded. Touch outside the thumb rect (expanded 8dp) does **not** start a drag — teleport-to-touch is dead |
| Resistance curve | Displayed fraction `d = r − 0.22·r³` (r = raw finger fraction): r 0.50→d 0.472 · r 0.80→d 0.687 · r 0.95→d 0.761 · r 1.00→d 0.78. The thumb visibly outruns the sled in the last third; the lag *is* the weight. No wall, no velocity gate — the finger always wins, the sled just gets heavy |
| Interior detents | On **displayed** d, every 10%: reduced tick, pitch rising 380→700Hz linear in d. Felt as grain thickening, never as discrete events worth counting |
| The gate | At **d = 0.80**: the last catch — full-strength detent, grip line thickens 3→5dp, sled fill steps to COMMIT orange on this frame (colour confirms position; it never leads it). Hysteresis ±0.02 so a jittering thumb on the line cannot re-fire it |
| Commit threshold | Displayed **d ≥ 0.82** on release (≈ raw r 0.97 under the curve) — see verdict below |
| Hit-stop at commit | **40ms**: sled frozen dead at end of travel, trail α steps 55→110, nothing else on the Deck moves |
| Powered seat | After the freeze: sled driven the final ≤6dp home in **90ms**, Curve A (0.2,0,0,1), with 2dp compression against the end wall in the last 20ms (thumb width −2dp, recovered over the dwell) |
| Seal dwell | Armed state must *read* armed for **≥180ms** before any Deck reconfiguration begins; full reconfiguration (wheel drains to ink, NOW line settles) staged over ≤330ms after the dwell, one beat at a time, never simultaneous. Total commit→sealed ≤560ms |
| Failed release | Critically damped return, ζ 1.0, stiffness 300, settled ≤400ms, **zero overshoot, silent** except one letting-go tick at d 0.10 on the way home. A failed slide is a non-event and must feel like one |
| Whole gesture | Grab→sealed ≈1.2–2.5s typical. ARM budget spent: ≤2.5s of 10s |

### Frame-by-frame at the moment of consequence (release past gate, 60fps)

- **F0 (release):** finger lifts. Sled is wherever the curve left it (~d 0.82–0.86). Nothing has committed yet — and the screen says so.
- **F0–F1:** powered seat begins; sled accelerates the last millimetres *faster than the finger was moving*. This speed-up is the tell: the machine has taken it.
- **Seat frame:** sled meets the end wall, compresses 2dp — **freeze, 40ms (≈2–3 frames held).** Trail α at 110. Grip line at 5dp. The seal sound/haptic (Ton's) fires *inside* this freeze, not before it, not after.
- **Freeze +1 frame:** compression recovers, dwell begins. The sled does not move again. It does not spring home. A sled that returns on success is a sled that un-commits.
- **+180ms:** Deck reconfigures in order — wheel arc drains, NOW line settles — while the sealed bar stays exactly as seated, for the whole session.

### VERDICT vs PLAN — two numbers I am contesting

1. **Commit threshold: 0.82 displayed, NOT ~0.65.** The synthesis
   ceiling ("latch comes to the thumb," §18) is wrong for the hand.
   At 0.65 the thumb has invested nothing; the seal lands unearned
   and the gesture degrades into a swipe — and a swipe is a gesture
   you can do *accidentally*, which is fatal for the one gesture
   whose whole meaning is deliberateness. Forgiveness belongs in
   the resistance curve (the finger always wins), not in a lowered
   bar. Zug-II holds 0.82 and is right; Fen's gate *tick* at 0.68 is
   fine as an early warning but must not become the commit line.
   My structure reconciles both: **warn at 0.68 (light tick, no
   colour), gate at 0.80 (full catch + orange), commit at 0.82.**
2. **Hit-stop: 40ms, not 32ms (my wave-1) and not zero (Kin's D3
   seat-then-sound sequence).** At 32ms the freeze is sub-perceptual
   next to a 78ms seal waveform — the sound arrives and the eye saw
   nothing stop for it. 40ms is the shortest stop that reads as
   *impact* at arm's length on a tired eye. Kin's 90ms seat + 60ms
   hold is the same total time spent as *waiting*; a freeze spends
   it as *force*. Mailed to Kin.

### The one feel-failure that makes Sarthak call it cheap

**The sled springs home after a successful arm.** One frame of
return travel and the entire gesture retroactively becomes a button
press with a long animation — the app visibly un-commits the instant
it commits, and no seal sound can buy that trust back. (Runner-up:
resistance that *fights* — any frame where the sled moves slower
because the app is testing him rather than because the load is
heavy. He will feel the difference in one morning.)

---

## 2. THE WHEEL THROW — the rare change, earned when touched

**What the hand must learn:** this wheel has mass and grease, not
ice. A flick travels, decays like something heavy losing speed in
oil, ticks *through the motion* as it passes detents, and seats
once — into a notch, with a knock — and stops dead. It is set once
in a while (the Deck idles armed-ready at the last session); when it
is touched, it must be the best-feeling object on the phone.

### The numbers

| Parameter | Spec |
|---|---|
| Contact response | Rim dot 14→15.5dp + contact shadow deepens, **≤16ms**, silent. Hub untouched (1 mover) |
| Drag | 1:1 continuous angle, **no snapping during drag**; readout steps on detent-crossing frames only — it never rolls through values the wheel cannot set |
| Detent density in the hand | 5-min detents, 8.57° apart. **Magnetic hesitation:** within ±1.0° of a detent, displayed angle leans toward it with weight 0→0.45 — at setting speed (≤90°/s) every detent is a catch you feel in the *motion*; above 90°/s the lean fades to 0 so a throw never snags. 15-min majors: same lean at 0.55 + heavier tick |
| Detent rate limit | Feel fires at most every **35ms**; crossings inside the window are drawn, not felt, never queued. A fast sweep smears; it never machine-guns after the finger stops |
| Fling decay | Release velocity ω₀ from the (now actually queried) tracker, clamped ±540°/s. Below 60°/s: no fling, straight to snap. Above: **exponential decay, ω(t) = ω₀·e^(−3.0t)** — a hard throw (540°/s) travels ≈55–65° hands-off (≈35–40 min of range) and is under 60°/s in ~730ms. Grease, not ice |
| Snap / seat | When \|ω\| < 60°/s: spring to nearest detent, **stiffness 320, ζ 0.78**, 10–90% ≤180ms, settled to ±0.5 min by **320ms**, overshoot **≤2° (¼ detent), exactly one pass, zero second oscillation**. Landing detent feel fires at the *seat frame*, not at release |
| End stops (5 / 180) | Rubber band: past the end, displayed = end + overdrag × **0.32**, hard cap **+7°**. Release inside the band: spring home 240ms. The **knock**: on first contact with either end per drag/fling — one firm, low event, distinctly *below* the detent pitch ramp (210Hz region) so both ends are identifiable blind; FLOW end softer than the 180 end. No repeated knocking while held against the stop |
| Total | Release→seated ≤**800ms** worst case (hard fling + snap). The hand is never watching a wheel it has let go of for longer than that |

### Frame-by-frame at the moment of consequence (the seat)

- **Last fling frames:** tick rate audibly/visibly decays with ω — the spacing between crossings *is* the speedometer; no other element reports speed.
- **Snap entry (\|ω\| crosses 60°/s):** motion changes character mid-flight — ballistic glide becomes a pull toward the notch. One continuous movement, two physics, no visible seam.
- **Seat frame:** angle arrives within ±0.5 min of the detent, overshoots ≤2°, comes back **once** — landing feel fires on *this* frame, shadow slaps 0.5→2dp in 30ms, readout's final value steps in at 88% ink settling to 100% over 120ms (letterpress, not animation).
- **Seat +1 frame onward:** absolute stillness. A wheel that trembles, breathes, or ticks after seating is broken, however slightly it moves.

### VERDICT vs PLAN — two numbers I am contesting

1. **Decay 3.0/s, not Kin's 2.4/s.** At 2.4 a 540°/s throw runs
   ~1.1s and crosses ~45 min hands-off — that is slot-machine
   territory: the user throws and then *spectates*. At 3.0 the
   throw is decisive and over in ~730ms; intent, travel, seat, all
   inside one attention beat. My wave-1 velocity cap stands with it:
   if a fling can cross >40 min hands-off, halve the clamp, not the
   decay.
2. **Rubber-banded ends, not Kin's dead clamp.** A machined stop in
   the hand is not a wall in the data — every real wheel gives a
   few degrees against its stop and knocks. A dead clamp at 5/180
   reads as the UI *refusing*, and refusal at the ends teaches the
   hand to distrust the middle of the range too. Rad's ×0.32 / ±7°
   band is the right give; I adopt it and add the single knock.
   Kin's "rubber is a phone-UI material" is true of *bounce* — this
   is not bounce, it is compliance, and it never oscillates.
   Mailed to Kin.

### The one feel-failure that makes Sarthak call it cheap

**A settle that rings twice.** Overshoot, return, overshoot again —
the instant the wheel wobbles like a spun coin, it stops being an
instrument and becomes a toy, and every detent it ever ticked is
retroactively a sound effect. One pass, ≤2°, dead stop — or don't
ship the fling at all; a wheel with only honest drag beats a wheel
with a lying throw.

---

## 3. TASK COMPLETION — the drawn strike and DONE's landing

**What the hand must learn:** a sub-step is a pen-stroke — frequent,
light, repeatable. The *last* sub-step is heavier but is **not** the
task finishing. DONE is the heaviest thing on the screen, and its
weight is the task's own minutes. Three different masses, never
blurred — steps-done and task-done are two facts and feel must not
merge them any more than rendering may.

*Scope note: this section is the visual/timing side only. Haptic
amplitudes and waveforms are Ton's; where a landing "knock" is
named, I am speccing its timing and its visual twin, not its
waveform.*

### 3a. Ordinary sub-step — the strike (spends NO hit-stop)

| Parameter | Spec |
|---|---|
| Contact | Row hairline darkens + numeral sinks one tone step, **≤16ms** on DOWN |
| Strike wipe | **110ms**, cubic-bezier (0.7, 0, 0.2, 1): drawn line tears left→right at 52% of text height, 1.5dp, nib-headed, tapering tail; 80% of width in first 50ms, last 20% crawls |
| Squash | Row scaleY 1.0→0.96 in 45ms anchored at baseline, spring back ζ 0.55 / stiffness 380, one overshoot to 1.015, settled ≤170ms. (0.96 respects the ≥0.94 floor with margin — this row is small; do not deepen it) |
| Settle | Struck row drops one tone step (pressed-in); next unstruck step nudges up 3dp at +35ms, 110ms — cause and consequence stay separate events |
| Cadence rule | Rapid repeats: a second tap mid-strike is buffered and fires the frame the first settles; a third coalesces to a 90ms plain strike. Corrections (un-strike) wipe reversed in 90ms, no freeze, no knock — a correction is never charged the achievement price |

### 3b. Final sub-step — the escalation (hit-stop #2: 50ms)

- **Freeze 50ms, Tasks-surface scope, at the tap** — the hand stops
  the screen, briefly, because this strike closes something.
- Strike wipe **170ms** at 1.9dp (1.25× weight), same nib geometry.
  Squash deepens to the floor: scaleY **0.94**, same spring, settled
  ≤230ms.
- Then the signature, and it is a *withholding*: the parent title is
  **not struck**. Its hairline closes left→right over **150ms**
  (0.2, 0.9, 0.25, 1) like a bracket sealing the block of steps, and
  the title block recesses one tone step — *set into* the panel,
  awaiting DONE. The eye learns: bracket = steps finished; strike =
  task finished; there is still one heavier thing left, and the
  hand knows where it lives.

### 3c. DONE — the landing (hit-stop #3: 40ms, row-local)

Weight scales with the task's own minutes — **visual side**:

| Task minutes | Fall duration | Landing compression | Settle |
|---|---|---|---|
| 15m | **260ms** | Done-stack compresses 1dp | Single settle |
| 25m | **320ms** | 2dp | Single settle |
| 45m | **400ms** | 2dp + row scaleY dip to 0.97 at contact | Single settle |
| 90m | **480ms** | 3dp | **Double settle:** second 1dp compression at landing +90ms — heavy things land twice: body, then settle |

- **Sequence:** DOWN ack ≤16ms (DONE word sinks one tone). Row-local
  **freeze 40ms** — the row stops, the screen doesn't; a task ending
  stops *itself*, not the world. Title strikes (**140ms**, full nib)
  *concurrent with the first 4dp of fall* — the row dies falling,
  not before it. Fall is **ease-in only** (0.5, 0, 0.9, 0.4), gravity
  curve, no rotation ever. Landing frame: compression per table,
  and **only on this frame** does the header count change — count
  and row land together or the screen holds two truths. Close-up:
  rows below rise on spring ζ 0.62 / stiffness 300 (~320ms) starting
  at the landing frame — the queue closes because the weight left.
- If the dropped task was ACTIVE: the next task does **not**
  auto-promote during the drop. Promotion is its own event at
  close-up settle +120ms. Two events, sequenced, or neither reads.

### VERDICT vs PLAN — two numbers I am contesting

1. **Ordinary sub-steps: freeze 0ms, not 60ms.** Luz's buffered
   strike spends 60ms ~30 times a day. Feel arithmetic: a freeze's
   value is its rarity — at 30 spends a day it stops reading as
   impact by day two and starts reading as *lag*, taxing the exact
   screen (Tasks, the daily act) that retention lives on. The
   strike's 110ms tear + 0.96 squash already carries impact at
   frequency. Spend the freeze only where the day spends it once:
   the final step (50ms — I trim Luz's 110ms; at 110 the screen
   feels *held*, not hit) and DONE's landing. This is my own
   wave-1 budget ("spend it rarely") enforced against a good
   design that spends it often. Luz gets the wall note; the
   strike mechanics themselves are hers and they stand.
2. **DONE's freeze: row-local 40ms, scaled landing — not a flat
   80ms.** A flat freeze charges a 15-minute task the same stop as
   a 90-minute one, which flattens the very weight-scaling the
   landing exists to express. Keep the freeze short and constant;
   put the minutes in the *fall and the compression*, where mass
   actually lives.

### The one feel-failure that makes Sarthak call it cheap

**An ease-out fall.** A completed row that decelerates into its
resting place — sliding gently to a stop like a notification
dismissing itself — is the single most fake curve in UI, and on
this screen it inverts the meaning: DONE should feel like weight
being *set down*, accelerating under gravity and stopping because
it hit something. Runner-up, equal and opposite: striking the
parent title at the final sub-step — completion language spent on
an act that didn't complete the task. He'd feel both as the app
crying wolf, and he'd be right.

---

## 4. THE BANKED EMERGENCY HOLD — pressure, not a progress bar

**What the hand must learn:** holding is *lifting weight into a
rack*. Every 15s, the weight he's carrying gets set down onto a
bank — heavier, lower, permanent. What he's still carrying is only
the tail since the last bank. A slip doesn't drop the barbell; it
lowers what he's carrying, visibly, at a speed he can chase and
catch — down to the rack, where it *catches* and holds. Draining,
not failing. Failing is a word this control never says.

### The numbers — accumulation

| Parameter | Spec |
|---|---|
| Contact | Full-width bar (≥112dp tall active surface) depresses scaleY 1→0.98 in 60ms. **Accrual starts on the contact frame**, never after any expansion animation — animation may not eat his seconds |
| Fill | Direct-drive 1:1 with monotonic held time, **no easing anywhere on this control** — an eased timer is a lying timer. Leading edge carries the single marker line; remaining is read from position |
| Pressure in the body of the bar | Bar compression deepens with the *unbanked tail*, not total progress: scaleY 0.98→**0.96** across each 15s segment, **resetting to 0.975 at each bank** — the strain visibly *unloads* when a bank lands, then builds again. Pressure you can see being set down |
| Segment ticks | Per-second tick, pitch climbing **within** each segment (420→660Hz across its 15s — climbing toward the next bolt), resetting at each bank. The ear hears segments, not a minute |
| Bank events (15 / 30 / 45) | The chamber **solidifies**: the earned segment's fill steps from live accent α180 to solid control-fill with its numeral stamped (scale 1.15→1.0, 140ms Curve A, once) and the bar gives a **2dp step-down** under the thumb — a pedal passing a detent. Each bank heavier and lower than the last (560→470→390Hz family — Ton owns waveforms; the *direction* is the feel law: weight accumulates downward in pitch, never up into excitement) |
| Final segment (45→60) | Tick pitch climb steepens; last 5 seconds: one tick per second at rising weight, chamber walls brighten one step per second. No countdown theatrics before the final minute matters — there is no final minute; there are four racks and a door |
| Completion (60) | **No freeze.** All four chambers hold banked-style for 180ms, then the disarm seal fires and the overlay enters. Banking alone never spends the weekly break — only this frame does — and the bar's rendering must never suggest otherwise |

### The numbers — the slip (where this control lives or dies)

| Phase | Spec |
|---|---|
| Grace beat | **600ms of nothing.** On release/slip: no drain, no motion, no sound. Tremor, knuckle-shift, re-grip — none of it is a decision and none of it is taxed. Re-grip inside grace resumes from the exact displayed value |
| Sensor freeze | Contact-loss gaps <150ms don't even start the grace clock — accrual and drain both pause; frozen time is never counted as held time |
| Drain | After grace, the **unbanked tail only** drains at **2 banked-seconds per real second**, linear — max tail 14.9s drains in ~7.5s: long enough to chase, short enough to cost. Visual: the tail fill renders as the app's only **hatch** (4dp diagonal stripes, accent/transparent) retreating right-to-left — draining is unmistakable at a glance and cannot be confused with progress. A descending feel-stream runs under it (ticks every 250ms, falling) so the loss is felt eyes-off; re-grip is a hand decision, not an eye decision |
| The catch (hit-stop #4) | Drain stops **dead** at the bank floor — and on that frame: **40ms freeze of the bar**, hatch snaps back to solid banked rendering, one latch catch. The floor is *felt*, not watched. Below the floor there is nothing to lose; the bar sits at `BANKED 30` and waits, inert, for the rest of the session |
| Re-grip mid-drain | Drain halts on that frame; 250ms re-seat (no accrual) so a panicked slap doesn't mint time; then accrual resumes from the drained value. The number still owed only ever moves for held, banked, or drained time — by construction, never by animation |

### Frame-by-frame at the moment of consequence (the bank, t = 15s)

- **F−1:** tail fill 1 frame short of the chamber wall; bar at full segment strain (scaleY ~0.96); tick pitch at the segment's top.
- **Bank frame:** fill crosses the wall — chamber solidifies in the same frame, numeral stamps, bar steps down 2dp and strain resets (scaleY →0.975 over 120ms). The hand feels the load *leave* it.
- **Bank +1 frame onward:** new segment begins from the rack: near-empty chamber, low tick pitch, light strain. The bar now visibly holds one solid block and a thin live tail — safe vs at-risk, readable with the numbers covered.

And at the slip: grace frames show *nothing happening* — that
stillness is the forgiveness, rendered honestly — then hatch, retreat,
and the catch's 40ms stop at the floor. Three textures (still /
hatching / solid) for three truths (paused / leaving / kept).

### VERDICT vs PLAN — two numbers I am contesting

1. **Drain 2× after a 600ms grace — not Zug-II's 3× after 150ms.**
   At 3×, a 14.9s tail is gone in ~5s; combined with only a sensor-
   freeze before it, a shaky 3:30 AM hand experiences the drain as
   *theft*: by the time the eye registers the hatch, a third of the
   tail is gone and re-grip feels futile — so he won't re-grip,
   he'll abandon, and the control will have selected on grip
   endurance exactly as the banked law was written to stop doing.
   Rosa's 800ms grace + 2× is the right shape; I trim grace to
   **600ms** (800ms of dead stillness after a *deliberate* release
   reads as the control not noticing — 600ms is the longest pause
   that still feels like the machine holding its breath *with*
   him) and hold drain at **2×**. The knob is named for Sarthak:
   if a slip should cost more, turn the multiplier — but turn it
   knowing the chase window is the feel, not a rounding error.
2. **Strain resets per segment, not across the minute.** My wave-1
   spec compressed the bar linearly across 60s. Under banking that
   lies: at 31s the bar would strain as if 31s were at risk when
   only 1s is. Compression must track the *tail* — the only part
   of the hold that can be lost — or the body's main pressure
   signal is reporting the wrong number. TRUTH once applies to
   strain too.

### The one feel-failure that makes Sarthak call it cheap

**A drain he can't chase.** If the tail vanishes faster than a
tired hand can find the bar again — or worse, if any frame of the
drain *rounds* (the fill jumping to the floor, the numeral skipping
seconds it didn't show leaving) — the banked promise collapses into
the old continuous-or-zero valve wearing segments as decoration.
He held 47 honest seconds; the control must let him *watch all 17
unbanked seconds leave*, one by one, and feel the catch at 30.
The moment the loss isn't watchable, it isn't a drain — it's a
reset with better art direction, and he will know in one slip.

---

## 5. Global guards (apply to all four gestures)

- **Contact audit:** every DOWN in this file answers ≤16ms. Any
  gesture whose first motion waits for UP is broken by definition,
  regardless of how good the UP animation is.
- **1 mover, audited per gesture:** slide → sled; wheel → rim;
  strike → the row; DONE → the falling row; hold → the bar. A
  second moving element in any of these windows is slop and gets
  cut, not tuned.
- **No ceremony tax:** nothing in this file adds a frame to the
  daily path (open → preset Deck → slide → sealed). Freezes total
  40ms on that path, once, at the moment the path exists for.
- **Reduced motion:** freezes and the strike survive (they are
  timing, not travel); squash, magnetic lean, and rubber-band are
  the first things cut, in that order. The seal, the seat, and the
  catch are never cut — they are the meaning, not the decoration.
- **Measure it:** 120fps recordings, contact-to-first-frame,
  seat-frame vs feel-frame offset ≤1 frame, spring 10–90%/settle
  verified against the tables above. Game feel that isn't
  frame-checked is a render of game feel — the disease this seat
  exists to cure.

**Inbox:** empty at start; one letter from Kin arrived before
finish (thread `motion-grammar-seat-frame`) — read, filed to
`read/`, replied in his inbox: arrival-frame seat definition
accepted; resistance curve defended as rendered weight, not a
strain wall; one open delta left to the merge — valve grace
800ms (Kin/Rosa) vs 600ms (mine). **Mail sent:** Kin
(Designers, motion) — thread `feel-verdicts-plan-wave`, the four
contested numbers above with counter-numbers, plus the reply. **Wall:** one signed
entry appended — the hit-stop ledger.

— Saft, Game-Feel, Designers cohort
