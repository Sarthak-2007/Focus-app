# Issue #134 — Wave-3 Report (composition onto current main)

**Lane:** Audit R3b · phone-call-mid-session
**Original lane tip:** ec408a5 (base 0a00e95) — CI run 41000551637 on its base:
assemble GREEN, 317 tests, 36 failed all shared floor; CallExclusionTest +
CallMigrationTest + CallMidSessionRepositoryTest all GREEN.
**Composition:** true 3-way merge (base 0a00e95 vs ec408a5 vs current main),
re-parented as a single composition commit on `agent/issue-134` whose parent
is current main. See the composition commit message / GESTALT report for the
composed tip SHA and the CI run on the composed branch.

**Composition target (final):** main `cdbeaca` — reached in three waves
(first composition on `1ef3e2b`; second-wave delta to `bebaa46` re-applied
hunk-by-hunk; final wave a true git 3-way merge onto `cdbeaca`, which
carries #67's FocusDomain refactor, #129/#139/#304 gates, #287's DB bump
and the #283/#287 extend-verb clash — see below). The final tree is
cdbeaca's tree plus this lane's 13 files; nothing of main's is reverted.

**DB RENUMBER (composition outcome):** main took the 12→13 slot itself
(issue #287: failureCausePackage / failureCauseKind / reflectionNote).
Per the composition gate, this lane's migration was renumbered to follow
main's head: **MIGRATION_13_14, DB version 14**, schema change unchanged
(callExcludedSeconds). CallMigrationTest and the three chain-head tests
were renumbered with it (chain now 7→…→12→13→14, validating v14).

**Main's compile clash (not this lane's):** current main does not compile
— the #283/#287 double-landing left two conflicting
`extendActiveSession` overloads in MonolithRepository.kt plus an
ambiguous call in MonolithViewModel.kt (3 errors). Both overloads are
preserved verbatim in this composition; the surviving verb is the
#283/#287 owners' call. CI on this branch is therefore expected to fail
at compileDebugKotlin with exactly those 3 errors and no others; test
verdicts are blocked behind that clash. (On the previous base, bebaa46,
where main compiled, this lane's CI ran the full suite: CallExclusionTest
+ CallMigrationTest + all CallMidSessionRepositoryTest green after the
canary fixture fix in befd597; 34 failures, all shared floor.)

**Remote-lane note:** while this composition was in flight, the remote
branch was independently rebased onto `1ef3e2b` (86e6aca→82aae48→8605926,
same three commits, new SHAs) and STURM-4 added 7281bc5, which removed the
dialer-identification test from CallMidSessionRepositoryTest with the note
"Finding 4's dialer exemption is deliberately absent — struck under
Sarthak's NEVER THROUGH verdict; see #134." That test removal and note are
preserved verbatim in this composition. The exemption CODE (which the
rebased lane also retained) is composed as-is; the verdict conflict is
flagged to GESTALT in the composition report — this lane did not silently
drop code, and did not re-add the struck test.

## Per-finding dispositions (all 4 FIXED by the original lane)

1. **Call time counted as focus time — FIXED.** `focus_sessions` gains a
   persisted exclusion ledger, `callExcludedSeconds`. While the call-bypass
   signal (the same signal the doors already trust) is live, each tick's
   seconds join the ledger (`callExcludedAfter`, WallAccrual) and every
   booking in `updateSessionProgress` runs on
   `focusElapsed = verifiedElapsed − callExcludedSeconds`. Task accrual is
   paused the same way (anchor advances, no credit). The disarm doors bank
   the focus-only figure too, so call minutes are never re-certified at the
   ending write.
2. **Session finish could fire mid-call — FIXED.** If the target was already
   met when the call began, the finish is held: frozen progress is booked,
   the row stays in the block, active; the Safe Break flip, the seal's
   release, the completion notification and the end stamp all wait for the
   first tick after hang-up.
3. **Safe Break countdown spent itself during a call — FIXED.** A call that
   straddles the break holds the countdown: remainder untouched, tick anchor
   advanced; the drain resumes from exactly that remainder when the call
   ends. The ledger does not grow inside a break (break seconds are not
   focus seconds).
4. **Phone app could be locked mid-session (could not dial out) — FIXED
   by the original lane; PARTIALLY STRUCK on the live branch.** The
   dialer package (`MonolithRepository.isDialerPackage`, via the
   system default-dialer role) is exempt at every door: the accessibility
   door's 3-tier branch, the shield service's usage-stats blocked set, and
   every Smart Loadout commit (dialer always lands OPEN). Telephony is a
   safety surface; placing a call starts in MODE_NORMAL, before any call
   bypass exists, so the exemption cannot ride on call state.
   **Strike note:** STURM-4 (7281bc5, on this branch) removed this
   finding's identification test under Sarthak's NEVER THROUGH verdict,
   and main has since landed its own dialler floor (#119:
   EssentialApps.floorTier — dialler never LOCKED, by package). The
   composed tree preserves the strike (test absent, note verbatim) and
   keeps the exemption code the rebased lane retained; whether the
   exemption code itself should now be struck in favour of main's floor
   alone is GESTALT's/Sarthak's call, flagged in the composition report.

## DB version outcome

Main was read FIRST, at every wave: v12 at 1ef3e2b and at bebaa46 — then
**v13 at cdbeaca** (issue #287 landed its own MIGRATION_12_13). The gate
therefore fired on the final wave: this lane's migration was
**renumbered 13→14** — version 13 → 14, migration object
MIGRATION_13_14 appended after main's MIGRATION_12_13 in the
addMigrations chain, schema change unchanged —
`ALTER TABLE focus_sessions ADD COLUMN callExcludedSeconds INTEGER NOT NULL DEFAULT 0`.
The three migration-chain tests (SliceEMigrationChainTest,
RoutinesMigrationChainTest, ShortsSniperTruthTest) were composed to the
new chain head v14, and CallMigrationTest was renumbered to cover 13→14
directly (its v13-shaped fixture now opens at 14 through MIGRATION_13_14
alone).

## Composition notes (per file)

- **MonolithDatabase.kt / MonolithModels.kt / WallAccrual.kt /
  MonolithShieldService.kt** — auto-merged cleanly; verified the diff
  against main contains only #134's additions (ledger column, migration,
  `callExcludedAfter`, shield dialer filter).
- **MonolithRepository.kt** — 6 conflict hunks, each resolved as a union of
  both intents:
  - `applySmartLoadout`: main (#107) moved the tier rule into the pure
    `LoadoutRules.tierFor` (preview == commit). #134's dialer exemption now
    wraps `tierFor` at the commit point — `tierFor` stays pure, the
    committed dialer row is OPEN, and the doors exempt it regardless.
  - Safe Break branch: main (#41) replaced `breakRemainingAfter` with the
    pair-returning `breakTickAfter`. #134's call-hold is inserted before
    it, unchanged in shape (remainder untouched, anchor = now).
  - Last-minute knock (#148, main) + held finish (#134): the knock crossing
    is now read on `focusElapsed` (call time is not focus time, so a call
    cannot carry the block into its last minute); `blockFinished` is
    `focusElapsed >= effectiveTarget`; #134's held-finish block follows
    verbatim.
  - `disarmActiveSession`: main's #149 10-second grace law classifies on
    `lawElapsed` exactly as main wrote it; the banked figure is #134's
    focus-only `max(focusElapsed, active.elapsedSeconds)`. With no call
    history the two formulas are identical.
  - `accrueActiveTaskSeconds`: #134's `accrue: Boolean = true` parameter
    (call site passes `accrue = !callActive`) composed with main's
    `seedAccrualFromDisk(nowMono)` first line and main's persist anchor.
- **MonolithAccessibilityService.kt** — main folded the old inline event
  handler into a queued `processEvent` pipeline. Git anchored #134's block
  inside `sweepOtherWindows`; that copy was dropped (it would have
  duplicated tier blocking in the wrong frame) and the dialer exemption
  was inserted at the head of the real branch 5 in `processEvent`
  (plain `return` — `processEvent` is a suspend fun, not a launch lambda).
  Imports are the union of both sides (CancellationException from main,
  MonolithRepository from #134).
- **Tests** — CallExclusionTest, CallMidSessionRepositoryTest,
  CallMigrationTest arrive verbatim from the original lane (assertions
  untouched, including the ec408a5 fixture fix). No assertion was changed
  during composition; the two CallMidSession tests are the canary for the
  progress machine.

## Second-wave delta (1ef3e2b → bebaa46) on the shared files

Main's 61 intervening commits touched five of this lane's files; every
hunk was carried into the composed files, none dropped:

- **MonolithRepository.kt** — #145 curio work (curioForgeRecords,
  drawCurioId replacing the shuffle at both forge sites, FLOW-door forge
  in disarmActiveSession, forgedArtifactName on all three receipt
  assemblies), #119 dialler floor (EssentialApps.floorTier in
  cycleAppBlockTier, setAppBlockTier, and the bulk profile write),
  #162 F4 countAsReach on recordIntercept, #126 F5 annulment rider
  (comment), #30 LIKE-escaping in searchStoredTasks, and the #119
  comment riding above the loadout tier line. Crucially, main's delta
  does NOT touch updateSessionProgress — the progress machine this lane
  modifies is byte-identical between 1ef3e2b and bebaa46, so the call
  exclusion / held finish / held break composition carries over
  unchanged. Note the interaction at the loadout: tierFor itself now
  ends with EssentialApps.floorTier (dialler never LOCKED), and #134's
  OPEN wrap composes outside it at the same commit point.
- **MonolithModels.kt** — tier label wording ("Shut", "2m Peek") and the
  SLEEP subtitle naming Camera/navigation; #134's callExcludedSeconds
  column is in a different region, untouched.
- **MonolithShieldService.kt** — #164 F2 notification line, #94
  InterceptLaunch at the shield door, #274 lock-screen redaction
  (VISIBILITY_PRIVATE + public version). #134's dialer filter sits in
  the blocked-set computation, untouched by all three.
- **MonolithAccessibilityService.kt** — #162 F1/F3 tamper narrowing
  (installer must name Monolith; Settings guard fires only on
  destructive CONFIRM dialogs), #77 arming order (mono before id),
  #94 InterceptLaunch. #134's branch-5 exemption in processEvent is in
  none of those regions.
- **LoadoutRules.kt / EssentialApps.kt / MonolithDao.kt /
  ReceiptState.kt** — changed on main, not #134 files; the final tree
  takes main's versions via the tree base, and the composed Repository
  compiles against them (floorTier, sleepLoadoutTierFor,
  getForgingSessions, forgedArtifactName).

## User-facing changes

- A phone call pauses the focus clock — call minutes never count as
  studied time, in the session, in task time, or in the final booking.
- Session end and Safe Break entry never fire mid-call; they land on the
  first tick after hang-up. A running Safe Break's countdown holds during
  a call instead of draining.
- The Phone app is always openable during a session, whatever its tier or
  loadout says.
