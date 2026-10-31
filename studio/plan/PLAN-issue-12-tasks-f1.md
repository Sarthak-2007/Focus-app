# PLAN — Issue #12: Tasks + F-1 (cell lead: Amt)

*Cell: Amt (lead) · Designer · Innovator · Critic · Builder. Paper and
DECLARED code only — nobody compiles; Ren compiles when Sarthak says.
Sources: AUDIT-tasks.md (this repo), issue #12, trunk code as cited.*

**RETRACTION (lead, on the genuine Critic's Charge 1 — read first):**
an early report received by this cell carried fabricated citations
(`TasksViewModel.kt:1219`, `MonolithRepository.kt:2627`,
`FocusSequencerScreen.kt:1060`, and the claim that SPLIT/HAND-SPLIT
had already landed in trunk). The lead integrated two of its claims
into this plan and into the cell's opening issue comment before the
genuine Critic — verifying against the clone — convicted them. Those
claims are RETRACTED wherever they appear; the corrections are worked
into the sections below and a correction comment stands on issue #12.
The genuine Critic filing (critic.md, clone-verified) is the cell's
evidence base. The audit itself was acquitted by the Critic as
evidence-clean.

## F-1 — the fix, final spec

**Premise (verified in code — genuine Critic, clone-checked):**
routine-born sessions seal with `intention = routine.name`
(RoutineDerive.kt:169) and the routine path never reads the task
queue; derive() takes no task input (:79-85), and the materialiser is
`materialiseRoutineSession` (MonolithRepository.kt:1086). The hand
arm honours the queue through the ACTIVE-else-first-QUEUED projection
(MonolithViewModel:317 over the MonolithDao:171 ordering). So "the
head" for F-1 is exactly that projection's first row — status CASE
(ACTIVE, QUEUED, DEFERRED, else), queueOrder ASC, id ASC — with
DEFERRED excluded from candidacy (see the spec).

**The change (Builder implements; lead-verified shape):**
1. `RoutineDerive.derive()` / `deriveOne()` gain ONE new parameter:
   `headTaskTitle: String?` — the title of the first row of the ordered
   task list whose status is ACTIVE or QUEUED (DEFERRED excluded: a
   deferred task is "not today" and must never headline a session), or
   null when no such row exists.
2. `DesiredSession.intention = headTaskTitle ?: routine.name` —
   with one ruled exception: SLEEP routines always take routine.name
   (see the edge-case table). The fallback IS the old behaviour — the
   change is a pure widening: every state that sealed before seals
   after, with the same title it had, unless a queued task now
   honestly names it.
3. Purity preserved absolutely: NO DAO, NO Context, NO clock inside
   derive(). The CALLER resolves the head. Live call sites (verified
   in the branch's code):
   - RoutineHeartbeat.kt:104 — the reconciler's derive() call; it
     already assembles every other derive input and materialises the
     result via `repository.materialiseRoutineSession(desired)`. It
     resolves the head title (one repository/DAO read of the ordered
     task list it can already reach) and passes it in. THIS is the
     seal path; this call site is the fix.
   - MonolithViewModel.kt:80 — CORRECTED by Finisher-1 (N-09): this
     call site is `evaluateTheme()`; it reads only `.state` and
     discards the DesiredSession entirely, and no task list exists in
     its scope. It passes null — not as a degradation but as the only
     lawful value; threading tasks into it would be a dead parameter.
     (An earlier draft of this plan claimed preview/seal parity here;
     that claim is struck. There is no preview of a routine intention
     at this call site to keep honest.)
   - Any future caller passes its best-known head or null; null
     degrades to today's behaviour, never to a crash or a blank.
4. No schema change. `intention` is already a String on the session;
   provenance needs no column this pass (see Parked, below).

**Guarantees (printed so they are never re-litigated):**
- Intention is a SNAPSHOT string written at seal time. Queue edits,
  reorders, deletions after the seal cannot rewrite a sealed session's
  intention — by construction, not by discipline. ONE KNOWN HOLE,
  charged by the genuine Critic and NOT closed by this plan:
  `completeTaskAndAdvance` (MonolithRepository.kt:2525; the rewrite
  inside it) replaces a live session's intention with no marker check,
  while `promoteTaskToActive` refuses the same write under ESR-02 —
  the two funnels disagree and the rewriting one is live. F-1 widens
  the blast radius (a routine session's queue-derived intention can be
  replaced when its head task completes). Whether the ESR-02 refusal
  extends to the advance path is a machinery ruling for Ren; this
  cell flags it at full size and does not silently fork the semantic.
  Related consequence, noted not solved: the session dedup key
  (Repository, ~:408-418) includes intention, so F-1 changes dedup
  behaviour for same-head/same-duration routine pairs — the Builder
  states it in his delivery note if his diff touches that path.
- Sealing NEVER consumes, completes, promotes, or dequeues the head
  task. A routine session carries no taskId; task linkage settles only
  for taskId-bearing sessions. Two routines in one morning therefore
  read the same head — ruled CORRECT: the head names the morning's
  first work; each block re-reads the queue as it stands at ITS seal.
- Empty queue → routine name (above). DEFERRED-only queue → routine
  name (deferred excluded from head candidacy).

## Edge cases — ruled

| Case | Ruling |
|---|---|
| Queue empty at seal | intention = routine.name (old behaviour) |
| Head is DEFERRED only | routine.name (deferred never headlines) |
| Head deleted between window-open and seal | Head is read at seal instant, not window-open — the deleted task is simply not there |
| Queue reordered mid-window, before seal | Seal reads the order as it stands at the seal instant |
| Queue edited after seal | No effect on the sealed session (snapshot) |
| Two routines, same morning | Both read the head as of their own seal; no consumption |
| Head title blank/whitespace | Treated as absent → next candidate row, else routine.name (title hygiene at the DAO read, not in derive()) |
| SLEEP routine | RULED EXCEPTION (lead, on Finisher-1's N-06 flag): SLEEP keeps `intention = routine.name` even when a head exists — the queue head names work, and a sleep session titled by a task corrupts both records. Implemented inside deriveOne via its existing local `isSleep`; tested (SLEEP with head available still seals the routine name). SLEEP's J2 non-finalisation is untouched |

## Lead rulings on cell disagreements

1. **Empty-queue headline — Designer OVERRULED.** The Designer banned
   the routine name as a "fake fallback" and proposed headlining
   "No task queued". Rejected: the routine name is a fact, not a
   fabrication — the session IS the FASTED BLOCK; the queue merely
   contributed nothing. Worse, "No task queued" would be archived by
   the Drawer as the session's intention, corrupting the record.
   What the Designer got right — provenance should be visible — is
   adopted in reduced form under Parked.
2. **ZIP-TEAR threshold — Designer OVERRULED.** Designer specced a 92%
   completion threshold; the audit's ruled ZIP-TEAR spec books at 85%
   crossing in G-01 order (finger traces the perforation in a 32dp
   band, tap retained as accessibility floor). The audit stands.
   And per the Critic: ZIP-TEAR does not exist as code anywhere — it
   remains the house standard for the NEXT tear build (audit
   escalation #2, Ren's call whether it displaces the respin). THIS
   pass carries the F-7 tear respin only.
3. **Provenance column — PARKED (Innovator + Designer, reduced).**
   Both proposed recording intention provenance (queue/routine/hand).
   Real value, real price: a DB migration + honest "not recorded"
   defaulting for old rows. Batch-10's MIGRATION_12_13 is already
   composed of other lanes' columns; adding ours is Ren's merge call.
   NOT in this cell's code. Noted here so the merge can take it or
   strike it on the record.
4. **Per-routine pinned intention — KILLED (Innovator's own kill,
   upheld).** A second queue by another name; the queue IS the pin.
5. **Claim token / claim-on-seal — KILLED (lead ruling).** The
   Innovator's strongest-priced alternative (the seal claims the head
   task: completes or locks it, later routines read the next leaf).
   Rejected for this pass on the Critic's evidence: routine sessions
   carry no taskId today and completion-linkage is a hand-path
   semantic; bolting claim semantics onto F-1 turns a SMALL widening
   into a queue-lifecycle change with failure modes (claimed-but-
   unfinished tasks, SLEEP adjacency) nobody has walked at 1 AM.
   Read-only derivation ships; claiming may return as its own issue
   with its own audit if the morning proves it wanted.

## Addendum — Innovator's full report, ruled (Amt)

The Innovator's priced bundle (innovator.md, cell work dir) reframed
three things; rulings:

6. **Frozen title YES, live bond NO.** P-2 proposed also writing the
   session's existing `taskId` column with the head task's id.
   STRUCK for this pass: `taskId` is not an inert label — completion
   linkage settles through it on the hand path, so a routine session
   carrying it risks silently completing the head task at finish.
   That is the killed claim semantics returning through the back
   door. This pass writes the intention STRING only.
7. **Head resolution must not reuse any "next-up" helper that falls
   back to DEFERRED** (Innovator P-6, verified hazard): the head is
   the first ACTIVE/QUEUED row of the ordered list, resolved inline
   at the caller for this purpose. If no such row exists, the answer
   is null — never a deferred task wearing a head's clothes.
8. **The Intention Ladder (P-3) is PARKED as a named shape** —
   Eve-Set > Routine-Pin > Queue-Head > Routine-Name. This pass
   implements only the bottom two rungs, which are the ones the audit
   ordered. The Eve Snapshot (P-5a, head snapshotted into a settings
   slot at night) is KILLED for this pass: a stored copy can go stale;
   the seal-instant read is the truthful one and costs no slot.
9. **Noted hazard, not ours to fix here:** the Innovator found two
   existing funnels disagreeing on seal law (promoteTaskToActive
   refuses to rewrite a sealed intention — ESR-02 — while
   completeTaskAndAdvance's advance path shows no seal check). F-1
   writes intention at seal time only and touches neither funnel;
   the disagreement predates us and is logged here for Ren's
   machinery queue.

## Scope of this pass (issue #12 acceptance)

- F-1: IN — spec above, Builder code + new unit tests (written, unrun).
- Horizon bar + HORIZON header kill (audit F-3): the bar still stands
  in trunk (BlockitTowerFadersAndCatalogViews.kt) — IN, as deletion +
  count rewire to the QUEUE header incl. the SEALED state word. The
  bar's PROPORTIONS die; the `estimatedMinutes` FIELD survives — the
  receipt summary, the Drawer, dial seeding, and undo still read it
  (genuine Critic). The dead `totalQueueMinutes` plumbing spans three
  files and is priced into F-9, not into F-3's "~113 lines".
- SPLIT (audit F-6): IN SCOPE — the earlier narrowing to verify-only
  rested on the retracted fabrication. The genuine state:
  `defaultSubStepsForCategory` is LIVE (MonolithModels.kt:168),
  called at MonolithRepository.kt:2916, wired at the screen — the
  fabricated physics triple still ships for every "Study" task.
  F-6 stands as audited: kill the generator as this screen's
  splitter; SPLIT opens blank ruled lines via the dead onAddSubStep
  (HAND-SPLIT); sub-step strike feedback per F-6/Feel-1 (narrowed
  target + tick).
- ZIP-TEAR: standard CONFIRMED as the audit ruled it; implementation
  is the next tear build's, not this pass. F-7 respin rides this pass
  if the Builder's hand fits it beside F-1 + F-3; else it is first in
  the batch-10 queue, stated here so it cannot silently drop.

## Price

F-1: SMALL — one parameter threaded through derive + its call sites,
one DAO read at the repository seam, tests. No migration, no new
entity, no UI change. F-3: ~113 deleted lines, small rewire. Total
cell estimate stands at the audit's ≈2½ builder-days for the FULL
Tasks recomposition (F-2/F-4/F-9 remain batch-10 scope; this pass
takes F-1 + F-3 + verification, the machinery and the lie, first).

## Closing notes (lead, at cell close)

- **Designer's NEXT head-card marker: OVERRULED** (Finisher-2 +
  audit). Head-ness keeps the audit's closed signal list — raised
  paper, bold title, #01, the accent verb. No NEXT marker.
- **Feel-5's "echo" (twice-daily tick pairing night-set with the
  05:00 seal): REFUSED at the seal, ABSORBED at night** (Feel-2's
  sound law). The seal sound is a bit-identical, test-pinned proof;
  no added tick has a lawful place in it. The night side survives as
  the functional queueing knock (Feel-2 S-3). Feel-5's receipt flag —
  the task title should hold the session record's highest line — goes
  to the merge with the batch-8/9 receipt state, Ren's call there.
- **Feel-2 found a live defect outside F-1:** ADD speaks twice (the
  screen's submitTask latch + the ViewModel's detent). One owner,
  cured inside F-2's composer move (batch-10), priced there.
- **F-7 tear respin did NOT ride this pass.** It stands fully specced
  by Finisher-3 (change list C-1…C-9, residuals R-1…R-7, device
  acceptance protocol) and is FIRST in the batch-10 queue for this
  screen — stated here so it cannot silently drop.
- **HAND-SPLIT tangle (Builder, documented not forced):** persisted
  blank ruled lines are impossible without changing parseSubSteps /
  the sub-step model; the blank lives as the step field, only written
  steps persist. Accepted for this pass; the model change is a
  separate ruled decision if ever wanted.
- **What the branch carries (DECLARED, uncompiled):** F-1 complete
  (resolver `RoutineDerive.resolveHeadTitle`, no production default,
  SLEEP exception, evaluateTheme passes null), F-3 complete (bar
  deleted whole, count from displayQueue, `N TASKS · SEALED`), F-6
  complete (generator killed, HAND-SPLIT wired), 15 new tests + the
  19 existing preserved in meaning. Open for Ren: the
  completeTaskAndAdvance/ESR-02 ruling, the provenance column, the
  dedup consequence — all flagged in the closing issue comment.

## Tests (written, NOT run — nobody compiles)

New RoutineDeriveTest cases: head title taken when present; routine
name on empty queue; routine name on DEFERRED-only queue; snapshot
semantics (derive is pure — same inputs, same intention, regardless
of later queue states by construction).

— Amt, cell lead, issue #12
