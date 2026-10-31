# FINISHER-4 — Issue #12, Tasks screen: every printed word, finished

*Cell: AMT (lead), issue #12, Sarthak-2007/Focus-app (Monolith). Slice:
print & words only — every string the Tasks screen puts in front of the
man's eyes, audited word by word and finished. Paper only: nothing
compiled, nothing run. Sources read in full: `studio/plan/AUDIT-tasks.md`,
`studio/plan/PLAN-issue-12-tasks-f1.md`, and the screen as built —
`FocusSequencerScreen.kt` (848 lines, branch `agent/issue-12`) plus its
components (`FullBleedSectionHeader` / `BlockitActionChip` in
MonolithCustomComponents.kt, `TaskHorizonBarView` in
BlockitTowerFadersAndCatalogViews.kt).*

**The laws applied:** one printing of any fact · words only where the
object can't speak · no spec-speak on a face · Monolith is a blocker,
not a tracker — no scores, totals, or grades of finished work, ever.

**Scope tags:** `AS-BUILT` = survives untouched · `F-3` = this pass
(audit fix list) · `F-1` = this pass · `BATCH-10` = the recomposition
pass the lead plan assigns to F-2/F-4/F-5/F-9. The list below is the
screen's FINISHED state; the tag says which pass prints it.

---

## §1 · Verifications the brief ordered (checked in code, not trusted)

- **ACTIVE / QUEUE plate words — audit's KILL verified.**
  FocusSequencerScreen.kt:571: `text = if (isCued) "ACTIVE" else
  "QUEUE"`, and `isCued` is passed as `index == 0` (line ~437). The
  word is derived from list position, never from `task.status`.
  ACTIVE is false as printed exactly as Muede charged: it means
  "first in line" while no session runs. Both words stand in code
  today; the kill is real work, not a formality.
- **"NNm" on START — verified present.** Lines 677–681: a legacy row
  prints `"START ${task.estimatedMinutes}m"`. Struck (§3) — on a verb
  the figure is a promise: it tells the man the session will be that
  long, and nothing in the app keeps that promise (MINUTES DIE; the
  Deck's dial, not the leaf, sets duration).
- **HORIZON header totals — verified present.** Lines 171–184:
  titleWord `"HORIZON"`, right word `"${orderedQueue.size} TASKS • "`
  + mono `"${computedTotalMins}m"` when the sum is > 0. Both die
  with the bar (§3); the count alone moves (§2, row 2).
- **QUEUE header as built prints NO right word at all** (lines
  305–312: prefix and number both `""`). The count and SEALED in §2
  are new print wired by F-3, not edits to existing print.
- **The blank leaf's second printing — found, inside the bar.**
  BlockitTowerFadersAndCatalogViews.kt:187: `TaskHorizonBarView`
  draws `"NOTHING QUEUED"` centred in the empty bar, ~200dp above
  the blank leaf that prints the same words. This is the duplicate
  Fremd and Plan charged (audit item 6). It dies with the bar; after
  F-3 exactly one printing remains, on the leaf.
- **LAST-FINISHED as built** (lines 343–348): `"FINISHED HH:mm"` —
  no day, any age. Fremd's charge verified: a finish from yesterday
  reads as today's time. Cure in §4.1.
- **Verbs as built are NOT seal-gated in print:** DEL (line ~706),
  SPLIT (~659), CUE (~694), START (~678) print regardless of
  `isSessionActive`; only the tear withdraws (line ~645). F-5's
  absence rule is therefore part of several rows' final form below.

---

## §2 · SURVIVES — the finished word-list

Every string that remains on the finished screen, in its exact final
form. `{…}` = a value slot; the words around it are literal.

| # | Final form | Where it prints | Scope | Note |
|---|---|---|---|---|
| 1 | `QUEUE` | The screen's one section header (title word) | AS-BUILT | Names the pad. Not to be confused with the killed plate word (§3.6) — same letters, different job: this one names a place, that one restated a position. |
| 2 | `1 TASK` / `{N} TASKS` / `{N} TASKS · SEALED` / `SEALED` / *(nothing)* | QUEUE header, right word | F-3 | Full grammar in §4.2. Count sourced from the DISPLAYED list (ghost included during the tear window — audit item 8 — so print never disagrees with the paper in hand). Number–word agreement is part of a finished form: `1 TASK`, never `1 TASKS`. |
| 3 | `{task.title}` — verbatim | Leaf title; last-finished line (struck); undo banner (after `REMOVED: `) | AS-BUILT | The man's own words. Never re-cased, trimmed on print, or summarised. The app adds nothing to them and takes nothing away. |
| 4 | `#{NN}` — zero-padded, `#01` | Leaf plate, right | AS-BUILT | The man's handle for a leaf (audit item 8, kept). During the ghost window it derives from the displayed list, ghost included. |
| 5 | `#{NN} · {NN}m` | Leaf plate, right — legacy rows only (`estimatedMinutes > 0`) | AS-BUILT | The plate's minutes SURVIVE as record (audit F-4, Dens): they state what a pre-order row once carried. A record on the plate is a fact; the same figure on START is a promise — that asymmetry is the whole ruling. Name-only rows print row 4 alone. |
| 6 | `{step.title}` — verbatim | Sub-step line on the leaf | AS-BUILT | Quiet print, acquitted (audit item 10). Strike is the state; no word changes. |
| 7 | `{NN}` — `twoDigitCount`, `01`… | Sub-step number, right of the step line | AS-BUILT | The shared two-digit voice (DRW-08; never `010`). No fraction, no `2 OF 5`, no step score prints anywhere — verified absent in code, and it stays absent. |
| 8 | `TEAR` | Head of the leaf's verb row | BATCH-10 (F-4 rename) | Renamed from `DONE — TEAR` (§3.8). One act, one word; the perforation already teaches the gesture. Printed only while unsealed; absent — not greyed — while sealed (already true as built). |
| 9 | `SPLIT` | Leaf verb row, leaves with no sub-steps | AS-BUILT word; referent already fixed | The word survives because its referent is now honest: HAND-SPLIT is landed behaviour (lead plan, Critic P2) — SPLIT opens blank ruled lines via `onAddSubStep` and the man writes his own steps. No count ever rides it (the `+ SPLIT 3` form is dead, §3.11). Gated by seal under F-5: absent while sealed. |
| 10 | `START` — bare, no figure | Head leaf only, accent text | BATCH-10 (F-4) | The screen's one commit verb and its one accent. Final form sheds the legacy figure (§3.7) and gains its true referent (arms via the Deck; CUE no longer teleports). Absent while sealed (F-5). |
| 11 | `CUE` | Non-head leaves | AS-BUILT word; referent fixed BATCH-10 (F-4) | Word unchanged; meaning narrows to the truth: make this leaf the head, STAY on Tasks. The secret preempt-and-teleport dies, not the word. Absent while sealed (F-5). |
| 12 | `DEL` | Leaf verb row | AS-BUILT | Lawful bare because the undo banner exists (audit item 15). Absent while sealed (F-5). |
| 13 | `ADD` | Composer key | AS-BUILT; moves BATCH-10 (F-2) | Under F-2 ADD becomes the end-key on the composer leaf at the pad's foot: write the name, ADD lands it at the foot — the pad's natural next leaf. Word unchanged. |
| 14 | `FIRST` | Second key on the composer leaf | BATCH-10 (F-2) — NEW word | Lands the leaf at the head. `ADD`/`FIRST` is the whole vocabulary the composer needs: two keys, two destinations, no third word. |
| 15 | `REMOVED: {title}` | Undo banner, left | AS-BUILT | Past tense, names the act and the leaf. One line, ellipsis on overflow — the title slot truncates, the word never does. |
| 16 | `UNDO` | Undo banner, accent chip word | AS-BUILT | The way back, one word. The banner carries NO dismiss word — no `DISMISS`, no `✕` (§3.12): it is state-only print and leaves when the undo store clears; F-9 wires `onDismissUndoBanner` as machinery, not as a new string. |
| 17 | `FINISHED …` + `REOPEN` | Last-finished line | AS-BUILT words; day-print cure §4.1 | Content untouchable per Sarthak's ruling — title, FINISHED word, REOPEN all stand. Only the time's grammar is cured. `REOPEN` composes only while unsealed (as built); while sealed the line is pure record. |
| 18 | `NOTHING QUEUED` | The held blank leaf, 15sp | AS-BUILT | Its ONE printing (the bar's twin dies, §3.4). The pad keeps its address when empty; the words name a state the blank paper alone could be misread as — a loading gap, a bug. Position + perforation + this word = unambiguous. |
| 19 | `{intention}` — the bare string, verbatim | Session intention line (Deck headline / receipt / Drawer — ruled here because F-1 rewrites what the string IS) | F-1 | Full ruling in §4.3: identical print form whether the string came from the queue head or the routine name; provenance is never printed. |
| 20 | `TASKS` | Rail word (app chrome, present on this face) | AS-BUILT | Boundary row: navigation furniture, not the screen's own print. Names the tab the man is standing on; one printing, no ruling needed. |

**Non-word print that carries word-jobs (recorded so nobody re-adds
words for them):** head-ness is carried by raised paper, bold title,
`#01`, the accent on START, and the 5dp running mark beside the plate
while sealed — which is exactly why §3.5–3.6 can die. The seal is
carried by the perforation's solid overprint plus header word
`SEALED` (§4.2). The tear's ghost leaf (260ms) prints the dying leaf's
title and `#NN` as record; its verbs are disabled in that window —
motion furniture under F-7's respin, no word change ruled.

---

## §3 · KILLED — every string that dies, with its reason

| # | Killed string (as built) | Where | Reason |
|---|---|---|---|
| 1 | `HORIZON` | Section header, line 173 | The section dies with its bar (audit item 1). It is also spec-speak: "horizon" is the design document's name for a workload chart — the man has a queue, not a horizon. No replacement header: QUEUE becomes the screen's one header. |
| 2 | `{N} TASKS • {NNN}m` (as the HORIZON right word) | Header, lines 177–183 | The minute total is a tracker total of a dead quantity — proportions floored at a phantom 10 per name-only task. It dies whole. The count does NOT die: it moves to the QUEUE header (§2.2) — one printing of that fact, in its true home. |
| 3 | `{NN}m` segment figures inside the bar | TaskHorizonBarView, `drawMixedWordAndMono` suffix `"m"` | Die with the bar. A proportional chart of minutes the composer no longer asks for is a lie in figures; figures are print. |
| 4 | `NOTHING QUEUED` (bar's copy) | TaskHorizonBarView:187 | Second printing of the blank leaf's fact (one-printing law), ~200dp above the first. Dies with the bar; the leaf's printing (§2.18) is the survivor. |
| 5 | `ACTIVE` | Leaf plate, line 571 | False as printed: derived from index, not status; means "first in line" while no session runs. Position, raised paper, and bold type already say head-ness — the word only adds a claim the screen can't back. No replacement. |
| 6 | `QUEUE` (plate word) | Leaf plate, line 571 | The same fact as position, printed a second time on every non-head leaf. The header's `QUEUE` (§2.1) survives — it names the pad; the plate word merely echoes where the leaf already visibly sits. No replacement. |
| 7 | `START {NN}m` | Head leaf verb, lines 677–679 | A promise on a verb. The figure pledges a duration the app neither asks for nor enforces; on legacy rows it pledges a number from a dead regime. START stands bare (§2.10); the figure survives only as plate record (§2.5). |
| 8 | `DONE` (in `DONE — TEAR`) | Leaf verb, line 648 | Tracker vocabulary on a blocker's screen: DONE grades the work ("done" is a judgement), and it double-names an act the perforation already teaches. The act is TEAR — a physical word for a physical gesture, booking at the tap. Final form §2.8. |
| 9 | `Add single-line focus task...` | Composer placeholder, line 224 | Spec-speak twice over: "single-line" is an implementation constraint (`singleLine = true`, IME-Done) and "focus task" is the entity's name, not the man's. The composer leaf's position on the pad and its ADD/FIRST keys say everything a placeholder could, in objects instead of instructions. Killed with NO replacement — the field prints nothing until the man writes; what he writes is the print. |
| 10 | `FROM QUEUE` / `· NO QUEUE` / any provenance word on the intention line (Designer's `ROUTINE 05:00 FASTED · FROM QUEUE` family) | Proposed, never built | Never printed — ruling in §4.3. The provenance column is PARKED (lead plan ruling 3); with no column, a printed provenance would be recomputed at print time and would be a guess for every pre-F-1 row. A guess in print is a lie with good typography. |
| 11 | `No task queued` / `— No task queued —` as an intention headline | Proposed by the Designer for the empty-queue seal | Killed by the lead (Amt overruled; lead plan ruling 1) and confirmed here on words grounds: the string would be ARCHIVED as the session's intention in the Drawer — an absence filed as a fact, corrupting the record. The empty-queue session's intention line reads the routine name (§4.3), because that session IS the routine block; the queue merely contributed nothing. |
| 12 | Any dismiss word on the undo banner (`DISMISS`, `✕`, `CLOSE`) | Not present; pre-emptively ruled | The banner is state-only (audit item 3). F-9 wires its dismiss callback; no new string is authorised for it. Print that exists only to manage other print is furniture noise. |
| 13 | `FINISHED {HH:mm}` — as the form for a NON-today finish | Last-finished line, lines 344–347 | False by omission: yesterday's 14:03 printed bare reads as today's. The bare form survives for today's finishes only; the cured grammar is §4.1. (The FINISHED word itself is untouchable — §2.17.) |

*Not print, recorded to prevent a false kill: the file's doc comment
still advertises a "`+ SPLIT 3`" splitter. Comments are not a face;
no ruling. The on-screen SPLIT is bare (§2.9).*

---

## §4 · The ruled forms, in full

### 4.1 · LAST-FINISHED — the day-print cure

Content untouchable (Sarthak's ruling: the line exists to undo a
mistaken finish); only the time gains its day when the finish wasn't
today. Day boundary = the device's local calendar day of
`completedAtMillis` vs now. Exact forms (Anonymous Pro, as built;
day/month tokens `EEE d MMM`, Locale.US, uppercased on print):

- **Today:** `FINISHED 14:03` — unchanged from as-built.
- **Yesterday:** `FINISHED YESTERDAY 14:03` — the man's own word for
  the day; instantly read at 1 AM, no date arithmetic asked of him.
- **Older, same year:** `FINISHED WED 28 OCT 14:03` (pattern:
  `FINISHED {EEE d MMM} {HH:mm}`).
- **Older, different year:** `FINISHED WED 28 OCT 2025 14:03`
  (pattern: `FINISHED {EEE d MMM yyyy} {HH:mm}`).

No other change to the line: title struck verbatim on the left,
`REOPEN` at the right while unsealed, absent while sealed. One slot,
latest finish only, never accumulated — the line is a way back, not
a history, and it prints no count of finishes anywhere (the old
`DONE n` tally that once rode the QUEUE header is already gone in
code and stays gone: counts of finished work are tracker talk).

### 4.2 · QUEUE header — count and SEALED, the whole grammar

Right word of the QUEUE header (§2.2), by state. Count = size of the
DISPLAYED queue (ghost included during the tear window):

| Queue | Unsealed | Sealed (session running) |
|---|---|---|
| Empty | *(nothing)* — `NOTHING QUEUED` on the leaf already states it; a `0 TASKS` here would be the same fact printed twice, in numbers | `SEALED` — the seal fact must be said once on this screen, and the header is its sentence (audit item 12) |
| One | `1 TASK` | `1 TASK · SEALED` |
| N ≥ 2 | `{N} TASKS` | `{N} TASKS · SEALED` |

`SEALED` is the seal's only printed word on this screen — one word,
one place. Separator is the house middle dot with spaces (` · `, as
in ` · AS SEALED` on the Shield's warrant line). The word prints in
the header's word voice (Atkinson, as the count does), never as a
mono badge and never in accent: the seal is a state, not an alarm.

### 4.3 · The routine-sealed intention line — provenance is never printed

The constraint: F-1 writes ONE string field. `intention =
headTaskTitle ?: routine.name` (lead plan) — and the provenance
column is PARKED, not in this cell's code. The words ruling under
that constraint:

**The intention line prints the string alone — verbatim, bare, with
no prefix, no suffix, no badge, no quotation marks, no source word —
identically, whichever source filled it.**

- **Intention from the queue head:** the line reads exactly what the
  man wrote on the leaf — e.g. `Physics — Rotational Mechanics PYQs`
  (the Designer's worked example). Nothing added. The words are his;
  any frame around them would be the app commenting on his choice.
- **Intention from the routine name (empty / DEFERRED-only queue):**
  the line reads exactly the routine's name — e.g. `FASTED BLOCK`.
  Nothing added. This is not a fallback costume: the session IS the
  FASTED BLOCK; the queue contributed nothing, and the name is a
  fact, not a fabrication (lead plan ruling 1).
- **Provenance (`FROM QUEUE`, `FROM ROUTINE`, the routine name as a
  sub-line under a queue title, seal-source of any kind): NEVER
  printed** — not on the Deck, not on the receipt, not in the
  Drawer. Three grounds: (1) *No column, no print* — with provenance
  parked, any printed source would be derived at print time and
  unverifiable for rows sealed before F-1; print that can be wrong
  about the past doesn't ship. (2) *One printing* — the routine's
  name and hour already print on the Shield/routine surfaces in
  their own right; repeating them under the intention to explain
  the intention is the same fact twice. (3) *The string is the
  record* — the Drawer archives the intention as searchable print;
  a provenance suffix would be archived INTO the string's reading
  and pollute every future search and receipt.
- **Collision accepted:** if the head's title happens to equal the
  routine's name, the two cases print identically. Ruled correct —
  the string is true under either source, and distinguishing them
  is precisely the parked column's job, if a later batch ever buys
  it. If that column lands, this ruling reopens for the Drawer only;
  the Deck headline stays bare regardless (a session's face carries
  what the work is, never its paperwork).
- **What never appears in the string's place:** `No task queued` in
  any casing or framing (§3.11), a generated category word
  (`Study`, `Focus`, `Session`), or the previous session's intention
  carried over. The fallback is the routine name or nothing seals —
  and sealing never waits on words: an empty queue changes the
  string, never the seal.

---

## §5 · Close

Census complete: 20 surviving print forms (§2, incl. the two ruled
grammars), 13 killed forms (§3), every string on the screen as built
accounted for — headers, bar, composer, banner, leaves, verbs, plate,
blank leaf, and the F-1 intention string. Nothing on the finished
screen totals, scores, or grades finished work; every fact prints
once; every verb says one thing and does that thing.

**DECLARED** — paper only, uncompiled, per standing orders. Ren
compiles when Sarthak says.

— Finisher-4 (issue #12 cell)

---

## Ready-to-post comment for issue #12 (AMT posts this)

```markdown
**Finisher-4 — print & words finish, Tasks screen (DECLARED, paper only)**

Every printed word on the Tasks screen audited against the screen as
built (`FocusSequencerScreen.kt` + components) and finished under:
one printing of any fact · words only where the object can't speak ·
no spec-speak · blocker, not a tracker.

**Verified in code:** ACTIVE/QUEUE plate words stand at line 571,
derived from `index == 0`, never from status — the audit's kill is
real. `START {NN}m` stands at 677–681. The blank leaf's second
printing is inside the bar (`TaskHorizonBarView:187`).

**Survives (final forms):** `QUEUE` header with right word
`1 TASK` / `N TASKS` / `N TASKS · SEALED` / `SEALED` alone when
sealed-empty / nothing when empty-unsealed (count from the displayed
list, ghost included) · title & step titles verbatim · `#NN` plate,
with legacy `· NNm` kept on the plate ONLY, as record · step numbers
in the shared two-digit voice, no fraction ever · `TEAR` (renamed) ·
`SPLIT` (bare; hand-split referent already landed) · `START` bare,
head only · `CUE` (make-head, stay) · `DEL` · composer keys `ADD` +
`FIRST` (F-2) · `REMOVED: {title}` + `UNDO`, no dismiss word ·
`NOTHING QUEUED` on the blank leaf only. All leaf verbs absent —
not greyed — while sealed (F-5).

**Killed:** `HORIZON` + its `N TASKS • NNNm` total and the bar's
segment figures (tracker totals of a dead quantity; the count moves
to QUEUE) · the bar's `NOTHING QUEUED` (second printing) · plate
`ACTIVE` (false: means first-in-line) · plate `QUEUE` (position
restated) · `START NNm` (a promise on a verb) · `DONE` in
`DONE — TEAR` (grades the work; the act is TEAR) · the placeholder
`Add single-line focus task...` (spec-speak; killed with no
replacement — the leaf and its keys speak).

**LAST-FINISHED day-print cure** (content untouchable per Sarthak's
ruling): today `FINISHED 14:03` · yesterday `FINISHED YESTERDAY
14:03` · older `FINISHED {EEE d MMM} 14:03` · other year adds
`yyyy`. REOPEN unsealed-only, as built.

**Intention line (F-1):** the string prints BARE and verbatim,
identical in form from either source — queue head: the man's title
exactly as written; empty queue: the routine name exactly (a fact,
not a fabrication). **Provenance is never printed** — no `FROM
QUEUE`, no routine sub-line: the provenance column is parked, so a
printed source would be a print-time guess for pre-F-1 rows; the
routine's name/hour already print on their own surfaces; and the
Drawer archives the string clean. `No task queued` as an intention
is killed — it would be archived as a fact. If the column ever
lands, this reopens for the Drawer only; the Deck headline stays
bare regardless.

Full word-list: finisher-4.md in the cell work dir. DECLARED —
uncompiled; Ren compiles when Sarthak says.

— Finisher-4 (issue #12 cell)
```
