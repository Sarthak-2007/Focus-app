# Issue #9 (Park) — DURCH-A9 dispositions, Oct 30, 2026

Cell: DURCH-A9 under DURCH-A. All rows verified against
current main code this run, not against the audit's older
trees. Sources: issue #9 body (3 charges), the completed
surge audit checklist on #9 (24 rows + C-charges, comment
6516303288), Zahn's charge file (Z-1…Z-12), the closer
record for `agent/issue-9` @ 1762d1cf (PR #343), and
PR #422 (`welle-6/issue-9-land`).

**Vehicle of record: PR #502** (this branch). First composed
on main `7a1d65d` @ 7154f49; that head's CI was read against a
compile-red main and held. After PR #496 healed main
(reference run 41062861570: compile green, 818 tests, 67
failures = the current floor), the same delta was recomposed
as a single commit directly on the healed main tip — every
target defect re-verified verbatim on the new tree before
re-applying, and every intervening main addition in the
touched files preserved (#122 battery snapshot + board word,
#296 battery FIX note, #335 detector line, a11y semantics /
48dp sleds). PR #343 is CLOSED-unmerged (accounted by
W6-BAU-9F); PR #422 is superseded (accounting on its thread).
**#343-only cures vs this PR:** Z-3/Z-5/Z-8/Z-9/Z-12 and Ruhe
R1/R2/R3b charge surfaces that do not exist on main
(NOT-PRESENT, below); R3a's substance is cured here via the
header + gated count. The one substantive #343 cure this PR
does not carry is **Z-7's UNSET tri-state** (carrier commit
`1762d1cf` on the preserved branch `agent/issue-9`) — named
END-PHASE below, stranded with its SHA, not silently dropped.

## Cured in this branch (`agent/issue-9-durch-a9`)

- **P-6 / Z-2 header form (S1)** — `guardLineFor` returns
  `GUARD — NOT CHECKED` for null / EMPTY / any-UNMEASURED,
  never blank, never a count. VM `guardLinePrint` is non-null
  with the NOT CHECKED floor as its initial value, so a cold
  open prints the confession. Pinned: ChromeWaveUnitTest
  `guardLine_unmeasuredPrintsNotChecked`, ParkGuardTruthTest
  `coldOpenPrintsNotCheckedNeverACount`.
- **Z-1 (S1)** — `WatchStore.publish()` completion now tests
  the stamp itself: `perms.all { it.sweepGen == pendingSweepGen }`.
  The wall-clock era test is deleted as the completion test;
  the dispatch wall time survives only as the C-5 coexistence
  window (span > STALE_AFTER_MS abandons the sweep and clears
  the pending generation). Gen-0 shield/BIND stamps and earlier
  sweeps' late landings complete nothing. Pinned:
  WatchStoreRobolectricTest `sweep_gen0StampsCannotCompleteAPendingSweep`;
  the pre-existing sweep tests (real gen stamps) still pin the
  positive path.
- **Z-2 primitive (S1)** — `WatchStore.countedWarranted` is
  gated: it returns null when any of the six is UNMEASURED.
  The fabrication primitive itself is dead, not just its old
  Park call sites (which main's #109 evolution had already
  removed — on current main the Park headline/footline count
  prints are NOT-PRESENT, verified by grep: ParkDrawer prints
  no `GUARD n/…` count). Pinned: ParkGuardTruthTest (EMPTY /
  partial / full).
- **Z-4 (S1)** — reconciled with #113: `resolveGuardLinePrint`
  treats a NOT CHECKED freeze as holding nothing (falls through
  to live) and a measured freeze as the fallback only while
  live is at the floor; a measured live print always wins.
  Pinned: GuardLineTruthTest (rewritten for the non-null law).
- **Z-11, RE-CHECK form (S3)** — Park's RE-CHECK key now
  dispatches `onRecheck()` only on a confirmed release
  (`tryAwaitRelease()` true), knock stays on DOWN — the same
  law FIX already carries. (The agent/issue-9 CHECK-key form
  does not exist on main; main's verb is RE-CHECK, #109 F3.)

## Reconciliation: #51 guard switches vs #7 R-9 strike — RULED

#51 F1 rendered the GUARDS switches on Shield (sniper +
browser blocker + profiles; `ShieldMatrixScreen` §5B on main).
#7's wave-2 R-9 struck the same seven params from Shield
("PARK owns both switches"). #51's own body flags the overlap
and assigns reconciliation to whichever lands second.

**Ruling (Park cell, owner of this reconciliation): RENDER
wins; the strike is superseded.** Evidence: (1) #51 is CLOSED
and its cure is on current main (§5B, verified); (2) #7's own
current branch (`agent/issue-7`) has re-adopted the #51
section — as §3.9, moved above the catalogue by #7's R-3 —
so the striking lane itself now carries the rendering;
(3) both surfaces drive the SAME ViewModel state
(`_shortsReelsSniperEnabled` / `_browserUrlBlockerEnabled`),
and Park's own sniper sled (§5) reads the same state — two
controls, one fact, no divergent truth. Remaining difference
is placement only (main §5B deep vs #7 §3.9 above the
catalogue): endorsed in favour of #7's §3.9 move, to land
with #7's lane (DURCH-A7, active this wave) — this branch
does not touch ShieldMatrixScreen, to avoid colliding with
that hot lane.

## Not cured here — named, with reasons

- **PR #422 overlap** — #422 carries the header NOT CHECKED
  cure in an older composition and REGRESSES #113 (it deletes
  `resolveGuardLinePrint`) while its publish() still badged
  `lastCompletedSweepGen` onto an era-admitted set (Z-1 core
  stands on that branch, verified by direct read). This branch
  supersedes #422's #9 delta; #422 should be closed-unmerged
  as accounted/superseded once this PR merges (its owning
  lane, welle-6, is notified on the PR thread). No cure is
  lost: every #422 item is either here in stronger form or
  already on main (FIX release-commit, #56 composition).
- **C-6 / C-7 / C-8** — STAND on main (auditor rows 4–6,
  re-verified in substance: interceptor re-stamp in publish,
  BIND presence stamps, residency stamp order). Escalated to
  Ren by Hof's cell close before this cell existed;
  derivation/warrant machinery beyond #9's build scope.
  END-PHASE / escalation, not silently dropped.
- **Z-6 (CHECK failure face)** — main-form: RE-CHECK has no
  CHECKING state to brick (auditor: NOT-PRESENT on main); a
  thrown query stamps nothing and the line ages to STALE,
  which re-offers RE-CHECK. The agent/issue-9 DID NOT ANSWER
  face belongs to that branch's CHECK key, which main never
  adopted. Ruled NOT-PRESENT on the main line.
- **Z-7 (USAGE UNSET → MISSING)** — STANDS on main
  (`watchGrantQuery` collapses UNSET to a Boolean). Agent9's
  tri-state cure touches PermState/PermPrint plumbing across
  the board + repository; named END-PHASE for this cell —
  it is a truth defect but not a build blocker, and porting
  it blind onto the six-item board risks the #58/#94 prints.
- **Z-3 / Z-5 / Z-8 / Z-9 / Z-12, Ruhe R1/R2/R3a/R3b** —
  NOT-PRESENT or NOT APPLIED on main (auditor rows 9, 11, 14,
  15, 18–22): they charge surfaces that exist only on
  `agent/issue-9` (wave-2 headline/footline, staircase label,
  age column, NOT PROVED row, warrant row). Main's plate
  stands as #109 left it. No main-side cure exists to make.
- **Z-10** — process charge; answered by this run's method
  (CI verdict read on this PR before any merge claim).
- **Panel leg** — the #9 panel verdict on record is NOT YET
  on every lens; this cell claims no panel approval.
- **Body charge 1 (CHECK verb)** — main form: Park open
  re-sweeps (MainActivity parkOpen) and RE-CHECK (#109 F3)
  is the on-demand verb; the always-present CHECK key +
  CHECKING state exist only on `agent/issue-9`. Adopting
  that key wholesale is a design-surface change — named
  END-PHASE (redesign sequence, Sarthak Oct 30 17:34:
  build first, broad/redesign last).

— DURCH-A9, cell captain, issue #9.
