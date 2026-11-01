# FINISHER-2 — Tasks screen, finished surface after the Horizon kill

**Issue:** Sarthak-2007/Focus-app #12 — Tasks + F-1 (cell lead: AMT)
**Role:** Finisher-2 (screen finish). Judge only — no code edited, nothing
compiled, no Gradle/tests run.
**Sources read in full:** `studio/plan/AUDIT-tasks.md` (items 1, 4, 6, 7 +
F-3), `studio/plan/PLAN-issue-12-tasks-f1.md`, and
`FocusSequencerScreen.kt` as built on branch `agent/issue-12`
(848 lines), plus the two components this slice touches:
`MonolithCustomComponents.kt` (FullBleedSectionHeader, :123–164) and
`BlockitTowerFadersAndCatalogViews.kt` (TaskHorizonBarView, :155–257).
Cell filings designer.md / critic.md / innovator.md read for collisions;
none found with this slice (the Designer's `NEXT` marker is **not**
adopted — see §3.4).

**Slice, exactly:** the finished surface after F-3 — (a) QUEUE as the
screen's one header with its new right word, (b) the blank leaf printed
exactly once, (c) head-leaf hierarchy with the ACTIVE/QUEUE state words
killed, (d) spacing rhythm after the ~113-line deletion, (e) the header
count derived from the displayed list through the tear ghost window.

— Finisher-2 (issue #12 cell)

---

## 1. The QUEUE header — the screen's one header (audit item 4, F-3)

### 1.1 As built today

Two `FullBleedSectionHeader` calls compete in one file:

- `FocusSequencerScreen.kt:172–185` — `HORIZON`, right word built from
  `orderedQueue.size` + `computedTotalMins` (:131–133), i.e. the count
  rides on the dead-minutes regime and the minute total prints beside it.
- `FocusSequencerScreen.kt:307–311` — `QUEUE`, right word deliberately
  empty (`rightWordPrefix = ""`, `rightMonoNumber = ""`).

### 1.2 Finished specification

- The HORIZON header dies with its item (:171–203). The QUEUE header at
  :303–312 is the **only** `FullBleedSectionHeader` left in the file —
  grep-verifiable acceptance: exactly one call site in
  `FocusSequencerScreen.kt` when F-3 lands.
- **Left word:** `QUEUE`, unchanged. Atkinson Hyperlegible Bold 11.5sp,
  letterSpacing 0.6sp, `onSurfaceVariant` (component :141–148).
- **Right word — exact strings.** Assembled through the component's
  existing slots, no component change: `rightWordPrefix = ""`,
  `rightMonoNumber = <count, plain decimal>` (Anonymous Pro Bold,
  `onBackground`, `useAccentOnNumber = false` — house precedent:
  Shield's headers pass counts the same way), `rightWordSuffix` carries
  the word part (Atkinson Bold, `onSurfaceVariant`), both at 11.5sp,
  right edge padded 3dp by the component (:159). The digit carries;
  the words label. Exact output:

  | Displayed leaves | Unsealed | Sealed (`isSessionActive`) |
  |---|---|---|
  | 0 | `0 TASKS` | `0 TASKS · SEALED` |
  | 1 | `1 TASK` | `1 TASK · SEALED` |
  | N ≥ 2 | `N TASKS` | `N TASKS · SEALED` |

  - The separator is `·` (U+00B7, one space each side), as the audit
    prints it — **not** the `•` bullet the dying HORIZON header uses
    at :179. Do not carry the bullet over in the rewire.
  - `1 TASK` singular is grammar, not deviation: the audit's "N TASKS"
    is the generic form; printing "1 TASKS" would be the only
    illiterate line on the screen.
  - `SEALED` wears **no accent and no new colour** — it stays in the
    suffix's word colour. The seal is already marked on every leaf by
    the perforation's solid overprint; the header word is the sentence
    (audit item 12's pairing), not an alarm. The screen's accent budget
    stays where the audit acquitted it: the head leaf's START (§3.3).
  - **No minute total anywhere on this line, in any state.**
    `computedTotalMins` (:131–133) loses its last consumer when the bar
    dies; it is deleted with F-3, not left as an unused sum.
- **Position:** unchanged — the header item stays where :303 puts it:
  after the composer item (and after the undo banner when present,
  :264–298), before the last-finished line (:322–367). The
  last-finished line does **not** move above it in the cleanup
  (audit item 5: placement stays).

---

## 2. The blank leaf — printed exactly once (audit item 6)

### 2.1 The second printing, located

`NOTHING QUEUED` is printed **twice** today on an empty queue:

1. Inside the bar itself — `BlockitTowerFadersAndCatalogViews.kt:183–188`:
   when `tasks.isEmpty()`, the 42dp strip paints `NOTHING QUEUED`
   centred at 10.5sp and returns.
2. On the held blank leaf — `FocusSequencerScreen.kt:394–399`, 15sp
   Atkinson, `onSurfaceVariant`.

Killing the bar kills printing #1 as a side effect — but the finisher
record states it as a requirement, not a side effect: after F-3,
`NOTHING QUEUED` occurs **exactly once** in the screen's rendering
(grep on the screen file: one occurrence, at the blank leaf).

### 2.2 The surviving printing, dimensioned as built — KEEP verbatim

Guard (:374): `orderedQueue.isEmpty() && tornTask == null` — keep
exactly; §5 shows why both terms are load-bearing.

- Item: `Column`, `padding(vertical = 4.dp)`, internal `spacedBy(8.dp)`,
  testTag `task_blank_leaf` (:376–381).
- Perforation row, 1dp (:383) — `sealed = isSessionActive`, so an
  empty queue under a running routine session still shows the solid
  overprint. Keep.
- Blank body: full width × **112dp** (:388–392), content centred.
  `NOTHING QUEUED`, Atkinson 15sp, `onSurfaceVariant`, weight as built
  (no bold — held paper, not a headline) (:394–399); 6dp spacer (:400);
  one 1dp rule, full width, alpha 0.18 — `#615B5A` day / `#8F8888`
  night (:401–412).
- **Total blank item height: 4 + 1 + 8 + 112 + 4 = 129dp.** It must not
  shrink in the cleanup: the pad holds its address when empty, and the
  queue→empty transition must not make the screen jump.
- The blank carries no action, as built. Nothing is added to it.

`0 TASKS` in the header (§1) and `NOTHING QUEUED` on the blank are two
different facts (a count, a pad state) — both acquitted by the audit.
What dies is the bar's duplicate of the blank's sentence.

---

## 3. Head-leaf hierarchy — no ACTIVE/QUEUE words (audit item 7)

### 3.1 As built today

The plate row (`TaskLeafItem`, :551–591) prints the state as a word:
`ACTIVE` / `QUEUE`, Anonymous Pro Bold 10.5sp, letterSpacing 1sp, head
in `onBackground`, queued in `onSurfaceVariant` (:570–577), preceded —
only when `isCued && isSessionActive` — by a 5dp filled square
(:563–569). The word is false as printed (audit item 7: ACTIVE means
"first in line" while no session runs) and duplicates position.

### 3.2 Finished plate row

- **The word dies, both branches** (:570–577). No replacement word:
  not `NEXT`, not `HEAD`, not `CURRENT` — the audit's signal list is
  closed (§3.3). The left inner `Row` (:556–569) exists only to hold
  the word and the square; it dies with them.
- **The 5dp running square dies with the word.** Its fact — a session
  is running — is now printed once, as `· SEALED` in the header (§1.2),
  and marked once, as the solid perforation overprint. A third
  printing of the same fact, floating alone at the left of a row whose
  word it accompanied, is exactly the duplication item 6 kills for
  the blank leaf. One printing of any fact.
- **What remains on the row: `#NN`, alone, at the right** — its current
  address; the eye has learned it, do not move it. Anonymous Pro 11sp,
  `onSurfaceVariant` (:579–590), format `#%02d` as built. The legacy
  `· NNm` suffix stays on the plate only where a stored estimate
  exists (`estimatedMinutes > 0`) — audit F-4 keeps it there as
  record while striking it from START; this slice does not touch it.
- The outer row keeps `SpaceBetween` geometry with the single right
  child; row height is one 11sp mono line.

### 3.3 The head's honest signals — the complete, closed list

Top to bottom inside the leaf (leaf `Column`, :524–548: end padding
`(leavesBehind * 3 + 8).dp`, vertical padding 4dp, `spacedBy(8.dp)`):

1. **Perforation** (:549) — 1dp, 3dp dash / 3dp gap, solid overprint
   while sealed. (Tone unification is F-8, not this pass.)
2. 8dp gap.
3. **Plate row** — `#01` at the right. The number itself says head;
   #01 needs no word beside it.
4. 8dp gap.
5. **Title** (:593–599) — Atkinson 15sp, `onBackground`: **Bold on the
   head leaf, Medium on queued leaves**. The leaf's largest, darkest
   object.
6. (Sub-steps, verb row — unchanged by this slice.)

Head-ness is carried by exactly four signals, all already in the code:

- **Raised paper** — head `surfaceBright` vs queued `surface` (:541).
- **Bold title** — same 15sp, weight steps (:596).
- **`#01`** — position printed as the man's own handle (audit item 8).
- **The accent verb** — START, head leaf only: Anonymous Pro Bold
  15sp, letterSpacing 0.5sp, `primaryContainer` **text**, never a fill
  (:673–691). Queued leaves get CUE in quiet 12sp (:692–703). START is
  the one accent on the pad (the undo banner's accent UNDO text is a
  pre-existing furniture exception, not a second pad accent).

### 3.4 Collision noted and ruled

The cell Designer's spec (§4.3 of designer.md) proposes a `NEXT`
marker on the head card. That contradicts audit item 7's closed list
("raised paper, bold title, #01, the accent verb"). The audit wins;
no `NEXT` in the finished surface. Flagged here so the merge does not
have to re-litigate it.

---

## 4. Spacing rhythm after the deletion (audit F-3)

### 4.1 What is deleted, counted on this branch

- `FocusSequencerScreen.kt`: the HORIZON item :168–203 (36 lines), the
  `TaskHorizonBarView` import (:61), `computedTotalMins` (:131–133),
  and the stale doc-comment lines that describe the bar (:63–69).
- `BlockitTowerFadersAndCatalogViews.kt`: `TaskHorizonBarView` itself,
  :155–257 (103 lines). Grep-verified: this screen is its **sole
  caller** in the branch. Left in the tree it would be unreachable
  code still printing `NOTHING QUEUED` into the void — the one-printing
  rule (§2) holds in code, not just on screen. Deleting it is part of
  F-3. (Audit prices F-3 at ~113 lines deleted, zero added; the exact
  split across files is the Builder's count to report, the shape is
  this one.)

### 4.2 Vertical space reclaimed — 94dp nominal, spent on nothing

The dying item's height: header internal spacer 16dp + one 11.5sp
header line (≈16dp nominal) + 8dp spacer (:186) + 42dp bar (:200) =
**≈82dp**, plus the 12dp `LazyColumn` gap that separated it from the
composer = **≈94dp nominal**. Everything below the composer moves up
by that amount **as one rigid block**. Zero replacement UI is added:
no filler, no skeleton, no smaller bar, no "removed" note. The air is
the dividend — more leaves visible without scrolling.

### 4.3 The finished rhythm, dimensioned top to bottom

`LazyColumn` frame unchanged (:158–167): horizontal padding 12dp,
vertical padding 8dp, `spacedBy(12.dp)`, bottom contentPadding 100dp
(floating-band clearance — untouched).

| # | Object | Height / gap |
|---|---|---|
| — | Top padding | 8dp |
| 1 | Composer (out of this slice; F-2 moves it in batch-10, not now): field 48dp + 10dp + ADD key 36dp | 94dp |
| — | Gap 12dp + header's internal spacer 16dp | **28dp — the screen's one big breath** |
| 2 | QUEUE header row (11.5sp line) | ≈16dp |
| — | Gap | 12dp |
| 3 | Last-finished line, only when present (one 12.5sp line) | ≈17dp + its 12dp gaps |
| — | Gap | 12dp |
| 4 | Head leaf: top pad 4dp, perforation 1dp, 8dp, plate row ≈15dp, 8dp, **title 15sp** | title top ≈ **194dp** from list top* |
| 5+ | Further leaves, inter-leaf gap | **12dp — unchanged this pass** |

\* Nominal, excluding app chrome above the list; with a last-finished
line present, ≈223dp. Cross-check against the audit: Plan measured
the head title at ~285dp down today; 285 − 94 ≈ 191 — the F-3
arithmetic and Plan's measurement agree within nominal line heights.

**Honest limit, stated so nobody overclaims F-3:** Plan's "title as
the 3rd object the eye crosses" is **not** delivered by this pass —
the composer still sits above the pad. That is F-2's recomposition
(batch-10 per the cell plan). F-3 delivers the one-header screen and
the 94dp; it does not deliver the 3rd-object count.

### 4.4 What must not collapse

1. **The header's internal 16dp spacer stays** (component :135). It is
   shared with other screens and is now the sole section break on
   this one. Do not "reclaim" it because a header died.
2. **The 28dp composer→QUEUE breath stays whole** (12 + 16). Do not
   tighten gaps to make the screen look "fuller" after the kill.
3. **Inter-leaf gap stays 12dp in this pass.** Audit item 16 / F-8
   proposes 4dp with the pad-edge re-anchor — that is the F-8 finish
   set, a separate diff. Folding it into F-3 would stop F-3 being the
   pure deletion + rewire the audit priced.
4. **The blank leaf's 129dp stays** (§2.2).
5. **Top padding stays 8dp.** The reclaimed space is above the
   composer only in the sense that the bar left; the composer does
   not crawl to the screen edge to fill it.

---

## 5. The count during the tear ghost window (audit item 8)

### 5.1 The defect as built

The tear books at the tap (G-01, :146–156): the torn leaf leaves
`orderedQueue` immediately, and a ghost snapshot is inserted into
`displayQueue` at its old index for 260ms (:109–130, delay at
:111–115). Three printings derive from three different lists today:

- `#NN` — from `displayQueue` index (:418, :582–586). **Ghost-correct
  already.**
- Header count — from `orderedQueue.size` (on the dying HORIZON
  header, :179–182). **Ghost-blind:** during the window it undercounts
  by one — the header says the torn leaf is gone while its paper is
  still visibly in hand, mid-tear.
- Blank leaf — guarded by `orderedQueue.isEmpty() && tornTask == null`
  (:374). Correct, and only because of the ghost term.

### 5.2 Finished rule — one list, one count

**The header count is `displayQueue.size`, in every state** — steady,
ghost window, empty, sealed. It is derived from the same list the
`itemsIndexed` renders and the same indices that print `#NN`, so the
three printings cannot drift apart. No second derivation may be
introduced for the header (not `orderedQueue.size`, not
`activeTask + queuedTasks`, not a stored counter).

Worked windows (acceptance, paper-checkable):

- **Tear the head of 3:** booking drops `orderedQueue` to 2; ghost
  inserted at index 0 → `displayQueue` = 3. Header reads `3 TASKS`
  for the full 260ms; ghost wears `#01`, survivors `#02`/`#03`
  (their display indices, as built). At ghost clear, header steps to
  `2 TASKS` in the same recomposition that removes the ghost —
  never a frame at 2 while 3 leaves show, never 3 while 2 show.
- **Tear the sole leaf:** `orderedQueue` = 0, ghost present →
  `displayQueue` = 1 → header `1 TASK`; blank leaf absent (the
  `tornTask == null` term at :374 is load-bearing — do not
  "simplify" the guard). At clear: `0 TASKS`, blank leaf enters.
- **Sealed:** sealing changes no list; the count source is identical,
  only the `· SEALED` suffix joins (§1.2).

---

## 6. Line-level notes — FocusSequencerScreen.kt as built

| Lines | Object | Finished verdict |
|---|---|---|
| :55 | `FullBleedSectionHeader` import | KEEP — one call remains |
| :61 | `TaskHorizonBarView` import | KILL with F-3 |
| :63–69 | File doc comment (bar, SPLIT) | REWRITE — bar sentences die with the bar |
| :99 | `effectiveNight` | KEEP — still consumed by the blank rule (:406) |
| :109–130 | Ghost state + `displayQueue` | KEEP — becomes the header count's source (§5) |
| :131–133 | `computedTotalMins` | KILL — last consumer dies with the bar |
| :158–167 | `LazyColumn` frame (12/8dp, spacedBy 12, bottom 100) | KEEP — every dimension (§4.3) |
| :171–203 | HORIZON header + 42dp bar item | KILL whole — header, 8dp spacer, AndroidView, `total_horizon_bar` tag |
| :208–261 | Composer | UNTOUCHED this pass (F-2, batch-10) |
| :264–298 | Undo banner | KEEP — position above the QUEUE header as built |
| :303–312 | QUEUE header item | REWIRE right word per §1.2; left word, position unchanged |
| :322–367 | Last-finished line | KEEP — content/placement (item 5's day-print cure is outside this slice) |
| :374–416 | Blank leaf | KEEP verbatim — sole `NOTHING QUEUED` (§2.2) |
| :418–433 | `itemsIndexed(displayQueue…)` | KEEP — `#NN` and `leavesBehind` already display-derived (:421–422) |
| :541 | Head raise `surfaceBright` / queued `surface` | KEEP (§3.3) |
| :549 | `PerforationRow(… raised = isCued)` | KEEP this pass (tone unify = F-8) |
| :551–591 | Plate row | KILL left word + square (:556–577); KEEP `#NN` right (:579–590) per §3.2 |
| :593–599 | Title 15sp, Bold/Medium | KEEP (§3.3) |
| :646–655 | `DONE — TEAR` label | UNTOUCHED — rename/48dp target is F-4/F-7, not this slice |
| :667–703 | START (head) / CUE (queued) | KEEP — the accent verb is a §3.3 signal; the CUE/START split itself is F-4 |
| Tower :155–257 | `TaskHorizonBarView` class | KILL — sole caller is this screen (§4.1) |
| Components :123–164 | `FullBleedSectionHeader` | NO CHANGE — existing slots express the finished header |

**Boundary of this slice, for the record:** F-5 seal gating of verbs,
F-7 tear respin/rename, and F-8 tones/pad-edge/gap are real audit
rulings but are **not** specified here and must not be read into this
surface — in particular, the header's `· SEALED` word names the seal;
it does not claim the leaf verbs are gated yet.

## 7. Gallery acceptance (paper checks for the Builder / Ren's compile)

1. Grep: one `FullBleedSectionHeader(` in the screen file; zero
   `TaskHorizonBarView`, `total_horizon_bar`, `computedTotalMins`,
   `HORIZON`; `NOTHING QUEUED` exactly once in the screen file and
   zero times in the views file.
2. Grep `TaskLeafItem`: zero `ACTIVE`, zero `"QUEUE"` state word.
3. State matrix §1.2: all six strings exact, `·` not `•`.
4. Ghost windows §5.2: both worked cases — header, `#NN`, visible
   leaves agree at every instant.
5. Empty sealed: `0 TASKS · SEALED` + blank leaf with solid
   perforation, 129dp tall.
6. Accent census on the pad: START's text is the only accent object.

**DECLARED — finish spec complete, uncompiled, no code written.**

— Finisher-2 (issue #12 cell)

---

## Ready-to-post comment for issue #12 (AMT posts this)

> **Finisher-2 — Tasks finished surface after the Horizon kill (F-3)**
>
> Full spec: `finisher-2.md` in the issue-12 cell work dir. Paper only,
> DECLARED, uncompiled. The headlines:
>
> - **QUEUE is the screen's one header.** The HORIZON header dies with
>   its item; QUEUE's right word becomes the count, assembled through
>   the existing header slots (mono digit + word suffix, no accent):
>   `0 TASKS` / `1 TASK` / `N TASKS`, and while a session runs,
>   `N TASKS · SEALED` — middle dot `·`, not the dying header's `•`
>   bullet. SEALED wears no accent; START keeps the pad's accent
>   budget. No minute total on the line in any state —
>   `computedTotalMins` dies with the bar.
> - **The blank leaf prints exactly once.** `NOTHING QUEUED` is
>   printed twice today: the second copy lives inside the bar itself
>   (BlockitTowerFadersAndCatalogViews.kt:187, painted when the queue
>   is empty). Killing the bar kills it; the held blank leaf
>   (112dp body, 129dp item, guard unchanged) is the sole printing.
> - **Head-leaf hierarchy, words killed.** ACTIVE/QUEUE and the 5dp
>   running square die together — the square's fact is now printed
>   once as `· SEALED` in the header. The plate row keeps `#NN` alone
>   at the right. Head-ness is carried by the audit's closed list
>   only: raised paper, bold 15sp title, `#01`, the accent START.
>   No `NEXT` marker (the Designer's proposal contradicts audit
>   item 7's closed list — flagged in the spec for the merge).
> - **Spacing: ≈94dp reclaimed, spent on nothing.** The dying item is
>   ≈82dp (16 spacer + header line + 8 + 42dp bar) plus its 12dp list
>   gap. Everything below the composer rises as one rigid block; zero
>   replacement UI. The header's 16dp spacer and the resulting 28dp
>   composer→QUEUE breath stay whole; inter-leaf gap stays 12dp this
>   pass (the 4dp move is F-8's separate diff). Head title lands at
>   ≈194dp nominal from list top (was ~285dp per Plan's measure —
>   arithmetic cross-checks). Stated honestly: F-3 does not deliver
>   Plan's 3rd-object count; that is F-2's, batch-10.
> - **Count from the displayed list, always.** Today `#NN` derives
>   from `displayQueue` (ghost-correct) while the header count
>   derives from `orderedQueue.size` (ghost-blind — it undercounts
>   by one for the tear's 260ms, declaring a leaf gone while its
>   paper is still in hand). Finished rule: header count =
>   `displayQueue.size` in every state, so header, `#NN`, and the
>   visible leaves can never disagree. Both ghost windows are worked
>   in the spec (tear head-of-3; tear sole leaf → `1 TASK`, blank
>   leaf held off by its `tornTask == null` guard, then `0 TASKS`).
> - Line-level KEEP/KILL/REWIRE table for the whole screen is in the
>   spec, including: `TaskHorizonBarView`'s class is deleted with
>   F-3 (this screen is its sole caller), and F-5/F-7/F-8 are
>   explicitly outside this surface — `· SEALED` names the seal, it
>   does not claim verb gating.
>
> — Finisher-2 (issue #12 cell)
