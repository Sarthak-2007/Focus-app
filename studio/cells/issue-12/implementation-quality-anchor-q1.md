# IMPLEMENTATION-QUALITY REVIEW — Issue #12, F-1 (routine sessions read the task queue)

*Seat: Implementation-Quality (Anchor seat Q-1, issue #12), dedicated-100
programme. Spec level only — no code written by this seat, nothing
compiled by this seat. Every verdict below is grounded in code I read
myself on branch `agent/issue-12` (tip at review time: the CI-verdict
commit `2993fbc` plus the dedicated-100 filings through MIP-06's
`0755ba3`), in `studio/plan/AUDIT-tasks.md`, the Amt plan
(`PLAN-issue-12-tasks-f1.md`), the genuine Critic filing, the Designer
spec, RASP's post-close audit, Grund's label analysis, and MIP-06's
maximised spec (`studio/max-impl/issue-12-f1-max.md`).*

**Timing note, stated plainly:** this seat was briefed "before code
exists". On arrival the cell had already built F-1 (waves 1–2), CI had
run (run 40969997299: assemble green, all RoutineDeriveTest cases pass),
and the issue was closed. That changes nothing about the job — it
improves it: implementability verdicts below are not predictions, they
are checked against the built branch as ground truth. Where the built
code and the spec disagree, I mark the SPEC, not the code, as the thing
that fails.

**Verdict vocabulary:** IMPLEMENTABLE (buildable to a top-notch
standard as specced, at the stated price) · IMPLEMENTABLE-WITH-CHANGES
(buildable only with the named change) · NOT-IMPLEMENTABLE-AS-SPECCED
(the spec as written cannot be delivered truthfully at any reasonable
price inside this issue).

---

## 1. The exact data path, queue → seal (verified, file:line at branch tip)

1. **Queue storage.** `focus_tasks` rows; canonical order is the DAO's:
   `ORDER BY CASE status WHEN 'ACTIVE' THEN 0 WHEN 'QUEUED' THEN 1
   WHEN 'DEFERRED' THEN 2 ELSE 3 END, queueOrder ASC, id ASC`
   (`MonolithDao.kt:171–175`, identical for Flow and snapshot queries).
   Status is a String column decoded by `resolvedStatus`
   (`MonolithModels.kt:125–126`, unknown values degrade to QUEUED —
   see §3.11 for why that matters).
2. **Resolution.** `RoutineDerive.resolveHeadTitle(tasks)` — pure,
   list-in/title-out: first row with `resolvedStatus` ACTIVE or QUEUED
   and `title.isNotBlank()`, title returned **verbatim** (no trim, no
   case change). DEFERRED and COMPLETED can never be returned. Null
   when nothing is eligible.
3. **Derive.** `RoutineHeartbeat.reconcileOnce` resolves the head from
   `dao.getAllTasksSnapshot()` and passes it as `headTaskTitle` into
   `RoutineDerive.derive(...)` (`RoutineHeartbeat.kt:126`). The
   parameter has **no default** in either production signature — a
   forgotten call site is a compile error. There are exactly two
   production call sites (verified by grep): the heartbeat, and
   `MonolithViewModel.evaluateTheme` (`MonolithViewModel.kt:80`), which
   reads only `.state`, discards the DesiredSession, and lawfully
   passes `null`.
4. **Intention assignment.** In `deriveOne`, the single
   `DesiredSession` construction site:
   `intention = if (isSleep) routine.name else headTaskTitle?.takeIf { it.isNotBlank() } ?: routine.name`,
   emitted only for an OPEN_IDLE window with remainder ≥ 5 minutes.
   `isSleep` is keyed to `loadoutName == SmartLoadout.SLEEP.name` in
   both derive and heartbeat — one predicate, two places, consistent.
5. **Re-resolve (wave-2 H1).** After the finalise loop's IO, step 3 of
   `reconcileOnce` re-resolves the head on a fresh snapshot and
   overrides via `derivedDesired.copy(intention = freshHead ?: routine.name)`
   for non-SLEEP routines. This — not derive, not the seal — is the
   last queue read that determines the stored string.
6. **Materialise.** `repository.materialiseRoutineSession(desired)`
   (`MonolithRepository.kt:1086`) applies the loadout, then calls
   `startSession(intention = desired.intention, routineId = …,
   windowStart/End = …)` (`:1321`), which inserts the
   `FocusSessionEntity` row with `intention` copied as a detached
   String and `taskId = null`. `startSession` rewrites a blank
   intention to `"Deep Uninterrupted Study"` (`:1348`) — see §3.10.
7. **Seal.** `SessionSealTransition.proveAndSeal` is launched off-pass
   and re-anchors wall/mono/HMAC only; it never touches intention.
   **The intention is therefore frozen at the step-3 re-resolve read,
   persisted at the startSession insert, and the seal adds nothing to
   it.** Any spec or comment that says "snapshot at seal instant" is
   naming the wrong instant by up to the materialise gap plus the
   ≤1500 ms proving window (during which the row already carries the
   final title — harmless, but the words must be exact).

**Path verdict: IMPLEMENTABLE — and implemented.** One construction
site, one materialiser, purity intact (no DAO/Context/clock inside
derive), no schema touch. This is the shape I would have demanded.

---

## 2. Headline verdicts

| # | Spec item | Verdict |
|---|---|---|
| 3.1 | Core F-1: resolver + `headTaskTitle` threading, routine-name fallback | **IMPLEMENTABLE** (proven on branch + CI) |
| 3.2 | SLEEP exception | **IMPLEMENTABLE** (proven; load-bearing nightly) |
| 3.3 | Snapshot guarantee as printed in the plan ("cannot rewrite — by construction") | **NOT-IMPLEMENTABLE-AS-SPECCED** until §3.4 lands |
| 3.4 | ESR-02 guard mirrored into `completeTaskAndAdvance` | **IMPLEMENTABLE-WITH-CHANGES** (the fix is small; its semantics must be stated) |
| 3.5 | Session dedup under F-1 titles | **IMPLEMENTABLE-WITH-CHANGES** (one condition; must ride the F-1 merge) |
| 3.6 | H1 re-resolve where it lives (heartbeat) | **IMPLEMENTABLE-WITH-CHANGES** (extract the decision pure, or formally accept an untestable seam) |
| 3.7 | Empty-queue → routine name (lead) vs "No task queued" headline (Designer) | Lead: **IMPLEMENTABLE**. Designer: **NOT-IMPLEMENTABLE-AS-SPECCED** |
| 3.8 | Routine fires while a hand session runs (OPEN_HELD) | **IMPLEMENTABLE** (state machine already correct) |
| 3.9 | Queue edited mid-window (four sub-windows) | **IMPLEMENTABLE-WITH-CHANGES** (one sub-window is §3.4) |
| 3.10 | Blank / padded / emoji / very long titles | **IMPLEMENTABLE** (two test fixtures owed) |
| 3.11 | Migration & state | **IMPLEMENTABLE** — zero migration; provenance column is a separate, priced change |
| 3.12 | MIP-06 maximised bond (`taskId` + promote-at-materialise + shared resolver) | **IMPLEMENTABLE-WITH-CHANGES** (four named changes; promotion is a new semantic, not a tier of F-1) |
| 3.13 | Designer pre-seal preview; QUEUE-MOVED receipt line; night-before strip | Preview: **NOT-IMPLEMENTABLE-AS-SPECCED** in this issue. Receipt line: **IMPLEMENTABLE-WITH-CHANGES**. Strip: **IMPLEMENTABLE**, batch-10 scope |
| 3.14 | Test story as a whole | **IMPLEMENTABLE-WITH-CHANGES** (derive layer complete; three gaps named in §4) |

---

## 3. Item-by-item

### 3.1 Core F-1 — IMPLEMENTABLE (proven)

The resolver honours the one DAO order the hand path's `activeTask`
projection also consumes (`MonolithViewModel.kt:317`), so the two arms
cannot disagree about who the head is on any queue the DAO can order.
No production default on the parameter; the only default lives in the
test wrapper — exactly right. Cost as built: one extra Room snapshot
read per reconcile pass (derive-time) plus one on materialising passes
— negligible for a user-scale task table on local Room; no perf work
owed. The derive-time read happens even on passes with no open window;
that is the honest price of derive's purity (the input must exist
before derive runs) and I accept it.

One sharp edge the spec must keep: `resolvedStatus` degrades an
unknown/corrupt status string to QUEUED, i.e. *eligible*. A row with a
garbage status can therefore headline a session. Pre-existing model
behaviour, not F-1's creation — noted so nobody "fixes" it inside F-1.

### 3.2 SLEEP exception — IMPLEMENTABLE (proven)

Implemented in derive via the existing `isSleep` local, mirrored in
the heartbeat re-resolve, and tested (`sleepRoutine_withHeadAvailable_
stillSealsTheRoutineName`). Student 1's walk and Grund's second pass
establish this is not an edge case: the queue is loaded *before* the
20:00 SLEEP seal on the same night, so without the carve-out F-1 fails
on its first night. Any future simplification that removes it as
"edge-case clutter" breaks the feature — this sentence is the guard.

### 3.3 The printed snapshot guarantee — NOT-IMPLEMENTABLE-AS-SPECCED

The plan prints: queue edits/reorders/deletions after the seal "cannot
rewrite a sealed session's intention — by construction." Reorder,
delete, and rename acquit (verified: `moveTaskOrder` touches
queueOrder only; `deleteTaskById` promotes a successor without touching
the session; `updateTaskDetails` renames the task, not the session).
**Completion does not acquit** — §3.4. A guarantee with a live exception
on the most common mid-session act (tearing the head task is the
designed ritual) is not a guarantee. Until §3.4 ships, every surface
that repeats this sentence — plan, closing comment, merge notes — is
overclaiming. RASP's BLOCKER 1 is correct and I second it at full
size: the panel made this a merge condition; the closing comment
demoted it to a footnote. Implementation-quality restores it as a
**merge condition**.

### 3.4 `completeTaskAndAdvance` ESR-02 hole — IMPLEMENTABLE-WITH-CHANGES

Verified on the branch: `MonolithRepository.kt:2580–2585` — when the
completed task was ACTIVE and no other ACTIVE remains, it promotes the
next task and executes `currentSession.copy(intention = promoted.title)`
+ `dao.updateSession(...)` with **no seal check**, while its sibling
`promoteTaskToActive` (`:2688–2698`) refuses the identical write when
`sealedSessionIdFromMarker() == activeSession.id`, citing ESR-02.
Worse, its next-up selection falls back to DEFERRED
(`remaining.filter { DEFERRED }.minByOrNull { queueOrder }`) — the one
fallback F-1's resolver was forbidden to use. So tearing the head at
06:00 can retitle the sealed 05:00 session with a *deferred* task's
name. Blast radius, all verified: receipt title is `row.intention`
(`:1629`); the receipt's deflection list matches intercept logs by
`log.sessionIntention == row.intention` (`:1597–1610`), and logs were
written with the then-live intention (`:2351`) — a mid-session rewrite
orphans every deflection logged before it.

**The fix, specced exactly:** mirror the marker guard — promotion of
the task proceeds (the Tasks surface's own bookkeeping, as in
`promoteTaskToActive`), the *session intention write* is refused while
the active row is the sealed row. Three lines plus a test. Two
semantics must be stated in the change, not discovered later:
(a) Grund's origin/pointer split is the correct frame — this fix rules
the sealed intention is **origin** (immutable); the "pointer" the
advance path wants must live somewhere else if it is ever wanted, not
in this column. (b) The DEFERRED fallback in the advance path is a
queue-order question, out of F-1's scope, but once the guard lands it
can no longer reach the session title — the remaining exposure is
queue bookkeeping only.
Price: SMALL. This is the single highest-value change in this review.

### 3.5 Dedup collision — IMPLEMENTABLE-WITH-CHANGES

Verified: `ensureSeededAndSyncPackages` deletes every inactive session
whose key `intention|targetDurationSeconds|completedTasksSummary`
repeats an earlier row (`MonolithRepository.kt:408–418`), no routine
exclusion; it runs at ViewModel init. Pre-F-1, `routine.name`
namespaced routine rows; post-F-1, a routine session and a hand session
for the same task at the same duration with an empty summary collide,
and one is deleted as a "duplicate" on next launch — silent Drawer
history loss. Routine-vs-routine collisions also widen (same head, same
remainder-derived duration). **Fix, specced exactly:** exclude rows
with `routineId != null` from this dedup (they carry frozen window
identity), or extend the key with `routineId|windowStartMillis`. Both
columns already exist — **no migration**. One condition, SMALL. It
must ride the same merge as F-1: shipping F-1 first and the dedup fix
"later" ships a known history-deletion window. (This also strengthens
§3.7: a literal "No task queued" intention would maximise collisions —
every empty-queue session sharing one key component.)

### 3.6 H1 re-resolve — IMPLEMENTABLE-WITH-CHANGES

The re-resolve is the right mechanism and its comment is admirably
honest (it names the residue: sequential DAO reads ≠ one transactional
snapshot). The problem is purely testability: the decision lives inside
`reconcileOnce`, which needs Context + Room, so **no unit test can
reach it** — and indeed none exists. RASP's BLOCKER 2 is correct: the
closing CI claim of "seal-time re-resolve era tests" names tests that
do not exist. **Cure, specced exactly:** extract the step-3 decision
into a pure function on `RoutineDerive`, e.g.
`reresolvedIntention(desired, isSleep, freshHeadTitle, routineName)`,
call it from the heartbeat, and unit-test its four quadrants
(SLEEP × head-present). ~15 lines moved, SMALL. Alternative I will also
accept, if the lead prefers: leave the code, strike the CI claim, and
record the seam as knowingly untested in the merge notes. What I will
not pass is the current state — claim without test.

### 3.7 Empty queue — lead IMPLEMENTABLE; Designer NOT-IMPLEMENTABLE-AS-SPECCED

Lead's ruling (intention = routine.name, the pre-F-1 behaviour) is a
pure widening: every state that sealed before seals after. The
Designer's "No task queued" headline fails on implementation grounds
independent of the lead's record-keeping argument: (a) it would be
*stored* as the session's intention and archived by the Drawer as if a
task had been named; (b) it maximises §3.5 collisions; (c) the
Designer's companion spec (provenance enum distinguishing the two
cases) requires the migration §3.11 parks — so the headline cannot be
delivered truthfully without the column, and with the column it is a
batch-10 change, not F-1. The fallback is also already the behaviour
every existing user has; changing the empty-queue string is a visible
behaviour change wearing a bug-fix's clothes.

### 3.8 Routine fires while a hand session runs — IMPLEMENTABLE

derive returns OPEN_HELD (active session with a different/no routineId,
or an overlapping session), `desiredSession = null`, the heartbeat
materialises nothing — the routine never steamrolls a live hand
session (existing test `openWindow_heldByManualSession_neverSteamrolled`).
The follow-through also works by construction: if the hand session ends
mid-window, the next reconcile pass finds the window unclaimed and
OPEN_IDLE and materialises at remainder (≥ 5 min rule still applies) —
no state needs to remember the held window, because derive is
stateless. If the hand session outlives the window, the closed window
is judged by the pre-existing J-rules; F-1 changes nothing there and
must not be made to. One test is owed for completeness, not
correctness: OPEN_HELD *with a head present* asserting
`desiredSession == null` (the existing held test predates the head
parameter's meaning). SMALL.

### 3.9 Queue edited mid-window — the four sub-windows

- **Before derive:** live by construction — the derive-time read sees
  the queue as it stands. IMPLEMENTABLE (proven).
- **Between derive and materialise (finalise IO):** covered by the H1
  re-resolve (§3.6), modulo its test gap. IMPLEMENTABLE-WITH-CHANGES
  (the §3.6 change).
- **Between re-resolve and the startSession insert:** a genuine residue
  — `applySmartLoadout` does IO inside `materialiseRoutineSession`
  between the read and the insert. A head torn in that sub-second
  window still headlines the session. This is *acceptable* — every
  snapshot has a last instant — provided the spec names the snapshot
  instant as the re-resolve read (§1, step 5) instead of "the seal".
  IMPLEMENTABLE with the wording correction.
- **After the seal:** frozen, except completion (§3.4). After §3.4,
  fully frozen. The Designer's §3.3 QUEUE-MOVED receipt line is *not*
  required for truth — the frozen headline is the record; see §3.13.

Also ruled correctly in the delivered spec and confirmed here: sealing
consumes, completes, promotes, and dequeues **nothing**; two routines
in one morning read the same unconsumed head at their own
materialisations (tested). Mid-edit unsaved keystrokes are not queue
truth — the seal reads saved rows only, which is what a DAO snapshot
means. App killed/rebooted between window-open and seal: no preview
state exists to go stale (there is no persisted preview — §3.13), the
post-restart reconcile reads the queue fresh. Clock changes do not
touch the intention string at all.

### 3.10 Titles: blank, padded, emoji, long — IMPLEMENTABLE, two fixtures owed

Resolver skips blank/whitespace-only titles to the next eligible row;
all-blank → null → routine name (tested). H2's in-derive
`takeIf { isNotBlank() }` guard means even a hypothetical future caller
passing a blank string cannot seal a blank (tested:
`fastedBlock_blankHeadPassedDirectly_fallsBackToRoutineName`). Two
residues: (a) titles are returned **verbatim including surrounding
whitespace** — `"  Physics  "` seals with its spaces. That is the
truthful choice (the man's own words), but no fixture pins it; add one
padded-title test so a future "helpful trim" fails loudly. (b) The
`startSession` fabrication (`"Deep Uninterrupted Study"`) is
unreachable via the routine path after H2 — *unless the routine's own
name is blank*, a pathological row no seed produces. I do not require
a fix; I require the merge notes to state the fabrication still exists
on the hand path and is out of F-1's scope. Emoji-only titles count as
visible text and seal verbatim — correct. Very long titles: storage is
never truncated (String column); display truncation is a surface
concern already owned by Shield/receipt rendering, unchanged by F-1.

### 3.11 Migration & state — IMPLEMENTABLE, zero migration

DB stays at version 12; the diff touches no database file (verified in
the branch diff stat). `intention` already exists as a String on the
session entity. Old rows keep their routine-name intentions — no
backfill, no relabelling; that is the only honest treatment of history.
`taskId` stays null for routine sessions in delivered F-1. Time accrual
is unaffected and was already queue-aware (`accrueActiveTaskSeconds`
credits the ACTIVE/first-QUEUED task during any session) — F-1 changes
the *title*, not the accounting, and the spec should stop saying "the
routine path never reads the queue" without the qualifier "for
intention" (the genuine Critic's correction; I uphold it).
**Provenance column** (Designer's `QUEUE_HEAD / NONE / MANUAL` enum):
a real migration to v13 with an honest `NOT RECORDED` default for old
rows, which must be composed against other lanes' claims on
MIGRATION_12_13 — MEDIUM, Ren's merge call, **not** F-1 scope. Parked
is the correct state; unpark it only as its own priced change.

### 3.12 MIP-06 maximised bond — IMPLEMENTABLE-WITH-CHANGES

MIP-06 proposes: one shared `QueueHead` resolver; derive proposes,
materialise re-resolves authoritatively; the session is bonded via the
existing `taskId` column inside startSession's birth; the head is
promoted to ACTIVE in the same unit of work. I verified the load-
bearing fact myself: **`FocusSessionEntity.taskId` has zero readers in
`app/src/main`** (the `WallAccrual.kt` hits are an unrelated class's
own field). Consequences, itemised:

1. **The bond write itself: IMPLEMENTABLE, cheap.** Column exists
   (landed 8→9, "this build lands the column only"), nullable, no
   backfill — writing it needs a `startSession` parameter plus a
   materialise pass-through; no migration, DB stays v12. But because
   nothing reads it, today it is a *record-only* write. Any sentence
   claiming the bond "enables" completion behaviour is speculative
   until a reader is specced. Spec it as record-only, under the same
   one-writer law the seal-manifest columns already carry (written
   exactly once, inside startSession, never updated after).
2. **Promote-at-materialise: NOT a tier of F-1 — a new queue-lifecycle
   semantic.** Delivered F-1's guarantee is "sealing consumes/promotes
   nothing" (plan rulings 5–6 struck the bond twice, the second time
   explicitly as claim semantics returning through the back door).
   Promotion mutates the queue the man sees, changes the hand path's
   `activeTask` projection, and interacts with
   `completeTaskAndAdvance`'s early return when an ACTIVE already
   exists. It may well be desirable — but it must be ruled as its own
   change with its own 1 AM walk, not slipped in as "maximisation".
   As specced inside the F-1 maximisation: **NOT-IMPLEMENTABLE-AS-
   SPECCED** (wrong vehicle). As its own issue: likely
   implementable-with-changes.
3. **One shared resolver: IMPLEMENTABLE-WITH-CHANGES.** Unifying the
   three embryonic head definitions is genuinely good — but the
   repository's promotion path picks `minByOrNull { queueOrder }`
   *without* the DAO's id tiebreak and the VM consumes the Flow order;
   a shared resolver changes hand-path behaviour on tied queueOrders.
   Price it as a refactor of the hand path with regression tests, not
   as a free tidy.
4. **Tier-2 provenance + frozen receipt print:** the §3.11 migration
   plus receipt rendering changes — MEDIUM, merge-coordinated, own
   ruling. Its "sealed intention frozen on all advance paths" clause
   is exactly §3.4 — which should not wait for Tier 2; §3.4 ships
   first, alone.

MIP-06's Tier 0 naming of the delivered work ("correct, and the tiers
price what it doesn't yet do") is fair. My ruling keeps Tier 0 as
*the* F-1 delivery; Tiers 1–2 are future issues, each re-priced under
the changes above.

### 3.13 Designer surfaces — mixed

- **Pre-seal preview (Designer §1.2): NOT-IMPLEMENTABLE-AS-SPECCED in
  this issue.** No preview surface exists; `evaluateTheme` discards
  the DesiredSession and there is no "window open, unsealed" UI state
  anywhere in the app. Building one is a new Shield surface with its
  own design/build/verify cycle — a fine future issue, a dishonest
  line item inside F-1's SMALL–MEDIUM price. The plan was right to
  strike the preview-parity claim.
- **QUEUE MOVED AFTER SEAL receipt line (§3.3): IMPLEMENTABLE-WITH-
  CHANGES.** Lawful only if the rendered line is *persisted at
  finalise* (a stored string), never recomputed live in the Drawer —
  live recomputation falsifies the record the second the queue moves
  again. LOW–MEDIUM. Not in F-1's price; batch-10 candidate.
- **Night-before Tasks strip (§2.3): IMPLEMENTABLE, LOW** — reads
  existing routine + queue state, lives only on Tasks. Batch-10
  scope; no objection.

### 3.14 What F-1 deliberately does not do — confirmed buildable-by-omission

No notifications, no queue editing on the Shield, no auto-advance, no
taskId, no provenance. Each omission is a scope wall I uphold: every
one of them re-enters only as its own priced change above.

---

## 4. Test story

**What exists (verified in `RoutineDeriveTest.kt`):** 35 test
functions — the 19 pre-existing derive cases with their meaning
preserved (the test-local wrapper is the only place a
`headTaskTitle = null` default exists), 7 resolver cases (seeded
morning head; DAO order — ACTIVE ahead of QUEUED regardless of
queueOrder; DEFERRED-only → null; COMPLETED never headlines;
blank-head skip; all-blank → null; empty → null), and 9 derive cases
(head taken with window/profile/remainder unchanged; empty-queue and
DEFERRED-only fallbacks; blank passed directly → fallback; snapshot
purity — same inputs, same intention; two routines one morning, queue
unconsumed; SLEEP with head → routine name; head does not resurrect a
sub-5-minute remainder; head does not override a pause).

**What CI proved:** run 40969997299 — assemble debug green; unit tests
compile and execute; **zero RoutineDeriveTest failures**. The run's 33
failures are 31 Robolectric classMethod environment failures
(studio-wide, identical on agent/issue-2) + 2 pre-existing shared
failures (ChromeWaveUnitTest, TruthCar3SweepTest) — neither is this
cell's code, both are named blockers for Ren/Methods, and neither
weakens the F-1 verdict because F-1's tests are pure JVM derive tests.

**Gaps (each with its cure):**
1. **H1 re-resolve: no test, none possible where it lives.** Cure:
   §3.6 extraction (preferred) or a struck claim + recorded seam.
2. **`completeTaskAndAdvance` guard (§3.4): no test exists for either
   funnel's seal behaviour.** A repository-level test needs the
   Robolectric runner, which is currently broken studio-wide — so the
   §3.4 fix ships with a written test that cannot go green until the
   runner is fixed. That sequencing must be stated at merge, not
   discovered after.
3. **Dedup (§3.5): no test.** Same runner dependency as (2); the fix is
   small enough to verify by inspection + a written test parked beside
   it.
4. **OPEN_HELD with head present (§3.8)** and **padded-title verbatim
   (§3.10):** pure-JVM fixtures, addable today, SMALL. These two I
   require before merge — they are cheap and they pin semantics future
   editors will otherwise "improve" away.

**Test-story verdict: IMPLEMENTABLE-WITH-CHANGES** — the derive layer
is genuinely well-tested (fixtures are the real seeded morning, not
toy data); the seams above it are where the story thins, and I have
named each one.

---

## 5. Merge conditions (for Ren — numbered, in order)

1. **§3.4 lands with F-1, or F-1 does not ship.** The ESR-02 guard is
   mirrored into `completeTaskAndAdvance`; promotion proceeds, the
   sealed intention write is refused. (Panel condition, RASP BLOCKER 1,
   this seat — three independent seats, same condition.)
2. **§3.5 lands in the same merge.** Routine rows excluded from the
   seed-sync session dedup, or the key extended. No F-1-first window.
3. **§3.6 resolved one way or the other:** pure extraction + tests, or
   the CI claim struck and the untested seam recorded in merge notes.
4. **Two pure fixtures added** (§3.8 held-with-head; §3.10 padded
   verbatim) — both green on the existing CI route.
5. **Wording corrected wherever it travels:** snapshot instant = the
   heartbeat's materialise-time re-resolve read; "the routine path
   never reads the queue" gains "for intention"; ZIP-TEAR stays
   labelled SPEC-ONLY — UNBUILT (unaffected by this review's scope,
   restated because the plan's threshold ruling spends authority on
   an unbuilt mechanism).

**Total price of my conditions:** §3.4 SMALL (3 lines + test), §3.5
SMALL (one condition + test), §3.6 SMALL (~15 lines moved + 4 tests)
or zero-with-honesty, §4 fixtures SMALL. Nothing here reopens the
design, touches the schema, or delays F-1 by more than a builder-day
in the worst case — and every item removes a way the shipped app could
lie or lose history. That is the cheapest truth available on this
issue.

## 6. Costs I am explicitly NOT imposing

No migration. No provenance column. No taskId bond. No preview
surface. No receipt-line computation. No perf work. No re-litigation
of the lead's overrules (empty-queue headline, NEXT marker, claim
token, Eve Snapshot) — each was checked against the code and each
overrule stands on implementation grounds as well as the lead's.

— Implementation-Quality (Anchor seat Q-1, issue #12)
