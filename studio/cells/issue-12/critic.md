# CRITIC — Issue #12 cell (Tasks + F-1)

*Scope unrestricted, per brief. Every charge below carries file:line evidence
from the cell clone itself — `~/workspace/clone-inspect/builds/cells/issue-12/`,
branch `agent/issue-12`, base commit `8438f5a` — or it does not file.
No code changed, nothing compiled. This file SUPERSEDES the critic.md
previously filed at this path by Amt "from the Critic's delivered report" —
see Charge 1: that version's evidence does not exist in this codebase.*

## Verdict in one paragraph

F-1's premise is TRUE and the fix is worth doing — but the cell's paper
trail around it is rotten: the lead plan and the pre-filed critic record
cite functions, files and line numbers that do not exist, declare SPLIT
already dead while it is live in the clone, and print a snapshot
guarantee the current code breaks. The fix direction itself has four
unpriced risks: SLEEP routines, the unguarded sealed-intention rewrite
in `completeTaskAndAdvance`, the session-dedup key, and a head definition
that must be pinned to the ACTIVE-or-first-QUEUED projection — not to
"the queue" in the abstract.

---

## Charges

### 1. BLOCKER — The pre-filed critic record and the lead plan cite code that does not exist; SPLIT is declared dead while live

Evidence (all checks run against this clone AND against
`~/workspace/clone-inspect/Focus-app`; both greps return nothing):

- Pre-filed critic.md, finding 1: "`repositoryRoutineLaunchOnce`
  (MonolithRepository.kt:2627) inserts the session" — no function of
  that name exists anywhere. The real materialiser is
  `MonolithRepository.kt:1086 materialiseRoutineSession(desired)`.
  Line 2627 in the clone sits inside `reopenStoredTask`'s comment block.
- Pre-filed critic.md, verdict + finding 2, repeated in
  `studio/plan/PLAN-issue-12-tasks-f1.md` ("Premise (Critic-verified)"):
  "`TasksViewModel.kt:1219 startSessionForTask`" — there is no
  `TasksViewModel.kt` in `app/src` and no `startSessionForTask` anywhere.
  The hand path is `MonolithViewModel.armMonolithSession()` passing
  `_intentionText.value` to the arm controller
  (`MonolithViewModel.kt:2203`), fed by the `activeTask` collector
  (`MonolithViewModel.kt:784-786`).
- Pre-filed critic.md, finding 8, repeated in the plan's scope section:
  bar "called from FocusSequencerScreen.kt:1060" — that file is 848
  lines long in this clone. The real call is
  `FocusSequencerScreen.kt:189`; the bar class is at
  `BlockitTowerFadersAndCatalogViews.kt:162`, not ":150".
- Pre-filed critic.md, finding 7, repeated in the plan ("SPLIT: ALREADY
  LANDED in trunk… HAND-SPLIT is shipped behaviour… Do not re-kill a
  dead verb"): FALSE in this clone. `defaultSubStepsForCategory` is
  live at `MonolithModels.kt:168-191` (the `else` branch returns the
  physics triple ending "Check units & boundary conditions",
  `MonolithModels.kt:188`), is called by
  `MonolithRepository.kt:2912-2920 autoSplitTaskIntoMicroSteps`, and is
  wired to the leaf's SPLIT verb at `FocusSequencerScreen.kt:657-664`.
  The composer still hard-codes category "Study"
  (`FocusSequencerScreen.kt:141 onAddTask(clean, 0, "Study", …)`),
  which matches no branch — the audit's fabrication charge stands
  exactly as Amt's audit filed it.

Smallest cure: strike every citation listed above from the plan; return
SPLIT/F-6 to IN scope (the plan's "VERIFY-ONLY" narrowing rests on the
false landing claim, and its price — "≈2½ builder-days" for a pass that
excludes F-6 — is understated by exactly that item); re-verify the plan
against this clone before the Builder writes a line. A plan that
attributes false verification to the Critic is worse than no plan:
it manufactures the convergence the audit process runs on.

### 2. HIGH — F-1's premise: CONFIRMED in code, with one correction to its wording

Evidence:

- `RoutineDerive.derive()` takes no task input at all:
  `RoutineDerive.kt:79-85` — parameters are `routines, nowMillis,
  activeSession, sessions, settings`. The file's own header forbids the
  obvious shortcut: "PURITY, ABSOLUTE: no clock reads, no DAO, no
  Context" (`RoutineDerive.kt:8-12`).
- `DesiredSession.intention` is documented "= routine.name"
  (`RoutineDerive.kt:33`) and set so at `RoutineDerive.kt:169`.
- The only live caller, `RoutineHeartbeat.reconcileOnce`, passes exactly
  those five inputs (`RoutineHeartbeat.kt:104-110`) and materialises via
  `repository.materialiseRoutineSession(desired)`
  (`RoutineHeartbeat.kt:150`), which forwards `desired.intention` into
  `startSession` (`MonolithRepository.kt:1086-1099`, intention at :1091).
  The seal that follows (`RoutineHeartbeat.kt:161-163` →
  `SessionSealTransition.proveAndSeal` → `MonolithRepository.kt:1414
  sealSession`) re-anchors wall/mono/HMAC only (`MonolithRepository.kt:
  1432-1440`) — it never touches intention. So a routine session's title
  is fixed at materialisation as the routine's name. The 05:00 FASTED
  BLOCK failure in AUDIT-tasks.md is real.
- Correction: the hand path does not honour "the head leaf" as a stored
  thing. `MonolithViewModel.activeTask` is a projection — first ACTIVE,
  else first QUEUED, over the DAO-ordered flow
  (`MonolithViewModel.kt:317-320`; ordering at `MonolithDao.kt:171`:
  status CASE ACTIVE→QUEUED→DEFERRED, `queueOrder ASC, id ASC`). Any
  tapped task becomes that head by promotion
  (`MonolithRepository.kt:2667 promoteTaskToActive`).

Smallest cure: the fix spec must define head as that exact projection —
ACTIVE if one exists, else the lowest-`queueOrder` QUEUED row,
DEFERRED never — or F-1 will implement a second, divergent definition
of "head" and the two arms will disagree on edge queues.

### 3. HIGH — The SLEEP carve-out is missing, and the plan's SLEEP row is false

Evidence: `deriveOne` builds a `DesiredSession` for every OPEN_IDLE
window with remainder ≥ 5 (`RoutineDerive.kt:162-171`); `isSleep` gates
only finalise and judgement (`RoutineDerive.kt:109-124`), never the
desired session. SLEEP sessions materialise and seal through the same
door — the heartbeat's own comment: "the SLEEP 20:00 seal sounds,
bit-identically" (`RoutineHeartbeat.kt:152-163`). PLAN-issue-12's edge
table claims "SLEEP seals nothing new in this pass — no behaviour
change intended". Wrong: under a uniform F-1, the 22:30 SLEEP routine
seals titled with tomorrow's first study task, and that string prints
on the shield notification (`MonolithShieldService.kt:143 "Monolith •
${updated.intention}"`) and as the receipt title
(`MonolithRepository.kt:1629 title = row.intention`).

Smallest cure: one line of spec — head-title intention applies only to
non-SLEEP loadouts; SLEEP keeps `routine.name` — plus one derive test.
Without it, F-1 "fixes" the morning by lying about the night.

### 4. HIGH — The plan's printed snapshot guarantee is already broken by `completeTaskAndAdvance`

The plan guarantees: "Queue edits, reorders, deletions after the seal
cannot rewrite a sealed session's intention — by construction."
Reorders and deletions acquit (Charges, Acquitted §A4). Completion does
not: `completeTaskAndAdvance` promotes the next task and rewrites the
active session's intention unconditionally —
`MonolithRepository.kt:2580-2585 currentSession.copy(intention =
promoted.title)` — with no seal check. Its sibling
`promoteTaskToActive` refuses exactly that write under seal, citing
ESR-02: "a SEALED session's intention is the receipt's title source —
rewriting it under the seal would falsify the slip"
(`MonolithRepository.kt:2688-2698`). Two funnels, opposite laws, same
column.

Consequence chain for a routine-born session under F-1: user tears the
head task mid-session → intention silently becomes the next task →
receipt title changes (`MonolithRepository.kt:1629`) → and the
receipt's deflection list, which matches intercept logs by
`log.sessionIntention == row.intention`
(`MonolithRepository.kt:1597-1610`), drops every deflection logged
under the original title (logs are written with the then-live
`active.intention`, `MonolithRepository.kt:2351`).

Smallest cure: mirror the ESR-02 marker guard from
`promoteTaskToActive` into `completeTaskAndAdvance` (three lines), in
this pass — F-1 makes the routine session the common case, so the
latent inconsistency becomes a daily one.

### 5. MEDIUM — F-1 names a session for a task it is not bonded to, and nobody priced the bond

Evidence: `FocusSessionEntity.taskId` exists (`MonolithModels.kt:323`)
with the comment "Writer is the Arm Ticket path; this build lands the
column only." `startSession`'s signature has no taskId parameter
(`MonolithRepository.kt:1321-1329`), and `materialiseRoutineSession`
passes none. A routine session under F-1 will carry a task's title as
its intention while `taskId = null`, the task stays ACTIVE, and —
nuance the audit missed — the queue is NOT wholly ignored by routine
sessions today: the progress path credits the ACTIVE/first-QUEUED task
with wall time during any session, routine ones included
(`MonolithRepository.kt:2423-2444 accrueActiveTaskSeconds`). The
premise is about intention at materialisation, not about "the routine
path never reads the queue" full stop; the plan should stop saying
the latter.

Two routines, one morning: the heartbeat materialises at most one
desired session per pass, first in derivation order
(`RoutineHeartbeat.kt:139`), and derivations follow routines ordered
by `startMinutes ASC` (`MonolithDao.kt:290-294`); a second overlapping
window derives OPEN_HELD while the first runs
(`RoutineDerive.kt:156-158`). So sequential routines re-read the head
at their own materialisation — the plan's ruling is acceptable, but
its stated reason ("each block re-reads at ITS seal") names the wrong
instant: the snapshot is taken at materialisation, and the seal
completes up to 1,500 ms later (`SessionSealTransition.kt:245,
ANNUL_MS`). A head torn in that window still headlines the sealed
session. Honest, if named; a lie, if the spec keeps saying "seal".

Smallest cure: spec names materialisation as the snapshot instant;
the taskId bond is explicitly deferred with its price noted, not
silently absent.

### 6. MEDIUM — F-1 widens a session-deleting dedup collision

Evidence: `ensureSeededAndSyncPackages` deletes every inactive session
whose key `intention|targetDurationSeconds|completedTasksSummary`
repeats an earlier row (`MonolithRepository.kt:408-418`), and it runs
at ViewModel init (`MonolithViewModel.kt:768`). Today `routine.name`
namespaces routine rows away from hand rows. Under F-1 that separation
dies: a routine FASTED BLOCK session titled with the head task, at the
same remainder-derived duration and an empty summary, collides with a
hand session of the same task and one of them is deleted as a
"duplicate" on next launch. Routine-vs-routine collisions pre-exist;
F-1 adds the cross-kind case and removes the only distinguishing
string.

Smallest cure: exclude `routineId != null` rows from that dedup (they
carry frozen window identity, `MonolithModels.kt` routine fields), or
add `routineId|windowStartMillis` to the key. One condition; cheaper
than the history it saves.

### 7. MEDIUM — Purity threading is real machinery; the plan mislabels a call site and undercounts the blast radius

Evidence: adding `headTaskTitle` to `derive()`/`deriveOne()` touches
every caller: `RoutineHeartbeat.kt:104` (the seal path — correctly
identified by the plan), the test helper at
`RoutineDeriveTest.kt:81`, and `MonolithViewModel.kt:80` — which the
plan calls "a deriveOne() call on the preview/state path" whose
previewed intention must "match what the seal will write". It is
`evaluateTheme`, a top-level theme function
(`MonolithViewModel.kt:70-90`); it reads only `.state` from the
derivation and never touches `desiredSession` or intention. Threading
a live head title into a theme check would give SLEEP-theme evaluation
a task-queue dependency for zero benefit; passing `null` there is the
honest move, and the plan should say so instead of inventing a preview
that doesn't exist.

Smallest cure: parameter with NO default value, so every present and
future caller must state its head knowledge explicitly; `null` at the
theme site with a one-line comment. A defaulted parameter is how the
next caller silently degrades to routine-name and F-1 quietly un-fixes
itself.

### 8. MEDIUM — ZIP-TEAR: the "house standard" is vapour as code; only the name and three numbers exist

Evidence: zero occurrences of "zip" in `app/src/main` or `app/src/test`
(grep -ri, both trees). The tear that exists is: tap → book at the tap
→ sound → 260 ms ghost (`FocusSequencerScreen.kt:145-157`), ghost
motion = `specA` 180 ms alpha fade + 10 dp drop
(`FocusSequencerScreen.kt:500-544`), dash settle `specB` 260 ms
(`FocusSequencerScreen.kt:505-509`), perforation at
`FocusSequencerScreen.kt:772`. No finger tracing, no 32 dp band, no
dash consumption behind a finger, no 85 % crossing — those exist only
as Vega's proposal text in AUDIT-tasks.md, Escalations §2 (~180 lines,
unbuilt). The plan's ruling 2 then spends lead authority settling an
85 %-vs-92 % threshold dispute for a mechanism nobody has prototyped,
while conceding in the same paragraph that it "does not exist as code
anywhere".

Smallest cure: label ZIP-TEAR "SPEC-ONLY — UNBUILT" wherever it is
called a standard; F-7's respin (which at least has Weiss's measured
curve against real code) is the only tear work this pass; threshold
arguments wait for a measured prototype.

### 9. LOW — Blank-head and seed-head edges the fix must not inherit

Evidence: if head resolution ever yields a blank string,
`startSession` substitutes a fabricated generic —
`MonolithRepository.kt:1348 intention.ifBlank { "Deep Uninterrupted
Study" }` — a worse lie than the routine name. The plan puts title
hygiene "at the DAO read, not in derive()"; one guard in derive
(blank → routine.name) is defence in depth at the exact point the
value becomes history. Separately: a fresh install seeds three demo
tasks, the first ACTIVE (`MonolithRepository.kt:347-392`), and the
ViewModel's default intention is that same seed string
(`MonolithViewModel.kt:231`). Under F-1, a user who never opened Tasks
gets their first routine session titled with demo content. Acceptable
only if named in the spec.

---

## Charge 4 investigation — is the Horizon/SPLIT kill safe? (verdict: yes for the bar, yes-with-care for SPLIT, no for `estimatedMinutes` itself)

- The bar has exactly one caller: `FocusSequencerScreen.kt:189`
  (import :61); class at `BlockitTowerFadersAndCatalogViews.kt:162`;
  the phantom floor is real — `estimatedMinutes.coerceAtLeast(10)` at
  :194 and :198, so name-only tasks (composer writes 0,
  `FocusSequencerScreen.kt:141`) draw equal segments. No analytics,
  export, or sync consumes the bar or its proportions:
  `compute7DayHistory` sums session `elapsedSeconds` only
  (`MonolithRepository.kt:2361-2400`); the app contains no export path
  (the only "export" hits are a comment recording a killed backup
  export, `CryptoIntegrityManager.kt:17`, and `exportSchema = false`,
  `MonolithDatabase.kt:26`); there is no GMC code in this repo at all.
  Killing the bar dangles nothing.
- BUT the kill's price in the audit/plan ("~113 lines deleted") omits
  the plumbing: the screen's `totalQueueMinutes` parameter
  (`FocusSequencerScreen.kt:77`) is already dead — declared, never
  used in the body; the header prints the locally computed
  `computedTotalMins` (`FocusSequencerScreen.kt:131-133`). An honest
  F-3 also deletes the ViewModel flow (`MonolithViewModel.kt:342-345`)
  and both MainActivity passes (`MainActivity.kt:190, 479, 601`).
- `estimatedMinutes` the FIELD must survive the bar's death. Live
  readers: leaf plate "#NN · Nm" (`FocusSequencerScreen.kt:582-583`),
  "START Nm" (`FocusSequencerScreen.kt:677-678`), dial seeding from a
  tapped task (`MonolithViewModel.kt:1641-1642`), the receipt's
  completed-task summary entry (`MonolithRepository.kt:2601-2604`),
  undo persistence (`UndoDeleteStore.kt:40, 60`), and Drawer surfaces.
  Any builder who deletes the column with the bar breaks receipts and
  the Drawer. The audit never says this out loud; the plan must.
- SPLIT's output is safe to kill: `defaultSubStepsForCategory`'s only
  caller is `autoSplitTaskIntoMicroSteps`
  (`MonolithRepository.kt:2916`); its product lands in
  `notesOrSubsteps`, read only as the leaf's sub-step print/toggle and
  Drawer search (`MonolithDao.kt:212`). No analytics reads sub-steps.
  Two facts the replacement spec needs: (a) the repository function
  OVERWRITES existing steps wholesale
  (`MonolithRepository.kt:2917-2919`) — unreachable from this screen
  only because SPLIT renders solely when `task.subSteps.isEmpty()`
  (`FocusSequencerScreen.kt:657`); HAND-SPLIT must not inherit the
  overwrite shape. (b) `onAddSubStep` is wired at the MainActivity seam
  (`MainActivity.kt:618 viewModel::addTaskSubStep`) and declared on
  the screen (`FocusSequencerScreen.kt:94`) but never invoked inside
  the screen — F-6 is real wiring work in the leaf, not "use the
  existing callback" for free. `addTaskSubStep` itself appends
  correctly (`MonolithRepository.kt:2899-2910`).

## Acquittals — checked and cleared

- A1. The audit's own line citations verified exact: RoutineDerive:169,
  BlockitTowerFadersAndCatalogViews:194/198, UndoDeleteStore's 5,000 ms
  window is as the audit states. Amt's audit (as opposed to the plan
  and the pre-filed critic record) is clean on evidence.
- A2. Audit item 12 (sealed verbs stay live) verified: sub-step toggle
  (`FocusSequencerScreen.kt:610`), SPLIT (:664), CUE/START (:687/:699)
  and DEL (:711) are all `clickable(enabled = !ghost)` with no seal
  condition; only DONE — TEAR is absent under a session (:646-654,
  `if (!isSessionActive)`). The charge stands as filed.
- A3. Desk-clock banner: acquitted of needing F-1. It already prefers
  the live task title over session intention
  (`AirlockAndDisarmOverlays.kt:196, 225-226 deskClockBannerText`) —
  F-1 changes the receipt, the shield notification and the record,
  not what the desk clock shows mid-session. The plan should stop
  implying otherwise.
- A4. Reorder/delete after materialisation cannot rewrite a session
  row: `moveTaskOrder` touches `queueOrder` only
  (`MonolithRepository.kt:2703-2724`); `deleteTaskById` promotes a
  successor without touching session intention
  (`MonolithRepository.kt:2750-2777`); `updateTaskDetails` renames the
  task, not the session (`MonolithRepository.kt:2493-2510`). Only
  completion breaks the snapshot (Charge 4).
- A5. Empty-queue fallback: the plan's ruling (routine name) is
  correct and is genuinely the old behaviour — `headTaskTitle ?:
  routine.name` cannot unseal anything that seals today. The Designer's
  "No task queued" headline would have archived a non-event as an
  intention; the lead was right to overrule it, wrong only about the
  evidence he cited while doing so.
- A6. RoutineDerive's purity, the tear's book-at-the-tap order (G-01,
  `FocusSequencerScreen.kt:145-157`), and MINUTES DIE in the composer
  (`FocusSequencerScreen.kt:136-142`) are honest machinery. F-1 must
  thread a value through the caller, never smuggle a DAO into
  derive() — on this the plan and I agree, and it is the one part of
  the plan whose shape I would not change.

## What I checked (full list)

AUDIT-tasks.md in full; RoutineDerive.kt in full; SessionSealTransition
in full; RoutineHeartbeat in full; MonolithRepository — materialise
(:1086), startSession (:1321), sealSession (:1414), receipt assembly
(:1590-1650), dedup (:395-420), accrual (:2423), addTask (:2444),
complete (:2525), reopen (:2629), promote (:2667), move (:2703),
defer (:2725), delete (:2750), sub-step add/auto-split (:2886-2920),
7-day history (:2361); MonolithDao task/routine orderings (:171, :290);
MonolithModels task/session entities (:101-135, :268-360) and
defaultSubStepsForCategory (:168); MonolithViewModel activeTask
projection (:317), intentionText lifecycle (:226, :784, :1481, :1669-
:2024, :2203); FocusSequencerScreen in full (848 lines);
BlockitTowerFadersAndCatalogViews horizon bar (:157-258);
AirlockAndDisarmOverlays banner (:185-226); ShieldService notification
(:143); ReceiptSheet title (:92); RoutineDeriveTest helper (:81);
PLAN-issue-12-tasks-f1.md and the pre-filed critic.md at this path —
both charged in Charge 1, neither followed as instruction.

DECLARED — paper only, no code changed, nothing compiled.

— Critic (issue #12 cell)
