# PIA — Slice DE, Strip Cell 3: TIME

*Seat: Pia (Wild, watches — timekeeping mechanisms). Coordinator: Marlowe.
Joint: the third cell of Rosa's strip (BLOCK → APPS → TIME, 56dp, fixed
addresses, 8dp above the slide track). Rendering channels are Rosa's and
are not re-fought here: position, fill density (pass = recess flush ·
fault = cap-tone fill · unknown = raised fill), word, edge (fault =
doubled top hairline); word in Anonymous Pro 700 15sp. This file rules
only what the TIME cell **says**, where the saying comes from, and what
can make it fault.*

---

## 1. Verdict

**AMENDED — Rosa's word stands, her machinery dies.**

The cell's content is the one Rosa filed and the merge already
ratified: **`ENDS HH:MM` — the finish of the session the Deck is
set to seal** (Rosa, beyond-deck-parts/rosa.md §2, Channel 3; BEYOND-
deck F-HERO: *"the finish is the strip TIME cell's fact, rendered
there once"*). That part builds as specced.

What does **not** build as specced is everything Rosa wrapped around
it: TIME as a *query* (her table: "local compute + schedule read,"
1500ms deadline → `UNKNOWN`), TIME going `UNKNOWN` "when the schedule
cannot be read," and the panel freshness stamp reading as if it
covers this cell. Under the law this slice was seated under — Live
Wire verdicts (Ines §3), Otis's TIME ruling (09-otis.md, Mechanism 1:
*"TIME: never polled, never on the bus as a measurement — it is a
pure function of the wheel state and the routines flow… derivation
makes it 0ms and un-stale-able by construction"*), and the Register's
Now Frame (one pure `derive()`; finishes-at is a frame field) — a
computed cell has no deadline to miss, no measurement to age, and no
UNKNOWN. The exact amended rendering law is §3 below. It is smaller
than the filed spec, not larger: one pure function, four Deck states,
two fault cases, no stamp.

**The two losing candidates, killed on the strip's own job:**

- **The primed routine's span** — a duration. The hero already owns
  the session's span (`TactileDeckScreen.kt:236–248` prints the true
  span as the hero; the routine's own duration lives on its Shield
  row). Printing a span in TIME would be a second rendering of a
  fact with a home, and it would not answer the cell's question.
  The strip's order is causal — BLOCK (can it block) → APPS (what
  will it block) → **TIME (until when)** — and "until when" is a
  clock time, not a length.
- **The next window** — a schedule fact. Its homes are chrome
  (`FASTED BLOCK · 05:00`, BEYOND W1) and Shield's routine rows;
  W1 bars schedule words from the Deck body, and Teo's fence bars
  the day beyond the imminent block from the Deck entirely. A next-
  window cell would also change when nothing about the session
  changed — furniture that moves on its own is a warning, and the
  strip is an instrument, not a warning. The next window enters
  this cell's life in exactly one place: as the *boundary the
  finish is compared against* for the crossing sentence (§3.4),
  never as the cell's word.

---

## 2. Against the real code (verified this seating)

All paths `Focus-app/app/src/main/java/com/example/`.

- **The true span already exists, exactly as the Register states
  it.** `ui/screens/TactileDeckScreen.kt:138–142`:
  `totalSpanMinutes = dialMinutes * studyBlocksCount +
  (studyBlocksCount - 1) * 10` — `n × block + (n−1) × 10`, comment
  cites BEYOND W3 and the engine. The TIME cell needs no new span
  arithmetic; it consumes this.
- **The shipped finish derivation is a lie in the running state —
  and the strip is its cure.** `TactileDeckScreen.kt:155–163`
  (`finishPair`): `Date(System.currentTimeMillis() +
  totalSpanMinutes × 60_000)`, formatted `h:mm a`. Three defects:
  (a) it is keyed on `displaySeconds` (`remember(dialMinutes,
  studyBlocksCount, isSessionRunning, displaySeconds,
  isStopwatchMode)`, :155), so while a session runs it recomputes
  every second and the finish **slides forward with the clock** —
  a finish that never gets closer; (b) mid-session it prices the
  *whole dial span from now*, never reduced by elapsed already
  burned (Nadia's W1, 08-nadia.md, same finding); (c) it reads the
  *dial*, not the session, so a running session whose dial state
  moved would print a finish for a session that doesn't exist.
  It renders at two sites — :324–329 (idle branch, 11sp) and
  :352–357 (running branch, 11sp) — mutually exclusive branches,
  so one rendering per state, but both must die when the strip
  lands: F-HERO already assigns the finish to the strip cell, and
  two homes for one fact is how the shipped bug survived.
- **The inputs are StateFlows, already collected by the Deck.**
  `ui/MonolithViewModel.kt:84–88`: `_dialMinutes` (default 50),
  `_studyBlocksCount` (default 2), both exposed as `StateFlow<Int>`;
  coercion at the setters — blocks `coerceIn(1, 8)` (:389), dial
  `coerceIn(3, 180)` (:393); the engine coerces again at session
  insert, `data/MonolithRepository.kt:905` (`coerceIn(1, 8)`).
  Routines: `MonolithViewModel.kt:102–104`, `routines:
  StateFlow<List<RoutineEntity>>` from
  `repository.allRoutinesFlow` (`MonolithRepository.kt:62`).
- **There is no "primed" state in code.** Grep for prime/primed
  across the app finds only `ShieldPermissionPrimerModal`
  (a permissions sheet, `MainActivity.kt:529`). Priming is plan
  law (BEYOND W1: the prime function carries wheel preset, count,
  loadout into the 04:58 frame) with no code representation yet.
  §3 therefore defines "primed" operationally from things that do
  exist — the routines flow + Now Frame window state — so the
  builder never has to invent a prime flag to render this cell.
- **Routine window math exists, pure, in one object.**
  `data/MonolithModels.kt:374–382` (`RoutineEntity`: startMinutes,
  endMinutes, daysMask, loadoutName, enabled) and `:385–462`
  (`RoutineSchedule`: `durationMinutes` :390–394 — wrap-aware,
  coerced 15–720; `nextStartMillis` :409; `nextEndMillis` :423;
  `isActiveNow` :441). Each walks `Calendar` independently with a
  caller-supplied `nowMillis` (Nadia's W2) — the Register's Now
  Frame `derive()` is the lawful single caller; this cell's
  function must call the three walks **once, through the frame**,
  never from the composable. Seeds, for the crossing examples:
  `MonolithRepository.kt:85–100` — Morning Deep Work 09:00–13:00
  Mon–Fri STUDY; Night Wind-Down 22:30–07:00 all-days SLEEP.
- **The session's own time facts are already unified on the
  entity.** `data/MonolithModels.kt:249–285`:
  `FocusSessionEntity(startWallTimeMillis, targetDurationSeconds`
  — per-block, set at `MonolithRepository.kt:879–905` —
  `totalStudyBlocks, currentBlockIndex, isInSafeBreak,
  penaltySecondsAdded)` with `effectiveTargetSeconds =
  target + penalty` (:281–282) and penalty-aware
  `remainingSeconds` (:284–285). The running cell's finish is a
  frame derivation over these fields, never over the dial.
- **Now Frame `derive()` is Register law, not yet code.** No
  `NowFrame.kt` exists in the tree; the entity getters above are
  its partial landing. §3's function is written to the Register's
  frame signature (`finishesAtWallMillis`, window state) so it
  drops into `derive()` when Nadia's file builds — and is priced
  below so it can also build standalone, unchanged, if the strip
  lands first.

---

## 3. Amended spec — the TIME cell rendering law (exact)

### 3.0 The function

One pure function, no I/O, no clock reads of its own:

```
timeCell(
  dialMinutes: Int, blocks: Int, preset: StudyModePreset,
  session: FocusSessionEntity?,          // null when unsealed
  frame: NowFrame                          // wallMillis, window state,
                                           // finishesAtWallMillis
) -> TimeCellState                         // (word, density, edge)
```

- `spanMin = dialMinutes × blocks + (blocks − 1) × 10`
  (the :142 formula; stopwatch/FLOW exempt, §3.2).
- Recomputed **only** when an input emits: wheel seat, hub count
  change, preset change, routines-flow emission, session-flow
  emission, Now Frame publish (Register: the publisher sleeps to
  the next change instant — at idle that is the next wall-minute
  boundary; running, the next frame change). Between emissions
  the cell is still, and the stillness is correct: none of its
  inputs *can* change without one of those emissions — dial and
  blocks are StateFlows, wall time enters only through the frame.
- Cost: arithmetic + one format, **0ms class** (Otis: derivation
  makes TIME 0ms; Rosa's own table priced the recompute <5ms —
  either way it is never on any deadline, because there is none).
- Format: `ENDS HH:MM`, 24-hour, zero-padded, Anonymous Pro 700
  15sp (Rosa's examples are 24h throughout: `ENDS 20:00`). The
  shipped `h:mm a` at :161 dies with `finishPair`. Seconds are
  truncated by the minute format, never rounded — the cell is a
  minute instrument; second truth lives in the running readout,
  its one home (my seat file, plan/wild-seats/03-pia.md).

### 3.1 Content by Deck state

| State | Cell word | Anchor / source | Density |
|---|---|---|---|
| **Idle, unprimed** (no session; no routine window open; none priming the Deck) | `ENDS HH:MM` | `frame.wallMillis + spanMin` — the finish *if he seals now*. It advances one minute per wall minute, on frame publish only: the fact ("a session started now ends then") genuinely changes every minute; the display never ticks inside a minute. | recess flush |
| **Primed** (a routine primes the Deck per W1 — operationally: a window is open now, or the next window's routine is the one whose preset the Deck carries) | `ENDS HH:MM` — same word, same form | **Open window:** `now + spanMin` (he starts inside it; the seal is his, the window is already running). **Primed for a future window:** `windowStart + spanMin` — at the 04:58 frame (W1: wheel 50, count 3, span 170, FASTED window 05:00) the cell prints `ENDS 07:50`, not `ENDS 07:48`. A finish for a start time he will not use is a lie of 2 minutes every dawn. | recess flush |
| **Mid-press** (ACTION_DOWN → gate → seat or release) | **Unchanged** — the word, density and edge rendered at DOWN persist for the whole press | Snapshot of the inputs at DOWN. The wheel and hub are inert under the slide contact, so span cannot change mid-press; the only input that could move is the wall minute, and a furniture cell does not re-print under a man's thumb. Recompute lands on the seat/release frame. No `CHECKING` state exists for this cell, at the press or anywhere — there is nothing to check. | unchanged |
| **Sealed / running** | `ENDS HH:MM` | `frame.finishesAtWallMillis` — derived from the **session**, never the dial: session anchors + penalty-aware remaining span incl. inter-block breaks (Register Now Frame field; entity facts at MonolithModels.kt:281–285). Recomputes on frame publish: in practice on the minute boundary of remaining, and **the same frame** a penalty lands (+15m tamper penalty moves the finish +15 — a truth event, not a tick). Entering a Safe Break does **not** move it: breaks are already priced into the span; moving the finish at break entry would count them twice. | recess flush (frozen verdict for BLOCK/APPS at seal per Rosa §1 stands; TIME alone keeps deriving, because its fact keeps being true) |
| **Session ends** (completion or abort) | Returns to the idle derivation at the next Deck composition | `now + spanMin` from the dial state as left. No receipt copy, no held finish — the receipt is the finish's record, and it lives in the receipt. | recess flush |

### 3.2 FLOW / stopwatch

A FLOW session has no finish; the cell must not fabricate one.
In every state (idle-FLOW, primed-FLOW, mid-press, sealed-FLOW):

- Word: **`NO END`** (Anonymous Pro 700 15sp), recess flush.
- No crossing sentence ever fires from a FLOW setting.
- The 20-minute floor is **not** printed here — it is the running
  readout's / hero's fact (shipped `finishPair`'s `"OPEN FLOW •
  20M FLOOR"` branch, :157–158, is a second home for it and dies
  with the pair regardless). `FLOW` the word likewise stays in
  the hero only (BEYOND F-FLOW).

### 3.3 Fault semantics — can a computed cell be UNKNOWN?

**No. UNKNOWN is dead for this cell, in every state, without
exception** (Register: the UNKNOWN class dies with the waiting
machinery; Ines §3 retains an UNKNOWN only as a first-run *slide*
state, not a strip state). A pure function of flows the Deck
already collects either returns a value or detects that its
inputs contradict each other. There is no third outcome, no
deadline, no raised-fill state for TIME: **the raised (unknown)
density is never used by cell 3.** A builder who reaches for
`UNKNOWN` here has rebuilt the poller.

Fault exists, and it is narrow — **input inconsistency**, exactly
two cases:

- **F-T1 — span inputs inconsistent at render.** `blocks ∉ 1..8`,
  or `dialMinutes` outside the engine's own range, arriving at
  the function *uncoerced* (i.e. raw stored/session state that
  bypassed the VM setters at MonolithViewModel.kt:389/:393 and
  the insert coercion at MonolithRepository.kt:905). Unreachable
  while those coercions hold — which is the point: the fault
  renders the morning a coercion is ever bypassed, instead of
  printing a finish computed from numbers the engine will not run.
- **F-T2 — sealed contradiction.** A session is active but its
  finish cannot be formed from its own anchors:
  `startWallTimeMillis ≤ 0`, or `effectiveTargetSeconds ≤ 0`,
  or `totalStudyBlocks ∉ 1..8` on the entity. (Tamper is *not*
  F-T2: tamper is handled, penalty priced in, finish consistent.
  A wall-clock change mid-session is *not* F-T2 either: remaining
  is mono-anchored, the finish is its wall rendering, and a clock
  set back honestly moves `ENDS` back.)

Fault rendering (both cases): **cap-tone fill + doubled top
hairline + word `INVALID`** (Anonymous Pro 700 15sp — a state
word, no fabricated time, no placeholder dashes). Advisory only,
exactly as Rosa filed for TIME: **a TIME fault never refuses a
seal** — BLOCK alone interlocks. The slot below the track may
carry, at advisory priority (below interlock, above empty):
`END TIME CANNOT BE WORKED OUT FROM THE WHEEL AND BLOCKS.`
(Atkinson 400 15sp, Rosa's advisory-slot form.)

**Routine-data inconsistency is not a TIME fault.** Overlapping
windows, `daysMask = 0`, a coerced duration
(`RoutineSchedule.durationMinutes`, :390–394) — none of it can
touch the finish, because the finish never needed the routine
store. This **strikes Rosa's filed line** ("TIME… if the routine
store is unreadable it goes `UNKNOWN` like anything else",
rosa.md §1): the store feeds only the crossing comparison
(§3.4); if window state is ambiguous or unreadable, the
*crossing sentence is suppressed* — silence over a guessed
warning — and the cell stands unaffected, flush, truthful.

### 3.4 The crossing — a sentence, never a cell state

Retained from Rosa §2 verbatim in structure: a crossing is not
rendered inside the cell. The cell prints the finish, flush, in
all cases. The comparison is computed in the same function:

- Boundary = the earliest routine boundary (any enabled
  routine's **start or end**, from the frame's single
  RoutineSchedule pass) strictly after the anchor and strictly
  before the finish. In the primed-for-future case the anchor is
  the window start, so the priming window's own start is never
  reported as "crossed."
- If one exists, the reserved 22dp slot 8dp below the track
  carries **one** sentence, first boundary only:
  - start boundary: `ENDS 21:40, AFTER SLEEP BEGINS AT 20:00.`
    (Rosa's exact form; name = the routine's loadout/name as
    printed elsewhere in the app, times variable, form fixed)
  - end boundary: `ENDS 08:20, AFTER FASTED BLOCK ENDS AT 08:00.`
    (same form, ENDS verb — the generalisation Rosa's single
    example implies; flagged here as the one sentence-form I am
    adding, priced at zero extra machinery)
- Atkinson 400 15sp lh 22, slot priority: **interlock >
  crossing > empty** (Rosa's order, unchanged). No haptic, no
  sound, no fill change. The strip remains an instrument; the
  sentence is the Deck speaking once, in the slot built for it.

### 3.5 Freshness — no stamp for TIME

**The seam stamp does not cover cell 3.** Rosa's stamp grammar
(`CHECKED JUST NOW` / ceiling-rounded age / `STALE` past 45s)
survives — under Otis's Mechanism 1 — for the *measured* cells,
BLOCK and APPS, whose bind-evidence has an age worth printing.
For TIME a stamp would be a category error in print: it would
claim a measurement event ("checked") for a value that was
*derived at render*, and an age for a fact that cannot age —
every recomposition recomputes it from the current flows, so
at any frame on screen its age is zero by construction, and
between frames it is not being asserted at all.

- No stamp, no age text, no `STALE`, ever, on or under cell 3.
  The stamp row prints over cells 1–2's span of the seam; over
  cell 3 the seam is blank metal. (If the strip builder judges a
  part-width stamp row unbuildable in the filed geometry, the
  fallback is: stamp stays full-width but its text is specified
  as *the readiness stamp* — it ages BLOCK/APPS only, and this
  file's law is that it is never read as TIME's. The part-width
  form is my recommendation; the geometry call is Rosa's.)
- TIME's freshness guarantee is structural, and it is stronger
  than a stamp: **un-stale-able by construction** (Otis). The
  failure a stamp exists to catch — a value silently older than
  it looks — cannot occur in a value recomputed from live flows
  at every render and re-rendered on every input emission (§3.0).

---

## 4. Code sites

| # | Site | Change |
|---|---|---|
| 1 | `data/NowFrame.kt` (Nadia's file, when built) | `timeCell` derivation inside / beside `derive()` — consumes the frame's existing `finishesAtWallMillis` + window state; ~35 lines. If the strip builds first: same function, same signature, in the strip's file — it moves into `derive()` later with zero call-site changes. |
| 2 | Strip composable (new view, per Rosa §2 geometry — `ui/views/` beside the slide, or `ui/components/`) | Cell-3 rendering only: word / density / edge from `TimeCellState`; ~25 lines of the strip's build. No timer, no poll, no stamp handling in this cell. |
| 3 | `ui/MonolithViewModel.kt` (near :84–104) | One derived `StateFlow<TimeCellState>`: `combine(dialMinutes, studyBlocksCount, studyPreset, routines, activeSession, nowFrame)` → `timeCell(...)`; ~20 lines. |
| 4 | `ui/screens/TactileDeckScreen.kt:155–163` + `:324–329` + `:352–357` | **Delete** `finishPair` and both render sites (−~21 lines). The Deck body stops printing the finish anywhere; the strip cell is its one home (F-HERO). The hero span (:142, :236–248) is untouched — span and finish are different facts. |
| 5 | Tests (new, test source set) | Pure-function fake-clock tests: idle anchor, open-window anchor, primed-future anchor (04:58 → 07:50), FLOW in all states, penalty shift (+15 same frame), break-entry non-shift, crossing start vs end boundary, F-T1/F-T2 fault, ambiguous-windows suppression; ~50 lines. |

No DB change, no migration, no new permission, no timer, no
receiver. Nothing in this cell reads `System.currentTimeMillis()`
— wall time enters through the frame alone (Nadia's grep law).

## 5. Price

- **Gross ≈ 130 lines** (derive ~35 · cell rendering ~25 ·
  VM combine ~20 · tests ~50), **net ≈ +109** after the
  `finishPair` deletion (−21).
- **4 files touched, 1 test file added, 0 new production files**
  if `derive()` hosts the function; 1 new file if the strip
  lands first (the function, alone, ~35 lines).
- ARM cost: 0ms serial, 0ms parallel — the cell is standing
  furniture rendered from flows already collected; it adds no
  step to any arm path and no work to the press.

## 6. Risks / device-only unknowns

- **Primed-future anchoring is plan behaviour with no code or
  device precedent.** The 04:58 frame exists in BEYOND W1 as a
  drawing; no prime function has ever run. Whether he reads
  `ENDS 07:50` at 04:58 as "the session I'm about to start" or
  is confused by a finish anchored to a start he hasn't made is
  a phone-in-hand question. Mitigation if it reads wrong: the
  anchor rule is one branch in one pure function — flipping it
  is a 3-line change, and §7 forks it to Ren rather than burying it.
- **Now Frame dependency.** If the strip builds before Nadia's
  `derive()`, the VM combine (site 3) needs a wall-minute tick
  to recompute the idle anchor — a `LaunchedEffect` minute loop
  in the ViewModel is the tempting shortcut and it is a
  counter-clock in Nadia's sense (drifts, dies with the process;
  a *late* frame, never a wrong one — the finish derives from
  wall time at recompute, so the error is bounded at one minute
  of staleness on a backgrounded-then-foregrounded Deck, cured
  by the resume recompute). Acceptable as scaffolding, must be
  deleted when `derive()` lands; named here so it isn't
  mistaken for the design.
- **24h vs the shipped `h:mm a`.** The strip adopts Rosa's 24h
  examples; chrome elsewhere in the plan also prints 24h
  (`05:00`, `20:00`). If any shipped surface still formats
  `h:mm a` when the strip lands, the app will tell the finish
  two ways in two dialects — a consistency sweep of time
  formats is owed by whoever lands the strip, not by this cell.
- **Crossing name source.** The sentence prints a routine's
  name/loadout (`SLEEP`, `FASTED BLOCK` in the examples come
  from plan frames; the seeded entities are named "Morning Deep
  Work" / "Night Wind-Down" with loadouts STUDY / SLEEP). Which
  string is canonical — entity `name` or `loadoutName` — must
  match whatever Shield's rows print, or the same window gets
  two names. One-line decision at build; flagged, not guessed.
- Density legibility at night (recess vs cap one staircase
  apart) is Rosa's named risk for the whole strip and is
  inherited, not worsened, by this cell: the word is the first
  channel, density corroborates.

## 7. Fork for Ren

**F-TIME-ANCHOR — one fork, one question:**
*When the Deck is primed for a routine window that hasn't
started yet (he arrives at 04:58 for the 05:00 block), should
the TIME cell count the finish from the window's start
(`ENDS 07:50`) or from now (`ENDS 07:48`)?*

- **Option A (my recommendation): from the window's start.**
  The primed frame is the rested man's preparation for 05:00;
  a finish priced from 04:58 describes a session that will
  never exist. Every other number on that frame (wheel 50,
  count 3, span 170) is already the window's, not the moment's —
  the finish should be too.
- **Option B: from now, always.** One anchor rule for every
  idle state, no window input to the cell at all; the cell
  gets simpler and the 04:58 frame carries a finish that is
  wrong by the wait, every dawn, by construction.

Everything else in this file is decided, not forked: content
(`ENDS`), FLOW (`NO END`), no UNKNOWN, two fault cases, no
stamp, crossing as a sentence.

## 8. Mail / wall note

- **Inbox:** three letters read and moved to `mail/pia/read/`
  per protocol — Fen (one-step-fight), Wren (step / Night Cut
  ask), Mara (silent-strike-light-refuses). None touches this
  joint: Fen's sealed-stillness and Mara's light law are
  consistent with §3.1's mid-press freeze and this cell's
  no-motion rendering, and neither is re-fought here. Wren's
  Night Cut ask (a special-cased first AOD patrol anchor) is
  **not ruled in this slice** — it gates on my patrol judgement,
  not on TIME, and I will not let it ride silently on this
  deliverable; it is named here so Marlowe can re-brief it as
  its own question.
- **Letters sent:** Rosa (strip owner — the amendment strikes
  her TIME-as-query row, her routine-store UNKNOWN line, and
  the stamp's scope over cell 3; her word, geometry, crossing
  sentence and advisory status all stand) and Marlowe
  (coordinator — verdict, price, fork F-TIME-ANCHOR).
- **Wall:** appended under `Pia — Wild, watches` with the
  verdict and the one fork, signed, shell-append only.

— Pia, Wild · watches
