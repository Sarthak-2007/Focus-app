# Issue #12 — FINISHER-1 (code finish): F-1 machinery, finish review

*Cell: Amt (lead) · Seat: Finisher-1, code finish. Paper only —
nothing compiled, nothing run, no code edited by this seat.
Finishers judge and specify; the Builder amends.*
*Binding source: `studio/plan/PLAN-issue-12-tasks-f1.md` (Amt).
Standard: `RoutineDerive.kt`'s own header voice — terse, law-like,
no chatter.*

## Status: SPEC-LEVEL — the Builder's changes are NOT in the clone

Checked first, on the branch `agent/issue-12` in the cell clone
(`issue-12/`): `git status` is clean except the untracked lead plan;
`git diff HEAD` is empty. `RoutineDerive.derive()` /
`deriveOne()` still take five parameters, line 169 still reads
`intention = routine.name`, and `RoutineDeriveTest.kt` has no
head-title cases. Every note below is therefore written against
the trunk line numbers as they stand, with the exact proposed
form the Builder's diff should land in. When the diff arrives,
each note converts to a one-line check: landed as specified, or
amend. Nothing here re-opens a lead ruling.

## Verdict in one paragraph

The plan's shape is finish-clean as specced: one parameter,
caller-resolved, fallback identical to today's behaviour. The
risks are all in the landing, not the design — a stale comment
that becomes a printed lie (N-02), a default value that silently
swallows a forgotten call site (N-03), a head resolved through
the wrong funnel and wearing DEFERRED (N-07), and one plan
sentence about the ViewModel call site that the code contradicts
in a way that matters (N-09: that caller discards the
DesiredSession entirely, so `null` there is not a degradation —
it is the only lawful value). One open flag for Amt, not a
blocker I can rule: SLEEP (N-06).

---

## Line-level notes — RoutineDerive.kt

### N-01 · Header purity paragraph (lines 3–17) — MUST gain one sentence, no more
- **Current:** "Every input arrives as a parameter; the same
  inputs return an equal result, always."
- **Proposed:** append exactly one law-line to that paragraph:
  *"The queue head arrives as a parameter like every other input,
  resolved by the caller at the seal instant; derive() never
  reads the queue."*
- **Why:** after F-1, a reader's first suspicion is that derive
  now reaches into the task table. The header is where this file
  answers suspicions. One sentence, in the existing voice — no
  paragraph, no history of F-1, no issue numbers. The header
  states laws, not provenance.

### N-02 · DesiredSession.intention comment (line 35) — MUST change, or it becomes a lie
- **Current:** `val intention: String,       // = routine.name`
- **Proposed:** `val intention: String,       // = headTaskTitle ?: routine.name — a snapshot, written once at seal`
- **Why:** this comment is the field's contract, read by anyone
  who touches the Drawer or the receipt. Leaving `= routine.name`
  in place after the change prints the old law over the new fact —
  the exact class of lie this audit exists to kill. Keep the
  trailing-comment column alignment of the surrounding fields.

### N-03 · derive() signature (lines 81–87) — parameter LAST, named, NO default
- **Current:**
  ```kotlin
  fun derive(
      routines: List<RoutineEntity>,
      nowMillis: Long,
      activeSession: FocusSessionEntity?,
      sessions: List<FocusSessionEntity>,
      settings: GuardSettingsEntity
  ): DeriveResult {
  ```
- **Proposed:** `headTaskTitle: String?` appended after
  `settings`, and **no default value**.
- **Why, three parts:** (1) Last position preserves the existing
  argument order at every call site — nobody re-reads a shuffled
  signature to find what moved. (2) No default: neither existing
  function in this file defaults any parameter, and a
  `= null` default is a silent-fallback machine — a future caller
  that forgets the parameter compiles clean and seals routine
  names forever, indistinguishable from an honest empty queue.
  The type system must force every caller to say its head or say
  `null` out loud. (3) The name is the plan's — `headTaskTitle`,
  not `headTitle` (head of what?), not `taskTitle` (any task),
  not `intentionOverride` (there is no override; there is one
  derivation with two sources, and the name must not suggest the
  caller outranks the law).

### N-04 · deriveOne() signature + the internal forward (lines 87, 101–107) — same law, verbatim
- **Current:** line 87 forwards positionally:
  `deriveOne(routine, nowMillis, activeSession, sessions, settings)`;
  deriveOne's own signature mirrors derive()'s five parameters.
- **Proposed:** deriveOne gains `headTaskTitle: String?` in the
  same last position, no default; line 87 forwards it. Either
  forwarding style is acceptable, but if the Builder touches line
  87 anyway, named forwarding for the new argument only
  (`headTaskTitle = headTaskTitle`) is the cheapest readability
  win in the diff — the one argument whose provenance a reviewer
  will hunt for.
- **Dead-parameter guard:** the parameter is consumed exactly
  once, at the DesiredSession construction (N-05). If the landed
  diff threads it anywhere else — a log line, a judgement path, a
  state decision — that is scope growth, flagged, not finish.

### N-05 · The seal itself (lines 161–173) — the fallback must read at a glance
- **Current:**
  ```kotlin
  intention = routine.name,
  ```
- **Proposed:**
  ```kotlin
  intention = headTaskTitle ?: routine.name,
  ```
  — inline, in the constructor, exactly this expression. No
  intermediate `val`, no helper, no `takeIf`, no comment on the
  line: the elvis IS the whole law, and it must be visible in the
  one place a reader looks. Any elaboration (blank-checking,
  trimming, a `resolveIntention()` wrapper for a two-source
  choice) buries a glanceable fact under machinery.
- **Purity check that follows from the plan's ruling:** NO
  `isBlank()` / `trim()` guard in derive. Title hygiene lives at
  the caller's DAO read (plan, edge table). A second guard here
  is not defence in depth; it is two owners for one law, and the
  next editor will not know which one is real.
- **Snapshot wording:** if a comment is wanted anywhere in this
  hunk, it belongs on N-02's field contract, not repeated here.
  This file does not say the same law twice.

### N-06 · OPEN FLAG FOR AMT — SLEEP takes the head title too, as specced (line ~120, the `isSleep` branch)
- **Fact:** `derive()` threads ONE `headTaskTitle` to every
  routine in the list. A SLEEP routine in an OPEN_IDLE window
  mints a DesiredSession (existing test:
  `sleepRoutine_openWindow_stillMaterialises_butNeverFinalises`),
  so under the plan's unconditional formula its intention also
  becomes the head title, not `"SLEEP"`.
- **The tension:** the plan's formula (§F-1.2) has no SLEEP
  exception; its edge table says SLEEP intention provenance
  "follows the same derive path but … no behaviour change
  intended, Builder flags any divergence."
- **Finisher's specification:** the Builder lands the formula
  AS WRITTEN — no `isSleep` special-case invented inside
  deriveOne (a hidden branch is worse than either ruling) — and
  states the SLEEP consequence in one sentence in his delivery
  note to Amt. If Amt wants SLEEP exempt, that is a one-line
  lead amendment, applied at the caller or as an explicit,
  commented branch — never smuggled in as finish polish.

---

## Line-level notes — RoutineHeartbeat.kt (the seal path)

### N-07 · Head resolution (before line 104) — inline, at the snapshot reads, through NO existing helper
- **Current:** lines 93–102 assemble every derive input as
  sibling snapshot reads (`routines`, `active`, `sessions`,
  `settings`); line 104 calls `RoutineDerive.derive(...)` with
  named arguments.
- **Proposed:** the head is resolved in that same block, as one
  more sibling read, named identically to the parameter:
  ```kotlin
  val headTaskTitle = dao.getAllTasksSnapshot()
      .firstOrNull { task ->
          (task.resolvedStatus == TaskQueueStatus.ACTIVE ||
              task.resolvedStatus == TaskQueueStatus.QUEUED) &&
              task.title.isNotBlank()
      }?.title
  ```
  and line 104's call gains `headTaskTitle = headTaskTitle`.
- **Why, four parts:** (1) `getAllTasksSnapshot()`
  (MonolithDao.kt:175) already returns the canonical order —
  status CASE (ACTIVE, QUEUED, DEFERRED, else), queueOrder ASC,
  id ASC — so "first ACTIVE/QUEUED row of the ordered list" is a
  filter over that list, never a re-sort in this file.
  (2) Status is compared via `resolvedStatus` /
  `TaskQueueStatus`, the house pattern everywhere in
  MonolithRepository — never raw string literals (`"ACTIVE"`).
  (3) The blank-title rule (plan: blank = absent, next candidate)
  is discharged by the `isNotBlank()` inside the predicate, so a
  blank head falls to the next candidate row, exactly as ruled —
  not to the routine name while a real task waits behind it.
  (4) **Never** route this through any existing next-up /
  advance helper in the repository: at least one of them falls
  back to DEFERRED when no QUEUED row remains (Innovator P-6,
  lead ruling 7). A deferred task headlining a 05:00 seal is the
  one failure this fix must be incapable of.
- **Placement law:** the read sits WITH the other snapshot reads,
  above the derive call — not computed inside the call's argument
  list, not fetched later near materialise. This file's shape is
  "gather, derive, apply"; the head is a gather.

### N-08 · The one comment this file needs (at the N-07 read) — two lines, WHY only
- **Proposed:**
  ```kotlin
  // The seal's head, read at the seal instant: the first
  // ACTIVE/QUEUED row of the ordered queue. DEFERRED is
  // "not today" and never headlines; blank titles are absent.
  ```
- **Why:** everything else about the read is visible in the code;
  what is NOT visible is why DEFERRED is excluded and why the
  read happens here. That is the entire comment. No F-1 label, no
  issue number, no restatement of the filter in prose. If the
  Builder's draft comment runs past three lines, cut it to this.

---

## Line-level notes — MonolithViewModel.kt (evaluateTheme, lines 69–95)

### N-09 · The deriveOne call (lines 80–83) — pass `null`, and the plan's "degrades" framing does not apply here
- **Current:**
  ```kotlin
  com.example.data.RoutineDerive.deriveOne(
      routine, nowMillis, null, emptyList(),
      com.example.data.GuardSettingsEntity()
  ).state in setOf(...)
  ```
- **Code fact the finisher must put on record:** this is
  `evaluateTheme()` — a top-level, pure, state-only evaluation.
  It reads ONLY `.state`; the DesiredSession is discarded whole;
  no task list exists in its scope, and `desiredSession` cannot
  influence `state` on any path through deriveOne (the desired
  session is minted inside one branch whose state is already
  decided). The plan's "previewed intention matches the seal"
  concern therefore has no object at this call site.
- **Proposed:** append the argument as an explicit named value —
  `headTaskTitle = null` (converting this one call to a named
  argument for the new parameter is acceptable and preferred;
  wholesale re-formatting of the call is not). Do NOT thread a
  task list into `evaluateTheme()` to feed a value nothing reads:
  that is a dead parameter wearing a fix's clothes, and it would
  tax every theme evaluation with a queue read for a discarded
  field.
- **Comment:** one short clause is justified here and only here,
  because the `null` will otherwise look like an omission to the
  next reader:
  ```kotlin
  // State only — the desired session is discarded, so no head is resolved.
  ```
  If that does not fit the local comment density (this function's
  header block already explains it consumes window state "from
  the one derivation"), the acceptable alternative is no comment
  and the explicit named `null` carrying the meaning. A bare
  positional `null` added as a sixth argument with neither name
  nor note FAILS finish: it reads as a placeholder awaiting a
  real value, and someone will "fix" it.

---

## Line-level notes — RoutineDeriveTest.kt (new cases)

### N-10 · The test-local derive() helper (lines 75–89) — the ONLY place a default is welcome
- **Current:** the helper forwards five positional arguments.
- **Proposed:** the helper gains
  `headTaskTitle: String? = null` and forwards it. A default
  HERE is correct: it keeps the ~20 existing cases byte-stable
  (they are all, truthfully, "no head known" cases) and makes
  the new cases state their head explicitly. The production
  no-default law (N-03) and this test convenience do not
  conflict; they are the same principle — silence must be
  impossible where the seal is real, cheap where it is not.

### N-11 · New cases — names in the file's existing voice, four cases, no more
The test file names cases as plain sentences in camelCase
(`pauseExpiredMidWindow_windowMaterialisesAtRemainder`). The
plan's four cases land as:
- `openWindow_withHeadTask_sealsHeadTitleAsIntention`
- `openWindow_emptyQueue_sealsRoutineNameAsIntention` —
  head passed as `null` explicitly at the call, not via helper
  silence, so the case proves the contract it names.
- `openWindow_deferredOnlyQueue_sealsRoutineNameAsIntention` —
  **specification for the Builder:** derive() cannot see a
  queue, so this case CANNOT be proven inside RoutineDeriveTest
  as a queue test; what is provable here is that `null` (which is
  what the caller passes for a DEFERRED-only queue, per N-07)
  yields the routine name. Name the case for what it actually
  proves, or place the DEFERRED-exclusion case where the caller's
  filter lives. A test whose name claims a queue behaviour its
  body never exercises is a small lie of exactly the audited kind.
- `derive_headTitleIsSnapshot_sameInputsSameIntention` — the
  purity case: two derives with the same head return equal
  results (the file's existing idempotence case is the model).
  No test may claim "later queue edits don't rewrite the seal"
  inside this file — that is true by construction (the seal
  holds a String, not a reference) and belongs in the delivery
  note's words, not in a test body pretending to exercise it.

### N-12 · Fixture hygiene
- The existing `routine()` fixture name `"DAY BLOCKS"` and the
  head title used in new cases must be visibly different strings
  (e.g. head = `"Botany ch. 4"`), so a swapped assertion cannot
  pass by coincidence. No new fixture machinery; the head is a
  String argument, not an entity — any test that builds a
  FocusTaskEntity to feed derive() has taken a wrong turn and
  broken the design to test it.

---

## Purity & dead-parameter sweep (the whole diff, when it lands)

1. `RoutineDerive.kt` must still contain no `Dao`, no `Context`,
   no `System.currentTimeMillis`, no `Calendar`, no repository
   reference. The parameter is the only new noun in the file.
2. `headTaskTitle` is consumed once (N-05), forwarded once
   (N-04), resolved once (N-07), and passed as `null` once
   (N-09). A fifth appearance anywhere is a finding.
3. No schema, no `taskId` write, no provenance column — all
   parked or killed by the lead (rulings 3, 6). If any appears
   in the Builder's diff, it is not finish drift, it is a ruling
   breach; send it back whole.
4. `materialiseRoutineSession` (MonolithRepository.kt:1086)
   needs NO change: it already writes `desired.intention`
   verbatim. If the Builder's diff touches it for F-1, ask why.

## Close

**Status: DECLARED** — SPEC-LEVEL finish review, complete
against the lead plan and the trunk as it stands. Twelve notes:
nine land-as-specified checks (N-01–N-05, N-07, N-08, N-10,
N-12), one plan-correcting specification (N-09), one
test-honesty specification (N-11), one flag reserved for the
lead's ruling (N-06, SLEEP). Re-review on the landed diff is a
one-pass conversion of these notes, nothing more.

— Finisher-1 (issue #12 cell)

---

## Ready-to-post comment for issue #12 (AMT posts)

F-1 finish review filed (SPEC-LEVEL — the Builder's diff is not
yet on the branch; clone verified clean, so all notes are written
against trunk line numbers with the exact proposed forms).
Twelve line-level notes in `finisher-1.md`, cell work dir. The
shape is finish-clean as specced; the risks are in the landing:
the `DesiredSession.intention` comment (`// = routine.name`)
must change or it becomes a printed lie; no default value on
the new parameter in production signatures, so a forgotten call
site cannot silently seal routine names; the Heartbeat head
read must filter the ordered snapshot inline (ACTIVE/QUEUED,
blank = absent) and never route through a next-up helper that
falls back to DEFERRED. One correction to the plan's framing:
the MonolithViewModel call site is `evaluateTheme()` — it reads
only `.state` and discards the DesiredSession, so `null` there
is the only lawful value, not a degradation; threading a task
list into it would be a dead parameter. One flag for Amt: as
specced, SLEEP routines also take the head title; the Builder
should land the formula as written and state that consequence
in his delivery note rather than smuggling in an `isSleep`
exception. No code edited, nothing compiled. Status: DECLARED.

— Finisher-1 (issue #12 cell)
