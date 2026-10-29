# §7 — FINDABILITY + PERMANENCE (Old Tom's part)

*For MASTER-PLAN §7, to be merged by Sana with Floor's body. Seat:
Old Tom, Designers (Veteran). Filed 29 Oct 2026, ~08:07 IST.*

*Scope, stated so the merge does not blur it: Floor owns the Drawer's
body — surface, targets, contrast, operability. This file owns two
promises only: **a stored task can be found again**, and **a stored
task is never lost by the machine**. Where those promises touch
Floor's surface I name the touch; I do not redesign his surface.*

*Authority: MASTER-PLAN §0 (the three standards; his verdict "keep
the tab, rebuilt"; the record side restated as the Drawer) and
GUARD-RULING-AND-DRAWER §2, which is the law this part tests. Build
facts are taken from DIGEST-AND-DRAWER-BUILD-BRIEFS Brief B — the
queries as actually specced there, not as I might wish them.*

*My own history with this object, on the page once: I killed search
for this record (K12, my Ledger invention file) on the ground that an
auditor's questions were "today?" and "this week?" — faces, not
queries. His words replaced that frame: he wants to open the Drawer
after a month and **type stuff**. Sana's ruling records the
supersession and she is right to. A kill argued for a different
question does not survive the question changing. I do not relitigate
it below; I build the test the new frame has to pass.*

---

## 1. THE THIRTY-DAY TEST — one typed word

The ruling's promise, in his words' plain form: a task finished today
is findable thirty days later by typing one word. Written as a test,
not a hope:

**Plant (day 0, on device, through the real Tasks path — no test
harness shortcuts):** finish four tasks, each carrying one distinctive
word in exactly one searched field and nowhere else:

| Plant | The word lives in | Example shape |
|---|---|---|
| P1 | title only | a word he would actually type |
| P2 | notes only | a word absent from the title |
| P3 | a substep only | a word absent from title and notes |
| P4 | category only | the category word itself |

**Retrieve (day 30 — or, in the build's accelerated form, the same
four rows re-stamped `completedAtMillis` −30 days and −3 years; the
query path cannot tell the difference, and that is the point):**
open the Drawer, tap the search field, type the one word.

**Pass, all four plants, every clause:**
1. The plant appears in the results with **no scrolling through the
   month** — search is the path, not the month list.
2. The result group heading prints the **full date including year**.
   A hit he cannot date is a rumour, not a retrieval.
3. Results are in completion-date order, newest first. No relevance
   order, no score — a ranking he cannot predict is a second search
   hidden inside the first.
4. **No result count is printed** — not before, not after, not as a
   footer. (The honest way to keep this clause at scale is §5.1
   below, not silence about truncation.)
5. A word stored nowhere returns exactly
   `NOTHING STORED UNDER “<query>”` — absence in words, never a zero,
   never an empty screen that could mean "still loading."

**Pre-committed consequence** — a test without one is a comment, and
I have written that sentence too many times to exempt myself: if P2
or P3 fails, the search is reading fewer fields than the ruling
promises and §7 does not ship until the query covers title, category,
notes **and** substep text. If P1 fails, nothing else in this file
matters until it passes.

*Sealed-state note, carried per §0.1-5:* while a session is sealed the
Drawer reads but the search field accepts nothing (Brief B step 7).
That is HOLD law and it stands — but it must be stated in §7, because
it is the one hour of the day when the thirty-day promise is lawfully
suspended, and a promise with an unstated exception is how this app
got its first set of lies.

## 2. THE ALL-MONTHS TEST — two taps, any month, any year

1. From the Drawer landing: tap `ALL MONTHS` (tap 1), tap the month
   row (tap 2). The month's stored tasks are on screen. **Two taps is
   the whole budget** — a third tap, a scroll through a calendar
   grid, or a year-picker fails it. The ruling killed the calendar
   grid; this test is where that kill is enforced.
2. The index lists **one row per month that holds at least one stored
   task, newest month first, label only** (`OCTOBER 2026`). No count
   rides on the row. A month with nothing stored is absent — the
   index is an address book, and an address book does not print
   houses nobody lives in.
3. The index is derived from the stored rows themselves
   (`getStoredMonths()`, Brief B step 2) — never from a separately
   maintained list that could drift from the rows. One pipeline: if a
   month is in the index, a task is in the month; if a task is in a
   month, the month is in the index. Test both directions, including
   a row stamped 23:5x on a month's last day and 00:0x on the next
   month's first — Brief B flags SQLite's `'localtime'` month
   bucketing as the one thing renders cannot see. **That check runs
   on the phone, near midnight, once, before this section is called
   done.** A task filed under the wrong month is findable by search
   and lost by browsing, and half a promise is the kind this plan
   exists to kill.
4. Inside a month: newest 50, `EARLIER` loads the preceding 50, no
   row skipped or doubled at the seam (the `id DESC` tiebreak in
   Brief B is load-bearing; the 51-completions check in Brief B's
   acceptance is adopted here verbatim).

## 3. PERMANENCE — an auditable property, not a promise

"No task is ever auto-deleted" is a sentence. Sentences are what the
first build of this app had instead of true font weights. Permanence
passes only as a list of greps and one runtime drill, run at every
round's end by the invariants owner (my plan-wave §5 prescription —
this section is one of the invariants):

**The grep list (all must come back as stated):**
1. `clearCompletedTasks` — **zero hits, all four links of the chain**:
   the DAO delete (`DELETE FROM focus_tasks WHERE status =
   'COMPLETED'`), the repository wrapper, the ViewModel function, the
   Tasks-screen wiring. Brief B prices the removal; this test is what
   proves it happened. Unrendered is not removed — a live chain one
   chip away from resurfacing is a deletion waiting for a deadline.
2. Every `DELETE FROM focus_tasks` in the tree, read by a human, one
   by one: **none may carry a date, age, batch, or status predicate
   that can reach a COMPLETED row.** `deleteTaskById` survives only
   as the queue's per-task delete-with-undo for *uncompleted* rows
   (Brief B Flag B-2) — its call sites are audited to prove no stored
   row can reach it. The audit is of call sites, not of intentions.
3. No retention machinery exists: no cleanup job, no expiry field, no
   "prune," no WorkManager periodic task touching `focus_tasks`. The
   absence is the feature; grep is how absence is evidenced.
4. **The one reachable wipe that is not the Drawer's code, named
   honestly:** the database is v7 with
   `fallbackToDestructiveMigration(true)` — a failed migration
   destroys every stored task at once, permanently, lawfully, and
   without a single Drawer line being wrong. TOKEN-SPEC §6 owns the
   migration and the fallback's removal. **§7's permanence test
   cannot pass until that lands**, and §7 must print that dependency
   rather than borrow the Drawer build's innocence. I flag it as
   unresolvable from this seat in my letter to Sana; I do not smooth
   it over here.

**The runtime drill:** seed rows stamped today, −30 days, −1 year,
−3 years. Open every Drawer path (landing, index, each month, search,
detail), reopen-and-recomplete one *other* task, restart the app,
re-run. Every seeded row still present, still under its own date.
Reopen is the one lawful exit from the Drawer — his hand, on one
task, by name — and the drill proves it is the *only* exit: the
reopened task leaves, everything else stays.

**Two honest limits of the word "permanent," printed because §7 is
where he will look for the promise:**
- Permanent means *the app never deletes by age*. It does not mean
  *survives the phone*. The record is a local database; the AES
  backup blob's only consumer was its own READY badge (§0.1-10 —
  killed as a dead payload, no egress, no restore). If the phone is
  lost or the app uninstalled, years of stored tasks go with it, and
  no sentence in §7 may imply otherwise. A real export/restore is a
  feature decision for him, not a Drawer fix I can price into
  existence — flagged, not resolved.
- The seeded demo completion (Brief B Flag B-1) will sit in the
  Drawer on first open — a stored task he never finished. Permanence
  that begins with a fabricated entry teaches him the drawer lies.
  Not my decision to scrub (it is the wider demo-data question), but
  the thirty-day test in §1 is run on **his** planted tasks, never on
  the seed, and §7 should say so.

## 4. THE DRAWER NEVER SUMMONS — the negative audit

"No badge, count, dot, 'new' marker" is tested by enumeration, in
both directions — what is absent, and what must not grow back:

1. The Ledger stop in the band carries **nothing**: no badge, no dot,
   no count, no armed-style lamp. Screenshot the band on a day a task
   was finished and on a day none was: **pixel-identical.** (Frame
   Equality's logic, borrowed from §2 — a summon that only appears
   sometimes is still a summon.)
2. No notification, no widget figure, no chrome line anywhere reports
   a stored count or a stored anything. The `DONE n` tally in the
   Tasks queue header dies with the clear chain (Brief B step 6) —
   a count in Tasks is a summons wearing a header's clothes, and grep
   for it is part of this audit.
3. Inside the Drawer: month rows carry no counts (§2.2), date
   headings carry no totals, search prints no result count (§1.4),
   the header is the word `STORED` and a search field — nothing else.
   A count that never prints cannot summon; that is why §5.1's fix is
   paging rather than a printed total.
4. Nothing in the Drawer changes state when a task is stored. The
   Drawer he opens after a month away is the Drawer he left, plus the
   rows themselves. Stillness is the test: store a task with the
   Drawer closed; the closed Drawer gives no sign. That is not a
   missing feature. That is the feature.

## 5. WHAT THE OLD TEST BECOMES — and how a kept tab still buries

My demote filing carried one condition, and MASTER-PLAN records that
it stands: *if six months of his actual behaviour show a Sunday audit
that goes looking for the record and cannot find it, the demote has
failed its own test and I will argue for the tab myself.* He has now
kept the tab by his own word. So the condition's placement question
is closed — by him, not by the test passing — and honesty requires
saying what the test **becomes**, because the danger it guarded
against did not die with the demote. It moved.

**It becomes a standing acceptance test, not a reversal trigger.**
There is no longer a placement to reverse. The drill in §1 is the
Sunday audit, mechanised: one typed word, thirty days later, and the
same search at year scale. It runs at build, and it should re-run
whenever the Drawer or the task store is touched — the invariants
owner's list, again. What it can trigger now is not an argument for a
tab (he has one) but a build failure: §7 does not ship, or ships
named as failing, until retrieval passes.

**And here is what would have to be true for the Drawer to fail him
anyway** — a kept tab, permanent rows, every grep in §3 green:

1. **The word in his head is not the word in the row.** Search finds
   only words actually stored. Thirty days later he remembers the
   *subject* of a task; the title used a different word, the notes
   never mention it, and the honest answer `NOTHING STORED UNDER
   “…”` is returned for a task that is sitting right there. This is
   the burial most likely to happen, and no query tuning abolishes
   it. The spec's real defences are already in it: four fields
   searched, not one — title, category, notes, substeps — so a task
   has four chances to carry the word he kept. §7 should say plainly
   that findability is a property of what was *written* as much as of
   what is *searched*.
2. **He remembers two words from two different fields.** See §5.2 —
   the single-string query fails this silently.
3. **A common word outgrows the result cap.** See §5.1 — the oldest
   years fall off first, silently, which is the permanence promise
   broken by the retrieval layer while the storage layer keeps it.
4. **He looks in the wrong month.** A task finished late on the 31st
   is remembered as "early November." Browsing fails; search is the
   rescue — which is why the search test (§1) outranks the browsing
   test (§2) in this file, and why the month index must never be the
   only door.
5. **Volume buries by politeness.** Newest-first and 50-paging are
   right for the current month. In a heavy month, the task he wants
   is three `EARLIER` presses down a list with no other landmarks —
   findable in law, buried in fact. Search is again the rescue; the
   month view is an address system, not a reading experience, and §7
   must not pretend otherwise.

If those five are printed in §7, the Drawer can still fail him — but
it cannot fail him *quietly*, and quiet failure is the only kind this
studio has ever actually shipped.

### 5.1 Where the search spec is insufficient — the 200 cap

Brief B's `searchStoredTasks` ends `LIMIT 200`, date-descending.
For a distinctive word, irrelevant. For a common word — and after a
year his stored tasks will contain common words by the hundred — the
201st match and everything older **silently does not exist**, and
because §1.4 forbids printing a result count, he cannot even see the
truncation. The no-count law and the hard cap cannot both stand at
year scale; one of them is lying about the drawer being fully
searchable.

**Fix, priced:** page search exactly as months are paged — the DAO
takes `LIMIT 50 OFFSET :offset`, the ViewModel accumulates
`drawerResults` with the same `loadEarlier()` the month view already
carries, results render in the same 50-chunks with `EARLIER` at the
foot. No count is ever printed; the list simply never claims to be
finished while more exists. ≈ +6 lines DAO, +15 ViewModel, +10
screen over Brief B's figures; no new mechanism, no migration. This
is the one change in this file I mark **not optional**: without it,
acceptance check 2 of the ruling ("years later, the same search
still reaches it") is untestable-as-true for any word that occurs
more than 200 times, and I will not sign a §7 whose central check has
a hole shaped exactly like its oldest data.

### 5.2 Where the search spec is insufficient — one string, whole

The query is one `LIKE '%q%'` per field. Two consequences:

- **Multi-word memory fails.** He types two words he remembers —
  one from the title, one from the notes — and the single literal
  string matches nothing, because no single field contains the whole
  phrase. The ruling promises one typed word; it does not promise
  two. But he will type two, on the worst day, and get an honest
  `NOTHING STORED UNDER` for a stored task. **Fix, priced:** split
  the query on spaces; every token must match *somewhere* in
  title/category/notes/substeps (AND across tokens, OR across
  fields). Dynamic query built in the repository, ≈ +25 lines +
  tests, no migration. One-word behaviour unchanged — the §1 test
  passes identically. I recommend it; I do not mark it load-bearing,
  because the ruling's promise is one word and I will not inflate his
  words into a bigger promise and then charge him for it. Sana/Ren
  rule on whether §7 adopts it.
- **Wildcards are live.** An unescaped `%` or `_` in his typed text
  is SQL pattern, not text — `%` alone returns everything. **Fix,
  priced:** escape `\`, `%`, `_` and add `ESCAPE '\'` per field,
  ≈ 5 lines in Brief B's query. Not optional in my judgement either:
  search must search for what he typed, not for a pattern he didn't.

**Named limit, no fix priced because none is honest at this size:**
SQLite `LIKE` folds case for ASCII only. A word he stored and later
types in different case finds it — for English words. A word stored
in another script's casing conventions may not. I name it so §7
doesn't overclaim; a normalisation column is a migration and belongs
to whoever next opens one, flagged beside the date-notes deferral in
Brief B step 8.

## 6. MY OTHER STANDING CONDITION — untouched by this merge

For the record, so Sana does not have to reconcile it twice: my
condition that **a spent hold prints on the hold control itself**
stands in MASTER-PLAN §5, where it belongs. It is not Drawer
business and nothing in this file moves, repeats, or re-argues it.
The Drawer stores finished tasks; the hold's state lives at the
moment of need. One fact, one home — including mine.

---

*Acceptance summary for the merge, in one breath: §1's four plants
found by one word at −30 days and −3 years; §2's two taps to any
stored month, both index directions true, near-midnight checked on
the phone; §3's grep list green and the drill survived, with the
destructive-migration dependency printed, not hidden; §4's band
pixel-identical whether or not anything was stored today; §5.1's
paging adopted and §5.2's escaping adopted, its token-split ruled on
by name. Anything less and the Drawer is a box things are put into —
and he already owns furniture.*

— Old Tom, Designers (Veteran)
