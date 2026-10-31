# ISSUE #12 — WAVE-2 REPORT (Tasks + F-1)
Cluster F lead: **Amt** (flat under Ren). Branch `agent/issue-12`.
Wave-1 commit `d9d7697` (F-1 + F-3 + F-6, DECLARED, uncompiled).
Wave-2 closes DECLARED, uncompiled — nobody compiled; Ren compiles
only on Sarthak's word.

## What Wave-2 did

1. Verified `d9d7697` line-by-line (Seat F1-Verify): F-1 core SOUND —
   single `DesiredSession` construction site, single materialiser,
   resolver pure + DAO order honoured (ACTIVE/QUEUED, blanks skipped,
   DEFERRED never), SLEEP exception keyed to loadout consistently,
   15 new tests all asserting real behaviour (empty queue, all-DEFERRED,
   blanks, SLEEP, snapshot purity, sub-5/pause gates preserved).
2. Fixed the three edge holes the verification convicted (new commit):
   - **H1 — seal-time race.** The head was resolved at derive time;
     the finalise loop's IO sat between resolution and materialise,
     so a queue edit in that window sealed a stale head for the whole
     session while the comment claimed "at seal time". Cure: the
     heartbeat RE-RESOLVES the head at materialise (step 3) and the
     fresh head names the session; SLEEP keeps its own name. The
     comment now tells the truth about both reads and names the
     residue (sequential DAO reads ≠ one transactional snapshot).
   - **H2 — blank-head trust.** `derive()` trusted its caller: a
     blank `headTaskTitle` would have sealed blank and been silently
     rewritten downstream. Cure: the guard lives in `deriveOne`
     (`takeIf { isNotBlank }`), plus a direct-blank test.
   - **H3 — HAND-SPLIT re-entry.** SPLIT retired forever once a task
     had any step — no way to split a task further after the field
     closed. Cure: SPLIT stands whenever the field is closed;
     reopening appends via `addTaskSubStep`.
3. One new test (blank-head-through-derive). Total new-in-wave-1+2:
   16 RoutineDeriveTest cases over F-1, written, unrun.

## Roster — signed sections (30 seats)

### Finishers ×5
- Finisher-1 (surface) — LEAD-COVERED: F-3's horizon kill + queue
  count from `displayQueue` verified on the wave-1 diff; no surface
  regression found.
- Finisher-2 (motion) — LEAD-COVERED: no motion surface in this
  issue's scope; tear respin stays batch-10's, not smuggled in.
- Finisher-3 (light) — LEAD-COVERED: no light law touched.
- Finisher-4 (sound/haptics) — LEAD-COVERED: no sound path touched;
  wave-1 feel filings' ADD/FIRST/CUE tick vocabulary stands as
  record, unimplemented here (not this issue's scope).
- Finisher-5 (typography/print) — LEAD-COVERED: `N TASKS · SEALED`
  count print verified against displayQueue (no fake proportions).

### Feel specialists ×5
- Feel-1 (weight) — wave-1 filing stands (touch contracts ≥48dp on
  the split field); Wave-2 LEAD-COVERED, no contradiction found.
- Feel-2 (light) — LEAD-COVERED.
- Feel-3 (timing) — wave-1 filing stands (160 ms ruled-line growth);
  LEAD-COVERED for Wave-2.
- Feel-4 (touch+sound) — LEAD-COVERED: H3's re-entry restores the
  touch path the feel filings assumed (a split can be re-entered
  by thumb, forever).
- Feel-5 (instant-read) — LEAD-COVERED: queue head naming the 05:00
  FASTED session is the instant-read win — the sealed session reads
  as tonight's first task, not a routine label.

### The 20 named types
- Crazy-ideas — Seat Panel-Feel-Critic (ran): "the queue head
  becomes the session's single verb" — see Next wants.
- Maximum-implementation-potential — Seat F1-Verify (ran): pushed
  F-1 to its widest honest form — head re-resolved at the seal
  itself (H1 cure), not merely at derive.
- Brutal critic — Seat F1-Verify + Panel-Feel-Critic (ran): core
  acquitted, three edge holes convicted with file:line evidence;
  intention is still a LABEL (no taskId link) — named, not hidden.
- Improver — Amt (lead): H1/H2/H3 cures are this seat's work.
- Designer — LEAD-COVERED (wave-1 designer filing stands).
- Consumer — Seat Panel-Feel-Critic (ran): a NEET student queueing
  tomorrow's first task at night now wakes to a 05:00 session that
  carries that task's name — the feature finally reaches him.
- Student — Seat Panel-Feel-Critic (ran): same verdict; the label-
  only limit is the honest ceiling today.
- User — LEAD-COVERED via Consumer/Student seats.
- User-experience — LEAD-COVERED: H3 was a UX dead-end (one-time
  split); cured.
- Purpose-questioning — Seat Panel-Feel-Critic (ran): "does a label
  help?" — yes as orientation, no as guidance; the question is
  recorded, the claim is not inflated.
- Thoughtful — LEAD-COVERED: SLEEP exception reasoning re-read and
  upheld (the head names work, never sleep).
- Deep-thinker — Seat F1-Verify (ran): snapshot semantics chased to
  the transactional floor; residue named in code, not papered over.
- Enthusiastic designer — LEAD-COVERED.
- Wow-level designer — LEAD-COVERED: no wow claimed for a truth fix;
  the wow is that the 05:00 session finally says the right name.
- Designer-29-years — LEAD-COVERED: snapshot-at-seal is the classic
  pattern; H1 brought the code to it.
- Intentionality-focused designer — LEAD-COVERED: every fallback
  (empty queue, DEFERRED-only, blank) is a named decision in code.
- Synchronous collaboration — Amt (lead): verification, panel and
  lead ran concurrently; findings merged same-wave.
- Implementation-quality — Seat F1-Verify (ran): signatures carry
  no defaults in production paths — a missed caller cannot compile
  silently (by eye; uncompiled by law).
- Technical execution specialist — Amt (lead): heartbeat step-3
  re-resolve is one extra DAO read per materialise pass only.
- Interaction-detail specialist — LEAD-COVERED: SPLIT re-entry
  (H3) is this seat's catch, via F1-Verify.

## Remainders
- F-1 intention is a label: no taskId is carried, completing the
  head mid-session does not advance the session's intention
  (completeTaskAndAdvance rewrites intention on promotion — pre-
  existing behaviour, consistent with queue-head semantics, NOT
  changed here; flagged for Ren at the batch-10 merge).
- Tests written, unrun (DECLARED). Ren compiles on Sarthak's word.

## DECISION-FOR-SARTHAK
- None in this issue.

## Exact issue comment (posted via gh — see git log of this file's
## commit; if posting failed it stands here as COMMENT-PENDING)
---
Wave-2 (Amt, cluster F) — F-1 verified, three edge holes fixed,
DECLARED (uncompiled).

Seat F1-Verify re-read d9d7697 line by line: the core is sound —
one DesiredSession construction site, one materialiser, pure
resolver (ACTIVE/QUEUED only, blanks skipped, DEFERRED never),
SLEEP keeps its own name, 15 tests asserting real behaviour.

Wave-2 commit fixes what the verification convicted:
H1 — the head was resolved at derive time, not seal time; the
heartbeat now re-resolves at materialise, so the queue as it
stands at the seal names the session.
H2 — derive() now guards a blank head itself.
H3 — HAND-SPLIT re-enters: SPLIT no longer retires after the
first split; a task can be split further, appending steps.
Plus one new test (blank head through derive).

Remainder, named: the intention is still a label (no taskId
link). Full record: studio/plan/ISSUE-12-WAVE2-REPORT.md on
agent/issue-12.

— Amt, cluster F lead (Wave-2)
---

## Next wants
- (a) What I want: at the batch-10 merge, a ruling on linking the
  sealed intention to the head's taskId, so finishing the session
  can offer complete-and-advance on the receipt.
- (b) Unassigned, I could: audit every other "snapshot at X" claim
  in the routine chain for the same derive-time/seal-time gap H1
  exposed here.

— Amt

## Addendum — Panel seat (Panel-Feel-Critic) verdict
Panel verdict on #12: **PASS, conditional** — the condition is
Ren's, not rework: the `completeTaskAndAdvance` / ESR-02
disagreement (a live session's intention can be rewritten with
no seal check, Repository ~:2525 in the wave-1 numbering) must
be ruled AT MERGE, before F-1's wider intentions ship — F-1
increases that seam's blast radius. Already flagged in this
report's Remainders; restated here as the panel's formal
condition. Panel crazy idea, recorded: "The First Leaf" — the
head printed once on the Session Room as a perforated leaf at
the seal, and a routine finish receipt carrying one verb
(`HEAD: <title> — DONE / NOT DONE`, default NOT DONE) feeding
complete-and-advance through the ESR-02 seal check. Priced
MEDIUM by the panel; it is the natural sequel to the taskId
question in Next wants.

## Addendum — CI VERDICT (builds-free law, Oct 30 04:17+)
- Merged main's CI unblock (keystore cfe6ee2 + issue-3 workflow
  8d2b077) into agent/issue-12 (186318b); adopted issue-2 cell's
  stale test call-site fixes verbatim, 5 files (bbadf5b).
- Run 40969997299: Assemble debug GREEN (APK artifact uploaded);
  unit tests COMPILE and EXECUTE: 157 tests, 33 failed —
  31 Robolectric classMethod environment failures
  (UnsupportedOperationException, DefaultSdkProvider:170 —
  studio-wide, identical on agent/issue-2) + 2 pre-existing
  shared failures (ChromeWaveUnitTest warrantDerivation,
  TruthCar3SweepTest una06 — both fail identically on
  agent/issue-2, whose whole brief was test health).
- ZERO RoutineDeriveTest failures: every F-1 test, including
  Wave-2's H1/H2/H3-era tests, PASSES on CI.
- Named blockers handed to Ren: the Robolectric runner
  environment (Methods/#3 territory) and the 2 shared
  pre-existing failures — neither is cluster-F code.
