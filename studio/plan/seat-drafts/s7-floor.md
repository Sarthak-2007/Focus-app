# MASTER-PLAN §7 — draft (seat draft, for merge)

*Drafted by Floor — Designers, floors/audit. Operative spec:
plan/GUARD-RULING-AND-DRAWER.md §2 (Sana's ruling, panel: Floor on the
record side), under MASTER-PLAN §0 law. This draft states the ruling
as §7 of the master plan; it does not relitigate it.*

---

## 7. LEDGER — THE DRAWER

**Floor:** GUARD-RULING-AND-DRAWER.md §2, as the operative spec,
over every earlier Ledger form wherever they collide — the Bound
Book faces of the kept-tab rebuild, DESIGNERS-PLAN §9, and Humans'
HPA-33…42 as disposed in §7.9 below. **One sentence:** the stop he
kept opens a storage drawer, not a report card — every task he
finishes is stored permanently, by month, searchable, and nothing in
it scores, sums, grades, or summons him.

**The DEMOTE (DESIGNERS-PLAN §9, carried by Critics' merge
condition 6) is superseded by his verdict — Ledger: KEEP THE TAB,
REBUILT (Oct 29, 05:41) — filed openly so the merge audit can charge
the change to its true author: him (§0.1-6's form).** What the kept
tab became is then his again, in his own words (Oct 29, 07:39):
*"this app is not a study tracker"* — finished tasks are stored
somewhere he can open after a month and look at what he did. Study
tracking stays YPT's and GMC's job. This section is that storage.

### 7.1 Sealed-state law (this surface)

While a session is sealed: the Drawer is read-only furniture. It
can be read — a stored row and its detail may be opened and read in
full — but search input, reopening, and every edit are inert until
the session ends. Nothing in the Drawer accepts input, offers a
decision, or changes state mid-session. This is the §0 standard-2
law stated for this surface, and it is stricter than reading alone:
the search field renders as print while sealed and answers nothing.

### 7.2 What is stored — the one content law

The Drawer reads **completed task rows only** — never the session's
`completedTasksSummary` string, which would be the same fact in a
second form. When a task becomes **COMPLETED**, retain:

- the task's internal ID, for identity only;
- **title**, exactly as it stood at completion;
- **category**;
- **notes / substeps**, exactly as stored with the task;
- **estimated minutes**;
- **actual seconds spent**;
- **completion timestamp** — the sort key and the stored date/time;
- **completed-in-session ID**, as internal provenance only; it is
  not printed as a score or code.

Nothing else is Drawer content. Sessions, holds, deflections, and
breaks are not stored here (§7.8); the finish receipt remains their
moment.

**Retention is permanent.** A stored task is never auto-deleted,
expired, pruned, or cleaned up by age — years of finished tasks
stay (his add-on, Oct 29, 07:38: *"Even after years etc."*). There
is **no Clear control** for completed tasks in Tasks or in the
Drawer, and no code path in the Drawer that deletes by date.

**Completion is not an achievement event.** It earns no point,
streak, badge, percentage, or total. If a task is deliberately
reopened, it is no longer finished and leaves the Drawer; if it is
completed again, it returns under the **new** completion timestamp.
(The reopen act's home surface is not named by the ruling — §7.10,
flagged, not invented here.)

### 7.3 How the Drawer opens

- **Header:** `STORED`, with one search field: `TYPE TO FIND A
  TASK`. The field's touch height meets the 48dp floor (REGISTER,
  touch floors) — retrieval is the Drawer's one act, and it is not
  specced below the app's floor.
- **Default view:** the current month's finished tasks, newest
  first, grouped under plain date headings such as `THU 29 OCT`.
  A date heading is a shelf label only — no total, grade, or
  verdict follows it. Today is merely the newest date group, and
  only if a task was finished that day.
- **Browsing by month:** an `ALL MONTHS` index lists one row for
  each month that has at least one stored task — `OCTOBER 2026`,
  newest month first, **label only**. No count or total rides on
  the row, and a month with nothing stored is simply absent.
  Opening a month shows its stored tasks by date, newest first.
  A month here is an **address** for finding things, not a
  measurement scope.
- **Paging:** within a month, show the newest **50** entries;
  `EARLIER` at the foot loads the preceding 50 (REGISTER: DRAWER
  PAGING 50). The repository pages by completion timestamp and
  never needs to materialise years of rows at once. Storage is not
  the constraint — a task is a line of text; the design constraint
  is that the list stays light in the hand.

### 7.4 Row and detail

- **Row:** task title (up to two lines), category, finish time,
  and time spent. No estimate on the row, no checkbox, no rank,
  no progress bar, no comparison.
- **Detail:** tapping a row opens its stored form — full title,
  category, finish date and time, estimate and actual time as two
  separate plain facts (**never a delta or percentage**), and the
  stored notes/substeps. The detail is read-only (and, while
  sealed, inert per §7.1).

### 7.5 Search — what "type stuff" means

**Interpretation, flagged (the ruling's own flag, carried):** the
most plausible reading of his *"and type stuff"* is that, after a
month, he wants to type words to find an old task. The conservative
build includes search and nothing more speculative.

- Search covers **title, category, notes, and substep text**.
- It searches **all stored tasks across all years** (REGISTER:
  DRAWER SEARCH, all years) — not queued work, not future work,
  and not only the month currently open. Permanence is only useful
  if the oldest line is as findable as yesterday's.
- Results stay in completion-date order, with the **full date
  including year** on every result. No relevance score and no
  result count are printed.
- The Drawer does **not** gain post-completion annotations or a
  journal in this build. Notes already stored with the task are
  shown as part of that task. If "type stuff" meant "write a note
  on an old task," that is a separate one-field feature to add
  only on his word; it is not silently inferred here.
- This search supersedes Old Tom's earlier search kill for this
  object: that kill assumed an auditor asking only "today?" and
  "this week?" — the frame he has now replaced. Search here is
  retrieval, not tracking.

### 7.6 Empty states and chrome silence

- **Empty states:** before anything is stored, `NOTHING STORED
  YET`. For a failed search, `NOTHING STORED UNDER “<query>”` —
  absence in words, never a zero. A day with no finished tasks
  produces no line at all: no zero, no empty grade, no
  manufactured absence line.
- **Chrome:** no badge, count, dot, or notification on the Ledger
  stop, and no "new" marker anywhere in the Drawer. The Drawer
  never summons him (HPA-38, §7.9). The stop's name in the band
  stays `LEDGER` (REGISTER: BAND — DECK · SHIELD · TASKS ·
  LEDGER + PARK); the Drawer is the face that stop opens, and
  its header is `STORED`.

### 7.7 The declaration word

If he explicitly declared **REST / ILL / FAMILY** for a date, that
word may be stored as a date note and printed beside that date
heading. It is **his authored word**, not an attendance result.
Per his verdict (Oct 29, 07:23 — Floor/Designers Q6, §0): the word
is **changeable or clearable freely all day**; it locks only when
the day rolls over — the record stays correctable while the day is
alive. An undeclared day creates no Drawer line. The word is never
converted into a number and never folded into a total (§7.9,
HPA-35's surviving law).

### 7.8 What the Drawer does not hold

- **Sessions, holds, and deflections — not Drawer content.** The
  finish receipt remains their moment. The Drawer does not become
  a disguised session-history or attendance feed.
- **Breaks and the hold spend — not Drawer content.** They survive
  in the record store only, at their own surfaces (HPA-37, §7.9);
  no break minute and no UNSPENT/spent state prints in the Drawer.
- **DAY CLOSED — not a Drawer entry.** It may remain a moment
  elsewhere in the app, but the Drawer stores finished tasks, not
  a judgement that a day is complete.
- **Aggregates — none.** No day total, week total, month total,
  season total, all-time count, streak, percentage, chart, or
  rank prints anywhere on this surface.

### 7.9 The Bound Book faces — dispositions

- **TODAY face — dies as a landing.** There is no day total at
  hero size and no grade for today. Today survives only as the
  newest date group in the current month's view, when a task was
  finished that day.
- **WEEK face — dies as a face.** No week line, no seven-day
  total, no week-on-week comparison, no attendance wording.
  Loading older entries in 50-chunks is plumbing, not a printed
  week scope. (§0.1-6's "the weekly line lives inside the tab"
  clause does not survive into this section — the Drawer ruling
  is the later, operative text.)
- **MONTH — survives as address only.** The month index is how a
  permanent drawer is browsed; a month label is a shelf name,
  never a subtotal, grade, or summary. Retrieval, not a third
  scope of measurement.
- **Aggregates — die** (see §7.8).
- **DAY CLOSED — not a Drawer entry** (see §7.8).
- **TEST-day marking — dead in the Drawer.** No `TEST` label,
  marker, special row, or search tag is created for a test day.
  This withdraws the **record-side effect** of the earlier
  TEST-line ruling (§0, Oct 29, 07:23): there is no longer a day
  book for the word to head. A task actually finished on that
  date is stored under its date like any other task — nothing
  more is inferred. (The Deck's Saturday face, §2.5, is not a
  Drawer surface and is untouched by this disposition.)
- **Bound Book discipline that survives:** flat ruled surfaces,
  real words, no orange, no charts, no streaks, no percentages,
  no season or all-time totals, no summons. The book's beauty
  survives as a quiet drawer of ruled slips; its day/week
  judgement machinery does not.

### 7.10 HPA-33…42 — dispositions (Humans, HUMANS-PHASE3 §1–2)

Each amendment is ruled for the Drawer only. The original texts
stand in HUMANS-PHASE3.md; nothing here rewrites them.

- **HPA-33 (the tab opens on TODAY) — DIES as a landing.** The
  Drawer opens on the current month (§7.3), not on a day face.
  Its protective intent — the depleted wanderer is not met by a
  grid — is met structurally: what he is met by is a shelf of
  stored tasks under date labels, with no sum, grade, or grid
  anywhere on it.
- **HPA-34 (the record has no future tense) — SURVIVES as print
  discipline.** Nothing in the Drawer is future: no planned rows,
  no projected anything, no cells for days not yet lived. Only
  completed tasks exist here, and a task exists only once it is
  finished — the Drawer is a past-tense object by construction.
- **HPA-35 (words ride inside every aggregate) — SURVIVES only
  in its reduced form:** *no total may eat a declared word —
  because no total prints.* There is no aggregate left for a word
  to ride inside; the underlying law stands whole (§7.7).
- **HPA-36 (two scopes — day and week — no all-time anything) —
  DEAD.** Both of its scopes die with their faces (§7.9). Its
  prohibition survives inside §7.8's aggregate ban, but the
  two-scope frame itself is not the Drawer's: the month is an
  address, not a scope, and the Drawer's reach is all years —
  as storage, never as a total.
- **HPA-37 (breaks named on the day line) — NOT DRAWER
  CONTENT.** There is no day line in the Drawer for breaks to be
  named on, and no sealed number here for break minutes to hide
  inside. The break minutes and the hold's UNSPENT/spent state
  survive in the record store only — the receipt's and the
  session record's facts, at their own surfaces.
- **HPA-38 (the tab never summons) — SURVIVES whole.** No badge,
  no dot, no count on the nav word LEDGER; no notification from
  the record, ever; the Drawer is pull-only at every hour, and
  the pull is always his (§7.6).
- **HPA-39 (no week sum over a gap) — DEAD with the week face.**
  There is no week sum to withhold. Its morning-after protection
  is inherited in form: gap days print nothing in the Drawer
  unless a task was finished on them or he declared a word for
  the date (§7.7) — absence is never rendered.
- **HPA-40 (Saturday renders as the word TEST only) — DEAD by
  his kill.** No TEST marking of any kind is created in the
  Drawer (§7.9). The word-only day line and the amended
  TEST-with-sessions-beneath form both belong to the dead day
  book; neither lands here.
- **HPA-41 (the week face, built for the rested auditor) — DEAD
  with the face.** The rested look-back it was built for survives
  as the Drawer's whole purpose — after a month, open it and look
  at what he did — but it is served by the month shelf and search,
  not by a week face, an appointed audit room, or any control
  that must justify itself to an audit.
- **HPA-42 (one pipeline, or the tab closes) — SURVIVES as the
  Drawer's one-pipeline law.** The Drawer renders only from the
  completed-task rows (§7.2) — the same stored rows, one
  definition per fact, one rendering per fact. It never renders
  the session's `completedTasksSummary` string, and any fact that
  cannot trace to a completed-task row does not render here.

### 7.11 Acceptance checks (this surface — the ruling's seven)

1. A task completed today appears in the Drawer once, under
   today's date, and in no second rendering.
2. Thirty days later, typing a distinctive word from its title or
   notes finds it without scrolling through a month of entries.
   Years later, the same search still reaches it, and the
   `ALL MONTHS` index reaches its month in two taps from the
   Drawer's landing.
3. No stored task is ever removed by age, housekeeping, or a
   retention job; there is no code path in the Drawer that
   deletes by date.
4. No surface in the Drawer prints a day total, week total, month
   total, streak, percentage, chart, rank, or all-time count.
5. A day with no finished tasks produces no zero, no empty grade,
   and no manufactured absence line.
6. A declared day word remains a word. It is never converted into
   a number or folded into a total.
7. Nothing in the Drawer calls him back: no badge, no count, no
   notification, no "new" marker.

### 7.12 Flagged, not resolved in this draft

- **§2.6's canary filing clause** ("breaches file as machine-fault
  events … in the rebuilt Ledger tab") collides with this
  section's content law: the Drawer holds completed tasks only,
  and §2.6 itself excludes canary events from every Ledger record
  of his behaviour. This draft follows the Drawer ruling — no
  machine-fault entries in the Drawer — and flags the clause for
  Ren/Sana to strike or re-home; it is not silently resolved
  here.
- **The reopen act's home** (§7.2) is unnamed by the ruling. The
  effect is law; the surface is an open build question, carried
  to Sana by letter with this draft.

**Build status at filing:** nothing in §7 is built yet. The nav
stop exists under the name LEDGER; the Drawer face, its month
index, 50-paging, and all-years search are the rebuild this
section specs. Where the shipped app differs, this section — not
the shipped app — is the record.

*— Floor, Designers (floors/audit), seat draft for MASTER-PLAN §7.*
