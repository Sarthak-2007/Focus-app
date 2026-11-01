# FINISHER-5 — F-1 tests, judged to gallery standard (issue #12)

*Slice: the F-1 unit tests for RoutineDerive's new `headTaskTitle`
parameter. Judged as text only — nothing compiled, nothing run.
Checklist: the edge-case table in
`studio/plan/PLAN-issue-12-tasks-f1.md`. House style reference: the
existing `RoutineDeriveTest.kt`.*

## Verdict: SPEC-LEVEL — the Builder's cases are not in the clone yet

Verified in the branch (`agent/issue-12`) at time of judging:

- `grep headTaskTitle app/src/` → **zero hits, anywhere.** The
  parameter does not exist in production code or tests.
  `RoutineDerive.derive()`/`deriveOne()` signatures are unchanged and
  `RoutineDerive.kt:169` still seals `intention = routine.name`.
- `RoutineDeriveTest.kt` is byte-for-byte the trunk suite: 19 tests,
  none touching a queue head, an intention-from-queue, or F-1.
- The plan's Tests section promises exactly four new cases (head
  taken; empty queue → routine name; DEFERRED-only → routine name;
  snapshot/purity). Even when those four land, they do **not** cover
  the plan's own edge-case table — see the matrix. Four of eight
  ruled rows have no promised test and no by-construction argument
  written down anywhere in the plan.

So below is (a) the coverage judgement row by row, (b) the naming
and fixture judgement, and (c) the finished suite specified exactly —
names plus arrange/act/assert in prose — so the Builder can write it
without inventing anything. When the cases land, this file is the
acceptance checklist.

## The morning the fixtures must show

F-1 is about a title, so dummy titles hide the very failure the test
exists to catch. The existing suite's fixtures are generic —
`routine()` defaults to "DAY BLOCKS" 09:00–19:30 and `session()`
stamps intentions `"s$id"` — acceptable for window arithmetic,
**not** for this slice. The finished F-1 cases must use the real
seeded morning, grounded in production code:

- Routine: the actual seed, `FASTED BLOCK`, 05:00–08:00
  (`startMinutes = 300, endMinutes = 480` —
  MonolithRepository.kt:841), STUDY loadout. Seal instant inside it,
  e.g. Wed 05:10 via the suite's existing `at(2, 5, 10)` helper —
  remainder 170 min, comfortably above the 5-minute floor.
- Queue titles: real work, the way his queue actually reads —
  e.g. `"Physics — Rotational Mechanics PYQs"`,
  `"Botany — Cell Cycle, NCERT line by line"`,
  `"Chemistry — Mole Concept drill"`. Never `"Task 1"`, `"head"`,
  `"test title"`. A test that passes with `"head"` as the title
  would also pass if derive returned any non-blank string in half
  the broken implementations; a real title makes the equality assert
  mean something.

## Coverage matrix — every ruled row, accounted for

| # | Ruled case (plan table) | In plan's promised tests? | Derive-level testable? | Disposition |
|---|---|---|---|---|
| 1 | Queue empty at seal → routine.name | Yes | Yes — `headTaskTitle = null` | Case T2 below |
| 2 | Head DEFERRED-only → routine.name | Yes (as stated) | **Only half** — at derive level DEFERRED-only is indistinguishable from empty: both arrive as `null`. The exclusion itself lives in the caller's head resolution (plan ruling 7 / addendum 7) | Derive case T3 **plus** resolver case R1 — without R1 the DEFERRED law is asserted nowhere |
| 3 | Head deleted between window-open and seal | **No** | Indirectly — derive is stateless and the caller reads at the seal instant; a derive test can only simulate the seal-instant read | Case T6 + by-construction argument B1 |
| 4 | Queue reordered mid-window, before seal | **No** | Same as #3 | Case T7 + B1 |
| 5 | Queue edited after seal → no effect (snapshot) | Yes ("purity") | Yes, in the honest form: the first `DesiredSession` is an immutable value; a second derive with a different head cannot reach back into it | Case T5 (the promised "same inputs, same intention" alone is just the existing idempotence test restated — not enough) |
| 6 | Two routines, same morning → both read head as of own seal, no consumption | **No** | Yes — one `derive()` call over two routines with one head; and a second call with an advanced head for the later seal | Cases T8 + T9; no-consumption is B2 |
| 7 | Blank/whitespace head title → treated as absent, next candidate else routine.name | **No** | **No — by the plan's own ruling** hygiene sits at the DAO/caller read, *not* in derive(). A derive test passing `"   "` and expecting the routine name would test behaviour the plan forbids derive from having | Resolver cases R2 + R3. **This is the matrix's sharpest gap: the row most likely to be "covered" by a wrong-layer test** |
| 8 | SLEEP routine → unchanged, never finalised/stamped | Existing suite covers no-head SLEEP (`sleepRoutine_openWindow_stillMaterialises_butNeverFinalises`) | Yes | Case T10: SLEEP **with** a head present — intention follows the same derive path (plan's own words), `finaliseDue` still empty, judgement still null. Without it, an implementation that special-cases SLEEP out of the head rule — or into finalise — passes the whole suite |

### By-construction arguments (must be written in the test file's header comment, not left implicit)

- **B1 — seal-instant read (rows 3, 4).** derive() holds no queue
  state, no window-open state, and no memory between calls; its only
  queue input is the single `headTaskTitle` string the caller
  resolved at the moment of the call. There is no code path by which
  a deleted or reordered-away title could reach an intention, because
  the earlier queue is never an input. T6/T7 demonstrate the
  consequence; B1 is the proof.
- **B2 — no consumption (row 6).** derive() takes the head as an
  immutable `String?` and returns derivations only. It has no DAO, no
  task entity, and no mutation channel (plan §3, purity preserved
  absolutely; addendum 6 struck even the `taskId` bond). Sealing
  cannot consume, complete, promote, or dequeue the head because
  there is nothing in the signature capable of expressing it.
- **B3 — snapshot (row 5).** `DesiredSession.intention` is a copied
  `String` value on an immutable data class, materialised into the
  session row at seal. No reference to the task row survives the
  seal, so no later queue edit has a path to the sealed intention.

## Naming judgement

House style is condition_outcome fragments —
`openWindow_noSession_desiresRemainderSession`,
`closedWindow_noSession_afterEditHorizon_isMissed`,
`sleepRoutine_openWindow_stillMaterialises_butNeverFinalises`.
These are not grammatical sentences, and the F-1 cases should **not**
break the file's idiom to become sentences. The gallery standard for
this file is the idiom done properly: **a name a reader can recite
as the law without opening the body — condition, verb, outcome, no
abbreviations, no test that needs its comment to say what it proves.**
Names like `headTask_taken`, `f1_works`, `sealWithQueue` would fail
this standard outright. The plan's Tests section gives only prose
descriptions ("head title taken when present"), so the naming risk
is real and lands on the Builder — the names in the spec below are
the finished ones; use them verbatim.

One more naming point: every F-1 case asserts on
`desiredSession.intention`, so every name must say *intention* or
*name* in its outcome clause. A name that says only "desiresSession"
would pass review while the assert quietly checked the wrong field.

## The finished suite — specified cases

All in `RoutineDeriveTest.kt`, reusing the existing `routine()`,
`session()`, `at()` helpers and the private `derive()` wrapper —
which itself must gain the `headTaskTitle` parameter (defaulting to
`null` so all 19 existing cases keep their exact meaning; that
default is itself part of the spec: it is the plan's "null degrades
to today's behaviour" guarantee, visible in the harness).

### Derive-level (T)

- **T1 `fastedBlock_withQueuedHeadTask_desiredIntentionIsTheHeadTaskTitle`**
  Arrange: FASTED BLOCK routine as above; now Wed 05:10; head title
  `"Physics — Rotational Mechanics PYQs"`. Act: derive. Assert:
  state OPEN_IDLE; desiredSession non-null; intention equals the
  head title exactly; profileName still `"Routine: FASTED BLOCK"`
  (the head names the intention, it does not rename the routine).
- **T2 `fastedBlock_withEmptyQueue_desiredIntentionIsTheRoutineName`**
  Arrange: same routine and instant; head `null` (the empty queue's
  only honest representation at this layer). Act: derive. Assert:
  intention equals `"FASTED BLOCK"` — row 1, the old behaviour,
  bit-for-bit.
- **T3 `fastedBlock_withNoEligibleHead_desiredIntentionIsTheRoutineName`**
  Arrange/act as T2. Assert as T2. This is the derive-level half of
  the DEFERRED-only row; its name deliberately does **not** claim to
  test DEFERRED, because at this layer it cannot — the DEFERRED half
  is R1. (If the Builder instead names this `…deferredOnlyQueue…`,
  that name lies about its layer and fails this review.)
- **T4 `deriveOne_withQueuedHeadTask_matchesDeriveIntentionForTheSameMorning`**
  Arrange: FASTED BLOCK, Wed 05:10, head `"Botany — Cell Cycle, NCERT line by line"`.
  Act: `deriveOne()` for the routine and `derive()` over the
  singleton list, same head. Assert: both desired intentions equal
  the head title. This is the preview/seal parity the plan §3
  demands of the MonolithViewModel call site — a preview that names
  one task and a seal that names another is a lie with extra steps.
- **T5 `sealedIntention_isASnapshot_laterQueueEditCannotRewriteIt`**
  Arrange: FASTED BLOCK, Wed 05:10. Act: derive with head
  `"Physics — Rotational Mechanics PYQs"` and keep the resulting
  DesiredSession; derive again, same instant, with head
  `"Chemistry — Mole Concept drill"` (the queue as edited after the
  first seal). Assert: the second intention is the Chemistry title
  (the new seal honestly reads the new queue) **and** the first
  DesiredSession's intention still equals the Physics title,
  unchanged. Backed by B3.
- **T6 `headDeletedBeforeSeal_desiredIntentionIsTheSurvivingHead_neverTheDeletedTitle`**
  Arrange: FASTED BLOCK, Wed 05:10; the seal-instant head is
  `"Botany — Cell Cycle, NCERT line by line"` — the row that became
  head when the earlier Physics head was deleted mid-window; the
  deleted title appears nowhere in the inputs. Act: derive. Assert:
  intention equals the Botany title. Backed by B1, which the test's
  comment must state: there was never an input through which the
  deleted title could arrive.
- **T7 `queueReorderedBeforeSeal_desiredIntentionFollowsTheOrderAtTheSealInstant`**
  Arrange: FASTED BLOCK, Wed 05:10; seal-instant head is
  `"Chemistry — Mole Concept drill"` (promoted to the top by a
  mid-window reorder). Act: derive. Assert: intention equals the
  Chemistry title. Backed by B1.
- **T8 `twoRoutinesOneMorning_bothDesiredIntentionsReadTheSameHead`**
  Arrange: FASTED BLOCK (05:00–08:00) and a second STUDY routine the
  same morning, e.g. `"SECOND BLOCK"` 08:30–11:30 is outside a single
  `now` — so use overlapping-at-one-instant construction honestly:
  now Wed 05:10, routines = FASTED BLOCK plus a second enabled STUDY
  routine whose window also contains 05:10 (e.g. `"EARLY DESK"`
  05:00–06:00); head `"Physics — Rotational Mechanics PYQs"`. Act:
  one derive over both. Assert: every derivation that desires a
  session carries intention equal to the head title — neither
  routine's own name leaks into the other's session. No-consumption
  is B2, stated in the comment.
- **T9 `secondRoutineSealingLater_readsTheQueueAsItStandsAtItsOwnSeal`**
  Arrange: FASTED BLOCK, Wed 05:10, head `"Physics — Rotational Mechanics PYQs"`;
  then the same routine list at Wed 05:10 with head
  `"Botany — Cell Cycle, NCERT line by line"` (the queue advanced
  between the two seals). Act: two derives. Assert: first intention
  is the Physics title, second is the Botany title — each block
  re-reads the queue at its own seal (plan guarantee, verbatim law).
- **T10 `sleepRoutine_withQueuedHeadTask_stillNeverFinalises_andIntentionFollowsTheSameRule`**
  Arrange: SLEEP routine exactly as the existing SLEEP case
  (20:00–03:30, SLEEP loadout), now Wed 21:00, a stale unstamped
  SLEEP session from the previous night in the sessions list, head
  `"Physics — Rotational Mechanics PYQs"`. Act: derive. Assert:
  desiredSession non-null; `finaliseDue` empty; judgement null —
  J2 untouched — **and** intention equals the head title: SLEEP's
  non-interference is about finalising and stamping, not about a
  second intention law.
- **T11 `fastedBlock_withQueuedHeadTask_butRemainderBelowFiveMinutes_stillNeverSeals`**
  Arrange: FASTED BLOCK, now Wed 07:57 (3 minutes of window left),
  head present. Act: derive. Assert: desiredSession null. The head
  names sessions; it does not resurrect a window the remainder floor
  has closed. Guards against an implementation that threads the new
  parameter through the seal decision instead of only the title.
- **T12 `pausedFastedBlock_withQueuedHeadTask_desiresNothing`**
  Arrange: FASTED BLOCK paused until 07:00, now Wed 05:10, head
  present. Act: derive. Assert: state PAUSED, desiredSession null.
  Same guard as T11 on the pause path.

### Resolver-level (R) — the caller's head resolution, wherever the Builder puts the seam

These belong to the head-resolution code at the repository/DAO seam
(plan §3: the caller resolves the head; addendum 7: resolved inline
for this purpose, never via a helper that falls back to DEFERRED).
If the Builder's implementation gives this seam no testable home,
that is itself a finding for AMT — rows 2 and 7 then rest on code
review alone, and the issue comment must say so rather than let T3
impersonate coverage.

- **R1 `deferredOnlyQueue_resolvesToNoHead`** — Arrange: an ordered
  task list whose only rows are DEFERRED (e.g. `"Chemistry — Mole Concept drill"`,
  DEFERRED). Act: resolve head. Assert: null — never the deferred
  title. This is the only case in the suite that actually proves
  the DEFERRED exclusion.
- **R2 `blankTitledHead_resolvesToTheNextEligibleRow`** — Arrange:
  ordered list: first row QUEUED with title `"   "`, second row
  QUEUED `"Botany — Cell Cycle, NCERT line by line"`. Act: resolve.
  Assert: the Botany title.
- **R3 `allEligibleTitlesBlank_queueResolvesToNoHead`** — Arrange:
  QUEUED rows with titles `""` and `"  "`. Act: resolve. Assert:
  null (which T2 then turns into the routine name at seal).
- **R4 `activeRowAheadOfQueuedRows_resolvesToTheActiveTitle`** —
  Arrange: ordered list per the DAO's own ordering (status CASE
  ACTIVE < QUEUED < DEFERRED, queueOrder ASC, id ASC —
  MonolithDao.kt:171): an ACTIVE row `"Physics — Rotational Mechanics PYQs"`
  ahead of QUEUED rows. Act: resolve. Assert: the ACTIVE title —
  the head is the first ACTIVE-or-QUEUED row of the ordered list,
  not merely the first QUEUED one (plan §1, the Critic's correction
  to the issue text).

## Gap list — summary for the Builder

1. **G1 (blocking):** the four promised cases do not exist yet in
   the clone; neither does the parameter they test. Suite is
   SPEC-LEVEL until T1–T3 + T5 land at minimum.
2. **G2:** rows 3, 4, 6 of the edge-case table have no promised test
   and no written by-construction argument → T6, T7, T8, T9 + B1, B2
   in the test-file header.
3. **G3:** row 7 (blank title) and the real half of row 2 (DEFERRED
   exclusion) live at the resolver layer, not in derive() → R1–R4.
   A derive-level test claiming either row is a wrong-layer test
   and must not be written.
4. **G4:** SLEEP-with-head (row 8's F-1 half) is untested by the
   existing SLEEP case → T10.
5. **G5:** the head must not leak into seal *decisions* → T11, T12.
6. **G6:** fixtures must be the real morning (FASTED BLOCK 05:00,
   real queue titles), and the private `derive()` test wrapper must
   default `headTaskTitle = null` so the 19 existing cases keep
   their meaning untouched.

**Status: DECLARED** — judged as text, nothing compiled, nothing
run. When the Builder's cases land, re-judge against T1–T12, R1–R4,
B1–B3 above; a suite matching this spec closes F-1's test slice at
gallery standard.

— Finisher-5 (issue #12 cell)

---

## Ready-to-post comment for issue #12 (AMT posts this)

**F-1 tests — Finisher-5 judgement: SPEC-LEVEL, gap list filed**

Checked the branch: the Builder's F-1 cases are not in the clone yet — `headTaskTitle` appears nowhere in `app/src`, and `RoutineDerive.kt` still seals `intention = routine.name`. So instead of a verdict on written tests, here is the finished suite specified exactly, judged against the plan's edge-case table. Full spec: `finisher-5.md` in the cell work dir.

Coverage findings:
- The plan's four promised cases (head taken; empty queue → routine name; DEFERRED-only → routine name; snapshot) cover only half the ruled table. Rows for **deleted head**, **mid-window reorder**, and **two routines one morning** have no test and no written by-construction argument — specified as T6–T9, with the arguments (derive is stateless; the caller reads at the seal instant; derive has no mutation channel to consume a task) to be written into the test-file header.
- **Blank-title head** and the real half of **DEFERRED-only** live at the caller's head-resolution seam, not in derive() — the plan itself puts title hygiene at the DAO read. They need resolver-level cases (R1–R4, incl. ACTIVE-ahead-of-QUEUED). A derive-level test claiming either row would be a wrong-layer test.
- **SLEEP with a head present** is untested by the existing SLEEP case — specified as T10: intention follows the same rule, finalise/stamp behaviour untouched.
- Guard cases T11/T12: a present head must never resurrect a sub-5-minute remainder or override a pause.
- Fixtures must be the real morning — the seeded FASTED BLOCK (05:00–08:00) and real queue titles — and names must state the law in the file's existing condition_outcome idiom, verbatim from the spec.

12 derive-level cases (T1–T12) + 4 resolver cases (R1–R4) specified with arrange/act/assert. When the Builder's cases land matching this spec, the F-1 test slice closes at gallery standard. DECLARED — nothing compiled, nothing run.

— Finisher-5 (issue #12 cell)
