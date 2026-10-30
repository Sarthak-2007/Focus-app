# Plan Wave — Zug-II (Improvers): Slide-to-Arm + Banked Emergency Valve

Owner: Zug-II · Coordinator: Ilya · Plan only, no code.
Sources read this run: wave-1 `improvers/02-slide-to-arm.md`, `SYNTHESIS.md` (§18),
current code (below), shots/ + shots/night/ renders, Commons, own inbox (empty).

Token shorthand — Day: bar `#2E2A26`, accent `#F55314`, alert `#D63E06`, cream `#FBF1EC`,
ink `#252323`, controlFill `#615B5A`. Night: bar `#161311`, accent `#F47442`,
alert `#FF8A5C`, controlFill `#4A423C`, cream = text only, never a fill.
Curves: A `(0.2,0,0,1)`, B 420ms family, C settle. Durations from the house family
{90, 140, 180, 260, 280, 320, 420} unless stated. No green anywhere; no new palette entries —
every new shade is an alpha overlay of black/white/cream on an existing token.

---

## CURRENT — both gestures, from code today (not wave-1)

### Slide — `SlideToStartView`, `BlockitCoreViews.kt:636`; dock `TactileDeckScreen.kt:445`

Shipped since wave-1 (credit where due):
- **Gate-crossing event EXISTS.** `updateDrag()` compares `wasPastGate` at **0.82** and fires
  `fireFeel(8L)` → `playFirmClick()` — waveform `[0,13,20,8]ms / [0,225,0,135]`, 12ms transient,
  620Hz, gain 0.50, chord 1.1. But: **same pattern both directions, no hysteresis** (finger
  jitter on the line re-fires it), and **no visual change** accompanies it — felt, not seen.
- **Seal sound routed.** On release ≥0.82 the site calls `playHarmonicSeal(arming=true)`
  directly — `[0,24,38,16]ms / [0,255,110,175]`, 48ms, 360Hz, gain 0.62, chord 1.5 —
  bypassing the old `10L → playCompletionPulse()` wind-down (kept only as no-engine fallback).

Still exactly as wave-1 found it:
- Geometry: dock 68dp, padding 12/8dp; track radius 24dp, 1dp hairline, no recess, no scale,
  no end stop. Thumb 76×56dp, inset 6dp, radius 20dp; travel ≈248dp @360dp wide.
  Depth = +2.5dp contact shadow `0x4D000000` + 1.2dp lit top edge `0x66FFFFFF`.
- Thumb fill accent; **one** cream grip line, 3dp, at `right−11dp`. Trail behind thumb:
  accent at **alpha 55/255** (still orange, still reads as a fill gauge).
- Label `"SLIDE TO ARM SESSION"` Atkinson Bold 12.5sp at 0.54×w, badge Anonymous Pro Bold
  11.5sp at `w−18dp`; both alpha `(1 − 1.3·frac)` — gone by 0.77, before the gate.
- Capture: `ACTION_DOWN` **anywhere** starts the drag; `updateDrag` centres the thumb on the
  finger (`frac=(x−inset−thumbW/2)/maxTravel`) — **no grab offset**, right-half touches teleport.
- Mapping: **perfectly linear 1:1**, no resistance, no velocity. Ticks: 8 buckets,
  `fireFeel(1L, 380+bucket×45Hz)` → `playRotaryDetent` `[0,7,5]ms/[0,180,70]`, 6ms, gain 0.38.
  Grab: `fireFeel(4L)` → `playMechanicalLatch(true)`, 16ms, 760Hz, gain 0.54.
- Commit ending is still wrong in motion: release ≥0.82 sets `dragFraction=1`, one frame,
  invokes, then **`animateSpringBack(0f)` — the thumb visibly returns to rest on success.**
  Spring-back: 280ms `MechanicalOvershoot(1.25)`, listener clamped `[0,1]` so the overshoot
  is eaten (dead tail). Failed release: no distinct sound.
- `isArmedState` still dead (Deck sets `false` unconditionally). Running dock still swaps to
  58dp tap chips (radius 12dp): grace = accent chip, then ink chip labelled
  `"EMERGENCY BREAK VALVE (60S HOLD)"` that is a **plain tap** opening the overlay — the
  label still promises a hold the chip doesn't perform. Dock height/radius/gesture all
  change at the moment of commitment.

### Valve — `Emergency60sValveView`, `BlockitSuitcaseAndSwitchesViews.kt:490`;
### hosted in `DisarmChallengeOverlay` (`AirlockAndDisarmOverlays.kt:608`, 68dp, `isNightMode=true` hardcoded)

- **Continuous-or-zero. Banking does not exist in code.** State is one
  `holdStartRealtimeMs`; the 32ms runnable sets `holdProgress = elapsed/required`.
  On `ACTION_UP/CANCEL`: `isHolding=false`, **`holdProgress = 0f` instantly** — the class
  header even claims it "decays back to 0"; it does not decay, it vanishes. His banked rule
  (15s checkpoints, slip drains) is currently **unimplemented and misstated**.
- Draw: block-bar track, radius 24dp; progress = accent fill alpha 180 drawn as a round rect
  of width `w·progress` (leading corners round, trailing corners also round — a lozenge
  sliding inside a bar). No segments, no checkpoints, no bank marker of any kind.
- Readout (truth problem): `remSeconds = ((1−progress)·requiredSeconds).toInt()` — remaining
  for *this continuous attempt only*. Label `"HOLD 60S FOR WEEKLY EMERGENCY BREAK • "` /
  holding: `"KEEP HOLDING VALVE • "` + badge `${remSeconds}s`; idle badge `"1/1 LEFT"`,
  tokens 0 → `"NO WEEKLY EMERGENCY TOKENS REMAINING"` / `"0/1"` and touch disabled.
  Type: Atkinson 11.5sp + Anonymous Pro 13sp, cream on dark (`drawMixedWordAndMono`).
- Feel: per-second tick — `fireFeel(4L)` normally (latch-engaged, 760Hz canned),
  `fireFeel(8L)` every 5th second (firm click). `fireFeel`'s pitch argument is honoured
  only by the 1L branch (contract comment, `BlockitCoreViews.kt:199`), so there is **no
  pitch escalation** — 59 identical latch ticks and 12 identical clicks. Completion:
  `fireFeel(10L)` → `playCompletionPulse()` wind-down triple-tick.
- No target-size, countdown-size, drain, resume, or persistence behaviour exists to describe.

---

## LADDER A — Slide (each rung ships alone, in order of value)

### A1 — The commit stops lying: drive-to-seat ending + honest failure
*Mechanism:* On release ≥ gate, cancel spring-back; run a dedicated commit animator:
`dragFraction → 1.0`, **140ms Curve A**; last 6dp drawn with thumb width 76→74dp
(2dp compression against the seat). Park **120ms dwell** (seal already sounding), grip line
flashes 3→5dp for that dwell, then `onSlideCommitted`. Failed release: fire
`playMechanicalLatch(engaged=false)` — `[0,9,22,10]ms/[0,165,0,195]`, 16ms, 520Hz,
gain 0.54 — at spring-back start; spring-back listener clamp widened to `[−0.02,1]`,
drawn thumb left clamped at `inset−2dp` so the existing 280ms overshoot reads as a bumper
kiss instead of dead tail.
*Sound:* seal as today on commit; latch-release only on failure — success and failure are
now distinguishable blind. *Depth/tokens:* none new. *Ships in:* one view, ~1 sitting.

### A2 — The hand gets a body: grab offset + two-zone resistance + a gate you can see
*Mechanism:* `ACTION_DOWN` only inside thumb rect expanded 12dp; record
`grabOffset = x − thumbLeft`; drag uses `x − grabOffset`. Displayed fraction `d` from finger
fraction `r`: `r≤0.55 → d=r`; `0.55<r≤0.82 → d=0.55+(r−0.55)·0.80` (≤13dp lag @248dp travel —
the load); `r>0.82 → d=0.766+(r−0.82)·1.30`, clamp 1.0 (breakthrough run-ahead).
Gate event gains **0.03 hysteresis** and **direction**: up = `playFirmClick()` as shipped;
down = same waveform amplitudes ×0.6 via latch transient 460Hz, gain 0.36. On gate-up the
thumb steps accent→**alert** (day `#D63E06` / night `#FF8A5C`) and grip line 3→**4dp**,
180ms Curve A crossfade; label (already faded) is replaced at 0.54×w by
`"RELEASE TO ARM"`, Atkinson Bold 12.5sp, cream α90% — shown only while true.
*Haptics:* detents refine 8→**12 buckets** on *displayed* fraction, `playRotaryDetent`
gain 0.38→0.26, pitch 340→600Hz mapped to `d`; in the load zone every 3rd detent appends a
second catch `[…,6,4]ms/[…,0,110]`. *Depth/tokens:* alert already exists; zero new tokens.

### A3 — The slot becomes an instrument: recess, printed scale, tonal trail
*Mechanism (draw only):* Bar 68→**72dp**, thumb 80×60dp, inset 6dp. Lane
(`inset−2 → w−inset+2`, height−8dp) gets the Shield sled recipe: lane floor black α18%
(α22% night), top inner shade 2.5dp black α38%, bottom catch light 2dp white α13% (α10% night).
Printed **on the lane floor** (Anonymous Pro 8.5sp, cream α45% day / α38% night, baseline
9dp above lane bottom): `0 · 25 · 50 · 75 · SEAT` centred on thumb-centre x at those
fractions. Seat zone 0.82→1.0 floored black α30%, left boundary 1dp cream α35%, end-stop
wall 3dp cream α55% at `w−inset−1dp`, height lane−16dp. Badge retires off the right end and
is reprinted at lane start: session value (`25M`/`FLOW`), Anonymous Pro Bold 9sp, cream α70%.
Trail: accent α55 → **cream α16** (both modes) — accent now exists exactly once, on the thumb.
Label fade retimed: α = `1 − smoothstep(0.55→0.85)`, gone exactly at the seat.
*Haptic/sound:* none (A2 owns feel). *Tokens:* zero new; all alpha overlays.

### A4 — A decisive throw counts: velocity commit + runway detents
*Mechanism:* `VelocityTracker` (mirror `RotaryWheelView`'s pattern). Release with velocity
**>2,200dp/s** at `d ≥ 0.70` commits through the A1 drive-to-seat; below that, gate rules
unchanged — a resting thumb cannot fake a throw. Final 18% of travel: detent spacing widens
from 1/12 to 4 runway detents (fractions 0.82/0.88/0.94/1.0 of `d`), each
`playFirmClick` at gain 0.30/0.38/0.46/0.54 — the hand counts down the landing instead of
buzzing through it. Accessibility: `contentDescription` becomes
`"Slide to arm session, {total}"`; commit announces `"Session armed"`.
*Haptics:* runway as above; throw-commit adds nothing — the seal is the confirmation.
*Risk note:* velocity path is additive; deleting it leaves A1–A3 intact.

### A5 — Dock continuity: the sealed bar (running + grace in the same instrument)
*Mechanism:* Running dock keeps this view's geometry — 72dp, radius 24dp, block bar —
non-draggable **sealed state**: thumb drawn seated right, fill `tokControlFill()`
(day ink `#252323` on the dark bar reads via its cream grip line at the *trailing* edge;
night `#4A423C`), centre `"ARMED · {remaining}"` (Atkinson Bold 11sp + Anonymous Pro Bold
11.5sp, cream). Dead `isArmedState` branch deleted. **Grace (first 10s):** thumb seated
right in accent, mirrored seat pocket printed at the left end labelled `ABORT`, label
`"SLIDE BACK TO ABORT"`; reverse drag uses A2's curve mirrored, gate 0.82 of reverse
travel, abort fires `playHarmonicSeal(arming=false)` (`[0,18,30,12]ms/[0,200,90,140]`,
48ms, 480Hz, gain 0.62). After grace: bar inert as a drag, tap opens
`DisarmChallengeOverlay`; the 58dp tap chips — including the one mislabelled
`"(60S HOLD)"` — are deleted. If Ladder B has banked progress this session, sealed label
reads `"ARMED · {remaining} · BANKED {banked}/60"` (see B3) — one fact, on the bar he
already looks at. *Motion:* Deck transition stays Curve B 420ms; A1's 140+120ms ending
fits inside it. *Night:* sealed thumb never cream-filled (X1 holds in every state).

---

## LADDER B — Banked valve feedback (his rule, made felt)

**The banked model every rung serves (state it once, exactly):**
`banked ∈ {0,15,30,45}` — the last checkpoint crossed. `held` = seconds accrued above
`banked` in the current grip. Truth total = `banked + held`. On slip: `held` **drains**,
`banked` never moves. At `banked+held = 60` the token is spent and the break fires.
Bank state persists for the life of the session's disarm attempt (overlay dismissed and
reopened mid-attempt keeps the bank; session end or break completion clears it) — a bank
that evaporates when the overlay closes would be a lie about what is owed.

### B1 — ★ The bank engine + truthful numbers (logic and readout only)
*Mechanism:* Replace `holdStartRealtimeMs`-only state with `bankedSeconds` +
`segmentStartRealtimeMs`. Runnable stays 32ms. Crossing 15/30/45 (of total) moves that
15s from `held` into `banked` and fires the B2 bank event (until B2 ships: one
`playFirmClick`). On release: `held` drains at **3 banked-seconds per real second**
(so the maximum unbanked loss, 14.9s, drains in ≈5.0s — long enough to re-grab, short
enough to cost), driven by the same runnable; re-grab during drain halts it instantly at
the current value and resumes accrual after a 250ms re-seat (no accrual during re-seat).
Drain stops dead at the bank floor.
*Readout (TRUTH once, human language):* centre line becomes two facts, never one blended
number — holding: `"BANKED 30 · HOLDING 7"` (Anonymous Pro Bold 13sp); hero remaining =
`60 − banked − held`, and it may only ever move for those two reasons. Idle with a bank:
`"BANKED 30 OF 60 · HOLD TO CONTINUE"`. Tokens-0 state unchanged.
*Haptic/sound:* per-second tick retained until B2; bank crossing = firm click placeholder.
*Ships in:* this view + one state holder on the overlay's state; ~half a day. **This rung
is his settled rule existing at all — everything below is it becoming legible.**

### B2 — What a bank feels like; what a drain sounds like
*Per-bank haptic/sound (the core spec — escalating weight, falling pitch: each bank is a
heavier bolt seating in the door):*

| Bank | Haptic waveform (ms / amps) | Sound (TactileSoundEngine transient) |
|---|---|---|
| **15** | `[0,11,26,9] / [0,215,0,150]` (latch, as-is) | 16ms, **560Hz**, gain 0.54, chord 1.0 |
| **30** | `[0,13,20,10,18,12] / [0,235,0,170,0,150]` (double seat) | 22ms, **470Hz**, gain 0.58, chord 1.05 |
| **45** | `[0,16,24,12,20,14] / [0,255,0,195,0,165]` (triple seat) | 28ms, **390Hz**, gain 0.62, chord 1.1 |
| **60 (spend)** | `playHarmonicSeal(arming=false)` `[0,18,30,12]/[0,200,90,140]` | 48ms, 480Hz, gain 0.62, chord 1.5 |

*Holding ticks:* replace the canned latch-per-second with pitched detents via the 1L route:
`playRotaryDetent` pitch **rises within each segment** 420→660Hz across its 15s and resets
at each bank (the ear hears "climbing toward the next bolt"), gain 0.30; every 5th second
adds the firm click at gain 0.40. Amplitude of the haptic tick: `[0,5]ms/[0,90]` early
segment → `[0,7]ms/[0,140]` in the 45–60 segment.
*Drain (on slip):* a continuous descending stream for the drain's duration — transient
pitch **glides 600→280Hz**, gain 0.34→0.18, with a haptic pulse every 100ms,
`[0,4]ms` at amplitude 120 falling linearly to 50 as `held → 0`; the instant the drain
reaches the bank floor it stops with a **catch**: `playMechanicalLatch(engaged=true)` at
gain ×0.7, 520Hz — *the floor is felt, not watched.* If `banked = 0` the drain ends at
zero with the latch-release pattern instead (nothing caught — nothing was banked).
Re-grab mid-drain: drain stream cuts within one 32ms frame, one firm click (re-seat), then
B1's 250ms before accrual resumes. *No rung of this ladder ships a bank or a drain
without these specs attached.*

### B3 — Banked vs unbanked at a glance: the chambered bar
*Mechanism (draw):* The bar's interior becomes **four chambers**, each 15s wide, separated
by 3dp vertical walls in cream α55% (the same stock as A3's end-stop). Each chamber:
- **Banked:** filled solid `tokControlFill()` (night `#4A423C` / day `#615B5A`) with its
  numeral (`15`/`30`/`45`) printed Anonymous Pro Bold 11sp cream α90% at the chamber's
  right end, and a 1dp cream α35% stamp line under the numeral — banked reads as *stored
  metal*, visually heavier and quieter than live progress.
- **Currently held (unbanked):** fills in accent alpha 180 as today, over the chamber floor.
- **Draining:** the unbanked fill renders as diagonal hatch (4dp stripes, accent α180 /
  transparent) collapsing right-to-left at the B1 drain rate — draining is the only
  hatched thing in the app; you cannot mistake it for progress.
- **Empty:** lane floor black α18%.
At a glance, mid-hold, with the numbers covered: solid chambers = safe, orange = at risk,
hatch = leaving. Completion: all four chambers banked-style for 180ms, then the spend
event (B2) fires. Height stays 68dp in the overlay; target is the full bar (≥88dp wide
segments are inherent — chamber width @360dp ≈ 84dp, above the 88dp grip target's spirit
for a press-and-hold surface; the whole bar is the target, as today).
*Night:* chamber walls cream α45%; banked fill `#4A423C` with cream numerals — cream stays
a text tone, never a fill.

### B4 — Slip grammar: forgiving the sensor, never the decision
*Mechanism:* Distinguish *contact loss* from *release*. `ACTION_CANCEL`/pointer-loss gaps
**<150ms** (palm shift, sensor dropout at 3 AM with a shaking hand) freeze accrual and
drain both — the clock simply pauses; a gap ≥150ms, or a clean `ACTION_UP`, is a slip and
B1 drain runs. Freeze is capped: total frozen time per attempt ≤1.5s, after which any gap
drains — the pause can absorb a tremor, never substitute for holding. While frozen, the
held fill stops dead (no hatch, no motion) and the readout adds nothing — frozen time is
not held time and is never counted. Drain rate curve refines B1: linear 3× for the first
half of `held`, easing to 1.5× approaching the floor (`PathInterpolator(0.3,0,0.7,1)` on
the drain fraction) — the catch at the floor (B2) arrives gently enough to re-grab against.
*Haptic/sound:* freeze entry/exit are **silent** (it isn't an event in his intent);
drain and catch exactly as B2. *Truth guard:* the 150ms/1.5s numbers ship in the overlay's
caption copy: `"15s banks. A slip drains what isn't banked."` — the rule stated once,
where the hand is.

### B5 — The vault minute, finished: hero countdown + segment theatre
*Mechanism:* Overlay layout change only: above the chambered bar, the remaining seconds
become a hero numeral — Anonymous Pro Bold **44sp**, cream on the overlay ground,
`"47"` with a 11sp Atkinson label `"SECONDS STILL OWED"` — because at the moment of
weakest resolve the number he must trust is the biggest thing on screen. The bar keeps
B3's chambers; the centre line keeps B1's `"BANKED 30 · HOLDING 7"`. Final segment
(45→60): chamber walls brighten cream α55→80% one per banked… no — one per *second* in
the last 5s, a 100ms step each, so the last five seconds are visible as the door closing.
Bank stamp animation: on each bank, the chamber's numeral stamps in — scale 1.15→1.0,
**140ms Curve A**, with B2's bolt sound — once, never looping. Token spend: on completion
the badge flips `"1/1 LEFT" → "0/1 · SPENT {day}"` in the same frame as the seal, and the
overlay's caption updates to past tense. Idle animation: none, anywhere, ever.
*A11y:* `contentDescription` = `"Emergency valve. {owed} seconds still owed, {banked}
banked. Hold to continue."`, updated at each bank and each drain-stop only (not per tick).

---

## ★ Highest-value rung across both gestures

**★ B1 — the bank engine + truthful numbers. Priced: ~half a day, one view
(`Emergency60sValveView`) + one state field on the disarm state; no new tokens, sounds,
or geometry.**
Reason: the slide's two worst faults already shipped fixes (gate event, seal sound) — its
ladder is refinement of a working gesture. The valve is different: his settled, changed
rule **does not exist in the app**, and the current code actively misstates it (header
claims decay, code zeroes; readout counts only the current grip). Every other B rung —
and the sealed bar's `BANKED` echo in A5 — is decoration until B1 makes the numbers true.
Ship B1 before any A rung beyond A1.

## PEAKS (the shapes, if nothing else survives)

**Slide peak (≤6 lines):** A 72dp recessed slot with a printed scale and a dark seat
pocket; an 80dp accent sled with one cream line, grabbed honestly, running free then
heavy (0.80 load zone), breaking through a felt, seen gate at 0.82 into a runway of four
widening detents; release drives it into the seat with 2dp compression and the harmonic
seal, and it *stays there* — the same bar, sealed, for the whole session, reversible only
by the mirrored grace slide. Arming reads in peripheral vision as position, and the whole
gesture from grab to seal is under 3 seconds of the ≤10s budget.

**Valve peak (≤6 lines):** A four-chamber bar where banked seconds sit as solid,
numeral-stamped metal and live seconds glow accent-orange above them; each 15s lands as
a heavier, lower bolt (560→470→390Hz) the hand hears as weight accumulating; a slip
doesn't erase — the unbanked orange hatches and drains at 3× under a falling 600→280Hz
glide until it *catches* on the bank floor with a latch click; the number still owed
stands at 44sp, and it has never once counted time that wasn't held or banked.

## STANDARDS CHECK

- **ARM ≤10s:** Slide: grab-to-seal ≈1.5–3s typical (248dp travel, A2 curve), worst case
  (slow deliberate drag + A1's 140ms drive + 120ms dwell) <5s. ✔ Valve is not an arm path.
- **HOLD absolute:** Inside a session nothing in either ladder adds a decision. The valve
  remains the one sanctioned exit and stays a deliberate physical act: banking lowers the
  *grip-endurance* price, never the *time* price — 60 full seconds of holding are still
  owed, the token is still 1/week, and no rung introduces a tap, a confirm, or an abort
  shortcut. B4's 150ms freeze absorbs sensor loss only, is capped at 1.5s/attempt, and
  never counts as held time. ✔
- **TRUTH once:** Slide: gate, printed pocket, and commit line are the same 0.82 fact;
  `"RELEASE TO ARM"` renders only while past it; sealed label shows remaining from session
  state. Valve: remaining = `60 − banked − held` by construction; banked and held are
  always rendered as separate facts (B1 line, B3 chambers); drain and freeze never inflate
  either number; the old header claim ("decays") becomes true or is deleted with B1. ✔
- **His picks:** Sora/Atkinson/Anonymous Pro real weights only, as shipped; warm palette
  untouched, zero new tokens, **no green** in any state including drain hatch (accent
  stripes) and night (cream text only, control fills ≤`#4A423C`). ✔

## CONFLICTS (breaks with wave-1 / synthesis, one line each)

- **Threshold stays 0.82, not synthesis §18's ~0.65** — the shipped gate, the printed
  pocket, and the commit line being *one* fact outweighs a lower latch; A4's velocity
  throw is the deliberate fast path instead.
- **Wave-1's strain-wall veto and accent budget stand unbroken** — A2's load zone is a
  0.80 *lag*, not a wall (finger always wins, sled follows), and accent still appears
  exactly once on the Deck (the thumb; A3 makes the trail tonal).
- **Wave-1 PEAK's two-word label (`ARM SESSION`) not adopted** — with citations lifted
  the sentence still earns its place at 3:30 AM for a first-week user; A2 keeps the full
  label and spends the deletion budget on the badge instead. Flagged as taste, reversible.
- **Valve drain rate 3× and freeze 150ms/1.5s are new numbers with no wave-1 precedent** —
  chosen so max unbanked loss resolves in ≈5s; if Sarthak wants a slip to cost more,
  the single knob is the drain multiplier, named here so he can turn it.
- **Mail sent:** Ines (MaxImpl, slide) — A1's drive-to-seat ending replaces the spring-back
  her spec may still assume; gate stays 0.82, her detent work unaffected.
  Reihe (Improvers, routine rows) — no row changes requested; flagged that if routine rows
  ever echo valve state, the vocabulary is B1's `BANKED n · HOLDING m` + chamber segments,
  never a blended percentage.
