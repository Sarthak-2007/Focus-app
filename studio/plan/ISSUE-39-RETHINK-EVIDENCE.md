# Issue #39 — week-routine-derive: RETHINK EVIDENCE (Audit R1)

**Status: RETHINK-PENDING.** Per Sarthak's order (Oct 30, 05:07 IST —
"Routines as a whole should be rethought"), no routine feature work
or routine-model fixes were made. This document dispositions the
five audit findings against the CURRENT code as evidence for the
rethink: what is broken, what is over-complex, what is unused.
Branch `agent/issue-39` carries this document only. The issue stays
OPEN. Nothing here merges routine changes to main.

All line references verified against origin/main (63537aa) by the
lead's own read and by the 20-seat bundle report
(ISSUE-39-SEATS.md), whose verdicts concur on every finding.

## The five findings — all HOLD

### 1. [CRITICAL] Week/window identity floats with the mutable system timezone
- `MonolithWeek.kt:28` — the week anchor is recomputed from the
  system zone at evaluation time: travel across a Monday boundary
  re-anchors the week, silently resetting the 1-break-per-week
  ledger — a second weekly break becomes lawful.
- `RoutineDerive.kt:142,153` — claim identity is exact epoch
  equality (`windowStartMillis ==`). Zone travel re-derives
  window starts in the new zone; the stored claim never matches,
  so one window mints a second session or judges itself MISSED.
- The header's "PURITY, ABSOLUTE" (which even scopes itself with
  "except at the memo's 2-second edge") is false: `derive()` takes
  only `nowMillis`; zone and DST rules are ambient, mutable,
  unlogged inputs. Claim identity is effectively an ELEVENTH
  judgement input the header's closed list denies.
- TRUE CURE IS SCHEMA: mint-time zone / local-window identity
  stored on the session and routine rows. Threading an explicit
  ZoneId through derive removes the ambient read and makes tests
  honest, but cannot repair identity for windows already minted —
  only stored identity survives travel.

### 2. [HIGH] Pause amnesty is retroactive over the whole window (J6)
- `RoutineDerive.kt:204` — `if (pausedUntilMillis >= closed.endMillis)
  return null` inside judgeLatestClosed. There is NO pause-SET
  timestamp anywhere on RoutineEntity (fields: `pausedUntilMillis`,
  `scheduleEditedAtMillis` — no provenance for either), so a pause
  set minutes before the window's end erases the whole window's
  judgement, however long it ran unpaused.
- Cure needs a `pausedSetAtMillis` column (schema) so amnesty can
  be bounded to the pause's own span.

### 3. [HIGH] The migration default defeats the edit-horizon guard
- `MonolithDatabase.kt:59` — migration adds
  `scheduleEditedAtMillis INTEGER NOT NULL DEFAULT 0`.
- `RoutineDerive.kt:~226` — `if (closed.startMillis >
  routine.scheduleEditedAtMillis) return MISSED`: every legacy
  window start is > 0, so the UNKNOWN branch ("the stamp cannot
  speak for it") is dead for every migrated routine and MISSED —
  a permanent-record judgement — is invented for windows the
  engine never witnessed.
- The seat bundle found the same comparison INVERTED between the
  two readers (deriveOne treats `==` as unknowable, judgeLatestClosed
  treats only `<` that way) — two readers of one stamp disagree
  about which side of it is knowable.
- A derive-local cure exists (treat 0 as "no edit recorded" →
  UNKNOWN) and both seat orders (the bundle's Brutal Critic and
  the Critic (systems)) demand it land even under the freeze, as
  a false-record stop-loss rather than a feature fix. It is
  recorded here as the rethink's first decision item, NOT
  implemented: the freeze order covers routine-model fixes, and
  this changes shipped judgement semantics.

### 4. [MEDIUM] Claim selection is order-dependent
- `RoutineDerive.kt:146,153` (+ the claim lookup in
  judgeLatestClosed) — `firstOrNull` over an unordered list: a
  stamped duplicate listed first masks an unstamped claim and
  flips CLOSED_DUE → OUTSIDE, contradicting the file's own
  definition (line 45: CLOSED_DUE = an unfinalised routine
  session exists).
- Deterministic cure specced: prefer the unstamped claim, else
  the lowest id. (Fix held by the freeze.)

### 5. [MEDIUM] HELD-vs-MISSED rests on an elapsed-time estimate that undercounts pauses
- `RoutineDerive.kt:243-255` — `overlapsAny` reconstructs span
  ends as `startWall + elapsedSeconds × 1000` for inactive rows.
  Elapsed excludes paused time, so every paused session's span
  is undercounted and can fail to overlap a window it truly
  covered → false MISSED, the record's worst direction.
- The truth already exists and is ignored: `FocusSessionEntity
  .endWallMillis` (MonolithModels.kt:364, Truth Car 2 RC-01) is
  stamped once at the ending write. overlapsAny never reads it.

## What is over-complex (rethink evidence, not findings)
- MonolithWeek's anchor is recomputed per evaluation and never
  persisted or logged — no surface can answer "which week is it?"
  durably; two calls across a zone change disagree inside one
  process.
- Five of the six relief valves are caller-supplied or ambient
  (zone, DST, memo) while the header claims a closed input list.
- The snap
...[truncated 3016 chars]