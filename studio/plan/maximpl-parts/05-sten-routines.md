# 05 — ROUTINES (PLAN WAVE) — Sten, Maximum-Implementable (coordinator: Voss)

**Element:** Routines on Shield — the registered Section, the row as hero, the ON/OFF
miniature sled, full CRUD, the reseed to his real day, the no-ghost attendance rule,
and theme-follows-schedule.
**Concept:** NONE. No-Idea won; the Consent Beat is DEAD. Routines fire as he set
them; the machine does not ask twice. Standards: ARM ≤10s · HOLD absolute · TELL
THE TRUTH once, in human language. Workshop laws: one fact one rendering · beauty
is retention · rested user decides, tired user executes — **this spec is where
that law becomes code: every control below is a rested-user surface. Nothing in
this file adds a decision, a prompt, or a gesture to a firing, a session, or the
arm path.**

**His seeds (the only three routines a fresh or migrated install starts with):**

| Name | Window | Days | Loadout | Duration |
|---|---|---|---|---|
| FASTED BLOCK | 05:00–08:00 | DAILY (127) | STUDY | 3H 00M |
| DAY BLOCKS | 09:00–19:30 | DAILY (127) | STUDY | 10H 30M |
| SLEEP | 20:00–03:30 (+1) | DAILY (127) | SLEEP | 7H 30M |

Note on DAY BLOCKS: 10H 30M exceeds the editor's current 720-minute span clamp
(`MonolithViewModel.adjustRoutineTime`, rawSpan `30..720`). The seed is his
decision and the clamp loses: the clamp rises to **30..1080** (18H) in §3.1.
A 10.5-hour window is not ten 50-minute blocks and this spec never calls it
that — it is one open window; what he does inside it is the Deck's business.

**Decisions made in this file (argued where they land):**
1. The wave-1 **LIVE accent node exception is KILLED** (Appendix B.1).
   Shield's accent budget is ZERO, full stop.
2. The word **LIVE is KILLED** as a routine state word. A window is
   `WINDOW OPEN`. A session is the Deck's fact. (Kato's ceiling, adopted.)
3. The wave-1 **time well leaves the collapsed row** and moves into the editor,
   because in the registered Section the block's position already renders the
   start (§6). One fact, one rendering.
4. **Attendance is booked per routine, in its own table, and minutes live only
   in sessions** — attendance never sums into any total (§5).
5. **SLEEP-loadout routines are exempt from attendance and auto-pause** (§5.4).
   A sleep window asks for nothing and books nothing.
6. **Delete has no confirmation dialog.** It has a 5-second undo (§3.4).
7. **The manual DAY/NIGHT override does not survive.** No pill, no toggle, no
   hidden setting (§7).

---

## 1. The row as hero

### Mechanism

The row lives inside Kato's Registered Section (§6 gives the Section its full
geometry; this section specifies the row that registers into it). Row module,
fixed minimum pitch **76dp**, three text lines + the sled:

```
 |←lane→|←———————— row field ————————→|
  block   FASTED BLOCK                          [ sled ]
          DAILY · STUDY · 3H 00M
          NEXT · TODAY 05:00
          LAST · ATTENDED 2H 50M OF 3H        ← attendance line (§5.7)
 ───────────────────────────────────────────────────────  hairline
```

- **Line 1 — name:** Sora, real Bold weight (locked: real font weights only —
  Sora Bold TTF, never synthesized), 16sp, −0.3sp tracking, uppercase, one
  line, ellipsis. Disabled/paused: text-secondary tier.
- **Line 2 — meta:** Anonymous Pro 11sp, 0.4sp tracking, format exactly
  `DAYS · LOADOUT · DURATION`: `DAILY · STUDY · 3H 00M`,
  `MON–FRI · STUDY · 4H 00M`, `SAT · STUDY · 10H 30M`. Duration computed by
  `RoutineSchedule.durationMinutes`, formatted `"%dH %02dM"` — computed
  numbers re-strike in place, instantly, no roll/flip through intermediates.
  `daysMask == 0` → the days segment reads `NO DAYS` at full ink + Bold (a
  fault carried by form — weight and word — never by alert colour).
- **Line 3 — the NEXT / WINDOW line:** Anonymous Pro Bold 10sp, 0.8sp
  tracking, text-secondary tier (night 5.8:1 — it is tappable-adjacent state
  text, never the 3.5:1 decorative tier). Driven by the section-hoisted
  ticker and `RoutineSchedule` pure math. Exact vocabulary, exhaustive:
  - Enabled, not in window: `NEXT · TODAY 05:00` / `NEXT · TOMORROW 20:00` /
    `NEXT · SAT 09:00` (day word when >1 day out).
  - Enabled, window contains now: `WINDOW OPEN — ENDS 08:00`
    (overnight: `WINDOW OPEN — ENDS 03:30 +1D`).
  - Window open + manual session overlapping: `HELD — MANUAL SESSION`
    (§5.6; the ENDS fact moves to the editor footer while held).
  - Window open + another routine's session running: `HELD — <NAME> RUNNING`.
  - Paused-until: line 3 is replaced by the pause line (§3.5) — one line,
    one current fact; NEXT returns when the pause does.
  - `daysMask == 0`: `WILL NOT RUN — NO DAYS SET`.
  - Disabled / auto-paused: no line 3. Line 4 carries what matters.
- **Line 4 — attendance line:** §5.7. Present only when there is a true
  sentence to print. Absence renders as absence.
- **The ticker:** one `nowMillis` per routines *section*, never per row:
  `mutableLongStateOf(System.currentTimeMillis())` in the Shield routines
  item, `LaunchedEffect` loop `delay(30_000)`. 30s cadence — every state
  flip this line can make is minute-granular; anything faster is theatre.
  The Section's now-line (§6) ticks at 60s off the same value (it reads the
  same state; two tickers for one clock is a second rendering of time).
- **Row tap target:** the whole row field (not the lane block, not the sled)
  toggles the editor. Minimum 76dp pitch ≥ 48dp target law. The sled owns
  its own target and consumes its taps (§2).

### Motion

- **Press:** no ripple (app-wide purge stands). Row field background washes
  one tone step toward recess at `specA(180)` in, `specA(260)` out; content
  settles `scale 0.99`, transform-origin (0f, 0.5f) — pushed at its rail.
- **Expand / collapse — one hoisted progress float:**
  `val progress by animateFloatAsState(if (expanded) 1f else 0f,
  if (expanded) specC(420) else specA(260))` hoisted in the row, passed to
  (a) `AnimatedVisibility(expandVertically(specC(420), Top) +
  fadeIn(specA(260)))` / exit `shrinkVertically(specA(260)) +
  fadeOut(specA(180))`, (b) the editor bands' stagger, (c) the Section
  leaders' length (§6 — leaders redraw from this same float each frame,
  never separately animated). Bands stagger on expand only: band *i* rides
  `bandP = ((progress − startᵢ)/(1 − startᵢ)).coerceIn(0,1)`, starts
  A 0.00 / B 0.12 / C 0.24 / D 0.36 / service 0.44 / footer 0.52, applied as
  `alpha = bandP; translationY = (1−bandP) × 8dp`. Collapse: no stagger, all
  bands ride down together — shutting a lid, not rewinding a ritual.
- **Accordion:** one editor open per section. Opening row B while A is open
  collapses A with the same 260ms exit; both animations run concurrently,
  neither waits.
- Meta/next/attendance text changes are **cuts**, never fades, except the
  sled-driven words in §2. A computed line re-strikes.

### Haptics

- Row press-down: none. Header tap commit: one firm click — waveform of
  `playFirmClick()` (8L family), fired by the row via the threaded
  `soundEngine` (the screen already owns it; `ShieldMatrixScreen.kt:77`
  today never passes it to `RoutineRow` — threading it in is part of this
  element's diff, precedent `FocusSequencerScreen.kt:108`).
- No haptic on expand *completion*, on ticker flips, on now-line movement,
  on any unattended event. Ever.

### Sound

- Expand: `playFirmClick()` once, on tap, from the row. Collapse:
  `playRotaryDetent(520.0)` once, on tap, from the row (a close is a detent
  step back, not a second click — direction must be audible).
- Everything inside the editor keeps wave-1's one-call-site table (§3.6).
  The row fires nothing else; the ViewModel fires the rest.

### Depth & light

- The row sits **on** the Section ground — no card, no fill, no shadow.
  Hairline 1dp `outlineVariant` under each row, owned by the row.
- Night staircase (locked): ground `#171412`; the editor panel recesses
  **below** ground (the old `surfaceVariant` #272320 inversion is dead);
  steps ≥1.35:1; text tiers 11 / 5.8 / 3.5 against ground; control fills
  cap `#4A423C`. All row colours resolve through tokens — zero literals.
- Light carries no data here: no highlight encodes armed-ness, progress,
  or attendance. The one light edge in this element is the editor panel's
  1dp top edge (alpha 0.5 day / 0.18 night), a material fact about a recess.

### State machine

Row display state = f(enabled, pausedUntilMillis, autoPausedAtMillis,
deleteAfterWindowEnd, windowOpen(now), heldBy, expanded):

| State | Name | Meta | Line 3 | Sled (§2) |
|---|---|---|---|---|
| DISABLED | secondary | normal | — | left, OFF |
| IDLE (enabled, waiting) | ink | ink days | `NEXT · …` | right, ON |
| WINDOW OPEN | ink | ink | `WINDOW OPEN — ENDS …` | right, ON |
| HELD | ink | ink | `HELD — …` | right, ON |
| PAUSED | secondary | ink days | `PAUSED UNTIL …` (§3.5) | left, PAUSED |
| AUTO-PAUSED | secondary | ink days | — (line 4 = note) | left, OFF |
| PENDING DELETE | secondary, dashed block (§6) | ink | `GOES AT 08:00` | inert |
| EXPANDED | = any above + editor below; line 2/3 keep rendering live | | | |

Expanded is orthogonal: any display state can be expanded except PENDING
DELETE (its editor is replaced by one line + `KEEP IT` word, §3.4).

### Edge cases

- **Name collision:** two routines may share a name. The row is identified
  by meta + block position; rename (§3.3) does not enforce uniqueness —
  his schedule, his labels. Duplicate suffixing (§3.2) keeps copies
  distinguishable by default, not by law.
- **Two windows open at once** (overlapping routines — legal after CRUD):
  both rows read `WINDOW OPEN`; the one whose session actually started
  is indistinguishable on the row by design — session-hood is the Deck's
  fact. The other reads `HELD — <NAME> RUNNING` per §5.6 (the receiver's
  first-come rule makes exactly one of them held, so at most one row
  ever claims a plain open window with a session behind it, and even
  that row only claims the *window*).
- **Ticker across midnight:** day words derive from `nextStartMillis`
  vs the calendar day of `nowMillis` at each tick — no cached "today".
- **Process death while expanded:** `expanded` is `remember(routine.id)`,
  not saved state. Recomposition lands collapsed. Correct: expansion is
  attention, not data.

### Code: helps / fights

- **Helps:** `RoutineSchedule.nextStartMillis / nextEndMillis /
  isActiveNow / durationMinutes` (`MonolithModels.kt:381–447`) already
  compute every fact line 2–3 print; nothing renders them today.
  `routineDaysLabel / routineLoadoutLabel / fmtRoutineTime`
  (`ShieldMatrixScreen.kt:292–309`) survive unchanged. The status drawer's
  `AnimatedVisibility` precedent (`MonolithCustomComponents.kt:337–340`)
  is the expand grammar's source.
- **Fights:** `RoutineRow`'s `expanded` is unkeyed `remember` today
  (`ShieldMatrixScreen.kt:322`) — list edits can inherit another row's
  expansion; keying by `routine.id` is mandatory. Routines sit in one
  `LazyColumn` item (`:167–188`) — height animation inside the item is
  safe; do **not** "fix" into `items {}`. `spacedBy(16.dp)` on the parent
  column (`:177`) dies — rows own their pitch and hairlines.

### Law check

ARM: zero cost — not on the arm path. HOLD: the row offers no verb that
touches a running session; editing a routine edits future windows only,
and §3.3/§3.4 fence the two edits that could lie about the present.
TRUTH: every line is derived from pure math or stored attendance at
render time; `WINDOW OPEN` never upgrades to a session claim.

---

## 2. ON/OFF as a miniature sled

### Mechanism

The wave-1 throw cap, kept and tightened under Yuki's throw contract
(mailed to her this wave — the contract as specced here: **one float,
one curve, tap-to-set first-class, drag as an addition never a
requirement, word is confirmation at the margin of the slot, never the
state itself**).

- **Hit rect (exclusive, per Yuki's contract):** 68dp wide (56dp slot +
  12dp left) × the row's full height (76dp). No other target may live
  inside it — the row's expand tap excludes this rect; two targets
  under one thumb is how accidental toggles are born. Visual slot
  56×36dp, `RoundedCornerShape(12.dp)`, recess fill (one step below
  ground day; night: recess below `#171412`), 1dp `outlineVariant`
  border. Sled 26×28dp, radius 8dp (concentric: 12 − 4 inset), travel
  22dp: offset x = `4.dp + 22.dp × throwFrac`.
- **One continuous value drives everything** (Yuki contract pt 1):
  `throwFrac ∈ [0f, 1f]`, `target = if (enabled && !paused &&
  !autoPaused) 1f else 0f`. Sled offset, travelled-line width + alpha,
  and sled fill (RGB blend between the two stop fills, a pure function
  of the float — no `animateColorAsState` twin, no discrete switch at
  the halfway point) are all functions of this one value.
- **Travelled line:** 2dp, centre height, from 6dp left inset to sled
  centre: width `6.dp + 22.dp × throwFrac`, alpha `0.55 × throwFrac`.
  OFF prints no line. The slot shows the travel; the line is its receipt.
- **Marker line:** one vertical line on the sled, 2×12dp, centred —
  ON: `onInkControlFill()`; OFF: text-secondary. One line per cap,
  family-wide.
- **The word — a margin readout, flips on ARRIVAL** (Yuki contract
  pt 7): `ON` / `OFF` printed in the slot's empty half (sled right →
  word left; sled left → word right), Anonymous Pro Bold 9.5sp,
  text-secondary, keyed to the **committed** state. It flips in the
  same arrival frame that fires the landing feel (first frame within
  max(1% of travel, 0.5dp) of target) — never mid-flight, never
  crossfaded: while the sled travels, the word still says where it
  *was*, because that is still where the schedule *is*.
- **Third word:** PAUSED (§3.5) — when `pausedUntilMillis > now`, the
  word is `PAUSED` (9.5sp; sled left leaves ~28dp of word space and
  `PAUSED` at 9.5sp Anonymous Pro ≈ 27dp — it fits; letterSpacing
  drops to 0 for this word only). Position + word agree: a paused
  routine sits OFF-ward because it will not fire.
- **Input:** tap = a throw to the opposite stop, through the same
  settle path as a drag (one recorded deviation from Yuki's
  side-tap-sets-side: this control is binary *inside a tappable row*,
  and side-selection would make taps near the sled ambiguous between
  set-side and expand — toggle semantics win here; everything else in
  her contract is adopted verbatim). Drag: 1:1 with grab offset
  (`grabOffset = touchX − sledLeftPx` on DOWN — never absolute
  position); past the ends, 0.32× follow, max 6dp overtravel; scroll
  surrender — if vertical beats horizontal (>12dp and >1.5×) before
  slop, the gesture CANCELS: silent return, the list scrolls.
  Release (drag): nearest stop wins; velocity ≥ 800dp/s confirms the
  far stop in the fling direction.
- **DOWN answers, silently, same frame:** slot scales 0.94 and the
  sled leans 2dp toward its target end (the Compose form of Yuki's
  contact-shadow answer). No sound, no haptic on DOWN — a press
  claims nothing. This pre-travel is intent; the settle is proof.

### Motion

Settle per Yuki contract pt 5: **`MechanicalOvershoot(1.25f)`** —
180ms if < 0.5 stop to travel, else 260ms (a full tap-throw is 260ms;
the overshoot is the shared mechanical vocabulary, and at 22dp it
reads as the sled seating against its end wall, not as bounce).
Returns (scroll-surrender CANCEL, any rejected change): CurveA,
180ms, **no overshoot**. Interrupting a throw with a second tap
retargets the same value — no queue, no lockout.

### Haptics

**One landing feel, owned by the view, fired at arrival — never on
UP, never by the ViewModel** (Yuki contract pt 6): ON (a routine
armed) → `fireFeel(4L)` latch; OFF → `fireFeel(1L, 820Hz)` detent.
No drag tick (a binary sled has no intermediate stop to tick).
**Code change this forces:** `toggleRoutine`'s
`soundEngine.playMechanicalLatch(...)` call (`MonolithViewModel.kt:428`)
**dies** — the VM persists silently for the sled path, or the gesture
double-fires into mush. Programmatic settles (auto-pause, pause
expiry, migration) are silent: `feelArmed = false` — a bulk or
machine change's sound belongs to a hand that touched something, and
no hand did.

### Sound

The landing feel *is* the sound (the engine pairs them in `fireFeel`'s
branches). Resume-from-pause via the sled (§3.5) lands with the same
ON latch — one voice for arriving at ON, whoever's tap caused it.

### Depth & light

Slot = recess (border + one-step tone), sled = raised within it (fill =
control fill, capped `#4A423C` at night; day ink `#2C2A29` via
`inkControlFill()`). No shadow; the travelled line and border carry the
depth. Specular: the sled samples the shared fixed light as a pure
function of its current offset — position only, never state meaning.

### State machine

| throwFrac target | Sled | Word | Entered by |
|---|---|---|---|
| 1 | right | ON | manual ON, pause expiry (silent re-throw on next composition — no latch sound: no hand did it) |
| 0 | left | OFF | manual OFF, auto-pause (also silent — §5.5) |
| 0 | left | PAUSED | pause-until set; leaving it (sled tap) = resume + ON throw with latch |

PENDING DELETE: sled inert (alpha 0.35, taps consumed silently — the
routine's fate is already written; a dead control must not pretend).

### Edge cases

- **Rapid double-tap:** second tap retargets mid-flight; the persisted
  `enabled` is the source of truth and the value chases it. A landing
  feel fires only when a settle actually arrives — a throw interrupted
  before arrival lands nothing and feels nothing.
- **Pause expiry while Shield open:** ticker flips `paused` false →
  target 1 → sled throws itself, silently. That self-throw is the
  happiest honest motion in this spec: the schedule resuming, visibly.
- **Row tap vs sled tap:** sled's clickable consumes; expansion never
  sees it (existing nesting behaviour, preserved deliberately).

### Code: helps / fights

- **Helps:** `toggleRoutine` + `persistRoutine` funnel
  (`MonolithViewModel.kt:419–430`) is exactly the single call site this
  control wants.
- **Fights:** today's cap (`ShieldMatrixScreen.kt:376–392`) is a colour-
  flip box whose fill is keyed to committed state — it teleports before
  it moves. Replaced wholesale. `inkControlFill()/onInkControlFill()`
  (`MonolithCustomComponents.kt:439/445`) stay the fill source.

### Law check

TRUTH: position is the state; the word only confirms; the fill cannot
arrive before the sled does because one float drives both. HOLD: toggling
a routine mid-session changes no running session — `persistRoutine`
reschedules future alarms only, and §5.6's receiver guard means an
in-flight manual session is never steamrolled by a sled thrown at 1 AM.

---

## 3. The editor + CRUD — the front door

### 3.0 The editor panel (one recessed program panel)

Mechanism: under the header row, one slab — full row-field width,
`RoundedCornerShape(12.dp)`, recess fill one step below ground (night:
below `#171412` per the locked staircase), 1dp `outlineVariant` border,
12dp inner padding, bands separated by 10dp gaps + 1dp hairlines:

- **Band 0 — NAME:** the routine's name, Sora Bold 17sp, tappable →
  becomes the rename field (§3.3). Right-aligned word: `RENAME` 9sp
  while untouched. This band exists because rename needs a home that
  isn't a dialog.
- **Band A — START**, **Band B — END:** label (Anonymous Pro Bold 10sp,
  40dp) · recessed display (panel-ground tone, Anonymous Pro Bold 16sp,
  54dp, centred; overnight end appends `+1` at 8.5sp Bold, baseline-
  raised) · four keys `−30 −5 +5 +30`, `weight(1f)`, 40dp tall, 6dp gaps.
- **Band C — WEEK STRIP:** 7 cells, hairline-divided, letter over
  micro-name (`MON TUE WED THU FRI SAT SUN` at 8.5sp under 13sp Bold) —
  the duplicate-letter ambiguity (`T…T`, `S…S`) dies here. Selected =
  ink fill, concentric 8dp inset radius; unselected = text-secondary.
- **Band D — LOADOUT SLED:** 3-stop mini selector, 40dp track recessed
  to display tone, sled `cellW − 8.dp` × 32dp, `specA(260)` throw —
  same grammar as §2 at a second size. Labels `STUDY / WORK / SLEEP`
  11sp Bold; the cell under the sled takes `onInkControlFill()`.
  **Directly tappable per cell** — this requires `setRoutineLoadout`
  (§3.6); the wave-1 cycle-only fallback is dead in this plan because
  CRUD wave is exactly when one 3-line VM function is affordable.
- **Band E — SERVICE ROW (new; CRUD lives here):** word-actions,
  Anonymous Pro Bold 10sp, 1sp tracking, each ≥48dp hit height via
  vertical padding, laid out left→right: `DUPLICATE` · `PAUSE UNTIL…`
  (or `RESUME` when paused) · — ≥32dp isolation gap — · `DELETE`
  (underlined; isolation + underline carry the danger, never colour).
- **Footer — the machine restates its program:** hairline, then one
  line, Anonymous Pro 10sp, single line, ellipsis:
  `ARMS STUDY · DAILY · 05:00 → 08:00 · 3H 00M WINDOW · NEXT TODAY 05:00`
  Disabled: prefix `DISARMED ·`. No-days: `NO DAYS SET — THIS PROGRAM
  WILL NOT RUN` at full ink Bold. SLEEP-loadout routines append
  `· THEME FOLLOWS THIS WINDOW` (§7). Auto-paused: footer is replaced
  by the auto-pause sentence (§5.5). Held: footer appends
  `· NOW HELD BY YOUR SESSION — THIS WINDOW STILL ENDS 08:00`.

Motion: §1's hoisted progress + band stagger. Panel internal controls
never run their own visibility animations. Display readouts blink on
accepted step: `Animatable` snap to alpha 0.35 → `specA(180)` back to
1f, keyed to the minutes value; a clamped step changes nothing, so the
refusal is silence + stillness, app-wide grammar.

Haptics & sound: §3.6 table — one call site per action, all pre-existing
engine voices, zero new sound call sites in Compose beyond §1's two.

Depth & light: three tonal steps nest only inside the slab: panel
(recess) → displays (one step deeper — toward ground and below) → keys
(raised, `surfaceBright` day / capped step night). Wave-1's day hexes
are superseded by the locked night staircase; the *relationships*
(recess → deeper → raised) are what survive.

State machine (editor): CLOSED → OPEN (tap) → OPEN·RENAMING (Band 0
tap) → OPEN (commit/revert) ; OPEN → OPEN·PAUSE-SHEET (Band E) →
OPEN (sheet is a modal layer over Shield, §3.5) ; OPEN → CLOSED (tap
header, open another row, delete, discard-draft). Only one editor, one
sheet, one draft at a time — enforced by section-level state, not by
each row hoping.

Edge cases: editor open while ticker flips the window state — all lines
are derived at render; nothing in the panel caches "was idle". Editor
open across a firing: nothing in the panel blocks, confirms, or
announces the firing (NONE concept); the row's line 3 simply becomes
`WINDOW OPEN` at the next tick.

### 3.1 Time keys — coarse + fine, auto-repeat, the clamp fix

Mechanism: `RoutineKey` — one private composable in
`ShieldMatrixScreen.kt` (not `BlockitActionChip`: Atkinson dialog-chip
grammar, wrong surface). Radius 12dp, raised fill, 1dp border, label
Anonymous Pro Bold 12.5sp. Pressed: scale 0.94 `specA(180)`, fill sinks
to panel tone.
Input via `Modifier.pointerInput` + `detectTapGestures(onPress)` —
**replaces** `clickable` on keys (both together double-fire). Recipe:
press → one step immediately → `delay(400)` → repeat every `120ms`
until release. 400ms arm means a tap never repeats; held `+30` sweeps
≈4H per second, each accepted step ticking (sound below). The repeat
loop must read the latest entity via `rememberUpdatedState` — a held
key stepping a stale routine is the known trap from wave-1.
**Clamp fix (code fight, decided — Reihe's catch, verified):** two
bounds currently disagree — `adjustRoutineTime` rejects raw spans
outside `30..720` (`MonolithViewModel.kt:447–453`) while
`RoutineSchedule.durationMinutes` coerces to `15..720`
(`MonolithModels.kt:381–385`). A window between the two lies would
print one length and run another. **Both bounds unify at the raw
wrapped span, `30..1080`:** `adjustRoutineTime`'s clamp becomes
`30..1080`, and `durationMinutes`' coercion becomes
`coerceIn(30, 1080)` — after which printed duration ≡ raw span for
every storable routine, by construction, and DAY BLOCKS (630) sits
mid-range instead of near a cap invented for smaller days. Keys that
would exit the span render alpha 0.35 and their steps fall into the
clamp silently. Midnight wrap is never a clamp event (times wrap mod
1440 in the VM, as today).

### 3.2 Create (scratch + duplicate) and the draft state

**From scratch — the front door:** section foot, under the strip legend
(§6): `+ NEW ROUTINE` — a word, ink, Anonymous Pro Bold 11sp, 48dp hit
box, left-aligned where a list would put its next row. No filled button;
a filled button would be Shield spending form it doesn't spend elsewhere.
- Tap → a **local draft** appears: a row in edit state, inserted at its
  sorted position in the Section (block drawn dashed + dotted inner
  line — the unsaved line style, §6), editor open, Band 0 field focused.
  Defaults: name `NEW ROUTINE`, 09:00–10:00, DAILY, STUDY, enabled.
  Footer: `NOT SAVED — SET THE WINDOW` + service row replaced by
  `SAVE` (ink) and `DISCARD` (plain word).
- **The draft is not in the database.** No id, no alarms, no attendance,
  no ghost in any count (section header counts persisted routines only).
  Abandoning it — DISCARD, tapping another row, leaving Shield — deletes
  nothing because nothing was written. TRUTH: an unsaved program must
  never be mistakable for a scheduled one, in data or in drawing.
- SAVE → `viewModel.createRoutine(...)` (§3.6) → insert returns the real
  id → `RoutineScheduler.scheduleRoutine` → the row re-renders as a
  normal row, still expanded, footer now restating the program in the
  §3.0 form. Sound on save: `playHarmonicSeal(arming = true)` — a new
  program sealing into the schedule is the one creation act that earns
  the seal voice. Haptic: 10L completion family via the same call path
  the engine already pairs.
- One draft at a time: while a draft exists, `+ NEW ROUTINE` dims to
  text-secondary and its tap scrolls to the draft instead of spawning.

**Duplicate:** Band E `DUPLICATE` on any persisted routine.
- Immediate persist (a copy of a rested decision is itself rested —
  no draft state): `viewModel.duplicateRoutine(routine)`.
- Name: `<NAME> COPY`; duplicating a `… COPY` yields `… COPY 2`, then
  smallest free integer suffix; names truncate at 28 chars *before*
  suffixing so the suffix always survives.
- Copies: start/end/days/loadout/enabled. Does **not** copy:
  `pausedUntilMillis` (pause is a circumstance of the original's life,
  not part of its program), attendance (history is not heritable),
  auto-pause state (the copy has missed nothing).
- The copy inserts at its start-sorted position, auto-expands; the
  original collapses (accordion). Scheduler registers the copy.
  Sound: `playFirmClick()` from the VM.

### 3.3 Rename

- Band 0 tap → inline `BasicTextField`, Sora SemiBold 17sp (real weight),
  hairline underline in ink, IME action Done. Commit on Done or focus
  loss. Store trimmed, as typed (display uppercases); empty commit
  **reverts** and the footer flashes the standing line for 2s:
  `NAME KEPT — EMPTY NAMES DON'T SAVE`. 28-char cap enforced in-field.
- Sound: commit `playFirmClick()` (VM); revert: silence.
- **The rename fence (load-bearing, from a real code fight):**
  `RoutineAlarmReceiver` BOUNDARY_END recognises its session by
  `active.profileName == "Routine: ${routine.name}"`. Renaming a
  routine *while its window is open* renames the string the END
  boundary will look for — the session would never stand down by
  schedule. Therefore: **while the routine's window is open, Band 0 is
  inert** and carries the line `RENAME AFTER THIS WINDOW ENDS —
  08:00`. No other edit is fenced (times/days/loadout are read from the
  DB at fire time; only the name is baked into a live session).
  Long-term fix, named but not specced (belongs to whoever touches
  sessions): store `routineId` on `FocusSessionEntity` and match END
  by id. Until then the fence is the truth.

### 3.4 Delete — no dialog, a 5-second undo

- Band E `DELETE` (routine not in an open window): **no confirmation.**
  Rationale, stated once: confirmation dialogs are the tired-user tax
  on a rested-user surface — he is in Shield, in the editor, having
  tapped a word isolated by 32dp and underlined; the decision is
  already rested. What delete needs is not a second question but a
  *reversible first second*. So: tap → row collapses (`specA(260)`),
  `RoutineScheduler.cancelRoutine(id)`, row removed from DB via
  `deleteRoutine`, and the section foot shows the undo strip for
  **5 seconds**: `ROUTINE DELETED — <NAME> · UNDO` (Anonymous Pro 10sp;
  UNDO ink Bold, 48dp hit). This reuses the app's existing task-undo
  pattern (`_deletedTaskForUndo`, 5s job, `MonolithViewModel.kt`
  `deleteTaskWithUndo`) — same duration, same grammar, second surface.
- UNDO → re-insert **with the same id** (the VM holds the entity
  snapshot), `scheduleRoutine` re-registers, row returns collapsed.
  Sound: delete `playMechanicalLatch(false)`; undo
  `playHarmonicSeal(true)` — task-undo precedent.
- Attendance on delete: rows for that routine are **not** deleted with
  it. They are purged when the undo window lapses (VM job, same 5s) —
  and if the process dies inside the window, undo dies with it and the
  next `ensureSeededAndSyncPackages` purges orphan attendance (routine
  id not in `routines`). Stated plainly: **undo does not survive
  process death.** Deleting a routine is, after 5 seconds, deleting
  its record too — the record's home was the routine (§5.7).
- **Delete inside an open window (pending delete):** the session the
  routine started must not be orphaned (its END match is name-based,
  §3.3, and its alarms are the schedule). So in-window delete does not
  delete: it sets `deleteAfterWindowEnd = true`, cancels the *START*
  alarm only (the END alarm must live to stand the session down), the
  block draws dashed, line 3 becomes `GOES AT 08:00`, the sled goes
  inert, and the editor is replaced by one line + `KEEP IT`:
  `THIS ROUTINE ENDS WITH ITS WINDOW AT 08:00 — THEN IT'S GONE.`
  `KEEP IT` clears the flag and re-registers START. At BOUNDARY_END,
  the receiver — after standing the session down and booking
  attendance (§5) — deletes the routine + cancels + purges attendance
  (no undo from a receiver; the decision was rested and confirmed by
  the window completing — the undo strip appears next Shield visit is
  *wrong*, so no strip: the footer line he saw at delete time said
  exactly when it would happen; TRUTH was told once, in advance).
- SLEEP routines get no extra protection and no extra warning beyond
  the §3.0 footer suffix that already says the theme follows them.

### 3.5 Pause-until-date

- Band E `PAUSE UNTIL…` → bottom sheet (Shield's sheet grammar, panel
  tone, 24dp top radius — the theme's large radius, used only here and
  dialogs): title `PAUSE <NAME> UNTIL`, Anonymous Pro.
- **Quick words row:** `TOMORROW` · `+3 DAYS` · `NEXT MONDAY` — each
  sets the calendar selection, nothing commits until the confirm word.
- **Calendar:** a true 28-day grid built in Compose from
  `java.util.Calendar` (no new dependency, no system DatePickerDialog —
  its chrome would be a foreign object in this app): 4 rows × 7,
  month label(s) above, each cell = day letter + date number (Anonymous
  Pro 9sp / Bold 13sp), 44dp cells, selected = ink fill + on-ink text,
  today and past disabled at alpha 0.35 (pausing until today is OFF
  with extra steps — the grid refuses it), beyond-28-days absent with
  the sheet's standing line: `LONGER THAN 28 DAYS — SWITCH IT OFF
  INSTEAD.`
- **Semantics, exact:** choosing date D stores
  `pausedUntilMillis = D at 00:00 local`. The routine fires for no
  window *starting* before that instant; it resumes with the first
  window starting on/after it. Display everywhere: `PAUSED UNTIL
  FRI 30 OCT` (the chosen date — the day it comes back is the first
  day whose window can fire; for a DAILY routine that is D itself).
- If a window is open when pause is set: sheet carries the line
  `THE OPEN WINDOW STILL RUNS TO 08:00.` Pause governs future firings;
  it never reaches into a live window (HOLD).
- Confirm word `PAUSE` (ink fill); cancel word `KEEP RUNNING`. Confirm →
  `viewModel.pauseRoutineUntil(routine, pausedUntilMillis)` →
  persist + reschedule: `scheduleRoutine` with the pause-aware next
  start (§3.6) — **no separate resume alarm exists**: the scheduler
  registers the first post-pause START directly, so expiry needs no
  event; the sled's silent re-throw (§2) is the visible half.
  Sound: `playMechanicalLatch(false)`. Date-cell taps:
  `playRotaryDetent(620.0)` each (1L pitched); quick words:
  `playFirmClick()`.
- Resume early: sled tap (§2) or Band E `RESUME` →
  `viewModel.resumeRoutine(routine)` clears `pausedUntilMillis`,
  persists, reschedules — silently in the VM (§3.6): the sled's
  landing latch or the RESUME word's firm click speaks for the surface
  that was touched.
- While paused: time/day/loadout edits remain available (rested work
  on a resting routine is exactly right); every edit keeps the pause
  unless it is a sled/RESUME action. Line 3 shows the pause; the
  Section block draws dashed (§6).

### 3.6 ViewModel + DAO additions — by name

**Entity changes — `RoutineEntity` (`MonolithModels.kt`), 3 new fields:**

| Field | Type / default | Meaning |
|---|---|---|
| `pausedUntilMillis` | `Long = 0L` | 0 = not paused; else first instant a window may start |
| `autoPausedAtMillis` | `Long = 0L` | set by §5.5 auto-pause; cleared by any manual enable/edit |
| `deleteAfterWindowEnd` | `Boolean = false` | §3.4 pending delete |

**New entity — `RoutineAttendanceEntity`** (§5.2 for full field spec),
table `routine_attendance`, unique index `(routineId, windowStartMillis)`.

**DB version: 7 → 8**, one `Migration(7, 8)`: `ALTER TABLE routines ADD
COLUMN` ×3 + `CREATE TABLE routine_attendance` + the reseed-settings
columns (§4: `routineReseedV1Done`, `routineReseedNoteDismissed` on
`guard_settings`). **Code fight, flagged hard:** `MonolithDatabase.kt:49`
ships `fallbackToDestructiveMigration(true)` — left as-is, the v8
upgrade *wipes the database* and §4's migration migrates nothing. This
plan requires the explicit migration registered for (7→8); the
destructive fallback may remain for older gaps but must never be the
7→8 path. Test: upgrade install with edited routines survives.

**DAO additions — `MonolithDao.kt`:**
```kotlin
@Insert(REPLACE) suspend fun insertRoutine(routine: RoutineEntity): Long
@Query("DELETE FROM routines WHERE id = :id") suspend fun deleteRoutineById(id: Int)
@Insert(REPLACE) suspend fun insertAttendance(e: RoutineAttendanceEntity): Long
@Update suspend fun updateAttendance(e: RoutineAttendanceEntity)
@Query("SELECT * FROM routine_attendance WHERE routineId = :routineId AND windowStartMillis = :ws LIMIT 1")
  suspend fun getAttendance(routineId: Int, ws: Long): RoutineAttendanceEntity?
@Query("SELECT * FROM routine_attendance WHERE routineId = :routineId ORDER BY windowStartMillis DESC LIMIT :limit")
  fun getAttendanceFlow(routineId: Int, limit: Int = 10): Flow<List<RoutineAttendanceEntity>>
@Query("SELECT * FROM routine_attendance WHERE routineId = :routineId ORDER BY windowStartMillis DESC LIMIT 1")
  suspend fun getLatestAttendance(routineId: Int): RoutineAttendanceEntity?
@Query("DELETE FROM routine_attendance WHERE routineId = :routineId")
  suspend fun deleteAttendanceForRoutine(routineId: Int)
@Query("DELETE FROM routine_attendance WHERE windowStartMillis < :cutoff")
  suspend fun purgeAttendanceBefore(cutoff: Long)
@Query("DELETE FROM routine_attendance WHERE routineId NOT IN (SELECT id FROM routines)")
  suspend fun purgeOrphanAttendance()
```
(`insertRoutines`, `updateRoutine`, `getRoutineById`,
`getAllRoutinesSnapshot` already exist and are reused.)

**Repository additions — `MonolithRepository.kt`:**
`createRoutine(name, start, end, daysMask, loadoutName): RoutineEntity`,
`duplicateRoutine(source): RoutineEntity`, `deleteRoutine(id)`,
`restoreRoutine(snapshot)`, `setRoutinePausedUntil(id, millis)`,
`recordAttendanceEvidence(routineId, kind)` (§5.3),
`finalizeAttendance(routineId, windowStart)` (§5.4),
`purgeOldAttendance()` (90-day, called from seed-sync),
`migrateRoutinesToRealDayIfNeeded()` (§4).

**ViewModel additions — `MonolithViewModel.kt`** (all persistence goes
through the existing private `persistRoutine` funnel or its two new
siblings — there is no fourth path):
```kotlin
fun createRoutine(name: String, startMinutes: Int, endMinutes: Int,
                  daysMask: Int, loadout: SmartLoadout)   // seal sound; insert + schedule
fun duplicateRoutine(routine: RoutineEntity)               // firm click; insert + schedule
fun renameRoutine(routine: RoutineEntity, newName: String) // firm click; persist (no reschedule needed — see below)
fun deleteRoutine(routine: RoutineEntity)                  // latch(false); cancel + delete + 5s undo state
fun undoDeleteRoutine()                                    // seal; re-insert same id + schedule
fun pauseRoutineUntil(routine: RoutineEntity, untilMillis: Long) // latch(false); persist + schedule
fun resumeRoutine(routine: RoutineEntity)                  // silent in VM (§3.6 note); persist + schedule
fun setRoutineLoadout(routine: RoutineEntity, loadout: SmartLoadout) // firm click; persist
private fun persistNewRoutine(routine: RoutineEntity)      // insert + scheduleRoutine
private fun removeRoutineNow(routine: RoutineEntity)       // cancelRoutine + delete
val deletedRoutineForUndo: StateFlow<RoutineEntity?>       // + dismiss job, task-undo pattern
```
`toggleRoutine` changes in exactly three ways: turning OFF also clears
`pausedUntilMillis` (OFF is the stronger word — a zombie resume after a
manual OFF would be the machine deciding again); turning ON clears
`autoPausedAtMillis`; and **its `playMechanicalLatch` call is deleted**
— the sled's landing feel is view-owned (§2, Yuki contract pt 6), so
the VM persists silently on this path. Likewise `resumeRoutine` is
silent in the VM: reached from the sled, the landing latch speaks;
reached from Band E's `RESUME` word, the composable fires
`playFirmClick()` via the threaded `soundEngine` (§1's pattern). `persistRoutine` also clears `autoPausedAtMillis`
on any manual edit (an edit is a human looking at the routine — the
auto-pause note has been seen and superseded).

**Scheduler re-registration points (exhaustive):** create ✓ · duplicate
✓ · delete = cancel ✓ · undo = register ✓ · toggle ✓ · pause ✓ ·
resume ✓ · time adjust ✓ (existing via persist) · day toggle ✓
(existing) · loadout = via persist (harmless; the receiver reads the DB
at fire time, so loadout is not in the intent) · **rename = NONE** —
PendingIntent request codes are `routineId × 2 (+1)`, extras carry only
the id; the name lives in the DB. Stating this is part of the spec:
anyone "fixing" rename to reschedule introduces churn for nothing ·
auto-pause ✓ (receiver calls `scheduleRoutine`, which returns early on
`!enabled`) · pending-delete set = cancel START only, keep END ·
pending-delete executed at END = cancel both · reseed migration =
cancel-all + `scheduleAll` (§4) · boot = `MonolithBootReceiver` already
calls `scheduleAll` ✓ · pause expiry = **no event** (first post-pause
START was registered at pause time).

**Scheduler change (one function):** `RoutineScheduler.scheduleRoutine`
becomes pause-aware — the effective search origin for next START is
`max(now, routine.pausedUntilMillis)` fed into
`RoutineSchedule.nextStartMillis(routine, origin)` (its strictly-after
contract means passing `pausedUntilMillis − 1` when origin == pause —
the helper `effectiveNextStartMillis(routine, now)` in
`RoutineSchedule` owns this arithmetic so the Deck ribbon (§5.8 mail to
Ines) and the scheduler can never disagree). END scheduling is
unchanged (nextEnd from now), except pending-delete routines keep END
and lose START per above.

### Law check (§3 whole)

Rested-decides is the editor's entire defence: every destructive or
durable act sits ≥3 taps deep (Shield → row → Band E), in daylight,
labelled in words. No act in this section fires during a session, gates
a session, or asks anything at fire time. TRUTH: draft ≠ saved, in both
drawing and database; delete tells its timing in advance; rename's
fence states its reason on its face.

---

## 4. Reseed migration — old seeds become his real day

### Mechanism

One-time, flag-guarded, inside `ensureSeededAndSyncPackages` under the
existing `seedMutex`, **before** the ViewModel init sequence's
`scheduleAll` (`MonolithViewModel.kt:~297`) reads the snapshot — so the
alarms registered on first post-update launch are already the new
day's. Flags live on the single-row `GuardSettingsEntity` (v8 columns):
`routineReseedV1Done: Boolean = false`,
`routineReseedNoteDismissed: Boolean = false`.

**Match rule — EXACT, all four fields, or the row is not a seed:**

| Old seed | name | start | end | daysMask | loadoutName |
|---|---|---|---|---|---|
| S1 | `Morning Deep Work` | 540 (09:00) | 780 (13:00) | 31 (MON–FRI) | `STUDY` |
| S2 | `Night Wind-Down` | 1350 (22:30) | 420 (07:00) | 127 (DAILY) | `SLEEP` |

`enabled` is deliberately **not** in the match (the brief's field list)
and is **carried over**: a seed he had switched off migrates to its
successor, still off. Toggling is use, not editing — it must not cost
him the migration or resurrect a routine he killed.

**Transforms (in place, id preserved):**
- S1 → **DAY BLOCKS**, 09:00–19:30 (540–1170), DAILY 127, STUDY.
- S2 → **SLEEP**, 20:00–03:30 (1200–210), DAILY 127, SLEEP.
- **FASTED BLOCK** (05:00–08:00, 300–480, DAILY, STUDY, enabled) has no
  ancestor — inserted fresh.
- After transforms: for each of the three target routines, if no row
  now matches its exact (name+times+days+loadout), insert it — unless a
  row already carries that exact **name** (he pre-created it by hand in
  some future build; name-collision means hands off). An *edited* old
  seed (any of the four fields differs) is **untouched** — and its
  successor is still inserted by the rule above. Overlap between his
  edited row and the inserted successor is possible; it is drawn
  honestly by the Section's lane-split (§6) and resolved by him with
  §3's tools. The migration never edits a row he edited. Full stop.

**The postponed row:** if, at migration time, an active session exists
with `profileName == "Routine: <old seed name>"` for a row about to be
transformed, that row's transform is **postponed** (the §3.3 name-match
fight, migration edition: renaming in place would orphan the live
session's END). The flag stays not-done; every subsequent seed-sync
retries; the row transforms on the first sync with no such session
(bounded by the session's own ≤12H life). Other rows transform
normally around it. FASTED insertion is not postponed (it orphans
nothing).

**Effect on `nextStartMillis`:** none stored, nothing to migrate — it
is pure math over the row, recomputed at every render and every
`scheduleRoutine`. The instant a row's fields change, every NEXT line
in the app is already telling the new truth.

**Effect on pending alarms:** after transforms + inserts, for every
routine id that existed pre-migration: `cancelRoutine(id)`; then
`scheduleAll(post-migration snapshot)`. Transformed rows keep their
ids so their PendingIntents would be overwritten anyway — the explicit
cancel-all is belt-and-braces against an id the transform path missed,
and it costs four lines. New FASTED row registers through `scheduleAll`.

**First-run-after-update behaviour:** no modal, no Deck interruption,
no first-launch ceremony (ARM is untouched; a migration that taxes the
arm path is a concept, and the concept is NONE). The one trace: a
one-time strip under the ROUTINES header, Anonymous Pro 10sp:
`SCHEDULE RESET TO YOUR DAY — TAP ANY ROW TO CHANGE IT`, shown until
he opens any routine editor (that tap sets `routineReseedNoteDismissed`)
or 14 days pass. Told once, in human language, then gone.

**Fresh installs:** the seed-on-empty block in
`MonolithRepository.ensureSeededAndSyncPackages` now seeds the three
real routines directly (replacing the two old seeds at
`MonolithRepository.kt:82–103`), and sets `routineReseedV1Done = true`
in the same pass — a fresh install never "migrates" and never sees the
strip.

### Motion / Haptics / Sound

None. A migration is bookkeeping; it does not perform. If he is looking
at Shield during the (sub-second) pass, rows re-render as cuts — the
computed-number rule: new values re-strike, never animate from old ones.

### Depth & light

The note strip is text on ground, hairline below. No banner fill, no
accent — a schedule note is not a commit.

### State machine

`NOT_STARTED → (sync, no active-seed-session) → DONE` ·
`NOT_STARTED → (postponed row exists) → PARTIAL → (later sync, session
gone) → DONE`. Flag flips only at DONE. A crash mid-pass is safe:
transforms are per-row `updateRoutine` writes; a re-run re-matches —
already-transformed rows no longer match S1/S2, untransformed ones
still do. The pass is idempotent by construction of the match rule.

### Edge cases

- **Both seeds edited:** nothing transforms; all three real routines
  insert (5 rows total). Correct per his rule — edited rows are sacred,
  and the reseed still delivers his day. The header strip earns its
  keep on exactly this install.
- **Routines table empty at update** (he never opened the app? seeds
  are written on first sync, so empty ⇒ pre-seed install): seed path
  handles it — three real routines, flag set.
- **Clock/timezone change between match and transform:** fields are
  minutes-of-day; timezone never enters the match.
- **The in-flight session postponement + he never reopens the app:**
  the session's own END alarm still fires (old name intact because the
  row was not renamed), stands the session down, and the next boot's
  `scheduleAll` path runs seed-sync first in VM init on next launch —
  transform lands then. No state can wedge: postponement only ever
  delays, and the delay's cause expires on its own.

### Code: helps / fights

- **Helps:** seed-on-empty + `seedMutex` + the ensure→scheduleAll init
  ordering already exist; the migration is a tenant of that structure,
  not a new subsystem. `insertRoutines` is seed-capable today.
- **Fights:** `fallbackToDestructiveMigration(true)` (§3.6) would erase
  the very rows this section migrates — the v8 explicit migration is a
  precondition of this section, not a nicety. The old seed constants
  live only in `MonolithRepository.kt:85–101`; there is no second seed
  site to keep in sync (verified by grep — `insertRoutines` has one
  caller).

### Law check

TRUTH: exact-match-or-untouched is the whole ethic in one rule — the
machine rewrites only what the machine wrote. Rested-decides: the
reset is his standing decision, executed once, announced once, and
every resulting row is editable by §3 from the first second.

---

## 5. The no-ghost rule — attendance, misses, conflicts, drift

**The load-bearing sentence:** *a routine never records a session he
wasn't in.* Today it does — §5.1 is the proof — and this section is the
fix. Everything else (evidence, outcomes, auto-pause) is machinery in
service of that sentence.

### 5.1 What ships today (the ghost, verified in code)

`RoutineAlarmReceiver` BOUNDARY_START calls
`repository.startSession(durationMinutes = full window)` whenever no
session is active. BOUNDARY_END calls
`repository.disarmActiveSession(completed = true, …)`, which writes
`elapsedSeconds = max(verified wall-clock elapsed, …)` — wall-clock,
whether or not a human ever touched the phone. `compute7DayHistory`
then sums that elapsed into his study totals, and completion mints a
Curio artifact (`updateSessionProgress` forges one on completion).
An untouched phone on a desk earns a full 3H FASTED BLOCK, marked
completed, with a prize. That is the ghost, and it is not a rendering
bug — it is the receiver + repository path working as written.

### 5.2 The attendance record — data

```kotlin
@Entity(tableName = "routine_attendance",
        indices = [Index(value = ["routineId", "windowStartMillis"], unique = true)])
data class RoutineAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routineId: Int,
    val windowStartMillis: Long,        // SCHEDULED start — the streak's clock
    val windowEndMillis: Long,          // SCHEDULED end
    val firedAtMillis: Long = 0L,       // ACTUAL START delivery; 0 = never delivered
    val attendedSeconds: Int = 0,       // accrued ONLY by §5.3 sampling
    val evidenceMask: Int = 0,          // bit per evidence kind (below)
    val firstEvidenceMillis: Long = 0L,
    val lastEvidenceMillis: Long = 0L,
    val outcome: String = "SCHEDULED",  // SCHEDULED → ATTENDED|PARTIAL|MISSED|HELD|LATE_FIRE|NOT_DELIVERED (MISSED_AUTOPAUSED = spent 3rd miss, §5.5)
    val driftMinutes: Int = 0           // firedAt − windowStart, if delivered
)
```
Retention: 90 days, purged in seed-sync (§3.6). Entries exist
**only** for windows that were scheduled to fire — routine enabled,
unpaused, day in mask. Days off, paused days, disabled periods produce
**no entry**: absence in the record means "not scheduled", never
"missed". The record cannot be weaponised against a day he chose off.
**Creation points (idempotent upsert on the unique index):** START
delivery creates the entry (outcome SCHEDULED); END delivery creates
it if missing (a START lost to a dead process still gets its window
judged); the boot/seed-sync sweep creates-and-finalises entries for
any scheduled window whose end has passed with no entry at all
(phone off through the whole window → MISSED, `firedAtMillis = 0`).
No window that was scheduled can escape the record, and no entry can
be created twice.

### 5.3 Attendance evidence — what counts, what never counts

An **attended minute** is a minute inside the *actual* window
(`firedAt` → scheduled end) in which the routine-started session is
active and evidence exists within the trailing **5 minutes**. Presence
is sampled, not assumed continuous; evidence decays after 5 minutes.

**Counts (evidenceMask bits):**
- **E1 · Session interaction (bit 0):** any deliberate act on the
  running session's surfaces — Deck session controls, task sub-step
  toggle, task DONE, task add — while the routine session is active.
- **E2 · Foreground return (bit 1):** Monolith activity `ON_RESUME`
  while the routine session is active. He picked the phone up and
  came to the app that is holding his session.
- **E3 · Intercept handled (bit 2):** `MonolithShieldService` records a
  blocked-app intercept, a 2-minute Peek lease start, or an airlock
  transmute during the session. A deflection only happens if a hand
  tried — presence proven by resistance.
- **E4 · Emergency interaction (bit 3):** the 60-second hold is
  started during the session. (If it completes, the session ended by
  his one weekly break — attendance finalises at that moment with
  whatever accrued; outcome computed on accrued vs elapsed window.
  A man who spends his break was there.)

**Never counts:**
- **N1** the alarm firing, the session starting, the shield service
  starting — the machine's acts are not his attendance.
- **N2** screen-on / device unlock without Monolith foreground or a
  session-surface act. Checking the time is not studying.
- **N3** other apps' foreground/usage time. Books are not apps;
  Monolith cannot see study and will not infer it from phone data.
- **N4** wall-clock elapsed of the session by itself.
- **N5** AOD / desk-clock visibility without touch — ambient by design.
- **N6** notification interactions outside Monolith, calls, alarms.

**Accrual mechanism:** one funnel —
`repository.recordAttendanceEvidence(routineId, kind)` — called from
the ViewModel (E1, E2 via the activity-resume hook the VM already
observes) and from `MonolithShieldService` (E3, E4). It stamps
`lastEvidenceMillis` (+ first, + mask bit) on the SCHEDULED row.
The shield service — already alive for the session's whole life —
samples every **30s**: if `now − lastEvidenceMillis ≤ 5min`, it adds
30s to `attendedSeconds` (capped at the window's scheduled length).
**Failure direction is specified: if the service dies, sampling stops
and attendance undercounts. When in doubt, book less.** An undercount
costs him minutes he earned; an overcount costs the record its truth.
The record's truth is the product.

### 5.4 Outcomes — what a firing books

Finalised at BOUNDARY_END by `finalizeAttendance` (called from the
receiver after its session handling), or at emergency-break completion,
whichever ends the routine session first. Let A = attendedMinutes,
W = window minutes (scheduled), R = actual remainder when fired late
(§5.6 drift):

| Outcome | Condition | What the SESSION books | Streak effect |
|---|---|---|---|
| **ATTENDED** | A ≥ W − 15 | Stands as a normal session: elapsed = verified, `completedSuccessfully = true` | resets miss streak |
| **PARTIAL** | 15 ≤ A < W − 15 | Session elapsed **rewritten to attendedSeconds** (`disarmRoutineSessionPartial`), `completedSuccessfully = false`, no artifact forged | resets miss streak (he came) |
| **MISSED** | A < 15 | Session **zeroed**: `elapsedSeconds = 0`, `completedSuccessfully = false`, `forgedArtifactId = null` (`disarmRoutineSessionUnattended`) — contributes 0 to every total, forever | **+1 miss** |
| **HELD** | §5.6 — manual/earlier session occupied the window | No routine session exists to book. Attendance entry only; its minutes are the manual session's own and are **never** added anywhere | resets miss streak |
| **LATE_FIRE** | START delivered with R < 30min (drift, §5.6) | Books exactly like ATTENDED/PARTIAL/MISSED judged on A vs **R**, except MISSED is impossible: A < 15 books PARTIAL-at-A (even 0 → entry stands, session zeroed) | **neutral** — neither resets nor increments. The machine's lateness is not his pattern. |
| **NOT_DELIVERED** | START never delivered + a boot occurred inside the window (§5.6) | No session exists; nothing books | **neutral** — a dead phone is not an absence |

Notes on the thresholds: the 15-minute floor is the smallest presence
that is unmistakably a person choosing to be there. Under 15 minutes
inside a ≥30-minute window is a cameo, and cameos are recorded in the
entry's `attendedSeconds` but **not booked** — the entry remembers,
the totals don't claim. The W−15 grace at the
top absorbs start/end friction (he arrives, the machine was already
running; he leaves at 07:52 of an 08:00 window — that is attendance).

Two new repository methods carry the session side, because
`disarmActiveSession` as written cannot express either:
`disarmRoutineSessionUnattended()` (zero + incomplete + no artifact +
an intercept-log line is **not** written — a miss is not an
interception, and the log must not learn to say it was) and
`disarmRoutineSessionPartial(attendedSeconds)`.

**SLEEP exemption (decided §0.5):** routines whose loadout is SLEEP
get **no attendance row, no accrual, no outcomes, no streak**. The
receiver path skips `finalizeAttendance` for them entirely. Their
session still starts and ends (the lockdown is real), but **this spec
books nothing for SLEEP and claims nothing for SLEEP** — no attended
minutes, no missed nights, no streak. What history surfaces do with
SLEEP session rows is the Ledger question, flagged in §5.7; routines
will not pre-decide it by minting an "attended sleep" fiction.

### 5.5 Three misses — auto-pause

- Streak = consecutive MISSED outcomes for one routine, computed from
  stored entries at each finalise (PARTIAL/ATTENDED/HELD reset;
  LATE_FIRE is skipped over — it neither breaks nor extends the run of
  *his* misses; paused/disabled/off-days have no entries and cannot
  break a streak either — a streak is a run of scheduled windows).
- On the 3rd consecutive MISS, the END receiver (same pass as
  finalise): `enabled = false`, `autoPausedAtMillis = now`,
  `scheduleRoutine` (returns early — no future alarms), sled throws
  silently next composition.
- **The note, exact words.** Row line 4 becomes:
  `AUTO-PAUSED · 3 MISSES IN A ROW`
  Editor footer (while `autoPausedAtMillis > 0`) becomes the full
  sentence: `THIS ROUTINE PAUSED ITSELF AFTER 3 MISSED WINDOWS. IT
  WILL NOT FIRE UNTIL YOU SWITCH IT ON.`
  Nothing else carries it — no Deck banner, no notification, no sound.
  He finds it where the routine lives, the next time he looks at his
  schedule. Told once, where it belongs.
- Any manual enable or edit clears `autoPausedAtMillis` (§3.6), and
  **an auto-pause consumes its streak**: a forgiven streak that still
  counted would re-pause him on his next single miss — the machine
  holding a grudge. Mechanism, no fourth field: when auto-pause
  fires, the 3rd entry's outcome is written as `MISSED_AUTOPAUSED`
  (the first two stay `MISSED`). The finaliser's streak is the count
  of trailing entries in {`MISSED`, `MISSED_AUTOPAUSED`} after the
  most recent ATTENDED/PARTIAL/HELD entry, **capped by the rule that
  a run containing `MISSED_AUTOPAUSED` is already spent** — i.e. the
  streak counter treats `MISSED_AUTOPAUSED` as a terminator: entries
  at or before it never join a new run. After re-enable, his next
  MISS is streak 1. One enum value carries the whole rule.

### 5.6 Conflicts + Doze drift honesty

**HELD — MANUAL SESSION (the conflict rule):**
- Trigger: BOUNDARY_START delivers and an active session exists that
  is not this routine's (profileName mismatch): either his manual
  session or an earlier routine's session (first-come seal — routines
  never interrupt each other either).
- Who wins: **the session already running, absolutely.** The routine
  does not start a session, does not apply its loadout, does not end
  anything. (Today's receiver already refuses to steamroll — this spec
  keeps the refusal and finally *records* it.)
- No belated takeover: if the occupying session ends mid-window, the
  routine does **not** start late into its own window. The window was
  held; the rest of it is his. The next act is the END alarm.
- Row display while held: line 3 `HELD — MANUAL SESSION` (ink Bold,
  never accent, never alert colour — being held by his own session is
  not an error); routine-vs-routine: `HELD — FASTED BLOCK RUNNING`.
- Scheduler during HELD: the START is consumed; the receiver's
  existing re-schedule at the end of its pass re-arms next START +
  this window's END (scheduleRoutine sets both boundaries from now —
  the END it computes for a window containing now is that window's
  end ✓). At END: no routine session to disarm; `finalizeAttendance`
  writes outcome HELD with `attendedSeconds` = the occupying session's
  overlap with the window (clamped) — a fact about the routine's
  window, booked **only** in the attendance entry (§5.4: never summed).
- Attendance entry for a held window is created at START (outcome
  SCHEDULED), finalised HELD at END. If the app dies between, the
  entry sits SCHEDULED and the next finalise-capable pass (boot
  seed-sync sweep) closes stale SCHEDULED entries older than their
  window end as HELD if a non-routine session overlapped, else
  MISSED with `firedAtMillis` as recorded. The sweep is specified
  here so no entry can hang forever: seed-sync closes any SCHEDULED
  entry whose windowEnd < now.

**Late START (drift inside the receiver):** when START delivers,
session duration = **minutes until scheduled end**, not the full
window (today's receiver starts a full-duration session at whatever
time it wakes — a 07:50 firing of FASTED would seal until 10:50; that
is a bug this plan fixes by name: duration =
`windowEndMillis − max(now, windowStartMillis)`). If that remainder
is < 5 minutes, START does not seal at all — it books LATE_FIRE with
A = 0 and moves on (sealing a man for 3 minutes because Doze was slow
is the machine punishing him for its own nap).

**What surfaces may claim (drift honesty, binding on §1, §6, and the
Deck ribbon):** `setAndAllowWhileIdle` is inexact — under Doze a
boundary can land minutes late, worst case at the next maintenance
window. Therefore:
- Lines print the **scheduled** time as a plan: `NEXT · TODAY 05:00`.
  They never print `FIRES AT 05:00`, never count down in seconds,
  never use the word ARMED about a future firing.
- `driftMinutes` is stored on every delivered entry. If drift > 2min,
  outcomes judge against the actual remainder (LATE_FIRE rules), and
  line 4 for that entry reads, when it was the latest:
  `LAST · FIRED 22M LATE — NOT COUNTED AS A MISS` (for LATE_FIRE only;
  ordinary small drift ≤ 2min is beneath notice and prints nothing —
  honesty is not pedantry).
- END drift: a session may physically run past scheduled end until
  the END alarm lands; session elapsed is capped at target by the
  existing session math, and `attendedSeconds` caps at scheduled
  window length. No surface ever prints the overrun as his time.

**Boot inside an open window (pre-existing gap, stated not fixed):**
alarms do not survive reboot; `MonolithBootReceiver` re-arms via
`scheduleAll`, which registers the *next* START and the current
window's END — but a START already in the past never re-fires, so a
window the phone reboots inside runs no session. For study routines
the §5.2 sweep books that window as a distinct outcome —
**`NOT_DELIVERED`** (entry created at END/sweep, `firedAtMillis = 0`,
boot count changed inside the window): no session, zero minutes, and
**streak-neutral** like LATE_FIRE, because a dead phone is the
machine's failure, not his absence. Line 4 for it:
`LAST · NEVER FIRED — PHONE OFF OR REBOOTED`. (A window whose START
simply never delivered with *no* boot inside it cannot be
distinguished from deep-Doze loss at sweep time and books MISSED —
the one case where the record gives the machine the benefit of the
doubt at his expense, named here so nobody discovers it later.)
For SLEEP the exemption holds: nothing is booked, including the
failure — sleep's record is silence by design (§5.4).

### 5.7 The attendance record's minimum truthful surface (Ledger-independent)

**Flagged dependency, stated before the spec:** the Ledger's fate is
OPEN and is **not decided here**. The attendance record needs a visible
home that survives either outcome. So: the data (§5.2) is complete
enough for a Ledger to render full history if it lives; the *surface
specced here* is only what the routine row + Shield owe the truth
regardless — last outcome, streak pressure, auto-pause. If the Ledger
dies, nothing promised is lost, because nothing here ever promised
history. If it lives, it reads `routine_attendance` and must obey
§5.4's rule: **attendance minutes are a per-routine view, never an
additive total** (sessions already own the minutes; summing both is
the one-fact-two-renderings violation in arithmetic form).

The surface — **line 4 of the row** (Anonymous Pro 10sp, 0.6sp
tracking, text-secondary tier; present only when true):
- `LAST · ATTENDED 2H 50M OF 3H`
- `LAST · PARTIAL 1H 05M OF 3H`
- `LAST · MISSED — 0M BOOKED`
- `LAST · HELD — MANUAL SESSION`
- `LAST · FIRED 22M LATE — NOT COUNTED AS A MISS`
- streak 2 appends: `… · 2 MISSED IN A ROW` (streak 1 appends nothing —
  one miss is a day, not a pattern; streak 3 is unreachable on this
  line because auto-pause replaces it, §5.5).
- Auto-paused: line 4 = `AUTO-PAUSED · 3 MISSES IN A ROW` (replaces
  LAST — the pause is the current fact).
- Paused-until: line 3 carries the pause (§3.5); line 4 still shows
  LAST (the pause is line 3's fact; the record is line 4's).
- SLEEP routines: **no line 4, ever.** Never-fired routines: no line 4.
That is the entire surface. No section totals, no cross-routine
streaks, no history list on Shield. Shield's header count
(`<n> ACTIVE`) counts enabled routines only — auto-paused and paused
routines are not active and the count already excludes them via
`enabled` / pause-aware counting in the header derivation.

### 5.8 Motion / Haptics / Sound (§5 whole)

Attendance is silent. No firing sound (a 05:00 click for a machine act
would be a concept wearing a sound), no miss sound, no auto-pause
sound, no haptic at any unattended event. **Conflict resolved against
SYNTHESIS item 24's "firing is paid (sound + Deck caught winding)":**
that line was written for the Consent Beat world, where the Deck
performs readiness for his release gesture. The Consent Beat is dead.
Under NONE, firing's payment is the record (§5.2) and the running
Deck he finds when he arrives. A sound would tax the house, not the
decision. The miss streak's only voice is text, on the row, when he
comes to look.

### State machine (per scheduled window)

`SCHEDULED ──START on time──▶ RUNNING ──END──▶ ATTENDED | PARTIAL |
MISSED` · `SCHEDULED ──START, occupied──▶ HELD_PENDING ──END──▶ HELD`
· `SCHEDULED ──START late──▶ RUNNING_LATE ──END──▶ LATE_FIRE(+A
booked)` · `SCHEDULED ──never delivered──▶ (END or sweep) MISSED,
firedAt = 0` · any state ──emergency break completes──▶ finalise now
with accrued A. Entries are immutable after finalise except the
`MISSED → MISSED_AUTOPAUSED` relabel at the moment auto-pause fires.

### Code: helps / fights

- **Helps:** the receiver's don't-steamroll guard and its end-of-pass
  re-schedule already implement HELD's skeleton; `MonolithBootReceiver`
  re-arms everything on boot; the shield service's always-alive loop
  is the sampler's natural home; session profile naming
  (`"Routine: <name>"`) is a working routine-session recogniser.
- **Fights:** `RoutineAlarmReceiver` (full-duration start, no
  remainder math, no attendance calls), `disarmActiveSession`
  (wall-clock elapsed, `completed = true` from the receiver, partial-
  stamp intercept-log noise), `compute7DayHistory` (sums whatever
  elapsed says — it becomes truthful only because §5.4 rewrites
  elapsed at the source; history itself needs no change, which is
  precisely why the fix must live at disarm time, not display time).

### Law check

TRUTH, load-bearing and now literal: minutes exist only where a
person was evidenced. HOLD: attendance asks him for nothing mid-
session — evidence is harvested from acts he was already doing.
ARM: untouched. Rested-decides: auto-pause is the rested user's own
schedule defending itself against a pattern, and its note tells him
exactly how to reverse it in one tap.

---

## 6. The Section strip — the registered 24-hour elevation

Adopted from Kato's Registered Section (his letter is in my inbox; the
registration rule and the wording ceiling are his and they are right),
merged with §1's rows and §3's editor. Where Kato's file and wave-1
disagreed, this section is the resolution of record.

### Mechanism — geometry

- The routines section is **one item** in Shield's scroll column:
  header, then strip + rows as a single unit, then legend/scale foot,
  then `+ NEW ROUTINE` (§3.2). Strip and rows scroll together; there is
  no independently scrolling timeline.
- **Axis:** 00:00 top → 24:00 bottom, **22dp per hour, uniform,
  528dp total.** No compression, no broken axis, no fit-to-content.
  Foot note, letterpressed: `24H · ONE SCALE`.
- **Left column 64dp:** 28dp hour-label margin (labels every 3H —
  `00 03 06 … 21`, Anonymous Pro 9sp, letterpress tone; the current
  hour's label at full text tier) + **36dp block lane**. A 1dp vertical
  hairline (the section cut line) separates lane from row field for
  the section's full height. Hour ticks 6dp inside the lane each hour;
  3-hour hairlines extend at 50% strength across the row field.
- **Blocks:** one per routine, top = start position (05:00 → 110dp;
  09:00 → 198dp; 20:00 → 440dp), height = duration × 22dp/60. Square
  corners, flat fill by the routine's **own** loadout — STUDY one tone
  step from ground, WORK two steps, SLEEP half a step with full-
  strength outline (night: steps rise from `#171412`, capped
  `#4A423C`) — 1dp darker outline. The Shield loadout cap never
  re-tones the strip (the cap is the manual loadout; the drawing is
  the schedule; coupling them was wave-1 theatre and a lie).
- **OFF / paused / auto-paused block:** dashed outline only (6dp dash /
  4dp gap) at 50% ink, no fill — **one ghost style for every not-firing
  state**; the row's words distinguish off from paused. Unsaved draft:
  dashed outline + dotted inner line (third style, exists only for
  drafts, dies at first save).
- **Overlaps:** for the overlapping span only, the lane splits into
  two 17dp lanes + 2dp ground gap; a third simultaneous routine shares
  the second lane across a 1dp ground line. Blocks never stack, merge,
  or hide. An overlap is a real property of his schedule; the drawing
  shows it in space, with no badge, no warning, no red.
- **Overnight wrap (SLEEP 20:00–03:30):** the block runs 20:00 → lane
  bottom (88dp) and resumes 00:00 → 03:30 at the top (77dp), same fill,
  each cut end carrying a **continuation serif** — a 6dp horizontal
  tick crossing the segment's full width at the edge, 1dp ink. One
  routine, one row (anchored at 20:00, start-day semantics), end
  printed `03:30 +1D` in meta-adjacent surfaces. The top segment is
  never a separate object.
- **Registration + disclosed displacement:** a row's top edge anchors
  at its block's start. If a start falls < 76dp after the previous
  row's start (collision), the row pushes to previous-bottom + 8dp
  and the displacement is **drawn**: an elbow leader (1dp ink at 60%,
  square bends) from the block's top-right corner, right 8dp, down the
  lane edge, into the row's top edge; a 4dp open circle marks the true
  start point. With his seeds, gaps (88dp, 242dp) exceed pitch — every
  row sits exactly on its start and no leader shows. The drawing
  admits displacement; it never absorbs it.
- **Gaps:** between ON blocks, void = ground + ticks. A gap ≥ 60min
  gets one label in the row-field void at its vertical centre:
  `1H 30M OPEN` (Anonymous Pro 9sp, text-secondary, flanked by 12dp
  hairlines). His day: 03:30–05:00 `1H 30M OPEN`, 08:00–09:00
  `1H 00M OPEN`, 19:30–20:00 unlabelled (30m, below threshold).
  Labels are derived arithmetic on enabled+unpaused routines,
  recomputed on any edit; not tappable, not editable; they hide while
  any editor is open. They say OPEN, never EMPTY, and never claim what
  the gap is for.
- **Now-line:** 1dp **ink** (the wave-1 orange now-line is dead —
  orange is COMMIT fill on Deck/Tasks only, zero on Shield), crossing
  the lane at now + 8dp into the row field, updated from §1's shared
  ticker. The row whose window contains now gains a 2dp ink spine on
  its block's left edge; its line 3 carries the `WINDOW OPEN — ENDS`
  sentence (§1). No other temporal decoration: no elapsed shading of
  the axis (Kato's too-good-to-lose stays lost in this plan — one
  temporal gesture per drawing).
- **Legend:** at the foot, on the scale note's baseline: three 8×8dp
  swatches + `STUDY / WORK / SLEEP` 9sp. One legend, one home.
- **Printed-scale discipline (Nadia's handover, adopted):** (1) the
  axis belongs to the strip, not the data — hour numerals print true
  values at drawn positions; displacement moves rows and leaders,
  never the scale. (2) Collision law for labels: if two adjacent
  boundary labels would come closer than 18dp, drop the *interior*
  one, keep labels nearer the head, always keep `00` and the end —
  dormant at the current 3-hour spacing (66dp apart) and binding the
  day anyone densifies the labelling. (3) The axis persists empty:
  with no routines in a span — or none at all — ticks, numerals, and
  end caps still print at full rhythm; the section never changes
  height or beat between empty and populated.

### The one interaction

**Tap a block → its row expands** (and scrolls into view). Both
segments of a wrapped block hit-test to the same row. Implementation:
the block tap calls the same header-tap handler as the row (expand +
`playFirmClick()` from the row), plus a `BringIntoViewRequester` on
the row for when the block is visible but its row is displaced below
the fold. Long-press: nothing. Tap a gap, a label, a leader: nothing.
That is the strip's entire interactivity — it is a drawing that can
open its own rows, not a second control panel. (Kato's press-and-hold
courtyard-create is **not** adopted: creation has one front door,
§3.2; a second, hidden door would be discovered by accident and
trusted by neither of us.)

**Resolved conflict — the time well:** wave-1 put a recessed time well
on the collapsed row. In the registered Section, the block's position
*is* the start and its height *is* the duration; a well would print
both a second time. One fact, one rendering: **the well moves into
the editor** (Band A/B displays, §3.0), where it stops being a
duplicate and becomes the instrument he edits with. The collapsed row
keeps name/meta/next/attendance + sled. Kato has mail on this.

### Motion

- Editor open/close inside the Section: rows below the editor push
  down; **blocks never move**; their leaders lengthen by exactly the
  editor's height, redrawn each frame from §1's single progress float.
- Toggle ON: block fill wipes upward from its base, 260ms CurveC-family
  (`CubicBezier(0.3, 0, 0.8, 0.15)` — the drawer's reveal curve; wrapped
  routines wipe both segments simultaneously, the top segment from the
  lane top downward — same wall, continuing). Toggle OFF: fill wipes
  downward 220ms (specA family, gravity) with the dashed outline
  present from frame one.
- Loadout change in an editor: its block re-tones by 180ms crossfade
  (CurveA) landing with the editor's firm click. Cause and effect in
  the same gesture, nowhere else. Now-line: no animation — it cuts on
  the tick, like a clock hand stepping.
- Scroll: nothing. No parallax, no haptics, no sound.

### Haptics

- Toggle ON (build): on wipe completion, waveform [14ms @ 0.8, +50ms
  gap, 8ms @ 0.5] — footing, then course (amplitude-array family,
  carrier pattern as specced for sled seats). Toggle OFF (demolish):
  three ticks 4ms @ 0.35 spaced 18ms. These ride *with* the sled's
  landing feel (§2) — the block wipe and the sled settle are one
  gesture with one owner (the view), and the waveform attaches to the
  same arrival frame. No second call site, no VM waveform.
- Block tap → expand: §1's firm click only. Everything else: silence.

### Sound

The strip itself is silent by design. Only its hosted controls speak,
through their existing owners (§3.6 table's discipline).

### Depth & light

Tone steps + hairlines only; zero shadows, zero gradients, square
corners in the lane. Blocks sit **on** the ground, not in cards; the
lane has no container fill. The single recess in the whole section is
the editor panel floor (§3.0). Text tiers at night ≈ 11 / 5.8 / 3.5
against `#171412` (labels at the 3.5 decorative tier are 9sp *printed*
labels — never state; every state-bearing string rides 5.8 or above).

### State machine

Strip state per routine mirrors §1's row states 1:1 (same source
fields): SOLID (enabled, unpaused) · GHOST-DASHED (off / paused /
auto-paused / pending-delete) · DRAFT (dashed + dotted, unsaved) ·
SPLIT-LANE (any of the above, during an overlap span) · WRAPPED
(segments + serifs, orthogonal). The now-line and WINDOW-OPEN spine
are section-level, derived from the shared ticker — no per-block
state can desync from its row because both render the same fields in
the same composition pass.

### Edge cases

- **A routine crossing 03:30** (e.g. a future 02:00–04:00 test-eve
  block): wraps across the *top/bottom cut* exactly like SLEEP — the
  serif convention is general, not sleep-shaped. The rotated axis
  (origin at wake) stays rejected: it buries midnight, moves the wrap
  onto his wake, and re-bases the day his wake time changes.
- **24H of routines** (no gaps): no gap labels; lane splits carry the
  whole drawing; rows displace with leaders throughout. Legal, ugly,
  honest — his schedule said so.
- **Zero routines** (all deleted): the strip is an empty axis with
  ticks + scale note + `+ NEW ROUTINE`. An empty drawing, not an
  error state.
- **Editor open + toggle another row's sled:** allowed; accordion is
  about editors, not about locking the section's controls.
- **Performance:** the lane is one Canvas-free composition of Boxes
  (blocks are positioned Boxes in a 528dp Box) — no draw-loop cost;
  leaders are 3-segment Box compositions, not paths. If a future build
  moves the lane to Canvas, the geometry above is the contract it
  must reproduce exactly.

### Code: helps / fights

- **Helps:** all block geometry derives from the same three entity
  fields the DAO already returns `ORDER BY startMinutes ASC` — the
  sort the registration wants is the sort the query does.
- **Fights:** Shield's current routines item is a compact column with
  `spacedBy(16.dp)` — replaced as one unit (header → section Box of
  fixed 528dp + rows overlaid by anchor offsets → foot). The section
  being one `LazyColumn` item keeps §1's height animations safe.

### Law check

TRUTH: one scale, never broken; displacement drawn, never absorbed;
wrap drawn as one routine; gaps measured, never interpreted; window
never rendered as session. BEAUTY-IS-RETENTION: this strip is the
prettiest object on Shield *because* it is the schedule itself drawn
true — the masonry of his day, sleep holding both ends.

---

## 7. Theme follows schedule

### Mechanism — the rule, exact

The app's colour scheme is decided by this precedence, evaluated
wherever `effectiveNight` is computed today (`MainActivity.kt:221`,
`Theme.kt:103` — `darkTheme || isFocusSessionActive`):

1. **SESSION ACTIVE** — any `FocusSessionEntity.isActive` → **NIGHT.**
   (Existing behaviour, kept. A sealed phone lives in the night scheme
   whatever the clock says.)
2. **SLEEP WINDOW** — else, if any routine with `loadoutName == SLEEP`,
   `enabled == true`, `pausedUntilMillis <= now`, and
   `RoutineSchedule.isActiveNow(routine, now)` → **NIGHT.**
3. **Else → DAY.**

That is the whole rule. Theme follows the *schedule* — the rested
user's written day — evaluated from the clock and the DB, **not** from
alarm delivery (Doze drift, §5.6, can delay a firing; it must not
delay the light — the scheme claims "his day says night", a plan
fact, and plan facts are punctual).

**The manual override does not survive.** `MonolithViewModel._darkTheme`
+ `toggleDarkTheme` (`MonolithViewModel.kt:81, 370`) are deleted; the
DAY/NIGHT pill in the status drawer (`MonolithCustomComponents.kt:302`)
is deleted with them — Otis has the kill order by mail this wave. No
replacement setting is offered anywhere (not in Guard, not in a
drawer): a theme override is a tired-user decision surface bolted
onto a rested-user fact, and SLEEP-pause (§3.5) already gives him the
honest lever — pause the routine and the app stays in day, because
his schedule says so.

### Evaluation + transition

- A single app-level `nowMillis` StateFlow in the ViewModel, ticking
  every 30s while the app is foregrounded + refreshed on resume
  (the same cadence family as §1's ticker; Shield's section ticker
  may read this flow instead of owning its own — one clock per app
  is the tidy end-state, but sections composing their own 30s ticker
  against the same wall clock cannot disagree by more than a frame's
  ordering, so this is guidance, not a gate).
- **Transition moment:** the first evaluation at/after a boundary
  (20:00 entering SLEEP → night; 03:30 leaving → day) — within one
  tick (≤30s foregrounded; on next foreground if backgrounded across
  the boundary; theme is a UI fact and background correctness is
  meaningless).
- **Transition duration: 0ms — a cut.** `MyApplicationTheme` swaps
  schemes discretely today; this spec keeps the cut and forbids a
  crossfade: a 300–500ms lerp passes through intermediate colour
  schemes that are neither day nor night — false colours, printed
  proudly. A light switch does not fade. Surfaces that already
  animate token colours (sleds, caps) ride their own 260ms incidentally;
  that is their motion, not a theme transition.
- **Sound/haptics:** none. Theme is in the silence map's dead class
  (navigation, theme, deletions, settings) — and a 20:00 click from an
  unattended phone would be the machine announcing itself at the hour
  he goes to sleep.

### Depth & light

Both schemes are already fully specced elsewhere (day tokens; the
locked night staircase — ground `#171412`, steps ≥1.35:1, text
11/5.8/3.5, fills cap `#4A423C`, true-black AOD ground). This section
changes *when* each scheme holds, never what either contains. The
AOD desk clock keeps its own true-black ground under both.

### State machine

`DAY ──(SLEEP window opens | session starts)──▶ NIGHT
 ──(window closes AND no session)──▶ DAY`. Two states, three inputs
(session, sleep-window, precedence), no third "manual" state can
exist. Session ending *inside* the sleep window: stays NIGHT (rule 2
catches what rule 1 releases — no day-flash at 23:00 when a session
completes). SLEEP window closing *during* a session: stays NIGHT
(rule 1). Both orderings are flicker-free by construction of the
precedence, not by debouncing.

### Edge cases

- **SLEEP routine paused/deleted/off:** rule 2 never fires — the app
  stays in day at 21:00. That is not a bug in this spec; it is the
  rule working: his schedule, as currently written, has no night.
  The §3.0 footer suffix (`THEME FOLLOWS THIS WINDOW`) makes the
  coupling visible *before* he pauses, in the editor, in daylight.
- **Multiple SLEEP routines:** any one active window suffices (OR).
- **A STUDY routine named "Sleep":** loadout decides, never the name.
- **He is awake at 02:00 using the app:** night scheme, low luminance,
  correct — the scheme serves his eyes' schedule, not his virtue.
- **Saturday tests:** irrelevant to theme — SLEEP is DAILY; a test
  day changes his blocks, not his night. If he ever splits SLEEP by
  days, rule 2 follows `daysMask` automatically via `isActiveNow`.

### Code: helps / fights

- **Helps:** `MyApplicationTheme(darkTheme, isFocusSessionActive)`
  already centralises the decision (`Theme.kt:97–106`); every screen
  already consumes `effectiveNight` — the change is concentrated in
  what feeds it (`MainActivity.kt:95/144/221` + VM state), not spread
  across screens. `isActiveNow` is the exact predicate rule 2 needs.
- **Fights:** `_darkTheme` is user-visible state today (the pill
  toggles it, canvas views read the same flag) — deleting it touches
  MainActivity's five `isDarkTheme = effectiveNight` pass-throughs
  only in that `darkTheme` stops being an input to the OR. The canvas
  views' `isNightMode` plumbing is untouched.

### Law check

TRUTH: the scheme now *means* something — night is a claim about his
schedule or his session, and both are checkable facts. A manual pill
made night mean "he once tapped something", the weakest fact in the
app. Rested-decides: he authors the night every time he edits SLEEP;
the tired user at 21:00 just executes it — which is the workshop law
this whole file exists to serve.

---

## Appendix A — All strings specced in this file (verbatim, for the builder)

`NEXT · TODAY 05:00` · `WINDOW OPEN — ENDS 08:00` · `HELD — MANUAL
SESSION` · `PAUSED UNTIL FRI 30 OCT` · `WILL NOT RUN — NO DAYS SET` ·
`LAST · ATTENDED 2H 50M OF 3H` · `LAST · PARTIAL 1H 05M OF 3H` ·
`LAST · MISSED — 0M BOOKED` · `LAST · HELD — MANUAL SESSION` ·
`LAST · FIRED 22M LATE — NOT COUNTED AS A MISS` ·
`LAST · NEVER FIRED — PHONE OFF OR REBOOTED` · `AUTO-PAUSED ·
3 MISSES IN A ROW` · `THIS ROUTINE PAUSED ITSELF AFTER 3 MISSED
WINDOWS. IT WILL NOT FIRE UNTIL YOU SWITCH IT ON.` ·
`NAME KEPT — EMPTY NAMES DON'T SAVE` · `RENAME AFTER THIS WINDOW
ENDS — 08:00` · `NOT SAVED — SET THE WINDOW` ·
`ROUTINE DELETED — <NAME> · UNDO` · `GOES AT 08:00` ·
`THIS ROUTINE ENDS WITH ITS WINDOW AT 08:00 — THEN IT'S GONE.` ·
`THE OPEN WINDOW STILL RUNS TO 08:00.` ·
`LONGER THAN 28 DAYS — SWITCH IT OFF INSTEAD.` ·
`24H · ONE SCALE` · `1H 30M OPEN` ·
`SCHEDULE RESET TO YOUR DAY — TAP ANY ROW TO CHANGE IT` ·
`THEME FOLLOWS THIS WINDOW` (footer suffix form: `· THEME FOLLOWS
THIS WINDOW`)

## Appendix B — Conflicts resolved in this file

1. **LIVE accent node (wave-1 §1.1) vs Shield accent budget ZERO:**
   exception KILLED (reasoning in §1/§6: orange is a
   COMMIT fill; a window opening is not a commit; and after CRUD,
   overlapping windows make "only one routine can be live" false as a
   premise — the exception's load-bearing fact dies with the front
   door). Node/window state now renders in ink silhouette + 2dp spine.
2. **LIVE wording vs Kato's ceiling:** Kato wins. `WINDOW OPEN`
   everywhere; Deck owns session language. (SYNTHESIS 21's "NEXT/LIVE
   line" survives as the NEXT line + window line.)
3. **Time well (wave-1) vs registration (Kato):** well moves into the
   editor (§6).
4. **`adjustRoutineTime` 720 clamp vs his 10H30M DAY BLOCKS:** clamp
   rises to 1080 (§3.1).
5. **Receiver END name-match vs rename/migration:** rename fence
   (§3.3) + migration postponement (§4); id-based session matching
   named as the long-term fix, owned by the sessions seat.
6. **Firing-is-paid (SYNTHESIS 24) vs NONE:** sound dies; payment is
   the record (§5.8).
7. **Delete confirmation vs undo:** undo wins (§3.4) — dialogs tax
   the rested surface to protect against a mistake the 5-second
   window already covers.
8. **Attendance totals vs session totals:** sessions own minutes;
   attendance is a per-routine view, never summed (§5.4/§5.7) —
   **flagged to the Ledger seats as a binding constraint if the
   Ledger lives.** Ledger fate itself: NOT decided here, by order.
9. **Sled feel ownership (Yuki's contract, received mid-wave) vs
   wave-1's VM-owned table:** the sled goes view-owned — one landing
   feel at arrival, `toggleRoutine`'s latch call deleted (§2). The
   editor's other actions keep the wave-1 VM-owned table (one call
   site each; no gesture has two owners under either regime).
   Adopted from the same contract: overshoot settle, arrival word
   flip, 1:1 drag with grab offset + scroll surrender, silent
   programmatic settles. One deviation kept and mailed: tap toggles
   (binary control inside a tappable row) instead of side-tap-sets.
10. **Presence-grace minting (Reihe) vs seal-at-boundary:** Reihe's
    10-minute presence grace is REJECTED for session *start* —
    enforcement begins at the boundary he set (NONE concept); a
    grace window is a daily unguarded gap. His underlying truth is
    fully adopted for the *record*: minutes count only from
    evidenced presence (§5.3–5.4). Enforcement at the boundary,
    honesty in the book — the two truths are different facts and
    each gets its own mechanism. His schema counters
    (`consecutiveMisses`, `lastOutcome` on the entity) are also
    rejected: streak and last outcome are derived from
    `routine_attendance`, one source, no denormalised drift.
11. **Ribbon tie-break (Ines):** her earlier-end-then-lower-id wins
    over the later-end rule in my first letter — among simultaneous
    starts, the window that releases him first is the operative
    near-term fact, and her spec was already built on it. Corrected
    by mail, same thread.
12. **Nadia's printed scale:** adopted into §6 (axis immovable,
    18dp label-collision law, axis persists empty).

## Appendix C — Build order (each step compiles + renders alone)

1. DB v8: entity fields + attendance table + explicit Migration(7,8)
   + reseed flags. Upgrade test with edited routines.
2. Reseed migration + fresh-install seeds (§4) behind the flag.
3. Receiver truth pass: remainder-duration START, HELD entry,
   finalizeAttendance + the two disarm variants, auto-pause (§5) —
   **before any paint**: the ghost dies first.
4. Pause-aware scheduler + `effectiveNextStartMillis` (§3.6).
5. CRUD VM/DAO surface + Band E + draft state + delete-undo (§3).
6. Row rebuild: lines, ticker, sled (§1–2).
7. Registered Section (§6) around the rebuilt rows.
8. Theme rule (§7) + pill deletion (Otis's surface, this rule).

— Sten, Maximum-Implementable · routines seat · plan wave
