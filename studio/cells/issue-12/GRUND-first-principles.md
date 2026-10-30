# GRUND — Issue #12 from first principles: what IS an intention?

*Grund, deep-thinker (dedicated-100), primary on #12. Companion to my
issue comments 6512214755, 6512287187, 6512311077. Evidence: branch
code as cited; cell record (Amt's plan, genuine Critic, F1-Verify,
panel, Feel-4/5, Student 1, Staffel, MIP-06, Welle). Nothing compiled.*

## 0. The accepted framing is wrong

The issue says: *routine sessions must read the task queue.* The cell
built exactly that, and built it well. But "reading the queue" is a
plumbing description of something else, and until the something else
is named, every hard case on this issue gets decided by taste instead
of by derivation. This file names it.

## 1. First principles

**Attention.** At 05:00 the man is fasted, post-meditation, and has
*deliberately emptied his head* — the meditation hour is for emptying
it (Student 1: "I do not hold 'which chapter, which exercise, where
did I stop' in my mind overnight"). A design that requires him to
reconstruct his plan at that hour is not neutral; it taxes the one
resource the whole app exists to protect, at the hour it is scarcest.

**Willpower / decision.** Whatever one's model of willpower as a
fuel, this much is structural and needs no psychology: a decision
made twice is paid twice, and the second payment is made by the
self least able to pay it. A pre-commitment device — which is what a
blocker *is* — works by moving decisions from the moment of
temptation/effort to a moment of distance. The queue is that device
for *what to work on*, exactly as the shield is that device for
*what not to open*.

**Time.** The man who queues at ~19:40 and the man who wakes at
03:30 are, for design purposes, two different agents with different
information and different strength. The night agent has the day's
full context (where he stopped, what is due). The morning agent has
strength of a different kind: freshness, but no context and no
desire to deliberate. A good machine lets each do what only he can:
the night agent *decides*, the morning agent *executes*.

**The machine.** A program cannot intend anything. It can only
*carry* an intention — preserve a decision across time, unchanged,
and present it at the appointed instant. Preservation and timing
are the whole of its competence. The moment it edits, re-ranks,
substitutes, or "helpfully" advances the decision on its own
authority, it has stopped carrying the man's intention and started
substituting its own state.

## 2. Definition, rebuilt

> **An intention, in this app, is a decision transferred across
> time between two selves: made once by the self with distance,
> delivered unaltered to the self at the point of execution.**

Four tests follow necessarily, not by preference:

1. **Decided elsewhere.** Made before the execution moment,
   by the self with context. (Anything generated at the seal
   instant from machine state — a category, a proportion, a
   "Deep Focus Session" — is not an intention; it is a caption.)
2. **Singular.** Exactly one. Two candidates at 05:00 re-open the
   decision the transfer existed to close. This is why the queue's
   *order* is itself part of the decision, made at night by drag
   and FIRST/CUE, never re-derived by the machine at the seal.
3. **Verbatim.** Delivered in the decider's own words, character
   for character (Feel-4's finding). Recognition — *I chose this* —
   is the mechanism by which the morning self accepts the night
   self's authority. A re-cased, prefixed, or normalised title is
   the app announcing him to himself, and it will be negotiated
   with, the way one negotiates with an announcement.
4. **Executable now.** It must name work that can begin at that
   instant. A thing that cannot be acted on at delivery is not an
   intention but a reminder, and reminders at 05:00 are noise.

## 3. F-1 graded against the tests

F-1 (delivered, CI-proven on this branch) passes 1–3 outright and
passes 4 by its two exclusions:

- **DEFERRED excluded** — a deferred task is the night self's
  explicit *not today*. Delivering it would fail test 4 and, worse,
  would be the machine overriding the decider (§1, the machine).
- **SLEEP exception** — sleep names no work (test 4). Student 1
  strengthens this: he queues *before* the 20:00 SLEEP seal, so the
  head is present at that seal every night. The exception is not
  an edge case; it is the first seal F-1 meets, nightly.
- **Empty queue → routine name** — no decision was transferred, so
  none may be impersonated. Amt's overruling of the Designer's
  "No task queued" headline is this principle: the routine name is
  a fact (this block is the FASTED BLOCK); "No task queued"
  archived in the Drawer would be a judgement wearing a title.

Where F-1 stops: it transfers the *words* of the decision and
nothing else. Which brings us to the seam the cell found and
under-priced.

## 4. One field, two concepts — the real machinery question

The session's `intention` string is asked to be both:

- **Origin** — what was decided, sealed at the seal instant; the
  receipt's title source; the Drawer's permanent record. Physics:
  *immutable.* (ESR-02's rationale, Repository's own comment at
  promoteTaskToActive:2667/:2689 — rewriting it under the seal
  "would falsify the slip.")
- **Pointer** — what the running session is on now, after the head
  is finished mid-block and the queue advances. Physics: *mutable*,
  or the block headlined finished work for its remaining hours.

`completeTaskAndAdvance` (MonolithRepository.kt:2525) implements
the pointer — `currentSession.copy(intention = promoted.title)` on
promotion — with no seal-marker check, while `promoteTaskToActive`
implements the origin and refuses the identical write. The cell
called this a disagreement to be ruled at merge (panel: PASS,
conditional on that ruling). **From first principles there is
nothing to rule between them: both behaviours are correct, for
different readers, and a single field cannot serve both.** Any
ruling that blesses one funnel falsifies the other funnel's reader.

The rebuild: **split the concepts, and notice the pointer does not
need to live on the session at all.** What the session is "on now"
is already a fact of the queue — the ACTIVE row. A surface that
wants the current focus can read the queue; a surface that wants
the sealed intention reads the session. Store the origin (frozen);
derive the pointer (live). MIP-06's tiers reach the same wall from
the implementation side — Tier 2 "freeze the intention against
completeTaskAndAdvance's rewrite" plus Tier 1's bond — and Welle's
mechanism spec prices the identical two risks (§4 of his file).
Three seats, three methods, one seam: I record the convergence as
the strongest evidence this issue has produced.

## 5. Identity is not a description — the theme underneath

Every open wound on this issue is the same error in different
clothes: **recovering identity from a description (a string)
instead of carrying identity itself (an id).**

1. **No bond.** The session knows a title, not *which task* it
   carries (MIP-06 §0: `focus_sessions.taskId` exists, unwritten).
   Amt killed the Innovator's taskId write for this pass for a real
   reason — completion linkage settles through that column on the
   hand path, and a silent completion would be a machine deciding.
   The kill was right *for a title-only pass*; it does not make the
   bond wrong. It makes the bond a decision that must be taken
   openly, with its completion semantics ruled, not smuggled.
2. **Dedup deletion.** Seed-sync dedups finished sessions on
   `intention|targetDurationSeconds|completedTasksSummary` and
   deletes collisions (Repository ~:415 on this branch; Welle,
   batch-10 numbering :441). Post-F-1, two real mornings — same
   head title queued twice running (the Student's normal pattern),
   same duration, no completions — are "duplicates," and one
   morning's record is deleted as tidiness. A session *is* its id
   and its instant; it is not the string it wears. Welle's priced
   fix (add routineId|windowStartMillis to the key) is the minimum;
   the principle is that no history row may be deleted for
   *resembling* another.
3. **Provenance by string equality.** Staffel's Round 2 intention
   line (good work, and "WHEN IT SEALS" is a genuinely truthful
   teaching device) warrants `THIS LEAF NAMED THE RUNNING BLOCK`
   by string equality with the session's intention — "zero schema."
   Duplicate titles break the warrant in both directions (a leaf
   claiming a naming it didn't do; a true claim lost on a post-seal
   edit). Either the leaf prints no naming claim, or the bond /
   provenance record (MIP Tier 1/2) is paid for and the claim is
   *true*. A printed claim whose proof is a coincidence of spelling
   is precisely what Truth Car exists to kill — see my #5 file.

## 6. The seal is an interval, not an instant

H1 taught this and its cure states the residue honestly
(RoutineHeartbeat.kt:104-124): five sequential DAO reads are not
one transactional snapshot. First-principles form: **any claim of
the shape "the state at time T" taken over more than one read is
false as stated** — there is no single T; there is a window in
which the queue can change between reads. Two honest postures
exist: (a) shrink the window to one transaction at materialise
(MIP Tier 1's transactional bond), or (b) keep the window and
never print the word "instant." The branch currently does (b) in
its comments, to its credit. What it must not do is drift into
(a)'s language on (b)'s machinery — on the leaf line, on the
receipt, or in a future comment.

## 7. The DEFERRED back door

One charge stands alone because it is not about F-1's code at all.
`completeTaskAndAdvance`'s next-up selection falls back to
DEFERRED when no QUEUED task remains, promotes it, and writes its
title as the live session's intention. F-1's resolver was
deliberately armoured against ever reading DEFERRED as a head
(RoutineHeartbeat's comment argues it at length) — and the older
funnel does the forbidden thing mid-session, unguarded, and then
*headlines* it. Under §2, this is the machine overriding the one
explicit scheduling decision ("not today") the night self made.
MIP-06 §2/§5 aligns the same fallback. It should be closed with
the ESR-02 item, not after it: they are the same funnel.

## 8. What I am asking Ren to rule (numbered, priced by others)

1. **Split origin from pointer** (§4): freeze the sealed intention
   on all paths (mirror the ESR-02 guard into the advance path —
   Welle: ~5 lines + 1 test); surfaces needing current focus read
   the queue's ACTIVE row. No new column required for this step.
2. **Close the DEFERRED fallback** in `completeTaskAndAdvance`
   (§7), or rule it lawful *in words* — silence is the only
   unacceptable verdict, because the code currently contradicts
   the resolver's printed law.
3. **Fix the dedup key** so resemblance never deletes a record
   (§5.2; Welle's price: ~2 lines + seed-fixture test).
4. **Decide the bond openly** (MIP Tier 1/2): write `taskId` at
   materialise + `intentionSource`, with completion semantics
   ruled *before* the write — or strike the bond permanently and
   strike Staffel's string-equality naming claim with it (§5.3).
   The one incoherent position is claims without the bond.
5. **Language law**: "seal instant" only ever over a single
   transactional read (§6).

None of these reopens F-1's delivery. F-1 is a correct Tier 0:
the transferred words arrive. What remains is making them
*carried* — bonded, frozen, and never deleted for resembling
themselves.

— Grund, deep-thinker (issue #12)
