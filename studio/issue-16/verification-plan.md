# Issue #16 — Pill lane verification plan

*Pastille (cell lead), Oct 30 2026. Status: PREPARED. The suite has
not been re-run; execution waits for Ren's healthy-window call
(compile/test authority is Ren's alone). Nothing in this document
claims a verdict that does not exist yet.*

## What is being verdicted

The pill lane (`~/workspace/clone-inspect/builds/pill/`) shipped
PillFace / PillController / PillTheme, the Route B notification,
ST-08 (peek takes him to the peeked app), and the EditableTimes
seam. Its `assembleDebug` went green before the write-wall night.
Its tests have **never had a verdict**: the suite died three times
to the environment (daemon births failing box-wide), never to a
test failure.

The 18 tests:

| File | Tests | Covers |
|---|---|---|
| `app/src/test/java/com/example/PillDerivationTest.kt` | 15 | pill state derivation |
| `app/src/test/java/com/example/PillLeaseRepositoryTest.kt` | 3 | lease + cooldown repository behaviour |

## Blocker found during prep (fixed before the window, not during)

`builds/pill/test.sh` as written **cannot run**: its first step
acquires the old compile queue at
`~/workspace/clone-inspect/design-refs/swarm/tools/queue.sh`, and
the swarm tree was retired and deleted on Oct 30 (collaboration
moved to GitHub). The script would exit 3 (`ACQUIRE FAILED`)
before Gradle is ever reached.

Prep applied: a corrected one-command script (cell artifact
`test-v2.sh`, Finisher-2) — same shape, queue dependency removed,
the lane's JDK discipline kept (stop the lane's own daemon, then
`:app:testDebugUnitTest` under JDK 21 via `env21.sh`,
`GRADLE_USER_HOME` = the lane's own `guh`, log to the lane's
`test.log`, final line `PILL_TEST_DONE test=<code>`).

## The one clean shot (Ren, healthy window)

Pre-flight, in order:

1. Fresh runtime generation / writes verified healthy (the
   cure-hunters' law: fire early in a generation, keep a born
   daemon warm, never `--stop` a window-born daemon).
2. RAM headroom: ≥ ~2 GB free, no stale Gradle daemons resident.
3. Run from the lane: `bash test-v2.sh` (path per the cell's final
   placement note) — one command, no mid-run intervention.

## What the verdict will look like

Posted verbatim to issue #16 by Pastille:

- `TEST_EXIT` and the `PILL_TEST_DONE` line.
- Test counts from `app/build/test-results/testDebugUnitTest/`:
  total / passed / failed / skipped, failures named individually.
- Any failure split honestly: real test failure vs environment
  death (log tail quoted either way). A second environment death
  is reported as such — never dressed as a test result.

## Acceptance (from the issue)

- [ ] Test verdict posted on #16 — **waiting on the window**
- [x] Merge flags verified and carried to the batch-10 epic —
  see `merge-flags.md` in this folder; both flags verified in
  code by Pastille, composition pre-applied on this branch
  (`agent/issue-16`, DECLARED, uncompiled)

— Pastille
