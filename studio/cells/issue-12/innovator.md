# Issue #12 — INNOVATOR: F-1 and everything the straight fix misses

*Cell: AMT, issue #12 (Tasks + F-1). Seat: Innovator. No code. No compile.*
*Grounding note: the issue-12 cell clone does not exist on this box, so
`studio/plan/AUDIT-tasks.md` was read from the sibling issue-13 cell clone,
and every code fact below was verified against trunk
`~/workspace/clone-inspect/Focus-app` (RoutineDerive.kt, MonolithModels.kt,
MonolithRepository.kt, MonolithViewModel.kt, ReceiptState.kt).*
*— Innovator (issue #12 cell)*

---

## 0. The straight fix, and its three hidden costs

Straight fix as briefed: in `RoutineDerive.kt`, stop setting
`intention = routine.name` (~line 169); read the task queue's head instead.

It is correct and it is not small in the way it looks. Three costs nobody
has priced yet:

1. **It breaks the file's founding law.** `RoutineDerive.derive()` is
   *pure, absolute* — no DAO, no clock, no Context; every input arrives
   as a parameter (routines, now, activeSession, sessions, settings).
   There is **no tasks parameter**. "Read the queue" inside derive() is
   impossible without either (a) breaking purity — the thing that makes
   a stale/hallucinated caller harmless — or (b) adding the queue as a
   new input parameter and making every caller snapshot it honestly.
   Only (b) is lawful. That is a signature change plus every reconciler
   call site, not a one-line swap.
2. **A string is not a bond.** The straight fix copies the head's *title*
   into `session.intention`. The session row already has a dedicated
   column for the real thing — `taskId` (the Arm Ticket's task bond,
   8→9, Rurik) — and the routine materialise path never writes it
   (`materialiseRoutineSession` passes intention/profile/duration/
   routineId/window, no taskId; the column's writer is the Arm Ticket
   path only). Copying the title alone loses *which leaf* it was the
   moment he renames, reorders, or tears anything.
3. **It inherits two inconsistent seal laws.** `promoteTaskToActive`
   refuses to rewrite a sealed session's intention (ESR-02: the sealed
   intention is the receipt's title source; rewriting falsifies the
   slip). `completeTaskAndAdvance`'s advance path rewrites
   `currentSession.intention = promoted.title` with **no seal check
   visible in that funnel**. F-1 lands a third intention writer on top
   of a disagreement that was never reconciled. F-1 is the moment to
   reconcile it, cheaply, or it gets more expensive forever.

Everything below builds on the straight fix, not instead of it.

---

## P-1 · Intention provenance — the session remembers where its intention came from

**Verdict: SHIP — this is the keystone; half the other proposals are free once this exists.**

**Mechanism, plain words.** Every session carries, alongside its
intention string, one small word saying where that intention came from:
`QUEUE_HEAD` (the pad's head at seal time), `EVE_SET` (he set it last
night, see P-5), `ROUTINE_PIN` (this routine's own pinned task, P-4),
`HAND_SET` (typed/chosen at manual arm), or `ROUTINE_NAME` (fallback —
queue was empty, the routine's own name stood in). One enum, stamped
once at birth, never revised — same discipline as `outcome` and
`routineOutcome`.

**Why it beats the straight fix.** The straight fix makes routine
sessions *usually* right and permanently *unverifiable*. When a session
titled "FASTED BLOCK" appears after F-1 ships, nobody — not Sarthak,
not Ren, not an audit — can tell whether the queue was empty (honest
fallback) or the fix regressed (bug). Provenance turns that argument
into a lookup. It also protects the fallback: an empty queue at 05:00
is not a failure, and the record should be able to say so.

**What it unlocks.**
- *Receipt:* the slip is re-derived from the session row at issuance
  and rehydration (ReceiptState contract — the slip is never stored),
  so a provenance word on the row prints truthfully forever, e.g. a
  quiet line under the title: `FROM QUEUE · SET 01:12` / `ROUTINE NAME ·
  QUEUE EMPTY`. No new receipt storage.
- *Drawer:* the Drawer already titles entries from `row.intention`.
  Provenance lets a finished routine session show *both* facts —
  task title as the headline, routine name as the frame ("FASTED BLOCK
  — Botanical Gardens ch. 4") — instead of one string having to be both.
- *Resumption:* after a reboot or a PARTIAL, rehydration can ask "was
  this intention bonded or borrowed?" before deciding whether anything
  may refresh it (P-7). Without provenance that question is unanswerable.
- *Audits like this one:* F-1 itself was only findable by reading code.
  With provenance, the next F-1 is findable by reading data.

**Spec sketch.** `DesiredSession` gains `intentionSource: String` and
`intentionTaskId: Int?`. `FocusSessionEntity` gains one column
`intentionSource: String = ""` (empty = pre-migration row, prints
nothing — the established -1/0 omission convention from the seal
snapshot columns). `materialiseRoutineSession` stamps both inside
`startSession`'s transaction, from the same snapshot that resolves the
head — one writer, no DAO update path for the column ever, exactly the
Fader writer law already used for `heldAppsAtSeal`.

**Price.** Files: MonolithModels.kt (entity + DesiredSession),
RoutineDerive.kt (resolver, P-3), MonolithRepository.kt
(materialise + startSession), ReceiptState/Deck slip rendering,
Drawer row rendering. **Schema: YES — one column, DB v12→v13, additive
with default, low risk** (same shape as the v10/v11 column landings).
Tests: RoutineDeriveTest (source stamped per branch), a Drawer/
permanence test asserting the provenance word survives rehydration,
Receipt slip test for the omission rule on legacy rows. Test surface:
medium, all in existing test files' neighbourhoods.

---

## P-2 · Frozen title + live bond (the dual representation)

**Verdict: SHIP — pair with P-1; together they are the real F-1.**

**Mechanism.** The session stores *two* things at seal: the intention
**string, frozen** (the title as it read at seal time — this is what
the receipt and the AOD/Deck print, and ESR-02 already forbids
rewriting it), and the **bond** — `taskId` of the leaf it came from,
using the column that already exists. The string is the record; the
bond is the thread back to the living queue.

**Why it beats the straight fix.** Title-only copying fails silently in
three ordinary cases: he renames the head leaf mid-window (record and
queue now disagree, nobody knows which is true); he tears the head leaf
(the session's intention now names a task that no longer exists, with
no way to detect it); two leaves share a title (the bond is ambiguous
forever). Bond + frozen title answers all three: print the frozen
title, follow the bond for behaviour. It also finally gives the routine
path parity with the Arm Ticket path, which already understood this.

**Spec sketch.** Routine materialise writes `taskId = head.id` via the
existing column — **zero schema cost for this half**. Readers that need
behaviour (completion advance, resumption) resolve through the bond;
readers that need print read the frozen string. If the bonded row is
gone at read time, behaviour degrades to "no bond" and print is
untouched — the record never breaks because the queue moved.

**Price.** Files: MonolithRepository.kt (materialise/startSession
writer), any reader that currently string-matches intention (the
repository already has one: attendance-log matching on
`log.sessionIntention == row.intention` — that match gets *stronger*
on the bond, flag it for the builder). Schema: **none**. Tests:
RoutineDeriveTest + a repository test that a renamed head does not
move a sealed session's printed intention. Small–medium.

---

## P-3 · The Intention Ladder — one resolver, an explicit precedence chain

**Verdict: SHIP — this is the form the straight fix should take.**

**Mechanism.** Nobody "reads the queue" ad hoc. One pure function —
inside RoutineDerive, taking the queue snapshot as a new parameter —
resolves a routine session's intention by walking a ladder, first hit
wins:

1. **EVE_SET** — a first-task he explicitly set for this window last
   night (P-5), if it still exists and is not completed/deferred.
2. **ROUTINE_PIN** — a task pinned to this routine (P-4), if it still
   exists and is actionable.
3. **QUEUE_HEAD** — the pad's head, defined precisely in P-6.
4. **ROUTINE_NAME** — the routine's own name. The honest floor, exactly
   today's behaviour, now *labelled* as fallback instead of masquerading
   as an intention.

Each rung stamps its provenance (P-1). A rung whose target has vanished
falls through silently to the next rung — never an error, never a
fabricated title.

**Why it beats the straight fix.** The straight fix hard-codes rung 3
and makes every future refinement (a pin, a night ritual) a new special
case hacked into the seal path. The ladder makes F-1 the *last* time
this logic is written: new sources join as rungs, with precedence
argued once, in one place, under the purity law. It also gives the
audit's other arms parity cheaply — the manual Arm Ticket path can walk
the same ladder's lower rungs later without a second implementation.

**Spec sketch.** `fun resolveIntention(routine, tasks, eveSetTaskId,
now): ResolvedIntention(text, taskId, source)` — pure, in
RoutineDerive.kt, called by `deriveOne` when minting a DesiredSession.
`derive()` signature gains `tasks: List<FocusTaskEntity>`; the
reconciler caller snapshots the queue in the same read it already does
for sessions/routines (no new race window: the snapshot *is* the seal
instant's truth, which is all provenance claims).

**Price.** Files: RoutineDerive.kt (the bulk), its caller(s) in the
repository/reconciler, RoutineDeriveTest.kt (one test per rung + per
fall-through). Schema: none by itself (P-1 carries the schema). Medium
— this is F-1's honest price, finally stated: the audit's SMALL–MEDIUM
was right, but for this shape, not the one-liner.

---

## P-4 · Pinned intention per routine ("this block is always X, until I say otherwise")

**Verdict: PARK — real need, wrong pass. Build the ladder rung now (P-3), the pin UI later.**

**Mechanism.** A routine may carry an optional pinned task (or pinned
title). Its sessions always seal with the pin, regardless of queue
head. Example pressure: a daily revision block whose work is *not*
whatever is on top of a general NEET pad.

**Honest judgement.** The queue-head rule fails exactly here — the head
is a *global* next, a routine is a *scoped* block, and forcing one
global queue to serve every scoped block makes the pad lie by
omission. But: Sarthak's actual routines today are broad study/sleep
windows, not narrow scoped blocks; there is no evidence yet of a
routine whose work is permanently different from the head. Building pin
storage + editor UI now is inventing a user who hasn't appeared. The
ladder (P-3) reserves the rung at near-zero cost; the day a second
queue or a standing revision block appears, the pin ships in one small
pass. Parking the mechanism, not the idea.

**Spec sketch (for when it ships).** `RoutineEntity` gains
`pinnedTaskId: Int? = null` (+ optionally `pinnedTitle: String` for a
pin that isn't a queue leaf at all). Editor: one row on the routine
editor — "First work: [queue head ▾ / choose task…]". Resolver rung 2
as in P-3; a deleted/completed pin falls through to QUEUE_HEAD and the
editor shows the pin as lapsed, never silently re-pins anything.

**Price (deferred).** Files: MonolithModels.kt, RoutineDerive.kt
(rung only — cheap if P-3 lands first), routine editor UI, repository
persistRoutine funnel. Schema: YES (one nullable column on routines).
Tests: resolver fall-through + editor round-trip. Medium.

---

## P-5 · The Eve Snapshot — what the machine does with the night-before gap

**Verdict: SHIP the snapshot; KILL the speculative pre-arm (P-5c).**

The situation, from the audit's own walk: he queues tomorrow's first
task at ~1 AM (in his real timetable: during the night, before the
05:00-class block — his FASTED BLOCK in the audit). The routine fires
hours later, before he touches the phone. That gap is not dead time;
it is the only moment the machine knows his intention *with his own
hand still on it*. Three mechanisms compete for the gap:

### P-5a · Eve Snapshot (SHIP)
**Mechanism.** When the night's last relevant event happens — simplest
honest trigger: the SLEEP routine's window opening, or 22:30, whichever
the builder finds already observed by the chain alarm — the machine
snapshots the queue head's `taskId` into a single pending slot:
"tomorrow's first, as set tonight." The morning routine's resolver
(P-3, rung 1) consumes that slot. Queue edits made *after* the snapshot
— a 3 AM half-asleep reorder, a morning fumble before the block starts —
do not move the morning's intention. What he set *deliberately, last,
at night* is what seals.
**Why it beats the straight fix.** The straight fix reads the head at
*seal time* — which is the moment of *maximum* distance from his
deliberate act and maximum exposure to accidental edits. The snapshot
moves the read to the moment of *intent*. It also creates the audit
trail P-1 prints: `SET 23:41 LAST NIGHT` is a stronger receipt line
than `FROM QUEUE`, because it's true in a way he can feel.
**Spec sketch.** One slot on `guard_settings` (the receipt contract
already lives there — Zettel Z1 precedent: a number, a pointer, a word;
no new table): `eveTaskId: Int`, `eveSetAtMillis: Long`,
`eveForWindowStartMillis: Long`. Written by one funnel when the head
changes during an "eve window" (evening→sleep) or explicitly by P-5b;
consumed-and-cleared by the resolver when a window with that start
seals. Stale slots (window passed unsealed) expire silently.
**Price.** Files: MonolithModels.kt (settings columns), repository
(one writer + resolver consumption), RoutineDerive.kt (rung 1 input),
Tasks screen (the FIRST key from audit F-2 can double as the explicit
set — see P-5b). Schema: settings columns are additive, low risk, same
migration as P-1 if bundled. Tests: snapshot/consume/expire, resolver
rung-1 precedence. Medium.

### P-5b · The "First for tomorrow" ritual key (SHIP, small)
**Mechanism.** The audit already rules a FIRST key into existence
(F-2: composer gains FIRST, landing a leaf at the head — Muede's
errand). Extend its meaning by one notch: pressing FIRST *after the
evening threshold* doesn't just reorder — it writes the Eve Snapshot
(P-5a) and the leaf wears a small `FIRST · TOMORROW` mark until the
morning window seals. One gesture he already makes (queueing the task)
becomes the ritual that guarantees it, with visible confirmation at
1 AM instead of silent hope.
**Why it beats the straight fix.** The straight fix is invisible: he
queues a task and *trusts*. A ritual with a mark converts trust into
verification — he can see, before sleep, that tomorrow is armed. That
is the difference between a pad and a machine that keeps its promise.
**Price.** Files: Tasks screen (key state + leaf mark), repository
(snapshot write). Schema: none beyond P-5a. Tests: UI-state level +
snapshot write. Small.

### P-5c · Speculative pre-arm / pre-materialised session (KILL)
**Mechanism considered.** Mint the session (or the full DesiredSession)
at night, hold it, fire it at window start.
**Why killed.** It fights the architecture's best idea. Derive-don't-
fire exists because a wrong trigger must only ever produce a correct
evaluation of *now*: remainder is computed at materialise
(`durationMinutes = remainderMinutes, NEVER the full window`), the
5-minute MIN_REMAINDER law protects him from Doze's lateness, and a
window claimed once is never re-minted. A pre-minted session freezes
last night's remainder, last night's loadout context, last night's
world — and every protection above becomes a special case. The Eve
Snapshot captures 100% of the user-visible benefit (the intention is
fixed at night) at 5% of the risk (one slot, consumed by the pure
resolver). Kill without regret.

---

## P-6 · Head, defined honestly — and the rules that must never automate

**Verdict: SHIP the definition and the DEFERRED guard; KILL auto-skip and auto-split outright.**

**Mechanism (the definition).** "Head" for intention purposes means:
the ACTIVE task if one exists, else the lowest-`queueOrder` QUEUED
task. **Never** a DEFERRED task — and this guard is not pedantry:
`completeTaskAndAdvance`'s next-up logic *already* falls back to
DEFERRED when no QUEUED task remains. For completion advance that's a
judgement call; for a 05:00 intention it would be a betrayal — he
deferred that leaf *on purpose*, and waking up sealed inside it is the
machine overriding a decision he made awake. **Never** a COMPLETED
task. Ties on queueOrder break by id (the moveTaskOrder sort already
establishes this ordering discipline).

**The huge/stale/recurring head — judged honestly.**
- *Huge head:* unknowable, and the app made it unknowable on purpose —
  MINUTES DIE: tasks carry no estimate, session length lives on the
  wheel, never on the task. Any "this head is too big for this window,
  pick another" logic would have to resurrect estimates or guess from
  sub-step counts. Guessing is the SPLIT generator's sin (fabrication)
  wearing a scheduling costume. **KILL auto-skip / auto-rotate.**
- *Stale head:* a head that has survived several mornings is *data for
  him*, not a trigger for the machine. Lawful form: the receipt/Drawer
  may print the plain fact — the bonded task's carry count ("head for
  the 3rd morning") derivable from the bond + past sessions'
  provenance. No nudge copy, no judgement word, no score — blocker,
  not tracker; the moment the machine editorialises about his stale
  head it has become the thing Monolith refuses to be. **PARK** the
  printed carry-fact (cheap, honest) until the Drawer pass wants it;
  **KILL** any auto-demotion of a stale head.
- *Recurring head:* a task that re-queues itself after tearing is a
  template engine wearing a pad's clothes. The pad's whole honesty is
  that a leaf is torn once and gone. **KILL.** If a real recurring
  need appears, it arrives as P-4's pin, not as an undead leaf.

**Price.** Definition + guard: inside P-3's resolver, ~zero marginal
cost, two tests (DEFERRED never seals; COMPLETED never seals). The
carry-fact print, if ever built: Drawer rendering only, no schema
(bond + history already suffice post-P-2).

---

## P-7 · Bond-first rehydration (resumption tells the truth)

**Verdict: SHIP — small, and it closes a lie the straight fix would open.**

**Mechanism.** When a routine session is rehydrated — process death,
reboot, the reconciler's finalise scan — its intention is read from
*its own row* (frozen string + bond, P-2), never re-resolved from the
*current* queue. Re-resolution looks tempting ("the head changed, the
session should follow") and is a lie in both directions: it would
rewrite a sealed record (ESR-02 forbids) and it would make the morning
un-reproducible at audit time. The resolver runs **once**, at seal.
After that, the row is the truth and the queue is free to move.

**Why it beats the straight fix.** The straight fix says nothing about
resumption, so the first builder to touch the reconciler after F-1
will re-derive — because the derivation is *right there* and looks
authoritative. Writing the law down now ("derive once, at seal; the
row rules after") costs one paragraph in RoutineDerive's header
comment and one test, and prevents the most plausible F-1 regression.

**Spec sketch.** No new state. A test: seal with head A, reorder queue
to head B, simulate rehydration/reconcile, assert intention still A
with source QUEUE_HEAD and bond A. Plus the header-comment law.

**Price.** Files: RoutineDerive.kt (comment), repository reconcile
path (verify-only), one test. Schema: none. Small.

---

## P-8 · Mid-window queue edits — the On-Deck, and the one lawful amendment

**Verdict: SHIP the On-Deck + tear-amendment codification; KILL live intention-following.**

**The problem.** He edits the queue *during* a routine window: tears
the bonded task early, promotes another leaf, reorders. Three candidate
behaviours: (a) the sealed intention follows the queue live — **KILL**:
it falsifies the receipt's title source mid-flight (ESR-02's whole
point) and makes the AOD/Deck intention flicker as he tidies the pad;
(b) the sealed intention is frozen and edits are invisible until the
next session — lawful but mute: he tears his bonded task at minute 40
of 240 and the app pretends nothing happened; (c) frozen intention +
an honest account of the divergence. Ship (c):

**Mechanism 1 — the On-Deck (derived, never stored).** The Deck may
show, quietly, under the sealed intention: `NEXT: <current head>` —
computed live from the queue on every emission, stored nowhere. It is
a *preview of the next seal*, not a claim about this session. When the
bonded task is torn, `completeTaskAndAdvance`'s existing advance
already moves the session to the next task — that path is the second
mechanism:

**Mechanism 2 — tear is the only lawful amendment.** Codify what the
code half-says: a sealed session's intention changes mid-window in
exactly one way — the bonded task is *completed* (torn), and the
advance funnel moves the intention to the next-up task, booking the
completion in `completedTasksSummary` as it already does. Promotion,
reorder, rename, delete of *other* leaves never touch the sealed
intention — which means reconciling the funnel disagreement found in
§0: `promoteTaskToActive` already refuses the sealed intention write;
`completeTaskAndAdvance` must be audited so its advance applies *only*
through the completion door, and any non-completion caller inherits the
refusal. One law, both funnels: **completion amends; editing doesn't.**
The receipt then tells a coherent story for free: title = intention at
seal, summary = what actually got torn, and the final intention is
derivable — no extra bookkeeping, no appendix lines, no lies.

**Why it beats the straight fix.** The straight fix only decides the
window's *first* minute. Most routine windows are hours long; the
queue will move during them. Deciding the amendment law in the same
pass as F-1 means routine sessions behave identically to manual ones
instead of becoming a second species with its own surprises.

**Spec sketch.** On-Deck: one derived line in the Deck's state
assembly (head resolution reuses P-6's definition — another reason the
definition must live in one place). Amendment codification: audit +
guard alignment in MonolithRepository's two funnels, header law in
both. No new columns.

**Price.** Files: MonolithRepository.kt (funnel reconciliation),
MonolithViewModel.kt / Deck rendering (On-Deck line), tests for
"promote during seal does not move intention" and "tear during routine
window advances intention + books summary". Schema: none. Medium —
mostly the funnel audit, which §0 argues is owed anyway.

---

## P-9 · The empty-queue morning — fallback with a face

**Verdict: SHIP the labelled fallback; KILL any invented intention.**

**Mechanism.** Queue empty at seal: intention = routine name,
provenance = `ROUTINE_NAME` (P-1/P-3 rung 4). On the Deck and the
receipt it prints exactly as today — the fallback is not shameful and
gets no warning colours — but the Drawer entry can carry the small
true word `NO TASK QUEUED`, and (the actually useful half) the **night
before** is where emptiness is actionable: if tomorrow has a routine
window and the queue is empty at Eve Snapshot time (P-5a), the Tasks
screen's blank leaf — already kept and acquitted by the audit — is
the surface that says so when he next opens the pad. No push, no
notification invention; Monolith doesn't nag.

**Killed alternative.** Machine-generated fallback intentions
("Review notes", "Free study", a category stencil like the dead SPLIT
triple). Fabrication again — the SPLIT generator died in this very
audit for printing plausible work the man never chose. An honest
routine name beats a synthetic task every time.

**Price.** Inside P-1/P-3/P-5a; the Drawer word is one conditional
print. Schema: none marginal. Small.

---

## Kill register (ideas considered and executed, so nobody rebuilds them)

| Idea | Cause of death |
|---|---|
| Live intention-following (session tracks queue edits) | Falsifies the sealed receipt; ESR-02 already forbids the write. |
| Speculative pre-arm at night (P-5c) | Breaks derive-don't-fire, remainder law, and the 5-minute protection. |
| Auto-skip / auto-rotate a huge or stale head | Huge is unknowable post-MINUTES-DIE; stale is his call, not the machine's. |
| Self-recurring / template tasks | Template engine in a tear-off pad; real need routes to P-4 pin. |
| Machine-invented fallback intention | SPLIT-generator fabrication, same sin, new surface. |
| DEFERRED task as seal intention | He deferred it awake; the machine doesn't un-defer it at 05:00. |
| Provenance stored in profileName string ("Routine: X") | String-parsing identity is Lie #13's corpse; the rebuild already buried it once. |
| A second "routine queue" per routine | Fragments the pad's one promise (one queue, one head); P-4 pin covers the scoped case at 10% of the cost. |

---

## Recommended bundle for the builder (Ren's call, priced whole)

**Ship together, one pass:** P-3 ladder (with tasks as a derive input)
+ P-6 head definition & DEFERRED guard + P-1 provenance column +
P-2 bond write + P-7 derive-once law + P-9 labelled fallback.
That is F-1 done *properly*: **~4 files of machinery
(RoutineDerive, MonolithModels, MonolithRepository, reconciler
caller), one additive migration (v12→v13, one settings/sessions
column set), test surface concentrated in RoutineDeriveTest +
repository seal tests.** Follow in the same pass if room: P-5a/P-5b
(snapshot slot rides the same migration; FIRST key already exists in
the audit's F-2). Then P-8's funnel reconciliation as the pass's
closing item — it is the debt §0 says F-1 otherwise inherits.

**Park:** P-4 pin UI (rung reserved), stale-head carry print (P-6).
**Kill:** everything in the register above, permanently.

## Open questions handed up (not blockers)

1. Eve Snapshot trigger: SLEEP-window opening vs a fixed evening hour —
   whichever the chain alarm already observes; builder to confirm
   against RoutineScheduler, Ren to rule if both are live.
2. Does the manual Arm Ticket path adopt the ladder (P-3) in this pass
   or later? Innovator's lean: later — F-1's scope is routine-born
   sessions; parity is a cleanup, not a correctness gap.
3. Provenance print wording on the slip (`FROM QUEUE` vs a glyph) is a
   finishers' call — the *fact* is machinery, the *typeface* is theirs.

**Status: DECLARED** (paper only; nothing compiled, nothing built).

— Innovator (issue #12 cell)
