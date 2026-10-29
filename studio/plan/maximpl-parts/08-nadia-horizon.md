# 08 — HORIZON: DEATH CERTIFICATE + THE SURVIVING FACT

**Seat:** Nadia (horizon) · **Cohort:** Maximum-Implementable · **Coordinator:** Voss
**Element:** `TaskHorizonBarView` — executed under SYNTHESIS item 29.
**Host:** `FocusSequencerScreen` SECTION 1, `Focus-app/app/src/main/java/com/example/`.
**Status:** PLAN ONLY — no code written, none to be written from this file except as specced.

The bar died of two laws, and the autopsy is short because the code confesses both:

- **TRUTH.** Its proportions were falsified by construction: the denominator was
  `Σ estimatedMinutes.coerceAtLeast(10)` (`BlockitTowerFadersAndCatalogViews.kt:379`),
  so every task under 10 minutes — the repository lawfully clamps estimates to 5–180
  (`MonolithRepository.addTaskToQueue:1208`, `updateTaskDuration:1507`) — was drawn at
  up to double its true share. And its total disagreed with the header two lines above
  it, which sums raw minutes (`FocusSequencerScreen.kt:101–103`). Two renderings of
  one queue, two different queues described.
- **TRUTH once.** It re-rendered, as coloured segments, the exact list printed in full
  directly underneath it — same tasks, same minutes, less information. A chart of the
  list that is right there does not survive one-fact-one-rendering.

What follows is the lawful execution: everything that dies, the one fact that
survives and its full spec, my dissent on the record, and a priced Ledger take.

---

# ASSIGNMENT 1 — THE DEATH CERTIFICATE + THE SURVIVING FACT

## Removal inventory

### Dies with the bar

| # | Asset | Anchor | Notes |
|---|---|---|---|
| 1 | `TaskHorizonBarView` class, entire — `tasks` property + unguarded setter, `onDraw`, the segment staircase, the 38dp label cliff, the centred `NOTHING QUEUED` empty branch | `ui/views/BlockitTowerFadersAndCatalogViews.kt:343–416`, plus its section comment header `:337–342` | The class is the **last in the file**; deletion is a tail truncation. Nothing else in the file references it. |
| 2 | `import com.example.data.FocusTaskEntity` | `BlockitTowerFadersAndCatalogViews.kt:10` | Sole consumer in the file was the bar (`tasks: List<FocusTaskEntity>`, `:345`). Tower/DayFaders/Tile use `DailyFocusStat` or nothing. |
| 3 | SECTION 1 `AndroidView` block — factory (incl. its `this.soundEngine = soundEngine` wiring), `update{}` (`isNightMode`, `soundEngine`, `tasks = orderedQueue`), `.height(42.dp)`, `.testTag("total_horizon_bar")` | `ui/screens/FocusSequencerScreen.kt:132–149` | The whole host wiring. The 8dp `Spacer` at `:131` that served the bar dies as bar spacing (the numeral block specced below carries its own 8dp). |
| 4 | `import com.example.ui.views.TaskHorizonBarView` | `FocusSequencerScreen.kt:54` | Only import of the class anywhere — grep-verified across the source tree: the bar has exactly one host. |
| 5 | The 11.5sp rendering of the total: `rightMonoNumber = "${computedTotalMins}m"` in SECTION 1's `FullBleedSectionHeader` call | `FocusSequencerScreen.kt:126–130` | This is a death, not a survival: the small total is *succeeded* by the numeral (§Header numeral). Leaving it would render the fact twice inside one section — the bar's own disease at label size. The whole SECTION 1 header call is replaced by Rurik's bespoke 68dp numeral block, whose label row carries the count only (different fact). |
| 6 | Screen KDoc line claiming the bar + "only active task #1 uses THE Accent (#F55314)" | `FocusSequencerScreen.kt:59` | False twice (no accent in the shipped bar; bar gone). Rewrite to name the numeral. |
| 7 | The unused `totalQueueMinutes: Int` parameter of `FocusSequencerScreen` + its argument at the TASKS call site | `FocusSequencerScreen.kt:69`; `MainActivity.kt` TASKS branch (~:378) | Already dead today (declared, never read in the screen) — and a live trap: see "Code: fights". Deleting it in the same commit removes the wrong-sum temptation permanently. The ViewModel flow itself **survives** (Deck consumes it). |
| 8 | Pre-existing dead imports in the views file, removed in the same pass — labelled honestly as *not* bar-caused: `android.animation.ValueAnimator` (`:3`), `com.example.ui.theme.MonolithCurves` (`:11`), `kotlin.math.min` (`:14`) | `BlockitTowerFadersAndCatalogViews.kt` | Grep-verified: zero call sites for any of the three in the file today. The bar's death is the occasion, not the cause. `kotlin.math.max` (`:13`) is **not** in this row — it survives (next table). |

### Must NOT die — something else consumes it

| Asset | Anchor | Consumer(s) |
|---|---|---|
| `TowerStreakView`, `DayFadersView`, `TileNumberCounterView` — the bar's three file-mates | Same views file, `:24`, `:157`, `:267` | All three belong to the Ledger screen (`CurioVaultScreen`), **not** to Horizon. Tower is additionally reused by `StreakTowerMonumentOverlay` (`ui/components/AirlockAndDisarmOverlays.kt:344`). Their fate is Assignment 2's question, not this execution's. |
| `BlockitCanvasView` base class — all paints (`paintFill/Stroke/Word/Mono/…`), pre-allocated rects (`trackRect`, `cellRect`, …), the 19-token resolvers (`tokPrimary()`, `tokSurfaceVariant()`, …), `dp()/sp()`, `radiusSmall`, `drawMixedWordAndMono`, `fireFeel`, the `isNightMode`/`soundEngine` properties | `ui/views/BlockitCoreViews.kt:53ff` | Every Canvas view in the app. The bar only *borrowed* these; not one was bar-private. No orphaned helpers exist — the bar kept no helper of its own, which is why its removal is a truncation, not a refactor. |
| `computedTotalMins` + `orderedQueue` | `FocusSequencerScreen.kt:95–103` | They become the numeral's source (below). The screen's own raw sum was the truthful one all along. |
| `FullBleedSectionHeader` component | `ui/components/MonolithCustomComponents.kt:96` | Five other screens + SECTION 3 of this screen. Only its SECTION 1 *arguments* change. |
| `ViewModel.totalQueueMinutes` flow | `ui/MonolithViewModel.kt:179` | Deck (`MainActivity.kt:291` passes it to `TactileDeckScreen`). It is the wrong source for the numeral (coercion, below) but it is not the bar's property and this execution does not touch it. |
| `MainActivity`'s TASKS wiring, `activeSession` collection | `MainActivity.kt` TASKS branch | The bar never received session parameters — my wave-1 now-line wiring was specced, never built — so there is nothing to unwire in MainActivity beyond item 7 above. |
| Tests | `app/src/test/` | Grep-verified: no test references `total_horizon_bar` or `TaskHorizonBarView`. No test updates are owed for the removal; screenshot renders will show the SECTION 1 change by design. |

**Net removal:** one class (~74 lines + comment), one screen block (~18 lines), three
import lines, one dead parameter + one call-site argument. Zero repository, ViewModel,
model, or navigation changes. SECTION 1 remains **exactly one `LazyColumn` item** —
which keeps Rurik's row scroll math (3 fixed items precede the queue rows, +1 when the
undo banner is present, his wave-1 §5d) valid unchanged. Mailed to Rurik; see "Conflicts
resolved" in my report.

---

## The header numeral

The bar's one true fact — **total queued minutes** — becomes the Tasks header numeral
at **44sp**. Everything below is the complete spec; there is no second step.

### Mechanism

- **Host and position.** `FocusSequencerScreen` SECTION 1, in the same single
  `LazyColumn` item the bar occupied. The block's construction is Rurik's
  succession spec (`plan/maximpl-parts/07-rurik-rows.md` §Mechanism 6) — a
  **fixed 68dp numeral block** replacing both the old header call and the bar:
  1. Label row: `TASKS` left; right side carries the **count**, a different fact —
     `3 QUEUED` (singular `1 QUEUED`; empty queue: `NOTHING QUEUED` and no
     count). The minutes leave this row entirely (inventory #5). This label row
     is bespoke to the block, not `FullBleedSectionHeader` — that component
     survives everywhere else it is used, untouched.
  2. The numeral row: **left-aligned** on the screen's 12dp margin axis — the same
     axis Rurik's rows start on. (The old 11.5sp total was right-aligned furniture;
     a hero numeral right-aligned would fight the list's entry edge.) Left-anchored
     also means digit-count growth extends rightward into empty row space and can
     never push the count label, which lives on the row above.
  The block is 68dp at every value including empty — the screen does not jump
  when the total changes digit count or the first task lands. **Sequencing
  (binding): the numeral lands in the same change as the removal.** Shipping
  the removal alone would leave SECTION 1 with no rendering of the total at
  all; the two halves of this succession are one commit.
- **What exactly is summed — the truth contract.** The numeral renders the screen's
  existing `computedTotalMins`: **Σ raw `estimatedMinutes` over `orderedQueue`** —
  the ACTIVE task (row #01) **plus** every QUEUED task, deduped by id, exactly the
  list printed below it. Consequences, all deliberate:
  - **ACTIVE is included.** It is printed as a row; excluding it would make the
    numeral ≠ Σ printed rows, uncheckable by eye — the same class of lie the bar
    died of. Numeral = Σ rows is the whole contract.
  - **No coercion, no floors.** Per-task values are already clamped 5–180 at the
    repository; the numeral applies nothing on top. A 5-minute task contributes 5.
  - **Completed, deferred, and deleted tasks contribute nothing** — they are not in
    `orderedQueue`.
  - **`actualSecondsSpent` is never subtracted.** The numeral is the gross committed
    total, not a remaining-time countdown. Elapsed time has its home (Deck / AOD);
    a numeral that shrank per second during a session would be a second rendering
    of elapsed *and* a live element on a planning surface. The numeral changes only
    when the queue itself changes: add, delete, duration edit, complete, defer.
    Reorder, promote, and cue change nothing and — per Motion — trigger nothing.
  - **It is never `ViewModel.totalQueueMinutes`.** That flow ends in
    `.coerceAtLeast(_dialMinutes.value)` and initialises at 50: on an empty queue it
    would print the dial's minutes as if they were queued. Any builder reaching for
    the ready-made flow produces a lying numeral. (Inventory #7 removes the
    same-named screen parameter so the trap cannot be taken by accident.)
- **Format — duration in human language, never a forecast.** A pure local formatter
  on the Int:
  - `0` → digits `0`, unit `M` (empty rendering, below).
  - `1–59` → `45` + `M`.
  - `≥60` → hours `H`, remainder `M`: `1H 35M`; zero remainder drops the minutes
    group entirely: `2H` (never `2H 0M`). Remainder is **unpadded** (`1H 5M`) —
    this is a spoken duration, not a clock. Hours unpadded, unbounded (`10H 15M`,
    `100H 5M` both format by the same rule).
  - A finish-by clock time (`DONE BY 14:20`) is **rejected**: it is a derived
    forecast assuming start-now, no breaks, no routine pre-emption; it would need a
    ticking `now` to stay true. Duration is the sum; the sum is the truth.
- **Typography.** Digits: **Sora Bold** — the real 700 file (`sora_bold`, registered
  in `Type.kt` after the Round-0b real-weights fix; no synthesized smear) — **44sp**,
  line-height **44sp** (inside Rurik's 68dp block), letter-spacing **−1.0sp**.
  Units `H` / `M`: **Sora SemiBold** (real 600 file) **16sp**, letter-spacing 0.
  Construction: a baseline-aligned `Row` of `Text`s — digit group, 4dp spacer,
  unit; for the H-form: hours, 4dp, `H`, 6dp, remainder digits, 4dp, `M` — each
  `Text` `maxLines = 1, softWrap = false`.
  - *Family ruling, recorded — the one digit this file and Rurik's do not yet
    share:* Horiz (Improvers) and Rurik both specced Anonymous Pro Bold digits +
    Atkinson Bold 16sp units; Set (Designers) specced Sora 700 (at 50sp). This
    spec holds **Sora at the synthesis-settled 44sp**: my brief locks Sora as
    the numeral's family, Sora is the standing display family, and Set's ladder
    independently lands on Sora — while Set's 50sp supersession claim is noted
    and not adopted (44sp is item 29's number; a global ladder change is Ren's
    call, not this seat's). Sora's proportional digits also settle the motion
    question mechanically (Motion). **The family digit — Sora (this file) vs
    Anonymous Pro (Rurik/Horiz) — is escalated to Voss as the single open
    item between our files; everything else in this spec is agreed to the
    digit.** If Voss rules Anonymous, this section's family names swap and
    nothing else in either file changes — the motion law already forbids the
    roll/flip that a mono face would have made tempting.
- **Colour.** Ink only, zero orange (the screen's accent stays where settled law
  puts it: the ACTIVE task's START), never green. Day: main pass
  `scheme.onBackground`. Night — and this screen's night is `effectiveNight =
  isDarkTheme || isSessionActive`, already computed in the screen — main pass
  `#D2C6BB` (the locked staircase's ≈11:1 text tier) on ground `#171412`; the
  header-row label stays its existing tier (night `#9C8E84`).
- **Not a control.** The numeral is never a touch target, at any size: it owns no
  action, and a tappable total would promise an editor the rows already are.

### Motion

- **Class: COMPUTED → re-strike only.** The total is arithmetic, not a measurement
  and not a choice. It therefore **never rolls, never flips, never counts** through
  intermediate values: 95 → 120 never passed through 96…119, and drawing those
  glyphs is a lie in motion. A digit-roll is additionally impossible honestly in
  Sora — a proportional face has no fixed slots to roll through; per-glyph travel
  would reflow the string it claims to animate.
- **The re-strike.** On any change of the Int value: glyphs swap **instantly**, in
  the same frame as the queue mutation that caused them, and the ink settles —
  alpha **88% → 100% over 180ms, `MonolithCurves.specA` (CurveA)** — a letterpress
  impression arriving, not an animation performing. (180ms is the vocabulary
  duration; the 120ms figure in the Improvers file is off-vocabulary and is not
  adopted.)
- **Guards.** Keyed to the **value**, not the list: recompositions with an unchanged
  total do nothing; reorder/promote/cue (sum-invariant) do nothing. First
  composition draws at 100% — no birth pulse. A second change mid-settle restarts
  the settle from 88% with the newest glyphs; pulses never queue. Under
  reduced-motion / animator-scale 0 the swap is simply instant.
- **Synchrony with the rows.** When DONE's delayed seal (Rurik's rows) fires the
  completion callback, `orderedQueue` changes at that moment — the re-strike lands
  on that same change, so number and list never disagree about whether the task
  exists.

### Sound / Haptics if any

**None — no `fireFeel` route, no sound-engine call, and this is a defended position,
not an omission.** Every change to this total is caused by a control that already
fired its feel: ADD's mechanical latch (already in `submitTask()`), DONE's
completion pulse, DEL's latch, the composer's duration caps. A buzz or click from
the numeral would double-fire the event and teach the hand that the confirmation
channel also means "a number changed somewhere." Haptics mean *your hand did
that*; no hand is ever on the numeral. The bar was silent for the same reason, and
its silence was the one thing about it that was never wrong.

### Depth & light

Flat print on the page ground — no card, no recess, no stroke, no elevation. The
one depth cue is the **letterpress bite**: the numeral row is drawn twice, an
under-print offset +1dp in y beneath the main pass — day: `scheme.surfaceBright`
(`#FAF6F0`) at 50% alpha; night: `#000000` at 60% alpha under the `#D2C6BB` main
pass. The re-strike's alpha settle applies to the whole two-pass stack, so the
bite arrives with the ink. Light here carries no data (no highlight encodes
progress or state); it is purely the impression of type pressed into the surface,
identical in structure day and night.

### Edge cases

- **Empty queue** (`orderedQueue` empty): numeral prints `0` + `M`, both in the
  muted tier — day `scheme.onSurfaceVariant`; night `#776A60` (the staircase's
  3.4:1 tier) — while the header row's right side reads `NOTHING QUEUED` in the
  label tier, carrying the fact in words at full label legibility. The block
  keeps its full height; the screen does not jump when the first task lands.
- **Active-only queue:** one row, label `1 TASK`, numeral = the active task's
  minutes. Not an empty state and not rendered as one.
- **Session running, Tasks opened mid-session:** tones flip to night via
  `effectiveNight` (existing behaviour); the numeral sits at the gross sum,
  frozen — it does not tick, dim, or offer any verb. HOLD is absolute and this
  surface is rested-hours territory.
- **Huge totals:** left-anchored growth is unbounded by construction — at 44sp,
  even `999H 59M` (≈8 glyphs) fits the 12dp-margined row on a 360dp screen, and
  the count label cannot collide with it because they are on separate rows.
- **Duration edits** (`onUpdateTaskDuration`, `onEditTask`): the sum changes →
  exactly one re-strike; the editing control owns its own feel.
- **Rapid successive adds:** each change re-strikes from 88% with the newest
  glyphs; no intermediate total is ever drawn mid-settle because the glyph swap
  precedes the settle.
- **Accessibility:** one `contentDescription` on the numeral row, glyph texts
  excluded from semantics so TalkBack reads the fact once, spoken in expanded
  human form built from the same Int and count: *"Queue total: 1 hour
  35 minutes, 3 tasks queued."* (singulars handled: *1 hour*, *1 minute*,
  *1 task*). Empty: *"Queue total: nothing queued."*
- **Screenshot determinism:** first frame renders settled at 100% alpha — the
  Robolectric harness sees the final state, no timing-dependent diffs.

---

## Dissent on record

*Signed — Nadia. The spec above executes the kill in full. This section is the
record that I argued against it in wave 1 and still believe one part of the
argument. The plan records dissent; it doesn't erase it, and it doesn't soften
the execution either.*

**The strongest version of my wave-1 case** (`maximpl/08-horizon-bar.md`), stated
as I would still state it:

1. **Proportional honesty.** A sum flattens distribution. `95M` does not tell you
   whether the queue is three even tasks or one 90-minute wall with a 5-minute
   pebble — and those are different mornings, different commitments, different
   failure modes. The rows list the parts, but the eye does not integrate a list
   into a shape; a truthful proportional bar does that integration for free, at
   a glance, before a single row is read.
2. **The printed scale.** Cumulative boundary numerals (`0 · 25 · 50 · 95`) state
   a fact **no row states**: where each task *starts* inside the committed
   block — "the third task begins at minute 50 of what you've promised
   yourself." That is planning information, not a re-rendering: order + duration
   are printed per row, but start-offsets exist nowhere except as arithmetic the
   user must do in his head.
3. **The now-line.** Segment 0 carrying spent-vs-remaining (from the already
   maintained `actualSecondsSpent`) was the only proposed rendering of partial
   work on the planning surface — the bar remembering that the head task is
   half-burned between sessions.

**The honest answer under settled law:**

- Point 1 is *true and insufficient*. The distribution information is real, but
  under the density settlement (SYNTHESIS 30: one expanded ACTIVE block, 44dp
  one-line rows) the list itself now carries shape — one hero and a column of
  one-liners is scannable in a way wave-1's uniform expanded rows were not. The
  bar's marginal information shrank when the rows grew up. I concede the point
  at current row design; I note it would reopen if rows ever flatten again.
- Point 2 is *a genuine loss, and I won't pretend otherwise*. Start-offsets die
  with the bar and nothing replaces them. The law's answer — that offsets are
  derived arithmetic over facts already rendered, and a second rendering
  surface for derived facts is exactly the pattern that produced a bar whose
  denominator disagreed with its own header — is a good answer about the bar
  *as built and as a class*. It does not make the information exist elsewhere.
  Recorded as the kill's real cost: **one fact (cumulative starts) is not
  re-homed; it is destroyed.**
- Point 3 *dies cleanly and I withdraw it myself*. Elapsed belongs to the
  running session; Deck and the AOD own it. A now-line on Tasks renders session
  state on the planning surface — two homes for one fact — and under HOLD
  absolute, a planning surface that tracks a live session is a mid-session
  surface by another name. My wave-1 self specced it; my wave-1 self was wrong
  under the law as settled.

**What survives elsewhere:** the printed-scale discipline — true axis, boundary
numerals, the 18dp label-collision rule (drop the interior label, always keep
`0` and the end), the axis persisting in the empty state — is handed to **Sten**
for the Section strip on Shield (SYNTHESIS 25), the one strip that is not a
chart of an adjacent list. Mailed to him this wave (thread:
printed-scale-handover). The re-strike grammar and the numeral = Σ printed rows
checkability survive here, in the numeral itself. The proportional bar, the
now-line, and cumulative starts on Tasks: dead, with the loss of the last one
recorded above.

I still believe a truthful bar would have beaten no bar on a skewed queue
(5 / 15 / 180). The law says a chart of the adjacent list dies regardless of
how truthful the chart is. The law is Sarthak's, the execution is mine, and
this paragraph is my name on the disagreement.

— Nadia

---

## Code: helps / fights

**Helps:**
- The truthful sum already exists, screen-local, one `remember` away
  (`computedTotalMins`, raw, over the deduped `orderedQueue`). The numeral is a
  promotion of existing code, not new plumbing.
- Single host, grep-verified: the bar is referenced from exactly one screen and
  one import. Removal cannot strand a second consumer.
- The bar is the last class in its file and kept no private helpers — deletion
  is truncation; the shared base class needs zero surgery.
- Real Sora Bold/SemiBold files are already shipped and registered (`Type.kt`,
  Round-0b) — the 44sp Sora spec is buildable today, no font work.
- Rurik's succession block was specced independently of this removal and the
  two agree on sum, format, motion, and sequencing (his mail, this wave) —
  the replacement is already designed; this file's job was only to clear
  its ground and state the truth contract it must honour.
- *Future-compat note (not part of this spec):* Horiz's peak — lifting the
  block out of the `LazyColumn` into a sticky masthead — is compatible with
  this behaviour spec unchanged, per Rurik. If it ever ships, SECTION 1
  leaves the list and the fixed-items count before the queue rows drops
  3 → 2; Rurik's scroll math must be re-checked at that point, not before.
- `effectiveNight` is already computed in the screen and already drives this
  section's tones; the numeral inherits day/night/session behaviour for free.
- SECTION 1 staying one `LazyColumn` item preserves every downstream index
  assumption (Rurik's scroll math) with zero coordination cost.

**Fights:**
- **The `totalQueueMinutes` trap (the big one).** A ready-made Int flow with
  almost the right name carries a `coerceAtLeast(dial)` falsification and a
  seed value of 50. It is even passed into this very screen — where it sits
  unused, line 69, waiting for a builder to wire the numeral to it. Inventory
  #7 deletes the parameter; the file flags the flow wherever the numeral is
  discussed. If only one warning in this document is heeded, this is it.
- `rememberMixedTypographyText` renders numerals in Anonymous Pro via the
  legacy `JetBrainsMonoFamily` alias — the header helper **cannot** build the
  numeral (wrong family, wrong tier). The numeral is bespoke `Text`s in
  SECTION 1; the helper survives for the count label only. A builder who tries
  to reuse it for the 44sp figure gets the wrong face silently.
- The screen file still speaks legacy font aliases (`PlusJakartaSansFamily` =
  Atkinson, etc.). Harmless, but the numeral must be written against
  `SoraFamily` directly, not against whichever alias is nearest.
- Recomposition discipline: `AndroidView.update{}` fired on every recomposition
  and the bar's setter invalidated unguarded — that cost dies with the bar, but
  its replacement must not reintroduce it: the re-strike is keyed on the Int
  value, and any implementation that pulses on recomposition (rather than on
  value change) is a defect against this spec.

---

## Law check

- **ARM ≤10s:** the removal deletes a 42dp Canvas `AndroidView` and its draw
  from the Tasks first frame and replaces it with plain Compose text. The arm
  path itself (Deck) is untouched; nothing anywhere gains a gesture, a wait,
  or a decision.
- **HOLD absolute:** the numeral is non-interactive, offers no verb, does not
  tick or shrink during a session, and renders no session state. Nothing on
  this surface negotiates with a sealed session.
- **TELL THE TRUTH once, in human language:** one rendering of the total, at
  hero size, in spoken-duration format (`1H 35M`); the sum is raw, coerced by
  nothing, and checkable against the printed rows digit for digit. The
  falsifying denominator and the disagreeing second total both die.
- **One fact, one rendering:** total → numeral only (the 11.5sp header copy
  dies); count → header row only; per-task minutes → rows only; elapsed →
  Deck/AOD only; cumulative starts → destroyed, recorded as a loss in the
  dissent, not quietly re-homed.
- **Beauty is retention:** what beauty the law leaves this element is the
  letterpress bite and the re-strike — type that feels pressed into the page
  rather than rendered on it. That is deliberately the minimum: a total that
  performs would be competing with the list it sums.
- **Rested decides, tired executes:** the numeral is read in rested hours on a
  planning surface; it asks for nothing and decides nothing.
- **Locked items:** real font weights only (Sora 700/600, shipped files);
  night staircase tones as specced; Tasks accent = the ACTIVE task's START
  only (numeral: zero orange); never green (no green anywhere in this spec).

---

# ASSIGNMENT 2 — LEDGER: INDEPENDENT COST TAKE

*Priced facts only, build terms. The verdict is the coordinator's and
Sarthak's; my one-line lean closes the section.*

## What exists today that serves ONLY the Ledger

| Asset | Anchor | Note |
|---|---|---|
| `CurioVaultScreen.kt`, entire file (288 lines) | `ui/screens/CurioVaultScreen.kt` | The tab itself. Its body never reads **6 of its own parameters** — `selectedArtifact`, `interceptLogs`, `harmonicSynthUnlocked`, `onSelectArtifact`, `onPlayHarmonicChord`, `onImportBackupJson` are declared and wired, never used. Already dead weight. |
| `TileNumberCounterView` (~75 lines) | Views file `:267ff` | Sole host: CurioVault (4 instances). The dot-matrix digits item 31 kills in a rebuild regardless. |
| `DayFadersView` (~105 lines) | Views file `:157ff` | Sole host: CurioVault. |
| AES half of `CryptoIntegrityManager` — `encryptBackupAesGcm` + `decryptBackupAesGcm` (~60 lines) + AES key plumbing | `data/CryptoIntegrityManager.kt:135–194` | Only caller chain: CurioVault's export chip → VM → `Repository.exportEncryptedBackup` (`MonolithRepository.kt:~1159–1180`). **`decryptBackupAesGcm` has zero call sites today** — the import half of the backup feature was never built. |
| VM backup/stats-for-display flows: `_encryptedBackupPayload` + `exportEncryptedStateBackup`; `_sevenDayStats` refresh path; `_selectedArtifact` + `selectArtifact` + `CurioCatalog` feed | `ui/MonolithViewModel.kt:129–130, 226–227, 509, 1054` | `sevenDayStats`' only consumer is DayFaders via CurioVault; `selectedArtifact` feeds only the dead parameter above. |
| The nav's 4th stop: `tabLabels = […, "LEDGER"]`, `tabCount = 4` | `ui/views/BlockitCoreViews.kt:829ff` (`MonolithBottomNavView`) | The tab's rent in chrome, paid on every navigation. |
| Deck's `onOpenCurioVault` parameter | `TactileDeckScreen.kt:100`; wired `MainActivity.kt:316` | Declared and wired — and **never invoked** in the Deck body (grep: declaration only). The Deck→Vault entry point is already vestigial. |

## What serves other screens too (survives any Ledger verdict)

- **The HMAC half of `CryptoIntegrityManager`** (`signSessionState`,
  `verifySessionState`) — this is HOLD integrity, not Ledger theatre:
  `MonolithRepository` signs at session start (`:~885`), re-signs on reboot /
  block rollover (`:~940, :~975`), verifies in
  `computeVerifiedSessionElapsed` (`:~584`) and in `getSystemGuardStatus`
  (`:~616`, feeding Guard's `isHmacSignatureValid`). Deleting the file
  wholesale in a Ledger purge would break tamper detection and Guard. Only
  the *display* of the signature (`HMAC•…` text in the receipts list) is
  Ledger-only.
- **Session history data** — `sessionHistoryFlow`, `FocusSessionEntity`, the
  DAO: feeds `totalFocusedMinutes` and `totalInterceptions` (Deck,
  `UnifiedStatusGuardDrawer`, `StreakTowerMonumentOverlay`). No verdict
  touches the data layer.
- **`TowerStreakView`** — reused by `StreakTowerMonumentOverlay`
  (`AirlockAndDisarmOverlays.kt:344`), independent of the tab.
- **Shared components** — `FullBleedSectionHeader`, `BlockitActionChip`,
  `rememberMixedTypographyText`: app-wide.

## The two prices

**Delete the tab.** Files touched: **4–6.** `CurioVaultScreen.kt` deleted
(−288); `MainActivity.kt` (VAULT branch ~20 lines, import, `selectedTabIndex`
mapping, Deck callback argument); `MonolithViewModel.kt` (`MonolithDestination.VAULT`
entry + its `navigateTo` refresh branch); `BlockitCoreViews.kt` (nav labels +
`tabCount` 4→3). Optional full cleanup adds the views file (−Tile, −DayFaders,
≈−180 lines) and VM flow pruning; the minimal delete leaves those dormant at
zero runtime cost. **New code: ~0** (nav slot math already divides by
`tabCount`). **Data migration: none** — sessions/tasks tables untouched;
orphaned flows can be pruned later or never.

**Rebuild it as the Ledger (SYNTHESIS item 31).** Files touched: **3–4.**
`CurioVaultScreen.kt` rewritten in place; views file: Tile dies (−75),
DayFaders dies and is replaced by a printed-scale column chart (new Canvas
view, ~120–180 lines); `MainActivity`/VM/nav untouched. **New code:** hero
stat, one mono stat line, hairline receipt rows, accession-number formatter,
the column chart with absence-as-gap rendering and the 4-zero-days event.
**Data migration: none** — every fact item 31 renders already exists
(`compute7DayHistory` already emits all 7 days including zeros; accession №
derives from the existing session `id` — a stored monotonic counter would
add one prefs key, still no DB migration). The `HMAC•` display line dies
(1 line); the export chip's fate is the only open sub-item — killing it
frees the entire AES chain priced above (~60 + ~20 + ~5 lines across three
files), keeping it costs nothing extra.

**Lean:** Delete the tab — its Ledger-only assets are already half-dead (six unused parameters, a decrypt with no caller, a Deck entry point never invoked), its shared spine survives untouched either way, and the rebuild spends a new chart and a rewritten screen to re-house facts that originate nowhere and are acted on nowhere in that room.

— Nadia
