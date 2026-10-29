# 07 — TASK ROWS + THE LIGHT WELL (PLAN WAVE)

**Seat:** Rurik, Maximum-Implementable (coordinator: Voss). **Element:** the queue of
`FocusSequencerScreen` SECTION 3 — the Light Well rendering, the ACTIVE block, all
other rows, the DONE seal, and the header numeral that inherits the dead horizon
bar's one true fact. **Plan only. No code.**

**Base verified against current code:** `FocusSequencerScreen.kt` (whole list
region), `MonolithModels.kt` (`FocusTaskEntity.subStepProgressFraction`,
`completedSubStepsCount`, `subSteps[i].isDone` — all already computed),
`MonolithCurves.kt` (specA/B/C; durations {180, 260, 280, 420, 520} only),
`Color.kt` / `Theme.kt` (post-staircase tokens), `Type.kt` (real weights:
Atkinson 400/700 only, Anonymous Pro 400/700, Sora 400/500/600/700),
`TactileSoundEngine.kt` (five named calls; haptics bundled inside each call).

## Decisions taken where sources fought (all final, nothing left open)

1. **The Well wins over "no tint."** Zeile's house position (no background tint on
   ACTIVE at any rung) is superseded by synthesis item 27 + this brief: the Light
   Well *is* tone. Rows get tonal backgrounds per §Depth & light.
2. **START stays an orange WORD, not a filled key.** Zeile's peak re-argument
   (44dp filled orange key) and Luz's START fill are rejected: a filled key is a
   new object inside a tone-and-type system, it breaks the one-composable
   WordAction grammar every other verb uses, and synthesis item 12's "orange is a
   fill" was written about Deck's slide — on Tasks the locked law for this wave is
   *exactly one orange: the ACTIVE task's START*, and the word is how wave-1 spent
   it without halving it. Findability is bought by position (ACTIVE is always the
   lit top row) and by the press choreography, not by a slab.
3. **START carries no minutes.** Wave-1 printed `START 25m`; Zeile is right under
   one-fact law: the same row's meta already prints `#01 · 25m`. The word is
   `START` (or `RESUME` while a session runs). One rendering of the minutes.
4. **The meta fraction `· 2/3` is DROPPED.** Wave-1 and Zeile both appended the
   step tally to ACTIVE's meta. That is the sub-step fraction rendered twice —
   once as counts, once as the rail's length. The rail alone carries progress
   (§Mechanism 2). The tally is recoverable by counting struck steps, which are
   on screen.
5. **The rail is honest zero.** Wave-1 §2c gave a no-sub-step ACTIVE task a
   full-height rail ("loaded"). The brief fixes fill height =
   `subStepProgressFraction`, and the code's fraction for a step-less queued task
   is `0f`. Fill = fraction, always, no special case. A step-less ACTIVE task
   shows track only; its hero weight comes from band + 20sp title.
6. **DONE strikes the TITLE, not the remaining steps.** Wave-1's seal drew every
   undrawn step strike at once — that renders "each step done" when only "task
   done" is true. Luz's correction is adopted as law here: the drawn strike is
   reserved — step strikes for steps, the title strike for DONE alone. During
   the seal the steps keep their true struck/unstruck state (§Mechanism 4).
7. **Hit-stop is row-local, 60ms (110ms on the final step).** WILD §3's
   screen-wide freeze is rejected (it kills scroll under a one-handed flick);
   Zeile's 40ms pause-at-70% and Luz's 60ms pre-strike freeze converge into one
   mechanism placed at strike start (§Mechanism 3). 60ms = 4 frames at 60Hz —
   the shortest stop that reads as impact at ~30 sub-step taps a day.
8. **CUE the verb is dead; its job is the row tap.** No word replaces it.
   Promotion is a full-row tap on a queue row (§Mechanism 5). The old CUE also
   navigated to Deck in the same click — that coupling dies: promotion stays on
   Tasks; only ACTIVE's START leaves the screen.
9. **DEL and SPLIT live only inside ACTIVE.** Queue rows carry no words at all,
   so a tired one-handed scroll contains zero destructive targets. Deleting a
   queued task costs promote-then-DEL (two deliberate taps).
10. **The header numeral re-strike is specA 180, not 120ms.** Horiz's ladder
    used a 120ms letterpress settle — outside the duration vocabulary. The
    vocabulary binds: 180ms (§Motion 6).
11. **Ivo's strum/stamp voices are not used.** The final sub-step does not strum
    a chord and DONE does not stamp: neither voice exists among the five named
    engine calls, and this element's envelope is the existing engine. The
    STEP SET pitch *values* are adopted — mapped onto `playRotaryDetent(freqHz)`
    (§Sound) — so the pitch family survives inside the implementable engine.
    Alba holds the family; the mapping is mailed to her for confirmation.

---

## Mechanism

### 1. The Light Well — the queue as a section of descending tone

The list is one `LazyColumn`. Every queue row renders on a tonal ground taken
from the ladder in §Depth & light, keyed to its **depth** = its index in
`orderedQueue` (0 = ACTIVE). Depth is a property of the task's position in the
queue, never of scroll position: scrolling does not relight anything, and a
row keeps its tone while it is on screen, off screen, or being recycled.

- **ACTIVE (depth 0)** sits in the shaft: the lightest ground on the screen,
  full row width (the LazyColumn's existing 12dp page inset is kept — the band
  is the row's own background, square corners, no clip, no border, no radius).
  There is no drawn shaft object, no Canvas seam, no gradient. The shaft is
  simply the brightest row, at the top, full width. (WILD §1's 88dp Canvas
  shaft + 1dp seam is rejected: an object. Tone and offset only.)
- **Queue rows (depth ≥ 1)** descend: background steps down, content offset
  steps right, title ink steps down — ladder in §Depth & light. The ladder
  **flattens at depth 3**: depths 3, 4, 5 … share one ground, one offset, one
  ink floor. Past the third tread the eye reads "deep queue" as one recessed
  block with a single aligned text column — a staircase that becomes a
  corridor, which is exactly how a 12-task day should feel.
- **Offset:** row content (title + minutes cell) starts at
  `12dp (page inset) + depthOffset`, depthOffset = 0 / 8 / 16 / 24dp for
  depths 0 / 1 / 2 / ≥3. Hairlines do **not** offset: every row's bottom
  hairline spans the full inset width. The hairlines are the well's fixed
  structure; only the work descends.
- **The foundation line** is the well's floor, drawn once, after the last
  queue row: a **3dp-high, full-inset-width bar**, top edge flush with the
  last row's hairline position (the last queue row draws no hairline of its
  own — the foundation replaces it), 8dp of air below it, then the done zone.
  Day fill: ink `TokenColorPrimary #252323`. Night fill: cap tone
  `NightColorSurfaceSheet #4A423C` (a structural bar, not a control fill; it
  is the heaviest structure tone that stays under the text tiers). No label,
  no count, no word on the line — a count of done tasks would be a second
  rendering of the done zone directly beneath it.
- **Done zone (below the foundation):** today's completed tasks as struck
  one-line rows (§Mechanism 4). The zone's ground is the ladder's floor tone
  (day `surfaceVariant #D8D1CB`, night recess `#100E0C`) applied to the whole
  zone, so "below the foundation" is a place, not a list style.
- **2-task queue:** ACTIVE lit at depth 0, one tread at depth 1, foundation
  directly under it. The well reads as shaft + one step + floor — shallow,
  honest, no ladder theatrics for a short day.
- **12-task queue:** depths 0, 1, 2, then nine rows at the flattened depth-3
  tread: same ground `#D8D1CB` / `#100E0C`, same +24dp offset, title ink held
  at its floor. Height = ACTIVE block (variable) + 11 × 44dp = 484dp of queue
  + foundation + done zone; it scrolls, and because tone is keyed to index,
  the staircase is identical at any scroll offset.
- **Empty queue:** no rows, no foundation line (a floor with nothing standing
  on it and nothing beneath it is furniture). The done zone still renders if
  today has completions — an emptied queue over a full foundation is the
  day's best state and it must be visible.

### 2. ACTIVE block anatomy

One expanded block; the only expanded block. Geometry, all fixed:

- **Rail:** 3dp wide, at x = 0 of the row (inside the 12dp page inset),
  full height of the block, drawn as two stacked boxes inside a
  `Row(Modifier.height(IntrinsicSize.Max))`: track = `outlineVariant` full
  height; fill = ink (`onBackground`), anchored to the **bottom**, height =
  `blockHeight × task.subStepProgressFraction`. Stated by length: at fraction
  0 the fill is 0dp (track only); at 1/3 of a 180dp block the fill is 60dp of
  ink rising from the block's foot; at 1.0 the fill is the full block height.
  The fill never uses orange and never uses width — length is its only voice.
- **Header line** (Anonymous Pro Bold 10.5sp, letterSpacing 1sp): left word
  `ACTIVE` in full ink; right cell `#01 · 25m` — position + minutes, the
  row's only minutes rendering. No step tally (Decision 4).
- **Title:** Atkinson Hyperlegible **Bold (real 700) 20sp / 25sp line
  height**, letterSpacing −0.4sp, full ink, maxLines 2 with ellipsis on the
  second line (a 20sp title that needs a third line is a composer problem,
  not a row problem; the full string remains in `contentDescription`).
  Zeile's Sora SemiBold 20sp is rejected: Sora is the instrument/display face;
  row titles are body type, and Atkinson SemiBold does not exist as a real
  file — hierarchy built on a fake weight is the exact lie Round 0 killed.
- **Sub-steps — inside ACTIVE only.** Each step row: 7dp detent square at
  the left (inside a 14dp indent from the title axis), step title Atkinson
  13.5sp Normal (400 — Atkinson has no Medium; wave-1's "Medium" is corrected
  to the real 400 everywhere in this element), right cell fixed **28dp**
  wide holding the step number / ✓ (§Mechanism 3). Step row minimum height
  **34dp**, full-width tap target. Vertical rhythm: header→title 6dp,
  title→first step 8dp, steps 5dp apart.
- **Action row** (10dp below the last step, or below the title when there
  are no steps): the WordActions `DONE` · `SPLIT` · `START` · `DEL`, in
  that order, `spacedBy(8dp)` with each word carrying its own internal
  padding (h 6dp, v 9dp → ≈37dp touch height). `SPLIT` renders **only when
  `task.subSteps.isEmpty()`** (the code's existing condition — splitting a
  task that already has steps is not an act). `DEL` is pushed to the right
  edge by `Spacer(weight(1f))` so the destructive word never sits under the
  thumb's resting arc next to START. Block bottom padding: actions→hairline
  10dp; the ACTIVE block's own bottom edge is closed by the next row's top —
  no extra rule is drawn under ACTIVE; its band edge is the closure.

#### The WordAction system (one composable, all verbs)

A single private `WordAction(word, restingInk, pressedInk, sound, onClick)`
in the same file, used for DONE / SPLIT / START / DEL — and for nothing else
on this screen. Identical mechanics for every verb; identity comes from
resting colour + sound only:

- **Press state:** `MutableInteractionSource` + `collectIsPressedAsState()`;
  `pressT = animateFloatAsState(if (pressed) 1f else 0f, specC(180) in /
  specA(260) out)`.
- **Travel:** `graphicsLayer { scaleX = scaleY = 1f − 0.06f × pressT`
  (floor **0.94**); `translationY = 1dp × pressT }` — the word sinks 1dp as
  it shrinks: a keycap going down.
- **Inking:** text colour = `lerp(restingInk, pressedInk, pressT)`. Muted
  verbs (SPLIT, DEL) rest at the muted tier and press **toward full ink** —
  touching a word prints it darker. DONE rests at full ink and presses
  toward the deeper ink (day `TokenColorPrimary #252323`; night cream
  `#FFE4CF`). START rests at the orange and presses one step within the hue
  (day `#F55314 → TokenAlert #D63E06`; night `#F47442 → NightAlert #FF8A5C`).
- **Drawn underline:** 1.5dp line under the word via `drawWithContent`,
  width fraction = `pressT`, colour = the word's current lerped colour. At
  rest its width is 0. START's underline is therefore orange only while
  START itself is pressed — it is part of the one orange object, not a
  second spend.
- **No ripple, ever:** `clickable(indication = null)`. (Theme already purges
  ripple app-wide via `LocalIndication`; the local `null` is belt-and-braces
  so the row never depends on a theme detail.)
- **Sound on press-DOWN** (`LaunchedEffect(pressed)`), action on the normal
  click-up. A machine answers the touch, not the release. Per-verb
  assignment in §Sound.
- **START's staged commit:** on click-up, `onCueTaskToFocus(task.id)` fires
  immediately; `onNavigateToDeck()` is delayed **180ms** so the deepened
  orange + full underline + seal sound land visibly before the Deck arrives.
  START on the ACTIVE task is an arming path: tap → 180ms → Deck with the
  task loaded, slide at rest. START never starts a timer and never seals —
  the Deck slide is the sole arming gesture, so Tasks can never grow a
  cheaper second arm path. While `isSessionActive`, START's word becomes
  `RESUME` — same orange, same choreography, honest label, navigation only.

### 3. Sub-steps — drawn strike, hit-stop, rolling number, detent square

Four beats, one tap, all inside **~440ms** (middle step) / **~490ms**
(final step):

1. **DOWN (frame 0):** sound + bundled haptic fire (§Sound, §Haptics). The
   state flag flips at tap (`onToggleSubStep`) — rendering chases the flag,
   never the reverse.
2. **Hit-stop (60ms; 110ms if this tap completes the set):** the step row
   freezes completely — strike fraction, square fill, text colour, settle
   offset all hold at their pre-tap values. Row-local only: scroll, other
   rows, and the rail are untouched. The freeze is the impact; nothing
   animates inside it.
3. **Strike + stamp (260ms):** the strike draws via `drawWithContent` —
   a line at **54% of the text height**, **2dp day / 1.5dp night**, from
   x = 0 to `width × strikeFraction`, colour = the step's current text
   colour at 85% alpha; `strikeFraction = animateFloatAsState(if (isDone)
   1f else 0f, specA(260))`. Concurrently the **7dp detent square** fills:
   `animateColorAsState(outline → ink, specA(180))`, and the step text
   cross-fades to its muted tier (`animateColorAsState`, specA 260). The
   rail's fill rises to the new fraction on specB(420) — it starts with the
   strike and settles after it, the slowest thing in the beat, like a level
   finding itself.
4. **Settle (overlapping the strike's tail):** the row drops **1.5dp**
   (`Animatable`: `animateTo(1.5f, specC(180))` then `animateTo(0f,
   specA(260))`, applied as `translationY`) — a key bottoming out and
   seating. Un-checking runs no settle and no stop: strike retracts
   right-to-left on the same specA(260) in reverse, square unfills specA
   180, text colour returns. A correction is never charged the celebration
   price.

- **The right-hand number rolls to ✓:** the fixed 28dp cell runs
  `AnimatedContent(targetState = isDone)`: in = `slideInVertically(specC
  (280)) { it } + fadeIn(specA(180))`, out = `slideOutVertically(specC
  (280)) { −it } + fadeOut(specA(180))` — the number rolls up and out, the
  ✓ rolls in, a counter wheel advancing one detent. Numbers are formatted
  `String.format("%02d", i + 1)` (the current `"0${i+1}"` renders step 10
  as `010` — fixed here). ✓ is set in Anonymous Pro at 10.5sp, full ink,
  never orange, never green. Un-check rolls in reverse (✓ exits downward,
  number enters from below) — `AnimatedContent` direction follows the
  target state.
- **Final step (this tap makes `completedSubStepsCount == subSteps.size`):**
  hit-stop extends to **110ms**; the strike line draws at **1.25× weight**
  (2.5dp day / 1.9dp night); sound escalates (§Sound). The parent title is
  **not** struck. Instead, on the strike's completion frame, the hairline
  under the ACTIVE title draws in left→right over specA(260) — a bracket
  closing over the finished steps — and the ACTIVE band recesses one step
  (day `#FAF6F0 → #E4DFDA`; night `#362C27 → #171412`, `animateColorAsState`
  specB(420)): a fully-stepped task sits *set into* the panel, awaiting its
  DONE. Steps-done and task-done remain two visibly different facts.
- **Rapid taps (buffering):** a second step tap landing during a strike is
  buffered and fires its own sequence the frame the first settles, with its
  hit-stop halved to **30ms**; a third queued tap coalesces — its flag
  flips, its strike runs plain specA(180) with no stop and no settle.
  Input is queued, never dropped; the hand doing honest fast work feels a
  run of cuts, not a queue of animations.

### 4. DONE — the three-beat seal, the landing, the done zone

DONE is a local `sealing` state inside `TaskRow`; the completion callback
is deliberately delayed. Every callback is id-based and the ViewModel
tolerates the lag (flagged in §Code).

- **Beat 1 — Seal (0–260ms):** on DONE press-down, `playCompletionPulse()`
  fires (§Sound) and `sealing = true`. The **title's drawn strike** draws
  across the 20sp title on specA(260) (same `drawWithContent` technique as
  the steps, 2.5dp line at 54% of title height). The **rail fills to 1.0**
  on specB(420) — at task level the fill-to-full is true: the task is
  complete even though individual steps keep their honest states; step
  strikes do **not** redraw. The action row fades to 0 alpha on specA(180)
  — nothing can be double-tapped mid-seal. Title colour cross-fades toward
  the muted tier on specA(260).
- **Beat 2 — Hold (260–440ms, 180ms):** the fully struck, full-railed row
  sits complete and legible for one beat. Finishing deserves a readable
  instant, not a flicker.
- **Beat 3 — Exit + landing (440ms → ~860ms):** at 440ms
  `onCompleteTask(task.id)` fires. The item leaves `orderedQueue`;
  `animateItem` fadeOut specA(180) removes it while the survivors'
  **placement glide specB(420)** advances the belt: the next row glides
  into the ACTIVE slot and transforms (§State machine — promotion) as one
  continuous motion, not two events. At **620ms** (fadeOut complete) the
  **landing haptic** fires, scaled by the completed task's minutes
  (§Haptics), and the task's done-zone row is present below the foundation
  line — count, zone, and haptic agree on the same frame; the screen never
  holds two versions of "done" at once.
- **Where the done task lands:** below the foundation line, as a **44dp
  one-line struck row** on the done-zone ground: title Atkinson 15sp
  Normal, drawn strike at rest fraction 1.0, ink at the floor tier (day
  secondary `#615B5A`; night `#776A60`, the 3.5:1 tier — quiet, never
  unreadable); right cell: the task's minutes in Anonymous Pro 11sp at the
  same floor tier (`25m` — its size is the last true thing said about it).
  No verbs, no tap target: there is no un-complete callback in the screen's
  API, so the done row honestly offers nothing. Recovery from a mistaken
  DONE is re-adding the task in the composer (which takes the next QUEUED
  number, §Mechanism 6).
- **How long it stays:** the zone shows **today's completions only**, all
  of them, for the rest of the day — they are the foundation the day stands
  on, and capping the count would hide finished work on exactly the big
  days that matter. Source: the screen's existing `completedTasks`
  parameter, filtered to `completedAtMillis` on the current calendar day
  (a no-op-safe filter if upstream already scopes it). At day rollover the
  zone empties with the data.
- **Ledger dependency — flagged, not specced:** these DONE records
  (`completedTasks`, `completedAtMillis`, `completedInSessionId`) are the
  same records that would feed session history if the Ledger is kept. My
  done zone renders **today only, spatially, as the Well's floor**. If the
  Ledger verdict keeps the tab, its scope is the archive (all days); the
  two scopes never render the same fact-set on the same day-view, and
  nothing in this element changes either way. If the Ledger dies, the done
  zone + the session-end receipt are the only renders of completion, and
  that is sufficient. The verdict does not block this spec.

### 5. All other rows — 44dp one-liners

Every non-ACTIVE row is exactly **44dp** tall, one line, no exceptions:

- **What fits:** [depth offset] title — Atkinson 15sp Normal, single line —
  then the right cell: `#02 · 25m` in Anonymous Pro 11sp (position +
  minutes, muted by depth per §Depth & light). The title takes all width
  remaining after the minutes cell and a 12dp gap.
- **Truncation rule:** `maxLines = 1`, `TextOverflow.Ellipsis`, hard
  ellipsis at the container edge. No fade edge (a fade is a gradient object
  doing a type job), no second line, no marquee. The full title lives in
  the row's `contentDescription` and becomes fully visible the moment the
  row is promoted into ACTIVE (where 2 lines at 20sp are allowed).
- **No sub-steps** (they exist only inside ACTIVE — a queued task's steps
  are not shown, not counted, not rendered in any form; a "▸ 3 steps"
  badge would be a second rendering of data whose only home is the ACTIVE
  block).
- **No verbs.** The row's one act is its own: **the whole 44dp row is the
  promotion target.** Tap → the task becomes ACTIVE (§State machine). What
  replaces CUE's job: nothing verbal — the gesture *is* the verb, and a
  one-handed tired user promotes by hitting a 44dp full-width bar, not a
  12sp word. Press feel (press-DOWN, via interaction source): title ink
  lerps toward full ink on specC(180) and the row sinks 1dp — the
  WordAction physical language at row scale, minus the scale (scaling a
  full row would move its hairline). Sound on press-DOWN: the promotion
  latch (§Sound). The tap does **not** navigate to Deck (Decision 8).
- **Hairline:** 1dp `outlineVariant` under every queue row, full inset
  width, except under the last queue row, where the foundation line
  (§Mechanism 1) takes its place.

### 6. Header numeral — the horizon bar's heir (shared with Nadia)

Nadia specs the bar's removal (`TaskHorizonBarView` + its `AndroidView`
host in SECTION 1 die). What stands in its place, in the same SECTION 1
slot at the head of the list region:

- **Block:** fixed height **68dp** (16dp section spacer + 14dp label row +
  44dp numeral line), so the queue below never shifts when the total
  changes digit count. The block is the header of the whole queue region —
  composer included — because the total it prints includes the ACTIVE task
  and every queued task; the SECTION 3 header below the composer is
  simultaneously slimmed to the single word `QUEUE` with an **empty right
  side**: its current `DONE n` count dies here (the done zone below the
  foundation is completion's only rendering on this screen).
- **Label row:** left `TASKS`, Atkinson Bold 10sp, letterSpacing 0.6sp,
  secondary tier; right, the count in the same tier: `3 QUEUED` — or
  `NOTHING QUEUED` when empty. The count and Alba's stamp share one
  numbering law (§below) and can never disagree.
- **The numeral:** digits Anonymous Pro **Bold (real 700) 44sp**, line
  height 44sp, letterSpacing 0, left-aligned; unit letters Atkinson Bold
  16sp on the same baseline, 4dp after their digit group, groups separated
  by 6dp. Ink only — day `#252323` on `#E4DFDA`; night `#D2C6BB` (11:1
  tier) on `#171412`. No orange (a total is not a verb), never green, never
  a touch target (it owns no action; the rows are the editor). Anonymous
  Pro, not Sora (counter to Nadia's removal-spec draft, answered by mail):
  mono's fixed advance means a total changing digit count moves nothing
  else on the screen, and every other number this screen prints (`#N`,
  minutes, step numerals) is already Anonymous Pro — one numeral family
  per screen. Sora stays with the Deck's instruments, where it lives.
- **Format — the truthful unit is the split duration:** the value is
  `computedTotalMins` (raw Σ `estimatedMinutes` — the bar's
  `coerceAtLeast(10)` denominator dies with the bar, so numeral = Σ rows,
  checkable by eye). Under 60: `45M`. At or over 60: `1H 35M`; remainder
  zero: `2H`, never `2H 0M`. Raw minutes above 59 are machine language;
  a finish-by clock time (`done by 14:20`) is a forecast that silently
  assumes start-now and no interruptions — the numeral prints the sum,
  never the promise.
- **Numbering law (shared with Alba):** a task's number is its **position**:
  `#N` = index + 1 in `orderedQueue`, zero-padded `%02d`. Alba's composer
  stamp `QUEUED · #N` prints `orderedQueue.size + 1` at submit — the
  position the new row will occupy — and rows renumber by position after
  every mutation, so the stamp's promise and the settled row's `#N` agree
  on the first frame after insertion. One law, two printers, no drift.
- **Empty queue:** numeral `0` + `M`, both at the floor tier (day
  `onSurfaceVariant #5F5856`; night `#776A60`), right label `NOTHING
  QUEUED`, block height unchanged at 68dp — the screen does not jump when
  the first task lands.
- **Spoken form:** `contentDescription` = *"Queue total: 1 hour 35
  minutes, 3 tasks queued."* / *"Queue total: nothing queued."* — built
  from the same sum and size the block draws.

---

## Motion (curve + ms)

All motion uses MonolithCurves specA (CurveA 0.2,0,0,1) / specB (CurveB
0.05,0.7,0.1,1) / specC (CurveC 0.3,0,0.8,0.15) and durations from
{180, 260, 280, 420, 520} only. No springs, no new specs.

| Event | Property | Spec | Ms |
|---|---|---|---|
| WordAction press in | pressT (scale 0.94, sink 1dp, ink lerp, underline) | specC | 180 |
| WordAction release | pressT → 0 | specA | 260 |
| START staged navigation | delay before `onNavigateToDeck()` | — (delay) | 180 |
| Sub-step hit-stop (middle / final) | full row freeze | none (timeScale 0, row-local) | 60 / 110 |
| Buffered 2nd tap hit-stop | full row freeze | none | 30 |
| Drawn strike (step or DONE title) | strikeFraction 0→1 (reverse on un-check) | specA | 260 |
| Detent square fill / unfill | colour outline ↔ ink | specA | 180 |
| Step text colour to muted / back | colour | specA | 260 |
| Step settle | translationY 0 → 1.5dp | specC | 180 |
| Step settle return | translationY 1.5dp → 0 | specA | 260 |
| Number ↔ ✓ roll | AnimatedContent slide + fade | specC slide / specA fade | 280 / 180 |
| Rail fill change | fillFraction → new fraction | specB | 420 |
| Final-step bracket hairline | draw-in left→right under ACTIVE title | specA | 260 |
| Final-step band recess | band colour one step down | specB | 420 |
| DONE Beat 1 | title strike specA 260; rail → 1.0 specB 420; actions fade specA 180 | mixed | 260 window |
| DONE Beat 2 | hold, fully legible | — | 180 (260→440) |
| DONE Beat 3 | `onCompleteTask` at 440; fadeOut specA 180; survivors' placement glide | specB | 420 |
| Landing haptic | fires at fadeOut end | — | t = 620 |
| Row arrival (composer ADD) | rise 10dp → 0 + alpha 0 → 1; its hairline draws L→R; seating detent fires on the landing frame (§Sound) | specB | 420 |
| Promotion / demotion | title size 15 ↔ 20sp; band colour; header word + `#N` roll | specB (size/band) / specC+specA (word roll) | 420 / 280+180 |
| Queue row press | ink lerp + 1dp sink (in / out) | specC in / specA out | 180 / 260 |
| Header numeral change | instant glyph swap + alpha 0.88 → 1.0 letterpress settle | specA | 180 |
| All list mutations | `Modifier.animateItem(fadeIn specA 260, placement specB 420, fadeOut specA 180)` on every item | — | as listed |

**Promotion transformation detail:** when a row becomes ACTIVE, its title
size animates 15f → 20f via `animateFloatAsState(specB(420))` (line height
= size × 1.25 each frame), its band colour animates transparent → shaft
tone on the same specB(420), its header word swaps QUEUE → ACTIVE through
the same AnimatedContent roll as the step numbers, its `#N` rolls to
`#01`, and `FontWeight` swaps Normal → Bold at the midpoint (titleSp ≥
17.5) where the swap is invisible under motion. Demotion is the exact
reverse. After a tap-promotion, the screen scrolls the ACTIVE row to the
top (`animateScrollToItem`, §Code). Completion-driven promotion needs no
scroll — the row is already at the top.

**Header numeral change detail:** the total is a *computed* number — it
may never roll, flip, or count through intermediate glyphs (95 → 120 never
passed through 96…119; drawing them is a lie in motion). On change: glyphs
swap instantly and re-strike at 88% ink alpha, settling to 100% over
specA(180). A second change inside the window restarts the settle from the
current alpha with the newest glyphs. No haptic, no sound — the causing
control (ADD, DONE, DEL) already fired its feel; a buzz from the numeral
would double-fire the event (one-owner rule, §Sound).

**Reduced motion:** if the system animator scale is 0, hit-stops are 0ms,
strikes render at fraction 1 instantly, placement glide is disabled
(animateItem placementSpec = null), the seal collapses to Beat 1 at 0ms +
immediate callback. State truth is identical; only the theatre leaves.

---

## Haptics (per beat)

Every haptic below is **bundled inside its engine call** (the engine couples
waveform + transient in one call — that coupling is the house law). Rows
never call `triggerOneShotHaptic` directly except for the single landing
beat, which is a separate timed event with its own named owner. No
ViewModel haptics anywhere in this element.

| Beat | Call that carries it | Waveform (verified in engine) | Class |
|---|---|---|---|
| Sub-step check / un-check (DOWN) | `playRotaryDetent(…)` | [0, 7, 5]ms @ [0, 180, 70] | 1L pitched |
| Final sub-step (DOWN) | `playFirmClick()` | [0, 13, 20, 8]ms @ [0, 225, 0, 135] | 8L |
| WordAction SPLIT (DOWN) | `playRotaryDetent(680)` | as detent | 1L |
| Promotion / row tap (DOWN) | `playMechanicalLatch(true)` | [0, 11, 26, 9]ms @ [0, 215, 0, 150] | 4L |
| WordAction DEL (DOWN) | `playMechanicalLatch(false)` | [0, 9, 22, 10]ms @ [0, 165, 0, 195] | 4L |
| WordAction DONE (DOWN) | `playCompletionPulse()` | [0, 10, 24, 10, 24, 14]ms @ [0, 175, 0, 125, 0, 80] | 10L |
| WordAction START (DOWN) | `playHarmonicSeal(true)` | [0, 24, 38, 16]ms @ [0, 255, 110, 175] | seal (heaviest) |
| DONE landing (t = 620ms) | `triggerOneShotHaptic(grade)` — owner: screen scope (§Sound) | one-shot, graded below | ≤10L + echo |

**Landing grade law (scaled by the task's own `estimatedMinutes` — the
mass is its printed number, checkable):**

- **LIGHT** — minutes < 30: request **4L** once.
- **STANDARD** — 30 ≤ minutes < 60: request **8L** once.
- **HEAVY** — minutes ≥ 60: request **10L**, then a second request **4L**
  at +30ms (body, then settle — heavy things land twice).

**Ceiling:** no landing ever exceeds the 10L completion class plus one 4L
settle echo; the grades are thresholds on the printed minutes, not a
continuous formula, so the thumb learns three masses, not a dial.
(`triggerOneShotHaptic` quantises requests to its {1, 4, 8, 10} buckets —
the grades are written directly in those buckets, so nothing is lost in
quantisation.) Nothing at DONE press beyond the pulse, nothing during the
exit glide — the landing knock *is* the landing, in both senses.

**Hit-stop haptics:** none inside the stop. The detent/firm-click haptic
fires on DOWN with its sound (splitting them would break the engine's
±frame coupling); the stop is visual only.

---

## Sound (primitive + pitch)

**One-owner rule, stated for every sound in this element:** the **view**
owns all row sound — `TaskRow` fires through the screen's existing
`soundEngine: TactileSoundEngine?` parameter, on **press-DOWN**, via
`LaunchedEffect(pressed)`. The **ViewModel fires nothing** for any row
event (killing the double-haptic failure mode at the root). One exception
of timing, not of layer: the landing haptic at t = 620ms is fired by the
**screen-level composable scope** (`FocusSequencerScreen`'s
`rememberCoroutineScope`), because `TaskRow` leaves composition at the
440ms callback and cannot fire its own landing. It is haptic-only; there
is no landing sound. The composer's latch (Alba's element) is untouched
and remains the composer's own owner.

| Event | Primitive | Pitch / params | Fired by |
|---|---|---|---|
| Sub-step check, step n (1-based) | `playRotaryDetent(freqHz)` | STEP SET: 01 → 329.63Hz, 02 → 392.00Hz, 03 → 493.88Hz, 04 → 587.33Hz, 05+ → 659.25Hz | TaskRow (view), DOWN |
| Sub-step un-check | `playRotaryDetent(freqHz)` | The step's **own STEP SET pitch** — same note taken back (Alba's ruling as pitch-family owner; wave-1's fixed 420Hz retract register is dropped). The detent has no damped variant in the current engine, so check vs un-check are differentiated visually — un-check has no hit-stop, no settle (§Mechanism 3) — never by inventing a second pitch | TaskRow, DOWN |
| Row arrival seating (composer ADD) | `playRotaryDetent(freqHz)` | CAP SET pitch of the filed duration (Alba's transposed set, above the detent's 280Hz clamp): 15m → 1046.50Hz, 25m → 880.00Hz, 45m → 698.46Hz, 90m → 587.33Hz. Fires once, on the entrance's landing frame (t = 420ms), never at animation start — the sound is caused by the landing | TaskRow entrance (view) |
| Final sub-step check | `playFirmClick()` **instead of** the detent | 620Hz fixed, chordRatio 1.1 — the carriage returning; the set is complete | TaskRow, DOWN |
| SPLIT press | `playRotaryDetent()` | 680Hz default | TaskRow (WordAction), DOWN |
| Promotion (queue row tap) | `playMechanicalLatch(true)` | 760Hz, chordRatio 1.2 | Queue row (view), DOWN |
| DEL press | `playMechanicalLatch(false)` | 520Hz, chordRatio 0.9 — heavier seat, lower pitch; deletion sounds heavier than promotion | TaskRow (WordAction), DOWN |
| DONE press | `playCompletionPulse()` | 185Hz, chordRatio 1.33 — three softening ticks, winding down | TaskRow (WordAction), DOWN, at seal Beat 1 start |
| START press | `playHarmonicSeal(true)` | 360Hz, chordRatio 1.5 — the seal clunk, reserved for arming; no lesser call may substitute | TaskRow (WordAction), DOWN |
| Header numeral change | — | **silent** (readout, not a control; the causing control already sounded) | — |
| Landing | — | **silent** (haptic only, §Haptics) | — |

**Pitch-set ownership:** the STEP SET values are a fixed set — identities
of the printed ordinals, never a formula (a pitch computed from position
would pretend to measure something). The family is Alba's (her literals
match this table exactly); this spec owns only the trigger (press-DOWN),
the substitution rule (final step → firm click), and the bundled haptics.
Mapping the set onto `playRotaryDetent(freqHz)` keeps every pitch inside
the engine's 280–1480Hz clamp with no engine change. Steps beyond 05 reuse
659.25Hz — the set is the set; no sixth pitch is invented. Agreed with
Alba by mail (threads: numbering-and-step-pitch, queued-words-and-seating).

**Engine-addition dependency (Alba's `playStepStrum`, named, decided):**
Alba's composer file proposes an engine addition, `playStepStrum(stepCount)`,
to voice the final sub-step. That method does not exist in the current
engine, and this element's envelope is the existing five calls — so the
default in this spec stands: **final step = `playFirmClick()` on
press-DOWN.** If Voss/Ren approve Alba's engine addition, the substitution
is exactly 1:1 and total: `playStepStrum` replaces the firm click at the
final step (firing on the toggle's commit, per her spec), the hit-stop
extends and the bracket close are unchanged, and **DONE after a completed
set never re-fires the strum** — `playCompletionPulse()` remains DONE's
only voice. No other beat in this file changes under either outcome.

**Sound-off behaviour:** with `soundEnabled = false` / system silent,
every visual beat still runs in full (strike, stop, settle, seal, bracket)
— muting removes air, never information. Haptics remain governed by
`hapticsEnabled` as the engine already does.

---

## Depth & light

The Well is tone + offset only. No new objects, no shadows, no elevation,
no gradients. All values are existing tokens; where a tier does not exist,
an alpha copy of the tier above is used (the wave-1 night catch, carried
forward: never invent a hex to fill a ladder rung).

### Day ladder (ground `#E4DFDA`)

| Depth | Row ground | Content offset | Title ink | Meta / minutes ink |
|---|---|---|---|---|
| 0 ACTIVE | `surfaceBright #FAF6F0` (the shaft) | +0dp | `#2C2A29` @ 100% | `#2C2A29` header word; `#615B5A` right cell |
| 1 | `#E4DFDA` (page ground — transparent) | +8dp | `#2C2A29` @ 0.82 alpha | `#615B5A` @ 100% |
| 2 | `surfaceVariant #D8D1CB` | +16dp | `#615B5A` (secondary, solid) | `#615B5A` @ 0.75 alpha |
| ≥3 (flat) | `surfaceVariant #D8D1CB` | +24dp (cap) | `#615B5A` (held — title floor) | `#615B5A` @ 0.58 alpha |

### Night ladder (ground `#171412`, steps ≥ 1.35:1, text tiers 11 / 5.8 / 3.5)

| Depth | Row ground | Content offset | Title ink | Meta / minutes ink |
|---|---|---|---|---|
| 0 ACTIVE | `NightColorSurfaceBright #362C27` (staircase step 1) | +0dp | `#D2C6BB` (11:1 tier) @ 100% | `#9C8E84` (5.8 tier) |
| 1 | `#171412` (ground) | +8dp | `#D2C6BB` @ 0.82 alpha (alpha copy — no tier exists between 11 and 5.8) | `#9C8E84` @ 0.75 alpha |
| 2 | recess `NightColorSurfaceVariant #100E0C` (below ground) | +16dp | `#9C8E84` (5.8 tier, solid) | `#776A60` (3.5 tier) |
| ≥3 (flat) | recess `#100E0C` | +24dp (cap) | `#9C8E84` (held — a queue title never drops below the 5.8 tier) | `#776A60` (held) |

- **Flattening law:** backgrounds flatten at depth 2→3 (no darker honest
  ground tone exists — the next step down would be the session block bar,
  which is not a row tone), offsets cap at +24dp, title ink floors at the
  secondary tier in both modes. Only meta ink takes the final step to the
  floor tier, and only by day-alpha / night-tier. A 12-deep queue and a
  4-deep queue have identical first four treads.
- **Direction is mode-stable:** lightest at the head, descending with
  depth, in both modes. (The old horizon bar inverted its staircase at
  night; the Well's ladder is written per-mode above precisely so that
  cannot happen here.)
- **ACTIVE band edges:** the band's top and bottom are closed by hairlines
  at `outline` strength (day `#615B5A`, night `#807A7A`) instead of
  `outlineVariant` — the one place a stronger rule is earned, because it
  is a material edge (the shaft's lip), not a separator.
- **Done zone:** ground = ladder floor (day `#D8D1CB`, night `#100E0C`),
  foundation line per §Mechanism 1, done-row ink at the floor tier (day
  `#615B5A` solid; night `#776A60`). Done rows take **no offset** — below
  the foundation there is no descent left; they rest at the page inset.
- **Light carries no data (Mara's test):** delete every tone and offset
  and the queue still reads perfectly — order, `#N`, minutes, rail, and
  strikes carry every fact. Tone is the room, not a rendering; it never
  encodes progress, readiness, or armed-ness, and it is identical in
  grayscale logic in both modes.

---

## State machine

**Row states** (per `task.id`, state held inside `TaskRow` via
`remember(task.id)` unless noted):

```
QUEUED(d) ──tap──▶ PROMOTING ──420ms──▶ ACTIVE
ACTIVE ──another row promoted──▶ DEMOTING ──420ms──▶ QUEUED(1)
QUEUED(d) / ACTIVE ──composer arrival──▶ ENTERING ──420ms──▶ QUEUED(d) / ACTIVE
ACTIVE ──DONE press──▶ SEALING ──440ms──▶ EXITING ──fadeOut 180ms──▶ DONE-TODAY
ACTIVE ──DEL press──▶ DELETING ──180ms──▶ (removed; undo banner is composer's territory)
any row ──isSessionActive──▶ FROZEN (overlay state, see below)
```

- **QUEUED(d):** 44dp one-liner at ladder depth d. Tap target live
  (promotion). No other interaction.
- **PROMOTING:** placement glide specB(420) carries the row to index 0
  while the transformation of §Motion runs; the previous ACTIVE is in
  DEMOTING simultaneously. Input on both rows is inert during the 420ms —
  a second promotion tap inside the window is ignored, not queued (queue
  surgery mid-glide is how lists tear).
- **ACTIVE:** full block; sub-steps, WordActions live.
- **SEALING:** entered on DONE press-down; actions at 0 alpha and inert;
  sub-step taps inert; DEL inert. Exits only forward, at 440ms, into
  EXITING. No cancellation — DONE has no undo on this screen (§Mechanism 4).
- **EXITING:** row is leaving composition (fadeOut); survivors gliding.
  Landing haptic scheduled by the screen scope at press + 620ms regardless
  of this row's composition lifetime.
- **DONE-TODAY:** terminal for the day; rendered below the foundation from
  `completedTasks`, not from `orderedQueue`. No transitions out (same day).
- **DELETING:** DEL press → content alpha → 0 + height shrink
  (`AnimatedVisibility` exit: `shrinkVertically(specA(180)) +
  fadeOut(specA(180))`), `onDeleteTask` at 180ms; survivors glide specB
  (420). Colder and faster than completion by design — deletion borrows
  none of DONE's ceremony. The existing undo banner (SECTION 2) is the
  recovery path and is untouched.
- **ENTERING:** once per `task.id`, first composition only: rise 10dp +
  fade on specB(420), hairline draws left→right on the same progress.
  Scroll recycling never replays it (stable `key = task.id` in
  `itemsIndexed`).

**Sub-step states** (per step, inside ACTIVE):

```
UNDONE ──tap──▶ STOP(60ms) ──▶ STRIKING(260ms) ──▶ SETTLING ──▶ DONE-STEP
UNDONE ──tap (final step)──▶ STOP(110ms) ──▶ STRIKING ──▶ SETTLING ──▶ DONE-STEP + BRACKET-CLOSED
DONE-STEP ──tap──▶ RETRACTING(260ms) ──▶ UNDONE        (no stop, no settle)
STRIKING ──another step tap──▶ buffered (30ms stop) ; 3rd tap ──▶ coalesced (plain 180ms strike)
```

BRACKET-CLOSED is a property of the ACTIVE block (all steps done, task not
done): bracket hairline drawn, band recessed one step. It exits on any
un-check (bracket retracts specA(260), band returns specB(420)) or on DONE
(the seal supersedes it).

**FROZEN (session overlay state):** while `isSessionActive` is true, HOLD
is absolute on this screen: DONE / SPLIT / DEL are **absent** (not greyed —
absent), sub-step rows render exactly as they stood at arm with tap
targets removed, queue-row promotion taps are inert, and START reads
`RESUME` (navigation only). `effectiveNight` already forces the night
ladder during a session; FROZEN changes no tones, only agency. Nothing on
this screen may be edited, completed, or re-cued mid-session.

**Header numeral states:** `EMPTY` (0M, floor tier, `NOTHING QUEUED`) /
`TOTAL` (split-duration format) — transitions are the §Motion re-strike
only. The numeral has no interactive states.

---

## Edge cases

- **Zero tasks:** no ACTIVE, no queue, no foundation line. Numeral EMPTY
  state; done zone renders if today has completions; composer unchanged.
  START exists nowhere — the screen's orange count is zero, correctly.
- **One task:** ACTIVE only; foundation line directly under the ACTIVE
  block; promotion impossible (no queue rows); DONE leaves an empty queue
  over a populated foundation.
- **Step-less ACTIVE task:** rail = track only (fraction 0f, Decision 5);
  SPLIT is offered; the final-step machinery never engages; DONE seal's
  rail-to-full still runs (0 → 1.0 on specB(420)).
- **Task with 10+ sub-steps:** numbers format `%02d` → `10`, `11`…; the
  28dp cell holds two digits at 10.5sp without layout shift; step pitches
  cap at the 05 value (659.25Hz) from step 05 onward.
- **All steps done, DONE never pressed:** BRACKET-CLOSED persists
  indefinitely. No auto-completion, no nudge, no pulse on START — the task
  waits for its deliberate seal for as many days as he leaves it.
- **DONE pressed mid-strike (a step animation running):** the seal
  supersedes — step animations are abandoned at their current fractions,
  the title strike + rail take over. The step flags in data are whatever
  the last completed toggle wrote; rendering never rewrites them.
- **Double DONE / DONE-then-DEL:** `sealing` guards everything — first
  press wins, the action row is at 0 alpha and inert from Beat 1, the
  callback fires exactly once at 440ms.
- **Promotion tap during another row's SEALING:** allowed. The sealing row
  exits at its 440ms; the tapped row's PROMOTING begins on the placement
  glide that follows — `animateItem` serialises the two; the transformation
  targets index 0 as it settles, never a moving slot.
- **DEL on the ACTIVE task:** DELETING runs; the next queue row promotes
  on the survivors' glide with the full 420ms transformation — deletion of
  the head is also a promotion event, and it reads as one belt advance.
- **Very long titles:** ACTIVE wraps to 2 lines then ellipsises (rail and
  band grow with the block — fill length is fraction × *actual* block
  height, so a tall block's rail is proportionally longer; that is the
  length law working, not a bug). Queue rows ellipsize on one line.
- **Extreme minutes:** repository clamps estimates to 5–180, so landing
  grades span LIGHT (5–25m typical) through HEAVY (60–180m); the header
  total's realistic ceiling (~12 × 90m = `18H`) fits the 44sp line without
  collision with the right-aligned count label.
- **Session starts while sealing:** FROZEN overlays at session start; a
  seal already in flight completes its 440ms (it was a rested decision,
  executed) — no new seal can begin.
- **Theme flip mid-animation (day ↔ night, or session forcing night):**
  all `animateColorAsState` targets re-resolve against the new scheme;
  ladder *depths* are unchanged, only their per-mode values swap. No
  animation restarts from zero.

---

## Code: helps / fights

**Helps (verified in the current tree):**

- `orderedQueue` + `computedTotalMins` already exist in the screen; stable
  `itemsIndexed(orderedQueue, key = { task.id })` keys are already present
  — `animateItem` and per-id `remember` state work with zero data changes.
- `subStepProgressFraction`, `completedSubStepsCount`, `subSteps[i].isDone`
  already computed on `FocusTaskEntity`. The rail costs no data work.
- `Modifier.animateItem` (Foundation 1.7, BOM 2024.09) is available and
  used nowhere — this element is its first user, exactly as wave-1 found.
- `AnimatedContent`, `animateFloatAsState`, `animateColorAsState`,
  `Animatable`, `drawWithContent` — all on the existing classpath.
- `soundEngine: TactileSoundEngine?` is already a parameter of this
  screen; every sound in §Sound is an existing call at an existing site.
- Real font weights now ship (Atkinson 400/700, Anonymous Pro 400/700) —
  every weight in this spec is a real file.
- Ripple is already purged app-wide in `Theme.kt`; WordAction's local
  `indication = null` is consistent, not novel.
- `completedTasks` is already a screen parameter; the done zone is a
  filter + a rendering, not a query.
- `completedAtMillis` exists on the entity — the today-filter and the
  Ledger dependency both read a real field.

**Fights (named, with the cost):**

- **No `LazyListState` exists** in the screen today. Promotion scroll
  (`animateScrollToItem`) needs `rememberLazyListState()` added plus the
  first-row index math (3 fixed items precede SECTION 3 — horizon block,
  composer, QUEUE header — plus 1 when the undo banner is present). Small,
  but it is the spec's only new screen-level state.
- **The delayed DONE callback (440ms)** is a behavioural timing change:
  any test asserting synchronous completion after a DONE click must
  advance its clock. Documented here so it is a decision, not a surprise.
  Fallback if rejected in review: fire `onCompleteTask` immediately and
  let fadeOut + placement carry the exit alone — Beats 1–2 are lost, the
  landing grade and glide survive.
- **`onCueTaskToFocus` and `onNavigateToDeck` are coupled** in one
  `clickable` today (the CUE/START text). They must split: promotion calls
  the first only; START calls the first immediately + the second at
  +180ms.
- **No un-complete callback exists.** DONE is irreversible on this screen.
  This spec states that honestly (§Mechanism 4) rather than inventing an
  API; adding `onUncompleteTask` is a ViewModel decision above this
  element.
- **The landing haptic outlives its row.** `TaskRow`'s own
  `LaunchedEffect(sealing)` dies at the 440ms removal; the 620ms landing
  must be scheduled from the screen scope at press time. One extra
  coroutine at screen level — the price of the one-owner split in §Sound.
- **Title-size animation re-lays-out one row for 420ms** (fontSize as a
  state float). One row, once per promotion — cheap, but Robolectric
  frame-0 renders are unaffected only because rest states match today's
  sizes (15sp / 20sp). Any screenshot diff outside the ACTIVE row signals
  a bug, not the spec.
- **Legacy type alias:** the screen still writes `PlusJakartaSansFamily`,
  which now aliases Atkinson. New code in this element should reference
  `AtkinsonHyperlegibleFamily` directly so the next reader isn't lied to
  by a name.
- **Dependency on Nadia's removal:** the header numeral (§Mechanism 6)
  occupies SECTION 1's slot; it must land in the same change as the
  horizon bar's deletion or the screen briefly carries both. Sequenced
  with Nadia by mail (thread: header-numeral-succession). Her removal
  keeps SECTION 1 as exactly one LazyColumn item, so the §Code scroll
  math above (3 fixed items + banner) survives unchanged.
- **The `totalQueueMinutes` trap (Nadia's catch, adopted):** the screen
  already receives a `totalQueueMinutes` parameter whose upstream flow
  ends in `.coerceAtLeast(dialMinutes)` and initialises at 50 — on an
  empty queue it would print minutes nobody queued. The numeral reads
  **`computedTotalMins` only** (Σ raw `estimatedMinutes` over
  `orderedQueue`, ACTIVE included — numeral = Σ printed rows). Nadia's
  removal spec deletes the unused parameter; until it lands, nobody wires
  the numeral to it.

---

## Law check

- **One fact, one rendering:** queue total → header numeral only (the bar
  is dead; rows never sum themselves). Minutes → the row's own `#N · Nm`
  cell only (START carries none; the numeral carries only the sum).
  Sub-step progress → rail length only (meta tally dropped, Decision 4).
  Steps-done → step strikes; task-done → title strike + done zone; the two
  never borrow each other's rendering (Decision 6). Completion count →
  rendered nowhere (SECTION 3's `DONE n` dies; the done zone *is* the
  record, not a count of it). Queue position → `#N` + order + Well depth,
  where depth is material, not data (§Depth & light, Mara's test).
  Cumulative starts and elapsed time → rendered nowhere on this screen
  (they died with the bar; elapsed belongs to Deck/AOD).
- **ARM ≤ 10s:** the ACTIVE block is the first thing under the QUEUE
  header; its START is one tap → 180ms staged commit → Deck loaded with
  the slide at rest. Promotion is never on the arm path for the
  already-active task. Nothing in this element adds a gesture, a hold, or
  a confirmation to that path.
- **HOLD absolute:** FROZEN state (§State machine) removes every mutation
  from this screen mid-session — verbs absent, steps inert, promotion
  inert. START degrades honestly to RESUME.
- **TELL THE TRUTH once, in human language:** the numeral speaks split
  duration (`1H 35M`), never a finish-by forecast; RESUME says what it
  does; a step-less rail shows an honest zero; done rows offer no undo
  they cannot perform; no number on this screen is animated through
  intermediate values it never equalled.
- **Locked items:** real font weights only (Atkinson 400/700, Anonymous
  Pro 700 for the numeral; wave-1's phantom Mediums corrected) · night
  staircase honoured exactly (ground `#171412`, steps ≥ 1.35:1, tiers
  11 / 5.8 / 3.5, alpha copies where no tier exists) · exactly **one
  orange** in the element — the ACTIVE task's START word, plus its own
  press-state underline and deepened press colour, which are the same
  object at different press depths, never a second object · ripple killed
  on every word and row · **never green** — no new colour of any kind is
  introduced; every value above is an existing token or an alpha copy of
  one.
- **Workshop laws:** beauty is retention — the Well, the strike, and the
  seal are the screen's beauty, and every one of them is also its
  mechanism; nothing decorative survives that does no work. Rested
  decides, tired executes: every queue operation in this spec is one tap
  with a ≥34dp target, performable one-handed, with destruction exiled to
  the expanded block where deliberation lives.
- **His open decisions, untouched:** banked hold — not this element, not
  specced. Routines — no routine state is rendered on any row (no
  provenance field exists; nothing fakes one). Guard — OPEN; no row reads
  Guard state under either outcome. Ledger — OPEN; dependency flagged in
  §Mechanism 4, fate not specced.

**Highest-impact decision in this file:** the DONE seal's ownership split —
title strike (not step strikes) + the screen-scope landing haptic graded by
minutes. It makes the app's most frequent meaningful act tell the truth in
three channels at once (eye: the title dies; margin: the rail fills; hand:
the mass lands at 620ms) while keeping every haptic inside the one-owner
rule. If only one paragraph of this spec ships, ship §Mechanism 4 with
§Motion's `animateItem` line — the belt-advance turns every other queue
mutation into machinery for free.
