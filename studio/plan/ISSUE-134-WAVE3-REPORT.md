# Issue #134 — Wave-3 Report (composition onto current main)

**Lane:** Audit R3b · phone-call-mid-session
**Original lane tip:** ec408a5 (base 0a00e95) — CI run 41000551637 on its base:
assemble GREEN, 317 tests, 36 failed all shared floor; CallExclusionTest +
CallMigrationTest + CallMidSessionRepositoryTest all GREEN.
**Composition:** true 3-way merge (base 0a00e95 vs ec408a5 vs current main),
re-parented as a single composition commit on `agent/issue-134` whose parent
is current main. See the composition commit message / GESTALT report for the
composed tip SHA and the CI run on the composed branch.

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
4. **Phone app could be locked mid-session (could not dial out) — FIXED.**
   The dialer package (`MonolithRepository.isDialerPackage`, via the
   system default-dialer role) is exempt at every door: the accessibility
   door's 3-tier branch, the shield service's usage-stats blocked set, and
   every Smart Loadout commit (dialer always lands OPEN). Telephony is a
   safety surface; placing a call starts in MODE_NORMAL, before any call
   bypass exists, so the exemption cannot ride on call state.

## DB version outcome

Main was read FIRST: at composition time main's `MonolithDatabase` was
still **version 12**. Therefore `MIGRATION_12_13` landed **as-is, no
renumber**: version 12 → 13, one schema change —
`ALTER TABLE focus_sessions ADD COLUMN callExcludedSeconds INTEGER NOT NULL DEFAULT 0` —
appended to the `addMigrations` chain. The three migration-chain tests
(SliceEMigrationChainTest, RoutinesMigrationChainTest, ShortsSniperTruthTest)
were composed to the new chain head v13, and CallMigrationTest (new) covers
12→13 directly.

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

## User-facing changes

- A phone call pauses the focus clock — call minutes never count as
  studied time, in the session, in task time, or in the final booking.
- Session end and Safe Break entry never fire mid-call; they land on the
  first tick after hang-up. A running Safe Break's countdown holds during
  a call instead of draining.
- The Phone app is always openable during a session, whatever its tier or
  loadout says.
