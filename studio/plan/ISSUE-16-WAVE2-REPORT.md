# ISSUE #16 — WAVE-2 REPORT: Pill lane — finish verification, then merge

**Lead:** Pforte, cluster G, flat under Ren. **Branch:** `agent/issue-16`
(cell repo: `builds/cells/issue-16/repo`, commits 352e5db + 6c86ea5,
clean at Wave-2 open). **Pill lane:** `builds/pill/Focus-app` (dirty
lane working tree on main, by design — the lane itself).
**Everything DECLARED, uncompiled. Nobody compiled; nobody ran gradle
in this wave. Verification below is log evidence + code evidence,
marked honestly as such.**

## THE OWED VERDICT — 18 TESTS

**NO_VERDICT — 18 written, 0 reported, 0 failed.** (Wave-1 Finisher-5's
line, re-verified by Wave-2 from the logs themselves.)

- `builds/pill/test.log`: `:app:compileDebugKotlin` and
  `:app:compileDebugUnitTestKotlin` completed (test sources compiled),
  `:app:testDebugUnitTest` **started**, then "Gradle build daemon
  disappeared unexpectedly" (daemon pid 53253) — FAILURE is
  environmental (daemon death, the RAM/generation pattern), **not a
  test failure and not a pass**. No JUnit XML was harvested.
- `builds/pill/assemble.log`: **BUILD SUCCESSFUL in 17m 43s** — for
  `:app:assembleDebug` only. It proves the pill lane's debug APK
  assembled pre-write-wall. It says nothing about any test.
- `builds/pill/test-run.log`: 0 bytes. No evidence.
- Any "18/18 PASS" claim anywhere would be fabrication. The run is
  owed to Ren's healthy-window call via `test-v2.sh` (Wave-1
  Finisher-2), on Sarthak's word. Merge-ready on verification: **NO**.

## FIXES APPLIED BY LEAD (this wave, DECLARED)

All in the **pill lane working tree** (`builds/pill/Focus-app/.../
PillDerivationTest.kt`, untracked lane files). They ride the lane into
the batch-10 composition. They were deliberately NOT committed onto
`agent/issue-16`: that tree contains no pill source, so pill tests
committed there would be orphaned and uncompilable.

1. **F1 (circular oracle, test #7) settled.** Expected values are now
   hand-computed literals — 600, 599, 541, 540, 1, 0, 0 for offsets
   0/1s/59s/60s/599s/600s/900s — replacing the call to the very
   function under test (`breakRemainingAfter`); the now-unused import
   was removed.
2. **F2 (formula-mirroring oracle, test #15) settled.** Literal
   expected seconds per sample — 120, 120, 119, 75, 30, 1 — replacing
   the mirrored ceiling expression. The statelessness claim stands.
3. **F4 (partial telecom coverage, test #4) settled.** The test now
   pins the exact 5-package set AND iterates the production set
   (`PillDerivation.TELECOM_FOREGROUND_PACKAGES`), so list and test
   cannot drift in either direction.
4. **F5 (type-only assertion, test #12) settled.** The end-instant
   assertion now pins the full reverted Session value
   (`Session("Deep Study", false, 1500, 0, 1, 1)`), lead-verified
   against `PillDerivation.derive`/`elapsedAt` (unstamped tick credits
   nothing; effective target 1500).
5. **test-v2.sh fail-fast (both copies, kept identical, `bash -n`
   clean).** The stop line's silent `gradle || ./gradlew` fallback
   masked a missing gradle until the main run failed late; the script
   now exits FAILED 9 with a clear log line if gradle is absent.
   (The seat's second claimed defect — verdict extraction — is a
   MISREAD, lead-verified: line 188 is `T=${PIPESTATUS[0]}`, correct.)

## MERGE FLAGS — VERIFIED IN CODE

- **Flag A (hand-merge at `start2MinutePeek`): CONFIRMED composed** in
  `agent/issue-16` (MonolithViewModel.kt:2385-2445): Suki's pre-grant
  guard (`peekOfferFor` re-derivation over current row/cooldown/pill,
  blank package no longer defaults to WhatsApp) runs first and
  unchanged; the builder's grant gate (`startPeekLease` Boolean,
  refused grant clears the seeded pill, no launch, no record) second;
  Pastille's ST-08 post-grant launch third (grant → launch → record).
  In the pill lane alone, ST-08's launch is UNGUARDED (no
  `peekOfferFor` call) — composition must take the #16 version whole.
- **Flag B (Frist seam): THE SEAM DOES NOT EXIST IN THE ISSUE-16
  TREE.** No `EditableTimes.kt`, no provider, zero consumers; the tree
  reads the 120 s / 600 s constants directly. The swap target is the
  **pill lane's** `EditableTimesProvider` shape
  (`builds/pill/.../data/EditableTimes.kt:36-48`). **Contract change
  priced in:** the pill lane's `startPeekLease` returns Unit with
  `if (durationMillis <= 0L) return` (MonolithRepository.kt:1182);
  at composition it must gain the Boolean return the #16 grant gate
  depends on, or the gate silently breaks. Frist's provider swaps
  into the pill seam unchanged — against the pill provider's shape,
  not against anything in this branch.
- **ST-08 is guarded by ZERO of the 18 tests** (re-confirmed). It
  stands on inspection + Flag A review only. Negative-path test specs
  (refused grant → no launch/no record; null launch intent → lease
  stands, no crash) are filed in the seat sections as remainders for
  the merged tree, where a ViewModel test harness exists.

## LEAD-REJECTED CHARGE

One seat cluster reported an accessibility-service peek grant
bypassing the gate (citing `MonolithAccessibilityService.kt:617-625`
in "the issue-16 repo"). **Lead verification: that code exists in
NEITHER the issue-16 tree (its A11Y service is 445 lines, no peek
grant) NOR the pill lane.** Charge not carried — evidence standard:
a charge files with proof in the tree in scope, or not at all. If it
exists in another lane, it belongs to that lane's cell.

## REMAINDERS

- **F6a/F6b/F6c** (Robolectric group): specs filed (in-memory DB or
  fresh-sandbox protocol; tighten #16's duration window to
  115_000..120_000 with rationale; drive `ShadowSystemClock` to a
  distinct now in #18). NOT applied this wave — they rewrite the
  Robolectric harness, and uncompiled harness rewrites are exactly
  what the first healthy run should not debug blind. Apply at the
  merge, before Ren's run.
- **F3** (#14 two-live-leases): keep as defensive-arbitration test,
  never cite it as proving the one-live-lease invariant; concurrency
  (C26, non-atomic grant) remains unguarded by any test.
- **Route B / PillController**: zero tests, zero logs; on-device only.
  Nothing in this wave changes that.
- **Feel-5's instant-read FAIL** (Wave-1) stands unaddressed by code
  in this branch — it is pill-face design work for the merged tree,
  not verification work.

## DECISION-FOR-SARTHAK

- None inside #16. (Whether/when Ren fires the owed test run is
  Ren's call under the build law, on Sarthak's explicit word to build.)

## SEAT SECTIONS (30 — 5 finishers, 5 feel, 20 named types)

Condensed by the lead to verdict + load-bearing evidence. No seat
compiled or ran anything; log/code inspection only.

### Finishers
- **Finisher-surface — NO_VERDICT.** PillFace/PillController/PillTheme
  exist in the pill lane; surface cannot be judged from a dead run.
- **Finisher-motion — NO_VERDICT.** Countdown cadence is derivation-
  pure (recomputed per instant, F2's statelessness half) — motion
  proof waits on the run.
- **Finisher-light — NO_VERDICT.** Theme state light untested by the
  18 (derivation + lease only).
- **Finisher-sound/haptics — NO_VERDICT.** Route B notification is
  the pill's voice; zero evidence in logs.
- **Finisher-typography/print — NO_VERDICT.** Nothing in the 18
  renders text; print judgement is device work.

### Feel specialists
- **Feel-weight — LEAD-COVERED from Wave-1 feel files (feel-1):**
  the pill's weight is its lease end written in storage, not a timer
  in the view — the face is recomputed, never counted.
- **Feel-light — LEAD-COVERED (feel-2).**
- **Feel-timing — LEAD-COVERED (feel-3):** ceiling law — the fuse
  reads 1 in its last partial second and dies at the end instant,
  never a beat early (pinned literally by test #12).
- **Feel-touch+sound — LEAD-COVERED (feel-4):** hand-back is the
  pill ✕'s verb and Route B END PEEK's verb — one verb, two hands
  (test #18).
- **Feel-instant-read — LEAD-COVERED (feel-5):** Wave-1 graded the
  current pill FAIL on the 0.5 s glance; carried as remainder above.

### The 20 named types
- **Crazy-ideas — proposals:** shard the 18 (derivation 15 pure-JUnit
  first, Robolectric 3 second) so a daemon death still leaves a
  partial verdict — already embodied in test-v2.sh's watcher design.
- **Maximum-implementation-potential — FIX-FIRST:** the Boolean
  contract change on the pill `startPeekLease` is the single
  highest-leverage line in the merge (Flag B, above).
- **Brutal critic — FIX-FIRST.** "18 tests" has been quoted as if it
  were a verdict; it is an inventory. The daemon died; nobody knows.
  Also: ST-08, the merge's headline behaviour, is the least-guarded.
- **Improver — FIX-FIRST.** F1/F2/F4/F5 settling fixes (applied by
  lead); F6 specs filed.
- **Designer — PASS (design of verification).** Inventory-before-run
  (Finisher-3) is the right order: verdicts can now be read against
  pre-registered claims and flags.
- **Consumer — NO_VERDICT.** He will meet the pill as a countdown
  over a shut app; nothing here proves that meeting.
- **Student — PASS.** The 18 read as a table (§2.1's cases) — a new
  reader can audit coverage in one pass.
- **User — NO_VERDICT.** Break outranks peek, telecom outranks the
  fuse, keyguard outranks all — the absence gates (#1-#5) are the
  user-facing conscience of the lane.
- **User-experience — FIX-FIRST.** A refused grant must never leave
  a pill seeded over nothing — the grant gate cures it in #16's
  composition; the pill lane alone still seeds before granting.
- **Purpose-questioning — PASS.** The pill exists so a peek is a
  fuse, not a door; every absence gate serves that.
- **Thoughtful — FIX-FIRST.** F3's unreachable-state test is honest
  about itself in its own comment — keep that honesty, cite it
  correctly.
- **Deep-thinker — FIX-FIRST.** Circular and mirroring oracles (F1,
  F2) are the same disease as S-1: a check that copies the checked.
  Both settled this wave.
- **Enthusiastic designer — PASS.** Ceiling-not-floor is a small,
  perfect decision: the last second reads 1.
- **Wow-level designer — NO_VERDICT.** Wow is a device property
  (overlay, Route B) — unprovable from this tree.
- **Designer-29-years — FIX-FIRST.** In 29 years: suites die at the
  harness more than at the code; test-v2.sh's pre-written RUNNING
  verdict is the mark of someone who has buried runs before.
- **Intentionality-focused designer — PASS.** Mid-flight law is
  structural: the provider is read at grant/creation only; a live
  lease can never be re-priced.
- **Synchronous collaboration — FIX-FIRST.** Flag A is a three-author
  composition (Suki/Pastille/builder) that only works whole; the
  report's job is to stop anyone taking a third of it.
- **Implementation-quality — FIX-FIRST.** Grant gate ordering verified
  line by line (guard → gate → launch → record, kt:2385-2445).
- **Technical execution specialist — FIX-FIRST.** test-v2.sh audit:
  fail-fast defect fixed by lead; PIPESTATUS extraction verified
  correct; watcher + verdict pre-write are sound.
- **Interaction-detail specialist — NO_VERDICT.** The pill ✕
  (hand-back) is the one touch verb; its feel is device-only.

## GITHUB COMMENT (fallback text — see lead note on route)

Route note: per Sarthak's Oct 30 04:06 correction, GitHub work goes
via gh CLI/token, never the connector; gh is not logged in on this
box and no token is stored, so the comment below is filed here
verbatim for Ren to post via gh, and the push was attempted via git.

> **Wave-2 — issue #16 (Pforte, cluster G)**
> Verdict on the owed 18 tests, from log evidence only:
> **NO_VERDICT** — 18 written, 0 reported, 0 failed. test.log shows
> the suite started and the Gradle daemon died (pid 53253);
> assemble.log's BUILD SUCCESSFUL is assembleDebug only.
> Wave-2 fixes (DECLARED, in the pill lane working tree, riding the
> batch-10 composition): F1/F2 oracles settled with literals, F4
> telecom set pinned + iterated, F5 Session values pinned;
> test-v2.sh fail-fast cured. Merge flags verified: Flag A composed
> whole at MonolithViewModel.kt:2385; Flag B's seam is the pill
> lane's provider — the pill `startPeekLease` must gain its Boolean
> return at composition. ST-08 remains guarded by zero tests.
> Full report: studio/plan/ISSUE-16-WAVE2-REPORT.md on this branch.
> — Pforte

## NEXT WANTS (lead, unassigned)

- Ren's healthy-window run of test-v2.sh — the only thing that can
  turn NO_VERDICT into a verdict.
- At batch-10 composition: Boolean contract on pill startPeekLease;
  ST-08 negative tests in the merged tree; F6 harness fixes before
  the run; Route B/PillController device checks.

— Pforte, cluster-G lead, Wave-2, issue #16. DECLARED, uncompiled.

## ADDENDUM (late seat returns, folded in by lead before close)

- **Route B does not consume the derivation (material truth defect).**
  `PillFace.kt:8-11` claims "ONE derivation with two mouths… the glass
  and the shade can never tell two times." The shipped Route B
  (`MonolithShieldService.startMonitoringLoop`) re-implements the
  priority inline on its own 2-second loop: break/session figures come
  from persisted fields, not `breakRemainingAfter`/`elapsedAt`, and it
  applies NONE of the absence gates (keyguard, own-package, telecom).
  Only the peek ceiling formula is duplicated identically. The two
  mouths can disagree by the progress tick's lag. At composition:
  Route B must call `PillDerivation.derive`, or the claim in
  PillFace.kt must be struck. — Feel-timing seat, lead-accepted.
- **C13 ghost clock survives the composition (batch-10 must name it).**
  Composed VM:2401-2406 still seeds `FloatingPeekPillState` from the
  hard constant `PEEK_DURATION_SECONDS` (120s) — the ghost clock
  PillFace's header declares dead. With peekMinutes > 2, every peek
  silently truncates to 2 minutes. Fix priced by Improver seat: seed
  from the granted lease end the repository just wrote.
- **The grant gate's false arm is currently dead in this tree.** The
  #16 repository's `startPeekLease` always returns true (no provider
  read, no refusal path); the gate fires only when the pill lane's
  repository lands wholesale WITH the Boolean patch (Flag B above).
  Ship the VM without that companion and the gate is wired to nothing.
- **Refusal experience is undesigned.** A peekMinutes=0 refusal hides
  the airlock, silently un-seeds the pill, and explains nothing. A
  designed refusal surface is owed before peekMinutes=0 is reachable.
- **Pill feel facts (device-unproven, code-read):** the pill itself is
  completely silent (no sound/haptics on any pill verb; sound lives at
  the airlock verbs only); snap animation is a single 140ms
  ValueAnimator; the ✕ target is ~28×27dp (under the 48dp guideline)
  inside a draggable parent; fill is ~90% opaque despite the file
  comment calling the alpha "slight". All recorded for the merged
  tree's feel pass.
- **Paperwork staleness:** Wave-1 cell docs call the grant gate
  "uncommitted" — lead-verified it IS committed in `6c86ea5`.

## PUSH STATUS (lead, Oct 30 Wave-2 close)
Push attempted. Remote `agent/issue-16` exists at `873a23e` — a
parallel Wave-1 commit (verification plan + merge flags only) with
a different history — so the local cell history (352e5db, 6c86ea5,
cdc5054, 0fd5645) diverges and a plain push is rejected; no
force-push was made. Reconciliation belongs to Ren/Konto at the
batch-10 composition. The Wave-2 commits are safe locally here.

## FINAL STATUS (gh route live, Oct 30 04:10 update — supersedes PUSH STATUS above)
- **DIVERGED — left local, Ren reconciles at batch-10.** Remote
  `agent/issue-16` @ `873a23e` "issue-16: pill verification plan +
  merge flags verified (Pastille cell)" carries a docs-only subset
  (verification-plan + merge-flags, based directly on 8438f5a).
  Local `agent/issue-16` carries different, fuller work:
  `352e5db` "pill verification prep — composed start2MinutePeek",
  `6c86ea5` "full cell package", `cdc5054` + `0fd5645` + status
  (Wave-2). Trees differ by 17 files. No rebase, no force-push.
  This branch — the issue-16 CELL CLONE's branch
  (`builds/cells/issue-16/repo`) — is the one carrying the pill
  verification package; the pill lane itself lives uncommitted in
  `builds/pill`.
- **COMMENT POSTED** via gh: issue #16, comment 6510187966.
