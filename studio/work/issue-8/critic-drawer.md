# CRITIC — DRAWER AUDIT FIXES (issue #8, main-app pass)

Critic (issue #8 cell). Scope unrestricted; every charge carries
evidence or is marked UNPROVEN. Nothing compiled — read-only
verification only.

## 0 · PROVENANCE OF THIS REVIEW

The cell clone at `builds/cells/issue-8/` was **absent when this
review started** and appeared during it (branch `agent/issue-8`,
HEAD `8438f5a`). All code below was verified in that clone; the
three load-bearing files (`MonolithRepository.kt`,
`MonolithDao.kt`, `DrawerScreen.kt`) are byte-identical to the
issue-5 cell clone and to `clone-inspect/Focus-app` on main, so
the evidence is not an artifact of one stale copy. The audit
under attack is `studio/plan/AUDIT-drawer.md`; the cell's own
floor and beyond-floor files (`designer-drawer.md`,
`innovator-drawer.md`, same folder) are attacked where they
repeat or extend the audit's errors.

Line numbers below are the CURRENT files. The audit's own
citations have drifted — see F2.

---

## 1 · THE FORGE BUG (issue #8 item 1)

### F1 — PROVEN. The bug is real, exactly as charged in substance.

`MonolithRepository.kt:2049-2050`, inside
`updateSessionProgress` (fun starts :1934):

```kotlin
val forgedId = if (allBlocksCompleted && !isRoutineSession) {
    CurioCatalog.artifacts.shuffled().first().id
} else null
```

Sampling **with replacement** over all six entries of
`CurioCatalog.artifacts` (`MonolithModels.kt:807-850`, six
`KineticArtifact`s). No exclusion of already-forged ids anywhere
on the write path; the result is stored as the session's single
`forgedArtifactId` (:2086).

The read path is the opposite contract —
`MonolithDao.kt:74-75`:

```kotlin
@Query("SELECT DISTINCT forgedArtifactId FROM focus_sessions WHERE forgedArtifactId IS NOT NULL")
suspend fun getForgedArtifactIds(): List<String>
```

surfaced via `MonolithRepository.kt:671-672`
(`forgedArtifactIds(): Set<String>`) and consumed in
`DrawerScreen.kt` as `item.id in forgedArtifactIds` (:375) /
`artifact.id in forgedArtifactIds` (:485). Once the distinct set
saturates at six, no later finish can change anything the
cabinet prints, while the cabinet's standing rule keeps
printing the promise — `DrawerScreen.kt:431`:
"A FINISHED SPAN FORGES ONE — THE FORGE CHOOSES WHICH."
The audit's hardest fact stands.

### F2 — KILLED. The audit's line citations are stale; do not hand them to the Builder.

The audit (§2A, §3) cites `MonolithRepository.kt:1861` and
`MonolithDao.kt:68`. In the current tree, :1861 sits inside
`advancePastBreak` (break/HMAC code — no forge within 200 lines)
and Dao :68 is `getSessionHistoryFlow`. The real sites are
Repository :2049-2050 and Dao :74-75 (F1). A builder who greps
the audit's numbers lands in the wrong function and may
"verify" the bug as absent. Citations: killed. Claim: proven
(F1). The fix brief must carry the corrected lines.

### F3 — PROVEN (wording). "Repeats from the second forge onward" overstates the mechanism.

Audit §2A: "repeats from the second forge onward." The draw is
an independent uniform shuffle per finish, so a repeat on the
second forge is a 1-in-6 event, not a certainty; the expected
number of qualifying finishes to complete the set is the
coupon-collector ~14.7, not 6. What IS certain — and is the
real charge — is only the post-saturation half: once six
distinct ids exist, every later forge is invisible with
probability 1. The audit's rhetoric borrows certainty from the
second half to dress the first. The fix (and the Innovator's
P1) must be argued on saturation + invisible repeats, not on
"repeats start immediately."

### F4 — PROVEN (audit missed it). There is a second silent forge path, and a pollutant in the DISTINCT set.

(a) `disarmActiveSession` never forges. Its completed door is
the stopwatch ending — `MonolithViewModel.kt:2246`:
`disarmActiveSession(completed = true, outcomeReason =
"Stopwatch Completed")`. The function
(`MonolithRepository.kt:2297ff`) writes
`forgedArtifactId = partialStampId` (:2334), where
`partialStampId` (:2314-2316) is `"partial_stamp"` for an
early end ≥60s, else the session's existing
`forgedArtifactId` — which `startSession` initialises to null
(:1368) and only the tick path (F1) ever sets. So a span that
ends through the disarm door gets no artifact, ever, while the
printed rule (DrawerScreen :431) says a finished span forges
one. The audit attacked one silent death; there are two doors
and only one of them forges at all.

(b) The same write stamps the literal `"partial_stamp"` into
the `forgedArtifactId` column. The DISTINCT read (Dao :74)
therefore returns a seventh, non-catalog id. The cabinet's
`in forgedArtifactIds` checks happen to ignore it, but any
future code that counts the distinct set (a "how many forged"
number — precisely the grading print this app forbids) or
computes a complement from it inherits the pollutant. Neither
the audit nor Innovator P1 (which builds its complement on
this very set) mentions it. Any shrinking-pool fix must filter
the set to catalog ids, explicitly.

### F5 — UNPROVEN (overclaim, audit §2A + Innovator P1). "The database already holds which session forged each artifact — and its task."

Half of this is real: a forging session row does carry dates
(`FocusSessionEntity.startWallTimeMillis`, `endWallMillis`,
MonolithModels.kt:274,364), so date provenance is feasible —
but **no DAO query by `forgedArtifactId` exists today** (Dao
has only the DISTINCT read, :74-75); the query is new work,
not a read-back.

The task half does not hold. The session's only task-shaped
fields are `intention` (a mutable string — rewritten on task
promotion, `promoteTaskToActive`), `completedTasksSummary`
(free-text prose built by string concatenation in
`completeTaskAndAdvance`), and `taskId` — whose own comment
(MonolithModels.kt, Routines rebuild 8→9) says it is the Arm
Ticket's bond, "this build lands the column only," nullable,
no backfill. There is no forge→task link in the schema.
"Prints the forging session's date (and its task where the
session carries one)" smuggles prose-parsing in as provenance.
Date: PROVEN feasible. Task: UNPROVEN — kill it from the cure
until a real link is designed.

---

## 2 · INDEX DIES → INLINE MONTHS SHELF (issue #8 item 2)

What the audit gets right, for the record: the corridor charge
is PROVEN in code — MONTH's BACK
(`DrawerScreen.kt`, MONTH branch) calls
`onSelectMonth(currentYearMonth…)` and sets
`view = DrawerView.LANDING`, bypassing INDEX and resetting the
VM month in the same tap. INDEX adds no content of its own.
Killing it is defensible. The replacement is where the charges
land.

### S1 — PROVEN. The shelf is buried under the very content it indexes; "2 taps" counts taps and hides the scroll.

Designer's own band order (designer-drawer.md §2): header,
search, filing zone, **current-month day groups with EARLIER
paging in place**, then — band 5 — the MONTHS shelf. The
landing's rows come from a single month's paged state
(`MonolithViewModel.kt:1848` `_drawerRows`; `loadEarlierDrawer`
appends page after page of the CURRENT month, 50 rows per page,
`DRAWER_PAGE_SIZE` Repository :163ff). Every EARLIER tap pushes
the shelf ~50 rows further down the same LazyColumn. In a heavy
current month — the normal case for a 12-hour-a-day aspirant —
the route to last month is: scroll past the entire current
month, however many pages he has opened. INDEX, for all its
corridor sin, put every month one tap away at the TOP of the
screen (DrawerScreen LANDING branch: ALL MONTHS is the first
row under search). The audit's standard is "findable in
seconds"; the shelf optimises tap count while making scroll
distance unbounded. Audit §4's own arithmetic is also loose:
it prices the corridor at "4 taps" and the shelf at "2," but
from the landing the corridor route is 2 taps today (ALL
MONTHS → month row). The honest comparison is 2 taps + no
scroll vs 2 taps + full-current-month scroll. On that
comparison the shelf can be SLOWER than the corridor it kills.

### S2 — PROVEN. The Designer's shelf silently deletes word-only months — reversing a deliberate repository law.

Designer §3.3: the shelf lists "only months that contain
stored work… at least one stored task." But the months set the
screen actually receives is a UNION built for the opposite
rule — `MonolithRepository.kt` `storedMonths()` (:2823ff)
merges `dao.getStoredMonths()` with months derived from
`allDayWords()`, under the comment: "a month holding only a
declared word is still an address in the index, or the word
would be filed nowhere." A month where he declared ILL and
finished nothing currently has exactly one address: INDEX →
MONTH, whose groups are built from dayWords scoped to that
month (DrawerScreen `buildDayGroups`, MONTH branch). Kill
INDEX and adopt §3.3 as written, and that word becomes
unreachable print — filed, stored, and shown nowhere. Neither
the audit nor the Designer mentions word-only months at all.
The shelf spec must state their fate; as written, it deletes
them.

### S3 — PROVEN. Scroll restoration (§3.6) is an unbuilt mechanism presented as a design detail.

Designer §3.6 rules that returning from MONTH "lands the
scroll at the shelf row he tapped." `DrawerScreen.kt` has **no
`rememberLazyListState` anywhere** — every LazyColumn (landing,
detail, cabinet) uses default state, and the landing's list
identity itself changes as EARLIER grows it (S1), so even an
index-based restore points at a different row after paging.
Add `MonolithViewModel.refreshDrawer()` (:1876ff), which
resets `_drawerMonth` to the current month on every VAULT
entry, and the "return to where you were standing" promise
needs: a saved list state, stable shelf keys, and a ruling on
what survives a destination switch (see M2 — today, nothing
does). None of that is priced in the audit or the Designer
file. §3.6 as written is a wish with a section number.

### S4 — UNPROVEN. The scale defence ("≤60 rows at five years, no scale furniture needed").

Designer §3.5's arithmetic is fine as far as it goes — one row
per month is cheap. But it defends the shelf as a LIST while
the actual scale problem is the shelf's POSITION inside an
ever-growing single scroll (S1) and an unbounded months set
that also includes word-only months (S2). "No scale furniture
will be needed" is asserted, not shown; Innovator P5 (thumb
rail) exists in the same cell precisely because the floor's
own crew does not believe it. Marked UNPROVEN, with S1 as the
standing counter-evidence.

### S5 — PROVEN (spec gap). Nobody says WHICH "current month" the shelf excludes — and at 00:00–03:29 the two candidates differ.

The landing's month is the FILING month:
`refreshDrawer()` seeds `_drawerMonth` from
`DrawerDay.filingYearMonth(now)` (VM :1844, :1877), and
`DrawerDay` shifts the boundary to 03:30. The shelf must
exclude the filing month, not the calendar month, or near a
month boundary the same month prints twice (once as band 4,
once as a shelf row) or vanishes from both. Audit §2 and
Designer §3.3 ("the current month never appears on the
shelf") never name the filing rule for the exclusion. Small,
but it is exactly the class of 03:30 bug this screen's history
(DRW-03, DRW-05) is made of.

---

## 3 · CABINET EVICTION, HOME PARKED IN #21 (issue #8 item 3)

### E1 — PROVEN. Sequencing hole: evicting the door before the new home exists makes the cabinet unreachable, indefinitely.

The cabinet has exactly two doors in the entire app, both in
`DrawerScreen.kt`: the CURIOS row in the globally-empty branch
and the CURIOS row in the normal LANDING branch (both
`onClick = { view = DrawerView.CABINET }`, testTag
`drawer_curios_row`). There is no other entry: the Deck's
`onOpenCurioVault` parameter (`TactileDeckScreen.kt:128`) is
**declared and never invoked** in that screen's body, and its
wiring (`MainActivity.kt:509`) points at
`MonolithDestination.VAULT` — the Drawer itself, not the
cabinet. Audit §2A kills the CURIOS row now; Designer §6
forbids even a forwarding sign ("no cabinet-shaped hole… not
annotated"); the new home is parked in issue #21 with its core
question (fifth destination vs Vault header band) still
escalated and unruled (audit §7.1). Net effect of shipping #8
as specified: forged state and lore become unreachable for an
unbounded interval, by design, with the absence deliberately
unmarked. If eviction must land in #8, the honest sequencing
is: CURIOS row dies in the same change that opens the new
door — or #8 keeps the row and #21 does both halves.

### E2 — PROVEN. A "fifth destination" is a rail-geometry rewrite, not a door addition — the escalation understates it.

The rail is a four-stop machine: `MonolithRailView.kt`
documents stops 0..3 with PARK as "index 4 in travel math
only" (:22-23, :63-64, :100-101 — the cap's travel, detents,
and stop-for-x math all assume exactly this layout). The
destination mapping is hard-coded in THREE places in
`MainActivity.kt` (selectedTabIndex when-block ~:354-357,
onTabSelected ~:718-721, stopIndexOf ~:816-820), plus the
`MonolithDestination` enum (`MonolithViewModel.kt:48-52`:
DECK / TASKS / SHIELD / VAULT). A fifth stop collides with
PARK_INDEX=4 and forces rail geometry, travel timing, and all
three mappings to change together. Audit §7.1 prices this as
"machinery: escalated… with Plan's prices" without naming
the collision. Any #8 plan that treats eviction as "delete a
row from DrawerScreen" is working from a false map of the
cost — the Drawer-side deletion is the cheap half.

### E3 — PROVEN. Eviction does not fix the naming tangle it claims to kill.

Audit §2A: the new home is "a destination-level door named
CABINET (door and room finally one noun — Fremd's three-nouns
charge dies with the move)." But the Drawer itself remains
destination `VAULT` (route `"vault"`, label `"Receipts"`,
VM :48-52) hosting a screen whose header prints STORED and
whose file is DrawerScreen. After the move the app has VAULT
(the drawer), CABINET (the curios), CURIOS (dead), STORED
(the room's print) — the noun count goes UP unless VAULT is
renamed in the same stroke. No file in this cell proposes
renaming VAULT. The charge the eviction advertises as its
naming cure survives the eviction.

### E4 — PROVEN. The forged-state data flow is wired through the Drawer; eviction orphans it.

`forgedArtifactIds` is loaded in exactly one place:
`navigateTo(VAULT)` triggers `refreshDrawer()`
(VM :1422, :1876-1882), which reads
`repository.forgedArtifactIds()`; it is passed into exactly
one consumer, `DrawerScreen` (`MainActivity.kt:641-642`).
Designer §7 says the cabinet's pop logic "travels with the
cabinet… not deleted from the codebase by this pass, only
from this screen" — but the cabinet UI is not a separable
composable: it is an early-return branch inside
DrawerScreen driven by DrawerScreen's own parameters
(`artifacts`, `forgedArtifactIds`, DrawerScreen :160-166,
:330ff). Removing it from this screen IS relocating the
composable plus its VM trigger — i.e., a real slice of the
#21 work — or deleting it and rebuilding later. The #8 plan
must say which; "only from this screen" papers over the fork.

### E5 — PROVEN. Test/screenshot breakage is unlisted in every file in this cell.

`ScreenshotHarnessTest.kt:197-205` and `:254-256` drive the
cabinet via `drawer_curios_row` and `cabinet_row_*` tags;
`DrawerRenderTest.kt:227` drives INDEX via
`drawer_all_months`. INDEX kill + eviction break both, at
runtime, in the harness that produces the screen's visual
record. Nobody compiles in this cell, so nobody will catch
it here — it lands on Ren's one compile. The fix plan must
carry the test updates as named work.

### E6 — KILLED (in the eviction's favour). "Eviction loses stored state" does not stand.

No persisted cabinet state exists to lose: forged state is
derived per-read from the sessions table (Dao :74), the
catalog is a static code list (`CurioCatalog`,
MonolithModels.kt:799ff), and the cabinet's UI state
(`cabinetArtifact`, `view`) is `remember`-only
(DrawerScreen :164-166) — it does not survive a destination
switch today, let alone an eviction. What eviction breaks is
reachability (E1), navigation geometry (E2), naming (E3),
wiring (E4), and habit — not data. Charges should be filed
on those grounds only.

---

## 4 · WHAT THE AUDIT MISSED IN THE DRAWER AREA

### M1 — PROVEN. System back ejects from the Drawer in ONE press, from any depth. The audit buries the screen's most destructive fact as one ruling among many.

`DrawerScreen.kt` contains no `BackHandler`. The only handler
is `MainActivity.kt:349-350`: for ANY non-DECK destination,
system back calls `navigateTo(DECK)`. So from DETAIL, from
MONTH's groups, from the cabinet detail — one system-back
press leaves the Drawer entirely. Audit §2 does rule the
cure ("system back is consumed inside the Drawer one step
per press"), but files it mid-list between tap targets and
the dead CABINET branch, with no severity marker; §4's 1 AM
test never presses it. A tired man reading a stored task who
taps the phone's back key expecting the shelf is thrown to
the Deck and (M2) loses his place completely.

### M2 — PROVEN. State loss is per-visit, not "process death" only — the audit understates its own charge.

Audit §2: "view / selectedTask / cabinetArtifact are bare
`remember`; process death resets a mid-browse man to
LANDING." True but weaker than the code: all three are
`remember` at DrawerScreen :164-166, and DrawerScreen is
composed only inside the `MonolithDestination.VAULT` branch
(`MainActivity.kt:624ff`). Leaving the Drawer for any other
destination destroys the composition and resets all three —
no process death required. Every Drawer visit starts on the
landing today, whatever he was reading thirty seconds ago in
another destination. The saveable-state cure is therefore
not a robustness nicety; it is the fix for ordinary use.

### M3 — PROVEN. Tomorrow-filing has no plumbing: the audit and the Designer treat a ViewModel + signature change as a placement decision.

The ruled filing zone needs declaring a word for TOMORROW.
The only VM entry point is `declareTodayWord`
(`MonolithViewModel.kt` ~:1953): it computes
`DrawerDay.filingDate(System.currentTimeMillis())` — today,
hard-coded — and the screen callback it feeds is
`onDeclareTodayWord: (String?) -> Unit` (DrawerScreen
params; wired at `MainActivity.kt:640`), a signature that
**cannot carry a date at all**. The repository layer is ready
(`declareDayWord(dateIso, word)` is generic), so the missing
work is precisely: a date-carrying callback through
DrawerScreen → MainActivity → ViewModel, plus a
declare-word-for(date) VM verb. Neither the audit §2 ruling
nor Designer §4 mentions any of it; §4 even specifies
tomorrow's line presentation down to the accent colour while
the function that would file it does not exist. Unpriced,
unspecified, and on the critical path of the audit's own
"worst single finding" cure (§4).

### M4 — PROVEN. Search treats `%` and `_` as wildcards: the empty-result line can be made to lie by typing one character.

`MonolithDao.kt:213`: `searchStoredTasks` matches with
`LIKE '%' || :q || '%'` over title / categoryTag /
notesOrSubsteps, with **no ESCAPE clause** and no escaping in
the repository wrapper (Repository :2836ff trims only). A
query of `%` or `_` matches effectively the whole archive, so
"NOTHING STORED UNDER '%'" is unprintable truth in reverse —
the search returns everything for a query that names nothing.
Six audit seats verified this screen's prints and missed the
one input that breaks its matching semantics. (Same query
also matches against the raw serialised substep string,
including its `[x]`/`[ ]` markers — searching "x" hits every
task with a completed substep. The audit's substep-reason
cure, §2, will print some of those reasons; it never asks
why the match set is shaped that way.)

### M5 — PROVEN. Designer §5 puts a FORGED line in the TASK detail, where no such datum exists.

Designer §5 (DETAIL paragraph): "FORGED, where it belongs,
prints at full ink here in the detail only." In context that
paragraph is the **task** DETAIL (estimate move, substeps,
BACK). A stored task carries no forged fact; the audit's §2A
ruling ("state belongs in the detail") is about the
**cabinet** detail. The only conceivable task→forge link —
`completedInSessionId` → that session's `forgedArtifactId` —
is specified nowhere, and would print a session-level fact on
a task's record (a session can complete several tasks; a task
completed outside any session has id 0). A builder following
§5 literally either invents the link silently or stalls.
Designer must strike or re-home that sentence.

### M6 — PROVEN. Innovator P4 (Kin Thread) re-opens a binding acquittal while claiming it doesn't.

Audit §6, binding: "DETAIL's single verb BACK (Dens)".
Designer §0 restates it as test 3. Innovator P4 adds tappable
kin lines inside DETAIL — "Tapping one opens its DETAIL" —
while asserting "It adds no verb." Opening another record
from inside the read-only room is a verb by the file's own
description of it. P4 may still be worth wanting, but it is
a request to re-open an acquittal and must be filed as one,
not smuggled under "the verbs DETAIL already had."

### M7 — UNPROVEN. Innovator P2/P3 costs rest on queries that do not exist.

P2 claims ghost search needs "no new data path — the
existing search query already matches… across all years."
The existing path returns PAGED ROWS (Repository :2836,
page size 50 + probe); P2's shelf lighting needs a per-month
contains-hit boolean for ALL stored months including
unloaded ones — a distinct-months-matching query that is
not in the Dao. It also cannot light hits sitting in the
current month's unloaded pages, a gap P2's own fallback
concedes only for performance, not correctness. P3's anchor
title likewise needs a new grouped query that reproduces
the 03:30 filing rule in SQL (the months index's
`- 12600` twin, Dao :210) per month — real work, priced in
P3 as "one grouped query… ~60–100 LOC" without noting the
rule duplication. Both proposals may be worth their true
price; the printed prices are UNPROVEN.

### M8 — PROVEN. Even after Innovator P1, the printed forge rule stays false — P1's "qualifying finishes" hides the exclusions.

P1 guarantees the first six QUALIFYING finishes add new
artifacts. The qualifiers do the hiding: routine sessions
never forge (Repository :2049 fence, and the finalise funnel
nulls the field for non-ATTENDED outcomes, :1040), and the
disarm door never forges (F4). The cabinet rule line —
"A FINISHED SPAN FORGES ONE" (DrawerScreen :431) — makes no
such distinction, and after eviction nothing in the Drawer
prints it, but wherever the cabinet lands, P1 alone does not
make that sentence true. Either the rule line narrows to the
spans that actually forge, or the forge widens to every
finished span. P1 does neither and claims the promise and
the machinery "finally describe the same event." They don't.

### M9 — PROVEN (minor). Search results strip declared day words that the same days carry everywhere else.

In the SEARCH branch, groups are built with
`dayWords = emptyMap()` (DrawerScreen, search branch of
`buildDayGroups` call). A task found by search under a day
declared ILL prints under a bare date heading; the same day
on the landing prints "· ILL". Same record, two prints,
depending on the door he entered by. The audit — which
ruled search's failure line, search's BACK, and search's
match-reason — never looks at what search does to the day
headings it reuses.

---

## 5 · ACQUITTALS THE CRITIC CONFIRMS (so the Builder doesn't re-litigate them)

- No totals/counts/streaks/percentages printed in
  DrawerScreen.kt: confirmed by full read. The audit's §6
  core acquittal stands.
- The sealed search box is a painted door, as charged:
  the field is composed with its "TYPE TO FIND A TASK"
  placeholder while `enabled = !isSealed`
  (DrawerScreen, search item). PROVEN.
- MONTH's BACK bypass + VM-month reset: PROVEN (see §2
  preamble).
- The dead CABINET branch in the list `when()` renders a 0dp
  Spacer behind an "unreachable" comment: present exactly as
  charged. PROVEN.

---

## 6 · BOTTOM LINE FOR LADE

Item 1 (forge): fix it, with F2's corrected lines, F4's
second door and partial_stamp filter in scope, and F5's
task-provenance struck from the cure.
Item 2 (shelf): the kill of INDEX survives criticism; the
shelf as specified does not — S1 (burial), S2 (word-only
months), and S3 (restoration mechanism) must be answered
before build.
Item 3 (eviction): do not ship the eviction half in #8
while #21 is parked (E1); if it ships anyway, E2–E5 are
named work, not surprises.
Missed by everyone: M1–M4 are Drawer defects in the current
code, independent of this pass's design fights, and M3 is
on the critical path of the audit's own worst finding.

— Critic (issue #8 cell)
