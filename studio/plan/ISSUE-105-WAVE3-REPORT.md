# Issue #105 — session-end-receipt-lived — closure report (re-cut)

Re-cut of PR #440 onto current main (e03ef59f) by the DURCH-C closure
seat, Oct 30 2026. The original branch (ec425269, built on cb17267)
could not merge blind: main had since landed BELEG #10 (W-41/W-43,
PR #451), the #10 A4 outcome allowlist, #157 (Vault receipts index),
#135 (slip doors), #124's intention freeze (PR #453) and the Nachdruck
deflection rows. This port keeps only what still holds; the
coordination note is comment 6523771573 on #105.

## Per-finding verdicts (verified against current main)

- **F1 [HIGH] — CURED in this PR.** Assembly never read
  `completedTasksSummary`; `PendingReceipt`/`ReceiptState` had no task
  field. Port: assembly splits the row's own booked summary,
  `completedTasks` threads assembly → publish → rehydrate, and the
  slip prints `DONE · <entry>` per finished task, in booking order,
  in the existing mono voice.
- **F2 [HIGH] — CURED ON MAIN by #124; assembly belt-and-braces in
  this PR.** #124's total freeze (PR #453, on main) removed the
  intention rewrite from both `completeTaskAndAdvance` and
  `promoteTaskToActive` — no session write on task finish/promotion,
  sealed or not. That is a superset of the seal guard in #106's F2
  cure; the two agree (flagged on #105, comment 6523771573). This PR
  adds the assembly side: deflection matching also accepts logs
  stamped with any title this session booked as finished, so
  deflections logged under a pre-freeze re-title are not silently
  dropped.
- **F3 [HIGH] — PARTIALLY CURED ON MAIN (A4 allowlist: a peek never
  reaches the slip, so it cannot print as a block); remainder CURED
  in this PR.** The three blocked outcomes printed identically.
  Port: the log's outcome rides on `ReceiptDeflection`
  (`outcome`/`outcomePrint`) and prints beside the attempt —
  `Stayed Focused` → `TURNED AWAY`, other blocked outcomes verbatim
  upper-cased; no outcome recorded, no word printed.
- **F4 [MEDIUM] — CURED in this PR.** `BREAK BANKED` reads the valve
  ledger (0 on every publishable receipt) while Safe Breaks book into
  `breakTakenSeconds` and were dropped. Port: `safeBreakSeconds`
  threads assembly → publish → rehydrate; the slip prints
  `BREAK TAKEN <longSpan>` (exact, never rounded) when > 0.
- **F5 [MEDIUM] — STRUCK, cured on main by other lanes.** Keep/reopen:
  #157's `claimedReceipts` index + Drawer RECEIPTS view + tap
  re-presents the slip. Actions: #135's NEXT SESSION / DONE WITH THIS
  TASK doors on the slip. The YPT-ordering footer the audit quoted is
  already replaced by BELEG's W-43 plain-words line ("THIS APP KEEPS
  ITS OWN LOG · STUDY TIME IS IN THE STUDY TIMER") — per the standing
  footer law, not new scope. The original branch's parallel
  `pastReceipts()`/Drawer view/`OPEN IN RECEIPTS` machinery is dropped
  as duplication. The export sub-part remains a design decision, as
  the original wave report priced it.

## ReceiptSheet discipline

Additive only. The W-41 HELD split line and the W-43 footer are
untouched; typography, line order and layout language unchanged —
new lines print through the existing `ReceiptMonoLine`.

## Tests

- `ReceiptClaimRepositoryTest.issue105_assemblyReadsTheRowsOwnLedgers`
  — F1/F2/F3/F4 at assembly: booked tasks in order, lived break
  seconds, peek excluded, blocked outcomes carried, a deflection
  stamped under a finished task's title kept.
- `ReceiptSheetTruthTest` — `DONE ·` lines in booking order,
  `BREAK TAKEN 4 MIN 30 SEC` exact, zero-lived prints nothing,
  `TURNED AWAY` vs `BLOCKED` print differently. Existing
  `deflections_printNamesOnly_noPeekMarkerAnywhere` still holds
  (hand-built deflections carry no outcome → no word printed).

CI verdict: read on the PR, green-per-baseline against main's latest
run before merge; tracker #245 updated in the same run as the merge.

— DURCH-C closure seat (issue-105)
