# 02 — INES · ARMING SURFACE (Slide · NEXT Ribbon · Mise en Place · Banked Valve)

**Seat:** Ines, Maximum-Implementable (coordinator: Voss) · Plan wave — plan only, no code.
**Hosts:** `SlideToStartView` in `ui/views/BlockitCoreViews.kt` (pinned dock, `TactileDeckScreen.kt`);
instrument card in `TactileDeckScreen.kt`; `Emergency60sValveView` in
`ui/views/BlockitSuitcaseAndSwitchesViews.kt`, hosted by `DisarmChallengeOverlay` in
`ui/components/AirlockAndDisarmOverlays.kt`; token/bank state in `ui/MonolithViewModel.kt`.
**Governing standards:** ARM ≤10s cold-open-to-sealed · HOLD absolute · TRUTH once, human language ·
one fact one rendering · beauty is retention · rested decides, tired executes · never green ·
orange = COMMIT fill only, and the slide thumb is the Deck's one orange.

**Round 0 state, named once so deltas are unambiguous:** slide commit fires
`playHarmonicSeal(true)` on release at `dragFraction ≥ 0.82` (linear 1:1 drag, thumb teleports to
finger on DOWN, gate event `fireFeel(8L)` at 0.82 both directions, no hysteresis), then sets
`dragFraction = 1` for one frame and **springs back to rest on success**; badge is set by the host
to `"${dialMinutes}M"` — the *block* length, not the total; label+badge die at `frac ≈ 0.77` via
`1 − 1.3·frac`; `isArmedState` resets the thumb to 0 and no host ever sets it. The valve is
continuous-or-zero: one `holdProgress` float, release sets it to `0f` instantly, per-second ticks
are canned latch/click with no pitch path, completion fires `fireFeel(10L)` in the view *and*
`playHarmonicSeal(false)` in the ViewModel (double sound). The weekly token is an in-memory
`StateFlow(1)` — process death refunds it. Banking does not exist in code.

---

# ELEMENT 1 — SLIDE CLIMAX

## The conflict, resolved (read this first)

Three numbers were fighting over one line:

| Source | Number | What it actually measured |
|---|---|---|
| Wave-1 (my own spec) | commit at **finger-raw 0.90** | finger travel under a 2:1 strain mapping (display 0.83) |
| SYNTHESIS item 18 | commit at **~0.65 travel**, latch comes to the thumb | display travel; humans' ceiling; NO strain wall |
| Round 0 (shipped) | gate event at **0.82** | display travel on a linear drag (= raw, no mapping) |

**Resolution — one machine, two landmarks, one commit rule:**

- **The GATE is at display 0.65.** Crossing it is a *warning and an offer*, not a commitment:
  the pawl drops, the label flips to `RELEASE TO ARM`, the receiver lights, the latch is armed.
- **The point of no return is not a position. It is the RELEASE.** Releasing the finger while
  the gate is latched commits; releasing anywhere else cancels. While the finger is down the
  gesture is always reversible — drag back below the gate exit (display 0.615) and the offer is
  withdrawn. A tired user at 03:30 must be able to change his mind *with the finger still down*
  at zero cost; the machine may only become irreversible at the exact moment he lets go.
- **Round 0's 0.82 is abolished as a threshold.** It survives nowhere — not as a second gate,
  not as a hidden check. Two commit-flavoured landmarks on one slot would be two renderings of
  one fact, and the second one would be a lie about where the decision lives. (Zug-II defended
  0.82 in mail; answered there — shipped-code seniority is not a truth argument, and 0.82 on a
  linear drag costs a depleted thumb ~203dp of travel before the machine helps him. The gate
  moves to the thumb; the thumb does not crawl to the gate.)
- **Wave-1's raw-0.90 commit dies with its strain wall.** Humans vetoed the 2:1 zone; without
  it, raw 0.90 is just a long drag. Finger travel to the gate under the gentle mapping below is
  **raw 0.67** — the hand and the eye now agree about where the decision is.

## Mechanism

Geometry, track, channel, printed scale, receiver, graded trail, thumb cap (ribs, face shading,
groove, marker line at `right − 10dp`, 40dp span) are inherited from wave-1 §§1–2, 5 unchanged,
with exactly three numeric deltas: **(a)** the gate tick moves from display 0.83 → **0.65** (still
the tallest/brightest floor tick: 10dp tall, 2dp wide, alpha 235 day / 225 night); **(b)** a seat
tick at display 1.00 is the receiver end-wall itself (already specced — no new print); **(c)**
trail "hot" gradient and receiver-notch lighting key on `gateLatched` (0.65) instead of 0.83.

**Grab.** DOWN engages only inside `thumbRect` expanded 12dp horizontally, full height; a down
elsewhere returns `false`. On engage: `grabOffsetPx = event.x − thumbCenterX`, animators
cancelled, `VelocityTracker` obtained (pattern already in `RotaryWheelView`, same file),
`fireFeel(4L)` grab latch. No teleport, ever — the cap is taken hold of, not summoned.

**Resistance mapping — gentle only (the exact mapping).** Finger `raw` → displayed `dragFraction`:

```
raw ≤ 0.50        →  display = raw                          // 1 : 1    free travel
raw > 0.50        →  display = 0.50 + (raw − 0.50) × 0.88   // 1.14 : 1 load, felt, never a wall
```

Continuous and monotonic; the slope changes once, by 12%. Landmarks:

| Finger raw | Thumb display | Event |
|---|---|---|
| 0.50 | 0.50 | halfway firm click; label becomes a live % reading |
| 0.67 | **0.65** | **GATE** — pawl, label `RELEASE TO ARM`, receiver lights |
| 1.00 (finger at end wall) | 0.94 | thumb cannot be dragged to 1.0; the last 6% exists only as latch travel |

The resistance is a *lean*, not a tax: at gate entry the thumb lags the finger by 2% of travel
(~5dp at 360dp width). It registers in the hand as weight. It never decides anything.

**Detents.** Distance-based on marker-line travel: fire when the marker has moved ≥14dp since
the last tick outside the gate, ≥7dp inside it (density doubles where the decision lives).
Pitch via the only pitched path: `fireFeel(1L, freq)`, `freq = 360 + display × 480` → 360Hz at
rest, 672Hz at the gate, 811Hz at finger-end. Throttle ≥28ms between detent fires
(`lastDetentAtMs`). Reverse travel: same rules, pitch falls — the scale reads backwards.

**Commit (assisted latch travel).** On `ACTION_UP` with `gateLatched == true` and no backward
fling (below): the finger leaves, **the latch completes the stroke**. `commitAnimator` drives
`dragFraction` from its current value to **1.0**, `CurveB`, duration by distance:
travel ≤ 0.18 → **180ms**; travel > 0.18 → **260ms**. At the seat frame the marker line coincides
with the receiver edge (the −10dp coincidence from wave-1 §2.4), the seal fires, the label flips
to `ARMED`, receiver notch alpha steps to 44 (day) / 36 (night). Then a **120ms seated dwell** —
nothing moves — then `onSlideCommitted` fires exactly once (guard `commitFired`, reset on DOWN).
**No spring-back on commit, ever.** The gesture of no return does not return.

**Fling-commit rule.** `VelocityTracker` velocity at release, converted to dp/s:
- `velocityX ≥ +1200 dp/s` **and** `display ≥ 0.50` → COMMIT via the same assisted travel
  (a thrown fader seats; below display 0.50 a fast flick is a slip, not an intent).
- `velocityX ≤ −800 dp/s` → **CANCEL, even if gate-latched.** A backward fling is a changed
  mind expressed at speed; the release veto beats the gate latch. The label said "release to
  arm" — a finger leaving *leftward at 800dp/s* was not releasing to arm, it was escaping.
- Otherwise the gate rule decides. `ACTION_CANCEL` → cancel, silent variant.

**Cancel path.** Release below the gate (or backward-fling veto): spring-back on
`MonolithCurves.CurveC` (fast release, long damped settle — mass, not rubber; `MechanicalOvershoot`
is deleted from this view), duration by release point, distance-honest:
display < 0.25 → **180ms** · 0.25–0.50 → **260ms** · > 0.50 → **420ms**.
`playMechanicalLatch(false)` at spring start (direct engine call; `fireFeel` cannot express
`false`), one `fireFeel(1L, 340f)` home tick when the marker lands on the zero tick.
`ACTION_CANCEL`: same spring, **no** release sound, **no** home tick — silence separates
"system interrupted" from "user changed his mind".

**Beckon.** Wave-1 §6.5 unchanged: max 2 per view instance, never after a commit, 0 → 0.045 → 0,
520ms `CurveC`, silent, skipped when animator duration scale is 0.

## Motion (curve + ms)

| Beat | Curve | Duration |
|---|---|---|
| Drag | finger-driven (mapping above) | — |
| Assisted latch travel | `CurveB (0.05, 0.7, 0.1, 1)` | 180ms (travel ≤0.18) / 260ms (>0.18) |
| Seated dwell | — | 120ms |
| Cancel spring | `CurveC (0.3, 0, 0.8, 0.15)` | 180 / 260 / 420ms by release point |
| Armed latch park (`isArmedState` setter) | `CurveA (0.2, 0, 0, 1)` | 260ms |
| Latched teaser spring-back | `CurveC` | 260ms |
| Beckon | `CurveC` | 520ms |

Total commit latency from release: ≤ 260 + 120 = **380ms**. Grab-to-gate is one ~166dp swipe,
well under a second. ARM standard (≤10s cold-open-to-sealed) is met by the gesture alone with
an order of magnitude to spare; nothing on this surface may add a step to that path.

## Haptics (per beat)

| Beat | Call | Waveform (engine-fixed) |
|---|---|---|
| Grab | `fireFeel(4L)` → `playMechanicalLatch(true)` | [0,11,26,9]ms / [0,215,0,150] |
| Detent | `fireFeel(1L, 360 + display×480)` | [0,7,5]ms / [0,180,70], ≥28ms apart |
| Halfway (display 0.50 crossing, edge, re-arms on recross) | `fireFeel(8L)` → `playFirmClick()` | [0,13,20,8]ms / [0,225,0,135] |
| Gate entry | `fireFeel(8L)` → `playFirmClick()` | as above — one pawl, one click |
| Gate exit | `fireFeel(1L, 300f)` | low detent, the pawl lifting |
| Commit seat | seal (below) — the only full-amplitude strike on this surface | [0,24,38,16]ms / [0,255,110,175] |
| Cancel release | `playMechanicalLatch(false)` direct | [0,9,22,10]ms / [0,165,0,195] |
| Cancel home arrival | `fireFeel(1L, 340f)` | low detent on the zero tick |
| System cancel | — | silence |

## Sound (primitive + pitch)

`GRAB = playMechanicalLatch(true)` 760Hz/16ms · `DETENT = playRotaryDetent(360→811Hz by display)`
6ms, gain 0.38 · `HALFWAY = playFirmClick()` 620Hz/12ms · `GATE IN = playFirmClick()` ·
`GATE OUT = playRotaryDetent(300Hz)` · `COMMIT SEAT = playHarmonicSeal(arming = true)` —
360Hz fundamental, 48ms, gain 0.62, chord 1.5, called **directly** on `soundEngine` at the seat
frame (not at release: sound and picture coincide on the seat, per Cato's zip-honesty constraint)
with `fireFeel(10L)` as the no-engine fallback only · `CANCEL = playMechanicalLatch(false)`
520Hz/16ms · `HOME = playRotaryDetent(340Hz)`. `playCompletionPulse` never plays in this view.

## Depth & light

Inherited whole from wave-1 §§2, 5, 9: milled channel (black overlay 0x2A day / 0x40 night,
1dp top inner shade, 1dp bottom catch-light), printed floor scale (minor 4dp / major 7dp /
gate 10dp; passed ticks lit 150/200), receiver end-wall hairline + notch, graded trail
(30→96 day / 22→72 night, gate-hot +24), thumb contact shadow +2.5dp `0x4D000000`, lit top edge
`0x66FFFFFF` 1.2dp, pushed-face strip black α46, trailing strip white α20, three grip ribs
`0x2E000000`. Light source fixed from above; specular is a function of position only — the trail
"heats" at the gate because the *gradient swap* is keyed to a state, and that is the one
sanctioned exception: it confirms a decision boundary, it does not encode progress (progress
is already rendered by position and the lit scale). Day thumb `tokAccent` #F55314, night
#F47442 — the Deck's single orange, a fill, never text.

## State machine

**Label (edge-triggered on stage change, 0.015 hysteresis on both thresholds):**

| Stage | Condition | Label |
|---|---|---|
| S0 | display < 0.50 | host label — `SLIDE TO ARM SESSION` (Atkinson Bold 12.5sp) |
| S1 | 0.50 ≤ display < 0.65 | live reading `"${(display×100).toInt()}%"` (Anonymous Pro Bold 12.5sp — a measured number, mono) |
| S2 | gate-latched | `RELEASE TO ARM` (Atkinson Bold 12.5sp) |
| S3 | assist travel | `RELEASE TO ARM` holds — the arm is not true yet, the words do not run ahead |
| S4 | seat frame → dwell end | `ARMED` |
| S5 | `isArmedState` latched | `SESSION ARMED` |

Label rendering: extinguish-by-passage (two clip passes at marker x, passed segment at 25%
alpha) and global fade `1 − display × 0.85` floored at 0.15 while dragging — wave-1 §6.1/6.2.

**Badge — TRUTH rule (the brief's, made mechanical): the badge repeats the TOTAL or it dies.**
The host currently sets `"${dialMinutes}M"` (block length). That is a lie whenever blocks > 1:
at 25×4 the badge says `25M` and the slide commits 100 minutes. New host contract
(`TactileDeckScreen` update block — the only host change in this element):

```
total = dialMinutes × studyBlocksCount
badge = FLOW mode            → "FLOW"          (no number — stopwatch has no total to repeat)
        total < 60           → "${total}M"     e.g. 50M
        total % 60 == 0      → "${total/60}H"  e.g. 1H, 3H
        else                 → "${total/60}H ${total%60}"  e.g. 1H 40
```

Badge never fades (alpha 255 always — scale print does not fade), Anonymous Pro Bold 11.5sp,
right-aligned `W − 18dp`; at gate entry a 1.5dp target-lock rule appears under it, width =
badge width, alpha 200, gone on gate exit. The badge is occluded by the arriving thumb — that
occlusion is the arrival.

**Gate:** `gateLatched` enters at display ≥ 0.65, exits at display < 0.615 (3.5% hysteresis —
the boundary cannot chatter). Entry/exit drive: receiver notch, hot gradient swap, marker line
3dp → 4dp, label S2, gate sounds. **Armed latch:** wave-1 §10.1/10.2 adopted whole — setter
parks the thumb at 1.0 (CurveA 260ms, or instantly pre-layout), trail steady (not gate-hot),
marker line mirrors to `thumbRect.left + 10dp` in `tokOnSecondaryContainer()` (fixes the
night white-on-peach invisibility), reverse teaser ≤6dp at the load ratio with no callback —
**this view is never a disarm path**; grace and emergency live in Element 4's world.

## Edge cases

Re-grab mid-spring or mid-assist: DOWN cancels both animators, `rawFraction` re-derived from
current `dragFraction` via the inverse mapping — continuity, no jump. Detach during dwell:
`commitFired` guard + animator/runnable cleanup in `onDetachedFromWindow`; the callback fires
zero or one times, never two. View re-used after commit (host unmounts it today, but): any DOWN
resets `commitFired`. Multi-touch: first pointer owns the drag (`activePointerId`); other
pointers ignored. Finger slides off the view vertically mid-drag: `ACTION_CANCEL` path — silent
spring, no penalty. Release exactly at gate boundary: hysteresis state at release decides —
if `gateLatched` is true it commits, even if the final position sample reads 0.648.

## Code: helps / fights

**Helps:** seal already routed to `playHarmonicSeal(true)`; gate event exists mid-drag;
`VelocityTracker` pattern exists in `RotaryWheelView` (same file); `soundEngine` is a public
inherited var — direct calls need no engine change; curves/durations all in vocabulary.
**Fights:** linear `updateDrag` with thumb teleport on any DOWN (rewrite); commit branch sets
`dragFraction = 1` then springs back (delete — replaced by `animateCommit()`); label fade
`1 − 1.3·frac` kills the instruction before the gate (replace); `isArmedState` setter resets to
0 (invert); host badge line in `TactileDeckScreen` (`"${dialMinutes}M"`) — one-line lie, fix to
the TOTAL formatter above; `animateSpringBack`'s `MechanicalOvershoot` (swap to CurveC).

## Law check

ARM ≤10s ✓ (one swipe + ≤380ms). HOLD absolute ✓ (no disarm path on this view; teaser fires
nothing). TRUTH once ✓ (badge = total, the readout's own number — the readout owns the total,
the badge *repeats* it at the point of commitment, which is the sanctioned exception the brief
states: repeat the total or die; gate tick is the only threshold mark; % stage is the drag's
own position, rendered once, where the hand is). One orange ✓ (thumb fill only; gate tick is
cream, never orange). No green ✓. Beauty/retention ✓ (the climax is the retention: resistance,
gate, snap, thock, dwell — a gesture that feels like a latch seats in memory at 03:30).

---

# ELEMENT 2 — NEXT RIBBON (under the readout)

## Mechanism

One printed line inside the Deck instrument card, directly under the readout's total caption
and above the wheel — the card's last line of print before the instrument. It renders exactly
one fact: **the next thing the scheduler will do without him.**

**Content grammar (exact):**

```
NEXT {HH:MM} · {LOADOUT} · FIRES ITSELF
```

- `HH:MM` — 24-hour, zero-padded, from `nextStartMillis` (Anonymous Pro — numbers are mono).
- `LOADOUT` — the routine's `loadoutName` mapped to one word: `STUDY` / `WORK` (WORK_SPRINT) /
  `SLEEP`. The routine's free-text *name* is never printed here (names are his prose, can be
  any length; the loadout is the scheduler's fact).
- `FIRES ITSELF` — fixed words, printed only when the truth rules below all hold.
- Day qualifier when the next start is not today: the grammar becomes
  `NEXT {DAY} {HH:MM} · …` with `DAY` = `TOMORROW`, or the 3-letter weekday (`FRI`) beyond
  tomorrow. A time with no day, printed at 21:00 for a 05:00 firing, is a lie of omission.
- Field order never changes; separators are always ` · `; the line is centred, single line,
  never wraps (loadout word max 5 chars keeps worst case ≈ 34 glyphs at 10.5sp — fits 360dp).

**Data source.** Computed in the ViewModel, passed to the Deck as one immutable value
(`nextRoutineLine: NextRoutineUi?`): among `routines` where `enabled && daysMask != 0`, take
`min(nextStartMillis(routine, now))` via `RoutineSchedule.nextStartMillis` (pure, already in
`MonolithModels.kt`); tie-break: earlier window end, then lower `id`. The Deck never computes
schedule math itself. Recompute triggers: routines flow emission, Deck composition, a 60-second
tick while the Deck is visible, and any session-state change. The same computation feeds
Element 3 — one source, two renderings is forbidden, so mise en place consumes the *state*,
never re-derives the line.

## Motion (curve + ms)

None of its own. The ribbon is print, not an object: on value change the text **re-strikes in
place** (instant swap, no fade, no roll — a computed/scheduled number never animates through
intermediate values). Screen transitions carry it (Deck enter/exit, Compose specB 420ms) and
that is all.

## Haptics

None. Ever. A line of print he reads does not touch him back.

## Sound (primitive + pitch)

None. The firing itself is paid elsewhere (Element 3); the ribbon is silent in both directions.

## Depth & light

Flat letterpress on the card surface, matching the card's existing caption treatment:
Anonymous Pro + Atkinson Hyperlegible Bold **10.5sp**, letterSpacing 1sp, colour = text-secondary
tier (day `onSurfaceVariant`; night #9C8E84, the 5.8:1 staircase tier — never the 3.5:1
decorative tier, because this line is decision-bearing), 1dp top highlight shadow in day mode
(the file's existing letterpress pattern), flat at night. No fill, no pill, no border, no
orange, no icon. Vertical: 8dp below the total caption, 10dp above the wheel's top edge.

## State machine

Precedence (first match wins; the ribbon slot shows exactly one state or is absent):

| # | State | Rendering |
|---|---|---|
| 1 | Session running | **Absent.** The running readout owns the card; a NEXT line under a live session would be a second present tense. (Includes routine-fired sessions.) |
| 2 | Inside a routine window, no session running (window fired, session broken/ended early, or fire was skipped) | **`WINDOW OPEN · {LOADOUT} · ENDS {HH:MM}`** — see Element 3 demotion; the slide slot changes with it |
| 3 | Next routine exists | `NEXT {HH:MM} · {LOADOUT} · FIRES ITSELF` (+ day qualifier as above) |
| 4 | Next is SLEEP | Same grammar, loadout word `SLEEP`: `NEXT 20:00 · SLEEP · FIRES ITSELF`. No special colour, no moon, no ceremony — sleep is a routine, printed like one |
| 5 | No routine today, next exists later | Grammar with day qualifier (state 3 covers it — "no routine today" is expressed by the qualifier, not by a separate lament) |
| 6 | No enabled routine at all (or all `daysMask == 0`) | **`NO ROUTINE SET — NOTHING FIRES ITSELF`** — one line, human, final. Not a CTA, not a button; setting routines is Shield's job and this line does not borrow its verb |

## Edge cases

Routine disabled between compute and fire: the 60s tick + flow emission recompute; a disabled
routine's line never survives a recompute. Two routines at the same start: tie-break above;
the loser is Shield's row problem, not printed here (one fact, one rendering). Window that
started yesterday and is still open (SLEEP 20:00–03:30 at 01:00): `isActiveNow` handles the
overnight wrap — state 2, `ENDS 03:30`. Clock change / timezone travel: recompute on
composition covers it; no cached line survives a Deck revisit.

## Truth rules (binding)

1. `FIRES ITSELF` prints only if the routine is enabled, has a non-zero days mask, **and**
   `RoutineScheduler` has actually armed its START alarm (`scheduleRoutine` ran for it). A
   routine that exists in the database but whose alarm was never armed gets no line — the
   ViewModel must derive the line from the same list it hands the scheduler.
2. The printed time is the scheduled boundary from `nextStartMillis`. The scheduler uses
   `setAndAllowWhileIdle` — under Doze the fire can land minutes late. The line claims
   *agency*, not minute-precision: it says the machine fires, not that it fires on the second.
   No "AT" prefix, no countdown, no seconds — nothing that implies precision the alarm
   doesn't have.
3. If a manual session is already running when the boundary passes, the receiver skips the
   fire (never steamrolls). The ribbon is absent while running (state 1), so it can never be
   caught promising a fire that a live session will veto.
4. The ribbon never prints a duration, a count, or a total — those have homes (readout,
   Shield rows). It never prints the word `LIVE` or `RUNNING` for a window; a window is not
   a session.

## Code: helps / fights

**Helps:** `RoutineSchedule.nextStartMillis` / `isActiveNow` are pure and complete, including
overnight windows; `routines: StateFlow<List<RoutineEntity>>` already exists in the ViewModel;
`RoutineScheduler.scheduleRoutine` is the single arming point (truth rule 1 has exactly one
place to check). **Fights:** `TactileDeckScreen` receives no routines/next-start parameter
today (host must accept the computed line value); the card's `FINISHES` line uses 12h
`h:mm a` while this grammar is 24h — flagged, not fixed here: the ribbon follows the brief's
`09:00` grammar; unifying FINISHES is Dario/readout's call, and two clocks on one card is a
known wart this spec refuses to hide. No periodic tick exists on the Deck (add a 60s
`LaunchedEffect` loop — Deck-lifetime only).

## Law check

TRUTH once ✓ (one line, one fact, sourced from the scheduler's own inputs). HOLD ✓ (no action,
no verb for the user — it is not tappable). One rendering ✓ (Shield rows render the schedule
as a list; the ribbon renders *imminence* — different fact, and it borrows no row data beyond
the single next start). Beauty ✓ (letterpress restraint; the line earns its place by being
the only print that tells him the future is handled).

---

# ELEMENT 3 — MISE EN PLACE + ROUTINE-WINDOW DEMOTION

## Mechanism

**Mise en place** is the Deck laying out its own prep before a routine fires — the rested
user's decision arriving pre-assembled for the tired user. It is a *state of the existing
Deck controls*, not a new surface, banner, or mode screen.

**Window:** mise begins at `nextStartMillis − 30 min` and ends at the fire. Only when the
Deck is idle (no session running) and the next routine's loadout is **not SLEEP** (sleep has
nothing to lay out — no dial, no blocks; the ribbon prints it and the Deck stays as he left
it). One firing gets one mise; the state is keyed by `(routineId, startMillis)`.

**What is pre-laid — exactly three things, each from its real source:**

1. **Duration** — the wheel/readout move to the routine's window length:
   `RoutineSchedule.durationMinutes(routine)`, clamped to the wheel's 5–180 range. The 05:00
   fasted block (180 min) lands exactly on the wheel's max. The readout total updates *because
   the wheel moved* — the number's only home stays the readout; mise prints nothing.
   Study blocks count resets to 1 (the receiver starts routine sessions at
   `studyBlocksCount = 1` with the window as one duration — the Deck must match the session
   the fire will actually create, or the laid-out Deck is a picture of a different session).
2. **Loadout** — the loadout cap (Marko's element) shows the routine's `loadoutName` as a
   **preview**: cap position/fill as if selected, driven by a `previewLoadout` input, visually
   distinguished only by the ribbon already saying NEXT (no badge, no outline, no second
   marker). **Nothing is applied.** `applySmartLoadout` mutates real app tiers — calling it
   30 minutes early would change what his phone blocks before the window, a side effect
   dressed as a preview. The real apply happens at fire time (receiver) or at his slide.
3. **Task** — the Deck task line surfaces the queue's head: the first uncompleted task (the
   ViewModel's active/next task in queue order) is shown as the session's first task, in the
   task line's normal rendering. If the queue is empty, the task line shows the routine's
   name (the receiver uses `routine.name` as the session intention — the laid-out Deck again
   matches the session that will exist).

**His slide during mise** arms what is laid out: duration = window length (or, if he arms
*after* the start boundary but the fire was skipped — see demotion — the remainder rule
below), loadout = routine loadout (applied at arm, by the normal arm path), first task as
shown. If he arms manually before the boundary, the receiver later finds an active session
and does not steamroll — the mise simply becomes his session.

**Override rule.** Any manual touch of wheel, loadout cap, or task line during mise cancels
mise for this firing (`miseOverridden(routineId, startMillis)`): the machine stops re-laying,
his values stand, the ribbon keeps printing (the fire is still coming — that fact didn't
change). Rested or not, a hand on the instrument is a decision; the prep serves, it doesn't
argue. Mise never re-asserts after override.

## Motion (curve + ms)

The pre-lay is **not animated as an event**. If the Deck is composed already in mise state, it
simply *is* laid out (no motion — he opened the app and the prep was done; that is the point).
If the boundary into mise (T−30) passes while he is looking at the Deck: wheel settles to the
new value with its own existing settle (wheel owner's spec), readout re-strikes in place,
loadout cap travels by its own cap motion. No Deck-level choreography, no crossfade, no sound
of arrival. A preparation you have to watch happening is a performance, not a preparation.

## Haptics

None for entering or leaving mise. (The wheel's own detents fire if the wheel animates under
its owner's rules while he watches — that is the wheel's voice, not a mise event.)

## Sound (primitive + pitch)

None. Silence is the spec: prep makes no noise, and the fire's payment is the session-start
soundscape he already knows (receiver → shield start), unchanged by this element.

## Depth & light

No new surfaces, fills, or tones. The preview loadout uses the cap's own selected-state depth
(Marko's spec) — depth belongs to the control, not to the mise state. Nothing glows, pulses,
or warms as the fire approaches; there is no countdown rendering anywhere in mise.

## State machine

```
IDLE ──(next start ≤30min away, loadout ≠ SLEEP, Deck idle)──► MISE LAID
MISE LAID ──(any manual control touch)──► MISE OVERRIDDEN (Deck = his values; ribbon persists)
MISE LAID ──(boundary fires)──► session running (receiver's, or his if he slid first)
MISE * ──(routine disabled / rescheduled)──► IDLE (Deck values stay wherever they are —
             mise never un-lays; unmoving is not a lie, the values are still valid choices)
INSIDE WINDOW (isActiveNow && no session) ──► DEMOTED (below)
```

**Routine-window demotion — exact behaviour.** When an enabled routine window is open
(`RoutineSchedule.isActiveNow`) and no session is running, the pinned 68dp slide is
**replaced in its own slot** (same pinned Box, height 58dp to match the running-state chips —
the slot's geometry family, not the slide's 68dp; the change of height is itself a quiet
signal that the instrument changed):

- **STUDY / WORK window:** a centred text action, no fill, no orange, no border:
  `ARM THE REST OF THIS WINDOW` (Atkinson Hyperlegible Bold 12.5sp, text-primary tier) with
  a right-aligned mono suffix `ENDS {HH:MM}` (Anonymous Pro Bold 11.5sp, text-secondary).
  **Tap** (not slide) arms a session for the **remaining window**: duration =
  `window end − now`, in whole minutes, minimum 5 (below 5 remaining minutes the action is
  absent and the slot shows the static line below), loadout = the window's routine loadout,
  intention = routine name. Why no slide: the commitment was already made by the rested user
  who created the routine — re-demanding the full commit gesture inside his own window
  re-asks a settled question, and the Deck's one orange spending itself inside an
  already-decided window would render "commit" twice for one decision.
- **SLEEP window:** no arm action at all. The slot shows a static centred line
  `SLEEP WINDOW · ENDS {HH:MM}` (Atkinson Bold 11.5sp, text-secondary tier), untappable.
  Arming a study session from inside the sleep window contradicts the rested decision the
  window *is*; if he truly wants one, Shield lets him pause the routine — a rested-venue
  decision, made where routines live, not a tired tap on the Deck.
- Demotion exits the instant a session starts (the slot becomes the normal running chips:
  grace, then the emergency-break chip) and the instant the window ends (the slide returns,
  restored exactly as it was — mise/demotion never mutate the slide's own state).

## Edge cases

Mise window opens while a manual session is running: mise never starts (Deck isn't idle);
the receiver also skips its fire — consistent silence. Two routines, next is SLEEP but a
STUDY routine follows within 30 min of the sleep start: mise keys on the *next* start only;
sleep's start wins, no mise — correct: at the sleep boundary the phone seals for sleep, and
the study routine's mise belongs to *its* T−30, computed after. Routine window opens while
Deck shows demotion for a *different* (earlier) window: windows can't overlap in
`isActiveNow` evaluation order — take the window with the earliest end; if two are somehow
active, the STUDY/WORK demotion beats the SLEEP static line (an actionable truth beats a
passive one). Remaining-window arm when `end − now` > 180 min (long window entered late is
impossible — remainder only shrinks; early entry remainder can exceed the wheel max, but the
demoted arm doesn't consult the wheel at all: duration goes straight to session creation).

## Code: helps / fights

**Helps:** the receiver already implements the fire semantics mise must mirror (window
duration, blocks = 1, loadout apply, intention = routine name, no-steamroll); `isActiveNow`
+ `nextEndMillis` give demotion its window and its ENDS time; the pinned slot already swaps
content by session state (`TactileDeckScreen` §5), so demotion is one more branch in a
switch that exists. **Fights:** the Deck has no mise/routine inputs today (depends on
Element 2's ViewModel value + a `deckRoutineContext` carrying routine, window end, and
mise state — Sten owns the firing data contract; mailed); `applySmartLoadout` is
mutate-on-call, so the preview must be a pure display input to the cap, never that call;
wheel value is user-owned state (`dialMinutes` StateFlow) — mise writing it must go through
a flagged path so the override rule can distinguish machine writes from his touches
(a `miseWriteInProgress` guard around the setter, or the override listener starves).

## Law check

Rested decides, tired executes ✓ — this element *is* that law, mechanised: the routine
(rested) lays out; the tired user slides once or, inside the window, taps once. TRUTH ✓
(the laid-out Deck is byte-identical to the session the fire creates — duration, blocks,
loadout, intention — verified against the receiver's `startSession` call, not against
intention). One fact one rendering ✓ (mise prints nothing; it *sets*). ARM ≤10s ✓ (mise
removes setup from the arm path entirely). HOLD ✓ (demotion offers less mid-window, never
more). Dependencies flagged: **Sten** (firing-data contract — mailed), **Marko** (preview
input on the loadout cap), **Ledger fate OPEN** — mise/demotion record nothing themselves;
whether a demoted arm or a skipped fire is *logged* is the Ledger's question, not specced
here. **Guard fate OPEN** — no dependency: nothing in this element touches Guard surfaces.

---

# ELEMENT 4 — THE BANKED-HOLD VALVE (emergency break)

His settled rule, stated once, exactly: **a full 60 seconds of holding ends the session
early; progress banks at 15 / 30 / 45 seconds; a slip drains only what isn't banked, down
to the last bank; the weekly token (1) is spent only by a completed break; a released hold
costs nothing but the drained time.** Current code implements none of the banking — this
element is the rule existing, then becoming legible.

## Mechanism

**State (the engine).** Two numbers, owned by the **ViewModel** (not the view — the bank
must survive the overlay closing, rotating, and the view being recreated):

- `bankedSeconds ∈ {0, 15, 30, 45}` — the last bank crossed. Never decreases inside an attempt.
- `heldSeconds: Float ∈ [0, 15)` — seconds accrued above the bank in the current grip-cycle.
- Truth total = `bankedSeconds + heldSeconds`. Completion at total = 60 exactly.
- Attempt lifecycle: bank state is born at session start (0/0), **persists across overlay
  dismiss/reopen within the same session**, and dies when the session ends by any path
  (completion, break, or natural end). A bank that evaporated when the overlay closed would
  be the app lying about what it owes him.

**Accrual.** While touching: the view's existing 32ms runnable pattern, elapsed from
`segmentStartRealtimeMs` (`SystemClock.elapsedRealtime`). Crossing a bank boundary moves
15s from held into banked and fires the bank beat (below). Accrual pauses during grace and
re-seat (below) — frozen time is never counted as held time.

**Slip grammar.** On `ACTION_UP` / `ACTION_CANCEL`:
1. **Grace — 800ms, no drain, no accrual.** A panicked hand's micro-slip, a palm shift, a
   sensor dropout: the machine debounces before it prices anything. Re-grab inside grace
   resumes accrual from the exact held value, no penalty, no re-seat delay. (Grace cannot be
   farmed: it grants no time, it only delays loss — progress still requires 60 actual held
   seconds above banks.)
2. **Drain — after grace, held drains at 2 banked-seconds per real second** (the maximum
   unbanked exposure, <15s, fully drains in <7.5s — long enough to re-grab against, short
   enough to cost). Drain is driven by the same 32ms runnable while the overlay is open.
   The drain **stops dead at the bank floor**: banked seconds never drain, inside this
   session, under any input. Re-grab mid-drain halts the drain at the current held value
   instantly; accrual resumes after a **250ms re-seat** (contact must prove itself before
   it earns again).
3. **Overlay dismissed mid-drain or mid-grace:** held settles to the bank floor immediately.
   He wasn't holding while he was elsewhere; a slow invisible drain behind a closed overlay
   would be a number changing for no witnessed reason. The bank — the part that matters —
   is untouched.

**Target.** The valve bar is **88dp tall** (view height 88dp for the bar itself; hero block
above it — layout below). The whole bar is the touch target, full width: a hold target you
must aim at is a target designed for calm hands. Touch is accepted anywhere on the bar;
there is no sub-target, no handle, no thumb.

**Layout (inside `DisarmChallengeOverlay`'s card — Freya owns the card; the valve block's
internal layout is specced here and mailed to her):**

```
┌──────────────────────────────────────────────┐
│  {47}            ← hero numeral, Anon Pro    │
│  SECONDS STILL OWED   Bold 44sp, cream       │
│  ── valve bar, 88dp, four chambers ──        │
│  [ 15 ▓▓▓▓ | 30 ▓▓░░ | live ░░ | empty ]     │
│  BANKED 30 · HOLDING 7      ← centre line    │
└──────────────────────────────────────────────┘
```

Valve view total height becomes **144dp** (44dp numeral + 8dp gap + 88dp bar + centre line
inside the bar) replacing the current 68dp AndroidView height in the overlay. The numeral is
drawn by the valve view (Canvas, `paintMono`) so numeral, bar, and line can never disagree
about the total — one view, one truth source, one frame.

**Segment rendering (the bar).** Four chambers, each exactly 15 seconds wide, separated by
2dp vertical walls in cream α55 (night) / α45 (day-card if ever day-hosted):

- **Banked chamber:** solid fill `tokControlFill()` (day #615B5A / night #4A423C — the fills
  cap, never cream, never orange: banked reads as *stored metal*, heavier and quieter than
  live progress). Its bank numeral (`15` / `30` / `45`) printed Anonymous Pro Bold 11sp,
  cream α90, at the chamber's right end, with a 1dp cream α35 stamp line beneath it. On the
  bank frame the numeral stamps in: scale 1.15 → 1.0, 140ms `CurveA`, once.
- **Live chamber (currently held):** `tokAccent` α180 fill over the chamber floor, width =
  held fraction of the chamber — the valve bar's orange is *at-risk progress*, the one place
  outside the Deck where COMMIT-family orange may appear, because this fill is literally a
  commitment being accumulated. It is a fill, never text.
- **Draining:** the live fill renders **hatched** — diagonal stripes, 4dp pitch, accent α180
  alternating with transparent, collapsing right-to-left at the drain rate. Hatching exists
  nowhere else in the app; draining can never be mistaken for progress or for banking.
- **Grace:** the live fill freezes exactly (no hatch, no motion) — frozen is its own reading.
- **Empty chamber:** lane floor black α18.

**Centre line (inside the bar, replacing today's single blended label):** two facts, never
blended — holding: `BANKED 30 · HOLDING 7`; idle with a bank: `BANKED 30 OF 60 · HOLD TO
CONTINUE`; idle no bank: `HOLD FOR THE EMERGENCY BREAK`; draining: `DRAINING — HOLD TO STOP
IT`; tokens 0: per the cap machine below. Atkinson Bold 11.5sp words + Anonymous Pro Bold
13sp numbers (`drawMixedWordAndMono`, as today), cream.

**Hero countdown numerals.** Seconds still owed = `ceil(60 − total)`, Anonymous Pro Bold
44sp, cream on the overlay card, label `SECONDS STILL OWED` (Atkinson Bold 10sp, tracked,
text-secondary tier) beneath it. The numeral is a *measured* number: it steps, once per
second-change, never rolls, never interpolates. During drain it counts **up** as held
evaporates — the cost of a slip is printed at hero size, in the open, which is the truth
doing the persuading. Final segment (45→60): nothing changes colour; the last bank's
chamber walls brighten α55 → α80 across the final 5 seconds in 100ms steps — the door
visibly closing, in the bar he is already holding.

**Overlay copy (for Freya's host — human language, truthful, replaces the current text
which says "hold continuously", now a lie):**
- Title: `EMERGENCY BREAK`
- Body (tokens = 1): `This ends the session early. It is the only break this week.`
- Body (tokens = 0): `This week's break is already spent. Sessions end when their time does.`
- Rule line, under the valve, small: `Hold to fill 60 seconds. Every 15 seconds banks. If
  your hand slips, what isn't banked drains away. The banked part stays.`
- Return chip: `KEEP FOCUSING` (the current parenthetical `(STAY ARMED)` dies — one verb,
  plainly).

## Motion (curve + ms)

Bank stamp: 140ms `CurveA`. Chamber-wall brightening: stepped, 100ms increments, no tween.
Fill widths: driven by the 32ms runnable (data motion, no interpolator — the fill *is* the
elapsed time; easing it would lie). Overlay enter/exit remain Freya's (specC/specA fades).
Nothing else moves. A valve that animates for pleasure while a man decides whether to quit
is noise at the worst address in the app.

## Haptics (per beat)

Escalation is the design: each segment climbs in pitch toward its bank, each bank lands
heavier than the last, and a drain is *felt* so the whole valve works eyes-off.

| Beat | Spec |
|---|---|
| Second tick (while holding) | `fireFeel(1L, freq)` — pitched detent, waveform [0,7,5]ms / [0,180,70]; `freq = 420 + (heldInSegment / 15) × 240` → climbs 420→660Hz within each 15s segment, **resets at each bank** (the ear hears "climbing toward the next bolt" four times) |
| Every 5th second | second tick adds `fireFeel(8L)` firm click [0,13,20,8] / [0,225,0,135] |
| **Bank 15** | pitched detent `fireFeel(1L, 560f)` + 1 latch strike `fireFeel(4L)` [0,11,26,9] / [0,215,0,150] |
| **Bank 30** | pitched detent `fireFeel(1L, 470f)` + 2 latch strikes, 120ms apart, second at amplitude ×1.15 (postDelayed runnable in-view) |
| **Bank 45** | pitched detent `fireFeel(1L, 390f)` + `fireFeel(8L)` then 2 latch strikes at +120/+240ms — pitch *falls* as strikes *multiply*: each bank is a heavier bolt |
| Drain tick | every 250ms of drain: `fireFeel(1L, freq)`, freq gliding 600 → 280Hz across the drain's duration |
| Catch at floor (banked > 0) | `fireFeel(4L)` latch — the floor is felt, not watched |
| Drain to zero (banked = 0) | `playMechanicalLatch(false)` direct at arrival — nothing caught, nothing banked |
| Grace entry/exit, re-seat | silent — they are not events in his intent |
| Completion (total = 60) | **the view plays nothing.** See Sound. |

Amplitude-shaped summary for the implementer: ticks ride the detent waveform (amps 180/70,
fixed by the engine); bank weight is expressed by strike *count* (1/2/3) and falling pitch
(560/470/390Hz), because the engine's only pitched path is 1L and latch amplitudes are
canned — anyone needing louder banks must change the engine, and this spec does not.

## Sound (primitive + pitch)

Ticks: `playRotaryDetent(420→660Hz per segment)` 6ms gain 0.38. Fifth-second:
`playFirmClick()` 620Hz. Banks: `playRotaryDetent(560 / 470 / 390Hz)` + `playMechanicalLatch(true)`
760Hz strikes per the table. Drain: `playRotaryDetent(600→280Hz glide)`. Catch:
`playMechanicalLatch(true)`. Zero-floor: `playMechanicalLatch(false)` 520Hz.
**Completion: the ViewModel's existing `playHarmonicSeal(arming = false)` (480Hz, 48ms,
gain 0.62) is the single completion sound.** The view's current `fireFeel(10L)` on completion
is **deleted** — today both fire, a seal stacked on a wind-down pulse, two renderings of one
event in sound. `playCompletionPulse` never plays in this view.

## Depth & light

Bar track: `tokBlockBar`, radius 24dp, 1dp hairline (as today). Chambers recessed: chamber
floor black α18 with a 1dp top inner shade α38 black — the chambers are slots milled in the
bar; banked fill sits *in* its chamber flush (stored), live fill sits in its chamber with the
existing pooled contact logic (in motion). Walls are printed (cream α), not raised — at 88dp
with a shaking hand, contrast must come from tone, not from shadow geometry he can't see.
Night: overlay is night-forced today (`isNightMode = true` hardcoded in the overlay's
factory); all values above are the night values, ground = overlay card surface, AOD black
does not apply here (this is not the desk clock).

## State machine

**Bank machine (per session attempt):**

```
EMPTY(0,0) ─hold─► ACCRUING ─cross 15─► BANKED15 ─► ACCRUING ─cross 30─► BANKED30
   ─► ACCRUING ─cross 45─► BANKED45 ─► ACCRUING ─total 60─► COMPLETE
ACCRUING ─slip─► GRACE(800ms) ─re-grab─► ACCRUING (no loss)
GRACE ─800ms expires─► DRAINING ─held=0─► AT FLOOR (= bank state, idle)
DRAINING ─re-grab─► RESEAT(250ms, no accrual) ─► ACCRUING
any ─overlay dismissed─► bank kept, held → 0 (settled to floor)
COMPLETE ─► token spent (below), bank state destroyed with the session
```

**Weekly-cap machine (the token):**

```
AVAILABLE(1) ──completed break only──► SPENT(0) ──week rollover──► AVAILABLE(1)
```

- The token is decremented **only** in `confirmDisarmSession` on a completed valve break —
  current ViewModel behaviour, kept and now stated as law: banking, slipping, draining,
  dismissing the overlay, and grace-window aborts consume **nothing**. A released hold costs
  the drained time and nothing else — no token, no record penalty, no lockout.
- **SPENT state rendering:** valve touch returns `false`; hero numeral replaced by the words
  `NO BREAKS LEFT` (Atkinson Bold 15sp); centre line `THE WEEK'S ONE BREAK IS SPENT`;
  chambers all empty-outline; body copy per above. The KEEP FOCUSING chip remains the only
  action — at zero tokens the overlay is information, not a door.
- **Persistence (required delta):** token + week must persist across process death
  (DataStore or a Room row): store `tokensRemaining` with `weekStartEpochDay` (Monday
  00:00 local — his week, matching the Sunday review rhythm's week boundary as Monday-start;
  if Ren's build uses a different stored convention already, follow the store, not this
  sentence). On read, if the stored week ≠ current week, token resets to 1 and the bank
  state is already dead with its session. Today's in-memory `StateFlow(1)` means force-quit
  refunds the break — a cap that can be refunded by killing the app is not a cap.
- Multiple sessions in one week share the one token; banks do **not** carry across sessions.

## Edge cases

Hold begins with tokens = 0: impossible (touch disabled) — and the ViewModel re-checks the
token in `confirmDisarmSession` regardless (defence in depth, kept). Session ends naturally
*while* the overlay is open (timer hits zero under his thumb): overlay dismisses with the
session; bank state dies unspent; token untouched. Process death mid-attempt: bank dies
(session is gone or restored without it — either way the attempt's bank is not resurrected;
the *token* survives correctly via persistence). Overlay reopened after a slip: centre line
reads `BANKED 30 OF 60 · HOLD TO CONTINUE`, hero numeral shows the true owed 30 — the bank
greets him accurately. Finger slides off the bar's edge mid-hold: `ACTION_CANCEL` = slip
(grace applies — the grammar does not distinguish why contact ended). Second finger touches
mid-hold: ignored; first pointer owns the hold. `requiredSeconds` is 60 by state today; the
engine is written against `requiredSeconds` generally, banks at its quarters — if the
duration ever changes, banks move to `required/4` steps and the copy's "15 seconds" must be
re-derived from the same constant, never hardcoded twice.

## Code: helps / fights

**Helps:** 32ms runnable + `postDelayed` patterns already in the view; `drawMixedWordAndMono`
already renders the centre line; ViewModel already decrements the token *only* on completed
non-grace disarm and already plays the completion seal; overlay already disables the valve at
0 tokens (touch returns false). **Fights:** single `holdProgress` float with instant zeroing
on release (replace with the banked state in the ViewModel + a view that renders it);
per-second ticks use canned 4L/8L with the pitch argument discarded (reroute to 1L);
completion double-sound (delete the view's 10L); overlay hardcodes valve height 68dp and
`isNightMode = true` (height → 144dp is Freya's host change, mailed; the night hardcode is
*correct* for this overlay and stays); in-memory token (persistence delta above);
`DisarmChallengeState` carries no bank fields (add `bankedSeconds` — the overlay state is
the view's input, the ViewModel remains the owner).

## Law check

HOLD absolute ✓ — the valve is the *one* sanctioned exit, priced exactly as he set it: a
full minute of intent, once a week. His settled rule ✓ — banks at 15/30/45, slip drains to
the floor, never zeroing; token spent only on completion; ≥88dp target; escalating ticks;
hero numerals. TRUTH ✓ — split readout (`BANKED · HOLDING`) instead of one blended number;
copy rewritten so no word claims "continuous"; owed numeral counts up during drain in the
open. Rested decides ✓ — the cap and the rule were set rested; the tired user only holds
or lets go. Dependencies: **Freya** (overlay card: valve height 68 → 144dp, copy block,
KEEP FOCUSING wording — mailed), **Ledger fate OPEN** — whether a spent break is *recorded*
anywhere is the Ledger's question; this element records nothing beyond the token itself.
**Guard fate OPEN** — no dependency.

---

## Cross-element invariants (whole surface, stated once)

1. Orange appears as: slide thumb fill (Deck's one orange), valve live-chamber fill α180
   (a commitment accumulating — the exception that proves the budget: it is a fill doing
   COMMIT's job in the one other place a commitment is made by hand). Nowhere else, never
   as text.
2. `playHarmonicSeal(true)` belongs to the slide seat; `playHarmonicSeal(false)` belongs to
   the completed break (ViewModel). No other element on this surface fires either.
3. Every number on this surface is Anonymous Pro; every word Atkinson Hyperlegible; real
   weights only (700 where bold is specced — Atkinson/Anonymous Pro ship 400/700, no Medium
   is ever named).
4. Durations used: {140, 180, 250, 260, 420, 520}ms + the valve's data-driven times (800ms
   grace, 250ms drain-tick interval, 32ms runnable) — 140/250 are valve-stamp/drain
   constants already present in the swarm's valve vocabulary; all *gesture* motion stays
   inside {180, 260, 280, 420, 520} and curves A/B/C. No new curves anywhere in this file.
5. Cold-open-to-sealed budget: Deck open → (mise has already laid out, or one wheel setting)
   → one slide (≤1s hand time + ≤380ms machine time). Nothing in Elements 2–4 touches that
   path: the ribbon is print, mise is pre-work, the valve lives behind a running session.

*— Ines, Maximum-Implementable, slide/arming seat. Mail sent this run: Sten (firing-data
contract), Freya (overlay host changes), Cato (reply: seat-then-fire confirmed), Zug-II
(reply: gate conflict — 0.65 stands, reasoning enclosed). Wall post appended.*
