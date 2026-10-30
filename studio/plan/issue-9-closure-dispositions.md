# ISSUE #9 — CLOSURE DISPOSITIONS (closer pass, Oct 30, 2026)

*Seat: CLOSER under STURM-1, flat under Ren. Ground: branch
`agent/issue-9` — Zahn's charge file (`issue-9-zahn-critic.md`),
Ruhe's improver pass (`issue-9-improver-ruhe.md`), Hof's cell
close, and the audit table posted on issue #9 (comment
6516303288). Every item below is FIXED in code on this branch or
STRUCK / ESCALATED with evidence quoted. CI verdict is the
closure record's own leg — see the PR and #245.*

## S1 BLOCKERS

- **Z-2 — FIXED.** `WatchStore.countedWarranted` is now gated on
  UNMEASURED (returns null when any counted door is unmeasured).
  PARK headline routes through `parkGuardHeadline`, foot
  signature through `parkGuardCountSegment` (GuardLine.kt): cold
  open (EMPTY), null, and every partial set print
  `GUARD — NOT CHECKED` / `NOT CHECKED`; a count prints only off
  a fully measured counted set. Pinned in `ParkGuardPrintTest`
  (EMPTY/null/partial/full) — the fabricated `GUARD 0/4`
  headline + `GUARD ···· 0/4` footline cannot survive.
- **Z-1 — FIXED (core).** `publish()` completion now tests the
  stamp itself: every permission measurement must carry
  `sweepGen == pendingSweepGen`. The wall-clock era test is
  deleted. Gen-0 shield-loop stamps and earlier sweeps' late
  landings complete nothing. `WatchMeasurement.sweepGen` is now
  read in the production completion path — the generation-purity
  comments (ParkDrawer, WatchStore) are true as written.
  Pinned: `sweep_gen0StampsCannotCompleteAPendingSweep`.
- **Z-4 — FIXED.** `frozenGuardLineOrLive` (GuardLine.kt): a
  freeze captured at the NOT CHECKED floor falls through to the
  live derivation; a MEASURED freeze holds for the session's
  life. The ViewModel additionally RE-CAPTURES the freeze's
  Guard line on the first measured emission while a session is
  active, so a session restored after process death ends with a
  measured frozen line, not a lifelong floor. Pinned in
  `ParkGuardPrintTest` (fall-through / measured-holds /
  no-session).

## SECOND TIER

- **Z-3 — FIXED.** `TruthCar3SweepTest.una05` updated to the
  wave-2 plate: asserts the plate head stands and BOTH the
  ROUTINES line and `FIRED AS SET` do not exist. On the record:
  until this test executes in CI, the W-11 strike was
  GREP-verified only (Zahn's wording stands in the test comment).
  The four render call-sites compile against the current
  ParkDrawer signature (onCheckClick defaulted; all required
  params supplied — verified by grep this pass).
- **Z-5 — FIXED.** The FIX key prints the honest form:
  `FIX — {DOOR} · {n} MORE OPEN`. The `FIX 1 OF n` literal —
  a numerator that could never climb — is gone from code and
  comments.
- **Z-6 — FIXED.** Abandonment is recorded:
  `WatchSnapshot.lastFailedSweepGen` is set when a sweep's
  coexistence window closes uncompleted (publish path +
  `expirePendingSweepIfStale`, driven by PARK's open tick).
  PARK derives `checkFailed` from it and prints a
  `DID NOT ANSWER` board row naming the first counted door the
  sweep never stamped (`firstUnansweredForGen`). CHECKING can
  no longer brick silently. Pinned:
  `sweep_abandonedSweepRecordsItsFailedGeneration`.
- **Z-7 — FIXED.** The sweep is three-valued:
  `MonolithRepository.watchGrantState` stamps the usage door's
  UNSET as `PermState.UNSET`; `permPrintAt` prints
  `PermPrint.UNSET` (never stales, never reads MISSING); the
  PARK row prints the word `UNSET`; the Guard line names
  `USAGE ACCESS UNSET`. Pinned:
  `sweep_usageUnsetStampsUnsetAndPrintsUnset` +
  `usageUnset_guardLineNamesUnset`.
- **Z-11 — FIXED.** CHECK dispatches on confirmed release only
  (Hof T-1 pattern, as FIX); the seat knock stays on DOWN and
  sounds only from idle. A scroll starting on CHECK fires no
  sweep and clears no verdict.
- **Z-12 — FIXED.** PARK's warrant row null word is `UNREAD`.
  `NOT CHECKED` now names exactly one ignorance (the counted
  four unmeasured) everywhere it prints.
- **Z-8 — STRUCK with evidence.** The age column prints each
  row's MEASUREMENT age (ParkDrawer age column, from
  `measuredMono`); for the two BIND doors the measurement IS a
  presence re-read — the row's own GRANTED word is that same
  presence derivation, acquitted by the audit as a measurement.
  The age certifies the measurement's recency, which is true;
  no separate "grant check" claim is printed anywhere on the
  row. No code change; the charge's residual (the board does
  not distinguish the two age meanings in words) is recorded
  here as accepted, priced at a board annotation nobody has
  designed.
- **Z-9 — OVERRULE FILED HERE.** The plate stands at five lines
  (head 2 + HOLD + PEEK + the combined SITES/STUDY-HOURS line).
  Filed by: the closer, on the wave-2 lead's (Vogt) reasoning
  quoted at the plate site — SITES and STUDY HOURS are truth
  facts this plate uniquely carries on this surface; cutting
  them to hit the audit's four would trade truth for
  arithmetic. Hof's §1.11 ruling and this overrule are both on
  the record; Ren may reverse either with a word.
- **Z-10 — SUPERSEDED by events.** The branch is no longer
  "typed, uncompiled": CI executed it (run 41000674236 —
  assemble green, tests executing) and this closer pass lands
  only with a full green CI run read on the branch before
  merge. The charge's wording demand is adopted for every
  pre-CI report; it no longer describes this branch.

## RUHE R1 / R2 / R3a / R3b — ALL FIXED

- **R1:** CHECK's dress reads the COUNTED four (was PERMISSIONS).
- **R2:** NOT PROVED is a dated receipt line — Anonymous Pro
  11sp, quiet ink, `NOT PROVED — {NAME} · {HH:mm}`,
  testTag `park_check_verdict`; the board-row form is deleted.
- **R3a:** the headline adopts the Guard line's trigger — this
  is the Z-2 cure above.
- **R3b:** the key's word/dip read `checkingDisplay`, which
  includes a host-fired in-flight sweep; verdict effects stay
  keyed to the manual press; the knock narrows to idle presses.

## ESCALATED CHARGES C-6 / C-7 / C-8 — STRUCK FROM #9's SCOPE

Hof's cell close (issue #9, comment 6509502788), quoted:
"C-6, C-7, C-8 stand and are escalated to Ren — interceptor
RUNNING has no stale form, the BIND doors are presence-proved,
and the residency stamp precedes the own-package filter; all
three are derivation/warrant machinery beyond this issue's
scope, and C-8 amends the Innovator's M2 (a provoked pull needs
its own stamp field)." They are NOT cured in #9 and are not
claimed as cured; they stand as escalations to Ren on the
record. #9's closure does not depend on them — they were never
#9-scope findings.

## PANEL

Panel verdict on #9 is NOT YET on every lens (audit row 23).
This closure claims NO panel approval. The cure set above is
the audit's own blocker list, executed; panel review of the
result is a separate, still-open record.

— CLOSER (STURM-1 surge), issue #9, Oct 30, 2026.
