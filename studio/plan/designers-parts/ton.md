# TON — Designers, Haptics & Sound seat
## THE SCORE — Monolith, whole app: every event × sound × haptic × visual pair
### Plan wave · NO CODE · the rhythm section of the visual plan

Coordinator: Sana (Designers). Seat: Ton, haptics & sound.
Ground law, not relitigated: **Concept = NONE · ARM ≤10 s · HOLD absolute · TRUTH once.**
Citation requirement lifted by Sarthak's direct order — nothing below is cited as a
quota. Numbers carried from wave 1 are carried because they survived the settled
law; every number the settled law kills is named in §6 with its reason.

**Co-ownership declared:** the two overlays' sensory design is co-owned here —
the **Airlock interception overlay** (§4.1) and the **emergency-hold overlay**
(§4.2). Their visual owners draw the surfaces; this file owns what they feel
and sound like, frame-locked to those surfaces. If a visual change moves an
overlay's timing, the sensory timing in §4 moves with it or the overlay ships
silent — never unsynced.

---

## 0. The score in five sentences

1. **One gesture, one owner, one waveform.** The view owns contact (detents,
   drags, throws); the ViewModel owns outcomes (arm, finish, break). A gesture
   gets one or the other, never both. No double-firing, ever — a flam is a bug,
   not a texture.
2. **Two feel families, and direction is the meaning.** Over-centre **throw**
   (sleds, rockers, caps that commit state): ON lands sharper and brighter,
   OFF lands duller and lower — the same gesture, two truths, learnable
   eyes-closed. Single-stage **key thock** (composer caps, ADD, nav arrival,
   drawer): one bottom-out, no gap, no second strike — a key that snaps twice
   is lying about being a key.
3. **Pitch is identity, never arithmetic.** Every pitched sound is a member of
   a fixed set in §1, looked up by *which thing happened*, never computed from
   a value. Formulas that compute pitch from position, duration, count, or
   progress are banned app-wide — not just on Tasks. A pitch that measures is
   a readout pretending to be a mechanism.
4. **The seal is rationed to the seal.** The seal thock fires for exactly three
   events: session arm, emergency break, routine firing. Roughly twenty trivial
   actions spent it in wave 1; they are all demoted in this score (key, throw,
   or silence). If everything clunks, nothing is locked.
5. **Silence is the default state, not the absence of design.** While a session
   runs the machine is mute except the session's own events (§5). Refusals are
   silence, never error buzzes. Night scales everything down (sound ×0.6,
   haptic ×0.85) — quieter and duller, never a different melody.

**Sync law (applies to every row in §2):** haptic onset and sound onset land
within **±8 ms** of each other, and the visual pair lands in the **same 100 ms
window**. Outside ±8 ms the brain reads a phone buzzing near a speaker, not
one object striking. Every row names its visual pair because this score is the
rhythm section of a *visual* plan — a sound without its picture is unfinished.

**Amplitude notation:** sound gain in dB relative to the **seal = 0 dB**
(linear gain in brackets where it helps builders). Haptics as
`timings ms @ amplitudes 0–255`, `timings[0]` = lead-in at 0. Night values are
the day values × the §5 scaling — never re-voiced.

---

## 1. The fixed pitch sets — the entire tuning of the app

Five sets. Nothing pitched exists outside them. A new pitched event must join
a set by identity or ship unpitched (noise + body only).

### 1.1 WHEEL SET — three fixed voices, no ramp
Noise-dominant; pitch is a whisper of body under the tooth, fixed per grade.

| Grade | Noise centre | Body | Job |
|---|---|---|---|
| Tick (5-min) | 2,400 Hz bandpass | 880 Hz | every 5-min detent |
| Major (15-min) | 2,000 Hz bandpass | 640 Hz | every 15-min detent |
| Anchor (30-min) | 1,700 Hz bandpass | 520 Hz | 30 / 60 / 90 / 120 / 150 / 180 stops |

Wave-1's `+6 Hz per minute-step` brightening ramp is **deleted** (§6): it
computed pitch from position. The wheel brightens *by grade* — three learnable
identities — or not at all.

### 1.2 CAP SET — Tasks duration caps (wooden bars)
One pitch per cap. Longer duration = lower bar. Learned as identities.

| Cap | Pitch | Fundamental |
|---|---|---|
| 90m | D3 | 146.83 Hz |
| 45m | F3 | 174.61 Hz |
| 25m | A3 | 220.00 Hz |
| 15m | C4 | 261.63 Hz |

Bar voice: sine partials 1 / 2.76 / 5.40, 90–140 ms decay. (Agreed with Ivo,
Wild sound, who owns the acoustic desk's voicing — one set, two files, no fork.)

### 1.3 STEP SET — Tasks sub-steps (smaller bars, same wood)

| Sub-step | Pitch | Fundamental |
|---|---|---|
| 01 | E4 | 329.63 Hz |
| 02 | G4 | 392.00 Hz |
| 03 | B4 | 493.88 Hz |
| 04 | D5 | 587.33 Hz |
| 05 | E5 | 659.25 Hz |

Steps 06+ reuse the Step-05 voice through a deeper felt damper (decay ×0.6,
same pitch). **No sixth pitch is ever invented. The set is the set.**

### 1.4 LADDER SET — the banked emergency hold
One pitch per bank. Rising by bank, falling on drain — height you can hear.

| Bank | Pitch | Fundamental | Banks at |
|---|---|---|---|
| Bank 1 | A3 | 220.00 Hz | 15 s held |
| Bank 2 | C4 | 261.63 Hz | 30 s held |
| Bank 3 | E4 | 329.63 Hz | 45 s held |
| Break | — (unpitched crack + body, §2 row 26) | — | 60 s held |

Between banks, the hold ticks unpitched (noise tooth only, §2 row 21 note) —
pitch is spent only when height is actually gained or lost. That rationing is
what makes a drain *audible as loss*: you hear the set walk back down.

### 1.5 MASS SET — the three heavy bodies (unpitched-led, fixed bodies)

| Voice | Body | Owner event(s) only |
|---|---|---|
| **Seal** | 165 Hz (+330 Hz partial @ 0.12) | arm · break (release voicing, 205 Hz) · routine firing |
| **Accession stamp** | 98 Hz (+196 Hz partial) | session finish · day close (heavier strike, §2 rows 27–28) |
| **Paper stamp** | 110 Hz, dry | Tasks DONE / final-step seal — Tasks-local, never the accession voice |

Three stamps, three masses, three meanings. A finished *task* never borrows
the session's stamp; a finished *session* never borrows the seal.

---

## 2. THE SCORE — master table

Read across: **Event | Sound (named pattern · set · duration · amplitude) |
Haptic (pattern · duration · amplitude) | Visual pair (same 100 ms).**
Owner in brackets: **[V]** view owns it · **[VM]** ViewModel owns it. The other
layer is silent for that gesture — by construction, not by discipline.

### Deck — the wheel and the hub

| # | Event | Sound | Haptic | Visual pair (same 100 ms) |
|---|---|---|---|---|
| 1 | **Wheel detent — 5-min** [V] | **Ratchet Tick.** WHEEL/Tick: noise burst 2,400 Hz, 6 ms + body 880 Hz, 4 ms. **6 ms · −17 dB** (0.14) | `[0,4,3] @ [0,150,55]` · **7 ms** | Readout numeral **steps** (hard cut, never rolls) to the new value; new glyph lands at 88% ink, settling to 100% over 120 ms; tick mark passes through the fixed light |
| 2 | **Wheel detent — 15-min** [V] | **Major Tooth.** WHEEL/Major: noise 2,000 Hz, 9 ms + body 640 Hz, 10 ms. **10 ms · −14 dB** (0.20) | `[0,6,4,4] @ [0,205,0,90]` · **14 ms** | Rim numeral at the major (25/50/90…) brightens to full ink on the crossing frame; FINISHES recomputes at full ink (it sat at ~55% while the wheel moved) |
| 3 | **Wheel detent — 30-min anchor** [V] | **Anchor Tooth.** WHEEL/Anchor: noise 1,700 Hz, 11 ms + body 520 Hz, 14 ms. **14 ms · −11 dB** (0.28) | `[0,7,5,5] @ [0,225,0,110]` · **17 ms** | Anchor numeral + its longer rim tick align under the pointer together; readout steps with a one-frame letterpress weight-pulse (no intermediate glyphs — the total is computed, it re-strikes, it never counts) |
| 4 | **Wheel end-stop** (5 or 180, still pushing) [V] | **Stop Wall.** Unpitched-led: noise lowpass 900 Hz, 14 ms + body 210 Hz, 28 ms. **28 ms · −10 dB** (0.30) | `[0,10,14,7] @ [0,240,0,130]` · **31 ms** | Wheel travel halts against visible rubber-band stretch (≤4 dp give, no further); readout holds at the stop value, full ink. Fires **once** on entry; re-arms only after leaving the stop by ≥10 min |
| 5 | **Hub press** (BLOCKS count cycles 1→2→3→4) [V] | **Hub Key.** Key family (§0.2): noise 1,900 Hz, 6 ms + body by count from a fixed 4-member sub-set — 580 / 660 / 740 / 820 Hz, looked up by resulting count, never computed. **14 ms · −12 dB** (0.40 transient peak, body −16 dB) | DOWN tick `[0,4] @ [0,90]` (silent-feel only, no sound) + commit on release `[0,5,14,4] @ [0,190,0,110]` · **23 ms** | Next bezel segment / pip **fills** clockwise over 180 ms as the count commits (fire-on-release; slide-off cancels silently); caption re-strikes the factorisation in words (`2 BLOCKS OF 25 MIN`) — computed, instant, no roll. Wrap 4→1: segments drain counter-clockwise, 45 ms stagger, sound = same sub-set descending 740→520 Hz members |

**Wheel rate law (rows 1–3, non-negotiable):** minimum 28 ms between ticks;
ticks older than 30 ms in the queue are **dropped, never played late**.
Above ~6 detents/s fire every 2nd detent; above ~12/s every 3rd, amplitude
×0.75. A fast spin is a *blur of teeth getting lighter*, like a real pawl
skipping — never a backlog of chirps arriving after the finger lifts.
**Settle (finger lifts, wheel snaps home): silent** — except one Major Tooth
at 60% amplitude if the settle animation itself crosses a 15-min detent.

### Deck — the slide (the centre gesture)

| # | Event | Sound | Haptic | Visual pair (same 100 ms) |
|---|---|---|---|---|
| 6 | **Slide pickup** (thumb takes the sled) [V] | **Take-Up.** Noise tick 1,800 Hz, 5 ms. **5 ms · −20 dB** (0.10) | `[0,5] @ [0,120]` · **5 ms** | Thumb cap's contact shadow collapses; grip dot scales to 1.12 — the part is *held*, visibly, before it moves |
| 7 | **Slide travel** (drag, before the gate) [V] | **SILENCE.** No ratchet teeth, no friction loop, no rising anything. (Wave-1's 16-tooth rising ratchet is killed — §6. The travel's information is position, and position is already in the eye and the thumb's travel.) | **None.** The drag itself is the feel; resistance is mechanical (progressive, gentle — no strain wall), not vibrated | Sled travels 1:1 under the thumb; slot shows travel honestly; nothing else on Deck moves |
| 8 | **Slide drag — crossing the gate** (~0.65 travel) [V] | **Gate Tick.** One tooth, harder than any wheel tooth: noise 2,200 Hz, 7 ms + body 740 Hz, 9 ms. **9 ms · −9 dB** (0.35) | `[0,6,4] @ [0,215,60]` · **10 ms** | Grip line **thickens** on the crossing frame; the gate mark in the slot seats/flushes — the eye and the thumb learn the same stair-edge in the same frame |
| 9 | **Slide commit — SEAL** (release past gate) [VM] | **THE SEAL THOCK.** MASS/Seal: clunk — noise lowpass 650 Hz, 18 ms + body 165 Hz, 70 ms (+330 @ 0.12); pin at +38 ms — noise 2,600 Hz, 6 ms + 1,180 Hz, 8 ms. **~78 ms audible · 0 dB (1.0) — the loudest sound in the app** | `[0,16,22,12,30,9] @ [0,255,0,170,60,120]` · **89 ms** — strike, dead gap, catch, settling shoulder, lock pin | Sled snaps home in the powered ~120 ms commit travel; **then 120 ms of dwell — near-silence, Deck reconfiguring** (≤510 ms staged) while the seal's tail decays into it. The dwell is part of the sound: mass, mechanism, then the room holding its breath. Session state word flips on arrival |
| 10 | **Slide refused return** (released below gate) [V] | **One Landing Tick only**, at rest: travel-tooth voicing at its quietest — noise 1,400 Hz, 5 ms. **5 ms · −20 dB** (0.10). During the spring-back itself: **silence** — no retrace ticks, no failure tone, ever | `[0,4,3] @ [0,110,45]` · **7 ms**, at the landing frame only | Sled returns critically damped, **zero overshoot**, and *stops*. Stillness is the refusal. No shake, no flash, no word change — the Deck looks exactly as it did before the attempt, because nothing happened |

### Shield / Guard — throws and rockers

| # | Event | Sound | Haptic | Visual pair (same 100 ms) |
|---|---|---|---|---|
| 11 | **Sled throw — ON / SHUT** (engaging) [V] | **Throw-Engage.** Throw family: noise 2,800 Hz, 7 ms + body 720 Hz, 12 ms; second strike at +25 ms: noise 3,200 Hz, 5 ms. **~30 ms · −8 dB** (0.22 + 0.18) | `[0,7,18,8] @ [0,190,0,235]` · **33 ms** — break-free, gap, **snap home harder than the start** (over-centre) | Cap travels and seats at the ON end; state word at the margin flips **on arrival** (never mid-flight); cap fill/label keyed to animated position, so the eye never reads a state the part hasn't reached |
| 12 | **Sled throw — OFF / OPEN** (releasing) [V] | **Throw-Release.** Same skeleton, duller in every parameter: noise 1,600 Hz, 8 ms + body 440 Hz, 16 ms; second strike at +24 ms: noise 1,900 Hz, 6 ms. **~30 ms · −9 dB** (0.18 + 0.11) | `[0,8,16,9] @ [0,170,0,150]` · **33 ms** — break-free, gap, duller landing, **no snap accent** | Cap travels and seats at the OFF end; word flips on arrival. Eyes-closed test is the spec: after a week, ON vs OFF must be nameable from the throw alone |
| 13 | **Rocker throw** (3-stop: loadout cap STUDY/DEEP/SLEEP · tier rocker SHUT/PEEK/OPEN) [V] | **End stops:** Throw-Engage voicing scaled by stop — fixed members, body **640 / 720 / 800 Hz** for stops 01 / 02 / 03, gains ×0.85 / 1.0 / 1.15 of row 11. **Mid stop (PEEK / stop 02 landing from a throw that continues):** engage at 80%, **second strike removed** — a half-commit lands, but nothing snaps. **~30 ms · −8 dB class** | End stops: row 11 pattern, amplitudes ×0.85 / 1.0 / 1.15 by stop. Mid stop: `[0,7,18,8] @ [0,150,0,185]` · **33 ms** | Cap lands on its printed stop; marker line (constant cream) aligns; word flips on arrival. **Travel between stops: silent** — a cap that ticks mid-animation is claiming detents it doesn't have |

### Tasks — Paperwork That Plays (fixed sets only)

| # | Event | Sound | Haptic | Visual pair (same 100 ms) |
|---|---|---|---|---|
| 14 | **Key dip — duration cap** (15/25/45/90 in the composer) [V] | **Wooden Bar.** CAP SET member for that cap (D3/F3/A3/C4), bar voice 90–140 ms. **~120 ms · −11 dB**. Re-dip of the already-selected cap: **same pitch, felt-damped** (decay ×0.5) · **−15 dB** — "already down" is audibly different from "going down" | Strike `[0,8,6] @ [0,150,55]` · **14 ms**; damped re-dip `[0,5] @ [0,90]` · **5 ms** | Cap dips (+1–2 dp skirt travel, ~80 ms) and sinks into its selected seat; printed duration label steps one tone darker on the onset frame, settling back over 140 ms. Selected cap: no fade — it is already sunk; the damper is heard, not drawn |
| 15 | **ADD** (task committed to the queue) [V] | **Card Punch.** Stage 1 punch (t=0): sine drop 320→140 Hz, 30 ms + noise lowpass 700 Hz · **−4 dB**. Stage 2 slice (t=+90 ms): noise bandpass sweep 3,000→1,200 Hz, 70 ms · **−12 dB**. Seating: the new row lands sounding **its own duration's CAP SET pitch · −8 dB**; the two displaced neighbours knock once each · **−20 dB** | Punch `[0,14,30,8] @ [0,230,0,160]` · **52 ms** (the tail *is* the slice arriving in the hand); seating `[0,7] @ [0,185]` · **7 ms** at the landing frame | New row springs in (one visible ~2 dp overshoot); the seating clack fires **at the overshoot/landing frame, not at animation start** — the sound is caused by the landing. Header queue-total numeral re-strikes instantly (computed: no roll, no tick-count, no celebration) |
| 16 | **Sub-step check** [V] | **Step Note.** STEP SET member for that ordinal (E4/G4/B4/D5/E5), bar voice ~110 ms. **110 ms · −13 dB**. Un-check: **same pitch, felt-damped** (decay ×0.5) · **−17 dB** — a correction is the same note taken back, never an error note | `[0,5] @ [0,120]` · **5 ms**; Step-05 variant `[0,5] @ [0,152]` (five fixed variants, listed, not computed). Un-check `[0,4] @ [0,80]` · **4 ms** | Drawn strikethrough tears across the step text in 140 ms starting on the note's onset frame; row squashes ~4% into its hairline and settles; the step's margin numeral gains its **waveform fossil** — a 1 px trace of that note's actual decay, drawn in 110 ms, then static. Un-check erases strike + fossil together: the record never claims an undone step |
| 17 | **Final sub-step** (the check that completes the set) [V] | **The Strum.** All of *that task's own* STEP SET pitches — only the steps it has — staggered **12 ms apart, low→high**, summed peak **−1 dB**, total decay ~480 ms, through the app's one small room tail (−26 dB wet; −29 dB night). Then, at **+160 ms**, the **Paper Stamp** (MASS/Paper, 110 Hz) · **−6 dB**. Never the seal, never the accession stamp | Strum mirrored: one pulse per note (e.g. 3-step: `[0,6,12,6,12,8] @ [0,170,0,190,0,215]`); stamp `[0,22] @ [0,235]` + settle `[0,8] @ [0,90]` at +50 ms after it | Each fossil on the task's rows brightens one tone step **in strum order** (12 ms stagger — the margin plays back what it recorded); at the stamp frame the task title's ink steps one value darker for 200 ms (pressed in). **The title is NOT struck** — steps-done ≠ task-done; the strike belongs to DONE alone (row 18) |
| 18 | **Task DONE** [VM] | **Paper Stamp, alone.** MASS/Paper (110 Hz dry press + filtered-noise press) · **−6 dB**, **~70 ms**. If the final-step strum already sounded for this task: stamp only, **no second strum** — one completion, counted once. If DONE is pressed with steps still open: **one dull paper fold** (noise lowpass 900 Hz, 60 ms) · **−16 dB** — closed-incomplete sounds incomplete. Zero-step tasks: stamp alone | `[0,22] @ [0,235]` + settle `[0,8] @ [0,90]` at +50 ms · **~80 ms**. DONE on the ACTIVE task additionally carries the drop weight: one low pulse `[0,18] @ [0,200]` timed to the row's landing (heavier tasks land heavier — amplitude from a fixed 3-step lookup by duration band, not a formula) | Title receives its **drawn strike** concurrent with the stamp — the only act allowed to strike a title — and the row drops/closes with a landing on the stamp's frame. Queue-total numeral re-strikes instantly |

### Chrome — band switch and drawer

| # | Event | Sound | Haptic | Visual pair (same 100 ms) |
|---|---|---|---|---|
| 19 | **Band-switch travel** (cap sliding between stops) [V] | **SILENCE during travel.** No ticks per stop passed — passing a stop is not arriving at it | **None during travel** | Travelling cap moves in its recessed rail; printed stops pass underneath; the *old* destination word stays at full ink for the whole flight |
| 20 | **Band-switch arrival** (cap seats on a destination) [V] | **Nav Key.** Key family at its quietest: noise 1,900 Hz, 6 ms + body 520 Hz, 14 ms. **14 ms · −16 dB** (gains ×0.7 of the standard key) — navigation is the most repeated deliberate tap in the app; it must be the quietest, or it nags by day three. **Re-tapping the current destination: silent** (no change, no feel) | `[0,5,4] @ [0,122,42]` · **9 ms** | Cap seats; destination word flips **on the arrival frame**; screen content cuts (never cross-fades into a half-state). The 3 dp armed lamp at FOCUS is the only orange in the rail, and it does not move with sound |
| 21 | **Drawer open / close** (Guard/settings drawer off the band's PARK detent) [V] | **Drawer Slide.** Open: shaped noise, bandpass sweep 900→1,600 Hz, 120 ms · **−24 dB** — furniture moving in the next room, not an event. Close: same shape reversed, 100 ms · **−26 dB**. The quietest voiced events in the app; if a builder cannot hit −24 dB cleanly, ship haptic-only rather than louder | Open `[0,6,10,4] @ [0,110,0,70]` · **20 ms**; close `[0,5,8,4] @ [0,95,0,60]` · **17 ms** | Drawer panel travels on the SEAT curve and *arrives* (≥97% by 85% of duration) — the slide sound ends on the arrival frame, not after it. Scrim dims in the same travel; no separate scrim sound, ever |

### The overlays and the long events (see §4 for full sensory choreography)

| # | Event | Sound | Haptic | Visual pair (same 100 ms) |
|---|---|---|---|---|
| 22 | **Routine firing — he's present** (in-app / screen on, watching) [VM] | **Present Cue.** He can see it happen, so the machine whispers: MASS/Seal *seat stage only* — body 185 Hz, 60 ms + one pin tick 2,600 Hz at +74 ms. **~140 ms · −14 dB**. No strike stage, no repeat, no escalation. (Absent/pocket variant: full §4.3 cue at −4 dB class — specified there so no builder improvises it.) | `[0,10,60,18] @ [0,150,0,235]` · **88 ms** — breath, then seat. The two-beat shape is the signature; no notification breathes like this | Deck is caught **already winding**: NEXT ribbon flips to the fired routine, program arc seats, the fired loadout's cap is in place — mise en place made visible in the same beat as the seat pulse. If a manual session already holds: **no cue at all** — the row states `HELD — MANUAL SESSION` in print; a sound here would claim a firing that didn't happen |
| 23 | **Airlock interception tick** (blocked app caught, overlay rises) [VM] | **The Catch.** Abrupt, dry, clipped: noise burst hard-clipped at 12 ms + body 180 Hz, no tail, no melody. **12 ms · −5 dB**. Repeat intercepts within 60 s: **quieter and duller** (−9 dB, lowpass drops) — never louder; the machine does not train a game loop | `[0,12] @ [0,230]` · **12 ms** — one hard stop, matching the clip. No second pulse | Overlay rises on the SEAT curve and is *there*; the caught app's icon/row sits stopped mid-intent — geometry is the message. Full overlay choreography: §4.1 |
| 24 | **Hold — Bank 1 fills** (15 s held) [V] | **Ladder Step 1.** LADDER/Bank 1: body A3 220 Hz, 40 ms + noise tooth 1,800 Hz, 6 ms. **~45 ms · −10 dB** | `[0,10,8] @ [0,200,90]` · **18 ms** | First bank segment of the hold bar **seats solid** (fill becomes structure, not progress-bar liquid); hero countdown steps to the banked figure; the banked `15` prints and stays |
| 25 | **Hold — Bank 2 fills** (30 s held) [V] | **Ladder Step 2.** LADDER/Bank 2: body C4 261.63 Hz, 40 ms + noise tooth 1,900 Hz, 6 ms. **~45 ms · −8 dB** | `[0,10,8] @ [0,215,95]` · **18 ms** | Second segment seats solid beside the first; the hand feels the ladder gain a rung on the same frame the eye sees the bar gain a floor |
| 26 | **Hold — Bank 3 fills** (45 s held) [V] | **Ladder Step 3.** LADDER/Bank 3: body E4 329.63 Hz, 40 ms + noise tooth 2,000 Hz, 6 ms. **~45 ms · −6 dB** | `[0,11,8] @ [0,230,100]` · **19 ms** | Third segment seats; only the unbanked tail (45→60 s) remains visibly live. Three solid floors + one live tail *is* the state — no numeral needs to explain it |
| 27 | **Hold — slip / drain** (grip released or slips before 60 s) [V] | **Losing Height.** **800 ms grace — total silence, no drain** (a panicked hand is debounced, not punished). Then the unbanked tail drains at **2 banked-seconds per real second**; each bank boundary crossed downward replays its LADDER member **in reverse order, damped** (decay ×0.6, −4 dB under its fill voicing): Bank 3 → 2 → 1, a descending step every time a floor is lost. Banked floors (15/30/45) **never drain inside the attempt**. This is a staircase walked down, not an alarm, not a deflation whistle | One pulse per downward boundary: `[0,8] @ [0,160]` · **8 ms**, timed to the segment un-seating. Between boundaries: none — the drain is heard at its floors, not as a buzz | Tail fill recedes **linearly** (a measured thing moves linearly); each lost segment un-seats (solid → live → gone) on its pulse frame. Banking alone never consumes the weekly break — only completion (row 28) does |
| 28 | **Emergency break** (60 s completed) [VM] | **THE BREAK — the one loud event besides the seal.** Broadband crack: noise burst 3 ms at **−2 dB**, two falling body partials 700→300 Hz, 180 ms decay — then **300 ms of enforced near-silence** before any UI sound is allowed. Release voicing of the seal skeleton layered *under* it at 70% (body 205 Hz). **Total ~180 ms + 300 ms silence moat** | `[0,14,20,10] @ [0,255,0,180]` · **44 ms** + the release-seal waveform at 70% — the only event in the app allowed to stack two heavy feels | Hold bar empties in one travel; session surface unseals; the break reads as *breaking*, never as finishing — no completion language (no stamp, no strum, no wind-down) shares this frame. Form must never render a broken session as a finished one |
| 29 | **Session finish** (armed session completes naturally) [VM] | **Accession Stamp Knock.** MASS/Accession: body 98 Hz, 90 ms + noise lowpass 800 Hz, 16 ms · **−3 dB**, followed after 120 ms of quiet by one soft exhale body 262 Hz, 55 ms, noise-free · **−17 dB**. The stamp is the event; the exhale is its tail, not a second event. Fires **once**, keyed to the persisted session state transition — unreplayable by process death or re-entry. If backgrounded: same voice from the service/alarm path (the finish must not depend on the UI ticker being alive) | `[0,16,30,10] @ [0,240,0,120]` · **56 ms** — stamp strike, settle | Accession card lands / stamp mark strikes the receipt line **on the knock frame**; day total prints against the bar. Ceremony on state change only — never ambient, never on the daily arm path |
| 30 | **Day close** (the day's last finish / DAY CLOSED state entered) [VM] | **Accession Stamp, heavier strike.** Same MASS/Accession voice, strike gain **−1 dB**, exhale removed — the day does not exhale twice. **~90 ms**. Once per day, keyed to the DAY CLOSED transition; re-opening the app that night replays **nothing** | `[0,20,34,12] @ [0,255,0,140]` · **66 ms** | Chrome-free end-of-day Receipt presents; `DAY CLOSED` state prints. This is the screenshot frame — the stamp lands as the receipt settles, and then the app is done speaking for the day |

**Hold inter-bank ticks (rows 24–26 connective tissue):** between banks the
holding finger gets one **unpitched noise tooth per second** (1,600 Hz, 5 ms,
−18 dB; haptic `[0,3] @ [0,80]`, 3 ms) — presence, not progress. Pitch is
spent only at banks (§1.4). Final stretch 55–59 s: the tooth doubles its rate
(every 500 ms) at −14 dB — the mechanism is about to give, and the hand knows
before the eye does. No pitch ramp anywhere in the 60 s: the ladder is climbed
in steps or not at all.

**Composer focus (completes the Tasks desk):** focus = paper-slide (shaped
noise, highpass sweep 900→2,400 Hz, 140 ms, **−22 dB**; haptic `[0,4] @
[0,70]`, 4 ms) paired with the field's recess deepening one tone step over
180 ms linear. Blur with an un-added draft = the same slide reversed,
**−26 dB**, haptic **none** — the draft is *kept*, so the sound says "put
away," never "discarded." **Typing itself: silence** (§5) — the system
keyboard owns its clicks; doubling them lies about which object is sounding.

---

## 3. Engine rules the score assumes (build once, in the engine — never at call sites)

- **G1 — One owner, enforced.** Views fire contact feels; the ViewModel fires
  outcome feels. The old `fireFeel(hapticMs, freqHz)` bucket router is deleted:
  haptic duration is not a sound identity, and its discarded `freqHz` is why
  wave-1 sleds sounded identical ON and OFF. Callers call named feels
  (`feelAnchorTooth()`, `feelThrow(on)`, …). A call site that cannot name what
  physically happened gets no sound.
- **G2 — Two-component voice.** Every voice = **noise transient** (the impact;
  band/low-passed white noise, τ = noiseMs/3) + **body resonance** (sine +
  one partial at 2.76× @ 0.18). The wave-1 global recipe (4.7× partial @ weight
  2.2, two global decay constants) is retired — it made every mechanism ring
  as the same small bell, and its "48 ms seal" measured ~6 ms of actual sound.
  Decay is a property of the event, never of the engine.
- **G3 — Focus per gesture, never per tick.** One
  `AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK` held from touch-down to touch-up (wheel,
  slide, hold) and for long events; never acquired inside a tick loop. Under
  playing media, sub-10 ms voices duck to −20 dB under it or wait — a 6 ms
  tick never ducks a podcast.
- **G4 — Latest-wins queue.** At most 2 pending gesture-class transients; a
  newer tick evicts an older unplayed one. Long events (seal, break, finish,
  routine) are never evicted and never evict each other into a stack — if two
  long events collide, the later one waits for the earlier one's silence moat.
- **G5 — No-amplitude-control fallback.** If `hasAmplitudeControl()` is false,
  re-encode by duration: amplitudes ≥170 → full segment; 90–169 → segment
  ×0.6 duration; <90 → 2 ms pulse. Without this, half the fleet hears every
  row of §2 as the same on/off rattle. Where Composition primitives
  (CLICK / TICK / THUD / LOW_TICK) are supported, prefer them per-voice; fall
  back per-primitive, not per-device.
- **G6 — Gates live in the engine.** Session-mute, night scaling, ringer/DND,
  and the SOUND EFFECTS sled are engine-level gates (§5), so no future call
  site can forget them. The sled off = **both** components off, everywhere,
  including finish — there is no "important enough to override his switch."

---

## 4. The two overlays — sensory choreography (co-owned)

### 4.1 Airlock interception overlay
*Situation: he reaches for a blocked app mid-session. The Shield catches it.
This overlay is the HOLD standard made visible — it must never become a
punishment screen, a negotiation, or a game.*

| Beat | Sensory spec | Visual pair |
|---|---|---|
| **Catch (t=0)** | Row 23 in full: clipped Catch (−5 dB, 12 ms) + `[0,12] @ [0,230]`. The clip's hard end *is* the design — the sound stops the way the app stopped | Overlay fully risen on the SEAT curve within the same 100 ms; no fade-in tail after the clip ends |
| **Dwell (overlay up)** | **Silence.** No ambient hum, no countdown ticks, no breathing pulse. The 2-minute peek allowance, if offered, is offered in print and position only | One clock, in minutes; seconds appear only in the final minute, where they change a decision. No T-minus language, no abort vocabulary |
| **Peek granted (his tap)** | One Throw-Release (row 12) at 70% — a latch opening, not a reward. Nothing else | Overlay lowers; peek pill seats on Deck/running surface |
| **Peek expiry** | **Soft double-tick:** two Key-family ticks, 400 Hz body, 40 ms apart, **−13 dB**; haptic `[0,5,20,5] @ [0,110,0,90]` · 50 ms. The lease closing, stated once | Pill expires and the block re-seats in the same beat. Early return by his own tap: **silence** — he chose it, he can see it |
| **Return / dismiss without peek** | **Silence.** Leaving the overlay is not an event | Overlay exits on the SEAT curve; the session surface underneath has not moved, ticked, or aged audibly |

Repeat-intercept law (from row 23): within 60 s, each further Catch is
**quieter and duller** (−9 dB class, lowpass drops a step per repeat, floor
at −13 dB). Escalating volume would train tapping as a slot machine; decaying
volume teaches the hand that this door does not change.

### 4.2 Emergency-hold overlay
*Situation: the one weekly break, bought with 60 continuous seconds. BANKED:
15 s checkpoints bank progress; a slip drains the unbanked tail, never the
floors. Intent is tested, not grip.*

Full event rows are §2 rows 24–28; this is the overlay's continuous design:

- **Surface while holding:** the full-width hold bar (≥88 dp target, operable
  eyes-off — a round pad he must aim at is a target designed for calm hands)
  + hero-size countdown. The bar's fill is **linear** (LINEAR-MEASURED): an
  eased timer is a lying timer.
- **The hand's timeline:** pickup = Take-Up (row 6 voicing). Seconds 0–55:
  unpitched tooth per second (§2 note). Banks at 15/30/45 = Ladder Steps
  1/2/3 — the only pitched moments before the break. 55–59 s: tooth rate
  doubles. At no point does pitch *creep*: there is no ramp to creep along.
- **The slip:** 800 ms of grace silence first (debounce for a panicked hand),
  then the drain (§2 row 27) — descending Ladder members at the floors, linear
  visual recession, nothing between floors. A slip at 47 s costs the 2 s tail,
  not the 45 s banked. The sound of losing height is a staircase, walked down
  at walking pace.
- **The break:** row 28, plus its 300 ms silence moat enforced by the engine
  (G4) — no UI sound, including the disarm surface's own arrival, may speak
  inside the moat. What he hears after breaking his one weekly seal is,
  briefly, nothing. That nothing is the price tag, and it is honest.
- **Abandoning the overlay** (backing out without holding): silence. The
  overlay closes; the session still holds; nothing was spent and nothing
  is said.

### 4.3 Routine firing — the absent variant (companion to row 22)
Row 22 covers *present*. For completeness, so no builder invents a third cue:
**absent / pocket / screen-off** = the full two-breath cue — waveform
`[0,14,60,10,60,18] @ [0,210,0,150,0,235]` (162 ms), sound = seal strike
(240 Hz body, −6 dB class) → pin tick at +74 ms → seat body 185 Hz at +144 ms.
Identifiable through a pocket, never confusable with a notification (nothing
else breathes twice). **SLEEP firing:** same waveform, sound gains ×0.45,
bodies down a minor third (240→202 Hz) — a 22:30 firing may be heard by a
household. **Under DND: the firing degrades to nothing at all** — the record
carries it; the machine does not nag. One firing, one feel, no repeat, ever.

---

## 5. THE SILENCE LIST — everything that must make NO sound

Silence here means **no sound component**. Where a haptic survives, it is
named; everywhere else, no haptic either. This list overrides §2: if an event
appears in both, the implementation is wrong.

**While a session runs (HOLD absolute — the loudest law in this file):**
- Minute marks, halfway marks, block boundaries inside the session — silent
- Blocked-app deflections absorbed by the Shield (the Shield working *is*
  silent; a buzz per deflection punishes him for the app working) — silent;
  only the Airlock overlay's Catch (§4.1) speaks, and only when the overlay
  actually rises
- Task switches / queue edits attempted mid-session — no surface, no sound
- Countdown ticking, progress fills, escapement/heartbeat visuals — silent,
  always. A clock that ticks is a roommate, not an instrument
- AOD / desk clock — **permanently, unconditionally silent.** No exceptions,
  no settings path enables it
- The *only* sounds inside a session: the hold overlay's own ladder (§4.2),
  the Airlock Catch + peek expiry (§4.1), and the finish (row 29)

**Refusals and failures (no error feel exists in this app):**
- Slide released short (beyond row 10's single landing tick) — silent
- Hold released early (beyond row 27's drain) — silent
- Blocked / denied actions, invalid site entries, denied permissions — silent.
  The visual state carries refusal; the hand is never scolded. Android's stock
  double-buzz failure pattern is banned by name
- Empty-field ADD (beyond one dull paper tap at −20 dB, haptic `[0,5] @
  [0,80]` — a contact receipt, not an error) — no punch, no buzz

**Navigation and chrome:**
- Re-tapping the current band destination / selected chip / current stop —
  silent (no change, no feel)
- Screen opens, list scrolling (Shield apps, Tasks queue, Ledger receipts),
  sheet/dialog *invitations* opening (the outcome sounds; the invitation
  doesn't) — silent. The drawer (row 21) is the sole sheet exception, because
  it is furniture, not a dialog
- Theme toggle, DAY/NIGHT change — silent
- Sled/rocker/cap *travel* mid-flight (rows 13, 19) — silent until arrival

**Composition and passive motion:**
- Typing in composer / search / filter fields — silent (system keyboard owns
  its clicks). Search/filter *changes* — silent
- Settle animations, spring-backs (except row 10's landing tick), cap/pill
  animations in flight, computed-numeral changes — silent. The finger caused
  it; the finger already knows
- Long-presses that don't commit; slide-off cancellations of any press —
  silent

**Demoted wave-1 seal spenders (the seal no longer fires for any of these):**
task completion (→ row 18 paper stamp) · sub-step ticks (→ row 16 step note) ·
routine *edits* (→ throw/key at their own rows; only routine *firing* keeps
the seal) · profile/loadout applies (→ row 13 rocker) · add-custom-app,
backup export, undo-delete, peek-end-early, impulse-transmute, serial-plate
taps — **silent**; visual state already confirms each one.

**Global gates (engine-level, G6):**
- **Night** (Night renders, and 22:30–07:00 regardless of theme): all sound
  gains **×0.6**, all haptic amplitudes **×0.85** (rounded; minimum tick
  amplitude 70 — below that, drop the tail segment, never the strike).
  SLEEP firing follows §4.3's stricter ×0.45. Nothing is ever *louder* at night
- **Ringer silent / vibrate-only:** no sound component at all; haptics unchanged
- **DND:** no sound; haptics only for direct finger gestures (§2 rows 1–21);
  routine firing degrades to nothing (§4.3)
- **SOUND EFFECTS sled off:** both components off, everywhere, no exemptions
- **Daily budget:** ~40 feels / 10 s is the ceiling, reachable legitimately
  only by wheel spinning — and §2's thinning law is that ceiling's enforcement

---

## 6. What this score kills from wave 1 — named, with reasons

1. **Slide travel ratchet (16 rising teeth + friction loop).** Killed by the
   settled slide law: gate tick + seal thock + 120 ms dwell. Travel is now
   silent (row 7). A bolt that chatters for its whole travel spends the ear
   before the one moment that matters — the gate.
2. **Continuous pitch ramps everywhere** (wheel +6 Hz/step, slide teeth
   1,400→2,200 Hz, hold 380→840 Hz creep). Killed by the pitch-set law (§0.3):
   pitch computed from position is a formula, and formulas are banned. All
   three are replaced by fixed set members (§1.1, §1.4) or silence.
3. **Old hold arc (per-second ramp + silent early release + completion-pulse
   reward).** Killed by the BANKED law: discrete ladder up (§1.4), grace +
   descending ladder on drain (row 27), and the break as a *break* (row 28) —
   the wave-1 stack of completion-pulse + disarm-seal on this path was a
   reward sound for breaking the seal, which is a lie told in two voices.
4. **Completion triplet as the finish.** Superseded by the accession stamp
   (rows 29–30): finish is a state change and gets a stamp knock, rationed to
   state changes only. The triplet's falling exhale survives only as the
   stamp's quiet tail in row 29 — demoted from event to aftermath.
5. **Settle haptics/sounds.** Killed (rows 1–3 note): the last detent already
   announced the value; a settle feel is a phantom latch for an event that
   already happened. Double-announcement is double-firing by another name.

## 7. Tuning order (for whoever builds this — hardware is the final reviewer)

1. **Wheel tick** — spin it 100 times. If it annoys, halve the gain; never
   lengthen it.
2. **Seal** — it is the 0 dB reference; every other gain in §2 is tuned
   *against the shipped seal*, not against this file's numbers in isolation.
3. **Throw ON vs OFF, eyes closed** — if they can't be named blind, the
   asymmetry in rows 11–12 widens (ON brighter / OFF duller), never narrows.
4. **Ladder, eyes closed** — climb and drain must read as height gained/lost
   with the screen covered. If the drain reads as an alarm, dull it further.
5. **Finish from the next room; routine SLEEP cue from the next bed.** The
   two sounds allowed to travel are tested at the distance they must travel.

— Ton, Designers (haptics & sound). The score is complete when a blindfolded
user, after one week, can name every sound in §2 and is never surprised by a
sound that isn't in it.
