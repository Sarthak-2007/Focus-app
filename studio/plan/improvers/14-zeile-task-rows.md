# Zeile — Improvers, task rows · PLAN WAVE (plan only, no code)

Element: the queue rows of `FocusSequencerScreen.kt` §3 (`itemsIndexed(orderedQueue)`).
Coordinator: Ilya. Wave-1 ladder is the floor; this file re-verifies it against
current code + day/night renders, then climbs past it where synthesis and the
shipped state demand.

---

## CURRENT — re-verified Oct 29 (code + `shots/03_tasks.png` + `shots/night/n03_tasks.png`)

Wave-1's §1 is accurate in every particular; I re-checked each claim against the
source and both renders rather than trusting it:

- **Sub-step numerals sit at the far right edge**, Anonymous Pro 10.5sp, `"0${stepIdx + 1}"`
  — step 10 would render `010`. They form a second right-edge numeral column
  competing with meta-right `#01 · 25m` (same face, near-same size). Confirmed in
  both renders: two unrelated `01`s stack within ~40dp vertically on row #01.
- **ACTIVE vs QUEUE differ in exactly three whispers**: meta word colour
  (ink vs muted), title weight (Bold vs Medium at the same 15sp), orange on
  `START 25m`. Nothing of position, size, or spacing. In both renders you must
  *read* `ACTIVE` to find the next session.
- **DONE (the sub-step state)**: `LineThrough` + colour swap to
  `onSurfaceVariant`. Day: works. Night: `onSurfaceVariant #FAE4D3` vs body
  `#F8F5F5` — the night render's struck first step of #01 is nearly as bright as
  the live steps under it. One 1dp line carries the whole state.
- **Row anatomy today**: flat `spacedBy(8.dp)` between meta/title/steps/actions/
  divider; actions are bare `Text.clickable` (glyph-box targets, ~12sp tall, no
  press state); `SPLIT` exists only when `subSteps.isEmpty()`, so `START/CUE`
  and `DEL` change address between rows; duration is stated twice on the cued
  row (`#01 · 25m` and `START 25m`); zero motion, zero haptic, zero sound in §3.
- **DONE (the task action)** removes the task instantly, mid-tap, no completing
  state — and the QUEUE header counts completions (`DONE 1`, backed by
  `completedTasks`). That count is an archive rendering living on a live screen.
- **No running state exists in §3.** `isSessionActive` only forces night
  (`effectiveNight`). During a session the rows render identically, fully
  editable — DONE/DEL/START all live mid-session. That is editing theatre
  against HOLD.
- **No routine provenance exists.** Nothing on a row can say a routine armed
  this task; `FocusTaskEntity` as passed to this screen carries no such field.

**Two wave-1 facts are now stale, and both change the plan:**
1. **Fonts.** Wave-1 specced Plus Jakarta Sans throughout. The app now ships his
   three picks with real weights — **Sora** (display), **Atkinson Hyperlegible**
   (body), **Anonymous Pro** (telemetry) — while this screen's code still
   registers Plus Jakarta. Every rung below is specced in the shipped three;
   Plus Jakarta leaves this screen entirely.
2. **Density is decided.** Synthesis item 30: one expanded ACTIVE block (20sp
   title), every other row a 44dp one-liner, sub-steps only inside ACTIVE, the
   CUE verb dead, drawn strikethrough + hit-stop on step completion, DONE drops
   the task with a landing haptic scaled by its minutes. Wave-1's peak (every
   row fully expanded, CUE alive) is superseded on those points; its axis,
   ring, and motion craft carry forward.

**House positions I keep without re-arguing:** hairlines not cards; words not
chips; no background tint on the ACTIVE row at any rung; gaps from the
4/8/12/20/32/48 system; margin 20dp (this screen's current 12dp horizontal
padding is pre-system — rung 2 absorbs the move); night staircase as shipped
(ground `#171412`, text tiers ≈11 / 5.6 / 3.4:1, one night accent `#F47442`).

---

## LADDER — 4 rungs, each shippable alone, in order

### Rung 1 — Truth and targets (no layout change, no new state)

Fixes what is simply wrong today, inside the existing block.

- **Numeral bug:** `"0${i+1}"` → `String.format("%02d", i + 1)`.
- **De-duplicate duration:** cued action `START 25m` → `START`. Minutes live in
  meta-right and in Horiz's header total; a third rendering that can disagree
  is a lie waiting for a desync.
- **Stable addresses:** `SPLIT` is always composed; when sub-steps exist it
  renders at alpha 0f, non-clickable. `START/CUE` and `DEL` never move again.
- **Hit targets:** every action word gets `padding(vertical = 12.dp,
  horizontal = 4.dp)` inside its clickable → ≈40dp hit height, visual rhythm
  unchanged. `DEL` exiled right via `Spacer(weight(1f))`.
- **Night done-step:** done step = `LineThrough` + `onSurfaceVariant` +
  whole-row alpha **0.55f**. Alpha, not a token edit — it fixes night tonight
  without touching the theme (the token fix lands properly at rung 2).
- **Ripple purged** on all word actions (`indication = null`): press feedback is
  a colour shift ink→muted, 120ms, curve A `cubic-bezier(0.2,0,0,1)`. A ripple
  blob on a letterpress word reads cheaper than nothing.
- **Haptic:** sub-step toggle only — `VibrationEffect` one-shot **4ms**,
  amplitude 90. **Sound:** silent, deliberately; no §3 sound until rung 4's two
  commitment sounds exist.
- **Type:** unchanged sizes; faces swap to shipped set (title Sora, steps
  Atkinson, meta/numerals Anonymous Pro) — pure registration change, zero
  layout risk.

### Rung 2 — The axis (★, priced below) + the night token

- **Geometry:** every row becomes `Row`: fixed **24dp axis column** + **10dp
  gap** + content column. Screen padding moves 12dp → **20dp** (system margin),
  so content starts at x = 54dp on every row, forever. Sub-step numerals move
  into the axis, right-aligned, Anonymous Pro 10sp. Hairline indents to start
  at content x. Meta-right `#01 · 25m` becomes the screen's only right-edge
  numeral column.
- **Done step, token-honest:** night rows stop using global `onSurfaceVariant`;
  done steps (and axis numerals, meta-right, queued words inside rows) take the
  staircase's lowest text tier (≈3.4:1 on `#171412`) + `LineThrough`, **no
  alpha** — rung 1's alpha retires. Day unchanged (`#5F5856` per the app-wide
  muted correction).
- **Axis numeral is state-bearing:** live step = numeral; done step = numeral
  replaced by a **10dp × 2dp tick** in the same cell, crossfade 120ms inside the
  rung-4 strike window. Progress reads down the axis alone: `01 02 — 04`.
- **Motion:** sub-step strike draws left→right, `scaleX` anchored left, curve A,
  **180ms**, with a **hit-stop**: the strike pauses at ~70% for **40ms** before
  completing (synthesis item 30's drawn strikethrough + hit-stop — the hand
  feels the step *land*, not just fade). Haptic on complete **8ms** @ 140;
  un-complete **4ms** @ 90. Sound: silent.
- **ACTIVE marker appears:** 8dp square in the axis cell beside the title —
  filled ink when ACTIVE, 1dp outline when queued. Silhouette carries state
  before a word is read.

### Rung 3 — Density: the synthesis model (CUE dies here)

- **Queued rows collapse to 44dp one-liners:** axis cell (marker outline) ·
  title, Atkinson 14sp Medium, single line, ellipsis · meta-right
  `#02 · 25m` Anonymous Pro 11sp. No sub-steps, no action row, hairline below.
  A 9-step queued task costs exactly 44dp, same as a 0-step one.
- **ACTIVE row is the one expanded block:** Sora SemiBold **20sp** title,
  lineHeight 27dp; meta line above (`ACTIVE` left, `#01 · 25m · 2/3` right —
  the progress fraction appended only when sub-steps exist); sub-steps at
  **28dp per step** on the axis grid; action row 44dp: `DONE` · `SPLIT` (slot
  stable per rung 1) · `START` · `DEL` exiled right.
- **Promotion replaces CUE:** tapping anywhere on a queued one-liner promotes
  it to ACTIVE. The old ACTIVE collapses and the new one expands — height
  animation on the 28dp step grid, curve A, **280ms**; `animateItem` placement
  curve A **420ms** so the promoted row visibly travels to the top instead of
  teleporting. Marker fills crossfade over the same 420ms. Haptic: waveform
  `[0, 10, 50, 4]` ms, amplitudes `[0, 160, 0, 90]` — a latch and a settle.
  Sound: the existing `TactileSoundEngine.playMechanicalLatch`, the same synth
  latch as task-add — promotion is a commitment, and commitments are the only
  §3 events that earn sound before rung 4.
- **Expand-in-place for queued steps: rejected.** Wave-1's `+ N MORE` dies with
  this rung — synthesis says sub-steps live only inside ACTIVE, and a queued
  row you must inspect is a row you promote. One gesture, one meaning.
- **TRUTH once, enforced structurally:** title, minutes, and the `2/3` fraction
  are rendered from the same `FocusTaskEntity` fields here, on the Deck's NOW
  line (Linea), and in Horiz's header total. Identical format strings:
  `{minutes}m`, fraction `done/total`, no rounding, no paraphrase.

### Rung 4 — Live states: running, routine-armed, completing

- **Running row (HOLD made visible):** while `isSessionActive`, all rows are
  read-only. The ACTIVE block's action row is replaced by a single mono line:
  `RUNNING · 12:04 LEFT · ENDS 14:35` (Anonymous Pro 11sp, muted, updating on
  the minute tick — a measured number, so it steps; it never rolls). Queued
  one-liners lose all click targets; promotion is impossible mid-session.
  Nothing on this screen can be edited, deleted, or re-cued until the session
  ends. The time shown is the session's own remaining time — the same number
  the Deck shows, from the same source, or it is not shown at all.
- **Routine-armed truth:** when the ACTIVE task was armed by a routine firing,
  meta-left reads `ACTIVE · BY ROUTINE 05:00` (Anonymous Pro Bold 10sp,
  ls 1.4sp, ink) — the routine's scheduled start, in words, no icon, no colour.
  Data dependency: needs an arming-provenance field on the task (none exists
  today — see CONFLICTS). Until that field exists this state does not ship and
  nothing fakes it.
- **Completing (DONE becomes an event):** DONE tap → title takes the drawn
  strike (curve A, 180ms — the title is struck *only* by DONE, never by the
  last sub-step; steps-done and task-done are two facts) → **120ms receipt
  hold** → row collapses (height + alpha, curve A, **280ms**) and drops.
  **Landing haptic scaled by minutes** (synthesis item 30): waveform timings
  `[0, D, 60, S]` ms where `D = clamp(6 + minutes/5, 8, 24)` ms at amplitude
  200, `S = 6` ms at amplitude 100 if minutes < 60, else `10` ms at 130 — a
  90-minute task lands measurably heavier than a 15-minute one. Sound:
  `playMechanicalLatch` at drop start. Interruptible: the existing undo banner
  cancels the drop during strike + hold.
- **The header's `DONE n` count dies.** Ledger is dead; a finished task's
  record is the session-end receipt, not a counter on the queue. The QUEUE
  header's right side goes empty. `completedTasks` data is untouched — only
  this rendering is removed. DONE on a row is a live state transition, never
  an archive write.

---

## ★ Best-ratio change — the axis, priced

**Move the sub-step numerals into a fixed 24dp left axis and indent the
hairline to the content edge (rung 2).**

- **Cost:** one `Row` wrapper + one column in the existing `itemsIndexed`
  block; the divider gains a start padding; zero new state, zero new tokens,
  zero data changes. One sitting, fully reversible, ships alone.
- **Pays four times:** (1) the numeral rejoins the step it numbers — today the
  widest empty relationship on the screen; (2) the screen drops to one
  right-edge numeral column, so `#01 · 25m` becomes the only voice at the
  right; (3) it is the load-bearing grid every later rung stands on — marker
  square, tick-for-numeral, one-liner alignment, and the ACTIVE block all
  live in that axis; without it rung 3's density has nothing to align to;
  (4) it makes rung 1's night fix *locatable* — done state becomes a column
  you scan, not a paragraph you read.
- **Risk:** the 12dp → 20dp margin move shifts the composer and Horizon
  header on the same screen; they must move in the same commit or the screen
  has two margins for one build. Named here so it isn't discovered there.

---

## PEAK (≤8 lines)

One expanded ACTIVE block — Sora 20sp, axis numerals, fraction in the meta —
over a column of 44dp one-liners that align to the same axis and hairline.
START is the screen's only orange, a filled commit key 44dp tall in the ACTIVE
block (re-argued below), findable peripherally in under a second.
Promotion is a tap on the row itself; CUE the verb does not exist.
Steps complete by drawn strike + 40ms hit-stop; the title is struck only by DONE.
DONE drops the task with a landing haptic priced by its minutes; no count remains.
Mid-session the whole list is read-only truth: one mono line, real remaining time.
If a routine armed the task, the row says so in words — or says nothing.

**The START re-argument:** wave-1 grammar put the one orange on START as
*text*. Synthesis item 12 rules orange is a fill and a verb, never text — and
ARM makes START's findability load-bearing: it is the arm gesture for every
task-led session. So at peak START becomes a filled key (orange fill `#F55314`
day / `#F47442` night, ink-on-orange label Sora Bold 12sp, 44dp tall, min
width 96dp, radius 8dp — the screen already speaks this language in the
composer's ADD key). Press: fill darkens one step, no scale, no ripple;
haptic one-shot **10ms** @ 200, the strongest feel in the element; sound none
— the Deck's arming ritual owns that sound and a double latch across the
navigation cut would stutter it. Every other action stays a word. One filled
object on the screen, and it is the commit.

---

## STANDARDS CHECK

- **ARM ≤10s:** the ACTIVE block is the first thing under the QUEUE header;
  its START is a 44dp filled orange key — the only filled object and the only
  orange on the screen — findable without reading. From screen-open to thumb-
  on-START is one glance + one tap. Promotion (rung 3) never sits on the arm
  path for the already-active task.
- **HOLD absolute:** rung 4 removes every §3 mutation during a session — no
  DONE, no DEL, no promotion, no step toggles. The running row shows time
  truthfully from the session's own source; a number Monolith cannot source
  identically to the Deck is not rendered.
- **TRUTH once:** title, minutes, sub-step fraction — one entity, three
  renderings (this row, Deck NOW line, Horiz header total), identical format
  strings, specified at rung 3. Computed totals re-strike in place instantly;
  they never animate through intermediate values. The queue header's DONE
  count — a second, archival rendering of completion — is removed at rung 4.
  Steps-done never borrows task-done's strike (Luz's correction, adopted).

---

## CONFLICTS (+ mail sent)

1. **Orange-as-text (synthesis item 12 vs wave-1 grammar).** My brief left
   this open ("keep or re-argue"); peak re-argues to a filled START key.
   This is a house-position change, so it is Ilya's/Ren's call to ratify —
   if text-START is kept instead, peak loses nothing but findability margin.
2. **Ledger reopened (Ren, wall 05:15) vs my brief's "Ledger dead."** I
   planned to the brief: DONE's record is the session-end receipt, header
   count removed at rung 4. If the Ledger verdict revives the tab, rung 4's
   header change still stands — a count on the queue was never the Ledger's
   home — but the receipt's wording becomes a cross-cohort question.
3. **Routine-armed state needs data that doesn't exist.** No provenance field
   on `FocusTaskEntity`. Mailed Rurik (MaxImpl task rows) — his
   implementation seat owns whether this is a field, a join, or a lie to drop.
4. **Mail sent:** Rurik (top step = the axis; provenance question) · Linea
   (peak carriage: 20sp Sora ACTIVE title + `2/3` fraction + filled START —
   what the Deck NOW line must match) · Horiz (header total: format + the
   instant re-strike rule + DONE-drop recomputation).
5. **The fallback lie (raised by Linea, confirmed in code).** `orderedQueue`
   puts `activeTask` first — but when nothing is active, the first *queued*
   task sits at index 0, and §3 labels index 0 `ACTIVE` with an orange
   `START`. The Deck's NOW line prints over the same fallback instance. Both
   surfaces agree with each other and both may be lying: nothing is active
   until he arms it. My position, mailed to Linea: the fallback state gets a
   joint rename on both surfaces (`NEXT` — the word the Deck already uses for
   what a routine will fire) or neither surface changes; a rename one screen
   makes alone is a worse lie than the shared one. Ilya's call.
6. **Convergences confirmed by return mail:** fraction derives from the
   entity getters (`completedSubStepsCount / subSteps.size`), never a local
   recount (Linea); row minutes stay raw — no rounding, clamping, or display
   floor anywhere in this ladder, so Horiz's numeral = Σ printed rows holds
   exactly; the drawn strikethrough is held against Linea's tone-only
   proposal because synthesis item 30 mandates drawn strike + hit-stop —
   she has agreed to take the strike on the Deck rather than diverge.
