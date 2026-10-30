# DRUCK — Designers, Print seat (coordinator: Sana)
## PLAN WAVE — TASKS composition spec. No code.

The screen Sarthak called "just so bland," composed as a printed page:
one hero number, one task in the light, a queue that descends like type
running out of ink, a ruled floor, and done underneath it. Nothing on
this page is a card farm, a chart, or an illustration. Grouping is done
by tone, offset, air and one rule — the print tools.

Binding, not relitigated: Concept = NONE · ARM ≤10s · HOLD absolute ·
TRUTH once · one fact, one rendering · Horizon bar dead as an object ·
Light Well composition · one expanded ACTIVE block · composer = sticky
single line, key bank only while focused · CUE verb dead · radius
8 keys / 16 panels · margin 20dp · label plate = Anonymous Pro Bold
10sp, +2.0 tracking, letterpressed (+0.75dp offset strike).

---

## 0. Frame, grid, tokens

Reference width 390dp; all x-positions are parametric in margin.
Content margin **20dp** both sides → measure **350dp** at reference.
Vertical space only from the spacing scale: **4 / 8 / 12 / 20 / 32 / 48**.
No gap outside the scale exists on this screen. Hairlines are 1dp
unless a rule below says otherwise. No shadows, no elevation anywhere;
depth is tone step + recess + skirt only.

### 0.1 Colour tokens (complete for this screen)

| Token | Night | Day | Job |
|---|---|---|---|
| `ground` | `#171412` | `#E4DFDA` | page stock; queue rows sit directly on it |
| `raised` | `#362C27` | `#FAF6F0` | ACTIVE panel fill; unselected cap faces |
| `recess` | `#100E0C` | `#D8D2CC` | composer field fill; key-bank well fill |
| `recessFocus` | `#0B0A09` | `#CFC8C1` | field fill while focused (one step below `recess`) |
| `ink1` | `#D2C6BB` | `#252323` | primary text; ACTIVE title; ADD key fill (see below) |
| `ink2` | `#9C8E84` | `#5E5A56` | secondary text; meta; unselected cap labels |
| `ink3` | `#776A60` | `#877F77` | tertiary text; struck/done text; depth floor |
| `hairline` | `ink1 @ 18%` | `ink1 @ 18%` | row separators, composer top rule |
| `foundation` | `#9C8E84` (solid, 2dp) | `ink1 @ 55%` ≈ `#8B8680` (2dp) | the foundation rule (§3) |
| `accent` (START only) | fill `#C74814` | fill `#F55314` | the screen's one orange — a fill, never text |
| `accentSkirt` | `#A83A10` | `#D9430F` | START key's 3dp skirt band |
| `accentLabel` | `#FFE4CF` | `#FAF6F0` | the word START on the orange fill |
| `inkKey` (ADD, bank state) | fill `#D2C6BB`, label `#171412`, skirt `#9C8E84` | fill `#252323`, label `#FAF6F0`, skirt `#100F0F` | the bank's only ink key |
| `capSkirt` | `#241E1A` | `#D8D1C9` | duration-cap skirt band, standing state |
| `capSunk` | `#1E1A17` | `#DCD6CF` | selected cap face (sunk into the well) |

Orange count on this screen, at rest and in every state: **one** —
the START key's fill. ADD is ink, never orange. The header numeral is
ink, never orange. No badge, count or word is orange anywhere.

### 0.2 Type tokens used on this screen

| Style | Spec |
|---|---|
| Plate | Anonymous Pro Bold 10sp, +2.0 tracking, letterpress offset +0.75dp — every eyebrow/label, no exceptions |
| Hero | Sora Bold 44sp, tracking −1.0, line height 48dp (§1) |
| ActiveTitle | Atkinson Hyperlegible Bold 20sp, line height 26dp |
| Body | Atkinson Hyperlegible Regular 15sp, line height 20dp |
| BodyBold | Atkinson Hyperlegible Bold 15sp, line height 20dp |
| Mono | Anonymous Pro Bold 15sp, line height 20dp — durations, indices, readouts |
| Word | Sora Bold 15sp, +0.5 tracking — DONE, DEL, ADD (word state), START |

Real weights only: Atkinson and Anonymous Pro exist at 400/700; nothing
below asks either family for a weight it does not ship.

---

## 1. Header zone — the hero: THE FOOTING

In print bookkeeping a column's total is its *footing*. This screen's
one hero is named **the Footing**: the queue total, promoted from the
dead Horizon bar to the head of the page, stated once, at hero size,
and nowhere else on the screen in any form — no bar, no segment, no
per-task sum, no caption repeating it.

### 1.1 Geometry (from the bottom of the chrome band)

| Element | Position / size |
|---|---|
| Air above header | 20dp |
| Plate row | height 12dp, baseline at +10dp. Left: `TASKS` (plate). Right, right-aligned to the right margin: `N TASKS` (plate) — the task *count*, its only summary rendering; row indices (§4) are addresses, not a count |
| Gap | 8dp |
| Numeral row | height 48dp. The Footing, left-aligned at the margin |
| Header block total | 20 + 12 + 8 + 48 = **88dp**, then a 32dp section break (§6) |

### 1.2 The Footing — what it prints

- **Value:** sum of minutes of the ACTIVE task + every queued task.
  Done tasks never count. Computed, never stored.
- **Form:** human language, never a stopwatch. Under 60: `45` with the
  unit set beside it (below). 60 and over: `2H 35M` — hour and minute
  glyphs all at 44sp Sora Bold; the letters H/M at 20sp Sora Bold on
  the same baseline, 4dp after their number. Examples: `45 MIN`,
  `1H 00M`, `2H 35M`, `12H 00M`.
- **Beside it, right-aligned, bottom-aligned to the numeral baseline:**
  a two-line stack, right edge at the right margin:
  - Line 1 (plate): `IF STARTED NOW`
  - Line 2 (Mono, ink1), 4dp below line 1's baseline: `ENDS 18:40`
  This is a *different fact* (a projected clock time, honestly
  conditional), with its only home here. If the total is 0, the stack
  is absent — no projection is printed for an empty queue.
- **Motion:** the Footing is a computed number. It never rolls, flips
  or counts through intermediate values. On change it re-strikes in
  place, instantly, with one letterpress pulse: glyphs land at 88%
  ink and settle to 100% over 120ms. That pulse is the only motion
  the header ever makes.

---

## 2. The Light Well — geometry

The well is not an object. It is a descent: the ACTIVE task sits in
the light (the one raised panel), and each queued row steps down in
**ink tone and left offset only** — no fills, no bands, no cards in
the queue. A row's depth is read from how pale its type is and how
far it has stepped in from the margin. Both channels agree, always,
from the same depth value.

### 2.1 Tone steps per queue depth (title ink; duration/index ink = one step further down the same ladder)

| Depth | Night title | Day title | Left indent | Notes |
|---|---|---|---|---|
| ACTIVE | `#D2C6BB` (ink1) on `raised` | `#252323` (ink1) on `raised` | 0 (panel edge) | in the light — §3 |
| Queue 1 | `#C9BBB0` | `#333130` | 4dp | nearest the light |
| Queue 2 | `#B7A797` | `#474441` | 8dp | |
| Queue 3 | `#9C8E84` (ink2) | `#5E5A56` (ink2) | 12dp | |
| Queue 4 | `#887A6E` | `#756F6A` | 16dp | |
| Queue 5+ | `#776A60` (ink3) — **floor** | `#877F77` (ink3) — **floor** | 20dp — **cap** | tone and offset both stop descending |

The floor is a legibility law, not a rendering failure: below ink3 a
15sp title stops being readable at 3:30 AM, so depth past 5 is carried
by the row's index numeral alone. The descent is capped, stated, and
never faked darker.

### 2.2 What the descent does NOT do

No row gets a background fill at any depth. No progress is encoded in
the tone (tone = position in queue, nothing else — position is the
fact the row exists to state). Done tasks do not continue the descent;
they leave the well entirely and live below the foundation line (§3.3)
at ink3, struck — a separate country with its own rule.

---

## 3. The ACTIVE block — anatomy

The screen's one panel and its one piece of furniture: a raised slab
in the light shaft. Exactly one exists at any time.

### 3.1 The slab

| Part | Spec |
|---|---|
| Fill / radius | `raised`, radius **16dp** (panel radius), full measure (350dp) |
| Padding | 20dp left/right, 20dp top, 20dp bottom — all from the scale |
| Plate row | height 12dp. Left: `ACTIVE` (plate, ink2, letterpressed). Right: `#01 · 25M` (Mono, ink2) — position + this task's duration, their only home inside the block |
| Gap | 12dp |
| Title | ActiveTitle (20sp Bold), ink1, max 2 lines, ellipsis on line 2 |
| Gap | 12dp |
| Sub-steps | §3.2 — they exist **only** inside this block, nowhere else on the screen |
| Gap | 20dp |
| Action row | height 48dp, §3.3 |

Expansion: when a queued row is raised (§4), this slab's content is
replaced — the previous active task collapses to a 44dp queue row at
depth 1 and the raised task's steps unfold inside the slab. Height
animates 280ms, Curve A `cubic-bezier(0.2,0,0,1)`. There is never a
second expanded block, in any state, ever.

### 3.2 Sub-step rows (inside ACTIVE only)

- Row height **40dp**, full panel inner width (310dp), whole row is
  the tap target. (44dp queue law governs queue rows; step rows are
  panel interior — target is full-width, stated here honestly.)
- Left: step numeral `01` — Anonymous Pro Bold 15sp (Mono), ink2,
  right-aligned in a 24dp column at the panel's inner left edge.
- Gap 12dp. Step text: Body (15sp Regular), ink1, single line,
  ellipsis. All elements share one baseline, 25dp from row top.
- No hairlines between steps. Air and the 40dp rhythm separate them;
  a ruled sub-list inside the slab would print a table, and this is
  a working block, not a table.
- **Add-step line:** after the last step, one 40dp line in the same
  geometry: `+` in the numeral column (Mono, ink3), `ADD STEP` as
  plate text in ink3 at the text position. Tap → the line becomes a
  single-line field (Body text, ink1, caret ink1, 1dp `hairline`
  rule appearing under it) for one step's text; return adds the step
  and restores the `ADD STEP` line, keyboard staying up.
- A task with zero steps shows only the `ADD STEP` line — no empty
  scaffolding, no placeholder steps.

### 3.3 The strike (completion language, spent once per fact)

- Tapping a step row completes that step: the strike is **drawn** —
  a 1.5dp rule in ink2, drawn left→right across the step text's exact
  measure in 180ms, Curve A — never the font's built-in line-through.
  On the same frame the text steps ink1 → ink3 and the numeral is
  replaced by a 10×2dp tick in ink2, in the same 24dp column.
  Haptic: one-shot 8ms. Un-tapping reverses all of it (strike erases
  right→left 120ms, tick → numeral, ink3 → ink1, haptic 4ms).
- Steps-done is **not** task-done. Completing the last step escalates
  everything *except* the title strike: 110ms freeze, the step's
  strike drawn at 2dp, a third haptic pulse (20ms @ full), and the
  action row's DONE word inks ink2 → ink1. The title is struck only
  by DONE itself (§3.4). One fact, one rendering — the strike is the
  strongest completion mark this screen owns and it is spent once.

### 3.4 Action row — DONE, DEL, and the one orange

Height 48dp, baseline-aligned, inside the slab:

- **`DONE`** — left. Word style (Sora Bold 15sp, +0.5), ink1, 48dp
  tall target, min width 64dp. This is the verb that completes the
  task. On tap: the title's strike draws (1.5dp, ink2, 180ms,
  concurrent with the drop below), then the slab performs the
  **drop**: it translates down and its height collapses over 280ms
  Curve A as the completed row lands below the foundation line (§5),
  struck, at ink3. Landing haptic scaled by the task's minutes:
  ≤25M → 14ms, 45M → 18ms, ≥90M → 24ms, one-shot. The next queued
  row rises into the slab in the same 280ms window. If the queue is
  empty, the slab is simply absent afterwards (§7).
- **`DEL`** — second from left, 20dp after DONE. Word style, ink3,
  48dp target. Deletes the active task: slab collapses 280ms, the
  queue rises, no drop, no strike — deletion is clerical, it gets no
  ceremony and no colour. (SPLIT does not exist on this screen. A
  task is split by composing two; no verb is drawn for it.)
- **`START`** — right, the screen's single orange. A key, radius
  **8dp**, height 48dp, width 124dp, right edge at the panel's inner
  right edge.
  - Fill `accent`; label `START` (Word style, `accentLabel`),
    centred. The duration is **not** printed on the key — the plate
    row's `25M` already states it; a second printing is a second
    rendering.
  - Skirt: a 3dp band of `accentSkirt` under the key's face — the
    key stands 3dp proud. Press: answers on DOWN, face travels down
    2dp (skirt 3→1dp) in 70ms; fires on release; slide-off cancels.
    Haptic on fire: one-shot 10ms. START hands the task to the Deck;
    no sound is played here — the arm owns its own sound.
  - START is the only filled element in the action row, the only
    orange on the screen, and it exists only inside ACTIVE. A queued
    row has no START; raising a row is how a task earns one.

---

## 4. The queue row module

One module, learned once, repeated at every depth.

| Part | Spec |
|---|---|
| Row height | **44dp**, exactly, at every depth |
| Separator | 1dp `hairline` **between** rows only — none above the first, none below the last (the 32dp section air and the foundation do that work) |
| Left indent | per depth, §2.1 (0/4/8/12/16/20dp added to the margin) |
| Index | `#02` — Mono, ink = one step below the row's title ink, in a 32dp-wide left-aligned slot at the indented edge |
| Title | BodyBold (15sp Bold), ink per §2.1, **one line**, ellipsis. The row is one line or it is not a queue row |
| Duration | `25M` — Mono, right-aligned to the right margin, ink = one step below the title ink |
| Baseline | all three elements share one baseline, **28dp from row top** (44dp row, 15sp/20dp line, centred) |
| Verbs | **none.** No DONE, no DEL, no START, no step text, no fraction, no progress mark. A queue row states three facts — address, name, length — and offers one gesture |

- **The one gesture:** tap anywhere on the row → it becomes ACTIVE
  (§3.1). The current active task returns to depth 1 as a queue row.
  This is the only way focus moves, and it is why no queue row needs
  verbs: verbs live in one home, the slab.
- **Press state:** on DOWN the row's title and duration ink step one
  tone *up* the ladder (toward ink1) for the duration of the press.
  No fill, no ripple, no scale. Letterpress does not bounce.
- New tasks enter at depth 1 (directly under the slab — or as ACTIVE
  if no active task exists), pushing every row down one depth: ink
  re-tones and indent re-steps over 260ms Curve A. Rows never jump
  tone instantly on someone else's add; the descent is visible.

---

## 5. The foundation line and DONE below it

### 5.1 What the foundation is

The floor of the well, drawn as print draws a floor: **a 2dp rule,
full measure, `foundation` token** — the heaviest rule on this screen,
used exactly once. Above it, 8dp of air and a plate row (height 12dp):
left `DONE`, right the done count as Mono ink2 (`2`). Below it, the
country of finished things. If there are no done tasks, the plate
row, the rule and the section are **all absent** — absence renders as
absence; no empty foundation is drawn under a well that has never
dropped anything.

### 5.2 DONE rows (below the foundation)

- Row height 44dp, same module geometry as queue rows — index slot,
  one baseline at 28dp, duration right — so the eye reads them with
  the module it already learned.
- Title: Body (15sp **Regular** — done tasks lose the bold), ink3,
  with the drawn strike through its measure (1.5dp, ink2 at 60%).
  The strike here is the same mark as §3.3, at rest: this is where
  struck titles live.
- Index and duration: Mono, ink3. Indent: 0 at every position — below
  the foundation there is no depth; finished is finished.
- Hairlines between done rows only, as §4. Done rows are inert: no
  tap, no verbs, no un-done on this screen. The record does not
  negotiate.
- The section ends with 32dp of air before the composer's rule (§6).
  Done rows are the last printed thing on the page.

---

## 6. Section cadence (the whole page, top to bottom)

```
chrome band (owned by chrome spec — not this screen's)
20   air
HEADER — plate row 12 + gap 8 + Footing 48            = 68
32   section break
ACTIVE slab (content-sized; 3 steps ≈ 20+12+26+12+120+20+48+20 = 278)
20   gap
QUEUE plate row                                       = 12
12   gap
queue rows 44 each, hairlines between
32   section break            (only if DONE section exists)
DONE plate row 12 + gap 8 + foundation rule 2
12   gap
done rows 44 each, hairlines between
32   air
——— composer top hairline (sticky, §7) ———
```

Rules of the cadence: a section whose content is empty does not
print its plate row or its gaps. The list scrolls under the sticky
composer; scroll bottom inset = composer height + 20dp so the last
done row can always clear the composer. Nothing else is sticky.

---

## 7. The composer — both states

The daily act. It is a ruled foot of the page: sticky, full measure,
sitting on `ground`, separated from the list by a 1dp `hairline`
rule across the full measure at its top. It never floats, never
carries a fill of its own, and never shows the key bank at rest.

### 7.1 Collapsed state (field unfocused) — one line, 56dp

One row, height **56dp**, contents vertically centred:

- **Field:** height 48dp, radius **8dp**, fill `recess`, left edge
  at the margin, flexible width. Inside, left inset 12dp:
  - Placeholder `Add a focus task…` — Body, ink3. Typed text replaces
    it in Body, ink1. Caret: ink1, never orange.
  - Duration echo, right inside the field, right inset 12dp:
    `· 25M` — Mono, ink2. This is the *selected duration*, stated
    where it will act. It persists between adds (§7.3).
- **`ADD` as a word:** right of the field, 12dp gap, right edge at
  the right margin, width 52dp, height 48dp target. Word style.
  - Field empty → ADD in ink3, **disarmed**: tapping it focuses the
    field and does nothing else. An ADD that fires on nothing is a
    lie; the word's paleness is the statement.
  - Field non-empty → ADD in ink1, armed, answers on DOWN (ink steps
    to full on touch-down, fires on release). No fill in this state —
    in the collapsed line ADD is a word, per the settled law.
- No key bank. No caps. Nothing else in the 56dp.

### 7.2 Focused state — the bank appears, 128dp total

On focus the composer grows downward-anchored (it is sticky at the
foot; growth is upward into the list's inset):

| Part | Spec |
|---|---|
| Field line | 56dp, as §7.1, with the focus treatment (§7.4) |
| Gap | 8dp |
| Key bank well | height **64dp**, radius **16dp** (panel), fill `recess`, padding 8dp on all sides |
| Composer total | 56 + 8 + 64 = **128dp** + the top hairline |

Inside the well, one row, height 48dp, gap 8dp between keys:

- **Four duration caps** — `15M` `25M` `45M` `90M` — equal flexible
  widths ((350 − 16 well padding − 72 ADD − 8 gap − 3×8 cap gaps)
  ÷ 4 = 57.5dp each at reference). Radius **8dp** (keys). Label:
  Mono, centred on the cap face.
  - **Cap anatomy:** face + **skirt** — a 3dp band of `capSkirt`
    along the cap's bottom edge; the cap stands 3dp proud of the
    well floor. Standing face = `raised`, label ink2.
  - **Press:** answers on DOWN; face travels down 2dp in 70ms
    (skirt 3→1dp) and selection commits on that travel — the press
    *is* the selection animation. Haptic 6ms.
  - **Selected cap — sunk:** skirt **0dp** (the cap sits flush on
    the well floor, 3dp below its standing neighbours), face =
    `capSunk`, label ink1, and a 12×2dp index line in ink1 centred
    3dp under the label. State is silhouette first, ink second:
    across the room, the sunk cap is the selected one. Selected
    never takes the ink fill — that fill belongs to ADD alone, so
    "which one commits?" is never ambiguous.
- **`ADD` — the bank's only ink key:** width **72dp**, height 48dp,
  radius 8dp, fill `inkKey` (ink fill, ground-coloured label),
  skirt 3dp of the inkKey skirt token, same 2dp press travel as the
  caps. Label `ADD` (Word style, in the key's label token), centred.
  Armed/disarmed as §7.1: with an empty field the key renders at
  ink3-equivalent (fill steps to `capSunk`, label ink3) and tapping
  it only holds focus. The moment the field has text, ADD is the
  darkest, heaviest object in the bank — the commit, visible.
- Blur (keyboard dismissed / nav away) collapses the bank: well
  height animates 64→0 over 260ms Curve A, the composer returns to
  the 56dp line, and an un-added draft **stays in the field** —
  blur puts the card away; it never throws it away.

### 7.3 ADD behaviour (the filing rhythm)

On ADD fire: the task is created with the field's text and the
selected duration, inserted at queue depth 1 (or as ACTIVE if the
slab is empty); the Footing re-strikes (§1.2); the field clears;
**focus and keyboard stay** and the selected duration persists —
batch entry is type → ADD → type, with no re-tap of field or cap
between tasks. The empty-field ADD fire does not exist (§7.1/§7.2);
there is no error state, no shake, no message.

### 7.4 The field's focus treatment (drawn on the field, nowhere else)

Focus is stated on the field itself, in three agreeing changes, all
reversible on blur over 180ms linear:

1. Fill deepens: `recess` → `recessFocus` (one tonal step down —
   the paper is pressed where the hand is).
2. The field's bottom edge-line — a 1dp catch line inside the
   field's bottom edge — steps ink3 → ink1.
3. The duration echo inks ink2 → ink1.

No outline ring, no glow, no orange, no label appearing elsewhere.
If you cannot tell the field is focused by looking *at the field*,
the treatment has failed; nothing off the field may carry it.

---

## 8. Empty states — print's answer (no illustration, ever)

### 8.1 Virgin empty (no active, no queue, no done)

- Header prints in full: plate row `TASKS` / `0 TASKS`; the Footing
  prints **`0 MIN`** at 44sp — a printed zero is a mark, not an
  absence — and the `ENDS` stack is absent (§1.2).
- Below the 32dp break, where the slab would sit: **one hairline,
  full measure** — the empty well's floor, drawn and nothing above
  it — and beneath it, 12dp down, one sentence, Body, ink2, left at
  the margin: `Nothing queued. Compose the first task below.`
- No foundation section (nothing has ever dropped). The composer
  sits in its collapsed 56dp state; its placeholder is the
  invitation. The keyboard is **not** summoned — an app that opens
  a keyboard uninvited has made a decision for the rested user.

### 8.2 All done (queue empty, done > 0)

- Footing prints `0 MIN`; the beside-stack's line 2 is replaced by
  `ALL OF IT DONE` (Mono, ink2) under plate line 1 `QUEUE TOTAL`.
- No slab, no queue section, no empty-well sentence (the well is
  not empty — it is *finished*, a different fact). The foundation
  and the struck done rows print as §5. The page reads: total zero,
  everything below the line.

### 8.3 Queue empty, active present

Slab prints normally; the QUEUE plate row and its gaps do not print
(§6 rule). The next thing below the slab is the foundation section
or the composer's air.

---

## 9. Under HOLD (session sealed)

Tasks is rested-user territory; mid-session it offers nothing.
While sealed: the slab's plate word reads `RUNNING` (state words
flip on arrival, never mid-flight), START is **absent** (not greyed
— the task is started; a dead key is a mid-session decision wearing
a costume), DONE and DEL are absent, step rows are inert, queue
rows are inert, and the composer is absent entirely — the page ends
32dp below its last row. The Footing, the well and the foundation
still print: facts stay readable, verbs leave. Everything returns
on session end, in place, with no animation ceremony.

---

## 10. Acceptance checks (any render, day or night)

1. Count orange fills: **1** (START). Count panels: **1** (ACTIVE).
   Count heroes: **1** (the Footing, 44sp). Count expanded blocks: **1**.
2. Overlay the grid: margin 20dp everywhere; queue indent steps
   0/4/8/12/16/20 by depth; every gap ∈ {4, 8, 12, 20, 32, 48}.
3. Read the queue by tone alone, titles covered: depth order must
   still be legible from ink + indent down to the ink3 floor.
4. Queue rows measure 44dp; every queue row is one line carrying
   exactly index, title, duration on one baseline (28dp).
5. The queue total appears **once** — as the Footing. Search the
   render for any bar, segment, caption or sum repeating it: none.
6. Composer at rest = 56dp, ADD a word, zero caps visible. Focused =
   128dp, four skirted caps + one ink ADD in a 64dp recessed well;
   selected cap sunk flush, skirt 0, and **not** ink-filled.
7. DONE tasks sit below a 2dp foundation rule, struck, Regular,
   ink3; with zero done tasks the rule does not exist.
8. Empty render: a `0 MIN` Footing, one hairline, one sentence,
   no illustration, no icon, no summoned keyboard.

— Druck, Print, Designers cohort
