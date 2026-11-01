# BUILDER REPORT — MONTEUR, issue #7 "Main-app pass — Shield (audit fixes)"

Cell: VOGT (lead) · MONTEUR (BUILDER, only editor of `app/src/` in this cell).
Base: branch `agent/issue-7` from main `8438f5a`, clone
`~/workspace/clone-inspect/builds/cells/issue-7/`.
State: **DECLARED — nobody compiled, built, or ran tests.** No commits,
no pushes, no branch switches; VOGT coordinates GitHub posting and commits.

## Files changed

| File | Change |
|---|---|
| `app/src/main/java/com/example/ui/ShieldBlockingStatus.kt` | **NEW.** Pure derivations: `computeShieldBlockingStatus` (+ `ShieldBlockingProof`/`ShieldBlockingStatus`), `routineCapWord`, `routineRunningNowSuffix`. No Android imports — unit-testable as-is. |
| `app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt` | Status sentence as item 0 (`testTag shield_blocking_status`); Standing Orders slip lines + all flip/rest machinery retired; catalogue grouped by ruling; ROUTINES header `ACTIVE` → `ARMED`; routine cap `ON` → `WON'T FIRE` for `daysMask=0`; RUNNING NOW marker on routine rows; dead `SledSwitchView` import struck (R-9's Shield half). Net ≈ −300 lines in this file. |
| `app/src/main/java/com/example/ui/MonolithViewModel.kt` | New `shieldBlockingStatus` StateFlow + `refreshShieldBlockingStatus()`, refreshed on the per-second ticker, on every Watch Store emission, and on every session emission. |
| `app/src/main/java/com/example/MainActivity.kt` | Collects `shieldBlockingStatus`, passes it as Shield's new `blockingStatus` param. |
| `app/src/main/java/com/example/data/AppRules.kt` | New `groupedForCatalogue()` beside `groupedByRule()`: PEEK → SHUT → OPEN, alphabetical inside groups, empty rulings contribute no group. |
| `app/src/main/java/com/example/data/MonolithModels.kt`, `RoutineDerive.kt` | Comment-only: the `liveCount` docs now name the printed word `ARMED`, not `ACTIVE`. |
| `app/src/test/java/com/example/ShieldBlockingStatusTest.kt` | **NEW tests, written not run** (see below). |

## The four in-scope fixes

**1 · Headline status sentence (audit #1 / R-5).** First line of the
screen, above the tally and every ruling it qualifies:
`BLOCKING NOW — SLEEP SESSION · ENDS 03:30` /
`NOT BLOCKING — NO SESSION RUNNING`, plus the two honest non-live forms
below. Tone map per the feel review (Haut): LIVE = full ink,
NO SESSION = quiet `onSurfaceVariant` (down as scheduled is not an
error), UNPROVEN/STALE = `scheme.error`.

**2 · Standing Orders de-duplicated (R-1).** The slip's per-app lines,
their tap-flip path, and the RestingFlip choreography (~200 lines) are
deleted. What remains of the slip is its tally — counts only, one home
per number. The catalogue is now the screen's ONE enumeration, grouped
by ruling via `groupedForCatalogue()` with word-only group headers
(`catalogue_group_PEEK_2M` etc.; counts deliberately NOT repeated in
the headers — R-13 / Salz C-11.1). Sled rows inside groups are
unchanged, so a ruling is read and revoked in the same place. Groups
derive from `filteredApps` (the shown list, framed by the existing
`SHOWING n` header); the tally still counts `allApps`.

**3 · ACTIVE → ARMED (R-6).** ROUTINES header now prints `n ARMED`
from the unchanged, acquitted `RoutineSchedule.liveCount`. No other
Shield `ACTIVE` print existed; session/task `ACTIVE` words elsewhere
are different facts and were left alone.

**4 · daysMask=0 → WON'T FIRE (R-6).** The routine cap word is now
`routineCapWord(enabled, daysMask)`: `OFF` / `WON'T FIRE` / `ON`.
`WON'T FIRE` replaces `ON` on the cap itself (Salz C-10: the cap is
the surface that lied), wears the middle density (surfaceVariant fill
+ outline, per designer §3.3), and the cap widens past 48dp instead of
clipping the word. `NO DAYS SET` stays in the meta line as the cause.
Bonus from R-5: routine rows carry `RUNNING NOW` — see decisions.

## Design / data-source decisions

- **Proof, not the row.** `isSessionActive` (`activeSession.isActive`)
  is a persisted claim both enforcers treat as an *input*. BLOCKING NOW
  requires **both** cascade witnesses for **this session id** — the
  same pair `ArmProvingController.LiveArmWitnesses` requires for t=0:
  `MonolithShieldService.protecting && protectingSessionId == id`
  (instance present) **and** `MonolithAccessibilityService.armedSessionId == id`
  (instance bound). The standing warrant was deliberately NOT used:
  its residency is Deck-scoped, its `readMono` is minted at read time,
  and it excludes shield-protecting by design (Salz C-4).
- **Unknown/stale are printed, not forced into the binary.** Session
  running without both witnesses → `NOT BLOCKING — … RUNNING,
  ENFORCEMENT NOT PROVEN` (with `missingProof` naming SHIELD /
  ACCESSIBILITY / both for diagnostics). Witnesses present but the
  row's shield beat stale → `NOT BLOCKING — SHIELD PROOF STALE`.
  Staleness uses the repository's own gap law
  (`SHIELD_GAP_THRESHOLD_MILLIS`, 10s) on **wall-vs-wall** stamps
  (beat vs now, start as fallback reference — the same reference
  `latchShieldGapIfStale` uses); witness mono stamps are flip
  instants and are never aged. No proof supplied at all (cold start,
  harness) → `ENFORCEMENT NOT PROVEN — NO WITNESS READ YET`, neither
  BLOCKING NOW nor a proven NOT BLOCKING (Salz C-3).
- **Label and ENDS from the session's frozen snapshot only** (Salz
  C-6): routine label from the row's `profileName` (`Routine: SLEEP`
  → `SLEEP SESSION`), never the live routine row, never the LOADOUT
  cap; ENDS = frozen `windowEndMillis` for routine sessions,
  now + `spanRemainingSeconds` (the whole-span derivation, not the
  per-block target) for timed hand sessions, and **no ENDS at all**
  for stopwatch sessions. Safe Break appends
  `· SAFE BREAK — PEEK APPS OPEN` (Salz C-8: unqualified BLOCKING NOW
  is false for the PEEK tier during a break).
- **RUNNING NOW from the session row, not window math** (Salz C-5):
  R-5 named `RoutineSchedule.isActiveNow`; that function cannot tell
  a running session from a paused/held/claimed/unmaterialised window.
  The status derivation carries `activeRoutineId` (the active
  session's frozen `routineId`); a row prints RUNNING NOW only when
  it is named by it.
- **Contract widening is named** (Salz C-1): R-5's recipe ("the
  snapshot the screen already receives") was unbuildable — Shield
  received only a Boolean. The widening is exactly one new param
  (`blockingStatus`) fed by one new VM flow; `isSessionActive` stays
  for palette/ratchet gating, its existing jobs.

## Tests written / adjusted

`ShieldBlockingStatusTest` (17 tests, plain JUnit, **not run**):
status truth table (no session, inactive, unproven with each/both
witnesses missing, witnesses for the wrong session id, LIVE with
routine label + window end, stale beat, beat exactly at the 10s
threshold still LIVE, hand-session label + span-derived end,
stopwatch prints no ENDS, Safe Break qualifier, `activeRoutineId`
carriage), `routineCapWord` truth table incl. `WON'T FIRE`,
RUNNING NOW suffix keyed to the session's routineId, and
`groupedForCatalogue` (group order, alphabetical inside groups,
each app exactly once, absent rulings contribute no group).
No existing test was adjusted; none referenced the retired slip
tags (`slip_line_*`, `slip_none_line`) — verified by grep.

## Delegated (explicit non-goal)

The **±30-minute chips** cannot express every time (20:00→20:40 is
unreachable). **Delegated to Voss's batch-10 time wheels (audit
R-8)** — not hacked here, chips untouched. Salz C-13's interim-truth
notes on the chips stand for VOGT's merge note.

## Unresolved risks / for VOGT's ruling

1. **Uncompiled.** All of the above is paper-verified only (brace
   balance, import/reference greps, full re-reads). First compile may
   still surface Compose/Kotlin issues — e.g. the grouped
   `item(key=…)`/`items(…)` calls inside `catalogueGroups.forEach`
   in the LazyColumn DSL, and the new VM flow's refresh call sites.
2. **The sentence attests the two cascade doors.** The shield
   service's own foreground-polling door additionally depends on
   usage access (`queryForegroundBlockedApp` returns null without
   it); the accessibility door — the one that enforces app tiers,
   sniper, and sites — does not, so BLOCKING NOW stays true for app
   blocking with usage revoked, but the sentence does not name its
   doors (Salz C-2's residual). If VOGT wants door-level wording,
   that is a derivation change, not a wording change.
3. **Designer Form's third word** was `CAN'T CHECK BLOCKING — TAP TO
   RETRY` for the unproven state; this pass follows the brief's
   binary-plus-honest-qualifier (`NOT BLOCKING — … ENFORCEMENT NOT
   PROVEN`, cold-start fallback excepted). One ruling should pick
   the permanent vocabulary.
4. **ARMED is a loaded word** (Salz C-9: receipt `ARMED hh:mm`, Deck
   section ARMED) and `liveCount` is blind to pause and chain-arm
   state. Kept because the brief orders ARMED; flagging the dissent.
5. **The status line is not sticky** (Salz C-7): it scrolls away from
   the catalogue it qualifies. Pinning it belongs with merge item 3's
   sticky-header work, outside this pass's four items.
6. **Semantics (R-11)** for the three new prints (status sentence,
   RUNNING NOW, WON'T FIRE) are not in this pass — TalkBack state
   descriptions remain a merge item, as audited.
7. **Behaviour change to note:** the slip's 2-tap SHUT↔PEEK flip is
   gone with the lines (Muede's 1 AM counter, answered in the audit
   by R-1's tally-jumps — the jump-words themselves are NOT built
   here; the catalogue sled is now the one flip path).
8. Tally-jump words, the PEEK contract line in the group header, and
   the glyph legend (designer §3.1) are specced but **not built** —
   outside the four items VOGT scoped to this pass.

## Draft GitHub comment (for VOGT to post)

— MONTEUR (BUILDER), for VOGT's issue #7 cell.

Builder pass DECLARED (nothing compiled, no tests run — Ren compiles
only when Sarthak says). Four in-scope fixes landed on
`agent/issue-7`:

1. **Status sentence, first line of Shield** (`shield_blocking_status`):
   `BLOCKING NOW — SLEEP SESSION · ENDS 03:30` only when both
   enforcement witnesses attest this session — the shield service's
   protecting flip for this session id AND the accessibility
   service's session-armed stamp for this session id (the arm
   cascade's own pair). Otherwise `NOT BLOCKING — NO SESSION
   RUNNING`, `… ENFORCEMENT NOT PROVEN`, or `… SHIELD PROOF STALE`
   (the repository's 10s beat law). Never derived from the session
   row's `isActive` flag alone.
2. **Standing Orders de-duplicated:** the slip's per-app lines and
   their flip machinery are retired; the tally remains as counts
   only, and the catalogue now groups by ruling (PEEK → SHUT →
   OPEN) — every app prints exactly once, on the sled row that can
   change it.
3. **ROUTINES header: `n ARMED`**, not ACTIVE (liveCount unchanged).
4. **daysMask = 0 caps print `WON'T FIRE`**, not ON; routine rows
   also gain RUNNING NOW, derived from the active session's
   routineId.

New pure derivation + truth-table tests in
`ShieldBlockingStatus.kt` / `ShieldBlockingStatusTest.kt`
(written, not run). The ±30-minute chips are untouched —
delegated to Voss's batch-10 time wheels, as instructed.
Full report: `studio/cells/issue-7/builder-monteur.md`.
