# SLICE-DE — LUZ (Wild, games) — THE GATE AT 0.68

*Joint: the FREE → DAMPED gate feel, its hysteresis, its direction
asymmetry, its relation to the D4 notches, and the Live Wire's second
read at the crossing. Not mine: the damped-zone physics (Vega), the
catch/commit at 0.84, the OFF-stop's gesture law (Joss), the wire
itself (Ines). Law obeyed throughout: REGISTER — GATE 0.68, haptic
only, no colour at the gate ever (F-ORANGE); CATCH 0.84; OFF-STOP
0.65; verdict synchronous ≤5ms at DOWN and at the gate.*

---

## 1) VERDICT

**Build it exactly as §3.** One Schmitt latch on *displayed*
progress: up-crossing fires at **0.680**, retreat fires at
**0.660** (dead-band 0.020 = 3.6dp of the 180dp travel), one feel
per latch transition, no time-based debounce anywhere. Both
directions speak through the one pitched branch the codebase has —
`fireFeel(1L, freq)` → `playRotaryDetent` — up at **620Hz**,
retreat at **340Hz**, same engine waveform `[0,7,5]ms /
[0,180,70]`. Today's gate feel (the 8L firm click at 0.82, both
directions, no hysteresis) dies entire. On every latch transition
the Live Wire is re-read *first*, synchronously, before any feel:
live (or merely unproven) → the feel fires and **nothing visible
changes, ever**; positively dead (BLOCK = OFF) → the feel is
suppressed, the crossing is handed to Joss's OFF-stop law, and this
joint goes silent for the rest of that drag. The gate gets **no
D4 notch** (§3.5). My old 0.76 onset is **not** revived as a live
proposal — it is filed in §7 as a dormant, priced amendment with
its anti-case, recommendation AGAINST, because Fen met my own
pre-registered kill condition (§8).

## 2) AGAINST THE REAL CODE

All lines verified this run in
`Focus-app/app/src/main/java/com/example/ui/views/BlockitCoreViews.kt`
and `.../service/TactileSoundEngine.kt`. Note the file has grown
since Ines §0 measured it: `SlideToStartView` is no longer at
669–855 / 187 lines — it now starts at **:1046**. Her §0 facts
about the gate itself still hold, at the new addresses:

| Fact today | Evidence |
|---|---|
| Slide view, one class | `BlockitCoreViews.kt:1046` (`class SlideToStartView`) |
| Gate feel = boolean flip at **0.82**, both directions, **no hysteresis**: `wasPastGate = dragFraction >= 0.82f` … `if ((newFrac >= 0.82f) != wasPastGate) fireFeel(8L)` | `:1198–1205` (`updateDrag` at :1198, flip at :1200/:1205) |
| That feel is the **firm click**: `fireFeel(8L)` → `engine.playFirmClick()` — canned, pitch-dead (the contract comment at :206–210 says only the 1L branch honours `freqHz`) | `:204–221` (dispatch), branch at `:218` |
| Firm click's actual body: waveform `[0,13,20,8]ms / [0,225,0,135]`, transient 620Hz fixed / 12ms / gain 0.5 — 41ms of waveform for a *landmark tick*, heavier than the latch knock's register warrants and identical in both directions | `TactileSoundEngine.kt:273–286` |
| Commit is still release-only at the same 0.82 the gate feel marks (`if (dragFraction >= 0.82f)` on UP) — so today's "gate" is not a zone boundary at all, it is the commit line clearing its throat | `:1177` (in `onTouchEvent` :1153) |
| Label + badge fade: `textAlpha = (1f − dragFraction × 1.3f)` applied to both the label (:1113) and the badge (:1121) — dies fully by frac ≈0.77, i.e. *before* today's gate even fires | `:1111` |
| A second feel competes in the same `updateDrag` call: 8-bucket ticks `fireFeel(1L, 380f + bucket×45f)` — a bucket change and the gate flip can land in one MOVE and double-fire | `:1207–1210` |
| The only pitched branch: `1L -> engine.playRotaryDetent(freqHz.toDouble())` | `:216` |
| Rotary detent's body: waveform `[0,7,5]ms / [0,180,70]`, transient pitch param (clamped 280–1480Hz) / 6ms / gain 0.38 | `TactileSoundEngine.kt:258–271` |
| `fireWeightedKnock(durationMs, amplitude01, pitchHz)` exists (:239) → `playSeatKnock` (:294) — real amplitude/pitch params, but its voice is the *seat* voice (wheel seat, strait landings); §3 rejects it for the gate, §6 prices it as the device-trial fallback | `:239–255`; engine `:294–310` |
| Spring-back writes `dragFraction` directly in its animator, bypassing `updateDrag` — so programmatic motion is *already* gate-silent in this code. §3.4 makes that separation a rule the rewrite must preserve, not an accident it may lose | `:1214–1226` |
| No drag integrator exists yet: `updateDrag` maps finger x straight to `dragFraction` (`(touchX − inset − thumbW×0.5f) / maxTravel`), no displayed-vs-raw split, no damping, no velocity, no crossing callback. Everything in §3.6 is new interface, priced in §5 | `:1198–1199` |

## 3) THE SPEC (build-ready, exact numbers)

### 3.1 The feel

| Crossing | Call | Engine result | Why |
|---|---|---|---|
| **UP** — FREE → DAMPED, displayed frac reaches 0.680 | `fireFeel(1L, 620f)` | `playRotaryDetent(620.0)`: waveform `[0,7,5]ms / [0,180,70]` (≈0.71 strike / 0.27 tail), transient 620Hz / 6ms / gain 0.38 | Bright, short, pitched — a pawl *engaging*. 620Hz sits at the top of the slide's detent family (Ines's plan ladder reaches ≈686Hz at her gate) without touching latch/seal pitches (360Hz seal, ≈300Hz seat knock) |
| **DOWN (retreat)** — DAMPED → FREE, displayed frac falls to 0.660 | `fireFeel(1L, 340f)` | `playRotaryDetent(340.0)`: same waveform `[0,7,5]ms / [0,180,70]`, transient 340Hz / 6ms / gain 0.38 | The pawl *lifting* (Ines's gate-exit image, kept): same body, pitch dropped 280Hz — direction is audible eyes-closed, amplitude need not change |

- **Not the 8L firm click.** It is pitch-dead, 41ms long, and in
  this app's vocabulary it is a *heavy control* voice (sled throws,
  Ines's halfway landmark). A gate the hand crosses twice on a
  bad morning cannot speak heavier than the events it precedes:
  latch knock is 14ms @ 0.85 at 0.97 of travel (REGISTER), the seal
  is the full strike. The gate is a tick between silence and grain.
- **Not Fen's abstract 6ms @ 0.45, literally.** Fen's plan number
  (his seat file: "gate crossing at 0.68 … tick 6ms @ 0.45") has no
  exact engine expression: the detent waveform is engine-fixed at
  `[0,180,70]`. The 1L detent is the closest lawful voice — 7ms
  strike + 5ms tail, perceived well under the firm click — and
  every other seat in the studio has accepted `[0,180,70]` as "the
  tick" at their own abstract amplitudes. If the device trial says
  the gate tick is too strong, the fallback is priced in §6 — it
  is an engine change, not a call-site change, and it does not
  block this slice.
- **Sound on/off:** the detent's transient rides the same call; if
  sound is off the waveform alone carries the gate (Fen's law for
  the seal applies here too: nothing is lost).
- **One feel per frame, gate wins.** If a bucket tick (today's
  :1207–1210) or a damped-zone grain tick (Fen: 3ms @ 0.18 per
  12dp — Vega's/Fen's joint, not mine) would fire on the same MOVE
  / frame as a gate latch transition, it is suppressed and its
  accumulator/bucket is reset to the gate position, so no grain
  fires within the first 12dp inside DAMPED on account of travel
  already counted in FREE. The engine's double-buzz lesson
  (the :211–213 comment) is the precedent: two strikes in one
  instant read as mush, not as two facts.

### 3.2 Direction asymmetry — yes, and a retreat MUST re-fire

- **Pitch only:** 620Hz up / 340Hz down, identical waveform and
  amplitude. Asymmetry in pitch, symmetry in weight — entering
  resistance and leaving it are the same pawl, worked in opposite
  directions; making the retreat *quieter* as well would teach the
  hand that leaving DAMPED is a lesser event, and it is not: it is
  the moment a deliberate un-arm begins.
- **A retreat re-fires, always** (subject only to the §3.3 latch):
  a hand that crosses up, thinks, and pulls back must feel the
  stair edge on the way down, or the edge exists in only one
  direction and the hand stops trusting it. A retreat that then
  re-crosses up fires up again — each genuine excursion gets its
  pair of ticks. Silence is reserved for *hover*, never for
  *travel*.

### 3.3 Hysteresis — the Schmitt latch (exact)

Two constants and one boolean. Nothing else.

```
GATE_UP   = 0.680f   // fire UP when displayedFrac >= GATE_UP, latch == FREE
GATE_DOWN = 0.660f   // fire DOWN when displayedFrac <= GATE_DOWN, latch == DAMPED
dead-band = 0.020 of travel = 3.6dp on the REGISTER's 180dp
state:    gateLatch ∈ {FREE, DAMPED}   // the zone the FEEL believes we are in
```

Rules:

1. `gateLatch` initialises **silently** at every ACTION_DOWN from
   the current displayed frac (`>= 0.680 → DAMPED`, else FREE).
   Landing a finger already past the gate is not a crossing and
   fires nothing (the crossing that matters is travel, not
   arrival). It also resets silently to FREE when a spring-back /
   return animator completes at rest, and is invalidated
   (`wireDead`, §3.4) for the remainder of a dead drag.
2. A transition fires **exactly once**: latch flips in the same
   call that fires the feel / performs the wire re-read. There is
   no second path to either.
3. **The hover case, worked:** finger trembling 0.679 ↔ 0.681
   around the boundary. First sample ≥0.680: one UP tick, latch =
   DAMPED. Every subsequent sample: 0.679 is not ≤0.660, so no
   DOWN; already DAMPED, so no second UP. **Total: one tick, then
   silence for as long as the hover lasts.** The mirror hover at
   0.659 ↔ 0.661 after a DOWN fire is identically one tick. A
   hover can never produce more than one gate feel per genuine
   excursion beyond the band — machine-gunning is impossible by
   construction, not by timing luck.
4. **No time-based debounce, deliberately.** A fast genuine
   sequence — cross up, retreat to 0.60, re-cross up inside
   100ms — fires UP, DOWN, UP in full. That is three real
   crossings and the hand is owed three ticks. Any ms-based
   throttle (e.g. the ≥28ms detent throttles elsewhere in the
   studio) would eat the middle tick of exactly the hesitant
   morning the gate exists for. Position is the debounce.
5. **The physics boundary does not inherit the hysteresis.**
   Vega's integrator switches FREE/DAMPED behaviour at displayed
   **0.680 in both directions, exactly** (REGISTER). Only the
   *feel + wire latch* is Schmitt-shifted on retreat (fires at
   0.660, 3.6dp / ~12ms at a 300dp/s drag after the zone has
   actually been exited — below perception as a timing error,
   and it buys the whole dead-band). Do not "simplify" this by
   moving the zone boundary to 0.660 on the way down: that would
   make the zone itself hysteretic, the catch arithmetic would
   inherit the wobble, and the REGISTER's one-number zone would
   have two values — the exact disease the register exists to cure.
6. Jump crossings count once: Android coalesces MOVEs, so a single
   sample step 0.60 → 0.72 is one transition (latch flip), one
   tick, one wire re-read — same boolean-transition shape as
   today's :1205, kept.

### 3.4 The Live Wire re-read at the crossing

On **every** latch transition (both directions), in this order,
synchronously, in the gesture's own call stack, before the frame
renders:

1. **Re-read:** `liveWarrant(nowMono)` (Ines §3: four volatile /
   AppOps reads, ≤2ms typical, **5ms hard budget**). The DOWN read
   already happened at touch-down (Ines's joint); this is the
   second read, and it is mine to place: it replaces the latch
   evaluation, it does not add a frame, a coroutine, or a wait.
   There is no SEEKING state at the gate.
2. **Verdict LIVE** (wire live) **or UNPROVEN** (Ines's narrow
   no-stamp-this-residency class — absence of evidence is not
   evidence of death, and Rosa's evidence split governs): fire
   the §3.1 feel for that direction. **Nothing visible changes —
   ever.** No colour (F-ORANGE / REGISTER: haptic only), no label
   or badge change (their fade stays the existing
   `1 − 1.3·frac` law, :1111), no notch, no strip change from
   this joint. Travel limit unchanged: the catch at 0.84 stays
   reachable on this same drag.
3. **Verdict DEAD** (positive evidence: BLOCK returns OFF):
   **do not fire the gate feel** — not up, not down. Set
   `wireDead = true` for this drag attempt, emit the crossing to
   the OFF-stop owner (direction + displayed frac + source =
   CONTACT) and let **Joss's OFF-stop law take the gesture** from
   that frame: travel limit collapses to 0.65, his dull knock /
   hold / return are his spec and his sounds, and two owners
   buzzing one frame is the double-buzz bug in a new costume.
   While `wireDead` holds: no further gate feels, no further
   re-reads from this joint (the attempt has been handed off;
   re-reading would be re-litigating a handed-off gesture), and
   the programmatic collapse toward 0.65 — which crosses
   GATE_DOWN's 0.660 on its way — fires **nothing** (see §3.5,
   source rule). `wireDead` clears at the next ACTION_DOWN,
   which performs its own fresh DOWN read anyway.
4. Retreat transitions get the same treatment, because a wire can
   die mid-drag inside DAMPED: DOWN transition → re-read → live:
   retreat tick (340Hz); dead-and-not-yet-handed-off: suppress
   the tick, hand off as in (3). In practice Ines's mid-drag
   death rule (limit collapses on the next MOVE frame) will
   usually have handed off before a retreat crossing occurs;
   this clause is the belt to that suspender, and it costs one
   boolean check.

### 3.5 Source rule + the D4 notch

- **Source rule:** the gate evaluator runs only on
  **contact-driven** displayed progress (ACTION_DOWN/MOVE while
  the finger is down). Programmatic motion — spring-back
  (:1214), the assist spring / driven mesh of the tap path, the
  OFF-stop collapse, the sled-goes-home return — must never call
  it: animators keep writing progress through their own path, as
  today's spring-back already does. Consequences, all intended:
  a released drag springing home through 0.660 is silent; the
  tap's driven mesh crossing 0.680 is silent and performs **no**
  gate re-read (the tap's verdict is its DOWN read — a held tap
  that outlives its read is the tap joint's problem, flagged in
  §6, not silently absorbed here).
- **The notch, in one line:** the gate gets NO D4 notch because a
  notch is a visible, let-go-here mark and the gate is a
  haptic-only boundary nothing is decided at — the notches sit at
  the CATCH 0.84, the one coordinate where letting go stops
  mattering (REGISTER / F-GATE).

### 3.6 Interface required from Vega's drag integrator

I do not need the integrator's internals. I need this contract,
and the gate feel cannot survive without every clause:

```kotlin
enum class GateDirection { UP, DOWN }
enum class ProgressSource { CONTACT, PROGRAMMATIC }

// Called by the integrator for EVERY displayed-progress sample, in order.
fun onDisplayedProgress(
    frac: Float,                 // DISPLAYED progress, post-damping, 0..1
    source: ProgressSource,
    frameMonoMs: Long,
): GateCrossing?                 // non-null exactly once per latch transition

data class GateCrossing(
    val direction: GateDirection,
    val displayedFrac: Float,    // the sample that crossed (0.680+ up / 0.660- down)
    val frameMonoMs: Long,
)
```

1. **Displayed, never raw.** Evaluation is on displayed progress —
   the same coordinate the CATCH law uses ("displayed progress
   crossing 0.84", REGISTER). This is load-bearing, not pedantic:
   Fen's damped lag runs up to 6dp ≈ 0.033 of travel — *larger
   than my whole dead-band (0.020)*. A gate evaluated on raw
   pointer progress would fire while the cap the user sees is
   still in FREE, and could fire again when the display caught
   up. One coordinate system or the gate lies.
2. **Synchronous, same-frame delivery.** A crossing is returned /
   dispatched inside the same MOVE handling that produced the
   sample, before `invalidate()` renders that frame — the wire
   re-read (≤5ms) and the feel must land on the crossing frame,
   not the next one. No posting to a handler, no flow collection.
3. **No integrator-side throttle on this channel.** Detent/grain
   throttles (≥28ms etc.) must not swallow, delay, or coalesce
   away a latch transition. Samples may be coalesced (jump rule,
   §3.3.6); transitions may not.
4. **Direction + frac + source + timestamp is all I consume.**
   Notably **velocity is NOT consumed**: the gate feel is
   fixed-amplitude and fixed-pitch per direction at any drag
   speed (Fen: no pace gate — a fast arm and a slow arm both
   pass, and both feel the same stair edge). The integrator may
   carry velocity for its own physics and for Joss; it must not
   scale, gate, or suppress my feel by it. If a future tuning
   wants velocity in the feel, that is a new amendment, not a
   parameter I am silently depending on.
5. **Zone switching stays the integrator's, at 0.680 sharp, both
   directions** (§3.3.5). My latch's 0.660 retreat threshold is a
   feel-layer value; the integrator must not read `gateLatch` to
   decide its physics — the dependency runs one way only
   (integrator → gate evaluator), or the two will chase each
   other across the band.
6. **Animator writes are tagged PROGRAMMATIC or bypass the
   evaluator entirely** (§3.5). Either satisfies me; silently
   routing animator samples through CONTACT does not.

## 4) CODE SITES

| Site | Change |
|---|---|
| `BlockitCoreViews.kt` — `SlideToStartView` fields (near :1075–1078, beside `lastTickBucket`) | Add `GATE_UP`/`GATE_DOWN` constants, `gateLatch` boolean, `wireDead` boolean |
| `BlockitCoreViews.kt:1198–1211` (`updateDrag`) | Delete the 0.82 boolean flip + `fireFeel(8L)` (:1200, :1205); route displayed frac through the gate evaluator (§3.3/§3.4); add crossing-frame suppression of the bucket tick (:1207–1210) |
| `BlockitCoreViews.kt:1153–1196` (`onTouchEvent`) | DOWN: silent latch init (§3.3.1) + clear `wireDead`; UP/CANCEL: no gate code (source rule) |
| `BlockitCoreViews.kt:1214–1226` (`animateSpringBack`) | No call into the evaluator (preserve today's separation); on completion, silent latch reset to FREE |
| `BlockitCoreViews.kt:204–230` (`fireFeel`) | **Unchanged.** The spec lives entirely in the existing 1L branch (:216) |
| `TactileSoundEngine.kt` | **Unchanged.** `playRotaryDetent` (:258) as-is; no new engine pattern |
| Host / VM (Ines's Live Wire sites) | View exposes one callback — `onGateWireDead: ((GateCrossing) -> Unit)?` — and consumes the synchronous `liveWarrant()` read; the OFF-stop behaviour itself is Joss's slice, wired at his sites, not these |

## 5) PRICE

Priced as an increment on the W4 slide rewrite (Ines §1 item 1,
~700 gross for the view, which already carried a line for "gate
haptic at 0.68" — this is that line, specced):

- Gate latch + evaluator (§3.3, incl. DOWN init + animator reset): **~30 lines**
- Wire re-read + DEAD handoff + `wireDead` latch (§3.4): **~14 lines**
- Crossing-frame tick suppression (§3.1): **~4 lines**
- Engine changes: **0 lines.** VM/repository changes in my joint: **0** (the callback is a view property; `liveWarrant()` is Ines's priced item, not double-counted here)
- **Net: ≈48 lines in one file** (≈35 if built as a standalone
  patch on today's view: move 0.82 → the Schmitt pair, swap 8L →
  pitched 1L, add the latch — the wire hook is the only part that
  must wait for Ines's read to exist)
- Tests: **~50 lines** — hover at 0.679/0.681 fires once; hover at
  0.659/0.661 fires once; full excursion re-fires both ways;
  sub-100ms excursion triple-fires; programmatic crossings silent;
  DEAD at crossing suppresses feel + hands off once; jump crossing
  fires once
- **Total: ≈98 gross / ≈48 net production, 1 file + tests.**

## 6) RISKS / DEVICE-ONLY UNKNOWNS

1. **Detent strength on the actual handset.** `[0,180,70]` is
   engine-fixed; whether the gate tick reads as "stair edge" or
   "too much / too little" on Sarthak's phone is unknowable from
   this desk. Fallback, priced: route the gate through
   `fireWeightedKnock` → `playSeatKnock` (UP 10ms @ 0.55 /
   340Hz, DOWN 7ms @ 0.35 / 260Hz — pitch clamp 160–420Hz fits)
   at **~2 lines** changed — but it spends the *seat* voice on a
   *passage* event, so it ships only if the trial convicts the
   detent, never pre-emptively.
2. **Pitch audibility of the asymmetry.** 620 vs 340Hz transients
   at 6ms / gain 0.38 may be indistinguishable with sound off in a
   noisy room; with sound off the *haptic* waveforms are identical
   by design (§3.2), so direction then reads from context (which
   way the hand is moving) — acceptable, stated plainly: pitch
   asymmetry is a sound-on refinement, not a load-bearing signal.
3. **The integrator doesn't exist yet.** §3.6 is a contract
   against Vega's unbuilt physics. If his displayed-progress
   pipeline ends up animator-driven (samples emitted from a
   spring loop rather than MOVE handling), clause 2 (same-frame)
   and clause 6 (source tagging) are the two that will break
   first — both are testable in unit tests before any device run.
4. **Held-tap wire staleness** (§3.5): the tap path reads the wire
   at DOWN and its mesh crosses the gate silently under my rule.
   A tap held for seconds before release could seat on a wire
   that died mid-hold. That is the tap joint's / Ines's call —
   flagged here because my source rule is what makes the mesh
   crossing silent; if they want a mesh-crossing re-read, it is
   a 3-line addition to §3.4, not a redesign.
5. **Dead-band size is a desk number.** 0.020 (3.6dp) is sized
   against finger tremor and the 6dp lag, not against his thumb
   on his phone at 03:30. If the trial shows hover-chatter (band
   too small) or a "late" retreat tick (band too big), the fix is
   one constant — the latch structure does not change.

## 7) FORK FOR REN

**One fork, filed dormant — the 0.76 amendment (my Thinning
Stage 2), priced, with its anti-case. Recommendation: DO NOT
ADOPT; keep on file under Fen's trigger condition.**

- **The amendment, if it were ever adopted:** parameterise the
  gate/onset behind a `FeedbackProfile` (gate 0.68 → 0.76,
  damped travel 28.8dp → 14.4dp, gate thresholds moving as a
  pair: UP 0.760 / DOWN 0.740, dead-band unchanged at 0.020, wire
  re-read moving with the crossing). Everything else in §3 —
  latch, pitches, source rule, notch law — is onset-agnostic and
  survives unchanged, which is exactly why filing it costs
  nothing now.
- **Price if adopted:** the profile parameterisation is the real
  cost — **~1 day** (it touches every tick schedule and zone
  constant in the slide + wheel, per my invention file's honest
  pricing of the flagship's feedback layer), plus the Thinning's
  sensing/store machinery (**~1 day**) if the move is earned-
  stage-gated as filed, plus **~1 day** device tuning. Call it
  **≈3 days all-in** for this one coordinate to move 0.08.
- **The anti-case, at full strength (Fen's, and mine):** the hand
  navigates *by* the gate as it commits *by* the catch — the gate
  tick is the road's only mid-travel landmark, and at a 0.76
  onset the damped road shrinks to ~14dp, the blip Fen's file
  killed the 0.80 onset for. Add my own invention file's
  anti-case, which I do not retract: consistency is the mechanism
  of muscle memory (the piano argument), and thinner/later
  feedback is a safety regression for exactly the depleted
  states this app is built for. Clean arms prove the landmark is
  being *used*, not that it has stopped being used.
- **Trigger condition (Fen's terms, accepted):** device evidence
  that his thumb no longer *reads* the gate — not merely that he
  arms cleanly. Until that evidence exists, the amendment sleeps.
  No other fork: the feel, the latch, and the wire placement in
  §3 need no decision from Ren — they are inside the REGISTER's
  law as filed.

## 8) MAIL / WALL NOTE

Inbox at start: one unread letter — Fen, 0719, thread
*the-thinning-vs-fixed-gate* — read and moved to `read/` (with
Orson's and Mara's citation-lift letters from the earlier wave,
already there). Fen's letter is the reason §7 reads as it does:
in my invention file I pre-registered the exact counter-argument
that would beat Stage 2 — *"if you can show the onset is as
load-bearing as the catch — that the hand navigates BY 0.68 the
way it commits BY 0.84 — Stage 2 dies by my own hand"* — and Fen
brought it, in the open, on the wall at 07:19: the gate is the
road's only mid-travel landmark, sameness is the road. Kill
condition met as a claim; the 0.76 move is filed dormant in §7
rather than relitigated, and the gate spec in §3 is built on the
fixed 0.68 without reservation. No mail sent — Fen's letter
needed no reply beyond this filing, which he can read here; a
wall note is appended under my name recording the concession and
the gate latch in one paragraph.

— Luz, Wild · games
