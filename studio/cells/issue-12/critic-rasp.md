# RASP — BRUTAL CRITIC filing, issue #12 (Tasks + F-1)

*Dedicated critic seat, programme of 100. Criticism only — no design,
no build, no fix. Every charge carries file:line, commit, or comment
evidence from the branch clone (`agent/issue-12` @ 2993fbc), trunk
(`~/workspace/clone-inspect/Focus-app`), or the issue's own comment
record (23 comments, read in full). Where the cell was right, I say
so — see Acquittals. Where it was not, I do not soften it.*

## Verdict in one paragraph

F-1's core is real and the resolver is honest. Everything around it
is not: the issue was **closed** on a CI claim that names tests which
do not exist, while the one defect the panel made a formal merge
condition — a sealed session's intention being rewritten with no seal
check — sits live and unruled in the shipped branch, now with a wider
blast radius and a DEFERRED-task trap inside it. Half the audit's fix
list never shipped and was never struck on the record. The wave-2
"30-seat" verification was one lead wearing 25 hats. This cell proved
a pure function and declared a system.

---

## Charges

### 1. BLOCKER — The snapshot guarantee is still false in the shipped branch, and F-1 made it a daily lie (ESR-02)

`completeTaskAndAdvance` (branch `MonolithRepository.kt:2525`) still
rewrites the live session's intention with no seal/marker check: at
the advance step it executes `currentSession.copy(intention =
promoted.title)` and persists it. Its sibling `promoteTaskToActive`
(:2667) refuses the identical write under ESR-02 (:2688-2698: "a
SEALED session's intention is the receipt's title source — rewriting
it under the seal would falsify the slip"). Worse, the advance's
next-up selection falls back to DEFERRED when no QUEUED row remains
(:~2578: QUEUED `minByOrNull` `?:` DEFERRED `minByOrNull`) — the exact
fallback F-1's own resolver was forbidden to use (plan ruling 7).

Consequence chain, now routine-sized: 05:00 FASTED seals titled with
the head task → he tears that head mid-session (the designed act,
the tear this whole screen exists for) → the session's intention
silently becomes the next task, or a task he DEFERRED awake → the
receipt title changes (`MonolithRepository.kt:1629 title =
row.intention`) → deflection logs matched by `log.sessionIntention ==
row.intention` (:1597-1610) orphan every deflection logged under the
original title.

The panel seat made this a formal condition: "must be ruled AT
MERGE, before F-1's wider intentions ship" (issue comment
6510309901; ISSUE-12-WAVE2-REPORT.md addendum). The closing comment
(6510781631) demoted it in one parenthesis — "it does not block this
issue's delivery" — and the issue closed. A condition converted into
a footnote by the party it constrained is not a condition met.

### 2. BLOCKER — The closing CI claim names tests that do not exist

Closing comment 6510752235: "ZERO RoutineDeriveTest failures: F-1 is
proven on CI, **including the seal-time re-resolve era tests**."
There are no such tests. The H1 re-resolve lives in
`RoutineHeartbeat.reconcileOnce` (branch, step 3: `derivedDesired
.copy(intention = freshHead ?: routine.name)`); grep of `app/src/test`
finds no test referencing the heartbeat re-resolve, `freshHead`, or
the override — the only `materialiseRoutineSession` test
(`RoutinesFinaliseTest.kt:285`) feeds it a hand-written literal
intention ("DAY BLOCKS") and asserts forwarding. All 16 new tests sit
in `RoutineDeriveTest.kt` and exercise the pure derive/resolver —
the seam Charge 3 shows production overrides. The issue's acceptance
text demanded "F-1 working on a routine-born session"; what was
proven is a pure function over lists. The end-to-end path — heartbeat
resolves a real queue, materialises, seals — has zero test coverage
at any level, and the comment that closed the issue claims the
opposite by name.

### 3. HIGH — H1 made derive()'s intention a dead value in production; the tests guard the corpse

Wave-2's H1 cure has the heartbeat unconditionally overwrite the
derived intention for every non-SLEEP desired session
(`RoutineHeartbeat.kt` step 3). So on the only production path,
`DesiredSession.intention` — the field all 8 derive intention tests
assert — is computed, tested, and discarded. The live value is
produced by an untested `copy()` in a service file. Two SLEEP laws
now exist in two files (`RoutineDerive.deriveOne`'s `isSleep`;
heartbeat's `routine.loadoutName != SmartLoadout.SLEEP.name`) and
must agree forever by discipline alone. The wave-1 design (one pure
seam, caller passes a value) was the honest architecture; H1 traded
it for a second writer and kept the first writer's test suite as the
proof. Nobody re-priced that trade on the record.

### 4. HIGH — "Re-resolved at the seal itself" is still false; the snapshot instant is materialise, and the seal lands up to 1,500 ms later

Issue comment 6510781631: "head re-resolved at the seal itself
(H1)". Branch code: the re-resolve is a DAO read inside
`reconcileOnce`, followed by a tombstone check, followed by
`repository.materialiseRoutineSession(desired)`, after which the
actual seal is launched off-pass — `scope.launch {
transition.proveAndSeal(sealSessionId, null) }` — with a proving
window of ≤1,500 ms (`SessionSealTransition.kt:245 ANNUL_MS`, per
the genuine Critic's Charge 5, unrebutted). A head torn inside that
window still headlines the sealed session. The heartbeat's own code
comment admits the residue ("five sequential DAO reads are not one
transactional snapshot"); the issue prose claims "at the seal
itself". The cell's own standard — TELL-THE-TRUTH — applies to its
comments too.

### 5. HIGH — A flagged data-loss collision was carried at cell close and vanished from the final closing

`ensureSeededAndSyncPackages` dedup (branch `MonolithRepository.kt`
:~408-418, unchanged by this cell) deletes every inactive session
whose key `intention|targetDurationSeconds|completedTasksSummary`
repeats an earlier row, and runs at ViewModel init. Pre-F-1,
`routine.name` namespaced routine rows away from hand rows; F-1
removed that separation by design. Two routine sessions sealing the
same head at the same remainder-derived duration with an empty
summary now collide, and one is deleted as a "duplicate" on next
launch. The cell-close comment (6509533859) flagged this honestly as
open item 3. The final closing comment (6510781631) does not mention
it at all. A known history-deleting consequence of the shipped spec
fell out of the record between "flagged" and "delivered".

### 6. HIGH — Seal gating (audit item 12 / F-5) never shipped — and this pass added a NEW live verb under seal

Audit item 12, convicted by three seats: under a seal, DEL, CUE/
START, SPLIT and sub-step toggles stay live. Branch state today:
the sub-step toggle is `clickable(enabled = !ghost)`
(`FocusSequencerScreen.kt` sub-step row) with no seal condition;
DEL is `clickable(enabled = !ghost)`; SPLIT is
`clickable(enabled = !ghost)`; and the new HAND-SPLIT field renders
under `if (splitting && !ghost)` — no `!isSessionActive` anywhere in
that chain. Only the tear is absent under seal. So during the 05:00
sealed FASTED session, the user can delete the very head task whose
title headlines the session, and — new in this pass — open the split
field and add steps to it. F-5 was priced SMALL in the audit. It was
deferred to "batch-10" without a strike reason, and the issue's
checklist never contained it, so its absence reads as completeness.

### 7. HIGH — Provenance was parked, so F-1 ships permanently unverifiable

The Innovator's P-1 was the keystone of his bundle: stamp where each
intention came from, or a post-F-1 session titled "FASTED BLOCK" is
indistinguishable between honest empty-queue fallback and a dead
regression — forever, for Sarthak, for Ren, for any future audit.
The lead parked it (plan ruling 3) because a migration slot in
batch-10's MIGRATION_12_13 was "Ren's merge call". No comment on the
issue records that call ever being requested or answered. The fix
that exists to make the morning truthful cannot itself be checked
for truth in the data it produces. That is not a deferral; it is the
amputation of the verification half of the feature, taken to fit the
price in Charge 12.

### 8. MEDIUM — The audit's headline overclaims: the routine path DOES read the task queue

AUDIT-tasks.md headline: "the routine path never reads the task
queue". False as written. Trunk `MonolithRepository.kt:2423-2444`
(`accrueActiveTaskSeconds`) reads the ordered task list during any
session — routine sessions included — and credits the ACTIVE /
first-QUEUED task with wall time, even promoting it to ACTIVE on the
record. What never reads the queue is *intention at materialisation*
(RoutineDerive.kt:169, trunk — verified). The audit's own escalation
text and the issue body both repeat the wide version. A brutal audit
that convicts other people's imprecision does not get its own free.

### 9. MEDIUM — The audit's F-3 price was false, and the shipped F-3 still didn't pay it

Audit: F-3 is "~113 lines deleted, zero added". The genuine Critic
convicted the omission (critic.md, Charge 4 investigation): killing
the bar honestly also means deleting the `totalQueueMinutes`
plumbing. Shipped branch, today: that plumbing is ALL still alive —
`MonolithViewModel.kt:342` (the flow), `MainActivity.kt:190, 479,
601` (collect + two passes), `FocusSequencerScreen.kt:84` (a screen
parameter the body never uses — one of the very "nine dead
parameters" the audit's F-9 convicted this screen for carrying), and
`TactileDeckScreen.kt:105`. The bar died; its supply line stands,
still computed on every task-list emission, still promising a number
no surface prints. F-3 was declared "DONE" (comment 6509533859).

### 10. MEDIUM — The audit's method: "Convergence is treated as proof"

AUDIT-tasks.md, Convergences: "Four seats convicted the horizon bar
without seeing each other's work… Convergence is treated as proof."
Six seats reading the same trunk file are one witness with six
mouths. Convergence over *code facts* (a line exists) is cheap and
was verified anyway; convergence over *verdicts* (KILL, house
standard, death sentences) proves only that the seats share the
brief, the aesthetic, and the coordinator. The audit spends the same
word — "convicted" — on both kinds, and its verdicts inherited an
authority its facts earned and its judgements did not.

### 11. MEDIUM — ZIP-TEAR: a "house standard" made of nothing, closed by declaration

Zero occurrences of "zip" in `app/src/main` or `app/src/test` (grep,
both trees, per the genuine Critic's Charge 8). No finger tracing,
no 32dp band, no 85% crossing — a proposal paragraph in the audit's
Escalations §2. The issue's checkbox ("ZIP-TEAR becomes the house
tear standard for the next tear build") was closed by comments
asserting the standard, and the lead spent a formal ruling
(plan ruling 2) overruling the Designer's 92% threshold in favour
of the audit's 85% — a precision dispute over a mechanism nobody
has prototyped, measured, or built. A standard that exists only as
an adjective in closing comments is vapour wearing a title.

### 12. MEDIUM — HAND-SPLIT as shipped is not the audited spec; the spec was quietly redefined to fit the model

Audit F-6: "SPLIT opens blank ruled lines on the leaf and the man
writes his own steps." Shipped: ONE text field; blank lines cannot
persist at all — `parseSubSteps` drops empty titles and
`addTaskSubStep` refuses blanks (Builder.md, "the one tangle"). The
lead accepted this as "documented not forced" and the closing
comment reports "F-6 HAND-SPLIT (generator dead, re-enterable)" with
no mention that the ruled-lines half of the spec was never
delivered. Killing the generator: real, acquitted below. The
replacement's name promises the audit's object; the object is a
form field. Name it what it is or build what was ruled.

### 13. MEDIUM — CUE/START: the audit's item-14 conviction stands untouched in the branch, unmentioned at close

Audit item 14: START and CUE are "one handler in two costumes", so
START starts nothing and CUE secretly preempts the head. Branch
`FocusSequencerScreen.kt` today: START's clickable and CUE's
clickable are byte-for-byte the same act —
`onCueTaskToFocus(task.id); onNavigateToDeck()`. F-4 (SMALL, per
the audit) did not ship, was not struck with a reason on the issue,
and appears in neither closing comment. The screen's one commit verb
still starts nothing.

### 14. MEDIUM — Scope vs. price: the issue promised a recomposition; a third of it shipped under the same estimate

Issue body: "Estimate on record: ≈2½ builder-days including F-1",
acceptance: items "closed or struck with reasons, here". The audit's
fix list runs F-1…F-9. Shipped: F-1, F-3 (see Charge 9), F-6 (see
Charge 12). Deferred wholesale to an unspecified batch-10: F-2
(composer/FIRST), F-4, F-5, F-7, F-8, F-9. No closing comment
restates that balance. A reader of the closed issue learns that
Tasks + F-1 was "delivered and CI-proven"; a reader of the audit
learns that most of what the audit ordered is still on the floor.

### 15. MEDIUM — Wave-2's "30 seats" were one lead and two seats; the panel PASS is a single voice

ISSUE-12-WAVE2-REPORT.md roster: of 30 seats, 25 are marked
"LEAD-COVERED" — every Finisher, nearly every Feel specialist, the
Designer, the User, UX, and a dozen named types, all covered by Amt
alone in the wave that claims to have *verified* wave-1. The seats
that actually ran: F1-Verify and Panel-Feel-Critic. The "panel
verdict: PASS, conditional" is one seat's paragraph appended to the
lead's own report. And the programme kept spawning after the
funeral: the Staffel improver's opening comment (6511646624) landed
at 00:15, an hour after the 23:17 closing declared the issue
delivered. Synchronous scrutiny this was not; it was a lead
reviewing his own cell and a roster printed around him.

### 16. MEDIUM — The Designer never saw the code or the audit; her "overrulings" are theatre, and her one law was shipped violated

designer.md source note, her own words: the cell clone "does not
exist on this box at time of writing", "AUDIT-tasks.md could not be
read", the spec "is worked from the cell brief alone". From that
position she produced a full seal-flow spec; the lead then formally
"OVERRULED" her twice (NEXT marker; empty-queue headline) — rulings
against a blind spec, manufacturing the appearance of deliberation.
Worse, her §0 governing law — intention is *either* the verbatim
head title *or* an explicit statement that there was no queued task;
"Never the routine name disguised as an intention" — is exactly
what shipped, inverted: empty queue seals the routine name as the
headline intention, with no provenance printed anywhere
(Finisher-4: "no provenance is printed anywhere"). The lead was
right that "No task queued" would corrupt the Drawer record; he
was not entitled to call the outcome TELL-THE-TRUTH while doing
the thing the cell's own truth spec banned, unamended.

### 17. MEDIUM — The CI verdict is contaminated, and its red was acquitted by comparison to the sickest branch in the studio

Run 40969997299: 157 tests, 33 failed — 21% red under a headline of
"CI-proven". The 31 Robolectric failures are waved off as identical
on `agent/issue-2`; that branch's whole brief was test health, so
"fails there too" acquits nothing. And the verdict is not a verdict
on this cell's diff: the branch absorbed the issue-3 CI workflow +
debug keystore (186318b, 8d2b077, cfe6ee2) and issue-2's stale
test call-site fixes "verbatim" across 5 files (bbadf5b) before the
run. Cross-cell cherry-picks may be pragmatic; presenting the
resulting run as issue #12's proof is not.

### 18. LOW — F-1's first real user is a demo task, and the tests enshrine it

A fresh install seeds three demo tasks, the first ACTIVE
(trunk `MonolithRepository.kt:347-392`). A user who never opens
Tasks gets their first 05:00 routine session titled with demo
content. The genuine Critic named this (Charge 9); the shipped
response was to build the test fixtures *from the seeded titles
verbatim* (Builder.md: ACTIVE "Finish Chapter 4 Problem Set
(#1–6)") — the edge case became the poster case, and no filing
names it as a shipped cost.

### 19. LOW — A test's meaning was replaced while its assertion was preserved

`ExampleRobolectricTest.kt`'s Solar Queue segment asserted 3
sub-steps produced by the generator. The Builder rewrote it to
hand-add 2 steps so the count stays 3 (Builder.md, "Test
adaptation"). The number survived; the behaviour under test —
automatic splitting — was deleted from the world and from the test
in the same motion, and the stated alternative ("keeping the
generator") is a false dichotomy: the third state, rewriting the
test's intent openly as a new test, was always available.

### 20. LOW — The two-routines ruling is derive-true and system-misleading

Plan edge table: "Two routines, same morning — both read the head
as of their own seal". At derive level, true. At system level, the
heartbeat materialises at most one desired session per pass
(`firstNotNullOfOrNull`), and a second overlapping window derives
OPEN_HELD while the first runs (trunk `RoutineDerive.kt:156-158`
logic, retained on branch). The morning the table describes —
sequential blocks each re-reading the queue — is the easy case; the
overlapping case is decided by held-window law the table never
mentions.

### 21. LOW — Emptiness is now printed twice, against the audit's own one-printing law

Audit item 6 killed the bar's second printing of "NOTHING QUEUED":
one printing of any fact. Shipped header prints `0 TASKS` on an
empty queue (Builder.md notes the mono "0" is always composed) a
short scroll above the 112dp blank leaf that exists to say the same
thing. Small. The law was the cell's; the exception is also the
cell's, unremarked.

---

## Acquittals — checked, and the cell stands cleared

- **A1. The audit's two headline code facts are TRUE.** Horizon bar:
  `estimatedMinutes.coerceAtLeast(10)` at trunk
  `BlockitTowerFadersAndCatalogViews.kt:194,198`, sole caller
  `FocusSequencerScreen.kt:189`, composer writes duration 0 — the
  proportions were phantom and the kill dangles nothing (the genuine
  Critic's consumer sweep stands: no analytics/export/GMC reader).
  SPLIT generator: trunk `MonolithModels.kt:168-191` takes `title`
  and never reads it; the composer hard-codes category "Study"
  (`FocusSequencerScreen.kt:141`), which matches no branch, so the
  physics triple shipped for every split. The death sentence was
  earned. Only the prices and the replacements are charged above.
- **A2. F-1's premise, narrowly stated, is TRUE**, and the shipped
  resolver honours its law: pure, DAO-ordered input, ACTIVE/QUEUED
  only, blanks skipped, DEFERRED/COMPLETED never returned (branch
  `RoutineDerive.kt:106-112`), no production default on the new
  parameter, `materialiseRoutineSession` forwards the intention
  faithfully (branch `MonolithRepository.kt:1086-1099`).
- **A3. The SLEEP exception is right and is tested** (branch
  `RoutineDerive.kt:223-224`; `RoutineDeriveTest.kt:468`), and the
  H2 blank guard inside deriveOne (:224 `takeIf { it.isNotBlank() }`)
  is real defence at the right seam.
- **A4. The genuine Critic's Charge 1 was handled honourably.**
  Fabricated citations entered the lead's paperwork; the lead
  retracted them on the issue (comment 6509437213), restored F-6 to
  scope, and carried the retraction at the head of the plan. That is
  the one place this cell's process worked exactly as advertised —
  which is why the later, quieter overclaims in Charges 1, 2, 5 and
  15 have no such excuse.

## What I checked

Issue #12 and all 23 comments via the GitHub API; AUDIT-tasks.md in
full; PLAN-issue-12-tasks-f1.md in full; the genuine Critic's,
Builder's, Innovator's and Designer's cell filings; the wave-2
report; branch code — RoutineDerive.kt, RoutineHeartbeat.kt,
MonolithRepository.kt (materialise :1086, dedup :395-425,
completeTaskAndAdvance :2525, promoteTaskToActive :2667,
accrueActiveTaskSeconds :2423), FocusSequencerScreen.kt (verbs,
HAND-SPLIT field, dead parameters), MonolithViewModel.kt (:342),
MainActivity.kt (:190, 479, 601), RoutineDeriveTest.kt (all 35
tests), RoutinesFinaliseTest.kt (:285), ExampleRobolectricTest.kt —
against trunk for every audit citation relied on. Nothing compiled,
nothing run, per the standing law of this seat.

— Rasp (brutal critic, #12)
