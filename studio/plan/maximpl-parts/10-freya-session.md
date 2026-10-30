# 10 — SESSION SURFACES — Freya, Maximum-Implementable (coordinator: Voss)

**Seat:** desk clock + running state. Four elements: (1) the Always-On Desk Clock,
(2) the escapement-lite running state on the Deck, (3) the Session Cloister,
(4) the session-end receipt. Plan only — no code was written or changed.

**Standards binding this file:** ARM ≤10s · HOLD absolute (these surfaces ARE the
hold — zero decisions may exist on them) · TELL THE TRUTH once, in human language.
Workshop laws: one fact one rendering · beauty is retention · rested decides,
tired executes. Never green. Night staircase. AOD ground true `#000000`.

---

## 0. Cuts, deltas, and the one truth problem everything else hangs on

### 0.1 First Light is REMOVED — by name

SYNTHESIS item 19 ("first arm pulls the dawn ramp up the Deck — AOD reads elapsed
as risen light") is **cut, both halves**. The Deck half is not my surface; the AOD
half is, and it dies here: the desk clock has **no dawn ramp, no risen-light
elapsed rendering, no brightness that grows with the session, no ceremony of any
kind**. Ground is `#000000` from the first second to the last; hero alpha is a
constant 88% all night; elapsed is rendered as numerals and one seam marker —
never as light. Wave-1's own language ("the plate breathes", "countdown
organism") is metaphor in a document, not UI, and none of it ships: the drift in
§1 is burn-in physics, it carries no meaning and must never be described to the
user as breathing, rising, or dawn. SYNTHESIS item 20's "escapement heartbeat"
is likewise renamed in §2: it is a **stepper**. There is no heartbeat — no sound,
no pulse — because a heartbeat is a narrative and a demand. What survives of
item 20 is the mechanism only: stepped arc + Session Cloister + both numbers
always.

### 0.2 Round 0 already shipped — deltas only, not re-spec

Verified in current code (`AirlockAndDisarmOverlays.kt:66–167` as it stands now,
not as wave-1 found it):

| Shipped (do not re-spec) | Still missing (this file specs the delta) |
|---|---|
| `FLAG_KEEP_SCREEN_ON` set/cleared in a `DisposableEffect(visible)` with the Activity-window walk | Brightness capture/set/restore inside that same effect — `screenBrightness` has **zero hits app-wide** (§1.7) |
| Ground is already `Color.Black` (wave-1's `#161311` complaint is stale) | Everything visual: composition, hero format, seam, drift maths (still the ±3 **pixel** raster at lines 99–100), permanent header, task banner card, 16dp pill bar with the 0.04 floor |
| Entry/exit fades (`specB` in / `specA` out) | The 4-second ghost exit hint (§1.6) |
| `toggleAlwaysOnDeskClock` wiring, `testTag` | Silence on open — the ViewModel still plays `playMechanicalLatch` on toggle (§1.9, fights) |

Fonts also moved since wave-1: real weight files now exist (Anonymous Pro ships
400/700 only). But `DeskClockTextStyle` (`Type.kt:79–87`) **still** points at
Sora 104sp and the overlay still hand-rolls its own style through the
`JetBrainsMonoFamily` alias — which happens to resolve to Anonymous Pro, so the
face is accidentally right and the token is still lying. §1.3 fixes the token.

### 0.3 THE truth problem: the entity counts BLOCKS, the surfaces print SESSIONS

`FocusSessionEntity.remainingSeconds` is **per-block**, not per-session.
`MonolithRepository.startSession` sets `targetDurationSeconds = dialMinutes × 60`
(one block), and `updateSessionProgress` **resets `elapsedSeconds` to 0 at every
block rollover**, inserting a 600s safe break (`SAFE_BREAK_DURATION_SECONDS`)
between blocks. Consequences in shipped code:

- The Deck's running `FINISHES h:mm a` is computed from block-remaining
  (`TactileDeckScreen.kt`, `finishPair`) — for a 4-block session it prints the
  **block's** end labelled as the session's finish. A lie, live today.
- A desk-clock seam drawn from `elapsedSeconds / targetDurationSeconds` would
  drain back to empty at every block boundary.
- `remainingSeconds` also ignores `penaltySecondsAdded`: after a tamper penalty
  the raw getter hits 0 up to 15 minutes before the engine actually completes
  the session (`updateSessionProgress` completes against
  `targetDurationSeconds + penaltySecondsAdded`). Any surface printing raw
  `remainingSeconds` in that window prints `00:00` during a live session.

**Ruling (applies to every element below):** surfaces never read
`remainingSeconds` raw. One shared formatter/derivation, used by Deck and desk
clock identically:

```
effectiveTarget  = targetDurationSeconds + penaltySecondsAdded
blockRemaining   = (effectiveTarget - elapsedSeconds).coerceAtLeast(0)
sessionRemaining = blockRemaining
                 + (totalStudyBlocks - currentBlockIndex) × (effectiveTarget + 600)   // later blocks + their breaks
                 + (if isInSafeBreak then safeBreakSecondsRemaining else 0)            // current break, when in one
sessionEndWall   = startWallTimeMillis + totalStudySeconds + (totalStudyBlocks - 1) × 600 × 1000
                   — for the CURRENT block chain only when no break/penalty has shifted the wall;
                   the robust form is: now + sessionRemaining×1000, formatted at render time.
sessionElapsedStudy = (currentBlockIndex - 1) × effectiveTarget + elapsedSeconds   // breaks excluded — breaks are not study
```

Cleanest implementation: fix the derivation once in `MonolithModels.kt` beside
`remainingSeconds` (add `effectiveTargetSeconds`, make `remainingSeconds`
penalty-aware) and add the session-level derivations as computed properties on
the entity. Every field needed already exists; §5 proves it field by field.
Stopwatch mode bypasses all of this: `targetDurationSeconds` is the 1200s floor,
`elapsedSeconds` counts up unbounded, and there is no session end to print.

**Hero consequence:** on both surfaces the hero is **block-remaining** (that is
the question at 3 AM — "when does this block end / when do I get the break"),
and the **session** end is the printed finish time. For the overwhelmingly
common single-block session the two coincide and nothing in the composition
changes. Wave-1 specced the hero as session-remaining without noticing the
reset; this file corrects it and says so.

---

## 1. THE DESK CLOCK (`AlwaysOnDeskClockOverlay`)

One rigid plate on true black. Calm enough to ignore for hours, exact enough to
trust in one glance across a room. It is the HOLD made visible: nothing on it
can be pressed, chosen, or changed — its only interaction is leaving it.

### 1.1 Mechanism — the Punkt composition, five lines, nothing else

The whole clock is one `Column` (the plate) that drifts as a single rigid object
(§1.5). Internal layout never changes; only the plate's position does.

```
        ●  CELL CYCLE — MITOSIS                          ← line 1: live dot + intention (whisper)
   ┌───────────────────────────────────────────────┐
   │  1:42        ELAPSED 47:12                     │  ← line 2: HERO + micro-block
   │              ENDS 5:42 AM                      │
   └───────────────────────────────────────────────┘
   ────────────────────────╂━━━━━━━━━━━━━━━━━        ← line 3: seam hairline (§1.4)
        BLOCK 2 OF 4                  THU 29 OCT · 3:47 AM   ← line 4: footnote row
                     TAP TO EXIT  (ghost, first 4s only, §1.6)
```

- **Plate width** = hero-row width, not screen width. Plate centred horizontally,
  at optical centre with a −24dp vertical bias (hairline + footnote weight below
  the hero pulls visual mass down).
- **Margins:** ≥32dp from screen edges; the drift field (§1.5) lives inside them.
- **Line 1 — whisper header.** The screen's entire accent budget: one **6dp
  circle, `NightColorPrimaryContainer` `#F47442` at 100%**, meaning exactly one
  thing — *a session is live*. No session → no dot (preview mode, §1.8). Nothing
  else on this surface is ever accent, ever. Beside it, the intention:
  `activeTask?.title ?: activeSession.intention`, uppercase, single line,
  ellipsis — **Atkinson Hyperlegible Regular 400, 12sp, letterSpacing 1.2sp,
  `NightColorPrimary` at 55% alpha**. (Wave-1 said Atkinson SemiBold; Atkinson
  ships only 400/700 — SemiBold would be synthesized smear, the exact fraud
  Round 0 killed. Regular at 55% is the whisper; the line is read at arm's
  length, never across the room, so it does not need Bold's shout.) Printed,
  never boxed — the current filled banner card (`NightColorSurfaceBright`, 14sp
  bold) is deleted.
- **Line 2 — hero + micro-block.** `Row`, hero left, micro-block right,
  top-aligned, 16dp gap, micro-block padded 20dp down from the hero's top — the
  two micro-lines stand *beside* the hero at its upper third, never centred
  against it. 14dp gap between line 1 and line 2.
- **Line 3 — the seam** (§1.4), 24dp below the hero baseline.
- **Line 4 — footnote row**, 16dp below the seam, `SpaceBetween` across plate
  width, Anonymous Pro 400, 10.5sp, letterSpacing 1sp, `NightColorPrimary` at
  42% alpha. Left: session state as a word (§1.8 per mode). Right: **wall-clock
  time** (§1.2).

### 1.2 Time of day — YES, the face shows it, demoted to the footnote

Wave-1's audit called its absence a forgotten job; wave-1's spec restored it in
the footnote. **This file confirms that and defends it under TRUTH:** at 3 AM
the two questions are "how long is left" (hero) and "what time is it" — and the
second cannot be derived from the first without arithmetic a tired man should
never be asked to do (`ENDS − remaining = now` is a computation, not a glance).
Wall time is a **different fact** from ENDS and from REMAINING, so one-fact-one-
rendering is not violated: three facts, three homes — REMAINING in the hero,
ENDS in the micro-block, NOW in the footnote at 42% alpha where it can be found
but never competes. Format: `THU 29 OCT · 3:47 AM`
(`SimpleDateFormat("EEE d MMM")` + `("h:mm a")`, uppercased — the Deck already
formats finish times with this idiom; no new dependency). It is formatted from
`System.currentTimeMillis()` at whatever recomposition the minute cadence
already triggers (§1.7), so it is minute-accurate by construction and flips its
date at midnight with no extra machinery. It never appears anywhere else on
the plate, and it is never the hero: this face is *for* the session; the wall
clock is a guest in the footnote.

### 1.3 The hero — REMAINING, precision by proximity, honest formats

**Hero = block-remaining** (§0.3), consistent with the Deck's running readout —
two surfaces with opposite heroes would make the app argue with itself.
Stopwatch inverts cleanly: hero = elapsed (there is no remaining; the floor is
the contract and lives in the footnote).

**The switch rule (exact):** let `R = blockRemaining` in seconds.

| Band | Hero rendering | Hero cadence | Micro-block |
|---|---|---|---|
| `R ≥ 600` (cruise) | minutes only: `1:42` (H:MM) if ≥60 min remain, else `42` with a 40%-alpha `MIN LEFT` micro-label under the hero's left edge | once per minute, on the minute boundary of R | `ELAPSED 47:12` / `ENDS 5:42 AM` |
| `60 ≤ R < 600` (finale) | `08:41` MM:SS, zero-padded | **once per second** | `ELAPSED 111:43` / `ENDS 5:42 AM` |
| `R < 60` (last minute) | `00:41` MM:SS — the `00:` is printed, never dropped; dropping it would change glyph count and plate width in the final minute | once per second | same |
| Stopwatch | elapsed: `47:12`, past 60 min `1:12:05`→ rendered `1:12` H:MM in cruise… see below | once per minute, forever (no finale exists) | `STARTED 3:02 AM` |
| Safe break | break-remaining, §1.8 | per its own rule | per §1.8 |

Stopwatch hero, stated without a table trick: elapsed minutes-only in the same
format family (`47` under the floor is impossible — the floor is 20 min, but
the format rule is uniform: `<60` min → bare minutes + `MIN IN` label is wrong
for elapsed, so stopwatch under 60 min renders `47:12` **without seconds
updating** — i.e. `MM:00`-style stale seconds are a lie, therefore stopwatch
renders bare elapsed **minutes** `47` with micro-label `MIN ELAPSED`, and
`1:12` H:MM past 60. One rule covers both modes: **a surface never displays a
unit it is not updating.** That sentence is the whole precision law; the finale
switch at exactly `R = 600` is its only trigger, it is evaluated from the same
derived `blockRemaining` the hero prints, and it cannot flap (R is monotonic
within a block; at a block rollover R resets above 600 and the clock returns to
cruise — correct, because a fresh block is not a finale).

**Formats, no exceptions:** `179:59` is banned everywhere in this app — total
minutes are never printed as a minute count above 59. ≥60 minutes is always
H:MM (`1:42`, `2:59`). Elapsed in the micro-block follows the same law
(`111:43` is banned; it renders `1:51:43`→ no — micro-block elapsed renders
`ELAPSED 1:51` H:MM in cruise and `ELAPSED 1:51:43` is too wide for the micro
column, so: micro elapsed is H:MM or MM:SS-under-an-hour, **minutes precision**,
always — the micro-block is a whisper, and minute precision is the truth at
whisper scale; the finale's per-second precision lives only in the hero where
the eye already is). Correcting wave-1 here: its corrected table still printed
`ELAPSED 111:43`. That glyph string is dead.

**Type:** Anonymous Pro **Bold 700** (the real file; the clock's face — locked).
Mono is non-negotiable: fixed advance width means `1:11 → 1:12` never changes
plate width, so the plate never breathes sideways.

- Cruise hero (≤4 glyphs): **132sp**, lineHeight 128sp, letterSpacing −2sp.
- Finale hero (5 glyphs): **112sp**, lineHeight 108sp, letterSpacing −2sp.
  Two pre-built `TextStyle`s chosen by band — no runtime measuring, no
  `BoxWithConstraints` font maths. Screens narrower than 340dp: drop one step
  (124/104sp).
- Hero ink: `NightColorPrimary` `#FFE4CF` at **88% alpha** on pure black. Full
  cream at 132sp in a dark room is a lamp; 88% is still unmistakably the
  brightest thing on the plate.
- Colon at 45% alpha via one `AnnotatedString` span — the eye reads two digit
  groups, the punctuation whispers.
- Micro-block: Anonymous Pro 400, 13sp, lineHeight 20sp, letterSpacing 0.5sp;
  labels (`ELAPSED`, `ENDS`) at 45% alpha, values at 80%, same line, one space.
- Fix the token: `DeskClockTextStyle` becomes Anonymous Pro Bold 132sp;
  a `DeskClockFinaleTextStyle` (112sp) sits beside it; the overlay consumes
  both and the hand-rolled style in the overlay is deleted.

### 1.4 Progress — the seam hairline, growing, no floor, no ring

A **2dp hairline across the plate width** that fills with **study-elapsed**
fraction (§0.3: `sessionElapsedStudy / (effectiveTarget × totalStudyBlocks)`),
growing left→right. Accumulation, not drainage: time banked, not time leaking.

- Track: plate width × 2dp, `NightColorPrimary` at 14% alpha — a printed line.
- Fill: same ink at 85% alpha, 2dp. **No floor.** The shipped
  `coerceIn(0.04f, 1f)` is deleted: at 0% the line is honestly empty. A floor is
  a progress bar lying about work not yet done.
- **Head marker:** at the fill's leading edge, one vertical bar 2dp wide × 11dp
  tall at 100% cream. It exists at 0% too — at x=0 it is the printed origin,
  like a fader cap at rest; a position marker is not a floor, because it claims
  a *place*, not an *amount*.
- **Block ticks:** when `totalStudyBlocks > 1`, at each `k/totalStudyBlocks`
  fraction a tick 1dp wide × 7dp tall centred on the line, drawn in ground black
  (a notch cut through the fill — legible filled or not) with a 14%-alpha cream
  twin while unfilled. The session's scale is printed on the progress itself.
- One `Canvas` draw pass (~35 lines). No `%` text exists anywhere on the plate:
  the marker's position *is* the percentage.
- **No ring.** A ring repeats the Deck dial's geometry, is the largest possible
  lit perimeter on an OLED, and is the single worst static burn-in shape. The
  shipped 16dp rounded pill (remaining-fraction, shrinking, floored, accent-
  spending) is deleted with the banner card and the permanent header.
- Stopwatch: the seam becomes the **floor gauge** — fill =
  `elapsedSeconds / 1200`, one tick at 100% (the floor itself); past the floor
  the fill holds at full and the footnote word flips (§1.8). Nothing recolours.

### 1.5 Burn-in — golden-angle figure-8, re-derived and confirmed

Physics, not aesthetics: this face is lit for hours on OLED. The shipped maths
(`((step % 5) − 2) * 3` fed to `IntOffset`, i.e. **pixels**) travels ±6px ≈ ±2dp
while a 96sp digit stroke is several dp wide — strokes sit on nearly the same
pixels forever. Theatre. Replaced wholesale:

For minute-step `n` (the ViewModel already increments `_burnInFigure8MinuteStep`
once per minute, `% 360` — cadence and flow kept untouched):

```
θ  = n × 137.508°            (golden angle — successive positions never repeat a
                              neighbourhood; the plate tours the whole field evenly)
dx = 26dp × sin(θ)
dy = 17dp × sin(2θ)          (sin 2θ against sin θ traces the Lissajous figure-8
                              the state flow was always named for)
```

**Re-derivation against digit geometry (confirming wave-1's numbers):** at
132sp Anonymous Pro Bold, stem thickness measures ≈0.13em ≈ **17dp**, digit
advance ≈0.6em ≈ 79dp. To protect a stroke, travel must exceed its thickness —
a shift smaller than the stroke re-lights the same columns. The horizontal
field is 52dp ≈ **3.1× stem thickness** and the vertical field 34dp ≈ **2× stem
thickness** (vertical stems are long, but the at-risk horizontal strokes —
top/mid/bottom bars — are ~17dp thick, and 34dp clears them twice over). Over
one hour the golden angle spreads the plate's lit pixels across roughly an
order of magnitude more area than the static layout. A larger field was
rejected: plate margins are 32dp, so ±26dp already leaves only 6dp worst-axis
clearance on the tightest screens; ±30dp+ would push the footnote into the
screen edge. **±26 × ±17dp stands.** On <340dp-wide screens the field scales
×0.75 (19.5 × 12.75dp — still >1× stem thickness, still real).

**What drifts:** the whole rigid plate — hero, micro-block, seam, footnote,
dot — via one `Modifier.offset` on the outer `Column`. Internal geometry is
frozen, so relative burn-in *between* elements is impossible.
**What stays fixed, and why:** (a) the black ground — unlit pixels cannot burn;
(b) the exit ghost (§1.6) — it is anchored to the screen, not the plate,
because it describes the screen ("tap anywhere"), and it exists for 4 seconds,
which cannot burn anything; (c) window state (keep-on flag, brightness) — not
visual. No per-element offsets, no alpha cycling: the hero digits change every
minute by definition, and every genuinely static element (colon, labels, seam
track, dot) rides the plate.

### 1.6 The exit hint — a 4-second ghost that fades forever

`TAP TO EXIT`, Anonymous Pro 400, 10.5sp, letterSpacing 1sp, centred, 28dp
below the footnote, rendered at **30% alpha for the first 4 seconds** after the
overlay opens, then faded out over `specA()` (260ms, CurveA) and **removed from
composition**. Never again for that open; reopening the clock restarts the 4s
(it is keyed to the open, `remember` + `LaunchedEffect { delay(4000) }` inside
the overlay). After four seconds the surface carries zero instructions,
forever — the shipped permanent `NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT`
header is deleted. Tap-anywhere-to-exit is the overlay's only interaction and
needs no other documentation: a button would be a target, and a target is an
invitation to leave the session.

### 1.7 Brightness and cadence — the Round 0 delta

**Brightness (the missing half of the shipped DisposableEffect).** Inside the
*same* `DisposableEffect` that already sets `FLAG_KEEP_SCREEN_ON`:

1. On effect entry: capture `prev = window.attributes.screenBrightness`
   (may be `BRIGHTNESS_OVERRIDE_NONE` = −1f, system control).
2. Set `screenBrightness` by local hour at open: **0.25f between 20:00–06:00,
   0.40f otherwise.** His sessions live at known dayparts (sleep 20:00, wake
   03:30); 132sp glyphs at 88% cream need very little luminance to read across
   a room, and 0.25 at 3 AM is an instrument, not a lamp. Wave-1's flat 0.35
   is superseded by this two-step — same mechanism, truer to the room.
3. On dispose: restore `screenBrightness = prev` **exactly** (including −1f),
   then clear `FLAG_KEEP_SCREEN_ON`, in that order, in the same `onDispose`.
   Leaking a brightness override into the whole app is the kind of bug that
   gets a feature deleted; the restore is not optional polish.
4. Wave-1's Phase-2 ambient-light sensor is **cut**: a `TYPE_LIGHT` listener
   wakes the sensor stack all night to approximate what the clock-hour rule
   already knows about his day. No sensor.
5. A manual brightness change from the notification shade while the clock is
   open is overridden by the window value until dispose, and dispose restores
   the *saved* value. The clock owns the window while it is shown — the same
   terms as keep-on — and this is stated here so it is a decision, not a bug
   report later.
6. Interaction with system timeout: keep-on suspends the timeout entirely
   while the clock is visible. On dismiss, the timeout resumes under normal
   system rules — correct: always-on is opt-in per-open, the Deck underneath
   is not an always-on surface. No other surface may set this flag.

**Cadence.** The engine keeps its 1s ticker (it owns peek countdowns,
completion, block rollovers). The overlay must not inherit that heartbeat:
beside `_burnInFigure8MinuteStep` in `MonolithViewModel`, one derived flow —
`deskClockMinuteKey` = in cruise, `blockRemaining / 60`; in finale, raw
`blockRemaining`; in stopwatch, `elapsedSeconds / 60` — `distinctUntilChanged()`,
`stateIn(WhileSubscribed(5000))` like its neighbours. The overlay keys its hero
on this. Net effect: **≤1 recomposition per minute** for almost the whole
session, per-second only inside the finale where he is watching anyway.
Battery, plainly: pure-black ground (most pixels off) + minute cadence + no
continuous animation + brightness cap puts this overlay within noise of the
system AOD; the shipped build (per-second recomposition) is the expensive one.

### 1.8 Modes — the same plate, five states

1. **Countdown cruise/finale:** §1.3. Footnote left: `BLOCK 2 OF 4` when
   `totalStudyBlocks > 1`, else the loadout word from `profileName`
   (e.g. `DEEP STUDY`). Dot lit.
2. **Safe break** (`isInSafeBreak`): hero = `safeBreakSecondsRemaining` at
   **60% alpha**, minutes-only while ≥60s remain, MM:SS per-second inside the
   final 60s (the one moment break-seconds change behaviour — he must be back
   at the desk). Seam **frozen** at the finished-blocks fraction. Micro-block:
   line 1 `BLOCK {currentBlockIndex + 1} STARTS {h:mm a}` (now + break
   remaining), line 2 `ENDS {session end}` unchanged. Footnote left:
   `SAFE BREAK`. Dot **stays lit** — the session is live; a break that looks
   identical to work, or to no-session, would both be lies.
3. **Stopwatch:** §1.3/§1.4. Footnote left: `FLOOR IN {MM:SS to 1200}` while
   under the floor, then `FLOOR CLEARED`. The word flip is the entire
   celebration — nothing flashes, nothing recolours.
4. **Idle preview** (no active session; reachable only programmatically today —
   the Deck chip is running-only — but the overlay already handles `dialMinutes`):
   hero = dial value at 70% alpha, **no dot**, micro-block `NOT RUNNING`,
   seam empty, footnote right still shows wall time (it is still a clock face).
5. **Tamper/penalty:** the clock renders **nothing** about tamper — no line, no
   colour. All numbers silently use `effectiveTarget` (§0.3), so ENDS stays
   true. An accusation on the calm surface would be a mid-session decision
   prompt, which HOLD forbids; the fact is stated once, on the receipt (§4).

**Completion while the clock is open:** the ViewModel already hides the overlay
on completion. The clock renders no special final frame and no flash; if the
finale is on screen the hero's last visible state fades out with the overlay
(`specA`), and §4's stillness beat runs from the moment the clock is fully
gone. The fade is the full stop.

### 1.9 Motion · Haptics · Sound · Depth & light

- **Motion:** entry `fadeIn(specB())` / exit `fadeOut(specA())` (shipped, kept).
  Drift: `animateIntOffsetAsState(target, MonolithCurves.specB(DURATION_520))`
  — CurveB (0.05, 0.7, 0.1, 1), 520ms, once a minute: a heavy object settling,
  subliminal in peripheral vision; stillness for the other ~59.5s of every
  minute is what makes it read as an instrument. Band switch cruise→finale is a
  hard cut of the text (a measured number stepping, never an animated morph).
  No other motion exists on this surface.
- **Haptics:** **none.** Minute steps, drift settles, and band switches are
  silent in the hand — the phone is across the room or face-up on the desk;
  a buzz per minute for three hours is a demand, and this surface makes none.
- **Sound:** **none, including on open and close.** The shipped
  `toggleAlwaysOnDeskClock` plays `playMechanicalLatch(visible)` in the
  ViewModel — for this surface that is a bug: a click at 3 AM into a silent
  room, for the act of *looking at the clock*. Spec: the toggle path for the
  desk clock plays nothing (the latch sound belongs to physical-feeling
  controls; opening a window is not one). Completion sound is §4's, fired by
  the ticker path, not by the clock.
- **Depth & light:** there is no depth. No container, no card, no shadow, no
  border, no rounded anything — on true black a container would be a lit
  rectangle burning battery to say "this is a card". Depth is tonal only:
  hero 88% / values 80% / labels 45% / footnote 42% / seam track 14%, all steps
  of the single cream token, plus the one orange dot. Light carries no data:
  brightness (§1.7) is a function of the clock hour, never of progress,
  elapsed, or armed-ness.

### 1.10 State machine

States are **derived** from `activeSession` + `isAlwaysOnDeskClockVisible`,
never stored as clock-local booleans (a stored boolean dies with the process;
the session does not):

`CLOSED → (toggle true) → OPEN_CRUISE ⇄ OPEN_FINALE (R crosses 600) ;
OPEN_* → OPEN_BREAK (isInSafeBreak) → OPEN_CRUISE (break ends, block index +1) ;
OPEN_* → OPEN_PREVIEW is impossible (preview requires no session, and the clock
can only be opened from the running Deck) — preview exists only if the flag is
set programmatically with no session; OPEN_* → (session completes) → CLOSED
(VM-forced) → §4 receipt sequence ; OPEN_* → (any tap) → CLOSED.`
Drift step and band are pure functions of `(burnInStep, derived session)` —
recomposition, relaunch, or process death re-derives the identical plate.

### 1.11 Edge cases

- **Process death mid-session, clock open:** session persists in Room; clock
  flag is in-memory → clock closed on relaunch, session + Cloister (§3)
  intact. Acceptable and stated: always-on is per-open by design.
- **Drift step after relaunch** restarts at `n=0` (plate centred). Within any
  single session the tour runs for hours, so the centre bias across sessions
  is negligible; persisting the step is not worth a write per minute.
- **Midnight rollover:** footnote date re-formats at the next minute
  recomposition — flips within the minute, no special case.
- **Session armed for 180 min:** hero `2:59`-format H:MM; glyph-count style
  selection is by band + glyph count, both precomputed — no wrap, no jitter.
- **Rapid open/close:** brightness save/restore is inside one `DisposableEffect`
  keyed on the open; opens cannot nest (single boolean), so save/restore
  cannot stack or leak.
- **Accessibility:** hero `contentDescription` = `"{minutes} minutes left, ends
  {h:mm a}"` (finale: with seconds), updated at the display's own cadence —
  spoken truth at the same precision as printed truth.

### 1.12 Code: helps / fights

- **Helps:** the whole `FocusSessionEntity` is already passed in — every field
  consumed above exists (§5). `burnInStep` flow + 60s increment already wired
  (`MonolithViewModel:117–118, 323–325`). Keep-on `DisposableEffect` shipped in
  the overlay — brightness extends it in place. Auto-dismiss on completion
  shipped (`MonolithViewModel:330–335`). Finish-time format idiom exists on the
  Deck. Night tokens (`NightColorPrimary`, `NightColorPrimaryContainer`) are
  the only colours needed; `Color.Black` already the ground.
- **Fights:** `remainingSeconds` ignores penalty and is per-block (§0.3 — fix
  the derivation in `MonolithModels.kt`, one place, both surfaces benefit).
  `DeskClockTextStyle` still Sora (`Type.kt:79–87`). The ticker recomposes the
  overlay per second today (the minute-key flow in §1.7 is the fix, ~8 lines).
  `toggleAlwaysOnDeskClock` plays a latch sound (§1.9 — remove for this path).
  Shipped overlay body still contains the pill bar, banner card, permanent
  header, and pixel-raster drift — all deleted by this spec's rewrite
  (same signature, same `testTag`, no call-site change).

### 1.13 Law check

ARM: untouched (the clock is post-arm). HOLD: zero targets, zero decisions —
one tap exists and it only *leaves*. TRUTH: no floored bar, no stale seconds,
no `179:59`, ENDS includes penalty + breaks, wall time present. One fact one
rendering: REMAINING/ENDS/NOW in three separate homes; progress as position
only, no `%`. Accent ration: exactly one 6dp orange dot = session live.
Beauty/retention: composition + drift settle are the beauty; nothing on the
face asks for attention, which is why it can stay up for hours.

---

## 2. ESCAPEMENT-LITE — the Deck's running state

The Deck in-session today: an 84sp readout printing total minutes as MM:SS (the
`179:59` lie, live), a 6dp pill bar drawn from the *remaining* fraction floored
at 0.03 (shrinking = progress rendered as loss), and a `%` numeral underneath —
**the same fact rendered three times, two of them dishonestly**. Escapement-lite
replaces all of it with one stepper and the two numbers. Restraint is the spec:
nothing on this surface animates continuously, ever.

### 2.1 Mechanism

**The stepped arc.** In the wheel's vacated circle on the instrument card, one
printed arc: 240° sweep, same centre and radius family as the dial it replaces,
so the card's geometry doesn't change when the session starts — the wheel
leaves, the arc stays in its house. Printed on the arc: minor ticks every 5
minutes of the **current block**, major ticks at block boundaries are *not*
printed here (block structure is the desk seam's fact, §1.4 — the Deck arc is
the current block only; different fact, different surface, no duplication).

**The marker.** One cream marker (2dp wide × 11dp tall — the same marker
language as the desk seam's head, so the hand learns it once) rides the arc at
the block-elapsed fraction. It does not sweep. It **steps**:

- Cruise (block-remaining ≥ 600s): the marker's position is quantized to whole
  minutes of elapsed. Once a minute it advances one step.
- Finale (block-remaining < 600s): quantization becomes 1 second; the marker
  steps once per second.
- The step itself: 180ms `specA` (CurveA — the snap curve) between adjacent
  detent positions. An escapement tooth advances and *seats*; it never glides.
  Between steps: absolute stillness.

**The numbers — both, always, in fixed positions.**

- **Hero readout** (existing position, centre of the instrument card):
  block-remaining, through the §0.3/§1.3 shared formatter — cruise minutes-only
  (`1:42` H:MM or `42` + `MIN LEFT` micro-label), finale `08:41` MM:SS
  per-second. Anonymous Pro Bold, 84sp (the Deck's existing hero size — the
  desk clock's 132sp belongs to the across-the-room surface; the Deck is read
  in the hand). The readout's header word (existing row, left) becomes
  `BLOCK {n} OF {m} · RUNNING` when `totalStudyBlocks > 1`, else
  `SESSION RUNNING` (shipped text). Right of the same row, the shipped
  `SHUT {n} APPS` fact stays — it is true, it is his, it costs nothing.
- **Finish line** (existing position, the row under the wheel/arc, currently
  `finishPair`): left = `BLOCK ENDS {h:mm a}` (this block, true), right =
  `FINISHES {h:mm a}` (the **session** end, computed per §0.3 including later
  blocks, breaks, and penalty). When `totalStudyBlocks == 1` the two coincide
  and only `FINISHES {h:mm a}` prints, left-aligned — printing the same time
  twice under two labels would be one fact in two renderings. The shipped
  code prints block-end labelled `FINISHES` — that mislabel is deleted.
- **Deleted:** the 6dp pill bar, the `%` numeral. Elapsed is not printed as a
  number on the Deck at all — its rendering here is the marker's *position*;
  elapsed as digits lives on the desk clock's micro-block. Forward-looking
  surface, backward-looking digits elsewhere: that division is deliberate.

**The current task line.** Source: `MonolithViewModel.activeTask` (the ACTIVE
task, else first QUEUED — the ViewModel's existing semantics; the line's `NOW`
label is therefore the effective current task, and the session's frozen
`intention` is the fallback text if the flow emits null). Rendering is the
shipped Deck task row (NOW · title · DONE · QUEUE count), with these
running-state rulings:

- **DONE stays.** Ruling on the brief's question — *marking DONE is not a
  decision.* A decision chooses the future (what next, how long, what's
  blocked, whether to continue). DONE reports a fact about the past: this
  work is finished. It requires no deliberation, presents no options, and its
  consequence was pre-decided by the rested self who ordered the queue —
  `completeTaskAndAdvance` advances automatically. Banning it would force a
  lie (the line would keep calling finished work "NOW"). This overrules the
  Improvers' Linea rung that removes DONE mid-session: her HOLD argument
  proves too much — by its logic the valve itself (a far larger act) would
  also be banned, and the valve is his settled law. The line HOLDS on
  *choices*; reports pass.
- **Sub-step toggles stay**, on the same grounds (reports of fact), behind
  the shipped tap-to-expand on the title. Expansion is looking, not choosing.
- **QUEUE becomes static text.** Its shipped form is a button navigating to
  Tasks — navigation is Cloister-refused (§3), so mid-session the count
  renders as a fact (`QUEUE {n}`, secondary tone, no click target). The
  number stays because "what remains after this" is orientation, not choice.
- **When the task is DONE mid-session:** the title crossfades (260ms `specA`)
  to the next active task — a report update, no prompt, no toast, no "what
  next?" If the queue empties: the line renders the session's frozen
  `intention`; if the intention *is* the just-finished task's title, the line
  renders `NO TASK QUEUED` in secondary tone — a static fact, never a call to
  action. Nothing on this surface ever asks him anything. DEFER and EXTEND
  callbacks exist in the Deck's signature; any rendering of them in the
  running state is banned (both are decisions).
- DONE's feel: the ViewModel currently plays `playHarmonicSeal(arming = true)`
  inside `completeTaskAndAdvance` — a seal for a report is the wrong weight
  and the wrong owner (one gesture, one owner: the view). Spec: DONE press =
  `playFirmClick()` (fireFeel 8L) fired by the row; the ViewModel's seal on
  this path is flagged as fights (§2.6).

**Stopwatch running state:** arc = floor gauge (marker position =
`elapsed / 1200`, holds at full past the floor), hero = elapsed minutes
(§1.3 stopwatch rule), finish line left = `FLOOR IN {MM:SS}` → `FLOOR CLEARED`,
right side empty (there is no finish time; printing one would be inventing
an end he never set).

**Session utilities row** (shipped: DESK CLOCK chip + HELD chip) stays as-is —
both are doors to look-surfaces (§3), and the desk clock chip is this file's
§1 entry point. HELD's digest: reading is allowed mid-session; its
`CLEAR ALL` action is a destruction decision — hidden while a session is
active (the digest sheet is not my surface; the ruling is recorded here for
its owner: CLOSE stays, CLEAR ALL is inert/absent under Cloister).

### 2.2 Motion

Marker steps: 180ms `specA` per step, as above. Task-title change on advance:
260ms `specA` crossfade. Band switch cruise→finale: hard cut (a measured
number stepping). Nothing else moves. There is no idle animation, no pulsing,
no breathing — a running session is the calmest state the app has.

### 2.3 Haptics

**None for the escapement.** No per-minute buzz, no per-second tick in the
hand. The marker steps in silence. (The valve's escalating ticks in §3 are
felt because his hand is *on* the valve; the escapement is watched, not held,
and a haptic he isn't holding the phone to feel is either missed or, on a desk,
an audible buzz — a demand. This surface makes none.) DONE press: the firm
click's waveform (via `playFirmClick`, §2.1). Refused taps under Cloister:
Otis's feel (§3.3).

### 2.4 Sound

**Deliberate silence for the beat — justified, not omitted.** A real
escapement's tick is information for someone listening for it; here it would
be a per-second or per-minute sound in a silent room at 3 AM, for hours,
carrying no information the marker doesn't already show. Sound is a demand
for attention; HOLD forbids demands. The only sounds in the running state are
event sounds owned elsewhere: block rollover/safe-break start — **none**
(the desk clock's footnote flip and the Deck's arc reset are the rendering;
a chime per block would tax multi-block sessions), session completion — §4's
single pulse, valve events — §3/Ines. DONE: `playFirmClick()` 620Hz.

### 2.5 Depth & light

The arc is print on the card: track at the night staircase's recess tone,
ticks at secondary alpha, marker at 100% cream — the card's brightest point
after the hero digits. No fill under the arc, no glow, no orange (the Deck's
one orange left with the slide; the running Deck spends **zero** accent — the
accent's absence is how the running card reads "sealed" at a glance). Light
carries no data: no brightening toward the finish, no finale colour change;
urgency in the finale is carried by *precision* (seconds appearing), never
by tone.

### 2.6 State machine

Derived from `activeSession` (never stored): `IDLE → (arm) → RUNNING_CRUISE
⇄ RUNNING_FINALE (block-remaining crosses 600) ; RUNNING_* → RUNNING_BREAK
(isInSafeBreak: hero = break remaining per §1.8's break rule, arc frozen at
the finished block's full position, finish line left = `BLOCK {n+1} STARTS
{h:mm a}`) → RUNNING_CRUISE (next block) ; RUNNING_* → (completion) → §4
receipt ; RUNNING_* → (valve/grace end) → IDLE`. A routine-fired session
enters the identical machine with the identical face — attendance truth is
Sten's; the face does not know or show how the session started. There is no
`ROUTINE` visual state, no badge, no tint. (A `HELD — MANUAL SESSION` style
annotation would be a second rendering of provenance and a mid-session fact
he cannot act on.)

### 2.7 Edge cases

- **Block rollover while on the Deck:** elapsed resets in the entity; the arc
  marker returns to zero and the header word increments — one frame, no
  animation backwards (the marker *teleports* to zero at rollover; animating
  it backwards along the arc would render progress un-happening).
- **Penalty applied mid-session:** all derivations use `effectiveTarget`
  (§0.3); `FINISHES` moves later by the penalty amount, silently. No tamper
  rendering here (§1.8's ruling, same reason).
- **Clock/timezone change mid-session:** engine time is monotonic
  (`startElapsedRealtimeMillis`); finish times re-format from `now +
  remaining` at render, so the printed wall times stay true.
- **Task completed in the final second of a session:** `completeTaskAndAdvance`
  and completion race on the ticker — whichever lands, the receipt (§4) reads
  the completed session row's `tasksCompletedCount`/`completedTasksSummary`,
  which the repository writes at completion time; the line never renders a
  half-advanced state because the session leaves RUNNING in the same state
  emission.

### 2.8 Code: helps / fights

- **Helps:** running branch already replaces the wheel (the arc takes its
  slot with no layout surgery); `activeTask`/`queuedTasks` flows already feed
  the task row; DONE/expand/QUEUE row already rendered — this spec mostly
  *removes* (bar, %) and *relabels* (finish pair, QUEUE target). Desk clock +
  HELD chips already exist as the utilities row.
- **Fights:** `primaryReadout` formatter prints total-minutes MM:SS (shared
  formatter §0.3 replaces it); `progressFraction` is remaining-based and
  floored (deleted with the bar); `finishPair` mislabels block-end as
  FINISHES (recompute per §0.3); `completeTaskAndAdvance` plays the arming
  seal in the ViewModel (move feel to the row, downgrade to firm click);
  QUEUE's click target must be removed in the running branch only (the idle
  Deck doesn't render this row, so the change is local).

### 2.9 Law check

HOLD: no decisions — DONE and step toggles survive only as *reports* under
the stated test; DEFER/EXTEND/navigation die. TRUTH: no floored bar, no
shrinking progress, no mislabelled finish, no invented stopwatch end.
One fact one rendering: block progress = marker position only; elapsed
digits live on the desk clock, not here; % deleted. ARM: untouched.
Beauty: the stepper's stillness *is* the beauty — an instrument that moves
once a minute reads as furniture you trust.

---

## 3. SESSION CLOISTER — the lockdown contract, and the valve it protects

The Cloister is not a screen. It is the rule that during a session, the app
has exactly one room (the Deck), two windows (the desk clock, the held
digest), and one door (the valve). Everything else is refused — not hidden,
not greyed into a museum of forbidden buttons: **refused, at the single
navigation choke point, with a feel that says so.**

### 3.1 Mechanism — the lock set and where it is enforced

Enforcement lives in `MonolithViewModel.navigateTo` — the one function every
navigation path already funnels through (bottom nav `onTabSelected`,
QUEUE tap, header Guard entries, drawer entries). Guard, at the top:

```
if (activeSession?.isActive == true && destination != DECK) → refuse (§3.3), return
```

Plus two non-destination surfaces refused at their own entries (below).
Because the guard reads `activeSession.isActive` — a Room-backed flow — the
Cloister **is derived state**: kill the app mid-session, relaunch, and the
Cloister is already standing. No boolean is ever set at arm time.

**Per-destination ruling** (destinations per `MonolithDestination`):

| Destination | Ruling | Reason |
|---|---|---|
| DECK (Focus) | **Open — the room.** | Running state, desk clock entry, valve entry live here. |
| SHIELD | **Refused.** | Sleds + routines are rested-self decisions. The facts he'd check (SHUT count) already print on the Deck's instrument header. The ViewModel's one-way ratchet (tighten-only mid-session) stays as defence-in-depth for non-nav mutation paths, but the Cloister does not rely on it: tightening is also a decision, and HOLD admits none. |
| TASKS | **Refused.** | Composer, reordering, promote/defer/delete are decisions. Queue count already prints on the Deck task line (§2.1). A "view-only Tasks" was considered and killed: a destination he can enter but not touch is a room of greyed buttons that invites workaround-hunting at his weakest hour. |
| VAULT (Ledger tab, fate OPEN) | **Refused, independent of the Ledger verdict.** | History mid-session is vanity plus decision fuel ("only 2h today — extend?"). Whatever the Ledger becomes — tab, sheet, or nothing — it is closed while a session runs. |
| GUARD (not a tab; via header/drawer) | **Refused.** | Permissions, sound, theme, website list are configuration decisions. Guard's fate is OPEN; this ruling is not: its *editing* surfaces are cloistered mid-session under any outcome. |

**Per-surface ruling for editing surfaces** (all become unreachable, none are
greyed — greying is for surfaces you can see; these he cannot reach):

- **Tasks composer** (FocusSequencerScreen): unreachable (destination refused).
- **App sleds** (Shield): unreachable. **Routine rows + routine editor**
  (Shield): unreachable — editing a routine mid-session would let the tired
  self rewrite the rested self, including the routine that fired *this*
  session. **Loadout cap / profiles**: unreachable (Shield/Deck idle only;
  the Deck's idle controls are already not rendered in the running branch —
  shipped behaviour, inherited). **Deck wheel / slide / preset / FLOW key:**
  not rendered while running (shipped ✓). **Intention field:** not rendered
  while running (shipped ✓). **Guard website list, toggles, sound/theme:**
  unreachable (drawer + destination refused).
- **Header pull-down drawer** (`UnifiedStatusGuardDrawer`): refused at the
  header tap, same feel as a nav refusal (§3.3) — the drawer hosts toggles
  and the Guard/tower entries, so the drawer itself is an editing surface.
  The header's printed facts (HELD count, guard readiness) stay printed;
  facts are not doors.
- **Streak tower:** unreachable (its only entry is the drawer).
- **Airlock intercept overlay:** not nav and not the Cloister's to redesign —
  it is the Shield doing its job. Flagged for Voss, one line: its
  reason-selection chips are mid-session choices sitting uneasily under
  HOLD; the default reason + RETURN + pre-authorized peek are not. Not
  specced here.

**What STAYS available (exhaustive):** the Deck running state (§2, read +
reports); desk clock open/dismiss (§1); the HELD digest — read + CLOSE only,
`CLEAR ALL` absent while a session is active; **the emergency valve** (§3.4)
including its KEEP FOCUSING exit; **grace-window abort** (first 10 seconds,
`elapsedSeconds <= 10`, shipped on the Deck) — kept, because it is not a
mid-session decision but the correction of a mis-arm inside the ARM window
itself, it costs no token, and after second 10 it ceases to exist; **task
DONE + sub-step toggles** (§2.1's report ruling); the **2-minute peek pill**
(pre-authorized by the rested self's tier choice; its countdown is a fact,
its early-end is a return, not a decision); system Back on the Deck does
nothing (shipped default once non-Deck destinations are unreachable — the
existing BackHandler to DECK becomes unreachable code under Cloister, kept
harmless).

### 3.2 Visual state of the locked nav

Otis owns the band switch (his `plan/maximpl-parts/09-otis-chrome.md`); the
contract from this side, converged by mail (thread cloister-nav-lock, his
numbers adopted, my lock set confirmed):

- The rail **stays visible** — hiding it would lie about the app's structure
  and send a tired man hunting for it. Locked stops print at the
  **text-secondary tier** (night staircase ≈5.8:1; never a Material
  "disabled" grey, no lock icons, no badges — alpha is the whole language).
  FOCUS stays full ink; Otis's 3dp armed lamp at FOCUS stays lit (his
  rendering of the same fact on his surface — the rail and the desk clock
  are never co-visible, so the lamp and §1's dot never duplicate in view).
- The nav takes `lockedStops: Set<Int>` as host-supplied data (Otis's
  design) = every non-FOCUS stop while `activeSession.isActive`. The host
  already passes `isSessionArmed` into `MonolithBottomNavView` — the data
  path exists; the guard in `navigateTo` is the enforcement, the rail state
  is the rendering. Both are required: rendering without the guard would be
  a locked door that's actually open (every non-nav caller), guard without
  the rendering would be a door that looks open and silently isn't (a lie
  by appearance).

### 3.3 Motion · Haptics · Sound — the refusal feel (Otis's, adopted verbatim)

- **Refused tap on a locked stop:** `fireFeel(1L, 300Hz)` — one low pitched
  detent, **sound only, no latch haptic** (a latch feel would mimic success);
  the cap **twitches 0.12 stop-units** toward the stop — **120ms out CurveA,
  180ms back CurveA** — and returns. No navigation, no seat sound, no text,
  no toast, no dialog, no counter, no escalation on repeated taps: the tenth
  refusal is identical to the first. A switch that moves a little and
  refuses reads honest; a dead switch reads broken.
- **Drag against the lock:** cap follows at **0.18× gain**, hard wall at
  **0.22 stop-units**, same spring-back on release.
- **Header drawer refusal:** the same 1L/300Hz knock; the header does not
  twitch (it is not a switch) — sound only, drawer stays shut.
- **Arm from elsewhere (converged with Otis):** if a session arms (manual
  slide — impossible off-Deck — or **routine-fired**) while he is seated on
  a non-FOCUS stop, the host **force-navigates to DECK**; the cap travels
  home under the nav's normal travel pricing, and the lock engages **on
  arrival**. He is never frozen standing in a room the session just locked.
- **Depth & light:** refusal changes no tones beyond the locked stops'
  secondary-tier print. No red, no alert colour (TokenAlert is dead as a
  colour; refusal is form — position + sound — never hue).

### 3.4 The valve host — DisarmChallengeOverlay (my surface, Ines's valve)

The full machine is **converged with Ines and mailed to Ren as a build spec**
(`mail/ren/0527__from-freya__valve-overlay-buildspec.md`; her Element 4 in
`plan/maximpl-parts/02-ines-arming.md` is the valve's own spec). What follows
is the overlay side, stated once, completely.

**One machine, three owners:** ViewModel owns the bank · view owns the grip ·
overlay owns the visit.

- **State:** `DisarmChallengeState` gains `bankedSeconds: Int ∈ {0,15,30,45}`
  (ViewModel-owned; born 0 at session start; **persists across overlay
  dismiss/reopen within the same session**; dies with the session).
  `Emergency60sValveView` receives it in its update block and gains
  `onBankCrossed: ((Int) -> Unit)?` for the 15/30/45 crossings. Held-seconds
  are view-local; on dismiss mid-grace/mid-drain, held settles to the bank
  floor immediately (Ines's rule — he wasn't holding while he was elsewhere).
- **Slip grammar (Ines's numbers, adopted whole):** release → **800ms grace**
  (no drain, no accrual; re-grab resumes exactly) → **drain at 2 held-seconds
  per real second**, unbanked only, stopping dead at the bank floor → re-grab
  mid-drain halts instantly, accrual resumes after **250ms re-seat**. Feel:
  segment ticks climb 420→660Hz resetting at each bank; bank strikes fall in
  pitch 560/470/390Hz and multiply (1/2/3 latches); drain ticks every 250ms
  gliding 600→280Hz; catch at floor = latch. (Rosa's and Cato's wall specs —
  800ms grace, bartack banks at 15/30/45 — converge on the same numbers.)
- **Host layout delta:** valve AndroidView height **68dp → 144dp**
  (accepted from Ines: 44sp hero numeral + 8dp + 88dp bar + centre line, all
  drawn inside the view so numeral/bar/line can never disagree). Card chrome,
  `fadeIn(specC)` / `fadeOut(specA)`, night-forced valve: unchanged. On
  screens <640dp tall, card vertical spacing compresses 16dp → 12dp before
  anything else moves; the 88dp bar never shrinks — it is the target a
  panicked hand must not have to aim at.
- **Copy (verbatim, replacing the shipped paragraph — whose "hold
  continuously for 60 seconds" the banked rule makes a lie):**
  title `EMERGENCY BREAK` · body, tokens=1: *"This ends the session early.
  It is the only break this week."* · body, tokens=0: *"This week's break is
  already spent. Sessions end when their time does."* · rule line **under**
  the valve: *"Hold to fill 60 seconds. Every 15 seconds banks. If your hand
  slips, what isn't banked drains away. The banked part stays."* · chip:
  `KEEP FOCUSING` (the "(STAY ARMED)" parenthetical dies).
- **Overlay states:**
  - `OPEN_IDLE` — chip live; nothing else on the card moves.
  - `HOLDING` (grip down) — **KEEP FOCUSING dims to 35% alpha and is inert**:
    a mid-grip dismissal (second hand, stray tap) would orphan the grip
    state. The finger on the valve is the only input the card accepts.
  - `GRACE / DRAINING` — chip **stays live** (abandoning an attempt is a
    legal act; dismiss settles held → floor). The **card renders nothing**
    about the slip — no shake, no colour, no copy. The slip's entire
    rendering lives inside the valve block (hatched draining fill, centre
    line `DRAINING — HOLD TO STOP IT`, hero numeral counting **up** as held
    evaporates). One event, one rendering, one view.
  - `COMPLETED` (total = 60) — the view plays **nothing**; the one sound is
    the ViewModel's existing `playHarmonicSeal(arming = false)`; the view's
    shipped `fireFeel(10L)` on completion is **deleted** (today both fire —
    two sounds for one event). Overlay closes straight to the idle,
    uncloistered Deck. **No receipt** (§4 is completion-only; emergency and
    completion never share an exit form).
  - `ABANDONED` (chip tap, or system Back — Back is routed to dismiss, the
    safe direction) — overlay closes; bank persists; reopening greets him
    with the true owed number and Ines's centre line
    `BANKED {n} OF 60 · HOLD TO CONTINUE`. Dismissing, slipping, and draining
    spend **no token**; the token decrements only on a completed break
    (shipped ViewModel behaviour, now stated as law).
  - `SPENT` (tokens = 0) — the overlay still opens: it is information, not a
    door. Valve touch disabled (shipped), hero replaced by valve-drawn
    `NO BREAKS LEFT`, centre line `THE WEEK'S ONE BREAK IS SPENT`, body =
    tokens=0 copy, chip the only action. The card must not show a
    holdable-looking valve that cannot complete.
  - **Scrim:** no outside-tap dismiss (shipped: none — kept). Exits are the
    chip and completion only. **Brightness/keep-on:** this overlay never
    touches either — those belong to §1's DisposableEffect alone.
  - **Deck chip truth:** the Deck's valve-entry chip does **not** display
    the bank (`EMERGENCY BREAK` + token count only). The bank's one home is
    the overlay; a `BANKED 30` badge on the Deck would be a second rendering
    and a standing temptation rendered at rest. (Marked UNCONVERGED in the
    letter to Ren — Ines has not ruled; flagged there so nobody builds a
    guess. My ruling stands in this file either way for the Deck surface.)

### 3.5 How the Cloister lifts

Trigger: the same state transitions that end the session — ticker-detected
natural completion, valve `confirmDisarmSession`, stopwatch clean completion
in `requestDisarmChallenge`, grace abort. Because the Cloister is derived
from `activeSession.isActive`, it lifts **in the same state emission** that
ends the session: there is no unlock event, no timer, no animation of its
own beyond the nav stops restoring to full ink over **420ms `specB`**. No
sound, no "unlocked" copy, no celebration. If §4's receipt is showing it
covers the rail; by the time he dismisses it, the lift is long done — there
is never a visible second step. Mid-session process death + relaunch:
Cloister re-derives standing (§3.1). Session ends while the desk clock is
open: the ViewModel already force-closes the clock; the receipt sequence
(§4) owns what he sees next.

### 3.6 State machine (whole Cloister)

`UNCLOISTERED ⇄ (session becomes active, from any destination → force-nav
DECK, lock on arrival) CLOISTERED{ substates: DECK_IDLE_VIEW / DESK_CLOCK /
DIGEST_READ / VALVE (§3.4's machine, nested) / GRACE_10S (first 10s only) }
→ (session inactive by any path) UNCLOISTERED`. The Airlock and peek pill
are parallel Shield machinery, not Cloister states.

### 3.7 Edge cases

- **Routine fires while the disarm overlay is somehow open:** impossible —
  the overlay requires an active session, and the receiver never steamrolls
  one. **Routine fires while the receipt is up:** receipt implies no active
  session; a routine firing during it force-navigates per §3.3 once it arms
  — the receipt is dismissed by the arm (the new session's Cloister covers
  it; the receipt's dismissed-id persistence in §4 prevents resurrection).
- **Two rapid valve opens:** bank state is single and VM-owned; the second
  open renders the same bank. No double-attempt can exist.
- **Token spent in a previous session this week:** §3.4 SPENT state, from
  the moment the overlay opens. **Persistence delta (Ines, endorsed):**
  token + week must persist (DataStore/Room, Monday-start local) — the
  shipped in-memory `StateFlow(1)` refunds the break on force-quit, and a
  refundable cap is not a cap.
- **Session ends naturally while the valve overlay is open** (timer hits
  zero under his thumb): overlay dismisses with the session (the
  ViewModel's completion path closes overlays), bank dies unspent, token
  untouched, §4 receipt follows. The valve can never complete into a dead
  session: `confirmDisarmSession` on an inactive session is a no-op in the
  repository path — stated so the race is designed, not discovered.

### 3.8 Code: helps / fights

- **Helps:** single `navigateTo` choke point; `isSessionArmed` already
  plumbed into the nav view; ratchet guards already exist as defence-in-
  depth; disarm overlay already has no scrim-dismiss and already disables
  the valve at 0 tokens; ViewModel already decrements the token only on
  non-grace completed disarm and already plays seal(false) there.
- **Fights:** `navigateTo` currently navigates unconditionally (guard is
  new, ~6 lines); header drawer has no session guard (add the same check at
  its toggle); digest `CLEAR ALL` has no session condition (one flag);
  in-memory weekly token (persistence delta); valve's single-float
  `holdProgress` with instant zeroing (replaced by Ines's bank engine);
  completion double-sound (delete the view's 10L).

### 3.9 Law check

HOLD absolute: this section *is* HOLD, enforced at one choke point, derived
from persisted state, with reports (§2.1) as the only surviving verbs and
the valve as the only surviving door — priced exactly as he set it. TRUTH:
locked things look locked (alpha + refusal feel), open things are actually
open (guard + rendering together), the bank is never displayed where it
isn't owned. ARM: force-nav on arm means a routine firing never strands
him. One machine: valve/overlay states above are Ines's machine with a host
around it, converged by mail, not two specs hoping to meet.

---

## 4. SESSION-END RECEIPT — the moment of completion

The receipt is the session's only ceremony, and it is rationed to exactly one
occasion: a session that **completed**. Not a valve break, not a grace abort
(a grace-cancelled session never happened — the repository already writes it
as zero banked seconds with no log line). It renders from the completed
session row, frozen at completion; it shows **regardless of the Ledger's
fate** (§4.4 flags the one line that depends on it).

### 4.1 Mechanism — what renders, line by line

Full-screen, chrome-free, on `NightColorBackground` `#171412` (the night
staircase ground — **not** AOD black; black belongs to the desk clock, and
using it here would make two surfaces share a ground that means different
things). No nav, no header, no buttons. All type real weights: Anonymous Pro
for numbers, Atkinson Hyperlegible for words.

```
   DONE                                                ← word, Atkinson Bold 13sp, tracked, secondary tier

   2H 00M                                              ← hero, Anonymous Pro Bold 64sp, cream 100%

   TARGET 2H 00M · 4 BLOCKS OF 30M                     ← Anonymous Pro 400 11.5sp, secondary
   LOADOUT — DEEP STUDY                                ← profileName, printed as stored
   INTENTION — CELL CYCLE — MITOSIS                    ← session.intention, frozen at arm
   TASKS DONE — 3                                      ← only if tasksCompletedCount > 0
   HELD AGAINST 7 REACHES                              ← only if phantomReachesBlocked > 0
   INCLUDES 15M PENALTY — CLOCK TAMPER DETECTED        ← only if tamperDetected (plain words, once)

   № 0147 · THU 29 OCT · ENDED 5:42 AM                 ← provenance line, Anonymous Pro 400 10.5sp, footnote tier
   {archive footer — §4.4, one line, Ledger-dependent}
```

- **Hero = actual study duration.** Countdown completion:
  `(targetDurationSeconds + penaltySecondsAdded) × totalStudyBlocks`,
  formatted H:MM / `M MIN` by the shared formatter — on a clean completion
  it equals the target by construction (every block ran its full effective
  target); with a tamper penalty it is honestly *larger* than the target
  line, and the penalty line explains the difference in plain words. Safe
  breaks are **never** folded in — breaks are not study. Stopwatch
  completion: hero = `elapsedSeconds` (the final row is true for stopwatch;
  it never resets).
- **Duration vs target** is therefore two numbers in fixed positions — hero
  (actual) and the TARGET line (the rested self's order, factorised:
  blocks × block length) — not a percentage, not a bar, not a verdict word.
  There is no "goal smashed", no streak, no comparison to yesterday:
  comparison is Ledger's business, and the receipt states this session once.
- **Provenance without crypto theatre:** `№ {id}` — `FocusSessionEntity.id`,
  the Room auto-generated primary key: plain, sequential, true. Zero-padded
  to 4. **Explicitly banned from this face:** `hmacSignature` and
  `forgedArtifactId` (the random Curio artifact the repository currently
  mints at completion) — a hash he cannot verify and a random artifact ID
  are provenance *costume*; the sequential number is provenance. (This
  answers Emil's wall objection — a sequence number is an archive in
  disguise — head-on: the number is not the archive, it is the *name* of
  the row that already exists in Room in every Ledger outcome; printing it
  creates nothing. If the Ledger is killed, §4.4's footer says what that
  means, in the open, on the same face.)
- **Ended time** is computed from the record (`startWallTimeMillis` +
  study seconds + `(totalStudyBlocks − 1) × 600` break seconds; stopwatch:
  start + elapsed), never from render-time `now` — a receipt re-rendered
  after process death still prints the true end.
- **Snapshot rule:** every line is computed **once**, from the completed row
  + `tasksCompletedCount`/`completedTasksSummary` (both genuinely written by
  `completeTaskAndAdvance` during the session — verified in the repository,
  §5), and frozen into a `ReceiptUi` value. The receipt never observes live
  flows; a task completed during a *later* session cannot leak into it.

### 4.2 Motion · Haptics · Sound

- **Stillness first:** on completion — desk clock (if open) fades, Deck
  returns idle — then **400ms of nothing**, then the receipt fades in over
  `specB` (420ms, CurveB). No bounce, no stamp slam, no
  `MechanicalOvershoot` (banned on this surface): a completion that startles
  is a demand; this one *arrives*. (Fen's completion law — stillness, one
  sound, one receipt line, unreplayable — is the shape adopted.)
- **Sound: exactly one — `playCompletionPulse()`** (185Hz, 64ms, gain 0.68,
  the winding-down triple-tick; its haptic waveform [0,10,24,10,24,14]ms
  rides with it — that is the receipt's entire haptics section: one bundled
  pulse, felt once). **This requires a ticker change:** the natural-
  completion path today plays `playHarmonicSeal(arming = false)` — the
  *same* sound as a valve break. Completion and emergency must never share
  an exit form, sound included: seal(false) becomes the break's sound
  alone (§3.4), the pulse becomes completion's alone. **Marked UNCONVERGED**
  in the letter to Ren (Ines's file bans the pulse from the valve *view*
  only and doesn't rule the ticker path) — flagged, not silently decided
  for her surface; the receipt spec stands on the pulse, and if Voss rules
  otherwise the substitution is one call site.
- **Dismissal:** the receipt **stays until tapped** — no auto-dismiss timer
  (it is the app's screenshot frame; a record that vanishes on a timer
  can't be kept, and checking the time on it shouldn't rush him). One tap
  anywhere dismisses to the idle Deck (the desk clock's learned gesture).
  No buttons exist: a "START ANOTHER" button would be a decision offered
  to a tired man at the exact moment HOLD ends — the rested self will
  decide later, in the idle app, by the normal path.

### 4.3 State machine

`NONE → (session row lands completedSuccessfully) PENDING_STILLNESS
(400ms, receipt value frozen) → SHOWING → (tap) DISMISSED`.
Persistence: `dismissedReceiptSessionId` (one Int, DataStore/preferences —
the same small store the token persistence delta in §3.7 needs). Rules: the
receipt renders only for the **latest** completed session, only if its id ≠
dismissed id, and never once a newer session has armed. Process death while
SHOWING: on relaunch it re-shows once (same rules) — a completion he never
saw is a fact he is still owed. Process death during PENDING_STILLNESS: the
stillness is skipped on relaunch (it was a courtesy, not a state he owes
time to). Valve break / grace abort never enter this machine (§3.4 /
§3.1). Stopwatch clean completion (`requestDisarmChallenge` past the
floor) **does** — it is a completion, and today it bypasses the ticker's
completion branch, so the receipt trigger must key on the session-state
transition (active → inactive + completedSuccessfully), not on the ticker
code path. One trigger, both roads in.

### 4.4 Archive dependency — flagged per Ledger outcome (not decided)

The receipt renders identically under every outcome except its **final
footer line**, which is a single string selected by which Ledger build
ships. The receipt must never print a destination that doesn't exist:

| Ledger outcome | Footer line | Archive home of the session row |
|---|---|---|
| **Kept as a tab** (VAULT, currently labelled "Receipts") | `KEPT IN RECEIPTS` | The Ledger tab's history list (session rows already feed `sessionHistory`). |
| **Demoted** (sheet / Shield section / chrome corner) | `KEPT IN SESSION HISTORY` — the implementer substitutes the shipped host's actual name at build time; the string must name a place that exists in that build | The demoted host's history section (same `sessionHistory` rows, smaller home). |
| **Killed entirely** | `MONOLITH KEEPS NO COPY — YOUR STUDY RECORD LIVES IN YPT` | Nowhere in the app's surfaces. The Room row remains as engine state (the ticker and 7-day math read history) but no surface renders it as a record — the footer says so plainly rather than letting the № line imply an archive that isn't there. |

YPT tracks his study; it does not track Monolith (holds spent, deflections,
routine attendance) — that asymmetry is the Ledger question itself, and it
is **Voss/Sarthak's to settle**. This file takes no side; it guarantees the
receipt is buildable and truthful under all three.

### 4.5 Edge cases

- **Completion while the desk clock is open:** §1.8's sequence — clock
  fades (specA), stillness counts from fully-gone, receipt fades in.
- **Completion while the disarm overlay is open:** §3.7 — overlay dies with
  the session, receipt follows; no valve state survives to render.
- **Multi-block with penalty in a later block:** hero math (§4.1) uses the
  final row's `penaltySecondsAdded` — the repository applies the penalty
  once (guarded by `penaltySecondsAdded == 0`), so the multiplication is
  honest: the penalty extended every subsequent block's effective target
  equally in `updateSessionProgress`'s accounting… stated precisely: the
  penalty is added to the effective target for the block in which it was
  detected and carried in the row thereafter; if it landed mid-session the
  true study total is `(blocks before) × target + (blocks from detection)
  × (target + penalty)`, which the final row alone cannot reconstruct.
  **Ruling:** the receipt prints the reconstructable truth — hero =
  `targetDurationSeconds × totalStudyBlocks + penaltySecondsAdded` (penalty
  counted **once**, the minimum true total) and the penalty line carries
  the fact. Understating by at most (blocks−1)×penalty in a tamper case is
  the honest bound; inventing the detection block would be a guess printed
  as fact. Tamper is by construction a rare, adversarial path; the common
  paths (no penalty; stopwatch) are exact.
- **Zero-task, zero-deflection session:** the conditional lines are simply
  absent — absence renders as absence, never as `0` rows (a printed zero is
  a stat begging to be a shrine).
- **Receipt for a 5-minute grace-adjacent session:** impossible — sessions
  under the stopwatch floor can't complete, and countdown minimums are
  enforced at arm. Any completed row is a real session.

### 4.6 Code: helps / fights

- **Helps:** the completed row carries every field the receipt reads (§5);
  `tasksCompletedCount`/`completedTasksSummary` are really populated
  (Repository ~L1298–1312, `completedInSessionId` stamped per task);
  `sessionHistory` flow already exists for the archive homes; completion
  detection exists in the ticker.
- **Fights:** completion sound is the disarm seal (one-call swap, §4.2 —
  UNCONVERGED flag noted); stopwatch completion bypasses the ticker branch
  (trigger must move to the state transition, §4.3); the repository mints a
  random `forgedArtifactId` at completion (left in the DB, banned from the
  face — no code change required, a rendering prohibition); no receipt
  surface or dismissed-id store exists yet (new, small: one composable +
  one persisted Int).

### 4.7 Law check

TRUTH once: every number from the frozen row, end time computed not
assumed, penalty understated-at-worst and labelled, banned crypto fields
named. HOLD: the receipt offers nothing — no next session, no extension,
no share button; it is looked at and dismissed. Ceremony rationed: one
sound, one fade, one occasion-type (completion only), stillness before it.
One fact one rendering: the № names the row; the Ledger (any outcome)
remains the row's home — the receipt is the *moment*, not a second archive.

---

## 5. FIELD PROOF — everything above reads fields that already exist

`FocusSessionEntity` (`data/MonolithModels.kt`), verified field by field.
No new plumbing is required for §§1–4 except where a § explicitly flags a
delta (minute-key flow, bank state, token/receipt persistence).

| Need | Field / source | Status |
|---|---|---|
| Block remaining / elapsed | `elapsedSeconds`, `targetDurationSeconds` (+ derived `remainingSeconds`) | Exists; derivation must add `penaltySecondsAdded` (§0.3) |
| Penalty truth | `penaltySecondsAdded`, `tamperDetected` | Exist; written by `updateSessionProgress` |
| Session end / start | `startWallTimeMillis` | Exists (HMAC-signed at start) |
| Block structure | `currentBlockIndex`, `totalStudyBlocks` | Exist; maintained across rollovers |
| Safe break | `isInSafeBreak`, `safeBreakSecondsRemaining` (600s breaks, `SAFE_BREAK_DURATION_SECONDS`) | Exist |
| Stopwatch + floor | `isStopwatchMode`; floor = `targetDurationSeconds` = `STOPWATCH_MIN_FLOOR_SECONDS` (1200) | Exist |
| Hero / intention | `intention` (frozen at arm in `startSession`) | Exists |
| Loadout line | `profileName` (the selected profile/loadout name at arm) | Exists |
| Receipt task facts | `tasksCompletedCount`, `completedTasksSummary`; `FocusTaskEntity.completedInSessionId` | Exist **and populated** (Repository `completeTaskAndAdvance`) |
| Receipt deflections | `phantomReachesBlocked` (incremented by `recordIntercept`) | Exists |
| Receipt provenance № | `id` (autoGenerate PK) | Exists |
| Banned from receipt | `hmacSignature`, `forgedArtifactId` | Exist — prohibited from rendering (§4.1) |
| Live task for §1/§2 lines | `MonolithViewModel.activeTask` flow (ACTIVE else first QUEUED) | Exists |
| Burn-in cadence | `_burnInFigure8MinuteStep`, incremented per 60 ticker ticks | Exists |
| Valve tokens | `_weeklyEmergencyTokensRemaining` (VM) | Exists; persistence is the §3.7 delta |
| Valve bank | — | **Delta:** `bankedSeconds` on `DisarmChallengeState` + VM ownership (§3.4) |
| Cloister source | `activeSession: StateFlow<FocusSessionEntity?>` (Room-backed) | Exists |
| Receipt dismissal | — | **Delta:** one persisted Int (§4.3) |

**Cross-surface invariants (stated once):** one shared duration formatter
(H:MM law, §0.3) serves Deck hero, desk hero, and receipt hero — a duration
must read identically everywhere it appears. Anonymous Pro is every
number's face; Atkinson every word's; real weights only (400/700 — no
Medium or SemiBold is ever named for either family). Orange appears on
these surfaces exactly twice in the whole file: §1's 6dp live dot and
Ines's valve live-chamber fill — both fills, both commitments, never text.
Curves used: A/B/C only; durations from {180, 260, 280, 420, 520} plus
Ines's valve data-constants (800ms grace, 250ms re-seat/drain-tick, 32ms
runnable, 140ms bank stamp). No new curves, no new colours, no green.

**Conflicts resolved in this file, for the record:** wave-1 hero =
session-remaining → corrected to block-remaining (§0.3, entity resets
elapsed per block); wave-1 micro-block `ELAPSED 111:43` → banned format
(§1.3); wave-1 flat brightness 0.35 + light sensor → two-step clock-hour
rule, sensor cut (§1.7); wave-1 Atkinson SemiBold intention → Regular 400
(§1.1, the weight doesn't exist); Improvers/Linea DONE-removal → overruled
with the report/decision test (§2.1); Emil's no-sequence-number receipt →
№ kept, reasoning stated (§4.1); Suki's seconds-in-final-minute → brief's
final-10-minutes rule stands (§1.3); Otis refusal numbers adopted over my
draft values (§3.3); Ines bank-persistence adopted over my
reset-on-dismiss draft (§3.4). **UNCONVERGED, flagged to Ren, not guessed:**
natural-completion sound (§4.2) and Deck-chip bank display (§3.4).

— Freya, Maximum-Implementable · plan wave · 2026-10-29
